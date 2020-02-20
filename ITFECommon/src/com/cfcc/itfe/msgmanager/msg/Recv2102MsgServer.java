package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * （报文编号涉及：2102批量扣税回执）
 * 
 * @author zhouchuan
 * 
 */
public class Recv2102MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv2102MsgServer.class);

	/**
	 * 报文信息处理
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		try {
			HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
			HashMap headMap = (HashMap) cfxMap.get("HEAD");
			HashMap msgMap = (HashMap) cfxMap.get("MSG");
			// 解析报文头 headMap
//			String sorgcode = (String) headMap.get("SRC"); // 发起机构代码
			String sdescode = (String) headMap.get("DES");// 接收节点代码
			String MsgNo = (String) headMap.get("MsgNo");// 报文编号
			String MsgID = (String) headMap.get("MsgID");// 报文标识号
//			String MsgRef = (String) headMap.get("MsgRef");// 报文参考号
//			String WorkDate = (String) headMap.get("WorkDate");// 工作日期
			String sbookorgcode=null;
			String recvseqno = StampFacade.getStampSendSeq("JS"); // 取接收日志流水
			String sendseqno = StampFacade.getStampSendSeq("FS"); // 取发送日志流水
			/**
			 * 取得回执头信息
			 */
			HashMap batchHeadMap = (HashMap) msgMap.get("BatchHead2102");
//			String paybkcode = (String) batchHeadMap.get("PayBkCode"); // 付款行行号
			String entrustdate = (String) batchHeadMap.get("EntrustDate");// 委托日期
			String packno = (String) batchHeadMap.get("PackNo");// 包流水号
			String oritaxorgcode = (String) batchHeadMap.get("OriTaxOrgCode"); // 原征收机关代码
			String orientrustdate = (String) batchHeadMap.get("OriEntrustDate"); // 原委托日期
			String oripackno = (String) batchHeadMap.get("OriPackNo"); // 原包流水号
			int allnum = Integer.parseInt(batchHeadMap.get("AllNum").toString()
					.trim());// 总笔数
			String AllAmt = (String) batchHeadMap.get("AllAmt");// 总金额
//			String succnum = (String) batchHeadMap.get("SuccNum"); // 成功笔数
//			String succamt = (String) batchHeadMap.get("SuccAmt");// 成功金额
//			String result = (String) batchHeadMap.get("Result");//处理结果
			String addword = (String) batchHeadMap.get("AddWord");//附言
			BigDecimal allamt = new BigDecimal(AllAmt);
			TsConvertfinorgDto finorgdto = new TsConvertfinorgDto();//根据财政代码取核算主体代码
			finorgdto.setSfinorgcode(oritaxorgcode);
			List<TsConvertfinorgDto> orglist = CommonFacade.getODB().findRsByDto(finorgdto);
			if(orglist!=null&&orglist.size()>0)
				sbookorgcode = orglist.get(0).getSorgcode();
			/**
			 * 记接收日志
			 *///<Object> batchReturnList = (List<Object>) msgMap.get("BatchReturn2102");
			MsgLogFacade.writeRcvLog(recvseqno, sendseqno, sbookorgcode,
					entrustdate, MsgNo, sdescode, (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"),allnum, allamt, packno, "", "", "",
					"", MsgID,DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER), null, null);
//			TsOperationmodelDto _dto = new TsOperationmodelDto();
//			String _smodelid = recvseqno.substring(0, 10);
//			int _ino = Integer.valueOf(recvseqno.substring(10, 20));
//			String path = (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH");
//			_dto.setSmodelid(_smodelid);
//			_dto.setIno(_ino);
//			_dto.setSoperationtypecode(MsgNo);
//			_dto.setSmodelsavepath(path);
//			DatabaseFacade.getDb().create(_dto);
			//更新发送日志
			TvSendlogDto tvsendlog = null;
			List tvsendloglist = null;
			tvsendlog = new TvSendlogDto();
			tvsendlog.setSbillorg(oritaxorgcode);
			tvsendlog.setSdate(orientrustdate);
			tvsendlog.setSpackno(oripackno);
			tvsendloglist = CommonFacade.getODB().findRsByDto(tvsendlog);
		    if (null!=tvsendloglist && tvsendloglist.size() > 0) {
		    	tvsendlog = (TvSendlogDto) tvsendloglist.get(0);
				tvsendlog.setSretcode(DealCodeConstants.DEALCODE_TIPS_SUCCESS);
				tvsendlog.setSdemo(addword);
				tvsendlog.setSproctime(new Timestamp(new java.util.Date().getTime()));
				DatabaseFacade.getDb().update(tvsendlog);
				if (StateConstant.MSG_SENDER_FLAG_2.equals(tvsendlog.getSifsend())) {
					// 写发送日志
					MsgLogFacade.writeSendLog(sendseqno, recvseqno,sbookorgcode, oritaxorgcode, orientrustdate,
							(String) headMap.get("MsgNo"), (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
							new BigDecimal(0), null, null, null, oritaxorgcode, null,(String) headMap.get("MsgID"),
							DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
							(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_SEND);
					// 获得原报文，重新发送
					//取得原发起报文的MQMSGID
//					TvMqmessageDto dto = MsgLogFacade.queryMQMSGID("1102", MsgRef);
//					if(dto==null){
						eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");
//					}else{
//						eventContext.getMessage().setCorrelationId(dto.getSmqmsgid());
//					}
					Object msg = eventContext.getMessage().getProperty("MSG_INFO");
					eventContext.getMessage().setPayload(msg);
				} else {
					// 不做进一步处理
					eventContext.setStopFurtherProcessing(true);
					return;
				}
			}
		} catch (SequenceException e) {
			logger.error("取接收流水号失败!", e);
			throw new ITFEBizException("取接收流水号失败!", e);
		} catch (JAFDatabaseException e) {
			logger.error("记接收报文日志表失败!", e);
			throw new ITFEBizException("记录接收报文日志表失败!", e);
		} catch (ValidateException e) {
			logger.error("记发送报文日志失败!", e);
			throw new ITFEBizException("记发送报文日志失败!", e);
		}
	}
}	


package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TfFundAppropriationDto;
import com.cfcc.itfe.persistence.dto.TfReconciliationDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 对账报文(北京) (2002)
 * @author Administrator
 *
 */
public class Recv2002MsgServer extends AbstractMsgManagerServer {
	private static Log logger = LogFactory.getLog(Recv2002MsgServer.class);

	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap)eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap)cfxMap.get("HEAD");
		HashMap msgMap = (HashMap)cfxMap.get("MSG");
		
		/**
		 * 解析报文头 headMap
		 */
		String msgNo = (String)headMap.get("MsgNo");
		String msgid = (String)headMap.get("MsgID");
		String msgref = (String)headMap.get("MsgRef");
		String sendnode = (String) headMap.get("SRC");
		
		HashMap batchheadMap = (HashMap)msgMap.get("BatchHead2002");
		String chkDate = (String)batchheadMap.get("ChkDate");
		String treCode = (String)batchheadMap.get("TreCode");
		int packNo = Integer.parseInt((String)batchheadMap.get("PackNo"));
		int recvPackNum = Integer.parseInt((String)batchheadMap.get("RecvPackNum"));
		BigDecimal packAmt = MtoCodeTrans.transformBigDecimal(batchheadMap.get("PackAmt"));
		int sendPackNum = Integer.parseInt((String)batchheadMap.get("SendPackNum"));
		BigDecimal sendPackAmt = MtoCodeTrans.transformBigDecimal(batchheadMap.get("SendPackAmt"));
		
		TfReconciliationDto dto = new TfReconciliationDto();
		dto.setSmsgid(msgid);                     //报文标识号
		dto.setSmsgno(msgNo);                     //报文编号
		dto.setSmsgref(msgref);                   //报文参考号
		dto.setSchkdate(chkDate);                 //对账日期
		dto.setStrecode(treCode);                 //国库主体代码
		dto.setIpackno(packNo);                   //对账次数
		dto.setIrecvpacknum(recvPackNum);         //本日接收包总个数
		dto.setNpackamt(packAmt);                 //本日接收包总金额
		dto.setIsendpacknum(sendPackNum);         //本日发起包总个数
		dto.setNsendpackamt(sendPackAmt);         //本日发起包总金额
		
		List <IDto> list = new ArrayList<IDto>();
		List BillCheck2002 = new ArrayList();
		if(msgMap.get("BillCheck2002") != null){
			BillCheck2002 = (List)msgMap.get("BillCheck2002");
		}
		Timestamp ts = null;
		try {
			ts = TSystemFacade.getDBSystemTime();
		} catch (JAFDatabaseException e) {
			logger.error("获取系统时间错误",e);
			throw new ITFEBizException("获取系统时间错误");
		}
		if(BillCheck2002!=null && BillCheck2002.size()>0){
			for(int i=0;i<BillCheck2002.size();i++){
				TfReconciliationDto tmpDto = (TfReconciliationDto)dto.clone();
				HashMap tmpmap = (HashMap) BillCheck2002.get(i);
				String payoutVouType = (String)tmpmap.get("PayoutVouType");
				String spackNo = (String)tmpmap.get("PackNo");
				int curPackVouNum = Integer.parseInt((String)tmpmap.get("CurPackVouNum"));
				BigDecimal curPackVouAmt = MtoCodeTrans.transformBigDecimal(tmpmap.get("CurPackVouAmt"));
				
				tmpDto.setSpayoutvoutype(payoutVouType);  // 凭证类型
				tmpDto.setSpackno(spackNo);               // 包流水号
				tmpDto.setIcurpackvounum(curPackVouNum);  // 本包凭证总笔数
				tmpDto.setNcurpackvouamt(curPackVouAmt);  // 本包凭证金额
				tmpDto.setTssysupdate(ts);
				
				list.add(tmpDto);
			}
			if(list.size()>0){
				try {
					DatabaseFacade.getDb().create(list.toArray(new IDto[list.size()]));
				} catch (JAFDatabaseException e) {
					logger.error("保存对账信息出错",e);
					throw new ITFEBizException("保存对账信息出错");
				}
			}
		}else{
			//代理行未收到任何资金拨付业务
			TfFundAppropriationDto fundDto = new TfFundAppropriationDto();
			fundDto.setSentrustdate(TimeFacade.getCurrentStringTime());
			List<TfFundAppropriationDto> fundList = null;
			try {
				fundList = CommonFacade.getODB().findRsByDto(fundDto);
			} catch (Exception e1) {
				logger.error("查找资金拨付信息出错",e1);
				throw new ITFEBizException("查找资金拨付信息出错");
			}
			if(fundList != null && fundList.size() > 0){
				TfReconciliationDto tmpDto = (TfReconciliationDto)dto.clone();
				tmpDto.setSpayoutvoutype(fundList.get(0).getSpayoutvoutype());  // 凭证类型
				tmpDto.setSpackno("0");               // 包流水号(随便写的不存在的流水号)
				tmpDto.setIcurpackvounum(fundList.get(0).getIallnum());  // 本包凭证总笔数
				tmpDto.setNcurpackvouamt(fundList.get(0).getNallamt());  // 本包凭证金额
				list.add(tmpDto);
			}
			
			//当天没有业务发生，所以业务类型随便填写的(数据库中为非空字段)
			dto.setSpayoutvoutype("6"); //
			dto.setSpackno("");
			dto.setIcurpackvounum(0);
			dto.setNcurpackvouamt(new BigDecimal(0.00));
			try {
				DatabaseFacade.getDb().create(dto);
			} catch (JAFDatabaseException e) {
				logger.error("保存对账信息出错",e);
				throw new ITFEBizException("保存对账信息出错");
			}
		}
		
		String _srecvno = null;
		String _ssendno = null;
		try {
			_srecvno = StampFacade.getStampSendSeq("JS");
			_ssendno = StampFacade.getStampSendSeq("FS");
		} catch (SequenceException e1) {
			logger.error("取接收或者发送流水号错误!", e1);
			throw new ITFEBizException("取接收或者发送流水号错误!", e1);
		}
		
		// 记接收日志
		MsgLogFacade.writeRcvLog(_srecvno, _ssendno, (String) headMap
				.get("SRC"), TimeFacade.getCurrentStringTime(),
				(String) headMap.get("MsgNo"), (String) headMap.get("SRC"),
				(String) eventContext.getMessage().getProperty(
						"XML_MSG_FILE_PATH"), 0, null, null, null, null, null,
				null, (String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null, (String) headMap
						.get("MsgNo"));
		
		
		try {
			TvVoucherinfoDto vDto = new TvVoucherinfoDto();
			vDto.setSpaybankcode(sendnode);
			vDto.setStrecode(treCode);
			MuleClient client = new MuleClient();

			Map map = new HashMap();
			MuleMessage message = new DefaultMuleMessage(map);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY,"1002");
			message.setProperty(MessagePropertyKeys.MSG_STATE, DealCodeConstants.DEALCODE_ITFE_RECEIVER);
			message.setProperty(MessagePropertyKeys.MSG_ADDWORD, "已收妥");
			message.setProperty(MessagePropertyKeys.MSG_TAG_KEY, msgNo);
			message.setProperty(MessagePropertyKeys.MSG_DESC, sendnode);
			message.setProperty(MessagePropertyKeys.MSG_REF, msgid);
			message.setProperty(MessagePropertyKeys.MSG_HEAD_DTO, dto);
			message.setProperty(MessagePropertyKeys.MSG_DTO, vDto);
			message.setProperty(MessagePropertyKeys.MSG_DTO_LIST, list);
			
			message.setPayload(map);

			message = client.send("vm://ManagerMsgWithCommBank", message);

		} catch (MuleException e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);
		}
	}
	
}

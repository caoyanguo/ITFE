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
import com.cfcc.itfe.persistence.dto.TfRefundNoticeDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 退款通知报文(2003)北京
 * @author Administrator
 *
 */
public class Recv2003TbsMsgServer extends AbstractMsgManagerServer {
	private static Log logger = LogFactory.getLog(Recv2003TbsMsgServer.class);

	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap)eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap)cfxMap.get("HEAD");
		HashMap msgMap = (HashMap)cfxMap.get("MSG");
		String filepath = (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH");
		/**
		 * 解析报文头 headMap
		 */
		String msgNo = (String)headMap.get("MsgNo");
		String msgid = (String)headMap.get("MsgID");
		String msgref = (String)headMap.get("MsgRef");
		String sendnode = (String) headMap.get("SRC");
		
		HashMap batchheadMap = (HashMap)msgMap.get("BatchHead2003");
		String entrustDate = (String)batchheadMap.get("EntrustDate");
		String packNo = (String)batchheadMap.get("PackNo");
		String treCode = (String)batchheadMap.get("TreCode");
		String changeNo=null;
		if(batchheadMap.get("ChangeNo") != null){
			changeNo = (String)batchheadMap.get("ChangeNo");
		}
		int allNum = Integer.parseInt((String)batchheadMap.get("AllNum"));
		BigDecimal allAmt = MtoCodeTrans.transformBigDecimal(batchheadMap.get("AllAmt"));
		String payoutVouType = (String)batchheadMap.get("PayoutVouType");
		
		String returnWorld = "接收成功";
		String returnStatus = "90000";
		
		TfRefundNoticeDto dto = new TfRefundNoticeDto();
		dto.setSmsgid(msgid);                     //报文标识号
		dto.setSmsgno(msgNo);                     //报文编号
		dto.setSmsgref(msgref);                   //报文参考号
		dto.setSentrustdate(entrustDate);         //委托日期
		dto.setSpackno(packNo);                   //包流水号
		dto.setStrecode(treCode);                 //国库主体代码
		dto.setSchangeno(changeNo);               //同城交换号
		dto.setIallnum(allNum);                   //总笔数
		dto.setNallamt(allAmt);                   //总金额
		dto.setSpayoutvoutype(payoutVouType);     //凭证类型
		
		List <IDto> list = new ArrayList<IDto>();
		List <IDto> voucherlist = new ArrayList<IDto>();
		List<Object> billSend2003 = null;
		if(msgMap.get("BillSend2003") != null){
			billSend2003 = (List<Object>)msgMap.get("BillSend2003");
		}
		Timestamp ts = null;
		try {
			ts = TSystemFacade.getDBSystemTime();
		} catch (JAFDatabaseException e) {
			returnStatus = "90006";
			returnWorld = e.getMessage();
			logger.error("获取系统时间错误",e);
			throw new ITFEBizException("获取系统时间错误");
		} catch (Exception e) {
			returnStatus = "90006";
			returnWorld = e.getMessage();
			logger.error("获取系统时间错误",e);
			throw new ITFEBizException("获取系统时间错误");
		}
		
		TsConvertfinorgDto convertfinorgDto = new TsConvertfinorgDto();
		convertfinorgDto.setStrecode(treCode);
		List<TsConvertfinorgDto> convertfinorglist = null;
		try {
			convertfinorglist = CommonFacade.getODB().findRsByDto(convertfinorgDto);
			if(convertfinorglist==null || convertfinorglist.size()<1){
				logger.error("财政标志参数未维护！");
				throw new ITFEBizException("财政标志参数未维护！");
			}
			convertfinorgDto = convertfinorglist.get(0);
		} catch (JAFDatabaseException e2) {
			returnStatus = "90006";
			returnWorld = e2.getMessage();
			logger.error("获取机构代码错误",e2);
			throw new ITFEBizException("获取机构代码错误");
		} catch (ValidateException e2) {
			returnStatus = "90006";
			returnWorld = e2.getMessage();
			logger.error("获取机构代码错误",e2);
			throw new ITFEBizException("获取机构代码错误");
		} catch (Exception e2) {
			returnStatus = "90006";
			returnWorld = e2.getMessage();
			logger.error("获取机构代码错误",e2);
			throw new ITFEBizException("获取机构代码错误");
		}
		
		if(billSend2003!=null && billSend2003.size()>0){
			for(int i=0;i<billSend2003.size();i++){
				TfRefundNoticeDto tmpdto = (TfRefundNoticeDto)dto.clone();
				HashMap tmpmap = (HashMap)billSend2003.get(i);
				String traNo = (String)tmpmap.get("TraNo");
				String vouNo = null;
				if(tmpmap.get("VouNo") != null){
					vouNo = (String)tmpmap.get("VouNo");
				}
				String vouDate = null;
				if(tmpmap.get("VouDate") != null){
					vouDate = (String)tmpmap.get("VouDate");
				}
				String payerAcct = (String)tmpmap.get("PayerAcct");
				String payerName = (String)tmpmap.get("PayerName");
				String payerAddr = null;
				if(tmpmap.get("PayerAddr") != null){
					payerAddr = (String)tmpmap.get("PayerAddr");
				}
				BigDecimal amt = MtoCodeTrans.transformBigDecimal(tmpmap.get("Amt"));
				String payerBankNo = (String)tmpmap.get("PayerBankNo");
				String payeeBankNo = (String)tmpmap.get("PayeeBankNo");
				String payeeAcct = (String)tmpmap.get("PayeeAcct");
				String payeeName = (String)tmpmap.get("PayeeName");
				String payReason = null;
				if(tmpmap.get("PayReason") != null){
					payReason = (String)tmpmap.get("PayReason");
				}
				String addWord = null;
				if(tmpmap.get("AddWord") != null){
					addWord = (String)tmpmap.get("AddWord");
				}
				String ofYear = (String)tmpmap.get("OfYear");
				/**
				 * 业务表赋值
				 */
				String sid = VoucherUtil.getGrantSequence();
				tmpdto.setSid(sid);
				tmpdto.setStrano(traNo);            //交易流水号
				tmpdto.setSvouno(vouNo);            //原凭证编号
				tmpdto.setSvoudate(vouDate);          //原凭证日期
				tmpdto.setSpayeracct(payerAcct);    //付款人账号
				tmpdto.setSpayername(payerName);    //付款人名称
				tmpdto.setSpayeraddr(payerAddr);    //付款人地址
				tmpdto.setNamt(amt);                //退款金额
				tmpdto.setSpayerbankno(payerBankNo);//付款行行号
				tmpdto.setSpayeebankno(payeeBankNo);//收款行行号
				tmpdto.setSpayeeacct(payeeAcct);    //收款人账号
				tmpdto.setSpayeename(payeeName);    //收款人名称
				tmpdto.setSpayreason(payReason);    //退款原因
				tmpdto.setSaddword(addWord);        //附言
				tmpdto.setSofyear(ofYear);          //所属年度
				tmpdto.setTssysupdate(ts);          //系统时间
				
				/**
				 * 索引表赋值
				 */
				TvVoucherinfoDto voucherinfoDto = new TvVoucherinfoDto();
				voucherinfoDto.setSdealno(sid);
				voucherinfoDto.setSorgcode(convertfinorgDto.getSorgcode());
				voucherinfoDto.setStrecode(treCode);
				voucherinfoDto.setSfilename(filepath);
				voucherinfoDto.setSadmdivcode(convertfinorgDto.getSadmdivcode());
				voucherinfoDto.setSstyear(entrustDate.substring(0, 4));
				voucherinfoDto.setSvtcode("2003");
				voucherinfoDto.setSvoucherno(sid);
				voucherinfoDto.setSvoucherflag("0");
				voucherinfoDto.setScreatdate(TimeFacade.getCurrentStringTime());
				voucherinfoDto.setNmoney(amt);
				voucherinfoDto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
				voucherinfoDto.setSpaybankcode(payerBankNo);
				voucherinfoDto.setSext1("");
				
				list.add(tmpdto);
				voucherlist.add(voucherinfoDto);
			}
			if(list.size()>0){
				try {
					DatabaseFacade.getDb().create(list.toArray(new IDto[list.size()]));
					DatabaseFacade.getDb().create(voucherlist.toArray(new IDto[voucherlist.size()]));
				} catch (Exception e) {
					returnStatus = "90006";
					returnWorld = e.getMessage();
					logger.error("保存退款通知报文出错",e);
					throw new ITFEBizException("保存退款通知报文出错");
				} 
			}
		}
		
		String _srecvno = null;
		String _ssendno = null;
		try {
			_srecvno = StampFacade.getStampSendSeq("JS");
			_ssendno = StampFacade.getStampSendSeq("FS");
		} catch (Exception e1) {
			returnStatus = "90006";
			returnWorld = e1.getMessage();
			logger.error("取接收或者发送流水号错误!", e1);
			throw new ITFEBizException("取接收或者发送流水号错误!", e1);
		}
		if(packNo.length()>8){
			packNo = packNo.substring(packNo.length()-8);
		}
		// 记接收日志
		MsgLogFacade.writeRcvLog(_srecvno, _ssendno, convertfinorgDto.getSorgcode(), TimeFacade.getCurrentStringTime(),
				(String) headMap.get("MsgNo"), (String) headMap.get("SRC"),
				(String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), allNum, allAmt, packNo, treCode,
				null, null, null, msgid, DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null, (String) headMap.get("MsgNo"));
		
		
		try {
			TvVoucherinfoDto vDto = new TvVoucherinfoDto();
			vDto.setSpaybankcode(sendnode);
			vDto.setStrecode(treCode);
			MuleClient client = new MuleClient();

			Map map = new HashMap();
			MuleMessage message = new DefaultMuleMessage(map);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY,"TBS_3001_OUT");
			message.setProperty(MessagePropertyKeys.MSG_STATE, returnStatus);
			message.setProperty(MessagePropertyKeys.MSG_ADDWORD, returnWorld);
			message.setProperty(MessagePropertyKeys.MSG_TAG_KEY, msgNo);
			message.setProperty(MessagePropertyKeys.MSG_DESC, sendnode);
			message.setProperty(MessagePropertyKeys.MSG_REF, msgid);
			//接收发送主键值
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, _srecvno);	//接收
			message.setProperty(MessagePropertyKeys.MSG_BILL_CODE, _ssendno);	//发送
			message.setProperty(MessagePropertyKeys.MSG_DTO, vDto);
			message.setProperty(MessagePropertyKeys.MSG_PACK_NO, packNo);
			
			message.setPayload(map);

			message = client.send("vm://ManagerMsgWithCommBank", message);

		} catch (MuleException e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);
		}
	}
	
	
}

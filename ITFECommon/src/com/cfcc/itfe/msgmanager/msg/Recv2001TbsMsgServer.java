package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TfRefundNoticeDto;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * 退款通知报文(2001)北京
 * @author Administrator
 *
 */
public class Recv2001TbsMsgServer extends AbstractMsgManagerServer {
	private static Log logger = LogFactory.getLog(Recv2001TbsMsgServer.class);

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
		
		HashMap batchheadMap = (HashMap)msgMap.get("BatchHead2001");
		String entrustDate = (String)batchheadMap.get("EntrustDate");
		String packNo = (String)batchheadMap.get("PackNo");
		String treCode = (String)batchheadMap.get("TreCode");
		String changeNo=null;
		if(batchheadMap.get("ChangeNo") != null){
			changeNo = (String)batchheadMap.get("ChangeNo");
		}
		int allNum = Integer.parseInt((String)batchheadMap.get("AllNum"));
		BigDecimal allAmt = MtoCodeTrans.transformBigDecimal(batchheadMap.get("allAmt"));
		String payoutVouType = (String)batchheadMap.get("PayoutVouType");
		
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
		List billSend2001 = new ArrayList();
		if(msgMap.get("BillSend2001") != null){
			billSend2001 = (List)msgMap.get("BillSend2001");
		}
		Timestamp ts = null;
		try {
			ts = TSystemFacade.getDBSystemTime();
		} catch (JAFDatabaseException e) {
			logger.error("获取系统时间错误",e);
			throw new ITFEBizException("获取系统时间错误");
		}
		if(billSend2001!=null && billSend2001.size()>0){
			for(int i=0;i<billSend2001.size();i++){
				TfRefundNoticeDto tmpdto = (TfRefundNoticeDto)dto.clone();
				HashMap tmpmap = (HashMap)billSend2001.get(i);
				String traNo = (String)tmpmap.get("TraNo");
				String vouNo = null;
				if(tmpmap.get("VouNo") != null){
					vouNo = (String)tmpmap.get("VouNo");
				}
				String vouDate = null;
				if(tmpmap.get("VouDate") != null){
					vouNo = (String)tmpmap.get("VouDate");
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
				
				tmpdto.setStrano(traNo);            //交易流水号
				tmpdto.setSvouno(vouNo);            //原凭证编号
				tmpdto.setSvoudate(vouNo);          //原凭证日期
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
				
				list.add(tmpdto);
			}
			if(list.size()>0){
				try {
					DatabaseFacade.getDb().create(list.toArray(new IDto[list.size()]));
				} catch (JAFDatabaseException e) {
					logger.error("保存退款通知报文出错",e);
					throw new ITFEBizException("保存退款通知报文出错");
				}
			}
		}
	}
	
	
}

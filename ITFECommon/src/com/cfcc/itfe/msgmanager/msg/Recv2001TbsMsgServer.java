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
 * �˿�֪ͨ����(2001)����
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
		 * ��������ͷ headMap
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
		dto.setSmsgid(msgid);                     //���ı�ʶ��
		dto.setSmsgno(msgNo);                     //���ı��
		dto.setSmsgref(msgref);                   //���Ĳο���
		dto.setSentrustdate(entrustDate);         //ί������
		dto.setSpackno(packNo);                   //����ˮ��
		dto.setStrecode(treCode);                 //�����������
		dto.setSchangeno(changeNo);               //ͬ�ǽ�����
		dto.setIallnum(allNum);                   //�ܱ���
		dto.setNallamt(allAmt);                   //�ܽ��
		dto.setSpayoutvoutype(payoutVouType);     //ƾ֤����
		
		List <IDto> list = new ArrayList<IDto>();
		List billSend2001 = new ArrayList();
		if(msgMap.get("BillSend2001") != null){
			billSend2001 = (List)msgMap.get("BillSend2001");
		}
		Timestamp ts = null;
		try {
			ts = TSystemFacade.getDBSystemTime();
		} catch (JAFDatabaseException e) {
			logger.error("��ȡϵͳʱ�����",e);
			throw new ITFEBizException("��ȡϵͳʱ�����");
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
				
				tmpdto.setStrano(traNo);            //������ˮ��
				tmpdto.setSvouno(vouNo);            //ԭƾ֤���
				tmpdto.setSvoudate(vouNo);          //ԭƾ֤����
				tmpdto.setSpayeracct(payerAcct);    //�������˺�
				tmpdto.setSpayername(payerName);    //����������
				tmpdto.setSpayeraddr(payerAddr);    //�����˵�ַ
				tmpdto.setNamt(amt);                //�˿���
				tmpdto.setSpayerbankno(payerBankNo);//�������к�
				tmpdto.setSpayeebankno(payeeBankNo);//�տ����к�
				tmpdto.setSpayeeacct(payeeAcct);    //�տ����˺�
				tmpdto.setSpayeename(payeeName);    //�տ�������
				tmpdto.setSpayreason(payReason);    //�˿�ԭ��
				tmpdto.setSaddword(addWord);        //����
				tmpdto.setSofyear(ofYear);          //�������
				tmpdto.setTssysupdate(ts);          //ϵͳʱ��
				
				list.add(tmpdto);
			}
			if(list.size()>0){
				try {
					DatabaseFacade.getDb().create(list.toArray(new IDto[list.size()]));
				} catch (JAFDatabaseException e) {
					logger.error("�����˿�֪ͨ���ĳ���",e);
					throw new ITFEBizException("�����˿�֪ͨ���ĳ���");
				}
			}
		}
	}
	
	
}

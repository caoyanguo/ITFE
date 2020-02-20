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
 * ���˱���(����) (2002)
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
		 * ��������ͷ headMap
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
		dto.setSmsgid(msgid);                     //���ı�ʶ��
		dto.setSmsgno(msgNo);                     //���ı��
		dto.setSmsgref(msgref);                   //���Ĳο���
		dto.setSchkdate(chkDate);                 //��������
		dto.setStrecode(treCode);                 //�����������
		dto.setIpackno(packNo);                   //���˴���
		dto.setIrecvpacknum(recvPackNum);         //���ս��հ��ܸ���
		dto.setNpackamt(packAmt);                 //���ս��հ��ܽ��
		dto.setIsendpacknum(sendPackNum);         //���շ�����ܸ���
		dto.setNsendpackamt(sendPackAmt);         //���շ�����ܽ��
		
		List <IDto> list = new ArrayList<IDto>();
		List BillCheck2002 = new ArrayList();
		if(msgMap.get("BillCheck2002") != null){
			BillCheck2002 = (List)msgMap.get("BillCheck2002");
		}
		Timestamp ts = null;
		try {
			ts = TSystemFacade.getDBSystemTime();
		} catch (JAFDatabaseException e) {
			logger.error("��ȡϵͳʱ�����",e);
			throw new ITFEBizException("��ȡϵͳʱ�����");
		}
		if(BillCheck2002!=null && BillCheck2002.size()>0){
			for(int i=0;i<BillCheck2002.size();i++){
				TfReconciliationDto tmpDto = (TfReconciliationDto)dto.clone();
				HashMap tmpmap = (HashMap) BillCheck2002.get(i);
				String payoutVouType = (String)tmpmap.get("PayoutVouType");
				String spackNo = (String)tmpmap.get("PackNo");
				int curPackVouNum = Integer.parseInt((String)tmpmap.get("CurPackVouNum"));
				BigDecimal curPackVouAmt = MtoCodeTrans.transformBigDecimal(tmpmap.get("CurPackVouAmt"));
				
				tmpDto.setSpayoutvoutype(payoutVouType);  // ƾ֤����
				tmpDto.setSpackno(spackNo);               // ����ˮ��
				tmpDto.setIcurpackvounum(curPackVouNum);  // ����ƾ֤�ܱ���
				tmpDto.setNcurpackvouamt(curPackVouAmt);  // ����ƾ֤���
				tmpDto.setTssysupdate(ts);
				
				list.add(tmpDto);
			}
			if(list.size()>0){
				try {
					DatabaseFacade.getDb().create(list.toArray(new IDto[list.size()]));
				} catch (JAFDatabaseException e) {
					logger.error("���������Ϣ����",e);
					throw new ITFEBizException("���������Ϣ����");
				}
			}
		}else{
			//������δ�յ��κ��ʽ𲦸�ҵ��
			TfFundAppropriationDto fundDto = new TfFundAppropriationDto();
			fundDto.setSentrustdate(TimeFacade.getCurrentStringTime());
			List<TfFundAppropriationDto> fundList = null;
			try {
				fundList = CommonFacade.getODB().findRsByDto(fundDto);
			} catch (Exception e1) {
				logger.error("�����ʽ𲦸���Ϣ����",e1);
				throw new ITFEBizException("�����ʽ𲦸���Ϣ����");
			}
			if(fundList != null && fundList.size() > 0){
				TfReconciliationDto tmpDto = (TfReconciliationDto)dto.clone();
				tmpDto.setSpayoutvoutype(fundList.get(0).getSpayoutvoutype());  // ƾ֤����
				tmpDto.setSpackno("0");               // ����ˮ��(���д�Ĳ����ڵ���ˮ��)
				tmpDto.setIcurpackvounum(fundList.get(0).getIallnum());  // ����ƾ֤�ܱ���
				tmpDto.setNcurpackvouamt(fundList.get(0).getNallamt());  // ����ƾ֤���
				list.add(tmpDto);
			}
			
			//����û��ҵ����������ҵ�����������д��(���ݿ���Ϊ�ǿ��ֶ�)
			dto.setSpayoutvoutype("6"); //
			dto.setSpackno("");
			dto.setIcurpackvounum(0);
			dto.setNcurpackvouamt(new BigDecimal(0.00));
			try {
				DatabaseFacade.getDb().create(dto);
			} catch (JAFDatabaseException e) {
				logger.error("���������Ϣ����",e);
				throw new ITFEBizException("���������Ϣ����");
			}
		}
		
		String _srecvno = null;
		String _ssendno = null;
		try {
			_srecvno = StampFacade.getStampSendSeq("JS");
			_ssendno = StampFacade.getStampSendSeq("FS");
		} catch (SequenceException e1) {
			logger.error("ȡ���ջ��߷�����ˮ�Ŵ���!", e1);
			throw new ITFEBizException("ȡ���ջ��߷�����ˮ�Ŵ���!", e1);
		}
		
		// �ǽ�����־
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
			message.setProperty(MessagePropertyKeys.MSG_ADDWORD, "������");
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

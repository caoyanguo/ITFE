/**
 * ����:�������뱨�Ľӿ�(9000)�����з���
 */
package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
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
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

/**
 * @author wangyunbin
 * 
 */
public class Recv9000MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(RecvPayOutMsgServer.class);

	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		// ����ͷ��ϢCFX->HEAD
		String sorgcode = (String) headMap.get("SRC"); // �����������
		String sdescode = (String) headMap.get("DES");// ���սڵ����
		String msgNo = (String) headMap.get("MsgNo");// ���ı��
		String msgID = (String) headMap.get("MsgID");// ���ı�ʶ��
		String msgRef = (String) headMap.get("MsgRef");// ���Ĳο���
		String sdate = (String) headMap.get("WorkDate");// ��������

		/**
		 * ȡ�û�ִͷ��Ϣ
		 */
		HashMap batchHeadMap = (HashMap) msgMap.get("BatchHead9000");
		String AppDate = (String) batchHeadMap.get("AppDate"); // ��������
		int count = 0 ;
		//BigDecimal allamt = new BigDecimal("0.00");
		String sendno = null;
		String recvorg = sdescode;
		//String sdemo ="���ı��:"+MsgConstant.MSG_NO_9000+",ԭ����ˮ:"+soripackno.trim()+",ԭ�����������"+sbillorg.trim()+",ԭί������:"+sorientrustDate.trim();
		/**
		 * ȡ�û�ִ������Ϣ
		 */
		List<Object> BillCheck9000 = (List<Object>) msgMap.get("BillCheck9000");
		if (null == BillCheck9000 || BillCheck9000.size() == 0) {
			return;
		} else {
			count = BillCheck9000.size();
			try {
				//������־��ˮ
				String _srecvno = StampFacade.getStampSendSeq("JS");
				String filepath = (String) eventContext.getMessage().getProperty(
						"XML_MSG_FILE_PATH");
				String stamp = TimeFacade.getCurrentStringTime();
				String ifsend = (String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER);
				// �ǽ�����־
				MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg, AppDate, msgNo,
						sorgcode, filepath, BillCheck9000.size(), new BigDecimal("0.00"), "", "", null,
						"", null, msgID,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, "�������9000�ط�" );
				
				for (int i = 0; i < count; i++) {
					HashMap tmpmap = (HashMap) BillCheck9000.get(i);
					String PayoutVouType = (String) tmpmap.get("PayoutVouType"); //ƾ֤����1-ʵ�� 2- �˿� 3���л���4����
					String TreCode = (String) tmpmap.get("TreCode"); // �����������
					String EntrustDate = (String) tmpmap.get("EntrustDate"); // ί������
					String PackNo = (String) tmpmap.get("PackNo"); //����ˮ��
					
					//����ls_TreCode�ж��ǲ���Ϊ�����
					HashMap<String, TsTreasuryDto> mapTreCode = SrvCacheFacade.cacheTreasuryInfo("");
					if(mapTreCode.containsKey(TreCode)){
						TsTreasuryDto treDto =  mapTreCode.get(TreCode);
						String ls_treattrib = treDto.getStreattrib();
						String ls_PbcBankCode = treDto.getSofcountrytrecode();//�����ع�����룬����Ϊ���������б����
						//�ֹ����÷��񣬷��ͱ�����Ϣ
						
						Map map = new HashMap();
						MuleMessage message = new DefaultMuleMessage(map);
						message.setProperty("payoutVouType",PayoutVouType);
						message.setProperty("treCode",TreCode);
						message.setProperty("entrustDate", EntrustDate);
						message.setProperty("packNo", PackNo);
						message.setProperty("bankCode",ls_PbcBankCode);
						message.setProperty(MessagePropertyKeys.MSG_ORGCODE,treDto.getSorgcode());
						message.setProperty(MessagePropertyKeys.MSG_NO_KEY,MsgConstant.MSG_NO_8000);
						MuleClient client = new MuleClient();
						message = client.send("SendToSameCityMsg", message);
					}
				}
				
			} catch (SequenceException e) {
				String error = "����9000���Ĵ���ʧ�ܣ�";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} catch (MuleException e) {
				logger.debug(e);
			} catch (JAFDatabaseException e) {
				logger.debug(e);
			}
		}
		eventContext.setStopFurtherProcessing(true);
		return;
	}

}


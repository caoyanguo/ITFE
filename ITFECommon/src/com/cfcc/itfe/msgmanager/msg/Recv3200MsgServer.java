package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

/**
 * ��������TIPS��ĳ�����������ղ�ת����������ر�����Ϣ�����ı���漰��5101��5102��5103��
 * ��Ҫ���ܣ��˶Դ�ITFEϵͳ���͸�TIPS������Ϣ
 * @author zhouchuan
 * 
 */
@SuppressWarnings("unchecked")
public class Recv3200MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3200MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		String msgRef  = (String)headMap.get("MsgRef");

		/**
		 * ������Ϣͷ
		 */
		HashMap batchheadMap = (HashMap) msgMap.get("BatchHead3200");
		String chkDate = (String) batchheadMap.get("ChkDate"); // �˶�����
		String packNo = (String) batchheadMap.get("PackNo"); // ����ˮ��
		String billOrg = (String) batchheadMap.get("BillOrg");// �˶Ի�������
//		String orgType = (String) batchheadMap.get("OrgType");// ��������
//		int allRcvNum = Integer.valueOf((String) (batchheadMap.get("AllRcvNum")));// ���հ���
//		int childPackNum = Integer.valueOf((String) batchheadMap.get("ChildPackNum")); // �Ӱ�����
//		String curPackNo = (String) batchheadMap.get("CurPackNo");// �������
		int curPackNum = Integer.valueOf((String) batchheadMap.get("CurPackNum")); // ��������
		BigDecimal curPackAmt = MtoCodeTrans.transformBigDecimal(batchheadMap.get("CurPackAmt"));// �������

		List returnList = (List) msgMap.get("CompDeduct3200");
		
		if (null == returnList || returnList.size() == 0) {
			
		} else {
				int count = returnList.size();
				for (int i = 0; i < count; i++) {
					HashMap returnmap = (HashMap) returnList.get(i);

					String orimsgno = (String) returnmap.get("OriMsgNo"); // ԭ���ı��
					String orisendorgcode = (String) returnmap.get("OriSendOrgCode"); // ԭ�����������
					String orientrustdate = (String) returnmap.get("OriEntrustDate");// ԭί������
					String oripackno = (String) returnmap.get("OriPackNo");// ԭ����ˮ��
//					int allnum = Integer.valueOf((String) (returnmap.get("AllNum")));// �ܱ���
//					BigDecimal allAmt = MtoCodeTrans.transformBigDecimal(returnmap.get("AllAmt"));// �ܽ��

//					String biztype = PublicSearchFacade.getBizTypeByMsgNo(orimsgno); // ҵ������
//					String sdemo = allnum + "_" + allAmt; // ��ע������+��� 
					//���°���Ӧ��ϵ��
					MsgRecvFacade.updateMsgHeadByMsgNo(orimsgno, orisendorgcode, oripackno, orientrustdate, DealCodeConstants.DEALCODE_ITFE_CHECK);
					//���·�����־��
					MsgRecvFacade.updateSendLogByPackageNo(orimsgno,orisendorgcode,oripackno,orientrustdate,DealCodeConstants.DEALCODE_ITFE_CHECK);
				}
				
				/**
				 * ���������־
				 */
				try {
					HashMap<String, TsConvertfinorgDto> map = SrvCacheFacade.cacheFincInfoByFinc(null);
					String sbookorgcode;
					if (null != map.get(billOrg)) {
						sbookorgcode = map.get(billOrg).getSorgcode();
					} else {
						sbookorgcode = ITFECommonConstant.SRC_NODE;
					}
					TvRecvlogDto recvlogdto = new TvRecvlogDto();
					recvlogdto.setSrecvno(StampFacade.getStampSendSeq("JS")); // ��ˮ��
					recvlogdto.setSpackno(packNo); // ԭ����ˮ��
					recvlogdto.setSrecvorgcode(sbookorgcode); // ���ͻ�������Ĭ��
					recvlogdto.setSsendorgcode((String) headMap.get("SRC ")); // ���ջ�������Ĭ��
					recvlogdto.setSdate(chkDate);// ��������-ί������
					recvlogdto.setSoperationtypecode(MsgConstant.MSG_NO_3200);// ҵ��ƾ֤����
					recvlogdto.setSrecvtime(new Timestamp(new java.util.Date().getTime()));// ����ʱ��
					recvlogdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_RECEIVER); // ������
					recvlogdto.setSretcodedesc("���ն��˰�"); // ����˵��- ����
					recvlogdto.setIcount(curPackNum);
					recvlogdto.setNmoney(curPackAmt);
					recvlogdto.setStitle((String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"));
					recvlogdto.setSsenddate(TimeFacade.getCurrentStringTime());
					recvlogdto.setSseq((String) headMap.get("MsgID"));
					recvlogdto.setSifsend((String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER));
					recvlogdto.setSdemo(MsgConstant.LOG_ADDWORD_RECV_TIPS);
					DatabaseFacade.getDb().create(recvlogdto);
					// ����ԭ������־
					TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(msgRef,MsgConstant.MSG_NO_9117);
					if (null!=senddto){
						//����ԭ������־��ˮ��
						MsgRecvFacade.updateMsgSendLogByMsgId(senddto, DealCodeConstants.DEALCODE_ITFE_RECEIVER, recvlogdto.getSrecvno(), "");
					}
					
				} catch (SequenceException e) {
					logger.error("ȡ������ˮ��ʧ��!", e);
					throw new ITFEBizException("ȡ������ˮ��ʧ��", e);
				} catch (JAFDatabaseException e) {
					logger.error("����������־�������ݿ��쳣!", e);
					throw new ITFEBizException("����������־�������ݿ��쳣!", e);
				}
		}
		eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");
		// ���ԭ���ģ����·���
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
		return;
	}

}

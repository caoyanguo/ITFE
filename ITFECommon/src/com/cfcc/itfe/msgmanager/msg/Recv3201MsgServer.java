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
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TvTips3201Dto;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;

/**
 * ��������TIPS��ĳ��������ת����������ر�����Ϣ�����ı���漰��3131��3133��3134�� ��Ҫ���ܣ��˶Դ�TIPSϵͳ���͸�ITFEϵͳ������Ϣ
 * 
 * @author zhouchuan
 * 
 */
@SuppressWarnings("unchecked")
public class Recv3201MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3201MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");

		/**
		 * ������Ϣͷ
		 */
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap batchheadMap = (HashMap) msgMap.get("BatchHead3201");
		String chkDate = (String) batchheadMap.get("ChkDate"); // �˶�����
		String packNo = (String) batchheadMap.get("PackNo"); // ����ˮ��
		String billOrg = (String) batchheadMap.get("BillOrg");// �˶Ի�������
		String orgType = (String) batchheadMap.get("OrgType");// ��������
//		int allRcvNum = Integer.valueOf((String) (batchheadMap
//				.get("AllSendNum")));// ���Ͱ���
		int childPackNum = Integer.valueOf((String) batchheadMap
				.get("ChildPackNum")); // �Ӱ�����
//		String curPackNo = (String) batchheadMap.get("CurPackNo");// �������
//		String curPackNum = (String) batchheadMap.get("CurPackNum"); // ��������
		BigDecimal curPackAmt = MtoCodeTrans.transformBigDecimal(batchheadMap
				.get("CurPackAmt"));// �������
		List<TvRecvlogDto> finddtolist;
		HashMap<String, TsConvertfinorgDto> map;
		String sbookorgcode;
		try {
			map = SrvCacheFacade.cacheFincInfoByFinc(null);
			if (null != map.get(billOrg)) {
				sbookorgcode = map.get(billOrg).getSorgcode();
			} else {
				sbookorgcode = ITFECommonConstant.SRC_NODE;
			}
			TvRecvlogDto logDto = new TvRecvlogDto();
			logDto.setSrecvorgcode(sbookorgcode);
			logDto.setSdate(chkDate);
			logDto.setSpackno(packNo);
			logDto.setSoperationtypecode(MsgConstant.MSG_NO_3201);
			finddtolist = CommonFacade.getODB().findRsByDto(logDto);
			if (finddtolist.size() == 0) {
				List returnList = (List) msgMap.get("CompDeduct3201");
				if (null == returnList || returnList.size() == 0) {
				} else {
					int count = returnList.size();
					for (int i = 0; i < count; i++) {
						HashMap returnmap = (HashMap) returnList.get(i);

						String orimsgno = (String) returnmap.get("OriMsgNo"); // ԭ���ı��
						String orisendorgcode = (String) returnmap
								.get("OriSendOrgCode"); // ԭ�����������
						String orientrustdate = (String) returnmap
								.get("OriEntrustDate");// ԭί������
						String oripackno = (String) returnmap.get("OriPackNo");// ԭ����ˮ��
						int allnum = Integer.valueOf((String) (returnmap
								.get("AllNum")));// �ܱ���
						BigDecimal allAmt = MtoCodeTrans
								.transformBigDecimal(returnmap.get("AllAmt"));// �ܽ��
//
//						String biztype = PublicSearchFacade
//								.getBizTypeByMsgNo(orimsgno); // ҵ������
//						String sdemo = allnum + "_" + allAmt; // ��ע������+���

						// ����3201��Ϣ
						TvTips3201Dto tv3201dto = new TvTips3201Dto();
						// ������������
						tv3201dto.setSorgcode(sbookorgcode);
						// �˶�����
						tv3201dto.setSchkdate(chkDate);
						// ����ˮ��
						tv3201dto.setSpackno(packNo);
						// �˶Ի�������
						tv3201dto.setSbillorg(orgType);
						// ��������
						tv3201dto.setSorgtype(orgType);
						// ԭ���ı��
						tv3201dto.setSorimsgno(orimsgno);
						// ԭ�����������
						tv3201dto.setSorisendorgcode(orisendorgcode);
						// ԭί������
						tv3201dto.setSorientrustdate(orientrustdate);
						// ԭ����ˮ��
						tv3201dto.setSoripackno(oripackno);
						// �ܱ���
						tv3201dto.setIallnum(allnum);
						// �ܽ��
						tv3201dto.setFallamt(allAmt);
						// �Ƿ�˶�
						tv3201dto.setScheck(StateConstant.CANCEL_FLAG_NOCHECK);
						// ��������
						DatabaseFacade.getDb().create(tv3201dto);

						// ���°���Ӧ��ϵ��
						MsgRecvFacade.updateMsgHeadByMsgNofor3201(
								orisendorgcode, oripackno, orientrustdate,
								DealCodeConstants.DEALCODE_ITFE_CHECK);
						// ���·�����־��
						MsgRecvFacade.updateRecvLogByPackageNo(orimsgno,
								orisendorgcode, oripackno, orientrustdate,
								DealCodeConstants.DEALCODE_ITFE_CHECK);
					}
				}
			}
			/**
			 * ���������־
			 */

			TvRecvlogDto recvlogdto = new TvRecvlogDto();
			recvlogdto.setSrecvno(StampFacade.getStampSendSeq("JS")); // ��ˮ��
			recvlogdto.setSpackno(packNo); // ԭ����ˮ��
			recvlogdto.setSrecvorgcode(sbookorgcode); // ���ͻ�������Ĭ��
			recvlogdto.setSsendorgcode((String) headMap.get("SRC")); // ���ջ�������Ĭ��
			recvlogdto.setSdate(chkDate);// ��������-ί������
			recvlogdto.setSoperationtypecode(MsgConstant.MSG_NO_3201);// ҵ��ƾ֤����
			recvlogdto.setSrecvtime(new Timestamp(new java.util.Date()
					.getTime()));// ����ʱ��
			recvlogdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_RECEIVER); // ������
			recvlogdto.setSretcodedesc(""); // ����˵��- ����
			recvlogdto.setIcount(childPackNum);
			recvlogdto.setNmoney(curPackAmt);
			recvlogdto.setStitle((String) eventContext.getMessage()
					.getProperty("XML_MSG_FILE_PATH"));
			recvlogdto.setSsenddate(TimeFacade.getCurrentStringTime());
			recvlogdto.setSseq((String) headMap.get("MsgID"));
			recvlogdto.setSifsend((String) eventContext.getMessage()
					.getProperty(MessagePropertyKeys.MSG_SENDER));
			recvlogdto.setSdemo(MsgConstant.LOG_ADDWORD_RECV_TIPS
					+ MsgConstant.MSG_NO_3201);
			DatabaseFacade.getDb().create(recvlogdto);

		} catch (Exception e) {
			logger.error("����3201���ĳ����쳣!", e);
			throw new ITFEBizException("����3201���ĳ����쳣!", e);
		}
		eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");
		// ���ԭ���ģ����·���
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
		return;
	}
}

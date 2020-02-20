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
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPbcpayMainDto;
import com.cfcc.itfe.persistence.dto.TvPbcpaySubDto;
//import com.cfcc.itfe.persistence.dto.TvGrantpayplanMainDto;
//import com.cfcc.itfe.persistence.dto.TvPbcgrantpayMainDto;
//import com.cfcc.itfe.persistence.dto.TvPbcgrantpaySubDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * ���ա������������������а���֧���˿�֪ͨ(3146) ��Ҫ���ܣ����ܡ�����������3146����
 * 
 * @author zhangxh
 * 
 */
public class Recv3146MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3146MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		try {

			/*
			 * ��1�� ��������ͷ��Ϣ����¼������־
			 */
			// ���ĵĴ���ʽ 1 �ֹ� 2 MQ
			String bankInput = (String) eventContext.getMessage().getProperty(
					"BANK_INPUT");
			HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
			HashMap headMap = (HashMap) cfxMap.get("HEAD");
			HashMap msgMap = (HashMap) cfxMap.get("MSG");

			// ����ͷ��ϢCFX->HEAD
			String sorgcode = (String) headMap.get("SRC"); // �����������
			String sdescode = (String) headMap.get("DES");// ���սڵ����
			String MsgNo = (String) headMap.get("MsgNo");// ���ı��
			String MsgID = (String) headMap.get("MsgID");// ���ı�ʶ��
			String MsgRef = (String) headMap.get("MsgRef");// ���Ĳο���
			String WorkDate = (String) headMap.get("WorkDate");// ��������

			// ����ʵʱҵ��ͷ CFX->MSG->BatchHead3146
			HashMap batchheadMap = (HashMap) msgMap.get("BatchHead3146");

			String trreCode = (String) batchheadMap.get("TreCode"); // �����������
			String billOrg = (String) batchheadMap.get("BillOrg"); // ��Ʊ��λ
			String entrustDate = (String) batchheadMap.get("EntrustDate"); // ί������
			String packNo = (String) batchheadMap.get("PackNo"); // ����ˮ��
			int allNum = Integer.valueOf((String) batchheadMap.get("AllNum")); // �ܱ���
			BigDecimal allAmt = MtoCodeTrans.transformBigDecimal(batchheadMap
					.get("AllAmt")); // �ܽ��
			String PayMode = (String) batchheadMap.get("PayMode"); // ֧����ʽ
			String sbookorgcode = sdescode;
			/*
			 * ��2�� ������������Ϣ����¼��ȡ���ϸ����
			 */
			// �����ϢCFX->MSG ->Bill3146
			List Bill3146s = (List) msgMap.get("Bill3146");
			for (int i = 0; i < Bill3146s.size(); i++) {
				HashMap Bill3146 = (HashMap) Bill3146s.get(i);
				String VouNo = (String) Bill3146.get("VouNo"); // ƾ֤���
				String VouDate = (String) Bill3146.get("VouDate"); // ƾ֤����
				String OriTraNo = (String) Bill3146.get("OriTraNo"); // ԭ������ˮ��
				String OriEntrustDate = (String) Bill3146.get("OriEntrustDate"); // ԭί������
				String OriVouNo = (String) Bill3146.get("OriVouNo"); // ԭ֧��ƾ֤���
				String OriVouDate = (String) Bill3146.get("OriVouDate"); // ԭ֧��ƾ֤����
				String PayerAcct = (String) Bill3146.get("PayerAcct"); // �������˺�
				String PayerName = (String) Bill3146.get("PayerName"); // ����������
				String PayerAddr = (String) Bill3146.get("PayerAddr"); // �����˵�ַ
				BigDecimal Amt = MtoCodeTrans.transformBigDecimal(Bill3146.get("Amt")); // ���
				String PayeeBankNo = (String) Bill3146.get("PayeeBankNo"); // �տ����к�
				String PayeeOpBkNo = (String) Bill3146.get("PayeeOpBkNo"); // �տ��˿������к�
				String PayeeAcct = (String) Bill3146.get("PayeeAcct"); // �տ����˺�
				String PayeeName = (String) Bill3146.get("PayeeName"); // �տ�������
				String AddWord = (String) Bill3146.get("AddWord"); // ����
				String OfYear = (String) Bill3146.get("OfYear"); // �������
				String BudgetType = (String) Bill3146.get("BudgetType"); // Ԥ������
				String TrimSign = (String) Bill3146.get("TrimSign"); // �����ڱ�־
				String BckReason = (String) Bill3146.get("BckReason"); // �˻�ԭ��
				int StatInfNum = Integer.valueOf((String) Bill3146
						.get("StatInfNum")); // ͳ����Ϣ����

				String seq = StampFacade.getBizSeq("RBTH"); // ҵ����ˮ��
				TsTreasuryDto findto = SrvCacheFacade.cacheTreasuryInfo(null).get(trreCode);
				if (null!=findto) {
					sbookorgcode =findto.getSorgcode();
				}

				// ��֯DTO׼����������Ϣ--------------------------
				// ����ͨ��֧����ʽ�������Ȩ֧��������Ϣ���˻���Ϣ dto
				TvPbcpayMainDto tvpbcgrantpaymaindto = new TvPbcpayMainDto();
				tvpbcgrantpaymaindto.setIvousrlno((new Long(OriTraNo))*(-10)); // ҵ����ˮ��
				tvpbcgrantpaymaindto.setStrano(OriTraNo);// ������ˮ��
				tvpbcgrantpaymaindto.setSorgcode(sbookorgcode); // ����������� 
				tvpbcgrantpaymaindto.setStrecode(trreCode);// �����������
				tvpbcgrantpaymaindto.setSbillorg(billOrg);// ��Ʊ��λ
				tvpbcgrantpaymaindto.setSentrustdate(entrustDate);// ί������
				tvpbcgrantpaymaindto.setSpackno(packNo);// ����ˮ��
				tvpbcgrantpaymaindto.setSpayoutvoutypeno("0");// ֧��ƾ֤����
				tvpbcgrantpaymaindto.setSpaymode(PayMode);// ֧����ʽ
				tvpbcgrantpaymaindto.setSvouno(VouNo);// ƾ֤���
				tvpbcgrantpaymaindto.setDvoucher(VouDate);// ƾ֤����
				tvpbcgrantpaymaindto.setSpayeracct(PayerAcct);// �������˺�
				tvpbcgrantpaymaindto.setSpayername(PayerName);// ����������
				tvpbcgrantpaymaindto.setSpayeraddr(PayerAddr);// �����˵�ַ
				tvpbcgrantpaymaindto.setSpayeeacct(PayeeAcct);// �տ����˺�
				tvpbcgrantpaymaindto.setSpayeename(PayeeName);// �տ�������
				tvpbcgrantpaymaindto.setSpayeeaddr("");// �տ��˵�ַ
				tvpbcgrantpaymaindto.setSrcvbnkno(PayeeBankNo);// �������к�
				tvpbcgrantpaymaindto.setSpayeeopnbnkno(PayeeOpBkNo);// �տ�����к�
				tvpbcgrantpaymaindto.setSaddword(AddWord);// ����
				tvpbcgrantpaymaindto.setCbdgkind(BudgetType);// Ԥ������
				tvpbcgrantpaymaindto.setIofyear(Integer.parseInt(OfYear));// �������
				tvpbcgrantpaymaindto.setSbdgadmtype(" ");// Ԥ���������
				tvpbcgrantpaymaindto.setFamt(Amt);// ���
				tvpbcgrantpaymaindto.setStrastate("");// ������
				tvpbcgrantpaymaindto.setDacct("");// TCBS��������
				tvpbcgrantpaymaindto.setCtrimflag(TrimSign);// �����ڱ�־
				tvpbcgrantpaymaindto.setIdetailnio(StatInfNum);// ��ϸ��¼��
				tvpbcgrantpaymaindto.setIchgnum(1);// �޸Ĵ���
				tvpbcgrantpaymaindto.setSinputerid("sysadmin");// ¼��Ա����
				tvpbcgrantpaymaindto.setTssysupdate(new Timestamp(
						new java.util.Date().getTime()));// ϵͳ����ʱ��

				tvpbcgrantpaymaindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_BACK); // ����״̬
				tvpbcgrantpaymaindto.setSbackflag(StateConstant.COMMON_YES);// �˻ر�־
				tvpbcgrantpaymaindto.setSdescription(BckReason);// ������˵��--�˻�ԭ��
				tvpbcgrantpaymaindto.setDorientrustdate(OriEntrustDate);// ԭί������
				tvpbcgrantpaymaindto.setSoritrano(OriTraNo);// ԭ������ˮ��
				tvpbcgrantpaymaindto.setSorivouno(OriVouNo);// ԭƾ֤���
				tvpbcgrantpaymaindto.setDorivoudate(OriVouDate);// ԭƾ֤����

				//--20101025 ���������˿��־---------------
				upBackFlag(tvpbcgrantpaymaindto);
				
				// ��������
				DatabaseFacade.getDb().create(tvpbcgrantpaymaindto);

				// ������Ϣ CFX->MSG->Bill3146->Detail3146
				List Detail3146s = (List) Bill3146.get("Detail3146");
				for (int j = 0; j < Detail3146s.size(); j++) {
					HashMap Detail3146 = (HashMap) Detail3146s.get(j);
					String SeqNo = (String) Detail3146.get("SeqNo"); // ���
					String BdgOrgCode = (String) Detail3146.get("BdgOrgCode"); // Ԥ�㵥λ����
					String FuncBdgSbtCode = (String) Detail3146
							.get("FuncBdgSbtCode"); // �������Ŀ����
					String EcnomicSubjectCode = (String) Detail3146
							.get("EcnomicSubjectCode"); // �������Ŀ����
					BigDecimal Amt1 = MtoCodeTrans
							.transformBigDecimal(Detail3146.get("Amt")); // ������
					String AcctProp = (String) Detail3146.get("AcctProp"); // �˻�����

					// ��֯DTO׼��������ϸ��Ϣ--------------------------
					// ����ͨ��֧����ʽ�������Ȩ֧������Ϣdto
					TvPbcpaySubDto tvpbcgrantpaysubdto = new TvPbcpaySubDto();
					tvpbcgrantpaysubdto.setIvousrlno((new Long(OriTraNo))*(-10)); // ҵ����ˮ��
					tvpbcgrantpaysubdto.setIseqno(Integer.parseInt(SeqNo)); // �������
					tvpbcgrantpaysubdto.setSbdgorgcode(BdgOrgCode);// Ԥ�㵥λ����
					tvpbcgrantpaysubdto.setSfuncsbtcode(FuncBdgSbtCode); // �������Ŀ����
					tvpbcgrantpaysubdto.setSecosbtcode(EcnomicSubjectCode); // �������Ŀ����
					tvpbcgrantpaysubdto.setFamt(Amt1); // ���
					tvpbcgrantpaysubdto.setCacctprop(AcctProp); // �˻�����
					// ��������
					DatabaseFacade.getDb().create(tvpbcgrantpaysubdto);
				}
			}

			/*
			 * ��3�� ���淢����־�����ͱ���
			 */
			// �Ƿ�����־
			String _srecvno = StampFacade.getStampSendSeq("JS");
			String filepath = (String) eventContext.getMessage().getProperty(
			"XML_MSG_FILE_PATH");
	        String stamp = TimeFacade.getCurrentStringTime();
	       String ifsend = (String) eventContext.getMessage().getProperty(
			MessagePropertyKeys.MSG_SENDER);
			MsgLogFacade.writeRcvLog(_srecvno, _srecvno, sbookorgcode, entrustDate, MsgNo,
					sorgcode, filepath, allNum, allAmt, packNo, trreCode, null,
					billOrg, null, MsgID,
					DealCodeConstants.DEALCODE_ITFE_RECEIVER, "���а���֧���˻�", null,
					null, MsgConstant.ITFE_SEND, "���а���֧���˻�" );

		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("���ݿ����", e);
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("ȷ��sequence��Ϣ����", e);
		}
	}
	
	/**
	 * �����˿��־
	 * @param dto
	 * @throws ITFEBizException
	 */
	private void upBackFlag(TvPbcpayMainDto dto) throws ITFEBizException{
		
		SQLExecutor updateExce = null;
		try {
			updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String updateSql = "update "
				+ dto.tableName()
				+ " set s_BackFlag = ? ,s_Description=? "
				+ " where  s_VouNo = ? and d_Voucher=? ";
			updateExce.addParam(dto.getSbackflag());
			updateExce.addParam(dto.getSdescription());
			updateExce.addParam(dto.getSorivouno());
			updateExce.addParam(dto.getDorivoudate());
			
			if(null !=dto.getDorientrustdate() && !"".endsWith(dto.getDorientrustdate())){
				updateSql+=" and s_EntrustDate = ? ";
				updateExce.addParam(dto.getDorientrustdate());
			}
			if(null !=dto.getSoritrano() && !"".endsWith(dto.getSoritrano())){
				updateSql+=" and S_TRANO = ? ";
				updateExce.addParam(dto.getSoritrano());
			}
			updateExce.runQuery(updateSql);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("����3146�˿����ݳ���", e);
		} finally {
			if (null != updateExce) {
				updateExce.closeConnection();
			}
		}
	}
}

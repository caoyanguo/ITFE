package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgSubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.BizTableUtil;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * ���ա����������Ͳ�����ʵ���ʽ��˿�֪ͨ(3145) ��Ҫ���ܣ����ܡ�����������3145����
 * 
 * @author zhangxh
 * 
 */
public class Recv3145MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3145MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		SQLExecutor updateExce = null;
		try {
			
			 // ��1�� ��������ͷ��Ϣ����¼������־
			String filename = (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"); // �ļ����� 
			HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
			HashMap headMap = (HashMap) cfxMap.get("HEAD");
			HashMap msgMap = (HashMap) cfxMap.get("MSG");

			// ����ͷ��ϢCFX->HEAD
			String sorgcode = (String) headMap.get("SRC"); // �����������
			String sdescode = (String) headMap.get("DES");// ���սڵ����
			String MsgNo = (String) headMap.get("MsgNo");// ���ı��
			String MsgID = (String) headMap.get("MsgID");// ���ı�ʶ��
//			String MsgRef = (String) headMap.get("MsgRef");// ���Ĳο���
//			String WorkDate = (String) headMap.get("WorkDate");// ��������

			// ����ʵʱҵ��ͷ CFX->MSG->BatchHead3145
			HashMap batchheadMap = (HashMap) msgMap.get("BatchHead3145");
			String sbookorgcode = "000000000000";
			String billOrg = (String) batchheadMap.get("BillOrg"); // ��Ʊ��λ
			String trreCode = (String) batchheadMap.get("TreCode"); // �����������
			String entrustDate = (String) batchheadMap.get("EntrustDate"); // ί������
			String packNo = (String) batchheadMap.get("PackNo"); // ����ˮ��
			int allNum = Integer.valueOf((String) batchheadMap.get("AllNum")); // �ܱ���
			BigDecimal allAmt = MtoCodeTrans.transformBigDecimal(batchheadMap.get("AllAmt")); // �ܽ��
			String payoutVouType = (String) batchheadMap.get("PayoutVouType"); // ֧��ƾ֤����
			
			String orgcode = BizTableUtil.getOrgcodeByTrecode(trreCode);
			Map<String,TdCorpDto> rpmap = SrvCacheFacade.cacheTdCorpInfo(orgcode); //���˴��뻺��
			HashMap<String, TsBudgetsubjectDto> budgetsubjectmap = SrvCacheFacade.cacheTsBdgsbtInfo(orgcode); //��Ŀ���뻺��

			
			// ��2�� ������������Ϣ����¼��ϸ����
			 
			// �����Ϣ CFX->MSG->Bill3145
			List Bill3145s = (List) msgMap.get("Bill3145");
			for (int i = 0; i < Bill3145s.size(); i++) {

				HashMap Bill3145 = (HashMap) Bill3145s.get(i);
				String VouNo = (String) Bill3145.get("VouNo"); // ƾ֤���
				String VouDate = (String) Bill3145.get("VouDate"); // ƾ֤����
				String oriTraNo = (String) Bill3145.get("OriTraNo");//ԭ������ˮ��
				String oriEntrustDate = (String) Bill3145.get("OriEntrustDate");//ԭί������
				String OriVouNo = (String) Bill3145.get("OriVouNo"); // ԭ֧��ƾ֤���
				String OriVouDate = (String) Bill3145.get("OriVouDate"); // ԭ֧��ƾ֤����
				String PayerAcct = (String) Bill3145.get("PayerAcct"); // �������˺�
				String PayerName = (String) Bill3145.get("PayerName"); // ����������
				String PayerAddr = (String) Bill3145.get("PayerAddr"); // �����˵�ַ
				BigDecimal Amt = MtoCodeTrans.transformBigDecimal(Bill3145.get("Amt")); // �ϼƽ��
				String PayeeOpBkNo = (String) Bill3145.get("PayeeOpBkNo"); // �տ��˿������к�
				String PayeeBankNo = (String) Bill3145.get("PayeeBankNo"); // �տ����к�
				String PayeeAcct = (String) Bill3145.get("PayeeAcct"); // �տ����˺�
				String PayeeName = (String) Bill3145.get("PayeeName"); // �տ�������
				String AddWord = (String) Bill3145.get("AddWord"); // ����
				String BdgOrgCode = (String) Bill3145.get("BdgOrgCode"); // Ԥ�㵥λ����
//				String BudgetOrgName = (String) Bill3145.get("BudgetOrgName"); // Ԥ�㵥λ����
				String OfYear = (String) Bill3145.get("OfYear"); // �������
				String BudgetType = (String) Bill3145.get("BudgetType"); // Ԥ������
				String TrimSign = (String) Bill3145.get("TrimSign"); // �����ڱ�־
				String BckReason = (String) Bill3145.get("BckReason"); // �˻�ԭ��
				//int StatInfNum = Integer.valueOf((String) Bill3145.get("StatInfNum")); // ͳ����Ϣ����
				
				String seq = StampFacade.getBizSeq("SBTH"); // ҵ����ˮ��

				// ��֯DTO׼����������Ϣ--------------------------
				// ʵ���ʽ��ޣ��У�ֽƾ֤����������Ϣ dto
				TvPayoutbackmsgMainDto tvpayoutmsgmaindto = new TvPayoutbackmsgMainDto();
				tvpayoutmsgmaindto.setSbizno(seq); // ҵ����ˮ��
				sbookorgcode = BizTableUtil.getOrgcodeByTrecode(trreCode);
				tvpayoutmsgmaindto.setSorgcode(sbookorgcode); // ����������� 
				tvpayoutmsgmaindto.setScommitdate(entrustDate);// ί������
				tvpayoutmsgmaindto.setSaccdate(VouDate);// TCBS��������
				tvpayoutmsgmaindto.setSfilename(filename); // �����ļ���
				tvpayoutmsgmaindto.setStrecode(trreCode);// �����������
				tvpayoutmsgmaindto.setSpackageno(packNo);// ����ˮ��
				tvpayoutmsgmaindto.setSpayunit(billOrg);// ��Ʊ��λ
				tvpayoutmsgmaindto.setSvouno(VouNo);// ƾ֤���
				tvpayoutmsgmaindto.setSvoudate(VouDate);// ƾ֤����
				tvpayoutmsgmaindto.setSoritrano(oriTraNo); // ԭ������ˮ��
				tvpayoutmsgmaindto.setSorientrustdate(oriEntrustDate);
				tvpayoutmsgmaindto.setSorivouno(OriVouNo);// ԭƾ֤���
				tvpayoutmsgmaindto.setSorivoudate(OriVouDate);// ԭƾ֤����

				tvpayoutmsgmaindto.setSpayeracct(PayerAcct);// �������˺�
				tvpayoutmsgmaindto.setSpayername(PayerName);// ����������
				tvpayoutmsgmaindto.setSpayeraddr(PayerAddr);// �����˵�ַ
				tvpayoutmsgmaindto.setNmoney(Amt);// ���
				tvpayoutmsgmaindto.setSpayeeopbkno(PayeeOpBkNo);// �տ�����к�
				tvpayoutmsgmaindto.setSpayeebankno(PayeeBankNo);//�տ����к�
				tvpayoutmsgmaindto.setSpayeeacct(PayeeAcct);// �տ����˺�
				tvpayoutmsgmaindto.setSpayeename(PayeeName);// �տ�������
				tvpayoutmsgmaindto.setStrimflag(TrimSign);// �����ڱ�־
				tvpayoutmsgmaindto.setSbudgetunitcode(BdgOrgCode==null?"":BdgOrgCode); // Ԥ�㵥λ����
				if(rpmap!=null && rpmap.get(BdgOrgCode)!=null){
					tvpayoutmsgmaindto.setSunitcodename(rpmap.get(BdgOrgCode).getScorpname()); // Ԥ�㵥λ����
				}else{
					tvpayoutmsgmaindto.setSunitcodename(""); // Ԥ�㵥λ����
				}
				tvpayoutmsgmaindto.setSofyear(OfYear);// �������
				tvpayoutmsgmaindto.setSbudgettype(BudgetType);// Ԥ������
				tvpayoutmsgmaindto.setSusercode("sysadmin");// ¼��Ա����
				tvpayoutmsgmaindto.setSaddword(AddWord); // ����
				tvpayoutmsgmaindto.setSdemo(BckReason); // ��ע
				tvpayoutmsgmaindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS); // ����״̬
				tvpayoutmsgmaindto.setSbackflag(StateConstant.MSG_BACK_FLAG_YES);// �˻ر�־
				tvpayoutmsgmaindto.setSisreturn(StateConstant.COMMON_NO);//�Ƿ��Ѿ�����ƾ֤
				tvpayoutmsgmaindto.setSbckreason(BckReason);// �˻�ԭ��
				
				
				//���ڴ洢�ֱ���Ϣ
				List<IDto> subList = new ArrayList<IDto>();
				// ������Ϣ CFX->MSG->Bill3145->Detail3145
				List Detail3145s = (List) Bill3145.get("Detail3145");
				for (int j = 0; j < Detail3145s.size(); j++) {
					HashMap Detail3145 = (HashMap) Detail3145s.get(j);
					String SeqNo = (String) Detail3145.get("SeqNo"); // ���
					String FuncBdgSbtCode = (String) Detail3145.get("FuncBdgSbtCode"); // �������Ŀ����
					String EcnomicSubjectCode = (String) Detail3145.get("EcnomicSubjectCode"); // �������Ŀ����
					String BudgetPrjCode = (String) Detail3145.get("BudgetPrjCode"); // Ԥ����Ŀ����
					BigDecimal Amt1 = MtoCodeTrans.transformBigDecimal(Detail3145.get("Amt")); // ������

					// ��֯DTO׼��������ϸ��Ϣ--------------------------
					// ʵ���ʽ��ޣ��У�ֽƾ֤��������Ϣdto
					TvPayoutbackmsgSubDto tvpayoutmsgsubdto = new TvPayoutbackmsgSubDto();
					tvpayoutmsgsubdto.setSbizno(seq); // ҵ����ˮ��
					tvpayoutmsgsubdto.setSseqno(Integer.parseInt(SeqNo)); // ��ϸ���
					tvpayoutmsgsubdto.setSecnomicsubjectcode(EcnomicSubjectCode); // �������Ŀ����
					tvpayoutmsgsubdto.setSbudgetprjcode(BudgetPrjCode); // Ԥ����Ŀ����
					tvpayoutmsgsubdto.setNmoney(Amt1); // ���
					tvpayoutmsgsubdto.setSfunsubjectcode(FuncBdgSbtCode); // ���ܿ�Ŀ����
					if(budgetsubjectmap != null && budgetsubjectmap.get(FuncBdgSbtCode)!=null){
						if(budgetsubjectmap.get(FuncBdgSbtCode).getSsubjectname().length() >= 15)
						{
							tvpayoutmsgsubdto.setSfunsubjectname(budgetsubjectmap.get(FuncBdgSbtCode).getSsubjectname().substring(0, 15));
						}else{
							tvpayoutmsgsubdto.setSfunsubjectname(budgetsubjectmap.get(FuncBdgSbtCode).getSsubjectname());							
						}
					}
					subList.add(tvpayoutmsgsubdto);
				}
				
				/**
				 * ����ԭƾ֤�Ĵ������������2252ԭƾ֤�����������״̬
				 * @author �Ż��
				 */
				TfPaybankRefundmainDto tfPaybankRefundmainDto = new TfPaybankRefundmainDto();
				tfPaybankRefundmainDto.setSorgcode(orgcode);
				tfPaybankRefundmainDto.setStrecode(trreCode);
				tfPaybankRefundmainDto.setSoriginalvoucherno(OriVouNo);
				tfPaybankRefundmainDto.setSpaytypecode(StateConstant.PAYOUT_PAY_CODE);
				List<TfPaybankRefundmainDto> paymentDetailDtoList = CommonFacade.getODB().findRsByDto(tfPaybankRefundmainDto);
				if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0&&null!=paymentDetailDtoList&&paymentDetailDtoList.size()>0){
					//��Ԥ���ֶ�S_HOLD4����������־���������ֵ��ʡ������˿�
					tvpayoutmsgmaindto.setShold4(StateConstant.BIZTYPE_CODE_BATCH);
					
					updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
					
					//��������
					String updateSql = "update " + TfPaybankRefundmainDto.tableName() + " set S_STATUS = ? , S_DEMO = ? ,N_PAYAMT = ? "
							+ " where S_ORGCODE = ? and S_TRECODE = ? and S_ORIGINALVOUCHERNO = ? and S_PAYTYPECODE = ? ";
					
					updateExce.clearParams();
					updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);//״̬
					updateExce.addParam(BckReason);//�˻�ԭ��
					updateExce.addParam(Amt);//�˻ؽ��
					updateExce.addParam(orgcode);//�����������
					updateExce.addParam(trreCode);//�������
					updateExce.addParam(OriVouNo);//ƾ֤���
					updateExce.addParam(StateConstant.PAYOUT_PAY_CODE);//֧����ʽ����
					updateExce.runQuery(updateSql);
					
					//�����ӱ�(����ʵ���ʽ��˿������Դ)
					String updateSubSql = "update " + TfPaybankRefundsubDto.tableName() + " set S_HOLD1 = ? , S_HOLD2 = ? ,S_HOLD3 = ? ,S_HOLD4 = ?,S_EXT1 = ? ,S_EXT2 = ? ,S_EXT3 = ? "
					+ " where I_VOUSRLNO = ? ";
			
					updateExce.clearParams();
					updateExce.addParam(orgcode);//�����������
					updateExce.addParam(trreCode);//�������
					updateExce.addParam(OriVouNo);//ԭƾ֤���
					updateExce.addParam(OriVouDate);//ԭƾ֤����
					updateExce.addParam(StateConstant.COMMON_NO);//�Ƿ��Ѿ�����ƾ֤
					updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);//����״̬
					updateExce.addParam(StateConstant.MSG_BACK_FLAG_YES);// �˿��־
					updateExce.addParam(paymentDetailDtoList.get(0).getIvousrlno());//ƾ֤��ˮ��
					updateExce.runQuery(updateSubSql);
					
					
					//����������
					String updateIndexSql = "update " + TvVoucherinfoDto.tableName() + " set S_STATUS = ? , S_DEMO = ? "
					+ " where S_ORGCODE = ? and S_TRECODE = ? and S_VTCODE = ?  and S_VOUCHERNO = ? and S_STATUS = ? ";
					
					updateExce.addParam(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
					updateExce.addParam("����ɹ�");
					updateExce.addParam(orgcode);
					updateExce.addParam(trreCode);
					updateExce.addParam(MsgConstant.VOUCHER_NO_2252);
					updateExce.addParam(paymentDetailDtoList.get(0).getSvoucherno());
					updateExce.addParam(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);//У��ɹ�����Ϊ2252������TIPS
					updateExce.runQueryCloseCon(updateIndexSql);
				}
				TvPayoutbackmsgMainDto findto = new TvPayoutbackmsgMainDto();
				findto.setSvouno(tvpayoutmsgmaindto.getSvouno());
				findto.setStrecode(tvpayoutmsgmaindto.getStrecode());
				List findlist = CommonFacade.getODB().findRsByDto(findto);
				if(findlist==null||findlist.size()<=0||ITFECommonConstant.PUBLICPARAM.contains(",3145insert=more,"))
				{
					// ������������
					DatabaseFacade.getDb().create(tvpayoutmsgmaindto);
					// �����ֱ�����
					DatabaseFacade.getDb().create(CommonUtil.listTArray(subList));
				}
			}
			
			// ��¼������־
			// ��֯DTO׼��������־��Ϣ--------------------------
			String recvseqno = StampFacade.getStampSendSeq("JS"); // ȡ������־��ˮ
			String sendseqno = StampFacade.getStampSendSeq("FS"); // ȡ������־��ˮ
			MsgLogFacade.writeRcvLog(recvseqno, sendseqno, sbookorgcode,
					entrustDate, MsgNo, sorgcode, (String) eventContext
							.getMessage().getProperty("XML_MSG_FILE_PATH"),
					allNum, allAmt, packNo, trreCode, "", billOrg,
					payoutVouType, MsgID,
					DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
					(String) eventContext.getMessage().getProperty(
							MessagePropertyKeys.MSG_SENDER), null, null);

			
			// ��3�� ���淢����־�����ͱ���
			 
			// �Ƿ�����־
			// ��֯DTo׼����������******************************************************
			MsgLogFacade.writeSendLog(sendseqno, recvseqno, sbookorgcode, sdescode,
					entrustDate, MsgNo, (String) eventContext.getMessage()
							.getProperty("XML_MSG_FILE_PATH"), allNum, allAmt,
					packNo, trreCode, "", billOrg, payoutVouType, MsgID,
					DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
					(String) eventContext.getMessage().getProperty(
							MessagePropertyKeys.MSG_SENDER), null, null);

			// ȡ�ñ�����Ϣֱ��ת������
			String xmlstr = (String) eventContext.getMessage().getProperty("MSG_INFO");
			eventContext.getMessage().setPayload(xmlstr);

		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("���ݿ����", e);
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("ȷ��sequence��Ϣ����", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯԭƾ֤ʱ����", e);
		}finally{
			if(updateExce != null){
				updateExce.closeConnection();
			}
		}
	}
}

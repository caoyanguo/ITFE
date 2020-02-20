package com.cfcc.itfe.msgmanager.msg;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.deptone.mto.paymentmto.msgparser.util.MsgFormat;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TdDownloadReportCheckDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsSysbatchDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeonlineDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.webservice.FinReportService;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * ��������TIPS��ĳ��������ת����������ر�����Ϣ�����ı���漰��3129����˰Ʊ�� ��Ҫ���ܣ��˶Դ�TIPSϵͳ���͸�ITFEϵͳ������Ϣ
 * 
 * @author zhouchuan
 * 
 */
public class Proc3129MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Proc3129MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		/**
		 * ������Ϣͷ
		 */
		HashMap batchheadMap = (HashMap) msgMap.get("BatchHead3129");
		String ApplyDate = (String) batchheadMap.get("ApplyDate"); // ��������
		String finOrgCode = (String) batchheadMap.get("FinOrgCode"); // �������ش���
		String applyDate = (String) batchheadMap.get("ApplyDate"); // ��������
		String packNo = (String) batchheadMap.get("PackNo"); // ����ˮ��
		String treCode = (String) batchheadMap.get("TreCode"); // �������
		String treName = (String) batchheadMap.get("TreName"); // ��������
		String allNum = (String) batchheadMap.get("AllNum"); // �ܱ���
		BigDecimal allAmt = MtoCodeTrans.transformBigDecimal(batchheadMap
				.get("AllAmt"));// �ܽ��
		int childPackNum = Integer.valueOf((String) batchheadMap
				.get("ChildPackNum")); // �Ӱ�����
		String curPackNo = (String) batchheadMap.get("CurPackNo"); // �������
		int curPackNum = Integer.valueOf((String) batchheadMap
				.get("CurPackNum")); // ��������
		BigDecimal curPackAmt = MtoCodeTrans.transformBigDecimal(batchheadMap
				.get("CurPackAmt"));// �������
		String sbookorgcode = (String) headMap.get("DES");

		
		List <IDto> list = new ArrayList<IDto>();
		// ��ѯ�����Ƿ���չ�
		TvRecvlogDto logDto = new TvRecvlogDto();
		logDto.setSseq((String) headMap.get("MsgID"));
		try {
			HashMap<String, TsConvertfinorgDto> mapFincInfoByFinc = SrvCacheFacade.cacheFincInfoByFinc(null);
			if (!mapFincInfoByFinc.containsKey(finOrgCode)) {
				eventContext.setStopFurtherProcessing(true);
				return;
			}
			// ���ݹ����ѯ��������
			TsTreasuryDto _dto = SrvCacheFacade.cacheTreasuryInfo(null).get(treCode);
			if (null != _dto) {
				sbookorgcode = _dto.getSorgcode();
			}
			List<TvRecvlogDto> finddtolist = CommonFacade.getODB().findRsByDto(
					logDto);
			// �Ѿ�������־��¼
			if (null == finddtolist || finddtolist.size() == 0) { // ˵���Ѿ�û�н��չ����������½���
				HashMap taxbody3129 = (HashMap) msgMap.get("TaxBody3129");
				if (null != taxbody3129) {
					List taxinfo3129 = (List) taxbody3129.get("TaxInfo3129");
					if (null == taxinfo3129 || taxinfo3129.size() == 0) {
						return;
					} else {
						try {
							int count = taxinfo3129.size();
							for (int i = 0; i < count; i++) {
								HashMap taxBody3129map = (HashMap) taxinfo3129
										.get(i);
								HashMap taxInfo3129map = (HashMap) taxBody3129map
										.get("TaxInfo3129");
								HashMap taxVou3129 = (HashMap) taxBody3129map
										.get("TaxVou3129");
								String TaxOrgCode = (String) taxVou3129
										.get("TaxOrgCode"); // ���ջ��ش���
								String PayBnkNo = (String) taxVou3129
										.get("PayBnkNo"); // �������к�
								String TraNo = (String) taxVou3129.get("TraNo"); // ������ˮ��
								String OriMsgNo = (String) taxVou3129
										.get("OriMsgNo"); // ԭ���ı��
								BigDecimal TraAmt = MtoCodeTrans
										.transformBigDecimal(taxVou3129
												.get("TraAmt")); // ���׽��
								String PayerOpBkNo = (String) taxVou3129
										.get("PayerOpBkNo"); // ��������к�
								String PayerOpBkName = (String) taxVou3129
										.get("PayerOpBkName"); // �����������
								String HandOrgName = (String) taxVou3129
										.get("HandOrgName"); // �ɿλ����
								String PayAcct = (String) taxVou3129
										.get("PayAcct"); // �����˻�
								String TaxVouNo = (String) taxVou3129
										.get("TaxVouNo"); // ˰Ʊ����
								String BillDate = (String) taxVou3129
										.get("BillDate"); // ��Ʊ����
								String TaxPayCode = (String) taxVou3129
										.get("TaxPayCode"); // ��˰�˱���
								String TaxPayName = (String) taxVou3129
										.get("TaxPayName"); // ��˰������
								String BudgetType = (String) taxVou3129
										.get("BudgetType"); // Ԥ������
								String TrimSign = (String) taxVou3129
										.get("TrimSign"); // �����ڱ�־
								String CorpCode = (String) taxVou3129
										.get("CorpCode"); // ��ҵ����
								String CorpName = (String) taxVou3129
										.get("CorpName"); // ��ҵ����
								String CorpType = (String) taxVou3129
										.get("CorpType"); // ��ҵע������
								String BudgetSubjectCode = (String) taxVou3129
										.get("BudgetSubjectCode"); // Ԥ���Ŀ����
								String BudgetSubjectName = (String) taxVou3129
										.get("BudgetSubjectName"); // Ԥ���Ŀ����
								String LimitDate = (String) taxVou3129
										.get("LimitDate"); // �޽�����
								String TaxTypeCode = (String) taxVou3129
										.get("TaxTypeCode"); // ˰�ִ���
								String TaxTypeName = (String) taxVou3129
										.get("TaxTypeName"); // ˰������
								String BudgetLevelCode = (String) taxVou3129
										.get("BudgetLevelCode"); // Ԥ�㼶�δ���
								String BudgetLevelName = (String) taxVou3129
										.get("BudgetLevelName"); // Ԥ�㼶������
								String TaxStartDate = (String) taxVou3129
										.get("TaxStartDate"); // ˰������������
								String TaxEndDate = (String) taxVou3129
										.get("TaxEndDate"); // ˰����������ֹ
								String ViceSign = (String) taxVou3129
										.get("ViceSign"); // ������־
								String TaxType = (String) taxVou3129
										.get("TaxType"); // ˰������
								String Remark = (String) taxVou3129
										.get("Remark"); // ��ע
								String Remark1 = (String) taxVou3129
										.get("Remark1"); // ��ע1
								String Remark2 = (String) taxVou3129
										.get("Remark2"); // ��ע2
								String OpStat = (String) taxVou3129
										.get("OpStat"); // ����״̬
								TvFinIncomeonlineDto dto = new TvFinIncomeonlineDto();
								/**
								 * ��֯DTO׼����������*****************************
								 */
								String _sseq = "";
								try {
									_sseq = StampFacade.getStampSendSeq("JS"); // ȡ�������������ˮ��Ϣҵ����ˮ
								} catch (SequenceException e) {
									logger.error(e);
									throw new ITFEBizException(
											"ȡ�����������˰Ʊ��Ϣҵ����ˮSEQ����");
								}
								dto.setSseq(_sseq);// ҵ����ˮ��
								dto.setSorgcode(finOrgCode); // �������ش���
								dto.setSapplydate(applyDate); // ��������
								dto.setSpackno(packNo); // ����ˮ��
								dto.setStrecode(treCode); // �������
								dto.setStrename(treName); // ��������					
								if(treName==null || treName.trim().equals("")){
									dto.setStrename("");// ��������
								}else{
									dto.setStrename(treName);// ��������
								}
								dto.setStaxorgcode(TaxOrgCode); // ���ջ��ش���
								if(PayBnkNo==null || PayBnkNo.trim().equals("")){
									dto.setSpaybnkno("");// �������к�
								}else{
								  dto.setSpaybnkno(PayBnkNo.trim());// �������к�
								}
								dto.setStrano(TraNo); // ������ˮ��
								dto.setSorimsgno(OriMsgNo); // ԭ���ı��
								dto.setFtraamt(TraAmt); // ���׽��
								if(PayerOpBkNo==null || PayerOpBkNo.trim().equals("")){
									dto.setSpayeropnbnkno("");// �����˿������к�
								}else{
								   dto.setSpayeropnbnkno(PayerOpBkNo.trim());// �����˿������к�
								}
								
								if(PayerOpBkName==null || PayerOpBkName.trim().equals("")){
									dto.setPayeropbkname("");// �����������
								}else{
								   dto.setPayeropbkname(PayerOpBkName.trim());// �����������
								}
								dto.setShandorgname(HandOrgName); // �ɿλ����
								
								if(PayAcct==null || PayAcct.trim().equals("")){
								   dto.setSpayacct("");// �������˺�
								}else{
								   dto.setSpayacct(PayAcct.trim());// �������˺�
								}
								dto.setStaxvouno(TaxVouNo); // ˰Ʊ����
								dto.setSbilldate(BillDate); // ��Ʊ����
								dto.setStaxpaycode(TaxPayCode); // ��˰�˱���
								dto.setStaxpayname(TaxPayName); // ��˰������
								dto.setCbudgettype(BudgetType); // Ԥ������
								dto.setCtrimflag(TrimSign); // �����ڱ�־
								
								if(CorpCode==null || CorpCode.trim().equals("")){
									dto.setSetpcode("");// ��ҵ����
								}else{
									dto.setSetpcode(CorpCode.trim());// ��ҵ����
								}
								
								if(CorpName==null || CorpName.trim().equals("")){
									dto.setSetpname("");// ��ҵ����
								}else{
									dto.setSetpname(CorpName.trim());// ��ҵ����
								}
								
								if(CorpType==null || CorpType.trim().equals("")){
									dto.setSetptype("");// ��ҵ����
								}else{
									dto.setSetptype(CorpType.trim());// ��ҵ����
								}
								dto.setSbdgsbtcode(BudgetSubjectCode); // Ԥ���Ŀ����
								
								if(BudgetSubjectName==null || BudgetSubjectName.trim().equals("")){
									dto.setSbdgsbtname("");// Ԥ���Ŀ����
								}else{
									dto.setSbdgsbtname(BudgetSubjectName.trim());// Ԥ���Ŀ����
								}
								dto.setSlimit(LimitDate.trim());// �޽�����
								
								if(TaxTypeCode==null || TaxTypeCode.trim().equals("")){
									dto.setStaxtypecode("");// ˰�ִ���
								}else{
									dto.setStaxtypecode(TaxTypeCode.trim());// ˰�ִ���
								}
								
								if(TaxTypeName==null || TaxTypeName.trim().equals("")){
									dto.setStaxkindname("");// ˰������
								}else{
									dto.setStaxkindname(TaxTypeName.trim());// ˰������
								}
								dto.setCbdglevel(BudgetLevelCode); // Ԥ�㼶�δ���
								if(BudgetLevelName==null || BudgetLevelName.trim().equals("")){
									dto.setCbdglevelname("");// Ԥ�㼶������
								}else{
									dto.setCbdglevelname(BudgetLevelName.trim());// Ԥ�㼶������
								}
								dto.setStaxstartdate(TaxStartDate); // ˰������������
								dto.setStaxenddate(TaxEndDate); // ˰����������ֹ
								
								if(ViceSign==null || ViceSign.trim().equals("")){
									dto.setSastflag("");// ������־
								}else{
									dto.setSastflag(ViceSign);// ������־
								}
								if(TaxType==null || TaxType.trim().equals("")){
									dto.setCtaxtype("");//˰������
								}else{
									dto.setCtaxtype(TaxType.trim());// ˰������
								}
								if(Remark==null || Remark.trim().equals("")){
									dto.setSremark("");// ��ע
								}else{
									dto.setSremark(StringUtil.replace(Remark.trim(), ",", "."));// ��ע
								}
								
								if(Remark1==null || Remark1.trim().equals("")){
									dto.setSremark1("");// ��ע1
								}else{
									dto.setSremark1(StringUtil.replace(Remark1.trim(), ",", "."));// ��ע1
								}
								
								if(Remark2==null || Remark2.trim().equals("")){
									dto.setSremark2("");// ��ע2
								}else{
									dto.setSremark2(StringUtil.replace(Remark2.trim(), ",", "."));// ��ע2
								}
								dto.setStrastate(OpStat.trim());// ����״̬
								dto.setSacct(ApplyDate);//��������
							    dto.setSinputerid("admin");
								dto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));// ϵͳ����ʱ��
								list.add(dto);
								if ((list.size()>0 && list.size() % 1000 == 0) ||( i+1) == count) {
								 DatabaseFacade.getDb().create(
										 list.toArray(new IDto[list.size()]));
								 saveDownloadReportCheck(ApplyDate,treCode);
								 list = new ArrayList<IDto>();
								}
							}
						} catch (JAFDatabaseException e) {
							String error = "����3129���ģ�TIPS�������ݱ��İ��˶ԣ�ʱ�������ݿ��쳣��";
							logger.error(error, e);
							throw new ITFEBizException(error, e);
						}
					}
				}
			}
		} catch (Exception e) {
			String error = "��ѯ3129�����������˰Ʊ��Ϣ����ʱ�������ݿ��쳣��";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		}
		/*
		 * ����/������־
		 */
		String recvseqno;// ������־��ˮ��
		String sendseqno;// ������־��ˮ��
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // ȡ������־��ˮ
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʧ��!", e);
			throw new ITFEBizException("ȡ������ˮ��ʧ��", e);
		}
		String path = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_FILE_NAME);
		// �ǽ�����־
		MsgLogFacade.writeRcvLog(recvseqno, recvseqno, sbookorgcode, applyDate,
				(String) headMap.get("MsgNo"), (String) headMap.get("SRC"),
				path, 0, new BigDecimal(0), packNo, treCode, String.valueOf(childPackNum), finOrgCode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,
				MsgConstant.LOG_ADDWORD_RECV_TIPS);
		
		//�����ж����Ӳ��������Ƿ��Ѿ�������������������WebService֪ͨ����ȡ��    --20131230 by hua
		if("030010011118".equals((String) headMap.get("DES"))) {//Ŀǰֻ֧�ֱ���
			//1�����ȿ��Ƿ��Ѿ�����������֪ͨ�����û�з��������
			boolean boo = BusinessFacade.checkAndSaveRecvlog(finOrgCode, applyDate, MsgConstant.MSG_NO_3129, StateConstant.COMMON_NO);
			if(!boo) {
				//2��У�����������Բ��õ�msgid�ļ���
				String[] msgidArray = BusinessFacade.checkIfComplete(finOrgCode, applyDate, MsgConstant.MSG_NO_3129);
				//3������֪ͨ�ӿ�
				if(null != msgidArray && msgidArray.length > 0) {
					FinReportService finReportService = new FinReportService();
					try {
						finReportService.readReportNotice(finOrgCode, applyDate, MsgConstant.MSG_NO_3129,String.valueOf(childPackNum),msgidArray);
						BusinessFacade.checkAndSaveRecvlog(finOrgCode, applyDate, MsgConstant.MSG_NO_3129, StateConstant.COMMON_YES);
					} catch (UnsupportedEncodingException e) {
						String error = "����3129���ģ�TIPS�������ݱ��İ��˶ԣ�ʱ�������ݿ��쳣��";
						logger.error(error, e);
						throw new ITFEBizException(error, e);
					}
					
				}
			}
		}
		
		eventContext.setStopFurtherProcessing(true);
		return;
	

	}
	private void saveDownloadReportCheck(String date,String trecode)
	{
		if(date==null||trecode==null||"".equals(date)||"".equals(trecode))
			return;
		TdDownloadReportCheckDto finddto = new TdDownloadReportCheckDto();
		finddto.setSdates(date);
		finddto.setStrecode(trecode);
		try {
			TdDownloadReportCheckDto dto = (TdDownloadReportCheckDto)DatabaseFacade.getODB().find(finddto);
			if(dto==null)
			{
				finddto.setSshuipiao("1");
				DatabaseFacade.getODB().create(finddto);
			}else
			{
				if("0".equals(dto.getSshuipiao())||null==dto.getSshuipiao())
				{
					dto.setSshuipiao("1");
					DatabaseFacade.getODB().update(dto);
				}
			}
		} catch (JAFDatabaseException e) {
			log.error("�������ر����������ʧ��:"+e.toString());
		}catch(Exception e)
		{
			log.error("�������ر����������ʧ��:"+e.toString());
		}
	}
}

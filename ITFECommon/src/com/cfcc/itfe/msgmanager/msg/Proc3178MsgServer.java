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
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TdDownloadReportCheckDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFinCustomonlineDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeonlineDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.webservice.FinReportService;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * ���ز�������˰Ʊ3178
 * 
 * @author zhangliang
 * 
 */
@SuppressWarnings("unchecked")
public class Proc3178MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Proc3178MsgServer.class);

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
		HashMap batchheadMap = (HashMap) msgMap.get("BatchHead3178");
		String finOrgCode = (String) batchheadMap.get("FinOrgCode"); // �������ش���
		String applyDate = (String) batchheadMap.get("ApplyDate"); // ��������
		String packNo = (String) batchheadMap.get("PackNo"); // ����ˮ��
		String treCode = (String) batchheadMap.get("TreCode"); // �������
		String treName = (String) batchheadMap.get("TreName"); // ��������
		String allNum = (String) batchheadMap.get("AllNum"); // �ܱ���
		BigDecimal allAmt = MtoCodeTrans.transformBigDecimal(batchheadMap.get("AllAmt"));// �ܽ��
		int childPackNum = Integer.valueOf((String) batchheadMap.get("ChildPackNum")); // �Ӱ�����
		String curPackNo = (String) batchheadMap.get("CurPackNo"); // �������
		int curPackNum = Integer.valueOf((String) batchheadMap.get("CurPackNum")); // ��������
		BigDecimal curPackAmt = MtoCodeTrans.transformBigDecimal(batchheadMap.get("CurPackAmt"));// �������
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
				HashMap taxbody3178 = (HashMap) msgMap.get("TaxBody3178");
				if (null != taxbody3178) {
					List taxinfo3178 = (List) taxbody3178.get("TaxInfo3178");
					if (null == taxinfo3178 || taxinfo3178.size() == 0) {
						return;
					} else {
						try {
							int count = taxinfo3178.size();
							for (int i = 0; i < count; i++) {
								HashMap taxBody3178map = (HashMap) taxinfo3178.get(i);
								HashMap taxInfo3178map = (HashMap) taxBody3178map.get("TaxInfo3178");
								HashMap taxVou3178 = (HashMap) taxBody3178map.get("TaxVou3178");
								String TaxOrgCode = (String) taxVou3178.get("TaxOrgCode"); // ���ջ��ش���
								String PayBnkNo = (String) taxVou3178.get("PayBnkNo"); // �������к�
								String TraNo = (String) taxVou3178.get("TraNo"); // ������ˮ��
								String OriMsgNo = (String) taxVou3178.get("OriMsgNo"); // ԭ���ı��
								BigDecimal TraAmt = MtoCodeTrans.transformBigDecimal(taxVou3178.get("TraAmt")); // ���׽��
								String PayerOpBkNo = (String) taxVou3178.get("PayerOpBkNo"); // ��������к�
								String PayerOpBkName = (String) taxVou3178.get("PayerOpBkName"); // �����������
								String HandOrgName = (String) taxVou3178.get("HandOrgName"); // �ɿλ����
								String PayAcct = (String) taxVou3178.get("PayAcct"); // �����˻�
								String TaxVouNo = (String) taxVou3178.get("TaxVouNo"); // ˰Ʊ����
								String BillDate = (String) taxVou3178.get("BillDate"); // ��Ʊ����
								String TaxPayCode = (String) taxVou3178.get("TaxPayCode"); // ��˰�˱���
								String TaxPayName = (String) taxVou3178.get("TaxPayName"); // ��˰������
								String TaxPayCode1 = (String) taxVou3178.get("TaxPayCode1"); // ��˰�˱���
								String TaxPayName1 = (String) taxVou3178.get("TaxPayName1"); // ��˰������
								String BudgetType = (String) taxVou3178.get("BudgetType"); // Ԥ������
								String TrimSign = (String) taxVou3178.get("TrimSign"); // �����ڱ�־
								String CorpCode = (String) taxVou3178.get("CorpCode"); // ��ҵ����
								String CorpName = (String) taxVou3178.get("CorpName"); // ��ҵ����
								String CorpType = (String) taxVou3178.get("CorpType"); // ��ҵע������
								String BudgetSubjectCode = (String) taxVou3178.get("BudgetSubjectCode"); // Ԥ���Ŀ����
								String BudgetSubjectName = (String) taxVou3178.get("BudgetSubjectName"); // Ԥ���Ŀ����
								String LimitDate = (String) taxVou3178.get("LimitDate"); // �޽�����
								String TaxTypeCode = (String) taxVou3178.get("TaxTypeCode"); // ˰�ִ���
								String TaxTypeName = (String) taxVou3178.get("TaxTypeName"); // ˰������
								String BudgetLevelCode = (String) taxVou3178.get("BudgetLevelCode"); // Ԥ�㼶�δ���
								String BudgetLevelName = (String) taxVou3178.get("BudgetLevelName"); // Ԥ�㼶������
								String TaxStartDate = (String) taxVou3178.get("TaxStartDate"); // ˰������������
								String TaxEndDate = (String) taxVou3178.get("TaxEndDate"); // ˰����������ֹ
								String ViceSign = (String) taxVou3178.get("ViceSign"); // ������־
								String TaxType = (String) taxVou3178.get("TaxType"); // ˰������
								String Remark = (String) taxVou3178.get("Remark"); // ��ע
								String Remark1 = (String) taxVou3178.get("Remark1"); // ��ע1
								String Remark2 = (String) taxVou3178.get("Remark2"); // ��ע2
								String OpStat = (String) taxVou3178.get("OpStat"); // ����״̬
//								TvFinIncomeonlineDto dto = new TvFinIncomeonlineDto();
								TvFinCustomonlineDto dto = new TvFinCustomonlineDto();
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
								dto.setSfinorgcode(finOrgCode); // �������ش���
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
								dto.setNtraamt(TraAmt); // ���׽��
								if(PayerOpBkNo==null || PayerOpBkNo.trim().equals("")){
									dto.setSpayeropbkno("");// �����˿������к�
								}else{
								   dto.setSpayeropbkno(PayerOpBkNo.trim());// �����˿������к�
								}
								
								if(PayerOpBkName==null || PayerOpBkName.trim().equals("")){
									dto.setSpayeropbkname("");// �����������
								}else{
								   dto.setSpayeropbkname(PayerOpBkName.trim());// �����������
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
								dto.setSbudgettype(BudgetType); // Ԥ������
								dto.setStrimsign(TrimSign); // �����ڱ�־
								
								if(CorpCode==null || CorpCode.trim().equals("")){
									dto.setScorpcode("");// ��ҵ����
								}else{
									dto.setScorpcode(CorpCode.trim());// ��ҵ����
								}
								
								if(CorpName==null || CorpName.trim().equals("")){
									dto.setScorpname("");// ��ҵ����
								}else{
									dto.setScorpname(CorpName.trim());// ��ҵ����
								}
								
								if(CorpType==null || CorpType.trim().equals("")){
									dto.setScorptype("");// ��ҵ����
								}else{
									dto.setScorptype(CorpType.trim());// ��ҵ����
								}
								dto.setSbudgetsubjectcode(BudgetSubjectCode); // Ԥ���Ŀ����
								
								if(BudgetSubjectName==null || BudgetSubjectName.trim().equals("")){
									dto.setSbudgetsubjectname("");// Ԥ���Ŀ����
								}else{
									dto.setSbudgetsubjectname(BudgetSubjectName.trim());// Ԥ���Ŀ����
								}
								dto.setSlimitdate(LimitDate.trim());// �޽�����
								
								if(TaxTypeCode==null || TaxTypeCode.trim().equals("")){
									dto.setStaxtypecode("");// ˰�ִ���
								}else{
									dto.setStaxtypecode(TaxTypeCode.trim());// ˰�ִ���
								}
								
								if(TaxTypeName==null || TaxTypeName.trim().equals("")){
									dto.setStaxtypename("");// ˰������
								}else{
									dto.setStaxtypename(TaxTypeName.trim());// ˰������
								}
								dto.setSbudgetlevelcode(BudgetLevelCode); // Ԥ�㼶�δ���
								if(BudgetLevelName==null || BudgetLevelName.trim().equals("")){
									dto.setSbudgetlevelname("");// Ԥ�㼶������
								}else{
									dto.setSbudgetlevelname(BudgetLevelName.trim());// Ԥ�㼶������
								}
								dto.setStaxstartdate(TaxStartDate); // ˰������������
								dto.setStaxenddate(TaxEndDate); // ˰����������ֹ
								
								if(ViceSign==null || ViceSign.trim().equals("")){
									dto.setSvicesign("");// ������־
								}else{
									dto.setSvicesign(ViceSign);// ������־
								}
								if(TaxType==null || TaxType.trim().equals("")){
									dto.setStaxtype("");//˰������
								}else{
									dto.setStaxtype(TaxType.trim());// ˰������
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
								dto.setSopstat(OpStat.trim());// ����״̬
								dto.setSext1(String.valueOf(new Timestamp(new java.util.Date().getTime())));// ϵͳ����ʱ��
								dto.setSext2(TimeFacade.getCurrentStringTime());
								list.add(dto);
								if ((list.size()>0 && list.size() % 1000 == 0) ||( i+1) == count) {
								 DatabaseFacade.getDb().create(
										 list.toArray(new IDto[list.size()]));
								 list = new ArrayList<IDto>();
								}
							}
							saveDownloadReportCheck(applyDate,treCode);
						} catch (JAFDatabaseException e) {
							String error = "����3178���ģ�TIPS�������ݱ��İ��˶ԣ�ʱ�������ݿ��쳣��";
							logger.error(error, e);
							throw new ITFEBizException(error, e);
						}
					}
				}
			}
		} catch (Exception e) {
			String error = "��ѯ3178�����������˰Ʊ��Ϣ����ʱ�������ݿ��쳣��";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		}
		/*
		 * ����/������־
		 */
		String recvseqno;// ������־��ˮ��
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // ȡ������־��ˮ
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʧ��!", e);
			throw new ITFEBizException("ȡ������ˮ��ʧ��", e);
		}
		String path = (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH");
		// �ǽ�����־
		MsgLogFacade.writeRcvLog(recvseqno, recvseqno, sbookorgcode, applyDate,
				(String) headMap.get("MsgNo"), (String) headMap.get("SRC"),
				path, Integer.parseInt(allNum),allAmt, packNo, treCode, String.valueOf(childPackNum), finOrgCode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,
				MsgConstant.LOG_ADDWORD_RECV_TIPS);
		
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
				finddto.setSishaiguan("1");
				DatabaseFacade.getODB().create(finddto);
			}else
			{
				if("0".equals(dto.getSshuipiao())||null==dto.getSshuipiao())
				{
					dto.setSishaiguan("1");
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

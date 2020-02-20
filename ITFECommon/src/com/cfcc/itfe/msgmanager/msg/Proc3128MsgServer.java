/**
 * �����ձ�����Ϣ
 */
package com.cfcc.itfe.msgmanager.msg;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TdDownloadReportCheckDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.webservice.FinReportService;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * 
 * @author wangtuo
 * 
 */
public class Proc3128MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Proc3128MsgServer.class);

	/**
	 * ������Ϣ����
	 * 
	 * @param eventContext
	 * @throws ITFEBizException
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		MuleMessage muleMessage = eventContext.getMessage();
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		/**
		 * ��������ͷ headMap
		 */
		String orgcode = (String) headMap.get("DES");// ���ջ�������
		String sendorgcode = (String) headMap.get("SRC");// ���ͻ�������
		String msgNo = (String) headMap.get("MsgNo");// ���ı��
		String msgid = (String) headMap.get("MsgID"); // ����id��
		String msgref = (String) headMap.get("MsgRef"); // ����id��
		/**
		 * ������Ϣͷ ȡ�ò��������뱨������ msgMap --> BillHead3128
		 */
		HashMap billheadMap = (HashMap) msgMap.get("BillHead3128");
		String finOrgCode = (String) billheadMap.get("FinOrgCode"); // �������ش���
		String reportDate = (String) billheadMap.get("ReportDate"); // ������������
		String trecode = ""; // �������
		boolean iskucun = false;
		boolean isshouru = false;
		String sbookorgcode = (String) headMap.get("DES");

		// ��ѯ�����Ƿ���չ�
		String sql = "select count(1) from TR_INCOMEDAYRPT where s_FinOrgCode = ? and s_RptDate = ? fetch first 1 rows only with ur";
		String sql1 = "select count(1) from HTR_INCOMEDAYRPT where s_FinOrgCode = ? and s_RptDate = ? fetch first 1 rows only with ur";
		SQLResults sr;
		SQLExecutor sqlExec;
		SQLResults sr1;
		SQLExecutor sqlExec1;
		try {
			//�ж��Ƿ����ǹ���ά���Ĳ����������룬��ֹά����������������������ˮ�ظ���
			// �����������뻺��keyΪ��������
			HashMap<String, TsConvertfinorgDto> mapFincInfoByFinc = SrvCacheFacade.cacheFincInfoByFinc(null);
			if (!mapFincInfoByFinc.containsKey(finOrgCode)) {
				eventContext.setStopFurtherProcessing(true);
				return;
			}
			// ���ݲ��������ѯ��������
			TsConvertfinorgDto _dto = SrvCacheFacade.cacheFincInfoByFinc(null)
					.get(finOrgCode);
			if (null != _dto) {
				sbookorgcode = _dto.getSorgcode();
			}
			// �жϵ�ǰ�� �Ƿ��в�����ǰ���������
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			sqlExec.addParam(finOrgCode);
			sqlExec.addParam(reportDate);
			sr = sqlExec.runQueryCloseCon(sql);
//			// �ж���ʷ�� �Ƿ��в�������ĵ�����
//			sqlExec1 = DatabaseFacade.getDb().getSqlExecutorFactory()
//					.getSQLExecutor();
//
//			sqlExec1.addParam(finOrgCode);
//			sqlExec1.addParam(reportDate);
//			sr1 = sqlExec1.runQueryCloseCon(sql1);
			//�����ж���ʷ����ʹ��ʷ�����Ѿ����ڵ��յ����ݣ�Ҳ�����ظ�����
			if (sr.getLong(0, 0) == 0 ) { // ˵���Ѿ�û�н��չ����������½���

				/**
				 * ����������Ԥ�����뱨���� msgMap --> NrBudget3128R
				 */
				HashMap nrbudget3128 = (HashMap) msgMap.get("NrBudget3128");
				if (null != nrbudget3128) {
					List nrbudgetbill3128 = (List) nrbudget3128
							.get("NrBudgetBill3128");
					if (null == nrbudgetbill3128
							|| nrbudgetbill3128.size() == 0) {
						// û�����������뱨������
					} else {
						int incomecount = nrbudgetbill3128.size();
						// �����LIST
						List<IDto> incomelist = new ArrayList<IDto>();
						for (int i = 0; i < incomecount; i++) {
							// CFX->MSG->NrBudget3128->NrBudgetBill3128
							HashMap billmap = (HashMap) nrbudgetbill3128.get(i);

							// ��������-Ԥ�������ձ���(3128)
							TrIncomedayrptDto tmpdto = new TrIncomedayrptDto();
							trecode = (String)billmap.get("TreCode");
							isshouru = true;
							tmpdto.setStaxorgcode((String) billmap
									.get("TaxOrgCode")); // ���ջ��ش���
							trecode = (String) billmap.get("TreCode");
							tmpdto.setStrecode((String) billmap.get("TreCode")); // �����������
							tmpdto.setSbudgettype((String) billmap
									.get("BudgetType")); // Ԥ������
							tmpdto.setSbudgetlevelcode((String) billmap
									.get("BudgetLevelCode")); // Ԥ�㼶�δ���
							tmpdto.setSbudgetsubcode((String) billmap
									.get("BudgetSubjectCode")); // Ԥ���Ŀ
							tmpdto.setSbudgetsubname((String) billmap
									.get("BudgetSubjectName")); // Ԥ���Ŀ����
							tmpdto
									.setNmoneyday(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("DayAmt"))); // ���ۼƽ��
							tmpdto.setNmoneytenday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TenDayAmt"))); // Ѯ�ۼƽ��
							tmpdto.setNmoneymonth(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("MonthAmt"))); // ���ۼƽ��
							tmpdto.setNmoneyquarter(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("QuarterAmt"))); // ���ۼƽ��
							tmpdto
									.setNmoneyyear(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("YearAmt"))); // ���ۼƽ��

							tmpdto.setSrptdate(reportDate); // ��������
							tmpdto
									.setSbelongflag(getRuleSignByTaxOrgCode(tmpdto
											.getStaxorgcode())); // Ͻ����־
							tmpdto.setStrimflag(MsgConstant.TIME_FLAG_NORMAL); // �����ڱ�־
							tmpdto.setSfinorgcode(finOrgCode);// �������ش���
							tmpdto
									.setSbillkind(StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL);// ������Ԥ�����뱨����
							tmpdto.setSdividegroup("0");// �ֳ����־

							incomelist.add(tmpdto);

							if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
									|| (i + 1) == incomecount) {
								try {
									DatabaseFacade.getDb().create(
											CommonUtil.listTArray(incomelist));
								} catch (JAFDatabaseException e) {
									logger.error("�������������뱨��ʱ�������ݿ��쳣!", e);
									throw new ITFEBizException(
											"�������������뱨��ʱ�������ݿ��쳣!", e);
								}
								incomelist.clear();
							}
						}
					}
				}

				/**
				 * �����������˿ⱨ���� msgMap --> NrDrawBack3128
				 */
				HashMap nrdrawback3128 = (HashMap) msgMap.get("NrDrawBack3128");
				if (null != nrdrawback3128) {
					// MSG-> NrDrawBack3128-> NrDrawBackBill3128
					List nrdrawbackbill3128 = (List) nrdrawback3128
							.get("NrDrawBackBill3128");
					if (null == nrdrawbackbill3128
							|| nrdrawbackbill3128.size() == 0) {
						// û���������˿ⱨ������
					} else {

						int incomecount = nrdrawbackbill3128.size();
						// �����LIST
						List<IDto> incomelist = new ArrayList<IDto>();
						for (int i = 0; i < incomecount; i++) {

							// CFX->MSG->NrBudget3128->NrBudgetBill3128
							HashMap billmap = (HashMap) nrdrawbackbill3128
									.get(i);

							// ��������-Ԥ�������ձ���(3128)
							TrIncomedayrptDto tmpdto = new TrIncomedayrptDto();
							trecode = (String)billmap.get("TreCode");
							isshouru = true;
							tmpdto.setStaxorgcode((String) billmap
									.get("TaxOrgCode")); // ���ջ��ش���
							tmpdto.setStrecode((String) billmap.get("TreCode")); // �����������
							tmpdto.setSbudgettype((String) billmap
									.get("BudgetType")); // Ԥ������
							tmpdto.setSbudgetlevelcode((String) billmap
									.get("BudgetLevelCode")); // Ԥ�㼶�δ���
							tmpdto.setSbudgetsubcode((String) billmap
									.get("BudgetSubjectCode")); // Ԥ���Ŀ
							tmpdto.setSbudgetsubname((String) billmap
									.get("BudgetSubjectName")); // Ԥ���Ŀ����
							tmpdto
									.setNmoneyday(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("DayAmt"))); // ���ۼƽ��
							tmpdto.setNmoneytenday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TenDayAmt"))); // Ѯ�ۼƽ��
							tmpdto.setNmoneymonth(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("MonthAmt"))); // ���ۼƽ��
							tmpdto.setNmoneyquarter(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("QuarterAmt"))); // ���ۼƽ��
							tmpdto
									.setNmoneyyear(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("YearAmt"))); // ���ۼƽ��

							tmpdto.setSrptdate(reportDate); // ��������
							tmpdto
									.setSbelongflag(getRuleSignByTaxOrgCode(tmpdto
											.getStaxorgcode())); // Ͻ����־
							tmpdto.setStrimflag(MsgConstant.TIME_FLAG_NORMAL); // �����ڱ�־
							tmpdto.setSfinorgcode(finOrgCode);// �������ش���
							tmpdto
									.setSbillkind(StateConstant.REPORTTYPE_FLAG_NRDRAWBACKBILL);// �������˿ⱨ��
							tmpdto.setSdividegroup("0");// �ֳ����־

							incomelist.add(tmpdto);
							if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
									|| (i + 1) == incomecount) {
								try {
									DatabaseFacade.getDb().create(
											CommonUtil.listTArray(incomelist));
								} catch (JAFDatabaseException e) {
									logger.error("�����������˿ⱨ��ʱ�������ݿ��쳣!", e);
									throw new ITFEBizException(
											"�����������˿ⱨ��ʱ�������ݿ��쳣!", e);
								}
								incomelist.clear();
							}
						}
					}
				}

				/**
				 * ���������ڵ������뱨���� msgMap --> NrRemove3128
				 */
				HashMap nrremove3128 = (HashMap) msgMap.get("NrRemove3128");
				if (null != nrremove3128) {
					List nrremovebill3128 = (List) nrremove3128
							.get("NrRemoveBill3128");
					if (null == nrremovebill3128
							|| nrremovebill3128.size() == 0) {
						// û�������ڵ������뱨������
						// <NrRemoveBill3128>
					} else {

						int incomecount = nrremovebill3128.size();
						// �����LIST
						List<IDto> incomelist = new ArrayList<IDto>();
						for (int i = 0; i < incomecount; i++) {

							// CFX->MSG->NrRemove3128->NrRemoveBill3128
							HashMap billmap = (HashMap) nrremovebill3128.get(i);
							trecode = (String)billmap.get("TreCode");
							isshouru = true;
							// ��������-Ԥ�������ձ���(3128)
							TrIncomedayrptDto tmpdto = new TrIncomedayrptDto();

							tmpdto.setStaxorgcode((String) billmap
									.get("TaxOrgCode")); // ���ջ��ش���
							tmpdto.setStrecode((String) billmap.get("TreCode")); // �����������
							tmpdto.setSbudgettype((String) billmap
									.get("BudgetType")); // Ԥ������
							tmpdto.setSbudgetlevelcode((String) billmap
									.get("BudgetLevelCode")); // Ԥ�㼶�δ���
							tmpdto.setSbudgetsubcode((String) billmap
									.get("BudgetSubjectCode")); // Ԥ���Ŀ
							tmpdto.setSbudgetsubname((String) billmap
									.get("BudgetSubjectName")); // Ԥ���Ŀ����
							tmpdto
									.setNmoneyday(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("DayAmt"))); // ���ۼƽ��
							tmpdto.setNmoneytenday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TenDayAmt"))); // Ѯ�ۼƽ��
							tmpdto.setNmoneymonth(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("MonthAmt"))); // ���ۼƽ��
							tmpdto.setNmoneyquarter(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("QuarterAmt"))); // ���ۼƽ��
							tmpdto
									.setNmoneyyear(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("YearAmt"))); // ���ۼƽ��

							tmpdto.setSrptdate(reportDate); // ��������
							tmpdto
									.setSbelongflag(getRuleSignByTaxOrgCode(tmpdto
											.getStaxorgcode())); // Ͻ����־
							tmpdto.setStrimflag(MsgConstant.TIME_FLAG_NORMAL); // �����ڱ�־
							tmpdto.setSfinorgcode(finOrgCode);// �������ش���
							tmpdto
									.setSbillkind(StateConstant.REPORTTYPE_FLAG_NRREMOVEBILL);// �����ڵ������뱨��
							tmpdto.setSdividegroup("0");// �ֳ����־
							incomelist.add(tmpdto);
							if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
									|| (i + 1) == incomecount) {
								try {
									DatabaseFacade.getDb().create(
											CommonUtil.listTArray(incomelist));
								} catch (JAFDatabaseException e) {
									logger.error("���������ڵ������뱨��ʱ�������ݿ��쳣!", e);
									throw new ITFEBizException(
											"���������ڵ������뱨��ʱ�������ݿ��쳣!", e);
								}
								incomelist.clear();
							}
						}
					}
				}

				/**
				 * �����������ܶ�ֳɱ����� msgMap --> Amount3128
				 */
				HashMap nramountmap = (HashMap) msgMap.get("Amount3128");
				if (null != nramountmap) {
					List amountbill3128 = (List) nramountmap
							.get("AmountBill3128");
					if (null == amountbill3128 || amountbill3128.size() == 0) {
						// û���������ܶ�ֳɱ�������
						// <AmountBill3128>
					} else {

						int incomecount = amountbill3128.size();
						// �����LIST
						List<IDto> incomelist = new ArrayList<IDto>();
						for (int i = 0; i < incomecount; i++) {
							// CFX->MSG->Amount3128->AmountBill3128
							HashMap billmap = (HashMap) amountbill3128.get(i);

							// ��������-Ԥ�������ձ���(3128)
							TrIncomedayrptDto tmpdto = new TrIncomedayrptDto();
							trecode = (String)billmap.get("TreCode");
							isshouru = true;
							tmpdto.setStaxorgcode(sendorgcode); // ���ջ��ش���
							tmpdto.setStrecode((String) billmap.get("TreCode")); // �����������
							tmpdto.setSbudgettype((String) billmap
									.get("BudgetType")); // Ԥ������
							tmpdto.setSbudgetlevelcode((String) billmap
									.get("BudgetLevelCode")); // Ԥ�㼶�δ���
							tmpdto.setSbudgetsubcode((String) billmap
									.get("BudgetSubjectCode")); // Ԥ���Ŀ
							tmpdto.setSbudgetsubname((String) billmap
									.get("BudgetSubjectName")); // Ԥ���Ŀ����
							tmpdto
									.setNmoneyday(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("DayAmt"))); // ���ۼƽ��
							tmpdto.setNmoneytenday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TenDayAmt"))); // Ѯ�ۼƽ��
							tmpdto.setNmoneymonth(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("MonthAmt"))); // ���ۼƽ��
							tmpdto.setNmoneyquarter(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("QuarterAmt"))); // ���ۼƽ��
							tmpdto
									.setNmoneyyear(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("YearAmt"))); // ���ۼƽ��

							tmpdto.setSrptdate(reportDate); // ��������
							tmpdto
									.setSbelongflag(getRuleSignByTaxOrgCode(tmpdto
											.getStaxorgcode())); // Ͻ����־
							tmpdto.setStrimflag(MsgConstant.TIME_FLAG_NORMAL); // �����ڱ�־
							tmpdto.setSfinorgcode(finOrgCode);// �������ش���
							tmpdto
									.setSbillkind(StateConstant.REPORTTYPE_FLAG_AMOUNTBILL);// �������ܶ�ֳɱ���
							tmpdto.setSdividegroup("0");// �ֳ����־

							incomelist.add(tmpdto);
							if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
									|| (i + 1) == incomecount) {
								try {
									DatabaseFacade.getDb().create(
											CommonUtil.listTArray(incomelist));
								} catch (JAFDatabaseException e) {
									logger.error("�����������ܶ�ֳɱ���ʱ�������ݿ��쳣!", e);
									throw new ITFEBizException(
											"�����������ܶ�ֳɱ���ʱ�������ݿ��쳣!", e);
								}
								incomelist.clear();
							}
						}
					}
				}

				/**
				 * ���������ڹ���ֳɱ����� msgMap --> NrShare3128
				 */
				HashMap nrsharemap = (HashMap) msgMap.get("NrShare3128");
				if (null != nrsharemap) {
					List nrsharebill3128 = (List) nrsharemap
							.get("NrShareBill3128");
					if (null == nrsharebill3128 || nrsharebill3128.size() == 0) {
						// û�������ڹ���ֳɱ�������
						// <NrShareBill3128>
					} else {

						int incomecount = nrsharebill3128.size();
						// �����LIST
						List<IDto> incomelist = new ArrayList<IDto>();
						for (int i = 0; i < incomecount; i++) {

							// CFX->MSG->NrShare3128->NrShareBill3128
							HashMap billmap = (HashMap) nrsharebill3128.get(i);

							// ��������-Ԥ�������ձ���(3128)
							TrIncomedayrptDto tmpdto = new TrIncomedayrptDto();
							trecode = (String)billmap.get("TreCode");
							isshouru = true;
							tmpdto.setStaxorgcode((String) billmap
									.get("TaxOrgCode")); // ���ջ��ش���
							tmpdto.setStrecode((String) billmap.get("TreCode")); // �����������
							tmpdto.setSbudgettype((String) billmap
									.get("BudgetType")); // Ԥ������
							tmpdto.setSbudgetlevelcode((String) billmap
									.get("BudgetLevelCode")); // Ԥ�㼶�δ���
							tmpdto.setSbudgetsubcode((String) billmap
									.get("BudgetSubjectCode")); // Ԥ���Ŀ
							tmpdto.setSbudgetsubname((String) billmap
									.get("BudgetSubjectName")); // Ԥ���Ŀ����
							tmpdto
									.setNmoneyday(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("DayAmt"))); // ���ۼƽ��
							tmpdto.setNmoneytenday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TenDayAmt"))); // Ѯ�ۼƽ��
							tmpdto.setNmoneymonth(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("MonthAmt"))); // ���ۼƽ��
							tmpdto.setNmoneyquarter(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("QuarterAmt"))); // ���ۼƽ��
							tmpdto
									.setNmoneyyear(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("YearAmt"))); // ���ۼƽ��

							tmpdto.setSrptdate(reportDate); // ��������
							tmpdto
									.setSbelongflag(getRuleSignByTaxOrgCode(tmpdto
											.getStaxorgcode())); // Ͻ����־
							tmpdto.setStrimflag(MsgConstant.TIME_FLAG_NORMAL); // �����ڱ�־
							tmpdto.setSfinorgcode(finOrgCode);// �������ش���
							tmpdto
									.setSbillkind(StateConstant.REPORTTYPE_FLAG_NRSHAREBILL);// �����ڹ���ֳɱ���
							tmpdto.setSdividegroup((String) billmap
									.get("DivideGroup"));// �ֳ����־
							incomelist.add(tmpdto);
							if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
									|| (i + 1) == incomecount) {
								try {
									DatabaseFacade.getDb().create(
											CommonUtil.listTArray(incomelist));
								} catch (JAFDatabaseException e) {
									logger.error("���������ڹ���ֳɱ���ʱ�������ݿ��쳣!", e);
									throw new ITFEBizException(
											"���������ڹ���ֳɱ���ʱ�������ݿ��쳣!", e);
								}
								incomelist.clear();
							}
						}
					}
				}

				/**
				 * ����������Ԥ�����뱨���� msgMap --> TrBudget3128
				 */
				HashMap trincomemap = (HashMap) msgMap.get("TrBudget3128");
				if (null != trincomemap) {
					List trim_incomelist = (List) trincomemap
							.get("TrBudgetBill3128");
					if (null == trim_incomelist || trim_incomelist.size() == 0) {
						// û�е�����Ԥ�����뱨������
					} else {

						int incomecount = trim_incomelist.size();
						List<IDto> incomelist = new ArrayList<IDto>();
						for (int i = 0; i < incomecount; i++) {
							HashMap billmap = (HashMap) trim_incomelist.get(i);
							TrIncomedayrptDto tmpdto = new TrIncomedayrptDto();
							trecode = (String)billmap.get("TreCode");
							isshouru = true;
							tmpdto.setStaxorgcode((String) billmap
									.get("TaxOrgCode")); // ���ջ��ش���
							tmpdto.setStrecode((String) billmap.get("TreCode")); // �����������
							tmpdto.setSbudgettype((String) billmap
									.get("BudgetType")); // Ԥ������
							tmpdto.setSbudgetlevelcode((String) billmap
									.get("BudgetLevelCode")); // Ԥ�㼶�δ���
							tmpdto.setSbudgetsubcode((String) billmap
									.get("BudgetSubjectCode")); // Ԥ���Ŀ
							tmpdto.setSbudgetsubname((String) billmap
									.get("BudgetSubjectName")); // Ԥ���Ŀ����
							tmpdto
									.setNmoneyday(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("DayAmt"))); // ���ۼƽ��
							tmpdto.setNmoneytenday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TenDayAmt"))); // Ѯ�ۼƽ��
							tmpdto.setNmoneymonth(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("MonthAmt"))); // ���ۼƽ��
							tmpdto.setNmoneyquarter(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("QuarterAmt"))); // ���ۼƽ��
							tmpdto
									.setNmoneyyear(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("YearAmt"))); // ���ۼƽ��

							tmpdto.setSrptdate(reportDate); // ��������
							tmpdto
									.setSbelongflag(getRuleSignByTaxOrgCode(tmpdto
											.getStaxorgcode())); // Ͻ����־
							tmpdto.setStrimflag(MsgConstant.TIME_FLAG_TRIM); // �����ڱ�־
							tmpdto.setSfinorgcode(finOrgCode);// �������ش���
							tmpdto
									.setSbillkind(StateConstant.REPORTTYPE_FLAG_TRBUDGETBILL);// ������Ԥ�����뱨��
							tmpdto.setSdividegroup("0");// �ֳ����־

							incomelist.add(tmpdto);
							if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
									|| (i + 1) == incomecount) {
								try {
									DatabaseFacade.getDb().create(
											CommonUtil.listTArray(incomelist));
								} catch (JAFDatabaseException e) {
									logger.error("������������뱨��ʱ�������ݿ��쳣!", e);
									throw new ITFEBizException(
											"������������뱨��ʱ�������ݿ��쳣!", e);
								}
								incomelist.clear();
							}
						}
					}
				}
				/**
				 * �����������˿ⱨ���� msgMap --> TrDrawBack3128
				 */
				HashMap trdwbkmap = (HashMap) msgMap.get("TrDrawBack3128");
				if (null != trdwbkmap) {
					List trdrawbackbill3128 = (List) trdwbkmap
							.get("TrDrawBackBill3128");
					if (null == trdrawbackbill3128
							|| trdrawbackbill3128.size() == 0) {
						// û�е������˿ⱨ������
						// <TrDrawBackBill3128>
					} else {

						int incomecount = trdrawbackbill3128.size();
						// �����LIST
						List<IDto> incomelist = new ArrayList<IDto>();
						for (int i = 0; i < incomecount; i++) {

							// CFX->MSG->TrDrawBack3128->TrDrawBackBill3128
							HashMap billmap = (HashMap) trdrawbackbill3128
									.get(i);

							// ��������-Ԥ�������ձ���(3128)
							TrIncomedayrptDto tmpdto = new TrIncomedayrptDto();
							trecode = (String)billmap.get("TreCode");
							isshouru = true;
							tmpdto.setStaxorgcode((String) billmap
									.get("TaxOrgCode")); // ���ջ��ش���
							tmpdto.setStrecode((String) billmap.get("TreCode")); // �����������
							tmpdto.setSbudgettype((String) billmap
									.get("BudgetType")); // Ԥ������
							tmpdto.setSbudgetlevelcode((String) billmap
									.get("BudgetLevelCode")); // Ԥ�㼶�δ���
							tmpdto.setSbudgetsubcode((String) billmap
									.get("BudgetSubjectCode")); // Ԥ���Ŀ
							tmpdto.setSbudgetsubname((String) billmap
									.get("BudgetSubjectName")); // Ԥ���Ŀ����
							tmpdto
									.setNmoneyday(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("DayAmt"))); // ���ۼƽ��
							tmpdto.setNmoneytenday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TenDayAmt"))); // Ѯ�ۼƽ��
							tmpdto.setNmoneymonth(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("MonthAmt"))); // ���ۼƽ��
							tmpdto.setNmoneyquarter(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("QuarterAmt"))); // ���ۼƽ��
							tmpdto
									.setNmoneyyear(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("YearAmt"))); // ���ۼƽ��

							tmpdto.setSrptdate(reportDate); // ��������
							tmpdto
									.setSbelongflag(getRuleSignByTaxOrgCode(tmpdto
											.getStaxorgcode())); // Ͻ����־
							tmpdto.setStrimflag(MsgConstant.TIME_FLAG_TRIM); // �����ڱ�־
							tmpdto.setSfinorgcode(finOrgCode);// �������ش���
							tmpdto
									.setSbillkind(StateConstant.REPORTTYPE_FLAG_TRDRAWBACKBILL);// �������˿ⱨ��
							tmpdto.setSdividegroup("0");// �ֳ����־

							incomelist.add(tmpdto);
							if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
									|| (i + 1) == incomecount) {
								try {
									DatabaseFacade.getDb().create(
											CommonUtil.listTArray(incomelist));
								} catch (JAFDatabaseException e) {
									logger.error("����������˿ⱨ��ʱ�������ݿ��쳣!", e);
									throw new ITFEBizException(
											"����������˿ⱨ��ʱ�������ݿ��쳣!", e);
								}
								incomelist.clear();
							}
						}
					}
				}

				/**
				 * ���������ڵ������뱨���� msgMap --> TrRemove3128
				 */
				HashMap trremove3128 = (HashMap) msgMap.get("TrRemove3128");
				if (null != trremove3128) {
					List trremovebill3128 = (List) trremove3128
							.get("TrRemoveBill3128");
					if (null == trremovebill3128
							|| trremovebill3128.size() == 0) {
						// û�е����ڵ������뱨������
						// <TrRemoveBill3128>
					} else {

						int incomecount = trremovebill3128.size();
						// �����LIST
						List<IDto> incomelist = new ArrayList<IDto>();
						for (int i = 0; i < incomecount; i++) {

							// CFX->MSG->TrRemove3128->TrRemoveBill3128
							HashMap billmap = (HashMap) trremovebill3128.get(i);

							// ��������-Ԥ�������ձ���(3128)
							TrIncomedayrptDto tmpdto = new TrIncomedayrptDto();
							trecode = (String)billmap.get("TreCode");
							isshouru = true;
							tmpdto.setStaxorgcode((String) billmap
									.get("TaxOrgCode")); // ���ջ��ش���
							tmpdto.setStrecode((String) billmap.get("TreCode")); // �����������
							tmpdto.setSbudgettype((String) billmap
									.get("BudgetType")); // Ԥ������
							tmpdto.setSbudgetlevelcode((String) billmap
									.get("BudgetLevelCode")); // Ԥ�㼶�δ���
							tmpdto.setSbudgetsubcode((String) billmap
									.get("BudgetSubjectCode")); // Ԥ���Ŀ
							tmpdto.setSbudgetsubname((String) billmap
									.get("BudgetSubjectName")); // Ԥ���Ŀ����
							tmpdto
									.setNmoneyday(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("DayAmt"))); // ���ۼƽ��
							tmpdto.setNmoneytenday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TenDayAmt"))); // Ѯ�ۼƽ��
							tmpdto.setNmoneymonth(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("MonthAmt"))); // ���ۼƽ��
							tmpdto.setNmoneyquarter(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("QuarterAmt"))); // ���ۼƽ��
							tmpdto
									.setNmoneyyear(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("YearAmt"))); // ���ۼƽ��

							tmpdto.setSrptdate(reportDate); // ��������
							tmpdto
									.setSbelongflag(getRuleSignByTaxOrgCode(tmpdto
											.getStaxorgcode())); // Ͻ����־
							tmpdto.setStrimflag(MsgConstant.TIME_FLAG_TRIM); // �����ڱ�־
							tmpdto.setSfinorgcode(finOrgCode);// �������ش���
							tmpdto
									.setSbillkind(StateConstant.REPORTTYPE_FLAG_TRREMOVEBILL);// �����ڵ������뱨��
							tmpdto.setSdividegroup("0");// �ֳ����־

							incomelist.add(tmpdto);

							if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
									|| (i + 1) == incomecount) {
								try {
									DatabaseFacade.getDb().create(
											CommonUtil.listTArray(incomelist));
								} catch (JAFDatabaseException e) {
									logger.error("��������ڵ������뱨��ʱ�������ݿ��쳣!", e);
									throw new ITFEBizException(
											"��������ڵ������뱨��ʱ�������ݿ��쳣!", e);
								}
								incomelist.clear();
							}
						}
					}
				}

				/**
				 * ���������ڹ���ֳɱ����� msgMap --> TrShare3128
				 */
				HashMap trshare3128 = (HashMap) msgMap.get("TrShare3128");
				if (null != trshare3128) {
					List trsharebill3128 = (List) trshare3128
							.get("TrShareBill3128");
					if (null == trsharebill3128 || trsharebill3128.size() == 0) {
						// û�е����ڹ���ֳɱ�������
						// <TrShareBill3128>
					} else {

						int incomecount = trsharebill3128.size();
						// �����LIST
						List<IDto> incomelist = new ArrayList<IDto>();
						for (int i = 0; i < incomecount; i++) {

							// CFX->MSG->TrShare3128->TrShareBill3128
							HashMap billmap = (HashMap) trsharebill3128.get(i);

							// ��������-Ԥ�������ձ���(3128)
							TrIncomedayrptDto tmpdto = new TrIncomedayrptDto();
							trecode = (String)billmap.get("TreCode");
							isshouru = true;
							tmpdto.setStaxorgcode((String) billmap
									.get("TaxOrgCode")); // ���ջ��ش���
							tmpdto.setStrecode((String) billmap.get("TreCode")); // �����������
							tmpdto.setSbudgettype((String) billmap
									.get("BudgetType")); // Ԥ������
							tmpdto.setSbudgetlevelcode((String) billmap
									.get("BudgetLevelCode")); // Ԥ�㼶�δ���
							tmpdto.setSbudgetsubcode((String) billmap
									.get("BudgetSubjectCode")); // Ԥ���Ŀ
							tmpdto.setSbudgetsubname((String) billmap
									.get("BudgetSubjectName")); // Ԥ���Ŀ����
							tmpdto
									.setNmoneyday(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("DayAmt"))); // ���ۼƽ��
							tmpdto.setNmoneytenday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TenDayAmt"))); // Ѯ�ۼƽ��
							tmpdto.setNmoneymonth(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("MonthAmt"))); // ���ۼƽ��
							tmpdto.setNmoneyquarter(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("QuarterAmt"))); // ���ۼƽ��
							tmpdto
									.setNmoneyyear(MtoCodeTrans
											.transformBigDecimal(billmap
													.get("YearAmt"))); // ���ۼƽ��

							tmpdto.setSrptdate(reportDate); // ��������
							tmpdto
									.setSbelongflag(getRuleSignByTaxOrgCode(tmpdto
											.getStaxorgcode())); // Ͻ����־
							tmpdto.setStrimflag(MsgConstant.TIME_FLAG_TRIM); // �����ڱ�־
							tmpdto.setSfinorgcode(finOrgCode);// �������ش���
							tmpdto
									.setSbillkind(StateConstant.REPORTTYPE_FLAG_TRSHAREBILL);// �����ڹ���ֳɱ���
							tmpdto.setSdividegroup((String) billmap
									.get("DivideGroup"));// �ֳ����־

							incomelist.add(tmpdto);
							if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
									|| (i + 1) == incomecount) {
								try {
									DatabaseFacade.getDb().create(
											CommonUtil.listTArray(incomelist));
								} catch (JAFDatabaseException e) {
									logger.error("��������ڹ���ֳɱ���ʱ�������ݿ��쳣!", e);
									throw new ITFEBizException(
											"��������ڹ���ֳɱ���ʱ�������ݿ��쳣!", e);
								}
								incomelist.clear();
							}

						}
					}
				}
				/**
				 * ��������ձ������� msgMap --> Stock3128
				 */
				HashMap stockmap = (HashMap) msgMap.get("Stock3128");
				if (null != stockmap) {
					List stockbill3128 = (List) stockmap.get("StockBill3128");
					if (null == stockbill3128 || stockbill3128.size() == 0) {
						// û�п���ձ�������
						// <StockBill3128>
					} else {
						String sql2 = "delete from TR_STOCKDAYRPT where s_OrgCode = ? and s_RptDate = ? ";
						SQLExecutor sqlExecs = DatabaseFacade.getDb()
								.getSqlExecutorFactory().getSQLExecutor();
						sqlExecs.addParam(finOrgCode);
						sqlExecs.addParam(reportDate);
						sqlExecs.runQueryCloseCon(sql2);
						int stockcount = stockbill3128.size();
						List<IDto> stocklist = new ArrayList<IDto>();
						for (int i = 0; i < stockcount; i++) {
							HashMap billmap = (HashMap) stockbill3128.get(i);
							// ��������-����ձ���(3128)
							TrStockdayrptDto tmpdto = new TrStockdayrptDto();
							trecode = (String)billmap.get("TreCode");
							iskucun = true;
							tmpdto.setSorgcode(finOrgCode);// ������������
							tmpdto.setStrecode((String) billmap.get("TreCode")); // �����������
							tmpdto.setSrptdate(reportDate); // ��������
							tmpdto.setSaccno((String) billmap.get("AcctCode")); // �ʻ�����
							tmpdto
									.setSaccname((String) billmap
											.get("AcctName")); // �ʻ�����
							tmpdto
									.setSaccdate((String) billmap
											.get("AcctDate")); // �ʻ�����
							tmpdto.setNmoneyyesterday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("YesterdayBalance"))); // �������
							tmpdto.setNmoneyin(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TodayReceipt"))); // ��������
							tmpdto.setNmoneyout(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TodayPay"))); // ����֧��
							tmpdto.setNmoneytoday(MtoCodeTrans
									.transformBigDecimal(billmap
											.get("TodayBalance"))); // �������

							stocklist.add(tmpdto);
							if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
									|| (i + 1) == stockcount) {
								try {
									DatabaseFacade.getDb().create(
											CommonUtil.listTArray(stocklist));
								} catch (JAFDatabaseException e) {
									logger.error("���������ձ���ʱ�������ݿ��쳣!", e);
									throw new ITFEBizException(
											"�������ձ�����ʱ�������ݿ��쳣!", e);
								}
								stocklist.clear();
							}
						}
					}
				}

			}
			/*
			 * ����/������־
			 */
			String recvseqno;// ������־��ˮ��
			String sendseqno;// ������־��ˮ��
			if(iskucun)
				saveDownloadReportCheck(reportDate,trecode,true);
			if(isshouru)
				saveDownloadReportCheck(reportDate,trecode,false);
			recvseqno = StampFacade.getStampSendSeq("JS"); // ȡ������־��ˮ
			String path = (String) eventContext.getMessage().getProperty(
					MessagePropertyKeys.MSG_FILE_NAME);
			// �ǽ�����־
			MsgLogFacade.writeRcvLog(recvseqno, recvseqno, sbookorgcode,
					reportDate, (String) headMap.get("MsgNo"), (String) headMap
							.get("SRC"), path, 0, new BigDecimal(0), null,
							trecode, null, finOrgCode, null,
					(String) headMap.get("MsgID"),
					DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
					(String) eventContext.getMessage().getProperty(
							MessagePropertyKeys.MSG_SENDER), null, null);

			//�����ж����Ӳ��������Ƿ��Ѿ�������������������WebService֪ͨ����ȡ��    --20131230 by hua
			if("030010011118".equals((String) headMap.get("DES"))) {//Ŀǰֻ֧�ֱ���
				//1�����ȿ��Ƿ��Ѿ�����������֪ͨ�����û�з��������
				boolean boo = BusinessFacade.checkAndSaveRecvlog(finOrgCode, reportDate, MsgConstant.MSG_NO_3128, StateConstant.COMMON_NO);
				if(!boo) {
					//2��У�����������Բ��õ�msgid�ļ���
					String[] msgidArray = BusinessFacade.checkIfComplete(finOrgCode, reportDate, MsgConstant.MSG_NO_3128);
					//3������֪ͨ�ӿ�
					if(null != msgidArray && msgidArray.length > 0) {
						FinReportService finReportService = new FinReportService();
						try {
							finReportService.readReportNotice(finOrgCode, reportDate, MsgConstant.MSG_NO_3128,"1",msgidArray);
							BusinessFacade.checkAndSaveRecvlog(finOrgCode, reportDate, MsgConstant.MSG_NO_3128, StateConstant.COMMON_YES);
						} catch (UnsupportedEncodingException e) {
							String error = "��������ͱ���ʱ�����쳣��";
							logger.error(error, e);
							throw new ITFEBizException(error, e);
						}
						
					}
				}
			}
			
			eventContext.setStopFurtherProcessing(true);
			return;

		} catch (JAFDatabaseException e1) {
			logger.error("��������3128���ĳ����쳣!", e1);
			throw new ITFEBizException("��������3128���ĳ����쳣!", e1);

		} catch (SequenceException e) {
			logger.error("��������3128���ĳ����쳣!", e);
			throw new ITFEBizException("��������3128���ĳ����쳣!", e);
		}
	}

	/**
	 * �������ջ��ش����ж�Ͻ����־��ȫϽ�򱾼�
	 * 
	 * @param String
	 *            staxorgcode ���ջ��ش���
	 * @return
	 */
	private String getRuleSignByTaxOrgCode(String staxorgcode) {
		if (MsgConstant.MSG_TAXORG_SHARE_CLASS.equals(staxorgcode.trim())
				|| MsgConstant.MSG_TAXORG_NATION_CLASS.equals(staxorgcode
						.trim())
				|| MsgConstant.MSG_TAXORG_PLACE_CLASS
						.equals(staxorgcode.trim())
				|| MsgConstant.MSG_TAXORG_CUSTOM_CLASS.equals(staxorgcode
						.trim())
				|| MsgConstant.MSG_TAXORG_FINANCE_CLASS.equals(staxorgcode
						.trim())
				|| MsgConstant.MSG_TAXORG_OTHER_CLASS
						.equals(staxorgcode.trim())) {
			return MsgConstant.RULE_SIGN_ALL;
		}

		return MsgConstant.RULE_SIGN_SELF;
	}
	private void saveDownloadReportCheck(String date,String trecode,boolean iskucun)
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
				if(iskucun)
					finddto.setSkucun("1");
				else
					finddto.setSribao("1");
				DatabaseFacade.getODB().create(finddto);
			}else
			{
				if(iskucun)
				{
					if("0".equals(dto.getSkucun())||null==dto.getSkucun())
					{
						dto.setSkucun("1");
						DatabaseFacade.getODB().update(dto);
					}
				}
				else
				{
					if("0".equals(dto.getSribao())||null==dto.getSribao())
					{
						dto.setSribao("1");
						DatabaseFacade.getODB().update(dto);
					}
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

package com.cfcc.itfe.service.recbiz.bankvouchervalidate.processer;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfGrantpaymsgsubDto;
import com.cfcc.itfe.service.recbiz.bankvouchervalidate.AbstractBankVoucherValidateProcesser;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * 8202���Ľ�����
 * 
 * @author hua
 * 
 */
public class Process8202 extends AbstractBankVoucherValidateProcesser {
	public MulitTableDto process(String fullFileName) {
		MulitTableDto result = null;

		result = new MulitTableDto();
		result.setFatherDtos(new ArrayList<IDto>());
		result.setSonDtos(new ArrayList<IDto>());
		List<IDto> mainList = result.getFatherDtos();
		List<IDto> subList = result.getSonDtos();
		List<String> delMainList = result.getVoulist(); // ���ڱ���ɾ���ظ���¼ʹ�õ�����
		try {
			Element doc = parseFile2Doc(fullFileName).getRootElement();
			if (doc == null) {
				result.getErrorList().add("�޷��������ļ�, ���֤!.");
				return result;
			}
			List voucherList = doc.selectNodes("Voucher");
			if (voucherList == null || voucherList.size() == 0) {
				result.getErrorList().add("�޷�ʶ��ı�������, �Ҳ���<Voucher/>�ڵ�!");
				return result;
			}

			out: for (Object voucherTemp : voucherList) {
				Element elementVoucher = (Element) voucherTemp;
				String id = elementVoucher.elementText("Id");// ������Ȩ֧��ƾ֤Id
				String admDivCode = elementVoucher.elementText("AdmDivCode");// ������������
				if (StringUtils.isBlank(admDivCode)) {
					result.getErrorList().add(new File(fullFileName).getName() + ": �ļ��д���'������������'Ϊ�����, ���֤!");
					break out;
				}
				String stYear = elementVoucher.elementText("StYear");// ҵ�����
				String vtCode = elementVoucher.elementText("VtCode");// ƾ֤���ͱ��
				if (!MsgConstant.VOUCHER_NO_8202.equals(vtCode)) {
					result.getErrorList().add("��֧�ָ����ͱ��ĵ���!");
					break;
				}
				String vouDate = elementVoucher.elementText("VouDate");// ƾ֤����
				String voucherNo = elementVoucher.elementText("VoucherNo");// ƾ֤��
				if (StringUtils.isBlank(voucherNo)) {
					result.getErrorList().add(new File(fullFileName).getName() + ": �ļ��д���'����ƾ֤���'Ϊ�����, ���֤!");
					break out;
				}
				String fundTypeCode = elementVoucher.elementText("FundTypeCode");// �ʽ����ʱ���
				String fundTypeName = elementVoucher.elementText("FundTypeName");// �ʽ���������
				String bgtTypeCode = elementVoucher.elementText("BgtTypeCode");// Ԥ�����ͱ���
				String bgtTypeName = elementVoucher.elementText("BgtTypeName");// Ԥ����������
				String payTypeCode = elementVoucher.elementText("PayTypeCode");// ֧����ʽ����
				String payTypeName = elementVoucher.elementText("PayTypeName");// ֧����ʽ����
				String proCatCode = elementVoucher.elementText("ProCatCode");// ��֧�������
				String proCatName = elementVoucher.elementText("ProCatName");// ��֧��������
				String mOFDepCode = elementVoucher.elementText("MOFDepCode");// ҵ���ұ���
				String mOFDepName = elementVoucher.elementText("MOFDepName");// ҵ��������
				String supDepCode = elementVoucher.elementText("SupDepCode");// һ��Ԥ�㵥λ����
				String supDepName = elementVoucher.elementText("SupDepName");// һ��Ԥ�㵥λ����
				String agencyCode = elementVoucher.elementText("AgencyCode");// ����Ԥ�㵥λ����
				String agencyName = elementVoucher.elementText("AgencyName");// ����Ԥ�㵥λ����
				String expFuncCode = elementVoucher.elementText("ExpFuncCode");// ���ܷ����Ŀ����
				String expFuncName = elementVoucher.elementText("ExpFuncName");// ���ܷ����Ŀ����
				String expFuncCode1 = elementVoucher.elementText("ExpFuncCode1");// ֧�����ܷ��������
				String expFuncName1 = elementVoucher.elementText("ExpFuncName1");// ֧�����ܷ���������
				String expFuncCode2 = elementVoucher.elementText("ExpFuncCode2");// ֧�����ܷ�������
				String expFuncName2 = elementVoucher.elementText("ExpFuncName2");// ֧�����ܷ��������
				String expFuncCode3 = elementVoucher.elementText("ExpFuncCode3");// ֧�����ܷ��������
				String expFuncName3 = elementVoucher.elementText("ExpFuncName3");// ֧�����ܷ���������
				String expEcoCode = elementVoucher.elementText("ExpEcoCode");// ���÷����Ŀ����
				String expEcoName = elementVoucher.elementText("ExpEcoName");// ���÷����Ŀ����
				String expEcoCode1 = elementVoucher.elementText("ExpEcoCode1");// ֧�����÷��������
				String expEcoName1 = elementVoucher.elementText("ExpEcoName1");// ֧�����÷���������
				String expEcoCode2 = elementVoucher.elementText("ExpEcoCode2");// ֧�����÷�������
				String expEcoName2 = elementVoucher.elementText("ExpEcoName2");// ֧�����÷��������
				String depProCode = elementVoucher.elementText("DepProCode");// Ԥ����Ŀ����
				String depProName = elementVoucher.elementText("DepProName");// Ԥ����Ŀ����
				String setModeCode = elementVoucher.elementText("SetModeCode");// ���㷽ʽ����
				String setModeName = elementVoucher.elementText("SetModeName");// ���㷽ʽ����
				String payBankCode = elementVoucher.elementText("PayBankCode");// �������б���
				String payBankName = elementVoucher.elementText("PayBankName");// ������������
				String clearBankCode = elementVoucher.elementText("ClearBankCode");// �������б���
				String clearBankName = elementVoucher.elementText("ClearBankName");// ������������
				String payeeAcctNo = elementVoucher.elementText("PayeeAcctNo");// �տ����˺�
				String payeeAcctName = elementVoucher.elementText("PayeeAcctName");// �տ�������
				String payeeAcctBankName = elementVoucher.elementText("PayeeAcctBankName");// �տ�������
				String payeeAcctBankNo = elementVoucher.elementText("PayeeAcctBankNo");// �տ��������к�
				String payAcctNo = elementVoucher.elementText("PayAcctNo");// �������˺�
				String payAcctName = elementVoucher.elementText("PayAcctName");// ����������
				String payAcctBankName = elementVoucher.elementText("PayAcctBankName");// ����������
				String paySummaryCode = elementVoucher.elementText("PaySummaryCode");// ��;����
				String paySummaryName = elementVoucher.elementText("PaySummaryName");// ��;����
				String payAmt = elementVoucher.elementText("PayAmt");// ֧�����
				if (StringUtils.isBlank(payAmt)) {
					result.getErrorList().add(new File(fullFileName).getName() + ": �ļ��д���'֧�����'Ϊ�����, ���֤!");
					break out;
				}
				String payMgrCode = elementVoucher.elementText("PayMgrCode");// ֧�����ͱ���
				String payMgrName = elementVoucher.elementText("PayMgrName");// ֧����������
				String fundDealModeCode = elementVoucher.elementText("FundDealModeCode");// ����ʽ����
				String fundDealModeName = elementVoucher.elementText("FundDealModeName");// ����ʽ����
				String acessAuthGroupCode = elementVoucher.elementText("AcessAuthGroupCode");// ��������ҵ��Ȩ�޷����ʶ
				String businessTypeCode = elementVoucher.elementText("BusinessTypeCode");// ҵ�����ͱ���
				String businessTypeName = elementVoucher.elementText("BusinessTypeName");// ҵ����������
				String taxBillNo = elementVoucher.elementText("TaxBillNo");// �걨��˰ƾ֤��
				String taxayerID = elementVoucher.elementText("TaxayerID");// ��˰��ʶ���
				String taxOrgCode = elementVoucher.elementText("TaxOrgCode");// ˰�����ջ��ش���
				String checkNo = elementVoucher.elementText("CheckNo");// ֧Ʊ�ţ�����ţ�
				String xPayDate = elementVoucher.elementText("XPayDate");// ʵ��֧������
				String xAgentBusinessNo = elementVoucher.elementText("XAgentBusinessNo");// ���н�����ˮ��
				String xCheckNo = elementVoucher.elementText("XCheckNo");// ֧Ʊ��(�����)
				String xPayAmt = elementVoucher.elementText("XPayAmt");// ʵ��֧�����
				String xPayeeAcctBankName = elementVoucher.elementText("XPayeeAcctBankName");// �տ�������
				String xPayeeAcctNo = elementVoucher.elementText("XPayeeAcctNo");// �տ����˺�
				String xPayeeAcctName = elementVoucher.elementText("XPayeeAcctName");// �տ���ȫ��
				String hold1 = elementVoucher.elementText("Hold1");// Ԥ���ֶ�1
				String hold2 = elementVoucher.elementText("Hold2");// Ԥ���ֶ�2

				/**
				 * ��װ��������
				 */
				TfGrantpaymsgmainDto mainDto = new TfGrantpaymsgmainDto();
				String mainvou = generateVousrlno();
				mainDto.setIvousrlno(Long.valueOf(mainvou));
				mainDto.setSorgcode(getLoginfo().getSorgcode());
				mainDto.setStrecode(""); // TODO �������
				mainDto.setSfinorgcode(""); // TODO �������ش��� �������д��룿����
				mainDto.setSfilename(fullFileName);
				mainDto.setScommitdate(TimeFacade.getCurrentStringTime()); // ��������ʱ��
				mainDto.setTssysupdate(DateUtil.currentTimestamp());
				mainDto.setSdealno(mainvou.substring(8, 16));
				mainDto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);

				mainDto.setSid(id);
				mainDto.setSadmdivcode(admDivCode);
				mainDto.setSstyear(stYear);
				mainDto.setSvtcode(vtCode);
				mainDto.setSvoudate(vouDate);
				mainDto.setSvoucherno(voucherNo);
				mainDto.setSfundtypecode(fundTypeCode);
				mainDto.setSfundtypename(fundTypeName);
				mainDto.setSbgttypecode(bgtTypeCode);
				mainDto.setSbgttypename(bgtTypeName);
				mainDto.setSpaytypecode(payTypeCode);
				mainDto.setSpaytypename(payTypeName);
				mainDto.setSprocatcode(proCatCode);
				mainDto.setSprocatname(proCatName);
				mainDto.setSmofdepcode(mOFDepCode);
				mainDto.setSmofdepname(mOFDepName);
				mainDto.setSsupdepcode(supDepCode);
				mainDto.setSsupdepname(supDepName);
				mainDto.setSagencycode(agencyCode);
				mainDto.setSagencyname(agencyName);
				mainDto.setSexpfunccode(expFuncCode);
				mainDto.setSexpfuncname(expFuncName);
				mainDto.setSexpfunccode1(expFuncCode1);
				mainDto.setSexpfuncname1(expFuncName1);
				mainDto.setSexpfunccode2(expFuncCode2);
				mainDto.setSexpfuncname2(expFuncName2);
				mainDto.setSexpfunccode3(expFuncCode3);
				mainDto.setSexpfuncname3(expFuncName3);
				mainDto.setSexpecocode(expEcoCode);
				mainDto.setSexpeconame(expEcoName);
				mainDto.setSexpecocode1(expEcoCode1);
				mainDto.setSexpeconame1(expEcoName1);
				mainDto.setSexpecocode2(expEcoCode2);
				mainDto.setSexpeconame2(expEcoName2);
				mainDto.setSdepprocode(depProCode);
				mainDto.setSdepproname(depProName);
				mainDto.setSsetmodecode(setModeCode);
				mainDto.setSsetmodename(setModeName);
				mainDto.setSpaybankcode(payBankCode);
				mainDto.setSpaybankname(payBankName);
				mainDto.setSclearbankcode(clearBankCode);
				mainDto.setSclearbankname(clearBankName);
				mainDto.setSpayeeacctno(payeeAcctNo);
				mainDto.setSpayeeacctname(payeeAcctName);
				mainDto.setSpayeeacctbankname(payeeAcctBankName);
				mainDto.setSpayeeacctbankno(payeeAcctBankNo);
				mainDto.setSpayacctno(payAcctNo);
				mainDto.setSpayacctname(payAcctName);
				mainDto.setSpayacctbankname(payAcctBankName);
				mainDto.setSpaysummarycode(paySummaryCode);
				mainDto.setSpaysummaryname(paySummaryName);
				mainDto.setNpayamt(new BigDecimal(payAmt));
				mainDto.setSpaymgrcode(payMgrCode);
				mainDto.setSpaymgrname(payMgrName);
				mainDto.setSfunddealmodecode(fundDealModeCode);
				mainDto.setSfunddealmodename(fundDealModeName);
				mainDto.setSbusinesstypecode(businessTypeCode);
				mainDto.setSbusinesstypename(businessTypeName);
				mainDto.setStaxbillno(taxBillNo);
				mainDto.setStaxayerid(taxayerID);
				mainDto.setStaxorgcode(taxOrgCode);
				mainDto.setScheckno(checkNo);
				mainDto.setSxpaydate(xPayDate);
				mainDto.setSxagentbusinessno(xAgentBusinessNo);
				mainDto.setSxcheckno(xCheckNo);
				mainDto.setNxpayamt(xPayAmt == null ? null : new BigDecimal(xPayAmt));
				mainDto.setSxpayeeacctbankname(xPayeeAcctBankName);
				mainDto.setSxpayeeacctno(xPayeeAcctNo);
				mainDto.setSxpayeeacctname(xPayeeAcctName);
				mainDto.setShold1(hold1);
				mainDto.setShold2(hold2);

				mainList.add(mainDto);
				// ɾ�����������������+��������+ƾ֤���+֧�����
				delMainList.add(new StringBuilder()//
						.append(admDivCode)//
						.append("#")//
						.append(vtCode)//
						.append("#")//
						.append(voucherNo)//
						.append("#")//
						.append(new BigDecimal(payAmt)).toString());

				List detailList = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");
				for (int x = 0; x < detailList.size(); x++) {
					Element elementDetail = (Element) detailList.get(x);
					String sub_id = elementDetail.elementText("Id");// ֧����ϸId
					String sub_voucherBillId = elementDetail.elementText("VoucherBillId");// ������Ȩ֧��ƾ֤Id
					String sub_voucherBillNo = elementDetail.elementText("VoucherBillNo");// ������Ȩ֧��ƾ֤����
					if (StringUtils.isBlank(sub_voucherBillNo)) {
						result.getErrorList().add(new File(fullFileName).getName() + ": �ļ��д���'����֧����Ȩƾ֤����(��ϸ)'Ϊ�����, ���֤!");
						break out;
					}
					String sub_voucherNo = elementDetail.elementText("VoucherNo");// ֧���������
					String sub_fundTypeCode = elementDetail.elementText("FundTypeCode");// �ʽ����ʱ���
					String sub_fundTypeName = elementDetail.elementText("FundTypeName");// �ʽ���������
					String sub_bgtTypeCode = elementDetail.elementText("BgtTypeCode");// Ԥ�����ͱ���
					String sub_bgtTypeName = elementDetail.elementText("BgtTypeName");// Ԥ����������
					String sub_proCatCode = elementDetail.elementText("ProCatCode");// ��֧�������
					String sub_proCatName = elementDetail.elementText("ProCatName");// ��֧��������
					String sub_payKindCode = elementDetail.elementText("PayKindCode");// ֧�����ͱ���
					String sub_payKindName = elementDetail.elementText("PayKindName");// ֧����������
					String sub_supDepCode = elementDetail.elementText("SupDepCode");// һ��Ԥ�㵥λ����
					String sub_supDepName = elementDetail.elementText("SupDepName");// һ��Ԥ�㵥λ����
					String sub_agencyCode = elementDetail.elementText("AgencyCode");// ����Ԥ�㵥λ����
					String sub_agencyName = elementDetail.elementText("AgencyName");// ����Ԥ�㵥λ����
					String sub_expFuncCode = elementDetail.elementText("ExpFuncCode");// ���ܷ����Ŀ����
					String sub_expFuncName = elementDetail.elementText("ExpFuncName");// ���ܷ����Ŀ����
					String sub_expEcoCode = elementDetail.elementText("ExpEcoCode");// ���÷����Ŀ����
					String sub_expEcoName = elementDetail.elementText("ExpEcoName");// ���÷����Ŀ����
					String sub_depProCode = elementDetail.elementText("DepProCode");// Ԥ����Ŀ����
					String sub_depProName = elementDetail.elementText("DepProName");// Ԥ����Ŀ����
					String sub_payeeAcctNo = elementDetail.elementText("PayeeAcctNo");// �տ����˺�
					String sub_payeeAcctName = elementDetail.elementText("PayeeAcctName");// �տ�������
					String sub_payeeAcctBankName = elementDetail.elementText("PayeeAcctBankName");// �տ�������
					String sub_payeeAcctBankNo = elementDetail.elementText("PayeeAcctBankNo");// �տ��������к�
					String sub_payAmt = elementDetail.elementText("PayAmt");// ֧�����
					String sub_xPayDate = elementDetail.elementText("XPayDate");// ʵ��֧������
					String sub_xAgentBusinessNo = elementDetail.elementText("XAgentBusinessNo");// ���н�����ˮ��
					String sub_xCheckNo = elementDetail.elementText("XCheckNo");// ֧Ʊ��(�����)
					String sub_xPayAmt = elementDetail.elementText("XPayAmt");// ʵ��֧�����
					String sub_xAddWord = elementDetail.elementText("XAddWord");// ����
					String sub_xPayeeAcctBankName = elementDetail.elementText("XPayeeAcctBankName");// �տ�������
					String sub_xPayeeAcctNo = elementDetail.elementText("XPayeeAcctNo");// �տ����˺�
					String sub_xPayeeAcctName = elementDetail.elementText("XPayeeAcctName");// �տ���ȫ��
					String sub_remark = elementDetail.elementText("Remark");// ��ע
					String sub_hold1 = elementDetail.elementText("Hold1");// Ԥ���ֶ�1
					String sub_hold2 = elementDetail.elementText("Hold2");// Ԥ���ֶ�2
					String sub_hold3 = elementDetail.elementText("Hold3");// Ԥ���ֶ�3
					String sub_hold4 = elementDetail.elementText("Hold4");// Ԥ���ֶ�4

					/**
					 * ��װ��ϸ����
					 */
					TfGrantpaymsgsubDto subDto = new TfGrantpaymsgsubDto();
					/*
					 * S_VOUCHERNO֧��������Ų���Ϊ��; S_FUNDTYPECODE�ʽ����ʱ��벻��Ϊ��; S_FUNDTYPENAME�ʽ��������Ʋ���Ϊ��; S_PAYEEACCTNO�տ����˺Ų���Ϊ��; S_PAYEEACCTNAME�տ������Ʋ���Ϊ��; S_PAYEEACCTBANKNAME�տ������в���Ϊ��;
					 */
					subDto.setIvousrlno(Long.valueOf(mainvou));
					subDto.setIseqno((long) (x + 1));
					subDto.setSid(sub_id);
					subDto.setSvoucherbillid(sub_voucherBillId);
					subDto.setSvoucherbillno(sub_voucherBillNo);
					subDto.setSvoucherno(sub_voucherNo == null ? "" : sub_voucherNo);
					subDto.setSfundtypecode(sub_fundTypeCode == null ? "" : sub_fundTypeCode);
					subDto.setSfundtypename(sub_fundTypeName == null ? "" : sub_fundTypeName);
					subDto.setSbgttypecode(sub_bgtTypeCode);
					subDto.setSbgttypename(sub_bgtTypeName);
					subDto.setSprocatcode(sub_proCatCode);
					subDto.setSprocatname(sub_proCatName);
					subDto.setSpaykindcode(sub_payKindCode);
					subDto.setSpaykindname(sub_payKindName);
					subDto.setSsupdepcode(sub_supDepCode);
					subDto.setSsupdepname(sub_supDepName);
					subDto.setSagencycode(sub_agencyCode);
					subDto.setSagencyname(sub_agencyName);
					subDto.setSexpfunccode(sub_expFuncCode);
					subDto.setSexpfuncname(sub_expFuncName);
					subDto.setSexpecocode(sub_expEcoCode);
					subDto.setSexpeconame(sub_expEcoName);
					subDto.setSdepprocode(sub_depProCode);
					subDto.setSdepproname(sub_depProName);
					subDto.setSpayeeacctno(sub_payeeAcctNo == null ? "" : sub_payeeAcctNo);
					subDto.setSpayeeacctname(sub_payeeAcctName == null ? "" : sub_payeeAcctName);
					subDto.setSpayeeacctbankname(sub_payeeAcctBankName == null ? "" : sub_payeeAcctBankName);
					subDto.setSpayeeacctbankno(sub_payeeAcctBankNo);
					subDto.setNpayamt(sub_payAmt == null ? null : new BigDecimal(sub_payAmt));
					subDto.setSxpaydate(sub_xPayDate);
					subDto.setSxagentbusinessno(sub_xAgentBusinessNo);
					subDto.setSxcheckno(sub_xCheckNo);
					subDto.setNxpayamt(sub_xPayAmt == null ? null : new BigDecimal(sub_xPayAmt));
					subDto.setSxpayeeacctbankname(sub_xPayeeAcctBankName);
					subDto.setSxpayeeacctno(sub_xPayeeAcctNo);
					subDto.setSxpayeeacctname(sub_xPayeeAcctName);
					subDto.setSremark(sub_remark);
					subDto.setShold1(sub_hold1);
					subDto.setShold2(sub_hold2);
					subDto.setShold3(sub_hold3);
					subDto.setShold4(sub_hold4);

					subList.add(subDto);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("����8202���ĳ����쳣", e);
		}

		return result;
	}
}

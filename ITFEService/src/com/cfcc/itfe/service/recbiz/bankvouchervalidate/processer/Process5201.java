package com.cfcc.itfe.service.recbiz.bankvouchervalidate.processer;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.service.recbiz.bankvouchervalidate.AbstractBankVoucherValidateProcesser;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * 5201������
 * 
 * @author hua
 * 
 */
public class Process5201 extends AbstractBankVoucherValidateProcesser {
	public MulitTableDto process(String fullFileName) {
		MulitTableDto result = null;
		result = new MulitTableDto();
		result.setFatherDtos(new ArrayList<IDto>());
		result.setSonDtos(new ArrayList<IDto>());
		List<IDto> mainList = result.getFatherDtos();
		List<IDto> subList = result.getSonDtos();
		List<String> delMainList = result.getVoulist();
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
				/**
				 * ƾ֤��Ϣ
				 */
				// String VoucherNo = elementVoucher.elementText("VoucherNo");//ƾ֤��
				String id = elementVoucher.elementText("Id");// ������Ȩ֧��ƾ֤Id
				String admDivCode = elementVoucher.elementText("AdmDivCode");// ������������
				if (StringUtils.isBlank(admDivCode)) {
					result.getErrorList().add(new File(fullFileName).getName() + ": �ļ��д���'������������'Ϊ�����, ���֤!");
					break out;
				}
				String stYear = elementVoucher.elementText("StYear");// ҵ�����
				String vtCode = elementVoucher.elementText("VtCode");// ƾ֤���ͱ��
				if (!MsgConstant.VOUCHER_NO_5201.equals(vtCode)) {
					result.getErrorList().add("��֧�ָ����ͱ��ĵ���!");
					break;
				}
				String VoucherNo = elementVoucher.elementText("VoucherNo");// ƾ֤���
				if (StringUtils.isBlank(VoucherNo)) {
					result.getErrorList().add(new File(fullFileName).getName() + ": �ļ��д���'����ƾ֤���'Ϊ�����, ���֤!");
					break out;
				}
				String VouDate = elementVoucher.elementText("VouDate"); // ƾ֤����
				String FundTypeCode = elementVoucher.elementText("FundTypeCode"); // �ʽ����ʱ���
				String FundTypeName = elementVoucher.elementText("FundTypeName"); // �ʽ���������
				String BgtTypeCode = elementVoucher.elementText("BgtTypeCode");// Ԥ�����ͱ���
				String BgtTypeName = elementVoucher.elementText("BgtTypeName");// Ԥ����������
				String PayTypeCode = elementVoucher.elementText("PayTypeCode"); // ֧����ʽ����
				String PayTypeName = elementVoucher.elementText("PayTypeName"); // ֧����ʽ����
				String ProCatCode = elementVoucher.elementText("ProCatCode");// ��֧�������
				String ProCatName = elementVoucher.elementText("ProCatName");// ��֧��������
				String MOFDepCode = elementVoucher.elementText("MOFDepCode");// ҵ���ұ���
				String MOFDepName = elementVoucher.elementText("MOFDepName");// ҵ��������
				String FileNoCode = elementVoucher.elementText("FileNoCode");// ָ���ĺű���
				String FileNoName = elementVoucher.elementText("FileNoName");// ָ���ĺ�����
				String SupDepCode = elementVoucher.elementText("SupDepCode");// һ��Ԥ�㵥λ����
				String SupDepName = elementVoucher.elementText("SupDepName");// һ��Ԥ�㵥λ����
				String AgencyCode = elementVoucher.elementText("AgencyCode"); // ����Ԥ�㵥λ����
				String AgencyName = elementVoucher.elementText("AgencyName"); // ����Ԥ�㵥λ����
				String ExpFuncCode = elementVoucher.elementText("ExpFuncCode");// ֧�����ܷ����Ŀ����
				String ExpFuncName = elementVoucher.elementText("ExpFuncName");// ֧�����ܷ����Ŀ����
				String ExpFuncCode1 = elementVoucher.elementText("ExpFuncCode1");// ֧�����ܷ����Ŀ����
				String ExpFuncName1 = elementVoucher.elementText("ExpFuncName1");// ֧�����ܷ����Ŀ����
				String ExpFuncCode2 = elementVoucher.elementText("ExpFuncCode2");// ֧�����ܷ����Ŀ����
				String ExpFuncName2 = elementVoucher.elementText("ExpFuncName2");// ֧�����ܷ����Ŀ����
				String ExpFuncCode3 = elementVoucher.elementText("ExpFuncCode3");// ֧�����ܷ����Ŀ����
				String ExpFuncName3 = elementVoucher.elementText("ExpFuncName3");// ֧�����ܷ����Ŀ����
				String ExpEcoCode = elementVoucher.elementText("ExpEcoCode");// ֧�����÷����Ŀ����
				String ExpEcoName = elementVoucher.elementText("ExpEcoName");// ֧�����÷����Ŀ����
				String ExpEcoCode1 = elementVoucher.elementText("ExpEcoCode1");// ֧�����÷����Ŀ����
				String ExpEcoName1 = elementVoucher.elementText("ExpEcoName1");// ֧�����÷����Ŀ����
				String ExpEcoCode2 = elementVoucher.elementText("ExpEcoCode2");// ֧�����÷����Ŀ����
				String ExpEcoName2 = elementVoucher.elementText("ExpEcoName2");// ֧�����÷����Ŀ����
				String DepProCode = elementVoucher.elementText("DepProCode");// Ԥ����Ŀ����
				String DepProName = elementVoucher.elementText("DepProName");// Ԥ����Ŀ����
				String SetModeCode = elementVoucher.elementText("SetModeCode");// ���㷽ʽ����
				String SetModeName = elementVoucher.elementText("SetModeName");// ���㷽ʽ����
				String PayBankCode = elementVoucher.elementText("PayBankCode"); // �������б���
				String PayBankName = elementVoucher.elementText("PayBankName"); // ������������
				String ClearBankCode = elementVoucher.elementText("ClearBankCode");// �������б���
				String ClearBankName = elementVoucher.elementText("ClearBankName");// ������������
				String PayeeAcctNo = elementVoucher.elementText("PayeeAcctNo");// �տ����˺�
				String PayeeAcctName = elementVoucher.elementText("PayeeAcctName");// �տ�������
				String PayeeAcctBankName = elementVoucher.elementText("PayeeAcctBankName");// �տ�������
				String PayeeAcctBankNo = elementVoucher.elementText("PayeeAcctBankNo");// �տ��������к�
				String PayAcctNo = elementVoucher.elementText("PayAcctNo"); // �������˺�
				String PayAcctName = elementVoucher.elementText("PayAcctName"); // ����������
				String PayAcctBankName = elementVoucher.elementText("PayAcctBankName"); // ����������
				String PaySummaryCode = elementVoucher.elementText("PaySummaryCode");// ��;����
				String PaySummaryName = elementVoucher.elementText("PaySummaryName");// ��;����
				String PayAmt = elementVoucher.elementText("PayAmt");// ֧�����
				if (StringUtils.isBlank(PayAmt)) {
					result.getErrorList().add(new File(fullFileName).getName() + ": �ļ��д���'֧�����'Ϊ�����, ���֤!");
					break out;
				}
				String BusinessTypeCode = elementVoucher.elementText("BusinessTypeCode"); // ҵ�����ͱ���
				String BusinessTypeName = elementVoucher.elementText("BusinessTypeName"); // ҵ����������
				String CheckNo = elementVoucher.elementText("CheckNo");// ֧Ʊ�ţ�����ţ�
				String XPayDate = elementVoucher.elementText("XPayDate"); // ʵ��֧������
				String XAgentBusinessNo = elementVoucher.elementText("XAgentBusinessNo");// ���н�����ˮ��
				String XCheckNo = elementVoucher.elementText("XCheckNo");// ֧Ʊ��(�����)
				String XPayAmt = elementVoucher.elementText("XPayAmt");// ʵ��֧�����
				String XPayeeAcctBankName = elementVoucher.elementText("XPayeeAcctBankName");// �տ�������
				String XPayeeAcctNo = elementVoucher.elementText("XPayeeAcctNo");// �տ����˺�
				String XPayeeAcctName = elementVoucher.elementText("XPayeeAcctName"); // �տ���ȫ��
				String Hold1 = elementVoucher.elementText("Hold1");// Ԥ���ֶ�1
				String Hold2 = elementVoucher.elementText("Hold2");// Ԥ���ֶ�2

				/**
				 * ��װTfPaymentDetailsmainDto����
				 **/
				TfDirectpaymsgmainDto maindto = new TfDirectpaymsgmainDto();
				String mainvou = generateVousrlno();
				maindto.setIvousrlno(Long.parseLong(mainvou));
				maindto.setSvoudate(VouDate);
				maindto.setSvtcode(vtCode);
				maindto.setSfundtypecode(FundTypeCode);
				maindto.setSfundtypename(FundTypeName);
				maindto.setSbgttypecode(BgtTypeCode);
				maindto.setSbgttypename(BgtTypeName);
				maindto.setSpaytypename(PayTypeName);
				maindto.setSprocatcode(ProCatCode);
				maindto.setSprocatname(ProCatName);
				maindto.setSmofdepcode(MOFDepCode);
				maindto.setSmofdepname(MOFDepName);
				maindto.setSfilenocode(FileNoCode);
				maindto.setSfilenoname(FileNoName);
				maindto.setSsupdepcode(SupDepCode);
				maindto.setSsupdepname(SupDepName);
				maindto.setSagencycode(AgencyCode);
				maindto.setSagencyname(AgencyName);
				maindto.setSexpfunccode(ExpFuncCode);
				maindto.setSexpfuncname(ExpFuncName);
				maindto.setSexpfunccode1(ExpFuncCode1);
				maindto.setSexpfuncname1(ExpFuncName1);
				maindto.setSexpfunccode2(ExpFuncCode2);
				maindto.setSexpfuncname2(ExpFuncName2);
				maindto.setSexpfunccode3(ExpFuncCode3);
				maindto.setSexpfuncname3(ExpFuncName3);
				maindto.setSexpecocode(ExpEcoCode);
				maindto.setSexpeconame(ExpEcoName);
				maindto.setSexpecocode1(ExpEcoCode1);
				maindto.setSexpeconame1(ExpEcoName1);
				maindto.setSexpecocode2(ExpEcoCode2);
				maindto.setSexpeconame2(ExpEcoName2);
				maindto.setSdepprocode(DepProCode);
				maindto.setSdepproname(DepProName);
				maindto.setSsetmodecode(SetModeCode);
				maindto.setSsetmodename(SetModeName);
				maindto.setSpaybankcode(PayBankCode);
				maindto.setSpaybankname(PayBankName);
				maindto.setSclearbankcode(ClearBankCode);
				maindto.setSclearbankname(ClearBankName);
				maindto.setSpayeeacctno(PayeeAcctNo);
				maindto.setSpayeeacctname(PayeeAcctName);
				maindto.setSpayeeacctbankname(PayeeAcctBankName);
				maindto.setSpayeeacctbankno(PayeeAcctBankNo);
				maindto.setSpayacctno(PayAcctNo);
				maindto.setSpayacctname(PayAcctName);
				maindto.setSpayacctbankname(PayAcctBankName);
				maindto.setSpaysummarycode(PaySummaryCode);
				maindto.setSpaysummaryname(PaySummaryName);
				maindto.setNpayamt(BigDecimal.valueOf(Double.valueOf(PayAmt)));
				maindto.setSbusinesstypecode(BusinessTypeCode);
				maindto.setSbusinesstypename(BusinessTypeName);
				maindto.setScheckno(CheckNo);
				maindto.setSxpaydate(XPayDate);
				maindto.setSxagentbusinessno(XAgentBusinessNo);
				maindto.setSxcheckno(XCheckNo);
				maindto.setNxpayamt(null);
				maindto.setSxpayeeacctbankname(XPayeeAcctBankName);
				maindto.setSxpayeeacctno(XPayeeAcctNo);
				maindto.setSxpayeeacctname(XPayeeAcctName);
				maindto.setShold1(Hold1);
				maindto.setShold2(Hold2);
				if (PayTypeCode != null && "11".equals(PayTypeCode)) {
					maindto.setSpaytypecode("0");// ֱ��֧��
				} else {
					maindto.setSpaytypecode(PayTypeCode);
				}
				maindto.setSid(id);
				maindto.setSvoucherno(VoucherNo);
				maindto.setSstyear(stYear);// ҵ�����
				maindto.setSfinorgcode("");// ������������
				maindto.setSdealno(mainvou.substring(8, 16));// ������ˮ��
				maindto.setScommitdate(TimeFacade.getCurrentStringTime());// ί������
				maindto.setSfilename(fullFileName);// �ļ���
				maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);// ״̬
				maindto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));// ϵͳʱ��
				maindto.setSorgcode(getLoginfo().getSorgcode());
				maindto.setStrecode("");
				maindto.setSadmdivcode(admDivCode);
				maindto.setSdemo("");
				maindto.setSbackflag("0");// �˻ر�־ 0-δ�˻� 1-���˻�

				mainList.add(maindto);
				// ɾ�����������������+��������+ƾ֤���+֧�����
				delMainList.add(new StringBuilder()//
						.append(admDivCode)//
						.append("#")//
						.append(vtCode)//
						.append("#")//
						.append(VoucherNo)//
						.append("#")//
						.append(new BigDecimal(PayAmt)).toString());

				List detailList = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");

				/**
				 * ��װTfDirectpaymsgsubDto����
				 */
				for (int x = 0; x < detailList.size(); x++) {
					Element elementDetail = (Element) detailList.get(x);
					String sdetailId = elementDetail.elementText("Id");// ��ϸ���
					String VoucherBillId = elementDetail.elementText("VoucherBillId");// ����ֱ��֧��ƾ֤Id
					String VoucherBillNo = elementDetail.elementText("VoucherBillNo");// ����ֱ��֧��ƾ֤����
					if (StringUtils.isBlank(VoucherBillNo)) {
						result.getErrorList().add(new File(fullFileName).getName() + ": �ļ��д���'����֧����Ȩƾ֤����(��ϸ)'Ϊ�����, ���֤!");
						break out;
					}
					String sdetailVoucherNo = elementDetail.elementText("VoucherNo");// ֧���������
					String sdetailFundTypeCode = elementDetail.elementText("FundTypeCode");// �ʽ����ʱ���
					String sdetailFundTypeName = elementDetail.elementText("FundTypeName");// �ʽ���������
					String sdetailBgtTypeCode = elementDetail.elementText("BgtTypeCode");// Ԥ�����ͱ���
					String sdetailBgtTypeName = elementDetail.elementText("BgtTypeName");// Ԥ����������
					String sdetailProCatCode = elementDetail.elementText("ProCatCode");// ��֧�������
					String sdetailProCatName = elementDetail.elementText("ProCatName");// ��֧��������
					String sdetailPayKindCode = elementDetail.elementText("PayKindCode");// ֧�����ͱ���
					String sdetailPayKindName = elementDetail.elementText("PayKindName");// ֧����������
					String sdetailMOFDepCode = elementDetail.elementText("MOFDepCode");// ҵ���ұ���
					String sdetailMOFDepName = elementDetail.elementText("MOFDepName");// ҵ��������
					String sdetailFileNoCode = elementDetail.elementText("FileNoCode");// ָ���ĺű���
					String sdetailFileNoName = elementDetail.elementText("FileNoName");// ָ���ĺ�����
					String sdetailSupDepCode = elementDetail.elementText("SupDepCode");// һ��Ԥ�㵥λ����
					String sdetailSupDepName = elementDetail.elementText("SupDepName");// һ��Ԥ�㵥λ����
					String sdetailAgencyCode = elementDetail.elementText("AgencyCode");// ����Ԥ�㵥λ����
					String sdetailAgencyName = elementDetail.elementText("AgencyName");// ����Ԥ�㵥λ����
					String sdetailExpFuncCode = elementDetail.elementText("ExpFuncCode");// ֧�����ܷ����Ŀ����
					String sdetailExpFuncName = elementDetail.elementText("ExpFuncName");// ֧�����ܷ����Ŀ����
					String sdetailExpEcoCode = elementDetail.elementText("ExpEcoCode");// ֧�����÷����Ŀ����
					String sdetailExpEcoName = elementDetail.elementText("ExpEcoName");// ֧�����÷����Ŀ����
					String sdetailDepProCode = elementDetail.elementText("DepProCode");// Ԥ����Ŀ����
					String sdetailDepProName = elementDetail.elementText("DepProName");// Ԥ����Ŀ����
					String sdetailPayeeAcctNo = elementDetail.elementText("PayeeAcctNo");// �տ����˺�
					String sdetailPayeeAcctName = elementDetail.elementText("PayeeAcctName");// �տ�������
					String sdetailPayeeAcctBankName = elementDetail.elementText("PayeeAcctBankName");// �տ�������
					String sdetailPayeeAcctBankNo = elementDetail.elementText("PayeeAcctBankNo");// �տ��������к�
					String sdetailPayAmt = elementDetail.elementText("PayAmt");// ֧�����
					String sdetailRemark = elementDetail.elementText("Remark");// ��ע
					String sdetailDetailXPayDate = elementDetail.elementText("XPayDate");// ʵ��֧������
					String sdetailXAgentBusinessNo = elementDetail.elementText("XAgentBusinessNo");// ���н�����ˮ��
					String sdetailXCheckNo = elementDetail.elementText("XCheckNo");// ֧Ʊ��(�����)
					String sdetailXPayAmt = elementDetail.elementText("XPayAmt");// ʵ��֧�����
					String sdetailXPayeeAcctBankName = elementDetail.elementText("XPayeeAcctBankName");// �տ�������
					String sdetailXPayeeAcctNo = elementDetail.elementText("XPayeeAcctNo");// �տ����˺�
					String sdetailXPayeeAcctName = elementDetail.elementText("XPayeeAcctName");// �տ���ȫ��
					String XAddWord = elementDetail.elementText("XAddWord");// ʧ��ԭ��
					String sdetailHold1 = elementDetail.elementText("Hold1");// Ԥ���ֶ�1
					String sdetailHold2 = elementDetail.elementText("Hold2");// Ԥ���ֶ�2
					String sdetailHold3 = elementDetail.elementText("Hold3");// Ԥ���ֶ�3
					String sdetailHold4 = elementDetail.elementText("Hold4");// Ԥ���ֶ�4

					TfDirectpaymsgsubDto subdto = new TfDirectpaymsgsubDto();
					subdto.setIvousrlno(Long.parseLong(mainvou));
					subdto.setIseqno((long) (x + 1));
					subdto.setSid(sdetailId);
					subdto.setSvoucherbillid(VoucherBillId);
					subdto.setSvoucherbillno(VoucherBillNo);
					// subdto.setSvoucherno(sdetailVoucherNo);
					subdto.setSvoucherno(VoucherNo);
					subdto.setSfundtypecode(FundTypeCode == null ? "" : FundTypeCode);
					subdto.setSfundtypename(FundTypeName == null ? "" : FundTypeName);
					subdto.setSbgttypecode(sdetailBgtTypeCode);
					subdto.setSbgttypename(sdetailBgtTypeName);
					subdto.setSprocatcode(sdetailProCatCode);
					subdto.setSprocatname(sdetailProCatName);
					subdto.setSpaykindcode(sdetailPayKindCode);
					subdto.setSpaykindname(sdetailPayKindName);
					subdto.setSmofdepcode(sdetailMOFDepCode);
					subdto.setSmofdepname(sdetailMOFDepName);
					subdto.setSfilenocode(sdetailFileNoCode);
					subdto.setSsupdepcode(sdetailFileNoName);
					subdto.setSsupdepname(sdetailSupDepCode);
					subdto.setSagencycode(sdetailAgencyCode);
					subdto.setSagencyname(sdetailAgencyName);
					subdto.setSexpfunccode(sdetailExpFuncCode);
					subdto.setSexpfuncname(sdetailExpFuncName);
					subdto.setSexpecocode((StringUtils.isNotBlank(sdetailExpEcoCode) && sdetailExpEcoCode.getBytes().length <= 30) ? sdetailExpEcoCode : "");// ���ÿ�Ŀ����ֻȡС��30λ��ֵ
					subdto.setSexpeconame(sdetailExpEcoName);
					subdto.setSdepprocode(sdetailDepProCode);
					subdto.setSdepproname(sdetailDepProName);
					subdto.setSpayeeacctno(sdetailPayeeAcctNo);
					subdto.setSpayeeacctname(sdetailPayeeAcctName);
					subdto.setSpayeeacctbankname(sdetailPayeeAcctBankName);
					subdto.setSpayeeacctbankno(sdetailPayeeAcctBankNo);
					subdto.setNpayamt(BigDecimal.valueOf(Double.valueOf(sdetailPayAmt)));
					subdto.setSremark(sdetailRemark);
					subdto.setSxpaydate(sdetailDetailXPayDate);
					subdto.setSxagentbusinessno(sdetailXAgentBusinessNo);
					subdto.setSxcheckno(sdetailXCheckNo);
					subdto.setNxpayamt(null);
					subdto.setSxpayeeacctbankname(sdetailXPayeeAcctBankName);
					subdto.setSxpayeeacctno(sdetailXPayeeAcctNo);
					subdto.setSxpayeeacctname(sdetailXPayeeAcctName);
					subdto.setSxaddword(XAddWord);
					subdto.setShold1(sdetailHold1);
					subdto.setShold2(sdetailHold2);
					subdto.setShold3(sdetailHold3);
					subdto.setShold4(sdetailHold4);

					subList.add(subdto);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("����5201���ĳ����쳣", e);
		}
		return result;
	}
}

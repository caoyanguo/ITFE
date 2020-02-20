package com.cfcc.itfe.service.recbiz.bankvouchervalidate.processer;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.service.recbiz.bankvouchervalidate.AbstractBankVoucherValidateProcesser;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * 2301���Ľ�����(Ϊ�˲��Է���д��, ʵ�ʵ��벻�����˱���)
 * 
 * @author hua
 * 
 */
public class Process2301 extends AbstractBankVoucherValidateProcesser {

	public MulitTableDto process(String fullFileName) {
		MulitTableDto result = null;

		result = new MulitTableDto();
		result.setFatherDtos(new ArrayList<IDto>());
		result.setSonDtos(new ArrayList<IDto>());
		List<IDto> mainList = result.getFatherDtos();
		List<IDto> subList = result.getSonDtos();
		try {
			Document doc = parseFile2Doc(fullFileName);
			if (doc == null) {
				result.getErrorList().add("�޷��������ļ�, ���֤!.");
				return result;
			}
			List voucherList = doc.selectNodes("Voucher");
			if (voucherList == null || voucherList.size() == 0) {
				result.getErrorList().add("�޷�ʶ��ı�������, �Ҳ���<Voucher/>�ڵ�!");
				return result;
			}

			for (Object voucherTemp : voucherList) {
				Element elementVoucher = (Element) voucherTemp;

				// Element elementVoucher = (Element) element.selectSingleNode("Voucher");
				/**
				 * ƾ֤��Ϣ
				 */
				// String Attach = ((Element) VoucherBodyList.get(i)).elementText("Attach");// ������Ϣ
				String admDivCode = elementVoucher.elementText("AdmDivCode");// ������������
				String stYear = elementVoucher.elementText("StYear");// ҵ�����
				String vtCode = elementVoucher.elementText("VtCode");// ƾ֤���ͱ��
				String Id = elementVoucher.elementText("Id");// ���뻮��ƾ֤Id
				String VouDate = elementVoucher.elementText("VouDate"); // ƾ֤����
				String VoucherNo = elementVoucher.elementText("VoucherNo");// ƾ֤��
				String TreCode = elementVoucher.elementText("TreCode"); // �����������
				String FinOrgCode = elementVoucher.elementText("FinOrgCode");// �������ش���
				String BgtTypeCode = elementVoucher.elementText("BgtTypeCode");// Ԥ�����ͱ���
				String BgtTypeName = elementVoucher.elementText("BgtTypeName");// Ԥ����������
				String FundTypeCode = elementVoucher.elementText("FundTypeCode");// �ʽ����ʱ���
				String FundTypeName = elementVoucher.elementText("FundTypeName");// �ʽ���������
				String PayTypeCode = elementVoucher.elementText("PayTypeCode");// ֧����ʽ����
				String PayTypeName = elementVoucher.elementText("PayTypeName");// ֧����ʽ����
				String AgentAcctNo = elementVoucher.elementText("AgentAcctNo");// �տ������˺�
				String AgentAcctName = elementVoucher.elementText("AgentAcctName");// �տ������˻�����
				String AgentAcctBankName = elementVoucher.elementText("AgentAcctBankName");// �տ�����
				String ClearAcctNo = elementVoucher.elementText("ClearAcctNo");// �����˺�
				String ClearAcctName = elementVoucher.elementText("ClearAcctName");// �����˻�����
				String ClearAcctBankName = elementVoucher.elementText("ClearAcctBankName");// ��������
				String PayAmt = elementVoucher.elementText("PayAmt");// ����������
				String PayBankName = elementVoucher.elementText("PayBankName");// ������������
				String PayBankNo = elementVoucher.elementText("PayBankNo");// ���������к�
				String Remark = elementVoucher.elementText("Remark");// ժҪ
				String MoneyCorpCode = elementVoucher.elementText("MoneyCorpCode");// ���ڻ�������
				String XPaySndBnkNo = elementVoucher.elementText("XPaySndBnkNo");// ֧���������к�
				String XAddWord = elementVoucher.elementText("XAddWord");// ����
				String XClearDate = elementVoucher.elementText("XClearDate");// ��������
				String XPayAmt = elementVoucher.elementText("XPayAmt");// ����������
				String Hold1 = elementVoucher.elementText("Hold1");// Ԥ���ֶ�1
				String Hold2 = elementVoucher.elementText("Hold2");// Ԥ���ֶ�2

				/**
				 * ��װTvPayreckBankDto����
				 **/
				TvPayreckBankDto maindto = new TvPayreckBankDto();
				String mainvou = generateVousrlno();
				maindto.setStrano(mainvou.substring(8, 16));// ���뻮��ƾ֤Id
				maindto.setIvousrlno(Long.valueOf(mainvou));// ƾ֤��ˮ��
				maindto.setSid(Id);// ���뻮��ƾ֤Id
				maindto.setSadmdivcode(admDivCode);// ������������
				maindto.setSofyear(stYear);// ҵ�����
				maindto.setSvtcode(vtCode);// ƾ֤���ͱ��\
				SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
				maindto.setSbookorgcode(getLoginfo().getSorgcode());// �����������
				maindto.setDvoudate(CommonUtil.strToDate(VouDate)); // ƾ֤����
				maindto.setSvouno(VoucherNo);// ƾ֤��
				maindto.setStrecode(TreCode); // �����������
				maindto.setSfinorgcode(FinOrgCode);// �������ش���
				maindto.setSbgttypecode(BgtTypeCode);// Ԥ�����ͱ���
				maindto.setSbgttypename(BgtTypeName);// Ԥ����������
				maindto.setSfundtypecode(FundTypeCode);// �ʽ����ʱ���
				maindto.setSfundtypename(FundTypeName);// �ʽ���������
				maindto.setSpaytypecode(PayTypeCode);// ֧����ʽ����
				if ("12".equals(PayTypeCode) || PayTypeCode.startsWith("001002")) {// �����ձ���õ���6λ
					maindto.setSpaymode("1");// ��Ȩ֧��
				} else if ("11".equals(PayTypeCode) || PayTypeCode.startsWith("001001")) {// �����ձ���õ���6λ
					maindto.setSpaymode("0");// ֧����ʽ���� ֱ��֧��
				}
				maindto.setSpaytypename(PayTypeName);// ֧����ʽ����
				maindto.setSpayeeacct(AgentAcctNo);// �տ������˺�
				maindto.setSpayeename(AgentAcctName);// �տ������˻�����
				maindto.setSagentacctbankname(AgentAcctBankName);// �տ�����
				maindto.setSpayeeaddr("");// �տ��˵�ַ
				maindto.setSpayeraddr("");// �����˵�ַ
				maindto.setSpayeracct(ClearAcctNo);// �����˺�
				maindto.setSpayername(ClearAcctName);// �����˻�����
				maindto.setSclearacctbankname(ClearAcctBankName);// ��������
				maindto.setFamt(BigDecimal.valueOf(Double.valueOf(PayAmt)));// ����������
				maindto.setSpaybankname(PayBankName);// ������������
				maindto.setSagentbnkcode(PayBankNo);// ���������к�
				maindto.setSdescription(Remark);// ժҪ
				maindto.setSmoneycorpcode(MoneyCorpCode);// ���ڻ�������
				maindto.setSxpaysndbnkno(XPaySndBnkNo);// ֧���������к�
				maindto.setSaddword("this is addword");// ժҪ��������
				maindto.setSxcleardate(CommonUtil.strToDate(XClearDate));// ��������
				if (XPayAmt != null && !XPayAmt.equals("")) {
					maindto.setSxpayamt(BigDecimal.valueOf(Double.valueOf(XPayAmt)));// ����������
				} else {
					maindto.setSxpayamt(BigDecimal.valueOf(Double.valueOf("0.00")));// ����������
				}
				if ("1".equals(ITFECommonConstant.ISCHECKPAYPLAN)) {
					maindto.setSxpayamt(BigDecimal.valueOf(Double.valueOf(PayAmt)));
				}
				maindto.setShold1(Hold1);// Ԥ���ֶ�1
				maindto.setShold2(Hold2);// Ԥ���ֶ�2
				maindto.setDentrustdate(DateUtil.currentDate());// ί������
				maindto.setSpackno("0000");// ����ˮ��
				maindto.setStrimsign(MsgConstant.TIME_FLAG_NORMAL);// �����ڱ�־
				if ("000073100012".equals(ITFECommonConstant.SRC_NODE)) {// �����ʽ�������ΪԤ������
					maindto.setSbudgettype(FundTypeCode);// Ԥ������(Ĭ��Ԥ����)
				} else {
					maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);// Ԥ������(Ĭ��Ԥ����)
				}
				maindto.setSpayeeopbkno(PayBankNo);// �տ��˿������к�
				maindto.setSfilename(fullFileName);
				maindto.setSresult(DealCodeConstants.DEALCODE_ITFE_DEALING);// ״̬ ������
				maindto.setTsupdate(new Timestamp(new java.util.Date().getTime()));// ϵͳʱ��

				mainList.add(maindto);

				List detailList = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");
				for (int x = 0; x < detailList.size(); x++) {
					Element elementDetail = (Element) detailList.get(x);

					/**
					 * У�鱨����ϸId�ڵ��Ƿ���� 1�����ڵ㲻���ڣ����ϰ汾����������ϸId 2�����ڵ���ڣ����°汾��������ϸId
					 */
					Element sdetailIdElement = elementDetail.element("Id");
					// �ڵ㲻����
					/*
					 * if(sdetailIdElement==null) subDtoIdList.add("�ڵ㲻����"); else{ sdetailId = sdetailIdElement.getText();//��ϸId if(StringUtils.isBlank(sdetailId)){ errDetailMsg="��ϸID�ֶβ���Ϊ�գ�"; break;
					 * } }
					 */
					String VoucherNol = elementDetail.elementText("VoucherNo");// ֧��ƾ֤����
					String SupDepCode = elementDetail.elementText("SupDepCode");// һ��Ԥ�㵥λ����
					String SupDepName = elementDetail.elementText("SupDepName");// һ��Ԥ�㵥λ����
					String ExpFuncCode = elementDetail.elementText("ExpFuncCode");// ֧�����ܷ����Ŀ����
					String ExpFuncName = elementDetail.elementText("ExpFuncName");// ֧�����ܷ����Ŀ����
					String sPayAmt = elementDetail.elementText("PayAmt");// ֧�����
					String PaySummaryName = elementDetail.elementText("PaySummaryName");// ժҪ����
					String sdetailHold1 = elementDetail.elementText("Hold1");// Ԥ���ֶ�1
					String sdetailHold2 = elementDetail.elementText("Hold2");// Ԥ���ֶ�2
					String sdetailHold3 = elementDetail.elementText("Hold3");// Ԥ���ֶ�3
					String sdetailHold4 = elementDetail.elementText("Hold4");// Ԥ���ֶ�4

					String PayDate = elementDetail.elementText("PayDate");// Ԥ���ֶ�4

					TvPayreckBankListDto subdto = new TvPayreckBankListDto();
					// �˴�����ֵ��ȷ��
					subdto.setIseqno(x + 1);// ���
					subdto.setSacctprop(MsgConstant.ACCT_PROP_ZERO);// �˻�����
					subdto.setIvousrlno(Long.valueOf(mainvou));// �ӱ����к�
					subdto.setSvouchern0(VoucherNol);// �ӱ���ϸ���
					// ֧��ƾ֤����
					subdto.setSbdgorgcode(SupDepCode);// һ��Ԥ�㵥λ����
					subdto.setSsupdepname(SupDepName);// һ��Ԥ�㵥λ����
					subdto.setSfuncbdgsbtcode(ExpFuncCode);// ֧�����ܷ����Ŀ����
					subdto.setSexpfuncname(ExpFuncName);// ֧�����ܷ����Ŀ����
					subdto.setSecnomicsubjectcode("");// ���ÿ�Ŀ����
					subdto.setFamt(BigDecimal.valueOf(Double.valueOf(sPayAmt)));// ֧�����
					subdto.setSpaysummaryname(PaySummaryName);// ժҪ����
					subdto.setShold1(sdetailHold1);// Ԥ���ֶ�1
					subdto.setShold2(sdetailHold2);// Ԥ���ֶ�2
					subdto.setShold3(sdetailHold3);// Ԥ���ֶ�3
					subdto.setShold4(sdetailHold4);// Ԥ���ֶ�4
					subdto.setTsupdate(new Timestamp(new java.util.Date().getTime()));// ����ʱ��
					subdto.setSid("");

					subList.add(subdto);
				}
				
				maindto.setIstatinfnum(subList.size());
			}
		} catch (Exception e) {
			throw new RuntimeException("����2301���ĳ����쳣", e);
		}
		return result;
	}

}

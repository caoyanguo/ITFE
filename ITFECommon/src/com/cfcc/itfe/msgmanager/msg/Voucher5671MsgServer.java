package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvNontaxmainDto;
import com.cfcc.itfe.persistence.dto.TvNontaxsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.voucher.service.VoucherVerify;

public class Voucher5671MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher5671MsgServer.class);
	private Voucher voucher;

	/**
	 * �����з������еķ�˰ƾ֤5671������ֻ����ҵ������
	 */
	@SuppressWarnings( { "static-access", "unchecked" })
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();
		String voucherXml = (String) muleMessage.getPayload();
		String ls_FileName = (String) muleMessage.getProperty("fileName");
		String ls_OrgCode = (String) muleMessage.getProperty("orgCode");
		HashMap<String, String> dealnos = (HashMap<String, String>) muleMessage
				.getProperty("dealnos");
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("������˰����ɿⵥ5671���ĳ��ִ���", e);
			throw new ITFEBizException("������˰����ɿⵥ5671���ĳ��ִ���", e);

		}
		List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes(
				"VoucherBody");
		String AdmDivCode = "";// ������������
		String StYear = "";// ���
		String VtCode = "";// ƾ֤����

		String currentDate = TimeFacade.getCurrentStringTime();// ��ǰϵͳ����
		List<String> voucherList = new ArrayList<String>();

		// ��ȡ�����������롢��Ⱥ�ƾ֤����
		if (VoucherBodyList.size() > 0) {
			// VoucherBody
			Element element = (Element) VoucherBodyList.get(0);
			AdmDivCode = element.attribute("AdmDivCode").getText();
			StYear = element.attribute("StYear").getText();
			VtCode = element.attribute("VtCode").getText();
		}

		// Ԥ���Ŀ����list
		ArrayList<String> expFuncCodeList = null;

		// String sAgencyCode ="";
		TvNontaxmainDto maindto = null;
		List mainDtoList = null;
		ArrayList<TvNontaxsubDto> subDtoList = null;
		List lists = new ArrayList();
		List<Object> list = null;
		// ƾ֤���к�
		String mainvou = "";
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		// ��������
		for (int i = 0; i < VoucherBodyList.size(); i++) {
			try {
				// ��ϸ�ϼƽ��
				BigDecimal sumAmt = new BigDecimal("0.00");
				// VoucherBody
				Element element = (Element) VoucherBodyList.get(i);
				// ����ͷ�뱨����ƾ֤��Ų�һ�µ���ǩ���쳣�����ģ�
				String VoucherNo = element.attribute("VoucherNo").getText();// ƾ֤���
				// Voucher
				Element elementVoucher = (Element) element
						.selectSingleNode("Voucher");
				// ��ϸ��ϢList
				List listDetail = elementVoucher.selectSingleNode("DetailList")
						.selectNodes("Detail");
				// ����У����Ϣdto
				VoucherVerifyDto verifydto = new VoucherVerifyDto();
				VoucherVerify voucherVerify = new VoucherVerify();
				/**
				 * ƾ֤��Ϣ
				 */
				String Id = elementVoucher.elementText("Id");// ������Ȩ֧������������֪ͨ��Id
				// String VoucherNo =
				// elementVoucher.elementText("VoucherNo");//ƾ֤��
				String VouDate = elementVoucher.elementText("VouDate"); // ƾ֤����
				String ExpDate = elementVoucher.elementText("ExpDate"); // �ɿ�����

				String FinOrgCode = elementVoucher.elementText("FinOrgCode");// �������ش���
				String FinOrgName = elementVoucher.elementText("FinOrgName");// ������������
				String TreCode = elementVoucher.elementText("TreCode"); // �����������

				String PayeeAcctNo = elementVoucher.elementText("PayeeAcctNo"); // �տ����˺�
				String PayeeAcctName = elementVoucher
						.elementText("PayeeAcctName"); // �տ�������
				String PayeeAcctBankName = elementVoucher
						.elementText("PayeeAcctBankName"); // �տ��˿�����
				String BudgetLevelCode = elementVoucher
						.elementText("BudgetLevelCode"); // Ԥ�㼶�α���
				String BudgetLevelName = elementVoucher
						.elementText("BudgetLevelName"); // Ԥ�㼶������
				String PayAcctName = elementVoucher.elementText("PayAcctName"); // ����������
				String PayAcctNo = elementVoucher.elementText("PayAcctNo"); // �������˺�
				String PayAcctBankName = elementVoucher
						.elementText("PayAcctBankName"); // �����˿�����
				String PayBankNo = elementVoucher.elementText("XPayBankNo"); // �������к�
				String TraNoM = elementVoucher.elementText("XTraNo"); // �ʽ�����ˮ��
				String OrginVouNo = elementVoucher.elementText("OrginVouNo"); // �ɿ�֪ͨƾ֤��
				String XAddWord = elementVoucher.elementText("XAddWord"); // ����
				String XTraDate = elementVoucher.elementText("XTraDate"); // ʵ��֧������
				String XTraAmt = elementVoucher.elementText("XTraAmt"); // �����
				String Hold1 = elementVoucher.elementText("Hold1"); // Ԥ���ֶ�1
				String Hold2 = elementVoucher.elementText("Hold2"); // Ԥ���ֶ�2
				String Hold3 = elementVoucher.elementText("Hold3"); // Ԥ���ֶ�3
				String Hold4 = elementVoucher.elementText("Hold4"); // Ԥ���ֶ�4
				String Remark = elementVoucher.elementText("Remark"); // ��ע
				String TotalCount = elementVoucher.elementText("TotalCount"); // �ܱ���
				String TraAmt = elementVoucher.elementText("TraAmt"); // �ɿ���
				String TaxOrgCode = elementVoucher.elementText("TaxOrgCode"); // ���ջ��ش���
				String TaxOrgName = elementVoucher.elementText("TaxOrgName"); // ���ջ�������
				/**
				 * ��װTvGrantpaymsgmainDto����
				 **/
				maindto = new TvNontaxmainDto();
				maindto.setScommitdate(TimeFacade.getCurrentStringTime());
				mainvou = dealnos.get(VoucherNo).toString();
				maindto.setSorgcode(ls_OrgCode);//�����������
				maindto.setSdealno(mainvou);// ��ˮ��
				maindto.setSid(Id);// �ɿⵥId
				maindto.setSvoudate(VouDate); // ƾ֤����
				maindto.setSvoucherno(VoucherNo);
				maindto.setSexpdate(ExpDate); // �ɿ�����
				maindto.setSfinorgcode(FinOrgCode);// �������ش���
				maindto.setSfingorgname(FinOrgName);// ������������
				maindto.setStrecode(TreCode); // �����������
				maindto.setSpayeeacctno(PayeeAcctNo); // �տ����˺�
				maindto.setSpayeeacctnane(PayeeAcctName); // �տ�������
				maindto.setSpayeeacctbankname(PayeeAcctBankName); // �տ��˿�����
				maindto.setSbudgetlevelcode(BudgetLevelCode); // Ԥ�㼶�α���
				maindto.setSbudgetlevelname(BudgetLevelName); // Ԥ�㼶������
				maindto.setSpayacctname(PayAcctName); // ����������
				maindto.setSpayacctno(PayAcctNo); // �������˺�
				maindto.setSpayacctbankname(PayAcctBankName); // �����˿�����
				maindto.setSpaybankno(PayBankNo); // �������к�
				maindto.setStranom(TraNoM); // �ʽ�����ˮ��
				maindto.setSorginvouno(OrginVouNo); // �ɿ�֪ͨƾ֤��
				maindto.setSxaddword(XAddWord); // ����
				maindto.setSxtradate(XTraDate); // ʵ��֧������
				maindto.setStaxorgcode(TaxOrgCode);// ���ջ��ش���
				maindto.setStaxorgname(TaxOrgName);// ���ջ�������
				if(XTraAmt==null || XTraAmt.equals(""))
					XTraAmt = "0.00";
				maindto.setNxmoney(BigDecimal.valueOf(Double.valueOf(XTraAmt))); // �����
				maindto.setShold1(Hold1); // Ԥ���ֶ�1
				maindto.setShold2(Hold2); // Ԥ���ֶ�2
				maindto.setShold3(Hold3); // Ԥ���ֶ�3
				maindto.setShold4(Hold4); // Ԥ���ֶ�4
				maindto.setScount(TotalCount); // �ܱ���
				maindto.setSdemo(Remark);// ��ע
				maindto.setNmoney(BigDecimal.valueOf(Double.valueOf(TraAmt))); // �ɿ���
				maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);
				maindto.setStrimflag(StateConstant.TRIMSIGN_FLAG_NORMAL);
				maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
				// Ԥ���Ŀ����list
				expFuncCodeList = new ArrayList<String>();
				/**
				 * ��װTvPayoutmsgsubDto����
				 */
				subDtoList = new ArrayList<TvNontaxsubDto>();
				for (int j = 0; j < listDetail.size(); j++) {

					Element elementDetail = (Element) listDetail.get(j);
					Element sdetailIdElement = elementDetail.element("Id");
					String SubId = "";
					// �ڵ㲻����
					if (sdetailIdElement != null)
						SubId = elementDetail.elementText("Id");// ��ϸID

					String TroNo = elementDetail.elementText("TroNo");// ������ˮ��
					String VoucherNosub = elementDetail.elementText("VoucherNo");// ƾ֤��
					
					String TraAmtsub = elementDetail
							.elementText("TraAmt");// �ɿ���
					String FundTypecode = elementDetail
							.elementText("FundTypecode");// �ʽ����ʱ���
					String PayCode = elementDetail
							.elementText("PayCode");// �ɿ���
					String BudgetSubject = elementDetail
							.elementText("BudgetSubject");// Ԥ���Ŀ����
					String BudgetName = elementDetail
							.elementText("BudgetName");// Ԥ���Ŀ����
					String ItemCode = elementDetail
							.elementText("ItemCode");// ������Ŀ����
					String ItemName = elementDetail
							.elementText("ItemName");// ������Ŀ����
					String DetailAmt = elementDetail
							.elementText("DetailAmt");// ��ϸ���
					
					String sdetailHold1 = elementDetail.elementText("Hold1");// Ԥ���ֶ�1
					String sdetailHold2 = elementDetail.elementText("Hold2");// Ԥ���ֶ�2
					String sdetailHold3 = elementDetail.elementText("Hold3");// Ԥ���ֶ�3
					String sdetailHold4 = elementDetail.elementText("Hold4");// Ԥ���ֶ�4
					String ViceSign = elementDetail.elementText("ViceSign");// ������־
					TvNontaxsubDto subdto = new TvNontaxsubDto();
					
					subdto.setSdealno(mainvou);
					subdto.setSseqno(String.valueOf(++j));
					subdto.setStrono(TroNo);// ������ˮ��
					subdto.setSvoucherno(VoucherNosub);// ƾ֤��
					
					subdto.setNtraamt(BigDecimal.valueOf(Double.valueOf(TraAmtsub)));// �ɿ���
					subdto.setSfundtypecode(FundTypecode);// �ʽ����ʱ���
					subdto.setSpaycode(PayCode);// �ɿ���
					subdto.setSbudgetsubject(BudgetSubject);// Ԥ���Ŀ����
					subdto.setSbudgetname(BudgetName);// Ԥ���Ŀ����
					subdto.setSitemcode(ItemCode);// ������Ŀ����
					subdto.setSitemname(ItemName);// ������Ŀ����
					subdto.setNdetailamt(BigDecimal.valueOf(Double.valueOf(DetailAmt)));// ��ϸ���
					subdto.setShold1(sdetailHold1);// Ԥ���ֶ�1
					subdto.setShold2(sdetailHold2);// Ԥ���ֶ�2
					subdto.setShold3(sdetailHold3);// Ԥ���ֶ�3
					subdto.setShold4(sdetailHold4);// Ԥ���ֶ�4
					subdto.setSvicesign(ViceSign);// ������־
					expFuncCodeList.add(BudgetSubject);
					sumAmt = sumAmt.add(BigDecimal.valueOf(Double.valueOf(DetailAmt)));
					subDtoList.add(subdto);
				}
				String ls_VoucherEnd = "";

				// ���ݽ�����ˮ��ȡ��ƾ֤������dto
				vDto = new TvVoucherinfoDto();
				vDto.setSdealno(mainvou);
				vDto = (TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(
						vDto).get(0);

				/**
				 * ��װverifydto,���б���У��
				 */
				verifydto.setTrecode(TreCode);
				verifydto.setFinorgcode(FinOrgCode);
				verifydto.setVoucherno(VoucherNo);
				verifydto.setVoudate(VouDate);
				verifydto.setPaybankno(PayBankNo);
				// ������ȡ��·ݣ��ܽ���У�� by renqingbin
				verifydto.setOfyear(StYear);
				verifydto.setFamt(TraAmt);
				String returnmsg = voucherVerify.checkValid(verifydto,
						MsgConstant.VOUCHER_NO_5671);
				if (returnmsg != null) {// ���ش�����Ϣǩ��ʧ��
					voucher.voucherComfail(vDto.getSdealno(), returnmsg);
					continue;
				}

				/**
				 *У���������Ƿ����ӱ������
				 * 
				 */
				if (maindto.getNmoney().compareTo(sumAmt) != 0) {
					String errMsg = "�����������ϸ�ۼƽ���ȣ�������"
							+ maindto.getNmoney() + " ��ϸ�ۼƽ� " + sumAmt;
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}

				/**
				 *У��ƾ֤��Ų��ܴ���17λ
				 * 
				 */
				if (maindto.getSvoucherno().getBytes().length > 20) {
					String errMsg = "ƾ֤��Ų��ܴ���20λ��";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}

				/**
				 *У��������=0.00����ϸ����=1
				 * 
				 */
				if (maindto.getNmoney().compareTo(BigDecimal.ZERO) == 0
						&& subDtoList.size() == 1) {
					String errMsg = "������" + maindto.getNmoney() + " ,��ϸ������1";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}

				/**
				 * ҵ�����⣬У�鱨�ĺϷ���
				 * 
				 * ҵ�������ӱ����ʧ�ܼ�ǩ��ʧ��
				 */
				TvNontaxsubDto [] subDtos = new TvNontaxsubDto[subDtoList.size()];
				subDtos = (TvNontaxsubDto[]) subDtoList.toArray(subDtos);

				DatabaseFacade.getODB().create(maindto);
				DatabaseFacade.getODB().create(subDtos);
				vDto.setIcount(subDtoList.size());
			} catch (Exception e) {
				logger.error(e);
				voucher.voucherComfail(mainvou, "���Ĳ��淶��" + e.getMessage());
				continue;
			}
			// ǩ�ճɹ�
			try {
				voucher.voucherConfirmSuccess(vDto.getSdealno());
			} catch (ITFEBizException e) {
				logger.error(e);
				VoucherException voucherE = new VoucherException();
				voucherE.saveErrInfo(VtCode, e);
				throw new ITFEBizException("ǩ��ƾ֤����" + VtCode + "�����쳣", e);
			}

			list = new ArrayList<Object>();
			list.add(maindto);
			list.add(vDto);
			list.add(expFuncCodeList);
			if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
				list.add(subDtoList);
			}
			lists.add(list);
		}

		/**
		 * У��ƾ֤��Ϣģ��
		 */
		try {
			if (lists.size() > 0) {
				voucher.voucherVerify(lists, VtCode);
			}

		} catch (ITFEBizException e) {
			logger.error(e);
			throw new ITFEBizException(e);
		} catch (Exception e) {
			logger.error(e);
			VoucherException voucherE = new VoucherException();
			voucherE.saveErrInfo(VtCode, e);
			throw new ITFEBizException("У��ƾ֤����" + VtCode + "�����쳣", e);
		}
		return;
	}

	public Voucher getVoucher() {
		return voucher;
	}

	public void setVoucher(Voucher voucher) {
		this.voucher = voucher;
	}
}

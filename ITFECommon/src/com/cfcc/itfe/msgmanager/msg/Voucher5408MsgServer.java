package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvNontaxmainDto;
import com.cfcc.itfe.persistence.dto.TvNontaxsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherVerify;

public class Voucher5408MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher5671MsgServer.class);
	private Voucher voucher;

	/**
	 * �����з������еķ�˰ƾ֤5408������ֻ����ҵ������
	 */
	@SuppressWarnings( { "static-access", "unchecked" })
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();
		String voucherXml = (String) muleMessage.getPayload();
		String ls_OrgCode = (String) muleMessage.getProperty("orgCode");
		HashMap<String, String> dealnos = (HashMap<String, String>) muleMessage
				.getProperty("dealnos");
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("������˰����ɿⵥ5408���ĳ��ִ���", e);
			throw new ITFEBizException("������˰����ɿⵥ5408���ĳ��ִ���", e);

		}
		List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes(
				"VoucherBody");
		String StYear = "";// ���
		String VtCode = "";// ƾ֤����

		// ��ȡ�����������롢��Ⱥ�ƾ֤����
		if (VoucherBodyList.size() > 0) {
			// VoucherBody
			Element element = (Element) VoucherBodyList.get(0);
			StYear = element.attribute("StYear").getText();
			VtCode = element.attribute("VtCode").getText();
		}

		// Ԥ���Ŀ����list
		ArrayList<String> expFuncCodeList = null;

		// String sAgencyCode ="";
		TvNontaxmainDto maindto = null;
		ArrayList<TvNontaxsubDto> subDtoList = null;
		List lists = new ArrayList();
		List<Object> list = null;
		// ƾ֤���к�
		String mainvou = "";
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		// ��������
		for (int i = 0; i < VoucherBodyList.size(); i++) {
			try {
				String errmessage = null;
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
				String VouDate = elementVoucher.elementText("VouDate"); // ƾ֤����
				String FinOrgCode = elementVoucher.elementText("FinOrgCode");// �������ش���
				String TreCode = elementVoucher.elementText("TreCode"); // �����������
				String PayeeAcctNo = elementVoucher.elementText("ClearAcctNo"); // �տ����˺�
				if(isNull(PayeeAcctNo))
					errmessage = "�տ����˺Ų���Ϊ��!";
				String PayeeAcctName = elementVoucher.elementText("ClearAcctName"); // �տ�������
				if(isNull(PayeeAcctName))
					errmessage = "�տ������Ʋ���Ϊ��!";
				String PayeeAcctBankName = elementVoucher.elementText("ClearAcctBankName"); // �տ��˿�����
				if(isNull(PayeeAcctBankName))
					errmessage = "�տ��˿����в���Ϊ��!";
				String BudgetLevelCode = elementVoucher.elementText("BudgetLevelCode"); // Ԥ�㼶�α���
				String BudgetLevelName = elementVoucher.elementText("BudgetLevelName"); // Ԥ�㼶������
				String FundTypeCode = elementVoucher.elementText("FundTypeCode"); // �ʽ����ʱ���
				String FundTypeName = elementVoucher.elementText("FundTypeName"); // �ʽ���������
				String PayBankCode = elementVoucher.elementText("PayBankCode"); // �������д���
				if(isNull(PayBankCode))
					errmessage = "�������д��벻��Ϊ��!";
				String PayBankName = elementVoucher.elementText("PayBankName"); // ������������
				if(isNull(PayBankName))
					errmessage = "�����������Ʋ���Ϊ��!";
				String PayBankNo = elementVoucher.elementText("PayBankNo"); // �������к�
				if(isNull(PayBankNo))
					errmessage = "�������кŲ���Ϊ��!";
				String AgentAcctName = elementVoucher.elementText("AgentAcctName"); // ����������
				if(isNull(AgentAcctName))
					errmessage = "���������Ʋ���Ϊ��!";
				String AgentAcctNo = elementVoucher.elementText("AgentAcctNo"); // �������˺�
				if(isNull(AgentAcctNo))
					errmessage = "�������˺Ų���Ϊ��!";
				String AgentAcctBankName = elementVoucher.elementText("AgentAcctBankName"); // �����˿�����
				if(isNull(AgentAcctBankName))
					errmessage = "�����˿����в���Ϊ��!";
				String PayDictateNo = null;
				String PayMsgNo = null;
				if(TreCode.startsWith("06")){
					PayDictateNo = elementVoucher.elementText("XPBPayDictateNo"); // ֧���������
					PayMsgNo = elementVoucher.elementText("XPBPayMsgNo"); // ֧�����ı��
				}else{
					PayDictateNo = elementVoucher.elementText("PayDictateNo"); // ֧���������
					PayMsgNo = elementVoucher.elementText("PayMsgNo"); // ֧�����ı��
				}
				if(isNull(PayDictateNo))
					errmessage = "֧��������Ų���Ϊ��!";
				
				if(isNull(PayMsgNo))
					errmessage = "֧�����ı�Ų���Ϊ��!";
//				String PayEntrustDate = elementVoucher.elementText("PayEntrustDate"); // ֧��ί������
				String XAddWord = elementVoucher.elementText("XAddWord"); // ����
				String SumAmt = elementVoucher.elementText("SumAmt"); // �ɿ���
				String XAcctDate = elementVoucher.elementText("XAcctDate"); // ʵ��֧������
				String XSumAmt = elementVoucher.elementText("XSumAmt"); // �����
				String Hold1 = elementVoucher.elementText("Hold1"); // Ԥ���ֶ�1
				String Hold2 = elementVoucher.elementText("Hold2"); // Ԥ���ֶ�2
				String Remark = elementVoucher.elementText("Remark"); // ��ע
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
				maindto.setSfinorgcode(FinOrgCode);// �������ش���
				maindto.setStrecode(TreCode); // �����������
				maindto.setSpayeeacctno(PayeeAcctNo); // �տ����˺�
				maindto.setSpayeeacctnane(PayeeAcctName); // �տ�������
				maindto.setSpayeeacctbankname(PayeeAcctBankName); // �տ��˿�����
				maindto.setSbudgetlevelcode(BudgetLevelCode); // Ԥ�㼶�α���
				maindto.setSbudgetlevelname(BudgetLevelName); // Ԥ�㼶������
				maindto.setShold3(FundTypeCode);//�ʽ����ʱ���
				maindto.setShold4(FundTypeName);//�ʽ���������
				maindto.setSpayacctname(AgentAcctName); // ����������
				maindto.setSpayacctno(AgentAcctNo); // �������˺�
				maindto.setSpayacctbankname(AgentAcctBankName); // �����˿�����
				maindto.setSpaybankno(PayBankNo); // �������к�
				maindto.setSpaybankcode(PayBankCode);
				maindto.setSpaybankname(PayBankName);
				maindto.setSpaymsgno(PayMsgNo);
				maindto.setSpaydictateno(PayDictateNo);
				maindto.setSxaddword(XAddWord); // ����
				maindto.setSxtradate(XAcctDate); // ʵ��֧������
				maindto.setStaxorgcode(TaxOrgCode);// ���ջ��ش���
				if(maindto.getStaxorgcode()==null||"".equals(maindto.getStaxorgcode())||"null".equals(maindto.getStaxorgcode())||"NULL".equals(maindto.getStaxorgcode()))
					maindto.setStaxorgcode(Hold1);
				
				maindto.setStaxorgname(TaxOrgName);// ���ջ�������
				if(XSumAmt==null || XSumAmt.equals(""))
					XSumAmt = "0.00";
				maindto.setNxmoney(BigDecimal.valueOf(Double.valueOf(XSumAmt))); // �����
//				maindto.setShold1(Hold1); // Ԥ���ֶ�1-�ʽ�������ˮ����
				maindto.setShold2(Hold2); // Ԥ���ֶ�2
				maindto.setSdemo(Remark);// ��ע
				maindto.setNmoney(BigDecimal.valueOf(Double.valueOf(SumAmt))); // �ɿ���
				maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);
				maindto.setStrimflag(StateConstant.TRIMSIGN_FLAG_NORMAL);
				maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
				
				if(TreCode.startsWith("06")){
					String PayEntrustDate = elementVoucher.elementText("PayEntrustDate"); // ֧��ί������
//					String FinCode = elementVoucher.elementText("FinCode"); // ���ڻ�������
//					String PeriodType = elementVoucher.elementText("PeriodType"); // ҵ���ڼ���������
					String PeriodTypeCode = elementVoucher.elementText("PeriodTypeCode"); // ҵ���ڼ����ʹ���
					String BusinessMode = elementVoucher.elementText("BusinessMode"); // ҵ��ģʽ����
//					String XPBAddWord = elementVoucher.elementText("XPBAddWord"); // �������и���
//					String XPBAcctDate = elementVoucher.elementText("XPBAcctDate"); // �������д�������
//					String XPBSumAmt = elementVoucher.elementText("XPBSumAmt"); // �������нɿ���
//					String XPBPayDictateNo = elementVoucher.elementText("XPBPayDictateNo"); // ֧���������
//					String XPBPayMsgNo = elementVoucher.elementText("XPBPayMsgNo"); // ֧�����ı��
					if(maindto.getStaxorgcode()==null||"".equals(maindto.getStaxorgcode())||"null".equals(maindto.getStaxorgcode())||"NULL".equals(maindto.getStaxorgcode()))
						maindto.setStaxorgcode(FinOrgCode);
					
					if(PeriodTypeCode.equals("01")){//����ҵ��
						maindto.setStrimflag(StateConstant.TRIMSIGN_FLAG_NORMAL);	
					}else{
						maindto.setStrimflag(StateConstant.TRIMSIGN_FLAG_TRIM);	//������ҵ��
					}
//					maindto.setScommitdate(PayEntrustDate);
				}
				
				TsTreasuryDto streDto = new TsTreasuryDto();
				streDto.setStrecode(TreCode);
//				List<TsTreasuryDto>	streDtoList = null;
//				try {
//					streDtoList = CommonFacade.getODB().findRsByDto(streDto);
//				} catch (ValidateException e) {
//					logger.error(e + "��ѯ������Ϣʧ�ܣ�");
//				}
				// Ԥ���Ŀ����list
				expFuncCodeList = new ArrayList<String>();
				/**
				 * ��װTvPayoutmsgsubDto����
				 */
				subDtoList = new ArrayList<TvNontaxsubDto>();
				boolean isfail = false;
				for (int j = 0; j < listDetail.size(); j++) {

					Element elementDetail = (Element) listDetail.get(j);
					Element sdetailIdElement = elementDetail.element("Id");
					String SubId = "";
					// �ڵ㲻����
					if (sdetailIdElement != null)
						SubId = elementDetail.elementText("Id");// ��ϸID

					String IncomeSortCode = elementDetail.elementText("IncomeSortCode");// ��������Ŀ����
					String IncomeSortName = elementDetail.elementText("IncomeSortName");// ��������Ŀ����
					String subRemark = elementDetail.elementText("Remark");// ��ע
					String Amt = elementDetail.elementText("Amt");// �ɿ���
					String sdetailHold1 = elementDetail.elementText("Hold1");// Ԥ���ֶ�1
					if(!TreCode.startsWith("06") && isNull(sdetailHold1))
						errmessage = "�ɿ�ʶ���벻��Ϊ��!";
					String sdetailHold2 = elementDetail.elementText("Hold2");// Ԥ���ֶ�2
					if(!TreCode.startsWith("06") && isNull(sdetailHold2))
						errmessage = "�ɿ���ȫ�Ʋ���Ϊ��!";
					String sdetailHold3 = elementDetail.elementText("Hold3");// Ԥ���ֶ�3
					if(!TreCode.startsWith("06") && isNull(sdetailHold3))
						errmessage = "�տ����˺Ų���Ϊ��!";
					String sdetailHold4 = elementDetail.elementText("Hold4");// Ԥ���ֶ�4
					if(!TreCode.startsWith("06") && isNull(sdetailHold4))
						errmessage = "ִ�յ�λ���Ʋ���Ϊ��!";
					String ViceSign = elementDetail.elementText("ViceSign");// ������־
					TvNontaxsubDto subdto = new TvNontaxsubDto();
					subdto.setSid(SubId);
					subdto.setSdealno(mainvou);
					subdto.setSseqno(String.valueOf(j));
					subdto.setNtraamt(BigDecimal.valueOf(Double.valueOf(Amt)));// �ɿ���
					subdto.setSfundtypecode(FundTypeCode);// �ʽ����ʱ���
					subdto.setSbudgetsubject(IncomeSortCode);
					subdto.setSbudgetname(IncomeSortName);
					subdto.setSitemcode(IncomeSortCode);//��������Ŀ����
					subdto.setSitemname(IncomeSortName);//��������Ŀ����
					subdto.setNdetailamt(BigDecimal.valueOf(Double.valueOf(Amt)));// ��ϸ���
					subdto.setShold1(sdetailHold1);// �ɿ�ʶ����
					if(TreCode.startsWith("06")){
						subdto.setShold1(VoucherNo);// �ɿ�ʶ����
					}
					subdto.setShold2(sdetailHold2);// �ɿ���ȫ��
					subdto.setShold3(sdetailHold3);// �տ����˺�
					subdto.setShold4(sdetailHold4);// ִ�յ�λ����
					subdto.setSvicesign(ViceSign);// ������־
					subdto.setSxaddword(subRemark);//Ԥ�㼶��
					if(subRemark!=null&&"0".equals(subRemark))//����Ϊ����
					{
						if(ViceSign==null||"".equals(ViceSign))
							isfail = true;
					}else
					{
						if(ViceSign!=null&&!"".equals(ViceSign))
							isfail = true;
					}
					expFuncCodeList.add(IncomeSortCode);
					sumAmt = sumAmt.add(BigDecimal.valueOf(Double.valueOf(Amt)));
					subDtoList.add(subdto);
				}
				maindto.setScount(String.valueOf(subDtoList==null?0:subDtoList.size()));
				// ���ݽ�����ˮ��ȡ��ƾ֤������dto
				vDto = new TvVoucherinfoDto();
				vDto.setSdealno(mainvou);
				vDto = (TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(
						vDto).get(0);
				if(isfail)
				{
					voucher.voucherComfail(vDto.getSdealno(), "Ԥ�㼶�κ͸�����־����Ӧ!");
					continue;
				}
				if(errmessage!=null&&errmessage.length()>5)
				{
					voucher.voucherComfail(vDto.getSdealno(), errmessage);
					continue;
				}
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
				verifydto.setFamt(String.valueOf(sumAmt));
				String returnmsg = voucherVerify.checkValid(verifydto,
						MsgConstant.VOUCHER_NO_5408);
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
	private boolean isNull(String code)
	{
		if(code==null||"".equals(code)||"null".equals(code.toLowerCase()))
			return true;
		else
			return false;
	}
}

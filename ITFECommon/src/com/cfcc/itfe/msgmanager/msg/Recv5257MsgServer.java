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

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvPayoutDetailListDto;
import com.cfcc.itfe.persistence.dto.TvPayoutDetailMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

public class Recv5257MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv5257MsgServer.class);
	private Voucher voucher;

	/**
	 * ���з���������ʵ������ƾ֤��ϸ�嵥�����ı��5257��
	 */
	@SuppressWarnings( { "unchecked", "null" })
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
			logger.error("����ʵ��������ϸ�嵥5257���ĳ��ִ���", e);
			throw new ITFEBizException("����ʵ��������ϸ�嵥5257���ĳ��ִ���", e);

		}
		// ��ȡVoucherBody
		List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes(
				"VoucherBody");
		String AdmDivCode = "";// ������������
		String StYear = "";// ���
		String VtCode = "";// ƾ֤����

		String currentDate = TimeFacade.getCurrentStringTime();// ��ǰϵͳ����
		List<String> voucherList = new ArrayList<String>();

		// ���ò��� ���� �����������롢��Ⱥ�ƾ֤����
		if (VoucherBodyList.size() > 0) {
			Element element = (Element) VoucherBodyList.get(0);
			AdmDivCode = element.attribute("AdmDivCode").getText();
			StYear = element.attribute("StYear").getText();
			VtCode = element.attribute("VtCode").getText();
		}

		ArrayList<BigDecimal> amtList = new ArrayList<BigDecimal>(); // �����list
		TvPayoutDetailMainDto maindto = null; // ����
		TvPayoutDetailListDto listdto = null;// �ӱ�

		List subDtoList = null;
		List lists = new ArrayList();
		List list = null;
		// ƾ֤���к�
		String mainvou = "";
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		// ��������
		for (int i = 0; i < VoucherBodyList.size(); i++) {
			try {

				String ErrorMsg="";
				// ��ȡVoucherBody
				Element element = (Element) VoucherBodyList.get(i);
				// ����ͷ�뱨����ƾ֤���
				String VoucherNo = element.attribute("VoucherNo").getText();// ƾ֤���
				// ��ȡ����ͷ��Ϣ
				Element elementVoucher = (Element) element
						.selectSingleNode("Voucher");
				// ��ȡ��ϸ��ϢList
				List listDetail = elementVoucher.selectSingleNode("DetailList")
						.selectNodes("Detail");
				// ͳ����ϸ���
				BigDecimal sumAmt = new BigDecimal("0.00");
				// ����У����Ϣdto
				VoucherVerifyDto verifydto = new VoucherVerifyDto();
				VoucherVerify voucherVerify = new VoucherVerify();
				/**
				 * ƾ֤��Ϣ
				 */
				String Attach = ((Element) VoucherBodyList.get(i))
						.elementText("Attach");// ������Ϣ
				/**
				 * ��ʽ�������� <������Ϣ>
				 */
				String Id = elementVoucher.elementText("Id");// ����ƾ֤�嵥Id
				/*
				 * �����������롢ҵ����ȡ�ƾ֤���ͱ���ѽ���
				 */
				String VouDate = elementVoucher.elementText("VouDate"); // ƾ֤����
				String VoucherNo_ID = elementVoucher.elementText("VoucherNo");// ����ƾ֤�嵥ID
				String TreCode = elementVoucher.elementText("TreCode");// 
				String FinOrgCode = elementVoucher.elementText("FinOrgCode");// 
				String PayAmt = elementVoucher.elementText("PayAmt"); // ���ܲ����ܽ��

				String PrintUser = elementVoucher.elementText("PrintUser");// ��ӡ��
				String Remark = elementVoucher.elementText("Remark");// ��ע
				String Hold1 = elementVoucher.elementText("Hold1");// Ԥ���ֶ�1
				String Hold2 = elementVoucher.elementText("Hold2");// Ԥ���ֶ�2

				/**
				 * ����������� ����
				 **/
				maindto = new TvPayoutDetailMainDto();
				mainvou = dealnos.get(VoucherNo);// ��ȡ���к�
				maindto.setIvousrlno(Long.valueOf(mainvou));// ƾ֤��ˮ��
				maindto.setSid(Id); // S_ID IS '�����嵥��ϸ���';
				maindto.setSadmdivcode(AdmDivCode); // S_ADMDIVCODE IS '������������';
				maindto.setSstyear(StYear); // S_STYEAR IS 'ҵ�����';
				maindto.setSvtcode(VtCode); // S_VTCODE IS 'ƾ֤���ͱ��5257';
				maindto.setSvoudate(VouDate); // S_VOUDATE IS 'ƾ֤����';
				maindto.setSvoucherno(VoucherNo_ID); // S_VOUCHERNO IS 'ƾ֤��';
				maindto.setStrecode(TreCode);// 
				maindto.setSfinorgcode(FinOrgCode);
				maindto.setNpayamt(new BigDecimal(PayAmt)); // N_PAYAMT//
				// '���ܲ�����';
				if (maindto.getNpayamt().compareTo(new BigDecimal("0.00")) == -1) {
					amtList.add(maindto.getNpayamt());
				}
				maindto.setSprintuser(PrintUser); // S_PRINTUSER IS '��ӡ��';
				maindto.setSremark(Remark); // S_REMARK IS '��ע';
				maindto.setShold1(Hold1); // S_HOLD2 IS 'Ԥ���ֶ�1';
				maindto.setShold2(Hold2); // S_HOLD3 IS 'Ԥ���ֶ�2';

				/**
				 * ��װ�ӱ���Ϣ ��װ����TvPayoutDetailListDto
				 * 
				 */
				subDtoList = new ArrayList<TvPayoutDetailListDto>();
				for (int j = 0; j < listDetail.size(); j++) {
					// ��ϸ�ϼƽ��
					Element elementDetail = (Element) listDetail.get(j);
					String _sId = elementDetail.elementText("Id");// ������ϸ���
					String _svoucherbillid = elementDetail
							.elementText("VoucherBillId");// ����ƾ֤�嵥Id
					String _spayvoucherno = elementDetail
							.elementText("PayVoucherNo");// ʵ������ƾ֤����
					String _sfundtypecode = elementDetail
							.elementText("FundTypeCode");// �ʽ����ʱ���
					String _sfundtypename = elementDetail
							.elementText("FundTypeName");// 
					String _spaytypecode = elementDetail
							.elementText("PayTypeCode");// ֧����ʽ����
					String _spaytypename = elementDetail
							.elementText("PayTypeName");//
					String _spayeeacctno = elementDetail
							.elementText("PayeeAcctNo");// �տ����˺�
					String _spayeeacctname = elementDetail
							.elementText("PayeeAcctName");// �տ�������
					String _spayeeacctbankname = elementDetail
							.elementText("PayeeAcctBankName");// �տ�������
					String _spayeeacctbankno = elementDetail
							.elementText("PayeeAcctBankNo");// �տ������к�
					String _spayacctno = elementDetail.elementText("PayAcctNo");// �������˺�
					String _spayacctname = elementDetail
							.elementText("PayAcctName");// ����������
					String _spayacctbankname = elementDetail
							.elementText("PayAcctBankName");// ����������
					String _sagencycode = elementDetail
							.elementText("AgencyCode");// Ԥ�㵥λ����
					String _sagencyname = elementDetail
							.elementText("AgencyName");// Ԥ�㵥λ����
					String _sexpfunccode = elementDetail
							.elementText("ExpFuncCode");// ֧�����ܷ����Ŀ����
					String _sexpfuncname = elementDetail
							.elementText("ExpFuncName");// ֧�����ܷ����Ŀ����
					String _sexpecocode = elementDetail
							.elementText("ExpEcoCode");// ֧�����÷����Ŀ�����
					String _sexpeconame = elementDetail
							.elementText("ExpEcoName");// ֧�����÷����Ŀ������
					String _spaysummarycode = elementDetail
							.elementText("PaySummaryCode");// ��;����
					String _spaysummaryname = elementDetail
							.elementText("PaySummaryName");// ��;����
					String _npayamt = elementDetail.elementText("PayAmt");// ֧�����
					String _dpaydate = elementDetail.elementText("PayDate");// ��������
					String _sdetailHold1 = elementDetail.elementText("Hold1");// Ԥ���ֶ�1
					String _sdetailHold2 = elementDetail.elementText("Hold2");// Ԥ���ֶ�2
					String _sdetailHold3 = elementDetail.elementText("Hold3");// Ԥ���ֶ�3
					String _sdetailHold4 = elementDetail.elementText("Hold4");// Ԥ���ֶ�4

					/*
					 * �����ӱ���Ϣ
					 */
					TvPayoutDetailListDto subdto = new TvPayoutDetailListDto();
					subdto.setIvousrlno(Long.valueOf(mainvou));// �ӱ����к�
					subdto.setSid(_sId);// S_ID IS '�����嵥��ϸ���';
					subdto.setSvoucherbillid(_svoucherbillid);// S_VOUCHERBILLID IS '����ƾ֤�嵥ID';
					subdto.setSpayvoucherno(_spayvoucherno);// S_PAYVOUCHERNO IS// '����ƾ֤ƾ֤��';
					subdto.setSfundtypecode(_sfundtypecode);// S_FUNDTYPECODE IS// '�ʽ����ʱ���';
					subdto.setSfundtypename(_sfundtypename);// S_FUNDTYPENAME IS/ '�ʽ���������';
					subdto.setSpaytypecode(_spaytypecode);// S_PAYTYPECODE IS// '֧����ʽ����';
					subdto.setSpaytypename(_spaytypename);// S_PAYTYPENAME IS// '֧����ʽ����';
					subdto.setSpayeeacctno(_spayeeacctno);// S_PAYEEACCTNO IS/ '�տ����˺�';
					subdto.setSpayeeacctname(_spayeeacctname);// S_PAYEEACCTNAME/ IS '�տ�������';
					
					subdto.setSpayacctno(_spayacctno);// S_PAYACCTNO IS '�������˺�';
					subdto.setSpayacctname(_spayacctname);// S_PAYACCTNAME IS
					// '����������';
					subdto.setSpayacctbankname(_spayacctbankname);// S_PAYACCTBANKNAME
					// IS
					// '����������';
					subdto.setSagencycode(_sagencycode);// S_AGENCYCODE IS
					// 'Ԥ�㵥λ����';
					subdto.setSagencyname(_sagencyname);// S_AGENCYNAME IS
					// 'Ԥ�㵥λ����';
					subdto.setSexpfunccode(_sexpfunccode);// S_EXPFUNCCODE IS
					// '֧�����ܷ����Ŀ����';
					subdto.setSexpfuncname(_sexpfuncname);// S_EXPFUNCNAME IS
					// '֧�ֹ��ܷ����Ŀ����';
					subdto.setSexpecocode(_sexpecocode);// S_EXPECOCODE IS
					// '֧�����÷����Ŀ����';
					subdto.setSexpeconame(_sexpeconame);// S_EXPECONAME IS
					// '֧�����÷����Ŀ����';
					subdto.setSpaysummarycode(_spaysummarycode);// S_PAYSUMMARYCODE
					// IS '��;����';
					subdto.setSpaysummaryname(_spaysummaryname);// S_PAYSUMMARYNAME
					// IS '��;����';
					subdto.setNpayamt(new BigDecimal(_npayamt));// N_PAYAMT IS
					// '֧�����';
					// ������б�
					if (subdto.getNpayamt().compareTo(new BigDecimal("0.00")) == -1) {
						amtList.add(subdto.getNpayamt());
					}
					// �ۼ���ϸ���
					sumAmt = sumAmt.add(subdto.getNpayamt());

					subdto.setDpaydate(_dpaydate);// D_PAYDATE IS '��������';
					subdto.setShold1(_sdetailHold1);// S_HOLD1 IS 'Ԥ���ֶ�1';
					subdto.setShold2(_sdetailHold2);// S_HOLD2 IS 'Ԥ���ֶ�2';
					subdto.setShold3(_sdetailHold3);// S_HOLD3 IS 'Ԥ���ֶ�3';
					subdto.setShold4(_sdetailHold4);// S_HOLD4 IS 'Ԥ���ֶ�4';
					
					subdto.setSpayeeacctbankname(_spayeeacctbankname);// S_PAYEEACCTBANKNAME	// '�տ�������';
					// S_PAYEEACCTBANKNO// IS '�տ������к�(���в���)';\
					if (null!=_spayeeacctbankno&&_spayeeacctbankno.length()==12) {
						subdto.setSpayeeacctbankno(_spayeeacctbankno);
					}else {
						subdto.setSpayeeacctbankno(null);
//						String spayeeacctbankno=CollectionInfo(subdto,maindto);
//						logger.debug("-----------�տ������к�(���в���)S_PAYEEACCTBANKNO��["+spayeeacctbankno+"]");
//						if (null!=spayeeacctbankno&&spayeeacctbankno.length()==12) {
//							subdto.setSpayeeacctbankno(spayeeacctbankno);//��¼�տ������к�
//						}else {
//							ErrorMsg+=_spayvoucherno+";";
//						}
						
					}
					subDtoList.add(subdto);
				}
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
				// ������ȣ��ܽ���У��
				verifydto.setOfyear(StYear);
				verifydto.setFamt(PayAmt);
				// ����У��Ԥ�㵥λ����
				String returnmsg = voucherVerify.checkValid(verifydto,
						MsgConstant.VOUCHER_NO_5257);

				if (returnmsg != null) {// ���ش�����Ϣǩ��ʧ��
					voucher.voucherComfail(vDto.getSdealno(), returnmsg);
					continue;
				}

				if (ErrorMsg != null&& ErrorMsg.trim().length()>0) {// ���ش�����Ϣǩ��ʧ��
					voucher.voucherComfail(vDto.getSdealno(), "δ�ҵ�ƾ֤��Ϊ["+ErrorMsg+"]��ԭʼƾ֤");
					continue;
				}
				/**
				 *У�鲦���嵥���Ƿ��и����
				 */

				if (amtList.size() > 0) {
					String errMsg = "�����嵥��Ϣ�а����и���������!";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}

				/**
				 *У���������Ƿ����ӱ������
				 */
				if (maindto.getNpayamt().compareTo(sumAmt) != 0) {
					String errMsg = "�����������ϸ�ۼƽ���ȣ�������"
							+ maindto.getNpayamt() + " ��ϸ�ۼƽ� " + sumAmt;
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}

				/**
				 * ҵ�����⣬У�鱨�ĺϷ���
				 * 
				 * ҵ�������ӱ����ʧ�ܼ�ǩ��ʧ��
				 */
				TvPayoutDetailListDto[] subDtos = new TvPayoutDetailListDto[subDtoList
						.size()];
				subDtos = (TvPayoutDetailListDto[]) subDtoList.toArray(subDtos);
				DatabaseFacade.getODB().create(maindto);
				DatabaseFacade.getODB().create(subDtos);
				vDto.setIcount(subDtoList.size());
			} catch (JAFDatabaseException e) {
				logger.error(e);
				voucher.voucherComfail(mainvou, "�������ݿ���ִ���" + e.getMessage());
				continue;
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
				VoucherException.saveErrInfo(vDto.getSvtcode(), e);
				continue;
			}

			list = new ArrayList();
			list.add(maindto);
			list.add(vDto);
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
			throw new ITFEBizException("У��ƾ֤����" + VtCode + "�����쳣", e);
		}
		return;
	}
/**
 * �տ������к�
 * ���ҷ�Χ�� ʵ���ʽ����ʷ����������Ҵ���ɹ�
 * 
 * ƾ֤���+�������ش���+�������+��������+���
 * @param _spayvoucherno
 * @return
 * @throws ValidateException 
 * @throws JAFDatabaseException 
 */
	private String CollectionInfo(TvPayoutDetailListDto subdto,TvPayoutDetailMainDto maindto) throws JAFDatabaseException, ValidateException {
//		vDto=(TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
		TvPayoutmsgmainDto dto=new TvPayoutmsgmainDto();
		String	sql=" AND S_TAXTICKETNO='"+subdto.getSpayvoucherno()+"' AND S_TRECODE='"+maindto.getStrecode()+"' AND S_PAYUNIT='"+maindto.getSfinorgcode()+
		"' and  S_XPAYAMT='"+subdto.getNpayamt()+"' AND S_OFYEAR='"+maindto.getSstyear()+"'";
		List<TvPayoutmsgmainDto> list= CommonFacade.getODB().findRsByDtoForWhere(dto, sql);

		if (list.size()>0) {
			TvPayoutmsgmainDto tvPayoutmsgmainDto=list.get(0);
			String bankno=tvPayoutmsgmainDto.getSrecbankno();
			if (null!=bankno&&bankno.length()==12 ) {
				return bankno;
			}
		}
		
		return null;
	}

	public Voucher getVoucher() {
		return voucher;
	}

	public void setVoucher(Voucher voucher) {
		this.voucher = voucher;
	}

}

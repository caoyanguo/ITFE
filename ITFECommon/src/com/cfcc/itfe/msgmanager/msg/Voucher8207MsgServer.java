package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailsmainDto;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailssubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherCompare;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
@SuppressWarnings({ "unchecked", "static-access" })
public class Voucher8207MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher8207MsgServer.class);
	private Voucher voucher;

	/**
	 * �����������е�����ҵ��֧����ϸƾ֤8207������ֻ����ҵ������
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();
		String voucherXml = (String) muleMessage.getPayload();
		String ls_FileName = (String) muleMessage.getProperty("fileName");
//		String ls_OrgCode = (String) muleMessage.getProperty("orgCode");
		HashMap<String, String> dealnos = (HashMap<String, String>) muleMessage
				.getProperty("dealnos");
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("������������ҵ��֧����ϸƾ֤8207���ĳ��ִ���", e);
			throw new ITFEBizException("������������ҵ��֧����ϸƾ֤8207���ĳ��ִ���", e);

		}
		List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes(
				"VoucherBody");
		String StYear = "";// ���
		String VtCode = "";// ƾ֤����

//		String currentDate = TimeFacade.getCurrentStringTime();// ��ǰϵͳ����
//		List<String> voucherList = new ArrayList<String>();

		// ��ȡ�����������롢��Ⱥ�ƾ֤����
		if (VoucherBodyList.size() > 0) {
			Element element = (Element) VoucherBodyList.get(0);
			StYear = element.attribute("StYear").getText();
			VtCode = element.attribute("VtCode").getText();
		}
		TfPaymentDetailsmainDto maindto = null;
		List subDtoList = null;
		// ƾ֤���к�
		String mainvou = "";
		TvVoucherinfoDto vDto;
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
				// String VoucherNo =
				// elementVoucher.elementText("VoucherNo");//ƾ֤��
				String VouDate = elementVoucher.elementText("VouDate"); // ƾ֤����
				String OriginalVtCode = elementVoucher
						.elementText("OriginalVtCode"); // ��ƾ֤���ͱ��
				String OriginalVoucherNo = elementVoucher
						.elementText("OriginalVoucherNo"); // ��֧��ƾ֤���
				String FundTypeCode = elementVoucher
						.elementText("FundTypeCode"); // �ʽ����ʱ���
				String FundTypeName = elementVoucher
						.elementText("FundTypeName"); // �ʽ���������
				String PayDictateNo = elementVoucher
						.elementText("PayDictateNo"); // ֧���������
				String PayMsgNo = elementVoucher.elementText("PayMsgNo"); // ֧�����ı��
				String PayEntrustDate = elementVoucher
						.elementText("PayEntrustDate"); // ֧��ί������
				String PaySndBnkNo = elementVoucher.elementText("PaySndBnkNo"); // ֧���������к�
				String SumAmt = elementVoucher.elementText("SumAmt"); // ����֧�����
				String AgencyCode = elementVoucher.elementText("AgencyCode"); // ����Ԥ�㵥λ����
				String AgencyName = elementVoucher.elementText("AgencyName"); // ����Ԥ�㵥λ����
				String PayAcctNo = elementVoucher.elementText("PayAcctNo"); // �������˺�
				String PayAcctName = elementVoucher.elementText("PayAcctName"); // ����������
				String PayAcctBankName = elementVoucher
						.elementText("PayAcctBankName"); // ����������
				String PayBankCode = elementVoucher.elementText("PayBankCode"); // �������б���
				String PayBankName = elementVoucher.elementText("PayBankName"); // ������������
				String BusinessTypeCode = elementVoucher
						.elementText("BusinessTypeCode"); // ҵ�����ͱ���
				String BusinessTypeName = elementVoucher
						.elementText("BusinessTypeName"); // ҵ����������
				String PayTypeCode = elementVoucher.elementText("PayTypeCode"); // ֧����ʽ����
				String PayTypeName = elementVoucher.elementText("PayTypeName"); // ֧����ʽ����
				String XPayDate = elementVoucher.elementText("XPayDate"); // ʵ��֧������
				String XSumAmt = elementVoucher.elementText("XSumAmt"); // ʵ��֧�����ܽ��
				String ExpFuncCode = elementVoucher.elementText("ExpFuncCode");// ֧�����ܿ�Ŀ����
				String ExpFuncName = elementVoucher.elementText("ExpFuncName");// ֧�����ܿ�Ŀ����
				String Remark = elementVoucher.elementText("Remark");// ��ע
				String Hold1 = elementVoucher.elementText("Hold1");// Ԥ���ֶ�1
				String Hold2 = elementVoucher.elementText("Hold2");// Ԥ���ֶ�2

				/**
				 * ��װTfPaymentDetailsmainDto����
				 **/
				vDto = new TvVoucherinfoDto();
				mainvou = dealnos.get(VoucherNo);// ��ȡ���к�
				vDto.setSdealno(mainvou);
				vDto = (TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(
						vDto).get(0);

				maindto = new TfPaymentDetailsmainDto();
				maindto.setIvousrlno(Long.parseLong(mainvou));
				maindto.setSvoudate(VouDate);
				maindto.setSvtcode(VtCode);
				maindto.setSoriginalvtcode(OriginalVtCode);
				maindto.setSoriginalvoucherno(OriginalVoucherNo);
				maindto.setSfundtypecode(FundTypeCode);
				maindto.setSfundtypename(FundTypeName);
				maindto.setSpaydictateno(PayDictateNo);
				maindto.setSpaymsgno(PayMsgNo);
				maindto.setSpayentrustdate(PayEntrustDate);
				maindto.setSpaysndbnkno(PaySndBnkNo);
				maindto.setNsumamt(BigDecimal.valueOf(Double.valueOf(SumAmt)));
				maindto.setSagencycode(AgencyCode);
				maindto.setSagencyname(AgencyName);
				maindto.setSpayacctno(PayAcctNo);
				maindto.setSpayacctname(PayAcctName);
				maindto.setSpayacctbankname(PayAcctBankName);
				maindto.setSpaybankcode(PayBankCode);
				maindto.setSpaybankname(PayBankName);
				maindto.setSbusinesstypecode(BusinessTypeCode);
				maindto.setSbusinesstypename(BusinessTypeName);
				if (PayTypeCode != null && "11".equals(PayTypeCode)) {
					maindto.setSpaytypecode("0");// ֱ��֧��
				} else if (PayTypeCode != null && "12".equals(PayTypeCode))
					maindto.setSpaytypecode("1");// ��Ȩ֧��
				else if (PayTypeCode != null)
					maindto.setSpaytypecode(PayTypeCode);
				maindto.setSpaytypename(PayTypeName);
				maindto.setSxpaydate(XPayDate);
				if (DealCodeConstants.VOUCHER_FLAT_1.equals(vDto
						.getSvoucherflag())) {
					if (StringUtils.isBlank(XSumAmt)) {
						voucher.voucherComfail(vDto.getSdealno(),
								"�ص��У�ʵ��֧�����ܽ���Ϊ�գ�");
						continue;
					}
					maindto.setNxsumamt(MtoCodeTrans
							.transformBigDecimal(XSumAmt));
				} else {
					maindto.setNxsumamt(null);
				}
				maindto.setShold1(Hold1);
				maindto.setShold2(Hold2);
				maindto.setSfilename(ls_FileName);
				maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);
				maindto.setTssysupdate(new Timestamp(new java.util.Date()
						.getTime()));// ϵͳʱ��
				maindto.setSext1(ExpFuncCode);
				maindto.setSext2(ExpFuncName);
				maindto.setSext3(Remark);

				// ҵ���ӱ���
				subDtoList = new ArrayList<TfPaymentDetailssubDto>();
				// �ӱ���ϸId����
				List<String> subDtoIdList = new ArrayList<String>();
				/**
				 * ��װTfPaymentDetailssubDto����
				 */
				for (int j = 0; j < listDetail.size(); j++) {

					Element elementDetail = (Element) listDetail.get(j);
					String Id = elementDetail.elementText("Id");// ��ϸ���
					String OriVoucherNo = elementDetail
							.elementText("OriVoucherNo");// ֧��ƾ֤���
					String PayeeAcctNo = elementDetail
							.elementText("PayeeAcctNo");// �տ����˺�
					String PayeeAcctName = elementDetail
							.elementText("PayeeAcctName");// �տ�������
					String PayeeAcctBankName = elementDetail
							.elementText("PayeeAcctBankName");// �տ�������
					String PayAmt = elementDetail.elementText("PayAmt");// ֧�����
					String sdetailRemark = elementDetail.elementText("Remark");// ��ע
					String DetailXPayDate = elementDetail
							.elementText("XPayDate");// ʵ��֧������
					String XAgentBusinessNo = elementDetail
							.elementText("XAgentBusinessNo");// ���н�����ˮ��
					String XPayAmt = elementDetail.elementText("XPayAmt");// ʵ��֧�����
					String XPayeeAcctBankName = elementDetail
							.elementText("XPayeeAcctBankName");// �տ�������
					String XPayeeAcctNo = elementDetail
							.elementText("XPayeeAcctNo");// �տ����˺�
					String XAddWordCode = elementDetail
							.elementText("XAddWordCode");// ʧ��ԭ�����
					String XAddWord = elementDetail.elementText("XAddWord");// ʧ��ԭ��
					String sdetailHold1 = elementDetail.elementText("Hold1");// Ԥ���ֶ�1
					String sdetailHold2 = elementDetail.elementText("Hold2");// Ԥ���ֶ�2
					String sdetailHold3 = elementDetail.elementText("Hold3");// Ԥ���ֶ�3
					String sdetailHold4 = elementDetail.elementText("Hold4");// Ԥ���ֶ�4
					//�Ϻ����������ֶ�
					String expFuncCode = elementDetail.elementText("ExpFuncCode");//���ܷ����Ŀ����
					String expFuncName = elementDetail.elementText("ExpFuncName");//���ܷ����Ŀ����
					TfPaymentDetailssubDto subdto = new TfPaymentDetailssubDto();
					subdto.setSext1(expFuncCode);
					subdto.setSext2(expFuncName);
					subdto.setIvousrlno(Long.parseLong(mainvou));
					subdto.setIseqno((long) (j + 1));
					subdto.setSid(Id);
					subdto.setSorivoucherno(OriVoucherNo);
					subdto.setSpayeeacctno(PayeeAcctNo);
					subdto.setSpayeeacctname(PayeeAcctName);
					subdto.setSpayeeacctbankname(PayeeAcctBankName);
					subdto.setNpayamt(BigDecimal
							.valueOf(Double.valueOf(PayAmt)));
					subdto.setSremark(sdetailRemark);
					subdto.setSxpaydate(DetailXPayDate);
					subdto.setSxagentbusinessno(XAgentBusinessNo);
					if (DealCodeConstants.VOUCHER_FLAT_1.equals(vDto
							.getSvoucherflag())) {
						if (StringUtils.isBlank(XPayAmt)) {
							voucher.voucherComfail(vDto.getSdealno(),
									"�ص��У�ʵ��֧������Ϊ�գ�");
							continue;
						}
						subdto.setNxpayamt(MtoCodeTrans
								.transformBigDecimal(XPayAmt));
					} else {
						subdto.setNxpayamt(null);
					}
					subdto.setSxpayeeacctbankname(XPayeeAcctBankName);
					subdto.setSxpayeeacctno(XPayeeAcctNo);
					subdto.setSxaddwordcode(XAddWordCode);
					subdto.setSxaddword(XAddWord);
					subdto.setShold1(sdetailHold1);
					subdto.setShold2(sdetailHold2);
					subdto.setShold3(sdetailHold3);
					subdto.setShold4(sdetailHold4);

					// ��ϸ�ϼƽ��
					sumAmt = sumAmt.add(BigDecimal.valueOf(Double
							.valueOf(PayAmt)));
					subDtoList.add(subdto);
					subDtoIdList.add(subdto.getSid());

				}

				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
					vDto.setShold3(OriginalVoucherNo);// 8207�ȶ�ƾ֤���
				}
				vDto.setShold4(BusinessTypeCode);// ҵ�����ͱ���
				maindto.setSstyear(vDto.getSstyear());
				maindto.setSvoucherno(vDto.getSvoucherno());
				maindto.setSorgcode(vDto.getSorgcode());
				maindto.setStrecode(vDto.getStrecode());
				maindto.setSadmdivcode(vDto.getSadmdivcode());
				maindto.setSdemo(vDto.getSattach());

				/**
				 * У����ϸId�Ƿ�Ϊ�ջ��ظ�
				 */
				String checkIdMsg = voucherVerify
						.checkValidSudDtoId(subDtoIdList);
				if (checkIdMsg != null) {
					// ���ش�����Ϣǩ��ʧ��
					voucher.voucherComfail(vDto.getSdealno(), checkIdMsg);
					continue;
				}
				/**
				 * ��װverifydto,���б���У��
				 */
				verifydto.setTrecode(maindto.getStrecode());
				verifydto.setFinorgcode(maindto.getSorgcode());
				verifydto.setVoucherno(VoucherNo);
				verifydto.setVoudate(VouDate);
				verifydto.setPaybankno(PayBankCode);
				verifydto.setPaytypecode(PayTypeCode);
				verifydto.setClearAcctNo(PayAcctNo);
				verifydto.setClearAcctName(PayAcctName);
				// ������ȣ��ܽ���У�� by renqingbin
				verifydto.setOfyear(StYear);
				verifydto.setFamt(SumAmt);
				// �Ϻ��ط���ɫ����У��
				// ����ԭƾ֤���͡��ʽ����ʱ��롢 ҵ�����ͱ����У��
				verifydto.setOriginalVtCode(OriginalVtCode);
				verifydto.setFundTypeCode(FundTypeCode);
				verifydto.setBusinessTypeCode(BusinessTypeCode);
				verifydto.setPayVoucherNo(OriginalVoucherNo);
				String returnmsg = voucherVerify.checkValid(verifydto,
						MsgConstant.VOUCHER_NO_8207);
				if (returnmsg != null) {// ���ش�����Ϣǩ��ʧ��
					voucher.voucherComfail(vDto.getSdealno(), returnmsg);
					continue;
				}

				/**
				 *У���������Ƿ����ӱ������
				 * 
				 */
				if (maindto.getNsumamt().compareTo(sumAmt) != 0) {
					String errMsg = "�����������ϸ�ۼƽ���ȣ�������"
							+ maindto.getNsumamt() + " ��ϸ�ۼƽ� " + sumAmt;
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}

				/**
				 *��ϸ�������ܳ���500
				 * 
				 */
				if (subDtoList.size() > 499) {
					String errMsg = "��ϸ��������С��500!";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}

				// ����8207���кͲ����ص�����
				// <VoucherFlag>0���͵�/1�ص�/ </ VoucherFlag >
				if (null != vDto
						&& DealCodeConstants.VOUCHER_FLAT_1.equals(vDto
								.getSvoucherflag())) {
					TvVoucherinfoDto tmpDto = new TvVoucherinfoDto();
					tmpDto.setSvoucherno(vDto.getSvoucherno());
					tmpDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS); // ״̬�ѻص�
					tmpDto.setSvoucherflag(DealCodeConstants.VOUCHER_FLAT_1);
					List<TvVoucherinfoDto> list = CommonFacade.getODB()
							.findRsByDto(tmpDto);
					if (null == list || list.size() == 0) {
						voucher
								.voucherComfail(vDto.getSdealno(),
										"�Ҳ�����Ӧ�Ļص���Ϣ��");
						continue;
					}
					TvVoucherinfoDto searchDto = list.get(0);
					if (null != searchDto) {
						VoucherCompare voucherCompare = new VoucherCompare();
						// �Ա��ص�ƾ֤�ͽ��ղ������͵�8207���жԱȲ��Ҹ���
						List returnList = new ArrayList();
						List acceptList = new ArrayList();
						// �ص���Ϣ
						returnList.add(vDto);
						returnList.add(maindto);
						returnList.add(subDtoList);
						// ԭʼ��Ϣ
						acceptList.add(searchDto);
						TfPaymentDetailsmainDto tfPaymentDetailsmainDto = new TfPaymentDetailsmainDto();
						tfPaymentDetailsmainDto.setIvousrlno(Long
								.valueOf(searchDto.getSdealno()));
						acceptList.add(CommonFacade.getODB().findRsByDto(
								tfPaymentDetailsmainDto).get(0));
						TfPaymentDetailssubDto tfPaymentDetailssubDto = new TfPaymentDetailssubDto();
						tfPaymentDetailssubDto.setIvousrlno(Long
								.valueOf(searchDto.getSdealno()));
						acceptList.add(CommonFacade.getODB().findRsByDto(
								tfPaymentDetailssubDto));
						String msg = voucherCompare.compare8207AcceptAndReturn(
								returnList, acceptList);
						if (StringUtils.isBlank(msg)) {
							// ǩ�ճɹ�
							try {
								voucher
										.voucherConfirmSuccess(vDto
												.getSdealno());
							} catch (ITFEBizException e) {
								logger.error(e);
								VoucherException voucherE = new VoucherException();
								voucherE.saveErrInfo(VtCode, e);
								throw new ITFEBizException("ǩ��ƾ֤����" + VtCode
										+ "�����쳣", e);
							}
							vDto.setSdemo("��ȡ�ص��ɹ�");	//��ϸ��Ϣ��Ϊ��
							vDto
									.setSstatus(DealCodeConstants.VOUCHER_READRETURN);
							DatabaseFacade.getODB().update(vDto);
							DatabaseFacade.getODB().update(
									(IDto) acceptList.get(1));
							DatabaseFacade.getODB().update(
									CommonUtil
											.listTArray((List<IDto>) acceptList
													.get(2)));
						} else {
							voucher.voucherComfail(vDto.getSdealno(), msg);
						}
					} else {
						voucher
								.voucherComfail(vDto.getSdealno(),
										"�Ҳ�����Ӧ��ƾ֤��Ϣ��");
					}
					continue;
				}

				/**
				 * ҵ�����⣬У�鱨�ĺϷ���
				 * 
				 * ҵ�������ӱ����ʧ�ܼ�ǩ��ʧ��
				 */
				DatabaseFacade.getODB().create(maindto);
				DatabaseFacade.getODB().create(
						CommonUtil.listTArray(subDtoList));
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

			if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") < 0) {
				try {
					VoucherUtil.voucherVerifyUpdateStatus(vDto, null, "У��ɹ�",
							true);
				} catch (JAFDatabaseException e) {
					logger.error(e);
					VoucherException.saveErrInfo(vDto.getSvtcode(), e
							.getMessage());
				}
				continue;
			}

			// ����ƾ֤״̬Ϊ"У����"
			try {
				VoucherUtil.voucherVerifyUpdateStatus(vDto, maindto
						.getSoriginalvtcode());
				// ƾ֤�ȶ�
				VoucherCompare voucherCompare = new VoucherCompare();
				voucherCompare.VoucherCompare(vDto, maindto, subDtoList);
			} catch (JAFDatabaseException e) {
				String errMsg = "ƾ֤��ţ�" + vDto.getSvoucherno()
						+ "����ƾ֤״̬Ϊ\"У����\"����ʧ��";
				logger.error(e);
				VoucherException.saveErrInfo(vDto.getSvtcode(), errMsg
						+ e.getMessage());
			} catch (ITFEBizException e) {
				logger.error(e);
				VoucherException.saveErrInfo(vDto.getSvtcode(), e.getMessage());
			}
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

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

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsPayacctinfoDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

public class Voucher5901MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher5901MsgServer.class);
	private Voucher voucher;

	/**
	 * ����Ԫ����5901������ֻ�����������
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
			logger.error("��������Ԫ����5901���ĳ��ִ���", e);
			throw new ITFEBizException("��������Ԫ����5901���ĳ��ִ���", e);

		}
		List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes(
				"VoucherBody");
		String AdmDivCode = "";// ������������
		String StYear = "";// ���
		String VtCode = "";// ƾ֤����
		List subDtoList = null;
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
		List mainDtoList = null;

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

				String VoucherNo = element.attribute("VoucherNo").getText();// ƾ֤���
				// Voucher
				Element elementVoucher = (Element) element
						.selectSingleNode("Voucher");
				mainvou = dealnos.get(VoucherNo);// ��ȡ���к�
				/**
				 * ƾ֤��Ϣ
				 */
				String VouDate = elementVoucher.elementText("VouDate"); // ƾ֤����
				AdmDivCode = elementVoucher.elementText("AdmDivCode"); // ƾ֤����
				String DataEle = elementVoucher.elementText("DataEle");// �������ݴ���
				String DataEleName = elementVoucher.elementText("DataEleName");// ����������������
				String Hold1 = elementVoucher.elementText("Hold1");// Ԥ���ֶ�1
				String Hold2 = elementVoucher.elementText("Hold2");// Ԥ���ֶ�2
				String Hold3 = elementVoucher.elementText("Hold3");// Ԥ���ֶ�3
				String Hold4 = elementVoucher.elementText("Hold4");// Ԥ���ֶ�4
				TsConvertfinorgDto conorg = new TsConvertfinorgDto();
				conorg.setSadmdivcode(AdmDivCode);
				conorg = (TsConvertfinorgDto) CommonFacade.getODB()
						.findRsByDto(conorg).get(0);
				// ��ϸ��ϢList
				List listDetail = elementVoucher.selectSingleNode("DetailList")
						.selectNodes("Detail");

				if (DataEle.equals("1")) {// Ԥ���Ŀ
					subDtoList = new ArrayList<TsBudgetsubjectDto>();
					for (int j = 0; j < listDetail.size(); j++) {
						Element elementDetail = (Element) listDetail.get(j);
						String Subjectcode = elementDetail
								.elementText("Subjectcode");// ��Ŀ����
						String Subjectname = elementDetail
								.elementText("Subjectname");// ��Ŀ����
						String Subjecttype = elementDetail
								.elementText("Subjecttype");// ��Ŀ����
						String Subjectclassification = elementDetail
								.elementText("Subjectclassification");// ��Ŀ����
						String IEsign = elementDetail.elementText("IEsign");// ��֧��־
						String Entrymark = elementDetail
								.elementText("Entrymark");// ¼���־
						String Subjectattribute = elementDetail
								.elementText("Subjectattribute");// ��Ŀ����
						String Allocationmark = elementDetail
								.elementText("Allocationmark");// ������־
						String SubjectcodeSimplify = elementDetail
								.elementText("SubjectcodeSimplify");// ��Ŀ����
						String FiscalCode = elementDetail
								.elementText("FiscalCode");// ��������
						String TaxCode = elementDetail.elementText("TaxCode");// ˰�����
						String SuperiorsCode = elementDetail
								.elementText("SuperiorsCode");// �ϼ�����
						String UniformCode = elementDetail
								.elementText("UniformCode");// ͳһ����
						String Statisticalcode = elementDetail
								.elementText("Statisticalcode");// ͳ�ƴ���
						TsBudgetsubjectDto idto = new TsBudgetsubjectDto();
						idto.setSsubjectcode(Subjectcode);// ��Ŀ����
						idto.setSsubjectname(Subjectname);// ��Ŀ����
						idto.setSsubjecttype(Subjecttype);// ��Ŀ����
						idto.setSsubjectclass(Subjectclassification);// ��Ŀ����
						idto.setSinoutflag(IEsign);// ��֧��־
						idto.setSwriteflag(Entrymark);// ¼���־
						idto.setSsubjectattr(Subjectattribute);// ��Ŀ����
						idto.setSmoveflag(Allocationmark);// ������־
						idto.setSorgcode(conorg.getSorgcode());
						subDtoList.add(idto);
					}
				} else if (DataEle.equals("2")) {// ֧���к�
					subDtoList = new ArrayList<TsPaybankDto>();
					for (int j = 0; j < listDetail.size(); j++) {
						Element elementDetail = (Element) listDetail.get(j);
						String Paylinenumber = elementDetail
								.elementText("Paylinenumber");// ֧���к�
						String Status = elementDetail.elementText("Status");// ״̬
						String Liquidationlinenumber = elementDetail
								.elementText("Liquidationlinenumber");// �������к�
						String City = elementDetail.elementText("City");// ���ڳ���
						String Participantsfullname = elementDetail
								.elementText("Participantsfullname");// ������ȫ��
						String effectivedate = elementDetail
								.elementText("effectivedate");// ��Ч����
						String Expirationdate = elementDetail
								.elementText("Expirationdate");// ʧЧ����
						String Remark = elementDetail.elementText("Remark");// ��ע

						TsPaybankDto idto = new TsPaybankDto();
						idto.setSbankno(Paylinenumber);// ֧���к�
						idto.setSstate(Status);// ״̬
						idto.setSpaybankno(Liquidationlinenumber);// �������к�
						idto.setSofcity(City);// ���ڳ���
						idto.setSbankname(Participantsfullname);// ������ȫ��
						idto.setDaffdate(DateUtil.stringToDate(effectivedate));// ��Ч����
						idto.setSorgcode("000000000000");
						subDtoList.add(idto);
					}
				} else if (DataEle.equals("3")) {// ������˻�
					subDtoList = new ArrayList<TsPayacctinfoDto>();
					for (int j = 0; j < listDetail.size(); j++) {
						Element elementDetail = (Element) listDetail.get(j);
						TsInfoconnorgaccDto accdto = new TsInfoconnorgaccDto();
						accdto.setStrecode(conorg.getStrecode());
						accdto.setSbiztype("1");
						accdto = (TsInfoconnorgaccDto) CommonFacade.getODB()
								.findRsByDto(accdto).get(0);

						String ZeroAccount = elementDetail
								.elementText("ZeroAccount");// ������˺�
						String ZBAsstate = elementDetail
								.elementText("ZBAsstate");// ������˻�״̬
						String Zerotime = elementDetail
								.elementText("Zerotime");// ��������ʱ��
						String ZeroBanknumber = elementDetail
								.elementText("ZeroBanknumber");// �������к�
						String ZeroBankname = elementDetail
								.elementText("ZeroBankname");// ������������
						String Zerocodingunit = elementDetail
								.elementText("Zerocodingunit");// ��������λ����
						String ZeroAccountName = elementDetail
								.elementText("ZeroAccountName");// ��������λ����
						TsPayacctinfoDto idto = new TsPayacctinfoDto();
						idto.setSorgcode(conorg.getSorgcode());
						idto.setStrecode(conorg.getStrecode());
						idto.setSpayeeacct(ZeroAccount);// �տ����˻�
						idto.setSpayeename(ZeroAccountName);// �տ�������
						idto.setSpayeracct(accdto.getSpayeraccount());// �������ʻ�
						idto.setSpayername(accdto.getSpayername());// ����������
						idto.setSgenbankcode(ZeroBanknumber);// ���������к�
						idto.setSbiztype("1");// ҵ������
						subDtoList.add(idto);
					}
				} else if (DataEle.equals("4")) {// Ԥ�㵥λ�䶯���
					subDtoList = new ArrayList<TdCorpDto>();
					for (int j = 0; j < listDetail.size(); j++) {
						Element elementDetail = (Element) listDetail.get(j);
						String SupDepName = elementDetail
								.elementText("SupDepName");// ��λ����
						String SupDepCode = elementDetail
								.elementText("SupDepCode");// ��λ����
						String UnitChangeStatus = elementDetail
								.elementText("UnitChangeStatus");// ��λ���״̬
						// 0�������1��������
						// 2��ɾ��
						String IssuedDate = elementDetail
								.elementText("IssuedDate");// ��������
						String EffectiveDate1 = elementDetail
								.elementText("EffectiveDate1");// ��Ч����

						TdCorpDto idto = new TdCorpDto();
						idto.setSbookorgcode(conorg.getSorgcode());
						idto.setStrecode(conorg.getStrecode());
						idto.setScorpcode(SupDepCode);// �տ����˻�
						idto.setScorpcode(SupDepName);// �տ�������
						subDtoList.add(idto);
					}
				}

				/**
				 * ҵ�����⣬У�鱨�ĺϷ���
				 * 
				 * ҵ�������ӱ����ʧ�ܼ�ǩ��ʧ��
				 */
				if (DataEle.equals("1")) {// Ԥ���Ŀ
					TsBudgetsubjectDto[] subDtos = new TsBudgetsubjectDto[subDtoList.size()];
					subDtos = (TsBudgetsubjectDto[]) subDtoList.toArray(subDtos);
					DatabaseFacade.getODB().create(subDtos);
				} else if (DataEle.equals("2")) {// ֧���к�
					TsPaybankDto[] subDtos = new TsPaybankDto[subDtoList.size()];
					subDtos = (TsPaybankDto[]) subDtoList.toArray(subDtos);
					DatabaseFacade.getODB().create(subDtos);
				} else if (DataEle.equals("3")) {// ������˻�
					TsPayacctinfoDto[] subDtos = new TsPayacctinfoDto[subDtoList.size()];
					subDtos = (TsPayacctinfoDto[]) subDtoList.toArray(subDtos);
					DatabaseFacade.getODB().create(subDtos);
				} else if (DataEle.equals("4")) {// Ԥ�㵥λ�䶯���
					TdCorpDto[] subDtos = new TdCorpDto[subDtoList.size()];
					subDtos = (TdCorpDto[]) subDtoList.toArray(subDtos);
					DatabaseFacade.getODB().create(subDtos);
				}

				// ǩ�ճɹ�
				vDto = new TvVoucherinfoDto();
				vDto.setSdealno(mainvou);
				vDto = (TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(
						vDto).get(0);
				vDto.setIcount(subDtoList.size());
				vDto.setSext5(DataEleName);
				vDto.setShold1(Hold1);
				vDto.setShold2(Hold2);
				vDto.setShold3(Hold3);
				vDto.setShold4(Hold4);
				vDto.setSext4(DataEle);
				
				voucher.voucherConfirmSuccess(vDto.getSdealno());

				vDto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
				vDto.setSdemo("У��ɹ�");
				DatabaseFacade.getODB().update(vDto);
				logger.debug("==============У��ɹ�==============");
			} catch (ITFEBizException e) {
				logger.error(e);
				VoucherException voucherE = new VoucherException();
				voucherE.saveErrInfo(VtCode, e);
				throw new ITFEBizException("ǩ��ƾ֤����" + VtCode + "�����쳣", e);
			} catch (JAFDatabaseException e) {
				logger.error(e);
				VoucherException voucherE = new VoucherException();
				voucherE.saveErrInfo(VtCode, e);
				throw new ITFEBizException("ǩ��ƾ֤����" + VtCode + "�����쳣", e);
			} catch (ValidateException e) {
				logger.error(e);
				VoucherException voucherE = new VoucherException();
				voucherE.saveErrInfo(VtCode, e);
				throw new ITFEBizException("ǩ��ƾ֤����" + VtCode + "�����쳣", e);
			} catch (Exception e) {
				logger.error(e);
				VoucherException voucherE = new VoucherException();
				voucherE.saveErrInfo(VtCode, e);
				throw new ITFEBizException("ǩ��ƾ֤����" + VtCode + "�����쳣", e);
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

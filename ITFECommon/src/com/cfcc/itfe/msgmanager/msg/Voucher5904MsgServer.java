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

public class Voucher5904MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher5904MsgServer.class);
	private Voucher voucher;

	/**
	 * ����Ԫ����5904������ֻ�����������
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
			logger.error("��������Ԫ����5904���ĳ��ִ���", e);
			throw new ITFEBizException("��������Ԫ����5904���ĳ��ִ���", e);

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

				// String VoucherNo =
				// element.attribute("VoucherNo").getText();// ƾ֤���
				// Voucher
				Element elementVoucher = (Element) element
						.selectSingleNode("Voucher");
				String VoucherNo = elementVoucher.elementText("VoucherNo");
				mainvou = dealnos.get(VoucherNo);// ��ȡ���к�
				/**
				 * ƾ֤��Ϣ
				 */
				String VouDate = elementVoucher.elementText("VouDate"); // ƾ֤����
				AdmDivCode = elementVoucher.elementText("AdmDivCode"); // ƾ֤����
				String VoucherCheckNo = elementVoucher
						.elementText("VoucherCheckNo");// �������ݵ���
				String ChildPackNum = elementVoucher
						.elementText("ChildPackNum");// �Ӱ�����
				String CurPackNo = elementVoucher.elementText("CurPackNo");// �������
				String DataEle = elementVoucher.elementText("DataEle");// �������ݴ���
				String DataEleName = elementVoucher.elementText("DataEleName");// ����������������
				String Hold1 = elementVoucher.elementText("Hold1");// Ԥ���ֶ�1
				String Hold2 = elementVoucher.elementText("Hold2");// Ԥ���ֶ�2
				TsConvertfinorgDto conorg = new TsConvertfinorgDto();
				conorg.setSadmdivcode(AdmDivCode);
				conorg = (TsConvertfinorgDto) CommonFacade.getODB()
						.findRsByDto(conorg).get(0);
				// ��ϸ��ϢList
				List listDetail = elementVoucher.selectSingleNode("DetailList")
						.selectNodes("Detail");
				for (int j = 0; j < listDetail.size(); j++) {
					Element elementDetail = (Element) listDetail.get(j);

					String Id = elementDetail.elementText("Id");// ���

					String ChrId = elementDetail.elementText("ChrId");// ��Ҫ��ID
					String ChrCode = elementDetail.elementText("ChrCode");// ��ʾ����
					String ChrName = elementDetail.elementText("ChrName");// ��ʾ����
					String LevelNum = elementDetail.elementText("LevelNum");// ����
					String IsLeaf = elementDetail.elementText("IsLeaf");// �Ƿ�׼�
					String Enabled = elementDetail.elementText("Enabled");// �Ƿ�����
					String CreateDate = elementDetail.elementText("CreateDate");// ����ʱ��
					String LatestOpDate = elementDetail
							.elementText("LatestOpDate");// ����޸�ʱ��
					String IsDeleted = elementDetail.elementText("IsDeleted");// �Ƿ�ɾ��
					String LastVer = elementDetail.elementText("LastVer");// ���汾
					String ParentId = elementDetail.elementText("ParentId");// ����ID
					String CurVer = elementDetail.elementText("CurVer");// ��ǰ�汾
					String AcctNo = elementDetail.elementText("AcctNo");// �˺�
					String AcctBankName = elementDetail
							.elementText("AcctBankName");// ������������
					String AcctBankCode = elementDetail
							.elementText("AcctBankCode");// �������б���
					String Hold1sub = elementDetail.elementText("Hold1");// Ԥ���ֶ�1
					String Hold2sub = elementDetail.elementText("Hold2");// Ԥ���ֶ�2
					String Hold3sub = elementDetail.elementText("Hold3");// Ԥ���ֶ�3
					String Hold4sub = elementDetail.elementText("Hold4");// Ԥ���ֶ�4
					if(DataEleName.equals("")){
						
					}
				}

				/**
				 * ҵ�����⣬У�鱨�ĺϷ���
				 * 
				 * ҵ�������ӱ����ʧ�ܼ�ǩ��ʧ��
				 */
				if (DataEle.equals("1")) {// Ԥ���Ŀ
					TsBudgetsubjectDto[] subDtos = new TsBudgetsubjectDto[subDtoList
							.size()];
					subDtos = (TsBudgetsubjectDto[]) subDtoList
							.toArray(subDtos);
					DatabaseFacade.getODB().create(subDtos);
				} else if (DataEle.equals("2")) {// ֧���к�
					TsPaybankDto[] subDtos = new TsPaybankDto[subDtoList.size()];
					subDtos = (TsPaybankDto[]) subDtoList.toArray(subDtos);
					DatabaseFacade.getODB().create(subDtos);
				} else if (DataEle.equals("3")) {// ������˻�
					TsPayacctinfoDto[] subDtos = new TsPayacctinfoDto[subDtoList
							.size()];
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

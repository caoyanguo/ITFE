package com.cfcc.itfe.voucher.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.mule.api.MuleException;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfDirectpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpayAdjustsubDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfGrantpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfGrantpayAdjustsubDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailsmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.persistence.dto.TvNontaxmainDto;
import com.cfcc.itfe.persistence.dto.TvNontaxsubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

public class Voucher extends VoucherFactory {
	private static Log logger = LogFactory.getLog(Voucher.class);

	public int voucherRead(String svtcode, String sorgcode) {
		
		Boolean b = true;
		if (b) {
		  return  readVoucher(svtcode, sorgcode);//�ֹ���ƾ֤���ȡ
		}else{
		  return TestLocalFile();//�����ļ����ԣ���Ҫ��b=false//���Է�ʽ
		}

	}

	public int voucherCommit(List list) throws ITFEBizException {
		return super.voucherCommit(VoucherUtil.getListByAdmdivcode(list));
	}

	public int voucherReturnSuccess(List list) throws ITFEBizException {
		return super.voucherSendReturnSuccess(VoucherUtil
				.getListByAdmdivcode(list));
	}

	public int voucherReturnBack(List list) throws ITFEBizException {
		
		return super.voucherReturnBack(VoucherUtil.getListByAdmdivcode(list));
	}

	public int voucherReturnQueryStatus(List list) throws ITFEBizException {
		
		return super.voucherReturnQueryStatus(VoucherUtil
				.getListByAdmdivcode(list));
	}

	/**
	 * ƾ֤У��
	 * 
	 * @param checkList
	 * @return
	 * @throws ITFEBizException
	 */
	public int voucherVerify(List checkList) throws ITFEBizException {
		int count = 0;
		List lists = new ArrayList();
		List list = null;
		try{
			for (TvVoucherinfoDto vDto : (List<TvVoucherinfoDto>) checkList) {
				list = new ArrayList();
				/**
				 * ����ƾ֤�б��ȡ ���� �ӱ�Ԥ�㵥λ�����б�
				 * 
				 */
				if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267)) {	
					
						TvPayoutmsgmainDto mainDto = new TvPayoutmsgmainDto();
						TvPayoutmsgsubDto subDto = new TvPayoutmsgsubDto();
						List<TvPayoutmsgsubDto> subList = new ArrayList<TvPayoutmsgsubDto>();
						subDto.setSbizno(vDto.getSdealno());
						mainDto.setSbizno(vDto.getSdealno());
						// ����
						mainDto = (TvPayoutmsgmainDto) CommonFacade.getODB()
									.findRsByDto(mainDto).get(0);					
						subList = CommonFacade.getODB().findRsByDto(subDto);
						ArrayList<String> expFuncCodeList = new ArrayList<String>();
						// �ӱ�Ԥ�㵥λ�����б�
						for (TvPayoutmsgsubDto sDto : subList) {
							expFuncCodeList.add(sDto.getSfunsubjectcode());
							
						}
						list.add(mainDto);
						list.add(vDto);
						list.add(expFuncCodeList);
						list.add(subList);
						lists.add(list);
					
				} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)) {
						TvPayreckBankDto mainDto = new TvPayreckBankDto();
						TvPayreckBankListDto subDto = new TvPayreckBankListDto();
						List<TvPayreckBankListDto> subList = new ArrayList<TvPayreckBankListDto>();
						subDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
						mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
						// ����
						mainDto = (TvPayreckBankDto) CommonFacade.getODB()
								.findRsByDto(mainDto).get(0);
						subList = CommonFacade.getODB().findRsByDto(subDto);
						ArrayList<String> expFuncCodeList = new ArrayList<String>();
						List agencyCodeList =  new ArrayList<String>();
						// �ӱ�Ԥ�㵥λ�����б�
						for (TvPayreckBankListDto sDto : subList) {
							expFuncCodeList.add(sDto.getSfuncbdgsbtcode());
							agencyCodeList.add(mainDto.getStrecode()+sDto.getSbdgorgcode());
						}
						list.add(mainDto);
						list.add(vDto);
						list.add(expFuncCodeList);
						list.add(agencyCodeList);
						list.add(subList);
						lists.add(list);
					
				} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)) {

						TvPayreckBankBackDto mainDto = new TvPayreckBankBackDto();
						TvPayreckBankBackListDto subDto = new TvPayreckBankBackListDto();
						List<TvPayreckBankBackListDto> subList = new ArrayList<TvPayreckBankBackListDto>();
						subDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
						mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
						// ����
						mainDto = (TvPayreckBankBackDto) CommonFacade.getODB().findRsByDto(mainDto).get(0);
						subList = CommonFacade.getODB().findRsByDto(subDto);
						ArrayList<String> expFuncCodeList = new ArrayList<String>();
						List agencyCodeList =  new ArrayList<String>();//Ԥ�㵥λlist
						// �ӱ�Ԥ�㵥λ�����б�
						for (TvPayreckBankBackListDto sDto : subList) {
							expFuncCodeList.add(sDto.getSfuncbdgsbtcode());
							agencyCodeList.add(mainDto.getStrecode()+sDto.getSbdgorgcode());
						}
						list.add(mainDto);
						list.add(vDto);
						list.add(expFuncCodeList);
						list.add(agencyCodeList);
						lists.add(list);				
				}else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)) {
					List<TvGrantpaymsgmainDto> mainDtoList = new ArrayList<TvGrantpaymsgmainDto>();
					TvGrantpaymsgmainDto mainDto = new TvGrantpaymsgmainDto();
					TvGrantpaymsgsubDto subDto = new TvGrantpaymsgsubDto();
					List<TvGrantpaymsgsubDto> subList = new ArrayList<TvGrantpaymsgsubDto>();
					mainDto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
					
					//���ܿ�Ŀ�����б�
					ArrayList<String> expFuncCodeList = new ArrayList<String>();
					// �ӱ�Ԥ�㵥λ�����б�
					List agencyCodeList =  new ArrayList<String>();
					// ����
					mainDtoList = CommonFacade.getODB().findRsByDto(mainDto);

					if (null!=mainDtoList &&  mainDtoList.size()> 0) {
						mainDto =mainDtoList.get(0);
					}
					//��Ȩ֧������漰��ƾ֤��֣�����Ҫ��ѯ��mainDtoList
					if(mainDtoList!=null&&mainDtoList.size()>0)
						mainDto = mainDtoList.get(0);
					for(TvGrantpaymsgmainDto tempMainDto:mainDtoList){
						subDto.setIvousrlno(tempMainDto.getIvousrlno());
						List<TvGrantpaymsgsubDto> tempSubDtoList = CommonFacade.getODB().findRsByDto(subDto);
						if(tempSubDtoList!=null&&tempSubDtoList.size()>0)
							subList.addAll(tempSubDtoList);
						for (TvGrantpaymsgsubDto sDto : tempSubDtoList) {
							agencyCodeList.add(mainDto.getStrecode()+sDto.getSbudgetunitcode());
							expFuncCodeList.add(sDto.getSfunsubjectcode());
						}
						if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0){
							mainDto.setNmoney(vDto.getNmoney());
						}
					}
					list.add(mainDto);
					list.add(vDto);
					list.add(expFuncCodeList);
					list.add(agencyCodeList);
					list.add(subList);
					lists.add(list);
				}else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)) {	
					TvDirectpaymsgmainDto mainDto = new TvDirectpaymsgmainDto();
					TvDirectpaymsgsubDto subDto = new TvDirectpaymsgsubDto();
					List<TvDirectpaymsgsubDto> subList = new ArrayList<TvDirectpaymsgsubDto>();
					subDto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
					mainDto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
					// ����
					mainDto = (TvDirectpaymsgmainDto) CommonFacade.getODB()
								.findRsByDto(mainDto).get(0);					
					subList = CommonFacade.getODB().findRsByDto(subDto);
					ArrayList<String> expFuncCodeList = new ArrayList<String>();
					List agencyCodeList =  new ArrayList<String>();
					// �ӱ�Ԥ�㵥λ�����б�
					for (TvDirectpaymsgsubDto sDto : subList) {
						expFuncCodeList.add(sDto.getSfunsubjectcode());
						agencyCodeList.add(mainDto.getStrecode()+sDto.getSagencycode());
					}
					list.add(mainDto);
					list.add(vDto);
					list.add(expFuncCodeList);
					list.add(agencyCodeList);
					list.add(subList);
					lists.add(list);
				}else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5209)) {			
					TvDwbkDto mainDto = new TvDwbkDto();					
					mainDto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
					// ����
					mainDto = (TvDwbkDto) CommonFacade.getODB()
								.findRsByDto(mainDto).get(0);					
					
					ArrayList<String> expFuncCodeList = new ArrayList<String>();
					// Ԥ���Ŀ�����б�
					expFuncCodeList.add(mainDto.getSbdgsbtcode());
					list.add(mainDto);
					list.add(vDto);
					list.add(expFuncCodeList);
					lists.add(list);
				}else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)) {
					TfDirectpaymsgmainDto mainDto = new TfDirectpaymsgmainDto();
					mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
					// ����
					mainDto = (TfDirectpaymsgmainDto) DatabaseFacade.getODB().find(mainDto);
					//�ӱ�
					List subList = PublicSearchFacade.findSubDtoByMain(mainDto);
					ArrayList<String> expFuncCodeList = new ArrayList<String>();
					ArrayList<String> agencyCodeList = new ArrayList<String>();					
					for (TfDirectpaymsgsubDto sDto : (List<TfDirectpaymsgsubDto>)subList) {
						expFuncCodeList.add(sDto.getSexpfunccode());
						agencyCodeList.add(mainDto.getStrecode()+sDto.getSagencycode());
					}
					list.add(mainDto);
					list.add(vDto);
					list.add(expFuncCodeList);//�������Ŀ����list
					list.add(agencyCodeList);//Ԥ�㵥λlist
					list.add(subList);
					lists.add(list);					
				}else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)) {
					TfPaymentDetailsmainDto mainDto = new TfPaymentDetailsmainDto();
					mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
					// ����
					mainDto = (TfPaymentDetailsmainDto) DatabaseFacade.getODB().find(mainDto);
					//�ӱ�
					List subList = PublicSearchFacade.findSubDtoByMain(mainDto);
					VoucherCompare voucherCompare = new VoucherCompare();
					voucherCompare.VoucherCompare(vDto, mainDto, subList);
					count+=voucherCompare.getCount();
				}else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5253)) {
					TfDirectpayAdjustmainDto mainDto = new TfDirectpayAdjustmainDto();
					mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
					// ����
					mainDto = (TfDirectpayAdjustmainDto) DatabaseFacade.getODB().find(mainDto);
					//�ӱ�
					List subList = PublicSearchFacade.findSubDtoByMain(mainDto);
					ArrayList<String> expFuncCodeList = new ArrayList<String>();
					ArrayList<String> agencyCodeList = new ArrayList<String>();					
					for (TfDirectpayAdjustsubDto sDto : (List<TfDirectpayAdjustsubDto>)subList) {
						expFuncCodeList.add(sDto.getSexpfunccode());
						agencyCodeList.add(mainDto.getStrecode()+sDto.getSagencycode());
					}
					list.add(mainDto);
					list.add(vDto);
					list.add(expFuncCodeList);//�������Ŀ����list
					list.add(agencyCodeList);//Ԥ�㵥λlist
					list.add(subList);
					lists.add(list);					
				}else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2252)) {
					TfPaybankRefundmainDto mainDto = new TfPaybankRefundmainDto();
					mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
					// ����
					mainDto = (TfPaybankRefundmainDto) DatabaseFacade.getODB().find(mainDto);
					//�ӱ�
					List subList = PublicSearchFacade.findSubDtoByMain(mainDto);					
					list.add(mainDto);
					list.add(vDto);
					list.add(subList);
					lists.add(list);					
				}else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5351)) {
					TfGrantpayAdjustmainDto mainDto = new TfGrantpayAdjustmainDto();
					mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
					// ����
					mainDto = (TfGrantpayAdjustmainDto) DatabaseFacade.getODB().find(mainDto);
					//�ӱ�
					List subList = PublicSearchFacade.findSubDtoByMain(mainDto);
					ArrayList<String> expFuncCodeList = new ArrayList<String>();
					ArrayList<String> agencyCodeList = new ArrayList<String>();					
					for (TfGrantpayAdjustsubDto sDto : (List<TfGrantpayAdjustsubDto>)subList) {
						expFuncCodeList.add(sDto.getSexpfunccode());
						agencyCodeList.add(mainDto.getStrecode()+sDto.getSsupdepcode());
					}
					list.add(mainDto);
					list.add(vDto);
					list.add(expFuncCodeList);//�������Ŀ����list
					list.add(agencyCodeList);//Ԥ�㵥λlist
					list.add(subList);
					lists.add(list);					
				}else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5407)) {
					TvInCorrhandbookDto mainDto = new TvInCorrhandbookDto();
					mainDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
					// ����
					mainDto = (TvInCorrhandbookDto) DatabaseFacade.getODB().find(mainDto);
					//�ӱ�
					List subList = new ArrayList();					
					list.add(mainDto);
					list.add(vDto);
					list.add(subList);
					lists.add(list);					
				}else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5671)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5408)) {
					TvNontaxmainDto mainDto = new TvNontaxmainDto();
					mainDto.setSorgcode(vDto.getSorgcode());
					mainDto.setScommitdate(vDto.getScreatdate());
					mainDto.setSdealno(vDto.getSdealno());
					// ����
					mainDto = (TvNontaxmainDto) DatabaseFacade.getODB().find(mainDto);
					//�ӱ�
					List subList = PublicSearchFacade.findSubDtoByMain(mainDto);
					ArrayList<String> expFuncCodeList = new ArrayList<String>();
					for(TvNontaxsubDto sDto : (List<TvNontaxsubDto>)subList){
						expFuncCodeList.add(sDto.getSbudgetsubject());
					}
					list.add(mainDto);
					list.add(vDto);
					list.add(expFuncCodeList);
					lists.add(list);					
				}else
					throw new ITFEBizException("û�ж����ҵ������!");
			}
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("����������Ϣ�쳣��",e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("����������Ϣ�쳣��",e);
		}
		
		count += super.voucherVerify(lists, ((TvVoucherinfoDto) checkList
				.get(0)).getSvtcode());
		return count;
	}
	
	/**
	 * �ֹ���ȡƾ֤
	 * @param svtcode
	 * @param sorgcode
	 * @return
	 */
	private int readVoucher(String svtcode, String sorgcode){
		// ���ö�ȡ�ӿ�
		int count = 0;
		SQLExecutor sqlExecutor = null;
		SQLResults rs = null;
		String allVtcodes = "SELECT c.S_ORGCODE,c.S_FINORGCODE,c.S_ADMDIVCODE,v.S_VTCODE,c.S_TRECODE FROM TS_CONVERTFINORG c JOIN TS_VOUCHERCOMMITAUTO v ON c.S_TRECODE=v.S_TRECODE WHERE c.S_ORGCODE= ? ";
		String singleVtcode = "SELECT c.S_ORGCODE,c.S_FINORGCODE,c.S_ADMDIVCODE,v.S_VTCODE,c.S_TRECODE FROM TS_CONVERTFINORG c JOIN TS_VOUCHERCOMMITAUTO v ON c.S_TRECODE=v.S_TRECODE WHERE c.S_ORGCODE= ? AND v.S_VTCODE = ?";
		try {
			sqlExecutor = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			if (svtcode != null && !svtcode.equals("")) {
				sqlExecutor.clearParams();
				sqlExecutor.addParam(sorgcode);
				sqlExecutor.addParam(svtcode);
				rs = sqlExecutor.runQueryCloseCon(singleVtcode);
			} else {
				sqlExecutor.clearParams();
				sqlExecutor.addParam(sorgcode);
				rs = sqlExecutor.runQueryCloseCon(allVtcodes);
			}

			List list = new ArrayList();
			List auto = new ArrayList();
			if (rs != null && rs.getRowCount() > 0) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					auto.add(rs.getString(i, 0));
					auto.add(rs.getString(i, 1));
					auto.add(rs.getString(i, 2));
					auto.add(rs.getString(i, 3));
					auto.add(rs.getString(i, 4));
					list.add(auto);
				}
				count = super.voucherRead(list);
			}

		} catch (JAFDatabaseException e) {
			logger.equals(e);
		} finally {
			if (sqlExecutor != null) {
				sqlExecutor.closeConnection();
			}
		}
		// �˴����ض�ȡ��ƾ֤����
		return count;
	}
	
	/**
	 * ���ڱ��ص���ʹ��
	 * @return
	 */
	private int TestLocalFile(){
		//���ص���ʹ�ã������Ѿ�base64�Ժ�ı���
		String ls_AdmDiveCode = "310000";
		String FileName = "F:/rev22521.msg";
		String TreCode = "0100000000";
		String ls_OrgCode = "030500000003";
		String ls_FinOrgCode = "1311000000";
		HashMap<String, String> dealnos = new HashMap<String, String>();
		TvVoucherinfoDto voucherDto = new TvVoucherinfoDto();
		try {
			
			String voucherXml = FileUtil.getInstance().readFile(FileName);
			Document fxrDoc = DocumentHelper.parseText(voucherXml);
			List listNodes = fxrDoc.selectSingleNode("MOF").selectNodes(
					"VoucherBody");
			int li_CountCurrent = Integer.parseInt(fxrDoc.selectSingleNode(
					"MOF").selectSingleNode("VoucherCount").getText());
			for (int i = 0; i < listNodes.size(); i++) {
				String bodyVoucher = ((Node) listNodes.get(i)).asXML();
				Element e = (Element) listNodes.get(i);
				Document fxrDocVoucher = DocumentHelper.parseText(bodyVoucher);

				Node voucherNode = fxrDocVoucher
						.selectSingleNode("VoucherBody").selectSingleNode(
								"Voucher");
				String voucher = voucherNode.asXML();
				String StYear = e.attribute("StYear").getText();
				String VtCode = e.attribute("VtCode").getText();
				String VoucherNo = e.attribute("VoucherNo").getText();
				String VoucherFlag = ((Element) listNodes.get(i))
						.elementText("VoucherFlag");
				String Attach = ((Element) listNodes.get(i))
						.elementText("Attach");

				// �Ϻ���ֽ�������޹������ڵ�
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") < 0
						|| VtCode.equals(MsgConstant.VOUCHER_NO_2252)) {
//					TreCode = voucherNode.selectSingleNode("TreCode").getText();
				}
				String billOrgCode = VoucherUtil.getSpaybankcode(VtCode,
						voucherNode);// ��Ʊ��λ
				billOrgCode = (VtCode.equals(MsgConstant.VOUCHER_NO_5201) && (StringUtils
						.isBlank(billOrgCode))) ? ls_FinOrgCode : billOrgCode;// 5201��Ʊ��λȡ��������
				String Total = VoucherUtil.getTotalAmt(VtCode, voucherNode);
				String mainvou = VoucherUtil.getGrantSequence();
				dealnos.put(VoucherNo, mainvou);
				voucherDto = new TvVoucherinfoDto();
				voucherDto.setSdealno(mainvou);
				voucherDto.setNmoney(MtoCodeTrans.transformBigDecimal(Total));
				voucherDto.setSadmdivcode(ls_AdmDiveCode);
				voucherDto.setScreatdate(TimeFacade.getCurrentStringTime());
				voucherDto.setSrecvtime(new Timestamp(new java.util.Date()
						.getTime()));
				voucherDto.setSfilename(FileName);
				voucherDto.setSorgcode(ls_OrgCode);
				voucherDto.setSattach(Attach);
				voucherDto.setSstatus(DealCodeConstants.VOUCHER_ACCEPT);
				voucherDto.setSdemo("���������쳣");
				voucherDto.setSstyear(TimeFacade.getCurrentStringTime()
						.substring(0, 4));
				voucherDto.setStrecode(TreCode);
				voucherDto.setSvoucherflag("1");
				voucherDto.setSvoucherno(VoucherNo);
				voucherDto.setSvtcode(VtCode);
				voucherDto.setSpaybankcode(billOrgCode); // ��Ʊ��λ

				DatabaseFacade.getDb().create(voucherDto);

				String savexml = voucherXml;

				String Xml = savexml.replaceAll("&lt;&lt;", "��").replaceAll(
						"&gt;&gt;", "��").replaceAll("&lt;", "��").replaceAll(
						"&gt;", "��");
				VoucherUtil.sendTips(voucherDto, ls_FinOrgCode, dealnos,
						savexml);
				}
			
			} catch (Exception e) {
				logger.equals(e);
				e.printStackTrace();
			} finally {
				logger.equals("*******************�������**********************8");
			}
		return 0;

	}
}

package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvPayreckbankSxDto;
import com.cfcc.itfe.persistence.dto.TvPayreckbanklistSxDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoSxDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.sxservice.VoucherVerifySX;
import com.cfcc.itfe.xmlparse.AbstractXmlParser;
import com.cfcc.jaf.common.util.DateUtil;

public class VoucherSX2301MsgServer extends AbstractXmlParser {

	private static Log logger = LogFactory.getLog(VoucherSX2301MsgServer.class);
	private Voucher voucher;
	/**
	 * ���Ľ���������
	 * @param map  �ؼ���ϢMAP
	 * 		  key����orgcode��-value:��String����������
	 * 		  key����filename��-value:��String�� �ļ���
	 *        key: ��dealnomap��-value:��HashMap<String,String>��������ˮ��MAP   
	 * @param xmlString ������Ϣ
	 * @author sunyan
	 */
	public void dealMsg(HashMap map, String xmlString) throws ITFEBizException {
		String voucherXml = xmlString;
		String ls_FileName = (String) map.get("filename");
		String ls_OrgCode = (String) map.get("orgcode");
		HashMap<String,String> dealnos=(HashMap<String,String>)map.get("dealnomap");
		
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("�������뻮��ƾ֤�ص�2301���ĳ��ִ���", e);
			throw new ITFEBizException("�������뻮��ƾ֤�ص�2301���ĳ��ִ���", e);

		}
		List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes(
				"VoucherBody");
		String AdmDivCode = "";// ������������
		String StYear = "";// ���
		String VtCode = "";// ƾ֤����
		Date currentDate = TimeFacade.getCurrentDateTime();// ��ǰϵͳ����
		List<String> voucherList = new ArrayList<String>();
		
		// ��ȡ�����������롢��Ⱥ�ƾ֤����
		if (VoucherBodyList.size() > 0) {
			Element element = (Element) VoucherBodyList.get(0);
			AdmDivCode = element.attribute("AdmDivCode").getText();
			StYear = element.attribute("StYear").getText();
			VtCode = element.attribute("VtCode").getText();
		}
		
		// Ԥ�㵥λ����list
		ArrayList<String> agencyCodeList = null;
		// Ԥ���Ŀ����list
		ArrayList<String> expFuncCodeList = null;
		
		String SupDepCode = "";
		TvPayreckbankSxDto maindto = null;
		List subDtoList = null;
		List lists = new ArrayList();
		List list = null;
		// ƾ֤���к�
		String mainvou = "";
		TvVoucherinfoSxDto vDto = new TvVoucherinfoSxDto();

		// ��������
		for (int i = 0; i < VoucherBodyList.size(); i++) {
			try {
				// ��ϸ�ϼƽ��
				BigDecimal sumAmt = new BigDecimal("0.00");
				// VoucherBody
				Element element = (Element) VoucherBodyList.get(i);
				// Voucher
				Element elementVoucher = (Element) element
						.selectSingleNode("Voucher");
				// ��ϸ��ϢList
				List listDetail = elementVoucher.selectSingleNode("DetailList")
						.selectNodes("Detail");
				//����У����Ϣdto
				VoucherVerifyDto verifydto = new VoucherVerifyDto();
				VoucherVerifySX voucherVerifySX = new VoucherVerifySX();	
				/**
				 * ƾ֤��Ϣ
				 */
				String Attach = ((Element) VoucherBodyList.get(i))
						.elementText("Attach");// ������Ϣ
				String Id = elementVoucher.elementText("Id");// ���뻮��ƾ֤Id
				String VouDate = elementVoucher.elementText("VouDate"); // ƾ֤����
				String VoucherNo = elementVoucher.elementText("VoucherNo");// ƾ֤��
				String TreCode = elementVoucher.elementText("TreCode"); // �����������
				String FinOrgCode = elementVoucher.elementText("FinOrgCode");// �������ش���
				String BgtTypeCode = elementVoucher.elementText("BgtTypeCode");// Ԥ�����ͱ���
				String BgtTypeName = elementVoucher.elementText("BgtTypeName");// Ԥ����������
				String FundTypeCode = elementVoucher
						.elementText("FundTypeCode");// �ʽ����ʱ���
				String FundTypeName = elementVoucher
						.elementText("FundTypeName");// �ʽ���������
				String PayTypeCode = elementVoucher.elementText("PayTypeCode");// ֧����ʽ����
				String PayTypeName = elementVoucher.elementText("PayTypeName");// ֧����ʽ����
				String AgentAcctNo = elementVoucher.elementText("AgentAcctNo");// �տ������˺�
				String AgentAcctName = elementVoucher
						.elementText("AgentAcctName");// �տ������˻�����
				String AgentAcctBankName = elementVoucher
						.elementText("AgentAcctBankName");// �տ�����
				String ClearAcctNo = elementVoucher.elementText("ClearAcctNo");// �����˺�
				String ClearAcctName = elementVoucher
						.elementText("ClearAcctName");// �����˻�����
				String ClearAcctBankName = elementVoucher
						.elementText("ClearAcctBankName");// ��������
				String PayAmt = elementVoucher.elementText("PayAmt");// ����������
				String PayBankName = elementVoucher.elementText("PayBankName");// ������������
				String PayBankNo = elementVoucher.elementText("PayBankNo");// ���������к�
				String Remark = elementVoucher.elementText("Remark");// ժҪ
				String MoneyCorpCode = elementVoucher
						.elementText("MoneyCorpCode");// ���ڻ�������
				String XPaySndBnkNo = elementVoucher
						.elementText("XPaySndBnkNo");// ֧���������к�
				String XAddWord = elementVoucher.elementText("XAddWord");// ����
				String XClearDate = elementVoucher.elementText("XClearDate");// ��������
				String XPayAmt = elementVoucher.elementText("XPayAmt");// ����������
				String Hold1 = elementVoucher.elementText("Hold1");// Ԥ���ֶ�1
				String Hold2 = elementVoucher.elementText("Hold2");// Ԥ���ֶ�2

				/**
				 * ��װTvPayreckBankSxDto����
				 **/
				maindto = new TvPayreckbankSxDto();
				mainvou = dealnos.get(VoucherNo);// ��ȡ���к�
				maindto.setStrano(mainvou.substring(8, 16));// ���뻮��ƾ֤Id
				maindto.setIvousrlno(Long.valueOf(mainvou));// ƾ֤��ˮ��
				maindto.setSid(mainvou.substring(8, 16));// ���뻮��ƾ֤Id
				maindto.setSadmdivcode(AdmDivCode);//������������
				maindto.setSofyear(StYear);// ҵ�����
				maindto.setSvtcode(VtCode);//ƾ֤���ͱ��\
				SimpleDateFormat f=new SimpleDateFormat("yyyyMMdd");
				maindto.setSbookorgcode(ls_OrgCode);//�����������
				maindto.setDvoudate(CommonUtil.strToDate(VouDate)); // ƾ֤����
				maindto.setSvouno(VoucherNo);// ƾ֤��
				maindto.setStrecode(TreCode); // �����������
				maindto.setSfinorgcode(FinOrgCode);// �������ش���
				maindto.setSbgttypecode(BgtTypeCode);// Ԥ�����ͱ���
				maindto.setSbgttypename(BgtTypeName);// Ԥ����������
				maindto.setSfundtypecode(FundTypeCode);// �ʽ����ʱ���
				maindto.setSfundtypename(FundTypeName);// �ʽ���������
				maindto.setSpaytypecode(PayTypeCode);// ֧����ʽ����
				if("12".equals(PayTypeCode)){
					maindto.setSpaymode("1");// ��Ȩ֧��
				}else if("11".equals(PayTypeCode)){
					maindto.setSpaymode("0");// ֧����ʽ����    ֱ��֧��
				}
				maindto.setSpaytypename(PayTypeName);// ֧����ʽ����
				maindto.setSpayeeacct(AgentAcctNo);// �տ������˺�
				maindto.setSpayeename(AgentAcctName);// �տ������˻�����
				maindto.setSagentacctbankname(AgentAcctBankName);// �տ�����
				maindto.setSpayeracct(ClearAcctNo);// �����˺�
				maindto.setSpayername(ClearAcctName);// �����˻�����
				maindto.setSclearacctbankname(ClearAcctBankName);// ��������
				maindto.setFamt(BigDecimal.valueOf(Double.valueOf(PayAmt)));// ����������
				maindto.setSpaybankname(PayBankName);// ������������
				maindto.setSagentbnkcode(PayBankNo);// ���������к�
				maindto.setSdescription(Remark);// ժҪ
				maindto.setSmoneycorpcode(MoneyCorpCode);// ���ڻ�������
				maindto.setSxpaysndbnkno(XPaySndBnkNo);// ֧���������к�
				maindto.setSaddword(XAddWord);// ����
				maindto.setSxcleardate(CommonUtil.strToDate(XClearDate));// ��������
				if(XPayAmt!=null && !XPayAmt.equals("")){
					maindto.setSxpayamt(BigDecimal.valueOf(Double.valueOf(XPayAmt)));// ����������
				}else{
					maindto.setSxpayamt(BigDecimal.valueOf(Double.valueOf("0.00")));// ����������
				}
				if("1".equals(ITFECommonConstant.ISCHECKPAYPLAN)){
					maindto.setSxpayamt(BigDecimal.valueOf(Double.valueOf(PayAmt)));
				}
				maindto.setShold1(Hold1);// Ԥ���ֶ�1
				maindto.setShold2(Hold2);// Ԥ���ֶ�2
				maindto.setDentrustdate(DateUtil.currentDate());//ί������
				maindto.setSpackno("0000");//����ˮ��
				maindto.setStrimsign(MsgConstant.TIME_FLAG_NORMAL);//�����ڱ�־
				maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);//Ԥ������(Ĭ��Ԥ����)
				maindto.setSpayeeopbkno(PayBankNo);//�տ��˿������к�
				maindto.setSfilename(ls_FileName);
				maindto.setSresult(DealCodeConstants.DEALCODE_ITFE_DEALING);//״̬ ������
				maindto.setTsupdate(new Timestamp(new java.util.Date()
						.getTime()));// ϵͳʱ��
				// Ԥ�㵥λ����list
				agencyCodeList = new ArrayList<String>();
				// Ԥ���Ŀ����list
				expFuncCodeList = new ArrayList<String>();
				/**
				 * ��װTvPayreckBankListDto����
				 */
				subDtoList = new ArrayList<TvPayreckbanklistSxDto>();
				for (int j = 0; j < listDetail.size(); j++) {
					Element elementDetail = (Element) listDetail.get(j);
					String VoucherNol = elementDetail.elementText("VoucherNo");// ֧��ƾ֤����
					SupDepCode = elementDetail.elementText("SupDepCode");// һ��Ԥ�㵥λ����
					String SupDepName = elementDetail.elementText("SupDepName");// һ��Ԥ�㵥λ����
					String ExpFuncCode = elementDetail
							.elementText("ExpFuncCode");// ֧�����ܷ����Ŀ����
					String ExpFuncName = elementDetail
							.elementText("ExpFuncName");// ֧�����ܷ����Ŀ����
					String sPayAmt = elementDetail.elementText("PayAmt");// ֧�����
					String PaySummaryName = elementDetail
							.elementText("PaySummaryName");// ժҪ����
					String sdetailHold1 = elementDetail.elementText("Hold1");// Ԥ���ֶ�1
					String sdetailHold2 = elementDetail.elementText("Hold2");// Ԥ���ֶ�2
					String sdetailHold3 = elementDetail.elementText("Hold3");// Ԥ���ֶ�3
					String sdetailHold4 = elementDetail.elementText("Hold4");// Ԥ���ֶ�4
					TvPayreckbanklistSxDto subdto = new TvPayreckbanklistSxDto();
					// �˴�����ֵ��ȷ��
					subdto.setIseqno(j+1);//���
					subdto.setSacctprop(MsgConstant.ACCT_PROP_ZERO);//�˻�����
					subdto.setIvousrlno(Long.valueOf(mainvou));// �ӱ����к�
					subdto.setSvouchern0(VoucherNol);// �ӱ���ϸ���
					// ֧��ƾ֤����
					subdto.setSbdgorgcode(SupDepCode);// һ��Ԥ�㵥λ����
					subdto.setSsupdepname(SupDepName);// һ��Ԥ�㵥λ����
					subdto.setSfuncbdgsbtcode(ExpFuncCode);// ֧�����ܷ����Ŀ����
					subdto.setSexpfuncname(ExpFuncName);// ֧�����ܷ����Ŀ����
					subdto.setFamt(BigDecimal.valueOf(Double.valueOf(sPayAmt)));// ֧�����
					subdto.setSpaysummaryname(PaySummaryName);// ժҪ����
					subdto.setShold1(sdetailHold1);// Ԥ���ֶ�1
					subdto.setShold2(sdetailHold2);// Ԥ���ֶ�2
					subdto.setShold3(sdetailHold3);// Ԥ���ֶ�3
					subdto.setShold4(sdetailHold4);// Ԥ���ֶ�4
					subdto.setTsupdate(new Timestamp(new java.util.Date()
					.getTime()));//����ʱ��
					
					//����Ԥ�㵥λ����ĿLIST,��ϸ�ϼ�
					if(!agencyCodeList.contains(TreCode+SupDepCode)){
						agencyCodeList.add(TreCode+SupDepCode);
					}
					expFuncCodeList.add(ExpFuncCode);
					// ��ϸ�ϼ�
					sumAmt = sumAmt.add(BigDecimal.valueOf(Double.valueOf(sPayAmt)));

					subDtoList.add(subdto);
					
				}
				vDto = new TvVoucherinfoSxDto();
				vDto.setSdealno(mainvou);
				vDto = (TvVoucherinfoSxDto) CommonFacade.getODB().findRsByDto(
						vDto).get(0);
				/**
				 * ��װverifydto,���б���У��
				 */
				verifydto.setTrecode(TreCode);
				verifydto.setFinorgcode(FinOrgCode);
				verifydto.setVoucherno(VoucherNo);
				verifydto.setVoudate(VouDate);
				verifydto.setPaytypecode(PayTypeCode);
				verifydto.setPaybankno(PayBankNo);
				verifydto.setAgentAcctNo(AgentAcctNo);
				verifydto.setAgentAcctName(AgentAcctName);
				verifydto.setClearAcctNo(ClearAcctNo);
				verifydto.setClearAcctName(ClearAcctName);
				String returnmsg = voucherVerifySX.checkValid(verifydto, MsgConstant.VOUCHER_NO_2301);
				if(returnmsg != null){//���ش�����Ϣǩ��ʧ��
					voucher.voucherComfailForSX(vDto.getSdealno(), returnmsg);
					continue;
				}
				/**
				 *У���������Ƿ����ӱ������
				 * 
				 */
				if (maindto.getFamt().compareTo(sumAmt) != 0) {
					String errMsg = "�����������ϸ�ۼƽ���ȣ�������" + maindto.getFamt()
							+ " ��ϸ�ۼƽ� " + sumAmt;
					voucher.voucherComfailForSX(vDto.getSdealno(), errMsg);
					continue;
				}
				/**
				 *��ϸ�������ܳ���500
				 * 
				 */
				if (subDtoList.size() > 499) {
					String errMsg = "��ϸ�������ܳ���500";
					voucher.voucherComfailForSX(vDto.getSdealno(), errMsg);
					continue;
				}
				
				maindto.setIstatinfnum(subDtoList.size());//��ϸ��Ϣ����
				/**
				 * ҵ�����⣬У�鱨�ĺϷ���
				 * 
				 * ҵ�������ӱ����ʧ�ܼ�ǩ��ʧ��
				 */
				TvPayreckbanklistSxDto[] subDtos = new TvPayreckbanklistSxDto[subDtoList
						.size()];
				subDtos = (TvPayreckbanklistSxDto[]) subDtoList.toArray(subDtos);
				DatabaseFacade.getODB().create(maindto);
				DatabaseFacade.getODB().create(subDtos);
			}catch(Exception e){
				logger.error(e);
				voucher.voucherComfailForSX(mainvou, "���Ĳ��淶��"+e.getMessage());
				continue;
			}

			list = new ArrayList();
			list.add(maindto);
			list.add(vDto);
			list.add(expFuncCodeList);
			list.add(agencyCodeList);//Ԥ�㵥λlist
			lists.add(list);
		}

		/**
		 * У��ƾ֤��Ϣģ��
		 */
		try {
			if(lists.size()>0){
				voucher.voucherVerifyForSX(lists, VtCode);
			}
			
		} catch (ITFEBizException e) {
			logger.error(e);
			throw new ITFEBizException(e);
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException("У��ƾ֤����"+VtCode+"�����쳣",e);
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

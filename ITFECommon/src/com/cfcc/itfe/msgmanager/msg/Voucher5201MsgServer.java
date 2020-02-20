package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanknameDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;


public class Voucher5201MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher8207MsgServer.class);
	private Voucher voucher;
	/**
	 * �����������еĲ���ֱ��֧��ƾ֤5201������ֻ����ҵ������
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();
		String voucherXml = (String) muleMessage.getPayload();
		String ls_FileName = (String) muleMessage.getProperty("fileName");
		String ls_OrgCode = (String) muleMessage.getProperty("orgCode");
		String ls_FinOrgCode = (String) muleMessage.getProperty("finOrgCode");
		HashMap<String,String> dealnos=(HashMap<String,String>)muleMessage.getProperty("dealnos");
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("��������ֱ��֧��ƾ֤5201���ĳ��ִ���",e);
			throw new ITFEBizException("��������ֱ��֧��ƾ֤5201���ĳ��ִ���",e);
			
		}
		Map<String, TsConvertbanknameDto> bankInfo=null;
		HashMap<String, TsPaybankDto>  bankmap = null;
		try {
			bankInfo = BusinessFacade.getBankInfo(ls_OrgCode);//���������ձ��ȡ�����������������к���Ϣ
			bankmap = SrvCacheFacade.cachePayBankInfo();
		} catch (JAFDatabaseException e1) {
			logger.error("��ȡ����������Ϣ����"+e1);
		} catch (ValidateException e1) {
			logger.error("��ȡ����������Ϣ����"+e1);
		}
		List VoucherBodyList  = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
		String StYear  = "";//���
		String VtCode  = "";//ƾ֤����
		
		String currentDate = TimeFacade.getCurrentStringTime();// ��ǰϵͳ����
		List<String> voucherList = new ArrayList<String>();
		
		//��ȡ�����������롢��Ⱥ�ƾ֤����
		if(VoucherBodyList.size()>0){
			Element element  = (Element)VoucherBodyList.get(0);
			StYear  = element.attribute("StYear").getText();
			VtCode  = element.attribute("VtCode").getText();
		}
		// Ԥ�㵥λ����list
		ArrayList<String> agencyCodeList = null;
		// �������Ŀ����list
		ArrayList<String> expFuncCodeList = null;
		
		TfDirectpaymsgmainDto maindto  = null;
		List subDtoList=null; 
		List lists = new ArrayList();
		List list = null;
		
		//ƾ֤���к�
		String mainvou="";
		TvVoucherinfoDto vDto=new TvVoucherinfoDto();
		//��������
		for(int i=0;i<VoucherBodyList.size();i++){
			try{
				
				//��ϸ�ϼƽ��
				BigDecimal sumAmt = new BigDecimal("0.00");	
				//VoucherBody
				Element element  = (Element)VoucherBodyList.get(i);
				// ����ͷ�뱨����ƾ֤��Ų�һ�µ���ǩ���쳣�����ģ�
				String VoucherNo = element.attribute("VoucherNo").getText();//ƾ֤���
				//Voucher
				Element elementVoucher  = (Element)element.selectSingleNode("Voucher");
				//��ϸ��ϢList
				List listDetail  = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");
				//����У����Ϣdto
				VoucherVerifyDto verifydto = new VoucherVerifyDto();
				VoucherVerify voucherVerify = new VoucherVerify();	
				/**
				 * ƾ֤��Ϣ
				 */
				//String VoucherNo  = elementVoucher.elementText("VoucherNo");//ƾ֤��
				String Id=elementVoucher.elementText("Id");//				
				String VouDate = elementVoucher.elementText("VouDate"); //ƾ֤����
				String FundTypeCode = elementVoucher.elementText("FundTypeCode"); //�ʽ����ʱ���
				String FundTypeName = elementVoucher.elementText("FundTypeName"); //�ʽ���������
				String BgtTypeCode=elementVoucher.elementText("BgtTypeCode");//Ԥ�����ͱ���
				String BgtTypeName=elementVoucher.elementText("BgtTypeName");//Ԥ����������
				String PayTypeCode = elementVoucher.elementText("PayTypeCode"); //֧����ʽ����
				String PayTypeName = elementVoucher.elementText("PayTypeName"); //֧����ʽ����
				String ProCatCode=elementVoucher.elementText("ProCatCode");//��֧�������
				String ProCatName=elementVoucher.elementText("ProCatName");//��֧��������
				String MOFDepCode=elementVoucher.elementText("MOFDepCode");//ҵ���ұ���
				String MOFDepName=elementVoucher.elementText("MOFDepName");//ҵ��������
				String FileNoCode=elementVoucher.elementText("FileNoCode");//ָ���ĺű���
				String FileNoName=elementVoucher.elementText("FileNoName");//ָ���ĺ�����
				String SupDepCode=elementVoucher.elementText("SupDepCode");//һ��Ԥ�㵥λ����
				String SupDepName=elementVoucher.elementText("SupDepName");//һ��Ԥ�㵥λ����
				String AgencyCode = elementVoucher.elementText("AgencyCode"); //����Ԥ�㵥λ����
				String AgencyName = elementVoucher.elementText("AgencyName"); //����Ԥ�㵥λ����
				String ExpFuncCode=elementVoucher.elementText("ExpFuncCode");//֧�����ܷ����Ŀ����
				String ExpFuncName=elementVoucher.elementText("ExpFuncName");//֧�����ܷ����Ŀ����
				String ExpFuncCode1=elementVoucher.elementText("ExpFuncCode1");//֧�����ܷ����Ŀ����
				String ExpFuncName1=elementVoucher.elementText("ExpFuncName1");//֧�����ܷ����Ŀ����
				String ExpFuncCode2=elementVoucher.elementText("ExpFuncCode2");//֧�����ܷ����Ŀ����
				String ExpFuncName2=elementVoucher.elementText("ExpFuncName2");//֧�����ܷ����Ŀ����
				String ExpFuncCode3=elementVoucher.elementText("ExpFuncCode3");//֧�����ܷ����Ŀ����
				String ExpFuncName3=elementVoucher.elementText("ExpFuncName3");//֧�����ܷ����Ŀ����
				String ExpEcoCode=elementVoucher.elementText("ExpEcoCode");//֧�����÷����Ŀ����
				String ExpEcoName=elementVoucher.elementText("ExpEcoName");//֧�����÷����Ŀ����
				String ExpEcoCode1=elementVoucher.elementText("ExpEcoCode1");//֧�����÷����Ŀ����
				String ExpEcoName1=elementVoucher.elementText("ExpEcoName1");//֧�����÷����Ŀ����
				String ExpEcoCode2=elementVoucher.elementText("ExpEcoCode2");//֧�����÷����Ŀ����
				String ExpEcoName2=elementVoucher.elementText("ExpEcoName2");//֧�����÷����Ŀ����
				String DepProCode=elementVoucher.elementText("DepProCode");//Ԥ����Ŀ����
				String DepProName=elementVoucher.elementText("DepProName");//Ԥ����Ŀ����
				String SetModeCode=elementVoucher.elementText("SetModeCode");//���㷽ʽ����
				String SetModeName=elementVoucher.elementText("SetModeName");//���㷽ʽ����
				String PayBankCode = elementVoucher.elementText("PayBankCode"); //�������б���
				String PayBankName = elementVoucher.elementText("PayBankName"); //������������
				String ClearBankCode=elementVoucher.elementText("ClearBankCode");//�������б���
				String ClearBankName=elementVoucher.elementText("ClearBankName");//������������
				String PayeeAcctNo=elementVoucher.elementText("PayeeAcctNo");//�տ����˺�				
				String PayeeAcctName=elementVoucher.elementText("PayeeAcctName");//�տ�������
				String PayeeAcctBankName=elementVoucher.elementText("PayeeAcctBankName");//�տ�������
				String PayeeAcctBankNo=elementVoucher.elementText("PayeeAcctBankNo");//�տ��������к�
				String PayAcctNo = elementVoucher.elementText("PayAcctNo"); //�������˺�
				String PayAcctName = elementVoucher.elementText("PayAcctName"); //����������
				String PayAcctBankName = elementVoucher.elementText("PayAcctBankName"); //����������				
				String PaySummaryCode=elementVoucher.elementText("PaySummaryCode");//��;����
				String PaySummaryName=elementVoucher.elementText("PaySummaryName");//��;����
				String PayAmt=elementVoucher.elementText("PayAmt");//֧�����
				String BusinessTypeCode = elementVoucher.elementText("BusinessTypeCode"); //ҵ�����ͱ���
				String BusinessTypeName = elementVoucher.elementText("BusinessTypeName"); //ҵ����������				
				String CheckNo=elementVoucher.elementText("CheckNo");//֧Ʊ�ţ�����ţ�
				String XPayDate = elementVoucher.elementText("XPayDate"); //ʵ��֧������
				String XAgentBusinessNo=elementVoucher.elementText("XAgentBusinessNo");//���н�����ˮ��
				String XCheckNo=elementVoucher.elementText("XCheckNo");//֧Ʊ��(�����)
				String XPayAmt=elementVoucher.elementText("XPayAmt");//ʵ��֧�����				
				String XPayeeAcctBankName=elementVoucher.elementText("XPayeeAcctBankName");//�տ�������
				String XPayeeAcctNo=elementVoucher.elementText("XPayeeAcctNo");//�տ����˺�
				String XPayeeAcctName = elementVoucher.elementText("XPayeeAcctName"); //�տ���ȫ��						
				String Hold1=elementVoucher.elementText("Hold1");//Ԥ���ֶ�1
				String Hold2=elementVoucher.elementText("Hold2");//Ԥ���ֶ�2
				
				/**
				 * ��װTfPaymentDetailsmainDto����
				 **/
				maindto  = new TfDirectpaymsgmainDto();
				mainvou =dealnos.get(VoucherNo);//��ȡ���к�
				maindto.setIvousrlno(Long.parseLong(mainvou));
				maindto.setSvoudate(VouDate);
				maindto.setSvtcode(VtCode);							
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
				if(PayTypeCode!=null&&"11".equals(PayTypeCode)){
					maindto.setSpaytypecode("0");// ֱ��֧��
				}
				maindto.setSid(Id);
				maindto.setSvoucherno(VoucherNo);
				maindto.setSstyear(StYear);//ҵ�����
				maindto.setSfinorgcode(ls_FinOrgCode);//������������
				maindto.setSdealno(mainvou.substring(8, 16));//������ˮ��
				maindto.setScommitdate(TimeFacade.getCurrentStringTime());//ί������
				maindto.setSfilename(ls_FileName);//�ļ���
				maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);//״̬			
				maindto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//ϵͳʱ��
				vDto=new TvVoucherinfoDto();
				vDto.setSdealno(mainvou);
				vDto=(TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
				vDto.setShold4(BusinessTypeCode);
				maindto.setSorgcode(vDto.getSorgcode());
				maindto.setStrecode(vDto.getStrecode());
				maindto.setSadmdivcode(vDto.getSadmdivcode());
				maindto.setSdemo(vDto.getSattach());
				maindto.setSbackflag("0");//�˻ر�־ 0-δ�˻� 1-���˻�
				//ҵ���ӱ���
				subDtoList=new ArrayList<TfDirectpaymsgsubDto>();
				//�ӱ���ϸId����
				List<String>  subDtoIdList = new ArrayList<String>();
				// Ԥ�㵥λ����list
				agencyCodeList = new ArrayList<String>();
				// �������Ŀ����list
				expFuncCodeList = new ArrayList<String>();
				/**
				 * ��װTfPaymentDetailssubDto����
				 */
				for(int j=0;j<listDetail.size();j++){
					
					Element elementDetail  = (Element)listDetail.get(j);
					String sdetailId = elementDetail.elementText("Id");//��ϸ���
					String VoucherBillId=elementDetail.elementText("VoucherBillId");//����ֱ��֧��ƾ֤Id
					String VoucherBillNo=elementDetail.elementText("VoucherBillNo");//����ֱ��֧��ƾ֤����
					String sdetailVoucherNo=elementDetail.elementText("VoucherNo");//֧���������
					String sdetailFundTypeCode=elementDetail.elementText("FundTypeCode");//�ʽ����ʱ���
					String sdetailFundTypeName=elementDetail.elementText("FundTypeName");//�ʽ���������
					String sdetailBgtTypeCode=elementDetail.elementText("BgtTypeCode");//Ԥ�����ͱ���
					String sdetailBgtTypeName=elementDetail.elementText("BgtTypeName");//Ԥ����������
					String sdetailProCatCode=elementDetail.elementText("ProCatCode");//��֧�������
					String sdetailProCatName=elementDetail.elementText("ProCatName");//��֧��������
					String sdetailPayKindCode=elementDetail.elementText("PayKindCode");//֧�����ͱ���
					String sdetailPayKindName=elementDetail.elementText("PayKindName");//֧����������
					String sdetailMOFDepCode=elementDetail.elementText("MOFDepCode");//ҵ���ұ���
					String sdetailMOFDepName=elementDetail.elementText("MOFDepName");//ҵ��������
					String sdetailFileNoCode=elementDetail.elementText("FileNoCode");//ָ���ĺű���
					String sdetailFileNoName=elementDetail.elementText("FileNoName");//ָ���ĺ�����
					String sdetailSupDepCode=elementDetail.elementText("SupDepCode");//һ��Ԥ�㵥λ����
					String sdetailSupDepName=elementDetail.elementText("SupDepName");//һ��Ԥ�㵥λ����
					String sdetailAgencyCode=elementDetail.elementText("AgencyCode");//����Ԥ�㵥λ����
					String sdetailAgencyName=elementDetail.elementText("AgencyName");//����Ԥ�㵥λ����
					String sdetailExpFuncCode=elementDetail.elementText("ExpFuncCode");//֧�����ܷ����Ŀ����
					String sdetailExpFuncName=elementDetail.elementText("ExpFuncName");//֧�����ܷ����Ŀ����
					String sdetailExpEcoCode=elementDetail.elementText("ExpEcoCode");//֧�����÷����Ŀ����
					String sdetailExpEcoName=elementDetail.elementText("ExpEcoName");//֧�����÷����Ŀ����
					String sdetailDepProCode=elementDetail.elementText("DepProCode");//Ԥ����Ŀ����
					String sdetailDepProName=elementDetail.elementText("DepProName");//Ԥ����Ŀ����			
					String sdetailPayeeAcctNo = elementDetail.elementText("PayeeAcctNo");//�տ����˺�
					String sdetailPayeeAcctName = elementDetail.elementText("PayeeAcctName");//�տ�������
					String sdetailPayeeAcctBankName = elementDetail.elementText("PayeeAcctBankName");//�տ�������
					String sdetailPayeeAcctBankNo = elementDetail.elementText("PayeeAcctBankNo");//�տ��������к�
					String sdetailPayAmt = elementDetail.elementText("PayAmt");//֧�����
					String sdetailRemark = elementDetail.elementText("Remark");//��ע
					String sdetailDetailXPayDate = elementDetail.elementText("XPayDate");//ʵ��֧������
					String sdetailXAgentBusinessNo = elementDetail.elementText("XAgentBusinessNo");//���н�����ˮ��
					String sdetailXCheckNo=elementDetail.elementText("XCheckNo");//֧Ʊ��(�����)
					String sdetailXPayAmt = elementDetail.elementText("XPayAmt");//ʵ��֧�����
					String sdetailXPayeeAcctBankName = elementDetail.elementText("XPayeeAcctBankName");//�տ�������
					String sdetailXPayeeAcctNo = elementDetail.elementText("XPayeeAcctNo");//�տ����˺�
					String sdetailXPayeeAcctName = elementDetail.elementText("XPayeeAcctName");//�տ���ȫ��
					String XAddWord = elementDetail.elementText("XAddWord");//ʧ��ԭ��				
					String sdetailHold1 = elementDetail.elementText("Hold1");//Ԥ���ֶ�1
					String sdetailHold2 = elementDetail.elementText("Hold2");//Ԥ���ֶ�2
					String sdetailHold3 = elementDetail.elementText("Hold3");//Ԥ���ֶ�3
					String sdetailHold4 = elementDetail.elementText("Hold4");//Ԥ���ֶ�4
					TfDirectpaymsgsubDto subdto=new TfDirectpaymsgsubDto();
					
					subdto.setIvousrlno(Long.parseLong(mainvou));
					subdto.setIseqno((long)(j+1));
					subdto.setSid(sdetailId);
					subdto.setSvoucherbillid(VoucherBillId);
					subdto.setSvoucherbillno(VoucherBillNo);
					subdto.setSvoucherno(sdetailVoucherNo);
					subdto.setSfundtypecode(sdetailFundTypeCode);
					subdto.setSfundtypename(sdetailFundTypeName);
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
					subdto.setSexpecocode((StringUtils.isNotBlank(sdetailExpEcoCode)
							&&sdetailExpEcoCode.getBytes().length<=30)?sdetailExpEcoCode:"");//���ÿ�Ŀ����ֻȡС��30λ��ֵ
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
					//��ϸ�ϼƽ��
					sumAmt = sumAmt.add(BigDecimal.valueOf(Double.valueOf(sdetailPayAmt))); 
					agencyCodeList.add(vDto.getStrecode()+sdetailAgencyCode);					
					expFuncCodeList.add(sdetailExpFuncCode);
					subDtoList.add(subdto);
					subDtoIdList.add(subdto.getSid());
					
				}
				
				/**
				 * У����ϸId�Ƿ�Ϊ�ջ��ظ�
				 */
				String checkIdMsg=voucherVerify.checkValidSudDtoId(subDtoIdList);
				if(checkIdMsg!=null){
					//���ش�����Ϣǩ��ʧ��
					voucher.voucherComfail(mainvou, checkIdMsg);
					continue;				
				}								
				
				/**
				 * ��װverifydto,���б���У��
				 */
				verifydto.setTrecode(maindto.getStrecode());
				verifydto.setFinorgcode(ls_FinOrgCode);//��������
				verifydto.setVoucherno(VoucherNo);
				verifydto.setVoudate(VouDate);
				verifydto.setPaytypecode(PayTypeCode);
				verifydto.setPaybankno(PayeeAcctBankNo);//�տ��������к�
				verifydto.setPaybankname(PayeeAcctBankName);//�տ�����������
				verifydto.setAgentAcctNo(PayeeAcctNo);
				verifydto.setAgentAcctName(PayeeAcctName);
				verifydto.setClearAcctNo(PayAcctNo);
				verifydto.setClearAcctName(PayAcctName);
				
				//������ȣ��ܽ���У�� by renqingbin
				verifydto.setOfyear(StYear);
				verifydto.setFamt(PayAmt);
				
				//�Ϻ��ط���ɫ����У��
				//����ԭƾ֤���͡��ʽ����ʱ��롢 ҵ�����ͱ����У��
				verifydto.setFundTypeCode(FundTypeCode);
				verifydto.setBusinessTypeCode(BusinessTypeCode);
				
				String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_5201);
				if(returnmsg != null){//���ش�����Ϣǩ��ʧ��
					voucher.voucherComfail(vDto.getSdealno(), returnmsg);
					continue;
				}
				
				/**
				 * У���Ƿ��Զ���¼�к�
				 * 1������ҵ��(BusinessTypeCode=1��3����
				 * 		�����տ����������Ʋ�¼�����տ��������к�
				 * 2������ҵ��(BusinessTypeCode=4����
				 * 		������¼�к�
				 */
				if(StringUtils.isBlank(PayeeAcctBankNo)&&BusinessTypeCode.equals(StateConstant.BIZTYPE_CODE_BATCH)){
					//����ҵ�� ������¼�к�
					voucher.voucherComfail(vDto.getSdealno(), "����ҵ���տ��������кŲ�����Ϊ��");
					continue;
				}
				if(!BusinessTypeCode.equals(StateConstant.BIZTYPE_CODE_BATCH)){
					if(StringUtils.isBlank(PayeeAcctBankNo)){						
						maindto.setSpayeeacctbankname(PayeeAcctBankName);
						if(bankInfo!=null&&bankInfo.size()>0){
							TsConvertbanknameDto dto = bankInfo.get(PayeeAcctBankName);
							if(dto != null){//�����Ѳ�¼	
								maindto.setSinputrecbankname(dto.getStcbankname());//֧��ϵͳ����
								maindto.setSpayeeacctbankno(dto.getSbankcode());//�տ��������к�
								maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);//��Ҫ��¼
								maindto.setScheckstatus(StateConstant.CHECKSTATUS_1);//�Ѳ�¼
							}else{//����δ��¼
								maindto.setSpayeeacctbankno("");//�տ��������к�		
								maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);//��Ҫ��¼
								maindto.setScheckstatus(StateConstant.CHECKSTATUS_0);//δ��¼
							}
						}else{//�����������ձ�Ϊ�գ�����δ��¼
							maindto.setSpayeeacctbankno("");//�տ��������к�			
							maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);//��Ҫ��¼
							maindto.setScheckstatus(StateConstant.CHECKSTATUS_0);//δ��¼
						}
					}else{
						maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_NO);//����Ҫ��¼
						maindto.setScheckstatus(StateConstant.CHECKSTATUS_1);//�Ѳ�¼
						//��д֧��ϵͳ�����к�
						if(bankmap != null){
							TsPaybankDto paybankdto = bankmap.get(PayeeAcctBankNo);
							if(paybankdto != null)
								maindto.setSinputrecbankname(paybankdto.getSbankname());//֧��ϵͳ����
							else
								maindto.setSinputrecbankname("δƥ�䵽֧��ϵͳ�к�");//֧��ϵͳ����
						}else
							maindto.setSinputrecbankname("δƥ�䵽֧��ϵͳ�к�");//֧��ϵͳ����					
					}
				}
				
				
				/**
				 *У���������Ƿ����ӱ������ 
				 *
				 */
				if(maindto.getNpayamt().compareTo(sumAmt) != 0){
					String errMsg="�����������ϸ�ۼƽ���ȣ�������"+maindto.getNpayamt()+" ��ϸ�ۼƽ� "+sumAmt;
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
				
				/**
				 * ҵ�����⣬У�鱨�ĺϷ���
				 * 
				 * ҵ�������ӱ����ʧ�ܼ�ǩ��ʧ��
				 */				
				DatabaseFacade.getODB().create(maindto);
				DatabaseFacade.getODB().create(CommonUtil.listTArray(subDtoList));	
				vDto.setIcount(subDtoList.size());
			}catch(Exception e){
				logger.error(e);
				voucher.voucherComfail(mainvou, "���Ĳ��淶��"+e.getMessage());
				continue;
			}
			
			//ǩ�ճɹ�
			try{
				voucher.voucherConfirmSuccess(vDto.getSdealno());
			}catch(ITFEBizException e){
				logger.error(e);
				VoucherException voucherE  = new VoucherException();
				voucherE.saveErrInfo(VtCode, e);
				throw new ITFEBizException("ǩ��ƾ֤����"+VtCode+"�����쳣",e);
			}
			
			list = new ArrayList();
			list.add(maindto);
			list.add(vDto);
			list.add(expFuncCodeList);//�������Ŀ����list
			list.add(agencyCodeList);//Ԥ�㵥λlist
			list.add(subDtoList);
			lists.add(list);
			
			/**
			 * У��ƾ֤��Ϣģ��
			 */
			try {
				if(lists.size()>0){
					voucher.voucherVerify(lists, VtCode);
				}				
			} catch (ITFEBizException e) {
				logger.error(e);
				throw new ITFEBizException(e);
			}catch(Exception e){
				logger.error(e);
				throw new ITFEBizException("У��ƾ֤����"+VtCode+"�����쳣",e);
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

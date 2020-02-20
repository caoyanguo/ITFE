package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertbanknameDto;
import com.cfcc.itfe.persistence.dto.TsFinmovepaysubDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsQueryAmtDto;
import com.cfcc.itfe.persistence.dto.TsSpecacctinfoDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.persistence.pk.TsInfoconnorgaccPK;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

@SuppressWarnings({ "unchecked", "static-access" })
public class Voucher5207MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher5207MsgServer.class);
	private Voucher voucher;
	private Map<String,TsQueryAmtDto> amtMap = null;
	private Map<String,TsSpecacctinfoDto> specacctMap = null;
	/**
	 * �����������е�ʵ������ƾ֤������ֻ����ҵ������
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();
		String voucherXml = (String) muleMessage.getPayload();
		String ls_FileName = (String) muleMessage.getProperty("fileName");
		String ls_OrgCode = (String) muleMessage.getProperty("orgCode");
		HashMap<String,String> dealnos=(HashMap<String,String>)muleMessage.getProperty("dealnos");
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
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("����ʵ������ƾ֤5207���ĳ��ִ���",e);			
		}
		List VoucherBodyList  = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
//		String AdmDivCode  = "";//������������
		String StYear  = "";//���
		String VtCode  = "";//ƾ֤����
		
		String currentDate = TimeFacade.getCurrentStringTime();// ��ǰϵͳ����
//		List<String> voucherList = new ArrayList<String>();
		
		//��ȡ�����������롢��Ⱥ�ƾ֤����
		if(VoucherBodyList.size()>0){
			//VoucherBody
			Element element  = (Element)VoucherBodyList.get(0);
//			AdmDivCode  = element.attribute("AdmDivCode").getText();
			StYear  = element.attribute("StYear").getText();
			VtCode  = element.attribute("VtCode").getText();
		}
		
		//Ԥ�㵥λ����list
		Set agencyCodeList = null;
		//Ԥ���Ŀ����list
		ArrayList<String> expFuncCodeList = null;
		//�����list
		ArrayList<BigDecimal> amtList = null; 
		//����list	 @author �Ż��
		ArrayList<BigDecimal> zeroList = null; 
		
		String sAgencyCode ="";
		TvPayoutmsgmainDto maindto  = null;
		List subDtoList=null; 
		List lists=new ArrayList();
		List list=null;
		//ƾ֤���к�
		String mainvou="";
		TvVoucherinfoDto vDto=null;
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
//			String Attach = ((Element) VoucherBodyList.get(i)).elementText("Attach");//������Ϣ
			String Id = elementVoucher.elementText("Id");//ʵ������ƾ֤Id
			//String VoucherNo  = elementVoucher.elementText("VoucherNo");//ƾ֤��
			String VouDate = elementVoucher.elementText("VouDate"); //ƾ֤����
			String TreCode = elementVoucher.elementText("TreCode"); //�����������
			String FinOrgCode = elementVoucher.elementText("FinOrgCode");//�������ش���
			String FundTypeCode = elementVoucher.elementText("FundTypeCode");//�ʽ����ʱ���
			String FundTypeName = elementVoucher.elementText("FundTypeName");//�ʽ���������
			String PayTypeCode = elementVoucher.elementText("PayTypeCode");//֧����ʽ����
			String PayTypeName = elementVoucher.elementText("PayTypeName");//֧����ʽ����
			String ClearBankCode = elementVoucher.elementText("ClearBankCode");//�������б���
			String ClearBankName = elementVoucher.elementText("ClearBankName"); //������������
			String PayeeAcctNo = elementVoucher.elementText("PayeeAcctNo");	//�տ����˺�
			String PayeeAcctName = elementVoucher.elementText("PayeeAcctName");//�տ�������
			String PayeeAcctBankName = elementVoucher.elementText("PayeeAcctBankName");//�տ�������
			//�����嵥ģʽ
			TsTreasuryDto tredto = SrvCacheFacade.cacheTreasuryInfo(null).get(TreCode);
			if (StateConstant.COMMON_YES.equals(String.valueOf(tredto==null?"":tredto.getSpayunitname()))) {
			   if (StringUtils.isBlank(PayeeAcctNo)) {
				   PayeeAcctNo="N";
			   }
			   if (StringUtils.isBlank(PayeeAcctName)) {
				   PayeeAcctName="N";
			   }
			   if (StringUtils.isBlank(PayeeAcctBankName)) {
				   PayeeAcctBankName="N";
			   }
			}
			String PayeeAcctBankNo = elementVoucher.elementText("PayeeAcctBankNo");//�տ������к�
			String PayAcctNo = elementVoucher.elementText("PayAcctNo");//�������˺�
			String PayAcctName = elementVoucher.elementText("PayAcctName");//����������
			String PayAcctBankName = elementVoucher.elementText("PayAcctBankName");//����������
			String PaySummaryCode = elementVoucher.elementText("PaySummaryCode");//��;����
			String PaySummaryName = elementVoucher.elementText("PaySummaryName"); //��;����
			String PayAmt = elementVoucher.elementText("PayAmt");//������
			String XAgentBusinessNo = elementVoucher.elementText("XAgentBusinessNo");//����������
			String Hold1=elementVoucher.elementText("Hold1");//Ԥ���ֶ�1
//			String Hold2=elementVoucher.elementText("Hold2");//Ԥ���ֶ�2
			
			String AgencyCode = "";//Ԥ�㵥λ����
			String AgencyName= "";//Ԥ�㵥λ����
			if("000057400006".equals(ITFECommonConstant.SRC_NODE)){//����ʵ���ʽ�Ԥ�㵥λ������������Ϣ��
				AgencyCode=elementVoucher.elementText("AgencyCode");//Ԥ�㵥λ����
				AgencyName=elementVoucher.elementText("AgencyName");//Ԥ�㵥λ����
			}
			/**
			 * ��װTvPayoutmsgmainDto����
			 **/
			maindto  = new TvPayoutmsgmainDto();
			mainvou =dealnos.get(VoucherNo);//��ȡ���к�
			vDto = new TvVoucherinfoDto();
			vDto.setSdealno(mainvou);
			List vouList = CommonFacade.getODB().findRsByDto(vDto);
			if(vouList!=null&&vouList.size()>0)
				vDto=(TvVoucherinfoDto)vouList.get(0) ;
			else
			{
				String errMsg="ƾ֤�������ѯ����ƾ֤���Ϊ"+vDto.getSdealno()+"������!";
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
			}
			maindto.setSbizno(mainvou);//���к�  
			maindto.setSorgcode(ls_OrgCode);//�����������
			maindto.setScommitdate(currentDate);//ί������
			maindto.setSaccdate(currentDate);//��������
			maindto.setSfilename(ls_FileName);//�ļ���
			maindto.setStrecode(TreCode);//�������
			maindto.setSpackageno("");//����ˮ��
			maindto.setSpayunit(FinOrgCode);//��Ʊ��λ
			maindto.setSdealno(mainvou.substring(8, 16));//������ˮ��
			maindto.setStaxticketno(VoucherNo);//ƾ֤���
			maindto.setSgenticketdate(VouDate);//ƾ֤����
			maindto.setSpayeracct(PayAcctNo);//�������˺�
			maindto.setSpayername(PayAcctName);//����������
			maindto.setSpayeraddr("");
			maindto.setNmoney(BigDecimal.valueOf(Double.valueOf(PayAmt)));//���
			if("".equals(PayeeAcctBankNo) || PayeeAcctBankNo ==null){
				maindto.setSrecbankname(PayeeAcctBankName);//�տ�����������
				if(bankInfo!=null&&bankInfo.size()>0){
					TsConvertbanknameDto dto = bankInfo.get(PayeeAcctBankName);
					if(dto != null){//������¼��
					
						maindto.setSinputrecbankname(dto.getStcbankname());//֧��ϵͳ����
						maindto.setSinputrecbankno(dto.getSbankcode());//֧��ϵͳ�к�
						maindto.setSpayeebankno(dto.getSbankcode());//�տ��˿�����
						maindto.setSrecbankno(dto.getSbankcode());//��������
						maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);//��Ҫ��¼
						maindto.setScheckstatus(StateConstant.CHECKSTATUS_1);//�Ѳ�¼
						if(ITFECommonConstant.PUBLICPARAM.indexOf(",verify=false,")>=0)
						{
							maindto.setScheckstatus(StateConstant.CHECKSTATUS_4);// ����״̬Ϊ�Ѹ���
							maindto.setSchecksercode("admin");// ������
						}
					}else{
						maindto.setSrecbankno("");//�տ��˿�����
						maindto.setSpayeebankno("");//��������			
						maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);//��Ҫ��¼
						maindto.setScheckstatus(StateConstant.CHECKSTATUS_0);//δ��¼
					}
				}else{
					maindto.setSrecbankno("");//�տ��˿�����
					maindto.setSpayeebankno("");//��������			
					maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);//��Ҫ��¼
					maindto.setScheckstatus(StateConstant.CHECKSTATUS_0);//δ��¼
				}
			}else{
				
				maindto.setSrecbankname(PayeeAcctBankName);//�տ�����������
				maindto.setSpayeebankno(PayeeAcctBankNo);//�տ��˿������к�
				maindto.setSrecbankno(PayeeAcctBankNo);//�տ��˿������к�
				maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_NO);//����Ҫ��¼
				maindto.setScheckstatus(StateConstant.CHECKSTATUS_1);//�Ѳ�¼
				//��д֧��ϵͳ�����к�
				if(bankmap != null){
					TsPaybankDto paybankdto = bankmap.get(PayeeAcctBankNo);
					if(paybankdto != null){
						maindto.setSinputrecbankname(paybankdto.getSbankname());//֧��ϵͳ����
						maindto.setSinputrecbankno(paybankdto.getSbankno());//֧��ϵͳ�к�
						if(ITFECommonConstant.PUBLICPARAM.indexOf(",verify=false,")>=0)
						{
							maindto.setScheckstatus(StateConstant.CHECKSTATUS_4);// ����״̬Ϊ�Ѹ���
							maindto.setSchecksercode("admin");// ������
						}
					}else{
						maindto.setSinputrecbankname("δƥ�䵽֧��ϵͳ�к�");//֧��ϵͳ����
						maindto.setSinputrecbankno("000000000000");//֧��ϵͳ�к�
					}
				}else{
					maindto.setSinputrecbankname("δƥ�䵽֧��ϵͳ�к�");//֧��ϵͳ����
					maindto.setSinputrecbankno("000000000000");//֧��ϵͳ�к�
				}
			}
			
			maindto.setSrecacct(PayeeAcctNo);//�տ����˺�
			maindto.setSrecname(PayeeAcctName);//�տ�������
			maindto.setStrimflag(MsgConstant.TIME_FLAG_NORMAL);//�����ڱ�־
			maindto.setSofyear(StYear);//�������
			if("000073100012".equals(ITFECommonConstant.SRC_NODE)){//�����ʽ�������ΪԤ������
				maindto.setSbudgettype(FundTypeCode);
			}else{
				maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);//Ԥ������(Ĭ��Ԥ����)
				TsInfoconnorgaccPK tmppk = new TsInfoconnorgaccPK();
				tmppk.setSorgcode(ls_OrgCode); // ��������
				tmppk.setStrecode(TreCode); // TreCode��������
				tmppk.setSpayeraccount(PayAcctNo);// �����˺�
				TsInfoconnorgaccDto resultdto = null;
				try {
					resultdto = (TsInfoconnorgaccDto) DatabaseFacade.getDb().find(tmppk);
					if(resultdto!=null&&resultdto.getSbiztype()!=null&&!"".equals(resultdto.getSbiztype()))
					{
						maindto.setSbudgettype(resultdto.getSbiztype());
					}
				} catch (JAFDatabaseException e) {
					logger.error("��ѯ����˻��쳣:" + e.getMessage());
				}
			}
			maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);//״̬
			maindto.setSusercode("0000");//�û�����
			//���ԣ��ʽ���;��Ϊ���Է��͵�TIPS��
			if(!StringUtils.isBlank(PaySummaryName)){
					maindto.setSaddword(PaySummaryName.getBytes().length>60 ? CommonUtil.subString(PaySummaryName, 60) : PaySummaryName);
			}else{
				maindto.setSaddword("");
			}
			maindto.setSbackflag(StateConstant.MSG_BACK_FLAG_NO);// �˿��־
			maindto.setSid(Id); //ʵ������ƾ֤Id
			maindto.setSpayerbankname(PayAcctBankName);//��������������
			maindto.setSfundtypecode(FundTypeCode);//�ʽ����ʱ���
			maindto.setSfundtypename(FundTypeName);//�ʽ���������
			maindto.setSpaytypecode(PayTypeCode);//֧����ʽ����
			maindto.setSpaytypename(PayTypeName);//֧����ʽ����
			maindto.setSclearbankcode(ClearBankCode);//�������б���
			maindto.setSclearbankname(ClearBankName);//������������
			maindto.setSpaysummarycode(PaySummaryCode);//��;����
			maindto.setSpaysummaryname(PaySummaryName);//��;����
			maindto.setShold1(Hold1);//�����ֶ�1
//			maindto.setShold2(Hold2);//�����ֶ�2-�˻ؽ��ռ��
			maindto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//ϵͳʱ��
			maindto.setSxagentbusinessno(XAgentBusinessNo);
			/**
			 * �Ϻ���ֽ����չ
			 * @author �Ż��
			 */
			String BusinessTypeCode = "";//ҵ�����ͱ���(1Ϊ ���ʣ� 4Ϊ ����)
			String BusinessTypeName = "";//ҵ����������
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0){
				BusinessTypeCode = elementVoucher.elementText("BusinessTypeCode");//ҵ�����ͱ���(1Ϊ ���ʣ� 4Ϊ ����)
				BusinessTypeName = elementVoucher.elementText("BusinessTypeName");//ҵ����������
				maindto.setSbusinesstypecode(BusinessTypeCode);//ҵ�����ͱ��루1-����ҵ��  4-����ҵ��
				maindto.setSbusinesstypename(BusinessTypeName);//ҵ����������
			}
			
			if("000057400006".equals(ITFECommonConstant.SRC_NODE)){//����ʵ���ʽ�Ԥ�㵥λ������������Ϣ��
				maindto.setSbudgetunitcode(AgencyCode);
				maindto.setSunitcodename(AgencyName);
			}
			
			//Ԥ�㵥λ����list
			agencyCodeList = new HashSet();
			//Ԥ���Ŀ����list
			expFuncCodeList = new ArrayList<String>();
			//�����list
			amtList = new ArrayList<BigDecimal>();
			
			//����list @author �Ż��
			zeroList = new ArrayList<BigDecimal>();
			
			//�洢֧����Ŀ����
			Set<String> subcodeSet = new HashSet<String>();
			
			//�ӱ���ϸId����
			List<String>  subDtoIdList = new ArrayList<String>();
			/**
			 * ��װTvPayoutmsgsubDto����
			 */
			subDtoList=new ArrayList<TvPayoutmsgsubDto>();
			if(listDetail==null||listDetail.size()<=0)
			{
				String errMsg="ƾ֤���Ϊ"+vDto.getSdealno()+"���������ӵ���Ϣ!";
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
			}
			for(int j=0;j<listDetail.size();j++){
				
				//��ϸ�ϼƽ��
				Element elementDetail  = (Element)listDetail.get(j);
				String sId=(j+1)+"";//������ϸ���
				String sVoucherBillId=elementDetail.elementText("VoucherBillId");//����ƾ֤Id
				String BgtTypeCode = elementDetail.elementText("BgtTypeCode");//Ԥ�����ͱ���
				String BgtTypeName = elementDetail.elementText("BgtTypeName");//Ԥ����������
				String ProCatCode = elementDetail.elementText("ProCatCode");//��֧�������
				String ProCatName = elementDetail.elementText("ProCatName");//��֧��������
				String sAgencyName= "";//Ԥ�㵥λ����
				if(!"000057400006".equals(ITFECommonConstant.SRC_NODE)){//����ʵ���ʽ�Ԥ�㵥λ������������Ϣ��
					sAgencyCode=elementDetail.elementText("AgencyCode");//Ԥ�㵥λ����
					sAgencyName=elementDetail.elementText("AgencyName");//Ԥ�㵥λ����
					//��������Ԥ�㵥λ����
					maindto.setSbudgetunitcode(sAgencyCode);
					maindto.setSunitcodename(sAgencyName);
					//����Ԥ�㵥λLIST
					agencyCodeList.add(sAgencyCode);
				}
				String ExpFuncCode = elementDetail.elementText("ExpFuncCode");//֧�����ܷ����Ŀ����
				subcodeSet.add(ExpFuncCode);
				String ExpFuncName = elementDetail.elementText("ExpFuncName");//֧�����ܷ����Ŀ����
				String ExpFuncCode1 = elementDetail.elementText("ExpFuncCode1");//֧�����ܷ����Ŀ�����
				String ExpFuncName1 = elementDetail.elementText("ExpFuncName1");//֧�����ܷ����Ŀ������
				String ExpFuncCode2 = elementDetail.elementText("ExpFuncCode2");//֧�����ܷ����Ŀ�����
				String ExpFuncName2 = elementDetail.elementText("ExpFuncName2");//֧�����ܷ����Ŀ������
				String ExpFuncCode3 = elementDetail.elementText("ExpFuncCode3");//֧�����ܷ����Ŀ�����
				String ExpFuncName3 = elementDetail.elementText("ExpFuncName3");//֧�����ܷ����Ŀ������
				String ExpEcoCode = elementDetail.elementText("ExpEcoCode");//֧�����÷����Ŀ�����
				String ExpEcoName = elementDetail.elementText("ExpEcoName");//֧�����÷����Ŀ������
				String ExpEcoCode1 = elementDetail.elementText("ExpEcoCode1");//֧�����÷����Ŀ�����
				String ExpEcoName1 = elementDetail.elementText("ExpEcoName1");//֧�����÷����Ŀ������
				String ExpEcoCode2 = elementDetail.elementText("ExpEcoCode2");//֧�����÷����Ŀ�����
				String ExpEcoName2 = elementDetail.elementText("ExpEcoName2");//֧�����÷����Ŀ������
				String sPayAmt = elementDetail.elementText("PayAmt");//֧�����
				String sdetailHold1 = elementDetail.elementText("Hold1");//Ԥ���ֶ�1
				String sdetailHold2 = elementDetail.elementText("Hold2");//Ԥ���ֶ�2
				String sdetailHold3 = elementDetail.elementText("Hold3");//Ԥ���ֶ�3
				String sdetailHold4 = elementDetail.elementText("Hold4");//Ԥ���ֶ�4
				if(sdetailHold1.getBytes().length>42)
					sdetailHold1 = CommonUtil.subString(sdetailHold1, 42);
				if(sdetailHold2.getBytes().length>42)
					sdetailHold2 = CommonUtil.subString(sdetailHold2, 42);
				if(sdetailHold3.getBytes().length>42)
					sdetailHold3 = CommonUtil.subString(sdetailHold3, 42);
				if(sdetailHold4.getBytes().length>42)
					sdetailHold4 = CommonUtil.subString(sdetailHold4, 42);
				String 	subId=elementDetail.elementText("Id");//����ƾ֤��ϸId
				
				TvPayoutmsgsubDto subdto=new TvPayoutmsgsubDto();
				//�˴�����ֵ��ȷ��
				subdto.setSbizno(mainvou);//�ӱ����к�
				subdto.setSseqno(Integer.valueOf(sId));//�ӱ���ϸ���
				subdto.setSid(subId);//����ƾ֤��ϸId
				subdto.setSaccdate(currentDate);//��������
				subdto.setSecnomicsubjectcode(ExpEcoCode);//���ÿ�Ŀ����
				subdto.setStaxticketno(sVoucherBillId);//����ƾ֤Id
				subdto.setSbgttypecode(BgtTypeCode);//Ԥ�����ͱ���
				subdto.setSbgttypename(BgtTypeName);//Ԥ����������
				subdto.setSprocatcode(ProCatCode);//��֧�������
				subdto.setSprocatname(ProCatName);//��֧��������
				subdto.setSagencycode(sAgencyCode);//Ԥ�㵥λ����
				subdto.setSagencyname(sAgencyName);//Ԥ�㵥λ����
				subdto.setSfunsubjectcode(ExpFuncCode);//֧�����ܷ����Ŀ����
				subdto.setSexpfuncname(ExpFuncName);//֧�����ܷ����Ŀ����
				subdto.setSexpfunccode1(ExpFuncCode1);//֧�����ܷ����Ŀ�����
				subdto.setSexpfuncname1(ExpFuncName1);//֧�����ܷ����Ŀ������
				subdto.setSexpfunccode2(ExpFuncCode2);//֧�����ܷ����Ŀ�����
				subdto.setSexpfuncname2(ExpFuncName2);//֧�����ܷ����Ŀ������
				subdto.setSexpfunccode3(ExpFuncCode3);//֧�����ܷ����Ŀ�����
				subdto.setSexpfuncname3(ExpFuncName3);//֧�����ܷ����Ŀ������
				subdto.setSexpecocode(ExpEcoCode);//֧�����÷����Ŀ�����
				subdto.setSexpeconame(ExpEcoName);//֧�����÷����Ŀ������
				subdto.setSexpecocode1(ExpEcoCode1);//֧�����÷����Ŀ�����
				subdto.setSexpeconame1(ExpEcoName1);//֧�����÷����Ŀ������
				subdto.setSexpecocode2(ExpEcoCode2);//֧�����÷����Ŀ�����
				subdto.setSexpeconame2(ExpEcoName2);//֧�����÷����Ŀ������
				BigDecimal subPayAmt = new BigDecimal(sPayAmt);
				//������б�
				if(subPayAmt.signum()==-1){
					amtList.add(subPayAmt);
				}
				//�����б� @author �Ż��
				if(subPayAmt.equals(BigDecimal.ZERO)){
					zeroList.add(subPayAmt);
				}
				subdto.setNmoney(subPayAmt);//֧�����
				subdto.setShold1(sdetailHold1);//Ԥ���ֶ�1
				subdto.setShold2(sdetailHold2);//Ԥ���ֶ�2
				subdto.setShold3(sdetailHold3);//Ԥ���ֶ�3
				subdto.setShold4(sdetailHold4);//Ԥ���ֶ�4
				//�����嵥ģʽ
				if (StateConstant.COMMON_YES.equals(String.valueOf(tredto==null?"":tredto.getSpayunitname()))) {
					String 	sDetailId=elementDetail.elementText("Id");//����ƾ֤��ϸId
					String  voucherDetailNo= elementDetail.elementText("VoucherDetailNo");//������ϸƾ֤���
					String 	SubPayeeAcctNo = elementDetail.elementText("PayeeAcctNo");//�տ����˺�
					String 	SubPayeeAcctName = elementDetail.elementText("PayeeAcctName");//�տ�������
					String  SubPayeeAcctBankNo=elementDetail.elementText("PayeeAcctBankNo");//�տ������к�
					String 	SubPayeeAcctBankName = elementDetail.elementText("PayeeAcctBankName");//�տ�������
					String  SubPaySummaryCode = elementDetail.elementText("PaySummaryCode");//��;����
					String  SubPaySummaryName = elementDetail.elementText("PaySummaryName"); //��;����
					String  Remark = elementDetail.elementText("Remark"); //ժҪ
					
					subdto.setSid(sDetailId);//����ƾ֤��ϸId/
					if (StringUtils.isBlank(voucherDetailNo)) {
						voucherDetailNo = sDetailId;
					}
					subdto.setSvoucherno(voucherDetailNo);
					subdto.setSpayeeacctno(SubPayeeAcctNo);//�տ����˺�
					subdto.setSpayeeacctname(SubPayeeAcctName);//�տ�������
					subdto.setSpayeebankno(SubPayeeAcctBankNo);
					subdto.setSpayeeacctbankname(SubPayeeAcctBankName);//�տ�������
					subdto.setSpaysummarycode(SubPaySummaryCode);//��;����
					subdto.setSpaysummaryname(SubPaySummaryName);//��;����	
					subdto.setSxpayamt(BigDecimal.ZERO);//���Ĭ��0
				} 
				
				
			   	
				/**
				 * �Ϻ���ֽ����չ
				 * @author �Ż��
				 */
				if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0){
					
					//������ϸ�����ֶ�
					String 	sDetailId=elementDetail.elementText("Id");//����ƾ֤��ϸId
					String 	SubPayeeAcctNo = elementDetail.elementText("PayeeAcctNo");//�տ����˺�
					String 	SubPayeeAcctName = elementDetail.elementText("PayeeAcctName");//�տ�������
					String 	SubPayeeAcctBankName = elementDetail.elementText("PayeeAcctBankName");//�տ�������
					String  SubPaySummaryCode = elementDetail.elementText("PaySummaryCode");//��;����
					String  SubPaySummaryName = elementDetail.elementText("PaySummaryName"); //��;����
					String  SubXpayAmt = elementDetail.elementText("XpayAmt");//���(ʵ��֧�������������ڻص��в�¼)
					String  SubXPayDate = elementDetail.elementText("XPayDate");//֧������(�������д������ڣ����������ڻص��в�¼)
					String  SubXAgentBusinessNo = elementDetail.elementText("XAgentBusinessNo");//���н�����ˮ��(���������ڻص��в�¼)
					String  SubXAddWord = elementDetail.elementText("XAddWord");//ʧ��ԭ��
					
					
					//Ϊ�ֱ�DTO��ֵ
					subdto.setSid(sDetailId);//����ƾ֤��ϸId/
					subdto.setSpayeeacctno(SubPayeeAcctNo);//�տ����˺�
					
					subdto.setSpayeeacctname(SubPayeeAcctName);//�տ�������
					subdto.setSpayeeacctbankname(SubPayeeAcctBankName);//�տ�������
					subdto.setSpaysummarycode(SubPaySummaryCode);//��;����
					subdto.setSpaysummaryname(SubPaySummaryName);//��;����
					if(StringUtils.isBlank(SubXpayAmt)){
						SubXpayAmt="0.00";
					}
					subdto.setSxpayamt(new BigDecimal(SubXpayAmt));//���(ʵ��֧�������������ڻص��в�¼)
					subdto.setSxpaydate(SubXPayDate);//֧������(�������д������ڣ����������ڻص��в�¼)
					subdto.setSxagentbusinessno(SubXAgentBusinessNo);//���н�����ˮ��(���������ڻص��в�¼)
					subdto.setSxaddword(SubXAddWord);//ʧ��ԭ��
				}
				
				
				//���ù��ܿ�ĿLIST,��ϸ�ϼ�
				expFuncCodeList.add(ExpFuncCode);
				sumAmt = sumAmt.add(subPayAmt); 
				subDtoList.add(subdto);
				subDtoIdList.add(subdto.getSid());
			}
			/**
			 * ��ƾ֤��������ʾҵ�������ո����������Ϣ
			 */
			if(ITFECommonConstant.PUBLICPARAM.contains(",voucherdisplaymain=true,"))
			{
				vDto.setShold3(maindto.getSpayeracct());//�����˺�
				vDto.setShold4(maindto.getSpayername());//��������
				vDto.setSext1(maindto.getSrecacct());//�տ��˺�
				vDto.setSext3(maindto.getSrecname());//�տ�����
				vDto.setSext4(maindto.getSrecbankname());//�տ�����
				if(vDto.getShold3()!=null&&new String(vDto.getShold3().getBytes("GBK"),"iso-8859-1").length()>60)
					vDto.setShold3(CommonUtil.subString(vDto.getShold3(),60));
				if(vDto.getShold4()!=null&&new String(vDto.getShold4().getBytes("GBK"),"iso-8859-1").length()>60)
					vDto.setShold4(CommonUtil.subString(vDto.getShold4(),60));
				if(vDto.getSext1()!=null&&new String(vDto.getSext1().getBytes("GBK"),"iso-8859-1").length()>50)
					vDto.setSext1(CommonUtil.subString(vDto.getSext1(),50));
				if(vDto.getSext3()!=null&&new String(vDto.getSext3().getBytes("GBK"),"iso-8859-1").length()>50)
					vDto.setSext3(CommonUtil.subString(vDto.getSext3(),50));
				if(vDto.getSext4()!=null&&new String(vDto.getSext4().getBytes("GBK"),"iso-8859-1").length()>50)
					vDto.setSext4(CommonUtil.subString(vDto.getSext4(),50));
			}
			/*
			ϵͳ�յ��������͵�ʵ������ʱ��У�鸶���˻��Ƿ��Ǻ�������+27101�˻���������Ǵ�����ר��ʵ���ʽ�
			У���տ��˺��Ƿ���ר������ǣ�����Ϊר��ʵ���ʽ�
			У�鲦���޶��Ƿ񳬱�
			��������� ���ʽ��꣬����ƾ֤��������s_ext2�����ֶν��б�ʶ 
			(��ȡ����   ���� -���� )  
			*/
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",xm5207,")>=0)
			{
				TsTreasuryDto streDto = new TsTreasuryDto();
				streDto.setStrecode(TreCode);
				List<TsTreasuryDto>	streDtoList = CommonFacade.getODB().findRsByDto(streDto);
				if(streDtoList.get(0).getStreattrib().equals("2")){ //�����
					
				}else{
					TsQueryAmtDto queryAmtDto =  getAmtMap().get(maindto.getSorgcode()+maindto.getStrecode()+VtCode);
					if (null!=queryAmtDto)
					{
						BigDecimal moneyfilter = queryAmtDto.getFendamt();
						if (moneyfilter.compareTo(maindto.getNmoney())<=0) 
						{
							vDto.setSext2("1");//ʵ������
							vDto.setSreturnerrmsg("ʵ�����");
							vDto = (TvVoucherinfoDto) DatabaseFacade.getODB().updateWithResult(vDto);
						}
					}
						//�Ƿ�Ͳ���ר������ƥ��
					TsSpecacctinfoDto specacctinfoDto = getSpecacctMap().get(maindto.getSorgcode()+maindto.getSrecacct());
					if (null!= specacctinfoDto ) {//ƥ��
						vDto.setSext2("1");//�տ��˻�Ϊʵ��ר��
						vDto.setSreturnerrmsg(vDto.getSreturnerrmsg()+"�տ��˻�Ϊ����ר����");
						vDto = (TvVoucherinfoDto) DatabaseFacade.getODB().updateWithResult(vDto);
					}else if(maindto.getSrecname()!=null&&maindto.getSrecname().contains("����"))
					{
						vDto.setSext2("1");//�տ��˻�Ϊʵ��ר��
						vDto.setSreturnerrmsg(vDto.getSreturnerrmsg()+"�տ��˻�Ϊʵ��ר��,��ר���������в����ڣ�");
						vDto = (TvVoucherinfoDto) DatabaseFacade.getODB().updateWithResult(vDto);
					}
					if(!(vDto.getSorgcode()+"27101").equals(maindto.getSpayeracct())&&!(vDto.getSorgcode()+"17202001").equals(maindto.getSpayeracct())){
						String errMsg="�����˺ű���Ϊ"+vDto.getSorgcode()+"27101��"+vDto.getSorgcode()+"17202001";
						voucher.voucherComfail(vDto.getSdealno(), errMsg);
						continue;
					}
				}
			}
			if(maindto.getSrecbankno()!=null&&maindto.getSrecbankno().startsWith("011"))
			{
				vDto.setSext2("1");//���Ϊ���ڵ���
				vDto.setSreturnerrmsg("�˱�֧��Ϊ���������(��)����֧����");
				vDto = (TvVoucherinfoDto) DatabaseFacade.getODB().updateWithResult(vDto);
				if(subDtoList!=null&&subDtoList.size()>0&&ITFECommonConstant.PUBLICPARAM.contains(",5207editsubject,"))
				{
					List<TsFinmovepaysubDto> qlist = DatabaseFacade.getODB().findWithUR(TsFinmovepaysubDto.class, " where s_orgcode='"+ls_OrgCode+"'");
					if(qlist!=null&&qlist.size()>0)
					{
						Map<String,String> cmap = new HashMap<String,String>();
						for(TsFinmovepaysubDto tdto:qlist)
						{
							if(tdto.getSsubjectcode()!=null&&tdto.getSsubjectname()!=null&&isNumber(tdto.getSsubjectname()))
								cmap.put(tdto.getSsubjectcode(), tdto.getSsubjectname());
						}
						TvPayoutmsgsubDto subjectdto  = null;
						for(int sub=0;sub<subDtoList.size();sub++)
						{
							subjectdto = (TvPayoutmsgsubDto)subDtoList.get(sub);
							if(cmap.containsKey(subjectdto.getSfunsubjectcode()))
							{
								subjectdto.setSfunsubjectcode(cmap.get(subjectdto.getSfunsubjectcode()));
							}
						}
					}
					
				}
			}
			if(ls_OrgCode.startsWith("17")&&maindto.getSrecacct()!=null&&(maindto.getSrecacct().equals("885-1")||maindto.getSrecacct().equals("17-783101040005000")))
			{
				vDto.setSext2("1");//���Ϊ���ڵ���
				vDto.setSreturnerrmsg("�˱�֧��Ϊ���������(��)����֧����");
				vDto = (TvVoucherinfoDto) DatabaseFacade.getODB().updateWithResult(vDto);
			}
			/**
			 * ��װverifydto,���б���У��
			 */
			verifydto.setTrecode(TreCode);
			verifydto.setFinorgcode(FinOrgCode);
			verifydto.setVoucherno(VoucherNo);
			verifydto.setVoudate(VouDate);
			verifydto.setAgentAcctNo(PayeeAcctNo);
			verifydto.setAgentAcctName(PayeeAcctName);
			verifydto.setClearAcctNo(PayAcctNo);
			verifydto.setClearAcctName(PayAcctName);
			//������ȣ��ܽ���У�� by renqingbin
			verifydto.setPaybankname(PayeeAcctBankName);
			verifydto.setOfyear(StYear);
			verifydto.setFamt(PayAmt);
			//����У��Ԥ�㵥λ����
			verifydto.setAgencyName(maindto.getSunitcodename());
			//У��Ԥ������
			verifydto.setBudgettype(maindto.getSbudgettype());
			//��������
			verifydto.setOrgcode(maindto.getSorgcode());
			/**
			 * У��ҵ�����ͱ����ҵ���������ƣ��Ϻ���ɫ��
			 * @author �Ż��
			 */
			verifydto.setBusinessTypeCode(BusinessTypeCode);//ҵ�����ͱ���
			verifydto.setBusinessTypeName(BusinessTypeName);//ҵ����������
			verifydto.setPaytypecode(PayTypeCode);//֧����ʽ����
			verifydto.setFundTypeCode(FundTypeCode);//�ʽ����ʱ���
			
			String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_5207);
			if(returnmsg != null){//���ش�����Ϣǩ��ʧ��
				voucher.voucherComfail(vDto.getSdealno(), returnmsg);
				continue;
			}
			/**
			 *У���ӱ����Ƿ��и����
			 *
			 */
			if(amtList.size() > 0){
				String errMsg="��ϸ��Ϣ�а����и���������!";
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
			}
			
			/**
			 *У���������Ƿ����ӱ������ 
			 *
			 */
			if(maindto.getNmoney().compareTo(sumAmt) != 0){
				String errMsg="�����������ϸ�ۼƽ���ȣ�������"+maindto.getNmoney()+" ��ϸ�ۼƽ� "+sumAmt;
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
			 * У���ӱ�Ԥ�㵥λ�����Ƿ��ظ�
			 * 
			 */
			if(agencyCodeList.size()>1){
				String errMsg="ƾ֤��ϸ�д��ڶ��Ԥ�㵥λ��Ϣ!";
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
			}
			/**
			 * ҵ�����⣬У�鱨�ĺϷ���
			 * 
			 * ҵ�������ӱ����ʧ�ܼ�ǩ��ʧ��
			 */
			TvPayoutmsgsubDto[] subDtos=new TvPayoutmsgsubDto[subDtoList.size()];
			subDtos=(TvPayoutmsgsubDto[]) subDtoList.toArray(subDtos);
			DatabaseFacade.getODB().create(maindto);
			DatabaseFacade.getODB().create(subDtos);
			vDto.setIcount(subDtoList.size());
			}catch(JAFDatabaseException e){
				logger.error(e);
				voucher.voucherComfail(mainvou, "�������ݿ���ִ���"+e.getMessage());
				continue;	
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
				VoucherException.saveErrInfo(vDto.getSvtcode(), e);
				continue;
			}
			
			list=new ArrayList();
			list.add(maindto);
			list.add(vDto);			
			list.add(expFuncCodeList);
			list.add(subDtoList);
			lists.add(list);
		}
		
		
		/**
		 * У��ƾ֤��Ϣģ��
		 */
		try {
			if(lists.size()>0){
				voucher.voucherVerify(lists, VtCode);
			}
			
		} catch (ITFEBizException e) {
			logger.error(e);
		}catch(Exception e){
			logger.error(e);
		}
		return;
	}
	
	public Voucher getVoucher() {
		return voucher;
	}
	public void setVoucher(Voucher voucher) {
		this.voucher = voucher;
	}
	public Map<String, TsQueryAmtDto> getAmtMap() throws JAFDatabaseException, ValidateException {
		if(amtMap==null)
		{
			amtMap = new HashMap<String, TsQueryAmtDto>();
			List<TsQueryAmtDto> queryAmtDtoList =  CommonFacade.getODB().findRsByDto(new TsQueryAmtDto());
			if(queryAmtDtoList!=null&&queryAmtDtoList.size()>0)
			{
				for(TsQueryAmtDto tempdto:queryAmtDtoList)
					amtMap.put(tempdto.getSorgcode()+tempdto.getStrecode()+tempdto.getSbiztype(), tempdto);
				
			}			
		}
		return amtMap;
	}

	public void setAmtMap(Map<String, TsQueryAmtDto> amtMap) {
		this.amtMap = amtMap;
	}

	public Map<String, TsSpecacctinfoDto> getSpecacctMap() throws JAFDatabaseException, ValidateException {
		if(specacctMap==null)
		{
			specacctMap = new HashMap<String, TsSpecacctinfoDto>();
			List<TsSpecacctinfoDto> specacctinfoList =  CommonFacade.getODB().findRsByDto(new TsSpecacctinfoDto());
			if(specacctinfoList!=null&&specacctinfoList.size()>0)
			{ 
				for(TsSpecacctinfoDto tempdto:specacctinfoList)
					specacctMap.put(tempdto.getSorgcode()+tempdto.getSpayeeacct(), tempdto);
			}
		}
		return specacctMap;
	}

	public void setSpecacctMap(Map<String, TsSpecacctinfoDto> specacctMap) {
		this.specacctMap = specacctMap;
	}
	/**
	 * �ж��ַ����Ƿ����������
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str)
    {
        java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("[0-9]*");
        java.util.regex.Matcher match=pattern.matcher(str);
        if(match.matches()==false)
        {
           return false;
        }
        else
        {
           return true;
        }
    }
}

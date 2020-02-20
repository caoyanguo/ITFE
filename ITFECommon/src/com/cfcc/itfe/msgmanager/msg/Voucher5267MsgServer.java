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
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsQueryAmtDto;
import com.cfcc.itfe.persistence.dto.TsSpecacctinfoDto;
import com.cfcc.itfe.persistence.dto.TsTaxpayacctDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.persistence.pk.TsInfoconnorgaccPK;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

@SuppressWarnings({ "unchecked", "static-access" })
public class Voucher5267MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher5267MsgServer.class);
	private Voucher voucher;
	private Map<String,TsQueryAmtDto> amtMap = null;
	private Map<String,TsSpecacctinfoDto> specacctMap = null;
	/**
	 * �����������е�ר������ƾ֤������ֻ����ҵ������
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
			logger.error("����ר������ƾ֤5267���ĳ��ִ���",e);
			throw new ITFEBizException("����ר������ƾ֤5267���ĳ��ִ���",e);
			
		}
		List VoucherBodyList  = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
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
		TvPayoutmsgmainDto maindto  = null;
		List subDtoList=null; 
		List lists=new ArrayList();
		List list=null;
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
			String Attach = ((Element) VoucherBodyList.get(i)).elementText("Attach");//������Ϣ
			String Id = elementVoucher.elementText("Id");//ר������ƾ֤Id
//			String admDivCode  = elementVoucher.elementText("AdmDivCode");//������������
//			String stYear = elementVoucher.elementText("StYear");//ҵ�����
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
			String PayeeAcctBankNo = elementVoucher.elementText("PayeeAcctBankNo");//�տ������к�
			String PayAcctNo = elementVoucher.elementText("PayAcctNo");//�������˺�
			String PayAcctName = elementVoucher.elementText("PayAcctName");//����������
			String PayAcctBankName = elementVoucher.elementText("PayAcctBankName");//����������
			String agencyCode=elementVoucher.elementText("AgencyCode");//Ԥ�㵥λ����
			String agencyName=elementVoucher.elementText("AgencyName");//Ԥ�㵥λ����
			String PaySummaryCode = elementVoucher.elementText("PaySummaryCode");//��;����
			String PaySummaryName = elementVoucher.elementText("PaySummaryName"); //��;����
			String PayAmt = elementVoucher.elementText("PayAmt");//������
//			String xPayAmt = elementVoucher.elementText("XPayAmt");//ʵ�ʲ�����
//			String xPayDate = elementVoucher.elementText("XPayDate");//֧������
			String XAgentBusinessNo = elementVoucher.elementText("XAgentBusinessNo");//���н�����ˮ��
			String Hold1=elementVoucher.elementText("Hold1");//Ԥ���ֶ�1
//			String Hold2=elementVoucher.elementText("Hold2");//Ԥ���ֶ�2���˻ؽ��ռ��
			/**
			 * ��װTvPayoutmsgmainDto����
			 **/
			maindto  = new TvPayoutmsgmainDto();
			mainvou =dealnos.get(VoucherNo);//��ȡ���к�
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
//						if(ITFECommonConstant.PUBLICPARAM.indexOf(",verify=false,")>=0)
//						{
//							maindto.setScheckstatus(StateConstant.CHECKSTATUS_4);// ����״̬Ϊ�Ѹ���
//							maindto.setSchecksercode("admin");// ������
//						}else
//						{
							maindto.setScheckstatus(StateConstant.CHECKSTATUS_1);//�Ѳ�¼
//						}
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
			maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);//Ԥ������(Ĭ��Ԥ����)
			TsInfoconnorgaccPK tmppk = new TsInfoconnorgaccPK();
			tmppk.setSorgcode(ls_OrgCode); // ��������
			tmppk.setStrecode(TreCode); // TreCode��������
			tmppk.setSpayeraccount(PayAcctNo);// �����˺�
			TsInfoconnorgaccDto resultdto = null;
			try {
				resultdto = (TsInfoconnorgaccDto) DatabaseFacade.getDb().find(tmppk);
				if(resultdto!=null)
				{
					maindto.setSbudgettype(resultdto.getSbiztype());
				}
			} catch (JAFDatabaseException e) {
				logger.error("��ѯ����˻��쳣:" + e.getMessage());
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
			maindto.setSid(Id); //ר������ƾ֤Id
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
			maindto.setSbudgetunitcode(agencyCode);//Ԥ�㵥λ����
			maindto.setSunitcodename(agencyName);//Ԥ�㵥λ����
			agencyCodeList = new HashSet();//Ԥ�㵥λ����list
			agencyCodeList.add(agencyCode);
			expFuncCodeList = new ArrayList<String>();//Ԥ���Ŀ����list
			amtList = new ArrayList<BigDecimal>();//�����list
			Set<String> subcodeSet = new HashSet<String>();//�洢֧����Ŀ����
			List<String>  subDtoIdList = new ArrayList<String>();//�ӱ���ϸId����
			/**
			 * ��װTvPayoutmsgsubDto����
			 */
			subDtoList=new ArrayList<TvPayoutmsgsubDto>();
			
			for(int j=0;j<listDetail.size();j++){
				
				//��ϸ�ϼƽ��
				Element elementDetail  = (Element)listDetail.get(j);
				String sId=(j+1)+"";//������ϸ���
				String sVoucherBillId=elementDetail.elementText("VoucherBillId");//����ƾ֤Id
				String BgtTypeCode = elementDetail.elementText("BgtTypeCode");//Ԥ�����ͱ���
				String BgtTypeName = elementDetail.elementText("BgtTypeName");//Ԥ����������
				String depProCode = elementDetail.elementText("DepProCode");//Ԥ����Ŀ����
				String depProName = elementDetail.elementText("DepProName");//Ԥ����Ŀ����
				if(depProName!=null && !depProName.equals("") && new String(depProName.getBytes("GBK"),"iso-8859-1").length()>42)
					depProName = FileUtil.subStringOfCh(depProName, 42, "GBK");
				String ProCatCode = elementDetail.elementText("ProCatCode");//��֧�������
				String ProCatName = elementDetail.elementText("ProCatName");//��֧��������
				String payeeAcctNo = elementDetail.elementText("PayeeAcctNo");//�տ����˺�
				String payeeAcctName = elementDetail.elementText("PayeeAcctName");//�տ�������
				String payeeAcctBankName = elementDetail.elementText("PayeeAcctBankName");//�տ�������
//				String payeeAcctBankNo = elementDetail.elementText("PayeeAcctBankNo");//�տ������к�
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
				String projectCategoryCode = elementDetail.elementText("ProjectCategoryCode");//ר����Ŀ�������
				String projectCategoryName = elementDetail.elementText("projectCategoryName");//ר����Ŀ��������
				String sPayAmt = elementDetail.elementText("PayAmt");//֧�����
				String sdetailHold1 = elementDetail.elementText("Hold1");//Ԥ���ֶ�1
//				String sdetailHold2 = elementDetail.elementText("Hold2");//Ԥ���ֶ�2
//				String sdetailHold3 = elementDetail.elementText("Hold3");//Ԥ���ֶ�3
//				String sdetailHold4 = elementDetail.elementText("Hold4");//Ԥ���ֶ�4
				String 	subId=elementDetail.elementText("Id");//����ƾ֤��ϸId
				
				TvPayoutmsgsubDto subdto=new TvPayoutmsgsubDto();
				//�˴�����ֵ��ȷ��
				subdto.setSbizno(mainvou);//�ӱ����к�
				subdto.setSseqno(Integer.valueOf(sId));//�ӱ���ϸ���
				subdto.setSid(subId);//����ƾ֤��ϸId
				subdto.setSaccdate(currentDate);//��������
				subdto.setSecnomicsubjectcode(ExpEcoCode);//���ÿ�Ŀ����
				subdto.setStaxticketno(sVoucherBillId);//����ƾ֤Id
				subdto.setSbudgetprjcode(depProCode);//Ԥ����Ŀ����
				subdto.setSbgttypecode(BgtTypeCode);//Ԥ�����ͱ���
				subdto.setSbgttypename(BgtTypeName);//Ԥ����������
				subdto.setSprocatcode(ProCatCode);//��֧�������
				subdto.setSprocatname(ProCatName);//��֧��������
				subdto.setSagencycode(agencyCode);//Ԥ�㵥λ����
				subdto.setSagencyname(agencyName);//Ԥ�㵥λ����
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
				String  SubXpayAmt = elementDetail.elementText("XpayAmt");//���(ʵ��֧�������������ڻص��в�¼)
				String  SubXPayDate = elementDetail.elementText("XPayDate");//֧������(�������д������ڣ����������ڻص��в�¼)
				String  SubXAgentBusinessNo = elementDetail.elementText("XAgentBusinessNo");//���н�����ˮ��(���������ڻص��в�¼)
				String  SubXAddWord = elementDetail.elementText("XAddWord");//ʧ��ԭ��
				BigDecimal subPayAmt = new BigDecimal(sPayAmt);
				subdto.setNmoney(subPayAmt);//֧�����
				subdto.setShold1(sdetailHold1);//Ԥ���ֶ�1
				subdto.setShold2(projectCategoryCode);//ר����Ŀ�������
				subdto.setShold3(projectCategoryName);//ר����Ŀ��������
				subdto.setShold4(depProName);//Ԥ���ֶ�4
				subdto.setSid(subId);//����ƾ֤��ϸId/
				subdto.setSpayeeacctno(payeeAcctNo);//�տ����˺�
				subdto.setSpayeeacctname(payeeAcctName);//�տ�������
				subdto.setSpayeeacctbankname(payeeAcctBankName);//�տ�������
				if(StringUtils.isBlank(SubXpayAmt)){
					SubXpayAmt="0.00";
				}
				subdto.setSxpayamt(new BigDecimal(SubXpayAmt));//���(ʵ��֧�������������ڻص��в�¼)
				subdto.setSxpaydate(SubXPayDate);//֧������(�������д������ڣ����������ڻص��в�¼)
				subdto.setSxagentbusinessno(SubXAgentBusinessNo);//���н�����ˮ��(���������ڻص��в�¼)
				subdto.setSxaddword(SubXAddWord);//ʧ��ԭ��
				//���ù��ܿ�ĿLIST,��ϸ�ϼ�
				expFuncCodeList.add(ExpFuncCode);
				sumAmt = sumAmt.add(subPayAmt); 
				subDtoList.add(subdto);
				subDtoIdList.add(subdto.getSid());
			}
			vDto=new TvVoucherinfoDto();
			vDto.setSdealno(mainvou);
			vDto=(TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
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
				TsQueryAmtDto queryAmtDto =  getAmtMap().get(maindto.getSorgcode()+maindto.getStrecode()+VtCode);
				if (null!=queryAmtDto)
				{
					BigDecimal moneyfilter = queryAmtDto.getFendamt();
					if (moneyfilter.compareTo(maindto.getNmoney())<=0) 
					{
						vDto.setSext2("1");//ʵ������
						vDto.setSreturnerrmsg("ר�����");
						vDto = (TvVoucherinfoDto) DatabaseFacade.getODB().updateWithResult(vDto);
					}
				}else if(maindto.getSrecname()!=null&&maindto.getSrecname().contains("����"))
				{
					vDto.setSext2("1");//�տ��˻�Ϊʵ��ר��
					vDto.setSreturnerrmsg(vDto.getSreturnerrmsg()+"�տ��˻�Ϊʵ��ר��,��ר���������в����ڣ�");
					vDto = (TvVoucherinfoDto) DatabaseFacade.getODB().updateWithResult(vDto);
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
					vDto.setSreturnerrmsg(vDto.getSreturnerrmsg()+"ר���տ��˻�Ϊ����ר��,��ר���������в����ڣ�");
					vDto = (TvVoucherinfoDto) DatabaseFacade.getODB().updateWithResult(vDto);
				}
				if((vDto.getSorgcode()+"27101").equals(maindto.getSpayeracct())){
					String errMsg="ר��ʵ���ʽ𸶿��˺Ų���Ϊ"+vDto.getSorgcode()+"27101";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
			}
			if(maindto.getSrecbankno()!=null&&maindto.getSrecbankno().startsWith("011"))
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
			verifydto.setPaytypecode(PayTypeCode);//֧����ʽ����
			verifydto.setFundTypeCode(FundTypeCode);//�ʽ����ʱ���
			
			String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_5267);
			
			//���ϵ�˰��ɫУ��
			if(ITFECommonConstant.SRC_NODE.equals("000087100006")&&VtCode.equals(MsgConstant.VOUCHER_NO_5267)&&
					!Attach.equals("")&&Attach!=null&&Attach.trim().length()==3){
				if(subcodeSet!=null&&subcodeSet.size()==1){
					TsTaxpayacctDto taxPayAcctDto = new TsTaxpayacctDto();
					taxPayAcctDto.setSorgcode(ls_OrgCode);
					taxPayAcctDto.setStrecode(TreCode);
					taxPayAcctDto.setSpayeracct(PayAcctNo);
					taxPayAcctDto.setStaxsubcode(subcodeSet.iterator().next().trim());
					List<TsTaxpayacctDto> taxList = CommonFacade.getODB().findRsByDto(taxPayAcctDto);
					if(taxList==null||taxList.size()<=0){
						returnmsg+="���⸶���˻�["+PayAcctNo+"]���˰��Ŀ����["+taxPayAcctDto.getStaxsubcode()+"]����Ӧ��";
					}
				}
			}
			
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
			 * У���Ϻ���ϸ��Ϣ�Ϸ���
			 * @author �Ż��
			 */
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0&&null!=subDtoList&&subDtoList.size()>0){
				String returnMsg = voucherVerify.checkVerify(subDtoList,MsgConstant.VOUCHER_NO_5267);
				if(StringUtils.isNotBlank(returnMsg)){
					voucher.voucherComfail(vDto.getSdealno(), returnMsg);
					continue;
				}
			}
			
			
			/**
			 * У����ϸId�Ƿ�Ϊ�ջ��ظ�
			 */
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0){
				String checkIdMsg=voucherVerify.checkValidSudDtoId(subDtoIdList);
				if(checkIdMsg!=null){
					//���ش�����Ϣǩ��ʧ��
					voucher.voucherComfail(mainvou, checkIdMsg);
					continue;				
				}
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
			
			/**
			 * ���ʵ���ʽ���ϸ�б�,������8207������ϸ�ȶԣ��Ϻ���
			 * @author �Ż��
			 */
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0&&maindto.getSbusinesstypecode().equals(StateConstant.BIZTYPE_CODE_BATCH)){
				list.add(subDtoList);
			}
			
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
}

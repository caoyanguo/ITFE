package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.persistence.pk.TsInfoconnorgaccPK;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;


public class Voucher5106MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher5106MsgServer.class);
	private Voucher voucher;
	/**
	 * �����������е���Ȩ֧�����ƾ֤5106������ֻ����ҵ������
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();
		String voucherXml = (String) muleMessage.getPayload();
		String ls_FileName = (String) muleMessage.getProperty("fileName");
		String ls_OrgCode = (String) muleMessage.getProperty("orgCode");
		HashMap<String,String> dealnos=(HashMap<String,String>)muleMessage.getProperty("dealnos");
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("������Ȩ֧�����ƾ֤5106���ĳ��ִ���",e);
			throw new ITFEBizException("������Ȩ֧�����ƾ֤5106���ĳ��ִ���",e);
			
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
		Map<String,BigDecimal> agencyCodeMap = null;
		//Ԥ���Ŀ����list
		ArrayList<String> expFuncCodeList = null; 
		//Ԥ�㵥λ����list
		List agencyCodeList = null;
		
		//String sAgencyCode ="";
		TvGrantpaymsgmainDto maindto  = null;
		List mainDtoList=null; 
		List<TvGrantpaymsgsubDto> subDtoList=null; 
		List lists=new ArrayList();
		List<Object> list=null;
		//ƾ֤���к�
		String mainvou="";
		TvVoucherinfoDto vDto=new TvVoucherinfoDto();
		String PayAcctNo="";//�������˺�
		String PayAcctName="";//����������
		String PayeeAcctNo="";//�տ����˺�
		String PayeeAcctName="";//�տ�������
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
			String Id = elementVoucher.elementText("Id");//������Ȩ֧������������֪ͨ��Id
			//String VoucherNo  = elementVoucher.elementText("VoucherNo");//ƾ֤��
			String VouDate = elementVoucher.elementText("VouDate"); //ƾ֤����
			String TreCode = elementVoucher.elementText("TreCode"); //�����������
			String FinOrgCode = elementVoucher.elementText("FinOrgCode");//�������ش���
			String PlanAmtSum = elementVoucher.elementText("PlanAmt");//�ϼ������Ƚ��
			String ClearVoucherNo = "";//�������뻮���
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0){
			     ClearVoucherNo = elementVoucher.elementText("ClearVoucherNo");//�������뻮���,5106����.ClearVoucherNo=2301����.VoucherNo(5351����.VoucherNo)
			}
			if(ITFECommonConstant.PUBLICPARAM.contains(",quotapayinfo,"))
			{
				PayAcctNo=elementVoucher.elementText("PayAcctNo");//�������˺�
				PayAcctName=elementVoucher.elementText("PayAcctName");//����������
				PayeeAcctNo=elementVoucher.elementText("PayeeAcctNo");//�տ����˺�
				PayeeAcctName=elementVoucher.elementText("PayeeAcctName");//�տ�������
			}
			String SetMonth = elementVoucher.elementText("SetMonth");//�ƻ��·�
			String DeptNum = elementVoucher.elementText("DeptNum");//һ��Ԥ�㵥λ����
			String FundTypeCode = elementVoucher.elementText("FundTypeCode");//�ʽ����ʱ���
			String FundTypeName = elementVoucher.elementText("FundTypeName");//�ʽ���������
			String ClearBankCode = elementVoucher.elementText("ClearBankCode");//�������б���
			String ClearBankName = elementVoucher.elementText("ClearBankName");//������������
			String PayBankCode = elementVoucher.elementText("PayBankCode");//�������б���
			String PayBankName = elementVoucher.elementText("PayBankName");//������������
			String PayBankNo = elementVoucher.elementText("PayBankNo");//���������к�
			String Hold1=elementVoucher.elementText("Hold1");//Ԥ���ֶ�1
			String Hold2=elementVoucher.elementText("Hold2");//Ԥ���ֶ�2
			
			/**
			 * ��װTvGrantpaymsgmainDto����
			 **/
			mainDtoList = new ArrayList();
			maindto  = new TvGrantpaymsgmainDto();
			mainvou = dealnos.get(VoucherNo).toString();
	        maindto.setIvousrlno(Long.valueOf(Long.parseLong(mainvou)));
			maindto.setNmoney(BigDecimal.valueOf(Double.valueOf(PlanAmtSum)));
			maindto.setSaccdate(currentDate);
			maindto.setSamttype(MsgConstant.AMT_KIND_BANK);
			if("000073100012".equals(ITFECommonConstant.SRC_NODE)){//�����ʽ�������ΪԤ������
				maindto.setSbudgettype(FundTypeCode);
			}else{
				if(ITFECommonConstant.PUBLICPARAM.contains(",quotabudgettype,"))
				{
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
						else
						{
							if("1".equals(FundTypeCode))
							{
								maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);
							}else
							{
								maindto.setSbudgettype(MsgConstant.BDG_KIND_OUT);
							}
						}
					} catch (JAFDatabaseException e) {
						if("1".equals(FundTypeCode))
						{
							maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);
						}else
						{
							maindto.setSbudgettype(MsgConstant.BDG_KIND_OUT);
						}
					}
				}else
				{
					maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);
				}
			}
			maindto.setSbudgetunitcode("");
			maindto.setSclearbankcode(ClearBankCode);
			maindto.setSclearbankname(ClearBankName);
			maindto.setScommitdate(currentDate);
			maindto.setSdealno(mainvou.substring(8, 16));
			//maindto.setSdemo(_sdemo);
			maindto.setSdeptnum(DeptNum);
			maindto.setSfilename(ls_FileName);
			maindto.setSfundtypecode(FundTypeCode);
			maindto.setSfundtypename(FundTypeName);
			maindto.setSgenticketdate(VouDate);
			maindto.setShold1(Hold1);
			maindto.setShold2(Hold2);
			maindto.setSid(Id);
			maindto.setSlimitid("0");
			maindto.setSofmonth(SetMonth);
			maindto.setSofyear(StYear);
			maindto.setSorgcode(ls_OrgCode);
			maindto.setSpackageno("0000");
			maindto.setSpackageticketno(VoucherNo);
			maindto.setSpaybankcode(PayBankCode);
			maindto.setSpaybankname(PayBankName);
			maindto.setSpaybankno(PayBankNo);
			maindto.setSpayunit(FinOrgCode);
			maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);
			maindto.setStransactunit(PayBankNo);
			//maindto.setStransbankcode(_stransbankcode);
			maindto.setStrecode(TreCode);
			maindto.setSusercode("0000");
			//maindto.setSxacctdate(_sxacctdate);
			maindto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0){
				maindto.setSclearvoucherno(ClearVoucherNo);
			}
			//������Ϻ���ɫ�������������Ϊ�㣬��ô�������ȱ�־����Ϊ��ͬԤ�㵥λ����Ҫ�ְ�
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0&&maindto.getNmoney().compareTo(BigDecimal.ZERO)==0){
				//ռ���ֶ�S_HOLD1��Ϊ���ȱ�־��������Ĭ��ֵ5351
				maindto.setShold1(MsgConstant.VOUCHER_NO_5351);
			}
			//Ԥ�㵥λ����list
			agencyCodeMap = new HashMap();
			//Ԥ���Ŀ����list
			expFuncCodeList = new ArrayList<String>(); 
			//��ϸ�ϼƽ��
			
			agencyCodeList = new ArrayList<String>();
			/**
			 * ��װTvPayoutmsgsubDto����
			 */
			subDtoList=new ArrayList<TvGrantpaymsgsubDto>();
			String money=null;
			for(int j=0;j<listDetail.size();j++){
		
				Element elementDetail  = (Element)listDetail.get(j);
				Element sdetailIdElement=elementDetail.element("Id");
				String SubId = "";
				//�ڵ㲻����
				if(sdetailIdElement!=null)
					SubId = elementDetail.elementText("Id");//��ϸID				
				
				String SupDepCode = elementDetail.elementText("SupDepCode");//һ��Ԥ�㵥λ����
				String SupDepName = elementDetail.elementText("SupDepName");//һ��Ԥ�㵥λ����
				if(new String(SupDepName.getBytes("GBK"),"iso-8859-1").length()>60)
					SupDepName = CommonUtil.subString(SupDepName,60);
				String ExpFuncCode = elementDetail.elementText("ExpFuncCode");//֧�����ܷ����Ŀ����
				String ExpFuncName = elementDetail.elementText("ExpFuncName");//֧�����ܷ����Ŀ����
				String ExpFuncCode1 = elementDetail.elementText("ExpFuncCode1");//֧�����ܷ����Ŀ�����
				String ExpFuncName1 = elementDetail.elementText("ExpFuncName1");//֧�����ܷ����Ŀ������
				String ExpFuncCode2 = elementDetail.elementText("ExpFuncCode2");//֧�����ܷ����Ŀ�����
				String ExpFuncName2 = elementDetail.elementText("ExpFuncName2");//֧�����ܷ����Ŀ������
				String ExpFuncCode3 = elementDetail.elementText("ExpFuncCode3");//֧�����ܷ����Ŀ�����
				String ExpFuncName3 = elementDetail.elementText("ExpFuncName3");//֧�����ܷ����Ŀ������
				String ProCatCode = elementDetail.elementText("ProCatCode");//��֧�������
				String ProCatName = elementDetail.elementText("ProCatName");//��֧��������
				String PlanAmt = elementDetail.elementText("PlanAmt");//֧�����
				String sdetailHold1 = elementDetail.elementText("Hold1");//Ԥ���ֶ�1,��Ϊ�ۼ��´���(�Ϻ���չ)
				String sdetailHold2 = elementDetail.elementText("Hold2");//Ԥ���ֶ�2
				String sdetailHold3 = elementDetail.elementText("Hold3");//Ԥ���ֶ�3
				String sdetailHold4 = elementDetail.elementText("Hold4");//Ԥ���ֶ�4
				
				/****************���������ֶ��޸�20161020****************/
				String FundTypeCodeSub = elementDetail.elementText("FundTypeCode");//	�ʽ����ʱ���
				String FundTypeNameSub = elementDetail.elementText("FundTypeName");//	�ʽ���������
				String AgencyCode = elementDetail.elementText("AgencyCode");//����Ԥ�㵥λ����
				String AgencyName = elementDetail.elementText("AgencyName");//����Ԥ�㵥λ����
				String ExpEcoCode = elementDetail.elementText("ExpEcoCode");//���÷����Ŀ����
				String ExpEcoName = elementDetail.elementText("ExpEcoName");//���÷����Ŀ����
				String DepProCode = elementDetail.elementText("DepProCode");//Ԥ����Ŀ����
				String DepProName = elementDetail.elementText("DepProName");//Ԥ����Ŀ����
				String AgencyAccoCode = elementDetail.elementText("AgencyAccoCode");//	��λ������˻�
				String AgencyBankName = elementDetail.elementText("AgencyBankName");//	��λ������ʻ�������
				/****************���������ֶ��޸�20161020****************/
				
				TvGrantpaymsgsubDto subdto=new TvGrantpaymsgsubDto();
				//�˴�����ֵ��ȷ��
				subdto.setIdetailseqno(j+1);
				subdto.setIvousrlno(Long.parseLong(mainvou));
				subdto.setNmoney(BigDecimal.valueOf(Double.valueOf(PlanAmt)));
				subdto.setSaccattrib(MsgConstant.ACCT_PROP_ZERO);
				subdto.setSaccdate(currentDate);//��������
				subdto.setSbudgettype(maindto.getSbudgettype());
				subdto.setSbudgetunitcode(SupDepCode);
				subdto.setSdealno(mainvou.substring(8, 16));
				//subdto.setSdemo(_sdemo);
				//subdto.setSecosubjectcode(_secosubjectcode);
				subdto.setSexpfuncname(ExpFuncName);//֧�����ܷ����Ŀ����
				subdto.setSexpfunccode1(ExpFuncCode1);//֧�����ܷ����Ŀ�����
				subdto.setSexpfuncname1(ExpFuncName1);//֧�����ܷ����Ŀ������
				subdto.setSexpfunccode2(ExpFuncCode2);//֧�����ܷ����Ŀ�����
				subdto.setSexpfuncname2(ExpFuncName2);//֧�����ܷ����Ŀ������
				subdto.setSexpfunccode3(ExpFuncCode3);//֧�����ܷ����Ŀ�����
				subdto.setSexpfuncname3(ExpFuncName3);//֧�����ܷ����Ŀ������
				subdto.setSfilename(ls_FileName);
				subdto.setSfunsubjectcode(ExpFuncCode);
				subdto.setShold1(sdetailHold1);//Ԥ���ֶ�1
				subdto.setShold2(sdetailHold2);//Ԥ���ֶ�2
				subdto.setShold3(sdetailHold3);//Ԥ���ֶ�3
				subdto.setShold4(sdetailHold4);//Ԥ���ֶ�4
				subdto.setSlimitid("0");
				subdto.setSline("1");
				subdto.setSofyear(StYear);
				subdto.setSorgcode(ls_OrgCode);
				subdto.setSpackageno("0000");
				subdto.setSpackageticketno(VoucherNo);
				subdto.setSprocatcode(ProCatCode);//��֧�������
				subdto.setSprocatname(ProCatName);//��֧��������
				subdto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);
				subdto.setSsupdepname(SupDepName);
				subdto.setSusercode("0000");
				subdto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));
				subdto.setSfunsubjectcode(ExpFuncCode);//֧�����ܷ����Ŀ����
				BigDecimal subPayAmt = new BigDecimal(PlanAmt);
				subdto.setNmoney(subPayAmt);//֧�����
				if(ITFECommonConstant.PUBLICPARAM.indexOf(",beijing,")<0){
					subdto.setSid(SubId);
				}
				//����Ԥ�㵥λ�����ͳ��
				if(subdto.getNmoney().signum()==-1)
					money="-";
				else
					money="+";
				if(agencyCodeMap.containsKey(subdto.getSbudgetunitcode()+money)){
					BigDecimal lbigD  = agencyCodeMap.get(subdto.getSbudgetunitcode()+money).add(subdto.getNmoney());
					agencyCodeMap.put(subdto.getSbudgetunitcode()+money,lbigD);
				}else{
					agencyCodeMap.put(subdto.getSbudgetunitcode()+money, subdto.getNmoney());
				}
				
				/****************���������ֶ��޸�20161020****************/
				//��ʱͨ���ж����������ֶ��Ƿ���ֵ��ȷ������ֶε���Դ�����ڿ���ͨ��Area���߽ڵ�ŵȷ�ʽ���ж�
				if(StringUtils.isNotBlank(FundTypeCodeSub) && StringUtils.isNotBlank(FundTypeNameSub)) {
					subdto.setSfundtypecode(FundTypeCodeSub); //�ʽ����ʱ���
					subdto.setSfundtypename(FundTypeNameSub); //�ʽ���������
					subdto.setSagencycode(AgencyCode); //����Ԥ�㵥λ����
					subdto.setSagencyname(AgencyName); //����Ԥ�㵥λ����
					subdto.setSexpecocode(ExpEcoCode); //���÷����Ŀ����
					subdto.setSexpeconame(ExpEcoName); //���÷����Ŀ����
					subdto.setSdepprocode(DepProCode); //Ԥ����Ŀ����
					subdto.setSdepproname(DepProName); //Ԥ����Ŀ����
					subdto.setSagencyaccocode(AgencyAccoCode); //��λ������˻�
					subdto.setSagencybankname(AgencyBankName); //��λ������ʻ�������	
				}
				
				agencyCodeList.add(TreCode+SupDepCode);
				expFuncCodeList.add(ExpFuncCode);
				
				sumAmt = sumAmt.add(subPayAmt); 
				subDtoList.add(subdto);
			}
			String ls_VoucherEnd = "";
			
			//���ݽ�����ˮ��ȡ��ƾ֤������dto
			vDto=new TvVoucherinfoDto();
			vDto.setSdealno(mainvou);
			vDto=(TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
			
			int k = 0;
			if(agencyCodeMap.size()>1){
				k = 0;
				Set subSet = agencyCodeMap.entrySet();
				if(subSet!=null&&subSet.size()>1000)
				{
					String errMsg = "Ԥ�㵥λ��������С��1000!";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				for(Iterator subIt = subSet.iterator(); subIt.hasNext();){
					ls_VoucherEnd = "000";
					ls_VoucherEnd = ls_VoucherEnd + k;
					ls_VoucherEnd = ls_VoucherEnd.substring(ls_VoucherEnd.length()-3, ls_VoucherEnd.length());
					Map.Entry<String, BigDecimal> subEntry = (Map.Entry<String, BigDecimal>) subIt.next();   
					String ls_budgetunitcode = subEntry.getKey();
					BigDecimal  sumSubMoney = subEntry.getValue();
					
					TvGrantpaymsgmainDto newTvGrantpaymsgmainDto = (TvGrantpaymsgmainDto) maindto.clone();
					String TraNo = VoucherUtil.getGrantSequence();  
					//ƾ֤��ˮ��
					String ls_VoucherNo  = maindto.getSpackageticketno()+ls_VoucherEnd;
					newTvGrantpaymsgmainDto.setSpackageticketno(ls_VoucherNo);
					newTvGrantpaymsgmainDto.setNmoney(sumSubMoney);
					if(ls_budgetunitcode.endsWith("-")||ls_budgetunitcode.endsWith("+"))
						newTvGrantpaymsgmainDto.setSbudgetunitcode(ls_budgetunitcode.substring(0,ls_budgetunitcode.length()-1));
					else
						newTvGrantpaymsgmainDto.setSbudgetunitcode(ls_budgetunitcode);
					newTvGrantpaymsgmainDto.setSdealno(TraNo.substring(8, 16));
					mainDtoList.add(newTvGrantpaymsgmainDto);
					int sum = 0;
					for(TvGrantpaymsgsubDto subDto:subDtoList){
						if(subDto.getNmoney().signum()==-1)
							money="-";
						else
							money="+";
						if((subDto.getSbudgetunitcode()+money).equals(ls_budgetunitcode)){
							subDto.setSpackageticketno(ls_VoucherNo);
							subDto.setIvousrlno(newTvGrantpaymsgmainDto.getIvousrlno());
							sum++;
						}
					}
					/**
					 *��ϸ�������ܳ���500
					 * 
					 */
					if (sum > 499) {
						String errMsg = "ÿ��Ԥ�㵥λ��ϸ��������С��500!";
						voucher.voucherComfail(vDto.getSdealno(), errMsg);
						continue;
					}
					k++;
				}
			}else{
				/**
				 *��ϸ�������ܳ���500
				 * 
				 */
				if (subDtoList.size() > 499) {
					String errMsg = "ÿ��Ԥ�㵥λ��ϸ��������С��500!";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				maindto.setSbudgetunitcode(subDtoList.get(0).getSbudgetunitcode());
				mainDtoList.add(maindto);
			}
			
			/**
			 * ��װverifydto,���б���У��
			 */
			verifydto.setTrecode(TreCode);
			verifydto.setFinorgcode(FinOrgCode);
			verifydto.setVoucherno(VoucherNo);
			verifydto.setVoudate(VouDate);
			verifydto.setPaybankno(PayBankNo);
			//������ȡ��·ݣ��ܽ���У�� by renqingbin
			verifydto.setOfyear(StYear);
			verifydto.setOfmonth(SetMonth);
			verifydto.setFamt(PlanAmtSum);
			verifydto.setPayVoucherNo(ClearVoucherNo);
			String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_5106);
			if(returnmsg != null){//���ش�����Ϣǩ��ʧ��
				voucher.voucherComfail(vDto.getSdealno(), returnmsg);
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
			 *У��ƾ֤��Ų��ܴ���17λ
			 *
			 */
			if(maindto.getSpackageticketno().getBytes().length>17){
				String errMsg="ƾ֤��Ų��ܴ���17λ��";
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
			}
			
			/**
			 *У��������=0.00����ϸ����=1 
			 *
			 */
			if(maindto.getNmoney().compareTo(BigDecimal.ZERO) == 0 && subDtoList.size()==1){
				String errMsg="������"+maindto.getNmoney()+" ,��ϸ������1";
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
			}
		
			/**
			 * ҵ�����⣬У�鱨�ĺϷ���
			 * 
			 * ҵ�������ӱ����ʧ�ܼ�ǩ��ʧ��
			 */
			TvGrantpaymsgmainDto[] mainDtos=new TvGrantpaymsgmainDto[mainDtoList.size()];
			mainDtos=(TvGrantpaymsgmainDto[]) mainDtoList.toArray(mainDtos);
			TvGrantpaymsgsubDto[] subDtos=new TvGrantpaymsgsubDto[subDtoList.size()];
			subDtos=(TvGrantpaymsgsubDto[]) subDtoList.toArray(subDtos);
			
			DatabaseFacade.getODB().create(mainDtos);
			DatabaseFacade.getODB().create(subDtos);
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
				continue;
			}
			
			if(ITFECommonConstant.PUBLICPARAM.contains(",quotapayinfo,"))
			{
				maindto.setSclearbankcode(PayAcctNo) ;//�������˺�
				maindto.setSclearbankname(PayAcctName) ;//����������
				maindto.setShold1(PayeeAcctNo) ;//�տ����˺�
				maindto.setShold2(PayeeAcctName) ;//�տ�������
			}
			list=new ArrayList<Object>();
			list.add(maindto);
			list.add(vDto);			
			list.add(expFuncCodeList);
			list.add(agencyCodeList);
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0){
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
		}catch(Exception e){
			logger.error(e);
			VoucherException voucherE  = new VoucherException();
			voucherE.saveErrInfo(VtCode, e);
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

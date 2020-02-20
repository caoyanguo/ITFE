package com.cfcc.itfe.msgmanager.msg;



import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainSxDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubSxDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoSxDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.voucher.sxservice.VoucherVerifySX;
import com.cfcc.itfe.xmlparse.AbstractXmlParser;


public class VoucherSX5106MsgServer extends AbstractXmlParser {

	private static Log logger = LogFactory.getLog(VoucherSX5106MsgServer.class);
	private Voucher voucher;
	/**
	 * ���Ľ���������
	 * @param map  �ؼ���ϢMAP
	 * 		  key����orgcode��-value:��String����������
	 * 		  key����filename��-value:��String�� �ļ���
	 *        key: ��dealnomap��-value:��HashMap<String,String>��������ˮ��MAP     
	 * @param xmlString ������Ϣ
	 */
	public void dealMsg(HashMap map, String xmlString) throws ITFEBizException {
		String ls_FileName = (String) map.get("filename");
		String ls_OrgCode = (String) map.get("orgcode");
		HashMap<String,String> dealnos=(HashMap<String,String>)map.get("dealnomap");
		
		String voucherXml= xmlString;
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("������Ȩ֧�����ƾ֤5106���ĳ��ִ���",e);
			throw new ITFEBizException("������Ȩ֧�����ƾ֤5106���ĳ��ִ���",e);
			
		}
		List VoucherBodyList  = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
		String AdmDivCode  = "";//������������
		String StYear  = "";//���
		String VtCode  = "";//ƾ֤����
		
		String currentDate = TimeFacade.getCurrentStringTime();// ��ǰϵͳ����
		List<String> voucherList = new ArrayList<String>();
		
		//��ȡ�����������롢��Ⱥ�ƾ֤����
		if(VoucherBodyList.size()>0){
			//VoucherBody
			Element element  = (Element)VoucherBodyList.get(0);
			AdmDivCode  = element.attribute("AdmDivCode").getText();
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
		TvGrantpaymsgmainSxDto maindto  = null;
		List mainDtoList=null; 
		List<TvGrantpaymsgsubSxDto> subDtoList=null; 
		List lists=new ArrayList();
		List list=null;
		//ƾ֤���к�
		String mainvou="";
		TvVoucherinfoSxDto vDto=new TvVoucherinfoSxDto();
		//��������
		for(int i=0;i<VoucherBodyList.size();i++){
			try{
				//��ϸ�ϼƽ��
			BigDecimal sumAmt = new BigDecimal("0.00");	
			//VoucherBody
			Element element  = (Element)VoucherBodyList.get(i);
			//Voucher
			Element elementVoucher  = (Element)element.selectSingleNode("Voucher");
			//��ϸ��ϢList
			List listDetail  = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");
			//����У����Ϣdto
			VoucherVerifyDto verifydto = new VoucherVerifyDto();
			VoucherVerifySX voucherVerifySX = new VoucherVerifySX();	
			/**
			 * ƾ֤��Ϣ
			 */
			String Id = elementVoucher.elementText("Id");//������Ȩ֧������������֪ͨ��Id
			String VoucherNo  = elementVoucher.elementText("VoucherNo");//ƾ֤��
			String VouDate = elementVoucher.elementText("VouDate"); //ƾ֤����
			String TreCode = elementVoucher.elementText("TreCode"); //�����������
			String FinOrgCode = elementVoucher.elementText("FinOrgCode");//�������ش���
			String PlanAmtSum = elementVoucher.elementText("PlanAmt");//�ϼ������Ƚ��
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
			maindto  = new TvGrantpaymsgmainSxDto();
			mainvou = dealnos.get(VoucherNo).toString();
		    maindto.setIvousrlno(Long.valueOf(Long.parseLong(mainvou)));
	        maindto.setIvousrlno(Long.valueOf(Long.parseLong(mainvou)));
			maindto.setNmoney(BigDecimal.valueOf(Double.valueOf(PlanAmtSum)));
			maindto.setSaccdate(currentDate);
			maindto.setSamttype(MsgConstant.AMT_KIND_BANK);
			maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);
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
			//Ԥ�㵥λ����list
			agencyCodeMap = new HashMap();
			//Ԥ���Ŀ����list
			expFuncCodeList = new ArrayList<String>(); 
			//��ϸ�ϼƽ��
			
			agencyCodeList = new ArrayList<String>();
			/**
			 * ��װTvPayoutmsgsubDto����
			 */
			subDtoList=new ArrayList<TvGrantpaymsgsubSxDto>();
			for(int j=0;j<listDetail.size();j++){
		
				Element elementDetail  = (Element)listDetail.get(j);
				String SupDepCode = elementDetail.elementText("SupDepCode");//һ��Ԥ�㵥λ����
				String SupDepName = elementDetail.elementText("SupDepName");//һ��Ԥ�㵥λ����
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
				String sdetailHold1 = elementDetail.elementText("Hold1");//Ԥ���ֶ�1
				String sdetailHold2 = elementDetail.elementText("Hold2");//Ԥ���ֶ�2
				String sdetailHold3 = elementDetail.elementText("Hold3");//Ԥ���ֶ�3
				String sdetailHold4 = elementDetail.elementText("Hold4");//Ԥ���ֶ�4
				TvGrantpaymsgsubSxDto subdto=new TvGrantpaymsgsubSxDto();
				//�˴�����ֵ��ȷ��
				subdto.setIdetailseqno(j+1);
				subdto.setIvousrlno(Long.parseLong(mainvou));
				subdto.setNmoney(BigDecimal.valueOf(Double.valueOf(PlanAmt)));
				subdto.setSaccattrib(MsgConstant.ACCT_PROP_ZERO);
				subdto.setSaccdate(currentDate);//��������
				subdto.setSbudgettype(MsgConstant.BDG_KIND_IN);
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
				//����Ԥ�㵥λ�����ͳ��
				if(agencyCodeMap.containsKey(subdto.getSbudgetunitcode())){
					BigDecimal lbigD  = agencyCodeMap.get(subdto.getSbudgetunitcode()).add(subdto.getNmoney());
					agencyCodeMap.put(subdto.getSbudgetunitcode(),lbigD);
				}else{
					agencyCodeMap.put(subdto.getSbudgetunitcode(), subdto.getNmoney());
				}
				
			
				agencyCodeList.add(TreCode+SupDepCode);
				expFuncCodeList.add(ExpFuncCode);
				
				sumAmt = sumAmt.add(subPayAmt); 
				subDtoList.add(subdto);
			}
			String ls_VoucherEnd = "";
			int k = 0;
			if(agencyCodeMap.size()>1){
				k = 0;
				Set subSet = agencyCodeMap.entrySet();
				for(Iterator subIt = subSet.iterator(); subIt.hasNext();){
					k++;
					ls_VoucherEnd = "00000";
					ls_VoucherEnd = ls_VoucherEnd + k;
					ls_VoucherEnd = ls_VoucherEnd.substring(ls_VoucherEnd.length()-5, ls_VoucherEnd.length());
					Map.Entry<String, BigDecimal> subEntry = (Map.Entry<String, BigDecimal>) subIt.next();   
					String ls_budgetunitcode = subEntry.getKey();
					BigDecimal  sumSubMoney = subEntry.getValue();
					
					TvGrantpaymsgmainSxDto newTvGrantpaymsgmainDto = (TvGrantpaymsgmainSxDto) maindto.clone();
					String TraNo = VoucherUtil.getGrantSequence();  
					//ƾ֤��ˮ��
					String ls_VoucherNo  = maindto.getSpackageticketno()+ls_VoucherEnd;
					newTvGrantpaymsgmainDto.setSpackageticketno(ls_VoucherNo);
					newTvGrantpaymsgmainDto.setNmoney(sumSubMoney);
					newTvGrantpaymsgmainDto.setSbudgetunitcode(ls_budgetunitcode);
					newTvGrantpaymsgmainDto.setSdealno(TraNo.substring(8, 16));
					mainDtoList.add(newTvGrantpaymsgmainDto);
					for(TvGrantpaymsgsubSxDto subDto:subDtoList){
						if(subDto.getSbudgetunitcode().equals(ls_budgetunitcode)){
							subDto.setSpackageticketno(ls_VoucherNo);
							subDto.setIvousrlno(newTvGrantpaymsgmainDto.getIvousrlno());
						}
					}
				}
			}else{
				maindto.setSbudgetunitcode(subDtoList.get(0).getSbudgetunitcode());
				mainDtoList.add(maindto);
			}
			
			vDto=new TvVoucherinfoSxDto();
			vDto.setSdealno(mainvou);
			vDto=(TvVoucherinfoSxDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
			/**
			 * ��װverifydto,���б���У��
			 */
			verifydto.setTrecode(TreCode);
			verifydto.setFinorgcode(FinOrgCode);
			verifydto.setVoucherno(VoucherNo);
			verifydto.setVoudate(VouDate);
			verifydto.setPaybankno(PayBankNo);
			String returnmsg = voucherVerifySX.checkValid(verifydto, MsgConstant.VOUCHER_NO_5106);
			if(returnmsg != null){//���ش�����Ϣǩ��ʧ��
				voucher.voucherComfailForSX(vDto.getSdealno(), returnmsg);
				continue;
			}
			
			/**
			 *У���������Ƿ����ӱ������ 
			 *
			 */
			if(maindto.getNmoney().compareTo(sumAmt) != 0){
				String errMsg="�����������ϸ�ۼƽ���ȣ�������"+maindto.getNmoney()+" ��ϸ�ۼƽ� "+sumAmt;
				voucher.voucherComfailForSX(vDto.getSdealno(), errMsg);
				continue;
			}
			
			/**
			 *У��ƾ֤��Ų��ܴ���15λ
			 *
			 */
			if(maindto.getSpackageticketno().length()>20){
				String errMsg="ƾ֤��Ų��ܴ���16λ��";
				voucher.voucherComfailForSX(vDto.getSdealno(), errMsg);
				continue;
			}
		
			/**
			 * ҵ�����⣬У�鱨�ĺϷ���
			 * 
			 * ҵ�������ӱ����ʧ�ܼ�ǩ��ʧ��
			 */
			TvGrantpaymsgmainSxDto[] mainDtos=new TvGrantpaymsgmainSxDto[mainDtoList.size()];
			mainDtos=(TvGrantpaymsgmainSxDto[]) mainDtoList.toArray(mainDtos);
			TvGrantpaymsgsubSxDto[] subDtos=new TvGrantpaymsgsubSxDto[subDtoList.size()];
			subDtos=(TvGrantpaymsgsubSxDto[]) subDtoList.toArray(subDtos);
			
			DatabaseFacade.getODB().create(mainDtos);
			DatabaseFacade.getODB().create(subDtos);
			}catch(Exception e){
				logger.error(e);
				voucher.voucherComfailForSX(mainvou, "���Ĳ��淶��"+e.getMessage());
				continue;
			}
			
			list=new ArrayList();
			list.add(maindto);
			list.add(vDto);			
			list.add(expFuncCodeList);
			list.add(agencyCodeList);
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
			VoucherException voucherE  = new VoucherException();
			voucherE.saveErrInfo(VtCode, e);
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

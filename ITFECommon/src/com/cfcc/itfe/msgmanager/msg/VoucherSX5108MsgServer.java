package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.cfcc.itfe.persistence.dto.TvAmtControlInfoDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainSxDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubSxDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoSxDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.sxservice.VoucherVerifySX;
import com.cfcc.itfe.xmlparse.AbstractXmlParser;


public class VoucherSX5108MsgServer extends AbstractXmlParser {

	private static Log logger = LogFactory.getLog(VoucherSX5108MsgServer.class);
	private Voucher voucher;

	/**
	 * * ���Ľ���������
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
			logger.error("����ֱ��֧�����ƾ֤5108���ĳ��ִ���",e);
			throw new ITFEBizException("����ֱ��֧�����ƾ֤5108���ĳ��ִ���",e);
			
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
		
		//Ԥ�㵥λ����list
		List agencyCodeList = null;
		//Ԥ���Ŀ����list
		ArrayList<String> expFuncCodeList = null; 
		
		//String sAgencyCode ="";
		TvDirectpaymsgmainSxDto maindto  = null;
		List subDtoList=null; 
		List lists=new ArrayList();
		List list=null;
		//ƾ֤���к�
		String mainvou="";
		TvVoucherinfoSxDto vDto=new TvVoucherinfoSxDto();
		List<TvAmtControlInfoDto> amtList  = null;
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
			String Id = elementVoucher.elementText("Id");//����ֱ��֧������������֪ͨ��Id
			String VoucherNo  = elementVoucher.elementText("VoucherNo");//ƾ֤��
			String VouDate = elementVoucher.elementText("VouDate"); //ƾ֤����
			String TreCode = elementVoucher.elementText("TreCode"); //�����������
			String FinOrgCode = elementVoucher.elementText("FinOrgCode");//�������ش���
			String PayAmtSum = elementVoucher.elementText("PayAmt");//�ϼ������Ƚ��
			String FundTypeCode = elementVoucher.elementText("FundTypeCode");//�ʽ����ʱ���
			String FundTypeName = elementVoucher.elementText("FundTypeName");//�ʽ���������
			String PayeeAcctNo = elementVoucher.elementText("PayeeAcctNo");//�տ����˺�
			String PayeeAcctName = elementVoucher.elementText("PayeeAcctName");//�տ�������
			String PayeeAcctBankName = elementVoucher.elementText("PayeeAcctBankName");//�տ�����������
			String PayAcctNo = elementVoucher.elementText("PayAcctNo");//�������˺�
			String PayAcctName = elementVoucher.elementText("PayAcctName");//����������
			String PayAcctBankName = elementVoucher.elementText("PayAcctBankName");//��������������
			String PayBankCode = elementVoucher.elementText("PayBankCode");//�������б���
			String PayBankName = elementVoucher.elementText("PayBankName");//������������
			String PayBankNo = elementVoucher.elementText("PayBankNo");//���������к�
			String Hold1=elementVoucher.elementText("Hold1");//Ԥ���ֶ�1
			String Hold2=elementVoucher.elementText("Hold2");//Ԥ���ֶ�2
			
			/**
			 * ��װTvDirectpayfilemainDto����
			 **/
			maindto  = new TvDirectpaymsgmainSxDto();
			mainvou =dealnos.get(VoucherNo);//��ȡ���к�
			maindto.setIvousrlno(Long.parseLong(mainvou));
			maindto.setNmoney(BigDecimal.valueOf(Double.valueOf(PayAmtSum)));
			maindto.setSaccdate(currentDate);
			maindto.setSamttype(MsgConstant.AMT_KIND_BANK);
			maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);
			maindto.setScommitdate(currentDate);
			maindto.setSdealno(mainvou.substring(8, 16));
			//maindto.setSdemo(_sdemo);
			maindto.setSfilename(ls_FileName);
			maindto.setSfundtypecode(FundTypeCode);
			maindto.setSfundtypename(FundTypeName);
			maindto.setSgenticketdate(VouDate);
			maindto.setShold1(Hold1);
			maindto.setShold2(Hold2);
			maindto.setSid(Id);
			maindto.setSofyear(StYear);
			maindto.setSorgcode(ls_OrgCode);
			maindto.setSpackageno("0000");
			maindto.setSpackageticketno(VoucherNo);
			maindto.setSpayacctbankname(PayAcctBankName);
			maindto.setSpayacctname(PayAcctName);
			maindto.setSpayacctno(PayAcctNo);
			maindto.setSpaybankcode(PayBankCode);
			maindto.setSpaybankname(PayBankName);
			maindto.setSpaybankno(PayBankNo);
			maindto.setSpayeeacctbankname(PayeeAcctBankName);
			maindto.setSpayeeacctname(PayeeAcctName);
			maindto.setSpayeeacctno(PayeeAcctNo);
			maindto.setSpayunit(FinOrgCode);
			maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);
			maindto.setStaxticketno(VoucherNo);
			maindto.setStransactunit(PayBankNo);
			maindto.setStransbankcode("");
			maindto.setStrecode(TreCode);
			maindto.setSusercode("0000");
			maindto.setSxacctdate("");
			maindto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//ϵͳʱ��
			//Ԥ�㵥λ����list
			agencyCodeList = new ArrayList<String>();
			//Ԥ���Ŀ����list
			expFuncCodeList = new ArrayList<String>(); 
			//��ϸ�ϼƽ��
			subDtoList=new ArrayList<TvDirectpaymsgsubSxDto>();
			amtList = new ArrayList<TvAmtControlInfoDto>();
			/**
			 * ��װTvPayoutmsgsubDto����
			 */
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
				String PayAmt = elementDetail.elementText("PayAmt");//֧�����
				String sdetailHold1 = elementDetail.elementText("Hold1");//Ԥ���ֶ�1
				String sdetailHold2 = elementDetail.elementText("Hold2");//Ԥ���ֶ�2
				String sdetailHold3 = elementDetail.elementText("Hold3");//Ԥ���ֶ�3
				String sdetailHold4 = elementDetail.elementText("Hold4");//Ԥ���ֶ�4
				TvDirectpaymsgsubSxDto  subdto=new TvDirectpaymsgsubSxDto();
				subdto.setIdetailseqno(j+1);
				//subdto.setInoafterpackage(_inoafterpackage);
				subdto.setIvousrlno(Long.parseLong(mainvou));
				subdto.setNmoney(BigDecimal.valueOf(Double.valueOf(PayAmt)));
				subdto.setSaccdate(currentDate);
				subdto.setSagencycode(SupDepCode);
				subdto.setSagencyname(SupDepName);
				subdto.setSbudgetunitcode(SupDepCode);
				//subdto.setSdemo(_sdemo);
				//subdto.setSecosubjectcode(_secosubjectcode);
				subdto.setSexpfunccode1(ExpFuncCode1);
				subdto.setSexpfunccode2(ExpFuncCode2);
				subdto.setSexpfunccode3(ExpFuncCode3);
				subdto.setSexpfuncname(ExpFuncName);
				subdto.setSexpfuncname1(ExpFuncName1);
				subdto.setSexpfuncname2(ExpFuncName2);
				subdto.setSexpfuncname3(ExpFuncName3);
				subdto.setSfunsubjectcode(ExpFuncCode);
				subdto.setShold1(sdetailHold1);
				subdto.setShold2(sdetailHold2);
				subdto.setShold3(sdetailHold3);
				subdto.setShold4(sdetailHold4);
				subdto.setSline((j+1)+"");
				subdto.setSofyear(StYear);
				subdto.setSorgcode(ls_OrgCode);
				subdto.setSpackageno("");
				subdto.setSprocatcode(ProCatCode);
				subdto.setSprocatname(ProCatName);
				subdto.setStaxticketno(VoucherNo);
				subdto.setSusercode("0000");
				agencyCodeList.add(TreCode+SupDepCode);
				expFuncCodeList.add(ExpFuncCode);
				sumAmt = sumAmt.add(BigDecimal.valueOf(Double.valueOf(PayAmt))); 
				subDtoList.add(subdto);
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
			String returnmsg = voucherVerifySX.checkValid(verifydto, MsgConstant.VOUCHER_NO_5108);
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
			 * ҵ�����⣬У�鱨�ĺϷ���
			 * 
			 * ҵ�������ӱ����ʧ�ܼ�ǩ��ʧ��
			 */
			TvDirectpaymsgsubSxDto[] subDtos=new TvDirectpaymsgsubSxDto[subDtoList.size()];
			subDtos=(TvDirectpaymsgsubSxDto[]) subDtoList.toArray(subDtos);
			DatabaseFacade.getODB().create(maindto);
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

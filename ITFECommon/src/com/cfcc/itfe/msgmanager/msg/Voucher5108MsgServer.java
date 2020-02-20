package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.persistence.pk.TsInfoconnorgaccPK;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

@SuppressWarnings({ "static-access", "unchecked" })
public class Voucher5108MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher5108MsgServer.class);
	private Voucher voucher;
	/**
	 * �����������е�ֱ��֧�����ƾ֤5108������ֻ����ҵ������
	 */
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
			logger.error("����ֱ��֧�����ƾ֤5108���ĳ��ִ���",e);
			throw new ITFEBizException("����ֱ��֧�����ƾ֤5108���ĳ��ִ���",e);
			
		}
		List VoucherBodyList  = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
		String StYear  = "";//���
		String VtCode  = "";//ƾ֤����
		
		String currentDate = TimeFacade.getCurrentStringTime();// ��ǰϵͳ����
//		List<String> voucherList = new ArrayList<String>();
		
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
		TvDirectpaymsgmainDto maindto  = null;
		List subDtoList=null; 
		List lists=new ArrayList();
		List list=null;
		//ƾ֤���к�
		String mainvou="";
		TvVoucherinfoDto vDto=new TvVoucherinfoDto();
//		List<TvAmtControlInfoDto> amtList  = null;
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
			String Id = elementVoucher.elementText("Id");//����ֱ��֧������������֪ͨ��Id
			//String VoucherNo  = elementVoucher.elementText("VoucherNo");//ƾ֤��
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
			maindto  = new TvDirectpaymsgmainDto();
			mainvou =dealnos.get(VoucherNo);//��ȡ���к�
			maindto.setIvousrlno(Long.parseLong(mainvou));
			maindto.setNmoney(BigDecimal.valueOf(Double.valueOf(PayAmtSum)));
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
			maindto.setShold2(FinOrgCode);
			vDto=new TvVoucherinfoDto();
			vDto.setSdealno(mainvou);
			vDto=(TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
			
			
			/**�Ϻ���ֽ��
			 * ����������־Hold1
			 * ֧��ƾ֤�ţ�����ƾ֤�ţ�PayVoucherNo     
			 */
			verifydto.setFundTypeCode(FundTypeCode);
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0){
				if(Hold1==null||(Hold1!=null&&Hold1.equals("0"))){
					vDto.setSpaybankcode(PayBankNo);//���������ȣ����쵥λȡ�������
					maindto.setStransactunit(TreCode);
					maindto.setSamttype(MsgConstant.AMT_KIND_PEOPLE);
					verifydto.setBusinessTypeCode("0");	//��������
//					vDto.setScheckvouchertype(null);
//					vDto.setShold3(null);
				}					
				else if(Hold1!=null&&Hold1.equals("1")){
					vDto.setSpaybankcode(PayBankNo);//���������ȣ����쵥λȡ�������д���
					maindto.setStransactunit(PayBankNo);
				}
					
				else{
					String errMsg="����������־["+Hold1+"]��ֻ��Ϊ0-���������� 	1-����������!";
					voucher.voucherComfail(mainvou, errMsg);
					continue;
				}
				vDto.setShold1(Hold1);	//���ʺ�������ʶ
				String PayVoucherNo=elementVoucher.elementText("PayVoucherNo");//֧��ƾ֤�ţ�����ƾ֤�ţ�
				maindto.setSpayvoucherno(PayVoucherNo);
			}
			
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
			subDtoList=new ArrayList<TvDirectpaymsgsubDto>();
//			amtList = new ArrayList<TvAmtControlInfoDto>();
			//�ӱ���ϸId����
			List<String>  subDtoIdList = new ArrayList<String>();
			String sdetailId = null;//��ϸId
			
			/**
			 * ��װTvPayoutmsgsubDto����
			 */
			for(int j=0;j<listDetail.size();j++){
				
				Element elementDetail  = (Element)listDetail.get(j);
				/**
				 * У�鱨����ϸId�ڵ��Ƿ����
				 * 1�����ڵ㲻���ڣ����ϰ汾����������ϸId
				 * 2�����ڵ���ڣ����°汾��������ϸId
				 */
				Element sdetailIdElement=elementDetail.element("Id");
				//�ڵ㲻����
				if(sdetailIdElement==null)
					subDtoIdList.add("�ڵ㲻����");					
				else
					sdetailId = sdetailIdElement.getText();//��ϸId
									
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
				String PayAmt = elementDetail.elementText("PayAmt");//֧�����
				String sdetailHold1 = elementDetail.elementText("Hold1");//Ԥ���ֶ�1
				String sdetailHold2 = elementDetail.elementText("Hold2");//Ԥ���ֶ�2
				String sdetailHold3 = elementDetail.elementText("Hold3");//Ԥ���ֶ�3
				String sdetailHold4 = elementDetail.elementText("Hold4");//Ԥ���ֶ�4
				
				/****************���������ֶ��޸�20161020****************/
				String DirectVoucherNo = elementDetail.elementText("DirectVoucherNo"); //֧���������         
				String FundTypeCodeSub = elementDetail.elementText("FundTypeCode"); //�ʽ����ʱ���        
				String FundTypeNameSub = elementDetail.elementText("FundTypeName"); //�ʽ���������        
				String AgencyCode = elementDetail.elementText("AgencyCode"); //����Ԥ�㵥λ����    
				String AgencyName = elementDetail.elementText("AgencyName"); //����Ԥ�㵥λ����    
				String ExpEcoCode = elementDetail.elementText("ExpEcoCode"); //֧�����÷����Ŀ����
				String ExpEcoName = elementDetail.elementText("ExpEcoName"); //֧�����÷����Ŀ����
				String PayeeAcctNoSub = elementDetail.elementText("PayeeAcctNo"); //�տ����˺�          
				String PayeeAcctNameSub = elementDetail.elementText("PayeeAcctName"); //�տ�������          
				String PayeeAcctBankNameSub = elementDetail.elementText("PayeeAcctBankName"); //�տ�������  
				/****************���������ֶ��޸�20161020****************/
				
				TvDirectpaymsgsubDto subdto=new TvDirectpaymsgsubDto();
				subdto.setIdetailseqno(j+1);
				//subdto.setInoafterpackage(_inoafterpackage);
				subdto.setIvousrlno(Long.parseLong(mainvou));
				subdto.setNmoney(BigDecimal.valueOf(Double.valueOf(PayAmt)));
				subdto.setSid(sdetailId);
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
				
				/****************���������ֶ��޸�20161020****************/
				//��ʱͨ���ж����������ֶ��Ƿ���ֵ��ȷ������ֶε���Դ�����ڿ���ͨ��Area���߽ڵ�ŵȷ�ʽ���ж�
				if(StringUtils.isNotBlank(DirectVoucherNo) && StringUtils.isNotBlank(FundTypeCodeSub)) {
					subdto.setSdirectvoucherno(DirectVoucherNo); //֧���������
					subdto.setSfundtypecode(FundTypeCodeSub); //�ʽ����ʱ���
					subdto.setSfundtypename(FundTypeNameSub); //�ʽ���������
					if(AgencyCode != null && !"".equals(AgencyCode)) {
						subdto.setSsubagencycode(AgencyCode); //����Ԥ�㵥λ����
					}
					if(AgencyName != null && !"".equals(AgencyName)) {
						subdto.setSsubagencyname(AgencyName); //����Ԥ�㵥λ����
					}
					subdto.setSexpecocode(ExpEcoCode); //֧�����÷����Ŀ����
					subdto.setSexpeconame(ExpEcoName); //֧�����÷����Ŀ����
					subdto.setSpayeeacctno(PayeeAcctNoSub); //�տ����˺�
					subdto.setSpayeeacctname(PayeeAcctNameSub); //�տ�������
					subdto.setSpayeeacctbankname(PayeeAcctBankNameSub); //�տ�������
				}
				/****************���������ֶ��޸�20161020****************/
				
				agencyCodeList.add(TreCode+SupDepCode);
				expFuncCodeList.add(ExpFuncCode);
				sumAmt = sumAmt.add(BigDecimal.valueOf(Double.valueOf(PayAmt))); 
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
			verifydto.setTrecode(TreCode);
			verifydto.setFinorgcode(FinOrgCode);
			verifydto.setVoucherno(VoucherNo);
			verifydto.setVoudate(VouDate);
			verifydto.setPaybankno(PayBankNo);
			//������ȣ��ܽ���У�� by renqingbin
			verifydto.setOfyear(StYear);
			verifydto.setFamt(PayAmtSum);
			//�Ϻ���ֽ������  ֧��ƾ֤�ţ�����ƾ֤�ţ�
			verifydto.setPayVoucherNo(maindto.getSpayvoucherno());
			String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_5108);
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
			 *У��������=0.00����ϸ����=1 
			 *
			 */
			if(maindto.getNmoney().compareTo(BigDecimal.ZERO) == 0 && subDtoIdList.size()==1){
				String errMsg="������"+maindto.getNmoney()+" ,��ϸ������1";
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
			}
			
			/**
			 *��ϸ�������ܳ���500
			 * ��ɽ��Ҫ�жϳ���500�����㶫�����ط�����Ҫ�ж� 2301ͬ
			 */
			if ((!TreCode.startsWith("19") || TreCode.startsWith("1906")) && subDtoList.size() > 499) {
				String errMsg = "��ϸ��������С��500!";
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
			}
			
			/**
			 * ҵ�����⣬У�鱨�ĺϷ���
			 * 
			 * ҵ�������ӱ����ʧ�ܼ�ǩ��ʧ��
			 */
			TvDirectpaymsgsubDto[] subDtos=new TvDirectpaymsgsubDto[subDtoList.size()];
			subDtos=(TvDirectpaymsgsubDto[]) subDtoList.toArray(subDtos);
			DatabaseFacade.getODB().create(maindto);
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
			
			list=new ArrayList();
			list.add(maindto);
			list.add(vDto);			
			list.add(expFuncCodeList);
			list.add(agencyCodeList);
			//ƾ֤�ȶԣ��Ϻ���ɫ��
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

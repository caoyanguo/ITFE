package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsConvertbanknameDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainSxDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubSxDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoSxDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.sxservice.VoucherVerifySX;
import com.cfcc.itfe.xmlparse.AbstractXmlParser;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;


public class VoucherSX5207MsgServer extends AbstractXmlParser {

	private static Log logger = LogFactory.getLog(VoucherSX5207MsgServer.class);
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
			throw new ITFEBizException("����ʵ������ƾ֤5207���ĳ��ִ���",e);
			
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
		Set agencyCodeList = null;
		//Ԥ���Ŀ����list
		ArrayList<String> expFuncCodeList = null; 
		
		String sAgencyCode ="";
		TvPayoutmsgmainSxDto maindto  = null;
		List subDtoList=null; 
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
			String Attach = ((Element) VoucherBodyList.get(i)).elementText("Attach");//������Ϣ
			String Id = elementVoucher.elementText("Id");//ʵ������ƾ֤Id
			String VoucherNo  = elementVoucher.elementText("VoucherNo");//ƾ֤��
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
			String PaySummaryCode = elementVoucher.elementText("PaySummaryCode");//��;����
			String PaySummaryName = elementVoucher.elementText("PaySummaryName"); //��;����
			String PayAmt = elementVoucher.elementText("PayAmt");//������
			String XAgentBusinessNo = elementVoucher.elementText("XAgentBusinessNo");//����������
			String Hold1=elementVoucher.elementText("Hold1");//Ԥ���ֶ�1
			String Hold2=elementVoucher.elementText("Hold2");//Ԥ���ֶ�2
			
			/**
			 * ��װTvPayoutmsgmainDto����
			 **/
			maindto  = new TvPayoutmsgmainSxDto();
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
						maindto.setScheckstatus(StateConstant.CHECKSTATUS_1);//�Ѳ�¼
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
			maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);//״̬
			maindto.setSusercode("0000");//�û�����
			maindto.setSaddword("");//����
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
			maindto.setShold2(Hold2);//�����ֶ�2
			maindto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//ϵͳʱ��
			maindto.setSxagentbusinessno(XAgentBusinessNo);
			//Ԥ�㵥λ����list
			agencyCodeList = new HashSet();
			//Ԥ���Ŀ����list
			expFuncCodeList = new ArrayList<String>(); 
			/**
			 * ��װTvPayoutmsgsubDto����
			 */
			subDtoList=new ArrayList<TvPayoutmsgsubSxDto>();
			for(int j=0;j<listDetail.size();j++){
				
				//��ϸ�ϼƽ��
				Element elementDetail  = (Element)listDetail.get(j);
				String sId=(j+1)+"";//������ϸ���
				String sVoucherBillId=elementDetail.elementText("VoucherBillId");//����ƾ֤Id
				String BgtTypeCode = elementDetail.elementText("BgtTypeCode");//Ԥ�����ͱ���
				String BgtTypeName = elementDetail.elementText("BgtTypeName");//Ԥ����������
				String ProCatCode = elementDetail.elementText("ProCatCode");//��֧�������
				String ProCatName = elementDetail.elementText("ProCatName");//��֧��������
				sAgencyCode=elementDetail.elementText("AgencyCode");//Ԥ�㵥λ����
				String sAgencyName=elementDetail.elementText("AgencyName");//Ԥ�㵥λ����
				String ExpFuncCode = elementDetail.elementText("ExpFuncCode");//֧�����ܷ����Ŀ����
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
				String[] AgencyCodeArr=new String[]{"AgencyCode",sAgencyCode,"15"};
				String[] ExpFuncCodeArr=new String[]{"ExpFuncCode",ExpFuncCode,"30"};				
				
				TvPayoutmsgsubSxDto subdto=new TvPayoutmsgsubSxDto();
				//�˴�����ֵ��ȷ��
				subdto.setSbizno(mainvou);//�ӱ����к�
				subdto.setSseqno(Integer.valueOf(sId));//�ӱ���ϸ���
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
				subdto.setNmoney(subPayAmt);//֧�����
				subdto.setShold1(sdetailHold1);//Ԥ���ֶ�1
				subdto.setShold2(sdetailHold2);//Ԥ���ֶ�2
				subdto.setShold3(sdetailHold3);//Ԥ���ֶ�3
				subdto.setShold4(sdetailHold4);//Ԥ���ֶ�4
				//����Ԥ�㵥λ����ĿLIST,��ϸ�ϼ�
				agencyCodeList.add(sAgencyCode);
				expFuncCodeList.add(ExpFuncCode);
				sumAmt = sumAmt.add(subPayAmt); 
				
				//��������Ԥ�㵥λ����
				maindto.setSbudgetunitcode(sAgencyCode);
				maindto.setSunitcodename(sAgencyName);
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
			verifydto.setAgentAcctNo(PayeeAcctNo);
			verifydto.setAgentAcctName(PayeeAcctName);
			verifydto.setClearAcctNo(PayAcctNo);
			verifydto.setClearAcctName(PayAcctName);
			String returnmsg = voucherVerifySX.checkValid(verifydto, MsgConstant.VOUCHER_NO_5207);
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
			 * У���ӱ�Ԥ�㵥λ�����Ƿ��ظ�
			 * 
			 */
			if(agencyCodeList.size()>1){
				String errMsg="ƾ֤��ϸ�д��ڶ��Ԥ�㵥λ��Ϣ!";
				voucher.voucherComfailForSX(vDto.getSdealno(), errMsg);
				continue;
			}
		
			/**
			 * ҵ�����⣬У�鱨�ĺϷ���
			 * 
			 * ҵ�������ӱ����ʧ�ܼ�ǩ��ʧ��
			 */
			TvPayoutmsgsubSxDto[] subDtos=new TvPayoutmsgsubSxDto[subDtoList.size()];
			subDtos=(TvPayoutmsgsubSxDto[]) subDtoList.toArray(subDtos);
			DatabaseFacade.getODB().create(maindto);
			DatabaseFacade.getODB().create(subDtos);
			}catch(JAFDatabaseException e){
				logger.error(e);
				voucher.voucherComfailForSX(mainvou, "�������ݿ���ִ���"+e.getMessage());
				continue;	
			}catch(Exception e){
				logger.error(e);
				voucher.voucherComfailForSX(mainvou, "���Ĳ��淶��"+e.getMessage());
				continue;
			}
			
			list=new ArrayList();
			list.add(maindto);
			list.add(vDto);			
			list.add(expFuncCodeList);
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

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
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TfGrantpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfGrantpayAdjustsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherVerify;


public class Voucher5351MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher5351MsgServer.class);
	private Voucher voucher;
	/**
	 * �����������е���Ȩ֧��������(����)5351
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
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
			logger.error("������Ȩ֧��������5351���ĳ��ִ���",e);
			throw new ITFEBizException("������Ȩ֧��������5351���ĳ��ִ���",e);
			
		}
		List VoucherBodyList  = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
		String AdmDivCode  = "";//������������
		String StYear  = "";//���
		String VtCode  = "";//ƾ֤����
		
		
		//��ȡ�����������롢��Ⱥ�ƾ֤����
		if(VoucherBodyList.size()>0){
			//VoucherBody
			Element element  = (Element)VoucherBodyList.get(0);
			AdmDivCode  = element.attribute("AdmDivCode").getText();
			StYear  = element.attribute("StYear").getText();
			VtCode  = element.attribute("VtCode").getText();
		}
		
		//Ԥ���Ŀ����list
		List<String> expFuncCodeList = null; 
		//Ԥ�㵥λ����list
		List<String> agencyCodeList = null;
		//����list	 @author �Ż��
		ArrayList<BigDecimal> zeroList = null; 
		
		TfGrantpayAdjustmainDto maindto  = null;
		List<TfGrantpayAdjustsubDto> subDtoList=null; 
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
			String Id = elementVoucher.elementText("Id");//���뵥Id
			/*String AdmDivCode  = elementVoucher.elementText("AdmDivCode");//������������
			String StYear  = elementVoucher.elementText("StYear");//ҵ�����
			String VtCode  = elementVoucher.elementText("VtCode");//ƾ֤���ͱ��
			String VoucherNo = elementVoucher.elementText("VoucherNo"); //ƾ֤��*/
			String VouDate = elementVoucher.elementText("VouDate"); //ƾ֤����
			String BgtTypeCode = elementVoucher.elementText("BgtTypeCode");//Ԥ�����ͱ���
			String BgtTypeName = elementVoucher.elementText("BgtTypeName");//Ԥ����������
			String FundTypeCode = elementVoucher.elementText("FundTypeCode");//�ʽ����ʱ���
			String FundTypeName = elementVoucher.elementText("FundTypeName");//�ʽ���������
			String PayAmt = elementVoucher.elementText("PayAmt");//���ܵ������
			String PayBankCode = elementVoucher.elementText("PayBankCode");//�������б���
			String PayBankName = elementVoucher.elementText("PayBankName");//������������
			String Remark = elementVoucher.elementText("Remark");//ժҪ
			String XAccDate = elementVoucher.elementText("XAccDate");//��������
			String Hold1=elementVoucher.elementText("Hold1");//Ԥ���ֶ�1
			String Hold2=elementVoucher.elementText("Hold2");//Ԥ���ֶ�2
			
			/**
			 * ��װTfGrantpayAdjustmainDto����
			 **/
			maindto  = new TfGrantpayAdjustmainDto();
			mainvou = dealnos.get(VoucherNo).toString();
		    maindto.setIvousrlno(Long.valueOf(Long.parseLong(mainvou)));//ҵ����ˮ��,�����������ҵ���ӱ�
		    maindto.setSorgcode(ls_OrgCode);//�����������
		    maindto.setStrecode("");//�ӿڹ淶��û����Ӧ�Ĺ������,����Ĭ��Ϊ���ַ���,��������������Ĺ���Ϊ�����¸�ֵ
		    maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);//״̬
		    maindto.setSdemo("");//����
		    maindto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//ϵͳʱ��
		    maindto.setSpackageno("0000");//����ˮ��
		    maindto.setSid(Id);//����ID
		    maindto.setSadmdivcode(AdmDivCode);//������������
		    maindto.setSstyear(StYear);//ҵ�����
		    maindto.setSvtcode(VtCode);//ƾ֤����
		    maindto.setSvoucherno(VoucherNo);//ƾ֤���
		    maindto.setSvoudate(VouDate);//ƾ֤����
		    maindto.setSbgttypecode(BgtTypeCode);//Ԥ�����ͱ���
		    maindto.setSbgttypename(BgtTypeName);//Ԥ����������
		    maindto.setSfundtypecode(FundTypeCode);//�ʽ����ʱ���
			maindto.setSfundtypename(FundTypeName);//�ʽ���������
			maindto.setNpayamt(new BigDecimal(PayAmt));//���ܵ������
			maindto.setSpaybankcode(PayBankCode);//�������б���
			maindto.setSpaybankname(PayBankName);//������������
			maindto.setSremark(Remark);//ժҪ
			maindto.setSxaccdate(XAccDate);//��������
			maindto.setShold1(Hold1);//Ԥ���ֶ�1
			maindto.setShold2(Hold2);//Ԥ���ֶ�2
			//���ݽ�����ˮ��ȡ��ƾ֤������dto
			vDto=new TvVoucherinfoDto();
			vDto.setSdealno(mainvou);
			vDto=(TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
			maindto.setStrecode(vDto.getStrecode());//�ӿڹ淶��û����Ӧ�Ĺ������,��������Ĺ���Ϊ�丳ֵ
			
			//Ԥ���Ŀ����list
			expFuncCodeList = new ArrayList<String>(); 
			//Ԥ�㵥λ����list
			agencyCodeList = new ArrayList<String>();
			//����list @author �Ż��
			zeroList = new ArrayList<BigDecimal>();
			//�ӱ���ϸId����
			List<String>  subDtoIdList = new ArrayList<String>();
			
			/**
			 * ��װTfGrantpayAdjustsubDto����
			 */
			subDtoList=new ArrayList<TfGrantpayAdjustsubDto>();
			for(int j=0;j<listDetail.size();j++){
		
				Element elementDetail  = (Element)listDetail.get(j);
				String SubId = elementDetail.elementText("Id");//��ˮ��
				String SubVoucherNo = elementDetail.elementText("VoucherNo");//��Ȩ֧������ƾ֤����
				String SupDepCode = elementDetail.elementText("SupDepCode");//һ��Ԥ�㵥λ����
				String SupDepName = elementDetail.elementText("SupDepName");//һ��Ԥ�㵥λ����
				String ExpFuncCode = elementDetail.elementText("ExpFuncCode");//֧�����ܷ����Ŀ����
				String ExpFuncName = elementDetail.elementText("ExpFuncName");//֧�����ܷ����Ŀ����
				String SubPayAmt = elementDetail.elementText("PayAmt");//֧�����
				String PaySummaryName = elementDetail.elementText("PaySummaryName");//ժҪ����
				String XDealResult = elementDetail.elementText("XDealResult");//������  1�ɹ���0ʧ��
				String XAddWord = elementDetail.elementText("XAddWord");//����,ʧ��ԭ��
				String sdetailHold1 = elementDetail.elementText("Hold1");//Ԥ���ֶ�1
				String sdetailHold2 = elementDetail.elementText("Hold2");//Ԥ���ֶ�2
				String sdetailHold3 = elementDetail.elementText("Hold3");//Ԥ���ֶ�3
				String sdetailHold4 = elementDetail.elementText("Hold4");//Ԥ���ֶ�4
				
				TfGrantpayAdjustsubDto subdto=new TfGrantpayAdjustsubDto();
				subdto.setIvousrlno(Long.parseLong(mainvou));//ҵ����ˮ��,�����������ҵ������
				subdto.setIseqno(Long.valueOf(j+1));//��ϸ��
				subdto.setSid(SubId);//��ˮ��
				subdto.setSvoucherno(SubVoucherNo);//��Ȩ֧������ƾ֤����
				subdto.setSsupdepcode(SupDepCode);//һ��Ԥ�㵥λ����
				subdto.setSsupdepname(SupDepName);//һ��Ԥ�㵥λ����
				subdto.setSexpfunccode(ExpFuncCode);//֧�����ܷ����Ŀ����
				subdto.setSexpfuncname(ExpFuncName);//֧�����ܷ����Ŀ����
				BigDecimal detailPayAmt = new BigDecimal(SubPayAmt);
				subdto.setNpayamt(detailPayAmt);//֧�����
				subdto.setSpaysummaryname(PaySummaryName);//ժҪ����
				subdto.setSxdealresult(XDealResult);//������  1�ɹ���0ʧ��
				subdto.setSxaddword(XAddWord);//����,ʧ��ԭ��
				subdto.setShold1(sdetailHold1);//Ԥ���ֶ�1
				subdto.setShold2(sdetailHold2);//Ԥ���ֶ�2
				subdto.setShold3(sdetailHold3);//Ԥ���ֶ�3
				subdto.setShold4(sdetailHold4);//Ԥ���ֶ�4

				agencyCodeList.add(maindto.getStrecode()+SupDepCode);
				expFuncCodeList.add(ExpFuncCode);
				//�����б� @author �Ż��
				if(detailPayAmt.equals(BigDecimal.ZERO)){
					zeroList.add(detailPayAmt);
				}
				
				sumAmt = sumAmt.add(detailPayAmt); 
				subDtoList.add(subdto);
				subDtoIdList.add(subdto.getSid());
			}					
			
			/**
			 * ��װverifydto,���б���У��
			 */
			verifydto.setTrecode(maindto.getStrecode());
			verifydto.setFinorgcode(ls_FinOrgCode);
			verifydto.setVoucherno(VoucherNo);
			verifydto.setVoudate(VouDate);
			//������ȡ��·ݣ��ܽ���У�� by renqingbin
			verifydto.setOfyear(StYear);
			verifydto.setFamt(PayAmt);
			verifydto.setPaybankno(PayBankCode);
			String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_5351);
			
			if(returnmsg != null){//���ش�����Ϣǩ��ʧ��
				voucher.voucherComfail(vDto.getSdealno(), returnmsg);
				continue;
			}
			
			
			/**
			 *У���ӱ����Ƿ�������Ϻ���
			 *@author �Ż��
			 */
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0&&zeroList.size() > 0){
				String errMsg="��ϸ��Ϣ�а��������������!";
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
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
				String errMsg = "ÿ��Ԥ�㵥λ��ϸ��������С��500!";
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
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
			 * ҵ�����⣬У�鱨�ĺϷ���
			 * 
			 * ҵ�������ӱ����ʧ�ܼ�ǩ��ʧ��
			 */
			TfGrantpayAdjustsubDto[] subDtos=new TfGrantpayAdjustsubDto[subDtoList.size()];
			subDtos=(TfGrantpayAdjustsubDto[]) subDtoList.toArray(subDtos);
			
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
				throw new ITFEBizException("ǩ��ƾ֤����"+VtCode+"�����쳣",e);
			}
			
			list=new ArrayList();
			list.add(maindto);
			list.add(vDto);			
			list.add(expFuncCodeList);
			list.add(agencyCodeList);
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

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

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoSubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

public class Voucher3507MsgServer extends AbstractMsgManagerServer {
	private static Log logger = LogFactory.getLog(Voucher3507MsgServer.class);
	private Voucher voucher;
	
	/**
	 * ���ղ��������������ϸ����
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		super.dealMsg(eventContext);
		MuleMessage muleMessage = eventContext.getMessage();
		String voucherXml = (String) muleMessage.getPayload();
		String ls_OrgCode = (String) muleMessage.getProperty("orgCode");
		HashMap<String,String> dealnos=(HashMap<String,String>)muleMessage.getProperty("dealnos");
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("����������ϸ����3507���ĳ��ִ���",e);
			throw new ITFEBizException("����������ϸ����3507���ĳ��ִ���",e);
			
		}
		List VoucherBodyList  = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
		
		Element elemt  = (Element)VoucherBodyList.get(0);
		String AdmDivCode  = elemt.attribute("AdmDivCode").getText();
		String StYear  = elemt.attribute("StYear").getText();
		String VtCode  = elemt.attribute("VtCode").getText();
		
		//Ԥ�㵥λ����list
		List agencyCodeList = null;
		//Ԥ���Ŀ����list
		ArrayList<String> expFuncCodeList = null; 
		
		TfReconcilePayinfoMainDto mainDto = null;
		List subDtoList=null;
		TvVoucherinfoDto indexInfoDto=null;
		List lists=new ArrayList();
		List list=null;
		
		/**�������ݿ����ֶ�˳����д*/
		for(int i=0;i<VoucherBodyList.size();i++){
			//VoucherBody
			Element element  = (Element)VoucherBodyList.get(i);
			//ƾ֤���
			String voucherNo = element.attribute("VoucherNo").getText();
			//Voucher
			Element elementVoucher  = (Element)element.selectSingleNode("Voucher");
			//��ϸ��ϢList
			List listDetail  = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");
			mainDto = new TfReconcilePayinfoMainDto();
			
			//ƾ֤��ˮ�� I_VOUSRLNO
			String srlNo = dealnos.get(voucherNo);
			Long vousrlno = Long.valueOf(srlNo);
			mainDto.setIvousrlno(vousrlno);
			//����������� S_ORGCODE
			mainDto.setSorgcode(ls_OrgCode);
			//����������� S_TRECODE
			String treCode = elementVoucher.elementText("TreCode");
			mainDto.setStrecode(treCode);
			//״̬
			mainDto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);
			//����
			mainDto.setSdemo(null);
			//ϵͳ����ʱ�� TS_SYSUPDATE
			mainDto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));
			//����ˮ��
			mainDto.setSpackageno(null);
			//������������ S_ADMDIVCODE
			String admDivCode = elementVoucher.elementText("AdmDivCode");
			mainDto.setSadmdivcode(admDivCode);
			//ҵ����� S_STYEAR
			String stYear = elementVoucher.elementText("StYear");
			mainDto.setSstyear(stYear);
			//ƾ֤���ͱ�� S_VTCODE
			String vtCode = elementVoucher.elementText("VtCode");
			mainDto.setSvtcode(vtCode);
			//ƾ֤���� S_VOUDATE
			String vouDate = elementVoucher.elementText("VouDate");
			mainDto.setSvoudate(vouDate);
			//ƾ֤�� S_VOUCHERNO
			mainDto.setSvoucherno(voucherNo);
			//���˵���
			String voucherCheckNo = elementVoucher.elementText("VoucherCheckNo");
			mainDto.setSvouchercheckno(voucherCheckNo);
			//�Ӱ�����
			String childPackNum = elementVoucher.elementText("ChildPackNum");
			mainDto.setSchildpacknum(childPackNum);
			//�������
			String curPackNo = elementVoucher.elementText("CurPackNo");
			mainDto.setScurpackno(curPackNo);
			//�������б���
			String clearBankCode = elementVoucher.elementText("ClearBankCode");
			mainDto.setSclearbankcode(clearBankCode);
			//������������
			String clearBankName = elementVoucher.elementText("ClearBankName");
			mainDto.setSclearbankname(clearBankName);
			//�����˺�
			String clearAccNo = elementVoucher.elementText("ClearAccNo");
			mainDto.setSclearaccno(clearAccNo);
			//�����˻�����
			String clearAccNanme = elementVoucher.elementText("ClearAccNanme");
			mainDto.setSclearaccnanme(clearAccNanme);
			//������ʼ����
			String beginDate = elementVoucher.elementText("BeginDate");
			mainDto.setSbegindate(beginDate);
			//������ֹ����
			String endDate = elementVoucher.elementText("EndDate");
			mainDto.setSenddate(endDate);
			//�ܱ���
			String allNum = elementVoucher.elementText("AllNum");
			mainDto.setSallnum(allNum);
			//�ܽ��
			String allAmt = elementVoucher.elementText("AllAmt");
			mainDto.setNallamt(new BigDecimal(allAmt));
			//���ʽ��
			mainDto.setSxcheckresult(null);
			//��������
			mainDto.setSxdiffnum(null);
			subDtoList = new ArrayList<TfReconcilePayinfoSubDto>();
			//Ԥ�㵥λ����list
			agencyCodeList = new ArrayList<String>();
			//Ԥ���Ŀ����list
			expFuncCodeList = new ArrayList<String>(); 
			
			//��װ��ϸ��Ϣ
			for(int j=0;j<listDetail.size();j++){
				Element elementDetail  = (Element)listDetail.get(j);
				TfReconcilePayinfoSubDto subdto = new TfReconcilePayinfoSubDto();
				
				//ƾ֤��ˮ�� I_VOUSRLNO
				subdto.setIvousrlno(vousrlno);
				//��ϸ��� I_SEQNO
				subdto.setIseqno(Long.valueOf(VoucherUtil.getGrantSequence()));
				//��� S_ID
				String id = elementDetail.elementText("Id");
				subdto.setSid(id);
				//���㵥��
				String payAgentBillNo = elementDetail.elementText("PayAgentBillNo");
				subdto.setSpayagentbillno(payAgentBillNo);
				//������ϸID
				String payDetailId = elementDetail.elementText("PayDetailId");
				subdto.setSpaydetailid(payDetailId);
				//һ��Ԥ�㵥λ����
				String supDepCode = elementDetail.elementText("SupDepCode");
				subdto.setSsupdepcode(supDepCode);
				//һ��Ԥ�㵥λ����
				String supDepName = elementDetail.elementText("SupDepName");
				subdto.setSsupdepname(supDepName);
				//�ʽ����ʱ���
				String fundTypeCode = elementDetail.elementText("FundTypeCode");
				subdto.setSfundtypecode(fundTypeCode);
				//�ʽ��������� S_FUNDTYPENAME
				String fundTypeName = elementDetail.elementText("FundTypeName");
				subdto.setSfundtypename(fundTypeName);
				//�������б���
				String payBankCode = elementDetail.elementText("PayBankCode");
				subdto.setSpaybankcode(payBankCode);
				//������������
				String payBankName = elementDetail.elementText("PayBankName");
				subdto.setSpaybankname(payBankName);
				//���������к�
				String payBankNo = elementDetail.elementText("PayBankNo");
				subdto.setSpaybankno(payBankNo);
				//֧�����ܷ����Ŀ����
				String expFuncCode = elementDetail.elementText("ExpFuncCode");
				subdto.setSexpfunccode(expFuncCode);
				//֧�����ܷ����Ŀ����
				String expFuncName = elementDetail.elementText("ExpFuncName");
				subdto.setSexpfuncname(expFuncName);
				//��֧�������
				String proCatCode = elementDetail.elementText("ProCatCode");
				subdto.setSprocatcode(proCatCode);
				//��֧��������
				String proCatName = elementDetail.elementText("ProCatName");
				subdto.setSprocatname(proCatName);
				//֧����ʽ����
				String payTypeCode = elementDetail.elementText("PayTypeCode");
				subdto.setSpaytypecode(payTypeCode);
				//֧����ʽ����
				String payTypeName = elementDetail.elementText("PayTypeName");
				subdto.setSpaytypename(payTypeName);
				//֧�����
				String payAmt = elementDetail.elementText("PayAmt");
				subdto.setNpayamt(new BigDecimal(payAmt));
				//���ʽ��
				subdto.setSxcheckresult(null);
				//����ԭ��
				subdto.setSxcheckreason(null);
				//Ԥ���ֶ�1 S_HOLD1
				String dtlhold1 = elementDetail.elementText("Hold1");
				subdto.setShold1(dtlhold1);
				//Ԥ���ֶ�2 S_HOLD2
				String dtlhold2 = elementDetail.elementText("Hold2");
				subdto.setShold2(dtlhold2);
				//Ԥ���ֶ�3 S_HOLD3
				String dtlhold3 = elementDetail.elementText("Hold3");
				subdto.setShold3(dtlhold3);
				//Ԥ���ֶ�1 S_HOLD1
				String dtlhold4 = elementDetail.elementText("Hold4");
				subdto.setShold4(dtlhold4);
				
				agencyCodeList.add(treCode+supDepCode);
				expFuncCodeList.add(expFuncCode);
				
				subdto.setSext1(null);
				subdto.setSext2(null);
				subdto.setSext3(null);
				subdto.setSext4(null);
				subdto.setSext5(null);
				
				subDtoList.add(subdto);
			}
			
			try {
				indexInfoDto=new TvVoucherinfoDto();
				indexInfoDto.setSdealno(srlNo);
				indexInfoDto=(TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(indexInfoDto).get(0);
				
				//����У��
				VoucherVerifyDto verifydto = new VoucherVerifyDto();
				VoucherVerify voucherVerify = new VoucherVerify();	
				verifydto.setTrecode(treCode);
				verifydto.setVoucherno(voucherNo);
				verifydto.setVoudate(vouDate);
				verifydto.setOfyear(stYear);
				
				verifydto.setOfyear(StYear);
				verifydto.setFamt(allAmt);
				
				String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_3507);
				//���ش�����Ϣǩ��ʧ��
				if(returnmsg != null){
					voucher.voucherComfail(indexInfoDto.getSdealno(), returnmsg);
					continue;
				}
				
				/**
				 *��ϸ�������ܳ���500
				 * 
				 */
				if (subDtoList.size() > 499) {
					String errMsg = "��ϸ��������С��500!";
					voucher.voucherComfail(indexInfoDto.getSdealno(), errMsg);
					continue;
				}
				
			} catch (JAFDatabaseException e1) {
				logger.error("У��������Ϣ����ʧ��", e1);
				throw new ITFEBizException("У��������Ϣ����ʧ��");
			} catch (ValidateException e1) {
				logger.error("У��������Ϣ����ʧ��", e1);
				throw new ITFEBizException("У��������Ϣ����ʧ��");
			}
			
			/**
			 * �������������Ϣ
			 * 
			 */
			try {
				DatabaseFacade.getODB().create(mainDto);
				TfReconcilePayinfoSubDto[] subDtos = (TfReconcilePayinfoSubDto[])(subDtoList.toArray(new TfReconcilePayinfoSubDto[subDtoList.size()]));
				DatabaseFacade.getODB().create(subDtos);
			} catch (JAFDatabaseException e1) {
				logger.error("����������Ϣ�������ݿ��쳣", e1);
				throw new ITFEBizException("����������Ϣ�������ݿ��쳣");
			}
			
			//ǩ�ճɹ�
			try{
				voucher.voucherConfirmSuccess(indexInfoDto.getSdealno());
			}catch(ITFEBizException e){
				logger.error(e);
				VoucherException.saveErrInfo(indexInfoDto.getSvtcode(), e);
				continue;
			}
		
			list=new ArrayList();
			list.add(mainDto);
			list.add(indexInfoDto);			
			list.add(expFuncCodeList);
			list.add(agencyCodeList);
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
	}

	public Voucher getVoucher() {
		return voucher;
	}

	public void setVoucher(Voucher voucher) {
		this.voucher = voucher;
	}
	
}

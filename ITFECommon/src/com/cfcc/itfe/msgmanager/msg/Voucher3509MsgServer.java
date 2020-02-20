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
import com.cfcc.itfe.persistence.dto.TfReconcileRefundMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcileRefundSubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

public class Voucher3509MsgServer extends AbstractMsgManagerServer {
	private static Log logger = LogFactory.getLog(Voucher3509MsgServer.class);
	private Voucher voucher;
	
	/**
	 * ���������˸�����������Ϣ
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
			logger.error("���������˸�����������Ϣ3509���ĳ��ִ���",e);
			throw new ITFEBizException("���������˸�����������Ϣ3509���ĳ��ִ���",e);
			
		}
		List VoucherBodyList  = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
		
		Element elemt  = (Element)VoucherBodyList.get(0);
		String AdmDivCode  = elemt.attribute("AdmDivCode").getText();
		String StYear  = elemt.attribute("StYear").getText();
		String VtCode  = elemt.attribute("VtCode").getText();
		
		TfReconcileRefundMainDto mainDto = null;
		List subDtoList=null;
		TvVoucherinfoDto indexInfoDto=null;
		
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
			mainDto = new TfReconcileRefundMainDto();
			
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
			//������Դ (1:���з���2����������3�����з���)
			mainDto.setSext1("2");
			
			subDtoList = new ArrayList<TfReconcileRefundSubDto>();
			//��װ��ϸ��Ϣ
			for(int j=0;j<listDetail.size();j++){
				Element elementDetail  = (Element)listDetail.get(j);
				TfReconcileRefundSubDto subdto = new TfReconcileRefundSubDto();
				
				//ƾ֤��ˮ�� I_VOUSRLNO
				subdto.setIvousrlno(vousrlno);
				//��ϸ��� I_SEQNO
				subdto.setIseqno(Long.valueOf(VoucherUtil.getGrantSequence()));
				//��� S_ID
				String id = elementDetail.elementText("Id");
				subdto.setSid(id);
				//�˸���ϸID
				String payDetailId = elementDetail.elementText("PayDetailId");
				subdto.setSpaydetailid(payDetailId);
				//�ʽ����ʱ���
				String fundTypeCode = elementDetail.elementText("FundTypeCode");
				subdto.setSfundtypecode(fundTypeCode);
				//�ʽ��������� S_FUNDTYPENAME
				String fundTypeName = elementDetail.elementText("FundTypeName");
				subdto.setSfundtypename(fundTypeName);
				//�տ����˺�
				String payeeAcctNo = elementDetail.elementText("PayeeAcctNo");
				subdto.setSpayeeacctno(payeeAcctNo);
				//�տ�������
				String payeeAcctName = elementDetail.elementText("PayeeAcctName");
				subdto.setSpayeeacctname(payeeAcctName);
				//�տ�������
				String payeeAcctBankName = elementDetail.elementText("PayeeAcctBankName");
				subdto.setSpayeeacctbankname(payeeAcctBankName);
				//�����˻��˺�
				String payAcctNo = elementDetail.elementText("PayAcctNo");
				subdto.setSpayacctno(payAcctNo);
				//�����˻�����
				String payAcctName = elementDetail.elementText("PayAcctName");
				subdto.setSpayacctname(payAcctName);
				//�����˻�����
				String payAcctBankName = elementDetail.elementText("PayAcctBankName");
				subdto.setSpayacctbankname(payAcctBankName);
				//Ԥ�㵥λ����
				String agencyCode = elementDetail.elementText("AgencyCode");
				subdto.setSagencycode(agencyCode);
				//Ԥ�㵥λ����
				String agencyName = elementDetail.elementText("AgencyName");
				subdto.setSagencyname(agencyName);
				//��������Ŀ����
				String incomeSortCode = elementDetail.elementText("IncomeSortCode");
				subdto.setSincomesortcode(incomeSortCode);
				//��������Ŀ����
				String incomeSortName = elementDetail.elementText("IncomeSortName");
				subdto.setSincomesortname(incomeSortName);
				//�˸����
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
				
				String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_3509);
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
				logger.error("У�������˸�����������Ϣʧ�ܣ�", e1);
				throw new ITFEBizException("У�������˸�����������Ϣʧ�ܣ�");
			} catch (ValidateException e1) {
				logger.error("У�������˸�����������Ϣʧ�ܣ�", e1);
				throw new ITFEBizException("У�������˸�����������Ϣʧ�ܣ�");
			}
			
			/**
			 * ����ʵ����Ϣ����ҵ��
			 * 
			 */
			try {
				DatabaseFacade.getODB().create(mainDto);
				TfReconcileRefundSubDto[] subDtos = (TfReconcileRefundSubDto[])(subDtoList.toArray(new TfReconcileRefundSubDto[subDtoList.size()]));
				DatabaseFacade.getODB().create(subDtos);
			} catch (JAFDatabaseException e1) {
				logger.error("���������˸�����������Ϣ���ݿ��쳣", e1);
				throw new ITFEBizException("���������˸�����������Ϣ���ݿ��쳣");
			}
			
			//ǩ�ճɹ�
			try{
				voucher.voucherConfirmSuccess(indexInfoDto.getSdealno());
			}catch(ITFEBizException e){
				logger.error(e);
				VoucherException.saveErrInfo(indexInfoDto.getSvtcode(), e);
				continue;
			}
		
		}
	}

	public Voucher getVoucher() {
		return voucher;
	}

	public void setVoucher(Voucher voucher) {
		this.voucher = voucher;
	}
	
}

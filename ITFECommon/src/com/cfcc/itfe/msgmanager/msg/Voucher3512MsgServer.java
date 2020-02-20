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
import com.cfcc.itfe.persistence.dto.TfReportDefrayMainDto;
import com.cfcc.itfe.persistence.dto.TfReportDefraySubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

public class Voucher3512MsgServer extends AbstractMsgManagerServer {
	private static Log logger = LogFactory.getLog(Voucher3512MsgServer.class);
	private Voucher voucher;
	
	/**
	 * ����֧�������������
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
			logger.error("����֧���������������Ϣ3512���ĳ��ִ���",e);
			throw new ITFEBizException("����֧���������������Ϣ3512���ĳ��ִ���",e);
			
		}
		List VoucherBodyList  = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
		
		Element elemt  = (Element)VoucherBodyList.get(0);
		String AdmDivCode  = elemt.attribute("AdmDivCode").getText();
		String StYear  = elemt.attribute("StYear").getText();
		String VtCode  = elemt.attribute("VtCode").getText();
		
		TfReportDefrayMainDto mainDto = null;
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
			mainDto = new TfReportDefrayMainDto();
			
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
			//��������
			String billKind = elementVoucher.elementText("BillKind");
			mainDto.setSbillkind(billKind);
			//�������ش���
			String finOrgCode = elementVoucher.elementText("FinOrgCode");
			mainDto.setSfinorgcode(finOrgCode);
			//������������
			String treName = elementVoucher.elementText("TreName");
			mainDto.setStrename(treName);
			//Ԥ�����ͱ���
			String bgtTypeCode = elementVoucher.elementText("BgtTypeCode");
			mainDto.setSbgttypecode(bgtTypeCode);
			//Ԥ����������
			String bgtTypeName = elementVoucher.elementText("BgtTypeName");
			mainDto.setSbgttypename(bgtTypeName);
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
			
			subDtoList = new ArrayList<TfReportDefraySubDto>();
			//��װ��ϸ��Ϣ
			for(int j=0;j<listDetail.size();j++){
				Element elementDetail  = (Element)listDetail.get(j);
				TfReportDefraySubDto subdto = new TfReportDefraySubDto();
				
				//ƾ֤��ˮ�� I_VOUSRLNO
				subdto.setIvousrlno(vousrlno);
				//��ϸ��� I_SEQNO
				subdto.setIseqno(Long.valueOf(VoucherUtil.getGrantSequence()));
				//��� S_ID
				String id = elementDetail.elementText("Id");
				subdto.setSid(id);
				//֧�����ܷ����Ŀ����
				String expFuncCode = elementDetail.elementText("ExpFuncCode");
				subdto.setSexpfunccode(expFuncCode);
				//֧�����ܷ����Ŀ����
				String expFuncName = elementDetail.elementText("ExpFuncName");
				subdto.setSexpfuncname(expFuncName);
				//���ڽ��
				String curPayAmt = elementDetail.elementText("CurPayAmt");
				subdto.setNcurpayamt(new BigDecimal(curPayAmt));
				//�ۼƽ��
				String sumPayAmt = elementDetail.elementText("SumPayAmt");
				subdto.setNsumpayamt(new BigDecimal(sumPayAmt));
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
				verifydto.setFinorgcode(finOrgCode);
				
				verifydto.setOfyear(StYear);
				verifydto.setFamt(allAmt);
				
				String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_3512);
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
				logger.error("У��֧���������������Ϣ3512���ĳ��ִ���", e1);
				throw new ITFEBizException("У��֧���������������Ϣ3512���ĳ��ִ���");
			} catch (ValidateException e1) {
				logger.error("У��֧���������������Ϣ3512���ĳ��ִ���", e1);
				throw new ITFEBizException("У��֧���������������Ϣ3512���ĳ��ִ���");
			}
			
			/**
			 * ����֧���������������Ϣ
			 * 
			 */
			try {
				DatabaseFacade.getODB().create(mainDto);
				TfReportDefraySubDto[] subDtos = (TfReportDefraySubDto[])(subDtoList.toArray(new TfReportDefraySubDto[subDtoList.size()]));
				DatabaseFacade.getODB().create(subDtos);
			} catch (JAFDatabaseException e1) {
				logger.error("����֧���������������Ϣ���ݿ��쳣", e1);
				throw new ITFEBizException("����֧���������������Ϣ���ݿ��쳣");
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

package com.cfcc.itfe.msgmanager.msg;

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

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TfUnitrecordmainDto;
import com.cfcc.itfe.persistence.dto.TfUnitrecordsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

public class Voucher5951MsgServer extends AbstractMsgManagerServer {
	
	private static Log logger = LogFactory.getLog(Voucher5951MsgServer.class);
	private Voucher voucher;
	
	/**
	 * ���յ�λ���ҵ�񣬱���ҵ���
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
			logger.error("������λ���ƾ֤5951���ĳ��ִ���",e);
			throw new ITFEBizException("������λ���ƾ֤5951���ĳ��ִ���",e);
			
		}
		List VoucherBodyList  = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
		TfUnitrecordmainDto mainDto = null;
		List subDtoList=null;
		TvVoucherinfoDto indexInfoDto=null;
		
		for(int i=0;i<VoucherBodyList.size();i++){
			//VoucherBody
			Element element  = (Element)VoucherBodyList.get(i);
			//ƾ֤���
			String voucherNo = element.attribute("VoucherNo").getText();
			//Voucher
			Element elementVoucher  = (Element)element.selectSingleNode("Voucher");
			//��ϸ��ϢList
			List listDetail  = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");
			mainDto = new TfUnitrecordmainDto();
			
			//ƾ֤��ˮ�� I_VOUSRLNO
			String srlNo = dealnos.get(voucherNo);
			Long vousrlno = Long.valueOf(srlNo);
			mainDto.setIvousrlno(vousrlno);
			//����������� S_ORGCODE
			mainDto.setSorgcode(ls_OrgCode);
			//ϵͳ����ʱ�� TS_SYSUPDATE
			mainDto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));
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
			//����������� S_TRECODE
			String treCode = elementVoucher.elementText("TreCode");
			mainDto.setStrecode(treCode);
			//�������ش��� S_FINORGCODE
			String finOrgCode = elementVoucher.elementText("FinOrgCode");
			mainDto.setSfinorgcode(finOrgCode);
			//�������б��� S_PAYBANKCODE
			String payBankCode = elementVoucher.elementText("PayBankCode");
			mainDto.setSpaybankcode(payBankCode);
			//������������ S_PAYBANKNAME
			String payBankName = elementVoucher.elementText("PayBankName");
			mainDto.setSpaybankname(payBankName);
			//ȫ����־ S_ALLFLAG
			String allFlag = elementVoucher.elementText("AllFlag");
			mainDto.setSallflag(allFlag);
			//Ԥ���ֶ�1 S_HOLD1
			String hold1 = elementVoucher.elementText("Hold1");
			mainDto.setShold1(hold1);
			//Ԥ���ֶ�2 S_HOLD2
			String hold2 = elementVoucher.elementText("Hold2");
			mainDto.setShold2(hold2);
			
			subDtoList = new ArrayList<TfUnitrecordsubDto>();
			//��װ��ϸ��Ϣ
			for(int j=0;j<listDetail.size();j++){
				Element elementDetail  = (Element)listDetail.get(j);
				TfUnitrecordsubDto subdto = new TfUnitrecordsubDto();
				
				//ƾ֤��ˮ�� I_VOUSRLNO
				subdto.setIvousrlno(vousrlno);
				//��ϸ��� I_SEQNO
				subdto.setIseqno(Long.valueOf(VoucherUtil.getGrantSequence()));
				//��� S_ID
				String id = elementDetail.elementText("Id");
				subdto.setSid(id);
				//Ԥ�㵥λ���� S_AGENCYCODE
				String agencyCode = elementDetail.elementText("AgencyCode");
				subdto.setSagencycode(agencyCode);
				//Ԥ�㵥λ���� S_AGENCYNAME
				String agencyName = elementDetail.elementText("AgencyName");
				subdto.setSagencyname(agencyName);
				//�ʽ����ʱ��� S_FUNDTYPECODE
				String fundTypeCode = elementDetail.elementText("FundTypeCode");
				subdto.setSfundtypecode(fundTypeCode);
				//�ʽ��������� S_FUNDTYPENAME
				String fundTypeName = elementDetail.elementText("FundTypeName");
				subdto.setSfundtypename(fundTypeName);
				//��λ�˺� S_AGENCYACCONO
				String agencyAccoNo = elementDetail.elementText("AgencyAccoNo");
				subdto.setSagencyaccono(agencyAccoNo);
				//��λ�˻����� S_AGENCYACCONAME
				String agencyAccoName = elementDetail.elementText("AgencyAccoName");
				subdto.setSagencyacconame(agencyAccoName);
				//��λ�ʻ������� S_AGENCYBANKNAME
				String agencyBankName = elementDetail.elementText("AgencyBankName");
				subdto.setSagencybankname(agencyBankName);
				//�˻�״̬ S_ACCSTATUS
				String accStatus = elementDetail.elementText("AccStatus");
				subdto.setSaccstatus(accStatus);
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
				verifydto.setFinorgcode(finOrgCode);
				verifydto.setVoucherno(voucherNo);
				verifydto.setVoudate(vouDate);
				verifydto.setOfyear(stYear);
				
				String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_5951);
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
				logger.error("У�����ҵ���", e1);
				throw new ITFEBizException("У�����ҵ���");
			} catch (ValidateException e1) {
				logger.error("У�����ҵ���", e1);
				throw new ITFEBizException("У�����ҵ���");
			}
			
			/**
			 * ����·�����ȫ�����ݣ�����������ݱ��ٱ������ݣ����Ϊ�������ݣ�����Ԥ�㵥λ�������׷�Ӹ���
			 * ����������������ɾ��
			 */
			SQLExecutor sqlExe = null;
			try {
				if(StateConstant.ALLFLAG_FULL.equals(allFlag)){
					String sql = "DELETE FROM TF_UNITRECORDSUB WHERE I_VOUSRLNO IN (SELECT I_VOUSRLNO FROM TF_UNITRECORDMAIN WHERE S_ADMDIVCODE = ? and S_PAYBANKCODE = ? )";
					sqlExe = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
					sqlExe.addParam(admDivCode);
					sqlExe.addParam(payBankCode);
					sqlExe.runQuery(sql,TfUnitrecordsubDto.class);
					
					sql = "DELETE FROM TF_UNITRECORDMAIN WHERE S_ADMDIVCODE = ? and S_PAYBANKCODE = ? ";
					sqlExe.clearParams();
					sqlExe.addParam(admDivCode);
					sqlExe.addParam(payBankCode);
					sqlExe.runQueryCloseCon(sql, TfUnitrecordmainDto.class);
					
					DatabaseFacade.getODB().create(mainDto);
					TfUnitrecordsubDto[] subDtos = (TfUnitrecordsubDto[])(subDtoList.toArray(new TfUnitrecordsubDto[subDtoList.size()]));
					DatabaseFacade.getODB().create(subDtos);
				}else{
					DatabaseFacade.getODB().create(mainDto);
					TfUnitrecordsubDto[] subDtos = (TfUnitrecordsubDto[])(subDtoList.toArray(new TfUnitrecordsubDto[subDtoList.size()]));
					DatabaseFacade.getODB().create(subDtos);
				}
			} catch (JAFDatabaseException e) {
				logger.error("�������ҵ�����ݴ�", e);
				throw new ITFEBizException("�������ҵ�����ݴ�");
			}finally{
				if(sqlExe != null){
					sqlExe.closeConnection();
				}
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

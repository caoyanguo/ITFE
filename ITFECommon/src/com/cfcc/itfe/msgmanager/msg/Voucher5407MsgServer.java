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
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherVerify;

/**
 * ���������ⱨ��5801  ��������
 * @author Administrator
 * 1)	1-���������2-�˿������3-֧��������4-���⹫��һ�ױ��Ĺ淶��
 * 2)	XML���Ľṹһ����һ��ϸ��ԭ��������ϸ������������ϸ��������ϸ������5801.xml
 * 3)	��������Id��AdmDivCode��StYear��VtCode��XacctDate�ֶΣ���ϸ������Id ��VoucherBillId �ֶΣ�������ƾ֤��š���ʶ����CorrVouNo�޸�ΪVoucherNo������ƾ֤�ⱨ�Ĺ淶��Ҫ��
 * 4)	��ϸ������OriBudgetSubjectName��OriBudgetLevName��OriTreName��OriPayer��OriBillTypeCode��OriBillTypeName��OriVouDate��OriVoucherNo��CurBudgetSubjectName��CurBudgetLevName��CurTreName��CurPayer��CurBillTypeCode��CurBillTypeName��CurVouDate��CurVoucherNo�����������⣩֪ͨ�����ƾ֤չʾ��Ҫ��
 * 5)	��������Hold1��Hold2����ϸ������Hold1��Hold2��Hold3��Hold4��ΪԤ���ֶ�ʹ�á�
 * 6)	���������������⴦�����͡������������������ʹ��롱���������������������ơ������ܽ�ȱ�ٱ�ʶ��������XML���Ĺ淶��Ҫ���塣
 *
 */
public class Voucher5407MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher5407MsgServer.class);
	private Voucher voucher;
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();
		String voucherXml = (String) muleMessage.getPayload();
		String ls_FileName = (String) muleMessage.getProperty("fileName");
		String ls_OrgCode = (String) muleMessage.getProperty("orgCode");
		HashMap<String, String> dealnos = (HashMap<String, String>) muleMessage.getProperty("dealnos");
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("������������5407���ĳ��ִ���", e);
			throw new ITFEBizException("������������5407���ĳ��ִ���", e);
		}
		//��ȡһ���ļ��д��ڵĶ��ƾ֤������Ϣ
		List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
		String AdmDivCode = "";// ������������
		String StYear = "";// ���
		String VtCode = "";// ƾ֤����
		String currentDate = TimeFacade.getCurrentStringTime();// ��ǰ8λϵͳ����
		List<String> voucherList = new ArrayList<String>();
		// ��ȡ�����������롢��Ⱥ�ƾ֤����
		if (VoucherBodyList.size() > 0) {
			Element element = (Element) VoucherBodyList.get(0);
			AdmDivCode = element.attribute("AdmDivCode").getText();
			StYear = element.attribute("StYear").getText();
			VtCode = element.attribute("VtCode").getText();
		}
		TvInCorrhandbookDto maindto = null;
		List subDtoIdList = new ArrayList();
		List lists = new ArrayList();
		List list = null;
		// ƾ֤���к�
		String mainvou = "";
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		// ��������
		for (int i = 0; i < VoucherBodyList.size(); i++) {
			try {
				
				
				// ��ϸ�ϼƽ��
				BigDecimal sumAmt = new BigDecimal("0.00");
				// VoucherBody
				Element element = (Element) VoucherBodyList.get(i);
				// Voucher
				Element elementVoucher = (Element) element.selectSingleNode("Voucher");
				// ��ϸ��ϢList
				List listDetail = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");
				// ����У����Ϣdto
				VoucherVerifyDto verifydto = new VoucherVerifyDto();
				VoucherVerify voucherVerify = new VoucherVerify();
				/**
				 * ƾ֤��Ϣ
				 */
				String Id = elementVoucher.elementText("Id");//���������⣩֪ͨ��Id
				String VouDate = elementVoucher.elementText("VouDate");//ƾ֤����
				String FinOrgCode = elementVoucher.elementText("FinOrgCode");//�������ش���
				String CorrReaName = elementVoucher.elementText("CorrReaName");//����ԭ������
				String Amt = elementVoucher.elementText("Amt");//�������
				
//				String 	TaxOrgCode  = elementVoucher.elementText("TaxOrgCode");//�������ش���-�������ջ��ش��룬���ջ�����д o
//				String  EntrustDate = elementVoucher.elementText("EntrustDate");//ί������ -��������������������
//				String  BillDate = elementVoucher.elementText("BillDate");//��Ʊ���ڣ�ƾ֤���ڣ�-�����Ŀ�Ʊ����
				String 	VoucherNo = element.attribute("VoucherNo").getText();// ƾ֤���//����ƾ֤���-ͬһ���ջ��ز����ظ�������֪ͨ���ϵı��
//				String  DealType= elementVoucher.elementText("DealType");//1-���ӣ����й������ƾ֤���յ��������ݺ󣬼�����TIPSϵͳת���������ݣ�
				//	2-�ֹ������й������ƾ֤���յ��������ݺ󣬲�����TIPSϵͳת���������ݣ����й����ӡ��ƾ֤��
				//�ֹ����ݵ��жϱ�׼����ƾ֤���ͱ��Ϊ�˿������֧�������͵��⣻��ƾ֤���ͱ��Ϊ��������ģ��������ԭ�������е�ԭ�տ������벻�������������е����տ������룻
				String  EditTypeCode = elementVoucher.elementText("EditTypeCode");//�������������ʹ���
				String  EditTypeName = elementVoucher.elementText("EditTypeName");//������������������ 1-���������2-�˿������3-֧��������4-����
//				String  EditAmt = elementVoucher.elementText("EditAmt");//�ܽ��  ������ϸ�����н�� �� ԭ�н���ֵΪ0
//				String  TraNo = elementVoucher.elementText("TraNo");//������ˮ��  ���ڷ���Ψһ��ʶһ�ʽ��ף����ɷ����Զ���
//				String  OriTraNo = elementVoucher.elementText("OriTraNo");//ԭ������ˮ�� �ο�������ˮ��,Ϊ����������Ϣ�Ľ�����ˮ�ţ�ͬһ���ջ��ز����ظ�
				String  CorrReaCode = elementVoucher.elementText("CorrReaCode");//����ԭ�����	NString  211������Ŀ����212�������Σ���213�������⣩��214����ơ��Ƽ���ļ�Ҫ���������215���������Ʊ仯��������216�������ջ��أ�
//				String  Remark = elementVoucher.elementText("Remark");//��ע
				String  XAcctDate = elementVoucher.elementText("XAcctDate");//�������� �������д������ڣ����������ڻص��в�¼
				String  mHold1 = elementVoucher.elementText("Hold1");//Ԥ���ֶ�1
				String  mHold2 = elementVoucher.elementText("Hold2");//Ԥ���ֶ�2
				

				/**
				 * ��װTvInCorrhandbookDto����
				 **/
				maindto = new TvInCorrhandbookDto();
				mainvou = dealnos.get(VoucherNo);// ��ȡ���к�
				
				//����ƾ֤������
				vDto = new TvVoucherinfoDto();
				vDto.setSdealno(mainvou);
				vDto = (TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
				

				maindto.setIvousrlno(Long.valueOf(mainvou));// ƾ֤��ˮ��
				maindto.setSid(Id);// ���뻮��ƾ֤Id
//				maindto.setSadmdivcode(AdmDivCode);// ������������
//				maindto.setsStYear(StYear);//ҵ�����
//				maindto.setSVtCode(VtCode);//ƾ֤���ͱ��
				maindto.setDvoucher(VouDate);//ƾ֤����
				maindto.setDentrustdate(VouDate);//ί������
				maindto.setDaccept(CommonUtil.strToDate(VouDate));//��Ʊ����
				maindto.setScorrvouno(VoucherNo);//ƾ֤��
				maindto.setSfinorgcode(FinOrgCode);//�������ش���
//				maindto.setSdealtype(DealType);
				maindto.setSedittypecode(EditTypeCode);//�������������ʹ���
				maindto.setSedittypename(EditTypeName);//������������������
				maindto.setSreasoncode(CorrReaCode);//����ԭ�����
				maindto.setScorrreaname(CorrReaName);//����ԭ������
				maindto.setFeditamt(BigDecimal.valueOf(Double.valueOf(Amt)));//������� ֵΪ0
				maindto.setSdealno(mainvou.substring(mainvou.length()-8));
				maindto.setSelecvouno(VoucherNo);
//				maindto.setStrano(mainvou);
//				maindto.setSoritrano(mainvou);
				
//				maindto.setSremark(Remark);
				maindto.setDxacctdate(XAcctDate);//��������
				maindto.setSmhold1(mHold1);//Ԥ���ֶ�1
				maindto.setSmhold2(mHold2);//Ԥ���ֶ�2

				maindto.setSbookorgcode(ls_OrgCode);
				maindto.setSpackageno("111");//����ˮ�Ų���Ϊ��
				
				
				
				maindto.setSfilename(ls_FileName);
				maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);// ״̬
				// ������
				maindto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));// ϵͳʱ��
			
				/**
				 * ��װ�Ӷ��� -������ϸ
				 */
				List<TvInCorrhandbookDto> subDtoList = new ArrayList<TvInCorrhandbookDto>();
				String sdetailId = null;// ��ϸId
				for (int j = 0; j < listDetail.size(); j++) {
					Element elementDetail = (Element) listDetail.get(j);
//					Element elementDetail = (Element) listDetail.get(0);
					String sdetailIdElement = elementDetail.elementText("Id");//���������⣩��ϸId
					
					String VoucherBillId = elementDetail.elementText("VoucherBillId");//���������⣩ƾ֤Id ������Id����һ��
//					String OriTaxVouNo = elementDetail.elementText("OriTaxVouNo");//ԭ˰Ʊ����  ͬһ���ջ��ز����ظ������ջ�����д
					String OriTaxOrgCode = elementDetail.elementText("OriTaxOrgCode");//ԭ���ջ��ش���
					String OriFundTypeCode = elementDetail.elementText("OriFundTypeCode");//ԭ���ջ��ش���
//					String OriBudgetType = elementDetail.elementText("OriBudgetType");//ԭԤ������
					String OriBudSubjectCode = elementDetail.elementText("OriBudSubjectCode");//ԭ��Ԥ���Ŀ���� ���������ݸ���֪ͨ������Ԥ���Ŀ���롣
					//��������ҵ������Ϊ֧�������ģ�Ԥ���Ŀ�������Ϊ֧����Ŀ������Ϊ�����Ŀ����������ҵ��Ԥ���Ŀ�������Ϊ�����Ŀ������Ϊ֧����Ŀ��

					String OriBudSubjectName = elementDetail.elementText("OriBudSubjectName");//ԭ��Ԥ���Ŀ����
					String OriBudLevelCode = elementDetail.elementText("OriBudLevelCode");//ԭ��Ԥ�㼶�δ���
					String OriBudLevelName= elementDetail.elementText("OriBudLevelName");//ԭ��Ԥ�㼶������
					String OriTrimSign = elementDetail.elementText("OriTrimSign");//ԭ�����ڱ�־ 0������ȣ�1�������
//					String OriViceSign = elementDetail.elementText("OriViceSign");//ԭ������־
					String OriTreCode= elementDetail.elementText("OriTreCode");//ԭ�տ�������
					String OriTreName= elementDetail.elementText("OriTreName");//ԭ�տ��������
//					String OriPayer = elementDetail.elementText("OriPayer");//ԭ�ɿ�ÿ��λ
					String OriAgencyCode= elementDetail.elementText("OriAgencyCode");//ԭ�տ��������
					String OriAgencyName= elementDetail.elementText("OriAgencyName");//ԭ�տ��������
//					String OriBillTypeCode = elementDetail.elementText("OriBillTypeCode");//ԭƱ�����ͱ���
//					String OriBillTypeName = elementDetail.elementText("OriBillTypeName");//ԭƱ����������
					String OriVouDate = elementDetail.elementText("OriVouDate");//ԭƱ��ƾ֤����
					String OriVoucherNo = elementDetail.elementText("OriVoucherNo");//ԭƱ��ƾ֤��
					String OriAmt = elementDetail.elementText("OriAmt");//ԭ�н��

					String CurTaxOrgCode = elementDetail.elementText("CurTaxOrgCode");//�������ջ��ش���
					String CurFundTypeCode = elementDetail.elementText("CurFundTypeCode");//��Ԥ������
					
					String CurBudSubjectCode = elementDetail.elementText("CurBudSubjectCode");//��Ԥ���Ŀ����
					String CurBudSubjectName = elementDetail.elementText("CurBudSubjectName");//��Ԥ���Ŀ����
					String CurBudLevelCode = elementDetail.elementText("CurBudLevelCode");//��Ԥ�㼶�δ���
					String CurBudLevelName = elementDetail.elementText("CurBudLevelName");//��Ԥ�㼶������
					String CurTrimSign = elementDetail.elementText("CurTrimSign");//�������ڱ�־
//					String CurViceSign = elementDetail.elementText("CurViceSign");//�ָ�����־
					String CurTreCode = elementDetail.elementText("CurTreCode");//���տ�������
					String CurTreName = elementDetail.elementText("CurTreName");//���տ��������
					String CurAgencyCode = elementDetail.elementText("CurAgencyCode");//�ֽɿλ����
					String CurAgencyName = elementDetail.elementText("CurAgencyName");//�ֽɿλ����
//					String CurPayer = elementDetail.elementText("CurPayer");//�ֽɿ�ÿ��λ
//					String CurBillTypeCode = elementDetail.elementText("CurBillTypeCode");//��Ʊ�����ͱ���
//					String CurBillTypeName = elementDetail.elementText("CurBillTypeName");//��Ʊ����������
					String CurVouDate = elementDetail.elementText("CurVouDate");//��Ʊ��ƾ֤����
					String CurVoucherNo = elementDetail.elementText("CurVoucherNo");//��Ʊ��ƾ֤��
					String CurAmt = elementDetail.elementText("CurAmt");//���н��
					String subHold1 = elementDetail.elementText("Hold1");//Ԥ���ֶ�1-ԭ������־
					String subHold2 = elementDetail.elementText("Hold2");//Ԥ���ֶ�2-�ָ�����־
					String subHold3 = elementDetail.elementText("Hold3");//Ԥ���ֶ�3
					String subHold4 = elementDetail.elementText("Hold4");//Ԥ���ֶ�4
					
					maindto.setSsubid(sdetailIdElement);
					maindto.setSvoucherbillid(VoucherBillId);
					//-------ԭ------//
//					maindto.setSoritaxvouno(OriTaxVouNo);//ԭ˰Ʊ����
					maindto.setSoritaxorgcode(OriTaxOrgCode);//ԭ���ջ��ش���
					maindto.setSorifundtypecode(OriFundTypeCode);//ԭ�ʽ����ʴ���
					maindto.setCoribdgkind("1");//ԭԤ������
					maindto.setSoribdgsbtcode(OriBudSubjectCode);//ԭ��Ԥ���Ŀ����
					maindto.setSoribudgetsubjectname(OriBudSubjectName); //ԭ��Ԥ���Ŀ����
					maindto.setCoribdglevel(OriBudLevelCode); //ԭ��Ԥ�㼶�δ���
					maindto.setSoribudgetlevname(OriBudLevelName);//ԭ��Ԥ�㼶������
					maindto.setCoritrimsign(OriTrimSign);//ԭ�����ڱ�־
					maindto.setSoriastflag(subHold1);//ԭ������־
					maindto.setSoriaimtrecode(OriTreCode);//ԭĿ�Ĺ���
					maindto.setSoripayeetrecode(OriTreCode);//ԭ�տ�������
					maindto.setSoritrename(OriTreName);//ԭ�տ��������
					maindto.setSoriagencycode(OriAgencyCode);//ԭ�ɿλ����
					maindto.setSoriagencyname(OriAgencyName);//ԭ�ɿλ����
//					maindto.setSoribilltypecode(OriBillTypeCode);//ԭƱ�����ͱ���
//					maindto.setSoribilltypename(OriBillTypeName);//����
					maindto.setSorivoudate(OriVouDate);//ԭƱ��ƾ֤����
					maindto.setSorivoucherno(OriVoucherNo);//ԭƱ��ƾ֤��
					maindto.setForicorramt(BigDecimal.valueOf(Double.valueOf(OriAmt)));//ԭ�н��
					
					//-------��---------//
					maindto.setScurtaxorgcode(CurTaxOrgCode);//�����ջ��ش���
					maindto.setScurfundtypecode(CurFundTypeCode);//���ʽ����ʴ���
					maindto.setCcurbdgkind("1");//��Ԥ������
					maindto.setScurbdgsbtcode(CurBudSubjectCode);//��Ԥ���Ŀ����
					maindto.setScurbudgetsubjectname(CurBudSubjectName);//����
					maindto.setCcurbdglevel(CurBudLevelCode);//��Ԥ�㼶��
					maindto.setScurbudgetlevname(CurBudLevelName);
					maindto.setCtrimflag(CurTrimSign);//�������ڱ�־
					maindto.setScurastflag(subHold2);//�ָ�����־
					maindto.setScurpayeetrecode(CurTreCode);//���տ�������
					maindto.setScurtrename(CurTreName);//���տ��������
//					maindto.setScurpayer(CurPayer); //�ֽɿ�ÿ��λ
					maindto.setScuragencycode(CurAgencyCode);//�ֽɿλ����
					maindto.setScuragencyname(CurAgencyName);//�ֽɿλ����
//					maindto.setScurbilltypecode(CurBillTypeCode);//��Ʊ�����ͱ���
//					maindto.setScurbilltypename(CurBillTypeName);//��Ʊ����������
					maindto.setScurvoudate(CurVouDate); //��ƾ֤����
					maindto.setScurvoucherno(CurVoucherNo); //��ƾ֤��
					maindto.setFcurcorramt(BigDecimal.valueOf(Double.valueOf(CurAmt))); //���н��  �������ԭ�������е�ԭ�н��
					maindto.setShold1(subHold1);//Ԥ��1
					maindto.setShold2(subHold2);//Ԥ��1
					maindto.setShold3(subHold3);//Ԥ��1
					maindto.setShold4(subHold4);//Ԥ��1
					
					subDtoList.add(maindto);
					subDtoIdList.add(maindto.getSid());
				}
				/**
				 * У����ϸId�Ƿ�Ϊ�ջ��ظ�
				 */
				String checkIdMsg = voucherVerify.checkValidSudDtoId(subDtoIdList);
				if (checkIdMsg != null) {
					// ���ش�����Ϣǩ��ʧ��
					voucher.voucherComfail(mainvou, checkIdMsg);
					continue;
				}
				if(maindto.getCoribdglevel()!=null&&maindto.getCoribdglevel().equals(MsgConstant.BUDGET_LEVEL_SHARE)){
					if(maindto.getSoriastflag()==null||"".equals(maindto.getSoriastflag())||"null".equals(maindto.getSoriastflag().toLowerCase()))
					{
						voucher.voucherComfail(vDto.getSdealno(), "ԭԤ�㼶��Ϊ����ԭ������־����Ϊ��!");
						continue;
					}
				}
				if(maindto.getCcurbdglevel()!=null&&maindto.getCcurbdglevel().equals(MsgConstant.BUDGET_LEVEL_SHARE)){
					if(maindto.getScurastflag()==null||"".equals(maindto.getScurastflag())||"null".equals(maindto.getScurastflag().toLowerCase()))
					{
						voucher.voucherComfail(vDto.getSdealno(), "��Ԥ�㼶��Ϊ�����ָ�����־����Ϊ��!");
						continue;
					}
				}
				/**
				 * ��װverifydto,���б���У��
				 */
				verifydto.setVoucherno(VoucherNo);
				// ������ȣ��ܽ���У�� by renqingbin
				String returnmsg = voucherVerify.checkValid(verifydto,MsgConstant.VOUCHER_NO_5407);
				if (returnmsg != null) {// ���ش�����Ϣǩ��ʧ��
					voucher.voucherComfail(vDto.getSdealno(), returnmsg);
					continue;
				}
				/**
				 *��ϸ�������ܳ���1
				 * 
				 */
				if (subDtoList.size() > 1) {
					String errMsg = "��ϸ�����������  1 !";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				//���У��
				if(maindto.getFeditamt().compareTo(new BigDecimal("0.00"))!=0){
					String errMsg = "������Ϊ0!";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				if((maindto.getForicorramt().compareTo(maindto.getFcurcorramt()))!=0){
					String errMsg = "ԭ���������ָ��������!";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				/**
				 * ҵ�����⣬У�鱨�ĺϷ���
				 * 
				 * ҵ�������ӱ����ʧ�ܼ�ǩ��ʧ��
				 */
				DatabaseFacade.getODB().create(maindto);
			} catch (Exception e) {
				logger.error(e);
				voucher.voucherComfail(mainvou, "���Ĳ��淶��" + e.getMessage());
				continue;
			}
			// ǩ�ճɹ�
			try {
				voucher.voucherConfirmSuccess(vDto.getSdealno());
			} catch (ITFEBizException e) {
				logger.error(e);
				continue;
			}

			//ת��������Ϣ�����ͱ�����Ϣ��TIPS
//			if(maindto.getSdealtype().equalsIgnoreCase(MsgConstant.VOUCHER_CORRHANDBOOK_DEALTYPE)){
//				
//			}
			
			
			list = new ArrayList();
			list.add(maindto);
			list.add(vDto);
			lists.add(list);
		}

		/**
		 * У��ƾ֤��Ϣģ��
		 */
		try {
			if (lists.size() > 0) {
				voucher.voucherVerify(lists, VtCode);
			}

		} catch (ITFEBizException e) {
			logger.error(e);
			throw new ITFEBizException(e);
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("У��ƾ֤����" + VtCode + "�����쳣", e);
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

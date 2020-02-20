package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
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
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.persistence.pk.TsInfoconnorgaccPK;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
@SuppressWarnings({ "unchecked", "static-access" })
public class Voucher2302MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher2302MsgServer.class);
	private Voucher voucher;

	/**
	 * �����������е������˿�ƾ֤�ص�
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();
		String voucherXml = (String) muleMessage.getPayload();
		String ls_FileName = (String) muleMessage.getProperty("fileName");
		String ls_OrgCode = (String) muleMessage.getProperty("orgCode");
		HashMap<String, String> dealnos = (HashMap<String, String>) muleMessage
				.getProperty("dealnos");
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("���������˿�ƾ֤�ص�2302���ĳ��ִ���", e);
			throw new ITFEBizException("���������˿�ƾ֤�ص�2302���ĳ��ִ���", e);

		}
		List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes(
				"VoucherBody");
		String AdmDivCode = "";// ������������
		String StYear = "";// ���
		String VtCode = "";// ƾ֤����
//		String currentDate = TimeFacade.getCurrentStringTime();// ��ǰϵͳ����
//		List<String> voucherList = new ArrayList<String>();

		// ��ȡ�����������롢��Ⱥ�ƾ֤����
		if (VoucherBodyList.size() > 0) {
			Element element = (Element) VoucherBodyList.get(0);
			AdmDivCode = element.attribute("AdmDivCode").getText();
			StYear = element.attribute("StYear").getText();
			VtCode = element.attribute("VtCode").getText();
		}

		// Ԥ�㵥λ����list
		ArrayList<String> agencyCodeList = null;
		// Ԥ���Ŀ����list
		ArrayList<String> expFuncCodeList = null;

		String SupDepCode = "";
		TvPayreckBankBackDto maindto = null;
		List<TvPayreckBankBackListDto> subDtoList = null;
		List lists = new ArrayList();
		List list = null;
		// 201310���Ĺ淶�����ֶ�
		String payDictateNo = "";// ֧���������
		String payMsgNo = "";// ֧�����ı��
		String payEntrustDate = "";// ֧��ί������
		// String paySndBnkNo = "";// ֧���������к�(���Ĺ淶�޸�--ɾ��paySndBnkNo�ֶ�)
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
				// ����ͷ�뱨����ƾ֤��Ų�һ�µ���ǩ���쳣�����ģ�
				String VoucherNo = element.attribute("VoucherNo").getText();// ƾ֤���
				// Voucher
				Element elementVoucher = (Element) element
						.selectSingleNode("Voucher");
				// ��ϸ��ϢList
				List listDetail = elementVoucher.selectSingleNode("DetailList")
						.selectNodes("Detail");
				// ����У����Ϣdto
				VoucherVerifyDto verifydto = new VoucherVerifyDto();
				VoucherVerify voucherVerify = new VoucherVerify();
				/**
				 * ƾ֤��Ϣ
				 */
//				String Attach = ((Element) VoucherBodyList.get(i))
//						.elementText("Attach");// ������Ϣ
				String Id = elementVoucher.elementText("Id");// ���뻮��ƾ֤Id
				String VouDate = elementVoucher.elementText("VouDate"); // ƾ֤����
				// String VoucherNo = elementVoucher.elementText("VoucherNo");//
				// ƾ֤��
				String TreCode = elementVoucher.elementText("TreCode"); // �����������
				String FinOrgCode = elementVoucher.elementText("FinOrgCode");// �������ش���
				String BgtTypeCode = elementVoucher.elementText("BgtTypeCode");// Ԥ�����ͱ���
				String BgtTypeName = elementVoucher.elementText("BgtTypeName");// Ԥ����������
				String FundTypeCode = elementVoucher
						.elementText("FundTypeCode");// �ʽ����ʱ���
				String FundTypeName = elementVoucher
						.elementText("FundTypeName");// �ʽ���������
				String PayTypeCode = elementVoucher.elementText("PayTypeCode");// ֧����ʽ����
				String PayTypeName = elementVoucher.elementText("PayTypeName");// ֧����ʽ����
				String AgentAcctNo = elementVoucher.elementText("AgentAcctNo");// ԭ�տ������˺�
				String AgentAcctName = elementVoucher
						.elementText("AgentAcctName");// ԭ�տ������˻�����
				String AgentAcctBankName = elementVoucher
						.elementText("AgentAcctBankName");// ԭ�տ�����
				String ClearAcctNo = elementVoucher.elementText("ClearAcctNo");// ԭ�����˺�
				String ClearAcctName = elementVoucher
						.elementText("ClearAcctName");// ԭ�����˻�����
				String ClearAcctBankName = elementVoucher
						.elementText("ClearAcctBankName");// ԭ��������
				String PayAmt = elementVoucher.elementText("PayAmt");// ����������
//				String ReturnDate = elementVoucher.elementText("ReturnDate");	//����ʵ���˿�����
				String verifyPayAmt = PayAmt;
				String PayBankName = elementVoucher.elementText("PayBankName");// ������������
				String PayBankNo = elementVoucher.elementText("PayBankNo");// ���������к�
				// ͨ��ǰ���ύ����֧������
				if (StateConstant.COMMON_YES
						.equals(ITFECommonConstant.ISITFECOMMIT)) {
					payDictateNo = elementVoucher.elementText("PayDictateNo");// ���֧���˿�����
					payMsgNo = elementVoucher.elementText("PayMsgNo");// ֧�����ı��
					payEntrustDate = elementVoucher
							.elementText("PayEntrustDate");// ֧��ί������
					// paySndBnkNo =
					// elementVoucher.elementText("PaySndBnkNo");// ֧���������к�
				}
				String Remark = elementVoucher.elementText("Remark");// ժҪ
				if (Remark != null
						&& new String(Remark.getBytes("GBK"), "iso-8859-1")
								.length() > 200)
					Remark = Remark.substring(0, 100);
				String MoneyCorpCode = elementVoucher
						.elementText("MoneyCorpCode");// ���ڻ�������
				String XPaySndBnkNo = elementVoucher
						.elementText("XPaySndBnkNo");// ֧���������к�
				String XAddWord = elementVoucher.elementText("XAddWord");// ����
				String XClearDate = elementVoucher.elementText("XClearDate");// ��������
//				String XPayAmt = elementVoucher.elementText("XPayAmt");// ����������
				String Hold1 = elementVoucher.elementText("Hold1");// Ԥ���ֶ�1
//				String Hold2 = elementVoucher.elementText("Hold2");// Ԥ���ֶ�2
				String tklx = "";
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
					tklx = elementVoucher.elementText("TKLX"); // �Ϻ����� TKLX���˿�����
				}

				/**
				 * ��װTvPayreckBankBackDto����
				 **/
				maindto = new TvPayreckBankBackDto();
				mainvou = dealnos.get(VoucherNo);// ��ȡ���к�
				
				//����ƾ֤������
				vDto = new TvVoucherinfoDto();
				vDto.setSdealno(mainvou);
				vDto = (TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(
						vDto).get(0);
				
				
				maindto.setStrano(mainvou.substring(8, 16));// ���뻮��ƾ֤Id
				maindto.setIvousrlno(Long.valueOf(mainvou));// ƾ֤��ˮ��
				maindto.setSid(Id);// ���뻮��ƾ֤Id
				maindto.setSadmdivcode(AdmDivCode);// ������������
				maindto.setSofyear(StYear);// ҵ�����
				maindto.setSvtcode(VtCode);// ƾ֤���ͱ��
//				SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
				maindto.setDvoudate(CommonUtil.strToDate(VouDate)); // ƾ֤����
				maindto.setSvouno(VoucherNo);// ƾ֤��
				maindto.setSbookorgcode(ls_OrgCode);// �����������
				maindto.setStrecode(TreCode); // �����������
				maindto.setSfinorgcode(FinOrgCode);// �������ش���
				maindto.setSbgttypecode(BgtTypeCode);// Ԥ�����ͱ���
				maindto.setSbgttypename(BgtTypeName);// Ԥ����������
				maindto.setSfundtypecode(FundTypeCode);// �ʽ����ʱ���
				maindto.setSfundtypename(FundTypeName);// �ʽ���������
				maindto.setSpaytypecode(PayTypeCode);// ֧����ʽ����
				if("12".equals(PayTypeCode)|| PayTypeCode.startsWith("001002")){//�����ձ���õ���6λ
					maindto.setSpaymode("1");// ��Ȩ֧��
				}else if("11".equals(PayTypeCode) || PayTypeCode.startsWith("001001")){//�����ձ���õ���6λ
					maindto.setSpaymode("0");// ֧����ʽ����    ֱ��֧��
				}
				maindto.setSpaytypename(PayTypeName);// ֧����ʽ����
				maindto.setSpayeeacct(AgentAcctNo);// �տ������˺�
				maindto.setSpayeename(AgentAcctName);// �տ������˻�����
				maindto.setSagentacctbankname(AgentAcctBankName);// �տ�����
				maindto.setSpayeracct(ClearAcctNo);// �����˺�
				maindto.setSpayername(ClearAcctName);// �����˻�����
				maindto.setSclearacctbankname(ClearAcctBankName);// ��������
				// ���ȡ����ֵ
				Double dpayamt = Math.abs(Double.valueOf(PayAmt));
				PayAmt = new DecimalFormat("#.00").format(dpayamt);

				maindto.setFamt(new BigDecimal(PayAmt));// ����������
				maindto.setSpaybankname(PayBankName);// ������������
				maindto.setSagentbnkcode(PayBankNo);// ���������к�
				maindto.setSremark(Remark);// ժҪ
				maindto.setSmoneycorpcode(MoneyCorpCode);// ���ڻ�������
				maindto.setSxpaysndbnkno(XPaySndBnkNo);// ֧���������к�
				maindto.setSaddword(XAddWord);// ����
				maindto.setSxcleardate(CommonUtil.strToDate(XClearDate));// ��������
				maindto.setSxpayamt(BigDecimal.valueOf(Double
						.valueOf(verifyPayAmt)));
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
					maindto.setShold1(tklx);// �Ϻ����� TKLX���˿����� ������hold1�ֶ�
				} else {
					maindto.setShold1(Hold1);// Ԥ���ֶ�1
				}
				maindto.setDentrustdate(DateUtil.currentDate());// ί������
				maindto.setDorientrustdate(DateUtil.currentDate());// ԭί������
				maindto.setSoritrano("");// ԭ������ˮ��
				maindto.setDacceptdate(DateUtil.currentDate());// ��������
				maindto.setSpackno("0000");// ����ˮ��
				maindto.setStrimsign(MsgConstant.TIME_FLAG_NORMAL);// �����ڱ�־
				if ("000073100012".equals(ITFECommonConstant.SRC_NODE)) {// �����ʽ�������ΪԤ������
					maindto.setSbudgettype(FundTypeCode);
				}else if(TreCode.startsWith("06")){//���������տ��˻���ΪԤ���ڡ���
					if(ClearAcctNo.endsWith("271001") || MsgConstant.BDG_KIND_IN.equals(FundTypeCode)){
						maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);//Ԥ������(Ĭ��Ԥ����)
					}else{
						maindto.setSbudgettype(MsgConstant.BDG_KIND_OUT);//Ԥ������(Ĭ��Ԥ����)
					}
				} else {
					maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);// Ԥ������(Ĭ��Ԥ����)
					TsInfoconnorgaccPK tmppk = new TsInfoconnorgaccPK();
					tmppk.setSorgcode(ls_OrgCode); // ��������
					tmppk.setStrecode(TreCode); // TreCode��������
					tmppk.setSpayeraccount(ClearAcctNo);// �����˺�
					TsInfoconnorgaccDto resultdto = null;
					try {
						resultdto = (TsInfoconnorgaccDto) DatabaseFacade.getDb().find(tmppk);
						if(resultdto!=null&&resultdto.getSbiztype()!=null&&!"".equals(resultdto.getSbiztype()))
						{
							maindto.setSbudgettype(resultdto.getSbiztype());
						}
					} catch (JAFDatabaseException e) {
						logger.error("��ѯ����˻��쳣:" + e.getMessage());
					}
				}
				maindto.setDorivoudate(CommonUtil.strToDate(TimeFacade
						.getCurrentStringTimebefor()));// ԭƾ֤����
				maindto.setSorivouno(VoucherNo);// ԭƾ֤���
				maindto.setSpaysndbnkno(PayBankNo);// �տ��˿������к�
				maindto.setDentrustdate(TimeFacade.getCurrentDateTime());
				// ͨ��ǰ���ύ����֧������
				if (StateConstant.COMMON_YES
						.equals(ITFECommonConstant.ISITFECOMMIT)) {
					maindto.setSpaydictateno(payDictateNo);// ���֧���˿�����
					maindto.setSpaymsgno(payMsgNo);// ֧�����ı��
					maindto.setDpayentrustdate(CommonUtil
							.strToDate(payEntrustDate));// ֧��ί������
					// maindto.setSpaysndbnkno(paySndBnkNo);// ֧���������к�
					String errPayMsg = verify(payDictateNo, payMsgNo,
							payEntrustDate);
					if (errPayMsg != null) {
						voucher.voucherComfail(vDto.getSdealno(), errPayMsg);
						continue;
					}
				}
//				if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0 && StringUtils.isBlank(ReturnDate)){
//					voucher.voucherComfail(vDto.getSdealno(), "ReturnDate����Ϊ��!");
//					continue;
//				}
//				maindto.setShold2(ReturnDate);// Ԥ���ֶ�2	�����Ϻ�ʵ���˿�����
//				vDto.setShold4(ReturnDate);	//�洢ʵ���˿�����
				
				maindto.setSfilename(ls_FileName);
				maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);// ״̬
				// ������
				maindto.setTsupdate(new Timestamp(new java.util.Date()
						.getTime()));// ϵͳʱ��
			

				// Ԥ�㵥λ����list
				agencyCodeList = new ArrayList<String>();
				// Ԥ���Ŀ����list
				expFuncCodeList = new ArrayList<String>();
				/**
				 * ��װTvPayreckBankListDto����
				 */
				subDtoList = new ArrayList<TvPayreckBankBackListDto>();
				// �ӱ���ϸId����
				List<String> subDtoIdList = new ArrayList<String>();
				String sdetailId = null;// ��ϸId
				boolean breakfor = false;
				StringBuffer verifyVoucherNoSql = new StringBuffer("");//У��sql
				List<String> paramList = new ArrayList<String>();
				for (int j = 0; j < listDetail.size(); j++) {
					Element elementDetail = (Element) listDetail.get(j);
					/**
					 * У�鱨����ϸId�ڵ��Ƿ���� 1�����ڵ㲻���ڣ����ϰ汾����������ϸId 2�����ڵ���ڣ����°汾��������ϸId
					 */
					Element sdetailIdElement = elementDetail.element("Id");
					// �ڵ㲻����
					if (sdetailIdElement == null)
						subDtoIdList.add("�ڵ㲻����");
					else
						sdetailId = sdetailIdElement.getText();// ��ϸId
					String VoucherNol = elementDetail.elementText("VoucherNo");// ֧��ƾ֤����
					String OriVoucherNo = "";// ԭ֧��ƾ֤����
					String OriVouDetailNo = "";// ԭ֧��ƾ֤��ϸ����
					String ReturnDate = "";
					if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
						ReturnDate = elementDetail.elementText("ReturnDate");	//����ʵ���˿�����
						OriVoucherNo = elementDetail
								.elementText("OriVoucherNo");// ԭ֧��ƾ֤����
						OriVouDetailNo = elementDetail
								.elementText("OriVouDetailNo");// ԭ֧��ƾ֤��ϸ����
						//��������˿�  OriVoucherNoԭ֧��ƾ֤���ź�OriVouDetailNoԭ֧��ƾ֤��ϸ����  ����Ϊ��
						if(StringUtils.isNotBlank(tklx)){
							if(StateConstant.TKLX_1.equals(tklx) && (StringUtils.isBlank(OriVoucherNo) || StringUtils.isBlank(OriVouDetailNo))){
								voucher.voucherComfail(mainvou, "ƾ֤��ţ�" + VoucherNo + "����ϸIdΪ��" + sdetailId + "�е�ԭ֧��ƾ֤���Ż�ԭ֧��ƾ֤��ϸ���Ų���Ϊ�գ�");
								breakfor = true;
								break;
							}
						}else{
							voucher.voucherComfail(mainvou, "ƾ֤��ţ�" + VoucherNo + "�˿�����(TKLX)����Ϊ�գ�");
							breakfor = true;
							break;
						}
						if(StringUtils.isBlank(ReturnDate)){
							voucher.voucherComfail(vDto.getSdealno(), "ReturnDate����Ϊ��!");
							breakfor = true;
							break;
						}
						
						
					}
					SupDepCode = elementDetail.elementText("SupDepCode");// һ��Ԥ�㵥λ����
					String SupDepName = elementDetail.elementText("SupDepName");// һ��Ԥ�㵥λ����
					if(new String(SupDepName.getBytes("GBK"),"iso-8859-1").length()>60)
						SupDepName = CommonUtil.subString(SupDepName,60);
					String ExpFuncCode = elementDetail
							.elementText("ExpFuncCode");// ֧�����ܷ����Ŀ����
					String ExpFuncName = elementDetail
							.elementText("ExpFuncName");// ֧�����ܷ����Ŀ����
					String sPayAmt = elementDetail.elementText("PayAmt");// ֧�����
					String PaySummaryName = elementDetail
							.elementText("PaySummaryName");// ժҪ����
					
					String sdetailHold1 = elementDetail.elementText("Hold1");// Ԥ���ֶ�1
					String sdetailHold2 = elementDetail.elementText("Hold2");// Ԥ���ֶ�2
					String sdetailHold3 = elementDetail.elementText("Hold3");// Ԥ���ֶ�3
					String sdetailHold4 = elementDetail.elementText("Hold4");// Ԥ���ֶ�4
					sdetailHold1 = sdetailHold1.getBytes().length>=42 ? CommonUtil.subString(sdetailHold1, 42) : sdetailHold1;
					sdetailHold2 = sdetailHold2.getBytes().length>=42 ? CommonUtil.subString(sdetailHold2, 42) : sdetailHold1;
					sdetailHold3 = sdetailHold3.getBytes().length>=900 ? CommonUtil.subString(sdetailHold3, 900) : sdetailHold1;
					sdetailHold4 = sdetailHold4.getBytes().length>=42 ? CommonUtil.subString(sdetailHold4, 42) : sdetailHold1;
					
					/****************���������ֶ��޸�20161020****************/
					String DAVoucherNo      = elementDetail.elementText("DAVoucherNo"); //ԭ֧���������      
					String FundTypeCodeSub     = elementDetail.elementText("FundTypeCode"); //�ʽ����ʱ���        
					String FundTypeNameSub     = elementDetail.elementText("FundTypeName"); //�ʽ���������        
					String AgencyCode       = elementDetail.elementText("AgencyCode"); //����Ԥ�㵥λ����    
					String AgencyName       = elementDetail.elementText("AgencyName"); //����Ԥ�㵥λ����    
					String ExpEcoCode       = elementDetail.elementText("ExpEcoCode"); //֧�����÷����Ŀ����
					String ExpEcoName       = elementDetail.elementText("ExpEcoName"); //֧�����÷����Ŀ����
					String PayeeAcctNo      = elementDetail.elementText("PayeeAcctNo"); //ԭ�տ����˺�        
					String PayeeAcctName    = elementDetail.elementText("PayeeAcctName"); //ԭ�տ�������        
					String PayeeAcctBankName= elementDetail.elementText("PayeeAcctBankName"); //ԭ�տ�������        
					String PayeeAcctBankNo  = elementDetail.elementText("PayeeAcctBankNo"); //ԭ�տ��������к�    
					String RemarkSub           = elementDetail.elementText("Remark"); //��ע  
					/****************���������ֶ��޸�20161020****************/
					
					TvPayreckBankBackListDto subdto = new TvPayreckBankBackListDto();
					// �˴�����ֵ��ȷ��
					subdto.setIseqno(j + 1);
					subdto.setSid(sdetailId);
					subdto.setSacctprop(MsgConstant.ACCT_PROP_ZERO);// �˻�����
					subdto.setIvousrlno(Long.valueOf(mainvou));// �ӱ����к�
					subdto.setSvoucherno(VoucherNol);// �ӱ���ϸ���
					verifyVoucherNoSql.append(" (c.S_VOUCHERNO=?) or");
					paramList.add(VoucherNol);
					// ֧��ƾ֤����
					subdto.setSbdgorgcode(SupDepCode);// һ��Ԥ�㵥λ����
					subdto.setSsupdepname(SupDepName);// һ��Ԥ�㵥λ����
					subdto.setSfuncbdgsbtcode(ExpFuncCode);// ֧�����ܷ����Ŀ����
					subdto.setSexpfuncname(ExpFuncName);// ֧�����ܷ����Ŀ����
					subdto.setSecnomicsubjectcode("");// ���ÿ�Ŀ����
					// ���ȡ����ֵ
					Double dpayamtsub = Math.abs(Double.valueOf(sPayAmt));
					sPayAmt = new DecimalFormat("#.00").format(dpayamtsub);
					subdto.setFamt(new BigDecimal(sPayAmt));// ֧�����
					subdto.setSpaysummaryname(PaySummaryName);// ժҪ����
					subdto.setShold1(sdetailHold1);// Ԥ���ֶ�1
					subdto.setShold2(sdetailHold2);// Ԥ���ֶ�2
					subdto.setShold3(sdetailHold3);// Ԥ���ֶ�3
					subdto.setShold4(ReturnDate);// Ԥ���ֶ�4
					subdto.setTsupdate(new Timestamp(new java.util.Date()
							.getTime()));// ����ʱ��
					subdto.setSorivouno(OriVoucherNo);// ԭ֧��ƾ֤����
					subdto.setSorivoudetailno(OriVouDetailNo);// ԭ֧��ƾ֤��ϸ����
					// ����Ԥ�㵥λ����ĿLIST,��ϸ�ϼ�

					if (!agencyCodeList.contains(TreCode + SupDepCode)) {
						agencyCodeList.add(TreCode + SupDepCode);
					}
					expFuncCodeList.add(ExpFuncCode);
					sumAmt = sumAmt.add(BigDecimal.valueOf(Double
							.valueOf(sPayAmt)));
					
					/****************���������ֶ��޸�20161020****************/
					//��ʱͨ���ж����������ֶ��Ƿ���ֵ��ȷ������ֶε���Դ�����ڿ���ͨ��Area���߽ڵ�ŵȷ�ʽ���ж�
					if(StringUtils.isNotBlank(FundTypeCodeSub) && StringUtils.isNotBlank(FundTypeNameSub)) {
						subdto.setSdavoucherno(DAVoucherNo);//ԭ֧���������
						subdto.setSfundtypecode(FundTypeCodeSub);//�ʽ����ʱ���
						subdto.setSfundtypename(FundTypeNameSub);//�ʽ���������
						subdto.setSagencycode(AgencyCode);//����Ԥ�㵥λ����
						subdto.setSagencyname(AgencyName);//����Ԥ�㵥λ����
						subdto.setSexpecocode(ExpEcoCode);//֧�����÷����Ŀ����
						subdto.setSexpeconame(ExpEcoName);//֧�����÷����Ŀ����
						subdto.setSpayeeacctno(PayeeAcctNo);//ԭ�տ����˺�
						subdto.setSpayeeacctname(PayeeAcctName);//ԭ�տ�������
						subdto.setSpayeeacctbankname(PayeeAcctBankName);//ԭ�տ�������
						subdto.setSpayeeacctbankno(PayeeAcctBankNo);//ԭ�տ��������к�
						subdto.setSremark(RemarkSub);//��ע	
					}
					/****************���������ֶ��޸�20161020****************/
					
					subDtoList.add(subdto);
					subDtoIdList.add(subdto.getSid());
				}
				//��� ��ϸ�������� ������ѭ��
				if(breakfor){
					continue;
				}
				/**
				 * У����ϸId�Ƿ�Ϊ�ջ��ظ�
				 */
				String checkIdMsg = voucherVerify
						.checkValidSudDtoId(subDtoIdList);
				if (checkIdMsg != null) {
					// ���ش�����Ϣǩ��ʧ��
					voucher.voucherComfail(mainvou, checkIdMsg);
					continue;
				}
				if(ITFECommonConstant.PUBLICPARAM.contains(",verifyVoucherNo=true,")||ITFECommonConstant.PUBLICPARAM.contains(",verifyid=true,")){
					String sql = "select c.S_VOUCHERNO as S_VOUCHERNO from TV_VOUCHERINFO a,TV_PAYRECK_BANK_BACK_LIST c where a.S_STYEAR=? and (a.S_STATUS <>? and a.S_STATUS <>? and a.S_STATUS <>?) and a.S_trecode=? and a.S_DEALNO = c.I_VOUSRLNO and ("+verifyVoucherNoSql.toString().substring(0,verifyVoucherNoSql.lastIndexOf("or"))+")";
					String mainsql = "select count(*) from (select S_ID from  TV_PAYRECK_BANK_BACK where S_ID=? union select S_ID from HTV_PAYRECK_BANK_BACK where S_ID=?)";
					SQLExecutor execDetail = null;
					try
					{
						execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
						SQLResults result = null;
						if(ITFECommonConstant.PUBLICPARAM.contains(",verifyid=true,"))
						{
							execDetail.addParam(maindto.getSid());
							execDetail.addParam(maindto.getSid());
							result=  execDetail.runQuery(mainsql);//��ѯ����
							if(result!=null&&result.getInt(0, 0)>0)
							{
								voucher.voucherComfail(mainvou, "������id������ظ���");
								continue;
							}
						}
						if(ITFECommonConstant.PUBLICPARAM.contains(",verifyVoucherNo=true,"))
						{
							execDetail.addParam(maindto.getSofyear());
							execDetail.addParam(DealCodeConstants.VOUCHER_FAIL);
							execDetail.addParam(DealCodeConstants.VOUCHER_RECEIVE_FAIL);
							execDetail.addParam(DealCodeConstants.VOUCHER_ACCEPT);
							execDetail.addParam(maindto.getStrecode());
							if(paramList!=null&&paramList.size()>0)
							{
								for(String temp:paramList)
									execDetail.addParam(temp);
							}
							result=  execDetail.runQuery(sql.toString());//��ѯ����
							if(result!=null&&result.getRowCount()>0)
							{
								voucher.voucherComfail(mainvou, "��ϸ��֧��ƾ֤���"+result.getString(0, 0)+"�Ѿ����㣡");
								continue;
							}
						}
						if(execDetail!=null)
							execDetail.closeConnection();
					}catch (Exception e2) {
						logger.error(e2.getMessage(),e2);
						continue;
					}finally
					{
						if(execDetail!=null)
							execDetail.closeConnection();
					}
				}
				/**
				 * ��װverifydto,���б���У��
				 */
				verifydto.setTrecode(TreCode);
				verifydto.setFinorgcode(FinOrgCode);
				verifydto.setVoucherno(VoucherNo);
				verifydto.setVoudate(VouDate);
				verifydto.setPaytypecode(PayTypeCode);
				verifydto.setPaybankno(PayBankNo);
				verifydto.setAgentAcctNo(AgentAcctNo);
				verifydto.setAgentAcctName(AgentAcctName);
				verifydto.setClearAcctNo(ClearAcctNo);
				verifydto.setClearAcctName(ClearAcctName);
				// ������ȣ��ܽ���У�� by renqingbin
				verifydto.setOfyear(StYear);
				verifydto.setFamt(verifyPayAmt);
				//У��Ԥ������
				verifydto.setBudgettype(maindto.getSbudgettype());
				//��������
				verifydto.setOrgcode(maindto.getSbookorgcode());
				String returnmsg = voucherVerify.checkValid(verifydto,
						MsgConstant.VOUCHER_NO_2302);
				if (returnmsg != null) {// ���ش�����Ϣǩ��ʧ��
					voucher.voucherComfail(vDto.getSdealno(), returnmsg);
					continue;
				}
				/**
				 *У���������Ƿ����ӱ������
				 * 
				 */
				if (maindto.getFamt().compareTo(sumAmt) != 0) {
					String errMsg = "�����������ϸ�ۼƽ���ȣ�������" + maindto.getFamt()
							+ " ��ϸ�ۼƽ� " + sumAmt;
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				/**
				 *��ϸ�������ܳ���500
				 * 
				 */
				if (subDtoList.size() > 499) {
					String errMsg = "��ϸ��������С��500!";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				maindto.setIstatinfnum(subDtoList.size());// ��ϸ��Ϣ����
				/**
				 * ҵ�����⣬У�鱨�ĺϷ���
				 * 
				 * ҵ�������ӱ����ʧ�ܼ�ǩ��ʧ��
				 */
				TvPayreckBankBackListDto[] subDtos = new TvPayreckBankBackListDto[subDtoList
						.size()];
				subDtos = (TvPayreckBankBackListDto[]) subDtoList
						.toArray(subDtos);
				DatabaseFacade.getODB().create(maindto);
				DatabaseFacade.getODB().create(subDtos);
				vDto.setIcount(subDtoList.size());
				vDto.setSext1(maindto.getSpaymode());//֧����ʽ
				if(ITFECommonConstant.PUBLICPARAM.contains(",quotabudgettype,"))
				{
					vDto.setSext4(maindto.getSbudgettype());
				}
				DatabaseFacade.getODB().update(vDto);
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

			list = new ArrayList();
			list.add(maindto);
			list.add(vDto);
			list.add(expFuncCodeList);
			list.add(agencyCodeList);// Ԥ�㵥λlist
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
		} catch (Exception e) {
			logger.error(e);
		}
		return;
	}

	public String verify(String PayDictateNo, String PayMsgNo,
			String PayEntrustDate) {
		if (StringUtils.isBlank(PayDictateNo)) {
			return "֧��������Ų���Ϊ�ա�";
		}
		if (StringUtils.isBlank(PayMsgNo)) {
			return "֧�����ı�Ų���Ϊ�ա�";
		}
		if (StringUtils.isBlank(PayEntrustDate)) {
			return "֧��ί�����ڲ���Ϊ�ա�";
		}
		return null;
	}

	public Voucher getVoucher() {
		return voucher;
	}

	public void setVoucher(Voucher voucher) {
		this.voucher = voucher;
	}

}

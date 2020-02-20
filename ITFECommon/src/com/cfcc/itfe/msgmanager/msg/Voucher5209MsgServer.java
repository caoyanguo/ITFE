package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertbanknameDto;
import com.cfcc.itfe.persistence.dto.TsDwbkReasonDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;


public class Voucher5209MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher5207MsgServer.class);
	private Voucher voucher;
	/**
	 * �����˸�ƾ֤
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();
		String voucherXml = (String) muleMessage.getPayload();
		String ls_FileName = (String) muleMessage.getProperty("fileName");
		String ls_OrgCode = (String) muleMessage.getProperty("orgCode");
		HashMap<String,String> dealnos=(HashMap<String,String>)muleMessage.getProperty("dealnos");
		Map<String, TsConvertbanknameDto> bankInfo=null;
		HashMap<String, TsPaybankDto>  bankmap = null;
		try {
			bankInfo = BusinessFacade.getBankInfo(ls_OrgCode);//���������ձ��ȡ�����������������к���Ϣ
			bankmap = SrvCacheFacade.cachePayBankInfo();
		} catch (JAFDatabaseException e1) {
			logger.error("��ȡ����������Ϣ����"+e1);
			
		} catch (ValidateException e1) {
			logger.error("��ȡ����������Ϣ����"+e1);
			
		}catch(Exception e1){
			logger.error("��ȡ����������Ϣ����"+e1);
			
		}
		HashMap<String, TsDwbkReasonDto> dwbkreasonlist = null;
		try {
			dwbkreasonlist =SrvCacheFacade.cacheTsDwbkReason(ls_OrgCode);
		} catch (JAFDatabaseException e1) {
			logger.error("��ȡ�����˸�ԭ����Ϣ����"+e1);
		} catch (ValidateException e1) {
			logger.error("��ȡ�����˸�ԭ����Ϣ����"+e1);
		}
		
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("���������˸�ƾ֤5209���ĳ��ִ���",e);
			throw new ITFEBizException("���������˸�ƾ֤5209���ĳ��ִ���",e);
			
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
		TvDwbkDto maindto  = null;
		
		List lists=new ArrayList();
		List list=null;
		//ƾ֤���к�
		String mainvou="";
		TvVoucherinfoDto vDto=new TvVoucherinfoDto();
		//��������
		for(int i=0;i<VoucherBodyList.size();i++){
			try{
			
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
			String Attach = ((Element) VoucherBodyList.get(i)).elementText("Attach");//������Ϣ
			String Id = elementVoucher.elementText("Id");//ʵ������ƾ֤Id
			//String VoucherNo  = elementVoucher.elementText("VoucherNo");//ƾ֤��
			String VouDate = elementVoucher.elementText("VouDate"); //ƾ֤����
			String TreCode = elementVoucher.elementText("TreCode"); //�����������
			String FinOrgCode = elementVoucher.elementText("FinOrgCode");//�������ش���
			String FundTypeCode = elementVoucher.elementText("FundTypeCode");//�ʽ����ʱ���
			String FundTypeName = elementVoucher.elementText("FundTypeName");//�ʽ���������
			String PayeeAcctNo = elementVoucher.elementText("PayeeAcctNo");	//�տ����˺�
			String PayeeAcctName = elementVoucher.elementText("PayeeAcctName");//�տ�������
			String PayeeAcctBankName = elementVoucher.elementText("PayeeAcctBankName");//�տ�������
			String PayeeAcctBankNo = elementVoucher.elementText("PayeeAcctBankNo");//�տ������к�
			String ClearBankCode = elementVoucher.elementText("ClearBankCode");//�������б���
			String ClearBankName = elementVoucher.elementText("ClearBankName"); //������������
			String ReturnReasonName=elementVoucher.elementText("ReturnReasonName"); //�˸�ԭ��
			String PayAcctNo = elementVoucher.elementText("PayAcctNo");//�������˺�
			String PayAcctName = elementVoucher.elementText("PayAcctName");//����������
			String PayAcctBankName = elementVoucher.elementText("PayAcctBankName");//����������
			String XAgentBusinessNo = elementVoucher.elementText("XAgentBusinessNo");//����������
			String PayAmt = elementVoucher.elementText("PayAmt");//���
			String Hold1=elementVoucher.elementText("Hold1");//Ԥ���ֶ�1(�����ø��ֶ���ΪԤ�㼶�δ���[0-���� 1-���� 2-ʡ 3-�� 4-���� 5-����]�������ط�Ĭ��)
			String Hold2=elementVoucher.elementText("Hold2");//Ԥ���ֶ�2(�����ø��ֶ���Ϊ���ջ��ش��룬�����ط�Ĭ��)
			
			//��ϸ�ϼƽ��
			Element elementDetail  = (Element)listDetail.get(0);
			String sId=elementDetail.elementText("Id");//������ϸ���
			String sVoucherBillId=elementDetail.elementText("VoucherBillId");//����ƾ֤Id
			String BgtTypeCode = elementDetail.elementText("BgtTypeCode");//Ԥ�����ͱ���
			String BgtTypeName = elementDetail.elementText("BgtTypeName");//Ԥ����������
			String ProCatCode = elementDetail.elementText("ProCatCode");//��֧�������
			String ProCatName = elementDetail.elementText("ProCatName");//��֧��������
			sAgencyCode=elementDetail.elementText("AgencyCode");//Ԥ�㵥λ����
			String sAgencyName=elementDetail.elementText("AgencyName");//Ԥ�㵥λ����
			String IncomeSortCode = elementDetail.elementText("IncomeSortCode");//��������Ŀ����
			String IncomeSortName = elementDetail.elementText("IncomeSortName");//��������Ŀ����
			String IncomeSortCode1 = elementDetail.elementText("IncomeSortCode1");
			String IncomeSortName1 = elementDetail.elementText("IncomeSortName1");
			String IncomeSortCode2 = elementDetail.elementText("IncomeSortCode2");
			String IncomeSortName2 = elementDetail.elementText("IncomeSortName2");
			String IncomeSortCode3 = elementDetail.elementText("IncomeSortCode3");
			String IncomeSortName3 = elementDetail.elementText("IncomeSortName3");
			String IncomeSortCode4 = elementDetail.elementText("IncomeSortCode4");
			String IncomeSortName4 = elementDetail.elementText("IncomeSortName4");
			String sPayAmt = elementDetail.elementText("PayAmt");//֧�����
			String sdetailHold1 = elementDetail.elementText("Hold1");//Ԥ���ֶ�1(�����ø��ֶ���ΪԤ�㼶�δ���[0-���� 1-���� 2-ʡ 3-�� 4-���� 5-����]�������ط�Ĭ��)
			String sdetailHold2 = elementDetail.elementText("Hold2");//Ԥ���ֶ�2(�����ø��ֶ���Ϊ�˸�ԭ����룬�����ط�Ĭ��)
			String sdetailHold3 = elementDetail.elementText("Hold3");//Ԥ���ֶ�3(�����ø��ֶ���ΪԤ�����࣬�����ط�Ĭ��)
			String sdetailHold4 = elementDetail.elementText("Hold4");//Ԥ���ֶ�4(�����ø��ֶ���Ϊ�˿������룬�����ط�Ĭ��)
			String sdetailHold5 = "";
			if("000057400006".equals(ITFECommonConstant.SRC_NODE)){//����ר��
				sdetailHold5 = elementDetail.elementText("Hold5");//Ԥ���ֶ�5(�����ø��ֶ���Ϊ�˿����ݣ������ط�Ĭ��)
			}
			/**
			 * ��װTvPayoutmsgmainDto����
			 **/
			maindto  = new TvDwbkDto();
			mainvou =dealnos.get(VoucherNo);//��ȡ���к�
			if(listDetail.size()>1){

				String errMsg="ƾ֤���Ĵ��ڶ����ϸ";
				voucher.voucherComfail(mainvou, errMsg);
				continue;
			
			}
			maindto.setIvousrlno(Long.parseLong(mainvou));//���к�  
			maindto.setSbizno(mainvou);
			maindto.setSdealno(mainvou.substring(8, 16));
			maindto.setSelecvouno(VoucherNo);
			maindto.setSdwbkvoucode(VoucherNo);
			maindto.setSpayertrecode(TreCode);
			maindto.setSaimtrecode(TreCode);
			maindto.setCbckflag(StateConstant.MSG_BACK_FLAG_NO);
			maindto.setCbdgkind(MsgConstant.BDG_KIND_IN);
			maindto.setCtrimflag(MsgConstant.TIME_FLAG_NORMAL);
			maindto.setSpackageno("");
			maindto.setSpayeeacct(PayeeAcctNo);
			maindto.setSpayeecode(sAgencyCode);
			maindto.setSbdgsbtcode(IncomeSortCode);//��Ŀ����
			maindto.setSpayeename(PayeeAcctName);
			maindto.setDaccept(TimeFacade.getCurrentDateTime());
			maindto.setDvoucher(CommonUtil.strToDate(VouDate));
			maindto.setDacct(TimeFacade.getCurrentDateTime());
			maindto.setDbill(TimeFacade.getCurrentDateTime());
			maindto.setSbookorgcode(ls_OrgCode);
			maindto.setSfilename(ls_FileName);
			maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);//״̬
			maindto.setSfundtypecode(FundTypeCode);//�ʽ����ʱ���
			maindto.setSfundtypename(FundTypeName);//�ʽ���������
			maindto.setSclearbankcode(ClearBankCode);
			maindto.setSclearbankname(ClearBankName);
			maindto.setSreturnreasonname(ReturnReasonName);//�˻�ԭ��
			maindto.setXagentbusinessno(XAgentBusinessNo);
			maindto.setSpayacctno(PayAcctNo);//�������˺�
			maindto.setSpayacctname(PayAcctName);//����������
			maindto.setSpayacctbankname(PayAcctBankName);//����������
			maindto.setSbgttypecode(BgtTypeCode);//Ԥ�����ͱ���
			maindto.setSbgttypename(BgtTypeName);//Ԥ�����ͱ���
			maindto.setSprocatcode(ProCatCode);
			maindto.setSprocatname(ProCatName);
			maindto.setSagencyname(sAgencyName);
			maindto.setSincomesortname(IncomeSortName);//��������Ŀ����
			maindto.setSincomesortname1(IncomeSortName1);//��������Ŀ����
			maindto.setSincomesortname2(IncomeSortName2);//��������Ŀ����
			maindto.setSincomesortname3(IncomeSortName3);//��������Ŀ����
			maindto.setSincomesortname4(IncomeSortName4);//��������Ŀ����
			maindto.setSincomesortcode1(IncomeSortCode1);//��������Ŀ�����
			maindto.setSincomesortcode2(IncomeSortCode2);//��������Ŀ�����
			maindto.setSincomesortcode3(IncomeSortCode3);//��������Ŀ�����
			maindto.setSincomesortcode4(IncomeSortCode4);//��������Ŀ�����
			maindto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//ϵͳʱ��
			maindto.setFamt(BigDecimal.valueOf(Double.valueOf(PayAmt)));//���
			maindto.setStaxorgcode(FinOrgCode);
			if("000057400006".equals(ITFECommonConstant.SRC_NODE)){//����ר��
				maindto.setSdwbkreasoncode(sdetailHold2);//�㽭��������ϸ��Ϣ�е�Hold2��Ϊ�˸�ԭ�����
//				maindto.setStaxorgcode(Hold2);//�����ӿ�����������Ϣ�е�Hold2��Ϊ���ջ��ش���
				maindto.setCbdglevel(Hold1);//�����ӿ�����������Ϣ�е�Hold1��ΪԤ�㼶�δ���
				maindto.setSdetailhold2(sId);//��ϸId������
				maindto.setSdetailhold3(sdetailHold3);
				maindto.setSdetailhold4(sdetailHold4);
			}else{
				if("201057100006".equals(ITFECommonConstant.SRC_NODE)){
					maindto.setCbdglevel(Hold1);//�㽭��������Ϣ�е�Hold1��ΪԤ�㼶�δ���
				}else{
					maindto.setCbdglevel("");
				}
				maindto.setSdwbkreasoncode("");
				maindto.setSdetailhold1(sdetailHold1);
//				maindto.setSdetailhold2(sdetailHold2);//�˻ؽ��ռ��
				maindto.setSdetailhold3(Id);//ƾ֤id
				maindto.setSdetailhold4(sId);//��ϸid
			}
			maindto.setShold1(Hold1);//�����ֶ�1
			maindto.setShold2(Hold2);//�����ֶ�2
			
			
			
			if("".equals(PayeeAcctBankNo) || PayeeAcctBankNo ==null){
				maindto.setSrecbankname(PayeeAcctBankName);//�տ�����������
				if(bankInfo!=null&&bankInfo.size()>0){
					TsConvertbanknameDto dto = bankInfo.get(PayeeAcctBankName);
					if(dto != null){//������¼��
					
						maindto.setSinputrecbankname(dto.getSbankname());//֧��ϵͳ����
						maindto.setSinputrecbankno(dto.getSbankcode());//֧��ϵͳ�к�
						maindto.setSpayeeopnbnkno(dto.getSbankcode()); //�տ��˿������к�
						maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);//��Ҫ��¼
						maindto.setScheckstatus(StateConstant.CHECKSTATUS_0);//�Ѳ�¼
					}else{
						maindto.setSpayeeopnbnkno("");//�տ��˿������к�					
						maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);//��Ҫ��¼
						maindto.setScheckstatus(StateConstant.CHECKSTATUS_0);//δ��¼
					}
				}else{
					maindto.setSpayeeopnbnkno("");//�տ��˿������к�					
					maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);//��Ҫ��¼
					maindto.setScheckstatus(StateConstant.CHECKSTATUS_0);//δ��¼
				}
			}else{
				maindto.setSrecbankname(PayeeAcctBankName);//�տ�����������
				maindto.setSpayeeopnbnkno(PayeeAcctBankNo);//�տ��˿������к�
				maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_NO);//����Ҫ��¼
				maindto.setScheckstatus(StateConstant.CHECKSTATUS_1);//�Ѳ�¼
				//��д֧��ϵͳ�����к�
				if(bankmap != null){
					TsPaybankDto paybankdto = bankmap.get(PayeeAcctBankNo);
					if(paybankdto != null){
						maindto.setSinputrecbankname(paybankdto.getSbankname());//֧��ϵͳ����
						maindto.setSinputrecbankno(paybankdto.getSbankno());//֧��ϵͳ�к�
						if(ITFECommonConstant.PUBLICPARAM.indexOf(",verify=false,")>=0)
						{
							maindto.setScheckstatus(StateConstant.CHECKSTATUS_4);// ����״̬Ϊ�Ѹ���
							maindto.setScheckusercode("admin");// ������
						}else
						{
							maindto.setScheckstatus(StateConstant.CHECKSTATUS_1);//�Ѳ�¼
						}
					}else{
						maindto.setSinputrecbankname("δƥ�䵽֧��ϵͳ�к�");//֧��ϵͳ����
						maindto.setSinputrecbankno("000000000000");//֧��ϵͳ�к�
					}
				}else{
					maindto.setSinputrecbankname("δƥ�䵽֧��ϵͳ�к�");//֧��ϵͳ����
					maindto.setSinputrecbankno("000000000000");//֧��ϵͳ�к�
				}
				
			}
			if(dwbkreasonlist!=null&&!"000057400006".equals(ITFECommonConstant.SRC_NODE)){
				TsDwbkReasonDto dto = dwbkreasonlist.get(ReturnReasonName);
				if(dto!=null && !"".equals(dto.getSdrawbackreacode())){
					maindto.setSdwbkreasoncode(dto.getSdrawbackreacode());
				}
			}
			//Ԥ���Ŀ����list
			expFuncCodeList = new ArrayList<String>(); 
			expFuncCodeList.add(IncomeSortCode);
			
			vDto=new TvVoucherinfoDto();
			vDto.setSdealno(mainvou);
			vDto=(TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
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
			//������ȣ��ܽ���У�� by renqingbin
			verifydto.setPaybankname(PayeeAcctBankName);
			verifydto.setOfyear(StYear);
			verifydto.setFamt(PayAmt);
			String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_5209);
			//�������Ľӿ�ר���ֶν���У��
			if("000057400006".equals(ITFECommonConstant.SRC_NODE)){
				if(StringUtils.isBlank(Hold1)){
					returnmsg+="Ԥ�㼶�δ��벻��Ϊ�ա�";
				}
				if(StringUtils.isBlank(Hold2)){
					returnmsg+="���ջ��ش��벻��Ϊ�ա�";
				}
				if(StringUtils.isBlank(ReturnReasonName)){
					returnmsg+="�˸�ԭ����Ϊ�ա�";
				}
				if(StringUtils.isBlank(sdetailHold2)){
					returnmsg+="�˿�ԭ����벻��Ϊ�ա�";
				}
				if(StringUtils.isBlank(sdetailHold5)){
					returnmsg+="TBS������־����Ϊ�ա�";
				}
			}
			if(returnmsg != null){//���ش�����Ϣǩ��ʧ��
				voucher.voucherComfail(vDto.getSdealno(), returnmsg);
				continue;
			}
			
			/**
			 *У���������Ƿ����ӱ������ 
			 *
			 */
			if(maindto.getFamt().compareTo(new BigDecimal("0.00"))<0){
				String errMsg="��������Ϊ��"+maindto.getFamt();
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
			}
			
			/**
			 *��ϸ�������ܳ���500
			 * 
			 */
			if (listDetail.size() > 1) {
				String errMsg = "��ϸ����ֻ����һ��";
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
			}
			
			if(maindto.getFamt().compareTo(BigDecimal.valueOf(Double.valueOf(sPayAmt))) != 0){
				String errMsg="�����������ϸ�ۼƽ���ȣ�������"+maindto.getFamt()+" ��ϸ�ۼƽ� "+sPayAmt;
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
			}
		
		
			/**
			 * ҵ�����⣬У�鱨�ĺϷ���
			 * 
			 * ҵ�������ӱ����ʧ�ܼ�ǩ��ʧ��
			 */
		
			DatabaseFacade.getODB().create(maindto);	
			}catch(JAFDatabaseException e){
				logger.error(e);				
				voucher.voucherComfail(mainvou,"�������ݿ����"+e.getMessage());
				continue;	
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
				VoucherException.saveErrInfo(vDto.getSvtcode(), e);
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
				voucher.voucherVerify(lists, VtCode);
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

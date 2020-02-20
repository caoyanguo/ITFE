package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanknameDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;


public class Voucher5201MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher8207MsgServer.class);
	private Voucher voucher;
	/**
	 * 财政发往人行的财政直接支付凭证5201，此类只保存业务数据
	 */
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
			logger.error("解析财政直接支付凭证5201报文出现错误！",e);
			throw new ITFEBizException("解析财政直接支付凭证5201报文出现错误！",e);
			
		}
		Map<String, TsConvertbanknameDto> bankInfo=null;
		HashMap<String, TsPaybankDto>  bankmap = null;
		try {
			bankInfo = BusinessFacade.getBankInfo(ls_OrgCode);//从行名对照表获取银行名（财政）与行号信息
			bankmap = SrvCacheFacade.cachePayBankInfo();
		} catch (JAFDatabaseException e1) {
			logger.error("获取行名对照信息出错："+e1);
		} catch (ValidateException e1) {
			logger.error("获取行名对照信息出错："+e1);
		}
		List VoucherBodyList  = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
		String StYear  = "";//年度
		String VtCode  = "";//凭证类型
		
		String currentDate = TimeFacade.getCurrentStringTime();// 当前系统日期
		List<String> voucherList = new ArrayList<String>();
		
		//获取行政区划代码、年度和凭证类型
		if(VoucherBodyList.size()>0){
			Element element  = (Element)VoucherBodyList.get(0);
			StYear  = element.attribute("StYear").getText();
			VtCode  = element.attribute("VtCode").getText();
		}
		// 预算单位代码list
		ArrayList<String> agencyCodeList = null;
		// 功能类科目代码list
		ArrayList<String> expFuncCodeList = null;
		
		TfDirectpaymsgmainDto maindto  = null;
		List subDtoList=null; 
		List lists = new ArrayList();
		List list = null;
		
		//凭证序列号
		String mainvou="";
		TvVoucherinfoDto vDto=new TvVoucherinfoDto();
		//解析报文
		for(int i=0;i<VoucherBodyList.size();i++){
			try{
				
				//明细合计金额
				BigDecimal sumAmt = new BigDecimal("0.00");	
				//VoucherBody
				Element element  = (Element)VoucherBodyList.get(i);
				// 报文头与报文体凭证编号不一致导致签收异常（宁夏）
				String VoucherNo = element.attribute("VoucherNo").getText();//凭证编号
				//Voucher
				Element elementVoucher  = (Element)element.selectSingleNode("Voucher");
				//明细信息List
				List listDetail  = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");
				//报文校验信息dto
				VoucherVerifyDto verifydto = new VoucherVerifyDto();
				VoucherVerify voucherVerify = new VoucherVerify();	
				/**
				 * 凭证信息
				 */
				//String VoucherNo  = elementVoucher.elementText("VoucherNo");//凭证号
				String Id=elementVoucher.elementText("Id");//				
				String VouDate = elementVoucher.elementText("VouDate"); //凭证日期
				String FundTypeCode = elementVoucher.elementText("FundTypeCode"); //资金性质编码
				String FundTypeName = elementVoucher.elementText("FundTypeName"); //资金性质名称
				String BgtTypeCode=elementVoucher.elementText("BgtTypeCode");//预算类型编码
				String BgtTypeName=elementVoucher.elementText("BgtTypeName");//预算类型名称
				String PayTypeCode = elementVoucher.elementText("PayTypeCode"); //支付方式编码
				String PayTypeName = elementVoucher.elementText("PayTypeName"); //支付方式名称
				String ProCatCode=elementVoucher.elementText("ProCatCode");//收支管理编码
				String ProCatName=elementVoucher.elementText("ProCatName");//收支管理名称
				String MOFDepCode=elementVoucher.elementText("MOFDepCode");//业务处室编码
				String MOFDepName=elementVoucher.elementText("MOFDepName");//业务处室名称
				String FileNoCode=elementVoucher.elementText("FileNoCode");//指标文号编码
				String FileNoName=elementVoucher.elementText("FileNoName");//指标文号名称
				String SupDepCode=elementVoucher.elementText("SupDepCode");//一级预算单位编码
				String SupDepName=elementVoucher.elementText("SupDepName");//一级预算单位名称
				String AgencyCode = elementVoucher.elementText("AgencyCode"); //基层预算单位编码
				String AgencyName = elementVoucher.elementText("AgencyName"); //基层预算单位名称
				String ExpFuncCode=elementVoucher.elementText("ExpFuncCode");//支出功能分类科目编码
				String ExpFuncName=elementVoucher.elementText("ExpFuncName");//支出功能分类科目名称
				String ExpFuncCode1=elementVoucher.elementText("ExpFuncCode1");//支出功能分类科目编码
				String ExpFuncName1=elementVoucher.elementText("ExpFuncName1");//支出功能分类科目名称
				String ExpFuncCode2=elementVoucher.elementText("ExpFuncCode2");//支出功能分类科目编码
				String ExpFuncName2=elementVoucher.elementText("ExpFuncName2");//支出功能分类科目名称
				String ExpFuncCode3=elementVoucher.elementText("ExpFuncCode3");//支出功能分类科目编码
				String ExpFuncName3=elementVoucher.elementText("ExpFuncName3");//支出功能分类科目名称
				String ExpEcoCode=elementVoucher.elementText("ExpEcoCode");//支出经济分类科目编码
				String ExpEcoName=elementVoucher.elementText("ExpEcoName");//支出经济分类科目名称
				String ExpEcoCode1=elementVoucher.elementText("ExpEcoCode1");//支出经济分类科目编码
				String ExpEcoName1=elementVoucher.elementText("ExpEcoName1");//支出经济分类科目名称
				String ExpEcoCode2=elementVoucher.elementText("ExpEcoCode2");//支出经济分类科目编码
				String ExpEcoName2=elementVoucher.elementText("ExpEcoName2");//支出经济分类科目名称
				String DepProCode=elementVoucher.elementText("DepProCode");//预算项目编码
				String DepProName=elementVoucher.elementText("DepProName");//预算项目名称
				String SetModeCode=elementVoucher.elementText("SetModeCode");//结算方式编码
				String SetModeName=elementVoucher.elementText("SetModeName");//结算方式名称
				String PayBankCode = elementVoucher.elementText("PayBankCode"); //代理银行编码
				String PayBankName = elementVoucher.elementText("PayBankName"); //代理银行名称
				String ClearBankCode=elementVoucher.elementText("ClearBankCode");//清算银行编码
				String ClearBankName=elementVoucher.elementText("ClearBankName");//清算银行名称
				String PayeeAcctNo=elementVoucher.elementText("PayeeAcctNo");//收款人账号				
				String PayeeAcctName=elementVoucher.elementText("PayeeAcctName");//收款人名称
				String PayeeAcctBankName=elementVoucher.elementText("PayeeAcctBankName");//收款人银行
				String PayeeAcctBankNo=elementVoucher.elementText("PayeeAcctBankNo");//收款人银行行号
				String PayAcctNo = elementVoucher.elementText("PayAcctNo"); //付款人账号
				String PayAcctName = elementVoucher.elementText("PayAcctName"); //付款人名称
				String PayAcctBankName = elementVoucher.elementText("PayAcctBankName"); //付款人银行				
				String PaySummaryCode=elementVoucher.elementText("PaySummaryCode");//用途编码
				String PaySummaryName=elementVoucher.elementText("PaySummaryName");//用途名称
				String PayAmt=elementVoucher.elementText("PayAmt");//支付金额
				String BusinessTypeCode = elementVoucher.elementText("BusinessTypeCode"); //业务类型编码
				String BusinessTypeName = elementVoucher.elementText("BusinessTypeName"); //业务类型名称				
				String CheckNo=elementVoucher.elementText("CheckNo");//支票号（结算号）
				String XPayDate = elementVoucher.elementText("XPayDate"); //实际支付日期
				String XAgentBusinessNo=elementVoucher.elementText("XAgentBusinessNo");//银行交易流水号
				String XCheckNo=elementVoucher.elementText("XCheckNo");//支票号(结算号)
				String XPayAmt=elementVoucher.elementText("XPayAmt");//实际支付金额				
				String XPayeeAcctBankName=elementVoucher.elementText("XPayeeAcctBankName");//收款人银行
				String XPayeeAcctNo=elementVoucher.elementText("XPayeeAcctNo");//收款人账号
				String XPayeeAcctName = elementVoucher.elementText("XPayeeAcctName"); //收款人全称						
				String Hold1=elementVoucher.elementText("Hold1");//预留字段1
				String Hold2=elementVoucher.elementText("Hold2");//预留字段2
				
				/**
				 * 组装TfPaymentDetailsmainDto对象
				 **/
				maindto  = new TfDirectpaymsgmainDto();
				mainvou =dealnos.get(VoucherNo);//获取序列号
				maindto.setIvousrlno(Long.parseLong(mainvou));
				maindto.setSvoudate(VouDate);
				maindto.setSvtcode(VtCode);							
				maindto.setSfundtypecode(FundTypeCode);
				maindto.setSfundtypename(FundTypeName);				
				maindto.setSbgttypecode(BgtTypeCode);
				maindto.setSbgttypename(BgtTypeName);				
				maindto.setSpaytypename(PayTypeName);
				maindto.setSprocatcode(ProCatCode);
				maindto.setSprocatname(ProCatName);
				maindto.setSmofdepcode(MOFDepCode);
				maindto.setSmofdepname(MOFDepName);
				maindto.setSfilenocode(FileNoCode);
				maindto.setSfilenoname(FileNoName);
				maindto.setSsupdepcode(SupDepCode);
				maindto.setSsupdepname(SupDepName);
				maindto.setSagencycode(AgencyCode);
				maindto.setSagencyname(AgencyName);
				maindto.setSexpfunccode(ExpFuncCode);
				maindto.setSexpfuncname(ExpFuncName);
				maindto.setSexpfunccode1(ExpFuncCode1);
				maindto.setSexpfuncname1(ExpFuncName1);
				maindto.setSexpfunccode2(ExpFuncCode2);
				maindto.setSexpfuncname2(ExpFuncName2);
				maindto.setSexpfunccode3(ExpFuncCode3);
				maindto.setSexpfuncname3(ExpFuncName3);
				maindto.setSexpecocode(ExpEcoCode);
				maindto.setSexpeconame(ExpEcoName);
				maindto.setSexpecocode1(ExpEcoCode1);
				maindto.setSexpeconame1(ExpEcoName1);
				maindto.setSexpecocode2(ExpEcoCode2);
				maindto.setSexpeconame2(ExpEcoName2);
				maindto.setSdepprocode(DepProCode);
				maindto.setSdepproname(DepProName);
				maindto.setSsetmodecode(SetModeCode);
				maindto.setSsetmodename(SetModeName);
				maindto.setSpaybankcode(PayBankCode);
				maindto.setSpaybankname(PayBankName);
				maindto.setSclearbankcode(ClearBankCode);
				maindto.setSclearbankname(ClearBankName);
				maindto.setSpayeeacctno(PayeeAcctNo);
				maindto.setSpayeeacctname(PayeeAcctName);
				maindto.setSpayeeacctbankname(PayeeAcctBankName);
				maindto.setSpayeeacctbankno(PayeeAcctBankNo);
				maindto.setSpayacctno(PayAcctNo);
				maindto.setSpayacctname(PayAcctName);
				maindto.setSpayacctbankname(PayAcctBankName);
				maindto.setSpaysummarycode(PaySummaryCode);
				maindto.setSpaysummaryname(PaySummaryName);
				maindto.setNpayamt(BigDecimal.valueOf(Double.valueOf(PayAmt)));
				maindto.setSbusinesstypecode(BusinessTypeCode);
				maindto.setSbusinesstypename(BusinessTypeName);
				maindto.setScheckno(CheckNo);
				maindto.setSxpaydate(XPayDate);
				maindto.setSxagentbusinessno(XAgentBusinessNo);
				maindto.setSxcheckno(XCheckNo);
				maindto.setNxpayamt(null);
				maindto.setSxpayeeacctbankname(XPayeeAcctBankName);				
				maindto.setSxpayeeacctno(XPayeeAcctNo);
				maindto.setSxpayeeacctname(XPayeeAcctName);
				maindto.setShold1(Hold1);
				maindto.setShold2(Hold2);
				if(PayTypeCode!=null&&"11".equals(PayTypeCode)){
					maindto.setSpaytypecode("0");// 直接支付
				}
				maindto.setSid(Id);
				maindto.setSvoucherno(VoucherNo);
				maindto.setSstyear(StYear);//业务年度
				maindto.setSfinorgcode(ls_FinOrgCode);//财政机构代码
				maindto.setSdealno(mainvou.substring(8, 16));//交易流水号
				maindto.setScommitdate(TimeFacade.getCurrentStringTime());//委托日期
				maindto.setSfilename(ls_FileName);//文件名
				maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);//状态			
				maindto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//系统时间
				vDto=new TvVoucherinfoDto();
				vDto.setSdealno(mainvou);
				vDto=(TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
				vDto.setShold4(BusinessTypeCode);
				maindto.setSorgcode(vDto.getSorgcode());
				maindto.setStrecode(vDto.getStrecode());
				maindto.setSadmdivcode(vDto.getSadmdivcode());
				maindto.setSdemo(vDto.getSattach());
				maindto.setSbackflag("0");//退回标志 0-未退回 1-已退回
				//业务子表集合
				subDtoList=new ArrayList<TfDirectpaymsgsubDto>();
				//子表明细Id集合
				List<String>  subDtoIdList = new ArrayList<String>();
				// 预算单位代码list
				agencyCodeList = new ArrayList<String>();
				// 功能类科目代码list
				expFuncCodeList = new ArrayList<String>();
				/**
				 * 组装TfPaymentDetailssubDto对象
				 */
				for(int j=0;j<listDetail.size();j++){
					
					Element elementDetail  = (Element)listDetail.get(j);
					String sdetailId = elementDetail.elementText("Id");//明细编号
					String VoucherBillId=elementDetail.elementText("VoucherBillId");//财政直接支付凭证Id
					String VoucherBillNo=elementDetail.elementText("VoucherBillNo");//财政直接支付凭证单号
					String sdetailVoucherNo=elementDetail.elementText("VoucherNo");//支付申请序号
					String sdetailFundTypeCode=elementDetail.elementText("FundTypeCode");//资金性质编码
					String sdetailFundTypeName=elementDetail.elementText("FundTypeName");//资金性质名称
					String sdetailBgtTypeCode=elementDetail.elementText("BgtTypeCode");//预算类型编码
					String sdetailBgtTypeName=elementDetail.elementText("BgtTypeName");//预算类型名称
					String sdetailProCatCode=elementDetail.elementText("ProCatCode");//收支管理编码
					String sdetailProCatName=elementDetail.elementText("ProCatName");//收支管理名称
					String sdetailPayKindCode=elementDetail.elementText("PayKindCode");//支出类型编码
					String sdetailPayKindName=elementDetail.elementText("PayKindName");//支出类型名称
					String sdetailMOFDepCode=elementDetail.elementText("MOFDepCode");//业务处室编码
					String sdetailMOFDepName=elementDetail.elementText("MOFDepName");//业务处室名称
					String sdetailFileNoCode=elementDetail.elementText("FileNoCode");//指标文号编码
					String sdetailFileNoName=elementDetail.elementText("FileNoName");//指标文号名称
					String sdetailSupDepCode=elementDetail.elementText("SupDepCode");//一级预算单位编码
					String sdetailSupDepName=elementDetail.elementText("SupDepName");//一级预算单位名称
					String sdetailAgencyCode=elementDetail.elementText("AgencyCode");//基层预算单位编码
					String sdetailAgencyName=elementDetail.elementText("AgencyName");//基层预算单位名称
					String sdetailExpFuncCode=elementDetail.elementText("ExpFuncCode");//支出功能分类科目编码
					String sdetailExpFuncName=elementDetail.elementText("ExpFuncName");//支出功能分类科目名称
					String sdetailExpEcoCode=elementDetail.elementText("ExpEcoCode");//支出经济分类科目编码
					String sdetailExpEcoName=elementDetail.elementText("ExpEcoName");//支出经济分类科目名称
					String sdetailDepProCode=elementDetail.elementText("DepProCode");//预算项目编码
					String sdetailDepProName=elementDetail.elementText("DepProName");//预算项目名称			
					String sdetailPayeeAcctNo = elementDetail.elementText("PayeeAcctNo");//收款人账号
					String sdetailPayeeAcctName = elementDetail.elementText("PayeeAcctName");//收款人名称
					String sdetailPayeeAcctBankName = elementDetail.elementText("PayeeAcctBankName");//收款人银行
					String sdetailPayeeAcctBankNo = elementDetail.elementText("PayeeAcctBankNo");//收款人银行行号
					String sdetailPayAmt = elementDetail.elementText("PayAmt");//支付金额
					String sdetailRemark = elementDetail.elementText("Remark");//备注
					String sdetailDetailXPayDate = elementDetail.elementText("XPayDate");//实际支付日期
					String sdetailXAgentBusinessNo = elementDetail.elementText("XAgentBusinessNo");//银行交易流水号
					String sdetailXCheckNo=elementDetail.elementText("XCheckNo");//支票号(结算号)
					String sdetailXPayAmt = elementDetail.elementText("XPayAmt");//实际支付金额
					String sdetailXPayeeAcctBankName = elementDetail.elementText("XPayeeAcctBankName");//收款人银行
					String sdetailXPayeeAcctNo = elementDetail.elementText("XPayeeAcctNo");//收款人账号
					String sdetailXPayeeAcctName = elementDetail.elementText("XPayeeAcctName");//收款人全称
					String XAddWord = elementDetail.elementText("XAddWord");//失败原因				
					String sdetailHold1 = elementDetail.elementText("Hold1");//预留字段1
					String sdetailHold2 = elementDetail.elementText("Hold2");//预留字段2
					String sdetailHold3 = elementDetail.elementText("Hold3");//预留字段3
					String sdetailHold4 = elementDetail.elementText("Hold4");//预留字段4
					TfDirectpaymsgsubDto subdto=new TfDirectpaymsgsubDto();
					
					subdto.setIvousrlno(Long.parseLong(mainvou));
					subdto.setIseqno((long)(j+1));
					subdto.setSid(sdetailId);
					subdto.setSvoucherbillid(VoucherBillId);
					subdto.setSvoucherbillno(VoucherBillNo);
					subdto.setSvoucherno(sdetailVoucherNo);
					subdto.setSfundtypecode(sdetailFundTypeCode);
					subdto.setSfundtypename(sdetailFundTypeName);
					subdto.setSbgttypecode(sdetailBgtTypeCode);
					subdto.setSbgttypename(sdetailBgtTypeName);
					subdto.setSprocatcode(sdetailProCatCode);
					subdto.setSprocatname(sdetailProCatName);
					subdto.setSpaykindcode(sdetailPayKindCode);
					subdto.setSpaykindname(sdetailPayKindName);
					subdto.setSmofdepcode(sdetailMOFDepCode);
					subdto.setSmofdepname(sdetailMOFDepName);
					subdto.setSfilenocode(sdetailFileNoCode);
					subdto.setSsupdepcode(sdetailFileNoName);
					subdto.setSsupdepname(sdetailSupDepCode);
					subdto.setSagencycode(sdetailAgencyCode);
					subdto.setSagencyname(sdetailAgencyName);
					subdto.setSexpfunccode(sdetailExpFuncCode);
					subdto.setSexpfuncname(sdetailExpFuncName);
					subdto.setSexpecocode((StringUtils.isNotBlank(sdetailExpEcoCode)
							&&sdetailExpEcoCode.getBytes().length<=30)?sdetailExpEcoCode:"");//经济科目编码只取小于30位的值
					subdto.setSexpeconame(sdetailExpEcoName);
					subdto.setSdepprocode(sdetailDepProCode);
					subdto.setSdepproname(sdetailDepProName);
					subdto.setSpayeeacctno(sdetailPayeeAcctNo);
					subdto.setSpayeeacctname(sdetailPayeeAcctName);					
					subdto.setSpayeeacctbankname(sdetailPayeeAcctBankName);
					subdto.setSpayeeacctbankno(sdetailPayeeAcctBankNo);
					subdto.setNpayamt(BigDecimal.valueOf(Double.valueOf(sdetailPayAmt)));
					subdto.setSremark(sdetailRemark);
					subdto.setSxpaydate(sdetailDetailXPayDate);
					subdto.setSxagentbusinessno(sdetailXAgentBusinessNo);
					subdto.setSxcheckno(sdetailXCheckNo);
					subdto.setNxpayamt(null);
					subdto.setSxpayeeacctbankname(sdetailXPayeeAcctBankName);
					subdto.setSxpayeeacctno(sdetailXPayeeAcctNo);
					subdto.setSxpayeeacctname(sdetailXPayeeAcctName);
					subdto.setSxaddword(XAddWord);					
					subdto.setShold1(sdetailHold1);
					subdto.setShold2(sdetailHold2);
					subdto.setShold3(sdetailHold3);
					subdto.setShold4(sdetailHold4);
					//明细合计金额
					sumAmt = sumAmt.add(BigDecimal.valueOf(Double.valueOf(sdetailPayAmt))); 
					agencyCodeList.add(vDto.getStrecode()+sdetailAgencyCode);					
					expFuncCodeList.add(sdetailExpFuncCode);
					subDtoList.add(subdto);
					subDtoIdList.add(subdto.getSid());
					
				}
				
				/**
				 * 校验明细Id是否为空或重复
				 */
				String checkIdMsg=voucherVerify.checkValidSudDtoId(subDtoIdList);
				if(checkIdMsg!=null){
					//返回错误信息签收失败
					voucher.voucherComfail(mainvou, checkIdMsg);
					continue;				
				}								
				
				/**
				 * 组装verifydto,进行报文校验
				 */
				verifydto.setTrecode(maindto.getStrecode());
				verifydto.setFinorgcode(ls_FinOrgCode);//财政代码
				verifydto.setVoucherno(VoucherNo);
				verifydto.setVoudate(VouDate);
				verifydto.setPaytypecode(PayTypeCode);
				verifydto.setPaybankno(PayeeAcctBankNo);//收款人银行行号
				verifydto.setPaybankname(PayeeAcctBankName);//收款人银行名称
				verifydto.setAgentAcctNo(PayeeAcctNo);
				verifydto.setAgentAcctName(PayeeAcctName);
				verifydto.setClearAcctNo(PayAcctNo);
				verifydto.setClearAcctName(PayAcctName);
				
				//增加年度，总金额的校验 by renqingbin
				verifydto.setOfyear(StYear);
				verifydto.setFamt(PayAmt);
				
				//上海地方特色报文校验
				//增加原凭证类型、资金性质编码、 业务类型编码的校验
				verifydto.setFundTypeCode(FundTypeCode);
				verifydto.setBusinessTypeCode(BusinessTypeCode);
				
				String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_5201);
				if(returnmsg != null){//返回错误信息签收失败
					voucher.voucherComfail(vDto.getSdealno(), returnmsg);
					continue;
				}
				
				/**
				 * 校验是否自动补录行号
				 * 1、单笔业务(BusinessTypeCode=1或3）：
				 * 		根据收款人银行名称补录补复收款人银行行号
				 * 2、批量业务(BusinessTypeCode=4）：
				 * 		不允许补录行号
				 */
				if(StringUtils.isBlank(PayeeAcctBankNo)&&BusinessTypeCode.equals(StateConstant.BIZTYPE_CODE_BATCH)){
					//批量业务 不允许补录行号
					voucher.voucherComfail(vDto.getSdealno(), "批量业务收款人银行行号不允许为空");
					continue;
				}
				if(!BusinessTypeCode.equals(StateConstant.BIZTYPE_CODE_BATCH)){
					if(StringUtils.isBlank(PayeeAcctBankNo)){						
						maindto.setSpayeeacctbankname(PayeeAcctBankName);
						if(bankInfo!=null&&bankInfo.size()>0){
							TsConvertbanknameDto dto = bankInfo.get(PayeeAcctBankName);
							if(dto != null){//行名已补录	
								maindto.setSinputrecbankname(dto.getStcbankname());//支付系统行名
								maindto.setSpayeeacctbankno(dto.getSbankcode());//收款人银行行号
								maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);//需要补录
								maindto.setScheckstatus(StateConstant.CHECKSTATUS_1);//已补录
							}else{//行名未补录
								maindto.setSpayeeacctbankno("");//收款人银行行号		
								maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);//需要补录
								maindto.setScheckstatus(StateConstant.CHECKSTATUS_0);//未补录
							}
						}else{//查找行名对照表为空，行名未补录
							maindto.setSpayeeacctbankno("");//收款人银行行号			
							maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);//需要补录
							maindto.setScheckstatus(StateConstant.CHECKSTATUS_0);//未补录
						}
					}else{
						maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_NO);//不需要补录
						maindto.setScheckstatus(StateConstant.CHECKSTATUS_1);//已补录
						//填写支付系统行名行号
						if(bankmap != null){
							TsPaybankDto paybankdto = bankmap.get(PayeeAcctBankNo);
							if(paybankdto != null)
								maindto.setSinputrecbankname(paybankdto.getSbankname());//支付系统行名
							else
								maindto.setSinputrecbankname("未匹配到支付系统行号");//支付系统行名
						}else
							maindto.setSinputrecbankname("未匹配到支付系统行号");//支付系统行名					
					}
				}
				
				
				/**
				 *校验主表金额是否与子表金额相等 
				 *
				 */
				if(maindto.getNpayamt().compareTo(sumAmt) != 0){
					String errMsg="主单金额与明细累计金额不相等，主单金额："+maindto.getNpayamt()+" 明细累计金额： "+sumAmt;
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				
				/**
				 *明细条数不能超过500
				 * 
				 */
				if (subDtoList.size() > 499) {
					String errMsg = "明细条数必须小于500!";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				
				/**
				 * 业务表入库，校验报文合法性
				 * 
				 * 业务主表，子表入库失败即签收失败
				 */				
				DatabaseFacade.getODB().create(maindto);
				DatabaseFacade.getODB().create(CommonUtil.listTArray(subDtoList));	
				vDto.setIcount(subDtoList.size());
			}catch(Exception e){
				logger.error(e);
				voucher.voucherComfail(mainvou, "报文不规范："+e.getMessage());
				continue;
			}
			
			//签收成功
			try{
				voucher.voucherConfirmSuccess(vDto.getSdealno());
			}catch(ITFEBizException e){
				logger.error(e);
				VoucherException voucherE  = new VoucherException();
				voucherE.saveErrInfo(VtCode, e);
				throw new ITFEBizException("签收凭证报文"+VtCode+"出现异常",e);
			}
			
			list = new ArrayList();
			list.add(maindto);
			list.add(vDto);
			list.add(expFuncCodeList);//功能类科目代码list
			list.add(agencyCodeList);//预算单位list
			list.add(subDtoList);
			lists.add(list);
			
			/**
			 * 校验凭证信息模块
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
				throw new ITFEBizException("校验凭证报文"+VtCode+"出现异常",e);
			}			
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

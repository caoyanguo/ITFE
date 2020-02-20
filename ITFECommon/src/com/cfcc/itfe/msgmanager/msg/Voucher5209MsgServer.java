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
	 * 收入退付凭证
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
			bankInfo = BusinessFacade.getBankInfo(ls_OrgCode);//从行名对照表获取银行名（财政）与行号信息
			bankmap = SrvCacheFacade.cachePayBankInfo();
		} catch (JAFDatabaseException e1) {
			logger.error("获取行名对照信息出错："+e1);
			
		} catch (ValidateException e1) {
			logger.error("获取行名对照信息出错："+e1);
			
		}catch(Exception e1){
			logger.error("获取行名对照信息出错："+e1);
			
		}
		HashMap<String, TsDwbkReasonDto> dwbkreasonlist = null;
		try {
			dwbkreasonlist =SrvCacheFacade.cacheTsDwbkReason(ls_OrgCode);
		} catch (JAFDatabaseException e1) {
			logger.error("获取收入退付原因信息出错："+e1);
		} catch (ValidateException e1) {
			logger.error("获取收入退付原因信息出错："+e1);
		}
		
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("解析收入退付凭证5209报文出现错误！",e);
			throw new ITFEBizException("解析收入退付凭证5209报文出现错误！",e);
			
		}
		List VoucherBodyList  = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
		String AdmDivCode  = "";//行政区划代码
		String StYear  = "";//年度
		String VtCode  = "";//凭证类型
		
		String currentDate = TimeFacade.getCurrentStringTime();// 当前系统日期
		List<String> voucherList = new ArrayList<String>();
		
		//获取行政区划代码、年度和凭证类型
		if(VoucherBodyList.size()>0){
			//VoucherBody
			Element element  = (Element)VoucherBodyList.get(0);
			AdmDivCode  = element.attribute("AdmDivCode").getText();
			StYear  = element.attribute("StYear").getText();
			VtCode  = element.attribute("VtCode").getText();
		}
		
		//预算单位代码list
		Set agencyCodeList = null;
		//预算科目代码list
		ArrayList<String> expFuncCodeList = null; 
		
		String sAgencyCode ="";
		TvDwbkDto maindto  = null;
		
		List lists=new ArrayList();
		List list=null;
		//凭证序列号
		String mainvou="";
		TvVoucherinfoDto vDto=new TvVoucherinfoDto();
		//解析报文
		for(int i=0;i<VoucherBodyList.size();i++){
			try{
			
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
			String Attach = ((Element) VoucherBodyList.get(i)).elementText("Attach");//附加信息
			String Id = elementVoucher.elementText("Id");//实拨拨款凭证Id
			//String VoucherNo  = elementVoucher.elementText("VoucherNo");//凭证号
			String VouDate = elementVoucher.elementText("VouDate"); //凭证日期
			String TreCode = elementVoucher.elementText("TreCode"); //国库主体代码
			String FinOrgCode = elementVoucher.elementText("FinOrgCode");//财政机关代码
			String FundTypeCode = elementVoucher.elementText("FundTypeCode");//资金性质编码
			String FundTypeName = elementVoucher.elementText("FundTypeName");//资金性质名称
			String PayeeAcctNo = elementVoucher.elementText("PayeeAcctNo");	//收款人账号
			String PayeeAcctName = elementVoucher.elementText("PayeeAcctName");//收款人名称
			String PayeeAcctBankName = elementVoucher.elementText("PayeeAcctBankName");//收款人银行
			String PayeeAcctBankNo = elementVoucher.elementText("PayeeAcctBankNo");//收款银行行号
			String ClearBankCode = elementVoucher.elementText("ClearBankCode");//人民银行编码
			String ClearBankName = elementVoucher.elementText("ClearBankName"); //人民银行名称
			String ReturnReasonName=elementVoucher.elementText("ReturnReasonName"); //退付原因
			String PayAcctNo = elementVoucher.elementText("PayAcctNo");//付款人账号
			String PayAcctName = elementVoucher.elementText("PayAcctName");//付款人名称
			String PayAcctBankName = elementVoucher.elementText("PayAcctBankName");//付款人银行
			String XAgentBusinessNo = elementVoucher.elementText("XAgentBusinessNo");//付款人银行
			String PayAmt = elementVoucher.elementText("PayAmt");//金额
			String Hold1=elementVoucher.elementText("Hold1");//预留字段1(宁波用该字段作为预算级次代码[0-共享 1-中央 2-省 3-市 4-县区 5-乡镇]，其他地方默认)
			String Hold2=elementVoucher.elementText("Hold2");//预留字段2(宁波用该字段作为征收机关代码，其他地方默认)
			
			//明细合计金额
			Element elementDetail  = (Element)listDetail.get(0);
			String sId=elementDetail.elementText("Id");//拨款明细编号
			String sVoucherBillId=elementDetail.elementText("VoucherBillId");//拨款凭证Id
			String BgtTypeCode = elementDetail.elementText("BgtTypeCode");//预算类型编码
			String BgtTypeName = elementDetail.elementText("BgtTypeName");//预算类型名称
			String ProCatCode = elementDetail.elementText("ProCatCode");//收支管理编码
			String ProCatName = elementDetail.elementText("ProCatName");//收支管理名称
			sAgencyCode=elementDetail.elementText("AgencyCode");//预算单位编码
			String sAgencyName=elementDetail.elementText("AgencyName");//预算单位名称
			String IncomeSortCode = elementDetail.elementText("IncomeSortCode");//收入分类科目编码
			String IncomeSortName = elementDetail.elementText("IncomeSortName");//收入分类科目名称
			String IncomeSortCode1 = elementDetail.elementText("IncomeSortCode1");
			String IncomeSortName1 = elementDetail.elementText("IncomeSortName1");
			String IncomeSortCode2 = elementDetail.elementText("IncomeSortCode2");
			String IncomeSortName2 = elementDetail.elementText("IncomeSortName2");
			String IncomeSortCode3 = elementDetail.elementText("IncomeSortCode3");
			String IncomeSortName3 = elementDetail.elementText("IncomeSortName3");
			String IncomeSortCode4 = elementDetail.elementText("IncomeSortCode4");
			String IncomeSortName4 = elementDetail.elementText("IncomeSortName4");
			String sPayAmt = elementDetail.elementText("PayAmt");//支付金额
			String sdetailHold1 = elementDetail.elementText("Hold1");//预留字段1(宁波用该字段作为预算级次代码[0-共享 1-中央 2-省 3-市 4-县区 5-乡镇]，其他地方默认)
			String sdetailHold2 = elementDetail.elementText("Hold2");//预留字段2(宁波用该字段作为退付原因代码，其他地方默认)
			String sdetailHold3 = elementDetail.elementText("Hold3");//预留字段3(宁波用该字段作为预算种类，其他地方默认)
			String sdetailHold4 = elementDetail.elementText("Hold4");//预留字段4(宁波用该字段作为退款国库代码，其他地方默认)
			String sdetailHold5 = "";
			if("000057400006".equals(ITFECommonConstant.SRC_NODE)){//宁波专用
				sdetailHold5 = elementDetail.elementText("Hold5");//预留字段5(宁波用该字段作为退款依据，其他地方默认)
			}
			/**
			 * 组装TvPayoutmsgmainDto对象
			 **/
			maindto  = new TvDwbkDto();
			mainvou =dealnos.get(VoucherNo);//获取序列号
			if(listDetail.size()>1){

				String errMsg="凭证报文存在多个明细";
				voucher.voucherComfail(mainvou, errMsg);
				continue;
			
			}
			maindto.setIvousrlno(Long.parseLong(mainvou));//序列号  
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
			maindto.setSbdgsbtcode(IncomeSortCode);//科目代码
			maindto.setSpayeename(PayeeAcctName);
			maindto.setDaccept(TimeFacade.getCurrentDateTime());
			maindto.setDvoucher(CommonUtil.strToDate(VouDate));
			maindto.setDacct(TimeFacade.getCurrentDateTime());
			maindto.setDbill(TimeFacade.getCurrentDateTime());
			maindto.setSbookorgcode(ls_OrgCode);
			maindto.setSfilename(ls_FileName);
			maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);//状态
			maindto.setSfundtypecode(FundTypeCode);//资金性质编码
			maindto.setSfundtypename(FundTypeName);//资金性质名称
			maindto.setSclearbankcode(ClearBankCode);
			maindto.setSclearbankname(ClearBankName);
			maindto.setSreturnreasonname(ReturnReasonName);//退回原因
			maindto.setXagentbusinessno(XAgentBusinessNo);
			maindto.setSpayacctno(PayAcctNo);//付款人账号
			maindto.setSpayacctname(PayAcctName);//付款人名称
			maindto.setSpayacctbankname(PayAcctBankName);//付款人银行
			maindto.setSbgttypecode(BgtTypeCode);//预算类型编码
			maindto.setSbgttypename(BgtTypeName);//预算类型编码
			maindto.setSprocatcode(ProCatCode);
			maindto.setSprocatname(ProCatName);
			maindto.setSagencyname(sAgencyName);
			maindto.setSincomesortname(IncomeSortName);//收入分类科目名称
			maindto.setSincomesortname1(IncomeSortName1);//收入分类科目名称
			maindto.setSincomesortname2(IncomeSortName2);//收入分类科目名称
			maindto.setSincomesortname3(IncomeSortName3);//收入分类科目名称
			maindto.setSincomesortname4(IncomeSortName4);//收入分类科目名称
			maindto.setSincomesortcode1(IncomeSortCode1);//收入分类科目类编码
			maindto.setSincomesortcode2(IncomeSortCode2);//收入分类科目类编码
			maindto.setSincomesortcode3(IncomeSortCode3);//收入分类科目类编码
			maindto.setSincomesortcode4(IncomeSortCode4);//收入分类科目类编码
			maindto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//系统时间
			maindto.setFamt(BigDecimal.valueOf(Double.valueOf(PayAmt)));//金额
			maindto.setStaxorgcode(FinOrgCode);
			if("000057400006".equals(ITFECommonConstant.SRC_NODE)){//宁波专用
				maindto.setSdwbkreasoncode(sdetailHold2);//浙江宁波用明细信息中的Hold2作为退付原因代码
//				maindto.setStaxorgcode(Hold2);//宁波接口中用主单信息中的Hold2作为征收机关代码
				maindto.setCbdglevel(Hold1);//宁波接口中用主单信息中的Hold1作为预算级次代码
				maindto.setSdetailhold2(sId);//明细Id对账用
				maindto.setSdetailhold3(sdetailHold3);
				maindto.setSdetailhold4(sdetailHold4);
			}else{
				if("201057100006".equals(ITFECommonConstant.SRC_NODE)){
					maindto.setCbdglevel(Hold1);//浙江用主单信息中的Hold1作为预算级次代码
				}else{
					maindto.setCbdglevel("");
				}
				maindto.setSdwbkreasoncode("");
				maindto.setSdetailhold1(sdetailHold1);
//				maindto.setSdetailhold2(sdetailHold2);//退回金额占用
				maindto.setSdetailhold3(Id);//凭证id
				maindto.setSdetailhold4(sId);//明细id
			}
			maindto.setShold1(Hold1);//保留字段1
			maindto.setShold2(Hold2);//保留字段2
			
			
			
			if("".equals(PayeeAcctBankNo) || PayeeAcctBankNo ==null){
				maindto.setSrecbankname(PayeeAcctBankName);//收款人银行名称
				if(bankInfo!=null&&bankInfo.size()>0){
					TsConvertbanknameDto dto = bankInfo.get(PayeeAcctBankName);
					if(dto != null){//行名补录过
					
						maindto.setSinputrecbankname(dto.getSbankname());//支付系统行名
						maindto.setSinputrecbankno(dto.getSbankcode());//支付系统行号
						maindto.setSpayeeopnbnkno(dto.getSbankcode()); //收款人开户行行号
						maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);//需要补录
						maindto.setScheckstatus(StateConstant.CHECKSTATUS_0);//已补录
					}else{
						maindto.setSpayeeopnbnkno("");//收款人开户行行号					
						maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);//需要补录
						maindto.setScheckstatus(StateConstant.CHECKSTATUS_0);//未补录
					}
				}else{
					maindto.setSpayeeopnbnkno("");//收款人开户行行号					
					maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);//需要补录
					maindto.setScheckstatus(StateConstant.CHECKSTATUS_0);//未补录
				}
			}else{
				maindto.setSrecbankname(PayeeAcctBankName);//收款人银行名称
				maindto.setSpayeeopnbnkno(PayeeAcctBankNo);//收款人开户行行号
				maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_NO);//不需要补录
				maindto.setScheckstatus(StateConstant.CHECKSTATUS_1);//已补录
				//填写支付系统行名行号
				if(bankmap != null){
					TsPaybankDto paybankdto = bankmap.get(PayeeAcctBankNo);
					if(paybankdto != null){
						maindto.setSinputrecbankname(paybankdto.getSbankname());//支付系统行名
						maindto.setSinputrecbankno(paybankdto.getSbankno());//支付系统行号
						if(ITFECommonConstant.PUBLICPARAM.indexOf(",verify=false,")>=0)
						{
							maindto.setScheckstatus(StateConstant.CHECKSTATUS_4);// 复核状态为已复核
							maindto.setScheckusercode("admin");// 复核人
						}else
						{
							maindto.setScheckstatus(StateConstant.CHECKSTATUS_1);//已补录
						}
					}else{
						maindto.setSinputrecbankname("未匹配到支付系统行号");//支付系统行名
						maindto.setSinputrecbankno("000000000000");//支付系统行号
					}
				}else{
					maindto.setSinputrecbankname("未匹配到支付系统行号");//支付系统行名
					maindto.setSinputrecbankno("000000000000");//支付系统行号
				}
				
			}
			if(dwbkreasonlist!=null&&!"000057400006".equals(ITFECommonConstant.SRC_NODE)){
				TsDwbkReasonDto dto = dwbkreasonlist.get(ReturnReasonName);
				if(dto!=null && !"".equals(dto.getSdrawbackreacode())){
					maindto.setSdwbkreasoncode(dto.getSdrawbackreacode());
				}
			}
			//预算科目代码list
			expFuncCodeList = new ArrayList<String>(); 
			expFuncCodeList.add(IncomeSortCode);
			
			vDto=new TvVoucherinfoDto();
			vDto.setSdealno(mainvou);
			vDto=(TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
			/**
			 * 组装verifydto,进行报文校验
			 */
			verifydto.setTrecode(TreCode);
			verifydto.setFinorgcode(FinOrgCode);
			verifydto.setVoucherno(VoucherNo);
			verifydto.setVoudate(VouDate);
			verifydto.setAgentAcctNo(PayeeAcctNo);
			verifydto.setAgentAcctName(PayeeAcctName);
			verifydto.setClearAcctNo(PayAcctNo);
			verifydto.setClearAcctName(PayAcctName);
			//增加年度，总金额的校验 by renqingbin
			verifydto.setPaybankname(PayeeAcctBankName);
			verifydto.setOfyear(StYear);
			verifydto.setFamt(PayAmt);
			String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_5209);
			//对宁波的接口专用字段进行校验
			if("000057400006".equals(ITFECommonConstant.SRC_NODE)){
				if(StringUtils.isBlank(Hold1)){
					returnmsg+="预算级次代码不能为空。";
				}
				if(StringUtils.isBlank(Hold2)){
					returnmsg+="征收机关代码不能为空。";
				}
				if(StringUtils.isBlank(ReturnReasonName)){
					returnmsg+="退付原因不能为空。";
				}
				if(StringUtils.isBlank(sdetailHold2)){
					returnmsg+="退库原因代码不能为空。";
				}
				if(StringUtils.isBlank(sdetailHold5)){
					returnmsg+="TBS辅助标志不能为空。";
				}
			}
			if(returnmsg != null){//返回错误信息签收失败
				voucher.voucherComfail(vDto.getSdealno(), returnmsg);
				continue;
			}
			
			/**
			 *校验主表金额是否与子表金额相等 
			 *
			 */
			if(maindto.getFamt().compareTo(new BigDecimal("0.00"))<0){
				String errMsg="主单金额不能为负"+maindto.getFamt();
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
			}
			
			/**
			 *明细条数不能超过500
			 * 
			 */
			if (listDetail.size() > 1) {
				String errMsg = "明细条数只能有一笔";
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
			}
			
			if(maindto.getFamt().compareTo(BigDecimal.valueOf(Double.valueOf(sPayAmt))) != 0){
				String errMsg="主单金额与明细累计金额不相等，主单金额："+maindto.getFamt()+" 明细累计金额： "+sPayAmt;
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
			}
		
		
			/**
			 * 业务表入库，校验报文合法性
			 * 
			 * 业务主表，子表入库失败即签收失败
			 */
		
			DatabaseFacade.getODB().create(maindto);	
			}catch(JAFDatabaseException e){
				logger.error(e);				
				voucher.voucherComfail(mainvou,"保存数据库错误："+e.getMessage());
				continue;	
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
		return;
	}
	
	public Voucher getVoucher() {
		return voucher;
	}
	public void setVoucher(Voucher voucher) {
		this.voucher = voucher;
	}

}

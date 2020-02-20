package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsQueryAmtDto;
import com.cfcc.itfe.persistence.dto.TsSpecacctinfoDto;
import com.cfcc.itfe.persistence.dto.TsTaxpayacctDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.persistence.pk.TsInfoconnorgaccPK;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

@SuppressWarnings({ "unchecked", "static-access" })
public class Voucher5267MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher5267MsgServer.class);
	private Voucher voucher;
	private Map<String,TsQueryAmtDto> amtMap = null;
	private Map<String,TsSpecacctinfoDto> specacctMap = null;
	/**
	 * 财政发往人行的专户拨款凭证，此类只保存业务数据
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
		}
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("解析专户拨款凭证5267报文出现错误！",e);
			throw new ITFEBizException("解析专户拨款凭证5267报文出现错误！",e);
			
		}
		List VoucherBodyList  = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
		String StYear  = "";//年度
		String VtCode  = "";//凭证类型
		
		String currentDate = TimeFacade.getCurrentStringTime();// 当前系统日期
//		List<String> voucherList = new ArrayList<String>();
		
		//获取行政区划代码、年度和凭证类型
		if(VoucherBodyList.size()>0){
			//VoucherBody
			Element element  = (Element)VoucherBodyList.get(0);
//			AdmDivCode  = element.attribute("AdmDivCode").getText();
			StYear  = element.attribute("StYear").getText();
			VtCode  = element.attribute("VtCode").getText();
		}
		
		//预算单位代码list
		Set agencyCodeList = null;
		//预算科目代码list
		ArrayList<String> expFuncCodeList = null;
		//负金额list
		ArrayList<BigDecimal> amtList = null; 
		TvPayoutmsgmainDto maindto  = null;
		List subDtoList=null; 
		List lists=new ArrayList();
		List list=null;
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
			String Attach = ((Element) VoucherBodyList.get(i)).elementText("Attach");//附加信息
			String Id = elementVoucher.elementText("Id");//专户拨款凭证Id
//			String admDivCode  = elementVoucher.elementText("AdmDivCode");//行政区划代码
//			String stYear = elementVoucher.elementText("StYear");//业务年度
			String VouDate = elementVoucher.elementText("VouDate"); //凭证日期
			String TreCode = elementVoucher.elementText("TreCode"); //国库主体代码
			String FinOrgCode = elementVoucher.elementText("FinOrgCode");//财政机关代码
			String FundTypeCode = elementVoucher.elementText("FundTypeCode");//资金性质编码
			String FundTypeName = elementVoucher.elementText("FundTypeName");//资金性质名称
			String PayTypeCode = elementVoucher.elementText("PayTypeCode");//支付方式编码
			String PayTypeName = elementVoucher.elementText("PayTypeName");//支付方式名称
			String ClearBankCode = elementVoucher.elementText("ClearBankCode");//人民银行编码
			String ClearBankName = elementVoucher.elementText("ClearBankName"); //人民银行名称
			String PayeeAcctNo = elementVoucher.elementText("PayeeAcctNo");	//收款人账号
			String PayeeAcctName = elementVoucher.elementText("PayeeAcctName");//收款人名称
			String PayeeAcctBankName = elementVoucher.elementText("PayeeAcctBankName");//收款人银行
			String PayeeAcctBankNo = elementVoucher.elementText("PayeeAcctBankNo");//收款银行行号
			String PayAcctNo = elementVoucher.elementText("PayAcctNo");//付款人账号
			String PayAcctName = elementVoucher.elementText("PayAcctName");//付款人名称
			String PayAcctBankName = elementVoucher.elementText("PayAcctBankName");//付款人银行
			String agencyCode=elementVoucher.elementText("AgencyCode");//预算单位编码
			String agencyName=elementVoucher.elementText("AgencyName");//预算单位名称
			String PaySummaryCode = elementVoucher.elementText("PaySummaryCode");//用途编码
			String PaySummaryName = elementVoucher.elementText("PaySummaryName"); //用途名称
			String PayAmt = elementVoucher.elementText("PayAmt");//拨款金额
//			String xPayAmt = elementVoucher.elementText("XPayAmt");//实际拨款金额
//			String xPayDate = elementVoucher.elementText("XPayDate");//支付日期
			String XAgentBusinessNo = elementVoucher.elementText("XAgentBusinessNo");//银行交易流水号
			String Hold1=elementVoucher.elementText("Hold1");//预留字段1
//			String Hold2=elementVoucher.elementText("Hold2");//预留字段2被退回金额占用
			/**
			 * 组装TvPayoutmsgmainDto对象
			 **/
			maindto  = new TvPayoutmsgmainDto();
			mainvou =dealnos.get(VoucherNo);//获取序列号
			maindto.setSbizno(mainvou);//序列号  
			maindto.setSorgcode(ls_OrgCode);//核算主体代码
			maindto.setScommitdate(currentDate);//委托日期
			maindto.setSaccdate(currentDate);//账务日期
			maindto.setSfilename(ls_FileName);//文件名
			maindto.setStrecode(TreCode);//国库代码
			maindto.setSpackageno("");//包流水号
			maindto.setSpayunit(FinOrgCode);//出票单位
			maindto.setSdealno(mainvou.substring(8, 16));//交易流水号
			maindto.setStaxticketno(VoucherNo);//凭证编号
			maindto.setSgenticketdate(VouDate);//凭证日期
			maindto.setSpayeracct(PayAcctNo);//付款人账号
			maindto.setSpayername(PayAcctName);//付款人名称
			maindto.setSpayeraddr("");
			maindto.setNmoney(BigDecimal.valueOf(Double.valueOf(PayAmt)));//金额
			if("".equals(PayeeAcctBankNo) || PayeeAcctBankNo ==null){
				maindto.setSrecbankname(PayeeAcctBankName);//收款人银行名称
				if(bankInfo!=null&&bankInfo.size()>0){
					TsConvertbanknameDto dto = bankInfo.get(PayeeAcctBankName);
					if(dto != null){//行名补录过
					
						maindto.setSinputrecbankname(dto.getStcbankname());//支付系统行名
						maindto.setSinputrecbankno(dto.getSbankcode());//支付系统行号
						maindto.setSpayeebankno(dto.getSbankcode());//收款人开户行
						maindto.setSrecbankno(dto.getSbankcode());//清算银行
						maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);//需要补录
//						if(ITFECommonConstant.PUBLICPARAM.indexOf(",verify=false,")>=0)
//						{
//							maindto.setScheckstatus(StateConstant.CHECKSTATUS_4);// 复核状态为已复核
//							maindto.setSchecksercode("admin");// 复核人
//						}else
//						{
							maindto.setScheckstatus(StateConstant.CHECKSTATUS_1);//已补录
//						}
					}else{
						maindto.setSrecbankno("");//收款人开户行
						maindto.setSpayeebankno("");//清算银行			
						maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);//需要补录
						maindto.setScheckstatus(StateConstant.CHECKSTATUS_0);//未补录
					}
				}else{
					maindto.setSrecbankno("");//收款人开户行
					maindto.setSpayeebankno("");//清算银行			
					maindto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);//需要补录
					maindto.setScheckstatus(StateConstant.CHECKSTATUS_0);//未补录
				}
			}else{
				
				maindto.setSrecbankname(PayeeAcctBankName);//收款人银行名称
				maindto.setSpayeebankno(PayeeAcctBankNo);//收款人开户行行号
				maindto.setSrecbankno(PayeeAcctBankNo);//收款人开户行行号
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
							maindto.setSchecksercode("admin");// 复核人
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
			
			maindto.setSrecacct(PayeeAcctNo);//收款人账号
			maindto.setSrecname(PayeeAcctName);//收款人名称
			maindto.setStrimflag(MsgConstant.TIME_FLAG_NORMAL);//调整期标志
			maindto.setSofyear(StYear);//所属年度
			maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);//预算种类(默认预算内)
			TsInfoconnorgaccPK tmppk = new TsInfoconnorgaccPK();
			tmppk.setSorgcode(ls_OrgCode); // 核算主体
			tmppk.setStrecode(TreCode); // TreCode国库主体
			tmppk.setSpayeraccount(PayAcctNo);// 付款账号
			TsInfoconnorgaccDto resultdto = null;
			try {
				resultdto = (TsInfoconnorgaccDto) DatabaseFacade.getDb().find(tmppk);
				if(resultdto!=null)
				{
					maindto.setSbudgettype(resultdto.getSbiztype());
				}
			} catch (JAFDatabaseException e) {
				logger.error("查询库款账户异常:" + e.getMessage());
			}
			maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);//状态
			maindto.setSusercode("0000");//用户代码
			//附言（资金用途作为附言发送到TIPS）
			if(!StringUtils.isBlank(PaySummaryName)){
					maindto.setSaddword(PaySummaryName.getBytes().length>60 ? CommonUtil.subString(PaySummaryName, 60) : PaySummaryName);
			}else{
				maindto.setSaddword("");
			}
			maindto.setSbackflag(StateConstant.MSG_BACK_FLAG_NO);// 退款标志
			maindto.setSid(Id); //专户拨款凭证Id
			maindto.setSpayerbankname(PayAcctBankName);//付款人银行名称
			maindto.setSfundtypecode(FundTypeCode);//资金性质编码
			maindto.setSfundtypename(FundTypeName);//资金性质名称
			maindto.setSpaytypecode(PayTypeCode);//支付方式编码
			maindto.setSpaytypename(PayTypeName);//支付方式名称
			maindto.setSclearbankcode(ClearBankCode);//人民银行编码
			maindto.setSclearbankname(ClearBankName);//人民银行名称
			maindto.setSpaysummarycode(PaySummaryCode);//用途编码
			maindto.setSpaysummaryname(PaySummaryName);//用途名称
			maindto.setShold1(Hold1);//保留字段1
//			maindto.setShold2(Hold2);//保留字段2-退回金额占用
			maindto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//系统时间
			maindto.setSxagentbusinessno(XAgentBusinessNo);
			maindto.setSbudgetunitcode(agencyCode);//预算单位代码
			maindto.setSunitcodename(agencyName);//预算单位名称
			agencyCodeList = new HashSet();//预算单位代码list
			agencyCodeList.add(agencyCode);
			expFuncCodeList = new ArrayList<String>();//预算科目代码list
			amtList = new ArrayList<BigDecimal>();//负金额list
			Set<String> subcodeSet = new HashSet<String>();//存储支出科目代码
			List<String>  subDtoIdList = new ArrayList<String>();//子表明细Id集合
			/**
			 * 组装TvPayoutmsgsubDto对象
			 */
			subDtoList=new ArrayList<TvPayoutmsgsubDto>();
			
			for(int j=0;j<listDetail.size();j++){
				
				//明细合计金额
				Element elementDetail  = (Element)listDetail.get(j);
				String sId=(j+1)+"";//拨款明细编号
				String sVoucherBillId=elementDetail.elementText("VoucherBillId");//拨款凭证Id
				String BgtTypeCode = elementDetail.elementText("BgtTypeCode");//预算类型编码
				String BgtTypeName = elementDetail.elementText("BgtTypeName");//预算类型名称
				String depProCode = elementDetail.elementText("DepProCode");//预算项目编码
				String depProName = elementDetail.elementText("DepProName");//预算项目名称
				if(depProName!=null && !depProName.equals("") && new String(depProName.getBytes("GBK"),"iso-8859-1").length()>42)
					depProName = FileUtil.subStringOfCh(depProName, 42, "GBK");
				String ProCatCode = elementDetail.elementText("ProCatCode");//收支管理编码
				String ProCatName = elementDetail.elementText("ProCatName");//收支管理名称
				String payeeAcctNo = elementDetail.elementText("PayeeAcctNo");//收款人账号
				String payeeAcctName = elementDetail.elementText("PayeeAcctName");//收款人名称
				String payeeAcctBankName = elementDetail.elementText("PayeeAcctBankName");//收款人银行
//				String payeeAcctBankNo = elementDetail.elementText("PayeeAcctBankNo");//收款银行行号
				String ExpFuncCode = elementDetail.elementText("ExpFuncCode");//支出功能分类科目编码
				subcodeSet.add(ExpFuncCode);
				String ExpFuncName = elementDetail.elementText("ExpFuncName");//支出功能分类科目名称
				String ExpFuncCode1 = elementDetail.elementText("ExpFuncCode1");//支出功能分类科目类编码
				String ExpFuncName1 = elementDetail.elementText("ExpFuncName1");//支出功能分类科目类名称
				String ExpFuncCode2 = elementDetail.elementText("ExpFuncCode2");//支出功能分类科目款编码
				String ExpFuncName2 = elementDetail.elementText("ExpFuncName2");//支出功能分类科目款名称
				String ExpFuncCode3 = elementDetail.elementText("ExpFuncCode3");//支出功能分类科目项编码
				String ExpFuncName3 = elementDetail.elementText("ExpFuncName3");//支出功能分类科目项名称
				String ExpEcoCode = elementDetail.elementText("ExpEcoCode");//支出经济分类科目类编码
				String ExpEcoName = elementDetail.elementText("ExpEcoName");//支出经济分类科目类名称
				String ExpEcoCode1 = elementDetail.elementText("ExpEcoCode1");//支出经济分类科目类编码
				String ExpEcoName1 = elementDetail.elementText("ExpEcoName1");//支出经济分类科目类名称
				String ExpEcoCode2 = elementDetail.elementText("ExpEcoCode2");//支出经济分类科目款编码
				String ExpEcoName2 = elementDetail.elementText("ExpEcoName2");//支出经济分类科目款名称
				String projectCategoryCode = elementDetail.elementText("ProjectCategoryCode");//专户项目归类代码
				String projectCategoryName = elementDetail.elementText("projectCategoryName");//专户项目归类名称
				String sPayAmt = elementDetail.elementText("PayAmt");//支付金额
				String sdetailHold1 = elementDetail.elementText("Hold1");//预留字段1
//				String sdetailHold2 = elementDetail.elementText("Hold2");//预留字段2
//				String sdetailHold3 = elementDetail.elementText("Hold3");//预留字段3
//				String sdetailHold4 = elementDetail.elementText("Hold4");//预留字段4
				String 	subId=elementDetail.elementText("Id");//拨款凭证明细Id
				
				TvPayoutmsgsubDto subdto=new TvPayoutmsgsubDto();
				//此处的设值待确认
				subdto.setSbizno(mainvou);//子表序列号
				subdto.setSseqno(Integer.valueOf(sId));//子表明细序号
				subdto.setSid(subId);//拨款凭证明细Id
				subdto.setSaccdate(currentDate);//账务日期
				subdto.setSecnomicsubjectcode(ExpEcoCode);//经济科目代码
				subdto.setStaxticketno(sVoucherBillId);//拨款凭证Id
				subdto.setSbudgetprjcode(depProCode);//预算项目编码
				subdto.setSbgttypecode(BgtTypeCode);//预算类型编码
				subdto.setSbgttypename(BgtTypeName);//预算类型名称
				subdto.setSprocatcode(ProCatCode);//收支管理编码
				subdto.setSprocatname(ProCatName);//收支管理名称
				subdto.setSagencycode(agencyCode);//预算单位编码
				subdto.setSagencyname(agencyName);//预算单位名称
				subdto.setSfunsubjectcode(ExpFuncCode);//支出功能分类科目编码
				subdto.setSexpfuncname(ExpFuncName);//支出功能分类科目名称
				subdto.setSexpfunccode1(ExpFuncCode1);//支出功能分类科目类编码
				subdto.setSexpfuncname1(ExpFuncName1);//支出功能分类科目类名称
				subdto.setSexpfunccode2(ExpFuncCode2);//支出功能分类科目款编码
				subdto.setSexpfuncname2(ExpFuncName2);//支出功能分类科目款名称
				subdto.setSexpfunccode3(ExpFuncCode3);//支出功能分类科目项编码
				subdto.setSexpfuncname3(ExpFuncName3);//支出功能分类科目项名称
				subdto.setSexpecocode(ExpEcoCode);//支出经济分类科目类编码
				subdto.setSexpeconame(ExpEcoName);//支出经济分类科目类名称
				subdto.setSexpecocode1(ExpEcoCode1);//支出经济分类科目类编码
				subdto.setSexpeconame1(ExpEcoName1);//支出经济分类科目类名称
				subdto.setSexpecocode2(ExpEcoCode2);//支出经济分类科目款编码
				subdto.setSexpeconame2(ExpEcoName2);//支出经济分类科目款名称
				String  SubXpayAmt = elementDetail.elementText("XpayAmt");//金额(实际支付金额，人民银行在回单中补录)
				String  SubXPayDate = elementDetail.elementText("XPayDate");//支付日期(人民银行处理日期，人民银行在回单中补录)
				String  SubXAgentBusinessNo = elementDetail.elementText("XAgentBusinessNo");//银行交易流水号(人民银行在回单中补录)
				String  SubXAddWord = elementDetail.elementText("XAddWord");//失败原因
				BigDecimal subPayAmt = new BigDecimal(sPayAmt);
				subdto.setNmoney(subPayAmt);//支付金额
				subdto.setShold1(sdetailHold1);//预留字段1
				subdto.setShold2(projectCategoryCode);//专户项目归类代码
				subdto.setShold3(projectCategoryName);//专户项目归类名称
				subdto.setShold4(depProName);//预留字段4
				subdto.setSid(subId);//拨款凭证明细Id/
				subdto.setSpayeeacctno(payeeAcctNo);//收款人账号
				subdto.setSpayeeacctname(payeeAcctName);//收款人名称
				subdto.setSpayeeacctbankname(payeeAcctBankName);//收款人银行
				if(StringUtils.isBlank(SubXpayAmt)){
					SubXpayAmt="0.00";
				}
				subdto.setSxpayamt(new BigDecimal(SubXpayAmt));//金额(实际支付金额，人民银行在回单中补录)
				subdto.setSxpaydate(SubXPayDate);//支付日期(人民银行处理日期，人民银行在回单中补录)
				subdto.setSxagentbusinessno(SubXAgentBusinessNo);//银行交易流水号(人民银行在回单中补录)
				subdto.setSxaddword(SubXAddWord);//失败原因
				//设置功能科目LIST,明细合计
				expFuncCodeList.add(ExpFuncCode);
				sumAmt = sumAmt.add(subPayAmt); 
				subDtoList.add(subdto);
				subDtoIdList.add(subdto.getSid());
			}
			vDto=new TvVoucherinfoDto();
			vDto.setSdealno(mainvou);
			vDto=(TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
			/**
			 * 在凭证索引表显示业务主表收付款人相关信息
			 */
			if(ITFECommonConstant.PUBLICPARAM.contains(",voucherdisplaymain=true,"))
			{
				vDto.setShold3(maindto.getSpayeracct());//付款账号
				vDto.setShold4(maindto.getSpayername());//付款名称
				vDto.setSext1(maindto.getSrecacct());//收款账号
				vDto.setSext3(maindto.getSrecname());//收款名称
				vDto.setSext4(maindto.getSrecbankname());//收款银行
				if(vDto.getShold3()!=null&&new String(vDto.getShold3().getBytes("GBK"),"iso-8859-1").length()>60)
					vDto.setShold3(CommonUtil.subString(vDto.getShold3(),60));
				if(vDto.getShold4()!=null&&new String(vDto.getShold4().getBytes("GBK"),"iso-8859-1").length()>60)
					vDto.setShold4(CommonUtil.subString(vDto.getShold4(),60));
				if(vDto.getSext1()!=null&&new String(vDto.getSext1().getBytes("GBK"),"iso-8859-1").length()>50)
					vDto.setSext1(CommonUtil.subString(vDto.getSext1(),50));
				if(vDto.getSext3()!=null&&new String(vDto.getSext3().getBytes("GBK"),"iso-8859-1").length()>50)
					vDto.setSext3(CommonUtil.subString(vDto.getSext3(),50));
				if(vDto.getSext4()!=null&&new String(vDto.getSext4().getBytes("GBK"),"iso-8859-1").length()>50)
					vDto.setSext4(CommonUtil.subString(vDto.getSext4(),50));
			}
			/*
			系统收到财政发送的实拨数据时，校验付款账户是否是核算主体+27101账户，如果不是代表是专户实拨资金
			校验收款账号是否是专户如果是，则标记为专户实拨资金，
			校验拨款限额是否超标
			解决方案： 单笔金额超标，启用凭证索引表中s_ext2备用字段进行标识 
			(抽取方法   参数 -厦门 )  
			*/
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",xm5207,")>=0)
			{
				TsQueryAmtDto queryAmtDto =  getAmtMap().get(maindto.getSorgcode()+maindto.getStrecode()+VtCode);
				if (null!=queryAmtDto)
				{
					BigDecimal moneyfilter = queryAmtDto.getFendamt();
					if (moneyfilter.compareTo(maindto.getNmoney())<=0) 
					{
						vDto.setSext2("1");//实拨金额超标
						vDto.setSreturnerrmsg("专户大额拨款！");
						vDto = (TvVoucherinfoDto) DatabaseFacade.getODB().updateWithResult(vDto);
					}
				}else if(maindto.getSrecname()!=null&&maindto.getSrecname().contains("财政"))
				{
					vDto.setSext2("1");//收款账户为实拨专户
					vDto.setSreturnerrmsg(vDto.getSreturnerrmsg()+"收款账户为实拨专户,在专户参数表中不存在！");
					vDto = (TvVoucherinfoDto) DatabaseFacade.getODB().updateWithResult(vDto);
				}
					//是否和财政专户数据匹配
				TsSpecacctinfoDto specacctinfoDto = getSpecacctMap().get(maindto.getSorgcode()+maindto.getSrecacct());
				if (null!= specacctinfoDto ) {//匹配
					vDto.setSext2("1");//收款账户为实拨专户
					vDto.setSreturnerrmsg(vDto.getSreturnerrmsg()+"收款账户为财政专户！");
					vDto = (TvVoucherinfoDto) DatabaseFacade.getODB().updateWithResult(vDto);
				}else if(maindto.getSrecname()!=null&&maindto.getSrecname().contains("财政"))
				{
					vDto.setSext2("1");//收款账户为实拨专户
					vDto.setSreturnerrmsg(vDto.getSreturnerrmsg()+"专户收款账户为财政专户,在专户参数表中不存在！");
					vDto = (TvVoucherinfoDto) DatabaseFacade.getODB().updateWithResult(vDto);
				}
				if((vDto.getSorgcode()+"27101").equals(maindto.getSpayeracct())){
					String errMsg="专户实拨资金付款账号不能为"+vDto.getSorgcode()+"27101";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
			}
			if(maindto.getSrecbankno()!=null&&maindto.getSrecbankno().startsWith("011"))
			{
				vDto.setSext2("1");//标记为库内调拨
				vDto.setSreturnerrmsg("此笔支出为核算主体间(内)调拨支出！");
				vDto = (TvVoucherinfoDto) DatabaseFacade.getODB().updateWithResult(vDto);
			}
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
			//增加校验预算单位名称
			verifydto.setAgencyName(maindto.getSunitcodename());
			//校验预算种类
			verifydto.setBudgettype(maindto.getSbudgettype());
			//核算主体
			verifydto.setOrgcode(maindto.getSorgcode());
			verifydto.setPaytypecode(PayTypeCode);//支付方式编码
			verifydto.setFundTypeCode(FundTypeCode);//资金性质编码
			
			String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_5267);
			
			//云南地税特色校验
			if(ITFECommonConstant.SRC_NODE.equals("000087100006")&&VtCode.equals(MsgConstant.VOUCHER_NO_5267)&&
					!Attach.equals("")&&Attach!=null&&Attach.trim().length()==3){
				if(subcodeSet!=null&&subcodeSet.size()==1){
					TsTaxpayacctDto taxPayAcctDto = new TsTaxpayacctDto();
					taxPayAcctDto.setSorgcode(ls_OrgCode);
					taxPayAcctDto.setStrecode(TreCode);
					taxPayAcctDto.setSpayeracct(PayAcctNo);
					taxPayAcctDto.setStaxsubcode(subcodeSet.iterator().next().trim());
					List<TsTaxpayacctDto> taxList = CommonFacade.getODB().findRsByDto(taxPayAcctDto);
					if(taxList==null||taxList.size()<=0){
						returnmsg+="国库付款账户["+PayAcctNo+"]与地税科目代码["+taxPayAcctDto.getStaxsubcode()+"]不对应。";
					}
				}
			}
			
			if(returnmsg != null){//返回错误信息签收失败
				voucher.voucherComfail(vDto.getSdealno(), returnmsg);
				continue;
			}
			
			/**
			 *校验子表中是否有负金额
			 *
			 */
			if(amtList.size() > 0){
				String errMsg="明细信息中包含有负金额的数据!";
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
			}
			
			/**
			 *校验主表金额是否与子表金额相等 
			 *
			 */
			if(maindto.getNmoney().compareTo(sumAmt) != 0){
				String errMsg="主单金额与明细累计金额不相等，主单金额："+maindto.getNmoney()+" 明细累计金额： "+sumAmt;
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
			 * 校验子表预算单位代码是否重复
			 * 
			 */
			if(agencyCodeList.size()>1){
				String errMsg="凭证明细中存在多个预算单位信息!";
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
			}
			
			/**
			 * 校验上海明细信息合法性
			 * @author 张会斌
			 */
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0&&null!=subDtoList&&subDtoList.size()>0){
				String returnMsg = voucherVerify.checkVerify(subDtoList,MsgConstant.VOUCHER_NO_5267);
				if(StringUtils.isNotBlank(returnMsg)){
					voucher.voucherComfail(vDto.getSdealno(), returnMsg);
					continue;
				}
			}
			
			
			/**
			 * 校验明细Id是否为空或重复
			 */
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0){
				String checkIdMsg=voucherVerify.checkValidSudDtoId(subDtoIdList);
				if(checkIdMsg!=null){
					//返回错误信息签收失败
					voucher.voucherComfail(mainvou, checkIdMsg);
					continue;				
				}
			}
			
			
			/**
			 * 业务表入库，校验报文合法性
			 * 
			 * 业务主表，子表入库失败即签收失败
			 */
			TvPayoutmsgsubDto[] subDtos=new TvPayoutmsgsubDto[subDtoList.size()];
			subDtos=(TvPayoutmsgsubDto[]) subDtoList.toArray(subDtos);
			DatabaseFacade.getODB().create(maindto);
			DatabaseFacade.getODB().create(subDtos);
			vDto.setIcount(subDtoList.size());
			}catch(JAFDatabaseException e){
				logger.error(e);
				voucher.voucherComfail(mainvou, "保存数据库出现错误："+e.getMessage());
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
			
			/**
			 * 添加实拨资金明细列表,用于与8207批量明细比对（上海）
			 * @author 张会斌
			 */
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0&&maindto.getSbusinesstypecode().equals(StateConstant.BIZTYPE_CODE_BATCH)){
				list.add(subDtoList);
			}
			
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
	public Map<String, TsQueryAmtDto> getAmtMap() throws JAFDatabaseException, ValidateException {
		if(amtMap==null)
		{
			amtMap = new HashMap<String, TsQueryAmtDto>();
			List<TsQueryAmtDto> queryAmtDtoList =  CommonFacade.getODB().findRsByDto(new TsQueryAmtDto());
			if(queryAmtDtoList!=null&&queryAmtDtoList.size()>0)
			{
				for(TsQueryAmtDto tempdto:queryAmtDtoList)
					amtMap.put(tempdto.getSorgcode()+tempdto.getStrecode()+tempdto.getSbiztype(), tempdto);
				
			}			
		}
		return amtMap;
	}

	public void setAmtMap(Map<String, TsQueryAmtDto> amtMap) {
		this.amtMap = amtMap;
	}

	public Map<String, TsSpecacctinfoDto> getSpecacctMap() throws JAFDatabaseException, ValidateException {
		if(specacctMap==null)
		{
			specacctMap = new HashMap<String, TsSpecacctinfoDto>();
			List<TsSpecacctinfoDto> specacctinfoList =  CommonFacade.getODB().findRsByDto(new TsSpecacctinfoDto());
			if(specacctinfoList!=null&&specacctinfoList.size()>0)
			{ 
				for(TsSpecacctinfoDto tempdto:specacctinfoList)
					specacctMap.put(tempdto.getSorgcode()+tempdto.getSpayeeacct(), tempdto);
			}
		}
		return specacctMap;
	}

	public void setSpecacctMap(Map<String, TsSpecacctinfoDto> specacctMap) {
		this.specacctMap = specacctMap;
	}
}

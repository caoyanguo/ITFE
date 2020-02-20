 package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
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

public class Voucher2301MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher2301MsgServer.class);
	private Voucher voucher;

	/**
	 * 财政发往人行的申请划款凭证回单
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
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
			logger.error("解析申请划款凭证回单2301报文出现错误！", e);
			throw new ITFEBizException("解析申请划款凭证回单2301报文出现错误！", e);

		}
		List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
		String AdmDivCode = "";// 行政区划代码
		String StYear = "";// 年度
		String VtCode = "";// 凭证类型
//		Date currentDate = TimeFacade.getCurrentDateTime();// 当前系统日期
//		List<String> voucherList = new ArrayList<String>();

		// 获取行政区划代码、年度和凭证类型
		if (VoucherBodyList.size() > 0) {
			Element element = (Element) VoucherBodyList.get(0);
			AdmDivCode = element.attribute("AdmDivCode").getText();
			StYear = element.attribute("StYear").getText();
			VtCode = element.attribute("VtCode").getText();
		}

		// 预算单位代码list
		ArrayList<String> agencyCodeList = null;
		// 预算科目代码list
		ArrayList<String> expFuncCodeList = null;

		String SupDepCode = "";
		TvPayreckBankDto maindto = null;
		List subDtoList = null;
		List lists = new ArrayList();
		List list = null;
		// 凭证序列号
		String mainvou = "";
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		
		// 解析报文
		for (int i = 0; i < VoucherBodyList.size(); i++) {
			try {
				// 明细合计金额
				BigDecimal sumAmt = new BigDecimal("0.00");
				// VoucherBody
				Element element = (Element) VoucherBodyList.get(i);
				// 报文头与报文体凭证编号不一致导致签收异常（宁夏）
				String VoucherNo = element.attribute("VoucherNo").getText();
				mainvou = dealnos.get(VoucherNo);// 获取序列号
				// Voucher
				Element elementVoucher = (Element) element
						.selectSingleNode("Voucher");
				// 明细信息List
				List listDetail = elementVoucher.selectSingleNode("DetailList")
						.selectNodes("Detail");
				//报文校验信息dto
				VoucherVerifyDto verifydto = new VoucherVerifyDto();
				VoucherVerify voucherVerify = new VoucherVerify();	
				/**
				 * 凭证信息
				 */
				String Attach = ((Element) VoucherBodyList.get(i))
						.elementText("Attach");// 附加信息
				String Id = elementVoucher.elementText("Id");// 申请划款凭证Id
				String VouDate = elementVoucher.elementText("VouDate"); // 凭证日期
				//String VoucherNo = elementVoucher.elementText("VoucherNo");// 凭证号
				String TreCode = elementVoucher.elementText("TreCode"); // 国库主体代码
				String FinOrgCode = elementVoucher.elementText("FinOrgCode");// 财政机关代码
				String BgtTypeCode = elementVoucher.elementText("BgtTypeCode");// 预算类型编码
				String BgtTypeName = elementVoucher.elementText("BgtTypeName");// 预算类型名称
				String FundTypeCode = elementVoucher
						.elementText("FundTypeCode");// 资金性质编码
				String FundTypeName = elementVoucher
						.elementText("FundTypeName");// 资金性质名称
				String PayTypeCode = elementVoucher.elementText("PayTypeCode");// 支付方式编码
				String PayTypeName = elementVoucher.elementText("PayTypeName");// 支付方式名称
				String AgentAcctNo = elementVoucher.elementText("AgentAcctNo");// 收款银行账号
				String AgentAcctName = elementVoucher
						.elementText("AgentAcctName");// 收款银行账户名称
				String AgentAcctBankName = elementVoucher
						.elementText("AgentAcctBankName");// 收款银行
				String ClearAcctNo = elementVoucher.elementText("ClearAcctNo");// 付款账号
				String ClearAcctName = elementVoucher
						.elementText("ClearAcctName");// 付款账户名称
				String ClearAcctBankName = elementVoucher
						.elementText("ClearAcctBankName");// 付款银行
				String PayAmt = elementVoucher.elementText("PayAmt");// 汇总清算金额
				String PayBankName = elementVoucher.elementText("PayBankName");// 代理银行名称
				String PayBankNo = elementVoucher.elementText("PayBankNo");// 代理银行行号
				String Remark = elementVoucher.elementText("Remark");// 摘要
				String MoneyCorpCode = elementVoucher
						.elementText("MoneyCorpCode");// 金融机构编码
				String XPaySndBnkNo = elementVoucher
						.elementText("XPaySndBnkNo");// 支付发起行行号
//				String XAddWord = elementVoucher.elementText("XAddWord");// 附言
				String XClearDate = elementVoucher.elementText("XClearDate");// 清算日期
				String XPayAmt = elementVoucher.elementText("XPayAmt");// 汇总清算金额
				String Hold1 = elementVoucher.elementText("Hold1");// 预留字段1
				String Hold2 = elementVoucher.elementText("Hold2");// 预留字段2

				/**
				 * 组装TvPayreckBankDto对象
				 **/
				maindto = new TvPayreckBankDto();
				maindto.setStrano(mainvou.substring(8, 16));// 申请划款凭证Id
				maindto.setIvousrlno(Long.valueOf(mainvou));// 凭证流水号
				maindto.setSid(Id);// 申请划款凭证Id
				maindto.setSadmdivcode(AdmDivCode);//行政区划代码
				maindto.setSofyear(StYear);// 业务年度
				maindto.setSvtcode(VtCode);//凭证类型编号\
//				SimpleDateFormat f=new SimpleDateFormat("yyyyMMdd");
				maindto.setSbookorgcode(ls_OrgCode);//核算主体代码
				maindto.setDvoudate(CommonUtil.strToDate(VouDate)); // 凭证日期
				maindto.setSvouno(VoucherNo);// 凭证号
				maindto.setStrecode(TreCode); // 国库主体代码
				maindto.setSfinorgcode(FinOrgCode);// 财政机关代码
				maindto.setSbgttypecode(BgtTypeCode);// 预算类型编码
				maindto.setSbgttypename(BgtTypeName);// 预算类型名称
				maindto.setSfundtypecode(FundTypeCode);// 资金性质编码
				maindto.setSfundtypename(FundTypeName);// 资金性质名称
				maindto.setSpaytypecode(PayTypeCode);// 支付方式编码
				if("12".equals(PayTypeCode)|| PayTypeCode.startsWith("001002")){//财政普遍采用的是6位
					maindto.setSpaymode("1");// 授权支付
				}else if("11".equals(PayTypeCode) || PayTypeCode.startsWith("001001")){//财政普遍采用的是6位
					maindto.setSpaymode("0");// 支付方式编码    直接支付
				}
				maindto.setSpaytypename(PayTypeName);// 支付方式名称
				maindto.setSpayeeacct(AgentAcctNo);// 收款银行账号
				maindto.setSpayeename(AgentAcctName);// 收款银行账户名称
				maindto.setSagentacctbankname(AgentAcctBankName);// 收款银行
				maindto.setSpayeeaddr("");//收款人地址
				maindto.setSpayeraddr("");//付款人地址
				maindto.setSpayeracct(ClearAcctNo);// 付款账号
				maindto.setSpayername(ClearAcctName);// 付款账户名称
				maindto.setSclearacctbankname(ClearAcctBankName);// 付款银行
				maindto.setFamt(BigDecimal.valueOf(Double.valueOf(PayAmt)));// 汇总清算金额
				maindto.setSpaybankname(PayBankName);// 代理银行名称
				maindto.setSagentbnkcode(PayBankNo);// 代理银行行号
				maindto.setSdescription(Remark);// 摘要
				maindto.setSmoneycorpcode(MoneyCorpCode);// 金融机构编码
				maindto.setSxpaysndbnkno(XPaySndBnkNo);// 支付发起行行号
				maindto.setSaddword(Remark==null||"".equals(Remark)?(Attach!=null&&Attach.getBytes().length>60?CommonUtil.subString(Attach, 60):Attach):Remark.getBytes().length>60?CommonUtil.subString(Remark,60):Remark);// 摘要当做附言
				maindto.setSxcleardate(CommonUtil.strToDate(XClearDate));// 清算日期
				if(XPayAmt!=null && !XPayAmt.equals("")){
					maindto.setSxpayamt(BigDecimal.valueOf(Double.valueOf(XPayAmt)));// 汇总清算金额
				}else{
					maindto.setSxpayamt(BigDecimal.valueOf(Double.valueOf("0.00")));// 汇总清算金额
				}
				if("1".equals(ITFECommonConstant.ISCHECKPAYPLAN)){
					maindto.setSxpayamt(BigDecimal.valueOf(Double.valueOf(PayAmt)));
				}
				maindto.setShold1(Hold1);// 预留字段1
				maindto.setShold2(Hold2);// 预留字段2
				maindto.setDentrustdate(DateUtil.currentDate());//委托日期
				maindto.setSpackno("0000");//包流水号
				maindto.setStrimsign(MsgConstant.TIME_FLAG_NORMAL);//调整期标志
				if("000073100012".equals(ITFECommonConstant.SRC_NODE)){//湖南资金性质作为预算种类
					maindto.setSbudgettype(FundTypeCode);//预算种类(默认预算内)
				} else if(TreCode.startsWith("06")){//沈阳安照收款账户分为预算内、外
					if(ClearAcctNo.endsWith("271001") || MsgConstant.BDG_KIND_IN.equals(FundTypeCode)){
						maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);//预算种类(默认预算内)
					}else{
						maindto.setSbudgettype(MsgConstant.BDG_KIND_OUT);//预算种类(默认预算内)
					}
				}else{
					maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);//预算种类(默认预算内)
					TsInfoconnorgaccPK tmppk = new TsInfoconnorgaccPK();
					tmppk.setSorgcode(ls_OrgCode); // 核算主体
					tmppk.setStrecode(TreCode); // TreCode国库主体
					tmppk.setSpayeraccount(ClearAcctNo);// 付款账号
					TsInfoconnorgaccDto resultdto = null;
					try {
						resultdto = (TsInfoconnorgaccDto) DatabaseFacade.getDb().find(tmppk);
						if(resultdto!=null&&resultdto.getSbiztype()!=null&&!"".equals(resultdto.getSbiztype()))
						{
							maindto.setSbudgettype(resultdto.getSbiztype());
						}
					} catch (JAFDatabaseException e) {
						logger.error("查询库款账户异常:" + e.getMessage());
					}
				}
				maindto.setSpayeeopbkno(PayBankNo);//收款人开户行行号
				maindto.setSfilename(ls_FileName);
				maindto.setSresult(DealCodeConstants.DEALCODE_ITFE_DEALING);//状态 处理中
				maindto.setTsupdate(new Timestamp(new java.util.Date()
						.getTime()));// 系统时间
				// 预算单位代码list
				agencyCodeList = new ArrayList<String>();
				// 预算科目代码list
				expFuncCodeList = new ArrayList<String>();
				//子表明细Id集合
				List<String>  subDtoIdList = new ArrayList<String>();
				String sdetailId = null;//明细Id
				String errDetailMsg=null;
				/**
				 * 组装TvPayreckBankListDto对象
				 */
				subDtoList = new ArrayList<TvPayreckBankListDto>();
				StringBuffer verifyVoucherNoSql = new StringBuffer("");//校验sql
				List<String> paramList = new ArrayList<String>();
				for (int j = 0; j < listDetail.size(); j++) {
					Element elementDetail = (Element) listDetail.get(j);
					/**
					 * 校验报文明细Id节点是否存在
					 * 1、若节点不存在，是老版本，不更新明细Id
					 * 2、若节点存在，是新版本，更新明细Id
					 */
					Element sdetailIdElement=elementDetail.element("Id");
					//节点不存在
					if(sdetailIdElement==null)
						subDtoIdList.add("节点不存在");					
					else{
						sdetailId = sdetailIdElement.getText();//明细Id
						if(StringUtils.isBlank(sdetailId)){
							errDetailMsg="明细ID字段不能为空！";
							break;	
						}
					}
					String VoucherNol = elementDetail.elementText("VoucherNo");// 支付凭证单号
					SupDepCode = elementDetail.elementText("SupDepCode");// 一级预算单位编码
					String SupDepName = elementDetail.elementText("SupDepName");// 一级预算单位名称
					if(new String(SupDepName.getBytes("GBK"),"iso-8859-1").length()>60)
						SupDepName = CommonUtil.subString(SupDepName,60);
					String ExpFuncCode = elementDetail
							.elementText("ExpFuncCode");// 支出功能分类科目编码
					String ExpFuncName = elementDetail
							.elementText("ExpFuncName");// 支出功能分类科目名称
					String sPayAmt = elementDetail.elementText("PayAmt");// 支付金额
					String PaySummaryName = elementDetail
							.elementText("PaySummaryName");// 摘要名称
					String sdetailHold1 = elementDetail.elementText("Hold1");// 预留字段1
					String sdetailHold2 = elementDetail.elementText("Hold2");// 预留字段2
					String sdetailHold3 = elementDetail.elementText("Hold3");// 预留字段3
					String sdetailHold4 = elementDetail.elementText("Hold4");// 预留字段4
					/****************广州新增字段修改20161020****************/
					String DAVoucherNo       = elementDetail.elementText("DAVoucherNo"); //支付申请序号        
					String FundTypeCodeSub      = elementDetail.elementText("FundTypeCode"); //资金性质编码        
					String FundTypeNameSub      = elementDetail.elementText("FundTypeName"); //资金性质名称        
					String AgencyCode        = elementDetail.elementText("AgencyCode"); //基层预算单位编码    
					String AgencyName        = elementDetail.elementText("AgencyName"); //基层预算单位名称    
					String ExpEcoCode        = elementDetail.elementText("ExpEcoCode"); //支出经济分类科目编码
					String ExpEcoName        = elementDetail.elementText("ExpEcoName"); //支出经济分类科目名称
					String PayeeAcctNo       = elementDetail.elementText("PayeeAcctNo"); //收款人账号          
					String PayeeAcctName     = elementDetail.elementText("PayeeAcctName"); //收款人名称          
					String PayeeAcctBankName = elementDetail.elementText("PayeeAcctBankName"); //收款人银行             
					String RemarkSub            = elementDetail.elementText("Remark"); //备注                
					String AgentBusinessNo   = elementDetail.elementText("AgentBusinessNo"); //银行交易流水号      
					String XPayDate          = elementDetail.elementText("XPayDate"); //实际支付日期        
					String XCheckNo          = elementDetail.elementText("XCheckNo"); //支票号(结算号)      
					String XPayAmtSub           = elementDetail.elementText("XPayAmt"); //实际支付金额   
					if(XPayDate==null||"".equals(XPayDate)||"null".equals(XPayDate.toLowerCase()))
						XPayDate="";
					if(XPayAmtSub==null||"".equals(XPayAmtSub)||"null".equals(XPayAmtSub.toLowerCase()))
						XPayAmtSub="0";
					String XAddWordSub          = elementDetail.elementText("XAddWord"); //附言                
					String XPayeeAcctBankName= elementDetail.elementText("XPayeeAcctBankName"); //收款人银行          
					String XPayeeAcctNo      = elementDetail.elementText("XPayeeAcctNo"); //收款人账号          
					String XPayeeAcctName    = elementDetail.elementText("XPayeeAcctName"); //收款人全称        
					/****************广州新增字段修改20161020****************/
//					String PayDate = elementDetail.elementText("PayDate");// 预留字段4
					TvPayreckBankListDto subdto = new TvPayreckBankListDto();
					// 此处的设值待确认
					subdto.setIseqno(j+1);//序号
					subdto.setSacctprop(MsgConstant.ACCT_PROP_ZERO);//账户性质
					subdto.setIvousrlno(Long.valueOf(mainvou));// 子表序列号
					subdto.setSvouchern0(VoucherNol);// 子表明细序号
					verifyVoucherNoSql.append(" (c.S_VOUCHERN0=?) or");
					paramList.add(VoucherNol);
					// 支付凭证单号
					subdto.setSbdgorgcode(SupDepCode);// 一级预算单位编码
					subdto.setSsupdepname(SupDepName);// 一级预算单位名称
					subdto.setSfuncbdgsbtcode(ExpFuncCode);// 支出功能分类科目编码
					subdto.setSexpfuncname(ExpFuncName);// 支出功能分类科目名称
					subdto.setSecnomicsubjectcode("");//经济科目代码
					subdto.setFamt(BigDecimal.valueOf(Double.valueOf(sPayAmt)));// 支付金额
					subdto.setSpaysummaryname(PaySummaryName);// 摘要名称
					sdetailHold1 = sdetailHold1.getBytes().length>=42 ? CommonUtil.subString(sdetailHold1, 42) : sdetailHold1;
					sdetailHold2 = sdetailHold2.getBytes().length>=42 ? CommonUtil.subString(sdetailHold2, 42) : sdetailHold1;
					sdetailHold3 = sdetailHold3.getBytes().length>=200 ? CommonUtil.subString(sdetailHold3, 200) : sdetailHold1;
					sdetailHold4 = sdetailHold4.getBytes().length>=42 ? CommonUtil.subString(sdetailHold4, 42) : sdetailHold1;
					subdto.setShold1(sdetailHold1);// 预留字段1
					subdto.setShold2(sdetailHold2);// 预留字段2
					subdto.setShold3(sdetailHold3);// 预留字段3
					subdto.setShold4(sdetailHold4);// 预留字段4
					if(TreCode.startsWith("06") && "".equals(sdetailHold4)){
						subdto.setShold4(StateConstant.MERGE_VALIDATE_NOTCOMPARE);// 预留字段4
					}
					if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0){
						String sdetailPayDate = elementDetail.elementText("PayDate");// 预留字段4
						if(StringUtils.isBlank(sdetailPayDate)){
							errDetailMsg="明细商业银行办理时间不能为空。";
							break;							
						}
						if(sdetailPayDate.length()!=14){
							errDetailMsg="明细商业银行办理时间["+sdetailPayDate+"]格式不规范。";
							break;							
						}
						subdto.setSpaydate(sdetailPayDate.substring(0, 8)+" "+sdetailPayDate.substring(8, 10)
								+":"+sdetailPayDate.substring(10, 12)+":"+sdetailPayDate.substring(12));											
					}
					subdto.setTsupdate(new Timestamp(new java.util.Date()
					.getTime()));//更新时间
					subdto.setSid(sdetailId);
					//设置预算单位、科目LIST,明细合计
					if(!agencyCodeList.contains(TreCode+SupDepCode)){
						agencyCodeList.add(TreCode+SupDepCode);
					}
					
					/****************广州新增字段修改20161020****************/
					//暂时通过判断新增必填字段是否有值来确定填充字段的来源，后期可以通过Area或者节点号等方式来判断
					if(StringUtils.isNotBlank(FundTypeCodeSub) && StringUtils.isNotBlank(FundTypeNameSub)) {
						subdto.setSdavoucherno(DAVoucherNo); //支付申请序号
						subdto.setSfundtypecode(FundTypeCodeSub); //资金性质编码
						subdto.setSfundtypename(FundTypeNameSub); //资金性质名称
						subdto.setSagencycode(AgencyCode); //基层预算单位编码
						subdto.setSagencyname(AgencyName); //基层预算单位名称
						subdto.setSexpecocode(ExpEcoCode); //支出经济分类科目编码
						subdto.setSexpeconame(ExpEcoName); //支出经济分类科目名称
						subdto.setSremark(RemarkSub); //备注
						subdto.setSagentbusinessno(AgentBusinessNo); //银行交易流水号
						subdto.setSxpaydate(CommonUtil.strToDate(XPayDate)); //实际支付日期
						subdto.setSxcheckno(XCheckNo); //支票号(结算号)
						subdto.setSxpayamt(BigDecimal.valueOf(Double.valueOf(XPayAmtSub))); //实际支付金额
						subdto.setSxaddword(XAddWordSub); //附言
						subdto.setSpayeeacctno(PayeeAcctNo); //收款人账号
						subdto.setSpayeeacctname(PayeeAcctName); //收款人名称
						subdto.setSpayeeacctbankname(PayeeAcctBankName); //收款人银行
						subdto.setSxpayeeacctbankname(XPayeeAcctBankName); //收款人银行
						subdto.setSxpayeeacctno(XPayeeAcctNo); //收款人账号
						subdto.setSxpayeeacctname(XPayeeAcctName); //收款人全称
					}
					/****************广州新增字段修改20161020****************/
					
					expFuncCodeList.add(ExpFuncCode);
					// 明细合计
					sumAmt = sumAmt.add(BigDecimal.valueOf(Double.valueOf(sPayAmt)));
					subDtoIdList.add(subdto.getSid());
					subDtoList.add(subdto);
					
				}
				
				if(errDetailMsg!=null){
					voucher.voucherComfail(mainvou, errDetailMsg);
					continue;
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
				if(ITFECommonConstant.PUBLICPARAM.contains(",verifyVoucherNo=true,")||ITFECommonConstant.PUBLICPARAM.contains(",verifyid=true,")){
					String sql = "select c.S_VOUCHERN0 as S_VOUCHERNO from TV_VOUCHERINFO a,Tv_Payreck_Bank_List c where a.S_STYEAR=? and (a.S_STATUS <>? and a.S_STATUS <>? and a.S_STATUS <>? ) and a.S_trecode=? and a.S_DEALNO = c.I_VOUSRLNO and ("+verifyVoucherNoSql.toString().substring(0,verifyVoucherNoSql.lastIndexOf("or"))+")";
					String mainsql = "select count(*) from (select S_ID from  Tv_Payreck_Bank where S_ID=? union select S_ID from HTv_Payreck_Bank where S_ID=?)";
					SQLExecutor execDetail = null;
					try
					{
						execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
						SQLResults result = null;
						if(ITFECommonConstant.PUBLICPARAM.contains(",verifyid=true,"))
						{
							execDetail.addParam(maindto.getSid());
							execDetail.addParam(maindto.getSid());
							result=  execDetail.runQuery(mainsql);//查询数据
							if(result!=null&&result.getInt(0, 0)>0)
							{
								voucher.voucherComfail(mainvou, "主单中id编号有重复！");
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
							result=  execDetail.runQuery(sql.toString());//查询数据
							if(result!=null&&result.getRowCount()>0)
							{
								voucher.voucherComfail(mainvou, "明细中支付凭证编号"+result.getString(0, 0)+"已经清算！");
								continue;
							}
						}
						if(execDetail!=null)
							execDetail.closeConnection();
					}catch (JAFDatabaseException e2) {		
						logger.error(e2.getMessage(),e2);
						continue;
					}catch (Exception e2) {
						logger.error(e2.getMessage(),e2);
						continue;
					}finally
					{
						if(execDetail!=null)
							execDetail.closeConnection();
					}
				}
				vDto = new TvVoucherinfoDto();
				vDto.setSdealno(mainvou);
				vDto = (TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(
						vDto).get(0);
								
				/**
				 * 组装verifydto,进行报文校验
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
				//增加年度，总金额的校验 by renqingbin
				verifydto.setOfyear(StYear);
				verifydto.setFamt(PayAmt);
				verifydto.setBudgettype(maindto.getSbudgettype());
				//核算主体
				verifydto.setOrgcode(maindto.getSbookorgcode());
				String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_2301);
				if(returnmsg != null){//返回错误信息签收失败
					voucher.voucherComfail(vDto.getSdealno(), returnmsg);
					continue;
				}
				/**
				 *校验主表金额是否与子表金额相等
				 * 
				 */
				if (maindto.getFamt().compareTo(sumAmt) != 0) {
					String errMsg = "主单金额与明细累计金额不相等，主单金额：" + maindto.getFamt()
							+ " 明细累计金额： " + sumAmt;
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				/**
				 *明细条数不能超过500
				 * 
				 */
				if ( (!TreCode.startsWith("19") || TreCode.startsWith("1906")) && subDtoList.size() > 499) {
					String errMsg = "明细条数必须小于500!";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				maindto.setIstatinfnum(subDtoList.size());//明细信息条数
				/**
				 * 业务表入库，校验报文合法性
				 * 
				 * 业务主表，子表入库失败即签收失败
				 */
				TvPayreckBankListDto[] subDtos = new TvPayreckBankListDto[subDtoList
						.size()];
				subDtos = (TvPayreckBankListDto[]) subDtoList.toArray(subDtos);
				DatabaseFacade.getODB().create(maindto);
				DatabaseFacade.getODB().create(subDtos);
				vDto.setIcount(subDtoList.size());
				vDto.setSext1(maindto.getSpaymode());//支付方式
				if(ITFECommonConstant.PUBLICPARAM.contains(",quotabudgettype,"))
				{
					vDto.setSext4(maindto.getSbudgettype());
				}
				DatabaseFacade.getODB().update(vDto);
			}catch(Exception e){
				logger.error(e);
				if(e.getMessage()==null||"null".equals(e.getMessage().trim()))
					voucher.voucherComfail(mainvou, "报文不规范缺少节点：请核对报文规范!");
				else
					voucher.voucherComfail(mainvou, "报文不规范："+e.getMessage());
				continue;
			}
			// 签收成功
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
			list.add(agencyCodeList);//预算单位list
			list.add(subDtoList);
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
		}catch(Exception e){
			logger.error(e);
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

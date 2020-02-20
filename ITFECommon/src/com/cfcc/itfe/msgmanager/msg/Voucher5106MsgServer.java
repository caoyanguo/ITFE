package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.persistence.pk.TsInfoconnorgaccPK;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;


public class Voucher5106MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher5106MsgServer.class);
	private Voucher voucher;
	/**
	 * 财政发往人行的授权支付额度凭证5106，此类只保存业务数据
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();
		String voucherXml = (String) muleMessage.getPayload();
		String ls_FileName = (String) muleMessage.getProperty("fileName");
		String ls_OrgCode = (String) muleMessage.getProperty("orgCode");
		HashMap<String,String> dealnos=(HashMap<String,String>)muleMessage.getProperty("dealnos");
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("解析授权支付额度凭证5106报文出现错误！",e);
			throw new ITFEBizException("解析授权支付额度凭证5106报文出现错误！",e);
			
		}
		List VoucherBodyList  = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
//		String AdmDivCode  = "";//行政区划代码
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
		Map<String,BigDecimal> agencyCodeMap = null;
		//预算科目代码list
		ArrayList<String> expFuncCodeList = null; 
		//预算单位代码list
		List agencyCodeList = null;
		
		//String sAgencyCode ="";
		TvGrantpaymsgmainDto maindto  = null;
		List mainDtoList=null; 
		List<TvGrantpaymsgsubDto> subDtoList=null; 
		List lists=new ArrayList();
		List<Object> list=null;
		//凭证序列号
		String mainvou="";
		TvVoucherinfoDto vDto=new TvVoucherinfoDto();
		String PayAcctNo="";//付款人账号
		String PayAcctName="";//付款人名称
		String PayeeAcctNo="";//收款人账号
		String PayeeAcctName="";//收款人名称
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
			String Id = elementVoucher.elementText("Id");//财政授权支付汇总清算额度通知单Id
			//String VoucherNo  = elementVoucher.elementText("VoucherNo");//凭证号
			String VouDate = elementVoucher.elementText("VouDate"); //凭证日期
			String TreCode = elementVoucher.elementText("TreCode"); //国库主体代码
			String FinOrgCode = elementVoucher.elementText("FinOrgCode");//财政机关代码
			String PlanAmtSum = elementVoucher.elementText("PlanAmt");//合计清算额度金额
			String ClearVoucherNo = "";//商行申请划款单号
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0){
			     ClearVoucherNo = elementVoucher.elementText("ClearVoucherNo");//商行申请划款单号,5106主单.ClearVoucherNo=2301主单.VoucherNo(5351主单.VoucherNo)
			}
			if(ITFECommonConstant.PUBLICPARAM.contains(",quotapayinfo,"))
			{
				PayAcctNo=elementVoucher.elementText("PayAcctNo");//付款人账号
				PayAcctName=elementVoucher.elementText("PayAcctName");//付款人名称
				PayeeAcctNo=elementVoucher.elementText("PayeeAcctNo");//收款人账号
				PayeeAcctName=elementVoucher.elementText("PayeeAcctName");//收款人名称
			}
			String SetMonth = elementVoucher.elementText("SetMonth");//计划月份
			String DeptNum = elementVoucher.elementText("DeptNum");//一级预算单位数量
			String FundTypeCode = elementVoucher.elementText("FundTypeCode");//资金性质编码
			String FundTypeName = elementVoucher.elementText("FundTypeName");//资金性质名称
			String ClearBankCode = elementVoucher.elementText("ClearBankCode");//人民银行编码
			String ClearBankName = elementVoucher.elementText("ClearBankName");//人民银行名称
			String PayBankCode = elementVoucher.elementText("PayBankCode");//代理银行编码
			String PayBankName = elementVoucher.elementText("PayBankName");//代理银行名称
			String PayBankNo = elementVoucher.elementText("PayBankNo");//代理银行行号
			String Hold1=elementVoucher.elementText("Hold1");//预留字段1
			String Hold2=elementVoucher.elementText("Hold2");//预留字段2
			
			/**
			 * 组装TvGrantpaymsgmainDto对象
			 **/
			mainDtoList = new ArrayList();
			maindto  = new TvGrantpaymsgmainDto();
			mainvou = dealnos.get(VoucherNo).toString();
	        maindto.setIvousrlno(Long.valueOf(Long.parseLong(mainvou)));
			maindto.setNmoney(BigDecimal.valueOf(Double.valueOf(PlanAmtSum)));
			maindto.setSaccdate(currentDate);
			maindto.setSamttype(MsgConstant.AMT_KIND_BANK);
			if("000073100012".equals(ITFECommonConstant.SRC_NODE)){//湖南资金性质作为预算种类
				maindto.setSbudgettype(FundTypeCode);
			}else{
				if(ITFECommonConstant.PUBLICPARAM.contains(",quotabudgettype,"))
				{
					TsInfoconnorgaccPK tmppk = new TsInfoconnorgaccPK();
					tmppk.setSorgcode(ls_OrgCode); // 核算主体
					tmppk.setStrecode(TreCode); // TreCode国库主体
					tmppk.setSpayeraccount(PayAcctNo);// 付款账号
					TsInfoconnorgaccDto resultdto = null;
					try {
						resultdto = (TsInfoconnorgaccDto) DatabaseFacade.getDb().find(tmppk);
						if(resultdto!=null&&resultdto.getSbiztype()!=null&&!"".equals(resultdto.getSbiztype()))
						{
							maindto.setSbudgettype(resultdto.getSbiztype());
						}
						else
						{
							if("1".equals(FundTypeCode))
							{
								maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);
							}else
							{
								maindto.setSbudgettype(MsgConstant.BDG_KIND_OUT);
							}
						}
					} catch (JAFDatabaseException e) {
						if("1".equals(FundTypeCode))
						{
							maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);
						}else
						{
							maindto.setSbudgettype(MsgConstant.BDG_KIND_OUT);
						}
					}
				}else
				{
					maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);
				}
			}
			maindto.setSbudgetunitcode("");
			maindto.setSclearbankcode(ClearBankCode);
			maindto.setSclearbankname(ClearBankName);
			maindto.setScommitdate(currentDate);
			maindto.setSdealno(mainvou.substring(8, 16));
			//maindto.setSdemo(_sdemo);
			maindto.setSdeptnum(DeptNum);
			maindto.setSfilename(ls_FileName);
			maindto.setSfundtypecode(FundTypeCode);
			maindto.setSfundtypename(FundTypeName);
			maindto.setSgenticketdate(VouDate);
			maindto.setShold1(Hold1);
			maindto.setShold2(Hold2);
			maindto.setSid(Id);
			maindto.setSlimitid("0");
			maindto.setSofmonth(SetMonth);
			maindto.setSofyear(StYear);
			maindto.setSorgcode(ls_OrgCode);
			maindto.setSpackageno("0000");
			maindto.setSpackageticketno(VoucherNo);
			maindto.setSpaybankcode(PayBankCode);
			maindto.setSpaybankname(PayBankName);
			maindto.setSpaybankno(PayBankNo);
			maindto.setSpayunit(FinOrgCode);
			maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);
			maindto.setStransactunit(PayBankNo);
			//maindto.setStransbankcode(_stransbankcode);
			maindto.setStrecode(TreCode);
			maindto.setSusercode("0000");
			//maindto.setSxacctdate(_sxacctdate);
			maindto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0){
				maindto.setSclearvoucherno(ClearVoucherNo);
			}
			//如果是上海特色，并且主单金额为零，那么设置零额度标志，因为不同预算单位代码要分包
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0&&maindto.getNmoney().compareTo(BigDecimal.ZERO)==0){
				//占用字段S_HOLD1作为零额度标志，并赋予默认值5351
				maindto.setShold1(MsgConstant.VOUCHER_NO_5351);
			}
			//预算单位代码list
			agencyCodeMap = new HashMap();
			//预算科目代码list
			expFuncCodeList = new ArrayList<String>(); 
			//明细合计金额
			
			agencyCodeList = new ArrayList<String>();
			/**
			 * 组装TvPayoutmsgsubDto对象
			 */
			subDtoList=new ArrayList<TvGrantpaymsgsubDto>();
			String money=null;
			for(int j=0;j<listDetail.size();j++){
		
				Element elementDetail  = (Element)listDetail.get(j);
				Element sdetailIdElement=elementDetail.element("Id");
				String SubId = "";
				//节点不存在
				if(sdetailIdElement!=null)
					SubId = elementDetail.elementText("Id");//明细ID				
				
				String SupDepCode = elementDetail.elementText("SupDepCode");//一级预算单位编码
				String SupDepName = elementDetail.elementText("SupDepName");//一级预算单位名称
				if(new String(SupDepName.getBytes("GBK"),"iso-8859-1").length()>60)
					SupDepName = CommonUtil.subString(SupDepName,60);
				String ExpFuncCode = elementDetail.elementText("ExpFuncCode");//支出功能分类科目编码
				String ExpFuncName = elementDetail.elementText("ExpFuncName");//支出功能分类科目名称
				String ExpFuncCode1 = elementDetail.elementText("ExpFuncCode1");//支出功能分类科目类编码
				String ExpFuncName1 = elementDetail.elementText("ExpFuncName1");//支出功能分类科目类名称
				String ExpFuncCode2 = elementDetail.elementText("ExpFuncCode2");//支出功能分类科目款编码
				String ExpFuncName2 = elementDetail.elementText("ExpFuncName2");//支出功能分类科目款名称
				String ExpFuncCode3 = elementDetail.elementText("ExpFuncCode3");//支出功能分类科目项编码
				String ExpFuncName3 = elementDetail.elementText("ExpFuncName3");//支出功能分类科目项名称
				String ProCatCode = elementDetail.elementText("ProCatCode");//收支管理编码
				String ProCatName = elementDetail.elementText("ProCatName");//收支管理名称
				String PlanAmt = elementDetail.elementText("PlanAmt");//支付金额
				String sdetailHold1 = elementDetail.elementText("Hold1");//预留字段1,作为累计下达额度(上海扩展)
				String sdetailHold2 = elementDetail.elementText("Hold2");//预留字段2
				String sdetailHold3 = elementDetail.elementText("Hold3");//预留字段3
				String sdetailHold4 = elementDetail.elementText("Hold4");//预留字段4
				
				/****************广州新增字段修改20161020****************/
				String FundTypeCodeSub = elementDetail.elementText("FundTypeCode");//	资金性质编码
				String FundTypeNameSub = elementDetail.elementText("FundTypeName");//	资金性质名称
				String AgencyCode = elementDetail.elementText("AgencyCode");//基层预算单位编码
				String AgencyName = elementDetail.elementText("AgencyName");//基层预算单位名称
				String ExpEcoCode = elementDetail.elementText("ExpEcoCode");//经济分类科目编码
				String ExpEcoName = elementDetail.elementText("ExpEcoName");//经济分类科目名称
				String DepProCode = elementDetail.elementText("DepProCode");//预算项目编码
				String DepProName = elementDetail.elementText("DepProName");//预算项目名称
				String AgencyAccoCode = elementDetail.elementText("AgencyAccoCode");//	单位零余额账户
				String AgencyBankName = elementDetail.elementText("AgencyBankName");//	单位零余额帐户开户行
				/****************广州新增字段修改20161020****************/
				
				TvGrantpaymsgsubDto subdto=new TvGrantpaymsgsubDto();
				//此处的设值待确认
				subdto.setIdetailseqno(j+1);
				subdto.setIvousrlno(Long.parseLong(mainvou));
				subdto.setNmoney(BigDecimal.valueOf(Double.valueOf(PlanAmt)));
				subdto.setSaccattrib(MsgConstant.ACCT_PROP_ZERO);
				subdto.setSaccdate(currentDate);//账务日期
				subdto.setSbudgettype(maindto.getSbudgettype());
				subdto.setSbudgetunitcode(SupDepCode);
				subdto.setSdealno(mainvou.substring(8, 16));
				//subdto.setSdemo(_sdemo);
				//subdto.setSecosubjectcode(_secosubjectcode);
				subdto.setSexpfuncname(ExpFuncName);//支出功能分类科目名称
				subdto.setSexpfunccode1(ExpFuncCode1);//支出功能分类科目类编码
				subdto.setSexpfuncname1(ExpFuncName1);//支出功能分类科目类名称
				subdto.setSexpfunccode2(ExpFuncCode2);//支出功能分类科目款编码
				subdto.setSexpfuncname2(ExpFuncName2);//支出功能分类科目款名称
				subdto.setSexpfunccode3(ExpFuncCode3);//支出功能分类科目项编码
				subdto.setSexpfuncname3(ExpFuncName3);//支出功能分类科目项名称
				subdto.setSfilename(ls_FileName);
				subdto.setSfunsubjectcode(ExpFuncCode);
				subdto.setShold1(sdetailHold1);//预留字段1
				subdto.setShold2(sdetailHold2);//预留字段2
				subdto.setShold3(sdetailHold3);//预留字段3
				subdto.setShold4(sdetailHold4);//预留字段4
				subdto.setSlimitid("0");
				subdto.setSline("1");
				subdto.setSofyear(StYear);
				subdto.setSorgcode(ls_OrgCode);
				subdto.setSpackageno("0000");
				subdto.setSpackageticketno(VoucherNo);
				subdto.setSprocatcode(ProCatCode);//收支管理编码
				subdto.setSprocatname(ProCatName);//收支管理名称
				subdto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);
				subdto.setSsupdepname(SupDepName);
				subdto.setSusercode("0000");
				subdto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));
				subdto.setSfunsubjectcode(ExpFuncCode);//支出功能分类科目编码
				BigDecimal subPayAmt = new BigDecimal(PlanAmt);
				subdto.setNmoney(subPayAmt);//支付金额
				if(ITFECommonConstant.PUBLICPARAM.indexOf(",beijing,")<0){
					subdto.setSid(SubId);
				}
				//设置预算单位及金额统计
				if(subdto.getNmoney().signum()==-1)
					money="-";
				else
					money="+";
				if(agencyCodeMap.containsKey(subdto.getSbudgetunitcode()+money)){
					BigDecimal lbigD  = agencyCodeMap.get(subdto.getSbudgetunitcode()+money).add(subdto.getNmoney());
					agencyCodeMap.put(subdto.getSbudgetunitcode()+money,lbigD);
				}else{
					agencyCodeMap.put(subdto.getSbudgetunitcode()+money, subdto.getNmoney());
				}
				
				/****************广州新增字段修改20161020****************/
				//暂时通过判断新增必填字段是否有值来确定填充字段的来源，后期可以通过Area或者节点号等方式来判断
				if(StringUtils.isNotBlank(FundTypeCodeSub) && StringUtils.isNotBlank(FundTypeNameSub)) {
					subdto.setSfundtypecode(FundTypeCodeSub); //资金性质编码
					subdto.setSfundtypename(FundTypeNameSub); //资金性质名称
					subdto.setSagencycode(AgencyCode); //基层预算单位编码
					subdto.setSagencyname(AgencyName); //基层预算单位名称
					subdto.setSexpecocode(ExpEcoCode); //经济分类科目编码
					subdto.setSexpeconame(ExpEcoName); //经济分类科目名称
					subdto.setSdepprocode(DepProCode); //预算项目编码
					subdto.setSdepproname(DepProName); //预算项目名称
					subdto.setSagencyaccocode(AgencyAccoCode); //单位零余额账户
					subdto.setSagencybankname(AgencyBankName); //单位零余额帐户开户行	
				}
				
				agencyCodeList.add(TreCode+SupDepCode);
				expFuncCodeList.add(ExpFuncCode);
				
				sumAmt = sumAmt.add(subPayAmt); 
				subDtoList.add(subdto);
			}
			String ls_VoucherEnd = "";
			
			//根据交易流水号取得凭证索引表dto
			vDto=new TvVoucherinfoDto();
			vDto.setSdealno(mainvou);
			vDto=(TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
			
			int k = 0;
			if(agencyCodeMap.size()>1){
				k = 0;
				Set subSet = agencyCodeMap.entrySet();
				if(subSet!=null&&subSet.size()>1000)
				{
					String errMsg = "预算单位个数必须小于1000!";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				for(Iterator subIt = subSet.iterator(); subIt.hasNext();){
					ls_VoucherEnd = "000";
					ls_VoucherEnd = ls_VoucherEnd + k;
					ls_VoucherEnd = ls_VoucherEnd.substring(ls_VoucherEnd.length()-3, ls_VoucherEnd.length());
					Map.Entry<String, BigDecimal> subEntry = (Map.Entry<String, BigDecimal>) subIt.next();   
					String ls_budgetunitcode = subEntry.getKey();
					BigDecimal  sumSubMoney = subEntry.getValue();
					
					TvGrantpaymsgmainDto newTvGrantpaymsgmainDto = (TvGrantpaymsgmainDto) maindto.clone();
					String TraNo = VoucherUtil.getGrantSequence();  
					//凭证流水号
					String ls_VoucherNo  = maindto.getSpackageticketno()+ls_VoucherEnd;
					newTvGrantpaymsgmainDto.setSpackageticketno(ls_VoucherNo);
					newTvGrantpaymsgmainDto.setNmoney(sumSubMoney);
					if(ls_budgetunitcode.endsWith("-")||ls_budgetunitcode.endsWith("+"))
						newTvGrantpaymsgmainDto.setSbudgetunitcode(ls_budgetunitcode.substring(0,ls_budgetunitcode.length()-1));
					else
						newTvGrantpaymsgmainDto.setSbudgetunitcode(ls_budgetunitcode);
					newTvGrantpaymsgmainDto.setSdealno(TraNo.substring(8, 16));
					mainDtoList.add(newTvGrantpaymsgmainDto);
					int sum = 0;
					for(TvGrantpaymsgsubDto subDto:subDtoList){
						if(subDto.getNmoney().signum()==-1)
							money="-";
						else
							money="+";
						if((subDto.getSbudgetunitcode()+money).equals(ls_budgetunitcode)){
							subDto.setSpackageticketno(ls_VoucherNo);
							subDto.setIvousrlno(newTvGrantpaymsgmainDto.getIvousrlno());
							sum++;
						}
					}
					/**
					 *明细条数不能超过500
					 * 
					 */
					if (sum > 499) {
						String errMsg = "每个预算单位明细条数必须小于500!";
						voucher.voucherComfail(vDto.getSdealno(), errMsg);
						continue;
					}
					k++;
				}
			}else{
				/**
				 *明细条数不能超过500
				 * 
				 */
				if (subDtoList.size() > 499) {
					String errMsg = "每个预算单位明细条数必须小于500!";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				maindto.setSbudgetunitcode(subDtoList.get(0).getSbudgetunitcode());
				mainDtoList.add(maindto);
			}
			
			/**
			 * 组装verifydto,进行报文校验
			 */
			verifydto.setTrecode(TreCode);
			verifydto.setFinorgcode(FinOrgCode);
			verifydto.setVoucherno(VoucherNo);
			verifydto.setVoudate(VouDate);
			verifydto.setPaybankno(PayBankNo);
			//增加年度、月份，总金额的校验 by renqingbin
			verifydto.setOfyear(StYear);
			verifydto.setOfmonth(SetMonth);
			verifydto.setFamt(PlanAmtSum);
			verifydto.setPayVoucherNo(ClearVoucherNo);
			String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_5106);
			if(returnmsg != null){//返回错误信息签收失败
				voucher.voucherComfail(vDto.getSdealno(), returnmsg);
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
			 *校验凭证编号不能大于17位
			 *
			 */
			if(maindto.getSpackageticketno().getBytes().length>17){
				String errMsg="凭证编号不能大于17位！";
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
			}
			
			/**
			 *校验主表金额=0.00且明细条数=1 
			 *
			 */
			if(maindto.getNmoney().compareTo(BigDecimal.ZERO) == 0 && subDtoList.size()==1){
				String errMsg="主单金额："+maindto.getNmoney()+" ,明细条数：1";
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
			}
		
			/**
			 * 业务表入库，校验报文合法性
			 * 
			 * 业务主表，子表入库失败即签收失败
			 */
			TvGrantpaymsgmainDto[] mainDtos=new TvGrantpaymsgmainDto[mainDtoList.size()];
			mainDtos=(TvGrantpaymsgmainDto[]) mainDtoList.toArray(mainDtos);
			TvGrantpaymsgsubDto[] subDtos=new TvGrantpaymsgsubDto[subDtoList.size()];
			subDtos=(TvGrantpaymsgsubDto[]) subDtoList.toArray(subDtos);
			
			DatabaseFacade.getODB().create(mainDtos);
			DatabaseFacade.getODB().create(subDtos);
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
				continue;
			}
			
			if(ITFECommonConstant.PUBLICPARAM.contains(",quotapayinfo,"))
			{
				maindto.setSclearbankcode(PayAcctNo) ;//付款人账号
				maindto.setSclearbankname(PayAcctName) ;//付款人名称
				maindto.setShold1(PayeeAcctNo) ;//收款人账号
				maindto.setShold2(PayeeAcctName) ;//收款人名称
			}
			list=new ArrayList<Object>();
			list.add(maindto);
			list.add(vDto);			
			list.add(expFuncCodeList);
			list.add(agencyCodeList);
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0){
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
		}catch(Exception e){
			logger.error(e);
			VoucherException voucherE  = new VoucherException();
			voucherE.saveErrInfo(VtCode, e);
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

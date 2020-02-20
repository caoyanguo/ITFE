package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsConvertbanknameDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainSxDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubSxDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoSxDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.sxservice.VoucherVerifySX;
import com.cfcc.itfe.xmlparse.AbstractXmlParser;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;


public class VoucherSX5207MsgServer extends AbstractXmlParser {

	private static Log logger = LogFactory.getLog(VoucherSX5207MsgServer.class);
	private Voucher voucher;

	/**
	 * * 报文解析处理类
	 * @param map  关键信息MAP
	 * 		  key：【orgcode】-value:【String】机构代码
	 * 		  key：【filename】-value:【String】 文件名
	 *        key: 【dealnomap】-value:【HashMap<String,String>】交易流水号MAP   
	 * @param xmlString 报文信息
	 */
	public void dealMsg(HashMap map, String xmlString) throws ITFEBizException {

		String ls_FileName = (String) map.get("filename");
		String ls_OrgCode = (String) map.get("orgcode");
		HashMap<String,String> dealnos=(HashMap<String,String>)map.get("dealnomap");
		
		String voucherXml= xmlString;
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
			logger.error("解析实拨拨款凭证5207报文出现错误！",e);
			throw new ITFEBizException("解析实拨拨款凭证5207报文出现错误！",e);
			
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
		TvPayoutmsgmainSxDto maindto  = null;
		List subDtoList=null; 
		List lists=new ArrayList();
		List list=null;
		//凭证序列号
		String mainvou="";
		TvVoucherinfoSxDto vDto=new TvVoucherinfoSxDto();
		//解析报文
		for(int i=0;i<VoucherBodyList.size();i++){
			try{
				//明细合计金额
			BigDecimal sumAmt = new BigDecimal("0.00");	
			//VoucherBody
			Element element  = (Element)VoucherBodyList.get(i);
			//Voucher
			Element elementVoucher  = (Element)element.selectSingleNode("Voucher");
			//明细信息List
			List listDetail  = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");
			//报文校验信息dto
			 VoucherVerifyDto verifydto = new VoucherVerifyDto();
			 VoucherVerifySX voucherVerifySX = new VoucherVerifySX();	
			/**
			 * 凭证信息
			 */
			String Attach = ((Element) VoucherBodyList.get(i)).elementText("Attach");//附加信息
			String Id = elementVoucher.elementText("Id");//实拨拨款凭证Id
			String VoucherNo  = elementVoucher.elementText("VoucherNo");//凭证号
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
			String PaySummaryCode = elementVoucher.elementText("PaySummaryCode");//用途编码
			String PaySummaryName = elementVoucher.elementText("PaySummaryName"); //用途名称
			String PayAmt = elementVoucher.elementText("PayAmt");//拨款金额
			String XAgentBusinessNo = elementVoucher.elementText("XAgentBusinessNo");//付款人银行
			String Hold1=elementVoucher.elementText("Hold1");//预留字段1
			String Hold2=elementVoucher.elementText("Hold2");//预留字段2
			
			/**
			 * 组装TvPayoutmsgmainDto对象
			 **/
			maindto  = new TvPayoutmsgmainSxDto();
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
						maindto.setScheckstatus(StateConstant.CHECKSTATUS_1);//已补录
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
			maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);//状态
			maindto.setSusercode("0000");//用户代码
			maindto.setSaddword("");//附言
			maindto.setSbackflag(StateConstant.MSG_BACK_FLAG_NO);// 退款标志
			maindto.setSid(Id); //实拨拨款凭证Id
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
			maindto.setShold2(Hold2);//保留字段2
			maindto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//系统时间
			maindto.setSxagentbusinessno(XAgentBusinessNo);
			//预算单位代码list
			agencyCodeList = new HashSet();
			//预算科目代码list
			expFuncCodeList = new ArrayList<String>(); 
			/**
			 * 组装TvPayoutmsgsubDto对象
			 */
			subDtoList=new ArrayList<TvPayoutmsgsubSxDto>();
			for(int j=0;j<listDetail.size();j++){
				
				//明细合计金额
				Element elementDetail  = (Element)listDetail.get(j);
				String sId=(j+1)+"";//拨款明细编号
				String sVoucherBillId=elementDetail.elementText("VoucherBillId");//拨款凭证Id
				String BgtTypeCode = elementDetail.elementText("BgtTypeCode");//预算类型编码
				String BgtTypeName = elementDetail.elementText("BgtTypeName");//预算类型名称
				String ProCatCode = elementDetail.elementText("ProCatCode");//收支管理编码
				String ProCatName = elementDetail.elementText("ProCatName");//收支管理名称
				sAgencyCode=elementDetail.elementText("AgencyCode");//预算单位编码
				String sAgencyName=elementDetail.elementText("AgencyName");//预算单位名称
				String ExpFuncCode = elementDetail.elementText("ExpFuncCode");//支出功能分类科目编码
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
				String sPayAmt = elementDetail.elementText("PayAmt");//支付金额
				String sdetailHold1 = elementDetail.elementText("Hold1");//预留字段1
				String sdetailHold2 = elementDetail.elementText("Hold2");//预留字段2
				String sdetailHold3 = elementDetail.elementText("Hold3");//预留字段3
				String sdetailHold4 = elementDetail.elementText("Hold4");//预留字段4
				String[] AgencyCodeArr=new String[]{"AgencyCode",sAgencyCode,"15"};
				String[] ExpFuncCodeArr=new String[]{"ExpFuncCode",ExpFuncCode,"30"};				
				
				TvPayoutmsgsubSxDto subdto=new TvPayoutmsgsubSxDto();
				//此处的设值待确认
				subdto.setSbizno(mainvou);//子表序列号
				subdto.setSseqno(Integer.valueOf(sId));//子表明细序号
				subdto.setSaccdate(currentDate);//账务日期
				subdto.setSecnomicsubjectcode(ExpEcoCode);//经济科目代码
				subdto.setStaxticketno(sVoucherBillId);//拨款凭证Id
				subdto.setSbgttypecode(BgtTypeCode);//预算类型编码
				subdto.setSbgttypename(BgtTypeName);//预算类型名称
				subdto.setSprocatcode(ProCatCode);//收支管理编码
				subdto.setSprocatname(ProCatName);//收支管理名称
				subdto.setSagencycode(sAgencyCode);//预算单位编码
				subdto.setSagencyname(sAgencyName);//预算单位名称
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
				BigDecimal subPayAmt = new BigDecimal(sPayAmt);
				subdto.setNmoney(subPayAmt);//支付金额
				subdto.setShold1(sdetailHold1);//预留字段1
				subdto.setShold2(sdetailHold2);//预留字段2
				subdto.setShold3(sdetailHold3);//预留字段3
				subdto.setShold4(sdetailHold4);//预留字段4
				//设置预算单位、科目LIST,明细合计
				agencyCodeList.add(sAgencyCode);
				expFuncCodeList.add(ExpFuncCode);
				sumAmt = sumAmt.add(subPayAmt); 
				
				//设置主表预算单位代码
				maindto.setSbudgetunitcode(sAgencyCode);
				maindto.setSunitcodename(sAgencyName);
				subDtoList.add(subdto);
			}
			vDto=new TvVoucherinfoSxDto();
			vDto.setSdealno(mainvou);
			vDto=(TvVoucherinfoSxDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
			
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
			String returnmsg = voucherVerifySX.checkValid(verifydto, MsgConstant.VOUCHER_NO_5207);
			if(returnmsg != null){//返回错误信息签收失败
				voucher.voucherComfailForSX(vDto.getSdealno(), returnmsg);
				continue;
			}
			
			/**
			 *校验主表金额是否与子表金额相等 
			 *
			 */
			if(maindto.getNmoney().compareTo(sumAmt) != 0){
				String errMsg="主单金额与明细累计金额不相等，主单金额："+maindto.getNmoney()+" 明细累计金额： "+sumAmt;
				voucher.voucherComfailForSX(vDto.getSdealno(), errMsg);
				continue;
			}
			/**
			 * 校验子表预算单位代码是否重复
			 * 
			 */
			if(agencyCodeList.size()>1){
				String errMsg="凭证明细中存在多个预算单位信息!";
				voucher.voucherComfailForSX(vDto.getSdealno(), errMsg);
				continue;
			}
		
			/**
			 * 业务表入库，校验报文合法性
			 * 
			 * 业务主表，子表入库失败即签收失败
			 */
			TvPayoutmsgsubSxDto[] subDtos=new TvPayoutmsgsubSxDto[subDtoList.size()];
			subDtos=(TvPayoutmsgsubSxDto[]) subDtoList.toArray(subDtos);
			DatabaseFacade.getODB().create(maindto);
			DatabaseFacade.getODB().create(subDtos);
			}catch(JAFDatabaseException e){
				logger.error(e);
				voucher.voucherComfailForSX(mainvou, "保存数据库出现错误："+e.getMessage());
				continue;	
			}catch(Exception e){
				logger.error(e);
				voucher.voucherComfailForSX(mainvou, "报文不规范："+e.getMessage());
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
				voucher.voucherVerifyForSX(lists, VtCode);
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

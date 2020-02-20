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
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.persistence.pk.TsInfoconnorgaccPK;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

@SuppressWarnings({ "static-access", "unchecked" })
public class Voucher5108MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher5108MsgServer.class);
	private Voucher voucher;
	/**
	 * 财政发往人行的直接支付额度凭证5108，此类只保存业务数据
	 */
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
			logger.error("解析直接支付额度凭证5108报文出现错误！",e);
			throw new ITFEBizException("解析直接支付额度凭证5108报文出现错误！",e);
			
		}
		List VoucherBodyList  = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
		String StYear  = "";//年度
		String VtCode  = "";//凭证类型
		
		String currentDate = TimeFacade.getCurrentStringTime();// 当前系统日期
//		List<String> voucherList = new ArrayList<String>();
		
		//获取行政区划代码、年度和凭证类型
		if(VoucherBodyList.size()>0){
			Element element  = (Element)VoucherBodyList.get(0);
			StYear  = element.attribute("StYear").getText();
			VtCode  = element.attribute("VtCode").getText();
		}
		
		//预算单位代码list
		List agencyCodeList = null;
		//预算科目代码list
		ArrayList<String> expFuncCodeList = null; 
		
		//String sAgencyCode ="";
		TvDirectpaymsgmainDto maindto  = null;
		List subDtoList=null; 
		List lists=new ArrayList();
		List list=null;
		//凭证序列号
		String mainvou="";
		TvVoucherinfoDto vDto=new TvVoucherinfoDto();
//		List<TvAmtControlInfoDto> amtList  = null;
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
			String Id = elementVoucher.elementText("Id");//财政直接支付汇总清算额度通知单Id
			//String VoucherNo  = elementVoucher.elementText("VoucherNo");//凭证号
			String VouDate = elementVoucher.elementText("VouDate"); //凭证日期
			String TreCode = elementVoucher.elementText("TreCode"); //国库主体代码
			String FinOrgCode = elementVoucher.elementText("FinOrgCode");//财政机关代码
			String PayAmtSum = elementVoucher.elementText("PayAmt");//合计清算额度金额
			String FundTypeCode = elementVoucher.elementText("FundTypeCode");//资金性质编码
			String FundTypeName = elementVoucher.elementText("FundTypeName");//资金性质名称
			String PayeeAcctNo = elementVoucher.elementText("PayeeAcctNo");//收款人账号
			String PayeeAcctName = elementVoucher.elementText("PayeeAcctName");//收款人名称
			String PayeeAcctBankName = elementVoucher.elementText("PayeeAcctBankName");//收款人银行名称
			String PayAcctNo = elementVoucher.elementText("PayAcctNo");//付款人账号
			String PayAcctName = elementVoucher.elementText("PayAcctName");//付款人名称
			String PayAcctBankName = elementVoucher.elementText("PayAcctBankName");//付款人银行名称
			String PayBankCode = elementVoucher.elementText("PayBankCode");//代理银行编码
			String PayBankName = elementVoucher.elementText("PayBankName");//代理银行名称
			String PayBankNo = elementVoucher.elementText("PayBankNo");//代理银行行号
			String Hold1=elementVoucher.elementText("Hold1");//预留字段1
			String Hold2=elementVoucher.elementText("Hold2");//预留字段2
			
			/**
			 * 组装TvDirectpayfilemainDto对象
			 **/
			maindto  = new TvDirectpaymsgmainDto();
			mainvou =dealnos.get(VoucherNo);//获取序列号
			maindto.setIvousrlno(Long.parseLong(mainvou));
			maindto.setNmoney(BigDecimal.valueOf(Double.valueOf(PayAmtSum)));
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
			maindto.setScommitdate(currentDate);
			maindto.setSdealno(mainvou.substring(8, 16));
			//maindto.setSdemo(_sdemo);
			maindto.setSfilename(ls_FileName);
			maindto.setSfundtypecode(FundTypeCode);
			maindto.setSfundtypename(FundTypeName);
			maindto.setSgenticketdate(VouDate);
			maindto.setShold1(Hold1);
			maindto.setShold2(Hold2);
			maindto.setSid(Id);
			maindto.setSofyear(StYear);
			maindto.setSorgcode(ls_OrgCode);
			maindto.setSpackageno("0000");
			maindto.setSpackageticketno(VoucherNo);
			maindto.setSpayacctbankname(PayAcctBankName);
			maindto.setSpayacctname(PayAcctName);
			maindto.setSpayacctno(PayAcctNo);
			maindto.setSpaybankcode(PayBankCode);
			maindto.setSpaybankname(PayBankName);
			maindto.setSpaybankno(PayBankNo);
			maindto.setSpayeeacctbankname(PayeeAcctBankName);
			maindto.setSpayeeacctname(PayeeAcctName);
			maindto.setSpayeeacctno(PayeeAcctNo);
			maindto.setSpayunit(FinOrgCode);
			maindto.setShold2(FinOrgCode);
			vDto=new TvVoucherinfoDto();
			vDto.setSdealno(mainvou);
			vDto=(TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
			
			
			/**上海无纸化
			 * 主单批量标志Hold1
			 * 支付凭证号（调整凭证号）PayVoucherNo     
			 */
			verifydto.setFundTypeCode(FundTypeCode);
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0){
				if(Hold1==null||(Hold1!=null&&Hold1.equals("0"))){
					vDto.setSpaybankcode(PayBankNo);//单笔清算额度：经办单位取国库代码
					maindto.setStransactunit(TreCode);
					maindto.setSamttype(MsgConstant.AMT_KIND_PEOPLE);
					verifydto.setBusinessTypeCode("0");	//单笔清算
//					vDto.setScheckvouchertype(null);
//					vDto.setShold3(null);
				}					
				else if(Hold1!=null&&Hold1.equals("1")){
					vDto.setSpaybankcode(PayBankNo);//批量清算额度：经办单位取代理银行代码
					maindto.setStransactunit(PayBankNo);
				}
					
				else{
					String errMsg="主单批量标志["+Hold1+"]错，只能为0-单笔清算额度 	1-批量清算额度!";
					voucher.voucherComfail(mainvou, errMsg);
					continue;
				}
				vDto.setShold1(Hold1);	//单笔和批量标识
				String PayVoucherNo=elementVoucher.elementText("PayVoucherNo");//支付凭证号（调整凭证号）
				maindto.setSpayvoucherno(PayVoucherNo);
			}
			
			maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);
			maindto.setStaxticketno(VoucherNo);
			maindto.setStransactunit(PayBankNo);
			maindto.setStransbankcode("");
			maindto.setStrecode(TreCode);
			maindto.setSusercode("0000");
			maindto.setSxacctdate("");
			maindto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//系统时间
			//预算单位代码list
			agencyCodeList = new ArrayList<String>();
			//预算科目代码list
			expFuncCodeList = new ArrayList<String>(); 
			//明细合计金额
			subDtoList=new ArrayList<TvDirectpaymsgsubDto>();
//			amtList = new ArrayList<TvAmtControlInfoDto>();
			//子表明细Id集合
			List<String>  subDtoIdList = new ArrayList<String>();
			String sdetailId = null;//明细Id
			
			/**
			 * 组装TvPayoutmsgsubDto对象
			 */
			for(int j=0;j<listDetail.size();j++){
				
				Element elementDetail  = (Element)listDetail.get(j);
				/**
				 * 校验报文明细Id节点是否存在
				 * 1、若节点不存在，是老版本，不更新明细Id
				 * 2、若节点存在，是新版本，更新明细Id
				 */
				Element sdetailIdElement=elementDetail.element("Id");
				//节点不存在
				if(sdetailIdElement==null)
					subDtoIdList.add("节点不存在");					
				else
					sdetailId = sdetailIdElement.getText();//明细Id
									
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
				String PayAmt = elementDetail.elementText("PayAmt");//支付金额
				String sdetailHold1 = elementDetail.elementText("Hold1");//预留字段1
				String sdetailHold2 = elementDetail.elementText("Hold2");//预留字段2
				String sdetailHold3 = elementDetail.elementText("Hold3");//预留字段3
				String sdetailHold4 = elementDetail.elementText("Hold4");//预留字段4
				
				/****************广州新增字段修改20161020****************/
				String DirectVoucherNo = elementDetail.elementText("DirectVoucherNo"); //支付申请序号         
				String FundTypeCodeSub = elementDetail.elementText("FundTypeCode"); //资金性质编码        
				String FundTypeNameSub = elementDetail.elementText("FundTypeName"); //资金性质名称        
				String AgencyCode = elementDetail.elementText("AgencyCode"); //基层预算单位编码    
				String AgencyName = elementDetail.elementText("AgencyName"); //基层预算单位名称    
				String ExpEcoCode = elementDetail.elementText("ExpEcoCode"); //支出经济分类科目编码
				String ExpEcoName = elementDetail.elementText("ExpEcoName"); //支出经济分类科目名称
				String PayeeAcctNoSub = elementDetail.elementText("PayeeAcctNo"); //收款人账号          
				String PayeeAcctNameSub = elementDetail.elementText("PayeeAcctName"); //收款人名称          
				String PayeeAcctBankNameSub = elementDetail.elementText("PayeeAcctBankName"); //收款人银行  
				/****************广州新增字段修改20161020****************/
				
				TvDirectpaymsgsubDto subdto=new TvDirectpaymsgsubDto();
				subdto.setIdetailseqno(j+1);
				//subdto.setInoafterpackage(_inoafterpackage);
				subdto.setIvousrlno(Long.parseLong(mainvou));
				subdto.setNmoney(BigDecimal.valueOf(Double.valueOf(PayAmt)));
				subdto.setSid(sdetailId);
				subdto.setSaccdate(currentDate);
				subdto.setSagencycode(SupDepCode);
				subdto.setSagencyname(SupDepName);
				subdto.setSbudgetunitcode(SupDepCode);
				//subdto.setSdemo(_sdemo);
				//subdto.setSecosubjectcode(_secosubjectcode);
				subdto.setSexpfunccode1(ExpFuncCode1);
				subdto.setSexpfunccode2(ExpFuncCode2);
				subdto.setSexpfunccode3(ExpFuncCode3);
				subdto.setSexpfuncname(ExpFuncName);
				subdto.setSexpfuncname1(ExpFuncName1);
				subdto.setSexpfuncname2(ExpFuncName2);
				subdto.setSexpfuncname3(ExpFuncName3);
				subdto.setSfunsubjectcode(ExpFuncCode);
				subdto.setShold1(sdetailHold1);
				subdto.setShold2(sdetailHold2);
				subdto.setShold3(sdetailHold3);
				subdto.setShold4(sdetailHold4);
				subdto.setSline((j+1)+"");
				subdto.setSofyear(StYear);
				subdto.setSorgcode(ls_OrgCode);
				subdto.setSpackageno("");
				subdto.setSprocatcode(ProCatCode);
				subdto.setSprocatname(ProCatName);
				subdto.setStaxticketno(VoucherNo);
				subdto.setSusercode("0000");
				
				/****************广州新增字段修改20161020****************/
				//暂时通过判断新增必填字段是否有值来确定填充字段的来源，后期可以通过Area或者节点号等方式来判断
				if(StringUtils.isNotBlank(DirectVoucherNo) && StringUtils.isNotBlank(FundTypeCodeSub)) {
					subdto.setSdirectvoucherno(DirectVoucherNo); //支付申请序号
					subdto.setSfundtypecode(FundTypeCodeSub); //资金性质编码
					subdto.setSfundtypename(FundTypeNameSub); //资金性质名称
					if(AgencyCode != null && !"".equals(AgencyCode)) {
						subdto.setSsubagencycode(AgencyCode); //基层预算单位编码
					}
					if(AgencyName != null && !"".equals(AgencyName)) {
						subdto.setSsubagencyname(AgencyName); //基层预算单位名称
					}
					subdto.setSexpecocode(ExpEcoCode); //支出经济分类科目编码
					subdto.setSexpeconame(ExpEcoName); //支出经济分类科目名称
					subdto.setSpayeeacctno(PayeeAcctNoSub); //收款人账号
					subdto.setSpayeeacctname(PayeeAcctNameSub); //收款人名称
					subdto.setSpayeeacctbankname(PayeeAcctBankNameSub); //收款人银行
				}
				/****************广州新增字段修改20161020****************/
				
				agencyCodeList.add(TreCode+SupDepCode);
				expFuncCodeList.add(ExpFuncCode);
				sumAmt = sumAmt.add(BigDecimal.valueOf(Double.valueOf(PayAmt))); 
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
			verifydto.setTrecode(TreCode);
			verifydto.setFinorgcode(FinOrgCode);
			verifydto.setVoucherno(VoucherNo);
			verifydto.setVoudate(VouDate);
			verifydto.setPaybankno(PayBankNo);
			//增加年度，总金额的校验 by renqingbin
			verifydto.setOfyear(StYear);
			verifydto.setFamt(PayAmtSum);
			//上海无纸化增加  支付凭证号（调整凭证号）
			verifydto.setPayVoucherNo(maindto.getSpayvoucherno());
			String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_5108);
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
			 *校验主表金额=0.00且明细条数=1 
			 *
			 */
			if(maindto.getNmoney().compareTo(BigDecimal.ZERO) == 0 && subDtoIdList.size()==1){
				String errMsg="主单金额："+maindto.getNmoney()+" ,明细条数：1";
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
			}
			
			/**
			 *明细条数不能超过500
			 * 佛山需要判断超过500条，广东其他地方不需要判断 2301同
			 */
			if ((!TreCode.startsWith("19") || TreCode.startsWith("1906")) && subDtoList.size() > 499) {
				String errMsg = "明细条数必须小于500!";
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
			}
			
			/**
			 * 业务表入库，校验报文合法性
			 * 
			 * 业务主表，子表入库失败即签收失败
			 */
			TvDirectpaymsgsubDto[] subDtos=new TvDirectpaymsgsubDto[subDtoList.size()];
			subDtos=(TvDirectpaymsgsubDto[]) subDtoList.toArray(subDtos);
			DatabaseFacade.getODB().create(maindto);
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
			
			list=new ArrayList();
			list.add(maindto);
			list.add(vDto);			
			list.add(expFuncCodeList);
			list.add(agencyCodeList);
			//凭证比对（上海特色）
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

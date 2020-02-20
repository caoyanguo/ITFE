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

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvAmtControlInfoDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainSxDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubSxDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoSxDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.sxservice.VoucherVerifySX;
import com.cfcc.itfe.xmlparse.AbstractXmlParser;


public class VoucherSX5108MsgServer extends AbstractXmlParser {

	private static Log logger = LogFactory.getLog(VoucherSX5108MsgServer.class);
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
		List<String> voucherList = new ArrayList<String>();
		
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
		TvDirectpaymsgmainSxDto maindto  = null;
		List subDtoList=null; 
		List lists=new ArrayList();
		List list=null;
		//凭证序列号
		String mainvou="";
		TvVoucherinfoSxDto vDto=new TvVoucherinfoSxDto();
		List<TvAmtControlInfoDto> amtList  = null;
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
			String Id = elementVoucher.elementText("Id");//财政直接支付汇总清算额度通知单Id
			String VoucherNo  = elementVoucher.elementText("VoucherNo");//凭证号
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
			maindto  = new TvDirectpaymsgmainSxDto();
			mainvou =dealnos.get(VoucherNo);//获取序列号
			maindto.setIvousrlno(Long.parseLong(mainvou));
			maindto.setNmoney(BigDecimal.valueOf(Double.valueOf(PayAmtSum)));
			maindto.setSaccdate(currentDate);
			maindto.setSamttype(MsgConstant.AMT_KIND_BANK);
			maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);
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
			subDtoList=new ArrayList<TvDirectpaymsgsubSxDto>();
			amtList = new ArrayList<TvAmtControlInfoDto>();
			/**
			 * 组装TvPayoutmsgsubDto对象
			 */
			for(int j=0;j<listDetail.size();j++){
				
				Element elementDetail  = (Element)listDetail.get(j);
				String SupDepCode = elementDetail.elementText("SupDepCode");//一级预算单位编码
				String SupDepName = elementDetail.elementText("SupDepName");//一级预算单位名称
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
				TvDirectpaymsgsubSxDto  subdto=new TvDirectpaymsgsubSxDto();
				subdto.setIdetailseqno(j+1);
				//subdto.setInoafterpackage(_inoafterpackage);
				subdto.setIvousrlno(Long.parseLong(mainvou));
				subdto.setNmoney(BigDecimal.valueOf(Double.valueOf(PayAmt)));
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
				agencyCodeList.add(TreCode+SupDepCode);
				expFuncCodeList.add(ExpFuncCode);
				sumAmt = sumAmt.add(BigDecimal.valueOf(Double.valueOf(PayAmt))); 
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
			verifydto.setPaybankno(PayBankNo);
			String returnmsg = voucherVerifySX.checkValid(verifydto, MsgConstant.VOUCHER_NO_5108);
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
			 * 业务表入库，校验报文合法性
			 * 
			 * 业务主表，子表入库失败即签收失败
			 */
			TvDirectpaymsgsubSxDto[] subDtos=new TvDirectpaymsgsubSxDto[subDtoList.size()];
			subDtos=(TvDirectpaymsgsubSxDto[]) subDtoList.toArray(subDtos);
			DatabaseFacade.getODB().create(maindto);
			DatabaseFacade.getODB().create(subDtos);
			}catch(Exception e){
				logger.error(e);
				voucher.voucherComfailForSX(mainvou, "报文不规范："+e.getMessage());
				continue;
			}
			
			list=new ArrayList();
			list.add(maindto);
			list.add(vDto);			
			list.add(expFuncCodeList);
			list.add(agencyCodeList);
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
			VoucherException voucherE  = new VoucherException();
			voucherE.saveErrInfo(VtCode, e);
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

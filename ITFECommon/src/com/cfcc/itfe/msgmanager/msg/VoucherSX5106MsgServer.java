package com.cfcc.itfe.msgmanager.msg;



import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainSxDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubSxDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoSxDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.voucher.sxservice.VoucherVerifySX;
import com.cfcc.itfe.xmlparse.AbstractXmlParser;


public class VoucherSX5106MsgServer extends AbstractXmlParser {

	private static Log logger = LogFactory.getLog(VoucherSX5106MsgServer.class);
	private Voucher voucher;
	/**
	 * 报文解析处理类
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
			logger.error("解析授权支付额度凭证5106报文出现错误！",e);
			throw new ITFEBizException("解析授权支付额度凭证5106报文出现错误！",e);
			
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
		Map<String,BigDecimal> agencyCodeMap = null;
		//预算科目代码list
		ArrayList<String> expFuncCodeList = null; 
		//预算单位代码list
		List agencyCodeList = null;
		
		//String sAgencyCode ="";
		TvGrantpaymsgmainSxDto maindto  = null;
		List mainDtoList=null; 
		List<TvGrantpaymsgsubSxDto> subDtoList=null; 
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
			String Id = elementVoucher.elementText("Id");//财政授权支付汇总清算额度通知单Id
			String VoucherNo  = elementVoucher.elementText("VoucherNo");//凭证号
			String VouDate = elementVoucher.elementText("VouDate"); //凭证日期
			String TreCode = elementVoucher.elementText("TreCode"); //国库主体代码
			String FinOrgCode = elementVoucher.elementText("FinOrgCode");//财政机关代码
			String PlanAmtSum = elementVoucher.elementText("PlanAmt");//合计清算额度金额
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
			maindto  = new TvGrantpaymsgmainSxDto();
			mainvou = dealnos.get(VoucherNo).toString();
		    maindto.setIvousrlno(Long.valueOf(Long.parseLong(mainvou)));
	        maindto.setIvousrlno(Long.valueOf(Long.parseLong(mainvou)));
			maindto.setNmoney(BigDecimal.valueOf(Double.valueOf(PlanAmtSum)));
			maindto.setSaccdate(currentDate);
			maindto.setSamttype(MsgConstant.AMT_KIND_BANK);
			maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);
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
			//预算单位代码list
			agencyCodeMap = new HashMap();
			//预算科目代码list
			expFuncCodeList = new ArrayList<String>(); 
			//明细合计金额
			
			agencyCodeList = new ArrayList<String>();
			/**
			 * 组装TvPayoutmsgsubDto对象
			 */
			subDtoList=new ArrayList<TvGrantpaymsgsubSxDto>();
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
				String PlanAmt = elementDetail.elementText("PlanAmt");//支付金额
				String sdetailHold1 = elementDetail.elementText("Hold1");//预留字段1
				String sdetailHold2 = elementDetail.elementText("Hold2");//预留字段2
				String sdetailHold3 = elementDetail.elementText("Hold3");//预留字段3
				String sdetailHold4 = elementDetail.elementText("Hold4");//预留字段4
				TvGrantpaymsgsubSxDto subdto=new TvGrantpaymsgsubSxDto();
				//此处的设值待确认
				subdto.setIdetailseqno(j+1);
				subdto.setIvousrlno(Long.parseLong(mainvou));
				subdto.setNmoney(BigDecimal.valueOf(Double.valueOf(PlanAmt)));
				subdto.setSaccattrib(MsgConstant.ACCT_PROP_ZERO);
				subdto.setSaccdate(currentDate);//账务日期
				subdto.setSbudgettype(MsgConstant.BDG_KIND_IN);
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
				//设置预算单位及金额统计
				if(agencyCodeMap.containsKey(subdto.getSbudgetunitcode())){
					BigDecimal lbigD  = agencyCodeMap.get(subdto.getSbudgetunitcode()).add(subdto.getNmoney());
					agencyCodeMap.put(subdto.getSbudgetunitcode(),lbigD);
				}else{
					agencyCodeMap.put(subdto.getSbudgetunitcode(), subdto.getNmoney());
				}
				
			
				agencyCodeList.add(TreCode+SupDepCode);
				expFuncCodeList.add(ExpFuncCode);
				
				sumAmt = sumAmt.add(subPayAmt); 
				subDtoList.add(subdto);
			}
			String ls_VoucherEnd = "";
			int k = 0;
			if(agencyCodeMap.size()>1){
				k = 0;
				Set subSet = agencyCodeMap.entrySet();
				for(Iterator subIt = subSet.iterator(); subIt.hasNext();){
					k++;
					ls_VoucherEnd = "00000";
					ls_VoucherEnd = ls_VoucherEnd + k;
					ls_VoucherEnd = ls_VoucherEnd.substring(ls_VoucherEnd.length()-5, ls_VoucherEnd.length());
					Map.Entry<String, BigDecimal> subEntry = (Map.Entry<String, BigDecimal>) subIt.next();   
					String ls_budgetunitcode = subEntry.getKey();
					BigDecimal  sumSubMoney = subEntry.getValue();
					
					TvGrantpaymsgmainSxDto newTvGrantpaymsgmainDto = (TvGrantpaymsgmainSxDto) maindto.clone();
					String TraNo = VoucherUtil.getGrantSequence();  
					//凭证流水号
					String ls_VoucherNo  = maindto.getSpackageticketno()+ls_VoucherEnd;
					newTvGrantpaymsgmainDto.setSpackageticketno(ls_VoucherNo);
					newTvGrantpaymsgmainDto.setNmoney(sumSubMoney);
					newTvGrantpaymsgmainDto.setSbudgetunitcode(ls_budgetunitcode);
					newTvGrantpaymsgmainDto.setSdealno(TraNo.substring(8, 16));
					mainDtoList.add(newTvGrantpaymsgmainDto);
					for(TvGrantpaymsgsubSxDto subDto:subDtoList){
						if(subDto.getSbudgetunitcode().equals(ls_budgetunitcode)){
							subDto.setSpackageticketno(ls_VoucherNo);
							subDto.setIvousrlno(newTvGrantpaymsgmainDto.getIvousrlno());
						}
					}
				}
			}else{
				maindto.setSbudgetunitcode(subDtoList.get(0).getSbudgetunitcode());
				mainDtoList.add(maindto);
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
			String returnmsg = voucherVerifySX.checkValid(verifydto, MsgConstant.VOUCHER_NO_5106);
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
			 *校验凭证编号不能大于15位
			 *
			 */
			if(maindto.getSpackageticketno().length()>20){
				String errMsg="凭证编号不能大于16位！";
				voucher.voucherComfailForSX(vDto.getSdealno(), errMsg);
				continue;
			}
		
			/**
			 * 业务表入库，校验报文合法性
			 * 
			 * 业务主表，子表入库失败即签收失败
			 */
			TvGrantpaymsgmainSxDto[] mainDtos=new TvGrantpaymsgmainSxDto[mainDtoList.size()];
			mainDtos=(TvGrantpaymsgmainSxDto[]) mainDtoList.toArray(mainDtos);
			TvGrantpaymsgsubSxDto[] subDtos=new TvGrantpaymsgsubSxDto[subDtoList.size()];
			subDtos=(TvGrantpaymsgsubSxDto[]) subDtoList.toArray(subDtos);
			
			DatabaseFacade.getODB().create(mainDtos);
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

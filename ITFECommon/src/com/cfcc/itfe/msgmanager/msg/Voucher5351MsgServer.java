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
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TfGrantpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfGrantpayAdjustsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherVerify;


public class Voucher5351MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher5351MsgServer.class);
	private Voucher voucher;
	/**
	 * 财政发往人行的授权支付调整书(零额度)5351
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
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
			logger.error("解析授权支付调整书5351报文出现错误！",e);
			throw new ITFEBizException("解析授权支付调整书5351报文出现错误！",e);
			
		}
		List VoucherBodyList  = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
		String AdmDivCode  = "";//行政区划代码
		String StYear  = "";//年度
		String VtCode  = "";//凭证类型
		
		
		//获取行政区划代码、年度和凭证类型
		if(VoucherBodyList.size()>0){
			//VoucherBody
			Element element  = (Element)VoucherBodyList.get(0);
			AdmDivCode  = element.attribute("AdmDivCode").getText();
			StYear  = element.attribute("StYear").getText();
			VtCode  = element.attribute("VtCode").getText();
		}
		
		//预算科目代码list
		List<String> expFuncCodeList = null; 
		//预算单位代码list
		List<String> agencyCodeList = null;
		//零金额list	 @author 张会斌
		ArrayList<BigDecimal> zeroList = null; 
		
		TfGrantpayAdjustmainDto maindto  = null;
		List<TfGrantpayAdjustsubDto> subDtoList=null; 
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
			String Id = elementVoucher.elementText("Id");//申请单Id
			/*String AdmDivCode  = elementVoucher.elementText("AdmDivCode");//行政区划代码
			String StYear  = elementVoucher.elementText("StYear");//业务年度
			String VtCode  = elementVoucher.elementText("VtCode");//凭证类型编号
			String VoucherNo = elementVoucher.elementText("VoucherNo"); //凭证号*/
			String VouDate = elementVoucher.elementText("VouDate"); //凭证日期
			String BgtTypeCode = elementVoucher.elementText("BgtTypeCode");//预算类型编码
			String BgtTypeName = elementVoucher.elementText("BgtTypeName");//预算类型名称
			String FundTypeCode = elementVoucher.elementText("FundTypeCode");//资金性质编码
			String FundTypeName = elementVoucher.elementText("FundTypeName");//资金性质名称
			String PayAmt = elementVoucher.elementText("PayAmt");//汇总调整金额
			String PayBankCode = elementVoucher.elementText("PayBankCode");//代理银行编码
			String PayBankName = elementVoucher.elementText("PayBankName");//代理银行名称
			String Remark = elementVoucher.elementText("Remark");//摘要
			String XAccDate = elementVoucher.elementText("XAccDate");//处理日期
			String Hold1=elementVoucher.elementText("Hold1");//预留字段1
			String Hold2=elementVoucher.elementText("Hold2");//预留字段2
			
			/**
			 * 组装TfGrantpayAdjustmainDto对象
			 **/
			maindto  = new TfGrantpayAdjustmainDto();
			mainvou = dealnos.get(VoucherNo).toString();
		    maindto.setIvousrlno(Long.valueOf(Long.parseLong(mainvou)));//业务流水号,关联索引表和业务子表
		    maindto.setSorgcode(ls_OrgCode);//核算主体代码
		    maindto.setStrecode("");//接口规范中没有相应的国库代码,首先默认为空字符串,在下面用索引表的国库为其重新赋值
		    maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);//状态
		    maindto.setSdemo("");//描述
		    maindto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//系统时间
		    maindto.setSpackageno("0000");//包流水号
		    maindto.setSid(Id);//申请ID
		    maindto.setSadmdivcode(AdmDivCode);//行政区划代码
		    maindto.setSstyear(StYear);//业务年度
		    maindto.setSvtcode(VtCode);//凭证类型
		    maindto.setSvoucherno(VoucherNo);//凭证编号
		    maindto.setSvoudate(VouDate);//凭证日期
		    maindto.setSbgttypecode(BgtTypeCode);//预算类型编码
		    maindto.setSbgttypename(BgtTypeName);//预算类型名称
		    maindto.setSfundtypecode(FundTypeCode);//资金性质编码
			maindto.setSfundtypename(FundTypeName);//资金性质名称
			maindto.setNpayamt(new BigDecimal(PayAmt));//汇总调整金额
			maindto.setSpaybankcode(PayBankCode);//代理银行编码
			maindto.setSpaybankname(PayBankName);//代理银行名称
			maindto.setSremark(Remark);//摘要
			maindto.setSxaccdate(XAccDate);//处理日期
			maindto.setShold1(Hold1);//预留字段1
			maindto.setShold2(Hold2);//预留字段2
			//根据交易流水号取得凭证索引表dto
			vDto=new TvVoucherinfoDto();
			vDto.setSdealno(mainvou);
			vDto=(TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
			maindto.setStrecode(vDto.getStrecode());//接口规范中没有相应的国库代码,用索引表的国库为其赋值
			
			//预算科目代码list
			expFuncCodeList = new ArrayList<String>(); 
			//预算单位代码list
			agencyCodeList = new ArrayList<String>();
			//零金额list @author 张会斌
			zeroList = new ArrayList<BigDecimal>();
			//子表明细Id集合
			List<String>  subDtoIdList = new ArrayList<String>();
			
			/**
			 * 组装TfGrantpayAdjustsubDto对象
			 */
			subDtoList=new ArrayList<TfGrantpayAdjustsubDto>();
			for(int j=0;j<listDetail.size();j++){
		
				Element elementDetail  = (Element)listDetail.get(j);
				String SubId = elementDetail.elementText("Id");//流水号
				String SubVoucherNo = elementDetail.elementText("VoucherNo");//授权支付调整凭证单号
				String SupDepCode = elementDetail.elementText("SupDepCode");//一级预算单位编码
				String SupDepName = elementDetail.elementText("SupDepName");//一级预算单位名称
				String ExpFuncCode = elementDetail.elementText("ExpFuncCode");//支出功能分类科目编码
				String ExpFuncName = elementDetail.elementText("ExpFuncName");//支出功能分类科目名称
				String SubPayAmt = elementDetail.elementText("PayAmt");//支付金额
				String PaySummaryName = elementDetail.elementText("PaySummaryName");//摘要名称
				String XDealResult = elementDetail.elementText("XDealResult");//处理结果  1成功、0失败
				String XAddWord = elementDetail.elementText("XAddWord");//附言,失败原因
				String sdetailHold1 = elementDetail.elementText("Hold1");//预留字段1
				String sdetailHold2 = elementDetail.elementText("Hold2");//预留字段2
				String sdetailHold3 = elementDetail.elementText("Hold3");//预留字段3
				String sdetailHold4 = elementDetail.elementText("Hold4");//预留字段4
				
				TfGrantpayAdjustsubDto subdto=new TfGrantpayAdjustsubDto();
				subdto.setIvousrlno(Long.parseLong(mainvou));//业务流水号,关联索引表和业务主表
				subdto.setIseqno(Long.valueOf(j+1));//明细号
				subdto.setSid(SubId);//流水号
				subdto.setSvoucherno(SubVoucherNo);//授权支付调整凭证单号
				subdto.setSsupdepcode(SupDepCode);//一级预算单位编码
				subdto.setSsupdepname(SupDepName);//一级预算单位名称
				subdto.setSexpfunccode(ExpFuncCode);//支出功能分类科目编码
				subdto.setSexpfuncname(ExpFuncName);//支出功能分类科目名称
				BigDecimal detailPayAmt = new BigDecimal(SubPayAmt);
				subdto.setNpayamt(detailPayAmt);//支付金额
				subdto.setSpaysummaryname(PaySummaryName);//摘要名称
				subdto.setSxdealresult(XDealResult);//处理结果  1成功、0失败
				subdto.setSxaddword(XAddWord);//附言,失败原因
				subdto.setShold1(sdetailHold1);//预留字段1
				subdto.setShold2(sdetailHold2);//预留字段2
				subdto.setShold3(sdetailHold3);//预留字段3
				subdto.setShold4(sdetailHold4);//预留字段4

				agencyCodeList.add(maindto.getStrecode()+SupDepCode);
				expFuncCodeList.add(ExpFuncCode);
				//零金额列表 @author 张会斌
				if(detailPayAmt.equals(BigDecimal.ZERO)){
					zeroList.add(detailPayAmt);
				}
				
				sumAmt = sumAmt.add(detailPayAmt); 
				subDtoList.add(subdto);
				subDtoIdList.add(subdto.getSid());
			}					
			
			/**
			 * 组装verifydto,进行报文校验
			 */
			verifydto.setTrecode(maindto.getStrecode());
			verifydto.setFinorgcode(ls_FinOrgCode);
			verifydto.setVoucherno(VoucherNo);
			verifydto.setVoudate(VouDate);
			//增加年度、月份，总金额的校验 by renqingbin
			verifydto.setOfyear(StYear);
			verifydto.setFamt(PayAmt);
			verifydto.setPaybankno(PayBankCode);
			String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_5351);
			
			if(returnmsg != null){//返回错误信息签收失败
				voucher.voucherComfail(vDto.getSdealno(), returnmsg);
				continue;
			}
			
			
			/**
			 *校验子表中是否有零金额（上海）
			 *@author 张会斌
			 */
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0&&zeroList.size() > 0){
				String errMsg="明细信息中包含有零金额的数据!";
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
				continue;
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
				String errMsg = "每个预算单位明细条数必须小于500!";
				voucher.voucherComfail(vDto.getSdealno(), errMsg);
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
			
			/**
			 * 业务表入库，校验报文合法性
			 * 
			 * 业务主表，子表入库失败即签收失败
			 */
			TfGrantpayAdjustsubDto[] subDtos=new TfGrantpayAdjustsubDto[subDtoList.size()];
			subDtos=(TfGrantpayAdjustsubDto[]) subDtoList.toArray(subDtos);
			
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
				throw new ITFEBizException("签收凭证报文"+VtCode+"出现异常",e);
			}
			
			list=new ArrayList();
			list.add(maindto);
			list.add(vDto);			
			list.add(expFuncCodeList);
			list.add(agencyCodeList);
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

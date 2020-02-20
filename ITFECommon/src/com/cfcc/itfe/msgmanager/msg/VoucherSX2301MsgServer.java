package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvPayreckbankSxDto;
import com.cfcc.itfe.persistence.dto.TvPayreckbanklistSxDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoSxDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.sxservice.VoucherVerifySX;
import com.cfcc.itfe.xmlparse.AbstractXmlParser;
import com.cfcc.jaf.common.util.DateUtil;

public class VoucherSX2301MsgServer extends AbstractXmlParser {

	private static Log logger = LogFactory.getLog(VoucherSX2301MsgServer.class);
	private Voucher voucher;
	/**
	 * 报文解析处理类
	 * @param map  关键信息MAP
	 * 		  key：【orgcode】-value:【String】机构代码
	 * 		  key：【filename】-value:【String】 文件名
	 *        key: 【dealnomap】-value:【HashMap<String,String>】交易流水号MAP   
	 * @param xmlString 报文信息
	 * @author sunyan
	 */
	public void dealMsg(HashMap map, String xmlString) throws ITFEBizException {
		String voucherXml = xmlString;
		String ls_FileName = (String) map.get("filename");
		String ls_OrgCode = (String) map.get("orgcode");
		HashMap<String,String> dealnos=(HashMap<String,String>)map.get("dealnomap");
		
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("解析申请划款凭证回单2301报文出现错误！", e);
			throw new ITFEBizException("解析申请划款凭证回单2301报文出现错误！", e);

		}
		List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes(
				"VoucherBody");
		String AdmDivCode = "";// 行政区划代码
		String StYear = "";// 年度
		String VtCode = "";// 凭证类型
		Date currentDate = TimeFacade.getCurrentDateTime();// 当前系统日期
		List<String> voucherList = new ArrayList<String>();
		
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
		TvPayreckbankSxDto maindto = null;
		List subDtoList = null;
		List lists = new ArrayList();
		List list = null;
		// 凭证序列号
		String mainvou = "";
		TvVoucherinfoSxDto vDto = new TvVoucherinfoSxDto();

		// 解析报文
		for (int i = 0; i < VoucherBodyList.size(); i++) {
			try {
				// 明细合计金额
				BigDecimal sumAmt = new BigDecimal("0.00");
				// VoucherBody
				Element element = (Element) VoucherBodyList.get(i);
				// Voucher
				Element elementVoucher = (Element) element
						.selectSingleNode("Voucher");
				// 明细信息List
				List listDetail = elementVoucher.selectSingleNode("DetailList")
						.selectNodes("Detail");
				//报文校验信息dto
				VoucherVerifyDto verifydto = new VoucherVerifyDto();
				VoucherVerifySX voucherVerifySX = new VoucherVerifySX();	
				/**
				 * 凭证信息
				 */
				String Attach = ((Element) VoucherBodyList.get(i))
						.elementText("Attach");// 附加信息
				String Id = elementVoucher.elementText("Id");// 申请划款凭证Id
				String VouDate = elementVoucher.elementText("VouDate"); // 凭证日期
				String VoucherNo = elementVoucher.elementText("VoucherNo");// 凭证号
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
				String XAddWord = elementVoucher.elementText("XAddWord");// 附言
				String XClearDate = elementVoucher.elementText("XClearDate");// 清算日期
				String XPayAmt = elementVoucher.elementText("XPayAmt");// 汇总清算金额
				String Hold1 = elementVoucher.elementText("Hold1");// 预留字段1
				String Hold2 = elementVoucher.elementText("Hold2");// 预留字段2

				/**
				 * 组装TvPayreckBankSxDto对象
				 **/
				maindto = new TvPayreckbankSxDto();
				mainvou = dealnos.get(VoucherNo);// 获取序列号
				maindto.setStrano(mainvou.substring(8, 16));// 申请划款凭证Id
				maindto.setIvousrlno(Long.valueOf(mainvou));// 凭证流水号
				maindto.setSid(mainvou.substring(8, 16));// 申请划款凭证Id
				maindto.setSadmdivcode(AdmDivCode);//行政区划代码
				maindto.setSofyear(StYear);// 业务年度
				maindto.setSvtcode(VtCode);//凭证类型编号\
				SimpleDateFormat f=new SimpleDateFormat("yyyyMMdd");
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
				if("12".equals(PayTypeCode)){
					maindto.setSpaymode("1");// 授权支付
				}else if("11".equals(PayTypeCode)){
					maindto.setSpaymode("0");// 支付方式编码    直接支付
				}
				maindto.setSpaytypename(PayTypeName);// 支付方式名称
				maindto.setSpayeeacct(AgentAcctNo);// 收款银行账号
				maindto.setSpayeename(AgentAcctName);// 收款银行账户名称
				maindto.setSagentacctbankname(AgentAcctBankName);// 收款银行
				maindto.setSpayeracct(ClearAcctNo);// 付款账号
				maindto.setSpayername(ClearAcctName);// 付款账户名称
				maindto.setSclearacctbankname(ClearAcctBankName);// 付款银行
				maindto.setFamt(BigDecimal.valueOf(Double.valueOf(PayAmt)));// 汇总清算金额
				maindto.setSpaybankname(PayBankName);// 代理银行名称
				maindto.setSagentbnkcode(PayBankNo);// 代理银行行号
				maindto.setSdescription(Remark);// 摘要
				maindto.setSmoneycorpcode(MoneyCorpCode);// 金融机构编码
				maindto.setSxpaysndbnkno(XPaySndBnkNo);// 支付发起行行号
				maindto.setSaddword(XAddWord);// 附言
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
				maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);//预算种类(默认预算内)
				maindto.setSpayeeopbkno(PayBankNo);//收款人开户行行号
				maindto.setSfilename(ls_FileName);
				maindto.setSresult(DealCodeConstants.DEALCODE_ITFE_DEALING);//状态 处理中
				maindto.setTsupdate(new Timestamp(new java.util.Date()
						.getTime()));// 系统时间
				// 预算单位代码list
				agencyCodeList = new ArrayList<String>();
				// 预算科目代码list
				expFuncCodeList = new ArrayList<String>();
				/**
				 * 组装TvPayreckBankListDto对象
				 */
				subDtoList = new ArrayList<TvPayreckbanklistSxDto>();
				for (int j = 0; j < listDetail.size(); j++) {
					Element elementDetail = (Element) listDetail.get(j);
					String VoucherNol = elementDetail.elementText("VoucherNo");// 支付凭证单号
					SupDepCode = elementDetail.elementText("SupDepCode");// 一级预算单位编码
					String SupDepName = elementDetail.elementText("SupDepName");// 一级预算单位名称
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
					TvPayreckbanklistSxDto subdto = new TvPayreckbanklistSxDto();
					// 此处的设值待确认
					subdto.setIseqno(j+1);//序号
					subdto.setSacctprop(MsgConstant.ACCT_PROP_ZERO);//账户性质
					subdto.setIvousrlno(Long.valueOf(mainvou));// 子表序列号
					subdto.setSvouchern0(VoucherNol);// 子表明细序号
					// 支付凭证单号
					subdto.setSbdgorgcode(SupDepCode);// 一级预算单位编码
					subdto.setSsupdepname(SupDepName);// 一级预算单位名称
					subdto.setSfuncbdgsbtcode(ExpFuncCode);// 支出功能分类科目编码
					subdto.setSexpfuncname(ExpFuncName);// 支出功能分类科目名称
					subdto.setFamt(BigDecimal.valueOf(Double.valueOf(sPayAmt)));// 支付金额
					subdto.setSpaysummaryname(PaySummaryName);// 摘要名称
					subdto.setShold1(sdetailHold1);// 预留字段1
					subdto.setShold2(sdetailHold2);// 预留字段2
					subdto.setShold3(sdetailHold3);// 预留字段3
					subdto.setShold4(sdetailHold4);// 预留字段4
					subdto.setTsupdate(new Timestamp(new java.util.Date()
					.getTime()));//更新时间
					
					//设置预算单位、科目LIST,明细合计
					if(!agencyCodeList.contains(TreCode+SupDepCode)){
						agencyCodeList.add(TreCode+SupDepCode);
					}
					expFuncCodeList.add(ExpFuncCode);
					// 明细合计
					sumAmt = sumAmt.add(BigDecimal.valueOf(Double.valueOf(sPayAmt)));

					subDtoList.add(subdto);
					
				}
				vDto = new TvVoucherinfoSxDto();
				vDto.setSdealno(mainvou);
				vDto = (TvVoucherinfoSxDto) CommonFacade.getODB().findRsByDto(
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
				String returnmsg = voucherVerifySX.checkValid(verifydto, MsgConstant.VOUCHER_NO_2301);
				if(returnmsg != null){//返回错误信息签收失败
					voucher.voucherComfailForSX(vDto.getSdealno(), returnmsg);
					continue;
				}
				/**
				 *校验主表金额是否与子表金额相等
				 * 
				 */
				if (maindto.getFamt().compareTo(sumAmt) != 0) {
					String errMsg = "主单金额与明细累计金额不相等，主单金额：" + maindto.getFamt()
							+ " 明细累计金额： " + sumAmt;
					voucher.voucherComfailForSX(vDto.getSdealno(), errMsg);
					continue;
				}
				/**
				 *明细条数不能超过500
				 * 
				 */
				if (subDtoList.size() > 499) {
					String errMsg = "明细条数不能超过500";
					voucher.voucherComfailForSX(vDto.getSdealno(), errMsg);
					continue;
				}
				
				maindto.setIstatinfnum(subDtoList.size());//明细信息条数
				/**
				 * 业务表入库，校验报文合法性
				 * 
				 * 业务主表，子表入库失败即签收失败
				 */
				TvPayreckbanklistSxDto[] subDtos = new TvPayreckbanklistSxDto[subDtoList
						.size()];
				subDtos = (TvPayreckbanklistSxDto[]) subDtoList.toArray(subDtos);
				DatabaseFacade.getODB().create(maindto);
				DatabaseFacade.getODB().create(subDtos);
			}catch(Exception e){
				logger.error(e);
				voucher.voucherComfailForSX(mainvou, "报文不规范："+e.getMessage());
				continue;
			}

			list = new ArrayList();
			list.add(maindto);
			list.add(vDto);
			list.add(expFuncCodeList);
			list.add(agencyCodeList);//预算单位list
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

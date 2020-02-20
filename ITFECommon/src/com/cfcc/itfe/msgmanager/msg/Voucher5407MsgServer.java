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

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherVerify;

/**
 * 更正、调库报文5801  厦门需求
 * @author Administrator
 * 1)	1-收入更正；2-退库更正；3-支出更正；4-调库公用一套报文规范；
 * 2)	XML报文结构一主单一明细，原列数据明细和现列数据明细并列在明细里，详情见5801.xml
 * 3)	主单新增Id、AdmDivCode、StYear、VtCode、XacctDate字段；明细单新增Id 、VoucherBillId 字段；“更正凭证编号”标识符由CorrVouNo修改为VoucherNo，电子凭证库报文规范需要。
 * 4)	明细单新增OriBudgetSubjectName、OriBudgetLevName、OriTreName、OriPayer、OriBillTypeCode、OriBillTypeName、OriVouDate、OriVoucherNo、CurBudgetSubjectName、CurBudgetLevName、CurTreName、CurPayer、CurBillTypeCode、CurBillTypeName、CurVouDate、CurVoucherNo，更正（调库）通知书电子凭证展示需要。
 * 5)	主单新增Hold1、Hold2，明细单新增Hold1、Hold2、Hold3、Hold4作为预留字段使用。
 * 6)	主单“更正、调库处理类型”、“更正、调库类型代码”、“更正、调库类型名称”、“总金额”缺少标识符，根据XML报文规范需要定义。
 *
 */
public class Voucher5407MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher5407MsgServer.class);
	private Voucher voucher;
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();
		String voucherXml = (String) muleMessage.getPayload();
		String ls_FileName = (String) muleMessage.getProperty("fileName");
		String ls_OrgCode = (String) muleMessage.getProperty("orgCode");
		HashMap<String, String> dealnos = (HashMap<String, String>) muleMessage.getProperty("dealnos");
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("解析更正调库5407报文出现错误！", e);
			throw new ITFEBizException("解析更正调库5407报文出现错误！", e);
		}
		//获取一个文件中存在的多个凭证主体信息
		List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
		String AdmDivCode = "";// 行政区划代码
		String StYear = "";// 年度
		String VtCode = "";// 凭证类型
		String currentDate = TimeFacade.getCurrentStringTime();// 当前8位系统日期
		List<String> voucherList = new ArrayList<String>();
		// 获取行政区划代码、年度和凭证类型
		if (VoucherBodyList.size() > 0) {
			Element element = (Element) VoucherBodyList.get(0);
			AdmDivCode = element.attribute("AdmDivCode").getText();
			StYear = element.attribute("StYear").getText();
			VtCode = element.attribute("VtCode").getText();
		}
		TvInCorrhandbookDto maindto = null;
		List subDtoIdList = new ArrayList();
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
				// Voucher
				Element elementVoucher = (Element) element.selectSingleNode("Voucher");
				// 明细信息List
				List listDetail = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");
				// 报文校验信息dto
				VoucherVerifyDto verifydto = new VoucherVerifyDto();
				VoucherVerify voucherVerify = new VoucherVerify();
				/**
				 * 凭证信息
				 */
				String Id = elementVoucher.elementText("Id");//更正（调库）通知书Id
				String VouDate = elementVoucher.elementText("VouDate");//凭证日期
				String FinOrgCode = elementVoucher.elementText("FinOrgCode");//财政机关代码
				String CorrReaName = elementVoucher.elementText("CorrReaName");//更正原因名称
				String Amt = elementVoucher.elementText("Amt");//发生金额
				
//				String 	TaxOrgCode  = elementVoucher.elementText("TaxOrgCode");//财政机关代码-基层征收机关代码，征收机关填写 o
//				String  EntrustDate = elementVoucher.elementText("EntrustDate");//委托日期 -更正、调库请求发起日期
//				String  BillDate = elementVoucher.elementText("BillDate");//开票日期（凭证日期）-更正的开票日期
				String 	VoucherNo = element.attribute("VoucherNo").getText();// 凭证编号//更正凭证编号-同一征收机关不能重复；更正通知书上的编号
//				String  DealType= elementVoucher.elementText("DealType");//1-电子，人行国库电子凭证库收到财政数据后，继续向TIPS系统转发电子数据；
				//	2-手工，人行国库电子凭证库收到财政数据后，不再向TIPS系统转发电子数据，人行国库打印出凭证；
				//手工数据的判断标准：①凭证类型编号为退库更正、支出更正和调库；②凭证类型编号为收入更正的，收入更正原列数据中的原收款国库代码不等于现列数据中的现收款国库代码；
				String  EditTypeCode = elementVoucher.elementText("EditTypeCode");//更正、调库类型代码
				String  EditTypeName = elementVoucher.elementText("EditTypeName");//更正、调库类型名称 1-收入更正；2-退库更正；3-支出更正；4-调库
//				String  EditAmt = elementVoucher.elementText("EditAmt");//总金额  数据明细中现列金额 减 原列金额，该值为0
//				String  TraNo = elementVoucher.elementText("TraNo");//交易流水号  用于发起方唯一标识一笔交易，可由发起方自定义
//				String  OriTraNo = elementVoucher.elementText("OriTraNo");//原交易流水号 参考交易流水号,为被更正的信息的交易流水号，同一征收机关不能重复
				String  CorrReaCode = elementVoucher.elementText("CorrReaCode");//更正原因代码	NString  211（串科目）、212（串级次）、213（串国库）、214（审计、财监办文件要求更正）、215（财政体制变化更正）、216（串征收机关）
//				String  Remark = elementVoucher.elementText("Remark");//备注
				String  XAcctDate = elementVoucher.elementText("XAcctDate");//处理日期 人民银行处理日期，人民银行在回单中补录
				String  mHold1 = elementVoucher.elementText("Hold1");//预留字段1
				String  mHold2 = elementVoucher.elementText("Hold2");//预留字段2
				

				/**
				 * 组装TvInCorrhandbookDto对象
				 **/
				maindto = new TvInCorrhandbookDto();
				mainvou = dealnos.get(VoucherNo);// 获取序列号
				
				//查找凭证索引表
				vDto = new TvVoucherinfoDto();
				vDto.setSdealno(mainvou);
				vDto = (TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
				

				maindto.setIvousrlno(Long.valueOf(mainvou));// 凭证流水号
				maindto.setSid(Id);// 申请划款凭证Id
//				maindto.setSadmdivcode(AdmDivCode);// 行政区划代码
//				maindto.setsStYear(StYear);//业务年度
//				maindto.setSVtCode(VtCode);//凭证类型编号
				maindto.setDvoucher(VouDate);//凭证日期
				maindto.setDentrustdate(VouDate);//委托日期
				maindto.setDaccept(CommonUtil.strToDate(VouDate));//开票日期
				maindto.setScorrvouno(VoucherNo);//凭证号
				maindto.setSfinorgcode(FinOrgCode);//财政机关代码
//				maindto.setSdealtype(DealType);
				maindto.setSedittypecode(EditTypeCode);//更正、调库类型代码
				maindto.setSedittypename(EditTypeName);//更正、调库类型名称
				maindto.setSreasoncode(CorrReaCode);//更正原因代码
				maindto.setScorrreaname(CorrReaName);//更正原因名称
				maindto.setFeditamt(BigDecimal.valueOf(Double.valueOf(Amt)));//发生金额 值为0
				maindto.setSdealno(mainvou.substring(mainvou.length()-8));
				maindto.setSelecvouno(VoucherNo);
//				maindto.setStrano(mainvou);
//				maindto.setSoritrano(mainvou);
				
//				maindto.setSremark(Remark);
				maindto.setDxacctdate(XAcctDate);//处理日期
				maindto.setSmhold1(mHold1);//预留字段1
				maindto.setSmhold2(mHold2);//预留字段2

				maindto.setSbookorgcode(ls_OrgCode);
				maindto.setSpackageno("111");//包流水号不能为空
				
				
				
				maindto.setSfilename(ls_FileName);
				maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);// 状态
				// 处理中
				maindto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));// 系统时间
			
				/**
				 * 组装子对象 -更正明细
				 */
				List<TvInCorrhandbookDto> subDtoList = new ArrayList<TvInCorrhandbookDto>();
				String sdetailId = null;// 明细Id
				for (int j = 0; j < listDetail.size(); j++) {
					Element elementDetail = (Element) listDetail.get(j);
//					Element elementDetail = (Element) listDetail.get(0);
					String sdetailIdElement = elementDetail.elementText("Id");//更正（调库）明细Id
					
					String VoucherBillId = elementDetail.elementText("VoucherBillId");//更正（调库）凭证Id 与主单Id内容一致
//					String OriTaxVouNo = elementDetail.elementText("OriTaxVouNo");//原税票号码  同一征收机关不得重复，征收机关填写
					String OriTaxOrgCode = elementDetail.elementText("OriTaxOrgCode");//原征收机关代码
					String OriFundTypeCode = elementDetail.elementText("OriFundTypeCode");//原征收机关代码
//					String OriBudgetType = elementDetail.elementText("OriBudgetType");//原预算种类
					String OriBudSubjectCode = elementDetail.elementText("OriBudSubjectCode");//原列预算科目代码 见公共数据更新通知报文中预算科目代码。
					//更正调库业务类型为支出更正的，预算科目代码必须为支出科目，不能为收入科目；其他类型业务，预算科目代码必须为收入科目，不能为支出科目。

					String OriBudSubjectName = elementDetail.elementText("OriBudSubjectName");//原列预算科目名称
					String OriBudLevelCode = elementDetail.elementText("OriBudLevelCode");//原列预算级次代码
					String OriBudLevelName= elementDetail.elementText("OriBudLevelName");//原列预算级次名称
					String OriTrimSign = elementDetail.elementText("OriTrimSign");//原整理期标志 0－本年度；1－上年度
//					String OriViceSign = elementDetail.elementText("OriViceSign");//原辅助标志
					String OriTreCode= elementDetail.elementText("OriTreCode");//原收款国库代码
					String OriTreName= elementDetail.elementText("OriTreName");//原收款国库名称
//					String OriPayer = elementDetail.elementText("OriPayer");//原缴款（用款）单位
					String OriAgencyCode= elementDetail.elementText("OriAgencyCode");//原收款国库名称
					String OriAgencyName= elementDetail.elementText("OriAgencyName");//原收款国库名称
//					String OriBillTypeCode = elementDetail.elementText("OriBillTypeCode");//原票据类型编码
//					String OriBillTypeName = elementDetail.elementText("OriBillTypeName");//原票据类型名称
					String OriVouDate = elementDetail.elementText("OriVouDate");//原票据凭证日期
					String OriVoucherNo = elementDetail.elementText("OriVoucherNo");//原票据凭证号
					String OriAmt = elementDetail.elementText("OriAmt");//原列金额

					String CurTaxOrgCode = elementDetail.elementText("CurTaxOrgCode");//更正征收机关代码
					String CurFundTypeCode = elementDetail.elementText("CurFundTypeCode");//现预算种类
					
					String CurBudSubjectCode = elementDetail.elementText("CurBudSubjectCode");//现预算科目代码
					String CurBudSubjectName = elementDetail.elementText("CurBudSubjectName");//现预算科目名称
					String CurBudLevelCode = elementDetail.elementText("CurBudLevelCode");//现预算级次代码
					String CurBudLevelName = elementDetail.elementText("CurBudLevelName");//现预算级次名称
					String CurTrimSign = elementDetail.elementText("CurTrimSign");//现整理期标志
//					String CurViceSign = elementDetail.elementText("CurViceSign");//现辅助标志
					String CurTreCode = elementDetail.elementText("CurTreCode");//现收款国库代码
					String CurTreName = elementDetail.elementText("CurTreName");//现收款国库名称
					String CurAgencyCode = elementDetail.elementText("CurAgencyCode");//现缴款单位代码
					String CurAgencyName = elementDetail.elementText("CurAgencyName");//现缴款单位名称
//					String CurPayer = elementDetail.elementText("CurPayer");//现缴款（用款）单位
//					String CurBillTypeCode = elementDetail.elementText("CurBillTypeCode");//现票据类型编码
//					String CurBillTypeName = elementDetail.elementText("CurBillTypeName");//现票据类型名称
					String CurVouDate = elementDetail.elementText("CurVouDate");//现票据凭证日期
					String CurVoucherNo = elementDetail.elementText("CurVoucherNo");//现票据凭证号
					String CurAmt = elementDetail.elementText("CurAmt");//现列金额
					String subHold1 = elementDetail.elementText("Hold1");//预留字段1-原辅助标志
					String subHold2 = elementDetail.elementText("Hold2");//预留字段2-现辅助标志
					String subHold3 = elementDetail.elementText("Hold3");//预留字段3
					String subHold4 = elementDetail.elementText("Hold4");//预留字段4
					
					maindto.setSsubid(sdetailIdElement);
					maindto.setSvoucherbillid(VoucherBillId);
					//-------原------//
//					maindto.setSoritaxvouno(OriTaxVouNo);//原税票号码
					maindto.setSoritaxorgcode(OriTaxOrgCode);//原征收机关代码
					maindto.setSorifundtypecode(OriFundTypeCode);//原资金性质代码
					maindto.setCoribdgkind("1");//原预算种类
					maindto.setSoribdgsbtcode(OriBudSubjectCode);//原列预算科目代码
					maindto.setSoribudgetsubjectname(OriBudSubjectName); //原列预算科目名称
					maindto.setCoribdglevel(OriBudLevelCode); //原列预算级次代码
					maindto.setSoribudgetlevname(OriBudLevelName);//原列预算级次名称
					maindto.setCoritrimsign(OriTrimSign);//原整理期标志
					maindto.setSoriastflag(subHold1);//原辅助标志
					maindto.setSoriaimtrecode(OriTreCode);//原目的国库
					maindto.setSoripayeetrecode(OriTreCode);//原收款国库代码
					maindto.setSoritrename(OriTreName);//原收款国库名称
					maindto.setSoriagencycode(OriAgencyCode);//原缴款单位代码
					maindto.setSoriagencyname(OriAgencyName);//原缴款单位名称
//					maindto.setSoribilltypecode(OriBillTypeCode);//原票据类型编码
//					maindto.setSoribilltypename(OriBillTypeName);//名称
					maindto.setSorivoudate(OriVouDate);//原票据凭证日期
					maindto.setSorivoucherno(OriVoucherNo);//原票据凭证号
					maindto.setForicorramt(BigDecimal.valueOf(Double.valueOf(OriAmt)));//原列金额
					
					//-------现---------//
					maindto.setScurtaxorgcode(CurTaxOrgCode);//现征收机关代码
					maindto.setScurfundtypecode(CurFundTypeCode);//现资金性质代码
					maindto.setCcurbdgkind("1");//现预算种类
					maindto.setScurbdgsbtcode(CurBudSubjectCode);//现预算科目代码
					maindto.setScurbudgetsubjectname(CurBudSubjectName);//名称
					maindto.setCcurbdglevel(CurBudLevelCode);//现预算级次
					maindto.setScurbudgetlevname(CurBudLevelName);
					maindto.setCtrimflag(CurTrimSign);//现整理期标志
					maindto.setScurastflag(subHold2);//现辅助标志
					maindto.setScurpayeetrecode(CurTreCode);//现收款国库代码
					maindto.setScurtrename(CurTreName);//现收款国库名称
//					maindto.setScurpayer(CurPayer); //现缴款（用款）单位
					maindto.setScuragencycode(CurAgencyCode);//现缴款单位代码
					maindto.setScuragencyname(CurAgencyName);//现缴款单位名称
//					maindto.setScurbilltypecode(CurBillTypeCode);//现票据类型编码
//					maindto.setScurbilltypename(CurBillTypeName);//现票据类型名称
					maindto.setScurvoudate(CurVouDate); //现凭证日期
					maindto.setScurvoucherno(CurVoucherNo); //现凭证号
					maindto.setFcurcorramt(BigDecimal.valueOf(Double.valueOf(CurAmt))); //现列金额  必须等于原列数据中的原列金额
					maindto.setShold1(subHold1);//预留1
					maindto.setShold2(subHold2);//预留1
					maindto.setShold3(subHold3);//预留1
					maindto.setShold4(subHold4);//预留1
					
					subDtoList.add(maindto);
					subDtoIdList.add(maindto.getSid());
				}
				/**
				 * 校验明细Id是否为空或重复
				 */
				String checkIdMsg = voucherVerify.checkValidSudDtoId(subDtoIdList);
				if (checkIdMsg != null) {
					// 返回错误信息签收失败
					voucher.voucherComfail(mainvou, checkIdMsg);
					continue;
				}
				if(maindto.getCoribdglevel()!=null&&maindto.getCoribdglevel().equals(MsgConstant.BUDGET_LEVEL_SHARE)){
					if(maindto.getSoriastflag()==null||"".equals(maindto.getSoriastflag())||"null".equals(maindto.getSoriastflag().toLowerCase()))
					{
						voucher.voucherComfail(vDto.getSdealno(), "原预算级次为共享原辅助标志不可为空!");
						continue;
					}
				}
				if(maindto.getCcurbdglevel()!=null&&maindto.getCcurbdglevel().equals(MsgConstant.BUDGET_LEVEL_SHARE)){
					if(maindto.getScurastflag()==null||"".equals(maindto.getScurastflag())||"null".equals(maindto.getScurastflag().toLowerCase()))
					{
						voucher.voucherComfail(vDto.getSdealno(), "现预算级次为共享现辅助标志不可为空!");
						continue;
					}
				}
				/**
				 * 组装verifydto,进行报文校验
				 */
				verifydto.setVoucherno(VoucherNo);
				// 增加年度，总金额的校验 by renqingbin
				String returnmsg = voucherVerify.checkValid(verifydto,MsgConstant.VOUCHER_NO_5407);
				if (returnmsg != null) {// 返回错误信息签收失败
					voucher.voucherComfail(vDto.getSdealno(), returnmsg);
					continue;
				}
				/**
				 *明细条数不能超过1
				 * 
				 */
				if (subDtoList.size() > 1) {
					String errMsg = "明细条数必须等于  1 !";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				//金额校验
				if(maindto.getFeditamt().compareTo(new BigDecimal("0.00"))!=0){
					String errMsg = "主单金额不为0!";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				if((maindto.getForicorramt().compareTo(maindto.getFcurcorramt()))!=0){
					String errMsg = "原更正金额和现更正金额不相等!";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				/**
				 * 业务表入库，校验报文合法性
				 * 
				 * 业务主表，子表入库失败即签收失败
				 */
				DatabaseFacade.getODB().create(maindto);
			} catch (Exception e) {
				logger.error(e);
				voucher.voucherComfail(mainvou, "报文不规范：" + e.getMessage());
				continue;
			}
			// 签收成功
			try {
				voucher.voucherConfirmSuccess(vDto.getSdealno());
			} catch (ITFEBizException e) {
				logger.error(e);
				continue;
			}

			//转换报文信息并发送报文信息到TIPS
//			if(maindto.getSdealtype().equalsIgnoreCase(MsgConstant.VOUCHER_CORRHANDBOOK_DEALTYPE)){
//				
//			}
			
			
			list = new ArrayList();
			list.add(maindto);
			list.add(vDto);
			lists.add(list);
		}

		/**
		 * 校验凭证信息模块
		 */
		try {
			if (lists.size() > 0) {
				voucher.voucherVerify(lists, VtCode);
			}

		} catch (ITFEBizException e) {
			logger.error(e);
			throw new ITFEBizException(e);
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("校验凭证报文" + VtCode + "出现异常", e);
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

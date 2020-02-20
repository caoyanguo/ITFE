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
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class Voucher2252MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher2252MsgServer.class);
	private Voucher voucher;

	/**
	 * 商行发往人行的收款银行退款通知凭证2252
	 * @author 张会斌
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();
		String voucherXml = (String) muleMessage.getPayload();
		String ls_FileName = (String) muleMessage.getProperty("fileName");
		String ls_OrgCode = (String) muleMessage.getProperty("orgCode");
		String ls_FinOrgCode = (String) muleMessage.getProperty("finOrgCode");// 财政代码
		HashMap<String, String> dealnos = (HashMap<String, String>) muleMessage.getProperty("dealnos");
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("解析收款银行退款通知凭证2252报文出现错误！", e);
			throw new ITFEBizException("解析收款银行退款通知凭证2252报文出现错误！", e);

		}
		List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
		String AdmDivCode = "";// 行政区划代码
		String StYear = "";// 年度
		String VtCode = "";// 凭证类型

		// 获取行政区划代码、年度和凭证类型
		if (VoucherBodyList.size() > 0) {
			Element element = (Element) VoucherBodyList.get(0);
			AdmDivCode = element.attribute("AdmDivCode").getText();
			StYear = element.attribute("StYear").getText();
			VtCode = element.attribute("VtCode").getText();
		}

		TfPaybankRefundmainDto maindto = null;
		List<IDto> subDtoList =null;
		List lists = new ArrayList();
		List list = null;
		//正金额金额list
		ArrayList<BigDecimal> moneyList = null; 
		//零金额list
		ArrayList<BigDecimal> zeroList = null; 
		
		// 凭证序列号
		String mainvou = "";
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		// 解析报文
		for (int i = 0; i < VoucherBodyList.size(); i++) {
			try {
				String mainFailReason = null;
				// 明细合计金额
				BigDecimal sumAmt = new BigDecimal("0.00");
				// VoucherBody
				Element element = (Element) VoucherBodyList.get(i);
				// 报文头与报文体凭证编号不一致导致签收异常
				String VoucherNo = element.attribute("VoucherNo").getText();//凭证编号,收款银行退款通知单号，按“3位银行编码||YYYYMMDD||4位序列号”生成
				// Voucher
				Element elementVoucher = (Element) element.selectSingleNode("Voucher");
				// 明细信息List
				List listDetail = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");
				//报文校验信息dto
				 VoucherVerifyDto verifydto = new VoucherVerifyDto();
				 VoucherVerify voucherVerify = new VoucherVerify();	
				/**
				 * 凭证信息
				 */
//				String Attach = ((Element) VoucherBodyList.get(i)).elementText("Attach");// 附加信息
				String Id = elementVoucher.elementText("Id");// 收款银行退款通知Id
				String VouDate = elementVoucher.elementText("VouDate"); // 凭证日期
				String OriBillNo = elementVoucher.elementText("OriBillNo");// 原业务单据号,原批量业务支付明细主单的凭证号
				String PayTypeCode = elementVoucher.elementText("PayTypeCode"); // 支付方式编码  11-直接支付　12-授权支付　91-实拨
				String PayTypeName = elementVoucher.elementText("PayTypeName");// 支付方式名称
				String OriginalVoucherNo = elementVoucher.elementText("OriginalVoucherNo");// 主支付凭证编号,直接支付或授权支付凭证编号
				String PayAmt = elementVoucher.elementText("PayAmt");// 退款汇总金额,负金额
				String verifyPayAmt = PayAmt;
				String payDictateNo = elementVoucher.elementText("PayDictateNo");// 支付交易序号,商行通过现代化支付系统汇划资金时产生
				String payMsgNo = elementVoucher.elementText("PayMsgNo");// 支付报文编号,大额支付系统为100；小额支付系统为001
				String payEntrustDate = elementVoucher.elementText("PayEntrustDate");// 支付委托日期
				String paySndBnkNo = elementVoucher.elementText("PaySndBnkNo");// 支付发起行行号
				String Remark = elementVoucher.elementText("Remark");// 摘要，柜员根据需要录入
				String PayDate = elementVoucher.elementText("PayDate");//实际退款日期
				String Hold1 = elementVoucher.elementText("Hold1");// 预留字段1
				String Hold2 = elementVoucher.elementText("Hold2");// 预留字段2
				/**
				 * 20150423上海新增字段
				 * FundTypeCode	资金性质编码  S_EXT2
				 * FundTypeName	资金性质名称	 S_EXT3
				 */
				String FundTypeCode = elementVoucher.elementText("FundTypeCode");
				String FundTypeName = elementVoucher.elementText("FundTypeName");
				
				/**
				 * 组装TfPaybankRefundmainDto对象
				 **/
				maindto = new TfPaybankRefundmainDto();
				mainvou = dealnos.get(VoucherNo);// 获取序列号
				maindto.setIvousrlno(Long.valueOf(mainvou));// 凭证流水号
				maindto.setSorgcode(ls_OrgCode);//核算主体代码
				maindto.setStrecode("");//接口规范中没有相应的国库代码,首先默认为空字符串,在下面用索引表的国库为其重新赋值
				maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);//状态 处理中
				maindto.setSdemo("");//描述
				maindto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));// 系统时间
				maindto.setSid(Id);// 收款银行退款通知Id
				maindto.setSadmdivcode(AdmDivCode);//行政区划代码
				maindto.setSstyear(StYear);// 业务年度
				maindto.setSvtcode(VtCode);//凭证类型编号
				maindto.setSvoudate(VouDate);// 凭证日期
				maindto.setSvoucherno(VoucherNo);//凭证编号,收款银行退款通知单号，按“3位银行编码||YYYYMMDD||4位序列号”生成
				maindto.setSoribillno(OriBillNo);//原业务单据号,原批量业务支付明细主单的凭证号
				maindto.setSpaytypecode(PayTypeCode);// 支付方式编码　11-直接支付　12-授权支付　91-实拨
				if(StringUtils.isNotBlank(PayTypeCode)&&PayTypeCode.equals(StateConstant.DIRECT_PAY_CODE)){
					maindto.setSpaytypecode(MsgConstant.directPay);				
				}				
				maindto.setSpaytypename(PayTypeName);// 支付方式名称
				maindto.setSoriginalvoucherno(OriginalVoucherNo);// 主支付凭证编号,直接支付或授权支付凭证编号
				maindto.setNpayamt(new BigDecimal(PayAmt).abs());// 退款汇总金额,负金额
				maindto.setSpaydictateno(payDictateNo);// 大额支付退款交易序号
				maindto.setSpaymsgno(payMsgNo);// 支付报文编号
				maindto.setSpayentrustdate(payEntrustDate);// 支付委托日期
				maindto.setSpaysndbnkno(paySndBnkNo);// 支付发起行行号
				maindto.setSremark(Remark);// 摘要，柜员根据需要录入
				maindto.setSpaydate(PayDate);//实际退款日期
				maindto.setSext1(listDetail.size()+"");
				maindto.setShold1(Hold1);// 预留字段1
				maindto.setShold2(Hold2);// 预留字段2
				maindto.setSext2(FundTypeCode);
				maindto.setSext3(FundTypeName);
				
				if(StringUtils.isBlank(FundTypeCode) || StringUtils.isBlank(FundTypeName) || StringUtils.isBlank(Remark)){
					mainFailReason = "凭证编号:" + VoucherNo + "主单中FundTypeCode、FundTypeName或Remark为空！";
				}
				
				
				vDto = new TvVoucherinfoDto();
				vDto.setSdealno(mainvou);
				vDto = (TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
				vDto.setShold2(maindto.getSpaytypecode());//该字段充当支付方式
				/**
				 * 主单中有失败原因为空的记录,校验
				 */
				if(StringUtils.isNotBlank(mainFailReason)){
					voucher.voucherComfail(vDto.getSdealno(), mainFailReason);
					continue;
				}
				
				
				
				
				
				
				
				//负金额list
				moneyList = new ArrayList<BigDecimal>();
				
				//零金额list @author 张会斌
				zeroList = new ArrayList<BigDecimal>();
				
				//退款原因校验@曹艳国
				String  bkReMsg = null ;
				
				//子表明细Id集合
				List<String>  subDtoIdList = new ArrayList<String>();
				
				/**
				 * 组装TfPaybankRefundsubDto对象
				 */
				subDtoList = new ArrayList<IDto>();
				for (int j = 0; j < listDetail.size(); j++) {
					Element elementDetail = (Element) listDetail.get(j);
					String subId = elementDetail.elementText("Id");// 退款明细Id,原批量业务支付明细的明细Id
					String VoucherBillId = elementDetail.elementText("VoucherBillId");// 收款银行退款通知Id
					String PayAcctNo = elementDetail.elementText("PayAcctNo");// 原付款人账号
					String PayAcctName = elementDetail.elementText("PayAcctName");// 原付款人名称
					String PayAcctBankName = elementDetail.elementText("PayAcctBankName");// 原付款人银行
					String PayeeAcctNo = elementDetail.elementText("PayeeAcctNo");// 原收款人账号
					String PayeeAcctName = elementDetail.elementText("PayeeAcctName");//原收款人名称
					String PayeeAcctBankName = elementDetail.elementText("PayeeAcctBankName");// 原收款人银行名称
					String PayeeAcctBankNo = elementDetail.elementText("PayeeAcctBankNo");// 原收款人银行行号
					String sPayAmt = elementDetail.elementText("PayAmt");// 退款金额
					String subRemark = elementDetail.elementText("Remark");// 退款原因
					String sdetailHold1 = elementDetail.elementText("Hold1");// 预留字段1
					String sdetailHold2 = elementDetail.elementText("Hold2");// 预留字段2
					String sdetailHold3 = elementDetail.elementText("Hold3");// 预留字段3
					String sdetailHold4 = elementDetail.elementText("Hold4");// 预留字段4
					//上海新增两个字段
					String expFuncCode = elementDetail.elementText("ExpFuncCode");//功能分类科目编码S_EXT1
					String expFuncName = elementDetail.elementText("ExpFuncName");//功能分类科目名称S_EXT2
					String bkyt = elementDetail.elementText("bkyt");// 拨款用途	S_EXT3
					TfPaybankRefundsubDto subdto = new TfPaybankRefundsubDto();
					subdto.setSext1(expFuncCode);
					subdto.setSext2(expFuncName);
					subdto.setSext3(bkyt);
					subdto.setIvousrlno(Long.valueOf(mainvou));// 凭证流水号
					subdto.setIseqno(Long.valueOf(j+1));//明细号
					subdto.setSid(subId);// 退款明细Id,原批量业务支付明细的明细Id
					subdto.setSvoucherbillid(VoucherBillId);// 收款银行退款通知Id
					subdto.setSpayacctno(PayAcctNo);// 原付款人账号
					subdto.setSpayacctname(PayAcctName);// 原付款人名称
					subdto.setSpayacctbankname(PayAcctBankName);// 原付款人银行
					subdto.setSpayeeacctno(PayeeAcctNo);// 原收款人账号
					subdto.setSpayeeacctname(PayeeAcctName);//原收款人名称
					subdto.setSpayeeacctbankno(PayeeAcctBankNo);// 原收款人银行行号
					subdto.setSpayeeacctbankname(PayeeAcctBankName);// 原收款人银行名称
					BigDecimal subPayAmt = new BigDecimal(sPayAmt);
					
					//正金额列表
					if(subPayAmt.signum()==1){
						moneyList.add(subPayAmt);
					}
					//零金额列表 @author 张会斌
					if(subPayAmt.equals(BigDecimal.ZERO)){
						zeroList.add(subPayAmt);
					}
					//退款原因M判断
					if (null ==subRemark||subRemark.trim().length()==0) {
						bkReMsg="凭证编号:" + VoucherNo +",明细ID:" + subId + "中Remark不能为空！";
						break;
					}
					//拨款用途M判断
					if(StringUtils.isBlank(bkyt)){
						bkReMsg="凭证编号:" + VoucherNo +",明细ID:" + subId + "中bkyt不能为空！";
						break;
					}
					
					subdto.setNpayamt(subPayAmt.abs());// 退款金额
					subdto.setSremark(subRemark);// 退款原因
					subdto.setShold1(sdetailHold1);// 预留字段1
					subdto.setShold2(sdetailHold2);// 预留字段2
					subdto.setShold3(sdetailHold3);// 预留字段3
					subdto.setShold4(sdetailHold4);// 预留字段4
					
					sumAmt = sumAmt.add(BigDecimal.valueOf(Double.valueOf(sPayAmt)));
					subDtoList.add(subdto);
					subDtoIdList.add(subdto.getSid());
				}
				/**
				 * 明细中有失败原因为空的记录,校验
				 */
				if(bkReMsg != null && bkReMsg.length()>0){
					voucher.voucherComfail(vDto.getSdealno(), bkReMsg);
					continue;
				}
				
				
				maindto.setSorgcode(vDto.getSorgcode());
				maindto.setStrecode(vDto.getStrecode());
				maindto.setSadmdivcode(vDto.getSadmdivcode());
				maindto.setSfinorgcode(ls_FinOrgCode);//财政机构代码
				maindto.setSdealno(mainvou.substring(8, 16));//交易流水号
				maindto.setScommitdate(TimeFacade.getCurrentStringTime());//委托日期
				maindto.setSfilename(ls_FileName);//文件名
				maindto.setSpackageno("");
				maindto.setSdemo(vDto.getSattach());
				maindto.setSbackflag("0");
				maindto.setSext1(subDtoList.size()+"");//明细退回笔数
				/**
				 * 组装verifydto,进行报文校验
				 */
				verifydto.setTrecode(maindto.getStrecode());
				verifydto.setFinorgcode(ls_FinOrgCode);
				verifydto.setVoucherno(VoucherNo);
				verifydto.setVoudate(VouDate);
				verifydto.setPaytypecode(PayTypeCode);
				verifydto.setPayVoucherNo(OriBillNo);
				verifydto.setPaybankname(payDictateNo);//用银行代码代传支付交易序号
				verifydto.setAgentAcctNo(payMsgNo);//用收款银行账号代传支付报文编号
				verifydto.setAgentAcctName(payEntrustDate);//用收款银行账户名称代传支付委托日期
				verifydto.setClearAcctNo(paySndBnkNo);//用付款账号代传支付发起行行号
				verifydto.setClearAcctName(PayDate);//用付款账号名称代传实际退款日期
				//增加年度，总金额的校验 by renqingbin
				verifydto.setOfyear(StYear);
				verifydto.setFamt(verifyPayAmt);
				String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_2252);
				
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
				 *校验子表中是否有正金额
				 *
				 */
				if(moneyList.size() > 0){
					String errMsg="明细信息中包含有正金额的数据!";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				
				
				
				/**
				 *校验主表金额是否与子表金额相等
				 * 
				 */
				if (new BigDecimal(PayAmt).compareTo(sumAmt) != 0) {
					String errMsg = "主单金额与明细累计金额不相等，主单金额：" + PayAmt
							+ " 明细累计金额： " + sumAmt;
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
				 * 校验明细信息合法性
				 * @author 张会斌
				 */
				if(null!=subDtoList&&subDtoList.size()>0){
					String returnMsg = voucherVerify.checkVerify(subDtoList,MsgConstant.VOUCHER_NO_2252);
					if(StringUtils.isNotBlank(returnMsg)){
						voucher.voucherComfail(vDto.getSdealno(), returnMsg);
						continue;
					}
					returnMsg = voucherVerify.checkVerify(subDtoList,maindto);
					if(StringUtils.isNotBlank(returnMsg)){
						voucher.voucherComfail(vDto.getSdealno(), returnMsg);
						continue;
					}
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
				TfPaybankRefundsubDto[] subDtos = new TfPaybankRefundsubDto[subDtoList.size()];
				subDtos = (TfPaybankRefundsubDto[]) subDtoList.toArray(subDtos);
				DatabaseFacade.getODB().create(maindto);
				DatabaseFacade.getODB().create(subDtos);
				vDto.setIcount(subDtoList.size());
			}catch(Exception e){
				logger.error(e);
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

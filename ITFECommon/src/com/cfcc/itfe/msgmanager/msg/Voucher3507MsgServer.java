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
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoSubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

public class Voucher3507MsgServer extends AbstractMsgManagerServer {
	private static Log logger = LogFactory.getLog(Voucher3507MsgServer.class);
	private Voucher voucher;
	
	/**
	 * 接收财政发起的清算明细对账
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		super.dealMsg(eventContext);
		MuleMessage muleMessage = eventContext.getMessage();
		String voucherXml = (String) muleMessage.getPayload();
		String ls_OrgCode = (String) muleMessage.getProperty("orgCode");
		HashMap<String,String> dealnos=(HashMap<String,String>)muleMessage.getProperty("dealnos");
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("解析清算明细对账3507报文出现错误！",e);
			throw new ITFEBizException("解析清算明细对账3507报文出现错误！",e);
			
		}
		List VoucherBodyList  = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
		
		Element elemt  = (Element)VoucherBodyList.get(0);
		String AdmDivCode  = elemt.attribute("AdmDivCode").getText();
		String StYear  = elemt.attribute("StYear").getText();
		String VtCode  = elemt.attribute("VtCode").getText();
		
		//预算单位代码list
		List agencyCodeList = null;
		//预算科目代码list
		ArrayList<String> expFuncCodeList = null; 
		
		TfReconcilePayinfoMainDto mainDto = null;
		List subDtoList=null;
		TvVoucherinfoDto indexInfoDto=null;
		List lists=new ArrayList();
		List list=null;
		
		/**按照数据库中字段顺序填写*/
		for(int i=0;i<VoucherBodyList.size();i++){
			//VoucherBody
			Element element  = (Element)VoucherBodyList.get(i);
			//凭证编号
			String voucherNo = element.attribute("VoucherNo").getText();
			//Voucher
			Element elementVoucher  = (Element)element.selectSingleNode("Voucher");
			//明细信息List
			List listDetail  = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");
			mainDto = new TfReconcilePayinfoMainDto();
			
			//凭证流水号 I_VOUSRLNO
			String srlNo = dealnos.get(voucherNo);
			Long vousrlno = Long.valueOf(srlNo);
			mainDto.setIvousrlno(vousrlno);
			//核算主体代码 S_ORGCODE
			mainDto.setSorgcode(ls_OrgCode);
			//国库主体代码 S_TRECODE
			String treCode = elementVoucher.elementText("TreCode");
			mainDto.setStrecode(treCode);
			//状态
			mainDto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);
			//描述
			mainDto.setSdemo(null);
			//系统更新时间 TS_SYSUPDATE
			mainDto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));
			//包流水号
			mainDto.setSpackageno(null);
			//行政区划代码 S_ADMDIVCODE
			String admDivCode = elementVoucher.elementText("AdmDivCode");
			mainDto.setSadmdivcode(admDivCode);
			//业务年度 S_STYEAR
			String stYear = elementVoucher.elementText("StYear");
			mainDto.setSstyear(stYear);
			//凭证类型编号 S_VTCODE
			String vtCode = elementVoucher.elementText("VtCode");
			mainDto.setSvtcode(vtCode);
			//凭证日期 S_VOUDATE
			String vouDate = elementVoucher.elementText("VouDate");
			mainDto.setSvoudate(vouDate);
			//凭证号 S_VOUCHERNO
			mainDto.setSvoucherno(voucherNo);
			//对账单号
			String voucherCheckNo = elementVoucher.elementText("VoucherCheckNo");
			mainDto.setSvouchercheckno(voucherCheckNo);
			//子包总数
			String childPackNum = elementVoucher.elementText("ChildPackNum");
			mainDto.setSchildpacknum(childPackNum);
			//本包序号
			String curPackNo = elementVoucher.elementText("CurPackNo");
			mainDto.setScurpackno(curPackNo);
			//人民银行编码
			String clearBankCode = elementVoucher.elementText("ClearBankCode");
			mainDto.setSclearbankcode(clearBankCode);
			//人民银行名称
			String clearBankName = elementVoucher.elementText("ClearBankName");
			mainDto.setSclearbankname(clearBankName);
			//清算账号
			String clearAccNo = elementVoucher.elementText("ClearAccNo");
			mainDto.setSclearaccno(clearAccNo);
			//清算账户名称
			String clearAccNanme = elementVoucher.elementText("ClearAccNanme");
			mainDto.setSclearaccnanme(clearAccNanme);
			//对账起始日期
			String beginDate = elementVoucher.elementText("BeginDate");
			mainDto.setSbegindate(beginDate);
			//对账终止日期
			String endDate = elementVoucher.elementText("EndDate");
			mainDto.setSenddate(endDate);
			//总笔数
			String allNum = elementVoucher.elementText("AllNum");
			mainDto.setSallnum(allNum);
			//总金额
			String allAmt = elementVoucher.elementText("AllAmt");
			mainDto.setNallamt(new BigDecimal(allAmt));
			//对帐结果
			mainDto.setSxcheckresult(null);
			//不符笔数
			mainDto.setSxdiffnum(null);
			subDtoList = new ArrayList<TfReconcilePayinfoSubDto>();
			//预算单位代码list
			agencyCodeList = new ArrayList<String>();
			//预算科目代码list
			expFuncCodeList = new ArrayList<String>(); 
			
			//封装明细信息
			for(int j=0;j<listDetail.size();j++){
				Element elementDetail  = (Element)listDetail.get(j);
				TfReconcilePayinfoSubDto subdto = new TfReconcilePayinfoSubDto();
				
				//凭证流水号 I_VOUSRLNO
				subdto.setIvousrlno(vousrlno);
				//明细序号 I_SEQNO
				subdto.setIseqno(Long.valueOf(VoucherUtil.getGrantSequence()));
				//序号 S_ID
				String id = elementDetail.elementText("Id");
				subdto.setSid(id);
				//清算单号
				String payAgentBillNo = elementDetail.elementText("PayAgentBillNo");
				subdto.setSpayagentbillno(payAgentBillNo);
				//清算明细ID
				String payDetailId = elementDetail.elementText("PayDetailId");
				subdto.setSpaydetailid(payDetailId);
				//一级预算单位编码
				String supDepCode = elementDetail.elementText("SupDepCode");
				subdto.setSsupdepcode(supDepCode);
				//一级预算单位名称
				String supDepName = elementDetail.elementText("SupDepName");
				subdto.setSsupdepname(supDepName);
				//资金性质编码
				String fundTypeCode = elementDetail.elementText("FundTypeCode");
				subdto.setSfundtypecode(fundTypeCode);
				//资金性质名称 S_FUNDTYPENAME
				String fundTypeName = elementDetail.elementText("FundTypeName");
				subdto.setSfundtypename(fundTypeName);
				//代理银行编码
				String payBankCode = elementDetail.elementText("PayBankCode");
				subdto.setSpaybankcode(payBankCode);
				//代理银行名称
				String payBankName = elementDetail.elementText("PayBankName");
				subdto.setSpaybankname(payBankName);
				//代理银行行号
				String payBankNo = elementDetail.elementText("PayBankNo");
				subdto.setSpaybankno(payBankNo);
				//支出功能分类科目编码
				String expFuncCode = elementDetail.elementText("ExpFuncCode");
				subdto.setSexpfunccode(expFuncCode);
				//支出功能分类科目名称
				String expFuncName = elementDetail.elementText("ExpFuncName");
				subdto.setSexpfuncname(expFuncName);
				//收支管理编码
				String proCatCode = elementDetail.elementText("ProCatCode");
				subdto.setSprocatcode(proCatCode);
				//收支管理名称
				String proCatName = elementDetail.elementText("ProCatName");
				subdto.setSprocatname(proCatName);
				//支付方式编码
				String payTypeCode = elementDetail.elementText("PayTypeCode");
				subdto.setSpaytypecode(payTypeCode);
				//支付方式名称
				String payTypeName = elementDetail.elementText("PayTypeName");
				subdto.setSpaytypename(payTypeName);
				//支付金额
				String payAmt = elementDetail.elementText("PayAmt");
				subdto.setNpayamt(new BigDecimal(payAmt));
				//对帐结果
				subdto.setSxcheckresult(null);
				//不符原因
				subdto.setSxcheckreason(null);
				//预留字段1 S_HOLD1
				String dtlhold1 = elementDetail.elementText("Hold1");
				subdto.setShold1(dtlhold1);
				//预留字段2 S_HOLD2
				String dtlhold2 = elementDetail.elementText("Hold2");
				subdto.setShold2(dtlhold2);
				//预留字段3 S_HOLD3
				String dtlhold3 = elementDetail.elementText("Hold3");
				subdto.setShold3(dtlhold3);
				//预留字段1 S_HOLD1
				String dtlhold4 = elementDetail.elementText("Hold4");
				subdto.setShold4(dtlhold4);
				
				agencyCodeList.add(treCode+supDepCode);
				expFuncCodeList.add(expFuncCode);
				
				subdto.setSext1(null);
				subdto.setSext2(null);
				subdto.setSext3(null);
				subdto.setSext4(null);
				subdto.setSext5(null);
				
				subDtoList.add(subdto);
			}
			
			try {
				indexInfoDto=new TvVoucherinfoDto();
				indexInfoDto.setSdealno(srlNo);
				indexInfoDto=(TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(indexInfoDto).get(0);
				
				//报文校验
				VoucherVerifyDto verifydto = new VoucherVerifyDto();
				VoucherVerify voucherVerify = new VoucherVerify();	
				verifydto.setTrecode(treCode);
				verifydto.setVoucherno(voucherNo);
				verifydto.setVoudate(vouDate);
				verifydto.setOfyear(stYear);
				
				verifydto.setOfyear(StYear);
				verifydto.setFamt(allAmt);
				
				String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_3507);
				//返回错误信息签收失败
				if(returnmsg != null){
					voucher.voucherComfail(indexInfoDto.getSdealno(), returnmsg);
					continue;
				}
				
				/**
				 *明细条数不能超过500
				 * 
				 */
				if (subDtoList.size() > 499) {
					String errMsg = "明细条数必须小于500!";
					voucher.voucherComfail(indexInfoDto.getSdealno(), errMsg);
					continue;
				}
				
			} catch (JAFDatabaseException e1) {
				logger.error("校验清算信息对账失败", e1);
				throw new ITFEBizException("校验清算信息对账失败");
			} catch (ValidateException e1) {
				logger.error("校验清算信息对账失败", e1);
				throw new ITFEBizException("校验清算信息对账失败");
			}
			
			/**
			 * 保存清算对账信息
			 * 
			 */
			try {
				DatabaseFacade.getODB().create(mainDto);
				TfReconcilePayinfoSubDto[] subDtos = (TfReconcilePayinfoSubDto[])(subDtoList.toArray(new TfReconcilePayinfoSubDto[subDtoList.size()]));
				DatabaseFacade.getODB().create(subDtos);
			} catch (JAFDatabaseException e1) {
				logger.error("保存清算信息对账数据库异常", e1);
				throw new ITFEBizException("保存清算信息对账数据库异常");
			}
			
			//签收成功
			try{
				voucher.voucherConfirmSuccess(indexInfoDto.getSdealno());
			}catch(ITFEBizException e){
				logger.error(e);
				VoucherException.saveErrInfo(indexInfoDto.getSvtcode(), e);
				continue;
			}
		
			list=new ArrayList();
			list.add(mainDto);
			list.add(indexInfoDto);			
			list.add(expFuncCodeList);
			list.add(agencyCodeList);
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
	}

	public Voucher getVoucher() {
		return voucher;
	}

	public void setVoucher(Voucher voucher) {
		this.voucher = voucher;
	}
	
}

package com.cfcc.itfe.msgmanager.msg;

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

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TfUnitrecordmainDto;
import com.cfcc.itfe.persistence.dto.TfUnitrecordsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

public class Voucher5951MsgServer extends AbstractMsgManagerServer {
	
	private static Log logger = LogFactory.getLog(Voucher5951MsgServer.class);
	private Voucher voucher;
	
	/**
	 * 接收单位清册业务，保存业务表
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
			logger.error("解析单位清册凭证5951报文出现错误！",e);
			throw new ITFEBizException("解析单位清册凭证5951报文出现错误！",e);
			
		}
		List VoucherBodyList  = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
		TfUnitrecordmainDto mainDto = null;
		List subDtoList=null;
		TvVoucherinfoDto indexInfoDto=null;
		
		for(int i=0;i<VoucherBodyList.size();i++){
			//VoucherBody
			Element element  = (Element)VoucherBodyList.get(i);
			//凭证编号
			String voucherNo = element.attribute("VoucherNo").getText();
			//Voucher
			Element elementVoucher  = (Element)element.selectSingleNode("Voucher");
			//明细信息List
			List listDetail  = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");
			mainDto = new TfUnitrecordmainDto();
			
			//凭证流水号 I_VOUSRLNO
			String srlNo = dealnos.get(voucherNo);
			Long vousrlno = Long.valueOf(srlNo);
			mainDto.setIvousrlno(vousrlno);
			//核算主体代码 S_ORGCODE
			mainDto.setSorgcode(ls_OrgCode);
			//系统更新时间 TS_SYSUPDATE
			mainDto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));
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
			//国库主体代码 S_TRECODE
			String treCode = elementVoucher.elementText("TreCode");
			mainDto.setStrecode(treCode);
			//财政机关代码 S_FINORGCODE
			String finOrgCode = elementVoucher.elementText("FinOrgCode");
			mainDto.setSfinorgcode(finOrgCode);
			//代理银行编码 S_PAYBANKCODE
			String payBankCode = elementVoucher.elementText("PayBankCode");
			mainDto.setSpaybankcode(payBankCode);
			//代理银行名称 S_PAYBANKNAME
			String payBankName = elementVoucher.elementText("PayBankName");
			mainDto.setSpaybankname(payBankName);
			//全量标志 S_ALLFLAG
			String allFlag = elementVoucher.elementText("AllFlag");
			mainDto.setSallflag(allFlag);
			//预留字段1 S_HOLD1
			String hold1 = elementVoucher.elementText("Hold1");
			mainDto.setShold1(hold1);
			//预留字段2 S_HOLD2
			String hold2 = elementVoucher.elementText("Hold2");
			mainDto.setShold2(hold2);
			
			subDtoList = new ArrayList<TfUnitrecordsubDto>();
			//封装明细信息
			for(int j=0;j<listDetail.size();j++){
				Element elementDetail  = (Element)listDetail.get(j);
				TfUnitrecordsubDto subdto = new TfUnitrecordsubDto();
				
				//凭证流水号 I_VOUSRLNO
				subdto.setIvousrlno(vousrlno);
				//明细序号 I_SEQNO
				subdto.setIseqno(Long.valueOf(VoucherUtil.getGrantSequence()));
				//序号 S_ID
				String id = elementDetail.elementText("Id");
				subdto.setSid(id);
				//预算单位编码 S_AGENCYCODE
				String agencyCode = elementDetail.elementText("AgencyCode");
				subdto.setSagencycode(agencyCode);
				//预算单位名称 S_AGENCYNAME
				String agencyName = elementDetail.elementText("AgencyName");
				subdto.setSagencyname(agencyName);
				//资金性质编码 S_FUNDTYPECODE
				String fundTypeCode = elementDetail.elementText("FundTypeCode");
				subdto.setSfundtypecode(fundTypeCode);
				//资金性质名称 S_FUNDTYPENAME
				String fundTypeName = elementDetail.elementText("FundTypeName");
				subdto.setSfundtypename(fundTypeName);
				//单位账号 S_AGENCYACCONO
				String agencyAccoNo = elementDetail.elementText("AgencyAccoNo");
				subdto.setSagencyaccono(agencyAccoNo);
				//单位账户名称 S_AGENCYACCONAME
				String agencyAccoName = elementDetail.elementText("AgencyAccoName");
				subdto.setSagencyacconame(agencyAccoName);
				//单位帐户开户行 S_AGENCYBANKNAME
				String agencyBankName = elementDetail.elementText("AgencyBankName");
				subdto.setSagencybankname(agencyBankName);
				//账户状态 S_ACCSTATUS
				String accStatus = elementDetail.elementText("AccStatus");
				subdto.setSaccstatus(accStatus);
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
				verifydto.setFinorgcode(finOrgCode);
				verifydto.setVoucherno(voucherNo);
				verifydto.setVoudate(vouDate);
				verifydto.setOfyear(stYear);
				
				String returnmsg = voucherVerify.checkValid(verifydto, MsgConstant.VOUCHER_NO_5951);
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
				logger.error("校验清册业务错", e1);
				throw new ITFEBizException("校验清册业务错");
			} catch (ValidateException e1) {
				logger.error("校验清册业务错", e1);
				throw new ITFEBizException("校验清册业务错");
			}
			
			/**
			 * 如果下发的是全量数据，则先清空数据表，再保存数据，如果为增量数据，则按照预算单位代码进行追加更新
			 * 根据行政区划代码删除
			 */
			SQLExecutor sqlExe = null;
			try {
				if(StateConstant.ALLFLAG_FULL.equals(allFlag)){
					String sql = "DELETE FROM TF_UNITRECORDSUB WHERE I_VOUSRLNO IN (SELECT I_VOUSRLNO FROM TF_UNITRECORDMAIN WHERE S_ADMDIVCODE = ? and S_PAYBANKCODE = ? )";
					sqlExe = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
					sqlExe.addParam(admDivCode);
					sqlExe.addParam(payBankCode);
					sqlExe.runQuery(sql,TfUnitrecordsubDto.class);
					
					sql = "DELETE FROM TF_UNITRECORDMAIN WHERE S_ADMDIVCODE = ? and S_PAYBANKCODE = ? ";
					sqlExe.clearParams();
					sqlExe.addParam(admDivCode);
					sqlExe.addParam(payBankCode);
					sqlExe.runQueryCloseCon(sql, TfUnitrecordmainDto.class);
					
					DatabaseFacade.getODB().create(mainDto);
					TfUnitrecordsubDto[] subDtos = (TfUnitrecordsubDto[])(subDtoList.toArray(new TfUnitrecordsubDto[subDtoList.size()]));
					DatabaseFacade.getODB().create(subDtos);
				}else{
					DatabaseFacade.getODB().create(mainDto);
					TfUnitrecordsubDto[] subDtos = (TfUnitrecordsubDto[])(subDtoList.toArray(new TfUnitrecordsubDto[subDtoList.size()]));
					DatabaseFacade.getODB().create(subDtos);
				}
			} catch (JAFDatabaseException e) {
				logger.error("保存清册业务数据错！", e);
				throw new ITFEBizException("保存清册业务数据错");
			}finally{
				if(sqlExe != null){
					sqlExe.closeConnection();
				}
			}
			
			//签收成功
			try{
				voucher.voucherConfirmSuccess(indexInfoDto.getSdealno());
			}catch(ITFEBizException e){
				logger.error(e);
				VoucherException.saveErrInfo(indexInfoDto.getSvtcode(), e);
				continue;
			}
		}
	}

	public Voucher getVoucher() {
		return voucher;
	}

	public void setVoucher(Voucher voucher) {
		this.voucher = voucher;
	}
	
}

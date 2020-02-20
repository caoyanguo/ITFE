package com.cfcc.itfe.msgmanager.msg;


import java.util.HashMap;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

public class Voucher5510MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher5510MsgServer.class);
	private Voucher voucher;
	/**
	 * 解析清算额度对帐回执5510
	 * @author zhangliang
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap<String,String> dealnos=(HashMap<String,String>)eventContext.getMessage().getProperty("dealnos");
		MuleMessage muleMessage = eventContext.getMessage();
		String voucherXml = (String) muleMessage.getPayload();
		Document fxrDoc = null;
		SQLExecutor execDetail = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
			List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
			String mainXCheckResult = null;//对帐结果0-相符 1-不符
			String mainXDiffNum = null;//主单不符笔数
			String subXCheckResult = null;//明细对帐结果0-相符 1-不符
			String subXCheckReason = null;//明细对帐原因
			String subId = null;//子表唯一标示id
			String voucherNo = null;//主单凭证号
			String voucherCheckNo = null;
			String childPackNum = null;
			String curPackNo = null;
			String sql = null;
			for (int i = 0; i < VoucherBodyList.size(); i++) {
				try {
					if(execDetail==null)
						execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
					Element element = (Element) VoucherBodyList.get(i);
					voucherNo = element.attribute("VoucherNo").getText();//凭证编号,收款银行退款通知单号，按“3位银行编码||YYYYMMDD||4位序列号”生成
					Element elementVoucher = (Element) element.selectSingleNode("Voucher");
					List listDetail = null;
					if(elementVoucher.selectSingleNode("DetailList")!=null)
						listDetail = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");
					voucherCheckNo = elementVoucher.elementText("VoucherCheckNo");//原对帐单号
					childPackNum = elementVoucher.elementText("ChildPackNum");//原包总数
					curPackNo = elementVoucher.elementText("CurPackNo");//本包序号
					mainXCheckResult = elementVoucher.elementText("XCheckResult");//对帐结果
					mainXDiffNum = elementVoucher.elementText("XDiffNum");//不符笔数
					if(ITFECommonConstant.PUBLICPARAM.contains(",bussreport=insert,"))
					{
						sql = "update TF_RECONCILE_PAYQUOTA_MAIN set S_XCHECKRESULT=?,S_XDIFFNUM=? where I_VOUSRLNO=(SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_ATTACH=? and S_HOLD1=? and S_HOLD2=? and S_VTCODE=? and S_STATUS=?)";
						execDetail.addParam(mainXCheckResult);
						execDetail.addParam(mainXDiffNum);
						execDetail.addParam(voucherCheckNo);
						execDetail.addParam(childPackNum);
						execDetail.addParam(curPackNo);
						execDetail.addParam(MsgConstant.VOUCHER_NO_3510);
						execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
						execDetail.runQuery(sql);
					}
					sql = "update TV_VOUCHERINFO set S_STATUS=?,S_DEMO=?"
					+" where S_ATTACH=? and S_HOLD1=? and S_HOLD2=? and S_VTCODE=? and S_STATUS=?";//+"',S_RECVTIME='"+new Timestamp(new java.util.Date().getTime())
					execDetail.addParam(DealCodeConstants.VOUCHER_READRETURN);
					execDetail.addParam(("0".equals(mainXCheckResult)?"对账成功":"对账失败"));
					execDetail.addParam(voucherCheckNo);
					execDetail.addParam(childPackNum);
					execDetail.addParam(curPackNo);
					execDetail.addParam(MsgConstant.VOUCHER_NO_3510);
					execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
					execDetail.runQuery(sql);
					sql = "update TV_VOUCHERINFO set S_EXT2=("+"select S_VOUCHERNO from TV_VOUCHERINFO where S_ATTACH=? and S_HOLD1=? and S_HOLD2=? and S_VTCODE=?) where S_VOUCHERNO=?";
					execDetail.addParam(voucherCheckNo);
					execDetail.addParam(childPackNum);
					execDetail.addParam(curPackNo);
					execDetail.addParam(MsgConstant.VOUCHER_NO_3510);
					execDetail.addParam(voucherNo);
					execDetail.runQuery(sql);
					if(ITFECommonConstant.PUBLICPARAM.contains(",bussreport=insert,"))
					{
						if(listDetail!=null&&listDetail.size()>0)
						{
							for (int j = 0; j < listDetail.size(); j++) {
								Element elementDetail = (Element) listDetail.get(j);
								subId = elementDetail.elementText("Id");// 退款明细Id,原批量业务支付明细的明细Id
								subXCheckResult = elementDetail.elementText("XCheckResult");// 对帐结果
								subXCheckReason = elementDetail.elementText("XCheckReason");// 不符原因
								sql = "update TF_RECONCILE_PAYQUOTA_SUB set S_XCHECKRESULT=?,S_XCHECKREASON=? where I_SEQNO=? and I_VOUSRLNO=(SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_ATTACH=? and S_HOLD1=? and S_HOLD2=? and S_VTCODE=? and S_STATUS=?)";
								execDetail.addParam(subXCheckResult);
								execDetail.addParam(subXCheckReason);
								execDetail.addParam(subId);
								execDetail.addParam(voucherCheckNo);
								execDetail.addParam(childPackNum);
								execDetail.addParam(curPackNo);
								execDetail.addParam(MsgConstant.VOUCHER_NO_3510);
								execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
								execDetail.runQuery(sql);
							}
						}
					}
				}catch(Exception e){
					logger.error(e);
					voucher.voucherComfail(dealnos.get(voucherNo), "报文不规范："+e.getMessage());
					continue;
				}
				// 签收成功
				try {
					voucher.voucherConfirmSuccess(dealnos.get(voucherNo));
				} catch (ITFEBizException e) {
					logger.error(e);
					voucher.voucherComfail(dealnos.get(voucherNo), "签收成功发生异常调用签收失败："+e.getMessage());
					continue;
				}
			}
		} catch (Exception e) {
			logger.error("解析清算额度对帐回执5510报文出现错误！", e);
			throw new ITFEBizException("解析清算额度对帐回执5510报文出现错误！", e);

		}finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
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

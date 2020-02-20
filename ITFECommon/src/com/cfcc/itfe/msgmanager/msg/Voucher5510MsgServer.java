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
	 * ���������ȶ��ʻ�ִ5510
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
			String mainXCheckResult = null;//���ʽ��0-��� 1-����
			String mainXDiffNum = null;//������������
			String subXCheckResult = null;//��ϸ���ʽ��0-��� 1-����
			String subXCheckReason = null;//��ϸ����ԭ��
			String subId = null;//�ӱ�Ψһ��ʾid
			String voucherNo = null;//����ƾ֤��
			String voucherCheckNo = null;
			String childPackNum = null;
			String curPackNo = null;
			String sql = null;
			for (int i = 0; i < VoucherBodyList.size(); i++) {
				try {
					if(execDetail==null)
						execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
					Element element = (Element) VoucherBodyList.get(i);
					voucherNo = element.attribute("VoucherNo").getText();//ƾ֤���,�տ������˿�֪ͨ���ţ�����3λ���б���||YYYYMMDD||4λ���кš�����
					Element elementVoucher = (Element) element.selectSingleNode("Voucher");
					List listDetail = null;
					if(elementVoucher.selectSingleNode("DetailList")!=null)
						listDetail = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");
					voucherCheckNo = elementVoucher.elementText("VoucherCheckNo");//ԭ���ʵ���
					childPackNum = elementVoucher.elementText("ChildPackNum");//ԭ������
					curPackNo = elementVoucher.elementText("CurPackNo");//�������
					mainXCheckResult = elementVoucher.elementText("XCheckResult");//���ʽ��
					mainXDiffNum = elementVoucher.elementText("XDiffNum");//��������
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
					execDetail.addParam(("0".equals(mainXCheckResult)?"���˳ɹ�":"����ʧ��"));
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
								subId = elementDetail.elementText("Id");// �˿���ϸId,ԭ����ҵ��֧����ϸ����ϸId
								subXCheckResult = elementDetail.elementText("XCheckResult");// ���ʽ��
								subXCheckReason = elementDetail.elementText("XCheckReason");// ����ԭ��
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
					voucher.voucherComfail(dealnos.get(voucherNo), "���Ĳ��淶��"+e.getMessage());
					continue;
				}
				// ǩ�ճɹ�
				try {
					voucher.voucherConfirmSuccess(dealnos.get(voucherNo));
				} catch (ITFEBizException e) {
					logger.error(e);
					voucher.voucherComfail(dealnos.get(voucherNo), "ǩ�ճɹ������쳣����ǩ��ʧ�ܣ�"+e.getMessage());
					continue;
				}
			}
		} catch (Exception e) {
			logger.error("���������ȶ��ʻ�ִ5510���ĳ��ִ���", e);
			throw new ITFEBizException("���������ȶ��ʻ�ִ5510���ĳ��ִ���", e);

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

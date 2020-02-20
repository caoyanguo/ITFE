/**
 * 功能:接收申请报文接口(9000)（工行发起）
 */
package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

/**
 * @author wangyunbin
 * 
 */
public class Recv9000MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(RecvPayOutMsgServer.class);

	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		// 报文头信息CFX->HEAD
		String sorgcode = (String) headMap.get("SRC"); // 发起机构代码
		String sdescode = (String) headMap.get("DES");// 接收节点代码
		String msgNo = (String) headMap.get("MsgNo");// 报文编号
		String msgID = (String) headMap.get("MsgID");// 报文标识号
		String msgRef = (String) headMap.get("MsgRef");// 报文参考号
		String sdate = (String) headMap.get("WorkDate");// 工作日期

		/**
		 * 取得回执头信息
		 */
		HashMap batchHeadMap = (HashMap) msgMap.get("BatchHead9000");
		String AppDate = (String) batchHeadMap.get("AppDate"); // 申请日期
		int count = 0 ;
		//BigDecimal allamt = new BigDecimal("0.00");
		String sendno = null;
		String recvorg = sdescode;
		//String sdemo ="报文编号:"+MsgConstant.MSG_NO_9000+",原包流水:"+soripackno.trim()+",原发起机构代码"+sbillorg.trim()+",原委托日期:"+sorientrustDate.trim();
		/**
		 * 取得回执批量信息
		 */
		List<Object> BillCheck9000 = (List<Object>) msgMap.get("BillCheck9000");
		if (null == BillCheck9000 || BillCheck9000.size() == 0) {
			return;
		} else {
			count = BillCheck9000.size();
			try {
				//接收日志流水
				String _srecvno = StampFacade.getStampSendSeq("JS");
				String filepath = (String) eventContext.getMessage().getProperty(
						"XML_MSG_FILE_PATH");
				String stamp = TimeFacade.getCurrentStringTime();
				String ifsend = (String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER);
				// 记接收日志
				MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg, AppDate, msgNo,
						sorgcode, filepath, BillCheck9000.size(), new BigDecimal("0.00"), "", "", null,
						"", null, msgID,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, "申请对账9000重发" );
				
				for (int i = 0; i < count; i++) {
					HashMap tmpmap = (HashMap) BillCheck9000.get(i);
					String PayoutVouType = (String) tmpmap.get("PayoutVouType"); //凭证类型1-实拨 2- 退库 3商行划款4其他
					String TreCode = (String) tmpmap.get("TreCode"); // 国库主体代码
					String EntrustDate = (String) tmpmap.get("EntrustDate"); // 委托日期
					String PackNo = (String) tmpmap.get("PackNo"); //包流水号
					
					//根据ls_TreCode判断是不是为代理库
					HashMap<String, TsTreasuryDto> mapTreCode = SrvCacheFacade.cacheTreasuryInfo("");
					if(mapTreCode.containsKey(TreCode)){
						TsTreasuryDto treDto =  mapTreCode.get(TreCode);
						String ls_treattrib = treDto.getStreattrib();
						String ls_PbcBankCode = treDto.getSofcountrytrecode();//所属县国库代码，设置为代理银行行别代码
						//手工调用服务，发送报文信息
						
						Map map = new HashMap();
						MuleMessage message = new DefaultMuleMessage(map);
						message.setProperty("payoutVouType",PayoutVouType);
						message.setProperty("treCode",TreCode);
						message.setProperty("entrustDate", EntrustDate);
						message.setProperty("packNo", PackNo);
						message.setProperty("bankCode",ls_PbcBankCode);
						message.setProperty(MessagePropertyKeys.MSG_ORGCODE,treDto.getSorgcode());
						message.setProperty(MessagePropertyKeys.MSG_NO_KEY,MsgConstant.MSG_NO_8000);
						MuleClient client = new MuleClient();
						message = client.send("SendToSameCityMsg", message);
					}
				}
				
			} catch (SequenceException e) {
				String error = "接收9000报文处理失败！";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} catch (MuleException e) {
				logger.debug(e);
			} catch (JAFDatabaseException e) {
				logger.debug(e);
			}
		}
		eventContext.setStopFurtherProcessing(true);
		return;
	}

}


package com.cfcc.itfe.component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.config.MuleProperties;
import org.mule.api.lifecycle.Callable;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;


/**
 * 功能：定时生成对账报文接口(8000)（代理库发起）
 * @author wangyunbin
 * @time   2014-10-29
 *
 */
public class VoucherTimerSendForSameCity implements Callable{
	private static Log logger = LogFactory.getLog(VoucherTimerSendForSameCity.class);
	private String currentDate = TimeFacade.getCurrentStringTime();//对账起始日期：取系统当前日期
	
	public Object onCall(MuleEventContext eventContext) throws Exception {
		eventContext.getMessage().setStringProperty(MuleProperties.MULE_REPLY_TO_STOP_PROPERTY, "true");
		eventContext.getMessage().setReplyTo(null);		
		// 接收报文时调用配置的插件进行转化
		eventContext.transformMessage();
		logger.debug("======================== 定时生成同城对账报文任务开始========================");
		TsTreasuryDto treasuryDto = new TsTreasuryDto();
		treasuryDto.setStreattrib("2");
		//String ls_TreSQL = "SELECT S_TRECODE FROM TS_TREASURY WHERE S_TREATTRIB ='2'";
		try {
			List<TsTreasuryDto> treasuryDtoList =  CommonFacade.getODB().findRsByDto(treasuryDto);
			for(TsTreasuryDto tempDto:treasuryDtoList){
				String ls_BankCode  = tempDto.getSofcountrytrecode();
				//手工调用服务，发送报文信息
				eventContext.getMessage().setProperty(MessagePropertyKeys.MSG_NO_KEY, MsgConstant.MSG_NO_8000);
				eventContext.getMessage().setProperty("payoutVouType", "");
				eventContext.getMessage().setProperty("treCode", tempDto.getStrecode());
				eventContext.getMessage().setProperty("entrustDate", currentDate);
				eventContext.getMessage().setProperty("packNo", "");
				eventContext.getMessage().setProperty("bankCode",ls_BankCode);
				eventContext.getMessage().setProperty(MessagePropertyKeys.MSG_ORGCODE,tempDto.getSorgcode());
				
				Map map = new HashMap();
				MuleMessage message = new DefaultMuleMessage(map);
				message.setProperty(MessagePropertyKeys.MSG_NO_KEY,MsgConstant.MSG_NO_8000);
				MuleClient client = new MuleClient();
				message = client.send("SendToSameCityMsg", message);
				
				/*Map xmlMap = VoucherUtil.verificationOfAccount("", tempDto.getStrecode(), currentDate, "");
				int num = Integer.parseInt((String)((Map)((Map)((Map)xmlMap.get("cfx")).get("MSG")).get("BatchHead8000")).get("SendPackNum"));
				BigDecimal amt =  new BigDecimal((String)((Map)((Map)((Map)xmlMap.get("cfx")).get("MSG")).get("BatchHead8000")).get("SendPackAmt"));
				String ls_MsgId  = (String)((Map)((Map)xmlMap.get("cfx")).get("HEAD")).get("MsgID");
				MuleMessage message = new DefaultMuleMessage(new HashMap());
				message.setProperty(MessagePropertyKeys.MSG_NO_KEY,ls_BankCode+"-8000");
				
				// 写发送日志
				message.setProperty(MessagePropertyKeys.MSG_SEND_LOG_DTO,
							MsgLogFacade.writeSendLogWithResult(StampFacade.getStampSendSeq("FS"), null,
									tempDto.getSorgcode(),
									ITFECommonConstant.DES_NODE, TimeFacade
											.getCurrentStringTime(),
											MsgConstant.MSG_NO_8000, (String) eventContext
									.getMessage().getProperty("XML_MSG_FILE_PATH"), num, amt, "", tempDto.getStrecode(),
									null, "", null, ls_MsgId,
									DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
									StateConstant.MSG_SENDER_FLAG_0, null, MsgConstant.ITFE_AUTO_SEND+ls_BankCode+"-8000"));
				
				// 设置消息体
				message.setPayload(xmlMap);*/
			}
		} catch (JAFDatabaseException e) {
			logger.debug(e);
		} catch (ValidateException e) {
			logger.debug(e);
		}catch(Exception e){
			logger.error(e);
			VoucherException.saveErrInfo(MsgConstant.MSG_NO_9000, e);						
		}
		logger.debug("======================== 定时生成同城对账报文任务关闭 ========================");
		return eventContext.getMessage().getPayload();	
	}
}

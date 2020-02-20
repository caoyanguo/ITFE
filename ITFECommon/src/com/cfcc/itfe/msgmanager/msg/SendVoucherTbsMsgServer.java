package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TfFundAppropriationDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.transformer.Dto2MapForTbsVoucher;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

public class SendVoucherTbsMsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(SendVoucherTbsMsgServer.class);

	/**
	 * 报文信息处理
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		try {
			MuleMessage muleMessage = eventContext.getMessage();
			// 取得报文信息直接发送报文
			TvVoucherinfoDto tvVoucherinfoDto = null;
			TfFundAppropriationDto tmpFundAppropriationDto = null;
			String Spaybankcode = null;
			Map xmlmap = null;
			if (eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_DTO) instanceof TvVoucherinfoDto) {
				tvVoucherinfoDto = (TvVoucherinfoDto) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_DTO);
				Spaybankcode = tvVoucherinfoDto.getSpaybankcode();
				xmlmap = Dto2MapForTbsVoucher.tranfor(tvVoucherinfoDto);
			} else if (eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_DTO) instanceof TfFundAppropriationDto) {
				tmpFundAppropriationDto = (TfFundAppropriationDto) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_DTO);
				Spaybankcode = tmpFundAppropriationDto.getSpayeebankno();
				xmlmap = Dto2MapForTbsVoucher.tranfor(tmpFundAppropriationDto);
			}
			String msgid = (String) ((Map) (((Map) xmlmap.get("cfx")).get("HEAD"))).get("MsgID");
			// 设置消息体
			muleMessage.setProperty(MessagePropertyKeys.MSG_NO_KEY, MsgConstant.MSG_TBS_NO_1000 + "_VOUCHER_TBS");
			String _ssendno = StampFacade.getStampSendSeq("FS");
			// 记录资金报文日志
			Map<String, Object> headMap = (Map) (((Map) xmlmap.get("cfx")).get("HEAD"));
			Map<String, Object> batchHeadMap = (Map<String, Object>) ((Map) ((Map) xmlmap.get("cfx")).get("MSG")).get("BatchHead1000");
			List list = (List) ((Map) ((Map) xmlmap.get("cfx")).get("MSG")).get("BillSend1000");
			Map<String, Object> BillSend1000Map = (Map<String, Object>) list.get(0);

			// 写发送日志(当前发往代理行的所有报文，通过mule传递的参数都是TvVoucherinfoDto，所以记日志的发送机构代码时，直接取TvVoucherinfoDto中的机构代码)
			muleMessage.setProperty(
					MessagePropertyKeys.MSG_SEND_LOG_DTO,
					MsgLogFacade.writeSendLogWithResult(
									_ssendno,
									null,
									tvVoucherinfoDto.getSorgcode(),
									(String) headMap.get("DES"),
									TimeFacade.getCurrentStringTime(),
									MsgConstant.MSG_TBS_NO_1000,
									(String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"),
									1,
									tvVoucherinfoDto.getNmoney(),
									tvVoucherinfoDto.getSpackno(),
									tvVoucherinfoDto.getStrecode(),
									null,
									Spaybankcode,
									null,
									msgid,
									DealCodeConstants.DEALCODE_ITFE_SEND,
									null,
									null,
									(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER),
									null, null));

			if (null == tvVoucherinfoDto && tmpFundAppropriationDto != null) {
				muleMessage.setPayload(xmlmap);
				return;
			}
			TfFundAppropriationDto tfFundAppropriationDto = new TfFundAppropriationDto();
			tfFundAppropriationDto.setSid(tvVoucherinfoDto.getSdealno());
			tfFundAppropriationDto.setSmsgid((String) headMap.get("MsgID"));
			tfFundAppropriationDto.setSmsgno((String) headMap.get("MsgNo"));
			tfFundAppropriationDto.setSmsgref((String) headMap.get("MsgRef"));
			tfFundAppropriationDto.setSbillorg((String) batchHeadMap.get("BillOrg"));
			tfFundAppropriationDto.setSentrustdate((String) batchHeadMap.get("EntrustDate"));
			tfFundAppropriationDto.setSpackno((String) batchHeadMap.get("PackNo"));
			tfFundAppropriationDto.setStrecode((String) batchHeadMap.get("TreCode"));
			tfFundAppropriationDto.setIallnum(Integer.valueOf((String) batchHeadMap.get("AllNum")));
			tfFundAppropriationDto.setNallamt(new BigDecimal((String) batchHeadMap.get("AllAmt")));
			tfFundAppropriationDto.setSpayoutvoutype((String) batchHeadMap.get("PayoutVouType"));
			tfFundAppropriationDto.setStrano((String) BillSend1000Map.get("TraNo"));
			tfFundAppropriationDto.setSvouno((String) BillSend1000Map.get("VouNo"));
			tfFundAppropriationDto.setSvoudate((String) BillSend1000Map.get("VouDate"));
			tfFundAppropriationDto.setSpayeracct((String) BillSend1000Map.get("PayerAcct"));
			tfFundAppropriationDto.setSpayername((String) BillSend1000Map.get("PayerName"));
			tfFundAppropriationDto.setNamt(new BigDecimal((String) BillSend1000Map.get("Amt")));
			tfFundAppropriationDto.setSpayeebankno((String) BillSend1000Map.get("PayeeBankNo"));
			tfFundAppropriationDto.setSpayeeopbkno((String) BillSend1000Map.get("PayeeOpBkNo"));
			tfFundAppropriationDto.setSpayeeacct((String) BillSend1000Map.get("PayeeAcct"));
			tfFundAppropriationDto.setSpayeename((String) BillSend1000Map.get("PayeeName"));
			tfFundAppropriationDto.setSpayreason((String) BillSend1000Map.get("PayReason"));
			tfFundAppropriationDto.setSbudgetsubjectcode((String) BillSend1000Map.get("BudgetSubjectCode"));
			tfFundAppropriationDto.setSaddword((String) BillSend1000Map.get("AddWord"));
			tfFundAppropriationDto.setSofyear((String) BillSend1000Map.get("OfYear"));
			tfFundAppropriationDto.setSflag((String) BillSend1000Map.get("Flag"));
			tfFundAppropriationDto.setSstatus("1"); // 0--清算成功； 1--待清算
			if(tvVoucherinfoDto != null && tvVoucherinfoDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)){
				TvPayreckBankDto clearDto = new TvPayreckBankDto();
				clearDto.setIvousrlno(Long.valueOf(tvVoucherinfoDto.getSdealno()));
				clearDto = (TvPayreckBankDto) CommonFacade.getODB().findRsByDto(clearDto).get(0);
				tfFundAppropriationDto.setSext2(clearDto.getSpaymode()); //用来表示2301的支付方式  1--授权支付   0--直接支付
			}
			DatabaseFacade.getODB().create(tfFundAppropriationDto);
			muleMessage.setPayload(xmlmap);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new RuntimeException(e);
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException(e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException(e);
		}
	}
}

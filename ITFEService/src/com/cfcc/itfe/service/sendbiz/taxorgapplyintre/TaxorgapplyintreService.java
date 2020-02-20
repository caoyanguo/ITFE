package com.cfcc.itfe.service.sendbiz.taxorgapplyintre;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HeadDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author zyt
 * @time 10-06-09 09:03:41 codecomment:
 */

public class TaxorgapplyintreService extends AbstractTaxorgapplyintreService {
	private static Log log = LogFactory.getLog(TaxorgapplyintreService.class);

	public void sendMsg(String sendOrgCode, String inTreDate, String msgno)
			throws ITFEBizException {
		// msgno = MsgConstant.MSG_NO_5002;
		String sfinorg = null;
		try {
			TsConvertfinorgDto finadto = new TsConvertfinorgDto();
			finadto.setStrecode(sendOrgCode);
//			if (!StateConstant.ORG_CENTER_CODE.equals(getLoginInfo().getSorgcode())) {
//				finadto.setSorgcode(this.getLoginInfo().getSorgcode());
//			}
			List<TsConvertfinorgDto> list;
			list = CommonFacade.getODB().findRsByDto(finadto);
			if (null == list || list.size() == 0) {
				log.error("请在财政机构信息中维护国库和财政代码的对应关系!");
				throw new ITFEBizException("请在财政机构信息中维护国库和财政代码的对应关系!");
			}
			sfinorg = list.get(0).getSfinorgcode();
		} catch (JAFDatabaseException e) {
			log.error("调用后台处理出现数据库异常!", e);
			throw new ITFEBizException("调用后台处理出现数据库异常!", e);
		} catch (ValidateException e) {
			log.error("调用后台处理出现数据库异常!", e);
			throw new ITFEBizException("调用后台处理出现数据库异常!", e);
		}

		HeadDto headdto = new HeadDto();
		headdto.set_VER(MsgConstant.MSG_HEAD_VER);
		headdto.set_SRC(sfinorg);
		headdto.set_APP(MsgConstant.MSG_HEAD_APP);
		headdto.set_DES(inTreDate);
		headdto.set_msgNo(msgno);
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headdto.set_msgID(msgid);
			headdto.set_msgRef(msgid);
		} catch (SequenceException e) {
			log.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		}
		headdto.set_workDate(TimeFacade.getCurrentStringTime());

		try {
			MuleClient client = new MuleClient();
			Map map = new HashMap();
			MuleMessage message = new DefaultMuleMessage(map);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY, msgno + "_OUT");
			message.setProperty(MessagePropertyKeys.MSG_HEAD_DTO, headdto);
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, getLoginInfo()
					.getSorgcode());// 核算主体代码
			message.setProperty(MessagePropertyKeys.MSG_DATE, inTreDate);// 入库日期
			message.setProperty(MessagePropertyKeys.MSG_BILL_CODE, sfinorg);// 发送机关代码

			message.setPayload(map);
			if(getLoginInfo().getSorgcode()!=null&&getLoginInfo().getSorgcode().startsWith("1702"))
				message = client.send("vm://ManagerMsgToPbcCity", message);
			else
				message = client.send("vm://ManagerMsgToPbc", message);
		} catch (MuleException e) {
			log.error("调用后台报文处理的时候出现异常!", e);
			throw new ITFEBizException("调用后台报文处理的时候出现异常!", e);
		}

	}

}
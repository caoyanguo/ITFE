package com.cfcc.itfe.service.sendbiz.socketsend;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.service.sendbiz.socketsend.AbstractSocketSendService;
import com.cfcc.itfe.util.ServiceUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.exception.ITFEBizException;
/**
 * @author Administrator
 * @time   14-07-22 11:21:41
 * codecomment: 
 */

public class SocketSendService extends AbstractSocketSendService {
	private static Log log = LogFactory.getLog(SocketSendService.class);	


	/**
	 * 发送文件
	 	 
	 * @generated
	 * @param list
	 * @throws ITFEBizException	 
	 */
	public void sendFile(List list) throws ITFEBizException {
		String root = ITFECommonConstant.FILE_ROOT_PATH;
		try {
			for (int i = 0; i < list.size(); i++) {
				MuleClient client = new MuleClient();
				Map map = new HashMap();
				MuleMessage message = new DefaultMuleMessage(map);
				message.setProperty(MessagePropertyKeys.MSG_FILE_NAME, root+list.get(i));
				message.setProperty(MessagePropertyKeys.MSG_ORGCODE, getLoginInfo().getSorgcode());
				message.setProperty(MessagePropertyKeys.MSG_NO_KEY, "10000");
				message.setPayload(map);
			    message.setProperty(MessagePropertyKeys.MSG_DATE, getLoginInfo().getCurrentDate());
				message = client.send("vm://TxtBJReportSend", message);
				ServiceUtil.checkResult(message);
			}
		} catch (MuleException e) {
			log.error("调用后台SOCKET发送文件出现异常!", e);
			throw new ITFEBizException("调用后台SOCKET发送文件出现异常!", e);

		}
	}

}
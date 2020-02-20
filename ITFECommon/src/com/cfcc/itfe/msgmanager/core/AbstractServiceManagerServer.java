package com.cfcc.itfe.msgmanager.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.util.ServiceUtil;

public class AbstractServiceManagerServer implements IServiceManagerServer {

	private static Log logger = LogFactory.getLog(AbstractServiceManagerServer.class);

	/**
	 * 处理上传文件
	 */
	public void dealFileDto(FileResultDto fileResultDto, String sorgcode, String susercode) throws ITFEBizException {
		// TODO Auto-generated method stub

	}

	/**
	 * 发送报文处理
	 * 
	 * @param String
	 *            sfilename 文件名称
	 * @param String
	 *            sorgcode 机构代码
	 * @param String
	 *            spackno 包流水号
	 * @param String
	 *            msgno 报文编号
	 * @param String
	 *            commitdate 委托日期
	 * @param String
	 *            msgcontent 文件内容(只有实拨资金[5101]需要)
	 * @param boolean isRepeat 是否重发
	 * @throws ITFEBizException
	 */
	public void sendMsg(String sfilename, String sorgcode, String spackno, String msgno, String commitdate, String msgcontent,boolean isRepeat)
			throws ITFEBizException {
		try {
			MuleClient client = new MuleClient();
			Map map = new HashMap();
			MuleMessage message = new DefaultMuleMessage(map);
			
			message.setProperty(MessagePropertyKeys.MSG_FILE_NAME, sfilename);
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, sorgcode);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY, msgno);
			message.setProperty(MessagePropertyKeys.MSG_REPEAT, isRepeat);
			
			message.setPayload(map);
			
			if((MsgConstant.MSG_NO_5101+"_OUT").equals(msgno) && !isRepeat){
				message.setProperty(MessagePropertyKeys.MSG_CONTENT, msgcontent);
				if(sorgcode!=null&&sorgcode.startsWith("1702"))
					message = client.send("vm://ManagerMsgTransferCity", message);
				else
					message = client.send("vm://ManagerMsgTransfer", message);
			}else{
				message.setProperty(MessagePropertyKeys.MSG_PACK_NO, spackno);
				message.setProperty(MessagePropertyKeys.MSG_DATE, commitdate);
				if(sorgcode!=null&&sorgcode.startsWith("1702"))
					message = client.send("vm://ManagerMsgToPbcCity", message);
				else
					message = client.send("vm://ManagerMsgToPbc", message);
			}

			ServiceUtil.checkResult(message);
		} catch (MuleException e) {
			logger.error("调用后台报文处理的时候出现异常!", e);
			throw new ITFEBizException("调用后台报文处理的时候出现异常!", e);

		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

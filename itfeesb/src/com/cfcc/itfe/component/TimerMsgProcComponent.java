package com.cfcc.itfe.component;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TsOperationmodelDto;
import com.cfcc.itfe.util.ServiceUtil;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * 
 * 报文发送接收
 * 
 */
public class TimerMsgProcComponent implements Callable {

	private static Log logger = LogFactory.getLog(TimerMsgProcComponent.class);

	@SuppressWarnings("unchecked")
	public Object onCall(MuleEventContext eventContext) throws Exception {

		HashMap<String, String> timerConfoMap = ITFECommonConstant.timerConfList;
		Set<String> set = timerConfoMap.keySet();
		String andwhere = " 1=1 ";
		if(ITFECommonConstant.PUBLICPARAM.contains(",recv31293139=false,"))
		{
			return eventContext.getMessage().getPayload();
		}
		if(ITFECommonConstant.PUBLICPARAM.contains(",recv3129=false,"))
		{
			andwhere = " AND S_OPERATIONTYPECODE<>? ";
		}
		if(ITFECommonConstant.PUBLICPARAM.contains(",recv3128=false,"))
		{
			andwhere = " AND S_OPERATIONTYPECODE<>? ";
		}
		// 取得当前系统时间：
		for (String startTime : set) {
			String endTime = timerConfoMap.get(startTime);
			Boolean b = IsTimeIn(startTime, endTime);
			if (b) {
				long start=System.currentTimeMillis();
				String trasrlsql = "select * from ts_operationmodel where "+andwhere+" order by i_no  fetch first "
						+ ITFECommonConstant.COMMIT_NUM + " rows only ";
				SQLExecutor sqlExec = null;
				try {
					sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
							.getSQLExecutor();
					if(ITFECommonConstant.PUBLICPARAM.contains(",recv3128=false,"))
					{
						sqlExec.addParam(MsgConstant.MSG_NO_3128);
					}
					if(ITFECommonConstant.PUBLICPARAM.contains(",recv3129=false,"))
					{
						sqlExec.addParam(MsgConstant.MSG_NO_3129);
					}
					SQLResults fileSrlnoRs = sqlExec.runQueryCloseCon(
							trasrlsql, TsOperationmodelDto.class);
					List<TsOperationmodelDto> dtoList = (List<TsOperationmodelDto>) fileSrlnoRs.getDtoCollection();
					if (null != dtoList && dtoList.size() > 0) {
						MuleMessage message = null;
						MuleClient client = null;
						DatabaseFacade.getODB().delete(dtoList.toArray(new TsOperationmodelDto[dtoList.size()]));
						for (TsOperationmodelDto _dto : dtoList) {
							String fileFullName = _dto.getSmodelsavepath();
							String msgNo = _dto.getSoperationtypecode();
//							DatabaseFacade.getODB().delete(_dto);
							if (!new File(fileFullName.trim()).exists()) {
								logger.error("Error*******定时解析报文失败,原因为报文文件不存在,详细信息为:报文种类--"+msgNo+",报文路径--"+fileFullName+" *********");
							}else {
								if (true) {
									if(message==null)
										message = new DefaultMuleMessage(fileFullName);
									message.setPayload(fileFullName);
									if(client==null)
										client = new MuleClient();
									message.setStringProperty(
											MessagePropertyKeys.MSG_NO_KEY, msgNo);
									message.setStringProperty(
											MessagePropertyKeys.MSG_SENDER, StateConstant.MSG_SENDER_FLAG_9);
									message.setStringProperty(
											MessagePropertyKeys.MSG_FILE_NAME,
											fileFullName);
									logger.error(Thread.currentThread().getName()+"=TimerMsgProcComponent-sendmsg="+_dto.getIno());
									message = client.send("vm://timersaveprocmsg",
											message);
									try{
										ServiceUtil.checkResult(message);
									}catch(Exception e)
									{}
								}
							}
						}
						if(!ITFECommonConstant.COMMIT_NUM.equals(String.valueOf(dtoList.size())))
						{
							TimerExportReportToDirComponent export = new TimerExportReportToDirComponent();
							export.onCall(eventContext);
						}
					}
				} catch (MuleException e) {
					logger.error(e);
					throw new ITFEBizException("调用mule发送报文出错\r\n", e);
				}finally
				{
					if(sqlExec!=null)
						sqlExec.closeConnection();
					logger.info(Thread.currentThread().getName()+"=TimerMsgProcComponent-processtime="+(System.currentTimeMillis()-start)/1000);
				}
			} else {
				continue;
			}
		}
		return eventContext.getMessage().getPayload();
	}

	/**
	 * 判断当前时间是否在配置的时间段内
	 * 
	 * @param startTimer
	 * @param endTimer
	 * @return
	 */
	private boolean IsTimeIn(String startTimer, String endTimer) {
		Date curDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String curtime = sdf.format(curDate);
		if (curtime.compareTo(startTimer) > 0
				&& curtime.compareTo(endTimer) < 0) {
			return true;
		}
		return false;

	}

}

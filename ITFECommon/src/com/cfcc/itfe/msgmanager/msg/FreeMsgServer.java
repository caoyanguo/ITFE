package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvFreeDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.Dto2MapFor1106;
import com.cfcc.itfe.util.transformer.Dto2MapFor11062;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

public class FreeMsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(FreeMsgServer.class);

	/**
	 * 免抵调请求报文信息处理
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		String filename = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_FILE_NAME);// 文件名称
		String orgcode = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_ORGCODE);// 机构代码
		String packno = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_PACK_NO);// 包流水号
		String commitdate = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_DATE); // 委托日期
		boolean isRepeat = (Boolean) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_REPEAT); // 是否重发报文

		// 查找数据库
		String orderbysql = "where S_BOOKORGCODE = ?  and S_PACKNO = ? and D_ACCEPTDATE = ? ";
		List<String> params = new ArrayList<String>();
		params.add(orgcode);
		// params.add(filename);
		params.add(packno);
		params.add(CommonUtil.strToDate(commitdate).toString());

		try {
			List<TvFreeDto> list = DatabaseFacade.getDb().find(TvFreeDto.class,
					orderbysql, params);
			Map xmlMap = new HashMap();
			// DTO -> MAP
			// 判断是否为新接口，如果为新接口，则转换报文编号，将报文按照新的接口组装
			if (ITFECommonConstant.IFNEWINTERFACE.equals("1")) {
				xmlMap = Dto2MapFor11062.tranfor(list, orgcode, filename,
						packno, isRepeat);
			} else {
				xmlMap = Dto2MapFor1106.tranfor(list, orgcode, filename,
						packno, isRepeat);
			}

			String ls_MsgId = (String) ((Map) ((Map) xmlMap.get("cfx"))
					.get("HEAD")).get("MsgID");
			Map msg = (Map) xmlMap.get("cfx");
			Map batchHead1106 = (Map) ((Map) msg.get("MSG"))
					.get("BatchHead1106");
			BigDecimal amt = new BigDecimal(batchHead1106.get("AllAmt")
					.toString());
			int num = Integer.parseInt(batchHead1106.get("AllNum").toString());
			// 设置消息体

			// 写发送日志
			eventContext.getMessage().setProperty(
					MessagePropertyKeys.MSG_SEND_LOG_DTO,
					MsgLogFacade.writeSendLogWithResult(StampFacade
							.getStampSendSeq("FS"), null, orgcode,
							ITFECommonConstant.DES_NODE, commitdate.replaceAll(
									"-", ""), MsgConstant.MSG_NO_1106,
							(String) eventContext.getMessage().getProperty(
									"XML_MSG_FILE_PATH"), num, amt, list.get(0)
									.getSpackno(),
							list.get(0).getSfreepluptrecode(), null, null, null,
							ls_MsgId, DealCodeConstants.DEALCODE_ITFE_SEND,
							null, null,
							(String) eventContext.getMessage().getProperty(
									MessagePropertyKeys.MSG_SENDER), null, ""));
			SQLExecutor updateExce = null;
			try {
				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();
				String updateSql = "update "
						+ TvFilepackagerefDto.tableName()
						+ " set  S_MSGID = ? , T_SENDTIME = CURRENT TIMESTAMP where  S_PACKAGENO = ? and S_ORGCODE = ? and S_COMMITDATE = ?";
				updateExce.clearParams();
				updateExce.addParam(ls_MsgId);
				// updateExce.addParam(new
				// Timestamp(TimeFacade.getCurrentDateTime().getTime()));
				updateExce.addParam(list.get(0).getSpackno());
				updateExce.addParam(orgcode);// 核算主体代码
				updateExce.addParam((list.get(0).getDacceptdate()).toString()
						.replaceAll("-", ""));// //委托日期
				updateExce.runQuery(updateSql);
				updateExce.closeConnection();

			} catch (JAFDatabaseException e) {
				String error = "更新免抵调处理回执状态时出现数据库异常！";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} finally {
				if (null != updateExce) {
					updateExce.closeConnection();
				}
			}

			// 设置消息体
			eventContext.getMessage().setPayload(xmlMap);
		} catch (JAFDatabaseException e) {
			logger.error("查询免抵调请求业务出现数据库异常!", e);
			throw new ITFEBizException("查询收免抵调请求业务出现数据库异常!", e);
		} catch (SequenceException e) {
			throw new ITFEBizException("取发送流水号失败", e);
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

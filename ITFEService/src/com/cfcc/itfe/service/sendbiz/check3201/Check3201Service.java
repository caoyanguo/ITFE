package com.cfcc.itfe.service.sendbiz.check3201;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.ContentDto;
import com.cfcc.itfe.persistence.dto.TvTips3201Dto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * @author zgz
 * @time 12-03-12 15:45:18 codecomment:
 */

public class Check3201Service extends AbstractCheck3201Service {
	private static Log log = LogFactory.getLog(Check3201Service.class);

	/**
	 * 重发申请
	 * 
	 * @generated
	 * @param idto
	 * @throws ITFEBizException
	 */
	public void doApply(IDto idto) throws ITFEBizException {

		try {
			TvTips3201Dto tv3201dto = (TvTips3201Dto)idto;
			ContentDto condto = new ContentDto();
			//原包报文编号
			condto.set_OriPackMsgNo(tv3201dto.getSorimsgno());
			// 原发起机构代码
			condto.set_OriSendOrgCode(tv3201dto.getSorisendorgcode());
			//原委托日期
			condto.set_OriEntrustDate(tv3201dto.getSorientrustdate());
			//原包流水号
			condto.set_OriPackNo(tv3201dto.getSoripackno());
			
			MuleClient client = new MuleClient();
			MuleMessage message = new DefaultMuleMessage(condto);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY,
					MsgConstant.MSG_NO_9111);
			message.setProperty(MessagePropertyKeys.MSG_DTO, condto);
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, tv3201dto.getSbillorg());
			message.setPayload(condto);
			if(tv3201dto.getSbillorg()!=null&&tv3201dto.getSbillorg().startsWith("1702"))
				message = client.send("vm://ManagerMsgToPbcCity", message);
			else
				message = client.send("vm://ManagerMsgToPbc", message);
		} catch (MuleException e) {
			log.error("调用后台报文处理的时候出现异常!", e);
			throw new ITFEBizException("调用后台报文处理的时候出现异常!", e);
		}
	}

	/**
	 * 核对
	 */
	public void doCheck(IDto idto) throws ITFEBizException {

		SQLExecutor sqlExec = null;
		try {
			/**
			 * 先将核对标志置为'0'
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String movedataSql = "UPDATE TV_TIPS_3201 A SET S_CHECK='0'"
					+ " WHERE S_ORGCODE= ? AND S_CHKDATE= ? ";
			TvTips3201Dto tv3201dto = (TvTips3201Dto) idto;
			// 核算主体代码
			sqlExec.addParam(tv3201dto.getSorgcode());
			// 核对日期
			sqlExec.addParam(tv3201dto.getSchkdate());
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 在接收日志中能找到的，则将核对标志置为"1"
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TV_TIPS_3201 A SET S_CHECK='1'"
					+ " WHERE S_ORGCODE= ? AND S_CHKDATE= ? "
					+ " AND EXISTS (SELECT 1 FROM TV_RECVLOG WHERE S_DATE=A.S_ORIENTRUSTDATE "
					+ " AND S_OPERATIONTYPECODE=A.S_ORIMSGNO AND S_PACKNO=A.S_ORIPACKNO AND S_BILLORG=A.S_ORISENDORGCODE)";
			// 核算主体代码
			sqlExec.addParam(tv3201dto.getSorgcode());
			// 核对日期
			sqlExec.addParam(tv3201dto.getSchkdate());
			sqlExec.runQueryCloseCon(movedataSql);
		} catch (JAFDatabaseException e) {
			log.error("数据库操作异常!", e);
			throw new ITFEBizException("数据库操作异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}

	}

}
package com.cfcc.itfe.service.subsysmanage.loginouttips;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.ContentDto;
import com.cfcc.itfe.persistence.dto.HeadDto;
import com.cfcc.itfe.persistence.dto.TsSystemDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.ServiceUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author caoyg
 * @time 09-11-23 09:15:48 codecomment:
 */

public class LogInOutTipsService extends AbstractLogInOutTipsService {
	private static Log log = LogFactory.getLog(LogInOutTipsService.class);

	/**
	 * 登陆报文发送
	 * 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException
	 */
	public String sendLoginMsg(ContentDto dto) throws ITFEBizException {
		HeadDto headdto = new HeadDto();
		headdto.set_VER(MsgConstant.MSG_HEAD_VER);
		headdto.set_SRC(ITFECommonConstant.SRC_NODE);
		headdto.set_DES(MsgConstant.MSG_HEAD_DES);
		headdto.set_APP(MsgConstant.MSG_HEAD_APP);
		headdto.set_msgNo(dto.get_Msgno());
		String seq;
		try {
			seq = MsgSeqFacade.getMsgSendSeq();
		} catch (SequenceException e1) {
			throw new ITFEBizException("获取报文序列号失败", e1);
		}
		headdto.set_msgID(seq);
		headdto.set_msgRef(seq);
		headdto.set_workDate(TimeFacade.getCurrentStringTime());

		try {
			MuleClient client = new MuleClient();
			Map map = new HashMap();
			MuleMessage message = new DefaultMuleMessage(map);
			message
					.setProperty(MessagePropertyKeys.MSG_NO_KEY, dto
							.get_Msgno());
			
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, getLoginInfo().getSorgcode());
			message.setProperty(MessagePropertyKeys.MSG_HEAD_DTO, headdto);
			message.setProperty(MessagePropertyKeys.MSG_CONTENT, dto);
			message.setPayload(map);
			if(getLoginInfo().getSorgcode()!=null&&getLoginInfo().getSorgcode().startsWith("1702"))
				message = client.send("vm://ManagerMsgToPbcCity", message);
			else
				message = client.send("vm://ManagerMsgToPbc", message);
			ServiceUtil.checkResult(message);
			// return message.getProperty(
			// MessagePropertyKeys.MSG_NO_KEY).toString();
			return seq;
		} catch (MuleException e) {
			log.error("调用后台报文处理的时候出现异常!", e);
			throw new ITFEBizException("调用后台报文处理的时候出现异常!", e);
		}

	}

	/**
	 * 查询测试结果
	 * 
	 * @generated
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public String queryLoginResult(String msgno) throws ITFEBizException {
		String sql = "SELECT * FROM TV_SENDLOG where S_TITLE = ? AND S_SENDORGCODE = ? AND S_DATE = ? AND (S_RETCODE IS NULL OR S_RETCODE =?)";
		SQLExecutor exec;
		try {
			exec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			exec.addParam(msgno);
			exec.addParam(getLoginInfo().getSorgcode());
			exec.addParam(TimeFacade.getCurrentStringTime());
			exec.addParam("");
			SQLResults res = exec.runQueryCloseCon(sql, TvSendlogDto.class);
			List data = (List) res.getDtoCollection();
			if (data != null && data.size() > 0) {
				TvSendlogDto dto = (TvSendlogDto) data.get(0);
				if (MsgConstant.MSG_STATE.equals(dto.getSretcode())) {
					if (msgno.equals(MsgConstant.MSG_NO_9006)) {
						return "登陆成功";
					} else {
						return "签退成功";
					}
				} else {
					if (msgno.equals(MsgConstant.MSG_NO_9006)) {
						return "登陆失败";
					} else {
						return "签退失败";
					}
				}
			}
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException("查询报文处理状态失败", e);
		}
		return null;

	}

	/**
	 * 查询测试结果
	 * 
	 * @generated
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public String queryLoginResult(String msgno, String msgid)
			throws ITFEBizException {
		String sql = "SELECT * FROM TV_SENDLOG where S_OPERATIONTYPECODE = ? AND S_SENDORGCODE = ? AND S_SEQ=?";
		SQLExecutor exec;
		try {
			exec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			exec.addParam(msgno);
			exec.addParam(getLoginInfo().getSorgcode());
			exec.addParam(msgid);
			SQLResults res = exec.runQueryCloseCon(sql, TvSendlogDto.class);
			List data = (List) res.getDtoCollection();
			if (data != null && data.size() > 0) {
				TvSendlogDto dto = (TvSendlogDto) data.get(0);
				if (StringUtils.isBlank(dto.getSretcode())
						|| DealCodeConstants.DEALCODE_ITFE_SEND.equals(dto
								.getSretcode()))
					return null;
				if (MsgConstant.MSG_STATE.equals(dto.getSretcode())) {
					if (msgno.equals(MsgConstant.MSG_NO_9006)) {
						return "登陆成功";
					} else {
						return "签退成功";
					}
				} else {
					if (msgno.equals(MsgConstant.MSG_NO_9006)) {
						return "登陆失败";
					} else {
						return "签退失败";
					}
				}
			}
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException("查询报文处理状态失败", e);
		}
		return null;

	}

	/**
	 * 签退报文发送
	 * 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException
	 */
	public String sendLogOutMsg(ContentDto dto) throws ITFEBizException {
		TsSystemDto _dto = new TsSystemDto();
		List<TsSystemDto> list;
		try {
			list = CommonFacade.getODB().findRsByDto(_dto);
			if (list.size()>0) {
				if (StateConstant.LOGINSTATE_FLAG_LOGIN.equals(list.get(0).getStipsloginstate())) {
					return StateConstant.LOGINSTATE_FLAG_LOGIN_NAME;
				}else{
					return StateConstant.LOGINSTATE_FLAG_LOGOUT_NAME;
				}
		
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询tips登陆状态出错",e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("查询tips登陆状态出错",e);
		}
		return null;
	}

	/**
	 * 查询测试结果
	 * 
	 * @generated
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List queryLogOutResult() throws ITFEBizException {
		return null;
	}

	public String sendLogOutMsg() throws ITFEBizException {
		
		
		// TODO Auto-generated method stub
		return null;
	}

}
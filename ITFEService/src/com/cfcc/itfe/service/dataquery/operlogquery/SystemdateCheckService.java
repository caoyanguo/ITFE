package com.cfcc.itfe.service.dataquery.operlogquery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsSyslogDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
/**
 * @author db2admin
 * @time   09-11-23 15:17:32
 * codecomment: 
 */

public class SystemdateCheckService extends AbstractSystemdateCheckService {
	private static Log log = LogFactory.getLog(SystemdateCheckService.class);

	public PageResponse querySystemlog(IDto dto, PageRequest pageRequest)
			throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}

	public void result() throws ITFEBizException {
		// TODO Auto-generated method stub
		
	}

	public void operLog(String userID, String funcCode, String menuName,
			String serviceName, Boolean operFlag, ITFELoginInfo loginInfo)
			throws ITFEBizException {
		TsSyslogDto log = new TsSyslogDto();
		// 流水号
		try {
			log.setIno(new Integer(MsgSeqFacade.getlogSeq()));
		} catch (NumberFormatException e2) {
			throw new ITFEBizException("日志内容:" + log.toString(),e2);
		} catch (SequenceException e2) {
			throw new ITFEBizException("日志内容:" + log.toString(),e2);
		}
		// 用户id
		log.setSusercode(userID);
		// 日期
		log.setSdate(TimeFacade.getCurrentStringTime());
		// 时间
		try {
			log.setStime(TSystemFacade.getDBSystemTime());
		} catch (JAFDatabaseException e1) {
			throw new ITFEBizException("日志内容:" + log.toString(),e1);
		}
		// 业务类型码
		log.setSoperationtypecode(funcCode);
		// 核算主体代码
		log.setSorgcode(loginInfo.getSorgcode());
		// 操作说明
		log.setSoperationdesc(menuName);
		// 备注
		if (operFlag) {
			log.setSdemo("操作未成功");
		}
		// 写数据库操作
		try {
			DatabaseFacade.getDb().create(log);
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException("日志内容:" + log.toString(),e);
		}
		
		//更新用户登录状态
		SQLExecutor exec=null;
		try {
			exec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String sql = "UPDATE TS_USERS SET S_LOGINSTATUS=?, S_LASTEXITTIME=?,S_USERSTATUS=? WHERE S_ORGCODE=? AND S_USERCODE=?";
			exec.addParam(StateConstant.LOGINSTATE_FLAG_LOGOUT);//退出
			exec.addParam(TSystemFacade.getDBSystemTime());
			exec.addParam(StateConstant.USERSTATUS_1);
			exec.addParam(loginInfo.getSorgcode());
			exec.addParam(loginInfo.getSuserCode());
			exec.runQueryCloseCon(sql);
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException("更新用户状态出错:" + log.toString(),e);
		}finally{
			if(exec != null){
				exec.closeConnection();
			}
		}
		
	}	


}
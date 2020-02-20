package com.cfcc.itfe.service.advice;

import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.jaf.core.interfaces.IServiceInvoker;
import com.cfcc.jaf.core.interfaces.IServiceRequest;
import com.cfcc.jaf.core.interfaces.IServiceResponse;
import com.cfcc.jaf.core.invoker.aop.IInterceptor;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsSyslogDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.ITFERequestExtInfo;

public class ITFEOperLogInvoker implements IInterceptor, IServiceInvoker {
	private Log logger = LogFactory.getLog(ITFEOperLogInvoker.class);
	private IServiceInvoker invoker = null;	

	public void setInvoker(IServiceInvoker arg0) {
		invoker = arg0;
	}

	public IServiceResponse invoke(IServiceRequest arg0) {
		String remoteUserCode = "";
		String remoteFunctionCode = "" ;
		String menuName = "";
		ITFELoginInfo loginInfo;
		ITFERequestExtInfo extInfo = (ITFERequestExtInfo)arg0.getRequestExtInfo();
		// 0051,登录
		IServiceResponse response = invoker.invoke(arg0);
		if(arg0.getRequestExtInfo().getLoginInfo() == null){
			Object[] objs = arg0.getArgs();
			//TODO
			loginInfo = (ITFELoginInfo)objs[0];
			remoteUserCode = loginInfo.getSuserCode();
			remoteFunctionCode = "0000";
			menuName = "登录";
		}else{	
		    loginInfo = (ITFELoginInfo) extInfo.getLoginInfo();
			menuName = extInfo.getMenuName();
			remoteFunctionCode = extInfo.getFuncCode();
			remoteUserCode = loginInfo.getSuserCode();
		}
		// 记录操作日志	
		String methodName = arg0.getMethodName();
		//过滤查询及缓存的service方法
		if (methodName.indexOf("search") < 0
				&& methodName.indexOf("find") < 0
				&& methodName.indexOf("cache") < 0) {
			if (null!=menuName && menuName.trim().length()>0) {
				operLog(remoteUserCode, remoteFunctionCode, menuName, arg0.getMethodName() + "()", response.isExceptionThrown(), loginInfo);
				if("0000".equals(remoteFunctionCode)){//如果是登录则更新用户表中的登录状态和登录时间
					SQLExecutor exec=null;
					try {
						exec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
						String sql = "UPDATE TS_USERS SET S_LOGINSTATUS=?, S_LASTLOGINTIME=? WHERE S_ORGCODE=? AND S_USERCODE=?";
						exec.addParam(StateConstant.LOGINSTATE_FLAG_LOGIN);//登录
						exec.addParam(TSystemFacade.getDBSystemTime());
						exec.addParam(loginInfo.getSorgcode());
						exec.addParam(loginInfo.getSuserCode());
						exec.runQueryCloseCon(sql);
					} catch (JAFDatabaseException e) {
						logger.error(e);
						try {
							throw new ITFEBizException("更新用户状态出错",e);
						} catch (ITFEBizException e1) {
							e1.printStackTrace();
						}
					}finally{
						if(exec != null){
							exec.closeConnection();
						}
					}
				}
			}
		}
		return response;
	}
	
	/**
	 * 记录日志
	 * @param userID
	 * @param sFuncCode
	 * @param menuName
	 * @param serviceName
	 * @param operFlag
	 * @param loginInfo
	 * @throws TASBizException 
	 */
	private void operLog(String userID, String sFuncCode,String menuName,String serviceName,boolean operFlag,ITFELoginInfo loginInfo) {
		if(operVoucherLog(sFuncCode, menuName, serviceName, loginInfo))
			return;
		TsSyslogDto log = new TsSyslogDto();
		//流水号
		try {
			log.setIno(new Integer(MsgSeqFacade.getlogSeq()));
		} catch (NumberFormatException e2) {
			logger.error("取SEQ异常", e2);
			logger.error("日志内容:" + log.toString());
		} catch (SequenceException e2) {
			logger.error("取SEQ异常", e2);
			logger.error("日志内容:" + log.toString());
		}
		//用户id
		log.setSusercode(userID);
		//日期
		log.setSdate(TimeFacade.getCurrentStringTime());
		//时间
		try {
			log.setStime(TSystemFacade.getDBSystemTime());
		} catch (JAFDatabaseException e1) {
			logger.error("取时间戳发生异常", e1);
			logger.error("日志内容:" + log.toString());
		}
		//业务类型码
		log.setSoperationtypecode(sFuncCode);
		//核算主体代码
		log.setSorgcode(loginInfo.getSorgcode());
		//操作说明
		log.setSoperationdesc(menuName);
		//备注
		if(operFlag){  
			log.setSdemo("操作未成功");
		}
		if (serviceName.equals("addInfo()")) {
			log.setSdemo("增加记录");
		} else if(serviceName.equals("delInfo()")){
			log.setSdemo("删除记录");
		} else if(serviceName.equals("modInfo()")){
			log.setSdemo("修改记录");
		}else if(serviceName.contains("send")||serviceName.contains("Send")){
			log.setSdemo("发送凭证");
		}else if(serviceName.contains("sign")||serviceName.contains("Sign")||serviceName.contains("stamp")||serviceName.contains("Stamp")){
			log.setSdemo("凭证签章");
		}else if(serviceName.contains("upload")||serviceName.contains("Upload")){
			log.setSdemo("凭证上传");
		}else if(serviceName.contains("download")||serviceName.contains("Download")){
			log.setSdemo("凭证下载");
		}		
		// 写数据库操作
		try {
			DatabaseFacade.getDb().create(log);
		} catch (JAFDatabaseException e) {
			logger.error("记录数据库日志发生异常", e);
			logger.error("日志内容:" + log.toString());
		}
	}
	
	/**
	 * 记录无纸化凭证操作日志
	 * @param sFuncCode
	 * @param menuName
	 * @param serviceName
	 * @param loginInfo
	 * @return
	 */
	private boolean operVoucherLog(String sFuncCode,String menuName,String serviceName,ITFELoginInfo loginInfo){		
		TsSyslogDto dto = new TsSyslogDto();
		if(dealServiceName(serviceName, dto))
			return false;
		dto.setSusercode(loginInfo.getSuserCode());
		dto.setSdate(TimeFacade.getCurrentStringTime());
		dto.setStime(new Timestamp(new java.util.Date().getTime()));
		dto.setSoperationtypecode(sFuncCode);
		dto.setSorgcode(loginInfo.getSorgcode());
		dto.setSoperationdesc(menuName);						
		try {
			DatabaseFacade.getDb().create(dto);
		} catch (JAFDatabaseException e) {
			logger.error("记录数据库日志发生异常", e);
		}return true;
	}
	
	/**
	 * 无纸化凭证签章、提交tips等操作记入操作日志备注信息
	 * @param serviceName
	 * @param dto
	 * @return
	 */
	private boolean dealServiceName(String serviceName,TsSyslogDto dto){
		boolean flag=false;
		if(serviceName.equals("voucherStamp()"))
			dto.setSdemo("凭证签章");
		else if(serviceName.equals("voucherStampCancle()"))
			dto.setSdemo("凭证撤销签章");
		else if(serviceName.equals("voucherReturnSuccess()"))
			dto.setSdemo("凭证发送电子凭证库");
		else if(serviceName.equals("voucherCheckRetBack()"))
			dto.setSdemo("凭证退回");
		else if(serviceName.equals("voucherCommit()"))
			dto.setSdemo("凭证提交");
		else if(serviceName.equals("amtControlVerify()"))
			dto.setSdemo("凭证额度控制校验");
		else 
			flag=true;
		return flag;
	}
}

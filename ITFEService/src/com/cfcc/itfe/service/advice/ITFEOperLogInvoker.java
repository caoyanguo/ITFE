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
		// 0051,��¼
		IServiceResponse response = invoker.invoke(arg0);
		if(arg0.getRequestExtInfo().getLoginInfo() == null){
			Object[] objs = arg0.getArgs();
			//TODO
			loginInfo = (ITFELoginInfo)objs[0];
			remoteUserCode = loginInfo.getSuserCode();
			remoteFunctionCode = "0000";
			menuName = "��¼";
		}else{	
		    loginInfo = (ITFELoginInfo) extInfo.getLoginInfo();
			menuName = extInfo.getMenuName();
			remoteFunctionCode = extInfo.getFuncCode();
			remoteUserCode = loginInfo.getSuserCode();
		}
		// ��¼������־	
		String methodName = arg0.getMethodName();
		//���˲�ѯ�������service����
		if (methodName.indexOf("search") < 0
				&& methodName.indexOf("find") < 0
				&& methodName.indexOf("cache") < 0) {
			if (null!=menuName && menuName.trim().length()>0) {
				operLog(remoteUserCode, remoteFunctionCode, menuName, arg0.getMethodName() + "()", response.isExceptionThrown(), loginInfo);
				if("0000".equals(remoteFunctionCode)){//����ǵ�¼������û����еĵ�¼״̬�͵�¼ʱ��
					SQLExecutor exec=null;
					try {
						exec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
						String sql = "UPDATE TS_USERS SET S_LOGINSTATUS=?, S_LASTLOGINTIME=? WHERE S_ORGCODE=? AND S_USERCODE=?";
						exec.addParam(StateConstant.LOGINSTATE_FLAG_LOGIN);//��¼
						exec.addParam(TSystemFacade.getDBSystemTime());
						exec.addParam(loginInfo.getSorgcode());
						exec.addParam(loginInfo.getSuserCode());
						exec.runQueryCloseCon(sql);
					} catch (JAFDatabaseException e) {
						logger.error(e);
						try {
							throw new ITFEBizException("�����û�״̬����",e);
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
	 * ��¼��־
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
		//��ˮ��
		try {
			log.setIno(new Integer(MsgSeqFacade.getlogSeq()));
		} catch (NumberFormatException e2) {
			logger.error("ȡSEQ�쳣", e2);
			logger.error("��־����:" + log.toString());
		} catch (SequenceException e2) {
			logger.error("ȡSEQ�쳣", e2);
			logger.error("��־����:" + log.toString());
		}
		//�û�id
		log.setSusercode(userID);
		//����
		log.setSdate(TimeFacade.getCurrentStringTime());
		//ʱ��
		try {
			log.setStime(TSystemFacade.getDBSystemTime());
		} catch (JAFDatabaseException e1) {
			logger.error("ȡʱ��������쳣", e1);
			logger.error("��־����:" + log.toString());
		}
		//ҵ��������
		log.setSoperationtypecode(sFuncCode);
		//�����������
		log.setSorgcode(loginInfo.getSorgcode());
		//����˵��
		log.setSoperationdesc(menuName);
		//��ע
		if(operFlag){  
			log.setSdemo("����δ�ɹ�");
		}
		if (serviceName.equals("addInfo()")) {
			log.setSdemo("���Ӽ�¼");
		} else if(serviceName.equals("delInfo()")){
			log.setSdemo("ɾ����¼");
		} else if(serviceName.equals("modInfo()")){
			log.setSdemo("�޸ļ�¼");
		}else if(serviceName.contains("send")||serviceName.contains("Send")){
			log.setSdemo("����ƾ֤");
		}else if(serviceName.contains("sign")||serviceName.contains("Sign")||serviceName.contains("stamp")||serviceName.contains("Stamp")){
			log.setSdemo("ƾ֤ǩ��");
		}else if(serviceName.contains("upload")||serviceName.contains("Upload")){
			log.setSdemo("ƾ֤�ϴ�");
		}else if(serviceName.contains("download")||serviceName.contains("Download")){
			log.setSdemo("ƾ֤����");
		}		
		// д���ݿ����
		try {
			DatabaseFacade.getDb().create(log);
		} catch (JAFDatabaseException e) {
			logger.error("��¼���ݿ���־�����쳣", e);
			logger.error("��־����:" + log.toString());
		}
	}
	
	/**
	 * ��¼��ֽ��ƾ֤������־
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
			logger.error("��¼���ݿ���־�����쳣", e);
		}return true;
	}
	
	/**
	 * ��ֽ��ƾ֤ǩ�¡��ύtips�Ȳ������������־��ע��Ϣ
	 * @param serviceName
	 * @param dto
	 * @return
	 */
	private boolean dealServiceName(String serviceName,TsSyslogDto dto){
		boolean flag=false;
		if(serviceName.equals("voucherStamp()"))
			dto.setSdemo("ƾ֤ǩ��");
		else if(serviceName.equals("voucherStampCancle()"))
			dto.setSdemo("ƾ֤����ǩ��");
		else if(serviceName.equals("voucherReturnSuccess()"))
			dto.setSdemo("ƾ֤���͵���ƾ֤��");
		else if(serviceName.equals("voucherCheckRetBack()"))
			dto.setSdemo("ƾ֤�˻�");
		else if(serviceName.equals("voucherCommit()"))
			dto.setSdemo("ƾ֤�ύ");
		else if(serviceName.equals("amtControlVerify()"))
			dto.setSdemo("ƾ֤��ȿ���У��");
		else 
			flag=true;
		return flag;
	}
}

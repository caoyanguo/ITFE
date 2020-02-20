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
		// ��ˮ��
		try {
			log.setIno(new Integer(MsgSeqFacade.getlogSeq()));
		} catch (NumberFormatException e2) {
			throw new ITFEBizException("��־����:" + log.toString(),e2);
		} catch (SequenceException e2) {
			throw new ITFEBizException("��־����:" + log.toString(),e2);
		}
		// �û�id
		log.setSusercode(userID);
		// ����
		log.setSdate(TimeFacade.getCurrentStringTime());
		// ʱ��
		try {
			log.setStime(TSystemFacade.getDBSystemTime());
		} catch (JAFDatabaseException e1) {
			throw new ITFEBizException("��־����:" + log.toString(),e1);
		}
		// ҵ��������
		log.setSoperationtypecode(funcCode);
		// �����������
		log.setSorgcode(loginInfo.getSorgcode());
		// ����˵��
		log.setSoperationdesc(menuName);
		// ��ע
		if (operFlag) {
			log.setSdemo("����δ�ɹ�");
		}
		// д���ݿ����
		try {
			DatabaseFacade.getDb().create(log);
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException("��־����:" + log.toString(),e);
		}
		
		//�����û���¼״̬
		SQLExecutor exec=null;
		try {
			exec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String sql = "UPDATE TS_USERS SET S_LOGINSTATUS=?, S_LASTEXITTIME=?,S_USERSTATUS=? WHERE S_ORGCODE=? AND S_USERCODE=?";
			exec.addParam(StateConstant.LOGINSTATE_FLAG_LOGOUT);//�˳�
			exec.addParam(TSystemFacade.getDBSystemTime());
			exec.addParam(StateConstant.USERSTATUS_1);
			exec.addParam(loginInfo.getSorgcode());
			exec.addParam(loginInfo.getSuserCode());
			exec.runQueryCloseCon(sql);
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException("�����û�״̬����:" + log.toString(),e);
		}finally{
			if(exec != null){
				exec.closeConnection();
			}
		}
		
	}	


}
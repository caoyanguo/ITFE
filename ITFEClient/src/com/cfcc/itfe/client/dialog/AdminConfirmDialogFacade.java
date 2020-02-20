package com.cfcc.itfe.client.dialog;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsSyslogDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.security.Md5App;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.rcp.util.MessageDialog;

public class AdminConfirmDialogFacade {

	private static Log log = LogFactory.getLog(AdminConfirmDialogFacade.class);	
	private static ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService) ServiceFactory
			.getService(ICommonDataAccessService.class);
	private static String managerUserName="";//�����û�
	
	/**
	 * ����Ȩ����
	 * ��¼������Ȩ������־
	 * @param sFuncCode ���ܴ���(ÿ���˵��Ĺ��ܴ���)
	 * @param menuName	��������
	 * @param sdemo ��ע
	 * @param message
	 * @return
	 */
	public static boolean open(String sFuncCode,String menuName,String sdemo,String message){
		if(open(message)){
			createSysLog(sFuncCode, menuName, sdemo);
			return true;
		}return false;
	}

	/**
	 * ����Ȩ����
	 * @param message ��Ȩ�����е���ʾ��Ϣ
	 * @return
	 */
	public static boolean open(String message) {
		final BursarAffirmDialog dialog = new BursarAffirmDialog(null, message);
		int dialogflag = dialog.open();
		if (dialogflag == 0) {
			String password = dialog.getLoginPassword();
			String usercode = dialog.getLoginUserCode();
			StringBuffer msg = new StringBuffer();

			if (StringUtils.isBlank(usercode)) {
				msg.append("�������û����\n");
			} else if (usercode.trim().getBytes().length > 30) {
				msg.append("�û���Ų��ܳ���30λ��\n");
			}
			if (StringUtils.isBlank(password)) {
				msg.append("����������\n");
			} 
			if (!msg.toString().equals("")) {
				MessageDialog.openMessageDialog(Display.getDefault()
						.getActiveShell(), msg.toString());
				return open(message);
			}
			// �û���У��ʧ�����µ���
			if (!checkin(usercode, password,getLoginDto().getSorgcode())) {
				return open(message);
			}
		} else 
			return false;		
		return true;
	}

	/**
	 * �û�У��
	 * 
	 * @return
	 * @throws Exception
	 */
	private static boolean checkin(String usercode, String password,
			String sorgcode) {
		TsUsersDto dto = new TsUsersDto();
		dto.setSusercode(usercode);
		dto.setSpassword(new Md5App().makeMd5(password.trim()));
		dto.setSorgcode(sorgcode);
		ITFELoginInfo loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
		if(loginInfo.getPublicparam().contains(",payoutstampmode=true,")||loginInfo.getPublicparam().contains("0swap2"))//���Ž�ɫ�������Ա��ҵ�����ܻ���
			dto.setSusertype("0");//ҵ������
		else
			dto.setSusertype("2");//ҵ������
		try {
			// ȡ����־��Ϣ
			List<TsUsersDto> dtolist = commonDataAccessService.findRsByDto(dto);
			if (null == dtolist || dtolist.size() != 1) {
				Exception ex = new Exception("�û�id�����������");
				log.error(ex);
				MessageDialog.openErrorDialog(Display.getDefault()
						.getActiveShell(), ex);
				return false;
			}managerUserName=dtolist.get(0).getSusername();
		} catch (Exception e) {
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
			log.error(e);
			return false;
		}
		return true;
	}
	
	/**
	 * ��¼������Ȩ������־
	 * @param sFuncCode ���ܴ���(ÿ���˵��Ĺ��ܴ���)
	 * @param menuName	��������
	 * @param sdemo ��ע
	 */
	public static void createSysLog(String sFuncCode,String menuName,String sdemo){		
		TsSyslogDto dto = new TsSyslogDto();
		try {	
			dto.setSusercode(getLoginDto().getSuserCode());
			dto.setSdate(TimeFacade.getCurrentStringTime());
			dto.setStime(new Timestamp(new java.util.Date().getTime()));
			dto.setSoperationtypecode(sFuncCode);
			dto.setSorgcode(getLoginDto().getSorgcode());
			dto.setSoperationdesc(menuName);
			dto.setSdemo("�����û�: "+managerUserName+sdemo);			
			commonDataAccessService.create(dto);			
		} catch(ITFEBizException e){
			log.error("��¼���ݿ���־�����쳣", e);
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(), e);		
		}
	}
	
	/**
	 * ��ȡ��½dto
	 * @return
	 */
	private static ITFELoginInfo getLoginDto(){
		return (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
	}
}

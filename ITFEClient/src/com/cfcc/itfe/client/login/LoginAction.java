package com.cfcc.itfe.client.login;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.cfcc.devplatform.utils.dialog.JafProgressMonitorDialog;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.TimerVoucherInfoTask;
import com.cfcc.itfe.client.sendbiz.bizcertsend.AsspOcx;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.pk.TsUsersPK;
import com.cfcc.itfe.security.Md5App;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.util.AreaSpecUtil;
import com.cfcc.jaf.core.interfaces.ILoginInfo;
import com.cfcc.jaf.core.interfaces.IPermissionChecker;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.core.service.security.AuthenticatorException;
import com.cfcc.jaf.core.service.security.IAuthenticator;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.rcp.util.SimpleImageUtil;

public class LoginAction extends Action {
	
	private static Log log = LogFactory.getLog(LoginAction.class);
	
	private String usercode;  // �û����� 
	private String password;  // �û����� 
	private String orgcode; // ��½����
	
	private ApplicationActionBarAdvisor advisor;

	private IAuthenticator iAuthenticator = (IAuthenticator) ServiceFactory
			.getService(IAuthenticator.class);
	private ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService) ServiceFactory
	.getService(ICommonDataAccessService.class);

	public LoginAction() {
		super("��¼");
		setId("LoginAction");
		setImageDescriptor(SimpleImageUtil.getDescriptor(SimpleImageUtil.LOGIN));
	}

	public void run() {
		log(true);
		/*�����ͻ��˶�ʱ��������*/
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
		if(loginfo!=null&&loginfo.getPublicparam()!=null&&loginfo.getPublicparam().contains(",tx=true,"))
			triggerTimerInfoTask();
	}

	private void log(boolean flag) {
		LoginRunnableWithProgress lrwp = null;
		final LoginTitleDialog dialog = new LoginTitleDialog(null);
		int dialogflag = dialog.open();
		if (dialogflag == 0) {
			password = dialog.getLoginPassword();
			usercode = dialog.getLoginUserCode();
			orgcode = dialog.getLoginOrgCode();
			StringBuffer msg = new StringBuffer();
			if (orgcode == null) {
				msg.append("����������������\n");
			} else if (orgcode.trim().length() > 12) {
				msg.append("�����������12λ��\n");

			}
			if (usercode == null) {
				msg.append("�������û����\n");
			} else if (usercode.trim().getBytes().length > 30) {
				msg.append("�û���Ų��ܳ���30λ��\n");
			}
			if (password == null) {
				msg.append("����������\n");
			} else if ((ITFELoginInfo.LOGIN_TYPE != '1') && (password.trim().length() < 8)) {
				msg.append("�û����볤��С��8λ\n");
			}
			if (!msg.toString().equals("")) {
				MessageDialog.openMessageDialog(null, msg.toString());
				run();
			}

			// ���ù��״̬
//			Shell shel = Display.getDefault().getShells()[0];
//			shel.setActive();
//			Cursor cursor = shel.getCursor();
//			if (cursor != null) {
//				cursor.dispose();
//			}
//			Cursor waitCursor = new Cursor(Display.getDefault(),
//					SWT.CURSOR_WAIT);

//			shel.setCursor(waitCursor);
			if (flag) {
				try {
					lrwp = new LoginRunnableWithProgress();
					new JafProgressMonitorDialog(Display.getDefault()
							.getActiveShell(), "�û���¼", "���ڵ�¼�����Ժ�......!").run(
							true, true, lrwp);
				} catch (Exception e) {
					MessageDialog.openMessageDialog(null, "��¼����,�����µ�¼!");
					log.info("��ָ���쳣,��δ�ҵ�����ԭ��......");
					log.error(e.getMessage(), e);
					login(true);
				}
			} else {
				boolean falg=login(false);
				if(!falg){
					log(false);
				}
			}
			if (lrwp != null && !lrwp.result) {
				run();
				return;
			}

			ITFELoginInfo resloginfo = (ITFELoginInfo) advisor.getLoginInfo();
			//��һ�ε�½	//�������
			if (resloginfo.isFirstLogin()||!resloginfo.isInvalidation()) {
				openModifyPasswordDialog();
				rerun();
			}
		} else if (dialogflag == 1) {
			System.exit(0);
		}

		
	}

	public void rerun() {
		log(false);
	}

	private boolean login(boolean flag) {

		advisor = ApplicationActionBarAdvisor.getDefault();
		advisor.setVersionIdentify(1);
		ITFELoginInfo loginfo = new ITFELoginInfo();
		loginfo.setSuserCode(usercode);
		loginfo.setSpassword(password);
		loginfo.setSorgcode(orgcode);

		ITFELoginInfo resloginfo = null;
		try {
//			String filename = "orgcode.properties";
//			String key = "itfe@icfcc.com";
//			String path = System.getProperty("java.class.path");
//			String lastpath = path.substring(0, path.lastIndexOf("\\"));
//			String configpath = lastpath.substring(0, lastpath.lastIndexOf("\\")+1)+"configuration\\" + filename;
//			List<String> orgcodeList = readFileWithLine(configpath);
//			String mainorgcode = orgcode.substring(0,2);
//			if(!orgcodeList.contains( new Md5App().makeMd5(mainorgcode+key))
//					&& !orgcodeList.contains( new Md5App().makeMd5(orgcode+key))){
//				MessageDialog.openMessageDialog(null, "û����Ȩʹ��ϵͳ��Ȩ�ޣ�����ϵ����Ա��");
//				return false;
//			}
			AreaSpecUtil area = (AreaSpecUtil) ContextFactory.getApplicationContext().getBean("AreaMode");
			loginfo.setVersion(area.getVersion().trim());
			resloginfo = (ITFELoginInfo) iAuthenticator.login(loginfo);
			
			if(resloginfo.getSpassword().equals("")){
				MessageDialog.openMessageDialog(null, "�û���Ϣ�����ڻ�����������������룡");
				return false;
			}
			//ʹ��UK��¼��У���¼��֤��ID�Ƿ���ȷ
			if(resloginfo.getLogintype()!='0'){
				String readCerID = getUserDN();
				if ((null == readCerID) || (readCerID.length() == 0)) {
					throw new AuthenticatorException(
							"�û�Usb-Keyû�в��룬�����key��");
				}
				if ((null == resloginfo.getSsign())
						|| (resloginfo.getSsign().length() == 0)) {
					throw new AuthenticatorException(
							"�û�����֤��IDû�����ã�����ϵ����Ա��");
				}
				if (!readCerID.equals(resloginfo.getSsign())) {
					throw new AuthenticatorException(
							"��ʹ�ñ��˵�Usb-Key��¼ϵͳ��");
				}
			}
			 //��¼�ɹ�
			 ModifyUserLoginStatusDialog(true,resloginfo);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
			return false;
		}
		//��Ϊ�ڷ��������صĵ�¼��Ϣ�������˲������ݣ����Դ˴���Ҫ�Ѵӷ��������صĵ�½��Ϣ��¼��Session��
		advisor.setLoginInfo(resloginfo);
		advisor.setPermissionChecker(new LoginiPermissionChecker());
		Display.getDefault().syncExec( 
				new Runnable() {
					public void run() {
						advisor.configMenuBar();
				}
			});
		if (null != resloginfo) {
			try {
				Thread current = Thread.currentThread();
				if ("main".equals(current.getName())) {
					advisor.getStatusItem().setText(resloginfo.toString());
				} else {
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * �޸��û���¼����,���ݵ�¼�����޸��û���¼״̬
	 * @param flag 
	 * @param resloginfo 
	 */
	private void ModifyUserLoginStatusDialog(boolean flag, ITFELoginInfo resloginfo) {
		try {
			TsUsersPK pk = new TsUsersPK();
			pk.setSusercode(usercode);
			pk.setSorgcode(orgcode);
			TsUsersDto userDto = (TsUsersDto) commonDataAccessService.find(pk);
			if (!flag) {
				if (null==userDto) {
					//�û�������
					log.info("****************�û�������******************");
				} else {
					if (null==userDto.getShold1()) {
						userDto.setShold1("0");
					}
					int Num=Integer.valueOf(userDto.getShold1());
					if ((Num+1)==3) {
						userDto.setSuserstatus(StateConstant.USERSTATUS_3);
					}
					userDto.setShold1(String.valueOf(Num+1));
				}
				commonDataAccessService.updateData(userDto);
			}else{
				userDto.setShold1("0");
				userDto.setSuserstatus(StateConstant.USERSTATUS_2);
				commonDataAccessService.updateData(userDto);
			}
			log.info("****************�û���¼�������û�״̬�޸ĳɹ�******************");

		} catch (ITFEBizException e) {
			org.eclipse.jface.dialogs.MessageDialog.openError(Display
					.getDefault().getActiveShell(), "�޸��û���¼״̬�쳣", e.getMessage());
			log.error(e.getMessage(), e);
		}

	}

	/**
	 * ����û�֤���DN
	 * @return
	 */
	private String getUserDN() throws ITFEBizException{
		//�û��������UK��¼
		AsspOcx asspOcx = new AsspOcx(new Shell());
		return asspOcx.DlgSelectCertId();
	}

	class LoginiPermissionChecker implements IPermissionChecker {

		public boolean check(ILoginInfo loginInfo, String functionCode) {
			ITFELoginInfo info = (ITFELoginInfo) loginInfo;

			return info.getFunctionList().contains(functionCode);
			// return true;
		}

		public boolean check(ILoginInfo loginInfo, String functionCode,
				String tbfunctionCode) {
			ITFELoginInfo info = (ITFELoginInfo) loginInfo;
			return info.getFunctionList().contains(functionCode);

		}

	}

	class LoginRunnableWithProgress implements IRunnableWithProgress {
		public boolean result = false;

		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			result = login(true);
		}
	}
	
	
	public void openModifyPasswordDialog() {
		try {
			TsUsersPK pk = new TsUsersPK();
			pk.setSusercode(usercode);
			pk.setSorgcode(orgcode);
			TsUsersDto userDto = (TsUsersDto) commonDataAccessService.find(pk);
			ModifyPasswordDialog mpd = new ModifyPasswordDialog(null, userDto);
			int mpdvalue = mpd.open();
			if (mpdvalue == IDialogConstants.OK_ID) {
				userDto.setSpassmoddate(TimeFacade.getCurrentStringTime());
				userDto.setSpassword(new Md5App().makeMd5(mpd.getNewPassword()));
				userDto.setImodicount(1);//�Ѿ����ǵ�һ�ε�½
				userDto.setSuserstatus(StateConstant.USERSTATUS_1);
				commonDataAccessService.updateData(userDto);
				MessageDialog.openMessageDialog(null, "�����޸ĳɹ��������µ�¼��");
			} else if (mpdvalue == IDialogConstants.CANCEL_ID) {
			} else {
				System.exit(0);
			}

		} catch (ITFEBizException e) {
			org.eclipse.jface.dialogs.MessageDialog.openError(Display
					.getDefault().getActiveShell(), "�����޸ĳ���", e.getMessage());
			log.error(e.getMessage(), e);
		}

	}
	/**
	 * �����ж�ȡ�ļ���ÿ�и���split���зָ���ַ�������
	 * 
	 * @param file
	 * @param split
	 * @return
	 * @throws FileOperateException
	 */
	public List<String> readFileWithLine(String fileName)
			throws FileOperateException {
		List<String> listStr = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			String data = null;
			while ((data = br.readLine()) != null) {
				if (data.trim().equals("") || data.startsWith("#")) {
					continue;
				}
				listStr.add(data);
			}
			br.close();
			return listStr;
		}catch(FileNotFoundException e1){ 
			log.error(e1);
			throw new FileOperateException("û����Ȩʹ��ϵͳ��Ȩ�ޣ�����ϵ����Ա��", e1);
		}catch (Exception e) {
			log.error(e);
			throw new FileOperateException("��ȡ�ļ������쳣", e);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					log.error(e);
					throw new FileOperateException("��ȡ�ļ������쳣", e);
				}

			}
		}

	}
	
	/**
	 * �����ͻ��˶�ʱ����ƾ֤�������
	 */
	private void triggerTimerInfoTask(){
		ContextFactory.getBeanFromApplicationContext("TimerFactory");
		TimerVoucherInfoTask timerVoucherInfoTask = (TimerVoucherInfoTask) ContextFactory.getBeanFromApplicationContext("TimerVoucherInfo");
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();		
		if(loginfo!=null&&loginfo.getPublicparam()!=null&&loginfo.getPublicparam().contains(",tx=true,"))
			timerVoucherInfoTask.run();
	}
}

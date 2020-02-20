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
	
	private String usercode;  // 用户名称 
	private String password;  // 用户密码 
	private String orgcode; // 登陆机构
	
	private ApplicationActionBarAdvisor advisor;

	private IAuthenticator iAuthenticator = (IAuthenticator) ServiceFactory
			.getService(IAuthenticator.class);
	private ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService) ServiceFactory
	.getService(ICommonDataAccessService.class);

	public LoginAction() {
		super("登录");
		setId("LoginAction");
		setImageDescriptor(SimpleImageUtil.getDescriptor(SimpleImageUtil.LOGIN));
	}

	public void run() {
		log(true);
		/*触发客户端定时提醒任务*/
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
				msg.append("请输入核算主体代码\n");
			} else if (orgcode.trim().length() > 12) {
				msg.append("核算主体代码12位长\n");

			}
			if (usercode == null) {
				msg.append("请输入用户编号\n");
			} else if (usercode.trim().getBytes().length > 30) {
				msg.append("用户编号不能超过30位长\n");
			}
			if (password == null) {
				msg.append("请输入密码\n");
			} else if ((ITFELoginInfo.LOGIN_TYPE != '1') && (password.trim().length() < 8)) {
				msg.append("用户密码长度小于8位\n");
			}
			if (!msg.toString().equals("")) {
				MessageDialog.openMessageDialog(null, msg.toString());
				run();
			}

			// 设置光标状态
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
							.getActiveShell(), "用户登录", "正在登录中请稍候......!").run(
							true, true, lrwp);
				} catch (Exception e) {
					MessageDialog.openMessageDialog(null, "登录出错,请重新登录!");
					log.info("空指针异常,暂未找到具体原因......");
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
			//第一次登陆	//密码过期
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
//				MessageDialog.openMessageDialog(null, "没有授权使用系统的权限，请联系管理员！");
//				return false;
//			}
			AreaSpecUtil area = (AreaSpecUtil) ContextFactory.getApplicationContext().getBean("AreaMode");
			loginfo.setVersion(area.getVersion().trim());
			resloginfo = (ITFELoginInfo) iAuthenticator.login(loginfo);
			
			if(resloginfo.getSpassword().equals("")){
				MessageDialog.openMessageDialog(null, "用户信息不存在或密码错误，请重新输入！");
				return false;
			}
			//使用UK登录，校验登录的证书ID是否正确
			if(resloginfo.getLogintype()!='0'){
				String readCerID = getUserDN();
				if ((null == readCerID) || (readCerID.length() == 0)) {
					throw new AuthenticatorException(
							"用户Usb-Key没有插入，请插入key。");
				}
				if ((null == resloginfo.getSsign())
						|| (resloginfo.getSsign().length() == 0)) {
					throw new AuthenticatorException(
							"用户个人证书ID没有设置，请联系管理员。");
				}
				if (!readCerID.equals(resloginfo.getSsign())) {
					throw new AuthenticatorException(
							"请使用本人的Usb-Key登录系统。");
				}
			}
			 //登录成功
			 ModifyUserLoginStatusDialog(true,resloginfo);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
			return false;
		}
		//因为在服务器返回的登录信息中增加了部分内容，所以此处需要把从服务器返回的登陆信息记录在Session中
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
	 * 修改用户登录次数,根据登录次数修改用户登录状态
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
					//用户不存在
					log.info("****************用户不存在******************");
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
			log.info("****************用户登录次数、用户状态修改成功******************");

		} catch (ITFEBizException e) {
			org.eclipse.jface.dialogs.MessageDialog.openError(Display
					.getDefault().getActiveShell(), "修改用户登录状态异常", e.getMessage());
			log.error(e.getMessage(), e);
		}

	}

	/**
	 * 获得用户证书的DN
	 * @return
	 */
	private String getUserDN() throws ITFEBizException{
		//用户名、口令、UK登录
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
				userDto.setImodicount(1);//已经不是第一次登陆
				userDto.setSuserstatus(StateConstant.USERSTATUS_1);
				commonDataAccessService.updateData(userDto);
				MessageDialog.openMessageDialog(null, "密码修改成功，请重新登录！");
			} else if (mpdvalue == IDialogConstants.CANCEL_ID) {
			} else {
				System.exit(0);
			}

		} catch (ITFEBizException e) {
			org.eclipse.jface.dialogs.MessageDialog.openError(Display
					.getDefault().getActiveShell(), "密码修改出错", e.getMessage());
			log.error(e.getMessage(), e);
		}

	}
	/**
	 * 按照行读取文件，每行根据split进行分割成字符串数组
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
			throw new FileOperateException("没有授权使用系统的权限，请联系管理员！", e1);
		}catch (Exception e) {
			log.error(e);
			throw new FileOperateException("读取文件出现异常", e);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					log.error(e);
					throw new FileOperateException("读取文件出现异常", e);
				}

			}
		}

	}
	
	/**
	 * 触发客户端定时提醒凭证处理情况
	 */
	private void triggerTimerInfoTask(){
		ContextFactory.getBeanFromApplicationContext("TimerFactory");
		TimerVoucherInfoTask timerVoucherInfoTask = (TimerVoucherInfoTask) ContextFactory.getBeanFromApplicationContext("TimerVoucherInfo");
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();		
		if(loginfo!=null&&loginfo.getPublicparam()!=null&&loginfo.getPublicparam().contains(",tx=true,"))
			timerVoucherInfoTask.run();
	}
}

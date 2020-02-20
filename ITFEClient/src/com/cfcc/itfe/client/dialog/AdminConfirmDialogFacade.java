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
	private static String managerUserName="";//主管用户
	
	/**
	 * 打开授权界面
	 * 记录主管授权操作日志
	 * @param sFuncCode 功能代码(每个菜单的功能代码)
	 * @param menuName	功能名称
	 * @param sdemo 备注
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
	 * 打开授权界面
	 * @param message 授权见面中的提示信息
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
				msg.append("请输入用户编号\n");
			} else if (usercode.trim().getBytes().length > 30) {
				msg.append("用户编号不能超过30位长\n");
			}
			if (StringUtils.isBlank(password)) {
				msg.append("请输入密码\n");
			} 
			if (!msg.toString().equals("")) {
				MessageDialog.openMessageDialog(Display.getDefault()
						.getActiveShell(), msg.toString());
				return open(message);
			}
			// 用户名校验失败重新调用
			if (!checkin(usercode, password,getLoginDto().getSorgcode())) {
				return open(message);
			}
		} else 
			return false;		
		return true;
	}

	/**
	 * 用户校验
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
		if(loginInfo.getPublicparam().contains(",payoutstampmode=true,")||loginInfo.getPublicparam().contains("0swap2"))//厦门角色管理管理员和业务主管互换
			dto.setSusertype("0");//业务主管
		else
			dto.setSusertype("2");//业务主管
		try {
			// 取得日志信息
			List<TsUsersDto> dtolist = commonDataAccessService.findRsByDto(dto);
			if (null == dtolist || dtolist.size() != 1) {
				Exception ex = new Exception("用户id或者密码错误！");
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
	 * 记录主管授权操作日志
	 * @param sFuncCode 功能代码(每个菜单的功能代码)
	 * @param menuName	功能名称
	 * @param sdemo 备注
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
			dto.setSdemo("主管用户: "+managerUserName+sdemo);			
			commonDataAccessService.create(dto);			
		} catch(ITFEBizException e){
			log.error("记录数据库日志发生异常", e);
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(), e);		
		}
	}
	
	/**
	 * 获取登陆dto
	 * @return
	 */
	private static ITFELoginInfo getLoginDto(){
		return (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
	}
}

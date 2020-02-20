package com.cfcc.itfe.service.common;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsMankeymodeDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsSystemDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.pk.TsUsersPK;
import com.cfcc.itfe.security.Md5App;
import com.cfcc.itfe.service.AbstractITFEService;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.AreaSpecUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.interfaces.ILoginInfo;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.core.service.security.AuthenticatorException;
import com.cfcc.jaf.core.service.security.IAuthenticator;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

public class ItfeAuthenticatorService extends AbstractITFEService implements
		IAuthenticator {

	public ILoginInfo login(ILoginInfo loginInfo) throws AuthenticatorException {
		if (loginInfo instanceof ITFELoginInfo) {
			ITFELoginInfo itfeinfo = (ITFELoginInfo) loginInfo;
			if(!ITFECommonConstant.PUBLICPARAM.contains("versionverify=false"))
			{
				if (!ITFECommonConstant.CLIENTEDITION.equals(itfeinfo.getVersion())) {
					throw new AuthenticatorException("当前登陆客户端版本"
							+ itfeinfo.getVersion() + "与服务器对应客户端版本"
							+ ITFECommonConstant.CLIENTEDITION + "不一致，请升级客户端！");
				}
			}
			// 修改登录信息
			TsUsersPK pk = new TsUsersPK();

			// tmUserDto.setClogonstat(StateConstant.LOGINSTATE_FLAG_LOGIN);
			// DatabaseFacade.getODB().update(tmUserDto);
			// 获取核算主体信息
			pk.setSorgcode(itfeinfo.getSorgcode());
			pk.setSusercode(itfeinfo.getSuserCode());

			try {
				TsUsersDto userDto = (TsUsersDto) DatabaseFacade.getDb().find(
						pk);
				if (userDto != null
						&& userDto.getSpassword().trim().length() < 32) {
					userDto.setSpassword(new Md5App().makeMd5(userDto
							.getSpassword().trim()));
					DatabaseFacade.getDb().update(userDto);
				}
				/***********************************************/
				checkUserStatus(itfeinfo);
				/***********************************************/
				itfeinfo.setSpassword(new Md5App().makeMd5(itfeinfo
						.getSpassword().trim()));
				if (null == userDto
						|| ((ITFECommonConstant.LOGIN_TYPE != '1') && (!itfeinfo
								.getSpassword().trim().equals(
										userDto.getSpassword().trim())))) {
					itfeinfo.setSpassword("");
					throw new AuthenticatorException("用户信息不存在或密码错误，请重新输入");
				} else {
					itfeinfo.setSuserName(userDto.getSusername());
					itfeinfo.setSuserType(userDto.getSusertype());
					if (null != userDto.getImodicount()
							&& userDto.getImodicount() == 0) {
						itfeinfo.setFirstLogin(Boolean.TRUE);
					} else {
						itfeinfo.setFirstLogin(Boolean.FALSE);
					}

					if (StateConstant.User_Type_Admin.equals(userDto
							.getSusertype())) {
						itfeinfo.setSystemUser(true);
					} else {
						itfeinfo.setSystemUser(false);
					}
					// 获取用户所属核算主体信息
					TsOrganDto orgDto = new TsOrganDto();
					orgDto.setSorgcode(itfeinfo.getSorgcode());
					List list = CommonFacade.getODB().findRsByDto(orgDto);
					if (list == null || list.size() <= 0) {
						throw new AuthenticatorException("机构基础数据有误，请联系管理员");
					}
					orgDto = (TsOrganDto) list.get(0);
					itfeinfo.setSorgName(orgDto.getSorgname());
					itfeinfo.setOrgKind(orgDto.getSorgcode());
					itfeinfo.setIscollect(orgDto.getSiscollect());
					itfeinfo.setCurrentDate(TimeFacade.getCurrent2StringTime());
					itfeinfo.setScertId(userDto.getScertid());
					if (orgDto.getSorgstatus().charAt(0) != '1') {
						throw new AuthenticatorException("用户所属机构已经被禁用，请联系管理员");
					}
					// 获取TIPS登陆状态
					TsSystemDto sysDto = new TsSystemDto();
					List<TsSystemDto> syslist = CommonFacade.getODB()
							.findRsByDto(sysDto);
					if (list == null || list.size() <= 0) {
						throw new AuthenticatorException("系统参数表有误，请联系管理员");
					}
					String tipslogstate = syslist.get(0).getStipsloginstate();
					if (StateConstant.LOGINSTATE_FLAG_LOGIN
							.equals(tipslogstate)) {
						itfeinfo
								.setTipslogState(StateConstant.LOGINSTATE_FLAG_LOGIN_NAME);
					} else {
						itfeinfo
								.setTipslogState(StateConstant.LOGINSTATE_FLAG_LOGOUT_NAME);
					}
					// 判断日期的有效性
					java.util.Date curdate = TimeFacade.getCurrentDate();
					java.util.Date pasdate = TimeFacade.parseDate(userDto
							.getSpassmoddate(), "yyyyMMdd");
					int days = (int) ((curdate.getTime() - pasdate.getTime()) / 1000 / 60 / 60 / 24);
					itfeinfo.setValidpassdays(userDto.getSpassmodcycle() - days);
					if (itfeinfo.getValidpassdays()<0) {
						itfeinfo.setInvalidation(false);
					}else {
						itfeinfo.setInvalidation(true);
					}
					// 取得用户登录方式
					itfeinfo.setLogintype(ITFECommonConstant.LOGIN_TYPE);
					// 设置用户证书ID
					itfeinfo.setSsign(userDto.getScertid());

					// 取地方特色和加密方式,放在对应loginfo中
					HashMap<String, String> encryptMode = (HashMap<String, String>) ContextFactory
							.getApplicationContext().getBean("encryptMode");
					String area = AreaSpecUtil.getInstance().getArea();
					String sysflag = AreaSpecUtil.getInstance().getSysflag();
					itfeinfo.setEncryptMode(encryptMode);
					itfeinfo.setArea(area);
					itfeinfo.setSysflag(sysflag);
					// 取加密方式是按核算主体加密还是全省统一
					List<TsMankeymodeDto> _dtoList = DatabaseFacade.getODB()
							.find(TsMankeymodeDto.class);
					if (_dtoList.size() > 0) {
						TsMankeymodeDto _dto = _dtoList.get(0);
						if (StateConstant.KEY_ALL.equals(_dto.getSkeymode())) {
							itfeinfo.setMankeyMode(StateConstant.KEY_ALL);// 全省统一维护
						} else if (StateConstant.KEY_BOOK.equals(_dto
								.getSkeymode())) {
							itfeinfo.setMankeyMode(StateConstant.KEY_BOOK); // 按核算主体维护
						} else {// 
							itfeinfo.setMankeyMode(StateConstant.KEY_BOOK); // 目前仅支持按核算主体和全省统一
						}

					} else {
						itfeinfo.setMankeyMode(StateConstant.KEY_BOOK); // 默认按照核算主体维护
					}
				}
				// 公共参数配置
				itfeinfo.setPublicparam(ITFECommonConstant.PUBLICPARAM);
				// 备份状态设置
				try {
					if (ITFECommonConstant.PUBLICPARAM.contains("localdbbackup=true")) {
						itfeinfo.setScurFunc(getlocalLogs());
					}
					if (ITFECommonConstant.PUBLICPARAM.contains("monvoudbbackup=true")) {
						itfeinfo.setSmenuName(getVoucherBakLogs());
					}
				
				} catch (FileOperateException e) {
					log.error(e);
				}
				// 如果是管理员，直接登录
				if (itfeinfo.getSuserCode().equals(ITFECommonConstant.ADMIN)
						&& StateConstant.ORG_CENTER_CODE.equals(itfeinfo
								.getSorgcode())) {
					adminGetFunc(itfeinfo);

					return itfeinfo;
				}
				// 查找权限表
				getFunc(itfeinfo);

				return itfeinfo;
			} catch (JAFDatabaseException e) {
				throw new AuthenticatorException("系统异常，请联系管理员", e);
			} catch (ValidateException e) {
				throw new AuthenticatorException("查询用户所属机构时发生异常，请联系管理员", e);
			}
		}

		return loginInfo;
	}

	private void checkUserStatus(ITFELoginInfo loginfo)
			throws JAFDatabaseException, AuthenticatorException {
		try {
			TsUsersPK pk = new TsUsersPK();
			pk.setSusercode(loginfo.getSuserCode());
			pk.setSorgcode(loginfo.getSorgcode());
			TsUsersDto userDto = (TsUsersDto) DatabaseFacade.getODB().find(pk);
			if (null == userDto) {
				throw new AuthenticatorException("用户不存在,请重新输入！");
			} else {
				if (userDto.getSuserstatus().equals(StateConstant.USERSTATUS_3)) {
					throw new AuthenticatorException("用户["
							+ loginfo.getSuserCode() + "]已被冻结，不能登录系统！");
				} else if (userDto.getSuserstatus().equals(
						StateConstant.USERSTATUS_0)) {
					throw new AuthenticatorException("用户["
							+ loginfo.getSuserCode() + "]已被禁用，不能登录系统！");
				}else if (userDto.getSuserstatus().equals(
						StateConstant.USERSTATUS_2)) {
					if(ITFECommonConstant.PUBLICPARAM.contains(",userlogin=one,"))
					{
						throw new AuthenticatorException("用户["
								+ loginfo.getSuserCode() + "]已经在工作，不能重复登录系统！");
					}
				}
				if (!userDto.getSpassword().equals(
						new Md5App().makeMd5(loginfo.getSpassword().trim()))) {
					if (StateConstant.USERSTATUS_1.equals(userDto
							.getSuserstatus())) {
						this.updateTsUserWhenPwdErr(userDto);
						int loginCount = 0;
						if (userDto.getShold1() == null
								|| "".equals(userDto.getShold1())) {
							loginCount = 0;
						} else {
							loginCount = Integer.valueOf(userDto.getShold1());
						}
						if (loginCount >= 3
								&& !StateConstant.User_Type_Admin
										.equals(userDto.getSusertype())) {
							throw new AuthenticatorException("用户["
									+ loginfo.getSuserCode()
									+ "]密码累计输错三次，该用户被冻结！");
						} else {
							throw new AuthenticatorException("用户["
									+ loginfo.getSuserCode() + "]密码错误，请重新输入！");
						}
					} else {
						throw new AuthenticatorException("用户["
								+ loginfo.getSuserCode() + "]密码错误，请重新输入！");
					}
				}
			}
		} catch (AuthenticatorException e) {
			e.printStackTrace();
			log.info("*****************" + e.getMessage());
			throw new AuthenticatorException(e.getMessage());
		}

	}

	private void updateTsUserWhenPwdErr(TsUsersDto userDto)
			throws AuthenticatorException {
		if("000000000000".equals(userDto.getSorgcode())&&"admin".equals(userDto.getSusercode()))
			return;
		if (userDto.getSuserstatus().equals(StateConstant.USERSTATUS_1)
				&& !StateConstant.User_Type_Admin
						.equals(userDto.getSusertype())) {
			String tmp = userDto.getShold1() == null ? "0" : userDto
					.getShold1();
			int loginCount = Integer.valueOf(tmp);
			loginCount++;
			userDto.setShold1(String.valueOf(loginCount));
			if (loginCount >= 3) {
				userDto.setSuserstatus(StateConstant.USERSTATUS_3);
			}
			try {
				DatabaseFacade.getODB().update(userDto);
			} catch (JAFDatabaseException e) {
				e.printStackTrace();
				throw new AuthenticatorException("系统异常，请联系管理员", e);
			}
		}

	}

	@Override
	public void setLoginInfo(ITFELoginInfo info) {
		// TODO Auto-generated method stub
		super.setLoginInfo(info);
	}

	public void logout() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getServiceDescription() {
		return "登录服务";
	}

	public boolean checkVersion(String versoionCode) {
		return ITFECommonConstant.EDITION.equals(versoionCode.trim());
	}

	/**
	 * 获取权限
	 * 
	 * @param itfeinfo
	 * @throws JAFDatabaseException
	 */
	private void getFunc(ITFELoginInfo itfeinfo) throws JAFDatabaseException {
		List functionList = new ArrayList();
		functionList = TSystemFacade.findFuncCodeByUserAndOrg(itfeinfo
				.getSuserCode(), itfeinfo.getSorgcode());
		if (itfeinfo.getSorgcode().equals(StateConstant.STAT_CENTER_CODE)) {
			functionList.add("S_211");
		}
		itfeinfo.setFunctionList(functionList);

	}

	/**
	 * 系统管理员权限通过ITFEConfig.xml配置文件获取
	 * 
	 * @param itfeinfo
	 */
	private void adminGetFunc(ITFELoginInfo itfeinfo) {
		itfeinfo.setFunctionList(ITFECommonConstant.ADMIN_FUNC);
	}

	/**
	 * 通过读取日志文件获取数据库备份文件日志文件，判断数据库是否备份成功
	 * 
	 * @throws FileOperateException
	 * 
	 */
	private String getlocalLogs() throws FileOperateException {
		String localBakPath = "/itfe/logs/dbbackup.log";
		// 判断本机备份文件是否成功
		File file = new File(localBakPath);
		if (file.exists()) {
			List<String> bakLogLines = FileUtil.getInstance().readFileWithLine(
					localBakPath);
			if (bakLogLines.size() > 0) {
				String logs = bakLogLines.get(bakLogLines.size() - 1);

				if ((logs.contains("successful")||logs.contains("备份成功"))
						&& logs.contains(TimeFacade.getCurrentStringTime())) {
					if (bakLogLines.size() > 100) {
						FileUtil.getInstance().writeFile(localBakPath, logs);
					}
					return "1";
				}
				
			}
		}
		return "0";
	}

	/**
	 * 通过读取日志文件获取数据库备份文件日志文件，判断数据库是否备份成功
	 * 
	 * @throws FileOperateException
	 * 
	 */
	private String getVoucherBakLogs() throws FileOperateException {
		String localBakPath = "/itfe/logs/db2fullbak.log";
		// 判断凭证库文件是否上传
		File file = new File(localBakPath);
		if (file.exists()) {
			List<String> bakLogLines = FileUtil.getInstance().readFileWithLine(
					localBakPath);
			if (bakLogLines.size() > 0 && bakLogLines.size() <200 ) {
				Boolean flag1=false;
				Boolean flag2= false;
				for(String tmpStr:bakLogLines){
					if(tmpStr.contains("备份成功")){
						flag1 = true;
					}
					if(tmpStr.contains("Transfer complete")){
						flag2 = true;
					}
				}
				if(flag1 && flag2){
					return "1";
				}
			}
		}
		return "0";
	}

}

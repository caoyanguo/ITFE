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
					throw new AuthenticatorException("��ǰ��½�ͻ��˰汾"
							+ itfeinfo.getVersion() + "���������Ӧ�ͻ��˰汾"
							+ ITFECommonConstant.CLIENTEDITION + "��һ�£��������ͻ��ˣ�");
				}
			}
			// �޸ĵ�¼��Ϣ
			TsUsersPK pk = new TsUsersPK();

			// tmUserDto.setClogonstat(StateConstant.LOGINSTATE_FLAG_LOGIN);
			// DatabaseFacade.getODB().update(tmUserDto);
			// ��ȡ����������Ϣ
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
					throw new AuthenticatorException("�û���Ϣ�����ڻ������������������");
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
					// ��ȡ�û���������������Ϣ
					TsOrganDto orgDto = new TsOrganDto();
					orgDto.setSorgcode(itfeinfo.getSorgcode());
					List list = CommonFacade.getODB().findRsByDto(orgDto);
					if (list == null || list.size() <= 0) {
						throw new AuthenticatorException("��������������������ϵ����Ա");
					}
					orgDto = (TsOrganDto) list.get(0);
					itfeinfo.setSorgName(orgDto.getSorgname());
					itfeinfo.setOrgKind(orgDto.getSorgcode());
					itfeinfo.setIscollect(orgDto.getSiscollect());
					itfeinfo.setCurrentDate(TimeFacade.getCurrent2StringTime());
					itfeinfo.setScertId(userDto.getScertid());
					if (orgDto.getSorgstatus().charAt(0) != '1') {
						throw new AuthenticatorException("�û����������Ѿ������ã�����ϵ����Ա");
					}
					// ��ȡTIPS��½״̬
					TsSystemDto sysDto = new TsSystemDto();
					List<TsSystemDto> syslist = CommonFacade.getODB()
							.findRsByDto(sysDto);
					if (list == null || list.size() <= 0) {
						throw new AuthenticatorException("ϵͳ��������������ϵ����Ա");
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
					// �ж����ڵ���Ч��
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
					// ȡ���û���¼��ʽ
					itfeinfo.setLogintype(ITFECommonConstant.LOGIN_TYPE);
					// �����û�֤��ID
					itfeinfo.setSsign(userDto.getScertid());

					// ȡ�ط���ɫ�ͼ��ܷ�ʽ,���ڶ�Ӧloginfo��
					HashMap<String, String> encryptMode = (HashMap<String, String>) ContextFactory
							.getApplicationContext().getBean("encryptMode");
					String area = AreaSpecUtil.getInstance().getArea();
					String sysflag = AreaSpecUtil.getInstance().getSysflag();
					itfeinfo.setEncryptMode(encryptMode);
					itfeinfo.setArea(area);
					itfeinfo.setSysflag(sysflag);
					// ȡ���ܷ�ʽ�ǰ�����������ܻ���ȫʡͳһ
					List<TsMankeymodeDto> _dtoList = DatabaseFacade.getODB()
							.find(TsMankeymodeDto.class);
					if (_dtoList.size() > 0) {
						TsMankeymodeDto _dto = _dtoList.get(0);
						if (StateConstant.KEY_ALL.equals(_dto.getSkeymode())) {
							itfeinfo.setMankeyMode(StateConstant.KEY_ALL);// ȫʡͳһά��
						} else if (StateConstant.KEY_BOOK.equals(_dto
								.getSkeymode())) {
							itfeinfo.setMankeyMode(StateConstant.KEY_BOOK); // ����������ά��
						} else {// 
							itfeinfo.setMankeyMode(StateConstant.KEY_BOOK); // Ŀǰ��֧�ְ����������ȫʡͳһ
						}

					} else {
						itfeinfo.setMankeyMode(StateConstant.KEY_BOOK); // Ĭ�ϰ��պ�������ά��
					}
				}
				// ������������
				itfeinfo.setPublicparam(ITFECommonConstant.PUBLICPARAM);
				// ����״̬����
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
				// ����ǹ���Ա��ֱ�ӵ�¼
				if (itfeinfo.getSuserCode().equals(ITFECommonConstant.ADMIN)
						&& StateConstant.ORG_CENTER_CODE.equals(itfeinfo
								.getSorgcode())) {
					adminGetFunc(itfeinfo);

					return itfeinfo;
				}
				// ����Ȩ�ޱ�
				getFunc(itfeinfo);

				return itfeinfo;
			} catch (JAFDatabaseException e) {
				throw new AuthenticatorException("ϵͳ�쳣������ϵ����Ա", e);
			} catch (ValidateException e) {
				throw new AuthenticatorException("��ѯ�û���������ʱ�����쳣������ϵ����Ա", e);
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
				throw new AuthenticatorException("�û�������,���������룡");
			} else {
				if (userDto.getSuserstatus().equals(StateConstant.USERSTATUS_3)) {
					throw new AuthenticatorException("�û�["
							+ loginfo.getSuserCode() + "]�ѱ����ᣬ���ܵ�¼ϵͳ��");
				} else if (userDto.getSuserstatus().equals(
						StateConstant.USERSTATUS_0)) {
					throw new AuthenticatorException("�û�["
							+ loginfo.getSuserCode() + "]�ѱ����ã����ܵ�¼ϵͳ��");
				}else if (userDto.getSuserstatus().equals(
						StateConstant.USERSTATUS_2)) {
					if(ITFECommonConstant.PUBLICPARAM.contains(",userlogin=one,"))
					{
						throw new AuthenticatorException("�û�["
								+ loginfo.getSuserCode() + "]�Ѿ��ڹ����������ظ���¼ϵͳ��");
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
							throw new AuthenticatorException("�û�["
									+ loginfo.getSuserCode()
									+ "]�����ۼ�������Σ����û������ᣡ");
						} else {
							throw new AuthenticatorException("�û�["
									+ loginfo.getSuserCode() + "]����������������룡");
						}
					} else {
						throw new AuthenticatorException("�û�["
								+ loginfo.getSuserCode() + "]����������������룡");
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
				throw new AuthenticatorException("ϵͳ�쳣������ϵ����Ա", e);
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
		return "��¼����";
	}

	public boolean checkVersion(String versoionCode) {
		return ITFECommonConstant.EDITION.equals(versoionCode.trim());
	}

	/**
	 * ��ȡȨ��
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
	 * ϵͳ����ԱȨ��ͨ��ITFEConfig.xml�����ļ���ȡ
	 * 
	 * @param itfeinfo
	 */
	private void adminGetFunc(ITFELoginInfo itfeinfo) {
		itfeinfo.setFunctionList(ITFECommonConstant.ADMIN_FUNC);
	}

	/**
	 * ͨ����ȡ��־�ļ���ȡ���ݿⱸ���ļ���־�ļ����ж����ݿ��Ƿ񱸷ݳɹ�
	 * 
	 * @throws FileOperateException
	 * 
	 */
	private String getlocalLogs() throws FileOperateException {
		String localBakPath = "/itfe/logs/dbbackup.log";
		// �жϱ��������ļ��Ƿ�ɹ�
		File file = new File(localBakPath);
		if (file.exists()) {
			List<String> bakLogLines = FileUtil.getInstance().readFileWithLine(
					localBakPath);
			if (bakLogLines.size() > 0) {
				String logs = bakLogLines.get(bakLogLines.size() - 1);

				if ((logs.contains("successful")||logs.contains("���ݳɹ�"))
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
	 * ͨ����ȡ��־�ļ���ȡ���ݿⱸ���ļ���־�ļ����ж����ݿ��Ƿ񱸷ݳɹ�
	 * 
	 * @throws FileOperateException
	 * 
	 */
	private String getVoucherBakLogs() throws FileOperateException {
		String localBakPath = "/itfe/logs/db2fullbak.log";
		// �ж�ƾ֤���ļ��Ƿ��ϴ�
		File file = new File(localBakPath);
		if (file.exists()) {
			List<String> bakLogLines = FileUtil.getInstance().readFileWithLine(
					localBakPath);
			if (bakLogLines.size() > 0 && bakLogLines.size() <200 ) {
				Boolean flag1=false;
				Boolean flag2= false;
				for(String tmpStr:bakLogLines){
					if(tmpStr.contains("���ݳɹ�")){
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

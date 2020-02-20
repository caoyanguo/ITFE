package com.cfcc.itfe.service.para.tsusers;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TsSysfuncDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TsUsersfuncDto;
import com.cfcc.itfe.persistence.pk.TsUsersPK;
import com.cfcc.itfe.security.Md5App;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author caoyg
 * @time 09-10-20 08:42:02 codecomment:
 */

public class TsUsersService extends AbstractTsUsersService {
	private static Log log = LogFactory.getLog(TsUsersService.class);

	/**
	 * ����
	 * 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException
	 */
	public IDto addInfo(IDto dtoInfo) throws ITFEBizException {
		try {
			TsUsersDto user = (TsUsersDto) dtoInfo;
			user.setSpassword(new Md5App().makeMd5(StateConstant.User_Default_PWD));
			// �ж��û�������,������Ա�����Ա���Է��䲻ͬ��Ȩ��
			// ��һ�� ��Ȩ��ɾ��
			TsUsersfuncDto funcdto = new TsUsersfuncDto();
			funcdto.setSorgcode(user.getSorgcode());
			funcdto.setSusercode(user.getSusercode());
			DatabaseFacade.getDb().delete(funcdto);
			// �㶫Ҫ����û����ս�ɫ����Ȩ�ޣ������ǹ���Ա�ͽ������Ĺ���ԱȨ��
			// ��Ȩ�ޱ��а�Ȩ�޷�Ϊ5��0������˽�й��� 1��ҵ������� 2�������� 3����ѯ�� 4���û����� 5����־��6��ÿ���˶��еĹ���
			List<TsSysfuncDto> list = null;
			TsSysfuncDto _dto = new TsSysfuncDto();
			// �ڶ��� ��Ȩ���ж�,����ͬ���͵Ľ�ɫ���費ͬ�Ĳ˵�Ȩ��
			if (StateConstant.User_Type_Admin.equals(user.getSusertype())) {// ϵͳ����Ա
																			// Ĭ��
																			// 4��5��
				_dto.setSflag(StateConstant.USER_MANAG);
				list = CommonFacade.getODB().findRsByDtoForWhere(_dto,
						" OR S_FLAG = '" + StateConstant.Mod_PASS + "'");
			} else if (StateConstant.User_Type_Normal.equals(user
					.getSusertype())) {// ����Ա Ĭ�� 1��3��5
				_dto.setSflag(StateConstant.BIZ_QUERY);
				if (user.getSorgcode().equals(StateConstant.STAT_CENTER_CODE)
						|| getLoginInfo().getSorgcode().equals(
								StateConstant.ORG_CENTER_CODE)) {
					list = CommonFacade.getODB().findRsByDtoForWhere(
							_dto,
							" OR S_FLAG ='" + StateConstant.BIZ_LOGS
									+ "' OR S_FLAG = '"
									+ StateConstant.Mod_PASS
									+ "' OR S_FLAG = '"
									+ StateConstant.CENTER_PRIFUNC + "' ");
				} else {
					list = CommonFacade.getODB().findRsByDtoForWhere(
							_dto,
							" OR S_FLAG = '" + StateConstant.BIZ_OPER
									+ "' OR S_FLAG ='" + StateConstant.BIZ_LOGS
									+ "' OR S_FLAG = '"
									+ StateConstant.Mod_PASS + "'");
				}
			} else if (StateConstant.User_Type_MainBiz.equals(user
					.getSusertype())) {// ҵ������ Ĭ��2��3��5
				_dto.setSflag(StateConstant.PARAM_MAIN);
				if (user.getSorgcode().equals(StateConstant.STAT_CENTER_CODE)) {
					list = CommonFacade.getODB().findRsByDtoForWhere(
							_dto,
							" OR S_FLAG ='" + StateConstant.BIZ_LOGS
									+ "' OR S_FLAG = '"
									+ StateConstant.Mod_PASS + "'");
				} else {
					list = CommonFacade.getODB().findRsByDtoForWhere(
							_dto,
							"  OR S_FLAG = '" + StateConstant.BIZ_QUERY
									+ "' OR S_FLAG ='" + StateConstant.BIZ_LOGS
									+ "' OR S_FLAG = '"
									+ StateConstant.Mod_PASS + "'");
				}
			} else if (StateConstant.User_Type_Stat.equals(user.getSusertype())) {// ���Ա
																					// Ĭ��
																					// 560
				_dto.setSflag(StateConstant.BIZ_LOGS);
				list = CommonFacade.getODB().findRsByDtoForWhere(
						_dto,
						" OR S_FLAG ='" + StateConstant.BIZ_LOGS
								+ "' OR S_FLAG = '" + StateConstant.Mod_PASS
								+ "' OR S_FLAG = '"
								+ StateConstant.CENTER_PRIFUNC + "' ");

			}
			// ������ ����Ȩ�ޱ�
			TsUsersfuncDto[] tmpfuncs = new TsUsersfuncDto[list.size()];
			for (int i = 0; i < list.size(); i++) {
				tmpfuncs[i] = new TsUsersfuncDto();
				tmpfuncs[i].setSorgcode(user.getSorgcode());
				tmpfuncs[i].setSusercode(user.getSusercode());
				tmpfuncs[i].setSfunccode(list.get(i).getSfunccode());
			}

			DatabaseFacade.getDb().create(tmpfuncs);
			//�������û���Ϣ
			DatabaseFacade.getDb().create(user);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("�ֶ��û������������룬�û������Ѵ��ڣ������ظ�¼�룡", e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		} catch (ValidateException e) {
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}
		return null;
	}

	/**
	 * ${JMethod.getCodecomment()}
	 * 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException
	 */
	public void delInfo(IDto dtoInfo) throws ITFEBizException {
		try {
			// ���ݿ��������
			TsUsersDto user = (TsUsersDto) dtoInfo;
			String strWhere = "where s_orgcode='" + user.getSorgcode()
					+ "' and s_usercode='" + user.getSusercode() + "'";
			// ����ɾ���û��ĸ���Ȩ��
			String sql = "delete from ts_userstampfunction " + strWhere;
			DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor()
					.runQueryCloseCon(sql);
			// ����ɾ���û������ǩ�¶��ڹ�ϵ
			sql = "delete from ts_userstamp " + strWhere;
			DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor()
					.runQueryCloseCon(sql);
			// ����ɾ���û�������Ȩ��
			sql = "delete from ts_usersfunc " + strWhere;
			DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor()
					.runQueryCloseCon(sql);
			// ɾ���û�
			CommonFacade.getODB().deleteRsByDto(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		}
	}

	/**
	 * ${JMethod.getCodecomment()}
	 * 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException
	 */
	public void modInfo(IDto dtoInfo) throws ITFEBizException {
		try {
			TsUsersDto tmpDto=(TsUsersDto) dtoInfo;
//			if (tmpDto.getSuserstatus().equals(StateConstant.USERSTATUS_1)) {
//				tmpDto.setShold1("0");
//			}else {
				tmpDto.setShold1("0");
//			}
			DatabaseFacade.getDb().update(tmpDto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("�ֶ��û������������룬�û������Ѵ��ڣ������ظ�¼�룡", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		}
	}


	public TsUsersDto getSingleInfo(TsUsersDto searchDto)
			throws ITFEBizException {
		// TODO Auto-generated method stub
		TsUsersPK pk =new TsUsersPK();
		pk.setSorgcode(searchDto.getSorgcode());
		pk.setSusercode(searchDto.getSusercode());
		try {
			return  (TsUsersDto) DatabaseFacade.getODB().find(pk);
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.info("��ѯ�û���Ϣ�쳣");
			throw new ITFEBizException("��ѯ�û���Ϣ�쳣",e);
		}
	}

}
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
	 * 增加
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
			// 判断用户的类型,给操作员或管理员各自分配不同的权限
			// 第一步 做权限删除
			TsUsersfuncDto funcdto = new TsUsersfuncDto();
			funcdto.setSorgcode(user.getSorgcode());
			funcdto.setSusercode(user.getSusercode());
			DatabaseFacade.getDb().delete(funcdto);
			// 广东要求给用户按照角色设置权限，尤其是管理员和结算中心管理员权限
			// 在权限表中把权限分为5类0、中心私有功能 1、业务操作类 2、参数类 3、查询类 4、用户管理 5、日志类6、每个人都有的功能
			List<TsSysfuncDto> list = null;
			TsSysfuncDto _dto = new TsSysfuncDto();
			// 第二步 做权限判断,给不同类型的角色赋予不同的菜单权限
			if (StateConstant.User_Type_Admin.equals(user.getSusertype())) {// 系统管理员
																			// 默认
																			// 4、5类
				_dto.setSflag(StateConstant.USER_MANAG);
				list = CommonFacade.getODB().findRsByDtoForWhere(_dto,
						" OR S_FLAG = '" + StateConstant.Mod_PASS + "'");
			} else if (StateConstant.User_Type_Normal.equals(user
					.getSusertype())) {// 操作员 默认 1、3、5
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
					.getSusertype())) {// 业务主管 默认2、3、5
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
			} else if (StateConstant.User_Type_Stat.equals(user.getSusertype())) {// 监控员
																					// 默认
																					// 560
				_dto.setSflag(StateConstant.BIZ_LOGS);
				list = CommonFacade.getODB().findRsByDtoForWhere(
						_dto,
						" OR S_FLAG ='" + StateConstant.BIZ_LOGS
								+ "' OR S_FLAG = '" + StateConstant.Mod_PASS
								+ "' OR S_FLAG = '"
								+ StateConstant.CENTER_PRIFUNC + "' ");

			}
			// 第三步 保存权限表
			TsUsersfuncDto[] tmpfuncs = new TsUsersfuncDto[list.size()];
			for (int i = 0; i < list.size(); i++) {
				tmpfuncs[i] = new TsUsersfuncDto();
				tmpfuncs[i].setSorgcode(user.getSorgcode());
				tmpfuncs[i].setSusercode(user.getSusercode());
				tmpfuncs[i].setSfunccode(list.get(i).getSfunccode());
			}

			DatabaseFacade.getDb().create(tmpfuncs);
			//保存新用户信息
			DatabaseFacade.getDb().create(user);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("字段用户所属机构代码，用户代码已存在，不能重复录入！", e);
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
			// 数据库操作对象
			TsUsersDto user = (TsUsersDto) dtoInfo;
			String strWhere = "where s_orgcode='" + user.getSorgcode()
					+ "' and s_usercode='" + user.getSusercode() + "'";
			// 级联删除用户的盖章权限
			String sql = "delete from ts_userstampfunction " + strWhere;
			DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor()
					.runQueryCloseCon(sql);
			// 级联删除用户与电子签章对于关系
			sql = "delete from ts_userstamp " + strWhere;
			DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor()
					.runQueryCloseCon(sql);
			// 级联删除用户的所有权限
			sql = "delete from ts_usersfunc " + strWhere;
			DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor()
					.runQueryCloseCon(sql);
			// 删除用户
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
				throw new ITFEBizException("字段用户所属机构代码，用户代码已存在，不能重复录入！", e);
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
			log.info("查询用户信息异常");
			throw new ITFEBizException("查询用户信息异常",e);
		}
	}

}
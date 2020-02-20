package com.cfcc.itfe.service.para.tsusersfunc;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.util.PropertiesUtils;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsSysfuncDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TsUsersfuncDto;
import com.cfcc.itfe.persistence.dto.TsUserstampfunctionDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author caoyg
 * @time 09-10-20 08:42:01 codecomment:
 */

public class TsUsersfuncService extends AbstractTsUsersfuncService {
	private static Log log = LogFactory.getLog(TsUsersfuncService.class);

	/**
	 * ${JMethod.getCodecomment()}
	 * 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException
	 */
	public void delInfo(IDto dtoInfo) throws ITFEBizException {
		try {
			TsUsersDto dto = (TsUsersDto) dtoInfo;
			SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String sql = "delete from  ts_usersfunc where S_ORGCODE =? and S_usercode=?";
			exec.addParam(dto.getSorgcode());
			exec.addParam(dto.getSusercode());
			exec.runQueryCloseCon(sql);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("删除用户功能码出错", e);
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

	}

	public List queryOrg(IDto dtoInfo) throws ITFEBizException {
		try {
			SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String sql = "select S_ORGCODE,S_ORGNAME from ts_organ where S_ORGCODE =? or S_GOVERNORGCODE=?";
			exec.addParam(getLoginInfo().getSorgcode());
			exec.addParam(getLoginInfo().getSorgcode());
			List<TsOrganDto> list = (List<TsOrganDto>) exec.runQueryCloseCon(
					sql, TsOrganDto.class).getDtoCollection();
			return list;
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询核算主体代码出错", e);
		}
	}

	public List queryUserFunc(TsUsersDto dtoInfo) throws ITFEBizException {
		try {
			SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String sql = "SELECT b.S_FLAG,A.S_FUNCCODE as sfunccode,B.S_FUNCNAME as sfuncname FROM "
					+ " TS_USERSFUNC a,TS_SYSFUNC b WHERE "
					+ " A.S_FUNCCODE = B.S_FUNCCODE AND A.S_USERCODE = ? AND A.S_ORGCODE =?";
			exec.addParam(dtoInfo.getSusercode());
			exec.addParam(dtoInfo.getSorgcode());
			List<TsSysfuncDto> list = (List<TsSysfuncDto>) exec
					.runQueryCloseCon(sql, TsSysfuncDto.class)
					.getDtoCollection();
			return list;
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询核算主体代码出错", e);
		}
	}

	public void addInfo(List listInfo) throws ITFEBizException {

		TsUsersfuncDto dto = (TsUsersfuncDto) listInfo.get(0);
		try {
			SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String sql = "DELETE FROM  TS_USERSFUNC WHERE S_ORGCODE =? AND S_USERCODE=?";
			exec.addParam(dto.getSorgcode());
			exec.addParam(dto.getSusercode());
			exec.runQueryCloseCon(sql);
			if (null != dto.getSfunccode()
					&& dto.getSfunccode().trim().length() > 0) {
				DatabaseFacade.getDb().create(CommonUtil.listTArray(listInfo));
			}
		} catch (JAFDatabaseException e1) {
			log.error(e1);
			throw new ITFEBizException("增加用户功能码出错", e1);
		}
	}

	@SuppressWarnings("unchecked")
	public List sysFuncList(TsUsersDto dtoInfo) throws ITFEBizException {
		List<TsSysfuncDto> list = null;
		TsSysfuncDto _dto = new TsSysfuncDto();
		try {
			// 第二步 做权限判断,给不同类型的角色赋予不同的菜单权限
			if (StateConstant.User_Type_Admin.equals(dtoInfo.getSusertype())) {// 系统管理员
				   												// 默认// 5、6、7、8、9类
				_dto.setSflag(StateConstant.USER_MANAG);
				if (dtoInfo.getSorgcode()
						.equals(StateConstant.STAT_CENTER_CODE)){
					 list = CommonFacade.getODB().findRsByDtoForWhere(
								_dto,
								"  OR S_FLAG = '" + StateConstant.Mod_PASS+"'");
				}else{
				  list = CommonFacade.getODB().findRsByDtoForWhere(
						_dto,
						"  OR S_FLAG = '" + StateConstant.BIZ_LOGS
								+ "' OR S_FLAG = '" + StateConstant.Mod_PASS
								+ "' OR S_FLAG = '" + StateConstant.ZERO_ONLY
								+ "' OR S_FLAG = '" + StateConstant.ZERO_ONE
								+ "' OR S_FLAG = '" + StateConstant.ZERO_TWO+ "'");
				}
			} else if (StateConstant.User_Type_Normal.equals(dtoInfo
					.getSusertype()) ) {// 操作员 默认 1、3、5、6、8
				_dto.setSflag(StateConstant.BIZ_QUERY);
				if (dtoInfo.getSorgcode()
						.equals(StateConstant.STAT_CENTER_CODE)) {
					list = CommonFacade.getODB().findRsByDtoForWhere(
							_dto,
							" OR S_FLAG ='" + StateConstant.BIZ_LOGS
									+ "' OR S_FLAG = '"
									+ StateConstant.Mod_PASS + "' OR S_FLAG = '"+StateConstant.CENTER_PRIFUNC+"'");//0、5、6
				} else {
					list = CommonFacade.getODB().findRsByDtoForWhere(
							_dto,
							" OR S_FLAG = '" + StateConstant.BIZ_OPER
									+ "' OR S_FLAG ='" + StateConstant.BIZ_LOGS
									+ "' OR S_FLAG = '" + StateConstant.ZERO_ONE
									+ "' OR S_FLAG = '"
									+ StateConstant.Mod_PASS + "'");//1、5、6、8
				}
			} else if (StateConstant.User_Type_MainBiz.equals(dtoInfo
					.getSusertype())) {// 业务主管 默认2、3、5、6、9
				_dto.setSflag(StateConstant.PARAM_MAIN);
				if (dtoInfo.getSorgcode()
						.equals(StateConstant.STAT_CENTER_CODE)) {
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
									+ "' OR S_FLAG = '" + StateConstant.ZERO_TWO
									+ "' OR S_FLAG = '"
									+ StateConstant.Mod_PASS + "'");
				}
			} else if(StateConstant.User_Type_Stat.equals(dtoInfo.getSusertype())){
				_dto.setSflag(StateConstant.BIZ_LOGS);//0、5、6

				list = CommonFacade.getODB().findRsByDtoForWhere(
						_dto,
						"  OR S_FLAG = '" + StateConstant.CENTER_PRIFUNC
								+ "' OR S_FLAG = '" + StateConstant.Mod_PASS
								+ "'");
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询系统功能码列表出错！", e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("查询系统功能码列表出错！", e);
		}
		return list;
	}

	/**
	 * 初始化系统权限
	 */
	public List initSysFunc(TsSysfuncDto dto) throws ITFEBizException {
		List<TsSysfuncDto> sysfunclist=new ArrayList<TsSysfuncDto>();
		try {
			sysfunclist=  CommonFacade.getODB().findRsByDtoForWhere(dto, " AND  S_FLAG!='N' ");
		} catch (JAFDatabaseException e) {
			e.printStackTrace();
			log.error(e);
			throw new ITFEBizException("初始化系统功能码列表异常！", e);
		} catch (ValidateException e) {
			e.printStackTrace();
			log.error(e);
			throw new ITFEBizException("初始化系统功能码列表异常！", e);
		}
		return sysfunclist;
	}

}
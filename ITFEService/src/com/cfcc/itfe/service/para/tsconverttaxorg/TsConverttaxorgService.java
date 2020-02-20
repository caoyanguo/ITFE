package com.cfcc.itfe.service.para.tsconverttaxorg;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.core.service.filetransfer.support.FileSystemConfig;
import com.cfcc.jaf.persistence.dao.exception.JAFDB2Exception;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author caoyg
 * @time 09-10-20 08:42:01 codecomment:
 */

public class TsConverttaxorgService extends AbstractTsConverttaxorgService {
	private static Log log = LogFactory.getLog(TsConverttaxorgService.class);

	/**
	 * 增加
	 * 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException
	 */
	public IDto addInfo(IDto dtoInfo) throws ITFEBizException {
		SQLExecutor selectExce = null;

		try {
			DatabaseFacade.getDb().create(dtoInfo);

			// 校验国地TBS机关代码的唯一性
			String orgcode = this.getLoginInfo().getSorgcode();
			selectExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			// 1.1 校验国税机关代码
			String nationsql = " select S_NATIONTAXORGCODE ,S_TRECODE ,count(*) as num from TS_CONVERTTAXORG " + " where S_ORGCODE = ? and S_NATIONTAXORGCODE <> 'N' group by S_NATIONTAXORGCODE , S_TRECODE having count(*) >= 2";
			selectExce.addParam(orgcode);
			SQLResults nationrs = selectExce.runQuery(nationsql);
			if (nationrs.getRowCount() > 0) {
				String errinfo = nationrs.getString(0, 0) + "_" + nationrs.getString(0, 1);
				selectExce.closeConnection();
				throw new ITFEBizException("国税征收机关_国库代码[" + errinfo + "]重复");
			}

			// 1.2 校验地税机关代码
			String placesql = " select S_AREATAXORGCODE ,S_TRECODE ,count(*) as num from TS_CONVERTTAXORG " + " where S_ORGCODE = ? and S_AREATAXORGCODE <> 'N' group by S_AREATAXORGCODE , S_TRECODE having count(*) >= 2";
			selectExce.clearParams();
			selectExce.addParam(orgcode);
			SQLResults palcers = selectExce.runQuery(placesql);
			if (palcers.getRowCount() > 0) {
				String errinfo = palcers.getString(0, 0) + "_" + palcers.getString(0, 1);
				selectExce.closeConnection();
				throw new ITFEBizException("地税征收机关_国库代码[" + errinfo + "]重复");
			}

			// 1.3 校验TBS机关代码
			String tbssql = " select S_TCBSTAXORGCODE, S_TBSTAXORGCODE ,S_TRECODE ,count(*) as num from TS_CONVERTTAXORG "
					+ " where S_ORGCODE = ? and S_TBSTAXORGCODE <> 'N' group by S_TCBSTAXORGCODE, S_TBSTAXORGCODE , S_TRECODE having count(*) >= 2";
			selectExce.clearParams();
			selectExce.addParam(orgcode);
			SQLResults tbsrs = selectExce.runQuery(tbssql);
			if (tbsrs.getRowCount() > 0) {
				String errinfo = tbsrs.getString(0, 0) + "_" + tbsrs.getString(0, 1);
				selectExce.closeConnection();
				throw new ITFEBizException("TBS征收机关_国库代码[" + errinfo + "]重复");
			}

			if (null != selectExce) {
				selectExce.closeConnection();
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("字段核算主体代码，国库主体代码，横联征收机关代码已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		} finally {
			if (null != selectExce) {
				selectExce.closeConnection();
			}
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
	 * @param initInfo
	 * @param dtoInfo
	 * @throws ITFEBizException
	 */
	public void modInfo(IDto initInfo, IDto dtoInfo) throws ITFEBizException {
		SQLExecutor selectExce = null;

		try {
			TsConverttaxorgDto wheredto = (TsConverttaxorgDto) initInfo;
			TsConverttaxorgDto updatedto = (TsConverttaxorgDto) dtoInfo;
			SQLExecutor updatesqlExec = null;

			String updateSql = "update TS_CONVERTTAXORG set S_NATIONTAXORGCODE = ? , S_AREATAXORGCODE = ? ,S_TBSTAXORGCODE = ? , S_TCBSTAXORGCODE = ?, I_MODICOUNT = ? where ";
			updateSql = updateSql + "S_ORGCODE = ? and S_TRECODE = ? and S_TBSTAXORGCODE = ? and S_NATIONTAXORGCODE = ? and S_AREATAXORGCODE = ? ";

			updatesqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updatesqlExec.addParam(updatedto.getSnationtaxorgcode());
			updatesqlExec.addParam(updatedto.getSareataxorgcode());
			updatesqlExec.addParam(updatedto.getStbstaxorgcode());
			updatesqlExec.addParam(updatedto.getStcbstaxorgcode());
			updatesqlExec.addParam(updatedto.getImodicount());
			updatesqlExec.addParam(wheredto.getSorgcode());
			updatesqlExec.addParam(wheredto.getStrecode());
			updatesqlExec.addParam(wheredto.getStbstaxorgcode());
			updatesqlExec.addParam(wheredto.getSnationtaxorgcode());
			updatesqlExec.addParam(wheredto.getSareataxorgcode());
			updatesqlExec.runQueryCloseCon(updateSql);

			// DatabaseFacade.getDb().update(dtoInfo);

			// 校验国地TBS机关代码的唯一性
			String orgcode = this.getLoginInfo().getSorgcode();
			selectExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			// 1.1 校验国税机关代码
			String nationsql = " select S_NATIONTAXORGCODE ,S_TRECODE ,count(*) as num from TS_CONVERTTAXORG " + " where S_ORGCODE = ? and S_NATIONTAXORGCODE <> 'N' group by S_NATIONTAXORGCODE , S_TRECODE having count(*) >= 2";
			selectExce.addParam(orgcode);
			SQLResults nationrs = selectExce.runQuery(nationsql);
			if (nationrs.getRowCount() > 0) {
				String errinfo = nationrs.getString(0, 0) + "_" + nationrs.getString(0, 1);
				selectExce.closeConnection();
				throw new ITFEBizException("国税征收机关_国库代码[" + errinfo + "]重复");
			}

			// 1.2 校验地税机关代码
			String placesql = " select S_AREATAXORGCODE ,S_TRECODE ,count(*) as num from TS_CONVERTTAXORG " + " where S_ORGCODE = ? and S_AREATAXORGCODE <> 'N' group by S_AREATAXORGCODE , S_TRECODE having count(*) >= 2";
			selectExce.clearParams();
			selectExce.addParam(orgcode);
			SQLResults palcers = selectExce.runQuery(placesql);
			if (palcers.getRowCount() > 0) {
				String errinfo = palcers.getString(0, 0) + "_" + palcers.getString(0, 1);
				selectExce.closeConnection();
				throw new ITFEBizException("地税征收机关_国库代码[" + errinfo + "]重复");
			}

			// 1.3 校验TBS机关代码
			String tbssql = " select S_TBSTAXORGCODE ,S_TRECODE ,count(*) as num from TS_CONVERTTAXORG " + " where S_ORGCODE = ? and S_TBSTAXORGCODE <> 'N' group by S_TBSTAXORGCODE , S_TRECODE having count(*) >= 2";
			selectExce.clearParams();
			selectExce.addParam(orgcode);
			SQLResults tbsrs = selectExce.runQuery(tbssql);
			if (tbsrs.getRowCount() > 0) {
				String errinfo = tbsrs.getString(0, 0) + "_" + tbsrs.getString(0, 1);
				selectExce.closeConnection();
				throw new ITFEBizException("TCBS征收机关_国库代码[" + errinfo + "]重复");
			}

			if (null != selectExce) {
				selectExce.closeConnection();
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("字段核算主体代码，国库主体代码，横联征收机关代码已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		} finally {
			if (null != selectExce) {
				selectExce.closeConnection();
			}
		}
	}

	public void dataimport(String filepath, String importtype) throws ITFEBizException {
		try {
			FileSystemConfig sysconfig = (FileSystemConfig) ContextFactory.getApplicationContext().getBean("fileSystemConfig.ITFE.ID");
			String root = sysconfig.getRoot();
			String filerootpath = (root + filepath).replace("/", File.separator).replace("\\", File.separator);
			List<String[]> fileContent = FileUtil.getInstance().readFileWithLine(filerootpath, ",");
			if (fileContent != null && fileContent.size() > 0) {
				HashMap<String, TsConverttaxorgDto> taxorgMap = SrvCacheFacade.cacheTaxInfo(this.getLoginInfo().getSorgcode());
				List<IDto> dtoList = new ArrayList<IDto>();
				TsConverttaxorgDto dto = null;
				for (int i = 0; i < fileContent.size(); i++) {
					String[] temp = fileContent.get(i);

					if (taxorgMap.containsKey(temp[1] + temp[2]) && "addimport".equals(importtype)) {
						continue;
					}
					dto = new TsConverttaxorgDto();
					dto.setSorgcode(temp[0]);
					if (dto.getSorgcode() == null || dto.getSorgcode().length() != 12) {
						throw new ITFEBizException("导入数据失败:核算主体代码必须等于12位", null);
					}
					dto.setStrecode(temp[1]);
					if (dto.getStrecode() == null || dto.getStrecode().length() != 10) {
						throw new ITFEBizException("导入数据失败:国库主体代码必须等于10位", null);
					}
					dto.setStbstaxorgcode(temp[2]);
					if (dto.getStbstaxorgcode() == null || dto.getStbstaxorgcode().length() > 20) {
						throw new ITFEBizException("导入数据失败:TBS征收机关代码必须小于等于20位", null);
					}
					dto.setStcbstaxorgcode(temp[3]);
					if (dto.getStcbstaxorgcode() == null || dto.getStcbstaxorgcode().length() > 12) {
						throw new ITFEBizException("导入数据失败:TCBS征收机关代码必须小于等于12位", null);
					}
					dto.setSnationtaxorgcode("N");
					dto.setSareataxorgcode("N");
					if (!"1".equals(temp[4]) && !"2".equals(temp[4]) && !"3".equals(temp[4]) && !"4".equals(temp[4]) && !"5".equals(temp[4])) {
						throw new ITFEBizException("导入数据失败:征收机关性质必须为1、2、3、4、5其中之一", null);
					}
					dto.setImodicount(Integer.valueOf(temp[4]));
					dtoList.add(dto);

				}
				if ("deleteimport".equals(importtype)) {
					String delSQL = "DELETE FROM TS_CONVERTTAXORG where S_ORGCODE='" + this.getLoginInfo().getSorgcode() + "'";
					SQLExecutor sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
					sqlExec.runQueryCloseCon(delSQL);
				}
				DatabaseFacade.getDb().create(CommonUtil.listTArray(dtoList));
				FileUtil.getInstance().deleteFile(filerootpath);
			}
		} catch (Exception e) {
			log.error(e);
			if(e instanceof JAFDB2Exception) {
				JAFDB2Exception jafE = (JAFDB2Exception) e;
				if (jafE.getSqlState().equals("23505")) {
					throw new ITFEBizException("数据中存在重复数据，可能是没有刷新后台缓存导致，请操作后重试!");
				}
			}
			throw new ITFEBizException("导入数据失败:", e);
		}
	}
}
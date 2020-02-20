package com.cfcc.itfe.service.recbiz.bankvouchervalidate;

import static com.cfcc.itfe.service.recbiz.bankvouchervalidate.AbstractBankVoucherValidateProcesser.resloveFileType;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import sun.reflect.misc.ReflectUtil;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.BankValidateDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfGrantpaymsgsubDto;
import com.cfcc.itfe.service.sendbiz.exporttbsforbj.BasicTBSFileProcesser;
import com.cfcc.itfe.service.sendbiz.exporttbsforbj.IProcessHandler;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * 对导入的商行凭证信息进行校验<8202|5201 -> 2301>
 * 
 * @author hua
 * @time 15-04-03 10:29:55
 */

public class BankVoucherValidateService extends AbstractBankVoucherValidateService {
	private static Log log = LogFactory.getLog(BankVoucherValidateService.class);

	/** 定义处理器的基名 **/
	private static final String BASE_PROCESSER_NAME = "com.cfcc.itfe.service.recbiz.bankvouchervalidate.processer.Process";

	/** 定义支持的文件类型 **/
	// private static final String[] validFileTypes = new String[] { "8202", "5201", "2301" }; // 2301导入只是在测试时使用
	private static final String[] validFileTypes = new String[] { "8202", "5201" };

	/**
	 * 导入文件
	 * 
	 * @param fileList
	 *            要导入的文件列表
	 * @param paramDto
	 *            参数对象
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public MulitTableDto voucherImport(List fileList, IDto paramDto) throws ITFEBizException {
		/** 1. 先解析报文文件 */
		if (fileList == null || fileList.size() == 0) {
			throw new RuntimeException("上传至服务端的文件列表为空!");
		}
		List<String> filePathList = fileList;
		MulitTableDto resultDto = new MulitTableDto();
		resultDto.setTotalCount(fileList.size());
		try {
			SQLExecutor sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.runQuery("update TV_PAYRECK_BANK_LIST set S_HOLD4='2' where S_HOLD4='' or S_HOLD4 is null");
			for (String fileName : filePathList) {
				String fullFileName = BasicTBSFileProcesser.getServerFilePath(fileName);
				/** TODO 解析的一个问题在于我无法提前知道是什么类型的报文.既然不能完全靠文件名获得类型，只好自己解析文件得到了，效率较低(或者考虑使用NIO) */
				String fileType = resloveFileType(fullFileName);
				if (StringUtils.isBlank(fileType) || !isValidFileType(fileType)) {
					addFileErrorInfo2Result(resultDto, new File(fullFileName).getName() + ": 不支持的报文类型, 请查证!", fullFileName);
					continue;
				}
				MulitTableDto processResult = null;
				try {
					/** 2. 解析报文 */
					IProcessHandler processer = generateProcesser(fileType);
					BeanUtils.setProperty(processer, "loginfo", getLoginInfo());
					processResult = processer.process(fullFileName);

					/** 3. 合并结果 */
					BasicTBSFileProcesser.copyProcessRes2Result(fullFileName, processResult, resultDto);

					/** 4. 如果结果中产生了错误信息，则不往下走 */
					if (processResult != null && processResult.getErrorList().size() > 0) {
						continue;
					}

					/** 5. 删除重复主表规则 : 行政区划+报文类型+凭证编号+支付金额 */
					delRepeatVoucher(processResult, fileType);

					/** 保存新数据 */
					saveResult(processResult);

				} catch (Exception e) {
					/** 6.出现异常,则记录问题信息,继续处理下一个文件 **/
					if (processResult != null && processResult.getErrorList().size() > 0) {
						resultDto.getErrorList().addAll(processResult.getErrorList());
					}
					addFileErrorInfo2Result(resultDto, new File(fullFileName).getName() + " : " + e.getMessage(), fullFileName);
					log.error(e);
					continue;
				}
			}
		} catch (Exception e) {
			log.error(e);
			throw new RuntimeException("导入报文文件出现异常!", e);
		}
		return resultDto;
	}

	/**
	 * 删除重复导入的数据
	 * 
	 * @param processResult
	 * @param fileType
	 * @throws SQLException
	 */
	private void delRepeatVoucher(MulitTableDto processResult, String fileType) throws SQLException {
		List<String> delMainList = processResult.getVoulist();
		String paramString = delMainList.size() > 0 ? delMainList.get(0) : "";
		if (!StringUtils.isBlank(paramString)) { // 只有参数值不为空, 才进行删除操作
			try {
				StringBuilder delMainSql = null;
				StringBuilder delSubSql = null;

				/** 1. 得到删除条件 */
				String[] params = (getLoginInfo().getSorgcode() + "#" + paramString).split("#");

				/** 2. 根据报文类型生成删除语句 */
				if (validFileTypes[0].equals(fileType)) { // 8202
					delMainSql = generateDelMainSql(TfGrantpaymsgmainDto.tableName());
					delSubSql = generateDelSubSql(TfGrantpaymsgmainDto.tableName(), TfGrantpaymsgsubDto.tableName());
				} else if (validFileTypes[1].equals(fileType)) { // 5201
					delMainSql = generateDelMainSql(TfDirectpaymsgmainDto.tableName());
					delSubSql = generateDelSubSql(TfDirectpaymsgmainDto.tableName(), TfDirectpaymsgsubDto.tableName());
				}

				/** 3. 开启事务 */
				JdbcUtils.startTransaction();
				/** 4. 删除子表 要注意先删子表, 然后再去删除主表 */
				JdbcUtils.executeUpdate(JdbcUtils.getConnection(), delSubSql.toString(), params); // 删除子表
				/** 5. 删除主表 */
				JdbcUtils.executeUpdate(JdbcUtils.getConnection(), delMainSql.toString(), params); // 删除主表
				/** 6. 提交事务 */
				JdbcUtils.commit();

			} catch (SQLException e) {
				/** 回滚事务 */
				JdbcUtils.rollback();
			} finally {
				/** 释放资源 */
				JdbcUtils.release();
			}
		}
	}

	/**
	 * 产生删除重复主表SQL语句
	 * 
	 * @param mainTableName
	 * @return
	 */
	private StringBuilder generateDelMainSql(String mainTableName) {
		StringBuilder mainSql = new StringBuilder();
		mainSql.append("DELETE ").append(generateDelFromSqlCondition(mainTableName));
		return mainSql;
	}

	/**
	 * 产生删除重复子表SQL语句
	 * 
	 * @param subTableName
	 * @return
	 */
	private StringBuilder generateDelSubSql(String mainTableName, String subTableName) {
		StringBuilder subSql = new StringBuilder();
		subSql.append("DELETE FROM ")//
				.append(subTableName)//
				.append(" WHERE I_VOUSRLNO IN ( ")//
				.append(" SELECT I_VOUSRLNO ").append(generateDelFromSqlCondition(mainTableName))//
				.append(")");
		return subSql;
	}

	/**
	 * 产生删除语句的条件部分(提取出来, 以后修改只需要修改这一处即可.)
	 * 
	 * @param tableName
	 * @return
	 */
	private StringBuilder generateDelFromSqlCondition(String tableName) {
		StringBuilder fromSql = new StringBuilder();
		fromSql.append(" FROM " + tableName + " WHERE S_ORGCODE=? AND S_ADMDIVCODE=? AND S_VTCODE=? AND S_VOUCHERNO=? AND N_PAYAMT=? ");
		return fromSql;
	}

	/**
	 * 添加错误信息到结果
	 * 
	 * @param resultDto
	 * @param errorInfo
	 * @param fileName
	 */
	private void addFileErrorInfo2Result(MulitTableDto resultDto, String errorInfo, String fileName) {
		if (!StringUtils.isBlank(errorInfo)) {
			resultDto.getErrorList().add(errorInfo);
		}
		resultDto.getErrNameList().add(new File(fileName).getName());
		resultDto.setErrorCount(resultDto.getErrorCount() + 1);
	}

	/**
	 * 判断文件是否是支持的文件类型
	 * 
	 * @param fileType
	 * @return
	 */
	private boolean isValidFileType(String fileType) {
		for (String validFileType : validFileTypes) {
			if (StringUtils.equals(fileType, validFileType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 开始比对数据
	 * 
	 * @param startDate
	 *            开始比对日期
	 * @param endDate
	 *            结束比对日期
	 * @param paramDto
	 *            参数对象
	 * @return
	 * @throws ITFEBizException
	 */
	public MulitTableDto voucherCompare(String startDate, String endDate, IDto paramDto) throws ITFEBizException {
		// 5201 merge语句
		StringBuilder directMergeSql = generateMergeSqlByTable(TfDirectpaymsgsubDto.tableName());
		// 8202 merge语句
		StringBuilder grantMergeSql = generateMergeSqlByTable(TfGrantpaymsgsubDto.tableName());

		// 5201 update语句
		StringBuilder directUpdateSql = genenrateUpdateSqlByTable(TfDirectpaymsgsubDto.tableName());
		// 8202 update语句
		StringBuilder grantUpdateSql = genenrateUpdateSqlByTable(TfGrantpaymsgsubDto.tableName());

		// execCompareByJdbc(startDate, endDate, directMergeSql, grantMergeSql, directUpdateSql, grantUpdateSql);

		execCompareByJaf(startDate, endDate, directMergeSql, grantMergeSql, directUpdateSql, grantUpdateSql);

		return null;
	}

	/**
	 * 使用JAF控制事务
	 * 
	 * @param startDate
	 * @param endDate
	 * @param directMergeSql
	 * @param grantMergeSql
	 * @param directUpdateSql
	 * @param grantUpdateSql
	 */
	private void execCompareByJaf(String startDate, String endDate, StringBuilder directMergeSql, StringBuilder grantMergeSql, StringBuilder directUpdateSql, StringBuilder grantUpdateSql) {
		SQLExecutor sqlExec = null;
		SQLResults rs = null;
		try {
			/** 执行merge语句 */
			List paramList = Arrays.asList(getLoginInfo().getSorgcode(),//
					formatDateString(startDate),//
					formatDateString(endDate),//
//					DealCodeConstants.DEALCODE_ITFE_SUCCESS,//
					StateConstant.MERGE_VALIDATE_SUCCESS,//
					StateConstant.MERGE_VALIDATE_FAILURE);
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();

			clearAndaddParam2SQLExector(sqlExec, paramList);
			sqlExec.runQuery(directMergeSql.toString()); // 5201Merge

			clearAndaddParam2SQLExector(sqlExec, paramList);
			sqlExec.runQuery(grantMergeSql.toString()); // 8202Merge

			/** 执行update语句 */
			paramList = Arrays.asList(getLoginInfo().getSorgcode(),//
					formatDateString(startDate),//
					formatDateString(endDate));//
//					DealCodeConstants.DEALCODE_ITFE_SUCCESS)

			clearAndaddParam2SQLExector(sqlExec, paramList);
			sqlExec.runQuery(directUpdateSql.toString()); // 5201更新

			clearAndaddParam2SQLExector(sqlExec, paramList);
			sqlExec.runQueryCloseCon(grantUpdateSql.toString()); // 8202更新
		} catch (Exception e) {
			log.error("比对数据出现异常", e);
			throw new RuntimeException("比对数据异常!", e);
		} finally {
			if (rs != null) {
				rs = null;
			}
			if (sqlExec != null) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * 使用JDBC控制事务
	 * 
	 * @param startDate
	 * @param endDate
	 * @param directMergeSql
	 * @param grantMergeSql
	 * @param directUpdateSql
	 * @param grantUpdateSql
	 */
	private void execCompareByJdbc(String startDate, String endDate, StringBuilder directMergeSql, StringBuilder grantMergeSql, StringBuilder directUpdateSql, StringBuilder grantUpdateSql) {
		try {
			/** 1. 开启事务 */
			JdbcUtils.startTransaction();

			/** 2. 执行merge */
			Object[] params = new Object[] { getLoginInfo().getSorgcode(),//
					formatDateString(startDate),//
					formatDateString(endDate),//
					DealCodeConstants.DEALCODE_ITFE_SUCCESS,//
					StateConstant.MERGE_VALIDATE_SUCCESS,//
					StateConstant.MERGE_VALIDATE_FAILURE };
			JdbcUtils.executeUpdate(JdbcUtils.getConnection(), directMergeSql.toString(), params); // 5201Merge
			JdbcUtils.executeUpdate(JdbcUtils.getConnection(), grantMergeSql.toString(), params); // 8202Merge

			/** 3. 执行update */
			params = new Object[] { getLoginInfo().getSorgcode(),//
					formatDateString(startDate),//
					formatDateString(endDate),//
					DealCodeConstants.DEALCODE_ITFE_SUCCESS };
			JdbcUtils.executeUpdate(JdbcUtils.getConnection(), directUpdateSql.toString(), params); // 5201更新
			JdbcUtils.executeUpdate(JdbcUtils.getConnection(), grantUpdateSql.toString(), params); // 8202更新

			/** 4. 提交事务 */
			JdbcUtils.commit();
		} catch (Exception e) {
			log.error("数据比对出现异常", e);
			try {
				/** 出现异常,回滚事务 */
				JdbcUtils.rollback();
			} catch (SQLException e1) {
				log.error(e);
				throw new RuntimeException("数据比对出现异常(回滚)");
			}
			throw new RuntimeException("数据比对出现异常!", e);
		} finally {
			try {
				/** 5. 释放资源 */
				JdbcUtils.release();
			} catch (SQLException e) {
				log.error(e);
				throw new RuntimeException("数据比对出现异常(提交)");
			}
		}
	}

	/**
	 * 根据报文类型生成merge语句
	 * 
	 * @param tableName
	 * @return
	 */
	private StringBuilder generateMergeSqlByTable(String tableName) {
		StringBuilder sql = new StringBuilder();
		sql.append(" MERGE INTO " + tableName + " b ");
		sql.append(" USING (SELECT * FROM TV_PAYRECK_BANK_LIST WHERE I_VOUSRLNO IN (SELECT I_VOUSRLNO FROM TV_PAYRECK_BANK WHERE S_BOOKORGCODE=? AND D_VOUDATE BETWEEN ? AND ? )) a ");
		sql.append("	 ON (a.S_VOUCHERN0=b.S_VOUCHERBILLNO)");
		sql.append(" WHEN MATCHED");
		sql.append(" 	 AND (a.S_BDGORGCODE=b.S_AGENCYCODE AND a.S_FUNCBDGSBTCODE=b.S_EXPFUNCCODE AND a.F_AMT=b.N_PAYAMT) ");
		sql.append(" THEN UPDATE SET b.S_EXT1=? ");
		sql.append(" WHEN MATCHED");
		sql.append(" 	 AND (a.S_BDGORGCODE<>b.S_AGENCYCODE OR a.S_FUNCBDGSBTCODE<>b.S_EXPFUNCCODE OR a.F_AMT<>b.N_PAYAMT) ");
		sql.append(" THEN UPDATE SET b.S_EXT1=? ");
		sql.append(" ELSE IGNORE ");
		return sql;
	}

	/**
	 * 根据报文类型生成update语句
	 * 
	 * @param tableName
	 * @return
	 */
	private StringBuilder genenrateUpdateSqlByTable(String tableName) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE TV_PAYRECK_BANK_LIST a ");
		sql.append("SET a.S_HOLD4 = (SELECT MAX(S_EXT1) FROM " + tableName + " b WHERE a.S_VOUCHERN0=b.S_VOUCHERBILLNO WITH UR)");
		sql.append("WHERE a.I_VOUSRLNO IN (SELECT I_VOUSRLNO FROM TV_PAYRECK_BANK WHERE S_BOOKORGCODE=? AND D_VOUDATE BETWEEN ? AND ? )");
		sql.append(" AND EXISTS (SELECT 1 FROM " + tableName + " b WHERE b.S_EXT1 IS NOT NULL AND a.S_VOUCHERN0=b.S_VOUCHERBILLNO)");
		return sql;
	}

	/**
	 * 查询比对结果数据(实现分页)
	 * 
	 * @param startDate
	 *            开始比对日期
	 * @param endDate
	 *            结束比对日期
	 * @param paramDto
	 *            参数对象
	 * @param request
	 *            分页对象
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public PageResponse findValidateResult(String startDate, String endDate, IDto paramDto, PageRequest request) throws ITFEBizException {
		SQLExecutor sqlExec = null;
		SQLResults rs = null;

		PageResponse response = new PageResponse(request);

		/** 查询语句 */
		StringBuilder querySql = new StringBuilder();
		querySql.append(" SELECT a.S_AGENTBNKCODE sagentbnkcode,replace(a.D_VOUDATE,'-','') dacctdate,a.S_VOUNO voucherNo,b.S_VOUCHERN0 detailVoucherNo,b.S_BDGORGCODE supdepcode,b.S_FUNCBDGSBTCODE expfunccode,b.F_AMT payamt,COALESCE(b.S_HOLD4,'" + StateConstant.MERGE_VALIDATE_NOTCOMPARE + "') result ");
		querySql.append(generateQueryFromSqlCondition(paramDto));

		/** 用于分页查询总记录数 */
		StringBuilder totalSql = new StringBuilder();
		totalSql.append("select count(*) ").append(generateQueryFromSqlCondition(paramDto));
		try {
			SQLExecutor sqlExecc = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExecc.runQuery("update TV_PAYRECK_BANK_LIST set S_HOLD4='2' where S_HOLD4='' or S_HOLD4 is null");
			/** 设置查询条件参数 */
			List paramList = new ArrayList();
			paramList.add(getLoginInfo().getSorgcode());
			paramList.add(formatDateString(startDate));
			paramList.add(formatDateString(endDate));
//			paramList.add(DealCodeConstants.DEALCODE_ITFE_SUCCESS);

			/** 填充总记录数 */
			if (response.getTotalCount() == 0) {
				sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				clearAndaddParam2SQLExector(sqlExec, paramList);
				int count = sqlExec.runQueryCloseCon(totalSql.toString()).getInt(0, 0);
				if (count == 0) {
					return response;
				}
				if (sqlExec != null) {
					sqlExec.clearParams();
				}
				response.setTotalCount(count);
			}

			/** 填充一页数据 */
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			clearAndaddParam2SQLExector(sqlExec, paramList);
			rs = sqlExec.runQueryCloseCon(querySql.toString(), BankValidateDto.class, request.getStartPosition(), request.getPageSize(), false);

			List list = new ArrayList();
			list.addAll(rs.getDtoCollection());
			response.getData().clear();
			response.setData(list);
			return response;
		} catch (Exception e) {
			log.error("查询数据出现异常", e);
			throw new RuntimeException("数据获取异常!", e);
		} finally {
			if (rs != null) {
				rs = null;
			}
			if (sqlExec != null) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * 将参数值填充到查询对象中
	 * 
	 * @param sqlExec
	 * @param paramList
	 */
	private void clearAndaddParam2SQLExector(SQLExecutor sqlExec, List paramList) {
		if (sqlExec != null && paramList != null && paramList.size() > 0) {
			sqlExec.clearParams();
			for (Object param : paramList) {
				sqlExec.addParam(param);
			}
		}
	}

	/**
	 * 产生查询语句的from部分
	 * 
	 * @param paramDto
	 * @return
	 */
	private String generateQueryFromSqlCondition(IDto paramDto) {
		StringBuilder fromSql = new StringBuilder();
		fromSql.append(" FROM TV_PAYRECK_BANK a,TV_PAYRECK_BANK_LIST b ");
		fromSql.append(" WHERE ");
		fromSql.append(" a.S_BOOKORGCODE=? ");
		fromSql.append(" AND a.D_VOUDATE BETWEEN ? AND ? ");
//		fromSql.append(" AND a.S_RESULT=? ");
		fromSql.append(" AND a.I_VOUSRLNO=b.I_VOUSRLNO ");
		if (paramDto != null && paramDto instanceof BankValidateDto) {
			BankValidateDto validateDto = (BankValidateDto) paramDto;
			String compareResult = validateDto.getRealResult();
			if(compareResult!=null && !compareResult.equals("")){
				fromSql.append(" AND b.S_HOLD4" + "='" + compareResult + "'");
			}
		}
		return fromSql.toString();
	}

	/**
	 * 保存数据
	 * 
	 * @param processResult
	 * @throws JAFDatabaseException
	 */
	private void saveResult(MulitTableDto processResult) throws JAFDatabaseException {
		List<IDto> mainList = processResult.getFatherDtos();
		List<IDto> subList = processResult.getSonDtos();
		if (mainList != null && mainList.size() > 0 && subList != null && subList.size() > 0) {
			DatabaseFacade.getODB().create(CommonUtil.listTArray(mainList));
			DatabaseFacade.getODB().create(CommonUtil.listTArray(subList));
			// 为了事务的统一, 合并后再一起保存
//			IDto[] dtos = (IDto[]) ArrayUtils.addAll(CommonUtil.listTArray(mainList), CommonUtil.listTArray(subList));
//			DatabaseFacade.getODB().create(dtos);
		}
	}

	/**
	 * 产生相应处理类
	 * 
	 * @param msgType
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private IProcessHandler generateProcesser(String msgType) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class clazz = Class.forName(BASE_PROCESSER_NAME + msgType);
		return (IProcessHandler) clazz.newInstance();
	}

	/**
	 * 处理日期格式
	 * 
	 * @param srcDateString
	 * @return
	 */
	private String formatDateString(String srcDateString) {
		if (srcDateString != null && !"".equals(srcDateString) && srcDateString.length() == 8) {
			return srcDateString.substring(0, 4) + "-" + srcDateString.substring(4, 6) + "-" + srcDateString.substring(6);
		}
		return srcDateString;
	}
}
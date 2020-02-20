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
 * �Ե��������ƾ֤��Ϣ����У��<8202|5201 -> 2301>
 * 
 * @author hua
 * @time 15-04-03 10:29:55
 */

public class BankVoucherValidateService extends AbstractBankVoucherValidateService {
	private static Log log = LogFactory.getLog(BankVoucherValidateService.class);

	/** ���崦�����Ļ��� **/
	private static final String BASE_PROCESSER_NAME = "com.cfcc.itfe.service.recbiz.bankvouchervalidate.processer.Process";

	/** ����֧�ֵ��ļ����� **/
	// private static final String[] validFileTypes = new String[] { "8202", "5201", "2301" }; // 2301����ֻ���ڲ���ʱʹ��
	private static final String[] validFileTypes = new String[] { "8202", "5201" };

	/**
	 * �����ļ�
	 * 
	 * @param fileList
	 *            Ҫ������ļ��б�
	 * @param paramDto
	 *            ��������
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public MulitTableDto voucherImport(List fileList, IDto paramDto) throws ITFEBizException {
		/** 1. �Ƚ��������ļ� */
		if (fileList == null || fileList.size() == 0) {
			throw new RuntimeException("�ϴ�������˵��ļ��б�Ϊ��!");
		}
		List<String> filePathList = fileList;
		MulitTableDto resultDto = new MulitTableDto();
		resultDto.setTotalCount(fileList.size());
		try {
			SQLExecutor sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.runQuery("update TV_PAYRECK_BANK_LIST set S_HOLD4='2' where S_HOLD4='' or S_HOLD4 is null");
			for (String fileName : filePathList) {
				String fullFileName = BasicTBSFileProcesser.getServerFilePath(fileName);
				/** TODO ������һ�������������޷���ǰ֪����ʲô���͵ı���.��Ȼ������ȫ���ļ���������ͣ�ֻ���Լ������ļ��õ��ˣ�Ч�ʽϵ�(���߿���ʹ��NIO) */
				String fileType = resloveFileType(fullFileName);
				if (StringUtils.isBlank(fileType) || !isValidFileType(fileType)) {
					addFileErrorInfo2Result(resultDto, new File(fullFileName).getName() + ": ��֧�ֵı�������, ���֤!", fullFileName);
					continue;
				}
				MulitTableDto processResult = null;
				try {
					/** 2. �������� */
					IProcessHandler processer = generateProcesser(fileType);
					BeanUtils.setProperty(processer, "loginfo", getLoginInfo());
					processResult = processer.process(fullFileName);

					/** 3. �ϲ���� */
					BasicTBSFileProcesser.copyProcessRes2Result(fullFileName, processResult, resultDto);

					/** 4. �������в����˴�����Ϣ���������� */
					if (processResult != null && processResult.getErrorList().size() > 0) {
						continue;
					}

					/** 5. ɾ���ظ�������� : ��������+��������+ƾ֤���+֧����� */
					delRepeatVoucher(processResult, fileType);

					/** ���������� */
					saveResult(processResult);

				} catch (Exception e) {
					/** 6.�����쳣,���¼������Ϣ,����������һ���ļ� **/
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
			throw new RuntimeException("���뱨���ļ������쳣!", e);
		}
		return resultDto;
	}

	/**
	 * ɾ���ظ����������
	 * 
	 * @param processResult
	 * @param fileType
	 * @throws SQLException
	 */
	private void delRepeatVoucher(MulitTableDto processResult, String fileType) throws SQLException {
		List<String> delMainList = processResult.getVoulist();
		String paramString = delMainList.size() > 0 ? delMainList.get(0) : "";
		if (!StringUtils.isBlank(paramString)) { // ֻ�в���ֵ��Ϊ��, �Ž���ɾ������
			try {
				StringBuilder delMainSql = null;
				StringBuilder delSubSql = null;

				/** 1. �õ�ɾ������ */
				String[] params = (getLoginInfo().getSorgcode() + "#" + paramString).split("#");

				/** 2. ���ݱ�����������ɾ����� */
				if (validFileTypes[0].equals(fileType)) { // 8202
					delMainSql = generateDelMainSql(TfGrantpaymsgmainDto.tableName());
					delSubSql = generateDelSubSql(TfGrantpaymsgmainDto.tableName(), TfGrantpaymsgsubDto.tableName());
				} else if (validFileTypes[1].equals(fileType)) { // 5201
					delMainSql = generateDelMainSql(TfDirectpaymsgmainDto.tableName());
					delSubSql = generateDelSubSql(TfDirectpaymsgmainDto.tableName(), TfDirectpaymsgsubDto.tableName());
				}

				/** 3. �������� */
				JdbcUtils.startTransaction();
				/** 4. ɾ���ӱ� Ҫע����ɾ�ӱ�, Ȼ����ȥɾ������ */
				JdbcUtils.executeUpdate(JdbcUtils.getConnection(), delSubSql.toString(), params); // ɾ���ӱ�
				/** 5. ɾ������ */
				JdbcUtils.executeUpdate(JdbcUtils.getConnection(), delMainSql.toString(), params); // ɾ������
				/** 6. �ύ���� */
				JdbcUtils.commit();

			} catch (SQLException e) {
				/** �ع����� */
				JdbcUtils.rollback();
			} finally {
				/** �ͷ���Դ */
				JdbcUtils.release();
			}
		}
	}

	/**
	 * ����ɾ���ظ�����SQL���
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
	 * ����ɾ���ظ��ӱ�SQL���
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
	 * ����ɾ��������������(��ȡ����, �Ժ��޸�ֻ��Ҫ�޸���һ������.)
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
	 * ��Ӵ�����Ϣ�����
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
	 * �ж��ļ��Ƿ���֧�ֵ��ļ�����
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
	 * ��ʼ�ȶ�����
	 * 
	 * @param startDate
	 *            ��ʼ�ȶ�����
	 * @param endDate
	 *            �����ȶ�����
	 * @param paramDto
	 *            ��������
	 * @return
	 * @throws ITFEBizException
	 */
	public MulitTableDto voucherCompare(String startDate, String endDate, IDto paramDto) throws ITFEBizException {
		// 5201 merge���
		StringBuilder directMergeSql = generateMergeSqlByTable(TfDirectpaymsgsubDto.tableName());
		// 8202 merge���
		StringBuilder grantMergeSql = generateMergeSqlByTable(TfGrantpaymsgsubDto.tableName());

		// 5201 update���
		StringBuilder directUpdateSql = genenrateUpdateSqlByTable(TfDirectpaymsgsubDto.tableName());
		// 8202 update���
		StringBuilder grantUpdateSql = genenrateUpdateSqlByTable(TfGrantpaymsgsubDto.tableName());

		// execCompareByJdbc(startDate, endDate, directMergeSql, grantMergeSql, directUpdateSql, grantUpdateSql);

		execCompareByJaf(startDate, endDate, directMergeSql, grantMergeSql, directUpdateSql, grantUpdateSql);

		return null;
	}

	/**
	 * ʹ��JAF��������
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
			/** ִ��merge��� */
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

			/** ִ��update��� */
			paramList = Arrays.asList(getLoginInfo().getSorgcode(),//
					formatDateString(startDate),//
					formatDateString(endDate));//
//					DealCodeConstants.DEALCODE_ITFE_SUCCESS)

			clearAndaddParam2SQLExector(sqlExec, paramList);
			sqlExec.runQuery(directUpdateSql.toString()); // 5201����

			clearAndaddParam2SQLExector(sqlExec, paramList);
			sqlExec.runQueryCloseCon(grantUpdateSql.toString()); // 8202����
		} catch (Exception e) {
			log.error("�ȶ����ݳ����쳣", e);
			throw new RuntimeException("�ȶ������쳣!", e);
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
	 * ʹ��JDBC��������
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
			/** 1. �������� */
			JdbcUtils.startTransaction();

			/** 2. ִ��merge */
			Object[] params = new Object[] { getLoginInfo().getSorgcode(),//
					formatDateString(startDate),//
					formatDateString(endDate),//
					DealCodeConstants.DEALCODE_ITFE_SUCCESS,//
					StateConstant.MERGE_VALIDATE_SUCCESS,//
					StateConstant.MERGE_VALIDATE_FAILURE };
			JdbcUtils.executeUpdate(JdbcUtils.getConnection(), directMergeSql.toString(), params); // 5201Merge
			JdbcUtils.executeUpdate(JdbcUtils.getConnection(), grantMergeSql.toString(), params); // 8202Merge

			/** 3. ִ��update */
			params = new Object[] { getLoginInfo().getSorgcode(),//
					formatDateString(startDate),//
					formatDateString(endDate),//
					DealCodeConstants.DEALCODE_ITFE_SUCCESS };
			JdbcUtils.executeUpdate(JdbcUtils.getConnection(), directUpdateSql.toString(), params); // 5201����
			JdbcUtils.executeUpdate(JdbcUtils.getConnection(), grantUpdateSql.toString(), params); // 8202����

			/** 4. �ύ���� */
			JdbcUtils.commit();
		} catch (Exception e) {
			log.error("���ݱȶԳ����쳣", e);
			try {
				/** �����쳣,�ع����� */
				JdbcUtils.rollback();
			} catch (SQLException e1) {
				log.error(e);
				throw new RuntimeException("���ݱȶԳ����쳣(�ع�)");
			}
			throw new RuntimeException("���ݱȶԳ����쳣!", e);
		} finally {
			try {
				/** 5. �ͷ���Դ */
				JdbcUtils.release();
			} catch (SQLException e) {
				log.error(e);
				throw new RuntimeException("���ݱȶԳ����쳣(�ύ)");
			}
		}
	}

	/**
	 * ���ݱ�����������merge���
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
	 * ���ݱ�����������update���
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
	 * ��ѯ�ȶԽ������(ʵ�ַ�ҳ)
	 * 
	 * @param startDate
	 *            ��ʼ�ȶ�����
	 * @param endDate
	 *            �����ȶ�����
	 * @param paramDto
	 *            ��������
	 * @param request
	 *            ��ҳ����
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public PageResponse findValidateResult(String startDate, String endDate, IDto paramDto, PageRequest request) throws ITFEBizException {
		SQLExecutor sqlExec = null;
		SQLResults rs = null;

		PageResponse response = new PageResponse(request);

		/** ��ѯ��� */
		StringBuilder querySql = new StringBuilder();
		querySql.append(" SELECT a.S_AGENTBNKCODE sagentbnkcode,replace(a.D_VOUDATE,'-','') dacctdate,a.S_VOUNO voucherNo,b.S_VOUCHERN0 detailVoucherNo,b.S_BDGORGCODE supdepcode,b.S_FUNCBDGSBTCODE expfunccode,b.F_AMT payamt,COALESCE(b.S_HOLD4,'" + StateConstant.MERGE_VALIDATE_NOTCOMPARE + "') result ");
		querySql.append(generateQueryFromSqlCondition(paramDto));

		/** ���ڷ�ҳ��ѯ�ܼ�¼�� */
		StringBuilder totalSql = new StringBuilder();
		totalSql.append("select count(*) ").append(generateQueryFromSqlCondition(paramDto));
		try {
			SQLExecutor sqlExecc = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExecc.runQuery("update TV_PAYRECK_BANK_LIST set S_HOLD4='2' where S_HOLD4='' or S_HOLD4 is null");
			/** ���ò�ѯ�������� */
			List paramList = new ArrayList();
			paramList.add(getLoginInfo().getSorgcode());
			paramList.add(formatDateString(startDate));
			paramList.add(formatDateString(endDate));
//			paramList.add(DealCodeConstants.DEALCODE_ITFE_SUCCESS);

			/** ����ܼ�¼�� */
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

			/** ���һҳ���� */
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			clearAndaddParam2SQLExector(sqlExec, paramList);
			rs = sqlExec.runQueryCloseCon(querySql.toString(), BankValidateDto.class, request.getStartPosition(), request.getPageSize(), false);

			List list = new ArrayList();
			list.addAll(rs.getDtoCollection());
			response.getData().clear();
			response.setData(list);
			return response;
		} catch (Exception e) {
			log.error("��ѯ���ݳ����쳣", e);
			throw new RuntimeException("���ݻ�ȡ�쳣!", e);
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
	 * ������ֵ��䵽��ѯ������
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
	 * ������ѯ����from����
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
	 * ��������
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
			// Ϊ�������ͳһ, �ϲ�����һ�𱣴�
//			IDto[] dtos = (IDto[]) ArrayUtils.addAll(CommonUtil.listTArray(mainList), CommonUtil.listTArray(subList));
//			DatabaseFacade.getODB().create(dtos);
		}
	}

	/**
	 * ������Ӧ������
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
	 * �������ڸ�ʽ
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
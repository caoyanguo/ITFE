package com.cfcc.itfe.service.subsysmanage.databackup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DBOperFacade;
import com.cfcc.itfe.facade.DBOperUtil;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.ZipCompressorBack;

/**
 * @author zouyutang
 * @time 11-12-30 09:33:48 codecomment:
 */

public class DataBackupService extends AbstractDataBackupService {
	private static Log log = LogFactory.getLog(DataBackupService.class);

	/**
	 * 按照核算主体代码进行数据备份
	 * 
	 * @generated
	 * @param sorgcode
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List databackup(String sorgcode) throws ITFEBizException {
		try {

			// 得到需要备份的表名称集合
			List<String> tableList = DBOperFacade.lookTable();

			if (tableList == null || tableList.size() == 0) {
				log.error("没有需要备份的数据表");
				throw new ITFEBizException("没有需要备份的数据表");
			} else {
				// 生成导出备份数据所在的目录名： 机构代码+yyMMddHHmmss
				String genBackupDirectoryName = sorgcode
						+ TimeFacade.getCurrentStringTime("yyyyMMddHHmmss");
				// 该机构完整的备份路径
				String genBackupDirectoryFullPath = ITFECommonConstant.SYS_BACKUP_PATH
						+ genBackupDirectoryName + File.separator;

				File genBackupDirectoryFile = new File(
						genBackupDirectoryFullPath);
				if (!genBackupDirectoryFile.exists()) {
					genBackupDirectoryFile.mkdirs();
				}
				// 生成导出表的sql语句
				String backupsql = makeBackupSql(tableList,
						genBackupDirectoryFullPath, sorgcode);
				// 把导出表数据的sql文件写到备份目录下,sysconfig.properties中有配置
				String backupsqlfilepath = genBackupDirectoryFullPath
						+ genBackupDirectoryName
						+ ITFECommonConstant.SYS_BACKUP_SQL_FILE_NAME;
				FileUtil.getInstance().writeFile(backupsqlfilepath, backupsql);
				// 设置调用shell之后生成的日志文件的完整路径
				String genBackupLogFilePath = genBackupDirectoryFullPath
						+ genBackupDirectoryName + "backup.log";
				// 执行生成的sql文件
				DBOperUtil.callShellWithLogFileName(backupsqlfilepath,
						genBackupLogFilePath);
				// 分析生成的日志文件
				String analseLogResult = DBOperUtil
						.analyseDb2ExportShellLog(genBackupLogFilePath);
				if (StringUtils.isNotBlank(analseLogResult)) {
					log.error(analseLogResult);
					throw new ITFEBizException(analseLogResult);
				}

				// 采用java方式压缩zip文件
				compress(sorgcode, genBackupDirectoryFullPath,
						genBackupDirectoryName);
				// 删除服务器其他文件
				List<String> filist = FileUtil.getInstance().listFileAbspath(
						genBackupDirectoryFullPath);
				List<String> resultList = new ArrayList<String>();
				for (String str : filist) {
					if (!str.endsWith(".zip")) {
						FileUtil.getInstance().deleteFile(str);
					}else{
						resultList.add(str.replace(ITFECommonConstant.FILE_TRANSFER_CONFIG_ROOT,""));
					}
				}
				// 使用Gzip方式压缩生成的文件(linux采用这种GZIP的方式)
				// GZipUtilBean.getInstance().compress(genBackupDirectoryFullPath,genBackupDirectoryFullPath,true,FileFilterUtil.getBackupCompressFileFilter());

				// 更新机构代码的备份状态为已备份

				return resultList;

			}
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(e);
		}
	}

	private String makeBackupSql(List<String> tableList,
			String exportSqlFileDirectory, String sbookorgcode) {
		// 生成导出表的sql语句
		StringBuffer backupsql = new StringBuffer();

		backupsql.append(DBOperUtil.makeDbConnectionSql());
		backupsql.append(DBOperUtil.sqlLineEndString);

		// 大对象数据文件目录及文件名
		List<String> lobTables = DBOperFacade.lookLobTable();

		for (String table : tableList) {
			//报表数据不备份
			if(table.startsWith("TR_") || table.startsWith("HTR_") || table.startsWith("TN_") || table.startsWith("HTR_")){
				continue;
			}
			String exportFileName = exportSqlFileDirectory + table + "."
					+ ITFECommonConstant.SYS_BACKUP_FILE_TYPE;
			String expsql = "";
			if (lobTables.contains(table)) {
				expsql = DBOperUtil.makeLobDataExportSql(table, exportFileName,
						ITFECommonConstant.SYS_BACKUP_FILE_TYPE,
						exportSqlFileDirectory,
						ITFECommonConstant.LOB_FILE_NAME, "");
			} else {
				expsql = DBOperUtil.makeDataExportSql(table, exportFileName,
						ITFECommonConstant.SYS_BACKUP_FILE_TYPE, "");
			}
			backupsql.append(expsql);
			backupsql.append(DBOperUtil.sqlLineEndString);

		}
		backupsql.append("connect reset;");

		return backupsql.toString();
	}

	private void compress(String sbookorgcode, String dirpath, String fname)
			throws IOException {
		String filename = dirpath + fname + ".zip ";
		ZipCompressorBack zc = new ZipCompressorBack(filename);
		File exportPath = new File(dirpath);
		if (!exportPath.exists()) {
			exportPath.mkdir();
		}
		zc.compress(dirpath);
	}
}
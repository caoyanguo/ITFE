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
	 * ���պ����������������ݱ���
	 * 
	 * @generated
	 * @param sorgcode
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List databackup(String sorgcode) throws ITFEBizException {
		try {

			// �õ���Ҫ���ݵı����Ƽ���
			List<String> tableList = DBOperFacade.lookTable();

			if (tableList == null || tableList.size() == 0) {
				log.error("û����Ҫ���ݵ����ݱ�");
				throw new ITFEBizException("û����Ҫ���ݵ����ݱ�");
			} else {
				// ���ɵ��������������ڵ�Ŀ¼���� ��������+yyMMddHHmmss
				String genBackupDirectoryName = sorgcode
						+ TimeFacade.getCurrentStringTime("yyyyMMddHHmmss");
				// �û��������ı���·��
				String genBackupDirectoryFullPath = ITFECommonConstant.SYS_BACKUP_PATH
						+ genBackupDirectoryName + File.separator;

				File genBackupDirectoryFile = new File(
						genBackupDirectoryFullPath);
				if (!genBackupDirectoryFile.exists()) {
					genBackupDirectoryFile.mkdirs();
				}
				// ���ɵ������sql���
				String backupsql = makeBackupSql(tableList,
						genBackupDirectoryFullPath, sorgcode);
				// �ѵ��������ݵ�sql�ļ�д������Ŀ¼��,sysconfig.properties��������
				String backupsqlfilepath = genBackupDirectoryFullPath
						+ genBackupDirectoryName
						+ ITFECommonConstant.SYS_BACKUP_SQL_FILE_NAME;
				FileUtil.getInstance().writeFile(backupsqlfilepath, backupsql);
				// ���õ���shell֮�����ɵ���־�ļ�������·��
				String genBackupLogFilePath = genBackupDirectoryFullPath
						+ genBackupDirectoryName + "backup.log";
				// ִ�����ɵ�sql�ļ�
				DBOperUtil.callShellWithLogFileName(backupsqlfilepath,
						genBackupLogFilePath);
				// �������ɵ���־�ļ�
				String analseLogResult = DBOperUtil
						.analyseDb2ExportShellLog(genBackupLogFilePath);
				if (StringUtils.isNotBlank(analseLogResult)) {
					log.error(analseLogResult);
					throw new ITFEBizException(analseLogResult);
				}

				// ����java��ʽѹ��zip�ļ�
				compress(sorgcode, genBackupDirectoryFullPath,
						genBackupDirectoryName);
				// ɾ�������������ļ�
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
				// ʹ��Gzip��ʽѹ�����ɵ��ļ�(linux��������GZIP�ķ�ʽ)
				// GZipUtilBean.getInstance().compress(genBackupDirectoryFullPath,genBackupDirectoryFullPath,true,FileFilterUtil.getBackupCompressFileFilter());

				// ���»�������ı���״̬Ϊ�ѱ���

				return resultList;

			}
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(e);
		}
	}

	private String makeBackupSql(List<String> tableList,
			String exportSqlFileDirectory, String sbookorgcode) {
		// ���ɵ������sql���
		StringBuffer backupsql = new StringBuffer();

		backupsql.append(DBOperUtil.makeDbConnectionSql());
		backupsql.append(DBOperUtil.sqlLineEndString);

		// ����������ļ�Ŀ¼���ļ���
		List<String> lobTables = DBOperFacade.lookLobTable();

		for (String table : tableList) {
			//�������ݲ�����
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
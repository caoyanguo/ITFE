package com.cfcc.itfe.service.subsysmanage.datarestore;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DBOperFacade;
import com.cfcc.itfe.facade.DBOperUtil;
import com.cfcc.itfe.facade.SysBackupRestoreFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.util.FileUtil;
/**
 * @author zouyutang
 * @time   11-12-30 09:33:48
 * codecomment: 
 */

public class DataRestoreService extends AbstractDataRestoreService {
	private static Log log = LogFactory.getLog(DataRestoreService.class);	


	/**
	 * ���ջ�������������ݻָ�	 
	 * @generated
	 * @param sorgcode
	 * @param relPathList
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    public String datarestore(String sorgcode, List relPathList) throws ITFEBizException {
		try {
			
			
			//���ɷ���˻ָ���ѹ��Ŀ¼
			String relBackupFilePath = (String)relPathList.get(0);
			String relBackupFileName = FileUtil.getStringBehindLastFileSeparator(relBackupFilePath);
			//��ȡ�����ļ��ı���ʱ�� 12-Ϊ�����������ĳ���
			String backupTimeString = relBackupFileName.substring(12, StateConstant.DATA_BACKUP_FILEDIRECTORYNAME_SIZE);
			//���ɻָ��ļ���Ŀ¼��  �����������+yyyyMMddHHmmss+(backupTime)
			String restoreFileDirectoryName = sorgcode+TimeFacade.getCurrentStringTime("yyyyMMddHHmmss")+"["+backupTimeString+"]";
			String restoreFileDirectoryFullName = ITFECommonConstant.SYS_RESTORE_PATH+restoreFileDirectoryName+File.separator;
			File restoreFileDirectory = new File(restoreFileDirectoryFullName);	
			if(!restoreFileDirectory.exists()){
				restoreFileDirectory.mkdirs();
			}
			
			//��ѹ���ϴ��������ļ���������sql�ļ�
			StringBuffer restoresql = new StringBuffer();
			restoresql.append(DBOperUtil.makeDbConnectionSql());
			restoresql.append(DBOperUtil.sqlLineEndString);
			//ɾ�����sql �и��ӱ������� ��ɾ���ӱ� ��ɾ������
			StringBuffer deleteTableSql = new StringBuffer();
			//������sql �и��ӱ������� �ȵ��븸�� �ٵ����ӱ�
			StringBuffer importTableSql = new StringBuffer();
			//��ȡϵͳ�����еĸ������ӱ���
			List<String> fatherTableList = DBOperFacade.lookFatherTable();
			List<String> sonTableList = DBOperFacade.lookSonTable();
			for(Object obj :relPathList){
				//��ѹ���ϴ��������ļ�
				String relPath = (String)obj;
				String srcPath = ITFECommonConstant.FILE_TRANSFER_CONFIG_ROOT+FileUtil.transFileSeparatorToSysSeparator(relPath);
				//Gzip��ʽ��ѹ���ļ�
//				GZipUtilBean.getInstance().decompress(srcPath, restoreFileDirectoryFullName);
				//����WINRAR��ʽ��ѹ���ļ�(��ʱʹ��RAR�ķ�ʽ)
//				DBOperUtil.deCompressWithRar(srcPath, restoreFileDirectoryFullName);
				//����java�ķ�ʽ��ѹ��zip�ļ�
				FileUtil.getInstance().unzip(srcPath, restoreFileDirectoryFullName);
				//���ɸñ��sql
				List<String> delFileList = FileUtil.getInstance().listFileAbspath(restoreFileDirectoryFullName);
				
				// ��ð���������ֶεı�
				List<String> lobTables = DBOperFacade.lookLobTable();
				
				for(int i = 0 ; i< delFileList.size();i++){
					String delFile = delFileList.get(i);
					String fname = new File(delFile).getName();
					if(fname.endsWith("."+ITFECommonConstant.SYS_BACKUP_FILE_TYPE)) {
						String tableCode = SysBackupRestoreFacade.getTableCodeByBackupFileName(fname);
												
						//��ʱ��ȥ�������delete���
						if(!fatherTableList.contains(tableCode)){
							String whereCondition = "";
							//���� ���պ�������������ɾ����sql
							deleteTableSql.append(DBOperUtil.makeDeleteTableSql(tableCode, whereCondition));
							deleteTableSql.append(DBOperUtil.sqlLineEndString);
						}
						//��ʱ��ȥ���ӱ��import���
						if(!sonTableList.contains(tableCode)){
							String importFileName = restoreFileDirectoryFullName+fname;
							if(lobTables.contains(tableCode)){
								String lobFilePath = restoreFileDirectoryFullName;
								importTableSql.append(DBOperUtil.makeLobImportTableSql(tableCode, importFileName,ITFECommonConstant.SYS_RESTORE_FILE_TYPE,lobFilePath,StateConstant.DATA_IMPORT_MODIFIEDBY_DELPRIORITYCHARANDLOB,StateConstant.DATA_IMPORT_MODE_INSERT));
							}else{
								importTableSql.append(DBOperUtil.makeImportTableSql(tableCode, importFileName,ITFECommonConstant.SYS_RESTORE_FILE_TYPE,StateConstant.DATA_IMPORT_MODIFIEDBY_DELPRIORITYCHAR,StateConstant.DATA_IMPORT_MODE_INSERT));
							}
							importTableSql.append(DBOperUtil.sqlLineEndString);
						}
					}
					
					if(i==delFileList.size()-1){
						//���������delete���
						for(String fatherTableCode:fatherTableList){
							String whereCondition = "";
							deleteTableSql.append(DBOperUtil.makeDeleteTableSql(fatherTableCode, whereCondition));
							deleteTableSql.append(DBOperUtil.sqlLineEndString);
						}
						//�����ӱ��import���
						for(String sonTableCode :sonTableList){
							String importFileName = restoreFileDirectoryFullName+SysBackupRestoreFacade.getBackupFileNameByTableCode(sonTableCode);
							importTableSql.append(DBOperUtil.makeImportTableSql(sonTableCode, importFileName,ITFECommonConstant.SYS_RESTORE_FILE_TYPE,StateConstant.DATA_IMPORT_MODIFIEDBY_DELPRIORITYCHAR,StateConstant.DATA_IMPORT_MODE_INSERT));
							importTableSql.append(DBOperUtil.sqlLineEndString);
						}
					}
				}
			}
			//����ɾ�����뵼����sql
			restoresql.append(deleteTableSql);
			restoresql.append(importTableSql);
			restoresql.append("connect reset;");
			//�����ɵ�restoresqlд���ļ���
			String restoreSqlFilePath = restoreFileDirectoryFullName+restoreFileDirectoryName+ITFECommonConstant.SYS_RESTORE_SQL_FILE_NAME;
			FileUtil.getInstance().writeFile(restoreSqlFilePath, restoresql.toString());
			
			//���õ���shell֮�����ɵ���־�ļ�������·��
			String restoreLogFilePath = restoreFileDirectoryFullName+restoreFileDirectoryName+"restore.log";
			//����shell��ִ�� restoresql�ļ�
			DBOperUtil.callShellWithLogFileName(restoreSqlFilePath, restoreLogFilePath);
			//�������ɵ���־�ļ�
			String errinfo = DBOperUtil.callShellResultProcess(restoreLogFilePath);
			//ɾ���ϴ��������������ݣ���־��ʱ��ɾ��
			//1.ɾ��del�ļ�
			List<String> delFileL = FileUtil.getInstance().listFileAbspath(restoreFileDirectoryFullName);
			for(String del : delFileL) {
				String delfn = new File(del).getName();
				//����־�ļ���sql�ļ�����
				if(delfn.contains("restore.log")||delfn.contains(ITFECommonConstant.SYS_RESTORE_SQL_FILE_NAME)){
					continue;
				}
					FileUtil.getInstance().deleteFile(del);
			}
			//2.ɾ��zip�ļ�
			String zipfilestrname = ITFECommonConstant.FILE_TRANSFER_CONFIG_ROOT+FileUtil.transFileSeparatorToSysSeparator((String)relPathList.get(0));
			FileUtil.getInstance().deleteFile(zipfilestrname);
			return errinfo;
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("���ݻָ�����", e);
		}
	}

}
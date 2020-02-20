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
	 * 按照机构代码进行数据恢复	 
	 * @generated
	 * @param sorgcode
	 * @param relPathList
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    public String datarestore(String sorgcode, List relPathList) throws ITFEBizException {
		try {
			
			
			//生成服务端恢复解压的目录
			String relBackupFilePath = (String)relPathList.get(0);
			String relBackupFileName = FileUtil.getStringBehindLastFileSeparator(relBackupFilePath);
			//获取备份文件的备份时间 12-为核算主体代码的长度
			String backupTimeString = relBackupFileName.substring(12, StateConstant.DATA_BACKUP_FILEDIRECTORYNAME_SIZE);
			//生成恢复文件的目录名  核算主体代码+yyyyMMddHHmmss+(backupTime)
			String restoreFileDirectoryName = sorgcode+TimeFacade.getCurrentStringTime("yyyyMMddHHmmss")+"["+backupTimeString+"]";
			String restoreFileDirectoryFullName = ITFECommonConstant.SYS_RESTORE_PATH+restoreFileDirectoryName+File.separator;
			File restoreFileDirectory = new File(restoreFileDirectoryFullName);	
			if(!restoreFileDirectory.exists()){
				restoreFileDirectory.mkdirs();
			}
			
			//解压缩上传过来的文件，并生成sql文件
			StringBuffer restoresql = new StringBuffer();
			restoresql.append(DBOperUtil.makeDbConnectionSql());
			restoresql.append(DBOperUtil.sqlLineEndString);
			//删除表的sql 有父子表的情况下 先删除子表 再删除父表
			StringBuffer deleteTableSql = new StringBuffer();
			//导入表的sql 有父子表的情况下 先导入父表 再导入子表
			StringBuffer importTableSql = new StringBuffer();
			//获取系统中所有的父表与子表集合
			List<String> fatherTableList = DBOperFacade.lookFatherTable();
			List<String> sonTableList = DBOperFacade.lookSonTable();
			for(Object obj :relPathList){
				//解压缩上传过来的文件
				String relPath = (String)obj;
				String srcPath = ITFECommonConstant.FILE_TRANSFER_CONFIG_ROOT+FileUtil.transFileSeparatorToSysSeparator(relPath);
				//Gzip方式解压缩文件
//				GZipUtilBean.getInstance().decompress(srcPath, restoreFileDirectoryFullName);
				//采用WINRAR方式解压缩文件(暂时使用RAR的方式)
//				DBOperUtil.deCompressWithRar(srcPath, restoreFileDirectoryFullName);
				//采用java的方式解压缩zip文件
				FileUtil.getInstance().unzip(srcPath, restoreFileDirectoryFullName);
				//生成该表的sql
				List<String> delFileList = FileUtil.getInstance().listFileAbspath(restoreFileDirectoryFullName);
				
				// 获得包含大对象字段的表
				List<String> lobTables = DBOperFacade.lookLobTable();
				
				for(int i = 0 ; i< delFileList.size();i++){
					String delFile = delFileList.get(i);
					String fname = new File(delFile).getName();
					if(fname.endsWith("."+ITFECommonConstant.SYS_BACKUP_FILE_TYPE)) {
						String tableCode = SysBackupRestoreFacade.getTableCodeByBackupFileName(fname);
												
						//暂时先去掉主表的delete语句
						if(!fatherTableList.contains(tableCode)){
							String whereCondition = "";
							//生成 按照核算主体代码进行删除的sql
							deleteTableSql.append(DBOperUtil.makeDeleteTableSql(tableCode, whereCondition));
							deleteTableSql.append(DBOperUtil.sqlLineEndString);
						}
						//暂时先去掉子表的import语句
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
						//加上主表的delete语句
						for(String fatherTableCode:fatherTableList){
							String whereCondition = "";
							deleteTableSql.append(DBOperUtil.makeDeleteTableSql(fatherTableCode, whereCondition));
							deleteTableSql.append(DBOperUtil.sqlLineEndString);
						}
						//加上子表的import语句
						for(String sonTableCode :sonTableList){
							String importFileName = restoreFileDirectoryFullName+SysBackupRestoreFacade.getBackupFileNameByTableCode(sonTableCode);
							importTableSql.append(DBOperUtil.makeImportTableSql(sonTableCode, importFileName,ITFECommonConstant.SYS_RESTORE_FILE_TYPE,StateConstant.DATA_IMPORT_MODIFIEDBY_DELPRIORITYCHAR,StateConstant.DATA_IMPORT_MODE_INSERT));
							importTableSql.append(DBOperUtil.sqlLineEndString);
						}
					}
				}
			}
			//加上删除表与导入表的sql
			restoresql.append(deleteTableSql);
			restoresql.append(importTableSql);
			restoresql.append("connect reset;");
			//将生成的restoresql写到文件中
			String restoreSqlFilePath = restoreFileDirectoryFullName+restoreFileDirectoryName+ITFECommonConstant.SYS_RESTORE_SQL_FILE_NAME;
			FileUtil.getInstance().writeFile(restoreSqlFilePath, restoresql.toString());
			
			//设置调用shell之后生成的日志文件的完整路径
			String restoreLogFilePath = restoreFileDirectoryFullName+restoreFileDirectoryName+"restore.log";
			//调用shell，执行 restoresql文件
			DBOperUtil.callShellWithLogFileName(restoreSqlFilePath, restoreLogFilePath);
			//分析生成的日志文件
			String errinfo = DBOperUtil.callShellResultProcess(restoreLogFilePath);
			//删除上传到服务器的内容，日志暂时不删除
			//1.删除del文件
			List<String> delFileL = FileUtil.getInstance().listFileAbspath(restoreFileDirectoryFullName);
			for(String del : delFileL) {
				String delfn = new File(del).getName();
				//将日志文件与sql文件保留
				if(delfn.contains("restore.log")||delfn.contains(ITFECommonConstant.SYS_RESTORE_SQL_FILE_NAME)){
					continue;
				}
					FileUtil.getInstance().deleteFile(del);
			}
			//2.删除zip文件
			String zipfilestrname = ITFECommonConstant.FILE_TRANSFER_CONFIG_ROOT+FileUtil.transFileSeparatorToSysSeparator((String)relPathList.get(0));
			FileUtil.getInstance().deleteFile(zipfilestrname);
			return errinfo;
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("数据恢复出错", e);
		}
	}

}
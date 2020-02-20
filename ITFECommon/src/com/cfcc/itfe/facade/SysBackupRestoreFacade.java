package com.cfcc.itfe.facade;

import com.cfcc.itfe.config.ITFECommonConstant;

public class SysBackupRestoreFacade {
	
	/**
	 * 解析备份文件名称，获得表代码
	 * @param backupFileName
	 * @return
	 */
	public static String getTableCodeByBackupFileName(String backupFileName) {
		int endIndex = backupFileName.indexOf("."+ITFECommonConstant.SYS_BACKUP_FILE_TYPE);
		return backupFileName.substring(0,endIndex);
	}

	/**
	 * 通过表代码，获取备份文件名
	 * @param backupFileName
	 * @return
	 */
	public static String getBackupFileNameByTableCode(String tableCode) {
		return tableCode+"."+ITFECommonConstant.SYS_BACKUP_FILE_TYPE;
	}
	
	

	/**
	 * 通过表代码，获得核算主体的where condition
	 * 
	 * @param tableCode
	 * @param sbookorgcode
	 * @return
	 */
	public static String getBookOrgcodeWhereConditionByTableCode(
			String tableCode, String sbookorgcode) {
		
		String bookorgcodeColumn = "s_bookorgcode";
		
//		if (OnlyServiceUseConstant.dbctMap.containsKey(tableCode)) {
//			bookorgcodeColumn = OnlyServiceUseConstant.dbctMap.get(tableCode);
//		}
		String whereCondition = " where " + bookorgcodeColumn + " =  '"
				+ sbookorgcode + "'";
		
		//由于兰州只有一个核算主体代码，则不需要加此条件，直接返回""
		return "";
	}

}

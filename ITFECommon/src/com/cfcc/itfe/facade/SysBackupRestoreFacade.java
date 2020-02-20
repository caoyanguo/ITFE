package com.cfcc.itfe.facade;

import com.cfcc.itfe.config.ITFECommonConstant;

public class SysBackupRestoreFacade {
	
	/**
	 * ���������ļ����ƣ���ñ����
	 * @param backupFileName
	 * @return
	 */
	public static String getTableCodeByBackupFileName(String backupFileName) {
		int endIndex = backupFileName.indexOf("."+ITFECommonConstant.SYS_BACKUP_FILE_TYPE);
		return backupFileName.substring(0,endIndex);
	}

	/**
	 * ͨ������룬��ȡ�����ļ���
	 * @param backupFileName
	 * @return
	 */
	public static String getBackupFileNameByTableCode(String tableCode) {
		return tableCode+"."+ITFECommonConstant.SYS_BACKUP_FILE_TYPE;
	}
	
	

	/**
	 * ͨ������룬��ú��������where condition
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
		
		//��������ֻ��һ������������룬����Ҫ�Ӵ�������ֱ�ӷ���""
		return "";
	}

}

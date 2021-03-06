      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TV_USERSCONDITION</p>
 * <p>Description:  Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:04 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class TvUsersconditionInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TV_USERSCONDITION";
	
	/**
	 * column S_ORGCODE
	 */
	public static String S_ORGCODE ="S_ORGCODE";
	/**
	 * column S_USERCODE
	 */
	public static String S_USERCODE ="S_USERCODE";
	/**
	 * column S_USERNAME
	 */
	public static String S_USERNAME ="S_USERNAME";
	/**
	 * column S_CONDITIONS
	 */
	public static String S_CONDITIONS ="S_CONDITIONS";
	/**
	 * @return String table name of dto
	 */
	public static String getTableName() {
		return TABLENAME;
	}
	
	/**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[4];        
        columnNames[0]="S_ORGCODE";
        columnNames[1]="S_USERCODE";
        columnNames[2]="S_USERNAME";
        columnNames[3]="S_CONDITIONS";
        return columnNames;     
    }
	/******************************************************
	*
	*  Get Column Name
	*
	*******************************************************/
	/**
	 * Getter S_ORGCODE, PK , NOT NULL   	 
	 */
	public static String columnSorgcode(){
		 return S_ORGCODE;
	}
	/**
	 * Getter S_USERCODE, PK , NOT NULL   	 
	 */
	public static String columnSusercode(){
		 return S_USERCODE;
	}
	/**
	 * Getter S_USERNAME , NOT NULL   	 
	 */
	public static String columnSusername(){
		 return S_USERNAME;
	}
	/**
	 * Getter S_CONDITIONS   	 
	 */
	public static String columnSconditions(){
		 return S_CONDITIONS;
	}
	/******************************************************
	*
	*  Get Column Java Type
	*
	*******************************************************/
	/**
	 * Getter S_ORGCODE, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeSorgcode(){
		 return "String";
	}
	/**
	 * Getter S_USERCODE, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeSusercode(){
		 return "String";
	}
	/**
	 * Getter S_USERNAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSusername(){
		 return "String";
	}
	/**
	 * Getter S_CONDITIONS   	 
	 */
	public static String columnJavaTypeSconditions(){
		 return "String";
	}
	/******************************************************
	*
	*  Get Column Database Type
	*
	*******************************************************/
	/**
	 * Getter S_ORGCODE, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSorgcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_USERCODE, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSusercode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_USERNAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSusername(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CONDITIONS   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSconditions(){
		 return "VARCHAR";
	}
	/******************************************************
	*
	*  Get Column With
	*
	*******************************************************/
	/**
	 * Getter S_ORGCODE, PK , NOT NULL   	 
	 */
	public static int columnWidthSorgcode(){
		 return 12;
	}
	/**
	 * Getter S_USERCODE, PK , NOT NULL   	 
	 */
	public static int columnWidthSusercode(){
		 return 30;
	}
	/**
	 * Getter S_USERNAME , NOT NULL   	 
	 */
	public static int columnWidthSusername(){
		 return 100;
	}
	/**
	 * Getter S_CONDITIONS   	 
	 */
	public static int columnWidthSconditions(){
		 return 600;
	}
}
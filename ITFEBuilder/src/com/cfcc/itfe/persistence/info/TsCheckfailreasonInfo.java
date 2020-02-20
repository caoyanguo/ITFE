      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TS_CHECKFAILREASON</p>
 * <p>Description:  Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:59 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class TsCheckfailreasonInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TS_CHECKFAILREASON";
	
	/**
	 * column S_ORGCODE
	 */
	public static String S_ORGCODE ="S_ORGCODE";
	/**
	 * column S_CHECKFAILCODE
	 */
	public static String S_CHECKFAILCODE ="S_CHECKFAILCODE";
	/**
	 * column S_CHECKFAILDSP
	 */
	public static String S_CHECKFAILDSP ="S_CHECKFAILDSP";
	/**
	 * column TS_SYSUPDATE
	 */
	public static String TS_SYSUPDATE ="TS_SYSUPDATE";
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
        columnNames[1]="S_CHECKFAILCODE";
        columnNames[2]="S_CHECKFAILDSP";
        columnNames[3]="TS_SYSUPDATE";
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
	 * Getter S_CHECKFAILCODE, PK , NOT NULL   	 
	 */
	public static String columnScheckfailcode(){
		 return S_CHECKFAILCODE;
	}
	/**
	 * Getter S_CHECKFAILDSP , NOT NULL   	 
	 */
	public static String columnScheckfaildsp(){
		 return S_CHECKFAILDSP;
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 */
	public static String columnTssysupdate(){
		 return TS_SYSUPDATE;
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
	 * Getter S_CHECKFAILCODE, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeScheckfailcode(){
		 return "String";
	}
	/**
	 * Getter S_CHECKFAILDSP , NOT NULL   	 
	 */
	public static String columnJavaTypeScheckfaildsp(){
		 return "String";
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 */
	public static String columnJavaTypeTssysupdate(){
		 return "Timestamp";
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
	 * Getter S_CHECKFAILCODE, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeScheckfailcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CHECKFAILDSP , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeScheckfaildsp(){
		 return "VARCHAR";
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 * columnType is TIMESTAMP
	 */
	public static String columnDatabaseTypeTssysupdate(){
		 return "TIMESTAMP";
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
	 * Getter S_CHECKFAILCODE, PK , NOT NULL   	 
	 */
	public static int columnWidthScheckfailcode(){
		 return 10;
	}
	/**
	 * Getter S_CHECKFAILDSP , NOT NULL   	 
	 */
	public static int columnWidthScheckfaildsp(){
		 return 100;
	}
}
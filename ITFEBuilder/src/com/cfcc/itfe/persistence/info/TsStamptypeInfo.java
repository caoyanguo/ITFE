      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TS_STAMPTYPE</p>
 * <p>Description:  Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:00 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class TsStamptypeInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TS_STAMPTYPE";
	
	/**
	 * column S_STAMPTYPECODE
	 */
	public static String S_STAMPTYPECODE ="S_STAMPTYPECODE";
	/**
	 * column S_STAMPTYPENAME
	 */
	public static String S_STAMPTYPENAME ="S_STAMPTYPENAME";
	/**
	 * column S_STAMPTYPEID
	 */
	public static String S_STAMPTYPEID ="S_STAMPTYPEID";
	/**
	 * column I_MODICOUNT
	 */
	public static String I_MODICOUNT ="I_MODICOUNT";
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
        columnNames[0]="S_STAMPTYPECODE";
        columnNames[1]="S_STAMPTYPENAME";
        columnNames[2]="S_STAMPTYPEID";
        columnNames[3]="I_MODICOUNT";
        return columnNames;     
    }
	/******************************************************
	*
	*  Get Column Name
	*
	*******************************************************/
	/**
	 * Getter S_STAMPTYPECODE, PK , NOT NULL   	 
	 */
	public static String columnSstamptypecode(){
		 return S_STAMPTYPECODE;
	}
	/**
	 * Getter S_STAMPTYPENAME , NOT NULL   	 
	 */
	public static String columnSstamptypename(){
		 return S_STAMPTYPENAME;
	}
	/**
	 * Getter S_STAMPTYPEID , NOT NULL   	 
	 */
	public static String columnSstamptypeid(){
		 return S_STAMPTYPEID;
	}
	/**
	 * Getter I_MODICOUNT   	 
	 */
	public static String columnImodicount(){
		 return I_MODICOUNT;
	}
	/******************************************************
	*
	*  Get Column Java Type
	*
	*******************************************************/
	/**
	 * Getter S_STAMPTYPECODE, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeSstamptypecode(){
		 return "String";
	}
	/**
	 * Getter S_STAMPTYPENAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSstamptypename(){
		 return "String";
	}
	/**
	 * Getter S_STAMPTYPEID , NOT NULL   	 
	 */
	public static String columnJavaTypeSstamptypeid(){
		 return "String";
	}
	/**
	 * Getter I_MODICOUNT   	 
	 */
	public static String columnJavaTypeImodicount(){
		 return "Integer";
	}
	/******************************************************
	*
	*  Get Column Database Type
	*
	*******************************************************/
	/**
	 * Getter S_STAMPTYPECODE, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSstamptypecode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_STAMPTYPENAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSstamptypename(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_STAMPTYPEID , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSstamptypeid(){
		 return "VARCHAR";
	}
	/**
	 * Getter I_MODICOUNT   	 
	 * columnType is INTEGER
	 */
	public static String columnDatabaseTypeImodicount(){
		 return "INTEGER";
	}
	/******************************************************
	*
	*  Get Column With
	*
	*******************************************************/
	/**
	 * Getter S_STAMPTYPECODE, PK , NOT NULL   	 
	 */
	public static int columnWidthSstamptypecode(){
		 return 10;
	}
	/**
	 * Getter S_STAMPTYPENAME , NOT NULL   	 
	 */
	public static int columnWidthSstamptypename(){
		 return 50;
	}
	/**
	 * Getter S_STAMPTYPEID , NOT NULL   	 
	 */
	public static int columnWidthSstamptypeid(){
		 return 30;
	}
}
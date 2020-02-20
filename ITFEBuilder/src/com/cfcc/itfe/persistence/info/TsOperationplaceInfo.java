      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TS_OPERATIONPLACE</p>
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

public class TsOperationplaceInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TS_OPERATIONPLACE";
	
	/**
	 * column S_MODELID
	 */
	public static String S_MODELID ="S_MODELID";
	/**
	 * column S_FORMID
	 */
	public static String S_FORMID ="S_FORMID";
	/**
	 * column S_PLACEID
	 */
	public static String S_PLACEID ="S_PLACEID";
	/**
	 * column S_PLACETYPE
	 */
	public static String S_PLACETYPE ="S_PLACETYPE";
	/**
	 * column S_STAMPTYPECODE
	 */
	public static String S_STAMPTYPECODE ="S_STAMPTYPECODE";
	/**
	 * column S_PLACEDESC
	 */
	public static String S_PLACEDESC ="S_PLACEDESC";
	/**
	 * column S_ISUSE
	 */
	public static String S_ISUSE ="S_ISUSE";
	/**
	 * column S_BEFORESTATUS
	 */
	public static String S_BEFORESTATUS ="S_BEFORESTATUS";
	/**
	 * column S_AFTERSTATUS
	 */
	public static String S_AFTERSTATUS ="S_AFTERSTATUS";
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
        String[] columnNames = new String[10];        
        columnNames[0]="S_MODELID";
        columnNames[1]="S_FORMID";
        columnNames[2]="S_PLACEID";
        columnNames[3]="S_PLACETYPE";
        columnNames[4]="S_STAMPTYPECODE";
        columnNames[5]="S_PLACEDESC";
        columnNames[6]="S_ISUSE";
        columnNames[7]="S_BEFORESTATUS";
        columnNames[8]="S_AFTERSTATUS";
        columnNames[9]="I_MODICOUNT";
        return columnNames;     
    }
	/******************************************************
	*
	*  Get Column Name
	*
	*******************************************************/
	/**
	 * Getter S_MODELID, PK , NOT NULL   	 
	 */
	public static String columnSmodelid(){
		 return S_MODELID;
	}
	/**
	 * Getter S_FORMID , NOT NULL   	 
	 */
	public static String columnSformid(){
		 return S_FORMID;
	}
	/**
	 * Getter S_PLACEID, PK , NOT NULL   	 
	 */
	public static String columnSplaceid(){
		 return S_PLACEID;
	}
	/**
	 * Getter S_PLACETYPE , NOT NULL   	 
	 */
	public static String columnSplacetype(){
		 return S_PLACETYPE;
	}
	/**
	 * Getter S_STAMPTYPECODE   	 
	 */
	public static String columnSstamptypecode(){
		 return S_STAMPTYPECODE;
	}
	/**
	 * Getter S_PLACEDESC   	 
	 */
	public static String columnSplacedesc(){
		 return S_PLACEDESC;
	}
	/**
	 * Getter S_ISUSE   	 
	 */
	public static String columnSisuse(){
		 return S_ISUSE;
	}
	/**
	 * Getter S_BEFORESTATUS   	 
	 */
	public static String columnSbeforestatus(){
		 return S_BEFORESTATUS;
	}
	/**
	 * Getter S_AFTERSTATUS   	 
	 */
	public static String columnSafterstatus(){
		 return S_AFTERSTATUS;
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
	 * Getter S_MODELID, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeSmodelid(){
		 return "String";
	}
	/**
	 * Getter S_FORMID , NOT NULL   	 
	 */
	public static String columnJavaTypeSformid(){
		 return "String";
	}
	/**
	 * Getter S_PLACEID, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeSplaceid(){
		 return "String";
	}
	/**
	 * Getter S_PLACETYPE , NOT NULL   	 
	 */
	public static String columnJavaTypeSplacetype(){
		 return "String";
	}
	/**
	 * Getter S_STAMPTYPECODE   	 
	 */
	public static String columnJavaTypeSstamptypecode(){
		 return "String";
	}
	/**
	 * Getter S_PLACEDESC   	 
	 */
	public static String columnJavaTypeSplacedesc(){
		 return "String";
	}
	/**
	 * Getter S_ISUSE   	 
	 */
	public static String columnJavaTypeSisuse(){
		 return "String";
	}
	/**
	 * Getter S_BEFORESTATUS   	 
	 */
	public static String columnJavaTypeSbeforestatus(){
		 return "String";
	}
	/**
	 * Getter S_AFTERSTATUS   	 
	 */
	public static String columnJavaTypeSafterstatus(){
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
	 * Getter S_MODELID, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSmodelid(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_FORMID , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSformid(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PLACEID, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSplaceid(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PLACETYPE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSplacetype(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_STAMPTYPECODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSstamptypecode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PLACEDESC   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSplacedesc(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ISUSE   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSisuse(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_BEFORESTATUS   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSbeforestatus(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_AFTERSTATUS   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSafterstatus(){
		 return "CHARACTER";
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
	 * Getter S_MODELID, PK , NOT NULL   	 
	 */
	public static int columnWidthSmodelid(){
		 return 10;
	}
	/**
	 * Getter S_FORMID , NOT NULL   	 
	 */
	public static int columnWidthSformid(){
		 return 30;
	}
	/**
	 * Getter S_PLACEID, PK , NOT NULL   	 
	 */
	public static int columnWidthSplaceid(){
		 return 30;
	}
	/**
	 * Getter S_PLACETYPE , NOT NULL   	 
	 */
	public static int columnWidthSplacetype(){
		 return 1;
	}
	/**
	 * Getter S_STAMPTYPECODE   	 
	 */
	public static int columnWidthSstamptypecode(){
		 return 10;
	}
	/**
	 * Getter S_PLACEDESC   	 
	 */
	public static int columnWidthSplacedesc(){
		 return 50;
	}
	/**
	 * Getter S_ISUSE   	 
	 */
	public static int columnWidthSisuse(){
		 return 1;
	}
	/**
	 * Getter S_BEFORESTATUS   	 
	 */
	public static int columnWidthSbeforestatus(){
		 return 5;
	}
	/**
	 * Getter S_AFTERSTATUS   	 
	 */
	public static int columnWidthSafterstatus(){
		 return 5;
	}
}
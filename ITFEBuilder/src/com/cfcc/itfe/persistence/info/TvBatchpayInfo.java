      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TV_BATCHPAY</p>
 * <p>Description:  Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:01 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class TvBatchpayInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TV_BATCHPAY";
	
	/**
	 * column I_NO
	 */
	public static String I_NO ="I_NO";
	/**
	 * column S_ORGCODE
	 */
	public static String S_ORGCODE ="S_ORGCODE";
	/**
	 * column S_TRECODE
	 */
	public static String S_TRECODE ="S_TRECODE";
	/**
	 * column S_DATE
	 */
	public static String S_DATE ="S_DATE";
	/**
	 * column S_COUNTYCODE
	 */
	public static String S_COUNTYCODE ="S_COUNTYCODE";
	/**
	 * column S_FILENAME
	 */
	public static String S_FILENAME ="S_FILENAME";
	/**
	 * column S_FILEPATH
	 */
	public static String S_FILEPATH ="S_FILEPATH";
	/**
	 * column S_OFZIPNAME
	 */
	public static String S_OFZIPNAME ="S_OFZIPNAME";
	/**
	 * column S_ZIPPATH
	 */
	public static String S_ZIPPATH ="S_ZIPPATH";
	/**
	 * column S_KEY
	 */
	public static String S_KEY ="S_KEY";
	/**
	 * column S_STATUS
	 */
	public static String S_STATUS ="S_STATUS";
	/**
	 * column S_HOLD1
	 */
	public static String S_HOLD1 ="S_HOLD1";
	/**
	 * column S_HOLD2
	 */
	public static String S_HOLD2 ="S_HOLD2";
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
        String[] columnNames = new String[14];        
        columnNames[0]="I_NO";
        columnNames[1]="S_ORGCODE";
        columnNames[2]="S_TRECODE";
        columnNames[3]="S_DATE";
        columnNames[4]="S_COUNTYCODE";
        columnNames[5]="S_FILENAME";
        columnNames[6]="S_FILEPATH";
        columnNames[7]="S_OFZIPNAME";
        columnNames[8]="S_ZIPPATH";
        columnNames[9]="S_KEY";
        columnNames[10]="S_STATUS";
        columnNames[11]="S_HOLD1";
        columnNames[12]="S_HOLD2";
        columnNames[13]="TS_SYSUPDATE";
        return columnNames;     
    }
	/******************************************************
	*
	*  Get Column Name
	*
	*******************************************************/
	/**
	 * Getter I_NO, PK , NOT NULL   	 
	 */
	public static String columnIno(){
		 return I_NO;
	}
	/**
	 * Getter S_ORGCODE , NOT NULL   	 
	 */
	public static String columnSorgcode(){
		 return S_ORGCODE;
	}
	/**
	 * Getter S_TRECODE , NOT NULL   	 
	 */
	public static String columnStrecode(){
		 return S_TRECODE;
	}
	/**
	 * Getter S_DATE , NOT NULL   	 
	 */
	public static String columnSdate(){
		 return S_DATE;
	}
	/**
	 * Getter S_COUNTYCODE , NOT NULL   	 
	 */
	public static String columnScountycode(){
		 return S_COUNTYCODE;
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 */
	public static String columnSfilename(){
		 return S_FILENAME;
	}
	/**
	 * Getter S_FILEPATH   	 
	 */
	public static String columnSfilepath(){
		 return S_FILEPATH;
	}
	/**
	 * Getter S_OFZIPNAME   	 
	 */
	public static String columnSofzipname(){
		 return S_OFZIPNAME;
	}
	/**
	 * Getter S_ZIPPATH   	 
	 */
	public static String columnSzippath(){
		 return S_ZIPPATH;
	}
	/**
	 * Getter S_KEY   	 
	 */
	public static String columnSkey(){
		 return S_KEY;
	}
	/**
	 * Getter S_STATUS , NOT NULL   	 
	 */
	public static String columnSstatus(){
		 return S_STATUS;
	}
	/**
	 * Getter S_HOLD1   	 
	 */
	public static String columnShold1(){
		 return S_HOLD1;
	}
	/**
	 * Getter S_HOLD2   	 
	 */
	public static String columnShold2(){
		 return S_HOLD2;
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
	 * Getter I_NO, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeIno(){
		 return "Integer";
	}
	/**
	 * Getter S_ORGCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSorgcode(){
		 return "String";
	}
	/**
	 * Getter S_TRECODE , NOT NULL   	 
	 */
	public static String columnJavaTypeStrecode(){
		 return "String";
	}
	/**
	 * Getter S_DATE , NOT NULL   	 
	 */
	public static String columnJavaTypeSdate(){
		 return "String";
	}
	/**
	 * Getter S_COUNTYCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeScountycode(){
		 return "String";
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSfilename(){
		 return "String";
	}
	/**
	 * Getter S_FILEPATH   	 
	 */
	public static String columnJavaTypeSfilepath(){
		 return "String";
	}
	/**
	 * Getter S_OFZIPNAME   	 
	 */
	public static String columnJavaTypeSofzipname(){
		 return "String";
	}
	/**
	 * Getter S_ZIPPATH   	 
	 */
	public static String columnJavaTypeSzippath(){
		 return "String";
	}
	/**
	 * Getter S_KEY   	 
	 */
	public static String columnJavaTypeSkey(){
		 return "String";
	}
	/**
	 * Getter S_STATUS , NOT NULL   	 
	 */
	public static String columnJavaTypeSstatus(){
		 return "String";
	}
	/**
	 * Getter S_HOLD1   	 
	 */
	public static String columnJavaTypeShold1(){
		 return "String";
	}
	/**
	 * Getter S_HOLD2   	 
	 */
	public static String columnJavaTypeShold2(){
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
	 * Getter I_NO, PK , NOT NULL   	 
	 * columnType is INTEGER
	 */
	public static String columnDatabaseTypeIno(){
		 return "INTEGER";
	}
	/**
	 * Getter S_ORGCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSorgcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_TRECODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStrecode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_DATE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSdate(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_COUNTYCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeScountycode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSfilename(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_FILEPATH   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSfilepath(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_OFZIPNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSofzipname(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ZIPPATH   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSzippath(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_KEY   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSkey(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_STATUS , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSstatus(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_HOLD1   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeShold1(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_HOLD2   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeShold2(){
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
	 * Getter S_ORGCODE , NOT NULL   	 
	 */
	public static int columnWidthSorgcode(){
		 return 12;
	}
	/**
	 * Getter S_TRECODE , NOT NULL   	 
	 */
	public static int columnWidthStrecode(){
		 return 10;
	}
	/**
	 * Getter S_DATE , NOT NULL   	 
	 */
	public static int columnWidthSdate(){
		 return 8;
	}
	/**
	 * Getter S_COUNTYCODE , NOT NULL   	 
	 */
	public static int columnWidthScountycode(){
		 return 6;
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 */
	public static int columnWidthSfilename(){
		 return 50;
	}
	/**
	 * Getter S_FILEPATH   	 
	 */
	public static int columnWidthSfilepath(){
		 return 200;
	}
	/**
	 * Getter S_OFZIPNAME   	 
	 */
	public static int columnWidthSofzipname(){
		 return 50;
	}
	/**
	 * Getter S_ZIPPATH   	 
	 */
	public static int columnWidthSzippath(){
		 return 200;
	}
	/**
	 * Getter S_KEY   	 
	 */
	public static int columnWidthSkey(){
		 return 60;
	}
	/**
	 * Getter S_STATUS , NOT NULL   	 
	 */
	public static int columnWidthSstatus(){
		 return 5;
	}
	/**
	 * Getter S_HOLD1   	 
	 */
	public static int columnWidthShold1(){
		 return 200;
	}
	/**
	 * Getter S_HOLD2   	 
	 */
	public static int columnWidthShold2(){
		 return 200;
	}
}
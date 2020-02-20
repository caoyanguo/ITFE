      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TV_INCOME_DETAIL_REPORT_CHECK</p>
 * <p>Description:  Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:02 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class TvIncomeDetailReportCheckInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TV_INCOME_DETAIL_REPORT_CHECK";
	
	/**
	 * column S_ORGCODE
	 */
	public static String S_ORGCODE ="S_ORGCODE";
	/**
	 * column S_TRECODE
	 */
	public static String S_TRECODE ="S_TRECODE";
	/**
	 * column S_CREATDATE
	 */
	public static String S_CREATDATE ="S_CREATDATE";
	/**
	 * column F_AMT
	 */
	public static String F_AMT ="F_AMT";
	/**
	 * column N_MONEYDAY
	 */
	public static String N_MONEYDAY ="N_MONEYDAY";
	/**
	 * column S_STATUS
	 */
	public static String S_STATUS ="S_STATUS";
	/**
	 * column S_DEMO
	 */
	public static String S_DEMO ="S_DEMO";
	/**
	 * column S_TREATTRIB
	 */
	public static String S_TREATTRIB ="S_TREATTRIB";
	/**
	 * column TS_SYSUPDATE
	 */
	public static String TS_SYSUPDATE ="TS_SYSUPDATE";
	/**
	 * column S_HOLD1
	 */
	public static String S_HOLD1 ="S_HOLD1";
	/**
	 * column S_HOLD2
	 */
	public static String S_HOLD2 ="S_HOLD2";
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
        String[] columnNames = new String[11];        
        columnNames[0]="S_ORGCODE";
        columnNames[1]="S_TRECODE";
        columnNames[2]="S_CREATDATE";
        columnNames[3]="F_AMT";
        columnNames[4]="N_MONEYDAY";
        columnNames[5]="S_STATUS";
        columnNames[6]="S_DEMO";
        columnNames[7]="S_TREATTRIB";
        columnNames[8]="TS_SYSUPDATE";
        columnNames[9]="S_HOLD1";
        columnNames[10]="S_HOLD2";
        return columnNames;     
    }
	/******************************************************
	*
	*  Get Column Name
	*
	*******************************************************/
	/**
	 * Getter S_ORGCODE , NOT NULL   	 
	 */
	public static String columnSorgcode(){
		 return S_ORGCODE;
	}
	/**
	 * Getter S_TRECODE, PK , NOT NULL   	 
	 */
	public static String columnStrecode(){
		 return S_TRECODE;
	}
	/**
	 * Getter S_CREATDATE, PK , NOT NULL   	 
	 */
	public static String columnScreatdate(){
		 return S_CREATDATE;
	}
	/**
	 * Getter F_AMT   	 
	 */
	public static String columnFamt(){
		 return F_AMT;
	}
	/**
	 * Getter N_MONEYDAY   	 
	 */
	public static String columnNmoneyday(){
		 return N_MONEYDAY;
	}
	/**
	 * Getter S_STATUS , NOT NULL   	 
	 */
	public static String columnSstatus(){
		 return S_STATUS;
	}
	/**
	 * Getter S_DEMO   	 
	 */
	public static String columnSdemo(){
		 return S_DEMO;
	}
	/**
	 * Getter S_TREATTRIB , NOT NULL   	 
	 */
	public static String columnStreattrib(){
		 return S_TREATTRIB;
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 */
	public static String columnTssysupdate(){
		 return TS_SYSUPDATE;
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
	/******************************************************
	*
	*  Get Column Java Type
	*
	*******************************************************/
	/**
	 * Getter S_ORGCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSorgcode(){
		 return "String";
	}
	/**
	 * Getter S_TRECODE, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeStrecode(){
		 return "String";
	}
	/**
	 * Getter S_CREATDATE, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeScreatdate(){
		 return "String";
	}
	/**
	 * Getter F_AMT   	 
	 */
	public static String columnJavaTypeFamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter N_MONEYDAY   	 
	 */
	public static String columnJavaTypeNmoneyday(){
		 return "BigDecimal";
	}
	/**
	 * Getter S_STATUS , NOT NULL   	 
	 */
	public static String columnJavaTypeSstatus(){
		 return "String";
	}
	/**
	 * Getter S_DEMO   	 
	 */
	public static String columnJavaTypeSdemo(){
		 return "String";
	}
	/**
	 * Getter S_TREATTRIB , NOT NULL   	 
	 */
	public static String columnJavaTypeStreattrib(){
		 return "String";
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 */
	public static String columnJavaTypeTssysupdate(){
		 return "Timestamp";
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
	/******************************************************
	*
	*  Get Column Database Type
	*
	*******************************************************/
	/**
	 * Getter S_ORGCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSorgcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_TRECODE, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStrecode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CREATDATE, PK , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeScreatdate(){
		 return "CHARACTER";
	}
	/**
	 * Getter F_AMT   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeFamt(){
		 return "DECIMAL";
	}
	/**
	 * Getter N_MONEYDAY   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNmoneyday(){
		 return "DECIMAL";
	}
	/**
	 * Getter S_STATUS , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSstatus(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_DEMO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSdemo(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_TREATTRIB , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStreattrib(){
		 return "VARCHAR";
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 * columnType is TIMESTAMP
	 */
	public static String columnDatabaseTypeTssysupdate(){
		 return "TIMESTAMP";
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
	 * Getter S_TRECODE, PK , NOT NULL   	 
	 */
	public static int columnWidthStrecode(){
		 return 10;
	}
	/**
	 * Getter S_CREATDATE, PK , NOT NULL   	 
	 */
	public static int columnWidthScreatdate(){
		 return 8;
	}
	/**
	 * Getter S_STATUS , NOT NULL   	 
	 */
	public static int columnWidthSstatus(){
		 return 1;
	}
	/**
	 * Getter S_DEMO   	 
	 */
	public static int columnWidthSdemo(){
		 return 60;
	}
	/**
	 * Getter S_TREATTRIB , NOT NULL   	 
	 */
	public static int columnWidthStreattrib(){
		 return 1;
	}
	/**
	 * Getter S_HOLD1   	 
	 */
	public static int columnWidthShold1(){
		 return 60;
	}
	/**
	 * Getter S_HOLD2   	 
	 */
	public static int columnWidthShold2(){
		 return 60;
	}
}
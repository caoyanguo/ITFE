      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TS_CORRREASON</p>
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

public class TsCorrreasonInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TS_CORRREASON";
	
	/**
	 * column S_BOOKORGCODE
	 */
	public static String S_BOOKORGCODE ="S_BOOKORGCODE";
	/**
	 * column S_TBSCORRCODE
	 */
	public static String S_TBSCORRCODE ="S_TBSCORRCODE";
	/**
	 * column S_TBSCORRNAME
	 */
	public static String S_TBSCORRNAME ="S_TBSCORRNAME";
	/**
	 * column S_TCBSCORRCODE
	 */
	public static String S_TCBSCORRCODE ="S_TCBSCORRCODE";
	/**
	 * column S_TCBSCORRNAME
	 */
	public static String S_TCBSCORRNAME ="S_TCBSCORRNAME";
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
        String[] columnNames = new String[6];        
        columnNames[0]="S_BOOKORGCODE";
        columnNames[1]="S_TBSCORRCODE";
        columnNames[2]="S_TBSCORRNAME";
        columnNames[3]="S_TCBSCORRCODE";
        columnNames[4]="S_TCBSCORRNAME";
        columnNames[5]="TS_SYSUPDATE";
        return columnNames;     
    }
	/******************************************************
	*
	*  Get Column Name
	*
	*******************************************************/
	/**
	 * Getter S_BOOKORGCODE, PK , NOT NULL   	 
	 */
	public static String columnSbookorgcode(){
		 return S_BOOKORGCODE;
	}
	/**
	 * Getter S_TBSCORRCODE, PK , NOT NULL   	 
	 */
	public static String columnStbscorrcode(){
		 return S_TBSCORRCODE;
	}
	/**
	 * Getter S_TBSCORRNAME , NOT NULL   	 
	 */
	public static String columnStbscorrname(){
		 return S_TBSCORRNAME;
	}
	/**
	 * Getter S_TCBSCORRCODE , NOT NULL   	 
	 */
	public static String columnStcbscorrcode(){
		 return S_TCBSCORRCODE;
	}
	/**
	 * Getter S_TCBSCORRNAME , NOT NULL   	 
	 */
	public static String columnStcbscorrname(){
		 return S_TCBSCORRNAME;
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
	 * Getter S_BOOKORGCODE, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeSbookorgcode(){
		 return "String";
	}
	/**
	 * Getter S_TBSCORRCODE, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeStbscorrcode(){
		 return "String";
	}
	/**
	 * Getter S_TBSCORRNAME , NOT NULL   	 
	 */
	public static String columnJavaTypeStbscorrname(){
		 return "String";
	}
	/**
	 * Getter S_TCBSCORRCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeStcbscorrcode(){
		 return "String";
	}
	/**
	 * Getter S_TCBSCORRNAME , NOT NULL   	 
	 */
	public static String columnJavaTypeStcbscorrname(){
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
	 * Getter S_BOOKORGCODE, PK , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSbookorgcode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_TBSCORRCODE, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStbscorrcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_TBSCORRNAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStbscorrname(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_TCBSCORRCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStcbscorrcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_TCBSCORRNAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStcbscorrname(){
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
	 * Getter S_BOOKORGCODE, PK , NOT NULL   	 
	 */
	public static int columnWidthSbookorgcode(){
		 return 12;
	}
	/**
	 * Getter S_TBSCORRCODE, PK , NOT NULL   	 
	 */
	public static int columnWidthStbscorrcode(){
		 return 12;
	}
	/**
	 * Getter S_TBSCORRNAME , NOT NULL   	 
	 */
	public static int columnWidthStbscorrname(){
		 return 50;
	}
	/**
	 * Getter S_TCBSCORRCODE , NOT NULL   	 
	 */
	public static int columnWidthStcbscorrcode(){
		 return 10;
	}
	/**
	 * Getter S_TCBSCORRNAME , NOT NULL   	 
	 */
	public static int columnWidthStcbscorrname(){
		 return 50;
	}
}
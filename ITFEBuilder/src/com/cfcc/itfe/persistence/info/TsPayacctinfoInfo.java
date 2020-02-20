      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TS_PAYACCTINFO</p>
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

public class TsPayacctinfoInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TS_PAYACCTINFO";
	
	/**
	 * column S_ORGCODE
	 */
	public static String S_ORGCODE ="S_ORGCODE";
	/**
	 * column S_TRECODE
	 */
	public static String S_TRECODE ="S_TRECODE";
	/**
	 * column S_GENBANKCODE
	 */
	public static String S_GENBANKCODE ="S_GENBANKCODE";
	/**
	 * column S_PAYERACCT
	 */
	public static String S_PAYERACCT ="S_PAYERACCT";
	/**
	 * column S_PAYERNAME
	 */
	public static String S_PAYERNAME ="S_PAYERNAME";
	/**
	 * column S_PAYEEACCT
	 */
	public static String S_PAYEEACCT ="S_PAYEEACCT";
	/**
	 * column S_PAYEENAME
	 */
	public static String S_PAYEENAME ="S_PAYEENAME";
	/**
	 * column S_BIZTYPE
	 */
	public static String S_BIZTYPE ="S_BIZTYPE";
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
        String[] columnNames = new String[9];        
        columnNames[0]="S_ORGCODE";
        columnNames[1]="S_TRECODE";
        columnNames[2]="S_GENBANKCODE";
        columnNames[3]="S_PAYERACCT";
        columnNames[4]="S_PAYERNAME";
        columnNames[5]="S_PAYEEACCT";
        columnNames[6]="S_PAYEENAME";
        columnNames[7]="S_BIZTYPE";
        columnNames[8]="TS_SYSUPDATE";
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
	 * Getter S_TRECODE, PK , NOT NULL   	 
	 */
	public static String columnStrecode(){
		 return S_TRECODE;
	}
	/**
	 * Getter S_GENBANKCODE, PK , NOT NULL   	 
	 */
	public static String columnSgenbankcode(){
		 return S_GENBANKCODE;
	}
	/**
	 * Getter S_PAYERACCT, PK , NOT NULL   	 
	 */
	public static String columnSpayeracct(){
		 return S_PAYERACCT;
	}
	/**
	 * Getter S_PAYERNAME , NOT NULL   	 
	 */
	public static String columnSpayername(){
		 return S_PAYERNAME;
	}
	/**
	 * Getter S_PAYEEACCT, PK , NOT NULL   	 
	 */
	public static String columnSpayeeacct(){
		 return S_PAYEEACCT;
	}
	/**
	 * Getter S_PAYEENAME , NOT NULL   	 
	 */
	public static String columnSpayeename(){
		 return S_PAYEENAME;
	}
	/**
	 * Getter S_BIZTYPE   	 
	 */
	public static String columnSbiztype(){
		 return S_BIZTYPE;
	}
	/**
	 * Getter TS_SYSUPDATE , NOT NULL   	 
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
	 * Getter S_TRECODE, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeStrecode(){
		 return "String";
	}
	/**
	 * Getter S_GENBANKCODE, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeSgenbankcode(){
		 return "String";
	}
	/**
	 * Getter S_PAYERACCT, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayeracct(){
		 return "String";
	}
	/**
	 * Getter S_PAYERNAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayername(){
		 return "String";
	}
	/**
	 * Getter S_PAYEEACCT, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayeeacct(){
		 return "String";
	}
	/**
	 * Getter S_PAYEENAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayeename(){
		 return "String";
	}
	/**
	 * Getter S_BIZTYPE   	 
	 */
	public static String columnJavaTypeSbiztype(){
		 return "String";
	}
	/**
	 * Getter TS_SYSUPDATE , NOT NULL   	 
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
	 * Getter S_TRECODE, PK , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeStrecode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_GENBANKCODE, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSgenbankcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYERACCT, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeracct(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYERNAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayername(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYEEACCT, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeeacct(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYEENAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeename(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_BIZTYPE   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSbiztype(){
		 return "CHARACTER";
	}
	/**
	 * Getter TS_SYSUPDATE , NOT NULL   	 
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
	 * Getter S_TRECODE, PK , NOT NULL   	 
	 */
	public static int columnWidthStrecode(){
		 return 10;
	}
	/**
	 * Getter S_GENBANKCODE, PK , NOT NULL   	 
	 */
	public static int columnWidthSgenbankcode(){
		 return 12;
	}
	/**
	 * Getter S_PAYERACCT, PK , NOT NULL   	 
	 */
	public static int columnWidthSpayeracct(){
		 return 32;
	}
	/**
	 * Getter S_PAYERNAME , NOT NULL   	 
	 */
	public static int columnWidthSpayername(){
		 return 60;
	}
	/**
	 * Getter S_PAYEEACCT, PK , NOT NULL   	 
	 */
	public static int columnWidthSpayeeacct(){
		 return 32;
	}
	/**
	 * Getter S_PAYEENAME , NOT NULL   	 
	 */
	public static int columnWidthSpayeename(){
		 return 60;
	}
	/**
	 * Getter S_BIZTYPE   	 
	 */
	public static int columnWidthSbiztype(){
		 return 10;
	}
}
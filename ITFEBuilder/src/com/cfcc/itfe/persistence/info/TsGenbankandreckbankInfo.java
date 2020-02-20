      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TS_GENBANKANDRECKBANK</p>
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

public class TsGenbankandreckbankInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TS_GENBANKANDRECKBANK";
	
	/**
	 * column I_SEQNO
	 */
	public static String I_SEQNO ="I_SEQNO";
	/**
	 * column S_BOOKORGCODE
	 */
	public static String S_BOOKORGCODE ="S_BOOKORGCODE";
	/**
	 * column S_GENBANKCODE
	 */
	public static String S_GENBANKCODE ="S_GENBANKCODE";
	/**
	 * column S_RECKBANKCODE
	 */
	public static String S_RECKBANKCODE ="S_RECKBANKCODE";
	/**
	 * column S_GENBANKNAME
	 */
	public static String S_GENBANKNAME ="S_GENBANKNAME";
	/**
	 * column S_RECKBANKNAME
	 */
	public static String S_RECKBANKNAME ="S_RECKBANKNAME";
	/**
	 * column TS_SYSUPDATE
	 */
	public static String TS_SYSUPDATE ="TS_SYSUPDATE";
	/**
	 * column S_TRECODE
	 */
	public static String S_TRECODE ="S_TRECODE";
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
        String[] columnNames = new String[8];        
        columnNames[0]="I_SEQNO";
        columnNames[1]="S_BOOKORGCODE";
        columnNames[2]="S_GENBANKCODE";
        columnNames[3]="S_RECKBANKCODE";
        columnNames[4]="S_GENBANKNAME";
        columnNames[5]="S_RECKBANKNAME";
        columnNames[6]="TS_SYSUPDATE";
        columnNames[7]="S_TRECODE";
        return columnNames;     
    }
	/******************************************************
	*
	*  Get Column Name
	*
	*******************************************************/
	/**
	 * Getter I_SEQNO, PK , NOT NULL  , Identity  , Generated 	 
	 */
	public static String columnIseqno(){
		 return I_SEQNO;
	}
	/**
	 * Getter S_BOOKORGCODE , NOT NULL   	 
	 */
	public static String columnSbookorgcode(){
		 return S_BOOKORGCODE;
	}
	/**
	 * Getter S_GENBANKCODE , NOT NULL   	 
	 */
	public static String columnSgenbankcode(){
		 return S_GENBANKCODE;
	}
	/**
	 * Getter S_RECKBANKCODE , NOT NULL   	 
	 */
	public static String columnSreckbankcode(){
		 return S_RECKBANKCODE;
	}
	/**
	 * Getter S_GENBANKNAME   	 
	 */
	public static String columnSgenbankname(){
		 return S_GENBANKNAME;
	}
	/**
	 * Getter S_RECKBANKNAME   	 
	 */
	public static String columnSreckbankname(){
		 return S_RECKBANKNAME;
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 */
	public static String columnTssysupdate(){
		 return TS_SYSUPDATE;
	}
	/**
	 * Getter S_TRECODE   	 
	 */
	public static String columnStrecode(){
		 return S_TRECODE;
	}
	/******************************************************
	*
	*  Get Column Java Type
	*
	*******************************************************/
	/**
	 * Getter I_SEQNO, PK , NOT NULL  , Identity  , Generated 	 
	 */
	public static String columnJavaTypeIseqno(){
		 return "Long";
	}
	/**
	 * Getter S_BOOKORGCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSbookorgcode(){
		 return "String";
	}
	/**
	 * Getter S_GENBANKCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSgenbankcode(){
		 return "String";
	}
	/**
	 * Getter S_RECKBANKCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSreckbankcode(){
		 return "String";
	}
	/**
	 * Getter S_GENBANKNAME   	 
	 */
	public static String columnJavaTypeSgenbankname(){
		 return "String";
	}
	/**
	 * Getter S_RECKBANKNAME   	 
	 */
	public static String columnJavaTypeSreckbankname(){
		 return "String";
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 */
	public static String columnJavaTypeTssysupdate(){
		 return "Timestamp";
	}
	/**
	 * Getter S_TRECODE   	 
	 */
	public static String columnJavaTypeStrecode(){
		 return "String";
	}
	/******************************************************
	*
	*  Get Column Database Type
	*
	*******************************************************/
	/**
	 * Getter I_SEQNO, PK , NOT NULL  , Identity  , Generated 	 
	 * columnType is BIGINT
	 */
	public static String columnDatabaseTypeIseqno(){
		 return "BIGINT";
	}
	/**
	 * Getter S_BOOKORGCODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSbookorgcode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_GENBANKCODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSgenbankcode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_RECKBANKCODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSreckbankcode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_GENBANKNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSgenbankname(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_RECKBANKNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSreckbankname(){
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
	 * Getter S_TRECODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStrecode(){
		 return "VARCHAR";
	}
	/******************************************************
	*
	*  Get Column With
	*
	*******************************************************/
	/**
	 * Getter S_BOOKORGCODE , NOT NULL   	 
	 */
	public static int columnWidthSbookorgcode(){
		 return 12;
	}
	/**
	 * Getter S_GENBANKCODE , NOT NULL   	 
	 */
	public static int columnWidthSgenbankcode(){
		 return 12;
	}
	/**
	 * Getter S_RECKBANKCODE , NOT NULL   	 
	 */
	public static int columnWidthSreckbankcode(){
		 return 12;
	}
	/**
	 * Getter S_GENBANKNAME   	 
	 */
	public static int columnWidthSgenbankname(){
		 return 60;
	}
	/**
	 * Getter S_RECKBANKNAME   	 
	 */
	public static int columnWidthSreckbankname(){
		 return 60;
	}
	/**
	 * Getter S_TRECODE   	 
	 */
	public static int columnWidthStrecode(){
		 return 12;
	}
}
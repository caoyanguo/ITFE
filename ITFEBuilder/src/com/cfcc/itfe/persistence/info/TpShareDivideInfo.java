      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TP_SHARE_DIVIDE</p>
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

public class TpShareDivideInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TP_SHARE_DIVIDE";
	
	/**
	 * column I_PARAMSEQNO
	 */
	public static String I_PARAMSEQNO ="I_PARAMSEQNO";
	/**
	 * column S_BOOKORGCODE
	 */
	public static String S_BOOKORGCODE ="S_BOOKORGCODE";
	/**
	 * column C_TRIMFLAG
	 */
	public static String C_TRIMFLAG ="C_TRIMFLAG";
	/**
	 * column I_DIVIDEGRPID
	 */
	public static String I_DIVIDEGRPID ="I_DIVIDEGRPID";
	/**
	 * column S_ROOTTRECODE
	 */
	public static String S_ROOTTRECODE ="S_ROOTTRECODE";
	/**
	 * column S_TRATRECODE
	 */
	public static String S_TRATRECODE ="S_TRATRECODE";
	/**
	 * column S_PAYEETRECODE
	 */
	public static String S_PAYEETRECODE ="S_PAYEETRECODE";
	/**
	 * column S_ROOTTAXORGCODE
	 */
	public static String S_ROOTTAXORGCODE ="S_ROOTTAXORGCODE";
	/**
	 * column S_ROOTBDGSBTCODE
	 */
	public static String S_ROOTBDGSBTCODE ="S_ROOTBDGSBTCODE";
	/**
	 * column C_ROOTBDGKIND
	 */
	public static String C_ROOTBDGKIND ="C_ROOTBDGKIND";
	/**
	 * column S_ROOTASTFLAG
	 */
	public static String S_ROOTASTFLAG ="S_ROOTASTFLAG";
	/**
	 * column S_AFTTRECODE
	 */
	public static String S_AFTTRECODE ="S_AFTTRECODE";
	/**
	 * column S_AFTTAXORGCODE
	 */
	public static String S_AFTTAXORGCODE ="S_AFTTAXORGCODE";
	/**
	 * column C_AFTBDGLEVEL
	 */
	public static String C_AFTBDGLEVEL ="C_AFTBDGLEVEL";
	/**
	 * column S_AFTBDGSBTCODE
	 */
	public static String S_AFTBDGSBTCODE ="S_AFTBDGSBTCODE";
	/**
	 * column C_AFTBDGTYPE
	 */
	public static String C_AFTBDGTYPE ="C_AFTBDGTYPE";
	/**
	 * column S_AFTASTFLAG
	 */
	public static String S_AFTASTFLAG ="S_AFTASTFLAG";
	/**
	 * column F_JOINDIVIDERATE
	 */
	public static String F_JOINDIVIDERATE ="F_JOINDIVIDERATE";
	/**
	 * column C_GOVERNRALATION
	 */
	public static String C_GOVERNRALATION ="C_GOVERNRALATION";
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
        String[] columnNames = new String[20];        
        columnNames[0]="I_PARAMSEQNO";
        columnNames[1]="S_BOOKORGCODE";
        columnNames[2]="C_TRIMFLAG";
        columnNames[3]="I_DIVIDEGRPID";
        columnNames[4]="S_ROOTTRECODE";
        columnNames[5]="S_TRATRECODE";
        columnNames[6]="S_PAYEETRECODE";
        columnNames[7]="S_ROOTTAXORGCODE";
        columnNames[8]="S_ROOTBDGSBTCODE";
        columnNames[9]="C_ROOTBDGKIND";
        columnNames[10]="S_ROOTASTFLAG";
        columnNames[11]="S_AFTTRECODE";
        columnNames[12]="S_AFTTAXORGCODE";
        columnNames[13]="C_AFTBDGLEVEL";
        columnNames[14]="S_AFTBDGSBTCODE";
        columnNames[15]="C_AFTBDGTYPE";
        columnNames[16]="S_AFTASTFLAG";
        columnNames[17]="F_JOINDIVIDERATE";
        columnNames[18]="C_GOVERNRALATION";
        columnNames[19]="TS_SYSUPDATE";
        return columnNames;     
    }
	/******************************************************
	*
	*  Get Column Name
	*
	*******************************************************/
	/**
	 * Getter I_PARAMSEQNO, PK , NOT NULL  , Identity  , Generated 	 
	 */
	public static String columnIparamseqno(){
		 return I_PARAMSEQNO;
	}
	/**
	 * Getter S_BOOKORGCODE , NOT NULL   	 
	 */
	public static String columnSbookorgcode(){
		 return S_BOOKORGCODE;
	}
	/**
	 * Getter C_TRIMFLAG , NOT NULL   	 
	 */
	public static String columnCtrimflag(){
		 return C_TRIMFLAG;
	}
	/**
	 * Getter I_DIVIDEGRPID , NOT NULL   	 
	 */
	public static String columnIdividegrpid(){
		 return I_DIVIDEGRPID;
	}
	/**
	 * Getter S_ROOTTRECODE , NOT NULL   	 
	 */
	public static String columnSroottrecode(){
		 return S_ROOTTRECODE;
	}
	/**
	 * Getter S_TRATRECODE , NOT NULL   	 
	 */
	public static String columnStratrecode(){
		 return S_TRATRECODE;
	}
	/**
	 * Getter S_PAYEETRECODE , NOT NULL   	 
	 */
	public static String columnSpayeetrecode(){
		 return S_PAYEETRECODE;
	}
	/**
	 * Getter S_ROOTTAXORGCODE , NOT NULL   	 
	 */
	public static String columnSroottaxorgcode(){
		 return S_ROOTTAXORGCODE;
	}
	/**
	 * Getter S_ROOTBDGSBTCODE , NOT NULL   	 
	 */
	public static String columnSrootbdgsbtcode(){
		 return S_ROOTBDGSBTCODE;
	}
	/**
	 * Getter C_ROOTBDGKIND , NOT NULL   	 
	 */
	public static String columnCrootbdgkind(){
		 return C_ROOTBDGKIND;
	}
	/**
	 * Getter S_ROOTASTFLAG   	 
	 */
	public static String columnSrootastflag(){
		 return S_ROOTASTFLAG;
	}
	/**
	 * Getter S_AFTTRECODE , NOT NULL   	 
	 */
	public static String columnSafttrecode(){
		 return S_AFTTRECODE;
	}
	/**
	 * Getter S_AFTTAXORGCODE , NOT NULL   	 
	 */
	public static String columnSafttaxorgcode(){
		 return S_AFTTAXORGCODE;
	}
	/**
	 * Getter C_AFTBDGLEVEL , NOT NULL   	 
	 */
	public static String columnCaftbdglevel(){
		 return C_AFTBDGLEVEL;
	}
	/**
	 * Getter S_AFTBDGSBTCODE , NOT NULL   	 
	 */
	public static String columnSaftbdgsbtcode(){
		 return S_AFTBDGSBTCODE;
	}
	/**
	 * Getter C_AFTBDGTYPE , NOT NULL   	 
	 */
	public static String columnCaftbdgtype(){
		 return C_AFTBDGTYPE;
	}
	/**
	 * Getter S_AFTASTFLAG   	 
	 */
	public static String columnSaftastflag(){
		 return S_AFTASTFLAG;
	}
	/**
	 * Getter F_JOINDIVIDERATE , NOT NULL   	 
	 */
	public static String columnFjoindividerate(){
		 return F_JOINDIVIDERATE;
	}
	/**
	 * Getter C_GOVERNRALATION , NOT NULL   	 
	 */
	public static String columnCgovernralation(){
		 return C_GOVERNRALATION;
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
	 * Getter I_PARAMSEQNO, PK , NOT NULL  , Identity  , Generated 	 
	 */
	public static String columnJavaTypeIparamseqno(){
		 return "Integer";
	}
	/**
	 * Getter S_BOOKORGCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSbookorgcode(){
		 return "String";
	}
	/**
	 * Getter C_TRIMFLAG , NOT NULL   	 
	 */
	public static String columnJavaTypeCtrimflag(){
		 return "String";
	}
	/**
	 * Getter I_DIVIDEGRPID , NOT NULL   	 
	 */
	public static String columnJavaTypeIdividegrpid(){
		 return "Integer";
	}
	/**
	 * Getter S_ROOTTRECODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSroottrecode(){
		 return "String";
	}
	/**
	 * Getter S_TRATRECODE , NOT NULL   	 
	 */
	public static String columnJavaTypeStratrecode(){
		 return "String";
	}
	/**
	 * Getter S_PAYEETRECODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayeetrecode(){
		 return "String";
	}
	/**
	 * Getter S_ROOTTAXORGCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSroottaxorgcode(){
		 return "String";
	}
	/**
	 * Getter S_ROOTBDGSBTCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSrootbdgsbtcode(){
		 return "String";
	}
	/**
	 * Getter C_ROOTBDGKIND , NOT NULL   	 
	 */
	public static String columnJavaTypeCrootbdgkind(){
		 return "String";
	}
	/**
	 * Getter S_ROOTASTFLAG   	 
	 */
	public static String columnJavaTypeSrootastflag(){
		 return "String";
	}
	/**
	 * Getter S_AFTTRECODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSafttrecode(){
		 return "String";
	}
	/**
	 * Getter S_AFTTAXORGCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSafttaxorgcode(){
		 return "String";
	}
	/**
	 * Getter C_AFTBDGLEVEL , NOT NULL   	 
	 */
	public static String columnJavaTypeCaftbdglevel(){
		 return "String";
	}
	/**
	 * Getter S_AFTBDGSBTCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSaftbdgsbtcode(){
		 return "String";
	}
	/**
	 * Getter C_AFTBDGTYPE , NOT NULL   	 
	 */
	public static String columnJavaTypeCaftbdgtype(){
		 return "String";
	}
	/**
	 * Getter S_AFTASTFLAG   	 
	 */
	public static String columnJavaTypeSaftastflag(){
		 return "String";
	}
	/**
	 * Getter F_JOINDIVIDERATE , NOT NULL   	 
	 */
	public static String columnJavaTypeFjoindividerate(){
		 return "BigDecimal";
	}
	/**
	 * Getter C_GOVERNRALATION , NOT NULL   	 
	 */
	public static String columnJavaTypeCgovernralation(){
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
	 * Getter I_PARAMSEQNO, PK , NOT NULL  , Identity  , Generated 	 
	 * columnType is INTEGER
	 */
	public static String columnDatabaseTypeIparamseqno(){
		 return "INTEGER";
	}
	/**
	 * Getter S_BOOKORGCODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSbookorgcode(){
		 return "CHARACTER";
	}
	/**
	 * Getter C_TRIMFLAG , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCtrimflag(){
		 return "CHARACTER";
	}
	/**
	 * Getter I_DIVIDEGRPID , NOT NULL   	 
	 * columnType is INTEGER
	 */
	public static String columnDatabaseTypeIdividegrpid(){
		 return "INTEGER";
	}
	/**
	 * Getter S_ROOTTRECODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSroottrecode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_TRATRECODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeStratrecode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_PAYEETRECODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSpayeetrecode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_ROOTTAXORGCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSroottaxorgcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ROOTBDGSBTCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSrootbdgsbtcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter C_ROOTBDGKIND , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCrootbdgkind(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_ROOTASTFLAG   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSrootastflag(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_AFTTRECODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSafttrecode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_AFTTAXORGCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSafttaxorgcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter C_AFTBDGLEVEL , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCaftbdglevel(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_AFTBDGSBTCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSaftbdgsbtcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter C_AFTBDGTYPE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCaftbdgtype(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_AFTASTFLAG   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSaftastflag(){
		 return "VARCHAR";
	}
	/**
	 * Getter F_JOINDIVIDERATE , NOT NULL   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeFjoindividerate(){
		 return "DECIMAL";
	}
	/**
	 * Getter C_GOVERNRALATION , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCgovernralation(){
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
	 * Getter S_BOOKORGCODE , NOT NULL   	 
	 */
	public static int columnWidthSbookorgcode(){
		 return 12;
	}
	/**
	 * Getter C_TRIMFLAG , NOT NULL   	 
	 */
	public static int columnWidthCtrimflag(){
		 return 1;
	}
	/**
	 * Getter S_ROOTTRECODE , NOT NULL   	 
	 */
	public static int columnWidthSroottrecode(){
		 return 10;
	}
	/**
	 * Getter S_TRATRECODE , NOT NULL   	 
	 */
	public static int columnWidthStratrecode(){
		 return 10;
	}
	/**
	 * Getter S_PAYEETRECODE , NOT NULL   	 
	 */
	public static int columnWidthSpayeetrecode(){
		 return 10;
	}
	/**
	 * Getter S_ROOTTAXORGCODE , NOT NULL   	 
	 */
	public static int columnWidthSroottaxorgcode(){
		 return 20;
	}
	/**
	 * Getter S_ROOTBDGSBTCODE , NOT NULL   	 
	 */
	public static int columnWidthSrootbdgsbtcode(){
		 return 30;
	}
	/**
	 * Getter C_ROOTBDGKIND , NOT NULL   	 
	 */
	public static int columnWidthCrootbdgkind(){
		 return 1;
	}
	/**
	 * Getter S_ROOTASTFLAG   	 
	 */
	public static int columnWidthSrootastflag(){
		 return 10;
	}
	/**
	 * Getter S_AFTTRECODE , NOT NULL   	 
	 */
	public static int columnWidthSafttrecode(){
		 return 10;
	}
	/**
	 * Getter S_AFTTAXORGCODE , NOT NULL   	 
	 */
	public static int columnWidthSafttaxorgcode(){
		 return 20;
	}
	/**
	 * Getter C_AFTBDGLEVEL , NOT NULL   	 
	 */
	public static int columnWidthCaftbdglevel(){
		 return 1;
	}
	/**
	 * Getter S_AFTBDGSBTCODE , NOT NULL   	 
	 */
	public static int columnWidthSaftbdgsbtcode(){
		 return 30;
	}
	/**
	 * Getter C_AFTBDGTYPE , NOT NULL   	 
	 */
	public static int columnWidthCaftbdgtype(){
		 return 1;
	}
	/**
	 * Getter S_AFTASTFLAG   	 
	 */
	public static int columnWidthSaftastflag(){
		 return 10;
	}
	/**
	 * Getter C_GOVERNRALATION , NOT NULL   	 
	 */
	public static int columnWidthCgovernralation(){
		 return 1;
	}
}
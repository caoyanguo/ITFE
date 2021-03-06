      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TBS_TV_DIRECTPAYPLAN_MAIN</p>
 * <p>Description:  Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:57 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class TbsTvDirectpayplanMainInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TBS_TV_DIRECTPAYPLAN_MAIN";
	
	/**
	 * column I_VOUSRLNO
	 */
	public static String I_VOUSRLNO ="I_VOUSRLNO";
	/**
	 * column S_TRECODE
	 */
	public static String S_TRECODE ="S_TRECODE";
	/**
	 * column S_PAYERACCT
	 */
	public static String S_PAYERACCT ="S_PAYERACCT";
	/**
	 * column S_PAYEROPNBNKNO
	 */
	public static String S_PAYEROPNBNKNO ="S_PAYEROPNBNKNO";
	/**
	 * column S_PAYEEACCT
	 */
	public static String S_PAYEEACCT ="S_PAYEEACCT";
	/**
	 * column S_PAYEENAME
	 */
	public static String S_PAYEENAME ="S_PAYEENAME";
	/**
	 * column S_PAYEEOPNBNKNO
	 */
	public static String S_PAYEEOPNBNKNO ="S_PAYEEOPNBNKNO";
	/**
	 * column C_BDGKIND
	 */
	public static String C_BDGKIND ="C_BDGKIND";
	/**
	 * column F_AMT
	 */
	public static String F_AMT ="F_AMT";
	/**
	 * column S_VOUNO
	 */
	public static String S_VOUNO ="S_VOUNO";
	/**
	 * column S_CRPVOUCODE
	 */
	public static String S_CRPVOUCODE ="S_CRPVOUCODE";
	/**
	 * column D_VOUCHER
	 */
	public static String D_VOUCHER ="D_VOUCHER";
	/**
	 * column I_OFYEAR
	 */
	public static String I_OFYEAR ="I_OFYEAR";
	/**
	 * column D_ACCEPT
	 */
	public static String D_ACCEPT ="D_ACCEPT";
	/**
	 * column D_ACCT
	 */
	public static String D_ACCT ="D_ACCT";
	/**
	 * column S_BIZTYPE
	 */
	public static String S_BIZTYPE ="S_BIZTYPE";
	/**
	 * column S_PACKAGENO
	 */
	public static String S_PACKAGENO ="S_PACKAGENO";
	/**
	 * column S_STATUS
	 */
	public static String S_STATUS ="S_STATUS";
	/**
	 * column S_BOOKORGCODE
	 */
	public static String S_BOOKORGCODE ="S_BOOKORGCODE";
	/**
	 * column S_FILENAME
	 */
	public static String S_FILENAME ="S_FILENAME";
	/**
	 * column TS_SYSUPDATE
	 */
	public static String TS_SYSUPDATE ="TS_SYSUPDATE";
	/**
	 * column S_PAYEEOPNBNKNAME
	 */
	public static String S_PAYEEOPNBNKNAME ="S_PAYEEOPNBNKNAME";
	/**
	 * column S_IFMATCH
	 */
	public static String S_IFMATCH ="S_IFMATCH";
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
        String[] columnNames = new String[23];        
        columnNames[0]="I_VOUSRLNO";
        columnNames[1]="S_TRECODE";
        columnNames[2]="S_PAYERACCT";
        columnNames[3]="S_PAYEROPNBNKNO";
        columnNames[4]="S_PAYEEACCT";
        columnNames[5]="S_PAYEENAME";
        columnNames[6]="S_PAYEEOPNBNKNO";
        columnNames[7]="C_BDGKIND";
        columnNames[8]="F_AMT";
        columnNames[9]="S_VOUNO";
        columnNames[10]="S_CRPVOUCODE";
        columnNames[11]="D_VOUCHER";
        columnNames[12]="I_OFYEAR";
        columnNames[13]="D_ACCEPT";
        columnNames[14]="D_ACCT";
        columnNames[15]="S_BIZTYPE";
        columnNames[16]="S_PACKAGENO";
        columnNames[17]="S_STATUS";
        columnNames[18]="S_BOOKORGCODE";
        columnNames[19]="S_FILENAME";
        columnNames[20]="TS_SYSUPDATE";
        columnNames[21]="S_PAYEEOPNBNKNAME";
        columnNames[22]="S_IFMATCH";
        return columnNames;     
    }
	/******************************************************
	*
	*  Get Column Name
	*
	*******************************************************/
	/**
	 * Getter I_VOUSRLNO, PK , NOT NULL   	 
	 */
	public static String columnIvousrlno(){
		 return I_VOUSRLNO;
	}
	/**
	 * Getter S_TRECODE , NOT NULL   	 
	 */
	public static String columnStrecode(){
		 return S_TRECODE;
	}
	/**
	 * Getter S_PAYERACCT   	 
	 */
	public static String columnSpayeracct(){
		 return S_PAYERACCT;
	}
	/**
	 * Getter S_PAYEROPNBNKNO   	 
	 */
	public static String columnSpayeropnbnkno(){
		 return S_PAYEROPNBNKNO;
	}
	/**
	 * Getter S_PAYEEACCT   	 
	 */
	public static String columnSpayeeacct(){
		 return S_PAYEEACCT;
	}
	/**
	 * Getter S_PAYEENAME   	 
	 */
	public static String columnSpayeename(){
		 return S_PAYEENAME;
	}
	/**
	 * Getter S_PAYEEOPNBNKNO , NOT NULL   	 
	 */
	public static String columnSpayeeopnbnkno(){
		 return S_PAYEEOPNBNKNO;
	}
	/**
	 * Getter C_BDGKIND , NOT NULL   	 
	 */
	public static String columnCbdgkind(){
		 return C_BDGKIND;
	}
	/**
	 * Getter F_AMT , NOT NULL   	 
	 */
	public static String columnFamt(){
		 return F_AMT;
	}
	/**
	 * Getter S_VOUNO , NOT NULL   	 
	 */
	public static String columnSvouno(){
		 return S_VOUNO;
	}
	/**
	 * Getter S_CRPVOUCODE   	 
	 */
	public static String columnScrpvoucode(){
		 return S_CRPVOUCODE;
	}
	/**
	 * Getter D_VOUCHER , NOT NULL   	 
	 */
	public static String columnDvoucher(){
		 return D_VOUCHER;
	}
	/**
	 * Getter I_OFYEAR , NOT NULL   	 
	 */
	public static String columnIofyear(){
		 return I_OFYEAR;
	}
	/**
	 * Getter D_ACCEPT , NOT NULL   	 
	 */
	public static String columnDaccept(){
		 return D_ACCEPT;
	}
	/**
	 * Getter D_ACCT   	 
	 */
	public static String columnDacct(){
		 return D_ACCT;
	}
	/**
	 * Getter S_BIZTYPE   	 
	 */
	public static String columnSbiztype(){
		 return S_BIZTYPE;
	}
	/**
	 * Getter S_PACKAGENO , NOT NULL   	 
	 */
	public static String columnSpackageno(){
		 return S_PACKAGENO;
	}
	/**
	 * Getter S_STATUS   	 
	 */
	public static String columnSstatus(){
		 return S_STATUS;
	}
	/**
	 * Getter S_BOOKORGCODE , NOT NULL   	 
	 */
	public static String columnSbookorgcode(){
		 return S_BOOKORGCODE;
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 */
	public static String columnSfilename(){
		 return S_FILENAME;
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 */
	public static String columnTssysupdate(){
		 return TS_SYSUPDATE;
	}
	/**
	 * Getter S_PAYEEOPNBNKNAME   	 
	 */
	public static String columnSpayeeopnbnkname(){
		 return S_PAYEEOPNBNKNAME;
	}
	/**
	 * Getter S_IFMATCH   	 
	 */
	public static String columnSifmatch(){
		 return S_IFMATCH;
	}
	/******************************************************
	*
	*  Get Column Java Type
	*
	*******************************************************/
	/**
	 * Getter I_VOUSRLNO, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeIvousrlno(){
		 return "Long";
	}
	/**
	 * Getter S_TRECODE , NOT NULL   	 
	 */
	public static String columnJavaTypeStrecode(){
		 return "String";
	}
	/**
	 * Getter S_PAYERACCT   	 
	 */
	public static String columnJavaTypeSpayeracct(){
		 return "String";
	}
	/**
	 * Getter S_PAYEROPNBNKNO   	 
	 */
	public static String columnJavaTypeSpayeropnbnkno(){
		 return "String";
	}
	/**
	 * Getter S_PAYEEACCT   	 
	 */
	public static String columnJavaTypeSpayeeacct(){
		 return "String";
	}
	/**
	 * Getter S_PAYEENAME   	 
	 */
	public static String columnJavaTypeSpayeename(){
		 return "String";
	}
	/**
	 * Getter S_PAYEEOPNBNKNO , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayeeopnbnkno(){
		 return "String";
	}
	/**
	 * Getter C_BDGKIND , NOT NULL   	 
	 */
	public static String columnJavaTypeCbdgkind(){
		 return "String";
	}
	/**
	 * Getter F_AMT , NOT NULL   	 
	 */
	public static String columnJavaTypeFamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter S_VOUNO , NOT NULL   	 
	 */
	public static String columnJavaTypeSvouno(){
		 return "String";
	}
	/**
	 * Getter S_CRPVOUCODE   	 
	 */
	public static String columnJavaTypeScrpvoucode(){
		 return "String";
	}
	/**
	 * Getter D_VOUCHER , NOT NULL   	 
	 */
	public static String columnJavaTypeDvoucher(){
		 return "Date";
	}
	/**
	 * Getter I_OFYEAR , NOT NULL   	 
	 */
	public static String columnJavaTypeIofyear(){
		 return "Integer";
	}
	/**
	 * Getter D_ACCEPT , NOT NULL   	 
	 */
	public static String columnJavaTypeDaccept(){
		 return "Date";
	}
	/**
	 * Getter D_ACCT   	 
	 */
	public static String columnJavaTypeDacct(){
		 return "Date";
	}
	/**
	 * Getter S_BIZTYPE   	 
	 */
	public static String columnJavaTypeSbiztype(){
		 return "String";
	}
	/**
	 * Getter S_PACKAGENO , NOT NULL   	 
	 */
	public static String columnJavaTypeSpackageno(){
		 return "String";
	}
	/**
	 * Getter S_STATUS   	 
	 */
	public static String columnJavaTypeSstatus(){
		 return "String";
	}
	/**
	 * Getter S_BOOKORGCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSbookorgcode(){
		 return "String";
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSfilename(){
		 return "String";
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 */
	public static String columnJavaTypeTssysupdate(){
		 return "Timestamp";
	}
	/**
	 * Getter S_PAYEEOPNBNKNAME   	 
	 */
	public static String columnJavaTypeSpayeeopnbnkname(){
		 return "String";
	}
	/**
	 * Getter S_IFMATCH   	 
	 */
	public static String columnJavaTypeSifmatch(){
		 return "String";
	}
	/******************************************************
	*
	*  Get Column Database Type
	*
	*******************************************************/
	/**
	 * Getter I_VOUSRLNO, PK , NOT NULL   	 
	 * columnType is BIGINT
	 */
	public static String columnDatabaseTypeIvousrlno(){
		 return "BIGINT";
	}
	/**
	 * Getter S_TRECODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeStrecode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_PAYERACCT   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeracct(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYEROPNBNKNO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeropnbnkno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYEEACCT   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeeacct(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYEENAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeename(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYEEOPNBNKNO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeeopnbnkno(){
		 return "VARCHAR";
	}
	/**
	 * Getter C_BDGKIND , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCbdgkind(){
		 return "CHARACTER";
	}
	/**
	 * Getter F_AMT , NOT NULL   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeFamt(){
		 return "DECIMAL";
	}
	/**
	 * Getter S_VOUNO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSvouno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CRPVOUCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeScrpvoucode(){
		 return "VARCHAR";
	}
	/**
	 * Getter D_VOUCHER , NOT NULL   	 
	 * columnType is DATE
	 */
	public static String columnDatabaseTypeDvoucher(){
		 return "DATE";
	}
	/**
	 * Getter I_OFYEAR , NOT NULL   	 
	 * columnType is INTEGER
	 */
	public static String columnDatabaseTypeIofyear(){
		 return "INTEGER";
	}
	/**
	 * Getter D_ACCEPT , NOT NULL   	 
	 * columnType is DATE
	 */
	public static String columnDatabaseTypeDaccept(){
		 return "DATE";
	}
	/**
	 * Getter D_ACCT   	 
	 * columnType is DATE
	 */
	public static String columnDatabaseTypeDacct(){
		 return "DATE";
	}
	/**
	 * Getter S_BIZTYPE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSbiztype(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PACKAGENO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpackageno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_STATUS   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSstatus(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_BOOKORGCODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSbookorgcode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSfilename(){
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
	 * Getter S_PAYEEOPNBNKNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeeopnbnkname(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_IFMATCH   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSifmatch(){
		 return "CHARACTER";
	}
	/******************************************************
	*
	*  Get Column With
	*
	*******************************************************/
	/**
	 * Getter S_TRECODE , NOT NULL   	 
	 */
	public static int columnWidthStrecode(){
		 return 10;
	}
	/**
	 * Getter S_PAYERACCT   	 
	 */
	public static int columnWidthSpayeracct(){
		 return 32;
	}
	/**
	 * Getter S_PAYEROPNBNKNO   	 
	 */
	public static int columnWidthSpayeropnbnkno(){
		 return 12;
	}
	/**
	 * Getter S_PAYEEACCT   	 
	 */
	public static int columnWidthSpayeeacct(){
		 return 32;
	}
	/**
	 * Getter S_PAYEENAME   	 
	 */
	public static int columnWidthSpayeename(){
		 return 60;
	}
	/**
	 * Getter S_PAYEEOPNBNKNO , NOT NULL   	 
	 */
	public static int columnWidthSpayeeopnbnkno(){
		 return 12;
	}
	/**
	 * Getter C_BDGKIND , NOT NULL   	 
	 */
	public static int columnWidthCbdgkind(){
		 return 1;
	}
	/**
	 * Getter S_VOUNO , NOT NULL   	 
	 */
	public static int columnWidthSvouno(){
		 return 22;
	}
	/**
	 * Getter S_CRPVOUCODE   	 
	 */
	public static int columnWidthScrpvoucode(){
		 return 22;
	}
	/**
	 * Getter S_BIZTYPE   	 
	 */
	public static int columnWidthSbiztype(){
		 return 6;
	}
	/**
	 * Getter S_PACKAGENO , NOT NULL   	 
	 */
	public static int columnWidthSpackageno(){
		 return 8;
	}
	/**
	 * Getter S_STATUS   	 
	 */
	public static int columnWidthSstatus(){
		 return 1;
	}
	/**
	 * Getter S_BOOKORGCODE , NOT NULL   	 
	 */
	public static int columnWidthSbookorgcode(){
		 return 12;
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 */
	public static int columnWidthSfilename(){
		 return 100;
	}
	/**
	 * Getter S_PAYEEOPNBNKNAME   	 
	 */
	public static int columnWidthSpayeeopnbnkname(){
		 return 100;
	}
	/**
	 * Getter S_IFMATCH   	 
	 */
	public static int columnWidthSifmatch(){
		 return 1;
	}
}
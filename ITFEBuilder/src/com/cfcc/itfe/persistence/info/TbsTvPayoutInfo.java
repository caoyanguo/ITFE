      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TBS_TV_PAYOUT</p>
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

public class TbsTvPayoutInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TBS_TV_PAYOUT";
	
	/**
	 * column I_VOUSRLNO
	 */
	public static String I_VOUSRLNO ="I_VOUSRLNO";
	/**
	 * column S_TRECODE
	 */
	public static String S_TRECODE ="S_TRECODE";
	/**
	 * column S_PAYERNAME
	 */
	public static String S_PAYERNAME ="S_PAYERNAME";
	/**
	 * column S_PAYERACCT
	 */
	public static String S_PAYERACCT ="S_PAYERACCT";
	/**
	 * column S_MOVEFUNDREASON
	 */
	public static String S_MOVEFUNDREASON ="S_MOVEFUNDREASON";
	/**
	 * column S_BDGORGCODE
	 */
	public static String S_BDGORGCODE ="S_BDGORGCODE";
	/**
	 * column S_BDGORGNAME
	 */
	public static String S_BDGORGNAME ="S_BDGORGNAME";
	/**
	 * column S_PAYEEACCT
	 */
	public static String S_PAYEEACCT ="S_PAYEEACCT";
	/**
	 * column C_BDGKIND
	 */
	public static String C_BDGKIND ="C_BDGKIND";
	/**
	 * column S_PAYBIZKIND
	 */
	public static String S_PAYBIZKIND ="S_PAYBIZKIND";
	/**
	 * column S_FUNCSBTCODE
	 */
	public static String S_FUNCSBTCODE ="S_FUNCSBTCODE";
	/**
	 * column S_ECOSBTCODE
	 */
	public static String S_ECOSBTCODE ="S_ECOSBTCODE";
	/**
	 * column S_BOOKSBTCODE
	 */
	public static String S_BOOKSBTCODE ="S_BOOKSBTCODE";
	/**
	 * column F_AMT
	 */
	public static String F_AMT ="F_AMT";
	/**
	 * column S_VOUNO
	 */
	public static String S_VOUNO ="S_VOUNO";
	/**
	 * column S_PAYEEBANKNO
	 */
	public static String S_PAYEEBANKNO ="S_PAYEEBANKNO";
	/**
	 * column S_PAYEENAME
	 */
	public static String S_PAYEENAME ="S_PAYEENAME";
	/**
	 * column S_PAYEEOPNBNKNO
	 */
	public static String S_PAYEEOPNBNKNO ="S_PAYEEOPNBNKNO";
	/**
	 * column S_BILLORG
	 */
	public static String S_BILLORG ="S_BILLORG";
	/**
	 * column S_BIZTYPE
	 */
	public static String S_BIZTYPE ="S_BIZTYPE";
	/**
	 * column D_VOUCHER
	 */
	public static String D_VOUCHER ="D_VOUCHER";
	/**
	 * column D_ACCEPT
	 */
	public static String D_ACCEPT ="D_ACCEPT";
	/**
	 * column D_ACCT
	 */
	public static String D_ACCT ="D_ACCT";
	/**
	 * column S_PAYLEVEL
	 */
	public static String S_PAYLEVEL ="S_PAYLEVEL";
	/**
	 * column S_PAYEEADDR
	 */
	public static String S_PAYEEADDR ="S_PAYEEADDR";
	/**
	 * column I_OFYEAR
	 */
	public static String I_OFYEAR ="I_OFYEAR";
	/**
	 * column S_ADDWORD
	 */
	public static String S_ADDWORD ="S_ADDWORD";
	/**
	 * column S_PACKAGENO
	 */
	public static String S_PACKAGENO ="S_PACKAGENO";
	/**
	 * column S_STATUS
	 */
	public static String S_STATUS ="S_STATUS";
	/**
	 * column C_TRIMFLAG
	 */
	public static String C_TRIMFLAG ="C_TRIMFLAG";
	/**
	 * column S_FILENAME
	 */
	public static String S_FILENAME ="S_FILENAME";
	/**
	 * column S_BOOKORGCODE
	 */
	public static String S_BOOKORGCODE ="S_BOOKORGCODE";
	/**
	 * column TS_SYSUPDATE
	 */
	public static String TS_SYSUPDATE ="TS_SYSUPDATE";
	/**
	 * column S_GROUPID
	 */
	public static String S_GROUPID ="S_GROUPID";
	/**
	 * column S_USERCODE
	 */
	public static String S_USERCODE ="S_USERCODE";
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
        String[] columnNames = new String[35];        
        columnNames[0]="I_VOUSRLNO";
        columnNames[1]="S_TRECODE";
        columnNames[2]="S_PAYERNAME";
        columnNames[3]="S_PAYERACCT";
        columnNames[4]="S_MOVEFUNDREASON";
        columnNames[5]="S_BDGORGCODE";
        columnNames[6]="S_BDGORGNAME";
        columnNames[7]="S_PAYEEACCT";
        columnNames[8]="C_BDGKIND";
        columnNames[9]="S_PAYBIZKIND";
        columnNames[10]="S_FUNCSBTCODE";
        columnNames[11]="S_ECOSBTCODE";
        columnNames[12]="S_BOOKSBTCODE";
        columnNames[13]="F_AMT";
        columnNames[14]="S_VOUNO";
        columnNames[15]="S_PAYEEBANKNO";
        columnNames[16]="S_PAYEENAME";
        columnNames[17]="S_PAYEEOPNBNKNO";
        columnNames[18]="S_BILLORG";
        columnNames[19]="S_BIZTYPE";
        columnNames[20]="D_VOUCHER";
        columnNames[21]="D_ACCEPT";
        columnNames[22]="D_ACCT";
        columnNames[23]="S_PAYLEVEL";
        columnNames[24]="S_PAYEEADDR";
        columnNames[25]="I_OFYEAR";
        columnNames[26]="S_ADDWORD";
        columnNames[27]="S_PACKAGENO";
        columnNames[28]="S_STATUS";
        columnNames[29]="C_TRIMFLAG";
        columnNames[30]="S_FILENAME";
        columnNames[31]="S_BOOKORGCODE";
        columnNames[32]="TS_SYSUPDATE";
        columnNames[33]="S_GROUPID";
        columnNames[34]="S_USERCODE";
        return columnNames;     
    }
	/******************************************************
	*
	*  Get Column Name
	*
	*******************************************************/
	/**
	 * Getter I_VOUSRLNO, PK , NOT NULL  , Identity  , Generated 	 
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
	 * Getter S_PAYERNAME , NOT NULL   	 
	 */
	public static String columnSpayername(){
		 return S_PAYERNAME;
	}
	/**
	 * Getter S_PAYERACCT , NOT NULL   	 
	 */
	public static String columnSpayeracct(){
		 return S_PAYERACCT;
	}
	/**
	 * Getter S_MOVEFUNDREASON   	 
	 */
	public static String columnSmovefundreason(){
		 return S_MOVEFUNDREASON;
	}
	/**
	 * Getter S_BDGORGCODE   	 
	 */
	public static String columnSbdgorgcode(){
		 return S_BDGORGCODE;
	}
	/**
	 * Getter S_BDGORGNAME   	 
	 */
	public static String columnSbdgorgname(){
		 return S_BDGORGNAME;
	}
	/**
	 * Getter S_PAYEEACCT , NOT NULL   	 
	 */
	public static String columnSpayeeacct(){
		 return S_PAYEEACCT;
	}
	/**
	 * Getter C_BDGKIND , NOT NULL   	 
	 */
	public static String columnCbdgkind(){
		 return C_BDGKIND;
	}
	/**
	 * Getter S_PAYBIZKIND   	 
	 */
	public static String columnSpaybizkind(){
		 return S_PAYBIZKIND;
	}
	/**
	 * Getter S_FUNCSBTCODE , NOT NULL   	 
	 */
	public static String columnSfuncsbtcode(){
		 return S_FUNCSBTCODE;
	}
	/**
	 * Getter S_ECOSBTCODE   	 
	 */
	public static String columnSecosbtcode(){
		 return S_ECOSBTCODE;
	}
	/**
	 * Getter S_BOOKSBTCODE   	 
	 */
	public static String columnSbooksbtcode(){
		 return S_BOOKSBTCODE;
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
	 * Getter S_PAYEEBANKNO , NOT NULL   	 
	 */
	public static String columnSpayeebankno(){
		 return S_PAYEEBANKNO;
	}
	/**
	 * Getter S_PAYEENAME   	 
	 */
	public static String columnSpayeename(){
		 return S_PAYEENAME;
	}
	/**
	 * Getter S_PAYEEOPNBNKNO   	 
	 */
	public static String columnSpayeeopnbnkno(){
		 return S_PAYEEOPNBNKNO;
	}
	/**
	 * Getter S_BILLORG   	 
	 */
	public static String columnSbillorg(){
		 return S_BILLORG;
	}
	/**
	 * Getter S_BIZTYPE , NOT NULL   	 
	 */
	public static String columnSbiztype(){
		 return S_BIZTYPE;
	}
	/**
	 * Getter D_VOUCHER   	 
	 */
	public static String columnDvoucher(){
		 return D_VOUCHER;
	}
	/**
	 * Getter D_ACCEPT   	 
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
	 * Getter S_PAYLEVEL   	 
	 */
	public static String columnSpaylevel(){
		 return S_PAYLEVEL;
	}
	/**
	 * Getter S_PAYEEADDR   	 
	 */
	public static String columnSpayeeaddr(){
		 return S_PAYEEADDR;
	}
	/**
	 * Getter I_OFYEAR   	 
	 */
	public static String columnIofyear(){
		 return I_OFYEAR;
	}
	/**
	 * Getter S_ADDWORD   	 
	 */
	public static String columnSaddword(){
		 return S_ADDWORD;
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
	 * Getter C_TRIMFLAG , NOT NULL   	 
	 */
	public static String columnCtrimflag(){
		 return C_TRIMFLAG;
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 */
	public static String columnSfilename(){
		 return S_FILENAME;
	}
	/**
	 * Getter S_BOOKORGCODE , NOT NULL   	 
	 */
	public static String columnSbookorgcode(){
		 return S_BOOKORGCODE;
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 */
	public static String columnTssysupdate(){
		 return TS_SYSUPDATE;
	}
	/**
	 * Getter S_GROUPID   	 
	 */
	public static String columnSgroupid(){
		 return S_GROUPID;
	}
	/**
	 * Getter S_USERCODE   	 
	 */
	public static String columnSusercode(){
		 return S_USERCODE;
	}
	/******************************************************
	*
	*  Get Column Java Type
	*
	*******************************************************/
	/**
	 * Getter I_VOUSRLNO, PK , NOT NULL  , Identity  , Generated 	 
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
	 * Getter S_PAYERNAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayername(){
		 return "String";
	}
	/**
	 * Getter S_PAYERACCT , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayeracct(){
		 return "String";
	}
	/**
	 * Getter S_MOVEFUNDREASON   	 
	 */
	public static String columnJavaTypeSmovefundreason(){
		 return "String";
	}
	/**
	 * Getter S_BDGORGCODE   	 
	 */
	public static String columnJavaTypeSbdgorgcode(){
		 return "String";
	}
	/**
	 * Getter S_BDGORGNAME   	 
	 */
	public static String columnJavaTypeSbdgorgname(){
		 return "String";
	}
	/**
	 * Getter S_PAYEEACCT , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayeeacct(){
		 return "String";
	}
	/**
	 * Getter C_BDGKIND , NOT NULL   	 
	 */
	public static String columnJavaTypeCbdgkind(){
		 return "String";
	}
	/**
	 * Getter S_PAYBIZKIND   	 
	 */
	public static String columnJavaTypeSpaybizkind(){
		 return "String";
	}
	/**
	 * Getter S_FUNCSBTCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSfuncsbtcode(){
		 return "String";
	}
	/**
	 * Getter S_ECOSBTCODE   	 
	 */
	public static String columnJavaTypeSecosbtcode(){
		 return "String";
	}
	/**
	 * Getter S_BOOKSBTCODE   	 
	 */
	public static String columnJavaTypeSbooksbtcode(){
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
	 * Getter S_PAYEEBANKNO , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayeebankno(){
		 return "String";
	}
	/**
	 * Getter S_PAYEENAME   	 
	 */
	public static String columnJavaTypeSpayeename(){
		 return "String";
	}
	/**
	 * Getter S_PAYEEOPNBNKNO   	 
	 */
	public static String columnJavaTypeSpayeeopnbnkno(){
		 return "String";
	}
	/**
	 * Getter S_BILLORG   	 
	 */
	public static String columnJavaTypeSbillorg(){
		 return "String";
	}
	/**
	 * Getter S_BIZTYPE , NOT NULL   	 
	 */
	public static String columnJavaTypeSbiztype(){
		 return "String";
	}
	/**
	 * Getter D_VOUCHER   	 
	 */
	public static String columnJavaTypeDvoucher(){
		 return "Date";
	}
	/**
	 * Getter D_ACCEPT   	 
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
	 * Getter S_PAYLEVEL   	 
	 */
	public static String columnJavaTypeSpaylevel(){
		 return "String";
	}
	/**
	 * Getter S_PAYEEADDR   	 
	 */
	public static String columnJavaTypeSpayeeaddr(){
		 return "String";
	}
	/**
	 * Getter I_OFYEAR   	 
	 */
	public static String columnJavaTypeIofyear(){
		 return "Integer";
	}
	/**
	 * Getter S_ADDWORD   	 
	 */
	public static String columnJavaTypeSaddword(){
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
	 * Getter C_TRIMFLAG , NOT NULL   	 
	 */
	public static String columnJavaTypeCtrimflag(){
		 return "String";
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSfilename(){
		 return "String";
	}
	/**
	 * Getter S_BOOKORGCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSbookorgcode(){
		 return "String";
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 */
	public static String columnJavaTypeTssysupdate(){
		 return "Timestamp";
	}
	/**
	 * Getter S_GROUPID   	 
	 */
	public static String columnJavaTypeSgroupid(){
		 return "String";
	}
	/**
	 * Getter S_USERCODE   	 
	 */
	public static String columnJavaTypeSusercode(){
		 return "String";
	}
	/******************************************************
	*
	*  Get Column Database Type
	*
	*******************************************************/
	/**
	 * Getter I_VOUSRLNO, PK , NOT NULL  , Identity  , Generated 	 
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
	 * Getter S_PAYERNAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayername(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYERACCT , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeracct(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_MOVEFUNDREASON   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSmovefundreason(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_BDGORGCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSbdgorgcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_BDGORGNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSbdgorgname(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYEEACCT , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeeacct(){
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
	 * Getter S_PAYBIZKIND   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSpaybizkind(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_FUNCSBTCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSfuncsbtcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ECOSBTCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSecosbtcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_BOOKSBTCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSbooksbtcode(){
		 return "VARCHAR";
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
	 * Getter S_PAYEEBANKNO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeebankno(){
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
	 * Getter S_PAYEEOPNBNKNO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeeopnbnkno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_BILLORG   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSbillorg(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_BIZTYPE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSbiztype(){
		 return "VARCHAR";
	}
	/**
	 * Getter D_VOUCHER   	 
	 * columnType is DATE
	 */
	public static String columnDatabaseTypeDvoucher(){
		 return "DATE";
	}
	/**
	 * Getter D_ACCEPT   	 
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
	 * Getter S_PAYLEVEL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSpaylevel(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_PAYEEADDR   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeeaddr(){
		 return "VARCHAR";
	}
	/**
	 * Getter I_OFYEAR   	 
	 * columnType is INTEGER
	 */
	public static String columnDatabaseTypeIofyear(){
		 return "INTEGER";
	}
	/**
	 * Getter S_ADDWORD   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSaddword(){
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
	 * Getter C_TRIMFLAG , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCtrimflag(){
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
	 * Getter S_BOOKORGCODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSbookorgcode(){
		 return "CHARACTER";
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 * columnType is TIMESTAMP
	 */
	public static String columnDatabaseTypeTssysupdate(){
		 return "TIMESTAMP";
	}
	/**
	 * Getter S_GROUPID   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSgroupid(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_USERCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSusercode(){
		 return "VARCHAR";
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
	 * Getter S_PAYERNAME , NOT NULL   	 
	 */
	public static int columnWidthSpayername(){
		 return 60;
	}
	/**
	 * Getter S_PAYERACCT , NOT NULL   	 
	 */
	public static int columnWidthSpayeracct(){
		 return 32;
	}
	/**
	 * Getter S_MOVEFUNDREASON   	 
	 */
	public static int columnWidthSmovefundreason(){
		 return 20;
	}
	/**
	 * Getter S_BDGORGCODE   	 
	 */
	public static int columnWidthSbdgorgcode(){
		 return 15;
	}
	/**
	 * Getter S_BDGORGNAME   	 
	 */
	public static int columnWidthSbdgorgname(){
		 return 60;
	}
	/**
	 * Getter S_PAYEEACCT , NOT NULL   	 
	 */
	public static int columnWidthSpayeeacct(){
		 return 32;
	}
	/**
	 * Getter C_BDGKIND , NOT NULL   	 
	 */
	public static int columnWidthCbdgkind(){
		 return 1;
	}
	/**
	 * Getter S_PAYBIZKIND   	 
	 */
	public static int columnWidthSpaybizkind(){
		 return 1;
	}
	/**
	 * Getter S_FUNCSBTCODE , NOT NULL   	 
	 */
	public static int columnWidthSfuncsbtcode(){
		 return 30;
	}
	/**
	 * Getter S_ECOSBTCODE   	 
	 */
	public static int columnWidthSecosbtcode(){
		 return 30;
	}
	/**
	 * Getter S_BOOKSBTCODE   	 
	 */
	public static int columnWidthSbooksbtcode(){
		 return 10;
	}
	/**
	 * Getter S_VOUNO , NOT NULL   	 
	 */
	public static int columnWidthSvouno(){
		 return 22;
	}
	/**
	 * Getter S_PAYEEBANKNO , NOT NULL   	 
	 */
	public static int columnWidthSpayeebankno(){
		 return 12;
	}
	/**
	 * Getter S_PAYEENAME   	 
	 */
	public static int columnWidthSpayeename(){
		 return 60;
	}
	/**
	 * Getter S_PAYEEOPNBNKNO   	 
	 */
	public static int columnWidthSpayeeopnbnkno(){
		 return 12;
	}
	/**
	 * Getter S_BILLORG   	 
	 */
	public static int columnWidthSbillorg(){
		 return 20;
	}
	/**
	 * Getter S_BIZTYPE , NOT NULL   	 
	 */
	public static int columnWidthSbiztype(){
		 return 6;
	}
	/**
	 * Getter S_PAYLEVEL   	 
	 */
	public static int columnWidthSpaylevel(){
		 return 1;
	}
	/**
	 * Getter S_PAYEEADDR   	 
	 */
	public static int columnWidthSpayeeaddr(){
		 return 100;
	}
	/**
	 * Getter S_ADDWORD   	 
	 */
	public static int columnWidthSaddword(){
		 return 120;
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
	 * Getter C_TRIMFLAG , NOT NULL   	 
	 */
	public static int columnWidthCtrimflag(){
		 return 1;
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 */
	public static int columnWidthSfilename(){
		 return 100;
	}
	/**
	 * Getter S_BOOKORGCODE , NOT NULL   	 
	 */
	public static int columnWidthSbookorgcode(){
		 return 12;
	}
	/**
	 * Getter S_GROUPID   	 
	 */
	public static int columnWidthSgroupid(){
		 return 8;
	}
	/**
	 * Getter S_USERCODE   	 
	 */
	public static int columnWidthSusercode(){
		 return 30;
	}
}
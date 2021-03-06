      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TV_IN_CORRHANDBOOK</p>
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

public class TvInCorrhandbookInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TV_IN_CORRHANDBOOK";
	
	/**
	 * column I_VOUSRLNO
	 */
	public static String I_VOUSRLNO ="I_VOUSRLNO";
	/**
	 * column S_ELECVOUNO
	 */
	public static String S_ELECVOUNO ="S_ELECVOUNO";
	/**
	 * column S_CORRVOUNO
	 */
	public static String S_CORRVOUNO ="S_CORRVOUNO";
	/**
	 * column D_ACCEPT
	 */
	public static String D_ACCEPT ="D_ACCEPT";
	/**
	 * column D_ACCT
	 */
	public static String D_ACCT ="D_ACCT";
	/**
	 * column D_VOUCHER
	 */
	public static String D_VOUCHER ="D_VOUCHER";
	/**
	 * column S_ORIPAYEETRECODE
	 */
	public static String S_ORIPAYEETRECODE ="S_ORIPAYEETRECODE";
	/**
	 * column S_ORIAIMTRECODE
	 */
	public static String S_ORIAIMTRECODE ="S_ORIAIMTRECODE";
	/**
	 * column S_ORITAXORGCODE
	 */
	public static String S_ORITAXORGCODE ="S_ORITAXORGCODE";
	/**
	 * column S_ORIBDGSBTCODE
	 */
	public static String S_ORIBDGSBTCODE ="S_ORIBDGSBTCODE";
	/**
	 * column C_ORIBDGLEVEL
	 */
	public static String C_ORIBDGLEVEL ="C_ORIBDGLEVEL";
	/**
	 * column C_ORIBDGKIND
	 */
	public static String C_ORIBDGKIND ="C_ORIBDGKIND";
	/**
	 * column S_ORIASTFLAG
	 */
	public static String S_ORIASTFLAG ="S_ORIASTFLAG";
	/**
	 * column F_ORICORRAMT
	 */
	public static String F_ORICORRAMT ="F_ORICORRAMT";
	/**
	 * column S_CURPAYEETRECODE
	 */
	public static String S_CURPAYEETRECODE ="S_CURPAYEETRECODE";
	/**
	 * column S_CURAIMTRECODE
	 */
	public static String S_CURAIMTRECODE ="S_CURAIMTRECODE";
	/**
	 * column S_CURTAXORGCODE
	 */
	public static String S_CURTAXORGCODE ="S_CURTAXORGCODE";
	/**
	 * column S_CURBDGSBTCODE
	 */
	public static String S_CURBDGSBTCODE ="S_CURBDGSBTCODE";
	/**
	 * column C_CURBDGLEVEL
	 */
	public static String C_CURBDGLEVEL ="C_CURBDGLEVEL";
	/**
	 * column C_CURBDGKIND
	 */
	public static String C_CURBDGKIND ="C_CURBDGKIND";
	/**
	 * column S_CURASTFLAG
	 */
	public static String S_CURASTFLAG ="S_CURASTFLAG";
	/**
	 * column F_CURCORRAMT
	 */
	public static String F_CURCORRAMT ="F_CURCORRAMT";
	/**
	 * column S_REASONCODE
	 */
	public static String S_REASONCODE ="S_REASONCODE";
	/**
	 * column C_TRIMFLAG
	 */
	public static String C_TRIMFLAG ="C_TRIMFLAG";
	/**
	 * column S_PACKAGENO
	 */
	public static String S_PACKAGENO ="S_PACKAGENO";
	/**
	 * column S_DEALNO
	 */
	public static String S_DEALNO ="S_DEALNO";
	/**
	 * column S_STATUS
	 */
	public static String S_STATUS ="S_STATUS";
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
	 * column S_TBSORIASTFLAG
	 */
	public static String S_TBSORIASTFLAG ="S_TBSORIASTFLAG";
	/**
	 * column S_TBSCURASTFLAG
	 */
	public static String S_TBSCURASTFLAG ="S_TBSCURASTFLAG";
	/**
	 * column S_DEMO
	 */
	public static String S_DEMO ="S_DEMO";
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
        String[] columnNames = new String[33];        
        columnNames[0]="I_VOUSRLNO";
        columnNames[1]="S_ELECVOUNO";
        columnNames[2]="S_CORRVOUNO";
        columnNames[3]="D_ACCEPT";
        columnNames[4]="D_ACCT";
        columnNames[5]="D_VOUCHER";
        columnNames[6]="S_ORIPAYEETRECODE";
        columnNames[7]="S_ORIAIMTRECODE";
        columnNames[8]="S_ORITAXORGCODE";
        columnNames[9]="S_ORIBDGSBTCODE";
        columnNames[10]="C_ORIBDGLEVEL";
        columnNames[11]="C_ORIBDGKIND";
        columnNames[12]="S_ORIASTFLAG";
        columnNames[13]="F_ORICORRAMT";
        columnNames[14]="S_CURPAYEETRECODE";
        columnNames[15]="S_CURAIMTRECODE";
        columnNames[16]="S_CURTAXORGCODE";
        columnNames[17]="S_CURBDGSBTCODE";
        columnNames[18]="C_CURBDGLEVEL";
        columnNames[19]="C_CURBDGKIND";
        columnNames[20]="S_CURASTFLAG";
        columnNames[21]="F_CURCORRAMT";
        columnNames[22]="S_REASONCODE";
        columnNames[23]="C_TRIMFLAG";
        columnNames[24]="S_PACKAGENO";
        columnNames[25]="S_DEALNO";
        columnNames[26]="S_STATUS";
        columnNames[27]="S_FILENAME";
        columnNames[28]="S_BOOKORGCODE";
        columnNames[29]="TS_SYSUPDATE";
        columnNames[30]="S_TBSORIASTFLAG";
        columnNames[31]="S_TBSCURASTFLAG";
        columnNames[32]="S_DEMO";
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
	 * Getter S_ELECVOUNO , NOT NULL   	 
	 */
	public static String columnSelecvouno(){
		 return S_ELECVOUNO;
	}
	/**
	 * Getter S_CORRVOUNO , NOT NULL   	 
	 */
	public static String columnScorrvouno(){
		 return S_CORRVOUNO;
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
	 * Getter D_VOUCHER   	 
	 */
	public static String columnDvoucher(){
		 return D_VOUCHER;
	}
	/**
	 * Getter S_ORIPAYEETRECODE , NOT NULL   	 
	 */
	public static String columnSoripayeetrecode(){
		 return S_ORIPAYEETRECODE;
	}
	/**
	 * Getter S_ORIAIMTRECODE , NOT NULL   	 
	 */
	public static String columnSoriaimtrecode(){
		 return S_ORIAIMTRECODE;
	}
	/**
	 * Getter S_ORITAXORGCODE , NOT NULL   	 
	 */
	public static String columnSoritaxorgcode(){
		 return S_ORITAXORGCODE;
	}
	/**
	 * Getter S_ORIBDGSBTCODE , NOT NULL   	 
	 */
	public static String columnSoribdgsbtcode(){
		 return S_ORIBDGSBTCODE;
	}
	/**
	 * Getter C_ORIBDGLEVEL , NOT NULL   	 
	 */
	public static String columnCoribdglevel(){
		 return C_ORIBDGLEVEL;
	}
	/**
	 * Getter C_ORIBDGKIND , NOT NULL   	 
	 */
	public static String columnCoribdgkind(){
		 return C_ORIBDGKIND;
	}
	/**
	 * Getter S_ORIASTFLAG   	 
	 */
	public static String columnSoriastflag(){
		 return S_ORIASTFLAG;
	}
	/**
	 * Getter F_ORICORRAMT , NOT NULL   	 
	 */
	public static String columnForicorramt(){
		 return F_ORICORRAMT;
	}
	/**
	 * Getter S_CURPAYEETRECODE , NOT NULL   	 
	 */
	public static String columnScurpayeetrecode(){
		 return S_CURPAYEETRECODE;
	}
	/**
	 * Getter S_CURAIMTRECODE , NOT NULL   	 
	 */
	public static String columnScuraimtrecode(){
		 return S_CURAIMTRECODE;
	}
	/**
	 * Getter S_CURTAXORGCODE , NOT NULL   	 
	 */
	public static String columnScurtaxorgcode(){
		 return S_CURTAXORGCODE;
	}
	/**
	 * Getter S_CURBDGSBTCODE , NOT NULL   	 
	 */
	public static String columnScurbdgsbtcode(){
		 return S_CURBDGSBTCODE;
	}
	/**
	 * Getter C_CURBDGLEVEL , NOT NULL   	 
	 */
	public static String columnCcurbdglevel(){
		 return C_CURBDGLEVEL;
	}
	/**
	 * Getter C_CURBDGKIND , NOT NULL   	 
	 */
	public static String columnCcurbdgkind(){
		 return C_CURBDGKIND;
	}
	/**
	 * Getter S_CURASTFLAG   	 
	 */
	public static String columnScurastflag(){
		 return S_CURASTFLAG;
	}
	/**
	 * Getter F_CURCORRAMT , NOT NULL   	 
	 */
	public static String columnFcurcorramt(){
		 return F_CURCORRAMT;
	}
	/**
	 * Getter S_REASONCODE , NOT NULL   	 
	 */
	public static String columnSreasoncode(){
		 return S_REASONCODE;
	}
	/**
	 * Getter C_TRIMFLAG , NOT NULL   	 
	 */
	public static String columnCtrimflag(){
		 return C_TRIMFLAG;
	}
	/**
	 * Getter S_PACKAGENO , NOT NULL   	 
	 */
	public static String columnSpackageno(){
		 return S_PACKAGENO;
	}
	/**
	 * Getter S_DEALNO   	 
	 */
	public static String columnSdealno(){
		 return S_DEALNO;
	}
	/**
	 * Getter S_STATUS   	 
	 */
	public static String columnSstatus(){
		 return S_STATUS;
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
	 * Getter S_TBSORIASTFLAG   	 
	 */
	public static String columnStbsoriastflag(){
		 return S_TBSORIASTFLAG;
	}
	/**
	 * Getter S_TBSCURASTFLAG   	 
	 */
	public static String columnStbscurastflag(){
		 return S_TBSCURASTFLAG;
	}
	/**
	 * Getter S_DEMO   	 
	 */
	public static String columnSdemo(){
		 return S_DEMO;
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
	 * Getter S_ELECVOUNO , NOT NULL   	 
	 */
	public static String columnJavaTypeSelecvouno(){
		 return "String";
	}
	/**
	 * Getter S_CORRVOUNO , NOT NULL   	 
	 */
	public static String columnJavaTypeScorrvouno(){
		 return "String";
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
	 * Getter D_VOUCHER   	 
	 */
	public static String columnJavaTypeDvoucher(){
		 return "Date";
	}
	/**
	 * Getter S_ORIPAYEETRECODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSoripayeetrecode(){
		 return "String";
	}
	/**
	 * Getter S_ORIAIMTRECODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSoriaimtrecode(){
		 return "String";
	}
	/**
	 * Getter S_ORITAXORGCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSoritaxorgcode(){
		 return "String";
	}
	/**
	 * Getter S_ORIBDGSBTCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSoribdgsbtcode(){
		 return "String";
	}
	/**
	 * Getter C_ORIBDGLEVEL , NOT NULL   	 
	 */
	public static String columnJavaTypeCoribdglevel(){
		 return "String";
	}
	/**
	 * Getter C_ORIBDGKIND , NOT NULL   	 
	 */
	public static String columnJavaTypeCoribdgkind(){
		 return "String";
	}
	/**
	 * Getter S_ORIASTFLAG   	 
	 */
	public static String columnJavaTypeSoriastflag(){
		 return "String";
	}
	/**
	 * Getter F_ORICORRAMT , NOT NULL   	 
	 */
	public static String columnJavaTypeForicorramt(){
		 return "BigDecimal";
	}
	/**
	 * Getter S_CURPAYEETRECODE , NOT NULL   	 
	 */
	public static String columnJavaTypeScurpayeetrecode(){
		 return "String";
	}
	/**
	 * Getter S_CURAIMTRECODE , NOT NULL   	 
	 */
	public static String columnJavaTypeScuraimtrecode(){
		 return "String";
	}
	/**
	 * Getter S_CURTAXORGCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeScurtaxorgcode(){
		 return "String";
	}
	/**
	 * Getter S_CURBDGSBTCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeScurbdgsbtcode(){
		 return "String";
	}
	/**
	 * Getter C_CURBDGLEVEL , NOT NULL   	 
	 */
	public static String columnJavaTypeCcurbdglevel(){
		 return "String";
	}
	/**
	 * Getter C_CURBDGKIND , NOT NULL   	 
	 */
	public static String columnJavaTypeCcurbdgkind(){
		 return "String";
	}
	/**
	 * Getter S_CURASTFLAG   	 
	 */
	public static String columnJavaTypeScurastflag(){
		 return "String";
	}
	/**
	 * Getter F_CURCORRAMT , NOT NULL   	 
	 */
	public static String columnJavaTypeFcurcorramt(){
		 return "BigDecimal";
	}
	/**
	 * Getter S_REASONCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSreasoncode(){
		 return "String";
	}
	/**
	 * Getter C_TRIMFLAG , NOT NULL   	 
	 */
	public static String columnJavaTypeCtrimflag(){
		 return "String";
	}
	/**
	 * Getter S_PACKAGENO , NOT NULL   	 
	 */
	public static String columnJavaTypeSpackageno(){
		 return "String";
	}
	/**
	 * Getter S_DEALNO   	 
	 */
	public static String columnJavaTypeSdealno(){
		 return "String";
	}
	/**
	 * Getter S_STATUS   	 
	 */
	public static String columnJavaTypeSstatus(){
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
	 * Getter S_TBSORIASTFLAG   	 
	 */
	public static String columnJavaTypeStbsoriastflag(){
		 return "String";
	}
	/**
	 * Getter S_TBSCURASTFLAG   	 
	 */
	public static String columnJavaTypeStbscurastflag(){
		 return "String";
	}
	/**
	 * Getter S_DEMO   	 
	 */
	public static String columnJavaTypeSdemo(){
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
	 * Getter S_ELECVOUNO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSelecvouno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CORRVOUNO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeScorrvouno(){
		 return "VARCHAR";
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
	 * Getter D_VOUCHER   	 
	 * columnType is DATE
	 */
	public static String columnDatabaseTypeDvoucher(){
		 return "DATE";
	}
	/**
	 * Getter S_ORIPAYEETRECODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSoripayeetrecode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_ORIAIMTRECODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSoriaimtrecode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_ORITAXORGCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSoritaxorgcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ORIBDGSBTCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSoribdgsbtcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter C_ORIBDGLEVEL , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCoribdglevel(){
		 return "CHARACTER";
	}
	/**
	 * Getter C_ORIBDGKIND , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCoribdgkind(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_ORIASTFLAG   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSoriastflag(){
		 return "VARCHAR";
	}
	/**
	 * Getter F_ORICORRAMT , NOT NULL   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeForicorramt(){
		 return "DECIMAL";
	}
	/**
	 * Getter S_CURPAYEETRECODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeScurpayeetrecode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_CURAIMTRECODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeScuraimtrecode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_CURTAXORGCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeScurtaxorgcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CURBDGSBTCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeScurbdgsbtcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter C_CURBDGLEVEL , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCcurbdglevel(){
		 return "CHARACTER";
	}
	/**
	 * Getter C_CURBDGKIND , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCcurbdgkind(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_CURASTFLAG   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeScurastflag(){
		 return "VARCHAR";
	}
	/**
	 * Getter F_CURCORRAMT , NOT NULL   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeFcurcorramt(){
		 return "DECIMAL";
	}
	/**
	 * Getter S_REASONCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSreasoncode(){
		 return "VARCHAR";
	}
	/**
	 * Getter C_TRIMFLAG , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCtrimflag(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_PACKAGENO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpackageno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_DEALNO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSdealno(){
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
	 * Getter S_TBSORIASTFLAG   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStbsoriastflag(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_TBSCURASTFLAG   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStbscurastflag(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_DEMO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSdemo(){
		 return "VARCHAR";
	}
	/******************************************************
	*
	*  Get Column With
	*
	*******************************************************/
	/**
	 * Getter S_ELECVOUNO , NOT NULL   	 
	 */
	public static int columnWidthSelecvouno(){
		 return 20;
	}
	/**
	 * Getter S_CORRVOUNO , NOT NULL   	 
	 */
	public static int columnWidthScorrvouno(){
		 return 20;
	}
	/**
	 * Getter S_ORIPAYEETRECODE , NOT NULL   	 
	 */
	public static int columnWidthSoripayeetrecode(){
		 return 10;
	}
	/**
	 * Getter S_ORIAIMTRECODE , NOT NULL   	 
	 */
	public static int columnWidthSoriaimtrecode(){
		 return 10;
	}
	/**
	 * Getter S_ORITAXORGCODE , NOT NULL   	 
	 */
	public static int columnWidthSoritaxorgcode(){
		 return 20;
	}
	/**
	 * Getter S_ORIBDGSBTCODE , NOT NULL   	 
	 */
	public static int columnWidthSoribdgsbtcode(){
		 return 30;
	}
	/**
	 * Getter C_ORIBDGLEVEL , NOT NULL   	 
	 */
	public static int columnWidthCoribdglevel(){
		 return 1;
	}
	/**
	 * Getter C_ORIBDGKIND , NOT NULL   	 
	 */
	public static int columnWidthCoribdgkind(){
		 return 1;
	}
	/**
	 * Getter S_ORIASTFLAG   	 
	 */
	public static int columnWidthSoriastflag(){
		 return 35;
	}
	/**
	 * Getter S_CURPAYEETRECODE , NOT NULL   	 
	 */
	public static int columnWidthScurpayeetrecode(){
		 return 10;
	}
	/**
	 * Getter S_CURAIMTRECODE , NOT NULL   	 
	 */
	public static int columnWidthScuraimtrecode(){
		 return 10;
	}
	/**
	 * Getter S_CURTAXORGCODE , NOT NULL   	 
	 */
	public static int columnWidthScurtaxorgcode(){
		 return 20;
	}
	/**
	 * Getter S_CURBDGSBTCODE , NOT NULL   	 
	 */
	public static int columnWidthScurbdgsbtcode(){
		 return 30;
	}
	/**
	 * Getter C_CURBDGLEVEL , NOT NULL   	 
	 */
	public static int columnWidthCcurbdglevel(){
		 return 1;
	}
	/**
	 * Getter C_CURBDGKIND , NOT NULL   	 
	 */
	public static int columnWidthCcurbdgkind(){
		 return 1;
	}
	/**
	 * Getter S_CURASTFLAG   	 
	 */
	public static int columnWidthScurastflag(){
		 return 35;
	}
	/**
	 * Getter S_REASONCODE , NOT NULL   	 
	 */
	public static int columnWidthSreasoncode(){
		 return 4;
	}
	/**
	 * Getter C_TRIMFLAG , NOT NULL   	 
	 */
	public static int columnWidthCtrimflag(){
		 return 1;
	}
	/**
	 * Getter S_PACKAGENO , NOT NULL   	 
	 */
	public static int columnWidthSpackageno(){
		 return 8;
	}
	/**
	 * Getter S_DEALNO   	 
	 */
	public static int columnWidthSdealno(){
		 return 8;
	}
	/**
	 * Getter S_STATUS   	 
	 */
	public static int columnWidthSstatus(){
		 return 5;
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
	 * Getter S_TBSORIASTFLAG   	 
	 */
	public static int columnWidthStbsoriastflag(){
		 return 35;
	}
	/**
	 * Getter S_TBSCURASTFLAG   	 
	 */
	public static int columnWidthStbscurastflag(){
		 return 35;
	}
	/**
	 * Getter S_DEMO   	 
	 */
	public static int columnWidthSdemo(){
		 return 120;
	}
}
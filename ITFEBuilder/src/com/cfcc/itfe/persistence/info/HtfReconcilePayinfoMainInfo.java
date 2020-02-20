      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: HTF_RECONCILE_PAYINFO_MAIN</p>
 * <p>Description:  Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:54 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class HtfReconcilePayinfoMainInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "HTF_RECONCILE_PAYINFO_MAIN";
	
	/**
	 * column I_VOUSRLNO
	 */
	public static String I_VOUSRLNO ="I_VOUSRLNO";
	/**
	 * column S_ORGCODE
	 */
	public static String S_ORGCODE ="S_ORGCODE";
	/**
	 * column S_TRECODE
	 */
	public static String S_TRECODE ="S_TRECODE";
	/**
	 * column S_STATUS
	 */
	public static String S_STATUS ="S_STATUS";
	/**
	 * column S_DEMO
	 */
	public static String S_DEMO ="S_DEMO";
	/**
	 * column TS_SYSUPDATE
	 */
	public static String TS_SYSUPDATE ="TS_SYSUPDATE";
	/**
	 * column S_PACKAGENO
	 */
	public static String S_PACKAGENO ="S_PACKAGENO";
	/**
	 * column S_ADMDIVCODE
	 */
	public static String S_ADMDIVCODE ="S_ADMDIVCODE";
	/**
	 * column S_STYEAR
	 */
	public static String S_STYEAR ="S_STYEAR";
	/**
	 * column S_VTCODE
	 */
	public static String S_VTCODE ="S_VTCODE";
	/**
	 * column S_VOUDATE
	 */
	public static String S_VOUDATE ="S_VOUDATE";
	/**
	 * column S_VOUCHERNO
	 */
	public static String S_VOUCHERNO ="S_VOUCHERNO";
	/**
	 * column S_VOUCHERCHECKNO
	 */
	public static String S_VOUCHERCHECKNO ="S_VOUCHERCHECKNO";
	/**
	 * column S_CHILDPACKNUM
	 */
	public static String S_CHILDPACKNUM ="S_CHILDPACKNUM";
	/**
	 * column S_CURPACKNO
	 */
	public static String S_CURPACKNO ="S_CURPACKNO";
	/**
	 * column S_CLEARBANKCODE
	 */
	public static String S_CLEARBANKCODE ="S_CLEARBANKCODE";
	/**
	 * column S_CLEARBANKNAME
	 */
	public static String S_CLEARBANKNAME ="S_CLEARBANKNAME";
	/**
	 * column S_CLEARACCNO
	 */
	public static String S_CLEARACCNO ="S_CLEARACCNO";
	/**
	 * column S_CLEARACCNANME
	 */
	public static String S_CLEARACCNANME ="S_CLEARACCNANME";
	/**
	 * column S_BEGINDATE
	 */
	public static String S_BEGINDATE ="S_BEGINDATE";
	/**
	 * column S_ENDDATE
	 */
	public static String S_ENDDATE ="S_ENDDATE";
	/**
	 * column S_ALLNUM
	 */
	public static String S_ALLNUM ="S_ALLNUM";
	/**
	 * column N_ALLAMT
	 */
	public static String N_ALLAMT ="N_ALLAMT";
	/**
	 * column S_XCHECKRESULT
	 */
	public static String S_XCHECKRESULT ="S_XCHECKRESULT";
	/**
	 * column S_XDIFFNUM
	 */
	public static String S_XDIFFNUM ="S_XDIFFNUM";
	/**
	 * column S_HOLD1
	 */
	public static String S_HOLD1 ="S_HOLD1";
	/**
	 * column S_HOLD2
	 */
	public static String S_HOLD2 ="S_HOLD2";
	/**
	 * column S_EXT1
	 */
	public static String S_EXT1 ="S_EXT1";
	/**
	 * column S_EXT2
	 */
	public static String S_EXT2 ="S_EXT2";
	/**
	 * column S_EXT3
	 */
	public static String S_EXT3 ="S_EXT3";
	/**
	 * column S_EXT4
	 */
	public static String S_EXT4 ="S_EXT4";
	/**
	 * column S_EXT5
	 */
	public static String S_EXT5 ="S_EXT5";
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
        String[] columnNames = new String[32];        
        columnNames[0]="I_VOUSRLNO";
        columnNames[1]="S_ORGCODE";
        columnNames[2]="S_TRECODE";
        columnNames[3]="S_STATUS";
        columnNames[4]="S_DEMO";
        columnNames[5]="TS_SYSUPDATE";
        columnNames[6]="S_PACKAGENO";
        columnNames[7]="S_ADMDIVCODE";
        columnNames[8]="S_STYEAR";
        columnNames[9]="S_VTCODE";
        columnNames[10]="S_VOUDATE";
        columnNames[11]="S_VOUCHERNO";
        columnNames[12]="S_VOUCHERCHECKNO";
        columnNames[13]="S_CHILDPACKNUM";
        columnNames[14]="S_CURPACKNO";
        columnNames[15]="S_CLEARBANKCODE";
        columnNames[16]="S_CLEARBANKNAME";
        columnNames[17]="S_CLEARACCNO";
        columnNames[18]="S_CLEARACCNANME";
        columnNames[19]="S_BEGINDATE";
        columnNames[20]="S_ENDDATE";
        columnNames[21]="S_ALLNUM";
        columnNames[22]="N_ALLAMT";
        columnNames[23]="S_XCHECKRESULT";
        columnNames[24]="S_XDIFFNUM";
        columnNames[25]="S_HOLD1";
        columnNames[26]="S_HOLD2";
        columnNames[27]="S_EXT1";
        columnNames[28]="S_EXT2";
        columnNames[29]="S_EXT3";
        columnNames[30]="S_EXT4";
        columnNames[31]="S_EXT5";
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
	 * Getter TS_SYSUPDATE   	 
	 */
	public static String columnTssysupdate(){
		 return TS_SYSUPDATE;
	}
	/**
	 * Getter S_PACKAGENO   	 
	 */
	public static String columnSpackageno(){
		 return S_PACKAGENO;
	}
	/**
	 * Getter S_ADMDIVCODE , NOT NULL   	 
	 */
	public static String columnSadmdivcode(){
		 return S_ADMDIVCODE;
	}
	/**
	 * Getter S_STYEAR , NOT NULL   	 
	 */
	public static String columnSstyear(){
		 return S_STYEAR;
	}
	/**
	 * Getter S_VTCODE , NOT NULL   	 
	 */
	public static String columnSvtcode(){
		 return S_VTCODE;
	}
	/**
	 * Getter S_VOUDATE , NOT NULL   	 
	 */
	public static String columnSvoudate(){
		 return S_VOUDATE;
	}
	/**
	 * Getter S_VOUCHERNO , NOT NULL   	 
	 */
	public static String columnSvoucherno(){
		 return S_VOUCHERNO;
	}
	/**
	 * Getter S_VOUCHERCHECKNO , NOT NULL   	 
	 */
	public static String columnSvouchercheckno(){
		 return S_VOUCHERCHECKNO;
	}
	/**
	 * Getter S_CHILDPACKNUM , NOT NULL   	 
	 */
	public static String columnSchildpacknum(){
		 return S_CHILDPACKNUM;
	}
	/**
	 * Getter S_CURPACKNO , NOT NULL   	 
	 */
	public static String columnScurpackno(){
		 return S_CURPACKNO;
	}
	/**
	 * Getter S_CLEARBANKCODE   	 
	 */
	public static String columnSclearbankcode(){
		 return S_CLEARBANKCODE;
	}
	/**
	 * Getter S_CLEARBANKNAME   	 
	 */
	public static String columnSclearbankname(){
		 return S_CLEARBANKNAME;
	}
	/**
	 * Getter S_CLEARACCNO   	 
	 */
	public static String columnSclearaccno(){
		 return S_CLEARACCNO;
	}
	/**
	 * Getter S_CLEARACCNANME   	 
	 */
	public static String columnSclearaccnanme(){
		 return S_CLEARACCNANME;
	}
	/**
	 * Getter S_BEGINDATE , NOT NULL   	 
	 */
	public static String columnSbegindate(){
		 return S_BEGINDATE;
	}
	/**
	 * Getter S_ENDDATE , NOT NULL   	 
	 */
	public static String columnSenddate(){
		 return S_ENDDATE;
	}
	/**
	 * Getter S_ALLNUM , NOT NULL   	 
	 */
	public static String columnSallnum(){
		 return S_ALLNUM;
	}
	/**
	 * Getter N_ALLAMT , NOT NULL   	 
	 */
	public static String columnNallamt(){
		 return N_ALLAMT;
	}
	/**
	 * Getter S_XCHECKRESULT   	 
	 */
	public static String columnSxcheckresult(){
		 return S_XCHECKRESULT;
	}
	/**
	 * Getter S_XDIFFNUM   	 
	 */
	public static String columnSxdiffnum(){
		 return S_XDIFFNUM;
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
	 * Getter S_EXT1   	 
	 */
	public static String columnSext1(){
		 return S_EXT1;
	}
	/**
	 * Getter S_EXT2   	 
	 */
	public static String columnSext2(){
		 return S_EXT2;
	}
	/**
	 * Getter S_EXT3   	 
	 */
	public static String columnSext3(){
		 return S_EXT3;
	}
	/**
	 * Getter S_EXT4   	 
	 */
	public static String columnSext4(){
		 return S_EXT4;
	}
	/**
	 * Getter S_EXT5   	 
	 */
	public static String columnSext5(){
		 return S_EXT5;
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
	 * Getter TS_SYSUPDATE   	 
	 */
	public static String columnJavaTypeTssysupdate(){
		 return "Timestamp";
	}
	/**
	 * Getter S_PACKAGENO   	 
	 */
	public static String columnJavaTypeSpackageno(){
		 return "String";
	}
	/**
	 * Getter S_ADMDIVCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSadmdivcode(){
		 return "String";
	}
	/**
	 * Getter S_STYEAR , NOT NULL   	 
	 */
	public static String columnJavaTypeSstyear(){
		 return "String";
	}
	/**
	 * Getter S_VTCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSvtcode(){
		 return "String";
	}
	/**
	 * Getter S_VOUDATE , NOT NULL   	 
	 */
	public static String columnJavaTypeSvoudate(){
		 return "String";
	}
	/**
	 * Getter S_VOUCHERNO , NOT NULL   	 
	 */
	public static String columnJavaTypeSvoucherno(){
		 return "String";
	}
	/**
	 * Getter S_VOUCHERCHECKNO , NOT NULL   	 
	 */
	public static String columnJavaTypeSvouchercheckno(){
		 return "String";
	}
	/**
	 * Getter S_CHILDPACKNUM , NOT NULL   	 
	 */
	public static String columnJavaTypeSchildpacknum(){
		 return "String";
	}
	/**
	 * Getter S_CURPACKNO , NOT NULL   	 
	 */
	public static String columnJavaTypeScurpackno(){
		 return "String";
	}
	/**
	 * Getter S_CLEARBANKCODE   	 
	 */
	public static String columnJavaTypeSclearbankcode(){
		 return "String";
	}
	/**
	 * Getter S_CLEARBANKNAME   	 
	 */
	public static String columnJavaTypeSclearbankname(){
		 return "String";
	}
	/**
	 * Getter S_CLEARACCNO   	 
	 */
	public static String columnJavaTypeSclearaccno(){
		 return "String";
	}
	/**
	 * Getter S_CLEARACCNANME   	 
	 */
	public static String columnJavaTypeSclearaccnanme(){
		 return "String";
	}
	/**
	 * Getter S_BEGINDATE , NOT NULL   	 
	 */
	public static String columnJavaTypeSbegindate(){
		 return "String";
	}
	/**
	 * Getter S_ENDDATE , NOT NULL   	 
	 */
	public static String columnJavaTypeSenddate(){
		 return "String";
	}
	/**
	 * Getter S_ALLNUM , NOT NULL   	 
	 */
	public static String columnJavaTypeSallnum(){
		 return "String";
	}
	/**
	 * Getter N_ALLAMT , NOT NULL   	 
	 */
	public static String columnJavaTypeNallamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter S_XCHECKRESULT   	 
	 */
	public static String columnJavaTypeSxcheckresult(){
		 return "String";
	}
	/**
	 * Getter S_XDIFFNUM   	 
	 */
	public static String columnJavaTypeSxdiffnum(){
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
	 * Getter S_EXT1   	 
	 */
	public static String columnJavaTypeSext1(){
		 return "String";
	}
	/**
	 * Getter S_EXT2   	 
	 */
	public static String columnJavaTypeSext2(){
		 return "String";
	}
	/**
	 * Getter S_EXT3   	 
	 */
	public static String columnJavaTypeSext3(){
		 return "String";
	}
	/**
	 * Getter S_EXT4   	 
	 */
	public static String columnJavaTypeSext4(){
		 return "String";
	}
	/**
	 * Getter S_EXT5   	 
	 */
	public static String columnJavaTypeSext5(){
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
	 * Getter TS_SYSUPDATE   	 
	 * columnType is TIMESTAMP
	 */
	public static String columnDatabaseTypeTssysupdate(){
		 return "TIMESTAMP";
	}
	/**
	 * Getter S_PACKAGENO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpackageno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ADMDIVCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSadmdivcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_STYEAR , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSstyear(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_VTCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSvtcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_VOUDATE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSvoudate(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_VOUCHERNO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSvoucherno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_VOUCHERCHECKNO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSvouchercheckno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CHILDPACKNUM , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSchildpacknum(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CURPACKNO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeScurpackno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CLEARBANKCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSclearbankcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CLEARBANKNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSclearbankname(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CLEARACCNO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSclearaccno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CLEARACCNANME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSclearaccnanme(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_BEGINDATE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSbegindate(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ENDDATE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSenddate(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ALLNUM , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSallnum(){
		 return "VARCHAR";
	}
	/**
	 * Getter N_ALLAMT , NOT NULL   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNallamt(){
		 return "DECIMAL";
	}
	/**
	 * Getter S_XCHECKRESULT   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSxcheckresult(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_XDIFFNUM   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSxdiffnum(){
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
	 * Getter S_EXT1   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSext1(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_EXT2   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSext2(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_EXT3   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSext3(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_EXT4   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSext4(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_EXT5   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSext5(){
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
	 * Getter S_TRECODE , NOT NULL   	 
	 */
	public static int columnWidthStrecode(){
		 return 10;
	}
	/**
	 * Getter S_STATUS , NOT NULL   	 
	 */
	public static int columnWidthSstatus(){
		 return 5;
	}
	/**
	 * Getter S_DEMO   	 
	 */
	public static int columnWidthSdemo(){
		 return 100;
	}
	/**
	 * Getter S_PACKAGENO   	 
	 */
	public static int columnWidthSpackageno(){
		 return 8;
	}
	/**
	 * Getter S_ADMDIVCODE , NOT NULL   	 
	 */
	public static int columnWidthSadmdivcode(){
		 return 9;
	}
	/**
	 * Getter S_STYEAR , NOT NULL   	 
	 */
	public static int columnWidthSstyear(){
		 return 4;
	}
	/**
	 * Getter S_VTCODE , NOT NULL   	 
	 */
	public static int columnWidthSvtcode(){
		 return 4;
	}
	/**
	 * Getter S_VOUDATE , NOT NULL   	 
	 */
	public static int columnWidthSvoudate(){
		 return 8;
	}
	/**
	 * Getter S_VOUCHERNO , NOT NULL   	 
	 */
	public static int columnWidthSvoucherno(){
		 return 42;
	}
	/**
	 * Getter S_VOUCHERCHECKNO , NOT NULL   	 
	 */
	public static int columnWidthSvouchercheckno(){
		 return 42;
	}
	/**
	 * Getter S_CHILDPACKNUM , NOT NULL   	 
	 */
	public static int columnWidthSchildpacknum(){
		 return 10;
	}
	/**
	 * Getter S_CURPACKNO , NOT NULL   	 
	 */
	public static int columnWidthScurpackno(){
		 return 10;
	}
	/**
	 * Getter S_CLEARBANKCODE   	 
	 */
	public static int columnWidthSclearbankcode(){
		 return 42;
	}
	/**
	 * Getter S_CLEARBANKNAME   	 
	 */
	public static int columnWidthSclearbankname(){
		 return 60;
	}
	/**
	 * Getter S_CLEARACCNO   	 
	 */
	public static int columnWidthSclearaccno(){
		 return 42;
	}
	/**
	 * Getter S_CLEARACCNANME   	 
	 */
	public static int columnWidthSclearaccnanme(){
		 return 120;
	}
	/**
	 * Getter S_BEGINDATE , NOT NULL   	 
	 */
	public static int columnWidthSbegindate(){
		 return 8;
	}
	/**
	 * Getter S_ENDDATE , NOT NULL   	 
	 */
	public static int columnWidthSenddate(){
		 return 8;
	}
	/**
	 * Getter S_ALLNUM , NOT NULL   	 
	 */
	public static int columnWidthSallnum(){
		 return 10;
	}
	/**
	 * Getter S_XCHECKRESULT   	 
	 */
	public static int columnWidthSxcheckresult(){
		 return 5;
	}
	/**
	 * Getter S_XDIFFNUM   	 
	 */
	public static int columnWidthSxdiffnum(){
		 return 10;
	}
	/**
	 * Getter S_HOLD1   	 
	 */
	public static int columnWidthShold1(){
		 return 42;
	}
	/**
	 * Getter S_HOLD2   	 
	 */
	public static int columnWidthShold2(){
		 return 42;
	}
	/**
	 * Getter S_EXT1   	 
	 */
	public static int columnWidthSext1(){
		 return 50;
	}
	/**
	 * Getter S_EXT2   	 
	 */
	public static int columnWidthSext2(){
		 return 50;
	}
	/**
	 * Getter S_EXT3   	 
	 */
	public static int columnWidthSext3(){
		 return 50;
	}
	/**
	 * Getter S_EXT4   	 
	 */
	public static int columnWidthSext4(){
		 return 50;
	}
	/**
	 * Getter S_EXT5   	 
	 */
	public static int columnWidthSext5(){
		 return 50;
	}
}
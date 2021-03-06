      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TF_UNITRECORDMAIN</p>
 * <p>Description: 单位清册5951主表 Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:59 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class TfUnitrecordmainInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TF_UNITRECORDMAIN";
	
	/**
	 * column I_VOUSRLNO
	 */
	public static String I_VOUSRLNO ="I_VOUSRLNO";
	/**
	 * column S_ORGCODE
	 */
	public static String S_ORGCODE ="S_ORGCODE";
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
	 * column S_TRECODE
	 */
	public static String S_TRECODE ="S_TRECODE";
	/**
	 * column S_FINORGCODE
	 */
	public static String S_FINORGCODE ="S_FINORGCODE";
	/**
	 * column S_PAYBANKCODE
	 */
	public static String S_PAYBANKCODE ="S_PAYBANKCODE";
	/**
	 * column S_PAYBANKNAME
	 */
	public static String S_PAYBANKNAME ="S_PAYBANKNAME";
	/**
	 * column S_ALLFLAG
	 */
	public static String S_ALLFLAG ="S_ALLFLAG";
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
        columnNames[0]="I_VOUSRLNO";
        columnNames[1]="S_ORGCODE";
        columnNames[2]="S_STATUS";
        columnNames[3]="S_DEMO";
        columnNames[4]="TS_SYSUPDATE";
        columnNames[5]="S_ADMDIVCODE";
        columnNames[6]="S_STYEAR";
        columnNames[7]="S_VTCODE";
        columnNames[8]="S_VOUDATE";
        columnNames[9]="S_VOUCHERNO";
        columnNames[10]="S_TRECODE";
        columnNames[11]="S_FINORGCODE";
        columnNames[12]="S_PAYBANKCODE";
        columnNames[13]="S_PAYBANKNAME";
        columnNames[14]="S_ALLFLAG";
        columnNames[15]="S_HOLD1";
        columnNames[16]="S_HOLD2";
        columnNames[17]="S_EXT1";
        columnNames[18]="S_EXT2";
        columnNames[19]="S_EXT3";
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
	 * Getter S_STATUS   	 
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
	 *行政区划代码 Getter S_ADMDIVCODE , NOT NULL   	 
	 */
	public static String columnSadmdivcode(){
		 return S_ADMDIVCODE;
	}
	/**
	 *业务年度 Getter S_STYEAR , NOT NULL   	 
	 */
	public static String columnSstyear(){
		 return S_STYEAR;
	}
	/**
	 *凭证类型编号 Getter S_VTCODE , NOT NULL   	 
	 */
	public static String columnSvtcode(){
		 return S_VTCODE;
	}
	/**
	 *凭证日期 Getter S_VOUDATE , NOT NULL   	 
	 */
	public static String columnSvoudate(){
		 return S_VOUDATE;
	}
	/**
	 *凭证号 Getter S_VOUCHERNO , NOT NULL   	 
	 */
	public static String columnSvoucherno(){
		 return S_VOUCHERNO;
	}
	/**
	 *国库主体代码 Getter S_TRECODE , NOT NULL   	 
	 */
	public static String columnStrecode(){
		 return S_TRECODE;
	}
	/**
	 *财政机关代码 Getter S_FINORGCODE , NOT NULL   	 
	 */
	public static String columnSfinorgcode(){
		 return S_FINORGCODE;
	}
	/**
	 *代理银行编码 Getter S_PAYBANKCODE , NOT NULL   	 
	 */
	public static String columnSpaybankcode(){
		 return S_PAYBANKCODE;
	}
	/**
	 *代理银行名称 Getter S_PAYBANKNAME , NOT NULL   	 
	 */
	public static String columnSpaybankname(){
		 return S_PAYBANKNAME;
	}
	/**
	 *全量标志 Getter S_ALLFLAG , NOT NULL   	 
	 */
	public static String columnSallflag(){
		 return S_ALLFLAG;
	}
	/**
	 *预留字段1 Getter S_HOLD1   	 
	 */
	public static String columnShold1(){
		 return S_HOLD1;
	}
	/**
	 *预留字段2 Getter S_HOLD2   	 
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
	 * Getter S_STATUS   	 
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
	 *行政区划代码 Getter S_ADMDIVCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSadmdivcode(){
		 return "String";
	}
	/**
	 *业务年度 Getter S_STYEAR , NOT NULL   	 
	 */
	public static String columnJavaTypeSstyear(){
		 return "String";
	}
	/**
	 *凭证类型编号 Getter S_VTCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSvtcode(){
		 return "String";
	}
	/**
	 *凭证日期 Getter S_VOUDATE , NOT NULL   	 
	 */
	public static String columnJavaTypeSvoudate(){
		 return "String";
	}
	/**
	 *凭证号 Getter S_VOUCHERNO , NOT NULL   	 
	 */
	public static String columnJavaTypeSvoucherno(){
		 return "String";
	}
	/**
	 *国库主体代码 Getter S_TRECODE , NOT NULL   	 
	 */
	public static String columnJavaTypeStrecode(){
		 return "String";
	}
	/**
	 *财政机关代码 Getter S_FINORGCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSfinorgcode(){
		 return "String";
	}
	/**
	 *代理银行编码 Getter S_PAYBANKCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSpaybankcode(){
		 return "String";
	}
	/**
	 *代理银行名称 Getter S_PAYBANKNAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSpaybankname(){
		 return "String";
	}
	/**
	 *全量标志 Getter S_ALLFLAG , NOT NULL   	 
	 */
	public static String columnJavaTypeSallflag(){
		 return "String";
	}
	/**
	 *预留字段1 Getter S_HOLD1   	 
	 */
	public static String columnJavaTypeShold1(){
		 return "String";
	}
	/**
	 *预留字段2 Getter S_HOLD2   	 
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
	 * Getter S_STATUS   	 
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
	 *行政区划代码 Getter S_ADMDIVCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSadmdivcode(){
		 return "VARCHAR";
	}
	/**
	 *业务年度 Getter S_STYEAR , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSstyear(){
		 return "VARCHAR";
	}
	/**
	 *凭证类型编号 Getter S_VTCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSvtcode(){
		 return "VARCHAR";
	}
	/**
	 *凭证日期 Getter S_VOUDATE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSvoudate(){
		 return "VARCHAR";
	}
	/**
	 *凭证号 Getter S_VOUCHERNO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSvoucherno(){
		 return "VARCHAR";
	}
	/**
	 *国库主体代码 Getter S_TRECODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStrecode(){
		 return "VARCHAR";
	}
	/**
	 *财政机关代码 Getter S_FINORGCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSfinorgcode(){
		 return "VARCHAR";
	}
	/**
	 *代理银行编码 Getter S_PAYBANKCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpaybankcode(){
		 return "VARCHAR";
	}
	/**
	 *代理银行名称 Getter S_PAYBANKNAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpaybankname(){
		 return "VARCHAR";
	}
	/**
	 *全量标志 Getter S_ALLFLAG , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSallflag(){
		 return "VARCHAR";
	}
	/**
	 *预留字段1 Getter S_HOLD1   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeShold1(){
		 return "VARCHAR";
	}
	/**
	 *预留字段2 Getter S_HOLD2   	 
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
	 * Getter S_STATUS   	 
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
	 *行政区划代码 Getter S_ADMDIVCODE , NOT NULL   	 
	 */
	public static int columnWidthSadmdivcode(){
		 return 9;
	}
	/**
	 *业务年度 Getter S_STYEAR , NOT NULL   	 
	 */
	public static int columnWidthSstyear(){
		 return 4;
	}
	/**
	 *凭证类型编号 Getter S_VTCODE , NOT NULL   	 
	 */
	public static int columnWidthSvtcode(){
		 return 4;
	}
	/**
	 *凭证日期 Getter S_VOUDATE , NOT NULL   	 
	 */
	public static int columnWidthSvoudate(){
		 return 8;
	}
	/**
	 *凭证号 Getter S_VOUCHERNO , NOT NULL   	 
	 */
	public static int columnWidthSvoucherno(){
		 return 42;
	}
	/**
	 *国库主体代码 Getter S_TRECODE , NOT NULL   	 
	 */
	public static int columnWidthStrecode(){
		 return 10;
	}
	/**
	 *财政机关代码 Getter S_FINORGCODE , NOT NULL   	 
	 */
	public static int columnWidthSfinorgcode(){
		 return 12;
	}
	/**
	 *代理银行编码 Getter S_PAYBANKCODE , NOT NULL   	 
	 */
	public static int columnWidthSpaybankcode(){
		 return 42;
	}
	/**
	 *代理银行名称 Getter S_PAYBANKNAME , NOT NULL   	 
	 */
	public static int columnWidthSpaybankname(){
		 return 60;
	}
	/**
	 *全量标志 Getter S_ALLFLAG , NOT NULL   	 
	 */
	public static int columnWidthSallflag(){
		 return 5;
	}
	/**
	 *预留字段1 Getter S_HOLD1   	 
	 */
	public static int columnWidthShold1(){
		 return 42;
	}
	/**
	 *预留字段2 Getter S_HOLD2   	 
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
}
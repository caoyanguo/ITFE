      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TF_INTEREST_PARAM</p>
 * <p>Description: 计息统计信息表 Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:58 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class TfInterestParamInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TF_INTEREST_PARAM";
	
	/**
	 * column I_VOUSRLNO
	 */
	public static String I_VOUSRLNO ="I_VOUSRLNO";
	/**
	 * column S_ORGCODE
	 */
	public static String S_ORGCODE ="S_ORGCODE";
	/**
	 * column S_QUARTER
	 */
	public static String S_QUARTER ="S_QUARTER";
	/**
	 * column S_STARTDATE
	 */
	public static String S_STARTDATE ="S_STARTDATE";
	/**
	 * column S_ENDDATE
	 */
	public static String S_ENDDATE ="S_ENDDATE";
	/**
	 * column N_INTEREST_RATES
	 */
	public static String N_INTEREST_RATES ="N_INTEREST_RATES";
	/**
	 * column S_UESRCODE
	 */
	public static String S_UESRCODE ="S_UESRCODE";
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
        String[] columnNames = new String[13];        
        columnNames[0]="I_VOUSRLNO";
        columnNames[1]="S_ORGCODE";
        columnNames[2]="S_QUARTER";
        columnNames[3]="S_STARTDATE";
        columnNames[4]="S_ENDDATE";
        columnNames[5]="N_INTEREST_RATES";
        columnNames[6]="S_UESRCODE";
        columnNames[7]="S_STATUS";
        columnNames[8]="S_DEMO";
        columnNames[9]="TS_SYSUPDATE";
        columnNames[10]="S_EXT1";
        columnNames[11]="S_EXT2";
        columnNames[12]="S_EXT3";
        return columnNames;     
    }
	/******************************************************
	*
	*  Get Column Name
	*
	*******************************************************/
	/**
	 *主键 Getter I_VOUSRLNO, PK , NOT NULL   	 
	 */
	public static String columnIvousrlno(){
		 return I_VOUSRLNO;
	}
	/**
	 *机构代码 Getter S_ORGCODE , NOT NULL   	 
	 */
	public static String columnSorgcode(){
		 return S_ORGCODE;
	}
	/**
	 *季度 Getter S_QUARTER   	 
	 */
	public static String columnSquarter(){
		 return S_QUARTER;
	}
	/**
	 *季度开始日期 Getter S_STARTDATE   	 
	 */
	public static String columnSstartdate(){
		 return S_STARTDATE;
	}
	/**
	 *季度截止日期 Getter S_ENDDATE   	 
	 */
	public static String columnSenddate(){
		 return S_ENDDATE;
	}
	/**
	 *利率 Getter N_INTEREST_RATES   	 
	 */
	public static String columnNinterestrates(){
		 return N_INTEREST_RATES;
	}
	/**
	 *用户 Getter S_UESRCODE   	 
	 */
	public static String columnSuesrcode(){
		 return S_UESRCODE;
	}
	/**
	 *状态 Getter S_STATUS   	 
	 */
	public static String columnSstatus(){
		 return S_STATUS;
	}
	/**
	 *描述 Getter S_DEMO   	 
	 */
	public static String columnSdemo(){
		 return S_DEMO;
	}
	/**
	 *系统时间 Getter TS_SYSUPDATE   	 
	 */
	public static String columnTssysupdate(){
		 return TS_SYSUPDATE;
	}
	/**
	 *扩展 Getter S_EXT1   	 
	 */
	public static String columnSext1(){
		 return S_EXT1;
	}
	/**
	 *扩展 Getter S_EXT2   	 
	 */
	public static String columnSext2(){
		 return S_EXT2;
	}
	/**
	 *扩展 Getter S_EXT3   	 
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
	 *主键 Getter I_VOUSRLNO, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeIvousrlno(){
		 return "Long";
	}
	/**
	 *机构代码 Getter S_ORGCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSorgcode(){
		 return "String";
	}
	/**
	 *季度 Getter S_QUARTER   	 
	 */
	public static String columnJavaTypeSquarter(){
		 return "String";
	}
	/**
	 *季度开始日期 Getter S_STARTDATE   	 
	 */
	public static String columnJavaTypeSstartdate(){
		 return "String";
	}
	/**
	 *季度截止日期 Getter S_ENDDATE   	 
	 */
	public static String columnJavaTypeSenddate(){
		 return "String";
	}
	/**
	 *利率 Getter N_INTEREST_RATES   	 
	 */
	public static String columnJavaTypeNinterestrates(){
		 return "BigDecimal";
	}
	/**
	 *用户 Getter S_UESRCODE   	 
	 */
	public static String columnJavaTypeSuesrcode(){
		 return "String";
	}
	/**
	 *状态 Getter S_STATUS   	 
	 */
	public static String columnJavaTypeSstatus(){
		 return "String";
	}
	/**
	 *描述 Getter S_DEMO   	 
	 */
	public static String columnJavaTypeSdemo(){
		 return "String";
	}
	/**
	 *系统时间 Getter TS_SYSUPDATE   	 
	 */
	public static String columnJavaTypeTssysupdate(){
		 return "Timestamp";
	}
	/**
	 *扩展 Getter S_EXT1   	 
	 */
	public static String columnJavaTypeSext1(){
		 return "String";
	}
	/**
	 *扩展 Getter S_EXT2   	 
	 */
	public static String columnJavaTypeSext2(){
		 return "String";
	}
	/**
	 *扩展 Getter S_EXT3   	 
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
	 *主键 Getter I_VOUSRLNO, PK , NOT NULL   	 
	 * columnType is BIGINT
	 */
	public static String columnDatabaseTypeIvousrlno(){
		 return "BIGINT";
	}
	/**
	 *机构代码 Getter S_ORGCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSorgcode(){
		 return "VARCHAR";
	}
	/**
	 *季度 Getter S_QUARTER   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSquarter(){
		 return "VARCHAR";
	}
	/**
	 *季度开始日期 Getter S_STARTDATE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSstartdate(){
		 return "VARCHAR";
	}
	/**
	 *季度截止日期 Getter S_ENDDATE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSenddate(){
		 return "VARCHAR";
	}
	/**
	 *利率 Getter N_INTEREST_RATES   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNinterestrates(){
		 return "DECIMAL";
	}
	/**
	 *用户 Getter S_UESRCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSuesrcode(){
		 return "VARCHAR";
	}
	/**
	 *状态 Getter S_STATUS   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSstatus(){
		 return "VARCHAR";
	}
	/**
	 *描述 Getter S_DEMO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSdemo(){
		 return "VARCHAR";
	}
	/**
	 *系统时间 Getter TS_SYSUPDATE   	 
	 * columnType is TIMESTAMP
	 */
	public static String columnDatabaseTypeTssysupdate(){
		 return "TIMESTAMP";
	}
	/**
	 *扩展 Getter S_EXT1   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSext1(){
		 return "VARCHAR";
	}
	/**
	 *扩展 Getter S_EXT2   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSext2(){
		 return "VARCHAR";
	}
	/**
	 *扩展 Getter S_EXT3   	 
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
	 *机构代码 Getter S_ORGCODE , NOT NULL   	 
	 */
	public static int columnWidthSorgcode(){
		 return 12;
	}
	/**
	 *季度 Getter S_QUARTER   	 
	 */
	public static int columnWidthSquarter(){
		 return 4;
	}
	/**
	 *季度开始日期 Getter S_STARTDATE   	 
	 */
	public static int columnWidthSstartdate(){
		 return 8;
	}
	/**
	 *季度截止日期 Getter S_ENDDATE   	 
	 */
	public static int columnWidthSenddate(){
		 return 8;
	}
	/**
	 *用户 Getter S_UESRCODE   	 
	 */
	public static int columnWidthSuesrcode(){
		 return 60;
	}
	/**
	 *状态 Getter S_STATUS   	 
	 */
	public static int columnWidthSstatus(){
		 return 5;
	}
	/**
	 *描述 Getter S_DEMO   	 
	 */
	public static int columnWidthSdemo(){
		 return 100;
	}
	/**
	 *扩展 Getter S_EXT1   	 
	 */
	public static int columnWidthSext1(){
		 return 50;
	}
	/**
	 *扩展 Getter S_EXT2   	 
	 */
	public static int columnWidthSext2(){
		 return 50;
	}
	/**
	 *扩展 Getter S_EXT3   	 
	 */
	public static int columnWidthSext3(){
		 return 50;
	}
}
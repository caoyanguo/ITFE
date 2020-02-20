      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TF_REPORT_DEFRAY_MAIN</p>
 * <p>Description: 支出报表对帐主表3512 Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:58 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class TfReportDefrayMainInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TF_REPORT_DEFRAY_MAIN";
	
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
	 * column S_BILLKIND
	 */
	public static String S_BILLKIND ="S_BILLKIND";
	/**
	 * column S_FINORGCODE
	 */
	public static String S_FINORGCODE ="S_FINORGCODE";
	/**
	 * column S_TRENAME
	 */
	public static String S_TRENAME ="S_TRENAME";
	/**
	 * column S_BGTTYPECODE
	 */
	public static String S_BGTTYPECODE ="S_BGTTYPECODE";
	/**
	 * column S_BGTTYPENAME
	 */
	public static String S_BGTTYPENAME ="S_BGTTYPENAME";
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
        String[] columnNames = new String[33];        
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
        columnNames[15]="S_BILLKIND";
        columnNames[16]="S_FINORGCODE";
        columnNames[17]="S_TRENAME";
        columnNames[18]="S_BGTTYPECODE";
        columnNames[19]="S_BGTTYPENAME";
        columnNames[20]="S_BEGINDATE";
        columnNames[21]="S_ENDDATE";
        columnNames[22]="S_ALLNUM";
        columnNames[23]="N_ALLAMT";
        columnNames[24]="S_XCHECKRESULT";
        columnNames[25]="S_XDIFFNUM";
        columnNames[26]="S_HOLD1";
        columnNames[27]="S_HOLD2";
        columnNames[28]="S_EXT1";
        columnNames[29]="S_EXT2";
        columnNames[30]="S_EXT3";
        columnNames[31]="S_EXT4";
        columnNames[32]="S_EXT5";
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
	 *国库代码 Getter S_TRECODE , NOT NULL   	 
	 */
	public static String columnStrecode(){
		 return S_TRECODE;
	}
	/**
	 *状态 Getter S_STATUS , NOT NULL   	 
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
	 *包流水号 Getter S_PACKAGENO   	 
	 */
	public static String columnSpackageno(){
		 return S_PACKAGENO;
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
	 *对账单号 Getter S_VOUCHERCHECKNO , NOT NULL   	 
	 */
	public static String columnSvouchercheckno(){
		 return S_VOUCHERCHECKNO;
	}
	/**
	 *子包总数 Getter S_CHILDPACKNUM , NOT NULL   	 
	 */
	public static String columnSchildpacknum(){
		 return S_CHILDPACKNUM;
	}
	/**
	 *本包序号 Getter S_CURPACKNO , NOT NULL   	 
	 */
	public static String columnScurpackno(){
		 return S_CURPACKNO;
	}
	/**
	 *报表种类 Getter S_BILLKIND , NOT NULL   	 
	 */
	public static String columnSbillkind(){
		 return S_BILLKIND;
	}
	/**
	 *财政机关代码 Getter S_FINORGCODE , NOT NULL   	 
	 */
	public static String columnSfinorgcode(){
		 return S_FINORGCODE;
	}
	/**
	 *国库主体名称 Getter S_TRENAME , NOT NULL   	 
	 */
	public static String columnStrename(){
		 return S_TRENAME;
	}
	/**
	 *预算类型编码 Getter S_BGTTYPECODE   	 
	 */
	public static String columnSbgttypecode(){
		 return S_BGTTYPECODE;
	}
	/**
	 *预算类型名称 Getter S_BGTTYPENAME   	 
	 */
	public static String columnSbgttypename(){
		 return S_BGTTYPENAME;
	}
	/**
	 *对账起始日期 Getter S_BEGINDATE , NOT NULL   	 
	 */
	public static String columnSbegindate(){
		 return S_BEGINDATE;
	}
	/**
	 *对账终止日期 Getter S_ENDDATE , NOT NULL   	 
	 */
	public static String columnSenddate(){
		 return S_ENDDATE;
	}
	/**
	 *总笔数 Getter S_ALLNUM , NOT NULL   	 
	 */
	public static String columnSallnum(){
		 return S_ALLNUM;
	}
	/**
	 *总金额 Getter N_ALLAMT , NOT NULL   	 
	 */
	public static String columnNallamt(){
		 return N_ALLAMT;
	}
	/**
	 *对帐结果 Getter S_XCHECKRESULT   	 
	 */
	public static String columnSxcheckresult(){
		 return S_XCHECKRESULT;
	}
	/**
	 *不符笔数 Getter S_XDIFFNUM   	 
	 */
	public static String columnSxdiffnum(){
		 return S_XDIFFNUM;
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
	/**
	 *扩展 Getter S_EXT4   	 
	 */
	public static String columnSext4(){
		 return S_EXT4;
	}
	/**
	 *扩展 Getter S_EXT5   	 
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
	 *国库代码 Getter S_TRECODE , NOT NULL   	 
	 */
	public static String columnJavaTypeStrecode(){
		 return "String";
	}
	/**
	 *状态 Getter S_STATUS , NOT NULL   	 
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
	 *包流水号 Getter S_PACKAGENO   	 
	 */
	public static String columnJavaTypeSpackageno(){
		 return "String";
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
	 *对账单号 Getter S_VOUCHERCHECKNO , NOT NULL   	 
	 */
	public static String columnJavaTypeSvouchercheckno(){
		 return "String";
	}
	/**
	 *子包总数 Getter S_CHILDPACKNUM , NOT NULL   	 
	 */
	public static String columnJavaTypeSchildpacknum(){
		 return "String";
	}
	/**
	 *本包序号 Getter S_CURPACKNO , NOT NULL   	 
	 */
	public static String columnJavaTypeScurpackno(){
		 return "String";
	}
	/**
	 *报表种类 Getter S_BILLKIND , NOT NULL   	 
	 */
	public static String columnJavaTypeSbillkind(){
		 return "String";
	}
	/**
	 *财政机关代码 Getter S_FINORGCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSfinorgcode(){
		 return "String";
	}
	/**
	 *国库主体名称 Getter S_TRENAME , NOT NULL   	 
	 */
	public static String columnJavaTypeStrename(){
		 return "String";
	}
	/**
	 *预算类型编码 Getter S_BGTTYPECODE   	 
	 */
	public static String columnJavaTypeSbgttypecode(){
		 return "String";
	}
	/**
	 *预算类型名称 Getter S_BGTTYPENAME   	 
	 */
	public static String columnJavaTypeSbgttypename(){
		 return "String";
	}
	/**
	 *对账起始日期 Getter S_BEGINDATE , NOT NULL   	 
	 */
	public static String columnJavaTypeSbegindate(){
		 return "String";
	}
	/**
	 *对账终止日期 Getter S_ENDDATE , NOT NULL   	 
	 */
	public static String columnJavaTypeSenddate(){
		 return "String";
	}
	/**
	 *总笔数 Getter S_ALLNUM , NOT NULL   	 
	 */
	public static String columnJavaTypeSallnum(){
		 return "String";
	}
	/**
	 *总金额 Getter N_ALLAMT , NOT NULL   	 
	 */
	public static String columnJavaTypeNallamt(){
		 return "BigDecimal";
	}
	/**
	 *对帐结果 Getter S_XCHECKRESULT   	 
	 */
	public static String columnJavaTypeSxcheckresult(){
		 return "String";
	}
	/**
	 *不符笔数 Getter S_XDIFFNUM   	 
	 */
	public static String columnJavaTypeSxdiffnum(){
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
	/**
	 *扩展 Getter S_EXT4   	 
	 */
	public static String columnJavaTypeSext4(){
		 return "String";
	}
	/**
	 *扩展 Getter S_EXT5   	 
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
	 *国库代码 Getter S_TRECODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStrecode(){
		 return "VARCHAR";
	}
	/**
	 *状态 Getter S_STATUS , NOT NULL   	 
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
	 *包流水号 Getter S_PACKAGENO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpackageno(){
		 return "VARCHAR";
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
	 *对账单号 Getter S_VOUCHERCHECKNO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSvouchercheckno(){
		 return "VARCHAR";
	}
	/**
	 *子包总数 Getter S_CHILDPACKNUM , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSchildpacknum(){
		 return "VARCHAR";
	}
	/**
	 *本包序号 Getter S_CURPACKNO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeScurpackno(){
		 return "VARCHAR";
	}
	/**
	 *报表种类 Getter S_BILLKIND , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSbillkind(){
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
	 *国库主体名称 Getter S_TRENAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStrename(){
		 return "VARCHAR";
	}
	/**
	 *预算类型编码 Getter S_BGTTYPECODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSbgttypecode(){
		 return "VARCHAR";
	}
	/**
	 *预算类型名称 Getter S_BGTTYPENAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSbgttypename(){
		 return "VARCHAR";
	}
	/**
	 *对账起始日期 Getter S_BEGINDATE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSbegindate(){
		 return "VARCHAR";
	}
	/**
	 *对账终止日期 Getter S_ENDDATE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSenddate(){
		 return "VARCHAR";
	}
	/**
	 *总笔数 Getter S_ALLNUM , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSallnum(){
		 return "VARCHAR";
	}
	/**
	 *总金额 Getter N_ALLAMT , NOT NULL   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNallamt(){
		 return "DECIMAL";
	}
	/**
	 *对帐结果 Getter S_XCHECKRESULT   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSxcheckresult(){
		 return "VARCHAR";
	}
	/**
	 *不符笔数 Getter S_XDIFFNUM   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSxdiffnum(){
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
	/**
	 *扩展 Getter S_EXT4   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSext4(){
		 return "VARCHAR";
	}
	/**
	 *扩展 Getter S_EXT5   	 
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
	 *机构代码 Getter S_ORGCODE , NOT NULL   	 
	 */
	public static int columnWidthSorgcode(){
		 return 12;
	}
	/**
	 *国库代码 Getter S_TRECODE , NOT NULL   	 
	 */
	public static int columnWidthStrecode(){
		 return 10;
	}
	/**
	 *状态 Getter S_STATUS , NOT NULL   	 
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
	 *包流水号 Getter S_PACKAGENO   	 
	 */
	public static int columnWidthSpackageno(){
		 return 8;
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
	 *对账单号 Getter S_VOUCHERCHECKNO , NOT NULL   	 
	 */
	public static int columnWidthSvouchercheckno(){
		 return 42;
	}
	/**
	 *子包总数 Getter S_CHILDPACKNUM , NOT NULL   	 
	 */
	public static int columnWidthSchildpacknum(){
		 return 10;
	}
	/**
	 *本包序号 Getter S_CURPACKNO , NOT NULL   	 
	 */
	public static int columnWidthScurpackno(){
		 return 10;
	}
	/**
	 *报表种类 Getter S_BILLKIND , NOT NULL   	 
	 */
	public static int columnWidthSbillkind(){
		 return 1;
	}
	/**
	 *财政机关代码 Getter S_FINORGCODE , NOT NULL   	 
	 */
	public static int columnWidthSfinorgcode(){
		 return 12;
	}
	/**
	 *国库主体名称 Getter S_TRENAME , NOT NULL   	 
	 */
	public static int columnWidthStrename(){
		 return 60;
	}
	/**
	 *预算类型编码 Getter S_BGTTYPECODE   	 
	 */
	public static int columnWidthSbgttypecode(){
		 return 42;
	}
	/**
	 *预算类型名称 Getter S_BGTTYPENAME   	 
	 */
	public static int columnWidthSbgttypename(){
		 return 60;
	}
	/**
	 *对账起始日期 Getter S_BEGINDATE , NOT NULL   	 
	 */
	public static int columnWidthSbegindate(){
		 return 8;
	}
	/**
	 *对账终止日期 Getter S_ENDDATE , NOT NULL   	 
	 */
	public static int columnWidthSenddate(){
		 return 8;
	}
	/**
	 *总笔数 Getter S_ALLNUM , NOT NULL   	 
	 */
	public static int columnWidthSallnum(){
		 return 10;
	}
	/**
	 *对帐结果 Getter S_XCHECKRESULT   	 
	 */
	public static int columnWidthSxcheckresult(){
		 return 5;
	}
	/**
	 *不符笔数 Getter S_XDIFFNUM   	 
	 */
	public static int columnWidthSxdiffnum(){
		 return 10;
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
	/**
	 *扩展 Getter S_EXT4   	 
	 */
	public static int columnWidthSext4(){
		 return 50;
	}
	/**
	 *扩展 Getter S_EXT5   	 
	 */
	public static int columnWidthSext5(){
		 return 50;
	}
}
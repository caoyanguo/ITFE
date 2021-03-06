      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TF_GRANTPAY_ADJUSTMAIN</p>
 * <p>Description: 财政授权支付调整凭证5351主表 Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:58 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class TfGrantpayAdjustmainInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TF_GRANTPAY_ADJUSTMAIN";
	
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
	 * column S_ID
	 */
	public static String S_ID ="S_ID";
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
	 * column S_BGTTYPECODE
	 */
	public static String S_BGTTYPECODE ="S_BGTTYPECODE";
	/**
	 * column S_BGTTYPENAME
	 */
	public static String S_BGTTYPENAME ="S_BGTTYPENAME";
	/**
	 * column S_FUNDTYPECODE
	 */
	public static String S_FUNDTYPECODE ="S_FUNDTYPECODE";
	/**
	 * column S_FUNDTYPENAME
	 */
	public static String S_FUNDTYPENAME ="S_FUNDTYPENAME";
	/**
	 * column N_PAYAMT
	 */
	public static String N_PAYAMT ="N_PAYAMT";
	/**
	 * column S_PAYBANKCODE
	 */
	public static String S_PAYBANKCODE ="S_PAYBANKCODE";
	/**
	 * column S_PAYBANKNAME
	 */
	public static String S_PAYBANKNAME ="S_PAYBANKNAME";
	/**
	 * column S_REMARK
	 */
	public static String S_REMARK ="S_REMARK";
	/**
	 * column S_XACCDATE
	 */
	public static String S_XACCDATE ="S_XACCDATE";
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
        String[] columnNames = new String[29];        
        columnNames[0]="I_VOUSRLNO";
        columnNames[1]="S_ORGCODE";
        columnNames[2]="S_TRECODE";
        columnNames[3]="S_STATUS";
        columnNames[4]="S_DEMO";
        columnNames[5]="TS_SYSUPDATE";
        columnNames[6]="S_PACKAGENO";
        columnNames[7]="S_ID";
        columnNames[8]="S_ADMDIVCODE";
        columnNames[9]="S_STYEAR";
        columnNames[10]="S_VTCODE";
        columnNames[11]="S_VOUDATE";
        columnNames[12]="S_VOUCHERNO";
        columnNames[13]="S_BGTTYPECODE";
        columnNames[14]="S_BGTTYPENAME";
        columnNames[15]="S_FUNDTYPECODE";
        columnNames[16]="S_FUNDTYPENAME";
        columnNames[17]="N_PAYAMT";
        columnNames[18]="S_PAYBANKCODE";
        columnNames[19]="S_PAYBANKNAME";
        columnNames[20]="S_REMARK";
        columnNames[21]="S_XACCDATE";
        columnNames[22]="S_HOLD1";
        columnNames[23]="S_HOLD2";
        columnNames[24]="S_EXT1";
        columnNames[25]="S_EXT2";
        columnNames[26]="S_EXT3";
        columnNames[27]="S_EXT4";
        columnNames[28]="S_EXT5";
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
	 *包流水号 Getter S_PACKAGENO   	 
	 */
	public static String columnSpackageno(){
		 return S_PACKAGENO;
	}
	/**
	 *申请单ID Getter S_ID , NOT NULL   	 
	 */
	public static String columnSid(){
		 return S_ID;
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
	 *资金性质编码 Getter S_FUNDTYPECODE , NOT NULL   	 
	 */
	public static String columnSfundtypecode(){
		 return S_FUNDTYPECODE;
	}
	/**
	 *资金性质名称 Getter S_FUNDTYPENAME , NOT NULL   	 
	 */
	public static String columnSfundtypename(){
		 return S_FUNDTYPENAME;
	}
	/**
	 *汇总调整金额 Getter N_PAYAMT , NOT NULL   	 
	 */
	public static String columnNpayamt(){
		 return N_PAYAMT;
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
	 *摘要 Getter S_REMARK   	 
	 */
	public static String columnSremark(){
		 return S_REMARK;
	}
	/**
	 *处理日期 Getter S_XACCDATE , NOT NULL   	 
	 */
	public static String columnSxaccdate(){
		 return S_XACCDATE;
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
	 *包流水号 Getter S_PACKAGENO   	 
	 */
	public static String columnJavaTypeSpackageno(){
		 return "String";
	}
	/**
	 *申请单ID Getter S_ID , NOT NULL   	 
	 */
	public static String columnJavaTypeSid(){
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
	 *资金性质编码 Getter S_FUNDTYPECODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSfundtypecode(){
		 return "String";
	}
	/**
	 *资金性质名称 Getter S_FUNDTYPENAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSfundtypename(){
		 return "String";
	}
	/**
	 *汇总调整金额 Getter N_PAYAMT , NOT NULL   	 
	 */
	public static String columnJavaTypeNpayamt(){
		 return "BigDecimal";
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
	 *摘要 Getter S_REMARK   	 
	 */
	public static String columnJavaTypeSremark(){
		 return "String";
	}
	/**
	 *处理日期 Getter S_XACCDATE , NOT NULL   	 
	 */
	public static String columnJavaTypeSxaccdate(){
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
	 *包流水号 Getter S_PACKAGENO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpackageno(){
		 return "VARCHAR";
	}
	/**
	 *申请单ID Getter S_ID , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSid(){
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
	 *资金性质编码 Getter S_FUNDTYPECODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSfundtypecode(){
		 return "VARCHAR";
	}
	/**
	 *资金性质名称 Getter S_FUNDTYPENAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSfundtypename(){
		 return "VARCHAR";
	}
	/**
	 *汇总调整金额 Getter N_PAYAMT , NOT NULL   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNpayamt(){
		 return "DECIMAL";
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
	 *摘要 Getter S_REMARK   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSremark(){
		 return "VARCHAR";
	}
	/**
	 *处理日期 Getter S_XACCDATE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSxaccdate(){
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
	 *包流水号 Getter S_PACKAGENO   	 
	 */
	public static int columnWidthSpackageno(){
		 return 8;
	}
	/**
	 *申请单ID Getter S_ID , NOT NULL   	 
	 */
	public static int columnWidthSid(){
		 return 38;
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
	 *资金性质编码 Getter S_FUNDTYPECODE , NOT NULL   	 
	 */
	public static int columnWidthSfundtypecode(){
		 return 42;
	}
	/**
	 *资金性质名称 Getter S_FUNDTYPENAME , NOT NULL   	 
	 */
	public static int columnWidthSfundtypename(){
		 return 60;
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
	 *摘要 Getter S_REMARK   	 
	 */
	public static int columnWidthSremark(){
		 return 200;
	}
	/**
	 *处理日期 Getter S_XACCDATE , NOT NULL   	 
	 */
	public static int columnWidthSxaccdate(){
		 return 8;
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
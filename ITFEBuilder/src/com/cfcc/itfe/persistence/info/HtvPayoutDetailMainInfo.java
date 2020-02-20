      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: HTV_PAYOUT_DETAIL_MAIN</p>
 * <p>Description: 实拨拨款凭证明细清单5257 Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:56 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class HtvPayoutDetailMainInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "HTV_PAYOUT_DETAIL_MAIN";
	
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
	 * column S_TRECODE
	 */
	public static String S_TRECODE ="S_TRECODE";
	/**
	 * column S_FINORGCODE
	 */
	public static String S_FINORGCODE ="S_FINORGCODE";
	/**
	 * column N_PAYAMT
	 */
	public static String N_PAYAMT ="N_PAYAMT";
	/**
	 * column S_PRINTUSER
	 */
	public static String S_PRINTUSER ="S_PRINTUSER";
	/**
	 * column S_REMARK
	 */
	public static String S_REMARK ="S_REMARK";
	/**
	 * column S_HOLD1
	 */
	public static String S_HOLD1 ="S_HOLD1";
	/**
	 * column S_HOLD2
	 */
	public static String S_HOLD2 ="S_HOLD2";
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
        columnNames[0]="S_ID";
        columnNames[1]="S_ADMDIVCODE";
        columnNames[2]="S_STYEAR";
        columnNames[3]="S_VTCODE";
        columnNames[4]="S_VOUDATE";
        columnNames[5]="S_VOUCHERNO";
        columnNames[6]="S_TRECODE";
        columnNames[7]="S_FINORGCODE";
        columnNames[8]="N_PAYAMT";
        columnNames[9]="S_PRINTUSER";
        columnNames[10]="S_REMARK";
        columnNames[11]="S_HOLD1";
        columnNames[12]="S_HOLD2";
        return columnNames;     
    }
	/******************************************************
	*
	*  Get Column Name
	*
	*******************************************************/
	/**
	 *拨款清单明细编号 Getter S_ID, PK , NOT NULL   	 
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
	 *凭证类型编号5257 Getter S_VTCODE , NOT NULL   	 
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
	 *汇总拨款金额 Getter N_PAYAMT , NOT NULL   	 
	 */
	public static String columnNpayamt(){
		 return N_PAYAMT;
	}
	/**
	 *打印人 Getter S_PRINTUSER   	 
	 */
	public static String columnSprintuser(){
		 return S_PRINTUSER;
	}
	/**
	 *备注 Getter S_REMARK   	 
	 */
	public static String columnSremark(){
		 return S_REMARK;
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
	/******************************************************
	*
	*  Get Column Java Type
	*
	*******************************************************/
	/**
	 *拨款清单明细编号 Getter S_ID, PK , NOT NULL   	 
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
	 *凭证类型编号5257 Getter S_VTCODE , NOT NULL   	 
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
	 *汇总拨款金额 Getter N_PAYAMT , NOT NULL   	 
	 */
	public static String columnJavaTypeNpayamt(){
		 return "BigDecimal";
	}
	/**
	 *打印人 Getter S_PRINTUSER   	 
	 */
	public static String columnJavaTypeSprintuser(){
		 return "String";
	}
	/**
	 *备注 Getter S_REMARK   	 
	 */
	public static String columnJavaTypeSremark(){
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
	/******************************************************
	*
	*  Get Column Database Type
	*
	*******************************************************/
	/**
	 *拨款清单明细编号 Getter S_ID, PK , NOT NULL   	 
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
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSstyear(){
		 return "CHARACTER";
	}
	/**
	 *凭证类型编号5257 Getter S_VTCODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSvtcode(){
		 return "CHARACTER";
	}
	/**
	 *凭证日期 Getter S_VOUDATE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSvoudate(){
		 return "CHARACTER";
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
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeStrecode(){
		 return "CHARACTER";
	}
	/**
	 *财政机关代码 Getter S_FINORGCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSfinorgcode(){
		 return "VARCHAR";
	}
	/**
	 *汇总拨款金额 Getter N_PAYAMT , NOT NULL   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNpayamt(){
		 return "DECIMAL";
	}
	/**
	 *打印人 Getter S_PRINTUSER   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSprintuser(){
		 return "VARCHAR";
	}
	/**
	 *备注 Getter S_REMARK   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSremark(){
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
	/******************************************************
	*
	*  Get Column With
	*
	*******************************************************/
	/**
	 *拨款清单明细编号 Getter S_ID, PK , NOT NULL   	 
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
	 *凭证类型编号5257 Getter S_VTCODE , NOT NULL   	 
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
	 *打印人 Getter S_PRINTUSER   	 
	 */
	public static int columnWidthSprintuser(){
		 return 42;
	}
	/**
	 *备注 Getter S_REMARK   	 
	 */
	public static int columnWidthSremark(){
		 return 200;
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
}
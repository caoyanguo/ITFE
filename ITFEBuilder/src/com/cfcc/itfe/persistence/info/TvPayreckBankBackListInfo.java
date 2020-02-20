      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TV_PAYRECK_BANK_BACK_LIST</p>
 * <p>Description:  Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:03 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class TvPayreckBankBackListInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TV_PAYRECK_BANK_BACK_LIST";
	
	/**
	 * column I_VOUSRLNO
	 */
	public static String I_VOUSRLNO ="I_VOUSRLNO";
	/**
	 * column I_SEQNO
	 */
	public static String I_SEQNO ="I_SEQNO";
	/**
	 * column S_ORIVOUNO
	 */
	public static String S_ORIVOUNO ="S_ORIVOUNO";
	/**
	 * column D_ORIVOUDATE
	 */
	public static String D_ORIVOUDATE ="D_ORIVOUDATE";
	/**
	 * column S_BDGORGCODE
	 */
	public static String S_BDGORGCODE ="S_BDGORGCODE";
	/**
	 * column S_FUNCBDGSBTCODE
	 */
	public static String S_FUNCBDGSBTCODE ="S_FUNCBDGSBTCODE";
	/**
	 * column S_ECNOMICSUBJECTCODE
	 */
	public static String S_ECNOMICSUBJECTCODE ="S_ECNOMICSUBJECTCODE";
	/**
	 * column F_AMT
	 */
	public static String F_AMT ="F_AMT";
	/**
	 * column S_ACCTPROP
	 */
	public static String S_ACCTPROP ="S_ACCTPROP";
	/**
	 * column TS_UPDATE
	 */
	public static String TS_UPDATE ="TS_UPDATE";
	/**
	 * column S_SUPDEPNAME
	 */
	public static String S_SUPDEPNAME ="S_SUPDEPNAME";
	/**
	 * column S_EXPFUNCNAME
	 */
	public static String S_EXPFUNCNAME ="S_EXPFUNCNAME";
	/**
	 * column S_PAYSUMMARYNAME
	 */
	public static String S_PAYSUMMARYNAME ="S_PAYSUMMARYNAME";
	/**
	 * column S_HOLD1
	 */
	public static String S_HOLD1 ="S_HOLD1";
	/**
	 * column S_HOLD2
	 */
	public static String S_HOLD2 ="S_HOLD2";
	/**
	 * column S_HOLD3
	 */
	public static String S_HOLD3 ="S_HOLD3";
	/**
	 * column S_HOLD4
	 */
	public static String S_HOLD4 ="S_HOLD4";
	/**
	 * column S_VOUCHERNO
	 */
	public static String S_VOUCHERNO ="S_VOUCHERNO";
	/**
	 * column S_ORIVOUDETAILNO
	 */
	public static String S_ORIVOUDETAILNO ="S_ORIVOUDETAILNO";
	/**
	 * column S_ID
	 */
	public static String S_ID ="S_ID";
	/**
	 * column S_PAYDATE
	 */
	public static String S_PAYDATE ="S_PAYDATE";
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
        String[] columnNames = new String[21];        
        columnNames[0]="I_VOUSRLNO";
        columnNames[1]="I_SEQNO";
        columnNames[2]="S_ORIVOUNO";
        columnNames[3]="D_ORIVOUDATE";
        columnNames[4]="S_BDGORGCODE";
        columnNames[5]="S_FUNCBDGSBTCODE";
        columnNames[6]="S_ECNOMICSUBJECTCODE";
        columnNames[7]="F_AMT";
        columnNames[8]="S_ACCTPROP";
        columnNames[9]="TS_UPDATE";
        columnNames[10]="S_SUPDEPNAME";
        columnNames[11]="S_EXPFUNCNAME";
        columnNames[12]="S_PAYSUMMARYNAME";
        columnNames[13]="S_HOLD1";
        columnNames[14]="S_HOLD2";
        columnNames[15]="S_HOLD3";
        columnNames[16]="S_HOLD4";
        columnNames[17]="S_VOUCHERNO";
        columnNames[18]="S_ORIVOUDETAILNO";
        columnNames[19]="S_ID";
        columnNames[20]="S_PAYDATE";
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
	 * Getter I_SEQNO, PK , NOT NULL   	 
	 */
	public static String columnIseqno(){
		 return I_SEQNO;
	}
	/**
	 * Getter S_ORIVOUNO   	 
	 */
	public static String columnSorivouno(){
		 return S_ORIVOUNO;
	}
	/**
	 * Getter D_ORIVOUDATE   	 
	 */
	public static String columnDorivoudate(){
		 return D_ORIVOUDATE;
	}
	/**
	 * Getter S_BDGORGCODE , NOT NULL   	 
	 */
	public static String columnSbdgorgcode(){
		 return S_BDGORGCODE;
	}
	/**
	 * Getter S_FUNCBDGSBTCODE , NOT NULL   	 
	 */
	public static String columnSfuncbdgsbtcode(){
		 return S_FUNCBDGSBTCODE;
	}
	/**
	 * Getter S_ECNOMICSUBJECTCODE   	 
	 */
	public static String columnSecnomicsubjectcode(){
		 return S_ECNOMICSUBJECTCODE;
	}
	/**
	 * Getter F_AMT , NOT NULL   	 
	 */
	public static String columnFamt(){
		 return F_AMT;
	}
	/**
	 * Getter S_ACCTPROP   	 
	 */
	public static String columnSacctprop(){
		 return S_ACCTPROP;
	}
	/**
	 * Getter TS_UPDATE , NOT NULL   	 
	 */
	public static String columnTsupdate(){
		 return TS_UPDATE;
	}
	/**
	 * Getter S_SUPDEPNAME   	 
	 */
	public static String columnSsupdepname(){
		 return S_SUPDEPNAME;
	}
	/**
	 * Getter S_EXPFUNCNAME   	 
	 */
	public static String columnSexpfuncname(){
		 return S_EXPFUNCNAME;
	}
	/**
	 * Getter S_PAYSUMMARYNAME   	 
	 */
	public static String columnSpaysummaryname(){
		 return S_PAYSUMMARYNAME;
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
	 * Getter S_HOLD3   	 
	 */
	public static String columnShold3(){
		 return S_HOLD3;
	}
	/**
	 * Getter S_HOLD4   	 
	 */
	public static String columnShold4(){
		 return S_HOLD4;
	}
	/**
	 * Getter S_VOUCHERNO   	 
	 */
	public static String columnSvoucherno(){
		 return S_VOUCHERNO;
	}
	/**
	 *原支付凭证明细单号 Getter S_ORIVOUDETAILNO   	 
	 */
	public static String columnSorivoudetailno(){
		 return S_ORIVOUDETAILNO;
	}
	/**
	 * Getter S_ID   	 
	 */
	public static String columnSid(){
		 return S_ID;
	}
	/**
	 *商行办理时间 Getter S_PAYDATE   	 
	 */
	public static String columnSpaydate(){
		 return S_PAYDATE;
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
	 * Getter I_SEQNO, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeIseqno(){
		 return "Integer";
	}
	/**
	 * Getter S_ORIVOUNO   	 
	 */
	public static String columnJavaTypeSorivouno(){
		 return "String";
	}
	/**
	 * Getter D_ORIVOUDATE   	 
	 */
	public static String columnJavaTypeDorivoudate(){
		 return "Date";
	}
	/**
	 * Getter S_BDGORGCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSbdgorgcode(){
		 return "String";
	}
	/**
	 * Getter S_FUNCBDGSBTCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSfuncbdgsbtcode(){
		 return "String";
	}
	/**
	 * Getter S_ECNOMICSUBJECTCODE   	 
	 */
	public static String columnJavaTypeSecnomicsubjectcode(){
		 return "String";
	}
	/**
	 * Getter F_AMT , NOT NULL   	 
	 */
	public static String columnJavaTypeFamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter S_ACCTPROP   	 
	 */
	public static String columnJavaTypeSacctprop(){
		 return "String";
	}
	/**
	 * Getter TS_UPDATE , NOT NULL   	 
	 */
	public static String columnJavaTypeTsupdate(){
		 return "Timestamp";
	}
	/**
	 * Getter S_SUPDEPNAME   	 
	 */
	public static String columnJavaTypeSsupdepname(){
		 return "String";
	}
	/**
	 * Getter S_EXPFUNCNAME   	 
	 */
	public static String columnJavaTypeSexpfuncname(){
		 return "String";
	}
	/**
	 * Getter S_PAYSUMMARYNAME   	 
	 */
	public static String columnJavaTypeSpaysummaryname(){
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
	 * Getter S_HOLD3   	 
	 */
	public static String columnJavaTypeShold3(){
		 return "String";
	}
	/**
	 * Getter S_HOLD4   	 
	 */
	public static String columnJavaTypeShold4(){
		 return "String";
	}
	/**
	 * Getter S_VOUCHERNO   	 
	 */
	public static String columnJavaTypeSvoucherno(){
		 return "String";
	}
	/**
	 *原支付凭证明细单号 Getter S_ORIVOUDETAILNO   	 
	 */
	public static String columnJavaTypeSorivoudetailno(){
		 return "String";
	}
	/**
	 * Getter S_ID   	 
	 */
	public static String columnJavaTypeSid(){
		 return "String";
	}
	/**
	 *商行办理时间 Getter S_PAYDATE   	 
	 */
	public static String columnJavaTypeSpaydate(){
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
	 * Getter I_SEQNO, PK , NOT NULL   	 
	 * columnType is INTEGER
	 */
	public static String columnDatabaseTypeIseqno(){
		 return "INTEGER";
	}
	/**
	 * Getter S_ORIVOUNO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSorivouno(){
		 return "VARCHAR";
	}
	/**
	 * Getter D_ORIVOUDATE   	 
	 * columnType is DATE
	 */
	public static String columnDatabaseTypeDorivoudate(){
		 return "DATE";
	}
	/**
	 * Getter S_BDGORGCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSbdgorgcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_FUNCBDGSBTCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSfuncbdgsbtcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ECNOMICSUBJECTCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSecnomicsubjectcode(){
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
	 * Getter S_ACCTPROP   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSacctprop(){
		 return "CHARACTER";
	}
	/**
	 * Getter TS_UPDATE , NOT NULL   	 
	 * columnType is TIMESTAMP
	 */
	public static String columnDatabaseTypeTsupdate(){
		 return "TIMESTAMP";
	}
	/**
	 * Getter S_SUPDEPNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSsupdepname(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_EXPFUNCNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSexpfuncname(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYSUMMARYNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpaysummaryname(){
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
	 * Getter S_HOLD3   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeShold3(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_HOLD4   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeShold4(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_VOUCHERNO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSvoucherno(){
		 return "VARCHAR";
	}
	/**
	 *原支付凭证明细单号 Getter S_ORIVOUDETAILNO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSorivoudetailno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ID   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSid(){
		 return "VARCHAR";
	}
	/**
	 *商行办理时间 Getter S_PAYDATE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpaydate(){
		 return "VARCHAR";
	}
	/******************************************************
	*
	*  Get Column With
	*
	*******************************************************/
	/**
	 * Getter S_ORIVOUNO   	 
	 */
	public static int columnWidthSorivouno(){
		 return 42;
	}
	/**
	 * Getter S_BDGORGCODE , NOT NULL   	 
	 */
	public static int columnWidthSbdgorgcode(){
		 return 20;
	}
	/**
	 * Getter S_FUNCBDGSBTCODE , NOT NULL   	 
	 */
	public static int columnWidthSfuncbdgsbtcode(){
		 return 30;
	}
	/**
	 * Getter S_ECNOMICSUBJECTCODE   	 
	 */
	public static int columnWidthSecnomicsubjectcode(){
		 return 30;
	}
	/**
	 * Getter S_ACCTPROP   	 
	 */
	public static int columnWidthSacctprop(){
		 return 1;
	}
	/**
	 * Getter S_SUPDEPNAME   	 
	 */
	public static int columnWidthSsupdepname(){
		 return 60;
	}
	/**
	 * Getter S_EXPFUNCNAME   	 
	 */
	public static int columnWidthSexpfuncname(){
		 return 60;
	}
	/**
	 * Getter S_PAYSUMMARYNAME   	 
	 */
	public static int columnWidthSpaysummaryname(){
		 return 200;
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
	 * Getter S_HOLD3   	 
	 */
	public static int columnWidthShold3(){
		 return 42;
	}
	/**
	 * Getter S_HOLD4   	 
	 */
	public static int columnWidthShold4(){
		 return 42;
	}
	/**
	 * Getter S_VOUCHERNO   	 
	 */
	public static int columnWidthSvoucherno(){
		 return 42;
	}
	/**
	 *原支付凭证明细单号 Getter S_ORIVOUDETAILNO   	 
	 */
	public static int columnWidthSorivoudetailno(){
		 return 42;
	}
	/**
	 * Getter S_ID   	 
	 */
	public static int columnWidthSid(){
		 return 38;
	}
	/**
	 *商行办理时间 Getter S_PAYDATE   	 
	 */
	public static int columnWidthSpaydate(){
		 return 50;
	}
}
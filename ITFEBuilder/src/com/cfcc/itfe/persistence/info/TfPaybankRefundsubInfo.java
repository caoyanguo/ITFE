      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TF_PAYBANK_REFUNDSUB</p>
 * <p>Description: 收款银行退款通知2252子表 Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:58 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class TfPaybankRefundsubInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TF_PAYBANK_REFUNDSUB";
	
	/**
	 * column I_VOUSRLNO
	 */
	public static String I_VOUSRLNO ="I_VOUSRLNO";
	/**
	 * column I_SEQNO
	 */
	public static String I_SEQNO ="I_SEQNO";
	/**
	 * column S_ID
	 */
	public static String S_ID ="S_ID";
	/**
	 * column S_VOUCHERBILLID
	 */
	public static String S_VOUCHERBILLID ="S_VOUCHERBILLID";
	/**
	 * column S_PAYACCTNO
	 */
	public static String S_PAYACCTNO ="S_PAYACCTNO";
	/**
	 * column S_PAYACCTNAME
	 */
	public static String S_PAYACCTNAME ="S_PAYACCTNAME";
	/**
	 * column S_PAYACCTBANKNAME
	 */
	public static String S_PAYACCTBANKNAME ="S_PAYACCTBANKNAME";
	/**
	 * column S_PAYEEACCTNO
	 */
	public static String S_PAYEEACCTNO ="S_PAYEEACCTNO";
	/**
	 * column S_PAYEEACCTNAME
	 */
	public static String S_PAYEEACCTNAME ="S_PAYEEACCTNAME";
	/**
	 * column S_PAYEEACCTBANKNAME
	 */
	public static String S_PAYEEACCTBANKNAME ="S_PAYEEACCTBANKNAME";
	/**
	 * column S_PAYEEACCTBANKNO
	 */
	public static String S_PAYEEACCTBANKNO ="S_PAYEEACCTBANKNO";
	/**
	 * column N_PAYAMT
	 */
	public static String N_PAYAMT ="N_PAYAMT";
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
	 * column S_HOLD3
	 */
	public static String S_HOLD3 ="S_HOLD3";
	/**
	 * column S_HOLD4
	 */
	public static String S_HOLD4 ="S_HOLD4";
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
        columnNames[1]="I_SEQNO";
        columnNames[2]="S_ID";
        columnNames[3]="S_VOUCHERBILLID";
        columnNames[4]="S_PAYACCTNO";
        columnNames[5]="S_PAYACCTNAME";
        columnNames[6]="S_PAYACCTBANKNAME";
        columnNames[7]="S_PAYEEACCTNO";
        columnNames[8]="S_PAYEEACCTNAME";
        columnNames[9]="S_PAYEEACCTBANKNAME";
        columnNames[10]="S_PAYEEACCTBANKNO";
        columnNames[11]="N_PAYAMT";
        columnNames[12]="S_REMARK";
        columnNames[13]="S_HOLD1";
        columnNames[14]="S_HOLD2";
        columnNames[15]="S_HOLD3";
        columnNames[16]="S_HOLD4";
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
	 * Getter I_SEQNO, PK , NOT NULL   	 
	 */
	public static String columnIseqno(){
		 return I_SEQNO;
	}
	/**
	 *退款明细ID Getter S_ID   	 
	 */
	public static String columnSid(){
		 return S_ID;
	}
	/**
	 *收款银行退款通知ID Getter S_VOUCHERBILLID   	 
	 */
	public static String columnSvoucherbillid(){
		 return S_VOUCHERBILLID;
	}
	/**
	 *原付款人账号 Getter S_PAYACCTNO   	 
	 */
	public static String columnSpayacctno(){
		 return S_PAYACCTNO;
	}
	/**
	 *原付款人名称 Getter S_PAYACCTNAME   	 
	 */
	public static String columnSpayacctname(){
		 return S_PAYACCTNAME;
	}
	/**
	 *原付款人银行 Getter S_PAYACCTBANKNAME   	 
	 */
	public static String columnSpayacctbankname(){
		 return S_PAYACCTBANKNAME;
	}
	/**
	 *原收款人账号 Getter S_PAYEEACCTNO   	 
	 */
	public static String columnSpayeeacctno(){
		 return S_PAYEEACCTNO;
	}
	/**
	 *原收款人名称 Getter S_PAYEEACCTNAME   	 
	 */
	public static String columnSpayeeacctname(){
		 return S_PAYEEACCTNAME;
	}
	/**
	 *原收款人银行 Getter S_PAYEEACCTBANKNAME   	 
	 */
	public static String columnSpayeeacctbankname(){
		 return S_PAYEEACCTBANKNAME;
	}
	/**
	 *原收款人银行行号 Getter S_PAYEEACCTBANKNO   	 
	 */
	public static String columnSpayeeacctbankno(){
		 return S_PAYEEACCTBANKNO;
	}
	/**
	 *退款金额 Getter N_PAYAMT   	 
	 */
	public static String columnNpayamt(){
		 return N_PAYAMT;
	}
	/**
	 *退款原因 Getter S_REMARK   	 
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
	/**
	 *预留字段3 Getter S_HOLD3   	 
	 */
	public static String columnShold3(){
		 return S_HOLD3;
	}
	/**
	 *预留字段4 Getter S_HOLD4   	 
	 */
	public static String columnShold4(){
		 return S_HOLD4;
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
	 * Getter I_VOUSRLNO, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeIvousrlno(){
		 return "Long";
	}
	/**
	 * Getter I_SEQNO, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeIseqno(){
		 return "Long";
	}
	/**
	 *退款明细ID Getter S_ID   	 
	 */
	public static String columnJavaTypeSid(){
		 return "String";
	}
	/**
	 *收款银行退款通知ID Getter S_VOUCHERBILLID   	 
	 */
	public static String columnJavaTypeSvoucherbillid(){
		 return "String";
	}
	/**
	 *原付款人账号 Getter S_PAYACCTNO   	 
	 */
	public static String columnJavaTypeSpayacctno(){
		 return "String";
	}
	/**
	 *原付款人名称 Getter S_PAYACCTNAME   	 
	 */
	public static String columnJavaTypeSpayacctname(){
		 return "String";
	}
	/**
	 *原付款人银行 Getter S_PAYACCTBANKNAME   	 
	 */
	public static String columnJavaTypeSpayacctbankname(){
		 return "String";
	}
	/**
	 *原收款人账号 Getter S_PAYEEACCTNO   	 
	 */
	public static String columnJavaTypeSpayeeacctno(){
		 return "String";
	}
	/**
	 *原收款人名称 Getter S_PAYEEACCTNAME   	 
	 */
	public static String columnJavaTypeSpayeeacctname(){
		 return "String";
	}
	/**
	 *原收款人银行 Getter S_PAYEEACCTBANKNAME   	 
	 */
	public static String columnJavaTypeSpayeeacctbankname(){
		 return "String";
	}
	/**
	 *原收款人银行行号 Getter S_PAYEEACCTBANKNO   	 
	 */
	public static String columnJavaTypeSpayeeacctbankno(){
		 return "String";
	}
	/**
	 *退款金额 Getter N_PAYAMT   	 
	 */
	public static String columnJavaTypeNpayamt(){
		 return "BigDecimal";
	}
	/**
	 *退款原因 Getter S_REMARK   	 
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
	/**
	 *预留字段3 Getter S_HOLD3   	 
	 */
	public static String columnJavaTypeShold3(){
		 return "String";
	}
	/**
	 *预留字段4 Getter S_HOLD4   	 
	 */
	public static String columnJavaTypeShold4(){
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
	 * columnType is BIGINT
	 */
	public static String columnDatabaseTypeIseqno(){
		 return "BIGINT";
	}
	/**
	 *退款明细ID Getter S_ID   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSid(){
		 return "VARCHAR";
	}
	/**
	 *收款银行退款通知ID Getter S_VOUCHERBILLID   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSvoucherbillid(){
		 return "VARCHAR";
	}
	/**
	 *原付款人账号 Getter S_PAYACCTNO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayacctno(){
		 return "VARCHAR";
	}
	/**
	 *原付款人名称 Getter S_PAYACCTNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayacctname(){
		 return "VARCHAR";
	}
	/**
	 *原付款人银行 Getter S_PAYACCTBANKNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayacctbankname(){
		 return "VARCHAR";
	}
	/**
	 *原收款人账号 Getter S_PAYEEACCTNO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeeacctno(){
		 return "VARCHAR";
	}
	/**
	 *原收款人名称 Getter S_PAYEEACCTNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeeacctname(){
		 return "VARCHAR";
	}
	/**
	 *原收款人银行 Getter S_PAYEEACCTBANKNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeeacctbankname(){
		 return "VARCHAR";
	}
	/**
	 *原收款人银行行号 Getter S_PAYEEACCTBANKNO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeeacctbankno(){
		 return "VARCHAR";
	}
	/**
	 *退款金额 Getter N_PAYAMT   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNpayamt(){
		 return "DECIMAL";
	}
	/**
	 *退款原因 Getter S_REMARK   	 
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
	/**
	 *预留字段3 Getter S_HOLD3   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeShold3(){
		 return "VARCHAR";
	}
	/**
	 *预留字段4 Getter S_HOLD4   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeShold4(){
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
	/******************************************************
	*
	*  Get Column With
	*
	*******************************************************/
	/**
	 *退款明细ID Getter S_ID   	 
	 */
	public static int columnWidthSid(){
		 return 38;
	}
	/**
	 *收款银行退款通知ID Getter S_VOUCHERBILLID   	 
	 */
	public static int columnWidthSvoucherbillid(){
		 return 38;
	}
	/**
	 *原付款人账号 Getter S_PAYACCTNO   	 
	 */
	public static int columnWidthSpayacctno(){
		 return 42;
	}
	/**
	 *原付款人名称 Getter S_PAYACCTNAME   	 
	 */
	public static int columnWidthSpayacctname(){
		 return 120;
	}
	/**
	 *原付款人银行 Getter S_PAYACCTBANKNAME   	 
	 */
	public static int columnWidthSpayacctbankname(){
		 return 60;
	}
	/**
	 *原收款人账号 Getter S_PAYEEACCTNO   	 
	 */
	public static int columnWidthSpayeeacctno(){
		 return 42;
	}
	/**
	 *原收款人名称 Getter S_PAYEEACCTNAME   	 
	 */
	public static int columnWidthSpayeeacctname(){
		 return 120;
	}
	/**
	 *原收款人银行 Getter S_PAYEEACCTBANKNAME   	 
	 */
	public static int columnWidthSpayeeacctbankname(){
		 return 60;
	}
	/**
	 *原收款人银行行号 Getter S_PAYEEACCTBANKNO   	 
	 */
	public static int columnWidthSpayeeacctbankno(){
		 return 42;
	}
	/**
	 *退款原因 Getter S_REMARK   	 
	 */
	public static int columnWidthSremark(){
		 return 255;
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
	 *预留字段3 Getter S_HOLD3   	 
	 */
	public static int columnWidthShold3(){
		 return 42;
	}
	/**
	 *预留字段4 Getter S_HOLD4   	 
	 */
	public static int columnWidthShold4(){
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
}
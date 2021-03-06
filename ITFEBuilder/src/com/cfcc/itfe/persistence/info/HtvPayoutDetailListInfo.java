      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: HTV_PAYOUT_DETAIL_LIST</p>
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

public class HtvPayoutDetailListInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "HTV_PAYOUT_DETAIL_LIST";
	
	/**
	 * column S_ID
	 */
	public static String S_ID ="S_ID";
	/**
	 * column S_VOUCHERBILLID
	 */
	public static String S_VOUCHERBILLID ="S_VOUCHERBILLID";
	/**
	 * column S_PAYVOUCHERNO
	 */
	public static String S_PAYVOUCHERNO ="S_PAYVOUCHERNO";
	/**
	 * column S_FUNDTYPECODE
	 */
	public static String S_FUNDTYPECODE ="S_FUNDTYPECODE";
	/**
	 * column S_FUNDTYPENAME
	 */
	public static String S_FUNDTYPENAME ="S_FUNDTYPENAME";
	/**
	 * column S_PAYTYPECODE
	 */
	public static String S_PAYTYPECODE ="S_PAYTYPECODE";
	/**
	 * column S_PAYTYPENAME
	 */
	public static String S_PAYTYPENAME ="S_PAYTYPENAME";
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
	 * column S_AGENCYCODE
	 */
	public static String S_AGENCYCODE ="S_AGENCYCODE";
	/**
	 * column S_AGENCYNAME
	 */
	public static String S_AGENCYNAME ="S_AGENCYNAME";
	/**
	 * column S_EXPFUNCCODE
	 */
	public static String S_EXPFUNCCODE ="S_EXPFUNCCODE";
	/**
	 * column S_EXPFUNCNAME
	 */
	public static String S_EXPFUNCNAME ="S_EXPFUNCNAME";
	/**
	 * column S_EXPECOCODE
	 */
	public static String S_EXPECOCODE ="S_EXPECOCODE";
	/**
	 * column S_EXPECONAME
	 */
	public static String S_EXPECONAME ="S_EXPECONAME";
	/**
	 * column S_PAYSUMMARYCODE
	 */
	public static String S_PAYSUMMARYCODE ="S_PAYSUMMARYCODE";
	/**
	 * column S_PAYSUMMARYNAME
	 */
	public static String S_PAYSUMMARYNAME ="S_PAYSUMMARYNAME";
	/**
	 * column N_PAYAMT
	 */
	public static String N_PAYAMT ="N_PAYAMT";
	/**
	 * column D_PAYDATE
	 */
	public static String D_PAYDATE ="D_PAYDATE";
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
	 * @return String table name of dto
	 */
	public static String getTableName() {
		return TABLENAME;
	}
	
	/**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[28];        
        columnNames[0]="S_ID";
        columnNames[1]="S_VOUCHERBILLID";
        columnNames[2]="S_PAYVOUCHERNO";
        columnNames[3]="S_FUNDTYPECODE";
        columnNames[4]="S_FUNDTYPENAME";
        columnNames[5]="S_PAYTYPECODE";
        columnNames[6]="S_PAYTYPENAME";
        columnNames[7]="S_PAYEEACCTNO";
        columnNames[8]="S_PAYEEACCTNAME";
        columnNames[9]="S_PAYEEACCTBANKNAME";
        columnNames[10]="S_PAYEEACCTBANKNO";
        columnNames[11]="S_PAYACCTNO";
        columnNames[12]="S_PAYACCTNAME";
        columnNames[13]="S_PAYACCTBANKNAME";
        columnNames[14]="S_AGENCYCODE";
        columnNames[15]="S_AGENCYNAME";
        columnNames[16]="S_EXPFUNCCODE";
        columnNames[17]="S_EXPFUNCNAME";
        columnNames[18]="S_EXPECOCODE";
        columnNames[19]="S_EXPECONAME";
        columnNames[20]="S_PAYSUMMARYCODE";
        columnNames[21]="S_PAYSUMMARYNAME";
        columnNames[22]="N_PAYAMT";
        columnNames[23]="D_PAYDATE";
        columnNames[24]="S_HOLD1";
        columnNames[25]="S_HOLD2";
        columnNames[26]="S_HOLD3";
        columnNames[27]="S_HOLD4";
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
	 *拨款凭证清单ID Getter S_VOUCHERBILLID , NOT NULL   	 
	 */
	public static String columnSvoucherbillid(){
		 return S_VOUCHERBILLID;
	}
	/**
	 *拨款凭证凭证号 Getter S_PAYVOUCHERNO , NOT NULL   	 
	 */
	public static String columnSpayvoucherno(){
		 return S_PAYVOUCHERNO;
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
	 *支付方式编码 Getter S_PAYTYPECODE , NOT NULL   	 
	 */
	public static String columnSpaytypecode(){
		 return S_PAYTYPECODE;
	}
	/**
	 *支付方式名称 Getter S_PAYTYPENAME , NOT NULL   	 
	 */
	public static String columnSpaytypename(){
		 return S_PAYTYPENAME;
	}
	/**
	 *收款人账号 Getter S_PAYEEACCTNO , NOT NULL   	 
	 */
	public static String columnSpayeeacctno(){
		 return S_PAYEEACCTNO;
	}
	/**
	 *收款人名称 Getter S_PAYEEACCTNAME , NOT NULL   	 
	 */
	public static String columnSpayeeacctname(){
		 return S_PAYEEACCTNAME;
	}
	/**
	 *收款人银行 Getter S_PAYEEACCTBANKNAME , NOT NULL   	 
	 */
	public static String columnSpayeeacctbankname(){
		 return S_PAYEEACCTBANKNAME;
	}
	/**
	 *收款银行行号(人行补填) Getter S_PAYEEACCTBANKNO   	 
	 */
	public static String columnSpayeeacctbankno(){
		 return S_PAYEEACCTBANKNO;
	}
	/**
	 *付款人账号 Getter S_PAYACCTNO , NOT NULL   	 
	 */
	public static String columnSpayacctno(){
		 return S_PAYACCTNO;
	}
	/**
	 *付款人名称 Getter S_PAYACCTNAME , NOT NULL   	 
	 */
	public static String columnSpayacctname(){
		 return S_PAYACCTNAME;
	}
	/**
	 *付款人银行 Getter S_PAYACCTBANKNAME , NOT NULL   	 
	 */
	public static String columnSpayacctbankname(){
		 return S_PAYACCTBANKNAME;
	}
	/**
	 *预算单位编码 Getter S_AGENCYCODE , NOT NULL   	 
	 */
	public static String columnSagencycode(){
		 return S_AGENCYCODE;
	}
	/**
	 *预算单位名称 Getter S_AGENCYNAME , NOT NULL   	 
	 */
	public static String columnSagencyname(){
		 return S_AGENCYNAME;
	}
	/**
	 *支出功能分类科目编码 Getter S_EXPFUNCCODE , NOT NULL   	 
	 */
	public static String columnSexpfunccode(){
		 return S_EXPFUNCCODE;
	}
	/**
	 *支持功能分类科目名称 Getter S_EXPFUNCNAME , NOT NULL   	 
	 */
	public static String columnSexpfuncname(){
		 return S_EXPFUNCNAME;
	}
	/**
	 *支出经济分类科目编码 Getter S_EXPECOCODE   	 
	 */
	public static String columnSexpecocode(){
		 return S_EXPECOCODE;
	}
	/**
	 *支出经济分类科目名称 Getter S_EXPECONAME   	 
	 */
	public static String columnSexpeconame(){
		 return S_EXPECONAME;
	}
	/**
	 *用途编码 Getter S_PAYSUMMARYCODE   	 
	 */
	public static String columnSpaysummarycode(){
		 return S_PAYSUMMARYCODE;
	}
	/**
	 *用途名称 Getter S_PAYSUMMARYNAME   	 
	 */
	public static String columnSpaysummaryname(){
		 return S_PAYSUMMARYNAME;
	}
	/**
	 *支付金额 Getter N_PAYAMT , NOT NULL   	 
	 */
	public static String columnNpayamt(){
		 return N_PAYAMT;
	}
	/**
	 *拨款日期 Getter D_PAYDATE , NOT NULL   	 
	 */
	public static String columnDpaydate(){
		 return D_PAYDATE;
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
	 *拨款凭证清单ID Getter S_VOUCHERBILLID , NOT NULL   	 
	 */
	public static String columnJavaTypeSvoucherbillid(){
		 return "String";
	}
	/**
	 *拨款凭证凭证号 Getter S_PAYVOUCHERNO , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayvoucherno(){
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
	 *支付方式编码 Getter S_PAYTYPECODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSpaytypecode(){
		 return "String";
	}
	/**
	 *支付方式名称 Getter S_PAYTYPENAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSpaytypename(){
		 return "String";
	}
	/**
	 *收款人账号 Getter S_PAYEEACCTNO , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayeeacctno(){
		 return "String";
	}
	/**
	 *收款人名称 Getter S_PAYEEACCTNAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayeeacctname(){
		 return "String";
	}
	/**
	 *收款人银行 Getter S_PAYEEACCTBANKNAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayeeacctbankname(){
		 return "String";
	}
	/**
	 *收款银行行号(人行补填) Getter S_PAYEEACCTBANKNO   	 
	 */
	public static String columnJavaTypeSpayeeacctbankno(){
		 return "String";
	}
	/**
	 *付款人账号 Getter S_PAYACCTNO , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayacctno(){
		 return "String";
	}
	/**
	 *付款人名称 Getter S_PAYACCTNAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayacctname(){
		 return "String";
	}
	/**
	 *付款人银行 Getter S_PAYACCTBANKNAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayacctbankname(){
		 return "String";
	}
	/**
	 *预算单位编码 Getter S_AGENCYCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSagencycode(){
		 return "String";
	}
	/**
	 *预算单位名称 Getter S_AGENCYNAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSagencyname(){
		 return "String";
	}
	/**
	 *支出功能分类科目编码 Getter S_EXPFUNCCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSexpfunccode(){
		 return "String";
	}
	/**
	 *支持功能分类科目名称 Getter S_EXPFUNCNAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSexpfuncname(){
		 return "String";
	}
	/**
	 *支出经济分类科目编码 Getter S_EXPECOCODE   	 
	 */
	public static String columnJavaTypeSexpecocode(){
		 return "String";
	}
	/**
	 *支出经济分类科目名称 Getter S_EXPECONAME   	 
	 */
	public static String columnJavaTypeSexpeconame(){
		 return "String";
	}
	/**
	 *用途编码 Getter S_PAYSUMMARYCODE   	 
	 */
	public static String columnJavaTypeSpaysummarycode(){
		 return "String";
	}
	/**
	 *用途名称 Getter S_PAYSUMMARYNAME   	 
	 */
	public static String columnJavaTypeSpaysummaryname(){
		 return "String";
	}
	/**
	 *支付金额 Getter N_PAYAMT , NOT NULL   	 
	 */
	public static String columnJavaTypeNpayamt(){
		 return "BigDecimal";
	}
	/**
	 *拨款日期 Getter D_PAYDATE , NOT NULL   	 
	 */
	public static String columnJavaTypeDpaydate(){
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
	 *拨款凭证清单ID Getter S_VOUCHERBILLID , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSvoucherbillid(){
		 return "VARCHAR";
	}
	/**
	 *拨款凭证凭证号 Getter S_PAYVOUCHERNO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayvoucherno(){
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
	 *支付方式编码 Getter S_PAYTYPECODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpaytypecode(){
		 return "VARCHAR";
	}
	/**
	 *支付方式名称 Getter S_PAYTYPENAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpaytypename(){
		 return "VARCHAR";
	}
	/**
	 *收款人账号 Getter S_PAYEEACCTNO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeeacctno(){
		 return "VARCHAR";
	}
	/**
	 *收款人名称 Getter S_PAYEEACCTNAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeeacctname(){
		 return "VARCHAR";
	}
	/**
	 *收款人银行 Getter S_PAYEEACCTBANKNAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeeacctbankname(){
		 return "VARCHAR";
	}
	/**
	 *收款银行行号(人行补填) Getter S_PAYEEACCTBANKNO   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSpayeeacctbankno(){
		 return "CHARACTER";
	}
	/**
	 *付款人账号 Getter S_PAYACCTNO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayacctno(){
		 return "VARCHAR";
	}
	/**
	 *付款人名称 Getter S_PAYACCTNAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayacctname(){
		 return "VARCHAR";
	}
	/**
	 *付款人银行 Getter S_PAYACCTBANKNAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayacctbankname(){
		 return "VARCHAR";
	}
	/**
	 *预算单位编码 Getter S_AGENCYCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSagencycode(){
		 return "VARCHAR";
	}
	/**
	 *预算单位名称 Getter S_AGENCYNAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSagencyname(){
		 return "VARCHAR";
	}
	/**
	 *支出功能分类科目编码 Getter S_EXPFUNCCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSexpfunccode(){
		 return "VARCHAR";
	}
	/**
	 *支持功能分类科目名称 Getter S_EXPFUNCNAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSexpfuncname(){
		 return "VARCHAR";
	}
	/**
	 *支出经济分类科目编码 Getter S_EXPECOCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSexpecocode(){
		 return "VARCHAR";
	}
	/**
	 *支出经济分类科目名称 Getter S_EXPECONAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSexpeconame(){
		 return "VARCHAR";
	}
	/**
	 *用途编码 Getter S_PAYSUMMARYCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpaysummarycode(){
		 return "VARCHAR";
	}
	/**
	 *用途名称 Getter S_PAYSUMMARYNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpaysummaryname(){
		 return "VARCHAR";
	}
	/**
	 *支付金额 Getter N_PAYAMT , NOT NULL   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNpayamt(){
		 return "DECIMAL";
	}
	/**
	 *拨款日期 Getter D_PAYDATE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeDpaydate(){
		 return "CHARACTER";
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
	 *拨款凭证清单ID Getter S_VOUCHERBILLID , NOT NULL   	 
	 */
	public static int columnWidthSvoucherbillid(){
		 return 38;
	}
	/**
	 *拨款凭证凭证号 Getter S_PAYVOUCHERNO , NOT NULL   	 
	 */
	public static int columnWidthSpayvoucherno(){
		 return 42;
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
	 *支付方式编码 Getter S_PAYTYPECODE , NOT NULL   	 
	 */
	public static int columnWidthSpaytypecode(){
		 return 42;
	}
	/**
	 *支付方式名称 Getter S_PAYTYPENAME , NOT NULL   	 
	 */
	public static int columnWidthSpaytypename(){
		 return 60;
	}
	/**
	 *收款人账号 Getter S_PAYEEACCTNO , NOT NULL   	 
	 */
	public static int columnWidthSpayeeacctno(){
		 return 42;
	}
	/**
	 *收款人名称 Getter S_PAYEEACCTNAME , NOT NULL   	 
	 */
	public static int columnWidthSpayeeacctname(){
		 return 120;
	}
	/**
	 *收款人银行 Getter S_PAYEEACCTBANKNAME , NOT NULL   	 
	 */
	public static int columnWidthSpayeeacctbankname(){
		 return 60;
	}
	/**
	 *收款银行行号(人行补填) Getter S_PAYEEACCTBANKNO   	 
	 */
	public static int columnWidthSpayeeacctbankno(){
		 return 12;
	}
	/**
	 *付款人账号 Getter S_PAYACCTNO , NOT NULL   	 
	 */
	public static int columnWidthSpayacctno(){
		 return 42;
	}
	/**
	 *付款人名称 Getter S_PAYACCTNAME , NOT NULL   	 
	 */
	public static int columnWidthSpayacctname(){
		 return 120;
	}
	/**
	 *付款人银行 Getter S_PAYACCTBANKNAME , NOT NULL   	 
	 */
	public static int columnWidthSpayacctbankname(){
		 return 60;
	}
	/**
	 *预算单位编码 Getter S_AGENCYCODE , NOT NULL   	 
	 */
	public static int columnWidthSagencycode(){
		 return 42;
	}
	/**
	 *预算单位名称 Getter S_AGENCYNAME , NOT NULL   	 
	 */
	public static int columnWidthSagencyname(){
		 return 60;
	}
	/**
	 *支出功能分类科目编码 Getter S_EXPFUNCCODE , NOT NULL   	 
	 */
	public static int columnWidthSexpfunccode(){
		 return 42;
	}
	/**
	 *支持功能分类科目名称 Getter S_EXPFUNCNAME , NOT NULL   	 
	 */
	public static int columnWidthSexpfuncname(){
		 return 60;
	}
	/**
	 *支出经济分类科目编码 Getter S_EXPECOCODE   	 
	 */
	public static int columnWidthSexpecocode(){
		 return 42;
	}
	/**
	 *支出经济分类科目名称 Getter S_EXPECONAME   	 
	 */
	public static int columnWidthSexpeconame(){
		 return 60;
	}
	/**
	 *用途编码 Getter S_PAYSUMMARYCODE   	 
	 */
	public static int columnWidthSpaysummarycode(){
		 return 42;
	}
	/**
	 *用途名称 Getter S_PAYSUMMARYNAME   	 
	 */
	public static int columnWidthSpaysummaryname(){
		 return 200;
	}
	/**
	 *拨款日期 Getter D_PAYDATE , NOT NULL   	 
	 */
	public static int columnWidthDpaydate(){
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
}
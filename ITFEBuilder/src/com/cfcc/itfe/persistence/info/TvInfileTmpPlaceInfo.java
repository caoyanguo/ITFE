      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TV_INFILE_TMP_PLACE</p>
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

public class TvInfileTmpPlaceInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TV_INFILE_TMP_PLACE";
	
	/**
	 * column S_OPERBATCH
	 */
	public static String S_OPERBATCH ="S_OPERBATCH";
	/**
	 * column S_OPERDATE
	 */
	public static String S_OPERDATE ="S_OPERDATE";
	/**
	 * column S_FLAGNO
	 */
	public static String S_FLAGNO ="S_FLAGNO";
	/**
	 * column S_OPERCODE
	 */
	public static String S_OPERCODE ="S_OPERCODE";
	/**
	 * column S_SEQNO
	 */
	public static String S_SEQNO ="S_SEQNO";
	/**
	 * column S_CORPCODE
	 */
	public static String S_CORPCODE ="S_CORPCODE";
	/**
	 * column S_CORPNAME
	 */
	public static String S_CORPNAME ="S_CORPNAME";
	/**
	 * column S_TAXSTARTDATE
	 */
	public static String S_TAXSTARTDATE ="S_TAXSTARTDATE";
	/**
	 * column S_TAXENDDATE
	 */
	public static String S_TAXENDDATE ="S_TAXENDDATE";
	/**
	 * column S_TAXTYPECODE
	 */
	public static String S_TAXTYPECODE ="S_TAXTYPECODE";
	/**
	 * column S_TAXTYPENAME
	 */
	public static String S_TAXTYPENAME ="S_TAXTYPENAME";
	/**
	 * column S_TAXSUBJECTNAME
	 */
	public static String S_TAXSUBJECTNAME ="S_TAXSUBJECTNAME";
	/**
	 * column I_TAXNUMBER
	 */
	public static String I_TAXNUMBER ="I_TAXNUMBER";
	/**
	 * column N_TAXAMT
	 */
	public static String N_TAXAMT ="N_TAXAMT";
	/**
	 * column N_DISCOUNTTAXAMT
	 */
	public static String N_DISCOUNTTAXAMT ="N_DISCOUNTTAXAMT";
	/**
	 * column N_TAXRATE
	 */
	public static String N_TAXRATE ="N_TAXRATE";
	/**
	 * column N_FACTTAXAMT
	 */
	public static String N_FACTTAXAMT ="N_FACTTAXAMT";
	/**
	 * column C_FLAG
	 */
	public static String C_FLAG ="C_FLAG";
	/**
	 * column S_BILLDATE
	 */
	public static String S_BILLDATE ="S_BILLDATE";
	/**
	 * column S_LIMITDATE
	 */
	public static String S_LIMITDATE ="S_LIMITDATE";
	/**
	 * column S_BUDGETSUBJECTCODE
	 */
	public static String S_BUDGETSUBJECTCODE ="S_BUDGETSUBJECTCODE";
	/**
	 * column S_BUDGETLEVELCODE
	 */
	public static String S_BUDGETLEVELCODE ="S_BUDGETLEVELCODE";
	/**
	 * column S_PAYBNKNO
	 */
	public static String S_PAYBNKNO ="S_PAYBNKNO";
	/**
	 * column S_PAYACCT
	 */
	public static String S_PAYACCT ="S_PAYACCT";
	/**
	 * column S_ENCOCODE
	 */
	public static String S_ENCOCODE ="S_ENCOCODE";
	/**
	 * column S_REFCODE
	 */
	public static String S_REFCODE ="S_REFCODE";
	/**
	 * column S_TRECODE
	 */
	public static String S_TRECODE ="S_TRECODE";
	/**
	 * column S_TAXORGCODE
	 */
	public static String S_TAXORGCODE ="S_TAXORGCODE";
	/**
	 * column S_TAXORGNAME
	 */
	public static String S_TAXORGNAME ="S_TAXORGNAME";
	/**
	 * column S_TAXVOUNO
	 */
	public static String S_TAXVOUNO ="S_TAXVOUNO";
	/**
	 * column S_TCBSTAXORGCODE
	 */
	public static String S_TCBSTAXORGCODE ="S_TCBSTAXORGCODE";
	/**
	 * column S_PAYOPBNKNO
	 */
	public static String S_PAYOPBNKNO ="S_PAYOPBNKNO";
	/**
	 * column S_PAYOPBNKNAME
	 */
	public static String S_PAYOPBNKNAME ="S_PAYOPBNKNAME";
	/**
	 * column S_TCBSTRECODE
	 */
	public static String S_TCBSTRECODE ="S_TCBSTRECODE";
	/**
	 * column S_ASSITSIGN
	 */
	public static String S_ASSITSIGN ="S_ASSITSIGN";
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
        String[] columnNames = new String[35];        
        columnNames[0]="S_OPERBATCH";
        columnNames[1]="S_OPERDATE";
        columnNames[2]="S_FLAGNO";
        columnNames[3]="S_OPERCODE";
        columnNames[4]="S_SEQNO";
        columnNames[5]="S_CORPCODE";
        columnNames[6]="S_CORPNAME";
        columnNames[7]="S_TAXSTARTDATE";
        columnNames[8]="S_TAXENDDATE";
        columnNames[9]="S_TAXTYPECODE";
        columnNames[10]="S_TAXTYPENAME";
        columnNames[11]="S_TAXSUBJECTNAME";
        columnNames[12]="I_TAXNUMBER";
        columnNames[13]="N_TAXAMT";
        columnNames[14]="N_DISCOUNTTAXAMT";
        columnNames[15]="N_TAXRATE";
        columnNames[16]="N_FACTTAXAMT";
        columnNames[17]="C_FLAG";
        columnNames[18]="S_BILLDATE";
        columnNames[19]="S_LIMITDATE";
        columnNames[20]="S_BUDGETSUBJECTCODE";
        columnNames[21]="S_BUDGETLEVELCODE";
        columnNames[22]="S_PAYBNKNO";
        columnNames[23]="S_PAYACCT";
        columnNames[24]="S_ENCOCODE";
        columnNames[25]="S_REFCODE";
        columnNames[26]="S_TRECODE";
        columnNames[27]="S_TAXORGCODE";
        columnNames[28]="S_TAXORGNAME";
        columnNames[29]="S_TAXVOUNO";
        columnNames[30]="S_TCBSTAXORGCODE";
        columnNames[31]="S_PAYOPBNKNO";
        columnNames[32]="S_PAYOPBNKNAME";
        columnNames[33]="S_TCBSTRECODE";
        columnNames[34]="S_ASSITSIGN";
        return columnNames;     
    }
	/******************************************************
	*
	*  Get Column Name
	*
	*******************************************************/
	/**
	 * Getter S_OPERBATCH , NOT NULL   	 
	 */
	public static String columnSoperbatch(){
		 return S_OPERBATCH;
	}
	/**
	 * Getter S_OPERDATE   	 
	 */
	public static String columnSoperdate(){
		 return S_OPERDATE;
	}
	/**
	 * Getter S_FLAGNO , NOT NULL   	 
	 */
	public static String columnSflagno(){
		 return S_FLAGNO;
	}
	/**
	 * Getter S_OPERCODE   	 
	 */
	public static String columnSopercode(){
		 return S_OPERCODE;
	}
	/**
	 * Getter S_SEQNO , NOT NULL   	 
	 */
	public static String columnSseqno(){
		 return S_SEQNO;
	}
	/**
	 * Getter S_CORPCODE   	 
	 */
	public static String columnScorpcode(){
		 return S_CORPCODE;
	}
	/**
	 * Getter S_CORPNAME   	 
	 */
	public static String columnScorpname(){
		 return S_CORPNAME;
	}
	/**
	 * Getter S_TAXSTARTDATE   	 
	 */
	public static String columnStaxstartdate(){
		 return S_TAXSTARTDATE;
	}
	/**
	 * Getter S_TAXENDDATE   	 
	 */
	public static String columnStaxenddate(){
		 return S_TAXENDDATE;
	}
	/**
	 * Getter S_TAXTYPECODE   	 
	 */
	public static String columnStaxtypecode(){
		 return S_TAXTYPECODE;
	}
	/**
	 * Getter S_TAXTYPENAME   	 
	 */
	public static String columnStaxtypename(){
		 return S_TAXTYPENAME;
	}
	/**
	 * Getter S_TAXSUBJECTNAME   	 
	 */
	public static String columnStaxsubjectname(){
		 return S_TAXSUBJECTNAME;
	}
	/**
	 * Getter I_TAXNUMBER   	 
	 */
	public static String columnItaxnumber(){
		 return I_TAXNUMBER;
	}
	/**
	 * Getter N_TAXAMT   	 
	 */
	public static String columnNtaxamt(){
		 return N_TAXAMT;
	}
	/**
	 * Getter N_DISCOUNTTAXAMT   	 
	 */
	public static String columnNdiscounttaxamt(){
		 return N_DISCOUNTTAXAMT;
	}
	/**
	 * Getter N_TAXRATE   	 
	 */
	public static String columnNtaxrate(){
		 return N_TAXRATE;
	}
	/**
	 * Getter N_FACTTAXAMT   	 
	 */
	public static String columnNfacttaxamt(){
		 return N_FACTTAXAMT;
	}
	/**
	 * Getter C_FLAG   	 
	 */
	public static String columnCflag(){
		 return C_FLAG;
	}
	/**
	 * Getter S_BILLDATE   	 
	 */
	public static String columnSbilldate(){
		 return S_BILLDATE;
	}
	/**
	 * Getter S_LIMITDATE   	 
	 */
	public static String columnSlimitdate(){
		 return S_LIMITDATE;
	}
	/**
	 * Getter S_BUDGETSUBJECTCODE   	 
	 */
	public static String columnSbudgetsubjectcode(){
		 return S_BUDGETSUBJECTCODE;
	}
	/**
	 * Getter S_BUDGETLEVELCODE   	 
	 */
	public static String columnSbudgetlevelcode(){
		 return S_BUDGETLEVELCODE;
	}
	/**
	 * Getter S_PAYBNKNO   	 
	 */
	public static String columnSpaybnkno(){
		 return S_PAYBNKNO;
	}
	/**
	 * Getter S_PAYACCT   	 
	 */
	public static String columnSpayacct(){
		 return S_PAYACCT;
	}
	/**
	 * Getter S_ENCOCODE   	 
	 */
	public static String columnSencocode(){
		 return S_ENCOCODE;
	}
	/**
	 * Getter S_REFCODE   	 
	 */
	public static String columnSrefcode(){
		 return S_REFCODE;
	}
	/**
	 * Getter S_TRECODE , NOT NULL   	 
	 */
	public static String columnStrecode(){
		 return S_TRECODE;
	}
	/**
	 * Getter S_TAXORGCODE , NOT NULL   	 
	 */
	public static String columnStaxorgcode(){
		 return S_TAXORGCODE;
	}
	/**
	 * Getter S_TAXORGNAME   	 
	 */
	public static String columnStaxorgname(){
		 return S_TAXORGNAME;
	}
	/**
	 * Getter S_TAXVOUNO   	 
	 */
	public static String columnStaxvouno(){
		 return S_TAXVOUNO;
	}
	/**
	 * Getter S_TCBSTAXORGCODE   	 
	 */
	public static String columnStcbstaxorgcode(){
		 return S_TCBSTAXORGCODE;
	}
	/**
	 * Getter S_PAYOPBNKNO   	 
	 */
	public static String columnSpayopbnkno(){
		 return S_PAYOPBNKNO;
	}
	/**
	 * Getter S_PAYOPBNKNAME   	 
	 */
	public static String columnSpayopbnkname(){
		 return S_PAYOPBNKNAME;
	}
	/**
	 * Getter S_TCBSTRECODE   	 
	 */
	public static String columnStcbstrecode(){
		 return S_TCBSTRECODE;
	}
	/**
	 * Getter S_ASSITSIGN   	 
	 */
	public static String columnSassitsign(){
		 return S_ASSITSIGN;
	}
	/******************************************************
	*
	*  Get Column Java Type
	*
	*******************************************************/
	/**
	 * Getter S_OPERBATCH , NOT NULL   	 
	 */
	public static String columnJavaTypeSoperbatch(){
		 return "String";
	}
	/**
	 * Getter S_OPERDATE   	 
	 */
	public static String columnJavaTypeSoperdate(){
		 return "String";
	}
	/**
	 * Getter S_FLAGNO , NOT NULL   	 
	 */
	public static String columnJavaTypeSflagno(){
		 return "String";
	}
	/**
	 * Getter S_OPERCODE   	 
	 */
	public static String columnJavaTypeSopercode(){
		 return "String";
	}
	/**
	 * Getter S_SEQNO , NOT NULL   	 
	 */
	public static String columnJavaTypeSseqno(){
		 return "String";
	}
	/**
	 * Getter S_CORPCODE   	 
	 */
	public static String columnJavaTypeScorpcode(){
		 return "String";
	}
	/**
	 * Getter S_CORPNAME   	 
	 */
	public static String columnJavaTypeScorpname(){
		 return "String";
	}
	/**
	 * Getter S_TAXSTARTDATE   	 
	 */
	public static String columnJavaTypeStaxstartdate(){
		 return "String";
	}
	/**
	 * Getter S_TAXENDDATE   	 
	 */
	public static String columnJavaTypeStaxenddate(){
		 return "String";
	}
	/**
	 * Getter S_TAXTYPECODE   	 
	 */
	public static String columnJavaTypeStaxtypecode(){
		 return "String";
	}
	/**
	 * Getter S_TAXTYPENAME   	 
	 */
	public static String columnJavaTypeStaxtypename(){
		 return "String";
	}
	/**
	 * Getter S_TAXSUBJECTNAME   	 
	 */
	public static String columnJavaTypeStaxsubjectname(){
		 return "String";
	}
	/**
	 * Getter I_TAXNUMBER   	 
	 */
	public static String columnJavaTypeItaxnumber(){
		 return "Integer";
	}
	/**
	 * Getter N_TAXAMT   	 
	 */
	public static String columnJavaTypeNtaxamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter N_DISCOUNTTAXAMT   	 
	 */
	public static String columnJavaTypeNdiscounttaxamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter N_TAXRATE   	 
	 */
	public static String columnJavaTypeNtaxrate(){
		 return "BigDecimal";
	}
	/**
	 * Getter N_FACTTAXAMT   	 
	 */
	public static String columnJavaTypeNfacttaxamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter C_FLAG   	 
	 */
	public static String columnJavaTypeCflag(){
		 return "String";
	}
	/**
	 * Getter S_BILLDATE   	 
	 */
	public static String columnJavaTypeSbilldate(){
		 return "String";
	}
	/**
	 * Getter S_LIMITDATE   	 
	 */
	public static String columnJavaTypeSlimitdate(){
		 return "String";
	}
	/**
	 * Getter S_BUDGETSUBJECTCODE   	 
	 */
	public static String columnJavaTypeSbudgetsubjectcode(){
		 return "String";
	}
	/**
	 * Getter S_BUDGETLEVELCODE   	 
	 */
	public static String columnJavaTypeSbudgetlevelcode(){
		 return "String";
	}
	/**
	 * Getter S_PAYBNKNO   	 
	 */
	public static String columnJavaTypeSpaybnkno(){
		 return "String";
	}
	/**
	 * Getter S_PAYACCT   	 
	 */
	public static String columnJavaTypeSpayacct(){
		 return "String";
	}
	/**
	 * Getter S_ENCOCODE   	 
	 */
	public static String columnJavaTypeSencocode(){
		 return "String";
	}
	/**
	 * Getter S_REFCODE   	 
	 */
	public static String columnJavaTypeSrefcode(){
		 return "String";
	}
	/**
	 * Getter S_TRECODE , NOT NULL   	 
	 */
	public static String columnJavaTypeStrecode(){
		 return "String";
	}
	/**
	 * Getter S_TAXORGCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeStaxorgcode(){
		 return "String";
	}
	/**
	 * Getter S_TAXORGNAME   	 
	 */
	public static String columnJavaTypeStaxorgname(){
		 return "String";
	}
	/**
	 * Getter S_TAXVOUNO   	 
	 */
	public static String columnJavaTypeStaxvouno(){
		 return "String";
	}
	/**
	 * Getter S_TCBSTAXORGCODE   	 
	 */
	public static String columnJavaTypeStcbstaxorgcode(){
		 return "String";
	}
	/**
	 * Getter S_PAYOPBNKNO   	 
	 */
	public static String columnJavaTypeSpayopbnkno(){
		 return "String";
	}
	/**
	 * Getter S_PAYOPBNKNAME   	 
	 */
	public static String columnJavaTypeSpayopbnkname(){
		 return "String";
	}
	/**
	 * Getter S_TCBSTRECODE   	 
	 */
	public static String columnJavaTypeStcbstrecode(){
		 return "String";
	}
	/**
	 * Getter S_ASSITSIGN   	 
	 */
	public static String columnJavaTypeSassitsign(){
		 return "String";
	}
	/******************************************************
	*
	*  Get Column Database Type
	*
	*******************************************************/
	/**
	 * Getter S_OPERBATCH , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSoperbatch(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_OPERDATE   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSoperdate(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_FLAGNO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSflagno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_OPERCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSopercode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_SEQNO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSseqno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CORPCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeScorpcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CORPNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeScorpname(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_TAXSTARTDATE   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeStaxstartdate(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_TAXENDDATE   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeStaxenddate(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_TAXTYPECODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStaxtypecode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_TAXTYPENAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStaxtypename(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_TAXSUBJECTNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStaxsubjectname(){
		 return "VARCHAR";
	}
	/**
	 * Getter I_TAXNUMBER   	 
	 * columnType is INTEGER
	 */
	public static String columnDatabaseTypeItaxnumber(){
		 return "INTEGER";
	}
	/**
	 * Getter N_TAXAMT   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNtaxamt(){
		 return "DECIMAL";
	}
	/**
	 * Getter N_DISCOUNTTAXAMT   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNdiscounttaxamt(){
		 return "DECIMAL";
	}
	/**
	 * Getter N_TAXRATE   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNtaxrate(){
		 return "DECIMAL";
	}
	/**
	 * Getter N_FACTTAXAMT   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNfacttaxamt(){
		 return "DECIMAL";
	}
	/**
	 * Getter C_FLAG   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCflag(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_BILLDATE   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSbilldate(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_LIMITDATE   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSlimitdate(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_BUDGETSUBJECTCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSbudgetsubjectcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_BUDGETLEVELCODE   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSbudgetlevelcode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_PAYBNKNO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpaybnkno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYACCT   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayacct(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ENCOCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSencocode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_REFCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSrefcode(){
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
	 * Getter S_TAXORGCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStaxorgcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_TAXORGNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStaxorgname(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_TAXVOUNO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStaxvouno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_TCBSTAXORGCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStcbstaxorgcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYOPBNKNO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayopbnkno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYOPBNKNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayopbnkname(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_TCBSTRECODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStcbstrecode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ASSITSIGN   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSassitsign(){
		 return "VARCHAR";
	}
	/******************************************************
	*
	*  Get Column With
	*
	*******************************************************/
	/**
	 * Getter S_OPERBATCH , NOT NULL   	 
	 */
	public static int columnWidthSoperbatch(){
		 return 18;
	}
	/**
	 * Getter S_OPERDATE   	 
	 */
	public static int columnWidthSoperdate(){
		 return 8;
	}
	/**
	 * Getter S_FLAGNO , NOT NULL   	 
	 */
	public static int columnWidthSflagno(){
		 return 18;
	}
	/**
	 * Getter S_OPERCODE   	 
	 */
	public static int columnWidthSopercode(){
		 return 18;
	}
	/**
	 * Getter S_SEQNO , NOT NULL   	 
	 */
	public static int columnWidthSseqno(){
		 return 18;
	}
	/**
	 * Getter S_CORPCODE   	 
	 */
	public static int columnWidthScorpcode(){
		 return 20;
	}
	/**
	 * Getter S_CORPNAME   	 
	 */
	public static int columnWidthScorpname(){
		 return 200;
	}
	/**
	 * Getter S_TAXSTARTDATE   	 
	 */
	public static int columnWidthStaxstartdate(){
		 return 8;
	}
	/**
	 * Getter S_TAXENDDATE   	 
	 */
	public static int columnWidthStaxenddate(){
		 return 8;
	}
	/**
	 * Getter S_TAXTYPECODE   	 
	 */
	public static int columnWidthStaxtypecode(){
		 return 30;
	}
	/**
	 * Getter S_TAXTYPENAME   	 
	 */
	public static int columnWidthStaxtypename(){
		 return 100;
	}
	/**
	 * Getter S_TAXSUBJECTNAME   	 
	 */
	public static int columnWidthStaxsubjectname(){
		 return 100;
	}
	/**
	 * Getter C_FLAG   	 
	 */
	public static int columnWidthCflag(){
		 return 1;
	}
	/**
	 * Getter S_BILLDATE   	 
	 */
	public static int columnWidthSbilldate(){
		 return 8;
	}
	/**
	 * Getter S_LIMITDATE   	 
	 */
	public static int columnWidthSlimitdate(){
		 return 8;
	}
	/**
	 * Getter S_BUDGETSUBJECTCODE   	 
	 */
	public static int columnWidthSbudgetsubjectcode(){
		 return 30;
	}
	/**
	 * Getter S_BUDGETLEVELCODE   	 
	 */
	public static int columnWidthSbudgetlevelcode(){
		 return 1;
	}
	/**
	 * Getter S_PAYBNKNO   	 
	 */
	public static int columnWidthSpaybnkno(){
		 return 12;
	}
	/**
	 * Getter S_PAYACCT   	 
	 */
	public static int columnWidthSpayacct(){
		 return 32;
	}
	/**
	 * Getter S_ENCOCODE   	 
	 */
	public static int columnWidthSencocode(){
		 return 12;
	}
	/**
	 * Getter S_REFCODE   	 
	 */
	public static int columnWidthSrefcode(){
		 return 12;
	}
	/**
	 * Getter S_TRECODE , NOT NULL   	 
	 */
	public static int columnWidthStrecode(){
		 return 10;
	}
	/**
	 * Getter S_TAXORGCODE , NOT NULL   	 
	 */
	public static int columnWidthStaxorgcode(){
		 return 10;
	}
	/**
	 * Getter S_TAXORGNAME   	 
	 */
	public static int columnWidthStaxorgname(){
		 return 100;
	}
	/**
	 * Getter S_TAXVOUNO   	 
	 */
	public static int columnWidthStaxvouno(){
		 return 10;
	}
	/**
	 * Getter S_TCBSTAXORGCODE   	 
	 */
	public static int columnWidthStcbstaxorgcode(){
		 return 30;
	}
	/**
	 * Getter S_PAYOPBNKNO   	 
	 */
	public static int columnWidthSpayopbnkno(){
		 return 12;
	}
	/**
	 * Getter S_PAYOPBNKNAME   	 
	 */
	public static int columnWidthSpayopbnkname(){
		 return 60;
	}
	/**
	 * Getter S_TCBSTRECODE   	 
	 */
	public static int columnWidthStcbstrecode(){
		 return 10;
	}
	/**
	 * Getter S_ASSITSIGN   	 
	 */
	public static int columnWidthSassitsign(){
		 return 35;
	}
}
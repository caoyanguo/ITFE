      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TR_TAXORG_DEBT_REPORT</p>
 * <p>Description:  Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:59 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class TrTaxorgDebtReportInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TR_TAXORG_DEBT_REPORT";
	
	/**
	 * column S_TAXORGCODE
	 */
	public static String S_TAXORGCODE ="S_TAXORGCODE";
	/**
	 * column S_TRECODE
	 */
	public static String S_TRECODE ="S_TRECODE";
	/**
	 * column S_RPTDATE
	 */
	public static String S_RPTDATE ="S_RPTDATE";
	/**
	 * column S_CASHBANK
	 */
	public static String S_CASHBANK ="S_CASHBANK";
	/**
	 * column S_KINDSIGN
	 */
	public static String S_KINDSIGN ="S_KINDSIGN";
	/**
	 * column S_CORPUSSUBJECT
	 */
	public static String S_CORPUSSUBJECT ="S_CORPUSSUBJECT";
	/**
	 * column N_CORDAYAMT
	 */
	public static String N_CORDAYAMT ="N_CORDAYAMT";
	/**
	 * column N_CORTENDAYAMT
	 */
	public static String N_CORTENDAYAMT ="N_CORTENDAYAMT";
	/**
	 * column N_CORMONTHAMT
	 */
	public static String N_CORMONTHAMT ="N_CORMONTHAMT";
	/**
	 * column N_CORQUARTERAMT
	 */
	public static String N_CORQUARTERAMT ="N_CORQUARTERAMT";
	/**
	 * column N_CORYEARAMT
	 */
	public static String N_CORYEARAMT ="N_CORYEARAMT";
	/**
	 * column S_ACCRUALSUBJECT
	 */
	public static String S_ACCRUALSUBJECT ="S_ACCRUALSUBJECT";
	/**
	 * column N_ACCDAYAMT
	 */
	public static String N_ACCDAYAMT ="N_ACCDAYAMT";
	/**
	 * column N_ACCTENDAYAMT
	 */
	public static String N_ACCTENDAYAMT ="N_ACCTENDAYAMT";
	/**
	 * column N_ACCMONTHAMT
	 */
	public static String N_ACCMONTHAMT ="N_ACCMONTHAMT";
	/**
	 * column N_ACCQUARTERAMT
	 */
	public static String N_ACCQUARTERAMT ="N_ACCQUARTERAMT";
	/**
	 * column N_ACCYEARAMT
	 */
	public static String N_ACCYEARAMT ="N_ACCYEARAMT";
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
        String[] columnNames = new String[17];        
        columnNames[0]="S_TAXORGCODE";
        columnNames[1]="S_TRECODE";
        columnNames[2]="S_RPTDATE";
        columnNames[3]="S_CASHBANK";
        columnNames[4]="S_KINDSIGN";
        columnNames[5]="S_CORPUSSUBJECT";
        columnNames[6]="N_CORDAYAMT";
        columnNames[7]="N_CORTENDAYAMT";
        columnNames[8]="N_CORMONTHAMT";
        columnNames[9]="N_CORQUARTERAMT";
        columnNames[10]="N_CORYEARAMT";
        columnNames[11]="S_ACCRUALSUBJECT";
        columnNames[12]="N_ACCDAYAMT";
        columnNames[13]="N_ACCTENDAYAMT";
        columnNames[14]="N_ACCMONTHAMT";
        columnNames[15]="N_ACCQUARTERAMT";
        columnNames[16]="N_ACCYEARAMT";
        return columnNames;     
    }
	/******************************************************
	*
	*  Get Column Name
	*
	*******************************************************/
	/**
	 * Getter S_TAXORGCODE, PK , NOT NULL   	 
	 */
	public static String columnStaxorgcode(){
		 return S_TAXORGCODE;
	}
	/**
	 * Getter S_TRECODE, PK , NOT NULL   	 
	 */
	public static String columnStrecode(){
		 return S_TRECODE;
	}
	/**
	 * Getter S_RPTDATE, PK , NOT NULL   	 
	 */
	public static String columnSrptdate(){
		 return S_RPTDATE;
	}
	/**
	 * Getter S_CASHBANK, PK , NOT NULL   	 
	 */
	public static String columnScashbank(){
		 return S_CASHBANK;
	}
	/**
	 * Getter S_KINDSIGN , NOT NULL   	 
	 */
	public static String columnSkindsign(){
		 return S_KINDSIGN;
	}
	/**
	 * Getter S_CORPUSSUBJECT, PK , NOT NULL   	 
	 */
	public static String columnScorpussubject(){
		 return S_CORPUSSUBJECT;
	}
	/**
	 * Getter N_CORDAYAMT   	 
	 */
	public static String columnNcordayamt(){
		 return N_CORDAYAMT;
	}
	/**
	 * Getter N_CORTENDAYAMT   	 
	 */
	public static String columnNcortendayamt(){
		 return N_CORTENDAYAMT;
	}
	/**
	 * Getter N_CORMONTHAMT   	 
	 */
	public static String columnNcormonthamt(){
		 return N_CORMONTHAMT;
	}
	/**
	 * Getter N_CORQUARTERAMT   	 
	 */
	public static String columnNcorquarteramt(){
		 return N_CORQUARTERAMT;
	}
	/**
	 * Getter N_CORYEARAMT   	 
	 */
	public static String columnNcoryearamt(){
		 return N_CORYEARAMT;
	}
	/**
	 * Getter S_ACCRUALSUBJECT, PK , NOT NULL   	 
	 */
	public static String columnSaccrualsubject(){
		 return S_ACCRUALSUBJECT;
	}
	/**
	 * Getter N_ACCDAYAMT   	 
	 */
	public static String columnNaccdayamt(){
		 return N_ACCDAYAMT;
	}
	/**
	 * Getter N_ACCTENDAYAMT   	 
	 */
	public static String columnNacctendayamt(){
		 return N_ACCTENDAYAMT;
	}
	/**
	 * Getter N_ACCMONTHAMT   	 
	 */
	public static String columnNaccmonthamt(){
		 return N_ACCMONTHAMT;
	}
	/**
	 * Getter N_ACCQUARTERAMT   	 
	 */
	public static String columnNaccquarteramt(){
		 return N_ACCQUARTERAMT;
	}
	/**
	 * Getter N_ACCYEARAMT   	 
	 */
	public static String columnNaccyearamt(){
		 return N_ACCYEARAMT;
	}
	/******************************************************
	*
	*  Get Column Java Type
	*
	*******************************************************/
	/**
	 * Getter S_TAXORGCODE, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeStaxorgcode(){
		 return "String";
	}
	/**
	 * Getter S_TRECODE, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeStrecode(){
		 return "String";
	}
	/**
	 * Getter S_RPTDATE, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeSrptdate(){
		 return "String";
	}
	/**
	 * Getter S_CASHBANK, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeScashbank(){
		 return "String";
	}
	/**
	 * Getter S_KINDSIGN , NOT NULL   	 
	 */
	public static String columnJavaTypeSkindsign(){
		 return "String";
	}
	/**
	 * Getter S_CORPUSSUBJECT, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeScorpussubject(){
		 return "String";
	}
	/**
	 * Getter N_CORDAYAMT   	 
	 */
	public static String columnJavaTypeNcordayamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter N_CORTENDAYAMT   	 
	 */
	public static String columnJavaTypeNcortendayamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter N_CORMONTHAMT   	 
	 */
	public static String columnJavaTypeNcormonthamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter N_CORQUARTERAMT   	 
	 */
	public static String columnJavaTypeNcorquarteramt(){
		 return "BigDecimal";
	}
	/**
	 * Getter N_CORYEARAMT   	 
	 */
	public static String columnJavaTypeNcoryearamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter S_ACCRUALSUBJECT, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeSaccrualsubject(){
		 return "String";
	}
	/**
	 * Getter N_ACCDAYAMT   	 
	 */
	public static String columnJavaTypeNaccdayamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter N_ACCTENDAYAMT   	 
	 */
	public static String columnJavaTypeNacctendayamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter N_ACCMONTHAMT   	 
	 */
	public static String columnJavaTypeNaccmonthamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter N_ACCQUARTERAMT   	 
	 */
	public static String columnJavaTypeNaccquarteramt(){
		 return "BigDecimal";
	}
	/**
	 * Getter N_ACCYEARAMT   	 
	 */
	public static String columnJavaTypeNaccyearamt(){
		 return "BigDecimal";
	}
	/******************************************************
	*
	*  Get Column Database Type
	*
	*******************************************************/
	/**
	 * Getter S_TAXORGCODE, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStaxorgcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_TRECODE, PK , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeStrecode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_RPTDATE, PK , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSrptdate(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_CASHBANK, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeScashbank(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_KINDSIGN , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSkindsign(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_CORPUSSUBJECT, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeScorpussubject(){
		 return "VARCHAR";
	}
	/**
	 * Getter N_CORDAYAMT   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNcordayamt(){
		 return "DECIMAL";
	}
	/**
	 * Getter N_CORTENDAYAMT   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNcortendayamt(){
		 return "DECIMAL";
	}
	/**
	 * Getter N_CORMONTHAMT   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNcormonthamt(){
		 return "DECIMAL";
	}
	/**
	 * Getter N_CORQUARTERAMT   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNcorquarteramt(){
		 return "DECIMAL";
	}
	/**
	 * Getter N_CORYEARAMT   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNcoryearamt(){
		 return "DECIMAL";
	}
	/**
	 * Getter S_ACCRUALSUBJECT, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSaccrualsubject(){
		 return "VARCHAR";
	}
	/**
	 * Getter N_ACCDAYAMT   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNaccdayamt(){
		 return "DECIMAL";
	}
	/**
	 * Getter N_ACCTENDAYAMT   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNacctendayamt(){
		 return "DECIMAL";
	}
	/**
	 * Getter N_ACCMONTHAMT   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNaccmonthamt(){
		 return "DECIMAL";
	}
	/**
	 * Getter N_ACCQUARTERAMT   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNaccquarteramt(){
		 return "DECIMAL";
	}
	/**
	 * Getter N_ACCYEARAMT   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNaccyearamt(){
		 return "DECIMAL";
	}
	/******************************************************
	*
	*  Get Column With
	*
	*******************************************************/
	/**
	 * Getter S_TAXORGCODE, PK , NOT NULL   	 
	 */
	public static int columnWidthStaxorgcode(){
		 return 30;
	}
	/**
	 * Getter S_TRECODE, PK , NOT NULL   	 
	 */
	public static int columnWidthStrecode(){
		 return 10;
	}
	/**
	 * Getter S_RPTDATE, PK , NOT NULL   	 
	 */
	public static int columnWidthSrptdate(){
		 return 8;
	}
	/**
	 * Getter S_CASHBANK, PK , NOT NULL   	 
	 */
	public static int columnWidthScashbank(){
		 return 20;
	}
	/**
	 * Getter S_KINDSIGN , NOT NULL   	 
	 */
	public static int columnWidthSkindsign(){
		 return 1;
	}
	/**
	 * Getter S_CORPUSSUBJECT, PK , NOT NULL   	 
	 */
	public static int columnWidthScorpussubject(){
		 return 30;
	}
	/**
	 * Getter S_ACCRUALSUBJECT, PK , NOT NULL   	 
	 */
	public static int columnWidthSaccrualsubject(){
		 return 30;
	}
}
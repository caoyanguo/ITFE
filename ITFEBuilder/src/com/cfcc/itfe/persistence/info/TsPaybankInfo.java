      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TS_PAYBANK</p>
 * <p>Description:  Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:00 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class TsPaybankInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TS_PAYBANK";
	
	/**
	 * column S_ORGCODE
	 */
	public static String S_ORGCODE ="S_ORGCODE";
	/**
	 * column S_BANKNO
	 */
	public static String S_BANKNO ="S_BANKNO";
	/**
	 * column S_BANKNAME
	 */
	public static String S_BANKNAME ="S_BANKNAME";
	/**
	 * column S_PAYBANKNO
	 */
	public static String S_PAYBANKNO ="S_PAYBANKNO";
	/**
	 * column S_PAYBANKNAME
	 */
	public static String S_PAYBANKNAME ="S_PAYBANKNAME";
	/**
	 * column I_MODICOUNT
	 */
	public static String I_MODICOUNT ="I_MODICOUNT";
	/**
	 * column S_STATE
	 */
	public static String S_STATE ="S_STATE";
	/**
	 * column D_AFFDATE
	 */
	public static String D_AFFDATE ="D_AFFDATE";
	/**
	 * column C_BNKCLASS
	 */
	public static String C_BNKCLASS ="C_BNKCLASS";
	/**
	 * column S_OFCITY
	 */
	public static String S_OFCITY ="S_OFCITY";
	/**
	 * column C_BNKCLSNO
	 */
	public static String C_BNKCLSNO ="C_BNKCLSNO";
	/**
	 * column C_BNKACCTSTA
	 */
	public static String C_BNKACCTSTA ="C_BNKACCTSTA";
	/**
	 * column C_BNKPOSTCODE
	 */
	public static String C_BNKPOSTCODE ="C_BNKPOSTCODE";
	/**
	 * column S_BNKADDR
	 */
	public static String S_BNKADDR ="S_BNKADDR";
	/**
	 * column C_OPTTYPE
	 */
	public static String C_OPTTYPE ="C_OPTTYPE";
	/**
	 * column S_BNKTEL
	 */
	public static String S_BNKTEL ="S_BNKTEL";
	/**
	 * column S_ADDCOLUMN1
	 */
	public static String S_ADDCOLUMN1 ="S_ADDCOLUMN1";
	/**
	 * column S_ADDCOLUMN2
	 */
	public static String S_ADDCOLUMN2 ="S_ADDCOLUMN2";
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
        String[] columnNames = new String[18];        
        columnNames[0]="S_ORGCODE";
        columnNames[1]="S_BANKNO";
        columnNames[2]="S_BANKNAME";
        columnNames[3]="S_PAYBANKNO";
        columnNames[4]="S_PAYBANKNAME";
        columnNames[5]="I_MODICOUNT";
        columnNames[6]="S_STATE";
        columnNames[7]="D_AFFDATE";
        columnNames[8]="C_BNKCLASS";
        columnNames[9]="S_OFCITY";
        columnNames[10]="C_BNKCLSNO";
        columnNames[11]="C_BNKACCTSTA";
        columnNames[12]="C_BNKPOSTCODE";
        columnNames[13]="S_BNKADDR";
        columnNames[14]="C_OPTTYPE";
        columnNames[15]="S_BNKTEL";
        columnNames[16]="S_ADDCOLUMN1";
        columnNames[17]="S_ADDCOLUMN2";
        return columnNames;     
    }
	/******************************************************
	*
	*  Get Column Name
	*
	*******************************************************/
	/**
	 * Getter S_ORGCODE, PK , NOT NULL   	 
	 */
	public static String columnSorgcode(){
		 return S_ORGCODE;
	}
	/**
	 * Getter S_BANKNO, PK , NOT NULL   	 
	 */
	public static String columnSbankno(){
		 return S_BANKNO;
	}
	/**
	 * Getter S_BANKNAME   	 
	 */
	public static String columnSbankname(){
		 return S_BANKNAME;
	}
	/**
	 * Getter S_PAYBANKNO , NOT NULL   	 
	 */
	public static String columnSpaybankno(){
		 return S_PAYBANKNO;
	}
	/**
	 * Getter S_PAYBANKNAME   	 
	 */
	public static String columnSpaybankname(){
		 return S_PAYBANKNAME;
	}
	/**
	 * Getter I_MODICOUNT   	 
	 */
	public static String columnImodicount(){
		 return I_MODICOUNT;
	}
	/**
	 * Getter S_STATE   	 
	 */
	public static String columnSstate(){
		 return S_STATE;
	}
	/**
	 * Getter D_AFFDATE   	 
	 */
	public static String columnDaffdate(){
		 return D_AFFDATE;
	}
	/**
	 * Getter C_BNKCLASS   	 
	 */
	public static String columnCbnkclass(){
		 return C_BNKCLASS;
	}
	/**
	 * Getter S_OFCITY   	 
	 */
	public static String columnSofcity(){
		 return S_OFCITY;
	}
	/**
	 * Getter C_BNKCLSNO   	 
	 */
	public static String columnCbnkclsno(){
		 return C_BNKCLSNO;
	}
	/**
	 * Getter C_BNKACCTSTA   	 
	 */
	public static String columnCbnkacctsta(){
		 return C_BNKACCTSTA;
	}
	/**
	 * Getter C_BNKPOSTCODE   	 
	 */
	public static String columnCbnkpostcode(){
		 return C_BNKPOSTCODE;
	}
	/**
	 * Getter S_BNKADDR   	 
	 */
	public static String columnSbnkaddr(){
		 return S_BNKADDR;
	}
	/**
	 * Getter C_OPTTYPE   	 
	 */
	public static String columnCopttype(){
		 return C_OPTTYPE;
	}
	/**
	 * Getter S_BNKTEL   	 
	 */
	public static String columnSbnktel(){
		 return S_BNKTEL;
	}
	/**
	 * Getter S_ADDCOLUMN1   	 
	 */
	public static String columnSaddcolumn1(){
		 return S_ADDCOLUMN1;
	}
	/**
	 * Getter S_ADDCOLUMN2   	 
	 */
	public static String columnSaddcolumn2(){
		 return S_ADDCOLUMN2;
	}
	/******************************************************
	*
	*  Get Column Java Type
	*
	*******************************************************/
	/**
	 * Getter S_ORGCODE, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeSorgcode(){
		 return "String";
	}
	/**
	 * Getter S_BANKNO, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeSbankno(){
		 return "String";
	}
	/**
	 * Getter S_BANKNAME   	 
	 */
	public static String columnJavaTypeSbankname(){
		 return "String";
	}
	/**
	 * Getter S_PAYBANKNO , NOT NULL   	 
	 */
	public static String columnJavaTypeSpaybankno(){
		 return "String";
	}
	/**
	 * Getter S_PAYBANKNAME   	 
	 */
	public static String columnJavaTypeSpaybankname(){
		 return "String";
	}
	/**
	 * Getter I_MODICOUNT   	 
	 */
	public static String columnJavaTypeImodicount(){
		 return "Integer";
	}
	/**
	 * Getter S_STATE   	 
	 */
	public static String columnJavaTypeSstate(){
		 return "String";
	}
	/**
	 * Getter D_AFFDATE   	 
	 */
	public static String columnJavaTypeDaffdate(){
		 return "Date";
	}
	/**
	 * Getter C_BNKCLASS   	 
	 */
	public static String columnJavaTypeCbnkclass(){
		 return "String";
	}
	/**
	 * Getter S_OFCITY   	 
	 */
	public static String columnJavaTypeSofcity(){
		 return "String";
	}
	/**
	 * Getter C_BNKCLSNO   	 
	 */
	public static String columnJavaTypeCbnkclsno(){
		 return "String";
	}
	/**
	 * Getter C_BNKACCTSTA   	 
	 */
	public static String columnJavaTypeCbnkacctsta(){
		 return "String";
	}
	/**
	 * Getter C_BNKPOSTCODE   	 
	 */
	public static String columnJavaTypeCbnkpostcode(){
		 return "String";
	}
	/**
	 * Getter S_BNKADDR   	 
	 */
	public static String columnJavaTypeSbnkaddr(){
		 return "String";
	}
	/**
	 * Getter C_OPTTYPE   	 
	 */
	public static String columnJavaTypeCopttype(){
		 return "String";
	}
	/**
	 * Getter S_BNKTEL   	 
	 */
	public static String columnJavaTypeSbnktel(){
		 return "String";
	}
	/**
	 * Getter S_ADDCOLUMN1   	 
	 */
	public static String columnJavaTypeSaddcolumn1(){
		 return "String";
	}
	/**
	 * Getter S_ADDCOLUMN2   	 
	 */
	public static String columnJavaTypeSaddcolumn2(){
		 return "String";
	}
	/******************************************************
	*
	*  Get Column Database Type
	*
	*******************************************************/
	/**
	 * Getter S_ORGCODE, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSorgcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_BANKNO, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSbankno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_BANKNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSbankname(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYBANKNO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpaybankno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYBANKNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpaybankname(){
		 return "VARCHAR";
	}
	/**
	 * Getter I_MODICOUNT   	 
	 * columnType is INTEGER
	 */
	public static String columnDatabaseTypeImodicount(){
		 return "INTEGER";
	}
	/**
	 * Getter S_STATE   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSstate(){
		 return "CHARACTER";
	}
	/**
	 * Getter D_AFFDATE   	 
	 * columnType is DATE
	 */
	public static String columnDatabaseTypeDaffdate(){
		 return "DATE";
	}
	/**
	 * Getter C_BNKCLASS   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCbnkclass(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_OFCITY   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSofcity(){
		 return "VARCHAR";
	}
	/**
	 * Getter C_BNKCLSNO   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCbnkclsno(){
		 return "CHARACTER";
	}
	/**
	 * Getter C_BNKACCTSTA   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCbnkacctsta(){
		 return "CHARACTER";
	}
	/**
	 * Getter C_BNKPOSTCODE   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCbnkpostcode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_BNKADDR   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSbnkaddr(){
		 return "VARCHAR";
	}
	/**
	 * Getter C_OPTTYPE   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCopttype(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_BNKTEL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSbnktel(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ADDCOLUMN1   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSaddcolumn1(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ADDCOLUMN2   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSaddcolumn2(){
		 return "VARCHAR";
	}
	/******************************************************
	*
	*  Get Column With
	*
	*******************************************************/
	/**
	 * Getter S_ORGCODE, PK , NOT NULL   	 
	 */
	public static int columnWidthSorgcode(){
		 return 12;
	}
	/**
	 * Getter S_BANKNO, PK , NOT NULL   	 
	 */
	public static int columnWidthSbankno(){
		 return 30;
	}
	/**
	 * Getter S_BANKNAME   	 
	 */
	public static int columnWidthSbankname(){
		 return 100;
	}
	/**
	 * Getter S_PAYBANKNO , NOT NULL   	 
	 */
	public static int columnWidthSpaybankno(){
		 return 12;
	}
	/**
	 * Getter S_PAYBANKNAME   	 
	 */
	public static int columnWidthSpaybankname(){
		 return 100;
	}
	/**
	 * Getter S_STATE   	 
	 */
	public static int columnWidthSstate(){
		 return 1;
	}
	/**
	 * Getter C_BNKCLASS   	 
	 */
	public static int columnWidthCbnkclass(){
		 return 2;
	}
	/**
	 * Getter S_OFCITY   	 
	 */
	public static int columnWidthSofcity(){
		 return 4;
	}
	/**
	 * Getter C_BNKCLSNO   	 
	 */
	public static int columnWidthCbnkclsno(){
		 return 3;
	}
	/**
	 * Getter C_BNKACCTSTA   	 
	 */
	public static int columnWidthCbnkacctsta(){
		 return 1;
	}
	/**
	 * Getter C_BNKPOSTCODE   	 
	 */
	public static int columnWidthCbnkpostcode(){
		 return 6;
	}
	/**
	 * Getter S_BNKADDR   	 
	 */
	public static int columnWidthSbnkaddr(){
		 return 60;
	}
	/**
	 * Getter C_OPTTYPE   	 
	 */
	public static int columnWidthCopttype(){
		 return 1;
	}
	/**
	 * Getter S_BNKTEL   	 
	 */
	public static int columnWidthSbnktel(){
		 return 30;
	}
	/**
	 * Getter S_ADDCOLUMN1   	 
	 */
	public static int columnWidthSaddcolumn1(){
		 return 12;
	}
	/**
	 * Getter S_ADDCOLUMN2   	 
	 */
	public static int columnWidthSaddcolumn2(){
		 return 60;
	}
}
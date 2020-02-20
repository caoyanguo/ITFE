      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TS_CHECKDELOPTLOG</p>
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

public class TsCheckdeloptlogInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TS_CHECKDELOPTLOG";
	
	/**
	 * column S_ORGCODE
	 */
	public static String S_ORGCODE ="S_ORGCODE";
	/**
	 * column S_BIZTYPE
	 */
	public static String S_BIZTYPE ="S_BIZTYPE";
	/**
	 * column S_USERCODE
	 */
	public static String S_USERCODE ="S_USERCODE";
	/**
	 * column S_USERNAME
	 */
	public static String S_USERNAME ="S_USERNAME";
	/**
	 * column S_FILENAME
	 */
	public static String S_FILENAME ="S_FILENAME";
	/**
	 * column S_VOUNO
	 */
	public static String S_VOUNO ="S_VOUNO";
	/**
	 * column T_OPETIME
	 */
	public static String T_OPETIME ="T_OPETIME";
	/**
	 * column S_REMAEK1
	 */
	public static String S_REMAEK1 ="S_REMAEK1";
	/**
	 * column S_REMAEK2
	 */
	public static String S_REMAEK2 ="S_REMAEK2";
	/**
	 * column S_REMAEK3
	 */
	public static String S_REMAEK3 ="S_REMAEK3";
	/**
	 * column F_AMT
	 */
	public static String F_AMT ="F_AMT";
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
        String[] columnNames = new String[11];        
        columnNames[0]="S_ORGCODE";
        columnNames[1]="S_BIZTYPE";
        columnNames[2]="S_USERCODE";
        columnNames[3]="S_USERNAME";
        columnNames[4]="S_FILENAME";
        columnNames[5]="S_VOUNO";
        columnNames[6]="T_OPETIME";
        columnNames[7]="S_REMAEK1";
        columnNames[8]="S_REMAEK2";
        columnNames[9]="S_REMAEK3";
        columnNames[10]="F_AMT";
        return columnNames;     
    }
	/******************************************************
	*
	*  Get Column Name
	*
	*******************************************************/
	/**
	 * Getter S_ORGCODE , NOT NULL   	 
	 */
	public static String columnSorgcode(){
		 return S_ORGCODE;
	}
	/**
	 * Getter S_BIZTYPE , NOT NULL   	 
	 */
	public static String columnSbiztype(){
		 return S_BIZTYPE;
	}
	/**
	 * Getter S_USERCODE , NOT NULL   	 
	 */
	public static String columnSusercode(){
		 return S_USERCODE;
	}
	/**
	 * Getter S_USERNAME , NOT NULL   	 
	 */
	public static String columnSusername(){
		 return S_USERNAME;
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 */
	public static String columnSfilename(){
		 return S_FILENAME;
	}
	/**
	 * Getter S_VOUNO   	 
	 */
	public static String columnSvouno(){
		 return S_VOUNO;
	}
	/**
	 * Getter T_OPETIME   	 
	 */
	public static String columnTopetime(){
		 return T_OPETIME;
	}
	/**
	 * Getter S_REMAEK1   	 
	 */
	public static String columnSremaek1(){
		 return S_REMAEK1;
	}
	/**
	 * Getter S_REMAEK2   	 
	 */
	public static String columnSremaek2(){
		 return S_REMAEK2;
	}
	/**
	 * Getter S_REMAEK3   	 
	 */
	public static String columnSremaek3(){
		 return S_REMAEK3;
	}
	/**
	 * Getter F_AMT   	 
	 */
	public static String columnFamt(){
		 return F_AMT;
	}
	/******************************************************
	*
	*  Get Column Java Type
	*
	*******************************************************/
	/**
	 * Getter S_ORGCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSorgcode(){
		 return "String";
	}
	/**
	 * Getter S_BIZTYPE , NOT NULL   	 
	 */
	public static String columnJavaTypeSbiztype(){
		 return "String";
	}
	/**
	 * Getter S_USERCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSusercode(){
		 return "String";
	}
	/**
	 * Getter S_USERNAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSusername(){
		 return "String";
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSfilename(){
		 return "String";
	}
	/**
	 * Getter S_VOUNO   	 
	 */
	public static String columnJavaTypeSvouno(){
		 return "String";
	}
	/**
	 * Getter T_OPETIME   	 
	 */
	public static String columnJavaTypeTopetime(){
		 return "Timestamp";
	}
	/**
	 * Getter S_REMAEK1   	 
	 */
	public static String columnJavaTypeSremaek1(){
		 return "String";
	}
	/**
	 * Getter S_REMAEK2   	 
	 */
	public static String columnJavaTypeSremaek2(){
		 return "String";
	}
	/**
	 * Getter S_REMAEK3   	 
	 */
	public static String columnJavaTypeSremaek3(){
		 return "String";
	}
	/**
	 * Getter F_AMT   	 
	 */
	public static String columnJavaTypeFamt(){
		 return "BigDecimal";
	}
	/******************************************************
	*
	*  Get Column Database Type
	*
	*******************************************************/
	/**
	 * Getter S_ORGCODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSorgcode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_BIZTYPE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSbiztype(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_USERCODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSusercode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_USERNAME , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSusername(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSfilename(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_VOUNO   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSvouno(){
		 return "CHARACTER";
	}
	/**
	 * Getter T_OPETIME   	 
	 * columnType is TIMESTAMP
	 */
	public static String columnDatabaseTypeTopetime(){
		 return "TIMESTAMP";
	}
	/**
	 * Getter S_REMAEK1   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSremaek1(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_REMAEK2   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSremaek2(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_REMAEK3   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSremaek3(){
		 return "CHARACTER";
	}
	/**
	 * Getter F_AMT   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeFamt(){
		 return "DECIMAL";
	}
	/******************************************************
	*
	*  Get Column With
	*
	*******************************************************/
	/**
	 * Getter S_ORGCODE , NOT NULL   	 
	 */
	public static int columnWidthSorgcode(){
		 return 12;
	}
	/**
	 * Getter S_BIZTYPE , NOT NULL   	 
	 */
	public static int columnWidthSbiztype(){
		 return 10;
	}
	/**
	 * Getter S_USERCODE , NOT NULL   	 
	 */
	public static int columnWidthSusercode(){
		 return 30;
	}
	/**
	 * Getter S_USERNAME , NOT NULL   	 
	 */
	public static int columnWidthSusername(){
		 return 100;
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 */
	public static int columnWidthSfilename(){
		 return 100;
	}
	/**
	 * Getter S_VOUNO   	 
	 */
	public static int columnWidthSvouno(){
		 return 100;
	}
	/**
	 * Getter S_REMAEK1   	 
	 */
	public static int columnWidthSremaek1(){
		 return 20;
	}
	/**
	 * Getter S_REMAEK2   	 
	 */
	public static int columnWidthSremaek2(){
		 return 50;
	}
	/**
	 * Getter S_REMAEK3   	 
	 */
	public static int columnWidthSremaek3(){
		 return 100;
	}
}
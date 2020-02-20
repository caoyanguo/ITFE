      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TS_FINMOVEPAYSUB</p>
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

public class TsFinmovepaysubInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TS_FINMOVEPAYSUB";
	
	/**
	 * column S_ORGCODE
	 */
	public static String S_ORGCODE ="S_ORGCODE";
	/**
	 * column S_SUBJECTCODE
	 */
	public static String S_SUBJECTCODE ="S_SUBJECTCODE";
	/**
	 * column S_SUBJECTNAME
	 */
	public static String S_SUBJECTNAME ="S_SUBJECTNAME";
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
        String[] columnNames = new String[3];        
        columnNames[0]="S_ORGCODE";
        columnNames[1]="S_SUBJECTCODE";
        columnNames[2]="S_SUBJECTNAME";
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
	 * Getter S_SUBJECTCODE, PK , NOT NULL   	 
	 */
	public static String columnSsubjectcode(){
		 return S_SUBJECTCODE;
	}
	/**
	 * Getter S_SUBJECTNAME   	 
	 */
	public static String columnSsubjectname(){
		 return S_SUBJECTNAME;
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
	 * Getter S_SUBJECTCODE, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeSsubjectcode(){
		 return "String";
	}
	/**
	 * Getter S_SUBJECTNAME   	 
	 */
	public static String columnJavaTypeSsubjectname(){
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
	 * Getter S_SUBJECTCODE, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSsubjectcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_SUBJECTNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSsubjectname(){
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
	 * Getter S_SUBJECTCODE, PK , NOT NULL   	 
	 */
	public static int columnWidthSsubjectcode(){
		 return 30;
	}
	/**
	 * Getter S_SUBJECTNAME   	 
	 */
	public static int columnWidthSsubjectname(){
		 return 60;
	}
}
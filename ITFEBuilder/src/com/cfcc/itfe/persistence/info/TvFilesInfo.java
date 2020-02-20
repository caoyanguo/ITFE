      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TV_FILES</p>
 * <p>Description:  Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:02 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class TvFilesInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TV_FILES";
	
	/**
	 * column I_NO
	 */
	public static String I_NO ="I_NO";
	/**
	 * column S_DATE
	 */
	public static String S_DATE ="S_DATE";
	/**
	 * column S_NO
	 */
	public static String S_NO ="S_NO";
	/**
	 * column S_CONTENT
	 */
	public static String S_CONTENT ="S_CONTENT";
	/**
	 * column I_FILELENGTH
	 */
	public static String I_FILELENGTH ="I_FILELENGTH";
	/**
	 * column S_SAVEPATH
	 */
	public static String S_SAVEPATH ="S_SAVEPATH";
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
        String[] columnNames = new String[6];        
        columnNames[0]="I_NO";
        columnNames[1]="S_DATE";
        columnNames[2]="S_NO";
        columnNames[3]="S_CONTENT";
        columnNames[4]="I_FILELENGTH";
        columnNames[5]="S_SAVEPATH";
        return columnNames;     
    }
	/******************************************************
	*
	*  Get Column Name
	*
	*******************************************************/
	/**
	 * Getter I_NO, PK , NOT NULL   	 
	 */
	public static String columnIno(){
		 return I_NO;
	}
	/**
	 * Getter S_DATE , NOT NULL   	 
	 */
	public static String columnSdate(){
		 return S_DATE;
	}
	/**
	 * Getter S_NO , NOT NULL   	 
	 */
	public static String columnSno(){
		 return S_NO;
	}
	/**
	 * Getter S_CONTENT   	 
	 */
	public static String columnScontent(){
		 return S_CONTENT;
	}
	/**
	 * Getter I_FILELENGTH   	 
	 */
	public static String columnIfilelength(){
		 return I_FILELENGTH;
	}
	/**
	 * Getter S_SAVEPATH   	 
	 */
	public static String columnSsavepath(){
		 return S_SAVEPATH;
	}
	/******************************************************
	*
	*  Get Column Java Type
	*
	*******************************************************/
	/**
	 * Getter I_NO, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeIno(){
		 return "Integer";
	}
	/**
	 * Getter S_DATE , NOT NULL   	 
	 */
	public static String columnJavaTypeSdate(){
		 return "String";
	}
	/**
	 * Getter S_NO , NOT NULL   	 
	 */
	public static String columnJavaTypeSno(){
		 return "String";
	}
	/**
	 * Getter S_CONTENT   	 
	 */
	public static String columnJavaTypeScontent(){
		 return "String";
	}
	/**
	 * Getter I_FILELENGTH   	 
	 */
	public static String columnJavaTypeIfilelength(){
		 return "Integer";
	}
	/**
	 * Getter S_SAVEPATH   	 
	 */
	public static String columnJavaTypeSsavepath(){
		 return "String";
	}
	/******************************************************
	*
	*  Get Column Database Type
	*
	*******************************************************/
	/**
	 * Getter I_NO, PK , NOT NULL   	 
	 * columnType is INTEGER
	 */
	public static String columnDatabaseTypeIno(){
		 return "INTEGER";
	}
	/**
	 * Getter S_DATE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSdate(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_NO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CONTENT   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeScontent(){
		 return "VARCHAR";
	}
	/**
	 * Getter I_FILELENGTH   	 
	 * columnType is INTEGER
	 */
	public static String columnDatabaseTypeIfilelength(){
		 return "INTEGER";
	}
	/**
	 * Getter S_SAVEPATH   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSsavepath(){
		 return "VARCHAR";
	}
	/******************************************************
	*
	*  Get Column With
	*
	*******************************************************/
	/**
	 * Getter S_DATE , NOT NULL   	 
	 */
	public static int columnWidthSdate(){
		 return 8;
	}
	/**
	 * Getter S_NO , NOT NULL   	 
	 */
	public static int columnWidthSno(){
		 return 20;
	}
	/**
	 * Getter S_CONTENT   	 
	 */
	public static int columnWidthScontent(){
		 return 250;
	}
	/**
	 * Getter S_SAVEPATH   	 
	 */
	public static int columnWidthSsavepath(){
		 return 200;
	}
}
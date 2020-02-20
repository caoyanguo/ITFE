      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TS_TAXORG</p>
 * <p>Description:  Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:01 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class TsTaxorgInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TS_TAXORG";
	
	/**
	 * column S_ORGCODE
	 */
	public static String S_ORGCODE ="S_ORGCODE";
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
	 * column S_TAXPROP
	 */
	public static String S_TAXPROP ="S_TAXPROP";
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
        String[] columnNames = new String[5];        
        columnNames[0]="S_ORGCODE";
        columnNames[1]="S_TRECODE";
        columnNames[2]="S_TAXORGCODE";
        columnNames[3]="S_TAXORGNAME";
        columnNames[4]="S_TAXPROP";
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
	 * Getter S_TRECODE, PK , NOT NULL   	 
	 */
	public static String columnStrecode(){
		 return S_TRECODE;
	}
	/**
	 * Getter S_TAXORGCODE, PK , NOT NULL   	 
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
	 * Getter S_TAXPROP   	 
	 */
	public static String columnStaxprop(){
		 return S_TAXPROP;
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
	 * Getter S_TRECODE, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeStrecode(){
		 return "String";
	}
	/**
	 * Getter S_TAXORGCODE, PK , NOT NULL   	 
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
	 * Getter S_TAXPROP   	 
	 */
	public static String columnJavaTypeStaxprop(){
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
	 * Getter S_TRECODE, PK , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeStrecode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_TAXORGCODE, PK , NOT NULL   	 
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
	 * Getter S_TAXPROP   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeStaxprop(){
		 return "CHARACTER";
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
	 * Getter S_TRECODE, PK , NOT NULL   	 
	 */
	public static int columnWidthStrecode(){
		 return 10;
	}
	/**
	 * Getter S_TAXORGCODE, PK , NOT NULL   	 
	 */
	public static int columnWidthStaxorgcode(){
		 return 30;
	}
	/**
	 * Getter S_TAXORGNAME   	 
	 */
	public static int columnWidthStaxorgname(){
		 return 50;
	}
	/**
	 * Getter S_TAXPROP   	 
	 */
	public static int columnWidthStaxprop(){
		 return 12;
	}
}
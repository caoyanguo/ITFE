      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: HTV_TAX_ITEM</p>
 * <p>Description:  Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:56 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class HtvTaxItemInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "HTV_TAX_ITEM";
	
	/**
	 * column S_SEQ
	 */
	public static String S_SEQ ="S_SEQ";
	/**
	 * column I_PROJECTID
	 */
	public static String I_PROJECTID ="I_PROJECTID";
	/**
	 * column I_DETAILNO
	 */
	public static String I_DETAILNO ="I_DETAILNO";
	/**
	 * column S_TAXSUBJECTCODE
	 */
	public static String S_TAXSUBJECTCODE ="S_TAXSUBJECTCODE";
	/**
	 * column S_TAXSUBJECTNAME
	 */
	public static String S_TAXSUBJECTNAME ="S_TAXSUBJECTNAME";
	/**
	 * column I_TAXNUMBER
	 */
	public static String I_TAXNUMBER ="I_TAXNUMBER";
	/**
	 * column F_TAXAMT
	 */
	public static String F_TAXAMT ="F_TAXAMT";
	/**
	 * column F_TAXRATE
	 */
	public static String F_TAXRATE ="F_TAXRATE";
	/**
	 * column F_EXPTAXAMT
	 */
	public static String F_EXPTAXAMT ="F_EXPTAXAMT";
	/**
	 * column F_DISCOUNTTAXAMT
	 */
	public static String F_DISCOUNTTAXAMT ="F_DISCOUNTTAXAMT";
	/**
	 * column F_REALAMT
	 */
	public static String F_REALAMT ="F_REALAMT";
	/**
	 * column TS_UPDATE
	 */
	public static String TS_UPDATE ="TS_UPDATE";
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
        String[] columnNames = new String[12];        
        columnNames[0]="S_SEQ";
        columnNames[1]="I_PROJECTID";
        columnNames[2]="I_DETAILNO";
        columnNames[3]="S_TAXSUBJECTCODE";
        columnNames[4]="S_TAXSUBJECTNAME";
        columnNames[5]="I_TAXNUMBER";
        columnNames[6]="F_TAXAMT";
        columnNames[7]="F_TAXRATE";
        columnNames[8]="F_EXPTAXAMT";
        columnNames[9]="F_DISCOUNTTAXAMT";
        columnNames[10]="F_REALAMT";
        columnNames[11]="TS_UPDATE";
        return columnNames;     
    }
	/******************************************************
	*
	*  Get Column Name
	*
	*******************************************************/
	/**
	 * Getter S_SEQ, PK , NOT NULL   	 
	 */
	public static String columnSseq(){
		 return S_SEQ;
	}
	/**
	 * Getter I_PROJECTID, PK , NOT NULL   	 
	 */
	public static String columnIprojectid(){
		 return I_PROJECTID;
	}
	/**
	 * Getter I_DETAILNO, PK , NOT NULL   	 
	 */
	public static String columnIdetailno(){
		 return I_DETAILNO;
	}
	/**
	 * Getter S_TAXSUBJECTCODE , NOT NULL   	 
	 */
	public static String columnStaxsubjectcode(){
		 return S_TAXSUBJECTCODE;
	}
	/**
	 * Getter S_TAXSUBJECTNAME , NOT NULL   	 
	 */
	public static String columnStaxsubjectname(){
		 return S_TAXSUBJECTNAME;
	}
	/**
	 * Getter I_TAXNUMBER , NOT NULL   	 
	 */
	public static String columnItaxnumber(){
		 return I_TAXNUMBER;
	}
	/**
	 * Getter F_TAXAMT , NOT NULL   	 
	 */
	public static String columnFtaxamt(){
		 return F_TAXAMT;
	}
	/**
	 * Getter F_TAXRATE , NOT NULL   	 
	 */
	public static String columnFtaxrate(){
		 return F_TAXRATE;
	}
	/**
	 * Getter F_EXPTAXAMT , NOT NULL   	 
	 */
	public static String columnFexptaxamt(){
		 return F_EXPTAXAMT;
	}
	/**
	 * Getter F_DISCOUNTTAXAMT , NOT NULL   	 
	 */
	public static String columnFdiscounttaxamt(){
		 return F_DISCOUNTTAXAMT;
	}
	/**
	 * Getter F_REALAMT , NOT NULL   	 
	 */
	public static String columnFrealamt(){
		 return F_REALAMT;
	}
	/**
	 * Getter TS_UPDATE , NOT NULL   	 
	 */
	public static String columnTsupdate(){
		 return TS_UPDATE;
	}
	/******************************************************
	*
	*  Get Column Java Type
	*
	*******************************************************/
	/**
	 * Getter S_SEQ, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeSseq(){
		 return "String";
	}
	/**
	 * Getter I_PROJECTID, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeIprojectid(){
		 return "Integer";
	}
	/**
	 * Getter I_DETAILNO, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeIdetailno(){
		 return "Integer";
	}
	/**
	 * Getter S_TAXSUBJECTCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeStaxsubjectcode(){
		 return "String";
	}
	/**
	 * Getter S_TAXSUBJECTNAME , NOT NULL   	 
	 */
	public static String columnJavaTypeStaxsubjectname(){
		 return "String";
	}
	/**
	 * Getter I_TAXNUMBER , NOT NULL   	 
	 */
	public static String columnJavaTypeItaxnumber(){
		 return "Integer";
	}
	/**
	 * Getter F_TAXAMT , NOT NULL   	 
	 */
	public static String columnJavaTypeFtaxamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter F_TAXRATE , NOT NULL   	 
	 */
	public static String columnJavaTypeFtaxrate(){
		 return "BigDecimal";
	}
	/**
	 * Getter F_EXPTAXAMT , NOT NULL   	 
	 */
	public static String columnJavaTypeFexptaxamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter F_DISCOUNTTAXAMT , NOT NULL   	 
	 */
	public static String columnJavaTypeFdiscounttaxamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter F_REALAMT , NOT NULL   	 
	 */
	public static String columnJavaTypeFrealamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter TS_UPDATE , NOT NULL   	 
	 */
	public static String columnJavaTypeTsupdate(){
		 return "Timestamp";
	}
	/******************************************************
	*
	*  Get Column Database Type
	*
	*******************************************************/
	/**
	 * Getter S_SEQ, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSseq(){
		 return "VARCHAR";
	}
	/**
	 * Getter I_PROJECTID, PK , NOT NULL   	 
	 * columnType is INTEGER
	 */
	public static String columnDatabaseTypeIprojectid(){
		 return "INTEGER";
	}
	/**
	 * Getter I_DETAILNO, PK , NOT NULL   	 
	 * columnType is INTEGER
	 */
	public static String columnDatabaseTypeIdetailno(){
		 return "INTEGER";
	}
	/**
	 * Getter S_TAXSUBJECTCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStaxsubjectcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_TAXSUBJECTNAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStaxsubjectname(){
		 return "VARCHAR";
	}
	/**
	 * Getter I_TAXNUMBER , NOT NULL   	 
	 * columnType is INTEGER
	 */
	public static String columnDatabaseTypeItaxnumber(){
		 return "INTEGER";
	}
	/**
	 * Getter F_TAXAMT , NOT NULL   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeFtaxamt(){
		 return "DECIMAL";
	}
	/**
	 * Getter F_TAXRATE , NOT NULL   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeFtaxrate(){
		 return "DECIMAL";
	}
	/**
	 * Getter F_EXPTAXAMT , NOT NULL   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeFexptaxamt(){
		 return "DECIMAL";
	}
	/**
	 * Getter F_DISCOUNTTAXAMT , NOT NULL   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeFdiscounttaxamt(){
		 return "DECIMAL";
	}
	/**
	 * Getter F_REALAMT , NOT NULL   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeFrealamt(){
		 return "DECIMAL";
	}
	/**
	 * Getter TS_UPDATE , NOT NULL   	 
	 * columnType is TIMESTAMP
	 */
	public static String columnDatabaseTypeTsupdate(){
		 return "TIMESTAMP";
	}
	/******************************************************
	*
	*  Get Column With
	*
	*******************************************************/
	/**
	 * Getter S_SEQ, PK , NOT NULL   	 
	 */
	public static int columnWidthSseq(){
		 return 20;
	}
	/**
	 * Getter S_TAXSUBJECTCODE , NOT NULL   	 
	 */
	public static int columnWidthStaxsubjectcode(){
		 return 20;
	}
	/**
	 * Getter S_TAXSUBJECTNAME , NOT NULL   	 
	 */
	public static int columnWidthStaxsubjectname(){
		 return 60;
	}
}
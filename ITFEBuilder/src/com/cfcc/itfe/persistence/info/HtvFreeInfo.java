      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: HTV_FREE</p>
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

public class HtvFreeInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "HTV_FREE";
	
	/**
	 * column I_VOUSRLNO
	 */
	public static String I_VOUSRLNO ="I_VOUSRLNO";
	/**
	 * column S_TAXORGCODE
	 */
	public static String S_TAXORGCODE ="S_TAXORGCODE";
	/**
	 * column S_PACKNO
	 */
	public static String S_PACKNO ="S_PACKNO";
	/**
	 * column S_TRANO
	 */
	public static String S_TRANO ="S_TRANO";
	/**
	 * column D_BILLDATE
	 */
	public static String D_BILLDATE ="D_BILLDATE";
	/**
	 * column S_ELECTROTAXVOUNO
	 */
	public static String S_ELECTROTAXVOUNO ="S_ELECTROTAXVOUNO";
	/**
	 * column S_FREEVOUNO
	 */
	public static String S_FREEVOUNO ="S_FREEVOUNO";
	/**
	 * column C_FREEPLUTYPE
	 */
	public static String C_FREEPLUTYPE ="C_FREEPLUTYPE";
	/**
	 * column S_FREEPLUSUBJECTCODE
	 */
	public static String S_FREEPLUSUBJECTCODE ="S_FREEPLUSUBJECTCODE";
	/**
	 * column C_FREEPLULEVEL
	 */
	public static String C_FREEPLULEVEL ="C_FREEPLULEVEL";
	/**
	 * column S_FREEPLUSIGN
	 */
	public static String S_FREEPLUSIGN ="S_FREEPLUSIGN";
	/**
	 * column S_FREEPLUPTRECODE
	 */
	public static String S_FREEPLUPTRECODE ="S_FREEPLUPTRECODE";
	/**
	 * column F_FREEPLUAMT
	 */
	public static String F_FREEPLUAMT ="F_FREEPLUAMT";
	/**
	 * column C_FREEMIKIND
	 */
	public static String C_FREEMIKIND ="C_FREEMIKIND";
	/**
	 * column S_FREEMISUBJECT
	 */
	public static String S_FREEMISUBJECT ="S_FREEMISUBJECT";
	/**
	 * column C_FREEMILEVEL
	 */
	public static String C_FREEMILEVEL ="C_FREEMILEVEL";
	/**
	 * column S_FREEMISIGN
	 */
	public static String S_FREEMISIGN ="S_FREEMISIGN";
	/**
	 * column S_FREEMIPTRE
	 */
	public static String S_FREEMIPTRE ="S_FREEMIPTRE";
	/**
	 * column F_FREEMIAMT
	 */
	public static String F_FREEMIAMT ="F_FREEMIAMT";
	/**
	 * column S_CORPCODE
	 */
	public static String S_CORPCODE ="S_CORPCODE";
	/**
	 * column C_TRIMSIGN
	 */
	public static String C_TRIMSIGN ="C_TRIMSIGN";
	/**
	 * column S_STATUS
	 */
	public static String S_STATUS ="S_STATUS";
	/**
	 * column S_FILENAME
	 */
	public static String S_FILENAME ="S_FILENAME";
	/**
	 * column S_BOOKORGCODE
	 */
	public static String S_BOOKORGCODE ="S_BOOKORGCODE";
	/**
	 * column TS_SYSUPDATE
	 */
	public static String TS_SYSUPDATE ="TS_SYSUPDATE";
	/**
	 * column D_ACCEPTDATE
	 */
	public static String D_ACCEPTDATE ="D_ACCEPTDATE";
	/**
	 * column D_AUDITDATE
	 */
	public static String D_AUDITDATE ="D_AUDITDATE";
	/**
	 * column S_CHANNELCODE
	 */
	public static String S_CHANNELCODE ="S_CHANNELCODE";
	/**
	 * column S_ADDWORD
	 */
	public static String S_ADDWORD ="S_ADDWORD";
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
        String[] columnNames = new String[30];        
        columnNames[0]="I_VOUSRLNO";
        columnNames[1]="S_TAXORGCODE";
        columnNames[2]="S_PACKNO";
        columnNames[3]="S_TRANO";
        columnNames[4]="D_BILLDATE";
        columnNames[5]="S_ELECTROTAXVOUNO";
        columnNames[6]="S_FREEVOUNO";
        columnNames[7]="C_FREEPLUTYPE";
        columnNames[8]="S_FREEPLUSUBJECTCODE";
        columnNames[9]="C_FREEPLULEVEL";
        columnNames[10]="S_FREEPLUSIGN";
        columnNames[11]="S_FREEPLUPTRECODE";
        columnNames[12]="F_FREEPLUAMT";
        columnNames[13]="C_FREEMIKIND";
        columnNames[14]="S_FREEMISUBJECT";
        columnNames[15]="C_FREEMILEVEL";
        columnNames[16]="S_FREEMISIGN";
        columnNames[17]="S_FREEMIPTRE";
        columnNames[18]="F_FREEMIAMT";
        columnNames[19]="S_CORPCODE";
        columnNames[20]="C_TRIMSIGN";
        columnNames[21]="S_STATUS";
        columnNames[22]="S_FILENAME";
        columnNames[23]="S_BOOKORGCODE";
        columnNames[24]="TS_SYSUPDATE";
        columnNames[25]="D_ACCEPTDATE";
        columnNames[26]="D_AUDITDATE";
        columnNames[27]="S_CHANNELCODE";
        columnNames[28]="S_ADDWORD";
        columnNames[29]="TS_UPDATE";
        return columnNames;     
    }
	/******************************************************
	*
	*  Get Column Name
	*
	*******************************************************/
	/**
	 * Getter I_VOUSRLNO, PK , NOT NULL  , Identity  , Generated 	 
	 */
	public static String columnIvousrlno(){
		 return I_VOUSRLNO;
	}
	/**
	 * Getter S_TAXORGCODE , NOT NULL   	 
	 */
	public static String columnStaxorgcode(){
		 return S_TAXORGCODE;
	}
	/**
	 * Getter S_PACKNO , NOT NULL   	 
	 */
	public static String columnSpackno(){
		 return S_PACKNO;
	}
	/**
	 * Getter S_TRANO   	 
	 */
	public static String columnStrano(){
		 return S_TRANO;
	}
	/**
	 * Getter D_BILLDATE , NOT NULL   	 
	 */
	public static String columnDbilldate(){
		 return D_BILLDATE;
	}
	/**
	 * Getter S_ELECTROTAXVOUNO   	 
	 */
	public static String columnSelectrotaxvouno(){
		 return S_ELECTROTAXVOUNO;
	}
	/**
	 * Getter S_FREEVOUNO , NOT NULL   	 
	 */
	public static String columnSfreevouno(){
		 return S_FREEVOUNO;
	}
	/**
	 * Getter C_FREEPLUTYPE , NOT NULL   	 
	 */
	public static String columnCfreeplutype(){
		 return C_FREEPLUTYPE;
	}
	/**
	 * Getter S_FREEPLUSUBJECTCODE , NOT NULL   	 
	 */
	public static String columnSfreeplusubjectcode(){
		 return S_FREEPLUSUBJECTCODE;
	}
	/**
	 * Getter C_FREEPLULEVEL , NOT NULL   	 
	 */
	public static String columnCfreeplulevel(){
		 return C_FREEPLULEVEL;
	}
	/**
	 * Getter S_FREEPLUSIGN   	 
	 */
	public static String columnSfreeplusign(){
		 return S_FREEPLUSIGN;
	}
	/**
	 * Getter S_FREEPLUPTRECODE , NOT NULL   	 
	 */
	public static String columnSfreepluptrecode(){
		 return S_FREEPLUPTRECODE;
	}
	/**
	 * Getter F_FREEPLUAMT , NOT NULL   	 
	 */
	public static String columnFfreepluamt(){
		 return F_FREEPLUAMT;
	}
	/**
	 * Getter C_FREEMIKIND , NOT NULL   	 
	 */
	public static String columnCfreemikind(){
		 return C_FREEMIKIND;
	}
	/**
	 * Getter S_FREEMISUBJECT , NOT NULL   	 
	 */
	public static String columnSfreemisubject(){
		 return S_FREEMISUBJECT;
	}
	/**
	 * Getter C_FREEMILEVEL , NOT NULL   	 
	 */
	public static String columnCfreemilevel(){
		 return C_FREEMILEVEL;
	}
	/**
	 * Getter S_FREEMISIGN   	 
	 */
	public static String columnSfreemisign(){
		 return S_FREEMISIGN;
	}
	/**
	 * Getter S_FREEMIPTRE , NOT NULL   	 
	 */
	public static String columnSfreemiptre(){
		 return S_FREEMIPTRE;
	}
	/**
	 * Getter F_FREEMIAMT , NOT NULL   	 
	 */
	public static String columnFfreemiamt(){
		 return F_FREEMIAMT;
	}
	/**
	 * Getter S_CORPCODE   	 
	 */
	public static String columnScorpcode(){
		 return S_CORPCODE;
	}
	/**
	 * Getter C_TRIMSIGN , NOT NULL   	 
	 */
	public static String columnCtrimsign(){
		 return C_TRIMSIGN;
	}
	/**
	 * Getter S_STATUS   	 
	 */
	public static String columnSstatus(){
		 return S_STATUS;
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 */
	public static String columnSfilename(){
		 return S_FILENAME;
	}
	/**
	 * Getter S_BOOKORGCODE , NOT NULL   	 
	 */
	public static String columnSbookorgcode(){
		 return S_BOOKORGCODE;
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 */
	public static String columnTssysupdate(){
		 return TS_SYSUPDATE;
	}
	/**
	 * Getter D_ACCEPTDATE , NOT NULL   	 
	 */
	public static String columnDacceptdate(){
		 return D_ACCEPTDATE;
	}
	/**
	 * Getter D_AUDITDATE   	 
	 */
	public static String columnDauditdate(){
		 return D_AUDITDATE;
	}
	/**
	 * Getter S_CHANNELCODE   	 
	 */
	public static String columnSchannelcode(){
		 return S_CHANNELCODE;
	}
	/**
	 * Getter S_ADDWORD   	 
	 */
	public static String columnSaddword(){
		 return S_ADDWORD;
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
	 * Getter I_VOUSRLNO, PK , NOT NULL  , Identity  , Generated 	 
	 */
	public static String columnJavaTypeIvousrlno(){
		 return "Long";
	}
	/**
	 * Getter S_TAXORGCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeStaxorgcode(){
		 return "String";
	}
	/**
	 * Getter S_PACKNO , NOT NULL   	 
	 */
	public static String columnJavaTypeSpackno(){
		 return "String";
	}
	/**
	 * Getter S_TRANO   	 
	 */
	public static String columnJavaTypeStrano(){
		 return "String";
	}
	/**
	 * Getter D_BILLDATE , NOT NULL   	 
	 */
	public static String columnJavaTypeDbilldate(){
		 return "Date";
	}
	/**
	 * Getter S_ELECTROTAXVOUNO   	 
	 */
	public static String columnJavaTypeSelectrotaxvouno(){
		 return "String";
	}
	/**
	 * Getter S_FREEVOUNO , NOT NULL   	 
	 */
	public static String columnJavaTypeSfreevouno(){
		 return "String";
	}
	/**
	 * Getter C_FREEPLUTYPE , NOT NULL   	 
	 */
	public static String columnJavaTypeCfreeplutype(){
		 return "String";
	}
	/**
	 * Getter S_FREEPLUSUBJECTCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSfreeplusubjectcode(){
		 return "String";
	}
	/**
	 * Getter C_FREEPLULEVEL , NOT NULL   	 
	 */
	public static String columnJavaTypeCfreeplulevel(){
		 return "String";
	}
	/**
	 * Getter S_FREEPLUSIGN   	 
	 */
	public static String columnJavaTypeSfreeplusign(){
		 return "String";
	}
	/**
	 * Getter S_FREEPLUPTRECODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSfreepluptrecode(){
		 return "String";
	}
	/**
	 * Getter F_FREEPLUAMT , NOT NULL   	 
	 */
	public static String columnJavaTypeFfreepluamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter C_FREEMIKIND , NOT NULL   	 
	 */
	public static String columnJavaTypeCfreemikind(){
		 return "String";
	}
	/**
	 * Getter S_FREEMISUBJECT , NOT NULL   	 
	 */
	public static String columnJavaTypeSfreemisubject(){
		 return "String";
	}
	/**
	 * Getter C_FREEMILEVEL , NOT NULL   	 
	 */
	public static String columnJavaTypeCfreemilevel(){
		 return "String";
	}
	/**
	 * Getter S_FREEMISIGN   	 
	 */
	public static String columnJavaTypeSfreemisign(){
		 return "String";
	}
	/**
	 * Getter S_FREEMIPTRE , NOT NULL   	 
	 */
	public static String columnJavaTypeSfreemiptre(){
		 return "String";
	}
	/**
	 * Getter F_FREEMIAMT , NOT NULL   	 
	 */
	public static String columnJavaTypeFfreemiamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter S_CORPCODE   	 
	 */
	public static String columnJavaTypeScorpcode(){
		 return "String";
	}
	/**
	 * Getter C_TRIMSIGN , NOT NULL   	 
	 */
	public static String columnJavaTypeCtrimsign(){
		 return "String";
	}
	/**
	 * Getter S_STATUS   	 
	 */
	public static String columnJavaTypeSstatus(){
		 return "String";
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSfilename(){
		 return "String";
	}
	/**
	 * Getter S_BOOKORGCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSbookorgcode(){
		 return "String";
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 */
	public static String columnJavaTypeTssysupdate(){
		 return "Timestamp";
	}
	/**
	 * Getter D_ACCEPTDATE , NOT NULL   	 
	 */
	public static String columnJavaTypeDacceptdate(){
		 return "Date";
	}
	/**
	 * Getter D_AUDITDATE   	 
	 */
	public static String columnJavaTypeDauditdate(){
		 return "Date";
	}
	/**
	 * Getter S_CHANNELCODE   	 
	 */
	public static String columnJavaTypeSchannelcode(){
		 return "String";
	}
	/**
	 * Getter S_ADDWORD   	 
	 */
	public static String columnJavaTypeSaddword(){
		 return "String";
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
	 * Getter I_VOUSRLNO, PK , NOT NULL  , Identity  , Generated 	 
	 * columnType is BIGINT
	 */
	public static String columnDatabaseTypeIvousrlno(){
		 return "BIGINT";
	}
	/**
	 * Getter S_TAXORGCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStaxorgcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PACKNO , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSpackno(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_TRANO   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeStrano(){
		 return "CHARACTER";
	}
	/**
	 * Getter D_BILLDATE , NOT NULL   	 
	 * columnType is DATE
	 */
	public static String columnDatabaseTypeDbilldate(){
		 return "DATE";
	}
	/**
	 * Getter S_ELECTROTAXVOUNO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSelectrotaxvouno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_FREEVOUNO , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSfreevouno(){
		 return "CHARACTER";
	}
	/**
	 * Getter C_FREEPLUTYPE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCfreeplutype(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_FREEPLUSUBJECTCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSfreeplusubjectcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter C_FREEPLULEVEL , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCfreeplulevel(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_FREEPLUSIGN   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSfreeplusign(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_FREEPLUPTRECODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSfreepluptrecode(){
		 return "CHARACTER";
	}
	/**
	 * Getter F_FREEPLUAMT , NOT NULL   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeFfreepluamt(){
		 return "DECIMAL";
	}
	/**
	 * Getter C_FREEMIKIND , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCfreemikind(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_FREEMISUBJECT , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSfreemisubject(){
		 return "VARCHAR";
	}
	/**
	 * Getter C_FREEMILEVEL , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCfreemilevel(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_FREEMISIGN   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSfreemisign(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_FREEMIPTRE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSfreemiptre(){
		 return "CHARACTER";
	}
	/**
	 * Getter F_FREEMIAMT , NOT NULL   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeFfreemiamt(){
		 return "DECIMAL";
	}
	/**
	 * Getter S_CORPCODE   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeScorpcode(){
		 return "CHARACTER";
	}
	/**
	 * Getter C_TRIMSIGN , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCtrimsign(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_STATUS   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSstatus(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSfilename(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_BOOKORGCODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSbookorgcode(){
		 return "CHARACTER";
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 * columnType is TIMESTAMP
	 */
	public static String columnDatabaseTypeTssysupdate(){
		 return "TIMESTAMP";
	}
	/**
	 * Getter D_ACCEPTDATE , NOT NULL   	 
	 * columnType is DATE
	 */
	public static String columnDatabaseTypeDacceptdate(){
		 return "DATE";
	}
	/**
	 * Getter D_AUDITDATE   	 
	 * columnType is DATE
	 */
	public static String columnDatabaseTypeDauditdate(){
		 return "DATE";
	}
	/**
	 * Getter S_CHANNELCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSchannelcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ADDWORD   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSaddword(){
		 return "VARCHAR";
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
	 * Getter S_TAXORGCODE , NOT NULL   	 
	 */
	public static int columnWidthStaxorgcode(){
		 return 12;
	}
	/**
	 * Getter S_PACKNO , NOT NULL   	 
	 */
	public static int columnWidthSpackno(){
		 return 8;
	}
	/**
	 * Getter S_TRANO   	 
	 */
	public static int columnWidthStrano(){
		 return 8;
	}
	/**
	 * Getter S_ELECTROTAXVOUNO   	 
	 */
	public static int columnWidthSelectrotaxvouno(){
		 return 20;
	}
	/**
	 * Getter S_FREEVOUNO , NOT NULL   	 
	 */
	public static int columnWidthSfreevouno(){
		 return 8;
	}
	/**
	 * Getter C_FREEPLUTYPE , NOT NULL   	 
	 */
	public static int columnWidthCfreeplutype(){
		 return 1;
	}
	/**
	 * Getter S_FREEPLUSUBJECTCODE , NOT NULL   	 
	 */
	public static int columnWidthSfreeplusubjectcode(){
		 return 30;
	}
	/**
	 * Getter C_FREEPLULEVEL , NOT NULL   	 
	 */
	public static int columnWidthCfreeplulevel(){
		 return 1;
	}
	/**
	 * Getter S_FREEPLUSIGN   	 
	 */
	public static int columnWidthSfreeplusign(){
		 return 35;
	}
	/**
	 * Getter S_FREEPLUPTRECODE , NOT NULL   	 
	 */
	public static int columnWidthSfreepluptrecode(){
		 return 10;
	}
	/**
	 * Getter C_FREEMIKIND , NOT NULL   	 
	 */
	public static int columnWidthCfreemikind(){
		 return 1;
	}
	/**
	 * Getter S_FREEMISUBJECT , NOT NULL   	 
	 */
	public static int columnWidthSfreemisubject(){
		 return 30;
	}
	/**
	 * Getter C_FREEMILEVEL , NOT NULL   	 
	 */
	public static int columnWidthCfreemilevel(){
		 return 1;
	}
	/**
	 * Getter S_FREEMISIGN   	 
	 */
	public static int columnWidthSfreemisign(){
		 return 35;
	}
	/**
	 * Getter S_FREEMIPTRE , NOT NULL   	 
	 */
	public static int columnWidthSfreemiptre(){
		 return 10;
	}
	/**
	 * Getter S_CORPCODE   	 
	 */
	public static int columnWidthScorpcode(){
		 return 20;
	}
	/**
	 * Getter C_TRIMSIGN , NOT NULL   	 
	 */
	public static int columnWidthCtrimsign(){
		 return 1;
	}
	/**
	 * Getter S_STATUS   	 
	 */
	public static int columnWidthSstatus(){
		 return 5;
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 */
	public static int columnWidthSfilename(){
		 return 100;
	}
	/**
	 * Getter S_BOOKORGCODE , NOT NULL   	 
	 */
	public static int columnWidthSbookorgcode(){
		 return 12;
	}
	/**
	 * Getter S_CHANNELCODE   	 
	 */
	public static int columnWidthSchannelcode(){
		 return 15;
	}
	/**
	 * Getter S_ADDWORD   	 
	 */
	public static int columnWidthSaddword(){
		 return 120;
	}
}
      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: HTV_VOUCHERINFO</p>
 * <p>Description:  Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:57 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class HtvVoucherinfoInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "HTV_VOUCHERINFO";
	
	/**
	 * column S_DEALNO
	 */
	public static String S_DEALNO ="S_DEALNO";
	/**
	 * column S_ORGCODE
	 */
	public static String S_ORGCODE ="S_ORGCODE";
	/**
	 * column S_TRECODE
	 */
	public static String S_TRECODE ="S_TRECODE";
	/**
	 * column S_FILENAME
	 */
	public static String S_FILENAME ="S_FILENAME";
	/**
	 * column S_ADMDIVCODE
	 */
	public static String S_ADMDIVCODE ="S_ADMDIVCODE";
	/**
	 * column S_STYEAR
	 */
	public static String S_STYEAR ="S_STYEAR";
	/**
	 * column S_VTCODE
	 */
	public static String S_VTCODE ="S_VTCODE";
	/**
	 * column S_VOUCHERNO
	 */
	public static String S_VOUCHERNO ="S_VOUCHERNO";
	/**
	 * column S_VOUCHERFLAG
	 */
	public static String S_VOUCHERFLAG ="S_VOUCHERFLAG";
	/**
	 * column S_ATTACH
	 */
	public static String S_ATTACH ="S_ATTACH";
	/**
	 * column S_RETURNERRMSG
	 */
	public static String S_RETURNERRMSG ="S_RETURNERRMSG";
	/**
	 * column S_CREATDATE
	 */
	public static String S_CREATDATE ="S_CREATDATE";
	/**
	 * column N_MONEY
	 */
	public static String N_MONEY ="N_MONEY";
	/**
	 * column S_STATUS
	 */
	public static String S_STATUS ="S_STATUS";
	/**
	 * column S_CONFIRUSERCODE
	 */
	public static String S_CONFIRUSERCODE ="S_CONFIRUSERCODE";
	/**
	 * column S_VERIFYUSERCODE
	 */
	public static String S_VERIFYUSERCODE ="S_VERIFYUSERCODE";
	/**
	 * column S_DEMO
	 */
	public static String S_DEMO ="S_DEMO";
	/**
	 * column S_STAMPID
	 */
	public static String S_STAMPID ="S_STAMPID";
	/**
	 * column S_PACKNO
	 */
	public static String S_PACKNO ="S_PACKNO";
	/**
	 * column I_COUNT
	 */
	public static String I_COUNT ="I_COUNT";
	/**
	 * column N_CHECKMONEY
	 */
	public static String N_CHECKMONEY ="N_CHECKMONEY";
	/**
	 * column I_CHECKCOUNT
	 */
	public static String I_CHECKCOUNT ="I_CHECKCOUNT";
	/**
	 * column TS_SYSUPDATE
	 */
	public static String TS_SYSUPDATE ="TS_SYSUPDATE";
	/**
	 * column S_STAMPUSER
	 */
	public static String S_STAMPUSER ="S_STAMPUSER";
	/**
	 * column S_CHECKDATE
	 */
	public static String S_CHECKDATE ="S_CHECKDATE";
	/**
	 * column S_CHECKVOUCHERTYPE
	 */
	public static String S_CHECKVOUCHERTYPE ="S_CHECKVOUCHERTYPE";
	/**
	 * column S_PAYBANKCODE
	 */
	public static String S_PAYBANKCODE ="S_PAYBANKCODE";
	/**
	 * column S_RECVTIME
	 */
	public static String S_RECVTIME ="S_RECVTIME";
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
	 * column S_EXT1
	 */
	public static String S_EXT1 ="S_EXT1";
	/**
	 * column S_EXT2
	 */
	public static String S_EXT2 ="S_EXT2";
	/**
	 * column S_EXT3
	 */
	public static String S_EXT3 ="S_EXT3";
	/**
	 * column S_EXT4
	 */
	public static String S_EXT4 ="S_EXT4";
	/**
	 * column S_EXT5
	 */
	public static String S_EXT5 ="S_EXT5";
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
        String[] columnNames = new String[37];        
        columnNames[0]="S_DEALNO";
        columnNames[1]="S_ORGCODE";
        columnNames[2]="S_TRECODE";
        columnNames[3]="S_FILENAME";
        columnNames[4]="S_ADMDIVCODE";
        columnNames[5]="S_STYEAR";
        columnNames[6]="S_VTCODE";
        columnNames[7]="S_VOUCHERNO";
        columnNames[8]="S_VOUCHERFLAG";
        columnNames[9]="S_ATTACH";
        columnNames[10]="S_RETURNERRMSG";
        columnNames[11]="S_CREATDATE";
        columnNames[12]="N_MONEY";
        columnNames[13]="S_STATUS";
        columnNames[14]="S_CONFIRUSERCODE";
        columnNames[15]="S_VERIFYUSERCODE";
        columnNames[16]="S_DEMO";
        columnNames[17]="S_STAMPID";
        columnNames[18]="S_PACKNO";
        columnNames[19]="I_COUNT";
        columnNames[20]="N_CHECKMONEY";
        columnNames[21]="I_CHECKCOUNT";
        columnNames[22]="TS_SYSUPDATE";
        columnNames[23]="S_STAMPUSER";
        columnNames[24]="S_CHECKDATE";
        columnNames[25]="S_CHECKVOUCHERTYPE";
        columnNames[26]="S_PAYBANKCODE";
        columnNames[27]="S_RECVTIME";
        columnNames[28]="S_HOLD1";
        columnNames[29]="S_HOLD2";
        columnNames[30]="S_HOLD3";
        columnNames[31]="S_HOLD4";
        columnNames[32]="S_EXT1";
        columnNames[33]="S_EXT2";
        columnNames[34]="S_EXT3";
        columnNames[35]="S_EXT4";
        columnNames[36]="S_EXT5";
        return columnNames;     
    }
	/******************************************************
	*
	*  Get Column Name
	*
	*******************************************************/
	/**
	 * Getter S_DEALNO, PK , NOT NULL   	 
	 */
	public static String columnSdealno(){
		 return S_DEALNO;
	}
	/**
	 * Getter S_ORGCODE , NOT NULL   	 
	 */
	public static String columnSorgcode(){
		 return S_ORGCODE;
	}
	/**
	 * Getter S_TRECODE , NOT NULL   	 
	 */
	public static String columnStrecode(){
		 return S_TRECODE;
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 */
	public static String columnSfilename(){
		 return S_FILENAME;
	}
	/**
	 * Getter S_ADMDIVCODE , NOT NULL   	 
	 */
	public static String columnSadmdivcode(){
		 return S_ADMDIVCODE;
	}
	/**
	 * Getter S_STYEAR , NOT NULL   	 
	 */
	public static String columnSstyear(){
		 return S_STYEAR;
	}
	/**
	 * Getter S_VTCODE , NOT NULL   	 
	 */
	public static String columnSvtcode(){
		 return S_VTCODE;
	}
	/**
	 * Getter S_VOUCHERNO , NOT NULL   	 
	 */
	public static String columnSvoucherno(){
		 return S_VOUCHERNO;
	}
	/**
	 * Getter S_VOUCHERFLAG , NOT NULL   	 
	 */
	public static String columnSvoucherflag(){
		 return S_VOUCHERFLAG;
	}
	/**
	 * Getter S_ATTACH   	 
	 */
	public static String columnSattach(){
		 return S_ATTACH;
	}
	/**
	 * Getter S_RETURNERRMSG   	 
	 */
	public static String columnSreturnerrmsg(){
		 return S_RETURNERRMSG;
	}
	/**
	 * Getter S_CREATDATE , NOT NULL   	 
	 */
	public static String columnScreatdate(){
		 return S_CREATDATE;
	}
	/**
	 * Getter N_MONEY , NOT NULL   	 
	 */
	public static String columnNmoney(){
		 return N_MONEY;
	}
	/**
	 * Getter S_STATUS , NOT NULL   	 
	 */
	public static String columnSstatus(){
		 return S_STATUS;
	}
	/**
	 * Getter S_CONFIRUSERCODE   	 
	 */
	public static String columnSconfirusercode(){
		 return S_CONFIRUSERCODE;
	}
	/**
	 * Getter S_VERIFYUSERCODE   	 
	 */
	public static String columnSverifyusercode(){
		 return S_VERIFYUSERCODE;
	}
	/**
	 * Getter S_DEMO   	 
	 */
	public static String columnSdemo(){
		 return S_DEMO;
	}
	/**
	 * Getter S_STAMPID   	 
	 */
	public static String columnSstampid(){
		 return S_STAMPID;
	}
	/**
	 * Getter S_PACKNO   	 
	 */
	public static String columnSpackno(){
		 return S_PACKNO;
	}
	/**
	 * Getter I_COUNT   	 
	 */
	public static String columnIcount(){
		 return I_COUNT;
	}
	/**
	 * Getter N_CHECKMONEY   	 
	 */
	public static String columnNcheckmoney(){
		 return N_CHECKMONEY;
	}
	/**
	 * Getter I_CHECKCOUNT   	 
	 */
	public static String columnIcheckcount(){
		 return I_CHECKCOUNT;
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 */
	public static String columnTssysupdate(){
		 return TS_SYSUPDATE;
	}
	/**
	 * Getter S_STAMPUSER   	 
	 */
	public static String columnSstampuser(){
		 return S_STAMPUSER;
	}
	/**
	 * Getter S_CHECKDATE   	 
	 */
	public static String columnScheckdate(){
		 return S_CHECKDATE;
	}
	/**
	 * Getter S_CHECKVOUCHERTYPE   	 
	 */
	public static String columnScheckvouchertype(){
		 return S_CHECKVOUCHERTYPE;
	}
	/**
	 * Getter S_PAYBANKCODE   	 
	 */
	public static String columnSpaybankcode(){
		 return S_PAYBANKCODE;
	}
	/**
	 * Getter S_RECVTIME   	 
	 */
	public static String columnSrecvtime(){
		 return S_RECVTIME;
	}
	/**
	 * Getter S_HOLD1   	 
	 */
	public static String columnShold1(){
		 return S_HOLD1;
	}
	/**
	 * Getter S_HOLD2   	 
	 */
	public static String columnShold2(){
		 return S_HOLD2;
	}
	/**
	 * Getter S_HOLD3   	 
	 */
	public static String columnShold3(){
		 return S_HOLD3;
	}
	/**
	 * Getter S_HOLD4   	 
	 */
	public static String columnShold4(){
		 return S_HOLD4;
	}
	/**
	 *备用 Getter S_EXT1   	 
	 */
	public static String columnSext1(){
		 return S_EXT1;
	}
	/**
	 *备用 Getter S_EXT2   	 
	 */
	public static String columnSext2(){
		 return S_EXT2;
	}
	/**
	 *备用 Getter S_EXT3   	 
	 */
	public static String columnSext3(){
		 return S_EXT3;
	}
	/**
	 *备用 Getter S_EXT4   	 
	 */
	public static String columnSext4(){
		 return S_EXT4;
	}
	/**
	 *备用 Getter S_EXT5   	 
	 */
	public static String columnSext5(){
		 return S_EXT5;
	}
	/******************************************************
	*
	*  Get Column Java Type
	*
	*******************************************************/
	/**
	 * Getter S_DEALNO, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeSdealno(){
		 return "String";
	}
	/**
	 * Getter S_ORGCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSorgcode(){
		 return "String";
	}
	/**
	 * Getter S_TRECODE , NOT NULL   	 
	 */
	public static String columnJavaTypeStrecode(){
		 return "String";
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSfilename(){
		 return "String";
	}
	/**
	 * Getter S_ADMDIVCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSadmdivcode(){
		 return "String";
	}
	/**
	 * Getter S_STYEAR , NOT NULL   	 
	 */
	public static String columnJavaTypeSstyear(){
		 return "String";
	}
	/**
	 * Getter S_VTCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSvtcode(){
		 return "String";
	}
	/**
	 * Getter S_VOUCHERNO , NOT NULL   	 
	 */
	public static String columnJavaTypeSvoucherno(){
		 return "String";
	}
	/**
	 * Getter S_VOUCHERFLAG , NOT NULL   	 
	 */
	public static String columnJavaTypeSvoucherflag(){
		 return "String";
	}
	/**
	 * Getter S_ATTACH   	 
	 */
	public static String columnJavaTypeSattach(){
		 return "String";
	}
	/**
	 * Getter S_RETURNERRMSG   	 
	 */
	public static String columnJavaTypeSreturnerrmsg(){
		 return "String";
	}
	/**
	 * Getter S_CREATDATE , NOT NULL   	 
	 */
	public static String columnJavaTypeScreatdate(){
		 return "String";
	}
	/**
	 * Getter N_MONEY , NOT NULL   	 
	 */
	public static String columnJavaTypeNmoney(){
		 return "BigDecimal";
	}
	/**
	 * Getter S_STATUS , NOT NULL   	 
	 */
	public static String columnJavaTypeSstatus(){
		 return "String";
	}
	/**
	 * Getter S_CONFIRUSERCODE   	 
	 */
	public static String columnJavaTypeSconfirusercode(){
		 return "String";
	}
	/**
	 * Getter S_VERIFYUSERCODE   	 
	 */
	public static String columnJavaTypeSverifyusercode(){
		 return "String";
	}
	/**
	 * Getter S_DEMO   	 
	 */
	public static String columnJavaTypeSdemo(){
		 return "String";
	}
	/**
	 * Getter S_STAMPID   	 
	 */
	public static String columnJavaTypeSstampid(){
		 return "String";
	}
	/**
	 * Getter S_PACKNO   	 
	 */
	public static String columnJavaTypeSpackno(){
		 return "String";
	}
	/**
	 * Getter I_COUNT   	 
	 */
	public static String columnJavaTypeIcount(){
		 return "Integer";
	}
	/**
	 * Getter N_CHECKMONEY   	 
	 */
	public static String columnJavaTypeNcheckmoney(){
		 return "BigDecimal";
	}
	/**
	 * Getter I_CHECKCOUNT   	 
	 */
	public static String columnJavaTypeIcheckcount(){
		 return "Integer";
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 */
	public static String columnJavaTypeTssysupdate(){
		 return "Timestamp";
	}
	/**
	 * Getter S_STAMPUSER   	 
	 */
	public static String columnJavaTypeSstampuser(){
		 return "String";
	}
	/**
	 * Getter S_CHECKDATE   	 
	 */
	public static String columnJavaTypeScheckdate(){
		 return "String";
	}
	/**
	 * Getter S_CHECKVOUCHERTYPE   	 
	 */
	public static String columnJavaTypeScheckvouchertype(){
		 return "String";
	}
	/**
	 * Getter S_PAYBANKCODE   	 
	 */
	public static String columnJavaTypeSpaybankcode(){
		 return "String";
	}
	/**
	 * Getter S_RECVTIME   	 
	 */
	public static String columnJavaTypeSrecvtime(){
		 return "Timestamp";
	}
	/**
	 * Getter S_HOLD1   	 
	 */
	public static String columnJavaTypeShold1(){
		 return "String";
	}
	/**
	 * Getter S_HOLD2   	 
	 */
	public static String columnJavaTypeShold2(){
		 return "String";
	}
	/**
	 * Getter S_HOLD3   	 
	 */
	public static String columnJavaTypeShold3(){
		 return "String";
	}
	/**
	 * Getter S_HOLD4   	 
	 */
	public static String columnJavaTypeShold4(){
		 return "String";
	}
	/**
	 *备用 Getter S_EXT1   	 
	 */
	public static String columnJavaTypeSext1(){
		 return "String";
	}
	/**
	 *备用 Getter S_EXT2   	 
	 */
	public static String columnJavaTypeSext2(){
		 return "String";
	}
	/**
	 *备用 Getter S_EXT3   	 
	 */
	public static String columnJavaTypeSext3(){
		 return "String";
	}
	/**
	 *备用 Getter S_EXT4   	 
	 */
	public static String columnJavaTypeSext4(){
		 return "String";
	}
	/**
	 *备用 Getter S_EXT5   	 
	 */
	public static String columnJavaTypeSext5(){
		 return "String";
	}
	/******************************************************
	*
	*  Get Column Database Type
	*
	*******************************************************/
	/**
	 * Getter S_DEALNO, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSdealno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ORGCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSorgcode(){
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
	 * Getter S_FILENAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSfilename(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ADMDIVCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSadmdivcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_STYEAR , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSstyear(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_VTCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSvtcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_VOUCHERNO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSvoucherno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_VOUCHERFLAG , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSvoucherflag(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ATTACH   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSattach(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_RETURNERRMSG   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSreturnerrmsg(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CREATDATE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeScreatdate(){
		 return "CHARACTER";
	}
	/**
	 * Getter N_MONEY , NOT NULL   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNmoney(){
		 return "DECIMAL";
	}
	/**
	 * Getter S_STATUS , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSstatus(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CONFIRUSERCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSconfirusercode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_VERIFYUSERCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSverifyusercode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_DEMO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSdemo(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_STAMPID   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSstampid(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PACKNO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpackno(){
		 return "VARCHAR";
	}
	/**
	 * Getter I_COUNT   	 
	 * columnType is INTEGER
	 */
	public static String columnDatabaseTypeIcount(){
		 return "INTEGER";
	}
	/**
	 * Getter N_CHECKMONEY   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNcheckmoney(){
		 return "DECIMAL";
	}
	/**
	 * Getter I_CHECKCOUNT   	 
	 * columnType is INTEGER
	 */
	public static String columnDatabaseTypeIcheckcount(){
		 return "INTEGER";
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 * columnType is TIMESTAMP
	 */
	public static String columnDatabaseTypeTssysupdate(){
		 return "TIMESTAMP";
	}
	/**
	 * Getter S_STAMPUSER   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSstampuser(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CHECKDATE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeScheckdate(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CHECKVOUCHERTYPE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeScheckvouchertype(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYBANKCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpaybankcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_RECVTIME   	 
	 * columnType is TIMESTAMP
	 */
	public static String columnDatabaseTypeSrecvtime(){
		 return "TIMESTAMP";
	}
	/**
	 * Getter S_HOLD1   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeShold1(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_HOLD2   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeShold2(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_HOLD3   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeShold3(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_HOLD4   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeShold4(){
		 return "VARCHAR";
	}
	/**
	 *备用 Getter S_EXT1   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSext1(){
		 return "VARCHAR";
	}
	/**
	 *备用 Getter S_EXT2   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSext2(){
		 return "VARCHAR";
	}
	/**
	 *备用 Getter S_EXT3   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSext3(){
		 return "VARCHAR";
	}
	/**
	 *备用 Getter S_EXT4   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSext4(){
		 return "VARCHAR";
	}
	/**
	 *备用 Getter S_EXT5   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSext5(){
		 return "VARCHAR";
	}
	/******************************************************
	*
	*  Get Column With
	*
	*******************************************************/
	/**
	 * Getter S_DEALNO, PK , NOT NULL   	 
	 */
	public static int columnWidthSdealno(){
		 return 16;
	}
	/**
	 * Getter S_ORGCODE , NOT NULL   	 
	 */
	public static int columnWidthSorgcode(){
		 return 12;
	}
	/**
	 * Getter S_TRECODE , NOT NULL   	 
	 */
	public static int columnWidthStrecode(){
		 return 10;
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 */
	public static int columnWidthSfilename(){
		 return 100;
	}
	/**
	 * Getter S_ADMDIVCODE , NOT NULL   	 
	 */
	public static int columnWidthSadmdivcode(){
		 return 10;
	}
	/**
	 * Getter S_STYEAR , NOT NULL   	 
	 */
	public static int columnWidthSstyear(){
		 return 4;
	}
	/**
	 * Getter S_VTCODE , NOT NULL   	 
	 */
	public static int columnWidthSvtcode(){
		 return 10;
	}
	/**
	 * Getter S_VOUCHERNO , NOT NULL   	 
	 */
	public static int columnWidthSvoucherno(){
		 return 42;
	}
	/**
	 * Getter S_VOUCHERFLAG , NOT NULL   	 
	 */
	public static int columnWidthSvoucherflag(){
		 return 1;
	}
	/**
	 * Getter S_ATTACH   	 
	 */
	public static int columnWidthSattach(){
		 return 32;
	}
	/**
	 * Getter S_RETURNERRMSG   	 
	 */
	public static int columnWidthSreturnerrmsg(){
		 return 1024;
	}
	/**
	 * Getter S_CREATDATE , NOT NULL   	 
	 */
	public static int columnWidthScreatdate(){
		 return 8;
	}
	/**
	 * Getter S_STATUS , NOT NULL   	 
	 */
	public static int columnWidthSstatus(){
		 return 4;
	}
	/**
	 * Getter S_CONFIRUSERCODE   	 
	 */
	public static int columnWidthSconfirusercode(){
		 return 12;
	}
	/**
	 * Getter S_VERIFYUSERCODE   	 
	 */
	public static int columnWidthSverifyusercode(){
		 return 12;
	}
	/**
	 * Getter S_DEMO   	 
	 */
	public static int columnWidthSdemo(){
		 return 1024;
	}
	/**
	 * Getter S_STAMPID   	 
	 */
	public static int columnWidthSstampid(){
		 return 100;
	}
	/**
	 * Getter S_PACKNO   	 
	 */
	public static int columnWidthSpackno(){
		 return 8;
	}
	/**
	 * Getter S_STAMPUSER   	 
	 */
	public static int columnWidthSstampuser(){
		 return 100;
	}
	/**
	 * Getter S_CHECKDATE   	 
	 */
	public static int columnWidthScheckdate(){
		 return 8;
	}
	/**
	 * Getter S_CHECKVOUCHERTYPE   	 
	 */
	public static int columnWidthScheckvouchertype(){
		 return 4;
	}
	/**
	 * Getter S_PAYBANKCODE   	 
	 */
	public static int columnWidthSpaybankcode(){
		 return 12;
	}
	/**
	 * Getter S_HOLD1   	 
	 */
	public static int columnWidthShold1(){
		 return 60;
	}
	/**
	 * Getter S_HOLD2   	 
	 */
	public static int columnWidthShold2(){
		 return 60;
	}
	/**
	 * Getter S_HOLD3   	 
	 */
	public static int columnWidthShold3(){
		 return 60;
	}
	/**
	 * Getter S_HOLD4   	 
	 */
	public static int columnWidthShold4(){
		 return 60;
	}
	/**
	 *备用 Getter S_EXT1   	 
	 */
	public static int columnWidthSext1(){
		 return 50;
	}
	/**
	 *备用 Getter S_EXT2   	 
	 */
	public static int columnWidthSext2(){
		 return 50;
	}
	/**
	 *备用 Getter S_EXT3   	 
	 */
	public static int columnWidthSext3(){
		 return 50;
	}
	/**
	 *备用 Getter S_EXT4   	 
	 */
	public static int columnWidthSext4(){
		 return 50;
	}
	/**
	 *备用 Getter S_EXT5   	 
	 */
	public static int columnWidthSext5(){
		 return 50;
	}
}
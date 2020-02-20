      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: HTF_PAYMENT_DETAILSMAIN</p>
 * <p>Description:  Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:54 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class HtfPaymentDetailsmainInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "HTF_PAYMENT_DETAILSMAIN";
	
	/**
	 * column I_VOUSRLNO
	 */
	public static String I_VOUSRLNO ="I_VOUSRLNO";
	/**
	 * column S_ORGCODE
	 */
	public static String S_ORGCODE ="S_ORGCODE";
	/**
	 * column S_TRECODE
	 */
	public static String S_TRECODE ="S_TRECODE";
	/**
	 * column S_STATUS
	 */
	public static String S_STATUS ="S_STATUS";
	/**
	 * column S_DEMO
	 */
	public static String S_DEMO ="S_DEMO";
	/**
	 * column S_FILENAME
	 */
	public static String S_FILENAME ="S_FILENAME";
	/**
	 * column TS_SYSUPDATE
	 */
	public static String TS_SYSUPDATE ="TS_SYSUPDATE";
	/**
	 * column S_PACKAGENO
	 */
	public static String S_PACKAGENO ="S_PACKAGENO";
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
	 * column S_VOUDATE
	 */
	public static String S_VOUDATE ="S_VOUDATE";
	/**
	 * column S_VOUCHERNO
	 */
	public static String S_VOUCHERNO ="S_VOUCHERNO";
	/**
	 * column S_ORIGINALVTCODE
	 */
	public static String S_ORIGINALVTCODE ="S_ORIGINALVTCODE";
	/**
	 * column S_ORIGINALVOUCHERNO
	 */
	public static String S_ORIGINALVOUCHERNO ="S_ORIGINALVOUCHERNO";
	/**
	 * column N_SUMAMT
	 */
	public static String N_SUMAMT ="N_SUMAMT";
	/**
	 * column S_AGENCYCODE
	 */
	public static String S_AGENCYCODE ="S_AGENCYCODE";
	/**
	 * column S_AGENCYNAME
	 */
	public static String S_AGENCYNAME ="S_AGENCYNAME";
	/**
	 * column S_PAYACCTNO
	 */
	public static String S_PAYACCTNO ="S_PAYACCTNO";
	/**
	 * column S_PAYACCTNAME
	 */
	public static String S_PAYACCTNAME ="S_PAYACCTNAME";
	/**
	 * column S_PAYACCTBANKNAME
	 */
	public static String S_PAYACCTBANKNAME ="S_PAYACCTBANKNAME";
	/**
	 * column S_PAYBANKCODE
	 */
	public static String S_PAYBANKCODE ="S_PAYBANKCODE";
	/**
	 * column S_PAYBANKNAME
	 */
	public static String S_PAYBANKNAME ="S_PAYBANKNAME";
	/**
	 * column S_BUSINESSTYPECODE
	 */
	public static String S_BUSINESSTYPECODE ="S_BUSINESSTYPECODE";
	/**
	 * column S_BUSINESSTYPENAME
	 */
	public static String S_BUSINESSTYPENAME ="S_BUSINESSTYPENAME";
	/**
	 * column S_PAYTYPECODE
	 */
	public static String S_PAYTYPECODE ="S_PAYTYPECODE";
	/**
	 * column S_PAYTYPENAME
	 */
	public static String S_PAYTYPENAME ="S_PAYTYPENAME";
	/**
	 * column S_XPAYDATE
	 */
	public static String S_XPAYDATE ="S_XPAYDATE";
	/**
	 * column N_XSUMAMT
	 */
	public static String N_XSUMAMT ="N_XSUMAMT";
	/**
	 * column S_HOLD1
	 */
	public static String S_HOLD1 ="S_HOLD1";
	/**
	 * column S_HOLD2
	 */
	public static String S_HOLD2 ="S_HOLD2";
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
	 * column S_FUNDTYPECODE
	 */
	public static String S_FUNDTYPECODE ="S_FUNDTYPECODE";
	/**
	 * column S_FUNDTYPENAME
	 */
	public static String S_FUNDTYPENAME ="S_FUNDTYPENAME";
	/**
	 * column S_PAYDICTATENO
	 */
	public static String S_PAYDICTATENO ="S_PAYDICTATENO";
	/**
	 * column S_PAYMSGNO
	 */
	public static String S_PAYMSGNO ="S_PAYMSGNO";
	/**
	 * column S_PAYENTRUSTDATE
	 */
	public static String S_PAYENTRUSTDATE ="S_PAYENTRUSTDATE";
	/**
	 * column S_PAYSNDBNKNO
	 */
	public static String S_PAYSNDBNKNO ="S_PAYSNDBNKNO";
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
        String[] columnNames = new String[42];        
        columnNames[0]="I_VOUSRLNO";
        columnNames[1]="S_ORGCODE";
        columnNames[2]="S_TRECODE";
        columnNames[3]="S_STATUS";
        columnNames[4]="S_DEMO";
        columnNames[5]="S_FILENAME";
        columnNames[6]="TS_SYSUPDATE";
        columnNames[7]="S_PACKAGENO";
        columnNames[8]="S_ADMDIVCODE";
        columnNames[9]="S_STYEAR";
        columnNames[10]="S_VTCODE";
        columnNames[11]="S_VOUDATE";
        columnNames[12]="S_VOUCHERNO";
        columnNames[13]="S_ORIGINALVTCODE";
        columnNames[14]="S_ORIGINALVOUCHERNO";
        columnNames[15]="N_SUMAMT";
        columnNames[16]="S_AGENCYCODE";
        columnNames[17]="S_AGENCYNAME";
        columnNames[18]="S_PAYACCTNO";
        columnNames[19]="S_PAYACCTNAME";
        columnNames[20]="S_PAYACCTBANKNAME";
        columnNames[21]="S_PAYBANKCODE";
        columnNames[22]="S_PAYBANKNAME";
        columnNames[23]="S_BUSINESSTYPECODE";
        columnNames[24]="S_BUSINESSTYPENAME";
        columnNames[25]="S_PAYTYPECODE";
        columnNames[26]="S_PAYTYPENAME";
        columnNames[27]="S_XPAYDATE";
        columnNames[28]="N_XSUMAMT";
        columnNames[29]="S_HOLD1";
        columnNames[30]="S_HOLD2";
        columnNames[31]="S_EXT1";
        columnNames[32]="S_EXT2";
        columnNames[33]="S_EXT3";
        columnNames[34]="S_EXT4";
        columnNames[35]="S_EXT5";
        columnNames[36]="S_FUNDTYPECODE";
        columnNames[37]="S_FUNDTYPENAME";
        columnNames[38]="S_PAYDICTATENO";
        columnNames[39]="S_PAYMSGNO";
        columnNames[40]="S_PAYENTRUSTDATE";
        columnNames[41]="S_PAYSNDBNKNO";
        return columnNames;     
    }
	/******************************************************
	*
	*  Get Column Name
	*
	*******************************************************/
	/**
	 * Getter I_VOUSRLNO, PK , NOT NULL   	 
	 */
	public static String columnIvousrlno(){
		 return I_VOUSRLNO;
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
	 * Getter S_STATUS   	 
	 */
	public static String columnSstatus(){
		 return S_STATUS;
	}
	/**
	 * Getter S_DEMO   	 
	 */
	public static String columnSdemo(){
		 return S_DEMO;
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 */
	public static String columnSfilename(){
		 return S_FILENAME;
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 */
	public static String columnTssysupdate(){
		 return TS_SYSUPDATE;
	}
	/**
	 * Getter S_PACKAGENO   	 
	 */
	public static String columnSpackageno(){
		 return S_PACKAGENO;
	}
	/**
	 * Getter S_ADMDIVCODE   	 
	 */
	public static String columnSadmdivcode(){
		 return S_ADMDIVCODE;
	}
	/**
	 * Getter S_STYEAR   	 
	 */
	public static String columnSstyear(){
		 return S_STYEAR;
	}
	/**
	 * Getter S_VTCODE   	 
	 */
	public static String columnSvtcode(){
		 return S_VTCODE;
	}
	/**
	 * Getter S_VOUDATE   	 
	 */
	public static String columnSvoudate(){
		 return S_VOUDATE;
	}
	/**
	 * Getter S_VOUCHERNO   	 
	 */
	public static String columnSvoucherno(){
		 return S_VOUCHERNO;
	}
	/**
	 * Getter S_ORIGINALVTCODE   	 
	 */
	public static String columnSoriginalvtcode(){
		 return S_ORIGINALVTCODE;
	}
	/**
	 * Getter S_ORIGINALVOUCHERNO   	 
	 */
	public static String columnSoriginalvoucherno(){
		 return S_ORIGINALVOUCHERNO;
	}
	/**
	 * Getter N_SUMAMT   	 
	 */
	public static String columnNsumamt(){
		 return N_SUMAMT;
	}
	/**
	 * Getter S_AGENCYCODE   	 
	 */
	public static String columnSagencycode(){
		 return S_AGENCYCODE;
	}
	/**
	 * Getter S_AGENCYNAME   	 
	 */
	public static String columnSagencyname(){
		 return S_AGENCYNAME;
	}
	/**
	 * Getter S_PAYACCTNO   	 
	 */
	public static String columnSpayacctno(){
		 return S_PAYACCTNO;
	}
	/**
	 * Getter S_PAYACCTNAME   	 
	 */
	public static String columnSpayacctname(){
		 return S_PAYACCTNAME;
	}
	/**
	 * Getter S_PAYACCTBANKNAME   	 
	 */
	public static String columnSpayacctbankname(){
		 return S_PAYACCTBANKNAME;
	}
	/**
	 * Getter S_PAYBANKCODE   	 
	 */
	public static String columnSpaybankcode(){
		 return S_PAYBANKCODE;
	}
	/**
	 * Getter S_PAYBANKNAME   	 
	 */
	public static String columnSpaybankname(){
		 return S_PAYBANKNAME;
	}
	/**
	 * Getter S_BUSINESSTYPECODE   	 
	 */
	public static String columnSbusinesstypecode(){
		 return S_BUSINESSTYPECODE;
	}
	/**
	 * Getter S_BUSINESSTYPENAME   	 
	 */
	public static String columnSbusinesstypename(){
		 return S_BUSINESSTYPENAME;
	}
	/**
	 * Getter S_PAYTYPECODE   	 
	 */
	public static String columnSpaytypecode(){
		 return S_PAYTYPECODE;
	}
	/**
	 * Getter S_PAYTYPENAME   	 
	 */
	public static String columnSpaytypename(){
		 return S_PAYTYPENAME;
	}
	/**
	 * Getter S_XPAYDATE   	 
	 */
	public static String columnSxpaydate(){
		 return S_XPAYDATE;
	}
	/**
	 * Getter N_XSUMAMT   	 
	 */
	public static String columnNxsumamt(){
		 return N_XSUMAMT;
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
	 * Getter S_EXT1   	 
	 */
	public static String columnSext1(){
		 return S_EXT1;
	}
	/**
	 * Getter S_EXT2   	 
	 */
	public static String columnSext2(){
		 return S_EXT2;
	}
	/**
	 * Getter S_EXT3   	 
	 */
	public static String columnSext3(){
		 return S_EXT3;
	}
	/**
	 * Getter S_EXT4   	 
	 */
	public static String columnSext4(){
		 return S_EXT4;
	}
	/**
	 * Getter S_EXT5   	 
	 */
	public static String columnSext5(){
		 return S_EXT5;
	}
	/**
	 * Getter S_FUNDTYPECODE   	 
	 */
	public static String columnSfundtypecode(){
		 return S_FUNDTYPECODE;
	}
	/**
	 * Getter S_FUNDTYPENAME   	 
	 */
	public static String columnSfundtypename(){
		 return S_FUNDTYPENAME;
	}
	/**
	 * Getter S_PAYDICTATENO   	 
	 */
	public static String columnSpaydictateno(){
		 return S_PAYDICTATENO;
	}
	/**
	 * Getter S_PAYMSGNO   	 
	 */
	public static String columnSpaymsgno(){
		 return S_PAYMSGNO;
	}
	/**
	 * Getter S_PAYENTRUSTDATE   	 
	 */
	public static String columnSpayentrustdate(){
		 return S_PAYENTRUSTDATE;
	}
	/**
	 * Getter S_PAYSNDBNKNO   	 
	 */
	public static String columnSpaysndbnkno(){
		 return S_PAYSNDBNKNO;
	}
	/******************************************************
	*
	*  Get Column Java Type
	*
	*******************************************************/
	/**
	 * Getter I_VOUSRLNO, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeIvousrlno(){
		 return "Long";
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
	 * Getter S_STATUS   	 
	 */
	public static String columnJavaTypeSstatus(){
		 return "String";
	}
	/**
	 * Getter S_DEMO   	 
	 */
	public static String columnJavaTypeSdemo(){
		 return "String";
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSfilename(){
		 return "String";
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 */
	public static String columnJavaTypeTssysupdate(){
		 return "Timestamp";
	}
	/**
	 * Getter S_PACKAGENO   	 
	 */
	public static String columnJavaTypeSpackageno(){
		 return "String";
	}
	/**
	 * Getter S_ADMDIVCODE   	 
	 */
	public static String columnJavaTypeSadmdivcode(){
		 return "String";
	}
	/**
	 * Getter S_STYEAR   	 
	 */
	public static String columnJavaTypeSstyear(){
		 return "String";
	}
	/**
	 * Getter S_VTCODE   	 
	 */
	public static String columnJavaTypeSvtcode(){
		 return "String";
	}
	/**
	 * Getter S_VOUDATE   	 
	 */
	public static String columnJavaTypeSvoudate(){
		 return "String";
	}
	/**
	 * Getter S_VOUCHERNO   	 
	 */
	public static String columnJavaTypeSvoucherno(){
		 return "String";
	}
	/**
	 * Getter S_ORIGINALVTCODE   	 
	 */
	public static String columnJavaTypeSoriginalvtcode(){
		 return "String";
	}
	/**
	 * Getter S_ORIGINALVOUCHERNO   	 
	 */
	public static String columnJavaTypeSoriginalvoucherno(){
		 return "String";
	}
	/**
	 * Getter N_SUMAMT   	 
	 */
	public static String columnJavaTypeNsumamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter S_AGENCYCODE   	 
	 */
	public static String columnJavaTypeSagencycode(){
		 return "String";
	}
	/**
	 * Getter S_AGENCYNAME   	 
	 */
	public static String columnJavaTypeSagencyname(){
		 return "String";
	}
	/**
	 * Getter S_PAYACCTNO   	 
	 */
	public static String columnJavaTypeSpayacctno(){
		 return "String";
	}
	/**
	 * Getter S_PAYACCTNAME   	 
	 */
	public static String columnJavaTypeSpayacctname(){
		 return "String";
	}
	/**
	 * Getter S_PAYACCTBANKNAME   	 
	 */
	public static String columnJavaTypeSpayacctbankname(){
		 return "String";
	}
	/**
	 * Getter S_PAYBANKCODE   	 
	 */
	public static String columnJavaTypeSpaybankcode(){
		 return "String";
	}
	/**
	 * Getter S_PAYBANKNAME   	 
	 */
	public static String columnJavaTypeSpaybankname(){
		 return "String";
	}
	/**
	 * Getter S_BUSINESSTYPECODE   	 
	 */
	public static String columnJavaTypeSbusinesstypecode(){
		 return "String";
	}
	/**
	 * Getter S_BUSINESSTYPENAME   	 
	 */
	public static String columnJavaTypeSbusinesstypename(){
		 return "String";
	}
	/**
	 * Getter S_PAYTYPECODE   	 
	 */
	public static String columnJavaTypeSpaytypecode(){
		 return "String";
	}
	/**
	 * Getter S_PAYTYPENAME   	 
	 */
	public static String columnJavaTypeSpaytypename(){
		 return "String";
	}
	/**
	 * Getter S_XPAYDATE   	 
	 */
	public static String columnJavaTypeSxpaydate(){
		 return "String";
	}
	/**
	 * Getter N_XSUMAMT   	 
	 */
	public static String columnJavaTypeNxsumamt(){
		 return "BigDecimal";
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
	 * Getter S_EXT1   	 
	 */
	public static String columnJavaTypeSext1(){
		 return "String";
	}
	/**
	 * Getter S_EXT2   	 
	 */
	public static String columnJavaTypeSext2(){
		 return "String";
	}
	/**
	 * Getter S_EXT3   	 
	 */
	public static String columnJavaTypeSext3(){
		 return "String";
	}
	/**
	 * Getter S_EXT4   	 
	 */
	public static String columnJavaTypeSext4(){
		 return "String";
	}
	/**
	 * Getter S_EXT5   	 
	 */
	public static String columnJavaTypeSext5(){
		 return "String";
	}
	/**
	 * Getter S_FUNDTYPECODE   	 
	 */
	public static String columnJavaTypeSfundtypecode(){
		 return "String";
	}
	/**
	 * Getter S_FUNDTYPENAME   	 
	 */
	public static String columnJavaTypeSfundtypename(){
		 return "String";
	}
	/**
	 * Getter S_PAYDICTATENO   	 
	 */
	public static String columnJavaTypeSpaydictateno(){
		 return "String";
	}
	/**
	 * Getter S_PAYMSGNO   	 
	 */
	public static String columnJavaTypeSpaymsgno(){
		 return "String";
	}
	/**
	 * Getter S_PAYENTRUSTDATE   	 
	 */
	public static String columnJavaTypeSpayentrustdate(){
		 return "String";
	}
	/**
	 * Getter S_PAYSNDBNKNO   	 
	 */
	public static String columnJavaTypeSpaysndbnkno(){
		 return "String";
	}
	/******************************************************
	*
	*  Get Column Database Type
	*
	*******************************************************/
	/**
	 * Getter I_VOUSRLNO, PK , NOT NULL   	 
	 * columnType is BIGINT
	 */
	public static String columnDatabaseTypeIvousrlno(){
		 return "BIGINT";
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
	 * Getter S_STATUS   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSstatus(){
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
	 * Getter S_FILENAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSfilename(){
		 return "VARCHAR";
	}
	/**
	 * Getter TS_SYSUPDATE   	 
	 * columnType is TIMESTAMP
	 */
	public static String columnDatabaseTypeTssysupdate(){
		 return "TIMESTAMP";
	}
	/**
	 * Getter S_PACKAGENO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpackageno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ADMDIVCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSadmdivcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_STYEAR   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSstyear(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_VTCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSvtcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_VOUDATE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSvoudate(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_VOUCHERNO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSvoucherno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ORIGINALVTCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSoriginalvtcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ORIGINALVOUCHERNO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSoriginalvoucherno(){
		 return "VARCHAR";
	}
	/**
	 * Getter N_SUMAMT   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNsumamt(){
		 return "DECIMAL";
	}
	/**
	 * Getter S_AGENCYCODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSagencycode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_AGENCYNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSagencyname(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYACCTNO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayacctno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYACCTNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayacctname(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYACCTBANKNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayacctbankname(){
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
	 * Getter S_PAYBANKNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpaybankname(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_BUSINESSTYPECODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSbusinesstypecode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_BUSINESSTYPENAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSbusinesstypename(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYTYPECODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpaytypecode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYTYPENAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpaytypename(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_XPAYDATE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSxpaydate(){
		 return "VARCHAR";
	}
	/**
	 * Getter N_XSUMAMT   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNxsumamt(){
		 return "DECIMAL";
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
	 * Getter S_EXT1   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSext1(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_EXT2   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSext2(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_EXT3   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSext3(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_EXT4   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSext4(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_EXT5   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSext5(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_FUNDTYPECODE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSfundtypecode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_FUNDTYPENAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSfundtypename(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYDICTATENO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpaydictateno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYMSGNO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpaymsgno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYENTRUSTDATE   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayentrustdate(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYSNDBNKNO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpaysndbnkno(){
		 return "VARCHAR";
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
	 * Getter S_TRECODE , NOT NULL   	 
	 */
	public static int columnWidthStrecode(){
		 return 10;
	}
	/**
	 * Getter S_STATUS   	 
	 */
	public static int columnWidthSstatus(){
		 return 5;
	}
	/**
	 * Getter S_DEMO   	 
	 */
	public static int columnWidthSdemo(){
		 return 100;
	}
	/**
	 * Getter S_FILENAME , NOT NULL   	 
	 */
	public static int columnWidthSfilename(){
		 return 100;
	}
	/**
	 * Getter S_PACKAGENO   	 
	 */
	public static int columnWidthSpackageno(){
		 return 8;
	}
	/**
	 * Getter S_ADMDIVCODE   	 
	 */
	public static int columnWidthSadmdivcode(){
		 return 9;
	}
	/**
	 * Getter S_STYEAR   	 
	 */
	public static int columnWidthSstyear(){
		 return 4;
	}
	/**
	 * Getter S_VTCODE   	 
	 */
	public static int columnWidthSvtcode(){
		 return 4;
	}
	/**
	 * Getter S_VOUDATE   	 
	 */
	public static int columnWidthSvoudate(){
		 return 8;
	}
	/**
	 * Getter S_VOUCHERNO   	 
	 */
	public static int columnWidthSvoucherno(){
		 return 42;
	}
	/**
	 * Getter S_ORIGINALVTCODE   	 
	 */
	public static int columnWidthSoriginalvtcode(){
		 return 4;
	}
	/**
	 * Getter S_ORIGINALVOUCHERNO   	 
	 */
	public static int columnWidthSoriginalvoucherno(){
		 return 42;
	}
	/**
	 * Getter S_AGENCYCODE   	 
	 */
	public static int columnWidthSagencycode(){
		 return 42;
	}
	/**
	 * Getter S_AGENCYNAME   	 
	 */
	public static int columnWidthSagencyname(){
		 return 60;
	}
	/**
	 * Getter S_PAYACCTNO   	 
	 */
	public static int columnWidthSpayacctno(){
		 return 42;
	}
	/**
	 * Getter S_PAYACCTNAME   	 
	 */
	public static int columnWidthSpayacctname(){
		 return 120;
	}
	/**
	 * Getter S_PAYACCTBANKNAME   	 
	 */
	public static int columnWidthSpayacctbankname(){
		 return 60;
	}
	/**
	 * Getter S_PAYBANKCODE   	 
	 */
	public static int columnWidthSpaybankcode(){
		 return 42;
	}
	/**
	 * Getter S_PAYBANKNAME   	 
	 */
	public static int columnWidthSpaybankname(){
		 return 60;
	}
	/**
	 * Getter S_BUSINESSTYPECODE   	 
	 */
	public static int columnWidthSbusinesstypecode(){
		 return 42;
	}
	/**
	 * Getter S_BUSINESSTYPENAME   	 
	 */
	public static int columnWidthSbusinesstypename(){
		 return 60;
	}
	/**
	 * Getter S_PAYTYPECODE   	 
	 */
	public static int columnWidthSpaytypecode(){
		 return 42;
	}
	/**
	 * Getter S_PAYTYPENAME   	 
	 */
	public static int columnWidthSpaytypename(){
		 return 60;
	}
	/**
	 * Getter S_XPAYDATE   	 
	 */
	public static int columnWidthSxpaydate(){
		 return 20;
	}
	/**
	 * Getter S_HOLD1   	 
	 */
	public static int columnWidthShold1(){
		 return 42;
	}
	/**
	 * Getter S_HOLD2   	 
	 */
	public static int columnWidthShold2(){
		 return 42;
	}
	/**
	 * Getter S_EXT1   	 
	 */
	public static int columnWidthSext1(){
		 return 50;
	}
	/**
	 * Getter S_EXT2   	 
	 */
	public static int columnWidthSext2(){
		 return 50;
	}
	/**
	 * Getter S_EXT3   	 
	 */
	public static int columnWidthSext3(){
		 return 50;
	}
	/**
	 * Getter S_EXT4   	 
	 */
	public static int columnWidthSext4(){
		 return 50;
	}
	/**
	 * Getter S_EXT5   	 
	 */
	public static int columnWidthSext5(){
		 return 50;
	}
	/**
	 * Getter S_FUNDTYPECODE   	 
	 */
	public static int columnWidthSfundtypecode(){
		 return 42;
	}
	/**
	 * Getter S_FUNDTYPENAME   	 
	 */
	public static int columnWidthSfundtypename(){
		 return 60;
	}
	/**
	 * Getter S_PAYDICTATENO   	 
	 */
	public static int columnWidthSpaydictateno(){
		 return 8;
	}
	/**
	 * Getter S_PAYMSGNO   	 
	 */
	public static int columnWidthSpaymsgno(){
		 return 10;
	}
	/**
	 * Getter S_PAYENTRUSTDATE   	 
	 */
	public static int columnWidthSpayentrustdate(){
		 return 8;
	}
	/**
	 * Getter S_PAYSNDBNKNO   	 
	 */
	public static int columnWidthSpaysndbnkno(){
		 return 14;
	}
}
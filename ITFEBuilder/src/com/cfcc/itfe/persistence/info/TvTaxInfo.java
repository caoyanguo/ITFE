      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TV_TAX</p>
 * <p>Description:  Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:04 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class TvTaxInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TV_TAX";
	
	/**
	 * column S_SEQ
	 */
	public static String S_SEQ ="S_SEQ";
	/**
	 * column S_TAXORGCODE
	 */
	public static String S_TAXORGCODE ="S_TAXORGCODE";
	/**
	 * column S_ENTRUSTDATE
	 */
	public static String S_ENTRUSTDATE ="S_ENTRUSTDATE";
	/**
	 * column S_TRANO
	 */
	public static String S_TRANO ="S_TRANO";
	/**
	 * column S_MSGID
	 */
	public static String S_MSGID ="S_MSGID";
	/**
	 * column S_TAXVOUNO
	 */
	public static String S_TAXVOUNO ="S_TAXVOUNO";
	/**
	 * column C_HANDLETYPE
	 */
	public static String C_HANDLETYPE ="C_HANDLETYPE";
	/**
	 * column S_BILLDATE
	 */
	public static String S_BILLDATE ="S_BILLDATE";
	/**
	 * column C_BUDGETTYPE
	 */
	public static String C_BUDGETTYPE ="C_BUDGETTYPE";
	/**
	 * column S_PAYEEBANKNO
	 */
	public static String S_PAYEEBANKNO ="S_PAYEEBANKNO";
	/**
	 * column S_PAYEEOPBKCODE
	 */
	public static String S_PAYEEOPBKCODE ="S_PAYEEOPBKCODE";
	/**
	 * column S_PAYEEACCT
	 */
	public static String S_PAYEEACCT ="S_PAYEEACCT";
	/**
	 * column S_PAYEENAME
	 */
	public static String S_PAYEENAME ="S_PAYEENAME";
	/**
	 * column S_PAYEEORGCODE
	 */
	public static String S_PAYEEORGCODE ="S_PAYEEORGCODE";
	/**
	 * column S_PAYBKCODE
	 */
	public static String S_PAYBKCODE ="S_PAYBKCODE";
	/**
	 * column S_PAYOPBKCODE
	 */
	public static String S_PAYOPBKCODE ="S_PAYOPBKCODE";
	/**
	 * column S_PAYACCT
	 */
	public static String S_PAYACCT ="S_PAYACCT";
	/**
	 * column S_CORPCODE
	 */
	public static String S_CORPCODE ="S_CORPCODE";
	/**
	 * column S_PROTOCOLNO
	 */
	public static String S_PROTOCOLNO ="S_PROTOCOLNO";
	/**
	 * column S_CORPTYPE
	 */
	public static String S_CORPTYPE ="S_CORPTYPE";
	/**
	 * column S_HANDORGNAME
	 */
	public static String S_HANDORGNAME ="S_HANDORGNAME";
	/**
	 * column S_TAXPAYCODE
	 */
	public static String S_TAXPAYCODE ="S_TAXPAYCODE";
	/**
	 * column S_TAXPAYNAME
	 */
	public static String S_TAXPAYNAME ="S_TAXPAYNAME";
	/**
	 * column C_PRINTFLAG
	 */
	public static String C_PRINTFLAG ="C_PRINTFLAG";
	/**
	 * column C_TRIMSIGN
	 */
	public static String C_TRIMSIGN ="C_TRIMSIGN";
	/**
	 * column F_TRAAMT
	 */
	public static String F_TRAAMT ="F_TRAAMT";
	/**
	 * column S_REMARK
	 */
	public static String S_REMARK ="S_REMARK";
	/**
	 * column S_REMARK1
	 */
	public static String S_REMARK1 ="S_REMARK1";
	/**
	 * column S_REMARK2
	 */
	public static String S_REMARK2 ="S_REMARK2";
	/**
	 * column I_TAXKINDCOUNT
	 */
	public static String I_TAXKINDCOUNT ="I_TAXKINDCOUNT";
	/**
	 * column C_RECKONTYPE
	 */
	public static String C_RECKONTYPE ="C_RECKONTYPE";
	/**
	 * column S_PROCSTAT
	 */
	public static String S_PROCSTAT ="S_PROCSTAT";
	/**
	 * column S_STATUS
	 */
	public static String S_STATUS ="S_STATUS";
	/**
	 * column S_RESULT
	 */
	public static String S_RESULT ="S_RESULT";
	/**
	 * column S_ADDWORD
	 */
	public static String S_ADDWORD ="S_ADDWORD";
	/**
	 * column C_CANCELSTAT
	 */
	public static String C_CANCELSTAT ="C_CANCELSTAT";
	/**
	 * column S_CHKBATCH
	 */
	public static String S_CHKBATCH ="S_CHKBATCH";
	/**
	 * column S_EXPORTER
	 */
	public static String S_EXPORTER ="S_EXPORTER";
	/**
	 * column S_ACCEPTDATE
	 */
	public static String S_ACCEPTDATE ="S_ACCEPTDATE";
	/**
	 * column S_PAYDATE
	 */
	public static String S_PAYDATE ="S_PAYDATE";
	/**
	 * column S_CHKDATE
	 */
	public static String S_CHKDATE ="S_CHKDATE";
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
        String[] columnNames = new String[42];        
        columnNames[0]="S_SEQ";
        columnNames[1]="S_TAXORGCODE";
        columnNames[2]="S_ENTRUSTDATE";
        columnNames[3]="S_TRANO";
        columnNames[4]="S_MSGID";
        columnNames[5]="S_TAXVOUNO";
        columnNames[6]="C_HANDLETYPE";
        columnNames[7]="S_BILLDATE";
        columnNames[8]="C_BUDGETTYPE";
        columnNames[9]="S_PAYEEBANKNO";
        columnNames[10]="S_PAYEEOPBKCODE";
        columnNames[11]="S_PAYEEACCT";
        columnNames[12]="S_PAYEENAME";
        columnNames[13]="S_PAYEEORGCODE";
        columnNames[14]="S_PAYBKCODE";
        columnNames[15]="S_PAYOPBKCODE";
        columnNames[16]="S_PAYACCT";
        columnNames[17]="S_CORPCODE";
        columnNames[18]="S_PROTOCOLNO";
        columnNames[19]="S_CORPTYPE";
        columnNames[20]="S_HANDORGNAME";
        columnNames[21]="S_TAXPAYCODE";
        columnNames[22]="S_TAXPAYNAME";
        columnNames[23]="C_PRINTFLAG";
        columnNames[24]="C_TRIMSIGN";
        columnNames[25]="F_TRAAMT";
        columnNames[26]="S_REMARK";
        columnNames[27]="S_REMARK1";
        columnNames[28]="S_REMARK2";
        columnNames[29]="I_TAXKINDCOUNT";
        columnNames[30]="C_RECKONTYPE";
        columnNames[31]="S_PROCSTAT";
        columnNames[32]="S_STATUS";
        columnNames[33]="S_RESULT";
        columnNames[34]="S_ADDWORD";
        columnNames[35]="C_CANCELSTAT";
        columnNames[36]="S_CHKBATCH";
        columnNames[37]="S_EXPORTER";
        columnNames[38]="S_ACCEPTDATE";
        columnNames[39]="S_PAYDATE";
        columnNames[40]="S_CHKDATE";
        columnNames[41]="TS_UPDATE";
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
	 * Getter S_TAXORGCODE , NOT NULL   	 
	 */
	public static String columnStaxorgcode(){
		 return S_TAXORGCODE;
	}
	/**
	 * Getter S_ENTRUSTDATE , NOT NULL   	 
	 */
	public static String columnSentrustdate(){
		 return S_ENTRUSTDATE;
	}
	/**
	 * Getter S_TRANO , NOT NULL   	 
	 */
	public static String columnStrano(){
		 return S_TRANO;
	}
	/**
	 * Getter S_MSGID , NOT NULL   	 
	 */
	public static String columnSmsgid(){
		 return S_MSGID;
	}
	/**
	 * Getter S_TAXVOUNO , NOT NULL   	 
	 */
	public static String columnStaxvouno(){
		 return S_TAXVOUNO;
	}
	/**
	 * Getter C_HANDLETYPE , NOT NULL   	 
	 */
	public static String columnChandletype(){
		 return C_HANDLETYPE;
	}
	/**
	 * Getter S_BILLDATE , NOT NULL   	 
	 */
	public static String columnSbilldate(){
		 return S_BILLDATE;
	}
	/**
	 * Getter C_BUDGETTYPE , NOT NULL   	 
	 */
	public static String columnCbudgettype(){
		 return C_BUDGETTYPE;
	}
	/**
	 * Getter S_PAYEEBANKNO   	 
	 */
	public static String columnSpayeebankno(){
		 return S_PAYEEBANKNO;
	}
	/**
	 * Getter S_PAYEEOPBKCODE , NOT NULL   	 
	 */
	public static String columnSpayeeopbkcode(){
		 return S_PAYEEOPBKCODE;
	}
	/**
	 * Getter S_PAYEEACCT , NOT NULL   	 
	 */
	public static String columnSpayeeacct(){
		 return S_PAYEEACCT;
	}
	/**
	 * Getter S_PAYEENAME , NOT NULL   	 
	 */
	public static String columnSpayeename(){
		 return S_PAYEENAME;
	}
	/**
	 * Getter S_PAYEEORGCODE , NOT NULL   	 
	 */
	public static String columnSpayeeorgcode(){
		 return S_PAYEEORGCODE;
	}
	/**
	 * Getter S_PAYBKCODE , NOT NULL   	 
	 */
	public static String columnSpaybkcode(){
		 return S_PAYBKCODE;
	}
	/**
	 * Getter S_PAYOPBKCODE , NOT NULL   	 
	 */
	public static String columnSpayopbkcode(){
		 return S_PAYOPBKCODE;
	}
	/**
	 * Getter S_PAYACCT , NOT NULL   	 
	 */
	public static String columnSpayacct(){
		 return S_PAYACCT;
	}
	/**
	 * Getter S_CORPCODE   	 
	 */
	public static String columnScorpcode(){
		 return S_CORPCODE;
	}
	/**
	 * Getter S_PROTOCOLNO , NOT NULL   	 
	 */
	public static String columnSprotocolno(){
		 return S_PROTOCOLNO;
	}
	/**
	 * Getter S_CORPTYPE   	 
	 */
	public static String columnScorptype(){
		 return S_CORPTYPE;
	}
	/**
	 * Getter S_HANDORGNAME , NOT NULL   	 
	 */
	public static String columnShandorgname(){
		 return S_HANDORGNAME;
	}
	/**
	 * Getter S_TAXPAYCODE , NOT NULL   	 
	 */
	public static String columnStaxpaycode(){
		 return S_TAXPAYCODE;
	}
	/**
	 * Getter S_TAXPAYNAME , NOT NULL   	 
	 */
	public static String columnStaxpayname(){
		 return S_TAXPAYNAME;
	}
	/**
	 * Getter C_PRINTFLAG , NOT NULL   	 
	 */
	public static String columnCprintflag(){
		 return C_PRINTFLAG;
	}
	/**
	 * Getter C_TRIMSIGN , NOT NULL   	 
	 */
	public static String columnCtrimsign(){
		 return C_TRIMSIGN;
	}
	/**
	 * Getter F_TRAAMT , NOT NULL   	 
	 */
	public static String columnFtraamt(){
		 return F_TRAAMT;
	}
	/**
	 * Getter S_REMARK   	 
	 */
	public static String columnSremark(){
		 return S_REMARK;
	}
	/**
	 * Getter S_REMARK1   	 
	 */
	public static String columnSremark1(){
		 return S_REMARK1;
	}
	/**
	 * Getter S_REMARK2   	 
	 */
	public static String columnSremark2(){
		 return S_REMARK2;
	}
	/**
	 * Getter I_TAXKINDCOUNT , NOT NULL   	 
	 */
	public static String columnItaxkindcount(){
		 return I_TAXKINDCOUNT;
	}
	/**
	 * Getter C_RECKONTYPE , NOT NULL   	 
	 */
	public static String columnCreckontype(){
		 return C_RECKONTYPE;
	}
	/**
	 * Getter S_PROCSTAT , NOT NULL   	 
	 */
	public static String columnSprocstat(){
		 return S_PROCSTAT;
	}
	/**
	 * Getter S_STATUS , NOT NULL   	 
	 */
	public static String columnSstatus(){
		 return S_STATUS;
	}
	/**
	 * Getter S_RESULT   	 
	 */
	public static String columnSresult(){
		 return S_RESULT;
	}
	/**
	 * Getter S_ADDWORD   	 
	 */
	public static String columnSaddword(){
		 return S_ADDWORD;
	}
	/**
	 * Getter C_CANCELSTAT , NOT NULL   	 
	 */
	public static String columnCcancelstat(){
		 return C_CANCELSTAT;
	}
	/**
	 * Getter S_CHKBATCH   	 
	 */
	public static String columnSchkbatch(){
		 return S_CHKBATCH;
	}
	/**
	 * Getter S_EXPORTER   	 
	 */
	public static String columnSexporter(){
		 return S_EXPORTER;
	}
	/**
	 * Getter S_ACCEPTDATE , NOT NULL   	 
	 */
	public static String columnSacceptdate(){
		 return S_ACCEPTDATE;
	}
	/**
	 * Getter S_PAYDATE   	 
	 */
	public static String columnSpaydate(){
		 return S_PAYDATE;
	}
	/**
	 * Getter S_CHKDATE   	 
	 */
	public static String columnSchkdate(){
		 return S_CHKDATE;
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
	 * Getter S_TAXORGCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeStaxorgcode(){
		 return "String";
	}
	/**
	 * Getter S_ENTRUSTDATE , NOT NULL   	 
	 */
	public static String columnJavaTypeSentrustdate(){
		 return "String";
	}
	/**
	 * Getter S_TRANO , NOT NULL   	 
	 */
	public static String columnJavaTypeStrano(){
		 return "String";
	}
	/**
	 * Getter S_MSGID , NOT NULL   	 
	 */
	public static String columnJavaTypeSmsgid(){
		 return "String";
	}
	/**
	 * Getter S_TAXVOUNO , NOT NULL   	 
	 */
	public static String columnJavaTypeStaxvouno(){
		 return "String";
	}
	/**
	 * Getter C_HANDLETYPE , NOT NULL   	 
	 */
	public static String columnJavaTypeChandletype(){
		 return "String";
	}
	/**
	 * Getter S_BILLDATE , NOT NULL   	 
	 */
	public static String columnJavaTypeSbilldate(){
		 return "String";
	}
	/**
	 * Getter C_BUDGETTYPE , NOT NULL   	 
	 */
	public static String columnJavaTypeCbudgettype(){
		 return "String";
	}
	/**
	 * Getter S_PAYEEBANKNO   	 
	 */
	public static String columnJavaTypeSpayeebankno(){
		 return "String";
	}
	/**
	 * Getter S_PAYEEOPBKCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayeeopbkcode(){
		 return "String";
	}
	/**
	 * Getter S_PAYEEACCT , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayeeacct(){
		 return "String";
	}
	/**
	 * Getter S_PAYEENAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayeename(){
		 return "String";
	}
	/**
	 * Getter S_PAYEEORGCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayeeorgcode(){
		 return "String";
	}
	/**
	 * Getter S_PAYBKCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSpaybkcode(){
		 return "String";
	}
	/**
	 * Getter S_PAYOPBKCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayopbkcode(){
		 return "String";
	}
	/**
	 * Getter S_PAYACCT , NOT NULL   	 
	 */
	public static String columnJavaTypeSpayacct(){
		 return "String";
	}
	/**
	 * Getter S_CORPCODE   	 
	 */
	public static String columnJavaTypeScorpcode(){
		 return "String";
	}
	/**
	 * Getter S_PROTOCOLNO , NOT NULL   	 
	 */
	public static String columnJavaTypeSprotocolno(){
		 return "String";
	}
	/**
	 * Getter S_CORPTYPE   	 
	 */
	public static String columnJavaTypeScorptype(){
		 return "String";
	}
	/**
	 * Getter S_HANDORGNAME , NOT NULL   	 
	 */
	public static String columnJavaTypeShandorgname(){
		 return "String";
	}
	/**
	 * Getter S_TAXPAYCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeStaxpaycode(){
		 return "String";
	}
	/**
	 * Getter S_TAXPAYNAME , NOT NULL   	 
	 */
	public static String columnJavaTypeStaxpayname(){
		 return "String";
	}
	/**
	 * Getter C_PRINTFLAG , NOT NULL   	 
	 */
	public static String columnJavaTypeCprintflag(){
		 return "String";
	}
	/**
	 * Getter C_TRIMSIGN , NOT NULL   	 
	 */
	public static String columnJavaTypeCtrimsign(){
		 return "String";
	}
	/**
	 * Getter F_TRAAMT , NOT NULL   	 
	 */
	public static String columnJavaTypeFtraamt(){
		 return "BigDecimal";
	}
	/**
	 * Getter S_REMARK   	 
	 */
	public static String columnJavaTypeSremark(){
		 return "String";
	}
	/**
	 * Getter S_REMARK1   	 
	 */
	public static String columnJavaTypeSremark1(){
		 return "String";
	}
	/**
	 * Getter S_REMARK2   	 
	 */
	public static String columnJavaTypeSremark2(){
		 return "String";
	}
	/**
	 * Getter I_TAXKINDCOUNT , NOT NULL   	 
	 */
	public static String columnJavaTypeItaxkindcount(){
		 return "Short";
	}
	/**
	 * Getter C_RECKONTYPE , NOT NULL   	 
	 */
	public static String columnJavaTypeCreckontype(){
		 return "String";
	}
	/**
	 * Getter S_PROCSTAT , NOT NULL   	 
	 */
	public static String columnJavaTypeSprocstat(){
		 return "String";
	}
	/**
	 * Getter S_STATUS , NOT NULL   	 
	 */
	public static String columnJavaTypeSstatus(){
		 return "String";
	}
	/**
	 * Getter S_RESULT   	 
	 */
	public static String columnJavaTypeSresult(){
		 return "String";
	}
	/**
	 * Getter S_ADDWORD   	 
	 */
	public static String columnJavaTypeSaddword(){
		 return "String";
	}
	/**
	 * Getter C_CANCELSTAT , NOT NULL   	 
	 */
	public static String columnJavaTypeCcancelstat(){
		 return "String";
	}
	/**
	 * Getter S_CHKBATCH   	 
	 */
	public static String columnJavaTypeSchkbatch(){
		 return "String";
	}
	/**
	 * Getter S_EXPORTER   	 
	 */
	public static String columnJavaTypeSexporter(){
		 return "String";
	}
	/**
	 * Getter S_ACCEPTDATE , NOT NULL   	 
	 */
	public static String columnJavaTypeSacceptdate(){
		 return "String";
	}
	/**
	 * Getter S_PAYDATE   	 
	 */
	public static String columnJavaTypeSpaydate(){
		 return "String";
	}
	/**
	 * Getter S_CHKDATE   	 
	 */
	public static String columnJavaTypeSchkdate(){
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
	 * Getter S_SEQ, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSseq(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_TAXORGCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStaxorgcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ENTRUSTDATE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSentrustdate(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_TRANO , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeStrano(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_MSGID , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSmsgid(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_TAXVOUNO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStaxvouno(){
		 return "VARCHAR";
	}
	/**
	 * Getter C_HANDLETYPE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeChandletype(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_BILLDATE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSbilldate(){
		 return "CHARACTER";
	}
	/**
	 * Getter C_BUDGETTYPE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCbudgettype(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_PAYEEBANKNO   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSpayeebankno(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_PAYEEOPBKCODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSpayeeopbkcode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_PAYEEACCT , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeeacct(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYEENAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayeename(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYEEORGCODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSpayeeorgcode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_PAYBKCODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSpaybkcode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_PAYOPBKCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayopbkcode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PAYACCT , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpayacct(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CORPCODE   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeScorpcode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_PROTOCOLNO , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSprotocolno(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CORPTYPE   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeScorptype(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_HANDORGNAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeShandorgname(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_TAXPAYCODE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeStaxpaycode(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_TAXPAYNAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeStaxpayname(){
		 return "VARCHAR";
	}
	/**
	 * Getter C_PRINTFLAG , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCprintflag(){
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
	 * Getter F_TRAAMT , NOT NULL   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeFtraamt(){
		 return "DECIMAL";
	}
	/**
	 * Getter S_REMARK   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSremark(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_REMARK1   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSremark1(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_REMARK2   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSremark2(){
		 return "VARCHAR";
	}
	/**
	 * Getter I_TAXKINDCOUNT , NOT NULL   	 
	 * columnType is SMALLINT
	 */
	public static String columnDatabaseTypeItaxkindcount(){
		 return "SMALLINT";
	}
	/**
	 * Getter C_RECKONTYPE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCreckontype(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_PROCSTAT , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSprocstat(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_STATUS , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSstatus(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_RESULT   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSresult(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_ADDWORD   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSaddword(){
		 return "VARCHAR";
	}
	/**
	 * Getter C_CANCELSTAT , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeCcancelstat(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_CHKBATCH   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSchkbatch(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_EXPORTER   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSexporter(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_ACCEPTDATE , NOT NULL   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSacceptdate(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_PAYDATE   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSpaydate(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_CHKDATE   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSchkdate(){
		 return "CHARACTER";
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
	 * Getter S_TAXORGCODE , NOT NULL   	 
	 */
	public static int columnWidthStaxorgcode(){
		 return 12;
	}
	/**
	 * Getter S_ENTRUSTDATE , NOT NULL   	 
	 */
	public static int columnWidthSentrustdate(){
		 return 8;
	}
	/**
	 * Getter S_TRANO , NOT NULL   	 
	 */
	public static int columnWidthStrano(){
		 return 8;
	}
	/**
	 * Getter S_MSGID , NOT NULL   	 
	 */
	public static int columnWidthSmsgid(){
		 return 20;
	}
	/**
	 * Getter S_TAXVOUNO , NOT NULL   	 
	 */
	public static int columnWidthStaxvouno(){
		 return 18;
	}
	/**
	 * Getter C_HANDLETYPE , NOT NULL   	 
	 */
	public static int columnWidthChandletype(){
		 return 1;
	}
	/**
	 * Getter S_BILLDATE , NOT NULL   	 
	 */
	public static int columnWidthSbilldate(){
		 return 8;
	}
	/**
	 * Getter C_BUDGETTYPE , NOT NULL   	 
	 */
	public static int columnWidthCbudgettype(){
		 return 1;
	}
	/**
	 * Getter S_PAYEEBANKNO   	 
	 */
	public static int columnWidthSpayeebankno(){
		 return 12;
	}
	/**
	 * Getter S_PAYEEOPBKCODE , NOT NULL   	 
	 */
	public static int columnWidthSpayeeopbkcode(){
		 return 12;
	}
	/**
	 * Getter S_PAYEEACCT , NOT NULL   	 
	 */
	public static int columnWidthSpayeeacct(){
		 return 32;
	}
	/**
	 * Getter S_PAYEENAME , NOT NULL   	 
	 */
	public static int columnWidthSpayeename(){
		 return 60;
	}
	/**
	 * Getter S_PAYEEORGCODE , NOT NULL   	 
	 */
	public static int columnWidthSpayeeorgcode(){
		 return 12;
	}
	/**
	 * Getter S_PAYBKCODE , NOT NULL   	 
	 */
	public static int columnWidthSpaybkcode(){
		 return 12;
	}
	/**
	 * Getter S_PAYOPBKCODE , NOT NULL   	 
	 */
	public static int columnWidthSpayopbkcode(){
		 return 12;
	}
	/**
	 * Getter S_PAYACCT , NOT NULL   	 
	 */
	public static int columnWidthSpayacct(){
		 return 32;
	}
	/**
	 * Getter S_CORPCODE   	 
	 */
	public static int columnWidthScorpcode(){
		 return 20;
	}
	/**
	 * Getter S_PROTOCOLNO , NOT NULL   	 
	 */
	public static int columnWidthSprotocolno(){
		 return 60;
	}
	/**
	 * Getter S_CORPTYPE   	 
	 */
	public static int columnWidthScorptype(){
		 return 12;
	}
	/**
	 * Getter S_HANDORGNAME , NOT NULL   	 
	 */
	public static int columnWidthShandorgname(){
		 return 200;
	}
	/**
	 * Getter S_TAXPAYCODE , NOT NULL   	 
	 */
	public static int columnWidthStaxpaycode(){
		 return 20;
	}
	/**
	 * Getter S_TAXPAYNAME , NOT NULL   	 
	 */
	public static int columnWidthStaxpayname(){
		 return 60;
	}
	/**
	 * Getter C_PRINTFLAG , NOT NULL   	 
	 */
	public static int columnWidthCprintflag(){
		 return 1;
	}
	/**
	 * Getter C_TRIMSIGN , NOT NULL   	 
	 */
	public static int columnWidthCtrimsign(){
		 return 1;
	}
	/**
	 * Getter S_REMARK   	 
	 */
	public static int columnWidthSremark(){
		 return 60;
	}
	/**
	 * Getter S_REMARK1   	 
	 */
	public static int columnWidthSremark1(){
		 return 60;
	}
	/**
	 * Getter S_REMARK2   	 
	 */
	public static int columnWidthSremark2(){
		 return 60;
	}
	/**
	 * Getter C_RECKONTYPE , NOT NULL   	 
	 */
	public static int columnWidthCreckontype(){
		 return 1;
	}
	/**
	 * Getter S_PROCSTAT , NOT NULL   	 
	 */
	public static int columnWidthSprocstat(){
		 return 2;
	}
	/**
	 * Getter S_STATUS , NOT NULL   	 
	 */
	public static int columnWidthSstatus(){
		 return 5;
	}
	/**
	 * Getter S_RESULT   	 
	 */
	public static int columnWidthSresult(){
		 return 5;
	}
	/**
	 * Getter S_ADDWORD   	 
	 */
	public static int columnWidthSaddword(){
		 return 60;
	}
	/**
	 * Getter C_CANCELSTAT , NOT NULL   	 
	 */
	public static int columnWidthCcancelstat(){
		 return 1;
	}
	/**
	 * Getter S_CHKBATCH   	 
	 */
	public static int columnWidthSchkbatch(){
		 return 4;
	}
	/**
	 * Getter S_EXPORTER   	 
	 */
	public static int columnWidthSexporter(){
		 return 10;
	}
	/**
	 * Getter S_ACCEPTDATE , NOT NULL   	 
	 */
	public static int columnWidthSacceptdate(){
		 return 8;
	}
	/**
	 * Getter S_PAYDATE   	 
	 */
	public static int columnWidthSpaydate(){
		 return 8;
	}
	/**
	 * Getter S_CHKDATE   	 
	 */
	public static int columnWidthSchkdate(){
		 return 8;
	}
}
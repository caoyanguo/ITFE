      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TF_GRANTPAY_ADJUSTSUB</p>
 * <p>Description: ������Ȩ֧������ƾ֤5351�ӱ� Data Information Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:58 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  tableinfo.vm version timestamp: 2007-05-24 16:30:00 
 *
 * @author win7
 */

public class TfGrantpayAdjustsubInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TF_GRANTPAY_ADJUSTSUB";
	
	/**
	 * column I_VOUSRLNO
	 */
	public static String I_VOUSRLNO ="I_VOUSRLNO";
	/**
	 * column I_SEQNO
	 */
	public static String I_SEQNO ="I_SEQNO";
	/**
	 * column S_ID
	 */
	public static String S_ID ="S_ID";
	/**
	 * column S_VOUCHERNO
	 */
	public static String S_VOUCHERNO ="S_VOUCHERNO";
	/**
	 * column S_SUPDEPCODE
	 */
	public static String S_SUPDEPCODE ="S_SUPDEPCODE";
	/**
	 * column S_SUPDEPNAME
	 */
	public static String S_SUPDEPNAME ="S_SUPDEPNAME";
	/**
	 * column S_EXPFUNCCODE
	 */
	public static String S_EXPFUNCCODE ="S_EXPFUNCCODE";
	/**
	 * column S_EXPFUNCNAME
	 */
	public static String S_EXPFUNCNAME ="S_EXPFUNCNAME";
	/**
	 * column N_PAYAMT
	 */
	public static String N_PAYAMT ="N_PAYAMT";
	/**
	 * column S_PAYSUMMARYNAME
	 */
	public static String S_PAYSUMMARYNAME ="S_PAYSUMMARYNAME";
	/**
	 * column S_XDEALRESULT
	 */
	public static String S_XDEALRESULT ="S_XDEALRESULT";
	/**
	 * column S_XADDWORD
	 */
	public static String S_XADDWORD ="S_XADDWORD";
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
	 * @return String table name of dto
	 */
	public static String getTableName() {
		return TABLENAME;
	}
	
	/**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[19];        
        columnNames[0]="I_VOUSRLNO";
        columnNames[1]="I_SEQNO";
        columnNames[2]="S_ID";
        columnNames[3]="S_VOUCHERNO";
        columnNames[4]="S_SUPDEPCODE";
        columnNames[5]="S_SUPDEPNAME";
        columnNames[6]="S_EXPFUNCCODE";
        columnNames[7]="S_EXPFUNCNAME";
        columnNames[8]="N_PAYAMT";
        columnNames[9]="S_PAYSUMMARYNAME";
        columnNames[10]="S_XDEALRESULT";
        columnNames[11]="S_XADDWORD";
        columnNames[12]="S_HOLD1";
        columnNames[13]="S_HOLD2";
        columnNames[14]="S_HOLD3";
        columnNames[15]="S_HOLD4";
        columnNames[16]="S_EXT1";
        columnNames[17]="S_EXT2";
        columnNames[18]="S_EXT3";
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
	 * Getter I_SEQNO, PK , NOT NULL   	 
	 */
	public static String columnIseqno(){
		 return I_SEQNO;
	}
	/**
	 *��� Getter S_ID , NOT NULL   	 
	 */
	public static String columnSid(){
		 return S_ID;
	}
	/**
	 *֧��ƾ֤���� Getter S_VOUCHERNO   	 
	 */
	public static String columnSvoucherno(){
		 return S_VOUCHERNO;
	}
	/**
	 *һ��Ԥ�㵥λ���� Getter S_SUPDEPCODE , NOT NULL   	 
	 */
	public static String columnSsupdepcode(){
		 return S_SUPDEPCODE;
	}
	/**
	 *һ��Ԥ�㵥λ���� Getter S_SUPDEPNAME , NOT NULL   	 
	 */
	public static String columnSsupdepname(){
		 return S_SUPDEPNAME;
	}
	/**
	 *֧�����ܷ����Ŀ���� Getter S_EXPFUNCCODE , NOT NULL   	 
	 */
	public static String columnSexpfunccode(){
		 return S_EXPFUNCCODE;
	}
	/**
	 *֧�����ܷ����Ŀ���� Getter S_EXPFUNCNAME , NOT NULL   	 
	 */
	public static String columnSexpfuncname(){
		 return S_EXPFUNCNAME;
	}
	/**
	 *֧����� Getter N_PAYAMT , NOT NULL   	 
	 */
	public static String columnNpayamt(){
		 return N_PAYAMT;
	}
	/**
	 *ժҪ���� Getter S_PAYSUMMARYNAME   	 
	 */
	public static String columnSpaysummaryname(){
		 return S_PAYSUMMARYNAME;
	}
	/**
	 *������� Getter S_XDEALRESULT , NOT NULL   	 
	 */
	public static String columnSxdealresult(){
		 return S_XDEALRESULT;
	}
	/**
	 *���� Getter S_XADDWORD   	 
	 */
	public static String columnSxaddword(){
		 return S_XADDWORD;
	}
	/**
	 *Ԥ���ֶ�1 Getter S_HOLD1   	 
	 */
	public static String columnShold1(){
		 return S_HOLD1;
	}
	/**
	 *Ԥ���ֶ�2 Getter S_HOLD2   	 
	 */
	public static String columnShold2(){
		 return S_HOLD2;
	}
	/**
	 *Ԥ���ֶ�3 Getter S_HOLD3   	 
	 */
	public static String columnShold3(){
		 return S_HOLD3;
	}
	/**
	 *Ԥ���ֶ�4 Getter S_HOLD4   	 
	 */
	public static String columnShold4(){
		 return S_HOLD4;
	}
	/**
	 *��չ Getter S_EXT1   	 
	 */
	public static String columnSext1(){
		 return S_EXT1;
	}
	/**
	 *��չ Getter S_EXT2   	 
	 */
	public static String columnSext2(){
		 return S_EXT2;
	}
	/**
	 *��չ Getter S_EXT3   	 
	 */
	public static String columnSext3(){
		 return S_EXT3;
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
	 * Getter I_SEQNO, PK , NOT NULL   	 
	 */
	public static String columnJavaTypeIseqno(){
		 return "Long";
	}
	/**
	 *��� Getter S_ID , NOT NULL   	 
	 */
	public static String columnJavaTypeSid(){
		 return "String";
	}
	/**
	 *֧��ƾ֤���� Getter S_VOUCHERNO   	 
	 */
	public static String columnJavaTypeSvoucherno(){
		 return "String";
	}
	/**
	 *һ��Ԥ�㵥λ���� Getter S_SUPDEPCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSsupdepcode(){
		 return "String";
	}
	/**
	 *һ��Ԥ�㵥λ���� Getter S_SUPDEPNAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSsupdepname(){
		 return "String";
	}
	/**
	 *֧�����ܷ����Ŀ���� Getter S_EXPFUNCCODE , NOT NULL   	 
	 */
	public static String columnJavaTypeSexpfunccode(){
		 return "String";
	}
	/**
	 *֧�����ܷ����Ŀ���� Getter S_EXPFUNCNAME , NOT NULL   	 
	 */
	public static String columnJavaTypeSexpfuncname(){
		 return "String";
	}
	/**
	 *֧����� Getter N_PAYAMT , NOT NULL   	 
	 */
	public static String columnJavaTypeNpayamt(){
		 return "BigDecimal";
	}
	/**
	 *ժҪ���� Getter S_PAYSUMMARYNAME   	 
	 */
	public static String columnJavaTypeSpaysummaryname(){
		 return "String";
	}
	/**
	 *������� Getter S_XDEALRESULT , NOT NULL   	 
	 */
	public static String columnJavaTypeSxdealresult(){
		 return "String";
	}
	/**
	 *���� Getter S_XADDWORD   	 
	 */
	public static String columnJavaTypeSxaddword(){
		 return "String";
	}
	/**
	 *Ԥ���ֶ�1 Getter S_HOLD1   	 
	 */
	public static String columnJavaTypeShold1(){
		 return "String";
	}
	/**
	 *Ԥ���ֶ�2 Getter S_HOLD2   	 
	 */
	public static String columnJavaTypeShold2(){
		 return "String";
	}
	/**
	 *Ԥ���ֶ�3 Getter S_HOLD3   	 
	 */
	public static String columnJavaTypeShold3(){
		 return "String";
	}
	/**
	 *Ԥ���ֶ�4 Getter S_HOLD4   	 
	 */
	public static String columnJavaTypeShold4(){
		 return "String";
	}
	/**
	 *��չ Getter S_EXT1   	 
	 */
	public static String columnJavaTypeSext1(){
		 return "String";
	}
	/**
	 *��չ Getter S_EXT2   	 
	 */
	public static String columnJavaTypeSext2(){
		 return "String";
	}
	/**
	 *��չ Getter S_EXT3   	 
	 */
	public static String columnJavaTypeSext3(){
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
	 * Getter I_SEQNO, PK , NOT NULL   	 
	 * columnType is BIGINT
	 */
	public static String columnDatabaseTypeIseqno(){
		 return "BIGINT";
	}
	/**
	 *��� Getter S_ID , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSid(){
		 return "VARCHAR";
	}
	/**
	 *֧��ƾ֤���� Getter S_VOUCHERNO   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSvoucherno(){
		 return "VARCHAR";
	}
	/**
	 *һ��Ԥ�㵥λ���� Getter S_SUPDEPCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSsupdepcode(){
		 return "VARCHAR";
	}
	/**
	 *һ��Ԥ�㵥λ���� Getter S_SUPDEPNAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSsupdepname(){
		 return "VARCHAR";
	}
	/**
	 *֧�����ܷ����Ŀ���� Getter S_EXPFUNCCODE , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSexpfunccode(){
		 return "VARCHAR";
	}
	/**
	 *֧�����ܷ����Ŀ���� Getter S_EXPFUNCNAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSexpfuncname(){
		 return "VARCHAR";
	}
	/**
	 *֧����� Getter N_PAYAMT , NOT NULL   	 
	 * columnType is DECIMAL
	 */
	public static String columnDatabaseTypeNpayamt(){
		 return "DECIMAL";
	}
	/**
	 *ժҪ���� Getter S_PAYSUMMARYNAME   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpaysummaryname(){
		 return "VARCHAR";
	}
	/**
	 *������� Getter S_XDEALRESULT , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSxdealresult(){
		 return "VARCHAR";
	}
	/**
	 *���� Getter S_XADDWORD   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSxaddword(){
		 return "VARCHAR";
	}
	/**
	 *Ԥ���ֶ�1 Getter S_HOLD1   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeShold1(){
		 return "VARCHAR";
	}
	/**
	 *Ԥ���ֶ�2 Getter S_HOLD2   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeShold2(){
		 return "VARCHAR";
	}
	/**
	 *Ԥ���ֶ�3 Getter S_HOLD3   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeShold3(){
		 return "VARCHAR";
	}
	/**
	 *Ԥ���ֶ�4 Getter S_HOLD4   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeShold4(){
		 return "VARCHAR";
	}
	/**
	 *��չ Getter S_EXT1   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSext1(){
		 return "VARCHAR";
	}
	/**
	 *��չ Getter S_EXT2   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSext2(){
		 return "VARCHAR";
	}
	/**
	 *��չ Getter S_EXT3   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSext3(){
		 return "VARCHAR";
	}
	/******************************************************
	*
	*  Get Column With
	*
	*******************************************************/
	/**
	 *��� Getter S_ID , NOT NULL   	 
	 */
	public static int columnWidthSid(){
		 return 38;
	}
	/**
	 *֧��ƾ֤���� Getter S_VOUCHERNO   	 
	 */
	public static int columnWidthSvoucherno(){
		 return 42;
	}
	/**
	 *һ��Ԥ�㵥λ���� Getter S_SUPDEPCODE , NOT NULL   	 
	 */
	public static int columnWidthSsupdepcode(){
		 return 42;
	}
	/**
	 *һ��Ԥ�㵥λ���� Getter S_SUPDEPNAME , NOT NULL   	 
	 */
	public static int columnWidthSsupdepname(){
		 return 60;
	}
	/**
	 *֧�����ܷ����Ŀ���� Getter S_EXPFUNCCODE , NOT NULL   	 
	 */
	public static int columnWidthSexpfunccode(){
		 return 42;
	}
	/**
	 *֧�����ܷ����Ŀ���� Getter S_EXPFUNCNAME , NOT NULL   	 
	 */
	public static int columnWidthSexpfuncname(){
		 return 60;
	}
	/**
	 *ժҪ���� Getter S_PAYSUMMARYNAME   	 
	 */
	public static int columnWidthSpaysummaryname(){
		 return 200;
	}
	/**
	 *������� Getter S_XDEALRESULT , NOT NULL   	 
	 */
	public static int columnWidthSxdealresult(){
		 return 10;
	}
	/**
	 *���� Getter S_XADDWORD   	 
	 */
	public static int columnWidthSxaddword(){
		 return 255;
	}
	/**
	 *Ԥ���ֶ�1 Getter S_HOLD1   	 
	 */
	public static int columnWidthShold1(){
		 return 42;
	}
	/**
	 *Ԥ���ֶ�2 Getter S_HOLD2   	 
	 */
	public static int columnWidthShold2(){
		 return 42;
	}
	/**
	 *Ԥ���ֶ�3 Getter S_HOLD3   	 
	 */
	public static int columnWidthShold3(){
		 return 42;
	}
	/**
	 *Ԥ���ֶ�4 Getter S_HOLD4   	 
	 */
	public static int columnWidthShold4(){
		 return 42;
	}
	/**
	 *��չ Getter S_EXT1   	 
	 */
	public static int columnWidthSext1(){
		 return 50;
	}
	/**
	 *��չ Getter S_EXT2   	 
	 */
	public static int columnWidthSext2(){
		 return 50;
	}
	/**
	 *��չ Getter S_EXT3   	 
	 */
	public static int columnWidthSext3(){
		 return 50;
	}
}
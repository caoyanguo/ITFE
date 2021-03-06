      

package com.cfcc.itfe.persistence.info;

/**
 * <p>Title: Information of table: TS_USERS</p>
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

public class TsUsersInfo
{
	/**
	 * table name of dto
	 */
	public static String TABLENAME = "TS_USERS";
	
	/**
	 * column S_ORGCODE
	 */
	public static String S_ORGCODE ="S_ORGCODE";
	/**
	 * column S_USERCODE
	 */
	public static String S_USERCODE ="S_USERCODE";
	/**
	 * column S_USERNAME
	 */
	public static String S_USERNAME ="S_USERNAME";
	/**
	 * column S_PASSWORD
	 */
	public static String S_PASSWORD ="S_PASSWORD";
	/**
	 * column S_CACERTDN
	 */
	public static String S_CACERTDN ="S_CACERTDN";
	/**
	 * column S_USERTYPE
	 */
	public static String S_USERTYPE ="S_USERTYPE";
	/**
	 * column S_USERSTATUS
	 */
	public static String S_USERSTATUS ="S_USERSTATUS";
	/**
	 * column I_MODICOUNT
	 */
	public static String I_MODICOUNT ="I_MODICOUNT";
	/**
	 * column S_PASSMODCYCLE
	 */
	public static String S_PASSMODCYCLE ="S_PASSMODCYCLE";
	/**
	 * column S_PASSMODDATE
	 */
	public static String S_PASSMODDATE ="S_PASSMODDATE";
	/**
	 * column S_LOGINSTATUS
	 */
	public static String S_LOGINSTATUS ="S_LOGINSTATUS";
	/**
	 * column S_LASTLOGINTIME
	 */
	public static String S_LASTLOGINTIME ="S_LASTLOGINTIME";
	/**
	 * column S_LASTEXITTIME
	 */
	public static String S_LASTEXITTIME ="S_LASTEXITTIME";
	/**
	 * column S_CERTID
	 */
	public static String S_CERTID ="S_CERTID";
	/**
	 * column S_STAMPID
	 */
	public static String S_STAMPID ="S_STAMPID";
	/**
	 * column S_HOLD1
	 */
	public static String S_HOLD1 ="S_HOLD1";
	/**
	 * column S_HOLD2
	 */
	public static String S_HOLD2 ="S_HOLD2";
	/**
	 * column S_STAMPPERMISSION
	 */
	public static String S_STAMPPERMISSION ="S_STAMPPERMISSION";
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
        String[] columnNames = new String[18];        
        columnNames[0]="S_ORGCODE";
        columnNames[1]="S_USERCODE";
        columnNames[2]="S_USERNAME";
        columnNames[3]="S_PASSWORD";
        columnNames[4]="S_CACERTDN";
        columnNames[5]="S_USERTYPE";
        columnNames[6]="S_USERSTATUS";
        columnNames[7]="I_MODICOUNT";
        columnNames[8]="S_PASSMODCYCLE";
        columnNames[9]="S_PASSMODDATE";
        columnNames[10]="S_LOGINSTATUS";
        columnNames[11]="S_LASTLOGINTIME";
        columnNames[12]="S_LASTEXITTIME";
        columnNames[13]="S_CERTID";
        columnNames[14]="S_STAMPID";
        columnNames[15]="S_HOLD1";
        columnNames[16]="S_HOLD2";
        columnNames[17]="S_STAMPPERMISSION";
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
	 * Getter S_USERCODE, PK , NOT NULL   	 
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
	 * Getter S_PASSWORD   	 
	 */
	public static String columnSpassword(){
		 return S_PASSWORD;
	}
	/**
	 * Getter S_CACERTDN   	 
	 */
	public static String columnScacertdn(){
		 return S_CACERTDN;
	}
	/**
	 * Getter S_USERTYPE   	 
	 */
	public static String columnSusertype(){
		 return S_USERTYPE;
	}
	/**
	 * Getter S_USERSTATUS   	 
	 */
	public static String columnSuserstatus(){
		 return S_USERSTATUS;
	}
	/**
	 * Getter I_MODICOUNT   	 
	 */
	public static String columnImodicount(){
		 return I_MODICOUNT;
	}
	/**
	 * Getter S_PASSMODCYCLE   	 
	 */
	public static String columnSpassmodcycle(){
		 return S_PASSMODCYCLE;
	}
	/**
	 * Getter S_PASSMODDATE   	 
	 */
	public static String columnSpassmoddate(){
		 return S_PASSMODDATE;
	}
	/**
	 * Getter S_LOGINSTATUS   	 
	 */
	public static String columnSloginstatus(){
		 return S_LOGINSTATUS;
	}
	/**
	 * Getter S_LASTLOGINTIME   	 
	 */
	public static String columnSlastlogintime(){
		 return S_LASTLOGINTIME;
	}
	/**
	 * Getter S_LASTEXITTIME   	 
	 */
	public static String columnSlastexittime(){
		 return S_LASTEXITTIME;
	}
	/**
	 * Getter S_CERTID   	 
	 */
	public static String columnScertid(){
		 return S_CERTID;
	}
	/**
	 * Getter S_STAMPID   	 
	 */
	public static String columnSstampid(){
		 return S_STAMPID;
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
	 * Getter S_STAMPPERMISSION   	 
	 */
	public static String columnSstamppermission(){
		 return S_STAMPPERMISSION;
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
	 * Getter S_USERCODE, PK , NOT NULL   	 
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
	 * Getter S_PASSWORD   	 
	 */
	public static String columnJavaTypeSpassword(){
		 return "String";
	}
	/**
	 * Getter S_CACERTDN   	 
	 */
	public static String columnJavaTypeScacertdn(){
		 return "String";
	}
	/**
	 * Getter S_USERTYPE   	 
	 */
	public static String columnJavaTypeSusertype(){
		 return "String";
	}
	/**
	 * Getter S_USERSTATUS   	 
	 */
	public static String columnJavaTypeSuserstatus(){
		 return "String";
	}
	/**
	 * Getter I_MODICOUNT   	 
	 */
	public static String columnJavaTypeImodicount(){
		 return "Integer";
	}
	/**
	 * Getter S_PASSMODCYCLE   	 
	 */
	public static String columnJavaTypeSpassmodcycle(){
		 return "Integer";
	}
	/**
	 * Getter S_PASSMODDATE   	 
	 */
	public static String columnJavaTypeSpassmoddate(){
		 return "String";
	}
	/**
	 * Getter S_LOGINSTATUS   	 
	 */
	public static String columnJavaTypeSloginstatus(){
		 return "String";
	}
	/**
	 * Getter S_LASTLOGINTIME   	 
	 */
	public static String columnJavaTypeSlastlogintime(){
		 return "Timestamp";
	}
	/**
	 * Getter S_LASTEXITTIME   	 
	 */
	public static String columnJavaTypeSlastexittime(){
		 return "Timestamp";
	}
	/**
	 * Getter S_CERTID   	 
	 */
	public static String columnJavaTypeScertid(){
		 return "String";
	}
	/**
	 * Getter S_STAMPID   	 
	 */
	public static String columnJavaTypeSstampid(){
		 return "String";
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
	 * Getter S_STAMPPERMISSION   	 
	 */
	public static String columnJavaTypeSstamppermission(){
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
	 * Getter S_USERCODE, PK , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSusercode(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_USERNAME , NOT NULL   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSusername(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_PASSWORD   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSpassword(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_CACERTDN   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeScacertdn(){
		 return "VARCHAR";
	}
	/**
	 * Getter S_USERTYPE   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSusertype(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_USERSTATUS   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSuserstatus(){
		 return "CHARACTER";
	}
	/**
	 * Getter I_MODICOUNT   	 
	 * columnType is INTEGER
	 */
	public static String columnDatabaseTypeImodicount(){
		 return "INTEGER";
	}
	/**
	 * Getter S_PASSMODCYCLE   	 
	 * columnType is INTEGER
	 */
	public static String columnDatabaseTypeSpassmodcycle(){
		 return "INTEGER";
	}
	/**
	 * Getter S_PASSMODDATE   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSpassmoddate(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_LOGINSTATUS   	 
	 * columnType is CHARACTER
	 */
	public static String columnDatabaseTypeSloginstatus(){
		 return "CHARACTER";
	}
	/**
	 * Getter S_LASTLOGINTIME   	 
	 * columnType is TIMESTAMP
	 */
	public static String columnDatabaseTypeSlastlogintime(){
		 return "TIMESTAMP";
	}
	/**
	 * Getter S_LASTEXITTIME   	 
	 * columnType is TIMESTAMP
	 */
	public static String columnDatabaseTypeSlastexittime(){
		 return "TIMESTAMP";
	}
	/**
	 * Getter S_CERTID   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeScertid(){
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
	 * Getter S_STAMPPERMISSION   	 
	 * columnType is VARCHAR
	 */
	public static String columnDatabaseTypeSstamppermission(){
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
	 * Getter S_USERCODE, PK , NOT NULL   	 
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
	 * Getter S_PASSWORD   	 
	 */
	public static int columnWidthSpassword(){
		 return 50;
	}
	/**
	 * Getter S_CACERTDN   	 
	 */
	public static int columnWidthScacertdn(){
		 return 200;
	}
	/**
	 * Getter S_USERTYPE   	 
	 */
	public static int columnWidthSusertype(){
		 return 1;
	}
	/**
	 * Getter S_USERSTATUS   	 
	 */
	public static int columnWidthSuserstatus(){
		 return 1;
	}
	/**
	 * Getter S_PASSMODDATE   	 
	 */
	public static int columnWidthSpassmoddate(){
		 return 8;
	}
	/**
	 * Getter S_LOGINSTATUS   	 
	 */
	public static int columnWidthSloginstatus(){
		 return 1;
	}
	/**
	 * Getter S_CERTID   	 
	 */
	public static int columnWidthScertid(){
		 return 64;
	}
	/**
	 * Getter S_STAMPID   	 
	 */
	public static int columnWidthSstampid(){
		 return 64;
	}
	/**
	 * Getter S_HOLD1   	 
	 */
	public static int columnWidthShold1(){
		 return 50;
	}
	/**
	 * Getter S_HOLD2   	 
	 */
	public static int columnWidthShold2(){
		 return 50;
	}
	/**
	 * Getter S_STAMPPERMISSION   	 
	 */
	public static int columnWidthSstamppermission(){
		 return 30;
	}
}
    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TsSyslogPK;
/**
 * <p>Title: DTO of table: TS_SYSLOG</p>
 * <p>Description:  Data Transfer Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:01 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  dto.vm version timestamp: 2008-01-08 16:30:00 
 *
 * @author win7
 */

public class TsSyslogDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  I_NO INTEGER , PK   , NOT NULL  , Identity  , Generated     */
    protected Integer ino;
    /**
    *  S_ORGCODE VARCHAR   , NOT NULL       */
    protected String sorgcode;
    /**
    *  S_DATE CHARACTER   , NOT NULL       */
    protected String sdate;
    /**
    *  S_TIME TIMESTAMP   , NOT NULL       */
    protected Timestamp stime;
    /**
    *  S_OPERATIONTYPECODE VARCHAR         */
    protected String soperationtypecode;
    /**
    *  S_USERCODE VARCHAR   , NOT NULL       */
    protected String susercode;
    /**
    *  S_OPERATIONDESC VARCHAR         */
    protected String soperationdesc;
    /**
    *  S_DEMO VARCHAR         */
    protected String sdemo;


/******************************************************
*
*  getter and setter
*
*******************************************************/


    public Integer getIno()
    {
        return ino;
    }
     /**
     *   Setter I_NO, PK , NOT NULL  , Identity  , Generated      * */
    public void setIno(Integer _ino) {
        this.ino = _ino;
    }


    public String getSorgcode()
    {
        return sorgcode;
    }
     /**
     *   Setter S_ORGCODE , NOT NULL        * */
    public void setSorgcode(String _sorgcode) {
        this.sorgcode = _sorgcode;
    }


    public String getSdate()
    {
        return sdate;
    }
     /**
     *   Setter S_DATE , NOT NULL        * */
    public void setSdate(String _sdate) {
        this.sdate = _sdate;
    }


    public Timestamp getStime()
    {
        return stime;
    }
     /**
     *   Setter S_TIME , NOT NULL        * */
    public void setStime(Timestamp _stime) {
        this.stime = _stime;
    }


    public String getSoperationtypecode()
    {
        return soperationtypecode;
    }
     /**
     *   Setter S_OPERATIONTYPECODE        * */
    public void setSoperationtypecode(String _soperationtypecode) {
        this.soperationtypecode = _soperationtypecode;
    }


    public String getSusercode()
    {
        return susercode;
    }
     /**
     *   Setter S_USERCODE , NOT NULL        * */
    public void setSusercode(String _susercode) {
        this.susercode = _susercode;
    }


    public String getSoperationdesc()
    {
        return soperationdesc;
    }
     /**
     *   Setter S_OPERATIONDESC        * */
    public void setSoperationdesc(String _soperationdesc) {
        this.soperationdesc = _soperationdesc;
    }


    public String getSdemo()
    {
        return sdemo;
    }
     /**
     *   Setter S_DEMO        * */
    public void setSdemo(String _sdemo) {
        this.sdemo = _sdemo;
    }




/******************************************************
*
*  Get Column Name
*
*******************************************************/
    /**
    *   Getter I_NO, PK , NOT NULL  , Identity  , Generated     * */
    public static String  columnIno()
    {
        return "I_NO";
    }
   
    /**
    *   Getter S_ORGCODE , NOT NULL       * */
    public static String  columnSorgcode()
    {
        return "S_ORGCODE";
    }
   
    /**
    *   Getter S_DATE , NOT NULL       * */
    public static String  columnSdate()
    {
        return "S_DATE";
    }
   
    /**
    *   Getter S_TIME , NOT NULL       * */
    public static String  columnStime()
    {
        return "S_TIME";
    }
   
    /**
    *   Getter S_OPERATIONTYPECODE       * */
    public static String  columnSoperationtypecode()
    {
        return "S_OPERATIONTYPECODE";
    }
   
    /**
    *   Getter S_USERCODE , NOT NULL       * */
    public static String  columnSusercode()
    {
        return "S_USERCODE";
    }
   
    /**
    *   Getter S_OPERATIONDESC       * */
    public static String  columnSoperationdesc()
    {
        return "S_OPERATIONDESC";
    }
   
    /**
    *   Getter S_DEMO       * */
    public static String  columnSdemo()
    {
        return "S_DEMO";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "TS_SYSLOG";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[8];        
        columnNames[0]="I_NO";
        columnNames[1]="S_ORGCODE";
        columnNames[2]="S_DATE";
        columnNames[3]="S_TIME";
        columnNames[4]="S_OPERATIONTYPECODE";
        columnNames[5]="S_USERCODE";
        columnNames[6]="S_OPERATIONDESC";
        columnNames[7]="S_DEMO";
        return columnNames;     
    }
/*******************************************************
*
*  supplementary methods
*
*****************************************************/


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TsSyslogDto))
            return false;

        TsSyslogDto bean = (TsSyslogDto) obj;


      //compare field ino
      if((this.ino==null && bean.ino!=null) || (this.ino!=null && bean.ino==null))
          return false;
        else if(this.ino==null && bean.ino==null){
        }
        else{
          if(!bean.ino.equals(this.ino))
               return false;
     }
      //compare field sorgcode
      if((this.sorgcode==null && bean.sorgcode!=null) || (this.sorgcode!=null && bean.sorgcode==null))
          return false;
        else if(this.sorgcode==null && bean.sorgcode==null){
        }
        else{
          if(!bean.sorgcode.equals(this.sorgcode))
               return false;
     }
      //compare field sdate
      if((this.sdate==null && bean.sdate!=null) || (this.sdate!=null && bean.sdate==null))
          return false;
        else if(this.sdate==null && bean.sdate==null){
        }
        else{
          if(!bean.sdate.equals(this.sdate))
               return false;
     }
      //compare field stime
      if((this.stime==null && bean.stime!=null) || (this.stime!=null && bean.stime==null))
          return false;
        else if(this.stime==null && bean.stime==null){
        }
        else{
          if(!bean.stime.equals(this.stime))
               return false;
     }
      //compare field soperationtypecode
      if((this.soperationtypecode==null && bean.soperationtypecode!=null) || (this.soperationtypecode!=null && bean.soperationtypecode==null))
          return false;
        else if(this.soperationtypecode==null && bean.soperationtypecode==null){
        }
        else{
          if(!bean.soperationtypecode.equals(this.soperationtypecode))
               return false;
     }
      //compare field susercode
      if((this.susercode==null && bean.susercode!=null) || (this.susercode!=null && bean.susercode==null))
          return false;
        else if(this.susercode==null && bean.susercode==null){
        }
        else{
          if(!bean.susercode.equals(this.susercode))
               return false;
     }
      //compare field soperationdesc
      if((this.soperationdesc==null && bean.soperationdesc!=null) || (this.soperationdesc!=null && bean.soperationdesc==null))
          return false;
        else if(this.soperationdesc==null && bean.soperationdesc==null){
        }
        else{
          if(!bean.soperationdesc.equals(this.soperationdesc))
               return false;
     }
      //compare field sdemo
      if((this.sdemo==null && bean.sdemo!=null) || (this.sdemo!=null && bean.sdemo==null))
          return false;
        else if(this.sdemo==null && bean.sdemo==null){
        }
        else{
          if(!bean.sdemo.equals(this.sdemo))
               return false;
     }



        return true;
    }

    /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
  
		int _hash_ = 1;
		
        if(this.ino!=null)
          _hash_ = _hash_ * 31+ this.ino.hashCode() ;
        if(this.sorgcode!=null)
          _hash_ = _hash_ * 31+ this.sorgcode.hashCode() ;
        if(this.sdate!=null)
          _hash_ = _hash_ * 31+ this.sdate.hashCode() ;
        if(this.stime!=null)
          _hash_ = _hash_ * 31+ this.stime.hashCode() ;
        if(this.soperationtypecode!=null)
          _hash_ = _hash_ * 31+ this.soperationtypecode.hashCode() ;
        if(this.susercode!=null)
          _hash_ = _hash_ * 31+ this.susercode.hashCode() ;
        if(this.soperationdesc!=null)
          _hash_ = _hash_ * 31+ this.soperationdesc.hashCode() ;
        if(this.sdemo!=null)
          _hash_ = _hash_ * 31+ this.sdemo.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsSyslogDto bean = new TsSyslogDto();

          bean.ino = this.ino;

          if (this.sorgcode != null)
            bean.sorgcode = new String(this.sorgcode);
          if (this.sdate != null)
            bean.sdate = new String(this.sdate);
          if (this.stime != null)
            bean.stime = (Timestamp) this.stime.clone();
          if (this.soperationtypecode != null)
            bean.soperationtypecode = new String(this.soperationtypecode);
          if (this.susercode != null)
            bean.susercode = new String(this.susercode);
          if (this.soperationdesc != null)
            bean.soperationdesc = new String(this.soperationdesc);
          if (this.sdemo != null)
            bean.sdemo = new String(this.sdemo);
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsSyslogDto").append(sep);
        sb.append("[ino]").append(" = ").append(ino).append(sep);
        sb.append("[sorgcode]").append(" = ").append(sorgcode).append(sep);
        sb.append("[sdate]").append(" = ").append(sdate).append(sep);
        sb.append("[stime]").append(" = ").append(stime).append(sep);
        sb.append("[soperationtypecode]").append(" = ").append(soperationtypecode).append(sep);
        sb.append("[susercode]").append(" = ").append(susercode).append(sep);
        sb.append("[soperationdesc]").append(" = ").append(soperationdesc).append(sep);
        sb.append("[sdemo]").append(" = ").append(sdemo).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //don't need check field I_NO,it is generated column
  
    //check field S_ORGCODE
      if (this.getSorgcode()==null)
             sb.append("S_ORGCODE����Ϊ��; ");
      if (this.getSorgcode()!=null)
             if (this.getSorgcode().getBytes().length > 12)
                sb.append("S_ORGCODE���Ȳ��ܳ��� "+12+"���ַ�; ");
    
    //check field S_DATE
      if (this.getSdate()==null)
             sb.append("S_DATE����Ϊ��; ");
      if (this.getSdate()!=null)
             if (this.getSdate().getBytes().length > 8)
                sb.append("S_DATE���Ȳ��ܳ��� "+8+"���ַ�; ");
    
    //check field S_TIME
      if (this.getStime()==null)
             sb.append("S_TIME����Ϊ��; ");
      
    //check field S_OPERATIONTYPECODE
      if (this.getSoperationtypecode()!=null)
             if (this.getSoperationtypecode().getBytes().length > 10)
                sb.append("S_OPERATIONTYPECODE���Ȳ��ܳ��� "+10+"���ַ�; ");
    
    //check field S_USERCODE
      if (this.getSusercode()==null)
             sb.append("S_USERCODE����Ϊ��; ");
      if (this.getSusercode()!=null)
             if (this.getSusercode().getBytes().length > 30)
                sb.append("S_USERCODE���Ȳ��ܳ��� "+30+"���ַ�; ");
    
    //check field S_OPERATIONDESC
      if (this.getSoperationdesc()!=null)
             if (this.getSoperationdesc().getBytes().length > 200)
                sb.append("S_OPERATIONDESC���Ȳ��ܳ��� "+200+"���ַ�; ");
    
    //check field S_DEMO
      if (this.getSdemo()!=null)
             if (this.getSdemo().getBytes().length > 100)
                sb.append("S_DEMO���Ȳ��ܳ��� "+100+"���ַ�; ");
    

 	String msg = sb.toString() ;
	if (msg.length() == 0)
		  return null ;

  return  msg;
  }
  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid(String[] _columnNames)
  {
  	StringBuffer sb = new StringBuffer() ;
    // check columnNames
    String checkNameMsg = checkColumnNamesValid(_columnNames);
    if (checkNameMsg != null) {
         return checkNameMsg;
    }
    //don't need check field I_NO,it is generated column
  
    //check field S_ORGCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_ORGCODE")) {
               if (this.getSorgcode()==null)
                    sb.append("S_ORGCODE ����Ϊ��; ");
               if (this.getSorgcode()!=null)
                    if (this.getSorgcode().getBytes().length > 12)
                        sb.append("S_ORGCODE ���Ȳ��ܳ��� "+12+"���ַ�");
             break;
         }
  }
    
    //check field S_DATE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_DATE")) {
               if (this.getSdate()==null)
                    sb.append("S_DATE ����Ϊ��; ");
               if (this.getSdate()!=null)
                    if (this.getSdate().getBytes().length > 8)
                        sb.append("S_DATE ���Ȳ��ܳ��� "+8+"���ַ�");
             break;
         }
  }
    
    //check field S_TIME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TIME")) {
               if (this.getStime()==null)
                    sb.append("S_TIME ����Ϊ��; ");
               break;
         }
  }
    
    //check field S_OPERATIONTYPECODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_OPERATIONTYPECODE")) {
                 if (this.getSoperationtypecode()!=null)
                    if (this.getSoperationtypecode().getBytes().length > 10)
                        sb.append("S_OPERATIONTYPECODE ���Ȳ��ܳ��� "+10+"���ַ�");
             break;
         }
  }
    
    //check field S_USERCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_USERCODE")) {
               if (this.getSusercode()==null)
                    sb.append("S_USERCODE ����Ϊ��; ");
               if (this.getSusercode()!=null)
                    if (this.getSusercode().getBytes().length > 30)
                        sb.append("S_USERCODE ���Ȳ��ܳ��� "+30+"���ַ�");
             break;
         }
  }
    
    //check field S_OPERATIONDESC
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_OPERATIONDESC")) {
                 if (this.getSoperationdesc()!=null)
                    if (this.getSoperationdesc().getBytes().length > 200)
                        sb.append("S_OPERATIONDESC ���Ȳ��ܳ��� "+200+"���ַ�");
             break;
         }
  }
    
    //check field S_DEMO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_DEMO")) {
                 if (this.getSdemo()!=null)
                    if (this.getSdemo().getBytes().length > 100)
                        sb.append("S_DEMO ���Ȳ��ܳ��� "+100+"���ַ�");
             break;
         }
  }
    
 	String msg = sb.toString() ;
	if (msg.length() == 0)
		  return null ;

  return  msg;
  }
  /* Returns value valid checking String , NULL is Valid*/
	public String checkValidExcept(String[] _columnNames) {
		String msg = checkColumnNamesValid(_columnNames);
		if (msg != null) {
			return msg;
		}
		String[] columnCheckNames = new String[columnNames().length
				- _columnNames.length];
		int k = 0;
		for (int i = 0; i < columnNames().length; i++) {
			boolean checkNameInColumn = true;
			for (int j = 0; j < _columnNames.length; j++) {
				if (_columnNames[i].equals(columnNames()[j])) {
					checkNameInColumn = false;
					break;
				}
			}
			if (checkNameInColumn) {
				columnCheckNames[k] = columnNames()[i];
				k++;
			}
		}
		return checkValid(columnCheckNames);
	}
	/* Returns value valid checking String , NULL is Valid */
	public String checkColumnNamesValid(String[] _columnNames) {
		StringBuffer sb = new StringBuffer();
		if (_columnNames.length > columnNames().length) {
			return "�����ֶθ������ڱ����ֶθ���; ";
		}
		// check columnNames
		for (int i = 0; i < _columnNames.length; i++) {
			boolean checkNameValid = false;
			for (int j = 0; j < columnNames().length; j++) {
				if (_columnNames[i] != null
						&& _columnNames[i].equals(columnNames()[j])) {
					checkNameValid = true;
					break;
				}
			}
			if (!checkNameValid)
				sb.append("�����ֶ� " + _columnNames[i] + " ���ڸñ��ֶ���; ");
		}
		String msg = sb.toString();
		if (msg.length() == 0)
			return null;

		return msg;
	}
/*******************************************************
*
*  implement IDto
*
*****************************************************/

  /* if this Dto has children Dtos*/
  public boolean  isParent() {
     return false;
  };

  /* get the children Dtos if this has children*/
  public IDto[]  getChildren() {
     return null;
  };

  /* set the children Dtos if this has children*/
  public void  setChildren(IDto[] _dtos) 
  {
     throw new RuntimeException("��dtoû�����������dto�����ܽ��д˲���");
  };
  
  /* return the IPK class  */
    public IPK      getPK(){
      TsSyslogPK pk = new TsSyslogPK();
      pk.setIno(getIno());
      return pk;
    };
}
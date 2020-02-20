    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TdTaxorgParamPK;
/**
 * <p>Title: DTO of table: TD_TAXORG_PARAM</p>
 * <p>Description:  Data Transfer Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:57 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  dto.vm version timestamp: 2008-01-08 16:30:00 
 *
 * @author win7
 */

public class TdTaxorgParamDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  I_SEQNO BIGINT , PK   , NOT NULL  , Identity  , Generated     */
    protected Long iseqno;
    /**
    *  S_BOOKORGCODE CHARACTER   , NOT NULL       */
    protected String sbookorgcode;
    /**
    *  C_TRIMFLAG CHARACTER   , NOT NULL       */
    protected String ctrimflag;
    /**
    *  S_TAXORGCODE VARCHAR   , NOT NULL       */
    protected String staxorgcode;
    /**
    *  S_TAXORGNAME VARCHAR   , NOT NULL       */
    protected String staxorgname;
    /**
    *  C_TAXORGPROP CHARACTER         */
    protected String ctaxorgprop;
    /**
    *  S_TAXORGPHONE VARCHAR         */
    protected String staxorgphone;
    /**
    *  S_TAXORGSHT VARCHAR         */
    protected String staxorgsht;
    /**
    *  TS_SYSUPDATE TIMESTAMP   , NOT NULL       */
    protected Timestamp tssysupdate;


/******************************************************
*
*  getter and setter
*
*******************************************************/


    public Long getIseqno()
    {
        return iseqno;
    }
     /**
     *   Setter I_SEQNO, PK , NOT NULL  , Identity  , Generated      * */
    public void setIseqno(Long _iseqno) {
        this.iseqno = _iseqno;
    }


    public String getSbookorgcode()
    {
        return sbookorgcode;
    }
     /**
     *   Setter S_BOOKORGCODE , NOT NULL        * */
    public void setSbookorgcode(String _sbookorgcode) {
        this.sbookorgcode = _sbookorgcode;
    }


    public String getCtrimflag()
    {
        return ctrimflag;
    }
     /**
     *   Setter C_TRIMFLAG , NOT NULL        * */
    public void setCtrimflag(String _ctrimflag) {
        this.ctrimflag = _ctrimflag;
    }


    public String getStaxorgcode()
    {
        return staxorgcode;
    }
     /**
     *   Setter S_TAXORGCODE , NOT NULL        * */
    public void setStaxorgcode(String _staxorgcode) {
        this.staxorgcode = _staxorgcode;
    }


    public String getStaxorgname()
    {
        return staxorgname;
    }
     /**
     *   Setter S_TAXORGNAME , NOT NULL        * */
    public void setStaxorgname(String _staxorgname) {
        this.staxorgname = _staxorgname;
    }


    public String getCtaxorgprop()
    {
        return ctaxorgprop;
    }
     /**
     *   Setter C_TAXORGPROP        * */
    public void setCtaxorgprop(String _ctaxorgprop) {
        this.ctaxorgprop = _ctaxorgprop;
    }


    public String getStaxorgphone()
    {
        return staxorgphone;
    }
     /**
     *   Setter S_TAXORGPHONE        * */
    public void setStaxorgphone(String _staxorgphone) {
        this.staxorgphone = _staxorgphone;
    }


    public String getStaxorgsht()
    {
        return staxorgsht;
    }
     /**
     *   Setter S_TAXORGSHT        * */
    public void setStaxorgsht(String _staxorgsht) {
        this.staxorgsht = _staxorgsht;
    }


    public Timestamp getTssysupdate()
    {
        return tssysupdate;
    }
     /**
     *   Setter TS_SYSUPDATE , NOT NULL        * */
    public void setTssysupdate(Timestamp _tssysupdate) {
        this.tssysupdate = _tssysupdate;
    }




/******************************************************
*
*  Get Column Name
*
*******************************************************/
    /**
    *   Getter I_SEQNO, PK , NOT NULL  , Identity  , Generated     * */
    public static String  columnIseqno()
    {
        return "I_SEQNO";
    }
   
    /**
    *   Getter S_BOOKORGCODE , NOT NULL       * */
    public static String  columnSbookorgcode()
    {
        return "S_BOOKORGCODE";
    }
   
    /**
    *   Getter C_TRIMFLAG , NOT NULL       * */
    public static String  columnCtrimflag()
    {
        return "C_TRIMFLAG";
    }
   
    /**
    *   Getter S_TAXORGCODE , NOT NULL       * */
    public static String  columnStaxorgcode()
    {
        return "S_TAXORGCODE";
    }
   
    /**
    *   Getter S_TAXORGNAME , NOT NULL       * */
    public static String  columnStaxorgname()
    {
        return "S_TAXORGNAME";
    }
   
    /**
    *   Getter C_TAXORGPROP       * */
    public static String  columnCtaxorgprop()
    {
        return "C_TAXORGPROP";
    }
   
    /**
    *   Getter S_TAXORGPHONE       * */
    public static String  columnStaxorgphone()
    {
        return "S_TAXORGPHONE";
    }
   
    /**
    *   Getter S_TAXORGSHT       * */
    public static String  columnStaxorgsht()
    {
        return "S_TAXORGSHT";
    }
   
    /**
    *   Getter TS_SYSUPDATE , NOT NULL       * */
    public static String  columnTssysupdate()
    {
        return "TS_SYSUPDATE";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "TD_TAXORG_PARAM";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[9];        
        columnNames[0]="I_SEQNO";
        columnNames[1]="S_BOOKORGCODE";
        columnNames[2]="C_TRIMFLAG";
        columnNames[3]="S_TAXORGCODE";
        columnNames[4]="S_TAXORGNAME";
        columnNames[5]="C_TAXORGPROP";
        columnNames[6]="S_TAXORGPHONE";
        columnNames[7]="S_TAXORGSHT";
        columnNames[8]="TS_SYSUPDATE";
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

        if (obj == null || !(obj instanceof TdTaxorgParamDto))
            return false;

        TdTaxorgParamDto bean = (TdTaxorgParamDto) obj;


      //compare field iseqno
      if((this.iseqno==null && bean.iseqno!=null) || (this.iseqno!=null && bean.iseqno==null))
          return false;
        else if(this.iseqno==null && bean.iseqno==null){
        }
        else{
          if(!bean.iseqno.equals(this.iseqno))
               return false;
     }
      //compare field sbookorgcode
      if((this.sbookorgcode==null && bean.sbookorgcode!=null) || (this.sbookorgcode!=null && bean.sbookorgcode==null))
          return false;
        else if(this.sbookorgcode==null && bean.sbookorgcode==null){
        }
        else{
          if(!bean.sbookorgcode.equals(this.sbookorgcode))
               return false;
     }
      //compare field ctrimflag
      if((this.ctrimflag==null && bean.ctrimflag!=null) || (this.ctrimflag!=null && bean.ctrimflag==null))
          return false;
        else if(this.ctrimflag==null && bean.ctrimflag==null){
        }
        else{
          if(!bean.ctrimflag.equals(this.ctrimflag))
               return false;
     }
      //compare field staxorgcode
      if((this.staxorgcode==null && bean.staxorgcode!=null) || (this.staxorgcode!=null && bean.staxorgcode==null))
          return false;
        else if(this.staxorgcode==null && bean.staxorgcode==null){
        }
        else{
          if(!bean.staxorgcode.equals(this.staxorgcode))
               return false;
     }
      //compare field staxorgname
      if((this.staxorgname==null && bean.staxorgname!=null) || (this.staxorgname!=null && bean.staxorgname==null))
          return false;
        else if(this.staxorgname==null && bean.staxorgname==null){
        }
        else{
          if(!bean.staxorgname.equals(this.staxorgname))
               return false;
     }
      //compare field ctaxorgprop
      if((this.ctaxorgprop==null && bean.ctaxorgprop!=null) || (this.ctaxorgprop!=null && bean.ctaxorgprop==null))
          return false;
        else if(this.ctaxorgprop==null && bean.ctaxorgprop==null){
        }
        else{
          if(!bean.ctaxorgprop.equals(this.ctaxorgprop))
               return false;
     }
      //compare field staxorgphone
      if((this.staxorgphone==null && bean.staxorgphone!=null) || (this.staxorgphone!=null && bean.staxorgphone==null))
          return false;
        else if(this.staxorgphone==null && bean.staxorgphone==null){
        }
        else{
          if(!bean.staxorgphone.equals(this.staxorgphone))
               return false;
     }
      //compare field staxorgsht
      if((this.staxorgsht==null && bean.staxorgsht!=null) || (this.staxorgsht!=null && bean.staxorgsht==null))
          return false;
        else if(this.staxorgsht==null && bean.staxorgsht==null){
        }
        else{
          if(!bean.staxorgsht.equals(this.staxorgsht))
               return false;
     }
      //compare field tssysupdate
      if((this.tssysupdate==null && bean.tssysupdate!=null) || (this.tssysupdate!=null && bean.tssysupdate==null))
          return false;
        else if(this.tssysupdate==null && bean.tssysupdate==null){
        }
        else{
          if(!bean.tssysupdate.equals(this.tssysupdate))
               return false;
     }



        return true;
    }

    /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
  
		int _hash_ = 1;
		
        if(this.iseqno!=null)
          _hash_ = _hash_ * 31+ this.iseqno.hashCode() ;
        if(this.sbookorgcode!=null)
          _hash_ = _hash_ * 31+ this.sbookorgcode.hashCode() ;
        if(this.ctrimflag!=null)
          _hash_ = _hash_ * 31+ this.ctrimflag.hashCode() ;
        if(this.staxorgcode!=null)
          _hash_ = _hash_ * 31+ this.staxorgcode.hashCode() ;
        if(this.staxorgname!=null)
          _hash_ = _hash_ * 31+ this.staxorgname.hashCode() ;
        if(this.ctaxorgprop!=null)
          _hash_ = _hash_ * 31+ this.ctaxorgprop.hashCode() ;
        if(this.staxorgphone!=null)
          _hash_ = _hash_ * 31+ this.staxorgphone.hashCode() ;
        if(this.staxorgsht!=null)
          _hash_ = _hash_ * 31+ this.staxorgsht.hashCode() ;
        if(this.tssysupdate!=null)
          _hash_ = _hash_ * 31+ this.tssysupdate.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TdTaxorgParamDto bean = new TdTaxorgParamDto();

          bean.iseqno = this.iseqno;

          if (this.sbookorgcode != null)
            bean.sbookorgcode = new String(this.sbookorgcode);
          if (this.ctrimflag != null)
            bean.ctrimflag = new String(this.ctrimflag);
          if (this.staxorgcode != null)
            bean.staxorgcode = new String(this.staxorgcode);
          if (this.staxorgname != null)
            bean.staxorgname = new String(this.staxorgname);
          if (this.ctaxorgprop != null)
            bean.ctaxorgprop = new String(this.ctaxorgprop);
          if (this.staxorgphone != null)
            bean.staxorgphone = new String(this.staxorgphone);
          if (this.staxorgsht != null)
            bean.staxorgsht = new String(this.staxorgsht);
          if (this.tssysupdate != null)
            bean.tssysupdate = (Timestamp) this.tssysupdate.clone();
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TdTaxorgParamDto").append(sep);
        sb.append("[iseqno]").append(" = ").append(iseqno).append(sep);
        sb.append("[sbookorgcode]").append(" = ").append(sbookorgcode).append(sep);
        sb.append("[ctrimflag]").append(" = ").append(ctrimflag).append(sep);
        sb.append("[staxorgcode]").append(" = ").append(staxorgcode).append(sep);
        sb.append("[staxorgname]").append(" = ").append(staxorgname).append(sep);
        sb.append("[ctaxorgprop]").append(" = ").append(ctaxorgprop).append(sep);
        sb.append("[staxorgphone]").append(" = ").append(staxorgphone).append(sep);
        sb.append("[staxorgsht]").append(" = ").append(staxorgsht).append(sep);
        sb.append("[tssysupdate]").append(" = ").append(tssysupdate).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //don't need check field I_SEQNO,it is generated column
  
    //check field S_BOOKORGCODE
      if (this.getSbookorgcode()==null)
             sb.append("S_BOOKORGCODE不能为空; ");
      if (this.getSbookorgcode()!=null)
             if (this.getSbookorgcode().getBytes().length > 12)
                sb.append("S_BOOKORGCODE宽度不能超过 "+12+"个字符; ");
    
    //check field C_TRIMFLAG
      if (this.getCtrimflag()==null)
             sb.append("C_TRIMFLAG不能为空; ");
      if (this.getCtrimflag()!=null)
             if (this.getCtrimflag().getBytes().length > 1)
                sb.append("C_TRIMFLAG宽度不能超过 "+1+"个字符; ");
    
    //check field S_TAXORGCODE
      if (this.getStaxorgcode()==null)
             sb.append("S_TAXORGCODE不能为空; ");
      if (this.getStaxorgcode()!=null)
             if (this.getStaxorgcode().getBytes().length > 20)
                sb.append("S_TAXORGCODE宽度不能超过 "+20+"个字符; ");
    
    //check field S_TAXORGNAME
      if (this.getStaxorgname()==null)
             sb.append("S_TAXORGNAME不能为空; ");
      if (this.getStaxorgname()!=null)
             if (this.getStaxorgname().getBytes().length > 60)
                sb.append("S_TAXORGNAME宽度不能超过 "+60+"个字符; ");
    
    //check field C_TAXORGPROP
      if (this.getCtaxorgprop()!=null)
             if (this.getCtaxorgprop().getBytes().length > 1)
                sb.append("C_TAXORGPROP宽度不能超过 "+1+"个字符; ");
    
    //check field S_TAXORGPHONE
      if (this.getStaxorgphone()!=null)
             if (this.getStaxorgphone().getBytes().length > 13)
                sb.append("S_TAXORGPHONE宽度不能超过 "+13+"个字符; ");
    
    //check field S_TAXORGSHT
      if (this.getStaxorgsht()!=null)
             if (this.getStaxorgsht().getBytes().length > 4)
                sb.append("S_TAXORGSHT宽度不能超过 "+4+"个字符; ");
    
    //don't need check field TS_SYSUPDATE,it is UpdateTimeStamp column
  

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
    //don't need check field I_SEQNO,it is generated column
  
    //check field S_BOOKORGCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BOOKORGCODE")) {
               if (this.getSbookorgcode()==null)
                    sb.append("S_BOOKORGCODE 不能为空; ");
               if (this.getSbookorgcode()!=null)
                    if (this.getSbookorgcode().getBytes().length > 12)
                        sb.append("S_BOOKORGCODE 宽度不能超过 "+12+"个字符");
             break;
         }
  }
    
    //check field C_TRIMFLAG
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("C_TRIMFLAG")) {
               if (this.getCtrimflag()==null)
                    sb.append("C_TRIMFLAG 不能为空; ");
               if (this.getCtrimflag()!=null)
                    if (this.getCtrimflag().getBytes().length > 1)
                        sb.append("C_TRIMFLAG 宽度不能超过 "+1+"个字符");
             break;
         }
  }
    
    //check field S_TAXORGCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXORGCODE")) {
               if (this.getStaxorgcode()==null)
                    sb.append("S_TAXORGCODE 不能为空; ");
               if (this.getStaxorgcode()!=null)
                    if (this.getStaxorgcode().getBytes().length > 20)
                        sb.append("S_TAXORGCODE 宽度不能超过 "+20+"个字符");
             break;
         }
  }
    
    //check field S_TAXORGNAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXORGNAME")) {
               if (this.getStaxorgname()==null)
                    sb.append("S_TAXORGNAME 不能为空; ");
               if (this.getStaxorgname()!=null)
                    if (this.getStaxorgname().getBytes().length > 60)
                        sb.append("S_TAXORGNAME 宽度不能超过 "+60+"个字符");
             break;
         }
  }
    
    //check field C_TAXORGPROP
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("C_TAXORGPROP")) {
                 if (this.getCtaxorgprop()!=null)
                    if (this.getCtaxorgprop().getBytes().length > 1)
                        sb.append("C_TAXORGPROP 宽度不能超过 "+1+"个字符");
             break;
         }
  }
    
    //check field S_TAXORGPHONE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXORGPHONE")) {
                 if (this.getStaxorgphone()!=null)
                    if (this.getStaxorgphone().getBytes().length > 13)
                        sb.append("S_TAXORGPHONE 宽度不能超过 "+13+"个字符");
             break;
         }
  }
    
    //check field S_TAXORGSHT
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXORGSHT")) {
                 if (this.getStaxorgsht()!=null)
                    if (this.getStaxorgsht().getBytes().length > 4)
                        sb.append("S_TAXORGSHT 宽度不能超过 "+4+"个字符");
             break;
         }
  }
    
    //don't need check field TS_SYSUPDATE,it is UpdateTimeStamp column
  
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
			return "输入字段个数多于表中字段个数; ";
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
				sb.append("输入字段 " + _columnNames[i] + " 不在该表字段中; ");
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
     throw new RuntimeException("此dto没有相关联的子dto，不能进行此操作");
  };
  
  /* return the IPK class  */
    public IPK      getPK(){
      TdTaxorgParamPK pk = new TdTaxorgParamPK();
      pk.setIseqno(getIseqno());
      return pk;
    };
}

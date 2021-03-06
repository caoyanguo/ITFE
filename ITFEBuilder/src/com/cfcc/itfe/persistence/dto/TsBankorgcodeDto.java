    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TsBankorgcodePK;
/**
 * <p>Title: DTO of table: TS_BANKORGCODE</p>
 * <p>Description:  Data Transfer Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:59 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  dto.vm version timestamp: 2008-01-08 16:30:00 
 *
 * @author win7
 */

public class TsBankorgcodeDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  S_ORGCODE CHARACTER , PK   , NOT NULL       */
    protected String sorgcode;
    /**
    *  S_BANKNO CHARACTER , PK   , NOT NULL       */
    protected String sbankno;
    /**
    *  S_BANKNAME VARCHAR         */
    protected String sbankname;
    /**
    *  S_BANKORGCODE VARCHAR   , NOT NULL       */
    protected String sbankorgcode;


/******************************************************
*
*  getter and setter
*
*******************************************************/


    public String getSorgcode()
    {
        return sorgcode;
    }
     /**
     *   Setter S_ORGCODE, PK , NOT NULL        * */
    public void setSorgcode(String _sorgcode) {
        this.sorgcode = _sorgcode;
    }


    public String getSbankno()
    {
        return sbankno;
    }
     /**
     *   Setter S_BANKNO, PK , NOT NULL        * */
    public void setSbankno(String _sbankno) {
        this.sbankno = _sbankno;
    }


    public String getSbankname()
    {
        return sbankname;
    }
     /**
     *   Setter S_BANKNAME        * */
    public void setSbankname(String _sbankname) {
        this.sbankname = _sbankname;
    }


    public String getSbankorgcode()
    {
        return sbankorgcode;
    }
     /**
     *   Setter S_BANKORGCODE , NOT NULL        * */
    public void setSbankorgcode(String _sbankorgcode) {
        this.sbankorgcode = _sbankorgcode;
    }




/******************************************************
*
*  Get Column Name
*
*******************************************************/
    /**
    *   Getter S_ORGCODE, PK , NOT NULL       * */
    public static String  columnSorgcode()
    {
        return "S_ORGCODE";
    }
   
    /**
    *   Getter S_BANKNO, PK , NOT NULL       * */
    public static String  columnSbankno()
    {
        return "S_BANKNO";
    }
   
    /**
    *   Getter S_BANKNAME       * */
    public static String  columnSbankname()
    {
        return "S_BANKNAME";
    }
   
    /**
    *   Getter S_BANKORGCODE , NOT NULL       * */
    public static String  columnSbankorgcode()
    {
        return "S_BANKORGCODE";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "TS_BANKORGCODE";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[4];        
        columnNames[0]="S_ORGCODE";
        columnNames[1]="S_BANKNO";
        columnNames[2]="S_BANKNAME";
        columnNames[3]="S_BANKORGCODE";
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

        if (obj == null || !(obj instanceof TsBankorgcodeDto))
            return false;

        TsBankorgcodeDto bean = (TsBankorgcodeDto) obj;


      //compare field sorgcode
      if((this.sorgcode==null && bean.sorgcode!=null) || (this.sorgcode!=null && bean.sorgcode==null))
          return false;
        else if(this.sorgcode==null && bean.sorgcode==null){
        }
        else{
          if(!bean.sorgcode.equals(this.sorgcode))
               return false;
     }
      //compare field sbankno
      if((this.sbankno==null && bean.sbankno!=null) || (this.sbankno!=null && bean.sbankno==null))
          return false;
        else if(this.sbankno==null && bean.sbankno==null){
        }
        else{
          if(!bean.sbankno.equals(this.sbankno))
               return false;
     }
      //compare field sbankname
      if((this.sbankname==null && bean.sbankname!=null) || (this.sbankname!=null && bean.sbankname==null))
          return false;
        else if(this.sbankname==null && bean.sbankname==null){
        }
        else{
          if(!bean.sbankname.equals(this.sbankname))
               return false;
     }
      //compare field sbankorgcode
      if((this.sbankorgcode==null && bean.sbankorgcode!=null) || (this.sbankorgcode!=null && bean.sbankorgcode==null))
          return false;
        else if(this.sbankorgcode==null && bean.sbankorgcode==null){
        }
        else{
          if(!bean.sbankorgcode.equals(this.sbankorgcode))
               return false;
     }



        return true;
    }

    /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
  
		int _hash_ = 1;
		
        if(this.sorgcode!=null)
          _hash_ = _hash_ * 31+ this.sorgcode.hashCode() ;
        if(this.sbankno!=null)
          _hash_ = _hash_ * 31+ this.sbankno.hashCode() ;
        if(this.sbankname!=null)
          _hash_ = _hash_ * 31+ this.sbankname.hashCode() ;
        if(this.sbankorgcode!=null)
          _hash_ = _hash_ * 31+ this.sbankorgcode.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsBankorgcodeDto bean = new TsBankorgcodeDto();

          bean.sorgcode = this.sorgcode;

          bean.sbankno = this.sbankno;

          if (this.sbankname != null)
            bean.sbankname = new String(this.sbankname);
          if (this.sbankorgcode != null)
            bean.sbankorgcode = new String(this.sbankorgcode);
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsBankorgcodeDto").append(sep);
        sb.append("[sorgcode]").append(" = ").append(sorgcode).append(sep);
        sb.append("[sbankno]").append(" = ").append(sbankno).append(sep);
        sb.append("[sbankname]").append(" = ").append(sbankname).append(sep);
        sb.append("[sbankorgcode]").append(" = ").append(sbankorgcode).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //check field S_ORGCODE
      if (this.getSorgcode()==null)
             sb.append("S_ORGCODE不能为空; ");
      if (this.getSorgcode()!=null)
             if (this.getSorgcode().getBytes().length > 12)
                sb.append("S_ORGCODE宽度不能超过 "+12+"个字符; ");
    
    //check field S_BANKNO
      if (this.getSbankno()==null)
             sb.append("S_BANKNO不能为空; ");
      if (this.getSbankno()!=null)
             if (this.getSbankno().getBytes().length > 12)
                sb.append("S_BANKNO宽度不能超过 "+12+"个字符; ");
    
    //check field S_BANKNAME
      if (this.getSbankname()!=null)
             if (this.getSbankname().getBytes().length > 60)
                sb.append("S_BANKNAME宽度不能超过 "+60+"个字符; ");
    
    //check field S_BANKORGCODE
      if (this.getSbankorgcode()==null)
             sb.append("S_BANKORGCODE不能为空; ");
      if (this.getSbankorgcode()!=null)
             if (this.getSbankorgcode().getBytes().length > 10)
                sb.append("S_BANKORGCODE宽度不能超过 "+10+"个字符; ");
    

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
    //check field S_ORGCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_ORGCODE")) {
               if (this.getSorgcode()==null)
                    sb.append("S_ORGCODE 不能为空; ");
               if (this.getSorgcode()!=null)
                    if (this.getSorgcode().getBytes().length > 12)
                        sb.append("S_ORGCODE 宽度不能超过 "+12+"个字符");
             break;
         }
  }
    
    //check field S_BANKNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BANKNO")) {
               if (this.getSbankno()==null)
                    sb.append("S_BANKNO 不能为空; ");
               if (this.getSbankno()!=null)
                    if (this.getSbankno().getBytes().length > 12)
                        sb.append("S_BANKNO 宽度不能超过 "+12+"个字符");
             break;
         }
  }
    
    //check field S_BANKNAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BANKNAME")) {
                 if (this.getSbankname()!=null)
                    if (this.getSbankname().getBytes().length > 60)
                        sb.append("S_BANKNAME 宽度不能超过 "+60+"个字符");
             break;
         }
  }
    
    //check field S_BANKORGCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BANKORGCODE")) {
               if (this.getSbankorgcode()==null)
                    sb.append("S_BANKORGCODE 不能为空; ");
               if (this.getSbankorgcode()!=null)
                    if (this.getSbankorgcode().getBytes().length > 10)
                        sb.append("S_BANKORGCODE 宽度不能超过 "+10+"个字符");
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
      TsBankorgcodePK pk = new TsBankorgcodePK();
      pk.setSorgcode(getSorgcode());
      pk.setSbankno(getSbankno());
      return pk;
    };
}

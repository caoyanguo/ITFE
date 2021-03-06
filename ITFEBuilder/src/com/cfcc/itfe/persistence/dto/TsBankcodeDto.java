    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TsBankcodePK;
/**
 * <p>Title: DTO of table: TS_BANKCODE</p>
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

public class TsBankcodeDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  S_BNKCODE VARCHAR , PK   , NOT NULL       */
    protected String sbnkcode;
    /**
    *  S_ACCTSTATUS CHARACTER , PK   , NOT NULL       */
    protected String sacctstatus;
    /**
    *  S_BNKNAME VARCHAR   , NOT NULL       */
    protected String sbnkname;


/******************************************************
*
*  getter and setter
*
*******************************************************/


    public String getSbnkcode()
    {
        return sbnkcode;
    }
     /**
     *   Setter S_BNKCODE, PK , NOT NULL        * */
    public void setSbnkcode(String _sbnkcode) {
        this.sbnkcode = _sbnkcode;
    }


    public String getSacctstatus()
    {
        return sacctstatus;
    }
     /**
     *   Setter S_ACCTSTATUS, PK , NOT NULL        * */
    public void setSacctstatus(String _sacctstatus) {
        this.sacctstatus = _sacctstatus;
    }


    public String getSbnkname()
    {
        return sbnkname;
    }
     /**
     *   Setter S_BNKNAME , NOT NULL        * */
    public void setSbnkname(String _sbnkname) {
        this.sbnkname = _sbnkname;
    }




/******************************************************
*
*  Get Column Name
*
*******************************************************/
    /**
    *   Getter S_BNKCODE, PK , NOT NULL       * */
    public static String  columnSbnkcode()
    {
        return "S_BNKCODE";
    }
   
    /**
    *   Getter S_ACCTSTATUS, PK , NOT NULL       * */
    public static String  columnSacctstatus()
    {
        return "S_ACCTSTATUS";
    }
   
    /**
    *   Getter S_BNKNAME , NOT NULL       * */
    public static String  columnSbnkname()
    {
        return "S_BNKNAME";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "TS_BANKCODE";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[3];        
        columnNames[0]="S_BNKCODE";
        columnNames[1]="S_ACCTSTATUS";
        columnNames[2]="S_BNKNAME";
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

        if (obj == null || !(obj instanceof TsBankcodeDto))
            return false;

        TsBankcodeDto bean = (TsBankcodeDto) obj;


      //compare field sbnkcode
      if((this.sbnkcode==null && bean.sbnkcode!=null) || (this.sbnkcode!=null && bean.sbnkcode==null))
          return false;
        else if(this.sbnkcode==null && bean.sbnkcode==null){
        }
        else{
          if(!bean.sbnkcode.equals(this.sbnkcode))
               return false;
     }
      //compare field sacctstatus
      if((this.sacctstatus==null && bean.sacctstatus!=null) || (this.sacctstatus!=null && bean.sacctstatus==null))
          return false;
        else if(this.sacctstatus==null && bean.sacctstatus==null){
        }
        else{
          if(!bean.sacctstatus.equals(this.sacctstatus))
               return false;
     }
      //compare field sbnkname
      if((this.sbnkname==null && bean.sbnkname!=null) || (this.sbnkname!=null && bean.sbnkname==null))
          return false;
        else if(this.sbnkname==null && bean.sbnkname==null){
        }
        else{
          if(!bean.sbnkname.equals(this.sbnkname))
               return false;
     }



        return true;
    }

    /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
  
		int _hash_ = 1;
		
        if(this.sbnkcode!=null)
          _hash_ = _hash_ * 31+ this.sbnkcode.hashCode() ;
        if(this.sacctstatus!=null)
          _hash_ = _hash_ * 31+ this.sacctstatus.hashCode() ;
        if(this.sbnkname!=null)
          _hash_ = _hash_ * 31+ this.sbnkname.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsBankcodeDto bean = new TsBankcodeDto();

          bean.sbnkcode = this.sbnkcode;

          bean.sacctstatus = this.sacctstatus;

          if (this.sbnkname != null)
            bean.sbnkname = new String(this.sbnkname);
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsBankcodeDto").append(sep);
        sb.append("[sbnkcode]").append(" = ").append(sbnkcode).append(sep);
        sb.append("[sacctstatus]").append(" = ").append(sacctstatus).append(sep);
        sb.append("[sbnkname]").append(" = ").append(sbnkname).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //check field S_BNKCODE
      if (this.getSbnkcode()==null)
             sb.append("S_BNKCODE不能为空; ");
      if (this.getSbnkcode()!=null)
             if (this.getSbnkcode().getBytes().length > 12)
                sb.append("S_BNKCODE宽度不能超过 "+12+"个字符; ");
    
    //check field S_ACCTSTATUS
      if (this.getSacctstatus()==null)
             sb.append("S_ACCTSTATUS不能为空; ");
      if (this.getSacctstatus()!=null)
             if (this.getSacctstatus().getBytes().length > 1)
                sb.append("S_ACCTSTATUS宽度不能超过 "+1+"个字符; ");
    
    //check field S_BNKNAME
      if (this.getSbnkname()==null)
             sb.append("S_BNKNAME不能为空; ");
      if (this.getSbnkname()!=null)
             if (this.getSbnkname().getBytes().length > 100)
                sb.append("S_BNKNAME宽度不能超过 "+100+"个字符; ");
    

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
    //check field S_BNKCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BNKCODE")) {
               if (this.getSbnkcode()==null)
                    sb.append("S_BNKCODE 不能为空; ");
               if (this.getSbnkcode()!=null)
                    if (this.getSbnkcode().getBytes().length > 12)
                        sb.append("S_BNKCODE 宽度不能超过 "+12+"个字符");
             break;
         }
  }
    
    //check field S_ACCTSTATUS
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_ACCTSTATUS")) {
               if (this.getSacctstatus()==null)
                    sb.append("S_ACCTSTATUS 不能为空; ");
               if (this.getSacctstatus()!=null)
                    if (this.getSacctstatus().getBytes().length > 1)
                        sb.append("S_ACCTSTATUS 宽度不能超过 "+1+"个字符");
             break;
         }
  }
    
    //check field S_BNKNAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BNKNAME")) {
               if (this.getSbnkname()==null)
                    sb.append("S_BNKNAME 不能为空; ");
               if (this.getSbnkname()!=null)
                    if (this.getSbnkname().getBytes().length > 100)
                        sb.append("S_BNKNAME 宽度不能超过 "+100+"个字符");
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
      TsBankcodePK pk = new TsBankcodePK();
      pk.setSbnkcode(getSbnkcode());
      pk.setSacctstatus(getSacctstatus());
      return pk;
    };
}

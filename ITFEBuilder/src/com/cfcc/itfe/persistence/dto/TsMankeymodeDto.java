    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TsMankeymodePK;
/**
 * <p>Title: DTO of table: TS_MANKEYMODE</p>
 * <p>Description:  Data Transfer Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:00 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  dto.vm version timestamp: 2008-01-08 16:30:00 
 *
 * @author win7
 */

public class TsMankeymodeDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  S_KEYMODE CHARACTER , PK   , NOT NULL       */
    protected String skeymode;


/******************************************************
*
*  getter and setter
*
*******************************************************/


    public String getSkeymode()
    {
        return skeymode;
    }
     /**
     *   Setter S_KEYMODE, PK , NOT NULL        * */
    public void setSkeymode(String _skeymode) {
        this.skeymode = _skeymode;
    }




/******************************************************
*
*  Get Column Name
*
*******************************************************/
    /**
    *   Getter S_KEYMODE, PK , NOT NULL       * */
    public static String  columnSkeymode()
    {
        return "S_KEYMODE";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "TS_MANKEYMODE";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[1];        
        columnNames[0]="S_KEYMODE";
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

        if (obj == null || !(obj instanceof TsMankeymodeDto))
            return false;

        TsMankeymodeDto bean = (TsMankeymodeDto) obj;


      //compare field skeymode
      if((this.skeymode==null && bean.skeymode!=null) || (this.skeymode!=null && bean.skeymode==null))
          return false;
        else if(this.skeymode==null && bean.skeymode==null){
        }
        else{
          if(!bean.skeymode.equals(this.skeymode))
               return false;
     }



        return true;
    }

    /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
  
		int _hash_ = 1;
		
        if(this.skeymode!=null)
          _hash_ = _hash_ * 31+ this.skeymode.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsMankeymodeDto bean = new TsMankeymodeDto();

          bean.skeymode = this.skeymode;

  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsMankeymodeDto").append(sep);
        sb.append("[skeymode]").append(" = ").append(skeymode).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //check field S_KEYMODE
      if (this.getSkeymode()==null)
             sb.append("S_KEYMODE不能为空; ");
      if (this.getSkeymode()!=null)
             if (this.getSkeymode().getBytes().length > 1)
                sb.append("S_KEYMODE宽度不能超过 "+1+"个字符; ");
    

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
    //check field S_KEYMODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_KEYMODE")) {
               if (this.getSkeymode()==null)
                    sb.append("S_KEYMODE 不能为空; ");
               if (this.getSkeymode()!=null)
                    if (this.getSkeymode().getBytes().length > 1)
                        sb.append("S_KEYMODE 宽度不能超过 "+1+"个字符");
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
      TsMankeymodePK pk = new TsMankeymodePK();
      pk.setSkeymode(getSkeymode());
      return pk;
    };
}

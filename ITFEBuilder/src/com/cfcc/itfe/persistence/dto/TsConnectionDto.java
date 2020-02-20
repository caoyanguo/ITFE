    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TsConnectionPK;
/**
 * <p>Title: DTO of table: TS_CONNECTION</p>
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

public class TsConnectionDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  S_CONNORGCODEA VARCHAR , PK   , NOT NULL       */
    protected String sconnorgcodea;
    /**
    *  S_CONNORGCODEB VARCHAR , PK   , NOT NULL       */
    protected String sconnorgcodeb;
    /**
    *  I_MODICOUNT INTEGER         */
    protected Integer imodicount;


/******************************************************
*
*  getter and setter
*
*******************************************************/


    public String getSconnorgcodea()
    {
        return sconnorgcodea;
    }
     /**
     *   Setter S_CONNORGCODEA, PK , NOT NULL        * */
    public void setSconnorgcodea(String _sconnorgcodea) {
        this.sconnorgcodea = _sconnorgcodea;
    }


    public String getSconnorgcodeb()
    {
        return sconnorgcodeb;
    }
     /**
     *   Setter S_CONNORGCODEB, PK , NOT NULL        * */
    public void setSconnorgcodeb(String _sconnorgcodeb) {
        this.sconnorgcodeb = _sconnorgcodeb;
    }


    public Integer getImodicount()
    {
        return imodicount;
    }
     /**
     *   Setter I_MODICOUNT        * */
    public void setImodicount(Integer _imodicount) {
        this.imodicount = _imodicount;
    }




/******************************************************
*
*  Get Column Name
*
*******************************************************/
    /**
    *   Getter S_CONNORGCODEA, PK , NOT NULL       * */
    public static String  columnSconnorgcodea()
    {
        return "S_CONNORGCODEA";
    }
   
    /**
    *   Getter S_CONNORGCODEB, PK , NOT NULL       * */
    public static String  columnSconnorgcodeb()
    {
        return "S_CONNORGCODEB";
    }
   
    /**
    *   Getter I_MODICOUNT       * */
    public static String  columnImodicount()
    {
        return "I_MODICOUNT";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "TS_CONNECTION";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[3];        
        columnNames[0]="S_CONNORGCODEA";
        columnNames[1]="S_CONNORGCODEB";
        columnNames[2]="I_MODICOUNT";
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

        if (obj == null || !(obj instanceof TsConnectionDto))
            return false;

        TsConnectionDto bean = (TsConnectionDto) obj;


      //compare field sconnorgcodea
      if((this.sconnorgcodea==null && bean.sconnorgcodea!=null) || (this.sconnorgcodea!=null && bean.sconnorgcodea==null))
          return false;
        else if(this.sconnorgcodea==null && bean.sconnorgcodea==null){
        }
        else{
          if(!bean.sconnorgcodea.equals(this.sconnorgcodea))
               return false;
     }
      //compare field sconnorgcodeb
      if((this.sconnorgcodeb==null && bean.sconnorgcodeb!=null) || (this.sconnorgcodeb!=null && bean.sconnorgcodeb==null))
          return false;
        else if(this.sconnorgcodeb==null && bean.sconnorgcodeb==null){
        }
        else{
          if(!bean.sconnorgcodeb.equals(this.sconnorgcodeb))
               return false;
     }
      //compare field imodicount
      if((this.imodicount==null && bean.imodicount!=null) || (this.imodicount!=null && bean.imodicount==null))
          return false;
        else if(this.imodicount==null && bean.imodicount==null){
        }
        else{
          if(!bean.imodicount.equals(this.imodicount))
               return false;
     }



        return true;
    }

    /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
  
		int _hash_ = 1;
		
        if(this.sconnorgcodea!=null)
          _hash_ = _hash_ * 31+ this.sconnorgcodea.hashCode() ;
        if(this.sconnorgcodeb!=null)
          _hash_ = _hash_ * 31+ this.sconnorgcodeb.hashCode() ;
        if(this.imodicount!=null)
          _hash_ = _hash_ * 31+ this.imodicount.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsConnectionDto bean = new TsConnectionDto();

          bean.sconnorgcodea = this.sconnorgcodea;

          bean.sconnorgcodeb = this.sconnorgcodeb;

          if (this.imodicount != null)
            bean.imodicount = new Integer(this.imodicount.toString());
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsConnectionDto").append(sep);
        sb.append("[sconnorgcodea]").append(" = ").append(sconnorgcodea).append(sep);
        sb.append("[sconnorgcodeb]").append(" = ").append(sconnorgcodeb).append(sep);
        sb.append("[imodicount]").append(" = ").append(imodicount).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //check field S_CONNORGCODEA
      if (this.getSconnorgcodea()==null)
             sb.append("S_CONNORGCODEA不能为空; ");
      if (this.getSconnorgcodea()!=null)
             if (this.getSconnorgcodea().getBytes().length > 12)
                sb.append("S_CONNORGCODEA宽度不能超过 "+12+"个字符; ");
    
    //check field S_CONNORGCODEB
      if (this.getSconnorgcodeb()==null)
             sb.append("S_CONNORGCODEB不能为空; ");
      if (this.getSconnorgcodeb()!=null)
             if (this.getSconnorgcodeb().getBytes().length > 12)
                sb.append("S_CONNORGCODEB宽度不能超过 "+12+"个字符; ");
    
    //check field I_MODICOUNT
      

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
    //check field S_CONNORGCODEA
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_CONNORGCODEA")) {
               if (this.getSconnorgcodea()==null)
                    sb.append("S_CONNORGCODEA 不能为空; ");
               if (this.getSconnorgcodea()!=null)
                    if (this.getSconnorgcodea().getBytes().length > 12)
                        sb.append("S_CONNORGCODEA 宽度不能超过 "+12+"个字符");
             break;
         }
  }
    
    //check field S_CONNORGCODEB
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_CONNORGCODEB")) {
               if (this.getSconnorgcodeb()==null)
                    sb.append("S_CONNORGCODEB 不能为空; ");
               if (this.getSconnorgcodeb()!=null)
                    if (this.getSconnorgcodeb().getBytes().length > 12)
                        sb.append("S_CONNORGCODEB 宽度不能超过 "+12+"个字符");
             break;
         }
  }
    
    //check field I_MODICOUNT
          
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
      TsConnectionPK pk = new TsConnectionPK();
      pk.setSconnorgcodea(getSconnorgcodea());
      pk.setSconnorgcodeb(getSconnorgcodeb());
      return pk;
    };
}

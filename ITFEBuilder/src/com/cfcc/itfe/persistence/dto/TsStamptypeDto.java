    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TsStamptypePK;
/**
 * <p>Title: DTO of table: TS_STAMPTYPE</p>
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

public class TsStamptypeDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  S_STAMPTYPECODE VARCHAR , PK   , NOT NULL       */
    protected String sstamptypecode;
    /**
    *  S_STAMPTYPENAME VARCHAR   , NOT NULL       */
    protected String sstamptypename;
    /**
    *  S_STAMPTYPEID VARCHAR   , NOT NULL       */
    protected String sstamptypeid;
    /**
    *  I_MODICOUNT INTEGER         */
    protected Integer imodicount;


/******************************************************
*
*  getter and setter
*
*******************************************************/


    public String getSstamptypecode()
    {
        return sstamptypecode;
    }
     /**
     *   Setter S_STAMPTYPECODE, PK , NOT NULL        * */
    public void setSstamptypecode(String _sstamptypecode) {
        this.sstamptypecode = _sstamptypecode;
    }


    public String getSstamptypename()
    {
        return sstamptypename;
    }
     /**
     *   Setter S_STAMPTYPENAME , NOT NULL        * */
    public void setSstamptypename(String _sstamptypename) {
        this.sstamptypename = _sstamptypename;
    }


    public String getSstamptypeid()
    {
        return sstamptypeid;
    }
     /**
     *   Setter S_STAMPTYPEID , NOT NULL        * */
    public void setSstamptypeid(String _sstamptypeid) {
        this.sstamptypeid = _sstamptypeid;
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
    *   Getter S_STAMPTYPECODE, PK , NOT NULL       * */
    public static String  columnSstamptypecode()
    {
        return "S_STAMPTYPECODE";
    }
   
    /**
    *   Getter S_STAMPTYPENAME , NOT NULL       * */
    public static String  columnSstamptypename()
    {
        return "S_STAMPTYPENAME";
    }
   
    /**
    *   Getter S_STAMPTYPEID , NOT NULL       * */
    public static String  columnSstamptypeid()
    {
        return "S_STAMPTYPEID";
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
        return "TS_STAMPTYPE";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[4];        
        columnNames[0]="S_STAMPTYPECODE";
        columnNames[1]="S_STAMPTYPENAME";
        columnNames[2]="S_STAMPTYPEID";
        columnNames[3]="I_MODICOUNT";
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

        if (obj == null || !(obj instanceof TsStamptypeDto))
            return false;

        TsStamptypeDto bean = (TsStamptypeDto) obj;


      //compare field sstamptypecode
      if((this.sstamptypecode==null && bean.sstamptypecode!=null) || (this.sstamptypecode!=null && bean.sstamptypecode==null))
          return false;
        else if(this.sstamptypecode==null && bean.sstamptypecode==null){
        }
        else{
          if(!bean.sstamptypecode.equals(this.sstamptypecode))
               return false;
     }
      //compare field sstamptypename
      if((this.sstamptypename==null && bean.sstamptypename!=null) || (this.sstamptypename!=null && bean.sstamptypename==null))
          return false;
        else if(this.sstamptypename==null && bean.sstamptypename==null){
        }
        else{
          if(!bean.sstamptypename.equals(this.sstamptypename))
               return false;
     }
      //compare field sstamptypeid
      if((this.sstamptypeid==null && bean.sstamptypeid!=null) || (this.sstamptypeid!=null && bean.sstamptypeid==null))
          return false;
        else if(this.sstamptypeid==null && bean.sstamptypeid==null){
        }
        else{
          if(!bean.sstamptypeid.equals(this.sstamptypeid))
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
		
        if(this.sstamptypecode!=null)
          _hash_ = _hash_ * 31+ this.sstamptypecode.hashCode() ;
        if(this.sstamptypename!=null)
          _hash_ = _hash_ * 31+ this.sstamptypename.hashCode() ;
        if(this.sstamptypeid!=null)
          _hash_ = _hash_ * 31+ this.sstamptypeid.hashCode() ;
        if(this.imodicount!=null)
          _hash_ = _hash_ * 31+ this.imodicount.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsStamptypeDto bean = new TsStamptypeDto();

          bean.sstamptypecode = this.sstamptypecode;

          if (this.sstamptypename != null)
            bean.sstamptypename = new String(this.sstamptypename);
          if (this.sstamptypeid != null)
            bean.sstamptypeid = new String(this.sstamptypeid);
          if (this.imodicount != null)
            bean.imodicount = new Integer(this.imodicount.toString());
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsStamptypeDto").append(sep);
        sb.append("[sstamptypecode]").append(" = ").append(sstamptypecode).append(sep);
        sb.append("[sstamptypename]").append(" = ").append(sstamptypename).append(sep);
        sb.append("[sstamptypeid]").append(" = ").append(sstamptypeid).append(sep);
        sb.append("[imodicount]").append(" = ").append(imodicount).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //check field S_STAMPTYPECODE
      if (this.getSstamptypecode()==null)
             sb.append("S_STAMPTYPECODE不能为空; ");
      if (this.getSstamptypecode()!=null)
             if (this.getSstamptypecode().getBytes().length > 10)
                sb.append("S_STAMPTYPECODE宽度不能超过 "+10+"个字符; ");
    
    //check field S_STAMPTYPENAME
      if (this.getSstamptypename()==null)
             sb.append("S_STAMPTYPENAME不能为空; ");
      if (this.getSstamptypename()!=null)
             if (this.getSstamptypename().getBytes().length > 50)
                sb.append("S_STAMPTYPENAME宽度不能超过 "+50+"个字符; ");
    
    //check field S_STAMPTYPEID
      if (this.getSstamptypeid()==null)
             sb.append("S_STAMPTYPEID不能为空; ");
      if (this.getSstamptypeid()!=null)
             if (this.getSstamptypeid().getBytes().length > 30)
                sb.append("S_STAMPTYPEID宽度不能超过 "+30+"个字符; ");
    
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
    //check field S_STAMPTYPECODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_STAMPTYPECODE")) {
               if (this.getSstamptypecode()==null)
                    sb.append("S_STAMPTYPECODE 不能为空; ");
               if (this.getSstamptypecode()!=null)
                    if (this.getSstamptypecode().getBytes().length > 10)
                        sb.append("S_STAMPTYPECODE 宽度不能超过 "+10+"个字符");
             break;
         }
  }
    
    //check field S_STAMPTYPENAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_STAMPTYPENAME")) {
               if (this.getSstamptypename()==null)
                    sb.append("S_STAMPTYPENAME 不能为空; ");
               if (this.getSstamptypename()!=null)
                    if (this.getSstamptypename().getBytes().length > 50)
                        sb.append("S_STAMPTYPENAME 宽度不能超过 "+50+"个字符");
             break;
         }
  }
    
    //check field S_STAMPTYPEID
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_STAMPTYPEID")) {
               if (this.getSstamptypeid()==null)
                    sb.append("S_STAMPTYPEID 不能为空; ");
               if (this.getSstamptypeid()!=null)
                    if (this.getSstamptypeid().getBytes().length > 30)
                        sb.append("S_STAMPTYPEID 宽度不能超过 "+30+"个字符");
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
      TsStamptypePK pk = new TsStamptypePK();
      pk.setSstamptypecode(getSstamptypecode());
      return pk;
    };
}

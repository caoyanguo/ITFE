    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TsAssitflagtransPK;
/**
 * <p>Title: DTO of table: TS_ASSITFLAGTRANS</p>
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

public class TsAssitflagtransDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  S_ORGCODE VARCHAR , PK   , NOT NULL       */
    protected String sorgcode;
    /**
    *  S_BUDGETSUBJECT VARCHAR , PK   , NOT NULL       */
    protected String sbudgetsubject;
    /**
    *  S_BUDGETLEVEL CHARACTER , PK   , NOT NULL       */
    protected String sbudgetlevel;
    /**
    *  S_TIPSASSISTSIGN VARCHAR         */
    protected String stipsassistsign;
    /**
    *  I_MODICOUNT INTEGER         */
    protected Integer imodicount;
    /**
    *  S_TRECODE CHARACTER , PK   , NOT NULL       */
    protected String strecode;


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


    public String getSbudgetsubject()
    {
        return sbudgetsubject;
    }
     /**
     *   Setter S_BUDGETSUBJECT, PK , NOT NULL        * */
    public void setSbudgetsubject(String _sbudgetsubject) {
        this.sbudgetsubject = _sbudgetsubject;
    }


    public String getSbudgetlevel()
    {
        return sbudgetlevel;
    }
     /**
     *   Setter S_BUDGETLEVEL, PK , NOT NULL        * */
    public void setSbudgetlevel(String _sbudgetlevel) {
        this.sbudgetlevel = _sbudgetlevel;
    }


    public String getStipsassistsign()
    {
        return stipsassistsign;
    }
     /**
     *   Setter S_TIPSASSISTSIGN        * */
    public void setStipsassistsign(String _stipsassistsign) {
        this.stipsassistsign = _stipsassistsign;
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


    public String getStrecode()
    {
        return strecode;
    }
     /**
     *   Setter S_TRECODE, PK , NOT NULL        * */
    public void setStrecode(String _strecode) {
        this.strecode = _strecode;
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
    *   Getter S_BUDGETSUBJECT, PK , NOT NULL       * */
    public static String  columnSbudgetsubject()
    {
        return "S_BUDGETSUBJECT";
    }
   
    /**
    *   Getter S_BUDGETLEVEL, PK , NOT NULL       * */
    public static String  columnSbudgetlevel()
    {
        return "S_BUDGETLEVEL";
    }
   
    /**
    *   Getter S_TIPSASSISTSIGN       * */
    public static String  columnStipsassistsign()
    {
        return "S_TIPSASSISTSIGN";
    }
   
    /**
    *   Getter I_MODICOUNT       * */
    public static String  columnImodicount()
    {
        return "I_MODICOUNT";
    }
   
    /**
    *   Getter S_TRECODE, PK , NOT NULL       * */
    public static String  columnStrecode()
    {
        return "S_TRECODE";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "TS_ASSITFLAGTRANS";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[6];        
        columnNames[0]="S_ORGCODE";
        columnNames[1]="S_BUDGETSUBJECT";
        columnNames[2]="S_BUDGETLEVEL";
        columnNames[3]="S_TIPSASSISTSIGN";
        columnNames[4]="I_MODICOUNT";
        columnNames[5]="S_TRECODE";
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

        if (obj == null || !(obj instanceof TsAssitflagtransDto))
            return false;

        TsAssitflagtransDto bean = (TsAssitflagtransDto) obj;


      //compare field sorgcode
      if((this.sorgcode==null && bean.sorgcode!=null) || (this.sorgcode!=null && bean.sorgcode==null))
          return false;
        else if(this.sorgcode==null && bean.sorgcode==null){
        }
        else{
          if(!bean.sorgcode.equals(this.sorgcode))
               return false;
     }
      //compare field sbudgetsubject
      if((this.sbudgetsubject==null && bean.sbudgetsubject!=null) || (this.sbudgetsubject!=null && bean.sbudgetsubject==null))
          return false;
        else if(this.sbudgetsubject==null && bean.sbudgetsubject==null){
        }
        else{
          if(!bean.sbudgetsubject.equals(this.sbudgetsubject))
               return false;
     }
      //compare field sbudgetlevel
      if((this.sbudgetlevel==null && bean.sbudgetlevel!=null) || (this.sbudgetlevel!=null && bean.sbudgetlevel==null))
          return false;
        else if(this.sbudgetlevel==null && bean.sbudgetlevel==null){
        }
        else{
          if(!bean.sbudgetlevel.equals(this.sbudgetlevel))
               return false;
     }
      //compare field stipsassistsign
      if((this.stipsassistsign==null && bean.stipsassistsign!=null) || (this.stipsassistsign!=null && bean.stipsassistsign==null))
          return false;
        else if(this.stipsassistsign==null && bean.stipsassistsign==null){
        }
        else{
          if(!bean.stipsassistsign.equals(this.stipsassistsign))
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
      //compare field strecode
      if((this.strecode==null && bean.strecode!=null) || (this.strecode!=null && bean.strecode==null))
          return false;
        else if(this.strecode==null && bean.strecode==null){
        }
        else{
          if(!bean.strecode.equals(this.strecode))
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
        if(this.sbudgetsubject!=null)
          _hash_ = _hash_ * 31+ this.sbudgetsubject.hashCode() ;
        if(this.sbudgetlevel!=null)
          _hash_ = _hash_ * 31+ this.sbudgetlevel.hashCode() ;
        if(this.stipsassistsign!=null)
          _hash_ = _hash_ * 31+ this.stipsassistsign.hashCode() ;
        if(this.imodicount!=null)
          _hash_ = _hash_ * 31+ this.imodicount.hashCode() ;
        if(this.strecode!=null)
          _hash_ = _hash_ * 31+ this.strecode.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsAssitflagtransDto bean = new TsAssitflagtransDto();

          bean.sorgcode = this.sorgcode;

          bean.sbudgetsubject = this.sbudgetsubject;

          bean.sbudgetlevel = this.sbudgetlevel;

          if (this.stipsassistsign != null)
            bean.stipsassistsign = new String(this.stipsassistsign);
          if (this.imodicount != null)
            bean.imodicount = new Integer(this.imodicount.toString());
          bean.strecode = this.strecode;

  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsAssitflagtransDto").append(sep);
        sb.append("[sorgcode]").append(" = ").append(sorgcode).append(sep);
        sb.append("[sbudgetsubject]").append(" = ").append(sbudgetsubject).append(sep);
        sb.append("[sbudgetlevel]").append(" = ").append(sbudgetlevel).append(sep);
        sb.append("[stipsassistsign]").append(" = ").append(stipsassistsign).append(sep);
        sb.append("[imodicount]").append(" = ").append(imodicount).append(sep);
        sb.append("[strecode]").append(" = ").append(strecode).append(sep);
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
    
    //check field S_BUDGETSUBJECT
      if (this.getSbudgetsubject()==null)
             sb.append("S_BUDGETSUBJECT不能为空; ");
      if (this.getSbudgetsubject()!=null)
             if (this.getSbudgetsubject().getBytes().length > 32)
                sb.append("S_BUDGETSUBJECT宽度不能超过 "+32+"个字符; ");
    
    //check field S_BUDGETLEVEL
      if (this.getSbudgetlevel()==null)
             sb.append("S_BUDGETLEVEL不能为空; ");
      if (this.getSbudgetlevel()!=null)
             if (this.getSbudgetlevel().getBytes().length > 1)
                sb.append("S_BUDGETLEVEL宽度不能超过 "+1+"个字符; ");
    
    //check field S_TIPSASSISTSIGN
      if (this.getStipsassistsign()!=null)
             if (this.getStipsassistsign().getBytes().length > 35)
                sb.append("S_TIPSASSISTSIGN宽度不能超过 "+35+"个字符; ");
    
    //check field I_MODICOUNT
      
    //check field S_TRECODE
      if (this.getStrecode()==null)
             sb.append("S_TRECODE不能为空; ");
      if (this.getStrecode()!=null)
             if (this.getStrecode().getBytes().length > 10)
                sb.append("S_TRECODE宽度不能超过 "+10+"个字符; ");
    

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
    
    //check field S_BUDGETSUBJECT
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BUDGETSUBJECT")) {
               if (this.getSbudgetsubject()==null)
                    sb.append("S_BUDGETSUBJECT 不能为空; ");
               if (this.getSbudgetsubject()!=null)
                    if (this.getSbudgetsubject().getBytes().length > 32)
                        sb.append("S_BUDGETSUBJECT 宽度不能超过 "+32+"个字符");
             break;
         }
  }
    
    //check field S_BUDGETLEVEL
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BUDGETLEVEL")) {
               if (this.getSbudgetlevel()==null)
                    sb.append("S_BUDGETLEVEL 不能为空; ");
               if (this.getSbudgetlevel()!=null)
                    if (this.getSbudgetlevel().getBytes().length > 1)
                        sb.append("S_BUDGETLEVEL 宽度不能超过 "+1+"个字符");
             break;
         }
  }
    
    //check field S_TIPSASSISTSIGN
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TIPSASSISTSIGN")) {
                 if (this.getStipsassistsign()!=null)
                    if (this.getStipsassistsign().getBytes().length > 35)
                        sb.append("S_TIPSASSISTSIGN 宽度不能超过 "+35+"个字符");
             break;
         }
  }
    
    //check field I_MODICOUNT
          
    //check field S_TRECODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TRECODE")) {
               if (this.getStrecode()==null)
                    sb.append("S_TRECODE 不能为空; ");
               if (this.getStrecode()!=null)
                    if (this.getStrecode().getBytes().length > 10)
                        sb.append("S_TRECODE 宽度不能超过 "+10+"个字符");
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
      TsAssitflagtransPK pk = new TsAssitflagtransPK();
      pk.setSorgcode(getSorgcode());
      pk.setSbudgetsubject(getSbudgetsubject());
      pk.setSbudgetlevel(getSbudgetlevel());
      pk.setStrecode(getStrecode());
      return pk;
    };
}

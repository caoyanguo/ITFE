    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TsConvertassitsignPK;
/**
 * <p>Title: DTO of table: TS_CONVERTASSITSIGN</p>
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

public class TsConvertassitsignDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  S_ORGCODE VARCHAR , PK   , NOT NULL       */
    protected String sorgcode;
    /**
    *  S_TRECODE VARCHAR , PK   , NOT NULL       */
    protected String strecode;
    /**
    *  S_TBSASSITSIGN VARCHAR , PK   , NOT NULL       */
    protected String stbsassitsign;
    /**
    *  S_BUDGETSUBCODE VARCHAR , PK   , NOT NULL       */
    protected String sbudgetsubcode;
    /**
    *  S_TIPSASSISTSIGN VARCHAR   , NOT NULL       */
    protected String stipsassistsign;
    /**
    *  I_MODICOUNT INTEGER         */
    protected Integer imodicount;


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


    public String getStrecode()
    {
        return strecode;
    }
     /**
     *   Setter S_TRECODE, PK , NOT NULL        * */
    public void setStrecode(String _strecode) {
        this.strecode = _strecode;
    }


    public String getStbsassitsign()
    {
        return stbsassitsign;
    }
     /**
     *   Setter S_TBSASSITSIGN, PK , NOT NULL        * */
    public void setStbsassitsign(String _stbsassitsign) {
        this.stbsassitsign = _stbsassitsign;
    }


    public String getSbudgetsubcode()
    {
        return sbudgetsubcode;
    }
     /**
     *   Setter S_BUDGETSUBCODE, PK , NOT NULL        * */
    public void setSbudgetsubcode(String _sbudgetsubcode) {
        this.sbudgetsubcode = _sbudgetsubcode;
    }


    public String getStipsassistsign()
    {
        return stipsassistsign;
    }
     /**
     *   Setter S_TIPSASSISTSIGN , NOT NULL        * */
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
    *   Getter S_TRECODE, PK , NOT NULL       * */
    public static String  columnStrecode()
    {
        return "S_TRECODE";
    }
   
    /**
    *   Getter S_TBSASSITSIGN, PK , NOT NULL       * */
    public static String  columnStbsassitsign()
    {
        return "S_TBSASSITSIGN";
    }
   
    /**
    *   Getter S_BUDGETSUBCODE, PK , NOT NULL       * */
    public static String  columnSbudgetsubcode()
    {
        return "S_BUDGETSUBCODE";
    }
   
    /**
    *   Getter S_TIPSASSISTSIGN , NOT NULL       * */
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
    *  Table Name
    */
    public static String tableName(){
        return "TS_CONVERTASSITSIGN";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[6];        
        columnNames[0]="S_ORGCODE";
        columnNames[1]="S_TRECODE";
        columnNames[2]="S_TBSASSITSIGN";
        columnNames[3]="S_BUDGETSUBCODE";
        columnNames[4]="S_TIPSASSISTSIGN";
        columnNames[5]="I_MODICOUNT";
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

        if (obj == null || !(obj instanceof TsConvertassitsignDto))
            return false;

        TsConvertassitsignDto bean = (TsConvertassitsignDto) obj;


      //compare field sorgcode
      if((this.sorgcode==null && bean.sorgcode!=null) || (this.sorgcode!=null && bean.sorgcode==null))
          return false;
        else if(this.sorgcode==null && bean.sorgcode==null){
        }
        else{
          if(!bean.sorgcode.equals(this.sorgcode))
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
      //compare field stbsassitsign
      if((this.stbsassitsign==null && bean.stbsassitsign!=null) || (this.stbsassitsign!=null && bean.stbsassitsign==null))
          return false;
        else if(this.stbsassitsign==null && bean.stbsassitsign==null){
        }
        else{
          if(!bean.stbsassitsign.equals(this.stbsassitsign))
               return false;
     }
      //compare field sbudgetsubcode
      if((this.sbudgetsubcode==null && bean.sbudgetsubcode!=null) || (this.sbudgetsubcode!=null && bean.sbudgetsubcode==null))
          return false;
        else if(this.sbudgetsubcode==null && bean.sbudgetsubcode==null){
        }
        else{
          if(!bean.sbudgetsubcode.equals(this.sbudgetsubcode))
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



        return true;
    }

    /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
  
		int _hash_ = 1;
		
        if(this.sorgcode!=null)
          _hash_ = _hash_ * 31+ this.sorgcode.hashCode() ;
        if(this.strecode!=null)
          _hash_ = _hash_ * 31+ this.strecode.hashCode() ;
        if(this.stbsassitsign!=null)
          _hash_ = _hash_ * 31+ this.stbsassitsign.hashCode() ;
        if(this.sbudgetsubcode!=null)
          _hash_ = _hash_ * 31+ this.sbudgetsubcode.hashCode() ;
        if(this.stipsassistsign!=null)
          _hash_ = _hash_ * 31+ this.stipsassistsign.hashCode() ;
        if(this.imodicount!=null)
          _hash_ = _hash_ * 31+ this.imodicount.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsConvertassitsignDto bean = new TsConvertassitsignDto();

          bean.sorgcode = this.sorgcode;

          bean.strecode = this.strecode;

          bean.stbsassitsign = this.stbsassitsign;

          bean.sbudgetsubcode = this.sbudgetsubcode;

          if (this.stipsassistsign != null)
            bean.stipsassistsign = new String(this.stipsassistsign);
          if (this.imodicount != null)
            bean.imodicount = new Integer(this.imodicount.toString());
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsConvertassitsignDto").append(sep);
        sb.append("[sorgcode]").append(" = ").append(sorgcode).append(sep);
        sb.append("[strecode]").append(" = ").append(strecode).append(sep);
        sb.append("[stbsassitsign]").append(" = ").append(stbsassitsign).append(sep);
        sb.append("[sbudgetsubcode]").append(" = ").append(sbudgetsubcode).append(sep);
        sb.append("[stipsassistsign]").append(" = ").append(stipsassistsign).append(sep);
        sb.append("[imodicount]").append(" = ").append(imodicount).append(sep);
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
    
    //check field S_TRECODE
      if (this.getStrecode()==null)
             sb.append("S_TRECODE不能为空; ");
      if (this.getStrecode()!=null)
             if (this.getStrecode().getBytes().length > 10)
                sb.append("S_TRECODE宽度不能超过 "+10+"个字符; ");
    
    //check field S_TBSASSITSIGN
      if (this.getStbsassitsign()==null)
             sb.append("S_TBSASSITSIGN不能为空; ");
      if (this.getStbsassitsign()!=null)
             if (this.getStbsassitsign().getBytes().length > 30)
                sb.append("S_TBSASSITSIGN宽度不能超过 "+30+"个字符; ");
    
    //check field S_BUDGETSUBCODE
      if (this.getSbudgetsubcode()==null)
             sb.append("S_BUDGETSUBCODE不能为空; ");
      if (this.getSbudgetsubcode()!=null)
             if (this.getSbudgetsubcode().getBytes().length > 30)
                sb.append("S_BUDGETSUBCODE宽度不能超过 "+30+"个字符; ");
    
    //check field S_TIPSASSISTSIGN
      if (this.getStipsassistsign()==null)
             sb.append("S_TIPSASSISTSIGN不能为空; ");
      if (this.getStipsassistsign()!=null)
             if (this.getStipsassistsign().getBytes().length > 35)
                sb.append("S_TIPSASSISTSIGN宽度不能超过 "+35+"个字符; ");
    
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
    
    //check field S_TBSASSITSIGN
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TBSASSITSIGN")) {
               if (this.getStbsassitsign()==null)
                    sb.append("S_TBSASSITSIGN 不能为空; ");
               if (this.getStbsassitsign()!=null)
                    if (this.getStbsassitsign().getBytes().length > 30)
                        sb.append("S_TBSASSITSIGN 宽度不能超过 "+30+"个字符");
             break;
         }
  }
    
    //check field S_BUDGETSUBCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BUDGETSUBCODE")) {
               if (this.getSbudgetsubcode()==null)
                    sb.append("S_BUDGETSUBCODE 不能为空; ");
               if (this.getSbudgetsubcode()!=null)
                    if (this.getSbudgetsubcode().getBytes().length > 30)
                        sb.append("S_BUDGETSUBCODE 宽度不能超过 "+30+"个字符");
             break;
         }
  }
    
    //check field S_TIPSASSISTSIGN
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TIPSASSISTSIGN")) {
               if (this.getStipsassistsign()==null)
                    sb.append("S_TIPSASSISTSIGN 不能为空; ");
               if (this.getStipsassistsign()!=null)
                    if (this.getStipsassistsign().getBytes().length > 35)
                        sb.append("S_TIPSASSISTSIGN 宽度不能超过 "+35+"个字符");
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
      TsConvertassitsignPK pk = new TsConvertassitsignPK();
      pk.setSorgcode(getSorgcode());
      pk.setStrecode(getStrecode());
      pk.setStbsassitsign(getStbsassitsign());
      pk.setSbudgetsubcode(getSbudgetsubcode());
      return pk;
    };
}

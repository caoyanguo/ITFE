    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TvGrantpayfilesubPK;
/**
 * <p>Title: DTO of table: TV_GRANTPAYFILESUB</p>
 * <p>Description:  Data Transfer Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:02 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  dto.vm version timestamp: 2008-01-08 16:30:00 
 *
 * @author win7
 */

public class TvGrantpayfilesubDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  S_ORGCODE VARCHAR , PK   , NOT NULL       */
    protected String sorgcode;
    /**
    *  I_NOBEFOREPACKAGE INTEGER , PK   , NOT NULL       */
    protected Integer inobeforepackage;
    /**
    *  S_FUNSUBJECTCODE VARCHAR , PK   , NOT NULL       */
    protected String sfunsubjectcode;
    /**
    *  S_ECOSUBJECTCODE VARCHAR         */
    protected String secosubjectcode;
    /**
    *  N_MONEY DECIMAL   , NOT NULL       */
    protected BigDecimal nmoney;
    /**
    *  S_ACCATTRIB CHARACTER         */
    protected String saccattrib;
    /**
    *  S_USERCODE VARCHAR   , NOT NULL       */
    protected String susercode;
    /**
    *  S_DEMO VARCHAR         */
    protected String sdemo;


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


    public Integer getInobeforepackage()
    {
        return inobeforepackage;
    }
     /**
     *   Setter I_NOBEFOREPACKAGE, PK , NOT NULL        * */
    public void setInobeforepackage(Integer _inobeforepackage) {
        this.inobeforepackage = _inobeforepackage;
    }


    public String getSfunsubjectcode()
    {
        return sfunsubjectcode;
    }
     /**
     *   Setter S_FUNSUBJECTCODE, PK , NOT NULL        * */
    public void setSfunsubjectcode(String _sfunsubjectcode) {
        this.sfunsubjectcode = _sfunsubjectcode;
    }


    public String getSecosubjectcode()
    {
        return secosubjectcode;
    }
     /**
     *   Setter S_ECOSUBJECTCODE        * */
    public void setSecosubjectcode(String _secosubjectcode) {
        this.secosubjectcode = _secosubjectcode;
    }


    public BigDecimal getNmoney()
    {
        return nmoney;
    }
     /**
     *   Setter N_MONEY , NOT NULL        * */
    public void setNmoney(BigDecimal _nmoney) {
        this.nmoney = _nmoney;
    }


    public String getSaccattrib()
    {
        return saccattrib;
    }
     /**
     *   Setter S_ACCATTRIB        * */
    public void setSaccattrib(String _saccattrib) {
        this.saccattrib = _saccattrib;
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
    *   Getter S_ORGCODE, PK , NOT NULL       * */
    public static String  columnSorgcode()
    {
        return "S_ORGCODE";
    }
   
    /**
    *   Getter I_NOBEFOREPACKAGE, PK , NOT NULL       * */
    public static String  columnInobeforepackage()
    {
        return "I_NOBEFOREPACKAGE";
    }
   
    /**
    *   Getter S_FUNSUBJECTCODE, PK , NOT NULL       * */
    public static String  columnSfunsubjectcode()
    {
        return "S_FUNSUBJECTCODE";
    }
   
    /**
    *   Getter S_ECOSUBJECTCODE       * */
    public static String  columnSecosubjectcode()
    {
        return "S_ECOSUBJECTCODE";
    }
   
    /**
    *   Getter N_MONEY , NOT NULL       * */
    public static String  columnNmoney()
    {
        return "N_MONEY";
    }
   
    /**
    *   Getter S_ACCATTRIB       * */
    public static String  columnSaccattrib()
    {
        return "S_ACCATTRIB";
    }
   
    /**
    *   Getter S_USERCODE , NOT NULL       * */
    public static String  columnSusercode()
    {
        return "S_USERCODE";
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
        return "TV_GRANTPAYFILESUB";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[8];        
        columnNames[0]="S_ORGCODE";
        columnNames[1]="I_NOBEFOREPACKAGE";
        columnNames[2]="S_FUNSUBJECTCODE";
        columnNames[3]="S_ECOSUBJECTCODE";
        columnNames[4]="N_MONEY";
        columnNames[5]="S_ACCATTRIB";
        columnNames[6]="S_USERCODE";
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

        if (obj == null || !(obj instanceof TvGrantpayfilesubDto))
            return false;

        TvGrantpayfilesubDto bean = (TvGrantpayfilesubDto) obj;


      //compare field sorgcode
      if((this.sorgcode==null && bean.sorgcode!=null) || (this.sorgcode!=null && bean.sorgcode==null))
          return false;
        else if(this.sorgcode==null && bean.sorgcode==null){
        }
        else{
          if(!bean.sorgcode.equals(this.sorgcode))
               return false;
     }
      //compare field inobeforepackage
      if((this.inobeforepackage==null && bean.inobeforepackage!=null) || (this.inobeforepackage!=null && bean.inobeforepackage==null))
          return false;
        else if(this.inobeforepackage==null && bean.inobeforepackage==null){
        }
        else{
          if(!bean.inobeforepackage.equals(this.inobeforepackage))
               return false;
     }
      //compare field sfunsubjectcode
      if((this.sfunsubjectcode==null && bean.sfunsubjectcode!=null) || (this.sfunsubjectcode!=null && bean.sfunsubjectcode==null))
          return false;
        else if(this.sfunsubjectcode==null && bean.sfunsubjectcode==null){
        }
        else{
          if(!bean.sfunsubjectcode.equals(this.sfunsubjectcode))
               return false;
     }
      //compare field secosubjectcode
      if((this.secosubjectcode==null && bean.secosubjectcode!=null) || (this.secosubjectcode!=null && bean.secosubjectcode==null))
          return false;
        else if(this.secosubjectcode==null && bean.secosubjectcode==null){
        }
        else{
          if(!bean.secosubjectcode.equals(this.secosubjectcode))
               return false;
     }
      //compare field nmoney
      if((this.nmoney==null && bean.nmoney!=null) || (this.nmoney!=null && bean.nmoney==null))
          return false;
        else if(this.nmoney==null && bean.nmoney==null){
        }
        else{
          if(!bean.nmoney.equals(this.nmoney))
               return false;
     }
      //compare field saccattrib
      if((this.saccattrib==null && bean.saccattrib!=null) || (this.saccattrib!=null && bean.saccattrib==null))
          return false;
        else if(this.saccattrib==null && bean.saccattrib==null){
        }
        else{
          if(!bean.saccattrib.equals(this.saccattrib))
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
		
        if(this.sorgcode!=null)
          _hash_ = _hash_ * 31+ this.sorgcode.hashCode() ;
        if(this.inobeforepackage!=null)
          _hash_ = _hash_ * 31+ this.inobeforepackage.hashCode() ;
        if(this.sfunsubjectcode!=null)
          _hash_ = _hash_ * 31+ this.sfunsubjectcode.hashCode() ;
        if(this.secosubjectcode!=null)
          _hash_ = _hash_ * 31+ this.secosubjectcode.hashCode() ;
        if(this.nmoney!=null)
          _hash_ = _hash_ * 31+ this.nmoney.hashCode() ;
        if(this.saccattrib!=null)
          _hash_ = _hash_ * 31+ this.saccattrib.hashCode() ;
        if(this.susercode!=null)
          _hash_ = _hash_ * 31+ this.susercode.hashCode() ;
        if(this.sdemo!=null)
          _hash_ = _hash_ * 31+ this.sdemo.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TvGrantpayfilesubDto bean = new TvGrantpayfilesubDto();

          bean.sorgcode = this.sorgcode;

          bean.inobeforepackage = this.inobeforepackage;

          bean.sfunsubjectcode = this.sfunsubjectcode;

          if (this.secosubjectcode != null)
            bean.secosubjectcode = new String(this.secosubjectcode);
          if (this.nmoney != null)
            bean.nmoney = new BigDecimal(this.nmoney.toString());
          if (this.saccattrib != null)
            bean.saccattrib = new String(this.saccattrib);
          if (this.susercode != null)
            bean.susercode = new String(this.susercode);
          if (this.sdemo != null)
            bean.sdemo = new String(this.sdemo);
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TvGrantpayfilesubDto").append(sep);
        sb.append("[sorgcode]").append(" = ").append(sorgcode).append(sep);
        sb.append("[inobeforepackage]").append(" = ").append(inobeforepackage).append(sep);
        sb.append("[sfunsubjectcode]").append(" = ").append(sfunsubjectcode).append(sep);
        sb.append("[secosubjectcode]").append(" = ").append(secosubjectcode).append(sep);
        sb.append("[nmoney]").append(" = ").append(nmoney).append(sep);
        sb.append("[saccattrib]").append(" = ").append(saccattrib).append(sep);
        sb.append("[susercode]").append(" = ").append(susercode).append(sep);
        sb.append("[sdemo]").append(" = ").append(sdemo).append(sep);
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
    
    //check field I_NOBEFOREPACKAGE
      if (this.getInobeforepackage()==null)
             sb.append("I_NOBEFOREPACKAGE不能为空; ");
      
    //check field S_FUNSUBJECTCODE
      if (this.getSfunsubjectcode()==null)
             sb.append("S_FUNSUBJECTCODE不能为空; ");
      if (this.getSfunsubjectcode()!=null)
             if (this.getSfunsubjectcode().getBytes().length > 30)
                sb.append("S_FUNSUBJECTCODE宽度不能超过 "+30+"个字符; ");
    
    //check field S_ECOSUBJECTCODE
      if (this.getSecosubjectcode()!=null)
             if (this.getSecosubjectcode().getBytes().length > 30)
                sb.append("S_ECOSUBJECTCODE宽度不能超过 "+30+"个字符; ");
    
    //check field N_MONEY
      if (this.getNmoney()==null)
             sb.append("N_MONEY不能为空; ");
      
    //check field S_ACCATTRIB
      if (this.getSaccattrib()!=null)
             if (this.getSaccattrib().getBytes().length > 1)
                sb.append("S_ACCATTRIB宽度不能超过 "+1+"个字符; ");
    
    //check field S_USERCODE
      if (this.getSusercode()==null)
             sb.append("S_USERCODE不能为空; ");
      if (this.getSusercode()!=null)
             if (this.getSusercode().getBytes().length > 30)
                sb.append("S_USERCODE宽度不能超过 "+30+"个字符; ");
    
    //check field S_DEMO
      if (this.getSdemo()!=null)
             if (this.getSdemo().getBytes().length > 100)
                sb.append("S_DEMO宽度不能超过 "+100+"个字符; ");
    

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
    
    //check field I_NOBEFOREPACKAGE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("I_NOBEFOREPACKAGE")) {
               if (this.getInobeforepackage()==null)
                    sb.append("I_NOBEFOREPACKAGE 不能为空; ");
               break;
         }
  }
    
    //check field S_FUNSUBJECTCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_FUNSUBJECTCODE")) {
               if (this.getSfunsubjectcode()==null)
                    sb.append("S_FUNSUBJECTCODE 不能为空; ");
               if (this.getSfunsubjectcode()!=null)
                    if (this.getSfunsubjectcode().getBytes().length > 30)
                        sb.append("S_FUNSUBJECTCODE 宽度不能超过 "+30+"个字符");
             break;
         }
  }
    
    //check field S_ECOSUBJECTCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_ECOSUBJECTCODE")) {
                 if (this.getSecosubjectcode()!=null)
                    if (this.getSecosubjectcode().getBytes().length > 30)
                        sb.append("S_ECOSUBJECTCODE 宽度不能超过 "+30+"个字符");
             break;
         }
  }
    
    //check field N_MONEY
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("N_MONEY")) {
               if (this.getNmoney()==null)
                    sb.append("N_MONEY 不能为空; ");
               break;
         }
  }
    
    //check field S_ACCATTRIB
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_ACCATTRIB")) {
                 if (this.getSaccattrib()!=null)
                    if (this.getSaccattrib().getBytes().length > 1)
                        sb.append("S_ACCATTRIB 宽度不能超过 "+1+"个字符");
             break;
         }
  }
    
    //check field S_USERCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_USERCODE")) {
               if (this.getSusercode()==null)
                    sb.append("S_USERCODE 不能为空; ");
               if (this.getSusercode()!=null)
                    if (this.getSusercode().getBytes().length > 30)
                        sb.append("S_USERCODE 宽度不能超过 "+30+"个字符");
             break;
         }
  }
    
    //check field S_DEMO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_DEMO")) {
                 if (this.getSdemo()!=null)
                    if (this.getSdemo().getBytes().length > 100)
                        sb.append("S_DEMO 宽度不能超过 "+100+"个字符");
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
      TvGrantpayfilesubPK pk = new TvGrantpayfilesubPK();
      pk.setSorgcode(getSorgcode());
      pk.setInobeforepackage(getInobeforepackage());
      pk.setSfunsubjectcode(getSfunsubjectcode());
      return pk;
    };
}

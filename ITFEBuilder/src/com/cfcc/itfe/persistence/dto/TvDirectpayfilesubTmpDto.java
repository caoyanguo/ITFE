    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TvDirectpayfilesubTmpPK;
/**
 * <p>Title: DTO of table: TV_DIRECTPAYFILESUB_TMP</p>
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

public class TvDirectpayfilesubTmpDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  S_OFYEAR CHARACTER   , NOT NULL       */
    protected String sofyear;
    /**
    *  S_BUDGETUNITCODE VARCHAR   , NOT NULL       */
    protected String sbudgetunitcode;
    /**
    *  S_TAXTICKETNO VARCHAR   , NOT NULL       */
    protected String staxticketno;
    /**
    *  S_LINE CHARACTER   , NOT NULL       */
    protected String sline;
    /**
    *  N_MONEY DECIMAL   , NOT NULL       */
    protected BigDecimal nmoney;
    /**
    *  S_TZBM CHARACTER         */
    protected String stzbm;
    /**
    *  S_TZMC VARCHAR         */
    protected String stzmc;
    /**
    *  S_FUNSUBJECTCODE VARCHAR   , NOT NULL       */
    protected String sfunsubjectcode;
    /**
    *  S_FUNSUBJECTCODENAME VARCHAR         */
    protected String sfunsubjectcodename;
    /**
    *  S_FILENAME VARCHAR   , NOT NULL       */
    protected String sfilename;
    /**
    *  S_ORGCODE VARCHAR   , NOT NULL       */
    protected String sorgcode;


/******************************************************
*
*  getter and setter
*
*******************************************************/


    public String getSofyear()
    {
        return sofyear;
    }
     /**
     *   Setter S_OFYEAR , NOT NULL        * */
    public void setSofyear(String _sofyear) {
        this.sofyear = _sofyear;
    }


    public String getSbudgetunitcode()
    {
        return sbudgetunitcode;
    }
     /**
     *   Setter S_BUDGETUNITCODE , NOT NULL        * */
    public void setSbudgetunitcode(String _sbudgetunitcode) {
        this.sbudgetunitcode = _sbudgetunitcode;
    }


    public String getStaxticketno()
    {
        return staxticketno;
    }
     /**
     *   Setter S_TAXTICKETNO , NOT NULL        * */
    public void setStaxticketno(String _staxticketno) {
        this.staxticketno = _staxticketno;
    }


    public String getSline()
    {
        return sline;
    }
     /**
     *   Setter S_LINE , NOT NULL        * */
    public void setSline(String _sline) {
        this.sline = _sline;
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


    public String getStzbm()
    {
        return stzbm;
    }
     /**
     *   Setter S_TZBM        * */
    public void setStzbm(String _stzbm) {
        this.stzbm = _stzbm;
    }


    public String getStzmc()
    {
        return stzmc;
    }
     /**
     *   Setter S_TZMC        * */
    public void setStzmc(String _stzmc) {
        this.stzmc = _stzmc;
    }


    public String getSfunsubjectcode()
    {
        return sfunsubjectcode;
    }
     /**
     *   Setter S_FUNSUBJECTCODE , NOT NULL        * */
    public void setSfunsubjectcode(String _sfunsubjectcode) {
        this.sfunsubjectcode = _sfunsubjectcode;
    }


    public String getSfunsubjectcodename()
    {
        return sfunsubjectcodename;
    }
     /**
     *   Setter S_FUNSUBJECTCODENAME        * */
    public void setSfunsubjectcodename(String _sfunsubjectcodename) {
        this.sfunsubjectcodename = _sfunsubjectcodename;
    }


    public String getSfilename()
    {
        return sfilename;
    }
     /**
     *   Setter S_FILENAME , NOT NULL        * */
    public void setSfilename(String _sfilename) {
        this.sfilename = _sfilename;
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




/******************************************************
*
*  Get Column Name
*
*******************************************************/
    /**
    *   Getter S_OFYEAR , NOT NULL       * */
    public static String  columnSofyear()
    {
        return "S_OFYEAR";
    }
   
    /**
    *   Getter S_BUDGETUNITCODE , NOT NULL       * */
    public static String  columnSbudgetunitcode()
    {
        return "S_BUDGETUNITCODE";
    }
   
    /**
    *   Getter S_TAXTICKETNO , NOT NULL       * */
    public static String  columnStaxticketno()
    {
        return "S_TAXTICKETNO";
    }
   
    /**
    *   Getter S_LINE , NOT NULL       * */
    public static String  columnSline()
    {
        return "S_LINE";
    }
   
    /**
    *   Getter N_MONEY , NOT NULL       * */
    public static String  columnNmoney()
    {
        return "N_MONEY";
    }
   
    /**
    *   Getter S_TZBM       * */
    public static String  columnStzbm()
    {
        return "S_TZBM";
    }
   
    /**
    *   Getter S_TZMC       * */
    public static String  columnStzmc()
    {
        return "S_TZMC";
    }
   
    /**
    *   Getter S_FUNSUBJECTCODE , NOT NULL       * */
    public static String  columnSfunsubjectcode()
    {
        return "S_FUNSUBJECTCODE";
    }
   
    /**
    *   Getter S_FUNSUBJECTCODENAME       * */
    public static String  columnSfunsubjectcodename()
    {
        return "S_FUNSUBJECTCODENAME";
    }
   
    /**
    *   Getter S_FILENAME , NOT NULL       * */
    public static String  columnSfilename()
    {
        return "S_FILENAME";
    }
   
    /**
    *   Getter S_ORGCODE , NOT NULL       * */
    public static String  columnSorgcode()
    {
        return "S_ORGCODE";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "TV_DIRECTPAYFILESUB_TMP";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[11];        
        columnNames[0]="S_OFYEAR";
        columnNames[1]="S_BUDGETUNITCODE";
        columnNames[2]="S_TAXTICKETNO";
        columnNames[3]="S_LINE";
        columnNames[4]="N_MONEY";
        columnNames[5]="S_TZBM";
        columnNames[6]="S_TZMC";
        columnNames[7]="S_FUNSUBJECTCODE";
        columnNames[8]="S_FUNSUBJECTCODENAME";
        columnNames[9]="S_FILENAME";
        columnNames[10]="S_ORGCODE";
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

        if (obj == null || !(obj instanceof TvDirectpayfilesubTmpDto))
            return false;

        TvDirectpayfilesubTmpDto bean = (TvDirectpayfilesubTmpDto) obj;


      //compare field sofyear
      if((this.sofyear==null && bean.sofyear!=null) || (this.sofyear!=null && bean.sofyear==null))
          return false;
        else if(this.sofyear==null && bean.sofyear==null){
        }
        else{
          if(!bean.sofyear.equals(this.sofyear))
               return false;
     }
      //compare field sbudgetunitcode
      if((this.sbudgetunitcode==null && bean.sbudgetunitcode!=null) || (this.sbudgetunitcode!=null && bean.sbudgetunitcode==null))
          return false;
        else if(this.sbudgetunitcode==null && bean.sbudgetunitcode==null){
        }
        else{
          if(!bean.sbudgetunitcode.equals(this.sbudgetunitcode))
               return false;
     }
      //compare field staxticketno
      if((this.staxticketno==null && bean.staxticketno!=null) || (this.staxticketno!=null && bean.staxticketno==null))
          return false;
        else if(this.staxticketno==null && bean.staxticketno==null){
        }
        else{
          if(!bean.staxticketno.equals(this.staxticketno))
               return false;
     }
      //compare field sline
      if((this.sline==null && bean.sline!=null) || (this.sline!=null && bean.sline==null))
          return false;
        else if(this.sline==null && bean.sline==null){
        }
        else{
          if(!bean.sline.equals(this.sline))
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
      //compare field stzbm
      if((this.stzbm==null && bean.stzbm!=null) || (this.stzbm!=null && bean.stzbm==null))
          return false;
        else if(this.stzbm==null && bean.stzbm==null){
        }
        else{
          if(!bean.stzbm.equals(this.stzbm))
               return false;
     }
      //compare field stzmc
      if((this.stzmc==null && bean.stzmc!=null) || (this.stzmc!=null && bean.stzmc==null))
          return false;
        else if(this.stzmc==null && bean.stzmc==null){
        }
        else{
          if(!bean.stzmc.equals(this.stzmc))
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
      //compare field sfunsubjectcodename
      if((this.sfunsubjectcodename==null && bean.sfunsubjectcodename!=null) || (this.sfunsubjectcodename!=null && bean.sfunsubjectcodename==null))
          return false;
        else if(this.sfunsubjectcodename==null && bean.sfunsubjectcodename==null){
        }
        else{
          if(!bean.sfunsubjectcodename.equals(this.sfunsubjectcodename))
               return false;
     }
      //compare field sfilename
      if((this.sfilename==null && bean.sfilename!=null) || (this.sfilename!=null && bean.sfilename==null))
          return false;
        else if(this.sfilename==null && bean.sfilename==null){
        }
        else{
          if(!bean.sfilename.equals(this.sfilename))
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



        return true;
    }

    /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
  
		int _hash_ = 1;
		
        if(this.sofyear!=null)
          _hash_ = _hash_ * 31+ this.sofyear.hashCode() ;
        if(this.sbudgetunitcode!=null)
          _hash_ = _hash_ * 31+ this.sbudgetunitcode.hashCode() ;
        if(this.staxticketno!=null)
          _hash_ = _hash_ * 31+ this.staxticketno.hashCode() ;
        if(this.sline!=null)
          _hash_ = _hash_ * 31+ this.sline.hashCode() ;
        if(this.nmoney!=null)
          _hash_ = _hash_ * 31+ this.nmoney.hashCode() ;
        if(this.stzbm!=null)
          _hash_ = _hash_ * 31+ this.stzbm.hashCode() ;
        if(this.stzmc!=null)
          _hash_ = _hash_ * 31+ this.stzmc.hashCode() ;
        if(this.sfunsubjectcode!=null)
          _hash_ = _hash_ * 31+ this.sfunsubjectcode.hashCode() ;
        if(this.sfunsubjectcodename!=null)
          _hash_ = _hash_ * 31+ this.sfunsubjectcodename.hashCode() ;
        if(this.sfilename!=null)
          _hash_ = _hash_ * 31+ this.sfilename.hashCode() ;
        if(this.sorgcode!=null)
          _hash_ = _hash_ * 31+ this.sorgcode.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TvDirectpayfilesubTmpDto bean = new TvDirectpayfilesubTmpDto();

          if (this.sofyear != null)
            bean.sofyear = new String(this.sofyear);
          if (this.sbudgetunitcode != null)
            bean.sbudgetunitcode = new String(this.sbudgetunitcode);
          if (this.staxticketno != null)
            bean.staxticketno = new String(this.staxticketno);
          if (this.sline != null)
            bean.sline = new String(this.sline);
          if (this.nmoney != null)
            bean.nmoney = new BigDecimal(this.nmoney.toString());
          if (this.stzbm != null)
            bean.stzbm = new String(this.stzbm);
          if (this.stzmc != null)
            bean.stzmc = new String(this.stzmc);
          if (this.sfunsubjectcode != null)
            bean.sfunsubjectcode = new String(this.sfunsubjectcode);
          if (this.sfunsubjectcodename != null)
            bean.sfunsubjectcodename = new String(this.sfunsubjectcodename);
          if (this.sfilename != null)
            bean.sfilename = new String(this.sfilename);
          if (this.sorgcode != null)
            bean.sorgcode = new String(this.sorgcode);
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TvDirectpayfilesubTmpDto").append(sep);
        sb.append("[sofyear]").append(" = ").append(sofyear).append(sep);
        sb.append("[sbudgetunitcode]").append(" = ").append(sbudgetunitcode).append(sep);
        sb.append("[staxticketno]").append(" = ").append(staxticketno).append(sep);
        sb.append("[sline]").append(" = ").append(sline).append(sep);
        sb.append("[nmoney]").append(" = ").append(nmoney).append(sep);
        sb.append("[stzbm]").append(" = ").append(stzbm).append(sep);
        sb.append("[stzmc]").append(" = ").append(stzmc).append(sep);
        sb.append("[sfunsubjectcode]").append(" = ").append(sfunsubjectcode).append(sep);
        sb.append("[sfunsubjectcodename]").append(" = ").append(sfunsubjectcodename).append(sep);
        sb.append("[sfilename]").append(" = ").append(sfilename).append(sep);
        sb.append("[sorgcode]").append(" = ").append(sorgcode).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //check field S_OFYEAR
      if (this.getSofyear()==null)
             sb.append("S_OFYEAR不能为空; ");
      if (this.getSofyear()!=null)
             if (this.getSofyear().getBytes().length > 4)
                sb.append("S_OFYEAR宽度不能超过 "+4+"个字符; ");
    
    //check field S_BUDGETUNITCODE
      if (this.getSbudgetunitcode()==null)
             sb.append("S_BUDGETUNITCODE不能为空; ");
      if (this.getSbudgetunitcode()!=null)
             if (this.getSbudgetunitcode().getBytes().length > 30)
                sb.append("S_BUDGETUNITCODE宽度不能超过 "+30+"个字符; ");
    
    //check field S_TAXTICKETNO
      if (this.getStaxticketno()==null)
             sb.append("S_TAXTICKETNO不能为空; ");
      if (this.getStaxticketno()!=null)
             if (this.getStaxticketno().getBytes().length > 30)
                sb.append("S_TAXTICKETNO宽度不能超过 "+30+"个字符; ");
    
    //check field S_LINE
      if (this.getSline()==null)
             sb.append("S_LINE不能为空; ");
      if (this.getSline()!=null)
             if (this.getSline().getBytes().length > 4)
                sb.append("S_LINE宽度不能超过 "+4+"个字符; ");
    
    //check field N_MONEY
      if (this.getNmoney()==null)
             sb.append("N_MONEY不能为空; ");
      
    //check field S_TZBM
      if (this.getStzbm()!=null)
             if (this.getStzbm().getBytes().length > 2)
                sb.append("S_TZBM宽度不能超过 "+2+"个字符; ");
    
    //check field S_TZMC
      if (this.getStzmc()!=null)
             if (this.getStzmc().getBytes().length > 120)
                sb.append("S_TZMC宽度不能超过 "+120+"个字符; ");
    
    //check field S_FUNSUBJECTCODE
      if (this.getSfunsubjectcode()==null)
             sb.append("S_FUNSUBJECTCODE不能为空; ");
      if (this.getSfunsubjectcode()!=null)
             if (this.getSfunsubjectcode().getBytes().length > 30)
                sb.append("S_FUNSUBJECTCODE宽度不能超过 "+30+"个字符; ");
    
    //check field S_FUNSUBJECTCODENAME
      if (this.getSfunsubjectcodename()!=null)
             if (this.getSfunsubjectcodename().getBytes().length > 120)
                sb.append("S_FUNSUBJECTCODENAME宽度不能超过 "+120+"个字符; ");
    
    //check field S_FILENAME
      if (this.getSfilename()==null)
             sb.append("S_FILENAME不能为空; ");
      if (this.getSfilename()!=null)
             if (this.getSfilename().getBytes().length > 100)
                sb.append("S_FILENAME宽度不能超过 "+100+"个字符; ");
    
    //check field S_ORGCODE
      if (this.getSorgcode()==null)
             sb.append("S_ORGCODE不能为空; ");
      if (this.getSorgcode()!=null)
             if (this.getSorgcode().getBytes().length > 12)
                sb.append("S_ORGCODE宽度不能超过 "+12+"个字符; ");
    

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
    //check field S_OFYEAR
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_OFYEAR")) {
               if (this.getSofyear()==null)
                    sb.append("S_OFYEAR 不能为空; ");
               if (this.getSofyear()!=null)
                    if (this.getSofyear().getBytes().length > 4)
                        sb.append("S_OFYEAR 宽度不能超过 "+4+"个字符");
             break;
         }
  }
    
    //check field S_BUDGETUNITCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BUDGETUNITCODE")) {
               if (this.getSbudgetunitcode()==null)
                    sb.append("S_BUDGETUNITCODE 不能为空; ");
               if (this.getSbudgetunitcode()!=null)
                    if (this.getSbudgetunitcode().getBytes().length > 30)
                        sb.append("S_BUDGETUNITCODE 宽度不能超过 "+30+"个字符");
             break;
         }
  }
    
    //check field S_TAXTICKETNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXTICKETNO")) {
               if (this.getStaxticketno()==null)
                    sb.append("S_TAXTICKETNO 不能为空; ");
               if (this.getStaxticketno()!=null)
                    if (this.getStaxticketno().getBytes().length > 30)
                        sb.append("S_TAXTICKETNO 宽度不能超过 "+30+"个字符");
             break;
         }
  }
    
    //check field S_LINE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_LINE")) {
               if (this.getSline()==null)
                    sb.append("S_LINE 不能为空; ");
               if (this.getSline()!=null)
                    if (this.getSline().getBytes().length > 4)
                        sb.append("S_LINE 宽度不能超过 "+4+"个字符");
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
    
    //check field S_TZBM
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TZBM")) {
                 if (this.getStzbm()!=null)
                    if (this.getStzbm().getBytes().length > 2)
                        sb.append("S_TZBM 宽度不能超过 "+2+"个字符");
             break;
         }
  }
    
    //check field S_TZMC
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TZMC")) {
                 if (this.getStzmc()!=null)
                    if (this.getStzmc().getBytes().length > 120)
                        sb.append("S_TZMC 宽度不能超过 "+120+"个字符");
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
    
    //check field S_FUNSUBJECTCODENAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_FUNSUBJECTCODENAME")) {
                 if (this.getSfunsubjectcodename()!=null)
                    if (this.getSfunsubjectcodename().getBytes().length > 120)
                        sb.append("S_FUNSUBJECTCODENAME 宽度不能超过 "+120+"个字符");
             break;
         }
  }
    
    //check field S_FILENAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_FILENAME")) {
               if (this.getSfilename()==null)
                    sb.append("S_FILENAME 不能为空; ");
               if (this.getSfilename()!=null)
                    if (this.getSfilename().getBytes().length > 100)
                        sb.append("S_FILENAME 宽度不能超过 "+100+"个字符");
             break;
         }
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
      throw new RuntimeException("此dto没有PK，不能进行此操作");
    };
}

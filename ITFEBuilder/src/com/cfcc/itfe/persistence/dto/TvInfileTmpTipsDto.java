    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TvInfileTmpTipsPK;
/**
 * <p>Title: DTO of table: TV_INFILE_TMP_TIPS</p>
 * <p>Description:  Data Transfer Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:03 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  dto.vm version timestamp: 2008-01-08 16:30:00 
 *
 * @author win7
 */

public class TvInfileTmpTipsDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  S_TAXORGCODE VARCHAR   , NOT NULL       */
    protected String staxorgcode;
    /**
    *  S_TRECODE CHARACTER   , NOT NULL       */
    protected String strecode;
    /**
    *  N_MONEY DECIMAL   , NOT NULL       */
    protected BigDecimal nmoney;
    /**
    *  S_BILLDATE CHARACTER         */
    protected String sbilldate;
    /**
    *  S_BUDGETSUBCODE VARCHAR         */
    protected String sbudgetsubcode;
    /**
    *  S_BUDGETLEVELCODE CHARACTER         */
    protected String sbudgetlevelcode;
    /**
    *  S_BUDGETTYPE CHARACTER   , NOT NULL       */
    protected String sbudgettype;
    /**
    *  S_ASSITSIGN VARCHAR         */
    protected String sassitsign;
    /**
    *  S_TAXPAYCODE VARCHAR         */
    protected String staxpaycode;
    /**
    *  S_TAXPAYNAME VARCHAR         */
    protected String staxpayname;
    /**
    *  S_PAYBNKNO VARCHAR         */
    protected String spaybnkno;
    /**
    *  S_PAYACCT VARCHAR         */
    protected String spayacct;
    /**
    *  S_PAYOPBNKNO VARCHAR         */
    protected String spayopbnkno;
    /**
    *  S_TAXSTARTDATE CHARACTER         */
    protected String staxstartdate;
    /**
    *  S_TAXENDDATE CHARACTER         */
    protected String staxenddate;
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


    public String getStaxorgcode()
    {
        return staxorgcode;
    }
     /**
     *   Setter S_TAXORGCODE , NOT NULL        * */
    public void setStaxorgcode(String _staxorgcode) {
        this.staxorgcode = _staxorgcode;
    }


    public String getStrecode()
    {
        return strecode;
    }
     /**
     *   Setter S_TRECODE , NOT NULL        * */
    public void setStrecode(String _strecode) {
        this.strecode = _strecode;
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


    public String getSbilldate()
    {
        return sbilldate;
    }
     /**
     *   Setter S_BILLDATE        * */
    public void setSbilldate(String _sbilldate) {
        this.sbilldate = _sbilldate;
    }


    public String getSbudgetsubcode()
    {
        return sbudgetsubcode;
    }
     /**
     *   Setter S_BUDGETSUBCODE        * */
    public void setSbudgetsubcode(String _sbudgetsubcode) {
        this.sbudgetsubcode = _sbudgetsubcode;
    }


    public String getSbudgetlevelcode()
    {
        return sbudgetlevelcode;
    }
     /**
     *   Setter S_BUDGETLEVELCODE        * */
    public void setSbudgetlevelcode(String _sbudgetlevelcode) {
        this.sbudgetlevelcode = _sbudgetlevelcode;
    }


    public String getSbudgettype()
    {
        return sbudgettype;
    }
     /**
     *   Setter S_BUDGETTYPE , NOT NULL        * */
    public void setSbudgettype(String _sbudgettype) {
        this.sbudgettype = _sbudgettype;
    }


    public String getSassitsign()
    {
        return sassitsign;
    }
     /**
     *   Setter S_ASSITSIGN        * */
    public void setSassitsign(String _sassitsign) {
        this.sassitsign = _sassitsign;
    }


    public String getStaxpaycode()
    {
        return staxpaycode;
    }
     /**
     *   Setter S_TAXPAYCODE        * */
    public void setStaxpaycode(String _staxpaycode) {
        this.staxpaycode = _staxpaycode;
    }


    public String getStaxpayname()
    {
        return staxpayname;
    }
     /**
     *   Setter S_TAXPAYNAME        * */
    public void setStaxpayname(String _staxpayname) {
        this.staxpayname = _staxpayname;
    }


    public String getSpaybnkno()
    {
        return spaybnkno;
    }
     /**
     *   Setter S_PAYBNKNO        * */
    public void setSpaybnkno(String _spaybnkno) {
        this.spaybnkno = _spaybnkno;
    }


    public String getSpayacct()
    {
        return spayacct;
    }
     /**
     *   Setter S_PAYACCT        * */
    public void setSpayacct(String _spayacct) {
        this.spayacct = _spayacct;
    }


    public String getSpayopbnkno()
    {
        return spayopbnkno;
    }
     /**
     *   Setter S_PAYOPBNKNO        * */
    public void setSpayopbnkno(String _spayopbnkno) {
        this.spayopbnkno = _spayopbnkno;
    }


    public String getStaxstartdate()
    {
        return staxstartdate;
    }
     /**
     *   Setter S_TAXSTARTDATE        * */
    public void setStaxstartdate(String _staxstartdate) {
        this.staxstartdate = _staxstartdate;
    }


    public String getStaxenddate()
    {
        return staxenddate;
    }
     /**
     *   Setter S_TAXENDDATE        * */
    public void setStaxenddate(String _staxenddate) {
        this.staxenddate = _staxenddate;
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
    *   Getter S_TAXORGCODE , NOT NULL       * */
    public static String  columnStaxorgcode()
    {
        return "S_TAXORGCODE";
    }
   
    /**
    *   Getter S_TRECODE , NOT NULL       * */
    public static String  columnStrecode()
    {
        return "S_TRECODE";
    }
   
    /**
    *   Getter N_MONEY , NOT NULL       * */
    public static String  columnNmoney()
    {
        return "N_MONEY";
    }
   
    /**
    *   Getter S_BILLDATE       * */
    public static String  columnSbilldate()
    {
        return "S_BILLDATE";
    }
   
    /**
    *   Getter S_BUDGETSUBCODE       * */
    public static String  columnSbudgetsubcode()
    {
        return "S_BUDGETSUBCODE";
    }
   
    /**
    *   Getter S_BUDGETLEVELCODE       * */
    public static String  columnSbudgetlevelcode()
    {
        return "S_BUDGETLEVELCODE";
    }
   
    /**
    *   Getter S_BUDGETTYPE , NOT NULL       * */
    public static String  columnSbudgettype()
    {
        return "S_BUDGETTYPE";
    }
   
    /**
    *   Getter S_ASSITSIGN       * */
    public static String  columnSassitsign()
    {
        return "S_ASSITSIGN";
    }
   
    /**
    *   Getter S_TAXPAYCODE       * */
    public static String  columnStaxpaycode()
    {
        return "S_TAXPAYCODE";
    }
   
    /**
    *   Getter S_TAXPAYNAME       * */
    public static String  columnStaxpayname()
    {
        return "S_TAXPAYNAME";
    }
   
    /**
    *   Getter S_PAYBNKNO       * */
    public static String  columnSpaybnkno()
    {
        return "S_PAYBNKNO";
    }
   
    /**
    *   Getter S_PAYACCT       * */
    public static String  columnSpayacct()
    {
        return "S_PAYACCT";
    }
   
    /**
    *   Getter S_PAYOPBNKNO       * */
    public static String  columnSpayopbnkno()
    {
        return "S_PAYOPBNKNO";
    }
   
    /**
    *   Getter S_TAXSTARTDATE       * */
    public static String  columnStaxstartdate()
    {
        return "S_TAXSTARTDATE";
    }
   
    /**
    *   Getter S_TAXENDDATE       * */
    public static String  columnStaxenddate()
    {
        return "S_TAXENDDATE";
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
        return "TV_INFILE_TMP_TIPS";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[17];        
        columnNames[0]="S_TAXORGCODE";
        columnNames[1]="S_TRECODE";
        columnNames[2]="N_MONEY";
        columnNames[3]="S_BILLDATE";
        columnNames[4]="S_BUDGETSUBCODE";
        columnNames[5]="S_BUDGETLEVELCODE";
        columnNames[6]="S_BUDGETTYPE";
        columnNames[7]="S_ASSITSIGN";
        columnNames[8]="S_TAXPAYCODE";
        columnNames[9]="S_TAXPAYNAME";
        columnNames[10]="S_PAYBNKNO";
        columnNames[11]="S_PAYACCT";
        columnNames[12]="S_PAYOPBNKNO";
        columnNames[13]="S_TAXSTARTDATE";
        columnNames[14]="S_TAXENDDATE";
        columnNames[15]="S_FILENAME";
        columnNames[16]="S_ORGCODE";
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

        if (obj == null || !(obj instanceof TvInfileTmpTipsDto))
            return false;

        TvInfileTmpTipsDto bean = (TvInfileTmpTipsDto) obj;


      //compare field staxorgcode
      if((this.staxorgcode==null && bean.staxorgcode!=null) || (this.staxorgcode!=null && bean.staxorgcode==null))
          return false;
        else if(this.staxorgcode==null && bean.staxorgcode==null){
        }
        else{
          if(!bean.staxorgcode.equals(this.staxorgcode))
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
      //compare field nmoney
      if((this.nmoney==null && bean.nmoney!=null) || (this.nmoney!=null && bean.nmoney==null))
          return false;
        else if(this.nmoney==null && bean.nmoney==null){
        }
        else{
          if(!bean.nmoney.equals(this.nmoney))
               return false;
     }
      //compare field sbilldate
      if((this.sbilldate==null && bean.sbilldate!=null) || (this.sbilldate!=null && bean.sbilldate==null))
          return false;
        else if(this.sbilldate==null && bean.sbilldate==null){
        }
        else{
          if(!bean.sbilldate.equals(this.sbilldate))
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
      //compare field sbudgetlevelcode
      if((this.sbudgetlevelcode==null && bean.sbudgetlevelcode!=null) || (this.sbudgetlevelcode!=null && bean.sbudgetlevelcode==null))
          return false;
        else if(this.sbudgetlevelcode==null && bean.sbudgetlevelcode==null){
        }
        else{
          if(!bean.sbudgetlevelcode.equals(this.sbudgetlevelcode))
               return false;
     }
      //compare field sbudgettype
      if((this.sbudgettype==null && bean.sbudgettype!=null) || (this.sbudgettype!=null && bean.sbudgettype==null))
          return false;
        else if(this.sbudgettype==null && bean.sbudgettype==null){
        }
        else{
          if(!bean.sbudgettype.equals(this.sbudgettype))
               return false;
     }
      //compare field sassitsign
      if((this.sassitsign==null && bean.sassitsign!=null) || (this.sassitsign!=null && bean.sassitsign==null))
          return false;
        else if(this.sassitsign==null && bean.sassitsign==null){
        }
        else{
          if(!bean.sassitsign.equals(this.sassitsign))
               return false;
     }
      //compare field staxpaycode
      if((this.staxpaycode==null && bean.staxpaycode!=null) || (this.staxpaycode!=null && bean.staxpaycode==null))
          return false;
        else if(this.staxpaycode==null && bean.staxpaycode==null){
        }
        else{
          if(!bean.staxpaycode.equals(this.staxpaycode))
               return false;
     }
      //compare field staxpayname
      if((this.staxpayname==null && bean.staxpayname!=null) || (this.staxpayname!=null && bean.staxpayname==null))
          return false;
        else if(this.staxpayname==null && bean.staxpayname==null){
        }
        else{
          if(!bean.staxpayname.equals(this.staxpayname))
               return false;
     }
      //compare field spaybnkno
      if((this.spaybnkno==null && bean.spaybnkno!=null) || (this.spaybnkno!=null && bean.spaybnkno==null))
          return false;
        else if(this.spaybnkno==null && bean.spaybnkno==null){
        }
        else{
          if(!bean.spaybnkno.equals(this.spaybnkno))
               return false;
     }
      //compare field spayacct
      if((this.spayacct==null && bean.spayacct!=null) || (this.spayacct!=null && bean.spayacct==null))
          return false;
        else if(this.spayacct==null && bean.spayacct==null){
        }
        else{
          if(!bean.spayacct.equals(this.spayacct))
               return false;
     }
      //compare field spayopbnkno
      if((this.spayopbnkno==null && bean.spayopbnkno!=null) || (this.spayopbnkno!=null && bean.spayopbnkno==null))
          return false;
        else if(this.spayopbnkno==null && bean.spayopbnkno==null){
        }
        else{
          if(!bean.spayopbnkno.equals(this.spayopbnkno))
               return false;
     }
      //compare field staxstartdate
      if((this.staxstartdate==null && bean.staxstartdate!=null) || (this.staxstartdate!=null && bean.staxstartdate==null))
          return false;
        else if(this.staxstartdate==null && bean.staxstartdate==null){
        }
        else{
          if(!bean.staxstartdate.equals(this.staxstartdate))
               return false;
     }
      //compare field staxenddate
      if((this.staxenddate==null && bean.staxenddate!=null) || (this.staxenddate!=null && bean.staxenddate==null))
          return false;
        else if(this.staxenddate==null && bean.staxenddate==null){
        }
        else{
          if(!bean.staxenddate.equals(this.staxenddate))
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
		
        if(this.staxorgcode!=null)
          _hash_ = _hash_ * 31+ this.staxorgcode.hashCode() ;
        if(this.strecode!=null)
          _hash_ = _hash_ * 31+ this.strecode.hashCode() ;
        if(this.nmoney!=null)
          _hash_ = _hash_ * 31+ this.nmoney.hashCode() ;
        if(this.sbilldate!=null)
          _hash_ = _hash_ * 31+ this.sbilldate.hashCode() ;
        if(this.sbudgetsubcode!=null)
          _hash_ = _hash_ * 31+ this.sbudgetsubcode.hashCode() ;
        if(this.sbudgetlevelcode!=null)
          _hash_ = _hash_ * 31+ this.sbudgetlevelcode.hashCode() ;
        if(this.sbudgettype!=null)
          _hash_ = _hash_ * 31+ this.sbudgettype.hashCode() ;
        if(this.sassitsign!=null)
          _hash_ = _hash_ * 31+ this.sassitsign.hashCode() ;
        if(this.staxpaycode!=null)
          _hash_ = _hash_ * 31+ this.staxpaycode.hashCode() ;
        if(this.staxpayname!=null)
          _hash_ = _hash_ * 31+ this.staxpayname.hashCode() ;
        if(this.spaybnkno!=null)
          _hash_ = _hash_ * 31+ this.spaybnkno.hashCode() ;
        if(this.spayacct!=null)
          _hash_ = _hash_ * 31+ this.spayacct.hashCode() ;
        if(this.spayopbnkno!=null)
          _hash_ = _hash_ * 31+ this.spayopbnkno.hashCode() ;
        if(this.staxstartdate!=null)
          _hash_ = _hash_ * 31+ this.staxstartdate.hashCode() ;
        if(this.staxenddate!=null)
          _hash_ = _hash_ * 31+ this.staxenddate.hashCode() ;
        if(this.sfilename!=null)
          _hash_ = _hash_ * 31+ this.sfilename.hashCode() ;
        if(this.sorgcode!=null)
          _hash_ = _hash_ * 31+ this.sorgcode.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TvInfileTmpTipsDto bean = new TvInfileTmpTipsDto();

          if (this.staxorgcode != null)
            bean.staxorgcode = new String(this.staxorgcode);
          if (this.strecode != null)
            bean.strecode = new String(this.strecode);
          if (this.nmoney != null)
            bean.nmoney = new BigDecimal(this.nmoney.toString());
          if (this.sbilldate != null)
            bean.sbilldate = new String(this.sbilldate);
          if (this.sbudgetsubcode != null)
            bean.sbudgetsubcode = new String(this.sbudgetsubcode);
          if (this.sbudgetlevelcode != null)
            bean.sbudgetlevelcode = new String(this.sbudgetlevelcode);
          if (this.sbudgettype != null)
            bean.sbudgettype = new String(this.sbudgettype);
          if (this.sassitsign != null)
            bean.sassitsign = new String(this.sassitsign);
          if (this.staxpaycode != null)
            bean.staxpaycode = new String(this.staxpaycode);
          if (this.staxpayname != null)
            bean.staxpayname = new String(this.staxpayname);
          if (this.spaybnkno != null)
            bean.spaybnkno = new String(this.spaybnkno);
          if (this.spayacct != null)
            bean.spayacct = new String(this.spayacct);
          if (this.spayopbnkno != null)
            bean.spayopbnkno = new String(this.spayopbnkno);
          if (this.staxstartdate != null)
            bean.staxstartdate = new String(this.staxstartdate);
          if (this.staxenddate != null)
            bean.staxenddate = new String(this.staxenddate);
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
        sb.append("TvInfileTmpTipsDto").append(sep);
        sb.append("[staxorgcode]").append(" = ").append(staxorgcode).append(sep);
        sb.append("[strecode]").append(" = ").append(strecode).append(sep);
        sb.append("[nmoney]").append(" = ").append(nmoney).append(sep);
        sb.append("[sbilldate]").append(" = ").append(sbilldate).append(sep);
        sb.append("[sbudgetsubcode]").append(" = ").append(sbudgetsubcode).append(sep);
        sb.append("[sbudgetlevelcode]").append(" = ").append(sbudgetlevelcode).append(sep);
        sb.append("[sbudgettype]").append(" = ").append(sbudgettype).append(sep);
        sb.append("[sassitsign]").append(" = ").append(sassitsign).append(sep);
        sb.append("[staxpaycode]").append(" = ").append(staxpaycode).append(sep);
        sb.append("[staxpayname]").append(" = ").append(staxpayname).append(sep);
        sb.append("[spaybnkno]").append(" = ").append(spaybnkno).append(sep);
        sb.append("[spayacct]").append(" = ").append(spayacct).append(sep);
        sb.append("[spayopbnkno]").append(" = ").append(spayopbnkno).append(sep);
        sb.append("[staxstartdate]").append(" = ").append(staxstartdate).append(sep);
        sb.append("[staxenddate]").append(" = ").append(staxenddate).append(sep);
        sb.append("[sfilename]").append(" = ").append(sfilename).append(sep);
        sb.append("[sorgcode]").append(" = ").append(sorgcode).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //check field S_TAXORGCODE
      if (this.getStaxorgcode()==null)
             sb.append("S_TAXORGCODE不能为空; ");
      if (this.getStaxorgcode()!=null)
             if (this.getStaxorgcode().getBytes().length > 30)
                sb.append("S_TAXORGCODE宽度不能超过 "+30+"个字符; ");
    
    //check field S_TRECODE
      if (this.getStrecode()==null)
             sb.append("S_TRECODE不能为空; ");
      if (this.getStrecode()!=null)
             if (this.getStrecode().getBytes().length > 10)
                sb.append("S_TRECODE宽度不能超过 "+10+"个字符; ");
    
    //check field N_MONEY
      if (this.getNmoney()==null)
             sb.append("N_MONEY不能为空; ");
      
    //check field S_BILLDATE
      if (this.getSbilldate()!=null)
             if (this.getSbilldate().getBytes().length > 8)
                sb.append("S_BILLDATE宽度不能超过 "+8+"个字符; ");
    
    //check field S_BUDGETSUBCODE
      if (this.getSbudgetsubcode()!=null)
             if (this.getSbudgetsubcode().getBytes().length > 30)
                sb.append("S_BUDGETSUBCODE宽度不能超过 "+30+"个字符; ");
    
    //check field S_BUDGETLEVELCODE
      if (this.getSbudgetlevelcode()!=null)
             if (this.getSbudgetlevelcode().getBytes().length > 1)
                sb.append("S_BUDGETLEVELCODE宽度不能超过 "+1+"个字符; ");
    
    //check field S_BUDGETTYPE
      if (this.getSbudgettype()==null)
             sb.append("S_BUDGETTYPE不能为空; ");
      if (this.getSbudgettype()!=null)
             if (this.getSbudgettype().getBytes().length > 1)
                sb.append("S_BUDGETTYPE宽度不能超过 "+1+"个字符; ");
    
    //check field S_ASSITSIGN
      if (this.getSassitsign()!=null)
             if (this.getSassitsign().getBytes().length > 35)
                sb.append("S_ASSITSIGN宽度不能超过 "+35+"个字符; ");
    
    //check field S_TAXPAYCODE
      if (this.getStaxpaycode()!=null)
             if (this.getStaxpaycode().getBytes().length > 20)
                sb.append("S_TAXPAYCODE宽度不能超过 "+20+"个字符; ");
    
    //check field S_TAXPAYNAME
      if (this.getStaxpayname()!=null)
             if (this.getStaxpayname().getBytes().length > 200)
                sb.append("S_TAXPAYNAME宽度不能超过 "+200+"个字符; ");
    
    //check field S_PAYBNKNO
      if (this.getSpaybnkno()!=null)
             if (this.getSpaybnkno().getBytes().length > 12)
                sb.append("S_PAYBNKNO宽度不能超过 "+12+"个字符; ");
    
    //check field S_PAYACCT
      if (this.getSpayacct()!=null)
             if (this.getSpayacct().getBytes().length > 32)
                sb.append("S_PAYACCT宽度不能超过 "+32+"个字符; ");
    
    //check field S_PAYOPBNKNO
      if (this.getSpayopbnkno()!=null)
             if (this.getSpayopbnkno().getBytes().length > 12)
                sb.append("S_PAYOPBNKNO宽度不能超过 "+12+"个字符; ");
    
    //check field S_TAXSTARTDATE
      if (this.getStaxstartdate()!=null)
             if (this.getStaxstartdate().getBytes().length > 8)
                sb.append("S_TAXSTARTDATE宽度不能超过 "+8+"个字符; ");
    
    //check field S_TAXENDDATE
      if (this.getStaxenddate()!=null)
             if (this.getStaxenddate().getBytes().length > 8)
                sb.append("S_TAXENDDATE宽度不能超过 "+8+"个字符; ");
    
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
    //check field S_TAXORGCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXORGCODE")) {
               if (this.getStaxorgcode()==null)
                    sb.append("S_TAXORGCODE 不能为空; ");
               if (this.getStaxorgcode()!=null)
                    if (this.getStaxorgcode().getBytes().length > 30)
                        sb.append("S_TAXORGCODE 宽度不能超过 "+30+"个字符");
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
    
    //check field N_MONEY
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("N_MONEY")) {
               if (this.getNmoney()==null)
                    sb.append("N_MONEY 不能为空; ");
               break;
         }
  }
    
    //check field S_BILLDATE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BILLDATE")) {
                 if (this.getSbilldate()!=null)
                    if (this.getSbilldate().getBytes().length > 8)
                        sb.append("S_BILLDATE 宽度不能超过 "+8+"个字符");
             break;
         }
  }
    
    //check field S_BUDGETSUBCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BUDGETSUBCODE")) {
                 if (this.getSbudgetsubcode()!=null)
                    if (this.getSbudgetsubcode().getBytes().length > 30)
                        sb.append("S_BUDGETSUBCODE 宽度不能超过 "+30+"个字符");
             break;
         }
  }
    
    //check field S_BUDGETLEVELCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BUDGETLEVELCODE")) {
                 if (this.getSbudgetlevelcode()!=null)
                    if (this.getSbudgetlevelcode().getBytes().length > 1)
                        sb.append("S_BUDGETLEVELCODE 宽度不能超过 "+1+"个字符");
             break;
         }
  }
    
    //check field S_BUDGETTYPE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BUDGETTYPE")) {
               if (this.getSbudgettype()==null)
                    sb.append("S_BUDGETTYPE 不能为空; ");
               if (this.getSbudgettype()!=null)
                    if (this.getSbudgettype().getBytes().length > 1)
                        sb.append("S_BUDGETTYPE 宽度不能超过 "+1+"个字符");
             break;
         }
  }
    
    //check field S_ASSITSIGN
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_ASSITSIGN")) {
                 if (this.getSassitsign()!=null)
                    if (this.getSassitsign().getBytes().length > 35)
                        sb.append("S_ASSITSIGN 宽度不能超过 "+35+"个字符");
             break;
         }
  }
    
    //check field S_TAXPAYCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXPAYCODE")) {
                 if (this.getStaxpaycode()!=null)
                    if (this.getStaxpaycode().getBytes().length > 20)
                        sb.append("S_TAXPAYCODE 宽度不能超过 "+20+"个字符");
             break;
         }
  }
    
    //check field S_TAXPAYNAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXPAYNAME")) {
                 if (this.getStaxpayname()!=null)
                    if (this.getStaxpayname().getBytes().length > 200)
                        sb.append("S_TAXPAYNAME 宽度不能超过 "+200+"个字符");
             break;
         }
  }
    
    //check field S_PAYBNKNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_PAYBNKNO")) {
                 if (this.getSpaybnkno()!=null)
                    if (this.getSpaybnkno().getBytes().length > 12)
                        sb.append("S_PAYBNKNO 宽度不能超过 "+12+"个字符");
             break;
         }
  }
    
    //check field S_PAYACCT
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_PAYACCT")) {
                 if (this.getSpayacct()!=null)
                    if (this.getSpayacct().getBytes().length > 32)
                        sb.append("S_PAYACCT 宽度不能超过 "+32+"个字符");
             break;
         }
  }
    
    //check field S_PAYOPBNKNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_PAYOPBNKNO")) {
                 if (this.getSpayopbnkno()!=null)
                    if (this.getSpayopbnkno().getBytes().length > 12)
                        sb.append("S_PAYOPBNKNO 宽度不能超过 "+12+"个字符");
             break;
         }
  }
    
    //check field S_TAXSTARTDATE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXSTARTDATE")) {
                 if (this.getStaxstartdate()!=null)
                    if (this.getStaxstartdate().getBytes().length > 8)
                        sb.append("S_TAXSTARTDATE 宽度不能超过 "+8+"个字符");
             break;
         }
  }
    
    //check field S_TAXENDDATE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXENDDATE")) {
                 if (this.getStaxenddate()!=null)
                    if (this.getStaxenddate().getBytes().length > 8)
                        sb.append("S_TAXENDDATE 宽度不能超过 "+8+"个字符");
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

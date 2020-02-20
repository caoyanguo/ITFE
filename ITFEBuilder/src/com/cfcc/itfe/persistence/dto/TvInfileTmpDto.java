    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TvInfileTmpPK;
/**
 * <p>Title: DTO of table: TV_INFILE_TMP</p>
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

public class TvInfileTmpDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  S_UNITCODE VARCHAR         */
    protected String sunitcode;
    /**
    *  S_RECVTRECODE CHARACTER   , NOT NULL       */
    protected String srecvtrecode;
    /**
    *  S_DESCTRECODE CHARACTER         */
    protected String sdesctrecode;
    /**
    *  S_TBS_TAXORGCODE VARCHAR         */
    protected String stbstaxorgcode;
    /**
    *  S_TAXTICKETNO VARCHAR         */
    protected String staxticketno;
    /**
    *  S_PAYBOOKKIND VARCHAR         */
    protected String spaybookkind;
    /**
    *  S_OPENACCBANKCODE VARCHAR         */
    protected String sopenaccbankcode;
    /**
    *  S_BUDGETLEVELCODE CHARACTER   , NOT NULL       */
    protected String sbudgetlevelcode;
    /**
    *  S_BUDGETTYPE CHARACTER   , NOT NULL       */
    protected String sbudgettype;
    /**
    *  S_BUDGETSUBCODE VARCHAR   , NOT NULL       */
    protected String sbudgetsubcode;
    /**
    *  S_TBS_ASSITSIGN VARCHAR         */
    protected String stbsassitsign;
    /**
    *  N_MONEY DECIMAL   , NOT NULL       */
    protected BigDecimal nmoney;
    /**
    *  S_FILENAME VARCHAR   , NOT NULL       */
    protected String sfilename;
    /**
    *  S_ORGCODE VARCHAR   , NOT NULL       */
    protected String sorgcode;
    /**
    *  S_DEALNO VARCHAR         */
    protected String sdealno;
    /**
    *  S_PACKAGENO VARCHAR         */
    protected String spackageno;
    /**
    *  S_TAXORGCODE VARCHAR         */
    protected String staxorgcode;
    /**
    *  S_ASSITSIGN VARCHAR         */
    protected String sassitsign;


/******************************************************
*
*  getter and setter
*
*******************************************************/


    public String getSunitcode()
    {
        return sunitcode;
    }
     /**
     *   Setter S_UNITCODE        * */
    public void setSunitcode(String _sunitcode) {
        this.sunitcode = _sunitcode;
    }


    public String getSrecvtrecode()
    {
        return srecvtrecode;
    }
     /**
     *   Setter S_RECVTRECODE , NOT NULL        * */
    public void setSrecvtrecode(String _srecvtrecode) {
        this.srecvtrecode = _srecvtrecode;
    }


    public String getSdesctrecode()
    {
        return sdesctrecode;
    }
     /**
     *   Setter S_DESCTRECODE        * */
    public void setSdesctrecode(String _sdesctrecode) {
        this.sdesctrecode = _sdesctrecode;
    }


    public String getStbstaxorgcode()
    {
        return stbstaxorgcode;
    }
     /**
     *   Setter S_TBS_TAXORGCODE        * */
    public void setStbstaxorgcode(String _stbstaxorgcode) {
        this.stbstaxorgcode = _stbstaxorgcode;
    }


    public String getStaxticketno()
    {
        return staxticketno;
    }
     /**
     *   Setter S_TAXTICKETNO        * */
    public void setStaxticketno(String _staxticketno) {
        this.staxticketno = _staxticketno;
    }


    public String getSpaybookkind()
    {
        return spaybookkind;
    }
     /**
     *   Setter S_PAYBOOKKIND        * */
    public void setSpaybookkind(String _spaybookkind) {
        this.spaybookkind = _spaybookkind;
    }


    public String getSopenaccbankcode()
    {
        return sopenaccbankcode;
    }
     /**
     *   Setter S_OPENACCBANKCODE        * */
    public void setSopenaccbankcode(String _sopenaccbankcode) {
        this.sopenaccbankcode = _sopenaccbankcode;
    }


    public String getSbudgetlevelcode()
    {
        return sbudgetlevelcode;
    }
     /**
     *   Setter S_BUDGETLEVELCODE , NOT NULL        * */
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


    public String getSbudgetsubcode()
    {
        return sbudgetsubcode;
    }
     /**
     *   Setter S_BUDGETSUBCODE , NOT NULL        * */
    public void setSbudgetsubcode(String _sbudgetsubcode) {
        this.sbudgetsubcode = _sbudgetsubcode;
    }


    public String getStbsassitsign()
    {
        return stbsassitsign;
    }
     /**
     *   Setter S_TBS_ASSITSIGN        * */
    public void setStbsassitsign(String _stbsassitsign) {
        this.stbsassitsign = _stbsassitsign;
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


    public String getSdealno()
    {
        return sdealno;
    }
     /**
     *   Setter S_DEALNO        * */
    public void setSdealno(String _sdealno) {
        this.sdealno = _sdealno;
    }


    public String getSpackageno()
    {
        return spackageno;
    }
     /**
     *   Setter S_PACKAGENO        * */
    public void setSpackageno(String _spackageno) {
        this.spackageno = _spackageno;
    }


    public String getStaxorgcode()
    {
        return staxorgcode;
    }
     /**
     *   Setter S_TAXORGCODE        * */
    public void setStaxorgcode(String _staxorgcode) {
        this.staxorgcode = _staxorgcode;
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




/******************************************************
*
*  Get Column Name
*
*******************************************************/
    /**
    *   Getter S_UNITCODE       * */
    public static String  columnSunitcode()
    {
        return "S_UNITCODE";
    }
   
    /**
    *   Getter S_RECVTRECODE , NOT NULL       * */
    public static String  columnSrecvtrecode()
    {
        return "S_RECVTRECODE";
    }
   
    /**
    *   Getter S_DESCTRECODE       * */
    public static String  columnSdesctrecode()
    {
        return "S_DESCTRECODE";
    }
   
    /**
    *   Getter S_TBS_TAXORGCODE       * */
    public static String  columnStbstaxorgcode()
    {
        return "S_TBS_TAXORGCODE";
    }
   
    /**
    *   Getter S_TAXTICKETNO       * */
    public static String  columnStaxticketno()
    {
        return "S_TAXTICKETNO";
    }
   
    /**
    *   Getter S_PAYBOOKKIND       * */
    public static String  columnSpaybookkind()
    {
        return "S_PAYBOOKKIND";
    }
   
    /**
    *   Getter S_OPENACCBANKCODE       * */
    public static String  columnSopenaccbankcode()
    {
        return "S_OPENACCBANKCODE";
    }
   
    /**
    *   Getter S_BUDGETLEVELCODE , NOT NULL       * */
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
    *   Getter S_BUDGETSUBCODE , NOT NULL       * */
    public static String  columnSbudgetsubcode()
    {
        return "S_BUDGETSUBCODE";
    }
   
    /**
    *   Getter S_TBS_ASSITSIGN       * */
    public static String  columnStbsassitsign()
    {
        return "S_TBS_ASSITSIGN";
    }
   
    /**
    *   Getter N_MONEY , NOT NULL       * */
    public static String  columnNmoney()
    {
        return "N_MONEY";
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
    *   Getter S_DEALNO       * */
    public static String  columnSdealno()
    {
        return "S_DEALNO";
    }
   
    /**
    *   Getter S_PACKAGENO       * */
    public static String  columnSpackageno()
    {
        return "S_PACKAGENO";
    }
   
    /**
    *   Getter S_TAXORGCODE       * */
    public static String  columnStaxorgcode()
    {
        return "S_TAXORGCODE";
    }
   
    /**
    *   Getter S_ASSITSIGN       * */
    public static String  columnSassitsign()
    {
        return "S_ASSITSIGN";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "TV_INFILE_TMP";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[18];        
        columnNames[0]="S_UNITCODE";
        columnNames[1]="S_RECVTRECODE";
        columnNames[2]="S_DESCTRECODE";
        columnNames[3]="S_TBS_TAXORGCODE";
        columnNames[4]="S_TAXTICKETNO";
        columnNames[5]="S_PAYBOOKKIND";
        columnNames[6]="S_OPENACCBANKCODE";
        columnNames[7]="S_BUDGETLEVELCODE";
        columnNames[8]="S_BUDGETTYPE";
        columnNames[9]="S_BUDGETSUBCODE";
        columnNames[10]="S_TBS_ASSITSIGN";
        columnNames[11]="N_MONEY";
        columnNames[12]="S_FILENAME";
        columnNames[13]="S_ORGCODE";
        columnNames[14]="S_DEALNO";
        columnNames[15]="S_PACKAGENO";
        columnNames[16]="S_TAXORGCODE";
        columnNames[17]="S_ASSITSIGN";
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

        if (obj == null || !(obj instanceof TvInfileTmpDto))
            return false;

        TvInfileTmpDto bean = (TvInfileTmpDto) obj;


      //compare field sunitcode
      if((this.sunitcode==null && bean.sunitcode!=null) || (this.sunitcode!=null && bean.sunitcode==null))
          return false;
        else if(this.sunitcode==null && bean.sunitcode==null){
        }
        else{
          if(!bean.sunitcode.equals(this.sunitcode))
               return false;
     }
      //compare field srecvtrecode
      if((this.srecvtrecode==null && bean.srecvtrecode!=null) || (this.srecvtrecode!=null && bean.srecvtrecode==null))
          return false;
        else if(this.srecvtrecode==null && bean.srecvtrecode==null){
        }
        else{
          if(!bean.srecvtrecode.equals(this.srecvtrecode))
               return false;
     }
      //compare field sdesctrecode
      if((this.sdesctrecode==null && bean.sdesctrecode!=null) || (this.sdesctrecode!=null && bean.sdesctrecode==null))
          return false;
        else if(this.sdesctrecode==null && bean.sdesctrecode==null){
        }
        else{
          if(!bean.sdesctrecode.equals(this.sdesctrecode))
               return false;
     }
      //compare field stbstaxorgcode
      if((this.stbstaxorgcode==null && bean.stbstaxorgcode!=null) || (this.stbstaxorgcode!=null && bean.stbstaxorgcode==null))
          return false;
        else if(this.stbstaxorgcode==null && bean.stbstaxorgcode==null){
        }
        else{
          if(!bean.stbstaxorgcode.equals(this.stbstaxorgcode))
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
      //compare field spaybookkind
      if((this.spaybookkind==null && bean.spaybookkind!=null) || (this.spaybookkind!=null && bean.spaybookkind==null))
          return false;
        else if(this.spaybookkind==null && bean.spaybookkind==null){
        }
        else{
          if(!bean.spaybookkind.equals(this.spaybookkind))
               return false;
     }
      //compare field sopenaccbankcode
      if((this.sopenaccbankcode==null && bean.sopenaccbankcode!=null) || (this.sopenaccbankcode!=null && bean.sopenaccbankcode==null))
          return false;
        else if(this.sopenaccbankcode==null && bean.sopenaccbankcode==null){
        }
        else{
          if(!bean.sopenaccbankcode.equals(this.sopenaccbankcode))
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
      //compare field sbudgetsubcode
      if((this.sbudgetsubcode==null && bean.sbudgetsubcode!=null) || (this.sbudgetsubcode!=null && bean.sbudgetsubcode==null))
          return false;
        else if(this.sbudgetsubcode==null && bean.sbudgetsubcode==null){
        }
        else{
          if(!bean.sbudgetsubcode.equals(this.sbudgetsubcode))
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
      //compare field nmoney
      if((this.nmoney==null && bean.nmoney!=null) || (this.nmoney!=null && bean.nmoney==null))
          return false;
        else if(this.nmoney==null && bean.nmoney==null){
        }
        else{
          if(!bean.nmoney.equals(this.nmoney))
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
      //compare field sdealno
      if((this.sdealno==null && bean.sdealno!=null) || (this.sdealno!=null && bean.sdealno==null))
          return false;
        else if(this.sdealno==null && bean.sdealno==null){
        }
        else{
          if(!bean.sdealno.equals(this.sdealno))
               return false;
     }
      //compare field spackageno
      if((this.spackageno==null && bean.spackageno!=null) || (this.spackageno!=null && bean.spackageno==null))
          return false;
        else if(this.spackageno==null && bean.spackageno==null){
        }
        else{
          if(!bean.spackageno.equals(this.spackageno))
               return false;
     }
      //compare field staxorgcode
      if((this.staxorgcode==null && bean.staxorgcode!=null) || (this.staxorgcode!=null && bean.staxorgcode==null))
          return false;
        else if(this.staxorgcode==null && bean.staxorgcode==null){
        }
        else{
          if(!bean.staxorgcode.equals(this.staxorgcode))
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



        return true;
    }

    /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
  
		int _hash_ = 1;
		
        if(this.sunitcode!=null)
          _hash_ = _hash_ * 31+ this.sunitcode.hashCode() ;
        if(this.srecvtrecode!=null)
          _hash_ = _hash_ * 31+ this.srecvtrecode.hashCode() ;
        if(this.sdesctrecode!=null)
          _hash_ = _hash_ * 31+ this.sdesctrecode.hashCode() ;
        if(this.stbstaxorgcode!=null)
          _hash_ = _hash_ * 31+ this.stbstaxorgcode.hashCode() ;
        if(this.staxticketno!=null)
          _hash_ = _hash_ * 31+ this.staxticketno.hashCode() ;
        if(this.spaybookkind!=null)
          _hash_ = _hash_ * 31+ this.spaybookkind.hashCode() ;
        if(this.sopenaccbankcode!=null)
          _hash_ = _hash_ * 31+ this.sopenaccbankcode.hashCode() ;
        if(this.sbudgetlevelcode!=null)
          _hash_ = _hash_ * 31+ this.sbudgetlevelcode.hashCode() ;
        if(this.sbudgettype!=null)
          _hash_ = _hash_ * 31+ this.sbudgettype.hashCode() ;
        if(this.sbudgetsubcode!=null)
          _hash_ = _hash_ * 31+ this.sbudgetsubcode.hashCode() ;
        if(this.stbsassitsign!=null)
          _hash_ = _hash_ * 31+ this.stbsassitsign.hashCode() ;
        if(this.nmoney!=null)
          _hash_ = _hash_ * 31+ this.nmoney.hashCode() ;
        if(this.sfilename!=null)
          _hash_ = _hash_ * 31+ this.sfilename.hashCode() ;
        if(this.sorgcode!=null)
          _hash_ = _hash_ * 31+ this.sorgcode.hashCode() ;
        if(this.sdealno!=null)
          _hash_ = _hash_ * 31+ this.sdealno.hashCode() ;
        if(this.spackageno!=null)
          _hash_ = _hash_ * 31+ this.spackageno.hashCode() ;
        if(this.staxorgcode!=null)
          _hash_ = _hash_ * 31+ this.staxorgcode.hashCode() ;
        if(this.sassitsign!=null)
          _hash_ = _hash_ * 31+ this.sassitsign.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TvInfileTmpDto bean = new TvInfileTmpDto();

          if (this.sunitcode != null)
            bean.sunitcode = new String(this.sunitcode);
          if (this.srecvtrecode != null)
            bean.srecvtrecode = new String(this.srecvtrecode);
          if (this.sdesctrecode != null)
            bean.sdesctrecode = new String(this.sdesctrecode);
          if (this.stbstaxorgcode != null)
            bean.stbstaxorgcode = new String(this.stbstaxorgcode);
          if (this.staxticketno != null)
            bean.staxticketno = new String(this.staxticketno);
          if (this.spaybookkind != null)
            bean.spaybookkind = new String(this.spaybookkind);
          if (this.sopenaccbankcode != null)
            bean.sopenaccbankcode = new String(this.sopenaccbankcode);
          if (this.sbudgetlevelcode != null)
            bean.sbudgetlevelcode = new String(this.sbudgetlevelcode);
          if (this.sbudgettype != null)
            bean.sbudgettype = new String(this.sbudgettype);
          if (this.sbudgetsubcode != null)
            bean.sbudgetsubcode = new String(this.sbudgetsubcode);
          if (this.stbsassitsign != null)
            bean.stbsassitsign = new String(this.stbsassitsign);
          if (this.nmoney != null)
            bean.nmoney = new BigDecimal(this.nmoney.toString());
          if (this.sfilename != null)
            bean.sfilename = new String(this.sfilename);
          if (this.sorgcode != null)
            bean.sorgcode = new String(this.sorgcode);
          if (this.sdealno != null)
            bean.sdealno = new String(this.sdealno);
          if (this.spackageno != null)
            bean.spackageno = new String(this.spackageno);
          if (this.staxorgcode != null)
            bean.staxorgcode = new String(this.staxorgcode);
          if (this.sassitsign != null)
            bean.sassitsign = new String(this.sassitsign);
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TvInfileTmpDto").append(sep);
        sb.append("[sunitcode]").append(" = ").append(sunitcode).append(sep);
        sb.append("[srecvtrecode]").append(" = ").append(srecvtrecode).append(sep);
        sb.append("[sdesctrecode]").append(" = ").append(sdesctrecode).append(sep);
        sb.append("[stbstaxorgcode]").append(" = ").append(stbstaxorgcode).append(sep);
        sb.append("[staxticketno]").append(" = ").append(staxticketno).append(sep);
        sb.append("[spaybookkind]").append(" = ").append(spaybookkind).append(sep);
        sb.append("[sopenaccbankcode]").append(" = ").append(sopenaccbankcode).append(sep);
        sb.append("[sbudgetlevelcode]").append(" = ").append(sbudgetlevelcode).append(sep);
        sb.append("[sbudgettype]").append(" = ").append(sbudgettype).append(sep);
        sb.append("[sbudgetsubcode]").append(" = ").append(sbudgetsubcode).append(sep);
        sb.append("[stbsassitsign]").append(" = ").append(stbsassitsign).append(sep);
        sb.append("[nmoney]").append(" = ").append(nmoney).append(sep);
        sb.append("[sfilename]").append(" = ").append(sfilename).append(sep);
        sb.append("[sorgcode]").append(" = ").append(sorgcode).append(sep);
        sb.append("[sdealno]").append(" = ").append(sdealno).append(sep);
        sb.append("[spackageno]").append(" = ").append(spackageno).append(sep);
        sb.append("[staxorgcode]").append(" = ").append(staxorgcode).append(sep);
        sb.append("[sassitsign]").append(" = ").append(sassitsign).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //check field S_UNITCODE
      if (this.getSunitcode()!=null)
             if (this.getSunitcode().getBytes().length > 15)
                sb.append("S_UNITCODE宽度不能超过 "+15+"个字符; ");
    
    //check field S_RECVTRECODE
      if (this.getSrecvtrecode()==null)
             sb.append("S_RECVTRECODE不能为空; ");
      if (this.getSrecvtrecode()!=null)
             if (this.getSrecvtrecode().getBytes().length > 10)
                sb.append("S_RECVTRECODE宽度不能超过 "+10+"个字符; ");
    
    //check field S_DESCTRECODE
      if (this.getSdesctrecode()!=null)
             if (this.getSdesctrecode().getBytes().length > 10)
                sb.append("S_DESCTRECODE宽度不能超过 "+10+"个字符; ");
    
    //check field S_TBS_TAXORGCODE
      if (this.getStbstaxorgcode()!=null)
             if (this.getStbstaxorgcode().getBytes().length > 30)
                sb.append("S_TBS_TAXORGCODE宽度不能超过 "+30+"个字符; ");
    
    //check field S_TAXTICKETNO
      if (this.getStaxticketno()!=null)
             if (this.getStaxticketno().getBytes().length > 30)
                sb.append("S_TAXTICKETNO宽度不能超过 "+30+"个字符; ");
    
    //check field S_PAYBOOKKIND
      if (this.getSpaybookkind()!=null)
             if (this.getSpaybookkind().getBytes().length > 2)
                sb.append("S_PAYBOOKKIND宽度不能超过 "+2+"个字符; ");
    
    //check field S_OPENACCBANKCODE
      if (this.getSopenaccbankcode()!=null)
             if (this.getSopenaccbankcode().getBytes().length > 12)
                sb.append("S_OPENACCBANKCODE宽度不能超过 "+12+"个字符; ");
    
    //check field S_BUDGETLEVELCODE
      if (this.getSbudgetlevelcode()==null)
             sb.append("S_BUDGETLEVELCODE不能为空; ");
      if (this.getSbudgetlevelcode()!=null)
             if (this.getSbudgetlevelcode().getBytes().length > 1)
                sb.append("S_BUDGETLEVELCODE宽度不能超过 "+1+"个字符; ");
    
    //check field S_BUDGETTYPE
      if (this.getSbudgettype()==null)
             sb.append("S_BUDGETTYPE不能为空; ");
      if (this.getSbudgettype()!=null)
             if (this.getSbudgettype().getBytes().length > 1)
                sb.append("S_BUDGETTYPE宽度不能超过 "+1+"个字符; ");
    
    //check field S_BUDGETSUBCODE
      if (this.getSbudgetsubcode()==null)
             sb.append("S_BUDGETSUBCODE不能为空; ");
      if (this.getSbudgetsubcode()!=null)
             if (this.getSbudgetsubcode().getBytes().length > 30)
                sb.append("S_BUDGETSUBCODE宽度不能超过 "+30+"个字符; ");
    
    //check field S_TBS_ASSITSIGN
      if (this.getStbsassitsign()!=null)
             if (this.getStbsassitsign().getBytes().length > 35)
                sb.append("S_TBS_ASSITSIGN宽度不能超过 "+35+"个字符; ");
    
    //check field N_MONEY
      if (this.getNmoney()==null)
             sb.append("N_MONEY不能为空; ");
      
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
    
    //check field S_DEALNO
      if (this.getSdealno()!=null)
             if (this.getSdealno().getBytes().length > 8)
                sb.append("S_DEALNO宽度不能超过 "+8+"个字符; ");
    
    //check field S_PACKAGENO
      if (this.getSpackageno()!=null)
             if (this.getSpackageno().getBytes().length > 8)
                sb.append("S_PACKAGENO宽度不能超过 "+8+"个字符; ");
    
    //check field S_TAXORGCODE
      if (this.getStaxorgcode()!=null)
             if (this.getStaxorgcode().getBytes().length > 30)
                sb.append("S_TAXORGCODE宽度不能超过 "+30+"个字符; ");
    
    //check field S_ASSITSIGN
      if (this.getSassitsign()!=null)
             if (this.getSassitsign().getBytes().length > 35)
                sb.append("S_ASSITSIGN宽度不能超过 "+35+"个字符; ");
    

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
    //check field S_UNITCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_UNITCODE")) {
                 if (this.getSunitcode()!=null)
                    if (this.getSunitcode().getBytes().length > 15)
                        sb.append("S_UNITCODE 宽度不能超过 "+15+"个字符");
             break;
         }
  }
    
    //check field S_RECVTRECODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_RECVTRECODE")) {
               if (this.getSrecvtrecode()==null)
                    sb.append("S_RECVTRECODE 不能为空; ");
               if (this.getSrecvtrecode()!=null)
                    if (this.getSrecvtrecode().getBytes().length > 10)
                        sb.append("S_RECVTRECODE 宽度不能超过 "+10+"个字符");
             break;
         }
  }
    
    //check field S_DESCTRECODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_DESCTRECODE")) {
                 if (this.getSdesctrecode()!=null)
                    if (this.getSdesctrecode().getBytes().length > 10)
                        sb.append("S_DESCTRECODE 宽度不能超过 "+10+"个字符");
             break;
         }
  }
    
    //check field S_TBS_TAXORGCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TBS_TAXORGCODE")) {
                 if (this.getStbstaxorgcode()!=null)
                    if (this.getStbstaxorgcode().getBytes().length > 30)
                        sb.append("S_TBS_TAXORGCODE 宽度不能超过 "+30+"个字符");
             break;
         }
  }
    
    //check field S_TAXTICKETNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXTICKETNO")) {
                 if (this.getStaxticketno()!=null)
                    if (this.getStaxticketno().getBytes().length > 30)
                        sb.append("S_TAXTICKETNO 宽度不能超过 "+30+"个字符");
             break;
         }
  }
    
    //check field S_PAYBOOKKIND
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_PAYBOOKKIND")) {
                 if (this.getSpaybookkind()!=null)
                    if (this.getSpaybookkind().getBytes().length > 2)
                        sb.append("S_PAYBOOKKIND 宽度不能超过 "+2+"个字符");
             break;
         }
  }
    
    //check field S_OPENACCBANKCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_OPENACCBANKCODE")) {
                 if (this.getSopenaccbankcode()!=null)
                    if (this.getSopenaccbankcode().getBytes().length > 12)
                        sb.append("S_OPENACCBANKCODE 宽度不能超过 "+12+"个字符");
             break;
         }
  }
    
    //check field S_BUDGETLEVELCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BUDGETLEVELCODE")) {
               if (this.getSbudgetlevelcode()==null)
                    sb.append("S_BUDGETLEVELCODE 不能为空; ");
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
    
    //check field S_TBS_ASSITSIGN
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TBS_ASSITSIGN")) {
                 if (this.getStbsassitsign()!=null)
                    if (this.getStbsassitsign().getBytes().length > 35)
                        sb.append("S_TBS_ASSITSIGN 宽度不能超过 "+35+"个字符");
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
    
    //check field S_DEALNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_DEALNO")) {
                 if (this.getSdealno()!=null)
                    if (this.getSdealno().getBytes().length > 8)
                        sb.append("S_DEALNO 宽度不能超过 "+8+"个字符");
             break;
         }
  }
    
    //check field S_PACKAGENO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_PACKAGENO")) {
                 if (this.getSpackageno()!=null)
                    if (this.getSpackageno().getBytes().length > 8)
                        sb.append("S_PACKAGENO 宽度不能超过 "+8+"个字符");
             break;
         }
  }
    
    //check field S_TAXORGCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXORGCODE")) {
                 if (this.getStaxorgcode()!=null)
                    if (this.getStaxorgcode().getBytes().length > 30)
                        sb.append("S_TAXORGCODE 宽度不能超过 "+30+"个字符");
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

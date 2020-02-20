    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TsPaybankPK;
/**
 * <p>Title: DTO of table: TS_PAYBANK</p>
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

public class TsPaybankDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  S_ORGCODE VARCHAR , PK   , NOT NULL       */
    protected String sorgcode;
    /**
    *  S_BANKNO VARCHAR , PK   , NOT NULL       */
    protected String sbankno;
    /**
    *  S_BANKNAME VARCHAR         */
    protected String sbankname;
    /**
    *  S_PAYBANKNO VARCHAR   , NOT NULL       */
    protected String spaybankno;
    /**
    *  S_PAYBANKNAME VARCHAR         */
    protected String spaybankname;
    /**
    *  I_MODICOUNT INTEGER         */
    protected Integer imodicount;
    /**
    *  S_STATE CHARACTER         */
    protected String sstate;
    /**
    *  D_AFFDATE DATE         */
    protected Date daffdate;
    /**
    *  C_BNKCLASS CHARACTER         */
    protected String cbnkclass;
    /**
    *  S_OFCITY VARCHAR         */
    protected String sofcity;
    /**
    *  C_BNKCLSNO CHARACTER         */
    protected String cbnkclsno;
    /**
    *  C_BNKACCTSTA CHARACTER         */
    protected String cbnkacctsta;
    /**
    *  C_BNKPOSTCODE CHARACTER         */
    protected String cbnkpostcode;
    /**
    *  S_BNKADDR VARCHAR         */
    protected String sbnkaddr;
    /**
    *  C_OPTTYPE CHARACTER         */
    protected String copttype;
    /**
    *  S_BNKTEL VARCHAR         */
    protected String sbnktel;
    /**
    *  S_ADDCOLUMN1 VARCHAR         */
    protected String saddcolumn1;
    /**
    *  S_ADDCOLUMN2 VARCHAR         */
    protected String saddcolumn2;


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


    public String getSbankno()
    {
        return sbankno;
    }
     /**
     *   Setter S_BANKNO, PK , NOT NULL        * */
    public void setSbankno(String _sbankno) {
        this.sbankno = _sbankno;
    }


    public String getSbankname()
    {
        return sbankname;
    }
     /**
     *   Setter S_BANKNAME        * */
    public void setSbankname(String _sbankname) {
        this.sbankname = _sbankname;
    }


    public String getSpaybankno()
    {
        return spaybankno;
    }
     /**
     *   Setter S_PAYBANKNO , NOT NULL        * */
    public void setSpaybankno(String _spaybankno) {
        this.spaybankno = _spaybankno;
    }


    public String getSpaybankname()
    {
        return spaybankname;
    }
     /**
     *   Setter S_PAYBANKNAME        * */
    public void setSpaybankname(String _spaybankname) {
        this.spaybankname = _spaybankname;
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


    public String getSstate()
    {
        return sstate;
    }
     /**
     *   Setter S_STATE        * */
    public void setSstate(String _sstate) {
        this.sstate = _sstate;
    }


    public Date getDaffdate()
    {
        return daffdate;
    }
     /**
     *   Setter D_AFFDATE        * */
    public void setDaffdate(Date _daffdate) {
        this.daffdate = _daffdate;
    }


    public String getCbnkclass()
    {
        return cbnkclass;
    }
     /**
     *   Setter C_BNKCLASS        * */
    public void setCbnkclass(String _cbnkclass) {
        this.cbnkclass = _cbnkclass;
    }


    public String getSofcity()
    {
        return sofcity;
    }
     /**
     *   Setter S_OFCITY        * */
    public void setSofcity(String _sofcity) {
        this.sofcity = _sofcity;
    }


    public String getCbnkclsno()
    {
        return cbnkclsno;
    }
     /**
     *   Setter C_BNKCLSNO        * */
    public void setCbnkclsno(String _cbnkclsno) {
        this.cbnkclsno = _cbnkclsno;
    }


    public String getCbnkacctsta()
    {
        return cbnkacctsta;
    }
     /**
     *   Setter C_BNKACCTSTA        * */
    public void setCbnkacctsta(String _cbnkacctsta) {
        this.cbnkacctsta = _cbnkacctsta;
    }


    public String getCbnkpostcode()
    {
        return cbnkpostcode;
    }
     /**
     *   Setter C_BNKPOSTCODE        * */
    public void setCbnkpostcode(String _cbnkpostcode) {
        this.cbnkpostcode = _cbnkpostcode;
    }


    public String getSbnkaddr()
    {
        return sbnkaddr;
    }
     /**
     *   Setter S_BNKADDR        * */
    public void setSbnkaddr(String _sbnkaddr) {
        this.sbnkaddr = _sbnkaddr;
    }


    public String getCopttype()
    {
        return copttype;
    }
     /**
     *   Setter C_OPTTYPE        * */
    public void setCopttype(String _copttype) {
        this.copttype = _copttype;
    }


    public String getSbnktel()
    {
        return sbnktel;
    }
     /**
     *   Setter S_BNKTEL        * */
    public void setSbnktel(String _sbnktel) {
        this.sbnktel = _sbnktel;
    }


    public String getSaddcolumn1()
    {
        return saddcolumn1;
    }
     /**
     *   Setter S_ADDCOLUMN1        * */
    public void setSaddcolumn1(String _saddcolumn1) {
        this.saddcolumn1 = _saddcolumn1;
    }


    public String getSaddcolumn2()
    {
        return saddcolumn2;
    }
     /**
     *   Setter S_ADDCOLUMN2        * */
    public void setSaddcolumn2(String _saddcolumn2) {
        this.saddcolumn2 = _saddcolumn2;
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
    *   Getter S_BANKNO, PK , NOT NULL       * */
    public static String  columnSbankno()
    {
        return "S_BANKNO";
    }
   
    /**
    *   Getter S_BANKNAME       * */
    public static String  columnSbankname()
    {
        return "S_BANKNAME";
    }
   
    /**
    *   Getter S_PAYBANKNO , NOT NULL       * */
    public static String  columnSpaybankno()
    {
        return "S_PAYBANKNO";
    }
   
    /**
    *   Getter S_PAYBANKNAME       * */
    public static String  columnSpaybankname()
    {
        return "S_PAYBANKNAME";
    }
   
    /**
    *   Getter I_MODICOUNT       * */
    public static String  columnImodicount()
    {
        return "I_MODICOUNT";
    }
   
    /**
    *   Getter S_STATE       * */
    public static String  columnSstate()
    {
        return "S_STATE";
    }
   
    /**
    *   Getter D_AFFDATE       * */
    public static String  columnDaffdate()
    {
        return "D_AFFDATE";
    }
   
    /**
    *   Getter C_BNKCLASS       * */
    public static String  columnCbnkclass()
    {
        return "C_BNKCLASS";
    }
   
    /**
    *   Getter S_OFCITY       * */
    public static String  columnSofcity()
    {
        return "S_OFCITY";
    }
   
    /**
    *   Getter C_BNKCLSNO       * */
    public static String  columnCbnkclsno()
    {
        return "C_BNKCLSNO";
    }
   
    /**
    *   Getter C_BNKACCTSTA       * */
    public static String  columnCbnkacctsta()
    {
        return "C_BNKACCTSTA";
    }
   
    /**
    *   Getter C_BNKPOSTCODE       * */
    public static String  columnCbnkpostcode()
    {
        return "C_BNKPOSTCODE";
    }
   
    /**
    *   Getter S_BNKADDR       * */
    public static String  columnSbnkaddr()
    {
        return "S_BNKADDR";
    }
   
    /**
    *   Getter C_OPTTYPE       * */
    public static String  columnCopttype()
    {
        return "C_OPTTYPE";
    }
   
    /**
    *   Getter S_BNKTEL       * */
    public static String  columnSbnktel()
    {
        return "S_BNKTEL";
    }
   
    /**
    *   Getter S_ADDCOLUMN1       * */
    public static String  columnSaddcolumn1()
    {
        return "S_ADDCOLUMN1";
    }
   
    /**
    *   Getter S_ADDCOLUMN2       * */
    public static String  columnSaddcolumn2()
    {
        return "S_ADDCOLUMN2";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "TS_PAYBANK";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[18];        
        columnNames[0]="S_ORGCODE";
        columnNames[1]="S_BANKNO";
        columnNames[2]="S_BANKNAME";
        columnNames[3]="S_PAYBANKNO";
        columnNames[4]="S_PAYBANKNAME";
        columnNames[5]="I_MODICOUNT";
        columnNames[6]="S_STATE";
        columnNames[7]="D_AFFDATE";
        columnNames[8]="C_BNKCLASS";
        columnNames[9]="S_OFCITY";
        columnNames[10]="C_BNKCLSNO";
        columnNames[11]="C_BNKACCTSTA";
        columnNames[12]="C_BNKPOSTCODE";
        columnNames[13]="S_BNKADDR";
        columnNames[14]="C_OPTTYPE";
        columnNames[15]="S_BNKTEL";
        columnNames[16]="S_ADDCOLUMN1";
        columnNames[17]="S_ADDCOLUMN2";
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

        if (obj == null || !(obj instanceof TsPaybankDto))
            return false;

        TsPaybankDto bean = (TsPaybankDto) obj;


      //compare field sorgcode
      if((this.sorgcode==null && bean.sorgcode!=null) || (this.sorgcode!=null && bean.sorgcode==null))
          return false;
        else if(this.sorgcode==null && bean.sorgcode==null){
        }
        else{
          if(!bean.sorgcode.equals(this.sorgcode))
               return false;
     }
      //compare field sbankno
      if((this.sbankno==null && bean.sbankno!=null) || (this.sbankno!=null && bean.sbankno==null))
          return false;
        else if(this.sbankno==null && bean.sbankno==null){
        }
        else{
          if(!bean.sbankno.equals(this.sbankno))
               return false;
     }
      //compare field sbankname
      if((this.sbankname==null && bean.sbankname!=null) || (this.sbankname!=null && bean.sbankname==null))
          return false;
        else if(this.sbankname==null && bean.sbankname==null){
        }
        else{
          if(!bean.sbankname.equals(this.sbankname))
               return false;
     }
      //compare field spaybankno
      if((this.spaybankno==null && bean.spaybankno!=null) || (this.spaybankno!=null && bean.spaybankno==null))
          return false;
        else if(this.spaybankno==null && bean.spaybankno==null){
        }
        else{
          if(!bean.spaybankno.equals(this.spaybankno))
               return false;
     }
      //compare field spaybankname
      if((this.spaybankname==null && bean.spaybankname!=null) || (this.spaybankname!=null && bean.spaybankname==null))
          return false;
        else if(this.spaybankname==null && bean.spaybankname==null){
        }
        else{
          if(!bean.spaybankname.equals(this.spaybankname))
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
      //compare field sstate
      if((this.sstate==null && bean.sstate!=null) || (this.sstate!=null && bean.sstate==null))
          return false;
        else if(this.sstate==null && bean.sstate==null){
        }
        else{
          if(!bean.sstate.equals(this.sstate))
               return false;
     }
      //compare field daffdate
      if((this.daffdate==null && bean.daffdate!=null) || (this.daffdate!=null && bean.daffdate==null))
          return false;
        else if(this.daffdate==null && bean.daffdate==null){
        }
        else{
          if(!bean.daffdate.equals(this.daffdate))
               return false;
     }
      //compare field cbnkclass
      if((this.cbnkclass==null && bean.cbnkclass!=null) || (this.cbnkclass!=null && bean.cbnkclass==null))
          return false;
        else if(this.cbnkclass==null && bean.cbnkclass==null){
        }
        else{
          if(!bean.cbnkclass.equals(this.cbnkclass))
               return false;
     }
      //compare field sofcity
      if((this.sofcity==null && bean.sofcity!=null) || (this.sofcity!=null && bean.sofcity==null))
          return false;
        else if(this.sofcity==null && bean.sofcity==null){
        }
        else{
          if(!bean.sofcity.equals(this.sofcity))
               return false;
     }
      //compare field cbnkclsno
      if((this.cbnkclsno==null && bean.cbnkclsno!=null) || (this.cbnkclsno!=null && bean.cbnkclsno==null))
          return false;
        else if(this.cbnkclsno==null && bean.cbnkclsno==null){
        }
        else{
          if(!bean.cbnkclsno.equals(this.cbnkclsno))
               return false;
     }
      //compare field cbnkacctsta
      if((this.cbnkacctsta==null && bean.cbnkacctsta!=null) || (this.cbnkacctsta!=null && bean.cbnkacctsta==null))
          return false;
        else if(this.cbnkacctsta==null && bean.cbnkacctsta==null){
        }
        else{
          if(!bean.cbnkacctsta.equals(this.cbnkacctsta))
               return false;
     }
      //compare field cbnkpostcode
      if((this.cbnkpostcode==null && bean.cbnkpostcode!=null) || (this.cbnkpostcode!=null && bean.cbnkpostcode==null))
          return false;
        else if(this.cbnkpostcode==null && bean.cbnkpostcode==null){
        }
        else{
          if(!bean.cbnkpostcode.equals(this.cbnkpostcode))
               return false;
     }
      //compare field sbnkaddr
      if((this.sbnkaddr==null && bean.sbnkaddr!=null) || (this.sbnkaddr!=null && bean.sbnkaddr==null))
          return false;
        else if(this.sbnkaddr==null && bean.sbnkaddr==null){
        }
        else{
          if(!bean.sbnkaddr.equals(this.sbnkaddr))
               return false;
     }
      //compare field copttype
      if((this.copttype==null && bean.copttype!=null) || (this.copttype!=null && bean.copttype==null))
          return false;
        else if(this.copttype==null && bean.copttype==null){
        }
        else{
          if(!bean.copttype.equals(this.copttype))
               return false;
     }
      //compare field sbnktel
      if((this.sbnktel==null && bean.sbnktel!=null) || (this.sbnktel!=null && bean.sbnktel==null))
          return false;
        else if(this.sbnktel==null && bean.sbnktel==null){
        }
        else{
          if(!bean.sbnktel.equals(this.sbnktel))
               return false;
     }
      //compare field saddcolumn1
      if((this.saddcolumn1==null && bean.saddcolumn1!=null) || (this.saddcolumn1!=null && bean.saddcolumn1==null))
          return false;
        else if(this.saddcolumn1==null && bean.saddcolumn1==null){
        }
        else{
          if(!bean.saddcolumn1.equals(this.saddcolumn1))
               return false;
     }
      //compare field saddcolumn2
      if((this.saddcolumn2==null && bean.saddcolumn2!=null) || (this.saddcolumn2!=null && bean.saddcolumn2==null))
          return false;
        else if(this.saddcolumn2==null && bean.saddcolumn2==null){
        }
        else{
          if(!bean.saddcolumn2.equals(this.saddcolumn2))
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
        if(this.sbankno!=null)
          _hash_ = _hash_ * 31+ this.sbankno.hashCode() ;
        if(this.sbankname!=null)
          _hash_ = _hash_ * 31+ this.sbankname.hashCode() ;
        if(this.spaybankno!=null)
          _hash_ = _hash_ * 31+ this.spaybankno.hashCode() ;
        if(this.spaybankname!=null)
          _hash_ = _hash_ * 31+ this.spaybankname.hashCode() ;
        if(this.imodicount!=null)
          _hash_ = _hash_ * 31+ this.imodicount.hashCode() ;
        if(this.sstate!=null)
          _hash_ = _hash_ * 31+ this.sstate.hashCode() ;
        if(this.daffdate!=null)
          _hash_ = _hash_ * 31+ this.daffdate.hashCode() ;
        if(this.cbnkclass!=null)
          _hash_ = _hash_ * 31+ this.cbnkclass.hashCode() ;
        if(this.sofcity!=null)
          _hash_ = _hash_ * 31+ this.sofcity.hashCode() ;
        if(this.cbnkclsno!=null)
          _hash_ = _hash_ * 31+ this.cbnkclsno.hashCode() ;
        if(this.cbnkacctsta!=null)
          _hash_ = _hash_ * 31+ this.cbnkacctsta.hashCode() ;
        if(this.cbnkpostcode!=null)
          _hash_ = _hash_ * 31+ this.cbnkpostcode.hashCode() ;
        if(this.sbnkaddr!=null)
          _hash_ = _hash_ * 31+ this.sbnkaddr.hashCode() ;
        if(this.copttype!=null)
          _hash_ = _hash_ * 31+ this.copttype.hashCode() ;
        if(this.sbnktel!=null)
          _hash_ = _hash_ * 31+ this.sbnktel.hashCode() ;
        if(this.saddcolumn1!=null)
          _hash_ = _hash_ * 31+ this.saddcolumn1.hashCode() ;
        if(this.saddcolumn2!=null)
          _hash_ = _hash_ * 31+ this.saddcolumn2.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsPaybankDto bean = new TsPaybankDto();

          bean.sorgcode = this.sorgcode;

          bean.sbankno = this.sbankno;

          if (this.sbankname != null)
            bean.sbankname = new String(this.sbankname);
          if (this.spaybankno != null)
            bean.spaybankno = new String(this.spaybankno);
          if (this.spaybankname != null)
            bean.spaybankname = new String(this.spaybankname);
          if (this.imodicount != null)
            bean.imodicount = new Integer(this.imodicount.toString());
          if (this.sstate != null)
            bean.sstate = new String(this.sstate);
          if (this.daffdate != null)
            bean.daffdate = (Date) this.daffdate.clone();
          if (this.cbnkclass != null)
            bean.cbnkclass = new String(this.cbnkclass);
          if (this.sofcity != null)
            bean.sofcity = new String(this.sofcity);
          if (this.cbnkclsno != null)
            bean.cbnkclsno = new String(this.cbnkclsno);
          if (this.cbnkacctsta != null)
            bean.cbnkacctsta = new String(this.cbnkacctsta);
          if (this.cbnkpostcode != null)
            bean.cbnkpostcode = new String(this.cbnkpostcode);
          if (this.sbnkaddr != null)
            bean.sbnkaddr = new String(this.sbnkaddr);
          if (this.copttype != null)
            bean.copttype = new String(this.copttype);
          if (this.sbnktel != null)
            bean.sbnktel = new String(this.sbnktel);
          if (this.saddcolumn1 != null)
            bean.saddcolumn1 = new String(this.saddcolumn1);
          if (this.saddcolumn2 != null)
            bean.saddcolumn2 = new String(this.saddcolumn2);
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsPaybankDto").append(sep);
        sb.append("[sorgcode]").append(" = ").append(sorgcode).append(sep);
        sb.append("[sbankno]").append(" = ").append(sbankno).append(sep);
        sb.append("[sbankname]").append(" = ").append(sbankname).append(sep);
        sb.append("[spaybankno]").append(" = ").append(spaybankno).append(sep);
        sb.append("[spaybankname]").append(" = ").append(spaybankname).append(sep);
        sb.append("[imodicount]").append(" = ").append(imodicount).append(sep);
        sb.append("[sstate]").append(" = ").append(sstate).append(sep);
        sb.append("[daffdate]").append(" = ").append(daffdate).append(sep);
        sb.append("[cbnkclass]").append(" = ").append(cbnkclass).append(sep);
        sb.append("[sofcity]").append(" = ").append(sofcity).append(sep);
        sb.append("[cbnkclsno]").append(" = ").append(cbnkclsno).append(sep);
        sb.append("[cbnkacctsta]").append(" = ").append(cbnkacctsta).append(sep);
        sb.append("[cbnkpostcode]").append(" = ").append(cbnkpostcode).append(sep);
        sb.append("[sbnkaddr]").append(" = ").append(sbnkaddr).append(sep);
        sb.append("[copttype]").append(" = ").append(copttype).append(sep);
        sb.append("[sbnktel]").append(" = ").append(sbnktel).append(sep);
        sb.append("[saddcolumn1]").append(" = ").append(saddcolumn1).append(sep);
        sb.append("[saddcolumn2]").append(" = ").append(saddcolumn2).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //check field S_ORGCODE
      if (this.getSorgcode()==null)
             sb.append("S_ORGCODE����Ϊ��; ");
      if (this.getSorgcode()!=null)
             if (this.getSorgcode().getBytes().length > 12)
                sb.append("S_ORGCODE���Ȳ��ܳ��� "+12+"���ַ�; ");
    
    //check field S_BANKNO
      if (this.getSbankno()==null)
             sb.append("S_BANKNO����Ϊ��; ");
      if (this.getSbankno()!=null)
             if (this.getSbankno().getBytes().length > 30)
                sb.append("S_BANKNO���Ȳ��ܳ��� "+30+"���ַ�; ");
    
    //check field S_BANKNAME
      if (this.getSbankname()!=null)
             if (this.getSbankname().getBytes().length > 100)
                sb.append("S_BANKNAME���Ȳ��ܳ��� "+100+"���ַ�; ");
    
    //check field S_PAYBANKNO
      if (this.getSpaybankno()==null)
             sb.append("S_PAYBANKNO����Ϊ��; ");
      if (this.getSpaybankno()!=null)
             if (this.getSpaybankno().getBytes().length > 12)
                sb.append("S_PAYBANKNO���Ȳ��ܳ��� "+12+"���ַ�; ");
    
    //check field S_PAYBANKNAME
      if (this.getSpaybankname()!=null)
             if (this.getSpaybankname().getBytes().length > 100)
                sb.append("S_PAYBANKNAME���Ȳ��ܳ��� "+100+"���ַ�; ");
    
    //check field I_MODICOUNT
      
    //check field S_STATE
      if (this.getSstate()!=null)
             if (this.getSstate().getBytes().length > 1)
                sb.append("S_STATE���Ȳ��ܳ��� "+1+"���ַ�; ");
    
    //check field D_AFFDATE
      
    //check field C_BNKCLASS
      if (this.getCbnkclass()!=null)
             if (this.getCbnkclass().getBytes().length > 2)
                sb.append("C_BNKCLASS���Ȳ��ܳ��� "+2+"���ַ�; ");
    
    //check field S_OFCITY
      if (this.getSofcity()!=null)
             if (this.getSofcity().getBytes().length > 4)
                sb.append("S_OFCITY���Ȳ��ܳ��� "+4+"���ַ�; ");
    
    //check field C_BNKCLSNO
      if (this.getCbnkclsno()!=null)
             if (this.getCbnkclsno().getBytes().length > 3)
                sb.append("C_BNKCLSNO���Ȳ��ܳ��� "+3+"���ַ�; ");
    
    //check field C_BNKACCTSTA
      if (this.getCbnkacctsta()!=null)
             if (this.getCbnkacctsta().getBytes().length > 1)
                sb.append("C_BNKACCTSTA���Ȳ��ܳ��� "+1+"���ַ�; ");
    
    //check field C_BNKPOSTCODE
      if (this.getCbnkpostcode()!=null)
             if (this.getCbnkpostcode().getBytes().length > 6)
                sb.append("C_BNKPOSTCODE���Ȳ��ܳ��� "+6+"���ַ�; ");
    
    //check field S_BNKADDR
      if (this.getSbnkaddr()!=null)
             if (this.getSbnkaddr().getBytes().length > 60)
                sb.append("S_BNKADDR���Ȳ��ܳ��� "+60+"���ַ�; ");
    
    //check field C_OPTTYPE
      if (this.getCopttype()!=null)
             if (this.getCopttype().getBytes().length > 1)
                sb.append("C_OPTTYPE���Ȳ��ܳ��� "+1+"���ַ�; ");
    
    //check field S_BNKTEL
      if (this.getSbnktel()!=null)
             if (this.getSbnktel().getBytes().length > 30)
                sb.append("S_BNKTEL���Ȳ��ܳ��� "+30+"���ַ�; ");
    
    //check field S_ADDCOLUMN1
      if (this.getSaddcolumn1()!=null)
             if (this.getSaddcolumn1().getBytes().length > 12)
                sb.append("S_ADDCOLUMN1���Ȳ��ܳ��� "+12+"���ַ�; ");
    
    //check field S_ADDCOLUMN2
      if (this.getSaddcolumn2()!=null)
             if (this.getSaddcolumn2().getBytes().length > 60)
                sb.append("S_ADDCOLUMN2���Ȳ��ܳ��� "+60+"���ַ�; ");
    

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
                    sb.append("S_ORGCODE ����Ϊ��; ");
               if (this.getSorgcode()!=null)
                    if (this.getSorgcode().getBytes().length > 12)
                        sb.append("S_ORGCODE ���Ȳ��ܳ��� "+12+"���ַ�");
             break;
         }
  }
    
    //check field S_BANKNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BANKNO")) {
               if (this.getSbankno()==null)
                    sb.append("S_BANKNO ����Ϊ��; ");
               if (this.getSbankno()!=null)
                    if (this.getSbankno().getBytes().length > 30)
                        sb.append("S_BANKNO ���Ȳ��ܳ��� "+30+"���ַ�");
             break;
         }
  }
    
    //check field S_BANKNAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BANKNAME")) {
                 if (this.getSbankname()!=null)
                    if (this.getSbankname().getBytes().length > 100)
                        sb.append("S_BANKNAME ���Ȳ��ܳ��� "+100+"���ַ�");
             break;
         }
  }
    
    //check field S_PAYBANKNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_PAYBANKNO")) {
               if (this.getSpaybankno()==null)
                    sb.append("S_PAYBANKNO ����Ϊ��; ");
               if (this.getSpaybankno()!=null)
                    if (this.getSpaybankno().getBytes().length > 12)
                        sb.append("S_PAYBANKNO ���Ȳ��ܳ��� "+12+"���ַ�");
             break;
         }
  }
    
    //check field S_PAYBANKNAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_PAYBANKNAME")) {
                 if (this.getSpaybankname()!=null)
                    if (this.getSpaybankname().getBytes().length > 100)
                        sb.append("S_PAYBANKNAME ���Ȳ��ܳ��� "+100+"���ַ�");
             break;
         }
  }
    
    //check field I_MODICOUNT
          
    //check field S_STATE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_STATE")) {
                 if (this.getSstate()!=null)
                    if (this.getSstate().getBytes().length > 1)
                        sb.append("S_STATE ���Ȳ��ܳ��� "+1+"���ַ�");
             break;
         }
  }
    
    //check field D_AFFDATE
          
    //check field C_BNKCLASS
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("C_BNKCLASS")) {
                 if (this.getCbnkclass()!=null)
                    if (this.getCbnkclass().getBytes().length > 2)
                        sb.append("C_BNKCLASS ���Ȳ��ܳ��� "+2+"���ַ�");
             break;
         }
  }
    
    //check field S_OFCITY
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_OFCITY")) {
                 if (this.getSofcity()!=null)
                    if (this.getSofcity().getBytes().length > 4)
                        sb.append("S_OFCITY ���Ȳ��ܳ��� "+4+"���ַ�");
             break;
         }
  }
    
    //check field C_BNKCLSNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("C_BNKCLSNO")) {
                 if (this.getCbnkclsno()!=null)
                    if (this.getCbnkclsno().getBytes().length > 3)
                        sb.append("C_BNKCLSNO ���Ȳ��ܳ��� "+3+"���ַ�");
             break;
         }
  }
    
    //check field C_BNKACCTSTA
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("C_BNKACCTSTA")) {
                 if (this.getCbnkacctsta()!=null)
                    if (this.getCbnkacctsta().getBytes().length > 1)
                        sb.append("C_BNKACCTSTA ���Ȳ��ܳ��� "+1+"���ַ�");
             break;
         }
  }
    
    //check field C_BNKPOSTCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("C_BNKPOSTCODE")) {
                 if (this.getCbnkpostcode()!=null)
                    if (this.getCbnkpostcode().getBytes().length > 6)
                        sb.append("C_BNKPOSTCODE ���Ȳ��ܳ��� "+6+"���ַ�");
             break;
         }
  }
    
    //check field S_BNKADDR
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BNKADDR")) {
                 if (this.getSbnkaddr()!=null)
                    if (this.getSbnkaddr().getBytes().length > 60)
                        sb.append("S_BNKADDR ���Ȳ��ܳ��� "+60+"���ַ�");
             break;
         }
  }
    
    //check field C_OPTTYPE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("C_OPTTYPE")) {
                 if (this.getCopttype()!=null)
                    if (this.getCopttype().getBytes().length > 1)
                        sb.append("C_OPTTYPE ���Ȳ��ܳ��� "+1+"���ַ�");
             break;
         }
  }
    
    //check field S_BNKTEL
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BNKTEL")) {
                 if (this.getSbnktel()!=null)
                    if (this.getSbnktel().getBytes().length > 30)
                        sb.append("S_BNKTEL ���Ȳ��ܳ��� "+30+"���ַ�");
             break;
         }
  }
    
    //check field S_ADDCOLUMN1
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_ADDCOLUMN1")) {
                 if (this.getSaddcolumn1()!=null)
                    if (this.getSaddcolumn1().getBytes().length > 12)
                        sb.append("S_ADDCOLUMN1 ���Ȳ��ܳ��� "+12+"���ַ�");
             break;
         }
  }
    
    //check field S_ADDCOLUMN2
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_ADDCOLUMN2")) {
                 if (this.getSaddcolumn2()!=null)
                    if (this.getSaddcolumn2().getBytes().length > 60)
                        sb.append("S_ADDCOLUMN2 ���Ȳ��ܳ��� "+60+"���ַ�");
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
			return "�����ֶθ������ڱ����ֶθ���; ";
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
				sb.append("�����ֶ� " + _columnNames[i] + " ���ڸñ��ֶ���; ");
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
     throw new RuntimeException("��dtoû�����������dto�����ܽ��д˲���");
  };
  
  /* return the IPK class  */
    public IPK      getPK(){
      TsPaybankPK pk = new TsPaybankPK();
      pk.setSorgcode(getSorgcode());
      pk.setSbankno(getSbankno());
      return pk;
    };
}
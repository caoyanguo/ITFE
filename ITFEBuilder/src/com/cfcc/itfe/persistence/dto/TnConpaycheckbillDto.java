    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TnConpaycheckbillPK;
/**
 * <p>Title: DTO of table: TN_CONPAYCHECKBILL</p>
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

public class TnConpaycheckbillDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  I_ENROLSRLNO BIGINT , PK   , NOT NULL  , Identity  , Generated     */
    protected Long ienrolsrlno;
    /**
    *  D_ENDDATE DATE   , NOT NULL       */
    protected Date denddate;
    /**
    *  D_STARTDATE DATE   , NOT NULL       */
    protected Date dstartdate;
    /**
    *  S_BOOKORGCODE CHARACTER   , NOT NULL       */
    protected String sbookorgcode;
    /**
    *  S_TRECODE CHARACTER   , NOT NULL       */
    protected String strecode;
    /**
    *  S_BDGORGCODE VARCHAR         */
    protected String sbdgorgcode;
    /**
    *  S_BDGORGNAME VARCHAR   , NOT NULL       */
    protected String sbdgorgname;
    /**
    *  S_BNKNO VARCHAR         */
    protected String sbnkno;
    /**
    *  S_FUNCSBTCODE VARCHAR         */
    protected String sfuncsbtcode;
    /**
    *  S_ECOSBTCODE VARCHAR         */
    protected String secosbtcode;
    /**
    *  C_AMTKIND CHARACTER         */
    protected String camtkind;
    /**
    *  F_LASTMONTHZEROAMT DECIMAL         */
    protected BigDecimal flastmonthzeroamt;
    /**
    *  F_CURZEROAMT DECIMAL         */
    protected BigDecimal fcurzeroamt;
    /**
    *  F_CURRECKZEROAMT DECIMAL         */
    protected BigDecimal fcurreckzeroamt;
    /**
    *  F_LASTMONTHSMALLAMT DECIMAL         */
    protected BigDecimal flastmonthsmallamt;
    /**
    *  F_CURSMALLAMT DECIMAL         */
    protected BigDecimal fcursmallamt;
    /**
    *  F_CURRECKSMALLAMT DECIMAL         */
    protected BigDecimal fcurrecksmallamt;
    /**
    *  TS_SYSUPDATE TIMESTAMP         */
    protected Timestamp tssysupdate;


/******************************************************
*
*  getter and setter
*
*******************************************************/


    public Long getIenrolsrlno()
    {
        return ienrolsrlno;
    }
     /**
     *   Setter I_ENROLSRLNO, PK , NOT NULL  , Identity  , Generated      * */
    public void setIenrolsrlno(Long _ienrolsrlno) {
        this.ienrolsrlno = _ienrolsrlno;
    }


    public Date getDenddate()
    {
        return denddate;
    }
     /**
     *   Setter D_ENDDATE , NOT NULL        * */
    public void setDenddate(Date _denddate) {
        this.denddate = _denddate;
    }


    public Date getDstartdate()
    {
        return dstartdate;
    }
     /**
     *   Setter D_STARTDATE , NOT NULL        * */
    public void setDstartdate(Date _dstartdate) {
        this.dstartdate = _dstartdate;
    }


    public String getSbookorgcode()
    {
        return sbookorgcode;
    }
     /**
     *   Setter S_BOOKORGCODE , NOT NULL        * */
    public void setSbookorgcode(String _sbookorgcode) {
        this.sbookorgcode = _sbookorgcode;
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


    public String getSbdgorgcode()
    {
        return sbdgorgcode;
    }
     /**
     *   Setter S_BDGORGCODE        * */
    public void setSbdgorgcode(String _sbdgorgcode) {
        this.sbdgorgcode = _sbdgorgcode;
    }


    public String getSbdgorgname()
    {
        return sbdgorgname;
    }
     /**
     *   Setter S_BDGORGNAME , NOT NULL        * */
    public void setSbdgorgname(String _sbdgorgname) {
        this.sbdgorgname = _sbdgorgname;
    }


    public String getSbnkno()
    {
        return sbnkno;
    }
     /**
     *   Setter S_BNKNO        * */
    public void setSbnkno(String _sbnkno) {
        this.sbnkno = _sbnkno;
    }


    public String getSfuncsbtcode()
    {
        return sfuncsbtcode;
    }
     /**
     *   Setter S_FUNCSBTCODE        * */
    public void setSfuncsbtcode(String _sfuncsbtcode) {
        this.sfuncsbtcode = _sfuncsbtcode;
    }


    public String getSecosbtcode()
    {
        return secosbtcode;
    }
     /**
     *   Setter S_ECOSBTCODE        * */
    public void setSecosbtcode(String _secosbtcode) {
        this.secosbtcode = _secosbtcode;
    }


    public String getCamtkind()
    {
        return camtkind;
    }
     /**
     *   Setter C_AMTKIND        * */
    public void setCamtkind(String _camtkind) {
        this.camtkind = _camtkind;
    }


    public BigDecimal getFlastmonthzeroamt()
    {
        return flastmonthzeroamt;
    }
     /**
     *   Setter F_LASTMONTHZEROAMT        * */
    public void setFlastmonthzeroamt(BigDecimal _flastmonthzeroamt) {
        this.flastmonthzeroamt = _flastmonthzeroamt;
    }


    public BigDecimal getFcurzeroamt()
    {
        return fcurzeroamt;
    }
     /**
     *   Setter F_CURZEROAMT        * */
    public void setFcurzeroamt(BigDecimal _fcurzeroamt) {
        this.fcurzeroamt = _fcurzeroamt;
    }


    public BigDecimal getFcurreckzeroamt()
    {
        return fcurreckzeroamt;
    }
     /**
     *   Setter F_CURRECKZEROAMT        * */
    public void setFcurreckzeroamt(BigDecimal _fcurreckzeroamt) {
        this.fcurreckzeroamt = _fcurreckzeroamt;
    }


    public BigDecimal getFlastmonthsmallamt()
    {
        return flastmonthsmallamt;
    }
     /**
     *   Setter F_LASTMONTHSMALLAMT        * */
    public void setFlastmonthsmallamt(BigDecimal _flastmonthsmallamt) {
        this.flastmonthsmallamt = _flastmonthsmallamt;
    }


    public BigDecimal getFcursmallamt()
    {
        return fcursmallamt;
    }
     /**
     *   Setter F_CURSMALLAMT        * */
    public void setFcursmallamt(BigDecimal _fcursmallamt) {
        this.fcursmallamt = _fcursmallamt;
    }


    public BigDecimal getFcurrecksmallamt()
    {
        return fcurrecksmallamt;
    }
     /**
     *   Setter F_CURRECKSMALLAMT        * */
    public void setFcurrecksmallamt(BigDecimal _fcurrecksmallamt) {
        this.fcurrecksmallamt = _fcurrecksmallamt;
    }


    public Timestamp getTssysupdate()
    {
        return tssysupdate;
    }
     /**
     *   Setter TS_SYSUPDATE        * */
    public void setTssysupdate(Timestamp _tssysupdate) {
        this.tssysupdate = _tssysupdate;
    }




/******************************************************
*
*  Get Column Name
*
*******************************************************/
    /**
    *   Getter I_ENROLSRLNO, PK , NOT NULL  , Identity  , Generated     * */
    public static String  columnIenrolsrlno()
    {
        return "I_ENROLSRLNO";
    }
   
    /**
    *   Getter D_ENDDATE , NOT NULL       * */
    public static String  columnDenddate()
    {
        return "D_ENDDATE";
    }
   
    /**
    *   Getter D_STARTDATE , NOT NULL       * */
    public static String  columnDstartdate()
    {
        return "D_STARTDATE";
    }
   
    /**
    *   Getter S_BOOKORGCODE , NOT NULL       * */
    public static String  columnSbookorgcode()
    {
        return "S_BOOKORGCODE";
    }
   
    /**
    *   Getter S_TRECODE , NOT NULL       * */
    public static String  columnStrecode()
    {
        return "S_TRECODE";
    }
   
    /**
    *   Getter S_BDGORGCODE       * */
    public static String  columnSbdgorgcode()
    {
        return "S_BDGORGCODE";
    }
   
    /**
    *   Getter S_BDGORGNAME , NOT NULL       * */
    public static String  columnSbdgorgname()
    {
        return "S_BDGORGNAME";
    }
   
    /**
    *   Getter S_BNKNO       * */
    public static String  columnSbnkno()
    {
        return "S_BNKNO";
    }
   
    /**
    *   Getter S_FUNCSBTCODE       * */
    public static String  columnSfuncsbtcode()
    {
        return "S_FUNCSBTCODE";
    }
   
    /**
    *   Getter S_ECOSBTCODE       * */
    public static String  columnSecosbtcode()
    {
        return "S_ECOSBTCODE";
    }
   
    /**
    *   Getter C_AMTKIND       * */
    public static String  columnCamtkind()
    {
        return "C_AMTKIND";
    }
   
    /**
    *   Getter F_LASTMONTHZEROAMT       * */
    public static String  columnFlastmonthzeroamt()
    {
        return "F_LASTMONTHZEROAMT";
    }
   
    /**
    *   Getter F_CURZEROAMT       * */
    public static String  columnFcurzeroamt()
    {
        return "F_CURZEROAMT";
    }
   
    /**
    *   Getter F_CURRECKZEROAMT       * */
    public static String  columnFcurreckzeroamt()
    {
        return "F_CURRECKZEROAMT";
    }
   
    /**
    *   Getter F_LASTMONTHSMALLAMT       * */
    public static String  columnFlastmonthsmallamt()
    {
        return "F_LASTMONTHSMALLAMT";
    }
   
    /**
    *   Getter F_CURSMALLAMT       * */
    public static String  columnFcursmallamt()
    {
        return "F_CURSMALLAMT";
    }
   
    /**
    *   Getter F_CURRECKSMALLAMT       * */
    public static String  columnFcurrecksmallamt()
    {
        return "F_CURRECKSMALLAMT";
    }
   
    /**
    *   Getter TS_SYSUPDATE       * */
    public static String  columnTssysupdate()
    {
        return "TS_SYSUPDATE";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "TN_CONPAYCHECKBILL";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[18];        
        columnNames[0]="I_ENROLSRLNO";
        columnNames[1]="D_ENDDATE";
        columnNames[2]="D_STARTDATE";
        columnNames[3]="S_BOOKORGCODE";
        columnNames[4]="S_TRECODE";
        columnNames[5]="S_BDGORGCODE";
        columnNames[6]="S_BDGORGNAME";
        columnNames[7]="S_BNKNO";
        columnNames[8]="S_FUNCSBTCODE";
        columnNames[9]="S_ECOSBTCODE";
        columnNames[10]="C_AMTKIND";
        columnNames[11]="F_LASTMONTHZEROAMT";
        columnNames[12]="F_CURZEROAMT";
        columnNames[13]="F_CURRECKZEROAMT";
        columnNames[14]="F_LASTMONTHSMALLAMT";
        columnNames[15]="F_CURSMALLAMT";
        columnNames[16]="F_CURRECKSMALLAMT";
        columnNames[17]="TS_SYSUPDATE";
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

        if (obj == null || !(obj instanceof TnConpaycheckbillDto))
            return false;

        TnConpaycheckbillDto bean = (TnConpaycheckbillDto) obj;


      //compare field ienrolsrlno
      if((this.ienrolsrlno==null && bean.ienrolsrlno!=null) || (this.ienrolsrlno!=null && bean.ienrolsrlno==null))
          return false;
        else if(this.ienrolsrlno==null && bean.ienrolsrlno==null){
        }
        else{
          if(!bean.ienrolsrlno.equals(this.ienrolsrlno))
               return false;
     }
      //compare field denddate
      if((this.denddate==null && bean.denddate!=null) || (this.denddate!=null && bean.denddate==null))
          return false;
        else if(this.denddate==null && bean.denddate==null){
        }
        else{
          if(!bean.denddate.equals(this.denddate))
               return false;
     }
      //compare field dstartdate
      if((this.dstartdate==null && bean.dstartdate!=null) || (this.dstartdate!=null && bean.dstartdate==null))
          return false;
        else if(this.dstartdate==null && bean.dstartdate==null){
        }
        else{
          if(!bean.dstartdate.equals(this.dstartdate))
               return false;
     }
      //compare field sbookorgcode
      if((this.sbookorgcode==null && bean.sbookorgcode!=null) || (this.sbookorgcode!=null && bean.sbookorgcode==null))
          return false;
        else if(this.sbookorgcode==null && bean.sbookorgcode==null){
        }
        else{
          if(!bean.sbookorgcode.equals(this.sbookorgcode))
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
      //compare field sbdgorgcode
      if((this.sbdgorgcode==null && bean.sbdgorgcode!=null) || (this.sbdgorgcode!=null && bean.sbdgorgcode==null))
          return false;
        else if(this.sbdgorgcode==null && bean.sbdgorgcode==null){
        }
        else{
          if(!bean.sbdgorgcode.equals(this.sbdgorgcode))
               return false;
     }
      //compare field sbdgorgname
      if((this.sbdgorgname==null && bean.sbdgorgname!=null) || (this.sbdgorgname!=null && bean.sbdgorgname==null))
          return false;
        else if(this.sbdgorgname==null && bean.sbdgorgname==null){
        }
        else{
          if(!bean.sbdgorgname.equals(this.sbdgorgname))
               return false;
     }
      //compare field sbnkno
      if((this.sbnkno==null && bean.sbnkno!=null) || (this.sbnkno!=null && bean.sbnkno==null))
          return false;
        else if(this.sbnkno==null && bean.sbnkno==null){
        }
        else{
          if(!bean.sbnkno.equals(this.sbnkno))
               return false;
     }
      //compare field sfuncsbtcode
      if((this.sfuncsbtcode==null && bean.sfuncsbtcode!=null) || (this.sfuncsbtcode!=null && bean.sfuncsbtcode==null))
          return false;
        else if(this.sfuncsbtcode==null && bean.sfuncsbtcode==null){
        }
        else{
          if(!bean.sfuncsbtcode.equals(this.sfuncsbtcode))
               return false;
     }
      //compare field secosbtcode
      if((this.secosbtcode==null && bean.secosbtcode!=null) || (this.secosbtcode!=null && bean.secosbtcode==null))
          return false;
        else if(this.secosbtcode==null && bean.secosbtcode==null){
        }
        else{
          if(!bean.secosbtcode.equals(this.secosbtcode))
               return false;
     }
      //compare field camtkind
      if((this.camtkind==null && bean.camtkind!=null) || (this.camtkind!=null && bean.camtkind==null))
          return false;
        else if(this.camtkind==null && bean.camtkind==null){
        }
        else{
          if(!bean.camtkind.equals(this.camtkind))
               return false;
     }
      //compare field flastmonthzeroamt
      if((this.flastmonthzeroamt==null && bean.flastmonthzeroamt!=null) || (this.flastmonthzeroamt!=null && bean.flastmonthzeroamt==null))
          return false;
        else if(this.flastmonthzeroamt==null && bean.flastmonthzeroamt==null){
        }
        else{
          if(!bean.flastmonthzeroamt.equals(this.flastmonthzeroamt))
               return false;
     }
      //compare field fcurzeroamt
      if((this.fcurzeroamt==null && bean.fcurzeroamt!=null) || (this.fcurzeroamt!=null && bean.fcurzeroamt==null))
          return false;
        else if(this.fcurzeroamt==null && bean.fcurzeroamt==null){
        }
        else{
          if(!bean.fcurzeroamt.equals(this.fcurzeroamt))
               return false;
     }
      //compare field fcurreckzeroamt
      if((this.fcurreckzeroamt==null && bean.fcurreckzeroamt!=null) || (this.fcurreckzeroamt!=null && bean.fcurreckzeroamt==null))
          return false;
        else if(this.fcurreckzeroamt==null && bean.fcurreckzeroamt==null){
        }
        else{
          if(!bean.fcurreckzeroamt.equals(this.fcurreckzeroamt))
               return false;
     }
      //compare field flastmonthsmallamt
      if((this.flastmonthsmallamt==null && bean.flastmonthsmallamt!=null) || (this.flastmonthsmallamt!=null && bean.flastmonthsmallamt==null))
          return false;
        else if(this.flastmonthsmallamt==null && bean.flastmonthsmallamt==null){
        }
        else{
          if(!bean.flastmonthsmallamt.equals(this.flastmonthsmallamt))
               return false;
     }
      //compare field fcursmallamt
      if((this.fcursmallamt==null && bean.fcursmallamt!=null) || (this.fcursmallamt!=null && bean.fcursmallamt==null))
          return false;
        else if(this.fcursmallamt==null && bean.fcursmallamt==null){
        }
        else{
          if(!bean.fcursmallamt.equals(this.fcursmallamt))
               return false;
     }
      //compare field fcurrecksmallamt
      if((this.fcurrecksmallamt==null && bean.fcurrecksmallamt!=null) || (this.fcurrecksmallamt!=null && bean.fcurrecksmallamt==null))
          return false;
        else if(this.fcurrecksmallamt==null && bean.fcurrecksmallamt==null){
        }
        else{
          if(!bean.fcurrecksmallamt.equals(this.fcurrecksmallamt))
               return false;
     }
      //compare field tssysupdate
      if((this.tssysupdate==null && bean.tssysupdate!=null) || (this.tssysupdate!=null && bean.tssysupdate==null))
          return false;
        else if(this.tssysupdate==null && bean.tssysupdate==null){
        }
        else{
          if(!bean.tssysupdate.equals(this.tssysupdate))
               return false;
     }



        return true;
    }

    /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
  
		int _hash_ = 1;
		
        if(this.ienrolsrlno!=null)
          _hash_ = _hash_ * 31+ this.ienrolsrlno.hashCode() ;
        if(this.denddate!=null)
          _hash_ = _hash_ * 31+ this.denddate.hashCode() ;
        if(this.dstartdate!=null)
          _hash_ = _hash_ * 31+ this.dstartdate.hashCode() ;
        if(this.sbookorgcode!=null)
          _hash_ = _hash_ * 31+ this.sbookorgcode.hashCode() ;
        if(this.strecode!=null)
          _hash_ = _hash_ * 31+ this.strecode.hashCode() ;
        if(this.sbdgorgcode!=null)
          _hash_ = _hash_ * 31+ this.sbdgorgcode.hashCode() ;
        if(this.sbdgorgname!=null)
          _hash_ = _hash_ * 31+ this.sbdgorgname.hashCode() ;
        if(this.sbnkno!=null)
          _hash_ = _hash_ * 31+ this.sbnkno.hashCode() ;
        if(this.sfuncsbtcode!=null)
          _hash_ = _hash_ * 31+ this.sfuncsbtcode.hashCode() ;
        if(this.secosbtcode!=null)
          _hash_ = _hash_ * 31+ this.secosbtcode.hashCode() ;
        if(this.camtkind!=null)
          _hash_ = _hash_ * 31+ this.camtkind.hashCode() ;
        if(this.flastmonthzeroamt!=null)
          _hash_ = _hash_ * 31+ this.flastmonthzeroamt.hashCode() ;
        if(this.fcurzeroamt!=null)
          _hash_ = _hash_ * 31+ this.fcurzeroamt.hashCode() ;
        if(this.fcurreckzeroamt!=null)
          _hash_ = _hash_ * 31+ this.fcurreckzeroamt.hashCode() ;
        if(this.flastmonthsmallamt!=null)
          _hash_ = _hash_ * 31+ this.flastmonthsmallamt.hashCode() ;
        if(this.fcursmallamt!=null)
          _hash_ = _hash_ * 31+ this.fcursmallamt.hashCode() ;
        if(this.fcurrecksmallamt!=null)
          _hash_ = _hash_ * 31+ this.fcurrecksmallamt.hashCode() ;
        if(this.tssysupdate!=null)
          _hash_ = _hash_ * 31+ this.tssysupdate.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TnConpaycheckbillDto bean = new TnConpaycheckbillDto();

          bean.ienrolsrlno = this.ienrolsrlno;

          if (this.denddate != null)
            bean.denddate = (Date) this.denddate.clone();
          if (this.dstartdate != null)
            bean.dstartdate = (Date) this.dstartdate.clone();
          if (this.sbookorgcode != null)
            bean.sbookorgcode = new String(this.sbookorgcode);
          if (this.strecode != null)
            bean.strecode = new String(this.strecode);
          if (this.sbdgorgcode != null)
            bean.sbdgorgcode = new String(this.sbdgorgcode);
          if (this.sbdgorgname != null)
            bean.sbdgorgname = new String(this.sbdgorgname);
          if (this.sbnkno != null)
            bean.sbnkno = new String(this.sbnkno);
          if (this.sfuncsbtcode != null)
            bean.sfuncsbtcode = new String(this.sfuncsbtcode);
          if (this.secosbtcode != null)
            bean.secosbtcode = new String(this.secosbtcode);
          if (this.camtkind != null)
            bean.camtkind = new String(this.camtkind);
          if (this.flastmonthzeroamt != null)
            bean.flastmonthzeroamt = new BigDecimal(this.flastmonthzeroamt.toString());
          if (this.fcurzeroamt != null)
            bean.fcurzeroamt = new BigDecimal(this.fcurzeroamt.toString());
          if (this.fcurreckzeroamt != null)
            bean.fcurreckzeroamt = new BigDecimal(this.fcurreckzeroamt.toString());
          if (this.flastmonthsmallamt != null)
            bean.flastmonthsmallamt = new BigDecimal(this.flastmonthsmallamt.toString());
          if (this.fcursmallamt != null)
            bean.fcursmallamt = new BigDecimal(this.fcursmallamt.toString());
          if (this.fcurrecksmallamt != null)
            bean.fcurrecksmallamt = new BigDecimal(this.fcurrecksmallamt.toString());
          if (this.tssysupdate != null)
            bean.tssysupdate = (Timestamp) this.tssysupdate.clone();
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TnConpaycheckbillDto").append(sep);
        sb.append("[ienrolsrlno]").append(" = ").append(ienrolsrlno).append(sep);
        sb.append("[denddate]").append(" = ").append(denddate).append(sep);
        sb.append("[dstartdate]").append(" = ").append(dstartdate).append(sep);
        sb.append("[sbookorgcode]").append(" = ").append(sbookorgcode).append(sep);
        sb.append("[strecode]").append(" = ").append(strecode).append(sep);
        sb.append("[sbdgorgcode]").append(" = ").append(sbdgorgcode).append(sep);
        sb.append("[sbdgorgname]").append(" = ").append(sbdgorgname).append(sep);
        sb.append("[sbnkno]").append(" = ").append(sbnkno).append(sep);
        sb.append("[sfuncsbtcode]").append(" = ").append(sfuncsbtcode).append(sep);
        sb.append("[secosbtcode]").append(" = ").append(secosbtcode).append(sep);
        sb.append("[camtkind]").append(" = ").append(camtkind).append(sep);
        sb.append("[flastmonthzeroamt]").append(" = ").append(flastmonthzeroamt).append(sep);
        sb.append("[fcurzeroamt]").append(" = ").append(fcurzeroamt).append(sep);
        sb.append("[fcurreckzeroamt]").append(" = ").append(fcurreckzeroamt).append(sep);
        sb.append("[flastmonthsmallamt]").append(" = ").append(flastmonthsmallamt).append(sep);
        sb.append("[fcursmallamt]").append(" = ").append(fcursmallamt).append(sep);
        sb.append("[fcurrecksmallamt]").append(" = ").append(fcurrecksmallamt).append(sep);
        sb.append("[tssysupdate]").append(" = ").append(tssysupdate).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //don't need check field I_ENROLSRLNO,it is generated column
  
    //check field D_ENDDATE
      if (this.getDenddate()==null)
             sb.append("D_ENDDATE不能为空; ");
      
    //check field D_STARTDATE
      if (this.getDstartdate()==null)
             sb.append("D_STARTDATE不能为空; ");
      
    //check field S_BOOKORGCODE
      if (this.getSbookorgcode()==null)
             sb.append("S_BOOKORGCODE不能为空; ");
      if (this.getSbookorgcode()!=null)
             if (this.getSbookorgcode().getBytes().length > 12)
                sb.append("S_BOOKORGCODE宽度不能超过 "+12+"个字符; ");
    
    //check field S_TRECODE
      if (this.getStrecode()==null)
             sb.append("S_TRECODE不能为空; ");
      if (this.getStrecode()!=null)
             if (this.getStrecode().getBytes().length > 10)
                sb.append("S_TRECODE宽度不能超过 "+10+"个字符; ");
    
    //check field S_BDGORGCODE
      if (this.getSbdgorgcode()!=null)
             if (this.getSbdgorgcode().getBytes().length > 15)
                sb.append("S_BDGORGCODE宽度不能超过 "+15+"个字符; ");
    
    //check field S_BDGORGNAME
      if (this.getSbdgorgname()==null)
             sb.append("S_BDGORGNAME不能为空; ");
      if (this.getSbdgorgname()!=null)
             if (this.getSbdgorgname().getBytes().length > 60)
                sb.append("S_BDGORGNAME宽度不能超过 "+60+"个字符; ");
    
    //check field S_BNKNO
      if (this.getSbnkno()!=null)
             if (this.getSbnkno().getBytes().length > 12)
                sb.append("S_BNKNO宽度不能超过 "+12+"个字符; ");
    
    //check field S_FUNCSBTCODE
      if (this.getSfuncsbtcode()!=null)
             if (this.getSfuncsbtcode().getBytes().length > 30)
                sb.append("S_FUNCSBTCODE宽度不能超过 "+30+"个字符; ");
    
    //check field S_ECOSBTCODE
      if (this.getSecosbtcode()!=null)
             if (this.getSecosbtcode().getBytes().length > 30)
                sb.append("S_ECOSBTCODE宽度不能超过 "+30+"个字符; ");
    
    //check field C_AMTKIND
      if (this.getCamtkind()!=null)
             if (this.getCamtkind().getBytes().length > 1)
                sb.append("C_AMTKIND宽度不能超过 "+1+"个字符; ");
    
    //check field F_LASTMONTHZEROAMT
      
    //check field F_CURZEROAMT
      
    //check field F_CURRECKZEROAMT
      
    //check field F_LASTMONTHSMALLAMT
      
    //check field F_CURSMALLAMT
      
    //check field F_CURRECKSMALLAMT
      
    //don't need check field TS_SYSUPDATE,it is UpdateTimeStamp column
  

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
    //don't need check field I_ENROLSRLNO,it is generated column
  
    //check field D_ENDDATE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("D_ENDDATE")) {
               if (this.getDenddate()==null)
                    sb.append("D_ENDDATE 不能为空; ");
               break;
         }
  }
    
    //check field D_STARTDATE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("D_STARTDATE")) {
               if (this.getDstartdate()==null)
                    sb.append("D_STARTDATE 不能为空; ");
               break;
         }
  }
    
    //check field S_BOOKORGCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BOOKORGCODE")) {
               if (this.getSbookorgcode()==null)
                    sb.append("S_BOOKORGCODE 不能为空; ");
               if (this.getSbookorgcode()!=null)
                    if (this.getSbookorgcode().getBytes().length > 12)
                        sb.append("S_BOOKORGCODE 宽度不能超过 "+12+"个字符");
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
    
    //check field S_BDGORGCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BDGORGCODE")) {
                 if (this.getSbdgorgcode()!=null)
                    if (this.getSbdgorgcode().getBytes().length > 15)
                        sb.append("S_BDGORGCODE 宽度不能超过 "+15+"个字符");
             break;
         }
  }
    
    //check field S_BDGORGNAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BDGORGNAME")) {
               if (this.getSbdgorgname()==null)
                    sb.append("S_BDGORGNAME 不能为空; ");
               if (this.getSbdgorgname()!=null)
                    if (this.getSbdgorgname().getBytes().length > 60)
                        sb.append("S_BDGORGNAME 宽度不能超过 "+60+"个字符");
             break;
         }
  }
    
    //check field S_BNKNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BNKNO")) {
                 if (this.getSbnkno()!=null)
                    if (this.getSbnkno().getBytes().length > 12)
                        sb.append("S_BNKNO 宽度不能超过 "+12+"个字符");
             break;
         }
  }
    
    //check field S_FUNCSBTCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_FUNCSBTCODE")) {
                 if (this.getSfuncsbtcode()!=null)
                    if (this.getSfuncsbtcode().getBytes().length > 30)
                        sb.append("S_FUNCSBTCODE 宽度不能超过 "+30+"个字符");
             break;
         }
  }
    
    //check field S_ECOSBTCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_ECOSBTCODE")) {
                 if (this.getSecosbtcode()!=null)
                    if (this.getSecosbtcode().getBytes().length > 30)
                        sb.append("S_ECOSBTCODE 宽度不能超过 "+30+"个字符");
             break;
         }
  }
    
    //check field C_AMTKIND
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("C_AMTKIND")) {
                 if (this.getCamtkind()!=null)
                    if (this.getCamtkind().getBytes().length > 1)
                        sb.append("C_AMTKIND 宽度不能超过 "+1+"个字符");
             break;
         }
  }
    
    //check field F_LASTMONTHZEROAMT
          
    //check field F_CURZEROAMT
          
    //check field F_CURRECKZEROAMT
          
    //check field F_LASTMONTHSMALLAMT
          
    //check field F_CURSMALLAMT
          
    //check field F_CURRECKSMALLAMT
          
    //don't need check field TS_SYSUPDATE,it is UpdateTimeStamp column
  
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
      TnConpaycheckbillPK pk = new TnConpaycheckbillPK();
      pk.setIenrolsrlno(getIenrolsrlno());
      return pk;
    };
}

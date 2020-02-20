    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TfUnitrecordsubPK;
/**
 * <p>Title: DTO of table: TF_UNITRECORDSUB</p>
 * <p>Description: 单位清册5951子表 Data Transfer Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:59 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  dto.vm version timestamp: 2008-01-08 16:30:00 
 *
 * @author win7
 */

public class TfUnitrecordsubDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  I_VOUSRLNO BIGINT , PK   , NOT NULL       */
    protected Long ivousrlno;
    /**
    *  I_SEQNO BIGINT , PK   , NOT NULL       */
    protected Long iseqno;
    /**
    * 序号 S_ID VARCHAR   , NOT NULL       */
    protected String sid;
    /**
    * 预算单位编码 S_AGENCYCODE VARCHAR   , NOT NULL       */
    protected String sagencycode;
    /**
    * 预算单位名称 S_AGENCYNAME VARCHAR   , NOT NULL       */
    protected String sagencyname;
    /**
    * 资金性质编码 S_FUNDTYPECODE VARCHAR   , NOT NULL       */
    protected String sfundtypecode;
    /**
    * 资金性质名称 S_FUNDTYPENAME VARCHAR   , NOT NULL       */
    protected String sfundtypename;
    /**
    * 单位账号 S_AGENCYACCONO VARCHAR   , NOT NULL       */
    protected String sagencyaccono;
    /**
    * 单位账户名称 S_AGENCYACCONAME VARCHAR   , NOT NULL       */
    protected String sagencyacconame;
    /**
    * 单位帐户开户行 S_AGENCYBANKNAME VARCHAR   , NOT NULL       */
    protected String sagencybankname;
    /**
    * 账户状态 S_ACCSTATUS VARCHAR   , NOT NULL       */
    protected String saccstatus;
    /**
    * 预留字段1 S_HOLD1 VARCHAR         */
    protected String shold1;
    /**
    * 预留字段2 S_HOLD2 VARCHAR         */
    protected String shold2;
    /**
    * 预留字段3 S_HOLD3 VARCHAR         */
    protected String shold3;
    /**
    * 预留字段4 S_HOLD4 VARCHAR         */
    protected String shold4;
    /**
    * 扩展 S_EXT1 VARCHAR         */
    protected String sext1;
    /**
    * 扩展 S_EXT2 VARCHAR         */
    protected String sext2;
    /**
    * 扩展 S_EXT3 VARCHAR         */
    protected String sext3;


/******************************************************
*
*  getter and setter
*
*******************************************************/


    public Long getIvousrlno()
    {
        return ivousrlno;
    }
     /**
     *   Setter I_VOUSRLNO, PK , NOT NULL        * */
    public void setIvousrlno(Long _ivousrlno) {
        this.ivousrlno = _ivousrlno;
    }


    public Long getIseqno()
    {
        return iseqno;
    }
     /**
     *   Setter I_SEQNO, PK , NOT NULL        * */
    public void setIseqno(Long _iseqno) {
        this.iseqno = _iseqno;
    }


    public String getSid()
    {
        return sid;
    }
     /**
     *  序号 Setter S_ID , NOT NULL        * */
    public void setSid(String _sid) {
        this.sid = _sid;
    }


    public String getSagencycode()
    {
        return sagencycode;
    }
     /**
     *  预算单位编码 Setter S_AGENCYCODE , NOT NULL        * */
    public void setSagencycode(String _sagencycode) {
        this.sagencycode = _sagencycode;
    }


    public String getSagencyname()
    {
        return sagencyname;
    }
     /**
     *  预算单位名称 Setter S_AGENCYNAME , NOT NULL        * */
    public void setSagencyname(String _sagencyname) {
        this.sagencyname = _sagencyname;
    }


    public String getSfundtypecode()
    {
        return sfundtypecode;
    }
     /**
     *  资金性质编码 Setter S_FUNDTYPECODE , NOT NULL        * */
    public void setSfundtypecode(String _sfundtypecode) {
        this.sfundtypecode = _sfundtypecode;
    }


    public String getSfundtypename()
    {
        return sfundtypename;
    }
     /**
     *  资金性质名称 Setter S_FUNDTYPENAME , NOT NULL        * */
    public void setSfundtypename(String _sfundtypename) {
        this.sfundtypename = _sfundtypename;
    }


    public String getSagencyaccono()
    {
        return sagencyaccono;
    }
     /**
     *  单位账号 Setter S_AGENCYACCONO , NOT NULL        * */
    public void setSagencyaccono(String _sagencyaccono) {
        this.sagencyaccono = _sagencyaccono;
    }


    public String getSagencyacconame()
    {
        return sagencyacconame;
    }
     /**
     *  单位账户名称 Setter S_AGENCYACCONAME , NOT NULL        * */
    public void setSagencyacconame(String _sagencyacconame) {
        this.sagencyacconame = _sagencyacconame;
    }


    public String getSagencybankname()
    {
        return sagencybankname;
    }
     /**
     *  单位帐户开户行 Setter S_AGENCYBANKNAME , NOT NULL        * */
    public void setSagencybankname(String _sagencybankname) {
        this.sagencybankname = _sagencybankname;
    }


    public String getSaccstatus()
    {
        return saccstatus;
    }
     /**
     *  账户状态 Setter S_ACCSTATUS , NOT NULL        * */
    public void setSaccstatus(String _saccstatus) {
        this.saccstatus = _saccstatus;
    }


    public String getShold1()
    {
        return shold1;
    }
     /**
     *  预留字段1 Setter S_HOLD1        * */
    public void setShold1(String _shold1) {
        this.shold1 = _shold1;
    }


    public String getShold2()
    {
        return shold2;
    }
     /**
     *  预留字段2 Setter S_HOLD2        * */
    public void setShold2(String _shold2) {
        this.shold2 = _shold2;
    }


    public String getShold3()
    {
        return shold3;
    }
     /**
     *  预留字段3 Setter S_HOLD3        * */
    public void setShold3(String _shold3) {
        this.shold3 = _shold3;
    }


    public String getShold4()
    {
        return shold4;
    }
     /**
     *  预留字段4 Setter S_HOLD4        * */
    public void setShold4(String _shold4) {
        this.shold4 = _shold4;
    }


    public String getSext1()
    {
        return sext1;
    }
     /**
     *  扩展 Setter S_EXT1        * */
    public void setSext1(String _sext1) {
        this.sext1 = _sext1;
    }


    public String getSext2()
    {
        return sext2;
    }
     /**
     *  扩展 Setter S_EXT2        * */
    public void setSext2(String _sext2) {
        this.sext2 = _sext2;
    }


    public String getSext3()
    {
        return sext3;
    }
     /**
     *  扩展 Setter S_EXT3        * */
    public void setSext3(String _sext3) {
        this.sext3 = _sext3;
    }




/******************************************************
*
*  Get Column Name
*
*******************************************************/
    /**
    *   Getter I_VOUSRLNO, PK , NOT NULL       * */
    public static String  columnIvousrlno()
    {
        return "I_VOUSRLNO";
    }
   
    /**
    *   Getter I_SEQNO, PK , NOT NULL       * */
    public static String  columnIseqno()
    {
        return "I_SEQNO";
    }
   
    /**
    *  序号 Getter S_ID , NOT NULL       * */
    public static String  columnSid()
    {
        return "S_ID";
    }
   
    /**
    *  预算单位编码 Getter S_AGENCYCODE , NOT NULL       * */
    public static String  columnSagencycode()
    {
        return "S_AGENCYCODE";
    }
   
    /**
    *  预算单位名称 Getter S_AGENCYNAME , NOT NULL       * */
    public static String  columnSagencyname()
    {
        return "S_AGENCYNAME";
    }
   
    /**
    *  资金性质编码 Getter S_FUNDTYPECODE , NOT NULL       * */
    public static String  columnSfundtypecode()
    {
        return "S_FUNDTYPECODE";
    }
   
    /**
    *  资金性质名称 Getter S_FUNDTYPENAME , NOT NULL       * */
    public static String  columnSfundtypename()
    {
        return "S_FUNDTYPENAME";
    }
   
    /**
    *  单位账号 Getter S_AGENCYACCONO , NOT NULL       * */
    public static String  columnSagencyaccono()
    {
        return "S_AGENCYACCONO";
    }
   
    /**
    *  单位账户名称 Getter S_AGENCYACCONAME , NOT NULL       * */
    public static String  columnSagencyacconame()
    {
        return "S_AGENCYACCONAME";
    }
   
    /**
    *  单位帐户开户行 Getter S_AGENCYBANKNAME , NOT NULL       * */
    public static String  columnSagencybankname()
    {
        return "S_AGENCYBANKNAME";
    }
   
    /**
    *  账户状态 Getter S_ACCSTATUS , NOT NULL       * */
    public static String  columnSaccstatus()
    {
        return "S_ACCSTATUS";
    }
   
    /**
    *  预留字段1 Getter S_HOLD1       * */
    public static String  columnShold1()
    {
        return "S_HOLD1";
    }
   
    /**
    *  预留字段2 Getter S_HOLD2       * */
    public static String  columnShold2()
    {
        return "S_HOLD2";
    }
   
    /**
    *  预留字段3 Getter S_HOLD3       * */
    public static String  columnShold3()
    {
        return "S_HOLD3";
    }
   
    /**
    *  预留字段4 Getter S_HOLD4       * */
    public static String  columnShold4()
    {
        return "S_HOLD4";
    }
   
    /**
    *  扩展 Getter S_EXT1       * */
    public static String  columnSext1()
    {
        return "S_EXT1";
    }
   
    /**
    *  扩展 Getter S_EXT2       * */
    public static String  columnSext2()
    {
        return "S_EXT2";
    }
   
    /**
    *  扩展 Getter S_EXT3       * */
    public static String  columnSext3()
    {
        return "S_EXT3";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "TF_UNITRECORDSUB";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[18];        
        columnNames[0]="I_VOUSRLNO";
        columnNames[1]="I_SEQNO";
        columnNames[2]="S_ID";
        columnNames[3]="S_AGENCYCODE";
        columnNames[4]="S_AGENCYNAME";
        columnNames[5]="S_FUNDTYPECODE";
        columnNames[6]="S_FUNDTYPENAME";
        columnNames[7]="S_AGENCYACCONO";
        columnNames[8]="S_AGENCYACCONAME";
        columnNames[9]="S_AGENCYBANKNAME";
        columnNames[10]="S_ACCSTATUS";
        columnNames[11]="S_HOLD1";
        columnNames[12]="S_HOLD2";
        columnNames[13]="S_HOLD3";
        columnNames[14]="S_HOLD4";
        columnNames[15]="S_EXT1";
        columnNames[16]="S_EXT2";
        columnNames[17]="S_EXT3";
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

        if (obj == null || !(obj instanceof TfUnitrecordsubDto))
            return false;

        TfUnitrecordsubDto bean = (TfUnitrecordsubDto) obj;


      //compare field ivousrlno
      if((this.ivousrlno==null && bean.ivousrlno!=null) || (this.ivousrlno!=null && bean.ivousrlno==null))
          return false;
        else if(this.ivousrlno==null && bean.ivousrlno==null){
        }
        else{
          if(!bean.ivousrlno.equals(this.ivousrlno))
               return false;
     }
      //compare field iseqno
      if((this.iseqno==null && bean.iseqno!=null) || (this.iseqno!=null && bean.iseqno==null))
          return false;
        else if(this.iseqno==null && bean.iseqno==null){
        }
        else{
          if(!bean.iseqno.equals(this.iseqno))
               return false;
     }
      //compare field sid
      if((this.sid==null && bean.sid!=null) || (this.sid!=null && bean.sid==null))
          return false;
        else if(this.sid==null && bean.sid==null){
        }
        else{
          if(!bean.sid.equals(this.sid))
               return false;
     }
      //compare field sagencycode
      if((this.sagencycode==null && bean.sagencycode!=null) || (this.sagencycode!=null && bean.sagencycode==null))
          return false;
        else if(this.sagencycode==null && bean.sagencycode==null){
        }
        else{
          if(!bean.sagencycode.equals(this.sagencycode))
               return false;
     }
      //compare field sagencyname
      if((this.sagencyname==null && bean.sagencyname!=null) || (this.sagencyname!=null && bean.sagencyname==null))
          return false;
        else if(this.sagencyname==null && bean.sagencyname==null){
        }
        else{
          if(!bean.sagencyname.equals(this.sagencyname))
               return false;
     }
      //compare field sfundtypecode
      if((this.sfundtypecode==null && bean.sfundtypecode!=null) || (this.sfundtypecode!=null && bean.sfundtypecode==null))
          return false;
        else if(this.sfundtypecode==null && bean.sfundtypecode==null){
        }
        else{
          if(!bean.sfundtypecode.equals(this.sfundtypecode))
               return false;
     }
      //compare field sfundtypename
      if((this.sfundtypename==null && bean.sfundtypename!=null) || (this.sfundtypename!=null && bean.sfundtypename==null))
          return false;
        else if(this.sfundtypename==null && bean.sfundtypename==null){
        }
        else{
          if(!bean.sfundtypename.equals(this.sfundtypename))
               return false;
     }
      //compare field sagencyaccono
      if((this.sagencyaccono==null && bean.sagencyaccono!=null) || (this.sagencyaccono!=null && bean.sagencyaccono==null))
          return false;
        else if(this.sagencyaccono==null && bean.sagencyaccono==null){
        }
        else{
          if(!bean.sagencyaccono.equals(this.sagencyaccono))
               return false;
     }
      //compare field sagencyacconame
      if((this.sagencyacconame==null && bean.sagencyacconame!=null) || (this.sagencyacconame!=null && bean.sagencyacconame==null))
          return false;
        else if(this.sagencyacconame==null && bean.sagencyacconame==null){
        }
        else{
          if(!bean.sagencyacconame.equals(this.sagencyacconame))
               return false;
     }
      //compare field sagencybankname
      if((this.sagencybankname==null && bean.sagencybankname!=null) || (this.sagencybankname!=null && bean.sagencybankname==null))
          return false;
        else if(this.sagencybankname==null && bean.sagencybankname==null){
        }
        else{
          if(!bean.sagencybankname.equals(this.sagencybankname))
               return false;
     }
      //compare field saccstatus
      if((this.saccstatus==null && bean.saccstatus!=null) || (this.saccstatus!=null && bean.saccstatus==null))
          return false;
        else if(this.saccstatus==null && bean.saccstatus==null){
        }
        else{
          if(!bean.saccstatus.equals(this.saccstatus))
               return false;
     }
      //compare field shold1
      if((this.shold1==null && bean.shold1!=null) || (this.shold1!=null && bean.shold1==null))
          return false;
        else if(this.shold1==null && bean.shold1==null){
        }
        else{
          if(!bean.shold1.equals(this.shold1))
               return false;
     }
      //compare field shold2
      if((this.shold2==null && bean.shold2!=null) || (this.shold2!=null && bean.shold2==null))
          return false;
        else if(this.shold2==null && bean.shold2==null){
        }
        else{
          if(!bean.shold2.equals(this.shold2))
               return false;
     }
      //compare field shold3
      if((this.shold3==null && bean.shold3!=null) || (this.shold3!=null && bean.shold3==null))
          return false;
        else if(this.shold3==null && bean.shold3==null){
        }
        else{
          if(!bean.shold3.equals(this.shold3))
               return false;
     }
      //compare field shold4
      if((this.shold4==null && bean.shold4!=null) || (this.shold4!=null && bean.shold4==null))
          return false;
        else if(this.shold4==null && bean.shold4==null){
        }
        else{
          if(!bean.shold4.equals(this.shold4))
               return false;
     }
      //compare field sext1
      if((this.sext1==null && bean.sext1!=null) || (this.sext1!=null && bean.sext1==null))
          return false;
        else if(this.sext1==null && bean.sext1==null){
        }
        else{
          if(!bean.sext1.equals(this.sext1))
               return false;
     }
      //compare field sext2
      if((this.sext2==null && bean.sext2!=null) || (this.sext2!=null && bean.sext2==null))
          return false;
        else if(this.sext2==null && bean.sext2==null){
        }
        else{
          if(!bean.sext2.equals(this.sext2))
               return false;
     }
      //compare field sext3
      if((this.sext3==null && bean.sext3!=null) || (this.sext3!=null && bean.sext3==null))
          return false;
        else if(this.sext3==null && bean.sext3==null){
        }
        else{
          if(!bean.sext3.equals(this.sext3))
               return false;
     }



        return true;
    }

    /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
  
		int _hash_ = 1;
		
        if(this.ivousrlno!=null)
          _hash_ = _hash_ * 31+ this.ivousrlno.hashCode() ;
        if(this.iseqno!=null)
          _hash_ = _hash_ * 31+ this.iseqno.hashCode() ;
        if(this.sid!=null)
          _hash_ = _hash_ * 31+ this.sid.hashCode() ;
        if(this.sagencycode!=null)
          _hash_ = _hash_ * 31+ this.sagencycode.hashCode() ;
        if(this.sagencyname!=null)
          _hash_ = _hash_ * 31+ this.sagencyname.hashCode() ;
        if(this.sfundtypecode!=null)
          _hash_ = _hash_ * 31+ this.sfundtypecode.hashCode() ;
        if(this.sfundtypename!=null)
          _hash_ = _hash_ * 31+ this.sfundtypename.hashCode() ;
        if(this.sagencyaccono!=null)
          _hash_ = _hash_ * 31+ this.sagencyaccono.hashCode() ;
        if(this.sagencyacconame!=null)
          _hash_ = _hash_ * 31+ this.sagencyacconame.hashCode() ;
        if(this.sagencybankname!=null)
          _hash_ = _hash_ * 31+ this.sagencybankname.hashCode() ;
        if(this.saccstatus!=null)
          _hash_ = _hash_ * 31+ this.saccstatus.hashCode() ;
        if(this.shold1!=null)
          _hash_ = _hash_ * 31+ this.shold1.hashCode() ;
        if(this.shold2!=null)
          _hash_ = _hash_ * 31+ this.shold2.hashCode() ;
        if(this.shold3!=null)
          _hash_ = _hash_ * 31+ this.shold3.hashCode() ;
        if(this.shold4!=null)
          _hash_ = _hash_ * 31+ this.shold4.hashCode() ;
        if(this.sext1!=null)
          _hash_ = _hash_ * 31+ this.sext1.hashCode() ;
        if(this.sext2!=null)
          _hash_ = _hash_ * 31+ this.sext2.hashCode() ;
        if(this.sext3!=null)
          _hash_ = _hash_ * 31+ this.sext3.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TfUnitrecordsubDto bean = new TfUnitrecordsubDto();

          bean.ivousrlno = this.ivousrlno;

          bean.iseqno = this.iseqno;

          if (this.sid != null)
            bean.sid = new String(this.sid);
          if (this.sagencycode != null)
            bean.sagencycode = new String(this.sagencycode);
          if (this.sagencyname != null)
            bean.sagencyname = new String(this.sagencyname);
          if (this.sfundtypecode != null)
            bean.sfundtypecode = new String(this.sfundtypecode);
          if (this.sfundtypename != null)
            bean.sfundtypename = new String(this.sfundtypename);
          if (this.sagencyaccono != null)
            bean.sagencyaccono = new String(this.sagencyaccono);
          if (this.sagencyacconame != null)
            bean.sagencyacconame = new String(this.sagencyacconame);
          if (this.sagencybankname != null)
            bean.sagencybankname = new String(this.sagencybankname);
          if (this.saccstatus != null)
            bean.saccstatus = new String(this.saccstatus);
          if (this.shold1 != null)
            bean.shold1 = new String(this.shold1);
          if (this.shold2 != null)
            bean.shold2 = new String(this.shold2);
          if (this.shold3 != null)
            bean.shold3 = new String(this.shold3);
          if (this.shold4 != null)
            bean.shold4 = new String(this.shold4);
          if (this.sext1 != null)
            bean.sext1 = new String(this.sext1);
          if (this.sext2 != null)
            bean.sext2 = new String(this.sext2);
          if (this.sext3 != null)
            bean.sext3 = new String(this.sext3);
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TfUnitrecordsubDto").append(sep);
        sb.append("[ivousrlno]").append(" = ").append(ivousrlno).append(sep);
        sb.append("[iseqno]").append(" = ").append(iseqno).append(sep);
        sb.append("[sid]").append(" = ").append(sid).append(sep);
        sb.append("[sagencycode]").append(" = ").append(sagencycode).append(sep);
        sb.append("[sagencyname]").append(" = ").append(sagencyname).append(sep);
        sb.append("[sfundtypecode]").append(" = ").append(sfundtypecode).append(sep);
        sb.append("[sfundtypename]").append(" = ").append(sfundtypename).append(sep);
        sb.append("[sagencyaccono]").append(" = ").append(sagencyaccono).append(sep);
        sb.append("[sagencyacconame]").append(" = ").append(sagencyacconame).append(sep);
        sb.append("[sagencybankname]").append(" = ").append(sagencybankname).append(sep);
        sb.append("[saccstatus]").append(" = ").append(saccstatus).append(sep);
        sb.append("[shold1]").append(" = ").append(shold1).append(sep);
        sb.append("[shold2]").append(" = ").append(shold2).append(sep);
        sb.append("[shold3]").append(" = ").append(shold3).append(sep);
        sb.append("[shold4]").append(" = ").append(shold4).append(sep);
        sb.append("[sext1]").append(" = ").append(sext1).append(sep);
        sb.append("[sext2]").append(" = ").append(sext2).append(sep);
        sb.append("[sext3]").append(" = ").append(sext3).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //check field I_VOUSRLNO
      if (this.getIvousrlno()==null)
             sb.append("I_VOUSRLNO不能为空; ");
      
    //check field I_SEQNO
      if (this.getIseqno()==null)
             sb.append("I_SEQNO不能为空; ");
      
    //check field S_ID
      if (this.getSid()==null)
             sb.append("S_ID序号不能为空; ");
      if (this.getSid()!=null)
             if (this.getSid().getBytes().length > 38)
                sb.append("S_ID序号宽度不能超过 "+38+"个字符; ");
    
    //check field S_AGENCYCODE
      if (this.getSagencycode()==null)
             sb.append("S_AGENCYCODE预算单位编码不能为空; ");
      if (this.getSagencycode()!=null)
             if (this.getSagencycode().getBytes().length > 42)
                sb.append("S_AGENCYCODE预算单位编码宽度不能超过 "+42+"个字符; ");
    
    //check field S_AGENCYNAME
      if (this.getSagencyname()==null)
             sb.append("S_AGENCYNAME预算单位名称不能为空; ");
      if (this.getSagencyname()!=null)
             if (this.getSagencyname().getBytes().length > 60)
                sb.append("S_AGENCYNAME预算单位名称宽度不能超过 "+60+"个字符; ");
    
    //check field S_FUNDTYPECODE
      if (this.getSfundtypecode()==null)
             sb.append("S_FUNDTYPECODE资金性质编码不能为空; ");
      if (this.getSfundtypecode()!=null)
             if (this.getSfundtypecode().getBytes().length > 42)
                sb.append("S_FUNDTYPECODE资金性质编码宽度不能超过 "+42+"个字符; ");
    
    //check field S_FUNDTYPENAME
      if (this.getSfundtypename()==null)
             sb.append("S_FUNDTYPENAME资金性质名称不能为空; ");
      if (this.getSfundtypename()!=null)
             if (this.getSfundtypename().getBytes().length > 60)
                sb.append("S_FUNDTYPENAME资金性质名称宽度不能超过 "+60+"个字符; ");
    
    //check field S_AGENCYACCONO
      if (this.getSagencyaccono()==null)
             sb.append("S_AGENCYACCONO单位账号不能为空; ");
      if (this.getSagencyaccono()!=null)
             if (this.getSagencyaccono().getBytes().length > 60)
                sb.append("S_AGENCYACCONO单位账号宽度不能超过 "+60+"个字符; ");
    
    //check field S_AGENCYACCONAME
      if (this.getSagencyacconame()==null)
             sb.append("S_AGENCYACCONAME单位账户名称不能为空; ");
      if (this.getSagencyacconame()!=null)
             if (this.getSagencyacconame().getBytes().length > 200)
                sb.append("S_AGENCYACCONAME单位账户名称宽度不能超过 "+200+"个字符; ");
    
    //check field S_AGENCYBANKNAME
      if (this.getSagencybankname()==null)
             sb.append("S_AGENCYBANKNAME单位帐户开户行不能为空; ");
      if (this.getSagencybankname()!=null)
             if (this.getSagencybankname().getBytes().length > 200)
                sb.append("S_AGENCYBANKNAME单位帐户开户行宽度不能超过 "+200+"个字符; ");
    
    //check field S_ACCSTATUS
      if (this.getSaccstatus()==null)
             sb.append("S_ACCSTATUS账户状态不能为空; ");
      if (this.getSaccstatus()!=null)
             if (this.getSaccstatus().getBytes().length > 5)
                sb.append("S_ACCSTATUS账户状态宽度不能超过 "+5+"个字符; ");
    
    //check field S_HOLD1
      if (this.getShold1()!=null)
             if (this.getShold1().getBytes().length > 42)
                sb.append("S_HOLD1预留字段1宽度不能超过 "+42+"个字符; ");
    
    //check field S_HOLD2
      if (this.getShold2()!=null)
             if (this.getShold2().getBytes().length > 42)
                sb.append("S_HOLD2预留字段2宽度不能超过 "+42+"个字符; ");
    
    //check field S_HOLD3
      if (this.getShold3()!=null)
             if (this.getShold3().getBytes().length > 42)
                sb.append("S_HOLD3预留字段3宽度不能超过 "+42+"个字符; ");
    
    //check field S_HOLD4
      if (this.getShold4()!=null)
             if (this.getShold4().getBytes().length > 42)
                sb.append("S_HOLD4预留字段4宽度不能超过 "+42+"个字符; ");
    
    //check field S_EXT1
      if (this.getSext1()!=null)
             if (this.getSext1().getBytes().length > 50)
                sb.append("S_EXT1扩展宽度不能超过 "+50+"个字符; ");
    
    //check field S_EXT2
      if (this.getSext2()!=null)
             if (this.getSext2().getBytes().length > 50)
                sb.append("S_EXT2扩展宽度不能超过 "+50+"个字符; ");
    
    //check field S_EXT3
      if (this.getSext3()!=null)
             if (this.getSext3().getBytes().length > 50)
                sb.append("S_EXT3扩展宽度不能超过 "+50+"个字符; ");
    

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
    //check field I_VOUSRLNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("I_VOUSRLNO")) {
               if (this.getIvousrlno()==null)
                    sb.append("I_VOUSRLNO 不能为空; ");
               break;
         }
  }
    
    //check field I_SEQNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("I_SEQNO")) {
               if (this.getIseqno()==null)
                    sb.append("I_SEQNO 不能为空; ");
               break;
         }
  }
    
    //check field S_ID
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_ID")) {
               if (this.getSid()==null)
                    sb.append("S_ID 序号不能为空; ");
               if (this.getSid()!=null)
                    if (this.getSid().getBytes().length > 38)
                        sb.append("S_ID 序号宽度不能超过 "+38+"个字符");
             break;
         }
  }
    
    //check field S_AGENCYCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_AGENCYCODE")) {
               if (this.getSagencycode()==null)
                    sb.append("S_AGENCYCODE 预算单位编码不能为空; ");
               if (this.getSagencycode()!=null)
                    if (this.getSagencycode().getBytes().length > 42)
                        sb.append("S_AGENCYCODE 预算单位编码宽度不能超过 "+42+"个字符");
             break;
         }
  }
    
    //check field S_AGENCYNAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_AGENCYNAME")) {
               if (this.getSagencyname()==null)
                    sb.append("S_AGENCYNAME 预算单位名称不能为空; ");
               if (this.getSagencyname()!=null)
                    if (this.getSagencyname().getBytes().length > 60)
                        sb.append("S_AGENCYNAME 预算单位名称宽度不能超过 "+60+"个字符");
             break;
         }
  }
    
    //check field S_FUNDTYPECODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_FUNDTYPECODE")) {
               if (this.getSfundtypecode()==null)
                    sb.append("S_FUNDTYPECODE 资金性质编码不能为空; ");
               if (this.getSfundtypecode()!=null)
                    if (this.getSfundtypecode().getBytes().length > 42)
                        sb.append("S_FUNDTYPECODE 资金性质编码宽度不能超过 "+42+"个字符");
             break;
         }
  }
    
    //check field S_FUNDTYPENAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_FUNDTYPENAME")) {
               if (this.getSfundtypename()==null)
                    sb.append("S_FUNDTYPENAME 资金性质名称不能为空; ");
               if (this.getSfundtypename()!=null)
                    if (this.getSfundtypename().getBytes().length > 60)
                        sb.append("S_FUNDTYPENAME 资金性质名称宽度不能超过 "+60+"个字符");
             break;
         }
  }
    
    //check field S_AGENCYACCONO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_AGENCYACCONO")) {
               if (this.getSagencyaccono()==null)
                    sb.append("S_AGENCYACCONO 单位账号不能为空; ");
               if (this.getSagencyaccono()!=null)
                    if (this.getSagencyaccono().getBytes().length > 60)
                        sb.append("S_AGENCYACCONO 单位账号宽度不能超过 "+60+"个字符");
             break;
         }
  }
    
    //check field S_AGENCYACCONAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_AGENCYACCONAME")) {
               if (this.getSagencyacconame()==null)
                    sb.append("S_AGENCYACCONAME 单位账户名称不能为空; ");
               if (this.getSagencyacconame()!=null)
                    if (this.getSagencyacconame().getBytes().length > 200)
                        sb.append("S_AGENCYACCONAME 单位账户名称宽度不能超过 "+200+"个字符");
             break;
         }
  }
    
    //check field S_AGENCYBANKNAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_AGENCYBANKNAME")) {
               if (this.getSagencybankname()==null)
                    sb.append("S_AGENCYBANKNAME 单位帐户开户行不能为空; ");
               if (this.getSagencybankname()!=null)
                    if (this.getSagencybankname().getBytes().length > 200)
                        sb.append("S_AGENCYBANKNAME 单位帐户开户行宽度不能超过 "+200+"个字符");
             break;
         }
  }
    
    //check field S_ACCSTATUS
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_ACCSTATUS")) {
               if (this.getSaccstatus()==null)
                    sb.append("S_ACCSTATUS 账户状态不能为空; ");
               if (this.getSaccstatus()!=null)
                    if (this.getSaccstatus().getBytes().length > 5)
                        sb.append("S_ACCSTATUS 账户状态宽度不能超过 "+5+"个字符");
             break;
         }
  }
    
    //check field S_HOLD1
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_HOLD1")) {
                 if (this.getShold1()!=null)
                    if (this.getShold1().getBytes().length > 42)
                        sb.append("S_HOLD1 预留字段1宽度不能超过 "+42+"个字符");
             break;
         }
  }
    
    //check field S_HOLD2
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_HOLD2")) {
                 if (this.getShold2()!=null)
                    if (this.getShold2().getBytes().length > 42)
                        sb.append("S_HOLD2 预留字段2宽度不能超过 "+42+"个字符");
             break;
         }
  }
    
    //check field S_HOLD3
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_HOLD3")) {
                 if (this.getShold3()!=null)
                    if (this.getShold3().getBytes().length > 42)
                        sb.append("S_HOLD3 预留字段3宽度不能超过 "+42+"个字符");
             break;
         }
  }
    
    //check field S_HOLD4
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_HOLD4")) {
                 if (this.getShold4()!=null)
                    if (this.getShold4().getBytes().length > 42)
                        sb.append("S_HOLD4 预留字段4宽度不能超过 "+42+"个字符");
             break;
         }
  }
    
    //check field S_EXT1
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_EXT1")) {
                 if (this.getSext1()!=null)
                    if (this.getSext1().getBytes().length > 50)
                        sb.append("S_EXT1 扩展宽度不能超过 "+50+"个字符");
             break;
         }
  }
    
    //check field S_EXT2
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_EXT2")) {
                 if (this.getSext2()!=null)
                    if (this.getSext2().getBytes().length > 50)
                        sb.append("S_EXT2 扩展宽度不能超过 "+50+"个字符");
             break;
         }
  }
    
    //check field S_EXT3
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_EXT3")) {
                 if (this.getSext3()!=null)
                    if (this.getSext3().getBytes().length > 50)
                        sb.append("S_EXT3 扩展宽度不能超过 "+50+"个字符");
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
      TfUnitrecordsubPK pk = new TfUnitrecordsubPK();
      pk.setIvousrlno(getIvousrlno());
      pk.setIseqno(getIseqno());
      return pk;
    };
}

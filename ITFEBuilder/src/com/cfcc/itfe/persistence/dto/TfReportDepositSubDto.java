    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TfReportDepositSubPK;
/**
 * <p>Title: DTO of table: TF_REPORT_DEPOSIT_SUB</p>
 * <p>Description: 库款账户对帐子表3513 Data Transfer Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:58 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  dto.vm version timestamp: 2008-01-08 16:30:00 
 *
 * @author win7
 */

public class TfReportDepositSubDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    * 外键联合主键 I_VOUSRLNO BIGINT , PK   , NOT NULL       */
    protected Long ivousrlno;
    /**
    * 联合主键 I_SEQNO BIGINT , PK   , NOT NULL       */
    protected Long iseqno;
    /**
    * 序号 S_ID VARCHAR   , NOT NULL       */
    protected String sid;
    /**
    * 明细日期 S_ACCTDATE VARCHAR   , NOT NULL       */
    protected String sacctdate;
    /**
    * 收入金额 N_INCOMEAMT DECIMAL   , NOT NULL       */
    protected BigDecimal nincomeamt;
    /**
    * 支出金额 N_PAYAMT DECIMAL   , NOT NULL       */
    protected BigDecimal npayamt;
    /**
    * 对帐结果 S_XCHECKRESULT VARCHAR         */
    protected String sxcheckresult;
    /**
    * 不符原因 S_XCHECKREASON VARCHAR         */
    protected String sxcheckreason;
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
    /**
    * 扩展 S_EXT4 VARCHAR         */
    protected String sext4;
    /**
    * 扩展 S_EXT5 VARCHAR         */
    protected String sext5;


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
     *  外键联合主键 Setter I_VOUSRLNO, PK , NOT NULL        * */
    public void setIvousrlno(Long _ivousrlno) {
        this.ivousrlno = _ivousrlno;
    }


    public Long getIseqno()
    {
        return iseqno;
    }
     /**
     *  联合主键 Setter I_SEQNO, PK , NOT NULL        * */
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


    public String getSacctdate()
    {
        return sacctdate;
    }
     /**
     *  明细日期 Setter S_ACCTDATE , NOT NULL        * */
    public void setSacctdate(String _sacctdate) {
        this.sacctdate = _sacctdate;
    }


    public BigDecimal getNincomeamt()
    {
        return nincomeamt;
    }
     /**
     *  收入金额 Setter N_INCOMEAMT , NOT NULL        * */
    public void setNincomeamt(BigDecimal _nincomeamt) {
        this.nincomeamt = _nincomeamt;
    }


    public BigDecimal getNpayamt()
    {
        return npayamt;
    }
     /**
     *  支出金额 Setter N_PAYAMT , NOT NULL        * */
    public void setNpayamt(BigDecimal _npayamt) {
        this.npayamt = _npayamt;
    }


    public String getSxcheckresult()
    {
        return sxcheckresult;
    }
     /**
     *  对帐结果 Setter S_XCHECKRESULT        * */
    public void setSxcheckresult(String _sxcheckresult) {
        this.sxcheckresult = _sxcheckresult;
    }


    public String getSxcheckreason()
    {
        return sxcheckreason;
    }
     /**
     *  不符原因 Setter S_XCHECKREASON        * */
    public void setSxcheckreason(String _sxcheckreason) {
        this.sxcheckreason = _sxcheckreason;
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


    public String getSext4()
    {
        return sext4;
    }
     /**
     *  扩展 Setter S_EXT4        * */
    public void setSext4(String _sext4) {
        this.sext4 = _sext4;
    }


    public String getSext5()
    {
        return sext5;
    }
     /**
     *  扩展 Setter S_EXT5        * */
    public void setSext5(String _sext5) {
        this.sext5 = _sext5;
    }




/******************************************************
*
*  Get Column Name
*
*******************************************************/
    /**
    *  外键联合主键 Getter I_VOUSRLNO, PK , NOT NULL       * */
    public static String  columnIvousrlno()
    {
        return "I_VOUSRLNO";
    }
   
    /**
    *  联合主键 Getter I_SEQNO, PK , NOT NULL       * */
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
    *  明细日期 Getter S_ACCTDATE , NOT NULL       * */
    public static String  columnSacctdate()
    {
        return "S_ACCTDATE";
    }
   
    /**
    *  收入金额 Getter N_INCOMEAMT , NOT NULL       * */
    public static String  columnNincomeamt()
    {
        return "N_INCOMEAMT";
    }
   
    /**
    *  支出金额 Getter N_PAYAMT , NOT NULL       * */
    public static String  columnNpayamt()
    {
        return "N_PAYAMT";
    }
   
    /**
    *  对帐结果 Getter S_XCHECKRESULT       * */
    public static String  columnSxcheckresult()
    {
        return "S_XCHECKRESULT";
    }
   
    /**
    *  不符原因 Getter S_XCHECKREASON       * */
    public static String  columnSxcheckreason()
    {
        return "S_XCHECKREASON";
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
    *  扩展 Getter S_EXT4       * */
    public static String  columnSext4()
    {
        return "S_EXT4";
    }
   
    /**
    *  扩展 Getter S_EXT5       * */
    public static String  columnSext5()
    {
        return "S_EXT5";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "TF_REPORT_DEPOSIT_SUB";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[17];        
        columnNames[0]="I_VOUSRLNO";
        columnNames[1]="I_SEQNO";
        columnNames[2]="S_ID";
        columnNames[3]="S_ACCTDATE";
        columnNames[4]="N_INCOMEAMT";
        columnNames[5]="N_PAYAMT";
        columnNames[6]="S_XCHECKRESULT";
        columnNames[7]="S_XCHECKREASON";
        columnNames[8]="S_HOLD1";
        columnNames[9]="S_HOLD2";
        columnNames[10]="S_HOLD3";
        columnNames[11]="S_HOLD4";
        columnNames[12]="S_EXT1";
        columnNames[13]="S_EXT2";
        columnNames[14]="S_EXT3";
        columnNames[15]="S_EXT4";
        columnNames[16]="S_EXT5";
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

        if (obj == null || !(obj instanceof TfReportDepositSubDto))
            return false;

        TfReportDepositSubDto bean = (TfReportDepositSubDto) obj;


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
      //compare field sacctdate
      if((this.sacctdate==null && bean.sacctdate!=null) || (this.sacctdate!=null && bean.sacctdate==null))
          return false;
        else if(this.sacctdate==null && bean.sacctdate==null){
        }
        else{
          if(!bean.sacctdate.equals(this.sacctdate))
               return false;
     }
      //compare field nincomeamt
      if((this.nincomeamt==null && bean.nincomeamt!=null) || (this.nincomeamt!=null && bean.nincomeamt==null))
          return false;
        else if(this.nincomeamt==null && bean.nincomeamt==null){
        }
        else{
          if(!bean.nincomeamt.equals(this.nincomeamt))
               return false;
     }
      //compare field npayamt
      if((this.npayamt==null && bean.npayamt!=null) || (this.npayamt!=null && bean.npayamt==null))
          return false;
        else if(this.npayamt==null && bean.npayamt==null){
        }
        else{
          if(!bean.npayamt.equals(this.npayamt))
               return false;
     }
      //compare field sxcheckresult
      if((this.sxcheckresult==null && bean.sxcheckresult!=null) || (this.sxcheckresult!=null && bean.sxcheckresult==null))
          return false;
        else if(this.sxcheckresult==null && bean.sxcheckresult==null){
        }
        else{
          if(!bean.sxcheckresult.equals(this.sxcheckresult))
               return false;
     }
      //compare field sxcheckreason
      if((this.sxcheckreason==null && bean.sxcheckreason!=null) || (this.sxcheckreason!=null && bean.sxcheckreason==null))
          return false;
        else if(this.sxcheckreason==null && bean.sxcheckreason==null){
        }
        else{
          if(!bean.sxcheckreason.equals(this.sxcheckreason))
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
      //compare field sext4
      if((this.sext4==null && bean.sext4!=null) || (this.sext4!=null && bean.sext4==null))
          return false;
        else if(this.sext4==null && bean.sext4==null){
        }
        else{
          if(!bean.sext4.equals(this.sext4))
               return false;
     }
      //compare field sext5
      if((this.sext5==null && bean.sext5!=null) || (this.sext5!=null && bean.sext5==null))
          return false;
        else if(this.sext5==null && bean.sext5==null){
        }
        else{
          if(!bean.sext5.equals(this.sext5))
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
        if(this.sacctdate!=null)
          _hash_ = _hash_ * 31+ this.sacctdate.hashCode() ;
        if(this.nincomeamt!=null)
          _hash_ = _hash_ * 31+ this.nincomeamt.hashCode() ;
        if(this.npayamt!=null)
          _hash_ = _hash_ * 31+ this.npayamt.hashCode() ;
        if(this.sxcheckresult!=null)
          _hash_ = _hash_ * 31+ this.sxcheckresult.hashCode() ;
        if(this.sxcheckreason!=null)
          _hash_ = _hash_ * 31+ this.sxcheckreason.hashCode() ;
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
        if(this.sext4!=null)
          _hash_ = _hash_ * 31+ this.sext4.hashCode() ;
        if(this.sext5!=null)
          _hash_ = _hash_ * 31+ this.sext5.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TfReportDepositSubDto bean = new TfReportDepositSubDto();

          bean.ivousrlno = this.ivousrlno;

          bean.iseqno = this.iseqno;

          if (this.sid != null)
            bean.sid = new String(this.sid);
          if (this.sacctdate != null)
            bean.sacctdate = new String(this.sacctdate);
          if (this.nincomeamt != null)
            bean.nincomeamt = new BigDecimal(this.nincomeamt.toString());
          if (this.npayamt != null)
            bean.npayamt = new BigDecimal(this.npayamt.toString());
          if (this.sxcheckresult != null)
            bean.sxcheckresult = new String(this.sxcheckresult);
          if (this.sxcheckreason != null)
            bean.sxcheckreason = new String(this.sxcheckreason);
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
          if (this.sext4 != null)
            bean.sext4 = new String(this.sext4);
          if (this.sext5 != null)
            bean.sext5 = new String(this.sext5);
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TfReportDepositSubDto").append(sep);
        sb.append("[ivousrlno]").append(" = ").append(ivousrlno).append(sep);
        sb.append("[iseqno]").append(" = ").append(iseqno).append(sep);
        sb.append("[sid]").append(" = ").append(sid).append(sep);
        sb.append("[sacctdate]").append(" = ").append(sacctdate).append(sep);
        sb.append("[nincomeamt]").append(" = ").append(nincomeamt).append(sep);
        sb.append("[npayamt]").append(" = ").append(npayamt).append(sep);
        sb.append("[sxcheckresult]").append(" = ").append(sxcheckresult).append(sep);
        sb.append("[sxcheckreason]").append(" = ").append(sxcheckreason).append(sep);
        sb.append("[shold1]").append(" = ").append(shold1).append(sep);
        sb.append("[shold2]").append(" = ").append(shold2).append(sep);
        sb.append("[shold3]").append(" = ").append(shold3).append(sep);
        sb.append("[shold4]").append(" = ").append(shold4).append(sep);
        sb.append("[sext1]").append(" = ").append(sext1).append(sep);
        sb.append("[sext2]").append(" = ").append(sext2).append(sep);
        sb.append("[sext3]").append(" = ").append(sext3).append(sep);
        sb.append("[sext4]").append(" = ").append(sext4).append(sep);
        sb.append("[sext5]").append(" = ").append(sext5).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //check field I_VOUSRLNO
      if (this.getIvousrlno()==null)
             sb.append("I_VOUSRLNO外键联合主键不能为空; ");
      
    //check field I_SEQNO
      if (this.getIseqno()==null)
             sb.append("I_SEQNO联合主键不能为空; ");
      
    //check field S_ID
      if (this.getSid()==null)
             sb.append("S_ID序号不能为空; ");
      if (this.getSid()!=null)
             if (this.getSid().getBytes().length > 38)
                sb.append("S_ID序号宽度不能超过 "+38+"个字符; ");
    
    //check field S_ACCTDATE
      if (this.getSacctdate()==null)
             sb.append("S_ACCTDATE明细日期不能为空; ");
      if (this.getSacctdate()!=null)
             if (this.getSacctdate().getBytes().length > 8)
                sb.append("S_ACCTDATE明细日期宽度不能超过 "+8+"个字符; ");
    
    //check field N_INCOMEAMT
      if (this.getNincomeamt()==null)
             sb.append("N_INCOMEAMT收入金额不能为空; ");
      
    //check field N_PAYAMT
      if (this.getNpayamt()==null)
             sb.append("N_PAYAMT支出金额不能为空; ");
      
    //check field S_XCHECKRESULT
      if (this.getSxcheckresult()!=null)
             if (this.getSxcheckresult().getBytes().length > 5)
                sb.append("S_XCHECKRESULT对帐结果宽度不能超过 "+5+"个字符; ");
    
    //check field S_XCHECKREASON
      if (this.getSxcheckreason()!=null)
             if (this.getSxcheckreason().getBytes().length > 200)
                sb.append("S_XCHECKREASON不符原因宽度不能超过 "+200+"个字符; ");
    
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
    
    //check field S_EXT4
      if (this.getSext4()!=null)
             if (this.getSext4().getBytes().length > 50)
                sb.append("S_EXT4扩展宽度不能超过 "+50+"个字符; ");
    
    //check field S_EXT5
      if (this.getSext5()!=null)
             if (this.getSext5().getBytes().length > 50)
                sb.append("S_EXT5扩展宽度不能超过 "+50+"个字符; ");
    

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
                    sb.append("I_VOUSRLNO 外键联合主键不能为空; ");
               break;
         }
  }
    
    //check field I_SEQNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("I_SEQNO")) {
               if (this.getIseqno()==null)
                    sb.append("I_SEQNO 联合主键不能为空; ");
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
    
    //check field S_ACCTDATE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_ACCTDATE")) {
               if (this.getSacctdate()==null)
                    sb.append("S_ACCTDATE 明细日期不能为空; ");
               if (this.getSacctdate()!=null)
                    if (this.getSacctdate().getBytes().length > 8)
                        sb.append("S_ACCTDATE 明细日期宽度不能超过 "+8+"个字符");
             break;
         }
  }
    
    //check field N_INCOMEAMT
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("N_INCOMEAMT")) {
               if (this.getNincomeamt()==null)
                    sb.append("N_INCOMEAMT 收入金额不能为空; ");
               break;
         }
  }
    
    //check field N_PAYAMT
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("N_PAYAMT")) {
               if (this.getNpayamt()==null)
                    sb.append("N_PAYAMT 支出金额不能为空; ");
               break;
         }
  }
    
    //check field S_XCHECKRESULT
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_XCHECKRESULT")) {
                 if (this.getSxcheckresult()!=null)
                    if (this.getSxcheckresult().getBytes().length > 5)
                        sb.append("S_XCHECKRESULT 对帐结果宽度不能超过 "+5+"个字符");
             break;
         }
  }
    
    //check field S_XCHECKREASON
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_XCHECKREASON")) {
                 if (this.getSxcheckreason()!=null)
                    if (this.getSxcheckreason().getBytes().length > 200)
                        sb.append("S_XCHECKREASON 不符原因宽度不能超过 "+200+"个字符");
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
    
    //check field S_EXT4
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_EXT4")) {
                 if (this.getSext4()!=null)
                    if (this.getSext4().getBytes().length > 50)
                        sb.append("S_EXT4 扩展宽度不能超过 "+50+"个字符");
             break;
         }
  }
    
    //check field S_EXT5
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_EXT5")) {
                 if (this.getSext5()!=null)
                    if (this.getSext5().getBytes().length > 50)
                        sb.append("S_EXT5 扩展宽度不能超过 "+50+"个字符");
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
      TfReportDepositSubPK pk = new TfReportDepositSubPK();
      pk.setIvousrlno(getIvousrlno());
      pk.setIseqno(getIseqno());
      return pk;
    };
}

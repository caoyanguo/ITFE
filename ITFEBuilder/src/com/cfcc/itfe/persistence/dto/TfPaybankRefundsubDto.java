    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TfPaybankRefundsubPK;
/**
 * <p>Title: DTO of table: TF_PAYBANK_REFUNDSUB</p>
 * <p>Description: 收款银行退款通知2252子表 Data Transfer Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:58 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  dto.vm version timestamp: 2008-01-08 16:30:00 
 *
 * @author win7
 */

public class TfPaybankRefundsubDto   
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
    * 退款明细ID S_ID VARCHAR         */
    protected String sid;
    /**
    * 收款银行退款通知ID S_VOUCHERBILLID VARCHAR         */
    protected String svoucherbillid;
    /**
    * 原付款人账号 S_PAYACCTNO VARCHAR         */
    protected String spayacctno;
    /**
    * 原付款人名称 S_PAYACCTNAME VARCHAR         */
    protected String spayacctname;
    /**
    * 原付款人银行 S_PAYACCTBANKNAME VARCHAR         */
    protected String spayacctbankname;
    /**
    * 原收款人账号 S_PAYEEACCTNO VARCHAR         */
    protected String spayeeacctno;
    /**
    * 原收款人名称 S_PAYEEACCTNAME VARCHAR         */
    protected String spayeeacctname;
    /**
    * 原收款人银行 S_PAYEEACCTBANKNAME VARCHAR         */
    protected String spayeeacctbankname;
    /**
    * 原收款人银行行号 S_PAYEEACCTBANKNO VARCHAR         */
    protected String spayeeacctbankno;
    /**
    * 退款金额 N_PAYAMT DECIMAL         */
    protected BigDecimal npayamt;
    /**
    * 退款原因 S_REMARK VARCHAR         */
    protected String sremark;
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
     *  退款明细ID Setter S_ID        * */
    public void setSid(String _sid) {
        this.sid = _sid;
    }


    public String getSvoucherbillid()
    {
        return svoucherbillid;
    }
     /**
     *  收款银行退款通知ID Setter S_VOUCHERBILLID        * */
    public void setSvoucherbillid(String _svoucherbillid) {
        this.svoucherbillid = _svoucherbillid;
    }


    public String getSpayacctno()
    {
        return spayacctno;
    }
     /**
     *  原付款人账号 Setter S_PAYACCTNO        * */
    public void setSpayacctno(String _spayacctno) {
        this.spayacctno = _spayacctno;
    }


    public String getSpayacctname()
    {
        return spayacctname;
    }
     /**
     *  原付款人名称 Setter S_PAYACCTNAME        * */
    public void setSpayacctname(String _spayacctname) {
        this.spayacctname = _spayacctname;
    }


    public String getSpayacctbankname()
    {
        return spayacctbankname;
    }
     /**
     *  原付款人银行 Setter S_PAYACCTBANKNAME        * */
    public void setSpayacctbankname(String _spayacctbankname) {
        this.spayacctbankname = _spayacctbankname;
    }


    public String getSpayeeacctno()
    {
        return spayeeacctno;
    }
     /**
     *  原收款人账号 Setter S_PAYEEACCTNO        * */
    public void setSpayeeacctno(String _spayeeacctno) {
        this.spayeeacctno = _spayeeacctno;
    }


    public String getSpayeeacctname()
    {
        return spayeeacctname;
    }
     /**
     *  原收款人名称 Setter S_PAYEEACCTNAME        * */
    public void setSpayeeacctname(String _spayeeacctname) {
        this.spayeeacctname = _spayeeacctname;
    }


    public String getSpayeeacctbankname()
    {
        return spayeeacctbankname;
    }
     /**
     *  原收款人银行 Setter S_PAYEEACCTBANKNAME        * */
    public void setSpayeeacctbankname(String _spayeeacctbankname) {
        this.spayeeacctbankname = _spayeeacctbankname;
    }


    public String getSpayeeacctbankno()
    {
        return spayeeacctbankno;
    }
     /**
     *  原收款人银行行号 Setter S_PAYEEACCTBANKNO        * */
    public void setSpayeeacctbankno(String _spayeeacctbankno) {
        this.spayeeacctbankno = _spayeeacctbankno;
    }


    public BigDecimal getNpayamt()
    {
        return npayamt;
    }
     /**
     *  退款金额 Setter N_PAYAMT        * */
    public void setNpayamt(BigDecimal _npayamt) {
        this.npayamt = _npayamt;
    }


    public String getSremark()
    {
        return sremark;
    }
     /**
     *  退款原因 Setter S_REMARK        * */
    public void setSremark(String _sremark) {
        this.sremark = _sremark;
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
    *  退款明细ID Getter S_ID       * */
    public static String  columnSid()
    {
        return "S_ID";
    }
   
    /**
    *  收款银行退款通知ID Getter S_VOUCHERBILLID       * */
    public static String  columnSvoucherbillid()
    {
        return "S_VOUCHERBILLID";
    }
   
    /**
    *  原付款人账号 Getter S_PAYACCTNO       * */
    public static String  columnSpayacctno()
    {
        return "S_PAYACCTNO";
    }
   
    /**
    *  原付款人名称 Getter S_PAYACCTNAME       * */
    public static String  columnSpayacctname()
    {
        return "S_PAYACCTNAME";
    }
   
    /**
    *  原付款人银行 Getter S_PAYACCTBANKNAME       * */
    public static String  columnSpayacctbankname()
    {
        return "S_PAYACCTBANKNAME";
    }
   
    /**
    *  原收款人账号 Getter S_PAYEEACCTNO       * */
    public static String  columnSpayeeacctno()
    {
        return "S_PAYEEACCTNO";
    }
   
    /**
    *  原收款人名称 Getter S_PAYEEACCTNAME       * */
    public static String  columnSpayeeacctname()
    {
        return "S_PAYEEACCTNAME";
    }
   
    /**
    *  原收款人银行 Getter S_PAYEEACCTBANKNAME       * */
    public static String  columnSpayeeacctbankname()
    {
        return "S_PAYEEACCTBANKNAME";
    }
   
    /**
    *  原收款人银行行号 Getter S_PAYEEACCTBANKNO       * */
    public static String  columnSpayeeacctbankno()
    {
        return "S_PAYEEACCTBANKNO";
    }
   
    /**
    *  退款金额 Getter N_PAYAMT       * */
    public static String  columnNpayamt()
    {
        return "N_PAYAMT";
    }
   
    /**
    *  退款原因 Getter S_REMARK       * */
    public static String  columnSremark()
    {
        return "S_REMARK";
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
        return "TF_PAYBANK_REFUNDSUB";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[20];        
        columnNames[0]="I_VOUSRLNO";
        columnNames[1]="I_SEQNO";
        columnNames[2]="S_ID";
        columnNames[3]="S_VOUCHERBILLID";
        columnNames[4]="S_PAYACCTNO";
        columnNames[5]="S_PAYACCTNAME";
        columnNames[6]="S_PAYACCTBANKNAME";
        columnNames[7]="S_PAYEEACCTNO";
        columnNames[8]="S_PAYEEACCTNAME";
        columnNames[9]="S_PAYEEACCTBANKNAME";
        columnNames[10]="S_PAYEEACCTBANKNO";
        columnNames[11]="N_PAYAMT";
        columnNames[12]="S_REMARK";
        columnNames[13]="S_HOLD1";
        columnNames[14]="S_HOLD2";
        columnNames[15]="S_HOLD3";
        columnNames[16]="S_HOLD4";
        columnNames[17]="S_EXT1";
        columnNames[18]="S_EXT2";
        columnNames[19]="S_EXT3";
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

        if (obj == null || !(obj instanceof TfPaybankRefundsubDto))
            return false;

        TfPaybankRefundsubDto bean = (TfPaybankRefundsubDto) obj;


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
      //compare field svoucherbillid
      if((this.svoucherbillid==null && bean.svoucherbillid!=null) || (this.svoucherbillid!=null && bean.svoucherbillid==null))
          return false;
        else if(this.svoucherbillid==null && bean.svoucherbillid==null){
        }
        else{
          if(!bean.svoucherbillid.equals(this.svoucherbillid))
               return false;
     }
      //compare field spayacctno
      if((this.spayacctno==null && bean.spayacctno!=null) || (this.spayacctno!=null && bean.spayacctno==null))
          return false;
        else if(this.spayacctno==null && bean.spayacctno==null){
        }
        else{
          if(!bean.spayacctno.equals(this.spayacctno))
               return false;
     }
      //compare field spayacctname
      if((this.spayacctname==null && bean.spayacctname!=null) || (this.spayacctname!=null && bean.spayacctname==null))
          return false;
        else if(this.spayacctname==null && bean.spayacctname==null){
        }
        else{
          if(!bean.spayacctname.equals(this.spayacctname))
               return false;
     }
      //compare field spayacctbankname
      if((this.spayacctbankname==null && bean.spayacctbankname!=null) || (this.spayacctbankname!=null && bean.spayacctbankname==null))
          return false;
        else if(this.spayacctbankname==null && bean.spayacctbankname==null){
        }
        else{
          if(!bean.spayacctbankname.equals(this.spayacctbankname))
               return false;
     }
      //compare field spayeeacctno
      if((this.spayeeacctno==null && bean.spayeeacctno!=null) || (this.spayeeacctno!=null && bean.spayeeacctno==null))
          return false;
        else if(this.spayeeacctno==null && bean.spayeeacctno==null){
        }
        else{
          if(!bean.spayeeacctno.equals(this.spayeeacctno))
               return false;
     }
      //compare field spayeeacctname
      if((this.spayeeacctname==null && bean.spayeeacctname!=null) || (this.spayeeacctname!=null && bean.spayeeacctname==null))
          return false;
        else if(this.spayeeacctname==null && bean.spayeeacctname==null){
        }
        else{
          if(!bean.spayeeacctname.equals(this.spayeeacctname))
               return false;
     }
      //compare field spayeeacctbankname
      if((this.spayeeacctbankname==null && bean.spayeeacctbankname!=null) || (this.spayeeacctbankname!=null && bean.spayeeacctbankname==null))
          return false;
        else if(this.spayeeacctbankname==null && bean.spayeeacctbankname==null){
        }
        else{
          if(!bean.spayeeacctbankname.equals(this.spayeeacctbankname))
               return false;
     }
      //compare field spayeeacctbankno
      if((this.spayeeacctbankno==null && bean.spayeeacctbankno!=null) || (this.spayeeacctbankno!=null && bean.spayeeacctbankno==null))
          return false;
        else if(this.spayeeacctbankno==null && bean.spayeeacctbankno==null){
        }
        else{
          if(!bean.spayeeacctbankno.equals(this.spayeeacctbankno))
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
      //compare field sremark
      if((this.sremark==null && bean.sremark!=null) || (this.sremark!=null && bean.sremark==null))
          return false;
        else if(this.sremark==null && bean.sremark==null){
        }
        else{
          if(!bean.sremark.equals(this.sremark))
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
        if(this.svoucherbillid!=null)
          _hash_ = _hash_ * 31+ this.svoucherbillid.hashCode() ;
        if(this.spayacctno!=null)
          _hash_ = _hash_ * 31+ this.spayacctno.hashCode() ;
        if(this.spayacctname!=null)
          _hash_ = _hash_ * 31+ this.spayacctname.hashCode() ;
        if(this.spayacctbankname!=null)
          _hash_ = _hash_ * 31+ this.spayacctbankname.hashCode() ;
        if(this.spayeeacctno!=null)
          _hash_ = _hash_ * 31+ this.spayeeacctno.hashCode() ;
        if(this.spayeeacctname!=null)
          _hash_ = _hash_ * 31+ this.spayeeacctname.hashCode() ;
        if(this.spayeeacctbankname!=null)
          _hash_ = _hash_ * 31+ this.spayeeacctbankname.hashCode() ;
        if(this.spayeeacctbankno!=null)
          _hash_ = _hash_ * 31+ this.spayeeacctbankno.hashCode() ;
        if(this.npayamt!=null)
          _hash_ = _hash_ * 31+ this.npayamt.hashCode() ;
        if(this.sremark!=null)
          _hash_ = _hash_ * 31+ this.sremark.hashCode() ;
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
        TfPaybankRefundsubDto bean = new TfPaybankRefundsubDto();

          bean.ivousrlno = this.ivousrlno;

          bean.iseqno = this.iseqno;

          if (this.sid != null)
            bean.sid = new String(this.sid);
          if (this.svoucherbillid != null)
            bean.svoucherbillid = new String(this.svoucherbillid);
          if (this.spayacctno != null)
            bean.spayacctno = new String(this.spayacctno);
          if (this.spayacctname != null)
            bean.spayacctname = new String(this.spayacctname);
          if (this.spayacctbankname != null)
            bean.spayacctbankname = new String(this.spayacctbankname);
          if (this.spayeeacctno != null)
            bean.spayeeacctno = new String(this.spayeeacctno);
          if (this.spayeeacctname != null)
            bean.spayeeacctname = new String(this.spayeeacctname);
          if (this.spayeeacctbankname != null)
            bean.spayeeacctbankname = new String(this.spayeeacctbankname);
          if (this.spayeeacctbankno != null)
            bean.spayeeacctbankno = new String(this.spayeeacctbankno);
          if (this.npayamt != null)
            bean.npayamt = new BigDecimal(this.npayamt.toString());
          if (this.sremark != null)
            bean.sremark = new String(this.sremark);
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
        sb.append("TfPaybankRefundsubDto").append(sep);
        sb.append("[ivousrlno]").append(" = ").append(ivousrlno).append(sep);
        sb.append("[iseqno]").append(" = ").append(iseqno).append(sep);
        sb.append("[sid]").append(" = ").append(sid).append(sep);
        sb.append("[svoucherbillid]").append(" = ").append(svoucherbillid).append(sep);
        sb.append("[spayacctno]").append(" = ").append(spayacctno).append(sep);
        sb.append("[spayacctname]").append(" = ").append(spayacctname).append(sep);
        sb.append("[spayacctbankname]").append(" = ").append(spayacctbankname).append(sep);
        sb.append("[spayeeacctno]").append(" = ").append(spayeeacctno).append(sep);
        sb.append("[spayeeacctname]").append(" = ").append(spayeeacctname).append(sep);
        sb.append("[spayeeacctbankname]").append(" = ").append(spayeeacctbankname).append(sep);
        sb.append("[spayeeacctbankno]").append(" = ").append(spayeeacctbankno).append(sep);
        sb.append("[npayamt]").append(" = ").append(npayamt).append(sep);
        sb.append("[sremark]").append(" = ").append(sremark).append(sep);
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
      if (this.getSid()!=null)
             if (this.getSid().getBytes().length > 38)
                sb.append("S_ID退款明细ID宽度不能超过 "+38+"个字符; ");
    
    //check field S_VOUCHERBILLID
      if (this.getSvoucherbillid()!=null)
             if (this.getSvoucherbillid().getBytes().length > 38)
                sb.append("S_VOUCHERBILLID收款银行退款通知ID宽度不能超过 "+38+"个字符; ");
    
    //check field S_PAYACCTNO
      if (this.getSpayacctno()!=null)
             if (this.getSpayacctno().getBytes().length > 42)
                sb.append("S_PAYACCTNO原付款人账号宽度不能超过 "+42+"个字符; ");
    
    //check field S_PAYACCTNAME
      if (this.getSpayacctname()!=null)
             if (this.getSpayacctname().getBytes().length > 120)
                sb.append("S_PAYACCTNAME原付款人名称宽度不能超过 "+120+"个字符; ");
    
    //check field S_PAYACCTBANKNAME
      if (this.getSpayacctbankname()!=null)
             if (this.getSpayacctbankname().getBytes().length > 60)
                sb.append("S_PAYACCTBANKNAME原付款人银行宽度不能超过 "+60+"个字符; ");
    
    //check field S_PAYEEACCTNO
      if (this.getSpayeeacctno()!=null)
             if (this.getSpayeeacctno().getBytes().length > 42)
                sb.append("S_PAYEEACCTNO原收款人账号宽度不能超过 "+42+"个字符; ");
    
    //check field S_PAYEEACCTNAME
      if (this.getSpayeeacctname()!=null)
             if (this.getSpayeeacctname().getBytes().length > 120)
                sb.append("S_PAYEEACCTNAME原收款人名称宽度不能超过 "+120+"个字符; ");
    
    //check field S_PAYEEACCTBANKNAME
      if (this.getSpayeeacctbankname()!=null)
             if (this.getSpayeeacctbankname().getBytes().length > 60)
                sb.append("S_PAYEEACCTBANKNAME原收款人银行宽度不能超过 "+60+"个字符; ");
    
    //check field S_PAYEEACCTBANKNO
      if (this.getSpayeeacctbankno()!=null)
             if (this.getSpayeeacctbankno().getBytes().length > 42)
                sb.append("S_PAYEEACCTBANKNO原收款人银行行号宽度不能超过 "+42+"个字符; ");
    
    //check field N_PAYAMT
      
    //check field S_REMARK
      if (this.getSremark()!=null)
             if (this.getSremark().getBytes().length > 255)
                sb.append("S_REMARK退款原因宽度不能超过 "+255+"个字符; ");
    
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
                 if (this.getSid()!=null)
                    if (this.getSid().getBytes().length > 38)
                        sb.append("S_ID 退款明细ID宽度不能超过 "+38+"个字符");
             break;
         }
  }
    
    //check field S_VOUCHERBILLID
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_VOUCHERBILLID")) {
                 if (this.getSvoucherbillid()!=null)
                    if (this.getSvoucherbillid().getBytes().length > 38)
                        sb.append("S_VOUCHERBILLID 收款银行退款通知ID宽度不能超过 "+38+"个字符");
             break;
         }
  }
    
    //check field S_PAYACCTNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_PAYACCTNO")) {
                 if (this.getSpayacctno()!=null)
                    if (this.getSpayacctno().getBytes().length > 42)
                        sb.append("S_PAYACCTNO 原付款人账号宽度不能超过 "+42+"个字符");
             break;
         }
  }
    
    //check field S_PAYACCTNAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_PAYACCTNAME")) {
                 if (this.getSpayacctname()!=null)
                    if (this.getSpayacctname().getBytes().length > 120)
                        sb.append("S_PAYACCTNAME 原付款人名称宽度不能超过 "+120+"个字符");
             break;
         }
  }
    
    //check field S_PAYACCTBANKNAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_PAYACCTBANKNAME")) {
                 if (this.getSpayacctbankname()!=null)
                    if (this.getSpayacctbankname().getBytes().length > 60)
                        sb.append("S_PAYACCTBANKNAME 原付款人银行宽度不能超过 "+60+"个字符");
             break;
         }
  }
    
    //check field S_PAYEEACCTNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_PAYEEACCTNO")) {
                 if (this.getSpayeeacctno()!=null)
                    if (this.getSpayeeacctno().getBytes().length > 42)
                        sb.append("S_PAYEEACCTNO 原收款人账号宽度不能超过 "+42+"个字符");
             break;
         }
  }
    
    //check field S_PAYEEACCTNAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_PAYEEACCTNAME")) {
                 if (this.getSpayeeacctname()!=null)
                    if (this.getSpayeeacctname().getBytes().length > 120)
                        sb.append("S_PAYEEACCTNAME 原收款人名称宽度不能超过 "+120+"个字符");
             break;
         }
  }
    
    //check field S_PAYEEACCTBANKNAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_PAYEEACCTBANKNAME")) {
                 if (this.getSpayeeacctbankname()!=null)
                    if (this.getSpayeeacctbankname().getBytes().length > 60)
                        sb.append("S_PAYEEACCTBANKNAME 原收款人银行宽度不能超过 "+60+"个字符");
             break;
         }
  }
    
    //check field S_PAYEEACCTBANKNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_PAYEEACCTBANKNO")) {
                 if (this.getSpayeeacctbankno()!=null)
                    if (this.getSpayeeacctbankno().getBytes().length > 42)
                        sb.append("S_PAYEEACCTBANKNO 原收款人银行行号宽度不能超过 "+42+"个字符");
             break;
         }
  }
    
    //check field N_PAYAMT
          
    //check field S_REMARK
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_REMARK")) {
                 if (this.getSremark()!=null)
                    if (this.getSremark().getBytes().length > 255)
                        sb.append("S_REMARK 退款原因宽度不能超过 "+255+"个字符");
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
      TfPaybankRefundsubPK pk = new TfPaybankRefundsubPK();
      pk.setIvousrlno(getIvousrlno());
      pk.setIseqno(getIseqno());
      return pk;
    };
}

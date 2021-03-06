package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: HTR_TAXORG_DEBT_REPORT</p>
 * <p>Description:  PK Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:55 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *
 *  pk.vm version timestamp: 2007-04-06 08:30:00
 * 
 * @author win7
 */

public class HtrTaxorgDebtReportPK implements IPK
{
    /**
    *   S_TAXORGCODE VARCHAR  , PK   , NOT NULL        */
    protected String staxorgcode;
    /**
    *   S_TRECODE CHARACTER  , PK   , NOT NULL        */
    protected String strecode;
    /**
    *   S_RPTDATE CHARACTER  , PK   , NOT NULL        */
    protected String srptdate;
    /**
    *   S_CASHBANK VARCHAR  , PK   , NOT NULL        */
    protected String scashbank;
    /**
    *   S_CORPUSSUBJECT VARCHAR  , PK   , NOT NULL        */
    protected String scorpussubject;
    /**
    *   S_ACCRUALSUBJECT VARCHAR  , PK   , NOT NULL        */
    protected String saccrualsubject;



    /*  Getter S_TAXORGCODE, PK , NOT NULL   */
    public String getStaxorgcode()
    {
        return staxorgcode;
    }

     /*   Setter S_TAXORGCODE, PK , NOT NULL   */
    public void setStaxorgcode(String _staxorgcode) {
        this.staxorgcode = _staxorgcode;
    }
    /*  Getter S_TRECODE, PK , NOT NULL   */
    public String getStrecode()
    {
        return strecode;
    }

     /*   Setter S_TRECODE, PK , NOT NULL   */
    public void setStrecode(String _strecode) {
        this.strecode = _strecode;
    }
    /*  Getter S_RPTDATE, PK , NOT NULL   */
    public String getSrptdate()
    {
        return srptdate;
    }

     /*   Setter S_RPTDATE, PK , NOT NULL   */
    public void setSrptdate(String _srptdate) {
        this.srptdate = _srptdate;
    }
    /*  Getter S_CASHBANK, PK , NOT NULL   */
    public String getScashbank()
    {
        return scashbank;
    }

     /*   Setter S_CASHBANK, PK , NOT NULL   */
    public void setScashbank(String _scashbank) {
        this.scashbank = _scashbank;
    }
    /*  Getter S_CORPUSSUBJECT, PK , NOT NULL   */
    public String getScorpussubject()
    {
        return scorpussubject;
    }

     /*   Setter S_CORPUSSUBJECT, PK , NOT NULL   */
    public void setScorpussubject(String _scorpussubject) {
        this.scorpussubject = _scorpussubject;
    }
    /*  Getter S_ACCRUALSUBJECT, PK , NOT NULL   */
    public String getSaccrualsubject()
    {
        return saccrualsubject;
    }

     /*   Setter S_ACCRUALSUBJECT, PK , NOT NULL   */
    public void setSaccrualsubject(String _saccrualsubject) {
        this.saccrualsubject = _saccrualsubject;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof HtrTaxorgDebtReportPK))
            return false;

        HtrTaxorgDebtReportPK bean = (HtrTaxorgDebtReportPK) obj;

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
  	      //compare field srptdate
      if((this.srptdate==null && bean.srptdate!=null) || (this.srptdate!=null && bean.srptdate==null))
          return false;
        else if(this.srptdate==null && bean.srptdate==null){
        }
        else{
          if(!bean.srptdate.equals(this.srptdate))
               return false;
     }
  	      //compare field scashbank
      if((this.scashbank==null && bean.scashbank!=null) || (this.scashbank!=null && bean.scashbank==null))
          return false;
        else if(this.scashbank==null && bean.scashbank==null){
        }
        else{
          if(!bean.scashbank.equals(this.scashbank))
               return false;
     }
  	      //compare field scorpussubject
      if((this.scorpussubject==null && bean.scorpussubject!=null) || (this.scorpussubject!=null && bean.scorpussubject==null))
          return false;
        else if(this.scorpussubject==null && bean.scorpussubject==null){
        }
        else{
          if(!bean.scorpussubject.equals(this.scorpussubject))
               return false;
     }
  	      //compare field saccrualsubject
      if((this.saccrualsubject==null && bean.saccrualsubject!=null) || (this.saccrualsubject!=null && bean.saccrualsubject==null))
          return false;
        else if(this.saccrualsubject==null && bean.saccrualsubject==null){
        }
        else{
          if(!bean.saccrualsubject.equals(this.saccrualsubject))
               return false;
     }
          return true;
    }


   /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
		int __hash__ = 1;
		

        if(this.staxorgcode!=null)
          __hash__ = __hash__ * 31+ this.staxorgcode.hashCode() ;

        if(this.strecode!=null)
          __hash__ = __hash__ * 31+ this.strecode.hashCode() ;

        if(this.srptdate!=null)
          __hash__ = __hash__ * 31+ this.srptdate.hashCode() ;

        if(this.scashbank!=null)
          __hash__ = __hash__ * 31+ this.scashbank.hashCode() ;

        if(this.scorpussubject!=null)
          __hash__ = __hash__ * 31+ this.scorpussubject.hashCode() ;

        if(this.saccrualsubject!=null)
          __hash__ = __hash__ * 31+ this.saccrualsubject.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        HtrTaxorgDebtReportPK bean = new HtrTaxorgDebtReportPK();
          if(this.staxorgcode!=null){
                  bean.staxorgcode = new String(this.staxorgcode);
            }
             if(this.strecode!=null){
                  bean.strecode = new String(this.strecode);
            }
             if(this.srptdate!=null){
                  bean.srptdate = new String(this.srptdate);
            }
             if(this.scashbank!=null){
                  bean.scashbank = new String(this.scashbank);
            }
             if(this.scorpussubject!=null){
                  bean.scorpussubject = new String(this.scorpussubject);
            }
             if(this.saccrualsubject!=null){
                  bean.saccrualsubject = new String(this.saccrualsubject);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("HtrTaxorgDebtReportPK").append(":");
            sb.append("[staxorgcode]").append(" = ").append(staxorgcode).append(sep);
            sb.append("[strecode]").append(" = ").append(strecode).append(sep);
            sb.append("[srptdate]").append(" = ").append(srptdate).append(sep);
            sb.append("[scashbank]").append(" = ").append(scashbank).append(sep);
            sb.append("[scorpussubject]").append(" = ").append(scorpussubject).append(sep);
            sb.append("[saccrualsubject]").append(" = ").append(saccrualsubject).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getStaxorgcode()==null)
              sb.append("数据库表：HTR_TAXORG_DEBT_REPORT 字段：S_TAXORGCODE 不能为空; ");
                
       if (this.getStaxorgcode()!=null)
          if (this.getStaxorgcode().getBytes().length > 30)
             sb.append("数据库表：HTR_TAXORG_DEBT_REPORT 字段：S_TAXORGCODE 宽度不能超过 "+30);
                
        if (this.getStrecode()==null)
              sb.append("数据库表：HTR_TAXORG_DEBT_REPORT 字段：S_TRECODE 不能为空; ");
                
       if (this.getStrecode()!=null)
          if (this.getStrecode().getBytes().length > 10)
             sb.append("数据库表：HTR_TAXORG_DEBT_REPORT 字段：S_TRECODE 宽度不能超过 "+10);
                
        if (this.getSrptdate()==null)
              sb.append("数据库表：HTR_TAXORG_DEBT_REPORT 字段：S_RPTDATE 不能为空; ");
                
       if (this.getSrptdate()!=null)
          if (this.getSrptdate().getBytes().length > 8)
             sb.append("数据库表：HTR_TAXORG_DEBT_REPORT 字段：S_RPTDATE 宽度不能超过 "+8);
                
        if (this.getScashbank()==null)
              sb.append("数据库表：HTR_TAXORG_DEBT_REPORT 字段：S_CASHBANK 不能为空; ");
                
       if (this.getScashbank()!=null)
          if (this.getScashbank().getBytes().length > 20)
             sb.append("数据库表：HTR_TAXORG_DEBT_REPORT 字段：S_CASHBANK 宽度不能超过 "+20);
                
        if (this.getScorpussubject()==null)
              sb.append("数据库表：HTR_TAXORG_DEBT_REPORT 字段：S_CORPUSSUBJECT 不能为空; ");
                
       if (this.getScorpussubject()!=null)
          if (this.getScorpussubject().getBytes().length > 30)
             sb.append("数据库表：HTR_TAXORG_DEBT_REPORT 字段：S_CORPUSSUBJECT 宽度不能超过 "+30);
                
        if (this.getSaccrualsubject()==null)
              sb.append("数据库表：HTR_TAXORG_DEBT_REPORT 字段：S_ACCRUALSUBJECT 不能为空; ");
                
       if (this.getSaccrualsubject()!=null)
          if (this.getSaccrualsubject().getBytes().length > 30)
             sb.append("数据库表：HTR_TAXORG_DEBT_REPORT 字段：S_ACCRUALSUBJECT 宽度不能超过 "+30);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}

package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: TV_TAX_KIND</p>
 * <p>Description:  PK Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:04 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *
 *  pk.vm version timestamp: 2007-04-06 08:30:00
 * 
 * @author win7
 */

public class TvTaxKindPK implements IPK
{
    /**
    *   S_SEQ VARCHAR  , PK   , NOT NULL        */
    protected String sseq;
    /**
    *   I_PROJECTID INTEGER  , PK   , NOT NULL        */
    protected Integer iprojectid;



    /*  Getter S_SEQ, PK , NOT NULL   */
    public String getSseq()
    {
        return sseq;
    }

     /*   Setter S_SEQ, PK , NOT NULL   */
    public void setSseq(String _sseq) {
        this.sseq = _sseq;
    }
    /*  Getter I_PROJECTID, PK , NOT NULL   */
    public Integer getIprojectid()
    {
        return iprojectid;
    }

     /*   Setter I_PROJECTID, PK , NOT NULL   */
    public void setIprojectid(Integer _iprojectid) {
        this.iprojectid = _iprojectid;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TvTaxKindPK))
            return false;

        TvTaxKindPK bean = (TvTaxKindPK) obj;

	      //compare field sseq
      if((this.sseq==null && bean.sseq!=null) || (this.sseq!=null && bean.sseq==null))
          return false;
        else if(this.sseq==null && bean.sseq==null){
        }
        else{
          if(!bean.sseq.equals(this.sseq))
               return false;
     }
  	      //compare field iprojectid
      if((this.iprojectid==null && bean.iprojectid!=null) || (this.iprojectid!=null && bean.iprojectid==null))
          return false;
        else if(this.iprojectid==null && bean.iprojectid==null){
        }
        else{
          if(!bean.iprojectid.equals(this.iprojectid))
               return false;
     }
          return true;
    }


   /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
		int __hash__ = 1;
		

        if(this.sseq!=null)
          __hash__ = __hash__ * 31+ this.sseq.hashCode() ;

        if(this.iprojectid!=null)
          __hash__ = __hash__ * 31+ this.iprojectid.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TvTaxKindPK bean = new TvTaxKindPK();
          if(this.sseq!=null){
                  bean.sseq = new String(this.sseq);
            }
             if(this.iprojectid!=null){
                  bean.iprojectid = new Integer(this.iprojectid.toString());
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("TvTaxKindPK").append(":");
            sb.append("[sseq]").append(" = ").append(sseq).append(sep);
            sb.append("[iprojectid]").append(" = ").append(iprojectid).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSseq()==null)
              sb.append("数据库表：TV_TAX_KIND 字段：S_SEQ 不能为空; ");
                
       if (this.getSseq()!=null)
          if (this.getSseq().getBytes().length > 20)
             sb.append("数据库表：TV_TAX_KIND 字段：S_SEQ 宽度不能超过 "+20);
                
        if (this.getIprojectid()==null)
              sb.append("数据库表：TV_TAX_KIND 字段：I_PROJECTID 不能为空; ");
                	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}

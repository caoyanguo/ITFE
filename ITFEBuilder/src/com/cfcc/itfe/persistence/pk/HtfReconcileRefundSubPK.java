package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: HTF_RECONCILE_REFUND_SUB</p>
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

public class HtfReconcileRefundSubPK implements IPK
{
    /**
    *   I_VOUSRLNO BIGINT  , PK   , NOT NULL        */
    protected Long ivousrlno;
    /**
    *   I_SEQNO BIGINT  , PK   , NOT NULL        */
    protected Long iseqno;



    /*  Getter I_VOUSRLNO, PK , NOT NULL   */
    public Long getIvousrlno()
    {
        return ivousrlno;
    }

     /*   Setter I_VOUSRLNO, PK , NOT NULL   */
    public void setIvousrlno(Long _ivousrlno) {
        this.ivousrlno = _ivousrlno;
    }
    /*  Getter I_SEQNO, PK , NOT NULL   */
    public Long getIseqno()
    {
        return iseqno;
    }

     /*   Setter I_SEQNO, PK , NOT NULL   */
    public void setIseqno(Long _iseqno) {
        this.iseqno = _iseqno;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof HtfReconcileRefundSubPK))
            return false;

        HtfReconcileRefundSubPK bean = (HtfReconcileRefundSubPK) obj;

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
          return true;
    }


   /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
		int __hash__ = 1;
		

        if(this.ivousrlno!=null)
          __hash__ = __hash__ * 31+ this.ivousrlno.hashCode() ;

        if(this.iseqno!=null)
          __hash__ = __hash__ * 31+ this.iseqno.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        HtfReconcileRefundSubPK bean = new HtfReconcileRefundSubPK();
          if(this.ivousrlno!=null){
                  bean.ivousrlno = new Long(this.ivousrlno.toString());
            }
             if(this.iseqno!=null){
                  bean.iseqno = new Long(this.iseqno.toString());
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("HtfReconcileRefundSubPK").append(":");
            sb.append("[ivousrlno]").append(" = ").append(ivousrlno).append(sep);
            sb.append("[iseqno]").append(" = ").append(iseqno).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getIvousrlno()==null)
              sb.append("数据库表：HTF_RECONCILE_REFUND_SUB 字段：I_VOUSRLNO 不能为空; ");
                        
        if (this.getIseqno()==null)
              sb.append("数据库表：HTF_RECONCILE_REFUND_SUB 字段：I_SEQNO 不能为空; ");
                	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}

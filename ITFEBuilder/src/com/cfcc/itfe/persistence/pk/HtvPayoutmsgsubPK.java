package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: HTV_PAYOUTMSGSUB</p>
 * <p>Description:  PK Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:56 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *
 *  pk.vm version timestamp: 2007-04-06 08:30:00
 * 
 * @author win7
 */

public class HtvPayoutmsgsubPK implements IPK
{
    /**
    *   S_BIZNO VARCHAR  , PK   , NOT NULL        */
    protected String sbizno;
    /**
    *   S_SEQNO INTEGER  , PK   , NOT NULL        */
    protected Integer sseqno;



    /*  Getter S_BIZNO, PK , NOT NULL   */
    public String getSbizno()
    {
        return sbizno;
    }

     /*   Setter S_BIZNO, PK , NOT NULL   */
    public void setSbizno(String _sbizno) {
        this.sbizno = _sbizno;
    }
    /*  Getter S_SEQNO, PK , NOT NULL   */
    public Integer getSseqno()
    {
        return sseqno;
    }

     /*   Setter S_SEQNO, PK , NOT NULL   */
    public void setSseqno(Integer _sseqno) {
        this.sseqno = _sseqno;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof HtvPayoutmsgsubPK))
            return false;

        HtvPayoutmsgsubPK bean = (HtvPayoutmsgsubPK) obj;

	      //compare field sbizno
      if((this.sbizno==null && bean.sbizno!=null) || (this.sbizno!=null && bean.sbizno==null))
          return false;
        else if(this.sbizno==null && bean.sbizno==null){
        }
        else{
          if(!bean.sbizno.equals(this.sbizno))
               return false;
     }
  	      //compare field sseqno
      if((this.sseqno==null && bean.sseqno!=null) || (this.sseqno!=null && bean.sseqno==null))
          return false;
        else if(this.sseqno==null && bean.sseqno==null){
        }
        else{
          if(!bean.sseqno.equals(this.sseqno))
               return false;
     }
          return true;
    }


   /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
		int __hash__ = 1;
		

        if(this.sbizno!=null)
          __hash__ = __hash__ * 31+ this.sbizno.hashCode() ;

        if(this.sseqno!=null)
          __hash__ = __hash__ * 31+ this.sseqno.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        HtvPayoutmsgsubPK bean = new HtvPayoutmsgsubPK();
          if(this.sbizno!=null){
                  bean.sbizno = new String(this.sbizno);
            }
             if(this.sseqno!=null){
                  bean.sseqno = new Integer(this.sseqno.toString());
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("HtvPayoutmsgsubPK").append(":");
            sb.append("[sbizno]").append(" = ").append(sbizno).append(sep);
            sb.append("[sseqno]").append(" = ").append(sseqno).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSbizno()==null)
              sb.append("数据库表：HTV_PAYOUTMSGSUB 字段：S_BIZNO 不能为空; ");
                
       if (this.getSbizno()!=null)
          if (this.getSbizno().getBytes().length > 20)
             sb.append("数据库表：HTV_PAYOUTMSGSUB 字段：S_BIZNO 宽度不能超过 "+20);
                
        if (this.getSseqno()==null)
              sb.append("数据库表：HTV_PAYOUTMSGSUB 字段：S_SEQNO 不能为空; ");
                	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}

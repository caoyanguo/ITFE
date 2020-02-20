package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: TV_DIRECTPAYMSGSUB_SX</p>
 * <p>Description:  PK Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:01 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *
 *  pk.vm version timestamp: 2007-04-06 08:30:00
 * 
 * @author win7
 */

public class TvDirectpaymsgsubSxPK implements IPK
{
    /**
    *   I_VOUSRLNO BIGINT  , PK   , NOT NULL        */
    protected Long ivousrlno;
    /**
    *   I_DETAILSEQNO INTEGER  , PK   , NOT NULL        */
    protected Integer idetailseqno;



    /*  Getter I_VOUSRLNO, PK , NOT NULL   */
    public Long getIvousrlno()
    {
        return ivousrlno;
    }

     /*   Setter I_VOUSRLNO, PK , NOT NULL   */
    public void setIvousrlno(Long _ivousrlno) {
        this.ivousrlno = _ivousrlno;
    }
    /*  Getter I_DETAILSEQNO, PK , NOT NULL   */
    public Integer getIdetailseqno()
    {
        return idetailseqno;
    }

     /*   Setter I_DETAILSEQNO, PK , NOT NULL   */
    public void setIdetailseqno(Integer _idetailseqno) {
        this.idetailseqno = _idetailseqno;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TvDirectpaymsgsubSxPK))
            return false;

        TvDirectpaymsgsubSxPK bean = (TvDirectpaymsgsubSxPK) obj;

	      //compare field ivousrlno
      if((this.ivousrlno==null && bean.ivousrlno!=null) || (this.ivousrlno!=null && bean.ivousrlno==null))
          return false;
        else if(this.ivousrlno==null && bean.ivousrlno==null){
        }
        else{
          if(!bean.ivousrlno.equals(this.ivousrlno))
               return false;
     }
  	      //compare field idetailseqno
      if((this.idetailseqno==null && bean.idetailseqno!=null) || (this.idetailseqno!=null && bean.idetailseqno==null))
          return false;
        else if(this.idetailseqno==null && bean.idetailseqno==null){
        }
        else{
          if(!bean.idetailseqno.equals(this.idetailseqno))
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

        if(this.idetailseqno!=null)
          __hash__ = __hash__ * 31+ this.idetailseqno.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TvDirectpaymsgsubSxPK bean = new TvDirectpaymsgsubSxPK();
          if(this.ivousrlno!=null){
                  bean.ivousrlno = new Long(this.ivousrlno.toString());
            }
             if(this.idetailseqno!=null){
                  bean.idetailseqno = new Integer(this.idetailseqno.toString());
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("TvDirectpaymsgsubSxPK").append(":");
            sb.append("[ivousrlno]").append(" = ").append(ivousrlno).append(sep);
            sb.append("[idetailseqno]").append(" = ").append(idetailseqno).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getIvousrlno()==null)
              sb.append("数据库表：TV_DIRECTPAYMSGSUB_SX 字段：I_VOUSRLNO 不能为空; ");
                        
        if (this.getIdetailseqno()==null)
              sb.append("数据库表：TV_DIRECTPAYMSGSUB_SX 字段：I_DETAILSEQNO 不能为空; ");
                	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}

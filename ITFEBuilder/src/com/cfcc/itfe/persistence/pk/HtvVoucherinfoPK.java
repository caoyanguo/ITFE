package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: HTV_VOUCHERINFO</p>
 * <p>Description:  PK Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:57 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *
 *  pk.vm version timestamp: 2007-04-06 08:30:00
 * 
 * @author win7
 */

public class HtvVoucherinfoPK implements IPK
{
    /**
    *   S_DEALNO VARCHAR  , PK   , NOT NULL        */
    protected String sdealno;



    /*  Getter S_DEALNO, PK , NOT NULL   */
    public String getSdealno()
    {
        return sdealno;
    }

     /*   Setter S_DEALNO, PK , NOT NULL   */
    public void setSdealno(String _sdealno) {
        this.sdealno = _sdealno;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof HtvVoucherinfoPK))
            return false;

        HtvVoucherinfoPK bean = (HtvVoucherinfoPK) obj;

	      //compare field sdealno
      if((this.sdealno==null && bean.sdealno!=null) || (this.sdealno!=null && bean.sdealno==null))
          return false;
        else if(this.sdealno==null && bean.sdealno==null){
        }
        else{
          if(!bean.sdealno.equals(this.sdealno))
               return false;
     }
          return true;
    }


   /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
		int __hash__ = 1;
		

        if(this.sdealno!=null)
          __hash__ = __hash__ * 31+ this.sdealno.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        HtvVoucherinfoPK bean = new HtvVoucherinfoPK();
          if(this.sdealno!=null){
                  bean.sdealno = new String(this.sdealno);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("HtvVoucherinfoPK").append(":");
            sb.append("[sdealno]").append(" = ").append(sdealno).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSdealno()==null)
              sb.append("数据库表：HTV_VOUCHERINFO 字段：S_DEALNO 不能为空; ");
                
       if (this.getSdealno()!=null)
          if (this.getSdealno().getBytes().length > 16)
             sb.append("数据库表：HTV_VOUCHERINFO 字段：S_DEALNO 宽度不能超过 "+16);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}

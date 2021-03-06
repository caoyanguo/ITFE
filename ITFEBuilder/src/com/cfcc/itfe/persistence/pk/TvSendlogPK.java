package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: TV_SENDLOG</p>
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

public class TvSendlogPK implements IPK
{
    /**
    *   S_SENDNO VARCHAR  , PK   , NOT NULL        */
    protected String ssendno;



    /*  Getter S_SENDNO, PK , NOT NULL   */
    public String getSsendno()
    {
        return ssendno;
    }

     /*   Setter S_SENDNO, PK , NOT NULL   */
    public void setSsendno(String _ssendno) {
        this.ssendno = _ssendno;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TvSendlogPK))
            return false;

        TvSendlogPK bean = (TvSendlogPK) obj;

	      //compare field ssendno
      if((this.ssendno==null && bean.ssendno!=null) || (this.ssendno!=null && bean.ssendno==null))
          return false;
        else if(this.ssendno==null && bean.ssendno==null){
        }
        else{
          if(!bean.ssendno.equals(this.ssendno))
               return false;
     }
          return true;
    }


   /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
		int __hash__ = 1;
		

        if(this.ssendno!=null)
          __hash__ = __hash__ * 31+ this.ssendno.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TvSendlogPK bean = new TvSendlogPK();
          if(this.ssendno!=null){
                  bean.ssendno = new String(this.ssendno);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("TvSendlogPK").append(":");
            sb.append("[ssendno]").append(" = ").append(ssendno).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSsendno()==null)
              sb.append("数据库表：TV_SENDLOG 字段：S_SENDNO 不能为空; ");
                
       if (this.getSsendno()!=null)
          if (this.getSsendno().getBytes().length > 20)
             sb.append("数据库表：TV_SENDLOG 字段：S_SENDNO 宽度不能超过 "+20);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}

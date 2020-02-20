package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: HTV_PAYOUT_DETAIL_MAIN</p>
 * <p>Description: 实拨拨款凭证明细清单5257 PK Object  </p>
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

public class HtvPayoutDetailMainPK implements IPK
{
    /**
    *  拨款清单明细编号 S_ID VARCHAR  , PK   , NOT NULL        */
    protected String sid;



    /* 拨款清单明细编号 Getter S_ID, PK , NOT NULL   */
    public String getSid()
    {
        return sid;
    }

     /*  拨款清单明细编号 Setter S_ID, PK , NOT NULL   */
    public void setSid(String _sid) {
        this.sid = _sid;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof HtvPayoutDetailMainPK))
            return false;

        HtvPayoutDetailMainPK bean = (HtvPayoutDetailMainPK) obj;

	      //compare field sid
      if((this.sid==null && bean.sid!=null) || (this.sid!=null && bean.sid==null))
          return false;
        else if(this.sid==null && bean.sid==null){
        }
        else{
          if(!bean.sid.equals(this.sid))
               return false;
     }
          return true;
    }


   /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
		int __hash__ = 1;
		

        if(this.sid!=null)
          __hash__ = __hash__ * 31+ this.sid.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        HtvPayoutDetailMainPK bean = new HtvPayoutDetailMainPK();
          if(this.sid!=null){
                  bean.sid = new String(this.sid);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("HtvPayoutDetailMainPK").append(":");
            sb.append("[sid]").append(" = ").append(sid).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSid()==null)
              sb.append("数据库表：HTV_PAYOUT_DETAIL_MAIN 字段：S_ID 不能为空; ");
                
       if (this.getSid()!=null)
          if (this.getSid().getBytes().length > 38)
             sb.append("数据库表：HTV_PAYOUT_DETAIL_MAIN 字段：S_ID 宽度不能超过 "+38);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}

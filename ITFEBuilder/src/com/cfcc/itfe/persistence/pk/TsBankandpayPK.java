package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: TS_BANKANDPAY</p>
 * <p>Description:  PK Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:59 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *
 *  pk.vm version timestamp: 2007-04-06 08:30:00
 * 
 * @author win7
 */

public class TsBankandpayPK implements IPK
{
    /**
    *   S_ORGCODE VARCHAR  , PK   , NOT NULL        */
    protected String sorgcode;
    /**
    *   S_NATIONTAXBANK CHARACTER  , PK   , NOT NULL        */
    protected String snationtaxbank;
    /**
    *   S_AREATAXBANK CHARACTER  , PK   , NOT NULL        */
    protected String sareataxbank;



    /*  Getter S_ORGCODE, PK , NOT NULL   */
    public String getSorgcode()
    {
        return sorgcode;
    }

     /*   Setter S_ORGCODE, PK , NOT NULL   */
    public void setSorgcode(String _sorgcode) {
        this.sorgcode = _sorgcode;
    }
    /*  Getter S_NATIONTAXBANK, PK , NOT NULL   */
    public String getSnationtaxbank()
    {
        return snationtaxbank;
    }

     /*   Setter S_NATIONTAXBANK, PK , NOT NULL   */
    public void setSnationtaxbank(String _snationtaxbank) {
        this.snationtaxbank = _snationtaxbank;
    }
    /*  Getter S_AREATAXBANK, PK , NOT NULL   */
    public String getSareataxbank()
    {
        return sareataxbank;
    }

     /*   Setter S_AREATAXBANK, PK , NOT NULL   */
    public void setSareataxbank(String _sareataxbank) {
        this.sareataxbank = _sareataxbank;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TsBankandpayPK))
            return false;

        TsBankandpayPK bean = (TsBankandpayPK) obj;

	      //compare field sorgcode
      if((this.sorgcode==null && bean.sorgcode!=null) || (this.sorgcode!=null && bean.sorgcode==null))
          return false;
        else if(this.sorgcode==null && bean.sorgcode==null){
        }
        else{
          if(!bean.sorgcode.equals(this.sorgcode))
               return false;
     }
  	      //compare field snationtaxbank
      if((this.snationtaxbank==null && bean.snationtaxbank!=null) || (this.snationtaxbank!=null && bean.snationtaxbank==null))
          return false;
        else if(this.snationtaxbank==null && bean.snationtaxbank==null){
        }
        else{
          if(!bean.snationtaxbank.equals(this.snationtaxbank))
               return false;
     }
  	      //compare field sareataxbank
      if((this.sareataxbank==null && bean.sareataxbank!=null) || (this.sareataxbank!=null && bean.sareataxbank==null))
          return false;
        else if(this.sareataxbank==null && bean.sareataxbank==null){
        }
        else{
          if(!bean.sareataxbank.equals(this.sareataxbank))
               return false;
     }
          return true;
    }


   /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
		int __hash__ = 1;
		

        if(this.sorgcode!=null)
          __hash__ = __hash__ * 31+ this.sorgcode.hashCode() ;

        if(this.snationtaxbank!=null)
          __hash__ = __hash__ * 31+ this.snationtaxbank.hashCode() ;

        if(this.sareataxbank!=null)
          __hash__ = __hash__ * 31+ this.sareataxbank.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsBankandpayPK bean = new TsBankandpayPK();
          if(this.sorgcode!=null){
                  bean.sorgcode = new String(this.sorgcode);
            }
             if(this.snationtaxbank!=null){
                  bean.snationtaxbank = new String(this.snationtaxbank);
            }
             if(this.sareataxbank!=null){
                  bean.sareataxbank = new String(this.sareataxbank);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsBankandpayPK").append(":");
            sb.append("[sorgcode]").append(" = ").append(sorgcode).append(sep);
            sb.append("[snationtaxbank]").append(" = ").append(snationtaxbank).append(sep);
            sb.append("[sareataxbank]").append(" = ").append(sareataxbank).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSorgcode()==null)
              sb.append("数据库表：TS_BANKANDPAY 字段：S_ORGCODE 不能为空; ");
                
       if (this.getSorgcode()!=null)
          if (this.getSorgcode().getBytes().length > 12)
             sb.append("数据库表：TS_BANKANDPAY 字段：S_ORGCODE 宽度不能超过 "+12);
                
        if (this.getSnationtaxbank()==null)
              sb.append("数据库表：TS_BANKANDPAY 字段：S_NATIONTAXBANK 不能为空; ");
                
       if (this.getSnationtaxbank()!=null)
          if (this.getSnationtaxbank().getBytes().length > 12)
             sb.append("数据库表：TS_BANKANDPAY 字段：S_NATIONTAXBANK 宽度不能超过 "+12);
                
        if (this.getSareataxbank()==null)
              sb.append("数据库表：TS_BANKANDPAY 字段：S_AREATAXBANK 不能为空; ");
                
       if (this.getSareataxbank()!=null)
          if (this.getSareataxbank().getBytes().length > 32)
             sb.append("数据库表：TS_BANKANDPAY 字段：S_AREATAXBANK 宽度不能超过 "+32);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}

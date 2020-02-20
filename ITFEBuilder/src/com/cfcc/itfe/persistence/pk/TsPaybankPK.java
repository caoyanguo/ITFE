package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: TS_PAYBANK</p>
 * <p>Description:  PK Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:00 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *
 *  pk.vm version timestamp: 2007-04-06 08:30:00
 * 
 * @author win7
 */

public class TsPaybankPK implements IPK
{
    /**
    *   S_ORGCODE VARCHAR  , PK   , NOT NULL        */
    protected String sorgcode;
    /**
    *   S_BANKNO VARCHAR  , PK   , NOT NULL        */
    protected String sbankno;



    /*  Getter S_ORGCODE, PK , NOT NULL   */
    public String getSorgcode()
    {
        return sorgcode;
    }

     /*   Setter S_ORGCODE, PK , NOT NULL   */
    public void setSorgcode(String _sorgcode) {
        this.sorgcode = _sorgcode;
    }
    /*  Getter S_BANKNO, PK , NOT NULL   */
    public String getSbankno()
    {
        return sbankno;
    }

     /*   Setter S_BANKNO, PK , NOT NULL   */
    public void setSbankno(String _sbankno) {
        this.sbankno = _sbankno;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TsPaybankPK))
            return false;

        TsPaybankPK bean = (TsPaybankPK) obj;

	      //compare field sorgcode
      if((this.sorgcode==null && bean.sorgcode!=null) || (this.sorgcode!=null && bean.sorgcode==null))
          return false;
        else if(this.sorgcode==null && bean.sorgcode==null){
        }
        else{
          if(!bean.sorgcode.equals(this.sorgcode))
               return false;
     }
  	      //compare field sbankno
      if((this.sbankno==null && bean.sbankno!=null) || (this.sbankno!=null && bean.sbankno==null))
          return false;
        else if(this.sbankno==null && bean.sbankno==null){
        }
        else{
          if(!bean.sbankno.equals(this.sbankno))
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

        if(this.sbankno!=null)
          __hash__ = __hash__ * 31+ this.sbankno.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsPaybankPK bean = new TsPaybankPK();
          if(this.sorgcode!=null){
                  bean.sorgcode = new String(this.sorgcode);
            }
             if(this.sbankno!=null){
                  bean.sbankno = new String(this.sbankno);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsPaybankPK").append(":");
            sb.append("[sorgcode]").append(" = ").append(sorgcode).append(sep);
            sb.append("[sbankno]").append(" = ").append(sbankno).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSorgcode()==null)
              sb.append("数据库表：TS_PAYBANK 字段：S_ORGCODE 不能为空; ");
                
       if (this.getSorgcode()!=null)
          if (this.getSorgcode().getBytes().length > 12)
             sb.append("数据库表：TS_PAYBANK 字段：S_ORGCODE 宽度不能超过 "+12);
                
        if (this.getSbankno()==null)
              sb.append("数据库表：TS_PAYBANK 字段：S_BANKNO 不能为空; ");
                
       if (this.getSbankno()!=null)
          if (this.getSbankno().getBytes().length > 30)
             sb.append("数据库表：TS_PAYBANK 字段：S_BANKNO 宽度不能超过 "+30);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}

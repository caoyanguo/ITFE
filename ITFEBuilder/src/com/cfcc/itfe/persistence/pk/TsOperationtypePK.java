package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: TS_OPERATIONTYPE</p>
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

public class TsOperationtypePK implements IPK
{
    /**
    *   S_OPERATIONTYPECODE VARCHAR  , PK   , NOT NULL        */
    protected String soperationtypecode;



    /*  Getter S_OPERATIONTYPECODE, PK , NOT NULL   */
    public String getSoperationtypecode()
    {
        return soperationtypecode;
    }

     /*   Setter S_OPERATIONTYPECODE, PK , NOT NULL   */
    public void setSoperationtypecode(String _soperationtypecode) {
        this.soperationtypecode = _soperationtypecode;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TsOperationtypePK))
            return false;

        TsOperationtypePK bean = (TsOperationtypePK) obj;

	      //compare field soperationtypecode
      if((this.soperationtypecode==null && bean.soperationtypecode!=null) || (this.soperationtypecode!=null && bean.soperationtypecode==null))
          return false;
        else if(this.soperationtypecode==null && bean.soperationtypecode==null){
        }
        else{
          if(!bean.soperationtypecode.equals(this.soperationtypecode))
               return false;
     }
          return true;
    }


   /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
		int __hash__ = 1;
		

        if(this.soperationtypecode!=null)
          __hash__ = __hash__ * 31+ this.soperationtypecode.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsOperationtypePK bean = new TsOperationtypePK();
          if(this.soperationtypecode!=null){
                  bean.soperationtypecode = new String(this.soperationtypecode);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsOperationtypePK").append(":");
            sb.append("[soperationtypecode]").append(" = ").append(soperationtypecode).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSoperationtypecode()==null)
              sb.append("数据库表：TS_OPERATIONTYPE 字段：S_OPERATIONTYPECODE 不能为空; ");
                
       if (this.getSoperationtypecode()!=null)
          if (this.getSoperationtypecode().getBytes().length > 10)
             sb.append("数据库表：TS_OPERATIONTYPE 字段：S_OPERATIONTYPECODE 宽度不能超过 "+10);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}

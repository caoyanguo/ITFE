package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: TS_STAMPTYPE</p>
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

public class TsStamptypePK implements IPK
{
    /**
    *   S_STAMPTYPECODE VARCHAR  , PK   , NOT NULL        */
    protected String sstamptypecode;



    /*  Getter S_STAMPTYPECODE, PK , NOT NULL   */
    public String getSstamptypecode()
    {
        return sstamptypecode;
    }

     /*   Setter S_STAMPTYPECODE, PK , NOT NULL   */
    public void setSstamptypecode(String _sstamptypecode) {
        this.sstamptypecode = _sstamptypecode;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TsStamptypePK))
            return false;

        TsStamptypePK bean = (TsStamptypePK) obj;

	      //compare field sstamptypecode
      if((this.sstamptypecode==null && bean.sstamptypecode!=null) || (this.sstamptypecode!=null && bean.sstamptypecode==null))
          return false;
        else if(this.sstamptypecode==null && bean.sstamptypecode==null){
        }
        else{
          if(!bean.sstamptypecode.equals(this.sstamptypecode))
               return false;
     }
          return true;
    }


   /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
		int __hash__ = 1;
		

        if(this.sstamptypecode!=null)
          __hash__ = __hash__ * 31+ this.sstamptypecode.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsStamptypePK bean = new TsStamptypePK();
          if(this.sstamptypecode!=null){
                  bean.sstamptypecode = new String(this.sstamptypecode);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsStamptypePK").append(":");
            sb.append("[sstamptypecode]").append(" = ").append(sstamptypecode).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSstamptypecode()==null)
              sb.append("数据库表：TS_STAMPTYPE 字段：S_STAMPTYPECODE 不能为空; ");
                
       if (this.getSstamptypecode()!=null)
          if (this.getSstamptypecode().getBytes().length > 10)
             sb.append("数据库表：TS_STAMPTYPE 字段：S_STAMPTYPECODE 宽度不能超过 "+10);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}

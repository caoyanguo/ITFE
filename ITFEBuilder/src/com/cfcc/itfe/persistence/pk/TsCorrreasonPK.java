package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: TS_CORRREASON</p>
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

public class TsCorrreasonPK implements IPK
{
    /**
    *   S_BOOKORGCODE CHARACTER  , PK   , NOT NULL        */
    protected String sbookorgcode;
    /**
    *   S_TBSCORRCODE VARCHAR  , PK   , NOT NULL        */
    protected String stbscorrcode;



    /*  Getter S_BOOKORGCODE, PK , NOT NULL   */
    public String getSbookorgcode()
    {
        return sbookorgcode;
    }

     /*   Setter S_BOOKORGCODE, PK , NOT NULL   */
    public void setSbookorgcode(String _sbookorgcode) {
        this.sbookorgcode = _sbookorgcode;
    }
    /*  Getter S_TBSCORRCODE, PK , NOT NULL   */
    public String getStbscorrcode()
    {
        return stbscorrcode;
    }

     /*   Setter S_TBSCORRCODE, PK , NOT NULL   */
    public void setStbscorrcode(String _stbscorrcode) {
        this.stbscorrcode = _stbscorrcode;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TsCorrreasonPK))
            return false;

        TsCorrreasonPK bean = (TsCorrreasonPK) obj;

	      //compare field sbookorgcode
      if((this.sbookorgcode==null && bean.sbookorgcode!=null) || (this.sbookorgcode!=null && bean.sbookorgcode==null))
          return false;
        else if(this.sbookorgcode==null && bean.sbookorgcode==null){
        }
        else{
          if(!bean.sbookorgcode.equals(this.sbookorgcode))
               return false;
     }
  	      //compare field stbscorrcode
      if((this.stbscorrcode==null && bean.stbscorrcode!=null) || (this.stbscorrcode!=null && bean.stbscorrcode==null))
          return false;
        else if(this.stbscorrcode==null && bean.stbscorrcode==null){
        }
        else{
          if(!bean.stbscorrcode.equals(this.stbscorrcode))
               return false;
     }
          return true;
    }


   /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
		int __hash__ = 1;
		

        if(this.sbookorgcode!=null)
          __hash__ = __hash__ * 31+ this.sbookorgcode.hashCode() ;

        if(this.stbscorrcode!=null)
          __hash__ = __hash__ * 31+ this.stbscorrcode.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsCorrreasonPK bean = new TsCorrreasonPK();
          if(this.sbookorgcode!=null){
                  bean.sbookorgcode = new String(this.sbookorgcode);
            }
             if(this.stbscorrcode!=null){
                  bean.stbscorrcode = new String(this.stbscorrcode);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsCorrreasonPK").append(":");
            sb.append("[sbookorgcode]").append(" = ").append(sbookorgcode).append(sep);
            sb.append("[stbscorrcode]").append(" = ").append(stbscorrcode).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSbookorgcode()==null)
              sb.append("数据库表：TS_CORRREASON 字段：S_BOOKORGCODE 不能为空; ");
                
       if (this.getSbookorgcode()!=null)
          if (this.getSbookorgcode().getBytes().length > 12)
             sb.append("数据库表：TS_CORRREASON 字段：S_BOOKORGCODE 宽度不能超过 "+12);
                
        if (this.getStbscorrcode()==null)
              sb.append("数据库表：TS_CORRREASON 字段：S_TBSCORRCODE 不能为空; ");
                
       if (this.getStbscorrcode()!=null)
          if (this.getStbscorrcode().getBytes().length > 12)
             sb.append("数据库表：TS_CORRREASON 字段：S_TBSCORRCODE 宽度不能超过 "+12);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}

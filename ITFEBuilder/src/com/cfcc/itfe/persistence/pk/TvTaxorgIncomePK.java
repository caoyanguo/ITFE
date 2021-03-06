package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: TV_TAXORG_INCOME</p>
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

public class TvTaxorgIncomePK implements IPK
{
    /**
    *   S_TAXORGCODE CHARACTER  , PK   , NOT NULL        */
    protected String staxorgcode;
    /**
    *   S_EXPVOUNO VARCHAR  , PK   , NOT NULL        */
    protected String sexpvouno;



    /*  Getter S_TAXORGCODE, PK , NOT NULL   */
    public String getStaxorgcode()
    {
        return staxorgcode;
    }

     /*   Setter S_TAXORGCODE, PK , NOT NULL   */
    public void setStaxorgcode(String _staxorgcode) {
        this.staxorgcode = _staxorgcode;
    }
    /*  Getter S_EXPVOUNO, PK , NOT NULL   */
    public String getSexpvouno()
    {
        return sexpvouno;
    }

     /*   Setter S_EXPVOUNO, PK , NOT NULL   */
    public void setSexpvouno(String _sexpvouno) {
        this.sexpvouno = _sexpvouno;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TvTaxorgIncomePK))
            return false;

        TvTaxorgIncomePK bean = (TvTaxorgIncomePK) obj;

	      //compare field staxorgcode
      if((this.staxorgcode==null && bean.staxorgcode!=null) || (this.staxorgcode!=null && bean.staxorgcode==null))
          return false;
        else if(this.staxorgcode==null && bean.staxorgcode==null){
        }
        else{
          if(!bean.staxorgcode.equals(this.staxorgcode))
               return false;
     }
  	      //compare field sexpvouno
      if((this.sexpvouno==null && bean.sexpvouno!=null) || (this.sexpvouno!=null && bean.sexpvouno==null))
          return false;
        else if(this.sexpvouno==null && bean.sexpvouno==null){
        }
        else{
          if(!bean.sexpvouno.equals(this.sexpvouno))
               return false;
     }
          return true;
    }


   /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
		int __hash__ = 1;
		

        if(this.staxorgcode!=null)
          __hash__ = __hash__ * 31+ this.staxorgcode.hashCode() ;

        if(this.sexpvouno!=null)
          __hash__ = __hash__ * 31+ this.sexpvouno.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TvTaxorgIncomePK bean = new TvTaxorgIncomePK();
          if(this.staxorgcode!=null){
                  bean.staxorgcode = new String(this.staxorgcode);
            }
             if(this.sexpvouno!=null){
                  bean.sexpvouno = new String(this.sexpvouno);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("TvTaxorgIncomePK").append(":");
            sb.append("[staxorgcode]").append(" = ").append(staxorgcode).append(sep);
            sb.append("[sexpvouno]").append(" = ").append(sexpvouno).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getStaxorgcode()==null)
              sb.append("数据库表：TV_TAXORG_INCOME 字段：S_TAXORGCODE 不能为空; ");
                
       if (this.getStaxorgcode()!=null)
          if (this.getStaxorgcode().getBytes().length > 12)
             sb.append("数据库表：TV_TAXORG_INCOME 字段：S_TAXORGCODE 宽度不能超过 "+12);
                
        if (this.getSexpvouno()==null)
              sb.append("数据库表：TV_TAXORG_INCOME 字段：S_EXPVOUNO 不能为空; ");
                
       if (this.getSexpvouno()!=null)
          if (this.getSexpvouno().getBytes().length > 20)
             sb.append("数据库表：TV_TAXORG_INCOME 字段：S_EXPVOUNO 宽度不能超过 "+20);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}

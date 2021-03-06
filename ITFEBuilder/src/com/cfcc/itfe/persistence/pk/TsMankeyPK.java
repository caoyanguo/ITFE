package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: TS_MANKEY</p>
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

public class TsMankeyPK implements IPK
{
    /**
    *   S_ORGCODE VARCHAR  , PK   , NOT NULL        */
    protected String sorgcode;
    /**
    *   S_KEYMODE CHARACTER  , PK   , NOT NULL        */
    protected String skeymode;
    /**
    *   S_KEYORGCODE VARCHAR  , PK   , NOT NULL        */
    protected String skeyorgcode;



    /*  Getter S_ORGCODE, PK , NOT NULL   */
    public String getSorgcode()
    {
        return sorgcode;
    }

     /*   Setter S_ORGCODE, PK , NOT NULL   */
    public void setSorgcode(String _sorgcode) {
        this.sorgcode = _sorgcode;
    }
    /*  Getter S_KEYMODE, PK , NOT NULL   */
    public String getSkeymode()
    {
        return skeymode;
    }

     /*   Setter S_KEYMODE, PK , NOT NULL   */
    public void setSkeymode(String _skeymode) {
        this.skeymode = _skeymode;
    }
    /*  Getter S_KEYORGCODE, PK , NOT NULL   */
    public String getSkeyorgcode()
    {
        return skeyorgcode;
    }

     /*   Setter S_KEYORGCODE, PK , NOT NULL   */
    public void setSkeyorgcode(String _skeyorgcode) {
        this.skeyorgcode = _skeyorgcode;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TsMankeyPK))
            return false;

        TsMankeyPK bean = (TsMankeyPK) obj;

	      //compare field sorgcode
      if((this.sorgcode==null && bean.sorgcode!=null) || (this.sorgcode!=null && bean.sorgcode==null))
          return false;
        else if(this.sorgcode==null && bean.sorgcode==null){
        }
        else{
          if(!bean.sorgcode.equals(this.sorgcode))
               return false;
     }
  	      //compare field skeymode
      if((this.skeymode==null && bean.skeymode!=null) || (this.skeymode!=null && bean.skeymode==null))
          return false;
        else if(this.skeymode==null && bean.skeymode==null){
        }
        else{
          if(!bean.skeymode.equals(this.skeymode))
               return false;
     }
  	      //compare field skeyorgcode
      if((this.skeyorgcode==null && bean.skeyorgcode!=null) || (this.skeyorgcode!=null && bean.skeyorgcode==null))
          return false;
        else if(this.skeyorgcode==null && bean.skeyorgcode==null){
        }
        else{
          if(!bean.skeyorgcode.equals(this.skeyorgcode))
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

        if(this.skeymode!=null)
          __hash__ = __hash__ * 31+ this.skeymode.hashCode() ;

        if(this.skeyorgcode!=null)
          __hash__ = __hash__ * 31+ this.skeyorgcode.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsMankeyPK bean = new TsMankeyPK();
          if(this.sorgcode!=null){
                  bean.sorgcode = new String(this.sorgcode);
            }
             if(this.skeymode!=null){
                  bean.skeymode = new String(this.skeymode);
            }
             if(this.skeyorgcode!=null){
                  bean.skeyorgcode = new String(this.skeyorgcode);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsMankeyPK").append(":");
            sb.append("[sorgcode]").append(" = ").append(sorgcode).append(sep);
            sb.append("[skeymode]").append(" = ").append(skeymode).append(sep);
            sb.append("[skeyorgcode]").append(" = ").append(skeyorgcode).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSorgcode()==null)
              sb.append("数据库表：TS_MANKEY 字段：S_ORGCODE 不能为空; ");
                
       if (this.getSorgcode()!=null)
          if (this.getSorgcode().getBytes().length > 12)
             sb.append("数据库表：TS_MANKEY 字段：S_ORGCODE 宽度不能超过 "+12);
                
        if (this.getSkeymode()==null)
              sb.append("数据库表：TS_MANKEY 字段：S_KEYMODE 不能为空; ");
                
       if (this.getSkeymode()!=null)
          if (this.getSkeymode().getBytes().length > 1)
             sb.append("数据库表：TS_MANKEY 字段：S_KEYMODE 宽度不能超过 "+1);
                
        if (this.getSkeyorgcode()==null)
              sb.append("数据库表：TS_MANKEY 字段：S_KEYORGCODE 不能为空; ");
                
       if (this.getSkeyorgcode()!=null)
          if (this.getSkeyorgcode().getBytes().length > 20)
             sb.append("数据库表：TS_MANKEY 字段：S_KEYORGCODE 宽度不能超过 "+20);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}

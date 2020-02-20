package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: TV_DIRECTPAYFILEMAIN</p>
 * <p>Description:  PK Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:01 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *
 *  pk.vm version timestamp: 2007-04-06 08:30:00
 * 
 * @author win7
 */

public class TvDirectpayfilemainPK implements IPK
{
    /**
    *   S_ORGCODE VARCHAR  , PK   , NOT NULL        */
    protected String sorgcode;
    /**
    *   S_COMMITDATE CHARACTER  , PK   , NOT NULL        */
    protected String scommitdate;
    /**
    *   S_FILENAME VARCHAR  , PK   , NOT NULL        */
    protected String sfilename;



    /*  Getter S_ORGCODE, PK , NOT NULL   */
    public String getSorgcode()
    {
        return sorgcode;
    }

     /*   Setter S_ORGCODE, PK , NOT NULL   */
    public void setSorgcode(String _sorgcode) {
        this.sorgcode = _sorgcode;
    }
    /*  Getter S_COMMITDATE, PK , NOT NULL   */
    public String getScommitdate()
    {
        return scommitdate;
    }

     /*   Setter S_COMMITDATE, PK , NOT NULL   */
    public void setScommitdate(String _scommitdate) {
        this.scommitdate = _scommitdate;
    }
    /*  Getter S_FILENAME, PK , NOT NULL   */
    public String getSfilename()
    {
        return sfilename;
    }

     /*   Setter S_FILENAME, PK , NOT NULL   */
    public void setSfilename(String _sfilename) {
        this.sfilename = _sfilename;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TvDirectpayfilemainPK))
            return false;

        TvDirectpayfilemainPK bean = (TvDirectpayfilemainPK) obj;

	      //compare field sorgcode
      if((this.sorgcode==null && bean.sorgcode!=null) || (this.sorgcode!=null && bean.sorgcode==null))
          return false;
        else if(this.sorgcode==null && bean.sorgcode==null){
        }
        else{
          if(!bean.sorgcode.equals(this.sorgcode))
               return false;
     }
  	      //compare field scommitdate
      if((this.scommitdate==null && bean.scommitdate!=null) || (this.scommitdate!=null && bean.scommitdate==null))
          return false;
        else if(this.scommitdate==null && bean.scommitdate==null){
        }
        else{
          if(!bean.scommitdate.equals(this.scommitdate))
               return false;
     }
  	      //compare field sfilename
      if((this.sfilename==null && bean.sfilename!=null) || (this.sfilename!=null && bean.sfilename==null))
          return false;
        else if(this.sfilename==null && bean.sfilename==null){
        }
        else{
          if(!bean.sfilename.equals(this.sfilename))
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

        if(this.scommitdate!=null)
          __hash__ = __hash__ * 31+ this.scommitdate.hashCode() ;

        if(this.sfilename!=null)
          __hash__ = __hash__ * 31+ this.sfilename.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TvDirectpayfilemainPK bean = new TvDirectpayfilemainPK();
          if(this.sorgcode!=null){
                  bean.sorgcode = new String(this.sorgcode);
            }
             if(this.scommitdate!=null){
                  bean.scommitdate = new String(this.scommitdate);
            }
             if(this.sfilename!=null){
                  bean.sfilename = new String(this.sfilename);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("TvDirectpayfilemainPK").append(":");
            sb.append("[sorgcode]").append(" = ").append(sorgcode).append(sep);
            sb.append("[scommitdate]").append(" = ").append(scommitdate).append(sep);
            sb.append("[sfilename]").append(" = ").append(sfilename).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSorgcode()==null)
              sb.append("���ݿ����TV_DIRECTPAYFILEMAIN �ֶΣ�S_ORGCODE ����Ϊ��; ");
                
       if (this.getSorgcode()!=null)
          if (this.getSorgcode().getBytes().length > 12)
             sb.append("���ݿ����TV_DIRECTPAYFILEMAIN �ֶΣ�S_ORGCODE ���Ȳ��ܳ��� "+12);
                
        if (this.getScommitdate()==null)
              sb.append("���ݿ����TV_DIRECTPAYFILEMAIN �ֶΣ�S_COMMITDATE ����Ϊ��; ");
                
       if (this.getScommitdate()!=null)
          if (this.getScommitdate().getBytes().length > 8)
             sb.append("���ݿ����TV_DIRECTPAYFILEMAIN �ֶΣ�S_COMMITDATE ���Ȳ��ܳ��� "+8);
                
        if (this.getSfilename()==null)
              sb.append("���ݿ����TV_DIRECTPAYFILEMAIN �ֶΣ�S_FILENAME ����Ϊ��; ");
                
       if (this.getSfilename()!=null)
          if (this.getSfilename().getBytes().length > 100)
             sb.append("���ݿ����TV_DIRECTPAYFILEMAIN �ֶΣ�S_FILENAME ���Ȳ��ܳ��� "+100);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}
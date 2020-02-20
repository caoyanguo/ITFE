package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: TV_DRAWBACKDATA</p>
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

public class TvDrawbackdataPK implements IPK
{
    /**
    *   S_SENDORGCODE VARCHAR  , PK   , NOT NULL        */
    protected String ssendorgcode;
    /**
    *   S_COMMITDATE CHARACTER  , PK   , NOT NULL        */
    protected String scommitdate;
    /**
    *   S_DEALNO VARCHAR  , PK   , NOT NULL        */
    protected String sdealno;



    /*  Getter S_SENDORGCODE, PK , NOT NULL   */
    public String getSsendorgcode()
    {
        return ssendorgcode;
    }

     /*   Setter S_SENDORGCODE, PK , NOT NULL   */
    public void setSsendorgcode(String _ssendorgcode) {
        this.ssendorgcode = _ssendorgcode;
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
    /*  Getter S_DEALNO, PK , NOT NULL   */
    public String getSdealno()
    {
        return sdealno;
    }

     /*   Setter S_DEALNO, PK , NOT NULL   */
    public void setSdealno(String _sdealno) {
        this.sdealno = _sdealno;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TvDrawbackdataPK))
            return false;

        TvDrawbackdataPK bean = (TvDrawbackdataPK) obj;

	      //compare field ssendorgcode
      if((this.ssendorgcode==null && bean.ssendorgcode!=null) || (this.ssendorgcode!=null && bean.ssendorgcode==null))
          return false;
        else if(this.ssendorgcode==null && bean.ssendorgcode==null){
        }
        else{
          if(!bean.ssendorgcode.equals(this.ssendorgcode))
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
  	      //compare field sdealno
      if((this.sdealno==null && bean.sdealno!=null) || (this.sdealno!=null && bean.sdealno==null))
          return false;
        else if(this.sdealno==null && bean.sdealno==null){
        }
        else{
          if(!bean.sdealno.equals(this.sdealno))
               return false;
     }
          return true;
    }


   /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
		int __hash__ = 1;
		

        if(this.ssendorgcode!=null)
          __hash__ = __hash__ * 31+ this.ssendorgcode.hashCode() ;

        if(this.scommitdate!=null)
          __hash__ = __hash__ * 31+ this.scommitdate.hashCode() ;

        if(this.sdealno!=null)
          __hash__ = __hash__ * 31+ this.sdealno.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TvDrawbackdataPK bean = new TvDrawbackdataPK();
          if(this.ssendorgcode!=null){
                  bean.ssendorgcode = new String(this.ssendorgcode);
            }
             if(this.scommitdate!=null){
                  bean.scommitdate = new String(this.scommitdate);
            }
             if(this.sdealno!=null){
                  bean.sdealno = new String(this.sdealno);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("TvDrawbackdataPK").append(":");
            sb.append("[ssendorgcode]").append(" = ").append(ssendorgcode).append(sep);
            sb.append("[scommitdate]").append(" = ").append(scommitdate).append(sep);
            sb.append("[sdealno]").append(" = ").append(sdealno).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSsendorgcode()==null)
              sb.append("���ݿ����TV_DRAWBACKDATA �ֶΣ�S_SENDORGCODE ����Ϊ��; ");
                
       if (this.getSsendorgcode()!=null)
          if (this.getSsendorgcode().getBytes().length > 12)
             sb.append("���ݿ����TV_DRAWBACKDATA �ֶΣ�S_SENDORGCODE ���Ȳ��ܳ��� "+12);
                
        if (this.getScommitdate()==null)
              sb.append("���ݿ����TV_DRAWBACKDATA �ֶΣ�S_COMMITDATE ����Ϊ��; ");
                
       if (this.getScommitdate()!=null)
          if (this.getScommitdate().getBytes().length > 8)
             sb.append("���ݿ����TV_DRAWBACKDATA �ֶΣ�S_COMMITDATE ���Ȳ��ܳ��� "+8);
                
        if (this.getSdealno()==null)
              sb.append("���ݿ����TV_DRAWBACKDATA �ֶΣ�S_DEALNO ����Ϊ��; ");
                
       if (this.getSdealno()!=null)
          if (this.getSdealno().getBytes().length > 30)
             sb.append("���ݿ����TV_DRAWBACKDATA �ֶΣ�S_DEALNO ���Ȳ��ܳ��� "+30);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}
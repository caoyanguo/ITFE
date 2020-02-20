package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: TS_CODEREGTYPE</p>
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

public class TsCoderegtypePK implements IPK
{
    /**
    *   S_ORGCODE VARCHAR  , PK   , NOT NULL        */
    protected String sorgcode;
    /**
    *   S_REGCODE CHARACTER  , PK   , NOT NULL        */
    protected String sregcode;



    /*  Getter S_ORGCODE, PK , NOT NULL   */
    public String getSorgcode()
    {
        return sorgcode;
    }

     /*   Setter S_ORGCODE, PK , NOT NULL   */
    public void setSorgcode(String _sorgcode) {
        this.sorgcode = _sorgcode;
    }
    /*  Getter S_REGCODE, PK , NOT NULL   */
    public String getSregcode()
    {
        return sregcode;
    }

     /*   Setter S_REGCODE, PK , NOT NULL   */
    public void setSregcode(String _sregcode) {
        this.sregcode = _sregcode;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TsCoderegtypePK))
            return false;

        TsCoderegtypePK bean = (TsCoderegtypePK) obj;

	      //compare field sorgcode
      if((this.sorgcode==null && bean.sorgcode!=null) || (this.sorgcode!=null && bean.sorgcode==null))
          return false;
        else if(this.sorgcode==null && bean.sorgcode==null){
        }
        else{
          if(!bean.sorgcode.equals(this.sorgcode))
               return false;
     }
  	      //compare field sregcode
      if((this.sregcode==null && bean.sregcode!=null) || (this.sregcode!=null && bean.sregcode==null))
          return false;
        else if(this.sregcode==null && bean.sregcode==null){
        }
        else{
          if(!bean.sregcode.equals(this.sregcode))
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

        if(this.sregcode!=null)
          __hash__ = __hash__ * 31+ this.sregcode.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsCoderegtypePK bean = new TsCoderegtypePK();
          if(this.sorgcode!=null){
                  bean.sorgcode = new String(this.sorgcode);
            }
             if(this.sregcode!=null){
                  bean.sregcode = new String(this.sregcode);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsCoderegtypePK").append(":");
            sb.append("[sorgcode]").append(" = ").append(sorgcode).append(sep);
            sb.append("[sregcode]").append(" = ").append(sregcode).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSorgcode()==null)
              sb.append("���ݿ����TS_CODEREGTYPE �ֶΣ�S_ORGCODE ����Ϊ��; ");
                
       if (this.getSorgcode()!=null)
          if (this.getSorgcode().getBytes().length > 12)
             sb.append("���ݿ����TS_CODEREGTYPE �ֶΣ�S_ORGCODE ���Ȳ��ܳ��� "+12);
                
        if (this.getSregcode()==null)
              sb.append("���ݿ����TS_CODEREGTYPE �ֶΣ�S_REGCODE ����Ϊ��; ");
                
       if (this.getSregcode()!=null)
          if (this.getSregcode().getBytes().length > 3)
             sb.append("���ݿ����TS_CODEREGTYPE �ֶΣ�S_REGCODE ���Ȳ��ܳ��� "+3);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}
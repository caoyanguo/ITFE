package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: TS_BANKCODE</p>
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

public class TsBankcodePK implements IPK
{
    /**
    *   S_BNKCODE VARCHAR  , PK   , NOT NULL        */
    protected String sbnkcode;
    /**
    *   S_ACCTSTATUS CHARACTER  , PK   , NOT NULL        */
    protected String sacctstatus;



    /*  Getter S_BNKCODE, PK , NOT NULL   */
    public String getSbnkcode()
    {
        return sbnkcode;
    }

     /*   Setter S_BNKCODE, PK , NOT NULL   */
    public void setSbnkcode(String _sbnkcode) {
        this.sbnkcode = _sbnkcode;
    }
    /*  Getter S_ACCTSTATUS, PK , NOT NULL   */
    public String getSacctstatus()
    {
        return sacctstatus;
    }

     /*   Setter S_ACCTSTATUS, PK , NOT NULL   */
    public void setSacctstatus(String _sacctstatus) {
        this.sacctstatus = _sacctstatus;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TsBankcodePK))
            return false;

        TsBankcodePK bean = (TsBankcodePK) obj;

	      //compare field sbnkcode
      if((this.sbnkcode==null && bean.sbnkcode!=null) || (this.sbnkcode!=null && bean.sbnkcode==null))
          return false;
        else if(this.sbnkcode==null && bean.sbnkcode==null){
        }
        else{
          if(!bean.sbnkcode.equals(this.sbnkcode))
               return false;
     }
  	      //compare field sacctstatus
      if((this.sacctstatus==null && bean.sacctstatus!=null) || (this.sacctstatus!=null && bean.sacctstatus==null))
          return false;
        else if(this.sacctstatus==null && bean.sacctstatus==null){
        }
        else{
          if(!bean.sacctstatus.equals(this.sacctstatus))
               return false;
     }
          return true;
    }


   /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
		int __hash__ = 1;
		

        if(this.sbnkcode!=null)
          __hash__ = __hash__ * 31+ this.sbnkcode.hashCode() ;

        if(this.sacctstatus!=null)
          __hash__ = __hash__ * 31+ this.sacctstatus.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsBankcodePK bean = new TsBankcodePK();
          if(this.sbnkcode!=null){
                  bean.sbnkcode = new String(this.sbnkcode);
            }
             if(this.sacctstatus!=null){
                  bean.sacctstatus = new String(this.sacctstatus);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsBankcodePK").append(":");
            sb.append("[sbnkcode]").append(" = ").append(sbnkcode).append(sep);
            sb.append("[sacctstatus]").append(" = ").append(sacctstatus).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSbnkcode()==null)
              sb.append("���ݿ����TS_BANKCODE �ֶΣ�S_BNKCODE ����Ϊ��; ");
                
       if (this.getSbnkcode()!=null)
          if (this.getSbnkcode().getBytes().length > 12)
             sb.append("���ݿ����TS_BANKCODE �ֶΣ�S_BNKCODE ���Ȳ��ܳ��� "+12);
                
        if (this.getSacctstatus()==null)
              sb.append("���ݿ����TS_BANKCODE �ֶΣ�S_ACCTSTATUS ����Ϊ��; ");
                
       if (this.getSacctstatus()!=null)
          if (this.getSacctstatus().getBytes().length > 1)
             sb.append("���ݿ����TS_BANKCODE �ֶΣ�S_ACCTSTATUS ���Ȳ��ܳ��� "+1);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}
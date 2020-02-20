package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: TS_DWBK_REASON</p>
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

public class TsDwbkReasonPK implements IPK
{
    /**
    *   S_ORGCODE CHARACTER  , PK   , NOT NULL        */
    protected String sorgcode;
    /**
    *   S_DRAWBACKREACODE CHARACTER  , PK   , NOT NULL        */
    protected String sdrawbackreacode;



    /*  Getter S_ORGCODE, PK , NOT NULL   */
    public String getSorgcode()
    {
        return sorgcode;
    }

     /*   Setter S_ORGCODE, PK , NOT NULL   */
    public void setSorgcode(String _sorgcode) {
        this.sorgcode = _sorgcode;
    }
    /*  Getter S_DRAWBACKREACODE, PK , NOT NULL   */
    public String getSdrawbackreacode()
    {
        return sdrawbackreacode;
    }

     /*   Setter S_DRAWBACKREACODE, PK , NOT NULL   */
    public void setSdrawbackreacode(String _sdrawbackreacode) {
        this.sdrawbackreacode = _sdrawbackreacode;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TsDwbkReasonPK))
            return false;

        TsDwbkReasonPK bean = (TsDwbkReasonPK) obj;

	      //compare field sorgcode
      if((this.sorgcode==null && bean.sorgcode!=null) || (this.sorgcode!=null && bean.sorgcode==null))
          return false;
        else if(this.sorgcode==null && bean.sorgcode==null){
        }
        else{
          if(!bean.sorgcode.equals(this.sorgcode))
               return false;
     }
  	      //compare field sdrawbackreacode
      if((this.sdrawbackreacode==null && bean.sdrawbackreacode!=null) || (this.sdrawbackreacode!=null && bean.sdrawbackreacode==null))
          return false;
        else if(this.sdrawbackreacode==null && bean.sdrawbackreacode==null){
        }
        else{
          if(!bean.sdrawbackreacode.equals(this.sdrawbackreacode))
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

        if(this.sdrawbackreacode!=null)
          __hash__ = __hash__ * 31+ this.sdrawbackreacode.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsDwbkReasonPK bean = new TsDwbkReasonPK();
          if(this.sorgcode!=null){
                  bean.sorgcode = new String(this.sorgcode);
            }
             if(this.sdrawbackreacode!=null){
                  bean.sdrawbackreacode = new String(this.sdrawbackreacode);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsDwbkReasonPK").append(":");
            sb.append("[sorgcode]").append(" = ").append(sorgcode).append(sep);
            sb.append("[sdrawbackreacode]").append(" = ").append(sdrawbackreacode).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSorgcode()==null)
              sb.append("���ݿ����TS_DWBK_REASON �ֶΣ�S_ORGCODE ����Ϊ��; ");
                
       if (this.getSorgcode()!=null)
          if (this.getSorgcode().getBytes().length > 12)
             sb.append("���ݿ����TS_DWBK_REASON �ֶΣ�S_ORGCODE ���Ȳ��ܳ��� "+12);
                
        if (this.getSdrawbackreacode()==null)
              sb.append("���ݿ����TS_DWBK_REASON �ֶΣ�S_DRAWBACKREACODE ����Ϊ��; ");
                
       if (this.getSdrawbackreacode()!=null)
          if (this.getSdrawbackreacode().getBytes().length > 12)
             sb.append("���ݿ����TS_DWBK_REASON �ֶΣ�S_DRAWBACKREACODE ���Ȳ��ܳ��� "+12);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}
package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: TS_VOUCHERCOMMITAUTO</p>
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

public class TsVouchercommitautoPK implements IPK
{
    /**
    *   S_ORGCODE VARCHAR  , PK   , NOT NULL        */
    protected String sorgcode;
    /**
    *   S_TRECODE CHARACTER  , PK   , NOT NULL        */
    protected String strecode;
    /**
    *   S_VTCODE CHARACTER  , PK   , NOT NULL        */
    protected String svtcode;



    /*  Getter S_ORGCODE, PK , NOT NULL   */
    public String getSorgcode()
    {
        return sorgcode;
    }

     /*   Setter S_ORGCODE, PK , NOT NULL   */
    public void setSorgcode(String _sorgcode) {
        this.sorgcode = _sorgcode;
    }
    /*  Getter S_TRECODE, PK , NOT NULL   */
    public String getStrecode()
    {
        return strecode;
    }

     /*   Setter S_TRECODE, PK , NOT NULL   */
    public void setStrecode(String _strecode) {
        this.strecode = _strecode;
    }
    /*  Getter S_VTCODE, PK , NOT NULL   */
    public String getSvtcode()
    {
        return svtcode;
    }

     /*   Setter S_VTCODE, PK , NOT NULL   */
    public void setSvtcode(String _svtcode) {
        this.svtcode = _svtcode;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TsVouchercommitautoPK))
            return false;

        TsVouchercommitautoPK bean = (TsVouchercommitautoPK) obj;

	      //compare field sorgcode
      if((this.sorgcode==null && bean.sorgcode!=null) || (this.sorgcode!=null && bean.sorgcode==null))
          return false;
        else if(this.sorgcode==null && bean.sorgcode==null){
        }
        else{
          if(!bean.sorgcode.equals(this.sorgcode))
               return false;
     }
  	      //compare field strecode
      if((this.strecode==null && bean.strecode!=null) || (this.strecode!=null && bean.strecode==null))
          return false;
        else if(this.strecode==null && bean.strecode==null){
        }
        else{
          if(!bean.strecode.equals(this.strecode))
               return false;
     }
  	      //compare field svtcode
      if((this.svtcode==null && bean.svtcode!=null) || (this.svtcode!=null && bean.svtcode==null))
          return false;
        else if(this.svtcode==null && bean.svtcode==null){
        }
        else{
          if(!bean.svtcode.equals(this.svtcode))
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

        if(this.strecode!=null)
          __hash__ = __hash__ * 31+ this.strecode.hashCode() ;

        if(this.svtcode!=null)
          __hash__ = __hash__ * 31+ this.svtcode.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsVouchercommitautoPK bean = new TsVouchercommitautoPK();
          if(this.sorgcode!=null){
                  bean.sorgcode = new String(this.sorgcode);
            }
             if(this.strecode!=null){
                  bean.strecode = new String(this.strecode);
            }
             if(this.svtcode!=null){
                  bean.svtcode = new String(this.svtcode);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsVouchercommitautoPK").append(":");
            sb.append("[sorgcode]").append(" = ").append(sorgcode).append(sep);
            sb.append("[strecode]").append(" = ").append(strecode).append(sep);
            sb.append("[svtcode]").append(" = ").append(svtcode).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSorgcode()==null)
              sb.append("���ݿ����TS_VOUCHERCOMMITAUTO �ֶΣ�S_ORGCODE ����Ϊ��; ");
                
       if (this.getSorgcode()!=null)
          if (this.getSorgcode().getBytes().length > 12)
             sb.append("���ݿ����TS_VOUCHERCOMMITAUTO �ֶΣ�S_ORGCODE ���Ȳ��ܳ��� "+12);
                
        if (this.getStrecode()==null)
              sb.append("���ݿ����TS_VOUCHERCOMMITAUTO �ֶΣ�S_TRECODE ����Ϊ��; ");
                
       if (this.getStrecode()!=null)
          if (this.getStrecode().getBytes().length > 10)
             sb.append("���ݿ����TS_VOUCHERCOMMITAUTO �ֶΣ�S_TRECODE ���Ȳ��ܳ��� "+10);
                
        if (this.getSvtcode()==null)
              sb.append("���ݿ����TS_VOUCHERCOMMITAUTO �ֶΣ�S_VTCODE ����Ϊ��; ");
                
       if (this.getSvtcode()!=null)
          if (this.getSvtcode().getBytes().length > 4)
             sb.append("���ݿ����TS_VOUCHERCOMMITAUTO �ֶΣ�S_VTCODE ���Ȳ��ܳ��� "+4);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}
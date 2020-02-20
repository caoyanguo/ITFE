package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: HTV_PAYRECK_BANK</p>
 * <p>Description:  PK Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:56 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *
 *  pk.vm version timestamp: 2007-04-06 08:30:00
 * 
 * @author win7
 */

public class HtvPayreckBankPK implements IPK
{
    /**
    *   S_BOOKORGCODE CHARACTER  , PK   , NOT NULL        */
    protected String sbookorgcode;
    /**
    *   D_ENTRUSTDATE DATE  , PK   , NOT NULL        */
    protected Date dentrustdate;
    /**
    *   S_PACKNO CHARACTER  , PK   , NOT NULL        */
    protected String spackno;
    /**
    *   S_AGENTBNKCODE CHARACTER  , PK   , NOT NULL        */
    protected String sagentbnkcode;
    /**
    *   S_TRANO CHARACTER  , PK   , NOT NULL        */
    protected String strano;



    /*  Getter S_BOOKORGCODE, PK , NOT NULL   */
    public String getSbookorgcode()
    {
        return sbookorgcode;
    }

     /*   Setter S_BOOKORGCODE, PK , NOT NULL   */
    public void setSbookorgcode(String _sbookorgcode) {
        this.sbookorgcode = _sbookorgcode;
    }
    /*  Getter D_ENTRUSTDATE, PK , NOT NULL   */
    public Date getDentrustdate()
    {
        return dentrustdate;
    }

     /*   Setter D_ENTRUSTDATE, PK , NOT NULL   */
    public void setDentrustdate(Date _dentrustdate) {
        this.dentrustdate = _dentrustdate;
    }
    /*  Getter S_PACKNO, PK , NOT NULL   */
    public String getSpackno()
    {
        return spackno;
    }

     /*   Setter S_PACKNO, PK , NOT NULL   */
    public void setSpackno(String _spackno) {
        this.spackno = _spackno;
    }
    /*  Getter S_AGENTBNKCODE, PK , NOT NULL   */
    public String getSagentbnkcode()
    {
        return sagentbnkcode;
    }

     /*   Setter S_AGENTBNKCODE, PK , NOT NULL   */
    public void setSagentbnkcode(String _sagentbnkcode) {
        this.sagentbnkcode = _sagentbnkcode;
    }
    /*  Getter S_TRANO, PK , NOT NULL   */
    public String getStrano()
    {
        return strano;
    }

     /*   Setter S_TRANO, PK , NOT NULL   */
    public void setStrano(String _strano) {
        this.strano = _strano;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof HtvPayreckBankPK))
            return false;

        HtvPayreckBankPK bean = (HtvPayreckBankPK) obj;

	      //compare field sbookorgcode
      if((this.sbookorgcode==null && bean.sbookorgcode!=null) || (this.sbookorgcode!=null && bean.sbookorgcode==null))
          return false;
        else if(this.sbookorgcode==null && bean.sbookorgcode==null){
        }
        else{
          if(!bean.sbookorgcode.equals(this.sbookorgcode))
               return false;
     }
  	      //compare field dentrustdate
      if((this.dentrustdate==null && bean.dentrustdate!=null) || (this.dentrustdate!=null && bean.dentrustdate==null))
          return false;
        else if(this.dentrustdate==null && bean.dentrustdate==null){
        }
        else{
          if(!bean.dentrustdate.equals(this.dentrustdate))
               return false;
     }
  	      //compare field spackno
      if((this.spackno==null && bean.spackno!=null) || (this.spackno!=null && bean.spackno==null))
          return false;
        else if(this.spackno==null && bean.spackno==null){
        }
        else{
          if(!bean.spackno.equals(this.spackno))
               return false;
     }
  	      //compare field sagentbnkcode
      if((this.sagentbnkcode==null && bean.sagentbnkcode!=null) || (this.sagentbnkcode!=null && bean.sagentbnkcode==null))
          return false;
        else if(this.sagentbnkcode==null && bean.sagentbnkcode==null){
        }
        else{
          if(!bean.sagentbnkcode.equals(this.sagentbnkcode))
               return false;
     }
  	      //compare field strano
      if((this.strano==null && bean.strano!=null) || (this.strano!=null && bean.strano==null))
          return false;
        else if(this.strano==null && bean.strano==null){
        }
        else{
          if(!bean.strano.equals(this.strano))
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

        if(this.dentrustdate!=null)
          __hash__ = __hash__ * 31+ this.dentrustdate.hashCode() ;

        if(this.spackno!=null)
          __hash__ = __hash__ * 31+ this.spackno.hashCode() ;

        if(this.sagentbnkcode!=null)
          __hash__ = __hash__ * 31+ this.sagentbnkcode.hashCode() ;

        if(this.strano!=null)
          __hash__ = __hash__ * 31+ this.strano.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        HtvPayreckBankPK bean = new HtvPayreckBankPK();
          if(this.sbookorgcode!=null){
                  bean.sbookorgcode = new String(this.sbookorgcode);
            }
             if(this.dentrustdate!=null){
                  bean.dentrustdate = (Date) this.dentrustdate.clone();
            }
             if(this.spackno!=null){
                  bean.spackno = new String(this.spackno);
            }
             if(this.sagentbnkcode!=null){
                  bean.sagentbnkcode = new String(this.sagentbnkcode);
            }
             if(this.strano!=null){
                  bean.strano = new String(this.strano);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("HtvPayreckBankPK").append(":");
            sb.append("[sbookorgcode]").append(" = ").append(sbookorgcode).append(sep);
            sb.append("[dentrustdate]").append(" = ").append(dentrustdate).append(sep);
            sb.append("[spackno]").append(" = ").append(spackno).append(sep);
            sb.append("[sagentbnkcode]").append(" = ").append(sagentbnkcode).append(sep);
            sb.append("[strano]").append(" = ").append(strano).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSbookorgcode()==null)
              sb.append("数据库表：HTV_PAYRECK_BANK 字段：S_BOOKORGCODE 不能为空; ");
                
       if (this.getSbookorgcode()!=null)
          if (this.getSbookorgcode().getBytes().length > 12)
             sb.append("数据库表：HTV_PAYRECK_BANK 字段：S_BOOKORGCODE 宽度不能超过 "+12);
                
        if (this.getDentrustdate()==null)
              sb.append("数据库表：HTV_PAYRECK_BANK 字段：D_ENTRUSTDATE 不能为空; ");
                        
        if (this.getSpackno()==null)
              sb.append("数据库表：HTV_PAYRECK_BANK 字段：S_PACKNO 不能为空; ");
                
       if (this.getSpackno()!=null)
          if (this.getSpackno().getBytes().length > 8)
             sb.append("数据库表：HTV_PAYRECK_BANK 字段：S_PACKNO 宽度不能超过 "+8);
                
        if (this.getSagentbnkcode()==null)
              sb.append("数据库表：HTV_PAYRECK_BANK 字段：S_AGENTBNKCODE 不能为空; ");
                
       if (this.getSagentbnkcode()!=null)
          if (this.getSagentbnkcode().getBytes().length > 12)
             sb.append("数据库表：HTV_PAYRECK_BANK 字段：S_AGENTBNKCODE 宽度不能超过 "+12);
                
        if (this.getStrano()==null)
              sb.append("数据库表：HTV_PAYRECK_BANK 字段：S_TRANO 不能为空; ");
                
       if (this.getStrano()!=null)
          if (this.getStrano().getBytes().length > 8)
             sb.append("数据库表：HTV_PAYRECK_BANK 字段：S_TRANO 宽度不能超过 "+8);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}

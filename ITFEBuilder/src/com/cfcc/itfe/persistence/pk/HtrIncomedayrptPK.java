package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: HTR_INCOMEDAYRPT</p>
 * <p>Description:  PK Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:55 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *
 *  pk.vm version timestamp: 2007-04-06 08:30:00
 * 
 * @author win7
 */

public class HtrIncomedayrptPK implements IPK
{
    /**
    *   S_FINORGCODE VARCHAR  , PK   , NOT NULL        */
    protected String sfinorgcode;
    /**
    *   S_TAXORGCODE VARCHAR  , PK   , NOT NULL        */
    protected String staxorgcode;
    /**
    *   S_TRECODE CHARACTER  , PK   , NOT NULL        */
    protected String strecode;
    /**
    *   S_RPTDATE CHARACTER  , PK   , NOT NULL        */
    protected String srptdate;
    /**
    *   S_BUDGETTYPE CHARACTER  , PK   , NOT NULL        */
    protected String sbudgettype;
    /**
    *   S_BUDGETLEVELCODE CHARACTER  , PK   , NOT NULL        */
    protected String sbudgetlevelcode;
    /**
    *   S_BUDGETSUBCODE VARCHAR  , PK   , NOT NULL        */
    protected String sbudgetsubcode;
    /**
    *   S_TRIMFLAG CHARACTER  , PK   , NOT NULL        */
    protected String strimflag;
    /**
    *   S_DIVIDEGROUP VARCHAR  , PK   , NOT NULL        */
    protected String sdividegroup;
    /**
    *   S_BILLKIND CHARACTER  , PK   , NOT NULL        */
    protected String sbillkind;



    /*  Getter S_FINORGCODE, PK , NOT NULL   */
    public String getSfinorgcode()
    {
        return sfinorgcode;
    }

     /*   Setter S_FINORGCODE, PK , NOT NULL   */
    public void setSfinorgcode(String _sfinorgcode) {
        this.sfinorgcode = _sfinorgcode;
    }
    /*  Getter S_TAXORGCODE, PK , NOT NULL   */
    public String getStaxorgcode()
    {
        return staxorgcode;
    }

     /*   Setter S_TAXORGCODE, PK , NOT NULL   */
    public void setStaxorgcode(String _staxorgcode) {
        this.staxorgcode = _staxorgcode;
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
    /*  Getter S_RPTDATE, PK , NOT NULL   */
    public String getSrptdate()
    {
        return srptdate;
    }

     /*   Setter S_RPTDATE, PK , NOT NULL   */
    public void setSrptdate(String _srptdate) {
        this.srptdate = _srptdate;
    }
    /*  Getter S_BUDGETTYPE, PK , NOT NULL   */
    public String getSbudgettype()
    {
        return sbudgettype;
    }

     /*   Setter S_BUDGETTYPE, PK , NOT NULL   */
    public void setSbudgettype(String _sbudgettype) {
        this.sbudgettype = _sbudgettype;
    }
    /*  Getter S_BUDGETLEVELCODE, PK , NOT NULL   */
    public String getSbudgetlevelcode()
    {
        return sbudgetlevelcode;
    }

     /*   Setter S_BUDGETLEVELCODE, PK , NOT NULL   */
    public void setSbudgetlevelcode(String _sbudgetlevelcode) {
        this.sbudgetlevelcode = _sbudgetlevelcode;
    }
    /*  Getter S_BUDGETSUBCODE, PK , NOT NULL   */
    public String getSbudgetsubcode()
    {
        return sbudgetsubcode;
    }

     /*   Setter S_BUDGETSUBCODE, PK , NOT NULL   */
    public void setSbudgetsubcode(String _sbudgetsubcode) {
        this.sbudgetsubcode = _sbudgetsubcode;
    }
    /*  Getter S_TRIMFLAG, PK , NOT NULL   */
    public String getStrimflag()
    {
        return strimflag;
    }

     /*   Setter S_TRIMFLAG, PK , NOT NULL   */
    public void setStrimflag(String _strimflag) {
        this.strimflag = _strimflag;
    }
    /*  Getter S_DIVIDEGROUP, PK , NOT NULL   */
    public String getSdividegroup()
    {
        return sdividegroup;
    }

     /*   Setter S_DIVIDEGROUP, PK , NOT NULL   */
    public void setSdividegroup(String _sdividegroup) {
        this.sdividegroup = _sdividegroup;
    }
    /*  Getter S_BILLKIND, PK , NOT NULL   */
    public String getSbillkind()
    {
        return sbillkind;
    }

     /*   Setter S_BILLKIND, PK , NOT NULL   */
    public void setSbillkind(String _sbillkind) {
        this.sbillkind = _sbillkind;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof HtrIncomedayrptPK))
            return false;

        HtrIncomedayrptPK bean = (HtrIncomedayrptPK) obj;

	      //compare field sfinorgcode
      if((this.sfinorgcode==null && bean.sfinorgcode!=null) || (this.sfinorgcode!=null && bean.sfinorgcode==null))
          return false;
        else if(this.sfinorgcode==null && bean.sfinorgcode==null){
        }
        else{
          if(!bean.sfinorgcode.equals(this.sfinorgcode))
               return false;
     }
  	      //compare field staxorgcode
      if((this.staxorgcode==null && bean.staxorgcode!=null) || (this.staxorgcode!=null && bean.staxorgcode==null))
          return false;
        else if(this.staxorgcode==null && bean.staxorgcode==null){
        }
        else{
          if(!bean.staxorgcode.equals(this.staxorgcode))
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
  	      //compare field srptdate
      if((this.srptdate==null && bean.srptdate!=null) || (this.srptdate!=null && bean.srptdate==null))
          return false;
        else if(this.srptdate==null && bean.srptdate==null){
        }
        else{
          if(!bean.srptdate.equals(this.srptdate))
               return false;
     }
  	      //compare field sbudgettype
      if((this.sbudgettype==null && bean.sbudgettype!=null) || (this.sbudgettype!=null && bean.sbudgettype==null))
          return false;
        else if(this.sbudgettype==null && bean.sbudgettype==null){
        }
        else{
          if(!bean.sbudgettype.equals(this.sbudgettype))
               return false;
     }
  	      //compare field sbudgetlevelcode
      if((this.sbudgetlevelcode==null && bean.sbudgetlevelcode!=null) || (this.sbudgetlevelcode!=null && bean.sbudgetlevelcode==null))
          return false;
        else if(this.sbudgetlevelcode==null && bean.sbudgetlevelcode==null){
        }
        else{
          if(!bean.sbudgetlevelcode.equals(this.sbudgetlevelcode))
               return false;
     }
  	      //compare field sbudgetsubcode
      if((this.sbudgetsubcode==null && bean.sbudgetsubcode!=null) || (this.sbudgetsubcode!=null && bean.sbudgetsubcode==null))
          return false;
        else if(this.sbudgetsubcode==null && bean.sbudgetsubcode==null){
        }
        else{
          if(!bean.sbudgetsubcode.equals(this.sbudgetsubcode))
               return false;
     }
  	      //compare field strimflag
      if((this.strimflag==null && bean.strimflag!=null) || (this.strimflag!=null && bean.strimflag==null))
          return false;
        else if(this.strimflag==null && bean.strimflag==null){
        }
        else{
          if(!bean.strimflag.equals(this.strimflag))
               return false;
     }
  	      //compare field sdividegroup
      if((this.sdividegroup==null && bean.sdividegroup!=null) || (this.sdividegroup!=null && bean.sdividegroup==null))
          return false;
        else if(this.sdividegroup==null && bean.sdividegroup==null){
        }
        else{
          if(!bean.sdividegroup.equals(this.sdividegroup))
               return false;
     }
  	      //compare field sbillkind
      if((this.sbillkind==null && bean.sbillkind!=null) || (this.sbillkind!=null && bean.sbillkind==null))
          return false;
        else if(this.sbillkind==null && bean.sbillkind==null){
        }
        else{
          if(!bean.sbillkind.equals(this.sbillkind))
               return false;
     }
          return true;
    }


   /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
		int __hash__ = 1;
		

        if(this.sfinorgcode!=null)
          __hash__ = __hash__ * 31+ this.sfinorgcode.hashCode() ;

        if(this.staxorgcode!=null)
          __hash__ = __hash__ * 31+ this.staxorgcode.hashCode() ;

        if(this.strecode!=null)
          __hash__ = __hash__ * 31+ this.strecode.hashCode() ;

        if(this.srptdate!=null)
          __hash__ = __hash__ * 31+ this.srptdate.hashCode() ;

        if(this.sbudgettype!=null)
          __hash__ = __hash__ * 31+ this.sbudgettype.hashCode() ;

        if(this.sbudgetlevelcode!=null)
          __hash__ = __hash__ * 31+ this.sbudgetlevelcode.hashCode() ;

        if(this.sbudgetsubcode!=null)
          __hash__ = __hash__ * 31+ this.sbudgetsubcode.hashCode() ;

        if(this.strimflag!=null)
          __hash__ = __hash__ * 31+ this.strimflag.hashCode() ;

        if(this.sdividegroup!=null)
          __hash__ = __hash__ * 31+ this.sdividegroup.hashCode() ;

        if(this.sbillkind!=null)
          __hash__ = __hash__ * 31+ this.sbillkind.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        HtrIncomedayrptPK bean = new HtrIncomedayrptPK();
          if(this.sfinorgcode!=null){
                  bean.sfinorgcode = new String(this.sfinorgcode);
            }
             if(this.staxorgcode!=null){
                  bean.staxorgcode = new String(this.staxorgcode);
            }
             if(this.strecode!=null){
                  bean.strecode = new String(this.strecode);
            }
             if(this.srptdate!=null){
                  bean.srptdate = new String(this.srptdate);
            }
             if(this.sbudgettype!=null){
                  bean.sbudgettype = new String(this.sbudgettype);
            }
             if(this.sbudgetlevelcode!=null){
                  bean.sbudgetlevelcode = new String(this.sbudgetlevelcode);
            }
             if(this.sbudgetsubcode!=null){
                  bean.sbudgetsubcode = new String(this.sbudgetsubcode);
            }
             if(this.strimflag!=null){
                  bean.strimflag = new String(this.strimflag);
            }
             if(this.sdividegroup!=null){
                  bean.sdividegroup = new String(this.sdividegroup);
            }
             if(this.sbillkind!=null){
                  bean.sbillkind = new String(this.sbillkind);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("HtrIncomedayrptPK").append(":");
            sb.append("[sfinorgcode]").append(" = ").append(sfinorgcode).append(sep);
            sb.append("[staxorgcode]").append(" = ").append(staxorgcode).append(sep);
            sb.append("[strecode]").append(" = ").append(strecode).append(sep);
            sb.append("[srptdate]").append(" = ").append(srptdate).append(sep);
            sb.append("[sbudgettype]").append(" = ").append(sbudgettype).append(sep);
            sb.append("[sbudgetlevelcode]").append(" = ").append(sbudgetlevelcode).append(sep);
            sb.append("[sbudgetsubcode]").append(" = ").append(sbudgetsubcode).append(sep);
            sb.append("[strimflag]").append(" = ").append(strimflag).append(sep);
            sb.append("[sdividegroup]").append(" = ").append(sdividegroup).append(sep);
            sb.append("[sbillkind]").append(" = ").append(sbillkind).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSfinorgcode()==null)
              sb.append("数据库表：HTR_INCOMEDAYRPT 字段：S_FINORGCODE 不能为空; ");
                
       if (this.getSfinorgcode()!=null)
          if (this.getSfinorgcode().getBytes().length > 30)
             sb.append("数据库表：HTR_INCOMEDAYRPT 字段：S_FINORGCODE 宽度不能超过 "+30);
                
        if (this.getStaxorgcode()==null)
              sb.append("数据库表：HTR_INCOMEDAYRPT 字段：S_TAXORGCODE 不能为空; ");
                
       if (this.getStaxorgcode()!=null)
          if (this.getStaxorgcode().getBytes().length > 30)
             sb.append("数据库表：HTR_INCOMEDAYRPT 字段：S_TAXORGCODE 宽度不能超过 "+30);
                
        if (this.getStrecode()==null)
              sb.append("数据库表：HTR_INCOMEDAYRPT 字段：S_TRECODE 不能为空; ");
                
       if (this.getStrecode()!=null)
          if (this.getStrecode().getBytes().length > 10)
             sb.append("数据库表：HTR_INCOMEDAYRPT 字段：S_TRECODE 宽度不能超过 "+10);
                
        if (this.getSrptdate()==null)
              sb.append("数据库表：HTR_INCOMEDAYRPT 字段：S_RPTDATE 不能为空; ");
                
       if (this.getSrptdate()!=null)
          if (this.getSrptdate().getBytes().length > 8)
             sb.append("数据库表：HTR_INCOMEDAYRPT 字段：S_RPTDATE 宽度不能超过 "+8);
                
        if (this.getSbudgettype()==null)
              sb.append("数据库表：HTR_INCOMEDAYRPT 字段：S_BUDGETTYPE 不能为空; ");
                
       if (this.getSbudgettype()!=null)
          if (this.getSbudgettype().getBytes().length > 1)
             sb.append("数据库表：HTR_INCOMEDAYRPT 字段：S_BUDGETTYPE 宽度不能超过 "+1);
                
        if (this.getSbudgetlevelcode()==null)
              sb.append("数据库表：HTR_INCOMEDAYRPT 字段：S_BUDGETLEVELCODE 不能为空; ");
                
       if (this.getSbudgetlevelcode()!=null)
          if (this.getSbudgetlevelcode().getBytes().length > 1)
             sb.append("数据库表：HTR_INCOMEDAYRPT 字段：S_BUDGETLEVELCODE 宽度不能超过 "+1);
                
        if (this.getSbudgetsubcode()==null)
              sb.append("数据库表：HTR_INCOMEDAYRPT 字段：S_BUDGETSUBCODE 不能为空; ");
                
       if (this.getSbudgetsubcode()!=null)
          if (this.getSbudgetsubcode().getBytes().length > 30)
             sb.append("数据库表：HTR_INCOMEDAYRPT 字段：S_BUDGETSUBCODE 宽度不能超过 "+30);
                
        if (this.getStrimflag()==null)
              sb.append("数据库表：HTR_INCOMEDAYRPT 字段：S_TRIMFLAG 不能为空; ");
                
       if (this.getStrimflag()!=null)
          if (this.getStrimflag().getBytes().length > 1)
             sb.append("数据库表：HTR_INCOMEDAYRPT 字段：S_TRIMFLAG 宽度不能超过 "+1);
                
        if (this.getSdividegroup()==null)
              sb.append("数据库表：HTR_INCOMEDAYRPT 字段：S_DIVIDEGROUP 不能为空; ");
                
       if (this.getSdividegroup()!=null)
          if (this.getSdividegroup().getBytes().length > 5)
             sb.append("数据库表：HTR_INCOMEDAYRPT 字段：S_DIVIDEGROUP 宽度不能超过 "+5);
                
        if (this.getSbillkind()==null)
              sb.append("数据库表：HTR_INCOMEDAYRPT 字段：S_BILLKIND 不能为空; ");
                
       if (this.getSbillkind()!=null)
          if (this.getSbillkind().getBytes().length > 1)
             sb.append("数据库表：HTR_INCOMEDAYRPT 字段：S_BILLKIND 宽度不能超过 "+1);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}

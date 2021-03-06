package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: TS_USERSTAMPFUNCTION</p>
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

public class TsUserstampfunctionPK implements IPK
{
    /**
    *   S_ORGCODE VARCHAR  , PK   , NOT NULL        */
    protected String sorgcode;
    /**
    *   S_USERCODE VARCHAR  , PK   , NOT NULL        */
    protected String susercode;
    /**
    *   S_STAMPTYPECODE VARCHAR  , PK   , NOT NULL        */
    protected String sstamptypecode;
    /**
    *   S_MODELID VARCHAR  , PK   , NOT NULL        */
    protected String smodelid;
    /**
    *   S_PLACEID VARCHAR  , PK   , NOT NULL        */
    protected String splaceid;



    /*  Getter S_ORGCODE, PK , NOT NULL   */
    public String getSorgcode()
    {
        return sorgcode;
    }

     /*   Setter S_ORGCODE, PK , NOT NULL   */
    public void setSorgcode(String _sorgcode) {
        this.sorgcode = _sorgcode;
    }
    /*  Getter S_USERCODE, PK , NOT NULL   */
    public String getSusercode()
    {
        return susercode;
    }

     /*   Setter S_USERCODE, PK , NOT NULL   */
    public void setSusercode(String _susercode) {
        this.susercode = _susercode;
    }
    /*  Getter S_STAMPTYPECODE, PK , NOT NULL   */
    public String getSstamptypecode()
    {
        return sstamptypecode;
    }

     /*   Setter S_STAMPTYPECODE, PK , NOT NULL   */
    public void setSstamptypecode(String _sstamptypecode) {
        this.sstamptypecode = _sstamptypecode;
    }
    /*  Getter S_MODELID, PK , NOT NULL   */
    public String getSmodelid()
    {
        return smodelid;
    }

     /*   Setter S_MODELID, PK , NOT NULL   */
    public void setSmodelid(String _smodelid) {
        this.smodelid = _smodelid;
    }
    /*  Getter S_PLACEID, PK , NOT NULL   */
    public String getSplaceid()
    {
        return splaceid;
    }

     /*   Setter S_PLACEID, PK , NOT NULL   */
    public void setSplaceid(String _splaceid) {
        this.splaceid = _splaceid;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TsUserstampfunctionPK))
            return false;

        TsUserstampfunctionPK bean = (TsUserstampfunctionPK) obj;

	      //compare field sorgcode
      if((this.sorgcode==null && bean.sorgcode!=null) || (this.sorgcode!=null && bean.sorgcode==null))
          return false;
        else if(this.sorgcode==null && bean.sorgcode==null){
        }
        else{
          if(!bean.sorgcode.equals(this.sorgcode))
               return false;
     }
  	      //compare field susercode
      if((this.susercode==null && bean.susercode!=null) || (this.susercode!=null && bean.susercode==null))
          return false;
        else if(this.susercode==null && bean.susercode==null){
        }
        else{
          if(!bean.susercode.equals(this.susercode))
               return false;
     }
  	      //compare field sstamptypecode
      if((this.sstamptypecode==null && bean.sstamptypecode!=null) || (this.sstamptypecode!=null && bean.sstamptypecode==null))
          return false;
        else if(this.sstamptypecode==null && bean.sstamptypecode==null){
        }
        else{
          if(!bean.sstamptypecode.equals(this.sstamptypecode))
               return false;
     }
  	      //compare field smodelid
      if((this.smodelid==null && bean.smodelid!=null) || (this.smodelid!=null && bean.smodelid==null))
          return false;
        else if(this.smodelid==null && bean.smodelid==null){
        }
        else{
          if(!bean.smodelid.equals(this.smodelid))
               return false;
     }
  	      //compare field splaceid
      if((this.splaceid==null && bean.splaceid!=null) || (this.splaceid!=null && bean.splaceid==null))
          return false;
        else if(this.splaceid==null && bean.splaceid==null){
        }
        else{
          if(!bean.splaceid.equals(this.splaceid))
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

        if(this.susercode!=null)
          __hash__ = __hash__ * 31+ this.susercode.hashCode() ;

        if(this.sstamptypecode!=null)
          __hash__ = __hash__ * 31+ this.sstamptypecode.hashCode() ;

        if(this.smodelid!=null)
          __hash__ = __hash__ * 31+ this.smodelid.hashCode() ;

        if(this.splaceid!=null)
          __hash__ = __hash__ * 31+ this.splaceid.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsUserstampfunctionPK bean = new TsUserstampfunctionPK();
          if(this.sorgcode!=null){
                  bean.sorgcode = new String(this.sorgcode);
            }
             if(this.susercode!=null){
                  bean.susercode = new String(this.susercode);
            }
             if(this.sstamptypecode!=null){
                  bean.sstamptypecode = new String(this.sstamptypecode);
            }
             if(this.smodelid!=null){
                  bean.smodelid = new String(this.smodelid);
            }
             if(this.splaceid!=null){
                  bean.splaceid = new String(this.splaceid);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsUserstampfunctionPK").append(":");
            sb.append("[sorgcode]").append(" = ").append(sorgcode).append(sep);
            sb.append("[susercode]").append(" = ").append(susercode).append(sep);
            sb.append("[sstamptypecode]").append(" = ").append(sstamptypecode).append(sep);
            sb.append("[smodelid]").append(" = ").append(smodelid).append(sep);
            sb.append("[splaceid]").append(" = ").append(splaceid).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSorgcode()==null)
              sb.append("数据库表：TS_USERSTAMPFUNCTION 字段：S_ORGCODE 不能为空; ");
                
       if (this.getSorgcode()!=null)
          if (this.getSorgcode().getBytes().length > 12)
             sb.append("数据库表：TS_USERSTAMPFUNCTION 字段：S_ORGCODE 宽度不能超过 "+12);
                
        if (this.getSusercode()==null)
              sb.append("数据库表：TS_USERSTAMPFUNCTION 字段：S_USERCODE 不能为空; ");
                
       if (this.getSusercode()!=null)
          if (this.getSusercode().getBytes().length > 30)
             sb.append("数据库表：TS_USERSTAMPFUNCTION 字段：S_USERCODE 宽度不能超过 "+30);
                
        if (this.getSstamptypecode()==null)
              sb.append("数据库表：TS_USERSTAMPFUNCTION 字段：S_STAMPTYPECODE 不能为空; ");
                
       if (this.getSstamptypecode()!=null)
          if (this.getSstamptypecode().getBytes().length > 10)
             sb.append("数据库表：TS_USERSTAMPFUNCTION 字段：S_STAMPTYPECODE 宽度不能超过 "+10);
                
        if (this.getSmodelid()==null)
              sb.append("数据库表：TS_USERSTAMPFUNCTION 字段：S_MODELID 不能为空; ");
                
       if (this.getSmodelid()!=null)
          if (this.getSmodelid().getBytes().length > 30)
             sb.append("数据库表：TS_USERSTAMPFUNCTION 字段：S_MODELID 宽度不能超过 "+30);
                
        if (this.getSplaceid()==null)
              sb.append("数据库表：TS_USERSTAMPFUNCTION 字段：S_PLACEID 不能为空; ");
                
       if (this.getSplaceid()!=null)
          if (this.getSplaceid().getBytes().length > 30)
             sb.append("数据库表：TS_USERSTAMPFUNCTION 字段：S_PLACEID 宽度不能超过 "+30);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}

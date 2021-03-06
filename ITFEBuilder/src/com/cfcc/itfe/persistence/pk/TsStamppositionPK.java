package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: TS_STAMPPOSITION</p>
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

public class TsStamppositionPK implements IPK
{
    /**
    *   S_ADMDIVCODE VARCHAR  , PK   , NOT NULL        */
    protected String sadmdivcode;
    /**
    *   S_VTCODE VARCHAR  , PK   , NOT NULL        */
    protected String svtcode;
    /**
    *   S_STAMPPOSITION VARCHAR  , PK   , NOT NULL        */
    protected String sstampposition;



    /*  Getter S_ADMDIVCODE, PK , NOT NULL   */
    public String getSadmdivcode()
    {
        return sadmdivcode;
    }

     /*   Setter S_ADMDIVCODE, PK , NOT NULL   */
    public void setSadmdivcode(String _sadmdivcode) {
        this.sadmdivcode = _sadmdivcode;
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
    /*  Getter S_STAMPPOSITION, PK , NOT NULL   */
    public String getSstampposition()
    {
        return sstampposition;
    }

     /*   Setter S_STAMPPOSITION, PK , NOT NULL   */
    public void setSstampposition(String _sstampposition) {
        this.sstampposition = _sstampposition;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TsStamppositionPK))
            return false;

        TsStamppositionPK bean = (TsStamppositionPK) obj;

	      //compare field sadmdivcode
      if((this.sadmdivcode==null && bean.sadmdivcode!=null) || (this.sadmdivcode!=null && bean.sadmdivcode==null))
          return false;
        else if(this.sadmdivcode==null && bean.sadmdivcode==null){
        }
        else{
          if(!bean.sadmdivcode.equals(this.sadmdivcode))
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
  	      //compare field sstampposition
      if((this.sstampposition==null && bean.sstampposition!=null) || (this.sstampposition!=null && bean.sstampposition==null))
          return false;
        else if(this.sstampposition==null && bean.sstampposition==null){
        }
        else{
          if(!bean.sstampposition.equals(this.sstampposition))
               return false;
     }
          return true;
    }


   /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
		int __hash__ = 1;
		

        if(this.sadmdivcode!=null)
          __hash__ = __hash__ * 31+ this.sadmdivcode.hashCode() ;

        if(this.svtcode!=null)
          __hash__ = __hash__ * 31+ this.svtcode.hashCode() ;

        if(this.sstampposition!=null)
          __hash__ = __hash__ * 31+ this.sstampposition.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsStamppositionPK bean = new TsStamppositionPK();
          if(this.sadmdivcode!=null){
                  bean.sadmdivcode = new String(this.sadmdivcode);
            }
             if(this.svtcode!=null){
                  bean.svtcode = new String(this.svtcode);
            }
             if(this.sstampposition!=null){
                  bean.sstampposition = new String(this.sstampposition);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsStamppositionPK").append(":");
            sb.append("[sadmdivcode]").append(" = ").append(sadmdivcode).append(sep);
            sb.append("[svtcode]").append(" = ").append(svtcode).append(sep);
            sb.append("[sstampposition]").append(" = ").append(sstampposition).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSadmdivcode()==null)
              sb.append("数据库表：TS_STAMPPOSITION 字段：S_ADMDIVCODE 不能为空; ");
                
       if (this.getSadmdivcode()!=null)
          if (this.getSadmdivcode().getBytes().length > 10)
             sb.append("数据库表：TS_STAMPPOSITION 字段：S_ADMDIVCODE 宽度不能超过 "+10);
                
        if (this.getSvtcode()==null)
              sb.append("数据库表：TS_STAMPPOSITION 字段：S_VTCODE 不能为空; ");
                
       if (this.getSvtcode()!=null)
          if (this.getSvtcode().getBytes().length > 10)
             sb.append("数据库表：TS_STAMPPOSITION 字段：S_VTCODE 宽度不能超过 "+10);
                
        if (this.getSstampposition()==null)
              sb.append("数据库表：TS_STAMPPOSITION 字段：S_STAMPPOSITION 不能为空; ");
                
       if (this.getSstampposition()!=null)
          if (this.getSstampposition().getBytes().length > 64)
             sb.append("数据库表：TS_STAMPPOSITION 字段：S_STAMPPOSITION 宽度不能超过 "+64);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}

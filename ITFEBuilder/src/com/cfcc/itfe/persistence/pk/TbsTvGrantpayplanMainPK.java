package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: TBS_TV_GRANTPAYPLAN_MAIN</p>
 * <p>Description:  PK Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:57 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *
 *  pk.vm version timestamp: 2007-04-06 08:30:00
 * 
 * @author win7
 */

public class TbsTvGrantpayplanMainPK implements IPK
{
    /**
    *   I_VOUSRLNO BIGINT  , PK   , NOT NULL        */
    protected Long ivousrlno;
    /**
    *   S_BOOKORGCODE CHARACTER  , PK   , NOT NULL        */
    protected String sbookorgcode;



    /*  Getter I_VOUSRLNO, PK , NOT NULL   */
    public Long getIvousrlno()
    {
        return ivousrlno;
    }

     /*   Setter I_VOUSRLNO, PK , NOT NULL   */
    public void setIvousrlno(Long _ivousrlno) {
        this.ivousrlno = _ivousrlno;
    }
    /*  Getter S_BOOKORGCODE, PK , NOT NULL   */
    public String getSbookorgcode()
    {
        return sbookorgcode;
    }

     /*   Setter S_BOOKORGCODE, PK , NOT NULL   */
    public void setSbookorgcode(String _sbookorgcode) {
        this.sbookorgcode = _sbookorgcode;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TbsTvGrantpayplanMainPK))
            return false;

        TbsTvGrantpayplanMainPK bean = (TbsTvGrantpayplanMainPK) obj;

	      //compare field ivousrlno
      if((this.ivousrlno==null && bean.ivousrlno!=null) || (this.ivousrlno!=null && bean.ivousrlno==null))
          return false;
        else if(this.ivousrlno==null && bean.ivousrlno==null){
        }
        else{
          if(!bean.ivousrlno.equals(this.ivousrlno))
               return false;
     }
  	      //compare field sbookorgcode
      if((this.sbookorgcode==null && bean.sbookorgcode!=null) || (this.sbookorgcode!=null && bean.sbookorgcode==null))
          return false;
        else if(this.sbookorgcode==null && bean.sbookorgcode==null){
        }
        else{
          if(!bean.sbookorgcode.equals(this.sbookorgcode))
               return false;
     }
          return true;
    }


   /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
		int __hash__ = 1;
		

        if(this.ivousrlno!=null)
          __hash__ = __hash__ * 31+ this.ivousrlno.hashCode() ;

        if(this.sbookorgcode!=null)
          __hash__ = __hash__ * 31+ this.sbookorgcode.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TbsTvGrantpayplanMainPK bean = new TbsTvGrantpayplanMainPK();
          if(this.ivousrlno!=null){
                  bean.ivousrlno = new Long(this.ivousrlno.toString());
            }
             if(this.sbookorgcode!=null){
                  bean.sbookorgcode = new String(this.sbookorgcode);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("TbsTvGrantpayplanMainPK").append(":");
            sb.append("[ivousrlno]").append(" = ").append(ivousrlno).append(sep);
            sb.append("[sbookorgcode]").append(" = ").append(sbookorgcode).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getIvousrlno()==null)
              sb.append("数据库表：TBS_TV_GRANTPAYPLAN_MAIN 字段：I_VOUSRLNO 不能为空; ");
                        
        if (this.getSbookorgcode()==null)
              sb.append("数据库表：TBS_TV_GRANTPAYPLAN_MAIN 字段：S_BOOKORGCODE 不能为空; ");
                
       if (this.getSbookorgcode()!=null)
          if (this.getSbookorgcode().getBytes().length > 12)
             sb.append("数据库表：TBS_TV_GRANTPAYPLAN_MAIN 字段：S_BOOKORGCODE 宽度不能超过 "+12);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}

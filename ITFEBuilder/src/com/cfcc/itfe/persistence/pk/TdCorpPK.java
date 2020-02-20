package com.cfcc.itfe.persistence.pk;

import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp ;
import com.cfcc.jaf.persistence.jaform.parent.IPK ;

/**
 * <p>Title: PK of table: TD_CORP</p>
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

public class TdCorpPK implements IPK
{
    /**
    *   S_BOOKORGCODE CHARACTER  , PK   , NOT NULL        */
    protected String sbookorgcode;
    /**
    *   S_TRECODE CHARACTER  , PK   , NOT NULL        */
    protected String strecode;
    /**
    *   S_CORPCODE VARCHAR  , PK   , NOT NULL        */
    protected String scorpcode;
    /**
    *   C_TRIMFLAG CHARACTER  , PK   , NOT NULL        */
    protected String ctrimflag;



    /*  Getter S_BOOKORGCODE, PK , NOT NULL   */
    public String getSbookorgcode()
    {
        return sbookorgcode;
    }

     /*   Setter S_BOOKORGCODE, PK , NOT NULL   */
    public void setSbookorgcode(String _sbookorgcode) {
        this.sbookorgcode = _sbookorgcode;
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
    /*  Getter S_CORPCODE, PK , NOT NULL   */
    public String getScorpcode()
    {
        return scorpcode;
    }

     /*   Setter S_CORPCODE, PK , NOT NULL   */
    public void setScorpcode(String _scorpcode) {
        this.scorpcode = _scorpcode;
    }
    /*  Getter C_TRIMFLAG, PK , NOT NULL   */
    public String getCtrimflag()
    {
        return ctrimflag;
    }

     /*   Setter C_TRIMFLAG, PK , NOT NULL   */
    public void setCtrimflag(String _ctrimflag) {
        this.ctrimflag = _ctrimflag;
    }


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TdCorpPK))
            return false;

        TdCorpPK bean = (TdCorpPK) obj;

	      //compare field sbookorgcode
      if((this.sbookorgcode==null && bean.sbookorgcode!=null) || (this.sbookorgcode!=null && bean.sbookorgcode==null))
          return false;
        else if(this.sbookorgcode==null && bean.sbookorgcode==null){
        }
        else{
          if(!bean.sbookorgcode.equals(this.sbookorgcode))
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
  	      //compare field scorpcode
      if((this.scorpcode==null && bean.scorpcode!=null) || (this.scorpcode!=null && bean.scorpcode==null))
          return false;
        else if(this.scorpcode==null && bean.scorpcode==null){
        }
        else{
          if(!bean.scorpcode.equals(this.scorpcode))
               return false;
     }
  	      //compare field ctrimflag
      if((this.ctrimflag==null && bean.ctrimflag!=null) || (this.ctrimflag!=null && bean.ctrimflag==null))
          return false;
        else if(this.ctrimflag==null && bean.ctrimflag==null){
        }
        else{
          if(!bean.ctrimflag.equals(this.ctrimflag))
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

        if(this.strecode!=null)
          __hash__ = __hash__ * 31+ this.strecode.hashCode() ;

        if(this.scorpcode!=null)
          __hash__ = __hash__ * 31+ this.scorpcode.hashCode() ;

        if(this.ctrimflag!=null)
          __hash__ = __hash__ * 31+ this.ctrimflag.hashCode() ;

		return __hash__;
	
	}



     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TdCorpPK bean = new TdCorpPK();
          if(this.sbookorgcode!=null){
                  bean.sbookorgcode = new String(this.sbookorgcode);
            }
             if(this.strecode!=null){
                  bean.strecode = new String(this.strecode);
            }
             if(this.scorpcode!=null){
                  bean.scorpcode = new String(this.scorpcode);
            }
             if(this.ctrimflag!=null){
                  bean.ctrimflag = new String(this.ctrimflag);
            }
   
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = ", ";
        StringBuffer sb = new StringBuffer();
        sb.append("TdCorpPK").append(":");
            sb.append("[sbookorgcode]").append(" = ").append(sbookorgcode).append(sep);
            sb.append("[strecode]").append(" = ").append(strecode).append(sep);
            sb.append("[scorpcode]").append(" = ").append(scorpcode).append(sep);
            sb.append("[ctrimflag]").append(" = ").append(ctrimflag).append(sep);
            return sb.toString();
    }


     /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

          
        if (this.getSbookorgcode()==null)
              sb.append("���ݿ����TD_CORP �ֶΣ�S_BOOKORGCODE ����Ϊ��; ");
                
       if (this.getSbookorgcode()!=null)
          if (this.getSbookorgcode().getBytes().length > 12)
             sb.append("���ݿ����TD_CORP �ֶΣ�S_BOOKORGCODE ���Ȳ��ܳ��� "+12);
                
        if (this.getStrecode()==null)
              sb.append("���ݿ����TD_CORP �ֶΣ�S_TRECODE ����Ϊ��; ");
                
       if (this.getStrecode()!=null)
          if (this.getStrecode().getBytes().length > 10)
             sb.append("���ݿ����TD_CORP �ֶΣ�S_TRECODE ���Ȳ��ܳ��� "+10);
                
        if (this.getScorpcode()==null)
              sb.append("���ݿ����TD_CORP �ֶΣ�S_CORPCODE ����Ϊ��; ");
                
       if (this.getScorpcode()!=null)
          if (this.getScorpcode().getBytes().length > 15)
             sb.append("���ݿ����TD_CORP �ֶΣ�S_CORPCODE ���Ȳ��ܳ��� "+15);
                
        if (this.getCtrimflag()==null)
              sb.append("���ݿ����TD_CORP �ֶΣ�C_TRIMFLAG ����Ϊ��; ");
                
       if (this.getCtrimflag()!=null)
          if (this.getCtrimflag().getBytes().length > 1)
             sb.append("���ݿ����TD_CORP �ֶΣ�C_TRIMFLAG ���Ȳ��ܳ��� "+1);
        	String msg = sb.toString() ;
	if (msg.length() == 0)
		return null ;
  	return  msg;
   }

}
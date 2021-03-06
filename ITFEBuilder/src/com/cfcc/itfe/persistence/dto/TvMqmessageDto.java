    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TvMqmessagePK;
/**
 * <p>Title: DTO of table: TV_MQMESSAGE</p>
 * <p>Description:  Data Transfer Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:03 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  dto.vm version timestamp: 2008-01-08 16:30:00 
 *
 * @author win7
 */

public class TvMqmessageDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  I_SEQNO BIGINT , PK   , NOT NULL  , Identity  , Generated     */
    protected Long iseqno;
    /**
    *  S_SENDNODE CHARACTER   , NOT NULL       */
    protected String ssendnode;
    /**
    *  S_RECVNODE CHARACTER   , NOT NULL       */
    protected String srecvnode;
    /**
    *  S_MSGNO CHARACTER   , NOT NULL       */
    protected String smsgno;
    /**
    *  S_MSGID CHARACTER   , NOT NULL       */
    protected String smsgid;
    /**
    *  S_ENTRUSTDATE VARCHAR         */
    protected String sentrustdate;
    /**
    *  S_PACKNO VARCHAR         */
    protected String spackno;
    /**
    *  S_MQMSGID CHARACTER   , NOT NULL       */
    protected String smqmsgid;
    /**
    *  S_CORRELID CHARACTER   , NOT NULL       */
    protected String scorrelid;
    /**
    *  S_TAXORGCODE VARCHAR         */
    protected String staxorgcode;
    /**
    *  S_REMARK VARCHAR         */
    protected String sremark;
    /**
    *  S_SYSDATE TIMESTAMP         */
    protected Timestamp ssysdate;


/******************************************************
*
*  getter and setter
*
*******************************************************/


    public Long getIseqno()
    {
        return iseqno;
    }
     /**
     *   Setter I_SEQNO, PK , NOT NULL  , Identity  , Generated      * */
    public void setIseqno(Long _iseqno) {
        this.iseqno = _iseqno;
    }


    public String getSsendnode()
    {
        return ssendnode;
    }
     /**
     *   Setter S_SENDNODE , NOT NULL        * */
    public void setSsendnode(String _ssendnode) {
        this.ssendnode = _ssendnode;
    }


    public String getSrecvnode()
    {
        return srecvnode;
    }
     /**
     *   Setter S_RECVNODE , NOT NULL        * */
    public void setSrecvnode(String _srecvnode) {
        this.srecvnode = _srecvnode;
    }


    public String getSmsgno()
    {
        return smsgno;
    }
     /**
     *   Setter S_MSGNO , NOT NULL        * */
    public void setSmsgno(String _smsgno) {
        this.smsgno = _smsgno;
    }


    public String getSmsgid()
    {
        return smsgid;
    }
     /**
     *   Setter S_MSGID , NOT NULL        * */
    public void setSmsgid(String _smsgid) {
        this.smsgid = _smsgid;
    }


    public String getSentrustdate()
    {
        return sentrustdate;
    }
     /**
     *   Setter S_ENTRUSTDATE        * */
    public void setSentrustdate(String _sentrustdate) {
        this.sentrustdate = _sentrustdate;
    }


    public String getSpackno()
    {
        return spackno;
    }
     /**
     *   Setter S_PACKNO        * */
    public void setSpackno(String _spackno) {
        this.spackno = _spackno;
    }


    public String getSmqmsgid()
    {
        return smqmsgid;
    }
     /**
     *   Setter S_MQMSGID , NOT NULL        * */
    public void setSmqmsgid(String _smqmsgid) {
        this.smqmsgid = _smqmsgid;
    }


    public String getScorrelid()
    {
        return scorrelid;
    }
     /**
     *   Setter S_CORRELID , NOT NULL        * */
    public void setScorrelid(String _scorrelid) {
        this.scorrelid = _scorrelid;
    }


    public String getStaxorgcode()
    {
        return staxorgcode;
    }
     /**
     *   Setter S_TAXORGCODE        * */
    public void setStaxorgcode(String _staxorgcode) {
        this.staxorgcode = _staxorgcode;
    }


    public String getSremark()
    {
        return sremark;
    }
     /**
     *   Setter S_REMARK        * */
    public void setSremark(String _sremark) {
        this.sremark = _sremark;
    }


    public Timestamp getSsysdate()
    {
        return ssysdate;
    }
     /**
     *   Setter S_SYSDATE        * */
    public void setSsysdate(Timestamp _ssysdate) {
        this.ssysdate = _ssysdate;
    }




/******************************************************
*
*  Get Column Name
*
*******************************************************/
    /**
    *   Getter I_SEQNO, PK , NOT NULL  , Identity  , Generated     * */
    public static String  columnIseqno()
    {
        return "I_SEQNO";
    }
   
    /**
    *   Getter S_SENDNODE , NOT NULL       * */
    public static String  columnSsendnode()
    {
        return "S_SENDNODE";
    }
   
    /**
    *   Getter S_RECVNODE , NOT NULL       * */
    public static String  columnSrecvnode()
    {
        return "S_RECVNODE";
    }
   
    /**
    *   Getter S_MSGNO , NOT NULL       * */
    public static String  columnSmsgno()
    {
        return "S_MSGNO";
    }
   
    /**
    *   Getter S_MSGID , NOT NULL       * */
    public static String  columnSmsgid()
    {
        return "S_MSGID";
    }
   
    /**
    *   Getter S_ENTRUSTDATE       * */
    public static String  columnSentrustdate()
    {
        return "S_ENTRUSTDATE";
    }
   
    /**
    *   Getter S_PACKNO       * */
    public static String  columnSpackno()
    {
        return "S_PACKNO";
    }
   
    /**
    *   Getter S_MQMSGID , NOT NULL       * */
    public static String  columnSmqmsgid()
    {
        return "S_MQMSGID";
    }
   
    /**
    *   Getter S_CORRELID , NOT NULL       * */
    public static String  columnScorrelid()
    {
        return "S_CORRELID";
    }
   
    /**
    *   Getter S_TAXORGCODE       * */
    public static String  columnStaxorgcode()
    {
        return "S_TAXORGCODE";
    }
   
    /**
    *   Getter S_REMARK       * */
    public static String  columnSremark()
    {
        return "S_REMARK";
    }
   
    /**
    *   Getter S_SYSDATE       * */
    public static String  columnSsysdate()
    {
        return "S_SYSDATE";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "TV_MQMESSAGE";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[12];        
        columnNames[0]="I_SEQNO";
        columnNames[1]="S_SENDNODE";
        columnNames[2]="S_RECVNODE";
        columnNames[3]="S_MSGNO";
        columnNames[4]="S_MSGID";
        columnNames[5]="S_ENTRUSTDATE";
        columnNames[6]="S_PACKNO";
        columnNames[7]="S_MQMSGID";
        columnNames[8]="S_CORRELID";
        columnNames[9]="S_TAXORGCODE";
        columnNames[10]="S_REMARK";
        columnNames[11]="S_SYSDATE";
        return columnNames;     
    }
/*******************************************************
*
*  supplementary methods
*
*****************************************************/


    /* Indicates whether some other object is "equal to" this one. */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (obj == null || !(obj instanceof TvMqmessageDto))
            return false;

        TvMqmessageDto bean = (TvMqmessageDto) obj;


      //compare field iseqno
      if((this.iseqno==null && bean.iseqno!=null) || (this.iseqno!=null && bean.iseqno==null))
          return false;
        else if(this.iseqno==null && bean.iseqno==null){
        }
        else{
          if(!bean.iseqno.equals(this.iseqno))
               return false;
     }
      //compare field ssendnode
      if((this.ssendnode==null && bean.ssendnode!=null) || (this.ssendnode!=null && bean.ssendnode==null))
          return false;
        else if(this.ssendnode==null && bean.ssendnode==null){
        }
        else{
          if(!bean.ssendnode.equals(this.ssendnode))
               return false;
     }
      //compare field srecvnode
      if((this.srecvnode==null && bean.srecvnode!=null) || (this.srecvnode!=null && bean.srecvnode==null))
          return false;
        else if(this.srecvnode==null && bean.srecvnode==null){
        }
        else{
          if(!bean.srecvnode.equals(this.srecvnode))
               return false;
     }
      //compare field smsgno
      if((this.smsgno==null && bean.smsgno!=null) || (this.smsgno!=null && bean.smsgno==null))
          return false;
        else if(this.smsgno==null && bean.smsgno==null){
        }
        else{
          if(!bean.smsgno.equals(this.smsgno))
               return false;
     }
      //compare field smsgid
      if((this.smsgid==null && bean.smsgid!=null) || (this.smsgid!=null && bean.smsgid==null))
          return false;
        else if(this.smsgid==null && bean.smsgid==null){
        }
        else{
          if(!bean.smsgid.equals(this.smsgid))
               return false;
     }
      //compare field sentrustdate
      if((this.sentrustdate==null && bean.sentrustdate!=null) || (this.sentrustdate!=null && bean.sentrustdate==null))
          return false;
        else if(this.sentrustdate==null && bean.sentrustdate==null){
        }
        else{
          if(!bean.sentrustdate.equals(this.sentrustdate))
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
      //compare field smqmsgid
      if((this.smqmsgid==null && bean.smqmsgid!=null) || (this.smqmsgid!=null && bean.smqmsgid==null))
          return false;
        else if(this.smqmsgid==null && bean.smqmsgid==null){
        }
        else{
          if(!bean.smqmsgid.equals(this.smqmsgid))
               return false;
     }
      //compare field scorrelid
      if((this.scorrelid==null && bean.scorrelid!=null) || (this.scorrelid!=null && bean.scorrelid==null))
          return false;
        else if(this.scorrelid==null && bean.scorrelid==null){
        }
        else{
          if(!bean.scorrelid.equals(this.scorrelid))
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
      //compare field sremark
      if((this.sremark==null && bean.sremark!=null) || (this.sremark!=null && bean.sremark==null))
          return false;
        else if(this.sremark==null && bean.sremark==null){
        }
        else{
          if(!bean.sremark.equals(this.sremark))
               return false;
     }
      //compare field ssysdate
      if((this.ssysdate==null && bean.ssysdate!=null) || (this.ssysdate!=null && bean.ssysdate==null))
          return false;
        else if(this.ssysdate==null && bean.ssysdate==null){
        }
        else{
          if(!bean.ssysdate.equals(this.ssysdate))
               return false;
     }



        return true;
    }

    /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
  
		int _hash_ = 1;
		
        if(this.iseqno!=null)
          _hash_ = _hash_ * 31+ this.iseqno.hashCode() ;
        if(this.ssendnode!=null)
          _hash_ = _hash_ * 31+ this.ssendnode.hashCode() ;
        if(this.srecvnode!=null)
          _hash_ = _hash_ * 31+ this.srecvnode.hashCode() ;
        if(this.smsgno!=null)
          _hash_ = _hash_ * 31+ this.smsgno.hashCode() ;
        if(this.smsgid!=null)
          _hash_ = _hash_ * 31+ this.smsgid.hashCode() ;
        if(this.sentrustdate!=null)
          _hash_ = _hash_ * 31+ this.sentrustdate.hashCode() ;
        if(this.spackno!=null)
          _hash_ = _hash_ * 31+ this.spackno.hashCode() ;
        if(this.smqmsgid!=null)
          _hash_ = _hash_ * 31+ this.smqmsgid.hashCode() ;
        if(this.scorrelid!=null)
          _hash_ = _hash_ * 31+ this.scorrelid.hashCode() ;
        if(this.staxorgcode!=null)
          _hash_ = _hash_ * 31+ this.staxorgcode.hashCode() ;
        if(this.sremark!=null)
          _hash_ = _hash_ * 31+ this.sremark.hashCode() ;
        if(this.ssysdate!=null)
          _hash_ = _hash_ * 31+ this.ssysdate.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TvMqmessageDto bean = new TvMqmessageDto();

          bean.iseqno = this.iseqno;

          if (this.ssendnode != null)
            bean.ssendnode = new String(this.ssendnode);
          if (this.srecvnode != null)
            bean.srecvnode = new String(this.srecvnode);
          if (this.smsgno != null)
            bean.smsgno = new String(this.smsgno);
          if (this.smsgid != null)
            bean.smsgid = new String(this.smsgid);
          if (this.sentrustdate != null)
            bean.sentrustdate = new String(this.sentrustdate);
          if (this.spackno != null)
            bean.spackno = new String(this.spackno);
          if (this.smqmsgid != null)
            bean.smqmsgid = new String(this.smqmsgid);
          if (this.scorrelid != null)
            bean.scorrelid = new String(this.scorrelid);
          if (this.staxorgcode != null)
            bean.staxorgcode = new String(this.staxorgcode);
          if (this.sremark != null)
            bean.sremark = new String(this.sremark);
          if (this.ssysdate != null)
            bean.ssysdate = (Timestamp) this.ssysdate.clone();
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TvMqmessageDto").append(sep);
        sb.append("[iseqno]").append(" = ").append(iseqno).append(sep);
        sb.append("[ssendnode]").append(" = ").append(ssendnode).append(sep);
        sb.append("[srecvnode]").append(" = ").append(srecvnode).append(sep);
        sb.append("[smsgno]").append(" = ").append(smsgno).append(sep);
        sb.append("[smsgid]").append(" = ").append(smsgid).append(sep);
        sb.append("[sentrustdate]").append(" = ").append(sentrustdate).append(sep);
        sb.append("[spackno]").append(" = ").append(spackno).append(sep);
        sb.append("[smqmsgid]").append(" = ").append(smqmsgid).append(sep);
        sb.append("[scorrelid]").append(" = ").append(scorrelid).append(sep);
        sb.append("[staxorgcode]").append(" = ").append(staxorgcode).append(sep);
        sb.append("[sremark]").append(" = ").append(sremark).append(sep);
        sb.append("[ssysdate]").append(" = ").append(ssysdate).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //don't need check field I_SEQNO,it is generated column
  
    //check field S_SENDNODE
      if (this.getSsendnode()==null)
             sb.append("S_SENDNODE不能为空; ");
      if (this.getSsendnode()!=null)
             if (this.getSsendnode().getBytes().length > 12)
                sb.append("S_SENDNODE宽度不能超过 "+12+"个字符; ");
    
    //check field S_RECVNODE
      if (this.getSrecvnode()==null)
             sb.append("S_RECVNODE不能为空; ");
      if (this.getSrecvnode()!=null)
             if (this.getSrecvnode().getBytes().length > 12)
                sb.append("S_RECVNODE宽度不能超过 "+12+"个字符; ");
    
    //check field S_MSGNO
      if (this.getSmsgno()==null)
             sb.append("S_MSGNO不能为空; ");
      if (this.getSmsgno()!=null)
             if (this.getSmsgno().getBytes().length > 4)
                sb.append("S_MSGNO宽度不能超过 "+4+"个字符; ");
    
    //check field S_MSGID
      if (this.getSmsgid()==null)
             sb.append("S_MSGID不能为空; ");
      if (this.getSmsgid()!=null)
             if (this.getSmsgid().getBytes().length > 20)
                sb.append("S_MSGID宽度不能超过 "+20+"个字符; ");
    
    //check field S_ENTRUSTDATE
      if (this.getSentrustdate()!=null)
             if (this.getSentrustdate().getBytes().length > 8)
                sb.append("S_ENTRUSTDATE宽度不能超过 "+8+"个字符; ");
    
    //check field S_PACKNO
      if (this.getSpackno()!=null)
             if (this.getSpackno().getBytes().length > 8)
                sb.append("S_PACKNO宽度不能超过 "+8+"个字符; ");
    
    //check field S_MQMSGID
      if (this.getSmqmsgid()==null)
             sb.append("S_MQMSGID不能为空; ");
      if (this.getSmqmsgid()!=null)
             if (this.getSmqmsgid().getBytes().length > 51)
                sb.append("S_MQMSGID宽度不能超过 "+51+"个字符; ");
    
    //check field S_CORRELID
      if (this.getScorrelid()==null)
             sb.append("S_CORRELID不能为空; ");
      if (this.getScorrelid()!=null)
             if (this.getScorrelid().getBytes().length > 51)
                sb.append("S_CORRELID宽度不能超过 "+51+"个字符; ");
    
    //check field S_TAXORGCODE
      if (this.getStaxorgcode()!=null)
             if (this.getStaxorgcode().getBytes().length > 12)
                sb.append("S_TAXORGCODE宽度不能超过 "+12+"个字符; ");
    
    //check field S_REMARK
      if (this.getSremark()!=null)
             if (this.getSremark().getBytes().length > 60)
                sb.append("S_REMARK宽度不能超过 "+60+"个字符; ");
    
    //check field S_SYSDATE
      

 	String msg = sb.toString() ;
	if (msg.length() == 0)
		  return null ;

  return  msg;
  }
  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid(String[] _columnNames)
  {
  	StringBuffer sb = new StringBuffer() ;
    // check columnNames
    String checkNameMsg = checkColumnNamesValid(_columnNames);
    if (checkNameMsg != null) {
         return checkNameMsg;
    }
    //don't need check field I_SEQNO,it is generated column
  
    //check field S_SENDNODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_SENDNODE")) {
               if (this.getSsendnode()==null)
                    sb.append("S_SENDNODE 不能为空; ");
               if (this.getSsendnode()!=null)
                    if (this.getSsendnode().getBytes().length > 12)
                        sb.append("S_SENDNODE 宽度不能超过 "+12+"个字符");
             break;
         }
  }
    
    //check field S_RECVNODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_RECVNODE")) {
               if (this.getSrecvnode()==null)
                    sb.append("S_RECVNODE 不能为空; ");
               if (this.getSrecvnode()!=null)
                    if (this.getSrecvnode().getBytes().length > 12)
                        sb.append("S_RECVNODE 宽度不能超过 "+12+"个字符");
             break;
         }
  }
    
    //check field S_MSGNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_MSGNO")) {
               if (this.getSmsgno()==null)
                    sb.append("S_MSGNO 不能为空; ");
               if (this.getSmsgno()!=null)
                    if (this.getSmsgno().getBytes().length > 4)
                        sb.append("S_MSGNO 宽度不能超过 "+4+"个字符");
             break;
         }
  }
    
    //check field S_MSGID
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_MSGID")) {
               if (this.getSmsgid()==null)
                    sb.append("S_MSGID 不能为空; ");
               if (this.getSmsgid()!=null)
                    if (this.getSmsgid().getBytes().length > 20)
                        sb.append("S_MSGID 宽度不能超过 "+20+"个字符");
             break;
         }
  }
    
    //check field S_ENTRUSTDATE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_ENTRUSTDATE")) {
                 if (this.getSentrustdate()!=null)
                    if (this.getSentrustdate().getBytes().length > 8)
                        sb.append("S_ENTRUSTDATE 宽度不能超过 "+8+"个字符");
             break;
         }
  }
    
    //check field S_PACKNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_PACKNO")) {
                 if (this.getSpackno()!=null)
                    if (this.getSpackno().getBytes().length > 8)
                        sb.append("S_PACKNO 宽度不能超过 "+8+"个字符");
             break;
         }
  }
    
    //check field S_MQMSGID
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_MQMSGID")) {
               if (this.getSmqmsgid()==null)
                    sb.append("S_MQMSGID 不能为空; ");
               if (this.getSmqmsgid()!=null)
                    if (this.getSmqmsgid().getBytes().length > 51)
                        sb.append("S_MQMSGID 宽度不能超过 "+51+"个字符");
             break;
         }
  }
    
    //check field S_CORRELID
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_CORRELID")) {
               if (this.getScorrelid()==null)
                    sb.append("S_CORRELID 不能为空; ");
               if (this.getScorrelid()!=null)
                    if (this.getScorrelid().getBytes().length > 51)
                        sb.append("S_CORRELID 宽度不能超过 "+51+"个字符");
             break;
         }
  }
    
    //check field S_TAXORGCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXORGCODE")) {
                 if (this.getStaxorgcode()!=null)
                    if (this.getStaxorgcode().getBytes().length > 12)
                        sb.append("S_TAXORGCODE 宽度不能超过 "+12+"个字符");
             break;
         }
  }
    
    //check field S_REMARK
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_REMARK")) {
                 if (this.getSremark()!=null)
                    if (this.getSremark().getBytes().length > 60)
                        sb.append("S_REMARK 宽度不能超过 "+60+"个字符");
             break;
         }
  }
    
    //check field S_SYSDATE
          
 	String msg = sb.toString() ;
	if (msg.length() == 0)
		  return null ;

  return  msg;
  }
  /* Returns value valid checking String , NULL is Valid*/
	public String checkValidExcept(String[] _columnNames) {
		String msg = checkColumnNamesValid(_columnNames);
		if (msg != null) {
			return msg;
		}
		String[] columnCheckNames = new String[columnNames().length
				- _columnNames.length];
		int k = 0;
		for (int i = 0; i < columnNames().length; i++) {
			boolean checkNameInColumn = true;
			for (int j = 0; j < _columnNames.length; j++) {
				if (_columnNames[i].equals(columnNames()[j])) {
					checkNameInColumn = false;
					break;
				}
			}
			if (checkNameInColumn) {
				columnCheckNames[k] = columnNames()[i];
				k++;
			}
		}
		return checkValid(columnCheckNames);
	}
	/* Returns value valid checking String , NULL is Valid */
	public String checkColumnNamesValid(String[] _columnNames) {
		StringBuffer sb = new StringBuffer();
		if (_columnNames.length > columnNames().length) {
			return "输入字段个数多于表中字段个数; ";
		}
		// check columnNames
		for (int i = 0; i < _columnNames.length; i++) {
			boolean checkNameValid = false;
			for (int j = 0; j < columnNames().length; j++) {
				if (_columnNames[i] != null
						&& _columnNames[i].equals(columnNames()[j])) {
					checkNameValid = true;
					break;
				}
			}
			if (!checkNameValid)
				sb.append("输入字段 " + _columnNames[i] + " 不在该表字段中; ");
		}
		String msg = sb.toString();
		if (msg.length() == 0)
			return null;

		return msg;
	}
/*******************************************************
*
*  implement IDto
*
*****************************************************/

  /* if this Dto has children Dtos*/
  public boolean  isParent() {
     return false;
  };

  /* get the children Dtos if this has children*/
  public IDto[]  getChildren() {
     return null;
  };

  /* set the children Dtos if this has children*/
  public void  setChildren(IDto[] _dtos) 
  {
     throw new RuntimeException("此dto没有相关联的子dto，不能进行此操作");
  };
  
  /* return the IPK class  */
    public IPK      getPK(){
      TvMqmessagePK pk = new TvMqmessagePK();
      pk.setIseqno(getIseqno());
      return pk;
    };
}

    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TsTaxpayacctPK;
/**
 * <p>Title: DTO of table: TS_TAXPAYACCT</p>
 * <p>Description:  Data Transfer Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:01 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  dto.vm version timestamp: 2008-01-08 16:30:00 
 *
 * @author win7
 */

public class TsTaxpayacctDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  S_ORGCODE VARCHAR , PK   , NOT NULL       */
    protected String sorgcode;
    /**
    *  S_TRECODE CHARACTER   , NOT NULL       */
    protected String strecode;
    /**
    *  S_TAXORGCODE VARCHAR , PK   , NOT NULL       */
    protected String staxorgcode;
    /**
    *  S_TAXORGNAME VARCHAR         */
    protected String staxorgname;
    /**
    *  S_PAYERACCT VARCHAR , PK   , NOT NULL       */
    protected String spayeracct;
    /**
    *  S_PAYERNAME VARCHAR         */
    protected String spayername;
    /**
    *  S_TAXSUBCODE VARCHAR   , NOT NULL       */
    protected String staxsubcode;
    /**
    *  S_TAXSUBNAME VARCHAR   , NOT NULL       */
    protected String staxsubname;
    /**
    *  TS_SYSUPDATE TIMESTAMP   , NOT NULL       */
    protected Timestamp tssysupdate;


/******************************************************
*
*  getter and setter
*
*******************************************************/


    public String getSorgcode()
    {
        return sorgcode;
    }
     /**
     *   Setter S_ORGCODE, PK , NOT NULL        * */
    public void setSorgcode(String _sorgcode) {
        this.sorgcode = _sorgcode;
    }


    public String getStrecode()
    {
        return strecode;
    }
     /**
     *   Setter S_TRECODE , NOT NULL        * */
    public void setStrecode(String _strecode) {
        this.strecode = _strecode;
    }


    public String getStaxorgcode()
    {
        return staxorgcode;
    }
     /**
     *   Setter S_TAXORGCODE, PK , NOT NULL        * */
    public void setStaxorgcode(String _staxorgcode) {
        this.staxorgcode = _staxorgcode;
    }


    public String getStaxorgname()
    {
        return staxorgname;
    }
     /**
     *   Setter S_TAXORGNAME        * */
    public void setStaxorgname(String _staxorgname) {
        this.staxorgname = _staxorgname;
    }


    public String getSpayeracct()
    {
        return spayeracct;
    }
     /**
     *   Setter S_PAYERACCT, PK , NOT NULL        * */
    public void setSpayeracct(String _spayeracct) {
        this.spayeracct = _spayeracct;
    }


    public String getSpayername()
    {
        return spayername;
    }
     /**
     *   Setter S_PAYERNAME        * */
    public void setSpayername(String _spayername) {
        this.spayername = _spayername;
    }


    public String getStaxsubcode()
    {
        return staxsubcode;
    }
     /**
     *   Setter S_TAXSUBCODE , NOT NULL        * */
    public void setStaxsubcode(String _staxsubcode) {
        this.staxsubcode = _staxsubcode;
    }


    public String getStaxsubname()
    {
        return staxsubname;
    }
     /**
     *   Setter S_TAXSUBNAME , NOT NULL        * */
    public void setStaxsubname(String _staxsubname) {
        this.staxsubname = _staxsubname;
    }


    public Timestamp getTssysupdate()
    {
        return tssysupdate;
    }
     /**
     *   Setter TS_SYSUPDATE , NOT NULL        * */
    public void setTssysupdate(Timestamp _tssysupdate) {
        this.tssysupdate = _tssysupdate;
    }




/******************************************************
*
*  Get Column Name
*
*******************************************************/
    /**
    *   Getter S_ORGCODE, PK , NOT NULL       * */
    public static String  columnSorgcode()
    {
        return "S_ORGCODE";
    }
   
    /**
    *   Getter S_TRECODE , NOT NULL       * */
    public static String  columnStrecode()
    {
        return "S_TRECODE";
    }
   
    /**
    *   Getter S_TAXORGCODE, PK , NOT NULL       * */
    public static String  columnStaxorgcode()
    {
        return "S_TAXORGCODE";
    }
   
    /**
    *   Getter S_TAXORGNAME       * */
    public static String  columnStaxorgname()
    {
        return "S_TAXORGNAME";
    }
   
    /**
    *   Getter S_PAYERACCT, PK , NOT NULL       * */
    public static String  columnSpayeracct()
    {
        return "S_PAYERACCT";
    }
   
    /**
    *   Getter S_PAYERNAME       * */
    public static String  columnSpayername()
    {
        return "S_PAYERNAME";
    }
   
    /**
    *   Getter S_TAXSUBCODE , NOT NULL       * */
    public static String  columnStaxsubcode()
    {
        return "S_TAXSUBCODE";
    }
   
    /**
    *   Getter S_TAXSUBNAME , NOT NULL       * */
    public static String  columnStaxsubname()
    {
        return "S_TAXSUBNAME";
    }
   
    /**
    *   Getter TS_SYSUPDATE , NOT NULL       * */
    public static String  columnTssysupdate()
    {
        return "TS_SYSUPDATE";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "TS_TAXPAYACCT";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[9];        
        columnNames[0]="S_ORGCODE";
        columnNames[1]="S_TRECODE";
        columnNames[2]="S_TAXORGCODE";
        columnNames[3]="S_TAXORGNAME";
        columnNames[4]="S_PAYERACCT";
        columnNames[5]="S_PAYERNAME";
        columnNames[6]="S_TAXSUBCODE";
        columnNames[7]="S_TAXSUBNAME";
        columnNames[8]="TS_SYSUPDATE";
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

        if (obj == null || !(obj instanceof TsTaxpayacctDto))
            return false;

        TsTaxpayacctDto bean = (TsTaxpayacctDto) obj;


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
      //compare field staxorgcode
      if((this.staxorgcode==null && bean.staxorgcode!=null) || (this.staxorgcode!=null && bean.staxorgcode==null))
          return false;
        else if(this.staxorgcode==null && bean.staxorgcode==null){
        }
        else{
          if(!bean.staxorgcode.equals(this.staxorgcode))
               return false;
     }
      //compare field staxorgname
      if((this.staxorgname==null && bean.staxorgname!=null) || (this.staxorgname!=null && bean.staxorgname==null))
          return false;
        else if(this.staxorgname==null && bean.staxorgname==null){
        }
        else{
          if(!bean.staxorgname.equals(this.staxorgname))
               return false;
     }
      //compare field spayeracct
      if((this.spayeracct==null && bean.spayeracct!=null) || (this.spayeracct!=null && bean.spayeracct==null))
          return false;
        else if(this.spayeracct==null && bean.spayeracct==null){
        }
        else{
          if(!bean.spayeracct.equals(this.spayeracct))
               return false;
     }
      //compare field spayername
      if((this.spayername==null && bean.spayername!=null) || (this.spayername!=null && bean.spayername==null))
          return false;
        else if(this.spayername==null && bean.spayername==null){
        }
        else{
          if(!bean.spayername.equals(this.spayername))
               return false;
     }
      //compare field staxsubcode
      if((this.staxsubcode==null && bean.staxsubcode!=null) || (this.staxsubcode!=null && bean.staxsubcode==null))
          return false;
        else if(this.staxsubcode==null && bean.staxsubcode==null){
        }
        else{
          if(!bean.staxsubcode.equals(this.staxsubcode))
               return false;
     }
      //compare field staxsubname
      if((this.staxsubname==null && bean.staxsubname!=null) || (this.staxsubname!=null && bean.staxsubname==null))
          return false;
        else if(this.staxsubname==null && bean.staxsubname==null){
        }
        else{
          if(!bean.staxsubname.equals(this.staxsubname))
               return false;
     }
      //compare field tssysupdate
      if((this.tssysupdate==null && bean.tssysupdate!=null) || (this.tssysupdate!=null && bean.tssysupdate==null))
          return false;
        else if(this.tssysupdate==null && bean.tssysupdate==null){
        }
        else{
          if(!bean.tssysupdate.equals(this.tssysupdate))
               return false;
     }



        return true;
    }

    /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
  
		int _hash_ = 1;
		
        if(this.sorgcode!=null)
          _hash_ = _hash_ * 31+ this.sorgcode.hashCode() ;
        if(this.strecode!=null)
          _hash_ = _hash_ * 31+ this.strecode.hashCode() ;
        if(this.staxorgcode!=null)
          _hash_ = _hash_ * 31+ this.staxorgcode.hashCode() ;
        if(this.staxorgname!=null)
          _hash_ = _hash_ * 31+ this.staxorgname.hashCode() ;
        if(this.spayeracct!=null)
          _hash_ = _hash_ * 31+ this.spayeracct.hashCode() ;
        if(this.spayername!=null)
          _hash_ = _hash_ * 31+ this.spayername.hashCode() ;
        if(this.staxsubcode!=null)
          _hash_ = _hash_ * 31+ this.staxsubcode.hashCode() ;
        if(this.staxsubname!=null)
          _hash_ = _hash_ * 31+ this.staxsubname.hashCode() ;
        if(this.tssysupdate!=null)
          _hash_ = _hash_ * 31+ this.tssysupdate.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsTaxpayacctDto bean = new TsTaxpayacctDto();

          bean.sorgcode = this.sorgcode;

          if (this.strecode != null)
            bean.strecode = new String(this.strecode);
          bean.staxorgcode = this.staxorgcode;

          if (this.staxorgname != null)
            bean.staxorgname = new String(this.staxorgname);
          bean.spayeracct = this.spayeracct;

          if (this.spayername != null)
            bean.spayername = new String(this.spayername);
          if (this.staxsubcode != null)
            bean.staxsubcode = new String(this.staxsubcode);
          if (this.staxsubname != null)
            bean.staxsubname = new String(this.staxsubname);
          if (this.tssysupdate != null)
            bean.tssysupdate = (Timestamp) this.tssysupdate.clone();
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsTaxpayacctDto").append(sep);
        sb.append("[sorgcode]").append(" = ").append(sorgcode).append(sep);
        sb.append("[strecode]").append(" = ").append(strecode).append(sep);
        sb.append("[staxorgcode]").append(" = ").append(staxorgcode).append(sep);
        sb.append("[staxorgname]").append(" = ").append(staxorgname).append(sep);
        sb.append("[spayeracct]").append(" = ").append(spayeracct).append(sep);
        sb.append("[spayername]").append(" = ").append(spayername).append(sep);
        sb.append("[staxsubcode]").append(" = ").append(staxsubcode).append(sep);
        sb.append("[staxsubname]").append(" = ").append(staxsubname).append(sep);
        sb.append("[tssysupdate]").append(" = ").append(tssysupdate).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //check field S_ORGCODE
      if (this.getSorgcode()==null)
             sb.append("S_ORGCODE不能为空; ");
      if (this.getSorgcode()!=null)
             if (this.getSorgcode().getBytes().length > 12)
                sb.append("S_ORGCODE宽度不能超过 "+12+"个字符; ");
    
    //check field S_TRECODE
      if (this.getStrecode()==null)
             sb.append("S_TRECODE不能为空; ");
      if (this.getStrecode()!=null)
             if (this.getStrecode().getBytes().length > 10)
                sb.append("S_TRECODE宽度不能超过 "+10+"个字符; ");
    
    //check field S_TAXORGCODE
      if (this.getStaxorgcode()==null)
             sb.append("S_TAXORGCODE不能为空; ");
      if (this.getStaxorgcode()!=null)
             if (this.getStaxorgcode().getBytes().length > 12)
                sb.append("S_TAXORGCODE宽度不能超过 "+12+"个字符; ");
    
    //check field S_TAXORGNAME
      if (this.getStaxorgname()!=null)
             if (this.getStaxorgname().getBytes().length > 100)
                sb.append("S_TAXORGNAME宽度不能超过 "+100+"个字符; ");
    
    //check field S_PAYERACCT
      if (this.getSpayeracct()==null)
             sb.append("S_PAYERACCT不能为空; ");
      if (this.getSpayeracct()!=null)
             if (this.getSpayeracct().getBytes().length > 32)
                sb.append("S_PAYERACCT宽度不能超过 "+32+"个字符; ");
    
    //check field S_PAYERNAME
      if (this.getSpayername()!=null)
             if (this.getSpayername().getBytes().length > 60)
                sb.append("S_PAYERNAME宽度不能超过 "+60+"个字符; ");
    
    //check field S_TAXSUBCODE
      if (this.getStaxsubcode()==null)
             sb.append("S_TAXSUBCODE不能为空; ");
      if (this.getStaxsubcode()!=null)
             if (this.getStaxsubcode().getBytes().length > 30)
                sb.append("S_TAXSUBCODE宽度不能超过 "+30+"个字符; ");
    
    //check field S_TAXSUBNAME
      if (this.getStaxsubname()==null)
             sb.append("S_TAXSUBNAME不能为空; ");
      if (this.getStaxsubname()!=null)
             if (this.getStaxsubname().getBytes().length > 60)
                sb.append("S_TAXSUBNAME宽度不能超过 "+60+"个字符; ");
    
    //don't need check field TS_SYSUPDATE,it is UpdateTimeStamp column
  

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
    //check field S_ORGCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_ORGCODE")) {
               if (this.getSorgcode()==null)
                    sb.append("S_ORGCODE 不能为空; ");
               if (this.getSorgcode()!=null)
                    if (this.getSorgcode().getBytes().length > 12)
                        sb.append("S_ORGCODE 宽度不能超过 "+12+"个字符");
             break;
         }
  }
    
    //check field S_TRECODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TRECODE")) {
               if (this.getStrecode()==null)
                    sb.append("S_TRECODE 不能为空; ");
               if (this.getStrecode()!=null)
                    if (this.getStrecode().getBytes().length > 10)
                        sb.append("S_TRECODE 宽度不能超过 "+10+"个字符");
             break;
         }
  }
    
    //check field S_TAXORGCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXORGCODE")) {
               if (this.getStaxorgcode()==null)
                    sb.append("S_TAXORGCODE 不能为空; ");
               if (this.getStaxorgcode()!=null)
                    if (this.getStaxorgcode().getBytes().length > 12)
                        sb.append("S_TAXORGCODE 宽度不能超过 "+12+"个字符");
             break;
         }
  }
    
    //check field S_TAXORGNAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXORGNAME")) {
                 if (this.getStaxorgname()!=null)
                    if (this.getStaxorgname().getBytes().length > 100)
                        sb.append("S_TAXORGNAME 宽度不能超过 "+100+"个字符");
             break;
         }
  }
    
    //check field S_PAYERACCT
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_PAYERACCT")) {
               if (this.getSpayeracct()==null)
                    sb.append("S_PAYERACCT 不能为空; ");
               if (this.getSpayeracct()!=null)
                    if (this.getSpayeracct().getBytes().length > 32)
                        sb.append("S_PAYERACCT 宽度不能超过 "+32+"个字符");
             break;
         }
  }
    
    //check field S_PAYERNAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_PAYERNAME")) {
                 if (this.getSpayername()!=null)
                    if (this.getSpayername().getBytes().length > 60)
                        sb.append("S_PAYERNAME 宽度不能超过 "+60+"个字符");
             break;
         }
  }
    
    //check field S_TAXSUBCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXSUBCODE")) {
               if (this.getStaxsubcode()==null)
                    sb.append("S_TAXSUBCODE 不能为空; ");
               if (this.getStaxsubcode()!=null)
                    if (this.getStaxsubcode().getBytes().length > 30)
                        sb.append("S_TAXSUBCODE 宽度不能超过 "+30+"个字符");
             break;
         }
  }
    
    //check field S_TAXSUBNAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXSUBNAME")) {
               if (this.getStaxsubname()==null)
                    sb.append("S_TAXSUBNAME 不能为空; ");
               if (this.getStaxsubname()!=null)
                    if (this.getStaxsubname().getBytes().length > 60)
                        sb.append("S_TAXSUBNAME 宽度不能超过 "+60+"个字符");
             break;
         }
  }
    
    //don't need check field TS_SYSUPDATE,it is UpdateTimeStamp column
  
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
      TsTaxpayacctPK pk = new TsTaxpayacctPK();
      pk.setSorgcode(getSorgcode());
      pk.setStaxorgcode(getStaxorgcode());
      pk.setSpayeracct(getSpayeracct());
      return pk;
    };
}

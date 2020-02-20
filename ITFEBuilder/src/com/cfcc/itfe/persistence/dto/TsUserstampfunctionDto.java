    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TsUserstampfunctionPK;
/**
 * <p>Title: DTO of table: TS_USERSTAMPFUNCTION</p>
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

public class TsUserstampfunctionDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  S_ORGCODE VARCHAR , PK   , NOT NULL       */
    protected String sorgcode;
    /**
    *  S_USERCODE VARCHAR , PK   , NOT NULL       */
    protected String susercode;
    /**
    *  S_STAMPTYPECODE VARCHAR , PK   , NOT NULL       */
    protected String sstamptypecode;
    /**
    *  S_MODELID VARCHAR , PK   , NOT NULL       */
    protected String smodelid;
    /**
    *  S_PLACEID VARCHAR , PK   , NOT NULL       */
    protected String splaceid;
    /**
    *  I_MODICOUNT INTEGER         */
    protected Integer imodicount;


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


    public String getSusercode()
    {
        return susercode;
    }
     /**
     *   Setter S_USERCODE, PK , NOT NULL        * */
    public void setSusercode(String _susercode) {
        this.susercode = _susercode;
    }


    public String getSstamptypecode()
    {
        return sstamptypecode;
    }
     /**
     *   Setter S_STAMPTYPECODE, PK , NOT NULL        * */
    public void setSstamptypecode(String _sstamptypecode) {
        this.sstamptypecode = _sstamptypecode;
    }


    public String getSmodelid()
    {
        return smodelid;
    }
     /**
     *   Setter S_MODELID, PK , NOT NULL        * */
    public void setSmodelid(String _smodelid) {
        this.smodelid = _smodelid;
    }


    public String getSplaceid()
    {
        return splaceid;
    }
     /**
     *   Setter S_PLACEID, PK , NOT NULL        * */
    public void setSplaceid(String _splaceid) {
        this.splaceid = _splaceid;
    }


    public Integer getImodicount()
    {
        return imodicount;
    }
     /**
     *   Setter I_MODICOUNT        * */
    public void setImodicount(Integer _imodicount) {
        this.imodicount = _imodicount;
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
    *   Getter S_USERCODE, PK , NOT NULL       * */
    public static String  columnSusercode()
    {
        return "S_USERCODE";
    }
   
    /**
    *   Getter S_STAMPTYPECODE, PK , NOT NULL       * */
    public static String  columnSstamptypecode()
    {
        return "S_STAMPTYPECODE";
    }
   
    /**
    *   Getter S_MODELID, PK , NOT NULL       * */
    public static String  columnSmodelid()
    {
        return "S_MODELID";
    }
   
    /**
    *   Getter S_PLACEID, PK , NOT NULL       * */
    public static String  columnSplaceid()
    {
        return "S_PLACEID";
    }
   
    /**
    *   Getter I_MODICOUNT       * */
    public static String  columnImodicount()
    {
        return "I_MODICOUNT";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "TS_USERSTAMPFUNCTION";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[6];        
        columnNames[0]="S_ORGCODE";
        columnNames[1]="S_USERCODE";
        columnNames[2]="S_STAMPTYPECODE";
        columnNames[3]="S_MODELID";
        columnNames[4]="S_PLACEID";
        columnNames[5]="I_MODICOUNT";
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

        if (obj == null || !(obj instanceof TsUserstampfunctionDto))
            return false;

        TsUserstampfunctionDto bean = (TsUserstampfunctionDto) obj;


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
      //compare field imodicount
      if((this.imodicount==null && bean.imodicount!=null) || (this.imodicount!=null && bean.imodicount==null))
          return false;
        else if(this.imodicount==null && bean.imodicount==null){
        }
        else{
          if(!bean.imodicount.equals(this.imodicount))
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
        if(this.susercode!=null)
          _hash_ = _hash_ * 31+ this.susercode.hashCode() ;
        if(this.sstamptypecode!=null)
          _hash_ = _hash_ * 31+ this.sstamptypecode.hashCode() ;
        if(this.smodelid!=null)
          _hash_ = _hash_ * 31+ this.smodelid.hashCode() ;
        if(this.splaceid!=null)
          _hash_ = _hash_ * 31+ this.splaceid.hashCode() ;
        if(this.imodicount!=null)
          _hash_ = _hash_ * 31+ this.imodicount.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsUserstampfunctionDto bean = new TsUserstampfunctionDto();

          bean.sorgcode = this.sorgcode;

          bean.susercode = this.susercode;

          bean.sstamptypecode = this.sstamptypecode;

          bean.smodelid = this.smodelid;

          bean.splaceid = this.splaceid;

          if (this.imodicount != null)
            bean.imodicount = new Integer(this.imodicount.toString());
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsUserstampfunctionDto").append(sep);
        sb.append("[sorgcode]").append(" = ").append(sorgcode).append(sep);
        sb.append("[susercode]").append(" = ").append(susercode).append(sep);
        sb.append("[sstamptypecode]").append(" = ").append(sstamptypecode).append(sep);
        sb.append("[smodelid]").append(" = ").append(smodelid).append(sep);
        sb.append("[splaceid]").append(" = ").append(splaceid).append(sep);
        sb.append("[imodicount]").append(" = ").append(imodicount).append(sep);
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
    
    //check field S_USERCODE
      if (this.getSusercode()==null)
             sb.append("S_USERCODE不能为空; ");
      if (this.getSusercode()!=null)
             if (this.getSusercode().getBytes().length > 30)
                sb.append("S_USERCODE宽度不能超过 "+30+"个字符; ");
    
    //check field S_STAMPTYPECODE
      if (this.getSstamptypecode()==null)
             sb.append("S_STAMPTYPECODE不能为空; ");
      if (this.getSstamptypecode()!=null)
             if (this.getSstamptypecode().getBytes().length > 10)
                sb.append("S_STAMPTYPECODE宽度不能超过 "+10+"个字符; ");
    
    //check field S_MODELID
      if (this.getSmodelid()==null)
             sb.append("S_MODELID不能为空; ");
      if (this.getSmodelid()!=null)
             if (this.getSmodelid().getBytes().length > 30)
                sb.append("S_MODELID宽度不能超过 "+30+"个字符; ");
    
    //check field S_PLACEID
      if (this.getSplaceid()==null)
             sb.append("S_PLACEID不能为空; ");
      if (this.getSplaceid()!=null)
             if (this.getSplaceid().getBytes().length > 30)
                sb.append("S_PLACEID宽度不能超过 "+30+"个字符; ");
    
    //check field I_MODICOUNT
      

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
    
    //check field S_USERCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_USERCODE")) {
               if (this.getSusercode()==null)
                    sb.append("S_USERCODE 不能为空; ");
               if (this.getSusercode()!=null)
                    if (this.getSusercode().getBytes().length > 30)
                        sb.append("S_USERCODE 宽度不能超过 "+30+"个字符");
             break;
         }
  }
    
    //check field S_STAMPTYPECODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_STAMPTYPECODE")) {
               if (this.getSstamptypecode()==null)
                    sb.append("S_STAMPTYPECODE 不能为空; ");
               if (this.getSstamptypecode()!=null)
                    if (this.getSstamptypecode().getBytes().length > 10)
                        sb.append("S_STAMPTYPECODE 宽度不能超过 "+10+"个字符");
             break;
         }
  }
    
    //check field S_MODELID
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_MODELID")) {
               if (this.getSmodelid()==null)
                    sb.append("S_MODELID 不能为空; ");
               if (this.getSmodelid()!=null)
                    if (this.getSmodelid().getBytes().length > 30)
                        sb.append("S_MODELID 宽度不能超过 "+30+"个字符");
             break;
         }
  }
    
    //check field S_PLACEID
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_PLACEID")) {
               if (this.getSplaceid()==null)
                    sb.append("S_PLACEID 不能为空; ");
               if (this.getSplaceid()!=null)
                    if (this.getSplaceid().getBytes().length > 30)
                        sb.append("S_PLACEID 宽度不能超过 "+30+"个字符");
             break;
         }
  }
    
    //check field I_MODICOUNT
          
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
      TsUserstampfunctionPK pk = new TsUserstampfunctionPK();
      pk.setSorgcode(getSorgcode());
      pk.setSusercode(getSusercode());
      pk.setSstamptypecode(getSstamptypecode());
      pk.setSmodelid(getSmodelid());
      pk.setSplaceid(getSplaceid());
      return pk;
    };
}

    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TsSysbatchPK;
/**
 * <p>Title: DTO of table: TS_SYSBATCH</p>
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

public class TsSysbatchDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  S_ORGCODE VARCHAR , PK   , NOT NULL       */
    protected String sorgcode;
    /**
    *  S_DATE CHARACTER , PK   , NOT NULL       */
    protected String sdate;
    /**
    *  S_OPERATIONTYPECODE VARCHAR , PK   , NOT NULL       */
    protected String soperationtypecode;
    /**
    *  I_COUNT INTEGER         */
    protected Integer icount;


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


    public String getSdate()
    {
        return sdate;
    }
     /**
     *   Setter S_DATE, PK , NOT NULL        * */
    public void setSdate(String _sdate) {
        this.sdate = _sdate;
    }


    public String getSoperationtypecode()
    {
        return soperationtypecode;
    }
     /**
     *   Setter S_OPERATIONTYPECODE, PK , NOT NULL        * */
    public void setSoperationtypecode(String _soperationtypecode) {
        this.soperationtypecode = _soperationtypecode;
    }


    public Integer getIcount()
    {
        return icount;
    }
     /**
     *   Setter I_COUNT        * */
    public void setIcount(Integer _icount) {
        this.icount = _icount;
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
    *   Getter S_DATE, PK , NOT NULL       * */
    public static String  columnSdate()
    {
        return "S_DATE";
    }
   
    /**
    *   Getter S_OPERATIONTYPECODE, PK , NOT NULL       * */
    public static String  columnSoperationtypecode()
    {
        return "S_OPERATIONTYPECODE";
    }
   
    /**
    *   Getter I_COUNT       * */
    public static String  columnIcount()
    {
        return "I_COUNT";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "TS_SYSBATCH";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[4];        
        columnNames[0]="S_ORGCODE";
        columnNames[1]="S_DATE";
        columnNames[2]="S_OPERATIONTYPECODE";
        columnNames[3]="I_COUNT";
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

        if (obj == null || !(obj instanceof TsSysbatchDto))
            return false;

        TsSysbatchDto bean = (TsSysbatchDto) obj;


      //compare field sorgcode
      if((this.sorgcode==null && bean.sorgcode!=null) || (this.sorgcode!=null && bean.sorgcode==null))
          return false;
        else if(this.sorgcode==null && bean.sorgcode==null){
        }
        else{
          if(!bean.sorgcode.equals(this.sorgcode))
               return false;
     }
      //compare field sdate
      if((this.sdate==null && bean.sdate!=null) || (this.sdate!=null && bean.sdate==null))
          return false;
        else if(this.sdate==null && bean.sdate==null){
        }
        else{
          if(!bean.sdate.equals(this.sdate))
               return false;
     }
      //compare field soperationtypecode
      if((this.soperationtypecode==null && bean.soperationtypecode!=null) || (this.soperationtypecode!=null && bean.soperationtypecode==null))
          return false;
        else if(this.soperationtypecode==null && bean.soperationtypecode==null){
        }
        else{
          if(!bean.soperationtypecode.equals(this.soperationtypecode))
               return false;
     }
      //compare field icount
      if((this.icount==null && bean.icount!=null) || (this.icount!=null && bean.icount==null))
          return false;
        else if(this.icount==null && bean.icount==null){
        }
        else{
          if(!bean.icount.equals(this.icount))
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
        if(this.sdate!=null)
          _hash_ = _hash_ * 31+ this.sdate.hashCode() ;
        if(this.soperationtypecode!=null)
          _hash_ = _hash_ * 31+ this.soperationtypecode.hashCode() ;
        if(this.icount!=null)
          _hash_ = _hash_ * 31+ this.icount.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsSysbatchDto bean = new TsSysbatchDto();

          bean.sorgcode = this.sorgcode;

          bean.sdate = this.sdate;

          bean.soperationtypecode = this.soperationtypecode;

          if (this.icount != null)
            bean.icount = new Integer(this.icount.toString());
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsSysbatchDto").append(sep);
        sb.append("[sorgcode]").append(" = ").append(sorgcode).append(sep);
        sb.append("[sdate]").append(" = ").append(sdate).append(sep);
        sb.append("[soperationtypecode]").append(" = ").append(soperationtypecode).append(sep);
        sb.append("[icount]").append(" = ").append(icount).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //check field S_ORGCODE
      if (this.getSorgcode()==null)
             sb.append("S_ORGCODE����Ϊ��; ");
      if (this.getSorgcode()!=null)
             if (this.getSorgcode().getBytes().length > 12)
                sb.append("S_ORGCODE���Ȳ��ܳ��� "+12+"���ַ�; ");
    
    //check field S_DATE
      if (this.getSdate()==null)
             sb.append("S_DATE����Ϊ��; ");
      if (this.getSdate()!=null)
             if (this.getSdate().getBytes().length > 8)
                sb.append("S_DATE���Ȳ��ܳ��� "+8+"���ַ�; ");
    
    //check field S_OPERATIONTYPECODE
      if (this.getSoperationtypecode()==null)
             sb.append("S_OPERATIONTYPECODE����Ϊ��; ");
      if (this.getSoperationtypecode()!=null)
             if (this.getSoperationtypecode().getBytes().length > 10)
                sb.append("S_OPERATIONTYPECODE���Ȳ��ܳ��� "+10+"���ַ�; ");
    
    //check field I_COUNT
      

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
                    sb.append("S_ORGCODE ����Ϊ��; ");
               if (this.getSorgcode()!=null)
                    if (this.getSorgcode().getBytes().length > 12)
                        sb.append("S_ORGCODE ���Ȳ��ܳ��� "+12+"���ַ�");
             break;
         }
  }
    
    //check field S_DATE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_DATE")) {
               if (this.getSdate()==null)
                    sb.append("S_DATE ����Ϊ��; ");
               if (this.getSdate()!=null)
                    if (this.getSdate().getBytes().length > 8)
                        sb.append("S_DATE ���Ȳ��ܳ��� "+8+"���ַ�");
             break;
         }
  }
    
    //check field S_OPERATIONTYPECODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_OPERATIONTYPECODE")) {
               if (this.getSoperationtypecode()==null)
                    sb.append("S_OPERATIONTYPECODE ����Ϊ��; ");
               if (this.getSoperationtypecode()!=null)
                    if (this.getSoperationtypecode().getBytes().length > 10)
                        sb.append("S_OPERATIONTYPECODE ���Ȳ��ܳ��� "+10+"���ַ�");
             break;
         }
  }
    
    //check field I_COUNT
          
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
			return "�����ֶθ������ڱ����ֶθ���; ";
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
				sb.append("�����ֶ� " + _columnNames[i] + " ���ڸñ��ֶ���; ");
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
     throw new RuntimeException("��dtoû�����������dto�����ܽ��д˲���");
  };
  
  /* return the IPK class  */
    public IPK      getPK(){
      TsSysbatchPK pk = new TsSysbatchPK();
      pk.setSorgcode(getSorgcode());
      pk.setSdate(getSdate());
      pk.setSoperationtypecode(getSoperationtypecode());
      return pk;
    };
}
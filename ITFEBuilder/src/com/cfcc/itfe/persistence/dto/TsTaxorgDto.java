    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TsTaxorgPK;
/**
 * <p>Title: DTO of table: TS_TAXORG</p>
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

public class TsTaxorgDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  S_ORGCODE VARCHAR , PK   , NOT NULL       */
    protected String sorgcode;
    /**
    *  S_TRECODE CHARACTER , PK   , NOT NULL       */
    protected String strecode;
    /**
    *  S_TAXORGCODE VARCHAR , PK   , NOT NULL       */
    protected String staxorgcode;
    /**
    *  S_TAXORGNAME VARCHAR         */
    protected String staxorgname;
    /**
    *  S_TAXPROP CHARACTER         */
    protected String staxprop;


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
     *   Setter S_TRECODE, PK , NOT NULL        * */
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


    public String getStaxprop()
    {
        return staxprop;
    }
     /**
     *   Setter S_TAXPROP        * */
    public void setStaxprop(String _staxprop) {
        this.staxprop = _staxprop;
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
    *   Getter S_TRECODE, PK , NOT NULL       * */
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
    *   Getter S_TAXPROP       * */
    public static String  columnStaxprop()
    {
        return "S_TAXPROP";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "TS_TAXORG";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[5];        
        columnNames[0]="S_ORGCODE";
        columnNames[1]="S_TRECODE";
        columnNames[2]="S_TAXORGCODE";
        columnNames[3]="S_TAXORGNAME";
        columnNames[4]="S_TAXPROP";
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

        if (obj == null || !(obj instanceof TsTaxorgDto))
            return false;

        TsTaxorgDto bean = (TsTaxorgDto) obj;


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
      //compare field staxprop
      if((this.staxprop==null && bean.staxprop!=null) || (this.staxprop!=null && bean.staxprop==null))
          return false;
        else if(this.staxprop==null && bean.staxprop==null){
        }
        else{
          if(!bean.staxprop.equals(this.staxprop))
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
        if(this.staxprop!=null)
          _hash_ = _hash_ * 31+ this.staxprop.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TsTaxorgDto bean = new TsTaxorgDto();

          bean.sorgcode = this.sorgcode;

          bean.strecode = this.strecode;

          bean.staxorgcode = this.staxorgcode;

          if (this.staxorgname != null)
            bean.staxorgname = new String(this.staxorgname);
          if (this.staxprop != null)
            bean.staxprop = new String(this.staxprop);
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TsTaxorgDto").append(sep);
        sb.append("[sorgcode]").append(" = ").append(sorgcode).append(sep);
        sb.append("[strecode]").append(" = ").append(strecode).append(sep);
        sb.append("[staxorgcode]").append(" = ").append(staxorgcode).append(sep);
        sb.append("[staxorgname]").append(" = ").append(staxorgname).append(sep);
        sb.append("[staxprop]").append(" = ").append(staxprop).append(sep);
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
    
    //check field S_TRECODE
      if (this.getStrecode()==null)
             sb.append("S_TRECODE����Ϊ��; ");
      if (this.getStrecode()!=null)
             if (this.getStrecode().getBytes().length > 10)
                sb.append("S_TRECODE���Ȳ��ܳ��� "+10+"���ַ�; ");
    
    //check field S_TAXORGCODE
      if (this.getStaxorgcode()==null)
             sb.append("S_TAXORGCODE����Ϊ��; ");
      if (this.getStaxorgcode()!=null)
             if (this.getStaxorgcode().getBytes().length > 30)
                sb.append("S_TAXORGCODE���Ȳ��ܳ��� "+30+"���ַ�; ");
    
    //check field S_TAXORGNAME
      if (this.getStaxorgname()!=null)
             if (this.getStaxorgname().getBytes().length > 50)
                sb.append("S_TAXORGNAME���Ȳ��ܳ��� "+50+"���ַ�; ");
    
    //check field S_TAXPROP
      if (this.getStaxprop()!=null)
             if (this.getStaxprop().getBytes().length > 12)
                sb.append("S_TAXPROP���Ȳ��ܳ��� "+12+"���ַ�; ");
    

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
    
    //check field S_TRECODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TRECODE")) {
               if (this.getStrecode()==null)
                    sb.append("S_TRECODE ����Ϊ��; ");
               if (this.getStrecode()!=null)
                    if (this.getStrecode().getBytes().length > 10)
                        sb.append("S_TRECODE ���Ȳ��ܳ��� "+10+"���ַ�");
             break;
         }
  }
    
    //check field S_TAXORGCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXORGCODE")) {
               if (this.getStaxorgcode()==null)
                    sb.append("S_TAXORGCODE ����Ϊ��; ");
               if (this.getStaxorgcode()!=null)
                    if (this.getStaxorgcode().getBytes().length > 30)
                        sb.append("S_TAXORGCODE ���Ȳ��ܳ��� "+30+"���ַ�");
             break;
         }
  }
    
    //check field S_TAXORGNAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXORGNAME")) {
                 if (this.getStaxorgname()!=null)
                    if (this.getStaxorgname().getBytes().length > 50)
                        sb.append("S_TAXORGNAME ���Ȳ��ܳ��� "+50+"���ַ�");
             break;
         }
  }
    
    //check field S_TAXPROP
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXPROP")) {
                 if (this.getStaxprop()!=null)
                    if (this.getStaxprop().getBytes().length > 12)
                        sb.append("S_TAXPROP ���Ȳ��ܳ��� "+12+"���ַ�");
             break;
         }
  }
    
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
      TsTaxorgPK pk = new TsTaxorgPK();
      pk.setSorgcode(getSorgcode());
      pk.setStrecode(getStrecode());
      pk.setStaxorgcode(getStaxorgcode());
      return pk;
    };
}
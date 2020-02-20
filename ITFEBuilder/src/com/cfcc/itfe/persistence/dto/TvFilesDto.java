    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TvFilesPK;
/**
 * <p>Title: DTO of table: TV_FILES</p>
 * <p>Description:  Data Transfer Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:02 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  dto.vm version timestamp: 2008-01-08 16:30:00 
 *
 * @author win7
 */

public class TvFilesDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  I_NO INTEGER , PK   , NOT NULL       */
    protected Integer ino;
    /**
    *  S_DATE CHARACTER   , NOT NULL       */
    protected String sdate;
    /**
    *  S_NO VARCHAR   , NOT NULL       */
    protected String sno;
    /**
    *  S_CONTENT VARCHAR         */
    protected String scontent;
    /**
    *  I_FILELENGTH INTEGER         */
    protected Integer ifilelength;
    /**
    *  S_SAVEPATH VARCHAR         */
    protected String ssavepath;


/******************************************************
*
*  getter and setter
*
*******************************************************/


    public Integer getIno()
    {
        return ino;
    }
     /**
     *   Setter I_NO, PK , NOT NULL        * */
    public void setIno(Integer _ino) {
        this.ino = _ino;
    }


    public String getSdate()
    {
        return sdate;
    }
     /**
     *   Setter S_DATE , NOT NULL        * */
    public void setSdate(String _sdate) {
        this.sdate = _sdate;
    }


    public String getSno()
    {
        return sno;
    }
     /**
     *   Setter S_NO , NOT NULL        * */
    public void setSno(String _sno) {
        this.sno = _sno;
    }


    public String getScontent()
    {
        return scontent;
    }
     /**
     *   Setter S_CONTENT        * */
    public void setScontent(String _scontent) {
        this.scontent = _scontent;
    }


    public Integer getIfilelength()
    {
        return ifilelength;
    }
     /**
     *   Setter I_FILELENGTH        * */
    public void setIfilelength(Integer _ifilelength) {
        this.ifilelength = _ifilelength;
    }


    public String getSsavepath()
    {
        return ssavepath;
    }
     /**
     *   Setter S_SAVEPATH        * */
    public void setSsavepath(String _ssavepath) {
        this.ssavepath = _ssavepath;
    }




/******************************************************
*
*  Get Column Name
*
*******************************************************/
    /**
    *   Getter I_NO, PK , NOT NULL       * */
    public static String  columnIno()
    {
        return "I_NO";
    }
   
    /**
    *   Getter S_DATE , NOT NULL       * */
    public static String  columnSdate()
    {
        return "S_DATE";
    }
   
    /**
    *   Getter S_NO , NOT NULL       * */
    public static String  columnSno()
    {
        return "S_NO";
    }
   
    /**
    *   Getter S_CONTENT       * */
    public static String  columnScontent()
    {
        return "S_CONTENT";
    }
   
    /**
    *   Getter I_FILELENGTH       * */
    public static String  columnIfilelength()
    {
        return "I_FILELENGTH";
    }
   
    /**
    *   Getter S_SAVEPATH       * */
    public static String  columnSsavepath()
    {
        return "S_SAVEPATH";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "TV_FILES";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[6];        
        columnNames[0]="I_NO";
        columnNames[1]="S_DATE";
        columnNames[2]="S_NO";
        columnNames[3]="S_CONTENT";
        columnNames[4]="I_FILELENGTH";
        columnNames[5]="S_SAVEPATH";
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

        if (obj == null || !(obj instanceof TvFilesDto))
            return false;

        TvFilesDto bean = (TvFilesDto) obj;


      //compare field ino
      if((this.ino==null && bean.ino!=null) || (this.ino!=null && bean.ino==null))
          return false;
        else if(this.ino==null && bean.ino==null){
        }
        else{
          if(!bean.ino.equals(this.ino))
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
      //compare field sno
      if((this.sno==null && bean.sno!=null) || (this.sno!=null && bean.sno==null))
          return false;
        else if(this.sno==null && bean.sno==null){
        }
        else{
          if(!bean.sno.equals(this.sno))
               return false;
     }
      //compare field scontent
      if((this.scontent==null && bean.scontent!=null) || (this.scontent!=null && bean.scontent==null))
          return false;
        else if(this.scontent==null && bean.scontent==null){
        }
        else{
          if(!bean.scontent.equals(this.scontent))
               return false;
     }
      //compare field ifilelength
      if((this.ifilelength==null && bean.ifilelength!=null) || (this.ifilelength!=null && bean.ifilelength==null))
          return false;
        else if(this.ifilelength==null && bean.ifilelength==null){
        }
        else{
          if(!bean.ifilelength.equals(this.ifilelength))
               return false;
     }
      //compare field ssavepath
      if((this.ssavepath==null && bean.ssavepath!=null) || (this.ssavepath!=null && bean.ssavepath==null))
          return false;
        else if(this.ssavepath==null && bean.ssavepath==null){
        }
        else{
          if(!bean.ssavepath.equals(this.ssavepath))
               return false;
     }



        return true;
    }

    /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
  
		int _hash_ = 1;
		
        if(this.ino!=null)
          _hash_ = _hash_ * 31+ this.ino.hashCode() ;
        if(this.sdate!=null)
          _hash_ = _hash_ * 31+ this.sdate.hashCode() ;
        if(this.sno!=null)
          _hash_ = _hash_ * 31+ this.sno.hashCode() ;
        if(this.scontent!=null)
          _hash_ = _hash_ * 31+ this.scontent.hashCode() ;
        if(this.ifilelength!=null)
          _hash_ = _hash_ * 31+ this.ifilelength.hashCode() ;
        if(this.ssavepath!=null)
          _hash_ = _hash_ * 31+ this.ssavepath.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TvFilesDto bean = new TvFilesDto();

          bean.ino = this.ino;

          if (this.sdate != null)
            bean.sdate = new String(this.sdate);
          if (this.sno != null)
            bean.sno = new String(this.sno);
          if (this.scontent != null)
            bean.scontent = new String(this.scontent);
          if (this.ifilelength != null)
            bean.ifilelength = new Integer(this.ifilelength.toString());
          if (this.ssavepath != null)
            bean.ssavepath = new String(this.ssavepath);
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TvFilesDto").append(sep);
        sb.append("[ino]").append(" = ").append(ino).append(sep);
        sb.append("[sdate]").append(" = ").append(sdate).append(sep);
        sb.append("[sno]").append(" = ").append(sno).append(sep);
        sb.append("[scontent]").append(" = ").append(scontent).append(sep);
        sb.append("[ifilelength]").append(" = ").append(ifilelength).append(sep);
        sb.append("[ssavepath]").append(" = ").append(ssavepath).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //check field I_NO
      if (this.getIno()==null)
             sb.append("I_NO不能为空; ");
      
    //check field S_DATE
      if (this.getSdate()==null)
             sb.append("S_DATE不能为空; ");
      if (this.getSdate()!=null)
             if (this.getSdate().getBytes().length > 8)
                sb.append("S_DATE宽度不能超过 "+8+"个字符; ");
    
    //check field S_NO
      if (this.getSno()==null)
             sb.append("S_NO不能为空; ");
      if (this.getSno()!=null)
             if (this.getSno().getBytes().length > 20)
                sb.append("S_NO宽度不能超过 "+20+"个字符; ");
    
    //check field S_CONTENT
      if (this.getScontent()!=null)
             if (this.getScontent().getBytes().length > 250)
                sb.append("S_CONTENT宽度不能超过 "+250+"个字符; ");
    
    //check field I_FILELENGTH
      
    //check field S_SAVEPATH
      if (this.getSsavepath()!=null)
             if (this.getSsavepath().getBytes().length > 200)
                sb.append("S_SAVEPATH宽度不能超过 "+200+"个字符; ");
    

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
    //check field I_NO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("I_NO")) {
               if (this.getIno()==null)
                    sb.append("I_NO 不能为空; ");
               break;
         }
  }
    
    //check field S_DATE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_DATE")) {
               if (this.getSdate()==null)
                    sb.append("S_DATE 不能为空; ");
               if (this.getSdate()!=null)
                    if (this.getSdate().getBytes().length > 8)
                        sb.append("S_DATE 宽度不能超过 "+8+"个字符");
             break;
         }
  }
    
    //check field S_NO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_NO")) {
               if (this.getSno()==null)
                    sb.append("S_NO 不能为空; ");
               if (this.getSno()!=null)
                    if (this.getSno().getBytes().length > 20)
                        sb.append("S_NO 宽度不能超过 "+20+"个字符");
             break;
         }
  }
    
    //check field S_CONTENT
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_CONTENT")) {
                 if (this.getScontent()!=null)
                    if (this.getScontent().getBytes().length > 250)
                        sb.append("S_CONTENT 宽度不能超过 "+250+"个字符");
             break;
         }
  }
    
    //check field I_FILELENGTH
          
    //check field S_SAVEPATH
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_SAVEPATH")) {
                 if (this.getSsavepath()!=null)
                    if (this.getSsavepath().getBytes().length > 200)
                        sb.append("S_SAVEPATH 宽度不能超过 "+200+"个字符");
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
      TvFilesPK pk = new TvFilesPK();
      pk.setIno(getIno());
      return pk;
    };
}

    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TdDownloadReportCheckPK;
/**
 * <p>Title: DTO of table: TD_DOWNLOAD_REPORT_CHECK</p>
 * <p>Description:  Data Transfer Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:57 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  dto.vm version timestamp: 2008-01-08 16:30:00 
 *
 * @author win7
 */

public class TdDownloadReportCheckDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  S_DATES VARCHAR , PK   , NOT NULL       */
    protected String sdates;
    /**
    *  S_TRECODE VARCHAR , PK   , NOT NULL       */
    protected String strecode;
    /**
    *  S_SHUIPIAO CHARACTER         */
    protected String sshuipiao;
    /**
    *  S_LIUSHUI CHARACTER         */
    protected String sliushui;
    /**
    *  S_RIBAO CHARACTER         */
    protected String sribao;
    /**
    *  S_KUCUN CHARACTER         */
    protected String skucun;
    /**
    *  S_ZHICHU CHARACTER         */
    protected String szhichu;
    /**
    *  S_EXT1 VARCHAR         */
    protected String sext1;
    /**
    *  S_EXT2 VARCHAR         */
    protected String sext2;
    /**
    *  S_EXT3 VARCHAR         */
    protected String sext3;


/******************************************************
*
*  getter and setter
*
*******************************************************/


    public String getSdates()
    {
        return sdates;
    }
     /**
     *   Setter S_DATES, PK , NOT NULL        * */
    public void setSdates(String _sdates) {
        this.sdates = _sdates;
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


    public String getSshuipiao()
    {
        return sshuipiao;
    }
     /**
     *   Setter S_SHUIPIAO        * */
    public void setSshuipiao(String _sshuipiao) {
        this.sshuipiao = _sshuipiao;
    }


    public String getSliushui()
    {
        return sliushui;
    }
     /**
     *   Setter S_LIUSHUI        * */
    public void setSliushui(String _sliushui) {
        this.sliushui = _sliushui;
    }


    public String getSribao()
    {
        return sribao;
    }
     /**
     *   Setter S_RIBAO        * */
    public void setSribao(String _sribao) {
        this.sribao = _sribao;
    }


    public String getSkucun()
    {
        return skucun;
    }
     /**
     *   Setter S_KUCUN        * */
    public void setSkucun(String _skucun) {
        this.skucun = _skucun;
    }


    public String getSzhichu()
    {
        return szhichu;
    }
     /**
     *   Setter S_ZHICHU        * */
    public void setSzhichu(String _szhichu) {
        this.szhichu = _szhichu;
    }


    public String getSext1()
    {
        return sext1;
    }
     /**
     *   Setter S_EXT1        * */
    public void setSext1(String _sext1) {
        this.sext1 = _sext1;
    }


    public String getSext2()
    {
        return sext2;
    }
     /**
     *   Setter S_EXT2        * */
    public void setSext2(String _sext2) {
        this.sext2 = _sext2;
    }


    public String getSext3()
    {
        return sext3;
    }
     /**
     *   Setter S_EXT3        * */
    public void setSext3(String _sext3) {
        this.sext3 = _sext3;
    }




/******************************************************
*
*  Get Column Name
*
*******************************************************/
    /**
    *   Getter S_DATES, PK , NOT NULL       * */
    public static String  columnSdates()
    {
        return "S_DATES";
    }
   
    /**
    *   Getter S_TRECODE, PK , NOT NULL       * */
    public static String  columnStrecode()
    {
        return "S_TRECODE";
    }
   
    /**
    *   Getter S_SHUIPIAO       * */
    public static String  columnSshuipiao()
    {
        return "S_SHUIPIAO";
    }
   
    /**
    *   Getter S_LIUSHUI       * */
    public static String  columnSliushui()
    {
        return "S_LIUSHUI";
    }
   
    /**
    *   Getter S_RIBAO       * */
    public static String  columnSribao()
    {
        return "S_RIBAO";
    }
   
    /**
    *   Getter S_KUCUN       * */
    public static String  columnSkucun()
    {
        return "S_KUCUN";
    }
   
    /**
    *   Getter S_ZHICHU       * */
    public static String  columnSzhichu()
    {
        return "S_ZHICHU";
    }
   
    /**
    *   Getter S_EXT1       * */
    public static String  columnSext1()
    {
        return "S_EXT1";
    }
   
    /**
    *   Getter S_EXT2       * */
    public static String  columnSext2()
    {
        return "S_EXT2";
    }
   
    /**
    *   Getter S_EXT3       * */
    public static String  columnSext3()
    {
        return "S_EXT3";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "TD_DOWNLOAD_REPORT_CHECK";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[10];        
        columnNames[0]="S_DATES";
        columnNames[1]="S_TRECODE";
        columnNames[2]="S_SHUIPIAO";
        columnNames[3]="S_LIUSHUI";
        columnNames[4]="S_RIBAO";
        columnNames[5]="S_KUCUN";
        columnNames[6]="S_ZHICHU";
        columnNames[7]="S_EXT1";
        columnNames[8]="S_EXT2";
        columnNames[9]="S_EXT3";
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

        if (obj == null || !(obj instanceof TdDownloadReportCheckDto))
            return false;

        TdDownloadReportCheckDto bean = (TdDownloadReportCheckDto) obj;


      //compare field sdates
      if((this.sdates==null && bean.sdates!=null) || (this.sdates!=null && bean.sdates==null))
          return false;
        else if(this.sdates==null && bean.sdates==null){
        }
        else{
          if(!bean.sdates.equals(this.sdates))
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
      //compare field sshuipiao
      if((this.sshuipiao==null && bean.sshuipiao!=null) || (this.sshuipiao!=null && bean.sshuipiao==null))
          return false;
        else if(this.sshuipiao==null && bean.sshuipiao==null){
        }
        else{
          if(!bean.sshuipiao.equals(this.sshuipiao))
               return false;
     }
      //compare field sliushui
      if((this.sliushui==null && bean.sliushui!=null) || (this.sliushui!=null && bean.sliushui==null))
          return false;
        else if(this.sliushui==null && bean.sliushui==null){
        }
        else{
          if(!bean.sliushui.equals(this.sliushui))
               return false;
     }
      //compare field sribao
      if((this.sribao==null && bean.sribao!=null) || (this.sribao!=null && bean.sribao==null))
          return false;
        else if(this.sribao==null && bean.sribao==null){
        }
        else{
          if(!bean.sribao.equals(this.sribao))
               return false;
     }
      //compare field skucun
      if((this.skucun==null && bean.skucun!=null) || (this.skucun!=null && bean.skucun==null))
          return false;
        else if(this.skucun==null && bean.skucun==null){
        }
        else{
          if(!bean.skucun.equals(this.skucun))
               return false;
     }
      //compare field szhichu
      if((this.szhichu==null && bean.szhichu!=null) || (this.szhichu!=null && bean.szhichu==null))
          return false;
        else if(this.szhichu==null && bean.szhichu==null){
        }
        else{
          if(!bean.szhichu.equals(this.szhichu))
               return false;
     }
      //compare field sext1
      if((this.sext1==null && bean.sext1!=null) || (this.sext1!=null && bean.sext1==null))
          return false;
        else if(this.sext1==null && bean.sext1==null){
        }
        else{
          if(!bean.sext1.equals(this.sext1))
               return false;
     }
      //compare field sext2
      if((this.sext2==null && bean.sext2!=null) || (this.sext2!=null && bean.sext2==null))
          return false;
        else if(this.sext2==null && bean.sext2==null){
        }
        else{
          if(!bean.sext2.equals(this.sext2))
               return false;
     }
      //compare field sext3
      if((this.sext3==null && bean.sext3!=null) || (this.sext3!=null && bean.sext3==null))
          return false;
        else if(this.sext3==null && bean.sext3==null){
        }
        else{
          if(!bean.sext3.equals(this.sext3))
               return false;
     }



        return true;
    }

    /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
  
		int _hash_ = 1;
		
        if(this.sdates!=null)
          _hash_ = _hash_ * 31+ this.sdates.hashCode() ;
        if(this.strecode!=null)
          _hash_ = _hash_ * 31+ this.strecode.hashCode() ;
        if(this.sshuipiao!=null)
          _hash_ = _hash_ * 31+ this.sshuipiao.hashCode() ;
        if(this.sliushui!=null)
          _hash_ = _hash_ * 31+ this.sliushui.hashCode() ;
        if(this.sribao!=null)
          _hash_ = _hash_ * 31+ this.sribao.hashCode() ;
        if(this.skucun!=null)
          _hash_ = _hash_ * 31+ this.skucun.hashCode() ;
        if(this.szhichu!=null)
          _hash_ = _hash_ * 31+ this.szhichu.hashCode() ;
        if(this.sext1!=null)
          _hash_ = _hash_ * 31+ this.sext1.hashCode() ;
        if(this.sext2!=null)
          _hash_ = _hash_ * 31+ this.sext2.hashCode() ;
        if(this.sext3!=null)
          _hash_ = _hash_ * 31+ this.sext3.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TdDownloadReportCheckDto bean = new TdDownloadReportCheckDto();

          bean.sdates = this.sdates;

          bean.strecode = this.strecode;

          if (this.sshuipiao != null)
            bean.sshuipiao = new String(this.sshuipiao);
          if (this.sliushui != null)
            bean.sliushui = new String(this.sliushui);
          if (this.sribao != null)
            bean.sribao = new String(this.sribao);
          if (this.skucun != null)
            bean.skucun = new String(this.skucun);
          if (this.szhichu != null)
            bean.szhichu = new String(this.szhichu);
          if (this.sext1 != null)
            bean.sext1 = new String(this.sext1);
          if (this.sext2 != null)
            bean.sext2 = new String(this.sext2);
          if (this.sext3 != null)
            bean.sext3 = new String(this.sext3);
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TdDownloadReportCheckDto").append(sep);
        sb.append("[sdates]").append(" = ").append(sdates).append(sep);
        sb.append("[strecode]").append(" = ").append(strecode).append(sep);
        sb.append("[sshuipiao]").append(" = ").append(sshuipiao).append(sep);
        sb.append("[sliushui]").append(" = ").append(sliushui).append(sep);
        sb.append("[sribao]").append(" = ").append(sribao).append(sep);
        sb.append("[skucun]").append(" = ").append(skucun).append(sep);
        sb.append("[szhichu]").append(" = ").append(szhichu).append(sep);
        sb.append("[sext1]").append(" = ").append(sext1).append(sep);
        sb.append("[sext2]").append(" = ").append(sext2).append(sep);
        sb.append("[sext3]").append(" = ").append(sext3).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //check field S_DATES
      if (this.getSdates()==null)
             sb.append("S_DATES����Ϊ��; ");
      if (this.getSdates()!=null)
             if (this.getSdates().getBytes().length > 12)
                sb.append("S_DATES���Ȳ��ܳ��� "+12+"���ַ�; ");
    
    //check field S_TRECODE
      if (this.getStrecode()==null)
             sb.append("S_TRECODE����Ϊ��; ");
      if (this.getStrecode()!=null)
             if (this.getStrecode().getBytes().length > 10)
                sb.append("S_TRECODE���Ȳ��ܳ��� "+10+"���ַ�; ");
    
    //check field S_SHUIPIAO
      if (this.getSshuipiao()!=null)
             if (this.getSshuipiao().getBytes().length > 1)
                sb.append("S_SHUIPIAO���Ȳ��ܳ��� "+1+"���ַ�; ");
    
    //check field S_LIUSHUI
      if (this.getSliushui()!=null)
             if (this.getSliushui().getBytes().length > 1)
                sb.append("S_LIUSHUI���Ȳ��ܳ��� "+1+"���ַ�; ");
    
    //check field S_RIBAO
      if (this.getSribao()!=null)
             if (this.getSribao().getBytes().length > 1)
                sb.append("S_RIBAO���Ȳ��ܳ��� "+1+"���ַ�; ");
    
    //check field S_KUCUN
      if (this.getSkucun()!=null)
             if (this.getSkucun().getBytes().length > 1)
                sb.append("S_KUCUN���Ȳ��ܳ��� "+1+"���ַ�; ");
    
    //check field S_ZHICHU
      if (this.getSzhichu()!=null)
             if (this.getSzhichu().getBytes().length > 1)
                sb.append("S_ZHICHU���Ȳ��ܳ��� "+1+"���ַ�; ");
    
    //check field S_EXT1
      if (this.getSext1()!=null)
             if (this.getSext1().getBytes().length > 30)
                sb.append("S_EXT1���Ȳ��ܳ��� "+30+"���ַ�; ");
    
    //check field S_EXT2
      if (this.getSext2()!=null)
             if (this.getSext2().getBytes().length > 50)
                sb.append("S_EXT2���Ȳ��ܳ��� "+50+"���ַ�; ");
    
    //check field S_EXT3
      if (this.getSext3()!=null)
             if (this.getSext3().getBytes().length > 100)
                sb.append("S_EXT3���Ȳ��ܳ��� "+100+"���ַ�; ");
    

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
    //check field S_DATES
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_DATES")) {
               if (this.getSdates()==null)
                    sb.append("S_DATES ����Ϊ��; ");
               if (this.getSdates()!=null)
                    if (this.getSdates().getBytes().length > 12)
                        sb.append("S_DATES ���Ȳ��ܳ��� "+12+"���ַ�");
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
    
    //check field S_SHUIPIAO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_SHUIPIAO")) {
                 if (this.getSshuipiao()!=null)
                    if (this.getSshuipiao().getBytes().length > 1)
                        sb.append("S_SHUIPIAO ���Ȳ��ܳ��� "+1+"���ַ�");
             break;
         }
  }
    
    //check field S_LIUSHUI
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_LIUSHUI")) {
                 if (this.getSliushui()!=null)
                    if (this.getSliushui().getBytes().length > 1)
                        sb.append("S_LIUSHUI ���Ȳ��ܳ��� "+1+"���ַ�");
             break;
         }
  }
    
    //check field S_RIBAO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_RIBAO")) {
                 if (this.getSribao()!=null)
                    if (this.getSribao().getBytes().length > 1)
                        sb.append("S_RIBAO ���Ȳ��ܳ��� "+1+"���ַ�");
             break;
         }
  }
    
    //check field S_KUCUN
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_KUCUN")) {
                 if (this.getSkucun()!=null)
                    if (this.getSkucun().getBytes().length > 1)
                        sb.append("S_KUCUN ���Ȳ��ܳ��� "+1+"���ַ�");
             break;
         }
  }
    
    //check field S_ZHICHU
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_ZHICHU")) {
                 if (this.getSzhichu()!=null)
                    if (this.getSzhichu().getBytes().length > 1)
                        sb.append("S_ZHICHU ���Ȳ��ܳ��� "+1+"���ַ�");
             break;
         }
  }
    
    //check field S_EXT1
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_EXT1")) {
                 if (this.getSext1()!=null)
                    if (this.getSext1().getBytes().length > 30)
                        sb.append("S_EXT1 ���Ȳ��ܳ��� "+30+"���ַ�");
             break;
         }
  }
    
    //check field S_EXT2
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_EXT2")) {
                 if (this.getSext2()!=null)
                    if (this.getSext2().getBytes().length > 50)
                        sb.append("S_EXT2 ���Ȳ��ܳ��� "+50+"���ַ�");
             break;
         }
  }
    
    //check field S_EXT3
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_EXT3")) {
                 if (this.getSext3()!=null)
                    if (this.getSext3().getBytes().length > 100)
                        sb.append("S_EXT3 ���Ȳ��ܳ��� "+100+"���ַ�");
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
      TdDownloadReportCheckPK pk = new TdDownloadReportCheckPK();
      pk.setSdates(getSdates());
      pk.setStrecode(getStrecode());
      return pk;
    };
}
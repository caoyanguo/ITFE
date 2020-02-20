    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.HtvTaxItemPK;
/**
 * <p>Title: DTO of table: HTV_TAX_ITEM</p>
 * <p>Description:  Data Transfer Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:56 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  dto.vm version timestamp: 2008-01-08 16:30:00 
 *
 * @author win7
 */

public class HtvTaxItemDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  S_SEQ VARCHAR , PK   , NOT NULL       */
    protected String sseq;
    /**
    *  I_PROJECTID INTEGER , PK   , NOT NULL       */
    protected Integer iprojectid;
    /**
    *  I_DETAILNO INTEGER , PK   , NOT NULL       */
    protected Integer idetailno;
    /**
    *  S_TAXSUBJECTCODE VARCHAR   , NOT NULL       */
    protected String staxsubjectcode;
    /**
    *  S_TAXSUBJECTNAME VARCHAR   , NOT NULL       */
    protected String staxsubjectname;
    /**
    *  I_TAXNUMBER INTEGER   , NOT NULL       */
    protected Integer itaxnumber;
    /**
    *  F_TAXAMT DECIMAL   , NOT NULL       */
    protected BigDecimal ftaxamt;
    /**
    *  F_TAXRATE DECIMAL   , NOT NULL       */
    protected BigDecimal ftaxrate;
    /**
    *  F_EXPTAXAMT DECIMAL   , NOT NULL       */
    protected BigDecimal fexptaxamt;
    /**
    *  F_DISCOUNTTAXAMT DECIMAL   , NOT NULL       */
    protected BigDecimal fdiscounttaxamt;
    /**
    *  F_REALAMT DECIMAL   , NOT NULL       */
    protected BigDecimal frealamt;
    /**
    *  TS_UPDATE TIMESTAMP   , NOT NULL       */
    protected Timestamp tsupdate;


/******************************************************
*
*  getter and setter
*
*******************************************************/


    public String getSseq()
    {
        return sseq;
    }
     /**
     *   Setter S_SEQ, PK , NOT NULL        * */
    public void setSseq(String _sseq) {
        this.sseq = _sseq;
    }


    public Integer getIprojectid()
    {
        return iprojectid;
    }
     /**
     *   Setter I_PROJECTID, PK , NOT NULL        * */
    public void setIprojectid(Integer _iprojectid) {
        this.iprojectid = _iprojectid;
    }


    public Integer getIdetailno()
    {
        return idetailno;
    }
     /**
     *   Setter I_DETAILNO, PK , NOT NULL        * */
    public void setIdetailno(Integer _idetailno) {
        this.idetailno = _idetailno;
    }


    public String getStaxsubjectcode()
    {
        return staxsubjectcode;
    }
     /**
     *   Setter S_TAXSUBJECTCODE , NOT NULL        * */
    public void setStaxsubjectcode(String _staxsubjectcode) {
        this.staxsubjectcode = _staxsubjectcode;
    }


    public String getStaxsubjectname()
    {
        return staxsubjectname;
    }
     /**
     *   Setter S_TAXSUBJECTNAME , NOT NULL        * */
    public void setStaxsubjectname(String _staxsubjectname) {
        this.staxsubjectname = _staxsubjectname;
    }


    public Integer getItaxnumber()
    {
        return itaxnumber;
    }
     /**
     *   Setter I_TAXNUMBER , NOT NULL        * */
    public void setItaxnumber(Integer _itaxnumber) {
        this.itaxnumber = _itaxnumber;
    }


    public BigDecimal getFtaxamt()
    {
        return ftaxamt;
    }
     /**
     *   Setter F_TAXAMT , NOT NULL        * */
    public void setFtaxamt(BigDecimal _ftaxamt) {
        this.ftaxamt = _ftaxamt;
    }


    public BigDecimal getFtaxrate()
    {
        return ftaxrate;
    }
     /**
     *   Setter F_TAXRATE , NOT NULL        * */
    public void setFtaxrate(BigDecimal _ftaxrate) {
        this.ftaxrate = _ftaxrate;
    }


    public BigDecimal getFexptaxamt()
    {
        return fexptaxamt;
    }
     /**
     *   Setter F_EXPTAXAMT , NOT NULL        * */
    public void setFexptaxamt(BigDecimal _fexptaxamt) {
        this.fexptaxamt = _fexptaxamt;
    }


    public BigDecimal getFdiscounttaxamt()
    {
        return fdiscounttaxamt;
    }
     /**
     *   Setter F_DISCOUNTTAXAMT , NOT NULL        * */
    public void setFdiscounttaxamt(BigDecimal _fdiscounttaxamt) {
        this.fdiscounttaxamt = _fdiscounttaxamt;
    }


    public BigDecimal getFrealamt()
    {
        return frealamt;
    }
     /**
     *   Setter F_REALAMT , NOT NULL        * */
    public void setFrealamt(BigDecimal _frealamt) {
        this.frealamt = _frealamt;
    }


    public Timestamp getTsupdate()
    {
        return tsupdate;
    }
     /**
     *   Setter TS_UPDATE , NOT NULL        * */
    public void setTsupdate(Timestamp _tsupdate) {
        this.tsupdate = _tsupdate;
    }




/******************************************************
*
*  Get Column Name
*
*******************************************************/
    /**
    *   Getter S_SEQ, PK , NOT NULL       * */
    public static String  columnSseq()
    {
        return "S_SEQ";
    }
   
    /**
    *   Getter I_PROJECTID, PK , NOT NULL       * */
    public static String  columnIprojectid()
    {
        return "I_PROJECTID";
    }
   
    /**
    *   Getter I_DETAILNO, PK , NOT NULL       * */
    public static String  columnIdetailno()
    {
        return "I_DETAILNO";
    }
   
    /**
    *   Getter S_TAXSUBJECTCODE , NOT NULL       * */
    public static String  columnStaxsubjectcode()
    {
        return "S_TAXSUBJECTCODE";
    }
   
    /**
    *   Getter S_TAXSUBJECTNAME , NOT NULL       * */
    public static String  columnStaxsubjectname()
    {
        return "S_TAXSUBJECTNAME";
    }
   
    /**
    *   Getter I_TAXNUMBER , NOT NULL       * */
    public static String  columnItaxnumber()
    {
        return "I_TAXNUMBER";
    }
   
    /**
    *   Getter F_TAXAMT , NOT NULL       * */
    public static String  columnFtaxamt()
    {
        return "F_TAXAMT";
    }
   
    /**
    *   Getter F_TAXRATE , NOT NULL       * */
    public static String  columnFtaxrate()
    {
        return "F_TAXRATE";
    }
   
    /**
    *   Getter F_EXPTAXAMT , NOT NULL       * */
    public static String  columnFexptaxamt()
    {
        return "F_EXPTAXAMT";
    }
   
    /**
    *   Getter F_DISCOUNTTAXAMT , NOT NULL       * */
    public static String  columnFdiscounttaxamt()
    {
        return "F_DISCOUNTTAXAMT";
    }
   
    /**
    *   Getter F_REALAMT , NOT NULL       * */
    public static String  columnFrealamt()
    {
        return "F_REALAMT";
    }
   
    /**
    *   Getter TS_UPDATE , NOT NULL       * */
    public static String  columnTsupdate()
    {
        return "TS_UPDATE";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "HTV_TAX_ITEM";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[12];        
        columnNames[0]="S_SEQ";
        columnNames[1]="I_PROJECTID";
        columnNames[2]="I_DETAILNO";
        columnNames[3]="S_TAXSUBJECTCODE";
        columnNames[4]="S_TAXSUBJECTNAME";
        columnNames[5]="I_TAXNUMBER";
        columnNames[6]="F_TAXAMT";
        columnNames[7]="F_TAXRATE";
        columnNames[8]="F_EXPTAXAMT";
        columnNames[9]="F_DISCOUNTTAXAMT";
        columnNames[10]="F_REALAMT";
        columnNames[11]="TS_UPDATE";
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

        if (obj == null || !(obj instanceof HtvTaxItemDto))
            return false;

        HtvTaxItemDto bean = (HtvTaxItemDto) obj;


      //compare field sseq
      if((this.sseq==null && bean.sseq!=null) || (this.sseq!=null && bean.sseq==null))
          return false;
        else if(this.sseq==null && bean.sseq==null){
        }
        else{
          if(!bean.sseq.equals(this.sseq))
               return false;
     }
      //compare field iprojectid
      if((this.iprojectid==null && bean.iprojectid!=null) || (this.iprojectid!=null && bean.iprojectid==null))
          return false;
        else if(this.iprojectid==null && bean.iprojectid==null){
        }
        else{
          if(!bean.iprojectid.equals(this.iprojectid))
               return false;
     }
      //compare field idetailno
      if((this.idetailno==null && bean.idetailno!=null) || (this.idetailno!=null && bean.idetailno==null))
          return false;
        else if(this.idetailno==null && bean.idetailno==null){
        }
        else{
          if(!bean.idetailno.equals(this.idetailno))
               return false;
     }
      //compare field staxsubjectcode
      if((this.staxsubjectcode==null && bean.staxsubjectcode!=null) || (this.staxsubjectcode!=null && bean.staxsubjectcode==null))
          return false;
        else if(this.staxsubjectcode==null && bean.staxsubjectcode==null){
        }
        else{
          if(!bean.staxsubjectcode.equals(this.staxsubjectcode))
               return false;
     }
      //compare field staxsubjectname
      if((this.staxsubjectname==null && bean.staxsubjectname!=null) || (this.staxsubjectname!=null && bean.staxsubjectname==null))
          return false;
        else if(this.staxsubjectname==null && bean.staxsubjectname==null){
        }
        else{
          if(!bean.staxsubjectname.equals(this.staxsubjectname))
               return false;
     }
      //compare field itaxnumber
      if((this.itaxnumber==null && bean.itaxnumber!=null) || (this.itaxnumber!=null && bean.itaxnumber==null))
          return false;
        else if(this.itaxnumber==null && bean.itaxnumber==null){
        }
        else{
          if(!bean.itaxnumber.equals(this.itaxnumber))
               return false;
     }
      //compare field ftaxamt
      if((this.ftaxamt==null && bean.ftaxamt!=null) || (this.ftaxamt!=null && bean.ftaxamt==null))
          return false;
        else if(this.ftaxamt==null && bean.ftaxamt==null){
        }
        else{
          if(!bean.ftaxamt.equals(this.ftaxamt))
               return false;
     }
      //compare field ftaxrate
      if((this.ftaxrate==null && bean.ftaxrate!=null) || (this.ftaxrate!=null && bean.ftaxrate==null))
          return false;
        else if(this.ftaxrate==null && bean.ftaxrate==null){
        }
        else{
          if(!bean.ftaxrate.equals(this.ftaxrate))
               return false;
     }
      //compare field fexptaxamt
      if((this.fexptaxamt==null && bean.fexptaxamt!=null) || (this.fexptaxamt!=null && bean.fexptaxamt==null))
          return false;
        else if(this.fexptaxamt==null && bean.fexptaxamt==null){
        }
        else{
          if(!bean.fexptaxamt.equals(this.fexptaxamt))
               return false;
     }
      //compare field fdiscounttaxamt
      if((this.fdiscounttaxamt==null && bean.fdiscounttaxamt!=null) || (this.fdiscounttaxamt!=null && bean.fdiscounttaxamt==null))
          return false;
        else if(this.fdiscounttaxamt==null && bean.fdiscounttaxamt==null){
        }
        else{
          if(!bean.fdiscounttaxamt.equals(this.fdiscounttaxamt))
               return false;
     }
      //compare field frealamt
      if((this.frealamt==null && bean.frealamt!=null) || (this.frealamt!=null && bean.frealamt==null))
          return false;
        else if(this.frealamt==null && bean.frealamt==null){
        }
        else{
          if(!bean.frealamt.equals(this.frealamt))
               return false;
     }
      //compare field tsupdate
      if((this.tsupdate==null && bean.tsupdate!=null) || (this.tsupdate!=null && bean.tsupdate==null))
          return false;
        else if(this.tsupdate==null && bean.tsupdate==null){
        }
        else{
          if(!bean.tsupdate.equals(this.tsupdate))
               return false;
     }



        return true;
    }

    /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
  
		int _hash_ = 1;
		
        if(this.sseq!=null)
          _hash_ = _hash_ * 31+ this.sseq.hashCode() ;
        if(this.iprojectid!=null)
          _hash_ = _hash_ * 31+ this.iprojectid.hashCode() ;
        if(this.idetailno!=null)
          _hash_ = _hash_ * 31+ this.idetailno.hashCode() ;
        if(this.staxsubjectcode!=null)
          _hash_ = _hash_ * 31+ this.staxsubjectcode.hashCode() ;
        if(this.staxsubjectname!=null)
          _hash_ = _hash_ * 31+ this.staxsubjectname.hashCode() ;
        if(this.itaxnumber!=null)
          _hash_ = _hash_ * 31+ this.itaxnumber.hashCode() ;
        if(this.ftaxamt!=null)
          _hash_ = _hash_ * 31+ this.ftaxamt.hashCode() ;
        if(this.ftaxrate!=null)
          _hash_ = _hash_ * 31+ this.ftaxrate.hashCode() ;
        if(this.fexptaxamt!=null)
          _hash_ = _hash_ * 31+ this.fexptaxamt.hashCode() ;
        if(this.fdiscounttaxamt!=null)
          _hash_ = _hash_ * 31+ this.fdiscounttaxamt.hashCode() ;
        if(this.frealamt!=null)
          _hash_ = _hash_ * 31+ this.frealamt.hashCode() ;
        if(this.tsupdate!=null)
          _hash_ = _hash_ * 31+ this.tsupdate.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        HtvTaxItemDto bean = new HtvTaxItemDto();

          bean.sseq = this.sseq;

          bean.iprojectid = this.iprojectid;

          bean.idetailno = this.idetailno;

          if (this.staxsubjectcode != null)
            bean.staxsubjectcode = new String(this.staxsubjectcode);
          if (this.staxsubjectname != null)
            bean.staxsubjectname = new String(this.staxsubjectname);
          if (this.itaxnumber != null)
            bean.itaxnumber = new Integer(this.itaxnumber.toString());
          if (this.ftaxamt != null)
            bean.ftaxamt = new BigDecimal(this.ftaxamt.toString());
          if (this.ftaxrate != null)
            bean.ftaxrate = new BigDecimal(this.ftaxrate.toString());
          if (this.fexptaxamt != null)
            bean.fexptaxamt = new BigDecimal(this.fexptaxamt.toString());
          if (this.fdiscounttaxamt != null)
            bean.fdiscounttaxamt = new BigDecimal(this.fdiscounttaxamt.toString());
          if (this.frealamt != null)
            bean.frealamt = new BigDecimal(this.frealamt.toString());
          if (this.tsupdate != null)
            bean.tsupdate = (Timestamp) this.tsupdate.clone();
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("HtvTaxItemDto").append(sep);
        sb.append("[sseq]").append(" = ").append(sseq).append(sep);
        sb.append("[iprojectid]").append(" = ").append(iprojectid).append(sep);
        sb.append("[idetailno]").append(" = ").append(idetailno).append(sep);
        sb.append("[staxsubjectcode]").append(" = ").append(staxsubjectcode).append(sep);
        sb.append("[staxsubjectname]").append(" = ").append(staxsubjectname).append(sep);
        sb.append("[itaxnumber]").append(" = ").append(itaxnumber).append(sep);
        sb.append("[ftaxamt]").append(" = ").append(ftaxamt).append(sep);
        sb.append("[ftaxrate]").append(" = ").append(ftaxrate).append(sep);
        sb.append("[fexptaxamt]").append(" = ").append(fexptaxamt).append(sep);
        sb.append("[fdiscounttaxamt]").append(" = ").append(fdiscounttaxamt).append(sep);
        sb.append("[frealamt]").append(" = ").append(frealamt).append(sep);
        sb.append("[tsupdate]").append(" = ").append(tsupdate).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //check field S_SEQ
      if (this.getSseq()==null)
             sb.append("S_SEQ����Ϊ��; ");
      if (this.getSseq()!=null)
             if (this.getSseq().getBytes().length > 20)
                sb.append("S_SEQ���Ȳ��ܳ��� "+20+"���ַ�; ");
    
    //check field I_PROJECTID
      if (this.getIprojectid()==null)
             sb.append("I_PROJECTID����Ϊ��; ");
      
    //check field I_DETAILNO
      if (this.getIdetailno()==null)
             sb.append("I_DETAILNO����Ϊ��; ");
      
    //check field S_TAXSUBJECTCODE
      if (this.getStaxsubjectcode()==null)
             sb.append("S_TAXSUBJECTCODE����Ϊ��; ");
      if (this.getStaxsubjectcode()!=null)
             if (this.getStaxsubjectcode().getBytes().length > 20)
                sb.append("S_TAXSUBJECTCODE���Ȳ��ܳ��� "+20+"���ַ�; ");
    
    //check field S_TAXSUBJECTNAME
      if (this.getStaxsubjectname()==null)
             sb.append("S_TAXSUBJECTNAME����Ϊ��; ");
      if (this.getStaxsubjectname()!=null)
             if (this.getStaxsubjectname().getBytes().length > 60)
                sb.append("S_TAXSUBJECTNAME���Ȳ��ܳ��� "+60+"���ַ�; ");
    
    //check field I_TAXNUMBER
      if (this.getItaxnumber()==null)
             sb.append("I_TAXNUMBER����Ϊ��; ");
      
    //check field F_TAXAMT
      if (this.getFtaxamt()==null)
             sb.append("F_TAXAMT����Ϊ��; ");
      
    //check field F_TAXRATE
      if (this.getFtaxrate()==null)
             sb.append("F_TAXRATE����Ϊ��; ");
      
    //check field F_EXPTAXAMT
      if (this.getFexptaxamt()==null)
             sb.append("F_EXPTAXAMT����Ϊ��; ");
      
    //check field F_DISCOUNTTAXAMT
      if (this.getFdiscounttaxamt()==null)
             sb.append("F_DISCOUNTTAXAMT����Ϊ��; ");
      
    //check field F_REALAMT
      if (this.getFrealamt()==null)
             sb.append("F_REALAMT����Ϊ��; ");
      
    //check field TS_UPDATE
      if (this.getTsupdate()==null)
             sb.append("TS_UPDATE����Ϊ��; ");
      

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
    //check field S_SEQ
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_SEQ")) {
               if (this.getSseq()==null)
                    sb.append("S_SEQ ����Ϊ��; ");
               if (this.getSseq()!=null)
                    if (this.getSseq().getBytes().length > 20)
                        sb.append("S_SEQ ���Ȳ��ܳ��� "+20+"���ַ�");
             break;
         }
  }
    
    //check field I_PROJECTID
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("I_PROJECTID")) {
               if (this.getIprojectid()==null)
                    sb.append("I_PROJECTID ����Ϊ��; ");
               break;
         }
  }
    
    //check field I_DETAILNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("I_DETAILNO")) {
               if (this.getIdetailno()==null)
                    sb.append("I_DETAILNO ����Ϊ��; ");
               break;
         }
  }
    
    //check field S_TAXSUBJECTCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXSUBJECTCODE")) {
               if (this.getStaxsubjectcode()==null)
                    sb.append("S_TAXSUBJECTCODE ����Ϊ��; ");
               if (this.getStaxsubjectcode()!=null)
                    if (this.getStaxsubjectcode().getBytes().length > 20)
                        sb.append("S_TAXSUBJECTCODE ���Ȳ��ܳ��� "+20+"���ַ�");
             break;
         }
  }
    
    //check field S_TAXSUBJECTNAME
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_TAXSUBJECTNAME")) {
               if (this.getStaxsubjectname()==null)
                    sb.append("S_TAXSUBJECTNAME ����Ϊ��; ");
               if (this.getStaxsubjectname()!=null)
                    if (this.getStaxsubjectname().getBytes().length > 60)
                        sb.append("S_TAXSUBJECTNAME ���Ȳ��ܳ��� "+60+"���ַ�");
             break;
         }
  }
    
    //check field I_TAXNUMBER
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("I_TAXNUMBER")) {
               if (this.getItaxnumber()==null)
                    sb.append("I_TAXNUMBER ����Ϊ��; ");
               break;
         }
  }
    
    //check field F_TAXAMT
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("F_TAXAMT")) {
               if (this.getFtaxamt()==null)
                    sb.append("F_TAXAMT ����Ϊ��; ");
               break;
         }
  }
    
    //check field F_TAXRATE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("F_TAXRATE")) {
               if (this.getFtaxrate()==null)
                    sb.append("F_TAXRATE ����Ϊ��; ");
               break;
         }
  }
    
    //check field F_EXPTAXAMT
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("F_EXPTAXAMT")) {
               if (this.getFexptaxamt()==null)
                    sb.append("F_EXPTAXAMT ����Ϊ��; ");
               break;
         }
  }
    
    //check field F_DISCOUNTTAXAMT
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("F_DISCOUNTTAXAMT")) {
               if (this.getFdiscounttaxamt()==null)
                    sb.append("F_DISCOUNTTAXAMT ����Ϊ��; ");
               break;
         }
  }
    
    //check field F_REALAMT
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("F_REALAMT")) {
               if (this.getFrealamt()==null)
                    sb.append("F_REALAMT ����Ϊ��; ");
               break;
         }
  }
    
    //check field TS_UPDATE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("TS_UPDATE")) {
               if (this.getTsupdate()==null)
                    sb.append("TS_UPDATE ����Ϊ��; ");
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
      HtvTaxItemPK pk = new HtvTaxItemPK();
      pk.setSseq(getSseq());
      pk.setIprojectid(getIprojectid());
      pk.setIdetailno(getIdetailno());
      return pk;
    };
}
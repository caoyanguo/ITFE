    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TvTaxorgIncomePK;
/**
 * <p>Title: DTO of table: TV_TAXORG_INCOME</p>
 * <p>Description:  Data Transfer Object </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:04 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  dto.vm version timestamp: 2008-01-08 16:30:00 
 *
 * @author win7
 */

public class TvTaxorgIncomeDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  S_TAXORGCODE CHARACTER , PK   , NOT NULL       */
    protected String staxorgcode;
    /**
    *  S_TRECODE CHARACTER   , NOT NULL       */
    protected String strecode;
    /**
    *  S_INTREDATE CHARACTER   , NOT NULL       */
    protected String sintredate;
    /**
    *  I_PKGSEQNO VARCHAR   , NOT NULL       */
    protected String ipkgseqno;
    /**
    *  S_EXPVOUNO VARCHAR , PK   , NOT NULL       */
    protected String sexpvouno;
    /**
    *  S_EXPVOUTYPE CHARACTER   , NOT NULL       */
    protected String sexpvoutype;
    /**
    *  C_BDGLEVEL CHARACTER   , NOT NULL       */
    protected String cbdglevel;
    /**
    *  S_BDGSBTCODE VARCHAR   , NOT NULL       */
    protected String sbdgsbtcode;
    /**
    *  C_BDGKIND CHARACTER   , NOT NULL       */
    protected String cbdgkind;
    /**
    *  F_AMT DECIMAL   , NOT NULL       */
    protected BigDecimal famt;
    /**
    *  C_VOUCHANNEL CHARACTER         */
    protected String cvouchannel;


/******************************************************
*
*  getter and setter
*
*******************************************************/


    public String getStaxorgcode()
    {
        return staxorgcode;
    }
     /**
     *   Setter S_TAXORGCODE, PK , NOT NULL        * */
    public void setStaxorgcode(String _staxorgcode) {
        this.staxorgcode = _staxorgcode;
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


    public String getSintredate()
    {
        return sintredate;
    }
     /**
     *   Setter S_INTREDATE , NOT NULL        * */
    public void setSintredate(String _sintredate) {
        this.sintredate = _sintredate;
    }


    public String getIpkgseqno()
    {
        return ipkgseqno;
    }
     /**
     *   Setter I_PKGSEQNO , NOT NULL        * */
    public void setIpkgseqno(String _ipkgseqno) {
        this.ipkgseqno = _ipkgseqno;
    }


    public String getSexpvouno()
    {
        return sexpvouno;
    }
     /**
     *   Setter S_EXPVOUNO, PK , NOT NULL        * */
    public void setSexpvouno(String _sexpvouno) {
        this.sexpvouno = _sexpvouno;
    }


    public String getSexpvoutype()
    {
        return sexpvoutype;
    }
     /**
     *   Setter S_EXPVOUTYPE , NOT NULL        * */
    public void setSexpvoutype(String _sexpvoutype) {
        this.sexpvoutype = _sexpvoutype;
    }


    public String getCbdglevel()
    {
        return cbdglevel;
    }
     /**
     *   Setter C_BDGLEVEL , NOT NULL        * */
    public void setCbdglevel(String _cbdglevel) {
        this.cbdglevel = _cbdglevel;
    }


    public String getSbdgsbtcode()
    {
        return sbdgsbtcode;
    }
     /**
     *   Setter S_BDGSBTCODE , NOT NULL        * */
    public void setSbdgsbtcode(String _sbdgsbtcode) {
        this.sbdgsbtcode = _sbdgsbtcode;
    }


    public String getCbdgkind()
    {
        return cbdgkind;
    }
     /**
     *   Setter C_BDGKIND , NOT NULL        * */
    public void setCbdgkind(String _cbdgkind) {
        this.cbdgkind = _cbdgkind;
    }


    public BigDecimal getFamt()
    {
        return famt;
    }
     /**
     *   Setter F_AMT , NOT NULL        * */
    public void setFamt(BigDecimal _famt) {
        this.famt = _famt;
    }


    public String getCvouchannel()
    {
        return cvouchannel;
    }
     /**
     *   Setter C_VOUCHANNEL        * */
    public void setCvouchannel(String _cvouchannel) {
        this.cvouchannel = _cvouchannel;
    }




/******************************************************
*
*  Get Column Name
*
*******************************************************/
    /**
    *   Getter S_TAXORGCODE, PK , NOT NULL       * */
    public static String  columnStaxorgcode()
    {
        return "S_TAXORGCODE";
    }
   
    /**
    *   Getter S_TRECODE , NOT NULL       * */
    public static String  columnStrecode()
    {
        return "S_TRECODE";
    }
   
    /**
    *   Getter S_INTREDATE , NOT NULL       * */
    public static String  columnSintredate()
    {
        return "S_INTREDATE";
    }
   
    /**
    *   Getter I_PKGSEQNO , NOT NULL       * */
    public static String  columnIpkgseqno()
    {
        return "I_PKGSEQNO";
    }
   
    /**
    *   Getter S_EXPVOUNO, PK , NOT NULL       * */
    public static String  columnSexpvouno()
    {
        return "S_EXPVOUNO";
    }
   
    /**
    *   Getter S_EXPVOUTYPE , NOT NULL       * */
    public static String  columnSexpvoutype()
    {
        return "S_EXPVOUTYPE";
    }
   
    /**
    *   Getter C_BDGLEVEL , NOT NULL       * */
    public static String  columnCbdglevel()
    {
        return "C_BDGLEVEL";
    }
   
    /**
    *   Getter S_BDGSBTCODE , NOT NULL       * */
    public static String  columnSbdgsbtcode()
    {
        return "S_BDGSBTCODE";
    }
   
    /**
    *   Getter C_BDGKIND , NOT NULL       * */
    public static String  columnCbdgkind()
    {
        return "C_BDGKIND";
    }
   
    /**
    *   Getter F_AMT , NOT NULL       * */
    public static String  columnFamt()
    {
        return "F_AMT";
    }
   
    /**
    *   Getter C_VOUCHANNEL       * */
    public static String  columnCvouchannel()
    {
        return "C_VOUCHANNEL";
    }
   


    /**
    *  Table Name
    */
    public static String tableName(){
        return "TV_TAXORG_INCOME";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[11];        
        columnNames[0]="S_TAXORGCODE";
        columnNames[1]="S_TRECODE";
        columnNames[2]="S_INTREDATE";
        columnNames[3]="I_PKGSEQNO";
        columnNames[4]="S_EXPVOUNO";
        columnNames[5]="S_EXPVOUTYPE";
        columnNames[6]="C_BDGLEVEL";
        columnNames[7]="S_BDGSBTCODE";
        columnNames[8]="C_BDGKIND";
        columnNames[9]="F_AMT";
        columnNames[10]="C_VOUCHANNEL";
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

        if (obj == null || !(obj instanceof TvTaxorgIncomeDto))
            return false;

        TvTaxorgIncomeDto bean = (TvTaxorgIncomeDto) obj;


      //compare field staxorgcode
      if((this.staxorgcode==null && bean.staxorgcode!=null) || (this.staxorgcode!=null && bean.staxorgcode==null))
          return false;
        else if(this.staxorgcode==null && bean.staxorgcode==null){
        }
        else{
          if(!bean.staxorgcode.equals(this.staxorgcode))
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
      //compare field sintredate
      if((this.sintredate==null && bean.sintredate!=null) || (this.sintredate!=null && bean.sintredate==null))
          return false;
        else if(this.sintredate==null && bean.sintredate==null){
        }
        else{
          if(!bean.sintredate.equals(this.sintredate))
               return false;
     }
      //compare field ipkgseqno
      if((this.ipkgseqno==null && bean.ipkgseqno!=null) || (this.ipkgseqno!=null && bean.ipkgseqno==null))
          return false;
        else if(this.ipkgseqno==null && bean.ipkgseqno==null){
        }
        else{
          if(!bean.ipkgseqno.equals(this.ipkgseqno))
               return false;
     }
      //compare field sexpvouno
      if((this.sexpvouno==null && bean.sexpvouno!=null) || (this.sexpvouno!=null && bean.sexpvouno==null))
          return false;
        else if(this.sexpvouno==null && bean.sexpvouno==null){
        }
        else{
          if(!bean.sexpvouno.equals(this.sexpvouno))
               return false;
     }
      //compare field sexpvoutype
      if((this.sexpvoutype==null && bean.sexpvoutype!=null) || (this.sexpvoutype!=null && bean.sexpvoutype==null))
          return false;
        else if(this.sexpvoutype==null && bean.sexpvoutype==null){
        }
        else{
          if(!bean.sexpvoutype.equals(this.sexpvoutype))
               return false;
     }
      //compare field cbdglevel
      if((this.cbdglevel==null && bean.cbdglevel!=null) || (this.cbdglevel!=null && bean.cbdglevel==null))
          return false;
        else if(this.cbdglevel==null && bean.cbdglevel==null){
        }
        else{
          if(!bean.cbdglevel.equals(this.cbdglevel))
               return false;
     }
      //compare field sbdgsbtcode
      if((this.sbdgsbtcode==null && bean.sbdgsbtcode!=null) || (this.sbdgsbtcode!=null && bean.sbdgsbtcode==null))
          return false;
        else if(this.sbdgsbtcode==null && bean.sbdgsbtcode==null){
        }
        else{
          if(!bean.sbdgsbtcode.equals(this.sbdgsbtcode))
               return false;
     }
      //compare field cbdgkind
      if((this.cbdgkind==null && bean.cbdgkind!=null) || (this.cbdgkind!=null && bean.cbdgkind==null))
          return false;
        else if(this.cbdgkind==null && bean.cbdgkind==null){
        }
        else{
          if(!bean.cbdgkind.equals(this.cbdgkind))
               return false;
     }
      //compare field famt
      if((this.famt==null && bean.famt!=null) || (this.famt!=null && bean.famt==null))
          return false;
        else if(this.famt==null && bean.famt==null){
        }
        else{
          if(!bean.famt.equals(this.famt))
               return false;
     }
      //compare field cvouchannel
      if((this.cvouchannel==null && bean.cvouchannel!=null) || (this.cvouchannel!=null && bean.cvouchannel==null))
          return false;
        else if(this.cvouchannel==null && bean.cvouchannel==null){
        }
        else{
          if(!bean.cvouchannel.equals(this.cvouchannel))
               return false;
     }



        return true;
    }

    /* return hashCode ,if A.equals(B) that A.hashCode()==B.hashCode() */
	public int hashCode()
	{
  
		int _hash_ = 1;
		
        if(this.staxorgcode!=null)
          _hash_ = _hash_ * 31+ this.staxorgcode.hashCode() ;
        if(this.strecode!=null)
          _hash_ = _hash_ * 31+ this.strecode.hashCode() ;
        if(this.sintredate!=null)
          _hash_ = _hash_ * 31+ this.sintredate.hashCode() ;
        if(this.ipkgseqno!=null)
          _hash_ = _hash_ * 31+ this.ipkgseqno.hashCode() ;
        if(this.sexpvouno!=null)
          _hash_ = _hash_ * 31+ this.sexpvouno.hashCode() ;
        if(this.sexpvoutype!=null)
          _hash_ = _hash_ * 31+ this.sexpvoutype.hashCode() ;
        if(this.cbdglevel!=null)
          _hash_ = _hash_ * 31+ this.cbdglevel.hashCode() ;
        if(this.sbdgsbtcode!=null)
          _hash_ = _hash_ * 31+ this.sbdgsbtcode.hashCode() ;
        if(this.cbdgkind!=null)
          _hash_ = _hash_ * 31+ this.cbdgkind.hashCode() ;
        if(this.famt!=null)
          _hash_ = _hash_ * 31+ this.famt.hashCode() ;
        if(this.cvouchannel!=null)
          _hash_ = _hash_ * 31+ this.cvouchannel.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TvTaxorgIncomeDto bean = new TvTaxorgIncomeDto();

          bean.staxorgcode = this.staxorgcode;

          if (this.strecode != null)
            bean.strecode = new String(this.strecode);
          if (this.sintredate != null)
            bean.sintredate = new String(this.sintredate);
          if (this.ipkgseqno != null)
            bean.ipkgseqno = new String(this.ipkgseqno);
          bean.sexpvouno = this.sexpvouno;

          if (this.sexpvoutype != null)
            bean.sexpvoutype = new String(this.sexpvoutype);
          if (this.cbdglevel != null)
            bean.cbdglevel = new String(this.cbdglevel);
          if (this.sbdgsbtcode != null)
            bean.sbdgsbtcode = new String(this.sbdgsbtcode);
          if (this.cbdgkind != null)
            bean.cbdgkind = new String(this.cbdgkind);
          if (this.famt != null)
            bean.famt = new BigDecimal(this.famt.toString());
          if (this.cvouchannel != null)
            bean.cvouchannel = new String(this.cvouchannel);
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TvTaxorgIncomeDto").append(sep);
        sb.append("[staxorgcode]").append(" = ").append(staxorgcode).append(sep);
        sb.append("[strecode]").append(" = ").append(strecode).append(sep);
        sb.append("[sintredate]").append(" = ").append(sintredate).append(sep);
        sb.append("[ipkgseqno]").append(" = ").append(ipkgseqno).append(sep);
        sb.append("[sexpvouno]").append(" = ").append(sexpvouno).append(sep);
        sb.append("[sexpvoutype]").append(" = ").append(sexpvoutype).append(sep);
        sb.append("[cbdglevel]").append(" = ").append(cbdglevel).append(sep);
        sb.append("[sbdgsbtcode]").append(" = ").append(sbdgsbtcode).append(sep);
        sb.append("[cbdgkind]").append(" = ").append(cbdgkind).append(sep);
        sb.append("[famt]").append(" = ").append(famt).append(sep);
        sb.append("[cvouchannel]").append(" = ").append(cvouchannel).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //check field S_TAXORGCODE
      if (this.getStaxorgcode()==null)
             sb.append("S_TAXORGCODE不能为空; ");
      if (this.getStaxorgcode()!=null)
             if (this.getStaxorgcode().getBytes().length > 12)
                sb.append("S_TAXORGCODE宽度不能超过 "+12+"个字符; ");
    
    //check field S_TRECODE
      if (this.getStrecode()==null)
             sb.append("S_TRECODE不能为空; ");
      if (this.getStrecode()!=null)
             if (this.getStrecode().getBytes().length > 10)
                sb.append("S_TRECODE宽度不能超过 "+10+"个字符; ");
    
    //check field S_INTREDATE
      if (this.getSintredate()==null)
             sb.append("S_INTREDATE不能为空; ");
      if (this.getSintredate()!=null)
             if (this.getSintredate().getBytes().length > 8)
                sb.append("S_INTREDATE宽度不能超过 "+8+"个字符; ");
    
    //check field I_PKGSEQNO
      if (this.getIpkgseqno()==null)
             sb.append("I_PKGSEQNO不能为空; ");
      if (this.getIpkgseqno()!=null)
             if (this.getIpkgseqno().getBytes().length > 8)
                sb.append("I_PKGSEQNO宽度不能超过 "+8+"个字符; ");
    
    //check field S_EXPVOUNO
      if (this.getSexpvouno()==null)
             sb.append("S_EXPVOUNO不能为空; ");
      if (this.getSexpvouno()!=null)
             if (this.getSexpvouno().getBytes().length > 20)
                sb.append("S_EXPVOUNO宽度不能超过 "+20+"个字符; ");
    
    //check field S_EXPVOUTYPE
      if (this.getSexpvoutype()==null)
             sb.append("S_EXPVOUTYPE不能为空; ");
      if (this.getSexpvoutype()!=null)
             if (this.getSexpvoutype().getBytes().length > 1)
                sb.append("S_EXPVOUTYPE宽度不能超过 "+1+"个字符; ");
    
    //check field C_BDGLEVEL
      if (this.getCbdglevel()==null)
             sb.append("C_BDGLEVEL不能为空; ");
      if (this.getCbdglevel()!=null)
             if (this.getCbdglevel().getBytes().length > 1)
                sb.append("C_BDGLEVEL宽度不能超过 "+1+"个字符; ");
    
    //check field S_BDGSBTCODE
      if (this.getSbdgsbtcode()==null)
             sb.append("S_BDGSBTCODE不能为空; ");
      if (this.getSbdgsbtcode()!=null)
             if (this.getSbdgsbtcode().getBytes().length > 30)
                sb.append("S_BDGSBTCODE宽度不能超过 "+30+"个字符; ");
    
    //check field C_BDGKIND
      if (this.getCbdgkind()==null)
             sb.append("C_BDGKIND不能为空; ");
      if (this.getCbdgkind()!=null)
             if (this.getCbdgkind().getBytes().length > 1)
                sb.append("C_BDGKIND宽度不能超过 "+1+"个字符; ");
    
    //check field F_AMT
      if (this.getFamt()==null)
             sb.append("F_AMT不能为空; ");
      
    //check field C_VOUCHANNEL
      if (this.getCvouchannel()!=null)
             if (this.getCvouchannel().getBytes().length > 1)
                sb.append("C_VOUCHANNEL宽度不能超过 "+1+"个字符; ");
    

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
    
    //check field S_INTREDATE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_INTREDATE")) {
               if (this.getSintredate()==null)
                    sb.append("S_INTREDATE 不能为空; ");
               if (this.getSintredate()!=null)
                    if (this.getSintredate().getBytes().length > 8)
                        sb.append("S_INTREDATE 宽度不能超过 "+8+"个字符");
             break;
         }
  }
    
    //check field I_PKGSEQNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("I_PKGSEQNO")) {
               if (this.getIpkgseqno()==null)
                    sb.append("I_PKGSEQNO 不能为空; ");
               if (this.getIpkgseqno()!=null)
                    if (this.getIpkgseqno().getBytes().length > 8)
                        sb.append("I_PKGSEQNO 宽度不能超过 "+8+"个字符");
             break;
         }
  }
    
    //check field S_EXPVOUNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_EXPVOUNO")) {
               if (this.getSexpvouno()==null)
                    sb.append("S_EXPVOUNO 不能为空; ");
               if (this.getSexpvouno()!=null)
                    if (this.getSexpvouno().getBytes().length > 20)
                        sb.append("S_EXPVOUNO 宽度不能超过 "+20+"个字符");
             break;
         }
  }
    
    //check field S_EXPVOUTYPE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_EXPVOUTYPE")) {
               if (this.getSexpvoutype()==null)
                    sb.append("S_EXPVOUTYPE 不能为空; ");
               if (this.getSexpvoutype()!=null)
                    if (this.getSexpvoutype().getBytes().length > 1)
                        sb.append("S_EXPVOUTYPE 宽度不能超过 "+1+"个字符");
             break;
         }
  }
    
    //check field C_BDGLEVEL
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("C_BDGLEVEL")) {
               if (this.getCbdglevel()==null)
                    sb.append("C_BDGLEVEL 不能为空; ");
               if (this.getCbdglevel()!=null)
                    if (this.getCbdglevel().getBytes().length > 1)
                        sb.append("C_BDGLEVEL 宽度不能超过 "+1+"个字符");
             break;
         }
  }
    
    //check field S_BDGSBTCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BDGSBTCODE")) {
               if (this.getSbdgsbtcode()==null)
                    sb.append("S_BDGSBTCODE 不能为空; ");
               if (this.getSbdgsbtcode()!=null)
                    if (this.getSbdgsbtcode().getBytes().length > 30)
                        sb.append("S_BDGSBTCODE 宽度不能超过 "+30+"个字符");
             break;
         }
  }
    
    //check field C_BDGKIND
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("C_BDGKIND")) {
               if (this.getCbdgkind()==null)
                    sb.append("C_BDGKIND 不能为空; ");
               if (this.getCbdgkind()!=null)
                    if (this.getCbdgkind().getBytes().length > 1)
                        sb.append("C_BDGKIND 宽度不能超过 "+1+"个字符");
             break;
         }
  }
    
    //check field F_AMT
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("F_AMT")) {
               if (this.getFamt()==null)
                    sb.append("F_AMT 不能为空; ");
               break;
         }
  }
    
    //check field C_VOUCHANNEL
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("C_VOUCHANNEL")) {
                 if (this.getCvouchannel()!=null)
                    if (this.getCvouchannel().getBytes().length > 1)
                        sb.append("C_VOUCHANNEL 宽度不能超过 "+1+"个字符");
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
      TvTaxorgIncomePK pk = new TvTaxorgIncomePK();
      pk.setStaxorgcode(getStaxorgcode());
      pk.setSexpvouno(getSexpvouno());
      return pk;
    };
}

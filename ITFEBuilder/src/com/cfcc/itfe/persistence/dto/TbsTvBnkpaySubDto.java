    
package com.cfcc.itfe.persistence.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;

import java.lang.reflect.Array;

import com.cfcc.jaf.persistence.jaform.parent.IDto ;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.itfe.persistence.pk.TbsTvBnkpaySubPK;
/**
 * <p>Title: DTO of table: TBS_TV_BNKPAY_SUB</p>
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

public class TbsTvBnkpaySubDto   
                              implements IDto  {
/********************************************************
 *   fields
 ********************************************************/

    /**
    *  I_VOUSRLNO BIGINT , PK   , NOT NULL       */
    protected Long ivousrlno;
    /**
    *  I_GRPINNERSEQNO INTEGER , PK   , NOT NULL       */
    protected Integer igrpinnerseqno;
    /**
    *  S_BOOKORGCODE CHARACTER         */
    protected String sbookorgcode;
    /**
    *  S_BDGORGCODE VARCHAR         */
    protected String sbdgorgcode;
    /**
    *  S_ORIVOUNO VARCHAR         */
    protected String sorivouno;
    /**
    *  D_ORIVOUDATE DATE         */
    protected Date dorivoudate;
    /**
    *  S_FUNCSBTCODE VARCHAR         */
    protected String sfuncsbtcode;
    /**
    *  S_ECOSBTCODE VARCHAR         */
    protected String secosbtcode;
    /**
    *  F_AMT DECIMAL         */
    protected BigDecimal famt;
    /**
    *  C_ACCTPROP CHARACTER         */
    protected String cacctprop;
    /**
    *  TS_SYSUPDATE TIMESTAMP   , NOT NULL       */
    protected Timestamp tssysupdate;


/******************************************************
*
*  getter and setter
*
*******************************************************/


    public Long getIvousrlno()
    {
        return ivousrlno;
    }
     /**
     *   Setter I_VOUSRLNO, PK , NOT NULL        * */
    public void setIvousrlno(Long _ivousrlno) {
        this.ivousrlno = _ivousrlno;
    }


    public Integer getIgrpinnerseqno()
    {
        return igrpinnerseqno;
    }
     /**
     *   Setter I_GRPINNERSEQNO, PK , NOT NULL        * */
    public void setIgrpinnerseqno(Integer _igrpinnerseqno) {
        this.igrpinnerseqno = _igrpinnerseqno;
    }


    public String getSbookorgcode()
    {
        return sbookorgcode;
    }
     /**
     *   Setter S_BOOKORGCODE        * */
    public void setSbookorgcode(String _sbookorgcode) {
        this.sbookorgcode = _sbookorgcode;
    }


    public String getSbdgorgcode()
    {
        return sbdgorgcode;
    }
     /**
     *   Setter S_BDGORGCODE        * */
    public void setSbdgorgcode(String _sbdgorgcode) {
        this.sbdgorgcode = _sbdgorgcode;
    }


    public String getSorivouno()
    {
        return sorivouno;
    }
     /**
     *   Setter S_ORIVOUNO        * */
    public void setSorivouno(String _sorivouno) {
        this.sorivouno = _sorivouno;
    }


    public Date getDorivoudate()
    {
        return dorivoudate;
    }
     /**
     *   Setter D_ORIVOUDATE        * */
    public void setDorivoudate(Date _dorivoudate) {
        this.dorivoudate = _dorivoudate;
    }


    public String getSfuncsbtcode()
    {
        return sfuncsbtcode;
    }
     /**
     *   Setter S_FUNCSBTCODE        * */
    public void setSfuncsbtcode(String _sfuncsbtcode) {
        this.sfuncsbtcode = _sfuncsbtcode;
    }


    public String getSecosbtcode()
    {
        return secosbtcode;
    }
     /**
     *   Setter S_ECOSBTCODE        * */
    public void setSecosbtcode(String _secosbtcode) {
        this.secosbtcode = _secosbtcode;
    }


    public BigDecimal getFamt()
    {
        return famt;
    }
     /**
     *   Setter F_AMT        * */
    public void setFamt(BigDecimal _famt) {
        this.famt = _famt;
    }


    public String getCacctprop()
    {
        return cacctprop;
    }
     /**
     *   Setter C_ACCTPROP        * */
    public void setCacctprop(String _cacctprop) {
        this.cacctprop = _cacctprop;
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
    *   Getter I_VOUSRLNO, PK , NOT NULL       * */
    public static String  columnIvousrlno()
    {
        return "I_VOUSRLNO";
    }
   
    /**
    *   Getter I_GRPINNERSEQNO, PK , NOT NULL       * */
    public static String  columnIgrpinnerseqno()
    {
        return "I_GRPINNERSEQNO";
    }
   
    /**
    *   Getter S_BOOKORGCODE       * */
    public static String  columnSbookorgcode()
    {
        return "S_BOOKORGCODE";
    }
   
    /**
    *   Getter S_BDGORGCODE       * */
    public static String  columnSbdgorgcode()
    {
        return "S_BDGORGCODE";
    }
   
    /**
    *   Getter S_ORIVOUNO       * */
    public static String  columnSorivouno()
    {
        return "S_ORIVOUNO";
    }
   
    /**
    *   Getter D_ORIVOUDATE       * */
    public static String  columnDorivoudate()
    {
        return "D_ORIVOUDATE";
    }
   
    /**
    *   Getter S_FUNCSBTCODE       * */
    public static String  columnSfuncsbtcode()
    {
        return "S_FUNCSBTCODE";
    }
   
    /**
    *   Getter S_ECOSBTCODE       * */
    public static String  columnSecosbtcode()
    {
        return "S_ECOSBTCODE";
    }
   
    /**
    *   Getter F_AMT       * */
    public static String  columnFamt()
    {
        return "F_AMT";
    }
   
    /**
    *   Getter C_ACCTPROP       * */
    public static String  columnCacctprop()
    {
        return "C_ACCTPROP";
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
        return "TBS_TV_BNKPAY_SUB";
    }
    
    /**
    *  Columns
    */
    public static String[] columnNames(){
        String[] columnNames = new String[11];        
        columnNames[0]="I_VOUSRLNO";
        columnNames[1]="I_GRPINNERSEQNO";
        columnNames[2]="S_BOOKORGCODE";
        columnNames[3]="S_BDGORGCODE";
        columnNames[4]="S_ORIVOUNO";
        columnNames[5]="D_ORIVOUDATE";
        columnNames[6]="S_FUNCSBTCODE";
        columnNames[7]="S_ECOSBTCODE";
        columnNames[8]="F_AMT";
        columnNames[9]="C_ACCTPROP";
        columnNames[10]="TS_SYSUPDATE";
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

        if (obj == null || !(obj instanceof TbsTvBnkpaySubDto))
            return false;

        TbsTvBnkpaySubDto bean = (TbsTvBnkpaySubDto) obj;


      //compare field ivousrlno
      if((this.ivousrlno==null && bean.ivousrlno!=null) || (this.ivousrlno!=null && bean.ivousrlno==null))
          return false;
        else if(this.ivousrlno==null && bean.ivousrlno==null){
        }
        else{
          if(!bean.ivousrlno.equals(this.ivousrlno))
               return false;
     }
      //compare field igrpinnerseqno
      if((this.igrpinnerseqno==null && bean.igrpinnerseqno!=null) || (this.igrpinnerseqno!=null && bean.igrpinnerseqno==null))
          return false;
        else if(this.igrpinnerseqno==null && bean.igrpinnerseqno==null){
        }
        else{
          if(!bean.igrpinnerseqno.equals(this.igrpinnerseqno))
               return false;
     }
      //compare field sbookorgcode
      if((this.sbookorgcode==null && bean.sbookorgcode!=null) || (this.sbookorgcode!=null && bean.sbookorgcode==null))
          return false;
        else if(this.sbookorgcode==null && bean.sbookorgcode==null){
        }
        else{
          if(!bean.sbookorgcode.equals(this.sbookorgcode))
               return false;
     }
      //compare field sbdgorgcode
      if((this.sbdgorgcode==null && bean.sbdgorgcode!=null) || (this.sbdgorgcode!=null && bean.sbdgorgcode==null))
          return false;
        else if(this.sbdgorgcode==null && bean.sbdgorgcode==null){
        }
        else{
          if(!bean.sbdgorgcode.equals(this.sbdgorgcode))
               return false;
     }
      //compare field sorivouno
      if((this.sorivouno==null && bean.sorivouno!=null) || (this.sorivouno!=null && bean.sorivouno==null))
          return false;
        else if(this.sorivouno==null && bean.sorivouno==null){
        }
        else{
          if(!bean.sorivouno.equals(this.sorivouno))
               return false;
     }
      //compare field dorivoudate
      if((this.dorivoudate==null && bean.dorivoudate!=null) || (this.dorivoudate!=null && bean.dorivoudate==null))
          return false;
        else if(this.dorivoudate==null && bean.dorivoudate==null){
        }
        else{
          if(!bean.dorivoudate.equals(this.dorivoudate))
               return false;
     }
      //compare field sfuncsbtcode
      if((this.sfuncsbtcode==null && bean.sfuncsbtcode!=null) || (this.sfuncsbtcode!=null && bean.sfuncsbtcode==null))
          return false;
        else if(this.sfuncsbtcode==null && bean.sfuncsbtcode==null){
        }
        else{
          if(!bean.sfuncsbtcode.equals(this.sfuncsbtcode))
               return false;
     }
      //compare field secosbtcode
      if((this.secosbtcode==null && bean.secosbtcode!=null) || (this.secosbtcode!=null && bean.secosbtcode==null))
          return false;
        else if(this.secosbtcode==null && bean.secosbtcode==null){
        }
        else{
          if(!bean.secosbtcode.equals(this.secosbtcode))
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
      //compare field cacctprop
      if((this.cacctprop==null && bean.cacctprop!=null) || (this.cacctprop!=null && bean.cacctprop==null))
          return false;
        else if(this.cacctprop==null && bean.cacctprop==null){
        }
        else{
          if(!bean.cacctprop.equals(this.cacctprop))
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
		
        if(this.ivousrlno!=null)
          _hash_ = _hash_ * 31+ this.ivousrlno.hashCode() ;
        if(this.igrpinnerseqno!=null)
          _hash_ = _hash_ * 31+ this.igrpinnerseqno.hashCode() ;
        if(this.sbookorgcode!=null)
          _hash_ = _hash_ * 31+ this.sbookorgcode.hashCode() ;
        if(this.sbdgorgcode!=null)
          _hash_ = _hash_ * 31+ this.sbdgorgcode.hashCode() ;
        if(this.sorivouno!=null)
          _hash_ = _hash_ * 31+ this.sorivouno.hashCode() ;
        if(this.dorivoudate!=null)
          _hash_ = _hash_ * 31+ this.dorivoudate.hashCode() ;
        if(this.sfuncsbtcode!=null)
          _hash_ = _hash_ * 31+ this.sfuncsbtcode.hashCode() ;
        if(this.secosbtcode!=null)
          _hash_ = _hash_ * 31+ this.secosbtcode.hashCode() ;
        if(this.famt!=null)
          _hash_ = _hash_ * 31+ this.famt.hashCode() ;
        if(this.cacctprop!=null)
          _hash_ = _hash_ * 31+ this.cacctprop.hashCode() ;
        if(this.tssysupdate!=null)
          _hash_ = _hash_ * 31+ this.tssysupdate.hashCode() ;

		return _hash_;
	
	}

     /* Creates and returns a copy of this object. */
    public Object clone()
    {
        TbsTvBnkpaySubDto bean = new TbsTvBnkpaySubDto();

          bean.ivousrlno = this.ivousrlno;

          bean.igrpinnerseqno = this.igrpinnerseqno;

          if (this.sbookorgcode != null)
            bean.sbookorgcode = new String(this.sbookorgcode);
          if (this.sbdgorgcode != null)
            bean.sbdgorgcode = new String(this.sbdgorgcode);
          if (this.sorivouno != null)
            bean.sorivouno = new String(this.sorivouno);
          if (this.dorivoudate != null)
            bean.dorivoudate = (Date) this.dorivoudate.clone();
          if (this.sfuncsbtcode != null)
            bean.sfuncsbtcode = new String(this.sfuncsbtcode);
          if (this.secosbtcode != null)
            bean.secosbtcode = new String(this.secosbtcode);
          if (this.famt != null)
            bean.famt = new BigDecimal(this.famt.toString());
          if (this.cacctprop != null)
            bean.cacctprop = new String(this.cacctprop);
          if (this.tssysupdate != null)
            bean.tssysupdate = (Timestamp) this.tssysupdate.clone();
  
        return bean;
    }


   /* Returns a string representation of the object. */
    public String toString()
    {
        String sep = "; ";
        StringBuffer sb = new StringBuffer();
        sb.append("TbsTvBnkpaySubDto").append(sep);
        sb.append("[ivousrlno]").append(" = ").append(ivousrlno).append(sep);
        sb.append("[igrpinnerseqno]").append(" = ").append(igrpinnerseqno).append(sep);
        sb.append("[sbookorgcode]").append(" = ").append(sbookorgcode).append(sep);
        sb.append("[sbdgorgcode]").append(" = ").append(sbdgorgcode).append(sep);
        sb.append("[sorivouno]").append(" = ").append(sorivouno).append(sep);
        sb.append("[dorivoudate]").append(" = ").append(dorivoudate).append(sep);
        sb.append("[sfuncsbtcode]").append(" = ").append(sfuncsbtcode).append(sep);
        sb.append("[secosbtcode]").append(" = ").append(secosbtcode).append(sep);
        sb.append("[famt]").append(" = ").append(famt).append(sep);
        sb.append("[cacctprop]").append(" = ").append(cacctprop).append(sep);
        sb.append("[tssysupdate]").append(" = ").append(tssysupdate).append(sep);
            return sb.toString();
    }

  /* Returns value valid checking String , NULL is Valid*/
   public String  checkValid()
  {
  	StringBuffer sb = new StringBuffer() ;

    //check field I_VOUSRLNO
      if (this.getIvousrlno()==null)
             sb.append("I_VOUSRLNO����Ϊ��; ");
      
    //check field I_GRPINNERSEQNO
      if (this.getIgrpinnerseqno()==null)
             sb.append("I_GRPINNERSEQNO����Ϊ��; ");
      
    //check field S_BOOKORGCODE
      if (this.getSbookorgcode()!=null)
             if (this.getSbookorgcode().getBytes().length > 12)
                sb.append("S_BOOKORGCODE���Ȳ��ܳ��� "+12+"���ַ�; ");
    
    //check field S_BDGORGCODE
      if (this.getSbdgorgcode()!=null)
             if (this.getSbdgorgcode().getBytes().length > 15)
                sb.append("S_BDGORGCODE���Ȳ��ܳ��� "+15+"���ַ�; ");
    
    //check field S_ORIVOUNO
      if (this.getSorivouno()!=null)
             if (this.getSorivouno().getBytes().length > 22)
                sb.append("S_ORIVOUNO���Ȳ��ܳ��� "+22+"���ַ�; ");
    
    //check field D_ORIVOUDATE
      
    //check field S_FUNCSBTCODE
      if (this.getSfuncsbtcode()!=null)
             if (this.getSfuncsbtcode().getBytes().length > 30)
                sb.append("S_FUNCSBTCODE���Ȳ��ܳ��� "+30+"���ַ�; ");
    
    //check field S_ECOSBTCODE
      if (this.getSecosbtcode()!=null)
             if (this.getSecosbtcode().getBytes().length > 30)
                sb.append("S_ECOSBTCODE���Ȳ��ܳ��� "+30+"���ַ�; ");
    
    //check field F_AMT
      
    //check field C_ACCTPROP
      if (this.getCacctprop()!=null)
             if (this.getCacctprop().getBytes().length > 1)
                sb.append("C_ACCTPROP���Ȳ��ܳ��� "+1+"���ַ�; ");
    
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
    //check field I_VOUSRLNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("I_VOUSRLNO")) {
               if (this.getIvousrlno()==null)
                    sb.append("I_VOUSRLNO ����Ϊ��; ");
               break;
         }
  }
    
    //check field I_GRPINNERSEQNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("I_GRPINNERSEQNO")) {
               if (this.getIgrpinnerseqno()==null)
                    sb.append("I_GRPINNERSEQNO ����Ϊ��; ");
               break;
         }
  }
    
    //check field S_BOOKORGCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BOOKORGCODE")) {
                 if (this.getSbookorgcode()!=null)
                    if (this.getSbookorgcode().getBytes().length > 12)
                        sb.append("S_BOOKORGCODE ���Ȳ��ܳ��� "+12+"���ַ�");
             break;
         }
  }
    
    //check field S_BDGORGCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_BDGORGCODE")) {
                 if (this.getSbdgorgcode()!=null)
                    if (this.getSbdgorgcode().getBytes().length > 15)
                        sb.append("S_BDGORGCODE ���Ȳ��ܳ��� "+15+"���ַ�");
             break;
         }
  }
    
    //check field S_ORIVOUNO
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_ORIVOUNO")) {
                 if (this.getSorivouno()!=null)
                    if (this.getSorivouno().getBytes().length > 22)
                        sb.append("S_ORIVOUNO ���Ȳ��ܳ��� "+22+"���ַ�");
             break;
         }
  }
    
    //check field D_ORIVOUDATE
          
    //check field S_FUNCSBTCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_FUNCSBTCODE")) {
                 if (this.getSfuncsbtcode()!=null)
                    if (this.getSfuncsbtcode().getBytes().length > 30)
                        sb.append("S_FUNCSBTCODE ���Ȳ��ܳ��� "+30+"���ַ�");
             break;
         }
  }
    
    //check field S_ECOSBTCODE
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("S_ECOSBTCODE")) {
                 if (this.getSecosbtcode()!=null)
                    if (this.getSecosbtcode().getBytes().length > 30)
                        sb.append("S_ECOSBTCODE ���Ȳ��ܳ��� "+30+"���ַ�");
             break;
         }
  }
    
    //check field F_AMT
          
    //check field C_ACCTPROP
     for (int i = 0; i < _columnNames.length; i++) {
        if (((String) _columnNames[i]).equals("C_ACCTPROP")) {
                 if (this.getCacctprop()!=null)
                    if (this.getCacctprop().getBytes().length > 1)
                        sb.append("C_ACCTPROP ���Ȳ��ܳ��� "+1+"���ַ�");
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
      TbsTvBnkpaySubPK pk = new TbsTvBnkpaySubPK();
      pk.setIvousrlno(getIvousrlno());
      pk.setIgrpinnerseqno(getIgrpinnerseqno());
      return pk;
    };
}
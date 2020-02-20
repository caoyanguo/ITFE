    



package com.cfcc.itfe.persistence.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp ;
import java.sql.Clob;

import com.cfcc.jaf.persistence.jaform.parent.* ;

import java.math.BigDecimal;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import java.io.StringReader;
import java.io.Reader;
import java.io.IOException;

import com.cfcc.itfe.persistence.dto.HtfReportIncomeSubDto ;
import com.cfcc.itfe.persistence.pk.HtfReportIncomeSubPK ;


/**
 * <p>Title: DAO of table: HTF_REPORT_INCOME_SUB</p>
 * <p>Description:���뱨�������ӱ���ʷ��3511 Data Access Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:55 </p>
 *  Do NOT change this file by hand.
 *  This java source file is generated by JAF_ORM .
 *  dao.vm timestamp: 2007-04-06 08:30:00
 ******************************************************
 * <p> <b>change record</b><br>
 *  make dao methods simply.<br>
 *  especially make update,delete methods simply.<br>
 *  remove "..withCheck" method,withCheck will impl in JAFOrmTemplate.<br>
 *  remove "delete(IDto _dto,..)" method.<br></p>
 * @author win7
 */

public class HtfReportIncomeSubDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO HTF_REPORT_INCOME_SUB ("
          + "I_VOUSRLNO,I_SEQNO,S_ID,S_ADMDIVCODE,S_STYEAR"
          + ",S_TAXORGCODE,S_TAXORGNAME,S_BUDGETTYPE,S_BUDGETLEVELCODE,S_BUDGETSUBJECTCODE"
          + ",S_BUDGETSUBJECTNAME,N_CURINCOMEAMT,N_SUMINCOMEAMT,S_XCHECKRESULT,S_XCHECKREASON"
          + ",S_HOLD1,S_HOLD2,S_HOLD3,S_HOLD4,S_EXT1"
          + ",S_EXT2,S_EXT3,S_EXT4,S_EXT5"
        + ") VALUES ("
        + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "HTF_REPORT_INCOME_SUB.I_VOUSRLNO, HTF_REPORT_INCOME_SUB.I_SEQNO, HTF_REPORT_INCOME_SUB.S_ID, HTF_REPORT_INCOME_SUB.S_ADMDIVCODE, HTF_REPORT_INCOME_SUB.S_STYEAR, "
        + "HTF_REPORT_INCOME_SUB.S_TAXORGCODE, HTF_REPORT_INCOME_SUB.S_TAXORGNAME, HTF_REPORT_INCOME_SUB.S_BUDGETTYPE, HTF_REPORT_INCOME_SUB.S_BUDGETLEVELCODE, HTF_REPORT_INCOME_SUB.S_BUDGETSUBJECTCODE, "
        + "HTF_REPORT_INCOME_SUB.S_BUDGETSUBJECTNAME, HTF_REPORT_INCOME_SUB.N_CURINCOMEAMT, HTF_REPORT_INCOME_SUB.N_SUMINCOMEAMT, HTF_REPORT_INCOME_SUB.S_XCHECKRESULT, HTF_REPORT_INCOME_SUB.S_XCHECKREASON, "
        + "HTF_REPORT_INCOME_SUB.S_HOLD1, HTF_REPORT_INCOME_SUB.S_HOLD2, HTF_REPORT_INCOME_SUB.S_HOLD3, HTF_REPORT_INCOME_SUB.S_HOLD4, HTF_REPORT_INCOME_SUB.S_EXT1, "
        + "HTF_REPORT_INCOME_SUB.S_EXT2, HTF_REPORT_INCOME_SUB.S_EXT3, HTF_REPORT_INCOME_SUB.S_EXT4, HTF_REPORT_INCOME_SUB.S_EXT5 "
        + "FROM HTF_REPORT_INCOME_SUB "
        +" WHERE " 
        + "I_VOUSRLNO = ? AND I_SEQNO = ?"
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "HTF_REPORT_INCOME_SUB.I_VOUSRLNO, HTF_REPORT_INCOME_SUB.I_SEQNO, HTF_REPORT_INCOME_SUB.S_ID, HTF_REPORT_INCOME_SUB.S_ADMDIVCODE, HTF_REPORT_INCOME_SUB.S_STYEAR, "
        + "HTF_REPORT_INCOME_SUB.S_TAXORGCODE, HTF_REPORT_INCOME_SUB.S_TAXORGNAME, HTF_REPORT_INCOME_SUB.S_BUDGETTYPE, HTF_REPORT_INCOME_SUB.S_BUDGETLEVELCODE, HTF_REPORT_INCOME_SUB.S_BUDGETSUBJECTCODE, "
        + "HTF_REPORT_INCOME_SUB.S_BUDGETSUBJECTNAME, HTF_REPORT_INCOME_SUB.N_CURINCOMEAMT, HTF_REPORT_INCOME_SUB.N_SUMINCOMEAMT, HTF_REPORT_INCOME_SUB.S_XCHECKRESULT, HTF_REPORT_INCOME_SUB.S_XCHECKREASON, "
        + "HTF_REPORT_INCOME_SUB.S_HOLD1, HTF_REPORT_INCOME_SUB.S_HOLD2, HTF_REPORT_INCOME_SUB.S_HOLD3, HTF_REPORT_INCOME_SUB.S_HOLD4, HTF_REPORT_INCOME_SUB.S_EXT1, "
        + "HTF_REPORT_INCOME_SUB.S_EXT2, HTF_REPORT_INCOME_SUB.S_EXT3, HTF_REPORT_INCOME_SUB.S_EXT4, HTF_REPORT_INCOME_SUB.S_EXT5 "
        + "FROM HTF_REPORT_INCOME_SUB "
        +" WHERE " 
        + "I_VOUSRLNO = ? AND I_SEQNO = ? FOR UPDATE"
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  HTF_REPORT_INCOME_SUB.I_VOUSRLNO  , HTF_REPORT_INCOME_SUB.I_SEQNO  , HTF_REPORT_INCOME_SUB.S_ID  , HTF_REPORT_INCOME_SUB.S_ADMDIVCODE  , HTF_REPORT_INCOME_SUB.S_STYEAR "
        + " , HTF_REPORT_INCOME_SUB.S_TAXORGCODE  , HTF_REPORT_INCOME_SUB.S_TAXORGNAME  , HTF_REPORT_INCOME_SUB.S_BUDGETTYPE  , HTF_REPORT_INCOME_SUB.S_BUDGETLEVELCODE  , HTF_REPORT_INCOME_SUB.S_BUDGETSUBJECTCODE "
        + " , HTF_REPORT_INCOME_SUB.S_BUDGETSUBJECTNAME  , HTF_REPORT_INCOME_SUB.N_CURINCOMEAMT  , HTF_REPORT_INCOME_SUB.N_SUMINCOMEAMT  , HTF_REPORT_INCOME_SUB.S_XCHECKRESULT  , HTF_REPORT_INCOME_SUB.S_XCHECKREASON "
        + " , HTF_REPORT_INCOME_SUB.S_HOLD1  , HTF_REPORT_INCOME_SUB.S_HOLD2  , HTF_REPORT_INCOME_SUB.S_HOLD3  , HTF_REPORT_INCOME_SUB.S_HOLD4  , HTF_REPORT_INCOME_SUB.S_EXT1 "
        + " , HTF_REPORT_INCOME_SUB.S_EXT2  , HTF_REPORT_INCOME_SUB.S_EXT3  , HTF_REPORT_INCOME_SUB.S_EXT4  , HTF_REPORT_INCOME_SUB.S_EXT5 "
        + "FROM HTF_REPORT_INCOME_SUB ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "HTF_REPORT_INCOME_SUB.I_VOUSRLNO, HTF_REPORT_INCOME_SUB.I_SEQNO, HTF_REPORT_INCOME_SUB.S_ID, HTF_REPORT_INCOME_SUB.S_ADMDIVCODE, HTF_REPORT_INCOME_SUB.S_STYEAR, "
        + "HTF_REPORT_INCOME_SUB.S_TAXORGCODE, HTF_REPORT_INCOME_SUB.S_TAXORGNAME, HTF_REPORT_INCOME_SUB.S_BUDGETTYPE, HTF_REPORT_INCOME_SUB.S_BUDGETLEVELCODE, HTF_REPORT_INCOME_SUB.S_BUDGETSUBJECTCODE, "
        + "HTF_REPORT_INCOME_SUB.S_BUDGETSUBJECTNAME, HTF_REPORT_INCOME_SUB.N_CURINCOMEAMT, HTF_REPORT_INCOME_SUB.N_SUMINCOMEAMT, HTF_REPORT_INCOME_SUB.S_XCHECKRESULT, HTF_REPORT_INCOME_SUB.S_XCHECKREASON, "
        + "HTF_REPORT_INCOME_SUB.S_HOLD1, HTF_REPORT_INCOME_SUB.S_HOLD2, HTF_REPORT_INCOME_SUB.S_HOLD3, HTF_REPORT_INCOME_SUB.S_HOLD4, HTF_REPORT_INCOME_SUB.S_EXT1, "
        + "HTF_REPORT_INCOME_SUB.S_EXT2, HTF_REPORT_INCOME_SUB.S_EXT3, HTF_REPORT_INCOME_SUB.S_EXT4, HTF_REPORT_INCOME_SUB.S_EXT5 "
        + "FROM HTF_REPORT_INCOME_SUB " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        + "I_VOUSRLNO = ? AND I_SEQNO = ?)"
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE HTF_REPORT_INCOME_SUB SET "
        + "S_ID =?,S_ADMDIVCODE =?,S_STYEAR =?,S_TAXORGCODE =?,S_TAXORGNAME =?, "
        + "S_BUDGETTYPE =?,S_BUDGETLEVELCODE =?,S_BUDGETSUBJECTCODE =?,S_BUDGETSUBJECTNAME =?,N_CURINCOMEAMT =?, "
        + "N_SUMINCOMEAMT =?,S_XCHECKRESULT =?,S_XCHECKREASON =?,S_HOLD1 =?,S_HOLD2 =?, "
        + "S_HOLD3 =?,S_HOLD4 =?,S_EXT1 =?,S_EXT2 =?,S_EXT3 =?, "
        + "S_EXT4 =?,S_EXT5 =? "
        + "WHERE "
        + "I_VOUSRLNO = ? AND I_SEQNO = ?"
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE HTF_REPORT_INCOME_SUB SET "
        + "S_ID =?, S_ADMDIVCODE =?, S_STYEAR =?, S_TAXORGCODE =?, S_TAXORGNAME =?,  "
        + "S_BUDGETTYPE =?, S_BUDGETLEVELCODE =?, S_BUDGETSUBJECTCODE =?, S_BUDGETSUBJECTNAME =?, N_CURINCOMEAMT =?,  "
        + "N_SUMINCOMEAMT =?, S_XCHECKRESULT =?, S_XCHECKREASON =?, S_HOLD1 =?, S_HOLD2 =?,  "
        + "S_HOLD3 =?, S_HOLD4 =?, S_EXT1 =?, S_EXT2 =?, S_EXT3 =?,  "
        + "S_EXT4 =?, S_EXT5 =? "
        + "WHERE "
        + "I_VOUSRLNO = ? AND I_SEQNO = ?"
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM HTF_REPORT_INCOME_SUB " 
        + " WHERE "
        + "I_VOUSRLNO = ? AND I_SEQNO = ?"
        ;


	/**
	*  ������ѯ һ������ѯ�Ĳ�������
	*/
	
	public static final int FIND_BATCH_SIZE = 150  / 2;
	



   /**
   * Create a new record in Database.
   */
  public void create(IDto _dto,  Connection conn) throws SQLException
  {
     HtfReportIncomeSubDto dto = (HtfReportIncomeSubDto)_dto ;
     String msgValid = dto.checkValid() ;
     if (msgValid != null)
     	throw new SQLException("�������"+msgValid) ;

     PreparedStatement ps = null;
     try
     {
         ps = conn.prepareStatement(SQL_INSERT);
          if (dto.getIvousrlno()==null)
            ps.setNull(1, java.sql.Types.BIGINT);
         else
            ps.setLong(1, dto.getIvousrlno().longValue());
          if (dto.getIseqno()==null)
            ps.setNull(2, java.sql.Types.BIGINT);
         else
            ps.setLong(2, dto.getIseqno().longValue());
          ps.setString(3, dto.getSid());

          ps.setString(4, dto.getSadmdivcode());

          ps.setString(5, dto.getSstyear());

          ps.setString(6, dto.getStaxorgcode());

          ps.setString(7, dto.getStaxorgname());

          ps.setString(8, dto.getSbudgettype());

          ps.setString(9, dto.getSbudgetlevelcode());

          ps.setString(10, dto.getSbudgetsubjectcode());

          ps.setString(11, dto.getSbudgetsubjectname());

          ps.setBigDecimal(12, dto.getNcurincomeamt());

          ps.setBigDecimal(13, dto.getNsumincomeamt());

          ps.setString(14, dto.getSxcheckresult());

          ps.setString(15, dto.getSxcheckreason());

          ps.setString(16, dto.getShold1());

          ps.setString(17, dto.getShold2());

          ps.setString(18, dto.getShold3());

          ps.setString(19, dto.getShold4());

          ps.setString(20, dto.getSext1());

          ps.setString(21, dto.getSext2());

          ps.setString(22, dto.getSext3());

          ps.setString(23, dto.getSext4());

          ps.setString(24, dto.getSext5());

         ps.executeUpdate();
     }
     finally
     {
         close(ps);
     }
     return  ;
  }

   /**
   * Create a new record in Database. and return result, only used when having generated key
   */
   public IDto createWithResult(IDto _dto,  Connection conn) throws SQLException
   {
       HtfReportIncomeSubDto dto = (HtfReportIncomeSubDto)_dto ;
       String msgValid = dto.checkValid() ;
       if (msgValid != null)
           throw new SQLException("�������"+msgValid) ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try
       {
           ps = conn.prepareStatement(SQL_INSERT_WITH_RESULT);
            if (dto.getIvousrlno()==null)
              ps.setNull(1, java.sql.Types.BIGINT);
           else
              ps.setLong(1, dto.getIvousrlno().longValue());
            if (dto.getIseqno()==null)
              ps.setNull(2, java.sql.Types.BIGINT);
           else
              ps.setLong(2, dto.getIseqno().longValue());
            ps.setString(3, dto.getSid());
            ps.setString(4, dto.getSadmdivcode());
            ps.setString(5, dto.getSstyear());
            ps.setString(6, dto.getStaxorgcode());
            ps.setString(7, dto.getStaxorgname());
            ps.setString(8, dto.getSbudgettype());
            ps.setString(9, dto.getSbudgetlevelcode());
            ps.setString(10, dto.getSbudgetsubjectcode());
            ps.setString(11, dto.getSbudgetsubjectname());
            ps.setBigDecimal(12, dto.getNcurincomeamt());
            ps.setBigDecimal(13, dto.getNsumincomeamt());
            ps.setString(14, dto.getSxcheckresult());
            ps.setString(15, dto.getSxcheckreason());
            ps.setString(16, dto.getShold1());
            ps.setString(17, dto.getShold2());
            ps.setString(18, dto.getShold3());
            ps.setString(19, dto.getShold4());
            ps.setString(20, dto.getSext1());
            ps.setString(21, dto.getSext2());
            ps.setString(22, dto.getSext3());
            ps.setString(23, dto.getSext4());
            ps.setString(24, dto.getSext5());
           rs = ps.executeQuery();
           List results = getResults(rs);
           if (results!=null && results.size() > 0)
               return (IDto)results.get(0);
           else
               return null;
        }
        finally
        {
           close(ps);
           close(rs);
        }
   }

 	/**
	* Create batch new record in Database. it only create the first layer dto
	*/
    public void create(IDto[] _dtos, Connection conn)    throws SQLException
    {
        HtfReportIncomeSubDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (HtfReportIncomeSubDto)_dtos[i] ; 
    	    String msgValid = dto.checkValid() ;
        	if (msgValid != null)
     	    	throw new SQLException("�������"+msgValid) ;
     	}
 	    PreparedStatement ps = null;
        try
        {
            ps = conn.prepareStatement(SQL_INSERT);
       
     	    for (int i= 0; i< _dtos.length ; i++)
     	    {
 	    	    dto  = (HtfReportIncomeSubDto)_dtos[i] ; 
  
               if (dto.getIvousrlno()==null)
                  ps.setNull(1, java.sql.Types.BIGINT);
               else
                  ps.setLong(1, dto.getIvousrlno().longValue());
  
               if (dto.getIseqno()==null)
                  ps.setNull(2, java.sql.Types.BIGINT);
               else
                  ps.setLong(2, dto.getIseqno().longValue());
  
               ps.setString(3, dto.getSid());
  
               ps.setString(4, dto.getSadmdivcode());
  
               ps.setString(5, dto.getSstyear());
  
               ps.setString(6, dto.getStaxorgcode());
  
               ps.setString(7, dto.getStaxorgname());
  
               ps.setString(8, dto.getSbudgettype());
  
               ps.setString(9, dto.getSbudgetlevelcode());
  
               ps.setString(10, dto.getSbudgetsubjectcode());
  
               ps.setString(11, dto.getSbudgetsubjectname());
  
               ps.setBigDecimal(12, dto.getNcurincomeamt());
  
               ps.setBigDecimal(13, dto.getNsumincomeamt());
  
               ps.setString(14, dto.getSxcheckresult());
  
               ps.setString(15, dto.getSxcheckreason());
  
               ps.setString(16, dto.getShold1());
  
               ps.setString(17, dto.getShold2());
  
               ps.setString(18, dto.getShold3());
  
               ps.setString(19, dto.getShold4());
  
               ps.setString(20, dto.getSext1());
  
               ps.setString(21, dto.getSext2());
  
               ps.setString(22, dto.getSext3());
  
               ps.setString(23, dto.getSext4());
  
               ps.setString(24, dto.getSext5());
               ps.addBatch(); 
           }
	
           ps.executeBatch();
        }
        finally
        {
           close(ps);
        }
        return  ;
   }




     /**
     * Retrive a record from Database.
     * (the statement below is somewhat redundant, but doesnot matter)
     */
    public IDto find(IPK _pk, Connection conn, boolean isLobSupport) throws SQLException
    {
       String msgValid = _pk.checkValid() ;
       if (msgValid != null)
       {
       		return null ;
        //	throw new SQLException("���Ҵ���"+msgValid) ;
       }
        	
       HtfReportIncomeSubPK pk = (HtfReportIncomeSubPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT);
           if (pk.getIvousrlno()==null)
               ps.setNull(1, java.sql.Types.BIGINT);
           else
               ps.setLong(1, pk.getIvousrlno().longValue());

           if (pk.getIseqno()==null)
               ps.setNull(2, java.sql.Types.BIGINT);
           else
               ps.setLong(2, pk.getIseqno().longValue());

           rs = ps.executeQuery();
           List results = getResults(rs,0,isLobSupport);
           if (results!=null && results.size() > 0)
               return (IDto)results.get(0);
           else
               return null;
        }finally {
           close(rs);
           close(ps);
        }
    }

     /**
     * Retrive a record from Database.
     * (the statement below is somewhat redundant, but doesnot matter)
     */
    public IDto findForUpdate(IPK _pk, Connection conn, boolean isLobSupport) throws SQLException
    {
       String msgValid = _pk.checkValid() ;
       if (msgValid != null)
       {
       		return null ;
        //	throw new SQLException("���Ҵ���"+msgValid) ;
       }
        	
       HtfReportIncomeSubPK pk = (HtfReportIncomeSubPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT_FOR_UPDATE);
           if (pk.getIvousrlno()==null)
               ps.setNull(1, java.sql.Types.BIGINT);
           else
               ps.setLong(1, pk.getIvousrlno().longValue());

           if (pk.getIseqno()==null)
               ps.setNull(2, java.sql.Types.BIGINT);
           else
               ps.setLong(2, pk.getIseqno().longValue());

           rs = ps.executeQuery();
           List results = getResults(rs,0,isLobSupport);
           if (results!=null && results.size() > 0)
               return (IDto)results.get(0);
           else
               return null;
        }finally {
           close(rs);
           close(ps);
        }
    }
  
     /**
     * Retrive a record from Database.
     * (the statement below is somewhat redundant, but doesnot matter)
     */
    public IDto[] find(IPK[] _pks, Connection conn, boolean isLobSupport) throws SQLException
    {
        HtfReportIncomeSubPK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (HtfReportIncomeSubPK)_pks[i] ; 
	    	String msgValid = pk.checkValid() ;
    		if (msgValid == null)
    		{
	    		pks.add(pk) ;
     		}
 		}
 
        PreparedStatement ps = null;
        ResultSet rs = null;
        
         
        List results = new ArrayList();  
		for (int iBegin = 0; iBegin < pks.size(); iBegin += FIND_BATCH_SIZE)   //
		{
        
            try {
          		int iFindBatchSize = pks.size() - iBegin ;
	    		if (iFindBatchSize >FIND_BATCH_SIZE )
		    		iFindBatchSize = FIND_BATCH_SIZE ;
		  
        	    StringBuffer sb = new StringBuffer() ;
            	sb.append(SQL_SELECT_BATCH).append(" WHERE ").append(SQL_SELECT_BATCH_WHERE);

	    		for (int i = iBegin+1; i < iBegin + iFindBatchSize; i++)
		    	{
			    	sb.append("OR").append(SQL_SELECT_BATCH_WHERE) ;
    			}
        	
                ps = conn.prepareStatement(sb.toString());

                for (int i = iBegin;i < iBegin + iFindBatchSize; i++)
                {
                    pk  = (HtfReportIncomeSubPK)(pks.get(i)) ; 
                   if (pk.getIvousrlno()==null)
                      ps.setNull((i-iBegin)*2+1, java.sql.Types.BIGINT);
                   else
                      ps.setLong((i-iBegin)*2+1, pk.getIvousrlno().longValue());

                   if (pk.getIseqno()==null)
                      ps.setNull((i-iBegin)*2+2, java.sql.Types.BIGINT);
                   else
                      ps.setLong((i-iBegin)*2+2, pk.getIseqno().longValue());

			
	            }
                rs = ps.executeQuery();
                results.addAll(getResults(rs,0,isLobSupport)); //
            }finally {
                close(rs);
                close(ps);
            }
        }
        if (results!=null && results.size() > 0)
        {
            HtfReportIncomeSubDto[] dtos = new HtfReportIncomeSubDto[0];
		    dtos = (HtfReportIncomeSubDto[]) results.toArray(dtos) ;
		    return dtos ;
        }
        else
        {
            return null;
        }
    }

  
  
  



    /**
	    * �õ���ѯ���
	    */
    public String getSelectSQL() 
    {
	        return SQL_SELECT_BATCH_SCROLLABLE ;
    }
   

	public List getResults(ResultSet rs) throws SQLException
    {
    	return getResults(rs, 0) ;
    }
  

    /**
     * Populate the ResultSet.
     */
    public List getResults(ResultSet rs, int maxSize) throws SQLException
    {
		return getResults(rs,maxSize,false);
	}
    /**
     * Populate the ResultSet.
     */
    public List getResults(ResultSet rs, int maxSize, boolean isLobSupport) throws SQLException
    {
        List results = new ArrayList();
        String str ;
        Reader reader;
        Clob clob;
        char[] chars ;
        
        while (rs.next())
         {
             HtfReportIncomeSubDto  dto = new  HtfReportIncomeSubDto ();
             //I_VOUSRLNO
             str = rs.getString("I_VOUSRLNO");
             if(str!=null){
                dto.setIvousrlno(new Long(str));
             }

             //I_SEQNO
             str = rs.getString("I_SEQNO");
             if(str!=null){
                dto.setIseqno(new Long(str));
             }

             //S_ID
             str = rs.getString("S_ID");
             if (str == null)
                dto.setSid(null);
             else
                dto.setSid(str.trim());

             //S_ADMDIVCODE
             str = rs.getString("S_ADMDIVCODE");
             if (str == null)
                dto.setSadmdivcode(null);
             else
                dto.setSadmdivcode(str.trim());

             //S_STYEAR
             str = rs.getString("S_STYEAR");
             if (str == null)
                dto.setSstyear(null);
             else
                dto.setSstyear(str.trim());

             //S_TAXORGCODE
             str = rs.getString("S_TAXORGCODE");
             if (str == null)
                dto.setStaxorgcode(null);
             else
                dto.setStaxorgcode(str.trim());

             //S_TAXORGNAME
             str = rs.getString("S_TAXORGNAME");
             if (str == null)
                dto.setStaxorgname(null);
             else
                dto.setStaxorgname(str.trim());

             //S_BUDGETTYPE
             str = rs.getString("S_BUDGETTYPE");
             if (str == null)
                dto.setSbudgettype(null);
             else
                dto.setSbudgettype(str.trim());

             //S_BUDGETLEVELCODE
             str = rs.getString("S_BUDGETLEVELCODE");
             if (str == null)
                dto.setSbudgetlevelcode(null);
             else
                dto.setSbudgetlevelcode(str.trim());

             //S_BUDGETSUBJECTCODE
             str = rs.getString("S_BUDGETSUBJECTCODE");
             if (str == null)
                dto.setSbudgetsubjectcode(null);
             else
                dto.setSbudgetsubjectcode(str.trim());

             //S_BUDGETSUBJECTNAME
             str = rs.getString("S_BUDGETSUBJECTNAME");
             if (str == null)
                dto.setSbudgetsubjectname(null);
             else
                dto.setSbudgetsubjectname(str.trim());

             //N_CURINCOMEAMT
           dto.setNcurincomeamt(rs.getBigDecimal("N_CURINCOMEAMT"));

             //N_SUMINCOMEAMT
           dto.setNsumincomeamt(rs.getBigDecimal("N_SUMINCOMEAMT"));

             //S_XCHECKRESULT
             str = rs.getString("S_XCHECKRESULT");
             if (str == null)
                dto.setSxcheckresult(null);
             else
                dto.setSxcheckresult(str.trim());

             //S_XCHECKREASON
             str = rs.getString("S_XCHECKREASON");
             if (str == null)
                dto.setSxcheckreason(null);
             else
                dto.setSxcheckreason(str.trim());

             //S_HOLD1
             str = rs.getString("S_HOLD1");
             if (str == null)
                dto.setShold1(null);
             else
                dto.setShold1(str.trim());

             //S_HOLD2
             str = rs.getString("S_HOLD2");
             if (str == null)
                dto.setShold2(null);
             else
                dto.setShold2(str.trim());

             //S_HOLD3
             str = rs.getString("S_HOLD3");
             if (str == null)
                dto.setShold3(null);
             else
                dto.setShold3(str.trim());

             //S_HOLD4
             str = rs.getString("S_HOLD4");
             if (str == null)
                dto.setShold4(null);
             else
                dto.setShold4(str.trim());

             //S_EXT1
             str = rs.getString("S_EXT1");
             if (str == null)
                dto.setSext1(null);
             else
                dto.setSext1(str.trim());

             //S_EXT2
             str = rs.getString("S_EXT2");
             if (str == null)
                dto.setSext2(null);
             else
                dto.setSext2(str.trim());

             //S_EXT3
             str = rs.getString("S_EXT3");
             if (str == null)
                dto.setSext3(null);
             else
                dto.setSext3(str.trim());

             //S_EXT4
             str = rs.getString("S_EXT4");
             if (str == null)
                dto.setSext4(null);
             else
                dto.setSext4(str.trim());

             //S_EXT5
             str = rs.getString("S_EXT5");
             if (str == null)
                dto.setSext5(null);
             else
                dto.setSext5(str.trim());



            results.add(dto);
            
            if(maxSize >0  && results.size() == maxSize)
            {
            	break ;
            }
            
         }
        
		return results ;
    }

  /**
   * populate resultSet as cclass
   */
  protected IDto[] getChildrenResults(ResultSet rs ,Class _dto) throws SQLException{

      throw new SQLException("��dtoû��������dto��������޷�Ӧ�ô˷���");
  }


     /**
     * Update a record in Database.
     */

    public void update(IDto _dto, Connection conn, boolean isLobSupport) throws SQLException
    {
        String msgValid = _dto.checkValid() ;
        if (msgValid != null)
            throw new SQLException("�޸Ĵ���"+msgValid) ;
        HtfReportIncomeSubDto dto = (HtfReportIncomeSubDto)_dto ;
        PreparedStatement ps = null;
        try {
            if(isLobSupport){
                ps = conn.prepareStatement(SQL_UPDATE_LOB);
            }
            else{
                ps = conn.prepareStatement(SQL_UPDATE);
            }
            int pos = 1;
            //S_ID
            ps.setString(pos, dto.getSid());
            pos++;

            //S_ADMDIVCODE
            ps.setString(pos, dto.getSadmdivcode());
            pos++;

            //S_STYEAR
            ps.setString(pos, dto.getSstyear());
            pos++;

            //S_TAXORGCODE
            ps.setString(pos, dto.getStaxorgcode());
            pos++;

            //S_TAXORGNAME
            ps.setString(pos, dto.getStaxorgname());
            pos++;

            //S_BUDGETTYPE
            ps.setString(pos, dto.getSbudgettype());
            pos++;

            //S_BUDGETLEVELCODE
            ps.setString(pos, dto.getSbudgetlevelcode());
            pos++;

            //S_BUDGETSUBJECTCODE
            ps.setString(pos, dto.getSbudgetsubjectcode());
            pos++;

            //S_BUDGETSUBJECTNAME
            ps.setString(pos, dto.getSbudgetsubjectname());
            pos++;

            //N_CURINCOMEAMT
            ps.setBigDecimal(pos, dto.getNcurincomeamt());
            pos++;

            //N_SUMINCOMEAMT
            ps.setBigDecimal(pos, dto.getNsumincomeamt());
            pos++;

            //S_XCHECKRESULT
            ps.setString(pos, dto.getSxcheckresult());
            pos++;

            //S_XCHECKREASON
            ps.setString(pos, dto.getSxcheckreason());
            pos++;

            //S_HOLD1
            ps.setString(pos, dto.getShold1());
            pos++;

            //S_HOLD2
            ps.setString(pos, dto.getShold2());
            pos++;

            //S_HOLD3
            ps.setString(pos, dto.getShold3());
            pos++;

            //S_HOLD4
            ps.setString(pos, dto.getShold4());
            pos++;

            //S_EXT1
            ps.setString(pos, dto.getSext1());
            pos++;

            //S_EXT2
            ps.setString(pos, dto.getSext2());
            pos++;

            //S_EXT3
            ps.setString(pos, dto.getSext3());
            pos++;

            //S_EXT4
            ps.setString(pos, dto.getSext4());
            pos++;

            //S_EXT5
            ps.setString(pos, dto.getSext5());
            pos++;


           //I_VOUSRLNO
           ps.setLong(pos, dto.getIvousrlno().longValue());
           pos++;
           //I_SEQNO
           ps.setLong(pos, dto.getIseqno().longValue());
           pos++;
           ps.executeUpdate();
        }
        finally
        {
           close(ps);
        }

        return ;
    }
    
    
    
      /**
     * Update batch record in Database.
     */

    public void update(IDto[] _dtos, Connection conn, boolean isLobSupport) throws SQLException
    {
     
     	 HtfReportIncomeSubDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (HtfReportIncomeSubDto)_dtos[i] ; 
            String msgValid = dto.checkValid() ;
            if (msgValid != null)
                throw new SQLException("�޸Ĵ���"+msgValid) ;
         }

        PreparedStatement ps = null;
        try {
            if(isLobSupport){
                ps = conn.prepareStatement(SQL_UPDATE_LOB);
            }
            else{
                ps = conn.prepareStatement(SQL_UPDATE);
            }
            
            for (int i= 0; i< _dtos.length ; i++)
            {
                dto  = (HtfReportIncomeSubDto)_dtos[i] ; 
                int pos = 1;
                //S_ID
                 ps.setString(pos, dto.getSid());
                pos++;

                //S_ADMDIVCODE
                 ps.setString(pos, dto.getSadmdivcode());
                pos++;

                //S_STYEAR
                 ps.setString(pos, dto.getSstyear());
                pos++;

                //S_TAXORGCODE
                 ps.setString(pos, dto.getStaxorgcode());
                pos++;

                //S_TAXORGNAME
                 ps.setString(pos, dto.getStaxorgname());
                pos++;

                //S_BUDGETTYPE
                 ps.setString(pos, dto.getSbudgettype());
                pos++;

                //S_BUDGETLEVELCODE
                 ps.setString(pos, dto.getSbudgetlevelcode());
                pos++;

                //S_BUDGETSUBJECTCODE
                 ps.setString(pos, dto.getSbudgetsubjectcode());
                pos++;

                //S_BUDGETSUBJECTNAME
                 ps.setString(pos, dto.getSbudgetsubjectname());
                pos++;

                //N_CURINCOMEAMT
                 ps.setBigDecimal(pos, dto.getNcurincomeamt());
                pos++;

                //N_SUMINCOMEAMT
                 ps.setBigDecimal(pos, dto.getNsumincomeamt());
                pos++;

                //S_XCHECKRESULT
                 ps.setString(pos, dto.getSxcheckresult());
                pos++;

                //S_XCHECKREASON
                 ps.setString(pos, dto.getSxcheckreason());
                pos++;

                //S_HOLD1
                 ps.setString(pos, dto.getShold1());
                pos++;

                //S_HOLD2
                 ps.setString(pos, dto.getShold2());
                pos++;

                //S_HOLD3
                 ps.setString(pos, dto.getShold3());
                pos++;

                //S_HOLD4
                 ps.setString(pos, dto.getShold4());
                pos++;

                //S_EXT1
                 ps.setString(pos, dto.getSext1());
                pos++;

                //S_EXT2
                 ps.setString(pos, dto.getSext2());
                pos++;

                //S_EXT3
                 ps.setString(pos, dto.getSext3());
                pos++;

                //S_EXT4
                 ps.setString(pos, dto.getSext4());
                pos++;

                //S_EXT5
                 ps.setString(pos, dto.getSext5());
                pos++;


               //I_VOUSRLNO
               ps.setLong(pos, dto.getIvousrlno().longValue());
               pos++;
               //I_SEQNO
               ps.setLong(pos, dto.getIseqno().longValue());
               pos++;
		       ps.addBatch(); 
	       }
	
           ps.executeBatch();
        }
        finally
        {
           close(ps);
        }

        return ;
    }



    /**
     * delete a record in Database.
     */
    public void delete(IPK _pk, Connection conn) throws SQLException
    {
       
      
       String msgValid = _pk.checkValid() ;
       if (msgValid != null)
       {
       		return ;
        //	throw new SQLException("ɾ������"+msgValid) ;
       }
       HtfReportIncomeSubPK pk = (HtfReportIncomeSubPK)_pk ;


       PreparedStatement ps = null;
       try {
           ps = conn.prepareStatement(SQL_DELETE);
           ps.setLong(1, pk.getIvousrlno().longValue());
           ps.setLong(2, pk.getIseqno().longValue());
           ps.executeUpdate();
        }
        finally
        {
            close(ps);
        }
    }


     /**
     * delete batch record in Database.
     */
    public void delete(IPK[] _pks, Connection conn) throws SQLException
    {
        HtfReportIncomeSubPK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (HtfReportIncomeSubPK)_pks[i] ; 
	    	String msgValid = pk.checkValid() ;
    		if (msgValid == null)
    		{
	    		pks.add(pk) ;
     		}
 		}
 		
 		
        PreparedStatement ps = null;
        try
         {
            ps = conn.prepareStatement(SQL_DELETE);
             
       		for (int i= 0; i< pks.size() ; i++)
       		{
       			pk  = (HtfReportIncomeSubPK)(pks.get(i)) ; 
                ps.setLong(1, pk.getIvousrlno().longValue());
                ps.setLong(2, pk.getIseqno().longValue());
		        ps.addBatch() ;
			}
            ps.executeBatch();
        }
        finally
        {
            close(ps);
        }      
    }



    /**
    * Check IChangerNumber or UpdateTimeStamp.
    */
    public void check(IDto _dto, Connection conn) throws SQLException
    {
                                                                  		throw new SQLException("���ݿ����HTF_REPORT_INCOME_SUBû�м���޸ĵ��ֶ�");
    }


	/**
	* synchronize values between parent and children
	*/
    public void syncToChildren(IDto _dto)     throws SQLException{
       throw new SQLException("����¼û����������¼��������޷���������¼����ϸ��¼֮�������ͬ����");
  }

	/**
	*get children
	*/
    public IDto[] findChildren (IDto _dto, Connection conn, boolean isLobSupport)     throws SQLException{
         throw new SQLException("��dtoû��������dto��������޷��õ���dto");
  }    
    
  /**
   *return the children IDto class
   * @return Class
   */
  public Class getChildrenClass(){
	  return null;
  };    



    /**
     * Close JDBC Statement.
     * @param stmt  Statement to be closed.
     */
    private void close(Statement stmt)
    {
        if (stmt != null)
        {
            try
            {
                stmt.close();
            }
            catch(SQLException    e)
            {}
        }
    }

    /**
     * Close JDBC ResultSet.
     * @param rs  ResultSet to be closed.
     */
    private void close(ResultSet rs)
    {
        if (rs != null)
        {
            try
            {
                rs.close();
            }catch(SQLException  e){}
        }
    }
}
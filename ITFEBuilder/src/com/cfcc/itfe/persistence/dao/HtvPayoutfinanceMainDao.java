    



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

import com.cfcc.itfe.persistence.dto.HtvPayoutfinanceMainDto ;
import com.cfcc.itfe.persistence.pk.HtvPayoutfinanceMainPK ;


/**
 * <p>Title: DAO of table: HTV_PAYOUTFINANCE_MAIN</p>
 * <p>Description: Data Access Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:56 </p>
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

public class HtvPayoutfinanceMainDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO HTV_PAYOUTFINANCE_MAIN ("
          + "I_VOUSRLNO,S_TRECODE,S_BILLORG,S_PAYEEBANKNO,S_PAYERACCT"
          + ",S_PAYERNAME,S_PAYERADDR,S_ENTRUSTDATE,S_PACKAGENO,S_PAYOUTVOUTYPE"
          + ",S_VOUNO,S_VOUDATE,S_ADDWORD,S_BUDGETTYPE,S_BDGORGCODE"
          + ",S_FUNCSBTCODE,S_ECNOMICSUBJECTCODE,N_AMT,S_FILENAME,S_RESULT"
          + ",TS_SYSUPDATE,S_BOOKORGCODE,S_STATUS"
        + ") VALUES ("
        + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,CURRENT TIMESTAMP ,?,?)";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "HTV_PAYOUTFINANCE_MAIN.I_VOUSRLNO, HTV_PAYOUTFINANCE_MAIN.S_TRECODE, HTV_PAYOUTFINANCE_MAIN.S_BILLORG, HTV_PAYOUTFINANCE_MAIN.S_PAYEEBANKNO, HTV_PAYOUTFINANCE_MAIN.S_PAYERACCT, "
        + "HTV_PAYOUTFINANCE_MAIN.S_PAYERNAME, HTV_PAYOUTFINANCE_MAIN.S_PAYERADDR, HTV_PAYOUTFINANCE_MAIN.S_ENTRUSTDATE, HTV_PAYOUTFINANCE_MAIN.S_PACKAGENO, HTV_PAYOUTFINANCE_MAIN.S_PAYOUTVOUTYPE, "
        + "HTV_PAYOUTFINANCE_MAIN.S_VOUNO, HTV_PAYOUTFINANCE_MAIN.S_VOUDATE, HTV_PAYOUTFINANCE_MAIN.S_ADDWORD, HTV_PAYOUTFINANCE_MAIN.S_BUDGETTYPE, HTV_PAYOUTFINANCE_MAIN.S_BDGORGCODE, "
        + "HTV_PAYOUTFINANCE_MAIN.S_FUNCSBTCODE, HTV_PAYOUTFINANCE_MAIN.S_ECNOMICSUBJECTCODE, HTV_PAYOUTFINANCE_MAIN.N_AMT, HTV_PAYOUTFINANCE_MAIN.S_FILENAME, HTV_PAYOUTFINANCE_MAIN.S_RESULT, "
        + "HTV_PAYOUTFINANCE_MAIN.TS_SYSUPDATE, HTV_PAYOUTFINANCE_MAIN.S_BOOKORGCODE, HTV_PAYOUTFINANCE_MAIN.S_STATUS "
        + "FROM HTV_PAYOUTFINANCE_MAIN "
        +" WHERE " 
        + "I_VOUSRLNO = ?"
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "HTV_PAYOUTFINANCE_MAIN.I_VOUSRLNO, HTV_PAYOUTFINANCE_MAIN.S_TRECODE, HTV_PAYOUTFINANCE_MAIN.S_BILLORG, HTV_PAYOUTFINANCE_MAIN.S_PAYEEBANKNO, HTV_PAYOUTFINANCE_MAIN.S_PAYERACCT, "
        + "HTV_PAYOUTFINANCE_MAIN.S_PAYERNAME, HTV_PAYOUTFINANCE_MAIN.S_PAYERADDR, HTV_PAYOUTFINANCE_MAIN.S_ENTRUSTDATE, HTV_PAYOUTFINANCE_MAIN.S_PACKAGENO, HTV_PAYOUTFINANCE_MAIN.S_PAYOUTVOUTYPE, "
        + "HTV_PAYOUTFINANCE_MAIN.S_VOUNO, HTV_PAYOUTFINANCE_MAIN.S_VOUDATE, HTV_PAYOUTFINANCE_MAIN.S_ADDWORD, HTV_PAYOUTFINANCE_MAIN.S_BUDGETTYPE, HTV_PAYOUTFINANCE_MAIN.S_BDGORGCODE, "
        + "HTV_PAYOUTFINANCE_MAIN.S_FUNCSBTCODE, HTV_PAYOUTFINANCE_MAIN.S_ECNOMICSUBJECTCODE, HTV_PAYOUTFINANCE_MAIN.N_AMT, HTV_PAYOUTFINANCE_MAIN.S_FILENAME, HTV_PAYOUTFINANCE_MAIN.S_RESULT, "
        + "HTV_PAYOUTFINANCE_MAIN.TS_SYSUPDATE, HTV_PAYOUTFINANCE_MAIN.S_BOOKORGCODE, HTV_PAYOUTFINANCE_MAIN.S_STATUS "
        + "FROM HTV_PAYOUTFINANCE_MAIN "
        +" WHERE " 
        + "I_VOUSRLNO = ? FOR UPDATE"
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  HTV_PAYOUTFINANCE_MAIN.I_VOUSRLNO  , HTV_PAYOUTFINANCE_MAIN.S_TRECODE  , HTV_PAYOUTFINANCE_MAIN.S_BILLORG  , HTV_PAYOUTFINANCE_MAIN.S_PAYEEBANKNO  , HTV_PAYOUTFINANCE_MAIN.S_PAYERACCT "
        + " , HTV_PAYOUTFINANCE_MAIN.S_PAYERNAME  , HTV_PAYOUTFINANCE_MAIN.S_PAYERADDR  , HTV_PAYOUTFINANCE_MAIN.S_ENTRUSTDATE  , HTV_PAYOUTFINANCE_MAIN.S_PACKAGENO  , HTV_PAYOUTFINANCE_MAIN.S_PAYOUTVOUTYPE "
        + " , HTV_PAYOUTFINANCE_MAIN.S_VOUNO  , HTV_PAYOUTFINANCE_MAIN.S_VOUDATE  , HTV_PAYOUTFINANCE_MAIN.S_ADDWORD  , HTV_PAYOUTFINANCE_MAIN.S_BUDGETTYPE  , HTV_PAYOUTFINANCE_MAIN.S_BDGORGCODE "
        + " , HTV_PAYOUTFINANCE_MAIN.S_FUNCSBTCODE  , HTV_PAYOUTFINANCE_MAIN.S_ECNOMICSUBJECTCODE  , HTV_PAYOUTFINANCE_MAIN.N_AMT  , HTV_PAYOUTFINANCE_MAIN.S_FILENAME  , HTV_PAYOUTFINANCE_MAIN.S_RESULT "
        + " , HTV_PAYOUTFINANCE_MAIN.TS_SYSUPDATE  , HTV_PAYOUTFINANCE_MAIN.S_BOOKORGCODE  , HTV_PAYOUTFINANCE_MAIN.S_STATUS "
        + "FROM HTV_PAYOUTFINANCE_MAIN ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "HTV_PAYOUTFINANCE_MAIN.I_VOUSRLNO, HTV_PAYOUTFINANCE_MAIN.S_TRECODE, HTV_PAYOUTFINANCE_MAIN.S_BILLORG, HTV_PAYOUTFINANCE_MAIN.S_PAYEEBANKNO, HTV_PAYOUTFINANCE_MAIN.S_PAYERACCT, "
        + "HTV_PAYOUTFINANCE_MAIN.S_PAYERNAME, HTV_PAYOUTFINANCE_MAIN.S_PAYERADDR, HTV_PAYOUTFINANCE_MAIN.S_ENTRUSTDATE, HTV_PAYOUTFINANCE_MAIN.S_PACKAGENO, HTV_PAYOUTFINANCE_MAIN.S_PAYOUTVOUTYPE, "
        + "HTV_PAYOUTFINANCE_MAIN.S_VOUNO, HTV_PAYOUTFINANCE_MAIN.S_VOUDATE, HTV_PAYOUTFINANCE_MAIN.S_ADDWORD, HTV_PAYOUTFINANCE_MAIN.S_BUDGETTYPE, HTV_PAYOUTFINANCE_MAIN.S_BDGORGCODE, "
        + "HTV_PAYOUTFINANCE_MAIN.S_FUNCSBTCODE, HTV_PAYOUTFINANCE_MAIN.S_ECNOMICSUBJECTCODE, HTV_PAYOUTFINANCE_MAIN.N_AMT, HTV_PAYOUTFINANCE_MAIN.S_FILENAME, HTV_PAYOUTFINANCE_MAIN.S_RESULT, "
        + "HTV_PAYOUTFINANCE_MAIN.TS_SYSUPDATE, HTV_PAYOUTFINANCE_MAIN.S_BOOKORGCODE, HTV_PAYOUTFINANCE_MAIN.S_STATUS "
        + "FROM HTV_PAYOUTFINANCE_MAIN " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        + "I_VOUSRLNO = ?)"
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE HTV_PAYOUTFINANCE_MAIN SET "
        + "S_TRECODE =?,S_BILLORG =?,S_PAYEEBANKNO =?,S_PAYERACCT =?,S_PAYERNAME =?, "
        + "S_PAYERADDR =?,S_ENTRUSTDATE =?,S_PACKAGENO =?,S_PAYOUTVOUTYPE =?,S_VOUNO =?, "
        + "S_VOUDATE =?,S_ADDWORD =?,S_BUDGETTYPE =?,S_BDGORGCODE =?,S_FUNCSBTCODE =?, "
        + "S_ECNOMICSUBJECTCODE =?,N_AMT =?,S_FILENAME =?,S_RESULT =?,TS_SYSUPDATE =CURRENT TIMESTAMP, "
        + "S_BOOKORGCODE =?,S_STATUS =? "
        + "WHERE "
        + "I_VOUSRLNO = ?"
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE HTV_PAYOUTFINANCE_MAIN SET "
        + "S_TRECODE =?, S_BILLORG =?, S_PAYEEBANKNO =?, S_PAYERACCT =?, S_PAYERNAME =?,  "
        + "S_PAYERADDR =?, S_ENTRUSTDATE =?, S_PACKAGENO =?, S_PAYOUTVOUTYPE =?, S_VOUNO =?,  "
        + "S_VOUDATE =?, S_ADDWORD =?, S_BUDGETTYPE =?, S_BDGORGCODE =?, S_FUNCSBTCODE =?,  "
        + "S_ECNOMICSUBJECTCODE =?, N_AMT =?, S_FILENAME =?, S_RESULT =?, TS_SYSUPDATE =CURRENT TIMESTAMP,  "
        + "S_BOOKORGCODE =?, S_STATUS =? "
        + "WHERE "
        + "I_VOUSRLNO = ?"
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM HTV_PAYOUTFINANCE_MAIN " 
        + " WHERE "
        + "I_VOUSRLNO = ?"
        ;


	/**
	*  批量查询 一次最多查询的参数数量
	*/
	
	public static final int FIND_BATCH_SIZE = 150  / 1;
	



   /**
   * Create a new record in Database.
   */
  public void create(IDto _dto,  Connection conn) throws SQLException
  {
     HtvPayoutfinanceMainDto dto = (HtvPayoutfinanceMainDto)_dto ;
     String msgValid = dto.checkValid() ;
     if (msgValid != null)
     	throw new SQLException("插入错误，"+msgValid) ;

     PreparedStatement ps = null;
     try
     {
         ps = conn.prepareStatement(SQL_INSERT);
          if (dto.getIvousrlno()==null)
            ps.setNull(1, java.sql.Types.BIGINT);
         else
            ps.setLong(1, dto.getIvousrlno().longValue());
          ps.setString(2, dto.getStrecode());

          ps.setString(3, dto.getSbillorg());

          ps.setString(4, dto.getSpayeebankno());

          ps.setString(5, dto.getSpayeracct());

          ps.setString(6, dto.getSpayername());

          ps.setString(7, dto.getSpayeraddr());

          ps.setString(8, dto.getSentrustdate());

          ps.setString(9, dto.getSpackageno());

          ps.setString(10, dto.getSpayoutvoutype());

          ps.setString(11, dto.getSvouno());

          ps.setString(12, dto.getSvoudate());

          ps.setString(13, dto.getSaddword());

          ps.setString(14, dto.getSbudgettype());

          ps.setString(15, dto.getSbdgorgcode());

          ps.setString(16, dto.getSfuncsbtcode());

          ps.setString(17, dto.getSecnomicsubjectcode());

          ps.setBigDecimal(18, dto.getNamt());

          ps.setString(19, dto.getSfilename());

          ps.setString(20, dto.getSresult());

           ps.setString(21, dto.getSbookorgcode());

          ps.setString(22, dto.getSstatus());

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
       HtvPayoutfinanceMainDto dto = (HtvPayoutfinanceMainDto)_dto ;
       String msgValid = dto.checkValid() ;
       if (msgValid != null)
           throw new SQLException("插入错误，"+msgValid) ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try
       {
           ps = conn.prepareStatement(SQL_INSERT_WITH_RESULT);
            if (dto.getIvousrlno()==null)
              ps.setNull(1, java.sql.Types.BIGINT);
           else
              ps.setLong(1, dto.getIvousrlno().longValue());
            ps.setString(2, dto.getStrecode());
            ps.setString(3, dto.getSbillorg());
            ps.setString(4, dto.getSpayeebankno());
            ps.setString(5, dto.getSpayeracct());
            ps.setString(6, dto.getSpayername());
            ps.setString(7, dto.getSpayeraddr());
            ps.setString(8, dto.getSentrustdate());
            ps.setString(9, dto.getSpackageno());
            ps.setString(10, dto.getSpayoutvoutype());
            ps.setString(11, dto.getSvouno());
            ps.setString(12, dto.getSvoudate());
            ps.setString(13, dto.getSaddword());
            ps.setString(14, dto.getSbudgettype());
            ps.setString(15, dto.getSbdgorgcode());
            ps.setString(16, dto.getSfuncsbtcode());
            ps.setString(17, dto.getSecnomicsubjectcode());
            ps.setBigDecimal(18, dto.getNamt());
            ps.setString(19, dto.getSfilename());
            ps.setString(20, dto.getSresult());
             ps.setString(21, dto.getSbookorgcode());
            ps.setString(22, dto.getSstatus());
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
        HtvPayoutfinanceMainDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (HtvPayoutfinanceMainDto)_dtos[i] ; 
    	    String msgValid = dto.checkValid() ;
        	if (msgValid != null)
     	    	throw new SQLException("插入错误，"+msgValid) ;
     	}
 	    PreparedStatement ps = null;
        try
        {
            ps = conn.prepareStatement(SQL_INSERT);
       
     	    for (int i= 0; i< _dtos.length ; i++)
     	    {
 	    	    dto  = (HtvPayoutfinanceMainDto)_dtos[i] ; 
  
               if (dto.getIvousrlno()==null)
                  ps.setNull(1, java.sql.Types.BIGINT);
               else
                  ps.setLong(1, dto.getIvousrlno().longValue());
  
               ps.setString(2, dto.getStrecode());
  
               ps.setString(3, dto.getSbillorg());
  
               ps.setString(4, dto.getSpayeebankno());
  
               ps.setString(5, dto.getSpayeracct());
  
               ps.setString(6, dto.getSpayername());
  
               ps.setString(7, dto.getSpayeraddr());
  
               ps.setString(8, dto.getSentrustdate());
  
               ps.setString(9, dto.getSpackageno());
  
               ps.setString(10, dto.getSpayoutvoutype());
  
               ps.setString(11, dto.getSvouno());
  
               ps.setString(12, dto.getSvoudate());
  
               ps.setString(13, dto.getSaddword());
  
               ps.setString(14, dto.getSbudgettype());
  
               ps.setString(15, dto.getSbdgorgcode());
  
               ps.setString(16, dto.getSfuncsbtcode());
  
               ps.setString(17, dto.getSecnomicsubjectcode());
  
               ps.setBigDecimal(18, dto.getNamt());
  
               ps.setString(19, dto.getSfilename());
  
               ps.setString(20, dto.getSresult());
   
               ps.setString(21, dto.getSbookorgcode());
  
               ps.setString(22, dto.getSstatus());
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
        //	throw new SQLException("查找错误，"+msgValid) ;
       }
        	
       HtvPayoutfinanceMainPK pk = (HtvPayoutfinanceMainPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT);
           if (pk.getIvousrlno()==null)
               ps.setNull(1, java.sql.Types.BIGINT);
           else
               ps.setLong(1, pk.getIvousrlno().longValue());

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
        //	throw new SQLException("查找错误，"+msgValid) ;
       }
        	
       HtvPayoutfinanceMainPK pk = (HtvPayoutfinanceMainPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT_FOR_UPDATE);
           if (pk.getIvousrlno()==null)
               ps.setNull(1, java.sql.Types.BIGINT);
           else
               ps.setLong(1, pk.getIvousrlno().longValue());

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
        HtvPayoutfinanceMainPK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (HtvPayoutfinanceMainPK)_pks[i] ; 
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
                    pk  = (HtvPayoutfinanceMainPK)(pks.get(i)) ; 
                   if (pk.getIvousrlno()==null)
                      ps.setNull((i-iBegin)*1+1, java.sql.Types.BIGINT);
                   else
                      ps.setLong((i-iBegin)*1+1, pk.getIvousrlno().longValue());

			
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
            HtvPayoutfinanceMainDto[] dtos = new HtvPayoutfinanceMainDto[0];
		    dtos = (HtvPayoutfinanceMainDto[]) results.toArray(dtos) ;
		    return dtos ;
        }
        else
        {
            return null;
        }
    }

  
  
  



    /**
	    * 得到查询语句
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
             HtvPayoutfinanceMainDto  dto = new  HtvPayoutfinanceMainDto ();
             //I_VOUSRLNO
             str = rs.getString("I_VOUSRLNO");
             if(str!=null){
                dto.setIvousrlno(new Long(str));
             }

             //S_TRECODE
             str = rs.getString("S_TRECODE");
             if (str == null)
                dto.setStrecode(null);
             else
                dto.setStrecode(str.trim());

             //S_BILLORG
             str = rs.getString("S_BILLORG");
             if (str == null)
                dto.setSbillorg(null);
             else
                dto.setSbillorg(str.trim());

             //S_PAYEEBANKNO
             str = rs.getString("S_PAYEEBANKNO");
             if (str == null)
                dto.setSpayeebankno(null);
             else
                dto.setSpayeebankno(str.trim());

             //S_PAYERACCT
             str = rs.getString("S_PAYERACCT");
             if (str == null)
                dto.setSpayeracct(null);
             else
                dto.setSpayeracct(str.trim());

             //S_PAYERNAME
             str = rs.getString("S_PAYERNAME");
             if (str == null)
                dto.setSpayername(null);
             else
                dto.setSpayername(str.trim());

             //S_PAYERADDR
             str = rs.getString("S_PAYERADDR");
             if (str == null)
                dto.setSpayeraddr(null);
             else
                dto.setSpayeraddr(str.trim());

             //S_ENTRUSTDATE
             str = rs.getString("S_ENTRUSTDATE");
             if (str == null)
                dto.setSentrustdate(null);
             else
                dto.setSentrustdate(str.trim());

             //S_PACKAGENO
             str = rs.getString("S_PACKAGENO");
             if (str == null)
                dto.setSpackageno(null);
             else
                dto.setSpackageno(str.trim());

             //S_PAYOUTVOUTYPE
             str = rs.getString("S_PAYOUTVOUTYPE");
             if (str == null)
                dto.setSpayoutvoutype(null);
             else
                dto.setSpayoutvoutype(str.trim());

             //S_VOUNO
             str = rs.getString("S_VOUNO");
             if (str == null)
                dto.setSvouno(null);
             else
                dto.setSvouno(str.trim());

             //S_VOUDATE
             str = rs.getString("S_VOUDATE");
             if (str == null)
                dto.setSvoudate(null);
             else
                dto.setSvoudate(str.trim());

             //S_ADDWORD
             str = rs.getString("S_ADDWORD");
             if (str == null)
                dto.setSaddword(null);
             else
                dto.setSaddword(str.trim());

             //S_BUDGETTYPE
             str = rs.getString("S_BUDGETTYPE");
             if (str == null)
                dto.setSbudgettype(null);
             else
                dto.setSbudgettype(str.trim());

             //S_BDGORGCODE
             str = rs.getString("S_BDGORGCODE");
             if (str == null)
                dto.setSbdgorgcode(null);
             else
                dto.setSbdgorgcode(str.trim());

             //S_FUNCSBTCODE
             str = rs.getString("S_FUNCSBTCODE");
             if (str == null)
                dto.setSfuncsbtcode(null);
             else
                dto.setSfuncsbtcode(str.trim());

             //S_ECNOMICSUBJECTCODE
             str = rs.getString("S_ECNOMICSUBJECTCODE");
             if (str == null)
                dto.setSecnomicsubjectcode(null);
             else
                dto.setSecnomicsubjectcode(str.trim());

             //N_AMT
           dto.setNamt(rs.getBigDecimal("N_AMT"));

             //S_FILENAME
             str = rs.getString("S_FILENAME");
             if (str == null)
                dto.setSfilename(null);
             else
                dto.setSfilename(str.trim());

             //S_RESULT
             str = rs.getString("S_RESULT");
             if (str == null)
                dto.setSresult(null);
             else
                dto.setSresult(str.trim());

             //TS_SYSUPDATE
           dto.setTssysupdate(rs.getTimestamp("TS_SYSUPDATE"));

             //S_BOOKORGCODE
             str = rs.getString("S_BOOKORGCODE");
             if (str == null)
                dto.setSbookorgcode(null);
             else
                dto.setSbookorgcode(str.trim());

             //S_STATUS
             str = rs.getString("S_STATUS");
             if (str == null)
                dto.setSstatus(null);
             else
                dto.setSstatus(str.trim());



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

      throw new SQLException("本dto没有与其它dto相关联，无法应用此方法");
  }


     /**
     * Update a record in Database.
     */

    public void update(IDto _dto, Connection conn, boolean isLobSupport) throws SQLException
    {
        String msgValid = _dto.checkValid() ;
        if (msgValid != null)
            throw new SQLException("修改错误，"+msgValid) ;
        HtvPayoutfinanceMainDto dto = (HtvPayoutfinanceMainDto)_dto ;
        PreparedStatement ps = null;
        try {
            if(isLobSupport){
                ps = conn.prepareStatement(SQL_UPDATE_LOB);
            }
            else{
                ps = conn.prepareStatement(SQL_UPDATE);
            }
            int pos = 1;
            //S_TRECODE
            ps.setString(pos, dto.getStrecode());
            pos++;

            //S_BILLORG
            ps.setString(pos, dto.getSbillorg());
            pos++;

            //S_PAYEEBANKNO
            ps.setString(pos, dto.getSpayeebankno());
            pos++;

            //S_PAYERACCT
            ps.setString(pos, dto.getSpayeracct());
            pos++;

            //S_PAYERNAME
            ps.setString(pos, dto.getSpayername());
            pos++;

            //S_PAYERADDR
            ps.setString(pos, dto.getSpayeraddr());
            pos++;

            //S_ENTRUSTDATE
            ps.setString(pos, dto.getSentrustdate());
            pos++;

            //S_PACKAGENO
            ps.setString(pos, dto.getSpackageno());
            pos++;

            //S_PAYOUTVOUTYPE
            ps.setString(pos, dto.getSpayoutvoutype());
            pos++;

            //S_VOUNO
            ps.setString(pos, dto.getSvouno());
            pos++;

            //S_VOUDATE
            ps.setString(pos, dto.getSvoudate());
            pos++;

            //S_ADDWORD
            ps.setString(pos, dto.getSaddword());
            pos++;

            //S_BUDGETTYPE
            ps.setString(pos, dto.getSbudgettype());
            pos++;

            //S_BDGORGCODE
            ps.setString(pos, dto.getSbdgorgcode());
            pos++;

            //S_FUNCSBTCODE
            ps.setString(pos, dto.getSfuncsbtcode());
            pos++;

            //S_ECNOMICSUBJECTCODE
            ps.setString(pos, dto.getSecnomicsubjectcode());
            pos++;

            //N_AMT
            ps.setBigDecimal(pos, dto.getNamt());
            pos++;

            //S_FILENAME
            ps.setString(pos, dto.getSfilename());
            pos++;

            //S_RESULT
            ps.setString(pos, dto.getSresult());
            pos++;

            //TS_SYSUPDATE
            //S_BOOKORGCODE
            ps.setString(pos, dto.getSbookorgcode());
            pos++;

            //S_STATUS
            ps.setString(pos, dto.getSstatus());
            pos++;


           //I_VOUSRLNO
           ps.setLong(pos, dto.getIvousrlno().longValue());
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
     
     	 HtvPayoutfinanceMainDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (HtvPayoutfinanceMainDto)_dtos[i] ; 
            String msgValid = dto.checkValid() ;
            if (msgValid != null)
                throw new SQLException("修改错误，"+msgValid) ;
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
                dto  = (HtvPayoutfinanceMainDto)_dtos[i] ; 
                int pos = 1;
                //S_TRECODE
                 ps.setString(pos, dto.getStrecode());
                pos++;

                //S_BILLORG
                 ps.setString(pos, dto.getSbillorg());
                pos++;

                //S_PAYEEBANKNO
                 ps.setString(pos, dto.getSpayeebankno());
                pos++;

                //S_PAYERACCT
                 ps.setString(pos, dto.getSpayeracct());
                pos++;

                //S_PAYERNAME
                 ps.setString(pos, dto.getSpayername());
                pos++;

                //S_PAYERADDR
                 ps.setString(pos, dto.getSpayeraddr());
                pos++;

                //S_ENTRUSTDATE
                 ps.setString(pos, dto.getSentrustdate());
                pos++;

                //S_PACKAGENO
                 ps.setString(pos, dto.getSpackageno());
                pos++;

                //S_PAYOUTVOUTYPE
                 ps.setString(pos, dto.getSpayoutvoutype());
                pos++;

                //S_VOUNO
                 ps.setString(pos, dto.getSvouno());
                pos++;

                //S_VOUDATE
                 ps.setString(pos, dto.getSvoudate());
                pos++;

                //S_ADDWORD
                 ps.setString(pos, dto.getSaddword());
                pos++;

                //S_BUDGETTYPE
                 ps.setString(pos, dto.getSbudgettype());
                pos++;

                //S_BDGORGCODE
                 ps.setString(pos, dto.getSbdgorgcode());
                pos++;

                //S_FUNCSBTCODE
                 ps.setString(pos, dto.getSfuncsbtcode());
                pos++;

                //S_ECNOMICSUBJECTCODE
                 ps.setString(pos, dto.getSecnomicsubjectcode());
                pos++;

                //N_AMT
                 ps.setBigDecimal(pos, dto.getNamt());
                pos++;

                //S_FILENAME
                 ps.setString(pos, dto.getSfilename());
                pos++;

                //S_RESULT
                 ps.setString(pos, dto.getSresult());
                pos++;

                //TS_SYSUPDATE
                 //S_BOOKORGCODE
                 ps.setString(pos, dto.getSbookorgcode());
                pos++;

                //S_STATUS
                 ps.setString(pos, dto.getSstatus());
                pos++;


               //I_VOUSRLNO
               ps.setLong(pos, dto.getIvousrlno().longValue());
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
        //	throw new SQLException("删除错误，"+msgValid) ;
       }
       HtvPayoutfinanceMainPK pk = (HtvPayoutfinanceMainPK)_pk ;


       PreparedStatement ps = null;
       try {
           ps = conn.prepareStatement(SQL_DELETE);
           ps.setLong(1, pk.getIvousrlno().longValue());
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
        HtvPayoutfinanceMainPK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (HtvPayoutfinanceMainPK)_pks[i] ; 
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
       			pk  = (HtvPayoutfinanceMainPK)(pks.get(i)) ; 
                ps.setLong(1, pk.getIvousrlno().longValue());
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
                                                                  		throw new SQLException("数据库表：HTV_PAYOUTFINANCE_MAIN没有检查修改的字段");
    }


	/**
	* synchronize values between parent and children
	*/
    public void syncToChildren(IDto _dto)     throws SQLException{
       throw new SQLException("本记录没有与其它记录相关联，无法进行主记录与明细记录之间的数据同步。");
  }

	/**
	*get children
	*/
    public IDto[] findChildren (IDto _dto, Connection conn, boolean isLobSupport)     throws SQLException{
         throw new SQLException("本dto没有与其它dto相关联，无法得到子dto");
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

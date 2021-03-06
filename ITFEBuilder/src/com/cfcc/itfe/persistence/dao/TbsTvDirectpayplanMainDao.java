    



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

import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanMainDto ;
import com.cfcc.itfe.persistence.pk.TbsTvDirectpayplanMainPK ;


/**
 * <p>Title: DAO of table: TBS_TV_DIRECTPAYPLAN_MAIN</p>
 * <p>Description: Data Access Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:57 </p>
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

public class TbsTvDirectpayplanMainDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO TBS_TV_DIRECTPAYPLAN_MAIN ("
          + "I_VOUSRLNO,S_TRECODE,S_PAYERACCT,S_PAYEROPNBNKNO,S_PAYEEACCT"
          + ",S_PAYEENAME,S_PAYEEOPNBNKNO,C_BDGKIND,F_AMT,S_VOUNO"
          + ",S_CRPVOUCODE,D_VOUCHER,I_OFYEAR,D_ACCEPT,D_ACCT"
          + ",S_BIZTYPE,S_PACKAGENO,S_STATUS,S_BOOKORGCODE,S_FILENAME"
          + ",TS_SYSUPDATE,S_PAYEEOPNBNKNAME,S_IFMATCH"
        + ") VALUES ("
        + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,CURRENT TIMESTAMP ,?,?)";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "TBS_TV_DIRECTPAYPLAN_MAIN.I_VOUSRLNO, TBS_TV_DIRECTPAYPLAN_MAIN.S_TRECODE, TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYERACCT, TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYEROPNBNKNO, TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYEEACCT, "
        + "TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYEENAME, TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYEEOPNBNKNO, TBS_TV_DIRECTPAYPLAN_MAIN.C_BDGKIND, TBS_TV_DIRECTPAYPLAN_MAIN.F_AMT, TBS_TV_DIRECTPAYPLAN_MAIN.S_VOUNO, "
        + "TBS_TV_DIRECTPAYPLAN_MAIN.S_CRPVOUCODE, TBS_TV_DIRECTPAYPLAN_MAIN.D_VOUCHER, TBS_TV_DIRECTPAYPLAN_MAIN.I_OFYEAR, TBS_TV_DIRECTPAYPLAN_MAIN.D_ACCEPT, TBS_TV_DIRECTPAYPLAN_MAIN.D_ACCT, "
        + "TBS_TV_DIRECTPAYPLAN_MAIN.S_BIZTYPE, TBS_TV_DIRECTPAYPLAN_MAIN.S_PACKAGENO, TBS_TV_DIRECTPAYPLAN_MAIN.S_STATUS, TBS_TV_DIRECTPAYPLAN_MAIN.S_BOOKORGCODE, TBS_TV_DIRECTPAYPLAN_MAIN.S_FILENAME, "
        + "TBS_TV_DIRECTPAYPLAN_MAIN.TS_SYSUPDATE, TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYEEOPNBNKNAME, TBS_TV_DIRECTPAYPLAN_MAIN.S_IFMATCH "
        + "FROM TBS_TV_DIRECTPAYPLAN_MAIN "
        +" WHERE " 
        + "I_VOUSRLNO = ?"
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "TBS_TV_DIRECTPAYPLAN_MAIN.I_VOUSRLNO, TBS_TV_DIRECTPAYPLAN_MAIN.S_TRECODE, TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYERACCT, TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYEROPNBNKNO, TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYEEACCT, "
        + "TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYEENAME, TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYEEOPNBNKNO, TBS_TV_DIRECTPAYPLAN_MAIN.C_BDGKIND, TBS_TV_DIRECTPAYPLAN_MAIN.F_AMT, TBS_TV_DIRECTPAYPLAN_MAIN.S_VOUNO, "
        + "TBS_TV_DIRECTPAYPLAN_MAIN.S_CRPVOUCODE, TBS_TV_DIRECTPAYPLAN_MAIN.D_VOUCHER, TBS_TV_DIRECTPAYPLAN_MAIN.I_OFYEAR, TBS_TV_DIRECTPAYPLAN_MAIN.D_ACCEPT, TBS_TV_DIRECTPAYPLAN_MAIN.D_ACCT, "
        + "TBS_TV_DIRECTPAYPLAN_MAIN.S_BIZTYPE, TBS_TV_DIRECTPAYPLAN_MAIN.S_PACKAGENO, TBS_TV_DIRECTPAYPLAN_MAIN.S_STATUS, TBS_TV_DIRECTPAYPLAN_MAIN.S_BOOKORGCODE, TBS_TV_DIRECTPAYPLAN_MAIN.S_FILENAME, "
        + "TBS_TV_DIRECTPAYPLAN_MAIN.TS_SYSUPDATE, TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYEEOPNBNKNAME, TBS_TV_DIRECTPAYPLAN_MAIN.S_IFMATCH "
        + "FROM TBS_TV_DIRECTPAYPLAN_MAIN "
        +" WHERE " 
        + "I_VOUSRLNO = ? FOR UPDATE"
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  TBS_TV_DIRECTPAYPLAN_MAIN.I_VOUSRLNO  , TBS_TV_DIRECTPAYPLAN_MAIN.S_TRECODE  , TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYERACCT  , TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYEROPNBNKNO  , TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYEEACCT "
        + " , TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYEENAME  , TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYEEOPNBNKNO  , TBS_TV_DIRECTPAYPLAN_MAIN.C_BDGKIND  , TBS_TV_DIRECTPAYPLAN_MAIN.F_AMT  , TBS_TV_DIRECTPAYPLAN_MAIN.S_VOUNO "
        + " , TBS_TV_DIRECTPAYPLAN_MAIN.S_CRPVOUCODE  , TBS_TV_DIRECTPAYPLAN_MAIN.D_VOUCHER  , TBS_TV_DIRECTPAYPLAN_MAIN.I_OFYEAR  , TBS_TV_DIRECTPAYPLAN_MAIN.D_ACCEPT  , TBS_TV_DIRECTPAYPLAN_MAIN.D_ACCT "
        + " , TBS_TV_DIRECTPAYPLAN_MAIN.S_BIZTYPE  , TBS_TV_DIRECTPAYPLAN_MAIN.S_PACKAGENO  , TBS_TV_DIRECTPAYPLAN_MAIN.S_STATUS  , TBS_TV_DIRECTPAYPLAN_MAIN.S_BOOKORGCODE  , TBS_TV_DIRECTPAYPLAN_MAIN.S_FILENAME "
        + " , TBS_TV_DIRECTPAYPLAN_MAIN.TS_SYSUPDATE  , TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYEEOPNBNKNAME  , TBS_TV_DIRECTPAYPLAN_MAIN.S_IFMATCH "
        + "FROM TBS_TV_DIRECTPAYPLAN_MAIN ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "TBS_TV_DIRECTPAYPLAN_MAIN.I_VOUSRLNO, TBS_TV_DIRECTPAYPLAN_MAIN.S_TRECODE, TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYERACCT, TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYEROPNBNKNO, TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYEEACCT, "
        + "TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYEENAME, TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYEEOPNBNKNO, TBS_TV_DIRECTPAYPLAN_MAIN.C_BDGKIND, TBS_TV_DIRECTPAYPLAN_MAIN.F_AMT, TBS_TV_DIRECTPAYPLAN_MAIN.S_VOUNO, "
        + "TBS_TV_DIRECTPAYPLAN_MAIN.S_CRPVOUCODE, TBS_TV_DIRECTPAYPLAN_MAIN.D_VOUCHER, TBS_TV_DIRECTPAYPLAN_MAIN.I_OFYEAR, TBS_TV_DIRECTPAYPLAN_MAIN.D_ACCEPT, TBS_TV_DIRECTPAYPLAN_MAIN.D_ACCT, "
        + "TBS_TV_DIRECTPAYPLAN_MAIN.S_BIZTYPE, TBS_TV_DIRECTPAYPLAN_MAIN.S_PACKAGENO, TBS_TV_DIRECTPAYPLAN_MAIN.S_STATUS, TBS_TV_DIRECTPAYPLAN_MAIN.S_BOOKORGCODE, TBS_TV_DIRECTPAYPLAN_MAIN.S_FILENAME, "
        + "TBS_TV_DIRECTPAYPLAN_MAIN.TS_SYSUPDATE, TBS_TV_DIRECTPAYPLAN_MAIN.S_PAYEEOPNBNKNAME, TBS_TV_DIRECTPAYPLAN_MAIN.S_IFMATCH "
        + "FROM TBS_TV_DIRECTPAYPLAN_MAIN " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        + "I_VOUSRLNO = ?)"
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE TBS_TV_DIRECTPAYPLAN_MAIN SET "
        + "S_TRECODE =?,S_PAYERACCT =?,S_PAYEROPNBNKNO =?,S_PAYEEACCT =?,S_PAYEENAME =?, "
        + "S_PAYEEOPNBNKNO =?,C_BDGKIND =?,F_AMT =?,S_VOUNO =?,S_CRPVOUCODE =?, "
        + "D_VOUCHER =?,I_OFYEAR =?,D_ACCEPT =?,D_ACCT =?,S_BIZTYPE =?, "
        + "S_PACKAGENO =?,S_STATUS =?,S_BOOKORGCODE =?,S_FILENAME =?,TS_SYSUPDATE =CURRENT TIMESTAMP, "
        + "S_PAYEEOPNBNKNAME =?,S_IFMATCH =? "
        + "WHERE "
        + "I_VOUSRLNO = ?"
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE TBS_TV_DIRECTPAYPLAN_MAIN SET "
        + "S_TRECODE =?, S_PAYERACCT =?, S_PAYEROPNBNKNO =?, S_PAYEEACCT =?, S_PAYEENAME =?,  "
        + "S_PAYEEOPNBNKNO =?, C_BDGKIND =?, F_AMT =?, S_VOUNO =?, S_CRPVOUCODE =?,  "
        + "D_VOUCHER =?, I_OFYEAR =?, D_ACCEPT =?, D_ACCT =?, S_BIZTYPE =?,  "
        + "S_PACKAGENO =?, S_STATUS =?, S_BOOKORGCODE =?, S_FILENAME =?, TS_SYSUPDATE =CURRENT TIMESTAMP,  "
        + "S_PAYEEOPNBNKNAME =?, S_IFMATCH =? "
        + "WHERE "
        + "I_VOUSRLNO = ?"
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM TBS_TV_DIRECTPAYPLAN_MAIN " 
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
     TbsTvDirectpayplanMainDto dto = (TbsTvDirectpayplanMainDto)_dto ;
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

          ps.setString(3, dto.getSpayeracct());

          ps.setString(4, dto.getSpayeropnbnkno());

          ps.setString(5, dto.getSpayeeacct());

          ps.setString(6, dto.getSpayeename());

          ps.setString(7, dto.getSpayeeopnbnkno());

          ps.setString(8, dto.getCbdgkind());

          ps.setBigDecimal(9, dto.getFamt());

          ps.setString(10, dto.getSvouno());

          ps.setString(11, dto.getScrpvoucode());

          ps.setDate(12, dto.getDvoucher());

          if (dto.getIofyear()==null)
            ps.setNull(13, java.sql.Types.INTEGER);
         else
            ps.setInt(13, dto.getIofyear().intValue());
          ps.setDate(14, dto.getDaccept());

          ps.setDate(15, dto.getDacct());

          ps.setString(16, dto.getSbiztype());

          ps.setString(17, dto.getSpackageno());

          ps.setString(18, dto.getSstatus());

          ps.setString(19, dto.getSbookorgcode());

          ps.setString(20, dto.getSfilename());

           ps.setString(21, dto.getSpayeeopnbnkname());

          ps.setString(22, dto.getSifmatch());

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
       TbsTvDirectpayplanMainDto dto = (TbsTvDirectpayplanMainDto)_dto ;
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
            ps.setString(3, dto.getSpayeracct());
            ps.setString(4, dto.getSpayeropnbnkno());
            ps.setString(5, dto.getSpayeeacct());
            ps.setString(6, dto.getSpayeename());
            ps.setString(7, dto.getSpayeeopnbnkno());
            ps.setString(8, dto.getCbdgkind());
            ps.setBigDecimal(9, dto.getFamt());
            ps.setString(10, dto.getSvouno());
            ps.setString(11, dto.getScrpvoucode());
            ps.setDate(12, dto.getDvoucher());
            if (dto.getIofyear()==null)
              ps.setNull(13, java.sql.Types.INTEGER);
           else
              ps.setInt(13, dto.getIofyear().intValue());
            ps.setDate(14, dto.getDaccept());
            ps.setDate(15, dto.getDacct());
            ps.setString(16, dto.getSbiztype());
            ps.setString(17, dto.getSpackageno());
            ps.setString(18, dto.getSstatus());
            ps.setString(19, dto.getSbookorgcode());
            ps.setString(20, dto.getSfilename());
             ps.setString(21, dto.getSpayeeopnbnkname());
            ps.setString(22, dto.getSifmatch());
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
        TbsTvDirectpayplanMainDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (TbsTvDirectpayplanMainDto)_dtos[i] ; 
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
 	    	    dto  = (TbsTvDirectpayplanMainDto)_dtos[i] ; 
  
               if (dto.getIvousrlno()==null)
                  ps.setNull(1, java.sql.Types.BIGINT);
               else
                  ps.setLong(1, dto.getIvousrlno().longValue());
  
               ps.setString(2, dto.getStrecode());
  
               ps.setString(3, dto.getSpayeracct());
  
               ps.setString(4, dto.getSpayeropnbnkno());
  
               ps.setString(5, dto.getSpayeeacct());
  
               ps.setString(6, dto.getSpayeename());
  
               ps.setString(7, dto.getSpayeeopnbnkno());
  
               ps.setString(8, dto.getCbdgkind());
  
               ps.setBigDecimal(9, dto.getFamt());
  
               ps.setString(10, dto.getSvouno());
  
               ps.setString(11, dto.getScrpvoucode());
  
               ps.setDate(12, dto.getDvoucher());
  
               if (dto.getIofyear()==null)
                  ps.setNull(13, java.sql.Types.INTEGER);
               else
                  ps.setInt(13, dto.getIofyear().intValue());
  
               ps.setDate(14, dto.getDaccept());
  
               ps.setDate(15, dto.getDacct());
  
               ps.setString(16, dto.getSbiztype());
  
               ps.setString(17, dto.getSpackageno());
  
               ps.setString(18, dto.getSstatus());
  
               ps.setString(19, dto.getSbookorgcode());
  
               ps.setString(20, dto.getSfilename());
   
               ps.setString(21, dto.getSpayeeopnbnkname());
  
               ps.setString(22, dto.getSifmatch());
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
        	
       TbsTvDirectpayplanMainPK pk = (TbsTvDirectpayplanMainPK)_pk ;

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
        	
       TbsTvDirectpayplanMainPK pk = (TbsTvDirectpayplanMainPK)_pk ;

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
        TbsTvDirectpayplanMainPK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TbsTvDirectpayplanMainPK)_pks[i] ; 
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
                    pk  = (TbsTvDirectpayplanMainPK)(pks.get(i)) ; 
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
            TbsTvDirectpayplanMainDto[] dtos = new TbsTvDirectpayplanMainDto[0];
		    dtos = (TbsTvDirectpayplanMainDto[]) results.toArray(dtos) ;
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
             TbsTvDirectpayplanMainDto  dto = new  TbsTvDirectpayplanMainDto ();
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

             //S_PAYERACCT
             str = rs.getString("S_PAYERACCT");
             if (str == null)
                dto.setSpayeracct(null);
             else
                dto.setSpayeracct(str.trim());

             //S_PAYEROPNBNKNO
             str = rs.getString("S_PAYEROPNBNKNO");
             if (str == null)
                dto.setSpayeropnbnkno(null);
             else
                dto.setSpayeropnbnkno(str.trim());

             //S_PAYEEACCT
             str = rs.getString("S_PAYEEACCT");
             if (str == null)
                dto.setSpayeeacct(null);
             else
                dto.setSpayeeacct(str.trim());

             //S_PAYEENAME
             str = rs.getString("S_PAYEENAME");
             if (str == null)
                dto.setSpayeename(null);
             else
                dto.setSpayeename(str.trim());

             //S_PAYEEOPNBNKNO
             str = rs.getString("S_PAYEEOPNBNKNO");
             if (str == null)
                dto.setSpayeeopnbnkno(null);
             else
                dto.setSpayeeopnbnkno(str.trim());

             //C_BDGKIND
             str = rs.getString("C_BDGKIND");
             if (str == null)
                dto.setCbdgkind(null);
             else
                dto.setCbdgkind(str.trim());

             //F_AMT
           dto.setFamt(rs.getBigDecimal("F_AMT"));

             //S_VOUNO
             str = rs.getString("S_VOUNO");
             if (str == null)
                dto.setSvouno(null);
             else
                dto.setSvouno(str.trim());

             //S_CRPVOUCODE
             str = rs.getString("S_CRPVOUCODE");
             if (str == null)
                dto.setScrpvoucode(null);
             else
                dto.setScrpvoucode(str.trim());

             //D_VOUCHER
           dto.setDvoucher(rs.getDate("D_VOUCHER"));

             //I_OFYEAR
             str = rs.getString("I_OFYEAR");
             if(str!=null){
                dto.setIofyear(new Integer(str));
             }

             //D_ACCEPT
           dto.setDaccept(rs.getDate("D_ACCEPT"));

             //D_ACCT
           dto.setDacct(rs.getDate("D_ACCT"));

             //S_BIZTYPE
             str = rs.getString("S_BIZTYPE");
             if (str == null)
                dto.setSbiztype(null);
             else
                dto.setSbiztype(str.trim());

             //S_PACKAGENO
             str = rs.getString("S_PACKAGENO");
             if (str == null)
                dto.setSpackageno(null);
             else
                dto.setSpackageno(str.trim());

             //S_STATUS
             str = rs.getString("S_STATUS");
             if (str == null)
                dto.setSstatus(null);
             else
                dto.setSstatus(str.trim());

             //S_BOOKORGCODE
             str = rs.getString("S_BOOKORGCODE");
             if (str == null)
                dto.setSbookorgcode(null);
             else
                dto.setSbookorgcode(str.trim());

             //S_FILENAME
             str = rs.getString("S_FILENAME");
             if (str == null)
                dto.setSfilename(null);
             else
                dto.setSfilename(str.trim());

             //TS_SYSUPDATE
           dto.setTssysupdate(rs.getTimestamp("TS_SYSUPDATE"));

             //S_PAYEEOPNBNKNAME
             str = rs.getString("S_PAYEEOPNBNKNAME");
             if (str == null)
                dto.setSpayeeopnbnkname(null);
             else
                dto.setSpayeeopnbnkname(str.trim());

             //S_IFMATCH
             str = rs.getString("S_IFMATCH");
             if (str == null)
                dto.setSifmatch(null);
             else
                dto.setSifmatch(str.trim());



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
        TbsTvDirectpayplanMainDto dto = (TbsTvDirectpayplanMainDto)_dto ;
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

            //S_PAYERACCT
            ps.setString(pos, dto.getSpayeracct());
            pos++;

            //S_PAYEROPNBNKNO
            ps.setString(pos, dto.getSpayeropnbnkno());
            pos++;

            //S_PAYEEACCT
            ps.setString(pos, dto.getSpayeeacct());
            pos++;

            //S_PAYEENAME
            ps.setString(pos, dto.getSpayeename());
            pos++;

            //S_PAYEEOPNBNKNO
            ps.setString(pos, dto.getSpayeeopnbnkno());
            pos++;

            //C_BDGKIND
            ps.setString(pos, dto.getCbdgkind());
            pos++;

            //F_AMT
            ps.setBigDecimal(pos, dto.getFamt());
            pos++;

            //S_VOUNO
            ps.setString(pos, dto.getSvouno());
            pos++;

            //S_CRPVOUCODE
            ps.setString(pos, dto.getScrpvoucode());
            pos++;

            //D_VOUCHER
            ps.setDate(pos, dto.getDvoucher());
            pos++;

            //I_OFYEAR
            if (dto.getIofyear()==null)
                ps.setNull(pos, java.sql.Types.INTEGER);
            else
                ps.setInt(pos, dto.getIofyear().intValue());
            pos++;

            //D_ACCEPT
            ps.setDate(pos, dto.getDaccept());
            pos++;

            //D_ACCT
            ps.setDate(pos, dto.getDacct());
            pos++;

            //S_BIZTYPE
            ps.setString(pos, dto.getSbiztype());
            pos++;

            //S_PACKAGENO
            ps.setString(pos, dto.getSpackageno());
            pos++;

            //S_STATUS
            ps.setString(pos, dto.getSstatus());
            pos++;

            //S_BOOKORGCODE
            ps.setString(pos, dto.getSbookorgcode());
            pos++;

            //S_FILENAME
            ps.setString(pos, dto.getSfilename());
            pos++;

            //TS_SYSUPDATE
            //S_PAYEEOPNBNKNAME
            ps.setString(pos, dto.getSpayeeopnbnkname());
            pos++;

            //S_IFMATCH
            ps.setString(pos, dto.getSifmatch());
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
     
     	 TbsTvDirectpayplanMainDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (TbsTvDirectpayplanMainDto)_dtos[i] ; 
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
                dto  = (TbsTvDirectpayplanMainDto)_dtos[i] ; 
                int pos = 1;
                //S_TRECODE
                 ps.setString(pos, dto.getStrecode());
                pos++;

                //S_PAYERACCT
                 ps.setString(pos, dto.getSpayeracct());
                pos++;

                //S_PAYEROPNBNKNO
                 ps.setString(pos, dto.getSpayeropnbnkno());
                pos++;

                //S_PAYEEACCT
                 ps.setString(pos, dto.getSpayeeacct());
                pos++;

                //S_PAYEENAME
                 ps.setString(pos, dto.getSpayeename());
                pos++;

                //S_PAYEEOPNBNKNO
                 ps.setString(pos, dto.getSpayeeopnbnkno());
                pos++;

                //C_BDGKIND
                 ps.setString(pos, dto.getCbdgkind());
                pos++;

                //F_AMT
                 ps.setBigDecimal(pos, dto.getFamt());
                pos++;

                //S_VOUNO
                 ps.setString(pos, dto.getSvouno());
                pos++;

                //S_CRPVOUCODE
                 ps.setString(pos, dto.getScrpvoucode());
                pos++;

                //D_VOUCHER
                 ps.setDate(pos, dto.getDvoucher());
                pos++;

                //I_OFYEAR
                 if (dto.getIofyear()==null)
                   ps.setNull(pos, java.sql.Types.INTEGER);
                else
                   ps.setInt(pos, dto.getIofyear().intValue());
                pos++ ;

                //D_ACCEPT
                 ps.setDate(pos, dto.getDaccept());
                pos++;

                //D_ACCT
                 ps.setDate(pos, dto.getDacct());
                pos++;

                //S_BIZTYPE
                 ps.setString(pos, dto.getSbiztype());
                pos++;

                //S_PACKAGENO
                 ps.setString(pos, dto.getSpackageno());
                pos++;

                //S_STATUS
                 ps.setString(pos, dto.getSstatus());
                pos++;

                //S_BOOKORGCODE
                 ps.setString(pos, dto.getSbookorgcode());
                pos++;

                //S_FILENAME
                 ps.setString(pos, dto.getSfilename());
                pos++;

                //TS_SYSUPDATE
                 //S_PAYEEOPNBNKNAME
                 ps.setString(pos, dto.getSpayeeopnbnkname());
                pos++;

                //S_IFMATCH
                 ps.setString(pos, dto.getSifmatch());
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
       TbsTvDirectpayplanMainPK pk = (TbsTvDirectpayplanMainPK)_pk ;


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
        TbsTvDirectpayplanMainPK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TbsTvDirectpayplanMainPK)_pks[i] ; 
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
       			pk  = (TbsTvDirectpayplanMainPK)(pks.get(i)) ; 
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
                                                                  		throw new SQLException("数据库表：TBS_TV_DIRECTPAYPLAN_MAIN没有检查修改的字段");
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

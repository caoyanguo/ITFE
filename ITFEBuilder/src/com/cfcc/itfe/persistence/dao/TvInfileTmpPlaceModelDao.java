    



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

import com.cfcc.itfe.persistence.dto.TvInfileTmpPlaceModelDto ;
import com.cfcc.itfe.persistence.pk.TvInfileTmpPlaceModelPK ;


/**
 * <p>Title: DAO of table: TV_INFILE_TMP_PLACE_MODEL</p>
 * <p>Description: Data Access Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:03 </p>
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

public class TvInfileTmpPlaceModelDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO TV_INFILE_TMP_PLACE_MODEL ("
          + "S_OPERDATE,S_FLAGNO,S_OPERCODE,S_SEQNO,S_CORPCODE"
          + ",S_CORPNAME,S_TAXSTARTDATE,S_TAXENDDATE,S_TAXTYPECODE,S_TAXTYPENAME"
          + ",S_TAXSUBJECTNAME,I_TAXNUMBER,N_TAXAMT,N_DISCOUNTTAXAMT,N_TAXRATE"
          + ",N_FACTTAXAMT,C_FLAG,S_BILLDATE,S_LIMITDATE,S_BUDGETSUBJECTCODE"
          + ",S_BUDGETLEVELCODE,S_PAYOPBNKNO,S_PAYACCT,S_ENCOCODE,S_REFCODE"
          + ",S_TRECODE,S_TAXORGCODE,S_TAXORGNAME,S_TAXVOUNO"
        + ") VALUES ("
        + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "TV_INFILE_TMP_PLACE_MODEL.S_OPERDATE, TV_INFILE_TMP_PLACE_MODEL.S_FLAGNO, TV_INFILE_TMP_PLACE_MODEL.S_OPERCODE, TV_INFILE_TMP_PLACE_MODEL.S_SEQNO, TV_INFILE_TMP_PLACE_MODEL.S_CORPCODE, "
        + "TV_INFILE_TMP_PLACE_MODEL.S_CORPNAME, TV_INFILE_TMP_PLACE_MODEL.S_TAXSTARTDATE, TV_INFILE_TMP_PLACE_MODEL.S_TAXENDDATE, TV_INFILE_TMP_PLACE_MODEL.S_TAXTYPECODE, TV_INFILE_TMP_PLACE_MODEL.S_TAXTYPENAME, "
        + "TV_INFILE_TMP_PLACE_MODEL.S_TAXSUBJECTNAME, TV_INFILE_TMP_PLACE_MODEL.I_TAXNUMBER, TV_INFILE_TMP_PLACE_MODEL.N_TAXAMT, TV_INFILE_TMP_PLACE_MODEL.N_DISCOUNTTAXAMT, TV_INFILE_TMP_PLACE_MODEL.N_TAXRATE, "
        + "TV_INFILE_TMP_PLACE_MODEL.N_FACTTAXAMT, TV_INFILE_TMP_PLACE_MODEL.C_FLAG, TV_INFILE_TMP_PLACE_MODEL.S_BILLDATE, TV_INFILE_TMP_PLACE_MODEL.S_LIMITDATE, TV_INFILE_TMP_PLACE_MODEL.S_BUDGETSUBJECTCODE, "
        + "TV_INFILE_TMP_PLACE_MODEL.S_BUDGETLEVELCODE, TV_INFILE_TMP_PLACE_MODEL.S_PAYOPBNKNO, TV_INFILE_TMP_PLACE_MODEL.S_PAYACCT, TV_INFILE_TMP_PLACE_MODEL.S_ENCOCODE, TV_INFILE_TMP_PLACE_MODEL.S_REFCODE, "
        + "TV_INFILE_TMP_PLACE_MODEL.S_TRECODE, TV_INFILE_TMP_PLACE_MODEL.S_TAXORGCODE, TV_INFILE_TMP_PLACE_MODEL.S_TAXORGNAME, TV_INFILE_TMP_PLACE_MODEL.S_TAXVOUNO "
        + "FROM TV_INFILE_TMP_PLACE_MODEL "
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "TV_INFILE_TMP_PLACE_MODEL.S_OPERDATE, TV_INFILE_TMP_PLACE_MODEL.S_FLAGNO, TV_INFILE_TMP_PLACE_MODEL.S_OPERCODE, TV_INFILE_TMP_PLACE_MODEL.S_SEQNO, TV_INFILE_TMP_PLACE_MODEL.S_CORPCODE, "
        + "TV_INFILE_TMP_PLACE_MODEL.S_CORPNAME, TV_INFILE_TMP_PLACE_MODEL.S_TAXSTARTDATE, TV_INFILE_TMP_PLACE_MODEL.S_TAXENDDATE, TV_INFILE_TMP_PLACE_MODEL.S_TAXTYPECODE, TV_INFILE_TMP_PLACE_MODEL.S_TAXTYPENAME, "
        + "TV_INFILE_TMP_PLACE_MODEL.S_TAXSUBJECTNAME, TV_INFILE_TMP_PLACE_MODEL.I_TAXNUMBER, TV_INFILE_TMP_PLACE_MODEL.N_TAXAMT, TV_INFILE_TMP_PLACE_MODEL.N_DISCOUNTTAXAMT, TV_INFILE_TMP_PLACE_MODEL.N_TAXRATE, "
        + "TV_INFILE_TMP_PLACE_MODEL.N_FACTTAXAMT, TV_INFILE_TMP_PLACE_MODEL.C_FLAG, TV_INFILE_TMP_PLACE_MODEL.S_BILLDATE, TV_INFILE_TMP_PLACE_MODEL.S_LIMITDATE, TV_INFILE_TMP_PLACE_MODEL.S_BUDGETSUBJECTCODE, "
        + "TV_INFILE_TMP_PLACE_MODEL.S_BUDGETLEVELCODE, TV_INFILE_TMP_PLACE_MODEL.S_PAYOPBNKNO, TV_INFILE_TMP_PLACE_MODEL.S_PAYACCT, TV_INFILE_TMP_PLACE_MODEL.S_ENCOCODE, TV_INFILE_TMP_PLACE_MODEL.S_REFCODE, "
        + "TV_INFILE_TMP_PLACE_MODEL.S_TRECODE, TV_INFILE_TMP_PLACE_MODEL.S_TAXORGCODE, TV_INFILE_TMP_PLACE_MODEL.S_TAXORGNAME, TV_INFILE_TMP_PLACE_MODEL.S_TAXVOUNO "
        + "FROM TV_INFILE_TMP_PLACE_MODEL "
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  TV_INFILE_TMP_PLACE_MODEL.S_OPERDATE  , TV_INFILE_TMP_PLACE_MODEL.S_FLAGNO  , TV_INFILE_TMP_PLACE_MODEL.S_OPERCODE  , TV_INFILE_TMP_PLACE_MODEL.S_SEQNO  , TV_INFILE_TMP_PLACE_MODEL.S_CORPCODE "
        + " , TV_INFILE_TMP_PLACE_MODEL.S_CORPNAME  , TV_INFILE_TMP_PLACE_MODEL.S_TAXSTARTDATE  , TV_INFILE_TMP_PLACE_MODEL.S_TAXENDDATE  , TV_INFILE_TMP_PLACE_MODEL.S_TAXTYPECODE  , TV_INFILE_TMP_PLACE_MODEL.S_TAXTYPENAME "
        + " , TV_INFILE_TMP_PLACE_MODEL.S_TAXSUBJECTNAME  , TV_INFILE_TMP_PLACE_MODEL.I_TAXNUMBER  , TV_INFILE_TMP_PLACE_MODEL.N_TAXAMT  , TV_INFILE_TMP_PLACE_MODEL.N_DISCOUNTTAXAMT  , TV_INFILE_TMP_PLACE_MODEL.N_TAXRATE "
        + " , TV_INFILE_TMP_PLACE_MODEL.N_FACTTAXAMT  , TV_INFILE_TMP_PLACE_MODEL.C_FLAG  , TV_INFILE_TMP_PLACE_MODEL.S_BILLDATE  , TV_INFILE_TMP_PLACE_MODEL.S_LIMITDATE  , TV_INFILE_TMP_PLACE_MODEL.S_BUDGETSUBJECTCODE "
        + " , TV_INFILE_TMP_PLACE_MODEL.S_BUDGETLEVELCODE  , TV_INFILE_TMP_PLACE_MODEL.S_PAYOPBNKNO  , TV_INFILE_TMP_PLACE_MODEL.S_PAYACCT  , TV_INFILE_TMP_PLACE_MODEL.S_ENCOCODE  , TV_INFILE_TMP_PLACE_MODEL.S_REFCODE "
        + " , TV_INFILE_TMP_PLACE_MODEL.S_TRECODE  , TV_INFILE_TMP_PLACE_MODEL.S_TAXORGCODE  , TV_INFILE_TMP_PLACE_MODEL.S_TAXORGNAME  , TV_INFILE_TMP_PLACE_MODEL.S_TAXVOUNO "
        + "FROM TV_INFILE_TMP_PLACE_MODEL ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "TV_INFILE_TMP_PLACE_MODEL.S_OPERDATE, TV_INFILE_TMP_PLACE_MODEL.S_FLAGNO, TV_INFILE_TMP_PLACE_MODEL.S_OPERCODE, TV_INFILE_TMP_PLACE_MODEL.S_SEQNO, TV_INFILE_TMP_PLACE_MODEL.S_CORPCODE, "
        + "TV_INFILE_TMP_PLACE_MODEL.S_CORPNAME, TV_INFILE_TMP_PLACE_MODEL.S_TAXSTARTDATE, TV_INFILE_TMP_PLACE_MODEL.S_TAXENDDATE, TV_INFILE_TMP_PLACE_MODEL.S_TAXTYPECODE, TV_INFILE_TMP_PLACE_MODEL.S_TAXTYPENAME, "
        + "TV_INFILE_TMP_PLACE_MODEL.S_TAXSUBJECTNAME, TV_INFILE_TMP_PLACE_MODEL.I_TAXNUMBER, TV_INFILE_TMP_PLACE_MODEL.N_TAXAMT, TV_INFILE_TMP_PLACE_MODEL.N_DISCOUNTTAXAMT, TV_INFILE_TMP_PLACE_MODEL.N_TAXRATE, "
        + "TV_INFILE_TMP_PLACE_MODEL.N_FACTTAXAMT, TV_INFILE_TMP_PLACE_MODEL.C_FLAG, TV_INFILE_TMP_PLACE_MODEL.S_BILLDATE, TV_INFILE_TMP_PLACE_MODEL.S_LIMITDATE, TV_INFILE_TMP_PLACE_MODEL.S_BUDGETSUBJECTCODE, "
        + "TV_INFILE_TMP_PLACE_MODEL.S_BUDGETLEVELCODE, TV_INFILE_TMP_PLACE_MODEL.S_PAYOPBNKNO, TV_INFILE_TMP_PLACE_MODEL.S_PAYACCT, TV_INFILE_TMP_PLACE_MODEL.S_ENCOCODE, TV_INFILE_TMP_PLACE_MODEL.S_REFCODE, "
        + "TV_INFILE_TMP_PLACE_MODEL.S_TRECODE, TV_INFILE_TMP_PLACE_MODEL.S_TAXORGCODE, TV_INFILE_TMP_PLACE_MODEL.S_TAXORGNAME, TV_INFILE_TMP_PLACE_MODEL.S_TAXVOUNO "
        + "FROM TV_INFILE_TMP_PLACE_MODEL " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE TV_INFILE_TMP_PLACE_MODEL SET "
        + "S_OPERDATE =?,S_FLAGNO =?,S_OPERCODE =?,S_SEQNO =?,S_CORPCODE =?, "
        + "S_CORPNAME =?,S_TAXSTARTDATE =?,S_TAXENDDATE =?,S_TAXTYPECODE =?,S_TAXTYPENAME =?, "
        + "S_TAXSUBJECTNAME =?,I_TAXNUMBER =?,N_TAXAMT =?,N_DISCOUNTTAXAMT =?,N_TAXRATE =?, "
        + "N_FACTTAXAMT =?,C_FLAG =?,S_BILLDATE =?,S_LIMITDATE =?,S_BUDGETSUBJECTCODE =?, "
        + "S_BUDGETLEVELCODE =?,S_PAYOPBNKNO =?,S_PAYACCT =?,S_ENCOCODE =?,S_REFCODE =?, "
        + "S_TRECODE =?,S_TAXORGCODE =?,S_TAXORGNAME =?,S_TAXVOUNO =? "
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE TV_INFILE_TMP_PLACE_MODEL SET "
        + "S_OPERDATE =?, S_FLAGNO =?, S_OPERCODE =?, S_SEQNO =?, S_CORPCODE =?,  "
        + "S_CORPNAME =?, S_TAXSTARTDATE =?, S_TAXENDDATE =?, S_TAXTYPECODE =?, S_TAXTYPENAME =?,  "
        + "S_TAXSUBJECTNAME =?, I_TAXNUMBER =?, N_TAXAMT =?, N_DISCOUNTTAXAMT =?, N_TAXRATE =?,  "
        + "N_FACTTAXAMT =?, C_FLAG =?, S_BILLDATE =?, S_LIMITDATE =?, S_BUDGETSUBJECTCODE =?,  "
        + "S_BUDGETLEVELCODE =?, S_PAYOPBNKNO =?, S_PAYACCT =?, S_ENCOCODE =?, S_REFCODE =?,  "
        + "S_TRECODE =?, S_TAXORGCODE =?, S_TAXORGNAME =?, S_TAXVOUNO =? "
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM TV_INFILE_TMP_PLACE_MODEL " 
        ;


	/**
	*  批量查询 一次最多查询的参数数量
	*/
	
	public static final int FIND_BATCH_SIZE = 150 ;
	



   /**
   * Create a new record in Database.
   */
  public void create(IDto _dto,  Connection conn) throws SQLException
  {
     TvInfileTmpPlaceModelDto dto = (TvInfileTmpPlaceModelDto)_dto ;
     String msgValid = dto.checkValid() ;
     if (msgValid != null)
     	throw new SQLException("插入错误，"+msgValid) ;

     PreparedStatement ps = null;
     try
     {
         ps = conn.prepareStatement(SQL_INSERT);
          ps.setString(1, dto.getSoperdate());

          ps.setString(2, dto.getSflagno());

          ps.setString(3, dto.getSopercode());

          ps.setString(4, dto.getSseqno());

          ps.setString(5, dto.getScorpcode());

          ps.setString(6, dto.getScorpname());

          ps.setString(7, dto.getStaxstartdate());

          ps.setString(8, dto.getStaxenddate());

          ps.setString(9, dto.getStaxtypecode());

          ps.setString(10, dto.getStaxtypename());

          ps.setString(11, dto.getStaxsubjectname());

          if (dto.getItaxnumber()==null)
            ps.setNull(12, java.sql.Types.INTEGER);
         else
            ps.setInt(12, dto.getItaxnumber().intValue());
          ps.setBigDecimal(13, dto.getNtaxamt());

          ps.setBigDecimal(14, dto.getNdiscounttaxamt());

          ps.setBigDecimal(15, dto.getNtaxrate());

          ps.setBigDecimal(16, dto.getNfacttaxamt());

          ps.setString(17, dto.getCflag());

          ps.setString(18, dto.getSbilldate());

          ps.setString(19, dto.getSlimitdate());

          ps.setString(20, dto.getSbudgetsubjectcode());

          ps.setString(21, dto.getSbudgetlevelcode());

          ps.setString(22, dto.getSpayopbnkno());

          ps.setString(23, dto.getSpayacct());

          ps.setString(24, dto.getSencocode());

          ps.setString(25, dto.getSrefcode());

          ps.setString(26, dto.getStrecode());

          ps.setString(27, dto.getStaxorgcode());

          ps.setString(28, dto.getStaxorgname());

          ps.setString(29, dto.getStaxvouno());

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
       TvInfileTmpPlaceModelDto dto = (TvInfileTmpPlaceModelDto)_dto ;
       String msgValid = dto.checkValid() ;
       if (msgValid != null)
           throw new SQLException("插入错误，"+msgValid) ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try
       {
           ps = conn.prepareStatement(SQL_INSERT_WITH_RESULT);
            ps.setString(1, dto.getSoperdate());
            ps.setString(2, dto.getSflagno());
            ps.setString(3, dto.getSopercode());
            ps.setString(4, dto.getSseqno());
            ps.setString(5, dto.getScorpcode());
            ps.setString(6, dto.getScorpname());
            ps.setString(7, dto.getStaxstartdate());
            ps.setString(8, dto.getStaxenddate());
            ps.setString(9, dto.getStaxtypecode());
            ps.setString(10, dto.getStaxtypename());
            ps.setString(11, dto.getStaxsubjectname());
            if (dto.getItaxnumber()==null)
              ps.setNull(12, java.sql.Types.INTEGER);
           else
              ps.setInt(12, dto.getItaxnumber().intValue());
            ps.setBigDecimal(13, dto.getNtaxamt());
            ps.setBigDecimal(14, dto.getNdiscounttaxamt());
            ps.setBigDecimal(15, dto.getNtaxrate());
            ps.setBigDecimal(16, dto.getNfacttaxamt());
            ps.setString(17, dto.getCflag());
            ps.setString(18, dto.getSbilldate());
            ps.setString(19, dto.getSlimitdate());
            ps.setString(20, dto.getSbudgetsubjectcode());
            ps.setString(21, dto.getSbudgetlevelcode());
            ps.setString(22, dto.getSpayopbnkno());
            ps.setString(23, dto.getSpayacct());
            ps.setString(24, dto.getSencocode());
            ps.setString(25, dto.getSrefcode());
            ps.setString(26, dto.getStrecode());
            ps.setString(27, dto.getStaxorgcode());
            ps.setString(28, dto.getStaxorgname());
            ps.setString(29, dto.getStaxvouno());
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
        TvInfileTmpPlaceModelDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (TvInfileTmpPlaceModelDto)_dtos[i] ; 
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
 	    	    dto  = (TvInfileTmpPlaceModelDto)_dtos[i] ; 
  
               ps.setString(1, dto.getSoperdate());
  
               ps.setString(2, dto.getSflagno());
  
               ps.setString(3, dto.getSopercode());
  
               ps.setString(4, dto.getSseqno());
  
               ps.setString(5, dto.getScorpcode());
  
               ps.setString(6, dto.getScorpname());
  
               ps.setString(7, dto.getStaxstartdate());
  
               ps.setString(8, dto.getStaxenddate());
  
               ps.setString(9, dto.getStaxtypecode());
  
               ps.setString(10, dto.getStaxtypename());
  
               ps.setString(11, dto.getStaxsubjectname());
  
               if (dto.getItaxnumber()==null)
                  ps.setNull(12, java.sql.Types.INTEGER);
               else
                  ps.setInt(12, dto.getItaxnumber().intValue());
  
               ps.setBigDecimal(13, dto.getNtaxamt());
  
               ps.setBigDecimal(14, dto.getNdiscounttaxamt());
  
               ps.setBigDecimal(15, dto.getNtaxrate());
  
               ps.setBigDecimal(16, dto.getNfacttaxamt());
  
               ps.setString(17, dto.getCflag());
  
               ps.setString(18, dto.getSbilldate());
  
               ps.setString(19, dto.getSlimitdate());
  
               ps.setString(20, dto.getSbudgetsubjectcode());
  
               ps.setString(21, dto.getSbudgetlevelcode());
  
               ps.setString(22, dto.getSpayopbnkno());
  
               ps.setString(23, dto.getSpayacct());
  
               ps.setString(24, dto.getSencocode());
  
               ps.setString(25, dto.getSrefcode());
  
               ps.setString(26, dto.getStrecode());
  
               ps.setString(27, dto.getStaxorgcode());
  
               ps.setString(28, dto.getStaxorgname());
  
               ps.setString(29, dto.getStaxvouno());
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
        	
       TvInfileTmpPlaceModelPK pk = (TvInfileTmpPlaceModelPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT);
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
        	
       TvInfileTmpPlaceModelPK pk = (TvInfileTmpPlaceModelPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT_FOR_UPDATE);
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
        TvInfileTmpPlaceModelPK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TvInfileTmpPlaceModelPK)_pks[i] ; 
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
                    pk  = (TvInfileTmpPlaceModelPK)(pks.get(i)) ; 
			
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
            TvInfileTmpPlaceModelDto[] dtos = new TvInfileTmpPlaceModelDto[0];
		    dtos = (TvInfileTmpPlaceModelDto[]) results.toArray(dtos) ;
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
             TvInfileTmpPlaceModelDto  dto = new  TvInfileTmpPlaceModelDto ();
             //S_OPERDATE
             str = rs.getString("S_OPERDATE");
             if (str == null)
                dto.setSoperdate(null);
             else
                dto.setSoperdate(str.trim());

             //S_FLAGNO
             str = rs.getString("S_FLAGNO");
             if (str == null)
                dto.setSflagno(null);
             else
                dto.setSflagno(str.trim());

             //S_OPERCODE
             str = rs.getString("S_OPERCODE");
             if (str == null)
                dto.setSopercode(null);
             else
                dto.setSopercode(str.trim());

             //S_SEQNO
             str = rs.getString("S_SEQNO");
             if (str == null)
                dto.setSseqno(null);
             else
                dto.setSseqno(str.trim());

             //S_CORPCODE
             str = rs.getString("S_CORPCODE");
             if (str == null)
                dto.setScorpcode(null);
             else
                dto.setScorpcode(str.trim());

             //S_CORPNAME
             str = rs.getString("S_CORPNAME");
             if (str == null)
                dto.setScorpname(null);
             else
                dto.setScorpname(str.trim());

             //S_TAXSTARTDATE
             str = rs.getString("S_TAXSTARTDATE");
             if (str == null)
                dto.setStaxstartdate(null);
             else
                dto.setStaxstartdate(str.trim());

             //S_TAXENDDATE
             str = rs.getString("S_TAXENDDATE");
             if (str == null)
                dto.setStaxenddate(null);
             else
                dto.setStaxenddate(str.trim());

             //S_TAXTYPECODE
             str = rs.getString("S_TAXTYPECODE");
             if (str == null)
                dto.setStaxtypecode(null);
             else
                dto.setStaxtypecode(str.trim());

             //S_TAXTYPENAME
             str = rs.getString("S_TAXTYPENAME");
             if (str == null)
                dto.setStaxtypename(null);
             else
                dto.setStaxtypename(str.trim());

             //S_TAXSUBJECTNAME
             str = rs.getString("S_TAXSUBJECTNAME");
             if (str == null)
                dto.setStaxsubjectname(null);
             else
                dto.setStaxsubjectname(str.trim());

             //I_TAXNUMBER
             str = rs.getString("I_TAXNUMBER");
             if(str!=null){
                dto.setItaxnumber(new Integer(str));
             }

             //N_TAXAMT
           dto.setNtaxamt(rs.getBigDecimal("N_TAXAMT"));

             //N_DISCOUNTTAXAMT
           dto.setNdiscounttaxamt(rs.getBigDecimal("N_DISCOUNTTAXAMT"));

             //N_TAXRATE
           dto.setNtaxrate(rs.getBigDecimal("N_TAXRATE"));

             //N_FACTTAXAMT
           dto.setNfacttaxamt(rs.getBigDecimal("N_FACTTAXAMT"));

             //C_FLAG
             str = rs.getString("C_FLAG");
             if (str == null)
                dto.setCflag(null);
             else
                dto.setCflag(str.trim());

             //S_BILLDATE
             str = rs.getString("S_BILLDATE");
             if (str == null)
                dto.setSbilldate(null);
             else
                dto.setSbilldate(str.trim());

             //S_LIMITDATE
             str = rs.getString("S_LIMITDATE");
             if (str == null)
                dto.setSlimitdate(null);
             else
                dto.setSlimitdate(str.trim());

             //S_BUDGETSUBJECTCODE
             str = rs.getString("S_BUDGETSUBJECTCODE");
             if (str == null)
                dto.setSbudgetsubjectcode(null);
             else
                dto.setSbudgetsubjectcode(str.trim());

             //S_BUDGETLEVELCODE
             str = rs.getString("S_BUDGETLEVELCODE");
             if (str == null)
                dto.setSbudgetlevelcode(null);
             else
                dto.setSbudgetlevelcode(str.trim());

             //S_PAYOPBNKNO
             str = rs.getString("S_PAYOPBNKNO");
             if (str == null)
                dto.setSpayopbnkno(null);
             else
                dto.setSpayopbnkno(str.trim());

             //S_PAYACCT
             str = rs.getString("S_PAYACCT");
             if (str == null)
                dto.setSpayacct(null);
             else
                dto.setSpayacct(str.trim());

             //S_ENCOCODE
             str = rs.getString("S_ENCOCODE");
             if (str == null)
                dto.setSencocode(null);
             else
                dto.setSencocode(str.trim());

             //S_REFCODE
             str = rs.getString("S_REFCODE");
             if (str == null)
                dto.setSrefcode(null);
             else
                dto.setSrefcode(str.trim());

             //S_TRECODE
             str = rs.getString("S_TRECODE");
             if (str == null)
                dto.setStrecode(null);
             else
                dto.setStrecode(str.trim());

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

             //S_TAXVOUNO
             str = rs.getString("S_TAXVOUNO");
             if (str == null)
                dto.setStaxvouno(null);
             else
                dto.setStaxvouno(str.trim());



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
        TvInfileTmpPlaceModelDto dto = (TvInfileTmpPlaceModelDto)_dto ;
        PreparedStatement ps = null;
        try {
            if(isLobSupport){
                ps = conn.prepareStatement(SQL_UPDATE_LOB);
            }
            else{
                ps = conn.prepareStatement(SQL_UPDATE);
            }
            int pos = 1;
            //S_OPERDATE
            ps.setString(pos, dto.getSoperdate());
            pos++;

            //S_FLAGNO
            ps.setString(pos, dto.getSflagno());
            pos++;

            //S_OPERCODE
            ps.setString(pos, dto.getSopercode());
            pos++;

            //S_SEQNO
            ps.setString(pos, dto.getSseqno());
            pos++;

            //S_CORPCODE
            ps.setString(pos, dto.getScorpcode());
            pos++;

            //S_CORPNAME
            ps.setString(pos, dto.getScorpname());
            pos++;

            //S_TAXSTARTDATE
            ps.setString(pos, dto.getStaxstartdate());
            pos++;

            //S_TAXENDDATE
            ps.setString(pos, dto.getStaxenddate());
            pos++;

            //S_TAXTYPECODE
            ps.setString(pos, dto.getStaxtypecode());
            pos++;

            //S_TAXTYPENAME
            ps.setString(pos, dto.getStaxtypename());
            pos++;

            //S_TAXSUBJECTNAME
            ps.setString(pos, dto.getStaxsubjectname());
            pos++;

            //I_TAXNUMBER
            if (dto.getItaxnumber()==null)
                ps.setNull(pos, java.sql.Types.INTEGER);
            else
                ps.setInt(pos, dto.getItaxnumber().intValue());
            pos++;

            //N_TAXAMT
            ps.setBigDecimal(pos, dto.getNtaxamt());
            pos++;

            //N_DISCOUNTTAXAMT
            ps.setBigDecimal(pos, dto.getNdiscounttaxamt());
            pos++;

            //N_TAXRATE
            ps.setBigDecimal(pos, dto.getNtaxrate());
            pos++;

            //N_FACTTAXAMT
            ps.setBigDecimal(pos, dto.getNfacttaxamt());
            pos++;

            //C_FLAG
            ps.setString(pos, dto.getCflag());
            pos++;

            //S_BILLDATE
            ps.setString(pos, dto.getSbilldate());
            pos++;

            //S_LIMITDATE
            ps.setString(pos, dto.getSlimitdate());
            pos++;

            //S_BUDGETSUBJECTCODE
            ps.setString(pos, dto.getSbudgetsubjectcode());
            pos++;

            //S_BUDGETLEVELCODE
            ps.setString(pos, dto.getSbudgetlevelcode());
            pos++;

            //S_PAYOPBNKNO
            ps.setString(pos, dto.getSpayopbnkno());
            pos++;

            //S_PAYACCT
            ps.setString(pos, dto.getSpayacct());
            pos++;

            //S_ENCOCODE
            ps.setString(pos, dto.getSencocode());
            pos++;

            //S_REFCODE
            ps.setString(pos, dto.getSrefcode());
            pos++;

            //S_TRECODE
            ps.setString(pos, dto.getStrecode());
            pos++;

            //S_TAXORGCODE
            ps.setString(pos, dto.getStaxorgcode());
            pos++;

            //S_TAXORGNAME
            ps.setString(pos, dto.getStaxorgname());
            pos++;

            //S_TAXVOUNO
            ps.setString(pos, dto.getStaxvouno());
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
     
     	 TvInfileTmpPlaceModelDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (TvInfileTmpPlaceModelDto)_dtos[i] ; 
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
                dto  = (TvInfileTmpPlaceModelDto)_dtos[i] ; 
                int pos = 1;
                //S_OPERDATE
                 ps.setString(pos, dto.getSoperdate());
                pos++;

                //S_FLAGNO
                 ps.setString(pos, dto.getSflagno());
                pos++;

                //S_OPERCODE
                 ps.setString(pos, dto.getSopercode());
                pos++;

                //S_SEQNO
                 ps.setString(pos, dto.getSseqno());
                pos++;

                //S_CORPCODE
                 ps.setString(pos, dto.getScorpcode());
                pos++;

                //S_CORPNAME
                 ps.setString(pos, dto.getScorpname());
                pos++;

                //S_TAXSTARTDATE
                 ps.setString(pos, dto.getStaxstartdate());
                pos++;

                //S_TAXENDDATE
                 ps.setString(pos, dto.getStaxenddate());
                pos++;

                //S_TAXTYPECODE
                 ps.setString(pos, dto.getStaxtypecode());
                pos++;

                //S_TAXTYPENAME
                 ps.setString(pos, dto.getStaxtypename());
                pos++;

                //S_TAXSUBJECTNAME
                 ps.setString(pos, dto.getStaxsubjectname());
                pos++;

                //I_TAXNUMBER
                 if (dto.getItaxnumber()==null)
                   ps.setNull(pos, java.sql.Types.INTEGER);
                else
                   ps.setInt(pos, dto.getItaxnumber().intValue());
                pos++ ;

                //N_TAXAMT
                 ps.setBigDecimal(pos, dto.getNtaxamt());
                pos++;

                //N_DISCOUNTTAXAMT
                 ps.setBigDecimal(pos, dto.getNdiscounttaxamt());
                pos++;

                //N_TAXRATE
                 ps.setBigDecimal(pos, dto.getNtaxrate());
                pos++;

                //N_FACTTAXAMT
                 ps.setBigDecimal(pos, dto.getNfacttaxamt());
                pos++;

                //C_FLAG
                 ps.setString(pos, dto.getCflag());
                pos++;

                //S_BILLDATE
                 ps.setString(pos, dto.getSbilldate());
                pos++;

                //S_LIMITDATE
                 ps.setString(pos, dto.getSlimitdate());
                pos++;

                //S_BUDGETSUBJECTCODE
                 ps.setString(pos, dto.getSbudgetsubjectcode());
                pos++;

                //S_BUDGETLEVELCODE
                 ps.setString(pos, dto.getSbudgetlevelcode());
                pos++;

                //S_PAYOPBNKNO
                 ps.setString(pos, dto.getSpayopbnkno());
                pos++;

                //S_PAYACCT
                 ps.setString(pos, dto.getSpayacct());
                pos++;

                //S_ENCOCODE
                 ps.setString(pos, dto.getSencocode());
                pos++;

                //S_REFCODE
                 ps.setString(pos, dto.getSrefcode());
                pos++;

                //S_TRECODE
                 ps.setString(pos, dto.getStrecode());
                pos++;

                //S_TAXORGCODE
                 ps.setString(pos, dto.getStaxorgcode());
                pos++;

                //S_TAXORGNAME
                 ps.setString(pos, dto.getStaxorgname());
                pos++;

                //S_TAXVOUNO
                 ps.setString(pos, dto.getStaxvouno());
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
       TvInfileTmpPlaceModelPK pk = (TvInfileTmpPlaceModelPK)_pk ;


       PreparedStatement ps = null;
       try {
           ps = conn.prepareStatement(SQL_DELETE);
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
        TvInfileTmpPlaceModelPK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TvInfileTmpPlaceModelPK)_pks[i] ; 
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
       			pk  = (TvInfileTmpPlaceModelPK)(pks.get(i)) ; 
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
                                                                                       		throw new SQLException("数据库表：TV_INFILE_TMP_PLACE_MODEL没有检查修改的字段");
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

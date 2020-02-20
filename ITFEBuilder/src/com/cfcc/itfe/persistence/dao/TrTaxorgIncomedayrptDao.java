    



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

import com.cfcc.itfe.persistence.dto.TrTaxorgIncomedayrptDto ;
import com.cfcc.itfe.persistence.pk.TrTaxorgIncomedayrptPK ;


/**
 * <p>Title: DAO of table: TR_TAXORG_INCOMEDAYRPT</p>
 * <p>Description: Data Access Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:59 </p>
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

public class TrTaxorgIncomedayrptDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO TR_TAXORG_INCOMEDAYRPT ("
          + "S_TAXORGCODE,S_TRECODE,S_RPTDATE,S_BUDGETTYPE,S_BUDGETLEVELCODE"
          + ",S_BUDGETSUBCODE,S_BUDGETSUBNAME,N_MONEYDAY,N_MONEYTENDAY,N_MONEYMONTH"
          + ",N_MONEYQUARTER,N_MONEYYEAR,S_BELONGFLAG,S_TRIMFLAG,S_DIVIDEGROUP"
          + ",S_BILLKIND"
        + ") VALUES ("
        + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "TR_TAXORG_INCOMEDAYRPT.S_TAXORGCODE, TR_TAXORG_INCOMEDAYRPT.S_TRECODE, TR_TAXORG_INCOMEDAYRPT.S_RPTDATE, TR_TAXORG_INCOMEDAYRPT.S_BUDGETTYPE, TR_TAXORG_INCOMEDAYRPT.S_BUDGETLEVELCODE, "
        + "TR_TAXORG_INCOMEDAYRPT.S_BUDGETSUBCODE, TR_TAXORG_INCOMEDAYRPT.S_BUDGETSUBNAME, TR_TAXORG_INCOMEDAYRPT.N_MONEYDAY, TR_TAXORG_INCOMEDAYRPT.N_MONEYTENDAY, TR_TAXORG_INCOMEDAYRPT.N_MONEYMONTH, "
        + "TR_TAXORG_INCOMEDAYRPT.N_MONEYQUARTER, TR_TAXORG_INCOMEDAYRPT.N_MONEYYEAR, TR_TAXORG_INCOMEDAYRPT.S_BELONGFLAG, TR_TAXORG_INCOMEDAYRPT.S_TRIMFLAG, TR_TAXORG_INCOMEDAYRPT.S_DIVIDEGROUP, "
        + "TR_TAXORG_INCOMEDAYRPT.S_BILLKIND "
        + "FROM TR_TAXORG_INCOMEDAYRPT "
        +" WHERE " 
        + "S_TAXORGCODE = ? AND S_TRECODE = ? AND S_RPTDATE = ? AND S_BUDGETTYPE = ? AND S_BUDGETLEVELCODE = ? AND "
        + "S_BUDGETSUBCODE = ? AND S_DIVIDEGROUP = ? AND S_BILLKIND = ?"
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "TR_TAXORG_INCOMEDAYRPT.S_TAXORGCODE, TR_TAXORG_INCOMEDAYRPT.S_TRECODE, TR_TAXORG_INCOMEDAYRPT.S_RPTDATE, TR_TAXORG_INCOMEDAYRPT.S_BUDGETTYPE, TR_TAXORG_INCOMEDAYRPT.S_BUDGETLEVELCODE, "
        + "TR_TAXORG_INCOMEDAYRPT.S_BUDGETSUBCODE, TR_TAXORG_INCOMEDAYRPT.S_BUDGETSUBNAME, TR_TAXORG_INCOMEDAYRPT.N_MONEYDAY, TR_TAXORG_INCOMEDAYRPT.N_MONEYTENDAY, TR_TAXORG_INCOMEDAYRPT.N_MONEYMONTH, "
        + "TR_TAXORG_INCOMEDAYRPT.N_MONEYQUARTER, TR_TAXORG_INCOMEDAYRPT.N_MONEYYEAR, TR_TAXORG_INCOMEDAYRPT.S_BELONGFLAG, TR_TAXORG_INCOMEDAYRPT.S_TRIMFLAG, TR_TAXORG_INCOMEDAYRPT.S_DIVIDEGROUP, "
        + "TR_TAXORG_INCOMEDAYRPT.S_BILLKIND "
        + "FROM TR_TAXORG_INCOMEDAYRPT "
        +" WHERE " 
        + "S_TAXORGCODE = ? AND S_TRECODE = ? AND S_RPTDATE = ? AND S_BUDGETTYPE = ? AND S_BUDGETLEVELCODE = ? AND  FOR UPDATE"
        + "S_BUDGETSUBCODE = ? AND S_DIVIDEGROUP = ? AND S_BILLKIND = ? FOR UPDATE"
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  TR_TAXORG_INCOMEDAYRPT.S_TAXORGCODE  , TR_TAXORG_INCOMEDAYRPT.S_TRECODE  , TR_TAXORG_INCOMEDAYRPT.S_RPTDATE  , TR_TAXORG_INCOMEDAYRPT.S_BUDGETTYPE  , TR_TAXORG_INCOMEDAYRPT.S_BUDGETLEVELCODE "
        + " , TR_TAXORG_INCOMEDAYRPT.S_BUDGETSUBCODE  , TR_TAXORG_INCOMEDAYRPT.S_BUDGETSUBNAME  , TR_TAXORG_INCOMEDAYRPT.N_MONEYDAY  , TR_TAXORG_INCOMEDAYRPT.N_MONEYTENDAY  , TR_TAXORG_INCOMEDAYRPT.N_MONEYMONTH "
        + " , TR_TAXORG_INCOMEDAYRPT.N_MONEYQUARTER  , TR_TAXORG_INCOMEDAYRPT.N_MONEYYEAR  , TR_TAXORG_INCOMEDAYRPT.S_BELONGFLAG  , TR_TAXORG_INCOMEDAYRPT.S_TRIMFLAG  , TR_TAXORG_INCOMEDAYRPT.S_DIVIDEGROUP "
        + " , TR_TAXORG_INCOMEDAYRPT.S_BILLKIND "
        + "FROM TR_TAXORG_INCOMEDAYRPT ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "TR_TAXORG_INCOMEDAYRPT.S_TAXORGCODE, TR_TAXORG_INCOMEDAYRPT.S_TRECODE, TR_TAXORG_INCOMEDAYRPT.S_RPTDATE, TR_TAXORG_INCOMEDAYRPT.S_BUDGETTYPE, TR_TAXORG_INCOMEDAYRPT.S_BUDGETLEVELCODE, "
        + "TR_TAXORG_INCOMEDAYRPT.S_BUDGETSUBCODE, TR_TAXORG_INCOMEDAYRPT.S_BUDGETSUBNAME, TR_TAXORG_INCOMEDAYRPT.N_MONEYDAY, TR_TAXORG_INCOMEDAYRPT.N_MONEYTENDAY, TR_TAXORG_INCOMEDAYRPT.N_MONEYMONTH, "
        + "TR_TAXORG_INCOMEDAYRPT.N_MONEYQUARTER, TR_TAXORG_INCOMEDAYRPT.N_MONEYYEAR, TR_TAXORG_INCOMEDAYRPT.S_BELONGFLAG, TR_TAXORG_INCOMEDAYRPT.S_TRIMFLAG, TR_TAXORG_INCOMEDAYRPT.S_DIVIDEGROUP, "
        + "TR_TAXORG_INCOMEDAYRPT.S_BILLKIND "
        + "FROM TR_TAXORG_INCOMEDAYRPT " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        + "S_TAXORGCODE = ? AND S_TRECODE = ? AND S_RPTDATE = ? AND S_BUDGETTYPE = ? AND S_BUDGETLEVELCODE = ? AND )"
        + "S_BUDGETSUBCODE = ? AND S_DIVIDEGROUP = ? AND S_BILLKIND = ?)"
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE TR_TAXORG_INCOMEDAYRPT SET "
        + "S_BUDGETSUBNAME =?,N_MONEYDAY =?,N_MONEYTENDAY =?,N_MONEYMONTH =?,N_MONEYQUARTER =?, "
        + "N_MONEYYEAR =?,S_BELONGFLAG =?,S_TRIMFLAG =? "
        + "WHERE "
        + "S_TAXORGCODE = ? AND S_TRECODE = ? AND S_RPTDATE = ? AND S_BUDGETTYPE = ? AND S_BUDGETLEVELCODE = ? AND "
        + "S_BUDGETSUBCODE = ? AND S_DIVIDEGROUP = ? AND S_BILLKIND = ?"
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE TR_TAXORG_INCOMEDAYRPT SET "
        + "S_BUDGETSUBNAME =?, N_MONEYDAY =?, N_MONEYTENDAY =?, N_MONEYMONTH =?, N_MONEYQUARTER =?,  "
        + "N_MONEYYEAR =?, S_BELONGFLAG =?, S_TRIMFLAG =? "
        + "WHERE "
        + "S_TAXORGCODE = ? AND S_TRECODE = ? AND S_RPTDATE = ? AND S_BUDGETTYPE = ? AND S_BUDGETLEVELCODE = ? AND "
        + "S_BUDGETSUBCODE = ? AND S_DIVIDEGROUP = ? AND S_BILLKIND = ?"
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM TR_TAXORG_INCOMEDAYRPT " 
        + " WHERE "
        + "S_TAXORGCODE = ? AND S_TRECODE = ? AND S_RPTDATE = ? AND S_BUDGETTYPE = ? AND S_BUDGETLEVELCODE = ? AND "
        + "S_BUDGETSUBCODE = ? AND S_DIVIDEGROUP = ? AND S_BILLKIND = ?"
        ;


	/**
	*  ������ѯ һ������ѯ�Ĳ�������
	*/
	
	public static final int FIND_BATCH_SIZE = 150  / 8;
	



   /**
   * Create a new record in Database.
   */
  public void create(IDto _dto,  Connection conn) throws SQLException
  {
     TrTaxorgIncomedayrptDto dto = (TrTaxorgIncomedayrptDto)_dto ;
     String msgValid = dto.checkValid() ;
     if (msgValid != null)
     	throw new SQLException("�������"+msgValid) ;

     PreparedStatement ps = null;
     try
     {
         ps = conn.prepareStatement(SQL_INSERT);
          ps.setString(1, dto.getStaxorgcode());

          ps.setString(2, dto.getStrecode());

          ps.setString(3, dto.getSrptdate());

          ps.setString(4, dto.getSbudgettype());

          ps.setString(5, dto.getSbudgetlevelcode());

          ps.setString(6, dto.getSbudgetsubcode());

          ps.setString(7, dto.getSbudgetsubname());

          ps.setBigDecimal(8, dto.getNmoneyday());

          ps.setBigDecimal(9, dto.getNmoneytenday());

          ps.setBigDecimal(10, dto.getNmoneymonth());

          ps.setBigDecimal(11, dto.getNmoneyquarter());

          ps.setBigDecimal(12, dto.getNmoneyyear());

          ps.setString(13, dto.getSbelongflag());

          ps.setString(14, dto.getStrimflag());

          ps.setString(15, dto.getSdividegroup());

          ps.setString(16, dto.getSbillkind());

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
       TrTaxorgIncomedayrptDto dto = (TrTaxorgIncomedayrptDto)_dto ;
       String msgValid = dto.checkValid() ;
       if (msgValid != null)
           throw new SQLException("�������"+msgValid) ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try
       {
           ps = conn.prepareStatement(SQL_INSERT_WITH_RESULT);
            ps.setString(1, dto.getStaxorgcode());
            ps.setString(2, dto.getStrecode());
            ps.setString(3, dto.getSrptdate());
            ps.setString(4, dto.getSbudgettype());
            ps.setString(5, dto.getSbudgetlevelcode());
            ps.setString(6, dto.getSbudgetsubcode());
            ps.setString(7, dto.getSbudgetsubname());
            ps.setBigDecimal(8, dto.getNmoneyday());
            ps.setBigDecimal(9, dto.getNmoneytenday());
            ps.setBigDecimal(10, dto.getNmoneymonth());
            ps.setBigDecimal(11, dto.getNmoneyquarter());
            ps.setBigDecimal(12, dto.getNmoneyyear());
            ps.setString(13, dto.getSbelongflag());
            ps.setString(14, dto.getStrimflag());
            ps.setString(15, dto.getSdividegroup());
            ps.setString(16, dto.getSbillkind());
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
        TrTaxorgIncomedayrptDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (TrTaxorgIncomedayrptDto)_dtos[i] ; 
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
 	    	    dto  = (TrTaxorgIncomedayrptDto)_dtos[i] ; 
  
               ps.setString(1, dto.getStaxorgcode());
  
               ps.setString(2, dto.getStrecode());
  
               ps.setString(3, dto.getSrptdate());
  
               ps.setString(4, dto.getSbudgettype());
  
               ps.setString(5, dto.getSbudgetlevelcode());
  
               ps.setString(6, dto.getSbudgetsubcode());
  
               ps.setString(7, dto.getSbudgetsubname());
  
               ps.setBigDecimal(8, dto.getNmoneyday());
  
               ps.setBigDecimal(9, dto.getNmoneytenday());
  
               ps.setBigDecimal(10, dto.getNmoneymonth());
  
               ps.setBigDecimal(11, dto.getNmoneyquarter());
  
               ps.setBigDecimal(12, dto.getNmoneyyear());
  
               ps.setString(13, dto.getSbelongflag());
  
               ps.setString(14, dto.getStrimflag());
  
               ps.setString(15, dto.getSdividegroup());
  
               ps.setString(16, dto.getSbillkind());
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
        	
       TrTaxorgIncomedayrptPK pk = (TrTaxorgIncomedayrptPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT);
           ps.setString(1, pk.getStaxorgcode());

           ps.setString(2, pk.getStrecode());

           ps.setString(3, pk.getSrptdate());

           ps.setString(4, pk.getSbudgettype());

           ps.setString(5, pk.getSbudgetlevelcode());

           ps.setString(6, pk.getSbudgetsubcode());

           ps.setString(7, pk.getSdividegroup());

           ps.setString(8, pk.getSbillkind());

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
        	
       TrTaxorgIncomedayrptPK pk = (TrTaxorgIncomedayrptPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT_FOR_UPDATE);
           ps.setString(1, pk.getStaxorgcode());

           ps.setString(2, pk.getStrecode());

           ps.setString(3, pk.getSrptdate());

           ps.setString(4, pk.getSbudgettype());

           ps.setString(5, pk.getSbudgetlevelcode());

           ps.setString(6, pk.getSbudgetsubcode());

           ps.setString(7, pk.getSdividegroup());

           ps.setString(8, pk.getSbillkind());

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
        TrTaxorgIncomedayrptPK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TrTaxorgIncomedayrptPK)_pks[i] ; 
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
                    pk  = (TrTaxorgIncomedayrptPK)(pks.get(i)) ; 
                   ps.setString((i-iBegin)*8+1, pk.getStaxorgcode());

                   ps.setString((i-iBegin)*8+2, pk.getStrecode());

                   ps.setString((i-iBegin)*8+3, pk.getSrptdate());

                   ps.setString((i-iBegin)*8+4, pk.getSbudgettype());

                   ps.setString((i-iBegin)*8+5, pk.getSbudgetlevelcode());

                   ps.setString((i-iBegin)*8+6, pk.getSbudgetsubcode());

                   ps.setString((i-iBegin)*8+7, pk.getSdividegroup());

                   ps.setString((i-iBegin)*8+8, pk.getSbillkind());

			
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
            TrTaxorgIncomedayrptDto[] dtos = new TrTaxorgIncomedayrptDto[0];
		    dtos = (TrTaxorgIncomedayrptDto[]) results.toArray(dtos) ;
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
             TrTaxorgIncomedayrptDto  dto = new  TrTaxorgIncomedayrptDto ();
             //S_TAXORGCODE
             str = rs.getString("S_TAXORGCODE");
             if (str == null)
                dto.setStaxorgcode(null);
             else
                dto.setStaxorgcode(str.trim());

             //S_TRECODE
             str = rs.getString("S_TRECODE");
             if (str == null)
                dto.setStrecode(null);
             else
                dto.setStrecode(str.trim());

             //S_RPTDATE
             str = rs.getString("S_RPTDATE");
             if (str == null)
                dto.setSrptdate(null);
             else
                dto.setSrptdate(str.trim());

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

             //S_BUDGETSUBCODE
             str = rs.getString("S_BUDGETSUBCODE");
             if (str == null)
                dto.setSbudgetsubcode(null);
             else
                dto.setSbudgetsubcode(str.trim());

             //S_BUDGETSUBNAME
             str = rs.getString("S_BUDGETSUBNAME");
             if (str == null)
                dto.setSbudgetsubname(null);
             else
                dto.setSbudgetsubname(str.trim());

             //N_MONEYDAY
           dto.setNmoneyday(rs.getBigDecimal("N_MONEYDAY"));

             //N_MONEYTENDAY
           dto.setNmoneytenday(rs.getBigDecimal("N_MONEYTENDAY"));

             //N_MONEYMONTH
           dto.setNmoneymonth(rs.getBigDecimal("N_MONEYMONTH"));

             //N_MONEYQUARTER
           dto.setNmoneyquarter(rs.getBigDecimal("N_MONEYQUARTER"));

             //N_MONEYYEAR
           dto.setNmoneyyear(rs.getBigDecimal("N_MONEYYEAR"));

             //S_BELONGFLAG
             str = rs.getString("S_BELONGFLAG");
             if (str == null)
                dto.setSbelongflag(null);
             else
                dto.setSbelongflag(str.trim());

             //S_TRIMFLAG
             str = rs.getString("S_TRIMFLAG");
             if (str == null)
                dto.setStrimflag(null);
             else
                dto.setStrimflag(str.trim());

             //S_DIVIDEGROUP
             str = rs.getString("S_DIVIDEGROUP");
             if (str == null)
                dto.setSdividegroup(null);
             else
                dto.setSdividegroup(str.trim());

             //S_BILLKIND
             str = rs.getString("S_BILLKIND");
             if (str == null)
                dto.setSbillkind(null);
             else
                dto.setSbillkind(str.trim());



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
        TrTaxorgIncomedayrptDto dto = (TrTaxorgIncomedayrptDto)_dto ;
        PreparedStatement ps = null;
        try {
            if(isLobSupport){
                ps = conn.prepareStatement(SQL_UPDATE_LOB);
            }
            else{
                ps = conn.prepareStatement(SQL_UPDATE);
            }
            int pos = 1;
            //S_BUDGETSUBNAME
            ps.setString(pos, dto.getSbudgetsubname());
            pos++;

            //N_MONEYDAY
            ps.setBigDecimal(pos, dto.getNmoneyday());
            pos++;

            //N_MONEYTENDAY
            ps.setBigDecimal(pos, dto.getNmoneytenday());
            pos++;

            //N_MONEYMONTH
            ps.setBigDecimal(pos, dto.getNmoneymonth());
            pos++;

            //N_MONEYQUARTER
            ps.setBigDecimal(pos, dto.getNmoneyquarter());
            pos++;

            //N_MONEYYEAR
            ps.setBigDecimal(pos, dto.getNmoneyyear());
            pos++;

            //S_BELONGFLAG
            ps.setString(pos, dto.getSbelongflag());
            pos++;

            //S_TRIMFLAG
            ps.setString(pos, dto.getStrimflag());
            pos++;


           //S_TAXORGCODE
           ps.setString(pos, dto.getStaxorgcode());
           pos++;
           //S_TRECODE
           ps.setString(pos, dto.getStrecode());
           pos++;
           //S_RPTDATE
           ps.setString(pos, dto.getSrptdate());
           pos++;
           //S_BUDGETTYPE
           ps.setString(pos, dto.getSbudgettype());
           pos++;
           //S_BUDGETLEVELCODE
           ps.setString(pos, dto.getSbudgetlevelcode());
           pos++;
           //S_BUDGETSUBCODE
           ps.setString(pos, dto.getSbudgetsubcode());
           pos++;
           //S_DIVIDEGROUP
           ps.setString(pos, dto.getSdividegroup());
           pos++;
           //S_BILLKIND
           ps.setString(pos, dto.getSbillkind());
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
     
     	 TrTaxorgIncomedayrptDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (TrTaxorgIncomedayrptDto)_dtos[i] ; 
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
                dto  = (TrTaxorgIncomedayrptDto)_dtos[i] ; 
                int pos = 1;
                //S_BUDGETSUBNAME
                 ps.setString(pos, dto.getSbudgetsubname());
                pos++;

                //N_MONEYDAY
                 ps.setBigDecimal(pos, dto.getNmoneyday());
                pos++;

                //N_MONEYTENDAY
                 ps.setBigDecimal(pos, dto.getNmoneytenday());
                pos++;

                //N_MONEYMONTH
                 ps.setBigDecimal(pos, dto.getNmoneymonth());
                pos++;

                //N_MONEYQUARTER
                 ps.setBigDecimal(pos, dto.getNmoneyquarter());
                pos++;

                //N_MONEYYEAR
                 ps.setBigDecimal(pos, dto.getNmoneyyear());
                pos++;

                //S_BELONGFLAG
                 ps.setString(pos, dto.getSbelongflag());
                pos++;

                //S_TRIMFLAG
                 ps.setString(pos, dto.getStrimflag());
                pos++;


               //S_TAXORGCODE
               ps.setString(pos, dto.getStaxorgcode());
               pos++;
               //S_TRECODE
               ps.setString(pos, dto.getStrecode());
               pos++;
               //S_RPTDATE
               ps.setString(pos, dto.getSrptdate());
               pos++;
               //S_BUDGETTYPE
               ps.setString(pos, dto.getSbudgettype());
               pos++;
               //S_BUDGETLEVELCODE
               ps.setString(pos, dto.getSbudgetlevelcode());
               pos++;
               //S_BUDGETSUBCODE
               ps.setString(pos, dto.getSbudgetsubcode());
               pos++;
               //S_DIVIDEGROUP
               ps.setString(pos, dto.getSdividegroup());
               pos++;
               //S_BILLKIND
               ps.setString(pos, dto.getSbillkind());
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
       TrTaxorgIncomedayrptPK pk = (TrTaxorgIncomedayrptPK)_pk ;


       PreparedStatement ps = null;
       try {
           ps = conn.prepareStatement(SQL_DELETE);
           ps.setString(1, pk.getStaxorgcode());
           ps.setString(2, pk.getStrecode());
           ps.setString(3, pk.getSrptdate());
           ps.setString(4, pk.getSbudgettype());
           ps.setString(5, pk.getSbudgetlevelcode());
           ps.setString(6, pk.getSbudgetsubcode());
           ps.setString(7, pk.getSdividegroup());
           ps.setString(8, pk.getSbillkind());
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
        TrTaxorgIncomedayrptPK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TrTaxorgIncomedayrptPK)_pks[i] ; 
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
       			pk  = (TrTaxorgIncomedayrptPK)(pks.get(i)) ; 
                ps.setString(1, pk.getStaxorgcode());
                ps.setString(2, pk.getStrecode());
                ps.setString(3, pk.getSrptdate());
                ps.setString(4, pk.getSbudgettype());
                ps.setString(5, pk.getSbudgetlevelcode());
                ps.setString(6, pk.getSbudgetsubcode());
                ps.setString(7, pk.getSdividegroup());
                ps.setString(8, pk.getSbillkind());
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
                        		throw new SQLException("���ݿ����TR_TAXORG_INCOMEDAYRPTû�м���޸ĵ��ֶ�");
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
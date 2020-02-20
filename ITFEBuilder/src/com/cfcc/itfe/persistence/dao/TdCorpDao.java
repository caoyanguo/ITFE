    



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

import com.cfcc.itfe.persistence.dto.TdCorpDto ;
import com.cfcc.itfe.persistence.pk.TdCorpPK ;


/**
 * <p>Title: DAO of table: TD_CORP</p>
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

public class TdCorpDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO TD_CORP ("
          + "S_BOOKORGCODE,S_TRECODE,S_CORPCODE,C_TRIMFLAG,S_CORPNAME"
          + ",S_CORPSHT,C_MAYAPRTFUND,C_PAYOUTPROP,C_TAXPAYERPROP,C_TRADEPROP"
          + ",I_ACCTNUM,TS_SYSUPDATE"
        + ") VALUES ("
        + "?,?,?,?,?,?,?,?,?,?,?,CURRENT TIMESTAMP )";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "TD_CORP.S_BOOKORGCODE, TD_CORP.S_TRECODE, TD_CORP.S_CORPCODE, TD_CORP.C_TRIMFLAG, TD_CORP.S_CORPNAME, "
        + "TD_CORP.S_CORPSHT, TD_CORP.C_MAYAPRTFUND, TD_CORP.C_PAYOUTPROP, TD_CORP.C_TAXPAYERPROP, TD_CORP.C_TRADEPROP, "
        + "TD_CORP.I_ACCTNUM, TD_CORP.TS_SYSUPDATE "
        + "FROM TD_CORP "
        +" WHERE " 
        + "S_BOOKORGCODE = ? AND S_TRECODE = ? AND S_CORPCODE = ? AND C_TRIMFLAG = ?"
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "TD_CORP.S_BOOKORGCODE, TD_CORP.S_TRECODE, TD_CORP.S_CORPCODE, TD_CORP.C_TRIMFLAG, TD_CORP.S_CORPNAME, "
        + "TD_CORP.S_CORPSHT, TD_CORP.C_MAYAPRTFUND, TD_CORP.C_PAYOUTPROP, TD_CORP.C_TAXPAYERPROP, TD_CORP.C_TRADEPROP, "
        + "TD_CORP.I_ACCTNUM, TD_CORP.TS_SYSUPDATE "
        + "FROM TD_CORP "
        +" WHERE " 
        + "S_BOOKORGCODE = ? AND S_TRECODE = ? AND S_CORPCODE = ? AND C_TRIMFLAG = ? FOR UPDATE"
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  TD_CORP.S_BOOKORGCODE  , TD_CORP.S_TRECODE  , TD_CORP.S_CORPCODE  , TD_CORP.C_TRIMFLAG  , TD_CORP.S_CORPNAME "
        + " , TD_CORP.S_CORPSHT  , TD_CORP.C_MAYAPRTFUND  , TD_CORP.C_PAYOUTPROP  , TD_CORP.C_TAXPAYERPROP  , TD_CORP.C_TRADEPROP "
        + " , TD_CORP.I_ACCTNUM  , TD_CORP.TS_SYSUPDATE "
        + "FROM TD_CORP ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "TD_CORP.S_BOOKORGCODE, TD_CORP.S_TRECODE, TD_CORP.S_CORPCODE, TD_CORP.C_TRIMFLAG, TD_CORP.S_CORPNAME, "
        + "TD_CORP.S_CORPSHT, TD_CORP.C_MAYAPRTFUND, TD_CORP.C_PAYOUTPROP, TD_CORP.C_TAXPAYERPROP, TD_CORP.C_TRADEPROP, "
        + "TD_CORP.I_ACCTNUM, TD_CORP.TS_SYSUPDATE "
        + "FROM TD_CORP " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        + "S_BOOKORGCODE = ? AND S_TRECODE = ? AND S_CORPCODE = ? AND C_TRIMFLAG = ?)"
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE TD_CORP SET "
        + "S_CORPNAME =?,S_CORPSHT =?,C_MAYAPRTFUND =?,C_PAYOUTPROP =?,C_TAXPAYERPROP =?, "
        + "C_TRADEPROP =?,I_ACCTNUM =?,TS_SYSUPDATE =CURRENT TIMESTAMP "
        + "WHERE "
        + "S_BOOKORGCODE = ? AND S_TRECODE = ? AND S_CORPCODE = ? AND C_TRIMFLAG = ?"
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE TD_CORP SET "
        + "S_CORPNAME =?, S_CORPSHT =?, C_MAYAPRTFUND =?, C_PAYOUTPROP =?, C_TAXPAYERPROP =?,  "
        + "C_TRADEPROP =?, I_ACCTNUM =?, TS_SYSUPDATE =CURRENT TIMESTAMP "
        + "WHERE "
        + "S_BOOKORGCODE = ? AND S_TRECODE = ? AND S_CORPCODE = ? AND C_TRIMFLAG = ?"
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM TD_CORP " 
        + " WHERE "
        + "S_BOOKORGCODE = ? AND S_TRECODE = ? AND S_CORPCODE = ? AND C_TRIMFLAG = ?"
        ;


	/**
	*  批量查询 一次最多查询的参数数量
	*/
	
	public static final int FIND_BATCH_SIZE = 150  / 4;
	



   /**
   * Create a new record in Database.
   */
  public void create(IDto _dto,  Connection conn) throws SQLException
  {
     TdCorpDto dto = (TdCorpDto)_dto ;
     String msgValid = dto.checkValid() ;
     if (msgValid != null)
     	throw new SQLException("插入错误，"+msgValid) ;

     PreparedStatement ps = null;
     try
     {
         ps = conn.prepareStatement(SQL_INSERT);
          ps.setString(1, dto.getSbookorgcode());

          ps.setString(2, dto.getStrecode());

          ps.setString(3, dto.getScorpcode());

          ps.setString(4, dto.getCtrimflag());

          ps.setString(5, dto.getScorpname());

          ps.setString(6, dto.getScorpsht());

          ps.setString(7, dto.getCmayaprtfund());

          ps.setString(8, dto.getCpayoutprop());

          ps.setString(9, dto.getCtaxpayerprop());

          ps.setString(10, dto.getCtradeprop());

          if (dto.getIacctnum()==null)
            ps.setNull(11, java.sql.Types.INTEGER);
         else
            ps.setInt(11, dto.getIacctnum().intValue());
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
       TdCorpDto dto = (TdCorpDto)_dto ;
       String msgValid = dto.checkValid() ;
       if (msgValid != null)
           throw new SQLException("插入错误，"+msgValid) ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try
       {
           ps = conn.prepareStatement(SQL_INSERT_WITH_RESULT);
            ps.setString(1, dto.getSbookorgcode());
            ps.setString(2, dto.getStrecode());
            ps.setString(3, dto.getScorpcode());
            ps.setString(4, dto.getCtrimflag());
            ps.setString(5, dto.getScorpname());
            ps.setString(6, dto.getScorpsht());
            ps.setString(7, dto.getCmayaprtfund());
            ps.setString(8, dto.getCpayoutprop());
            ps.setString(9, dto.getCtaxpayerprop());
            ps.setString(10, dto.getCtradeprop());
            if (dto.getIacctnum()==null)
              ps.setNull(11, java.sql.Types.INTEGER);
           else
              ps.setInt(11, dto.getIacctnum().intValue());
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
        TdCorpDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (TdCorpDto)_dtos[i] ; 
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
 	    	    dto  = (TdCorpDto)_dtos[i] ; 
  
               ps.setString(1, dto.getSbookorgcode());
  
               ps.setString(2, dto.getStrecode());
  
               ps.setString(3, dto.getScorpcode());
  
               ps.setString(4, dto.getCtrimflag());
  
               ps.setString(5, dto.getScorpname());
  
               ps.setString(6, dto.getScorpsht());
  
               ps.setString(7, dto.getCmayaprtfund());
  
               ps.setString(8, dto.getCpayoutprop());
  
               ps.setString(9, dto.getCtaxpayerprop());
  
               ps.setString(10, dto.getCtradeprop());
  
               if (dto.getIacctnum()==null)
                  ps.setNull(11, java.sql.Types.INTEGER);
               else
                  ps.setInt(11, dto.getIacctnum().intValue());
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
        	
       TdCorpPK pk = (TdCorpPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT);
           ps.setString(1, pk.getSbookorgcode());

           ps.setString(2, pk.getStrecode());

           ps.setString(3, pk.getScorpcode());

           ps.setString(4, pk.getCtrimflag());

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
        	
       TdCorpPK pk = (TdCorpPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT_FOR_UPDATE);
           ps.setString(1, pk.getSbookorgcode());

           ps.setString(2, pk.getStrecode());

           ps.setString(3, pk.getScorpcode());

           ps.setString(4, pk.getCtrimflag());

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
        TdCorpPK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TdCorpPK)_pks[i] ; 
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
                    pk  = (TdCorpPK)(pks.get(i)) ; 
                   ps.setString((i-iBegin)*4+1, pk.getSbookorgcode());

                   ps.setString((i-iBegin)*4+2, pk.getStrecode());

                   ps.setString((i-iBegin)*4+3, pk.getScorpcode());

                   ps.setString((i-iBegin)*4+4, pk.getCtrimflag());

			
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
            TdCorpDto[] dtos = new TdCorpDto[0];
		    dtos = (TdCorpDto[]) results.toArray(dtos) ;
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
             TdCorpDto  dto = new  TdCorpDto ();
             //S_BOOKORGCODE
             str = rs.getString("S_BOOKORGCODE");
             if (str == null)
                dto.setSbookorgcode(null);
             else
                dto.setSbookorgcode(str.trim());

             //S_TRECODE
             str = rs.getString("S_TRECODE");
             if (str == null)
                dto.setStrecode(null);
             else
                dto.setStrecode(str.trim());

             //S_CORPCODE
             str = rs.getString("S_CORPCODE");
             if (str == null)
                dto.setScorpcode(null);
             else
                dto.setScorpcode(str.trim());

             //C_TRIMFLAG
             str = rs.getString("C_TRIMFLAG");
             if (str == null)
                dto.setCtrimflag(null);
             else
                dto.setCtrimflag(str.trim());

             //S_CORPNAME
             str = rs.getString("S_CORPNAME");
             if (str == null)
                dto.setScorpname(null);
             else
                dto.setScorpname(str.trim());

             //S_CORPSHT
             str = rs.getString("S_CORPSHT");
             if (str == null)
                dto.setScorpsht(null);
             else
                dto.setScorpsht(str.trim());

             //C_MAYAPRTFUND
             str = rs.getString("C_MAYAPRTFUND");
             if (str == null)
                dto.setCmayaprtfund(null);
             else
                dto.setCmayaprtfund(str.trim());

             //C_PAYOUTPROP
             str = rs.getString("C_PAYOUTPROP");
             if (str == null)
                dto.setCpayoutprop(null);
             else
                dto.setCpayoutprop(str.trim());

             //C_TAXPAYERPROP
             str = rs.getString("C_TAXPAYERPROP");
             if (str == null)
                dto.setCtaxpayerprop(null);
             else
                dto.setCtaxpayerprop(str.trim());

             //C_TRADEPROP
             str = rs.getString("C_TRADEPROP");
             if (str == null)
                dto.setCtradeprop(null);
             else
                dto.setCtradeprop(str.trim());

             //I_ACCTNUM
             str = rs.getString("I_ACCTNUM");
             if(str!=null){
                dto.setIacctnum(new Integer(str));
             }

             //TS_SYSUPDATE
           dto.setTssysupdate(rs.getTimestamp("TS_SYSUPDATE"));



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
        TdCorpDto dto = (TdCorpDto)_dto ;
        PreparedStatement ps = null;
        try {
            if(isLobSupport){
                ps = conn.prepareStatement(SQL_UPDATE_LOB);
            }
            else{
                ps = conn.prepareStatement(SQL_UPDATE);
            }
            int pos = 1;
            //S_CORPNAME
            ps.setString(pos, dto.getScorpname());
            pos++;

            //S_CORPSHT
            ps.setString(pos, dto.getScorpsht());
            pos++;

            //C_MAYAPRTFUND
            ps.setString(pos, dto.getCmayaprtfund());
            pos++;

            //C_PAYOUTPROP
            ps.setString(pos, dto.getCpayoutprop());
            pos++;

            //C_TAXPAYERPROP
            ps.setString(pos, dto.getCtaxpayerprop());
            pos++;

            //C_TRADEPROP
            ps.setString(pos, dto.getCtradeprop());
            pos++;

            //I_ACCTNUM
            if (dto.getIacctnum()==null)
                ps.setNull(pos, java.sql.Types.INTEGER);
            else
                ps.setInt(pos, dto.getIacctnum().intValue());
            pos++;

            //TS_SYSUPDATE

           //S_BOOKORGCODE
           ps.setString(pos, dto.getSbookorgcode());
           pos++;
           //S_TRECODE
           ps.setString(pos, dto.getStrecode());
           pos++;
           //S_CORPCODE
           ps.setString(pos, dto.getScorpcode());
           pos++;
           //C_TRIMFLAG
           ps.setString(pos, dto.getCtrimflag());
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
     
     	 TdCorpDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (TdCorpDto)_dtos[i] ; 
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
                dto  = (TdCorpDto)_dtos[i] ; 
                int pos = 1;
                //S_CORPNAME
                 ps.setString(pos, dto.getScorpname());
                pos++;

                //S_CORPSHT
                 ps.setString(pos, dto.getScorpsht());
                pos++;

                //C_MAYAPRTFUND
                 ps.setString(pos, dto.getCmayaprtfund());
                pos++;

                //C_PAYOUTPROP
                 ps.setString(pos, dto.getCpayoutprop());
                pos++;

                //C_TAXPAYERPROP
                 ps.setString(pos, dto.getCtaxpayerprop());
                pos++;

                //C_TRADEPROP
                 ps.setString(pos, dto.getCtradeprop());
                pos++;

                //I_ACCTNUM
                 if (dto.getIacctnum()==null)
                   ps.setNull(pos, java.sql.Types.INTEGER);
                else
                   ps.setInt(pos, dto.getIacctnum().intValue());
                pos++ ;

                //TS_SYSUPDATE
 
               //S_BOOKORGCODE
               ps.setString(pos, dto.getSbookorgcode());
               pos++;
               //S_TRECODE
               ps.setString(pos, dto.getStrecode());
               pos++;
               //S_CORPCODE
               ps.setString(pos, dto.getScorpcode());
               pos++;
               //C_TRIMFLAG
               ps.setString(pos, dto.getCtrimflag());
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
       TdCorpPK pk = (TdCorpPK)_pk ;


       PreparedStatement ps = null;
       try {
           ps = conn.prepareStatement(SQL_DELETE);
           ps.setString(1, pk.getSbookorgcode());
           ps.setString(2, pk.getStrecode());
           ps.setString(3, pk.getScorpcode());
           ps.setString(4, pk.getCtrimflag());
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
        TdCorpPK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TdCorpPK)_pks[i] ; 
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
       			pk  = (TdCorpPK)(pks.get(i)) ; 
                ps.setString(1, pk.getSbookorgcode());
                ps.setString(2, pk.getStrecode());
                ps.setString(3, pk.getScorpcode());
                ps.setString(4, pk.getCtrimflag());
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
                        		throw new SQLException("数据库表：TD_CORP没有检查修改的字段");
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

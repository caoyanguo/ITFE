    



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

import com.cfcc.itfe.persistence.dto.TsGenbankandreckbankDto ;
import com.cfcc.itfe.persistence.pk.TsGenbankandreckbankPK ;


/**
 * <p>Title: DAO of table: TS_GENBANKANDRECKBANK</p>
 * <p>Description: Data Access Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:00 </p>
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

public class TsGenbankandreckbankDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO TS_GENBANKANDRECKBANK ("
          + "I_SEQNO,S_BOOKORGCODE,S_GENBANKCODE,S_RECKBANKCODE,S_GENBANKNAME"
          + ",S_RECKBANKNAME,TS_SYSUPDATE,S_TRECODE"
        + ") VALUES ("
        + "DEFAULT ,?,?,?,?,?,CURRENT TIMESTAMP ,?)";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "TS_GENBANKANDRECKBANK.I_SEQNO, TS_GENBANKANDRECKBANK.S_BOOKORGCODE, TS_GENBANKANDRECKBANK.S_GENBANKCODE, TS_GENBANKANDRECKBANK.S_RECKBANKCODE, TS_GENBANKANDRECKBANK.S_GENBANKNAME, "
        + "TS_GENBANKANDRECKBANK.S_RECKBANKNAME, TS_GENBANKANDRECKBANK.TS_SYSUPDATE, TS_GENBANKANDRECKBANK.S_TRECODE "
        + "FROM TS_GENBANKANDRECKBANK "
        +" WHERE " 
        + "I_SEQNO = ?"
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "TS_GENBANKANDRECKBANK.I_SEQNO, TS_GENBANKANDRECKBANK.S_BOOKORGCODE, TS_GENBANKANDRECKBANK.S_GENBANKCODE, TS_GENBANKANDRECKBANK.S_RECKBANKCODE, TS_GENBANKANDRECKBANK.S_GENBANKNAME, "
        + "TS_GENBANKANDRECKBANK.S_RECKBANKNAME, TS_GENBANKANDRECKBANK.TS_SYSUPDATE, TS_GENBANKANDRECKBANK.S_TRECODE "
        + "FROM TS_GENBANKANDRECKBANK "
        +" WHERE " 
        + "I_SEQNO = ? FOR UPDATE"
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  TS_GENBANKANDRECKBANK.I_SEQNO  , TS_GENBANKANDRECKBANK.S_BOOKORGCODE  , TS_GENBANKANDRECKBANK.S_GENBANKCODE  , TS_GENBANKANDRECKBANK.S_RECKBANKCODE  , TS_GENBANKANDRECKBANK.S_GENBANKNAME "
        + " , TS_GENBANKANDRECKBANK.S_RECKBANKNAME  , TS_GENBANKANDRECKBANK.TS_SYSUPDATE  , TS_GENBANKANDRECKBANK.S_TRECODE "
        + "FROM TS_GENBANKANDRECKBANK ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "TS_GENBANKANDRECKBANK.I_SEQNO, TS_GENBANKANDRECKBANK.S_BOOKORGCODE, TS_GENBANKANDRECKBANK.S_GENBANKCODE, TS_GENBANKANDRECKBANK.S_RECKBANKCODE, TS_GENBANKANDRECKBANK.S_GENBANKNAME, "
        + "TS_GENBANKANDRECKBANK.S_RECKBANKNAME, TS_GENBANKANDRECKBANK.TS_SYSUPDATE, TS_GENBANKANDRECKBANK.S_TRECODE "
        + "FROM TS_GENBANKANDRECKBANK " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        + "I_SEQNO = ?)"
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE TS_GENBANKANDRECKBANK SET "
        + "S_BOOKORGCODE =?,S_GENBANKCODE =?,S_RECKBANKCODE =?,S_GENBANKNAME =?,S_RECKBANKNAME =?, "
        + "TS_SYSUPDATE =CURRENT TIMESTAMP,S_TRECODE =? "
        + "WHERE "
        + "I_SEQNO = ?"
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE TS_GENBANKANDRECKBANK SET "
        + "S_BOOKORGCODE =?, S_GENBANKCODE =?, S_RECKBANKCODE =?, S_GENBANKNAME =?, S_RECKBANKNAME =?,  "
        + "TS_SYSUPDATE =CURRENT TIMESTAMP, S_TRECODE =? "
        + "WHERE "
        + "I_SEQNO = ?"
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM TS_GENBANKANDRECKBANK " 
        + " WHERE "
        + "I_SEQNO = ?"
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
     TsGenbankandreckbankDto dto = (TsGenbankandreckbankDto)_dto ;
     String msgValid = dto.checkValid() ;
     if (msgValid != null)
     	throw new SQLException("插入错误，"+msgValid) ;

     PreparedStatement ps = null;
     try
     {
         ps = conn.prepareStatement(SQL_INSERT);
           ps.setString(1, dto.getSbookorgcode());

          ps.setString(2, dto.getSgenbankcode());

          ps.setString(3, dto.getSreckbankcode());

          ps.setString(4, dto.getSgenbankname());

          ps.setString(5, dto.getSreckbankname());

           ps.setString(6, dto.getStrecode());

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
       TsGenbankandreckbankDto dto = (TsGenbankandreckbankDto)_dto ;
       String msgValid = dto.checkValid() ;
       if (msgValid != null)
           throw new SQLException("插入错误，"+msgValid) ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try
       {
           ps = conn.prepareStatement(SQL_INSERT_WITH_RESULT);
             ps.setString(1, dto.getSbookorgcode());
            ps.setString(2, dto.getSgenbankcode());
            ps.setString(3, dto.getSreckbankcode());
            ps.setString(4, dto.getSgenbankname());
            ps.setString(5, dto.getSreckbankname());
             ps.setString(6, dto.getStrecode());
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
        TsGenbankandreckbankDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (TsGenbankandreckbankDto)_dtos[i] ; 
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
 	    	    dto  = (TsGenbankandreckbankDto)_dtos[i] ; 
   
               ps.setString(1, dto.getSbookorgcode());
  
               ps.setString(2, dto.getSgenbankcode());
  
               ps.setString(3, dto.getSreckbankcode());
  
               ps.setString(4, dto.getSgenbankname());
  
               ps.setString(5, dto.getSreckbankname());
   
               ps.setString(6, dto.getStrecode());
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
        	
       TsGenbankandreckbankPK pk = (TsGenbankandreckbankPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT);
           if (pk.getIseqno()==null)
               ps.setNull(1, java.sql.Types.BIGINT);
           else
               ps.setLong(1, pk.getIseqno().longValue());

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
        	
       TsGenbankandreckbankPK pk = (TsGenbankandreckbankPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT_FOR_UPDATE);
           if (pk.getIseqno()==null)
               ps.setNull(1, java.sql.Types.BIGINT);
           else
               ps.setLong(1, pk.getIseqno().longValue());

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
        TsGenbankandreckbankPK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TsGenbankandreckbankPK)_pks[i] ; 
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
                    pk  = (TsGenbankandreckbankPK)(pks.get(i)) ; 
                   if (pk.getIseqno()==null)
                      ps.setNull((i-iBegin)*1+1, java.sql.Types.BIGINT);
                   else
                      ps.setLong((i-iBegin)*1+1, pk.getIseqno().longValue());

			
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
            TsGenbankandreckbankDto[] dtos = new TsGenbankandreckbankDto[0];
		    dtos = (TsGenbankandreckbankDto[]) results.toArray(dtos) ;
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
             TsGenbankandreckbankDto  dto = new  TsGenbankandreckbankDto ();
             //I_SEQNO
             str = rs.getString("I_SEQNO");
             if(str!=null){
                dto.setIseqno(new Long(str));
             }

             //S_BOOKORGCODE
             str = rs.getString("S_BOOKORGCODE");
             if (str == null)
                dto.setSbookorgcode(null);
             else
                dto.setSbookorgcode(str.trim());

             //S_GENBANKCODE
             str = rs.getString("S_GENBANKCODE");
             if (str == null)
                dto.setSgenbankcode(null);
             else
                dto.setSgenbankcode(str.trim());

             //S_RECKBANKCODE
             str = rs.getString("S_RECKBANKCODE");
             if (str == null)
                dto.setSreckbankcode(null);
             else
                dto.setSreckbankcode(str.trim());

             //S_GENBANKNAME
             str = rs.getString("S_GENBANKNAME");
             if (str == null)
                dto.setSgenbankname(null);
             else
                dto.setSgenbankname(str.trim());

             //S_RECKBANKNAME
             str = rs.getString("S_RECKBANKNAME");
             if (str == null)
                dto.setSreckbankname(null);
             else
                dto.setSreckbankname(str.trim());

             //TS_SYSUPDATE
           dto.setTssysupdate(rs.getTimestamp("TS_SYSUPDATE"));

             //S_TRECODE
             str = rs.getString("S_TRECODE");
             if (str == null)
                dto.setStrecode(null);
             else
                dto.setStrecode(str.trim());



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
        TsGenbankandreckbankDto dto = (TsGenbankandreckbankDto)_dto ;
        PreparedStatement ps = null;
        try {
            if(isLobSupport){
                ps = conn.prepareStatement(SQL_UPDATE_LOB);
            }
            else{
                ps = conn.prepareStatement(SQL_UPDATE);
            }
            int pos = 1;
            //S_BOOKORGCODE
            ps.setString(pos, dto.getSbookorgcode());
            pos++;

            //S_GENBANKCODE
            ps.setString(pos, dto.getSgenbankcode());
            pos++;

            //S_RECKBANKCODE
            ps.setString(pos, dto.getSreckbankcode());
            pos++;

            //S_GENBANKNAME
            ps.setString(pos, dto.getSgenbankname());
            pos++;

            //S_RECKBANKNAME
            ps.setString(pos, dto.getSreckbankname());
            pos++;

            //TS_SYSUPDATE
            //S_TRECODE
            ps.setString(pos, dto.getStrecode());
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
     
     	 TsGenbankandreckbankDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (TsGenbankandreckbankDto)_dtos[i] ; 
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
                dto  = (TsGenbankandreckbankDto)_dtos[i] ; 
                int pos = 1;
                //S_BOOKORGCODE
                 ps.setString(pos, dto.getSbookorgcode());
                pos++;

                //S_GENBANKCODE
                 ps.setString(pos, dto.getSgenbankcode());
                pos++;

                //S_RECKBANKCODE
                 ps.setString(pos, dto.getSreckbankcode());
                pos++;

                //S_GENBANKNAME
                 ps.setString(pos, dto.getSgenbankname());
                pos++;

                //S_RECKBANKNAME
                 ps.setString(pos, dto.getSreckbankname());
                pos++;

                //TS_SYSUPDATE
                 //S_TRECODE
                 ps.setString(pos, dto.getStrecode());
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
        //	throw new SQLException("删除错误，"+msgValid) ;
       }
       TsGenbankandreckbankPK pk = (TsGenbankandreckbankPK)_pk ;


       PreparedStatement ps = null;
       try {
           ps = conn.prepareStatement(SQL_DELETE);
           ps.setLong(1, pk.getIseqno().longValue());
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
        TsGenbankandreckbankPK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TsGenbankandreckbankPK)_pks[i] ; 
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
       			pk  = (TsGenbankandreckbankPK)(pks.get(i)) ; 
                ps.setLong(1, pk.getIseqno().longValue());
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
                     		throw new SQLException("数据库表：TS_GENBANKANDRECKBANK没有检查修改的字段");
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

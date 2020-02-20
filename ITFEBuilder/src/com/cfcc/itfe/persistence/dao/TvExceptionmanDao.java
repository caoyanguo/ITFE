    



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

import com.cfcc.itfe.persistence.dto.TvExceptionmanDto ;
import com.cfcc.itfe.persistence.pk.TvExceptionmanPK ;


/**
 * <p>Title: DAO of table: TV_EXCEPTIONMAN</p>
 * <p>Description: Data Access Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:02 </p>
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

public class TvExceptionmanDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO TV_EXCEPTIONMAN ("
          + "I_EXCEPTIONNO,D_WORKDATE,C_SYSID,S_USERID,S_EXCEPTIONINFO"
          + ",S_OFBIZKIND,TS_UPDATE"
        + ") VALUES ("
        + "DEFAULT ,?,?,?,?,?,?)";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "TV_EXCEPTIONMAN.I_EXCEPTIONNO, TV_EXCEPTIONMAN.D_WORKDATE, TV_EXCEPTIONMAN.C_SYSID, TV_EXCEPTIONMAN.S_USERID, TV_EXCEPTIONMAN.S_EXCEPTIONINFO, "
        + "TV_EXCEPTIONMAN.S_OFBIZKIND, TV_EXCEPTIONMAN.TS_UPDATE "
        + "FROM TV_EXCEPTIONMAN "
        +" WHERE " 
        + "I_EXCEPTIONNO = ?"
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "TV_EXCEPTIONMAN.I_EXCEPTIONNO, TV_EXCEPTIONMAN.D_WORKDATE, TV_EXCEPTIONMAN.C_SYSID, TV_EXCEPTIONMAN.S_USERID, TV_EXCEPTIONMAN.S_EXCEPTIONINFO, "
        + "TV_EXCEPTIONMAN.S_OFBIZKIND, TV_EXCEPTIONMAN.TS_UPDATE "
        + "FROM TV_EXCEPTIONMAN "
        +" WHERE " 
        + "I_EXCEPTIONNO = ? FOR UPDATE"
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  TV_EXCEPTIONMAN.I_EXCEPTIONNO  , TV_EXCEPTIONMAN.D_WORKDATE  , TV_EXCEPTIONMAN.C_SYSID  , TV_EXCEPTIONMAN.S_USERID  , TV_EXCEPTIONMAN.S_EXCEPTIONINFO "
        + " , TV_EXCEPTIONMAN.S_OFBIZKIND  , TV_EXCEPTIONMAN.TS_UPDATE "
        + "FROM TV_EXCEPTIONMAN ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "TV_EXCEPTIONMAN.I_EXCEPTIONNO, TV_EXCEPTIONMAN.D_WORKDATE, TV_EXCEPTIONMAN.C_SYSID, TV_EXCEPTIONMAN.S_USERID, TV_EXCEPTIONMAN.S_EXCEPTIONINFO, "
        + "TV_EXCEPTIONMAN.S_OFBIZKIND, TV_EXCEPTIONMAN.TS_UPDATE "
        + "FROM TV_EXCEPTIONMAN " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        + "I_EXCEPTIONNO = ?)"
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE TV_EXCEPTIONMAN SET "
        + "D_WORKDATE =?,C_SYSID =?,S_USERID =?,S_EXCEPTIONINFO =?,S_OFBIZKIND =?, "
        + "TS_UPDATE =? "
        + "WHERE "
        + "I_EXCEPTIONNO = ?"
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE TV_EXCEPTIONMAN SET "
        + "D_WORKDATE =?, C_SYSID =?, S_USERID =?, S_EXCEPTIONINFO =?, S_OFBIZKIND =?,  "
        + "TS_UPDATE =? "
        + "WHERE "
        + "I_EXCEPTIONNO = ?"
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM TV_EXCEPTIONMAN " 
        + " WHERE "
        + "I_EXCEPTIONNO = ?"
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
     TvExceptionmanDto dto = (TvExceptionmanDto)_dto ;
     String msgValid = dto.checkValid() ;
     if (msgValid != null)
     	throw new SQLException("插入错误，"+msgValid) ;

     PreparedStatement ps = null;
     try
     {
         ps = conn.prepareStatement(SQL_INSERT);
           ps.setString(1, dto.getDworkdate());

          ps.setString(2, dto.getCsysid());

          ps.setString(3, dto.getSuserid());

          ps.setString(4, dto.getSexceptioninfo());

          ps.setString(5, dto.getSofbizkind());

          ps.setTimestamp(6, dto.getTsupdate());

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
       TvExceptionmanDto dto = (TvExceptionmanDto)_dto ;
       String msgValid = dto.checkValid() ;
       if (msgValid != null)
           throw new SQLException("插入错误，"+msgValid) ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try
       {
           ps = conn.prepareStatement(SQL_INSERT_WITH_RESULT);
             ps.setString(1, dto.getDworkdate());
            ps.setString(2, dto.getCsysid());
            ps.setString(3, dto.getSuserid());
            ps.setString(4, dto.getSexceptioninfo());
            ps.setString(5, dto.getSofbizkind());
            ps.setTimestamp(6, dto.getTsupdate());
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
        TvExceptionmanDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (TvExceptionmanDto)_dtos[i] ; 
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
 	    	    dto  = (TvExceptionmanDto)_dtos[i] ; 
   
               ps.setString(1, dto.getDworkdate());
  
               ps.setString(2, dto.getCsysid());
  
               ps.setString(3, dto.getSuserid());
  
               ps.setString(4, dto.getSexceptioninfo());
  
               ps.setString(5, dto.getSofbizkind());
  
               ps.setTimestamp(6, dto.getTsupdate());
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
        	
       TvExceptionmanPK pk = (TvExceptionmanPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT);
           if (pk.getIexceptionno()==null)
               ps.setNull(1, java.sql.Types.BIGINT);
           else
               ps.setLong(1, pk.getIexceptionno().longValue());

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
        	
       TvExceptionmanPK pk = (TvExceptionmanPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT_FOR_UPDATE);
           if (pk.getIexceptionno()==null)
               ps.setNull(1, java.sql.Types.BIGINT);
           else
               ps.setLong(1, pk.getIexceptionno().longValue());

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
        TvExceptionmanPK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TvExceptionmanPK)_pks[i] ; 
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
                    pk  = (TvExceptionmanPK)(pks.get(i)) ; 
                   if (pk.getIexceptionno()==null)
                      ps.setNull((i-iBegin)*1+1, java.sql.Types.BIGINT);
                   else
                      ps.setLong((i-iBegin)*1+1, pk.getIexceptionno().longValue());

			
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
            TvExceptionmanDto[] dtos = new TvExceptionmanDto[0];
		    dtos = (TvExceptionmanDto[]) results.toArray(dtos) ;
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
             TvExceptionmanDto  dto = new  TvExceptionmanDto ();
             //I_EXCEPTIONNO
             str = rs.getString("I_EXCEPTIONNO");
             if(str!=null){
                dto.setIexceptionno(new Long(str));
             }

             //D_WORKDATE
             str = rs.getString("D_WORKDATE");
             if (str == null)
                dto.setDworkdate(null);
             else
                dto.setDworkdate(str.trim());

             //C_SYSID
             str = rs.getString("C_SYSID");
             if (str == null)
                dto.setCsysid(null);
             else
                dto.setCsysid(str.trim());

             //S_USERID
             str = rs.getString("S_USERID");
             if (str == null)
                dto.setSuserid(null);
             else
                dto.setSuserid(str.trim());

             //S_EXCEPTIONINFO
             str = rs.getString("S_EXCEPTIONINFO");
             if (str == null)
                dto.setSexceptioninfo(null);
             else
                dto.setSexceptioninfo(str.trim());

             //S_OFBIZKIND
             str = rs.getString("S_OFBIZKIND");
             if (str == null)
                dto.setSofbizkind(null);
             else
                dto.setSofbizkind(str.trim());

             //TS_UPDATE
           dto.setTsupdate(rs.getTimestamp("TS_UPDATE"));



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
        TvExceptionmanDto dto = (TvExceptionmanDto)_dto ;
        PreparedStatement ps = null;
        try {
            if(isLobSupport){
                ps = conn.prepareStatement(SQL_UPDATE_LOB);
            }
            else{
                ps = conn.prepareStatement(SQL_UPDATE);
            }
            int pos = 1;
            //D_WORKDATE
            ps.setString(pos, dto.getDworkdate());
            pos++;

            //C_SYSID
            ps.setString(pos, dto.getCsysid());
            pos++;

            //S_USERID
            ps.setString(pos, dto.getSuserid());
            pos++;

            //S_EXCEPTIONINFO
            ps.setString(pos, dto.getSexceptioninfo());
            pos++;

            //S_OFBIZKIND
            ps.setString(pos, dto.getSofbizkind());
            pos++;

            //TS_UPDATE
            ps.setTimestamp(pos, dto.getTsupdate());
            pos++;


           //I_EXCEPTIONNO
           ps.setLong(pos, dto.getIexceptionno().longValue());
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
     
     	 TvExceptionmanDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (TvExceptionmanDto)_dtos[i] ; 
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
                dto  = (TvExceptionmanDto)_dtos[i] ; 
                int pos = 1;
                //D_WORKDATE
                 ps.setString(pos, dto.getDworkdate());
                pos++;

                //C_SYSID
                 ps.setString(pos, dto.getCsysid());
                pos++;

                //S_USERID
                 ps.setString(pos, dto.getSuserid());
                pos++;

                //S_EXCEPTIONINFO
                 ps.setString(pos, dto.getSexceptioninfo());
                pos++;

                //S_OFBIZKIND
                 ps.setString(pos, dto.getSofbizkind());
                pos++;

                //TS_UPDATE
                 ps.setTimestamp(pos, dto.getTsupdate());
                pos++;


               //I_EXCEPTIONNO
               ps.setLong(pos, dto.getIexceptionno().longValue());
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
       TvExceptionmanPK pk = (TvExceptionmanPK)_pk ;


       PreparedStatement ps = null;
       try {
           ps = conn.prepareStatement(SQL_DELETE);
           ps.setLong(1, pk.getIexceptionno().longValue());
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
        TvExceptionmanPK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TvExceptionmanPK)_pks[i] ; 
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
       			pk  = (TvExceptionmanPK)(pks.get(i)) ; 
                ps.setLong(1, pk.getIexceptionno().longValue());
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
                  		throw new SQLException("数据库表：TV_EXCEPTIONMAN没有检查修改的字段");
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

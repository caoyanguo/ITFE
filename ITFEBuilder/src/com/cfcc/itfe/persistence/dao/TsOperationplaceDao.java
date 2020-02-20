    



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

import com.cfcc.itfe.persistence.dto.TsOperationplaceDto ;
import com.cfcc.itfe.persistence.pk.TsOperationplacePK ;


/**
 * <p>Title: DAO of table: TS_OPERATIONPLACE</p>
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

public class TsOperationplaceDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO TS_OPERATIONPLACE ("
          + "S_MODELID,S_FORMID,S_PLACEID,S_PLACETYPE,S_STAMPTYPECODE"
          + ",S_PLACEDESC,S_ISUSE,S_BEFORESTATUS,S_AFTERSTATUS,I_MODICOUNT"
        + ") VALUES ("
        + "?,?,?,?,?,?,?,?,?,?)";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "TS_OPERATIONPLACE.S_MODELID, TS_OPERATIONPLACE.S_FORMID, TS_OPERATIONPLACE.S_PLACEID, TS_OPERATIONPLACE.S_PLACETYPE, TS_OPERATIONPLACE.S_STAMPTYPECODE, "
        + "TS_OPERATIONPLACE.S_PLACEDESC, TS_OPERATIONPLACE.S_ISUSE, TS_OPERATIONPLACE.S_BEFORESTATUS, TS_OPERATIONPLACE.S_AFTERSTATUS, TS_OPERATIONPLACE.I_MODICOUNT "
        + "FROM TS_OPERATIONPLACE "
        +" WHERE " 
        + "S_MODELID = ? AND S_PLACEID = ?"
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "TS_OPERATIONPLACE.S_MODELID, TS_OPERATIONPLACE.S_FORMID, TS_OPERATIONPLACE.S_PLACEID, TS_OPERATIONPLACE.S_PLACETYPE, TS_OPERATIONPLACE.S_STAMPTYPECODE, "
        + "TS_OPERATIONPLACE.S_PLACEDESC, TS_OPERATIONPLACE.S_ISUSE, TS_OPERATIONPLACE.S_BEFORESTATUS, TS_OPERATIONPLACE.S_AFTERSTATUS, TS_OPERATIONPLACE.I_MODICOUNT "
        + "FROM TS_OPERATIONPLACE "
        +" WHERE " 
        + "S_MODELID = ? AND S_PLACEID = ? FOR UPDATE"
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  TS_OPERATIONPLACE.S_MODELID  , TS_OPERATIONPLACE.S_FORMID  , TS_OPERATIONPLACE.S_PLACEID  , TS_OPERATIONPLACE.S_PLACETYPE  , TS_OPERATIONPLACE.S_STAMPTYPECODE "
        + " , TS_OPERATIONPLACE.S_PLACEDESC  , TS_OPERATIONPLACE.S_ISUSE  , TS_OPERATIONPLACE.S_BEFORESTATUS  , TS_OPERATIONPLACE.S_AFTERSTATUS  , TS_OPERATIONPLACE.I_MODICOUNT "
        + "FROM TS_OPERATIONPLACE ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "TS_OPERATIONPLACE.S_MODELID, TS_OPERATIONPLACE.S_FORMID, TS_OPERATIONPLACE.S_PLACEID, TS_OPERATIONPLACE.S_PLACETYPE, TS_OPERATIONPLACE.S_STAMPTYPECODE, "
        + "TS_OPERATIONPLACE.S_PLACEDESC, TS_OPERATIONPLACE.S_ISUSE, TS_OPERATIONPLACE.S_BEFORESTATUS, TS_OPERATIONPLACE.S_AFTERSTATUS, TS_OPERATIONPLACE.I_MODICOUNT "
        + "FROM TS_OPERATIONPLACE " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        + "S_MODELID = ? AND S_PLACEID = ?)"
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE TS_OPERATIONPLACE SET "
        + "S_FORMID =?,S_PLACETYPE =?,S_STAMPTYPECODE =?,S_PLACEDESC =?,S_ISUSE =?, "
        + "S_BEFORESTATUS =?,S_AFTERSTATUS =?,I_MODICOUNT =? "
        + "WHERE "
        + "S_MODELID = ? AND S_PLACEID = ?"
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE TS_OPERATIONPLACE SET "
        + "S_FORMID =?, S_PLACETYPE =?, S_STAMPTYPECODE =?, S_PLACEDESC =?, S_ISUSE =?,  "
        + "S_BEFORESTATUS =?, S_AFTERSTATUS =?, I_MODICOUNT =? "
        + "WHERE "
        + "S_MODELID = ? AND S_PLACEID = ?"
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM TS_OPERATIONPLACE " 
        + " WHERE "
        + "S_MODELID = ? AND S_PLACEID = ?"
        ;


	/**
	*  批量查询 一次最多查询的参数数量
	*/
	
	public static final int FIND_BATCH_SIZE = 150  / 2;
	



   /**
   * Create a new record in Database.
   */
  public void create(IDto _dto,  Connection conn) throws SQLException
  {
     TsOperationplaceDto dto = (TsOperationplaceDto)_dto ;
     String msgValid = dto.checkValid() ;
     if (msgValid != null)
     	throw new SQLException("插入错误，"+msgValid) ;

     PreparedStatement ps = null;
     try
     {
         ps = conn.prepareStatement(SQL_INSERT);
          ps.setString(1, dto.getSmodelid());

          ps.setString(2, dto.getSformid());

          ps.setString(3, dto.getSplaceid());

          ps.setString(4, dto.getSplacetype());

          ps.setString(5, dto.getSstamptypecode());

          ps.setString(6, dto.getSplacedesc());

          ps.setString(7, dto.getSisuse());

          ps.setString(8, dto.getSbeforestatus());

          ps.setString(9, dto.getSafterstatus());

          if (dto.getImodicount()==null)
            ps.setNull(10, java.sql.Types.INTEGER);
         else
            ps.setInt(10, dto.getImodicount().intValue());
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
       TsOperationplaceDto dto = (TsOperationplaceDto)_dto ;
       String msgValid = dto.checkValid() ;
       if (msgValid != null)
           throw new SQLException("插入错误，"+msgValid) ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try
       {
           ps = conn.prepareStatement(SQL_INSERT_WITH_RESULT);
            ps.setString(1, dto.getSmodelid());
            ps.setString(2, dto.getSformid());
            ps.setString(3, dto.getSplaceid());
            ps.setString(4, dto.getSplacetype());
            ps.setString(5, dto.getSstamptypecode());
            ps.setString(6, dto.getSplacedesc());
            ps.setString(7, dto.getSisuse());
            ps.setString(8, dto.getSbeforestatus());
            ps.setString(9, dto.getSafterstatus());
            if (dto.getImodicount()==null)
              ps.setNull(10, java.sql.Types.INTEGER);
           else
              ps.setInt(10, dto.getImodicount().intValue());
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
        TsOperationplaceDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (TsOperationplaceDto)_dtos[i] ; 
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
 	    	    dto  = (TsOperationplaceDto)_dtos[i] ; 
  
               ps.setString(1, dto.getSmodelid());
  
               ps.setString(2, dto.getSformid());
  
               ps.setString(3, dto.getSplaceid());
  
               ps.setString(4, dto.getSplacetype());
  
               ps.setString(5, dto.getSstamptypecode());
  
               ps.setString(6, dto.getSplacedesc());
  
               ps.setString(7, dto.getSisuse());
  
               ps.setString(8, dto.getSbeforestatus());
  
               ps.setString(9, dto.getSafterstatus());
  
               if (dto.getImodicount()==null)
                  ps.setNull(10, java.sql.Types.INTEGER);
               else
                  ps.setInt(10, dto.getImodicount().intValue());
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
        	
       TsOperationplacePK pk = (TsOperationplacePK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT);
           ps.setString(1, pk.getSmodelid());

           ps.setString(2, pk.getSplaceid());

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
        	
       TsOperationplacePK pk = (TsOperationplacePK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT_FOR_UPDATE);
           ps.setString(1, pk.getSmodelid());

           ps.setString(2, pk.getSplaceid());

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
        TsOperationplacePK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TsOperationplacePK)_pks[i] ; 
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
                    pk  = (TsOperationplacePK)(pks.get(i)) ; 
                   ps.setString((i-iBegin)*2+1, pk.getSmodelid());

                   ps.setString((i-iBegin)*2+2, pk.getSplaceid());

			
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
            TsOperationplaceDto[] dtos = new TsOperationplaceDto[0];
		    dtos = (TsOperationplaceDto[]) results.toArray(dtos) ;
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
             TsOperationplaceDto  dto = new  TsOperationplaceDto ();
             //S_MODELID
             str = rs.getString("S_MODELID");
             if (str == null)
                dto.setSmodelid(null);
             else
                dto.setSmodelid(str.trim());

             //S_FORMID
             str = rs.getString("S_FORMID");
             if (str == null)
                dto.setSformid(null);
             else
                dto.setSformid(str.trim());

             //S_PLACEID
             str = rs.getString("S_PLACEID");
             if (str == null)
                dto.setSplaceid(null);
             else
                dto.setSplaceid(str.trim());

             //S_PLACETYPE
             str = rs.getString("S_PLACETYPE");
             if (str == null)
                dto.setSplacetype(null);
             else
                dto.setSplacetype(str.trim());

             //S_STAMPTYPECODE
             str = rs.getString("S_STAMPTYPECODE");
             if (str == null)
                dto.setSstamptypecode(null);
             else
                dto.setSstamptypecode(str.trim());

             //S_PLACEDESC
             str = rs.getString("S_PLACEDESC");
             if (str == null)
                dto.setSplacedesc(null);
             else
                dto.setSplacedesc(str.trim());

             //S_ISUSE
             str = rs.getString("S_ISUSE");
             if (str == null)
                dto.setSisuse(null);
             else
                dto.setSisuse(str.trim());

             //S_BEFORESTATUS
             str = rs.getString("S_BEFORESTATUS");
             if (str == null)
                dto.setSbeforestatus(null);
             else
                dto.setSbeforestatus(str.trim());

             //S_AFTERSTATUS
             str = rs.getString("S_AFTERSTATUS");
             if (str == null)
                dto.setSafterstatus(null);
             else
                dto.setSafterstatus(str.trim());

             //I_MODICOUNT
             str = rs.getString("I_MODICOUNT");
             if(str!=null){
                dto.setImodicount(new Integer(str));
             }



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
        TsOperationplaceDto dto = (TsOperationplaceDto)_dto ;
        PreparedStatement ps = null;
        try {
            if(isLobSupport){
                ps = conn.prepareStatement(SQL_UPDATE_LOB);
            }
            else{
                ps = conn.prepareStatement(SQL_UPDATE);
            }
            int pos = 1;
            //S_FORMID
            ps.setString(pos, dto.getSformid());
            pos++;

            //S_PLACETYPE
            ps.setString(pos, dto.getSplacetype());
            pos++;

            //S_STAMPTYPECODE
            ps.setString(pos, dto.getSstamptypecode());
            pos++;

            //S_PLACEDESC
            ps.setString(pos, dto.getSplacedesc());
            pos++;

            //S_ISUSE
            ps.setString(pos, dto.getSisuse());
            pos++;

            //S_BEFORESTATUS
            ps.setString(pos, dto.getSbeforestatus());
            pos++;

            //S_AFTERSTATUS
            ps.setString(pos, dto.getSafterstatus());
            pos++;

            //I_MODICOUNT
            if (dto.getImodicount()==null)
                ps.setNull(pos, java.sql.Types.INTEGER);
            else
                ps.setInt(pos, dto.getImodicount().intValue());
            pos++;


           //S_MODELID
           ps.setString(pos, dto.getSmodelid());
           pos++;
           //S_PLACEID
           ps.setString(pos, dto.getSplaceid());
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
     
     	 TsOperationplaceDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (TsOperationplaceDto)_dtos[i] ; 
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
                dto  = (TsOperationplaceDto)_dtos[i] ; 
                int pos = 1;
                //S_FORMID
                 ps.setString(pos, dto.getSformid());
                pos++;

                //S_PLACETYPE
                 ps.setString(pos, dto.getSplacetype());
                pos++;

                //S_STAMPTYPECODE
                 ps.setString(pos, dto.getSstamptypecode());
                pos++;

                //S_PLACEDESC
                 ps.setString(pos, dto.getSplacedesc());
                pos++;

                //S_ISUSE
                 ps.setString(pos, dto.getSisuse());
                pos++;

                //S_BEFORESTATUS
                 ps.setString(pos, dto.getSbeforestatus());
                pos++;

                //S_AFTERSTATUS
                 ps.setString(pos, dto.getSafterstatus());
                pos++;

                //I_MODICOUNT
                 if (dto.getImodicount()==null)
                   ps.setNull(pos, java.sql.Types.INTEGER);
                else
                   ps.setInt(pos, dto.getImodicount().intValue());
                pos++ ;


               //S_MODELID
               ps.setString(pos, dto.getSmodelid());
               pos++;
               //S_PLACEID
               ps.setString(pos, dto.getSplaceid());
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
       TsOperationplacePK pk = (TsOperationplacePK)_pk ;


       PreparedStatement ps = null;
       try {
           ps = conn.prepareStatement(SQL_DELETE);
           ps.setString(1, pk.getSmodelid());
           ps.setString(2, pk.getSplaceid());
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
        TsOperationplacePK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TsOperationplacePK)_pks[i] ; 
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
       			pk  = (TsOperationplacePK)(pks.get(i)) ; 
                ps.setString(1, pk.getSmodelid());
                ps.setString(2, pk.getSplaceid());
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
                        		throw new SQLException("数据库表：TS_OPERATIONPLACE没有检查修改的字段");
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

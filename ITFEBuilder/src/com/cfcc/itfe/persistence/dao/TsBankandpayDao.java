    



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

import com.cfcc.itfe.persistence.dto.TsBankandpayDto ;
import com.cfcc.itfe.persistence.pk.TsBankandpayPK ;


/**
 * <p>Title: DAO of table: TS_BANKANDPAY</p>
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

public class TsBankandpayDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO TS_BANKANDPAY ("
          + "S_ORGCODE,S_NATIONTAXBANK,S_AREATAXBANK,S_PAYERBANKCODE,S_PAYERACCCODE"
          + ",S_PAYERACCNAME,I_MODICOUNT"
        + ") VALUES ("
        + "?,?,?,?,?,?,?)";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "TS_BANKANDPAY.S_ORGCODE, TS_BANKANDPAY.S_NATIONTAXBANK, TS_BANKANDPAY.S_AREATAXBANK, TS_BANKANDPAY.S_PAYERBANKCODE, TS_BANKANDPAY.S_PAYERACCCODE, "
        + "TS_BANKANDPAY.S_PAYERACCNAME, TS_BANKANDPAY.I_MODICOUNT "
        + "FROM TS_BANKANDPAY "
        +" WHERE " 
        + "S_ORGCODE = ? AND S_NATIONTAXBANK = ? AND S_AREATAXBANK = ?"
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "TS_BANKANDPAY.S_ORGCODE, TS_BANKANDPAY.S_NATIONTAXBANK, TS_BANKANDPAY.S_AREATAXBANK, TS_BANKANDPAY.S_PAYERBANKCODE, TS_BANKANDPAY.S_PAYERACCCODE, "
        + "TS_BANKANDPAY.S_PAYERACCNAME, TS_BANKANDPAY.I_MODICOUNT "
        + "FROM TS_BANKANDPAY "
        +" WHERE " 
        + "S_ORGCODE = ? AND S_NATIONTAXBANK = ? AND S_AREATAXBANK = ? FOR UPDATE"
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  TS_BANKANDPAY.S_ORGCODE  , TS_BANKANDPAY.S_NATIONTAXBANK  , TS_BANKANDPAY.S_AREATAXBANK  , TS_BANKANDPAY.S_PAYERBANKCODE  , TS_BANKANDPAY.S_PAYERACCCODE "
        + " , TS_BANKANDPAY.S_PAYERACCNAME  , TS_BANKANDPAY.I_MODICOUNT "
        + "FROM TS_BANKANDPAY ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "TS_BANKANDPAY.S_ORGCODE, TS_BANKANDPAY.S_NATIONTAXBANK, TS_BANKANDPAY.S_AREATAXBANK, TS_BANKANDPAY.S_PAYERBANKCODE, TS_BANKANDPAY.S_PAYERACCCODE, "
        + "TS_BANKANDPAY.S_PAYERACCNAME, TS_BANKANDPAY.I_MODICOUNT "
        + "FROM TS_BANKANDPAY " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        + "S_ORGCODE = ? AND S_NATIONTAXBANK = ? AND S_AREATAXBANK = ?)"
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE TS_BANKANDPAY SET "
        + "S_PAYERBANKCODE =?,S_PAYERACCCODE =?,S_PAYERACCNAME =?,I_MODICOUNT =? "
        + "WHERE "
        + "S_ORGCODE = ? AND S_NATIONTAXBANK = ? AND S_AREATAXBANK = ?"
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE TS_BANKANDPAY SET "
        + "S_PAYERBANKCODE =?, S_PAYERACCCODE =?, S_PAYERACCNAME =?, I_MODICOUNT =? "
        + "WHERE "
        + "S_ORGCODE = ? AND S_NATIONTAXBANK = ? AND S_AREATAXBANK = ?"
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM TS_BANKANDPAY " 
        + " WHERE "
        + "S_ORGCODE = ? AND S_NATIONTAXBANK = ? AND S_AREATAXBANK = ?"
        ;


	/**
	*  批量查询 一次最多查询的参数数量
	*/
	
	public static final int FIND_BATCH_SIZE = 150  / 3;
	



   /**
   * Create a new record in Database.
   */
  public void create(IDto _dto,  Connection conn) throws SQLException
  {
     TsBankandpayDto dto = (TsBankandpayDto)_dto ;
     String msgValid = dto.checkValid() ;
     if (msgValid != null)
     	throw new SQLException("插入错误，"+msgValid) ;

     PreparedStatement ps = null;
     try
     {
         ps = conn.prepareStatement(SQL_INSERT);
          ps.setString(1, dto.getSorgcode());

          ps.setString(2, dto.getSnationtaxbank());

          ps.setString(3, dto.getSareataxbank());

          ps.setString(4, dto.getSpayerbankcode());

          ps.setString(5, dto.getSpayeracccode());

          ps.setString(6, dto.getSpayeraccname());

          if (dto.getImodicount()==null)
            ps.setNull(7, java.sql.Types.INTEGER);
         else
            ps.setInt(7, dto.getImodicount().intValue());
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
       TsBankandpayDto dto = (TsBankandpayDto)_dto ;
       String msgValid = dto.checkValid() ;
       if (msgValid != null)
           throw new SQLException("插入错误，"+msgValid) ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try
       {
           ps = conn.prepareStatement(SQL_INSERT_WITH_RESULT);
            ps.setString(1, dto.getSorgcode());
            ps.setString(2, dto.getSnationtaxbank());
            ps.setString(3, dto.getSareataxbank());
            ps.setString(4, dto.getSpayerbankcode());
            ps.setString(5, dto.getSpayeracccode());
            ps.setString(6, dto.getSpayeraccname());
            if (dto.getImodicount()==null)
              ps.setNull(7, java.sql.Types.INTEGER);
           else
              ps.setInt(7, dto.getImodicount().intValue());
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
        TsBankandpayDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (TsBankandpayDto)_dtos[i] ; 
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
 	    	    dto  = (TsBankandpayDto)_dtos[i] ; 
  
               ps.setString(1, dto.getSorgcode());
  
               ps.setString(2, dto.getSnationtaxbank());
  
               ps.setString(3, dto.getSareataxbank());
  
               ps.setString(4, dto.getSpayerbankcode());
  
               ps.setString(5, dto.getSpayeracccode());
  
               ps.setString(6, dto.getSpayeraccname());
  
               if (dto.getImodicount()==null)
                  ps.setNull(7, java.sql.Types.INTEGER);
               else
                  ps.setInt(7, dto.getImodicount().intValue());
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
        	
       TsBankandpayPK pk = (TsBankandpayPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT);
           ps.setString(1, pk.getSorgcode());

           ps.setString(2, pk.getSnationtaxbank());

           ps.setString(3, pk.getSareataxbank());

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
        	
       TsBankandpayPK pk = (TsBankandpayPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT_FOR_UPDATE);
           ps.setString(1, pk.getSorgcode());

           ps.setString(2, pk.getSnationtaxbank());

           ps.setString(3, pk.getSareataxbank());

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
        TsBankandpayPK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TsBankandpayPK)_pks[i] ; 
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
                    pk  = (TsBankandpayPK)(pks.get(i)) ; 
                   ps.setString((i-iBegin)*3+1, pk.getSorgcode());

                   ps.setString((i-iBegin)*3+2, pk.getSnationtaxbank());

                   ps.setString((i-iBegin)*3+3, pk.getSareataxbank());

			
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
            TsBankandpayDto[] dtos = new TsBankandpayDto[0];
		    dtos = (TsBankandpayDto[]) results.toArray(dtos) ;
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
             TsBankandpayDto  dto = new  TsBankandpayDto ();
             //S_ORGCODE
             str = rs.getString("S_ORGCODE");
             if (str == null)
                dto.setSorgcode(null);
             else
                dto.setSorgcode(str.trim());

             //S_NATIONTAXBANK
             str = rs.getString("S_NATIONTAXBANK");
             if (str == null)
                dto.setSnationtaxbank(null);
             else
                dto.setSnationtaxbank(str.trim());

             //S_AREATAXBANK
             str = rs.getString("S_AREATAXBANK");
             if (str == null)
                dto.setSareataxbank(null);
             else
                dto.setSareataxbank(str.trim());

             //S_PAYERBANKCODE
             str = rs.getString("S_PAYERBANKCODE");
             if (str == null)
                dto.setSpayerbankcode(null);
             else
                dto.setSpayerbankcode(str.trim());

             //S_PAYERACCCODE
             str = rs.getString("S_PAYERACCCODE");
             if (str == null)
                dto.setSpayeracccode(null);
             else
                dto.setSpayeracccode(str.trim());

             //S_PAYERACCNAME
             str = rs.getString("S_PAYERACCNAME");
             if (str == null)
                dto.setSpayeraccname(null);
             else
                dto.setSpayeraccname(str.trim());

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
        TsBankandpayDto dto = (TsBankandpayDto)_dto ;
        PreparedStatement ps = null;
        try {
            if(isLobSupport){
                ps = conn.prepareStatement(SQL_UPDATE_LOB);
            }
            else{
                ps = conn.prepareStatement(SQL_UPDATE);
            }
            int pos = 1;
            //S_PAYERBANKCODE
            ps.setString(pos, dto.getSpayerbankcode());
            pos++;

            //S_PAYERACCCODE
            ps.setString(pos, dto.getSpayeracccode());
            pos++;

            //S_PAYERACCNAME
            ps.setString(pos, dto.getSpayeraccname());
            pos++;

            //I_MODICOUNT
            if (dto.getImodicount()==null)
                ps.setNull(pos, java.sql.Types.INTEGER);
            else
                ps.setInt(pos, dto.getImodicount().intValue());
            pos++;


           //S_ORGCODE
           ps.setString(pos, dto.getSorgcode());
           pos++;
           //S_NATIONTAXBANK
           ps.setString(pos, dto.getSnationtaxbank());
           pos++;
           //S_AREATAXBANK
           ps.setString(pos, dto.getSareataxbank());
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
     
     	 TsBankandpayDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (TsBankandpayDto)_dtos[i] ; 
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
                dto  = (TsBankandpayDto)_dtos[i] ; 
                int pos = 1;
                //S_PAYERBANKCODE
                 ps.setString(pos, dto.getSpayerbankcode());
                pos++;

                //S_PAYERACCCODE
                 ps.setString(pos, dto.getSpayeracccode());
                pos++;

                //S_PAYERACCNAME
                 ps.setString(pos, dto.getSpayeraccname());
                pos++;

                //I_MODICOUNT
                 if (dto.getImodicount()==null)
                   ps.setNull(pos, java.sql.Types.INTEGER);
                else
                   ps.setInt(pos, dto.getImodicount().intValue());
                pos++ ;


               //S_ORGCODE
               ps.setString(pos, dto.getSorgcode());
               pos++;
               //S_NATIONTAXBANK
               ps.setString(pos, dto.getSnationtaxbank());
               pos++;
               //S_AREATAXBANK
               ps.setString(pos, dto.getSareataxbank());
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
       TsBankandpayPK pk = (TsBankandpayPK)_pk ;


       PreparedStatement ps = null;
       try {
           ps = conn.prepareStatement(SQL_DELETE);
           ps.setString(1, pk.getSorgcode());
           ps.setString(2, pk.getSnationtaxbank());
           ps.setString(3, pk.getSareataxbank());
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
        TsBankandpayPK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TsBankandpayPK)_pks[i] ; 
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
       			pk  = (TsBankandpayPK)(pks.get(i)) ; 
                ps.setString(1, pk.getSorgcode());
                ps.setString(2, pk.getSnationtaxbank());
                ps.setString(3, pk.getSareataxbank());
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
            		throw new SQLException("数据库表：TS_BANKANDPAY没有检查修改的字段");
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

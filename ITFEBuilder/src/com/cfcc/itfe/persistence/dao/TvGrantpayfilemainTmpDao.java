    



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

import com.cfcc.itfe.persistence.dto.TvGrantpayfilemainTmpDto ;
import com.cfcc.itfe.persistence.pk.TvGrantpayfilemainTmpPK ;


/**
 * <p>Title: DAO of table: TV_GRANTPAYFILEMAIN_TMP</p>
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

public class TvGrantpayfilemainTmpDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO TV_GRANTPAYFILEMAIN_TMP ("
          + "S_LIMITID,S_OFYEAR,S_GENTICKETDATE,S_OFMONTH,N_MONEY"
          + ",S_RECBANKNAME,S_FILENAME,S_ORGCODE"
        + ") VALUES ("
        + "?,?,?,?,?,?,?,?)";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "TV_GRANTPAYFILEMAIN_TMP.S_LIMITID, TV_GRANTPAYFILEMAIN_TMP.S_OFYEAR, TV_GRANTPAYFILEMAIN_TMP.S_GENTICKETDATE, TV_GRANTPAYFILEMAIN_TMP.S_OFMONTH, TV_GRANTPAYFILEMAIN_TMP.N_MONEY, "
        + "TV_GRANTPAYFILEMAIN_TMP.S_RECBANKNAME, TV_GRANTPAYFILEMAIN_TMP.S_FILENAME, TV_GRANTPAYFILEMAIN_TMP.S_ORGCODE "
        + "FROM TV_GRANTPAYFILEMAIN_TMP "
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "TV_GRANTPAYFILEMAIN_TMP.S_LIMITID, TV_GRANTPAYFILEMAIN_TMP.S_OFYEAR, TV_GRANTPAYFILEMAIN_TMP.S_GENTICKETDATE, TV_GRANTPAYFILEMAIN_TMP.S_OFMONTH, TV_GRANTPAYFILEMAIN_TMP.N_MONEY, "
        + "TV_GRANTPAYFILEMAIN_TMP.S_RECBANKNAME, TV_GRANTPAYFILEMAIN_TMP.S_FILENAME, TV_GRANTPAYFILEMAIN_TMP.S_ORGCODE "
        + "FROM TV_GRANTPAYFILEMAIN_TMP "
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  TV_GRANTPAYFILEMAIN_TMP.S_LIMITID  , TV_GRANTPAYFILEMAIN_TMP.S_OFYEAR  , TV_GRANTPAYFILEMAIN_TMP.S_GENTICKETDATE  , TV_GRANTPAYFILEMAIN_TMP.S_OFMONTH  , TV_GRANTPAYFILEMAIN_TMP.N_MONEY "
        + " , TV_GRANTPAYFILEMAIN_TMP.S_RECBANKNAME  , TV_GRANTPAYFILEMAIN_TMP.S_FILENAME  , TV_GRANTPAYFILEMAIN_TMP.S_ORGCODE "
        + "FROM TV_GRANTPAYFILEMAIN_TMP ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "TV_GRANTPAYFILEMAIN_TMP.S_LIMITID, TV_GRANTPAYFILEMAIN_TMP.S_OFYEAR, TV_GRANTPAYFILEMAIN_TMP.S_GENTICKETDATE, TV_GRANTPAYFILEMAIN_TMP.S_OFMONTH, TV_GRANTPAYFILEMAIN_TMP.N_MONEY, "
        + "TV_GRANTPAYFILEMAIN_TMP.S_RECBANKNAME, TV_GRANTPAYFILEMAIN_TMP.S_FILENAME, TV_GRANTPAYFILEMAIN_TMP.S_ORGCODE "
        + "FROM TV_GRANTPAYFILEMAIN_TMP " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE TV_GRANTPAYFILEMAIN_TMP SET "
        + "S_LIMITID =?,S_OFYEAR =?,S_GENTICKETDATE =?,S_OFMONTH =?,N_MONEY =?, "
        + "S_RECBANKNAME =?,S_FILENAME =?,S_ORGCODE =? "
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE TV_GRANTPAYFILEMAIN_TMP SET "
        + "S_LIMITID =?, S_OFYEAR =?, S_GENTICKETDATE =?, S_OFMONTH =?, N_MONEY =?,  "
        + "S_RECBANKNAME =?, S_FILENAME =?, S_ORGCODE =? "
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM TV_GRANTPAYFILEMAIN_TMP " 
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
     TvGrantpayfilemainTmpDto dto = (TvGrantpayfilemainTmpDto)_dto ;
     String msgValid = dto.checkValid() ;
     if (msgValid != null)
     	throw new SQLException("插入错误，"+msgValid) ;

     PreparedStatement ps = null;
     try
     {
         ps = conn.prepareStatement(SQL_INSERT);
          ps.setString(1, dto.getSlimitid());

          ps.setString(2, dto.getSofyear());

          ps.setString(3, dto.getSgenticketdate());

          ps.setString(4, dto.getSofmonth());

          ps.setBigDecimal(5, dto.getNmoney());

          ps.setString(6, dto.getSrecbankname());

          ps.setString(7, dto.getSfilename());

          ps.setString(8, dto.getSorgcode());

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
       TvGrantpayfilemainTmpDto dto = (TvGrantpayfilemainTmpDto)_dto ;
       String msgValid = dto.checkValid() ;
       if (msgValid != null)
           throw new SQLException("插入错误，"+msgValid) ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try
       {
           ps = conn.prepareStatement(SQL_INSERT_WITH_RESULT);
            ps.setString(1, dto.getSlimitid());
            ps.setString(2, dto.getSofyear());
            ps.setString(3, dto.getSgenticketdate());
            ps.setString(4, dto.getSofmonth());
            ps.setBigDecimal(5, dto.getNmoney());
            ps.setString(6, dto.getSrecbankname());
            ps.setString(7, dto.getSfilename());
            ps.setString(8, dto.getSorgcode());
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
        TvGrantpayfilemainTmpDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (TvGrantpayfilemainTmpDto)_dtos[i] ; 
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
 	    	    dto  = (TvGrantpayfilemainTmpDto)_dtos[i] ; 
  
               ps.setString(1, dto.getSlimitid());
  
               ps.setString(2, dto.getSofyear());
  
               ps.setString(3, dto.getSgenticketdate());
  
               ps.setString(4, dto.getSofmonth());
  
               ps.setBigDecimal(5, dto.getNmoney());
  
               ps.setString(6, dto.getSrecbankname());
  
               ps.setString(7, dto.getSfilename());
  
               ps.setString(8, dto.getSorgcode());
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
        	
       TvGrantpayfilemainTmpPK pk = (TvGrantpayfilemainTmpPK)_pk ;

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
        	
       TvGrantpayfilemainTmpPK pk = (TvGrantpayfilemainTmpPK)_pk ;

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
        TvGrantpayfilemainTmpPK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TvGrantpayfilemainTmpPK)_pks[i] ; 
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
                    pk  = (TvGrantpayfilemainTmpPK)(pks.get(i)) ; 
			
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
            TvGrantpayfilemainTmpDto[] dtos = new TvGrantpayfilemainTmpDto[0];
		    dtos = (TvGrantpayfilemainTmpDto[]) results.toArray(dtos) ;
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
             TvGrantpayfilemainTmpDto  dto = new  TvGrantpayfilemainTmpDto ();
             //S_LIMITID
             str = rs.getString("S_LIMITID");
             if (str == null)
                dto.setSlimitid(null);
             else
                dto.setSlimitid(str.trim());

             //S_OFYEAR
             str = rs.getString("S_OFYEAR");
             if (str == null)
                dto.setSofyear(null);
             else
                dto.setSofyear(str.trim());

             //S_GENTICKETDATE
             str = rs.getString("S_GENTICKETDATE");
             if (str == null)
                dto.setSgenticketdate(null);
             else
                dto.setSgenticketdate(str.trim());

             //S_OFMONTH
             str = rs.getString("S_OFMONTH");
             if (str == null)
                dto.setSofmonth(null);
             else
                dto.setSofmonth(str.trim());

             //N_MONEY
           dto.setNmoney(rs.getBigDecimal("N_MONEY"));

             //S_RECBANKNAME
             str = rs.getString("S_RECBANKNAME");
             if (str == null)
                dto.setSrecbankname(null);
             else
                dto.setSrecbankname(str.trim());

             //S_FILENAME
             str = rs.getString("S_FILENAME");
             if (str == null)
                dto.setSfilename(null);
             else
                dto.setSfilename(str.trim());

             //S_ORGCODE
             str = rs.getString("S_ORGCODE");
             if (str == null)
                dto.setSorgcode(null);
             else
                dto.setSorgcode(str.trim());



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
        TvGrantpayfilemainTmpDto dto = (TvGrantpayfilemainTmpDto)_dto ;
        PreparedStatement ps = null;
        try {
            if(isLobSupport){
                ps = conn.prepareStatement(SQL_UPDATE_LOB);
            }
            else{
                ps = conn.prepareStatement(SQL_UPDATE);
            }
            int pos = 1;
            //S_LIMITID
            ps.setString(pos, dto.getSlimitid());
            pos++;

            //S_OFYEAR
            ps.setString(pos, dto.getSofyear());
            pos++;

            //S_GENTICKETDATE
            ps.setString(pos, dto.getSgenticketdate());
            pos++;

            //S_OFMONTH
            ps.setString(pos, dto.getSofmonth());
            pos++;

            //N_MONEY
            ps.setBigDecimal(pos, dto.getNmoney());
            pos++;

            //S_RECBANKNAME
            ps.setString(pos, dto.getSrecbankname());
            pos++;

            //S_FILENAME
            ps.setString(pos, dto.getSfilename());
            pos++;

            //S_ORGCODE
            ps.setString(pos, dto.getSorgcode());
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
     
     	 TvGrantpayfilemainTmpDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (TvGrantpayfilemainTmpDto)_dtos[i] ; 
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
                dto  = (TvGrantpayfilemainTmpDto)_dtos[i] ; 
                int pos = 1;
                //S_LIMITID
                 ps.setString(pos, dto.getSlimitid());
                pos++;

                //S_OFYEAR
                 ps.setString(pos, dto.getSofyear());
                pos++;

                //S_GENTICKETDATE
                 ps.setString(pos, dto.getSgenticketdate());
                pos++;

                //S_OFMONTH
                 ps.setString(pos, dto.getSofmonth());
                pos++;

                //N_MONEY
                 ps.setBigDecimal(pos, dto.getNmoney());
                pos++;

                //S_RECBANKNAME
                 ps.setString(pos, dto.getSrecbankname());
                pos++;

                //S_FILENAME
                 ps.setString(pos, dto.getSfilename());
                pos++;

                //S_ORGCODE
                 ps.setString(pos, dto.getSorgcode());
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
       TvGrantpayfilemainTmpPK pk = (TvGrantpayfilemainTmpPK)_pk ;


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
        TvGrantpayfilemainTmpPK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TvGrantpayfilemainTmpPK)_pks[i] ; 
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
       			pk  = (TvGrantpayfilemainTmpPK)(pks.get(i)) ; 
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
                        		throw new SQLException("数据库表：TV_GRANTPAYFILEMAIN_TMP没有检查修改的字段");
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

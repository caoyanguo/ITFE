    



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

import com.cfcc.itfe.persistence.dto.TdTaxorgParamDto ;
import com.cfcc.itfe.persistence.pk.TdTaxorgParamPK ;


/**
 * <p>Title: DAO of table: TD_TAXORG_PARAM</p>
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

public class TdTaxorgParamDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO TD_TAXORG_PARAM ("
          + "I_SEQNO,S_BOOKORGCODE,C_TRIMFLAG,S_TAXORGCODE,S_TAXORGNAME"
          + ",C_TAXORGPROP,S_TAXORGPHONE,S_TAXORGSHT,TS_SYSUPDATE"
        + ") VALUES ("
        + "DEFAULT ,?,?,?,?,?,?,?,CURRENT TIMESTAMP )";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "TD_TAXORG_PARAM.I_SEQNO, TD_TAXORG_PARAM.S_BOOKORGCODE, TD_TAXORG_PARAM.C_TRIMFLAG, TD_TAXORG_PARAM.S_TAXORGCODE, TD_TAXORG_PARAM.S_TAXORGNAME, "
        + "TD_TAXORG_PARAM.C_TAXORGPROP, TD_TAXORG_PARAM.S_TAXORGPHONE, TD_TAXORG_PARAM.S_TAXORGSHT, TD_TAXORG_PARAM.TS_SYSUPDATE "
        + "FROM TD_TAXORG_PARAM "
        +" WHERE " 
        + "I_SEQNO = ?"
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "TD_TAXORG_PARAM.I_SEQNO, TD_TAXORG_PARAM.S_BOOKORGCODE, TD_TAXORG_PARAM.C_TRIMFLAG, TD_TAXORG_PARAM.S_TAXORGCODE, TD_TAXORG_PARAM.S_TAXORGNAME, "
        + "TD_TAXORG_PARAM.C_TAXORGPROP, TD_TAXORG_PARAM.S_TAXORGPHONE, TD_TAXORG_PARAM.S_TAXORGSHT, TD_TAXORG_PARAM.TS_SYSUPDATE "
        + "FROM TD_TAXORG_PARAM "
        +" WHERE " 
        + "I_SEQNO = ? FOR UPDATE"
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  TD_TAXORG_PARAM.I_SEQNO  , TD_TAXORG_PARAM.S_BOOKORGCODE  , TD_TAXORG_PARAM.C_TRIMFLAG  , TD_TAXORG_PARAM.S_TAXORGCODE  , TD_TAXORG_PARAM.S_TAXORGNAME "
        + " , TD_TAXORG_PARAM.C_TAXORGPROP  , TD_TAXORG_PARAM.S_TAXORGPHONE  , TD_TAXORG_PARAM.S_TAXORGSHT  , TD_TAXORG_PARAM.TS_SYSUPDATE "
        + "FROM TD_TAXORG_PARAM ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "TD_TAXORG_PARAM.I_SEQNO, TD_TAXORG_PARAM.S_BOOKORGCODE, TD_TAXORG_PARAM.C_TRIMFLAG, TD_TAXORG_PARAM.S_TAXORGCODE, TD_TAXORG_PARAM.S_TAXORGNAME, "
        + "TD_TAXORG_PARAM.C_TAXORGPROP, TD_TAXORG_PARAM.S_TAXORGPHONE, TD_TAXORG_PARAM.S_TAXORGSHT, TD_TAXORG_PARAM.TS_SYSUPDATE "
        + "FROM TD_TAXORG_PARAM " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        + "I_SEQNO = ?)"
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE TD_TAXORG_PARAM SET "
        + "S_BOOKORGCODE =?,C_TRIMFLAG =?,S_TAXORGCODE =?,S_TAXORGNAME =?,C_TAXORGPROP =?, "
        + "S_TAXORGPHONE =?,S_TAXORGSHT =?,TS_SYSUPDATE =CURRENT TIMESTAMP "
        + "WHERE "
        + "I_SEQNO = ?"
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE TD_TAXORG_PARAM SET "
        + "S_BOOKORGCODE =?, C_TRIMFLAG =?, S_TAXORGCODE =?, S_TAXORGNAME =?, C_TAXORGPROP =?,  "
        + "S_TAXORGPHONE =?, S_TAXORGSHT =?, TS_SYSUPDATE =CURRENT TIMESTAMP "
        + "WHERE "
        + "I_SEQNO = ?"
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM TD_TAXORG_PARAM " 
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
     TdTaxorgParamDto dto = (TdTaxorgParamDto)_dto ;
     String msgValid = dto.checkValid() ;
     if (msgValid != null)
     	throw new SQLException("插入错误，"+msgValid) ;

     PreparedStatement ps = null;
     try
     {
         ps = conn.prepareStatement(SQL_INSERT);
           ps.setString(1, dto.getSbookorgcode());

          ps.setString(2, dto.getCtrimflag());

          ps.setString(3, dto.getStaxorgcode());

          ps.setString(4, dto.getStaxorgname());

          ps.setString(5, dto.getCtaxorgprop());

          ps.setString(6, dto.getStaxorgphone());

          ps.setString(7, dto.getStaxorgsht());

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
       TdTaxorgParamDto dto = (TdTaxorgParamDto)_dto ;
       String msgValid = dto.checkValid() ;
       if (msgValid != null)
           throw new SQLException("插入错误，"+msgValid) ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try
       {
           ps = conn.prepareStatement(SQL_INSERT_WITH_RESULT);
             ps.setString(1, dto.getSbookorgcode());
            ps.setString(2, dto.getCtrimflag());
            ps.setString(3, dto.getStaxorgcode());
            ps.setString(4, dto.getStaxorgname());
            ps.setString(5, dto.getCtaxorgprop());
            ps.setString(6, dto.getStaxorgphone());
            ps.setString(7, dto.getStaxorgsht());
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
        TdTaxorgParamDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (TdTaxorgParamDto)_dtos[i] ; 
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
 	    	    dto  = (TdTaxorgParamDto)_dtos[i] ; 
   
               ps.setString(1, dto.getSbookorgcode());
  
               ps.setString(2, dto.getCtrimflag());
  
               ps.setString(3, dto.getStaxorgcode());
  
               ps.setString(4, dto.getStaxorgname());
  
               ps.setString(5, dto.getCtaxorgprop());
  
               ps.setString(6, dto.getStaxorgphone());
  
               ps.setString(7, dto.getStaxorgsht());
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
        	
       TdTaxorgParamPK pk = (TdTaxorgParamPK)_pk ;

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
        	
       TdTaxorgParamPK pk = (TdTaxorgParamPK)_pk ;

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
        TdTaxorgParamPK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TdTaxorgParamPK)_pks[i] ; 
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
                    pk  = (TdTaxorgParamPK)(pks.get(i)) ; 
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
            TdTaxorgParamDto[] dtos = new TdTaxorgParamDto[0];
		    dtos = (TdTaxorgParamDto[]) results.toArray(dtos) ;
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
             TdTaxorgParamDto  dto = new  TdTaxorgParamDto ();
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

             //C_TRIMFLAG
             str = rs.getString("C_TRIMFLAG");
             if (str == null)
                dto.setCtrimflag(null);
             else
                dto.setCtrimflag(str.trim());

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

             //C_TAXORGPROP
             str = rs.getString("C_TAXORGPROP");
             if (str == null)
                dto.setCtaxorgprop(null);
             else
                dto.setCtaxorgprop(str.trim());

             //S_TAXORGPHONE
             str = rs.getString("S_TAXORGPHONE");
             if (str == null)
                dto.setStaxorgphone(null);
             else
                dto.setStaxorgphone(str.trim());

             //S_TAXORGSHT
             str = rs.getString("S_TAXORGSHT");
             if (str == null)
                dto.setStaxorgsht(null);
             else
                dto.setStaxorgsht(str.trim());

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
        TdTaxorgParamDto dto = (TdTaxorgParamDto)_dto ;
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

            //C_TRIMFLAG
            ps.setString(pos, dto.getCtrimflag());
            pos++;

            //S_TAXORGCODE
            ps.setString(pos, dto.getStaxorgcode());
            pos++;

            //S_TAXORGNAME
            ps.setString(pos, dto.getStaxorgname());
            pos++;

            //C_TAXORGPROP
            ps.setString(pos, dto.getCtaxorgprop());
            pos++;

            //S_TAXORGPHONE
            ps.setString(pos, dto.getStaxorgphone());
            pos++;

            //S_TAXORGSHT
            ps.setString(pos, dto.getStaxorgsht());
            pos++;

            //TS_SYSUPDATE

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
     
     	 TdTaxorgParamDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (TdTaxorgParamDto)_dtos[i] ; 
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
                dto  = (TdTaxorgParamDto)_dtos[i] ; 
                int pos = 1;
                //S_BOOKORGCODE
                 ps.setString(pos, dto.getSbookorgcode());
                pos++;

                //C_TRIMFLAG
                 ps.setString(pos, dto.getCtrimflag());
                pos++;

                //S_TAXORGCODE
                 ps.setString(pos, dto.getStaxorgcode());
                pos++;

                //S_TAXORGNAME
                 ps.setString(pos, dto.getStaxorgname());
                pos++;

                //C_TAXORGPROP
                 ps.setString(pos, dto.getCtaxorgprop());
                pos++;

                //S_TAXORGPHONE
                 ps.setString(pos, dto.getStaxorgphone());
                pos++;

                //S_TAXORGSHT
                 ps.setString(pos, dto.getStaxorgsht());
                pos++;

                //TS_SYSUPDATE
 
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
       TdTaxorgParamPK pk = (TdTaxorgParamPK)_pk ;


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
        TdTaxorgParamPK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TdTaxorgParamPK)_pks[i] ; 
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
       			pk  = (TdTaxorgParamPK)(pks.get(i)) ; 
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
                        		throw new SQLException("数据库表：TD_TAXORG_PARAM没有检查修改的字段");
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

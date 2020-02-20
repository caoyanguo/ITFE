    



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

import com.cfcc.itfe.persistence.dto.TsPayacctinfoDto ;
import com.cfcc.itfe.persistence.pk.TsPayacctinfoPK ;


/**
 * <p>Title: DAO of table: TS_PAYACCTINFO</p>
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

public class TsPayacctinfoDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO TS_PAYACCTINFO ("
          + "S_ORGCODE,S_TRECODE,S_GENBANKCODE,S_PAYERACCT,S_PAYERNAME"
          + ",S_PAYEEACCT,S_PAYEENAME,S_BIZTYPE,TS_SYSUPDATE"
        + ") VALUES ("
        + "?,?,?,?,?,?,?,?,CURRENT TIMESTAMP )";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "TS_PAYACCTINFO.S_ORGCODE, TS_PAYACCTINFO.S_TRECODE, TS_PAYACCTINFO.S_GENBANKCODE, TS_PAYACCTINFO.S_PAYERACCT, TS_PAYACCTINFO.S_PAYERNAME, "
        + "TS_PAYACCTINFO.S_PAYEEACCT, TS_PAYACCTINFO.S_PAYEENAME, TS_PAYACCTINFO.S_BIZTYPE, TS_PAYACCTINFO.TS_SYSUPDATE "
        + "FROM TS_PAYACCTINFO "
        +" WHERE " 
        + "S_ORGCODE = ? AND S_TRECODE = ? AND S_GENBANKCODE = ? AND S_PAYERACCT = ? AND S_PAYEEACCT = ?"
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "TS_PAYACCTINFO.S_ORGCODE, TS_PAYACCTINFO.S_TRECODE, TS_PAYACCTINFO.S_GENBANKCODE, TS_PAYACCTINFO.S_PAYERACCT, TS_PAYACCTINFO.S_PAYERNAME, "
        + "TS_PAYACCTINFO.S_PAYEEACCT, TS_PAYACCTINFO.S_PAYEENAME, TS_PAYACCTINFO.S_BIZTYPE, TS_PAYACCTINFO.TS_SYSUPDATE "
        + "FROM TS_PAYACCTINFO "
        +" WHERE " 
        + "S_ORGCODE = ? AND S_TRECODE = ? AND S_GENBANKCODE = ? AND S_PAYERACCT = ? AND S_PAYEEACCT = ? FOR UPDATE"
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  TS_PAYACCTINFO.S_ORGCODE  , TS_PAYACCTINFO.S_TRECODE  , TS_PAYACCTINFO.S_GENBANKCODE  , TS_PAYACCTINFO.S_PAYERACCT  , TS_PAYACCTINFO.S_PAYERNAME "
        + " , TS_PAYACCTINFO.S_PAYEEACCT  , TS_PAYACCTINFO.S_PAYEENAME  , TS_PAYACCTINFO.S_BIZTYPE  , TS_PAYACCTINFO.TS_SYSUPDATE "
        + "FROM TS_PAYACCTINFO ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "TS_PAYACCTINFO.S_ORGCODE, TS_PAYACCTINFO.S_TRECODE, TS_PAYACCTINFO.S_GENBANKCODE, TS_PAYACCTINFO.S_PAYERACCT, TS_PAYACCTINFO.S_PAYERNAME, "
        + "TS_PAYACCTINFO.S_PAYEEACCT, TS_PAYACCTINFO.S_PAYEENAME, TS_PAYACCTINFO.S_BIZTYPE, TS_PAYACCTINFO.TS_SYSUPDATE "
        + "FROM TS_PAYACCTINFO " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        + "S_ORGCODE = ? AND S_TRECODE = ? AND S_GENBANKCODE = ? AND S_PAYERACCT = ? AND S_PAYEEACCT = ?)"
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE TS_PAYACCTINFO SET "
        + "S_PAYERNAME =?,S_PAYEENAME =?,S_BIZTYPE =?,TS_SYSUPDATE =CURRENT TIMESTAMP "
        + "WHERE "
        + "S_ORGCODE = ? AND S_TRECODE = ? AND S_GENBANKCODE = ? AND S_PAYERACCT = ? AND S_PAYEEACCT = ?"
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE TS_PAYACCTINFO SET "
        + "S_PAYERNAME =?, S_PAYEENAME =?, S_BIZTYPE =?, TS_SYSUPDATE =CURRENT TIMESTAMP "
        + "WHERE "
        + "S_ORGCODE = ? AND S_TRECODE = ? AND S_GENBANKCODE = ? AND S_PAYERACCT = ? AND S_PAYEEACCT = ?"
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM TS_PAYACCTINFO " 
        + " WHERE "
        + "S_ORGCODE = ? AND S_TRECODE = ? AND S_GENBANKCODE = ? AND S_PAYERACCT = ? AND S_PAYEEACCT = ?"
        ;


	/**
	*  ������ѯ һ������ѯ�Ĳ�������
	*/
	
	public static final int FIND_BATCH_SIZE = 150  / 5;
	



   /**
   * Create a new record in Database.
   */
  public void create(IDto _dto,  Connection conn) throws SQLException
  {
     TsPayacctinfoDto dto = (TsPayacctinfoDto)_dto ;
     String msgValid = dto.checkValid() ;
     if (msgValid != null)
     	throw new SQLException("�������"+msgValid) ;

     PreparedStatement ps = null;
     try
     {
         ps = conn.prepareStatement(SQL_INSERT);
          ps.setString(1, dto.getSorgcode());

          ps.setString(2, dto.getStrecode());

          ps.setString(3, dto.getSgenbankcode());

          ps.setString(4, dto.getSpayeracct());

          ps.setString(5, dto.getSpayername());

          ps.setString(6, dto.getSpayeeacct());

          ps.setString(7, dto.getSpayeename());

          ps.setString(8, dto.getSbiztype());

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
       TsPayacctinfoDto dto = (TsPayacctinfoDto)_dto ;
       String msgValid = dto.checkValid() ;
       if (msgValid != null)
           throw new SQLException("�������"+msgValid) ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try
       {
           ps = conn.prepareStatement(SQL_INSERT_WITH_RESULT);
            ps.setString(1, dto.getSorgcode());
            ps.setString(2, dto.getStrecode());
            ps.setString(3, dto.getSgenbankcode());
            ps.setString(4, dto.getSpayeracct());
            ps.setString(5, dto.getSpayername());
            ps.setString(6, dto.getSpayeeacct());
            ps.setString(7, dto.getSpayeename());
            ps.setString(8, dto.getSbiztype());
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
        TsPayacctinfoDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (TsPayacctinfoDto)_dtos[i] ; 
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
 	    	    dto  = (TsPayacctinfoDto)_dtos[i] ; 
  
               ps.setString(1, dto.getSorgcode());
  
               ps.setString(2, dto.getStrecode());
  
               ps.setString(3, dto.getSgenbankcode());
  
               ps.setString(4, dto.getSpayeracct());
  
               ps.setString(5, dto.getSpayername());
  
               ps.setString(6, dto.getSpayeeacct());
  
               ps.setString(7, dto.getSpayeename());
  
               ps.setString(8, dto.getSbiztype());
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
        	
       TsPayacctinfoPK pk = (TsPayacctinfoPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT);
           ps.setString(1, pk.getSorgcode());

           ps.setString(2, pk.getStrecode());

           ps.setString(3, pk.getSgenbankcode());

           ps.setString(4, pk.getSpayeracct());

           ps.setString(5, pk.getSpayeeacct());

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
        	
       TsPayacctinfoPK pk = (TsPayacctinfoPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT_FOR_UPDATE);
           ps.setString(1, pk.getSorgcode());

           ps.setString(2, pk.getStrecode());

           ps.setString(3, pk.getSgenbankcode());

           ps.setString(4, pk.getSpayeracct());

           ps.setString(5, pk.getSpayeeacct());

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
        TsPayacctinfoPK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TsPayacctinfoPK)_pks[i] ; 
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
                    pk  = (TsPayacctinfoPK)(pks.get(i)) ; 
                   ps.setString((i-iBegin)*5+1, pk.getSorgcode());

                   ps.setString((i-iBegin)*5+2, pk.getStrecode());

                   ps.setString((i-iBegin)*5+3, pk.getSgenbankcode());

                   ps.setString((i-iBegin)*5+4, pk.getSpayeracct());

                   ps.setString((i-iBegin)*5+5, pk.getSpayeeacct());

			
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
            TsPayacctinfoDto[] dtos = new TsPayacctinfoDto[0];
		    dtos = (TsPayacctinfoDto[]) results.toArray(dtos) ;
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
             TsPayacctinfoDto  dto = new  TsPayacctinfoDto ();
             //S_ORGCODE
             str = rs.getString("S_ORGCODE");
             if (str == null)
                dto.setSorgcode(null);
             else
                dto.setSorgcode(str.trim());

             //S_TRECODE
             str = rs.getString("S_TRECODE");
             if (str == null)
                dto.setStrecode(null);
             else
                dto.setStrecode(str.trim());

             //S_GENBANKCODE
             str = rs.getString("S_GENBANKCODE");
             if (str == null)
                dto.setSgenbankcode(null);
             else
                dto.setSgenbankcode(str.trim());

             //S_PAYERACCT
             str = rs.getString("S_PAYERACCT");
             if (str == null)
                dto.setSpayeracct(null);
             else
                dto.setSpayeracct(str.trim());

             //S_PAYERNAME
             str = rs.getString("S_PAYERNAME");
             if (str == null)
                dto.setSpayername(null);
             else
                dto.setSpayername(str.trim());

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

             //S_BIZTYPE
             str = rs.getString("S_BIZTYPE");
             if (str == null)
                dto.setSbiztype(null);
             else
                dto.setSbiztype(str.trim());

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
        TsPayacctinfoDto dto = (TsPayacctinfoDto)_dto ;
        PreparedStatement ps = null;
        try {
            if(isLobSupport){
                ps = conn.prepareStatement(SQL_UPDATE_LOB);
            }
            else{
                ps = conn.prepareStatement(SQL_UPDATE);
            }
            int pos = 1;
            //S_PAYERNAME
            ps.setString(pos, dto.getSpayername());
            pos++;

            //S_PAYEENAME
            ps.setString(pos, dto.getSpayeename());
            pos++;

            //S_BIZTYPE
            ps.setString(pos, dto.getSbiztype());
            pos++;

            //TS_SYSUPDATE

           //S_ORGCODE
           ps.setString(pos, dto.getSorgcode());
           pos++;
           //S_TRECODE
           ps.setString(pos, dto.getStrecode());
           pos++;
           //S_GENBANKCODE
           ps.setString(pos, dto.getSgenbankcode());
           pos++;
           //S_PAYERACCT
           ps.setString(pos, dto.getSpayeracct());
           pos++;
           //S_PAYEEACCT
           ps.setString(pos, dto.getSpayeeacct());
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
     
     	 TsPayacctinfoDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (TsPayacctinfoDto)_dtos[i] ; 
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
                dto  = (TsPayacctinfoDto)_dtos[i] ; 
                int pos = 1;
                //S_PAYERNAME
                 ps.setString(pos, dto.getSpayername());
                pos++;

                //S_PAYEENAME
                 ps.setString(pos, dto.getSpayeename());
                pos++;

                //S_BIZTYPE
                 ps.setString(pos, dto.getSbiztype());
                pos++;

                //TS_SYSUPDATE
 
               //S_ORGCODE
               ps.setString(pos, dto.getSorgcode());
               pos++;
               //S_TRECODE
               ps.setString(pos, dto.getStrecode());
               pos++;
               //S_GENBANKCODE
               ps.setString(pos, dto.getSgenbankcode());
               pos++;
               //S_PAYERACCT
               ps.setString(pos, dto.getSpayeracct());
               pos++;
               //S_PAYEEACCT
               ps.setString(pos, dto.getSpayeeacct());
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
       TsPayacctinfoPK pk = (TsPayacctinfoPK)_pk ;


       PreparedStatement ps = null;
       try {
           ps = conn.prepareStatement(SQL_DELETE);
           ps.setString(1, pk.getSorgcode());
           ps.setString(2, pk.getStrecode());
           ps.setString(3, pk.getSgenbankcode());
           ps.setString(4, pk.getSpayeracct());
           ps.setString(5, pk.getSpayeeacct());
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
        TsPayacctinfoPK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TsPayacctinfoPK)_pks[i] ; 
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
       			pk  = (TsPayacctinfoPK)(pks.get(i)) ; 
                ps.setString(1, pk.getSorgcode());
                ps.setString(2, pk.getStrecode());
                ps.setString(3, pk.getSgenbankcode());
                ps.setString(4, pk.getSpayeracct());
                ps.setString(5, pk.getSpayeeacct());
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
            		throw new SQLException("���ݿ����TS_PAYACCTINFOû�м���޸ĵ��ֶ�");
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
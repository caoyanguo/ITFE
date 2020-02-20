    



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

import com.cfcc.itfe.persistence.dto.TvAmtControlInfoDto ;
import com.cfcc.itfe.persistence.pk.TvAmtControlInfoPK ;


/**
 * <p>Title: DAO of table: TV_AMT_CONTROL_INFO</p>
 * <p>Description: Data Access Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:01 </p>
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

public class TvAmtControlInfoDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO TV_AMT_CONTROL_INFO ("
          + "S_TRECODE,S_BANKCODE,S_BUDGETUNITCODE,S_FUNCBDGSBTCODE,S_YEAR"
          + ",N_AMT,S_PAYTYPECODE,S_BUDGETTYPE,S_REMARK"
        + ") VALUES ("
        + "?,?,?,?,?,?,?,?,?)";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "TV_AMT_CONTROL_INFO.S_TRECODE, TV_AMT_CONTROL_INFO.S_BANKCODE, TV_AMT_CONTROL_INFO.S_BUDGETUNITCODE, TV_AMT_CONTROL_INFO.S_FUNCBDGSBTCODE, TV_AMT_CONTROL_INFO.S_YEAR, "
        + "TV_AMT_CONTROL_INFO.N_AMT, TV_AMT_CONTROL_INFO.S_PAYTYPECODE, TV_AMT_CONTROL_INFO.S_BUDGETTYPE, TV_AMT_CONTROL_INFO.S_REMARK "
        + "FROM TV_AMT_CONTROL_INFO "
        +" WHERE " 
        + "S_TRECODE = ? AND S_BANKCODE = ? AND S_BUDGETUNITCODE = ? AND S_FUNCBDGSBTCODE = ? AND S_YEAR = ? AND "
        + "S_PAYTYPECODE = ? AND S_BUDGETTYPE = ?"
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "TV_AMT_CONTROL_INFO.S_TRECODE, TV_AMT_CONTROL_INFO.S_BANKCODE, TV_AMT_CONTROL_INFO.S_BUDGETUNITCODE, TV_AMT_CONTROL_INFO.S_FUNCBDGSBTCODE, TV_AMT_CONTROL_INFO.S_YEAR, "
        + "TV_AMT_CONTROL_INFO.N_AMT, TV_AMT_CONTROL_INFO.S_PAYTYPECODE, TV_AMT_CONTROL_INFO.S_BUDGETTYPE, TV_AMT_CONTROL_INFO.S_REMARK "
        + "FROM TV_AMT_CONTROL_INFO "
        +" WHERE " 
        + "S_TRECODE = ? AND S_BANKCODE = ? AND S_BUDGETUNITCODE = ? AND S_FUNCBDGSBTCODE = ? AND S_YEAR = ? AND  FOR UPDATE"
        + "S_PAYTYPECODE = ? AND S_BUDGETTYPE = ? FOR UPDATE"
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  TV_AMT_CONTROL_INFO.S_TRECODE  , TV_AMT_CONTROL_INFO.S_BANKCODE  , TV_AMT_CONTROL_INFO.S_BUDGETUNITCODE  , TV_AMT_CONTROL_INFO.S_FUNCBDGSBTCODE  , TV_AMT_CONTROL_INFO.S_YEAR "
        + " , TV_AMT_CONTROL_INFO.N_AMT  , TV_AMT_CONTROL_INFO.S_PAYTYPECODE  , TV_AMT_CONTROL_INFO.S_BUDGETTYPE  , TV_AMT_CONTROL_INFO.S_REMARK "
        + "FROM TV_AMT_CONTROL_INFO ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "TV_AMT_CONTROL_INFO.S_TRECODE, TV_AMT_CONTROL_INFO.S_BANKCODE, TV_AMT_CONTROL_INFO.S_BUDGETUNITCODE, TV_AMT_CONTROL_INFO.S_FUNCBDGSBTCODE, TV_AMT_CONTROL_INFO.S_YEAR, "
        + "TV_AMT_CONTROL_INFO.N_AMT, TV_AMT_CONTROL_INFO.S_PAYTYPECODE, TV_AMT_CONTROL_INFO.S_BUDGETTYPE, TV_AMT_CONTROL_INFO.S_REMARK "
        + "FROM TV_AMT_CONTROL_INFO " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        + "S_TRECODE = ? AND S_BANKCODE = ? AND S_BUDGETUNITCODE = ? AND S_FUNCBDGSBTCODE = ? AND S_YEAR = ? AND )"
        + "S_PAYTYPECODE = ? AND S_BUDGETTYPE = ?)"
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE TV_AMT_CONTROL_INFO SET "
        + "N_AMT =?,S_REMARK =? "
        + "WHERE "
        + "S_TRECODE = ? AND S_BANKCODE = ? AND S_BUDGETUNITCODE = ? AND S_FUNCBDGSBTCODE = ? AND S_YEAR = ? AND "
        + "S_PAYTYPECODE = ? AND S_BUDGETTYPE = ?"
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE TV_AMT_CONTROL_INFO SET "
        + "N_AMT =?, S_REMARK =? "
        + "WHERE "
        + "S_TRECODE = ? AND S_BANKCODE = ? AND S_BUDGETUNITCODE = ? AND S_FUNCBDGSBTCODE = ? AND S_YEAR = ? AND "
        + "S_PAYTYPECODE = ? AND S_BUDGETTYPE = ?"
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM TV_AMT_CONTROL_INFO " 
        + " WHERE "
        + "S_TRECODE = ? AND S_BANKCODE = ? AND S_BUDGETUNITCODE = ? AND S_FUNCBDGSBTCODE = ? AND S_YEAR = ? AND "
        + "S_PAYTYPECODE = ? AND S_BUDGETTYPE = ?"
        ;


	/**
	*  ������ѯ һ������ѯ�Ĳ�������
	*/
	
	public static final int FIND_BATCH_SIZE = 150  / 7;
	



   /**
   * Create a new record in Database.
   */
  public void create(IDto _dto,  Connection conn) throws SQLException
  {
     TvAmtControlInfoDto dto = (TvAmtControlInfoDto)_dto ;
     String msgValid = dto.checkValid() ;
     if (msgValid != null)
     	throw new SQLException("�������"+msgValid) ;

     PreparedStatement ps = null;
     try
     {
         ps = conn.prepareStatement(SQL_INSERT);
          ps.setString(1, dto.getStrecode());

          ps.setString(2, dto.getSbankcode());

          ps.setString(3, dto.getSbudgetunitcode());

          ps.setString(4, dto.getSfuncbdgsbtcode());

          ps.setString(5, dto.getSyear());

          ps.setBigDecimal(6, dto.getNamt());

          ps.setString(7, dto.getSpaytypecode());

          ps.setString(8, dto.getSbudgettype());

          ps.setString(9, dto.getSremark());

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
       TvAmtControlInfoDto dto = (TvAmtControlInfoDto)_dto ;
       String msgValid = dto.checkValid() ;
       if (msgValid != null)
           throw new SQLException("�������"+msgValid) ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try
       {
           ps = conn.prepareStatement(SQL_INSERT_WITH_RESULT);
            ps.setString(1, dto.getStrecode());
            ps.setString(2, dto.getSbankcode());
            ps.setString(3, dto.getSbudgetunitcode());
            ps.setString(4, dto.getSfuncbdgsbtcode());
            ps.setString(5, dto.getSyear());
            ps.setBigDecimal(6, dto.getNamt());
            ps.setString(7, dto.getSpaytypecode());
            ps.setString(8, dto.getSbudgettype());
            ps.setString(9, dto.getSremark());
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
        TvAmtControlInfoDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (TvAmtControlInfoDto)_dtos[i] ; 
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
 	    	    dto  = (TvAmtControlInfoDto)_dtos[i] ; 
  
               ps.setString(1, dto.getStrecode());
  
               ps.setString(2, dto.getSbankcode());
  
               ps.setString(3, dto.getSbudgetunitcode());
  
               ps.setString(4, dto.getSfuncbdgsbtcode());
  
               ps.setString(5, dto.getSyear());
  
               ps.setBigDecimal(6, dto.getNamt());
  
               ps.setString(7, dto.getSpaytypecode());
  
               ps.setString(8, dto.getSbudgettype());
  
               ps.setString(9, dto.getSremark());
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
        	
       TvAmtControlInfoPK pk = (TvAmtControlInfoPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT);
           ps.setString(1, pk.getStrecode());

           ps.setString(2, pk.getSbankcode());

           ps.setString(3, pk.getSbudgetunitcode());

           ps.setString(4, pk.getSfuncbdgsbtcode());

           ps.setString(5, pk.getSyear());

           ps.setString(6, pk.getSpaytypecode());

           ps.setString(7, pk.getSbudgettype());

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
        	
       TvAmtControlInfoPK pk = (TvAmtControlInfoPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT_FOR_UPDATE);
           ps.setString(1, pk.getStrecode());

           ps.setString(2, pk.getSbankcode());

           ps.setString(3, pk.getSbudgetunitcode());

           ps.setString(4, pk.getSfuncbdgsbtcode());

           ps.setString(5, pk.getSyear());

           ps.setString(6, pk.getSpaytypecode());

           ps.setString(7, pk.getSbudgettype());

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
        TvAmtControlInfoPK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TvAmtControlInfoPK)_pks[i] ; 
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
                    pk  = (TvAmtControlInfoPK)(pks.get(i)) ; 
                   ps.setString((i-iBegin)*7+1, pk.getStrecode());

                   ps.setString((i-iBegin)*7+2, pk.getSbankcode());

                   ps.setString((i-iBegin)*7+3, pk.getSbudgetunitcode());

                   ps.setString((i-iBegin)*7+4, pk.getSfuncbdgsbtcode());

                   ps.setString((i-iBegin)*7+5, pk.getSyear());

                   ps.setString((i-iBegin)*7+6, pk.getSpaytypecode());

                   ps.setString((i-iBegin)*7+7, pk.getSbudgettype());

			
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
            TvAmtControlInfoDto[] dtos = new TvAmtControlInfoDto[0];
		    dtos = (TvAmtControlInfoDto[]) results.toArray(dtos) ;
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
             TvAmtControlInfoDto  dto = new  TvAmtControlInfoDto ();
             //S_TRECODE
             str = rs.getString("S_TRECODE");
             if (str == null)
                dto.setStrecode(null);
             else
                dto.setStrecode(str.trim());

             //S_BANKCODE
             str = rs.getString("S_BANKCODE");
             if (str == null)
                dto.setSbankcode(null);
             else
                dto.setSbankcode(str.trim());

             //S_BUDGETUNITCODE
             str = rs.getString("S_BUDGETUNITCODE");
             if (str == null)
                dto.setSbudgetunitcode(null);
             else
                dto.setSbudgetunitcode(str.trim());

             //S_FUNCBDGSBTCODE
             str = rs.getString("S_FUNCBDGSBTCODE");
             if (str == null)
                dto.setSfuncbdgsbtcode(null);
             else
                dto.setSfuncbdgsbtcode(str.trim());

             //S_YEAR
             str = rs.getString("S_YEAR");
             if (str == null)
                dto.setSyear(null);
             else
                dto.setSyear(str.trim());

             //N_AMT
           dto.setNamt(rs.getBigDecimal("N_AMT"));

             //S_PAYTYPECODE
             str = rs.getString("S_PAYTYPECODE");
             if (str == null)
                dto.setSpaytypecode(null);
             else
                dto.setSpaytypecode(str.trim());

             //S_BUDGETTYPE
             str = rs.getString("S_BUDGETTYPE");
             if (str == null)
                dto.setSbudgettype(null);
             else
                dto.setSbudgettype(str.trim());

             //S_REMARK
             str = rs.getString("S_REMARK");
             if (str == null)
                dto.setSremark(null);
             else
                dto.setSremark(str.trim());



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
        TvAmtControlInfoDto dto = (TvAmtControlInfoDto)_dto ;
        PreparedStatement ps = null;
        try {
            if(isLobSupport){
                ps = conn.prepareStatement(SQL_UPDATE_LOB);
            }
            else{
                ps = conn.prepareStatement(SQL_UPDATE);
            }
            int pos = 1;
            //N_AMT
            ps.setBigDecimal(pos, dto.getNamt());
            pos++;

            //S_REMARK
            ps.setString(pos, dto.getSremark());
            pos++;


           //S_TRECODE
           ps.setString(pos, dto.getStrecode());
           pos++;
           //S_BANKCODE
           ps.setString(pos, dto.getSbankcode());
           pos++;
           //S_BUDGETUNITCODE
           ps.setString(pos, dto.getSbudgetunitcode());
           pos++;
           //S_FUNCBDGSBTCODE
           ps.setString(pos, dto.getSfuncbdgsbtcode());
           pos++;
           //S_YEAR
           ps.setString(pos, dto.getSyear());
           pos++;
           //S_PAYTYPECODE
           ps.setString(pos, dto.getSpaytypecode());
           pos++;
           //S_BUDGETTYPE
           ps.setString(pos, dto.getSbudgettype());
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
     
     	 TvAmtControlInfoDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (TvAmtControlInfoDto)_dtos[i] ; 
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
                dto  = (TvAmtControlInfoDto)_dtos[i] ; 
                int pos = 1;
                //N_AMT
                 ps.setBigDecimal(pos, dto.getNamt());
                pos++;

                //S_REMARK
                 ps.setString(pos, dto.getSremark());
                pos++;


               //S_TRECODE
               ps.setString(pos, dto.getStrecode());
               pos++;
               //S_BANKCODE
               ps.setString(pos, dto.getSbankcode());
               pos++;
               //S_BUDGETUNITCODE
               ps.setString(pos, dto.getSbudgetunitcode());
               pos++;
               //S_FUNCBDGSBTCODE
               ps.setString(pos, dto.getSfuncbdgsbtcode());
               pos++;
               //S_YEAR
               ps.setString(pos, dto.getSyear());
               pos++;
               //S_PAYTYPECODE
               ps.setString(pos, dto.getSpaytypecode());
               pos++;
               //S_BUDGETTYPE
               ps.setString(pos, dto.getSbudgettype());
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
       TvAmtControlInfoPK pk = (TvAmtControlInfoPK)_pk ;


       PreparedStatement ps = null;
       try {
           ps = conn.prepareStatement(SQL_DELETE);
           ps.setString(1, pk.getStrecode());
           ps.setString(2, pk.getSbankcode());
           ps.setString(3, pk.getSbudgetunitcode());
           ps.setString(4, pk.getSfuncbdgsbtcode());
           ps.setString(5, pk.getSyear());
           ps.setString(6, pk.getSpaytypecode());
           ps.setString(7, pk.getSbudgettype());
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
        TvAmtControlInfoPK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TvAmtControlInfoPK)_pks[i] ; 
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
       			pk  = (TvAmtControlInfoPK)(pks.get(i)) ; 
                ps.setString(1, pk.getStrecode());
                ps.setString(2, pk.getSbankcode());
                ps.setString(3, pk.getSbudgetunitcode());
                ps.setString(4, pk.getSfuncbdgsbtcode());
                ps.setString(5, pk.getSyear());
                ps.setString(6, pk.getSpaytypecode());
                ps.setString(7, pk.getSbudgettype());
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
      		throw new SQLException("���ݿ����TV_AMT_CONTROL_INFOû�м���޸ĵ��ֶ�");
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
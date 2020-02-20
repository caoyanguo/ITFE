    



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

import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto ;
import com.cfcc.itfe.persistence.pk.TsBudgetsubjectPK ;


/**
 * <p>Title: DAO of table: TS_BUDGETSUBJECT</p>
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

public class TsBudgetsubjectDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO TS_BUDGETSUBJECT ("
          + "S_ORGCODE,S_SUBJECTCODE,S_SUBJECTNAME,S_SUBJECTCLASS,S_SUBJECTTYPE"
          + ",S_SUBJECTATTR,S_INOUTFLAG,S_WRITEFLAG,S_MOVEFLAG,S_BUDGETTYPE"
          + ",S_CLASSFLAG,S_DRAWBACKTYPE,I_MODICOUNT"
        + ") VALUES ("
        + "?,?,?,?,?,?,?,?,?,?,?,?,?)";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "TS_BUDGETSUBJECT.S_ORGCODE, TS_BUDGETSUBJECT.S_SUBJECTCODE, TS_BUDGETSUBJECT.S_SUBJECTNAME, TS_BUDGETSUBJECT.S_SUBJECTCLASS, TS_BUDGETSUBJECT.S_SUBJECTTYPE, "
        + "TS_BUDGETSUBJECT.S_SUBJECTATTR, TS_BUDGETSUBJECT.S_INOUTFLAG, TS_BUDGETSUBJECT.S_WRITEFLAG, TS_BUDGETSUBJECT.S_MOVEFLAG, TS_BUDGETSUBJECT.S_BUDGETTYPE, "
        + "TS_BUDGETSUBJECT.S_CLASSFLAG, TS_BUDGETSUBJECT.S_DRAWBACKTYPE, TS_BUDGETSUBJECT.I_MODICOUNT "
        + "FROM TS_BUDGETSUBJECT "
        +" WHERE " 
        + "S_ORGCODE = ? AND S_SUBJECTCODE = ? AND S_SUBJECTTYPE = ? AND S_BUDGETTYPE = ?"
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "TS_BUDGETSUBJECT.S_ORGCODE, TS_BUDGETSUBJECT.S_SUBJECTCODE, TS_BUDGETSUBJECT.S_SUBJECTNAME, TS_BUDGETSUBJECT.S_SUBJECTCLASS, TS_BUDGETSUBJECT.S_SUBJECTTYPE, "
        + "TS_BUDGETSUBJECT.S_SUBJECTATTR, TS_BUDGETSUBJECT.S_INOUTFLAG, TS_BUDGETSUBJECT.S_WRITEFLAG, TS_BUDGETSUBJECT.S_MOVEFLAG, TS_BUDGETSUBJECT.S_BUDGETTYPE, "
        + "TS_BUDGETSUBJECT.S_CLASSFLAG, TS_BUDGETSUBJECT.S_DRAWBACKTYPE, TS_BUDGETSUBJECT.I_MODICOUNT "
        + "FROM TS_BUDGETSUBJECT "
        +" WHERE " 
        + "S_ORGCODE = ? AND S_SUBJECTCODE = ? AND S_SUBJECTTYPE = ? AND S_BUDGETTYPE = ? FOR UPDATE"
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  TS_BUDGETSUBJECT.S_ORGCODE  , TS_BUDGETSUBJECT.S_SUBJECTCODE  , TS_BUDGETSUBJECT.S_SUBJECTNAME  , TS_BUDGETSUBJECT.S_SUBJECTCLASS  , TS_BUDGETSUBJECT.S_SUBJECTTYPE "
        + " , TS_BUDGETSUBJECT.S_SUBJECTATTR  , TS_BUDGETSUBJECT.S_INOUTFLAG  , TS_BUDGETSUBJECT.S_WRITEFLAG  , TS_BUDGETSUBJECT.S_MOVEFLAG  , TS_BUDGETSUBJECT.S_BUDGETTYPE "
        + " , TS_BUDGETSUBJECT.S_CLASSFLAG  , TS_BUDGETSUBJECT.S_DRAWBACKTYPE  , TS_BUDGETSUBJECT.I_MODICOUNT "
        + "FROM TS_BUDGETSUBJECT ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "TS_BUDGETSUBJECT.S_ORGCODE, TS_BUDGETSUBJECT.S_SUBJECTCODE, TS_BUDGETSUBJECT.S_SUBJECTNAME, TS_BUDGETSUBJECT.S_SUBJECTCLASS, TS_BUDGETSUBJECT.S_SUBJECTTYPE, "
        + "TS_BUDGETSUBJECT.S_SUBJECTATTR, TS_BUDGETSUBJECT.S_INOUTFLAG, TS_BUDGETSUBJECT.S_WRITEFLAG, TS_BUDGETSUBJECT.S_MOVEFLAG, TS_BUDGETSUBJECT.S_BUDGETTYPE, "
        + "TS_BUDGETSUBJECT.S_CLASSFLAG, TS_BUDGETSUBJECT.S_DRAWBACKTYPE, TS_BUDGETSUBJECT.I_MODICOUNT "
        + "FROM TS_BUDGETSUBJECT " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        + "S_ORGCODE = ? AND S_SUBJECTCODE = ? AND S_SUBJECTTYPE = ? AND S_BUDGETTYPE = ?)"
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE TS_BUDGETSUBJECT SET "
        + "S_SUBJECTNAME =?,S_SUBJECTCLASS =?,S_SUBJECTATTR =?,S_INOUTFLAG =?,S_WRITEFLAG =?, "
        + "S_MOVEFLAG =?,S_CLASSFLAG =?,S_DRAWBACKTYPE =?,I_MODICOUNT =? "
        + "WHERE "
        + "S_ORGCODE = ? AND S_SUBJECTCODE = ? AND S_SUBJECTTYPE = ? AND S_BUDGETTYPE = ?"
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE TS_BUDGETSUBJECT SET "
        + "S_SUBJECTNAME =?, S_SUBJECTCLASS =?, S_SUBJECTATTR =?, S_INOUTFLAG =?, S_WRITEFLAG =?,  "
        + "S_MOVEFLAG =?, S_CLASSFLAG =?, S_DRAWBACKTYPE =?, I_MODICOUNT =? "
        + "WHERE "
        + "S_ORGCODE = ? AND S_SUBJECTCODE = ? AND S_SUBJECTTYPE = ? AND S_BUDGETTYPE = ?"
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM TS_BUDGETSUBJECT " 
        + " WHERE "
        + "S_ORGCODE = ? AND S_SUBJECTCODE = ? AND S_SUBJECTTYPE = ? AND S_BUDGETTYPE = ?"
        ;


	/**
	*  ������ѯ һ������ѯ�Ĳ�������
	*/
	
	public static final int FIND_BATCH_SIZE = 150  / 4;
	



   /**
   * Create a new record in Database.
   */
  public void create(IDto _dto,  Connection conn) throws SQLException
  {
     TsBudgetsubjectDto dto = (TsBudgetsubjectDto)_dto ;
     String msgValid = dto.checkValid() ;
     if (msgValid != null)
     	throw new SQLException("�������"+msgValid) ;

     PreparedStatement ps = null;
     try
     {
         ps = conn.prepareStatement(SQL_INSERT);
          ps.setString(1, dto.getSorgcode());

          ps.setString(2, dto.getSsubjectcode());

          ps.setString(3, dto.getSsubjectname());

          ps.setString(4, dto.getSsubjectclass());

          ps.setString(5, dto.getSsubjecttype());

          ps.setString(6, dto.getSsubjectattr());

          ps.setString(7, dto.getSinoutflag());

          ps.setString(8, dto.getSwriteflag());

          ps.setString(9, dto.getSmoveflag());

          ps.setString(10, dto.getSbudgettype());

          ps.setString(11, dto.getSclassflag());

          ps.setString(12, dto.getSdrawbacktype());

          if (dto.getImodicount()==null)
            ps.setNull(13, java.sql.Types.INTEGER);
         else
            ps.setInt(13, dto.getImodicount().intValue());
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
       TsBudgetsubjectDto dto = (TsBudgetsubjectDto)_dto ;
       String msgValid = dto.checkValid() ;
       if (msgValid != null)
           throw new SQLException("�������"+msgValid) ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try
       {
           ps = conn.prepareStatement(SQL_INSERT_WITH_RESULT);
            ps.setString(1, dto.getSorgcode());
            ps.setString(2, dto.getSsubjectcode());
            ps.setString(3, dto.getSsubjectname());
            ps.setString(4, dto.getSsubjectclass());
            ps.setString(5, dto.getSsubjecttype());
            ps.setString(6, dto.getSsubjectattr());
            ps.setString(7, dto.getSinoutflag());
            ps.setString(8, dto.getSwriteflag());
            ps.setString(9, dto.getSmoveflag());
            ps.setString(10, dto.getSbudgettype());
            ps.setString(11, dto.getSclassflag());
            ps.setString(12, dto.getSdrawbacktype());
            if (dto.getImodicount()==null)
              ps.setNull(13, java.sql.Types.INTEGER);
           else
              ps.setInt(13, dto.getImodicount().intValue());
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
        TsBudgetsubjectDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (TsBudgetsubjectDto)_dtos[i] ; 
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
 	    	    dto  = (TsBudgetsubjectDto)_dtos[i] ; 
  
               ps.setString(1, dto.getSorgcode());
  
               ps.setString(2, dto.getSsubjectcode());
  
               ps.setString(3, dto.getSsubjectname());
  
               ps.setString(4, dto.getSsubjectclass());
  
               ps.setString(5, dto.getSsubjecttype());
  
               ps.setString(6, dto.getSsubjectattr());
  
               ps.setString(7, dto.getSinoutflag());
  
               ps.setString(8, dto.getSwriteflag());
  
               ps.setString(9, dto.getSmoveflag());
  
               ps.setString(10, dto.getSbudgettype());
  
               ps.setString(11, dto.getSclassflag());
  
               ps.setString(12, dto.getSdrawbacktype());
  
               if (dto.getImodicount()==null)
                  ps.setNull(13, java.sql.Types.INTEGER);
               else
                  ps.setInt(13, dto.getImodicount().intValue());
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
        	
       TsBudgetsubjectPK pk = (TsBudgetsubjectPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT);
           ps.setString(1, pk.getSorgcode());

           ps.setString(2, pk.getSsubjectcode());

           ps.setString(3, pk.getSsubjecttype());

           ps.setString(4, pk.getSbudgettype());

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
        	
       TsBudgetsubjectPK pk = (TsBudgetsubjectPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT_FOR_UPDATE);
           ps.setString(1, pk.getSorgcode());

           ps.setString(2, pk.getSsubjectcode());

           ps.setString(3, pk.getSsubjecttype());

           ps.setString(4, pk.getSbudgettype());

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
        TsBudgetsubjectPK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TsBudgetsubjectPK)_pks[i] ; 
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
                    pk  = (TsBudgetsubjectPK)(pks.get(i)) ; 
                   ps.setString((i-iBegin)*4+1, pk.getSorgcode());

                   ps.setString((i-iBegin)*4+2, pk.getSsubjectcode());

                   ps.setString((i-iBegin)*4+3, pk.getSsubjecttype());

                   ps.setString((i-iBegin)*4+4, pk.getSbudgettype());

			
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
            TsBudgetsubjectDto[] dtos = new TsBudgetsubjectDto[0];
		    dtos = (TsBudgetsubjectDto[]) results.toArray(dtos) ;
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
             TsBudgetsubjectDto  dto = new  TsBudgetsubjectDto ();
             //S_ORGCODE
             str = rs.getString("S_ORGCODE");
             if (str == null)
                dto.setSorgcode(null);
             else
                dto.setSorgcode(str.trim());

             //S_SUBJECTCODE
             str = rs.getString("S_SUBJECTCODE");
             if (str == null)
                dto.setSsubjectcode(null);
             else
                dto.setSsubjectcode(str.trim());

             //S_SUBJECTNAME
             str = rs.getString("S_SUBJECTNAME");
             if (str == null)
                dto.setSsubjectname(null);
             else
                dto.setSsubjectname(str.trim());

             //S_SUBJECTCLASS
             str = rs.getString("S_SUBJECTCLASS");
             if (str == null)
                dto.setSsubjectclass(null);
             else
                dto.setSsubjectclass(str.trim());

             //S_SUBJECTTYPE
             str = rs.getString("S_SUBJECTTYPE");
             if (str == null)
                dto.setSsubjecttype(null);
             else
                dto.setSsubjecttype(str.trim());

             //S_SUBJECTATTR
             str = rs.getString("S_SUBJECTATTR");
             if (str == null)
                dto.setSsubjectattr(null);
             else
                dto.setSsubjectattr(str.trim());

             //S_INOUTFLAG
             str = rs.getString("S_INOUTFLAG");
             if (str == null)
                dto.setSinoutflag(null);
             else
                dto.setSinoutflag(str.trim());

             //S_WRITEFLAG
             str = rs.getString("S_WRITEFLAG");
             if (str == null)
                dto.setSwriteflag(null);
             else
                dto.setSwriteflag(str.trim());

             //S_MOVEFLAG
             str = rs.getString("S_MOVEFLAG");
             if (str == null)
                dto.setSmoveflag(null);
             else
                dto.setSmoveflag(str.trim());

             //S_BUDGETTYPE
             str = rs.getString("S_BUDGETTYPE");
             if (str == null)
                dto.setSbudgettype(null);
             else
                dto.setSbudgettype(str.trim());

             //S_CLASSFLAG
             str = rs.getString("S_CLASSFLAG");
             if (str == null)
                dto.setSclassflag(null);
             else
                dto.setSclassflag(str.trim());

             //S_DRAWBACKTYPE
             str = rs.getString("S_DRAWBACKTYPE");
             if (str == null)
                dto.setSdrawbacktype(null);
             else
                dto.setSdrawbacktype(str.trim());

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
        TsBudgetsubjectDto dto = (TsBudgetsubjectDto)_dto ;
        PreparedStatement ps = null;
        try {
            if(isLobSupport){
                ps = conn.prepareStatement(SQL_UPDATE_LOB);
            }
            else{
                ps = conn.prepareStatement(SQL_UPDATE);
            }
            int pos = 1;
            //S_SUBJECTNAME
            ps.setString(pos, dto.getSsubjectname());
            pos++;

            //S_SUBJECTCLASS
            ps.setString(pos, dto.getSsubjectclass());
            pos++;

            //S_SUBJECTATTR
            ps.setString(pos, dto.getSsubjectattr());
            pos++;

            //S_INOUTFLAG
            ps.setString(pos, dto.getSinoutflag());
            pos++;

            //S_WRITEFLAG
            ps.setString(pos, dto.getSwriteflag());
            pos++;

            //S_MOVEFLAG
            ps.setString(pos, dto.getSmoveflag());
            pos++;

            //S_CLASSFLAG
            ps.setString(pos, dto.getSclassflag());
            pos++;

            //S_DRAWBACKTYPE
            ps.setString(pos, dto.getSdrawbacktype());
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
           //S_SUBJECTCODE
           ps.setString(pos, dto.getSsubjectcode());
           pos++;
           //S_SUBJECTTYPE
           ps.setString(pos, dto.getSsubjecttype());
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
     
     	 TsBudgetsubjectDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (TsBudgetsubjectDto)_dtos[i] ; 
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
                dto  = (TsBudgetsubjectDto)_dtos[i] ; 
                int pos = 1;
                //S_SUBJECTNAME
                 ps.setString(pos, dto.getSsubjectname());
                pos++;

                //S_SUBJECTCLASS
                 ps.setString(pos, dto.getSsubjectclass());
                pos++;

                //S_SUBJECTATTR
                 ps.setString(pos, dto.getSsubjectattr());
                pos++;

                //S_INOUTFLAG
                 ps.setString(pos, dto.getSinoutflag());
                pos++;

                //S_WRITEFLAG
                 ps.setString(pos, dto.getSwriteflag());
                pos++;

                //S_MOVEFLAG
                 ps.setString(pos, dto.getSmoveflag());
                pos++;

                //S_CLASSFLAG
                 ps.setString(pos, dto.getSclassflag());
                pos++;

                //S_DRAWBACKTYPE
                 ps.setString(pos, dto.getSdrawbacktype());
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
               //S_SUBJECTCODE
               ps.setString(pos, dto.getSsubjectcode());
               pos++;
               //S_SUBJECTTYPE
               ps.setString(pos, dto.getSsubjecttype());
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
       TsBudgetsubjectPK pk = (TsBudgetsubjectPK)_pk ;


       PreparedStatement ps = null;
       try {
           ps = conn.prepareStatement(SQL_DELETE);
           ps.setString(1, pk.getSorgcode());
           ps.setString(2, pk.getSsubjectcode());
           ps.setString(3, pk.getSsubjecttype());
           ps.setString(4, pk.getSbudgettype());
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
        TsBudgetsubjectPK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TsBudgetsubjectPK)_pks[i] ; 
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
       			pk  = (TsBudgetsubjectPK)(pks.get(i)) ; 
                ps.setString(1, pk.getSorgcode());
                ps.setString(2, pk.getSsubjectcode());
                ps.setString(3, pk.getSsubjecttype());
                ps.setString(4, pk.getSbudgettype());
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
                           		throw new SQLException("���ݿ����TS_BUDGETSUBJECTû�м���޸ĵ��ֶ�");
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
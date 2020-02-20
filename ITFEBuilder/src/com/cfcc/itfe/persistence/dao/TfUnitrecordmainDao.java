    



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

import com.cfcc.itfe.persistence.dto.TfUnitrecordmainDto ;
import com.cfcc.itfe.persistence.pk.TfUnitrecordmainPK ;


/**
 * <p>Title: DAO of table: TF_UNITRECORDMAIN</p>
 * <p>Description:单位清册5951主表 Data Access Object  </p>
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

public class TfUnitrecordmainDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO TF_UNITRECORDMAIN ("
          + "I_VOUSRLNO,S_ORGCODE,S_STATUS,S_DEMO,TS_SYSUPDATE"
          + ",S_ADMDIVCODE,S_STYEAR,S_VTCODE,S_VOUDATE,S_VOUCHERNO"
          + ",S_TRECODE,S_FINORGCODE,S_PAYBANKCODE,S_PAYBANKNAME,S_ALLFLAG"
          + ",S_HOLD1,S_HOLD2,S_EXT1,S_EXT2,S_EXT3"
        + ") VALUES ("
        + "?,?,?,?,CURRENT TIMESTAMP ,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "TF_UNITRECORDMAIN.I_VOUSRLNO, TF_UNITRECORDMAIN.S_ORGCODE, TF_UNITRECORDMAIN.S_STATUS, TF_UNITRECORDMAIN.S_DEMO, TF_UNITRECORDMAIN.TS_SYSUPDATE, "
        + "TF_UNITRECORDMAIN.S_ADMDIVCODE, TF_UNITRECORDMAIN.S_STYEAR, TF_UNITRECORDMAIN.S_VTCODE, TF_UNITRECORDMAIN.S_VOUDATE, TF_UNITRECORDMAIN.S_VOUCHERNO, "
        + "TF_UNITRECORDMAIN.S_TRECODE, TF_UNITRECORDMAIN.S_FINORGCODE, TF_UNITRECORDMAIN.S_PAYBANKCODE, TF_UNITRECORDMAIN.S_PAYBANKNAME, TF_UNITRECORDMAIN.S_ALLFLAG, "
        + "TF_UNITRECORDMAIN.S_HOLD1, TF_UNITRECORDMAIN.S_HOLD2, TF_UNITRECORDMAIN.S_EXT1, TF_UNITRECORDMAIN.S_EXT2, TF_UNITRECORDMAIN.S_EXT3 "
        + "FROM TF_UNITRECORDMAIN "
        +" WHERE " 
        + "I_VOUSRLNO = ?"
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "TF_UNITRECORDMAIN.I_VOUSRLNO, TF_UNITRECORDMAIN.S_ORGCODE, TF_UNITRECORDMAIN.S_STATUS, TF_UNITRECORDMAIN.S_DEMO, TF_UNITRECORDMAIN.TS_SYSUPDATE, "
        + "TF_UNITRECORDMAIN.S_ADMDIVCODE, TF_UNITRECORDMAIN.S_STYEAR, TF_UNITRECORDMAIN.S_VTCODE, TF_UNITRECORDMAIN.S_VOUDATE, TF_UNITRECORDMAIN.S_VOUCHERNO, "
        + "TF_UNITRECORDMAIN.S_TRECODE, TF_UNITRECORDMAIN.S_FINORGCODE, TF_UNITRECORDMAIN.S_PAYBANKCODE, TF_UNITRECORDMAIN.S_PAYBANKNAME, TF_UNITRECORDMAIN.S_ALLFLAG, "
        + "TF_UNITRECORDMAIN.S_HOLD1, TF_UNITRECORDMAIN.S_HOLD2, TF_UNITRECORDMAIN.S_EXT1, TF_UNITRECORDMAIN.S_EXT2, TF_UNITRECORDMAIN.S_EXT3 "
        + "FROM TF_UNITRECORDMAIN "
        +" WHERE " 
        + "I_VOUSRLNO = ? FOR UPDATE"
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  TF_UNITRECORDMAIN.I_VOUSRLNO  , TF_UNITRECORDMAIN.S_ORGCODE  , TF_UNITRECORDMAIN.S_STATUS  , TF_UNITRECORDMAIN.S_DEMO  , TF_UNITRECORDMAIN.TS_SYSUPDATE "
        + " , TF_UNITRECORDMAIN.S_ADMDIVCODE  , TF_UNITRECORDMAIN.S_STYEAR  , TF_UNITRECORDMAIN.S_VTCODE  , TF_UNITRECORDMAIN.S_VOUDATE  , TF_UNITRECORDMAIN.S_VOUCHERNO "
        + " , TF_UNITRECORDMAIN.S_TRECODE  , TF_UNITRECORDMAIN.S_FINORGCODE  , TF_UNITRECORDMAIN.S_PAYBANKCODE  , TF_UNITRECORDMAIN.S_PAYBANKNAME  , TF_UNITRECORDMAIN.S_ALLFLAG "
        + " , TF_UNITRECORDMAIN.S_HOLD1  , TF_UNITRECORDMAIN.S_HOLD2  , TF_UNITRECORDMAIN.S_EXT1  , TF_UNITRECORDMAIN.S_EXT2  , TF_UNITRECORDMAIN.S_EXT3 "
        + "FROM TF_UNITRECORDMAIN ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "TF_UNITRECORDMAIN.I_VOUSRLNO, TF_UNITRECORDMAIN.S_ORGCODE, TF_UNITRECORDMAIN.S_STATUS, TF_UNITRECORDMAIN.S_DEMO, TF_UNITRECORDMAIN.TS_SYSUPDATE, "
        + "TF_UNITRECORDMAIN.S_ADMDIVCODE, TF_UNITRECORDMAIN.S_STYEAR, TF_UNITRECORDMAIN.S_VTCODE, TF_UNITRECORDMAIN.S_VOUDATE, TF_UNITRECORDMAIN.S_VOUCHERNO, "
        + "TF_UNITRECORDMAIN.S_TRECODE, TF_UNITRECORDMAIN.S_FINORGCODE, TF_UNITRECORDMAIN.S_PAYBANKCODE, TF_UNITRECORDMAIN.S_PAYBANKNAME, TF_UNITRECORDMAIN.S_ALLFLAG, "
        + "TF_UNITRECORDMAIN.S_HOLD1, TF_UNITRECORDMAIN.S_HOLD2, TF_UNITRECORDMAIN.S_EXT1, TF_UNITRECORDMAIN.S_EXT2, TF_UNITRECORDMAIN.S_EXT3 "
        + "FROM TF_UNITRECORDMAIN " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        + "I_VOUSRLNO = ?)"
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE TF_UNITRECORDMAIN SET "
        + "S_ORGCODE =?,S_STATUS =?,S_DEMO =?,TS_SYSUPDATE =CURRENT TIMESTAMP,S_ADMDIVCODE =?, "
        + "S_STYEAR =?,S_VTCODE =?,S_VOUDATE =?,S_VOUCHERNO =?,S_TRECODE =?, "
        + "S_FINORGCODE =?,S_PAYBANKCODE =?,S_PAYBANKNAME =?,S_ALLFLAG =?,S_HOLD1 =?, "
        + "S_HOLD2 =?,S_EXT1 =?,S_EXT2 =?,S_EXT3 =? "
        + "WHERE "
        + "I_VOUSRLNO = ?"
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE TF_UNITRECORDMAIN SET "
        + "S_ORGCODE =?, S_STATUS =?, S_DEMO =?, TS_SYSUPDATE =CURRENT TIMESTAMP, S_ADMDIVCODE =?,  "
        + "S_STYEAR =?, S_VTCODE =?, S_VOUDATE =?, S_VOUCHERNO =?, S_TRECODE =?,  "
        + "S_FINORGCODE =?, S_PAYBANKCODE =?, S_PAYBANKNAME =?, S_ALLFLAG =?, S_HOLD1 =?,  "
        + "S_HOLD2 =?, S_EXT1 =?, S_EXT2 =?, S_EXT3 =? "
        + "WHERE "
        + "I_VOUSRLNO = ?"
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM TF_UNITRECORDMAIN " 
        + " WHERE "
        + "I_VOUSRLNO = ?"
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
     TfUnitrecordmainDto dto = (TfUnitrecordmainDto)_dto ;
     String msgValid = dto.checkValid() ;
     if (msgValid != null)
     	throw new SQLException("插入错误，"+msgValid) ;

     PreparedStatement ps = null;
     try
     {
         ps = conn.prepareStatement(SQL_INSERT);
          if (dto.getIvousrlno()==null)
            ps.setNull(1, java.sql.Types.BIGINT);
         else
            ps.setLong(1, dto.getIvousrlno().longValue());
          ps.setString(2, dto.getSorgcode());

          ps.setString(3, dto.getSstatus());

          ps.setString(4, dto.getSdemo());

           ps.setString(5, dto.getSadmdivcode());

          ps.setString(6, dto.getSstyear());

          ps.setString(7, dto.getSvtcode());

          ps.setString(8, dto.getSvoudate());

          ps.setString(9, dto.getSvoucherno());

          ps.setString(10, dto.getStrecode());

          ps.setString(11, dto.getSfinorgcode());

          ps.setString(12, dto.getSpaybankcode());

          ps.setString(13, dto.getSpaybankname());

          ps.setString(14, dto.getSallflag());

          ps.setString(15, dto.getShold1());

          ps.setString(16, dto.getShold2());

          ps.setString(17, dto.getSext1());

          ps.setString(18, dto.getSext2());

          ps.setString(19, dto.getSext3());

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
       TfUnitrecordmainDto dto = (TfUnitrecordmainDto)_dto ;
       String msgValid = dto.checkValid() ;
       if (msgValid != null)
           throw new SQLException("插入错误，"+msgValid) ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try
       {
           ps = conn.prepareStatement(SQL_INSERT_WITH_RESULT);
            if (dto.getIvousrlno()==null)
              ps.setNull(1, java.sql.Types.BIGINT);
           else
              ps.setLong(1, dto.getIvousrlno().longValue());
            ps.setString(2, dto.getSorgcode());
            ps.setString(3, dto.getSstatus());
            ps.setString(4, dto.getSdemo());
             ps.setString(5, dto.getSadmdivcode());
            ps.setString(6, dto.getSstyear());
            ps.setString(7, dto.getSvtcode());
            ps.setString(8, dto.getSvoudate());
            ps.setString(9, dto.getSvoucherno());
            ps.setString(10, dto.getStrecode());
            ps.setString(11, dto.getSfinorgcode());
            ps.setString(12, dto.getSpaybankcode());
            ps.setString(13, dto.getSpaybankname());
            ps.setString(14, dto.getSallflag());
            ps.setString(15, dto.getShold1());
            ps.setString(16, dto.getShold2());
            ps.setString(17, dto.getSext1());
            ps.setString(18, dto.getSext2());
            ps.setString(19, dto.getSext3());
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
        TfUnitrecordmainDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (TfUnitrecordmainDto)_dtos[i] ; 
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
 	    	    dto  = (TfUnitrecordmainDto)_dtos[i] ; 
  
               if (dto.getIvousrlno()==null)
                  ps.setNull(1, java.sql.Types.BIGINT);
               else
                  ps.setLong(1, dto.getIvousrlno().longValue());
  
               ps.setString(2, dto.getSorgcode());
  
               ps.setString(3, dto.getSstatus());
  
               ps.setString(4, dto.getSdemo());
   
               ps.setString(5, dto.getSadmdivcode());
  
               ps.setString(6, dto.getSstyear());
  
               ps.setString(7, dto.getSvtcode());
  
               ps.setString(8, dto.getSvoudate());
  
               ps.setString(9, dto.getSvoucherno());
  
               ps.setString(10, dto.getStrecode());
  
               ps.setString(11, dto.getSfinorgcode());
  
               ps.setString(12, dto.getSpaybankcode());
  
               ps.setString(13, dto.getSpaybankname());
  
               ps.setString(14, dto.getSallflag());
  
               ps.setString(15, dto.getShold1());
  
               ps.setString(16, dto.getShold2());
  
               ps.setString(17, dto.getSext1());
  
               ps.setString(18, dto.getSext2());
  
               ps.setString(19, dto.getSext3());
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
        	
       TfUnitrecordmainPK pk = (TfUnitrecordmainPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT);
           if (pk.getIvousrlno()==null)
               ps.setNull(1, java.sql.Types.BIGINT);
           else
               ps.setLong(1, pk.getIvousrlno().longValue());

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
        	
       TfUnitrecordmainPK pk = (TfUnitrecordmainPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT_FOR_UPDATE);
           if (pk.getIvousrlno()==null)
               ps.setNull(1, java.sql.Types.BIGINT);
           else
               ps.setLong(1, pk.getIvousrlno().longValue());

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
        TfUnitrecordmainPK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TfUnitrecordmainPK)_pks[i] ; 
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
                    pk  = (TfUnitrecordmainPK)(pks.get(i)) ; 
                   if (pk.getIvousrlno()==null)
                      ps.setNull((i-iBegin)*1+1, java.sql.Types.BIGINT);
                   else
                      ps.setLong((i-iBegin)*1+1, pk.getIvousrlno().longValue());

			
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
            TfUnitrecordmainDto[] dtos = new TfUnitrecordmainDto[0];
		    dtos = (TfUnitrecordmainDto[]) results.toArray(dtos) ;
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
             TfUnitrecordmainDto  dto = new  TfUnitrecordmainDto ();
             //I_VOUSRLNO
             str = rs.getString("I_VOUSRLNO");
             if(str!=null){
                dto.setIvousrlno(new Long(str));
             }

             //S_ORGCODE
             str = rs.getString("S_ORGCODE");
             if (str == null)
                dto.setSorgcode(null);
             else
                dto.setSorgcode(str.trim());

             //S_STATUS
             str = rs.getString("S_STATUS");
             if (str == null)
                dto.setSstatus(null);
             else
                dto.setSstatus(str.trim());

             //S_DEMO
             str = rs.getString("S_DEMO");
             if (str == null)
                dto.setSdemo(null);
             else
                dto.setSdemo(str.trim());

             //TS_SYSUPDATE
           dto.setTssysupdate(rs.getTimestamp("TS_SYSUPDATE"));

             //S_ADMDIVCODE
             str = rs.getString("S_ADMDIVCODE");
             if (str == null)
                dto.setSadmdivcode(null);
             else
                dto.setSadmdivcode(str.trim());

             //S_STYEAR
             str = rs.getString("S_STYEAR");
             if (str == null)
                dto.setSstyear(null);
             else
                dto.setSstyear(str.trim());

             //S_VTCODE
             str = rs.getString("S_VTCODE");
             if (str == null)
                dto.setSvtcode(null);
             else
                dto.setSvtcode(str.trim());

             //S_VOUDATE
             str = rs.getString("S_VOUDATE");
             if (str == null)
                dto.setSvoudate(null);
             else
                dto.setSvoudate(str.trim());

             //S_VOUCHERNO
             str = rs.getString("S_VOUCHERNO");
             if (str == null)
                dto.setSvoucherno(null);
             else
                dto.setSvoucherno(str.trim());

             //S_TRECODE
             str = rs.getString("S_TRECODE");
             if (str == null)
                dto.setStrecode(null);
             else
                dto.setStrecode(str.trim());

             //S_FINORGCODE
             str = rs.getString("S_FINORGCODE");
             if (str == null)
                dto.setSfinorgcode(null);
             else
                dto.setSfinorgcode(str.trim());

             //S_PAYBANKCODE
             str = rs.getString("S_PAYBANKCODE");
             if (str == null)
                dto.setSpaybankcode(null);
             else
                dto.setSpaybankcode(str.trim());

             //S_PAYBANKNAME
             str = rs.getString("S_PAYBANKNAME");
             if (str == null)
                dto.setSpaybankname(null);
             else
                dto.setSpaybankname(str.trim());

             //S_ALLFLAG
             str = rs.getString("S_ALLFLAG");
             if (str == null)
                dto.setSallflag(null);
             else
                dto.setSallflag(str.trim());

             //S_HOLD1
             str = rs.getString("S_HOLD1");
             if (str == null)
                dto.setShold1(null);
             else
                dto.setShold1(str.trim());

             //S_HOLD2
             str = rs.getString("S_HOLD2");
             if (str == null)
                dto.setShold2(null);
             else
                dto.setShold2(str.trim());

             //S_EXT1
             str = rs.getString("S_EXT1");
             if (str == null)
                dto.setSext1(null);
             else
                dto.setSext1(str.trim());

             //S_EXT2
             str = rs.getString("S_EXT2");
             if (str == null)
                dto.setSext2(null);
             else
                dto.setSext2(str.trim());

             //S_EXT3
             str = rs.getString("S_EXT3");
             if (str == null)
                dto.setSext3(null);
             else
                dto.setSext3(str.trim());



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
        TfUnitrecordmainDto dto = (TfUnitrecordmainDto)_dto ;
        PreparedStatement ps = null;
        try {
            if(isLobSupport){
                ps = conn.prepareStatement(SQL_UPDATE_LOB);
            }
            else{
                ps = conn.prepareStatement(SQL_UPDATE);
            }
            int pos = 1;
            //S_ORGCODE
            ps.setString(pos, dto.getSorgcode());
            pos++;

            //S_STATUS
            ps.setString(pos, dto.getSstatus());
            pos++;

            //S_DEMO
            ps.setString(pos, dto.getSdemo());
            pos++;

            //TS_SYSUPDATE
            //S_ADMDIVCODE
            ps.setString(pos, dto.getSadmdivcode());
            pos++;

            //S_STYEAR
            ps.setString(pos, dto.getSstyear());
            pos++;

            //S_VTCODE
            ps.setString(pos, dto.getSvtcode());
            pos++;

            //S_VOUDATE
            ps.setString(pos, dto.getSvoudate());
            pos++;

            //S_VOUCHERNO
            ps.setString(pos, dto.getSvoucherno());
            pos++;

            //S_TRECODE
            ps.setString(pos, dto.getStrecode());
            pos++;

            //S_FINORGCODE
            ps.setString(pos, dto.getSfinorgcode());
            pos++;

            //S_PAYBANKCODE
            ps.setString(pos, dto.getSpaybankcode());
            pos++;

            //S_PAYBANKNAME
            ps.setString(pos, dto.getSpaybankname());
            pos++;

            //S_ALLFLAG
            ps.setString(pos, dto.getSallflag());
            pos++;

            //S_HOLD1
            ps.setString(pos, dto.getShold1());
            pos++;

            //S_HOLD2
            ps.setString(pos, dto.getShold2());
            pos++;

            //S_EXT1
            ps.setString(pos, dto.getSext1());
            pos++;

            //S_EXT2
            ps.setString(pos, dto.getSext2());
            pos++;

            //S_EXT3
            ps.setString(pos, dto.getSext3());
            pos++;


           //I_VOUSRLNO
           ps.setLong(pos, dto.getIvousrlno().longValue());
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
     
     	 TfUnitrecordmainDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (TfUnitrecordmainDto)_dtos[i] ; 
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
                dto  = (TfUnitrecordmainDto)_dtos[i] ; 
                int pos = 1;
                //S_ORGCODE
                 ps.setString(pos, dto.getSorgcode());
                pos++;

                //S_STATUS
                 ps.setString(pos, dto.getSstatus());
                pos++;

                //S_DEMO
                 ps.setString(pos, dto.getSdemo());
                pos++;

                //TS_SYSUPDATE
                 //S_ADMDIVCODE
                 ps.setString(pos, dto.getSadmdivcode());
                pos++;

                //S_STYEAR
                 ps.setString(pos, dto.getSstyear());
                pos++;

                //S_VTCODE
                 ps.setString(pos, dto.getSvtcode());
                pos++;

                //S_VOUDATE
                 ps.setString(pos, dto.getSvoudate());
                pos++;

                //S_VOUCHERNO
                 ps.setString(pos, dto.getSvoucherno());
                pos++;

                //S_TRECODE
                 ps.setString(pos, dto.getStrecode());
                pos++;

                //S_FINORGCODE
                 ps.setString(pos, dto.getSfinorgcode());
                pos++;

                //S_PAYBANKCODE
                 ps.setString(pos, dto.getSpaybankcode());
                pos++;

                //S_PAYBANKNAME
                 ps.setString(pos, dto.getSpaybankname());
                pos++;

                //S_ALLFLAG
                 ps.setString(pos, dto.getSallflag());
                pos++;

                //S_HOLD1
                 ps.setString(pos, dto.getShold1());
                pos++;

                //S_HOLD2
                 ps.setString(pos, dto.getShold2());
                pos++;

                //S_EXT1
                 ps.setString(pos, dto.getSext1());
                pos++;

                //S_EXT2
                 ps.setString(pos, dto.getSext2());
                pos++;

                //S_EXT3
                 ps.setString(pos, dto.getSext3());
                pos++;


               //I_VOUSRLNO
               ps.setLong(pos, dto.getIvousrlno().longValue());
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
       TfUnitrecordmainPK pk = (TfUnitrecordmainPK)_pk ;


       PreparedStatement ps = null;
       try {
           ps = conn.prepareStatement(SQL_DELETE);
           ps.setLong(1, pk.getIvousrlno().longValue());
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
        TfUnitrecordmainPK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TfUnitrecordmainPK)_pks[i] ; 
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
       			pk  = (TfUnitrecordmainPK)(pks.get(i)) ; 
                ps.setLong(1, pk.getIvousrlno().longValue());
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
                                                         		throw new SQLException("数据库表：TF_UNITRECORDMAIN没有检查修改的字段");
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

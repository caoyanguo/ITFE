    



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

import com.cfcc.itfe.persistence.dto.TfInterestrateMsgDto ;
import com.cfcc.itfe.persistence.pk.TfInterestrateMsgPK ;


/**
 * <p>Title: DAO of table: TF_INTERESTRATE_MSG</p>
 * <p>Description:计息统计信息表 Data Access Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:58 </p>
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

public class TfInterestrateMsgDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO TF_INTERESTRATE_MSG ("
          + "I_VOUSRLNO,S_ORGCODE,S_YEAR,S_QUARTER,S_STARTDATE"
          + ",S_ENDDATE,S_BANKTYPE,N_INTERESTRATE_COUNT,N_INTEREST_RATES,N_INTEREST_VALUE"
          + ",S_UESRCODE,S_STATUS,S_DEMO,TS_SYSUPDATE,S_EXT1"
          + ",S_EXT2,S_EXT3"
        + ") VALUES ("
        + "?,?,?,?,?,?,?,?,?,?,?,?,?,CURRENT TIMESTAMP ,?,?,?)";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "TF_INTERESTRATE_MSG.I_VOUSRLNO, TF_INTERESTRATE_MSG.S_ORGCODE, TF_INTERESTRATE_MSG.S_YEAR, TF_INTERESTRATE_MSG.S_QUARTER, TF_INTERESTRATE_MSG.S_STARTDATE, "
        + "TF_INTERESTRATE_MSG.S_ENDDATE, TF_INTERESTRATE_MSG.S_BANKTYPE, TF_INTERESTRATE_MSG.N_INTERESTRATE_COUNT, TF_INTERESTRATE_MSG.N_INTEREST_RATES, TF_INTERESTRATE_MSG.N_INTEREST_VALUE, "
        + "TF_INTERESTRATE_MSG.S_UESRCODE, TF_INTERESTRATE_MSG.S_STATUS, TF_INTERESTRATE_MSG.S_DEMO, TF_INTERESTRATE_MSG.TS_SYSUPDATE, TF_INTERESTRATE_MSG.S_EXT1, "
        + "TF_INTERESTRATE_MSG.S_EXT2, TF_INTERESTRATE_MSG.S_EXT3 "
        + "FROM TF_INTERESTRATE_MSG "
        +" WHERE " 
        + "I_VOUSRLNO = ?"
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "TF_INTERESTRATE_MSG.I_VOUSRLNO, TF_INTERESTRATE_MSG.S_ORGCODE, TF_INTERESTRATE_MSG.S_YEAR, TF_INTERESTRATE_MSG.S_QUARTER, TF_INTERESTRATE_MSG.S_STARTDATE, "
        + "TF_INTERESTRATE_MSG.S_ENDDATE, TF_INTERESTRATE_MSG.S_BANKTYPE, TF_INTERESTRATE_MSG.N_INTERESTRATE_COUNT, TF_INTERESTRATE_MSG.N_INTEREST_RATES, TF_INTERESTRATE_MSG.N_INTEREST_VALUE, "
        + "TF_INTERESTRATE_MSG.S_UESRCODE, TF_INTERESTRATE_MSG.S_STATUS, TF_INTERESTRATE_MSG.S_DEMO, TF_INTERESTRATE_MSG.TS_SYSUPDATE, TF_INTERESTRATE_MSG.S_EXT1, "
        + "TF_INTERESTRATE_MSG.S_EXT2, TF_INTERESTRATE_MSG.S_EXT3 "
        + "FROM TF_INTERESTRATE_MSG "
        +" WHERE " 
        + "I_VOUSRLNO = ? FOR UPDATE"
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  TF_INTERESTRATE_MSG.I_VOUSRLNO  , TF_INTERESTRATE_MSG.S_ORGCODE  , TF_INTERESTRATE_MSG.S_YEAR  , TF_INTERESTRATE_MSG.S_QUARTER  , TF_INTERESTRATE_MSG.S_STARTDATE "
        + " , TF_INTERESTRATE_MSG.S_ENDDATE  , TF_INTERESTRATE_MSG.S_BANKTYPE  , TF_INTERESTRATE_MSG.N_INTERESTRATE_COUNT  , TF_INTERESTRATE_MSG.N_INTEREST_RATES  , TF_INTERESTRATE_MSG.N_INTEREST_VALUE "
        + " , TF_INTERESTRATE_MSG.S_UESRCODE  , TF_INTERESTRATE_MSG.S_STATUS  , TF_INTERESTRATE_MSG.S_DEMO  , TF_INTERESTRATE_MSG.TS_SYSUPDATE  , TF_INTERESTRATE_MSG.S_EXT1 "
        + " , TF_INTERESTRATE_MSG.S_EXT2  , TF_INTERESTRATE_MSG.S_EXT3 "
        + "FROM TF_INTERESTRATE_MSG ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "TF_INTERESTRATE_MSG.I_VOUSRLNO, TF_INTERESTRATE_MSG.S_ORGCODE, TF_INTERESTRATE_MSG.S_YEAR, TF_INTERESTRATE_MSG.S_QUARTER, TF_INTERESTRATE_MSG.S_STARTDATE, "
        + "TF_INTERESTRATE_MSG.S_ENDDATE, TF_INTERESTRATE_MSG.S_BANKTYPE, TF_INTERESTRATE_MSG.N_INTERESTRATE_COUNT, TF_INTERESTRATE_MSG.N_INTEREST_RATES, TF_INTERESTRATE_MSG.N_INTEREST_VALUE, "
        + "TF_INTERESTRATE_MSG.S_UESRCODE, TF_INTERESTRATE_MSG.S_STATUS, TF_INTERESTRATE_MSG.S_DEMO, TF_INTERESTRATE_MSG.TS_SYSUPDATE, TF_INTERESTRATE_MSG.S_EXT1, "
        + "TF_INTERESTRATE_MSG.S_EXT2, TF_INTERESTRATE_MSG.S_EXT3 "
        + "FROM TF_INTERESTRATE_MSG " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        + "I_VOUSRLNO = ?)"
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE TF_INTERESTRATE_MSG SET "
        + "S_ORGCODE =?,S_YEAR =?,S_QUARTER =?,S_STARTDATE =?,S_ENDDATE =?, "
        + "S_BANKTYPE =?,N_INTERESTRATE_COUNT =?,N_INTEREST_RATES =?,N_INTEREST_VALUE =?,S_UESRCODE =?, "
        + "S_STATUS =?,S_DEMO =?,TS_SYSUPDATE =CURRENT TIMESTAMP,S_EXT1 =?,S_EXT2 =?, "
        + "S_EXT3 =? "
        + "WHERE "
        + "I_VOUSRLNO = ?"
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE TF_INTERESTRATE_MSG SET "
        + "S_ORGCODE =?, S_YEAR =?, S_QUARTER =?, S_STARTDATE =?, S_ENDDATE =?,  "
        + "S_BANKTYPE =?, N_INTERESTRATE_COUNT =?, N_INTEREST_RATES =?, N_INTEREST_VALUE =?, S_UESRCODE =?,  "
        + "S_STATUS =?, S_DEMO =?, TS_SYSUPDATE =CURRENT TIMESTAMP, S_EXT1 =?, S_EXT2 =?,  "
        + "S_EXT3 =? "
        + "WHERE "
        + "I_VOUSRLNO = ?"
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM TF_INTERESTRATE_MSG " 
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
     TfInterestrateMsgDto dto = (TfInterestrateMsgDto)_dto ;
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

          ps.setString(3, dto.getSyear());

          ps.setString(4, dto.getSquarter());

          ps.setString(5, dto.getSstartdate());

          ps.setString(6, dto.getSenddate());

          ps.setString(7, dto.getSbanktype());

          ps.setBigDecimal(8, dto.getNinterestratecount());

          ps.setBigDecimal(9, dto.getNinterestrates());

          ps.setBigDecimal(10, dto.getNinterestvalue());

          ps.setString(11, dto.getSuesrcode());

          ps.setString(12, dto.getSstatus());

          ps.setString(13, dto.getSdemo());

           ps.setString(14, dto.getSext1());

          ps.setString(15, dto.getSext2());

          ps.setString(16, dto.getSext3());

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
       TfInterestrateMsgDto dto = (TfInterestrateMsgDto)_dto ;
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
            ps.setString(3, dto.getSyear());
            ps.setString(4, dto.getSquarter());
            ps.setString(5, dto.getSstartdate());
            ps.setString(6, dto.getSenddate());
            ps.setString(7, dto.getSbanktype());
            ps.setBigDecimal(8, dto.getNinterestratecount());
            ps.setBigDecimal(9, dto.getNinterestrates());
            ps.setBigDecimal(10, dto.getNinterestvalue());
            ps.setString(11, dto.getSuesrcode());
            ps.setString(12, dto.getSstatus());
            ps.setString(13, dto.getSdemo());
             ps.setString(14, dto.getSext1());
            ps.setString(15, dto.getSext2());
            ps.setString(16, dto.getSext3());
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
        TfInterestrateMsgDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (TfInterestrateMsgDto)_dtos[i] ; 
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
 	    	    dto  = (TfInterestrateMsgDto)_dtos[i] ; 
  
               if (dto.getIvousrlno()==null)
                  ps.setNull(1, java.sql.Types.BIGINT);
               else
                  ps.setLong(1, dto.getIvousrlno().longValue());
  
               ps.setString(2, dto.getSorgcode());
  
               ps.setString(3, dto.getSyear());
  
               ps.setString(4, dto.getSquarter());
  
               ps.setString(5, dto.getSstartdate());
  
               ps.setString(6, dto.getSenddate());
  
               ps.setString(7, dto.getSbanktype());
  
               ps.setBigDecimal(8, dto.getNinterestratecount());
  
               ps.setBigDecimal(9, dto.getNinterestrates());
  
               ps.setBigDecimal(10, dto.getNinterestvalue());
  
               ps.setString(11, dto.getSuesrcode());
  
               ps.setString(12, dto.getSstatus());
  
               ps.setString(13, dto.getSdemo());
   
               ps.setString(14, dto.getSext1());
  
               ps.setString(15, dto.getSext2());
  
               ps.setString(16, dto.getSext3());
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
        	
       TfInterestrateMsgPK pk = (TfInterestrateMsgPK)_pk ;

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
        	
       TfInterestrateMsgPK pk = (TfInterestrateMsgPK)_pk ;

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
        TfInterestrateMsgPK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TfInterestrateMsgPK)_pks[i] ; 
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
                    pk  = (TfInterestrateMsgPK)(pks.get(i)) ; 
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
            TfInterestrateMsgDto[] dtos = new TfInterestrateMsgDto[0];
		    dtos = (TfInterestrateMsgDto[]) results.toArray(dtos) ;
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
             TfInterestrateMsgDto  dto = new  TfInterestrateMsgDto ();
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

             //S_YEAR
             str = rs.getString("S_YEAR");
             if (str == null)
                dto.setSyear(null);
             else
                dto.setSyear(str.trim());

             //S_QUARTER
             str = rs.getString("S_QUARTER");
             if (str == null)
                dto.setSquarter(null);
             else
                dto.setSquarter(str.trim());

             //S_STARTDATE
             str = rs.getString("S_STARTDATE");
             if (str == null)
                dto.setSstartdate(null);
             else
                dto.setSstartdate(str.trim());

             //S_ENDDATE
             str = rs.getString("S_ENDDATE");
             if (str == null)
                dto.setSenddate(null);
             else
                dto.setSenddate(str.trim());

             //S_BANKTYPE
             str = rs.getString("S_BANKTYPE");
             if (str == null)
                dto.setSbanktype(null);
             else
                dto.setSbanktype(str.trim());

             //N_INTERESTRATE_COUNT
           dto.setNinterestratecount(rs.getBigDecimal("N_INTERESTRATE_COUNT"));

             //N_INTEREST_RATES
           dto.setNinterestrates(rs.getBigDecimal("N_INTEREST_RATES"));

             //N_INTEREST_VALUE
           dto.setNinterestvalue(rs.getBigDecimal("N_INTEREST_VALUE"));

             //S_UESRCODE
             str = rs.getString("S_UESRCODE");
             if (str == null)
                dto.setSuesrcode(null);
             else
                dto.setSuesrcode(str.trim());

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
        TfInterestrateMsgDto dto = (TfInterestrateMsgDto)_dto ;
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

            //S_YEAR
            ps.setString(pos, dto.getSyear());
            pos++;

            //S_QUARTER
            ps.setString(pos, dto.getSquarter());
            pos++;

            //S_STARTDATE
            ps.setString(pos, dto.getSstartdate());
            pos++;

            //S_ENDDATE
            ps.setString(pos, dto.getSenddate());
            pos++;

            //S_BANKTYPE
            ps.setString(pos, dto.getSbanktype());
            pos++;

            //N_INTERESTRATE_COUNT
            ps.setBigDecimal(pos, dto.getNinterestratecount());
            pos++;

            //N_INTEREST_RATES
            ps.setBigDecimal(pos, dto.getNinterestrates());
            pos++;

            //N_INTEREST_VALUE
            ps.setBigDecimal(pos, dto.getNinterestvalue());
            pos++;

            //S_UESRCODE
            ps.setString(pos, dto.getSuesrcode());
            pos++;

            //S_STATUS
            ps.setString(pos, dto.getSstatus());
            pos++;

            //S_DEMO
            ps.setString(pos, dto.getSdemo());
            pos++;

            //TS_SYSUPDATE
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
     
     	 TfInterestrateMsgDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (TfInterestrateMsgDto)_dtos[i] ; 
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
                dto  = (TfInterestrateMsgDto)_dtos[i] ; 
                int pos = 1;
                //S_ORGCODE
                 ps.setString(pos, dto.getSorgcode());
                pos++;

                //S_YEAR
                 ps.setString(pos, dto.getSyear());
                pos++;

                //S_QUARTER
                 ps.setString(pos, dto.getSquarter());
                pos++;

                //S_STARTDATE
                 ps.setString(pos, dto.getSstartdate());
                pos++;

                //S_ENDDATE
                 ps.setString(pos, dto.getSenddate());
                pos++;

                //S_BANKTYPE
                 ps.setString(pos, dto.getSbanktype());
                pos++;

                //N_INTERESTRATE_COUNT
                 ps.setBigDecimal(pos, dto.getNinterestratecount());
                pos++;

                //N_INTEREST_RATES
                 ps.setBigDecimal(pos, dto.getNinterestrates());
                pos++;

                //N_INTEREST_VALUE
                 ps.setBigDecimal(pos, dto.getNinterestvalue());
                pos++;

                //S_UESRCODE
                 ps.setString(pos, dto.getSuesrcode());
                pos++;

                //S_STATUS
                 ps.setString(pos, dto.getSstatus());
                pos++;

                //S_DEMO
                 ps.setString(pos, dto.getSdemo());
                pos++;

                //TS_SYSUPDATE
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
       TfInterestrateMsgPK pk = (TfInterestrateMsgPK)_pk ;


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
        TfInterestrateMsgPK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TfInterestrateMsgPK)_pks[i] ; 
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
       			pk  = (TfInterestrateMsgPK)(pks.get(i)) ; 
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
                                                		throw new SQLException("数据库表：TF_INTERESTRATE_MSG没有检查修改的字段");
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

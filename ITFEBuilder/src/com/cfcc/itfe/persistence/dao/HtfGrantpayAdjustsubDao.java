    



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

import com.cfcc.itfe.persistence.dto.HtfGrantpayAdjustsubDto ;
import com.cfcc.itfe.persistence.pk.HtfGrantpayAdjustsubPK ;


/**
 * <p>Title: DAO of table: HTF_GRANTPAY_ADJUSTSUB</p>
 * <p>Description: Data Access Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:28:54 </p>
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

public class HtfGrantpayAdjustsubDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO HTF_GRANTPAY_ADJUSTSUB ("
          + "I_VOUSRLNO,I_SEQNO,S_ID,S_VOUCHERNO,S_SUPDEPCODE"
          + ",S_SUPDEPNAME,S_EXPFUNCCODE,S_EXPFUNCNAME,N_PAYAMT,S_PAYSUMMARYNAME"
          + ",S_XDEALRESULT,S_XADDWORD,S_HOLD1,S_HOLD2,S_HOLD3"
          + ",S_HOLD4,S_EXT1,S_EXT2,S_EXT3"
        + ") VALUES ("
        + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "HTF_GRANTPAY_ADJUSTSUB.I_VOUSRLNO, HTF_GRANTPAY_ADJUSTSUB.I_SEQNO, HTF_GRANTPAY_ADJUSTSUB.S_ID, HTF_GRANTPAY_ADJUSTSUB.S_VOUCHERNO, HTF_GRANTPAY_ADJUSTSUB.S_SUPDEPCODE, "
        + "HTF_GRANTPAY_ADJUSTSUB.S_SUPDEPNAME, HTF_GRANTPAY_ADJUSTSUB.S_EXPFUNCCODE, HTF_GRANTPAY_ADJUSTSUB.S_EXPFUNCNAME, HTF_GRANTPAY_ADJUSTSUB.N_PAYAMT, HTF_GRANTPAY_ADJUSTSUB.S_PAYSUMMARYNAME, "
        + "HTF_GRANTPAY_ADJUSTSUB.S_XDEALRESULT, HTF_GRANTPAY_ADJUSTSUB.S_XADDWORD, HTF_GRANTPAY_ADJUSTSUB.S_HOLD1, HTF_GRANTPAY_ADJUSTSUB.S_HOLD2, HTF_GRANTPAY_ADJUSTSUB.S_HOLD3, "
        + "HTF_GRANTPAY_ADJUSTSUB.S_HOLD4, HTF_GRANTPAY_ADJUSTSUB.S_EXT1, HTF_GRANTPAY_ADJUSTSUB.S_EXT2, HTF_GRANTPAY_ADJUSTSUB.S_EXT3 "
        + "FROM HTF_GRANTPAY_ADJUSTSUB "
        +" WHERE " 
        + "I_VOUSRLNO = ? AND I_SEQNO = ?"
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "HTF_GRANTPAY_ADJUSTSUB.I_VOUSRLNO, HTF_GRANTPAY_ADJUSTSUB.I_SEQNO, HTF_GRANTPAY_ADJUSTSUB.S_ID, HTF_GRANTPAY_ADJUSTSUB.S_VOUCHERNO, HTF_GRANTPAY_ADJUSTSUB.S_SUPDEPCODE, "
        + "HTF_GRANTPAY_ADJUSTSUB.S_SUPDEPNAME, HTF_GRANTPAY_ADJUSTSUB.S_EXPFUNCCODE, HTF_GRANTPAY_ADJUSTSUB.S_EXPFUNCNAME, HTF_GRANTPAY_ADJUSTSUB.N_PAYAMT, HTF_GRANTPAY_ADJUSTSUB.S_PAYSUMMARYNAME, "
        + "HTF_GRANTPAY_ADJUSTSUB.S_XDEALRESULT, HTF_GRANTPAY_ADJUSTSUB.S_XADDWORD, HTF_GRANTPAY_ADJUSTSUB.S_HOLD1, HTF_GRANTPAY_ADJUSTSUB.S_HOLD2, HTF_GRANTPAY_ADJUSTSUB.S_HOLD3, "
        + "HTF_GRANTPAY_ADJUSTSUB.S_HOLD4, HTF_GRANTPAY_ADJUSTSUB.S_EXT1, HTF_GRANTPAY_ADJUSTSUB.S_EXT2, HTF_GRANTPAY_ADJUSTSUB.S_EXT3 "
        + "FROM HTF_GRANTPAY_ADJUSTSUB "
        +" WHERE " 
        + "I_VOUSRLNO = ? AND I_SEQNO = ? FOR UPDATE"
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  HTF_GRANTPAY_ADJUSTSUB.I_VOUSRLNO  , HTF_GRANTPAY_ADJUSTSUB.I_SEQNO  , HTF_GRANTPAY_ADJUSTSUB.S_ID  , HTF_GRANTPAY_ADJUSTSUB.S_VOUCHERNO  , HTF_GRANTPAY_ADJUSTSUB.S_SUPDEPCODE "
        + " , HTF_GRANTPAY_ADJUSTSUB.S_SUPDEPNAME  , HTF_GRANTPAY_ADJUSTSUB.S_EXPFUNCCODE  , HTF_GRANTPAY_ADJUSTSUB.S_EXPFUNCNAME  , HTF_GRANTPAY_ADJUSTSUB.N_PAYAMT  , HTF_GRANTPAY_ADJUSTSUB.S_PAYSUMMARYNAME "
        + " , HTF_GRANTPAY_ADJUSTSUB.S_XDEALRESULT  , HTF_GRANTPAY_ADJUSTSUB.S_XADDWORD  , HTF_GRANTPAY_ADJUSTSUB.S_HOLD1  , HTF_GRANTPAY_ADJUSTSUB.S_HOLD2  , HTF_GRANTPAY_ADJUSTSUB.S_HOLD3 "
        + " , HTF_GRANTPAY_ADJUSTSUB.S_HOLD4  , HTF_GRANTPAY_ADJUSTSUB.S_EXT1  , HTF_GRANTPAY_ADJUSTSUB.S_EXT2  , HTF_GRANTPAY_ADJUSTSUB.S_EXT3 "
        + "FROM HTF_GRANTPAY_ADJUSTSUB ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "HTF_GRANTPAY_ADJUSTSUB.I_VOUSRLNO, HTF_GRANTPAY_ADJUSTSUB.I_SEQNO, HTF_GRANTPAY_ADJUSTSUB.S_ID, HTF_GRANTPAY_ADJUSTSUB.S_VOUCHERNO, HTF_GRANTPAY_ADJUSTSUB.S_SUPDEPCODE, "
        + "HTF_GRANTPAY_ADJUSTSUB.S_SUPDEPNAME, HTF_GRANTPAY_ADJUSTSUB.S_EXPFUNCCODE, HTF_GRANTPAY_ADJUSTSUB.S_EXPFUNCNAME, HTF_GRANTPAY_ADJUSTSUB.N_PAYAMT, HTF_GRANTPAY_ADJUSTSUB.S_PAYSUMMARYNAME, "
        + "HTF_GRANTPAY_ADJUSTSUB.S_XDEALRESULT, HTF_GRANTPAY_ADJUSTSUB.S_XADDWORD, HTF_GRANTPAY_ADJUSTSUB.S_HOLD1, HTF_GRANTPAY_ADJUSTSUB.S_HOLD2, HTF_GRANTPAY_ADJUSTSUB.S_HOLD3, "
        + "HTF_GRANTPAY_ADJUSTSUB.S_HOLD4, HTF_GRANTPAY_ADJUSTSUB.S_EXT1, HTF_GRANTPAY_ADJUSTSUB.S_EXT2, HTF_GRANTPAY_ADJUSTSUB.S_EXT3 "
        + "FROM HTF_GRANTPAY_ADJUSTSUB " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        + "I_VOUSRLNO = ? AND I_SEQNO = ?)"
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE HTF_GRANTPAY_ADJUSTSUB SET "
        + "S_ID =?,S_VOUCHERNO =?,S_SUPDEPCODE =?,S_SUPDEPNAME =?,S_EXPFUNCCODE =?, "
        + "S_EXPFUNCNAME =?,N_PAYAMT =?,S_PAYSUMMARYNAME =?,S_XDEALRESULT =?,S_XADDWORD =?, "
        + "S_HOLD1 =?,S_HOLD2 =?,S_HOLD3 =?,S_HOLD4 =?,S_EXT1 =?, "
        + "S_EXT2 =?,S_EXT3 =? "
        + "WHERE "
        + "I_VOUSRLNO = ? AND I_SEQNO = ?"
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE HTF_GRANTPAY_ADJUSTSUB SET "
        + "S_ID =?, S_VOUCHERNO =?, S_SUPDEPCODE =?, S_SUPDEPNAME =?, S_EXPFUNCCODE =?,  "
        + "S_EXPFUNCNAME =?, N_PAYAMT =?, S_PAYSUMMARYNAME =?, S_XDEALRESULT =?, S_XADDWORD =?,  "
        + "S_HOLD1 =?, S_HOLD2 =?, S_HOLD3 =?, S_HOLD4 =?, S_EXT1 =?,  "
        + "S_EXT2 =?, S_EXT3 =? "
        + "WHERE "
        + "I_VOUSRLNO = ? AND I_SEQNO = ?"
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM HTF_GRANTPAY_ADJUSTSUB " 
        + " WHERE "
        + "I_VOUSRLNO = ? AND I_SEQNO = ?"
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
     HtfGrantpayAdjustsubDto dto = (HtfGrantpayAdjustsubDto)_dto ;
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
          if (dto.getIseqno()==null)
            ps.setNull(2, java.sql.Types.BIGINT);
         else
            ps.setLong(2, dto.getIseqno().longValue());
          ps.setString(3, dto.getSid());

          ps.setString(4, dto.getSvoucherno());

          ps.setString(5, dto.getSsupdepcode());

          ps.setString(6, dto.getSsupdepname());

          ps.setString(7, dto.getSexpfunccode());

          ps.setString(8, dto.getSexpfuncname());

          ps.setBigDecimal(9, dto.getNpayamt());

          ps.setString(10, dto.getSpaysummaryname());

          ps.setString(11, dto.getSxdealresult());

          ps.setString(12, dto.getSxaddword());

          ps.setString(13, dto.getShold1());

          ps.setString(14, dto.getShold2());

          ps.setString(15, dto.getShold3());

          ps.setString(16, dto.getShold4());

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
       HtfGrantpayAdjustsubDto dto = (HtfGrantpayAdjustsubDto)_dto ;
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
            if (dto.getIseqno()==null)
              ps.setNull(2, java.sql.Types.BIGINT);
           else
              ps.setLong(2, dto.getIseqno().longValue());
            ps.setString(3, dto.getSid());
            ps.setString(4, dto.getSvoucherno());
            ps.setString(5, dto.getSsupdepcode());
            ps.setString(6, dto.getSsupdepname());
            ps.setString(7, dto.getSexpfunccode());
            ps.setString(8, dto.getSexpfuncname());
            ps.setBigDecimal(9, dto.getNpayamt());
            ps.setString(10, dto.getSpaysummaryname());
            ps.setString(11, dto.getSxdealresult());
            ps.setString(12, dto.getSxaddword());
            ps.setString(13, dto.getShold1());
            ps.setString(14, dto.getShold2());
            ps.setString(15, dto.getShold3());
            ps.setString(16, dto.getShold4());
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
        HtfGrantpayAdjustsubDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (HtfGrantpayAdjustsubDto)_dtos[i] ; 
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
 	    	    dto  = (HtfGrantpayAdjustsubDto)_dtos[i] ; 
  
               if (dto.getIvousrlno()==null)
                  ps.setNull(1, java.sql.Types.BIGINT);
               else
                  ps.setLong(1, dto.getIvousrlno().longValue());
  
               if (dto.getIseqno()==null)
                  ps.setNull(2, java.sql.Types.BIGINT);
               else
                  ps.setLong(2, dto.getIseqno().longValue());
  
               ps.setString(3, dto.getSid());
  
               ps.setString(4, dto.getSvoucherno());
  
               ps.setString(5, dto.getSsupdepcode());
  
               ps.setString(6, dto.getSsupdepname());
  
               ps.setString(7, dto.getSexpfunccode());
  
               ps.setString(8, dto.getSexpfuncname());
  
               ps.setBigDecimal(9, dto.getNpayamt());
  
               ps.setString(10, dto.getSpaysummaryname());
  
               ps.setString(11, dto.getSxdealresult());
  
               ps.setString(12, dto.getSxaddword());
  
               ps.setString(13, dto.getShold1());
  
               ps.setString(14, dto.getShold2());
  
               ps.setString(15, dto.getShold3());
  
               ps.setString(16, dto.getShold4());
  
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
        	
       HtfGrantpayAdjustsubPK pk = (HtfGrantpayAdjustsubPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT);
           if (pk.getIvousrlno()==null)
               ps.setNull(1, java.sql.Types.BIGINT);
           else
               ps.setLong(1, pk.getIvousrlno().longValue());

           if (pk.getIseqno()==null)
               ps.setNull(2, java.sql.Types.BIGINT);
           else
               ps.setLong(2, pk.getIseqno().longValue());

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
        	
       HtfGrantpayAdjustsubPK pk = (HtfGrantpayAdjustsubPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT_FOR_UPDATE);
           if (pk.getIvousrlno()==null)
               ps.setNull(1, java.sql.Types.BIGINT);
           else
               ps.setLong(1, pk.getIvousrlno().longValue());

           if (pk.getIseqno()==null)
               ps.setNull(2, java.sql.Types.BIGINT);
           else
               ps.setLong(2, pk.getIseqno().longValue());

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
        HtfGrantpayAdjustsubPK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (HtfGrantpayAdjustsubPK)_pks[i] ; 
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
                    pk  = (HtfGrantpayAdjustsubPK)(pks.get(i)) ; 
                   if (pk.getIvousrlno()==null)
                      ps.setNull((i-iBegin)*2+1, java.sql.Types.BIGINT);
                   else
                      ps.setLong((i-iBegin)*2+1, pk.getIvousrlno().longValue());

                   if (pk.getIseqno()==null)
                      ps.setNull((i-iBegin)*2+2, java.sql.Types.BIGINT);
                   else
                      ps.setLong((i-iBegin)*2+2, pk.getIseqno().longValue());

			
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
            HtfGrantpayAdjustsubDto[] dtos = new HtfGrantpayAdjustsubDto[0];
		    dtos = (HtfGrantpayAdjustsubDto[]) results.toArray(dtos) ;
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
             HtfGrantpayAdjustsubDto  dto = new  HtfGrantpayAdjustsubDto ();
             //I_VOUSRLNO
             str = rs.getString("I_VOUSRLNO");
             if(str!=null){
                dto.setIvousrlno(new Long(str));
             }

             //I_SEQNO
             str = rs.getString("I_SEQNO");
             if(str!=null){
                dto.setIseqno(new Long(str));
             }

             //S_ID
             str = rs.getString("S_ID");
             if (str == null)
                dto.setSid(null);
             else
                dto.setSid(str.trim());

             //S_VOUCHERNO
             str = rs.getString("S_VOUCHERNO");
             if (str == null)
                dto.setSvoucherno(null);
             else
                dto.setSvoucherno(str.trim());

             //S_SUPDEPCODE
             str = rs.getString("S_SUPDEPCODE");
             if (str == null)
                dto.setSsupdepcode(null);
             else
                dto.setSsupdepcode(str.trim());

             //S_SUPDEPNAME
             str = rs.getString("S_SUPDEPNAME");
             if (str == null)
                dto.setSsupdepname(null);
             else
                dto.setSsupdepname(str.trim());

             //S_EXPFUNCCODE
             str = rs.getString("S_EXPFUNCCODE");
             if (str == null)
                dto.setSexpfunccode(null);
             else
                dto.setSexpfunccode(str.trim());

             //S_EXPFUNCNAME
             str = rs.getString("S_EXPFUNCNAME");
             if (str == null)
                dto.setSexpfuncname(null);
             else
                dto.setSexpfuncname(str.trim());

             //N_PAYAMT
           dto.setNpayamt(rs.getBigDecimal("N_PAYAMT"));

             //S_PAYSUMMARYNAME
             str = rs.getString("S_PAYSUMMARYNAME");
             if (str == null)
                dto.setSpaysummaryname(null);
             else
                dto.setSpaysummaryname(str.trim());

             //S_XDEALRESULT
             str = rs.getString("S_XDEALRESULT");
             if (str == null)
                dto.setSxdealresult(null);
             else
                dto.setSxdealresult(str.trim());

             //S_XADDWORD
             str = rs.getString("S_XADDWORD");
             if (str == null)
                dto.setSxaddword(null);
             else
                dto.setSxaddword(str.trim());

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

             //S_HOLD3
             str = rs.getString("S_HOLD3");
             if (str == null)
                dto.setShold3(null);
             else
                dto.setShold3(str.trim());

             //S_HOLD4
             str = rs.getString("S_HOLD4");
             if (str == null)
                dto.setShold4(null);
             else
                dto.setShold4(str.trim());

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
        HtfGrantpayAdjustsubDto dto = (HtfGrantpayAdjustsubDto)_dto ;
        PreparedStatement ps = null;
        try {
            if(isLobSupport){
                ps = conn.prepareStatement(SQL_UPDATE_LOB);
            }
            else{
                ps = conn.prepareStatement(SQL_UPDATE);
            }
            int pos = 1;
            //S_ID
            ps.setString(pos, dto.getSid());
            pos++;

            //S_VOUCHERNO
            ps.setString(pos, dto.getSvoucherno());
            pos++;

            //S_SUPDEPCODE
            ps.setString(pos, dto.getSsupdepcode());
            pos++;

            //S_SUPDEPNAME
            ps.setString(pos, dto.getSsupdepname());
            pos++;

            //S_EXPFUNCCODE
            ps.setString(pos, dto.getSexpfunccode());
            pos++;

            //S_EXPFUNCNAME
            ps.setString(pos, dto.getSexpfuncname());
            pos++;

            //N_PAYAMT
            ps.setBigDecimal(pos, dto.getNpayamt());
            pos++;

            //S_PAYSUMMARYNAME
            ps.setString(pos, dto.getSpaysummaryname());
            pos++;

            //S_XDEALRESULT
            ps.setString(pos, dto.getSxdealresult());
            pos++;

            //S_XADDWORD
            ps.setString(pos, dto.getSxaddword());
            pos++;

            //S_HOLD1
            ps.setString(pos, dto.getShold1());
            pos++;

            //S_HOLD2
            ps.setString(pos, dto.getShold2());
            pos++;

            //S_HOLD3
            ps.setString(pos, dto.getShold3());
            pos++;

            //S_HOLD4
            ps.setString(pos, dto.getShold4());
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
     
     	 HtfGrantpayAdjustsubDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (HtfGrantpayAdjustsubDto)_dtos[i] ; 
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
                dto  = (HtfGrantpayAdjustsubDto)_dtos[i] ; 
                int pos = 1;
                //S_ID
                 ps.setString(pos, dto.getSid());
                pos++;

                //S_VOUCHERNO
                 ps.setString(pos, dto.getSvoucherno());
                pos++;

                //S_SUPDEPCODE
                 ps.setString(pos, dto.getSsupdepcode());
                pos++;

                //S_SUPDEPNAME
                 ps.setString(pos, dto.getSsupdepname());
                pos++;

                //S_EXPFUNCCODE
                 ps.setString(pos, dto.getSexpfunccode());
                pos++;

                //S_EXPFUNCNAME
                 ps.setString(pos, dto.getSexpfuncname());
                pos++;

                //N_PAYAMT
                 ps.setBigDecimal(pos, dto.getNpayamt());
                pos++;

                //S_PAYSUMMARYNAME
                 ps.setString(pos, dto.getSpaysummaryname());
                pos++;

                //S_XDEALRESULT
                 ps.setString(pos, dto.getSxdealresult());
                pos++;

                //S_XADDWORD
                 ps.setString(pos, dto.getSxaddword());
                pos++;

                //S_HOLD1
                 ps.setString(pos, dto.getShold1());
                pos++;

                //S_HOLD2
                 ps.setString(pos, dto.getShold2());
                pos++;

                //S_HOLD3
                 ps.setString(pos, dto.getShold3());
                pos++;

                //S_HOLD4
                 ps.setString(pos, dto.getShold4());
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
       HtfGrantpayAdjustsubPK pk = (HtfGrantpayAdjustsubPK)_pk ;


       PreparedStatement ps = null;
       try {
           ps = conn.prepareStatement(SQL_DELETE);
           ps.setLong(1, pk.getIvousrlno().longValue());
           ps.setLong(2, pk.getIseqno().longValue());
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
        HtfGrantpayAdjustsubPK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (HtfGrantpayAdjustsubPK)_pks[i] ; 
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
       			pk  = (HtfGrantpayAdjustsubPK)(pks.get(i)) ; 
                ps.setLong(1, pk.getIvousrlno().longValue());
                ps.setLong(2, pk.getIseqno().longValue());
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
                                                   		throw new SQLException("数据库表：HTF_GRANTPAY_ADJUSTSUB没有检查修改的字段");
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

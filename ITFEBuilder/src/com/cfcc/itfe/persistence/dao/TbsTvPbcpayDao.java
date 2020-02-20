    



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

import com.cfcc.itfe.persistence.dto.TbsTvPbcpayDto ;
import com.cfcc.itfe.persistence.pk.TbsTvPbcpayPK ;


/**
 * <p>Title: DAO of table: TBS_TV_PBCPAY</p>
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

public class TbsTvPbcpayDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO TBS_TV_PBCPAY ("
          + "I_VOUSRLNO,S_TRECODE,S_PAYERACCT,S_PAYERNAME,S_PAYEROPNBNKNO"
          + ",S_PAYEEACCT,S_PAYEENAME,S_PAYEEOPNBNKNO,C_BDGKIND,S_FUNCSBTCODE"
          + ",S_ECOSBTCODE,S_BDGORGCODE,S_BDGORGNAME,S_VOUNO,D_VOUCHER"
          + ",F_AMT,S_BILLORG,S_BIZTYPE,D_ACCEPT,D_ACCT"
          + ",S_BACKFLAG,S_ADDWORD,S_PACKAGENO,S_STATUS,C_TRIMFLAG"
          + ",S_FILENAME,S_BOOKORGCODE,TS_SYSUPDATE"
        + ") VALUES ("
        + "DEFAULT ,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,CURRENT TIMESTAMP )";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "TBS_TV_PBCPAY.I_VOUSRLNO, TBS_TV_PBCPAY.S_TRECODE, TBS_TV_PBCPAY.S_PAYERACCT, TBS_TV_PBCPAY.S_PAYERNAME, TBS_TV_PBCPAY.S_PAYEROPNBNKNO, "
        + "TBS_TV_PBCPAY.S_PAYEEACCT, TBS_TV_PBCPAY.S_PAYEENAME, TBS_TV_PBCPAY.S_PAYEEOPNBNKNO, TBS_TV_PBCPAY.C_BDGKIND, TBS_TV_PBCPAY.S_FUNCSBTCODE, "
        + "TBS_TV_PBCPAY.S_ECOSBTCODE, TBS_TV_PBCPAY.S_BDGORGCODE, TBS_TV_PBCPAY.S_BDGORGNAME, TBS_TV_PBCPAY.S_VOUNO, TBS_TV_PBCPAY.D_VOUCHER, "
        + "TBS_TV_PBCPAY.F_AMT, TBS_TV_PBCPAY.S_BILLORG, TBS_TV_PBCPAY.S_BIZTYPE, TBS_TV_PBCPAY.D_ACCEPT, TBS_TV_PBCPAY.D_ACCT, "
        + "TBS_TV_PBCPAY.S_BACKFLAG, TBS_TV_PBCPAY.S_ADDWORD, TBS_TV_PBCPAY.S_PACKAGENO, TBS_TV_PBCPAY.S_STATUS, TBS_TV_PBCPAY.C_TRIMFLAG, "
        + "TBS_TV_PBCPAY.S_FILENAME, TBS_TV_PBCPAY.S_BOOKORGCODE, TBS_TV_PBCPAY.TS_SYSUPDATE "
        + "FROM TBS_TV_PBCPAY "
        +" WHERE " 
        + "I_VOUSRLNO = ?"
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "TBS_TV_PBCPAY.I_VOUSRLNO, TBS_TV_PBCPAY.S_TRECODE, TBS_TV_PBCPAY.S_PAYERACCT, TBS_TV_PBCPAY.S_PAYERNAME, TBS_TV_PBCPAY.S_PAYEROPNBNKNO, "
        + "TBS_TV_PBCPAY.S_PAYEEACCT, TBS_TV_PBCPAY.S_PAYEENAME, TBS_TV_PBCPAY.S_PAYEEOPNBNKNO, TBS_TV_PBCPAY.C_BDGKIND, TBS_TV_PBCPAY.S_FUNCSBTCODE, "
        + "TBS_TV_PBCPAY.S_ECOSBTCODE, TBS_TV_PBCPAY.S_BDGORGCODE, TBS_TV_PBCPAY.S_BDGORGNAME, TBS_TV_PBCPAY.S_VOUNO, TBS_TV_PBCPAY.D_VOUCHER, "
        + "TBS_TV_PBCPAY.F_AMT, TBS_TV_PBCPAY.S_BILLORG, TBS_TV_PBCPAY.S_BIZTYPE, TBS_TV_PBCPAY.D_ACCEPT, TBS_TV_PBCPAY.D_ACCT, "
        + "TBS_TV_PBCPAY.S_BACKFLAG, TBS_TV_PBCPAY.S_ADDWORD, TBS_TV_PBCPAY.S_PACKAGENO, TBS_TV_PBCPAY.S_STATUS, TBS_TV_PBCPAY.C_TRIMFLAG, "
        + "TBS_TV_PBCPAY.S_FILENAME, TBS_TV_PBCPAY.S_BOOKORGCODE, TBS_TV_PBCPAY.TS_SYSUPDATE "
        + "FROM TBS_TV_PBCPAY "
        +" WHERE " 
        + "I_VOUSRLNO = ? FOR UPDATE"
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  TBS_TV_PBCPAY.I_VOUSRLNO  , TBS_TV_PBCPAY.S_TRECODE  , TBS_TV_PBCPAY.S_PAYERACCT  , TBS_TV_PBCPAY.S_PAYERNAME  , TBS_TV_PBCPAY.S_PAYEROPNBNKNO "
        + " , TBS_TV_PBCPAY.S_PAYEEACCT  , TBS_TV_PBCPAY.S_PAYEENAME  , TBS_TV_PBCPAY.S_PAYEEOPNBNKNO  , TBS_TV_PBCPAY.C_BDGKIND  , TBS_TV_PBCPAY.S_FUNCSBTCODE "
        + " , TBS_TV_PBCPAY.S_ECOSBTCODE  , TBS_TV_PBCPAY.S_BDGORGCODE  , TBS_TV_PBCPAY.S_BDGORGNAME  , TBS_TV_PBCPAY.S_VOUNO  , TBS_TV_PBCPAY.D_VOUCHER "
        + " , TBS_TV_PBCPAY.F_AMT  , TBS_TV_PBCPAY.S_BILLORG  , TBS_TV_PBCPAY.S_BIZTYPE  , TBS_TV_PBCPAY.D_ACCEPT  , TBS_TV_PBCPAY.D_ACCT "
        + " , TBS_TV_PBCPAY.S_BACKFLAG  , TBS_TV_PBCPAY.S_ADDWORD  , TBS_TV_PBCPAY.S_PACKAGENO  , TBS_TV_PBCPAY.S_STATUS  , TBS_TV_PBCPAY.C_TRIMFLAG "
        + " , TBS_TV_PBCPAY.S_FILENAME  , TBS_TV_PBCPAY.S_BOOKORGCODE  , TBS_TV_PBCPAY.TS_SYSUPDATE "
        + "FROM TBS_TV_PBCPAY ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "TBS_TV_PBCPAY.I_VOUSRLNO, TBS_TV_PBCPAY.S_TRECODE, TBS_TV_PBCPAY.S_PAYERACCT, TBS_TV_PBCPAY.S_PAYERNAME, TBS_TV_PBCPAY.S_PAYEROPNBNKNO, "
        + "TBS_TV_PBCPAY.S_PAYEEACCT, TBS_TV_PBCPAY.S_PAYEENAME, TBS_TV_PBCPAY.S_PAYEEOPNBNKNO, TBS_TV_PBCPAY.C_BDGKIND, TBS_TV_PBCPAY.S_FUNCSBTCODE, "
        + "TBS_TV_PBCPAY.S_ECOSBTCODE, TBS_TV_PBCPAY.S_BDGORGCODE, TBS_TV_PBCPAY.S_BDGORGNAME, TBS_TV_PBCPAY.S_VOUNO, TBS_TV_PBCPAY.D_VOUCHER, "
        + "TBS_TV_PBCPAY.F_AMT, TBS_TV_PBCPAY.S_BILLORG, TBS_TV_PBCPAY.S_BIZTYPE, TBS_TV_PBCPAY.D_ACCEPT, TBS_TV_PBCPAY.D_ACCT, "
        + "TBS_TV_PBCPAY.S_BACKFLAG, TBS_TV_PBCPAY.S_ADDWORD, TBS_TV_PBCPAY.S_PACKAGENO, TBS_TV_PBCPAY.S_STATUS, TBS_TV_PBCPAY.C_TRIMFLAG, "
        + "TBS_TV_PBCPAY.S_FILENAME, TBS_TV_PBCPAY.S_BOOKORGCODE, TBS_TV_PBCPAY.TS_SYSUPDATE "
        + "FROM TBS_TV_PBCPAY " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        + "I_VOUSRLNO = ?)"
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE TBS_TV_PBCPAY SET "
        + "S_TRECODE =?,S_PAYERACCT =?,S_PAYERNAME =?,S_PAYEROPNBNKNO =?,S_PAYEEACCT =?, "
        + "S_PAYEENAME =?,S_PAYEEOPNBNKNO =?,C_BDGKIND =?,S_FUNCSBTCODE =?,S_ECOSBTCODE =?, "
        + "S_BDGORGCODE =?,S_BDGORGNAME =?,S_VOUNO =?,D_VOUCHER =?,F_AMT =?, "
        + "S_BILLORG =?,S_BIZTYPE =?,D_ACCEPT =?,D_ACCT =?,S_BACKFLAG =?, "
        + "S_ADDWORD =?,S_PACKAGENO =?,S_STATUS =?,C_TRIMFLAG =?,S_FILENAME =?, "
        + "S_BOOKORGCODE =?,TS_SYSUPDATE =CURRENT TIMESTAMP "
        + "WHERE "
        + "I_VOUSRLNO = ?"
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE TBS_TV_PBCPAY SET "
        + "S_TRECODE =?, S_PAYERACCT =?, S_PAYERNAME =?, S_PAYEROPNBNKNO =?, S_PAYEEACCT =?,  "
        + "S_PAYEENAME =?, S_PAYEEOPNBNKNO =?, C_BDGKIND =?, S_FUNCSBTCODE =?, S_ECOSBTCODE =?,  "
        + "S_BDGORGCODE =?, S_BDGORGNAME =?, S_VOUNO =?, D_VOUCHER =?, F_AMT =?,  "
        + "S_BILLORG =?, S_BIZTYPE =?, D_ACCEPT =?, D_ACCT =?, S_BACKFLAG =?,  "
        + "S_ADDWORD =?, S_PACKAGENO =?, S_STATUS =?, C_TRIMFLAG =?, S_FILENAME =?,  "
        + "S_BOOKORGCODE =?, TS_SYSUPDATE =CURRENT TIMESTAMP "
        + "WHERE "
        + "I_VOUSRLNO = ?"
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM TBS_TV_PBCPAY " 
        + " WHERE "
        + "I_VOUSRLNO = ?"
        ;


	/**
	*  ������ѯ һ������ѯ�Ĳ�������
	*/
	
	public static final int FIND_BATCH_SIZE = 150  / 1;
	



   /**
   * Create a new record in Database.
   */
  public void create(IDto _dto,  Connection conn) throws SQLException
  {
     TbsTvPbcpayDto dto = (TbsTvPbcpayDto)_dto ;
     String msgValid = dto.checkValid() ;
     if (msgValid != null)
     	throw new SQLException("�������"+msgValid) ;

     PreparedStatement ps = null;
     try
     {
         ps = conn.prepareStatement(SQL_INSERT);
           ps.setString(1, dto.getStrecode());

          ps.setString(2, dto.getSpayeracct());

          ps.setString(3, dto.getSpayername());

          ps.setString(4, dto.getSpayeropnbnkno());

          ps.setString(5, dto.getSpayeeacct());

          ps.setString(6, dto.getSpayeename());

          ps.setString(7, dto.getSpayeeopnbnkno());

          ps.setString(8, dto.getCbdgkind());

          ps.setString(9, dto.getSfuncsbtcode());

          ps.setString(10, dto.getSecosbtcode());

          ps.setString(11, dto.getSbdgorgcode());

          ps.setString(12, dto.getSbdgorgname());

          ps.setString(13, dto.getSvouno());

          ps.setDate(14, dto.getDvoucher());

          ps.setBigDecimal(15, dto.getFamt());

          ps.setString(16, dto.getSbillorg());

          ps.setString(17, dto.getSbiztype());

          ps.setDate(18, dto.getDaccept());

          ps.setDate(19, dto.getDacct());

          ps.setString(20, dto.getSbackflag());

          ps.setString(21, dto.getSaddword());

          ps.setString(22, dto.getSpackageno());

          ps.setString(23, dto.getSstatus());

          ps.setString(24, dto.getCtrimflag());

          ps.setString(25, dto.getSfilename());

          ps.setString(26, dto.getSbookorgcode());

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
       TbsTvPbcpayDto dto = (TbsTvPbcpayDto)_dto ;
       String msgValid = dto.checkValid() ;
       if (msgValid != null)
           throw new SQLException("�������"+msgValid) ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try
       {
           ps = conn.prepareStatement(SQL_INSERT_WITH_RESULT);
             ps.setString(1, dto.getStrecode());
            ps.setString(2, dto.getSpayeracct());
            ps.setString(3, dto.getSpayername());
            ps.setString(4, dto.getSpayeropnbnkno());
            ps.setString(5, dto.getSpayeeacct());
            ps.setString(6, dto.getSpayeename());
            ps.setString(7, dto.getSpayeeopnbnkno());
            ps.setString(8, dto.getCbdgkind());
            ps.setString(9, dto.getSfuncsbtcode());
            ps.setString(10, dto.getSecosbtcode());
            ps.setString(11, dto.getSbdgorgcode());
            ps.setString(12, dto.getSbdgorgname());
            ps.setString(13, dto.getSvouno());
            ps.setDate(14, dto.getDvoucher());
            ps.setBigDecimal(15, dto.getFamt());
            ps.setString(16, dto.getSbillorg());
            ps.setString(17, dto.getSbiztype());
            ps.setDate(18, dto.getDaccept());
            ps.setDate(19, dto.getDacct());
            ps.setString(20, dto.getSbackflag());
            ps.setString(21, dto.getSaddword());
            ps.setString(22, dto.getSpackageno());
            ps.setString(23, dto.getSstatus());
            ps.setString(24, dto.getCtrimflag());
            ps.setString(25, dto.getSfilename());
            ps.setString(26, dto.getSbookorgcode());
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
        TbsTvPbcpayDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (TbsTvPbcpayDto)_dtos[i] ; 
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
 	    	    dto  = (TbsTvPbcpayDto)_dtos[i] ; 
   
               ps.setString(1, dto.getStrecode());
  
               ps.setString(2, dto.getSpayeracct());
  
               ps.setString(3, dto.getSpayername());
  
               ps.setString(4, dto.getSpayeropnbnkno());
  
               ps.setString(5, dto.getSpayeeacct());
  
               ps.setString(6, dto.getSpayeename());
  
               ps.setString(7, dto.getSpayeeopnbnkno());
  
               ps.setString(8, dto.getCbdgkind());
  
               ps.setString(9, dto.getSfuncsbtcode());
  
               ps.setString(10, dto.getSecosbtcode());
  
               ps.setString(11, dto.getSbdgorgcode());
  
               ps.setString(12, dto.getSbdgorgname());
  
               ps.setString(13, dto.getSvouno());
  
               ps.setDate(14, dto.getDvoucher());
  
               ps.setBigDecimal(15, dto.getFamt());
  
               ps.setString(16, dto.getSbillorg());
  
               ps.setString(17, dto.getSbiztype());
  
               ps.setDate(18, dto.getDaccept());
  
               ps.setDate(19, dto.getDacct());
  
               ps.setString(20, dto.getSbackflag());
  
               ps.setString(21, dto.getSaddword());
  
               ps.setString(22, dto.getSpackageno());
  
               ps.setString(23, dto.getSstatus());
  
               ps.setString(24, dto.getCtrimflag());
  
               ps.setString(25, dto.getSfilename());
  
               ps.setString(26, dto.getSbookorgcode());
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
        	
       TbsTvPbcpayPK pk = (TbsTvPbcpayPK)_pk ;

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
        //	throw new SQLException("���Ҵ���"+msgValid) ;
       }
        	
       TbsTvPbcpayPK pk = (TbsTvPbcpayPK)_pk ;

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
        TbsTvPbcpayPK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TbsTvPbcpayPK)_pks[i] ; 
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
                    pk  = (TbsTvPbcpayPK)(pks.get(i)) ; 
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
            TbsTvPbcpayDto[] dtos = new TbsTvPbcpayDto[0];
		    dtos = (TbsTvPbcpayDto[]) results.toArray(dtos) ;
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
             TbsTvPbcpayDto  dto = new  TbsTvPbcpayDto ();
             //I_VOUSRLNO
             str = rs.getString("I_VOUSRLNO");
             if(str!=null){
                dto.setIvousrlno(new Long(str));
             }

             //S_TRECODE
             str = rs.getString("S_TRECODE");
             if (str == null)
                dto.setStrecode(null);
             else
                dto.setStrecode(str.trim());

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

             //S_PAYEROPNBNKNO
             str = rs.getString("S_PAYEROPNBNKNO");
             if (str == null)
                dto.setSpayeropnbnkno(null);
             else
                dto.setSpayeropnbnkno(str.trim());

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

             //S_PAYEEOPNBNKNO
             str = rs.getString("S_PAYEEOPNBNKNO");
             if (str == null)
                dto.setSpayeeopnbnkno(null);
             else
                dto.setSpayeeopnbnkno(str.trim());

             //C_BDGKIND
             str = rs.getString("C_BDGKIND");
             if (str == null)
                dto.setCbdgkind(null);
             else
                dto.setCbdgkind(str.trim());

             //S_FUNCSBTCODE
             str = rs.getString("S_FUNCSBTCODE");
             if (str == null)
                dto.setSfuncsbtcode(null);
             else
                dto.setSfuncsbtcode(str.trim());

             //S_ECOSBTCODE
             str = rs.getString("S_ECOSBTCODE");
             if (str == null)
                dto.setSecosbtcode(null);
             else
                dto.setSecosbtcode(str.trim());

             //S_BDGORGCODE
             str = rs.getString("S_BDGORGCODE");
             if (str == null)
                dto.setSbdgorgcode(null);
             else
                dto.setSbdgorgcode(str.trim());

             //S_BDGORGNAME
             str = rs.getString("S_BDGORGNAME");
             if (str == null)
                dto.setSbdgorgname(null);
             else
                dto.setSbdgorgname(str.trim());

             //S_VOUNO
             str = rs.getString("S_VOUNO");
             if (str == null)
                dto.setSvouno(null);
             else
                dto.setSvouno(str.trim());

             //D_VOUCHER
           dto.setDvoucher(rs.getDate("D_VOUCHER"));

             //F_AMT
           dto.setFamt(rs.getBigDecimal("F_AMT"));

             //S_BILLORG
             str = rs.getString("S_BILLORG");
             if (str == null)
                dto.setSbillorg(null);
             else
                dto.setSbillorg(str.trim());

             //S_BIZTYPE
             str = rs.getString("S_BIZTYPE");
             if (str == null)
                dto.setSbiztype(null);
             else
                dto.setSbiztype(str.trim());

             //D_ACCEPT
           dto.setDaccept(rs.getDate("D_ACCEPT"));

             //D_ACCT
           dto.setDacct(rs.getDate("D_ACCT"));

             //S_BACKFLAG
             str = rs.getString("S_BACKFLAG");
             if (str == null)
                dto.setSbackflag(null);
             else
                dto.setSbackflag(str.trim());

             //S_ADDWORD
             str = rs.getString("S_ADDWORD");
             if (str == null)
                dto.setSaddword(null);
             else
                dto.setSaddword(str.trim());

             //S_PACKAGENO
             str = rs.getString("S_PACKAGENO");
             if (str == null)
                dto.setSpackageno(null);
             else
                dto.setSpackageno(str.trim());

             //S_STATUS
             str = rs.getString("S_STATUS");
             if (str == null)
                dto.setSstatus(null);
             else
                dto.setSstatus(str.trim());

             //C_TRIMFLAG
             str = rs.getString("C_TRIMFLAG");
             if (str == null)
                dto.setCtrimflag(null);
             else
                dto.setCtrimflag(str.trim());

             //S_FILENAME
             str = rs.getString("S_FILENAME");
             if (str == null)
                dto.setSfilename(null);
             else
                dto.setSfilename(str.trim());

             //S_BOOKORGCODE
             str = rs.getString("S_BOOKORGCODE");
             if (str == null)
                dto.setSbookorgcode(null);
             else
                dto.setSbookorgcode(str.trim());

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
        TbsTvPbcpayDto dto = (TbsTvPbcpayDto)_dto ;
        PreparedStatement ps = null;
        try {
            if(isLobSupport){
                ps = conn.prepareStatement(SQL_UPDATE_LOB);
            }
            else{
                ps = conn.prepareStatement(SQL_UPDATE);
            }
            int pos = 1;
            //S_TRECODE
            ps.setString(pos, dto.getStrecode());
            pos++;

            //S_PAYERACCT
            ps.setString(pos, dto.getSpayeracct());
            pos++;

            //S_PAYERNAME
            ps.setString(pos, dto.getSpayername());
            pos++;

            //S_PAYEROPNBNKNO
            ps.setString(pos, dto.getSpayeropnbnkno());
            pos++;

            //S_PAYEEACCT
            ps.setString(pos, dto.getSpayeeacct());
            pos++;

            //S_PAYEENAME
            ps.setString(pos, dto.getSpayeename());
            pos++;

            //S_PAYEEOPNBNKNO
            ps.setString(pos, dto.getSpayeeopnbnkno());
            pos++;

            //C_BDGKIND
            ps.setString(pos, dto.getCbdgkind());
            pos++;

            //S_FUNCSBTCODE
            ps.setString(pos, dto.getSfuncsbtcode());
            pos++;

            //S_ECOSBTCODE
            ps.setString(pos, dto.getSecosbtcode());
            pos++;

            //S_BDGORGCODE
            ps.setString(pos, dto.getSbdgorgcode());
            pos++;

            //S_BDGORGNAME
            ps.setString(pos, dto.getSbdgorgname());
            pos++;

            //S_VOUNO
            ps.setString(pos, dto.getSvouno());
            pos++;

            //D_VOUCHER
            ps.setDate(pos, dto.getDvoucher());
            pos++;

            //F_AMT
            ps.setBigDecimal(pos, dto.getFamt());
            pos++;

            //S_BILLORG
            ps.setString(pos, dto.getSbillorg());
            pos++;

            //S_BIZTYPE
            ps.setString(pos, dto.getSbiztype());
            pos++;

            //D_ACCEPT
            ps.setDate(pos, dto.getDaccept());
            pos++;

            //D_ACCT
            ps.setDate(pos, dto.getDacct());
            pos++;

            //S_BACKFLAG
            ps.setString(pos, dto.getSbackflag());
            pos++;

            //S_ADDWORD
            ps.setString(pos, dto.getSaddword());
            pos++;

            //S_PACKAGENO
            ps.setString(pos, dto.getSpackageno());
            pos++;

            //S_STATUS
            ps.setString(pos, dto.getSstatus());
            pos++;

            //C_TRIMFLAG
            ps.setString(pos, dto.getCtrimflag());
            pos++;

            //S_FILENAME
            ps.setString(pos, dto.getSfilename());
            pos++;

            //S_BOOKORGCODE
            ps.setString(pos, dto.getSbookorgcode());
            pos++;

            //TS_SYSUPDATE

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
     
     	 TbsTvPbcpayDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (TbsTvPbcpayDto)_dtos[i] ; 
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
                dto  = (TbsTvPbcpayDto)_dtos[i] ; 
                int pos = 1;
                //S_TRECODE
                 ps.setString(pos, dto.getStrecode());
                pos++;

                //S_PAYERACCT
                 ps.setString(pos, dto.getSpayeracct());
                pos++;

                //S_PAYERNAME
                 ps.setString(pos, dto.getSpayername());
                pos++;

                //S_PAYEROPNBNKNO
                 ps.setString(pos, dto.getSpayeropnbnkno());
                pos++;

                //S_PAYEEACCT
                 ps.setString(pos, dto.getSpayeeacct());
                pos++;

                //S_PAYEENAME
                 ps.setString(pos, dto.getSpayeename());
                pos++;

                //S_PAYEEOPNBNKNO
                 ps.setString(pos, dto.getSpayeeopnbnkno());
                pos++;

                //C_BDGKIND
                 ps.setString(pos, dto.getCbdgkind());
                pos++;

                //S_FUNCSBTCODE
                 ps.setString(pos, dto.getSfuncsbtcode());
                pos++;

                //S_ECOSBTCODE
                 ps.setString(pos, dto.getSecosbtcode());
                pos++;

                //S_BDGORGCODE
                 ps.setString(pos, dto.getSbdgorgcode());
                pos++;

                //S_BDGORGNAME
                 ps.setString(pos, dto.getSbdgorgname());
                pos++;

                //S_VOUNO
                 ps.setString(pos, dto.getSvouno());
                pos++;

                //D_VOUCHER
                 ps.setDate(pos, dto.getDvoucher());
                pos++;

                //F_AMT
                 ps.setBigDecimal(pos, dto.getFamt());
                pos++;

                //S_BILLORG
                 ps.setString(pos, dto.getSbillorg());
                pos++;

                //S_BIZTYPE
                 ps.setString(pos, dto.getSbiztype());
                pos++;

                //D_ACCEPT
                 ps.setDate(pos, dto.getDaccept());
                pos++;

                //D_ACCT
                 ps.setDate(pos, dto.getDacct());
                pos++;

                //S_BACKFLAG
                 ps.setString(pos, dto.getSbackflag());
                pos++;

                //S_ADDWORD
                 ps.setString(pos, dto.getSaddword());
                pos++;

                //S_PACKAGENO
                 ps.setString(pos, dto.getSpackageno());
                pos++;

                //S_STATUS
                 ps.setString(pos, dto.getSstatus());
                pos++;

                //C_TRIMFLAG
                 ps.setString(pos, dto.getCtrimflag());
                pos++;

                //S_FILENAME
                 ps.setString(pos, dto.getSfilename());
                pos++;

                //S_BOOKORGCODE
                 ps.setString(pos, dto.getSbookorgcode());
                pos++;

                //TS_SYSUPDATE
 
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
        //	throw new SQLException("ɾ������"+msgValid) ;
       }
       TbsTvPbcpayPK pk = (TbsTvPbcpayPK)_pk ;


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
        TbsTvPbcpayPK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TbsTvPbcpayPK)_pks[i] ; 
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
       			pk  = (TbsTvPbcpayPK)(pks.get(i)) ; 
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
                                                                                 		throw new SQLException("���ݿ����TBS_TV_PBCPAYû�м���޸ĵ��ֶ�");
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
    



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

import com.cfcc.itfe.persistence.dto.TfVoucherSplitDto ;
import com.cfcc.itfe.persistence.pk.TfVoucherSplitPK ;


/**
 * <p>Title: DAO of table: TF_VOUCHER_SPLIT</p>
 * <p>Description:ƾ֤��ֱ� Data Access Object  </p>
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

public class TfVoucherSplitDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO TF_VOUCHER_SPLIT ("
          + "I_VOUSRLNO,S_MAIN_NO,S_SPLIT_NO,S_VTCODE,S_SPLIT_VOUSRLNO"
          + ",S_COMMITDATE,S_ORGCODE,S_TRECODE,S_STATUS,S_DEMO"
          + ",TS_SYSUPDATE,S_PACKAGENO,S_ALLNUM,N_ALLAMT,S_XACCDATE"
          + ",N_XALLAMT,S_EXT1,S_EXT2,S_EXT3,S_EXT4"
          + ",S_EXT5"
        + ") VALUES ("
        + "?,?,?,?,?,?,?,?,?,?,CURRENT TIMESTAMP ,?,?,?,?,?,?,?,?,?,?)";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "TF_VOUCHER_SPLIT.I_VOUSRLNO, TF_VOUCHER_SPLIT.S_MAIN_NO, TF_VOUCHER_SPLIT.S_SPLIT_NO, TF_VOUCHER_SPLIT.S_VTCODE, TF_VOUCHER_SPLIT.S_SPLIT_VOUSRLNO, "
        + "TF_VOUCHER_SPLIT.S_COMMITDATE, TF_VOUCHER_SPLIT.S_ORGCODE, TF_VOUCHER_SPLIT.S_TRECODE, TF_VOUCHER_SPLIT.S_STATUS, TF_VOUCHER_SPLIT.S_DEMO, "
        + "TF_VOUCHER_SPLIT.TS_SYSUPDATE, TF_VOUCHER_SPLIT.S_PACKAGENO, TF_VOUCHER_SPLIT.S_ALLNUM, TF_VOUCHER_SPLIT.N_ALLAMT, TF_VOUCHER_SPLIT.S_XACCDATE, "
        + "TF_VOUCHER_SPLIT.N_XALLAMT, TF_VOUCHER_SPLIT.S_EXT1, TF_VOUCHER_SPLIT.S_EXT2, TF_VOUCHER_SPLIT.S_EXT3, TF_VOUCHER_SPLIT.S_EXT4, "
        + "TF_VOUCHER_SPLIT.S_EXT5 "
        + "FROM TF_VOUCHER_SPLIT "
        +" WHERE " 
        + "I_VOUSRLNO = ? AND S_SPLIT_NO = ? AND S_VTCODE = ? AND S_COMMITDATE = ?"
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "TF_VOUCHER_SPLIT.I_VOUSRLNO, TF_VOUCHER_SPLIT.S_MAIN_NO, TF_VOUCHER_SPLIT.S_SPLIT_NO, TF_VOUCHER_SPLIT.S_VTCODE, TF_VOUCHER_SPLIT.S_SPLIT_VOUSRLNO, "
        + "TF_VOUCHER_SPLIT.S_COMMITDATE, TF_VOUCHER_SPLIT.S_ORGCODE, TF_VOUCHER_SPLIT.S_TRECODE, TF_VOUCHER_SPLIT.S_STATUS, TF_VOUCHER_SPLIT.S_DEMO, "
        + "TF_VOUCHER_SPLIT.TS_SYSUPDATE, TF_VOUCHER_SPLIT.S_PACKAGENO, TF_VOUCHER_SPLIT.S_ALLNUM, TF_VOUCHER_SPLIT.N_ALLAMT, TF_VOUCHER_SPLIT.S_XACCDATE, "
        + "TF_VOUCHER_SPLIT.N_XALLAMT, TF_VOUCHER_SPLIT.S_EXT1, TF_VOUCHER_SPLIT.S_EXT2, TF_VOUCHER_SPLIT.S_EXT3, TF_VOUCHER_SPLIT.S_EXT4, "
        + "TF_VOUCHER_SPLIT.S_EXT5 "
        + "FROM TF_VOUCHER_SPLIT "
        +" WHERE " 
        + "I_VOUSRLNO = ? AND S_SPLIT_NO = ? AND S_VTCODE = ? AND S_COMMITDATE = ? FOR UPDATE"
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  TF_VOUCHER_SPLIT.I_VOUSRLNO  , TF_VOUCHER_SPLIT.S_MAIN_NO  , TF_VOUCHER_SPLIT.S_SPLIT_NO  , TF_VOUCHER_SPLIT.S_VTCODE  , TF_VOUCHER_SPLIT.S_SPLIT_VOUSRLNO "
        + " , TF_VOUCHER_SPLIT.S_COMMITDATE  , TF_VOUCHER_SPLIT.S_ORGCODE  , TF_VOUCHER_SPLIT.S_TRECODE  , TF_VOUCHER_SPLIT.S_STATUS  , TF_VOUCHER_SPLIT.S_DEMO "
        + " , TF_VOUCHER_SPLIT.TS_SYSUPDATE  , TF_VOUCHER_SPLIT.S_PACKAGENO  , TF_VOUCHER_SPLIT.S_ALLNUM  , TF_VOUCHER_SPLIT.N_ALLAMT  , TF_VOUCHER_SPLIT.S_XACCDATE "
        + " , TF_VOUCHER_SPLIT.N_XALLAMT  , TF_VOUCHER_SPLIT.S_EXT1  , TF_VOUCHER_SPLIT.S_EXT2  , TF_VOUCHER_SPLIT.S_EXT3  , TF_VOUCHER_SPLIT.S_EXT4 "
        + " , TF_VOUCHER_SPLIT.S_EXT5 "
        + "FROM TF_VOUCHER_SPLIT ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "TF_VOUCHER_SPLIT.I_VOUSRLNO, TF_VOUCHER_SPLIT.S_MAIN_NO, TF_VOUCHER_SPLIT.S_SPLIT_NO, TF_VOUCHER_SPLIT.S_VTCODE, TF_VOUCHER_SPLIT.S_SPLIT_VOUSRLNO, "
        + "TF_VOUCHER_SPLIT.S_COMMITDATE, TF_VOUCHER_SPLIT.S_ORGCODE, TF_VOUCHER_SPLIT.S_TRECODE, TF_VOUCHER_SPLIT.S_STATUS, TF_VOUCHER_SPLIT.S_DEMO, "
        + "TF_VOUCHER_SPLIT.TS_SYSUPDATE, TF_VOUCHER_SPLIT.S_PACKAGENO, TF_VOUCHER_SPLIT.S_ALLNUM, TF_VOUCHER_SPLIT.N_ALLAMT, TF_VOUCHER_SPLIT.S_XACCDATE, "
        + "TF_VOUCHER_SPLIT.N_XALLAMT, TF_VOUCHER_SPLIT.S_EXT1, TF_VOUCHER_SPLIT.S_EXT2, TF_VOUCHER_SPLIT.S_EXT3, TF_VOUCHER_SPLIT.S_EXT4, "
        + "TF_VOUCHER_SPLIT.S_EXT5 "
        + "FROM TF_VOUCHER_SPLIT " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        + "I_VOUSRLNO = ? AND S_SPLIT_NO = ? AND S_VTCODE = ? AND S_COMMITDATE = ?)"
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE TF_VOUCHER_SPLIT SET "
        + "S_MAIN_NO =?,S_SPLIT_VOUSRLNO =?,S_ORGCODE =?,S_TRECODE =?,S_STATUS =?, "
        + "S_DEMO =?,TS_SYSUPDATE =CURRENT TIMESTAMP,S_PACKAGENO =?,S_ALLNUM =?,N_ALLAMT =?, "
        + "S_XACCDATE =?,N_XALLAMT =?,S_EXT1 =?,S_EXT2 =?,S_EXT3 =?, "
        + "S_EXT4 =?,S_EXT5 =? "
        + "WHERE "
        + "I_VOUSRLNO = ? AND S_SPLIT_NO = ? AND S_VTCODE = ? AND S_COMMITDATE = ?"
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE TF_VOUCHER_SPLIT SET "
        + "S_MAIN_NO =?, S_SPLIT_VOUSRLNO =?, S_ORGCODE =?, S_TRECODE =?, S_STATUS =?,  "
        + "S_DEMO =?, TS_SYSUPDATE =CURRENT TIMESTAMP, S_PACKAGENO =?, S_ALLNUM =?, N_ALLAMT =?,  "
        + "S_XACCDATE =?, N_XALLAMT =?, S_EXT1 =?, S_EXT2 =?, S_EXT3 =?,  "
        + "S_EXT4 =?, S_EXT5 =? "
        + "WHERE "
        + "I_VOUSRLNO = ? AND S_SPLIT_NO = ? AND S_VTCODE = ? AND S_COMMITDATE = ?"
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM TF_VOUCHER_SPLIT " 
        + " WHERE "
        + "I_VOUSRLNO = ? AND S_SPLIT_NO = ? AND S_VTCODE = ? AND S_COMMITDATE = ?"
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
     TfVoucherSplitDto dto = (TfVoucherSplitDto)_dto ;
     String msgValid = dto.checkValid() ;
     if (msgValid != null)
     	throw new SQLException("�������"+msgValid) ;

     PreparedStatement ps = null;
     try
     {
         ps = conn.prepareStatement(SQL_INSERT);
          if (dto.getIvousrlno()==null)
            ps.setNull(1, java.sql.Types.BIGINT);
         else
            ps.setLong(1, dto.getIvousrlno().longValue());
          ps.setString(2, dto.getSmainno());

          ps.setString(3, dto.getSsplitno());

          ps.setString(4, dto.getSvtcode());

          ps.setString(5, dto.getSsplitvousrlno());

          ps.setString(6, dto.getScommitdate());

          ps.setString(7, dto.getSorgcode());

          ps.setString(8, dto.getStrecode());

          ps.setString(9, dto.getSstatus());

          ps.setString(10, dto.getSdemo());

           ps.setString(11, dto.getSpackageno());

          ps.setString(12, dto.getSallnum());

          ps.setBigDecimal(13, dto.getNallamt());

          ps.setString(14, dto.getSxaccdate());

          ps.setBigDecimal(15, dto.getNxallamt());

          ps.setString(16, dto.getSext1());

          ps.setString(17, dto.getSext2());

          ps.setString(18, dto.getSext3());

          ps.setString(19, dto.getSext4());

          ps.setString(20, dto.getSext5());

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
       TfVoucherSplitDto dto = (TfVoucherSplitDto)_dto ;
       String msgValid = dto.checkValid() ;
       if (msgValid != null)
           throw new SQLException("�������"+msgValid) ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try
       {
           ps = conn.prepareStatement(SQL_INSERT_WITH_RESULT);
            if (dto.getIvousrlno()==null)
              ps.setNull(1, java.sql.Types.BIGINT);
           else
              ps.setLong(1, dto.getIvousrlno().longValue());
            ps.setString(2, dto.getSmainno());
            ps.setString(3, dto.getSsplitno());
            ps.setString(4, dto.getSvtcode());
            ps.setString(5, dto.getSsplitvousrlno());
            ps.setString(6, dto.getScommitdate());
            ps.setString(7, dto.getSorgcode());
            ps.setString(8, dto.getStrecode());
            ps.setString(9, dto.getSstatus());
            ps.setString(10, dto.getSdemo());
             ps.setString(11, dto.getSpackageno());
            ps.setString(12, dto.getSallnum());
            ps.setBigDecimal(13, dto.getNallamt());
            ps.setString(14, dto.getSxaccdate());
            ps.setBigDecimal(15, dto.getNxallamt());
            ps.setString(16, dto.getSext1());
            ps.setString(17, dto.getSext2());
            ps.setString(18, dto.getSext3());
            ps.setString(19, dto.getSext4());
            ps.setString(20, dto.getSext5());
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
        TfVoucherSplitDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (TfVoucherSplitDto)_dtos[i] ; 
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
 	    	    dto  = (TfVoucherSplitDto)_dtos[i] ; 
  
               if (dto.getIvousrlno()==null)
                  ps.setNull(1, java.sql.Types.BIGINT);
               else
                  ps.setLong(1, dto.getIvousrlno().longValue());
  
               ps.setString(2, dto.getSmainno());
  
               ps.setString(3, dto.getSsplitno());
  
               ps.setString(4, dto.getSvtcode());
  
               ps.setString(5, dto.getSsplitvousrlno());
  
               ps.setString(6, dto.getScommitdate());
  
               ps.setString(7, dto.getSorgcode());
  
               ps.setString(8, dto.getStrecode());
  
               ps.setString(9, dto.getSstatus());
  
               ps.setString(10, dto.getSdemo());
   
               ps.setString(11, dto.getSpackageno());
  
               ps.setString(12, dto.getSallnum());
  
               ps.setBigDecimal(13, dto.getNallamt());
  
               ps.setString(14, dto.getSxaccdate());
  
               ps.setBigDecimal(15, dto.getNxallamt());
  
               ps.setString(16, dto.getSext1());
  
               ps.setString(17, dto.getSext2());
  
               ps.setString(18, dto.getSext3());
  
               ps.setString(19, dto.getSext4());
  
               ps.setString(20, dto.getSext5());
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
        	
       TfVoucherSplitPK pk = (TfVoucherSplitPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT);
           if (pk.getIvousrlno()==null)
               ps.setNull(1, java.sql.Types.BIGINT);
           else
               ps.setLong(1, pk.getIvousrlno().longValue());

           ps.setString(2, pk.getSsplitno());

           ps.setString(3, pk.getSvtcode());

           ps.setString(4, pk.getScommitdate());

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
        	
       TfVoucherSplitPK pk = (TfVoucherSplitPK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT_FOR_UPDATE);
           if (pk.getIvousrlno()==null)
               ps.setNull(1, java.sql.Types.BIGINT);
           else
               ps.setLong(1, pk.getIvousrlno().longValue());

           ps.setString(2, pk.getSsplitno());

           ps.setString(3, pk.getSvtcode());

           ps.setString(4, pk.getScommitdate());

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
        TfVoucherSplitPK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TfVoucherSplitPK)_pks[i] ; 
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
                    pk  = (TfVoucherSplitPK)(pks.get(i)) ; 
                   if (pk.getIvousrlno()==null)
                      ps.setNull((i-iBegin)*4+1, java.sql.Types.BIGINT);
                   else
                      ps.setLong((i-iBegin)*4+1, pk.getIvousrlno().longValue());

                   ps.setString((i-iBegin)*4+2, pk.getSsplitno());

                   ps.setString((i-iBegin)*4+3, pk.getSvtcode());

                   ps.setString((i-iBegin)*4+4, pk.getScommitdate());

			
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
            TfVoucherSplitDto[] dtos = new TfVoucherSplitDto[0];
		    dtos = (TfVoucherSplitDto[]) results.toArray(dtos) ;
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
             TfVoucherSplitDto  dto = new  TfVoucherSplitDto ();
             //I_VOUSRLNO
             str = rs.getString("I_VOUSRLNO");
             if(str!=null){
                dto.setIvousrlno(new Long(str));
             }

             //S_MAIN_NO
             str = rs.getString("S_MAIN_NO");
             if (str == null)
                dto.setSmainno(null);
             else
                dto.setSmainno(str.trim());

             //S_SPLIT_NO
             str = rs.getString("S_SPLIT_NO");
             if (str == null)
                dto.setSsplitno(null);
             else
                dto.setSsplitno(str.trim());

             //S_VTCODE
             str = rs.getString("S_VTCODE");
             if (str == null)
                dto.setSvtcode(null);
             else
                dto.setSvtcode(str.trim());

             //S_SPLIT_VOUSRLNO
             str = rs.getString("S_SPLIT_VOUSRLNO");
             if (str == null)
                dto.setSsplitvousrlno(null);
             else
                dto.setSsplitvousrlno(str.trim());

             //S_COMMITDATE
             str = rs.getString("S_COMMITDATE");
             if (str == null)
                dto.setScommitdate(null);
             else
                dto.setScommitdate(str.trim());

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

             //S_PACKAGENO
             str = rs.getString("S_PACKAGENO");
             if (str == null)
                dto.setSpackageno(null);
             else
                dto.setSpackageno(str.trim());

             //S_ALLNUM
             str = rs.getString("S_ALLNUM");
             if (str == null)
                dto.setSallnum(null);
             else
                dto.setSallnum(str.trim());

             //N_ALLAMT
           dto.setNallamt(rs.getBigDecimal("N_ALLAMT"));

             //S_XACCDATE
             str = rs.getString("S_XACCDATE");
             if (str == null)
                dto.setSxaccdate(null);
             else
                dto.setSxaccdate(str.trim());

             //N_XALLAMT
           dto.setNxallamt(rs.getBigDecimal("N_XALLAMT"));

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

             //S_EXT4
             str = rs.getString("S_EXT4");
             if (str == null)
                dto.setSext4(null);
             else
                dto.setSext4(str.trim());

             //S_EXT5
             str = rs.getString("S_EXT5");
             if (str == null)
                dto.setSext5(null);
             else
                dto.setSext5(str.trim());



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
        TfVoucherSplitDto dto = (TfVoucherSplitDto)_dto ;
        PreparedStatement ps = null;
        try {
            if(isLobSupport){
                ps = conn.prepareStatement(SQL_UPDATE_LOB);
            }
            else{
                ps = conn.prepareStatement(SQL_UPDATE);
            }
            int pos = 1;
            //S_MAIN_NO
            ps.setString(pos, dto.getSmainno());
            pos++;

            //S_SPLIT_VOUSRLNO
            ps.setString(pos, dto.getSsplitvousrlno());
            pos++;

            //S_ORGCODE
            ps.setString(pos, dto.getSorgcode());
            pos++;

            //S_TRECODE
            ps.setString(pos, dto.getStrecode());
            pos++;

            //S_STATUS
            ps.setString(pos, dto.getSstatus());
            pos++;

            //S_DEMO
            ps.setString(pos, dto.getSdemo());
            pos++;

            //TS_SYSUPDATE
            //S_PACKAGENO
            ps.setString(pos, dto.getSpackageno());
            pos++;

            //S_ALLNUM
            ps.setString(pos, dto.getSallnum());
            pos++;

            //N_ALLAMT
            ps.setBigDecimal(pos, dto.getNallamt());
            pos++;

            //S_XACCDATE
            ps.setString(pos, dto.getSxaccdate());
            pos++;

            //N_XALLAMT
            ps.setBigDecimal(pos, dto.getNxallamt());
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

            //S_EXT4
            ps.setString(pos, dto.getSext4());
            pos++;

            //S_EXT5
            ps.setString(pos, dto.getSext5());
            pos++;


           //I_VOUSRLNO
           ps.setLong(pos, dto.getIvousrlno().longValue());
           pos++;
           //S_SPLIT_NO
           ps.setString(pos, dto.getSsplitno());
           pos++;
           //S_VTCODE
           ps.setString(pos, dto.getSvtcode());
           pos++;
           //S_COMMITDATE
           ps.setString(pos, dto.getScommitdate());
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
     
     	 TfVoucherSplitDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (TfVoucherSplitDto)_dtos[i] ; 
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
                dto  = (TfVoucherSplitDto)_dtos[i] ; 
                int pos = 1;
                //S_MAIN_NO
                 ps.setString(pos, dto.getSmainno());
                pos++;

                //S_SPLIT_VOUSRLNO
                 ps.setString(pos, dto.getSsplitvousrlno());
                pos++;

                //S_ORGCODE
                 ps.setString(pos, dto.getSorgcode());
                pos++;

                //S_TRECODE
                 ps.setString(pos, dto.getStrecode());
                pos++;

                //S_STATUS
                 ps.setString(pos, dto.getSstatus());
                pos++;

                //S_DEMO
                 ps.setString(pos, dto.getSdemo());
                pos++;

                //TS_SYSUPDATE
                 //S_PACKAGENO
                 ps.setString(pos, dto.getSpackageno());
                pos++;

                //S_ALLNUM
                 ps.setString(pos, dto.getSallnum());
                pos++;

                //N_ALLAMT
                 ps.setBigDecimal(pos, dto.getNallamt());
                pos++;

                //S_XACCDATE
                 ps.setString(pos, dto.getSxaccdate());
                pos++;

                //N_XALLAMT
                 ps.setBigDecimal(pos, dto.getNxallamt());
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

                //S_EXT4
                 ps.setString(pos, dto.getSext4());
                pos++;

                //S_EXT5
                 ps.setString(pos, dto.getSext5());
                pos++;


               //I_VOUSRLNO
               ps.setLong(pos, dto.getIvousrlno().longValue());
               pos++;
               //S_SPLIT_NO
               ps.setString(pos, dto.getSsplitno());
               pos++;
               //S_VTCODE
               ps.setString(pos, dto.getSvtcode());
               pos++;
               //S_COMMITDATE
               ps.setString(pos, dto.getScommitdate());
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
       TfVoucherSplitPK pk = (TfVoucherSplitPK)_pk ;


       PreparedStatement ps = null;
       try {
           ps = conn.prepareStatement(SQL_DELETE);
           ps.setLong(1, pk.getIvousrlno().longValue());
           ps.setString(2, pk.getSsplitno());
           ps.setString(3, pk.getSvtcode());
           ps.setString(4, pk.getScommitdate());
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
        TfVoucherSplitPK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TfVoucherSplitPK)_pks[i] ; 
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
       			pk  = (TfVoucherSplitPK)(pks.get(i)) ; 
                ps.setLong(1, pk.getIvousrlno().longValue());
                ps.setString(2, pk.getSsplitno());
                ps.setString(3, pk.getSvtcode());
                ps.setString(4, pk.getScommitdate());
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
                                                   		throw new SQLException("���ݿ����TF_VOUCHER_SPLITû�м���޸ĵ��ֶ�");
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
    



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

import com.cfcc.itfe.persistence.dto.TpShareDivideDto ;
import com.cfcc.itfe.persistence.pk.TpShareDividePK ;


/**
 * <p>Title: DAO of table: TP_SHARE_DIVIDE</p>
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

public class TpShareDivideDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO TP_SHARE_DIVIDE ("
          + "I_PARAMSEQNO,S_BOOKORGCODE,C_TRIMFLAG,I_DIVIDEGRPID,S_ROOTTRECODE"
          + ",S_TRATRECODE,S_PAYEETRECODE,S_ROOTTAXORGCODE,S_ROOTBDGSBTCODE,C_ROOTBDGKIND"
          + ",S_ROOTASTFLAG,S_AFTTRECODE,S_AFTTAXORGCODE,C_AFTBDGLEVEL,S_AFTBDGSBTCODE"
          + ",C_AFTBDGTYPE,S_AFTASTFLAG,F_JOINDIVIDERATE,C_GOVERNRALATION,TS_SYSUPDATE"
        + ") VALUES ("
        + "DEFAULT ,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,CURRENT TIMESTAMP )";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "TP_SHARE_DIVIDE.I_PARAMSEQNO, TP_SHARE_DIVIDE.S_BOOKORGCODE, TP_SHARE_DIVIDE.C_TRIMFLAG, TP_SHARE_DIVIDE.I_DIVIDEGRPID, TP_SHARE_DIVIDE.S_ROOTTRECODE, "
        + "TP_SHARE_DIVIDE.S_TRATRECODE, TP_SHARE_DIVIDE.S_PAYEETRECODE, TP_SHARE_DIVIDE.S_ROOTTAXORGCODE, TP_SHARE_DIVIDE.S_ROOTBDGSBTCODE, TP_SHARE_DIVIDE.C_ROOTBDGKIND, "
        + "TP_SHARE_DIVIDE.S_ROOTASTFLAG, TP_SHARE_DIVIDE.S_AFTTRECODE, TP_SHARE_DIVIDE.S_AFTTAXORGCODE, TP_SHARE_DIVIDE.C_AFTBDGLEVEL, TP_SHARE_DIVIDE.S_AFTBDGSBTCODE, "
        + "TP_SHARE_DIVIDE.C_AFTBDGTYPE, TP_SHARE_DIVIDE.S_AFTASTFLAG, TP_SHARE_DIVIDE.F_JOINDIVIDERATE, TP_SHARE_DIVIDE.C_GOVERNRALATION, TP_SHARE_DIVIDE.TS_SYSUPDATE "
        + "FROM TP_SHARE_DIVIDE "
        +" WHERE " 
        + "I_PARAMSEQNO = ?"
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "TP_SHARE_DIVIDE.I_PARAMSEQNO, TP_SHARE_DIVIDE.S_BOOKORGCODE, TP_SHARE_DIVIDE.C_TRIMFLAG, TP_SHARE_DIVIDE.I_DIVIDEGRPID, TP_SHARE_DIVIDE.S_ROOTTRECODE, "
        + "TP_SHARE_DIVIDE.S_TRATRECODE, TP_SHARE_DIVIDE.S_PAYEETRECODE, TP_SHARE_DIVIDE.S_ROOTTAXORGCODE, TP_SHARE_DIVIDE.S_ROOTBDGSBTCODE, TP_SHARE_DIVIDE.C_ROOTBDGKIND, "
        + "TP_SHARE_DIVIDE.S_ROOTASTFLAG, TP_SHARE_DIVIDE.S_AFTTRECODE, TP_SHARE_DIVIDE.S_AFTTAXORGCODE, TP_SHARE_DIVIDE.C_AFTBDGLEVEL, TP_SHARE_DIVIDE.S_AFTBDGSBTCODE, "
        + "TP_SHARE_DIVIDE.C_AFTBDGTYPE, TP_SHARE_DIVIDE.S_AFTASTFLAG, TP_SHARE_DIVIDE.F_JOINDIVIDERATE, TP_SHARE_DIVIDE.C_GOVERNRALATION, TP_SHARE_DIVIDE.TS_SYSUPDATE "
        + "FROM TP_SHARE_DIVIDE "
        +" WHERE " 
        + "I_PARAMSEQNO = ? FOR UPDATE"
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  TP_SHARE_DIVIDE.I_PARAMSEQNO  , TP_SHARE_DIVIDE.S_BOOKORGCODE  , TP_SHARE_DIVIDE.C_TRIMFLAG  , TP_SHARE_DIVIDE.I_DIVIDEGRPID  , TP_SHARE_DIVIDE.S_ROOTTRECODE "
        + " , TP_SHARE_DIVIDE.S_TRATRECODE  , TP_SHARE_DIVIDE.S_PAYEETRECODE  , TP_SHARE_DIVIDE.S_ROOTTAXORGCODE  , TP_SHARE_DIVIDE.S_ROOTBDGSBTCODE  , TP_SHARE_DIVIDE.C_ROOTBDGKIND "
        + " , TP_SHARE_DIVIDE.S_ROOTASTFLAG  , TP_SHARE_DIVIDE.S_AFTTRECODE  , TP_SHARE_DIVIDE.S_AFTTAXORGCODE  , TP_SHARE_DIVIDE.C_AFTBDGLEVEL  , TP_SHARE_DIVIDE.S_AFTBDGSBTCODE "
        + " , TP_SHARE_DIVIDE.C_AFTBDGTYPE  , TP_SHARE_DIVIDE.S_AFTASTFLAG  , TP_SHARE_DIVIDE.F_JOINDIVIDERATE  , TP_SHARE_DIVIDE.C_GOVERNRALATION  , TP_SHARE_DIVIDE.TS_SYSUPDATE "
        + "FROM TP_SHARE_DIVIDE ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "TP_SHARE_DIVIDE.I_PARAMSEQNO, TP_SHARE_DIVIDE.S_BOOKORGCODE, TP_SHARE_DIVIDE.C_TRIMFLAG, TP_SHARE_DIVIDE.I_DIVIDEGRPID, TP_SHARE_DIVIDE.S_ROOTTRECODE, "
        + "TP_SHARE_DIVIDE.S_TRATRECODE, TP_SHARE_DIVIDE.S_PAYEETRECODE, TP_SHARE_DIVIDE.S_ROOTTAXORGCODE, TP_SHARE_DIVIDE.S_ROOTBDGSBTCODE, TP_SHARE_DIVIDE.C_ROOTBDGKIND, "
        + "TP_SHARE_DIVIDE.S_ROOTASTFLAG, TP_SHARE_DIVIDE.S_AFTTRECODE, TP_SHARE_DIVIDE.S_AFTTAXORGCODE, TP_SHARE_DIVIDE.C_AFTBDGLEVEL, TP_SHARE_DIVIDE.S_AFTBDGSBTCODE, "
        + "TP_SHARE_DIVIDE.C_AFTBDGTYPE, TP_SHARE_DIVIDE.S_AFTASTFLAG, TP_SHARE_DIVIDE.F_JOINDIVIDERATE, TP_SHARE_DIVIDE.C_GOVERNRALATION, TP_SHARE_DIVIDE.TS_SYSUPDATE "
        + "FROM TP_SHARE_DIVIDE " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        + "I_PARAMSEQNO = ?)"
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE TP_SHARE_DIVIDE SET "
        + "S_BOOKORGCODE =?,C_TRIMFLAG =?,I_DIVIDEGRPID =?,S_ROOTTRECODE =?,S_TRATRECODE =?, "
        + "S_PAYEETRECODE =?,S_ROOTTAXORGCODE =?,S_ROOTBDGSBTCODE =?,C_ROOTBDGKIND =?,S_ROOTASTFLAG =?, "
        + "S_AFTTRECODE =?,S_AFTTAXORGCODE =?,C_AFTBDGLEVEL =?,S_AFTBDGSBTCODE =?,C_AFTBDGTYPE =?, "
        + "S_AFTASTFLAG =?,F_JOINDIVIDERATE =?,C_GOVERNRALATION =?,TS_SYSUPDATE =CURRENT TIMESTAMP "
        + "WHERE "
        + "I_PARAMSEQNO = ?"
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE TP_SHARE_DIVIDE SET "
        + "S_BOOKORGCODE =?, C_TRIMFLAG =?, I_DIVIDEGRPID =?, S_ROOTTRECODE =?, S_TRATRECODE =?,  "
        + "S_PAYEETRECODE =?, S_ROOTTAXORGCODE =?, S_ROOTBDGSBTCODE =?, C_ROOTBDGKIND =?, S_ROOTASTFLAG =?,  "
        + "S_AFTTRECODE =?, S_AFTTAXORGCODE =?, C_AFTBDGLEVEL =?, S_AFTBDGSBTCODE =?, C_AFTBDGTYPE =?,  "
        + "S_AFTASTFLAG =?, F_JOINDIVIDERATE =?, C_GOVERNRALATION =?, TS_SYSUPDATE =CURRENT TIMESTAMP "
        + "WHERE "
        + "I_PARAMSEQNO = ?"
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM TP_SHARE_DIVIDE " 
        + " WHERE "
        + "I_PARAMSEQNO = ?"
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
     TpShareDivideDto dto = (TpShareDivideDto)_dto ;
     String msgValid = dto.checkValid() ;
     if (msgValid != null)
     	throw new SQLException("插入错误，"+msgValid) ;

     PreparedStatement ps = null;
     try
     {
         ps = conn.prepareStatement(SQL_INSERT);
           ps.setString(1, dto.getSbookorgcode());

          ps.setString(2, dto.getCtrimflag());

          if (dto.getIdividegrpid()==null)
            ps.setNull(3, java.sql.Types.INTEGER);
         else
            ps.setInt(3, dto.getIdividegrpid().intValue());
          ps.setString(4, dto.getSroottrecode());

          ps.setString(5, dto.getStratrecode());

          ps.setString(6, dto.getSpayeetrecode());

          ps.setString(7, dto.getSroottaxorgcode());

          ps.setString(8, dto.getSrootbdgsbtcode());

          ps.setString(9, dto.getCrootbdgkind());

          ps.setString(10, dto.getSrootastflag());

          ps.setString(11, dto.getSafttrecode());

          ps.setString(12, dto.getSafttaxorgcode());

          ps.setString(13, dto.getCaftbdglevel());

          ps.setString(14, dto.getSaftbdgsbtcode());

          ps.setString(15, dto.getCaftbdgtype());

          ps.setString(16, dto.getSaftastflag());

          ps.setBigDecimal(17, dto.getFjoindividerate());

          ps.setString(18, dto.getCgovernralation());

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
       TpShareDivideDto dto = (TpShareDivideDto)_dto ;
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
            if (dto.getIdividegrpid()==null)
              ps.setNull(3, java.sql.Types.INTEGER);
           else
              ps.setInt(3, dto.getIdividegrpid().intValue());
            ps.setString(4, dto.getSroottrecode());
            ps.setString(5, dto.getStratrecode());
            ps.setString(6, dto.getSpayeetrecode());
            ps.setString(7, dto.getSroottaxorgcode());
            ps.setString(8, dto.getSrootbdgsbtcode());
            ps.setString(9, dto.getCrootbdgkind());
            ps.setString(10, dto.getSrootastflag());
            ps.setString(11, dto.getSafttrecode());
            ps.setString(12, dto.getSafttaxorgcode());
            ps.setString(13, dto.getCaftbdglevel());
            ps.setString(14, dto.getSaftbdgsbtcode());
            ps.setString(15, dto.getCaftbdgtype());
            ps.setString(16, dto.getSaftastflag());
            ps.setBigDecimal(17, dto.getFjoindividerate());
            ps.setString(18, dto.getCgovernralation());
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
        TpShareDivideDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (TpShareDivideDto)_dtos[i] ; 
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
 	    	    dto  = (TpShareDivideDto)_dtos[i] ; 
   
               ps.setString(1, dto.getSbookorgcode());
  
               ps.setString(2, dto.getCtrimflag());
  
               if (dto.getIdividegrpid()==null)
                  ps.setNull(3, java.sql.Types.INTEGER);
               else
                  ps.setInt(3, dto.getIdividegrpid().intValue());
  
               ps.setString(4, dto.getSroottrecode());
  
               ps.setString(5, dto.getStratrecode());
  
               ps.setString(6, dto.getSpayeetrecode());
  
               ps.setString(7, dto.getSroottaxorgcode());
  
               ps.setString(8, dto.getSrootbdgsbtcode());
  
               ps.setString(9, dto.getCrootbdgkind());
  
               ps.setString(10, dto.getSrootastflag());
  
               ps.setString(11, dto.getSafttrecode());
  
               ps.setString(12, dto.getSafttaxorgcode());
  
               ps.setString(13, dto.getCaftbdglevel());
  
               ps.setString(14, dto.getSaftbdgsbtcode());
  
               ps.setString(15, dto.getCaftbdgtype());
  
               ps.setString(16, dto.getSaftastflag());
  
               ps.setBigDecimal(17, dto.getFjoindividerate());
  
               ps.setString(18, dto.getCgovernralation());
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
        	
       TpShareDividePK pk = (TpShareDividePK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT);

           if (pk.getIparamseqno()==null)
               ps.setNull(1, java.sql.Types.INTEGER);
           else
               ps.setInt(1, pk.getIparamseqno().intValue());

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
        	
       TpShareDividePK pk = (TpShareDividePK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT_FOR_UPDATE);

           if (pk.getIparamseqno()==null)
               ps.setNull(1, java.sql.Types.INTEGER);
           else
               ps.setInt(1, pk.getIparamseqno().intValue());

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
        TpShareDividePK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TpShareDividePK)_pks[i] ; 
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
                    pk  = (TpShareDividePK)(pks.get(i)) ; 
                   if (pk.getIparamseqno()==null)
                      ps.setNull((i-iBegin)*1+ 1 , java.sql.Types.INTEGER);
                   else
                     ps.setInt((i-iBegin)*1+1, pk.getIparamseqno().intValue());

			
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
            TpShareDivideDto[] dtos = new TpShareDivideDto[0];
		    dtos = (TpShareDivideDto[]) results.toArray(dtos) ;
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
             TpShareDivideDto  dto = new  TpShareDivideDto ();
             //I_PARAMSEQNO
             str = rs.getString("I_PARAMSEQNO");
             if(str!=null){
                dto.setIparamseqno(new Integer(str));
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

             //I_DIVIDEGRPID
             str = rs.getString("I_DIVIDEGRPID");
             if(str!=null){
                dto.setIdividegrpid(new Integer(str));
             }

             //S_ROOTTRECODE
             str = rs.getString("S_ROOTTRECODE");
             if (str == null)
                dto.setSroottrecode(null);
             else
                dto.setSroottrecode(str.trim());

             //S_TRATRECODE
             str = rs.getString("S_TRATRECODE");
             if (str == null)
                dto.setStratrecode(null);
             else
                dto.setStratrecode(str.trim());

             //S_PAYEETRECODE
             str = rs.getString("S_PAYEETRECODE");
             if (str == null)
                dto.setSpayeetrecode(null);
             else
                dto.setSpayeetrecode(str.trim());

             //S_ROOTTAXORGCODE
             str = rs.getString("S_ROOTTAXORGCODE");
             if (str == null)
                dto.setSroottaxorgcode(null);
             else
                dto.setSroottaxorgcode(str.trim());

             //S_ROOTBDGSBTCODE
             str = rs.getString("S_ROOTBDGSBTCODE");
             if (str == null)
                dto.setSrootbdgsbtcode(null);
             else
                dto.setSrootbdgsbtcode(str.trim());

             //C_ROOTBDGKIND
             str = rs.getString("C_ROOTBDGKIND");
             if (str == null)
                dto.setCrootbdgkind(null);
             else
                dto.setCrootbdgkind(str.trim());

             //S_ROOTASTFLAG
             str = rs.getString("S_ROOTASTFLAG");
             if (str == null)
                dto.setSrootastflag(null);
             else
                dto.setSrootastflag(str.trim());

             //S_AFTTRECODE
             str = rs.getString("S_AFTTRECODE");
             if (str == null)
                dto.setSafttrecode(null);
             else
                dto.setSafttrecode(str.trim());

             //S_AFTTAXORGCODE
             str = rs.getString("S_AFTTAXORGCODE");
             if (str == null)
                dto.setSafttaxorgcode(null);
             else
                dto.setSafttaxorgcode(str.trim());

             //C_AFTBDGLEVEL
             str = rs.getString("C_AFTBDGLEVEL");
             if (str == null)
                dto.setCaftbdglevel(null);
             else
                dto.setCaftbdglevel(str.trim());

             //S_AFTBDGSBTCODE
             str = rs.getString("S_AFTBDGSBTCODE");
             if (str == null)
                dto.setSaftbdgsbtcode(null);
             else
                dto.setSaftbdgsbtcode(str.trim());

             //C_AFTBDGTYPE
             str = rs.getString("C_AFTBDGTYPE");
             if (str == null)
                dto.setCaftbdgtype(null);
             else
                dto.setCaftbdgtype(str.trim());

             //S_AFTASTFLAG
             str = rs.getString("S_AFTASTFLAG");
             if (str == null)
                dto.setSaftastflag(null);
             else
                dto.setSaftastflag(str.trim());

             //F_JOINDIVIDERATE
           dto.setFjoindividerate(rs.getBigDecimal("F_JOINDIVIDERATE"));

             //C_GOVERNRALATION
             str = rs.getString("C_GOVERNRALATION");
             if (str == null)
                dto.setCgovernralation(null);
             else
                dto.setCgovernralation(str.trim());

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
        TpShareDivideDto dto = (TpShareDivideDto)_dto ;
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

            //I_DIVIDEGRPID
            if (dto.getIdividegrpid()==null)
                ps.setNull(pos, java.sql.Types.INTEGER);
            else
                ps.setInt(pos, dto.getIdividegrpid().intValue());
            pos++;

            //S_ROOTTRECODE
            ps.setString(pos, dto.getSroottrecode());
            pos++;

            //S_TRATRECODE
            ps.setString(pos, dto.getStratrecode());
            pos++;

            //S_PAYEETRECODE
            ps.setString(pos, dto.getSpayeetrecode());
            pos++;

            //S_ROOTTAXORGCODE
            ps.setString(pos, dto.getSroottaxorgcode());
            pos++;

            //S_ROOTBDGSBTCODE
            ps.setString(pos, dto.getSrootbdgsbtcode());
            pos++;

            //C_ROOTBDGKIND
            ps.setString(pos, dto.getCrootbdgkind());
            pos++;

            //S_ROOTASTFLAG
            ps.setString(pos, dto.getSrootastflag());
            pos++;

            //S_AFTTRECODE
            ps.setString(pos, dto.getSafttrecode());
            pos++;

            //S_AFTTAXORGCODE
            ps.setString(pos, dto.getSafttaxorgcode());
            pos++;

            //C_AFTBDGLEVEL
            ps.setString(pos, dto.getCaftbdglevel());
            pos++;

            //S_AFTBDGSBTCODE
            ps.setString(pos, dto.getSaftbdgsbtcode());
            pos++;

            //C_AFTBDGTYPE
            ps.setString(pos, dto.getCaftbdgtype());
            pos++;

            //S_AFTASTFLAG
            ps.setString(pos, dto.getSaftastflag());
            pos++;

            //F_JOINDIVIDERATE
            ps.setBigDecimal(pos, dto.getFjoindividerate());
            pos++;

            //C_GOVERNRALATION
            ps.setString(pos, dto.getCgovernralation());
            pos++;

            //TS_SYSUPDATE

           //I_PARAMSEQNO
           ps.setInt(pos, dto.getIparamseqno().intValue());
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
     
     	 TpShareDivideDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (TpShareDivideDto)_dtos[i] ; 
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
                dto  = (TpShareDivideDto)_dtos[i] ; 
                int pos = 1;
                //S_BOOKORGCODE
                 ps.setString(pos, dto.getSbookorgcode());
                pos++;

                //C_TRIMFLAG
                 ps.setString(pos, dto.getCtrimflag());
                pos++;

                //I_DIVIDEGRPID
                 if (dto.getIdividegrpid()==null)
                   ps.setNull(pos, java.sql.Types.INTEGER);
                else
                   ps.setInt(pos, dto.getIdividegrpid().intValue());
                pos++ ;

                //S_ROOTTRECODE
                 ps.setString(pos, dto.getSroottrecode());
                pos++;

                //S_TRATRECODE
                 ps.setString(pos, dto.getStratrecode());
                pos++;

                //S_PAYEETRECODE
                 ps.setString(pos, dto.getSpayeetrecode());
                pos++;

                //S_ROOTTAXORGCODE
                 ps.setString(pos, dto.getSroottaxorgcode());
                pos++;

                //S_ROOTBDGSBTCODE
                 ps.setString(pos, dto.getSrootbdgsbtcode());
                pos++;

                //C_ROOTBDGKIND
                 ps.setString(pos, dto.getCrootbdgkind());
                pos++;

                //S_ROOTASTFLAG
                 ps.setString(pos, dto.getSrootastflag());
                pos++;

                //S_AFTTRECODE
                 ps.setString(pos, dto.getSafttrecode());
                pos++;

                //S_AFTTAXORGCODE
                 ps.setString(pos, dto.getSafttaxorgcode());
                pos++;

                //C_AFTBDGLEVEL
                 ps.setString(pos, dto.getCaftbdglevel());
                pos++;

                //S_AFTBDGSBTCODE
                 ps.setString(pos, dto.getSaftbdgsbtcode());
                pos++;

                //C_AFTBDGTYPE
                 ps.setString(pos, dto.getCaftbdgtype());
                pos++;

                //S_AFTASTFLAG
                 ps.setString(pos, dto.getSaftastflag());
                pos++;

                //F_JOINDIVIDERATE
                 ps.setBigDecimal(pos, dto.getFjoindividerate());
                pos++;

                //C_GOVERNRALATION
                 ps.setString(pos, dto.getCgovernralation());
                pos++;

                //TS_SYSUPDATE
 
               //I_PARAMSEQNO
               ps.setInt(pos, dto.getIparamseqno().intValue());
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
       TpShareDividePK pk = (TpShareDividePK)_pk ;


       PreparedStatement ps = null;
       try {
           ps = conn.prepareStatement(SQL_DELETE);
           ps.setInt(1, pk.getIparamseqno().intValue());
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
        TpShareDividePK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TpShareDividePK)_pks[i] ; 
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
       			pk  = (TpShareDividePK)(pks.get(i)) ; 
                ps.setInt(1, pk.getIparamseqno().intValue());
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
                                                         		throw new SQLException("数据库表：TP_SHARE_DIVIDE没有检查修改的字段");
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

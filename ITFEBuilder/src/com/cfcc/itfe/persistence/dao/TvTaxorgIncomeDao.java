    



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

import com.cfcc.itfe.persistence.dto.TvTaxorgIncomeDto ;
import com.cfcc.itfe.persistence.pk.TvTaxorgIncomePK ;


/**
 * <p>Title: DAO of table: TV_TAXORG_INCOME</p>
 * <p>Description: Data Access Object  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: CFCC </p>
 * <p>Generation Time: 2014-12-25 15:29:04 </p>
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

public class TvTaxorgIncomeDao  implements IDao
{


    /* SQL to insert data */
    private static final String SQL_INSERT =
        "INSERT INTO TV_TAXORG_INCOME ("
          + "S_TAXORGCODE,S_TRECODE,S_INTREDATE,I_PKGSEQNO,S_EXPVOUNO"
          + ",S_EXPVOUTYPE,C_BDGLEVEL,S_BDGSBTCODE,C_BDGKIND,F_AMT"
          + ",C_VOUCHANNEL"
        + ") VALUES ("
        + "?,?,?,?,?,?,?,?,?,?,?)";


    private static final String SQL_INSERT_WITH_RESULT = "SELECT * FROM FINAL TABLE( " + SQL_INSERT + " )";
    
    /* SQL to select data */
    private static final String SQL_SELECT =
        "SELECT "
        + "TV_TAXORG_INCOME.S_TAXORGCODE, TV_TAXORG_INCOME.S_TRECODE, TV_TAXORG_INCOME.S_INTREDATE, TV_TAXORG_INCOME.I_PKGSEQNO, TV_TAXORG_INCOME.S_EXPVOUNO, "
        + "TV_TAXORG_INCOME.S_EXPVOUTYPE, TV_TAXORG_INCOME.C_BDGLEVEL, TV_TAXORG_INCOME.S_BDGSBTCODE, TV_TAXORG_INCOME.C_BDGKIND, TV_TAXORG_INCOME.F_AMT, "
        + "TV_TAXORG_INCOME.C_VOUCHANNEL "
        + "FROM TV_TAXORG_INCOME "
        +" WHERE " 
        + "S_TAXORGCODE = ? AND S_EXPVOUNO = ?"
        ;
    /* SQL to select for update data */
    private static final String SQL_SELECT_FOR_UPDATE =
        "SELECT "
        + "TV_TAXORG_INCOME.S_TAXORGCODE, TV_TAXORG_INCOME.S_TRECODE, TV_TAXORG_INCOME.S_INTREDATE, TV_TAXORG_INCOME.I_PKGSEQNO, TV_TAXORG_INCOME.S_EXPVOUNO, "
        + "TV_TAXORG_INCOME.S_EXPVOUTYPE, TV_TAXORG_INCOME.C_BDGLEVEL, TV_TAXORG_INCOME.S_BDGSBTCODE, TV_TAXORG_INCOME.C_BDGKIND, TV_TAXORG_INCOME.F_AMT, "
        + "TV_TAXORG_INCOME.C_VOUCHANNEL "
        + "FROM TV_TAXORG_INCOME "
        +" WHERE " 
        + "S_TAXORGCODE = ? AND S_EXPVOUNO = ? FOR UPDATE"
        ;

      /* SQL to select data (SCROLLABLE)*/
     private static final String SQL_SELECT_BATCH_SCROLLABLE =
        "SELECT "
        + "  TV_TAXORG_INCOME.S_TAXORGCODE  , TV_TAXORG_INCOME.S_TRECODE  , TV_TAXORG_INCOME.S_INTREDATE  , TV_TAXORG_INCOME.I_PKGSEQNO  , TV_TAXORG_INCOME.S_EXPVOUNO "
        + " , TV_TAXORG_INCOME.S_EXPVOUTYPE  , TV_TAXORG_INCOME.C_BDGLEVEL  , TV_TAXORG_INCOME.S_BDGSBTCODE  , TV_TAXORG_INCOME.C_BDGKIND  , TV_TAXORG_INCOME.F_AMT "
        + " , TV_TAXORG_INCOME.C_VOUCHANNEL "
        + "FROM TV_TAXORG_INCOME ";



    /* SQL to select batch data */
    private static final String SQL_SELECT_BATCH =
        "SELECT "
        + "TV_TAXORG_INCOME.S_TAXORGCODE, TV_TAXORG_INCOME.S_TRECODE, TV_TAXORG_INCOME.S_INTREDATE, TV_TAXORG_INCOME.I_PKGSEQNO, TV_TAXORG_INCOME.S_EXPVOUNO, "
        + "TV_TAXORG_INCOME.S_EXPVOUTYPE, TV_TAXORG_INCOME.C_BDGLEVEL, TV_TAXORG_INCOME.S_BDGSBTCODE, TV_TAXORG_INCOME.C_BDGKIND, TV_TAXORG_INCOME.F_AMT, "
        + "TV_TAXORG_INCOME.C_VOUCHANNEL "
        + "FROM TV_TAXORG_INCOME " ;
        

   /* SQL to select batch data where condition  */
    private static final String SQL_SELECT_BATCH_WHERE =" ( "
        + "S_TAXORGCODE = ? AND S_EXPVOUNO = ?)"
        ;


    /* SQL to update data */
    private static final String SQL_UPDATE =
        "UPDATE TV_TAXORG_INCOME SET "
        + "S_TRECODE =?,S_INTREDATE =?,I_PKGSEQNO =?,S_EXPVOUTYPE =?,C_BDGLEVEL =?, "
        + "S_BDGSBTCODE =?,C_BDGKIND =?,F_AMT =?,C_VOUCHANNEL =? "
        + "WHERE "
        + "S_TAXORGCODE = ? AND S_EXPVOUNO = ?"
        ;

	/* SQL to update data, support LOB */
    private static final String SQL_UPDATE_LOB =
        "UPDATE TV_TAXORG_INCOME SET "
        + "S_TRECODE =?, S_INTREDATE =?, I_PKGSEQNO =?, S_EXPVOUTYPE =?, C_BDGLEVEL =?,  "
        + "S_BDGSBTCODE =?, C_BDGKIND =?, F_AMT =?, C_VOUCHANNEL =? "
        + "WHERE "
        + "S_TAXORGCODE = ? AND S_EXPVOUNO = ?"
        ;	
	
    /* SQL to delete data */
    private static final String SQL_DELETE =
        "DELETE FROM TV_TAXORG_INCOME " 
        + " WHERE "
        + "S_TAXORGCODE = ? AND S_EXPVOUNO = ?"
        ;


	/**
	*  ������ѯ һ������ѯ�Ĳ�������
	*/
	
	public static final int FIND_BATCH_SIZE = 150  / 2;
	



   /**
   * Create a new record in Database.
   */
  public void create(IDto _dto,  Connection conn) throws SQLException
  {
     TvTaxorgIncomeDto dto = (TvTaxorgIncomeDto)_dto ;
     String msgValid = dto.checkValid() ;
     if (msgValid != null)
     	throw new SQLException("�������"+msgValid) ;

     PreparedStatement ps = null;
     try
     {
         ps = conn.prepareStatement(SQL_INSERT);
          ps.setString(1, dto.getStaxorgcode());

          ps.setString(2, dto.getStrecode());

          ps.setString(3, dto.getSintredate());

          ps.setString(4, dto.getIpkgseqno());

          ps.setString(5, dto.getSexpvouno());

          ps.setString(6, dto.getSexpvoutype());

          ps.setString(7, dto.getCbdglevel());

          ps.setString(8, dto.getSbdgsbtcode());

          ps.setString(9, dto.getCbdgkind());

          ps.setBigDecimal(10, dto.getFamt());

          ps.setString(11, dto.getCvouchannel());

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
       TvTaxorgIncomeDto dto = (TvTaxorgIncomeDto)_dto ;
       String msgValid = dto.checkValid() ;
       if (msgValid != null)
           throw new SQLException("�������"+msgValid) ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try
       {
           ps = conn.prepareStatement(SQL_INSERT_WITH_RESULT);
            ps.setString(1, dto.getStaxorgcode());
            ps.setString(2, dto.getStrecode());
            ps.setString(3, dto.getSintredate());
            ps.setString(4, dto.getIpkgseqno());
            ps.setString(5, dto.getSexpvouno());
            ps.setString(6, dto.getSexpvoutype());
            ps.setString(7, dto.getCbdglevel());
            ps.setString(8, dto.getSbdgsbtcode());
            ps.setString(9, dto.getCbdgkind());
            ps.setBigDecimal(10, dto.getFamt());
            ps.setString(11, dto.getCvouchannel());
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
        TvTaxorgIncomeDto dto  ;
     	for (int i= 0; i< _dtos.length ; i++)
 	    {
 		    dto  = (TvTaxorgIncomeDto)_dtos[i] ; 
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
 	    	    dto  = (TvTaxorgIncomeDto)_dtos[i] ; 
  
               ps.setString(1, dto.getStaxorgcode());
  
               ps.setString(2, dto.getStrecode());
  
               ps.setString(3, dto.getSintredate());
  
               ps.setString(4, dto.getIpkgseqno());
  
               ps.setString(5, dto.getSexpvouno());
  
               ps.setString(6, dto.getSexpvoutype());
  
               ps.setString(7, dto.getCbdglevel());
  
               ps.setString(8, dto.getSbdgsbtcode());
  
               ps.setString(9, dto.getCbdgkind());
  
               ps.setBigDecimal(10, dto.getFamt());
  
               ps.setString(11, dto.getCvouchannel());
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
        	
       TvTaxorgIncomePK pk = (TvTaxorgIncomePK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT);
           ps.setString(1, pk.getStaxorgcode());

           ps.setString(2, pk.getSexpvouno());

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
        	
       TvTaxorgIncomePK pk = (TvTaxorgIncomePK)_pk ;

       PreparedStatement ps = null;
       ResultSet rs = null;
       try {
           ps = conn.prepareStatement(SQL_SELECT_FOR_UPDATE);
           ps.setString(1, pk.getStaxorgcode());

           ps.setString(2, pk.getSexpvouno());

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
        TvTaxorgIncomePK pk ;
        
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TvTaxorgIncomePK)_pks[i] ; 
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
                    pk  = (TvTaxorgIncomePK)(pks.get(i)) ; 
                   ps.setString((i-iBegin)*2+1, pk.getStaxorgcode());

                   ps.setString((i-iBegin)*2+2, pk.getSexpvouno());

			
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
            TvTaxorgIncomeDto[] dtos = new TvTaxorgIncomeDto[0];
		    dtos = (TvTaxorgIncomeDto[]) results.toArray(dtos) ;
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
             TvTaxorgIncomeDto  dto = new  TvTaxorgIncomeDto ();
             //S_TAXORGCODE
             str = rs.getString("S_TAXORGCODE");
             if (str == null)
                dto.setStaxorgcode(null);
             else
                dto.setStaxorgcode(str.trim());

             //S_TRECODE
             str = rs.getString("S_TRECODE");
             if (str == null)
                dto.setStrecode(null);
             else
                dto.setStrecode(str.trim());

             //S_INTREDATE
             str = rs.getString("S_INTREDATE");
             if (str == null)
                dto.setSintredate(null);
             else
                dto.setSintredate(str.trim());

             //I_PKGSEQNO
             str = rs.getString("I_PKGSEQNO");
             if (str == null)
                dto.setIpkgseqno(null);
             else
                dto.setIpkgseqno(str.trim());

             //S_EXPVOUNO
             str = rs.getString("S_EXPVOUNO");
             if (str == null)
                dto.setSexpvouno(null);
             else
                dto.setSexpvouno(str.trim());

             //S_EXPVOUTYPE
             str = rs.getString("S_EXPVOUTYPE");
             if (str == null)
                dto.setSexpvoutype(null);
             else
                dto.setSexpvoutype(str.trim());

             //C_BDGLEVEL
             str = rs.getString("C_BDGLEVEL");
             if (str == null)
                dto.setCbdglevel(null);
             else
                dto.setCbdglevel(str.trim());

             //S_BDGSBTCODE
             str = rs.getString("S_BDGSBTCODE");
             if (str == null)
                dto.setSbdgsbtcode(null);
             else
                dto.setSbdgsbtcode(str.trim());

             //C_BDGKIND
             str = rs.getString("C_BDGKIND");
             if (str == null)
                dto.setCbdgkind(null);
             else
                dto.setCbdgkind(str.trim());

             //F_AMT
           dto.setFamt(rs.getBigDecimal("F_AMT"));

             //C_VOUCHANNEL
             str = rs.getString("C_VOUCHANNEL");
             if (str == null)
                dto.setCvouchannel(null);
             else
                dto.setCvouchannel(str.trim());



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
        TvTaxorgIncomeDto dto = (TvTaxorgIncomeDto)_dto ;
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

            //S_INTREDATE
            ps.setString(pos, dto.getSintredate());
            pos++;

            //I_PKGSEQNO
            ps.setString(pos, dto.getIpkgseqno());
            pos++;

            //S_EXPVOUTYPE
            ps.setString(pos, dto.getSexpvoutype());
            pos++;

            //C_BDGLEVEL
            ps.setString(pos, dto.getCbdglevel());
            pos++;

            //S_BDGSBTCODE
            ps.setString(pos, dto.getSbdgsbtcode());
            pos++;

            //C_BDGKIND
            ps.setString(pos, dto.getCbdgkind());
            pos++;

            //F_AMT
            ps.setBigDecimal(pos, dto.getFamt());
            pos++;

            //C_VOUCHANNEL
            ps.setString(pos, dto.getCvouchannel());
            pos++;


           //S_TAXORGCODE
           ps.setString(pos, dto.getStaxorgcode());
           pos++;
           //S_EXPVOUNO
           ps.setString(pos, dto.getSexpvouno());
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
     
     	 TvTaxorgIncomeDto dto  ;
         for (int i= 0; i< _dtos.length ; i++)
         {
            dto  = (TvTaxorgIncomeDto)_dtos[i] ; 
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
                dto  = (TvTaxorgIncomeDto)_dtos[i] ; 
                int pos = 1;
                //S_TRECODE
                 ps.setString(pos, dto.getStrecode());
                pos++;

                //S_INTREDATE
                 ps.setString(pos, dto.getSintredate());
                pos++;

                //I_PKGSEQNO
                 ps.setString(pos, dto.getIpkgseqno());
                pos++;

                //S_EXPVOUTYPE
                 ps.setString(pos, dto.getSexpvoutype());
                pos++;

                //C_BDGLEVEL
                 ps.setString(pos, dto.getCbdglevel());
                pos++;

                //S_BDGSBTCODE
                 ps.setString(pos, dto.getSbdgsbtcode());
                pos++;

                //C_BDGKIND
                 ps.setString(pos, dto.getCbdgkind());
                pos++;

                //F_AMT
                 ps.setBigDecimal(pos, dto.getFamt());
                pos++;

                //C_VOUCHANNEL
                 ps.setString(pos, dto.getCvouchannel());
                pos++;


               //S_TAXORGCODE
               ps.setString(pos, dto.getStaxorgcode());
               pos++;
               //S_EXPVOUNO
               ps.setString(pos, dto.getSexpvouno());
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
       TvTaxorgIncomePK pk = (TvTaxorgIncomePK)_pk ;


       PreparedStatement ps = null;
       try {
           ps = conn.prepareStatement(SQL_DELETE);
           ps.setString(1, pk.getStaxorgcode());
           ps.setString(2, pk.getSexpvouno());
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
        TvTaxorgIncomePK pk ;
        List pks = new ArrayList();  
	  
		for (int i= 0; i< _pks.length ; i++)
 		{
 			pk  = (TvTaxorgIncomePK)_pks[i] ; 
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
       			pk  = (TvTaxorgIncomePK)(pks.get(i)) ; 
                ps.setString(1, pk.getStaxorgcode());
                ps.setString(2, pk.getSexpvouno());
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
                           		throw new SQLException("���ݿ����TV_TAXORG_INCOMEû�м���޸ĵ��ֶ�");
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
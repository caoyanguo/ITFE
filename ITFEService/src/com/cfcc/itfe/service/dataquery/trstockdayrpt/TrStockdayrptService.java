package com.cfcc.itfe.service.dataquery.trstockdayrpt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.jdbc.odbc.ee.CommonDataSource;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.HtrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptReportDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.CommonDataAccessService;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.CommonQto;
import com.cfcc.jaf.persistence.util.SqlUtil;
/**
 * @author t60
 * @time   12-02-24 10:44:47
 * codecomment: 
 */
@SuppressWarnings("unchecked")
public class TrStockdayrptService extends AbstractTrStockdayrptService {
	private static Log log = LogFactory.getLog(TrStockdayrptService.class);	

	/**
	 * 增加	 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
    public IDto addInfo(IDto dtoInfo) throws ITFEBizException {
      return null;
    }

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void delInfo(IDto dtoInfo) throws ITFEBizException {
      
    }

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void modInfo(IDto dtoInfo) throws ITFEBizException {
      
    }

    /**
     * 查询某段时间某账户本日余额累计
     */
	public List findTotalBalForExport(IDto dtoInfo, List finddtolist,
			String startDate, String endDate) throws ITFEBizException {
		TrStockdayrptDto dto = (TrStockdayrptDto)dtoInfo;
		HtrStockdayrptDto hdto = new HtrStockdayrptDto();
		hdto.setSorgcode(dto.getSorgcode());
		hdto.setStrecode(dto.getStrecode());
		hdto.setSrptdate(dto.getSrptdate());
		StringBuffer sqlBuffer = new StringBuffer();
		String where="";
		List list = new ArrayList();
		try {
			SQLExecutor sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			//财政代码为空，国库代码为空，查全局
			if((dto.getStrecode()==null || "".equals(dto.getStrecode())) && (dto.getSorgcode()==null || "".equals(dto.getSorgcode())) && finddtolist !=null && finddtolist.size()!=0 ){
				
				where+=" and ( S_TRECODE='"+((TsConvertfinorgDto)finddtolist.get(0)).getStrecode()+"' ";
				for(int i = 1 ;i<finddtolist.size();i++){
					where += " or S_TRECODE= '"+((TsConvertfinorgDto)finddtolist.get(i)).getStrecode()+"' ";
				}
				where+=" ) ";
			}
			
			sqlBuffer.append("SELECT s_accno AS s_accno,sum (N_MONEYTODAY) AS n_totalmoneytoday "+
							   "FROM ( (SELECT S_ACCNO, N_MONEYTODAY "+
							             "FROM TR_STOCKDAYRPT "+
							             "WHERE S_RPTDATE >= '"+startDate+"' AND S_RPTDATE <= '"+endDate+"' ");
			sqlBuffer.append(where);
			if(dto.getSorgcode() != null && !dto.getSorgcode().trim().equals("")){
				sqlBuffer.append(" and S_ORGCODE = ? ");
				sqlExec.addParam(dto.getSorgcode().trim());
			}
			if(dto.getStrecode() != null && !dto.getStrecode().trim().equals("")){
				sqlBuffer.append(" and S_TRECODE = ? ");
				sqlExec.addParam(dto.getStrecode().trim());
			}
//			if(dto.getSrptdate() != null && !dto.getSrptdate().trim().equals("")){
//				sqlBuffer.append(" and S_RPTDATE = ? ");
//				sqlExec.addParam(dto.getSrptdate().trim());
//			}
			sqlBuffer.append(") UNION ALL ( ");
			
			
			sqlBuffer.append("SELECT S_ACCNO, N_MONEYTODAY "+
					           "FROM HTR_STOCKDAYRPT "+
					          "WHERE S_RPTDATE >= '"+startDate+"' AND S_RPTDATE <= '"+endDate+"' ");
			sqlBuffer.append(where);
			if(dto.getSorgcode() != null && !dto.getSorgcode().trim().equals("")){
				sqlBuffer.append(" and S_ORGCODE = ? ");
				sqlExec.addParam(dto.getSorgcode().trim());
			}
			if(dto.getStrecode() != null && !dto.getStrecode().trim().equals("")){
				sqlBuffer.append(" and S_TRECODE = ? ");
				sqlExec.addParam(dto.getStrecode().trim());
			}
//			if(dto.getSrptdate() != null && !dto.getSrptdate().trim().equals("")){
//				sqlBuffer.append(" and S_RPTDATE = ? ");
//				sqlExec.addParam(dto.getSrptdate().trim());
//			}
			sqlBuffer.append("))");
			sqlBuffer.append("GROUP BY s_accno");
			
			SQLResults rs = sqlExec.runQueryCloseCon(sqlBuffer.toString(),TrStockdayrptReportDto.class);
			if(rs != null){
				list.addAll(rs.getDtoCollection());
			}
			sqlExec.clearParams();
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询本日余额累计数据连接错误！", e);
		}
		
		return list;
	}

	/**
	 * 根据账号取户名：某一段时间，同一个账号可能有不同的户名，因此取最新的户名，首先从当前库取，如果当前库不存在，再从历史库取，如果历史库也不存在，则返回空串
	 */
	public String findAcctName(String acctNo, String startDate, String endDate)
			throws ITFEBizException {
		String sql = "";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sql = "select S_ACCNAME from TR_STOCKDAYRPT where S_ACCNO = ? and S_RPTDATE >= ? and S_RPTDATE <= ? order by S_RPTDATE desc";
			sqlExec.addParam(acctNo);
			sqlExec.addParam(startDate);
			sqlExec.addParam(endDate);
			SQLResults rs = sqlExec.runQuery(sql);
			if(rs != null && rs.getRowCount()>0){
				return rs.getString(0, "S_ACCNAME");
			}else{
				sql = "select S_ACCNAME from HTR_STOCKDAYRPT where S_ACCNO = ? and S_RPTDATE >= ? and S_RPTDATE <= ? order by S_RPTDATE desc";
				sqlExec.clearParams();
				sqlExec.addParam(acctNo);
				sqlExec.addParam(startDate);
				sqlExec.addParam(endDate);
				rs = sqlExec.runQuery(sql);
				if(rs != null && rs.getRowCount()>0){
					return rs.getString(0, "S_ACCNAME");
				}else{
					return "";
				}
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询账户名称数据库错！", e);
		}finally{
			if(sqlExec!=null)
				sqlExec.closeConnection();
		}
	}

	public List findRsByDtoWithWhere(IDto _dto, String where)
			throws ITFEBizException {
		CommonQto qto = SqlUtil.IDto2CommonQto(_dto);
		try {
			if(null != qto) {
				return DatabaseFacade.getODB().find(_dto.getClass(),
						qto.getSWhereClause() + " " + where, qto.getLParams());
			}else {
				return DatabaseFacade.getODB().findWithUR(_dto.getClass(), where);
			}			
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("数据查询异常！", e);
		}
	}
}
package com.cfcc.itfe.util;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
/**
 * 国库信息操作工具类
 * @author Yuan
 *
 */
public class BizTableUtil {
	private static Log log=LogFactory.getLog(BizTableUtil.class);
	
	/**
	 * 根据国库代码获取机构代码
	 * @param strecode
	 * @return
	 * @throws ITFEBizException 
	 */
	public static String getOrgcodeByTrecode(String strecode) throws ITFEBizException{
		SQLExecutor sqlExecutor=null;
		String sorgcode=null;
		try {
			sqlExecutor=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			String sql="SELECT * FROM TS_TREASURY WHERE S_TRECODE=?";
			sqlExecutor.addParam(strecode);
			List<TsTreasuryDto> list=(List<TsTreasuryDto>) sqlExecutor.runQueryCloseCon(sql, TsTreasuryDto.class).getDtoCollection(); 
			if(list!=null&&list.size()>0){
				sorgcode=list.get(0).getSorgcode();
			}
		} catch (JAFDatabaseException e) {
			log.error("查询国库信息时出现异常",e);
			throw new ITFEBizException("查询国库信息时出现异常",e);
		}
		return sorgcode;
	}
	
	
	/**
	 * 根据财政机构代码获取机构代码
	 * @param strecode
	 * @return
	 * @throws ITFEBizException 
	 */
	public static String getOrgcodeByFinorgcode(String sfinorgcode) throws ITFEBizException{
		SQLExecutor sqlExecutor=null;
		String sorgcode=null;
		try {
			sqlExecutor=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			String sql="SELECT * FROM TS_CONVERTFINORG WHERE S_FINORGCODE=?";
			sqlExecutor.addParam(sfinorgcode);
			List<TsConvertfinorgDto> list=(List<TsConvertfinorgDto>) sqlExecutor.runQueryCloseCon(sql, TsConvertfinorgDto.class).getDtoCollection(); 
			if(list!=null&&list.size()>0){
				sorgcode=list.get(0).getSorgcode();
			}
		} catch (JAFDatabaseException e) {
			log.error("查询财政标志参数对照表时出现异常",e);
			throw new ITFEBizException("查询财政标志参数对照表时出现异常",e);
		}
		return sorgcode;
	}
	
	/**
	 * 根据财政机构代码获取国库代码
	 * @param strecode
	 * @return
	 * @throws ITFEBizException 
	 */
	public static String getTrecodeByFinorgcode(String sfinorgcode) throws ITFEBizException{
		SQLExecutor sqlExecutor=null;
		String strecode=null;
		try {
			sqlExecutor=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			String sql="SELECT * FROM TS_CONVERTFINORG WHERE S_FINORGCODE=?";
			sqlExecutor.addParam(sfinorgcode);
			List<TsConvertfinorgDto> list=(List<TsConvertfinorgDto>) sqlExecutor.runQueryCloseCon(sql, TsConvertfinorgDto.class).getDtoCollection(); 
			if(list!=null&&list.size()>0){
				strecode=list.get(0).getStrecode();
			}
		} catch (JAFDatabaseException e) {
			log.error("查询财政标志参数对照表时出现异常",e);
			throw new ITFEBizException("查询财政标志参数对照表时出现异常",e);
		}
		return strecode;
	}
	
	
	
	
}

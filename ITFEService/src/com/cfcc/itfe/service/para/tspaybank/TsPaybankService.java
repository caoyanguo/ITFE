package com.cfcc.itfe.service.para.tspaybank;

import java.util.*;
import java.math.*;

import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.service.para.tspaybank.AbstractTsPaybankService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author caoyg
 * @time   09-10-20 08:42:02
 * codecomment: 
 */

public class TsPaybankService extends AbstractTsPaybankService {
	private static Log log = LogFactory.getLog(TsPaybankService.class);	
	private static final int MAX_NUM = 150000; // 每次取出最大记录数

	/**
	 * 增加	 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
    public IDto addInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
			DatabaseFacade.getDb().create(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("字段行号已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}
		return null;
    }

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void delInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
			CommonFacade.getODB().deleteRsByDto(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		}
    }

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void modInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
			DatabaseFacade.getDb().update(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("字段行号已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		} 
    }
	
	
	public String bankInfoexport(IDto dtoInfo) throws ITFEBizException {
		String sqlwhere = makewhere((TsPaybankDto)dtoInfo);
		String root = ITFECommonConstant.FILE_ROOT_PATH;
		String dirsep = File.separator; // 取得系统分割符
		String strdate = DateUtil.date2String2(new java.util.Date()); // 当前系统年月日
		String filename = "TS_PAYBANK.CSV";
		String fullpath = root + "exportFile" + dirsep + strdate + dirsep
				+ filename;
		String sql = "select S_BANKNO ,S_BANKNAME ,S_PAYBANKNO ,S_PAYBANKNAME,I_MODICOUNT ,S_STATE  FROM TS_PAYBANK "+sqlwhere;
		SQLExecutor sqlExec;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			sqlExec.setMaxRows(MAX_NUM);
			List<TsPaybankDto> list = (List<TsPaybankDto>) sqlExec
					.runQueryCloseCon(sql, TsPaybankDto.class)
					.getDtoCollection();
			String splitSign = ","; // 文件记录分隔符号
			if (list.size() > 0) {
				StringBuffer filebuf = new StringBuffer("行号,名称,清算行,清算行名称,城市代码,状态\r\n");
				for (TsPaybankDto _dto : list) {
					filebuf.append(_dto.getSbankno());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbankname());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSpaybankno());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSpaybankname());
					filebuf.append(splitSign);
					filebuf.append(_dto.getImodicount());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSstate());
					filebuf.append("\r\n");
				}
				File f = new File(fullpath);
				if (f.exists()) {
					FileUtil.getInstance().deleteFiles(fullpath);
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
			    return fullpath.replaceAll(root, "");
				
			} else {
				throw new ITFEBizException("查询无数据");
			}
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("写文件出错",e);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错",e);
		}
	}
	
	/**
	 * 生成查询条件
	 * @return
	 */
	private String makewhere(TsPaybankDto dto){
		String sqlwhere = " where (1=1) ";
		if (null!=dto.getSbankno()) {
			sqlwhere += " and  s_bankno = '"+dto.getSbankno()+"'";
		}
		if (null!=dto.getSbankname()) {
			sqlwhere += " and s_bankname  like '%"+dto.getSbankname()+"%'";
		}
		if (null!=dto.getSpaybankno()) {
			sqlwhere += " and  s_paybankno = '"+dto.getSpaybankno()+"'";
		}
		if (null!=dto.getSpaybankname()) {
			sqlwhere += " and  S_PAYBANKNAME like '%"+dto.getSpaybankname()+"%'";
		}
		if (null!=dto.getImodicount()) {
			sqlwhere += " and  I_MODICOUNT = "+dto.getImodicount()+"";
		}
		if (null!=dto.getSstate()) {
			sqlwhere += " and  s_state = '"+dto.getSstate()+"'";
		}
	    if(null!=dto.getSaddcolumn1() && !"".equals(dto.getSaddcolumn1())) {
			sqlwhere += " and substr(s_bankno,1,3)='"+dto.getSaddcolumn1()+"'";
		}
		return sqlwhere;
		
	}

}
package com.cfcc.itfe.service.dataquery.tbbillstore;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.*;

import com.cfcc.deptone.common.core.exception.FileOperateException;
import com.cfcc.itfe.persistence.dto.TbBillstoreDto;
import com.cfcc.itfe.service.dataquery.tbbillstore.AbstractTbbillstoreService;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * @author Administrator
 * @time 15-06-01 14:41:03 codecomment:
 */

public class TbbillstoreService extends AbstractTbbillstoreService {
	private static Log log = LogFactory.getLog(TbbillstoreService.class);

	/**
	 * 文件下载
	 * 
	 * @generated
	 * @param paramlist
	 * @throws ITFEBizException
	 */
	public void filedownLoad(List paramlist) throws ITFEBizException {
//		CommonFacade.getODB().findrsbydto
	}

	/**
	 * 文件上传
	 * 
	 * @generated
	 * @param paramList
	 * @throws ITFEBizException
	 */
	public void fileupload(List paramList) throws ITFEBizException {
		if (null == paramList || paramList.size() == 0) {
			return;
		}
		try {
			for (TbBillstoreDto tmpdto : (ArrayList<TbBillstoreDto>) paramList) {
				tmpdto.setSseqno(StampFacade.getStampSendSeq("FS"));
				tmpdto.setScontent(FileUtil.getInstance().transformFileToByte(tmpdto.getSfilepath()));
				tmpdto.setSstoretime(TSystemFacade.getDBSystemTime());
				tmpdto.setTsupdate(TSystemFacade.getDBSystemTime());
			}
			DatabaseFacade.getODB().create(CommonUtil.listTArray(paramList));
		} catch (SequenceException e) {
			log.error("获取sequence值失败！");
			throw new ITFEBizException("获取sequence值失败！",e);
		} catch (JAFDatabaseException e) {
			log.error("数据库操作失败！");
			throw new ITFEBizException("数据库操作失败！",e);
		}  catch (com.cfcc.itfe.exception.FileOperateException e) {
			log.error("文件转换失败！");
			throw new ITFEBizException("文件转换失败！",e);
		}

	}

	/**
	 * 查询上传报表信息
	 	 
	 * @generated
	 * @param paramList
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
	public List searchFileInfo(List paramList) throws ITFEBizException {
		// TODO Auto-generated method stub
		String sql = "SELECT S_SEQNO,S_BOOKORGCODE,S_TRECODE,D_BILLDATE,S_YEAR,S_FILENAME,S_FILETYPE,S_FILEPATH FROM TB_BILLSTORE WHERE S_TRECODE = ? AND D_BILLDATE BETWEEN ? AND ? ";
		try {
			SQLExecutor sqlExecutor = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExecutor.addParam(paramList.get(0));
			sqlExecutor.addParam(paramList.get(1));
			sqlExecutor.addParam(paramList.get(2));
			SQLResults sqlResults = sqlExecutor.runQueryCloseCon(sql);
			if(null == sqlResults || sqlResults.getRowCount() <= 0){
				return null;
			}
			List resultList = new ArrayList();
			TbBillstoreDto tmpDto ;
			for(int i = 0; i < sqlResults.getRowCount() ; i ++ ){
				tmpDto = new TbBillstoreDto();
				tmpDto.setSseqno(sqlResults.getString(i, "S_SEQNO"));
				tmpDto.setStrecode(sqlResults.getString(i, "S_TRECODE"));
				tmpDto.setSbookorgcode(sqlResults.getString(i, "S_BOOKORGCODE"));
				tmpDto.setDbilldate(sqlResults.getDate(i, "D_BILLDATE"));
				tmpDto.setSyear(sqlResults.getString(i, "S_YEAR"));
				tmpDto.setSfilename(sqlResults.getString(i, "S_FILENAME"));
				tmpDto.setSfiletype(sqlResults.getString(i, "S_FILETYPE"));
				tmpDto.setSfilepath(sqlResults.getString(i, "S_FILEPATH"));
				resultList.add(tmpDto);
			}
			return resultList;
		} catch (JAFDatabaseException e) {
			log.error("查询数据库失败！", e);
			return null;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
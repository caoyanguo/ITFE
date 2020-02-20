package com.cfcc.itfe.service.recbiz.tfunitrecordmain;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TfUnitrecordCustomDto;
import com.cfcc.itfe.persistence.dto.TfUnitrecordmainDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * @author Administrator
 * @time   14-10-09 11:17:45
 * codecomment: 
 */

public class TfUnitrecordmainService extends AbstractTfUnitrecordmainService {
	private static Log log = LogFactory.getLog(TfUnitrecordmainService.class);	
	private static final int MAX_NUM = 150000; // 每次取出最大记录数


	/**
	 * 导出法人代码信息
	 	 
	 * @generated
	 * @param dtoInfo
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    public String legalPersonCodeExport(IDto dtoInfo) throws ITFEBizException {
    	TfUnitrecordmainDto mainDto = (TfUnitrecordmainDto)dtoInfo;
    	String sqlWhere = genWhere(mainDto);
    	
    	String root = ITFECommonConstant.FILE_ROOT_PATH;
		String dirsep = File.separator; // 取得系统分割符
		String strdate = DateUtil.date2String2(new java.util.Date()); // 当前系统年月日
		String filename = getLoginInfo().getSorgcode()+"_法人代码维护_正常期.CSV";
		String fullpath = root + "exportFile" + dirsep + strdate + dirsep + filename;
		String sql = "SELECT S_ORGCODE ,S_TRECODE ,S_AGENCYCODE ,S_AGENCYNAME FROM TF_UNITRECORDMAIN a,TF_UNITRECORDSUB b where a.I_VOUSRLNO = b.I_VOUSRLNO AND b.S_FUNDTYPECODE = '1' "+sqlWhere;
		SQLExecutor sqlExec;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.setMaxRows(MAX_NUM);
			List<TfUnitrecordCustomDto> list = (List<TfUnitrecordCustomDto>) sqlExec.runQueryCloseCon(sql, TfUnitrecordCustomDto.class).getDtoCollection();
			String splitSign = ","; // 文件记录分隔符号
			if (list.size() > 0) {
				StringBuffer filebuf = new StringBuffer("核算主体代码,国库主体代码,法人代码,法人名称,法人简码,能否实拨资金,单位性质,注册类型,行业性质,账户数\r\n");
				for (TfUnitrecordCustomDto _dto : list) {
					filebuf.append(_dto.getSorgcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getStrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSagencycode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSagencyname());
					filebuf.append(splitSign);
					filebuf.append("");
					filebuf.append(splitSign);
					filebuf.append("1");
					filebuf.append(splitSign);
					filebuf.append("N");
					filebuf.append(splitSign);
					filebuf.append("N");
					filebuf.append(splitSign);
					filebuf.append("N");
					filebuf.append(splitSign);
					filebuf.append(" ");
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
	private String genWhere(TfUnitrecordmainDto dto){
		String sqlwhere = "";
		if (null!=dto.getSadmdivcode()) {
			sqlwhere += " AND  a.S_ADMDIVCODE = '"+dto.getSadmdivcode()+"'";
		}
		if (null!=dto.getSstyear()) {
			sqlwhere += " AND a.S_STYEAR  = '"+dto.getSstyear()+"'";
		}
		if (null!=dto.getSvtcode()) {
			sqlwhere += " AND  a.S_VTCODE = '"+dto.getSvtcode()+"'";
		}
		if (null!=dto.getSvoudate()) {
			sqlwhere += " AND  a.S_VOUDATE = '"+dto.getSvoudate()+"'";
		}
		if (null!=dto.getSvoucherno()) {
			sqlwhere += " AND  a.S_VOUCHERNO = "+dto.getSvoucherno()+"";
		}
		if (null!=dto.getStrecode()) {
			sqlwhere += " AND  a.S_TRECODE = '"+dto.getStrecode()+"'";
		}
		if (null!=dto.getSfinorgcode()) {
			sqlwhere += " AND  a.S_FINORGCODE = '"+dto.getSfinorgcode()+"'";
		}
		if (null!=dto.getSpaybankcode()) {
			sqlwhere += " AND  a.S_PAYBANKCODE = '"+dto.getSpaybankcode()+"'";
		}
		if (null!=dto.getSpaybankname()) {
			sqlwhere += " AND  a.S_PAYBANKNAME = '"+dto.getSpaybankname()+"'";
		}
		if (null!=dto.getSallflag()) {
			sqlwhere += " AND  a.S_ALLFLAG = '"+dto.getSallflag()+"'";
		}
		return sqlwhere;
		
	}
}
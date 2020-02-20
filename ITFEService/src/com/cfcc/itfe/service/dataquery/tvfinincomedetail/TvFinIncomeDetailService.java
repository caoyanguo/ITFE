package com.cfcc.itfe.service.dataquery.tvfinincomedetail;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvFinIncomeDetailDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeonlineDivideDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeonlineDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.datamove.DataMoveUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.CommonQto;
import com.cfcc.jaf.persistence.util.SqlUtil;

/**
 * @author t60
 * @time   12-02-24 10:44:46
 * codecomment: 
 */

public class TvFinIncomeDetailService extends AbstractTvFinIncomeDetailService {
	private static Log log = LogFactory.getLog(TvFinIncomeDetailService.class);	

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
     * 导出入库流水
     */
    public String makeDivide(List excheckList, String where) throws ITFEBizException {
    	SQLExecutor exec = null;
    	try {
	    	if(null != excheckList && excheckList.size() >0) {
	    		TvFinIncomeDetailDto finddto = (TvFinIncomeDetailDto)excheckList.get(0);
	    		String sql = "select t.* from (select * from TV_FIN_INCOME_DETAIL union all select * from HTV_FIN_INCOME_DETAIL) as t ";
	    		exec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
	    		exec.setMaxRows(1000000);
	    		CommonQto qto = SqlUtil.IDto2CommonQto(finddto);
				if (qto != null) {
					if (where != null) {
						sql += qto.getSWhereClause() + " and " + where;
					} else {
						sql += qto.getSWhereClause();
					}
					exec.addParam(qto.getLParams());
				} else {
					if (where != null) {
						sql += " where " + where;
					}
				}
	    		
	    		SQLResults res = exec.runQueryCloseCon(sql, TvFinIncomeDetailDto.class);
	    		List<TvFinIncomeDetailDto> reslist = (List<TvFinIncomeDetailDto>)res.getDtoCollection();
	    		
	    		String root = ITFECommonConstant.FILE_ROOT_PATH;
				String dirsep = File.separator; // 取得系统分割符
				String strdate = DateUtil.date2String2(new Date()); // 当前系统年月日
				String dateStr = "";
				if(StringUtils.isBlank(finddto.getSintredate())){
					dateStr = TimeFacade.getCurrentStringTime();
				}else{
					dateStr = finddto.getSintredate();
				}
				String fileName = "3139入库流水("+dateStr+").txt";
				String fullpath = root + "exportFile" + dirsep + strdate + dirsep + fileName;
	    		
	    		if(null != reslist && reslist.size() > 0) {
	    			String title="财政机关代码,国库代码,导入委托日期,包流水号,凭证编号,导出凭证类型, 预算级次,预算科目代码,预算种类,金额,凭证来源,征收机关代码,业务流水号";
					StringBuffer filebuf = new StringBuffer(title+"\r\n");
					String splitSign = ",";
			        for (TvFinIncomeDetailDto _dto :reslist) {
			        	filebuf.append(_dto.getSorgcode().trim());filebuf.append(splitSign);
			        	filebuf.append(_dto.getStrecode());filebuf.append(splitSign);
			        	filebuf.append(_dto.getSintredate());filebuf.append(splitSign);
			        	filebuf.append(_dto.getIpkgseqno());filebuf.append(splitSign);
			        	filebuf.append(_dto.getSexpvouno());filebuf.append(splitSign);
			        	filebuf.append(_dto.getSexpvoutype());filebuf.append(splitSign);
			        	filebuf.append(_dto.getCbdglevel());filebuf.append(splitSign);
			        	filebuf.append(_dto.getSbdgsbtcode());filebuf.append(splitSign);
			        	filebuf.append(_dto.getCbdgkind());filebuf.append(splitSign);
			        	filebuf.append(_dto.getFamt());filebuf.append(splitSign);
			        	filebuf.append(_dto.getCvouchannel());filebuf.append(splitSign);
			        	filebuf.append(_dto.getStaxorgcode());filebuf.append(splitSign);
			        	filebuf.append(_dto.getSseq());
			        	filebuf.append("\r\n");
					}
				
					File f = new File(fullpath);
					if (f.exists()) {
						FileUtil.getInstance().deleteFiles(fullpath);
					}
					FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
					
				    return fullpath.replaceAll(root, "");
	    		}
	    	}
    	} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("写文件出错",e);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错",e);
		} finally {
			if(null != exec) {
				exec.closeConnection();
			}
		}
		return null;
    }

	/*public String makeDivide(List excheckList, String reportdate)
			throws ITFEBizException {
		
		List<TvFinIncomeonlineDto> list = new ArrayList<TvFinIncomeonlineDto>();
		
		try {
			//查询选中的入库流水数据是否有电子税票信息
			for(TvFinIncomeDetailDto dto: (List<TvFinIncomeDetailDto>)excheckList){
				
				String where = " WHERE F_TRAAMT="+dto.getFamt().toString().trim()+" and S_TAXVOUNO='"+dto.getSexpvouno().trim()+"' and S_TRECODE='"+dto.getStrecode().trim()+
				"' and S_APPLYDATE='"+dto.getSintredate().trim()+"' and S_TAXORGCODE='"+dto.getStaxorgcode().trim()+"' ";
				
				List detaildtolist = DatabaseFacade.getDb().find(TvFinIncomeonlineDto.class, where);
				
				if(null == detaildtolist || 0 ==detaildtolist.size() ){
					continue;
				}
				else{
					TvFinIncomeonlineDto o= (TvFinIncomeonlineDto)detaildtolist.get(0);
					list.add(o);
				}
			}
		} catch (JAFDatabaseException e) {
			log.error("手动处理共享分成数据转换时错误!", e);
			throw new ITFEBizException("手动处理共享分成数据转换时错误!", e);
		}
		return null;
//		return DataMoveUtil.makeDivide_(list,1,reportdate,this.getLoginInfo().getSorgcode(),null);
	}*/

}
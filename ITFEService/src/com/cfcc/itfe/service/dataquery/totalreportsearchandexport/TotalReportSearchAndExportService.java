package com.cfcc.itfe.service.dataquery.totalreportsearchandexport;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.*;
import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.persistence.dto.HtrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.service.dataquery.totalreportsearchandexport.AbstractTotalReportSearchAndExportService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author zl
 * @time   13-04-18 08:49:22
 * codecomment: 
 */

public class TotalReportSearchAndExportService extends AbstractTotalReportSearchAndExportService {
	private static Log log = LogFactory.getLog(TotalReportSearchAndExportService.class);	
	private static String startdate = TimeFacade.getCurrentStringTime();
	private static String enddate = TimeFacade.getCurrentStringTime();
	private static int pici = 0;

	/**
	 * 导出文件
	 	 
	 * @generated
	 * @param findto
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    @SuppressWarnings("unchecked")
	public String dataExport(IDto findto,String whereSql) throws ITFEBizException {
		List list=new ArrayList();
		String filename="";
		try {
			startdate = TimeFacade.getCurrentStringTime();
			if(startdate.equals(enddate))
				pici++;
			else
				enddate = TimeFacade.getCurrentStringTime();
			if(findto instanceof TrIncomedayrptDto ){
				TrIncomedayrptDto mdto=(TrIncomedayrptDto)findto;
				list=CommonFacade.getODB().findRsByDtoForWhere(mdto, whereSql);
				filename=TimeFacade.getCurrentStringTime()+"_4"+(pici<10?"0"+pici:pici)+"hd.txt";
			}else if(findto instanceof HtrIncomedayrptDto){
				HtrIncomedayrptDto mdto=(HtrIncomedayrptDto)findto;
				list=CommonFacade.getODB().findRsByDtoForWhere(mdto,whereSql);
				filename=TimeFacade.getCurrentStringTime()+"_4"+(pici<10?"0"+pici:pici)+"hd.txt";
			}
			String root = ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // 取得系统分割符
			String strdate = DateUtil.date2String2(new Date()); // 当前系统年月日
			String fullpath = root + "exportFile" + dirsep + strdate + dirsep
					+ filename;
			String splitSign = ",";//"\t"; // 文件记录分隔符号
			if (list.size() > 0) {
				StringBuffer filebuf = new StringBuffer();
//				int index=1;
				if(findto instanceof TrIncomedayrptDto){
					for (TrIncomedayrptDto _dto :(List<TrIncomedayrptDto>) list) {
						filebuf.append(_dto.getStrecode());//国库代码
						filebuf.append(splitSign+_dto.getSbudgetlevelcode());//预算级次
						filebuf.append(splitSign+_dto.getSbudgetsubcode());//科目代码
						filebuf.append(splitSign+_dto.getSrptdate());//账务日期
						filebuf.append(splitSign+_dto.getSbudgettype());//预算种类
						filebuf.append(splitSign+_dto.getNmoneyday());//日累计
						filebuf.append(splitSign+_dto.getNmoneymonth());//月累计
						filebuf.append(splitSign+_dto.getNmoneyyear());//年累计
						filebuf.append("\r\n");
					}
				}else if(findto instanceof HtrIncomedayrptDto){
					for (HtrIncomedayrptDto _dto :(List<HtrIncomedayrptDto>) list) {
						filebuf.append(_dto.getStrecode());//国库代码
						filebuf.append(splitSign+_dto.getSbudgetlevelcode());//预算级次
						filebuf.append(splitSign+_dto.getSbudgetsubcode());//科目代码
						filebuf.append(splitSign+_dto.getSrptdate());//账务日期
						filebuf.append(splitSign+_dto.getSbudgettype());//预算种类
						filebuf.append(splitSign+_dto.getNmoneyday());//日累计
						filebuf.append(splitSign+_dto.getNmoneymonth());//月累计
						filebuf.append(splitSign+_dto.getNmoneyyear());//年累计
						filebuf.append("\r\n");
					}
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
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException(e);
		}
    }


	public static Log getLog() {
		return log;
	}


	public static void setLog(Log log) {
		TotalReportSearchAndExportService.log = log;
	}

}
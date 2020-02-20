package com.cfcc.itfe.util.datamove;

import java.io.File;
import java.sql.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DB2CallShell;
import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsSystemDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeDetailDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeonlineDto;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceMainDto;
import com.cfcc.itfe.util.DBTools;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.itfe.util.FileUtil;

public class incomeShareMove {
	private static Log log = LogFactory.getLog(DataMoveUtil.class);
	
	@SuppressWarnings("unchecked")
	public static void timerTaskForDataMove() throws ITFEBizException{
		
		Date currentDate = TimeFacade.getCurrentDateTime(); // 当前系统的时间
		
		log.debug("==========================开启定时清理收入共享数据窗口=========================="+currentDate);
		
		// 取得系统表中的数据清理条件
		int clsdays = 30;
		int movedays = 7;
		try {
			List<TsSystemDto> list = DatabaseFacade.getDb().find(TsSystemDto.class);
			if(null != list && list.size() != 0){
				clsdays = list.get(0).getIcleardays(); // 数据清理天数（从历史表中清理）
				movedays = list.get(0).getItransdays(); // 数据转移天数  (从当前表转移到历史表)
			}
						
			//转移电子税票
			String incomesql = createSqlByDays(currentDate, clsdays, "S_APPLYDATE");
			String incomemovsql = createSqlByDays(currentDate,movedays , "S_APPLYDATE");
			importData(TvFinIncomeonlineDto.tableName(), incomesql, incomemovsql);
			//转移海关电子税票
			importData(TvFinIncomeonlineDto.tableName(), incomesql, incomemovsql);
			//转移入库流水
			String detailsql = createSqlByDays(currentDate, clsdays , "S_INTREDATE");
			String detailmov = createSqlByDays(currentDate, movedays , "S_INTREDATE");
			importData(TvFinIncomeDetailDto.tableName(), detailsql, detailmov);
			
			//转移税票明细
			String incomeDetailsql = createSqlByDays(currentDate, clsdays, "S_COMMITDATE");
			String incomemDetailMovsql = createSqlByDays(currentDate,movedays , "S_COMMITDATE");
			importData(TvInfileDetailDto.tableName(), incomeDetailsql, incomemDetailMovsql);
				
			//转移汇总明细
			String incomeSumsql = createSqlByDays(currentDate, clsdays,null);
			String incomeSummovsql = createSqlByDays(currentDate,movedays ,null);
			importData(TvInfileDto.tableName(), incomeSumsql, incomeSummovsql);
			//批量拨付
			String batchclssql = createSqlByDays(currentDate, clsdays , "S_VOUDATE");
			String batchmovsql = createSqlByDays(currentDate, movedays , "S_VOUDATE");
			importData(TvPayoutfinanceMainDto.tableName(),batchclssql , batchmovsql); 
			
			//转移日报数据
			String incomeDaysql = createSqlByDays(currentDate, clsdays , "S_RPTDATE");
			String incomemovDaysql = createSqlByDays(currentDate, movedays , "S_RPTDATE");
			importData(TrIncomedayrptDto.tableName(), incomeDaysql, incomemovDaysql); 
			importData(TrStockdayrptDto.tableName(), incomeDaysql, incomemovDaysql);
			
						
		} catch (JAFDatabaseException e) {
			log.error("数据清理失败！", e);
			//throw new ITFEBizException("数据清理失败！", e);
		}
		
		log.debug("==========================清理收入共享数据窗口结束=========================="+currentDate);
	} 
	
	
	public static String createSqlByDays(Date currentDate,int days ,String dbprop){
		String date = DateUtil.dateBefore(currentDate, days, "D").replace("-", "");
		if(null == dbprop || "".equals(dbprop.trim())){
			return " S_ACCDATE <= '" + date + "'";
		}else{
			return dbprop + " <= '" + date + "'";
		}
	}
	
	public static String createSqlByDays(Date currentDate,int days ,String dbprop,String bizTye){
		String date = DateUtil.dateBefore(currentDate, days, "D").replace("-", "");
		if(null == dbprop || "".equals(dbprop.trim())){
			if(bizTye.equals(">")){
				return " D_ACCEPT > '" + date + "'";
			}else{
				return " D_ACCEPT <= '" + date + "'";
			}
		}else{
			if(bizTye.equals(">")){
				return dbprop + " > '" + date + "'";
			}else{
				return dbprop + " <= '" + date + "'";
			}
		}
	}
	
	/**
	 * 根据表名得到表的字段
	 * @param tabName
	 * @return
	 * @throws ITFEBizException 
	 */
	public static String findColumByTable(String tabName, String alias) throws ITFEBizException { 
		List<String> l = DBTools.lookColumnByTabName(tabName);
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < l.size(); i++) {
			String col = l.get(i);
			b.append(alias + ".");
			b.append(col);
			if (i != l.size() - 1) {
				b.append(",");
			}

		}
		return b.toString();
	}
	
	/**
	 * 文件从当前表导出，历史表导入
	 * @throws ITFEBizException 
	 * @throws JAFDatabaseException 
	 */
	public static void importData(String tabName, String deleteSql, String moveSql) throws  JAFDatabaseException { 
		String currentDate = TimeFacade.getCurrentStringTime(); // 当前系统的时间
		String dirsep = File.separator; // 取得系统分割符
		String absolutePath = ITFECommonConstant.FILE_ROOT_PATH + currentDate + dirsep + tabName;
//		String absoluteHPath = ITFECommonConstant.FILE_ROOT_PATH + currentDate + dirsep + "H" + tabName;
		String absoluteSql = absolutePath + ".sql";
		//删除历史表数据
		String exportcontent = PublicSearchFacade.getSqlConnectByProp() + ";\n";
		exportcontent = exportcontent  + "delete from H" + tabName
        +  " where " + deleteSql + ";\n";
		
		//导出当前表数据
		exportcontent = exportcontent  + "export to  " + absolutePath + ".del of DEL select * from "
		                + tabName + " where " + moveSql + ";\n";
			
		//当前表数据导入历史表
		exportcontent = exportcontent +  "import from " + absolutePath + ".del of del commitcount 10000 insert_update into H" + tabName + ";\n";
		
		//删除当前表数据
		exportcontent = exportcontent + "delete from " + tabName
        + " where " + moveSql + ";\n connect reset;\n";
		
		String results = null;
		try {
			FileUtil.getInstance().writeFile(absoluteSql, exportcontent);
			byte[] bytes = null;
			bytes = DB2CallShell.dbCallShellWithRes(absoluteSql);
			if (bytes.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
				results = new String(bytes, 0, MsgConstant.MAX_CALLSHELL_RS * 1024);
			} else {
				results = new String(bytes);
			}
			log.debug("调用SHELL结果:" + results);
			FileUtil.getInstance().deleteFileForExists(absolutePath+".del");
			FileUtil.getInstance().deleteFileForExists(absoluteSql);
			bytes = null;
		} catch (Exception e) {
			log.error("调用SHELL:数据导入的时候出现数据库异常!", e);
		}
	}
	/**
	 * @return
	 */
//	private static boolean isWin() {
//		String osName = System.getProperty("os.name");
//		if (osName.indexOf("Windows") >=0) {
//			return true;
//		} else {
//			return false;
//		}
//	}

}

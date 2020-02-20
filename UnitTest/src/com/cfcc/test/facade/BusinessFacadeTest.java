package com.cfcc.test.facade;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.HtrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TvInfileTmpCountryDto;
import com.cfcc.itfe.util.ReportLKXMUtil;
import com.cfcc.itfe.util.datamove.DataMoveUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.CommonQto;
import com.cfcc.jaf.persistence.util.SqlUtil;

/**
 * @author caoyg
 * 
 */
public class BusinessFacadeTest extends TestCase {
	static {
		ContextFactory.setContextFile("/config/ContextLoader_02.xml");
	}

	/*
	 * public void testBusinessFacade() throws Exception {
	 * 
	 * try { BusinessFacade.makeStatBill("020000000000", java.sql.Date
	 * .valueOf("2009-01-05")); } catch (Exception e) { e.printStackTrace();
	 * throw e; } }
	 */
	public static void insertTV_INFILE_TMP_COUNTRY(String filepath) {
/*		List<TvInfileTmpCountryDto> insertList = new ArrayList<TvInfileTmpCountryDto>();
		TvInfileTmpCountryDto countryDto;
		DBFFileFacade dbfFacade = new DBFFileFacade();
		String filename = "";
		insertList = dbfFacade.doResultSet(filepath);
		String deleteSql;
		int iCreateNumber = MsgConstant.TIPS_MAX_OF_PACK;
		int iInsertRow = 0;
		
		try {
			if(insertList != null && insertList.isEmpty() == false)
			{
				filename = insertList.get(0).getSfilename();
			}
			
			deleteSql = "delete from DB2ADMIN.TV_INFILE_TMP_COUNTRY WHERE S_FILENAME = ?";
			SQLExecutor sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.addParam(filename);
			SQLResults taxorgRs = sqlExec.runQueryCloseCon(deleteSql);

			List<TvInfileTmpCountryDto> list = new ArrayList<TvInfileTmpCountryDto>();
			for (int i = 0; i < insertList.size(); i++) {
				countryDto = insertList.get(i);
				list.add(countryDto);
				
				if(iInsertRow == iCreateNumber - 1 || i == insertList.size() -1){
					TvInfileTmpCountryDto[] countryDtos = new TvInfileTmpCountryDto[list.size()];
					countryDtos = list.toArray(countryDtos);
					
					DatabaseFacade.getDb().create(countryDtos);
					list.clear();
					iInsertRow = 0;
				}
				
				iInsertRow++;
			}
		} catch (Exception e) {

		}
*/	}

	public void testBusinessFacade() throws Exception {

		try {
			// DataMoveUtil.timerTaskForDataMove();
			// AcctFacade.computeacctfamt("020000000000",
			// java.sql.Date.valueOf("2009-03-30"));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static void main(String[] argv) throws Exception {
		 try {
			 TrIncomedayrptDto _dto = new TrIncomedayrptDto();
			 _dto.setSrptdate("20140206");
			 _dto.setStrecode("0100000000");
			 _dto.setSbillkind("1");
			 _dto.setSbudgetlevelcode("1");
			 _dto.setSbelongflag("1");
			 _dto.setStaxorgcode("000000000000");
			 CommonQto qto = SqlUtil.IDto2CommonQto(_dto);
		    
			 List<TrIncomedayrptDto> reportList = DatabaseFacade.getODB().findWithUR(_dto.getClass(), qto);
			 ReportLKXMUtil ru = new ReportLKXMUtil();
			 List <TrIncomedayrptDto> list = ru.computeLKM("090000000002", reportList, "1");
			 for (TrIncomedayrptDto p : list) {
				 System.out.println(p);
			}
//		 DataMoveUtil.timerTaskForDataMove();
		 //AcctFacade.computeacctfamt("020000000000",
//		 java.sql.Date.valueOf("2009-03-30"));
		 } catch (Exception e) {
		 e.printStackTrace();
		 throw e;
		 }
//		String filepath = "D:\\国库前置资料文件\\杭州\\测试数据\\一户通扣款数据0315\\国税一户通扣款数据0315\\03160601.dbf";
//		insertTV_INFILE_TMP_COUNTRY(filepath);
	}

	/*
	 * public void testCollectFacade() throws Exception { try {
	 * BusinessFacade.collectacctinfo("020000000000", java.sql.Date
	 * .valueOf("2009-01-05")); } catch (Exception e) { e.printStackTrace();
	 * throw e; }
	 * 
	 * }
	 */

}

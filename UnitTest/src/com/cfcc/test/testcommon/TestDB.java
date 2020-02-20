package com.cfcc.test.testcommon;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DBOperUtil;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.data.SQLMapping;


public class TestDB {
	static {
		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
	}

	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		List<SQLMapping> deltas =  (List<SQLMapping>)ContextFactory.getApplicationContext()
//		.getBean("TASDelete");
//		List<SQLMapping> inserttas =  (List<SQLMapping>)ContextFactory.getApplicationContext()
//		.getBean("TASInsert");
//		List<SQLMapping> deltbs =  (List<SQLMapping>)ContextFactory.getApplicationContext()
//		.getBean("TBSDelete");
//		List<Object> params = new ArrayList<Object>();
////		params.add("020000000000");
//		params.add("020000000000");
//		try {
////			DBOperUtil.exec(deltas,null, params);
//			DBOperUtil.exec(deltas,null, params);
//			DBOperUtil.exec(inserttas,null, params);
////			DBOperUtil.exec(deltbs,null, params);
//		} catch (JAFDatabaseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	public static void createdb() throws JAFDatabaseException {
//		TmFuncDto dto = new TmFuncDto();
//		dto.setCfuncstate("2");
//		dto.setCnotelogflag("2");
//		dto.setCoforgkind("1");
//		dto.setCofsys("1");
//		dto.setSfunccode("1113");
//		dto.setSfuncname("ÄãºÃ");
//		DatabaseFacade.getODB().create(dto);
//
//		TmRoleDto dto1 = new TmRoleDto();
//		dto1.setSbookorgcode("1");
//		dto1.setSroleid("abcf");
//		dto1.setSroletype("2");
//		dto1.setSroleidsht("1");
//		dto1.setSrolename("½ÇÉ«1");
//		DatabaseFacade.getBatchODB().createWithResult(dto1);
//
//	}
//
//	public static void find() throws JAFDatabaseException {
//		List<TmFuncDto> tmfuncList = DatabaseFacade.getODB().find(
//				TmFuncDto.class);
//
//		for (int i = 0; i < tmfuncList.size(); i++) {
//			TmFuncDto x = tmfuncList.get(i);
//			System.out.println(x.getSfunccode());
//		}
//	}
//
//	public static void findSQL() throws JAFDatabaseException {
//		String sql = "select S_ROLENAME,S_ROLEID from tm_role with ur ";
//		// String sql =
//		// "select current timestamp from sysibm.sysdummy1 with ur";
//		SQLExecutor sqlExec;
//		try {
//			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
//					.getSQLExecutor();
//			SQLResults res = sqlExec.runQueryCloseCon(sql);
//
//			String i = res.getString(0, 0);
//			System.out.println(i);
//			System.out.println(res.getString(0, 1));
//		} catch (JAFDatabaseException e) {
//			throw e;
//		}
//	}
//
//	public static void findSQL1() throws JAFDatabaseException,
//			ValidateException {
//		TmFuncDto dto = new TmFuncDto();
//		dto.setCfuncstate("1");
//		dto.setCnotelogflag("1");
//		List<TmFuncDto> dtos = CommonFacade.getODB().findRsByDto(dto);
//		for (int i = 0; i < dtos.size(); i++) {
//			System.out.println(dtos.get(i).getSfuncname());
//		}
//	}
}

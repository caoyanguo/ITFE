package com.cfcc.test.testcommon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.BatchRetriever;
import com.cfcc.jaf.persistence.jaform.IOrmTemplate;
import com.cfcc.jaf.persistence.jaform.OrmTemplateHelper;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;


public class AccessDBExample {
	private static Log log = LogFactory.getLog(AccessDBExample.class);

	static {
		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
	}

	public static void main(String[] args) {
////		testFacade();
//		TpErrortypeDto dto = new TpErrortypeDto();
//		dto.setCdelflag("1");
////		dto.setIparamseqno(new Integer(1));
//		dto.setSerrtypecode("1");
//		dto.setSerrtypename("1");
//		dto.setSremark("1");
//		try {
//			DatabaseFacade.getODB().create(dto);
//		} catch (JAFDatabaseException e) {
//			String error = "保存数据,发生数据库异常!";
//			log.error(error, e);
//			e.printStackTrace();
//		}
//
//		// // 批量新增记录
//		 List<TpErrortypeDto> dtos = new ArrayList<TpErrortypeDto>();
//		 for (int i = 1; i <= 5; i++) {
//		 TpErrortypeDto dtoa = new TpErrortypeDto();
//					
//		 dtos.add(dtoa);
//		 }
//		
//		 try {
//		 TpErrortypeDto[] dtoarray = new TpErrortypeDto[dtos.size()];
//		 dtoarray = dtos.toArray(dtoarray);
//		 DatabaseFacade.getODB().create(dtoarray);
//		 } catch (JAFDatabaseException e) {
//		 String error = "保存数据,发生数据库异常!";
//		 log.error(error, e);
//		 e.printStackTrace();
//		 }
//		//
//		// // 根据主键查询
//		 TpErrortypePK pk = new TpErrortypePK();
//			
//		 try {
//		 TpErrortypeDto dtopk = (TpErrortypeDto) DatabaseFacade.getODB()
//		 .find(pk);
//		 System.out.println(dtopk);
//		 } catch (JAFDatabaseException e) {
//		 String error = "查询数据,发生数据库异常!";
//		 log.error(error, e);
//		 e.printStackTrace();
//		 }
//		// // 根据SQL查询
//		 try {
//		 String sql = "where I_serialno = ?";
//		 List param = new ArrayList();
//		 param.add(205);
//		 List rs = DatabaseFacade.getODB().find(TpErrortypeDto.class, sql,
//		 param);
//		 System.out.println(rs);
//		 } catch (JAFDatabaseException e) {
//		 String error = "查询数据,发生数据库异常!";
//		 log.error(error, e);
//		 e.printStackTrace();
//		 }
//		//
//		// // 修改记录
//		// TlOperlogPK pku = new TlOperlogPK();
//		// pk.setDworkdate(java.sql.Date.valueOf("2007-12-26"));
//		// pk.setIserialno(new BigDecimal(205));
//		// try {
//		// TlOperlogDto dtou = (TlOperlogDto) DatabaseFacade.getODB()
//		// .find(pku);
//		// if (dtou != null) {
//		// dtou.setSbusinesspk("更新数据");
//		// DatabaseFacade.getODB().update(dtou);
//		// }
//		// } catch (JAFDatabaseException e) {
//		// String error = "更新数据,发生数据库异常!";
//		// log.error(error, e);
//		// e.printStackTrace();
//		// }
//		//
//		// // 删除数据
//		
//	}
//
//	public static void batchFind() {
//		IOrmTemplate jot = OrmTemplateHelper.getOrmTemplate();
//		BatchRetriever br = jot.getBatchRetriever(TpErrortypeDto.class);
//		String sql = "select * from tl_monitor_log order by  ts_update desc";
//		br.setMaxRows(1000);
//		while (br.hasMore()) {
//
//		}
//	}
//	public static void testFacade(){
//		TmUserDto dto = new TmUserDto();
//		dto.setSuserid("0001");
//		dto.setSbookorgcode("100000000000");
//		try {
//			List<TmRoleDto> l=TmFacade.findUserFuncCrp(dto);
//			for(int i=0;i<l.size();i++){
//				TmRoleDto dto1=l.get(i);
//				System.out.println("");
//			}
//			System.out.println("");
//		} catch (JAFDatabaseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}

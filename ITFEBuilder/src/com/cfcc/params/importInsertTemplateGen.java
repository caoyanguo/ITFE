package com.cfcc.params;

import java.io.StringWriter;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.cfcc.FileOper;
import com.cfcc.database.DBOpertion;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;


/**
 * Action template generate
 */

public class importInsertTemplateGen {
	static VelocityContext context = new VelocityContext();
	static {
		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
		try {
			Velocity.init();
		} catch (Exception e) {
			System.out.println("Problem initializing Velocity : " + e);
			System.exit(0);
		}
	}

	public static void main(String args[]) {
		try {
			genIns();
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

	/**
	 * 生成插入参数表的代码
	 * @throws ValidateException 
	 * @throws JAFDatabaseException 
	 */
	private static void genIns() throws ValidateException, JAFDatabaseException {
	
		List<String> tables = DBOpertion.lookTable("db2itfe", null);
		List<TableName> tabdatas = new ArrayList<TableName>();
		
		for(String table:tables){
			if(table.toUpperCase().startsWith("TS")){
				TableName tableName = new TableName();
				tableName.setSrctable(table);
				tabdatas.add(tableName);
			}
		}
		
		
		
		try {
			context.put("InsTasTableList", tabdatas);
			StringWriter w = new StringWriter();
			Velocity.mergeTemplate("./src/com/cfcc/params/vm/import.vm", "GBK",
					context, w);
			FileOper.saveFile(w.toString(), "c:/DataInsSqlConfig.xml");
		} catch (Exception e) {
			System.out.println("Problem merging template : " + e);
		}
	}

	private static String getColNames(String table) {
		StringBuffer colStrs = new StringBuffer();
		HashMap<String, String> fileterColMap = new HashMap<String, String>();

		List<String> cols = DBOpertion.lookColumnByTabName(table,
				null);
		for (int i = 0; i < cols.size(); i++) {
			colStrs.append(cols.get(i));
			if (i != cols.size() - 1) {
				colStrs.append(",");
			}
		}
		return colStrs.toString();
	}
}

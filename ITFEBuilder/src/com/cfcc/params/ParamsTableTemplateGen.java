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
import com.cfcc.itfe.config.BizConfigInfo;
import com.cfcc.itfe.util.CommonUtil;

/**
 * Action template generate
 */

public class ParamsTableTemplateGen {
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
		genTable();
		/* lets make a Context and put data into it */

		/* lets render a template */

	}

	/**
	 * 生成插入参数表的代码
	 */
	private static void genTable() {
		List<String> tables = DBOpertion.lookTable("tcbsusra", null);
		List<TableName> tabdatas = new ArrayList<TableName>();

		for (int i = 0; i < tables.size(); i++) {
			String tabName = tables.get(i);
			if (tabName.indexOf("TAS_TC") >= 0||tabName.indexOf("TBS_TC") >= 0) {
				TableName table = new TableName();
				table.setSrctable(tabName);
				table.setTabcname(BizConfigInfo.TableDesMap().get(tabName));
				String xxx = table.getTabcname().replace("(被监督)", "");
				xxx = xxx.replace("(标准库)", "");
				table.setTabModuleName(xxx);
				table.setTabUIName(table.getTabModuleName() + "_参数监督");
				table.setTabDtoClsName(CommonUtil.tableToDtoName(tabName));
				table.setTabBeanClsName(CommonUtil.tableToBeanName(tabName));
				tabdatas.add(table);
			}
		}
		try {
			context.put("TasTableList", tabdatas);
			// context.put("InsTbsTableList", tbstabdatas);
			StringWriter w = new StringWriter();
			Velocity.mergeTemplate("./src/com/cfcc/params/vm/table.vm", "GBK",
					context, w);
			FileOper.saveFile(w.toString(), "c:/DataTabSqlConfig.xml");
		} catch (Exception e) {
			System.out.println("Problem merging template : " + e);
		}
	}

}

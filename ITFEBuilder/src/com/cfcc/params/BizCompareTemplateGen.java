package com.cfcc.params;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.cfcc.FileOper;
import com.cfcc.database.ColumnData;
import com.cfcc.database.DBOpertion;
import com.cfcc.jaf.core.loader.ContextFactory;

public class BizCompareTemplateGen {

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
		genBizCompare();

	}

	/**
	 * 生成数据搬移参数表的代码
	 */
	private static void genBizCompare() {
		// 地方横联
		//TBS_TV_PAYOUT                
		//TBS_TV_DWBK                  
		//TBS_TV_HANDBOOK_DETAIL       
		//TBS_TV_IN_CORRHANDBOOK       
		//TBS_TV_MOVEINCM              
		//TBS_TV_NATIONDEBTCASH_DETAIL 
		//TBS_TV_BNKPAY_main           
		//TBS_TV_DIRECTPAYPLAN_main    
		//TBS_TV_GRANTPAYPLAN_main     
		//TBS_TV_NATIONDEBT_LOWIN_main 
		//TBS_TV_NATIONDEBTCASH_IN_main

		List<String> tables = DBOpertion.lookTable("db2admin", null);
		List<TableName> tabdatas = new ArrayList<TableName>();
		for (int i = 0; i < tables.size(); i++) {
			String tabName = tables.get(i);

			if (tabName.toLowerCase().equals("tbs_tv_payout")
					|| tabName.toLowerCase().equals("tbs_tv_dwbk")
					|| tabName.toLowerCase().equals("tbs_tv_handbook_detail")
					|| tabName.toLowerCase().equals("tbs_tv_in_corrhandbook")
					|| tabName.toLowerCase().equals("tbs_tv_moveincm")
					|| tabName.toLowerCase().equals(
							"tbs_tv_nationdebtcash_detail")
					|| tabName.toLowerCase().equals("tbs_tv_bnkpay_main")
					|| tabName.toLowerCase()
							.equals("tbs_tv_directpayplan_main")
					|| tabName.toLowerCase().equals("tbs_tv_grantpayplan_main")
					|| tabName.toLowerCase().equals(
							"tbs_tv_nationdebt_lowin_main")
					|| tabName.toLowerCase().equals(
							"tbs_tv_nationdebtcash_in_main")

			) {

				TableName table = new TableName();
				//String strColumns = getColNamesFormat(tabName);// 字段列表
				String strColumns=ColNamesAboutNull(tabName);//考虑空字段的字段列表
				table.setStrColumns(strColumns);

				table.setSrctable(tabName);// 源 tbs表
				table.setDestable("TAS_TV"
						+ tabName.substring(6, tabName.length()));// 目标 tas表
				tabdatas.add(table);

			}
		}
		try {
			context.put("BizCompareTableList", tabdatas);
			StringWriter w = new StringWriter();
			Velocity.mergeTemplate("./src/com/cfcc/params/vm/bizcompare.vm",
					"GBK", context, w);
			FileOper.saveFile(w.toString(), "c:/BizMainCompareSqlConfig.xml");
		} catch (Exception e) {
			System.out.println("Problem merging template : " + e);
		}
	}

	private static String getColNamesFormat(String table) {
		StringBuffer colstrs = new StringBuffer();
		HashMap<String, String> fileterColMap = new HashMap<String, String>();
		String col1 = "s_AuditResult".toUpperCase();
		String col2 = "c_AuditState".toUpperCase();

		fileterColMap.put(col1, col1);
		fileterColMap.put(col2, col2);

		List<String> cols = DBOpertion.lookColumnByTabNameWithFilter(table,
				fileterColMap);

		for (int i = 0; i < cols.size(); i++) {
			colstrs.append("a." + cols.get(i) + "=b." + cols.get(i));
			if (i != cols.size() - 1) {
				colstrs.append(" and ");
			}

		}
		return colstrs.toString();
	}
	
	private static String ColNamesAboutNull(String table){
		
		StringBuffer colstrs=new StringBuffer();
		HashMap<String,String> filtermap=new HashMap<String, String>();
		String col1 = "s_AuditResult".toUpperCase();
		String col2 = "c_AuditState".toUpperCase();
		
		filtermap.put(col1, col1);
		filtermap.put(col2, col2);
		
		List<ColumnData> collist=DBOpertion.lookColumnDataByTabNameWithFilter(table, null, filtermap);
		
		for(int i=0;i<collist.size();i++){
			if(collist.get(i).isIsnull()){
				colstrs.append("((a."+collist.get(i).getColid()+"=b."+ collist.get(i).getColid() + ") or (a."
						+ collist.get(i).getColid() + " is null and b."
						+ collist.get(i).getColid() + " is null))");
			}else{
				colstrs.append("a." + collist.get(i).getColid() + "=b."
						+ collist.get(i).getColid());
			}

			if (i != collist.size() - 1) {
				colstrs.append(" and ");
			}
		}
		return colstrs.toString();
	}
}

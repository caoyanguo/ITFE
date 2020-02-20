package com.cfcc.model;

import java.util.Collection;
import java.util.HashMap;

import javax.swing.text.html.HTMLDocument.Iterator;

import com.cfcc.database.DBOpertion;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.itfe.config.BizConfigInfo;
import com.cfcc.itfe.util.CommonUtil;

public class PayOutBizModelGen {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
		String file1="E:/caoyg_snapshotview/TAS/3CODE/TASApp/ModelProject/model/JUIComponent_4f9b454e-c268-4f7d-8792-b3b94ad0f374.model";
		String file2="";
		//payoutModelGen("Payout_Biz_03_Table",file1);
		payoutModelGen("Book_Biz_12_Table",file1);
		// TODO Auto-generated method stub

	}
	public static void payoutModelGen(String beanid,String file) {
		IModelGenerator mg = new ModelGeneratorImpl();
		HashMap<String, String> map = BizConfigInfo.BizTable(beanid);
		java.util.Iterator<String> it = map.values().iterator();
		HashMap<String, String> attribute = new HashMap<String, String>();
		while (it.hasNext()) {
			String table = it.next();
			String dtoname = CommonUtil.tableToDtoName(table);
			String bind = table.toLowerCase().replaceAll("_", "") + "Dto";
			HashMap<String, String> colMap = DBOpertion
					.lookColumnDataByTabNameWithHashMap(table, null);

			if (table.startsWith("TAS")) {
			mg
						.generateTextarea(
								file,
								dtoname, colMap, table, "监督数据录入区_"
										+ BizConfigInfo.TableDesMap()
												.get(table), bind, false, null,false);
				
				
			} else if(table.startsWith("TBS")) {
				mg
						.generateTextarea(
								file,
								dtoname, colMap, table, "被监督数据显示区"
										+ BizConfigInfo.TableDesMap()
												.get(table), bind, false, null,true);
			}

			attribute.put(bind, dtoname);
		}
		mg
				.generateAttribute(
						"E:/caoyg_snapshotview/TAS/3CODE/TASApp/ModelProject/model/JUIComponent_4f9b454e-c268-4f7d-8792-b3b94ad0f374.model",
						attribute);

	}
	
}

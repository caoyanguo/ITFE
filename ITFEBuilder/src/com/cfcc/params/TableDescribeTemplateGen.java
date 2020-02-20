package com.cfcc.params;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.cfcc.FileOper;
import com.cfcc.database.DBOpertion;
import com.cfcc.database.TableData;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.itfe.config.BizConfigInfo;
import com.cfcc.itfe.facade.DBOperUtil;
import com.cfcc.itfe.facade.data.TableDesDto;
import com.cfcc.itfe.util.CommonUtil;

/**
 * Action template generate
 */

public class TableDescribeTemplateGen {
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
		genDel();
		/* lets make a Context and put data into it */

		/* lets render a template */

	}

	/**
	 * 生成删除参数表的代码
	 */
	private static void genDel() {

		// tbs_tv_nationdebt_lowin_main
		// tbs_tv_nationdebtcash_detail
		// tbs_tv_fundrpt_tblhead
		// tbs_tv_grantpayplan_main
		// tbs_tv_nationdebtcash_in_main
		// tbs_tv_directpayplan_main
		// tbs_tv_bnkpay_main
		// tbs_tv_in_corrhandbook
		// tbs_tv_payout
		// tbs_tv_handbook_detail
		// tbs_tv_dwbk
		// tbs_tv_moveincm
		// tbs_trl_cmt100go
		// tbs_trl_cmt100come
		// tbs_trl_cmt103go
		// tbs_trl_cmt103come
		// tbs_trl_cmt108go
		// tbs_trl_cmt108come
		// tbs_trl_pkg001go_snd_detail
		// tbs_trl_pkg001come_rcv_detail
		// tbs_trl_pkg002go_snd_detail
		// tbs_trl_pkg002come_rcv_detail
		// tbs_trl_pkg007go_snd_detail
		// tbs_trl_pkg007come_rcv_detail
		// tbs_trl_pkg008go_snd_detail
		// tbs_trl_pkg008come_rcv_detail
		// tbs_trl_innergo
		// tbs_trl_innercome
		// tbs_tv_nationdebt_lowin_main
		// tbs_tv_nationdebtcash_detail
		// tbs_tv_fundrpt_tblhead
		// tbs_tv_grantpayplan_main
		// tbs_tv_nationdebtcash_in_main
		// tbs_tv_directpayplan_main
		// tbs_tv_bnkpay_main
		// tbs_tv_in_corrhandbook
		// tbs_tv_payout
		// tbs_tv_handbook_detail
		// tbs_tv_dwbk
		// tbs_tv_moveincm
		// tbs_trl_cmt100go
		// tbs_trl_cmt100come
		// tbs_trl_cmt103go
		// tbs_trl_cmt103come
		// tbs_trl_cmt108go
		// tbs_trl_cmt108come
		// tbs_trl_pkg001go_snd_detail
		// tbs_trl_pkg001come_rcv_detail
		// tbs_trl_pkg002go_snd_detail
		// tbs_trl_pkg002come_rcv_detail
		// tbs_trl_pkg007go_snd_detail
		// tbs_trl_pkg007come_rcv_detail
		// tbs_trl_pkg008go_snd_detail
		// tbs_trl_pkg008come_rcv_detail
		// tbs_trl_innergo
		// tbs_trl_innercome

		List<String> tables = DBOpertion.lookTable("db2admin", null);
		List<TableDesDto> tastabdatas = new ArrayList<TableDesDto>();
		for (int i = 0; i < tables.size(); i++) {
			String tabName = tables.get(i);
			if (tabName.equals("TAS_TV_NATIONDEBT_LOWIN_MAIN")||
					tabName.equals("TAS_TV_NATIONDEBTCASH_DETAIL")||
					tabName.equals("TAS_TV_FUNDRPT_TBLHEAD")||
					tabName.equals("TAS_TV_GRANTPAYPLAN_MAIN")||
					tabName.equals("TAS_TV_NATIONDEBTCASH_IN_MAIN")||
					tabName.equals("TAS_TV_DIRECTPAYPLAN_MAIN")||
					tabName.equals("TAS_TV_BNKPAY_MAIN")||
					tabName.equals("TAS_TV_IN_CORRHANDBOOK")||
					tabName.equals("TAS_TV_PAYOUT")||
					tabName.equals("TAS_TV_HANDBOOK_DETAIL")||
					tabName.equals("TAS_TV_DWBK")||
					tabName.equals("TAS_TV_MOVEINCM")||
					tabName.equals("TAS_TRL_CMT100GO")||
					tabName.equals("TAS_TRL_CMT100COME")||
					tabName.equals("TAS_TRL_CMT103GO")||
					tabName.equals("TAS_TRL_CMT103COME")||
					tabName.equals("TAS_TRL_CMT108GO")||
					tabName.equals("TAS_TRL_CMT108COME")||
					tabName.equals("TAS_TRL_PKG001GO_SND_DETAIL")||
					tabName.equals("TAS_TRL_PKG001COME_RCV_DETAIL")||
					tabName.equals("TAS_TRL_PKG002GO_SND_DETAIL")||
					tabName.equals("TAS_TRL_PKG002COME_RCV_DETAIL")||
					tabName.equals("TAS_TRL_PKG007GO_SND_DETAIL")||
					tabName.equals("TAS_TRL_PKG007COME_RCV_DETAIL")||
					tabName.equals("TAS_TRL_PKG008GO_SND_DETAIL")||
					tabName.equals("TAS_TRL_PKG008COME_RCV_DETAIL")||
					tabName.equals("TAS_TRL_INNERGO")||
					tabName.equals("TAS_TRL_INNERCOME")||
					tabName.equals("TBS_TV_NATIONDEBT_LOWIN_MAIN")||
					tabName.equals("TBS_TV_NATIONDEBTCASH_DETAIL")||
					tabName.equals("TBS_TV_FUNDRPT_TBLHEAD")||
					tabName.equals("TBS_TV_GRANTPAYPLAN_MAIN")||
					tabName.equals("TBS_TV_NATIONDEBTCASH_IN_MAIN")||
					tabName.equals("TBS_TV_DIRECTPAYPLAN_MAIN")||
					tabName.equals("TBS_TV_BNKPAY_MAIN")||
					tabName.equals("TBS_TV_IN_CORRHANDBOOK")||
					tabName.equals("TBS_TV_PAYOUT")||
					tabName.equals("TBS_TV_HANDBOOK_DETAIL")||
					tabName.equals("TBS_TV_DWBK")||
					tabName.equals("TBS_TV_MOVEINCM")||
					tabName.equals("TBS_TRL_CMT100GO")||
					tabName.equals("TBS_TRL_CMT100COME")||
					tabName.equals("TBS_TRL_CMT103GO")||
					tabName.equals("TBS_TRL_CMT103COME")||
					tabName.equals("TBS_TRL_CMT108GO")||
					tabName.equals("TBS_TRL_CMT108COME")||
					tabName.equals("TBS_TRL_PKG001GO_SND_DETAIL")||
					tabName.equals("TBS_TRL_PKG001COME_RCV_DETAIL")||
					tabName.equals("TBS_TRL_PKG002GO_SND_DETAIL")||
					tabName.equals("TBS_TRL_PKG002COME_RCV_DETAIL")||
					tabName.equals("TBS_TRL_PKG007GO_SND_DETAIL")||
					tabName.equals("TBS_TRL_PKG007COME_RCV_DETAIL")||
					tabName.equals("TBS_TRL_PKG008GO_SND_DETAIL")||
					tabName.equals("TBS_TRL_PKG008COME_RCV_DETAIL")||
					tabName.equals("TBS_TRL_INNERGO")||
					tabName.equals("TBS_TRL_INNERCOME")) {
				TableDesDto data = new TableDesDto();
				data.setTabEname(tabName);

				String cname = BizConfigInfo.TableDesMap().get(tabName);
				String xxx = cname;
				if (cname == null || cname.equals("0")) {
					System.out.println("XXXXXXXXXXXXXX" + tabName);
				}
				// if(cname.indexOf("(被监督)")>=0){
				// xxx.replaceAll("(被监督)","");
				// }

				data.setTabCname(BizConfigInfo.TableDesMap().get(tabName));
				data.setTabBeanClsName(CommonUtil.bizTableToBeanName(tabName));// 对应的bean文件
				data.setTabDtoClsName(CommonUtil.tableToDtoName(tabName));
				data.setTabModuleName(xxx);
				data.setTabUIName(xxx + "_参数监督");
				tastabdatas.add(data);
			}

		}
		try {
			context.put("TasTableList", tastabdatas);
			StringWriter w = new StringWriter();
			Velocity.mergeTemplate("./src/com/cfcc/params/vm/table.vm", "GBK",
					context, w);
			FileOper.saveFile(w.toString(), "c:/TableDescribeConfig.xml");
		} catch (Exception e) {
			System.out.println("Problem merging template : " + e);
		}
	}

	private static String getColNames(String table) {
		StringBuffer colStrs = new StringBuffer();
		HashMap<String, String> fileterColMap = new HashMap<String, String>();
		String col1 = "s_AuditResult".toUpperCase();
		String col2 = "c_AuditState".toUpperCase();
		fileterColMap.put(col1, col1);
		fileterColMap.put(col2, col2);
		List<String> cols = DBOpertion.lookColumnByTabNameWithFilter(table,
				fileterColMap);
		for (int i = 0; i < cols.size(); i++) {
			colStrs.append(cols.get(i));
			if (i != cols.size() - 1) {
				colStrs.append(",");
			}

		}
		return colStrs.toString();
	}
}

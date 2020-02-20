package com.cfcc.params;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.cfcc.FileOper;
import com.cfcc.database.DBOpertion;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.CommonDataAccessService;

public class InsertToIndexTemplateGen {
	private static final Object TBSname = null;
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
		genIns();
		/* lets make a Context and put data into it */

		/* lets render a template */

	}

	/**
	 * 生成插入参数表的代码
	 */
	private static void genIns() {
		// 需要过滤的表
		// TBS_TV_NATIONDEBT_LOWIN_SUB
		// TBS_TV_GRANTPAYPLAN_SUB
		// TBS_TV_NATIONDEBTCASH_IN_SUB
		// TBS_TV_DIRECTPAYPLAN_SUB
		// TBS_TV_BNKPAY_SUB

		
		List<Table> tabdatas = new ArrayList<Table>();

		List<String> tablename=new ArrayList<String>();
		tablename.add("TBS_TV_NATIONDEBT_LOWIN_MAIN");
		tablename.add("TBS_TV_NATIONDEBTCASH_DETAIL");
		tablename.add("TBS_TV_FUNDRPT_TBLHEAD");
		tablename.add("TBS_TV_GRANTPAYPLAN_MAIN");
		tablename.add("TBS_TV_NATIONDEBTCASH_IN_MAIN");
		tablename.add("TBS_TV_DIRECTPAYPLAN_MAIN");
		tablename.add("TBS_TV_BNKPAY_MAIN");
		tablename.add("TBS_TV_IN_CORRHANDBOOK");
		tablename.add("TBS_TV_PAYOUT");
		tablename.add("TBS_TV_HANDBOOK_DETAIL");
		tablename.add("TBS_TV_DWBK");
		tablename.add("TBS_TV_MOVEINCM");
		tablename.add("TBS_TRL_CMT100GO");
		tablename.add("TBS_TRL_CMT100COME");
		tablename.add("TBS_TRL_CMT103GO");
		tablename.add("TBS_TRL_CMT103COME");
		tablename.add("TBS_TRL_CMT108GO");
		tablename.add("TBS_TRL_CMT108COME");
		tablename.add("TBS_TRL_PKG001GO_SND_DETAIL");
		tablename.add("TBS_TRL_PKG001COME_RCV_DETAIL");
		tablename.add("TBS_TRL_PKG002GO_SND_DETAIL");
		tablename.add("TBS_TRL_PKG002COME_RCV_DETAIL");
		tablename.add("TBS_TRL_PKG007GO_SND_DETAIL");
		tablename.add("TBS_TRL_PKG007COME_RCV_DETAIL");
		tablename.add("TBS_TRL_PKG008GO_SND_DETAIL");
		tablename.add("TBS_TRL_PKG008COME_RCV_DETAIL");
		tablename.add("TBS_TRL_INNERGO");
		tablename.add("TBS_TRL_INNERCOME");

		
		
		
		

//		List<TpAuditedtabDto> list = new ArrayList<TpAuditedtabDto>();
//		try {
//			list = findBizType();
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
		for (String tab : tablename) {
			Table t = new Table();
			t.setTbsname(tab);// tbs表名
////			t.setBiztype(dto.getSofbizkind());// 业务类型
//
//			if (t.getTbsname().indexOf("TBS_TV") >= 0
//					&& !t.getTbsname().equals("TBS_TV_GROUP_HEAD")
//					&& !t.getTbsname().equals("TBS_TV_NATIONDEBT_LOWIN_SUB")
//					&& !t.getTbsname().equals("TBS_TV_GRANTPAYPLAN_SUB")
//					&& !t.getTbsname().equals("TBS_TV_NATIONDEBTCASH_IN_SUB")
//					&& !t.getTbsname().equals("TBS_TV_DIRECTPAYPLAN_SUB")
//					&& !t.getTbsname().equals("TBS_TV_BNKPAY_SUB")
//					&& !t.getTbsname().equals(
//							"index_TBS_TV_NATIONDEBTCASH_LOWIN_SUB")) {
//
				t.setTasname(t.getTbsname().replace("TBS_", "TAS_"));// TAS表名
//
//				// String AuditKind;//1主表监督2子表监督
			if (t.getTbsname().substring(t.getTbsname().length() - 4).equals(
					"MAIN")) {
				t.setTbssubname(t.getTbsname().replace("MAIN", "SUB"));// tbs子表
				t.setTassubname(t.getTasname().replace("MAIN", "SUB"));// tas子表
			} else {
				t.setTbssubname("");
				t.setTassubname("");
			}
				tabdatas.add(t);
			}
		try {
			context.put("InsSelTableList", tabdatas);

			StringWriter w = new StringWriter();
			Velocity.mergeTemplate("./src/com/cfcc/params/vm/insindex.vm",
					"GBK", context, w);
			FileOper.saveFile(w.toString(), "c:/BizDataInsertIndexSqlConfig.xml");
		} catch (Exception e) {
			System.out.println("Problem merging template : " + e);
		}
	}

	/**
	 * 
	 * @param dto
	 * @return
	 * @throws Exception
	 */
//	public static List<TpAuditedtabDto> findBizType() throws Exception {
//
//		return DatabaseFacade.getODB().find(TpAuditedtabDto.class);
//	}
}

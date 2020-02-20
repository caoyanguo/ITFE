package com.cfcc.itfe.component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.config.MuleProperties;
import org.mule.api.lifecycle.Callable;
import org.mule.module.client.MuleClient;
import org.mule.transport.bpm.jbpm.actions.Continue;
import org.springframework.scheduling.timer.TimerFactoryBean;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.IMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TdTaxorgMergerDto;
import com.cfcc.itfe.persistence.dto.TsOperationmodelDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.ServiceUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * 
 * 报文发送接收
 * 
 */
public class TimerUpdateIncomeComponent implements Callable {

	private static Log logger = LogFactory
			.getLog(TimerUpdateIncomeComponent.class);

	public Object onCall(MuleEventContext eventContext) throws Exception {

		// 根据配置文件配置判断是否需要更新对应的收入明细
		if (ITFECommonConstant.IFUPDATETAXVOU
				.equals(StateConstant.IFUPDATEVOU_TRUE)) {
			// 检查3139全部接收完毕后，进行
			// 更新明细记录
			String slesql = "select count(1) from ts_operationmodel   where S_OPERATIONTYPECODE = '3139'  fetch first "
					+ ITFECommonConstant.COMMIT_NUM + " rows only ";
			SQLExecutor sqlExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			SQLResults sr = sqlExec.runQueryCloseCon(slesql);
			if (sr.getLong(0, 0) == 0) {// 说明3139已经接收完毕
				// 先判断是否有需要更新的记录
				String querySQL = "SELECT count(1) "
						+ " FROM TV_FIN_INCOME_DETAIL TMP,TV_INFILE_DETAIL T1 WHERE TMP.S_TAXORGCODE=T1.S_TAXORGCODE AND TMP.S_EXPVOUNO = T1.S_SUM_TAXTICKETNO AND TMP.S_TRECODE = T1.S_RECVTRECODE "
						+ " AND  (T1.S_STATUS = '80002' OR T1.S_STATUS is NULL ) ";
				SQLExecutor sqlExec1 = DatabaseFacade.getDb()
						.getSqlExecutorFactory().getSQLExecutor();
				SQLResults sr1 = sqlExec1.runQueryCloseCon(querySQL);
				// 大于0才更新，否则不更新，小于o的话执行如下语句会将S_STATUS,S_COMMITDATE置为空
				if (sr1.getLong(0, 0) > 0) {
					// 按照国库代码+征收机关+汇总凭证号进行更新明细,入库流水参数有重复下发的情况
					SQLExecutor sqlExec2 = DatabaseFacade.getDb()
							.getSqlExecutorFactory().getSQLExecutor();
					String updSql = "update TV_INFILE_DETAIL T1 set (S_STATUS,S_COMMITDATE) = (SELECT '80000' as S_STATUS,S_INTREDATE "
							+ " FROM TV_FIN_INCOME_DETAIL TMP WHERE TMP.S_TAXORGCODE=T1.S_TAXORGCODE AND TMP.S_EXPVOUNO = T1.S_SUM_TAXTICKETNO AND TMP.S_TRECODE = T1.S_RECVTRECODE " +
									" AND TMP.S_ORGCODE IN(SELECT S_FINORGCODE FROM TS_CONVERTFINORG))  where  T1.S_STATUS = '80002' OR T1.S_STATUS is NULL ";
					sqlExec2.runQueryCloseCon(updSql);
				}
				// 更新汇总，先判断是否有要更新的汇总记录
				querySQL = "SELECT count(1) "
						+ " FROM TV_FIN_INCOME_DETAIL TMP,TV_INFILE T1 WHERE TMP.S_TAXORGCODE=T1.S_TAXORGCODE AND TMP.S_EXPVOUNO = T1.S_TAXTICKETNO AND TMP.S_TRECODE = T1.S_RECVTRECODE "
						+ " AND  T1.S_STATUS = '80002'";
				SQLExecutor sqlExec3 = DatabaseFacade.getDb()
						.getSqlExecutorFactory().getSQLExecutor();
				SQLResults sr2 = sqlExec3.runQueryCloseCon(querySQL);
				// 大于0才更新，否则不更新，小于o的话执行如下语句会将S_STATUS,S_COMMITDATE置为空
				if (sr2.getLong(0, 0) > 0) {
					SQLExecutor sqlExec2 = DatabaseFacade.getDb()
							.getSqlExecutorFactory().getSQLExecutor();
					String updSql = "MERGE INTO (SELECT * FROM TV_INFILE WHERE S_STATUS ='80002') AS T1  USING (select distinct S_TAXORGCODE,S_EXPVOUNO,S_TRECODE from  TV_FIN_INCOME_DETAIL  ) AS TMP ON  T1.S_TAXORGCODE=TMP.S_TAXORGCODE AND T1.S_TAXTICKETNO = TMP.S_EXPVOUNO  AND T1.S_RECVTRECODE =  TMP.S_TRECODE  "
							+ "  WHEN MATCHED  AND  T1.S_TAXORGCODE=TMP.S_TAXORGCODE AND T1.S_TAXTICKETNO = TMP.S_EXPVOUNO  AND T1.S_RECVTRECODE =  TMP.S_TRECODE "
							+ " THEN  UPDATE SET S_STATUS ='80000'";
					sqlExec2.runQueryCloseCon(updSql);

				}
			}
		}

		// 定时汇总合并征收机关报表：
		HashMap<String, String> timerConfoMap = ITFECommonConstant.timerCollectConfList;
		Set<String> set1 = timerConfoMap.keySet();
		// 取得当前系统时间：
		for (String startTime : set1) {
			String endTime = timerConfoMap.get(startTime);
			if (IsTimeIn(startTime, endTime)) {
				// 取报表中最大的账务日期
				Date date;
				SQLExecutor selExec = DatabaseFacade.getDb()
						.getSqlExecutorFactory().getSQLExecutor();
				String sqlm = "select max(s_rptdate) from TR_STOCKDAYRPT";
				SQLResults sr = selExec.runQueryCloseCon(sqlm);
				if (null != sr.getString(0, 0)) {
					date = CommonUtil.strToDate(sr.getString(0, 0));
				} else {
					date = java.sql.Date.valueOf("1900-01-01");
				}
				TsOrganDto _dto = new TsOrganDto();
				_dto.setSorgcode(StateConstant.ORG_CENTER_CODE);
				List<TsOrganDto> list = CommonFacade.getODB().findRsByDto(_dto);
				if (null != list && list.size() > 0) {// 判断是否合并过的标志
					TsOrganDto tmpdto = list.get(0);
					if (tmpdto.getIpartno() == 0) {// 说明没有合并过,徐娅萍进行合并
						HashMap<String, String> map = new HashMap<String, String>();
						TdTaxorgMergerDto dto = new TdTaxorgMergerDto();
						List<TdTaxorgMergerDto> listm = CommonFacade.getODB()
								.findRsByDto(dto);
						if (listm.size() > 0) {
							for (TdTaxorgMergerDto tmp : listm) {
								if (map.containsKey(tmp.getSaftertaxorgcode())) {
									map.put(tmp.getSaftertaxorgcode(), map
											.get(tmp.getSaftertaxorgcode())
											+ "," + tmp.getSpretaxorgcode());
								} else {
									map.put(tmp.getSaftertaxorgcode(), tmp
											.getSpretaxorgcode());
								}
							}
							Set<String> set = map.keySet();
							for (String staxorg : set) {
								String str = map.get(staxorg);
								String[] l = str.split(",");
								String where = "";
								for (int i = 0; i < l.length; i++) {
									where += "'" + l[i] + "',";
								}
								where += "'1'";
								SQLExecutor sqlExec = DatabaseFacade.getDb()
										.getSqlExecutorFactory()
										.getSQLExecutor();
								String sql = "insert into TR_INCOMEDAYRPT select S_FINORGCODE,'"
										+ staxorg
										+ "',S_TRECODE,S_RPTDATE,S_BUDGETTYPE,S_BUDGETLEVELCODE,S_BUDGETSUBCODE,S_BUDGETSUBNAME,SUM(N_MONEYDAY),SUM(N_MONEYTENDAY),SUM(N_MONEYMONTH),SUM(N_MONEYQUARTER),SUM(N_MONEYYEAR),S_BELONGFLAG,S_TRIMFLAG,S_DIVIDEGROUP,S_BILLKIND "
										+ "   from TR_INCOMEDAYRPT where  S_rptDate > ? AND S_taxorgcode in ("
										+ where
										+ ") GROUP BY S_FINORGCODE,S_TRECODE,S_RPTDATE,S_BUDGETTYPE,S_BUDGETLEVELCODE,S_BUDGETSUBCODE,S_BUDGETSUBNAME,S_BELONGFLAG,S_TRIMFLAG,S_DIVIDEGROUP,S_BILLKIND";
								if (null==tmpdto.getSpaybankno()) {
									tmpdto.setSpaybankno(date.toString());
								}
								sqlExec.addParam(tmpdto.getSpaybankno().trim().replace("-", ""));

								sqlExec.runQueryCloseCon(sql);

							}
							tmpdto.setIpartno(1);
							tmpdto.setSpaybankno(date.toString());
							DatabaseFacade.getODB().update(tmpdto);
						}
					}
				}
			}

		}

		return eventContext;
	}

	/**
	 * 判断当前时间是否在配置的时间段内
	 * 
	 * @param startTimer
	 * @param endTimer
	 * @return
	 */
	private boolean IsTimeIn(String startTimer, String endTimer) {
		Date curDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String curtime = sdf.format(curDate);
		if (curtime.compareTo(startTimer) > 0
				&& curtime.compareTo(endTimer) < 0) {
			return true;
		}
		return false;

	}

}

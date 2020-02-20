package com.cfcc.itfe.service.expreport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TdTaxorgMergerDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

public abstract class AbstractExpReport implements IMakeReport {
	final Log log = LogFactory.getLog(this.getClass());

	protected HashMap<String, String> converTaxCode(String orgCode)
			throws JAFDatabaseException, ValidateException {
		TsConverttaxorgDto dto = new TsConverttaxorgDto();
		dto.setSorgcode(orgCode);
		List<TsConverttaxorgDto> list = CommonFacade.getODB().findRsByDto(dto);
		HashMap<String, String> taxMap = new HashMap<String, String>();
		taxMap.put(MsgConstant.MSG_TAXORG_NATION_CLASS,
				MsgConstant.MSG_TAXORG_NATION_CLASS.substring(0, 10));
		taxMap.put(MsgConstant.MSG_TAXORG_PLACE_CLASS,
				MsgConstant.MSG_TAXORG_PLACE_CLASS.substring(0, 10));
		taxMap.put(MsgConstant.MSG_TAXORG_CUSTOM_CLASS,
				MsgConstant.MSG_TAXORG_CUSTOM_CLASS.substring(0, 10));
		taxMap.put(MsgConstant.MSG_TAXORG_FINANCE_CLASS,
				MsgConstant.MSG_TAXORG_FINANCE_CLASS.substring(0, 10));
		taxMap.put(MsgConstant.MSG_TAXORG_OTHER_CLASS,
				MsgConstant.MSG_TAXORG_OTHER_CLASS.substring(0, 10));
		taxMap.put(MsgConstant.MSG_TAXORG_SHARE_CLASS,
				MsgConstant.MSG_TAXORG_SHARE_CLASS.substring(0, 10));
		taxMap.put(MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS,
				MsgConstant.MSG_TAXORG_SHARE_CLASS.substring(0, 10));
		for (TsConverttaxorgDto tmpdto : list) {
			taxMap.put(tmpdto.getStcbstaxorgcode(), tmpdto.getStbstaxorgcode());
		}
		return taxMap;

	}

	/**
	 * 计算列表款合计，并返回款合计汇总列表
	 * 
	 * @param list
	 * @param strecode
	 * @return
	 */
	protected List<TrIncomedayrptDto> getSumKuanItem(
			List<TrIncomedayrptDto> list, String strecode) {
		List<TrIncomedayrptDto> retList = new ArrayList<TrIncomedayrptDto>();
		HashMap<String, TrIncomedayrptDto> map = new HashMap<String, TrIncomedayrptDto>();
		for (TrIncomedayrptDto _dto : list) {
			TrIncomedayrptDto dto = (TrIncomedayrptDto) _dto.clone();
			String key = _dto.getStrecode() + strecode + _dto.getStaxorgcode()
					+ _dto.getSrptdate() + _dto.getSbudgettype()
					+ _dto.getSbudgetlevelcode()
					+ _dto.getSbudgetsubcode().substring(0, 5);
			if (map.containsKey(key)) { // 发生额应该累加
				TrIncomedayrptDto tmpdto = map.get(key);
				tmpdto.setNmoneyday(tmpdto.getNmoneyday().add(
						_dto.getNmoneyday()));
				tmpdto.setNmoneymonth(tmpdto.getNmoneymonth().add(
						_dto.getNmoneymonth()));
				tmpdto.setNmoneyquarter(tmpdto.getNmoneyquarter().add(
						_dto.getNmoneyquarter()));
				tmpdto.setNmoneytenday(tmpdto.getNmoneytenday().add(
						_dto.getNmoneytenday()));
				tmpdto.setNmoneyyear(tmpdto.getNmoneyyear().add(
						_dto.getNmoneyyear()));
				map.put(key, tmpdto);
			} else {// 没有的话增加到MAP中
				dto.setSbudgetsubcode(dto.getSbudgetsubcode().substring(0, 5));
				map.put(key, dto);
			}
		}
		Set<String> set = map.keySet();
		for (String key : set) {
			retList.add(map.get(key));
		}
		return retList;

	}

	/**
	 * 统计合并后的征收机关报表
	 * 
	 * @param idto
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	protected List<TrIncomedayrptDto> mergeTaxOrgBill(TrIncomedayrptDto idto,
			String sbookorgcode, String rptType) throws JAFDatabaseException,
			ValidateException {

		TrTaxorgIncomedayrptDto _dto = new TrTaxorgIncomedayrptDto();
		_dto.setSrptdate(idto.getSrptdate());
		CommonFacade.getODB().deleteRsByDto(_dto);

		HashMap<String, String> mapInCome = new HashMap<String, String>();
		HashMap<String, String> mapDwbk = new HashMap<String, String>();
		TdTaxorgMergerDto dto = new TdTaxorgMergerDto();
		dto.setSbookorgcode(sbookorgcode);
		List<TdTaxorgMergerDto> listm;
		if (MsgConstant.RULE_SIGN_SELF.equals(idto.getSbelongflag())) {
			listm = CommonFacade
					.getODB()
					.findRsByDtoForWhere(
							dto,
							" AND S_PRETAXORGCODE IN (SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_ORGCODE ='"
									+ sbookorgcode
									+ "' AND S_TRECODE ='"
									+ idto.getStrecode() + "')");
		} else {
			listm = CommonFacade
					.getODB()
					.findRsByDtoForWhere(
							dto,
							" AND S_PRETAXORGCODE IN (SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_ORGCODE ='"
									+ sbookorgcode
									+ "' AND (S_TRECODE = '"
									+ idto.getStrecode() + "' OR S_TRECODE IN (SELECT S_TRECODE FROM TS_TREASURY WHERE  s_governtrecode='"+idto.getStrecode()+ "')))");
		}
		if (listm.size() > 0) {
			for (TdTaxorgMergerDto tmp : listm) {
				// 收入应该取正值
				if (StateConstant.COMMON_YES.equals(tmp.getSbiztype())) {
					if (mapInCome.containsKey(tmp.getSaftertaxorgcode())) {
						mapInCome.put(tmp.getSaftertaxorgcode(), mapInCome
								.get(tmp.getSaftertaxorgcode())
								+ "," + tmp.getSpretaxorgcode());
					} else {
						mapInCome.put(tmp.getSaftertaxorgcode(), tmp
								.getSpretaxorgcode());
					}
				} else {
					if (mapDwbk.containsKey(tmp.getSaftertaxorgcode())) {
						mapDwbk.put(tmp.getSaftertaxorgcode(), mapDwbk.get(tmp
								.getSaftertaxorgcode())
								+ "," + tmp.getSpretaxorgcode());
					} else {
						mapDwbk.put(tmp.getSaftertaxorgcode(), tmp
								.getSpretaxorgcode());
					}
				}
			}
			Set<String> setIncome = mapInCome.keySet();
			for (String staxorg : setIncome) {
				String str = mapInCome.get(staxorg);
				String[] l = str.split(",");
				String where = "";
				for (int i = 0; i < l.length; i++) {
					where += "'" + l[i] + "',";
				}
				where += "'1'";

				SQLExecutor sqlExec = DatabaseFacade.getDb()
						.getSqlExecutorFactory().getSQLExecutor();
				String sql = " insert into TR_TAXORG_INCOMEDAYRPT select '"
						+ staxorg
						+ "' as s_taxorgcode,S_TRECODE,S_RPTDATE,S_BUDGETTYPE,S_BUDGETLEVELCODE,S_BUDGETSUBCODE,S_BUDGETSUBNAME,SUM(N_MONEYDAY),SUM(N_MONEYTENDAY),SUM(N_MONEYMONTH),SUM(N_MONEYQUARTER),SUM(N_MONEYYEAR),S_BELONGFLAG,S_TRIMFLAG,S_DIVIDEGROUP,'1' "
						+ "   from TR_INCOMEDAYRPT where  S_rptDate = ? AND  (S_BILLKIND =? OR S_BILLKIND =? ) AND S_TRIMFLAG = ? AND S_BUDGETTYPE =? AND S_taxorgcode in ("
						+ where
						+ ") GROUP BY S_FINORGCODE,S_TRECODE,S_RPTDATE,S_BUDGETTYPE,S_BUDGETLEVELCODE,S_BUDGETSUBCODE,S_BUDGETSUBNAME,S_BELONGFLAG,S_TRIMFLAG,S_DIVIDEGROUP";

				sqlExec.addParam(idto.getSrptdate().replace("-", ""));
				sqlExec.addParam(rptType);
				sqlExec.addParam(StateConstant.REPORTTYPE_FLAG_TRBUDGETBILL);
				sqlExec.addParam(idto.getStrimflag());
				sqlExec.addParam(idto.getSbudgettype());
				sqlExec.runQueryCloseCon(sql);
			}
			Set<String> setDwbk = mapDwbk.keySet();
			for (String staxorg : setDwbk) {
				String str = mapDwbk.get(staxorg);
				String[] l = str.split(",");
				String where = "";
				for (int i = 0; i < l.length; i++) {
					where += "'" + l[i] + "',";
				}
				where += "'1'";
				SQLExecutor sqlExec = DatabaseFacade.getDb()
						.getSqlExecutorFactory().getSQLExecutor();
				String sql = "insert into TR_TAXORG_INCOMEDAYRPT select '"
						+ staxorg
						+ "',S_TRECODE,S_RPTDATE,S_BUDGETTYPE,S_BUDGETLEVELCODE,S_BUDGETSUBCODE,S_BUDGETSUBNAME,SUM(-N_MONEYDAY),SUM(-N_MONEYTENDAY),SUM(-N_MONEYMONTH),SUM(-N_MONEYQUARTER),SUM(-N_MONEYYEAR),S_BELONGFLAG,S_TRIMFLAG,S_DIVIDEGROUP,'2' "
						+ "   from TR_INCOMEDAYRPT where  S_rptDate = ? AND   (S_BILLKIND =? OR S_BILLKIND = ? ) AND S_TRIMFLAG = ? AND S_BUDGETTYPE =?  AND S_taxorgcode in ("
						+ where
						+ ") GROUP BY S_FINORGCODE,S_TRECODE,S_RPTDATE,S_BUDGETTYPE,S_BUDGETLEVELCODE,S_BUDGETSUBCODE,S_BUDGETSUBNAME,S_BELONGFLAG,S_TRIMFLAG,S_DIVIDEGROUP";

				sqlExec.addParam(idto.getSrptdate().replace("-", ""));
				sqlExec.addParam(StateConstant.REPORTTYPE_FLAG_NRDRAWBACKBILL);
				sqlExec.addParam(StateConstant.REPORTTYPE_FLAG_TRDRAWBACKBILL);
				sqlExec.addParam(idto.getStrimflag());
				sqlExec.addParam(idto.getSbudgettype());

				sqlExec.runQueryCloseCon(sql);
			}
		}
		SQLExecutor sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
				.getSQLExecutor();
		String sql = "select S_TAXORGCODE,S_TRECODE,S_RPTDATE,S_BUDGETTYPE,S_BUDGETLEVELCODE,S_BUDGETSUBCODE,S_BUDGETSUBNAME,SUM(N_MONEYDAY) AS N_MONEYDAY,SUM(N_MONEYTENDAY) AS N_MONEYTENDAY,SUM(N_MONEYMONTH) AS N_MONEYMONTH,SUM(N_MONEYQUARTER) AS N_MONEYQUARTER ,SUM(N_MONEYYEAR) As N_MONEYYEAR,S_BELONGFLAG,S_TRIMFLAG FROM TR_TAXORG_INCOMEDAYRPT "
				+ " WHERE S_rptDate = ? AND S_TAXORGCODE IN (SELECT DISTINCT S_AFTERTAXORGCODE  FROM TD_TAXORG_MERGER WHERE S_BOOKORGCODE =? )  GROUP by S_TAXORGCODE,S_TRECODE,S_RPTDATE,S_BUDGETTYPE,S_BUDGETLEVELCODE,S_BUDGETSUBCODE,S_BUDGETSUBNAME,S_BELONGFLAG,S_TRIMFLAG";
		sqlExec.addParam(idto.getSrptdate().replace("-", ""));
		sqlExec.addParam(sbookorgcode);
		List<TrIncomedayrptDto> list = (List<TrIncomedayrptDto>) sqlExec
				.runQueryCloseCon(sql, TrIncomedayrptDto.class)
				.getDtoCollection();

		CommonFacade.getODB().deleteRsByDto(_dto);

		return list;
	}

	/**
	 * 统计合并后的征收机关报表
	 * 
	 * @description 【特殊用于共享分成报表导出】
	 * @param idto
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	protected List<TrIncomedayrptDto> mergeTaxOrgBillForShareRpt(
			TrIncomedayrptDto idto, String sbookorgcode, String rptType)
			throws JAFDatabaseException, ValidateException {

		TrTaxorgIncomedayrptDto _dto = new TrTaxorgIncomedayrptDto();
		_dto.setSrptdate(idto.getSrptdate());
		CommonFacade.getODB().deleteRsByDto(_dto);

		HashMap<String, String> mapInCome = new HashMap<String, String>();
		HashMap<String, String> mapDwbk = new HashMap<String, String>();
		TdTaxorgMergerDto dto = new TdTaxorgMergerDto();
		dto.setSbookorgcode(sbookorgcode);
		List<TdTaxorgMergerDto> listm;
		if (MsgConstant.RULE_SIGN_SELF.equals(idto.getSbelongflag())) {
			listm = CommonFacade
					.getODB()
					.findRsByDtoForWhere(
							dto,
							" AND S_PRETAXORGCODE IN (SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_ORGCODE ='"
									+ sbookorgcode
									+ "' AND S_TRECODE ='"
									+ idto.getStrecode() + "')");
		} else {
			listm = CommonFacade
					.getODB()
					.findRsByDtoForWhere(
							dto,
							" AND S_PRETAXORGCODE IN (SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_ORGCODE ='"
									+ sbookorgcode
									+ "' AND (S_TRECODE = '"
									+ idto.getStrecode() + "' OR S_TRECODE IN (SELECT S_TRECODE FROM TS_TREASURY WHERE  s_governtrecode='"+idto.getStrecode()+ "')))");
		}
		if (listm.size() > 0) {
			for (TdTaxorgMergerDto tmp : listm) {
				// 收入应该取正值
				if (StateConstant.COMMON_YES.equals(tmp.getSbiztype())) {
					if (mapInCome.containsKey(tmp.getSaftertaxorgcode())) {
						mapInCome.put(tmp.getSaftertaxorgcode(), mapInCome
								.get(tmp.getSaftertaxorgcode())
								+ "," + tmp.getSpretaxorgcode());
					} else {
						mapInCome.put(tmp.getSaftertaxorgcode(), tmp
								.getSpretaxorgcode());
					}
				} else {
					if (mapDwbk.containsKey(tmp.getSaftertaxorgcode())) {
						mapDwbk.put(tmp.getSaftertaxorgcode(), mapDwbk.get(tmp
								.getSaftertaxorgcode())
								+ "," + tmp.getSpretaxorgcode());
					} else {
						mapDwbk.put(tmp.getSaftertaxorgcode(), tmp
								.getSpretaxorgcode());
					}
				}
			}
			Set<String> setIncome = mapInCome.keySet();
			for (String staxorg : setIncome) {
				String str = mapInCome.get(staxorg);
				String[] l = str.split(",");
				String where = "";
				for (int i = 0; i < l.length; i++) {
					where += "'" + l[i] + "',";
				}
				where += "'1'";

				SQLExecutor sqlExec = DatabaseFacade.getDb()
						.getSqlExecutorFactory().getSQLExecutor();
				String sql = " insert into TR_TAXORG_INCOMEDAYRPT select '"
						+ staxorg
						+ "' as s_taxorgcode,S_TRECODE,S_RPTDATE,S_BUDGETTYPE,S_BUDGETLEVELCODE,S_BUDGETSUBCODE,S_BUDGETSUBNAME,SUM(N_MONEYDAY),SUM(N_MONEYTENDAY),SUM(N_MONEYMONTH),SUM(N_MONEYQUARTER),SUM(N_MONEYYEAR),S_BELONGFLAG,S_TRIMFLAG,S_DIVIDEGROUP,'1' "
						+ "   from TR_INCOMEDAYRPT where  S_rptDate = ? AND  S_BILLKIND =? AND S_TRIMFLAG = ? AND S_BUDGETLEVELCODE = ? AND S_BUDGETTYPE =? AND S_taxorgcode in ("
						+ where
						+ ") GROUP BY S_FINORGCODE,S_TRECODE,S_RPTDATE,S_BUDGETTYPE,S_BUDGETLEVELCODE,S_BUDGETSUBCODE,S_BUDGETSUBNAME,S_BELONGFLAG,S_TRIMFLAG,S_DIVIDEGROUP";

				sqlExec.addParam(idto.getSrptdate().replace("-", ""));
				sqlExec.addParam(rptType);
				sqlExec.addParam(idto.getStrimflag());
				sqlExec.addParam(StateConstant.shareBudgetLevel);
				sqlExec.addParam(idto.getSbudgettype());
				sqlExec.runQueryCloseCon(sql);
			}
			Set<String> setDwbk = mapDwbk.keySet();
			for (String staxorg : setDwbk) {
				String str = mapDwbk.get(staxorg);
				String[] l = str.split(",");
				String where = "";
				for (int i = 0; i < l.length; i++) {
					where += "'" + l[i] + "',";
				}
				where += "'1'";
				SQLExecutor sqlExec = DatabaseFacade.getDb()
						.getSqlExecutorFactory().getSQLExecutor();
				String sql = "insert into TR_TAXORG_INCOMEDAYRPT select '"
						+ staxorg
						+ "',S_TRECODE,S_RPTDATE,S_BUDGETTYPE,S_BUDGETLEVELCODE,S_BUDGETSUBCODE,S_BUDGETSUBNAME,SUM(-N_MONEYDAY),SUM(-N_MONEYTENDAY),SUM(-N_MONEYMONTH),SUM(-N_MONEYQUARTER),SUM(-N_MONEYYEAR),S_BELONGFLAG,S_TRIMFLAG,S_DIVIDEGROUP,'2' "
						+ "   from TR_INCOMEDAYRPT where  S_rptDate = ? AND   (S_BILLKIND =? OR S_BILLKIND = ? ) AND S_BUDGETLEVELCODE = ? AND S_TRIMFLAG = ? AND S_BUDGETTYPE =?  AND S_taxorgcode in ("
						+ where
						+ ") GROUP BY S_FINORGCODE,S_TRECODE,S_RPTDATE,S_BUDGETTYPE,S_BUDGETLEVELCODE,S_BUDGETSUBCODE,S_BUDGETSUBNAME,S_BELONGFLAG,S_TRIMFLAG,S_DIVIDEGROUP";

				sqlExec.addParam(idto.getSrptdate().replace("-", ""));
				sqlExec.addParam(StateConstant.REPORTTYPE_FLAG_NRDRAWBACKBILL);
				sqlExec.addParam(StateConstant.REPORTTYPE_FLAG_TRDRAWBACKBILL);
				sqlExec.addParam(StateConstant.shareBudgetLevel);
				sqlExec.addParam(idto.getStrimflag());
				sqlExec.addParam(idto.getSbudgettype());

				sqlExec.runQueryCloseCon(sql);
			}
		}
		SQLExecutor sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
				.getSQLExecutor();
		String sql = "select S_TAXORGCODE,S_TRECODE,S_RPTDATE,S_BUDGETTYPE,S_BUDGETLEVELCODE,S_BUDGETSUBCODE,S_BUDGETSUBNAME,SUM(N_MONEYDAY) AS N_MONEYDAY,SUM(N_MONEYTENDAY) AS N_MONEYTENDAY,SUM(N_MONEYMONTH) AS N_MONEYMONTH,SUM(N_MONEYQUARTER) AS N_MONEYQUARTER ,SUM(N_MONEYYEAR) As N_MONEYYEAR,S_BELONGFLAG,S_TRIMFLAG FROM TR_TAXORG_INCOMEDAYRPT "
				+ " WHERE S_rptDate = ? AND S_TAXORGCODE IN (SELECT DISTINCT S_AFTERTAXORGCODE  FROM TD_TAXORG_MERGER WHERE S_BOOKORGCODE =? )  GROUP by S_TAXORGCODE,S_TRECODE,S_RPTDATE,S_BUDGETTYPE,S_BUDGETLEVELCODE,S_BUDGETSUBCODE,S_BUDGETSUBNAME,S_BELONGFLAG,S_TRIMFLAG";
		sqlExec.addParam(idto.getSrptdate().replace("-", ""));
		sqlExec.addParam(sbookorgcode);
		List<TrIncomedayrptDto> list = (List<TrIncomedayrptDto>) sqlExec
				.runQueryCloseCon(sql, TrIncomedayrptDto.class)
				.getDtoCollection();

		CommonFacade.getODB().deleteRsByDto(_dto);

		return list;
	}

	/**
	 * 对报表List进行排序
	 * 
	 * @author db2admin
	 * 
	 */
	protected class SortByDayReport implements Comparator {
		public int compare(Object o1, Object o2) {
			TrIncomedayrptDto _dto = (TrIncomedayrptDto) o1;
			TrIncomedayrptDto dto = (TrIncomedayrptDto) o2;

			String c1 = _dto.getStrecode() + _dto.getStaxorgcode()
					+ _dto.getSrptdate() + _dto.getSbudgettype()
					+ _dto.getSbudgetlevelcode() + _dto.getSbudgetsubcode();
			String c2 = dto.getStrecode() + dto.getStaxorgcode()
					+ dto.getSrptdate() + dto.getSbudgettype()
					+ dto.getSbudgetlevelcode() + dto.getSbudgetsubcode();
			return c1.compareTo(c2);
		}
	}

	/**
	 * 获取大类征收机关dass
	 * 
	 * @returna
	 */
	protected HashMap<String, String> getBigKindTaxorg() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(MsgConstant.MSG_TAXORG_NATION_CLASS, "");
		map.put(MsgConstant.MSG_TAXORG_PLACE_CLASS, "");
		map.put(MsgConstant.MSG_TAXORG_CUSTOM_CLASS, "");
		map.put(MsgConstant.MSG_TAXORG_FINANCE_CLASS, "");
		map.put(MsgConstant.MSG_TAXORG_OTHER_CLASS, "");
		map.put(MsgConstant.MSG_TAXORG_SHARE_CLASS, "");
		return map;

	}
}

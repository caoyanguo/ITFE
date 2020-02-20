package com.cfcc.itfe.service.expreport;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dao.TvInfileDao;
import com.cfcc.itfe.persistence.dto.AddColumnPayOutDto;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.service.dataquery.findatastatdown.AbstractFinDataStatDownService;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author db2admin
 * @time 12-02-25 21:02:16 codecomment:
 */

public class CommonMakeReportForTJ implements IService {
	private static Log log = LogFactory.getLog(CommonMakeReportForTJ.class);

	/**
	 * 
	 * 
	 * @return
	 */
	public List getReportDataByBllType(TrIncomedayrptDto idto, String billType) {
		return null;

	}

	/**
	 * 生成报表取数的条件
	 * 
	 * @param idto
	 * @return
	 * @throws ITFEBizException
	 */
	public static String makesqlwhere(IDto idto, HashMap map, List proxyTreList) throws ITFEBizException {
		TrIncomedayrptDto incomedto = (TrIncomedayrptDto) idto;
		// 国库主体代码
		String strecode = incomedto.getStrecode();
		// 征收机关代码
		String staxorg = incomedto.getStaxorgcode();
		// 预算种类
		String bugtype = incomedto.getSbudgettype();
		// 辖属标志
		String sbelong = incomedto.getSbelongflag();
		// 调整期标志
		String strimflag = incomedto.getStrimflag();
		// 日期
		String sdate = incomedto.getSrptdate();
		// 代理库日期
		String agentDate = (String)map.get("agentDate");
		// 月末代理库日期
		String agentEndDate = (String)map.get("agentEndDate");
		// 是否含款合计
		String slesumitem = incomedto.getSdividegroup();
		String sqlwhere = "";
		HashMap treLevels = (HashMap) map.get("treLevels");
		treLevels.remove("0288000000");
		Set set = treLevels.keySet();
		//取得单选项List
		List inputList = (List) map.get("inputList");
		
		// 全辖--征收机关大类--国库代码
		if (MsgConstant.RULE_SIGN_ALL.equals(sbelong)) {
			if (null == staxorg) {
				sqlwhere += " and s_trecode ='" + strecode + "' "
						+ " and S_BELONGFLAG ='" + sbelong + "' "
						+ " and S_RPTDATE = '" + sdate+ "' ";
			} else {
				if(staxorg.equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS)
					|| (staxorg.equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS) && "0200000000".equals(strecode))){// 征收机关大类--财政\天津海关(天津模式)
					sqlwhere += " and s_trecode ='" + strecode + "' "
						+ " and s_TaxOrgCode ='" + staxorg + "' "
						+ " and S_BELONGFLAG ='" + sbelong + "' "
						+ " and S_RPTDATE = '" + sdate+ "' ";
				}else if (staxorg.equals(MsgConstant.MSG_TAXORG_NATION_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_PLACE_CLASS)
						|| (staxorg.equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS)&& !"0200000000".equals(strecode))//塘沽海关
//						|| staxorg.equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_OTHER_CLASS)) {// 征收机关大类(天津模式)
					sqlwhere += " and ((s_trecode ='"+strecode+"' and S_BELONGFLAG ='" + MsgConstant.RULE_SIGN_SELF + "' and S_RPTDATE = '"+sdate+"'";
					sqlwhere += " and S_TAXORGCODE IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_TRECODE='" + strecode +"' AND S_TAXPROP='"+staxorg.substring(0, 1)+"'))";
					for(Object key : set){
						String skey = (String)key;
						String agent = agentDate;
						if(proxyTreList.contains(skey)){
							agent = agentDate;
						}else{
							agent = sdate;
						}
						ArrayList list = (ArrayList) treLevels.get(skey);
						if(list.size() != 0){
							sqlwhere += " or (s_trecode ='" + skey +"' and (S_BUDGETLEVELCODE <>'"+list.get(0)+"'";
							for(int i=1; i<list.size(); i++){
								sqlwhere += "and S_BUDGETLEVELCODE <>'"+list.get(i)+"'";
							}
							sqlwhere += ")";
							sqlwhere += " and S_TAXORGCODE IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_TRECODE='" + key +"' AND S_TAXPROP='"+staxorg.substring(0, 1)+"')";
							sqlwhere += " and S_BELONGFLAG ='" + MsgConstant.RULE_SIGN_SELF + "' and (S_RPTDATE = '"+agent+"'";
							if(!"".equals(agentEndDate)){
								sqlwhere += " or S_RPTDATE = '" + agentEndDate + "'))"; 
							}else{
								sqlwhere += "))";
							}
						}else{
							sqlwhere += " or (s_trecode ='" + skey +"'";
							sqlwhere += " and S_TAXORGCODE IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_TRECODE='" + key +"' AND S_TAXPROP='"+staxorg.substring(0, 1)+"')";
							sqlwhere += " and S_BELONGFLAG ='" + MsgConstant.RULE_SIGN_SELF + "' and (S_RPTDATE = '"+agent+"'";
							if(!"".equals(agentEndDate)){
								sqlwhere += " or S_RPTDATE = '" + agentEndDate + "'))"; 
							}else{
								sqlwhere += "))";
							}
						}
					}
					if(inputList.size() > 0){
						for(int i = 0; i < inputList.size(); i++){
							ArrayList inputList1 = (ArrayList) inputList.get(i);
							sqlwhere += " or (s_trecode ='" + inputList1.get(1) +"'";
							ArrayList levelList = (ArrayList) inputList1.get(2);
							sqlwhere += "and (S_BUDGETLEVELCODE ='" + levelList.get(0) + "'";
							for(int j = 1; j < levelList.size(); j++){
								sqlwhere += " or S_BUDGETLEVELCODE ='" + levelList.get(j) + "'";
							}
							sqlwhere += ")";
							sqlwhere += " and S_TAXORGCODE IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_TRECODE='" + inputList1.get(1) +"' AND S_TAXPROP='"+staxorg.substring(0, 1)+"') and S_BELONGFLAG ='" + MsgConstant.RULE_SIGN_SELF + "' and S_RPTDATE = '"+inputList1.get(0)+"') ";
						}
					}
					sqlwhere += ")";
				} else if (staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)) {// 不分征收机关(天津模式)
					sqlwhere += " and ((s_trecode ='"+strecode+"' and S_BELONGFLAG ='" + MsgConstant.RULE_SIGN_SELF + "' and S_RPTDATE = '"+sdate+"')";
					for(Object key : set){
						String skey = (String)key;
						String agent = agentDate;
						if(proxyTreList.contains(skey)){
							agent = agentDate;
						}else{
							agent = sdate;
						}
						ArrayList list = (ArrayList) treLevels.get(skey);
						if(list.size() != 0){
							sqlwhere += " or (s_trecode ='" + skey +"' and (S_BUDGETLEVELCODE <>'"+list.get(0)+"'";
							for(int i=1; i<list.size(); i++){
								sqlwhere += "and S_BUDGETLEVELCODE <>'"+list.get(i)+"'";
							}
							sqlwhere += ")";
							sqlwhere += " and S_BELONGFLAG ='" + MsgConstant.RULE_SIGN_SELF + "' and (S_RPTDATE = '"+agent+"'";
							if(!"".equals(agentEndDate)){
								sqlwhere += " or S_RPTDATE = '" + agentEndDate + "'))"; 
							}else{
								sqlwhere += "))";
							}
						}else{
							sqlwhere += " or (s_trecode ='" + skey +"'";
							sqlwhere += " and S_BELONGFLAG ='" + MsgConstant.RULE_SIGN_SELF + "' and (S_RPTDATE = '"+agent+"'";
							if(!"".equals(agentEndDate)){
								sqlwhere += " or S_RPTDATE = '" + agentEndDate + "'))"; 
							}else{
								sqlwhere += "))";
							}
						}
					}
					if(inputList.size() > 0){
						for(int i = 0; i < inputList.size(); i++){
							ArrayList inputList1 = (ArrayList) inputList.get(i);
							sqlwhere += " or (s_trecode ='" + inputList1.get(1) +"'";
							ArrayList levelList = (ArrayList) inputList1.get(2);
							sqlwhere += "and (S_BUDGETLEVELCODE ='" + levelList.get(0) + "'";
							for(int j = 1; j < levelList.size(); j++){
								sqlwhere += " or S_BUDGETLEVELCODE ='" + levelList.get(j) + "'";
							}
							sqlwhere += ")";
							sqlwhere += " and S_BELONGFLAG ='" + MsgConstant.RULE_SIGN_SELF + "' and S_RPTDATE = '"+inputList1.get(0)+"') ";
						}
					}
					sqlwhere += ")";
				} else {// 具体征收机关
					sqlwhere += " and s_trecode ='" + strecode + "' "
							+ " and s_TaxOrgCode ='" + staxorg + "' "
							+ " and S_BELONGFLAG ='" + MsgConstant.RULE_SIGN_SELF + "' and S_RPTDATE = '"+sdate+"'";
				}
			}
		}
		// 本级--征收机关大类或具体征收机关--国库代码
		if (MsgConstant.RULE_SIGN_SELF.equals(sbelong)) {
			if (null == staxorg) {
				sqlwhere += " and s_trecode ='" + strecode + "' "
						+ " and S_BELONGFLAG ='" + sbelong + "' ";
			} else {
				if (staxorg.equals(MsgConstant.MSG_TAXORG_NATION_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_PLACE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_OTHER_CLASS)) {// 征收机关大类
					sqlwhere += " and s_trecode ='"
							+ strecode
							+ "' "
							+ " and s_TaxOrgCode IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_TRECODE='"
							+ strecode + "' AND S_TAXPROP='"
							+ staxorg.substring(0, 1) + "' ) "
							+ " and S_BELONGFLAG ='" + sbelong +"' "
							+ " and S_RPTDATE = '" + sdate+ "' ";
				} else if (staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)) {// 不分征收机关
					sqlwhere += " and s_trecode ='"
							+ strecode
							+ "' "
							+ " and s_TaxOrgCode IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_TRECODE='"
							+ strecode + "' ) " + " and S_BELONGFLAG ='"
							+ sbelong + "' and S_RPTDATE = '" + sdate + "' ";
				} else {// 具体征收机关
					sqlwhere += " and s_trecode ='" + strecode + "' "
							+ " and s_TaxOrgCode ='" + staxorg + "' "
							+ " and S_BELONGFLAG ='" + sbelong + "' "
							+ " and S_RPTDATE = '" + sdate +"' ";
				}
			}
		}
		// 全辖非本级--征收机关大类--国库代码：所有辖属国库的全辖数据之和，但不包括本级国库全辖数
		if (MsgConstant.RULE_SIGN_ALLNOTSELF.equals(sbelong)) {
			if (null == staxorg) {
				sqlwhere += " and s_trecode ='" + strecode + "' "
						+ " and S_BELONGFLAG ='" + sbelong + "' ";
			} else {
				if (staxorg.equals(MsgConstant.MSG_TAXORG_NATION_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_PLACE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_OTHER_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)) {
					// 国库全辖"
					sqlwhere += " and s_trecode in ( "
							+ " SELECT s_trecode FROM TS_TREASURY WHERE  s_governtrecode='"
							+ strecode + "' " + " and s_TaxOrgCode ='"
							+ staxorg + "' " + " and S_BELONGFLAG ='"
							+ MsgConstant.RULE_SIGN_ALL + "') "
							+ " and S_RPTDATE = '" + sdate +"' ";

				} else {
					throw new ITFEBizException(
							"查询条件：辖属标志位全辖非本级的时候，征收机关只允许选择征收大类！");
				}
			}
		}
		return sqlwhere;

	}

	/**
	 * 根据下载报表的种类得到报表业务类型
	 * 
	 * @return
	 */
	public static String getReportTypeByBillType(TrIncomedayrptDto idto,
			String billType) {
		if (idto.getStrimflag().equals(StateConstant.TRIMSIGN_FLAG_NORMAL)) {// 正常期
			if (billType.equals(StateConstant.REPORTTYPE_1)) {
				return StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL;
			} else if (billType.equals(StateConstant.REPORTTYPE_3)) {
				return StateConstant.REPORTTYPE_FLAG_NRREMOVEBILL;
			} else if (billType.equals(StateConstant.REPORTTYPE_8)) {
				return StateConstant.REPORTTYPE_FLAG_NRDRAWBACKBILL;
			} else if (billType.equals(StateConstant.REPORTTYPE_7)) {
				return StateConstant.REPORTTYPE_FLAG_AMOUNTBILL;
			} else if (billType.equals(StateConstant.REPORTTYPE_4)) {
				return StateConstant.REPORTTYPE_FLAG_NRSHAREBILL;
			}
		} else {
			if (billType.equals(StateConstant.REPORTTYPE_1)) {
				return StateConstant.REPORTTYPE_FLAG_TRBUDGETBILL;
			} else if (billType.equals(StateConstant.REPORTTYPE_3)) {
				return StateConstant.REPORTTYPE_FLAG_TRREMOVEBILL;
			} else if (billType.equals(StateConstant.REPORTTYPE_8)) {
				return StateConstant.REPORTTYPE_FLAG_TRDRAWBACKBILL;
			} else if (billType.equals(StateConstant.REPORTTYPE_7)) {
				return StateConstant.REPORTTYPE_FLAG_AMOUNTBILL;
			} else if (billType.equals(StateConstant.REPORTTYPE_4)) {
				return StateConstant.REPORTTYPE_FLAG_TRSHAREBILL;
			}
		}
		return null;

	}

	/**
	 * 根据报表类型得到文件名称
	 * 
	 * @return
	 */
	public static String getExpFileNameByBillType(TrIncomedayrptDto idto,
			String billType) {
		String filetype = getExpFileBillType(idto, billType);
		String filename = "s" + idto.getStrecode() + "_" + filetype
				+ idto.getSbelongflag();
		if (idto.getSbudgettype().equals(StateConstant.BudgetType_OUT)) {
			filename += StateConstant.BudgetType_OUT;
		}
		filename += idto.getSrptdate() + "_"+idto.getStaxorgcode().substring(0, 10) + ".txt";
		return filename;
	}

	/**
	 * 根据报表类型得到文件名称中的业务类型
	 * 
	 * @return
	 */
	public static String getExpFileBillType(TrIncomedayrptDto idto,
			String billType) {
		if (billType.equals(StateConstant.REPORTTYPE_5)) {
			return StateConstant.ReportName_c;
		} else if (billType.equals(StateConstant.REPORTTYPE_6)) {
			return StateConstant.ReportName_7;
		} else if (billType.equals(StateConstant.REPORTTYPE_7)) {
			return StateConstant.ReportName_0;
		} else if (billType.equals(StateConstant.REPORTTYPE_8)) {
			return StateConstant.ReportName_6;
		} else if (billType.equals(StateConstant.REPORTTYPE_9)) {
			return StateConstant.ReportName_8;
		} else if (billType.equals(StateConstant.REPORTTYPE_10)) {
			return StateConstant.ReportName_z;
		} else if (billType.equals(StateConstant.REPORTTYPE_11)) {
			return StateConstant.ReportName_t;
		} else if (billType.equals(StateConstant.REPORTTYPE_12)) {
			return StateConstant.ReportName_g;
		} else {
			return billType;
		}

	}

}
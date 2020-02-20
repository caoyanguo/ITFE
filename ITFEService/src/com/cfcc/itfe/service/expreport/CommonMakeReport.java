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
import com.cfcc.itfe.persistence.dto.TdTaxorgMergerDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.service.dataquery.findatastatdown.AbstractFinDataStatDownService;
import com.cfcc.itfe.service.dataquery.findatastatdown.FinDataStatDownService;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author db2admin
 * @time 12-02-25 21:02:16 codecomment:
 */

public class CommonMakeReport implements IService {
	private static Log log = LogFactory.getLog(CommonMakeReport.class);

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
	public static String makesqlwhere(IDto idto) throws ITFEBizException {
		
		TrIncomedayrptDto incomedto = (TrIncomedayrptDto) idto;
		// 国库主体代码
		String strecode = incomedto.getStrecode();
		// 征收机关代码
		String staxorg = incomedto.getStaxorgcode();
		List<String> taxorglist=getTaxorgList(strecode,staxorg);
		String uniontaxorg="";
		if(staxorg!=null){
			for(String str:taxorglist){
				uniontaxorg+="'"+str+"',";
			}
			uniontaxorg=uniontaxorg.substring(0, uniontaxorg.length()-1);
		}
		
		// 预算种类
		String bugtype = incomedto.getSbudgettype();
		// 辖属标志
		String sbelong = incomedto.getSbelongflag();
		// 调整期标志
		String strimflag = incomedto.getStrimflag();
		// 日期
		String sdate = incomedto.getSrptdate();
		// 是否含款合计
		String slesumitem = incomedto.getSdividegroup();
		String sqlwhere = "";
		
		
		// 全辖--征收机关大类--国库代码
		if (MsgConstant.RULE_SIGN_ALL.equals(sbelong)) {
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
					sqlwhere += " and s_trecode ='" + strecode + "' "
							+ " and s_TaxOrgCode in (" + uniontaxorg + ") "
							+ " and S_BELONGFLAG ='" + sbelong + "' ";
				} else if(staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS)){
					//珠海报表特殊处理
					if ("1913000000".equals(strecode)
							&& (null != incomedto.getSbillkind()
							&& !"".equals(incomedto.getSbillkind())
							&& (StateConstant.REPORTTYPE_FLAG_NRSHAREBILL.equals(incomedto.getSbillkind()) || StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL.equals(incomedto.getSbillkind())|| StateConstant.REPORTTYPE_FLAG_TRBUDGETBILL.equals(incomedto.getSbillkind())|| StateConstant.REPORTTYPE_FLAG_TRSHAREBILL.equals(incomedto.getSbillkind())))) { 
						//本级国库本机收入报表
						sqlwhere += " and (( s_trecode ='"
							+ strecode
							+ "' "
							+ " and s_TaxOrgCode IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_TRECODE='"
							+ strecode + "') " 
							+ " and S_BELONGFLAG ='" + MsgConstant.RULE_SIGN_SELF+ "' ) ";
						
						//下辖国库的全辖报表
						// 国库全辖"
						sqlwhere += " or ( s_trecode in ( "
								+ " SELECT s_trecode FROM TS_TREASURY WHERE  s_governtrecode='"
								+ strecode + "') " + " and s_TaxOrgCode not in ('"
								+ MsgConstant.MSG_TAXORG_SHARE_CLASS + "') " + " and S_BELONGFLAG ='"
								+ MsgConstant.RULE_SIGN_SELF + "')) ";
					}else {
						/**
						 * 解决调拨收入报表导出时，当选择全辖时只能导出下级国库全辖数的问题，核心问题在于TCBS下发调拨收入报表时
						 * 默认了征收机关代码为444444444444，所以当取本级数据时就无法取出。
						 * 主要修改两点(改动最小)：
						 * 		1-将征收机关代码范围写死为444444444444;
						 * 		2-辖属标志范围改为全辖;
						 * by hua -20160822
						 * 
						 * 补充修改：增加征收机关范围为111111111111~555555555555
						 * by hua -20161027
						 */
						if(null != incomedto && null != incomedto.getSbillkind() && !"".equals(incomedto.getSbillkind()) && StateConstant.REPORTTYPE_FLAG_NRREMOVEBILL.equals(incomedto.getSbillkind())) {
							//本级国库本机收入报表
							sqlwhere += " and (( s_trecode ='"
								+ strecode
								+ "' "
								+ " and s_TaxOrgCode IN ('"+MsgConstant.MSG_TAXORG_NATION_CLASS+"','"+MsgConstant.MSG_TAXORG_PLACE_CLASS+"','"+MsgConstant.MSG_TAXORG_CUSTOM_CLASS+"','"+MsgConstant.MSG_TAXORG_FINANCE_CLASS+"','"+MsgConstant.MSG_TAXORG_OTHER_CLASS+"') " 
								+ " and S_BELONGFLAG ='" + MsgConstant.RULE_SIGN_ALL+ "' ) ";
						} else {
							//本级国库本机收入报表
							sqlwhere += " and (( s_trecode ='"
								+ strecode
								+ "' "
								+ " and s_TaxOrgCode IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_TRECODE='"
								+ strecode + "') " 
								+ " and S_BELONGFLAG ='" + MsgConstant.RULE_SIGN_SELF+ "' ) ";
						}
						
						//下辖国库的全辖报表
						// 国库全辖"
						sqlwhere += " or ( s_trecode in ( "
								+ " SELECT s_trecode FROM TS_TREASURY WHERE  s_governtrecode='"
								+ strecode + "') " + " and s_TaxOrgCode not in ('"
								+ MsgConstant.MSG_TAXORG_SHARE_CLASS + "') " + " and S_BELONGFLAG ='"
								+ MsgConstant.RULE_SIGN_ALL + "')) ";
					}
			    } else{
					throw new ITFEBizException("查询条件：辖属标志位全辖的时候，征收机关只允许选择征收大类！");
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
						+ " and S_BELONGFLAG ='" + sbelong + "' ";
				} else if (staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS) || staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS)) {// 不分征收机关
					sqlwhere += " and s_trecode ='"
						+ strecode
						+ "' "
						+ " and s_TaxOrgCode IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_TRECODE='"
						+ strecode + "' ) " + " and S_BELONGFLAG ='"
						+ sbelong + "' ";
				} else {// 具体征收机关
					sqlwhere += " and s_trecode ='" + strecode + "' "
							+ " and s_TaxOrgCode in (" + uniontaxorg + ") "
							+ " and S_BELONGFLAG ='" + sbelong + "' ";
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
							+ strecode + "' " + " and s_TaxOrgCode in ("
							+ uniontaxorg + ") " + " and S_BELONGFLAG ='"
							+ MsgConstant.RULE_SIGN_ALL + "') ";
				} else if (staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS)){
					// 国库全辖"
					sqlwhere += " and s_trecode in ( "
							+ " SELECT s_trecode FROM TS_TREASURY WHERE  s_governtrecode='"
							+ strecode + "' " + " and s_TaxOrgCode not in ('"
							+  MsgConstant.MSG_TAXORG_SHARE_CLASS + "') " + " and S_BELONGFLAG ='"
							+ MsgConstant.RULE_SIGN_ALL + "') ";
					
				}else {
					throw new ITFEBizException(
							"查询条件：辖属标志位全辖非本级的时候，征收机关只允许选择征收大类！");
				}
			}
		}
		return sqlwhere;

	}

	
	/**
	 * 生成报表取数的条件
	 * 
	 * @param idto
	 * @return
	 * @throws ITFEBizException
	 */
	public static String makesqlwherebyarea(IDto idto,String sbookorgcode) throws ITFEBizException {
		
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
		// 是否含款合计
		String slesumitem = incomedto.getSdividegroup();
		String sqlwhere = "";
		//包含下级国库代码的集合
		StringBuffer trecodes=new StringBuffer();
		
		String uniontaxorg="";
		if (MsgConstant.RULE_SIGN_ALL.equals(sbelong)&&incomedto.getSbudgetlevelcode().equals(StateConstant.ALL_AREA)) {
			//合并包含下级国库所有征收机关(待开发)
			/*List<String> taxorglist=getTaxorgList(strecode,staxorg);
			uniontaxorg="";
			if(staxorg!=null){
				for(String str:taxorglist){
					uniontaxorg+="'"+str+"',";
				}
				uniontaxorg=uniontaxorg.substring(0, uniontaxorg.length()-1);
			}*/
			// 取管本级国库及下级国库的所有子国库
	    	List<TsTreasuryDto> trelist=getSubTreCode(sbookorgcode);
	    	
	    	for(int i=0;i<trelist.size();i++){
	    		if(i==trelist.size()-1){
	    			trecodes.append("'").append(trelist.get(i).getStrecode()).append("'");
	    		}else{
	    			trecodes.append("'").append(trelist.get(i).getStrecode()).append("',");
	    		}
	    	}
		}else{
			List<String> taxorglist=getTaxorgList(strecode,staxorg);
			uniontaxorg="";
			if(staxorg!=null){
				for(String str:taxorglist){
					uniontaxorg+="'"+str+"',";
				}
				uniontaxorg=uniontaxorg.substring(0, uniontaxorg.length()-1);
			}
		}
		
		
		// 全辖--征收机关大类--国库代码
		if (MsgConstant.RULE_SIGN_ALL.equals(sbelong)&&incomedto.getSbudgetlevelcode().equals(StateConstant.SELF_AREA)) {
			if (null == staxorg) {
				sqlwhere += " AND  s_trecode  = '"+strecode+"' "+" and S_BELONGFLAG ='" + sbelong + "' ";
			} else {
				if (staxorg.equals(MsgConstant.MSG_TAXORG_NATION_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_PLACE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_OTHER_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)) {
					sqlwhere += " AND  s_trecode  = '"+strecode+"' "
					        + " and s_TaxOrgCode in (" + uniontaxorg + ") "
							+ " and S_BELONGFLAG ='" + sbelong + "' ";
				} else if(staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS)){
						// 国库全辖"
						sqlwhere += " AND  s_trecode  = '"+strecode+"' "
						        +" and s_TaxOrgCode not in ('"
								+ MsgConstant.MSG_TAXORG_SHARE_CLASS + "') " + " and S_BELONGFLAG ='"
								+ MsgConstant.RULE_SIGN_ALL + "' ";
			    } else{
					throw new ITFEBizException("查询条件：辖属标志位全辖的时候，征收机关只允许选择征收大类！");
				}
			}
		}else if (MsgConstant.RULE_SIGN_ALL.equals(sbelong)&&incomedto.getSbudgetlevelcode().equals(StateConstant.ALL_AREA)) {
			if (null == staxorg) {
				sqlwhere += " and s_trecode  IN ( "+trecodes.toString()+" ) "+" and S_BELONGFLAG ='" + sbelong + "' ";
			} else {
				if (staxorg.equals(MsgConstant.MSG_TAXORG_NATION_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_PLACE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_OTHER_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)) {
					sqlwhere += " and s_trecode  IN ( "+trecodes.toString()+" ) "
					        //暂时去掉合并的征收机关代码待开发
							+ " and s_TaxOrgCode = '"+staxorg+"' "
							+ " and S_BELONGFLAG ='" + sbelong + "' ";
				} else if(staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS)){
						// 国库全辖"
						sqlwhere += " and s_trecode  IN ( "+trecodes.toString()+" ) "
						        +" and s_TaxOrgCode not in ('"
								+ MsgConstant.MSG_TAXORG_SHARE_CLASS + "') " + " and S_BELONGFLAG ='"
								+ MsgConstant.RULE_SIGN_ALL + "'";
			    } else{
					throw new ITFEBizException("查询条件：辖属标志位全辖的时候，征收机关只允许选择征收大类！");
				}
			}
		}
		// 本级--征收机关大类或具体征收机关--国库代码
		if (MsgConstant.RULE_SIGN_SELF.equals(sbelong)) {
			if (null == staxorg) {
				sqlwhere +=" AND  s_trecode  = '"+strecode+"' "
						+ " and S_BELONGFLAG ='" + sbelong + "' ";
			} else {
				if (staxorg.equals(MsgConstant.MSG_TAXORG_NATION_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_PLACE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_OTHER_CLASS)) {// 征收机关大类
					sqlwhere += " AND  s_trecode  = '"+strecode+"' "
					    + " and s_TaxOrgCode IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE  s_trecode  = '"+strecode+"' "
					    + " AND S_TAXPROP='"+ staxorg.substring(0, 1) + "' ) "
						+ " and S_BELONGFLAG ='" + sbelong + "' ";
				} else if (staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS) || staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS)) {// 不分征收机关
					sqlwhere += " AND  s_trecode  = '"+strecode+"' "
					    + " and s_TaxOrgCode IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE s_trecode  = '"+strecode+"' ) "
					    + " and S_BELONGFLAG ='"+ sbelong + "' ";
				} else {// 具体征收机关
					sqlwhere += " AND  s_trecode  = '"+strecode+"' "
					         + " and s_TaxOrgCode in (" + uniontaxorg + ") "
							 + " and S_BELONGFLAG ='" + sbelong + "' ";
				}
			}
		}
		// 全辖非本级--征收机关大类--国库代码：所有辖属国库的全辖数据之和，但不包括本级国库全辖数
		if (MsgConstant.RULE_SIGN_ALLNOTSELF.equals(sbelong)&&incomedto.getSbudgetlevelcode().equals(StateConstant.SELF_AREA)) {
			if (null == staxorg) {
				sqlwhere +=" AND  s_trecode  = '"+strecode+"' "
						+ " and S_BELONGFLAG ='" + sbelong + "' ";
			} else {
				if (staxorg.equals(MsgConstant.MSG_TAXORG_NATION_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_PLACE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_OTHER_CLASS)
						|| staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)) {
					// 国库全辖"
					sqlwhere +=" AND  s_trecode  = '"+strecode+"' "
					         + " and s_TaxOrgCode in ("
							+ uniontaxorg + ") " + " and S_BELONGFLAG ='"
							+ MsgConstant.RULE_SIGN_ALL +"'";
				} else if (staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS)){
					// 国库全辖"
					sqlwhere +=" AND  s_trecode  = '"+strecode+"' "
					        +" and s_TaxOrgCode not in ('"
							+  MsgConstant.MSG_TAXORG_SHARE_CLASS + "') " + " and S_BELONGFLAG ='"
							+ MsgConstant.RULE_SIGN_ALL +"'";
					
				}else {
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
		filename += idto.getSrptdate() + ".txt";
		return filename;
	}
	
	
	
	/**
	 * 根据报表类型得到文件名称
	 * 
	 * @return
	 * @throws ITFEBizException 
	 */
	public static String getExpFileNameByBillTypeForGZ(TrIncomedayrptDto idto,
			String billType,Map map) throws ITFEBizException {
		String filetype = getExpFileBillType(idto, billType);
		String filename = "s" + idto.getStrecode() + "_" + filetype
				+ idto.getSbelongflag();
		if (idto.getSbudgettype().equals(StateConstant.BudgetType_OUT)) {
			filename += StateConstant.BudgetType_OUT;
		}
		if (null==idto.getStaxorgcode()) {
			filename += idto.getSrptdate()+"_0000000000.txt";
		}else if (map.containsKey(idto.getStaxorgcode())) {
			String staxorgcode =(String) map.get(idto.getStaxorgcode());
			filename += idto.getSrptdate() +"_"+staxorgcode+".txt";
		}else {
			throw new ITFEBizException("国库代码" + idto.getStrecode()+ "下TCBS征收机关代码（" + idto.getStaxorgcode()+ "），没有找到对应的地方横联征收机关代码！");
		}
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
	/**
	 * 获取合并前和合并后征收机关代码
	 * @param afterTaxorg合并后征收机关代码
	 * @return 合并前和合并后征收机关代码集合
	 * @throws ITFEBizException 
	 */
	public static List<String> getTaxorgList(String trecode,String afterTaxorg) throws ITFEBizException{
		List<String> taxorglist=new ArrayList<String>();
		try {
			HashMap<String, TsTreasuryDto> mapTreInfo = SrvCacheFacade.cacheTreasuryInfo(null);
			String sbookorgcode = mapTreInfo.get(trecode).getSorgcode();
			TdTaxorgMergerDto tmdto=new TdTaxorgMergerDto();
			tmdto.setSbookorgcode(sbookorgcode);
			tmdto.setSaftertaxorgcode(afterTaxorg);
			taxorglist.add(afterTaxorg);
			List<TdTaxorgMergerDto> tmdtos=CommonFacade.getODB().findRsByDto(tmdto);
			if(tmdtos!=null&&tmdtos.size()>0){
				for(TdTaxorgMergerDto tmpdto:tmdtos){
					taxorglist.add(tmpdto.getSpretaxorgcode());
				}
			}
		} catch (Exception e) {
			log.error("获取征收机关合并参数出现异常："+e.getMessage());
			throw new ITFEBizException("获取征收机关合并参数出现异常："+e.getMessage());
		}
		return taxorglist;
	}
	/**
	 * 获取合并前和合并后征收机关代码
	 * @param afterTaxorg国库代码
	 * @return 合并前和合并后征收机关代码集合
	 * @throws ITFEBizException 
	 */
	public static List<String> getTaxorgListByTrecode(String trecode,String staxorgcode) throws ITFEBizException{
		List<String> taxorglist=new ArrayList<String>();
		try {
			HashMap<String, TsTreasuryDto> mapTreInfo = SrvCacheFacade.cacheTreasuryInfo(null);
			String sbookorgcode = mapTreInfo.get(trecode).getSorgcode();
			TsTaxorgDto tstaxorgdto=new TsTaxorgDto();
			tstaxorgdto.setStrecode(trecode);
			tstaxorgdto.setSorgcode(sbookorgcode);
			if(staxorgcode!=null)
				tstaxorgdto.setStaxprop(staxorgcode.substring(0, 1));
			List<TsTaxorgDto> tmdtos=CommonFacade.getODB().findRsByDto(tstaxorgdto);
			if(tmdtos!=null&&tmdtos.size()>0){
				for(TsTaxorgDto tmptaxorg:tmdtos){
					String taxorgcode=tmptaxorg.getStaxorgcode();
					taxorglist.addAll(getTaxorgList(trecode,taxorgcode));
				}
			}
		} catch (Exception e) {
			log.error("获取征收机关合并参数出现异常："+e.getMessage());
			throw new ITFEBizException("获取征收机关合并参数出现异常："+e.getMessage());
		}
		return taxorglist;
	}
	
	/**
	 * 修改SQL增加征收机关代码条件
	 */
	public String alertSqlStringWithTaxorg(String strecode,String staxorg,String sql) {
		//如果是不分征收机关，则不增加
		if(!staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)&& !staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS)) {
			if (staxorg.equals(MsgConstant.MSG_TAXORG_NATION_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_PLACE_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_OTHER_CLASS)) {
				sql = sql + " and S_TAXORGCODE IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_TRECODE='"
				+ strecode + "' AND S_TAXPROP='"
				+ staxorg.substring(0, 1) + "' ) ";
			}else {
				sql = sql + " and S_TAXORGCODE='"+staxorg+"' ";
			}
		}
		return sql;
	}
	
	public static List<TsTreasuryDto> getSubTreCode(TsTreasuryDto dto) throws ITFEBizException 
	{
		List<TsTreasuryDto> trelist = null;
		try{
			if(dto.getStrecode()==null||"".equals(dto.getStrecode().trim()))
				return trelist;
			else
			{
				trelist = new ArrayList<TsTreasuryDto>();
				TsTreasuryDto finddto = new TsTreasuryDto();
				finddto.setSgoverntrecode(dto.getStrecode());
				trelist.add(dto);
				List<TsTreasuryDto> templist = CommonFacade.getODB().findRsByDto(finddto);
				if(templist!=null&&templist.size()>0)
				{
					trelist.addAll(templist);
					TsTreasuryDto tempdto = null;
					for(int i=0;i<templist.size();i++)
					{
						tempdto = templist.get(i);
						if(!tempdto.getStrecode().equals(tempdto.getSgoverntrecode()))
							trelist.addAll(getSubTreCode(tempdto));
					}
				}	
			}
		} catch (JAFDatabaseException e) {
			log.error("查询国库参数表发生数据库异常-递归查询全辖国库!", e);
			throw new ITFEBizException("查询国库参数表发生数据库异常-递归查询全辖国库!", e);
		} catch (ValidateException e) {
			log.error("查询国库参数表发生数据库异常-递归查询全辖国库!", e);
			throw new ITFEBizException("查询国库参数表发生数据库异常-递归查询全辖国库!", e);
		}
		return trelist;
	}
	public static List<TsTreasuryDto> getSubTreCode(String orgCode) throws ITFEBizException 
	{
		List<TsTreasuryDto> returnList = null;
		try{
			if(orgCode==null||"".equals(orgCode.trim()))
				return returnList;
			else
			{
				List<TsTreasuryDto> trelist = new ArrayList<TsTreasuryDto>();
				returnList = new ArrayList<TsTreasuryDto>();
				TsTreasuryDto finddto = new TsTreasuryDto();
				finddto.setSorgcode(orgCode);
				List<TsTreasuryDto> templist = CommonFacade.getODB().findRsByDto(finddto);
				if(templist!=null&&templist.size()>0)
				{
					trelist.addAll(templist);
					for(int i=0;i<templist.size();i++)
						trelist.addAll(getSubTreCode(templist.get(i)));
				}
				Set<String> tmpTreSet = new HashSet<String>();
				if(trelist!=null&&trelist.size()>0)
				{
					for(int i=0;i<trelist.size();i++)
					{
						if(tmpTreSet.add(trelist.get(i).getStrecode()))
							returnList.add(trelist.get(i));
					}
				}
			}
		} catch (JAFDatabaseException e) {
			log.error("查询国库参数表发生数据库异常-递归查询全辖国库!", e);
			throw new ITFEBizException("查询国库参数表发生数据库异常-递归查询全辖国库!", e);
		} catch (ValidateException e) {
			log.error("查询国库参数表发生数据库异常-递归查询全辖国库!", e);
			throw new ITFEBizException("查询国库参数表发生数据库异常-递归查询全辖国库!", e);
		}
		return returnList;
	}
}
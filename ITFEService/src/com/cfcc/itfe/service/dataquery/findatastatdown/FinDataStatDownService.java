package com.cfcc.itfe.service.dataquery.findatastatdown;

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
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.service.dataquery.findatastatdown.AbstractFinDataStatDownService;
import com.cfcc.itfe.service.expreport.IMakeReport;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author db2admin
 * @time 12-02-25 21:02:16 codecomment:
 */

public class FinDataStatDownService extends AbstractFinDataStatDownService {
	private static Log log = LogFactory.getLog(FinDataStatDownService.class);

	public List makeRptFile(IDto idto, List billTypeList)
			throws ITFEBizException {
		List<TdEnumvalueDto> list = billTypeList;
		TrIncomedayrptDto incomedto = (TrIncomedayrptDto) idto;
		/*// 国库主体代码
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
		String srptdate = incomedto.getSrptdate();
		// 是否含款合计
		String slesumitem = incomedto.getSdividegroup();*/
		String bizType;
		String filepath;
		List <String> fileNameList = new ArrayList<String>();
		String sbookorgcode =getLoginInfo().getSorgcode();
		String root = ITFECommonConstant.FILE_ROOT_PATH ;
		// 循环获取报表数据
		//TrIncomedayrptDto queryDto = new TrIncomedayrptDto();
		for (TdEnumvalueDto _dto : list) {
			bizType =_dto.getSvalue();
			String areaBizType = bizType;
			if(StateConstant.SPECIAL_AREA_GUANGDONG.equals(getLoginInfo().getArea().trim())){
				areaBizType = areaBizType + MsgConstant.PLACE_GZ;
			}
			if ((StateConstant.IS_COLLECT_YES).equals(getLoginInfo().getIscollect()) && StateConstant.REPORTTYPE_9.equals(bizType)) {
				areaBizType =bizType+MsgConstant.PLACE_GZ+StateConstant.IS_COLLECT_YES;
			}
			IMakeReport makereport = (IMakeReport) ContextFactory
			.getApplicationContext().getBean(
					MsgConstant.SPRING_EXP_REPORT+areaBizType);
			filepath = makereport.makeReportByBiz(incomedto, bizType,sbookorgcode);
			if (null!=filepath) {
				fileNameList.add(filepath.replaceAll(root, ""));
			} 
		}
        
		return fileNameList;
		
	}

	
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
	public String makesqlwhere(IDto idto) throws ITFEBizException {
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
		// 全辖--征收机关大类--国库代码
		if (MsgConstant.RULE_SIGN_ALL.equals(sbelong)) {
			if (staxorg.equals(MsgConstant.MSG_TAXORG_NATION_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_PLACE_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_OTHER_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)) {
				sqlwhere += " and a.s_trecode ='" + strecode + "' "
						+ " and a.s_TaxOrgCode ='" + staxorg + "' "
						+ " and a.S_BELONGFLAG ='" + sbelong + "' ";
			} else {
				throw new ITFEBizException("查询条件：辖属标志位全辖的时候，征收机关只允许选择征收大类！");
			}
		}
		// 本级--征收机关大类或具体征收机关--国库代码
		if (MsgConstant.RULE_SIGN_SELF.equals(sbelong)) {
			if (staxorg.equals(MsgConstant.MSG_TAXORG_NATION_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_PLACE_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_OTHER_CLASS)) {// 征收机关大类
				sqlwhere += " and a.s_trecode ='"
						+ strecode
						+ "' "
						+ " and a.s_TaxOrgCode IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_TRECODE='"
						+ strecode + "' AND S_TAXPROP='"
						+ staxorg.substring(0, 1) + "' ) "
						+ " and a.S_BELONGFLAG ='" + sbelong + "' ";
			} else if (staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)) {// 不分征收机关
				sqlwhere += " and a.s_trecode ='"
						+ strecode
						+ "' "
						+ " and a.s_TaxOrgCode IN ( SELECT S_TAXORGCODE FROM TS_TAXORG WHERE S_TRECODE='"
						+ strecode + "' ) " + " and a.S_BELONGFLAG ='"
						+ sbelong + "' ";
			} else {// 具体征收机关
				sqlwhere += " and a.s_trecode ='" + strecode + "' "
						+ " and a.s_TaxOrgCode ='" + staxorg + "' "
						+ " and a.S_BELONGFLAG ='" + sbelong + "' ";
			}
		}
		// 全辖非本级--征收机关大类--国库代码：所有辖属国库的全辖数据之和，但不包括本级国库全辖数
		if (MsgConstant.RULE_SIGN_ALLNOTSELF.equals(sbelong)) {
			if (staxorg.equals(MsgConstant.MSG_TAXORG_NATION_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_PLACE_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_CUSTOM_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_FINANCE_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_OTHER_CLASS)
					|| staxorg.equals(MsgConstant.MSG_TAXORG_SHARE_CLASS)) {
				// 国库全辖"
				sqlwhere += " and a.s_trecode in ( "
						+ " SELECT s_trecode FROM TS_TREASURY WHERE  s_governtrecode='"
						+ strecode + "' " + " and a.s_TaxOrgCode ='" + staxorg
						+ "' " + " and a.S_BELONGFLAG ='"
						+ MsgConstant.RULE_SIGN_ALL + "' ";

			} else {
				throw new ITFEBizException("查询条件：辖属标志位全辖非本级的时候，征收机关只允许选择征收大类！");
			}
		}
		return sqlwhere;

	}

	/**
	 * @return
	 */
	public String getReportTypeByBillType(TrIncomedayrptDto idto,
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
	


}
package com.cfcc.itfe.service.dataquery.businessdetaillist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.data.TipsParamDto;
import com.cfcc.itfe.persistence.dto.FundGrantCustomDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author renqingbin
 * @time 14-03-31 14:48:39 codecomment:
 */

public class BusinessDetailListService extends
		AbstractBusinessDetailListService {
	private static Log log = LogFactory.getLog(BusinessDetailListService.class);

	/**
	 * 生成报表
	 * 
	 * @generated
	 * @param biztype
	 * @param paramdto
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List makeReport(String biztype, TipsParamDto paramdto)
			throws ITFEBizException {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String startdate = format.format(paramdto.getStartdate());
		String enddate = format.format(paramdto.getEnddate());
		String datetype = "S_CONFIRUSERCODE";
		if(paramdto.getDatatype()!=null&&"0".equals(paramdto.getDatatype()))
			datetype = "S_CREATDATE";
		TsTreasuryDto streDto = new TsTreasuryDto();
		streDto.setStrecode(paramdto.getStrecode());
		List<TsTreasuryDto> streDtoList = null;
		try {
			streDtoList = CommonFacade.getODB().findRsByDto(streDto);
		} catch (JAFDatabaseException e1) {
			log.error("查找国库信息出错"+e1);
			throw new ITFEBizException(e1);
		} catch (ValidateException e1) {
			log.error("查找国库信息出错"+e1);
			throw new ITFEBizException(e1);
		}
		
		// 查询sql
		String sql = "SELECT  S_ORGCODE AS orgcode, S_TRECODE AS trecode, S_VTCODE AS biztype, "
				+ "CASE S_VTCODE  "
				+ "WHEN '5207' THEN '财政机构代码' "
				+ "WHEN '5209' THEN '财政机构代码' "
				+ "ELSE '代理银行代码'  "
				+ "END AS desorgcode,  "
				+ "CASE S_VTCODE  "
				+ "WHEN '5106' THEN '授权支付额度' "
				+ "WHEN '5108' THEN '直接支付额度' "
				+ "WHEN '5207' THEN '实拨资金' "
				+ "WHEN '5209' THEN '收入退付' "
				+ "WHEN '2301' THEN '商行划款申请' "
				+ "WHEN '2302' THEN '商行退款申请' "
				+ "ELSE '未知业务类型'  "
				+ "END AS bizname,  "
				+ "S_CREATDATE AS voudate, "
				+ "S_VOUCHERNO AS vouno, S_PAYBANKCODE AS billorgcode, N_MONEY AS money, S_STATUS AS status, "
				+ "CASE S_STATUS  "
				+ "WHEN '10' THEN '已读取' "
				+ "WHEN '15' THEN '签收成功' "
				+ "WHEN '16' THEN '签收失败' "
				+ "WHEN '20' THEN '校验成功' "
				+ "WHEN '30' THEN '校验失败' "
				+ "WHEN '40' THEN '审核成功' "
				+ "WHEN '45' THEN '审核失败' "
				+ "WHEN '50' THEN '复核成功' "
				+ "WHEN '60' THEN '已发送' "
				+ "WHEN '61' THEN '已收妥' "
				+ "WHEN '62' THEN 'TIPS处理失败' "
				+ "WHEN '71' THEN '处理成功' "
				+ "WHEN '72' THEN 'TCBS处理失败' "
				+ "WHEN '73' THEN '签章成功' "
				+ "WHEN '80' THEN '已回单' "
				+ "WHEN '90' THEN '已退回' "
				+ "ELSE '未知状态'  "
				+ "END AS desstatus "
				+ "FROM TV_VOUCHERINFO "
				+ "WHERE "+datetype+" >='"+startdate+ "' AND "+datetype+"<='"
				+ enddate + "' AND S_TRECODE='" + paramdto.getStrecode() + "' ";
		if(streDtoList.get(0).getStreattrib().equals("2")){ //代理库
			sql = "SELECT  S_ORGCODE AS orgcode, S_TRECODE AS trecode, S_VTCODE AS biztype, "
				+ "CASE S_VTCODE  "
				+ "WHEN '5207' THEN '财政机构代码' "
				+ "WHEN '5209' THEN '财政机构代码' "
				+ "ELSE '代理银行代码'  "
				+ "END AS desorgcode,  "
				+ "CASE S_VTCODE  "
				+ "WHEN '5106' THEN '授权支付额度' "
				+ "WHEN '5108' THEN '直接支付额度' "
				+ "WHEN '5207' THEN '实拨资金' "
				+ "WHEN '5209' THEN '收入退付' "
				+ "WHEN '2301' THEN '商行划款申请' "
				+ "WHEN '2302' THEN '商行退款申请' "
				+ "ELSE '未知业务类型'  "
				+ "END AS bizname,  "
				+ "S_CREATDATE AS voudate, "
				+ "S_VOUCHERNO AS vouno, S_PAYBANKCODE AS billorgcode, N_MONEY AS money, S_STATUS AS status, "
				+ "CASE S_STATUS  "
				+ "WHEN '10' THEN '已读取' "
				+ "WHEN '15' THEN '签收成功' "
				+ "WHEN '16' THEN '签收失败' "
				+ "WHEN '20' THEN '校验成功' "
				+ "WHEN '30' THEN '校验失败' "
				+ "WHEN '40' THEN '审核成功' "
				+ "WHEN '45' THEN '审核失败' "
				+ "WHEN '50' THEN '复核成功' "
				+ "WHEN '60' THEN '已发送' "
				+ "WHEN '61' THEN '已收妥' "
				+ "WHEN '62' THEN 'TIPS处理失败' "
				+ "WHEN '71' THEN '处理成功' "
				+ "WHEN '72' THEN 'TCBS处理失败' "
				+ "WHEN '73' THEN '签章成功' "
				+ "WHEN '80' THEN '已回单' "
				+ "WHEN '90' THEN '已退回' "
				+ "ELSE '未知状态'  "
				+ "END AS desstatus "
				+ "FROM TV_VOUCHERINFO "
				+ "WHERE "+datetype+" >='"+startdate+ "' AND "+datetype+"<='"
				+ enddate + "' AND S_TRECODE='" + paramdto.getStrecode() + "' ";
		}
		String type = StringUtils.replace(StringUtils.replace(biztype, "(", ""),")","");
		String[] types = StringUtils.split(type,",");
		if(types!=null&&types.length>0)
		{
			sql +=" and (";
			for(int i=0;i<types.length;i++)
			{
				if(i==0)
				{
					if(types[i].indexOf(MsgConstant.VOUCHER_NO_5106)>0||types[i].indexOf(MsgConstant.VOUCHER_NO_5108)>0)
					{
						sql += " (S_VTCODE ="+types[i];
						if(StringUtils.isNotBlank(paramdto.getSbankcode()))
							sql += " and S_PAYBANKCODE='"+paramdto.getSbankcode()+"' ";
						sql +=")";
					}else if(types[i].indexOf(MsgConstant.VOUCHER_NO_2301)>0||types[i].indexOf(MsgConstant.VOUCHER_NO_2302)>0)
					{
						sql += " (S_VTCODE ="+types[i];
						if(!streDtoList.get(0).getStreattrib().equals("2")){ //非代理库
							if(StringUtils.isNotBlank(paramdto.getPaymode()))
								sql += " and S_EXT1='"+paramdto.getPaymode()+"' ";
						}
						if(StringUtils.isNotBlank(paramdto.getSbankcode()))
							sql += " and S_PAYBANKCODE='"+paramdto.getSbankcode()+"' ";
						sql +=")";
					}else
					{
						sql += " (S_VTCODE ="+types[i]+") ";
					}
				}else
				{
					if(types[i].indexOf(MsgConstant.VOUCHER_NO_5106)>0||types[i].indexOf(MsgConstant.VOUCHER_NO_5108)>0)
					{
						sql += " or (S_VTCODE ="+types[i];
						if(StringUtils.isNotBlank(paramdto.getSbankcode()))
							sql += " and S_PAYBANKCODE='"+paramdto.getSbankcode()+"' ";
						sql +=")";
					}else if(types[i].indexOf(MsgConstant.VOUCHER_NO_2301)>0||types[i].indexOf(MsgConstant.VOUCHER_NO_2302)>0)
					{
						sql += " or (S_VTCODE ="+types[i];
						if(!streDtoList.get(0).getStreattrib().equals("2")){ //非代理库
							if(StringUtils.isNotBlank(paramdto.getPaymode()))
								sql += " and S_EXT1='"+paramdto.getPaymode()+"' ";
						}
						if(StringUtils.isNotBlank(paramdto.getSbankcode()))
							sql += " and S_PAYBANKCODE='"+paramdto.getSbankcode()+"' ";
						sql +=")";
					}else
					{
						sql += " or (S_VTCODE ="+types[i]+") ";
					}
				}
				
			}
			sql +=")";
		}
		if (!StringUtils.isBlank(paramdto.getSbeflag())) {
			sql += " AND S_STATUS = '" + paramdto.getSbeflag() + "' ";
		}
		sql += " ORDER BY voudate";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			sqlExec.setMaxRows(100000);
			SQLResults rs = null;
			if("17999".equals(paramdto.getExptype()))
			{
				StringBuffer countsql = new StringBuffer();
				countsql.append("select * from (select (CASE WHEN zjasqh.s_dates IS NULL THEN sqtk.S_CONFIRUSERCODE WHEN zjasqh.s_dates IS NOT NULL THEN zjasqh.s_dates END ) AS dates,zjhkhz,zjtkhz,sqhkhz,sqtkhz from ");
				countsql.append("(select (CASE WHEN zja.s_dates IS NULL THEN sqhk.S_CONFIRUSERCODE WHEN zja.s_dates IS NOT NULL THEN zja.s_dates END ) AS s_dates,zjhkhz,zjtkhz,sqhkhz from ");
				countsql.append("(select (CASE WHEN zjhk.S_CONFIRUSERCODE IS NULL THEN zjtk.S_CONFIRUSERCODE WHEN zjhk.S_CONFIRUSERCODE IS NOT NULL THEN zjhk.S_CONFIRUSERCODE END ) AS s_dates,zjhkhz,zjtkhz from ");
				countsql.append("(select S_CONFIRUSERCODE,sum(N_MONEY) as zjhkhz from tv_voucherinfo where S_STATUS = ? and s_vtcode=? and S_EXT1 =? and S_PAYBANKCODE=? and "+datetype+" BETWEEN ? AND ? group by S_CONFIRUSERCODE) as zjhk ");//直接支付划款
				sqlExec.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				sqlExec.addParam(MsgConstant.VOUCHER_NO_2301);
				sqlExec.addParam(MsgConstant.directPay);
				sqlExec.addParam(paramdto.getSbankcode());
				sqlExec.addParam(startdate);
				sqlExec.addParam(enddate);
				countsql.append(" FULL JOIN ");//
				countsql.append("(select S_CONFIRUSERCODE,sum(N_MONEY) as zjtkhz from tv_voucherinfo where S_STATUS = ? and s_vtcode=? and S_EXT1 =? and S_PAYBANKCODE=? and "+datetype+" BETWEEN ? AND ? group by S_CONFIRUSERCODE) as zjtk ");//直接支付退款
				sqlExec.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				sqlExec.addParam(MsgConstant.VOUCHER_NO_2302);
				sqlExec.addParam(MsgConstant.directPay);
				sqlExec.addParam(paramdto.getSbankcode());
				sqlExec.addParam(startdate);
				sqlExec.addParam(enddate);
				countsql.append(" on zjhk.S_CONFIRUSERCODE=zjtk.S_CONFIRUSERCODE) as zja FULL JOIN ");
				countsql.append("(select S_CONFIRUSERCODE,sum(N_MONEY) as sqhkhz from tv_voucherinfo where S_STATUS = ? and s_vtcode=? and S_EXT1 =? and S_PAYBANKCODE=? and "+datetype+" BETWEEN ? AND ? group by S_CONFIRUSERCODE) as sqhk ");//授权支付划款
				sqlExec.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				sqlExec.addParam(MsgConstant.VOUCHER_NO_2301);
				sqlExec.addParam(MsgConstant.grantPay);
				sqlExec.addParam(paramdto.getSbankcode());
				sqlExec.addParam(startdate);
				sqlExec.addParam(enddate);
				countsql.append(" on zja.s_dates=sqhk.S_CONFIRUSERCODE) as zjasqh FULL JOIN ");
				countsql.append("(select S_CONFIRUSERCODE,sum(N_MONEY) as sqtkhz from tv_voucherinfo where S_STATUS = ? and s_vtcode=? and S_EXT1 =? and S_PAYBANKCODE=? and "+datetype+" BETWEEN ? AND ? group by S_CONFIRUSERCODE) as sqtk ");//授权支付退款
				sqlExec.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				sqlExec.addParam(MsgConstant.VOUCHER_NO_2302);
				sqlExec.addParam(MsgConstant.grantPay);
				sqlExec.addParam(paramdto.getSbankcode());
				sqlExec.addParam(startdate);
				sqlExec.addParam(enddate);
				countsql.append(" on zjasqh.s_dates=sqtk.S_CONFIRUSERCODE ) order by dates ");
				sql = countsql.toString();
			}
				rs = sqlExec.runQueryCloseCon(sql);
			return (List) rs.getDtoCollection();
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(e);
		} finally {
			if (sqlExec != null) {
				sqlExec.closeConnection();
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.cfcc.itfe.service.dataquery.businessdetaillist.IBusinessDetailListService#makestatReport(java.lang.String, com.cfcc.itfe.facade.data.TipsParamDto)
	 */
	public PageResponse makestatReport(String biztype, TipsParamDto paramdto,PageRequest request)
			throws ITFEBizException {
		PageResponse response = new PageResponse(request);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String startdate = format.format(paramdto.getStartdate());
		String enddate = format.format(paramdto.getEnddate());
		String datetype = "S_CONFIRUSERCODE";
		if(paramdto.getDatatype()!=null&&"0".equals(paramdto.getDatatype()))
			datetype = "S_CREATDATE";
		TsTreasuryDto streDto = new TsTreasuryDto();
		streDto.setStrecode(paramdto.getStrecode());
		List<TsTreasuryDto> streDtoList = null;
		try {
			streDtoList = CommonFacade.getODB().findRsByDto(streDto);
		} catch (JAFDatabaseException e1) {
			log.error("查找国库信息出错"+e1);
			throw new ITFEBizException(e1);
		} catch (ValidateException e1) {
			log.error("查找国库信息出错"+e1);
			throw new ITFEBizException(e1);
		}
		
		String sql = "SELECT  S_ORGCODE,S_TRECODE,S_ADMDIVCODE,S_STYEAR,S_CREATDATE,S_VTCODE,count(1) as icount,sum(N_money) as ncheckmoney,S_STATUS "
				+ "FROM TV_VOUCHERINFO "
				+ "WHERE "+datetype+" >='"+startdate+ "' AND "+datetype+"<='"
				+ enddate + "' AND S_TRECODE='" + paramdto.getStrecode() + "' ";
		if(streDtoList.get(0).getStreattrib().equals("2")){ //代理库
			sql = "SELECT  S_ORGCODE,S_TRECODE,S_ADMDIVCODE,S_STYEAR,S_CREATDATE,S_VTCODE,count(1) as icount,sum(N_money) as ncheckmoney,S_STATUS "
				+ "FROM TV_VOUCHERINFO "
				+ "WHERE "+datetype+" >='"+startdate+ "' AND "+datetype+"<='"
				+ enddate + "' AND S_TRECODE='" + paramdto.getStrecode() + "' ";
		}
		String type = StringUtils.replace(StringUtils.replace(biztype, "(", ""),")","");
		String[] types = StringUtils.split(type,",");
		if(types!=null&&types.length>0)
		{
			sql +=" and (";
			for(int i=0;i<types.length;i++)
			{
				if(i==0)
				{
					if(types[i].indexOf(MsgConstant.VOUCHER_NO_5106)>0||types[i].indexOf(MsgConstant.VOUCHER_NO_5108)>0)
					{
						sql += " (S_VTCODE ="+types[i];
						if(StringUtils.isNotBlank(paramdto.getSbankcode()))
							sql += " and S_PAYBANKCODE='"+paramdto.getSbankcode()+"' ";
						sql +=")";
					}else if(types[i].indexOf(MsgConstant.VOUCHER_NO_2301)>0||types[i].indexOf(MsgConstant.VOUCHER_NO_2302)>0)
					{
						sql += " (S_VTCODE ="+types[i];
						if(StringUtils.isNotBlank(paramdto.getPaymode()))
							sql += " and S_EXT1='"+paramdto.getPaymode()+"' ";
						if(StringUtils.isNotBlank(paramdto.getSbankcode()))
							sql += " and S_PAYBANKCODE='"+paramdto.getSbankcode()+"' ";
						sql +=")";
					}else
					{
						sql += " (S_VTCODE ="+types[i]+") ";
					}
				}else
				{
					if(types[i].indexOf(MsgConstant.VOUCHER_NO_5106)>0||types[i].indexOf(MsgConstant.VOUCHER_NO_5108)>0)
					{
						sql += " or (S_VTCODE ="+types[i];
						if(StringUtils.isNotBlank(paramdto.getSbankcode()))
							sql += " and S_PAYBANKCODE='"+paramdto.getSbankcode()+"' ";
						sql +=")";
					}else if(types[i].indexOf(MsgConstant.VOUCHER_NO_2301)>0||types[i].indexOf(MsgConstant.VOUCHER_NO_2302)>0)
					{
						sql += " or (S_VTCODE ="+types[i];
						if(StringUtils.isNotBlank(paramdto.getPaymode()))
							sql += " and S_EXT1='"+paramdto.getPaymode()+"' ";
						if(StringUtils.isNotBlank(paramdto.getSbankcode()))
							sql += " and S_PAYBANKCODE='"+paramdto.getSbankcode()+"' ";
						sql +=")";
					}else
					{
						sql += " or (S_VTCODE ="+types[i]+") ";
					}
				}
				
			}
			sql +=")";
		}
//		sql += " AND S_VTCODE IN " + biztype;
		if (!StringUtils.isBlank(paramdto.getSbeflag())) {
			sql += " AND S_STATUS = '" + paramdto.getSbeflag() + "' ";
		}
		sql += " group by  S_ORGCODE,S_TRECODE,S_ADMDIVCODE,S_STYEAR,S_CREATDATE,S_VTCODE,S_STATUS  ORDER BY S_ORGCODE,S_TRECODE,S_CREATDATE,S_VTCODE,S_STATUS";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			SQLResults res = sqlExec.runQueryCloseCon(sql,TvVoucherinfoDto.class ,
					request.getStartPosition(), request.getPageSize(), false);
			List list = new ArrayList();
			list.addAll(res.getDtoCollection());
			response.getData().clear();
			response.setData(list);
			response.setTotalCount(list.size());
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(e);
		} finally {
			if (sqlExec != null) {
				sqlExec.closeConnection();
			}
		}
		return response;
	
	}

	public List  makeSumCountRecord(String biztype, TipsParamDto paramdto)
			throws ITFEBizException {
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String startdate = format.format(paramdto.getStartdate());
		String enddate = format.format(paramdto.getEnddate());
		String datetype = "S_CONFIRUSERCODE";
		if(paramdto.getDatatype()!=null&&"0".equals(paramdto.getDatatype()))
			datetype = "S_CREATDATE";
		// 查询sql
		String sql = "SELECT  S_ORGCODE,S_ADMDIVCODE,S_TRECODE,S_VTCODE,S_STYEAR,S_CREATDATE,S_VOUCHERNO,S_STATUS From TV_VOUCHERINFO "  
			+ "WHERE "+datetype+" >='"+startdate+ "' AND "+datetype+"<='"
				+ enddate + "' AND S_TRECODE='" + paramdto.getStrecode() + "' ";
//		sql += " AND S_VTCODE IN " + biztype;
		String type = StringUtils.replace(StringUtils.replace(biztype, "(", ""),")","");
		String[] types = StringUtils.split(type,",");
		if(types!=null&&types.length>0)
		{
			sql +=" and (";
			for(int i=0;i<types.length;i++)
			{
				if(i==0)
				{
					if(types[i].indexOf(MsgConstant.VOUCHER_NO_5106)>0||types[i].indexOf(MsgConstant.VOUCHER_NO_5108)>0)
					{
						sql += " (S_VTCODE ="+types[i];
						if(StringUtils.isNotBlank(paramdto.getSbankcode()))
							sql += " and S_PAYBANKCODE='"+paramdto.getSbankcode()+"' ";
						sql +=")";
					}else if(types[i].indexOf(MsgConstant.VOUCHER_NO_2301)>0||types[i].indexOf(MsgConstant.VOUCHER_NO_2302)>0)
					{
						sql += " (S_VTCODE ="+types[i];
						if(StringUtils.isNotBlank(paramdto.getPaymode()))
							sql += " and S_EXT1='"+paramdto.getPaymode()+"' ";
						if(StringUtils.isNotBlank(paramdto.getSbankcode()))
							sql += " and S_PAYBANKCODE='"+paramdto.getSbankcode()+"' ";
						sql +=")";
					}else
					{
						sql += " (S_VTCODE ="+types[i]+") ";
					}
				}else
				{
					if(types[i].indexOf(MsgConstant.VOUCHER_NO_5106)>0||types[i].indexOf(MsgConstant.VOUCHER_NO_5108)>0)
					{
						sql += " or (S_VTCODE ="+types[i];
						if(StringUtils.isNotBlank(paramdto.getSbankcode()))
							sql += " and S_PAYBANKCODE='"+paramdto.getSbankcode()+"' ";
						sql +=")";
					}else if(types[i].indexOf(MsgConstant.VOUCHER_NO_2301)>0||types[i].indexOf(MsgConstant.VOUCHER_NO_2302)>0)
					{
						sql += " or (S_VTCODE ="+types[i];
						if(StringUtils.isNotBlank(paramdto.getPaymode()))
							sql += " and S_EXT1='"+paramdto.getPaymode()+"' ";
						if(StringUtils.isNotBlank(paramdto.getSbankcode()))
							sql += " and S_PAYBANKCODE='"+paramdto.getSbankcode()+"' ";
						sql +=")";
					}else
					{
						sql += " or (S_VTCODE ="+types[i]+") ";
					}
				}
				
			}
			sql +=")";
		}
		if (!StringUtils.isBlank(paramdto.getSbeflag())) {
			sql += " AND S_STATUS = '" + paramdto.getSbeflag() + "' ";
		}
		sql += " ORDER BY S_CREATDATE,S_STATUS";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			sqlExec.setMaxRows(100000);
			List <TvVoucherinfoDto> list = (List<TvVoucherinfoDto>) sqlExec.runQueryCloseCon(sql,TvVoucherinfoDto.class).getDtoCollection();
			return list;
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(e);
		} finally {
			if (sqlExec != null) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * 5207,5108,5106,2301,2302,5209打印查询
	 */
	public List findRsForMain(String biztype, TipsParamDto paramdto)
			throws ITFEBizException {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String startdate = format.format(paramdto.getStartdate());
		String enddate = format.format(paramdto.getEnddate());
		// 查询sql
		String sql = "";
		if(biztype.contains(MsgConstant.VOUCHER_NO_5207)){ //实拨资金
			String datetype = "S_XPAYDATE";
			if(paramdto.getDatatype()!=null&&"0".equals(paramdto.getDatatype()))
				datetype = "S_ACCDATE";
			sql = "select row_number() over() as rowid,a."+datetype+" as voudate,a.S_TAXTICKETNO as vouno,a.S_PAYERNAME as spayername," 
					+ "a.S_PAYERACCT as spayeracct,a.S_RECBANKNAME as srecbankname,a.S_RECNAME as srecname,a.S_RECACCT as srecacct,a.S_BUDGETUNITCODE as sbudgetunitcode," 
					+ "a.N_MONEY as nmoney,a.S_STATUS as sstatus,a.S_TRECODE AS strecode,c.S_FUNSUBJECTCODE as sfunsubjectcode,a.S_ADDWORD as saddword " 
					+ "from TV_PAYOUTMSGMAIN a,(SELECT * FROM TV_PAYOUTMSGSUB b WHERE NOT EXISTS (SELECT * FROM TV_PAYOUTMSGSUB b1  " 
					+ "WHERE b.S_BIZNO = b1.S_BIZNO AND b.S_SEQNO < b1.S_SEQNO)) c WHERE a.S_BIZNO = c.S_BIZNO and a."+datetype+" >='" 
					+ startdate+"' AND a."+datetype+"<='"+ enddate + "' AND a.S_TRECODE='" + paramdto.getStrecode() + "' and a.S_PAYTYPECODE='91'";
			if (StringUtils.isNotBlank(paramdto.getSbeflag())) {
				sql += " AND a.S_STATUS = '" + paramdto.getSbeflag() + "'";
			}
			sql += " order by voudate";
		}else if(biztype.contains(MsgConstant.VOUCHER_NO_3208)){ //实拨资金
			String datetype = "S_CONFIRUSERCODE";
			if(paramdto.getDatatype()!=null&&"0".equals(paramdto.getDatatype()))
				datetype = "S_CREATDATE";
			sql = "select row_number() over() as rowid,d."+datetype+" as voudate,d.S_VOUCHERNO as vouno,a.S_PAYERNAME as spayername," 
				+ "a.S_PAYERACCT as spayeracct,a.S_RECBANKNAME as srecbankname,a.S_RECNAME as srecname,a.S_RECACCT as srecacct,a.S_BUDGETUNITCODE as sbudgetunitcode," 
				+ "'-'||d.N_MONEY as nmoney,d.S_STATUS as sstatus,a.S_TRECODE AS strecode,c.S_FUNSUBJECTCODE as sfunsubjectcode,a.S_ADDWORD as saddword " 
				+ "from TV_PAYOUTMSGMAIN a,(SELECT * FROM TV_PAYOUTMSGSUB b WHERE NOT EXISTS (SELECT * FROM TV_PAYOUTMSGSUB b1  " 
				+ "WHERE b.S_BIZNO = b1.S_BIZNO AND b.S_SEQNO < b1.S_SEQNO)) c,TV_VOUCHERINFO d WHERE d.S_EXT4=a.S_BIZNO and a.S_BACKFLAG='1' and a.S_BIZNO = c.S_BIZNO and d."+datetype+" >='" 
				+ startdate+"' AND d."+datetype+"<='"+ enddate + "' AND d.S_TRECODE='" + paramdto.getStrecode() + "'";
			if (StringUtils.isNotBlank(paramdto.getSbeflag())) {
				sql += " AND a.S_STATUS = '" + paramdto.getSbeflag() + "'";
			}
			datetype = "S_COMMITDATE";
			if(paramdto.getDatatype()!=null&&"0".equals(paramdto.getDatatype()))
				datetype = "S_ACCDATE";
			String tcsql = "select row_number() over() as rowid,a."+datetype+" as voudate,a.S_VOUNO as vouno,a.S_PAYEENAME as spayername," 
			+ "a.S_PAYEEACCT as spayeracct,'未知' as srecbankname,a.S_PAYERNAME as srecname,a.S_PAYERACCT as srecacct,a.S_BUDGETUNITCODE as sbudgetunitcode," 
			+ "'-'||a.N_MONEY as nmoney,a.S_STATUS as sstatus,a.S_TRECODE AS strecode,c.S_FUNSUBJECTCODE as sfunsubjectcode,a.S_ADDWORD as saddword " 
			+ "from TV_PAYOUTBACKMSG_MAIN a,(SELECT * FROM TV_PAYOUTBACKMSG_SUB b WHERE NOT EXISTS (SELECT * FROM TV_PAYOUTBACKMSG_SUB b1  " 
			+ "WHERE b.S_BIZNO = b1.S_BIZNO AND b.S_SEQNO < b1.S_SEQNO)) c WHERE  a.S_BACKFLAG='1' and a.S_BIZNO = c.S_BIZNO and a."+datetype+" >='" 
			+ startdate+"' AND a."+datetype+"<='"+ enddate + "' AND a.S_TRECODE='" + paramdto.getStrecode()+"'";
			if (StringUtils.isNotBlank(paramdto.getSbeflag())) {
				tcsql += " AND a.S_STATUS = '" + paramdto.getSbeflag() + "'";
			}
			sql = sql + " union "+tcsql;
			sql += " order by voudate";
		}else if(biztype.contains(MsgConstant.VOUCHER_NO_5108)){ //直接支付额度
			String datetype = "S_CONFIRUSERCODE";
			if(paramdto.getDatatype()!=null&&"0".equals(paramdto.getDatatype()))
				datetype = "S_CREATDATE";
			sql = "select row_number() over() as rowid,S_GENTICKETDATE as voudate,S_PACKAGETICKETNO as vouno,"
					+ "S_PAYBANKNAME as spaybankname,N_MONEY as nmoney,S_STATUS as sstatus from TV_DIRECTPAYMSGMAIN "
					+ "where";
			
			sql += " I_VOUSRLNO in(select S_DEALNO from tv_voucherinfo where S_VTCODE='5108' AND " +datetype+" >= '"+ startdate+ "' and "+datetype+" <='"+ enddate +"'";
			if(StringUtils.isNotBlank(paramdto.getStrecode()))
			{
				sql += " and S_TRECODE='"+paramdto.getStrecode()+"'";
			}
			if (StringUtils.isNotBlank(paramdto.getSbeflag())&&("80001".equals(paramdto.getSbeflag())||"80000".equals(paramdto.getSbeflag()))) {
				sql += " AND S_STATUS = '" + ("80001".equals(paramdto.getSbeflag())?"90":"80") + "' ";
			}
			if(StringUtils.isNotBlank(paramdto.getSbankcode()))
				sql += " and S_PAYBANKCODE='"+paramdto.getSbankcode()+"' ";
			sql +=")";
			if (StringUtils.isNotBlank(paramdto.getSbeflag())&&!"80001".equals(paramdto.getSbeflag())&&!"80000".equals(paramdto.getSbeflag())){
				sql += " AND S_STATUS = '" + paramdto.getSbeflag() + "'";
			}
			sql += " order by voudate";
		}else if(biztype.contains(MsgConstant.VOUCHER_NO_5106)){ //授权支付额度
			String datetype = "S_CONFIRUSERCODE";
			if(paramdto.getDatatype()!=null&&"0".equals(paramdto.getDatatype()))
				datetype = "S_CREATDATE";
			sql = "select row_number() over() as rowid,S_GENTICKETDATE as voudate,S_PACKAGETICKETNO as vouno,"
				+ "S_PAYBANKNAME as spaybankname,N_MONEY as nmoney,S_STATUS as sstatus from TV_GRANTPAYMSGMAIN "
				+ "where ";
			
			sql += " I_VOUSRLNO in(select S_DEALNO from tv_voucherinfo where S_VTCODE='5106' AND "+datetype+" >= '"+ startdate+ "' and "+datetype+" <='"+ enddate +"'";
			if(StringUtils.isNotBlank(paramdto.getStrecode()))
			{
				sql += " and S_TRECODE='"+paramdto.getStrecode()+"'";
			}
			if (StringUtils.isNotBlank(paramdto.getSbeflag())&&("80001".equals(paramdto.getSbeflag())||"80000".equals(paramdto.getSbeflag()))) {
				sql += " AND S_STATUS = '" + ("80001".equals(paramdto.getSbeflag())?"90":"80") + "' ";
			}
			if(StringUtils.isNotBlank(paramdto.getSbankcode()))
				sql += " and S_PAYBANKCODE='"+paramdto.getSbankcode()+"' ";
			sql +=")";
			if (StringUtils.isNotBlank(paramdto.getSbeflag())&&!"80001".equals(paramdto.getSbeflag())&&!"80000".equals(paramdto.getSbeflag())){
				sql += " AND S_STATUS = '" + paramdto.getSbeflag() + "'";
			}
			sql += " order by voudate";
		}else if(biztype.contains(MsgConstant.VOUCHER_NO_2301)){ //商行支付划款
			String datetype = "S_XCLEARDATE";
			if(paramdto.getDatatype()!=null&&"0".equals(paramdto.getDatatype()))
				datetype = "D_VOUDATE";
			sql = "select row_number() over() as rowid,replace("+datetype+",'-','') as voudate,S_VOUNO as vouno,S_PAYERNAME as spayername," 
				+ "S_PAYERACCT as spayeracct,S_AGENTACCTBANKNAME as srecbankname,S_PAYEENAME as srecname,S_PAYEEACCT as srecacct," 
				+ "F_AMT as nmoney,S_RESULT as sstatus,S_TRECODE AS strecode,S_DESCRIPTION as saddword " 
				+ "from TV_PAYRECK_BANK "
				+ "WHERE "+datetype+" >='"
				+ paramdto.getStartdate()
				+ "' AND "+datetype+"<='"
				+ paramdto.getEnddate() + "' AND S_TRECODE='" + paramdto.getStrecode() + "' ";
			if(StringUtils.isNotBlank(paramdto.getPaymode()))
				sql += " and S_PAYMODE='"+paramdto.getPaymode()+"' ";
			if(StringUtils.isNotBlank(paramdto.getSbankcode()))
				sql += " and S_AGENTBNKCODE='"+paramdto.getSbankcode()+"' ";
			if (StringUtils.isNotBlank(paramdto.getSbeflag())) {
				sql += " AND S_RESULT = '" + paramdto.getSbeflag() + "' ";
			}
			sql += " order by voudate";
		}else if(biztype.contains(MsgConstant.VOUCHER_NO_2302)){ //商行支付退款
			String datetype = "S_XCLEARDATE";
			if(paramdto.getDatatype()!=null&&"0".equals(paramdto.getDatatype()))
				datetype = "D_VOUDATE";
			sql = "select row_number() over() as rowid,replace("+datetype+",'-','') as voudate,S_VOUNO as vouno,S_PAYEENAME as spayername," 
				+ "S_PAYEEACCT as spayeracct,S_AGENTACCTBANKNAME as spayerbankname,S_REMARK as saddword,S_CLEARACCTBANKNAME as srecbankname,S_PAYERNAME as srecname,S_PAYERACCT as srecacct," 
				+ "F_AMT as nmoney,S_STATUS as sstatus,S_TRECODE AS strecode " 
				+ "from TV_PAYRECK_BANK_BACK "
				+ "WHERE "+datetype+" >='"
				+ paramdto.getStartdate()
				+ "' AND "+datetype+"<='"
				+ paramdto.getEnddate() + "' AND S_TRECODE='" + paramdto.getStrecode() + "' ";
			if(StringUtils.isNotBlank(paramdto.getPaymode()))
				sql += " and S_PAYMODE='"+paramdto.getPaymode()+"' ";
			if(StringUtils.isNotBlank(paramdto.getSbankcode()))
				sql += " and S_AGENTBNKCODE='"+paramdto.getSbankcode()+"' ";
			if (StringUtils.isNotBlank(paramdto.getSbeflag())) {
				sql += " AND S_STATUS = '" + paramdto.getSbeflag() + "' ";
			}
			sql += " order by voudate";
		}else if(biztype.contains(MsgConstant.VOUCHER_NO_5209)){ //收入退付业务
			String datetype = "D_ACCEPT";
			if(paramdto.getDatatype()!=null&&"0".equals(paramdto.getDatatype()))
				datetype = "D_ACCT";
			sql = "select row_number() over() as rowid,replace("+datetype+",'-','') as voudate,S_ELECVOUNO as vouno,S_PAYACCTNAME as spayername," 
				+ "S_PAYACCTNO as spayeracct,S_RECBANKNAME as srecbankname,S_PAYEENAME as srecname,S_PAYEEACCT as srecacct," 
				+ "F_AMT as nmoney,S_STATUS as sstatus,S_PAYERTRECODE AS strecode " 
				+ "from TV_DWBK "
				+ "WHERE "+datetype+" >='"
				+ paramdto.getStartdate()
				+ "' AND "+datetype+"<='"
				+ paramdto.getEnddate() + "' AND S_PAYERTRECODE='" + paramdto.getStrecode() + "' ";
			if (!StringUtils.isBlank(paramdto.getSbeflag())) {
				sql += " AND S_STATUS = '" + paramdto.getSbeflag() + "' ";
			}
			sql += " order by voudate";
		}
		
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.setMaxRows(100000);
			SQLResults rs = sqlExec.runQueryCloseCon(sql,FundGrantCustomDto.class);
			if(rs.getDtoCollection()!=null&&rs.getDtoCollection().size()>0)
			{
				FundGrantCustomDto temp = null;
				List<FundGrantCustomDto> returnlist = (List<FundGrantCustomDto>)rs.getDtoCollection();
				for(int i=0;i<returnlist.size();i++)
				{
					temp = returnlist.get(i);
					temp.setRowid(new Integer(i+1));
				}
				return returnlist;
			}
			return (List) rs.getDtoCollection();
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(e);
		} finally {
			if (sqlExec != null) {
				sqlExec.closeConnection();
			}
		}
	}
	/**
	 * 5207,5108,5106,2301,2302,5209明细打印查询
	 */
	public List findRsForDetail(String biztype, TipsParamDto paramdto)
			throws ITFEBizException {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String startdate = format.format(paramdto.getStartdate());
		String enddate = format.format(paramdto.getEnddate());
		// 查询sql
		String sql = "";
		if(biztype.contains(MsgConstant.VOUCHER_NO_5207)){ //实拨资金
			String datetype = "S_XPAYDATE";
			if(paramdto.getDatatype()!=null&&"0".equals(paramdto.getDatatype()))
				datetype = "S_ACCDATE";
			sql = "select row_number() over() as rowid,a.S_COMMITDATE as voudate,a.S_TAXTICKETNO as vouno,a.S_PAYERNAME as spayername," 
					+ "a.S_PAYERACCT as spayeracct,a.S_RECBANKNAME as srecbankname,a.S_RECNAME as srecname,a.S_RECACCT as srecacct," 
					+ "a.N_MONEY as nmoney,a.S_STATUS as sstatus,a.S_TRECODE AS strecode,c.S_FUNSUBJECTCODE as sfunsubjectcode,a.S_ADDWORD " 
					+ "from TV_PAYOUTMSGMAIN a,(SELECT * FROM TV_PAYOUTMSGSUB b WHERE NOT EXISTS (SELECT * FROM TV_PAYOUTMSGSUB b1  " 
					+ "WHERE b.S_BIZNO = b1.S_BIZNO AND b.S_SEQNO < b1.S_SEQNO)) c WHERE a.S_BIZNO = c.S_BIZNO and a."+datetype+" >='" 
					+ startdate+"' AND a."+datetype+"<='"+ enddate + "' AND a.S_TRECODE='" + paramdto.getStrecode() + "' ";
			if (!StringUtils.isBlank(paramdto.getSbeflag())) {
				sql += " AND a.S_STATUS = '" + paramdto.getSbeflag() + "' ";
			}
		}else if(biztype.contains(MsgConstant.VOUCHER_NO_3208)){ //实拨资金
			String datetype = "S_CONFIRUSERCODE";
			if(paramdto.getDatatype()!=null&&"0".equals(paramdto.getDatatype()))
				datetype = "S_CREATDATE";
			sql = "select row_number() over() as rowid,d."+datetype+" as voudate,d.s_voucherno as vouno,a.S_PAYERNAME as spayername," 
				+ "a.S_PAYERACCT as spayeracct,a.S_RECBANKNAME as srecbankname,a.S_RECNAME as srecname,a.S_RECACCT as srecacct,a.S_BUDGETUNITCODE as sbudgetunitcode," 
				+ "'-'||d.N_MONEY as nmoney,d.S_STATUS as sstatus,a.S_TRECODE AS strecode,c.S_FUNSUBJECTCODE as sfunsubjectcode,a.S_ADDWORD as saddword " 
				+ "from TV_PAYOUTMSGMAIN a,(SELECT * FROM TV_PAYOUTMSGSUB b WHERE NOT EXISTS (SELECT * FROM TV_PAYOUTMSGSUB b1  " 
				+ "WHERE b.S_BIZNO = b1.S_BIZNO AND b.S_SEQNO < b1.S_SEQNO)) c,TV_VOUCHERINFO d WHERE d.S_EXT4=a.S_BIZNO and  a.S_BACKFLAG='1' and a.S_BIZNO = c.S_BIZNO and d."+datetype+" >='" 
				+ startdate+"' AND d."+datetype+"<='"+ enddate + "' AND d.S_TRECODE='" + paramdto.getStrecode() + "'";
			if (!StringUtils.isBlank(paramdto.getSbeflag())) {
				sql += " AND a.S_STATUS = '" + paramdto.getSbeflag() + "' ";
			}
			datetype = "S_COMMITDATE";
			if(paramdto.getDatatype()!=null&&"0".equals(paramdto.getDatatype()))
				datetype = "S_ACCDATE";
			String tcsql = "select row_number() over() as rowid,a."+datetype+" as voudate,a.S_VOUNO as vouno,a.S_PAYEENAME as spayername," 
				+ "a.S_PAYEEACCT as spayeracct,'未知' as srecbankname,a.S_PAYERNAME as srecname,a.S_PAYERACCT as srecacct,a.S_BUDGETUNITCODE as sbudgetunitcode," 
				+ "'-'||a.N_MONEY as nmoney,a.S_STATUS as sstatus,a.S_TRECODE AS strecode,c.S_FUNSUBJECTCODE as sfunsubjectcode,a.S_ADDWORD as saddword " 
				+ "from TV_PAYOUTBACKMSG_MAIN a,(SELECT * FROM TV_PAYOUTBACKMSG_SUB b WHERE NOT EXISTS (SELECT * FROM TV_PAYOUTBACKMSG_SUB b1  " 
				+ "WHERE b.S_BIZNO = b1.S_BIZNO AND b.S_SEQNO < b1.S_SEQNO)) c WHERE  a.S_BACKFLAG='1' and a.S_BIZNO = c.S_BIZNO and a."+datetype+" >='" 
				+ startdate+"' AND a."+datetype+"<='"+ enddate + "' AND a.S_TRECODE='" + paramdto.getStrecode()+"'";
			if (!StringUtils.isBlank(paramdto.getSbeflag())) {
				tcsql += " AND a.S_STATUS = '" + paramdto.getSbeflag() + "' ";
			}
			sql = sql + " union "+tcsql;
		}else if(biztype.contains(MsgConstant.VOUCHER_NO_5108)){ //直接支付额度
			String datetype = "S_CONFIRUSERCODE";
			if(paramdto.getDatatype()!=null&&"0".equals(paramdto.getDatatype()))
				datetype = "S_CREATDATE";
			sql = "select row_number() over() as rowid,S_GENTICKETDATE as voudate,a.S_PACKAGETICKETNO as vouno,"
					+ "S_PAYBANKNAME as spaybankname,a.N_MONEY as nmoney,a.S_STATUS as sstatus,b.S_BUDGETUNITCODE AS sbudgetunitcode,b.S_FUNSUBJECTCODE AS sfunsubjectcode,b.N_MONEY AS submoney,b.I_DETAILSEQNO as idetailseqno from TV_DIRECTPAYMSGMAIN a, TV_DIRECTPAYMSGSUB b "
					+ "where A.I_VOUSRLNO = B.I_VOUSRLNO" +
					" AND A.S_TAXTICKETNO = B.S_TAXTICKETNO ";
			if (StringUtils.isNotBlank(paramdto.getSbeflag())&&!"80001".equals(paramdto.getSbeflag())&&!"80000".equals(paramdto.getSbeflag())){
				sql += " AND a.S_STATUS = '" + paramdto.getSbeflag() + "' ";
			}
			sql += " and a.I_VOUSRLNO in(select S_DEALNO from tv_voucherinfo where S_VTCODE='5108' AND " +datetype+" >= '"+ startdate+ "' and "+datetype+" <='"+ enddate +"'";
			if(StringUtils.isNotBlank(paramdto.getStrecode()))
			{
				sql += " and S_TRECODE='"+paramdto.getStrecode()+"'";
			}
			if (StringUtils.isNotBlank(paramdto.getSbeflag())&&("80001".equals(paramdto.getSbeflag())||"80000".equals(paramdto.getSbeflag()))) {
				sql += " AND S_STATUS = '" + ("80001".equals(paramdto.getSbeflag())?"90":"80") + "' ";
			}
			if(StringUtils.isNotBlank(paramdto.getSbankcode()))
				sql += " and S_PAYBANKCODE='"+paramdto.getSbankcode()+"' ";
			sql +=")";
		}else if(biztype.contains(MsgConstant.VOUCHER_NO_5106)){ //授权支付额度
			String datetype = "S_CONFIRUSERCODE";
			if(paramdto.getDatatype()!=null&&"0".equals(paramdto.getDatatype()))
				datetype = "S_CREATDATE";
			sql = "select row_number() over() as rowid,S_GENTICKETDATE as voudate,a.S_PACKAGETICKETNO as vouno,"
				+ "S_PAYBANKNAME as spaybankname,a.N_MONEY as nmoney,a.S_STATUS as sstatus,b.S_BUDGETUNITCODE AS sbudgetunitcode,b.S_FUNSUBJECTCODE AS sfunsubjectcode,B.N_MONEY AS submoney,b.I_DETAILSEQNO as idetailseqno " +
				" from TV_GRANTPAYMSGMAIN A ,TV_GRANTPAYMSGSUB B "
				+ " where A.I_VOUSRLNO = B.I_VOUSRLNO AND A.S_PACKAGETICKETNO = B.S_PACKAGETICKETNO ";
			if (StringUtils.isNotBlank(paramdto.getSbeflag())&&!"80001".equals(paramdto.getSbeflag())&&!"80000".equals(paramdto.getSbeflag())){
				sql += " AND a.S_STATUS = '" + paramdto.getSbeflag() + "' ";
			}
			sql += " and a.I_VOUSRLNO in(select S_DEALNO from tv_voucherinfo where S_VTCODE='5106' AND "+datetype+" >= '"+ startdate+ "' and "+datetype+" <='"+ enddate +"'";
			if(StringUtils.isNotBlank(paramdto.getStrecode()))
			{
				sql += " and S_TRECODE='"+paramdto.getStrecode()+"'";
			}
			if (StringUtils.isNotBlank(paramdto.getSbeflag())&&("80001".equals(paramdto.getSbeflag())||"80000".equals(paramdto.getSbeflag()))) {
				sql += " AND S_STATUS = '" + ("80001".equals(paramdto.getSbeflag())?"90":"80") + "' ";
			}
			if(StringUtils.isNotBlank(paramdto.getSbankcode()))
				sql += " and S_PAYBANKCODE='"+paramdto.getSbankcode()+"' ";
			sql +=")";
		}else if(biztype.contains(MsgConstant.VOUCHER_NO_2301)){ //商行支付划款
			String datetype = "S_XCLEARDATE";
			if(paramdto.getDatatype()!=null&&"0".equals(paramdto.getDatatype()))
				datetype = "D_VOUDATE";
			sql = "select row_number() over() as rowid,replace(a.D_VOUDATE,'-','') as voudate,a.S_VOUNO as vouno,S_PAYERNAME as spayername," 
				+ "S_PAYERACCT as spayeracct,S_AGENTACCTBANKNAME as srecbankname,S_PAYEENAME as srecname,S_PAYEEACCT as srecacct," 
				+ "a.F_AMT as nmoney,a.S_RESULT as sstatus,S_TRECODE AS strecode," +
				"b.S_BDGORGCODE AS sbudgetunitcode,b.S_FUNCBDGSBTCODE AS sfunsubjectcode,B.F_AMT AS submoney,b.I_SEQNO as idetailseqno " 
				+ "from TV_PAYRECK_BANK a,TV_PAYRECK_BANK_LIST b "
				+ "WHERE "+datetype+" >='"
				+ paramdto.getStartdate()
				+ "' AND "+datetype+"<='"
				+ paramdto.getEnddate() 
				+ "' AND S_TRECODE='" + paramdto.getStrecode() + "' " +
				" and a.I_VOUSRLNO = b.I_VOUSRLNO " ;
			if(StringUtils.isNotBlank(paramdto.getPaymode()))
				sql += " and a.S_PAYMODE='"+paramdto.getPaymode()+"' ";
			if(StringUtils.isNotBlank(paramdto.getSbankcode()))
				sql += " and a.S_AGENTBNKCODE='"+paramdto.getSbankcode()+"' ";
			if (!StringUtils.isBlank(paramdto.getSbeflag())) {
				sql += " AND a.S_RESULT = '" + paramdto.getSbeflag() + "' ";
			}
		}else if(biztype.contains(MsgConstant.VOUCHER_NO_2302)){ //商行支付退款
			String datetype = "S_XCLEARDATE";
			if(paramdto.getDatatype()!=null&&"0".equals(paramdto.getDatatype()))
				datetype = "D_VOUDATE";
			sql = "select row_number() over() as rowid,replace(a.D_VOUDATE,'-','') as voudate,a.S_VOUNO as vouno,S_PAYEENAME as spayername," 
				+ "S_PAYEEACCT as spayeracct,S_CLEARACCTBANKNAME as srecbankname,S_PAYERNAME as srecname,S_PAYERACCT as srecacct," 
				+ "a.F_AMT as nmoney,a.S_STATUS as sstatus,S_TRECODE AS strecode, " +
				"b.S_BDGORGCODE AS sbudgetunitcode,b.S_FUNCBDGSBTCODE AS sfunsubjectcode,B.F_AMT AS submoney,b.I_SEQNO as idetailseqno "  
				+ "from TV_PAYRECK_BANK_BACK a,TV_PAYRECK_BANK_BACK_LIST b "
				+ "WHERE a."+datetype+" >='"+ paramdto.getStartdate()
				+ "' AND a."+datetype+"<='"+ paramdto.getEnddate() 
				+ "' AND S_TRECODE='" + paramdto.getStrecode() + "' " +
				" and a.I_VOUSRLNO = b.I_VOUSRLNO";
			if(StringUtils.isNotBlank(paramdto.getPaymode()))
				sql += " and a.S_PAYMODE='"+paramdto.getPaymode()+"' ";
			if(StringUtils.isNotBlank(paramdto.getSbankcode()))
				sql += " and a.S_AGENTBNKCODE='"+paramdto.getSbankcode()+"' ";
			if (!StringUtils.isBlank(paramdto.getSbeflag())) {
				sql += " AND a.S_STATUS = '" + paramdto.getSbeflag() + "' ";
			}
		}else if(biztype.contains(MsgConstant.VOUCHER_NO_5209)){ //收入退付业务
			String datetype = "D_ACCEPT";
			if(paramdto.getDatatype()!=null&&"0".equals(paramdto.getDatatype()))
				datetype = "D_ACCT";
			sql = "select row_number() over() as rowid,replace(D_ACCEPT,'-','') as voudate,S_ELECVOUNO as vouno,S_PAYACCTNAME as spayername," 
				+ "S_PAYACCTNO as spayeracct,S_RECBANKNAME as srecbankname,S_PAYEENAME as srecname,S_PAYEEACCT as srecacct," 
				+ "F_AMT as nmoney,S_STATUS as sstatus,S_PAYERTRECODE AS strecode " 
				+ "from TV_DWBK "
				+ "WHERE "+datetype+" >='"
				+ paramdto.getStartdate()
				+ "' AND "+datetype+"<='"
				+ paramdto.getEnddate() + "' AND S_PAYERTRECODE='" + paramdto.getStrecode() + "' ";
			if (!StringUtils.isBlank(paramdto.getSbeflag())) {
				sql += " AND S_STATUS = '" + paramdto.getSbeflag() + "' ";
			}
		}
		
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.setMaxRows(100000);
			sql +=" ORDER BY VOUDATE";
			SQLResults rs = sqlExec.runQueryCloseCon(sql,FundGrantCustomDto.class);
			List<FundGrantCustomDto> ll_returnList = (List) rs.getDtoCollection();
			Map<String,Integer> voucherRowMap = new HashMap();
			int rowCount = 1;
			for(int i=0;i<ll_returnList.size();i++){
				String ls_voucherNo =ll_returnList.get(i).getVouno();
				if(voucherRowMap.containsKey(ls_voucherNo)){
					ll_returnList.get(i).setRowid(voucherRowMap.get(ls_voucherNo));
				}else{
					voucherRowMap.put(ls_voucherNo,rowCount++);
					ll_returnList.get(i).setRowid(voucherRowMap.get(ls_voucherNo));
				}
			}
			return ll_returnList;
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(e);
		} finally {
			if (sqlExec != null) {
				sqlExec.closeConnection();
			}
		}
	}
}
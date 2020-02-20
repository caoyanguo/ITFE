package com.cfcc.itfe.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cfcc.deptone.common.util.CallShellUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;
@SuppressWarnings("unchecked")
public class ExportBusinessDataCsv {
	private String filepath;
	String split = ",";// 文件记录分隔符号
	String line = System.getProperty("line.separator");//换行
	public int exportPayoutData(IDto dto,String where,Map filepathnameMap) throws ITFEBizException//实拨资金导出
	{
		TvPayoutmsgmainDto finddto = (TvPayoutmsgmainDto)dto;
		finddto.setSbackflag(StateConstant.MSG_BACK_FLAG_NO);
		List<IDto> dataList=null;
		String fname = null;
		if(where!=null&&where.indexOf(" S_STATUS")>=0)
			fname = getFilepath()+finddto.getScommitdate()+"_5101_shibozijin_tcbsreturn.csv";
		else
			fname = getFilepath()+finddto.getScommitdate()+"_5101_shibozijin_all.csv";
		StringBuffer filecontent = null;
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.setMaxRows(50000);
			StringBuffer mainsql = new StringBuffer("select * from Tv_Payoutmsgmain where S_COMMITDATE=? ");
			sqlExec.addParam(finddto.getScommitdate());
			if(where!=null&&where.contains(" S_STATUS"))
			{
				mainsql.append("AND (S_STATUS=? or S_STATUS=?)");
				sqlExec.addParam("80000");
				sqlExec.addParam("80001");
			}
			mainsql.append(" union select * from HTv_Payoutmsgmain where S_COMMITDATE=? ");
			sqlExec.addParam(finddto.getScommitdate());
			if(where!=null&&where.contains(" S_STATUS"))
			{
				mainsql.append("AND (S_STATUS=? or S_STATUS=?)");
				sqlExec.addParam("80000");
				sqlExec.addParam("80001");
			}
			dataList = (List<IDto>)sqlExec.runQuery(mainsql.toString(),TvPayoutmsgmainDto.class).getDtoCollection();
			sqlExec.setMaxRows(50000);
			if(dataList!=null&&dataList.size()>0)
			{
				filecontent = new StringBuffer("");
				TvPayoutmsgmainDto maindto = null;
				String sql = " select * from Tv_Payoutmsgsub where S_BIZNO in( "+StringUtil.replace(mainsql.toString(), "*", "S_BIZNO")+")";
				sqlExec.addParam(finddto.getScommitdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				sqlExec.addParam(finddto.getScommitdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				List<TvPayoutmsgsubDto> subList = new ArrayList<TvPayoutmsgsubDto>();
				List<TvPayoutmsgsubDto> templist = null;
				templist = (List<TvPayoutmsgsubDto>)sqlExec.runQuery(sql, TvPayoutmsgsubDto.class).getDtoCollection();
				sqlExec.setMaxRows(50000);
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				sql = " select * from HTv_Payoutmsgsub where S_BIZNO in( "+StringUtil.replace(mainsql.toString(), "*", "S_BIZNO")+")";
				sqlExec.addParam(finddto.getScommitdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				sqlExec.addParam(finddto.getScommitdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				templist = (List<TvPayoutmsgsubDto>)sqlExec.runQuery(sql, TvPayoutmsgsubDto.class).getDtoCollection();
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				Map<String,List<TvPayoutmsgsubDto>> subMap = new HashMap<String,List<TvPayoutmsgsubDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<TvPayoutmsgsubDto> tempList = null;
					for(TvPayoutmsgsubDto subdto:subList)
					{
						tempList = subMap.get(subdto.getSbizno());
						if(tempList==null)
						{
							tempList = new ArrayList<TvPayoutmsgsubDto>();
							tempList.add(subdto);
							subMap.put(subdto.getSbizno(), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(subdto.getSbizno(), tempList);
						}
					}
				}
				filecontent.append("主信息:文件名称,委托日期,账务日期,国库代码,出票单位,凭证编号,金额,收款人开户行行号,收款人账号,收款人名称,付款人账号,付款人名称,预算单位代码,预算单位名称,");
				filecontent.append("预算种类,整理器标志,所属年度,附言,交易状态,原因"+line);
				filecontent.append("明细信息:账务日期,经济科目代码,预算项目代码,交易金额,功能科目代码,预算类型编码,预算类型名称,收支管理代码,收支管理名称"+line);
				for(int i=0;i<dataList.size();i++)
				{
					maindto = (TvPayoutmsgmainDto)dataList.get(i);
					if(maindto.getSfilename()!=null&&maindto.getSfilename().contains("/"))
						filecontent.append(maindto.getSfilename().substring(maindto.getSfilename().lastIndexOf("/")+1)+split);//文件名称
					else
						filecontent.append(getString(maindto.getSfilename())+split);//文件名称
					filecontent.append(getString(maindto.getScommitdate())+split);//委托日期
					filecontent.append(getString(maindto.getSaccdate())+split);//账务日期
					filecontent.append(getString(maindto.getStrecode())+split);//国库代码
					filecontent.append(getString(maindto.getSpayunit())+split);//出票单位
					filecontent.append(getString(maindto.getStaxticketno())+split);//凭证编号
					filecontent.append(getString(String.valueOf(maindto.getNmoney()))+split);//金额
					String bankno = maindto.getSrecbankno();//接收行号//					String bankname = "";//					if(bankMap != null){//						bankname = bankMap.get(bankno)==null ? "": bankMap.get(bankno).getSbankname();//接收行名//					}
					filecontent.append(getString(bankno)+split);//收款人开户行行号//					filecontent.append(bankname+split);//收款人开户行名称
					filecontent.append(getString(maindto.getSrecacct())+split);//收款人账号
					filecontent.append(getString(maindto.getSrecname())+split);//收款人名称
					filecontent.append(getString(maindto.getSpayeracct())+split);//付款人账号
					filecontent.append(getString(maindto.getSpayername())+split);//付款人名称
					filecontent.append(getString(maindto.getSbudgetunitcode())+split); //预算单位代码
					filecontent.append(getString(maindto.getSunitcodename())+split); //预算单位名称
					filecontent.append(getString(maindto.getSbudgettype())+split); //预算种类
					filecontent.append(getString(maindto.getStrimflag())+split); //整理器标志
					filecontent.append(getString(maindto.getSofyear())+split); //所属年度
					filecontent.append(getString(maindto.getSaddword())+split);//附言
					filecontent.append(getString(maindto.getSstatus())+split);//交易状态
					filecontent.append(maindto.getSdemo()==null ? ""+line : maindto.getSdemo()+line);//原因
					subList = subMap.get(maindto.getSbizno());
					if(subList!=null&&subList.size()>0)
					{
						for(TvPayoutmsgsubDto subdto:subList)
						{
							filecontent.append("	");
							filecontent.append(getString(subdto.getSaccdate())+split);//账务日期
							filecontent.append(getString(subdto.getSecnomicsubjectcode())+split);//经济科目代码
							filecontent.append(getString(subdto.getSbudgetprjcode())+split);//预算项目代码
							filecontent.append(getString(String.valueOf(subdto.getNmoney()))+split);//交易金额
							filecontent.append(getString(subdto.getSfunsubjectcode())+split);//功能科目代码
							filecontent.append(getString(subdto.getSbgttypecode())+split);//预算类型编码
							filecontent.append(getString(subdto.getSbgttypename())+split);//预算类型名称
							filecontent.append(getString(subdto.getSprocatcode())+split);//收支管理代码
							filecontent.append(getString(subdto.getSprocatname())+line);//收支管理名称
						}
					}
				}
				writeFile(fname,filecontent.toString(),filepathnameMap);
				if(sqlExec!=null)
					sqlExec.closeConnection();
				CallShellUtil.callShellWithRes("chmod 777 "+fname);
			}
			
		} catch (FileOperateException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("写文件出错",e);
		} catch (JAFDatabaseException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("查询出错:"+e.toString(),e);
		} catch (Exception e) {
			throw new ITFEBizException("文件赋权出错:chmod 777"+fname+e.toString(),e);
		}finally
		{
			if(sqlExec!=null)
				sqlExec.closeConnection();
		}
		return dataList!=null&&dataList.size()>0?dataList.size():0;
	}
	public int exportPayoutBack(IDto dto,Map filepathnameMap) throws ITFEBizException//实拨资金退回导出
	{
		TvPayoutmsgmainDto finddto = (TvPayoutmsgmainDto)dto;
		List<IDto> dataList=null;
		String fname = null;
		fname = getFilepath()+finddto.getScommitdate()+"_3145_shibotuihui_all.csv";
		StringBuffer filecontent = null;
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.setMaxRows(50000);
			StringBuffer mainsql = new StringBuffer("select * from Tv_Payoutmsgmain where S_COMMITDATE=? and S_BACKFLAG=? ");
			sqlExec.addParam(finddto.getScommitdate());
			sqlExec.addParam(StateConstant.MSG_BACK_FLAG_YES);
			mainsql.append(" union select * from HTv_Payoutmsgmain where S_COMMITDATE=? and S_BACKFLAG=? ");
			sqlExec.addParam(finddto.getScommitdate());
			sqlExec.addParam(StateConstant.MSG_BACK_FLAG_YES);
			dataList = (List<IDto>)sqlExec.runQuery(mainsql.toString(),TvPayoutmsgmainDto.class).getDtoCollection();
			sqlExec.setMaxRows(50000);
			if(dataList!=null&&dataList.size()>0)
			{
				TvPayoutmsgmainDto maindto = null;
				String sql = " select * from Tv_Payoutmsgsub where S_BIZNO in( "+StringUtil.replace(mainsql.toString(), "*", "S_BIZNO")+")";
				sqlExec.addParam(finddto.getScommitdate());
				sqlExec.addParam(StateConstant.MSG_BACK_FLAG_YES);
				sqlExec.addParam(finddto.getScommitdate());
				sqlExec.addParam(StateConstant.MSG_BACK_FLAG_YES);
				List<TvPayoutmsgsubDto> subList = new ArrayList<TvPayoutmsgsubDto>();
				List<TvPayoutmsgsubDto> templist = null;
				templist = (List<TvPayoutmsgsubDto>)sqlExec.runQuery(sql, TvPayoutmsgsubDto.class).getDtoCollection();
				sqlExec.setMaxRows(50000);
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				sql = " select * from HTv_Payoutmsgsub where S_BIZNO in( "+StringUtil.replace(mainsql.toString(), "*", "S_BIZNO")+")";
				sqlExec.addParam(finddto.getScommitdate());
				sqlExec.addParam(StateConstant.MSG_BACK_FLAG_YES);
				sqlExec.addParam(finddto.getScommitdate());
				sqlExec.addParam(StateConstant.MSG_BACK_FLAG_YES);
				templist = (List<TvPayoutmsgsubDto>)sqlExec.runQuery(sql, TvPayoutmsgsubDto.class).getDtoCollection();
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				Map<String,List<TvPayoutmsgsubDto>> subMap = new HashMap<String,List<TvPayoutmsgsubDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<TvPayoutmsgsubDto> tempList = null;
					for(TvPayoutmsgsubDto subdto:subList)
					{
						tempList = subMap.get(subdto.getSbizno());
						if(tempList==null)
						{
							tempList = new ArrayList<TvPayoutmsgsubDto>();
							tempList.add(subdto);
							subMap.put(subdto.getSbizno(), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(subdto.getSbizno(), tempList);
						}
					}
				}
				filecontent = new StringBuffer("");
				filecontent.append("主信息:文件名称,委托日期,账务日期,国库代码,出票单位,凭证编号,交易金额,退回金额,原收款人开户行行号,原收款人账号,原收款人名称,原付款人账号,原付款人名称,预算单位代码,预算单位名称,");
				filecontent.append("预算种类,整理期标志,所属年度,附言,交易状态,原因"+line);
				filecontent.append("明细信息:账务日期,经济科目代码,预算项目代码,交易金额,功能科目代码,预算类型编码,预算类型名称,收支管理代码,收支管理名称"+line);
				for(int i=0;i<dataList.size();i++)
				{
					maindto = (TvPayoutmsgmainDto)dataList.get(i);
					if(maindto.getSfilename()!=null&&maindto.getSfilename().contains("/"))
						filecontent.append(maindto.getSfilename().substring(maindto.getSfilename().lastIndexOf("/")+1)+split);//文件名称
					else
						filecontent.append(getString(maindto.getSfilename())+split);//文件名称
					filecontent.append(getString(maindto.getScommitdate())+split);//委托日期
					filecontent.append(getString(maindto.getSaccdate())+split);//账务日期
					filecontent.append(getString(maindto.getStrecode())+split);//国库代码
					filecontent.append(getString(maindto.getSpayunit())+split);//出票单位
					filecontent.append(getString(maindto.getStaxticketno())+split);//凭证编号
					filecontent.append(getString(String.valueOf(maindto.getNmoney()))+split);//交易金额
					filecontent.append(getString(maindto.getShold2())+split);//退回金额
					String bankno = maindto.getSrecbankno();//接收行号//					String bankname = "";//					if(bankMap != null){//						bankname = bankMap.get(bankno)==null ? "": bankMap.get(bankno).getSbankname();//接收行名//					}
					filecontent.append(getString(bankno)+split);//收款人开户行行号//					filecontent.append(bankname+split);//收款人开户行名称
					filecontent.append(getString(maindto.getSrecacct())+split);//收款人账号
					filecontent.append(getString(maindto.getSrecname())+split);//收款人名称
					filecontent.append(getString(maindto.getSpayeracct())+split);//付款人账号
					filecontent.append(getString(maindto.getSpayername())+split);//付款人名称
					filecontent.append(getString(maindto.getSbudgetunitcode())+split); //预算单位代码
					filecontent.append(getString(maindto.getSunitcodename())+split); //预算单位名称
					filecontent.append(getString(maindto.getSbudgettype())+split); //预算种类
					filecontent.append(getString(maindto.getStrimflag())+split); //整理器标志
					filecontent.append(getString(maindto.getSofyear())+split); //所属年度
					filecontent.append(getString(maindto.getSaddword())+split);//附言
					filecontent.append(getString(maindto.getSstatus())+split);//交易状态
					filecontent.append(maindto.getSdemo()==null ? ""+line : maindto.getSdemo()+line);//原因
					subList = subMap.get(maindto.getSbizno());
					if(subList!=null&&subList.size()>0)
					{
						for(TvPayoutmsgsubDto subdto:subList)
						{
							filecontent.append("	");
							filecontent.append(getString(subdto.getSaccdate())+split);//账务日期
							filecontent.append(getString(subdto.getSecnomicsubjectcode())+split);//经济科目代码
							filecontent.append(getString(subdto.getSbudgetprjcode())+split);//预算项目代码
							filecontent.append(getString(String.valueOf(subdto.getNmoney()))+split);//交易金额
							filecontent.append(getString(subdto.getSfunsubjectcode())+split);//功能科目代码
							filecontent.append(getString(subdto.getSbgttypecode())+split);//预算类型编码
							filecontent.append(getString(subdto.getSbgttypename())+split);//预算类型名称
							filecontent.append(getString(subdto.getSprocatcode())+split);//收支管理代码
							filecontent.append(getString(subdto.getSprocatname())+line);//收支管理名称
						}
					}
				}
				writeFile(fname,filecontent.toString(),filepathnameMap);
				if(sqlExec!=null)
					sqlExec.closeConnection();
				CallShellUtil.callShellWithRes("chmod 777 "+fname);
			}
			
		} catch (FileOperateException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("写文件出错",e);
		} catch (JAFDatabaseException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("查询出错:"+e.toString(),e);
		} catch (Exception e) {
			throw new ITFEBizException("文件赋权出错:chmod 777"+fname+e.toString(),e);
		} finally
		{
			if(sqlExec!=null)
				sqlExec.closeConnection();
		}
		return dataList!=null&&dataList.size()>0?dataList.size():0;
	}
	public int exportDirectPayData(IDto dto,String where,Map filepathnameMap) throws ITFEBizException//直接支付额度导出
	{
		TvDirectpaymsgmainDto finddto = (TvDirectpaymsgmainDto)dto;
		List<IDto> dataList=null;
		String fname = null;
		if(where!=null&&where.indexOf(" S_STATUS")>=0)
			fname = getFilepath()+finddto.getScommitdate()+"_5102_zhijiezhifu_tcbsreturn.csv";
		else
			fname = getFilepath()+finddto.getScommitdate()+"_5102_zhijiezhifu_all.csv";
		StringBuffer filecontent = null;
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.setMaxRows(50000);
			StringBuffer mainsql = new StringBuffer("select * from TV_DIRECTPAYMSGMAIN where S_COMMITDATE=? ");
			sqlExec.addParam(finddto.getScommitdate());
			if(where!=null&&where.contains(" S_STATUS"))
			{
				mainsql.append("AND (S_STATUS=? or S_STATUS=?)");
				sqlExec.addParam("80000");
				sqlExec.addParam("80001");
			}
			mainsql.append(" union select * from HTV_DIRECTPAYMSGMAIN where S_COMMITDATE=? ");
			sqlExec.addParam(finddto.getScommitdate());
			if(where!=null&&where.contains(" S_STATUS"))
			{
				mainsql.append("AND (S_STATUS=? or S_STATUS=?)");
				sqlExec.addParam("80000");
				sqlExec.addParam("80001");
			}
			dataList = (List<IDto>)sqlExec.runQuery(mainsql.toString(),TvDirectpaymsgmainDto.class).getDtoCollection();
			sqlExec.setMaxRows(50000);
			if(dataList!=null&&dataList.size()>0)
			{
				filecontent = new StringBuffer("");
				TvDirectpaymsgmainDto maindto = null;
				String sql = " select * from Tv_Directpaymsgsub where I_VOUSRLNO in("+StringUtil.replace(mainsql.toString(), "*", "I_VOUSRLNO")+")";
				sqlExec.addParam(finddto.getScommitdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				sqlExec.addParam(finddto.getScommitdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				List<TvDirectpaymsgsubDto> subList = new ArrayList<TvDirectpaymsgsubDto>();
				List<TvDirectpaymsgsubDto> templist = null;
				templist = (List<TvDirectpaymsgsubDto>)sqlExec.runQuery(sql, TvDirectpaymsgsubDto.class).getDtoCollection();
				sqlExec.setMaxRows(50000);
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				sql = " select * from HTv_Directpaymsgsub where I_VOUSRLNO in("+StringUtil.replace(mainsql.toString(), "*", "I_VOUSRLNO")+")";
				sqlExec.addParam(finddto.getScommitdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				sqlExec.addParam(finddto.getScommitdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				templist = (List<TvDirectpaymsgsubDto>)sqlExec.runQuery(sql, TvDirectpaymsgsubDto.class).getDtoCollection();
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				Map<String,List<TvDirectpaymsgsubDto>> subMap = new HashMap<String,List<TvDirectpaymsgsubDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<TvDirectpaymsgsubDto> tempList = null;
					for(TvDirectpaymsgsubDto subdto:subList)
					{
						tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
						if(tempList==null)
						{
							tempList = new ArrayList<TvDirectpaymsgsubDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}
					}
				}
				filecontent.append("主信息:文件名称,委托日期,账务日期,国库代码,出票单位,凭证编号,金额,包流水号,转发银行,经办单位,预算种类,额度种类,收款人账号,收款人名称,");
				filecontent.append("收款人开户行名称,付款人账号,付款人名称,付款银行名称,代理银行编码,代理银行行号,交易状态,代理银行名称"+line);
				filecontent.append("明细信息:账务日期,预算内单位代码,功能科目代码,交易金额,经济科目代码,预算单位编码,预算单位名称,支付明细,所属年度"+line);
				for(int i=0;i<dataList.size();i++)
				{
					maindto = (TvDirectpaymsgmainDto)dataList.get(i);
					if(maindto.getSfilename()!=null&&maindto.getSfilename().contains("/"))
						filecontent.append(maindto.getSfilename().substring(maindto.getSfilename().lastIndexOf("/")+1)+split);//文件名称
					else
						filecontent.append(getString(maindto.getSfilename())+split);//文件名称
					filecontent.append(getString(maindto.getScommitdate())+split);//委托日期
					filecontent.append(getString(maindto.getSaccdate())+split);//账务日期
					filecontent.append(getString(maindto.getStrecode())+split);//国库代码
					filecontent.append(getString(maindto.getSpayunit())+split);//出票单位
					filecontent.append(getString(maindto.getStaxticketno())+split);//凭证编号
					filecontent.append(getString(String.valueOf(maindto.getNmoney()))+split);//金额
					filecontent.append(getString(maindto.getSpackageno())+split);//包流水号
					filecontent.append(getString(maindto.getStransbankcode())+split);//转发银行
					filecontent.append(getString(maindto.getStransactunit())+split);//经办单位
					filecontent.append(getString(maindto.getSbudgettype())+split);//预算种类
					filecontent.append(getString(maindto.getSamttype())+split);//额度种类
					filecontent.append(getString(maindto.getSpayeeacctno())+split);//收款人账号
					filecontent.append(getString(maindto.getSpayeeacctname())+split); //收款人名称
					filecontent.append(getString(maindto.getSpayeeacctbankname())+split); //收款人开户行名称
					filecontent.append(getString(maindto.getSpayacctno())+split); //付款人账号
					filecontent.append(getString(maindto.getSpayacctname())+split); //付款人名称
					filecontent.append(getString(maindto.getSpayacctbankname())+split); //付款银行名称
					filecontent.append(getString(maindto.getSpaybankcode())+split);//代理银行编码
					filecontent.append(getString(maindto.getSpaybankno())+split);//代理银行行号
					filecontent.append(getString(maindto.getSstatus())+split);//交易状态
					filecontent.append(getString(maindto.getSpaybankname())+line);//代理银行名称
					subList = subMap.get(String.valueOf(maindto.getIvousrlno()));
					if(subList!=null&&subList.size()>0)
					{
						for(TvDirectpaymsgsubDto subdto:subList)
						{
							filecontent.append("	");
							filecontent.append(getString(subdto.getSaccdate())+split);//账务日期
							filecontent.append(getString(subdto.getSbudgetunitcode())+split);//预算内单位代码
							filecontent.append(getString(subdto.getSfunsubjectcode())+split);//功能科目代码
							filecontent.append(getString(String.valueOf(subdto.getNmoney()))+split);//交易金额
							filecontent.append(getString(subdto.getSecosubjectcode())+split);//经济科目代码
							filecontent.append(getString(subdto.getSagencycode())+split);//预算单位编码
							filecontent.append(getString(subdto.getSagencyname())+split);//预算单位名称
							filecontent.append(getString(subdto.getSline())+split);//支付明细
							filecontent.append(getString(subdto.getSofyear())+line);//所属年度
						}
					}
				}
				writeFile(fname,filecontent.toString(),filepathnameMap);
				if(sqlExec!=null)
					sqlExec.closeConnection();
				CallShellUtil.callShellWithRes("chmod 777 "+fname);
			}
			
		} catch (FileOperateException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("写文件出错",e);
		} catch (JAFDatabaseException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("查询出错:"+e.toString(),e);
		} catch (Exception e) {
			throw new ITFEBizException("文件赋权出错:chmod 777"+fname+e.toString(),e);
		}finally
		{
			if(sqlExec!=null)
				sqlExec.closeConnection();
		}
		return dataList!=null&&dataList.size()>0?dataList.size():0;
	}
	public int exportGrantPayData(IDto dto,String where,Map filepathnameMap) throws ITFEBizException//授权支付额度导出
	{
		TvGrantpaymsgmainDto finddto = (TvGrantpaymsgmainDto)dto;
		List<IDto> dataList=null;
		String fname = null;
		if(where!=null&&where.indexOf(" S_STATUS")>=0)
			fname = getFilepath()+finddto.getScommitdate()+"_5103_shouquanzhifu_tcbsreturn.csv";
		else
			fname = getFilepath()+finddto.getScommitdate()+"_5103_shouquanzhifu_all.csv";
		StringBuffer filecontent = null;
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.setMaxRows(50000);
			StringBuffer mainsql = new StringBuffer("select * from TV_GRANTPAYMSGMAIN where S_COMMITDATE=? ");
			sqlExec.addParam(finddto.getScommitdate());
			if(where!=null&&where.contains(" S_STATUS"))
			{
				mainsql.append("AND (S_STATUS=? or S_STATUS=?)");
				sqlExec.addParam("80000");
				sqlExec.addParam("80001");
			}
			mainsql.append(" union select * from HTV_GRANTPAYMSGMAIN where S_COMMITDATE=? ");
			sqlExec.addParam(finddto.getScommitdate());
			if(where!=null&&where.contains(" S_STATUS"))
			{
				mainsql.append("AND (S_STATUS=? or S_STATUS=?)");
				sqlExec.addParam("80000");
				sqlExec.addParam("80001");
			}
			dataList = (List<IDto>)sqlExec.runQuery(mainsql.toString(),TvGrantpaymsgmainDto.class).getDtoCollection();	
			sqlExec.setMaxRows(50000);
			if(dataList!=null&&dataList.size()>0)
			{
				TvGrantpaymsgmainDto maindto = null;
				String sql = " select b.* from TV_GRANTPAYMSGSUB b,TV_GRANTPAYMSGMAIN a where a.I_VOUSRLNO=b.I_VOUSRLNO and a.S_PACKAGETICKETNO=b.S_PACKAGETICKETNO and a.S_COMMITDATE='"+finddto.getScommitdate()+"' "+StringUtil.replace(where, "S_STATUS", "a.S_STATUS");
				List<TvGrantpaymsgsubDto> subList = new ArrayList<TvGrantpaymsgsubDto>();
				List<TvGrantpaymsgsubDto> templist = (List<TvGrantpaymsgsubDto>)sqlExec.runQuery(sql, TvGrantpaymsgsubDto.class).getDtoCollection();
				sqlExec.setMaxRows(50000);
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				sql = " select b.* from TV_GRANTPAYMSGSUB b,HTV_GRANTPAYMSGMAIN a where a.I_VOUSRLNO=b.I_VOUSRLNO and a.S_PACKAGETICKETNO=b.S_PACKAGETICKETNO and a.S_COMMITDATE='"+finddto.getScommitdate()+"' "+StringUtil.replace(where, "S_STATUS", "a.S_STATUS");
				templist = (List<TvGrantpaymsgsubDto>)sqlExec.runQuery(sql, TvGrantpaymsgsubDto.class).getDtoCollection();
				sqlExec.setMaxRows(50000);
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				sql = " select b.* from HTV_GRANTPAYMSGSUB b,TV_GRANTPAYMSGMAIN a where a.I_VOUSRLNO=b.I_VOUSRLNO and a.S_PACKAGETICKETNO=b.S_PACKAGETICKETNO and a.S_COMMITDATE='"+finddto.getScommitdate()+"' "+StringUtil.replace(where, "S_STATUS", "a.S_STATUS");
				templist = (List<TvGrantpaymsgsubDto>)sqlExec.runQuery(sql, TvGrantpaymsgsubDto.class).getDtoCollection();
				sqlExec.setMaxRows(50000);
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				sql = " select b.* from HTV_GRANTPAYMSGSUB b,HTV_GRANTPAYMSGMAIN a where a.I_VOUSRLNO=b.I_VOUSRLNO and a.S_PACKAGETICKETNO=b.S_PACKAGETICKETNO and a.S_COMMITDATE='"+finddto.getScommitdate()+"' "+StringUtil.replace(where, "S_STATUS", "a.S_STATUS");
				templist = (List<TvGrantpaymsgsubDto>)sqlExec.runQuery(sql, TvGrantpaymsgsubDto.class).getDtoCollection();
				sqlExec.setMaxRows(50000);
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				Map<String,List<TvGrantpaymsgsubDto>> subMap = new HashMap<String,List<TvGrantpaymsgsubDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<TvGrantpaymsgsubDto> tempList = null;
					for(TvGrantpaymsgsubDto subdto:subList)
					{
						tempList = subMap.get(String.valueOf(subdto.getIvousrlno())+String.valueOf(subdto.getSpackageticketno()));
						if(tempList==null)
						{
							tempList = new ArrayList<TvGrantpaymsgsubDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno())+String.valueOf(subdto.getSpackageticketno()), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno())+String.valueOf(subdto.getSpackageticketno()), tempList);
						}
					}
				}
				filecontent = new StringBuffer("");
				filecontent.append("主信息:文件名称,委托日期,账务日期,国库代码,出票单位,批次号,金额,包流水号,转发银行,经办单位,预算种类,额度种类,开票日期,所属年度,所属月份,");
				filecontent.append("备注,清算额度通知单,预算单位数量,资金性质编码,代理银行编码,代理银行行号,交易状态,代理银行名称"+line);
				filecontent.append("明细信息:账务日期,预算内单位代码,功能科目代码,交易金额,经济科目代码,账户性质,备注,支付明细,所属年度"+line);
				for(int i=0;i<dataList.size();i++)
				{
					maindto = (TvGrantpaymsgmainDto)dataList.get(i);
					if(maindto.getSfilename()!=null&&maindto.getSfilename().contains("/"))
						filecontent.append(maindto.getSfilename().substring(maindto.getSfilename().lastIndexOf("/")+1)+split);//文件名称
					else
						filecontent.append(getString(maindto.getSfilename())+split);//文件名称
					filecontent.append(getString(maindto.getScommitdate())+split);//委托日期
					filecontent.append(getString(maindto.getSaccdate())+split);//账务日期
					filecontent.append(getString(maindto.getStrecode())+split);//国库代码
					filecontent.append(getString(maindto.getSpayunit())+split);//出票单位
					filecontent.append(getString(maindto.getSlimitid())+split);//批次号
					filecontent.append(getString(String.valueOf(maindto.getNmoney()))+split);//金额
					filecontent.append(getString(maindto.getSpackageno())+split);//包流水号
					filecontent.append(getString(maindto.getStransbankcode())+split);//转发银行
					filecontent.append(getString(maindto.getStransactunit())+split);//经办单位
					filecontent.append(getString(maindto.getSbudgettype())+split);//预算种类
					filecontent.append(getString(maindto.getSamttype())+split);//额度种类
					filecontent.append(getString(maindto.getSgenticketdate())+split);//开票日期
					filecontent.append(getString(maindto.getSofyear())+split); //所属年度
					filecontent.append(getString(maindto.getSofmonth())+split); //所属月份
					filecontent.append(getString(maindto.getSdemo())+split); //备注
					filecontent.append(getString(maindto.getSid())+split); //清算额度通知单
					filecontent.append(getString(maindto.getSdeptnum())+split); //预算单位数量
					filecontent.append(getString(maindto.getSfundtypecode())+split); //资金性质编码
					filecontent.append(getString(maindto.getSpaybankcode())+split);//代理银行编码
					filecontent.append(getString(maindto.getSpaybankno())+split);//代理银行行号
					filecontent.append(getString(maindto.getSstatus())+split);//交易状态
					filecontent.append(getString(maindto.getSpaybankname())+line);//代理银行名称
					subList = subMap.get(String.valueOf(maindto.getIvousrlno())+String.valueOf(maindto.getSpackageticketno()));
					if(subList!=null&&subList.size()>0)
					{
						for(TvGrantpaymsgsubDto subdto:subList)
						{
							filecontent.append("	");
							filecontent.append(getString(subdto.getSaccdate())+split);//账务日期
							filecontent.append(getString(subdto.getSbudgetunitcode())+split);//预算内单位代码
							filecontent.append(getString(subdto.getSfunsubjectcode())+split);//功能科目代码
							filecontent.append(getString(String.valueOf(subdto.getNmoney()))+split);//交易金额
							filecontent.append(getString(subdto.getSecosubjectcode())+split);//经济科目代码
							filecontent.append(getString(subdto.getSaccattrib())+split);//账户性质
							filecontent.append(getString(subdto.getSdemo())+split);//备注
							filecontent.append(getString(subdto.getSline())+split);//支付明细
							filecontent.append(getString(subdto.getSofyear())+line);//所属年度
						}
					}
				}
				writeFile(fname,filecontent.toString(),filepathnameMap);
				if(sqlExec!=null)
					sqlExec.closeConnection();
				CallShellUtil.callShellWithRes("chmod 777 "+fname);
			}
			
		} catch (FileOperateException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("写文件出错",e);
		} catch (JAFDatabaseException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("查询出错:"+e.toString(),e);
		} catch (Exception e) {
			throw new ITFEBizException("文件赋权出错:chmod 777"+fname+e.toString(),e);
		}finally
		{
			if(sqlExec!=null)
				sqlExec.closeConnection();
		}
		return dataList!=null&&dataList.size()>0?dataList.size():0;
	}
	public int exportCommApplyPayData(IDto dto,String where,Map filepathnameMap) throws ITFEBizException//商行划款导出
	{
		TvPayreckBankDto finddto = (TvPayreckBankDto)dto;
		List<IDto> dataList=null;
		String fname = null;
		if(where!=null&&where.indexOf(" S_RESULT")>=0)
			fname = getFilepath()+finddto.getDentrustdate()+"_2301_shanghenghuakuan_tcbsreturn.csv";
		else
			fname = getFilepath()+finddto.getDentrustdate()+"_2301_shanghenghuakuan_all.csv";
		StringBuffer filecontent = null;
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.setMaxRows(50000);
			StringBuffer mainsql = new StringBuffer("select * from TV_PAYRECK_BANK where D_ENTRUSTDATE=? ");
			sqlExec.addParam(finddto.getDentrustdate());
			if(where!=null&&where.contains(" S_RESULT"))
			{
				mainsql.append("AND (S_RESULT=? or S_RESULT=?)");
				sqlExec.addParam("80000");
				sqlExec.addParam("80001");
			}
			mainsql.append(" union select * from HTV_PAYRECK_BANK where D_ENTRUSTDATE=?  ");
			sqlExec.addParam(finddto.getDentrustdate());
			if(where!=null&&where.contains(" S_RESULT"))
			{
				mainsql.append("AND (S_RESULT=? or S_RESULT=?)");
				sqlExec.addParam("80000");
				sqlExec.addParam("80001");
			}
			dataList = (List<IDto>)sqlExec.runQuery(mainsql.toString(),TvPayreckBankDto.class).getDtoCollection();
			sqlExec.setMaxRows(50000);
			if(dataList!=null&&dataList.size()>0)
			{
				TvPayreckBankDto maindto = null;
				filecontent = new StringBuffer("");
				String sql = " select * from Tv_Payreck_Bank_List where I_VOUSRLNO in( "+StringUtil.replace(mainsql.toString(), "*", "I_VOUSRLNO")+")";
				sqlExec.addParam(finddto.getDentrustdate());
				if(where!=null&&where.contains(" S_RESULT"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				sqlExec.addParam(finddto.getDentrustdate());
				if(where!=null&&where.contains(" S_RESULT"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				List<TvPayreckBankListDto> subList = new ArrayList<TvPayreckBankListDto>();
				List<TvPayreckBankListDto> templist = null;
				templist = (List<TvPayreckBankListDto>)sqlExec.runQuery(sql, TvPayreckBankListDto.class).getDtoCollection();
				sqlExec.setMaxRows(50000);
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				sql = " select * from HTv_Payreck_Bank_List where I_VOUSRLNO in( "+StringUtil.replace(mainsql.toString(), "*", "I_VOUSRLNO")+")";
				sqlExec.addParam(finddto.getDentrustdate());
				if(where!=null&&where.contains(" S_RESULT"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				sqlExec.addParam(finddto.getDentrustdate());
				if(where!=null&&where.contains(" S_RESULT"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				templist = (List<TvPayreckBankListDto>)sqlExec.runQuery(sql, TvPayreckBankListDto.class).getDtoCollection();
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				Map<String,List<TvPayreckBankListDto>> subMap = new HashMap<String,List<TvPayreckBankListDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<TvPayreckBankListDto> tempList = null;
					for(TvPayreckBankListDto subdto:subList)
					{
						tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
						if(tempList==null)
						{
							tempList = new ArrayList<TvPayreckBankListDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}
					}
				}
				filecontent.append("主信息:文件名称,委托日期,账务日期,凭证日期,受理日期,付款国库代码,付款人账号,付款人名称,付款人地址,收款人账号,收款人名称,收款人地址,收款人开户行行号,");
				filecontent.append("代理银行代码,代理银行名称,包流水号,交易流水号,所属年度,财政机关代码,附言,预算种类,整理期标志,金额,");
				filecontent.append("处理结果,明细条数,说明,预算类型代码,预算类型名称,资金性质代码,资金性质名称,支付方式代码,支付方式名称,支付发起行行号,申请划款凭证,交易状态,代理银行名称"+line);
				filecontent.append("明细信息:预算单位代码,预算单位名称,功能类科目代码,经济类科目代码,交易金额,支出功能科目名称,摘要名称"+line);
				for(int i=0;i<dataList.size();i++)
				{
					maindto = (TvPayreckBankDto)dataList.get(i);
					if(maindto.getSfilename()!=null&&maindto.getSfilename().contains("/"))
						filecontent.append(maindto.getSfilename().substring(maindto.getSfilename().lastIndexOf("/")+1)+split);//文件名称
					else
						filecontent.append(getString(maindto.getSfilename())+split);//文件名称
					filecontent.append(getString(String.valueOf(maindto.getDentrustdate()))+split);//委托日期
					filecontent.append(getString(String.valueOf(maindto.getDacctdate()))+split);//账务日期
					filecontent.append(getString(String.valueOf(maindto.getDvoudate()))+split);//凭证日期
					filecontent.append(getString(String.valueOf(maindto.getDacceptdate()))+split);//受理日期
					filecontent.append(getString(maindto.getStrecode())+split);//付款国库代码
					filecontent.append(getString(maindto.getSpayeracct())+split);//付款人账号
					filecontent.append(getString(maindto.getSpayername())+split);//付款人名称
					filecontent.append(getString(maindto.getSpayeraddr())+split);//付款人地址
					filecontent.append(getString(maindto.getSpayeeacct())+split);//收款人账号
					filecontent.append(getString(maindto.getSpayeename())+split);//收款人名称
					filecontent.append(getString(maindto.getSpayeeaddr())+split);//收款人地址
					filecontent.append(getString(maindto.getSpayeeopbkno())+split);//收款人开户行行号
					filecontent.append(getString(maindto.getSagentbnkcode())+split);//代理银行代码
					filecontent.append(getString(maindto.getSagentacctbankname())+split);//代理银行名称
					filecontent.append(getString(maindto.getSpackno())+split);//包流水号
					filecontent.append(getString(maindto.getStrano())+split);//交易流水号
					filecontent.append(getString(maindto.getSofyear())+split); //所属年度
					filecontent.append(getString(maindto.getSfinorgcode())+split); //财政机关代码
					filecontent.append(getString(maindto.getSaddword())+split); //附言
					filecontent.append(getString(maindto.getSbudgettype())+split); //预算种类
					filecontent.append(getString(maindto.getStrimsign())+split); //整理期标志
					filecontent.append(getString(String.valueOf(maindto.getFamt()))+split); //金额
					filecontent.append(getString(maindto.getSresult())+split); //处理结果
					filecontent.append(getString(String.valueOf(maindto.getIstatinfnum()))+split);//明细条数
					filecontent.append(getString(maindto.getSdescription())+split);//说明
					filecontent.append(getString(maindto.getSbgttypecode())+split);//预算类型代码
					filecontent.append(getString(maindto.getSbgttypename())+split);//预算类型名称
					filecontent.append(getString(maindto.getSfundtypecode())+split);//资金性质代码
					filecontent.append(getString(maindto.getSfundtypename())+split);//资金性质名称
					filecontent.append(getString(maindto.getSpaytypecode())+split);//支付方式代码
					filecontent.append(getString(maindto.getSpaytypename())+split);//支付方式名称
					filecontent.append(getString(maindto.getSxpaysndbnkno())+split);//支付发起行行号
					filecontent.append(getString(maindto.getSid())+split);//申请划款凭证
					filecontent.append(getString(maindto.getSresult())+split);//交易状态
					filecontent.append(getString(maindto.getSpaybankname())+line);//代理银行名称
					subList = subMap.get(String.valueOf(maindto.getIvousrlno()));
					if(subList!=null&&subList.size()>0)
					{
						for(TvPayreckBankListDto subdto:subList)
						{
							filecontent.append("	");
							filecontent.append(getString(subdto.getSbdgorgcode())+split);//预算单位代码
							filecontent.append(getString(subdto.getSsupdepname())+split);//预算单位名称
							filecontent.append(getString(subdto.getSfuncbdgsbtcode())+split);//功能类科目代码
							filecontent.append(getString(subdto.getSecnomicsubjectcode())+split);//经济类科目代码
							filecontent.append(getString(String.valueOf(subdto.getFamt()))+split);//交易金额
							filecontent.append(getString(subdto.getSacctprop())+split);//账户性质
							filecontent.append(getString(subdto.getSexpfuncname())+split);//支出功能科目名称
							filecontent.append(getString(subdto.getSpaysummaryname())+line);//摘要名称
						}
					}
				}
				writeFile(fname,filecontent.toString(),filepathnameMap);
				if(sqlExec!=null)
					sqlExec.closeConnection();
				CallShellUtil.callShellWithRes("chmod 777 "+fname);
			}
			
		} catch (FileOperateException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("写文件出错",e);
		} catch (JAFDatabaseException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("查询出错:"+e.toString(),e);
		} catch (Exception e) {
			throw new ITFEBizException("文件赋权出错:chmod 777 "+fname+e.toString(),e);
		}finally
		{
			if(sqlExec!=null)
				sqlExec.closeConnection();
		}
		return dataList!=null&&dataList.size()>0?dataList.size():0;
	}
	public int exportCommApplyPayBackData(IDto dto,String where,Map filepathnameMap) throws ITFEBizException//商行退款导出
	{
		TvPayreckBankBackDto finddto = (TvPayreckBankBackDto)dto;
		List<IDto> dataList=null;
		String fname = null;
		if(where!=null&&where.indexOf(" S_STATUS")>=0)
			fname = getFilepath()+finddto.getDentrustdate()+"_2302_shanghengtuikuan_tcbsreturn.csv";
		else
			fname = getFilepath()+finddto.getDentrustdate()+"_2302_shanghengtuikuan_all.csv";
		StringBuffer filecontent = null;
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.setMaxRows(50000);
			StringBuffer mainsql = new StringBuffer("select * from TV_PAYRECK_BANK_BACK where D_ENTRUSTDATE=? ");
			sqlExec.addParam(finddto.getDentrustdate());
			if(where!=null&&where.contains(" S_STATUS"))
			{
				mainsql.append("AND (S_STATUS=? or S_STATUS=?)");
				sqlExec.addParam("80000");
				sqlExec.addParam("80001");
			}
			mainsql.append(" union select * from HTV_PAYRECK_BANK_BACK where D_ENTRUSTDATE=? ");
			sqlExec.addParam(finddto.getDentrustdate());
			if(where!=null&&where.contains(" S_STATUS"))
			{
				mainsql.append("AND (S_STATUS=? or S_STATUS=?)");
				sqlExec.addParam("80000");
				sqlExec.addParam("80001");
			}
			dataList = (List<IDto>)sqlExec.runQuery(mainsql.toString(),TvPayreckBankBackDto.class).getDtoCollection();
			sqlExec.setMaxRows(50000);
			if(dataList!=null&&dataList.size()>0)
			{
				filecontent = new StringBuffer("");
				TvPayreckBankBackDto maindto = null;
				String sql = " select * from TV_PAYRECK_BANK_BACK_LIST where I_VOUSRLNO in( "+StringUtil.replace(mainsql.toString(), "*", "I_VOUSRLNO")+")";
				sqlExec.addParam(finddto.getDentrustdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				sqlExec.addParam(finddto.getDentrustdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				List<TvPayreckBankBackListDto> subList = new ArrayList<TvPayreckBankBackListDto>();
				List<TvPayreckBankBackListDto> templist = null;
				templist = (List<TvPayreckBankBackListDto>)sqlExec.runQuery(sql, TvPayreckBankBackListDto.class).getDtoCollection();
				sqlExec.setMaxRows(50000);
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				sql = " select * from HTV_PAYRECK_BANK_BACK_LIST where I_VOUSRLNO in( "+StringUtil.replace(mainsql.toString(), "*", "I_VOUSRLNO")+")";
				sqlExec.addParam(finddto.getDentrustdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				sqlExec.addParam(finddto.getDentrustdate());
				if(where!=null&&where.contains(" S_STATUS"))
				{
					sqlExec.addParam("80000");
					sqlExec.addParam("80001");
				}
				templist = (List<TvPayreckBankBackListDto>)sqlExec.runQuery(sql, TvPayreckBankBackListDto.class).getDtoCollection();
				if(templist!=null&&templist.size()>0)
					subList.addAll(templist);
				Map<String,List<TvPayreckBankBackListDto>> subMap = new HashMap<String,List<TvPayreckBankBackListDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<TvPayreckBankBackListDto> tempList = null;
					for(TvPayreckBankBackListDto subdto:subList)
					{
						tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
						if(tempList==null)
						{
							tempList = new ArrayList<TvPayreckBankBackListDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}
					}
				}
				filecontent.append("主信息:文件名称,委托日期,包流水号,代理银行行号,代理银行名称,凭证编号,凭证日期,财政机关代码,国库代码,收款人账号,收款人名称,原委托日期,原凭证日期,原凭证编号,");
				filecontent.append("付款人账号,付款人名称,代理银行代码,代理银行名称,原交易流水号,所属年度,财政机关代码,附言,预算种类,整理期标志,金额,支付交易序号,明细条数,支付报文编号,");
				filecontent.append("支付委托日期,预算类型代码,预算类型名称,资金性质代码,资金性质名称,支付方式代码,支付方式名称,支付发起行行号,申请划款凭证,交易状态,代理银行名称"+line);
				filecontent.append("明细信息:原凭证日期,原凭证编号,预算单位代码,预算单位名称,功能科目代码,功能科目名称,经济类科目代码,交易金额,账户性质,摘要名称"+line);
				for(int i=0;i<dataList.size();i++)
				{
					maindto = (TvPayreckBankBackDto)dataList.get(i);
					if(maindto.getSfilename()!=null&&maindto.getSfilename().contains("/"))
						filecontent.append(maindto.getSfilename().substring(maindto.getSfilename().lastIndexOf("/")+1)+split);//文件名称
					else
						filecontent.append(getString(maindto.getSfilename())+split);//文件名称
					filecontent.append(getString(String.valueOf(maindto.getDentrustdate()))+split);//委托日期
					filecontent.append(getString(maindto.getSpackno())+split);//包流水号
					filecontent.append(getString(maindto.getSagentbnkcode())+split);//代理银行行号
					filecontent.append(getString(maindto.getSagentacctbankname())+split);//代理银行名称
					filecontent.append(getString(maindto.getSvouno())+split);//凭证编号
					filecontent.append(getString(String.valueOf(maindto.getDvoudate()))+split);//凭证日期
					filecontent.append(getString(maindto.getSfinorgcode())+split);//财政机关代码
					filecontent.append(getString(maindto.getStrecode())+split);//国库代码
					filecontent.append(getString(maindto.getSpayeeacct())+split);//收款人账号
					filecontent.append(getString(maindto.getSpayeename())+split);//收款人名称
					filecontent.append(getString(String.valueOf(maindto.getDorientrustdate()))+split);//原委托日期
					filecontent.append(getString(String.valueOf(maindto.getDorivoudate()))+split);//原凭证日期
					filecontent.append(getString(maindto.getSorivouno())+split);//原凭证编号
					filecontent.append(getString(maindto.getSpayeracct())+split);//付款人账号
					filecontent.append(getString(maindto.getSpayername())+split);//付款人名称
					filecontent.append(getString(maindto.getSagentbnkcode())+split);//代理银行代码
					filecontent.append(getString(maindto.getSagentacctbankname())+split);//代理银行名称
					filecontent.append(getString(maindto.getSoritrano())+split);//原交易流水号
					filecontent.append(getString(maindto.getSofyear())+split); //所属年度
					filecontent.append(getString(maindto.getSfinorgcode())+split); //财政机关代码
					filecontent.append(getString(maindto.getSaddword())+split); //附言
					filecontent.append(getString(maindto.getSbudgettype())+split); //预算种类
					filecontent.append(getString(maindto.getStrimsign())+split); //整理期标志
					filecontent.append(getString(String.valueOf(maindto.getFamt()))+split); //金额
					filecontent.append(getString(maindto.getSpaydictateno())+split); //支付交易序号
					filecontent.append(getString(String.valueOf(maindto.getIstatinfnum()))+split);//明细条数
					filecontent.append(getString(maindto.getSpaymsgno())+split);//支付报文编号
					filecontent.append(getString(String.valueOf(maindto.getDpayentrustdate()))+split);//支付委托日期
					filecontent.append(getString(maindto.getSbgttypecode())+split);//预算类型代码
					filecontent.append(getString(maindto.getSbgttypename())+split);//预算类型名称
					filecontent.append(getString(maindto.getSfundtypecode())+split);//资金性质代码
					filecontent.append(getString(maindto.getSfundtypename())+split);//资金性质名称
					filecontent.append(getString(maindto.getSpaytypecode())+split);//支付方式代码
					filecontent.append(getString(maindto.getSpaytypename())+split);//支付方式名称
					filecontent.append(getString(maindto.getSxpaysndbnkno())+split);//支付发起行行号
					filecontent.append(getString(maindto.getSid())+split);//申请划款凭证
					filecontent.append(getString(maindto.getSstatus())+split);//交易状态
					filecontent.append(getString(maindto.getSpaybankname())+line);//代理银行名称
					subList = subMap.get(String.valueOf(maindto.getIvousrlno()));
					if(subList!=null&&subList.size()>0)
					{
						for(TvPayreckBankBackListDto subdto:subList)
						{
							filecontent.append("	");
							filecontent.append(getString(String.valueOf(subdto.getDorivoudate()))+split);//原凭证日期
							filecontent.append(getString(subdto.getSorivouno())+split);//原凭证编号
							filecontent.append(getString(subdto.getSbdgorgcode())+split);//预算单位代码
							filecontent.append(getString(subdto.getSsupdepname())+split);//预算单位名称
							filecontent.append(getString(subdto.getSfuncbdgsbtcode())+split);//功能科目代码
							filecontent.append(getString(subdto.getSexpfuncname())+split);//功能科目名称
							filecontent.append(getString(subdto.getSecnomicsubjectcode())+split);//经济类科目代码
							filecontent.append(getString(String.valueOf(subdto.getFamt()))+split);//交易金额
							filecontent.append(getString(subdto.getSacctprop())+split);//账户性质
							filecontent.append(getString(subdto.getSpaysummaryname())+line);//摘要名称
						}
					}
				}
				writeFile(fname,filecontent.toString(),filepathnameMap);
				if(sqlExec!=null)
					sqlExec.closeConnection();
				CallShellUtil.callShellWithRes("chmod 777 "+fname);
			}
			
		} catch (FileOperateException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("写文件出错",e);
		} catch (JAFDatabaseException e) {
			if(sqlExec!=null)
				sqlExec.closeConnection();
			throw new ITFEBizException("查询出错:"+e.toString(),e);
		} catch (Exception e) {
			throw new ITFEBizException("文件赋权出错:chmod 777"+fname+e.toString(),e);
		} finally
		{
			if(sqlExec!=null)
				sqlExec.closeConnection();
		}
		return dataList!=null&&dataList.size()>0?dataList.size():0;
	}
	private boolean isWin()
	 {
			String osName = System.getProperty("os.name");
			if (osName.indexOf("Windows") >= 0) {
				return true;
			} else {
				return false;
			}
	}
	public  List<String> getFileList(String filePath){
		File tmp1 = new File(filePath);
		String str = tmp1.getAbsolutePath();
		File file = new File(str);
		List<String> filesStr = new ArrayList<String>(); 
		if (file.isFile()) {
			filesStr.add(str);
		} else {
			File[] f = file.listFiles();
			if (f == null || f.length <= 0) {
				filesStr.add(str);
			}else
			{
				for (int i = 0; i < f.length; i++) {
					File tmp = f[i];
					String strF = tmp.getAbsolutePath();
					if (tmp.isFile()&&(tmp.getName().startsWith("3143_")||tmp.getName().startsWith("3144_")))
						filesStr.add(strF);
				}
			}
		}
		return filesStr;
	}
	// 复制文件
    private void copyFile(String sourceFile, String targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
        	File souFile = new File(sourceFile);
        	File tarFile = new File(targetFile);
        	if(!tarFile.getParentFile().exists())
        		tarFile.getParentFile().mkdirs();
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(souFile));
            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(tarFile));
            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }
	public String getFilepath() {
		if(filepath==null||"".equals(filepath))
		{
			if(isWin())
				filepath = "D:/itfe/kbftp/";
			else
				filepath = "/itfe/kbftp/";
		}
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	private void writeFile(String fname,String fileContent,Map filepathnameMap) throws FileOperateException
	{
		if(fname!=null&&!"".equals(fname)&&fileContent!=null&&!"".equals(fileContent))
		{
			if(filepathnameMap!=null&&filepathnameMap.get("fileList")!=null)
			{
				String newfilename = TimeFacade.getCurrentStringTime()+File.separator+StringUtil.replace(fname, getFilepath(), "");//"ITFEDATA"+File.separator
				File f = new File(ITFECommonConstant.FILE_ROOT_PATH+newfilename);
				if (f.exists()) {
					FileUtil.getInstance().deleteFiles(ITFECommonConstant.FILE_ROOT_PATH+newfilename);
				}
				FileUtil.getInstance().writeFile(ITFECommonConstant.FILE_ROOT_PATH+newfilename,fileContent);
				((List)filepathnameMap.get("fileList")).add(newfilename);
			}else
			{
				File f = new File(fname);
				if (f.exists()) {
					FileUtil.getInstance().deleteFiles(fname);
				}
				FileUtil.getInstance().writeFile(fname,fileContent);
			}
		}
	}
	private String getString(String getString)
	{
		if(getString==null||"".equals(getString)||"null".equals(getString)||"NULL".equals(getString))
			return "";
		else
			return getString;
	}
	public String recvLogCopyFile(String copydate,String msgno) throws ITFEBizException
	{
		TvRecvlogDto recvlog = new TvRecvlogDto();
		recvlog.setSdate(copydate);
		recvlog.setSoperationtypecode(msgno);
		List<TvRecvlogDto> fileList = null;
		String filepath = null;
		try {
			fileList = CommonFacade.getODB().findRsByDto(recvlog);
			if(fileList!=null&&fileList.size()>0)
			{
				for(TvRecvlogDto tempdto:fileList)
				{
					filepath = tempdto.getStitle();
					if(filepath!=null&&!"".equals(filepath))
					{
						copyFile(filepath,getFilepath()+copydate+"/"+filepath.substring(filepath.indexOf(msgno+"_"),filepath.length()));
					}
				}
				CallShellUtil.callShellWithRes("chmod -R 777 "+getFilepath()+copydate+"/");
				return String.valueOf(fileList.size());
			}
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException("广西导出业务数据到ftp目录下查询接收日志中报文出错-"+copydate+"-"+msgno,e);
		} catch (ValidateException e) {
			throw new ITFEBizException("广西导出业务数据到ftp目录下查询接收日志中报文出错-"+copydate+"-"+msgno,e);
		} catch (IOException e) {
			throw new ITFEBizException("广西导出业务数据到ftp目录下拷贝文件出错-"+copydate+"-"+msgno,e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0";
	}
	public static void main(String args[]) throws IOException
	{
		String s="你在哪里啊ds";
		String ls =  new String(s.getBytes("GBK"),"iso-8859-1");
		System.out.println("================"+s.length());
		System.out.println("================"+ls.length());
//		List<String> fileList = getFileList("E:/fujian/2014082873071713030700000");
//		if(fileList!=null)
//		{
//			for(int i=0;i<fileList.size();i++)
//			{
//				String tempString = fileList.get(i);
//				System.out.println(tempString.indexOf("3143_")>0?tempString.substring(tempString.indexOf("3143_"),tempString.length()):tempString.substring(tempString.indexOf("3144_"),tempString.length()));
//				copyFile(tempString,"E:/fujian/20140829/"+(tempString.indexOf("3143_")>0?tempString.substring(tempString.indexOf("3143_"),tempString.length()):tempString.substring(tempString.indexOf("3144_"),tempString.length())));
//			}
//		}
	}
}

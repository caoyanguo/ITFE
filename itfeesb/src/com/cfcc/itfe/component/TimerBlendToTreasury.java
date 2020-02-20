package com.cfcc.itfe.component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.StatusOfReportDownLoad;
import com.cfcc.itfe.persistence.dto.TdDownloadReportCheckDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeDetailDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

public class TimerBlendToTreasury implements Callable {

	private static Log log = LogFactory.getLog(TimerBlendToTreasury.class);
	private static String datadate = null;
	@SuppressWarnings("unchecked")
	public Object onCall(MuleEventContext eventContext) throws Exception {
		SQLExecutor execDetail = null;
		try {
			String beforDate = (datadate==null||"".equals(datadate))?TimeFacade.getCurrentStringTimebefor():new String(datadate);
			boolean iscount = false;
			log.info(datadate+"=======山西需求 每日中午和晚上分两次自动把上一天未勾兑的数据，自动勾兑入库，开始勾兑入库======="+beforDate);
			execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String sql = "select * from TV_FIN_INCOME_DETAIL where S_INTREDATE=?";
			execDetail.addParam(beforDate);
			SQLResults rs = execDetail.runQuery(sql,TvFinIncomeDetailDto.class);//周五的电子税票下周一下发以3129电子税票判断为准select * from HTR_TAXORG_PAYOUT_REPORT where s_rptdate='"+beforDate+"' union 
			if(rs.getDtoCollection()==null||rs.getDtoCollection().size()<=0)
			{
				if(datadate==null||"".equals(datadate))
					datadate = beforDate;//得到没有数据的日期
				execDetail.clearParams();
				sql = "select * from TV_FIN_INCOME_DETAIL where S_INTREDATE=?";
				execDetail.addParam(TimeFacade.getCurrentStringTimebefor());
				rs = execDetail.runQuery(sql,TvFinIncomeDetailDto.class);
				if(rs.getDtoCollection()!=null&&rs.getDtoCollection().size()>0)
				{
					iscount=true;
					datadate = TimeFacade.getCurrentStringTimebefor();
					beforDate = TimeFacade.getCurrentStringTimebefor();
				}
			}
			else
			{
				iscount=true;
				datadate = beforDate;
			}
			if(iscount)
			{
				Map<String,TsOrganDto> orgmap = SrvCacheFacade.cacheOrgInfo();
				Set<String> setkeys = orgmap.keySet();
				TsOrganDto tempdto = null;
				StringBuffer yh = null;
				StringBuffer insertsql = null;
				Map<String,StatusOfReportDownLoad> treMap = searchStatusOfReportDownLoad(datadate);
				sql = "select * from TS_TREASURY";
				execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				List<TsTreasuryDto> tList=(List)execDetail.runQuery(sql,TsTreasuryDto.class).getDtoCollection();
				Map<String,List<TsTreasuryDto>> tremap = new HashMap<String,List<TsTreasuryDto>>();
				if(tList!=null&&tList.size()>0)
				{
					for(TsTreasuryDto trdto:tList)
					{
						if(tremap.get(trdto.getSorgcode())==null)
						{
							List<TsTreasuryDto> maplist = new ArrayList<TsTreasuryDto>();
							maplist.add(trdto);
							tremap.put(trdto.getSorgcode(), maplist);
						}else
						{
							tremap.get(trdto.getSorgcode()).add(trdto);
						}
					}
				}
				for(String string:setkeys)
				{
					yh = new StringBuffer("");
					tempdto = orgmap.get(string);
					execDetail.clearParams();
					tList=tremap.get(tempdto.getSorgcode());
					List<TsTreasuryDto> newtList = new ArrayList<TsTreasuryDto>();
					StatusOfReportDownLoad tempdl = null;
					if(tList!=null&&tList.size()>0)
					{
						try{
						
							TsTreasuryDto tDto = null;
							for(int i=0;i<tList.size();i++)
							{
								tDto = (TsTreasuryDto)tList.get(i);
								tempdl = treMap.get(tDto.getStrecode());
								if(tempdl!=null&&tempdl.getSliushui()!=null&&"1".equals(tempdl.getSliushui())&&(tempdl.getSext1()==null||"".equals(tempdl.getSext1())||"0".equals(tempdl.getSext1())))
								{
									if(i==0)
									{
										yh.append(" (b.S_TRECODE=? ");
										execDetail.addParam(tDto.getStrecode());
										newtList.add(tDto);
									}else
									{
										if(yh.length()>5)
											yh.append(" or b.S_TRECODE=? ");
										else
											yh.append(" (b.S_TRECODE=? ");
										execDetail.addParam(tDto.getStrecode());
										newtList.add(tDto);
									}
								}
							}
							if(newtList==null||newtList.size()<=0)
								continue;
							yh.append(")");
							insertsql = new StringBuffer("insert into TV_INCOMEONLINE_INCOMEONDETAIL_BLEND(");
							insertsql.append("S_SEQ,S_FINORGCODE,S_APPLYDATE,S_INTREDATE,S_BLEND,S_PACKNO,S_TRECODE,S_TRENAME,S_TAXORGCODE,S_PAYBNKNO,S_TRANO,S_ORIMSGNO,F_TRAAMT,");
							insertsql.append("F_AMT,S_PAYEROPNBNKNO,S_PAYEROPBKNAME,S_HANDORGNAME,S_PAYACCT,S_TAXVOUNO,S_BILLDATE,S_TAXPAYCODE,S_TAXPAYNAME,S_BUDGETTYPE,S_TRIMFLAG,");
							insertsql.append("S_ETPCODE,S_ETPNAME,S_ETPTYPE,S_BDGSBTCODE,S_BDGSBTNAME,S_LIMIT,S_TAXTYPECODE,S_TAXKINDNAME,S_BDGLEVEL,S_BDGLEVELNAME,S_TAXSTARTDATE,");
							insertsql.append("S_TAXENDDATE,S_ASTFLAG,S_TAXTYPE,S_ACCT,S_TRASTATE,S_INPUTERID,TS_SYSUPDATE,S_VOUCHANNEL,S_STATUS,S_REMARK,S_REMARK1,S_REMARK2,S_REMARK3)");
							insertsql.append(" (select ");//year(CURRENT date)||month(CURRENT date)||day(CURRENT date)
							insertsql.append(" b.S_SEQ,a.S_ORGCODE,a.S_APPLYDATE,b.S_INTREDATE,b.S_EXPVOUTYPE,a.S_PACKNO,a.S_TRECODE,a.S_TRENAME,a.S_TAXORGCODE,a.S_PAYBNKNO,a.S_TRANO,a.S_ORIMSGNO,a.F_TRAAMT,");
							insertsql.append("b.F_AMT,a.S_PAYEROPNBNKNO,a.PAYEROPBKNAME,a.S_HANDORGNAME,a.S_PAYACCT,a.S_TAXVOUNO,a.S_BILLDATE,a.S_TAXPAYCODE,a.S_TAXPAYNAME,a.c_BUDGETTYPE,a.c_TRIMFLAG,");
							insertsql.append("a.S_ETPCODE,a.S_ETPNAME,a.S_ETPTYPE,a.S_BDGSBTCODE,a.S_BDGSBTNAME,a.S_LIMIT,a.S_TAXTYPECODE,a.S_TAXKINDNAME,a.c_BDGLEVEL,a.c_BDGLEVELNAME,a.S_TAXSTARTDATE,");
							insertsql.append("a.S_TAXENDDATE,a.S_ASTFLAG,a.c_TAXTYPE,a.S_ACCT,a.S_TRASTATE,a.S_INPUTERID,CURRENT TIMESTAMP,b.C_VOUCHANNEL,a.S_TRASTATE,a.S_REMARK,a.S_REMARK1,a.S_REMARK2,'定时勾兑入库'");
							insertsql.append(" from TV_FIN_INCOMEONLINE a,TV_FIN_INCOME_DETAIL b where b.S_INTREDATE='"+datadate+"' and"+yh.toString());
							insertsql.append(" and a.S_TRECODE=b.S_TRECODE and  a.S_TRASTATE='20' and a.S_TAXVOUNO=b.S_EXPVOUNO and a.S_ORGCODE=b.S_ORGCODE and a.S_TAXORGCODE=b.S_TAXORGCODE and a.S_BDGSBTCODE=b.S_BDGSBTCODE ");
							StringBuffer blendsql = new StringBuffer(" and b.S_SEQ not in(select S_SEQ from TV_INCOMEONLINE_INCOMEONDETAIL_BLEND where S_INTREDATE='"+datadate+"' and "+yh.toString().replace("b.", "")+") ");
							for(int i=0;i<newtList.size();i++)
							{
								tDto = (TsTreasuryDto)newtList.get(i);
									execDetail.addParam(tDto.getStrecode());
							}
							execDetail.runQuery(insertsql.toString()+blendsql+")");
							insertsql = new StringBuffer("insert into TV_INCOMEONLINE_INCOMEONDETAIL_BLEND(");
							insertsql.append("S_SEQ,S_FINORGCODE,S_APPLYDATE,S_INTREDATE,S_BLEND,S_PACKNO,S_TRECODE,S_TRENAME,S_TAXORGCODE,S_PAYBNKNO,S_TRANO,S_ORIMSGNO,F_TRAAMT,");
							insertsql.append("F_AMT,S_PAYEROPNBNKNO,S_PAYEROPBKNAME,S_HANDORGNAME,S_PAYACCT,S_TAXVOUNO,S_BILLDATE,S_TAXPAYCODE,S_TAXPAYNAME,S_BUDGETTYPE,S_TRIMFLAG,");
							insertsql.append("S_ETPCODE,S_ETPNAME,S_ETPTYPE,S_BDGSBTCODE,S_BDGSBTNAME,S_LIMIT,S_TAXTYPECODE,S_TAXKINDNAME,S_BDGLEVEL,S_BDGLEVELNAME,S_TAXSTARTDATE,");
							insertsql.append("S_TAXENDDATE,S_ASTFLAG,S_TAXTYPE,S_ACCT,S_TRASTATE,S_INPUTERID,TS_SYSUPDATE,S_VOUCHANNEL,S_STATUS,S_REMARK,S_REMARK1,S_REMARK2,S_REMARK3)");
							insertsql.append(" (select ");//year(CURRENT date)||month(CURRENT date)||day(CURRENT date)
							insertsql.append(" b.S_SEQ,b.S_ORGCODE,'',b.S_INTREDATE,b.S_EXPVOUTYPE,b.I_PKGSEQNO,b.S_TRECODE,'',b.S_TAXORGCODE,'','','',0,");
							insertsql.append("b.F_AMT,'','','','',b.S_EXPVOUNO,'','','',b.C_BDGKIND,'',");
							insertsql.append("'','','',b.S_BDGSBTCODE,'','','','',b.c_BDGLEVEL,'','',");
							insertsql.append("'','','',year(CURRENT date)||month(CURRENT date)||day(CURRENT date),'','',CURRENT TIMESTAMP,b.C_VOUCHANNEL,'','','','','手工票定时入库'");
							insertsql.append(" from TV_FIN_INCOME_DETAIL b where b.S_INTREDATE='"+datadate+"' and "+yh.toString()+" and b.S_SEQ not in(select S_SEQ from TV_INCOMEONLINE_INCOMEONDETAIL_BLEND");
							insertsql.append(" where S_INTREDATE='"+datadate+"' and "+yh.toString().replace("b.", "")+")");
							for(int i=0;i<newtList.size();i++)
							{
								tDto = (TsTreasuryDto)newtList.get(i);
									execDetail.addParam(tDto.getStrecode());
							}
							for(int i=0;i<newtList.size();i++)
							{
								tDto = (TsTreasuryDto)newtList.get(i);
									execDetail.addParam(tDto.getStrecode());
							}
							execDetail.runQuery(insertsql.toString()+")");
							saveDownloadReportCheck(datadate,newtList);
						}catch(Exception fore)
						{
							continue;
						}
					}
				}
				if(datadate!=null&&datadate.equals(beforDate))
					datadate = null;
				if(execDetail!=null)
					execDetail.closeConnection();
			}
		} catch (Exception e2) {
			datadate = null;
			log.error(e2.getMessage(),e2);
			throw new ITFEBizException("查询信息异常！",e2);
		} finally {
			if(execDetail!=null) {
				execDetail.closeConnection();
			}
		}
		log.info("=======山西前一账务日期电子税票与入库流水定时勾兑完成=======");
		return null;
	}
	/**
	 * 用来比较两个日期之间相隔的天数
	 * @param fDate
	 * @param oDate
	 * @return
	 */
	public int daysOfTwo(String startDate, String endDate) {
		if(startDate==null||"".equals(startDate)||endDate==null||"".equals(endDate))
			return 0;
	     Calendar aCalendar = Calendar.getInstance();
	       aCalendar.setTime(TimeFacade.parseDate(startDate));
	       int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
	       aCalendar.setTime(TimeFacade.parseDate(endDate));
	       int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
	       return Math.abs(day2-day1);
	}
	private void saveDownloadReportCheck(String date,List<TsTreasuryDto> tList)
	{
		if(date==null||tList==null||"".equals(date)||tList.size()<=0)
			return;
		TdDownloadReportCheckDto finddto = new TdDownloadReportCheckDto();
		for(TsTreasuryDto tempdto:tList)
		{
			finddto.setSdates(date);
			finddto.setStrecode(tempdto.getStrecode());
			try {
				TdDownloadReportCheckDto dto = (TdDownloadReportCheckDto)DatabaseFacade.getODB().find(finddto);
				if(dto==null)
				{
					finddto.setSext1("1");
					DatabaseFacade.getODB().create(finddto);
				}else
				{
					if("0".equals(dto.getSext1())||null==dto.getSext1())
					{
						dto.setSext1("1");
						DatabaseFacade.getODB().update(dto);
					}
				}
			} catch (JAFDatabaseException e) {
				log.error("保存下载报表情况检查表失败:"+e.toString());
			}catch(Exception e)
			{
				log.error("保存下载报表情况检查表失败:"+e.toString());
			}
		}
	}
	@SuppressWarnings("unchecked")
	public Map<String,StatusOfReportDownLoad> searchStatusOfReportDownLoad(String searchdate) throws ITFEBizException {
		
		String stringdate = null;
		String tresql = "";
		if(searchdate != null && !searchdate.equals(""))
			stringdate =searchdate;
		else
			stringdate = DateUtil.date2String2(TimeFacade.getCurrentDate());
		TdDownloadReportCheckDto finddto = new TdDownloadReportCheckDto();
		finddto.setSdates(stringdate);
		List<StatusOfReportDownLoad> list = new ArrayList<StatusOfReportDownLoad>();
		List<TdDownloadReportCheckDto> checkList=null;
		try {
			checkList = CommonFacade.getODB().findRsByDtoForWhere(finddto,tresql);
			TsTreasuryDto tredto = new TsTreasuryDto();
			List<TsTreasuryDto> trelist =CommonFacade.getODB().findRsByDtoForWhere(tredto, tresql);
			Map<String, TsTreasuryDto> treNameMap = BusinessFacade.findTreasuryInfo("");
			Set<String> tmpTreSet = new HashSet<String>();
			StatusOfReportDownLoad dto =null;
			if(checkList!=null && checkList.size()>0){
				for(TdDownloadReportCheckDto checkdto: checkList){
					dto = new StatusOfReportDownLoad();
					dto.setSdates(checkdto.getSdates());
					dto.setStrecode(checkdto.getStrecode());
					dto.setStrename(treNameMap.get(dto.getStrecode()).getStrename());
					if(checkdto.getSkucun()!=null&&!"".equals(checkdto.getSkucun())&&!"NULL".equals(checkdto.getSkucun())&&!"null".equals(checkdto.getSkucun()))
						dto.setSkucun(checkdto.getSkucun());
					else
						dto.setSkucun("0");
					if(checkdto.getSliushui()!=null&&!"".equals(checkdto.getSliushui())&&!"NULL".equals(checkdto.getSliushui())&&!"null".equals(checkdto.getSliushui()))
						dto.setSliushui(checkdto.getSliushui());
					else
						dto.setSliushui("0");
					if(checkdto.getSribao()!=null&&!"".equals(checkdto.getSribao())&&!"NULL".equals(checkdto.getSribao())&&!"null".equals(checkdto.getSribao()))
						dto.setSribao(checkdto.getSribao());
					else
						dto.setSribao("0");
					if(checkdto.getSshuipiao()!=null&&!"".equals(checkdto.getSshuipiao())&&!"NULL".equals(checkdto.getSshuipiao())&&!"null".equals(checkdto.getSshuipiao()))
						dto.setSshuipiao(checkdto.getSshuipiao());
					else
						dto.setSshuipiao("0");
					if(checkdto.getSzhichu()!=null&&!"".equals(checkdto.getSzhichu())&&!"NULL".equals(checkdto.getSzhichu())&&!"null".equals(checkdto.getSzhichu()))
						dto.setSzhichu(checkdto.getSext1());
					else
						dto.setSzhichu("0");
					if(checkdto.getSext1()!=null&&!"".equals(checkdto.getSext1())&&!"NULL".equals(checkdto.getSext1())&&!"null".equals(checkdto.getSext1()))
						dto.setSext1(checkdto.getSext1());
					else
						dto.setSext1("0");
					if(checkdto.getSext2()!=null&&!"".equals(checkdto.getSext2())&&!"NULL".equals(checkdto.getSext2())&&!"null".equals(checkdto.getSext2()))
						dto.setSext2(checkdto.getSext2());
					else
						dto.setSext2("0");
					if(checkdto.getSext3()!=null&&!"".equals(checkdto.getSext3())&&!"NULL".equals(checkdto.getSext3())&&!"null".equals(checkdto.getSext3()))
						dto.setSext3(checkdto.getSext3());
					else
						dto.setSext3("0");
					list.add(dto);
					tmpTreSet.add(dto.getStrecode());
				}
			}
			for(TsTreasuryDto tre : trelist){
				if(tmpTreSet.add(tre.getStrecode())){
					StatusOfReportDownLoad srdldto = new StatusOfReportDownLoad();
					srdldto.setSdates(stringdate);
					srdldto.setStrecode(tre.getStrecode());
					srdldto.setStrename(tre.getStrename());
					srdldto.setSribao("0");
					srdldto.setSliushui("0");
					srdldto.setSshuipiao("0");
					srdldto.setSkucun("0");
					srdldto.setSzhichu("0");
					srdldto.setSext1("0");
					srdldto.setSext2("0");
					srdldto.setSext3("0");
					list.add(srdldto);
				}
			}
		} catch (JAFDatabaseException e) {
			log.error("查询下载报表情况检查的时候出现异常!", e);
			throw new ITFEBizException("查询下载报表情况检查的时候出现异常!", e);
		} catch (ValidateException e) {
			log.error("查询下载报表情况检查的时候出现异常!", e);
			throw new ITFEBizException("查询下载报表情况检查的时候出现异常!", e);
		}
		Map<String,StatusOfReportDownLoad> hashMap = new HashMap<String,StatusOfReportDownLoad>();
		if(list!=null&&list.size()>0)
		{
			for(StatusOfReportDownLoad temp:list)
				hashMap.put(temp.getStrecode(), temp);
		}
		return hashMap;
	}
}

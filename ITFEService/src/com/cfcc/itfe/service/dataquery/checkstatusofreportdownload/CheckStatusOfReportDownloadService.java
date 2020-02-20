package com.cfcc.itfe.service.dataquery.checkstatusofreportdownload;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Date;
import org.apache.commons.logging.*;
import com.cfcc.deptone.common.util.CallShellUtil;
import com.cfcc.deptone.common.util.DB2CallShell;
import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.persistence.dto.StatusOfReportDownLoad;
import com.cfcc.itfe.persistence.dto.TdDownloadReportCheckDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.service.dataquery.checkstatusofreportdownload.AbstractCheckStatusOfReportDownloadService;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.ExportBusinessDataCsv;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.BatchRetriever;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author db2admin
 * @time 13-04-19 14:21:47 codecomment:
 */

public class CheckStatusOfReportDownloadService extends
		AbstractCheckStatusOfReportDownloadService {
	private static Log log = LogFactory
			.getLog(CheckStatusOfReportDownloadService.class);

	/**
	 * ��ѯ���ر������
	 * 
	 * @generated
	 * @param searchdate
	 * @param strecode
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public PageResponse searchStatusOfReportDownLoad(Date searchdate,
			String strecode, PageRequest request) throws ITFEBizException {
		final String datestr = DateUtil.date2String2(searchdate);
		if(strecode!=null&&strecode.contains("blend")){
			new Thread(){
				public void run(){
					try {
						blend(datestr);
					} catch (ITFEBizException e) {}
				}
			}.start();
			return null;
		}else if(strecode!=null&&strecode.contains("exportparam"))
		{
			new Thread(){
				public void run(){
					try {
						exportparam();
					} catch (ITFEBizException e) {}
				}
			}.start();
			return null;
		}else if(strecode!=null&&strecode.contains("exportreport"))
		{
			try {
				exportreport();
			} catch (ITFEBizException e) {}
			return null;
		}
		PageResponse response = new PageResponse(request);
		String stringdate = null;
		String tresql = null;
		if(searchdate != null && !searchdate.equals(""))
			stringdate =DateUtil.date2String2(searchdate);
		else
			stringdate = DateUtil.date2String2(TimeFacade.getCurrentDate());
		TdDownloadReportCheckDto finddto = new TdDownloadReportCheckDto();
		finddto.setSdates(stringdate);
		if (strecode != null && !strecode.equals("")) 
			tresql = " and s_trecode in("+strecode+") with ur";
		else
			tresql = "";
		List<TdDownloadReportCheckDto> checkList=null;
		try {
			checkList = CommonFacade.getODB().findRsByDtoForWhere(finddto,tresql);
			TsTreasuryDto tredto = new TsTreasuryDto();
			List<TsTreasuryDto> trelist =CommonFacade.getODB().findRsByDtoForWhere(tredto, tresql);
			Map<String, TsTreasuryDto> treNameMap = BusinessFacade.findTreasuryInfo("");
			Set<String> tmpTreSet = new HashSet<String>();
			List<StatusOfReportDownLoad> list = new ArrayList<StatusOfReportDownLoad>();
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
						dto.setSzhichu(checkdto.getSzhichu());
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
					if(checkdto.getSishaiguan()!=null&&!"".equals(checkdto.getSishaiguan())&&!"NULL".equals(checkdto.getSishaiguan())&&!"null".equals(checkdto.getSishaiguan()))
						dto.setSishaiguan(checkdto.getSishaiguan());
					else
						dto.setSishaiguan("0");
					dto.setItuikucount(checkdto.getItuikucount());
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
					srdldto.setSishaiguan("0");
					srdldto.setItuikucount(0);
					list.add(srdldto);
				}
			}
			response.getData().clear();
			response.setTotalCount(list.size());
			response.setData(list);
		} catch (JAFDatabaseException e) {
			log.error("��ѯ���ر����������ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ���ر����������ʱ������쳣!", e);
		} catch (ValidateException e) {
			log.error("��ѯ���ر����������ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ���ر����������ʱ������쳣!", e);
		}		
		return response;
	}
	@SuppressWarnings("unchecked")
	public PageResponse searchStatusOfReportDownLoadOld(Date searchdate,
			String strecode, PageRequest request) throws ITFEBizException {
		PageResponse response = new PageResponse(request);
		SQLExecutor selectsqlExec = null;
		SQLResults rs = null;
		BatchRetriever retriever = null;
		List<StatusOfReportDownLoad> list = new ArrayList<StatusOfReportDownLoad>();
		String stringdate = null;
		if(searchdate != null && !searchdate.equals(""))
			stringdate =DateUtil.date2String2(searchdate);
		else
			stringdate = DateUtil.date2String2(TimeFacade.getCurrentDate());
		try {
			selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			StringBuffer sql = new StringBuffer();
			//sql.append("SELECT (CASE WHEN m.dates IS NULL THEN n.S_RPTDATE WHEN m.dates IS NOT NULL THEN m.dates END ) AS s_dates,m.shuipiao AS s_shuipiao,m.liushui AS s_liushui,n.S_TRECODE AS s_ribao FROM ((SELECT (CASE WHEN a.S_BILLDATE IS NULL THEN b.S_INTREDATE WHEN a.S_BILLDATE IS NOT NULL THEN a.S_BILLDATE END ) AS dates,a.S_TRECODE AS shuipiao,b.S_TRECODE AS liushui FROM (Select  S_BILLDATE,  S_TRECODE from TV_FIN_INCOMEONLINE WHERE S_TRECODE IN (SELECT S_TRECODE FROM TS_TREASURY  WHERE S_ORGCODE =? ) group by S_BILLDATE, S_TRECODE order by  S_BILLDATE,  S_TRECODE) AS a FULL JOIN (Select S_INTREDATE,  S_TRECODE from TV_FIN_INCOME_DETAIL WHERE S_TRECODE IN (SELECT S_TRECODE FROM TS_TREASURY  WHERE S_ORGCODE =? ) group by S_INTREDATE, S_TRECODE order by S_INTREDATE,  S_TRECODE) AS b ON a.S_BILLDATE=b.S_INTREDATE AND a.S_TRECODE=b.S_TRECODE) AS m FULL JOIN (Select S_RPTDATE, S_TRECODE FROM TR_INCOMEDAYRPT WHERE S_TRECODE IN (SELECT S_TRECODE FROM TS_TREASURY  WHERE S_ORGCODE =? ) group by S_RPTDATE, S_TRECODE order by S_RPTDATE, S_TRECODE) AS n ON m.dates=n.S_RPTDATE AND (m.shuipiao=n.S_TRECODE OR n.S_TRECODE=m.liushui)) WHERE 1=1 ");
//			sql.append("SELECT (CASE WHEN m.dates IS NULL THEN n.S_RPTDATE WHEN m.dates IS NOT NULL THEN m.dates END ) AS s_dates,m.shuipiao AS s_shuipiao,m.liushui AS s_liushui,n.S_TRECODE AS s_ribao FROM ((SELECT (CASE WHEN a.S_BILLDATE IS NULL THEN b.S_INTREDATE WHEN a.S_BILLDATE IS NOT NULL THEN a.S_BILLDATE END ) AS dates,a.S_TRECODE AS shuipiao,b.S_TRECODE AS liushui FROM (Select  S_BILLDATE,  S_TRECODE from (Select  S_BILLDATE,  S_TRECODE from TV_FIN_INCOMEONLINE union Select  S_BILLDATE,  S_TRECODE from HTV_FIN_INCOMEONLINE) WHERE S_TRECODE IN (SELECT S_TRECODE FROM TS_TREASURY) group by S_BILLDATE, S_TRECODE order by  S_BILLDATE,  S_TRECODE) AS a FULL JOIN (Select S_INTREDATE,  S_TRECODE from (Select S_INTREDATE,  S_TRECODE from TV_FIN_INCOME_DETAIL union Select S_INTREDATE,  S_TRECODE from HTV_FIN_INCOME_DETAIL) WHERE S_TRECODE IN (SELECT S_TRECODE FROM TS_TREASURY) group by S_INTREDATE, S_TRECODE order by S_INTREDATE,  S_TRECODE) AS b ON a.S_BILLDATE=b.S_INTREDATE AND a.S_TRECODE=b.S_TRECODE) AS m FULL JOIN (Select S_RPTDATE, S_TRECODE FROM (Select S_RPTDATE, S_TRECODE FROM TR_INCOMEDAYRPT union Select S_RPTDATE, S_TRECODE FROM HTR_INCOMEDAYRPT) WHERE S_TRECODE IN (SELECT S_TRECODE FROM TS_TREASURY) group by S_RPTDATE, S_TRECODE order by S_RPTDATE, S_TRECODE) AS n ON m.dates=n.S_RPTDATE AND (m.shuipiao=n.S_TRECODE OR n.S_TRECODE=m.liushui)) WHERE 1=1 ");
//			selectsqlExec.addParam(this.getLoginInfo().getSorgcode());
//			selectsqlExec.addParam(this.getLoginInfo().getSorgcode());
//			selectsqlExec.addParam(this.getLoginInfo().getSorgcode());
			sql.append("SELECT (CASE WHEN rs.dates IS NULL THEN zc.S_RPTDATE WHEN rs.dates IS NOT NULL THEN rs.dates END ) AS s_dates,rs.shuipiao AS ");
			sql.append("s_shuipiao,rs.liushui AS s_liushui,rs.ribao AS S_RIBAO,rs.kucun AS S_KUCUN, zc.S_TRECODE AS s_zhichu FROM (");
			sql.append("(SELECT (CASE WHEN xf.dates IS NULL THEN kc.S_RPTDATE WHEN xf.dates IS NOT NULL THEN xf.dates END ) AS dates,xf.shuipiao ");
			sql.append(",xf.liushui,xf.ribao,kc.S_TRECODE AS kucun FROM (SELECT (CASE WHEN m.dates IS NULL THEN n.S_RPTDATE WHEN m.dates IS NOT NULL THEN m.dates END ) AS dates,m.shuipiao,");
			sql.append("m.liushui,n.S_TRECODE AS ribao FROM (SELECT (CASE WHEN a.S_APPLYDATE IS NULL THEN b.S_INTREDATE WHEN ");
			sql.append("a.S_APPLYDATE IS NOT NULL THEN a.S_APPLYDATE END ) AS dates,a.S_TRECODE AS shuipiao,b.S_TRECODE AS liushui FROM ");
			sql.append("(Select  S_APPLYDATE,  S_TRECODE from (");
			sql.append("Select  S_APPLYDATE,  S_TRECODE from TV_FIN_INCOMEONLINE union Select  S_APPLYDATE,");  
			sql.append("S_TRECODE from HTV_FIN_INCOMEONLINE) WHERE S_TRECODE IN (SELECT S_TRECODE FROM TS_TREASURY) group by S_APPLYDATE, S_TRECODE ");
			sql.append("order by  S_APPLYDATE,  S_TRECODE) AS a ");
			sql.append(" FULL JOIN (Select S_INTREDATE,  S_TRECODE from (Select S_INTREDATE,  S_TRECODE from ");
			sql.append("TV_FIN_INCOME_DETAIL union Select S_INTREDATE,  S_TRECODE from HTV_FIN_INCOME_DETAIL) WHERE S_TRECODE IN (SELECT S_TRECODE ");
			sql.append(" FROM TS_TREASURY) group by S_INTREDATE, S_TRECODE order by S_INTREDATE,  S_TRECODE) AS b ON a.S_APPLYDATE=b.S_INTREDATE AND "); 
			sql.append("a.S_TRECODE=b.S_TRECODE) AS m ");
			sql.append("FULL JOIN (Select S_RPTDATE, S_TRECODE FROM (Select S_RPTDATE, S_TRECODE FROM TR_INCOMEDAYRPT ");
			sql.append(" union Select S_RPTDATE, S_TRECODE FROM HTR_INCOMEDAYRPT) WHERE S_TRECODE IN (SELECT S_TRECODE FROM TS_TREASURY) group by ");
			sql.append("S_RPTDATE, S_TRECODE order by S_RPTDATE, S_TRECODE) AS n ON m.dates=n.S_RPTDATE AND (m.shuipiao=n.S_TRECODE OR ");
			sql.append("n.S_TRECODE=m.liushui)) AS xf ");
			sql.append("FULL JOIN (Select S_RPTDATE, S_TRECODE FROM (Select S_RPTDATE, S_TRECODE FROM Tr_Stockdayrpt ");
			sql.append(" union Select S_RPTDATE, S_TRECODE FROM HTr_Stockdayrpt) WHERE S_TRECODE IN (SELECT S_TRECODE FROM TS_TREASURY) group by ");
			sql.append("S_RPTDATE, S_TRECODE order by S_RPTDATE, S_TRECODE) AS kc ON xf.dates=kc.S_RPTDATE AND (xf.shuipiao=kc.S_TRECODE OR ");
			sql.append("xf.liushui=kc.S_TRECODE OR xf.ribao=kc.S_TRECODE)) AS rs");
			sql.append(" FULL JOIN (Select S_RPTDATE, S_TRECODE FROM (Select S_RPTDATE, S_TRECODE FROM Tr_Taxorg_Payout_Report ");
			sql.append(" union Select S_RPTDATE, S_TRECODE FROM HTr_Taxorg_Payout_Report) WHERE S_TRECODE IN (SELECT S_TRECODE FROM TS_TREASURY) group by ");
			sql.append("S_RPTDATE, S_TRECODE order by S_RPTDATE, S_TRECODE) AS zc");
			sql.append(" ON rs.dates=zc.S_RPTDATE AND (rs.shuipiao=zc.S_TRECODE OR zc.S_TRECODE=rs.liushui OR zc.s_trecode=rs.ribao)) WHERE 1=1");
			if (searchdate != null && !searchdate.equals("")) {
				sql.append(" AND (rs.dates=? OR zc.S_RPTDATE=?)");
				selectsqlExec.addParam(stringdate);
				selectsqlExec.addParam(stringdate);
			}else{
				sql.append(" AND (rs.dates<=? OR zc.S_RPTDATE<=?)");
				selectsqlExec.addParam(stringdate);
				selectsqlExec.addParam(stringdate);
			}
			//�����������
			if (strecode != null && !strecode.equals("")) {
				//sql.append(" AND ( m.shuipiao=? OR m.liushui=? OR n.S_TRECODE=? )");selectsqlExec.addParam(strecode);selectsqlExec.addParam(strecode);selectsqlExec.addParam(strecode);
				sql.append(" AND ( rs.shuipiao in("+strecode+") OR rs.liushui in("+strecode+")OR rs.ribao in("+strecode+")OR rs.kucun in("+strecode+") OR zc.S_TRECODE in("+strecode+") )");
			}
			sql.append(" ORDER BY s_shuipiao,s_liushui,s_ribao,S_KUCUN,s_zhichu ");
			rs = selectsqlExec.runQueryCloseCon(sql.toString(),StatusOfReportDownLoad.class);

		
		//������ݣ���ӹ����������
		list.addAll(rs.getDtoCollection());
		retriever = DatabaseFacade.getODB().getBatchRetriever(TsTreasuryDto.class);
		String tresql = null;
		if (strecode != null && !strecode.equals("")) 
			tresql = " where s_trecode in("+strecode+") with ur";
		else
			tresql = " where 1=1 with ur";
		List<Object> params = new ArrayList<Object>();
		retriever.runQuery(tresql, params);
		List<TsTreasuryDto> trelist = retriever.getResults();
		retriever.clearConnecton();
		Map<String, TsTreasuryDto> treNameMap = BusinessFacade.findTreasuryInfo("");
		Set<String> tmpTreSet = new HashSet<String>(); 
		if(list!=null && list.size()>0){
			for(StatusOfReportDownLoad dto : list){
				if(dto.getSribao()!=null && !dto.getSribao().equals("")){
					dto.setStrecode(dto.getSribao());
					dto.setSribao("1");
				}else {
					dto.setSribao("0");
				}
				if(dto.getSliushui()!=null && !dto.getSliushui().equals("")){
					dto.setStrecode(dto.getSliushui());
					dto.setSliushui("1");
				}else {
					dto.setSliushui("0");
				}
				if(dto.getSshuipiao()!=null && !dto.getSshuipiao().equals("")){
					dto.setStrecode(dto.getSshuipiao());
					dto.setSshuipiao("1");
				}else{
					dto.setSshuipiao("0");
				}
				if(dto.getSkucun()!=null&&!"".equals(dto.getSkucun()))
				{
					dto.setStrecode(dto.getSkucun());
					dto.setSkucun("1");
				}else
					dto.setSkucun("0");
				if(dto.getSzhichu()!=null&&!"".equals(dto.getSzhichu()))
				{
					dto.setStrecode(dto.getSzhichu());
					dto.setSzhichu("1");
				}else
					dto.setSzhichu("0");
				tmpTreSet.add(dto.getStrecode());
				dto.setStrename(treNameMap.get(dto.getStrecode()).getStrename());
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
				list.add(srdldto);
			}
		}
		response.getData().clear();
		response.setTotalCount(list.size());
		response.setData(list);
		} catch (Exception e) {
			if (null != selectsqlExec) {
				selectsqlExec.closeConnection();
			}
			if(null!=retriever)
				retriever.clearConnecton();
			log.error("��ѯ���ر����������ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ���ر����������ʱ������쳣!", e);
		}finally
		{
			if (null != selectsqlExec) {
				selectsqlExec.closeConnection();
			}
			if(null!=retriever)
				retriever.clearConnecton();
		}
		return response;
	}
	/**
	 * ��������������Ŀ¼,���������ϵͳ��ȡ�ļ���������
	 */
	public void exportToServer(String exportdate) throws ITFEBizException {
		exportReportToCsv(exportdate);
	}
	private void exportReportToCsv(String exportdate) throws ITFEBizException
	{
		try {
			List<String> filelist = new ArrayList<String>(); //����ļ��б�
			String beforDate = null;
			if(exportdate==null||"".equals(exportdate))
				beforDate = TimeFacade.getCurrentStringTimebefor();
			else
				beforDate = exportdate;
			StringBuffer expcontent =new StringBuffer("");
			expcontent.append(PublicSearchFacade.getSqlConnectByProp());
			//String time = TimeFacade.getCurrentStringTime();//TimeFacade.getCurrentStringTime("yyyyMMddHHmmss");
			String abTipsRoot = null;
			if(isWin())
				abTipsRoot = "D:/itfe/kbftp/";
			else
				abTipsRoot = "/itfe/kbftp/";
			String shellpath = abTipsRoot+"tipsexport.sql";
			/*
			 * ��ǰ�����ļ�Ŀ¼������LINUX�ᱨ��
			 */
			FileUtil.getInstance().createDir(abTipsRoot);
			//���ڵõ�ĳ������һ��
			String fname = abTipsRoot+beforDate+"_3128_shoururibao_1.csv"; // 3128�����ձ���
			expcontent.append("export to "+fname+" of del select * from HTR_INCOMEDAYRPT where S_RPTDATE='"+beforDate+"' union select * from TR_INCOMEDAYRPT where S_RPTDATE='"+beforDate+"';\n");
			filelist.add(fname);
			fname = abTipsRoot+beforDate+"_3130_kucunribao_1.csv"; // 3128����ձ���
			expcontent.append("export to "+fname+" of del select * from HTR_STOCKDAYRPT where S_RPTDATE='"+beforDate+"' union select * from TR_STOCKDAYRPT where S_RPTDATE='"+beforDate+"';\n");
			filelist.add(fname);
			fname = abTipsRoot+beforDate+"_3129_dianzishuipiao_1.csv"; // 3129����˰Ʊ
			expcontent.append("export to "+fname+" of del select * from HTV_FIN_INCOMEONLINE where S_APPLYDATE='"+beforDate+"' union select * from TV_FIN_INCOMEONLINE where S_APPLYDATE='"+beforDate+"';\n");
			fname = abTipsRoot+beforDate+"_3139_rukuliushui_1.csv";//3139�����ˮ
			expcontent.append("export to "+fname+" of del select * from HTV_FIN_INCOME_DETAIL where S_INTREDATE='"+beforDate+"' union select * from TV_FIN_INCOME_DETAIL where S_INTREDATE='"+beforDate+"';\n");
			filelist.add(fname);
			fname = abTipsRoot+beforDate+"_3127_yusuanzhichu_1.csv";
			expcontent.append("export to "+fname+" of del select * from HTR_TAXORG_PAYOUT_REPORT where s_rptdate='"+beforDate+"' union select * from TR_TAXORG_PAYOUT_REPORT where s_rptdate='"+beforDate+"';\n");
			filelist.add(fname);
			if(filelist.size() > 0){
				try{
					FileUtil.getInstance().deleteFileForExists(shellpath);
				}catch(Exception e)
				{}
				FileUtil.getInstance().writeFile(shellpath, expcontent.toString());
				CallShellUtil.callShellWithRes("chmod -R 777 "+shellpath);
				byte[] bytes = null;
				String results = null;
				bytes = DB2CallShell.dbCallShellWithRes(shellpath);
				FileUtil.getInstance().writeFile(abTipsRoot+TimeFacade.getCurrentStringTime()+".end", "");
				CallShellUtil.callShellWithRes("chmod -R 777 "+abTipsRoot+beforDate);
//				CallShellUtil.callShellWithRes("chmod 777 "+abTipsRoot+TimeFacade.getCurrentStringTime()+".end");
				if (bytes.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
					results = new String(bytes, 0, MsgConstant.MAX_CALLSHELL_RS * 1024);
				} else {
					results = new String(bytes);
				}
				log.error("�˹������������ļ�����SHELL���:" + results);
			}			
//			this.process3129CSV(filelist);
		} catch (FileOperateException e) {
			log.error("����ʱ���񵼳������ļ��ӱ���ͷ����",e);
			throw new ITFEBizException("����ʱ���񵼳������ļ��ӱ���ͷ����",e);
		} catch (FileNotFoundException e) {
			log.error("����ʱ���񵼳������ļ�û���ҵ�",e);
			throw new ITFEBizException("����ʱ���񵼳������ļ�û���ҵ�",e);
		} catch (Exception e) {
			log.error("����ʱ���񵼳������ļ�����",e);
			throw new ITFEBizException("����ʱ���񵼳������ļ�����",e);
		}
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
	public String exportBusData(String exportdate, String busType)
			throws ITFEBizException {
		if(exportdate == null || "".equals(exportdate))
			exportdate = DateUtil.date2String2(TimeFacade.getCurrentDate());
		StringBuffer result = new StringBuffer();
		String line = System.getProperty("line.separator");//����
		ExportBusinessDataCsv exportDataUtil = new ExportBusinessDataCsv();
		int count = 0;
		if(busType==null||"".equals(busType))
		{
			TvPayoutmsgmainDto payout = new TvPayoutmsgmainDto();//ʵ���ʽ�
			TvDirectpaymsgmainDto directpay = new TvDirectpaymsgmainDto();//ֱ��֧�����
			TvGrantpaymsgmainDto grantpay = new TvGrantpaymsgmainDto();//��Ȩ֧�����
			TvPayreckBankDto payreckbank = new TvPayreckBankDto();//���л���
			TvPayreckBankBackDto payreckbankbank = new TvPayreckBankBackDto();//�����˿�
			payout.setScommitdate(exportdate);		
			directpay.setScommitdate(exportdate);		
			grantpay.setScommitdate(exportdate);		
			payreckbank.setDentrustdate(CommonUtil.strToDate(exportdate));		
			payreckbankbank.setDentrustdate(CommonUtil.strToDate(exportdate));
			count = exportDataUtil.exportPayoutData(payout, "",null);//����ʵ���ʽ���ȫ��״̬����
			result.append("ʵ���ʽ�ҵ�����ݵ���������"+count+line);
			count = exportDataUtil.exportPayoutData(payout, " AND (S_STATUS='80000' or S_STATUS='80001')",null);//����ʵ���ʽ���TCBS��ִ������
			result.append("ʵ���ʽ�ҵ��TCBS�ѻ�ִ���ݵ���������"+count+line);
			count = exportDataUtil.exportDirectPayData(directpay, "",null);//����ֱ��֧����ȵ���ȫ��״̬����
			result.append("ֱ��֧�����ҵ�����ݵ���������"+count+line);
			count = exportDataUtil.exportDirectPayData(directpay, " AND (S_STATUS='80000' or S_STATUS='80001')",null);//����ֱ��֧����ȵ���TCBS��ִ������
			result.append("ֱ��֧�����ҵ��TCBS�ѻ�ִ���ݵ���������"+count+line);
			count = exportDataUtil.exportGrantPayData(grantpay, "",null);//������Ȩ֧����ȵ���ȫ��״̬����
			result.append("��Ȩ֧�����ҵ�����ݵ���������"+count+line);
			count = exportDataUtil.exportGrantPayData(grantpay, " AND (S_STATUS='80000' or S_STATUS='80001')",null);//������Ȩ֧����ȵ���TCBS��ִ������
			result.append("��Ȩ֧�����ҵ��TCBS�ѻ�ִ���ݵ���������"+count+line);
			count = exportDataUtil.exportCommApplyPayData(payreckbank, "",null);//�������л����ȫ��״̬����
			result.append("���л���ҵ�����ݵ���������"+count+line);
			count = exportDataUtil.exportCommApplyPayData(payreckbank, " AND (S_RESULT='80000' or S_RESULT='80001')",null);//�������л����TCBS��ִ������
			result.append("���л���ҵ��TCBS�ѻ�ִ���ݵ���������"+count+line);
			count = exportDataUtil.exportCommApplyPayBackData(payreckbankbank, "",null);//���������˿��ȫ��״̬����
			result.append("�����˿�ҵ�����ݵ���������"+count+line);
			count = exportDataUtil.exportCommApplyPayBackData(payreckbankbank, " AND (S_STATUS='80000' or S_STATUS='80001')",null);//���������˿��TCBS��ִ������
			result.append("�����˿�ҵ��TCBS�ѻ�ִ���ݵ���������"+count+line);
			String scount = exportDataUtil.recvLogCopyFile(exportdate,"3143");//����������־��3143�ļ���ftpĿ¼
			result.append("TCBS��ִ3143�ļ�������"+scount+line);
			scount = exportDataUtil.recvLogCopyFile(exportdate,"3144");//����������־��3144�ļ���ftpĿ¼
			result.append("TCBS��ִ3144�ļ�������"+scount+line);
		}else if(MsgConstant.MSG_NO_5101.equals(busType))
		{
			TvPayoutmsgmainDto payout = new TvPayoutmsgmainDto();//ʵ���ʽ�
			payout.setScommitdate(exportdate);
			count = exportDataUtil.exportPayoutData(payout, "",null);//����ʵ���ʽ���ȫ��״̬����
			result.append("ʵ���ʽ�ҵ�����ݵ���������"+count+line);
			count = exportDataUtil.exportPayoutData(payout, " AND (S_STATUS='80000' or S_STATUS='80001')",null);//����ʵ���ʽ���TCBS��ִ������
			result.append("ʵ���ʽ�ҵ��TCBS�ѻ�ִ���ݵ���������"+count+line);
		}else if(MsgConstant.MSG_NO_5102.equals(busType))
		{
			TvDirectpaymsgmainDto directpay = new TvDirectpaymsgmainDto();//ֱ��֧�����
			directpay.setScommitdate(exportdate);
			count = exportDataUtil.exportDirectPayData(directpay, "",null);//����ֱ��֧����ȵ���ȫ��״̬����
			result.append("ֱ��֧�����ҵ�����ݵ���������"+count+line);
			count = exportDataUtil.exportDirectPayData(directpay, " AND (S_STATUS='80000' or S_STATUS='80001')",null);//����ֱ��֧����ȵ���TCBS��ִ������
			result.append("ֱ��֧�����ҵ��TCBS�ѻ�ִ���ݵ���������"+count+line);
		}else if(MsgConstant.MSG_NO_5103.equals(busType))
		{
			TvGrantpaymsgmainDto grantpay = new TvGrantpaymsgmainDto();//��Ȩ֧�����
			grantpay.setScommitdate(exportdate);
			count = exportDataUtil.exportGrantPayData(grantpay, "",null);//������Ȩ֧����ȵ���ȫ��״̬����
			result.append("��Ȩ֧�����ҵ�����ݵ���������"+count+line);
			count = exportDataUtil.exportGrantPayData(grantpay, " AND (S_STATUS='80000' or S_STATUS='80001')",null);//������Ȩ֧����ȵ���TCBS��ִ������
			result.append("��Ȩ֧�����ҵ��TCBS�ѻ�ִ���ݵ���������"+count+line);
		}else if(MsgConstant.VOUCHER_NO_2301.equals(busType))
		{
			TvPayreckBankDto payreckbank = new TvPayreckBankDto();//���л���
			payreckbank.setDentrustdate(CommonUtil.strToDate(exportdate));
			count = exportDataUtil.exportCommApplyPayData(payreckbank, "",null);//�������л����ȫ��״̬����
			result.append("���л���ҵ�����ݵ���������"+count+line);
			count = exportDataUtil.exportCommApplyPayData(payreckbank, " AND (S_RESULT='80000' or S_RESULT='80001')",null);//�������л����TCBS��ִ������
			result.append("���л���ҵ��TCBS�ѻ�ִ���ݵ���������"+count+line);
			String scount = exportDataUtil.recvLogCopyFile(exportdate,"3143");//����������־��3143�ļ���ftpĿ¼
			result.append("TCBS��ִ3143�ļ�������"+scount+line);
		}else if(MsgConstant.VOUCHER_NO_2302.equals(busType))
		{
			TvPayreckBankBackDto payreckbankbank = new TvPayreckBankBackDto();//�����˿�
			payreckbankbank.setDentrustdate(CommonUtil.strToDate(exportdate));
			count = exportDataUtil.exportCommApplyPayBackData(payreckbankbank, "",null);//���������˿��ȫ��״̬����
			result.append("�����˿�ҵ�����ݵ���������"+count+line);
			count = exportDataUtil.exportCommApplyPayBackData(payreckbankbank, " AND (S_STATUS='80000' or S_STATUS='80001')",null);//���������˿��TCBS��ִ������
			result.append("�����˿�ҵ��TCBS�ѻ�ִ���ݵ���������"+count+line);
			String scount = exportDataUtil.recvLogCopyFile(exportdate,"3144");//����������־��3144�ļ���ftpĿ¼
			result.append("TCBS��ִ3144�ļ�������"+scount+line);
		}
		return result.toString();
	}
	@SuppressWarnings("unchecked")
	public Map downloadbus(String exportdate, String busType)
			throws ITFEBizException {
		Map getMap = new HashMap();
		List<String> filepathname = new ArrayList<String>();
		getMap.put("fileList", filepathname);
		if(exportdate == null || "".equals(exportdate))
			exportdate = DateUtil.date2String2(TimeFacade.getCurrentDate());
		StringBuffer result = new StringBuffer();
		String line = System.getProperty("line.separator");//����
		ExportBusinessDataCsv exportDataUtil = new ExportBusinessDataCsv();
		int count = 0;
		if(busType==null||"".equals(busType))
		{
			TvPayoutmsgmainDto payout = new TvPayoutmsgmainDto();//ʵ���ʽ�
			TvDirectpaymsgmainDto directpay = new TvDirectpaymsgmainDto();//ֱ��֧�����
			TvGrantpaymsgmainDto grantpay = new TvGrantpaymsgmainDto();//��Ȩ֧�����
			TvPayreckBankDto payreckbank = new TvPayreckBankDto();//���л���
			TvPayreckBankBackDto payreckbankbank = new TvPayreckBankBackDto();//�����˿�
			payout.setScommitdate(exportdate);		
			directpay.setScommitdate(exportdate);		
			grantpay.setScommitdate(exportdate);		
			payreckbank.setDentrustdate(CommonUtil.strToDate(exportdate));		
			payreckbankbank.setDentrustdate(CommonUtil.strToDate(exportdate));
			count = exportDataUtil.exportPayoutData(payout, "",getMap);//����ʵ���ʽ���ȫ��״̬����
			result.append("ʵ���ʽ�ҵ�����ݵ���������"+count+line);
			count = exportDataUtil.exportPayoutData(payout, " AND (S_STATUS='80000' or S_STATUS='80001')",getMap);//����ʵ���ʽ���TCBS��ִ������
			result.append("ʵ���ʽ�ҵ��TCBS�ѻ�ִ���ݵ���������"+count+line);
			count = exportDataUtil.exportPayoutBack(payout,getMap);//ʵ���ʽ��˻�
			result.append("ʵ���ʽ��˻����ݵ���������"+count+line);
			count = exportDataUtil.exportDirectPayData(directpay, "",getMap);//����ֱ��֧����ȵ���ȫ��״̬����
			result.append("ֱ��֧�����ҵ�����ݵ���������"+count+line);
			count = exportDataUtil.exportDirectPayData(directpay, " AND (S_STATUS='80000' or S_STATUS='80001')",getMap);//����ֱ��֧����ȵ���TCBS��ִ������
			result.append("ֱ��֧�����ҵ��TCBS�ѻ�ִ���ݵ���������"+count+line);
			count = exportDataUtil.exportGrantPayData(grantpay, "",getMap);//������Ȩ֧����ȵ���ȫ��״̬����
			result.append("��Ȩ֧�����ҵ�����ݵ���������"+count+line);
			count = exportDataUtil.exportGrantPayData(grantpay, " AND (S_STATUS='80000' or S_STATUS='80001')",getMap);//������Ȩ֧����ȵ���TCBS��ִ������
			result.append("��Ȩ֧�����ҵ��TCBS�ѻ�ִ���ݵ���������"+count+line);
			count = exportDataUtil.exportCommApplyPayData(payreckbank, "",getMap);//�������л����ȫ��״̬����
			result.append("���л���ҵ�����ݵ���������"+count+line);
			count = exportDataUtil.exportCommApplyPayData(payreckbank, " AND (S_RESULT='80000' or S_RESULT='80001')",getMap);//�������л����TCBS��ִ������
			result.append("���л���ҵ��TCBS�ѻ�ִ���ݵ���������"+count+line);
			count = exportDataUtil.exportCommApplyPayBackData(payreckbankbank, "",getMap);//���������˿��ȫ��״̬����
			result.append("�����˿�ҵ�����ݵ���������"+count+line);
			count = exportDataUtil.exportCommApplyPayBackData(payreckbankbank, " AND (S_STATUS='80000' or S_STATUS='80001')",getMap);//���������˿��TCBS��ִ������
			result.append("�����˿�ҵ��TCBS�ѻ�ִ���ݵ���������"+count+line);
		}else if(MsgConstant.MSG_NO_5101.equals(busType))
		{
			TvPayoutmsgmainDto payout = new TvPayoutmsgmainDto();//ʵ���ʽ�
			payout.setScommitdate(exportdate);
			count = exportDataUtil.exportPayoutData(payout, "",getMap);//����ʵ���ʽ���ȫ��״̬����
			result.append("ʵ���ʽ�ҵ�����ݵ���������"+count+line);
			count = exportDataUtil.exportPayoutData(payout, " AND (S_STATUS='80000' or S_STATUS='80001')",getMap);//����ʵ���ʽ���TCBS��ִ������
			result.append("ʵ���ʽ�ҵ��TCBS�ѻ�ִ���ݵ���������"+count+line);
		}else if(MsgConstant.MSG_NO_5102.equals(busType))
		{
			TvDirectpaymsgmainDto directpay = new TvDirectpaymsgmainDto();//ֱ��֧�����
			directpay.setScommitdate(exportdate);
			count = exportDataUtil.exportDirectPayData(directpay, "",getMap);//����ֱ��֧����ȵ���ȫ��״̬����
			result.append("ֱ��֧�����ҵ�����ݵ���������"+count+line);
			count = exportDataUtil.exportDirectPayData(directpay, " AND (S_STATUS='80000' or S_STATUS='80001')",getMap);//����ֱ��֧����ȵ���TCBS��ִ������
			result.append("ֱ��֧�����ҵ��TCBS�ѻ�ִ���ݵ���������"+count+line);
		}else if(MsgConstant.MSG_NO_5103.equals(busType))
		{
			TvGrantpaymsgmainDto grantpay = new TvGrantpaymsgmainDto();//��Ȩ֧�����
			grantpay.setScommitdate(exportdate);
			count = exportDataUtil.exportGrantPayData(grantpay, "",getMap);//������Ȩ֧����ȵ���ȫ��״̬����
			result.append("��Ȩ֧�����ҵ�����ݵ���������"+count+line);
			count = exportDataUtil.exportGrantPayData(grantpay, " AND (S_STATUS='80000' or S_STATUS='80001')",getMap);//������Ȩ֧����ȵ���TCBS��ִ������
			result.append("��Ȩ֧�����ҵ��TCBS�ѻ�ִ���ݵ���������"+count+line);
		}else if(MsgConstant.VOUCHER_NO_2301.equals(busType))
		{
			TvPayreckBankDto payreckbank = new TvPayreckBankDto();//���л���
			payreckbank.setDentrustdate(CommonUtil.strToDate(exportdate));
			count = exportDataUtil.exportCommApplyPayData(payreckbank, "",getMap);//�������л����ȫ��״̬����
			result.append("���л���ҵ�����ݵ���������"+count+line);
			count = exportDataUtil.exportCommApplyPayData(payreckbank, " AND (S_RESULT='80000' or S_RESULT='80001')",getMap);//�������л����TCBS��ִ������
			result.append("���л���ҵ��TCBS�ѻ�ִ���ݵ���������"+count+line);
		}else if(MsgConstant.VOUCHER_NO_2302.equals(busType))
		{
			TvPayreckBankBackDto payreckbankbank = new TvPayreckBankBackDto();//�����˿�
			payreckbankbank.setDentrustdate(CommonUtil.strToDate(exportdate));
			count = exportDataUtil.exportCommApplyPayBackData(payreckbankbank, "",getMap);//���������˿��ȫ��״̬����
			result.append("�����˿�ҵ�����ݵ���������"+count+line);
			count = exportDataUtil.exportCommApplyPayBackData(payreckbankbank, " AND (S_STATUS='80000' or S_STATUS='80001')",getMap);//���������˿��TCBS��ִ������
			result.append("�����˿�ҵ��TCBS�ѻ�ִ���ݵ���������"+count+line);
		}else if(MsgConstant.VOUCHER_NO_3208.equals(busType))
		{
			TvPayoutmsgmainDto payout = new TvPayoutmsgmainDto();//ʵ���ʽ��˻�
			count = exportDataUtil.exportPayoutBack(payout,getMap);//ʵ���ʽ��˻�
			result.append("ʵ���ʽ��˻����ݵ���������"+count+line);
		}
		getMap.put("result", result.toString());
		return getMap;//result.toString();
	}
	@SuppressWarnings("unchecked")
	private void blend(String datadate) throws ITFEBizException
	{
		log.info("=====================================================���ر��������鹴�������ÿ�ʼ");
		SQLExecutor execDetail = null;
		try
		{
			Map<String,TsOrganDto> orgmap = SrvCacheFacade.cacheOrgInfo();
			Set<String> setkeys = orgmap.keySet();
			TsOrganDto tempdto = null;
			StringBuffer yh = null;
			StringBuffer insertsql = null;
			String sql = null;
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
				if(execDetail!=null)
					execDetail.clearParams();
				else
					execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
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
						insertsql.append("a.S_TAXENDDATE,a.S_ASTFLAG,a.c_TAXTYPE,a.S_ACCT,a.S_TRASTATE,a.S_INPUTERID,CURRENT TIMESTAMP,b.C_VOUCHANNEL,a.S_TRASTATE,a.S_REMARK,a.S_REMARK1,a.S_REMARK2,'��ʱ�������'");
						insertsql.append(" from TV_FIN_INCOMEONLINE a,TV_FIN_INCOME_DETAIL b where b.S_INTREDATE='"+datadate+"' and"+yh.toString());
						insertsql.append(" and a.S_TRECODE=b.S_TRECODE and a.S_TRASTATE='20' and a.S_TAXVOUNO=b.S_EXPVOUNO and a.S_ORGCODE=b.S_ORGCODE and a.S_TAXORGCODE=b.S_TAXORGCODE and a.S_BDGSBTCODE=b.S_BDGSBTCODE ");
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
						insertsql.append("'','','',year(CURRENT date)||month(CURRENT date)||day(CURRENT date),'','',CURRENT TIMESTAMP,b.C_VOUCHANNEL,'','','','','�ֹ�Ʊ��ʱ���'");
						insertsql.append(" from TV_FIN_INCOME_DETAIL b where b.S_INTREDATE='"+datadate+"' and "+yh.toString());
						insertsql.append(" and b.S_SEQ not in(select S_SEQ from TV_INCOMEONLINE_INCOMEONDETAIL_BLEND where S_INTREDATE='"+datadate+"' and "+yh.toString().replace("b.", "")+")");
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
						log.error("��鹴������쳣:",fore);
						continue;
					}
				}
			}
			if(execDetail!=null)
				execDetail.closeConnection();
		} catch (Exception e2) {
			throw new ITFEBizException("��������쳣��",e2);
		} finally {
			if(execDetail!=null) {
				execDetail.closeConnection();
			}
		}
		log.info("=====================================================���ر��������鹴�������ý���");
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
				log.error("�������ر����������ʧ��:"+e.toString());
			}catch(Exception e)
			{
				log.error("�������ر����������ʧ��:"+e.toString());
			}
		}
	}
	@SuppressWarnings("unchecked")
	public Map<String,StatusOfReportDownLoad> searchStatusOfReportDownLoad(String searchdate) throws ITFEBizException {
		
		String stringdate = searchdate;
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
			log.error("��ѯ���ر����������ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ���ر����������ʱ������쳣!", e);
		} catch (ValidateException e) {
			log.error("��ѯ���ر����������ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ���ر����������ʱ������쳣!", e);
		}
		Map<String,StatusOfReportDownLoad> hashMap = new HashMap<String,StatusOfReportDownLoad>();
		if(list!=null&&list.size()>0)
		{
			for(StatusOfReportDownLoad temp:list)
				hashMap.put(temp.getStrecode(), temp);
		}
		return hashMap;
	}
	private void exportparam() throws ITFEBizException
	{
		try
		{
			String line = System.getProperty("line.separator");//����
			String fname = getFilepath();
			String filename = null;
			File file = new File(fname+TimeFacade.getCurrentStringTime());
			if(!file.exists())
				file.mkdirs();
			
			StringBuffer expcontent =new StringBuffer("");
			expcontent.append(PublicSearchFacade.getSqlConnectByProp()+line);
			expcontent.append("export to "+fname+TimeFacade.getCurrentStringTime()+"/yinheng.del"+" of del select * from Ts_Paybank;"+line);//֧��ϵͳ������
			expcontent.append("export to "+fname+TimeFacade.getCurrentStringTime()+"/yusuankemu.del"+" of del select * from Ts_Budgetsubject ORDER BY S_ORGCODE;"+line);//���ܿ�Ŀ����
			expcontent.append("export to "+fname+TimeFacade.getCurrentStringTime()+"/farendaima.del"+" of del select * from TD_CORP ORDER BY S_BOOKORGCODE;"+line);//���˴���
			expcontent.append("export to "+fname+TimeFacade.getCurrentStringTime()+"/kukuanzhanghu.del"+" of del select * from TS_INFOCONNORGACC ORDER BY S_ORGCODE;"+line);//����˻�
			expcontent.append("export to "+fname+TimeFacade.getCurrentStringTime()+"/caizhengdaima.del"+" of del select * from TS_CONVERTFINORG ORDER BY S_ORGCODE;"+line);//��������
			expcontent.append("export to "+fname+TimeFacade.getCurrentStringTime()+"/zhengshoujiguan.del"+" of del select * from TS_TAXORG ORDER BY S_ORGCODE;"+line);//���ջ��ش���
			filename = fname + "exportparam.sql";
			FileUtil.getInstance().writeFile(filename, expcontent.toString());
			if(!isWin())
				CallShellUtil.callShellWithRes("chmod -R 777 "+filename);
			DB2CallShell.dbCallShellWithRes(filename+" >>"+fname+"/param.log");
			FileUtil.getInstance().deleteFileForExists(filename);
		}catch(Exception e)
		{
			throw new ITFEBizException(e);
		}
	}
	private void exportreport() throws ITFEBizException
	{
		try
		{
			String fname = getFilepath();
			String filename = fname + "exportreport.sh";
			if(isWin())
				CallShellUtil.callShellWithRes(fname.replace(".sh", ".bat"));
			else
				CallShellUtil.callShellWithRes("sh "+filename+" >>"+fname+"/exportreport.log");
		}catch(Exception e)
		{
			throw new ITFEBizException(e);
		}
	}
	public String getFilepath() {
		String filepath = null;
		if(filepath==null||"".equals(filepath))
		{
			if(isWin())
				filepath = "D:/itfeext/czftp/";
			else
				filepath = "/itfeext/czftp/";
		}
		return filepath;
	}
}
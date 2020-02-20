package com.cfcc.itfe.service.dataquery.tipsdataexport;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.CallShellUtil;
import com.cfcc.deptone.common.util.DB2CallShell;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.data.TipsParamDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * @author hua
 * @time 12-05-16 18:43:18 codecomment:
 */

public class TipsDataExportService extends AbstractTipsDataExportService {
	private static Log log = LogFactory.getLog(TipsDataExportService.class);

	/**
	 * 生成导出Tips文件
	 * 
	 * @generated
	 * @param list
	 * @param dto
	 * @param str
	 * @return java.util.Map
	 * @throws ITFEBizException
	 */
	public Map generateTipsToFile(List list, IDto dto, String str)
			throws ITFEBizException {
		List<String> errorlist = new ArrayList<String>();
		String root = ITFECommonConstant.FILE_ROOT_PATH;
		String shellpath = root+"tipsexport.sql";
		String abTipsRoot = root+"tipstemp/";
		List<String> filelist = new ArrayList<String>();
		TipsParamDto paramdto = null;
		Map<String, List<String>> resmap = new HashMap<String, List<String>>();
		if (null != dto) {
			paramdto = (TipsParamDto) dto; // 得到前台传来的查询条件
			if("sendreport".equals(paramdto.getSbankcode()))
			{
				byte[] bytes = null;
				String results = null;
				String commad = "sh /itfe/script/timer/exportbyparam "+paramdto.getStartdate().toString().replace("-", "");
				try {
					bytes = CallShellUtil.callShellWithRes(commad);
				} catch (Exception e) {
					throw new ITFEBizException("重新发送报表失败:",e);
				}
				if (bytes.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
					results = new String(bytes, 0, MsgConstant.MAX_CALLSHELL_RS * 1024);
				} else {
					results = new String(bytes);
				}
				log.error("报表定时任务导出报表文件调用SHELL结果:" + results);	
				return null;
			}
		}
		SQLExecutor sqlExec = null;
		boolean boo = false;
		StringBuffer expBuffer = new StringBuffer("");
		expBuffer.append(PublicSearchFacade.getSqlConnectByProp()); //首先给脚本添加上连接数据库的语句		
		try {
			//得到财政标志参数对照表记录，用于在判断非中心时查询所属国库使用
			TsConvertfinorgDto finddto = new TsConvertfinorgDto();
			List<TsConvertfinorgDto> finddtolist = null;
			finddto.setSorgcode(getLoginInfo().getSorgcode()); // 核算主体代码
			finddtolist = (List<TsConvertfinorgDto>)CommonFacade.getODB().findRsByDtoWithUR(finddto);
			for (Object obj : list) { // 循环需要导出的数据类型
				TdEnumvalueDto typedto = (TdEnumvalueDto) obj;
				if (StateConstant.RecvTips_3128_SR.equals(typedto.getSremark())) { // 3128收入日报表
					sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
					StringBuffer paramsql = new StringBuffer("");
					StringBuffer paramsql2 = new StringBuffer("");
					StringBuffer paramsql3 = new StringBuffer("");
					StringBuffer currenttable = new StringBuffer("");
					paramsql.append("select distinct s_rptdate from HTR_INCOMEDAYRPT where ");
					paramsql2.append(this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
					paramsql3.append(" and S_RPTDATE >= ? and S_RPTDATE <= ? ");
					sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
					sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					{
						currenttable.append(" union select distinct s_rptdate from TR_INCOMEDAYRPT where "+this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
						currenttable.append(" and S_RPTDATE >= ? and S_RPTDATE <= ? ");
						sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
						sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					}
					SQLResults rs = sqlExec.runQueryCloseCon(paramsql.toString()+paramsql2.toString()+paramsql3.toString()+currenttable);
					if(rs.getRowCount() == 0 || "".equals(rs.getString(0, 0))) {
						errorlist.add("3128财政申请收入日报表查询无数据,未导出");
						continue;
					}else {
						List<String> ls = this.acheiveCollFromRS(rs);
						Map<String,List<String>> newMap = this.createShellForExp(ls,paramdto,"", paramsql2.toString(), "", StateConstant.RecvTips_3128_SR, paramdto.getStartdate(), paramdto.getEnddate());
						expBuffer.append(newMap.get("shell").get(0));
						filelist.addAll(newMap.get("files"));
						
					}
				} else if (StateConstant.RecvTips_3128_KC.equals(typedto.getSremark())) { // 3128库存日报表
					sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
					StringBuffer paramsql = new StringBuffer("");
					StringBuffer paramsql2 = new StringBuffer("");
					StringBuffer paramsql3 = new StringBuffer("");
					StringBuffer currenttable = new StringBuffer("");
					paramsql.append("select distinct s_rptdate from HTR_STOCKDAYRPT where ");
					paramsql2.append(this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
					paramsql3.append(" and S_RPTDATE >= ? and S_RPTDATE <= ? ");
					sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
					sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					{
						currenttable.append(" union select distinct s_rptdate from TR_STOCKDAYRPT where "+this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
						currenttable.append(" and S_RPTDATE >= ? and S_RPTDATE <= ? ");
						sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
						sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					}
					SQLResults rs = sqlExec.runQueryCloseCon(paramsql.toString()+paramsql2.toString()+paramsql3.toString()+currenttable);
					if(rs.getRowCount() == 0 || "".equals(rs.getString(0, 0))) {//如果没有数据，不进行下去
						errorlist.add("3128财政申请库存日报表查询无数据,未导出");
						continue;
					}else { //如果有数据则生成脚本
						List<String> ls = this.acheiveCollFromRS(rs);
						Map<String,List<String>> newMap = this.createShellForExp(ls,paramdto,"", paramsql2.toString(), "", StateConstant.RecvTips_3128_KC, paramdto.getStartdate(), paramdto.getEnddate());
						expBuffer.append(newMap.get("shell").get(0));
						filelist.addAll(newMap.get("files"));
					}
				} else if (StateConstant.RecvTips_3129.equals(typedto.getSremark())) { // 3129电子税票
					sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
					StringBuffer paramsql = new StringBuffer("");
					StringBuffer paramsql2 = new StringBuffer("");
					StringBuffer paramsql3 = new StringBuffer("");
					StringBuffer currenttable = new StringBuffer("");
					paramsql.append("select distinct s_applydate from HTV_FIN_INCOMEONLINE where ");
					paramsql2.append(this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
					paramsql3.append(" and S_APPLYDATE >= ? and S_APPLYDATE <= ? ");
					sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
					sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					{
						currenttable.append(" union select distinct s_applydate from TV_FIN_INCOMEONLINE where "+this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
						currenttable.append(" and S_APPLYDATE >= ? and S_APPLYDATE <= ? ");
						sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
						sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					}
					SQLResults rs = sqlExec.runQueryCloseCon(paramsql.toString()+paramsql2.toString()+paramsql3.toString()+currenttable);
					if(rs.getRowCount() == 0 || "".equals(rs.getString(0, 0))) {//如果没有数据，不进行下去
						errorlist.add("3129财政申请电子税票查询无数据,未导出");
						continue;
					}else { //如果有数据则生成脚本
						List<String> ls = this.acheiveCollFromRS(rs);
						Map<String,List<String>> newMap = this.createShellForExp(ls,paramdto,"", paramsql2.toString(), "", StateConstant.RecvTips_3129, paramdto.getStartdate(), paramdto.getEnddate());
						expBuffer.append(newMap.get("shell").get(0));
						filelist.addAll(newMap.get("files"));
					}
				} else if (StateConstant.RecvTips_3139.equals(typedto.getSremark())) { // 3139入库流水
					sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
					StringBuffer paramsql = new StringBuffer("");
					StringBuffer paramsql2 = new StringBuffer("");
					StringBuffer paramsql3 = new StringBuffer("");
					StringBuffer currenttable = new StringBuffer("");
					paramsql.append("select distinct s_intredate from HTV_FIN_INCOME_DETAIL where ");
					paramsql2.append(this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
					paramsql3.append(" and S_INTREDATE >= ? and S_INTREDATE <= ? ");
					sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
					sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					{
						currenttable.append(" union select distinct s_intredate from TV_FIN_INCOME_DETAIL where "+this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
						currenttable.append(" and S_INTREDATE >= ? and S_INTREDATE <= ? ");
						sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
						sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					}
					SQLResults rs = sqlExec.runQueryCloseCon(paramsql.toString()+paramsql2.toString()+paramsql3.toString()+currenttable);
					if(rs.getRowCount() == 0 || "".equals(rs.getString(0, 0))) {//如果没有数据，不进行下去
						errorlist.add("3139财政申请入库流水查询无数据,未导出");
						continue;
					}else { //如果有数据则生成脚本
						List<String> ls = this.acheiveCollFromRS(rs);
						Map<String,List<String>> newMap = this.createShellForExp(ls,paramdto,"", paramsql2.toString(), "", StateConstant.RecvTips_3139, paramdto.getStartdate(), paramdto.getEnddate());
						expBuffer.append(newMap.get("shell").get(0));
						filelist.addAll(newMap.get("files"));
					}
				}else if(StateConstant.RecvTips_3127.equals(typedto.getSremark()))
				{
					sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
					StringBuffer paramsql = new StringBuffer("");
					StringBuffer paramsql2 = new StringBuffer("");
					StringBuffer paramsql3 = new StringBuffer("");
					StringBuffer currenttable = new StringBuffer("");
					paramsql.append("select distinct s_rptdate from HTR_TAXORG_PAYOUT_REPORT where ");
					paramsql2.append(this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
					paramsql3.append("  and S_TAXORGCODE='1' and s_rptdate >= ? and s_rptdate <= ? ");
					sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
					sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					{
						currenttable.append(" union select distinct s_rptdate from TR_TAXORG_PAYOUT_REPORT where "+this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
						currenttable.append("  and S_TAXORGCODE='1' and s_rptdate >= ? and s_rptdate <= ? ");
						sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
						sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					}
					SQLResults rs = sqlExec.runQuery(paramsql.toString()+paramsql2.toString()+paramsql3.toString()+currenttable);
					if(rs.getRowCount() == 0 || "".equals(rs.getString(0, 0))) {//如果没有数据，不进行下去
						errorlist.add("3127一般预算支出报表查询无数据,未导出");
						continue;
					}else { //如果有数据则生成脚本
						List<String> ls = this.acheiveCollFromRS(rs);
						Map<String,List<String>> newMap = this.createShellForExp(ls,paramdto,"", paramsql2.toString(), "", StateConstant.RecvTips_3127+"ybyszc", paramdto.getStartdate(), paramdto.getEnddate());
						expBuffer.append(newMap.get("shell").get(0));
						filelist.addAll(newMap.get("files"));
					}
					paramsql = new StringBuffer("");
					paramsql2 = new StringBuffer("");
					paramsql3 = new StringBuffer("");
					currenttable = new StringBuffer("");
					sqlExec.clearParams();
					paramsql.append("select distinct s_rptdate from HTR_TAXORG_PAYOUT_REPORT where ");
					paramsql2.append(this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
					paramsql3.append("  and S_TAXORGCODE='2' and s_rptdate >= ? and s_rptdate <= ? ");
					sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
					sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					{
						currenttable.append(" union select distinct s_rptdate from TR_TAXORG_PAYOUT_REPORT where "+this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
						currenttable.append("  and S_TAXORGCODE='2' and s_rptdate >= ? and s_rptdate <= ? ");
						sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
						sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					}
					rs = sqlExec.runQuery(paramsql.toString()+paramsql2.toString()+paramsql3.toString()+currenttable);
					if(rs.getRowCount() == 0 || "".equals(rs.getString(0, 0))) {//如果没有数据，不进行下去
						errorlist.add("3127实拨资金支出报表查询无数据,未导出");
						continue;
					}else { //如果有数据则生成脚本
						List<String> ls = this.acheiveCollFromRS(rs);
						Map<String,List<String>> newMap = this.createShellForExp(ls,paramdto,"", paramsql2.toString(), "", StateConstant.RecvTips_3127+"sbzjzc", paramdto.getStartdate(), paramdto.getEnddate());
						expBuffer.append(newMap.get("shell").get(0));
						filelist.addAll(newMap.get("files"));
					}
					paramsql = new StringBuffer("");
					paramsql2 = new StringBuffer("");
					paramsql3 = new StringBuffer("");
					currenttable = new StringBuffer("");
					sqlExec.clearParams();
					paramsql.append("select distinct s_rptdate from HTR_TAXORG_PAYOUT_REPORT where ");
					paramsql2.append(this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
					paramsql3.append("  and S_TAXORGCODE='3' and s_rptdate >= ? and s_rptdate <= ? ");
					sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
					sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					{
						currenttable.append(" union select distinct s_rptdate from TR_TAXORG_PAYOUT_REPORT where "+this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
						currenttable.append("  and S_TAXORGCODE='3' and s_rptdate >= ? and s_rptdate <= ? ");
						sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
						sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					}
					rs = sqlExec.runQuery(paramsql.toString()+paramsql2.toString()+paramsql3.toString()+currenttable);
					if(rs.getRowCount() == 0 || "".equals(rs.getString(0, 0))) {//如果没有数据，不进行下去
						errorlist.add("3127调拨预算支出报表查询无数据,未导出");
						continue;
					}else { //如果有数据则生成脚本
						List<String> ls = this.acheiveCollFromRS(rs);
						Map<String,List<String>> newMap = this.createShellForExp(ls,paramdto,"", paramsql2.toString(), "", StateConstant.RecvTips_3127+"dbyszc", paramdto.getStartdate(), paramdto.getEnddate());
						expBuffer.append(newMap.get("shell").get(0));
						filelist.addAll(newMap.get("files"));
					}
					paramsql = new StringBuffer("");
					paramsql2 = new StringBuffer("");
					paramsql3 = new StringBuffer("");
					currenttable = new StringBuffer("");
					sqlExec.clearParams();
					paramsql.append("select distinct s_rptdate from HTR_TAXORG_PAYOUT_REPORT where ");
					paramsql2.append(this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
					paramsql3.append("  and S_TAXORGCODE='4' and s_rptdate >= ? and s_rptdate <= ? ");
					sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
					sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					{
						currenttable.append(" union select distinct s_rptdate from TR_TAXORG_PAYOUT_REPORT where "+this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
						currenttable.append("  and S_TAXORGCODE='4' and s_rptdate >= ? and s_rptdate <= ? ");
						sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
						sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					}
					rs = sqlExec.runQuery(paramsql.toString()+paramsql2.toString()+paramsql3.toString()+currenttable);
					if(rs.getRowCount() == 0 || "".equals(rs.getString(0, 0))) {//如果没有数据，不进行下去
						errorlist.add("3127直接支付报表查询无数据,未导出");
						continue;
					}else { //如果有数据则生成脚本
						List<String> ls = this.acheiveCollFromRS(rs);
						Map<String,List<String>> newMap = this.createShellForExp(ls,paramdto,"", paramsql2.toString(), "", StateConstant.RecvTips_3127+"zjzf", paramdto.getStartdate(), paramdto.getEnddate());
						expBuffer.append(newMap.get("shell").get(0));
						filelist.addAll(newMap.get("files"));
					}
					paramsql = new StringBuffer("");
					paramsql2 = new StringBuffer("");
					paramsql3 = new StringBuffer("");
					currenttable = new StringBuffer("");
					sqlExec.clearParams();
					paramsql.append("select distinct s_rptdate from HTR_TAXORG_PAYOUT_REPORT where ");
					paramsql2.append(this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
					paramsql3.append("  and S_TAXORGCODE='5' and s_rptdate >= ? and s_rptdate <= ? ");
					sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
					sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					{
						currenttable.append(" union select distinct s_rptdate from TR_TAXORG_PAYOUT_REPORT where "+this.createParamStr(paramdto, typedto.getSremark(),finddtolist));
						currenttable.append("  and S_TAXORGCODE='5' and s_rptdate >= ? and s_rptdate <= ? ");
						sqlExec.addParam(paramdto.getStartdate().toString().replace("-", ""));
						sqlExec.addParam(paramdto.getEnddate().toString().replace("-", ""));
//					}
					rs = sqlExec.runQueryCloseCon(paramsql.toString()+paramsql2.toString()+paramsql3.toString()+currenttable);
					if(rs.getRowCount() == 0 || "".equals(rs.getString(0, 0))) {//如果没有数据，不进行下去
						errorlist.add("3127授权支付报表查询无数据,未导出");
						continue;
					}else { //如果有数据则生成脚本
						List<String> ls = this.acheiveCollFromRS(rs);
						Map<String,List<String>> newMap = this.createShellForExp(ls,paramdto,"", paramsql2.toString(), "", StateConstant.RecvTips_3127+"sqzf", paramdto.getStartdate(), paramdto.getEnddate());
						expBuffer.append(newMap.get("shell").get(0));
						filelist.addAll(newMap.get("files"));
					}
				}
			}
			if(filelist.size() > 0){
				FileUtil.getInstance().writeFile(shellpath, expBuffer.toString());
				byte[] bytes = null;
				String results = null;
				bytes = DB2CallShell.dbCallShellWithRes(shellpath);
				if (bytes.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
					results = new String(bytes, 0, MsgConstant.MAX_CALLSHELL_RS * 1024);
				} else {
					results = new String(bytes);
				}
				log.debug("调用SHELL结果:" + results);
			}			
			resmap.put("error", errorlist);
			/*对所有文件加上字段名称*/
			resmap.put("files", this.process3129CSV(filelist, paramdto));
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("导出文件异常!\n"+e.getMessage(),e);			
		} finally{
			if(sqlExec != null) {
				sqlExec.closeConnection();
			}
		}
		return resmap;
	}
	
	/**
	 * 用于在操作全部完成之后删除服务器上文件
	 */
	public void deleteTheFiles(List filelist) throws ITFEBizException {		
		try {
			File dir = new File(ITFECommonConstant.FILE_ROOT_PATH+"tipstemp");
			if(dir.exists()) {
				FileUtil.getInstance().deleteFiles(ITFECommonConstant.FILE_ROOT_PATH+"tipstemp"); //删除目录
			}
			File sqlfile = new File(ITFECommonConstant.FILE_ROOT_PATH+"tipsexport.sql");
			if(sqlfile.exists()) {
				FileUtil.getInstance().deleteFile(ITFECommonConstant.FILE_ROOT_PATH+"tipsexport.sql"); //删除脚本
			}			
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage(),e);
		} catch (FileNotFoundException e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage(),e);
		}
	}
	/**
	 * 根据条件生成linux的shell脚本
	 * @throws FileOperateException 
	 */
		String dirsep = File.separator; // 取得系统分割符
		public Map<String, List<String>> createShellForExp(List<String> ls ,TipsParamDto paramdto,String paramsql ,String paramsql2,String paramsql3,String type ,Date sdate,Date edate) throws FileOperateException {
		Map<String, List<String>> rtmap = new HashMap<String, List<String>>(); //存放脚本信息
		List<String> filelist = new ArrayList<String>(); //存放文件列表
		//文件操作根路径,目前是
		String root = ITFECommonConstant.FILE_ROOT_PATH;
		//开始日期和结束日期相隔天数
		int count = this.daysOfTwo(sdate, edate);
		String shellpath = root+"tipsexport.sql";
		StringBuffer expcontent =new StringBuffer("");
		String abTipsRoot = root+"tipstemp/";
		String sr_3128 = abTipsRoot+StateConstant.sr_3128;
		String kc_3128 = abTipsRoot+StateConstant.kc_3128;
		String on_3129 = abTipsRoot+StateConstant.on_3129;
		String in_3139 = abTipsRoot+StateConstant.in_3139;
		String in_3127 = abTipsRoot+StateConstant.in_3127;
		/*
		 * 提前创建文件目录，否则LINUX会报错
		 */
		FileUtil.getInstance().createDir(sr_3128);
		FileUtil.getInstance().createDir(kc_3128);
		FileUtil.getInstance().createDir(on_3129);
		FileUtil.getInstance().createDir(in_3139);
		FileUtil.getInstance().createDir(in_3127);
		//用于得到某日期下一天
		String time = TimeFacade.getCurrentStringTime("yyyyMMddHHmmss");
//
		for(int i = 1 ; i <= ls.size() ; i++) {
			String fileEnd = time+"_数据日期-"+ls.get(i-1)+"_"+i+".csv"; //文件名后部分，提取出来，便于修改
			if (StateConstant.RecvTips_3128_SR.equals(type)) { // 3128收入日报表
				String fname = getLoginInfo().getSorgcode()+"_3128收入日报表_"+fileEnd;
				expcontent.append("export to "+sr_3128+fname+" of del select * from HTR_INCOMEDAYRPT where "+paramsql2+" and S_RPTDATE='"+ls.get(i-1)+"' union select * from TR_INCOMEDAYRPT where "+paramsql2+" and S_RPTDATE='"+ls.get(i-1)+"';\n");
				filelist.add(sr_3128+fname);
			} else if (StateConstant.RecvTips_3128_KC.equals(type)) { // 3128库存日报表
				String fname = getLoginInfo().getSorgcode()+"_3128库存日报表_"+fileEnd;
				expcontent.append("export to "+kc_3128+fname+" of del select * from HTR_STOCKDAYRPT where "+paramsql2+" and S_RPTDATE='"+ls.get(i-1)+"' union select * from TR_STOCKDAYRPT where "+paramsql2+" and S_RPTDATE='"+ls.get(i-1)+"';\n");
				filelist.add(kc_3128+fname);
			} else if (StateConstant.RecvTips_3129.equals(type)) { // 3129电子税票
				String fname = getLoginInfo().getSorgcode()+"_3129电子税票信息_"+fileEnd;
				if(null != paramdto && null != paramdto.getExptype() && !"".equals(paramdto.getExptype())) {
					if("0".equals(paramdto.getExptype())) { //采用库表格式
						expcontent.append("export to "+on_3129+fname+" of del select * from HTV_FIN_INCOMEONLINE where "+paramsql2+" and S_APPLYDATE='"+ls.get(i-1)+"' union select * from TV_FIN_INCOMEONLINE where "+paramsql2+" and S_APPLYDATE='"+ls.get(i-1)+"';\n");
						filelist.add(on_3129+fname);
					}
				}else { //未选择默认采用库表格式
					expcontent.append("export to "+on_3129+fname+" of del select * from HTV_FIN_INCOMEONLINE where "+paramsql2+" and S_APPLYDATE='"+ls.get(i-1)+"' union select * from TV_FIN_INCOMEONLINE where "+paramsql2+" and S_APPLYDATE='"+ls.get(i-1)+"';\n");
					filelist.add(on_3129+fname);
				}			
				
			} else if (StateConstant.RecvTips_3139.equals(type)) { // 3139入库流水
				String fname = getLoginInfo().getSorgcode()+"_3139入库流水信息_"+fileEnd;
				expcontent.append("export to "+in_3139+fname+" of del select * from HTV_FIN_INCOME_DETAIL where "+paramsql2+" and S_INTREDATE='"+ls.get(i-1)+"' union select * from TV_FIN_INCOME_DETAIL where "+paramsql2+" and S_INTREDATE='"+ls.get(i-1)+"';\n");
				filelist.add(in_3139+fname);
			}else if((StateConstant.RecvTips_3127+"ybyszc").equals(type))//预算支出报表
			{
				String fname = getLoginInfo().getSorgcode()+"_3127一般预算支出报表信息_"+fileEnd;
				expcontent.append("export to "+in_3127+fname+" of del select * from HTR_TAXORG_PAYOUT_REPORT where "+paramsql2+" and S_TAXORGCODE='1' and s_rptdate='"+ls.get(i-1)+"' union select * from TR_TAXORG_PAYOUT_REPORT where "+paramsql2+" and S_TAXORGCODE='1' and s_rptdate='"+ls.get(i-1)+"';\n");
				filelist.add(in_3127+fname);	
			}else if((StateConstant.RecvTips_3127+"sbzjzc").equals(type))//预算支出报表
			{
				String fname = getLoginInfo().getSorgcode()+"_3127实拨资金支出报表信息_"+fileEnd;
				expcontent.append("export to "+in_3127+fname+" of del select * from HTR_TAXORG_PAYOUT_REPORT where "+paramsql2+" and S_TAXORGCODE='2' and s_rptdate='"+ls.get(i-1)+"' union select * from TR_TAXORG_PAYOUT_REPORT where "+paramsql2+" and S_TAXORGCODE='2' and s_rptdate='"+ls.get(i-1)+"';\n");
				filelist.add(in_3127+fname);	
			}else if((StateConstant.RecvTips_3127+"dbyszc").equals(type))//预算支出报表
			{
				String fname = getLoginInfo().getSorgcode()+"_3127调拨预算支出报表信息_"+fileEnd;
				expcontent.append("export to "+in_3127+fname+" of del select * from HTR_TAXORG_PAYOUT_REPORT where "+paramsql2+" and S_TAXORGCODE='3'  and s_rptdate='"+ls.get(i-1)+"' union select * from TR_TAXORG_PAYOUT_REPORT where "+paramsql2+" and S_TAXORGCODE='3'  and s_rptdate='"+ls.get(i-1)+"';\n");
				filelist.add(in_3127+fname);	
			}else if((StateConstant.RecvTips_3127+"zjzf").equals(type))//预算支出报表
			{
				String fname = getLoginInfo().getSorgcode()+"_3127直接支付报表信息_"+fileEnd;
				expcontent.append("export to "+in_3127+fname+" of del select * from HTR_TAXORG_PAYOUT_REPORT where "+paramsql2+" and S_TAXORGCODE='4'  and s_rptdate='"+ls.get(i-1)+"' union select * from TR_TAXORG_PAYOUT_REPORT where "+paramsql2+" and S_TAXORGCODE='4'  and s_rptdate='"+ls.get(i-1)+"';\n");
				filelist.add(in_3127+fname);	
			}else if((StateConstant.RecvTips_3127+"sqzf").equals(type))//预算支出报表
			{
				String fname = getLoginInfo().getSorgcode()+"_3127授权支付报表信息_"+fileEnd;
				expcontent.append("export to "+in_3127+fname+" of del select * from HTR_TAXORG_PAYOUT_REPORT where "+paramsql2+" and S_TAXORGCODE='5'  and s_rptdate='"+ls.get(i-1)+"' union select * from TR_TAXORG_PAYOUT_REPORT where "+paramsql2+" and S_TAXORGCODE='5'  and s_rptdate='"+ls.get(i-1)+"';\n");
				filelist.add(in_3127+fname);	
			}
		}
		//修改成福建统计格式不按天存放
		if (StateConstant.RecvTips_3129.equals(type) && null != paramdto.getExptype() && "1".equals(paramdto.getExptype())) { //采用福建统计格式
			String f_name = getLoginInfo().getSorgcode()+"_3129电子税票信息_"+time+"_TEMP.csv";
			expcontent.append("export to "+on_3129+f_name+" of del select substr(S_TAXORGCODE,1,1),S_TAXPAYCODE,S_TAXPAYNAME,'','',C_BDGLEVEL,S_BDGSBTCODE,S_TAXTYPECODE,S_ASTFLAG,F_TRAAMT from HTV_FIN_INCOMEONLINE where "+paramsql2+" and S_APPLYDATE >= '"+paramdto.getStartdate().toString().replaceAll("-", "")+"' and S_APPLYDATE <='"+paramdto.getEnddate().toString().replaceAll("-", "")+"' union select substr(S_TAXORGCODE,1,1),S_TAXPAYCODE,S_TAXPAYNAME,'','',C_BDGLEVEL,S_BDGSBTCODE,S_TAXTYPECODE,S_ASTFLAG,F_TRAAMT from TV_FIN_INCOMEONLINE where "+paramsql2+" and S_APPLYDATE >= '"+paramdto.getStartdate().toString().replaceAll("-", "")+"' and S_APPLYDATE <='"+paramdto.getEnddate().toString().replaceAll("-", "")+"';\n");
			filelist.add(on_3129+f_name);
		}
		List<String> l = new ArrayList<String>();
		l.add(expcontent.toString());
		rtmap.put("shell", l);
		rtmap.put("files", filelist);
		return rtmap;				
	}
	
	/**
	 * 根据DTO生成sql查询条件
	 * @param dto
	 * @param type
	 * @param finlist
	 */
	public String createParamStr(TipsParamDto dto ,String type,List<TsConvertfinorgDto> finlist) {
		String trecode = dto.getStrecode();
		String trestr = "";
		int point = 0;
		if(null != dto.getIfsub() && "1".equals(dto.getIfsub()) && null != trecode && !"".equals(trecode)) { //对于包含下级的特殊处理					
			for(int i = trecode.length() ;i > 0 ; i--) {
				char c = trecode.charAt(i-1);
				if(c != '0') {
					trestr = trecode.substring(0, i);
					point = i;
					break;
				}
			}
		}
		StringBuffer sb = new StringBuffer("");
		sb.append(" 1=1 ");
		if(StateConstant.RecvTips_3129.equals(type)&&!(this.getLoginInfo().getSorgcode().startsWith("20")||this.getLoginInfo().getSorgcode().startsWith("24"))) { //电子税票默认刷选状态
			sb.append(" and S_TRASTATE in ("+StateConstant.STATE_OF_3129+") ");
		}
		//非中心机构，查询所属国库所有
		if (!getLoginInfo().getSorgcode().equals(StateConstant.ORG_CENTER_CODE)&&!(this.getLoginInfo().getSorgcode().startsWith("20")||this.getLoginInfo().getSorgcode().startsWith("24"))) {
			if((null == dto.getSorgcode() || "".equals(dto.getSorgcode())) && (null == dto.getStrecode() || "".equals(dto.getStrecode()))) {
				//国库代码和财政机构同时为空，则查询本核算主体所有国库
				sb.append(" and ( S_TRECODE='"+finlist.get(0).getStrecode()+"' ");
				for(int i = 1 ;i<finlist.size();i++){
					sb.append(" or S_TRECODE= '"+finlist.get(i).getStrecode()+"' ");
				}
				sb.append(" ) ");
			}			
		}		
		if(null != dto.getSorgcode() && !"".equals(dto.getSorgcode())) { //财政机构代码
			if(StateConstant.RecvTips_3128_SR.equals(type)) {
				sb.append(" and S_FINORGCODE='"+dto.getSorgcode()+"' ");
			}else {
				sb.append(" and S_ORGCODE='"+dto.getSorgcode()+"' ");
			}			
		}
		if(null != dto.getStaxorgcode() && !"".equals(dto.getStaxorgcode())) { //征收机关代码
			if(!StateConstant.RecvTips_3128_KC.equals(type)) {
				sb.append(" and S_TAXORGCODE='"+dto.getStaxorgcode()+"' ");
			}
		}
		if(null != dto.getStrecode() && !"".equals(dto.getStrecode())) { //国库代码
			if(this.getLoginInfo().getSorgcode().startsWith("20")||this.getLoginInfo().getSorgcode().startsWith("24"))
			{
				List<TsTreasuryDto> treasuryList =null;//查询国库列表
				try
				{
					if("0".equals(dto.getIfsub()))
					{
						TsTreasuryDto findto = new TsTreasuryDto();
						findto.setSorgcode(this.getLoginInfo().getSorgcode());
						findto.setStrecode(dto.getStrecode());
						treasuryList = CommonFacade.getODB().findRsByDto(findto);
					}else if("1".equals(dto.getIfsub()))
					{
						TsTreasuryDto findto = new TsTreasuryDto();
						TsTreasuryDto resultdto = null;
						findto.setSorgcode(this.getLoginInfo().getSorgcode());
						findto.setStrecode(dto.getStrecode());
						List<TsTreasuryDto> resultList = CommonFacade.getODB().findRsByDto(findto);
						resultdto = resultList!=null&&resultList.size()==1?resultList.get(0):null;
						if(resultdto!=null)
							treasuryList = PublicSearchFacade.getSubTreCode(resultdto);
						else
							treasuryList = PublicSearchFacade.getSubTreCode(this.getLoginInfo().getSorgcode());
					}else
						sb.append(" and S_TRECODE='"+dto.getStrecode()+"' ");
				}catch (Exception e) {
					log.error(e);			
				}
				if(treasuryList!=null&&treasuryList.size()>0)
				{
					for(int i=0;i<treasuryList.size();i++)
					{
						if(i==0)
							sb.append(" and S_TRECODE in('"+treasuryList.get(i).getStrecode()+"'");
						else
							sb.append(",'"+treasuryList.get(i).getStrecode()+"'");
					}
					sb.append(") ");
				}
			}else
			{
				if(null == dto.getIfsub() || "".equals(dto.getIfsub()) || "0".equals(dto.getIfsub())) { //不包含下级
					sb.append(" and S_TRECODE='"+dto.getStrecode()+"' ");
				}else {
					sb.append(" and substr(S_TRECODE,1,"+point+")='"+trestr+"' ");
				}
			}
		}else
		{
			List<TsTreasuryDto> treasuryList =null;//查询国库列表
			try
			{
				if("0".equals(dto.getIfsub()))
				{
					TsTreasuryDto findto = new TsTreasuryDto();
					findto.setSorgcode(this.getLoginInfo().getSorgcode());
					treasuryList = CommonFacade.getODB().findRsByDto(findto);
				}else if("1".equals(dto.getIfsub()))
				{
					treasuryList = PublicSearchFacade.getSubTreCode(this.getLoginInfo().getSorgcode());
				}
			}catch (Exception e) {
				log.error(e);			
			}
			if(treasuryList!=null&&treasuryList.size()>0)
			{
				for(int i=0;i<treasuryList.size();i++)
				{
					if(i==0)
						sb.append(" and S_TRECODE in('"+treasuryList.get(i).getStrecode()+"'");
					else
						sb.append(",'"+treasuryList.get(i).getStrecode()+"'");
				}
				sb.append(") ");
			}
		}
		if(null != dto.getSbeflag() && !"".equals(dto.getSbeflag())) {  //辖属标志
			if(StateConstant.RecvTips_3128_SR.equals(type)) {
				sb.append(" and S_BELONGFLAG='"+dto.getSbeflag()+"' ");
			}			
		}		
		return sb.toString();
	}
	
	/**
	 * 将生成的3129文件转换成福建要求格式,并对其他文件加上字段名称
	 * @param flist
	 * @param tipsdto
	 * @throws FileOperateException 
	 * @throws FileNotFoundException 
	 */
	public List<String> process3129CSV(List<String> flist,TipsParamDto tipsdto) throws FileOperateException, FileNotFoundException {
		List<String> filel = new ArrayList<String>();
		for(String file : flist) {
			if(new File(file).exists()) {
				if(file.indexOf(StateConstant.sr_3128) != -1) { //包含3128sr此字符串
					String newContent = StateConstant.RecvTips_3128_SR_ColName+"\r\n"+FileUtil.getInstance().readFile(file); //生成加上名称的文件内容
					FileUtil.getInstance().deleteFile(file); //删除原文件
					FileUtil.getInstance().writeFile(file, newContent); //生成新文件
					filel.add(file);
				}else if(file.indexOf(StateConstant.kc_3128) != -1) { //包含3128kc此字符串
					String newContent = StateConstant.RecvTips_3128_KC_ColName+"\r\n"+FileUtil.getInstance().readFile(file);
					FileUtil.getInstance().deleteFile(file);
					FileUtil.getInstance().writeFile(file, newContent);
					filel.add(file);
				}else if(file.indexOf(StateConstant.on_3129) != -1) { //包含3129此字符串
					if(null != tipsdto.getExptype() && "1".equals(tipsdto.getExptype())) { //按照重点税源统计格式
						String split = ","; //del文件默认分隔符
						String newFileName = tipsdto.getStaxorgcode()+"_"+tipsdto.getStartdate().toString().replaceAll("-", "").substring(0,6)+"_8.txt"; //要生成的文件名称
						List<String[]> temps = FileUtil.getInstance().readFileWithLine(file, ","); //将文件内容按行读取
						StringBuffer sb = new StringBuffer(""); //存放文件第一行
						BigDecimal afamt = new BigDecimal("0.00"); //存放总金额
						for(String[] strs : temps) {
							afamt = afamt.add(new BigDecimal(strs[9])); //计算出文件中总金额
						}
						sb.append(newFileName+split+"0"+split+"0"+split+afamt+"\r\n"); //文件第一行
						sb.append(FileUtil.getInstance().readFile(file).replaceAll("\"", "").replaceAll(" ", ""));  //文件主要内容,需要去掉引号和空格
						String newFileDir = file.substring(0,file.lastIndexOf("/")+1); //文件存放路径
						FileUtil.getInstance().writeFile(newFileDir+newFileName, sb.toString()); //生成重点税源格式文件
						filel.add(newFileDir+newFileName);
					}else { // 不采用库表格式，加上字段名称
						String newContent = StateConstant.RecvTips_3129_ColName+"\r\n"+FileUtil.getInstance().readFile(file);
						FileUtil.getInstance().deleteFile(file);
						FileUtil.getInstance().writeFile(file, newContent);
						filel.add(file);
					}	
				}else if(file.indexOf(StateConstant.in_3139) != -1) { //包含3139此字符串
					String newContent = StateConstant.RecvTips_3139_ColName+"\r\n"+FileUtil.getInstance().readFile(file);
					FileUtil.getInstance().deleteFile(file);
					FileUtil.getInstance().writeFile(file, newContent);	
					filel.add(file);
				}else if(file.indexOf(StateConstant.in_3127) != -1)
				{
					String newContent = StateConstant.RecvTips_3127_ColName+"\r\n"+FileUtil.getInstance().readFile(file);
					FileUtil.getInstance().deleteFile(file);
					FileUtil.getInstance().writeFile(file, newContent);	
					filel.add(file);
				}
			}else {
				continue;
			}			
		}		
		return filel;		
	}
	
	/**
	 * 用于将结果集中的日期数据存到List中
	 * @param rs
	 * @return
	 */
	public List<String> acheiveCollFromRS(SQLResults rs) {
		List<String> datelist = new ArrayList<String>();
		for(int i = 0 ; i < rs.getRowCount() ; i++) {
			datelist.add(rs.getString(0, i));
		}
		return datelist;
	}
	/**
	 * 用来比较两个日期之间相隔的天数
	 * @param fDate
	 * @param oDate
	 * @return
	 */
	public int daysOfTwo(Date fDate, Date oDate) {
	       Calendar aCalendar = Calendar.getInstance();
	       aCalendar.setTime(fDate);
	       int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
	       aCalendar.setTime(oDate);
	       int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
	       return day2-day1;
	}
}
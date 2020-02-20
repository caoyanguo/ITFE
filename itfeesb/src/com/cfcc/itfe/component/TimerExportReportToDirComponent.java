package com.cfcc.itfe.component;

//import java.io.File;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.module.client.MuleClient;

import com.cfcc.deptone.common.util.CallShellUtil;
import com.cfcc.deptone.common.util.DB2CallShell;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HeadDto;
import com.cfcc.itfe.persistence.dto.StatusOfReportDownLoad;
import com.cfcc.itfe.persistence.dto.TdDownloadReportCheckDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeonlineDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 
 * 报文发送接收
 * 
 */
@SuppressWarnings("unchecked")
public class TimerExportReportToDirComponent implements Callable {

	private static Log logger = LogFactory.getLog(TimerExportReportToDirComponent.class);
	private static String datadate = null;
//	private static String datatype = null;
	private static int count = 1;
	public Object onCall(MuleEventContext eventContext) throws Exception {
		logger.info("报表定时任务导出报表文件start=================================="+datadate);
		logger.info("报表定时任务导出报表文件count=================================="+count);
		if(ITFECommonConstant.PUBLICPARAM.contains(",exportReportToCsv=true,"))
		{
			if(count>=4)
				count = 1;
			exportReportToCsv();
			count++;
		}
		if(count==0)
			searchStatusOfReportDownLoad();
		logger.info("报表定时任务导出报表文件end=================================="+TimeFacade.getCurrentStringTimebefor());
		return eventContext;
	}
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
	private void exportReportToCsv() throws ITFEBizException
	{
		SQLExecutor selectsqlExec = null;
		try {
			List<String> filelist = new ArrayList<String>(); //存放文件列表
			String beforDate = TimeFacade.getCurrentStringTimebefor();
			StringBuffer expcontent =new StringBuffer("");
			expcontent.append(PublicSearchFacade.getSqlConnectByProp());
			SQLResults rs = null;
			String abTipsRoot = null;
			if(isWin())
				abTipsRoot = "D:/itfe/kbftp/";
			else
				abTipsRoot = "/itfe/kbftp/";
			String shellpath = abTipsRoot+"tipsexport.sql";
			/*
			 * 提前创建文件目录，否则LINUX会报错
			 */
			FileUtil.getInstance().createDir(abTipsRoot);
			//用于得到某日期下一天
			String fname = abTipsRoot+beforDate+"_3128_shoururibao_1.csv"; // 3128收入日报表
			expcontent.append("export to "+fname+" of del select * from TR_INCOMEDAYRPT where S_RPTDATE='"+beforDate+"';\n");//select * from HTR_INCOMEDAYRPT where S_RPTDATE='"+beforDate+"' union
			filelist.add(fname);
			fname = abTipsRoot+beforDate+"_3130_kucunribao_1.csv"; // 3130库存日报表
			expcontent.append("export to "+fname+" of del select * from TR_STOCKDAYRPT where S_RPTDATE='"+beforDate+"';\n");//select * from HTR_STOCKDAYRPT where S_RPTDATE='"+beforDate+"' union
			filelist.add(fname);
			fname = abTipsRoot+beforDate+"_3139_rukuliushui_1.csv";//3139入库流水
			expcontent.append("export to "+fname+" of del select * from TV_FIN_INCOME_DETAIL where S_INTREDATE='"+beforDate+"';\n");//select * from HTV_FIN_INCOME_DETAIL where S_INTREDATE='"+beforDate+"' union 
			filelist.add(fname);
			fname = abTipsRoot+beforDate+"_3127_yusuanzhichu_1.csv";//3127支出报表
			expcontent.append("export to "+fname+" of del select * from TR_TAXORG_PAYOUT_REPORT where s_rptdate='"+beforDate+"';\n");//select * from HTR_TAXORG_PAYOUT_REPORT where s_rptdate='"+beforDate+"' union 
			filelist.add(fname);
			fname = abTipsRoot+beforDate+"_3129_dianzishuipiao_1.csv"; // 3129电子税票
			expcontent.append("export to "+fname+" of del select * from TV_FIN_INCOMEONLINE where S_APPLYDATE='"+beforDate+"';\n");//select * from HTV_FIN_INCOMEONLINE where S_APPLYDATE='"+beforDate+"' union 
			filelist.add(fname);
			if(datadate!=null&&!"".equals(datadate)&&!beforDate.equals(datadate))
			{
				if(selectsqlExec==null)
					selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				rs = selectsqlExec.runQuery("select * from TV_FIN_INCOMEONLINE where S_APPLYDATE='"+datadate+"'",TvFinIncomeonlineDto.class);//select * from HTR_TAXORG_PAYOUT_REPORT where s_rptdate='"+datadate+"' union 
				if(rs.getDtoCollection()!=null&&rs.getDtoCollection().size()>0)
				{
					fname = abTipsRoot+datadate+"_3128_shoururibao_1.csv"; // 3128收入日报表
					expcontent.append("export to "+fname+" of del select * from TR_INCOMEDAYRPT where S_RPTDATE='"+datadate+"';\n");//select * from HTR_INCOMEDAYRPT where S_RPTDATE='"+datadate+"' union
					filelist.add(fname);
					fname = abTipsRoot+datadate+"_3130_kucunribao_1.csv"; // 3130库存日报表
					expcontent.append("export to "+fname+" of del select * from TR_STOCKDAYRPT where S_RPTDATE='"+datadate+"';\n");//select * from HTR_STOCKDAYRPT where S_RPTDATE='"+datadate+"' union 
					filelist.add(fname);
					fname = abTipsRoot+datadate+"_3139_rukuliushui_1.csv"; //3139入库流水
					expcontent.append("export to "+fname+" of del select * from TV_FIN_INCOME_DETAIL where S_INTREDATE='"+datadate+"';\n");//select * from HTV_FIN_INCOME_DETAIL where S_INTREDATE='"+datadate+"' union 
					filelist.add(fname);
					fname = abTipsRoot+datadate+"_3127_yusuanzhichu_1.csv"; // 3127支出报表
					expcontent.append("export to "+fname+" of del select * from TR_TAXORG_PAYOUT_REPORT where s_rptdate='"+datadate+"';\n");//select * from HTR_TAXORG_PAYOUT_REPORT where s_rptdate='"+datadate+"' union 
					filelist.add(fname);
					fname = abTipsRoot+datadate+"_3129_dianzishuipiao_1.csv"; // 3129电子税票
					expcontent.append("export to "+fname+" of del select * from TV_FIN_INCOMEONLINE where S_APPLYDATE='"+datadate+"';\n");//select * from HTV_FIN_INCOMEONLINE where S_APPLYDATE='"+datadate+"' union 
					filelist.add(fname);
				}
			}
			if(count==3)
			{
				if(selectsqlExec==null)
					selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				rs = selectsqlExec.runQuery("select * from TV_FIN_INCOMEONLINE where S_APPLYDATE='"+beforDate+"'",TvFinIncomeonlineDto.class);//周五的电子税票下周一下发以3129电子税票判断为准select * from HTR_TAXORG_PAYOUT_REPORT where s_rptdate='"+beforDate+"' union 
				if(rs.getDtoCollection()==null||rs.getDtoCollection().size()<=0)
				{
					if(datadate==null||"".equals(datadate))
						datadate = beforDate;//得到没有数据的日期
					else
					{
						selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
						rs = selectsqlExec.runQuery("select * from TV_FIN_INCOMEONLINE where S_APPLYDATE='"+datadate+"'",TvFinIncomeonlineDto.class);//周五的电子税票下周一下发以3129电子税票判断为准 
						if(rs!=null&&rs.getDtoCollection().size()>0)
							datadate = null;
					}
				}
				else if(datadate!=null&&!"".equals(datadate))
				{
					if(selectsqlExec==null)
						selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
					rs = selectsqlExec.runQuery("select * from TV_FIN_INCOMEONLINE where S_APPLYDATE='"+datadate+"'",TvFinIncomeonlineDto.class);//周五的电子税票下周一下发以3129电子税票判断为准 
					if(rs!=null&&rs.getDtoCollection().size()>0)
						datadate = null;
					else if(daysOfTwo(datadate,beforDate)>7)
						datadate = null;
						
				}
			}
			try{
				FileUtil.getInstance().deleteFileForExists(shellpath);
			}catch(Exception e)
			{
			}
			FileUtil.getInstance().writeFile(shellpath, expcontent.toString());
			CallShellUtil.callShellWithRes("chmod -R 777 "+shellpath);
			byte[] bytes = null;
			String results = null;
			bytes = DB2CallShell.dbCallShellWithRes(shellpath);
			FileUtil.getInstance().writeFile(abTipsRoot+TimeFacade.getCurrentStringTime()+".end", "");
			CallShellUtil.callShellWithRes("chmod -R 777 "+abTipsRoot+beforDate);
//			CallShellUtil.callShellWithRes("chmod 777 "+abTipsRoot+TimeFacade.getCurrentStringTime()+".end");
			if (bytes.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
				results = new String(bytes, 0, MsgConstant.MAX_CALLSHELL_RS * 1024);
			} else {
				results = new String(bytes);
			}
			logger.error("报表定时任务导出报表文件调用SHELL结果:" + results);			
		} catch (FileOperateException e) {
			logger.error("报表定时任务导出报表文件加标题头出错",e);
			throw new ITFEBizException("报表定时任务导出报表文件加标题头出错",e);
		} catch (FileNotFoundException e) {
			logger.error("报表定时任务导出报表文件没有找到",e);
			throw new ITFEBizException("报表定时任务导出报表文件没有找到",e);
		} catch (Exception e) {
			logger.error("报表定时任务导出报表文件出错",e);
			throw new ITFEBizException("报表定时任务导出报表文件出错",e);
		}finally
		{
			if(selectsqlExec!=null)
				selectsqlExec.closeConnection();
		}
	}
//	private List<String> process3129CSV(List<String> flist) throws FileOperateException, FileNotFoundException {
//		List<String> filel = new ArrayList<String>();
//		for(String file : flist) {
//			if(new File(file).exists()) {
//				if(file.indexOf("_3128_shoururibao") != -1) { //包含3128sr此字符串
//					String newContent = StateConstant.RecvTips_3128_SR_ColName+"\r\n"+FileUtil.getInstance().readFile(file); //生成加上名称的文件内容
//					FileUtil.getInstance().deleteFile(file); //删除原文件
//					FileUtil.getInstance().writeFile(file, newContent); //生成新文件
//					filel.add(file);
//				}else if(file.indexOf("_3128_kucunribao") != -1) { //包含3128kc此字符串
//					String newContent = StateConstant.RecvTips_3128_KC_ColName+"\r\n"+FileUtil.getInstance().readFile(file);
//					FileUtil.getInstance().deleteFile(file);
//					FileUtil.getInstance().writeFile(file, newContent);
//					filel.add(file);
//				}else if(file.indexOf("_3129_dianzishuipiao") != -1) { //包含3129此字符串
//					// 不采用库表格式，加上字段名称
//					String newContent = StateConstant.RecvTips_3129_ColName+"\r\n"+FileUtil.getInstance().readFile(file);
//					FileUtil.getInstance().deleteFile(file);
//					FileUtil.getInstance().writeFile(file, newContent);
//					filel.add(file);	
//				}else if(file.indexOf("_3139_rukuliushui") != -1) { //包含3139此字符串
//					String newContent = StateConstant.RecvTips_3139_ColName+"\r\n"+FileUtil.getInstance().readFile(file);
//					FileUtil.getInstance().deleteFile(file);
//					FileUtil.getInstance().writeFile(file, newContent);	
//					filel.add(file);
//				}else if(file.indexOf("_3127_yusuanzhichu") != -1)
//				{
//					String newContent = StateConstant.RecvTips_3127_ColName+"\r\n"+FileUtil.getInstance().readFile(file);
//					FileUtil.getInstance().deleteFile(file);
//					FileUtil.getInstance().writeFile(file, newContent);	
//					filel.add(file);
//				}
//			}		
//		}		
//		return filel;		
//	}
	private void searchStatusOfReportDownLoad() throws ITFEBizException
	{
		List<StatusOfReportDownLoad> list = new ArrayList<StatusOfReportDownLoad>();
		String beforDate = TimeFacade.getCurrentStringTimebefor();
		int downloadcount = 0;
		String stringdate = null;
		String tresql = "";
		TdDownloadReportCheckDto finddto = new TdDownloadReportCheckDto();
		finddto.setSdates(beforDate);
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
					dto.setSkucun(checkdto.getSkucun()==null?"0":checkdto.getSkucun());
					dto.setSliushui(checkdto.getSliushui()==null?"0":checkdto.getSliushui());
					dto.setSribao(checkdto.getSribao()==null?"0":checkdto.getSribao());
					dto.setSshuipiao(checkdto.getSshuipiao()==null?"0":checkdto.getSshuipiao());
					dto.setSzhichu(checkdto.getSzhichu()==null?"0":checkdto.getSzhichu());
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
					downloadcount++;
					list.add(srdldto);
				}
			}
			for(StatusOfReportDownLoad senddto:list)
			{
				if(("0".equals(senddto.getSribao())||"0".equals(senddto.getSkucun()))&&downloadcount!=trelist.size())
					sendMsg(dto.getStrecode(),beforDate,MsgConstant.MSG_NO_5001);
				if("0".equals(senddto.getSshuipiao())&&downloadcount!=trelist.size())
					sendMsg(senddto.getStrecode(),beforDate,MsgConstant.MSG_NO_5003);
				if("0".equals(senddto.getSliushui())&&downloadcount!=trelist.size())
					sendMsg(senddto.getStrecode(),beforDate,MsgConstant.MSG_NO_5002);
			}
		} catch (JAFDatabaseException e) {
			logger.error("报表定时任务导出报表文件检查哪些国库没有数据申请数据失败!", e);
			throw new ITFEBizException("报表定时任务导出报表文件检查哪些国库没有数据申请数据失败!", e);
		} catch (ValidateException e) {
			logger.error("报表定时任务导出报表文件检查哪些国库没有数据申请数据失败!", e);
			throw new ITFEBizException("报表定时任务导出报表文件检查哪些国库没有数据申请数据失败!", e);
		}		
	}
	private void sendMsg(String trecode, String inTreDate, String msgno)throws ITFEBizException {
		String sfinorg = null;
		try {
			TsConvertfinorgDto finadto = new TsConvertfinorgDto();
			finadto.setStrecode(trecode);
			List<TsConvertfinorgDto> list;
			list = CommonFacade.getODB().findRsByDto(finadto);
			if (null == list || list.size() == 0) 
				logger.error("请在财政机构信息中维护国库和财政代码的对应关系!");
			sfinorg = list.get(0).getSfinorgcode();
			HeadDto headdto = new HeadDto();
			headdto.set_VER(MsgConstant.MSG_HEAD_VER);
			headdto.set_SRC(sfinorg);
			headdto.set_APP(MsgConstant.MSG_HEAD_APP);
			headdto.set_DES(inTreDate);
			headdto.set_msgNo(msgno);
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headdto.set_msgID(msgid);
			headdto.set_msgRef(msgid);
			headdto.set_workDate(TimeFacade.getCurrentStringTime());
			MuleClient client = new MuleClient();
			Map map = new HashMap();
			MuleMessage message = new DefaultMuleMessage(map);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY, msgno + "_OUT");
			message.setProperty(MessagePropertyKeys.MSG_HEAD_DTO, headdto);
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, "000000000000");// 核算主体代码
			message.setProperty(MessagePropertyKeys.MSG_DATE, inTreDate);// 入库日期
			message.setProperty(MessagePropertyKeys.MSG_BILL_CODE, sfinorg);// 发送机关代码
		
			message.setPayload(map);
			if(trecode!=null&&trecode.startsWith("1702"))
				message = client.send("vm://ManagerMsgToPbcCity", message);
			else
				message = client.send("vm://ManagerMsgToPbc", message);
		} catch (MuleException e) {
			logger.error("调用后台报文处理的时候出现异常!", e);
			throw new ITFEBizException("调用后台报文处理的时候出现异常!", e);
		} catch (JAFDatabaseException e) {
			logger.error("调用后台处理出现数据库异常!", e);
		} catch (ValidateException e) {
			logger.error("调用后台处理出现数据库异常!", e);
		} catch (com.cfcc.itfe.exception.SequenceException e) {
			logger.error("取交易流水号时出现异常！", e);
		}
	}
//	private void requestRpt(String strecode, String srptdate)throws ITFEBizException {
//		try {
//			/**
//			 * 第一步 根据国库代码找到对应的财政代码
//			 */
//			TsConvertfinorgDto finadto = new TsConvertfinorgDto();
//			finadto.setStrecode(strecode);
//			List<TsConvertfinorgDto> list = CommonFacade.getODB().findRsByDto(finadto);
//			if (null == list || list.size() == 0) {
//				logger.error("请在财政机构信息中维护国库和财政代码的对应关系!国库代码:"+strecode);
//				throw new ITFEBizException("请在财政机构信息中维护国库和财政代码的对应关系!国库代码:"+strecode);
//			}
//		
//			String finorgcode = list.get(0).getSfinorgcode();
//		
//			/**
//			 * 第二步 根据财政代码和申请报表日期调用后台处理
//			 */
//			MuleClient client = new MuleClient();
//			Map map = new HashMap();
//			MuleMessage message = new DefaultMuleMessage(map);
//			message.setProperty(MessagePropertyKeys.MSG_NO_KEY,MsgConstant.MSG_NO_5001 + "_OUT");
//			message.setProperty(MessagePropertyKeys.MSG_BILL_CODE, finorgcode);
//			message.setProperty(MessagePropertyKeys.MSG_DATE, srptdate);
//			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, list.get(0).getSorgcode());
//			message.setPayload(map);
//			message = client.send("vm://ManagerMsgToPbc", message);
//			ServiceUtil.checkResult(message);
//		} catch (MuleException e) {
//			logger.error("调用YAK后台报文处理出现异常!", e);
//			throw new ITFEBizException("调用YAK后台报文处理出现异常!", e);
//		} catch (JAFDatabaseException e) {
//			logger.error("调用后台处理出现数据库异常!", e);
//			throw new ITFEBizException("调用后台处理出现数据库异常!", e);
//		} catch (ValidateException e) {
//			logger.error("调用后台处理出现数据库异常!", e);
//			throw new ITFEBizException("调用后台处理出现数据库异常!", e);
//		}
//	}
	/**
	 * 用来比较两个日期之间相隔的天数
	 * @param fDate
	 * @param oDate
	 * @return
	 */
	private int daysOfTwo(String startDate, String endDate) {
		if(startDate==null||"".equals(startDate)||endDate==null||"".equals(endDate))
			return 0;
	     Calendar aCalendar = Calendar.getInstance();
	       aCalendar.setTime(TimeFacade.parseDate(startDate));
	       int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
	       aCalendar.setTime(TimeFacade.parseDate(endDate));
	       int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
	       return Math.abs(day2-day1);
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
	
	private void exportBusinessData() throws ITFEBizException {
		String splitSign = ",";// "\t"; // 文件记录分隔符号
		String root = ITFECommonConstant.FILE_ROOT_PATH;
		String dirsep = File.separator; // 取得系统分割符
		String strdate = TimeFacade.getCurrentStringTimebefor(); // 当前系统年月日
		String path = root + dirsep + "businessFile" + dirsep + strdate
				+ dirsep;
		String strdate1 = strdate.substring(0, 4)+"-"+strdate.substring(4, 6)+"-"+strdate.substring(6, 8);
		try {
			List<TsTreasuryDto> trelist = CommonFacade.getODB().findRsByDto(
					new TsTreasuryDto());
			if (trelist != null && trelist.size() > 0) {
				for (TsTreasuryDto tredto : trelist) {

					String sql = " and S_COMMITDATE='"
							+ strdate
							+ "' and S_TRECODE='" + tredto.getStrecode()+"'";
					directPlan(splitSign, path, sql);
					grantPlan(splitSign, path, sql);
					payout(splitSign, path, sql);
					payreckbank(splitSign, path, " and D_VOUDATE='"
							+ strdate1
							+ "' and S_TRECODE='" + tredto.getStrecode()+"'");
					payreckbankback(splitSign, path, " and D_VOUDATE='"
							+ strdate1
							+ "' and S_TRECODE='" + tredto.getStrecode()+"'");
				}
			}

		} catch (Exception e) {
			logger.error("调用后台报文处理的时候出现异常!", e);
			throw new ITFEBizException("调用后台报文处理的时候出现异常!", e);
		}
	}

	/**
	 * 
	 * @param splitSign
	 * @param path
	 * @param string
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws FileOperateException
	 */
	private void payreckbankback(String splitSign, String path, String sql)
			throws JAFDatabaseException, ValidateException,
			FileOperateException {
		List<TvPayreckBankBackDto> list = CommonFacade.getODB()
				.findRsByDtoForWhere(new TvPayreckBankBackDto(), sql);
		StringBuffer filebuf = new StringBuffer();
		if (list != null && list.size() > 0) {
			String filename = TimeFacade.getCurrentStringTime()
					+ list.get(0).getStrecode() + "280hd.txt";
			path = path + filename;
			for (TvPayreckBankBackDto _dto : (List<TvPayreckBankBackDto>) list) {
				filebuf.append("**"+_dto.getSbookorgcode()+splitSign);//核算主体代码
				filebuf.append(_dto.getDentrustdate()+splitSign);//委托日期
				filebuf.append(_dto.getSpackno()+splitSign);//包流水号
				filebuf.append(_dto.getSagentbnkcode()+splitSign);//代理银行行号
				filebuf.append(_dto.getStrano()+splitSign);//交易流水号
				filebuf.append(_dto.getIvousrlno()+splitSign);//凭证流水号
				filebuf.append(_dto.getSvouno()+splitSign);//凭证编号
				filebuf.append(_dto.getDvoudate()+splitSign);//凭证日期
				filebuf.append(_dto.getSfinorgcode()+splitSign);//财政机关代码
				filebuf.append(_dto.getStrecode()+splitSign);//国库代码
				filebuf.append(_dto.getSoritrano()+splitSign);//原交易流水号
				filebuf.append(_dto.getDorientrustdate()+splitSign);//原委托日期
				filebuf.append(_dto.getSorivouno()+splitSign);//原凭证编号
				filebuf.append(_dto.getDorivoudate()+splitSign);//原凭证日期
				filebuf.append(_dto.getSpayeracct()+splitSign);//付款人账号
				filebuf.append(_dto.getSpayername()+splitSign);//付款人名称
				filebuf.append(_dto.getSpayeeacct()+splitSign);//收款人账号
				filebuf.append(_dto.getSpayeename()+splitSign);//收款人名称
				filebuf.append(_dto.getSpaydictateno()+splitSign);//支付交易序号
				filebuf.append(_dto.getSpaymsgno()+splitSign);//支付报文编号
				filebuf.append(_dto.getDpayentrustdate()+splitSign);//支付委托日期
				filebuf.append(_dto.getSpaysndbnkno()+splitSign);//支付发起行行号
				filebuf.append(_dto.getSbudgettype()+splitSign);//预算种类
				filebuf.append(_dto.getStrimsign()+splitSign);//整理期标志
				filebuf.append(_dto.getSofyear()+splitSign);//所属年度
				filebuf.append(_dto.getFamt()+splitSign);//金额
				filebuf.append(_dto.getIstatinfnum()+splitSign);//明细信息条数
				filebuf.append(_dto.getSstatus()+splitSign);//交易状态
				filebuf.append(_dto.getSaddword()+splitSign);//附言
				filebuf.append(_dto.getDacceptdate()+splitSign);//受理日期
				filebuf.append(_dto.getTsupdate()+splitSign);//更新时间
				filebuf.append(_dto.getSpaymode()+splitSign);//支付方式
				filebuf.append(_dto.getSfilename()+splitSign);//文件名
				filebuf.append(_dto.getSid()+splitSign);//申请划款凭证Id
				filebuf.append(_dto.getSbgttypecode()+splitSign);//预算类型编码
				filebuf.append(_dto.getSbgttypename()+splitSign);//预算类型名称
				filebuf.append(_dto.getSfundtypecode()+splitSign);//资金性质编码
				filebuf.append(_dto.getSfundtypename()+splitSign);//资金性质名称
				filebuf.append(_dto.getSpaytypecode()+splitSign);//支付方式编码
				filebuf.append(_dto.getSpaytypename()+splitSign);//支付方式名称
				filebuf.append(_dto.getSpaybankname()+splitSign);//代理银行名称
				filebuf.append(_dto.getSxpaysndbnkno()+splitSign);//支付发起行行号
				filebuf.append(_dto.getSxpayamt()+splitSign);//汇总清算金额
				filebuf.append(_dto.getShold1()+splitSign);//预留字段1
				filebuf.append(_dto.getShold2()+splitSign);//预留字段2
				filebuf.append(_dto.getSxcleardate()+splitSign);//清算日期
				filebuf.append(_dto.getSagentacctbankname()+splitSign);//原收款银行
				filebuf.append(_dto.getSclearacctbankname()+splitSign);//原付款银行
				filebuf.append(_dto.getSremark()+splitSign);//摘要
				filebuf.append(_dto.getSmoneycorpcode()+splitSign);//金融机构编码
				filebuf.append(_dto.getSadmdivcode()+splitSign);//行政区划代码
				filebuf.append(_dto.getSvtcode()+splitSign);//凭证类型编号
				filebuf.append(_dto.getSbackflag()+splitSign);//支付报文编号
				filebuf.append(_dto.getSisreturn()+splitSign);//支付报文编号
				filebuf.append(_dto.getSbckreason()+splitSign);//支付报文编号
				filebuf.append(_dto.getNbackmoney()+splitSign);//支付报文编号
				filebuf.append(_dto.getSrefundtype()+splitSign);//支付报文编号
				filebuf.append(_dto.getShold3()+splitSign);//支付报文编号
				filebuf.append(_dto.getShold4()+splitSign);//支付报文编号
				filebuf.append(_dto.getSext1()+splitSign);//支付报文编号
				filebuf.append(_dto.getSext2()+splitSign);//支付报文编号
				filebuf.append(_dto.getSext3()+splitSign);//支付报文编号
				filebuf.append(_dto.getSext4()+splitSign);//支付报文编号
				filebuf.append(_dto.getSext5()+splitSign);//支付报文编号
				filebuf.append("\r\n");
				TvPayreckBankBackListDto subpara = new TvPayreckBankBackListDto();
				subpara.setIvousrlno(_dto.getIvousrlno());
				List<TvPayreckBankBackListDto> sublist = CommonFacade.getODB().findRsByDto(subpara);
				for(TvPayreckBankBackListDto dto : sublist){
					filebuf.append(dto.getIvousrlno()+splitSign);//凭证流水号
					filebuf.append(dto.getIseqno()+splitSign);//序号
					filebuf.append(dto.getSorivouno()+splitSign);//原凭证编号
					filebuf.append(dto.getDorivoudate()+splitSign);//原凭证日期
					filebuf.append(dto.getSbdgorgcode()+splitSign);//预算单位代码
					filebuf.append(dto.getSfuncbdgsbtcode()+splitSign);//功能类科目代码
					filebuf.append(dto.getSecnomicsubjectcode()+splitSign);//经济类科目代码
					filebuf.append(dto.getFamt()+splitSign);//金额
					filebuf.append(dto.getSacctprop()+splitSign);//账户性质
					filebuf.append(dto.getTsupdate()+splitSign);//更新时间
					filebuf.append(dto.getSsupdepname()+splitSign);//预算单位名称
					filebuf.append(dto.getSexpfuncname()+splitSign);//支出功能分类科目名称
					filebuf.append(dto.getSpaysummaryname()+splitSign);//摘要名称
					filebuf.append(dto.getShold1()+splitSign);//预留字段1
					filebuf.append(dto.getShold2()+splitSign);//预留字段2
					filebuf.append(dto.getShold3()+splitSign);//预留字段3
					filebuf.append(dto.getShold4()+splitSign);//预留字段4
					filebuf.append(dto.getSvoucherno()+splitSign);
					filebuf.append(dto.getSorivoudetailno()+splitSign);
					filebuf.append(dto.getSid()+splitSign);
					filebuf.append(dto.getSpaydate()+splitSign);
					filebuf.append("\r\n");
				}
			}
			File f = new File(path);
			if (f.exists()) {
				FileUtil.getInstance().deleteFiles(path);
			}
			FileUtil.getInstance().writeFile(path, filebuf.toString());
		}
	}

	/**
	 * 
	 * @param splitSign
	 * @param path
	 * @param string
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws FileOperateException
	 */
	private void payreckbank(String splitSign, String path, String sql)
			throws JAFDatabaseException, ValidateException,
			FileOperateException {
		List<TvPayreckBankDto> list = CommonFacade.getODB()
				.findRsByDtoForWhere(new TvPayreckBankDto(), sql);
		StringBuffer filebuf = new StringBuffer();
		if (list != null && list.size() > 0) {
			String filename = TimeFacade.getCurrentStringTime()
					+ list.get(0).getStrecode() + "260hd.txt";
			path = path + filename;
			for (TvPayreckBankDto _dto : (List<TvPayreckBankDto>) list) {
				TvPayreckBankListDto subPara = new TvPayreckBankListDto();
				subPara.setIvousrlno(_dto.getIvousrlno());
				filebuf.append("**"+_dto.getSbookorgcode()+splitSign);//核算主体代码
				filebuf.append(_dto.getDentrustdate()+splitSign);//委托日期
				filebuf.append(_dto.getSpackno()+splitSign);//包流水号
				filebuf.append(_dto.getSagentbnkcode()+splitSign);//代理银行行号
				filebuf.append(_dto.getStrano()+splitSign);//交易流水号
				filebuf.append(_dto.getIvousrlno()+splitSign);//凭证流水号
				filebuf.append(_dto.getSfinorgcode()+splitSign);//财政机关代码
				filebuf.append(_dto.getStrecode()+splitSign);//国库代码
				filebuf.append(_dto.getSvouno()+splitSign);//凭证编号
				filebuf.append(_dto.getDvoudate()+splitSign);//凭证日期
				filebuf.append(_dto.getSpayeracct()+splitSign);//付款人账号
				filebuf.append(_dto.getSpayername()+splitSign);//付款人名称
				filebuf.append(_dto.getSpayeraddr()+splitSign);//付款人地址
				filebuf.append(_dto.getSpayeeacct()+splitSign);//收款人账号
				filebuf.append(_dto.getSpayeename()+splitSign);//收款人名称
				filebuf.append(_dto.getSpayeeaddr()+splitSign);//收款人地址
				filebuf.append(_dto.getSpayeeopbkno()+splitSign);//收款人开户行行号
				filebuf.append(_dto.getSaddword()+splitSign);//附言
				filebuf.append(_dto.getSbudgettype()+splitSign);//预算种类
				filebuf.append(_dto.getStrimsign()+splitSign);//整理期标志
				filebuf.append(_dto.getSofyear()+splitSign);//所属年度
				filebuf.append(_dto.getFamt()+splitSign);//金额
				filebuf.append(_dto.getIstatinfnum()+splitSign);//明细信息条数
				filebuf.append(_dto.getDacctdate()+splitSign);//账务日期
				filebuf.append(_dto.getSresult()+splitSign);//处理结果
				filebuf.append(_dto.getSdescription()+splitSign);//说明
				filebuf.append(_dto.getSprocstat()+splitSign);//处理状态
				filebuf.append(_dto.getDacceptdate()+splitSign);//受理日期
				filebuf.append(_dto.getTsupdate()+splitSign);//更新时间
				filebuf.append(_dto.getSpaymode()+splitSign);//支付方式
				filebuf.append(_dto.getSfilename()+splitSign);//文件名
				filebuf.append(_dto.getSid()+splitSign);//申请划款凭证Id
				filebuf.append(_dto.getSbgttypecode()+splitSign);//预算类型编码
				filebuf.append(_dto.getSbgttypename()+splitSign);//预算类型名称
				filebuf.append(_dto.getSfundtypecode()+splitSign);//资金性质编码
				filebuf.append(_dto.getSfundtypename()+splitSign);//资金性质名称
				filebuf.append(_dto.getSpaytypecode()+splitSign);//支付方式编码
				filebuf.append(_dto.getSpaytypename()+splitSign);//支付方式名称
				filebuf.append(_dto.getSpaybankname()+splitSign);//代理银行名称
				filebuf.append(_dto.getSxpaysndbnkno()+splitSign);//支付发起行行号
				filebuf.append(_dto.getSxpayamt()+splitSign);//汇总清算金额
				filebuf.append(_dto.getShold1()+splitSign);//预留字段1
				filebuf.append(_dto.getShold2()+splitSign);//预留字段2
				filebuf.append(_dto.getSxcleardate()+splitSign);//清算日期
				filebuf.append(_dto.getSagentacctbankname()+splitSign);//收款银行
				filebuf.append(_dto.getSclearacctbankname()+splitSign);//付款银行
				filebuf.append(_dto.getSmoneycorpcode()+splitSign);//金融机构编码
				filebuf.append(_dto.getSadmdivcode()+splitSign);//行政区划代码
				filebuf.append(_dto.getSvtcode()+splitSign);//凭证类型编号
				filebuf.append("\r\n");
				List<TvPayreckBankListDto> sublist = CommonFacade.getODB().findRsByDto(subPara);
				for(TvPayreckBankListDto dto : sublist){
					filebuf.append(dto.getIvousrlno()+splitSign);//凭证流水号
					filebuf.append(dto.getIseqno()+splitSign);//序号
					filebuf.append(dto.getSbdgorgcode()+splitSign);//预算单位代码
					filebuf.append(dto.getSfuncbdgsbtcode()+splitSign);//功能类科目代码
					filebuf.append(dto.getSecnomicsubjectcode()+splitSign);//经济类科目代码
					filebuf.append(dto.getFamt()+splitSign);//金额
					filebuf.append(dto.getSacctprop()+splitSign);//账户性质
					filebuf.append(dto.getTsupdate()+splitSign);//更新时间
					filebuf.append(dto.getSsupdepname()+splitSign);//预算单位名称
					filebuf.append(dto.getSexpfuncname()+splitSign);//支出功能分类科目名称
					filebuf.append(dto.getSpaysummaryname()+splitSign);//摘要名称
					filebuf.append(dto.getShold1()+splitSign);//预留字段1
					filebuf.append(dto.getShold2()+splitSign);//预留字段2
					filebuf.append(dto.getShold3()+splitSign);//预留字段3
					filebuf.append(dto.getShold4()+splitSign);//预留字段4
					filebuf.append(dto.getSid()+splitSign);
					filebuf.append(dto.getSpaydate()+splitSign);
					filebuf.append(dto.getSvouchern0()+splitSign);//支付凭证单号
					filebuf.append("\r\n");
				}
			}
			File f = new File(path);
			if (f.exists()) {
				FileUtil.getInstance().deleteFiles(path);
			}
			FileUtil.getInstance().writeFile(path, filebuf.toString());
		}
	}

	/**
	 * 实拨资金数据导出
	 * 
	 * @param splitSign
	 * @param path
	 * @param sql
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws FileOperateException
	 */
	private void payout(String splitSign, String path, String sql)
			throws JAFDatabaseException, ValidateException,
			FileOperateException {
		List<TvPayoutmsgmainDto> list = CommonFacade.getODB()
				.findRsByDtoForWhere(new TvPayoutmsgmainDto(), sql);
		StringBuffer filebuf = new StringBuffer();
		if (list != null && list.size() > 0) {
			String filename = TimeFacade.getCurrentStringTime()
					+ list.get(0).getStrecode() + "170hd.txt";
			path = path + filename;
			for (TvPayoutmsgmainDto _dto : (List<TvPayoutmsgmainDto>) list) {
				TvPayoutmsgsubDto subPara = new TvPayoutmsgsubDto();
				subPara.setSbizno(_dto.getSbizno());
				List<TvPayoutmsgsubDto> sublist = CommonFacade.getODB()
						.findRsByDtoWithUR(subPara);

				filebuf.append("**"+_dto.getSbizno());// 发送流水号
				filebuf.append(splitSign);
				filebuf.append(_dto.getSorgcode());// 机构代码
				filebuf.append(splitSign);
				filebuf.append(_dto.getScommitdate());// 委托日期
				filebuf.append(splitSign);
				filebuf.append(_dto.getSaccdate());// 帐务日期
				filebuf.append(splitSign);
				filebuf.append(_dto.getSfilename());// 导入文件名
				filebuf.append(splitSign);
				filebuf.append(_dto.getStrecode());// 国库主体代码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpackageno());// 包流水号
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayunit());// 出票单位
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayeebankno());// 转发银行
				filebuf.append(splitSign);
				filebuf.append(_dto.getSdealno());// 交易流水号
				filebuf.append(splitSign);
				filebuf.append(_dto.getStaxticketno());// 税票号码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSgenticketdate());// 开票日期
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayeracct());// 付款人帐号
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayername());// 付款人名称
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayeraddr());// 付款人名称
				filebuf.append(splitSign);
				filebuf.append(_dto.getNmoney());// 交易金额
				filebuf.append(splitSign);
				filebuf.append(_dto.getSrecbankno());// 收款人开户行名称
				filebuf.append(splitSign);
				filebuf.append(_dto.getSrecacct());// 收款人帐号
				filebuf.append(splitSign);
				filebuf.append(_dto.getSrecname());// 收款人名称
				filebuf.append(splitSign);
				filebuf.append(_dto.getStrimflag());// 调整期标志
				filebuf.append(splitSign);
				filebuf.append(_dto.getSbudgetunitcode());// 预算单位代码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSunitcodename());// 预算单位名称
				filebuf.append(splitSign);
				filebuf.append(_dto.getSofyear());// 所属年度
				filebuf.append(splitSign);
				filebuf.append(_dto.getSbudgettype());// 预算种类
				filebuf.append(splitSign);
				filebuf.append(_dto.getSstatus());// 交易状态
				filebuf.append(splitSign);
				filebuf.append(_dto.getSusercode());// 操作员代码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSaddword());// 附言
				filebuf.append(splitSign);
				filebuf.append(_dto.getSorivouno());// 原支出凭证编号
				filebuf.append(splitSign);
				filebuf.append(_dto.getSorivoudate());// 原支出凭证日期
				filebuf.append(splitSign);
				filebuf.append(_dto.getSbackflag());// 退回标志
				filebuf.append(splitSign);
				filebuf.append(_dto.getSdemo());// 备注
				filebuf.append(splitSign);
				filebuf.append(_dto.getTssysupdate());// 系统更新时间
				filebuf.append(splitSign);
				filebuf.append(_dto.getSid());// 实拨拨款凭证Id
				filebuf.append(splitSign);
				filebuf.append(_dto.getSrecbankname());// 收款银行行名
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayerbankname());// 付款人银行行名
				filebuf.append(splitSign);
				filebuf.append(_dto.getSfundtypecode());// 资金性质编码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSfundtypename());// 资金性质名称
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpaytypecode());// 支付方式编码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpaytypename());// 支付方式名称
				filebuf.append(splitSign);
				filebuf.append(_dto.getSclearbankcode());// 人民银行编码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSclearbankname());// 人民银行名称
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpaysummarycode());// 用途编码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpaysummaryname());// 用途名称
				filebuf.append(splitSign);
				filebuf.append(_dto.getSxagentbusinessno());// 银行交易流水号
				filebuf.append(splitSign);
				filebuf.append(_dto.getSxpayamt());// 实际支付金额
				filebuf.append(splitSign);
				filebuf.append(_dto.getSxpaydate());// 支付日期
				filebuf.append(splitSign);
				filebuf.append(_dto.getSifmatch());// 是否需要补录行号(0--不补录，1--补录)
				filebuf.append(splitSign);
				filebuf.append(_dto.getShold1());// 预留字段1
				filebuf.append(splitSign);
				filebuf.append(_dto.getShold2());// 预留字段2
				filebuf.append(splitSign);
				filebuf.append(_dto.getSinputrecbankno());// 补录的行号
				filebuf.append(splitSign);
				filebuf.append(_dto.getSinputusercode());// 补录人
				filebuf.append(splitSign);
				filebuf.append(_dto.getSchecksercode());// 复核人
				filebuf.append(splitSign);
				filebuf.append(_dto.getSinputrecbankname());// 补录行名
				filebuf.append(splitSign);
				filebuf.append(_dto.getScheckstatus());// 补录复核状态
				filebuf.append(splitSign);
				filebuf.append(_dto.getSbusinesstypecode());
				filebuf.append(splitSign);
				filebuf.append(_dto.getSbusinesstypename());
				filebuf.append(splitSign);

				if (DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto
						.getSstatus()))
					filebuf.append("1");// 处理结果1成功，0失败
				else
					filebuf.append("0");// 处理结果1成功，0失败
				filebuf.append(splitSign);
				filebuf.append(_dto.getSdemo() + splitSign);// 原因
				if ("1".equals(_dto.getSbackflag())) {
					filebuf.append("实拨资金退款" + splitSign);
					filebuf.append(_dto.getShold2());
				} else {
					filebuf.append("实拨资金");
				}
				filebuf.append("\r\n");
				for (TvPayoutmsgsubDto subdto : sublist) {
					filebuf.append(subdto.getSbizno());// 发送流水号
					filebuf.append(splitSign);
					filebuf.append(subdto.getSseqno());//
					filebuf.append(splitSign);
					filebuf.append(subdto.getSaccdate());// 帐务日期
					filebuf.append(splitSign);
					filebuf.append(subdto.getSecnomicsubjectcode());// 经济科目代码
					filebuf.append(splitSign);
					filebuf.append(subdto.getSbudgetprjcode());// 预算项目代码
					filebuf.append(splitSign);
					filebuf.append(subdto.getNmoney());// 交易金额
					filebuf.append(splitSign);
					filebuf.append(subdto.getSfunsubjectcode());// 功能科目代码
					filebuf.append(splitSign);
					filebuf.append(subdto.getTssysupdate());// 系统更新时间
					filebuf.append(splitSign);
					filebuf.append(subdto.getStaxticketno());// 拨款凭证Id
					filebuf.append(splitSign);
					filebuf.append(subdto.getSbgttypecode());// 预算类型编码
					filebuf.append(splitSign);
					filebuf.append(subdto.getSbgttypename());// 预算类型名称
					filebuf.append(splitSign);
					filebuf.append(subdto.getSprocatcode());// 收支管理编码
					filebuf.append(splitSign);
					filebuf.append(subdto.getSprocatname());// 收支管理名称
					filebuf.append(splitSign);
					filebuf.append(subdto.getSagencycode());// 预算单位编码
					filebuf.append(splitSign);
					filebuf.append(subdto.getSagencyname());// 预算单位名称
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpfuncname());// 支出功能分类科目名称
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpfunccode1());// 支出功能分类科目类编码
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpfuncname1());// 支出功能分类科目类名称
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpfunccode2());// 支出功能分类科目款编码
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpfuncname2());// 支出功能分类科目款名称
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpfunccode3());// 支出功能分类科目项编码
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpfuncname3());// 支出功能分类科目项名称
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpecocode());// 支出经济分类科目编码
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpeconame());// 支出经济分类科目名称
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpecocode1());// 支出经济分类科目类编码
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpeconame1());// 支出经济分类科目类名称
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpecocode2());// 支出经济分类科目款编码
					filebuf.append(splitSign);
					filebuf.append(subdto.getSexpeconame2());// 支出经济分类科目款名称
					filebuf.append(splitSign);
					filebuf.append(subdto.getShold1());// 预留字段1
					filebuf.append(splitSign);
					filebuf.append(subdto.getShold2());// 预留字段2
					filebuf.append(splitSign);
					filebuf.append(subdto.getShold3());// 预留字段3
					filebuf.append(splitSign);
					filebuf.append(subdto.getShold4());// 预留字段4
					filebuf.append(splitSign);
					filebuf.append(subdto.getSpayeeacctno());
					filebuf.append(splitSign);
					filebuf.append(subdto.getSpayeeacctname());
					filebuf.append(splitSign);
					filebuf.append(subdto.getSpayeeacctbankname());
					filebuf.append(splitSign);
					filebuf.append(subdto.getSpaysummarycode());
					filebuf.append(splitSign);
					filebuf.append(subdto.getSpaysummaryname());
					filebuf.append(splitSign);
					filebuf.append(subdto.getSxpayamt());
					filebuf.append(splitSign);
					filebuf.append(subdto.getSxpaydate());
					filebuf.append(splitSign);
					filebuf.append(subdto.getSxagentbusinessno());
					filebuf.append(splitSign);
					filebuf.append(subdto.getSxaddword());
					filebuf.append(splitSign);
					filebuf.append(subdto.getSid());
					filebuf.append(splitSign);
					filebuf.append("\r\n");
				}
			}
			File f = new File(path);
			if (f.exists()) {
				FileUtil.getInstance().deleteFiles(path);
			}
			FileUtil.getInstance().writeFile(path, filebuf.toString());
		}
	}

	/**
	 * 授权支付额度数据导出
	 * 
	 * @param splitSign
	 * @param path
	 * @param sql
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws FileOperateException
	 */
	private void grantPlan(String splitSign, String path, String sql)
			throws JAFDatabaseException, ValidateException,
			FileOperateException {
		List<TvGrantpaymsgmainDto> listgrant = CommonFacade.getODB()
				.findRsByDtoForWhere(new TvGrantpaymsgmainDto(), sql);
		StringBuffer filebuf = new StringBuffer();
		if (listgrant != null && listgrant.size() > 0) {
			String filename = TimeFacade.getCurrentStringTime()
					+ listgrant.get(0).getStrecode() + "020hd.txt";
			path = path + filename;
			for (TvGrantpaymsgmainDto _dto : (List<TvGrantpaymsgmainDto>) listgrant) {
				TvGrantpaymsgsubDto subPara = new TvGrantpaymsgsubDto();
				subPara.setSorgcode(_dto.getSorgcode());
				subPara.setSlimitid(_dto.getSlimitid());
				subPara.setSofyear(_dto.getSofyear());
				subPara.setSfilename(_dto.getSfilename());
				subPara.setSpackageno(_dto.getSpackageno());
				List sublist = CommonFacade.getODB().findRsByDtoWithUR(subPara);
				filebuf.append("**"+_dto.getIvousrlno());// 凭证流水号
				filebuf.append(splitSign);
				filebuf.append(_dto.getSorgcode());// 机构代码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSlimitid());// 批次号
				filebuf.append(splitSign);
				filebuf.append(_dto.getScommitdate());// 委托日期
				filebuf.append(splitSign);
				filebuf.append(_dto.getSaccdate());// 帐务日期
				filebuf.append(splitSign);
				filebuf.append(_dto.getSfilename());// 导入文件名
				filebuf.append(splitSign);
				filebuf.append(_dto.getStrecode());// 国库主体代码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpackageno());// 包流水号
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayunit());// 出票单位
				filebuf.append(splitSign);
				filebuf.append(_dto.getStransbankcode());// 转发银行
				filebuf.append(splitSign);
				filebuf.append(_dto.getNmoney());// 交易金额
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpackageticketno());// 分包税票号码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSgenticketdate());// 开票日期
				filebuf.append(splitSign);
				filebuf.append(_dto.getSofyear());// 所属年度
				filebuf.append(splitSign);
				filebuf.append(_dto.getSofmonth());// 所属月份
				filebuf.append(splitSign);
				filebuf.append(_dto.getStransactunit());// 经办单位
				filebuf.append(splitSign);
				filebuf.append(_dto.getSamttype());// 额度种类
				filebuf.append(splitSign);
				filebuf.append(_dto.getSusercode());// 操作员代码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSstatus());// 销号状态
				filebuf.append(splitSign);
				filebuf.append(_dto.getSdemo());// 备注
				filebuf.append(splitSign);
				filebuf.append(_dto.getTssysupdate());// 系统更新时间
				filebuf.append(splitSign);
				filebuf.append(_dto.getSdealno());// 交易流水号
				filebuf.append(splitSign);
				filebuf.append(_dto.getSbudgetunitcode());// 预算内单位代码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSbudgettype());// 预算种类
				filebuf.append(splitSign);
				filebuf.append(_dto.getSid());// 清算额度通知单Id
				filebuf.append(splitSign);
				filebuf.append(_dto.getSdeptnum());// 一级预算单位数量
				filebuf.append(splitSign);
				filebuf.append(_dto.getSfundtypecode());// 资金性质编码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSfundtypename());// 资金性质名称
				filebuf.append(splitSign);
				filebuf.append(_dto.getSclearbankcode());// 人民银行编码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSclearbankname());// 人民银行名称
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpaybankcode());// 代理银行编码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpaybankname());// 代理银行名称
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpaybankno());// 代理银行行号
				filebuf.append(splitSign);
				filebuf.append(_dto.getSxacctdate());// 处理日期
				filebuf.append(splitSign);
				filebuf.append(_dto.getShold1());// 预留字段1
				filebuf.append(splitSign);
				filebuf.append(_dto.getShold2());// 预留字段2
				filebuf.append(splitSign);
				filebuf.append(_dto.getSclearvoucherno());
				filebuf.append(splitSign);
				filebuf.append(_dto.getNxallamt());
				filebuf.append(splitSign);
				if (DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto
						.getSstatus()))
					filebuf.append("1");// 处理结果1成功，0失败
				else
					filebuf.append("0");// 处理结果1成功，0失败
				filebuf.append(splitSign);
				if ("80000".equals(_dto.getSstatus()))
					filebuf.append("成功");// 说明
				else if ("80001".equals(_dto.getSstatus()))
					filebuf.append("失败");// 说明
				else if ("80002".equals(_dto.getSstatus()))
					filebuf.append("处理中");// 说明
				else if ("80008".equals(_dto.getSstatus()))
					filebuf.append("未发送");// 说明
				else if ("80009".equals(_dto.getSstatus()))
					filebuf.append("待处理");// 说明
				else
					filebuf.append(_dto.getSdemo());// 说明
				filebuf.append("\r\n");
				// 子表信息
				for (TvGrantpaymsgsubDto sub : (List<TvGrantpaymsgsubDto>) sublist) {
					filebuf.append(sub.getIvousrlno());// 凭证流水号
					filebuf.append(splitSign);
					filebuf.append(sub.getIdetailseqno());// 明细序号
					filebuf.append(splitSign);
					filebuf.append(sub.getSorgcode());// 机构代码
					filebuf.append(splitSign);
					filebuf.append(sub.getSfilename());// 导入文件名
					filebuf.append(splitSign);
					filebuf.append(sub.getSlimitid());// 批次号
					filebuf.append(splitSign);
					filebuf.append(sub.getSofyear());// 所属年度
					filebuf.append(splitSign);
					filebuf.append(sub.getSline());// 明细号
					filebuf.append(splitSign);
					filebuf.append(sub.getSpackageno());// 包流水号
					filebuf.append(splitSign);
					filebuf.append(sub.getSdealno());// 交易流水号
					filebuf.append(splitSign);
					filebuf.append(sub.getSpackageticketno());// 分包税票号码
					filebuf.append(splitSign);
					filebuf.append(sub.getSbudgetunitcode());// 预算内单位代码
					filebuf.append(splitSign);
					filebuf.append(sub.getSbudgettype());// 预算种类
					filebuf.append(splitSign);
					filebuf.append(sub.getSfunsubjectcode());// 功能科目代码
					filebuf.append(splitSign);
					filebuf.append(sub.getSecosubjectcode());// 经济科目代码
					filebuf.append(splitSign);
					filebuf.append(sub.getNmoney());// 发生额
					filebuf.append(splitSign);
					filebuf.append(sub.getSaccattrib());// 帐户性质
					filebuf.append(splitSign);
					filebuf.append(sub.getSaccdate());// 帐务日期
					filebuf.append(splitSign);
					filebuf.append(sub.getSstatus());// 交易状态
					filebuf.append(splitSign);
					filebuf.append(sub.getSusercode());// 操作员代码
					filebuf.append(splitSign);
					filebuf.append(sub.getTssysupdate());// 系统更新时间
					filebuf.append(splitSign);
					filebuf.append(sub.getSdemo());// 备注
					filebuf.append(splitSign);
					filebuf.append(sub.getSsupdepname());// 一级预算单位名称
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfuncname());// 支出功能分类科目名称
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfunccode1());// 支出功能分类科目类编码
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfuncname1());// 支出功能分类科目类名称
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfunccode2());// 支出功能分类科目款编码
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfuncname2());// 支出功能分类科目款名称
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfunccode3());// 支出功能分类科目项编码
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfuncname3());// 支出功能分类科目项名称
					filebuf.append(splitSign);
					filebuf.append(sub.getSprocatcode());// 收支管理编码
					filebuf.append(splitSign);
					filebuf.append(sub.getShold1());// 预留字段1
					filebuf.append(splitSign);
					filebuf.append(sub.getShold2());// 预留字段2
					filebuf.append(splitSign);
					filebuf.append(sub.getShold3());// 预留字段3
					filebuf.append(splitSign);
					filebuf.append(sub.getShold4());// 预留字段4
					filebuf.append(splitSign);
					filebuf.append(sub.getSid());
					filebuf.append(splitSign);
					filebuf.append("\r\n");
				}
			}
			File f = new File(path);
			if (f.exists()) {
				FileUtil.getInstance().deleteFiles(path);
			}
			FileUtil.getInstance().writeFile(path, filebuf.toString());
		}
	}

	/**
	 * 直接支付额度数据导出
	 * 
	 * @param splitSign
	 * @param path
	 * @param sql
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws FileOperateException
	 */
	private void directPlan(String splitSign, String path, String sql)
			throws JAFDatabaseException, ValidateException,
			FileOperateException {
		List<TvDirectpaymsgmainDto> listdirect = CommonFacade.getODB()
				.findRsByDtoForWhere(new TvDirectpaymsgmainDto(), sql);
		StringBuffer filebuf = new StringBuffer();
		if (listdirect != null && listdirect.size() > 0) {
			String filename = TimeFacade.getCurrentStringTime()
					+ listdirect.get(0).getStrecode() + "010hd.txt";
			path = path + filename;
			for (int i = 0; i < listdirect.size(); i++) {
				TvDirectpaymsgmainDto _dto = listdirect.get(i);
				TvDirectpaymsgsubDto subPara = new TvDirectpaymsgsubDto();
				subPara.setSorgcode(_dto.getSorgcode());
				subPara.setSofyear(_dto.getSofyear());
				subPara.setStaxticketno(_dto.getStaxticketno());
				subPara.setSpackageno(_dto.getSpackageno());
				List<TvDirectpaymsgsubDto> sublist = CommonFacade.getODB()
						.findRsByDtoWithUR(subPara);
				filebuf.append("**"+_dto.getIvousrlno());// 凭证流水号
				filebuf.append(splitSign);
				filebuf.append(_dto.getSorgcode());// 机构代码
				filebuf.append(splitSign);
				filebuf.append(_dto.getScommitdate());// 委托日期
				filebuf.append(splitSign);
				filebuf.append(_dto.getSaccdate());// 帐务日期
				filebuf.append(splitSign);
				filebuf.append(_dto.getSfilename());// 导入文件名
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpackageno());// 包流水号
				filebuf.append(splitSign);
				filebuf.append(_dto.getStrecode());// 国库主体代码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayunit());// 出票单位
				filebuf.append(splitSign);
				filebuf.append(_dto.getStransbankcode());// 转发银行
				filebuf.append(splitSign);
				filebuf.append(_dto.getSdealno());// 交易流水号
				filebuf.append(splitSign);
				filebuf.append(_dto.getNmoney());// 交易金额
				filebuf.append(splitSign);
				filebuf.append(_dto.getStaxticketno());// 税票号码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpackageticketno());// 分包税票号码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSgenticketdate());// 开票日期
				filebuf.append(splitSign);
				filebuf.append(_dto.getSbudgettype());// 预算种类
				filebuf.append(splitSign);
				filebuf.append(_dto.getSofyear());// 所属年度
				filebuf.append(splitSign);
				filebuf.append(_dto.getStransactunit());// 经办单位
				filebuf.append(splitSign);
				filebuf.append(_dto.getSamttype());// 额度种类
				filebuf.append(splitSign);
				filebuf.append(_dto.getSstatus());// 交易状态
				filebuf.append(splitSign);
				filebuf.append(_dto.getSusercode());// 操作员代码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSdemo());// 备注
				filebuf.append(splitSign);
				filebuf.append(_dto.getTssysupdate());// 系统更新时间
				filebuf.append(splitSign);
				filebuf.append(_dto.getSid());// 直接支付汇总清算额度通知单
				filebuf.append(splitSign);
				filebuf.append(_dto.getSfundtypecode());// 资金性质编码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSfundtypename());// 资金性质名称
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayeeacctno());// 收款人账号
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayeeacctname());// 收款人名称
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayeeacctbankname());// 收款人银行名称
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayacctno());// 付款人账号
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayacctname());// 付款人名称
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayacctbankname());// 付款人银行名称
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpaybankcode());// 代理银行编码
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpaybankname());// 代理银行名称
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpaybankno());// 代理银行行号
				filebuf.append(splitSign);
				filebuf.append(_dto.getSxacctdate());// 处理日期
				filebuf.append(splitSign);
				filebuf.append(_dto.getShold1());// 预留字段1
				filebuf.append(splitSign);
				filebuf.append(_dto.getShold2());// 预留字段2
				filebuf.append(splitSign);
				filebuf.append(_dto.getSpayvoucherno());
				filebuf.append(splitSign);
				filebuf.append(_dto.getNxallamt());
				filebuf.append(splitSign);

				if (DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto
						.getSstatus()))
					filebuf.append("1");// 处理结果1成功，0失败
				else
					filebuf.append("0");// 处理结果1成功，0失败
				filebuf.append(splitSign);
				if ("80000".equals(_dto.getSstatus()))
					filebuf.append("成功");// 说明
				else if ("80001".equals(_dto.getSstatus()))
					filebuf.append("失败");// 说明
				else if ("80002".equals(_dto.getSstatus()))
					filebuf.append("处理中");// 说明
				else if ("80008".equals(_dto.getSstatus()))
					filebuf.append("未发送");// 说明
				else if ("80009".equals(_dto.getSstatus()))
					filebuf.append("待处理");// 说明
				else
					filebuf.append(_dto.getSdemo());// 说明
				filebuf.append("\r\n");
				// 子表信息
				for (TvDirectpaymsgsubDto sub : (List<TvDirectpaymsgsubDto>) sublist) {
					// filebuf.append(sub.getSfunsubjectcode());//功能科目代码
					// filebuf.append(splitSign);
					// filebuf.append(sub.getSecosubjectcode());//经济科目代码
					// filebuf.append(splitSign);
					// filebuf.append(sub.getSbudgetunitcode());//预算单位代码
					// filebuf.append(splitSign);
					// filebuf.append(sub.getNmoney());//发生额
					// filebuf.append(splitSign);
					filebuf.append(sub.getIvousrlno());// 凭证流水号
					filebuf.append(splitSign);
					filebuf.append(sub.getIdetailseqno());// 明细序号
					filebuf.append(splitSign);
					filebuf.append(sub.getSorgcode());// 机构代码
					filebuf.append(splitSign);
					filebuf.append(sub.getSpackageno());// 包流水号
					filebuf.append(splitSign);
					filebuf.append(sub.getSbudgetunitcode());// 预算内单位代码
					filebuf.append(splitSign);
					filebuf.append(sub.getStaxticketno());// 税票号码
					filebuf.append(splitSign);
					filebuf.append(sub.getSline());// 支付明细
					filebuf.append(splitSign);
					filebuf.append(sub.getSofyear());// 所属年度
					filebuf.append(splitSign);
					filebuf.append(sub.getInoafterpackage());// 分包后序号
					filebuf.append(splitSign);
					filebuf.append(sub.getSfunsubjectcode());// 功能科目代码
					filebuf.append(splitSign);
					filebuf.append(sub.getSecosubjectcode());// 经济科目代码
					filebuf.append(splitSign);
					filebuf.append(sub.getNmoney());// 发生额
					filebuf.append(splitSign);
					filebuf.append(sub.getSaccdate());// 帐务日期
					filebuf.append(splitSign);
					filebuf.append(sub.getSusercode());// 操作员代码
					filebuf.append(splitSign);
					filebuf.append(sub.getSdemo());// 备注
					filebuf.append(splitSign);
					filebuf.append(sub.getTssysupdate());// 系统更新时间
					filebuf.append(splitSign);
					filebuf.append(sub.getSagencycode());// 预算单位编码
					filebuf.append(splitSign);
					filebuf.append(sub.getSagencyname());// 预算单位名称
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfuncname());// 支出功能分类科目名称
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfunccode1());// 支出功能分类科目类编码
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfuncname1());// 支出功能分类科目类名称
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfunccode2());// 支出功能分类科目款编码
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfuncname2());// 支出功能分类科目款名称
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfunccode3());// 支出功能分类科目项编码
					filebuf.append(splitSign);
					filebuf.append(sub.getSexpfuncname3());// 支出功能分类科目项名称
					filebuf.append(splitSign);
					filebuf.append(sub.getSprocatcode());// 收支管理编码
					filebuf.append(splitSign);
					filebuf.append(sub.getSprocatname());// 收支管理名称
					filebuf.append(splitSign);
					filebuf.append(sub.getShold1());// 预留字段1
					filebuf.append(splitSign);
					filebuf.append(sub.getShold2());// 预留字段2
					filebuf.append(splitSign);
					filebuf.append(sub.getShold3());// 预留字段3
					filebuf.append(splitSign);
					filebuf.append(sub.getShold4());// 预留字段4
					filebuf.append(splitSign);
					filebuf.append(sub.getSid());//
					filebuf.append("\r\n");
				}
			}
			File f = new File(path);
			if (f.exists()) {
				FileUtil.getInstance().deleteFiles(path);
			}
			FileUtil.getInstance().writeFile(path, filebuf.toString());
		}
	}
	
	public static void main(String[] a)
	{
		System.out.println(count++);
		System.out.println(count++);
		System.out.println(count++);
		System.out.println(count++);
		System.out.println(count++);
		
	}
}

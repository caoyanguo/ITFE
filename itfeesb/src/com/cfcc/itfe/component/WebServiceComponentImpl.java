package com.cfcc.itfe.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.interceptor.IpAddressInInterceptor;
import com.cfcc.itfe.persistence.dto.TsStamptypeDto;
import com.cfcc.itfe.persistence.dto.TsSyslogDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

@WebService(endpointInterface = "com.cfcc.itfe.component.WebServiceComponent", serviceName = "WebServiceComponent")
public class WebServiceComponentImpl implements WebServiceComponent {
	private static Log logger = LogFactory.getLog(VoucherTimerReturnSuccess.class);
	
	public String sayHi(String text) {
		return "Hello " + text +" ["+ getClientIP()+"]";
	}

	public byte[] readReport(String finorgcode, String reportdate,
			String biztype, String msgid) throws Exception {
		String msg = "[readReport]读取财政机构[" + finorgcode + "]报表日期["+ reportdate + "]业务类型[" + biztype + "]报文标识号[" + msgid+ "]的数据";
		logger.debug("----" + msg +"开始----");
		if(ITFECommonConstant.PUBLICPARAM.contains(",reportsendfin=false,"))
			return new String("").getBytes("GBK");
		TvRecvlogDto selectrecvlog = new TvRecvlogDto();
		selectrecvlog.setSbillorg(finorgcode);//财政机构代码
		selectrecvlog.setSdate(reportdate);//报表日期
		selectrecvlog.setSoperationtypecode(biztype);//业务类型
		selectrecvlog.setSseq(msgid);//报文标识号
		try {
			List recvLogList = CommonFacade.getODB().findRsByDto(selectrecvlog);
			if(recvLogList == null || recvLogList.size()==0){
				return null;
			}
			TvRecvlogDto recvlog = (TvRecvlogDto)recvLogList.get(0);
			String filepath = recvlog.getStitle();
			String reportXml =FileUtil.getInstance().readFile(filepath);
			//记录财政读取日志
			TsSyslogDto syslog = new TsSyslogDto();
			syslog.setSorgcode(recvlog.getSrecvorgcode());
			syslog.setSoperationtypecode(biztype);
			syslog.setSdate(reportdate);
			syslog.setSdemo(getClientIP());//读取方财政IP地址
			syslog.setSusercode(finorgcode);//设置读取的哪个财政的数据
			syslog.setSoperationdesc(msgid);
			syslog.setStime(TSystemFacade.getDBSystemTime());
			DatabaseFacade.getDb().create(syslog);
			logger.debug("----" + msg +"结束----");
			return reportXml.getBytes("GBK");
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new Exception("查询报文接收日志出现异常："+e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new Exception("查询报文接收日志出现异常："+e);
		} catch (FileOperateException e) {
			logger.error(e);
			throw new Exception("读取报文出现异常："+e);
		}
	}

	public void readReportReceipt(String finorgcode, String reportdate,
			String biztype, String msgid, String status) {
		
	}

	public List readReportMsgID(String finorgcode, String reportdate,
			String biztype) throws Exception  {
		SQLExecutor selectExce=null;
		SQLResults result = null;
		try {
			selectExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			//选择条件下的报文标识号（去重）
			String selectSql;
			if (biztype.equals("1104")) {
				selectSql = "SELECT DISTINCT S_SEQ FROM TV_RECVLOG WHERE S_DATE = ?  AND S_OPERATIONTYPECODE = ? ";
				selectExce.addParam(reportdate);//报表日期
				selectExce.addParam(biztype);//业务类型
			} else{
				selectSql = "SELECT DISTINCT S_SEQ FROM TV_RECVLOG WHERE S_DATE = ? AND S_BILLORG = ? AND S_OPERATIONTYPECODE = ? ";
				selectExce.addParam(reportdate);//报表日期
				selectExce.addParam(finorgcode);//财政机构代码
				selectExce.addParam(biztype);//业务类型
			}
			result = selectExce.runQueryCloseCon(selectSql);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new Exception("查询报文接收日志出现异常："+e);
		}finally{
			if(selectExce != null){
				selectExce.closeConnection();
			}
		}
		int count = result.getRowCount();
		//报文标识号集合
		List<String> msgidList = new ArrayList<String>();
		for(int i = 0; i < count; i++){
			msgidList.add(result.getString(i, 0));
		}
		return msgidList;
	}
	
	public List readReportMsgIDByUser(String finorgcode, String reportdate,
			String biztype, String usercode, String password) throws Exception  {
		String msg = "[readReportMsgIDByUser]读取财政机构[" + finorgcode + "]报表日期[" + reportdate + "]业务类型[" + biztype + "]的报文标识号";
		logger.debug("----" + msg + "开始----");
		//校验用户合法性
		if(StringUtils.isBlank(usercode)){
			throw new Exception("用户名不能为空！");
		}
		if(StringUtils.isBlank(password)){
			throw new Exception("密码不能为空！");
		}
		TsStamptypeDto userdto = new TsStamptypeDto();
		userdto.setSstamptypecode(finorgcode);
		userdto.setSstamptypename(usercode);
		userdto.setSstamptypeid(password);
		List list = CommonFacade.getODB().findRsByDto(userdto);
		if(list==null || list.size() == 0){
			logger.error("用户不存在");
			throw new Exception("用户不存在");
		}
		SQLExecutor selectExce=null;
		SQLResults result = null;
		try {
			selectExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			//选择条件下的报文标识号（去重）
			String selectSql = "SELECT DISTINCT S_SEQ FROM TV_RECVLOG WHERE S_DATE = ? AND S_BILLORG = ? AND S_OPERATIONTYPECODE = ? ";
			selectExce.addParam(reportdate);//报表日期
			selectExce.addParam(finorgcode);//财政机构代码
			selectExce.addParam(biztype);//业务类型
			result = selectExce.runQueryCloseCon(selectSql);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new Exception("查询报文接收日志出现异常："+e);
		}finally{
			if(selectExce != null){
				selectExce.closeConnection();
			}
		}
		int count = result.getRowCount();
		//报文标识号集合
		List<String> msgidList = new ArrayList<String>();
		for(int i = 0; i < count; i++){
			msgidList.add(result.getString(i, 0));
		}
		logger.debug("----" + msg + "结束----");
		return msgidList;
	}
	
	private String getClientIP(){
		return IpAddressInInterceptor.ip.get();
	}
	/*
	 * biztype业务类型
	 * creatdate接收日期
	 * finorgcode财政机关代码
	 * reportdate报表日期
	 * extparam扩展备用参数
	 */
	public byte[] readTipsData(String biztype,String creatdate,String finorgcode, String reportdate,Map extparam) throws Exception {
		if(ITFECommonConstant.PUBLICPARAM.contains(",readTipsData=false,"))
			return new String("").getBytes("GBK");
		try {
			if(creatdate==null||"".equals(creatdate))
				creatdate = TimeFacade.getCurrentStringTime();
			String filepath;
			if(isWin())
				filepath = "D:/itfeext/czftp/"+creatdate+"/";
			else
				filepath = "/itfeext/czftp/"+creatdate+"/";
			String filename = biztype+"_"+creatdate+"_"+finorgcode+"_"+reportdate;
			List<String> filenames = FileUtil.getInstance().listFileRelpath(filepath);
			String getfilename = null;
			if(filenames!=null&&filenames.size()>0)
			{
				for(String temp:filenames)
				{
					if(temp.contains(filename))
					{
						getfilename = temp;
						break;
					}
				}
			}
			String reportXml =null;
			logger.debug(getfilename+"[readTipsData]读取财政机构[" + finorgcode + "]报表日期["+ reportdate + "]业务类型[" + biztype + "]接收日期[" +creatdate+ "]的数据");
			if(getfilename==null)
				reportXml = "";
			else
				reportXml = FileUtil.getInstance().readFile(filepath+getfilename);
			//记录财政读取日志
//			TsSyslogDto syslog = new TsSyslogDto();
//			syslog.setSorgcode(recvlog.getSrecvorgcode());
//			syslog.setSoperationtypecode(biztype);
//			syslog.setSdate(reportdate);
//			syslog.setSdemo(getClientIP());//读取方财政IP地址
//			syslog.setSusercode(finorgcode);//设置读取的哪个财政的数据
//			syslog.setSoperationdesc("财政webservice读数");
//			syslog.setStime(TSystemFacade.getDBSystemTime());
//			DatabaseFacade.getDb().create(syslog);
			return reportXml.getBytes("GBK");
		}catch (Exception e) {
			logger.error(e);
			throw new Exception("读取报文出现异常："+e);
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
}

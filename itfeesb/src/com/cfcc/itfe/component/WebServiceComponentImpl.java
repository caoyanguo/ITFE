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
		String msg = "[readReport]��ȡ��������[" + finorgcode + "]��������["+ reportdate + "]ҵ������[" + biztype + "]���ı�ʶ��[" + msgid+ "]������";
		logger.debug("----" + msg +"��ʼ----");
		if(ITFECommonConstant.PUBLICPARAM.contains(",reportsendfin=false,"))
			return new String("").getBytes("GBK");
		TvRecvlogDto selectrecvlog = new TvRecvlogDto();
		selectrecvlog.setSbillorg(finorgcode);//������������
		selectrecvlog.setSdate(reportdate);//��������
		selectrecvlog.setSoperationtypecode(biztype);//ҵ������
		selectrecvlog.setSseq(msgid);//���ı�ʶ��
		try {
			List recvLogList = CommonFacade.getODB().findRsByDto(selectrecvlog);
			if(recvLogList == null || recvLogList.size()==0){
				return null;
			}
			TvRecvlogDto recvlog = (TvRecvlogDto)recvLogList.get(0);
			String filepath = recvlog.getStitle();
			String reportXml =FileUtil.getInstance().readFile(filepath);
			//��¼������ȡ��־
			TsSyslogDto syslog = new TsSyslogDto();
			syslog.setSorgcode(recvlog.getSrecvorgcode());
			syslog.setSoperationtypecode(biztype);
			syslog.setSdate(reportdate);
			syslog.setSdemo(getClientIP());//��ȡ������IP��ַ
			syslog.setSusercode(finorgcode);//���ö�ȡ���ĸ�����������
			syslog.setSoperationdesc(msgid);
			syslog.setStime(TSystemFacade.getDBSystemTime());
			DatabaseFacade.getDb().create(syslog);
			logger.debug("----" + msg +"����----");
			return reportXml.getBytes("GBK");
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new Exception("��ѯ���Ľ�����־�����쳣��"+e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new Exception("��ѯ���Ľ�����־�����쳣��"+e);
		} catch (FileOperateException e) {
			logger.error(e);
			throw new Exception("��ȡ���ĳ����쳣��"+e);
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
			//ѡ�������µı��ı�ʶ�ţ�ȥ�أ�
			String selectSql;
			if (biztype.equals("1104")) {
				selectSql = "SELECT DISTINCT S_SEQ FROM TV_RECVLOG WHERE S_DATE = ?  AND S_OPERATIONTYPECODE = ? ";
				selectExce.addParam(reportdate);//��������
				selectExce.addParam(biztype);//ҵ������
			} else{
				selectSql = "SELECT DISTINCT S_SEQ FROM TV_RECVLOG WHERE S_DATE = ? AND S_BILLORG = ? AND S_OPERATIONTYPECODE = ? ";
				selectExce.addParam(reportdate);//��������
				selectExce.addParam(finorgcode);//������������
				selectExce.addParam(biztype);//ҵ������
			}
			result = selectExce.runQueryCloseCon(selectSql);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new Exception("��ѯ���Ľ�����־�����쳣��"+e);
		}finally{
			if(selectExce != null){
				selectExce.closeConnection();
			}
		}
		int count = result.getRowCount();
		//���ı�ʶ�ż���
		List<String> msgidList = new ArrayList<String>();
		for(int i = 0; i < count; i++){
			msgidList.add(result.getString(i, 0));
		}
		return msgidList;
	}
	
	public List readReportMsgIDByUser(String finorgcode, String reportdate,
			String biztype, String usercode, String password) throws Exception  {
		String msg = "[readReportMsgIDByUser]��ȡ��������[" + finorgcode + "]��������[" + reportdate + "]ҵ������[" + biztype + "]�ı��ı�ʶ��";
		logger.debug("----" + msg + "��ʼ----");
		//У���û��Ϸ���
		if(StringUtils.isBlank(usercode)){
			throw new Exception("�û�������Ϊ�գ�");
		}
		if(StringUtils.isBlank(password)){
			throw new Exception("���벻��Ϊ�գ�");
		}
		TsStamptypeDto userdto = new TsStamptypeDto();
		userdto.setSstamptypecode(finorgcode);
		userdto.setSstamptypename(usercode);
		userdto.setSstamptypeid(password);
		List list = CommonFacade.getODB().findRsByDto(userdto);
		if(list==null || list.size() == 0){
			logger.error("�û�������");
			throw new Exception("�û�������");
		}
		SQLExecutor selectExce=null;
		SQLResults result = null;
		try {
			selectExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			//ѡ�������µı��ı�ʶ�ţ�ȥ�أ�
			String selectSql = "SELECT DISTINCT S_SEQ FROM TV_RECVLOG WHERE S_DATE = ? AND S_BILLORG = ? AND S_OPERATIONTYPECODE = ? ";
			selectExce.addParam(reportdate);//��������
			selectExce.addParam(finorgcode);//������������
			selectExce.addParam(biztype);//ҵ������
			result = selectExce.runQueryCloseCon(selectSql);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new Exception("��ѯ���Ľ�����־�����쳣��"+e);
		}finally{
			if(selectExce != null){
				selectExce.closeConnection();
			}
		}
		int count = result.getRowCount();
		//���ı�ʶ�ż���
		List<String> msgidList = new ArrayList<String>();
		for(int i = 0; i < count; i++){
			msgidList.add(result.getString(i, 0));
		}
		logger.debug("----" + msg + "����----");
		return msgidList;
	}
	
	private String getClientIP(){
		return IpAddressInInterceptor.ip.get();
	}
	/*
	 * biztypeҵ������
	 * creatdate��������
	 * finorgcode�������ش���
	 * reportdate��������
	 * extparam��չ���ò���
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
			logger.debug(getfilename+"[readTipsData]��ȡ��������[" + finorgcode + "]��������["+ reportdate + "]ҵ������[" + biztype + "]��������[" +creatdate+ "]������");
			if(getfilename==null)
				reportXml = "";
			else
				reportXml = FileUtil.getInstance().readFile(filepath+getfilename);
			//��¼������ȡ��־
//			TsSyslogDto syslog = new TsSyslogDto();
//			syslog.setSorgcode(recvlog.getSrecvorgcode());
//			syslog.setSoperationtypecode(biztype);
//			syslog.setSdate(reportdate);
//			syslog.setSdemo(getClientIP());//��ȡ������IP��ַ
//			syslog.setSusercode(finorgcode);//���ö�ȡ���ĸ�����������
//			syslog.setSoperationdesc("����webservice����");
//			syslog.setStime(TSystemFacade.getDBSystemTime());
//			DatabaseFacade.getDb().create(syslog);
			return reportXml.getBytes("GBK");
		}catch (Exception e) {
			logger.error(e);
			throw new Exception("��ȡ���ĳ����쳣��"+e);
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

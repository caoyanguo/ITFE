package com.cfcc.itfe.client.dataquery.querylogconsole;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.ComboListDto;
import com.cfcc.itfe.persistence.dto.TvRecvLogShowDto;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.ibm.db2.jcc.a.a;

/**
 * codecomment: 
 * @author Administrator
 * @time   09-12-30 08:41:15
 * ��ϵͳ: DataQuery
 * ģ��:queryLogConsole
 * ���:QueryLogConsole
 */
public class QueryLogConsoleBean extends AbstractQueryLogConsoleBean implements IPageDataProvider {
    private static Log log = LogFactory.getLog(QueryLogConsoleBean.class);
    //���������б�
    private List<ComboListDto> recvDateList;
    //��������
    private String recvDate;
    //������־
    private List<TvRecvLogShowDto> recvLogs;
    //���������б�
    private List<ComboListDto> sendDateList;
    //��������
    private String sendDate;
    //������־
    private List<TvRecvLogShowDto> sendLogs;
    
    public QueryLogConsoleBean() {
      super();
      try{
    	  //������־�е�ȫ������
    	  recvDateList = commonDataAccessService.findLogDateList(StateConstant.LOG_RECV);
    	  if (recvDateList.size()>0){
    		  recvDate = recvDateList.get(0).getData();
    	  }else{
    		  recvDate = "";
    	  }
    	  //������־�е�ȫ������
    	  sendDateList = commonDataAccessService.findLogDateList(StateConstant.LOG_SEND);
    	  if (sendDateList.size()>0){
    		  sendDate = sendDateList.get(0).getData();
    	  }else{
    		  sendDate = "";
    	  }
      }catch(Throwable e){
    	  log.error(e);
    	  MessageDialog.openErrorDialog(null, e);
      }
    }

	/**
	 * ��ѯ������־
	 * @throws Exception
	 */
    public void queryRecvLog() throws Exception{
    	if ((null == recvDate) || (recvDate.length() == 0)){
    		recvLogs = new ArrayList<TvRecvLogShowDto>();
    	}else{
    		recvLogs = queryLogConsoleService.getLogByDate(StateConstant.LOG_RECV, recvDate);
    	}
		editor.fireModelChanged();
	}
		
	/**
	 * ��ѯ������־
	 * @throws Exception
	 */
    public void querySendLog() throws Exception{
    	if ((null == sendDate) || (sendDate.length() == 0)){
    		sendLogs = new ArrayList<TvRecvLogShowDto>();
    	}else{
			sendLogs = queryLogConsoleService.getLogByDate(StateConstant.LOG_SEND, sendDate);
    	}
		editor.fireModelChanged();
	}
	
    public List<ComboListDto> getRecvDateList() {
		return recvDateList;
	}

	public void setRecvDateList(List<ComboListDto> recvDateList) {
		this.recvDateList = recvDateList;
	}

	public String getRecvDate() {
		return recvDate;
	}

	public void setRecvDate(String recvDate) {
		this.recvDate = recvDate;
	}

	public List<TvRecvLogShowDto> getRecvLogs() {
		return recvLogs;
	}

	public void setRecvLogs(List<TvRecvLogShowDto> recvLogs) {
		this.recvLogs = recvLogs;
	}

	public List<ComboListDto> getSendDateList() {
		return sendDateList;
	}

	public void setSendDateList(List<ComboListDto> sendDateList) {
		this.sendDateList = sendDateList;
	}

	public String getSendDate() {
		return sendDate;
	}

	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}

	public List<TvRecvLogShowDto> getSendLogs() {
		return sendLogs;
	}

	public void setSendLogs(List<TvRecvLogShowDto> sendLogs) {
		this.sendLogs = sendLogs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

}
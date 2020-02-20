package com.cfcc.itfe.client.recbiz.certrec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.file.FileOperFacade;
import com.cfcc.itfe.client.common.local.LocalInfoHelper;
import com.cfcc.itfe.client.common.report.ReportComposite;
import com.cfcc.itfe.client.common.report.ReportGen;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.ComboListDto;
import com.cfcc.itfe.persistence.dto.TvFilesDto;
import com.cfcc.itfe.persistence.dto.TvRecvLogReportDto;
import com.cfcc.itfe.persistence.dto.TvRecvLogShowDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author sjz
 * @time   09-10-19 09:41:25
 * ��ϵͳ: RecBiz
 * ģ��:CertRec
 * ���:ReceiveUI
 */
public class ReceiveUIBean extends AbstractReceiveUIBean implements IPageDataProvider {
    private static Log log = LogFactory.getLog(ReceiveUIBean.class);
    //�û���¼��Ϣ
    private ITFELoginInfo loginfo;
	//�����ļ������ȱʡ·��
    private String downloadPath;
    //�����ļ�����·��
    private String backupPath;
    //���������б�
    private List<ComboListDto> dateList;
    //��������
    private String recvDate;
    //������־
    private List<TvRecvLogShowDto> recvLogs;
    //ѡ�еĽ�����־(��ѡ��ѡ��)
    private List<TvRecvLogShowDto> selectedRecvLogs;
    //ѡ�еĽ�����־���б���ѡ��һ����
    private TvRecvLogShowDto selectedRow;
    private TvRecvlogDto detailDto;
    //��ϸ��Ϣ
    private TvFilesDto oneFile;
    //����
    private List<TvFilesDto> attachFiles;
    private String attachFileNames;
    //������ʱ�ļ�
    private LocalInfoHelper localInfo;
    
    //������־ͳ�Ʊ�����ϸ����
    private Collection<Object> recvLogList;//List<TvRecvLogShowDto>
    //������־ͳ�Ʊ������
    private Map<String, Object> recvLogParam;
    //������־ͳ�Ʊ��������ļ�Ŀ¼
    private String recvLogReportPath = "/com/cfcc/itfe/client/recbiz/certrec/report_recvLog.jasper";
    
    
    
    public ReceiveUIBean() {
      super();
      try{
          dateList = commonDataAccessService.findLogDateList(StateConstant.LOG_RECV);
          if (dateList.size()>0){
        	  recvDate = dateList.get(0).getData();
          }else{
        	  recvDate = "20100104";
          }
          oneFile = new TvFilesDto();
          attachFiles = new ArrayList<TvFilesDto>();
          selectedRecvLogs = new ArrayList<TvRecvLogShowDto>();
          detailDto = new TvRecvlogDto();
          selectedRow =  new TvRecvLogShowDto();
          localInfo = new LocalInfoHelper();
          downloadPath = localInfo.getFileSavePath();
          backupPath = localInfo.getFileBackupPath();
          loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
       
      }catch(Throwable e){
    	  log.error(e);
    	  MessageDialog.openErrorDialog(null, e);
      }
    }
    
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}
    
    
	@Override
	public String selectOneRow(Object o) {
		selectedRow = (TvRecvLogShowDto)o;
		return super.selectOneRow(o);
	}

	@Override
	public String downloadOneFile(Object o) {
		// ѡ�񱣴�·��
		if ((null == downloadPath) || (downloadPath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ�������ļ�����·����");
			return "";
		}
		if (selectedRow.getSretcode().equals(ITFECommonConstant.STATUS_CANCELED)){
			MessageDialog.openMessageDialog(null, "�����Ѿ����ϣ������������еĸ�����");
			return "";
		}
		// �������и���
		try {
			//���ϵͳ���ݿ�����
			String today = commonDataAccessService.getSysDBDate();
			if ((today == null) || (today.length() != 8)){
				today = DateUtil.date2String2(new Date());
			}
			for (TvFilesDto file : attachFiles) {
				String fileName = FileOperFacade.getFileName(file.getSsavepath());
				String fileSaveName = downloadPath + "/" + fileName;
				// ���ļ����ص�����Ŀ¼��
				ClientFileTransferUtil.downloadFile(file.getSsavepath(), fileSaveName);
				if ((backupPath != null) && (backupPath.length() > 0)){
					//�����ں�ҵ��ƾ֤�����������ļ�����·��
					String fileBackupPath = backupPath + "/" + today.substring(0,4) + "/" + today.substring(4,6) + "/"
						+ today.substring(4) + "/";
					//��������Ŀ¼
					FileOperFacade.createDir(fileBackupPath);
					//�����ļ��ľ���·��
					String fileBackupName = fileBackupPath + fileName;
					//�����ص��ļ����Ƶ�����Ŀ¼��
					String content = FileOperFacade.readFile(fileSaveName);
					FileOperFacade.writeFile(fileBackupName, content);
					if (fileName.toLowerCase().startsWith(selectedRow.getSoperationtypecode().toLowerCase())){
						//�����ҵ���ģ���ô��Ҫ�������ĵ�����Ŀ¼��
						FileOperFacade.writeFile(fileBackupName + ".txt", file.getScontent());
					}
				}
			}
			//������ϣ��޸Ĵ����־
			List<TvRecvLogShowDto> list = new ArrayList<TvRecvLogShowDto>();
			list.add(selectedRow);
			downloadAllFileService.updateStatus(list, ITFECommonConstant.STATUS_FINISHED);
	        recvLogs = downloadAllFileService.getRecvLogBeforeDate(recvDate);
			MessageDialog.openMessageDialog(null, "�����Ѿ����浽·��" + downloadPath + "�С�");
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}
		return super.downloadOneFile(o);
	}

	/**
	 * �������һ�����صķ���
	 */
	public String downloadSelectedFiles(Object o) {
		if ((selectedRecvLogs == null) || (selectedRecvLogs.size() == 0)){
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص���Ϣ��");
			return "";
		}
		if ((downloadPath == null) || (downloadPath.length() == 0)){
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�����ļ�����·����");
			return "";
		}
		try{
			//���ϵͳ���ݿ�����
			String today = commonDataAccessService.getSysDBDate();
			if ((today == null) || (today.length() != 8)){
				today = DateUtil.date2String2(new Date());
			}
			for (TvRecvLogShowDto recvlog : selectedRecvLogs){
				//�����Ѿ����ϵĽ���ƾ֤
				if (recvlog.getSretcode().equals(ITFECommonConstant.STATUS_CANCELED)){
					continue;
				}
				List<TvFilesDto> files = (List<TvFilesDto>)downloadAllFileService.getFileListBySendNo(recvlog, recvlog.getSsendno());
				for (TvFilesDto file : files){
					String fileName = FileOperFacade.getFileName(file.getSsavepath());
					String fileSaveName = downloadPath + "/" + fileName;
					//���ļ����ص�����Ŀ¼��
					ClientFileTransferUtil.downloadFile(file.getSsavepath(), fileSaveName);
					if ((backupPath != null) && (backupPath.length() > 0)){
						//�����ں�ҵ��ƾ֤�����������ļ�����·��
						String fileBackupPath = backupPath + "/" + today.substring(0,4) + "/" + today.substring(4,6) + "/"
							+ today.substring(4) + "/";
						//��������Ŀ¼
						FileOperFacade.createDir(fileBackupPath);
						//�����ļ��ľ���·��
						String fileBackupName = fileBackupPath + fileName;
						//�����ص��ļ����Ƶ�����Ŀ¼��
						String content = FileOperFacade.readFile(fileSaveName);
						FileOperFacade.writeFile(fileBackupName, content);
						if (fileName.toLowerCase().startsWith(recvlog.getSoperationtypecode().toLowerCase())){
							//�����ҵ���ģ���ô��Ҫ�������ĵ�����Ŀ¼��
							FileOperFacade.writeFile(fileBackupName + ".txt", file.getScontent());
						}
					}
				}
			}
			//������ϣ��޸Ĵ����־
			downloadAllFileService.updateStatus(selectedRecvLogs, ITFECommonConstant.STATUS_FINISHED);
	        recvLogs = downloadAllFileService.getRecvLogBeforeDate(recvDate);
	        editor.fireModelChanged();
			MessageDialog.openMessageDialog(null, "������ϣ��뵽Ŀ¼" + downloadPath + "�м�顣");
		}catch(Throwable e){
			log.error(e);
			MessageDialog.openErrorDialog(null , e);
		}
		return super.downloadSelectedFiles(o);
	}

	@Override
	public String queryCerDetail(Object o) {
		if ((selectedRow == null) || (selectedRow.getSsendno() == null)){
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		try{
			detailDto.setSrecvorgcode(loginfo.getSorgName());
			detailDto.setSsendorgcode(selectedRow.getSorgname());
			detailDto.setStitle(selectedRow.getStitle());
			detailDto.setSdate(selectedRow.getSdate());
			oneFile = downloadAllFileService.getFileInfoBySendNo(selectedRow.getSsendno());
			attachFiles = (List<TvFilesDto>)downloadAllFileService.getFileListBySendNo(selectedRow, selectedRow.getSsendno());
			attachFileNames = "";
			for (TvFilesDto one : attachFiles){
				attachFileNames += FileOperFacade.getFileName(one.getSsavepath()) + "\n";
			}
			editor.openComposite("ƾ֤��ϸ",true);
		}catch(Throwable e){
			log.error(e);
			MessageDialog.openErrorDialog(null , e);
		}
		return super.queryCerDetail(o);
	}

	/**
	 * ѡ������·�� 
	 */
	public String pathSelect(Object o) {
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent().getActiveShell());
		String strPath = path.open();
		if ((null == strPath) || (strPath.length() == 0)){
			MessageDialog.openMessageDialog(null, "��ѡ�������ļ�����·����");
			return "";
		}
		downloadPath = strPath;
		localInfo.setFileSavePath(downloadPath);
		editor.fireModelChanged();
		return super.pathSelect(o);
	}

	/**
	 * ѡ�񱸷�·�� 
	 */
	public String backupPathSelect(Object o) {
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent().getActiveShell());
		String strPath = path.open();
		if ((null == strPath) || (strPath.length() == 0)){
			MessageDialog.openMessageDialog(null, "��ѡ�������ļ�����·����");
			return "";
		}
		backupPath = strPath;
		localInfo.setFileBackupPath(backupPath);
		editor.fireModelChanged();
		return super.pathSelect(o);
	}

	/**
	 * ��ѯָ�����ڵĽ�����־
	 */
	public String queryRecvLog(Object o) {
		try{
	        if ((recvDate != null) && (recvDate.length() > 0)) {
				recvLogs = downloadAllFileService.getRecvLogBeforeDate(recvDate);
			} else {
				recvLogs = downloadAllFileService.getRecvLogBeforeDate(null);
			}
	        ReportComposite report = (ReportComposite)o;
	        createReport();
	        //��ʾ����
	        ReportGen.genReport(recvLogParam, recvLogList, report,recvLogReportPath);
			editor.fireModelChanged();
		}catch(Throwable e){
			log.error(e);
			MessageDialog.openErrorDialog(null , e);
		}
		return super.queryRecvLog(o);
	}

	/**
	 * ������־ͳ�Ʊ���
	 */
	public String queryRecvLogReport(Object o) {
		try{
	        if ((null == recvDate) || (recvDate.length() == 0)){
	        	MessageDialog.openMessageDialog(null, "��ѡ��Ҫͳ�Ƶ����ڡ�");
	        	return "";
	        }
	        createReport();
	        //���Ѿ����úõ�View
	        editor.openComposite("��������ͳ��",true);
		}catch(Throwable e){
			log.error(e);
			MessageDialog.openErrorDialog(null , e);
		}
		return super.queryRecvLogReport(o);
	}

	/**
	 * ����һ���Ѿ����͵���Ϣ
	 */
	public String recvDelete(Object o) {
		try{
			//�����Ѿ����յ���Ϣ
			if (ITFECommonConstant.STATUS_CANCELED.equals(selectedRow.getSretcode())){
				//�Ѿ����ϣ������ظ�����
				MessageDialog.openMessageDialog(null, "�����Ѿ����ϡ�");
			}else{
				//���ϱ���
				downloadAllFileService.recvDelete(selectedRow);
		        recvLogs = downloadAllFileService.getRecvLogBeforeDate(recvDate);
				editor.fireModelChanged();
				MessageDialog.openMessageDialog(null, "ҵ��ƾ֤���ϳɹ���");
			}
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.recvDelete(o);
	}
	
	/**
	 * ���ɱ�����ʾ���������
	 */
	private void createReport() throws Exception{
        //������Ҫ��ʾ������
        List<TvRecvLogShowDto> list = downloadAllFileService.getRecvLogReport(recvDate);
        //ת����ʾ���ݵĸ�ʽ
        recvLogList = new ArrayList<Object>();
        String strOrgCode = "";
        TvRecvLogReportDto row = new TvRecvLogReportDto();
        for (TvRecvLogShowDto one : list){
        	if (!strOrgCode.equals(one.getSsendorgcode())){
        		if (strOrgCode.length() > 0){
	        		//����������� List��
	        		recvLogList.add(row);
        		}
        		//��������µĻ�������ô���½�һ�����󣬵�һ�β��أ���Ϊ��ʼ��ʱ���Ѿ��½���
        		row = new TvRecvLogReportDto();
        		strOrgCode = one.getSsendorgcode();
        		row.setOrgcode(one.getSsendorgcode());
        		row.setOrgname(one.getSorgname());
        	}
        	if ("QS".equals(one.getSoperationtypecode())){
        		row.setQscount(row.getQscount() + one.getIcount());
        	}else if("TK".equals(one.getSoperationtypecode())){
        		row.setTkcount(row.getTkcount() + one.getIcount());
        	}else{
        		row.setQtcount(row.getQtcount() + one.getIcount());
        	}
        }
        //�������һ��
		recvLogList.add(row);
        //�����еĴ������
        recvLogParam = new HashMap<String, Object>();
        recvLogParam.put("recvOrg",	loginfo.getSorgName() + "(" + loginfo.getSorgcode() + ")");
        recvLogParam.put("recvDate", recvDate.substring(0, 4) + "��" + recvDate.substring(4,6) + "��" + recvDate.substring(6) + "��");
	}
	
	public List<TvRecvLogShowDto> getRecvLogs() {
		return recvLogs;
	}
	public void setRecvLogs(List<TvRecvLogShowDto> recvLogs) {
		this.recvLogs = recvLogs;
	}
	public TvFilesDto getOneFile() {
		return oneFile;
	}
	public void setOneFile(TvFilesDto oneFile) {
		this.oneFile = oneFile;
	}
	public List<TvFilesDto> getAttachFiles() {
		return attachFiles;
	}
	public void setAttachFiles(List<TvFilesDto> attachFiles) {
		this.attachFiles = attachFiles;
	}

	public String getRecvDate() {
		return recvDate;
	}

	public void setRecvDate(String recvDate) {
		this.recvDate = recvDate;
	}

	public List<TvRecvLogShowDto> getSelectedRecvLogs() {
		return selectedRecvLogs;
	}

	public void setSelectedRecvLogs(List<TvRecvLogShowDto> selectedRecvLogs) {
		this.selectedRecvLogs = selectedRecvLogs;
	}

	public TvRecvLogShowDto getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(TvRecvLogShowDto selectedRow) {
		this.selectedRow = selectedRow;
	}

	public String getDownloadPath() {
		return downloadPath;
	}

	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}
    public List<ComboListDto> getDateList() {
		return dateList;
	}

	public void setDateList(List<ComboListDto> dateList) {
		this.dateList = dateList;
	}

	public String getBackupPath() {
		return backupPath;
	}

	public void setBackupPath(String backupPaht) {
		this.backupPath = backupPaht;
	}

	public Collection<Object> getRecvLogList() {
		return recvLogList;
	}

	public void setRecvLogList(Collection<Object> recvLogList) {
		this.recvLogList = recvLogList;
	}

	public Map<String, Object> getRecvLogParam() {
		return recvLogParam;
	}

	public void setRecvLogParam(Map<String, Object> recvLogParam) {
		this.recvLogParam = recvLogParam;
	}

	public String getRecvLogReportPath() {
		return recvLogReportPath;
	}

	public void setRecvLogReportPath(String recvLogReportPath) {
		this.recvLogReportPath = recvLogReportPath;
	}

	public String getAttachFileNames() {
		return attachFileNames;
	}

	public void setAttachFileNames(String attachFileNames) {
		this.attachFileNames = attachFileNames;
	}

	public TvRecvlogDto getDetailDto() {
		return detailDto;
	}

	public void setDetailDto(TvRecvlogDto detailDto) {
		this.detailDto = detailDto;
	}


}
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
 * 子系统: RecBiz
 * 模块:CertRec
 * 组件:ReceiveUI
 */
public class ReceiveUIBean extends AbstractReceiveUIBean implements IPageDataProvider {
    private static Log log = LogFactory.getLog(ReceiveUIBean.class);
    //用户登录信息
    private ITFELoginInfo loginfo;
	//下载文件保存的缺省路径
    private String downloadPath;
    //下载文件备份路径
    private String backupPath;
    //接收日期列表
    private List<ComboListDto> dateList;
    //接收日期
    private String recvDate;
    //接收日志
    private List<TvRecvLogShowDto> recvLogs;
    //选中的接收日志(复选框选择)
    private List<TvRecvLogShowDto> selectedRecvLogs;
    //选中的接收日志（列表中选择一条）
    private TvRecvLogShowDto selectedRow;
    private TvRecvlogDto detailDto;
    //详细信息
    private TvFilesDto oneFile;
    //附件
    private List<TvFilesDto> attachFiles;
    private String attachFileNames;
    //本地临时文件
    private LocalInfoHelper localInfo;
    
    //接收日志统计报表明细数据
    private Collection<Object> recvLogList;//List<TvRecvLogShowDto>
    //接收日志统计报表参数
    private Map<String, Object> recvLogParam;
    //接收日志统计报表配置文件目录
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
		// 选择保存路径
		if ((null == downloadPath) || (downloadPath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择下载文件保存路径。");
			return "";
		}
		if (selectedRow.getSretcode().equals(ITFECommonConstant.STATUS_CANCELED)){
			MessageDialog.openMessageDialog(null, "报文已经作废，不必下载其中的附件。");
			return "";
		}
		// 下载所有附件
		try {
			//获得系统数据库日期
			String today = commonDataAccessService.getSysDBDate();
			if ((today == null) || (today.length() != 8)){
				today = DateUtil.date2String2(new Date());
			}
			for (TvFilesDto file : attachFiles) {
				String fileName = FileOperFacade.getFileName(file.getSsavepath());
				String fileSaveName = downloadPath + "/" + fileName;
				// 将文件下载到下载目录中
				ClientFileTransferUtil.downloadFile(file.getSsavepath(), fileSaveName);
				if ((backupPath != null) && (backupPath.length() > 0)){
					//用日期和业务凭证类型来生成文件保存路径
					String fileBackupPath = backupPath + "/" + today.substring(0,4) + "/" + today.substring(4,6) + "/"
						+ today.substring(4) + "/";
					//创建保存目录
					FileOperFacade.createDir(fileBackupPath);
					//备份文件的绝对路径
					String fileBackupName = fileBackupPath + fileName;
					//将下载的文件复制到备份目录中
					String content = FileOperFacade.readFile(fileSaveName);
					FileOperFacade.writeFile(fileBackupName, content);
					if (fileName.toLowerCase().startsWith(selectedRow.getSoperationtypecode().toLowerCase())){
						//如果是业务报文，那么需要保存明文到备份目录中
						FileOperFacade.writeFile(fileBackupName + ".txt", file.getScontent());
					}
				}
			}
			//下载完毕，修改处理标志
			List<TvRecvLogShowDto> list = new ArrayList<TvRecvLogShowDto>();
			list.add(selectedRow);
			downloadAllFileService.updateStatus(list, ITFECommonConstant.STATUS_FINISHED);
	        recvLogs = downloadAllFileService.getRecvLogBeforeDate(recvDate);
			MessageDialog.openMessageDialog(null, "附件已经保存到路径" + downloadPath + "中。");
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}
		return super.downloadOneFile(o);
	}

	/**
	 * 多个附件一起下载的方法
	 */
	public String downloadSelectedFiles(Object o) {
		if ((selectedRecvLogs == null) || (selectedRecvLogs.size() == 0)){
			MessageDialog.openMessageDialog(null, "请选择要下载的信息。");
			return "";
		}
		if ((downloadPath == null) || (downloadPath.length() == 0)){
			MessageDialog.openMessageDialog(null, "请选择要下载文件保存路径。");
			return "";
		}
		try{
			//获得系统数据库日期
			String today = commonDataAccessService.getSysDBDate();
			if ((today == null) || (today.length() != 8)){
				today = DateUtil.date2String2(new Date());
			}
			for (TvRecvLogShowDto recvlog : selectedRecvLogs){
				//跳过已经作废的接收凭证
				if (recvlog.getSretcode().equals(ITFECommonConstant.STATUS_CANCELED)){
					continue;
				}
				List<TvFilesDto> files = (List<TvFilesDto>)downloadAllFileService.getFileListBySendNo(recvlog, recvlog.getSsendno());
				for (TvFilesDto file : files){
					String fileName = FileOperFacade.getFileName(file.getSsavepath());
					String fileSaveName = downloadPath + "/" + fileName;
					//将文件下载到下载目录中
					ClientFileTransferUtil.downloadFile(file.getSsavepath(), fileSaveName);
					if ((backupPath != null) && (backupPath.length() > 0)){
						//用日期和业务凭证类型来生成文件保存路径
						String fileBackupPath = backupPath + "/" + today.substring(0,4) + "/" + today.substring(4,6) + "/"
							+ today.substring(4) + "/";
						//创建保存目录
						FileOperFacade.createDir(fileBackupPath);
						//备份文件的绝对路径
						String fileBackupName = fileBackupPath + fileName;
						//将下载的文件复制到备份目录中
						String content = FileOperFacade.readFile(fileSaveName);
						FileOperFacade.writeFile(fileBackupName, content);
						if (fileName.toLowerCase().startsWith(recvlog.getSoperationtypecode().toLowerCase())){
							//如果是业务报文，那么需要保存明文到备份目录中
							FileOperFacade.writeFile(fileBackupName + ".txt", file.getScontent());
						}
					}
				}
			}
			//下载完毕，修改处理标志
			downloadAllFileService.updateStatus(selectedRecvLogs, ITFECommonConstant.STATUS_FINISHED);
	        recvLogs = downloadAllFileService.getRecvLogBeforeDate(recvDate);
	        editor.fireModelChanged();
			MessageDialog.openMessageDialog(null, "下载完毕，请到目录" + downloadPath + "中检查。");
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
			editor.openComposite("凭证明细",true);
		}catch(Throwable e){
			log.error(e);
			MessageDialog.openErrorDialog(null , e);
		}
		return super.queryCerDetail(o);
	}

	/**
	 * 选择下载路径 
	 */
	public String pathSelect(Object o) {
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent().getActiveShell());
		String strPath = path.open();
		if ((null == strPath) || (strPath.length() == 0)){
			MessageDialog.openMessageDialog(null, "请选择下载文件保存路径。");
			return "";
		}
		downloadPath = strPath;
		localInfo.setFileSavePath(downloadPath);
		editor.fireModelChanged();
		return super.pathSelect(o);
	}

	/**
	 * 选择备份路径 
	 */
	public String backupPathSelect(Object o) {
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent().getActiveShell());
		String strPath = path.open();
		if ((null == strPath) || (strPath.length() == 0)){
			MessageDialog.openMessageDialog(null, "请选择下载文件备份路径。");
			return "";
		}
		backupPath = strPath;
		localInfo.setFileBackupPath(backupPath);
		editor.fireModelChanged();
		return super.pathSelect(o);
	}

	/**
	 * 查询指定日期的接收日志
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
	        //显示报表
	        ReportGen.genReport(recvLogParam, recvLogList, report,recvLogReportPath);
			editor.fireModelChanged();
		}catch(Throwable e){
			log.error(e);
			MessageDialog.openErrorDialog(null , e);
		}
		return super.queryRecvLog(o);
	}

	/**
	 * 接收日志统计报表
	 */
	public String queryRecvLogReport(Object o) {
		try{
	        if ((null == recvDate) || (recvDate.length() == 0)){
	        	MessageDialog.openMessageDialog(null, "请选择要统计的日期。");
	        	return "";
	        }
	        createReport();
	        //打开已经设置好的View
	        editor.openComposite("接收数据统计",true);
		}catch(Throwable e){
			log.error(e);
			MessageDialog.openErrorDialog(null , e);
		}
		return super.queryRecvLogReport(o);
	}

	/**
	 * 作废一条已经发送的信息
	 */
	public String recvDelete(Object o) {
		try{
			//作废已经接收的信息
			if (ITFECommonConstant.STATUS_CANCELED.equals(selectedRow.getSretcode())){
				//已经作废，不必重复作废
				MessageDialog.openMessageDialog(null, "报文已经作废。");
			}else{
				//作废报文
				downloadAllFileService.recvDelete(selectedRow);
		        recvLogs = downloadAllFileService.getRecvLogBeforeDate(recvDate);
				editor.fireModelChanged();
				MessageDialog.openMessageDialog(null, "业务凭证作废成功。");
			}
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.recvDelete(o);
	}
	
	/**
	 * 生成报表显示所需的数据
	 */
	private void createReport() throws Exception{
        //报表中要显示的数据
        List<TvRecvLogShowDto> list = downloadAllFileService.getRecvLogReport(recvDate);
        //转换显示数据的格式
        recvLogList = new ArrayList<Object>();
        String strOrgCode = "";
        TvRecvLogReportDto row = new TvRecvLogReportDto();
        for (TvRecvLogShowDto one : list){
        	if (!strOrgCode.equals(one.getSsendorgcode())){
        		if (strOrgCode.length() > 0){
	        		//将结果保存在 List中
	        		recvLogList.add(row);
        		}
        		//如果遇到新的机构，那么就新建一个对象，第一次不必，因为开始的时候已经新建了
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
        //保存最后一个
		recvLogList.add(row);
        //报表中的传入参数
        recvLogParam = new HashMap<String, Object>();
        recvLogParam.put("recvOrg",	loginfo.getSorgName() + "(" + loginfo.getSorgcode() + ")");
        recvLogParam.put("recvDate", recvDate.substring(0, 4) + "年" + recvDate.substring(4,6) + "月" + recvDate.substring(6) + "日");
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
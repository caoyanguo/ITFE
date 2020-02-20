package com.cfcc.itfe.client.dataquery.tipsdataexport;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.TipsParamDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author hua
 * @time 12-05-16 17:39:20 子系统: DataQuery 模块:TipsDataExport 组件:TipsDataExport
 */
public class TipsDataExportBean extends AbstractTipsDataExportBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TipsDataExportBean.class);
	private List typelist = new ArrayList();
	private List checklist = new ArrayList();
	private ITFELoginInfo loginfo = null;

	public TipsDataExportBean() {
		super();
		sorgcode = "";
		staxorgcode = "";
		strecode = "";
		sbeflag = "";
		startdate = TimeFacade.getCurrentDateTime();
		enddate = TimeFacade.getCurrentDateTime();
		ifsub = "0";
		exptype = "0";
		loginfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
		init();
	}

	public void init() {
		typelist.add(initValue("3128财政申请收入日报表",StateConstant.RecvTips_3128_SR));
		typelist.add(initValue("3128财政申请库存日报表",StateConstant.RecvTips_3128_KC));
		typelist.add(initValue("3129财政申请电子税票信息",StateConstant.RecvTips_3129));
		typelist.add(initValue("3139财政申请入库流水明细",StateConstant.RecvTips_3139));
		typelist.add(initValue("3127支出报表明细",StateConstant.RecvTips_3127));
	}

	public TdEnumvalueDto initValue(String type,String mark) {
		TdEnumvalueDto dto = new TdEnumvalueDto();
		dto.setSvalue("是否导出");
		dto.setSremark(mark);
		dto.setSvaluecmt(type);
		return dto;
	}
	/**
	 * Direction: 定时导出
	 * ename: timerExport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String timerExport(Object o){
    	if(null == startdate || "".equals(startdate) ) {
			MessageDialog.openMessageDialog(null, "请输入定时导出报表的日期!");
			return null;
		}else
		{
			if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
					.getCurrentComposite().getShell(), "提示", "你确定要重新发送日期为"+startdate.toString().replace("-","")+"的报表数据吗?")) {
				return "";
			}
		}
    	TipsParamDto paramdto = new TipsParamDto();
    	paramdto.setStartdate(startdate);
    	paramdto.setSbankcode("sendreport");
    	try {
			tipsDataExportService.generateTipsToFile(null, paramdto, "");
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return "";
		}
		MessageDialog.openMessageDialog(null, "重新发送报表成功!");
        return super.timerExport(o);
    }
	/**
	 * Direction: 导出数据 ename: exportData 引用方法: viewers: * messages:
	 */
	public String exportData(Object o) {
		if(checklist.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择需要导出的数据种类!");
			return null;
		}
		TipsParamDto paramdto = new TipsParamDto();
		if(null == startdate || null == enddate ) {
			MessageDialog.openMessageDialog(null, "请输入开始日期和结束日期!");
			return null;
		}
		if(null != startdate && null != enddate && java.sql.Date.valueOf(startdate.toString()).after(java.sql.Date.valueOf(enddate.toString()))) {
			MessageDialog.openMessageDialog(null, "开始日期必须小于结束日期!");
			paramdto = new TipsParamDto();
			return null;
		}
		try {
			// 判断当前登录查询国库代码、财政代码是否在核算主体中
			TsConvertfinorgDto finddto = new TsConvertfinorgDto();
			List finddtolist = null;
			if (!loginfo.getSorgcode().equals(StateConstant.ORG_CENTER_CODE)) {
				finddto.setSorgcode(loginfo.getSorgcode()); // 核算主体代码
				if(null != sorgcode && !"".equals(sorgcode)) {
					finddto.setSfinorgcode(sorgcode); // 财政代码
				}
				if(null != strecode && !"".equals(strecode)) {
					finddto.setStrecode(strecode); // 国库主体代码
				}	
				finddtolist = commonDataAccessService.findRsByDtocheckUR(finddto, "1");
				if (null == finddtolist || finddtolist.size() == 0) {
					MessageDialog.openMessageDialog(null, "权限不足！请填写正确的财政代码或者国库代码！");
					return null;
				}
			}
			if(null != sorgcode && !"".equals(sorgcode)) {
				paramdto.setSorgcode(sorgcode);
			}
			if(null != staxorgcode && !"".equals(staxorgcode)) {
				paramdto.setStaxorgcode(staxorgcode);
			}
			if(null != strecode && !"".equals(strecode)) {
				paramdto.setStrecode(strecode);
			}
			if(null != sbeflag && !"".equals(sbeflag)) {
				paramdto.setSbeflag(sbeflag);
			}
			if(null != startdate ) {
				paramdto.setStartdate(startdate);
			}
			if(null != enddate) {
				paramdto.setEnddate(enddate);
			}
			if(null != ifsub && !"".equals(ifsub)) {
				paramdto.setIfsub(ifsub);
			}
			paramdto.setExptype(exptype); //初始化设置
			if(null != exptype && !"".equals(exptype)) {
				if("1".equals(paramdto.getExptype())) {
					if(null == staxorgcode || "".equals(staxorgcode)) {
						MessageDialog.openMessageDialog(null, "3129电子税票导出重点税源接口格式数据时，请输入征收机关代码!");
						return null;
					}
				}
				paramdto.setExptype(exptype);
			}
			Map<String,List<String>> map = new HashMap<String,List<String>>();
			// 选择保存路径
			DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
					.getActiveShell());
			String filePath = path.open();
			List<String> filelist = new ArrayList<String>();
			StringBuffer errorInfo = new StringBuffer("");
			if ((null == filePath) || (filePath.length() == 0)) {
				MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
				return "";
			}	
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			/*
			 * 正式开始导出
			 */
			map = tipsDataExportService.generateTipsToFile(checklist, paramdto, "");
			
			filelist = map.get("files");
			//存到本地目录的父路径
			if(filePath.endsWith("/")||filePath.endsWith("\\")) {
				filePath += "数据下载目录("+new Date(new java.util.Date().getTime()).toString()+")";
			}else {
				filePath += "/数据下载目录("+new Date(new java.util.Date().getTime()).toString()+")";
			}
			
			String dirsep = File.separator; // 取得系统分割符
			dirsep = "/";
			for(String filep : filelist) {
				String clientfile ="";
				if(filep.indexOf(StateConstant.sr_3128) != -1) { //包含3128sr此字符串
					String clientdir = filePath+"/3128收入日报表/";
					File dir = new File(clientdir);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					clientfile = clientdir +filep.substring(filep.lastIndexOf(StateConstant.sr_3128)+StateConstant.sr_3128.length());
				}else if(filep.indexOf(StateConstant.kc_3128) != -1) { //包含3128kc此字符串
					String clientdir = filePath+"/3128库存日报表/";
					File dir = new File(clientdir);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					clientfile = clientdir+filep.substring(filep.lastIndexOf(StateConstant.kc_3128)+StateConstant.kc_3128.length());
				}else if(filep.indexOf(StateConstant.on_3129) != -1) { //包含3129此字符串
					String clientdir = filePath+"/3129电子税票/";
					File dir = new File(clientdir);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					clientfile = clientdir+filep.substring(filep.lastIndexOf(StateConstant.on_3129)+StateConstant.on_3129.length());
				}else if(filep.indexOf(StateConstant.in_3139) != -1) { //包含3139此字符串
					String clientdir = filePath+"/3139入库流水/";
					File dir = new File(clientdir);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					clientfile = clientdir+filep.substring(filep.lastIndexOf(StateConstant.in_3139)+StateConstant.in_3139.length());					
				}else if(filep.indexOf(StateConstant.in_3127) != -1) { //包含3139此字符串
					String clientdir = filePath+"/3127支出报表/";
					File dir = new File(clientdir);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					clientfile = clientdir+filep.substring(filep.lastIndexOf(StateConstant.in_3127)+StateConstant.in_3127.length());					
				}
				
				ClientFileTransferUtil.downloadFile(filep.replace(queryLogsService.getFileRootPath(),""),clientfile);
			}	
			tipsDataExportService.deleteTheFiles(filelist); //下载完之后删除服务器上数据
			errorInfo.append("数据导出到路径["+filePath+"]下\r\n");
			if(map != null && null != map.get("error") && map.get("error").size() > 0) {
				errorInfo.append("日志信息如下：\r\n");
				for(String er : map.get("error")) {
					errorInfo.append(er+"\r\n");					
				}
			}
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openMessageDialog(null, errorInfo.toString());
		} catch (ITFEBizException e) {
			log.error(e);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
		} catch (FileTransferException e) {
			log.error(e);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.exportData(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

	public List getTypelist() {
		return typelist;
	}

	public void setTypelist(List typelist) {
		this.typelist = typelist;
	}

	public List getChecklist() {
		return checklist;
	}

	public void setChecklist(List checklist) {
		this.checklist = checklist;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

}
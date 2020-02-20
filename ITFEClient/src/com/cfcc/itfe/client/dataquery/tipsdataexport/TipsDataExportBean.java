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
 * @time 12-05-16 17:39:20 ��ϵͳ: DataQuery ģ��:TipsDataExport ���:TipsDataExport
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
		typelist.add(initValue("3128�������������ձ���",StateConstant.RecvTips_3128_SR));
		typelist.add(initValue("3128�����������ձ���",StateConstant.RecvTips_3128_KC));
		typelist.add(initValue("3129�����������˰Ʊ��Ϣ",StateConstant.RecvTips_3129));
		typelist.add(initValue("3139�������������ˮ��ϸ",StateConstant.RecvTips_3139));
		typelist.add(initValue("3127֧��������ϸ",StateConstant.RecvTips_3127));
	}

	public TdEnumvalueDto initValue(String type,String mark) {
		TdEnumvalueDto dto = new TdEnumvalueDto();
		dto.setSvalue("�Ƿ񵼳�");
		dto.setSremark(mark);
		dto.setSvaluecmt(type);
		return dto;
	}
	/**
	 * Direction: ��ʱ����
	 * ename: timerExport
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String timerExport(Object o){
    	if(null == startdate || "".equals(startdate) ) {
			MessageDialog.openMessageDialog(null, "�����붨ʱ�������������!");
			return null;
		}else
		{
			if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
					.getCurrentComposite().getShell(), "��ʾ", "��ȷ��Ҫ���·�������Ϊ"+startdate.toString().replace("-","")+"�ı���������?")) {
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
		MessageDialog.openMessageDialog(null, "���·��ͱ���ɹ�!");
        return super.timerExport(o);
    }
	/**
	 * Direction: �������� ename: exportData ���÷���: viewers: * messages:
	 */
	public String exportData(Object o) {
		if(checklist.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ����Ҫ��������������!");
			return null;
		}
		TipsParamDto paramdto = new TipsParamDto();
		if(null == startdate || null == enddate ) {
			MessageDialog.openMessageDialog(null, "�����뿪ʼ���ںͽ�������!");
			return null;
		}
		if(null != startdate && null != enddate && java.sql.Date.valueOf(startdate.toString()).after(java.sql.Date.valueOf(enddate.toString()))) {
			MessageDialog.openMessageDialog(null, "��ʼ���ڱ���С�ڽ�������!");
			paramdto = new TipsParamDto();
			return null;
		}
		try {
			// �жϵ�ǰ��¼��ѯ������롢���������Ƿ��ں���������
			TsConvertfinorgDto finddto = new TsConvertfinorgDto();
			List finddtolist = null;
			if (!loginfo.getSorgcode().equals(StateConstant.ORG_CENTER_CODE)) {
				finddto.setSorgcode(loginfo.getSorgcode()); // �����������
				if(null != sorgcode && !"".equals(sorgcode)) {
					finddto.setSfinorgcode(sorgcode); // ��������
				}
				if(null != strecode && !"".equals(strecode)) {
					finddto.setStrecode(strecode); // �����������
				}	
				finddtolist = commonDataAccessService.findRsByDtocheckUR(finddto, "1");
				if (null == finddtolist || finddtolist.size() == 0) {
					MessageDialog.openMessageDialog(null, "Ȩ�޲��㣡����д��ȷ�Ĳ���������߹�����룡");
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
			paramdto.setExptype(exptype); //��ʼ������
			if(null != exptype && !"".equals(exptype)) {
				if("1".equals(paramdto.getExptype())) {
					if(null == staxorgcode || "".equals(staxorgcode)) {
						MessageDialog.openMessageDialog(null, "3129����˰Ʊ�����ص�˰Դ�ӿڸ�ʽ����ʱ�����������ջ��ش���!");
						return null;
					}
				}
				paramdto.setExptype(exptype);
			}
			Map<String,List<String>> map = new HashMap<String,List<String>>();
			// ѡ�񱣴�·��
			DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
					.getActiveShell());
			String filePath = path.open();
			List<String> filelist = new ArrayList<String>();
			StringBuffer errorInfo = new StringBuffer("");
			if ((null == filePath) || (filePath.length() == 0)) {
				MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
				return "";
			}	
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			/*
			 * ��ʽ��ʼ����
			 */
			map = tipsDataExportService.generateTipsToFile(checklist, paramdto, "");
			
			filelist = map.get("files");
			//�浽����Ŀ¼�ĸ�·��
			if(filePath.endsWith("/")||filePath.endsWith("\\")) {
				filePath += "��������Ŀ¼("+new Date(new java.util.Date().getTime()).toString()+")";
			}else {
				filePath += "/��������Ŀ¼("+new Date(new java.util.Date().getTime()).toString()+")";
			}
			
			String dirsep = File.separator; // ȡ��ϵͳ�ָ��
			dirsep = "/";
			for(String filep : filelist) {
				String clientfile ="";
				if(filep.indexOf(StateConstant.sr_3128) != -1) { //����3128sr���ַ���
					String clientdir = filePath+"/3128�����ձ���/";
					File dir = new File(clientdir);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					clientfile = clientdir +filep.substring(filep.lastIndexOf(StateConstant.sr_3128)+StateConstant.sr_3128.length());
				}else if(filep.indexOf(StateConstant.kc_3128) != -1) { //����3128kc���ַ���
					String clientdir = filePath+"/3128����ձ���/";
					File dir = new File(clientdir);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					clientfile = clientdir+filep.substring(filep.lastIndexOf(StateConstant.kc_3128)+StateConstant.kc_3128.length());
				}else if(filep.indexOf(StateConstant.on_3129) != -1) { //����3129���ַ���
					String clientdir = filePath+"/3129����˰Ʊ/";
					File dir = new File(clientdir);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					clientfile = clientdir+filep.substring(filep.lastIndexOf(StateConstant.on_3129)+StateConstant.on_3129.length());
				}else if(filep.indexOf(StateConstant.in_3139) != -1) { //����3139���ַ���
					String clientdir = filePath+"/3139�����ˮ/";
					File dir = new File(clientdir);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					clientfile = clientdir+filep.substring(filep.lastIndexOf(StateConstant.in_3139)+StateConstant.in_3139.length());					
				}else if(filep.indexOf(StateConstant.in_3127) != -1) { //����3139���ַ���
					String clientdir = filePath+"/3127֧������/";
					File dir = new File(clientdir);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					clientfile = clientdir+filep.substring(filep.lastIndexOf(StateConstant.in_3127)+StateConstant.in_3127.length());					
				}
				
				ClientFileTransferUtil.downloadFile(filep.replace(queryLogsService.getFileRootPath(),""),clientfile);
			}	
			tipsDataExportService.deleteTheFiles(filelist); //������֮��ɾ��������������
			errorInfo.append("���ݵ�����·��["+filePath+"]��\r\n");
			if(map != null && null != map.get("error") && map.get("error").size() > 0) {
				errorInfo.append("��־��Ϣ���£�\r\n");
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
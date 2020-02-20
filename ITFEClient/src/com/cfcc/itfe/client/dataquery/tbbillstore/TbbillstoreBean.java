package com.cfcc.itfe.client.dataquery.tbbillstore;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.*;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.dataquery.tbbillstore.ITbbillstoreService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TbBillstoreDto;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 15-06-01 14:41:03 ��ϵͳ: DataQuery ģ��:tbbillstore ���:Tbbillstore
 */
public class TbbillstoreBean extends AbstractTbbillstoreBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TbbillstoreBean.class);
	private Date sysDate;
	private ITFELoginInfo loginfo;
	private Date startDate;
	private Date endDate;

	public TbbillstoreBean() {
		super();
		fileList = new ArrayList();
		uploadDto = new TbBillstoreDto();
		searchDto = new TbBillstoreDto();
		resultList = new ArrayList();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		try {
			String sysdatestr = commonDataAccessService.getSysDate();
			sysDate = CommonUtil.strToDate(sysdatestr);
			uploadDto.setSyear(sysdatestr.substring(0, 4));
			startDate = sysDate;
			endDate = sysDate;
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		uploadDto.setDbilldate(sysDate);
	}

	/**
	 * Direction: ��ѯ ename: searchFileContent ���÷���: viewers: * messages:
	 */
	public String searchFileContent(Object o) {
		if (StringUtils.isBlank(searchDto.getStrecode())) {
			MessageDialog.openMessageDialog(null, "������벻��Ϊ��");
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		List paramlist = new ArrayList();
		paramlist.add(searchDto.getStrecode());
		paramlist.add(format.format(startDate));
		paramlist.add(format.format(endDate));
		try {
			resultList = tbbillstoreService.searchFileInfo(paramlist);
			if (null == resultList || resultList.size() == 0) {
				paramlist = new ArrayList();
				resultList = new ArrayList();
				MessageDialog.openMessageDialog(null, "û�з�����������Ϣ��");
				return null;
			}
		} catch (ITFEBizException e) {
			log.error("��ѯ���ݿ�ʧ�ܣ�", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		} finally {
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		}
		return super.searchFileContent(o);
	}

	/**
	 * Direction: �ļ����� ename: download ���÷���: viewers: * messages:
	 */
	public String download(Object o) {
		if (null == selectedDto || StringUtils.isBlank(selectedDto.getSseqno())) {
			MessageDialog.openMessageDialog(null, "��ѡ����Ҫ���ص��ļ�");
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		// �ͻ������ر��ĵ�·��
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		dirsep = "/";
		String clientpath = "c:" + dirsep + "client" + dirsep + "downreport"
				+ dirsep + format.format(selectedDto.getDbilldate()) + dirsep
				+ loginfo.getSorgcode() + dirsep;
		File dir = new File(clientpath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String clientfile = clientpath + selectedDto.getSfilename();

		try {
			ClientFileTransferUtil.downloadFile(selectedDto.getSfilepath()
					.replace(commonDataAccessService.getFileRootPath(), ""),
					clientfile);
		} catch (FileTransferException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		MessageDialog.openMessageDialog(null, "�ļ����سɹ���\n" + clientfile);

		return super.download(o);
	}

	/**
	 * Direction: ���ļ� ename: openFile ���÷���: viewers: * messages:
	 */
	public String openFile(Object o) {
		try {
			if(null == selectedDto){
				MessageDialog.openMessageDialog(null, "��ѡ����Ҫ �򿪵��ļ���");
				return null;
			}
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			// �ͻ������ر��ĵ�·��
			String dirsep = File.separator; // ȡ��ϵͳ�ָ��
			dirsep = "/";
			String clientpath = "c:" + dirsep + "client" + dirsep + "downreport"
					+ dirsep + format.format(selectedDto.getDbilldate()) + dirsep
					+ loginfo.getSorgcode() + dirsep;
			File dir = new File(clientpath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String clientfile = clientpath + selectedDto.getSfilename();
			File f = new File(clientfile);
			if(!f.exists()){
				try {
					ClientFileTransferUtil.downloadFile(selectedDto.getSfilepath()
							.replace(commonDataAccessService.getFileRootPath(), ""),
							clientfile);
				} catch (FileTransferException e) {
					log.error(e);
					MessageDialog.openErrorDialog(null, e);
					return null;
				} catch (ITFEBizException e) {
					log.error(e);
					MessageDialog.openErrorDialog(null, e);
					return null;
				}
			}
			String cmd = "rundll32 url.dll FileProtocolHandler file://" + clientfile;
			Process p = Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.openFile(o);
	}

	/**
	 * Direction: ��񵥻��¼� ename: singletableClick ���÷���: viewers: * messages:
	 */
	public String singletableClick(Object o) {
		this.selectedDto = (TbBillstoreDto) o;
		return super.singletableClick(o);
	}

	/**
	 * Direction: �ļ��ϴ� ename: fileUpload ���÷���: viewers: * messages:
	 */
	public String fileUpload(Object o) {
		if (null == fileList || fileList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ����Ҫ�ϴ��ı����ļ���");
			return "";
		}
		try {
			String uploadpath = commonDataAccessService.getFileRootPath();
			String serverpath;
			TbBillstoreDto tmpDto;
			String filename;
			List paramList = new ArrayList();
			for (File tmpFile : ((ArrayList<File>) fileList)) {
				serverpath = ClientFileTransferUtil.uploadFile(tmpFile
						.getAbsolutePath());
				filename = tmpFile.getName();
				tmpDto = new TbBillstoreDto();
				tmpDto.setSfiletype(filename.substring(filename
						.lastIndexOf(".") + 1, filename.length()));
				tmpDto.setDbilldate(uploadDto.getDbilldate());
				tmpDto.setSbookorgcode(loginfo.getSorgcode());
				tmpDto.setStrecode(uploadDto.getStrecode());
				tmpDto.setSfilename(tmpFile.getName());
				tmpDto.setSfilepath(uploadpath + serverpath);
				tmpDto.setSyear(uploadDto.getSyear());
				paramList.add(tmpDto);
			}
			tbbillstoreService.fileupload(paramList);
			fileList.clear();
			MessageDialog.openMessageDialog(null, "�����ϴ��ɹ�");
			fileList = new ArrayList();
		} catch (FileTransferException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		} finally {
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		}

		return super.fileUpload(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest request) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			commonDataAccessService.findRsByDtoWithWherePaging(searchDto,
					request, " 1=1 and D_BILLDATE BETWEEN '"
							+ format.format(startDate) + "' AND '"
							+ format.format(endDate) + "' ");
		} catch (ITFEBizException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.retrieve(request);
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
package com.cfcc.itfe.client.dataquery.querylogs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.file.FileOperFacade;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvRecvLogShowDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 09-10-15 16:04:50 ��ϵͳ: DataQuery ģ��:querylogs ���:Querylogs
 */
public class QueryLogsBean extends AbstractQueryLogsBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(QueryLogsBean.class);
	private String selDate = "";
	private String selfiletype;
	private String selopertype;
	private TvRecvLogShowDto seldto = new TvRecvLogShowDto();
	private TvRecvlogDto binddto = new TvRecvlogDto();
	private String srecvtime;
	private List<String> attachFiles;
	private String attachFileNames;
	TvSendlogDto dto;

	public TvSendlogDto getDto() {
		return dto;
	}

	public void setDto(TvSendlogDto dto) {
		this.dto = dto;
	}

	public String getSrecvtime() {
		return srecvtime;
	}

	public void setSrecvtime(String srecvtime) {
		this.srecvtime = srecvtime;
	}

	private ITFELoginInfo loginfo;

	public TvRecvlogDto getBinddto() {
		return binddto;
	}

	public void setBinddto(TvRecvlogDto binddto) {
		this.binddto = binddto;
	}

	public TvRecvLogShowDto getSeldto() {
		return seldto;
	}

	public void setSeldto(TvRecvLogShowDto seldto) {
		this.seldto = seldto;
	}

	private List<TvRecvlogDto> loglist = new ArrayList<TvRecvlogDto>();

	public String getSelDate() {
		return selDate;
	}

	public List<TvRecvlogDto> getLoglist() {
		return loglist;
	}

	public void setLoglist(List<TvRecvlogDto> loglist) {
		this.loglist = loglist;
	}

	public void setSelDate(String selDate) {
		this.selDate = selDate;
	}

	public String getSelfiletype() {
		return selfiletype;
	}

	public void setSelfiletype(String selfiletype) {
		this.selfiletype = selfiletype;
	}

	public String getSelopertype() {
		return selopertype;
	}

	public void setSelopertype(String selopertype) {
		this.selopertype = selopertype;
	}

	public List<String> getAttachFiles() {
		return attachFiles;
	}

	public void setAttachFiles(List<String> attachFiles) {
		this.attachFiles = attachFiles;
	}

	public String getAttachFileNames() {
		return attachFileNames;
	}

	public void setAttachFileNames(String attachFileNames) {
		this.attachFileNames = attachFileNames;
	}

	private PagingContext pagingcontext = new PagingContext(this);

	public QueryLogsBean() {
		super();
		try {
			selDate = commonDataAccessService.getSysDBDate();
			loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
					.getLoginInfo();
			selopertype = StateConstant.LOG_SEND;
			doQuery(loginfo);
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}
	}

	/**
	 * Direction: �շ���־��ѯ ename: queryLogs ���÷���: viewers: ��־��ѯ��� messages:
	 */
	public String queryLogs(Object o) {
		if (doQuery(o))
			editor.fireModelChanged();
		return "";
	}

	public PagingContext getPagingcontext() {
		return pagingcontext;
	}

	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
	}

	/**
	 * Direction: ��־��ϸ��ѯ ename: queryLogDetail ���÷���: viewers: ��־��ϸ��Ϣ messages:
	 */
	public String queryLogDetail(Object o) {
		if (seldto == null || seldto.getSoperationtypecode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		binddto = new TvRecvlogDto();
		srecvtime = seldto.getSsendtime().toString();
		binddto.setSdemo(seldto.getSdemo());
		binddto.setSoperationtypecode(seldto.getSoperationtypecode());
		binddto.setStitle(seldto.getStitle());
		if (seldto.getSopertype().equals(StateConstant.LOG_SEND)) {// ����
			binddto.setSsendorgcode(loginfo.getSorgName());
			binddto.setSrecvorgcode(seldto.getSorgcode());
		} else {
			binddto.setSrecvorgcode(loginfo.getSorgName());
			binddto.setSsendorgcode(seldto.getSorgcode());
		}
		// ��ø����б�
		try {
			binddto.setSdemo(queryLogsService.queryContent(seldto.getSendno()));
			attachFiles = (List<String>) queryLogsService.queryAttach(seldto
					.getSendno());
			attachFileNames = "";
			for (String file : attachFiles) {
				attachFileNames += FileOperFacade.getFileName(file) + "\n";
			}
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.queryLogDetail(o);
	}

	/**
	 * ����һ���Ѿ����͵���Ϣ
	 */
	public String recvDelete(Object o) {
		try{
			if ((seldto == null) || (seldto.getSopertype() == null)){
				MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ϵķ�����־��");
				return "";
			}
			if (seldto.getSopertype().equals(StateConstant.LOG_SEND)){
				//�����Ѿ����͵���Ϣ
				if (ITFECommonConstant.STATUS_FINISHED.equals(seldto.getSretcode())){
					//�����Ѿ�������ϣ���������
					MessageDialog.openMessageDialog(null, "�����Ѿ�������ϣ��������ϡ�");
				}else if (ITFECommonConstant.STATUS_CANCELED.equals(seldto.getSretcode())){
					//�Ѿ����ϣ������ظ�����
					MessageDialog.openMessageDialog(null, "�����Ѿ����ϡ�");
				}else if (ITFECommonConstant.STATUS_SUCCESS.equals(seldto.getSretcode())){
					if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(editor.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�Ҫ����ѡ�еļ�¼��")){
						//���ϱ���
						queryLogsService.cancelSend(seldto);
						//���¼�����־��Ϣ
						PageRequest pageRequest = new PageRequest();
						PageResponse pageResponse = retrieve(pageRequest);
						pagingcontext.setPage(pageResponse);
						editor.fireModelChanged();
						MessageDialog.openMessageDialog(null, "ҵ��ƾ֤���ϳɹ���");
					}
				}else{
					MessageDialog.openMessageDialog(null, "ҵ��ƾ֤û�з��ͳɹ����������ϡ�");
				}
			}else{
				MessageDialog.openMessageDialog(null, "ֻ�������Լ����͵ı��ġ�");
			}
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.recvDelete(o);
	}

	/**
	 * Direction: ������־��ѯ ename: returnQueryLogs ���÷���: viewers: �շ���־��ѯ messages:
	 */
	public String returnQueryLogs(Object o) {

		return "�շ���־��ѯ";
	}

	/**
	 * Direction: ��ѡһ����¼ ename: selOneRecode ���÷���: viewers: * messages:
	 */
	public String selOneRecode(Object o) {
		seldto = (TvRecvLogShowDto) o;
		return super.selOneRecode(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest pageRequest) {

		try {
			return queryLogsService.queryLog(dto, pageRequest);
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	/**
	 * ��������
	 */
	public String attachDownload(Object o) {
		// ѡ�񱣴�·��
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String strPath = path.open();
		if ((null == strPath) || (strPath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ�������ļ�����·����");
			return "";
		}
		// �������и���
		try {
			for (String file : attachFiles) {
				// ���ļ����ص�����Ŀ¼��
				ClientFileTransferUtil.downloadFile(file, strPath + "/"
						+ FileOperFacade.getFileName(file));
			}
			MessageDialog.openMessageDialog(null, "�����Ѿ����浽·��" + strPath + "�С�");
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}
		return super.attachDownload(o);
	}
	
	/**
	 * ʵ��ִ�в�ѯ�ķ��� 
	 */
	private boolean doQuery(Object o){
		dto = new TvSendlogDto();
		dto.setSdate(selDate);
		dto.setSoperationtypecode(selfiletype);
		if (null == selopertype || "".equals(selopertype.trim())) {
			MessageDialog.openMessageDialog(null, "�������ͱ���ѡ��");
			return false;
		} else {
			dto.setSdemo(selopertype);
		}
		try {
			PageRequest pageRequest = new PageRequest();
			PageResponse pageResponse = retrieve(pageRequest);
			pagingcontext.setPage(pageResponse);
			return true;
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return false;
		}
	}

}
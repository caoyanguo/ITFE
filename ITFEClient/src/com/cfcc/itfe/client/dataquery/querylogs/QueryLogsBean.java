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
 * @time 09-10-15 16:04:50 子系统: DataQuery 模块:querylogs 组件:Querylogs
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
	 * Direction: 收发日志查询 ename: queryLogs 引用方法: viewers: 日志查询结果 messages:
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
	 * Direction: 日志明细查询 ename: queryLogDetail 引用方法: viewers: 日志详细信息 messages:
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
		if (seldto.getSopertype().equals(StateConstant.LOG_SEND)) {// 发送
			binddto.setSsendorgcode(loginfo.getSorgName());
			binddto.setSrecvorgcode(seldto.getSorgcode());
		} else {
			binddto.setSrecvorgcode(loginfo.getSorgName());
			binddto.setSsendorgcode(seldto.getSorgcode());
		}
		// 获得附件列表
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
	 * 作废一条已经发送的信息
	 */
	public String recvDelete(Object o) {
		try{
			if ((seldto == null) || (seldto.getSopertype() == null)){
				MessageDialog.openMessageDialog(null, "请选择要作废的发送日志。");
				return "";
			}
			if (seldto.getSopertype().equals(StateConstant.LOG_SEND)){
				//作废已经发送的信息
				if (ITFECommonConstant.STATUS_FINISHED.equals(seldto.getSretcode())){
					//报文已经处理完毕，不能作废
					MessageDialog.openMessageDialog(null, "报文已经处理完毕，不能作废。");
				}else if (ITFECommonConstant.STATUS_CANCELED.equals(seldto.getSretcode())){
					//已经作废，不必重复作废
					MessageDialog.openMessageDialog(null, "报文已经作废。");
				}else if (ITFECommonConstant.STATUS_SUCCESS.equals(seldto.getSretcode())){
					if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(editor.getCurrentComposite().getShell(), "提示", "是否要作废选中的记录？")){
						//作废报文
						queryLogsService.cancelSend(seldto);
						//重新检索日志信息
						PageRequest pageRequest = new PageRequest();
						PageResponse pageResponse = retrieve(pageRequest);
						pagingcontext.setPage(pageResponse);
						editor.fireModelChanged();
						MessageDialog.openMessageDialog(null, "业务凭证作废成功。");
					}
				}else{
					MessageDialog.openMessageDialog(null, "业务凭证没有发送成功，不必作废。");
				}
			}else{
				MessageDialog.openMessageDialog(null, "只能作废自己发送的报文。");
			}
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.recvDelete(o);
	}

	/**
	 * Direction: 返回日志查询 ename: returnQueryLogs 引用方法: viewers: 收发日志查询 messages:
	 */
	public String returnQueryLogs(Object o) {

		return "收发日志查询";
	}

	/**
	 * Direction: 单选一条记录 ename: selOneRecode 引用方法: viewers: * messages:
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
	 * 附件下载
	 */
	public String attachDownload(Object o) {
		// 选择保存路径
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String strPath = path.open();
		if ((null == strPath) || (strPath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择下载文件保存路径。");
			return "";
		}
		// 下载所有附件
		try {
			for (String file : attachFiles) {
				// 将文件下载到下载目录中
				ClientFileTransferUtil.downloadFile(file, strPath + "/"
						+ FileOperFacade.getFileName(file));
			}
			MessageDialog.openMessageDialog(null, "附件已经保存到路径" + strPath + "中。");
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}
		return super.attachDownload(o);
	}
	
	/**
	 * 实际执行查询的方法 
	 */
	private boolean doQuery(Object o){
		dto = new TvSendlogDto();
		dto.setSdate(selDate);
		dto.setSoperationtypecode(selfiletype);
		if (null == selopertype || "".equals(selopertype.trim())) {
			MessageDialog.openMessageDialog(null, "操作类型必须选择");
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
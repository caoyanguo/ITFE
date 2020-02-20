package com.cfcc.itfe.client.dataquery.querymsglog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.BursarAffirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
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
 * @author zhouchuan
 * @time 09-12-11 11:55:00 子系统: DataQuery 模块:queryMsgLog 组件:QueryMsgLog
 */
public class QueryMsgLogBean extends AbstractQueryMsgLogBean implements
		IPageDataProvider {

	private TvRecvlogDto finddto = null; // 查询条件DTO对象
	private PagingContext msgtablepage; // 分页控件
	private TvRecvlogDto oneRecorddto = null; // 查询条件DTO对象
	private ITFELoginInfo loginfo = null;
	private List selectRs=null;
	private String startdate;
	private String enddate;
	private List trelist;
	private List finorglist;
	public QueryMsgLogBean() {
		super();
		selectRs=new ArrayList();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		finddto = new TvRecvlogDto();
		finddto.setSsenddate(TimeFacade.getCurrentStringTime());
		finddto.setSdemo("1");// 默认为发送日志
		//finddto.setSifsend("1");
		startdate=TimeFacade.getCurrentStringTime();
		enddate=startdate;
		msgtablepage = new PagingContext(this);
		init();

	}
	private void init(){
		TsTreasuryDto tredto=new TsTreasuryDto();
		TsConvertfinorgDto findto=new TsConvertfinorgDto();
		if(!StateConstant.ORG_CENTER_CODE.equals(loginfo.getSorgcode()) && !StateConstant.STAT_CENTER_CODE.equals(loginfo.getSorgcode())){
			findto.setSorgcode(loginfo.getSorgcode());
			tredto.setSorgcode(loginfo.getSorgcode());
		}
		try {
			trelist=commonDataAccessService.findRsByDto(tredto);
			finorglist=commonDataAccessService.findRsByDto(findto);
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}
	}
	public String refreshrs(Object o){
		return this.searchMsgLog(o);
	}

	
	/**
	 * Direction: 报文收发日志查询 ename: searchMsgLog 引用方法: viewers: 日志查询结果 messages:
	 */
	public String searchMsgLog(Object o) {
		// 中心机构代码
		String centerorg = null;
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		if (!loginfo.getSorgcode().equals(centerorg)) {
			// finddto.setStrecode(loginfo.getTrecode()); //当前登陆机构对应国库代码
			// finddto.setSbillorg(loginfo.getSorgcode()); //出票单位就是登陆机构代码
		}
		if(null!=startdate&&!"".equals(startdate)&&null!=enddate&&!"".equals(enddate)){
			if (Integer.parseInt(startdate)>Integer.parseInt(enddate)) {
				MessageDialog.openMessageDialog(null, "查询起始日期应小于查询结束日期！");
				return null;
			}
		}
		
		if (StringUtils.isBlank(finddto.getSdemo())) {
			MessageDialog.openMessageDialog(null, "操作类型必须选择!");
			return super.backSearch(o);
		}

		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		msgtablepage.setPage(pageResponse);

		if (pageResponse.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录!");
			return super.backSearch(o);
		}

		editor.fireModelChanged();
		String opertype = finddto.getSdemo(); // 取得操作类型
		if (StateConstant.LOG_SEND.equals(opertype)) {
			return super.searchMsgLog(o);
		}else{
			return "接收日志查询结果";
		}
		
	}
	public String selectAll(Object o){
		List curlist=msgtablepage.getPage().getData();
		if(selectRs!=null&&selectRs.size()==curlist.size()){
			selectRs=new ArrayList();
			this.editor.fireModelChanged();
			return "";
		}
		selectRs=new ArrayList();
		selectRs.addAll(curlist);
		this.editor.fireModelChanged();
		return "";
	}

	/**
	 * Direction: 返回日志查询界面 ename: backSearch 引用方法: viewers: 报文收发日志查询 messages:
	 */
	public String backSearch(Object o) {
		return super.backSearch(o);
	}

	/**
	 * Direction: 日志单击事件 ename: singleClickLog 引用方法: viewers: 日志查询结果 messages:
	 */
	public String singleClickLog(Object o) {
		oneRecorddto = (TvRecvlogDto) o;
		return super.singleClickLog(o);
	}

	/**
	 * Direction: 报文日志下载 ename: download 引用方法: viewers: * messages:
	 */
	public String download(Object o) {
		if (oneRecorddto == null) {
			MessageDialog.openMessageDialog(null, "请选中某一条记录!");
			return super.download(o);
		}

		// 客户端下载报文的路径
		String dirsep = File.separator; // 取得系统分割符
		dirsep = "/";
		String clientpath = "c:" + dirsep + "client" + dirsep + "msg" + dirsep
				+ oneRecorddto.getSsenddate() + dirsep + loginfo.getSorgcode()
				+ dirsep;
		File dir = new File(clientpath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try {
			int lastIndex = oneRecorddto.getStitle().replace("\\", "/").lastIndexOf(dirsep);
			String clientfile = clientpath
					+ oneRecorddto.getStitle().substring(lastIndex + 1);
			ClientFileTransferUtil.downloadFile(oneRecorddto.getStitle()
					.replace(queryLogsService.getFileRootPath(), ""),
					clientfile);
			MessageDialog.openMessageDialog(null, "报文下载成功！\n" + clientfile);
		} catch (Exception e) {
			log.error("报文下载过程中出现错误", e);
			MessageDialog.openErrorDialog(null, e);
			return super.download(o);
		}
		return super.download(o);
	}
	/**
	 * Direction: 接收报文日志下载 ename: download 引用方法: viewers: * messages:
	 */
	public String downloadAll(Object o) {
		if (selectRs == null||selectRs.size()<=0) {
			MessageDialog.openMessageDialog(null, "请选中某一条记录!");
			return null;
		}
		// 客户端下载报文的路径
		String dirsep = File.separator; // 取得系统分割符
		dirsep = "/";
		try {
			for(TvRecvlogDto recvDto:(List<TvRecvlogDto>)selectRs){
				String clientpath = "c:" + dirsep + "client" + dirsep + "msg" + dirsep
				+ recvDto.getSsenddate() + dirsep + loginfo.getSorgcode()
				+ dirsep;
				File dir = new File(clientpath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				int lastIndex = recvDto.getStitle().lastIndexOf(dirsep);
				String clientfile = clientpath
						+ recvDto.getStitle().substring(lastIndex + 1);
				ClientFileTransferUtil.downloadFile(recvDto.getStitle()
						.replace(queryLogsService.getFileRootPath(), ""),
						clientfile);
			}
			MessageDialog.openMessageDialog(null, "报文批量下载成功！\n"+"下载存放路径：c:" + dirsep + "client" + dirsep + "msg" );
		} catch (Exception e) {
			log.error("报文下载过程中出现错误", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		
		selectRs=new ArrayList();
		return super.downloadAll(o);
	}

	/**
	 * Direction: 报文重发 ename: msgresend 引用方法: viewers: * messages:
	 */
	public String msgresend(Object o) {
		if (oneRecorddto == null) {
			MessageDialog.openMessageDialog(null, "请选中某一条记录!");
			return super.msgresend(o);
		}
		if ( !(finddto instanceof TvRecvlogDto))  {
			MessageDialog.openMessageDialog(null, "只能对【发送日志】的记录进行重发!!");
			return super.msgresend(o);
		}
		String retcode=oneRecorddto.getSretcode();
		
		String strecode = oneRecorddto.getStrecode();
		if(strecode == null || strecode.trim().equals("")){
			MessageDialog.openMessageDialog(null, "所选记录国库代码为空，不允许重发");
			return null;
		}
		TsTreasuryDto trecodeDto = new TsTreasuryDto();
		trecodeDto.setStrecode(strecode);
		try {
			List<TsTreasuryDto> treDtoList = commonDataAccessService.findRsByDto(trecodeDto);
			if(treDtoList == null || treDtoList.size()==0){
				MessageDialog.openMessageDialog(null, "所选记录国库代码在国库代码参数中未维护！");
				return null;
			}
			String ls_OperationType  = oneRecorddto.getSoperationtypecode();
			if(treDtoList.get(0).getStreattrib().equals("2")){ //代理库
				if(ls_OperationType.equals(MsgConstant.MSG_TBS_NO_1000) || ls_OperationType.equals(MsgConstant.MSG_TBS_NO_3001)){
					if(retcode.equals(DealCodeConstants.DEALCODE_ITFE_RECEIPT)||retcode.equals(DealCodeConstants.DEALCODE_ITFE_CHECK)
							||retcode.equals(DealCodeConstants.DEALCODE_ITFE_RECEIVER)|| retcode.equals(DealCodeConstants.DEALCODE_ITFE_SEND)
							||retcode.equals(DealCodeConstants.DEALCODE_ITFE_CANCELLATION)){
						MessageDialog.openMessageDialog(null, "该状态的业务包或者是回执包不能进行重发，请检查业务包状态!");
						return super.msgresend(o);
					}
				}else{
					MessageDialog.openMessageDialog(null, "只有资金报文1000和发出的通用回执3001可以重发!");
					return super.msgresend(o);
				}
			}else{
				if(queryLogsService.isBizMsgNo(ls_OperationType)||ls_OperationType.equals("3139")||ls_OperationType.equals("3129")||ls_OperationType.equals("3128")){
					if(queryLogsService.isBizMsgNo(ls_OperationType)){
						if(retcode.equals(DealCodeConstants.DEALCODE_ITFE_RECEIPT)||retcode.equals(DealCodeConstants.DEALCODE_ITFE_CHECK)
								||retcode.equals(DealCodeConstants.DEALCODE_ITFE_RECEIVER)|| retcode.equals(DealCodeConstants.DEALCODE_ITFE_SEND)
								||retcode.equals(DealCodeConstants.DEALCODE_ITFE_CANCELLATION)){
							MessageDialog.openMessageDialog(null, "该状态的业务包不能进行重发，请检查业务包状态!");
							return super.msgresend(o);
						}
					}
				}else{
					MessageDialog.openMessageDialog(null, "非业务报文，不能进行重发!!");
					return super.msgresend(o);
				}
			}
			queryLogsService.resendMsg(oneRecorddto.getSsendno());

			PageRequest pageRequest = new PageRequest();
			PageResponse pageResponse = retrieve(pageRequest);
			msgtablepage.setPage(pageResponse);
			editor.fireModelChanged();
			MessageDialog.openMessageDialog(null, "重发成功!");
		} catch (ITFEBizException e) {
			log.error("报文重发过程中出现错误", e);
			MessageDialog.openErrorDialog(null, e);
			return super.msgresend(o);
		}
		return super.msgresend(o);
	}
	
	/**
	 * Direction: 设置失败
	 */
	public String updateFail(Object o) {
		if(oneRecorddto==null){
			MessageDialog.openMessageDialog(null, "请选中一条记录!!");
			return super.updateFail(o);
		}
		//打开授权窗口
		String msg = "需要主管授权才能设置报文状态为失败！";
		if(!AdminConfirmDialogFacade.open(msg)){
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			return null;
		}
		String ls_OperationType  = oneRecorddto.getSoperationtypecode();
		String strecode = oneRecorddto.getStrecode();
		if(strecode == null || strecode.trim().equals("")){
			MessageDialog.openMessageDialog(null, "所选记录国库代码为空，不允许设置失败");
			return null;
		}
		TsTreasuryDto trecodeDto = new TsTreasuryDto();
		trecodeDto.setStrecode(strecode);
		try {
			List<TsTreasuryDto> treDtoList = commonDataAccessService.findRsByDto(trecodeDto);
			if(treDtoList == null || treDtoList.size()==0){
				MessageDialog.openMessageDialog(null, "所选记录国库代码在国库代码参数中未维护！");
				return null;
			}
			if(treDtoList.get(0).getStreattrib().equals("2")){ //代理库
				if ( !(finddto instanceof TvRecvlogDto))  {
					MessageDialog.openMessageDialog(null, "只能对【发送日志】的记录设置失败!!");
					return null;
				}
				if(!(MsgConstant.MSG_TBS_NO_1000.equals(ls_OperationType) || MsgConstant.MSG_TBS_NO_3001.equals(ls_OperationType))){
					MessageDialog.openMessageDialog(null, "只能对发出的资金报文(1000)或者是发出的通用回执日志设置失败!!");
					return null;
				}
			}else{
				if(!queryLogsService.isBizMsgNo(ls_OperationType)){
					MessageDialog.openMessageDialog(null, "非业务报文，不能设置失败状态!!");
					return super.updateFail(o);
				}
			}
			queryLogsService.updateFail(oneRecorddto.getSsendno(),oneRecorddto.getSpackno(),oneRecorddto.getSoperationtypecode(),oneRecorddto.getSdate());
			PageRequest pageRequest = new PageRequest();
			PageResponse pageResponse = retrieve(pageRequest);
			msgtablepage.setPage(pageResponse);
			MessageDialog.openMessageDialog(null, "设置状态执行成功!");
			editor.fireModelChanged();
		}  catch (ITFEBizException e) {
			log.error("设置状态过程中出现错误", e);
			MessageDialog.openErrorDialog(null, e);
			return super.msgresend(o);
		}
		return super.updateFail(o);
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
			/*PageResponse pr=queryLogsService.queryMsgLog(finddto, pageRequest);
			List<>*/
			 if(StateConstant.LOG_RECV.equals(finddto.getSdemo().trim())){
					pageRequest.setPageSize(50);
				}
			return queryLogsService.queryMsgLog(finddto, pageRequest,startdate,enddate);
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return super.retrieve(pageRequest);
	}

	public PagingContext getMsgtablepage() {
		return msgtablepage;
	}

	public void setMsgtablepage(PagingContext msgtablepage) {
		this.msgtablepage = msgtablepage;
	}

	public TvRecvlogDto getFinddto() {
		return finddto;
	}

	public void setFinddto(TvRecvlogDto finddto) {
		this.finddto = finddto;
	}

	public TvRecvlogDto getOneRecorddto() {
		return oneRecorddto;
	}
	

	public List getSelectRs() {
		return selectRs;
	}

	public void setSelectRs(List selectRs) {
		this.selectRs = selectRs;
	}

	public void setOneRecorddto(TvRecvlogDto oneRecorddto) {
		this.oneRecorddto = oneRecorddto;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public List getTrelist() {
		return trelist;
	}

	public void setTrelist(List trelist) {
		this.trelist = trelist;
	}

	public List getFinorglist() {
		return finorglist;
	}

	public void setFinorglist(List finorglist) {
		this.finorglist = finorglist;
	}
	
}
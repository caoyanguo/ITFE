package com.cfcc.itfe.client.dataquery.payoutfinancequery;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.dataquery.payoutfinancequery.IPayOutFinanceQueryService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.page.HisMainPayOutFinanceBean;
import com.cfcc.itfe.client.common.page.HisSubPayOutFinanceBean;
import com.cfcc.itfe.client.common.page.MainPayOutFinanceBean;
import com.cfcc.itfe.client.common.page.SubPayOutFinanceBean;
import com.cfcc.itfe.client.dialog.BursarAffirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtvPayoutfinanceSubDto;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPayOutFinanceReportDto;
import com.cfcc.itfe.persistence.dto.TvPayOutFinanceWZReportDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceMainDto;
import com.cfcc.itfe.persistence.dto.HtvPayoutfinanceMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceSubDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * codecomment: 
 * @author t60
 * @time   12-02-23 15:31:47
 * 子系统: DataQuery
 * 模块:PayOutFinanceQuery
 * 组件:PayOutFinanceQuery
 */
public class PayOutFinanceQueryBean extends AbstractPayOutFinanceQueryBean implements IPageDataProvider {
	private TvPayoutfinanceMainDto finddto;
	private TvPayoutfinanceMainDto operdto;
	private HtvPayoutfinanceMainDto hisoperdto;
	private TvPayoutfinanceMainDto checkdto ;//更新状态用DTO
	private ITFELoginInfo loginfo ;
	private String selectedtable;
    private List statelist;
	private List statelist2 ;
	/** 属性列表 */
	MainPayOutFinanceBean mainPayOutFinanceBean = null;
	SubPayOutFinanceBean subPayOutFinanceBean = null;
	HisMainPayOutFinanceBean hisMainPayOutFinanceBean=null;
	HisSubPayOutFinanceBean hisSubPayOutFinanceBean=null;
    private static Log log = LogFactory.getLog(PayOutFinanceQueryBean.class);
    
	// 在主信息列表选择用list
	private List<TvPayoutfinanceMainDto> selectedlist = new ArrayList<TvPayoutfinanceMainDto>();
	// 在主信息列表选择用list
	private List<HtvPayoutfinanceMainDto> hselectedlist = new ArrayList<HtvPayoutfinanceMainDto>();
    
    private List reportRs = null;
	private Map reportmap = new HashMap();
	private String reportPath = "com/cfcc/itfe/client/ireport/payOutFinanceReport.jasper";
	private List reportRswz=null;
	private Map reportmapwz=new HashMap();
	private String reportPathwz="com/cfcc/itfe/client/ireport/payoutFinancewzReport.jasper";
    public PayOutFinanceQueryBean() {
      super();
      finddto=new TvPayoutfinanceMainDto();
      operdto=new TvPayoutfinanceMainDto();
      hisoperdto=new HtvPayoutfinanceMainDto();
      checkdto=new TvPayoutfinanceMainDto();
      selectedtable="0";
	  initStatelist();
	  reportRs = new ArrayList<TvPayOutFinanceReportDto>();
	  reportRswz=new ArrayList<TvPayOutFinanceWZReportDto>();
	  setMainPayOutFinanceBean(new MainPayOutFinanceBean(payOutFinanceQueryService, finddto));
		setSubPayOutFinanceBean(new SubPayOutFinanceBean(payOutFinanceQueryService, finddto));
		setHisMainPayOutFinanceBean(new HisMainPayOutFinanceBean(payOutFinanceQueryService,new HtvPayoutfinanceMainDto()));
		setHisSubPayOutFinanceBean(new HisSubPayOutFinanceBean(payOutFinanceQueryService,new HtvPayoutfinanceMainDto()));
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
		finddto.setSentrustdate(TimeFacade.getCurrentStringTime());
    }
    /**
	 * Direction: 退库对账单打印
	 * ename: queryPrint
	 * 引用方法: 
	 * viewers: 实拨资金对账单
	 * messages: 
	 */
    public String queryPrint(Object o){
    
    	try {
    		finddto.setSbookorgcode(loginfo.getSorgcode());
			reportRs=payOutFinanceQueryService.searchForReport(selectedtable, finddto);
		} catch (ITFEBizException e1) {
			MessageDialog.openErrorDialog(null, e1);
			if(selectedtable.equals("0")){
    			return "批量拨付信息列表";
    		}else if(selectedtable.equals("1")){
    			return "批量拨付信息列表(历史表)";
    		}
		}
    	if(null == reportRs || reportRs.size() == 0){
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录!");
			if(selectedtable.equals("0")){
    			return "批量拨付信息列表";
    		}else if(selectedtable.equals("1")){
    			return "批量拨付信息列表(历史表)";
    		}
		}
    	
    	reportmap.put("title", "批量拨付对账单");
    	reportmap.put("printdate", new SimpleDateFormat("yyyy年MM月dd日").format(new java.util.Date()));
    	reportmap.put("worker",loginfo.getSuserName());
    	
        return super.queryPrint(o);
    }
    /**
	 * Direction: 往帐清单打印
	 * ename: wzqueryPrint
	 * 引用方法: 
	 * viewers: 批量拨付往帐清单
	 * messages: 
	 */
    public String wzqueryPrint(Object o){
    	if((operdto==null||operdto.getIvousrlno()==null)&&(hisoperdto==null||hisoperdto.getIvousrlno()==null)){
    		MessageDialog.openMessageDialog(null, "请选择一条记录");
    		if(selectedtable.equals("0")){
    			return "批量拨付信息列表";
    		}else if(selectedtable.equals("1")){
    			return "批量拨付信息列表(历史表)";
    		}
    		
    	}
    	BigDecimal totalmoney=new BigDecimal(0);
    	try {
    		String status="";
    		if(operdto.getSstatus()!=null&&!operdto.getSstatus().equals("")){
    			TdEnumvalueDto enumDto=new TdEnumvalueDto();
    			enumDto.setStypecode("0412");
    			enumDto.setSvalue(operdto.getSstatus());
    			enumDto=((List<TdEnumvalueDto>)commonDataAccessService.findRsByDto(enumDto)).get(0);
    			status=enumDto.getSvaluecmt();
    			}
    	if(selectedtable.equals("0")){
    		TvPayoutfinanceSubDto subdto = new TvPayoutfinanceSubDto();
        	subdto.setIvousrlno(operdto.getIvousrlno());
			List<TvPayoutfinanceSubDto> subdtos=commonDataAccessService.findRsByDto(subdto);
			for(TvPayoutfinanceSubDto psubdto:subdtos){
				TvPayOutFinanceWZReportDto rdto=new TvPayOutFinanceWZReportDto();
				rdto.setIvousrlno(operdto.getIvousrlno());
				rdto.setNsubamt(psubdto.getNsubamt());
				rdto.setSpayeeacct(psubdto.getSpayeeacct());
				rdto.setSpayeename(psubdto.getSpayeename());
				rdto.setSpayeeopnbnkno(psubdto.getSpayeeopnbnkno());
				rdto.setSpayeracct(operdto.getSpayeracct());
				rdto.setSpayername(operdto.getSpayername());
				rdto.setSpayoutvoutype(operdto.getSpayoutvoutype());
				rdto.setSresult(status);
				rdto.setSvoudate(operdto.getSvoudate());
				rdto.setSvouno(operdto.getSvouno());
				totalmoney=totalmoney.add(psubdto.getNsubamt());
				reportRswz.add(rdto);
	    	}
		}else if(selectedtable.equals("1")){
			HtvPayoutfinanceSubDto subdto = new HtvPayoutfinanceSubDto();
    		subdto.setIvousrlno(hisoperdto.getIvousrlno());
			List<HtvPayoutfinanceSubDto> subdtos=commonDataAccessService.findRsByDto(subdto);
			for(HtvPayoutfinanceSubDto psubdto:subdtos){
				TvPayOutFinanceWZReportDto rdto=new TvPayOutFinanceWZReportDto();
				rdto.setIvousrlno(hisoperdto.getIvousrlno());
				rdto.setNsubamt(psubdto.getNsubamt());
				rdto.setSpayeeacct(psubdto.getSpayeeacct());
				rdto.setSpayeename(psubdto.getSpayeename());
				rdto.setSpayeeopnbnkno(psubdto.getSpayeeopnbnkno());
				rdto.setSpayeracct(hisoperdto.getSpayeracct());
				rdto.setSpayername(hisoperdto.getSpayername());
				rdto.setSpayoutvoutype(hisoperdto.getSpayoutvoutype());
				rdto.setSresult(status);
				rdto.setSvoudate(hisoperdto.getSvoudate());
				rdto.setSvouno(hisoperdto.getSvouno());
				totalmoney=totalmoney.add(psubdto.getNsubamt());
				reportRswz.add(rdto);
	    	}
		}
    	} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			if(selectedtable.equals("0")){
    			return "批量拨付信息列表";
    		}else if(selectedtable.equals("1")){
    			return "批量拨付信息列表(历史表)";
    		}
		}
    	if(null == reportRswz || reportRswz.size() == 0){
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录!");
			if(selectedtable.equals("0")){
    			return "批量拨付信息列表";
    		}else if(selectedtable.equals("1")){
    			return "批量拨付信息列表(历史表)";
    		}
		}
    	reportmapwz.put("title", "批量拨付往帐清单");
    	reportmapwz.put("printdate", new SimpleDateFormat("yyyy年MM月dd日").format(new java.util.Date()));
    	reportmapwz.put("totalcount", reportRswz.size());
    	reportmapwz.put("totalmoney", totalmoney);
    	reportmapwz.put("worker",loginfo.getSuserName());
        return "批量拨付往帐清单";
    }

	/**
	 * Direction: 查询列表事件
	 * ename: searchList
	 * 引用方法: 
	 * viewers: 批量拨付信息列表
	 * messages: 
	 */
    public String searchList(Object o){
    	PageRequest mainpageRequest = new PageRequest();
		PageResponse response=null;
		if(selectedtable.equals("0")){
			this.getMainPayOutFinanceBean().setMaindto(finddto);
			response = this.getMainPayOutFinanceBean().retrieve(mainpageRequest);
		}else if(selectedtable.equals("1")){
			HtvPayoutfinanceMainDto htvdto=new HtvPayoutfinanceMainDto();
			htvdto.setStrecode(finddto.getStrecode());
			htvdto.setIvousrlno(finddto.getIvousrlno());
			htvdto.setSbillorg(finddto.getSbillorg());
			htvdto.setSpayeebankno(finddto.getSpayeebankno());
			htvdto.setSpayeracct(finddto.getSpayeracct());
			htvdto.setSpayername(finddto.getSpayername());
			htvdto.setSentrustdate(finddto.getSentrustdate());
			htvdto.setSpackageno(finddto.getSpackageno());
			htvdto.setSpayoutvoutype(finddto.getSpayoutvoutype());
			htvdto.setSvouno(finddto.getSvouno());
			htvdto.setSvoudate(finddto.getSvoudate());
			htvdto.setSfilename(finddto.getSfilename());
			this.getHisMainPayOutFinanceBean().setMaindto(htvdto);
			response=this.getHisMainPayOutFinanceBean().retrieve(mainpageRequest);
			if (response.getTotalCount() <= 0) {
				MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录!");
				this.finddto=new TvPayoutfinanceMainDto();
				return super.rebackSearch(o);
			}
			
			editor.fireModelChanged();
			return "批量拨付信息列表(历史表)";
		}
		
		if (response.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录!");
//			this.finddto=new TvPayoutfinanceMainDto();
			return super.rebackSearch(o);
		}
		
		editor.fireModelChanged();
		operdto = new TvPayoutfinanceMainDto();
		return "批量拨付信息列表";
    }

	/**
	 * Direction: 返回查询界面
	 * ename: rebackSearch
	 * 引用方法: 
	 * viewers: 批量拨付查询界面
	 * messages: 
	 */
    public String rebackSearch(Object o){
    	this.operdto=new TvPayoutfinanceMainDto();
    	this.hisoperdto=new HtvPayoutfinanceMainDto();
    	this.finddto=new TvPayoutfinanceMainDto();
          return super.rebackSearch(o);
    }
    public String rebackInfo(Object o){
    	this.reportmap=new HashMap();
    	this.reportmapwz=new HashMap();
    	this.reportRs=new ArrayList();
    	this.reportRswz=new ArrayList();
    	if(selectedtable.equals("0")){
			return "批量拨付信息列表";
		}else if(selectedtable.equals("1")){
			return "批量拨付信息列表(历史表)";
		}
    	return "";
    }

	/**
	 * Direction: 主信息单击事件
	 * ename: singleclickMain
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleclickMain(Object o){
    	PageRequest subpageRequest = new PageRequest();
		
		if(selectedtable.equals("0")){
			operdto = (TvPayoutfinanceMainDto) o;
			this.getSubPayOutFinanceBean().setMaindto(operdto);
			this.getSubPayOutFinanceBean().retrieve(subpageRequest);
		}else if(selectedtable.equals("1")){
			hisoperdto=(HtvPayoutfinanceMainDto)o;
			this.getHisSubPayOutFinanceBean().setMaindto(hisoperdto);
			this.getHisSubPayOutFinanceBean().retrieve(subpageRequest);
		}
		
		editor.fireModelChanged();
		return super.singleclickMain(o);
    }
	/**
	 * Direction: 主信息双击事件
	 * ename: doubleclickMain
	 * 引用方法: 
	 * viewers: 实拨资金修改界面
	 * messages: 
	 */
    public String doubleclickMain(Object o){
        
    	PageRequest subpageRequest = new PageRequest();
		
		if(selectedtable.equals("0")){
			operdto = (TvPayoutfinanceMainDto) o;
			this.getSubPayOutFinanceBean().setMaindto(operdto);
			this.getSubPayOutFinanceBean().retrieve(subpageRequest);
		}else if(selectedtable.equals("1")){
			hisoperdto=(HtvPayoutfinanceMainDto)o;
			this.getHisSubPayOutFinanceBean().setMaindto(hisoperdto);
			this.getHisSubPayOutFinanceBean().retrieve(subpageRequest);
		}
		
		editor.fireModelChanged();
		return super.doubleclickMain(o);
    }
    /**
	 * Direction: 全选/反选
	 * ename: selectAllOrNone
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectAllOrNone(Object o){
    	if (this.mainPayOutFinanceBean.getMaintablepage() == null) {
			return super.selectAllOrNone(o);
		}
		PageResponse page = this.mainPayOutFinanceBean.getMaintablepage().getPage();
		if (page == null || page.getTotalCount() == 0) {
			return super.selectAllOrNone(o);
		}
		List<TvPayoutfinanceMainDto> templist = page.getData();
		if (templist != null && this.selectedlist != null) {
			if (selectedlist.size() != 0 && selectedlist.containsAll(templist)) {
				selectedlist.removeAll(templist);
			} else {
				for (int i = 0; i < templist.size(); i++) {
					if (selectedlist.contains(templist.get(i))) {
						selectedlist.set(selectedlist.indexOf(templist.get(i)),
								templist.get(i));
					} else {
						selectedlist.add(i, templist.get(i));
					}
				}
			}
		}
		this.editor.fireModelChanged();
		return super.selectAllOrNone(o);
    }
    /**
	 * Direction: 更新失败
	 * ename: updateFail
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String updateFail(Object o){
    	if(selectedlist.size()==0 || selectedlist == null){
			MessageDialog.openMessageDialog(null, "请选中更新状态的记录！");
			return null;
    	}
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定要设置选中的记录为失败吗？")) {
			return "";
		}
		//打开授权窗口
		String msg = "需要主管授权才能更新状态为失败！";
		if(!BursarAffirmDialogFacade.open(msg)){
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			return null;
		};
    	for (int i = 0; i < selectedlist.size(); i++) {
    		checkdto = selectedlist.get(i);
    		checkdto.setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
    		try {
    			commonDataAccessService.updateData(checkdto);
    		} catch (ITFEBizException e) {
    			MessageDialog.openErrorDialog(null, e);
    		}
    	}
    	editor.fireModelChanged();
    	selectedlist.clear();
    	MessageDialog.openMessageDialog(null, "更新状态成功！");   
        return super.updateFail(o);
    }
    
	/**
	 * Direction: 更新成功
	 * ename: updateSuccess
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String updateSuccess(Object o){
    	if(selectedlist.size()==0 || selectedlist == null){
			MessageDialog.openMessageDialog(null, "请选中更新状态的记录！");
			return null;
    	}
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定要设置选中的记录为成功吗？")) {
			return "";
		}
		//打开授权窗口
		String msg = "需要主管授权才能更新状态为成功！";
		if(!BursarAffirmDialogFacade.open(msg)){
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			return null;
		};
    	for (int i = 0; i < selectedlist.size(); i++) {
    		checkdto = selectedlist.get(i);
    		checkdto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
    		try {
    			commonDataAccessService.updateData(checkdto);
    		} catch (ITFEBizException e) {
    			MessageDialog.openErrorDialog(null, e);
    		}
    	}
    	editor.fireModelChanged();
    	selectedlist.clear();
    	MessageDialog.openMessageDialog(null, "更新状态成功！");   
        return super.updateSuccess(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}
    /**
	 * Direction: 导出 ename: dataExport 引用方法: viewers: * messages:
	 */
	public String dataExport(Object o) {
		
		// 选择保存路径
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		List<String> filelist = new ArrayList<String>();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
			return "";
		}
		String clientFilePath;
		String strdate = DateUtil.date2String2(new java.util.Date()); // 当前系统年月日
		String dirsep = File.separator;
		String serverFilePath;
		try {
			serverFilePath = payOutFinanceQueryService.dataexport(finddto,selectedtable).replace("\\",
					"/");
			int j = serverFilePath.lastIndexOf("/");
			String partfilepath  = serverFilePath.replaceAll(serverFilePath.substring(
					0, j + 1), "");
			clientFilePath = filePath + dirsep
					+ partfilepath;
			File f = new File(clientFilePath);
			File dir = new File(f.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			ClientFileTransferUtil.downloadFile(serverFilePath, clientFilePath);
			MessageDialog.openMessageDialog(null, "导出文件成功！文件保存在：\n"
					+ clientFilePath);

		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return "";
	}
    public void  initStatelist(){
    	this.statelist = new ArrayList<TdEnumvalueDto>();

		TdEnumvalueDto valuedto3 = new TdEnumvalueDto();
		valuedto3.setStypecode("处理中");
		valuedto3.setSvalue(DealCodeConstants.DEALCODE_ITFE_DEALING);
		this.statelist.add(valuedto3);
		
		TdEnumvalueDto valuedto4 = new TdEnumvalueDto();
		valuedto4.setStypecode("已收妥");
		valuedto4.setSvalue(DealCodeConstants.DEALCODE_ITFE_RECEIVER);
		this.statelist.add(valuedto4);
		    
		TdEnumvalueDto valuedto1 = new TdEnumvalueDto();
		valuedto1.setStypecode("成功");
		valuedto1.setSvalue(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
		this.statelist.add(valuedto1);
		
		TdEnumvalueDto valuedto2 = new TdEnumvalueDto();
		valuedto2.setStypecode("失败");
		valuedto2.setSvalue(DealCodeConstants.DEALCODE_ITFE_FAIL);
		this.statelist.add(valuedto2);
		
		TdEnumvalueDto valuedto5 = new TdEnumvalueDto();
		valuedto5.setStypecode("已重发");
		valuedto5.setSvalue(DealCodeConstants.DEALCODE_ITFE_REPEAT_SEND);
		this.statelist.add(valuedto5);
		
		this.statelist2 = new ArrayList<TdEnumvalueDto>();

		TdEnumvalueDto valuedtoa = new TdEnumvalueDto();
		valuedtoa.setStypecode("当前表");
		valuedtoa.setSvalue("0");
		this.statelist2.add(valuedtoa);
		
		TdEnumvalueDto valuedtob = new TdEnumvalueDto();
		valuedtob.setStypecode("历史表");
		valuedtob.setSvalue("1");
		this.statelist2.add(valuedtob);
    }

	public TvPayoutfinanceMainDto getFinddto() {
		return finddto;
	}

	public void setFinddto(TvPayoutfinanceMainDto finddto) {
		this.finddto = finddto;
	}

	public TvPayoutfinanceMainDto getOperdto() {
		return operdto;
	}

	public void setOperdto(TvPayoutfinanceMainDto operdto) {
		this.operdto = operdto;
	}

	public HtvPayoutfinanceMainDto getHisoperdto() {
		return hisoperdto;
	}

	public void setHisoperdto(HtvPayoutfinanceMainDto hisoperdto) {
		this.hisoperdto = hisoperdto;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public String getSelectedtable() {
		return selectedtable;
	}

	public void setSelectedtable(String selectedtable) {
		this.selectedtable = selectedtable;
	}

	public List getStatelist() {
		return statelist;
	}

	public void setStatelist(List statelist) {
		this.statelist = statelist;
	}

	public List getStatelist2() {
		return statelist2;
	}

	public void setStatelist2(List statelist2) {
		this.statelist2 = statelist2;
	}

	public MainPayOutFinanceBean getMainPayOutFinanceBean() {
		return mainPayOutFinanceBean;
	}

	public void setMainPayOutFinanceBean(MainPayOutFinanceBean mainPayOutFinanceBean) {
		this.mainPayOutFinanceBean = mainPayOutFinanceBean;
	}

	public SubPayOutFinanceBean getSubPayOutFinanceBean() {
		return subPayOutFinanceBean;
	}

	public void setSubPayOutFinanceBean(SubPayOutFinanceBean subPayOutFinanceBean) {
		this.subPayOutFinanceBean = subPayOutFinanceBean;
	}

	public HisMainPayOutFinanceBean getHisMainPayOutFinanceBean() {
		return hisMainPayOutFinanceBean;
	}

	public void setHisMainPayOutFinanceBean(
			HisMainPayOutFinanceBean hisMainPayOutFinanceBean) {
		this.hisMainPayOutFinanceBean = hisMainPayOutFinanceBean;
	}

	public HisSubPayOutFinanceBean getHisSubPayOutFinanceBean() {
		return hisSubPayOutFinanceBean;
	}

	public void setHisSubPayOutFinanceBean(
			HisSubPayOutFinanceBean hisSubPayOutFinanceBean) {
		this.hisSubPayOutFinanceBean = hisSubPayOutFinanceBean;
	}

	public List getReportRs() {
		return reportRs;
	}

	public void setReportRs(List reportRs) {
		this.reportRs = reportRs;
	}

	public Map getReportmap() {
		return reportmap;
	}

	public void setReportmap(Map reportmap) {
		this.reportmap = reportmap;
	}

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}
	public List getReportRswz() {
		return reportRswz;
	}
	public void setReportRswz(List reportRswz) {
		this.reportRswz = reportRswz;
	}
	public Map getReportmapwz() {
		return reportmapwz;
	}
	public void setReportmapwz(Map reportmapwz) {
		this.reportmapwz = reportmapwz;
	}
	public String getReportPathwz() {
		return reportPathwz;
	}
	public void setReportPathwz(String reportPathwz) {
		this.reportPathwz = reportPathwz;
	}
	public List<TvPayoutfinanceMainDto> getSelectedlist() {
		return selectedlist;
	}
	public void setSelectedlist(List<TvPayoutfinanceMainDto> selectedlist) {
		this.selectedlist = selectedlist;
	}
	public List<HtvPayoutfinanceMainDto> getHselectedlist() {
		return hselectedlist;
	}
	public void setHselectedlist(List<HtvPayoutfinanceMainDto> hselectedlist) {
		this.hselectedlist = hselectedlist;
	}
    
}
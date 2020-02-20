package com.cfcc.itfe.client.dataquery.checkstatusofreportdownload;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.StatusOfReportDownLoad;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.dataquery.querylogs.IQueryLogsService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;

/**
 * codecomment: 
 * @author db2admin
 * @time   13-04-19 14:21:47
 * 子系统: DataQuery
 * 模块:checkStatusOfReportDownload
 * 组件:CheckStatusOfReportDownload
 */
@SuppressWarnings("unchecked")
public class CheckStatusOfReportDownloadBean extends AbstractCheckStatusOfReportDownloadBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(CheckStatusOfReportDownloadBean.class);
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
    protected IQueryLogsService queryLogsService = (IQueryLogsService)getService(IQueryLogsService.class);
    private String reportpath = "com/cfcc/itfe/client/ireport/StatusOfDownLoadReport.jasper";
	private List reportRs = null;
	private List treCodeList = null;
	private String searchArea = null;
	private Map reportmap = new HashMap();
	private List<StatusOfReportDownLoad> pagingList = null;
	private ITFELoginInfo loginfo = null;
	private List voucherTypeList;
	private int gkcount=0;
	private int srcount=0;
	private int lscount=0;
	private int spcount=0;
	private int kccount=0;
	private int zccount=0;
	private int gdcount=0;
	private int hgcount=0;
	private int tkcount=0;
	private String busType;
    public CheckStatusOfReportDownloadBean() {
      super();
      pagingContext = new PagingContext(this);
      searchdate = java.sql.Date.valueOf(TimeFacade.getCurrent2StringTime()); 
      reportRs = new ArrayList();
      searchArea = "0";
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      init();
    }
    private void init()
    {
    	try
    	{
    		treCodeList=commonDataAccessService.getSubTreCode(loginfo.getSorgcode());
    	} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
    }
    
	/**
	 * Direction: 查询下载报表情况检查结果
	 * ename: search
	 * 引用方法: 
	 * viewers: 下载报表情况检查界面
	 * messages: 
	 */
    public String search(Object o){
    	PageRequest request = new PageRequest();
    	if(searchArea!=null&&!"".equals(searchArea.trim()))
    		strecode = "";
    	PageResponse response = retrieve(request);
    	if (response.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录!");
			 return "";
		}
    	pagingList = response.getData();
    	if(pagingList!=null&&pagingList.size()>0)
		{
    		gkcount = pagingList.size();
    		lscount=0;srcount=0;spcount=0;kccount=0;zccount=0;gdcount=0;hgcount=0;tkcount=0;
			for(int i=0;i<pagingList.size();i++)
			{
				if("1".equals(pagingList.get(i).getSliushui()))
					lscount++;
				if("1".equals(pagingList.get(i).getSribao()))
					srcount++;
				if("1".equals(pagingList.get(i).getSshuipiao()))
					spcount++;
				if("1".equals(pagingList.get(i).getSkucun()))
					kccount++;
				if("1".equals(pagingList.get(i).getSzhichu()))
					zccount++;
				if("1".equals(pagingList.get(i).getSext1()))
					gdcount++;
				if("1".equals(pagingList.get(i).getSishaiguan()))
					hgcount++;
				if(pagingList.get(i).getItuikucount()!=null&&pagingList.get(i).getItuikucount()>0)
					tkcount+=pagingList.get(i).getItuikucount();
			}
		}
		editor.fireModelChanged();
        return super.search(o);
    }
    /**
	 * Direction: 导出
	 * ename: exportcheckresult
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportcheckresult(Object o){
    	if(pagingList==null||pagingList.size()<=0)
    	{
    		MessageDialog.openMessageDialog(null, "没有需要导出的数据，请先查询需要导出的数据！");
    		return "";
    	}
    	// 选择保存路径
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
			return "";
		}
		String fileName = filePath+ File.separator+ "下发报表检查情况" +loginfo.getCurrentDate().replaceAll("-", "") + ".txt";
		try {
			StringBuffer resultStr = new StringBuffer();
			resultStr.append("日期,国库代码,国库名称,收入日报,入库流水,电子税票,库存日报,支出报表"+System.getProperty("line.separator"));
			for(StatusOfReportDownLoad tmp : pagingList){
				resultStr.append(tmp.getSdates()+","); //日期
				resultStr.append(tmp.getStrecode()+",");	//国库代码
				resultStr.append(tmp.getStrename()+",");	//国库名称
				resultStr.append(tmp.getSribao()+",");//日报下发情况
				resultStr.append(tmp.getSliushui()+",");//流水下发情况
				resultStr.append(tmp.getSshuipiao()+",");//税票下发情况
				resultStr.append(tmp.getSkucun()+",");//库存下发情况
				resultStr.append(tmp.getSzhichu()+System.getProperty("line.separator"));//支出导入情况
			}
			FileUtil.getInstance().writeFile(fileName, resultStr.toString());
			MessageDialog.openMessageDialog(null, "操作成功！");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
        return super.exportcheckresult(o);
    }
	/**
	 * Direction: 打印预览
	 * ename: queryprint
	 * 引用方法: 
	 * viewers: 下载报表情况检查结果报表界面
	 * messages: 
	 */
    public String queryprint(Object o){
		try {
			PageRequest request = new PageRequest();
			PageResponse  response = checkStatusOfReportDownloadService.searchStatusOfReportDownLoad(searchdate, strecode, request);
			List<StatusOfReportDownLoad> list = new ArrayList<StatusOfReportDownLoad>();
			list.addAll(response.getData());
			if(null == list || list.size() == 0) {
				MessageDialog.openMessageDialog(null, "没有找到相应记录!");
				return null;
			}
			if(list!=null&&list.size()>0)
			{
				for(int i=0;i<list.size();i++)
				{
					if("0".equals(list.get(i).getSliushui()))
						list.get(i).setSliushui("否");
					else
						list.get(i).setSliushui("是");
					if("0".equals(list.get(i).getSribao()))
						list.get(i).setSribao("否");
					else
						list.get(i).setSribao("是");
					if("0".equals(list.get(i).getSshuipiao()))
						list.get(i).setSshuipiao("否");
					else
						list.get(i).setSshuipiao("是");
					if("0".equals(list.get(i).getSkucun()))
						list.get(i).setSkucun("否");
					else
						list.get(i).setSkucun("是");
					if("0".equals(list.get(i).getSzhichu()))
						list.get(i).setSzhichu("否");
					else
						list.get(i).setSzhichu("是");
				}
			}
			reportRs.clear();
			reportRs.addAll(list);
			reportmap.put("printDate",  new SimpleDateFormat("yyyy年MM月dd日").format(new java.util.Date()));
			editor.fireModelChanged();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
          return super.queryprint(o);
    }
    /**
	 * Direction: 导出报表到服务器
	 * ename: exportToServer
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportToServer(Object o){
    	String exportdate = searchdate==null?TimeFacade.getCurrentStringTimebefor():StringUtil.replace(searchdate.toString(), "-", "");
    	if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否确认导出"+exportdate+"报表文件到服务器目录?")) {
    		try
    		{
    			checkStatusOfReportDownloadService.exportToServer(exportdate);
    			MessageDialog.openMessageDialog(null,"导出报表文件到服务器成功！");
    		} catch (ITFEBizException e) {
    			log.error(e);
    			MessageDialog.openMessageDialog(null,"导出报表文件到服务器失败！");
    		}
    	}
        return super.exportToServer(o);
    }
	/**
	 * Direction: 返回
	 * ename: reback
	 * 引用方法: 
	 * viewers: 下载报表情况检查界面
	 * messages: 
	 */
    public String reback(Object o){
//    	PageRequest request = new PageRequest();
//    	retrieve(request);
        return super.reback(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {
		try {
			if(strecode==null||"".equals(strecode.trim()))
			{
				List<TsTreasuryDto> findTreCodeList = null;
				if("0".equals(searchArea))
				{
					TsTreasuryDto findto = new TsTreasuryDto();
					findto.setSorgcode(loginfo.getSorgcode());
					findTreCodeList = commonDataAccessService.findRsByDto(findto);
				}else if("1".equals(searchArea))
				{
					findTreCodeList = commonDataAccessService.getSubTreCode(loginfo.getSorgcode());
				}
				StringBuffer temp = new StringBuffer("");
				for(int i=0;i<findTreCodeList.size();i++)
				{
					if(i==(findTreCodeList.size()-1))
						temp.append("'"+findTreCodeList.get(i).getStrecode()+"'");
					else
						temp.append("'"+findTreCodeList.get(i).getStrecode()+"',");
				}
				strecode = temp.toString();
			}else
			{
				if(strecode.indexOf("'")<0)
					strecode = "'"+strecode+"'";
			}
			
			return checkStatusOfReportDownloadService.searchStatusOfReportDownLoad(searchdate, strecode, arg0);
		} catch (ITFEBizException e) {
			e.printStackTrace();
			return null;
		}
	}
    /**
	 * Direction: 导出业务数据到服务器
	 * ename: exportBusData
	 * 引用方法: 
	 * viewers: 导出业务数据到服务器
	 * messages: 
	 */
    public String exportBusData(Object o){
    	String exportdate = searchdate==null?TimeFacade.getCurrentStringTime():StringUtil.replace(searchdate.toString(), "-", "");
    	if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否确认导出"+exportdate+"业务数据到服务器目录?")) {
    		try
    		{
    			String result = checkStatusOfReportDownloadService.exportBusData(exportdate, busType);
    			MessageDialog.openMessageDialog(null,result);
    		} catch (ITFEBizException e) {
    			log.error(e);
    			MessageDialog.openMessageDialog(null,"导出业务数据到服务器目录失败！");
    		}
    	}
       
        return super.exportBusData(o);
    }
    /**
	 * Direction: 导出tips下发报表
	 * ename: exporttipsreport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exporttipsreport(Object o){
		String exportdate = searchdate==null?TimeFacade.getCurrentStringTimebefor():StringUtil.replace(searchdate.toString(), "-", "");
    	if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否确认下载日期"+exportdate+"的tips下发的报表数据?")) {
    		// 客户端下载报文的路径
    		String dirsep = "/"; // 取得系统分割符
    		// 选择保存路径
    		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
    				.getActiveShell());
    		String filePath = path.open();
    		if ((null == filePath) || (filePath.length() == 0)) {
    			MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
    			return "";
    		}
    		filePath = filePath+dirsep+"downloadTipsReport"+dirsep;
    		File dir = new File(filePath);
    		if (!dir.exists()) {
    			dir.mkdirs();
    		}
    		TvRecvlogDto findto = new TvRecvlogDto();
    		findto.setSdate(exportdate);
    		if(loginfo.getPublicparam().indexOf(",tipsDownLoadReport=clientall,")<0)
    			findto.setSrecvorgcode(loginfo.getSorgcode());
    		try {
				List tempList = commonDataAccessService.findRsByDtoWithWhere(findto," and (S_OPERATIONTYPECODE='"+MsgConstant.MSG_NO_3128+"' or S_OPERATIONTYPECODE='"+MsgConstant.MSG_NO_3129+"' or S_OPERATIONTYPECODE='"+MsgConstant.MSG_NO_3139+"')");
				if(tempList!=null&&tempList.size()>0)
				{
					List<TvRecvlogDto> dataList = new ArrayList<TvRecvlogDto>();
					TvRecvlogDto tempdto = null;
					Set<String> tmpSet = new HashSet<String>(); 
					for(int i=0;i<tempList.size();i++)
					{
						tempdto = (TvRecvlogDto)tempList.get(i);
						if(MsgConstant.MSG_NO_3128.equals(tempdto.getSoperationtypecode())&&StringUtils.isNotBlank(tempdto.getStrecode()))
						{
							if(tmpSet.add(tempdto.getSoperationtypecode()+tempdto.getStrecode()))
								dataList.add(tempdto);
						}else if(MsgConstant.MSG_NO_3129.equals(tempdto.getSoperationtypecode())||MsgConstant.MSG_NO_3139.equals(tempdto.getSoperationtypecode()))
						{
							if(tmpSet.add(tempdto.getSoperationtypecode()+tempdto.getSseq()))
								dataList.add(tempdto);
						}else
						{
							dataList.add(tempdto);
						}
					}
					if(dataList!=null&&dataList.size()>0)
					{
						int lastIndex = 0;
						String clientfile = null;
						for(TvRecvlogDto downdto:dataList)
						{
							lastIndex = downdto.getStitle().replace("\\", "/").lastIndexOf(dirsep);
							clientfile = filePath+downdto.getStitle().substring(lastIndex + 1);
							ClientFileTransferUtil.downloadFile(downdto.getStitle().replace(queryLogsService.getFileRootPath(),""), clientfile);
						}
					}else
					{
						MessageDialog.openMessageDialog(null, "查询不到数据！");
						return "";
					}	
					
				}else
				{
					MessageDialog.openMessageDialog(null, "查询不到数据！");
					return "";
				}
			} catch (ITFEBizException e) {
				MessageDialog.openMessageDialog(null, "下载失败："+e.toString());
			} catch (FileTransferException e) {
				MessageDialog.openMessageDialog(null, "下载失败："+e.toString());
			}
    	}
        return super.exporttipsreport(o);
    }
    /**
	 * Direction: 下载业务数据
	 * ename: downloadBus
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String downloadBus(Object o){
    	// 客户端下载报文的路径
		String dirsep = File.separator; // 取得系统分割符
		dirsep = "/";
		String clientpath = "c:" + dirsep + "client" + dirsep + "01" + dirsep+TimeFacade.getCurrentStringTime() + dirsep + loginfo.getSorgcode()
				+ dirsep;
		File dir = new File(clientpath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String exportdate = searchdate==null?TimeFacade.getCurrentStringTime():StringUtil.replace(searchdate.toString(), "-", "");
    	if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否确认下载"+exportdate+"的业务数据?")) {
    		try
    		{
    			String result = null;
    			Map getMap = checkStatusOfReportDownloadService.downloadbus(exportdate, busType);
    			result = String.valueOf(getMap.get("result"));
    			List<String> fileList = (List<String>)getMap.get("fileList");
    			if(fileList!=null&&fileList.size()>0)
    			{
    				File file = null;
    				for(int i=0;i<fileList.size();i++)
    				{
    					String clientfile = null;
    					if(fileList.get(i).indexOf(File.separator)>0)
    						clientfile=clientpath+fileList.get(i).substring(fileList.get(i).lastIndexOf(File.separator)+1);
    					else
    						clientfile=clientpath+fileList.get(i).substring(fileList.get(i).lastIndexOf("/")+1);
    					file = new File(clientfile);
    					if(!new File(file.getParent()).exists()){
            				new File(file.getParent()).mkdirs();
            			}
            			if(file.exists())
            				file.delete();
    					ClientFileTransferUtil.downloadFile(fileList.get(i),clientfile);
    				}
    			}
    			String line = System.getProperty("line.separator");
    			MessageDialog.openMessageDialog(null,"下载文件成功,保存在目录："+clientpath+line+result);
    		} catch (ITFEBizException e) {
    			log.error(e);
    			MessageDialog.openMessageDialog(null,"下载业务数据失败！");
    		} catch (FileTransferException e) {
    			log.error(e);
    			MessageDialog.openMessageDialog(null,"下载业务数据失败！");
			}
    	}
		
        return super.downloadBus(o);
    }
    /**
	 * Direction: 勾兑入库
	 * ename: blend
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String blend(Object o){
    	try {
    		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
    				.getCurrentComposite().getShell(), "提示", "是否确认勾兑全省国库数据！")) {
			checkStatusOfReportDownloadService.searchStatusOfReportDownLoad(searchdate, "blend", null);
			MessageDialog.openMessageDialog(null,"已成功调用勾兑接口,请过段时间查看勾兑结果！");
    		}
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
        return super.blend(o);
    }
    /**
	 * Direction: 导出参数
	 * ename: exportParam
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportParam(Object o){
    	try {
    		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
    				.getCurrentComposite().getShell(), "提示", "是否确定参数传送到数据交换平台吗?")) {
			checkStatusOfReportDownloadService.searchStatusOfReportDownLoad(searchdate, "exportparam", null);
			MessageDialog.openMessageDialog(null,"已成功调用参数传送接口,请过段时间查看传送结果！");
    		}
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
        return super.exportParam(o);
    }
    /**
	 * Direction: 报表传送
	 * ename: exportReport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportReport(Object o){
    	try {
    		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
    				.getCurrentComposite().getShell(), "提示", "是否确定报表传送到数据交换平台吗?")) {
			checkStatusOfReportDownloadService.searchStatusOfReportDownLoad(searchdate, "exportreport", null);
			MessageDialog.openMessageDialog(null,"已成功调用报表传送接口,请过段时间查看传送结果！");
    		}
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
        return super.exportReport(o);
    }
	public String getReportpath() {
		return reportpath;
	}

	public void setReportpath(String reportpath) {
		this.reportpath = reportpath;
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

	public List getTreCodeList() {
		return treCodeList;
	}

	public void setTreCodeList(List treCodeList) {
		this.treCodeList = treCodeList;
	}

	public String getSearchArea() {
		return searchArea;
	}
	 public void setStrecode(java.lang.String _strecode) {
	        strecode = _strecode;
	        if(strecode!=null&&!"".equals(strecode.trim()))
	        	searchArea = "";
	    }
	public void setSearchArea(String searchArea) {
		this.searchArea = searchArea;
		if(searchArea!=null&&!"".equals(searchArea.trim()))
    		strecode = "";
		editor.fireModelChanged();
	}
	public List getPagingList() {
		return pagingList;
	}
	public void setPagingList(List pagingList) {
		this.pagingList = pagingList;
	}
	public int getGkcount() {
		return gkcount;
	}
	public void setGkcount(int gkcount) {
		this.gkcount = gkcount;
	}
	public int getSrcount() {
		return srcount;
	}
	public void setSrcount(int srcount) {
		this.srcount = srcount;
	}
	public int getLscount() {
		return lscount;
	}
	public void setLscount(int lscount) {
		this.lscount = lscount;
	}
	public int getSpcount() {
		return spcount;
	}
	public void setSpcount(int spcount) {
		this.spcount = spcount;
	}
	public int getKccount() {
		return kccount;
	}
	public void setKccount(int kccount) {
		this.kccount = kccount;
	}
	public int getZccount() {
		return zccount;
	}
	public void setZccount(int zccount) {
		this.zccount = zccount;
	}
	public List getVoucherTypeList() {
		if(voucherTypeList==null)
		{
			voucherTypeList = new ArrayList();
			Mapper map = new Mapper(MsgConstant.MSG_NO_5101, "实拨资金业务");
			Mapper map1 = new Mapper(MsgConstant.VOUCHER_NO_3208, "实拨资金退款");
			Mapper map2 = new Mapper(MsgConstant.MSG_NO_5102, "直接支付额度");
			Mapper map3 = new Mapper(MsgConstant.MSG_NO_5103, "授权支付额度");
			Mapper map4 = new Mapper(MsgConstant.VOUCHER_NO_2301, "申请划款凭证");
			Mapper map5 = new Mapper(MsgConstant.VOUCHER_NO_2302, "申请退款凭证");
			voucherTypeList.add(map);
			voucherTypeList.add(map1);
			voucherTypeList.add(map2);
			voucherTypeList.add(map3);
			voucherTypeList.add(map4);
			voucherTypeList.add(map5);
		}
		return voucherTypeList;
	}
	public void setVoucherTypeList(List voucherTypeList) {
		this.voucherTypeList = voucherTypeList;
	}
	public String getBusType() {
		return busType;
	}
	public void setBusType(String busType) {
		this.busType = busType;
	}
	public int getGdcount() {
		return gdcount;
	}
	public void setGdcount(int gdcount) {
		this.gdcount = gdcount;
	}
	public int getHgcount() {
		return hgcount;
	}
	public void setHgcount(int hgcount) {
		this.hgcount = hgcount;
	}
	public int getTkcount() {
		return tkcount;
	}
	public void setTkcount(int tkcount) {
		this.tkcount = tkcount;
	}

}
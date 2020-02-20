package com.cfcc.itfe.client.para.conpaychecksearchui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TnConpaycheckbillDto;
import com.cfcc.itfe.persistence.dto.TnConpaycheckpaybankDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.para.tsuserstampfunction.ITsUserstampfunctionService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * codecomment:
 * 
 * @author db2admin
 * @time 13-03-05 20:05:36 子系统: Para 模块:ConPayCheckSearchUI
 *       组件:ConPayCheckBillSearchUI
 */
@SuppressWarnings("unchecked")
public class ConPayCheckBillSearchUIBean extends
		AbstractConPayCheckBillSearchUIBean implements IPageDataProvider {

	private static Log log = LogFactory
			.getLog(ConPayCheckBillSearchUIBean.class);
	protected ITsUserstampfunctionService tsUserstampfunctionService = (ITsUserstampfunctionService)getService(ITsUserstampfunctionService.class);
	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();
	private PagingContext pagingcontext = new PagingContext(this);
	private PagingContext qscontext = new PagingContext(this);
	private TnConpaycheckbillDto querydto = null;
	private TnConpaycheckbillDto editdto = null;
	private TnConpaycheckbillDto savedto = null;
	private TnConpaycheckpaybankDto searchdto = null;
	private boolean searchtype = false;
	private List checktypelist = null;
	private String strecode;
	private String bankcode;
	private List bankcodelist;
	private String funcinfo;
	private List monthlist;
	public ConPayCheckBillSearchUIBean() {
		super();
		querydto= new TnConpaycheckbillDto();
		editdto = new TnConpaycheckbillDto();
		searchdto = new TnConpaycheckpaybankDto();
		searchdto.setSbgtlevel("0");
		searchdto.setSbgttypecode("1");
		searchdto.setSpaytypecode("0");
		filePath = new ArrayList();
		bankcodelist = new ArrayList();
		checktypelist = new ArrayList();
		querydto.setDstartdate(TimeFacade.getCurrentDateTime());
		querydto.setDenddate(TimeFacade.getCurrentDateTime());
		setFuncinfo("对账单支持导入*.csv格式的文件，导入csv格式时必须按照TC导出时选择的条件导入。");
	}

	/**
	 * Direction: 查询 ename: queryBudget 引用方法: viewers: 查询结果 messages:
	 */
	public String queryBudget(Object o) {
		searchtype = false;
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		if (null==pageResponse ||pageResponse.getTotalCount()==0 ) {
			MessageDialog.openMessageDialog(null, "查询无记录！");
			return null;
		}
		pagingcontext.setPage(pageResponse);
		return super.queryBudget(o);
	}
	/**
	 * Direction: 查询 ename: queryBudget 引用方法: viewers: 查询结果 messages:
	 */
	public String queryQs(Object o) {
		searchtype = true;
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		if (null==pageResponse ||pageResponse.getTotalCount()==0 ) {
			MessageDialog.openMessageDialog(null, "查询无记录！");
			return null;
		}
		qscontext.setPage(pageResponse);
		this.editor.fireModelChanged();
		return super.queryQs(o);
	}
	/**
	 * Direction: 额度对账单删除
	 * ename: eddelete
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String eddelete(Object o){
    	StringBuffer ts = new StringBuffer("确定删除:");
        if(querydto.getStrecode()!=null&&!"".equals(querydto.getStrecode()))
        {
        	ts.append("国库代码["+querydto.getStrecode()+"]");
        }
        if(querydto.getSbnkno()!=null&&!"".equals(querydto.getSbnkno()))
        {
        	ts.append("代理银行["+querydto.getSbnkno()+"]");
        }
        if(querydto.getDstartdate()!=null&&!"".equals(querydto.getDstartdate()))
        {
        	ts.append("开始日期["+querydto.getDstartdate()+"]");
        }
        if(querydto.getDenddate()!=null&&!"".equals(querydto.getDenddate()))
        {
        	ts.append("结束日期["+querydto.getDenddate()+"]");
        }
        ts.append("记录吗?");
        if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(null,
				"信息提示", ts.toString())) {
			return "";
		}
        try{
        	tsUserstampfunctionService.delInfo(querydto);
        	MessageDialog.openMessageDialog(null, "删除成功!");
        }catch(Exception e)
        {
        	MessageDialog.openMessageDialog(null, "删除失败:"+e.getMessage());
        }        
        return super.eddelete(o);
    }
    
	/**
	 * Direction: 清算对账单删除
	 * ename: qsdelete
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String qsdelete(Object o){
    	StringBuffer ts = new StringBuffer("确定删除:");
        if(searchdto.getStrecode()!=null&&!"".equals(searchdto.getStrecode()))
        {
        	ts.append("国库代码["+searchdto.getStrecode()+"]");
        }
        if(searchdto.getSbankcode()!=null&&!"".equals(searchdto.getSbankcode()))
        {
        	ts.append("代理银行["+searchdto.getSbankcode()+"]");
        }
        if(searchdto.getSext2()!=null&&!"".equals(searchdto.getSext2()))
        {
        	ts.append("月份["+searchdto.getSext2()+"]");
        }
        ts.append("记录吗?");
        if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(null,
				"信息提示", ts.toString())) {
			return "";
		}
        try{
        	tsUserstampfunctionService.delInfo(searchdto);
        	MessageDialog.openMessageDialog(null, "删除成功!");
        }catch(Exception e)
        {
        	MessageDialog.openMessageDialog(null, "删除失败:"+e.getMessage());
        }        
        return super.qsdelete(o);
    }
	public String dataexport(Object o){
		List list = new ArrayList();
		String fsp = System.getProperty("file.separator");
		DirectoryDialog directory = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String clientPath = directory.open();
		String fileName =  "TN_CONPAYCHECKBILL.txt";
		if(clientPath==null){
			return null;
		}
		BufferedWriter w = null;
		try {
			list = commonDataAccessService.findRsByDtoWithBookOrgUR(querydto);
			if(list.size() > 0){
				w = new BufferedWriter(new FileWriter(clientPath + fsp + fileName));
				StringBuffer sb = new StringBuffer("I_ENROLSRLNO, D_ENDDATE, D_STARTDATE, S_BOOKORGCODE,S_TRECODE, S_BDGORGCODE, S_BDGORGNAME, S_BNKNO, S_FUNCSBTCODE,S_ECOSBTCODE, C_AMTKIND, F_LASTMONTHZEROAMT, F_CURZEROAMT,F_CURRECKZEROAMT, F_LASTMONTHSMALLAMT, F_CURSMALLAMT,F_CURRECKSMALLAMT, TS_SYSUPDATE" + "\r\n");
				for(int i = 0 ; i < list.size() ; i ++){
					TnConpaycheckbillDto tnConpaycheckbillDto = (TnConpaycheckbillDto)list.get(i);
					sb.append(tnConpaycheckbillDto.getIenrolsrlno()).append(",");
					sb.append(tnConpaycheckbillDto.getDenddate()).append(",");
					sb.append(tnConpaycheckbillDto.getDstartdate()).append(",");
					sb.append(tnConpaycheckbillDto.getSbookorgcode()).append(",");
					sb.append(tnConpaycheckbillDto.getStrecode()).append(",");
					sb.append(tnConpaycheckbillDto.getSbdgorgcode()).append(",");
					sb.append(tnConpaycheckbillDto.getSbdgorgname()).append(",");
					sb.append(tnConpaycheckbillDto.getSbnkno()).append(",");
					sb.append(tnConpaycheckbillDto.getSfuncsbtcode()).append(",");
					sb.append(tnConpaycheckbillDto.getSecosbtcode()).append(",");
					sb.append(tnConpaycheckbillDto.getCamtkind()).append(",");
					sb.append(tnConpaycheckbillDto.getFlastmonthzeroamt()).append(",");
					sb.append(tnConpaycheckbillDto.getFcurzeroamt()).append(",");
					sb.append(tnConpaycheckbillDto.getFcurreckzeroamt()).append(",");
					sb.append(tnConpaycheckbillDto.getFlastmonthsmallamt()).append(",");
					sb.append(tnConpaycheckbillDto.getFcursmallamt()).append(",");
					sb.append(tnConpaycheckbillDto.getFcurrecksmallamt()).append(",");
					sb.append(tnConpaycheckbillDto.getTssysupdate()).append("\r\n");	
				}
				w.write(sb.toString());
				w.flush();
				MessageDialog.openMessageDialog(null, "导出数据成功!");
			}else{
				MessageDialog.openMessageDialog(null, "无数据!");
			}
			
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(null != w){
				try {
					w.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return super.dataexport(o);
	}
	
	/**
	 * 导入数据
	 */
	public String dataimport(Object o){
		if(strecode==null||"".equals(strecode))
		{
			MessageDialog.openMessageDialog(null, "国库主体代码不能为空！");
			return null;
		}
		searchdto.setStrecode(strecode);
		searchdto.setSbankcode(bankcode);
		if(searchdto.getSbankcode()==null||"".equals(searchdto.getSbankcode()))
		{
			MessageDialog.openMessageDialog(null, "银行代码不能为空！");
			return null;
		}
		if(searchdto.getSext1()==null||"".equals(searchdto.getSext1()))
		{
			MessageDialog.openMessageDialog(null, "对账单类型不能为空！");
			return null;
		}
		if("2".equals(searchdto.getSext1()))
		{
			if(searchdto.getSpaytypecode()==null||"".equals(searchdto.getSpaytypecode()))
			{
				MessageDialog.openMessageDialog(null, "支付方式不能为空！");
				return null;
			}else if(searchdto.getSbgtlevel()==null||"".equals(searchdto.getSbgtlevel()))
			{
				MessageDialog.openMessageDialog(null, "辖属标志不能为空！");
				return null;
			}else if(searchdto.getSbgttypecode()==null||"".equals(searchdto.getSbgttypecode()))
			{
				MessageDialog.openMessageDialog(null, "预算种类不能为空！");
				return null;
			}else if(searchdto.getSext2()==null||"".equals(searchdto.getSext2()))
			{
				MessageDialog.openMessageDialog(null, "对账单月份不能为空！");
				return null;
			}
		}
		// 接口类型
		List<File> fileList = new ArrayList<File>();
		List<String> serverpathlist = new ArrayList<String>();
		StringBuffer problemStr = new StringBuffer();
		StringBuffer errorStr = new StringBuffer();
		String resultType = "JZZF";
		int sumFile = 0; // 统计所有导入的txt文件
		int wrongFileSum = 0; // 统计错误文件的个数，如果超出20个，不再追加错误信息
		// 判断是否选中文件
		if (null == filePath || filePath.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要加载的文件！");
			return null;
		}

	
		try {
			// 判断所选文件是否大于1000个
			if (filePath.size() > 1000) {
				MessageDialog.openMessageDialog(null, "所选加载文件不能大于1000个！");
				return null;
			}
			// 数据加载
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			for (int i = 0; i < filePath.size(); i++) {
				File tmpfile = (File) filePath.get(i);
				// 文件的名字
				String tmpfilename = tmpfile.getName();
				// 获取文件的路径
				String tmpfilepath = tmpfile.getAbsolutePath();
				if (!tmpfilename.trim().toLowerCase().endsWith(".txt")&&
						!tmpfilename.trim().toLowerCase().endsWith(".csv")) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, " 选择的文件中只能包含txt或者csv格式的文件！");
					return super.dataimport(o);
				}
				if(tmpfilename.trim().toLowerCase().endsWith(".csv") && StringUtils.isBlank(bankcode)){
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "导入csv格式的文件时必须按照银行导入，请选择代理银行！");
					return super.dataimport(o);
				}
				sumFile++; // 统计所有的csv文件
				if (null == tmpfile || null == tmpfilename
						|| null == tmpfilepath) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, " 请选择要加载的文件！");
					return null;
				}
				// 文件上传并记录日志
				String serverpath = ClientFileTransferUtil.uploadFile(tmpfile
						.getAbsolutePath());
				fileList.add(new File(serverpath));
				serverpathlist.add(serverpath);
			}
			// 服务器加载
			String errName = "";
			int errCount = 0;
			
			if (fileList != null && fileList.size() > 0) {
			
				MulitTableDto bizDto = fileResolveCommonService.loadFile(
						fileList, BizTypeConstant.BIZ_TYPE_JZZF,
						resultType, searchdto);
				errCount = bizDto.getErrorCount() + wrongFileSum;
				if (null != bizDto.getErrorList()
						&& bizDto.getErrorList().size() > 0) {
					for (int m = 0; m < bizDto.getErrorList().size(); m++) {
						wrongFileSum++;
						if (wrongFileSum < 15) {
							problemStr.append(bizDto.getErrorList().get(m)
									.substring(6)
									+ "\r\n");
							errorStr.append(bizDto.getErrorList().get(m)
									+ "\r\n");
						} else {
							errorStr.append(bizDto.getErrorList().get(m)
									+ "\r\n");
						}
					}
				}
				// 记接收日志
				commonDataAccessService.saveTvrecvlog(DeleteServerFileUtil
						.wipeFileOut(serverpathlist, bizDto.getErrNameList()),
						BizTypeConstant.BIZ_TYPE_JZZF);
				errName = StateConstant.Import_Errinfo_DIR
						+ "直接支付文件导入错误信息("
						+ new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒")
								.format(new java.util.Date()) + ").txt";
				if (!"".equals(errorStr.toString())) {
					FileUtil.getInstance().writeFile(errName,
							errorStr.toString());
				}
				FileUtil
						.getInstance()
						.deleteFileWithDays(
								StateConstant.Import_Errinfo_DIR,
								Integer
										.parseInt(StateConstant.Import_Errinfo_BackDays));
			}
			if (problemStr.toString().trim().length() > 0) {
				String noteInfo = "";
				if (fileList == null || fileList.size() == 0) {
					noteInfo = "共加载了" + sumFile + "个文件，其中有" + wrongFileSum
							+ "个错误文件，信息如下：\r\n";
				} else {
					noteInfo = "共加载了" + sumFile + "个文件，其中有" + errCount
							+ "个错误文件，部分信息如下【详细错误信息请查看" + errName + "】：\r\n";
				}
				MessageDialog.openMessageDialog(null, noteInfo
						+ problemStr.toString());
			} else {
				MessageDialog.openMessageDialog(null, "文件加载成功,本次共加载成功 "
						+ fileList.size() + " 个文件！");
			}
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
		} catch (Exception e) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			// 删除服务器上传失败文件
			try {
				DeleteServerFileUtil.delFile(commonDataAccessService,
						serverpathlist);
			} catch (ITFEBizException e1) {
				log.error("删除服务器文件失败！", e1);
				MessageDialog.openErrorDialog(null, e);
			}
			return null;
		}
		filePath = new ArrayList();
		this.editor.fireModelChanged();
		return super.dataimport(o);
	}

	/**
	 * Direction: 返回 ename: goBack 引用方法: viewers: 信息查询 messages:
	 */
	public String goBack(Object o) {
//		querydto = new TnConpaycheckbillDto();
		filePath = new ArrayList();
		return super.goBack(o);
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
			PageResponse response = null;
			if(searchtype)
			{
				response = commonDataAccessService.findRsByDtoWithWherePaging(searchdto,pageRequest, "1=1");
			}else
			{
				querydto.setSbookorgcode(loginfo.getSorgcode());
				String where = "D_STARTDATE>='"+querydto.getDstartdate()+"' and D_STARTDATE<='"+querydto.getDenddate()+"'";
				TnConpaycheckbillDto newquerydto = (TnConpaycheckbillDto)querydto.clone();
				newquerydto.setDstartdate(null);
				newquerydto.setDenddate(null);
				response = commonDataAccessService.findRsByDtoWithWherePaging(newquerydto,pageRequest, where);
			}
			
			return response;

		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}
	/**
	 * Direction: 保存数据
	 * ename: savedate
	 * 引用方法: 
	 * viewers: 查询结果
	 * messages: 
	 */
    public String savedate(Object o){
    	if (!AdminConfirmDialogFacade.open("B_247", "业务对账凭证", "授权用户"
				+ loginfo.getSuserName() + "修改集中支付额度数据", "主管授权")) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			return null;
		}
        savedto.setFcurrecksmallamt(editdto.getFcurrecksmallamt());
        savedto.setFcurreckzeroamt(editdto.getFcurreckzeroamt());
        savedto.setFcursmallamt(editdto.getFcursmallamt());
        savedto.setFcurzeroamt(editdto.getFcurzeroamt());
        savedto.setFlastmonthsmallamt(editdto.getFlastmonthsmallamt());
        savedto.setFlastmonthzeroamt(editdto.getFlastmonthzeroamt());
        try {
			commonDataAccessService.updateData(savedto);
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "保存数据失败!"+e.toString());
		}
        return super.savedate(o);
    }
    
	/**
	 * Direction: 返回
	 * ename: returnqueryresult
	 * 引用方法: 
	 * viewers: 查询结果
	 * messages: 
	 */
    public String returnqueryresult(Object o){
        
        return super.returnqueryresult(o);
    }
    
	/**
	 * Direction: 跳转查询结果
	 * ename: goqueryresult
	 * 引用方法: 
	 * viewers: 信息修改
	 * messages: 
	 */
    public String goqueryresult(Object o){
    	if(loginfo.getPublicparam().contains("paymentCalculation=true"))
    	{
	        editdto = (TnConpaycheckbillDto)o;
	        savedto = (TnConpaycheckbillDto)o;
	        return super.goqueryresult(o);
    	}else
    	{
    		return "";
    	}
    }
	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public PagingContext getPagingcontext() {
		return pagingcontext;
	}

	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
	}

	public TnConpaycheckbillDto getQuerydto() {
		return querydto;
	}

	public void setQuerydto(TnConpaycheckbillDto querydto) {
		this.querydto = querydto;
	}

	public String getStrecode() {
		return strecode;
	}

	public void setStrecode(String strecode) {
		this.strecode = strecode;
		bankcodelist = new ArrayList();
		bankcodelist.add(new Mapper("",""));
		TsConvertbanktypeDto dto = new TsConvertbanktypeDto();
		dto.setSorgcode(loginfo.getSorgcode());
		dto.setStrecode(strecode);
		try {
			List<TsConvertbanktypeDto> banklist =  commonDataAccessService.findRsByDto(dto);
			for (TsConvertbanktypeDto emuDto : banklist) {
				Mapper map = new Mapper(emuDto.getSbankcode(), emuDto.getSbankname());
				bankcodelist.add(map);
			}
			querydto.setStrecode(strecode);
			searchdto.setStrecode(strecode);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}

	public String getBankcode() {
		return bankcode;
	}

	public void setBankcode(String bankcode) {
		this.bankcode = bankcode;
	}

	public List getBankcodelist() {
		return bankcodelist;
	}

	public void setBankcodelist(List bankcodelist) {
		this.bankcodelist = bankcodelist;
	}

	public String getFuncinfo() {
		return funcinfo;
	}

	public void setFuncinfo(String funcinfo) {
		this.funcinfo = funcinfo;
	}

	public TnConpaycheckpaybankDto getSearchdto() {
		return searchdto;
	}

	public void setSearchdto(TnConpaycheckpaybankDto searchdto) {
		this.searchdto = searchdto;
	}

	public List getChecktypelist() {
		if(checktypelist==null)
			checktypelist = new ArrayList();
		else if(checktypelist.size()==0)
		{
			Mapper map = new Mapper("1", "集中支付额度对账单");
			Mapper map1 = new Mapper("2", "集中支付清算对账单");
			checktypelist.add(map);
			checktypelist.add(map1);
		}
		return checktypelist;
	}

	public void setChecktypelist(List checktypelist) {
		this.checktypelist = checktypelist;
	}

	public List getMonthlist() {
		if(monthlist==null)
		{
			monthlist = new ArrayList();
			Mapper map = new Mapper("01","1月");
			monthlist.add(map);
			map = new Mapper("02","2月");
			monthlist.add(map);
			map = new Mapper("03","3月");
			monthlist.add(map);
			map = new Mapper("04","4月");
			monthlist.add(map);
			map = new Mapper("05","5月");
			monthlist.add(map);
			map = new Mapper("06","6月");
			monthlist.add(map);
			map = new Mapper("07","7月");
			monthlist.add(map);
			map = new Mapper("08","8月");
			monthlist.add(map);
			map = new Mapper("09","9月");
			monthlist.add(map);
			map = new Mapper("10","10月");
			monthlist.add(map);
			map = new Mapper("11","11月");
			monthlist.add(map);
			map = new Mapper("12","12月");
			monthlist.add(map);
		}
		return monthlist;
	}

	public void setMonthlist(List monthlist) {
		this.monthlist = monthlist;
	}

	public PagingContext getQscontext() {
		return qscontext;
	}

	public void setQscontext(PagingContext qscontext) {
		this.qscontext = qscontext;
	}

	public TnConpaycheckbillDto getEditdto() {
		return editdto;
	}

	public void setEditdto(TnConpaycheckbillDto editdto) {
		this.editdto = editdto;
	}

}
package com.cfcc.itfe.client.dataquery.trtaxorgpayoutreport;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IFileResolveCommonService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;

/**
 * codecomment:
 * 
 * @author ZZD
 * @time 13-03-20 08:59:05 子系统: DataQuery 模块:trTaxorgPayoutReport
 *       组件:TrTaxorgPayoutReport
 */
public class TrTaxorgPayoutReportBean extends AbstractTrTaxorgPayoutReportBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(TrTaxorgPayoutReportBean.class);
	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();
	String conPayBillType;
	QueryPayOutReportBean querypayoutreportbean;
	QueryConPayOutReportBean queryconpayoutreportbean;
	private BigDecimal nmoneyday, nmoneymonth, nmoneyyear;

	public TrTaxorgPayoutReportBean() {
		super();
		nmoneyday = new BigDecimal("0.00");
		nmoneymonth = new BigDecimal("0.00");
		nmoneyyear = new BigDecimal("0.00");
		dto = new TrTaxorgPayoutReportDto();
		pagingcontext = new PagingContext(this);
		filePath = new ArrayList();
		querypayoutreportbean = new QueryPayOutReportBean(
				commonDataAccessService, dto);
		queryconpayoutreportbean = new QueryConPayOutReportBean(
				commonDataAccessService, dto);
		init();
	}

	/**
	 * Direction: 查询 ename: query 引用方法: viewers: 预算支出日报表查询结果 messages:
	 */
	public String query(Object o) {	
		if(!query(dto))
			return null;
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = querypayoutreportbean.retrieve(pageRequest);		
		return super.query(o);
	}

	/**
	 * Direction: 导出 ename: exportToFile 引用方法: viewers: * messages:
	 */
	public String exptofile(Object o) {
		exportReport(dto.getStrecode()+dto.getSrptdate(), true);
		return super.exptofile(o);
	}

	/**
	 * Direction: 集中支付导出 ename: conexpfile 引用方法: viewers: * messages:
	 */
	public String conexpfile(Object o) {
		exportReport(dto.getStrecode()+dto.getSrptdate(), true);
		return super.conexpfile(o);
	}
	
	/**
	 * Direction: 集中支付导出月报
	 * ename: contexpmonthfile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String contexpmonthfile(Object o){
    	if(!dto.getSrptdate().equals(getLastDayOfMonth(dto.getSrptdate()))){
    		MessageDialog.openMessageDialog(null, "当前日期不是该月最后一天，不能导出月报！");
			return null;
    	}   	
    	exportReport(dto.getStrecode()+dto.getSrptdate(), false);
        return super.contexpmonthfile(o);
    }
    
	/**
	 * Direction: 导出月报
	 * ename: exptomonthfile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exptomonthfile(Object o){
    	if(!dto.getSrptdate().equals(getLastDayOfMonth(dto.getSrptdate()))){
    		MessageDialog.openMessageDialog(null, "当前日期不是该月最后一天，不能导出月报！");
			return null;
    	}   	
    	exportReport(dto.getStrecode()+dto.getSrptdate(), false);
        return super.exptomonthfile(o);
    }
    
    /**
     * 获取当月最后一天
     * @param day
     * @return
     */
    public String getLastDayOfMonth(String day){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(TimeFacade.parseDate(day));		
		return day.substring(0, 6)+calendar.getActualMaximum(Calendar.DAY_OF_MONTH);	
    }
    
    /**
     * 导出报表
     * @param fileName
     * @param flag true 日报 false 月报
     */
    private void exportReport(String fileName,Boolean flag){    	
		String path = new DirectoryDialog(Display.getCurrent().getActiveShell()).open();		
		if (StringUtils.isBlank(path)) {
			MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
			return ;
		}
		try {
			List<TrTaxorgPayoutReportDto> result = commonDataAccessService
					.findRsByDto(querypayoutreportbean.getDto()," ",querypayoutreportbean.getDto().tableName());
			if (null == result || result.size() == 0) {
				MessageDialog.openMessageDialog(null, "没有需要导出的数据！");
				return ;
			}
			expdata(result, path+File.separator+fileName+".txt",flag);
			MessageDialog.openMessageDialog(null, "操作成功！");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);			
		}
    }
    
    /**
     * 导出数据
     * @param result
     * @param fileName
     * @param flag true 日报 false 月报
     * @throws FileOperateException
     */
	private void expdata(List<TrTaxorgPayoutReportDto> result, String fileName,boolean flag)
			throws FileOperateException {
		StringBuffer resultStr = new StringBuffer();
		resultStr.append("国库代码,报表日期,预算科目,");
		if(flag)
			resultStr.append("日累计,");
		resultStr.append("月累计,年累计\n");
		for (TrTaxorgPayoutReportDto tmp : result) {
			resultStr.append(tmp.getStrecode() + ","); // 国库代码
			resultStr.append(tmp.getSrptdate() + ","); // 报表日期
			resultStr.append(tmp.getSbudgetsubcode() + ","); // 预算科目代码
			if(flag)
				resultStr.append(tmp.getNmoneyday().toString() + ","); // 日累计
			resultStr.append(tmp.getNmoneymonth().toString() + ","); // 月累计
			resultStr.append(tmp.getNmoneyyear() + "\n"); // 年累计
		}FileUtil.getInstance().writeFile(fileName, resultStr.toString());
	}

	/**
	 * Direction: 集中支付查询 ename: conPayoutQuery 引用方法: viewers: 集中支付日报表查询结果
	 * messages:
	 */
	public String conPayoutQuery(Object o) {
		dto.setStaxorgcode(conPayBillType);		
		if(!query(dto))
			return null;
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = queryconpayoutreportbean.retrieve(pageRequest);		
		return super.conPayoutQuery(o);
	}
	
	/**
	 * 查询
	 * @param dto
	 * @return
	 */
	private boolean query(TrTaxorgPayoutReportDto dto){
		if (StringUtils.isBlank(dto.getSrptdate())) {
			MessageDialog.openMessageDialog(null, "请输入报表日期！");
			return false;
		}
		if (StringUtils.isBlank(dto.getStrecode())) {
			MessageDialog.openMessageDialog(null, "请选择国库代码！");
			return false;
		}				
		if (StringUtils.isBlank(dto.getStaxorgcode())) {
			MessageDialog.openMessageDialog(null, "请选择报表种类！");
			return false;
		}
		try {
			List<TrTaxorgPayoutReportDto> list = commonDataAccessService
				.findRsByDto(dto," ",dto.tableName());
			if(list==null||list.size()==0){
				MessageDialog.openMessageDialog(null, "查询无数据！");
				return false;	
			}
			if(StringUtils.isBlank(dto.getSfinorgcode())){
				nmoneyday = new BigDecimal("0.00");
				nmoneymonth = new BigDecimal("0.00");
				nmoneyyear = new BigDecimal("0.00");
				for (TrTaxorgPayoutReportDto tmpDto : list) {
					if(tmpDto.getSbudgetsubcode().length()==3){//类款项只加类的
						nmoneyday = nmoneyday.add(tmpDto.getNmoneyday());
						nmoneymonth = nmoneymonth.add(tmpDto.getNmoneymonth());
						nmoneyyear = nmoneyyear.add(tmpDto.getNmoneyyear());
					}		
				}
			}
			return true;							
		} catch (ITFEBizException e) {
			log.error("查询数据失败！");
			MessageDialog.openMessageDialog(null, "查询数据失败！");			
		}return false;
	}

	/**
	 * Direction: 查询 ename: conPayquery 引用方法: viewers: 集中支付日报表查询结果 messages:
	 */
	public String goConpayQuery(Object o) {

		return super.goConpayQuery(o);

	}

	/**
	 * Direction: 返回查询页面 ename: goQuery 引用方法: viewers: 预算支出日报表查询 messages:
	 */
	public String goQuery(Object o) {
		// init();
		filePath = new ArrayList();
		return super.goQuery(o);
	}

	/**
	 * Direction: 导入数据 ename: upLoad 引用方法: viewers: * messages:
	 */
	public String upLoad(Object o) {

		// 接口类型
		List<File> fileList = new ArrayList<File>();
		List<String> serverpathlist = new ArrayList<String>();
		StringBuffer problemStr = new StringBuffer();
		StringBuffer errorStr = new StringBuffer();
		String resultType = "3127";
		int sumFile = 0; // 统计所有导入的csv文件
		int wrongFileSum = 0; // 统计错误文件的个数，如果超出20个，不再追加错误信息
		// 判断是否选中文件
		if (null == filePath || filePath.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要加载的文件！");
			return null;
		}

		if (this.check(inputDto, null)) {
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
				if (!tmpfilename.trim().toLowerCase().endsWith(".csv")&&!tmpfilename.trim().toLowerCase().endsWith(".xml")) {
					MessageDialog.openMessageDialog(null, " 请选择正确的文件格式,只支持csv和xml格式文件！");
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					return null;
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
						fileList, BizTypeConstant.BIZ_TYPE_TAXORG_PAYOUT,
						resultType, inputDto);
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
						BizTypeConstant.BIZ_TYPE_TAXORG_PAYOUT);
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
		// inputDto = new TrTaxorgPayoutReportDto();
		this.editor.fireModelChanged();
		return super.upLoad(o);
	}

	/**
	 * Direction: 进入导入数据页面 ename: goUpload 引用方法: viewers: 导入数据 messages:
	 */
	public String goUpload(Object o) {
		return super.goUpload(o);
	}

	public boolean check(TrTaxorgPayoutReportDto checkDto, String billType) {
		
		if (null == checkDto.getSrptdate() || "".equals(checkDto.getSrptdate())) {
			MessageDialog.openMessageDialog(null, "请填写报表日期！");
			return true;
		}
		if ((billType==null||"".equals(billType))&&(null == checkDto.getStrecode() || "".equals(checkDto.getStrecode()))) {
			MessageDialog.openMessageDialog(null, "请选择国库代码！");
			return true;
		}
		if ((billType==null||"".equals(billType))&&(null == checkDto.getSbudgettype()|| "".equals(checkDto.getSbudgettype()))) {
			MessageDialog.openMessageDialog(null, "请选择预算种类！");
			return true;
		}
		return false;
	}

	private void init() {
		inputDto = new TrTaxorgPayoutReportDto();
		inputDto.setSrptdate(TimeFacade.getCurrentStringTime());
		inputDto.setSbudgettype(StateConstant.BudgetType_IN);
		TsTreasuryDto _dto = new TsTreasuryDto();
		_dto.setSorgcode(loginfo.getSorgcode());
		TsTreasuryDto tmpdto = null;
		try {
			tmpdto = (TsTreasuryDto) commonDataAccessService.findRsByDto(_dto)
					.get(0);
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}
		inputDto.setStrecode(tmpdto.getStrecode());
		inputDto.setSbudgetlevelcode(tmpdto.getStrelevel());
		dto.setSrptdate(TimeFacade.getCurrentStringTime());
		dto.setSbudgettype(StateConstant.BudgetType_IN);
		dto.setStaxorgcode(StateConstant.REPORT_PAYOUT_TYPE_1);
		dto.setStrecode(tmpdto.getStrecode());
		conPayBillType = StateConstant.REPORT_PAYOUT_TYPE_4;
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

			return commonDataAccessService.findRsByDtoWithWherePaging(dto,
					pageRequest, " 1=1 ", "", dto.tableName());
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	public String getConPayBillType() {
		return conPayBillType;
	}

	public void setConPayBillType(String conPayBillType) {
		this.conPayBillType = conPayBillType;
	}

	public QueryPayOutReportBean getQuerypayoutreportbean() {
		return querypayoutreportbean;
	}

	public void setQuerypayoutreportbean(
			QueryPayOutReportBean querypayoutreportbean) {
		this.querypayoutreportbean = querypayoutreportbean;
	}

	public QueryConPayOutReportBean getQueryconpayoutreportbean() {
		return queryconpayoutreportbean;
	}

	public void setQueryconpayoutreportbean(
			QueryConPayOutReportBean queryconpayoutreportbean) {
		this.queryconpayoutreportbean = queryconpayoutreportbean;
	}

	public BigDecimal getNmoneyday() {
		return nmoneyday;
	}

	public void setNmoneyday(BigDecimal nmoneyday) {
		this.nmoneyday = nmoneyday;
	}

	public BigDecimal getNmoneymonth() {
		return nmoneymonth;
	}

	public void setNmoneymonth(BigDecimal nmoneymonth) {
		this.nmoneymonth = nmoneymonth;
	}

	public BigDecimal getNmoneyyear() {
		return nmoneyyear;
	}

	public void setNmoneyyear(BigDecimal nmoneyyear) {
		this.nmoneyyear = nmoneyyear;
	}

}
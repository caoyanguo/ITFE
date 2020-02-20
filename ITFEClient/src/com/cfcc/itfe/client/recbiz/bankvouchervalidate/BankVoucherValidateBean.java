package com.cfcc.itfe.client.recbiz.bankvouchervalidate;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.BankValidateDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.sendbiz.exporttbsforbj.IExportTBSForBJService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * 对导入的商行凭证信息进行校验<8202|5201 -> 2301>
 * 
 * @author hua
 * @time 15-04-03 10:29:55
 */
public class BankVoucherValidateBean extends AbstractBankVoucherValidateBean implements IPageDataProvider {
	private static Log log = LogFactory.getLog(BankVoucherValidateBean.class);
	private IExportTBSForBJService exportTBSForBJService = (IExportTBSForBJService) getService(IExportTBSForBJService.class);
	private String startDate; // 开始比对日期
	private String endDate; // 结束比对日期
	private List<File> fileList = new ArrayList<File>(); // 导入文件列表
	private List<Mapper> compareEnumList = new ArrayList<Mapper>(); // 比对状态枚举
	private String compareStatus; // 比对状态
	private ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();

	public BankVoucherValidateBean() {
		super();
		pagingContext = new PagingContext(this);
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		startDate = TimeFacade.getCurrentStringTime();
		endDate = TimeFacade.getCurrentStringTime();
		compareEnumList.add(new Mapper(StateConstant.MERGE_VALIDATE_SUCCESS, "比对成功"));
		compareEnumList.add(new Mapper(StateConstant.MERGE_VALIDATE_FAILURE, "比对失败"));
		compareEnumList.add(new Mapper(StateConstant.MERGE_VALIDATE_NOTCOMPARE, "未必对"));
	}

	/**
	 * Direction: 导入文件
	 */
	public String voucherImport(Object o) {
		long startTime = System.currentTimeMillis();
		System.out.println("Start to update file : "+ startTime);
		if (fileList.size() == 0) {
			MessageDialog.openMessageDialog(null, "请先选择文件!");
			return super.voucherImport(o);
		}
		try {
			/** 校验文件类型 */
			String validateInfo = validate(fileList);
			if (null != validateInfo && !"".equals(validateInfo)) {
				MessageDialog.openMessageDialog(null, validateInfo);
				return super.voucherImport(o);
			}

			/** 将符合条件的文件上传到服务器 */
			List<String> serverPathList = new ArrayList<String>();
			
//			ListUtils.sub
//			ExecutorService es = Executors.newFixedThreadPool(4);
//			es.execute(new Runnable() {
//				public void run() {
//					
//				}
//			});
			for (int i = 0; i < fileList.size(); i++) {
				File file = fileList.get(i);
				String absFilePath = file.getAbsolutePath();
				String serverFilePath = commonDataAccessService.getServerRootPath(absFilePath, loginfo.getSorgcode());
				exportTBSForBJService.deleteServerFile(serverFilePath);
				serverPathList.add(ClientFileTransferUtil.uploadFile(absFilePath));
			}
			long endUploadFileTime = System.currentTimeMillis();
			System.out.println("update file cost time : "+(endUploadFileTime-startTime));
			/** 开始进行导入 */
			MulitTableDto resultDto = bankVoucherValidateService.voucherImport(serverPathList, null);
			System.out.println("import server cost time : "+(System.currentTimeMillis()-endUploadFileTime));

			MessageDialog.openMessageDialog(null, generateMessage(resultDto, serverPathList));
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			refresh();
		} catch (Exception e) {
			log.error(e);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			return super.voucherImport(o);
		}
		return super.voucherImport(o);
	}

	/**
	 * Direction: 开始比对 ename: compare 引用方法: viewers: * messages:
	 */
	public String compare(Object o) {
		/** 校验日期 */
		String validateInfo = validate4Compare();
		if (StringUtils.isNotBlank(validateInfo)) {
			MessageDialog.openMessageDialog(null, validateInfo);
			return "";
		}
		try {
			/** 开始进行比对 */
			bankVoucherValidateService.voucherCompare(startDate, endDate, null);
			MessageDialog.openMessageDialog(null, "数据比对完成!");
			queryResult(o);
			editor.fireModelChanged();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return "";
	}

	/**
	 * Direction: 查询数据 ename: queryResult 引用方法: viewers: 数据比对界面 messages:
	 */
	public String queryResult(Object o) {
		/** 校验日期 */
		String validateInfo = validate4Compare();
		if (StringUtils.isNotBlank(validateInfo)) {
			MessageDialog.openMessageDialog(null, validateInfo);
			return "";
		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		if (pageResponse == null || pageResponse.getData().size() <= 0) {
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的数据");
		}
		pagingContext.setPage(pageResponse);
		editor.fireModelChanged();
		return super.queryResult(o);
	}

	/**
	 * 校验日期
	 */
	private String validate4Compare() {
		try {
			Date dateStart = new SimpleDateFormat("yyyyMMdd").parse(startDate);
			Date dateEnd = new SimpleDateFormat("yyyyMMdd").parse(endDate);
			boolean before = DateUtil.before(new java.sql.Date(dateStart.getTime()), new java.sql.Date(dateEnd.getTime()));
			int diffDate = DateUtil.difference(new java.sql.Date(dateStart.getTime()), new java.sql.Date(dateEnd.getTime()));
			if (!before && dateStart.compareTo(dateEnd) != 0) {
				return "开始日期不能小于结束日期!";
			}
			if (Math.abs(diffDate) > 7) {
				return "开始日期和结束日期间隔不能大于7天!";
			}
		} catch (ParseException e) {
			return "日期格式非法!(正确格式:yyyyMMdd)";
		}
		return "";
	}

	/** 清空选择的文件,并刷新界面 **/
	private void refresh() {
		fileList = new ArrayList<File>();
		editor.fireModelChanged();
	}

	/**
	 * 校验文件列表
	 * 
	 * @param fileList
	 * @return
	 */
	private String validate(List<File> fileList) {
		StringBuilder sb = new StringBuilder("");
		/* 文件必须为xml文件 */
		for (File file : fileList) {
			if (!file.getName().toUpperCase().endsWith(".XML")) {
				sb.append(file.getName() + " ： 文件类型错误!\n");
			}
		}
		return sb.toString();
	}

	public PageResponse retrieve(PageRequest request) {
		try {
			BankValidateDto paramDto = null;
			if (compareStatus != null && !"".equals(compareStatus)) {
				paramDto = new BankValidateDto();
				paramDto.setResult(compareStatus);
			}
			return bankVoucherValidateService.findValidateResult(startDate, endDate, paramDto, request);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(request);
	}

	/**
	 * 根据结果对象生成提示信息(商行凭证校验信息)
	 * 
	 * @param resultDto
	 * @param serverFileList
	 *            TODO 考虑把这个日志处理提取成一个公用方法
	 */
	public String generateMessage(MulitTableDto resultDto, List<String> serverFileList) {
		List<String> errorInfoList = resultDto.getErrorList(); // 错误信息列表
		List<String> errorFileList = resultDto.getErrNameList(); // 错误文件列表
		if (errorFileList == null || errorFileList.size() == 0) {
			/** 如果错误文件名列表为空,则说明全部导入成功 **/
			return "文件加载成功,本次共加载成功" + serverFileList.size() + "个文件!";

		} else {
			/** 处理错误日志信息 **/
			String errorLogName = StateConstant.Import_Errinfo_DIR + "商行凭证校验错误信息(" + new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒").format(new java.util.Date()) + ").txt";
			StringBuffer errorLog = new StringBuffer(""); // 存放全部的错误日志信息
			StringBuffer errorMessage = new StringBuffer(""); // 存放要提示给用户的错误日志信息，由于数据较长，所以只显示前15条
			for (int i = 0; errorInfoList.size() > 0 && i < errorInfoList.size(); i++) {
				String errorInfo = errorInfoList.get(i);
				errorLog.append(errorInfo + "\r\n");
				if (i < 15) {
					errorMessage.append(errorInfo + "\r\n");
				}
			}
			if (!"".equals(errorLog.toString())) {
				try {
					/** 清理掉10天之前的错误日志信息 **/
					FileUtil.getInstance().deleteFileWithDays(StateConstant.Import_Errinfo_DIR, Integer.parseInt(StateConstant.Import_Errinfo_BackDays));
					/** 将完整的错误信息保存到本地 **/
					FileUtil.getInstance().writeFile(errorLogName, errorLog.toString());
				} catch (Exception e) {
					/** 如果保存日志出现异常,则放弃保存 **/
					e.printStackTrace();// 用于调试
				}
			}

			/** 错误文件列表不为空,说明存在错误文件 **/
			StringBuffer sb = new StringBuffer("本次加载文件共" + serverFileList.size() + "个,其中" + (serverFileList.size() - errorFileList.size()) + "个成功," + errorFileList.size() + "个失败,部分信息如下【详细错误信息请查看" + errorLogName + "】：\r\n");

			sb.append(errorMessage.toString());
			return sb.toString();
		}
	}

	public List<File> getFileList() {
		return fileList;
	}

	public void setFileList(List<File> fileList) {
		this.fileList = fileList;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public List<Mapper> getCompareEnumList() {
		return compareEnumList;
	}

	public void setCompareEnumList(List<Mapper> compareEnumList) {
		this.compareEnumList = compareEnumList;
	}

	public String getCompareStatus() {
		return compareStatus;
	}

	public void setCompareStatus(String compareStatus) {
		this.compareStatus = compareStatus;
	}
}
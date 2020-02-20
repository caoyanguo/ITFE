package com.cfcc.itfe.client.recbiz.pbcdirectmodule;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.ResultDesDto;
import com.cfcc.itfe.persistence.dto.TbsTvPbcpayDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author hua
 * @time 12-06-13 09:30:11 子系统: RecBiz 模块:pbcDirectModule 组件:PbcDirectImport
 */
public class PbcDirectImportBean extends AbstractPbcDirectImportBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(PbcDirectImportBean.class);
	private Date adjustdate;
	private Date systemDate;
	private int totalnum;
	private BigDecimal totalmoney = new BigDecimal("0.00");
	private ITFELoginInfo loginfo;
	private TbsTvPbcpayDto _dto;

	public PbcDirectImportBean() {
		super();
		filelist = new ArrayList();
		// sumamt = new BigDecimal("");
		selectedfilelist = new ArrayList();
		showfilelist = new ArrayList();
		selectedDetaillist = new ArrayList();
		showDetaillist = new ArrayList();
		searchdto = new TbsTvPbcpayDto();
		pbcdto = new TbsTvPbcpayDto();
		pbclist = new ArrayList<ResultDesDto>();
		trecode = "";
		defvou = "";
		endvou = "";
		newbudgcode = "";
		singleResList = new ArrayList();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
	}

	/**
	 * Direction: 数据加载 ename: dataImport 引用方法: viewers: * messages:
	 */
	public String dataImport(Object o) {
		// 判断是否选中文件
		if (null == filelist || filelist.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要加载的文件!");
			return null;
		}
		// 初始正常的文件数量,即txt文件
		int trueSum = 0;
		// 用来存放错误信息
		StringBuffer errorPartInfo = new StringBuffer("");
		// 存放所有错误信息
		StringBuffer errorTotalInfo = new StringBuffer("");
		// 错误文件个数
		int errorFileCount = 0;
		// 加载到服务器之后的文件路径列表
		List<String> serverFilelist = new ArrayList<String>();
		try {
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			// 分别获得调整期日期和数据库日期
			adjustdate = commonDataAccessService.getAdjustDate();
			String dbstr = commonDataAccessService.getSysDBDate();
			systemDate = Date.valueOf(dbstr.substring(0, 4) + "-"
					+ dbstr.substring(4, 6) + "-" + dbstr.substring(6, 8));
			// 开始加载文件
			for (int i = 0; i < filelist.size(); i++) {
				File tmpFile = (File) filelist.get(i);
				// 得到文件名称
				String fileName = tmpFile.getName();
				// 获取文件的路径
				String filepath = tmpFile.getAbsolutePath();
				// 去掉非txt文件
				if (!fileName.trim().toLowerCase().endsWith(".txt")) {
					continue;
				}
				trueSum++;
				if (null == tmpFile || null == fileName || null == filepath) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, " 请选择要加载的文件！");
					return null;
				}
				// 校验文件名中包含的信息(长度、业务类型、调整期)
				String valiReturn = validateFileByName(
						BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY, fileName);
				// 文件名校验返回不为空，则为错误文件
				if (!"".equals(valiReturn)) {
					// 提示明细信息不超过20
					errorFileCount++;
					if (errorFileCount < 15) {
						errorPartInfo.append(valiReturn);
						errorTotalInfo.append(valiReturn);
					} else {
						errorTotalInfo.append(valiReturn);
					}
					continue;
				}
				// 将校验正确的文件上传到服务器上
				serverFilelist.add(ClientFileTransferUtil.uploadFile(tmpFile
						.getAbsolutePath()));
			}

			// 调用服务器加载并解析文件
			if (serverFilelist != null && serverFilelist.size() > 0) {
				// 服务器端加载文件并返回信息
				MulitTableDto bizDto = fileResolveCommonService.loadFile(
						serverFilelist,
						BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY,
						MsgConstant.PBC_DIRECT_IMPORT, null);
				int errCount = errorFileCount + bizDto.getErrorCount(); // 总错误个数
				if (null != bizDto.getErrorList()
						&& bizDto.getErrorList().size() > 0) {
					for (String error : bizDto.getErrorList()) {
						if (errorFileCount < 15) {
							errorFileCount++;
							errorPartInfo.append(error.substring(8) + "\r\n");
							errorTotalInfo.append(error + "\r\n");
						} else {
							errorTotalInfo.append(error + "\r\n");
						}
					}
				}
				String errName = StateConstant.Import_Errinfo_DIR
						+ "人行办理直接支付文件导入错误信息("
						+ new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒")
								.format(new java.util.Date()) + ").txt";
				if(!"".equals(errorTotalInfo.toString())) {
					FileUtil.getInstance().writeFile(errName,
							errorTotalInfo.toString());
				}
				
				FileUtil
						.getInstance()
						.deleteFileWithDays(
								StateConstant.Import_Errinfo_DIR,
								Integer
										.parseInt(StateConstant.Import_Errinfo_BackDays));
				// 记文件接收日志
				commonDataAccessService.saveTvrecvlog(DeleteServerFileUtil
						.wipeFileOut(serverFilelist, bizDto.getErrNameList()),
						BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY);
				// 将记录的错误信息提示给用户
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				if (errorPartInfo.toString().trim().length() > 0) {
					String noteInfo = "共加载了" + trueSum + "个文件，其中有" + errCount
							+ "个错误文件，部分信息如下【详细错误信息请查看" + errName + "】：\r\n";
					MessageDialog.openMessageDialog(null, noteInfo
							+ errorPartInfo.toString());
				} else {
					MessageDialog.openMessageDialog(null, "文件加载成功！");
				}

			} else {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				if (errorPartInfo.toString().trim().length() > 0) {
					MessageDialog.openMessageDialog(null, "共加载了" + trueSum
							+ "个文件，其中有" + errorFileCount + "个错误文件，信息如下：\r\n"
							+ errorPartInfo.toString());
				}

			}

		} catch (Throwable e) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			try {
				// 删除服务器上传失败文件
				DeleteServerFileUtil.delFile(commonDataAccessService,
						serverFilelist);
			} catch (ITFEBizException e1) {
				log.error("删除服务器文件失败！", e1);
				MessageDialog.openErrorDialog(null, e);
			}
			return null;
		}
		filelist = new ArrayList();
		this.editor.fireModelChanged();
		return super.dataImport(o);
	}

	/**
	 * Direction: 批量确认 ename: batchCommit 引用方法: viewers: * messages:
	 */
	public String batchCommit(Object o) {

		try {
			if (commonDataAccessService.judgeBatchConfirm(
					BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY, loginfo
							.getSorgcode())) {
				if (null == selectedfilelist || selectedfilelist.size() == 0) {
					MessageDialog.openMessageDialog(null, "请选择一条记录！");
					return null;
				}
				// 选中文件的总金额
				BigDecimal selSumamt = new BigDecimal(0.00);
				// 选中文件的凭证总笔数
				Integer selvouCount = 0;
				if (sumamt == null) {
					MessageDialog.openMessageDialog(null, "请填写总金额！");
					return null;
				}
				if (vouCount == null) {
					MessageDialog.openMessageDialog(null, "请填写凭证总笔数！");
					return null;
				}
				for (int i = 0; i < selectedfilelist.size(); i++) {
					TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
							.get(i);
					selSumamt = selSumamt.add(dto.getNmoney());
					selvouCount = selvouCount + dto.getIcount();
				}
				if (selSumamt.compareTo(sumamt) != 0
						|| selvouCount.compareTo(vouCount) != 0) {
					MessageDialog.openMessageDialog(null, "输入的总金额 ["
							+ sumamt.toString() + "]、凭证总笔数["
							+ vouCount.toString() + "]与销号文件的总金额、凭证总笔数不符,请查证!");
					return null;
				} else {
					// 批量处理
					massdispose();
				}
			} else {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "没有批量销号权限，请与管理员联系！");
				return null;
			}

		} catch (ITFEBizException e) {
			log.error("批量提交失败！", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.batchCommit(o);
	}

	/**
	 * Direction: 逐笔确认 ename: singleCommit 引用方法: viewers: * messages:
	 */
	public String singleCommit(Object o) {
		// 确认已经有选择的记录
		if (null == selectedDetaillist || selectedDetaillist.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择一条记录！");
			return null;
		}
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否确认提交？")) {
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			try {
				for (int i = 0; i < selectedDetaillist.size(); i++) {
					TbsTvPbcpayDto dto = (TbsTvPbcpayDto) selectedDetaillist
							.get(i);
					int result = uploadConfirmService.eachConfirm(
							BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY, dto);
					if (StateConstant.SUBMITSTATE_DONE == result
							|| StateConstant.SUBMITSTATE_SUCCESS == result) {
						showDetaillist.remove(dto);
					}
				}
				selectedDetaillist = new ArrayList();
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "操作成功！");
			} catch (ITFEBizException e) {
				log.error("逐笔提交失败！", e);
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openErrorDialog(null, e);
				return null;
			}
		}
		return super.singleCommit(o);
	}

	/**
	 * Direction: 批量删除 ename: batchDelete 引用方法: viewers: * messages:
	 */
	public String batchDelete(Object o) {
		// 确认已经有选择的记录
		if (null == selectedfilelist || selectedfilelist.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择一条记录！");
			return null;
		}
		// 提示用户确定删除
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否确认删除?")) {
			try {
				DisplayCursor.setCursor(SWT.CURSOR_WAIT);
				for (int i = 0; i < selectedfilelist.size(); i++) {
					TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
							.get(i);
					int result = uploadConfirmService.batchDelete(
							BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY, dto
									);
					if (StateConstant.SUBMITSTATE_DONE == result
							|| StateConstant.SUBMITSTATE_SUCCESS == result)
						showfilelist.remove(dto);

				}
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				selectedfilelist = new ArrayList();
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				MessageDialog.openMessageDialog(null, "操作成功！");
			} catch (ITFEBizException e) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				log.error("批量删除失败！", e);
				MessageDialog.openErrorDialog(null, e);
				return null;
			}
			return null;
		}
		return super.batchDelete(o);
	}

	/**
	 * Direction: 直接提交 ename: directCommit 引用方法: viewers: * messages:
	 */
	public String directCommit(Object o) {
		try {
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			if (commonDataAccessService.judgeDirectSubmit(
					BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY, loginfo
							.getSorgcode())) {
				int result = uploadConfirmService
						.directSubmit(BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY);
				if (StateConstant.SUBMITSTATE_SUCCESS == result) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "操作成功！");
				} else if (StateConstant.SUBMITSTATE_DONE == result) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "请导入数据！");
				} else {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "处理失败！");
				}
			} else {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "没有直接提交权限，请与管理员联系！");
				return null;
			}
		} catch (ITFEBizException e) {
			log.error("直接提交失败！", e);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.directCommit(o);
	}

	/**
	 * Direction: 查询服务 ename: queryService 引用方法: viewers: 查询界面 messages:
	 */
	public String queryService(Object o) {
		DisplayCursor.setCursor(SWT.CURSOR_WAIT);
		// 核算主体代码
		pbcdto.setSbookorgcode(loginfo.getSorgcode());
		// 未销号
		pbcdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
		if (null == defvou) {
			defvou = "";
		}
		if (null == endvou) {
			endvou = "";
		}
		pbcdto.setStrecode(trecode);
		pbcdto.setSvouno(defvou.trim() + endvou.trim());
		try {
			singleResList = uploadConfirmService.eachQuery(
					BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY, pbcdto);
			if (null == singleResList || singleResList.size() == 0) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "查询无记录!");
				return null;
			}
		} catch (ITFEBizException e) {
			log.error("逐笔销号查询异常！", e);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		DisplayCursor.setCursor(SWT.CURSOR_ARROW);
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.queryService(o);
	}

	/**
	 * Direction: 设置失败 ename: setFail 引用方法: viewers: * messages:
	 */
	public String thinkFail(Object o) {
		if (null == _dto || null == _dto.getIvousrlno()) {
			MessageDialog.openMessageDialog(null, "请选择要设置失败的记录！");
		} else {

			if (!StateConstant.CONFIRMSTATE_YES.equals(_dto.getSstatus())) {
				DisplayCursor.setCursor(SWT.CURSOR_WAIT);
				_dto.setSstatus(StateConstant.CONFIRMSTATE_YES);
				_dto.setSaddword("销号失败");
				_dto.setSpackageno("00000000");
				try {
					commonDataAccessService.updateData(_dto);
					// 判断文件下是否全部销号，如果全部销号，调用发送报文
					uploadConfirmService.checkAndSendMsg(_dto,
							MsgConstant.MSG_NO_5104,
							TbsTvPbcpayDto.tableName(), _dto.getIvousrlno());
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "设置销号失败成功！");
					singleResList.remove(_dto);
					_dto = null;
					editor.fireModelChanged();
				} catch (ITFEBizException e) {
					log.error("查询临时表未销号记录出错！", e);
					MessageDialog.openErrorDialog(null, e);
				}
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			} else {
				MessageDialog.openMessageDialog(null, "该凭证已销号，不能确认失败！");
			}
		}

		return "";
	}

	/**
	 * Direction: 跳转逐笔销号 ename: goToSingle 引用方法: viewers: 逐笔销号 messages:
	 */
	public String goToSingle(Object o) {
		if (StateConstant.SPECIAL_AREA_SHANXI.equals(loginfo.getArea())) { // 采取福建模式
			return super.openFJMode(o);
		}
		return super.goToSingle(o);
	}

	/**
	 * Direction: 单选一条记录 ename: selOneRecord 引用方法: viewers: * messages:
	 */
	public String selOneRecord(Object o) {
		_dto = (TbsTvPbcpayDto) o;
		return super.selOneRecord(o);
	}

	/**
	 * Direction: 全选 ename: selectAll 引用方法: viewers: * messages:
	 */
	public String selectAll(Object o) {
		if (null == selectedDetaillist || selectedDetaillist.size() == 0) {
			selectedDetaillist = new ArrayList();
			selectedDetaillist.addAll(showDetaillist);
		} else {
			selectedDetaillist.clear();
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.selectAll(o);
	}

	/**
	 * Direction: 批量销号查询 ename: batchQuery 引用方法: viewers: * messages:
	 */
	public String batchQuery(Object o) {

		return super.batchQuery(o);
	}

	/**
	 * Direction: 逐笔销号查询 ename: singleQuery 引用方法: viewers: * messages:
	 */
	public String singleQuery(Object o) {
		selectedDetaillist = new ArrayList();
		showDetaillist = new ArrayList();
		try {
			// 核算主体代码
			searchdto.setSbookorgcode(loginfo.getSorgcode());
			// 未销号
			searchdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
			showDetaillist = uploadConfirmService.eachQuery(
					BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY, searchdto);
			// 统计笔数金额
			List<TbsTvPbcpayDto> mlist = (List<TbsTvPbcpayDto>) showDetaillist;
			totalmoney = new BigDecimal(0.00);
			totalnum = 0;
			for (TbsTvPbcpayDto mdto : mlist) {
				totalmoney = totalmoney.add(mdto.getFamt());
				totalnum++;
			}
			if (showDetaillist.size() == 0) {
				MessageDialog.openMessageDialog(null, "没有查询到符合条件的数据！");
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				return null;
			}
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		} catch (ITFEBizException e) {
			log.error("逐笔销号查询失败！", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.singleQuery(o);
	}

	/**
	 * Direction: 统计信息 ename: countInfo 引用方法: viewers: * messages:
	 */
	public String countInfo(Object o) {
		List<TbsTvPbcpayDto> mlist = (List<TbsTvPbcpayDto>) selectedDetaillist;
		totalmoney = new BigDecimal(0.00);
		totalnum = 0;
		for (TbsTvPbcpayDto mdto : mlist) {
			totalmoney = totalmoney.add(mdto.getFamt());
			totalnum++;
		}
		this.editor.fireModelChanged();
		return super.countInfo(o);
	}

	/**
	 * 调用批量销号
	 * 
	 * @throws ITFEBizException
	 */
	private void massdispose() throws ITFEBizException {

		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否确认提交？")) {
			for (int i = 0; i < selectedfilelist.size(); i++) {
				TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
						.get(i);
				int result = uploadConfirmService.batchConfirm(
						BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY, dto
								);
				if (StateConstant.SUBMITSTATE_DONE == result
						|| StateConstant.SUBMITSTATE_SUCCESS == result) {
					showfilelist.remove(dto);
				}
			}
			selectedfilelist = new ArrayList();
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
			MessageDialog.openMessageDialog(null, "处理成功！");
		}

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

	/**
	 * 校验文件名(长度、业务类型、调整期)
	 */
	private String validateFileByName(String biztype, String filename)
			throws ITFEBizException {
		String fileN = filename.trim().toLowerCase();
		StringBuffer sb = new StringBuffer("");
		// 13或15位的数字加上.txt
		if (fileN.length() == 17 || fileN.length() == 19) {
			// 文件名中业务类型
			String biz = fileN
					.substring(fileN.length() - 7, fileN.length() - 5);
			// 文件名中调整期标志
			String trimflag = fileN.substring(fileN.length() - 5, fileN
					.length() - 4);
			if (!BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY.equals(biz)) {
				sb.append("文件 " + filename + " 业务类型不符\r\n");
			} else {
				if (!MsgConstant.TIME_FLAG_NORMAL.equals(trimflag)
						&& !MsgConstant.TIME_FLAG_TRIM.equals(trimflag)) {
					sb.append("文件 " + filename
							+ " 调整期标志不符合，必须为“0”正常期 或 “1”调整期！\r\n");
				} else {
					if (MsgConstant.TIME_FLAG_TRIM.equals(trimflag)) {
						if (DateUtil.after(systemDate, adjustdate)) {
							sb.append("文件 "
									+ filename
									+ " 调整期"
									+ com.cfcc.deptone.common.util.DateUtil
											.date2String(adjustdate)
									+ "已过，不能处理调整期业务!\r\n");
						}
					}
				}
			}
		} else {
			sb.append("文件 " + filename + " 文件名长度不符\r\n");
		}
		return sb.toString();
	}

	/**
	 * 绑定监听，用于在回车时执行
	 */
	public void setSumamt(BigDecimal sumamt) {
		if(null == vouCount || null == sumamt){
			MessageDialog.openMessageDialog(null, "凭证总数和总金额不能为空！");
			return ;
		}
		this.sumamt = sumamt;
		if (sumamt == null || sumamt.compareTo(new BigDecimal(0)) == 0) {
		} else {
			try {
				showfilelist = new ArrayList();
				selectedfilelist = new ArrayList();
				showfilelist = uploadConfirmService.batchQuery(
						BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY, sumamt);
				if (null != showfilelist) {
					if (1 == showfilelist.size()) {
						selectedfilelist.addAll(showfilelist);
						this.editor
								.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
						// massdispose();
					}
					this.editor
							.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				}
				if (showfilelist == null || showfilelist.size() <= 0) {
					MessageDialog.openMessageDialog(null, "没有符合条件的数据！");
				}
			} catch (ITFEBizException e) {
				log.error("操作失败！", e);
				MessageDialog.openErrorDialog(null, e);
			}
		}
	}

	public int getTotalnum() {
		return totalnum;
	}

	public void setTotalnum(int totalnum) {
		this.totalnum = totalnum;
	}

	public BigDecimal getTotalmoney() {
		return totalmoney;
	}

	public void setTotalmoney(BigDecimal totalmoney) {
		this.totalmoney = totalmoney;
	}

}
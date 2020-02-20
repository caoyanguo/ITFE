package com.cfcc.itfe.client.recbiz.updateinto;

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
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TbsTvInCorrhandbookDto;
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
 * @author Administrator
 * @time 12-02-21 15:15:51 子系统: RecBiz 模块:updateInto 组件:UpdateInto
 */
public class UpdateIntoBean extends AbstractUpdateIntoBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(UpdateIntoBean.class);
	private ITFELoginInfo loginfo;
	private BigDecimal sumamt;
	// 凭证总笔数
	private Integer vouCount;
	private Date adjustdate;
	private Date systemDate;

	public UpdateIntoBean() {
		super();
		filepath = new ArrayList();
		sumamt = null;
		vouCount = null;
		selectedfilelist = new ArrayList();
		showfilelist = new ArrayList();
		selecteddatalist = new ArrayList();
		showdatalist = new ArrayList();
		filedata = new FileResultDto();
		searchdto = new TbsTvInCorrhandbookDto();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();

	}

	/**
	 * Direction: 数据加载 ename: dateload 引用方法: viewers: * messages:
	 */
	public String dateload(Object o) {
		String interfacetype = ""; // 接口类型
		List<File> fileList = new ArrayList<File>();
		List<String> serverpathlist = new ArrayList<String>();
		StringBuffer errorPartInfo = new StringBuffer("");
		StringBuffer errorTotalInfo = new StringBuffer("");
		int errorFileCount = 0;
		// 判断是否选中文件
		if (null == filepath || filepath.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要加载的文件！");
			filepath = new ArrayList();
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
			return null;
		}
		try {
			adjustdate = commonDataAccessService.getAdjustDate();
			String str = commonDataAccessService.getSysDBDate();
			systemDate = Date.valueOf(str.substring(0, 4)+ "-" + str.substring(4, 6) + "-" + str.substring(6, 8));
			// 数据加载
			for (int i = 0; i < filepath.size(); i++) {
				File tmpfile = (File) filepath.get(i);
				String tmpfilename = tmpfile.getName().toLowerCase(); // 文件的名字
				String tmpfilepath = tmpfile.getAbsolutePath(); // 获取文件的路径
				if (null == tmpfile || null == tmpfilename
						|| null == tmpfilepath) {
					MessageDialog.openMessageDialog(null, " 请选择要加载的文件！");
					return null;
				}
				// 解析文件名字
				interfacetype = getFileObjByFileNameBiz(
						BizTypeConstant.BIZ_TYPE_CORRECT, tmpfilename);
				// 判断文件是否已经解析
//				if (commonDataAccessService.verifyImportRepeat(tmpfilename)) {
//					errorFileCount ++;
//					if(errorFileCount < 15) {
//						errorPartInfo.append("更正文件["+tmpfilename+"]校验为重复导入!\r\n");
//						errorTotalInfo.append("更正文件["+tmpfilename+"]校验为重复导入!\r\n");
//					} else {
//						errorTotalInfo.append("更正文件["+tmpfilename+"]校验为重复导入!\r\n");
//					}					
//					continue;
//				}
				// 文件上传
				String serverpath = ClientFileTransferUtil.uploadFile(tmpfile
						.getAbsolutePath());
				fileList.add(new File(serverpath));
				serverpathlist.add(serverpath);
			}
			// 服务器加载
			MulitTableDto bizDto = fileResolveCommonService.loadFile(fileList,
					BizTypeConstant.BIZ_TYPE_CORRECT, interfacetype, null);
			int tmp_errcount = errorFileCount;
			String errName = "";
			if(null != bizDto.getErrorList() && bizDto.getErrorList().size() > 0) {
				for(int m = 0 ; m < bizDto.getErrorList().size() ; m++) {
					tmp_errcount ++ ;
					if(tmp_errcount < 15) {
						errorPartInfo.append(bizDto.getErrorList().get(m).substring(2) +"\r\n");
						errorTotalInfo.append(bizDto.getErrorList().get(m) +"\r\n");
					} else {
						errorTotalInfo.append(bizDto.getErrorList().get(m) +"\r\n");
					}
				}
			}
			if(fileList != null && fileList.size() > 0) {
				errName = StateConstant.Import_Errinfo_DIR 
				+  "更正文件导入错误信息("+new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒").format(new java.util.Date())+").txt";
				if(!"".equals(errorTotalInfo.toString())) {
					FileUtil.getInstance().writeFile(errName, errorTotalInfo.toString());
				}
				FileUtil.getInstance().deleteFileWithDays(StateConstant.Import_Errinfo_DIR, Integer.parseInt(StateConstant.Import_Errinfo_BackDays));
				commonDataAccessService.saveTvrecvlog(DeleteServerFileUtil.wipeFileOut(serverpathlist, bizDto.getErrNameList()),
						BizTypeConstant.BIZ_TYPE_CORRECT);
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				if (errorPartInfo.toString().trim().length() > 0) {	
					String noteInfo = "共加载了" + filepath.size() + "个文件，其中有" + (errorFileCount+bizDto.getErrorCount()) + "个错误文件，部分信息如下【详细错误信息请查看"+errName+"】：\r\n";				
					MessageDialog.openMessageDialog(null, noteInfo + errorPartInfo.toString());
				}else {
					MessageDialog.openMessageDialog(null, "文件加载成功,本次共加载成功 "+fileList.size()+" 个文件！");
				}
			}else {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				if(null != filepath && filepath.size() > 0) {
					String errmsg="";
					if (errorFileCount > 0) {
					    errmsg = "其中有" + errorFileCount + "个错误文件，信息如下：\r\n" + errorPartInfo.toString();
					}
					MessageDialog.openMessageDialog(null, "共加载了" + filepath.size() + "个文件" +errmsg);
				}
			}
			
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
			//删除服务器上传失败文件
			try {
				DeleteServerFileUtil.delFile(commonDataAccessService, serverpathlist);
			} catch (ITFEBizException e1) {
				log.error("删除服务器文件失败！", e1);
				MessageDialog.openErrorDialog(null, e);
			}
			return null;
		}
		filepath = new ArrayList();
		this.editor.fireModelChanged();
		return super.dateload(o);
	}

	/**
	 * Direction: 跳转批量销号查询 ename: topiliangxh 引用方法: viewers: 批量销号 messages:
	 */
	public String topiliangxh(Object o) {
		return super.topiliangxh(o);
	}

	/**
	 * Direction: 跳转逐笔销号查询 ename: tozhubixh 引用方法: viewers: 逐笔销号 messages:
	 */
	public String tozhubixh(Object o) {
		return super.tozhubixh(o);
	}

	/**
	 * Direction: 直接提交 ename: submit 引用方法: viewers: * messages:
	 */
	public String submit(Object o) {
		try {
			if (commonDataAccessService.judgeDirectSubmit(
					BizTypeConstant.BIZ_TYPE_CORRECT, loginfo.getSorgcode())) {
				int result = uploadConfirmService
						.directSubmit(BizTypeConstant.BIZ_TYPE_CORRECT);
				if(StateConstant.SUBMITSTATE_SUCCESS == result){
					MessageDialog.openMessageDialog(null, "操作成功！");
				}else if(StateConstant.SUBMITSTATE_DONE == result){
					MessageDialog.openMessageDialog(null, "请导入数据！");
				}else{
					MessageDialog.openMessageDialog(null, "处理失败！");
				}
			} else {
				MessageDialog.openMessageDialog(null, "没有直接提交权限，请与管理员联系！");
			}
		} catch (ITFEBizException e) {
			log.error("直接提交失败！", e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.submit(o);
	}

	/**
	 * Direction: 返回默认界面 ename: goback 引用方法: viewers: 更正导入 messages:
	 */
	public String goback(Object o) {
		return super.goback(o);
	}

	/**
	 * Direction: 批量确认 ename: plsubmit 引用方法: viewers: * messages:
	 */
	public String plsubmit(Object o) {

		try {
			if (commonDataAccessService.judgeBatchConfirm(
					BizTypeConstant.BIZ_TYPE_CORRECT, loginfo.getSorgcode())) {

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
				MessageDialog.openMessageDialog(null, "没有批量销号权限，请与管理员联系！");
			}
			} catch (ITFEBizException e) {
				log.error("批量提交失败！", e);
				MessageDialog.openErrorDialog(null, e);
				return null;
			}
		return super.plsubmit(o);
	}

	/**
	 * Direction: 批量删除 ename: pldel 引用方法: viewers: * messages:
	 */
	public String pldel(Object o) {
		// 确认已经有选择的记录
		if (null == selectedfilelist || selectedfilelist.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择一条记录！");
			return null;
		}
		// 提示用户确定删除
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否确认删除?")) {
			try {
				for (int i = 0; i < selectedfilelist.size(); i++) {
					TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
							.get(i);
					int result = uploadConfirmService.batchDelete(
							BizTypeConstant.BIZ_TYPE_CORRECT, dto
									);
					if (StateConstant.SUBMITSTATE_DONE == result
							|| StateConstant.SUBMITSTATE_SUCCESS == result)
						showfilelist.remove(dto);

				}
				selectedfilelist = new ArrayList();
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				MessageDialog.openMessageDialog(null, "处理成功！");
			} catch (ITFEBizException e) {
				log.error("批量删除失败！", e);
				MessageDialog.openErrorDialog(null, e);
				return null;
			}
			return null;
		}

		return super.pldel(o);
	}

	/**
	 * Direction: 逐笔提交 ename: zbsubmit 引用方法: viewers: * messages:
	 */
	public String zbsubmit(Object o) {
		// 确认已经有选择的记录
		if (null == selecteddatalist || selecteddatalist.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择一条记录！");
			return null;
		}
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否确认提交？")) {
			try {
				for (int i = 0; i < selecteddatalist.size(); i++) {
					TbsTvInCorrhandbookDto dto = (TbsTvInCorrhandbookDto) selecteddatalist
							.get(i);
					int result = uploadConfirmService.eachConfirm(
							BizTypeConstant.BIZ_TYPE_CORRECT, dto);
					if (StateConstant.SUBMITSTATE_DONE == result
							|| StateConstant.SUBMITSTATE_SUCCESS == result)
						showdatalist.remove(dto);

				}
				selecteddatalist = new ArrayList();
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				MessageDialog.openMessageDialog(null, "处理成功！");
			} catch (ITFEBizException e) {
				log.error("逐笔提交失败！", e);
				MessageDialog.openErrorDialog(null, e);
				return null;
			}
		}
		return super.zbsubmit(o);
	}

	// /**
	// * Direction: 批量销号查询 ename: plsearch 引用方法: viewers: * messages:
	// */
	// public String plsearch(Object o) {
	// 调用其他接口获取showfilelist，如果showfilelist有两条记录，则不选中，否则直接选中
	// try {
	// showfilelist = new ArrayList();
	// selectedfilelist = new ArrayList();
	// showfilelist = uploadConfirmService.batchQuery(
	// BizTypeConstant.BIZ_TYPE_CORRECT, sumamt);
	// if (null != showfilelist) {
	// if (1 == showfilelist.size()) {
	// selectedfilelist.addAll(showfilelist);
	// }
	// }
	// this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	// } catch (ITFEBizException e) {
	// log.error("批量销号查询错误！", e);
	// MessageDialog.openErrorDialog(null, e);
	// return null;
	// }
	//
	// return super.plsearch(o);
	// }

	/**
	 * Direction: 逐笔销号查询 ename: zbsearch 引用方法: viewers: * messages:
	 */
	public String zbsearch(Object o) {
		selecteddatalist = new ArrayList();
		showdatalist = new ArrayList();
		try {
			//核算主体代码
			searchdto.setSbookorgcode(loginfo.getSorgcode());
			//未销号
			searchdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
			showdatalist = uploadConfirmService.eachQuery(
					BizTypeConstant.BIZ_TYPE_CORRECT, searchdto);
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		} catch (ITFEBizException e) {
			log.error("逐笔销号查询失败！", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.zbsearch(o);
	}

	/**
	 * Direction: 全选 ename: selectall 引用方法: viewers: * messages:
	 */
	public String selectall(Object o) {
		if (null == selecteddatalist || selecteddatalist.size() == 0) {
			selecteddatalist = new ArrayList();
			selecteddatalist.addAll(showdatalist);
		} else {
			selecteddatalist.clear();
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.selectall(o);
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

	// ///////////////////////////////////////////////////////////////////////////////////////
	private String getFileObjByFileNameBiz(String biztype, String filename)
			throws ITFEBizException {
		String interficetype = "";
		String tmpfilename_new = filename.trim().toLowerCase();
		FileObjDto fileobj = new FileObjDto();
		if (tmpfilename_new.indexOf(".txt") > 0) {
			String tmpfilename = tmpfilename_new.toLowerCase().replace(".txt", "");
			// 十三位说明是有可能是地方横连或tbs bool为true 则为tbs接口否则为地方横连
			if (tmpfilename.length() == 13 || tmpfilename.length() == 15 || tmpfilename.length()==14) {
				// 8位日期，2位批次号，2位业务类型，1位调整期标志组成
				fileobj.setSdate(tmpfilename.substring(0, 8)); // 日期
				if (tmpfilename.length() == 13 ) {
					fileobj.setSbatch(tmpfilename.substring(8, 10)); // 批次号
					fileobj.setSbiztype(tmpfilename.substring(10, 12)); // 业务类型
					fileobj.setCtrimflag(tmpfilename.substring(12, 13)); // 调整期标志
				} else if(tmpfilename.length() == 15){
					fileobj.setSbatch(tmpfilename.substring(8, 12)); // 批次号
					fileobj.setSbiztype(tmpfilename.substring(12, 14)); // 业务类型
					fileobj.setCtrimflag(tmpfilename.substring(14, 15)); // 调整期标志
				}else{
					fileobj.setSbatch(tmpfilename.substring(8, 10)); // 批次号
					fileobj.setSbiztype(tmpfilename.substring(10, 12)); // 业务类型
					fileobj.setCtrimflag(tmpfilename.substring(12, 13)); // 业务类型
				}
				interficetype = MsgConstant.GENGZHENG_DAORU;// tbs接口
				if (BizTypeConstant.BIZ_TYPE_CORRECT.equals(fileobj
						.getSbiztype())) {
				} else {
					throw new ITFEBizException("业务类型不符合!");
				}
				if (!MsgConstant.TIME_FLAG_NORMAL
						.equals(fileobj.getCtrimflag())
						&& !MsgConstant.TIME_FLAG_TRIM.equals(fileobj
								.getCtrimflag())) {
					throw new ITFEBizException("调整期标志不符合，必须为“0”正常期 或 “1”调整期!");
				}else{
					if(MsgConstant.TIME_FLAG_TRIM.equals(fileobj.getCtrimflag())){
						if(DateUtil.after(systemDate,adjustdate)){
							throw new ITFEBizException("调整期" + com.cfcc.deptone.common.util.DateUtil.date2String(adjustdate) + "已过，不能处理调整期业务！");
						}
					}
				}
			} else {
				throw new ITFEBizException("文件名格式不符!");
			}
		} else {
			throw new ITFEBizException("文件名格式不符!");
		}
		return interficetype;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public BigDecimal getSumamt() {
		return sumamt;
	}

	public void setSumamt(BigDecimal sumamt) {
		if(null == vouCount || null == sumamt){
			MessageDialog.openMessageDialog(null, "凭证总数和总金额不能为空！");
			return ;
		}
		this.sumamt = sumamt;
		if (sumamt == null || sumamt.compareTo(new BigDecimal(0)) == 0) {
			// MessageDialog.openMessageDialog(null, "金额不能为空或0！");
		} else {
			try {
				showfilelist = new ArrayList();
				selectedfilelist = new ArrayList();
				showfilelist = uploadConfirmService.batchQuery(
						BizTypeConstant.BIZ_TYPE_CORRECT, sumamt);
				if (null != showfilelist) {
					if (1 == showfilelist.size()) {
						selectedfilelist.addAll(showfilelist);
						this.editor
								.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
						// 批量销号处理
						//massdispose();
					}
					this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				}
				if(showfilelist==null||showfilelist.size()<=0){
					MessageDialog.openMessageDialog(null, "没有符合条件的数据！");
				}
			} catch (ITFEBizException e) {
				log.error("批量处理失败！", e);
				MessageDialog.openErrorDialog(null, e);
			}
		}
	}

	// 批量销号处理
	private void massdispose() throws ITFEBizException {
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否确认提交？")) {
			for (int i = 0; i < selectedfilelist.size(); i++) {
				TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
						.get(i);
				int result = uploadConfirmService.batchConfirm(
						BizTypeConstant.BIZ_TYPE_CORRECT, dto);
				if (StateConstant.SUBMITSTATE_DONE == result
						|| StateConstant.SUBMITSTATE_SUCCESS == result)
					showfilelist.remove(dto);

			}
			selectedfilelist = new ArrayList();
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
			MessageDialog.openMessageDialog(null, "处理成功！");
		}
	}

	public Integer getVouCount() {
		return vouCount;
	}

	public void setVouCount(Integer vouCount) {
		this.vouCount = vouCount;
	}
}
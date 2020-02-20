package com.cfcc.itfe.client.recbiz.fundintoforfz;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.ChinaTest;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.client.common.encrypt.DecryptClientUtil;
import com.cfcc.itfe.client.common.file.ErrorNoteDialog;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.ResultDesDto;
import com.cfcc.itfe.persistence.dto.ShiboDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IFileResolveCommonService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IUploadConfirmService;
import com.cfcc.itfe.service.recbiz.fundinto.IFundIntoService;
import com.cfcc.itfe.util.AreaSpecUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 13-09-13 15:34:59 子系统: RecBiz 模块:fundintoforfz 组件:Fundintoforfz
 */
public class FundintoforfzBean extends AbstractFundintoforfzBean implements
		IPageDataProvider {
	private static Log log = LogFactory.getLog(FundintoforfzBean.class);
	protected IFileResolveCommonService fileResolveCommonService = (IFileResolveCommonService) getService(IFileResolveCommonService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService) getService(ICommonDataAccessService.class);
	protected IFundIntoService fundIntoService = (IFundIntoService) getService(IFundIntoService.class);
	protected IUploadConfirmService uploadConfirmService = (IUploadConfirmService) getService(IUploadConfirmService.class);
	private Date adjustdate;
	private Date systemDate;
	private List salarydetaillist;
	private BigDecimal salarysumamt;
	private TbsTvPayoutDto salarysearch;
	private ITFELoginInfo loginfo;
	private List filepath;
	private ShiboDto shibodto;

	public FundintoforfzBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		salarydetaillist = new ArrayList();
		salarysumamt = new BigDecimal(0);
		salarysearch = new TbsTvPayoutDto();
		filepath = new ArrayList();

	}

	/**
	 * Direction: 工资文件加载 ename: salarydataload 引用方法: viewers: * messages:
	 */
	public String salarydataload(Object o) {
		// 判断是否选中文件
		// if (!dataload(filepath, "Salary", serverpathlist)) {
		// return null;
		// }
		Map tmpMap = new HashMap();
		String interfacetype = ""; // 接口类型
		List<File> fileList = new ArrayList<File>();
		List<String> serverpathlist = new ArrayList<String>();
		StringBuffer errorPartInfo = new StringBuffer(""); // 用来存放错误信息
		StringBuffer errorTotalInfo = new StringBuffer(""); // 存放所有错误信息
		int errorFileCount = 0;
		// 判断是否选中文件
		if (null == filepath || filepath.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要加载的文件！");
			filepath = new ArrayList();
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
			return null;
		}

		try {
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			boolean bool = false;
			Set<String> sets = new HashSet<String>();
			for (int i = 0; i < filepath.size(); i++) {
				String tmpname = ((File) filepath.get(i)).getName();
				String str = tmpname.toLowerCase().substring(
						tmpname.lastIndexOf("."));
				// 判断是否两种文件都有。txt格式和pas格式同时加载
				if (".txt".equals(str) || ".pas".equals(str)) {
					sets.add(str);
					if (sets.size() == 2) {
						break;
					}
				} else {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "选择的文件格式不正确！");
					return null;
				}
			}
			if (sets.size() == 1) {
				Object[] strs = (Object[]) sets.toArray();
				if (".pas".equals(strs[0])) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "选择的文件格式不正确！");
					return null;
				}
			} else if (sets.size() == 2) {
				bool = true;
				// 获取密匙
				tmpMap = commonDataAccessService
						.getMiYao(loginfo.getSorgcode());
			}

			adjustdate = commonDataAccessService.getAdjustDate();
			String tmpdate = commonDataAccessService.getSysDBDate();
			systemDate = Date.valueOf(tmpdate.substring(0, 4) + "-"
					+ tmpdate.substring(4, 6) + "-" + tmpdate.substring(6, 8));
			// 数据加载
			boolean financeflag = true; // 记录是否是财政接口
			for (int i = 0; i < filepath.size(); i++) {
				File tmpfile = (File) filepath.get(i);
				String tmpfilename = tmpfile.getName().toLowerCase(); // 文件的名字
				String tmpfilepath = tmpfile.getAbsolutePath(); // 获取文件的路径

				// 判断文件是否选中
				if (null == tmpfile || null == tmpfilename
						|| null == tmpfilepath) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, " 请选择要加载的文件！");
					return null;
				}
				// 解析文件名字获取接口类型
				interfacetype = getFileObjByFileNameBiz(
						BizTypeConstant.BIZ_TYPE_PAY_OUT, tmpfilename, bool);
				// 判断文件是否已经解析
				if (commonDataAccessService.verifyImportRepeat(tmpfilename)) {
					errorFileCount++;
					if (errorFileCount < 15) {
						errorPartInfo.append("实拨资金文件[" + tmpfilename
								+ "]校验为重复导入!\r\n");
						errorTotalInfo.append("实拨资金文件[" + tmpfilename
								+ "]校验为重复导入!\r\n");
					} else {
						errorTotalInfo.append("实拨资金文件[" + tmpfilename
								+ "]校验为重复导入!\r\n");
					}
					continue;
				}
				// 福建接口处理 如果是财政接口，首先判断征税机构是否相同，如果不同停止处理
				if (MsgConstant.SHIBO_FUJIAN_DAORU.equals(interfacetype)) {
					if (financeflag) { // 如果financeflag为true，开始判断文件列表是否是同一征税机关，financeflag为false，已经判断并且文件列表时同一征税机关
						if (judgeOrgcode(filepath)) {
							DisplayCursor.setCursor(SWT.CURSOR_ARROW);
							MessageDialog.openMessageDialog(null,
									"选择文件不是同一个征税机构，请重新选择！");
							// 删除服务器已上传文件
							DeleteServerFileUtil.delFile(
									commonDataAccessService, serverpathlist);
							return null;
						}
						financeflag = false;
					}
					// 验签
					if (!TipsFileDecrypt.verifyCA(tmpfilepath)) {
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						MessageDialog.openMessageDialog(null, "验证文件有误！");
						// 删除服务器已上传文件
						DeleteServerFileUtil.delFile(commonDataAccessService,
								serverpathlist);
						return null;
					}
					// 上传并且记录日志
					String serverpath = ClientFileTransferUtil
							.uploadFile(tmpfile.getAbsolutePath());
					fileList.add(new File(serverpath));
					serverpathlist.add(serverpath);
					continue;
				}
				// 以上为福建接口处理

				// 以下是 文件是厦门的pas文件 和txt文件共同处理
				if (bool) {
					if (tmpfilename.endsWith(".pas")) {
						if (ChinaTest.isChinese(tmpfilepath)) {
							DisplayCursor.setCursor(SWT.CURSOR_ARROW);
							MessageDialog.openMessageDialog(null,
									"文件路径有汉字，请更改路径！");
							// 删除服务器已上传文件
							DeleteServerFileUtil.delFile(
									commonDataAccessService, serverpathlist);
							return null;
						}
						String miyao = (String) tmpMap.get(tmpfilename
								.substring(tmpfilename.length() - 16,
										tmpfilename.lastIndexOf(".")));
						if (miyao != null && miyao.length() > 0) {
							// 密钥解密
							miyao = commonDataAccessService.decrypt(miyao);
							if ("-1".equals(TipsFileDecrypt.decryptPassFile(
									tmpfilepath, tmpfilepath.replaceAll(".pas",
											".tmp"), miyao))) {
								DisplayCursor.setCursor(SWT.CURSOR_ARROW);
								MessageDialog.openMessageDialog(null,
										tmpfilename + "文件解密失败！");
								// 删除服务器已上传文件
								DeleteServerFileUtil
										.delFile(commonDataAccessService,
												serverpathlist);
								return null;
							}
							// 上传未解析文件并记录日志
							String serverpath = ClientFileTransferUtil
									.uploadFile(tmpfile.getAbsolutePath());
							// 上传解析成功文件
							fileList.add(new File(ClientFileTransferUtil
									.uploadFile(tmpfile.getAbsolutePath()
											.replaceAll(".pas", ".tmp"))));
							serverpathlist.add(serverpath);
							// 删除临时文件
							FileUtil.getInstance().deleteFile(
									tmpfilepath.replaceAll(".pas", ".tmp"));
							continue;
						} else {
							DisplayCursor.setCursor(SWT.CURSOR_ARROW);
							MessageDialog.openMessageDialog(null,
									"密钥没有维护，请先维护密钥！");
							// 删除服务器已上传文件
							DeleteServerFileUtil.delFile(
									commonDataAccessService, serverpathlist);
							return null;
						}
					}
				}
				// 厦门接口处理
				// 上传。txt文件
				String serverpath = ClientFileTransferUtil.uploadFile(tmpfile
						.getAbsolutePath());
				fileList.add(new File(serverpath));
				serverpathlist.add(serverpath);
			}
			MulitTableDto bizDto = null;
			String errName = "";
			// 如果接口是福建的，则弹出对话框选择付款人信息
			if (fileList != null && fileList.size() > 0) {
				if (MsgConstant.SHIBO_FUJIAN_DAORU.equals(interfacetype)) {
					String str = ((File) filepath.get(0)).getName();
					List<ShiboDto> shiboList = fundIntoService.getPayerInfo(str
							.substring(str.indexOf("q") - 9, str.indexOf("q")),
							loginfo.getSorgcode());
					// 如果付款人信息是一个 则不弹出对话框
					if (null == shiboList || shiboList.size() == 0) {
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						// 如果没有对应的收款人信息，则提示用户
						MessageDialog
								.openMessageDialog(null, "没有对应的付款人信息，请核实！");
						// 删除服务器已上传文件
						DeleteServerFileUtil.delFile(commonDataAccessService,
								serverpathlist);
						return null;
					} else {
						// 付款人信息存在多条则弹出对话框
						if (shiboList.size() > 1) {
							DisplayCursor.setCursor(SWT.CURSOR_ARROW);
							ErrorNoteDialog dialog = new ErrorNoteDialog(null,
									shiboList);
							if (IDialogConstants.OK_ID == dialog.open()) {
								this.shibodto = dialog.getShibo();
								DisplayCursor.setCursor(SWT.CURSOR_WAIT);
							}
						} else {
							this.shibodto = shiboList.get(0);
						}
					}
					bizDto = fileResolveCommonService.loadFile(fileList,
							BizTypeConstant.BIZ_TYPE_PAY_OUT, interfacetype
									+ "Salary", shibodto);
				} else {
					// 服务器加载上传文件
					bizDto = fileResolveCommonService.loadFile(fileList,
							BizTypeConstant.BIZ_TYPE_PAY_OUT, interfacetype
									+ "Salary", null);
				}
				int tmp_errcount = errorFileCount;
				if (null != bizDto.getErrorList()
						&& bizDto.getErrorList().size() > 0) {
					for (int m = 0; m < bizDto.getErrorList().size(); m++) {
						tmp_errcount++;
						if (tmp_errcount < 15) {
							errorPartInfo.append(bizDto.getErrorList().get(m)
									.substring(4)
									+ "\r\n");
							errorTotalInfo.append(bizDto.getErrorList().get(m)
									+ "\r\n");
						} else {
							errorTotalInfo.append(bizDto.getErrorList().get(m)
									+ "\r\n");
						}
					}
				}
				errName = StateConstant.Import_Errinfo_DIR
						+ "实拨资金文件导入错误信息("
						+ new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒")
								.format(new java.util.Date()) + ").txt";
				if (!"".equals(errorTotalInfo.toString())) {
					FileUtil.getInstance().writeFile(errName,
							errorTotalInfo.toString());
				}
				FileUtil
						.getInstance()
						.deleteFileWithDays(
								StateConstant.Import_Errinfo_DIR,
								Integer
										.parseInt(StateConstant.Import_Errinfo_BackDays));
				// 记录日志
				commonDataAccessService.saveTvrecvlog(DeleteServerFileUtil
						.wipeFileOut(serverpathlist, bizDto.getErrNameList()),
						BizTypeConstant.BIZ_TYPE_PAY_OUT);
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				if (errorPartInfo.toString().trim().length() > 0) {
					String noteInfo = "共加载了" + filepath.size() + "个文件，其中有"
							+ (errorFileCount + bizDto.getErrorCount())
							+ "个错误文件，部分信息如下【详细错误信息请查看" + errName + "】：\r\n";
					MessageDialog.openMessageDialog(null, noteInfo
							+ errorPartInfo.toString());
				} else {
					MessageDialog.openMessageDialog(null, "文件加载成功！");
				}
			} else {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "共加载了" + filepath.size()
						+ "个文件，其中有" + errorFileCount + "个错误文件，信息如下：\r\n"
						+ errorPartInfo.toString());
			}

		} catch (Throwable e) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			// 删除服务器已上传文件
			try {
				DeleteServerFileUtil.delFile(commonDataAccessService,
						serverpathlist);
			} catch (ITFEBizException e1) {
				log.error("删除服务器文件失败！", e1);
				MessageDialog.openErrorDialog(null, e);
			}
			return null;
		}
		DisplayCursor.setCursor(SWT.CURSOR_ARROW);
		filepath = new ArrayList();
		this.editor.fireModelChanged();
		return super.salarydataload(o);
	}

	/**
	 * Direction: 跳转工资文件销号列表 ename: gosalarydestoryview 引用方法: viewers: 工资文件销号
	 * messages:
	 */
	public String gosalarydestoryview(Object o) {
		return super.gosalarydestoryview(o);
	}

	/**
	 * Direction: 返回工资加载页 ename: backsalaryloadview 引用方法: viewers: 工资文件加载
	 * messages:
	 */
	public String backsalaryloadview(Object o) {
		return super.backsalaryloadview(o);
	}

	/**
	 * Direction: 工资文件销号操作 ename: salarydestory 引用方法: viewers: * messages:
	 */
	public String salarydestory(Object o) {
		try {
			List list = fundintoforfzService.getdestorydata(salarysearch);
			if (null == list || list.size() == 0) {
				MessageDialog.openMessageDialog(null, "没有需要销号的数据！");
				return null;
			}
			TbsTvPayoutDto updatedto = (TbsTvPayoutDto) salarysearch.clone();
			updatedto.setSbookorgcode(loginfo.getSorgcode());
			updatedto.setSstatus(StateConstant.CONFIRMSTATE_NO);
			int result = fundintoforfzService.destorydata(updatedto);
			if (StateConstant.SUBMITSTATE_DONE == result
					|| StateConstant.SUBMITSTATE_SUCCESS == result) {
				salarydetaillist.clear();
				salarysumamt = new BigDecimal(0);
				this.editor.fireModelChanged();
				MessageDialog.openMessageDialog(null, "操作成功！");
				return null;
			}

		} catch (ITFEBizException e) {
			log.error("工资文件销号失败！", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}

		return super.salarydestory(o);
	}

	/**
	 * Direction: 工资文件查询 ename: salarysearch 引用方法: viewers: * messages:
	 */
	public String salarysearch(Object o) {
		try {
			salarydetaillist.clear();
			salarydetaillist = fundintoforfzService
					.getdestorydata(salarysearch);
			if (null == salarydetaillist || salarydetaillist.size() == 0) {
				this.editor.fireModelChanged();
				MessageDialog.openMessageDialog(null, "没有符合条件的数据！");
				return null;
			}
			this.editor.fireModelChanged();
		} catch (ITFEBizException e) {
			log.error("查询导入工资文件详细信息失败！", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.salarysearch(o);
	}

	/**
	 * 
	 * 判断选择的文件是否是一个征税机构
	 */
	private boolean judgeOrgcode(List<File> filelist) {
		if (filelist.size() == 1) {
			return false;
		} else {
			String tmpname = ((File) filepath.get(0)).getName();
			String taxcode = tmpname.substring(tmpname.indexOf("z") + 1,
					tmpname.lastIndexOf("q"));
			for (int i = 1; i < filepath.size(); i++) {
				String str = ((File) filepath.get(i)).getName();
				if (!taxcode.equals(str.substring(str.indexOf("z") + 1, str
						.lastIndexOf("q")))) {
					return true;
				}
			}
			return false;
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	private String getFileObjByFileNameBiz(String biztype, String filename,
			boolean bool) throws ITFEBizException {
		String interficetype = "";
		String tmpfilename_new = filename.trim().toLowerCase();
		FileObjDto fileobj = new FileObjDto();

		if (tmpfilename_new.toLowerCase().indexOf(".txt") > 0) {
			tmpfilename_new = tmpfilename_new.toLowerCase();
			String tmpfilename = tmpfilename_new.replace(".txt", "");
			// 十三位说明是有可能是地方横连或tbs bool为true 则为tbs接口否则为地方横连
			if (tmpfilename.length() == 13 || tmpfilename.length() == 15) {
				// 8位日期，2位批次号，2位业务类型，1位调整期标志组成
				fileobj.setSdate(tmpfilename.substring(0, 8)); // 日期
				if (tmpfilename.length() == 13) {
					fileobj.setSbatch(tmpfilename.substring(8, 10)); // 批次号
					fileobj.setSbiztype(tmpfilename.substring(10, 12)); // 业务类型
					fileobj.setCtrimflag(tmpfilename.substring(12, 13)); // 调整期标志
				} else {
					fileobj.setSbatch(tmpfilename.substring(8, 12)); // 批次号
					fileobj.setSbiztype(tmpfilename.substring(12, 14)); // 业务类型
					fileobj.setCtrimflag(tmpfilename.substring(14, 15)); // 业务类型
				}
				if (bool) {
					interficetype = MsgConstant.SHIBO_XIAMEN_DAORU;// tbs接口
				} else {
					interficetype = MsgConstant.SHIBO_SHANDONG_DAORU; // 横连接口类型
					// 常量还没定义
				}
				if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(fileobj
						.getSbiztype())
						|| BizTypeConstant.BIZ_TYPE_PAY_OUT2.equals(fileobj
								.getSbiztype())) {
				} else {
					throw new ITFEBizException("业务类型不符合!");
				}
				if (!MsgConstant.TIME_FLAG_NORMAL
						.equals(fileobj.getCtrimflag())
						&& !MsgConstant.TIME_FLAG_TRIM.equals(fileobj
								.getCtrimflag())) {
					throw new ITFEBizException("调整期标志不符合，必须为“0”正常期 或 “1”调整期!");
				} else {
					if (MsgConstant.TIME_FLAG_TRIM.equals(fileobj
							.getCtrimflag())) {
						if (DateUtil.after(systemDate, adjustdate)) {
							throw new ITFEBizException("调整期"
									+ com.cfcc.deptone.common.util.DateUtil
											.date2String(adjustdate)
									+ "已过，不能处理调整期业务！");
						}
					}
				}

			} else if (tmpfilename.length() == 23) {
				fileobj.setSdate(tmpfilename.substring(0, 8)); // 日期
				fileobj.setSbiztype(BizTypeConstant.BIZ_TYPE_PAY_OUT); // 业务类型
				interficetype = MsgConstant.SHIBO_FUJIAN_DAORU;// 财政接口
				if ("q".equals(tmpfilename.substring(19, 20))) {
				} else {
					throw new ITFEBizException("文件名格式不符!");
				}
			} else {
				throw new ITFEBizException("文件名格式不符!");
			}
		} else if (tmpfilename_new.indexOf(".pas") > 0) {
			// 接收清算行行号（12位）＋日期(8位)＋当日文件编号（10位）＋核算主体(12位)“.pas
			String tmpfilename = tmpfilename_new.replace(".pas", "");
			if (tmpfilename.length() == 42) {
				if (!commonDataAccessService.auditBankCode(tmpfilename
						.substring(0, 12)))
					throw new ITFEBizException("清算行号不存在!");
				interficetype = MsgConstant.SHIBO_XIAMEN_DAORU;// TBS接口数据
			} else {
				throw new ITFEBizException("文件名格式不符!");
			}
		} else {
			throw new ITFEBizException("文件名格式不符!");
		}
		return interficetype;
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

	public List getSalarydetaillist() {
		return salarydetaillist;
	}

	public void setSalarydetaillist(List salarydetaillist) {
		this.salarydetaillist = salarydetaillist;
	}

	public BigDecimal getSalarysumamt() {
		return salarysumamt;
	}

	public void setSalarysumamt(BigDecimal salarysumamt) {
		this.salarysumamt = salarysumamt;
	}

	public TbsTvPayoutDto getSalarysearch() {
		return salarysearch;
	}

	public void setSalarysearch(TbsTvPayoutDto salarysearch) {
		this.salarysearch = salarysearch;
	}

	public Date getAdjustdate() {
		return adjustdate;
	}

	public void setAdjustdate(Date adjustdate) {
		this.adjustdate = adjustdate;
	}

	public Date getSystemDate() {
		return systemDate;
	}

	public void setSystemDate(Date systemDate) {
		this.systemDate = systemDate;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public List getFilepath() {
		return filepath;
	}

	public void setFilepath(List filepath) {
		this.filepath = filepath;
	}

	public ShiboDto getShibodto() {
		return shibodto;
	}

	public void setShibodto(ShiboDto shibodto) {
		this.shibodto = shibodto;
	}

}
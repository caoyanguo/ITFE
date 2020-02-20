package com.cfcc.itfe.client.recbiz.fundpay;

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
import com.cfcc.itfe.client.common.file.ErrorNoteDialog;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.client.dialog.MessageInfoDialog;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.BizDataCountDto;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.ResultDesDto;
import com.cfcc.itfe.persistence.dto.ShiboDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TsCheckbalanceDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.AreaSpecUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.constants.MetaDataConstants;
import com.cfcc.jaf.ui.metadata.ViewMetaData;

/**
 * codecomment:
 * 
 * @author hua
 * @time 12-09-27 09:19:17 子系统: RecBiz 模块:fundpay 组件:FundPay
 */
public class FundPayBean extends AbstractFundPayBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(FundPayBean.class);

	private ITFELoginInfo loginfo;
	private BigDecimal sumamt;
	// 凭证总笔数
	private Integer vouCount;
	private Date adjustdate;
	private Date systemDate;
	private BigDecimal totalmoney = new BigDecimal(0.00);
	private int totalnum = 0;
	private TbsTvPayoutDto _dto;;
	private String area;
	private GroupQueryBean groupbean = null;
	private TbsTvPayoutDto groupSelect = new TbsTvPayoutDto();
	//销号待定选中list
	List selectedwaitlist = null;

	public FundPayBean() {
		super();
		filepath = new ArrayList();
		sumamt = null;
		vouCount = null;
		selectedfilelist = new ArrayList();
		showfilelist = new ArrayList();
		selecteddatalist = new ArrayList();
		showdatalist = new ArrayList();
		selectedwaitlist = new ArrayList();
		filedata = new FileResultDto();
		searchdto = new TbsTvPayoutDto();
		paysdto = new TbsTvPayoutDto();
		paylist = new ArrayList<ResultDesDto>();
		trecode = "";
		searchdto.setDaccept(DateUtil.currentDate());
		defvou = "";
		endvou = "";
		newbudgcode = "";
		grpno = "";
		totalcount = null;
		setGroupbean(new GroupQueryBean(commonDataAccessService,new TbsTvPayoutDto()));
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		area = AreaSpecUtil.getInstance().getArea();

	}

	/**
	 * Direction: 数据加载 ename: dateload 引用方法: viewers: * messages:
	 */
	public String dateload(Object o) {
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
				if (commonDataAccessService.verifyImportRepeat(tmpfilename)
						&& bool) {
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
							BizTypeConstant.BIZ_TYPE_PAY_OUT, interfacetype,
							shibodto);
				} else {
					// 服务器加载上传文件
					bizDto = fileResolveCommonService.loadFile(fileList,
							BizTypeConstant.BIZ_TYPE_PAY_OUT, interfacetype,
							null);
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
					MessageInfoDialog.openMessageDialog(null, noteInfo
							+ errorPartInfo.toString());
				} else {
					MessageDialog.openMessageDialog(null, "文件加载成功,本次共加载成功 "
							+ fileList.size() + " 个文件！");
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
//		if ("FUZHOU".equals(area)) { // 采取福建模式
			return super.openfjPayout(o);
//		}
//		return super.tozhubixh(o);
	}

	/**
	 * Direction: 直接提交 ename: submit 引用方法: viewers: * messages:
	 */
	public String submit(Object o) {
		try {
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			if (commonDataAccessService.judgeDirectSubmit(
					BizTypeConstant.BIZ_TYPE_PAY_OUT, loginfo.getSorgcode())) {
				int result = uploadConfirmService
						.directSubmit(BizTypeConstant.BIZ_TYPE_PAY_OUT);
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
		}
		return super.submit(o);
	}

	/**
	 * Direction: 返回默认界面 ename: goback 引用方法: viewers: 直接支付额度导入 messages:
	 */
	public String goback(Object o) {
		paysdto.setSpayeename(null);
		paysdto.setFamt(null);
		editor.fireModelChanged();
		return super.goback(o);
	}

	/**
	 * Direction: 批量确认 ename: plsubmit 引用方法: viewers: * messages:
	 */
	public String plsubmit(Object o) {
		try {
			if(true) {
				MessageDialog.openMessageDialog(null, "该功能尚未开放!");
				return super.plsubmit(o);
			}			
			if (commonDataAccessService.judgeBatchConfirm(
					BizTypeConstant.BIZ_TYPE_PAY_OUT, loginfo.getSorgcode())) {
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
					DisplayCursor.setCursor(SWT.CURSOR_WAIT);
					massdispose();
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				}
			} else {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "没有批量销号权限，请与管理员联系！");
				return null;
			}

		} catch (ITFEBizException e) {
			log.error("批量处理失败！", e);
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
		DisplayCursor.setCursor(SWT.CURSOR_WAIT);
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否确认删除?")) {
			try {
				for (int i = 0; i < selectedfilelist.size(); i++) {
					TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
							.get(i);
					int result = uploadConfirmService.batchDelete(
							BizTypeConstant.BIZ_TYPE_PAY_OUT, dto);
					if (StateConstant.SUBMITSTATE_DONE == result
							|| StateConstant.SUBMITSTATE_SUCCESS == result)
						showfilelist.remove(dto);

				}
				selectedfilelist = new ArrayList();
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "操作成功！");
			} catch (ITFEBizException e) {
				log.error("批量删除失败！", e);
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
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
		DisplayCursor.setCursor(SWT.CURSOR_WAIT);
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否确认提交？")) {
			try {
				for (int i = 0; i < selecteddatalist.size(); i++) {
					TbsTvPayoutDto dto = (TbsTvPayoutDto) selecteddatalist
							.get(i);
					int result = uploadConfirmService.eachConfirm(
							BizTypeConstant.BIZ_TYPE_PAY_OUT, dto);
					if (StateConstant.SUBMITSTATE_DONE == result
							|| StateConstant.SUBMITSTATE_SUCCESS == result)
						showdatalist.remove(dto);

				}
				selecteddatalist = new ArrayList();
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
		return super.zbsubmit(o);
	}

	// /**
	// * Direction: 批量销号查询 ename: plsearch 引用方法: viewers: * messages:
	// */
	// public String plsearch(Object o) {
	// // 调用其他接口获取showfilelist，如果showfilelist有两条记录，则不选中，否则直接选中
	// try {
	// showfilelist = new ArrayList();
	// selectedfilelist = new ArrayList();
	// showfilelist = uploadConfirmService.batchQuery(
	// BizTypeConstant.BIZ_TYPE_PAY_OUT, sumamt);
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
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			// 核算主体代码
			searchdto.setSbookorgcode(loginfo.getSorgcode());
			// 未销号
			searchdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
			showdatalist = uploadConfirmService.eachQuery(
					BizTypeConstant.BIZ_TYPE_PAY_OUT, searchdto);
			// 统计笔数金额
			List<TbsTvPayoutDto> mlist = (List<TbsTvPayoutDto>) showdatalist;
			totalmoney = new BigDecimal(0.00);
			totalnum = 0;
			for (TbsTvPayoutDto mdto : mlist) {
				totalmoney = totalmoney.add(mdto.getFamt());
				totalnum++;
			}
			if (showdatalist.size() == 0) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "没有查询到符合条件的数据！");
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				return null;
			}

			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		} catch (ITFEBizException e) {
			log.error("逐笔销号查询失败！", e);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		DisplayCursor.setCursor(SWT.CURSOR_ARROW);
		return super.zbsearch(o);
	}

	/**
	 * Direction: 查询服务 ename: queryService 引用方法: viewers: 查询界面 messages:
	 */
	public String queryService(Object o) {
		DisplayCursor.setCursor(SWT.CURSOR_WAIT);
		// 核算主体代码
		paysdto.setSbookorgcode(loginfo.getSorgcode());
		// 未销号
		paysdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
		if (null == defvou) {
			defvou = "";
		}
		if (null == endvou) {
			endvou = "";
		}
		paysdto.setStrecode(trecode);
		paysdto.setSvouno(defvou.trim() + endvou.trim());
		paysdto.setSgroupid(null);
		try {
			querylist = uploadConfirmService.eachQuery(
					BizTypeConstant.BIZ_TYPE_PAY_OUT+",grpnull", paysdto);
			if (null == querylist || querylist.size() == 0) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "没有查询到未销号的记录!");
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

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
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
						BizTypeConstant.BIZ_TYPE_PAY_OUT, sumamt);
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

	// 批量提交处理
	private void massdispose() throws ITFEBizException {
		String warnInfo = "";
		if (selectedfilelist.size() == 0) {

		}
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否确认提交？")) {
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			for (int i = 0; i < selectedfilelist.size(); i++) {
				TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
						.get(i);
				int result = uploadConfirmService.batchConfirm(
						BizTypeConstant.BIZ_TYPE_PAY_OUT, dto);
				if (StateConstant.SUBMITSTATE_DONE == result
						|| StateConstant.SUBMITSTATE_SUCCESS == result)
					showfilelist.remove(dto);

			}
			selectedfilelist = new ArrayList();
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openMessageDialog(null, "处理成功！");
		}
	}
	
	/**
	 * Direction: 销号待定全选
	 * ename: selectallwait
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectallwait(Object o){
		if (null == selectedwaitlist || selectedwaitlist.size() == 0) {
			selectedwaitlist = new ArrayList();
			selectedwaitlist.addAll(querylist);
		} else {
			selectedwaitlist.clear();
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.selectallwait(o);
    }

	/**
	 * Direction: 设置失败 ename: setFail 引用方法: viewers: * messages:
	 */
	public String setFail(Object o) {
		if (selectedwaitlist == null || selectedwaitlist.size()==0) {
			MessageDialog.openMessageDialog(null, "请选择要设置销号待定的记录！");
		} else {
			String msg="";
			for(int i=0; i < selectedwaitlist.size(); i++){
				TbsTvPayoutDto dto = (TbsTvPayoutDto) selectedwaitlist.get(i);
				if (!StateConstant.CONFIRMSTATE_YES.equals(dto.getSstatus())) {
					DisplayCursor.setCursor(SWT.CURSOR_WAIT);
					try {
						// 判断文件下是否全部销号，如果全部销号，调用发送报文
						uploadConfirmService.checkAndSendMsg(dto,
								MsgConstant.MSG_NO_5101,
								TbsTvPayoutDto.tableName(), dto.getIvousrlno());
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						dto = null;
						editor.fireModelChanged();
					} catch (ITFEBizException e) {
						log.error("查询临时表未销号记录出错！", e);
						MessageDialog.openErrorDialog(null, e);
					}
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				} else {
					msg += "该凭证"+ dto.getSvouno() +"已销号，不能设置成销号待定！\n";
				}
			}
			selectedwaitlist.clear();
			if("".equals(msg)){
				try {
					querylist = uploadConfirmService.eachQuery(BizTypeConstant.BIZ_TYPE_PAY_OUT, paysdto);
				} catch (ITFEBizException e) {
					log.error("查询临时表未销号记录出错！", e);
					MessageDialog.openErrorDialog(null, e);
				}
				if (null == querylist || querylist.size() == 0) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "查询无记录!");
					return null;
				}
				MessageDialog.openMessageDialog(null, "设置销号待定成功！");
			}else{
				MessageDialog.openMessageDialog(null, msg);
			}
		}
		editor.fireModelChanged();
		return super.setFail(o);
	}

	/**
	 * Direction: 单选一条记录 ename: selOneRecord 引用方法: viewers: * messages:
	 */
	public String selOneRecord(Object o) {
		_dto = (TbsTvPayoutDto) o;
		return super.selOneRecord(o);
	}

	/**
	 * Direction: 明细删除 ename: delDetail 引用方法: viewers: * messages:
	 */
	public String delDetail(Object o) {
		if (selectedwaitlist ==null || selectedwaitlist.size()==0) {
			MessageDialog.openMessageDialog(null, "请选择要删除的记录！");
		} else {
			if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
					.getCurrentComposite().getShell(), "提示", "是否确认删除选中的数据?")) {
			for(int i=0; i < selectedwaitlist.size(); i++){
				TbsTvPayoutDto _dto = (TbsTvPayoutDto) selectedwaitlist.get(i);
				if (!StateConstant.CONFIRMSTATE_YES.equals(_dto.getSstatus())) {
					DisplayCursor.setCursor(SWT.CURSOR_WAIT);
					try {
						int result = uploadConfirmService.eachDelete(
								BizTypeConstant.BIZ_TYPE_PAY_OUT, _dto);
						if (StateConstant.SUBMITSTATE_DONE == result
								|| StateConstant.SUBMITSTATE_SUCCESS == result)
							showdatalist.remove(_dto);
						// 判断文件下是否全部销号，如果全部销号，调用发送报文
						uploadConfirmService.checkAndSendMsg(_dto,
								MsgConstant.MSG_NO_5101,
								TbsTvPayoutDto.tableName(), null);
						querylist.remove(_dto);
						_dto = null;
						editor.fireModelChanged();
					} catch (ITFEBizException e) {
						log.error("查询临时表未销号记录出错！", e);
						MessageDialog.openErrorDialog(null, e);
					}
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				}
			}
			selectedwaitlist.clear();
			MessageDialog.openMessageDialog(null, "明细删除成功！");
			}
		}
		return super.delDetail(o);

	}

	/**
	 * Direction: 试算平衡 ename: tryBalance 引用方法: viewers: * messages:
	 */
    public String tryBalance(Object o){
    	if(null == grpno || "".equals(grpno)) {
    		MessageDialog.openMessageDialog(null, "请输入组号!");
    		return "";
    	}
    	/**
    	 * 校验平衡之前将当前总笔数和总金额更新进数据库
    	 */
    	try {
    		/**
	    	 * 找到当前组号信息
	    	 */
	    	TsCheckbalanceDto check = getcheckDto();        
	    	List l = commonDataAccessService.findRsByDtoUR(check);
			if(null == l || l.size() == 0) {
				MessageDialog.openMessageDialog(null, "该组号对应信息不存在或已提交！");
				return "";
			}
	    	TsCheckbalanceDto updateDto = getcheckDto();
	    	StringBuffer sb = new StringBuffer("");
	    	if(null == totalcount || totalcount == 0) {
				sb.append("总笔数必须大于0\n");
			}
			if(null == totalfamt || totalfamt.compareTo(new BigDecimal("0.00"))==0) {
				sb.append("总金额不能为空\n");
			}
			if(!"".equals(sb.toString())) {
				MessageDialog.openMessageDialog(null, sb.toString());
				return "";
			}
			/**
			 * 将用户修改之后的总金额、总笔数更新到数据库中
			 */
	    	updateDto.setItotalnum(this.getTotalcount());
	    	updateDto.setNtotalmoney(this.getTotalfamt());
	    	updateDto.setIcheckednum(((TsCheckbalanceDto)l.get(0)).getIcheckednum());
	    	updateDto.setNcheckmoney(((TsCheckbalanceDto)l.get(0)).getNcheckmoney());
	    	updateDto.setSisbalance(((TsCheckbalanceDto)l.get(0)).getSisbalance());
	    	commonDataAccessService.updateData(updateDto);
	    	
			TsCheckbalanceDto resdto = (TsCheckbalanceDto)l.get(0);
			Integer checknum = resdto.getIcheckednum();
			Integer totalunm = resdto.getItotalnum();
			BigDecimal checkfamt = resdto.getNcheckmoney();
			BigDecimal totalfamt = resdto.getNtotalmoney();
			
			if(null == checknum || checknum.intValue() == 0) {
				checknum = 0;
			}
			if(null == totalunm || totalunm.intValue() == 0) {
				totalunm = 0;
			}
			if(null == checkfamt) {
				checkfamt = new BigDecimal("0.00");
			}
			if(null == totalfamt) {
				totalfamt = new BigDecimal("0.00");
			}
			if(totalunm.intValue() == checknum.intValue()) {
				if(this.getTotalfamt().compareTo(checkfamt) == 0) { //平衡
					if(!"1".equals(resdto.getSisbalance())) {
						resdto.setSisbalance("1"); //1--平衡
						commonDataAccessService.updateData(resdto);
					}
					MessageDialog.openMessageDialog(null, "组校验平衡,可进行提交操作！\n" +
							"["+grpno+"] 已销号笔数:"+checknum+" 已销号金额:"+checkfamt);
					return "";
				}else { //不平衡
					if(!"0".equals(resdto.getSisbalance())) {
						resdto.setSisbalance("0"); //1--不平衡
						commonDataAccessService.updateData(resdto);
					}
					MessageDialog.openMessageDialog(null, "组校验不平衡,且已销号笔数达到总笔数,详细如下：\n" +
							"["+grpno+"] 已销号笔数:"+checknum+" 已销号金额:"+checkfamt);
					return "";
				}
			}else {
				MessageDialog.openMessageDialog(null, "组校验不平衡！\n" +
						"["+grpno+"] 已销号笔数:"+checknum+" 已销号金额:"+checkfamt);
				return "";
			}
			
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
        return super.tryBalance(o);
    }
	
    public TsCheckbalanceDto getcheckDto() {
    	TsCheckbalanceDto check = new TsCheckbalanceDto();
        check.setSorgcode(loginfo.getSorgcode());
        check.setSgroupid(getGrpno());
        check.setSusercode(loginfo.getSuserCode());
        return check;
    }
    /**
	 * Direction: 查询本组 ename: queryTheGroup 引用方法: viewers: 组信息显示 messages:
	 */
    public String queryTheGroup(Object o){
    	if(null == grpno || "".equals(grpno)) {
    		MessageDialog.openMessageDialog(null, "请输入组号!");
    		return "";
    	}
        String s = queryGroup();
        if("".equals(s)) {
        	return super.queryTheGroup(o) ;
        }else if(null == s) {
        	MessageDialog.openMessageDialog(null, "该组号尚无销号信息!");
        	return "";
        }
        return super.queryTheGroup(o) ;       
    }
    
    public String queryGroup() {    	
    	TbsTvPayoutDto paydto = new TbsTvPayoutDto();
        paydto.setSusercode(loginfo.getSuserCode());
        paydto.setSbookorgcode(loginfo.getSorgcode());
        paydto.setSgroupid(grpno);
        paydto.setDaccept(TimeFacade.getCurrentDateTime());
//        paydto.setSstatus("0");
        groupbean.setPayout(paydto);
        
        PageRequest grpRequest = new PageRequest();
        PageResponse resp = groupbean.retrieve(grpRequest);
        if(null == resp) {
        	return null;
        }
        editor.fireModelChanged();
        return "";
    }
    
	/**
	 * Direction: 本组提交 ename: groupSubmit 引用方法: viewers: * messages:
	 */
    public String groupSubmit(Object o){
    	if(null == grpno || "".equals(grpno)) {
    		MessageDialog.openMessageDialog(null, "请输入组号!");
    		return "";
    	}
    	TsCheckbalanceDto check = getcheckDto(); 
    	try {
			List l = commonDataAccessService.findRsByDtoUR(check);
			if(null == l || l.size() == 0) {
				MessageDialog.openMessageDialog(null, "该组号对应信息不存在或已提交!");
				return "";
			}
			TsCheckbalanceDto resdto = (TsCheckbalanceDto)l.get(0);
			
			if(!"1".equals(resdto.getSisbalance())) {
				MessageDialog.openMessageDialog(null, "该组尚未平衡，请先校验平衡!");
				return "";
			}		
			boolean boo = confirmGroup();
			if(boo) {
				/**
				 * 1、删除使用完的组信息
				 */
				TsCheckbalanceDto cedto = new TsCheckbalanceDto();
				cedto.setSgroupid(grpno);
				cedto.setSorgcode(loginfo.getSorgcode());
				cedto.setSusercode(loginfo.getSuserCode());
				fundIntoService.deleteData(cedto);
				/**
				 * 2、置空界面上的要素
				 *    平衡之后置空组号、总笔数、总金额
				 */				
				setGrpno("");
				setTotalcount(null); //总笔数
				setTotalfamt(null);
				paysdto.setFamt(null);
				paysdto.setSpayeename(null);
				setPaylist(new ArrayList());
				setEndvou("");
				setSpayeecode("");
				setNewbudgcode(null);
				MessageDialog.openMessageDialog(null, "本组数据提交成功!");
				editor.fireModelChanged();
				/**
				 * 3、刷新界面，并将鼠标的焦点定位在‘所属国库代码’区域上，本组销号结束
				 */
				MVCUtils.setContentAreaFocus(editor, "逐笔销号");
				editor.openComposite(((ViewMetaData) editor.getCurrentComposite()
						.getData(MetaDataConstants.META_DATA_KEY)).id, true,true);
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}    	
        return super.groupSubmit(o);
    }
    
    /**
	 * Direction: 移除出本组 ename: wipeOutFromGroup 引用方法: viewers: * messages:
	 */
	public String wipeOutFromGroup(Object o) {
		if (null == groupSelect 
				|| "null".equals(groupSelect)
				|| null == groupSelect.getSbookorgcode()
				|| "".equals(groupSelect.getSbookorgcode())) {
			MessageDialog.openMessageDialog(null, "请选择一条记录!");
			return "";
		}
		if("1".equals(groupSelect.getSstatus())){
			MessageDialog.openMessageDialog(null, "该记录已经提交不能移除!");
			return "";
		}
		groupSelect.setSgroupid(null);
		groupSelect.setSusercode(null);
		try {
			BizDataCountDto datacount = new BizDataCountDto();
			datacount.setBizname(grpno);
			datacount.setTotalfamt(groupSelect.getFamt());
			/**
			 * //首先将数据更新成原始数据并在组中去除数据
			 */
			String grpresult = fundIntoService.updateGroup(datacount, "", "", groupSelect,"wipe");
			/**
			 * 重新取数据，相当于刷新
			 */
			queryGroup();
			groupSelect = null;
			editor.fireModelChanged();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.wipeOutFromGroup(o);
	}

	/**
	 * Direction: 选择一条组信息 ename: chooseOneGroup 引用方法: viewers: * messages:
	 */
	public String chooseOneGroup(Object o) {
		groupSelect = (TbsTvPayoutDto)o;
		return super.chooseOneGroup(o);
	}
    
	public String selectEvent(Object o) {
		List<TbsTvPayoutDto> mlist = (List<TbsTvPayoutDto>) selecteddatalist;
		totalmoney = new BigDecimal(0.00);
		totalnum = 0;
		for (TbsTvPayoutDto mdto : mlist) {
			totalmoney = totalmoney.add(mdto.getFamt());
			totalnum++;
		}
		this.editor.fireModelChanged();
		return super.selectEvent(o);
	}
	
	/**
	 * 根据组号 直接提交整组·
	 */
	public boolean confirmGroup() throws ITFEBizException {
		TbsTvPayoutDto paramdto = new TbsTvPayoutDto();
		paramdto.setSbookorgcode(loginfo.getSorgcode()); //核算主体
		paramdto.setSgroupid(grpno); //组号
		paramdto.setSusercode(loginfo.getSuserCode()); //用户名
		paramdto.setSstatus(StateConstant.CONFIRMSTATE_NO); //未销号
		List l = commonDataAccessService.findRsByDtoUR(paramdto);
		if(null == l || l.size() == 0) {
			return Boolean.FALSE;
		}else {
			for(Object obj : l) {
				IDto idto = (IDto)obj; 
				int result = uploadConfirmService.eachConfirm(BizTypeConstant.BIZ_TYPE_PAY_OUT, idto);
				if (StateConstant.SUBMITSTATE_DONE != result && StateConstant.SUBMITSTATE_SUCCESS != result) {
					return Boolean.FALSE;			
				}
			}
		}
		return Boolean.TRUE;		
	}

	public BigDecimal getTotalmoney() {
		return totalmoney;
	}

	public void setTotalmoney(BigDecimal totalmoney) {
		this.totalmoney = totalmoney;
	}

	public int getTotalnum() {
		return totalnum;
	}

	public void setTotalnum(int totalnum) {
		this.totalnum = totalnum;
	}

	public Integer getVouCount() {
		return vouCount;
	}

	public void setVouCount(Integer vouCount) {
		this.vouCount = vouCount;
	}

	public GroupQueryBean getGroupbean() {
		return groupbean;
	}

	public void setGroupbean(GroupQueryBean groupbean) {
		this.groupbean = groupbean;
	}

	public TbsTvPayoutDto getGroupSelect() {
		return groupSelect;
	}

	public void setGroupSelect(TbsTvPayoutDto groupSelect) {
		this.groupSelect = groupSelect;
	}

	public List getSelectedwaitlist() {
		return selectedwaitlist;
	}

	public void setSelectedwaitlist(List selectedwaitlist) {
		this.selectedwaitlist = selectedwaitlist;
	}

}
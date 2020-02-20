package com.cfcc.itfe.client.recbiz.fundinto;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.client.dialog.MessageInfoDialog;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.ResultDesDto;
import com.cfcc.itfe.persistence.dto.ShiboDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
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
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 12-02-21 15:15:51 子系统: RecBiz 模块:fundInto 组件:FundInto
 */
public class FundIntoBean extends AbstractFundIntoBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(FundIntoBean.class);
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
	private String sfilename;
	private List filelist;
	private int filecount=0;
	private int okcount=0;
	private int nocount=0;
	public FundIntoBean() {
		super();
		filepath = new ArrayList();
		filelist = new ArrayList();
		sumamt = null;
		vouCount = null;
		selectedfilelist = new ArrayList();
		showfilelist = new ArrayList();
		selecteddatalist = new ArrayList();
		showdatalist = new ArrayList();
		filedata = new FileResultDto();
		searchdto = new TbsTvPayoutDto();
		paysdto = new TbsTvPayoutDto();
		paylist = new ArrayList<ResultDesDto>();
		trecode = "";
		searchdto.setDaccept(DateUtil.currentDate());
		defvou = "";
		endvou = "";
		newbudgcode = "";
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		area = loginfo.getArea();
		sfilename="";
		init();
	}
	private void init()
	{
		// 核算主体代码
		paysdto.setSbookorgcode(loginfo.getSorgcode());
		// 未销号
		paysdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(TimeFacade.getCurrentDate());
			calendar.add(Calendar.DATE, -10);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 时间格式
			String getdate = dateFormat.format(calendar.getTime());
			List qlist = commonDataAccessService.findRsByDtoWithWhere(paysdto, " and D_VOUCHER >='"+getdate+"'");
			if(qlist!=null&&qlist.size()>0)
			{
				Set<String> tmpTreSet = new HashSet<String>();
				TbsTvPayoutDto tmp = null;
				filelist = new ArrayList();
				TsTreasuryDto filemap = null;
				for(int i=0;i<qlist.size();i++)
				{
					tmp = (TbsTvPayoutDto)qlist.get(i);
					if(tmpTreSet.add(tmp.getStrecode()+tmp.getSfilename()))
					{
						filemap = new TsTreasuryDto();
						filemap.setStrecode(tmp.getStrecode()+"-"+tmp.getSfilename());
						filemap.setStrename(tmp.getStrecode()+"-"+tmp.getSfilename());
						filelist.add(filemap);
					}
				}
				
			}
		} catch (ITFEBizException e) {
			log.error("逐笔销号查询异常！", e);
			MessageDialog.openErrorDialog(null, e);
		}
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
					// MessageDialog.openMessageDialog(null, noteInfo +
					// errorPartInfo.toString());
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
		// 采取逐笔录入方式福建模式
		if (StateConstant.SPECIAL_AREA_FUZHOU.equals(area)
				|| StateConstant.SPECIAL_AREA_GUANGDONG.equals(area)) {
			return super.openfjPayout(o);
		}
		return super.tozhubixh(o);
	}

	/**
	 * Direction: 直接提交 ename: submit 引用方法: viewers: * messages:
	 */
	public String submit(Object o) {
		try {
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			if (commonDataAccessService.judgeDirectSubmit(
					BizTypeConstant.BIZ_TYPE_PAY_OUT, loginfo.getSorgcode())) {
				
				if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(area)) {
					List<TbsTvPayoutDto> retList = needGrantList(null, null);
					if (null != retList && retList.size() > 0) {// 主管授权
						String msg = "系统内调拨支出以下账户在账户表中不存在,需要主管授权:\n";
						for (TbsTvPayoutDto tmp : retList) {
							msg = msg + tmp.getSpayeeacct() + "\n";
						}
						if (!AdminConfirmDialogFacade.open(msg)) {
							DisplayCursor.setCursor(SWT.CURSOR_ARROW);
							return null;
						}
					}
				}
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
		return super.goback(o);
	}

	/**
	 * Direction: 批量确认 ename: plsubmit 引用方法: viewers: * messages:
	 */
	public String plsubmit(Object o) {
		try {
			if (StateConstant.IfStopPayoutSubmit_true
					.equals(commonDataAccessService
							.getCacheInfo("IFSTOPPAYOUTSUBMIT"))) {
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
					if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(area)) {
						List<TbsTvPayoutDto> retList = needGrantList(dto
								.getSfilename(), null);
						if (null != retList && retList.size() > 0) {// 主管授权
							String msg = "系统内调拨支出以下账户在账户表中不存在,需要主管授权:\n";
							for (TbsTvPayoutDto tmp : retList) {
								msg = msg + tmp.getSpayeeacct() + "\n";
							}
							if (!AdminConfirmDialogFacade.open(msg)) {
								DisplayCursor.setCursor(SWT.CURSOR_ARROW);
								return null;
							}
						}
					}
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
			MessageDialog.openMessageDialog(null, "请选择需要删除的记录！");
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
					if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(area)) {
						List<TbsTvPayoutDto> retList = needGrantList(null, dto.getIvousrlno());
						if (null != retList && retList.size() > 0) {// 主管授权
							String msg = "系统内调拨支出以下账户在账户表中中不存,需要主管授权:\n";
							for (TbsTvPayoutDto tmp : retList) {
								msg = msg + tmp.getSpayeeacct() + "\n";
							}
							if (!AdminConfirmDialogFacade.open(msg)) {
								DisplayCursor.setCursor(SWT.CURSOR_ARROW);
								return null;
							}
						}
					}
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
		try {
			querylist = uploadConfirmService.eachQuery(
					BizTypeConstant.BIZ_TYPE_PAY_OUT, paysdto);
			if (null == querylist || querylist.size() == 0) {
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

	public String delDetail2(Object o) {
		if (null == selecteddatalist || selecteddatalist.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择一条记录！");
			return null;
		} else {
			if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(
					this.editor.getCurrentComposite().getShell(), "提示",
					"是否确认删除？")) {

				try {
					for (int i = 0; i < selecteddatalist.size(); i++) {
						TbsTvPayoutDto dto = (TbsTvPayoutDto) selecteddatalist
								.get(i);
						if (!StateConstant.CONFIRMSTATE_YES.equals(dto
								.getSstatus())) {
							int result = uploadConfirmService.eachDelete(
									BizTypeConstant.BIZ_TYPE_PAY_OUT, dto);
							if (StateConstant.SUBMITSTATE_DONE == result
									|| StateConstant.SUBMITSTATE_SUCCESS == result)
								showdatalist.remove(dto);
							// 判断文件下是否全部销号，如果全部销号，调用发送报文
							uploadConfirmService.checkAndSendMsg(dto,
									MsgConstant.MSG_NO_5101, TbsTvPayoutDto
											.tableName(), null);
						}
					}
					totalmoney = new BigDecimal(0.00);
					totalnum = 0;
					for (int i = 0; i < showdatalist.size(); i++) {
						TbsTvPayoutDto dto = (TbsTvPayoutDto) showdatalist
								.get(i);
						totalmoney = totalmoney.add(dto.getFamt());
						totalnum++;
					}
					selecteddatalist = new ArrayList();
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "明细删除成功！");
					editor.fireModelChanged();
				} catch (ITFEBizException e) {
					log.error("查询临时表未销号记录出错！", e);
					MessageDialog.openErrorDialog(null, e);
				}
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			}
		}
		return super.delDetail2(o);
	}

	public String setFail2(Object o) {
		if (null == selecteddatalist || selecteddatalist.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择一条记录！");
			return null;
		} else {
			if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(
					this.editor.getCurrentComposite().getShell(), "提示",
					"是否确认销号待定？")) {
				try {
					// 判断文件下是否全部销号，如果全部销号，调用发送报文
					for (int i = 0; i < selecteddatalist.size(); i++) {
						TbsTvPayoutDto dto = (TbsTvPayoutDto) selecteddatalist
								.get(i);
						if (!StateConstant.CONFIRMSTATE_YES.equals(dto
								.getSstatus())) {

							uploadConfirmService.setFail(
									MsgConstant.MSG_NO_5101, dto);

							uploadConfirmService.checkAndSendMsg(dto,
									MsgConstant.MSG_NO_5101, TbsTvPayoutDto
											.tableName(), null);
						} else {
							MessageDialog.openMessageDialog(null,
									"该凭证已销号，不能设置成销号待定！");
						}
					}
					selecteddatalist = new ArrayList();
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "设置销号待定成功！");
					this.zbsearch(o);
					editor.fireModelChanged();
				} catch (ITFEBizException e) {
					log.error("查询临时表未销号记录出错！", e);
					MessageDialog.openErrorDialog(null, e);
				}
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);

			}
		}
		editor.fireModelChanged();
		return super.setFail2(o);
	}

	/**
	 * Direction: 设置失败 ename: setFail 引用方法: viewers: * messages:
	 */
	public String setFail(Object o) {
		if (null == _dto || null == _dto.getIvousrlno()) {
			MessageDialog.openMessageDialog(null, "请选择要设置销号待定的记录！");
		} else {
			if (!StateConstant.CONFIRMSTATE_YES.equals(_dto.getSstatus())) {
				DisplayCursor.setCursor(SWT.CURSOR_WAIT);
				try {
					// 设定销号待定状态
					uploadConfirmService.setFail(MsgConstant.MSG_NO_5101, _dto);
					// 判断文件下是否全部销号，如果全部销号，调用发送报文
					uploadConfirmService.checkAndSendMsg(_dto,
							MsgConstant.MSG_NO_5101,
							TbsTvPayoutDto.tableName(), _dto.getIvousrlno());
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "设置销号待定成功！");
					_dto = null;
					querylist = uploadConfirmService.eachQuery(
							BizTypeConstant.BIZ_TYPE_PAY_OUT, paysdto);
					if (null == querylist || querylist.size() == 0) {
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						MessageDialog.openMessageDialog(null, "查询无记录!");
						return null;
					}
					editor.fireModelChanged();
				} catch (ITFEBizException e) {
					log.error("查询临时表未销号记录出错！", e);
					MessageDialog.openErrorDialog(null, e);
				}
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			} else {
				MessageDialog.openMessageDialog(null, "该凭证已销号，不能设置成销号待定！");
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
		if (null == _dto || null == _dto.getIvousrlno()) {
			MessageDialog.openMessageDialog(null, "请选择要删除的记录！");
		} else {

			if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(
					this.editor.getCurrentComposite().getShell(), "提示",
					"是否确认删除该记录？")) {
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
								MsgConstant.MSG_NO_5101, TbsTvPayoutDto
										.tableName(), null);
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						MessageDialog.openMessageDialog(null, "明细删除成功！");
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
		}
		return super.delDetail(o);

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

	/**
	 * 返回需要主管授权的记录的列表
	 * 
	 * @throws ITFEBizException
	 */
	private List<TbsTvPayoutDto> needGrantList(String filename, Long ivousrlno)
			throws ITFEBizException {
		TbsTvPayoutDto _dto = new TbsTvPayoutDto();
		_dto.setSbookorgcode(loginfo.getSorgcode());
		if (null == filename && null == ivousrlno) {// 直接提交
			_dto.setSstatus(StateConstant.CONFIRMSTATE_NO);
			_dto.setSgroupid(StateConstant.COMMON_YES);
		} else if (null == filename) {// 逐笔销号
			_dto.setIvousrlno(ivousrlno);
			_dto.setSgroupid(StateConstant.COMMON_YES);
		} else if (null == ivousrlno) {// 批量销号
			_dto.setSfilename(filename);
			_dto.setSgroupid(StateConstant.COMMON_YES);
		}
		return commonDataAccessService.findRsByDto(_dto);

	}
	public List getFilelist() {
		return filelist;
	}

	public void setFilelist(List filelist) {
		this.filelist = filelist;
	}

	public int getFilecount() {
		return filecount;
	}

	public void setFilecount(int filecount) {
		this.filecount = filecount;
	}

	public int getOkcount() {
		return okcount;
	}

	public void setOkcount(int okcount) {
		this.okcount = okcount;
	}

	public int getNocount() {
		return nocount;
	}

	public void setNocount(int nocount) {
		this.nocount = nocount;
	}
	public String getSfilename() {
		return sfilename;
	}
	public void setSfilename(String sfilename) {
		this.sfilename = sfilename;
		if(sfilename!=null&&sfilename.contains("-"))
		{
			String trecode = sfilename.split("-")[0];
			String filename = sfilename.split("-")[1];
			TvFilepackagerefDto findcount = new TvFilepackagerefDto();
			findcount.setStrecode(trecode);
			findcount.setSfilename(filename);
			try {
				List<TvFilepackagerefDto> findlist = commonDataAccessService.findRsByDto(findcount);
				if(findlist!=null&&findlist.size()>0)
				{
					filecount =0;
					for(TvFilepackagerefDto tempdto:findlist)
						filecount+=tempdto.getIcount();
					TbsTvPayoutDto pdto = new TbsTvPayoutDto();
					pdto.setStrecode(trecode);
					pdto.setSfilename(filename);
					pdto.setSbookorgcode(loginfo.getSorgcode());
					pdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
					List<TbsTvPayoutDto> relist = commonDataAccessService.findRsByDto(pdto);
					nocount = relist!=null?relist.size():0;
					okcount = filecount - nocount;
				}
			} catch (ITFEBizException e) {
			}
			editor.fireModelChanged();
		}
	}
	/**
	 * Direction: 全选 ename: selectall 引用方法: viewers: * messages:
	 */
	public String pldselectall(Object o) {
		if (null == selectedfilelist || selectedfilelist.size() == 0) {
			selectedfilelist = new ArrayList();
			selectedfilelist.addAll(showfilelist);
		} else {
			selectedfilelist.clear();
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.pldselectall(o);
	}
}
package com.cfcc.itfe.client.recbiz.masspayintoforsdnxs;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.ChinaTest;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.client.dialog.BursarAffirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.PiLiangDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvBatchpayDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IUploadConfirmService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 12-02-21 15:15:51 子系统: RecBiz 模块:massPayInto 组件:MassPayInto
 */
@SuppressWarnings("unchecked")
public class MassPayIntoForSDNXSBean extends AbstractMassPayIntoForSDNXSBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(MassPayIntoForSDNXSBean.class);
	private IUploadConfirmService uploadConfirmService = (IUploadConfirmService) getService(IUploadConfirmService.class);
	private ITFELoginInfo loginfo;
	private BigDecimal sumamt;
	// 凭证总笔数
	private Integer vouCount;
	// 国库代码列表
	private List<TsTreasuryDto> treList;
	//用于区分是一般批量拨付还是山东库区移民发放
	private String type;
	
	public MassPayIntoForSDNXSBean() {
		super();
		filepath = new ArrayList();
		sumamt = null;
		vouCount = null;
		selectedfilelist = new ArrayList();
		showfilelist = new ArrayList();
		selecteddatalist = new ArrayList();
		showdatalist = new ArrayList();
		selectedftpfilelist = new ArrayList();
		selectftpreturnlist = new ArrayList();
		filedata = new FileResultDto();
		orgcodelist = new ArrayList();
		searchdto = new TvPayoutfinanceDto();
		budgetype = StateConstant.BudgetType_IN;
		sdate = TimeFacade.getCurrentStringTime();
		type = "SDNXS";//山东库区移民发放(山东农信社)
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		init();

	}

	private void init() {
		// 查询国库代码用dto
		TsTreasuryDto tredto = new TsTreasuryDto();
		// 中心机构代码
		String centerorg = null;

		try {
			centerorg = StateConstant.ORG_CENTER_CODE;
			// 中心机构，取得所有国库列表
			if (loginfo.getSorgcode().equals(centerorg)) {
				treList = commonDataAccessService.findRsByDto(tredto);
			}
			// 非中心机构，取得登录机构对应国库
			else {
				tredto = new TsTreasuryDto();
				tredto.setSorgcode(loginfo.getSorgcode());
				treList = commonDataAccessService.findRsByDto(tredto);
			}

			// 初始化国库代码默认值
			if (treList.size() > 0) {
				trecode = treList.get(0).getStrecode();
				ftptrecode = treList.get(0).getStrecode();
			}
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
			return;
		}
	}

	/**
	 * Direction: 数据加载 ename: dateload 引用方法: viewers: * messages:
	 */
	public String dateload(Object o) {
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		DisplayCursor.setCursor(SWT.CURSOR_WAIT);
		Map tmpMap = new HashMap();
		String interfacetype = ""; // 接口类型
		List<File> fileList = new ArrayList<File>();
		List<String> serverpathlist = new ArrayList<String>();
		TsBudgetsubjectDto tsBudgetsubject = new TsBudgetsubjectDto();
		tsBudgetsubject.setSorgcode(loginfo.getSorgcode());
		tsBudgetsubject.setSsubjectcode(funccode);
		try {
			List list = commonDataAccessService.findRsByDto(tsBudgetsubject);
			if (null == list || list.size() == 0) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "功能类科目代码录入有误，请核实！");
				return null;
			}
			// 判断是否选中文件
			if (null == filepath || filepath.size() == 0) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "请选择要加载的文件！");
				return null;
			}
			Set<String> sets = new HashSet<String>();
			for (int i = 0; i < filepath.size(); i++) {
				String tmpname = ((File) filepath.get(i)).getName();
				String str = tmpname.substring(tmpname.lastIndexOf("."));
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
			// 获取密匙
			tmpMap = commonDataAccessService.getMiYao(loginfo.getSorgcode());
			if (null == tmpMap || tmpMap.keySet().size() == 0) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openMessageDialog(null, "获取密钥失败！");
				return null;
			}
			// 记录txt文件验证码
			Map<String, String> yzmMap = new HashMap<String, String>();
			List<File> yzmExistFileList = new ArrayList<File>(); // 记录验证码已经存在的文件
			String yzmExistPromptMsg = "";// 记录验证码存在提示信息

			// 数据加载
			for (int i = 0; i < filepath.size(); i++) {
				File tmpfile = (File) filepath.get(i);
				String tmpfilename = tmpfile.getName().toLowerCase(); // 文件的名字
				String tmpfilepath = tmpfile.getAbsolutePath(); // 获取文件的路径

				if (null == tmpfile || null == tmpfilename
						|| null == tmpfilepath) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, " 请选择要加载的文件！");
					return null;
				}
				// 解析文件名字
				interfacetype = getFileObjByFileNameBiz(
						BizTypeConstant.BIZ_TYPE_BATCH_OUT, tmpfilename);
				// 判断文件是否已经解析
				if (commonDataAccessService.verifyImportRepeat(tmpfilename)) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, tmpfilename
							+ "已经导入，请确认！");
					// 删除服务器上传失败文件
					DeleteServerFileUtil.delFile(commonDataAccessService,
							serverpathlist);
					return null;
				}
				// 文件分为pas文件和txt文件
				if (tmpfilename.endsWith(".pas")) {
					if (ChinaTest.isChinese(tmpfilepath)) {
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						MessageDialog.openMessageDialog(null, "文件路径有汉字，请更改路径！");
						// 删除服务器上传失败文件
						DeleteServerFileUtil.delFile(commonDataAccessService,
								serverpathlist);
						return null;
					}
					String miyao = (String) tmpMap.get(tmpfilename.substring(
							tmpfilename.length() - 16, tmpfilename
									.lastIndexOf(".")));
					if (null != miyao && miyao.length() > 0) {
						// 密钥解密
						miyao = commonDataAccessService.decrypt(miyao);
						if ("-1".equals(TipsFileDecrypt.decryptPassFile(
								tmpfilepath, tmpfilepath
										.replace(".pas", ".tmp"), miyao))) {
							DisplayCursor.setCursor(SWT.CURSOR_ARROW);
							MessageDialog.openMessageDialog(null, tmpfilename
									+ "文件解密失败！");
							// 删除服务器上传失败文件
							DeleteServerFileUtil.delFile(
									commonDataAccessService, serverpathlist);
							return null;
						}
						// 上传原文件并记录日志
						String serverpath = ClientFileTransferUtil
								.uploadFile(tmpfile.getAbsolutePath());
						// 上传解析成功文件
						fileList.add(new File(serverpath));
						serverpathlist.add(serverpath);
						// 删除临时文件
						FileUtil.getInstance().deleteFile(
								tmpfilepath.replace(".pas", ".tmp"));
						continue;
					} else {
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						MessageDialog.openMessageDialog(null, "密钥没有维护，请先维护密钥！");
						// 删除服务器上传失败文件
						DeleteServerFileUtil.delFile(commonDataAccessService,
								serverpathlist);
						return null;
					}
				} else {
					String outorg = "";
					if (soutorgcode != null && !"".equals(soutorgcode)) {
						outorg = soutorgcode;
					} else {
						outorg = "aaaaaaaaaaaa";
					}
					String miyao = (String) tmpMap.get(outorg);
					if (null != miyao && miyao.length() > 0) {
						// 密钥解密
						miyao = commonDataAccessService.decrypt(miyao);
						String yzm = TipsFileDecrypt.checkSignFile(tmpfilepath,
								tmpfilepath.replaceAll(".txt", ".tmp"), miyao);
						if ("-1".equals(yzm)) {
							DisplayCursor.setCursor(SWT.CURSOR_ARROW);
							MessageDialog.openMessageDialog(null, tmpfilename
									+ "文件解密失败！");
							// 删除服务器上传失败文件
							DeleteServerFileUtil.delFile(
									commonDataAccessService, serverpathlist);
							return null;
						}
						TvRecvlogDto recvlogDto = new TvRecvlogDto();
						recvlogDto.setSbatch(yzm);
						List<TvRecvlogDto> list2 = commonDataAccessService
								.findRsByDto(recvlogDto);
						if (list2.size() > 0) {
							// 验证码存在，不处理上传等操作，但是需要记录已经存在的验证码
							yzmExistFileList.add(tmpfile);
							yzmExistPromptMsg += tmpfilename + "\r\n";
						}
						// 上传原文件并记录日志
						String serverpath = ClientFileTransferUtil
								.uploadFile(tmpfile.getAbsolutePath());
						// 上传解析成功文件
						fileList.add(new File(serverpath));
						serverpathlist.add(serverpath);
						yzmMap.put(yzm, serverpath);
						// 删除临时文件
						FileUtil.getInstance().deleteFile(
								tmpfilepath.replace(".txt", ".tmp"));
						continue;
					} else {
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						MessageDialog.openMessageDialog(null, "密钥没有维护，请先维护密钥！");
						// 删除服务器上传失败文件
						DeleteServerFileUtil.delFile(commonDataAccessService,
								serverpathlist);
						return null;
					}

				}

			}
			// 如果验证码已经存在，则提示用户是否导入
			if (yzmExistFileList.size() > 0) {

				// 打开授权窗口
				String msg = "服务器存在下列文件相同的验证码，授权后才能导入\r\n" + yzmExistPromptMsg;

				if (!BursarAffirmDialogFacade.open(msg)) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, "授权不成功，不能导入！");
					// 删除服务器上传失败文件
					DeleteServerFileUtil.delFile(commonDataAccessService,
							serverpathlist);
					return null;
				}
				;
			}

			// 服务器加载文件
			fileResolveCommonService.loadFile(fileList,
					BizTypeConstant.BIZ_TYPE_BATCH_OUT, interfacetype,
					new PiLiangDto(trecode, budgetype, funccode, sbdgorgcode, type));
			Iterator<String> iterators = sets.iterator();
			if (".txt".equals(iterators.next())) {
				commonDataAccessService.saveMassTvrecvlog(yzmMap,
						BizTypeConstant.BIZ_TYPE_BATCH_OUT);
			} else {
				commonDataAccessService.saveTvrecvlog(serverpathlist,
						BizTypeConstant.BIZ_TYPE_BATCH_OUT);
			}
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openMessageDialog(null, "文件加载成功,本次共加载成功 "
					+ fileList.size() + " 个文件！");
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
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			if (commonDataAccessService.judgeDirectSubmit(
					BizTypeConstant.BIZ_TYPE_BATCH_OUT, loginfo.getSorgcode())) {
				int result = uploadConfirmService
						.directSubmit(BizTypeConstant.BIZ_TYPE_BATCH_OUT);
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
				MessageDialog.openMessageDialog(null, "不能直接提交，请与管理员联系！");
				return null;
			}
		} catch (ITFEBizException e) {
			log.error("直接提交失败！", e);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			return null;
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
		if (null == selectedfilelist || selectedfilelist.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择一条记录！");
			return null;
		}
		try {
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
						+ sumamt.toString() + "]、凭证总笔数[" + vouCount.toString()
						+ "]与销号文件的总金额["+selSumamt+"]、凭证总笔数["+selvouCount+"]不符,请查证!");
				return null;
			} else {
				DisplayCursor.setCursor(SWT.CURSOR_WAIT);
				massdispose();
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			}
		} catch (ITFEBizException e) {
			log.error("批量提交失败！", e);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
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
				DisplayCursor.setCursor(SWT.CURSOR_WAIT);
				for (int i = 0; i < selectedfilelist.size(); i++) {
					TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
							.get(i);
					int result = uploadConfirmService.batchDelete(
							BizTypeConstant.BIZ_TYPE_BATCH_OUT, dto);
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
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否确认提交？")) {
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			try {
				for (int i = 0; i < selecteddatalist.size(); i++) {
					TvPayoutfinanceDto dto = (TvPayoutfinanceDto) selecteddatalist
							.get(i);
					int result = uploadConfirmService.eachConfirm(
							BizTypeConstant.BIZ_TYPE_BATCH_OUT, dto);
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
				log.error("批量提交失败！", e);
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
	// BizTypeConstant.BIZ_TYPE_BATCH_OUT, sumamt);
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
		DisplayCursor.setCursor(SWT.CURSOR_WAIT);
		selecteddatalist = new ArrayList();
		showdatalist = new ArrayList();
		try {
			// 核算主体代码
			searchdto.setSbookorgcode(loginfo.getSorgcode());
			// 未销号
			searchdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
			showdatalist = uploadConfirmService.eachQuery(
					BizTypeConstant.BIZ_TYPE_BATCH_OUT, searchdto);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		} catch (ITFEBizException e) {
			log.error("逐笔销号查询失败！", e);
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
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

	/**
	 * Direction: 批量全选 ename: plselectall 引用方法: viewers: * messages:
	 */
	public String plselectall(Object o) {
		if (null == selectedfilelist || selectedfilelist.size() == 0) {
			selectedfilelist = new ArrayList();
			selectedfilelist.addAll(showfilelist);
		} else {
			selectedfilelist.clear();
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.plselectall(o);
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
	private String getFileObjByFileNameBiz(String biztype, String filename)
			throws ITFEBizException {
		String interficetype = "";
		String tmpfilename_new = filename.trim().toLowerCase();
		if (tmpfilename_new.indexOf(".pas") > 0) {
			// 接收清算行行号（12位）＋日期(8位)＋当日文件编号（10位）＋核算主体(12位)“.pas
			String tmpfilename = tmpfilename_new.replace(".pas", "");
			if (tmpfilename.length() == 42) {
				if (!commonDataAccessService.auditBankCode(tmpfilename
						.substring(0, 12)))
					throw new ITFEBizException("清算行行号不存在!");
				interficetype = MsgConstant.PILIANG_DAORU;// TBS接口数据
			} else {
				throw new ITFEBizException("文件名格式不符!");
			}
		} else if (tmpfilename_new.indexOf(".txt") > 0) {
			String tmpfilename = tmpfilename_new.replace(".txt", "");
			if (tmpfilename.length() == 30) {
				if (!commonDataAccessService.auditBankCode(tmpfilename
						.substring(0, 12)))
					throw new ITFEBizException("清算行行号不存在!");
				interficetype = MsgConstant.PILIANG_DAORU;// TBS接口数据
			} else {
				throw new ITFEBizException("文件名格式不符!");
			}
		} else {
			throw new ITFEBizException("文件名格式不符!");
		}
		return interficetype;
	}

	public List<TsTreasuryDto> getTreList() {
		return treList;
	}

	public void setTreList(List<TsTreasuryDto> treList) {
		this.treList = treList;
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
//		DisplayCursor.setCursor(SWT.);
		if(null == vouCount || null == sumamt){
			MessageDialog.openMessageDialog(null, "凭证总数和总金额不能为空！");
			return ;
		}
		this.sumamt = sumamt;
		if (sumamt == null || sumamt.compareTo(new BigDecimal(0)) == 0) {
		} else {
			try {
				TvFilepackagerefDto refdto = new TvFilepackagerefDto();
				BigDecimal totalfamt = new BigDecimal("0.00");
				int totalnum = 0;
				showfilelist = new ArrayList();
				selectedfilelist = new ArrayList();
				showfilelist = uploadConfirmService.batchQuery(
						BizTypeConstant.BIZ_TYPE_BATCH_OUT, sumamt);
				if (null != showfilelist) {
					for(int i = 0; i <  showfilelist.size(); i++){
						refdto = (TvFilepackagerefDto) showfilelist.get(i);
						totalfamt= totalfamt.add(refdto.getNmoney());
						totalnum=totalnum+refdto.getIcount();
					}
					if(totalfamt.compareTo(sumamt) != 0 || vouCount != totalnum){
						MessageDialog.openMessageDialog(null, "输入的总笔数 ["+vouCount+"]、总金额["
								+ sumamt + "]与未销号凭证的总笔数与总金额数据不一致\n"
								+ "明细提示:所有未销号凭证总笔数为["+totalnum+"]总金额为[" + totalfamt
								+ "],共计 [" + showfilelist.size() + "]个文件");
					}
					selectedfilelist.addAll(showfilelist);
					this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
					this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				}else if (showfilelist == null || showfilelist.size() <= 0) {
					MessageDialog.openMessageDialog(null, "没有符合条件的数据！");
				}
			} catch (Throwable e) {
				log.error("操作失败！", e);
				MessageDialog.openErrorDialog(null, e);
			}
		}
	}
	/**
	 * Direction: 读取ftp文件
	 * ename: readftp
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String readftp(Object o){
    	if(sdate==null||"".equals(sdate))
    	{
    		MessageDialog.openMessageDialog(null, "账务日期不可为空！");
    		return null;
    	}
    	if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "确认读取Ftp服务器上日期为"+sdate+"的数据？")) {
	    	try{
	    		 uploadConfirmService.readFtpFile(sdate);
	    		 if(ftptrecode!=null&&!"".equals(ftptrecode))
	    	     {
		    		 TvBatchpayDto queryDto = new TvBatchpayDto();
		    		 queryDto.setSorgcode(loginfo.getSorgcode());
		    	     queryDto.setSdate(sdate);
		    	     if(sstatus==null||"".equals(sstatus))
			   	    	 sstatus = StateConstant.FTPFILESTATE_DOWNLOAD;
			   	     queryDto.setSstatus(sstatus);
			   	     queryDto.setStrecode(ftptrecode);
		    	     this.showftpfilelist = commonDataAccessService.findRsByDto(queryDto);
	    	     }
	    	     MessageDialog.openMessageDialog(null, "读取Ftp服务器数据完成。");
	    	     this.selectedftpfilelist = new ArrayList();
	    	     this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	    	} catch (ITFEBizException e) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				if(e.getCause().getMessage().indexOf("数据已经读取")>=0)
					MessageDialog.openMessageDialog(null, "Ftp服务器数据已经读取，无新数据。");
				else
					MessageDialog.openMessageDialog(null, "Ftp服务器上没有数据。");
				return null;
			}
    	}else {
			return null;
		}
        return super.readftp(o);
    }
    /**
	 * Direction: ftp汇总文件下载
	 * ename: ftpsummarydown
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String ftpsummarydown(Object o){
    	if(selectedftpfilelist!=null&&selectedftpfilelist.size()>0)
    	{
    		TvBatchpayDto dto = null;
    		for (int i = 0; i < selectedftpfilelist.size(); i++) {
				dto = (TvBatchpayDto)selectedftpfilelist.get(i);
				if(!dto.getSstatus().equals(StateConstant.FTPFILESTATE_SUMMARY))
				{
					MessageDialog.openMessageDialog(null, "选中的文件列表中状态必须为汇总文件！");
					return null;
				}
    		}
    		DirectoryDialog path = new DirectoryDialog(Display.getCurrent().getActiveShell());
    		String strPath = path.open();
    		if ((null == strPath) || (strPath.length() == 0)) {
    			MessageDialog.openMessageDialog(null, "请选择下载文件保存路径。");
    			return "";
    		}
    		// 下载所有附件
    		try {
    			uploadConfirmService.ftpfileadd(selectedftpfilelist);
        		for (int i = 0; i < selectedftpfilelist.size(); i++) {
    				dto = (TvBatchpayDto)selectedftpfilelist.get(i);
    				// 将文件下载到下载目录中
    				ClientFileTransferUtil.downloadFile("ITFEDATA"+"/"+loginfo.getSorgcode()+"/"+TimeFacade.getCurrentStringTime()+"/"+dto.getSfilename(), strPath + "/"
    						+ dto.getSfilename());
    			}
    			MessageDialog.openMessageDialog(null, "附件已经保存到路径" + strPath + "中。");
    		} catch (Throwable e) {
    			MessageDialog.openErrorDialog(null, e);
    		}
    	}else {
    		MessageDialog.openMessageDialog(null, "请选择需要下载的汇总文件！");
    		return null;
		}
        return super.ftpsummarydown(o);
    }
    /**
	 * Direction: 跳转ftp文件列表
	 * ename: goftpfilelist
	 * 引用方法: 
	 * viewers: ftp批量文件
	 * messages: 
	 */
    public String goftpfilelist(Object o){
    	sstatus = StateConstant.FTPFILESTATE_DOWNLOAD;
        return super.goftpfilelist(o);
    }
    /**
	 * Direction: 查询读取的ftp数据
	 * ename: queryftpdata
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String queryftpdata(Object o){
    	if(sdate==null||"".equals(sdate))
    	{
    		MessageDialog.openMessageDialog(null, "账务日期不可为空！");
    		return null;
    	}
    	if(ftptrecode==null||"".equals(ftptrecode))
    	{
    		MessageDialog.openMessageDialog(null, "国库代码不可为空！");
    		return null;
    	}
    	if(sstatus==null||"".equals(sstatus))
    	{
    		MessageDialog.openMessageDialog(null, "状态不可为空！");
    		return null;
    	}
    	try{
	   		 TvBatchpayDto queryDto = new TvBatchpayDto();
	   		 queryDto.setSorgcode(loginfo.getSorgcode());
	   	     queryDto.setSdate(sdate);
	   	     queryDto.setStrecode(ftptrecode);
	   	     if(sstatus==null||"".equals(sstatus))
	   	    	 sstatus = StateConstant.FTPFILESTATE_DOWNLOAD;
	   	     queryDto.setSstatus(sstatus);
	   	     this.showftpfilelist = commonDataAccessService.findRsByDto(queryDto);
	   	     if(showftpfilelist==null||showftpfilelist.size()<=0)
	   	    	MessageDialog.openMessageDialog(null, "查询数据结果为空！");
	   	  selectedftpfilelist = new ArrayList();
	   	  this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
    	} catch (ITFEBizException e) {
			 DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			 MessageDialog.openErrorDialog(null, e);
			
			return null;
		}
    	return super.queryftpdata(o);
    }
    /**
	 * Direction: ftp文件加载
	 * ename: ftpfileadd
	 * 引用方法: 
	 * viewers: ftp批量文件
	 * messages: 
	 */
    public String ftpfileadd(Object o){
    	if(selectedftpfilelist!=null&&selectedftpfilelist.size()>0)
    	{
    		TvBatchpayDto dto = null;
    		Set<String> sets = new HashSet<String>();
    		for (int i = 0; i < selectedftpfilelist.size(); i++) {
				dto = (TvBatchpayDto)selectedftpfilelist.get(i);
				if(!dto.getSstatus().equals(StateConstant.FTPFILESTATE_DOWNLOAD))
				{
					MessageDialog.openMessageDialog(null, "选中的文件列表中状态必须为已读取！");
					return null;
				}
				String tmpname = dto.getSfilename();
				String str = tmpname.substring(tmpname.lastIndexOf("."));
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
    		if(!dto.getStrecode().equals(trecode))
			{
				if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
						.getCurrentComposite().getShell(), "提示", "文件名中的国库和补录所选国库不一致，确认提交吗？")) {
					
				}else
					return null;
			}
    		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
    		DisplayCursor.setCursor(SWT.CURSOR_WAIT);
    		Map tmpMap = new HashMap();
    		String interfacetype = ""; // 接口类型
    		List<File> fileList = new ArrayList<File>();
    		List<String> serverpathlist = new ArrayList<String>();
    		TsBudgetsubjectDto tsBudgetsubject = new TsBudgetsubjectDto();
    		tsBudgetsubject.setSorgcode(loginfo.getSorgcode());
    		tsBudgetsubject.setSsubjectcode(funccode);
    		try {
    			List list = commonDataAccessService.findRsByDto(tsBudgetsubject);
    			if (null == list || list.size() == 0) {
    				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    				MessageDialog.openMessageDialog(null, "功能类科目代码录入有误，请核实！");
    				return null;
    			}
    			// 判断是否选中文件
    			if (null == selectedftpfilelist || selectedftpfilelist.size() == 0) {
    				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    				MessageDialog.openMessageDialog(null, "请选择要加载的文件！");
    				return null;
    			}
    			// 获取密匙
    			tmpMap = commonDataAccessService.getMiYao(loginfo.getSorgcode());
    			if (null == tmpMap || tmpMap.keySet().size() == 0) {
    				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    				MessageDialog.openMessageDialog(null, "获取密钥失败！");
    				return null;
    			}
    			// 记录txt文件验证码
    			Map<String, String> yzmMap = new HashMap<String, String>();
    			List<File> yzmExistFileList = new ArrayList<File>(); // 记录验证码已经存在的文件
    			String yzmExistPromptMsg = "";// 记录验证码存在提示信息
    			Map<String,TvBatchpayDto> idtoMap = new HashMap<String,TvBatchpayDto>();
    			// 数据加载
    			File tempFile = null;
    			for (int i = 0; i < selectedftpfilelist.size(); i++) {
    				dto = (TvBatchpayDto)selectedftpfilelist.get(i);
    				String tmpfilename = dto.getSfilename(); // 文件的名字
    				String tmpfilepath = dto.getSfilepath(); // 获取文件的路径
    				// 解析文件名字
    				interfacetype = getFileObjByFileNameBiz(
    						BizTypeConstant.BIZ_TYPE_BATCH_OUT, tmpfilename);
    				// 判断文件是否已经解析
    				if (commonDataAccessService.verifyImportRepeat(tmpfilename)) {
    					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    					MessageDialog.openMessageDialog(null, tmpfilename
    							+ "已经导入，请确认！");
    					// 删除服务器上传失败文件
    					DeleteServerFileUtil.delFile(commonDataAccessService,
    							serverpathlist);
    					return null;
    				}
    				// 文件分为pas文件和txt文件
    				if (tmpfilename.endsWith(".txt")) {
    					String outorg = "";
    					if (soutorgcode != null && !"".equals(soutorgcode)) {
    						outorg = soutorgcode;
    					} else {
    						outorg = "aaaaaaaaaaaa";
    					}
    					String miyao = (String) tmpMap.get(outorg);
    					if (null != miyao && miyao.length() > 0) {
    						// 密钥解密
    						miyao = commonDataAccessService.decrypt(miyao);
    						String yzm =null;
    						dto.setSkey(miyao);//验签密钥回执用
    						Map getMap =uploadConfirmService.checkSignFileForSd(tmpfilepath+tmpfilename, tmpfilepath+tmpfilename.replaceAll(".txt", ".tmp"), miyao);
    						yzm = String.valueOf(getMap.get("yzm"));
    						if(getMap.get("newfilename")!=null)
    						{
    							serverpathlist.add(String.valueOf(getMap.get("newfilename")));
    							tempFile = new File(String.valueOf(getMap.get("newfilename")));
    							fileList.add(tempFile);
    							idtoMap.put(tempFile.getName(), dto);
    						}
    						if ("-1".equals(yzm)) {
    							DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    							MessageDialog.openMessageDialog(null, tmpfilename+ "文件解密失败！");
    							// 删除服务器上传失败文件
    							DeleteServerFileUtil.delFile(commonDataAccessService, serverpathlist);
    							return null;
    						}
    						TvRecvlogDto recvlogDto = new TvRecvlogDto();
    						recvlogDto.setSbatch(yzm);
    						List<TvRecvlogDto> list2 = commonDataAccessService.findRsByDto(recvlogDto);
    						if (list2.size() > 0) {
    							// 验证码存在，不处理上传等操作，但是需要记录已经存在的验证码
    							yzmExistFileList.add(new File(tmpfilename));
    							yzmExistPromptMsg += tmpfilename + "\r\n";
    						}
    						// 上传原文件并记录日志
    						if(serverpathlist!=null&&serverpathlist.size()==1)
    							yzmMap.put(yzm, serverpathlist.get(0));
    						else
    							yzmMap.put(yzm, serverpathlist.get(serverpathlist.size()-1));
    						continue;
    					} else {
    						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    						MessageDialog.openMessageDialog(null, "密钥没有维护，请先维护密钥！");
    						// 删除服务器上传失败文件
    						DeleteServerFileUtil.delFile(commonDataAccessService,
    								serverpathlist);
    						return null;
    					}
    				}
    			}
    			// 如果验证码已经存在，则提示用户是否导入
    			if (yzmExistFileList.size() > 0) {

    				// 打开授权窗口
    				String msg = "服务器存在下列文件相同的验证码，授权后才能导入\r\n" + yzmExistPromptMsg;

    				if (!BursarAffirmDialogFacade.open(msg)) {
    					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    					MessageDialog.openMessageDialog(null, "授权不成功，不能导入！");
    					// 删除服务器上传失败文件
    					DeleteServerFileUtil.delFile(commonDataAccessService,
    							serverpathlist);
    					return null;
    				}
    				;
    			}
    			// 服务器加载文件
    			fileResolveCommonService.loadFile(fileList,
    					BizTypeConstant.BIZ_TYPE_BATCH_OUT, interfacetype,
    					new PiLiangDto(trecode, budgetype, funccode, sbdgorgcode, "ftppljz",idtoMap));
    			Iterator<String> iterators = sets.iterator();
    			if (".txt".equals(iterators.next())) {
    				commonDataAccessService.saveMassTvrecvlog(yzmMap,
    						BizTypeConstant.BIZ_TYPE_BATCH_OUT);
    			} else {
    				commonDataAccessService.saveTvrecvlog(serverpathlist,
    						BizTypeConstant.BIZ_TYPE_BATCH_OUT);
    			}
    			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    			MessageDialog.openMessageDialog(null, "文件加载成功,本次共加载成功 "
    					+ fileList.size() + " 个文件！");
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
    			queryftpdata(o);
        		this.editor.fireModelChanged();
    			return null;
    		}
    		filepath = new ArrayList();
    		queryftpdata(o);
    		this.editor.fireModelChanged();
    	}else {
    		MessageDialog.openMessageDialog(null, "请选择需要加载的文件！");
    		return null;
		}
        return super.ftpfileadd(o);
    }
    
	/**
	 * Direction: ftp文件全选
	 * ename: ftpfileselectall
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String ftpfileselectall(Object o){
    	if (null == selectedftpfilelist || selectedftpfilelist.size() == 0) {
			selectedftpfilelist = new ArrayList();
			selectedftpfilelist.addAll(showftpfilelist);
		} else {
			selectedftpfilelist.clear();
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.ftpfileselectall(o);
    }
    /**
	 * Direction: 全选/反选
	 * ename: selectallreturn
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectallreturn(Object o){
        if(null==this.selectftpreturnlist||this.selectftpreturnlist.size()==0)
        {
        	this.selectftpreturnlist = new ArrayList();
        	this.selectftpreturnlist.addAll(this.showftpreturnlist);
        }
        else
        	this.selectftpreturnlist.clear();
        this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.selectallreturn(o);
    }
    /**
	 * Direction: 跳转ftp回执列表
	 * ename: goftpreturn
	 * 引用方法: 
	 * viewers: ftp文件回执
	 * messages: 
	 */
    public String goftpreturn(Object o){
//    	sdate = TimeFacade.getCurrentStringTime();
//    	queryftpreturn(o);
        return super.goftpreturn(o);
    }
    
	/**
	 * Direction: 查询ftp回执列表
	 * ename: queryftpreturn
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String queryftpreturn(Object o){
    	TvBatchpayDto querydto = new TvBatchpayDto();
    	if(sdate==null||"".equals(sdate))
    		sdate = TimeFacade.getCurrentStringTime();
    	querydto.setSdate(sdate);
    	if(ftptrecode!=null&&!"".equals(ftptrecode))
    		querydto.setStrecode(ftptrecode);
    	else
    	{
    		MessageDialog.openMessageDialog(null, "国库代码不可为空！");
    		return null;
    	}
    	try {
			this.showftpreturnlist = uploadConfirmService.queryFtpReturnFileList(querydto);
			if(this.showftpreturnlist==null||this.showftpreturnlist.size()<=0)
				MessageDialog.openMessageDialog(null, "查询数据结果为空！");
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "查询异常，原因："+e.toString());
		}
    	this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.queryftpreturn(o);
    }
    
	/**
	 * Direction: ftp文件回执发送
	 * ename: ftpreturnsend
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String ftpreturnsend(Object o){
    	if(this.selectftpreturnlist==null||this.selectftpreturnlist.size()<=0)
    	{
    		MessageDialog.openMessageDialog(null, "请选择需要回执的文件列表！");
    		return null;
    	}
    	try {
			uploadConfirmService.sendReturnToFtp(this.selectftpreturnlist);
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "回执异常，原因："+e.toString());
		}
		MessageDialog.openMessageDialog(null, "回执成功！");
    	queryftpreturn(o);
    	this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.ftpreturnsend(o);
    }
	private void massdispose() throws ITFEBizException {

		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否确认提交？")) {
			for (int i = 0; i < selectedfilelist.size(); i++) {
				TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
						.get(i);
				int result = uploadConfirmService.batchConfirm(
						BizTypeConstant.BIZ_TYPE_BATCH_OUT, dto);
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
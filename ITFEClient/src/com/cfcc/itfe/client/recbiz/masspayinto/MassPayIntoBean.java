package com.cfcc.itfe.client.recbiz.masspayinto;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;

import com.cfcc.deptone.common.util.DateUtil;
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
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IUploadConfirmService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
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
public class MassPayIntoBean extends AbstractMassPayIntoBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(MassPayIntoBean.class);
	private IUploadConfirmService uploadConfirmService = (IUploadConfirmService) getService(IUploadConfirmService.class);
	private ITFELoginInfo loginfo;
	private BigDecimal sumamt;
	// 凭证总笔数
	private Integer vouCount;
	// 国库代码列表
	private List<TsTreasuryDto> treList;
	private String ftpFilePath;
	private String filemainpath="/itfe/dsftp/";
	private List ftpList;
	private List selectFtpList;
	private List ftpchecklist;
	private List ftpfilelist;
	private List ftpfilepath;
	public MassPayIntoBean() {
		super();
		filepath = new ArrayList();
		sumamt = null;
		vouCount = null;
		selectedfilelist = new ArrayList();
		showfilelist = new ArrayList();
		selecteddatalist = new ArrayList();
		showdatalist = new ArrayList();
		filedata = new FileResultDto();
		orgcodelist = new ArrayList();
		searchdto = new TvPayoutfinanceDto();
		budgetype = StateConstant.BudgetType_IN;
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		ftpFilePath = "/itfe/dsftp/";
		selectFtpList = new ArrayList();
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
			}
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
			return;
		}
	}

	private void getServerFileInfo() {
		try {
			ftpList = uploadConfirmService.getDirectGrantFileList(ftpFilePath,
					BizTypeConstant.BIZ_TYPE_BATCH_OUT);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "获取文件列表失败！");
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
						fileList.add(new File(ClientFileTransferUtil
								.uploadFile(tmpfile.getAbsolutePath().replace(
										".pas", ".tmp"))));
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
					new PiLiangDto(trecode, budgetype, funccode, sbdgorgcode));
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

	/**
	 * Direction: 服务器文件加载全选 ename: ftpSeclectAll 引用方法: viewers: * messages:
	 */
	public String ftpSeclectAll(Object o) {
		if (null == selectFtpList || selectFtpList.size() == 0) {
			selectFtpList = new ArrayList();
			selectFtpList.addAll(ftpList);
		} else {
			selectFtpList = new ArrayList();
		}
		this.editor.fireModelChanged();
		return super.ftpSeclectAll(o);
	}

	/**
	 * Direction: 服务器文件加载删除功能 ename: ftpDelFile 引用方法: viewers: * messages:
	 */
	public String ftpDelFile(Object o) {
		if (null == selectFtpList || selectFtpList.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择需要删除的文件！");
			return null;
		}
		List<String> delServerFile = new ArrayList<String>();
		for (int i = 0; i < selectFtpList.size(); i++) {
			ftpList.remove(selectFtpList.get(i));
			delServerFile.add(String.valueOf(((Map) selectFtpList.get(i))
					.get("filepath")));
		}
		selectFtpList.clear();
		try {
			DeleteServerFileUtil
					.delFile(commonDataAccessService, delServerFile);
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "删除服务器文件失败！");
			return null;
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return "";
	}

	/**
	 * Direction: 服务器文件加载 文件加载功能 ename: ftpFileLoad 引用方法: viewers: * messages:
	 */
	public String ftpFileLoad(Object o) {
		List<String> delServerFile = new ArrayList<String>(); // 记录服务器需要删除的文件
		if (null == selectFtpList || selectFtpList.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择需要加载的文件！");
			return null;
		}
		String dirsep = File.separator; // 取得系统分割符
		String clientpath = "c:" + dirsep + "client" + dirsep + "20" + dirsep
				+ DateUtil.date2String2(new Date(System.currentTimeMillis()))
				+ dirsep + loginfo.getSorgcode() + dirsep;
		File dir = new File(clientpath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try {
			filepath = new ArrayList();
			for (int i = 0; i < selectFtpList.size(); i++) {
				String clientfile = clientpath
						+ new File(String.valueOf(((Map) selectFtpList.get(i))
								.get("filepath"))).getName();
				ClientFileTransferUtil.downloadFile(String
						.valueOf(((Map) selectFtpList.get(i)).get("filepath")),
						clientfile);
				ftpList.remove(selectFtpList.get(i));
				filepath.add(new File(clientfile));
				delServerFile.add(String.valueOf(((Map) selectFtpList.get(i))
						.get("filepath")));
			}
			selectFtpList.clear();
			DeleteServerFileUtil
					.delFile(commonDataAccessService, delServerFile);
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
			MessageDialog.openMessageDialog(null, "文件保存在[" + clientpath
					+ "]目录下");
		} catch (FileTransferException e) {
			MessageDialog.openMessageDialog(null, "下载文件失败！");
			return null;
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "下载文件失败！");
			return null;
		}

		return super.ftpFileLoad(o);
	}

	/**
	 * Direction: 服务器目录加载 ename: goServiceDirAdd 引用方法: viewers: 服务器目录批量导入
	 * messages:
	 */
	public String goServiceDirAdd(Object o) {
		selectFtpList = new ArrayList();
		getServerFileInfo();
		return super.goServiceDirAdd(o);
	}

	/**
	 * Direction: 刷新服务器目录内容 ename: refreshServerDir 引用方法: viewers: * messages:
	 */
	public String refreshServerDir(Object o) {
		getServerFileInfo();
		this.editor.fireModelChanged();
		return super.refreshServerDir(o);
	}

	/**
	 * Direction: 服务器文件加载 返回功能 ename: ftpGoMainView 引用方法: viewers: 批量拨付数据导入
	 * messages:
	 */
	public String ftpGoMainView(Object o) {
		selectFtpList = new ArrayList();
		this.editor.fireModelChanged();
		return super.ftpGoMainView(o);
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

	public String getFtpFilePath() {
		return ftpFilePath;
	}

	public void setFtpFilePath(String ftpFilePath) {
		this.ftpFilePath = ftpFilePath;
	}

	public List getFtpList() {
		return ftpList;
	}

	public void setFtpList(List ftpList) {
		this.ftpList = ftpList;
	}

	public List getSelectFtpList() {
		return selectFtpList;
	}

	public void setSelectFtpList(List selectFtpList) {
		this.selectFtpList = selectFtpList;
	}

	public List getFtpchecklist() {
		return ftpchecklist;
	}

	public void setFtpchecklist(List ftpchecklist) {
		this.ftpchecklist = ftpchecklist;
	}

	public List getFtpfilelist() {
		return ftpfilelist;
	}

	public void setFtpfilelist(List ftpfilelist) {
		this.ftpfilelist = ftpfilelist;
	}

	public List getFtpfilepath() {
		return ftpfilepath;
	}

	public void setFtpfilepath(List ftpfilepath) {
		this.ftpfilepath = ftpfilepath;
	}
	/**
	 * Direction: 跳转ftp文件管理
	 * ename: goftpfilemanager
	 * 引用方法: 
	 * viewers: ftp文件管理
	 * messages: 
	 */
    public String goftpfilemanager(Object o){
        this.ftpchecklist = new ArrayList();
        this.ftpfilepath = new ArrayList();
        try {
			ftpfilelist = uploadConfirmService.getDirectGrantFileList(filemainpath+"swap/", "ftp");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "获取文件列表失败！");
		}
        return super.goftpfilemanager(o);
    }
    
	/**
	 * Direction: ftp全选反选
	 * ename: ftpselectall
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String ftpselectall(Object o){
        if(ftpchecklist!=null&&ftpchecklist.size()>0)
        	ftpchecklist = new ArrayList();
        else
        	ftpchecklist = ftpfilelist;
        this.editor.fireModelChanged();
        return super.ftpselectall(o);
    }
    
	/**
	 * Direction: ftp下载
	 * ename: ftpdownload
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String ftpdownload(Object o){
    	if (ftpchecklist == null||ftpchecklist.size()<=0) {
			MessageDialog.openMessageDialog(null, "请选中要下载的记录!");
			return super.ftpdownload(o);
		}

		// 客户端下载报文的路径
		String dirsep = File.separator; // 取得系统分割符
		dirsep = "/";
		String clientpath = "c:" + dirsep + "client" + dirsep + filemainpath +TimeFacade.getCurrentStringTimebefor() + dirsep;
		File dir = new File(clientpath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		List<String> servicefilelist = null;
		if (ftpchecklist != null && ftpchecklist.size() > 0) {
			servicefilelist = new ArrayList<String>();
			Map fileMap = null;
			try {
				for(int i=0;i<ftpchecklist.size();i++)
				{
					fileMap = (Map)ftpchecklist.get(i);
					servicefilelist.addAll(uploadConfirmService.getDirectGrantFileList(String.valueOf(fileMap.get("filepath")),"ftpdownload"));
				}
				for(int i=0;i<servicefilelist.size();i++)
				{
					String clientfile = clientpath+ servicefilelist.get(i).substring(servicefilelist.get(i).lastIndexOf(dirsep)+1);
					ClientFileTransferUtil.downloadFile(servicefilelist.get(i),clientfile);
				}
				DeleteServerFileUtil.delFile(commonDataAccessService,servicefilelist);
				MessageDialog.openMessageDialog(null, "Ftp文件下载成功！\n路径:"+clientpath );
			} catch (Exception e) {
				log.error("Ftp文件下载失败:", e);
				MessageDialog.openErrorDialog(null, e);
			}
		}
        return super.ftpdownload(o);
    }
    
	/**
	 * Direction: ftp删除
	 * ename: ftpdeletefile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String ftpdeletefile(Object o){
    	if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否确认删除选中文件?")) {
	    	List<String> servicefilelist = null;
			try {
				if (ftpchecklist != null && ftpchecklist.size() > 0) {
					servicefilelist = new ArrayList<String>();
					Map fileMap = null;
					for(int i=0;i<ftpchecklist.size();i++)
					{
						fileMap = (Map)ftpchecklist.get(i);
						servicefilelist.add(String.valueOf(fileMap.get("filepath")));
					}
					uploadConfirmService.delfilelist(servicefilelist,"2");
					MessageDialog.openMessageDialog(null, "删除文件成功！");
					ftprefresh(o);
					ftpchecklist = new ArrayList();
					editor.fireModelChanged();
				}else
					MessageDialog.openMessageDialog(null, "请选择要删除的文件！");
			} catch (ITFEBizException e) {
				e.printStackTrace();
			}
    	}
        return super.ftpdeletefile(o);
    }
    /**
	 * Direction: ftp上传文件
	 * ename: ftpupload
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String ftpupload(Object o){
    	// 判断是否选中文件
		if (null == ftpfilepath || ftpfilepath.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要加载的文件！");
			return null;
		}
		if(ftpfilepath!=null&&ftpfilepath.size()>0)
		{
			File tmpfile = null;
			String serverpath = null;
			try
			{
				for(int i=0;i<ftpfilepath.size();i++)
				{
					tmpfile = (File) ftpfilepath.get(i);
					// 文件上传并记录日志
					serverpath = ClientFileTransferUtil.uploadFile(tmpfile.getAbsolutePath());
					uploadConfirmService.getDirectGrantFileList(serverpath, "ftpupload"+filemainpath);
				}
			} catch (Exception e) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				MessageDialog.openErrorDialog(null, e);
				return null;
			}
		}
		MessageDialog.openMessageDialog(null, "文件上传成功！");
		goftpfilemanager(o);
		editor.fireModelChanged();
        return super.ftpupload(o);
    }
	/**
	 * Direction: ftp刷新
	 * ename: ftprefresh
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String ftprefresh(Object o){
    	this.ftpchecklist = new ArrayList();
        this.ftpfilepath = new ArrayList();
        try {
			ftpfilelist = uploadConfirmService.getDirectGrantFileList(filemainpath+"swap/", "ftp");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "获取文件列表失败！");
		}
		editor.fireModelChanged();
        return super.ftprefresh(o);
    }
}
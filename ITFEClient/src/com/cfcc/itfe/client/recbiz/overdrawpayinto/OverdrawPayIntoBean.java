package com.cfcc.itfe.client.recbiz.overdrawpayinto;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.AreaConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TbsTvGrantpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.AreaSpecUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 12-02-21 15:15:51 子系统: RecBiz 模块:overdrawPayInto 组件:OverdrawPayInto
 */
@SuppressWarnings("unchecked")
public class OverdrawPayIntoBean extends AbstractOverdrawPayIntoBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(OverdrawPayIntoBean.class);
	private ITFELoginInfo loginfo;
	private BigDecimal sumamt;
	// 凭证总笔数
	private Integer vouCount;
	private Date adjustdate;
	private Date systemDate;
	private BigDecimal totalmoney = new BigDecimal(0.00);
	private int totalnum = 0;
	private BigDecimal totalmoney_rel = new BigDecimal("0.00");
	private GrantPaySubBean subBean = null;
	private static String filemainpath;
	private List filelist;
	private List checklist;
	private List ftpchecklist;
	private List ftpfilelist;
	private List ftpfilepath;
	private boolean isselect=true;
	public OverdrawPayIntoBean() {
		super();
		filepath = new ArrayList();
		sumamt = null;
		vouCount = null;
		selectedfilelist = new ArrayList();
		showfilelist = new ArrayList();
		selecteddatalist = new ArrayList();
		showdatalist = new ArrayList();
		filedata = new FileResultDto();
		searchdto = new TbsTvGrantpayplanMainDto();
		setSubBean(new GrantPaySubBean(commonDataAccessService, searchdto));
		// searchdto.setDaccept(DateUtil.currentDate());
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		filemainpath="/itfe/czftp/";
		checklist = new ArrayList();
		init();
	}
	private void init()
	{
		try {
			filelist = uploadConfirmService.getDirectGrantFileList(filemainpath, "020");
		} catch (Throwable e) {
			log.error(e);
			if(e.toString().contains("HttpInvokerException")){
				MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失败\r\n请重新登录！");
			}else{
				MessageDialog.openMessageDialog(null, "获取文件列表失败！");
			}
		}
	}
	/**
	 * Direction: 服务器目录刷新
	 * ename: servicedirRefresh
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String servicedirRefresh(Object o){
        init();
        editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.servicedirRefresh(o);
    }
	/**
	 * Direction: 服务器目录加载
	 * ename: serviceFileAdd
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String serviceFileAdd(Object o){
    	String errName = "";
		int errCount = 0;
		int wrongFileSum = 0;
		StringBuffer problemStr = new StringBuffer();
		StringBuffer errorStr = new StringBuffer();
		List<String> servicefilelist = null;
		List<String> oldfilelist = null;
		if (checklist == null||checklist.size()<=0) {
			MessageDialog.openMessageDialog(null, "请选中要加载的记录!");
			return super.serviceFileAdd(o);
		}
		try
		{
			
			if (checklist != null && checklist.size() > 0) {
				servicefilelist = new ArrayList<String>();
				oldfilelist = new ArrayList<String>();
				Map fileMap = null;
				for(int i=0;i<checklist.size();i++)
				{
					fileMap = (Map)checklist.get(i);
					servicefilelist.add(String.valueOf(fileMap.get("newfilepath")));
					oldfilelist.add(String.valueOf(fileMap.get("oldfilepath")));
				}
				MulitTableDto bizDto = fileResolveCommonService.loadFile(servicefilelist, BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN,MsgConstant.SHOUQUAN_DAORU, null);
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
				commonDataAccessService.saveTvrecvlog(DeleteServerFileUtil.wipeFileOut(servicefilelist, bizDto.getErrNameList()),BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN);
				errName = StateConstant.Import_Errinfo_DIR+ "直接支付文件导入错误信息("+ new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒").format(new java.util.Date()) + ").txt";
				if (!"".equals(errorStr.toString())) {
					FileUtil.getInstance().writeFile(errName,
							errorStr.toString());
				}
			}
			if (problemStr.toString().trim().length() > 0) {
				String noteInfo = "";
				if (servicefilelist != null && servicefilelist.size() > 0) {
					noteInfo = "共加载了" + servicefilelist.size() + "个文件，其中有" + wrongFileSum
							+ "个错误文件，信息如下：\r\n";
				} else {
					noteInfo = "共加载了" + checklist.size() + "个文件，其中有" + errCount
							+ "个错误文件，部分信息如下【详细错误信息请查看" + errName + "】：\r\n";
				}
				MessageDialog.openMessageDialog(null, noteInfo+ problemStr.toString());
				if(oldfilelist!=null&&oldfilelist.size()>0)
				{
					List<String> dellist = new ArrayList<String>();
					for(String temfile:oldfilelist)
					{
						if(problemStr.toString().indexOf(temfile.substring(temfile.lastIndexOf("/")+1))<0)
							dellist.add(temfile);
					}
					if(dellist!=null&&dellist.size()>0)
						uploadConfirmService.delfilelist(dellist,"0");
				}
			} else {
				if(oldfilelist!=null&&oldfilelist.size()>0)
					uploadConfirmService.delfilelist(oldfilelist,"0");
				MessageDialog.openMessageDialog(null, "文件加载成功,本次共加载成功 "+ checklist.size() + " 个文件！");
			}
	    } catch (Exception e) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
	    init();
		checklist= new ArrayList();
		editor.fireModelChanged();
        return super.serviceFileAdd(o);
    }
    /**
	 * Direction: 跳转服务器目录加载
	 * ename: goServiceDirAdd
	 * 引用方法: 
	 * viewers: 服务器目录批量导入
	 * messages: 
	 */
    public String goServiceDirAdd(Object o){
        init();
        return super.goServiceDirAdd(o);
    }
	/**
	 * Direction: 全选反选
	 * ename: selectAllOrOne
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectAllOrOne(Object o){
    	if(filelist==null||filelist.size()<=0)
    		return super.ftpselectall(o);
        if(checklist!=null&&checklist.size()>0)
        	checklist = new ArrayList();
        else
        {
        	if(filelist!=null&&filelist.size()>=100)
        		checklist = filelist.subList(0, 100);
        	else
        		checklist = filelist;
        	if(isselect)
        	{
        		MessageDialog.openMessageDialog(null, "为了系统性能,全选只能选100条记录!");
        		isselect=false;
        	}
        }
    	editor.fireModelChanged();
        return super.selectAllOrOne(o);
    }
    /**
	 * Direction: 批量下载
	 * ename: pldownload
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String pldownload(Object o){
    	if (checklist == null||checklist.size()<=0) {
			MessageDialog.openMessageDialog(null, "请选中要下载的记录!");
			return super.pldownload(o);
		}

		// 客户端下载报文的路径
		String dirsep = File.separator; // 取得系统分割符
		dirsep = "/";
		String clientpath = "c:" + dirsep + "client" + dirsep + "02" + dirsep+TimeFacade.getCurrentStringTime() + dirsep + loginfo.getSorgcode()
				+ dirsep;
		File dir = new File(clientpath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		List<String> servicefilelist = null;
		List<String> oldfilelist = null;
		if (checklist != null && checklist.size() > 0) {
			servicefilelist = new ArrayList<String>();
			oldfilelist = new ArrayList<String>();
			Map fileMap = null;
			for(int i=0;i<checklist.size();i++)
			{
				fileMap = (Map)checklist.get(i);
				servicefilelist.add(String.valueOf(fileMap.get("newfilepath")));
				oldfilelist.add(String.valueOf(fileMap.get("oldfilepath")));
			}
			try {
				for(int i=0;i<oldfilelist.size();i++)
				{
					String clientfile = clientpath+ servicefilelist.get(i).substring(servicefilelist.get(i).lastIndexOf(dirsep)+1);
					ClientFileTransferUtil.downloadFile(servicefilelist.get(i),clientfile);
				}
				
				MessageDialog.openMessageDialog(null, "批量授权支付数据下载成功！\n路径:"+clientpath );
			} catch (Exception e) {
				log.error("批量授权支付数据下载成功", e);
				MessageDialog.openErrorDialog(null, e);
			}
		}
        return super.pldownload(o);
    }
	/**
	 * Direction: 删除服务器目录文件
	 * ename: deleteServiceDirFile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String deleteServiceDirFile(Object o){
    	if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否确认删除选中文件?")) {
	    	List<String> servicefilelist = null;
			List<String> oldfilelist = null;
			try {
				if (checklist != null && checklist.size() > 0) {
					servicefilelist = new ArrayList<String>();
					oldfilelist = new ArrayList<String>();
					Map fileMap = null;
					for(int i=0;i<checklist.size();i++)
					{
						fileMap = (Map)checklist.get(i);
						servicefilelist.add(String.valueOf(fileMap.get("newfilepath")));
						oldfilelist.add(String.valueOf(fileMap.get("oldfilepath")));
					}
					uploadConfirmService.delfilelist(oldfilelist,"1");
					MessageDialog.openMessageDialog(null, "删除文件成功！");
					init();
					checklist = new ArrayList();
					editor.fireModelChanged();
				}else
					MessageDialog.openMessageDialog(null, "请选择要删除的文件！");
			} catch (ITFEBizException e) {
				e.printStackTrace();
			}
    	}
        return super.deleteServiceDirFile(o);
    }
	public String selectEvent(Object o) {
		List<TbsTvGrantpayplanMainDto> mlist = (List<TbsTvGrantpayplanMainDto>) selecteddatalist;
		totalmoney = new BigDecimal(0.00);
		totalnum = 0;
		for (TbsTvGrantpayplanMainDto mdto : mlist) {
			totalmoney = totalmoney.add(mdto.getFzerosumamt());
			totalnum++;
		}
		this.editor.fireModelChanged();
		return super.selectEvent(o);
	}

	/**
	 * Direction: 数据加载 ename: dateload 引用方法: viewers: * messages:
	 */
	public String dateload(Object o) {
		String interfacetype = ""; // 接口类型
		List<File> fileList = new ArrayList<File>();
		List<String> serverpathlist = new ArrayList<String>();
		StringBuffer problemStr = new StringBuffer();
		StringBuffer errorStr = new StringBuffer();
		int sumFile = 0; // 统计所有导入的txt文件
		int wrongFileSum = 0; // 统计错误文件的个数，如果超出20个，不再追加错误信息
		String resultType = "";
		// 判断是否选中文件
		if (null == filepath || filepath.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要加载的文件！");
			return null;
		}
		try {
			// 判断所选文件是否大于1000个
			if (filepath.size() > 1000) {
				MessageDialog.openMessageDialog(null, "所选加载文件不能大于1000个！");
				return null;
			}
			adjustdate = commonDataAccessService.getAdjustDate();
			String str = commonDataAccessService.getSysDBDate();
			systemDate = Date.valueOf(str.substring(0, 4) + "-"
					+ str.substring(4, 6) + "-" + str.substring(6, 8));
			// 数据加载
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			for (int i = 0; i < filepath.size(); i++) {
				File tmpfile = (File) filepath.get(i);
				String tmpfilename = tmpfile.getName(); // 文件的名字
				String tmpfilepath = tmpfile.getAbsolutePath(); // 获取文件的路径
				if (!tmpfilename.trim().toLowerCase().endsWith(".txt")) {
					continue;
				}
				sumFile++; // 统计所有的txt文件
				if (null == tmpfile || null == tmpfilename
						|| null == tmpfilepath) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, " 请选择要加载的文件！");
					return null;
				}
				// 解析文件名字
				interfacetype = getFileObjByFileNameBiz(
						BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, tmpfilename);
				if (interfacetype.trim().length() != 4) {
					if (wrongFileSum < 15) {
						problemStr.append(interfacetype);
					}
					errorStr.append(interfacetype);
					wrongFileSum++;
					continue;
				} else {
					resultType = interfacetype;
				}
				//判断文件是否存在，如果已经存在，则删除
				String serverFilePath=commonDataAccessService.getServerRootPath(tmpfilepath, loginfo.getSorgcode());
				FileUtil.getInstance().deleteFileForExists(serverFilePath);
				// 文件上传并记录日志
				String serverpath = ClientFileTransferUtil.uploadFile(tmpfile
						.getAbsolutePath());
				fileList.add(new File(serverpath));
				serverpathlist.add(serverpath);
			}
			// 服务器加载
			String errName = "";
			int errCount = 0;
			if (fileList != null && fileList.size() > 0 && resultType != null
					&& resultType.trim().length() > 0) {
				MulitTableDto bizDto = fileResolveCommonService.loadFile(
						fileList, BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN,
						resultType, null);
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
				// 记录接受日志
				commonDataAccessService.saveTvrecvlog(DeleteServerFileUtil
						.wipeFileOut(serverpathlist, bizDto.getErrNameList()),
						BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN);
				errName = StateConstant.Import_Errinfo_DIR
						+ "授权支付文件导入错误信息("
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
		}catch (FileTransferException e) {
			log.error("删除服务器文件失败！", e);
			MessageDialog.openErrorDialog(null, e);
		} catch (FileOperateException e) {
			log.error("生成错误信息文件失败！", e);
			MessageDialog.openErrorDialog(null, e);
		} catch (Throwable e) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			// 删除服务器上传失败文件
			try {
				DeleteServerFileUtil.delFile(commonDataAccessService,
						serverpathlist);
			} catch (ITFEBizException e1) {
				log.error("删除服务器文件失败！", e1);
				MessageDialog.openErrorDialog(null, e1);
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
					BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, loginfo
							.getSorgcode())) {
				int result = uploadConfirmService
						.directSubmit(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN);
				if (StateConstant.SUBMITSTATE_SUCCESS == result) {
					MessageDialog.openMessageDialog(null, "操作成功！");
				} else if (StateConstant.SUBMITSTATE_DONE == result) {
					MessageDialog.openMessageDialog(null, "请导入数据！");
				} else {
					MessageDialog.openMessageDialog(null, "处理失败！");
				}
			} else {
				MessageDialog.openMessageDialog(null, "没有直接提交权限，请与管理员联系！");
				return null;
			}
		} catch (ITFEBizException e) {
			log.error("直接提交失败！", e);
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
			if (commonDataAccessService.judgeBatchConfirm(
					BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, loginfo
							.getSorgcode())) {
				if (null == selectedfilelist || selectedfilelist.size() == 0) {
					MessageDialog.openMessageDialog(null, "请选择一条记录！");
					return null;
				}

				String area = AreaSpecUtil.getInstance().getArea();
				if ("FUZHOU".equals(area)) {
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
								+ vouCount.toString()
								+ "]与销号文件的总金额、凭证总笔数不符,请查证!");
						return null;
					} else {
						DisplayCursor.setCursor(SWT.CURSOR_WAIT);
						massdispose();
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					}
				} else {
					DisplayCursor.setCursor(SWT.CURSOR_WAIT);
					massdispose();
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				}
			} else {
				MessageDialog.openMessageDialog(null, "没有批量销号权限，请与管理员联系！");
				return null;
			}

		} catch (ITFEBizException e) {
			log.error("批量提交失败！", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.plsubmit(o);
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
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否确认删除?")) {
			try {
				DisplayCursor.setCursor(SWT.CURSOR_WAIT);
				for (int i = 0; i < selectedfilelist.size(); i++) {
					TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
							.get(i);
					int result = uploadConfirmService.batchDelete(
							BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, dto);
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
				DisplayCursor.setCursor(SWT.CURSOR_WAIT);
				for (int i = 0; i < selecteddatalist.size(); i++) {
					TbsTvGrantpayplanMainDto dto = (TbsTvGrantpayplanMainDto) selecteddatalist
							.get(i);
					int result = uploadConfirmService.eachConfirm(
							BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, dto);
					if (StateConstant.SUBMITSTATE_DONE == result
							|| StateConstant.SUBMITSTATE_SUCCESS == result)
						showdatalist.remove(dto);

				}
				selecteddatalist = new ArrayList();
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				MessageDialog.openMessageDialog(null, "操作成功！");
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			} catch (ITFEBizException e) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
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
	// // 调用其他接口获取showfilelist，如果showfilelist有两条记录，则不选中，否则直接选中
	// try {
	// showfilelist = new ArrayList();
	// selectedfilelist = new ArrayList();
	// showfilelist = uploadConfirmService.batchQuery(
	// BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, sumamt);
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
			// 核算主体代码
			searchdto.setSbookorgcode(loginfo.getSorgcode());
			// 未销号
			searchdto.setSstatus(StateConstant.CONFIRMSTATE_NO);
			showdatalist = uploadConfirmService.eachQuery(
					BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, searchdto);
			// 统计笔数金额
			List<TbsTvGrantpayplanMainDto> mlist = (List<TbsTvGrantpayplanMainDto>) showdatalist;
			totalmoney = new BigDecimal(0.00);
			totalnum = 0;
			for (TbsTvGrantpayplanMainDto mdto : mlist) {
				totalmoney = totalmoney.add(mdto.getFzerosumamt());
				totalnum++;
			}
			if (showdatalist.size() == 0) {
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
	 * Direction: 授权双击事件 ename: doubleClickEvent 引用方法: viewers: 子信息记录显示
	 * messages:
	 */
	public String doubleClickEvent(Object o) {
		if (null != o) {
			TbsTvGrantpayplanMainDto main = (TbsTvGrantpayplanMainDto) o;
			PageRequest subRequest = new PageRequest();
			this.getSubBean().setMainDto(main);
			this.getSubBean().retrieve(subRequest);
		}
		editor.fireModelChanged();
		return super.doubleClickEvent(o);
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

	// 解析文件名字
	// /////////////////////////////////////////////////////////////////////////////////////////////
	private String getFileObjByFileNameBiz(String biztype, String filename)
			throws ITFEBizException {
		String interficetype = "";
		String tmpfilename_new = filename.trim().toLowerCase();
		FileObjDto fileobj = new FileObjDto();
		// 处理退库 并且是厦门（多个文件）
		if (tmpfilename_new.indexOf(".txt") > 0) {
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
				interficetype = MsgConstant.SHOUQUAN_DAORU; // tbs接口
				if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(fileobj
						.getSbiztype())) {
				} else {
					return "文件" + filename + "业务类型不符合！\r\n";
					// throw new ITFEBizException("业务类型不符合!");
				}
				if (!MsgConstant.TIME_FLAG_NORMAL
						.equals(fileobj.getCtrimflag())
						&& !MsgConstant.TIME_FLAG_TRIM.equals(fileobj
								.getCtrimflag())) {
					return "文件" + filename + "调整期标志不符合，必须为“0”正常期 或 “1”调整期！\r\n";
					// throw new
					// ITFEBizException("调整期标志不符合，必须为“0”正常期 或 “1”调整期!");
				} else {
					if (MsgConstant.TIME_FLAG_TRIM.equals(fileobj
							.getCtrimflag())) {
						if (DateUtil.after(systemDate, adjustdate)) {
							return "文件"
									+ filename
									+ "调整期"
									+ com.cfcc.deptone.common.util.DateUtil
											.date2String(adjustdate)
									+ "已过，不能处理调整期业务！";
							// throw new ITFEBizException("调整期" +
							// com.cfcc.deptone.common.util.DateUtil.date2String(adjustdate)
							// + "已过，不能处理调整期业务！");
						}
					}
				}

			} else {
				return "文件" + filename + "文件名格式不符！\r\n";
				// throw new ITFEBizException("文件名格式不符!");
			}
		} else {
			return "文件" + filename + "文件名格式不符！\r\n";
			// throw new ITFEBizException("文件名格式不符!");
		}
		return interficetype;

		// 业务类型需要改动
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
		String area = AreaSpecUtil.getInstance().getArea();
		if (sumamt == null || sumamt.compareTo(new BigDecimal(0)) == 0) {
		} else if ("FUZHOU".equals(area)
				|| AreaConstant.AREA_SHANDONG.equals(area)
				|| AreaConstant.AREA_SICHUAN.equals(area)
				|| "XIAMEN".equals(area)) {
			BigDecimal totalfamt = new BigDecimal("0.00");
			Set<String> strset = new HashSet<String>();
			showfilelist = new ArrayList();
			selectedfilelist = new ArrayList();
			TvFilepackagerefDto refdto = new TvFilepackagerefDto();
			refdto.setSorgcode(loginfo.getSorgcode());
			refdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
			refdto
					.setSoperationtypecode(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN);
			try {
				showfilelist = commonDataAccessService.findRsByDtoUR(refdto);
				if (showfilelist != null && showfilelist.size() > 0) {
					for (Object obj : showfilelist) {
						TvFilepackagerefDto packdto = (TvFilepackagerefDto) obj;
						totalfamt = totalfamt.add(packdto.getNmoney());
						strset.add(packdto.getSpackageno()); // 包流水号
					}
					this.setTotalmoney_rel(totalfamt);
					if (totalfamt.compareTo(sumamt) == 0) {
						selectedfilelist.clear();
						selectedfilelist.addAll(showfilelist);
						this.editor
								.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
						// massdispose();
					} else {
						List<String> inlist = new ArrayList<String>();
						for (String pkno : strset) {
							BigDecimal treamt = new BigDecimal("0.00");
							int sum = 0;
							List<TvFilepackagerefDto> filist = new ArrayList<TvFilepackagerefDto>();
							for (Object obj : showfilelist) {
								TvFilepackagerefDto packdto = (TvFilepackagerefDto) obj;
								if (null != packdto.getSpackageno()
										&& pkno.equals(packdto.getSpackageno())) {
									treamt = treamt.add(packdto.getNmoney());
									sum++;
									filist.add(packdto);
								}
							}
							inlist.add(treamt + "--" + sum);
						}
						selectedfilelist.clear();
						selectedfilelist.addAll(showfilelist);
						this.editor
								.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
						if (strset.size() == 1) {
							MessageDialog.openMessageDialog(null, "输入的总金额["
									+ sumamt + "]]与未销号文件的合计金额数据不一致\n"
									+ "明细提示:所有未销号文件总金额为[" + totalfamt
									+ "],共计 [" + showfilelist.size() + "]个");
						} else {
							MessageDialog.openMessageDialog(null, "输入的总金额["
									+ sumamt + "]与未销号文件的合计金额数据不一致\n"
									+ "明细提示:所有未销号文件总金额为[" + totalfamt
									+ "],共计 [" + showfilelist.size()
									+ "]个 ,共分为[" + strset.size()
									+ "]个包,'包总金额-笔数' 对应关系分别如下\n" + ""
									+ inlist.toString() + "");
						}
					}
					this.editor
							.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				} else {
					MessageDialog.openMessageDialog(null, "授权支付额度不存在未销号数据!");
				}
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
			}

		} else {
			try {
				showfilelist = new ArrayList();
				selectedfilelist = new ArrayList();
				showfilelist = uploadConfirmService.batchQuery(
						BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, sumamt);
				if (null != showfilelist) {
					if (1 == showfilelist.size()) {
						selectedfilelist.addAll(showfilelist);
						this.editor
								.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
						// massdispose();
					}
					this.editor
							.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
					if (showfilelist.size() <= 0) {
						MessageDialog.openMessageDialog(null, "没有符合条件的数据！");
					}
				}

			} catch (ITFEBizException e) {
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
						BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, dto);
				if (StateConstant.SUBMITSTATE_DONE == result
						|| StateConstant.SUBMITSTATE_SUCCESS == result)
					showfilelist.remove(dto);

			}
			selectedfilelist = new ArrayList();
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
			MessageDialog.openMessageDialog(null, "处理成功！");
		}

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

	public BigDecimal getTotalmoney_rel() {
		return totalmoney_rel;
	}

	public void setTotalmoney_rel(BigDecimal totalmoney_rel) {
		this.totalmoney_rel = totalmoney_rel;
	}

	public Integer getVouCount() {
		return vouCount;
	}

	public void setVouCount(Integer vouCount) {
		this.vouCount = vouCount;
	}

	public GrantPaySubBean getSubBean() {
		return subBean;
	}

	public void setSubBean(GrantPaySubBean subBean) {
		this.subBean = subBean;
	}

	public String getFilemainpath() {
		return filemainpath;
	}

	public void setFilemainpath(String filemainpath) {
		if(filemainpath.indexOf("/itfe/czftp/")>=0||filemainpath.indexOf("/itfe/nnczftp/")>=0)
			OverdrawPayIntoBean.filemainpath = filemainpath;
		else
			OverdrawPayIntoBean.filemainpath="/itfe/czftp/";
	}

	public List getFilelist() {
		return filelist;
	}

	public void setFilelist(List filelist) {
		this.filelist = filelist;
	}

	public List getChecklist() {
		return checklist;
	}

	public void setChecklist(List checklist) {
		this.checklist = checklist;
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
        isselect = true;
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
    	if(ftpfilelist==null||ftpfilelist.size()<=0)
    		return super.ftpselectall(o);
        if(ftpchecklist!=null&&ftpchecklist.size()>0)
        	ftpchecklist = new ArrayList();
        else
        {
        	if(ftpfilelist!=null&&ftpfilelist.size()>=100)
        		ftpchecklist = ftpfilelist.subList(0, 100);
        	else
        		ftpchecklist = ftpfilelist;
        	if(isselect)
        	{
        		MessageDialog.openMessageDialog(null, "为了系统性能,全选只能选100条记录!");
        		isselect=false;
        	}
        }
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
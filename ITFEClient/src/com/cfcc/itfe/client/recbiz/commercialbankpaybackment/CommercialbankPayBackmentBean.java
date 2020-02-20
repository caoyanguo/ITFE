package com.cfcc.itfe.client.recbiz.commercialbankpaybackment;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.client.recbiz.commercialbankpayment.PayreckBankSubBean;
import com.cfcc.itfe.constant.AreaConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpayMainDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.AreaSpecUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.validator.ValidationError;

/**
 * codecomment: 
 * @author db2admin
 * @time   13-01-26 13:24:26
 * 子系统: RecBiz
 * 模块:commercialbankPayBackment
 * 组件:CommercialbankPayBackment
 */
public class CommercialbankPayBackmentBean extends AbstractCommercialbankPayBackmentBean implements IPageDataProvider {

	private static Log log = LogFactory.getLog(CommercialbankPayBackmentBean.class);
    private ITFELoginInfo loginfo;
    private BigDecimal sumamt;
	// 凭证总笔数
	private Integer vouCount;
	private Date adjustdate;
	private Date systemDate;
	private BigDecimal totalmoney = new BigDecimal(0.00);
	private int totalnum = 0;
	private BigDecimal totalmoney_rel = new BigDecimal("0.00");
	private PayreckBankBackSubBean subBean = null;
	private List<String> biztypeList=new ArrayList<String>();

	
	public CommercialbankPayBackmentBean() {
      super();
      filepath = new ArrayList();
      selectedfilelist = new ArrayList();
      showfilelist = new ArrayList();
      selecteddatalist = new ArrayList();
      showdatalist = new ArrayList();
      searchdto = new TbsTvBnkpayMainDto();
      payreckbackdto = new TvPayreckBankBackDto();
      payreckbackdto.setDpayentrustdate(DateUtil.currentDate());
      payreckbackdto.setSpaymsgno(StateConstant.CMT100);
//      setSubBean(new PayreckBankBackSubBean(commonDataAccessService, searchdto));
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();    
    }
    
	/**
	 * Direction: 数据加载
	 * ename: dateload
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String dateload(Object o){
    	
    	// 接口类型
		String interfacetype = "";
		List<File> fileList = new ArrayList<File>();
		List<String> serverpathlist = new ArrayList<String>();
		StringBuffer problemStr = new StringBuffer();
		StringBuffer errorStr = new StringBuffer();
		String resultType = "";
		int sumFile = 0; // 统计所有导入的txt文件
		int wrongFileSum = 0; // 统计错误文件的个数，如果超出20个，不再追加错误信息
		// 判断是否选中文件
		if (null == getFilepath() || filepath.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要加载的文件！");
			return null;
		}
		String info =checkRequired();
		if (null!=info && info.length()>0) {
			MessageDialog.openMessageDialog(null, "请补录来账资金信息\n"+info);
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
			biztypeList.clear();
			for (int i = 0; i < filepath.size(); i++) {
				File tmpfile = (File) filepath.get(i);
				// 文件的名字
				String tmpfilename = tmpfile.getName();
				// 获取文件的路径
				String tmpfilepath = tmpfile.getAbsolutePath();
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
				interfacetype = getFileObjByFileNameBiz(tmpfilename);
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
						fileList,"",resultType, payreckbackdto);
				
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
				String flag=getBiztype();
				// 记接收日志
				commonDataAccessService.saveTvrecvlog(DeleteServerFileUtil
						.wipeFileOut(serverpathlist, bizDto.getErrNameList()),flag);
				errName = StateConstant.Import_Errinfo_DIR
						+ "商行处理支付划款申请文件导入错误信息("
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
			 payreckbackdto = new TvPayreckBankBackDto();
		     payreckbackdto.setDpayentrustdate(DateUtil.currentDate());
		     payreckbackdto.setSpaymsgno(StateConstant.CMT103);   
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
		} catch (Throwable e) {
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
	 * Direction: 跳转批量销号查询
	 * ename: topiliangxh
	 * 引用方法: 
	 * viewers: 批量销号
	 * messages: 
	 */
    public String topiliangxh(Object o){
          return super.topiliangxh(o);
    }

	/**
	 * Direction: 跳转逐笔销号查询
	 * ename: tozhubixh
	 * 引用方法: 
	 * viewers: 逐笔销号
	 * messages: 
	 */
    public String tozhubixh(Object o){
          return super.tozhubixh(o);
    }

	/**
	 * Direction: 直接提交
	 * ename: submit
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String submit(Object o){
    	//获取导入文件的业务类型
    	String flag=BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK;
    	try {
			if (commonDataAccessService.judgeDirectSubmit(flag, loginfo.getSorgcode())) {
				int result = uploadConfirmService.applyBackDirectSubmit(flag, payreckbackdto);
				if (StateConstant.SUBMITSTATE_SUCCESS == result) {
					MessageDialog.openMessageDialog(null, "操作成功！");
				} else if (StateConstant.SUBMITSTATE_DONE == result) {
					MessageDialog.openMessageDialog(null, "请导入数据！");
				} else {
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
	 * Direction: 返回默认界面
	 * ename: goback
	 * 引用方法: 
	 * viewers: 商行办理支付划款申请数据导入
	 * messages: 
	 */
    public String goback(Object o){
          return super.goback(o);
    }

	/**
	 * Direction: 批量确认
	 * ename: plsubmit
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String plsubmit(Object o){
    	//获取导入文件的业务类型
    	String flag=BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK;
    	try {
			/**
			 * 判断能否批量销号
			 */
			if (commonDataAccessService.judgeBatchConfirm(flag, loginfo.getSorgcode())) {

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
								+ vouCount.toString()
								+ "]与销号文件的总金额、凭证总笔数不符,请查证!");
						return null;
					} else {
						// 批量销号处理
						DisplayCursor.setCursor(SWT.CURSOR_WAIT);
						massdispose(flag);
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);

					}
			
			} else {
				MessageDialog.openMessageDialog(null, "没有批量销号权限，请与管理员联系！");
			}

		} catch (ITFEBizException e) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			log.error("批量提交失败！", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
          return super.plsubmit(o);
    }

	/**
	 * Direction: 批量删除
	 * ename: pldel
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String pldel(Object o){
//    	String flag=getBiztype();
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
							dto.getSoperationtypecode(), dto);
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
	 * Direction: 逐笔提交
	 * ename: zbsubmit
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String zbsubmit(Object o){
    	
    	//获取导入文件的业务类型
//    	String flag=getBiztype();
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
					TbsTvBnkpayMainDto dto = (TbsTvBnkpayMainDto) selecteddatalist
							.get(i);
					int result = uploadConfirmService.applyBackEachConfirm(dto.getSbiztype(), dto, payreckbackdto);
					if (StateConstant.SUBMITSTATE_DONE == result
							|| StateConstant.SUBMITSTATE_SUCCESS == result)
						showdatalist.remove(dto);

				}
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				selecteddatalist = new ArrayList();
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				MessageDialog.openMessageDialog(null, "操作成功！");
			} catch (ITFEBizException e) {
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				log.error("逐笔提交失败！", e);
				MessageDialog.openErrorDialog(null, e);
				return null;
			}
		}
          return super.zbsubmit(o);
    }

	/**
	 * Direction: 批量销号查询
	 * ename: plsearch
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String plsearch(Object o){
          return super.plsearch(o);
    }

	/**
	 * Direction: 逐笔销号查询
	 * ename: zbsearch
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String zbsearch(Object o){
    	selecteddatalist = new ArrayList();
		showdatalist = new ArrayList();
		String flag=getBiztype();
		try {
			// 核算主体代码
			searchdto.setSbookorgcode(loginfo.getSorgcode());
			// 未销号
//			searchdto.setSstate(StateConstant.CONFIRMSTATE_NO);
			showdatalist = uploadConfirmService.eachQuery(
					flag, searchdto);
			selecteddatalist.addAll(showdatalist);
			// 统计笔数金额
			List<TbsTvBnkpayMainDto> mlist = (List<TbsTvBnkpayMainDto>) showdatalist;
			totalmoney = new BigDecimal(0.00);
			totalnum = 0;
			for (TbsTvBnkpayMainDto mdto : mlist) {
				totalmoney = totalmoney.add(mdto.getFsmallsumamt().add(mdto.getFzerosumamt()));
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
	 * Direction: 全选
	 * ename: selectall
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectall(Object o){
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
	 * Direction: 选中事件
	 * ename: selectEvent
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectEvent(Object o){
    	List<TbsTvBnkpayMainDto> mlist = (List<TbsTvBnkpayMainDto>) selecteddatalist;
		totalmoney = new BigDecimal(0.00);
		totalnum = 0;
		for (TbsTvBnkpayMainDto mdto : mlist) {
			totalmoney = totalmoney.add(mdto.getFsmallsumamt().add(mdto.getFzerosumamt()));
			totalnum++;
		}
		this.editor.fireModelChanged();
          return super.selectEvent(o);
    }

	/**
	 * Direction: 双击显示明细
	 * ename: doubleClickEvent
	 * 引用方法: 
	 * viewers: 子明細记录显示界面
	 * messages: 
	 */
    public String doubleClickEvent(Object o){
    	if (null != o) {
			TbsTvBnkpayMainDto main = (TbsTvBnkpayMainDto) o;
			PageRequest subRequest = new PageRequest();
			this.getSubBean().setMaindto(main);
			this.getSubBean().retrieve(subRequest);
		}
		editor.fireModelChanged();
          return super.doubleClickEvent(o);
    }
    
    
    /**
	 * Direction: 跳转直接提交补录信息页面
	 * ename: toDirectBulu
	 * 引用方法: 
	 * viewers: 
	 * messages: 
	 */
	public String toDirectBulu(Object o) {
		List<String> list=getBiztypeList();
		if (null == list || list.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要加载的文件！");
			return null;
		}
		MessageDialog.openMessageDialog(null, "请先填写补录信息！");
		payreckbackdto = new TvPayreckBankBackDto();
		return super.toDirectBulu(o);
	}

	/**
	 * Direction: 跳转批量提交补录信息页面
	 * ename: toPilBulu
	 * 引用方法: 
	 * viewers: 
	 * messages: 
	 */
	public String toPilBulu(Object o) {
		MessageDialog.openMessageDialog(null, "请先填写补录信息！");
		payreckbackdto = new TvPayreckBankBackDto();
		return super.toPilBulu(o);
	}

	/**
	 * Direction: 跳转逐笔提交补录信息页面
	 * ename: toZhubiBulu
	 * 引用方法: 
	 * viewers:
	 * messages: 
	 */
	public String toZhubiBulu(Object o) {
		MessageDialog.openMessageDialog(null, "请先填写补录信息！");
		payreckbackdto = new TvPayreckBankBackDto();
		return super.toZhubiBulu(o);
	}
    
    
	public String checkRequired(){
		StringBuffer demo=new StringBuffer();
 		if(null==payreckbackdto.getSpaydictateno()||payreckbackdto.getSpaydictateno().equals("")){
			demo.append("支付交易序号不能为空！\r");
		}else {
			String regex = "\\d{8}";
			Matcher matcher;
			Pattern pattern;
			String tempStr = payreckbackdto.getSpaydictateno().toString();
			pattern = Pattern.compile(regex);
			matcher = pattern.matcher(tempStr);
			if (!matcher.matches()) {
				demo.append("输入不合法，支付交易序号应该输入8位数字！\r\n");
				payreckbackdto.setSpaydictateno(null);
			}
			
		
		}
 		if(null==payreckbackdto.getSpaymsgno()||payreckbackdto.getSpaymsgno().equals("")){
			demo.append("支付报文编号不能为空！\r");
		}
 		if(null==payreckbackdto.getDpayentrustdate()||payreckbackdto.getDpayentrustdate().equals("")){
			demo.append("支付委托日期不能为空！\r");
		}
 		if(null==payreckbackdto.getSpaysndbnkno()||payreckbackdto.getSpaysndbnkno().equals("")){
			demo.append("支付发起行行号不能为空！\r");
		}
 		if(null==payreckbackdto.getFamt()||payreckbackdto.getFamt().equals("")){
			demo.append("来账资金总额不能为空！");
		}
		
		return demo.toString();
	}
	
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

    /**
	 * 根据文件名得到文件接口类型
	 * 
	 * @param biztype
	 * @param filename
	 * @return
	 * @throws ITFEBizException
	 */
	private String getFileObjByFileNameBiz(String filename)
			throws ITFEBizException {
		String interficetype = "";
		String tmpfilename_new = filename.trim().toLowerCase();
		FileObjDto fileobj = new FileObjDto();
		if (tmpfilename_new.indexOf(".txt") > 0) {
			String tmpfilename = tmpfilename_new.replace(".txt", "");
			// 十三位说明是有可能是地方横连或tbs， bool为true 则为tbs接口否则为地方横连
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

				biztypeList.add(fileobj.getSbiztype());
				interficetype = MsgConstant.APPLYPAY_BACK_DAORU; // tbs接口
				if (BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK.equals(fileobj
						.getSbiztype())||BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK.equals(fileobj
								.getSbiztype())) {
				} else {
					return "文件" + filename + "业务类型不符合！\r\n";
				}
				if (!MsgConstant.TIME_FLAG_NORMAL
						.equals(fileobj.getCtrimflag())
						&& !MsgConstant.TIME_FLAG_TRIM.equals(fileobj
								.getCtrimflag())) {
					return "文件" + filename + "调整期标志不符合，必须为“0”正常期 或 “1”调整期！\r\n";
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
						}
					}
				}

			} else {
				return "文件" + filename + "文件名格式不符！\r\n";
			}
		} else {
			return "文件" + filename + "文件名格式不符！\r\n";
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
		String flag=BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK;
		String area = AreaSpecUtil.getInstance().getArea();
		if (sumamt == null || sumamt.compareTo(new BigDecimal(0)) == 0) {
			 MessageDialog.openMessageDialog(null, "金额不能为空或0！");
		} else  {
			BigDecimal totalfamt = new BigDecimal("0.00");
			showfilelist = new ArrayList();
			selectedfilelist = new ArrayList();
			
			String sqlWhere="";
			if(flag.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
				sqlWhere=" and ( S_operationtypecode= '"+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+"' or S_operationtypecode='"+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK+"')";
			}else if(flag.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)){
				sqlWhere=" and S_operationtypecode= '"+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+"'";
			}else if(flag.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
				sqlWhere=" and S_operationtypecode= '"+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK+"'";
			}
			
			TvFilepackagerefDto refdto = new TvFilepackagerefDto();
			refdto.setSorgcode(loginfo.getSorgcode());
			refdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
//			refdto.setSoperationtypecode(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY);
			try {
				Set<String> strset = new HashSet<String>();
				showfilelist = commonDataAccessService.findRsByDto(refdto, sqlWhere);
				if (showfilelist != null && showfilelist.size() > 0) {
					for (Object obj : showfilelist) {
						TvFilepackagerefDto packdto = (TvFilepackagerefDto) obj;
						totalfamt = totalfamt.add(packdto.getNmoney());
						strset.add(packdto.getSpackageno());
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
									+ sumamt + "]与未销号文件的合计金额数据不一致\n"
									+ "明细提示:所有未销号文件总金额为[" + totalfamt
									+ "],共计 [" + showfilelist.size() + "]个");
						} else {
							MessageDialog.openMessageDialog(null, "输入的总金额[["
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
					MessageDialog.openMessageDialog(null, "划款申请退回不存在未销号数据!");
				}
			} catch (Throwable e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
			}

		} 
	}

	private void massdispose(String flag) throws ITFEBizException {

		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否确认提交？")) {
			for (int i = 0; i < selectedfilelist.size(); i++) {
				TvFilepackagerefDto dto = (TvFilepackagerefDto) selectedfilelist
						.get(i);
				int result = uploadConfirmService.applyBackBatchConfirm(flag, dto, payreckbackdto);
				if (StateConstant.SUBMITSTATE_DONE == result
						|| StateConstant.SUBMITSTATE_SUCCESS == result)
					showfilelist.remove(dto);
			}
			selectedfilelist = new ArrayList();
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
			MessageDialog.openMessageDialog(null, "处理成功！");
		}

	}
	
	public String getBiztype(){
		StringBuffer flag=new StringBuffer();
    	List<String> list=getBiztypeList();
    	if(null!=list&&list.size()>0){
    		if(list.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)){
        		flag.append(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK);
        	}
        	if(list.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
        		flag.append(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK);
        	}
    	}else{
    		flag.append(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)
    		.append(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK);
    	}
		return flag.toString();
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

	public PayreckBankBackSubBean getSubBean() {
		return subBean;
	}

	public void setSubBean(PayreckBankBackSubBean subBean) {
		this.subBean = subBean;
	}
	
	public List<String> getBiztypeList() {
		return biztypeList;
	}

	public void setBiztypeList(List<String> biztypeList) {
		this.biztypeList = biztypeList;
	}

}
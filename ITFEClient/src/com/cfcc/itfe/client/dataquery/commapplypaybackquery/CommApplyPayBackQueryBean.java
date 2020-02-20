package com.cfcc.itfe.client.dataquery.commapplypaybackquery;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.page.MainCommApplyPayBackBean;
import com.cfcc.itfe.client.common.page.SubCommApplyPayBackBean;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.security.SM3Process;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author db2admin
 * @time   13-03-04 21:08:15
 * 子系统: DataQuery
 * 模块:commApplyPayBackQuery
 * 组件:CommApplyPayBackQuery
 */
public class CommApplyPayBackQueryBean extends AbstractCommApplyPayBackQueryBean implements IPageDataProvider {

    @SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(CommApplyPayBackQueryBean.class);
    
    private TvPayreckBankBackDto finddto;
    private TvPayreckBankBackDto operdto;
    private ITFELoginInfo loginfo;
    private String dentrustdate=null;
    private String dvoudate=null;
    private String dorientrustdate=null;
    private String dorivoudate=null;
    private String dpayentrustdate=null;
    private java.sql.Date dentrustdatestart=null;
    private java.sql.Date dentrustdateend=null;
    private String expfunccode;
	private String payamt;
    String startdate = TimeFacade.getCurrentStringTime();
	String enddate = TimeFacade.getCurrentStringTime();
	int pici = 0;
    MainCommApplyPayBackBean mainCommApplyPayBackBean = null;
    SubCommApplyPayBackBean subCommApplyPayBackBean = null;
    
    public CommApplyPayBackQueryBean() {
    	super();
    	selectDataList = new ArrayList();
    	finddto = new TvPayreckBankBackDto();
		operdto = new TvPayreckBankBackDto();
		finddto.setSpaymode("0");
		setMainCommApplyPayBackBean(new MainCommApplyPayBackBean(commApplyPayBackQueryService, finddto));
		setSubCommApplyPayBackBean(new SubCommApplyPayBackBean(commApplyPayBackQueryService, finddto));
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
		dentrustdatestart = CommonUtil.strToDate(TimeFacade.getCurrentStringTime());
		dentrustdateend = CommonUtil.strToDate(TimeFacade.getCurrentStringTime());
    }
    
	/**
	 * Direction: 查询列表事件
	 * ename: searchList
	 * 引用方法: 
	 * viewers: 商行办理支付划款申请退回主表信息界面
	 * messages: 
	 */
    public String searchList(Object o){
    	if(finddto.getSpaymode()==null||"".equals(finddto.getSpaymode()))
    	{
    		MessageDialog.openMessageDialog(null, "支付方式不能为空!");
    		return "";
    	}
    	StringBuffer where = null;
		if(dentrustdatestart!=null&&!"".equals(dentrustdatestart))
		{
			where = new StringBuffer("");
			where.append(" D_ENTRUSTDATE >='"+dentrustdatestart+"'");
		}
		if(dentrustdateend!=null&&!"".equals(dentrustdateend))
		{
			if(where==null)
			{
				where = new StringBuffer();
				where.append(" D_ENTRUSTDATE <='"+dentrustdateend+"'");
			}else
			{
				where.append(" and D_ENTRUSTDATE <='"+dentrustdateend+"'");
			}
		}
		if(where!=null)
			finddto.setSaddword(where.toString());
		else
			finddto.setSaddword(null);
    	PageRequest mainpageRequest = new PageRequest();
		PageResponse response=null;
		this.getMainCommApplyPayBackBean().setMaindto(finddto);
		this.getMainCommApplyPayBackBean().setExpfunccode(expfunccode);
		this.getMainCommApplyPayBackBean().setPayamt(payamt);
		response = this.getMainCommApplyPayBackBean().retrieve(mainpageRequest);
    	
		if (response.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录!");
			return super.rebackSearch(o);
		}
		
		editor.fireModelChanged();
		operdto=new TvPayreckBankBackDto();
    	return super.searchList(o);
    }

	/**
	 * Direction: 返回查询界面
	 * ename: rebackSearch
	 * 引用方法: 
	 * viewers: 商行办理支付划款申请退回查询界面
	 * messages: 
	 */
    public String rebackSearch(Object o){
    	setSubCommApplyPayBackBean(new SubCommApplyPayBackBean(commApplyPayBackQueryService, new TvPayreckBankBackDto()));
        return super.rebackSearch(o);
    }
    
    
    /**
	 * Direction: 主信息单击事件
	 * ename: singleclickMain
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleclickMain(Object o){
    	PageRequest subpageRequest = new PageRequest();
    	
    	operdto = (TvPayreckBankBackDto) o;
		this.getSubCommApplyPayBackBean().setMaindto(operdto);
		this.getSubCommApplyPayBackBean().retrieve(subpageRequest);
		
		editor.fireModelChanged();
		
        return super.singleclickMain(o);
    }

	/**
	 * Direction: 主信息双击事件
	 * ename: doubleclickMain
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String doubleclickMain(Object o){
    	PageRequest subpageRequest = new PageRequest();
    	operdto = (TvPayreckBankBackDto) o;
		this.getSubCommApplyPayBackBean().setMaindto(operdto);
		this.getSubCommApplyPayBackBean().setExpfunccode(expfunccode);
		this.getSubCommApplyPayBackBean().setPayamt(payamt);
		this.getSubCommApplyPayBackBean().retrieve(subpageRequest);
		editor.fireModelChanged();
          return super.doubleclickMain(o);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

	public TvPayreckBankBackDto getFinddto() {
		return finddto;
	}

	public void setFinddto(TvPayreckBankBackDto finddto) {
		this.finddto = finddto;
	}

	public TvPayreckBankBackDto getOperdto() {
		return operdto;
	}

	public void setOperdto(TvPayreckBankBackDto operdto) {
		this.operdto = operdto;
	}

	public MainCommApplyPayBackBean getMainCommApplyPayBackBean() {
		return mainCommApplyPayBackBean;
	}

	public void setMainCommApplyPayBackBean(
			MainCommApplyPayBackBean mainCommApplyPayBackBean) {
		this.mainCommApplyPayBackBean = mainCommApplyPayBackBean;
	}

	public SubCommApplyPayBackBean getSubCommApplyPayBackBean() {
		return subCommApplyPayBackBean;
	}

	public void setSubCommApplyPayBackBean(
			SubCommApplyPayBackBean subCommApplyPayBackBean) {
		this.subCommApplyPayBackBean = subCommApplyPayBackBean;
	}
    
	/**
	 * Direction: 导出txt
	 * ename: exportCommApplyPay
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportCommApplyPayBack(Object o){
    	// 选择保存路径
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
//		List<String> filelist = new ArrayList<String>();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
			return "";
		}
		String clientFilePath;
//		String strdate = DateUtil.date2String2(new Date()); // 当前系统年月日
		String dirsep = File.separator;
		String serverFilePath;
		try {
			StringBuffer where = null;
			if(dentrustdatestart!=null&&!"".equals(dentrustdatestart))
			{
				where = new StringBuffer("");
				where.append(" D_ENTRUSTDATE >='"+dentrustdatestart+"'");
			}
			if(dentrustdateend!=null&&!"".equals(dentrustdateend))
			{
				if(where==null)
				{
					where = new StringBuffer();
					where.append(" D_ENTRUSTDATE <='"+dentrustdateend+"'");
				}else
				{
					where.append(" and D_ENTRUSTDATE <='"+dentrustdateend+"'");
				}
			}
			if(where!=null)
				finddto.setSaddword(where.toString());
			else
				finddto.setSaddword(null);
			serverFilePath = commApplyPayBackQueryService.exportCommApplyPayBack(finddto,"0").replace("\\",
					"/");
			int j = serverFilePath.lastIndexOf("/");
			String partfilepath  = serverFilePath.replaceAll(serverFilePath.substring(
					0, j + 1), "");
			clientFilePath = filePath + dirsep
					+ partfilepath;
			File f = new File(clientFilePath);
			File dir = new File(f.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			ClientFileTransferUtil.downloadFile(serverFilePath, clientFilePath);
			String key = commonDataAccessService.getModeKey(loginfo.getSorgcode(), "1");
			SM3Process.addSM3Sign(clientFilePath, key);
			MessageDialog.openMessageDialog(null, "导出文件成功！文件保存在：\n"
					+ clientFilePath);

		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}
        return "";
    }
    
    /**
	 * Direction: 导出选中回单
	 * ename: exportSelectData
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportSelectData(Object o){
    	if(selectDataList.size()==0){
    		MessageDialog.openMessageDialog(null, "未选择回单，请选择要导出的回单！");
			return "";
    	}
    	// 选择保存路径
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
			return "";
		}
		String serverFilePath;
		try {
			serverFilePath = exportCommApplyPayBack(finddto,"0", selectDataList,filePath).replace("\\",
					"/");
			String key = commonDataAccessService.getModeKey(loginfo.getSorgcode(), "1");
			SM3Process.addSM3Sign(serverFilePath, key);
			MessageDialog.openMessageDialog(null, "导出文件成功！文件保存在：\n"
					+ serverFilePath);

		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}
        return "";
    }
    public String exportCommApplyPayBack(IDto finddto, String selectedtable,List selectDataList,String filePath)throws ITFEBizException {
    	
		String filename = "";

		try {
			startdate = TimeFacade.getCurrentStringTime();
			if (startdate.equals(enddate))
				pici++;
			else
				enddate = TimeFacade.getCurrentStringTime();
			if (selectedtable.equals("0")) {
				TvPayreckBankBackDto mdto = (TvPayreckBankBackDto) finddto;
				if ("0".equals(mdto.getSpaymode()))// 0直接1授权
					filename = TimeFacade.getCurrentStringTime()
							+ BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK
							+ (pici < 10 ? "0" + pici : pici) + "hd.txt";
				else
					filename = TimeFacade.getCurrentStringTime()
							+ BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK
							+ (pici < 10 ? "0" + pici : pici) + "hd.txt";
			}
//			String root = "/itfe/root/temp/";// ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // 取得系统分割符
			String strdate = DateUtil.date2String2(new Date()); // 当前系统年月日
			String fullpath = filePath + dirsep + filename;
			String splitSign = ",";// "\t"; // 文件记录分隔符号

			StringBuffer filebuf = new StringBuffer();
			if (selectedtable.equals("0")) {
				for (TvPayreckBankBackDto _dto : (List<TvPayreckBankBackDto>) selectDataList) {
					filebuf.append(_dto.getStrecode());// 收款国库代码
					filebuf.append(splitSign + _dto.getSpayername());// 代理行名称-付款人名称
					filebuf.append(splitSign + _dto.getSpayeracct());// 代理行收款账户-付款人账号
					filebuf.append(splitSign + _dto.getSagentbnkcode());// 代理银行代码
					filebuf.append(splitSign + "");// 代理银行开户行行号-付款人开户行代码
					filebuf.append(splitSign + _dto.getSpayeename());// 国库收款人名称-收款人名称
					filebuf.append(splitSign + _dto.getSpayeeacct());// 国库收款人账户-收款人账号
					filebuf.append(splitSign + "");// 国库所在银行代码-收款人开户行代码
					filebuf.append(splitSign + "");// 国库所在银行行号-收款人开户行行号
					filebuf.append(splitSign + _dto.getSbudgettype());// 预算种类代码
					filebuf.append(splitSign + _dto.getFamt());// 零余额发生额
					filebuf.append(splitSign + "0.00");// 小额现金发生额
					filebuf.append(splitSign);// 摘要代码
					filebuf.append(splitSign + _dto.getSvouno());// 凭证编号
					filebuf
							.append(splitSign
									+ _dto.getDvoudate().toString().replaceAll(
											"-", ""));// 凭证日期
					filebuf.append(splitSign + _dto.getSorivouno());// 原凭证编号
					filebuf.append(splitSign
							+ _dto.getDorivoudate().toString().replaceAll("-",
									""));// 原凭证日期
					filebuf.append(splitSign);// 是否成功
					if (DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(_dto
							.getSstatus()))
						filebuf.append("1");// 处理结果1成功，0失败
					else
						filebuf.append("0");// 处理结果1成功，0失败
					if (_dto.getSaddword() != null) {
						filebuf.append(splitSign + _dto.getSaddword());// 原因
					} else {
						filebuf.append(splitSign + "");// 原因
					}

					filebuf.append("\r\n");
				}
			}
			File f = new File(fullpath);
			if (f.exists()) {
				FileUtil.getInstance().deleteFiles(fullpath);
			}
			FileUtil.getInstance().writeFile(
					fullpath,
					filebuf.toString().substring(0,
							filebuf.toString().length() - 2));
			return fullpath;
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("写文件出错", e);
		} 
	}
    
    
    /**
	 * Direction: 导出CSV
	 * ename: exportFile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
	public String exportFile(Object o) {
		// 选择保存路径
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent().getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择导出文件路径！");
			return "";
		}
		String clientFilePath;
		String dirsep = File.separator;
		String serverFilePath;
		try {
			StringBuffer where = null;
			if(dentrustdatestart!=null&&!"".equals(dentrustdatestart))
			{
				where = new StringBuffer("");
				where.append(" D_ENTRUSTDATE >='"+dentrustdatestart+"'");
			}
			if(dentrustdateend!=null&&!"".equals(dentrustdateend))
			{
				if(where==null)
				{
					where = new StringBuffer();
					where.append(" D_ENTRUSTDATE <='"+dentrustdateend+"'");
				}else
				{
					where.append(" and D_ENTRUSTDATE <='"+dentrustdateend+"'");
				}
			}
			if(where!=null)
				finddto.setSaddword(where.toString());
			else
				finddto.setSaddword(null);
			serverFilePath = commApplyPayBackQueryService.exportFile(finddto,"0").replace("\\","/");
			int j = serverFilePath.lastIndexOf("/");
			String partfilepath  = serverFilePath.replaceAll(serverFilePath.substring(0, j + 1), "");
			clientFilePath = filePath + dirsep+ partfilepath;
			File f = new File(clientFilePath);
			File dir = new File(f.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			ClientFileTransferUtil.downloadFile(serverFilePath, clientFilePath);
			MessageDialog.openMessageDialog(null, "导出文件成功！文件保存在：\n" + clientFilePath);
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}
        return "";
	}

	public String getDentrustdate() {
		return dentrustdate;
	}

	public void setDentrustdate(String dentrustdate) {
		this.dentrustdate = dentrustdate;
		if(finddto!=null)
			finddto.setDentrustdate(com.cfcc.itfe.util.CommonUtil.strToDate(dentrustdate));
		if(operdto!=null)
			operdto.setDentrustdate(com.cfcc.itfe.util.CommonUtil.strToDate(dentrustdate));		
	}

	public String getDvoudate() {
		return dvoudate;
	}

	public void setDvoudate(String dvoudate) {
		this.dvoudate = dvoudate;
		if(finddto!=null)
			finddto.setDentrustdate(com.cfcc.itfe.util.CommonUtil.strToDate(dvoudate));
		if(operdto!=null)
			operdto.setDentrustdate(com.cfcc.itfe.util.CommonUtil.strToDate(dvoudate));		
	}

	public String getDorientrustdate() {
		return dorientrustdate;
	}

	public void setDorientrustdate(String dorientrustdate) {
		this.dorientrustdate = dorientrustdate;
		if(finddto!=null)
			finddto.setDentrustdate(com.cfcc.itfe.util.CommonUtil.strToDate(dorientrustdate));
		if(operdto!=null)
			operdto.setDentrustdate(com.cfcc.itfe.util.CommonUtil.strToDate(dorientrustdate));		
	}

	public String getDorivoudate() {
		return dorivoudate;
	}

	public void setDorivoudate(String dorivoudate) {
		this.dorivoudate = dorivoudate;
		if(finddto!=null)
			finddto.setDentrustdate(com.cfcc.itfe.util.CommonUtil.strToDate(dorivoudate));
		if(operdto!=null)
			operdto.setDentrustdate(com.cfcc.itfe.util.CommonUtil.strToDate(dorivoudate));		
	}

	public String getDpayentrustdate() {
		return dpayentrustdate;
	}

	public void setDpayentrustdate(String dpayentrustdate) {
		this.dpayentrustdate = dpayentrustdate;
		if(finddto!=null)
			finddto.setDentrustdate(com.cfcc.itfe.util.CommonUtil.strToDate(dpayentrustdate));
		if(operdto!=null)
			operdto.setDentrustdate(com.cfcc.itfe.util.CommonUtil.strToDate(dpayentrustdate));		
	}

	public java.sql.Date getDentrustdatestart() {
		return dentrustdatestart;
	}

	public void setDentrustdatestart(java.sql.Date dentrustdatestart) {
		this.dentrustdatestart = dentrustdatestart;
	}

	public java.sql.Date getDentrustdateend() {
		return dentrustdateend;
	}

	public void setDentrustdateend(java.sql.Date dentrustdateend) {
		this.dentrustdateend = dentrustdateend;
	}

	public String getExpfunccode() {
		return expfunccode;
	}

	public void setExpfunccode(String expfunccode) {
		this.expfunccode = expfunccode;
	}

	public String getPayamt() {
		return payamt;
	}

	public void setPayamt(String payamt) {
		this.payamt = payamt;
	}
}
package com.cfcc.itfe.client.para.tsconverttaxorg;

import java.io.File;
import java.util.*;

import org.apache.commons.logging.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.ChinaTest;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;

/**
 * codecomment:
 * 
 * @author caoyg
 * @time 09-10-20 08:42:01 子系统: Para 模块:TsConverttaxorg 组件:TsConverttaxorg
 */
@SuppressWarnings("unchecked")
public class TsConverttaxorgBean extends AbstractTsConverttaxorgBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsConverttaxorgBean.class);
	private List list1;
	private List list2;
	private String filedemo="导入文件为txt格式文件,文件名及文件路径中不能包含汉字。文件中每行为一条数据，一条数据分5个字段，每个字段以逗号分隔。字段含义:核算主体代码,国库主体代码,TBS征收机关代码,TIPS征收机关代码,征收机关性质(1-国税;2-地税;3-海关;4-财政;5-其它;)";
	private ITFELoginInfo loginfo = null;
	private TsConverttaxorgDto initdto = new TsConverttaxorgDto();
	private List<TsTaxorgDto> taxorglist=new ArrayList<TsTaxorgDto>();
	private String smodicount;
	List filepath = null;
	public TsConverttaxorgBean() {
		super();
		filepath = new ArrayList();
		dto = new TsConverttaxorgDto();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		dto.setSorgcode(loginfo.getSorgcode());
		pagingcontext = new PagingContext(this);
		init();
	}

	/**
	 * Direction: 跳转录入界面 ename: goInput 引用方法: viewers: 录入界面 messages:
	 */
	public String goInput(Object o) {
		dto = new TsConverttaxorgDto();
		dto.setSorgcode(loginfo.getSorgcode());
		smodicount = null;
		return super.goInput(o);
	}

	/**
	 * Direction: 录入保存 ename: inputSave 引用方法: viewers: * messages:
	 * 
	 * @param stbstaxorgcode
	 * @param stbstaxorgcode
	 * @param stcbstaxorgcode
	 * @param stcbstaxorgcode
	 */
	public String inputSave(Object o) {
		if(null != smodicount && !"".equals(smodicount)) {
			dto.setImodicount(Integer.valueOf(smodicount));
		}		
		if (datacheck(dto, "input")) {
			return null;
		}
		try {
			
			tsConverttaxorgService.addInfo(dto);
			smodicount = null;
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsConverttaxorgDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 返回到维护界面 ename: backMaintenance 引用方法: viewers: 维护界面 messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsConverttaxorgDto();
		dto.setSorgcode(loginfo.getSorgcode());
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsConverttaxorgDto) o;
		initdto =  (TsConverttaxorgDto)dto.clone();
		return super.singleSelect(o);
	}
	/**
	 * Direction: 导出数据
	 * ename: dataexport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String dataexport(Object o){
    	// 选择保存路径
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
			return "";
		}
		String fileName = filePath+ File.separator+ "征收机关对应数据" +loginfo.getCurrentDate().replaceAll("-", "") + ".txt";
		try {
			TsConverttaxorgDto finddto = new TsConverttaxorgDto();
			if(!"000000000000".equals(loginfo.getSorgcode()))
				finddto.setSorgcode(loginfo.getSorgcode());
			List<TsConverttaxorgDto> result = commonDataAccessService.findRsByDto(finddto);
			if(null == result || result.size() == 0 ){
				MessageDialog.openMessageDialog(null, "没有需要导出的数据！");
				return null;
			}
			expdata(result,fileName);
			MessageDialog.openMessageDialog(null, "操作成功！");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
        return super.dataexport(o);
    }
    private void expdata(List<TsConverttaxorgDto> result ,String fileName) throws FileOperateException{
    	StringBuffer resultStr = new StringBuffer();
		for(TsConverttaxorgDto tmp : result){
			resultStr.append(tmp.getSorgcode() + ","); //核算主体代码
			resultStr.append(tmp.getStrecode() + ",");	//国库代码
			resultStr.append(tmp.getStbstaxorgcode() + ",");	//TBS征收机关
			resultStr.append(tmp.getStcbstaxorgcode() + ",");	//TIPS征收机关
			resultStr.append(tmp.getImodicount() + System.getProperty("line.separator"));	//征收机关性质
		}
		FileUtil.getInstance().writeFile(fileName, resultStr.toString());
    }
	/**
	 * Direction: 追加导入数据
	 * ename: adddataimport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String adddataimport(Object o){
    	try{
    	// 接口类型
			List<File> fileList = new ArrayList<File>();
			List<String> serverpathlist = new ArrayList<String>();
			// 判断是否选中文件
			if (null == filepath || filepath.size() == 0) {
				MessageDialog.openMessageDialog(null, "请选择要加载的文件！");
				return null;
			}
			if (filepath.size() > 1) {
				MessageDialog.openMessageDialog(null, "所选加载文件不能是多个！");
				return null;
			}
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			for (int i = 0; i < filepath.size(); i++) {
				File tmpfile = (File) filepath.get(i);
				// 文件的名字
				String tmpfilename = tmpfile.getName();
				// 获取文件的路径
				String tmpfilepath = tmpfile.getAbsolutePath();
				if(ChinaTest.isChinese(tmpfilepath))
				{
					MessageDialog.openMessageDialog(null, " 文件名或文件路径中不能有汉字！");
					return null;
				}
				if (!tmpfilename.trim().toLowerCase().endsWith(".txt")) {
					continue;
				}
				if (null == tmpfile || null == tmpfilename
						|| null == tmpfilepath) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, " 请选择要加载的文件！");
					return null;
				}
				// 文件上传并记录日志
				String serverpath = ClientFileTransferUtil.uploadFile(tmpfile
						.getAbsolutePath());
				fileList.add(new File(serverpath.replace("/", File.separator).replace("\\",File.separator)));
				serverpathlist.add(serverpath);
			}
			if(fileList!=null&&fileList.size()>0)
			{
				tsConverttaxorgService.dataimport(String.valueOf(fileList.get(0)), "addimport");
			}
			MessageDialog.openMessageDialog(null, " 追加导入数据成功！");
			return null;
    	}catch(Exception e)
    	{
    		MessageDialog.openErrorDialog(null, e);
    	}finally {
    		DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    	}
    	this.editor.fireModelChanged();
        return super.adddataimport(o);
    }
    
	/**
	 * Direction: 删除导入数据
	 * ename: deletedataimport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String deletedataimport(Object o){
    	try
    	{
	    	// 接口类型
			List<File> fileList = new ArrayList<File>();
			List<String> serverpathlist = new ArrayList<String>();
			// 判断是否选中文件
			if (null == filepath || filepath.size() == 0) {
				MessageDialog.openMessageDialog(null, "请选择要加载的文件！");
				return null;
			}
			if (filepath.size() > 1) {
				MessageDialog.openMessageDialog(null, "所选加载文件不能是多个！");
				return null;
			}
			if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(
					this.editor.getCurrentComposite().getShell(), "提示",
					"该操作会删除原有征收机关对照数据，请备份数据，是否继续导入？")) {
				return null;
			}
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			for (int i = 0; i < filepath.size(); i++) {
				File tmpfile = (File) filepath.get(i);
				// 文件的名字
				String tmpfilename = tmpfile.getName();
				// 获取文件的路径
				String tmpfilepath = tmpfile.getAbsolutePath();
				if(ChinaTest.isChinese(tmpfilepath))
				{
					MessageDialog.openMessageDialog(null, " 文件名或文件路径中不能有汉字！");
					return null;
				}
				if (!tmpfilename.trim().toLowerCase().endsWith(".txt")) {
					continue;
				}
				if (null == tmpfile || null == tmpfilename
						|| null == tmpfilepath) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, " 请选择要加载的文件！");
					return null;
				}
				// 文件上传并记录日志
				String serverpath = ClientFileTransferUtil.uploadFile(tmpfile
						.getAbsolutePath());
				fileList.add(new File(serverpath));
				serverpathlist.add(serverpath);
			}
			if(fileList!=null&&fileList.size()>0)
			{
				tsConverttaxorgService.dataimport(String.valueOf(fileList.get(0)), "deleteimport");
			}
			MessageDialog.openMessageDialog(null, " 删除导入数据成功！");
			return null;
    	}catch(Exception e)
    	{
    		MessageDialog.openErrorDialog(null, e);
    	}finally {
    		DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    	}
		
        return super.deletedataimport(o);
    }
	/**
	 * Direction: 删除 ename: delete 引用方法: viewers: * messages:
	 */
	public String delete(Object o) {
		if (dto.getStbstaxorgcode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		try {
			tsConverttaxorgService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsConverttaxorgDto();
		init();
		return super.backMaintenance(o);

	}

	/**
	 * Direction: 到修改界面 ename: goModify 引用方法: viewers: 修改界面 messages:
	 */
	public String goModify(Object o) {
		if (dto == null || dto.getStbstaxorgcode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		smodicount = dto.getImodicount().toString();
		return super.goModify(o);
	}

	/**
	 * Direction: 修改保存 ename: modifySave 引用方法: viewers: * messages:
	 * 
	 * @param stbstaxorgcode
	 * @param stbstaxorgcode
	 * @param stcbstaxorgcode
	 * @param stcbstaxorgcode
	 */
	public String modifySave(Object o) {
		if(null != smodicount && !"".equals(smodicount)) {
			dto.setImodicount(Integer.valueOf(smodicount));
		}
		if (datacheck(dto, "modify")) {
			return null;
		}
		try {
			tsConverttaxorgService.modInfo(initdto,dto);
			smodicount = null;
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsConverttaxorgDto();
		return super.backMaintenance(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest pageRequest) {
		TsConverttaxorgDto tmpdto = new TsConverttaxorgDto();
		tmpdto.setSorgcode(loginfo.getSorgcode());
		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(tmpdto,
					pageRequest, "1=1");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	private void init() {
		TsOrganDto orgdto = new TsOrganDto();
		orgdto.setSorgcode(loginfo.getSorgcode());

		try {
			list1 = commonDataAccessService.findRsByDto(orgdto);
			
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}

		TsTreasuryDto tredto = new TsTreasuryDto();
		tredto.setSorgcode(loginfo.getSorgcode());
		try {
			TsTaxorgDto taxorgdto=new TsTaxorgDto();
			taxorgdto.setSorgcode(loginfo.getSorgcode());
			taxorglist=commonDataAccessService.findRsByDto(taxorgdto, " ORDER BY S_TAXORGCODE");
			list2 = commonDataAccessService.findRsByDto(tredto);
			if(taxorglist!=null&&taxorglist.size()>0)
			{
				List<TsTaxorgDto> temp = taxorglist;
				taxorglist = new ArrayList<TsTaxorgDto>();
				Map<String,TsTaxorgDto> tempMap = new HashMap<String,TsTaxorgDto>();
				for(TsTaxorgDto tempdto:temp)
				{
					if(!tempMap.containsKey(tempdto.getStaxorgcode()))
					{
						if(tempdto.getStaxorgname()==null||"".equals(tempdto.getStaxorgname()))
							tempdto.setStaxorgname(tempdto.getStaxorgcode());
						tempMap.put(tempdto.getStaxorgcode(), tempdto);
						taxorglist.add(tempdto);
					}
				}
			}
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	private boolean datacheck(TsConverttaxorgDto dto, String operType) {
		if (null == dto.getSorgcode() || dto.getSorgcode().trim().length() == 0) {
			MessageDialog.openMessageDialog(null, "核算主体代码不能为空！");
			return true;
		} else if (null == dto.getStrecode() || dto.getStrecode().trim().length() == 0) {
			MessageDialog.openMessageDialog(null, "国库代码不能为空！");
			return true;
		} else if (null == dto.getStcbstaxorgcode() || dto.getStcbstaxorgcode().trim().length() == 0) {
			MessageDialog.openMessageDialog(null, "TIPS征收机关代码不能为空！");
			return true;
		} else if (null == dto.getImodicount()
				|| !(dto.getImodicount() == 0 || dto.getImodicount() == 1 || dto.getImodicount() == 2 || dto.getImodicount() == 3 || dto.getImodicount() == 4 || dto.getImodicount() == 5)) {
			MessageDialog.openMessageDialog(null, "征收机关性质错误！");
			return true;
		}
		
		if (null == dto.getSnationtaxorgcode() || dto.getSnationtaxorgcode().trim().length() == 0) {
			dto.setSnationtaxorgcode("N");
//			MessageDialog.openMessageDialog(null, "国税征收机关代码不能为空！");
//			return true;
		} 
		
		if (null == dto.getSareataxorgcode() || dto.getSareataxorgcode().trim().length() == 0) {
			dto.setSareataxorgcode("N");
//			MessageDialog.openMessageDialog(null, "地税征收机关代码不能为空！");
//			return true;
		} 

		if (null == dto.getStbstaxorgcode() || dto.getStbstaxorgcode().trim().length() == 0) {
			dto.setStbstaxorgcode("N");
//			Mess8ageDialog.openMessageDialog(null, "TBS征收机关代码不能为空！");
//			return true;
		}
		
		TsConverttaxorgDto tempdto = new TsConverttaxorgDto();
		tempdto.setSorgcode(dto.getSorgcode());
		tempdto.setStbstaxorgcode(dto.getStbstaxorgcode());
		tempdto.setStrecode(dto.getStrecode());
		tempdto.setStcbstaxorgcode(dto.getStcbstaxorgcode());

		List list = null;
		try {
			list = commonDataAccessService.findRsByDto(tempdto);
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return true;
		}
		if (operType.equals("input")) {
			if (null != list && list.size() > 0) {
				MessageDialog.openMessageDialog(null, "核算主体代码+国库主体代码+TBS征收机关代码+TIPS征收机关代码重复！");
				return true;
			} else {
				return false;
			}
		} else {
			if (null != list && list.size() > 1) {
				MessageDialog.openMessageDialog(null, "核算主体代码+国库代码+TBS征收机关代码+TIPS征收机关代码重复！");
				return true;
			} else {
				return false;
			}
		}
	}

	public List getList1() {
		return list1;
	}

	public void setList1(List list1) {
		this.list1 = list1;
	}

	public List getList2() {
		return list2;
	}

	public void setList2(List list2) {
		this.list2 = list2;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public List<TsTaxorgDto> getTaxorglist() {
		return taxorglist;
	}

	public void setTaxorglist(List<TsTaxorgDto> taxorglist) {
		this.taxorglist = taxorglist;
	}

	public String getSmodicount() {
		return smodicount;
	}

	public void setSmodicount(String smodicount) {
		this.smodicount = smodicount;
	}
	public String getFiledemo() {
		return filedemo;
	}

	public void setFiledemo(String filedemo) {
		this.filedemo = filedemo;
	}

	public List getFilepath() {
		return filepath;
	}

	public void setFilepath(List filepath) {
		this.filepath = filepath;
	}
	
	
}
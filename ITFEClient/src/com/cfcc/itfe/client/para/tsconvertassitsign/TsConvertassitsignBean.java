package com.cfcc.itfe.client.para.tsconvertassitsign;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.ChinaTest;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.persistence.dto.TsConvertassitsignDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;

/**
 * codecomment:
 * 
 * @author caoyg
 * @time 09-10-20 08:42:02 子系统: Para 模块:TsConvertassitsign 组件:TsConvertassitsign
 */
@SuppressWarnings("unchecked")
public class TsConvertassitsignBean extends AbstractTsConvertassitsignBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(TsConvertassitsignBean.class);
	private List list;
	private ITFELoginInfo loginfo = null;
	private TsConvertassitsignDto searchDto;
	List filepath = null;
	private String filedemo="导入文件为txt格式文件,文件名或文件路径中不能有汉字。文件中每行为一条数据，一条数据分5个字段，每个字段以逗号分隔。字段含义:核算主体代码,国库主体代码,预算科目代码,TBS辅助标志,TIPS辅助标志";
	public TsConvertassitsignBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		searchDto = new TsConvertassitsignDto();
		searchDto.setSorgcode(loginfo.getSorgcode());
		dto = new TsConvertassitsignDto();
		dto.setSorgcode(loginfo.getSorgcode());
		filepath = new ArrayList();
		pagingcontext = new PagingContext(this);
		init();
	}

	/**
	 * Direction: 跳转录入界面 ename: goInput 引用方法: viewers: 录入界面 messages:
	 */
	public String goInput(Object o) {
		dto = new TsConvertassitsignDto();
		dto.setSorgcode(loginfo.getSorgcode());
		return super.goInput(o);
	}

	/**
	 * Direction: 录入保存 ename: inputSave 引用方法: viewers: * messages:
	 * 
	 * @param stbsassitsign
	 * @param stbsassitsign
	 */
	public String inputSave(Object o) {
		if (datacheck(dto)) {
			return null;
		}
		try {
			tsConvertassitsignService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);

			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsConvertassitsignDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 返回到维护界面 ename: backMaintenance 引用方法: viewers: 维护界面 messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsConvertassitsignDto();
		dto.setSorgcode(loginfo.getSorgcode());
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsConvertassitsignDto) o;
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
		String fileName = filePath+ File.separator+ "辅助标志对应数据" +loginfo.getCurrentDate().replaceAll("-", "") + ".txt";
		try {
			TsConvertassitsignDto finddto = new TsConvertassitsignDto();
			if(!"000000000000".equals(loginfo.getSorgcode()))
				finddto.setSorgcode(loginfo.getSorgcode());
			List<TsConvertassitsignDto> result = commonDataAccessService.findRsByDto(finddto);
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
    private void expdata(List<TsConvertassitsignDto> result ,String fileName) throws FileOperateException{
    	StringBuffer resultStr = new StringBuffer();
		for(TsConvertassitsignDto tmp : result){
			resultStr.append(tmp.getSorgcode() + ","); //核算主体代码
			resultStr.append(tmp.getStrecode() + ",");	//国库代码
			resultStr.append(tmp.getSbudgetsubcode() + ",");	//预算科目代码
			resultStr.append(tmp.getStbsassitsign() + ",");	//TBS辅助标志
			resultStr.append(tmp.getStipsassistsign() + System.getProperty("line.separator"));	//TIPS辅助标志
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
				
				tsConvertassitsignService.dataimport(String.valueOf(fileList.get(0)), "addimport");
			}
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openMessageDialog(null, " 追加导入数据成功！");
			return null;
    	}catch(Exception e)
    	{
    		MessageDialog.openErrorDialog(null, e);
    	}
		
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
					"该操作会删除原有辅助标志数据，请备份数据，是否继续导入？")) {
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
				tsConvertassitsignService.dataimport(String.valueOf(fileList.get(0)), "deleteimport");
			}
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openMessageDialog(null, " 删除导入数据成功！");
			return null;
    	}catch(Exception e)
    	{
    		MessageDialog.openErrorDialog(null, e);
    	}
        return super.deletedataimport(o);
    }
	/**
	 * Direction: 删除 ename: delete 引用方法: viewers: * messages:
	 */
	public String delete(Object o) {
		if (dto.getStbsassitsign() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		try {
			tsConvertassitsignService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsConvertassitsignDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 到修改界面 ename: goModify 引用方法: viewers: 修改界面 messages:
	 */
	public String goModify(Object o) {
		if (dto == null || dto.getStbsassitsign() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}

		return super.goModify(o);
	}

	/**
	 * Direction: 修改保存 ename: modifySave 引用方法: viewers: * messages:
	 * 
	 * @param stbsassitsign
	 * @param stbsassitsign
	 */
	public String modifySave(Object o) {
		if (datacheck(dto)) {
			return null;
		}
		try {
			tsConvertassitsignService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsConvertassitsignDto();
		return super.backMaintenance(o);

	}

	private void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	
	/**
	 * Direction: 查询
	 * ename: search
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String search(Object o){
    	init();
    	PageResponse pageResponse=pagingcontext.getPage();
    	if(pageResponse.getTotalCount()==0)
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录！");		
        editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
        return "";
    }
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest pageRequest) {
		
		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(searchDto,
					pageRequest, "1=1");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	private boolean datacheck(TsConvertassitsignDto dto) {
		if (null == dto.getSorgcode() || dto.getSorgcode().trim().length() == 0) {
			MessageDialog.openMessageDialog(null, "核算主体代码不能为空！");
			return true;
		} else if (null == dto.getStbsassitsign()
				|| dto.getStbsassitsign().trim().length() == 0) {
			MessageDialog.openMessageDialog(null, "横联辅助标志不能为空！");
			return true;

		} else if (null == dto.getStipsassistsign()
				|| dto.getStipsassistsign().trim().length() == 0) {
			MessageDialog.openMessageDialog(null, "TIPS辅助标志不能为空！");
			return true;
		} else if (dto.getStipsassistsign().trim().length() != 35) {
			MessageDialog.openMessageDialog(null, "TIPS辅助标志必须为35位！");
			return true;
		}
		return false;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public TsConvertassitsignDto getSearchDto() {
		return searchDto;
	}

	public void setSearchDto(TsConvertassitsignDto searchDto) {
		this.searchDto = searchDto;
	}

	public List getFilepath() {
		return filepath;
	}

	public void setFilepath(List filepath) {
		this.filepath = filepath;
	}

	public String getFiledemo() {
		return filedemo;
	}

	public void setFiledemo(String filedemo) {
		this.filedemo = filedemo;
	}
	
	
}
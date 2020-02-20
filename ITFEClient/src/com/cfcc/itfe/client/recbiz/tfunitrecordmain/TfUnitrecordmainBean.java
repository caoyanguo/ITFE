package com.cfcc.itfe.client.recbiz.tfunitrecordmain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.persistence.dto.TfUnitrecordmainDto;
import com.cfcc.itfe.persistence.dto.TfUnitrecordsubDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author Administrator
 * @time   14-09-22 09:54:37
 * 子系统: RecBiz
 * 模块:TfUnitrecordmain
 * 组件:TfUnitrecordmain
 */
public class TfUnitrecordmainBean extends AbstractTfUnitrecordmainBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TfUnitrecordmainBean.class);
    private ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
    private ITFELoginInfo logInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    private TfUnitrecordsubBean subBean;
    public TfUnitrecordmainBean() {
      super();
      searchDto = new TfUnitrecordmainDto();
      searchDto.setSorgcode(logInfo.getSorgcode());
      detailDto = new TfUnitrecordmainDto();
      pagingContext = new PagingContext(this);
      subSearchDto = new TfUnitrecordsubDto();
      subBean = new TfUnitrecordsubBean();
    }
    
	/**
	 * Direction: 查询
	 * ename: searchDtoList
	 * 引用方法: 
	 * viewers: 查询结果
	 * messages: 
	 */
    public String searchDtoList(Object o){
    	PageRequest pageRequest = new PageRequest();
		try {
			PageResponse p = this.retrieve(pageRequest);
			if (p == null || p.getPageRowCount() == 0) {
				MessageDialog.openMessageDialog(null, "查询数据不存在!");
				return null;
			}
			pagingContext.setPage(p);
		} catch (Exception e) {
			log.error("查询数据错误！", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.searchDtoList(o);
    }

	/**
	 * Direction: 返回查询
	 * ename: backToSearch
	 * 引用方法: 
	 * viewers: 查询条件
	 * messages: 
	 */
    public String backToSearch(Object o){
    	searchDto = new TfUnitrecordmainDto();
    	searchDto.setSorgcode(logInfo.getSorgcode());
        return super.backToSearch(o);
    }

	/**
	 * Direction: 返回查询结果
	 * ename: backToResult
	 * 引用方法: 
	 * viewers: 查询结果
	 * messages: 
	 */
    public String backToResult(Object o){
          return super.backToResult(o);
    }

	/**
	 * Direction: 打开明细
	 * ename: toDetail
	 * 引用方法: 
	 * viewers: 明细信息
	 * messages: 
	 */
    public String toDetail(Object o){
          return super.toDetail(o);
    }

	/**
	 * Direction: 返回查询结果
	 * ename: detailToResult
	 * 引用方法: 
	 * viewers: 查询结果
	 * messages: 
	 */
    public String detailToResult(Object o){
          return super.detailToResult(o);
    }

	/**
	 * Direction: 单选
	 * ename: singleSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	if(o instanceof TfUnitrecordmainDto){
    		subSearchDto.setIvousrlno(((TfUnitrecordmainDto)o).getIvousrlno());
    	}
    	return super.singleSelect(o);
    }

	/**
	 * Direction: 双击
	 * ename: doubleClick
	 * 引用方法: 
	 * viewers: 子表查询结果
	 * messages: 
	 */
    public String doubleClick(Object o){
    	if(o instanceof TfUnitrecordmainDto){
    		subSearchDto.setIvousrlno(((TfUnitrecordmainDto)o).getIvousrlno());
    	}
    	return subQueryResult(o);
    }

	/**
	 * Direction: 子表列表返回主表列表
	 * ename: subBackToSearch
	 * 引用方法: 
	 * viewers: 查询结果
	 * messages: 
	 */
    public String subBackToSearch(Object o){
    	subSearchDto = new TfUnitrecordsubDto();
        return super.subBackToSearch(o);
    }

	/**
	 * Direction: 查询到子表结果页面
	 * ename: subQueryResult
	 * 引用方法: 
	 * viewers: 子表查询结果
	 * messages: 
	 */
    public String subQueryResult(Object o){
    	if (subSearchDto == null || subSearchDto.getIvousrlno() == null) {
			MessageDialog.openMessageDialog(null,"请选择一条记录查询明细信息！");
			return null;
		}
		try {
			String isJumpNext = subBean.searchDtoList(subSearchDto);
			if (isJumpNext != null) {
				return super.subQueryResult(o);
			} else {
				MessageDialog.openMessageDialog(null,"未查询到明细信息！");
				return null;
			}
		} catch (Exception e) {
			log.info("查询明细信息", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
    }
    
    /**
	 * Direction: 法人代码导出
	 * ename: dataExport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String dataExport(Object o){
    	// 选择保存路径
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent().getActiveShell());
		String filePath = path.open();
		List<String> filelist = new ArrayList<String>();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
			return "";
		}
		String clientFilePath;
		String strdate = DateUtil.date2String2(new java.util.Date()); // 当前系统年月日
		String dirsep = File.separator;
		String serverFilePath;
		try {
			serverFilePath = tfUnitrecordmainService.legalPersonCodeExport(searchDto).replace("\\","/");
			int j = serverFilePath.lastIndexOf("/");
			String partfilepath  = serverFilePath.replaceAll(serverFilePath.substring(0, j + 1), "");
			clientFilePath = filePath + dirsep + partfilepath;
			File f = new File(clientFilePath);
			if(f.exists()){
				f.delete();
			}
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
    
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest request) {
    	PageResponse response = new PageResponse(request);
		try {
			String wheresql = " 1=1 ";
			response = commonDataAccessService.findRsByDtoWithWherePaging(searchDto, request, wheresql);
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			this.editor.fireModelChanged();
		}
		return response;
	}

	public TfUnitrecordsubBean getSubBean() {
		return subBean;
	}

	public void setSubBean(TfUnitrecordsubBean subBean) {
		this.subBean = subBean;
	}

}
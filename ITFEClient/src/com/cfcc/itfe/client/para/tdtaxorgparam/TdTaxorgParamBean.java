package com.cfcc.itfe.client.para.tdtaxorgparam;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TdTaxorgParamDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author ZZD
 * @time   13-03-01 10:54:40
 * 子系统: Para
 * 模块:TdTaxorgParam
 * 组件:TdTaxorgParam
 */
public class TdTaxorgParamBean extends AbstractTdTaxorgParamBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TdTaxorgParamBean.class);
    private ITFELoginInfo loginfo = null;
    private String staxorgcode=null;
    public TdTaxorgParamBean() {
      super();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      dto = new TdTaxorgParamDto();
      finddto = new TdTaxorgParamDto();
      finddto.setSbookorgcode(loginfo.getSorgcode());
      pagingcontext = new PagingContext(this);
                  
    }
    
	/**
	 * Direction: 跳转录入界面
	 * ename: goInput
	 * 引用方法: 
	 * viewers: 录入界面
	 * messages: 
	 */
    public String goInput(Object o){
    	dto = new TdTaxorgParamDto();
    	dto.setSbookorgcode(loginfo.getSorgcode());
		dto.setTssysupdate(new Timestamp(System.currentTimeMillis()));
          return super.goInput(o);
    }

	/**
	 * Direction: 录入保存
	 * ename: inputSave
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String inputSave(Object o){
    	try {
    		
    		tdTaxorgParamService.addInfo(dto);
    		
   		}catch(ITFEBizException e){
   			log.error(e);
   			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e);
   			return "";
   		}catch (Throwable e) {
   			log.error(e);
   			MessageDialog.openErrorDialog(null, e);   			
   			return super.inputSave(o);
   		}
   		MessageDialog.openMessageDialog(null,StateConstant.INPUTSAVE);
   		dto = new TdTaxorgParamDto();
   		dto.setSbookorgcode(loginfo.getSorgcode());
		dto.setTssysupdate(new Timestamp(System.currentTimeMillis()));
   		editor.fireModelChanged();
   		return backMaintenance(o);
    }

	/**
	 * Direction: 返回到维护界面
	 * ename: backMaintenance
	 * 引用方法: 
	 * viewers: 维护界面
	 * messages: 
	 */
    public String backMaintenance(Object o){
    	dto = new TdTaxorgParamDto();
    	searchRs(null);
        return super.backMaintenance(o);
    }

	/**
	 * Direction: 单选
	 * ename: singleSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	dto = (TdTaxorgParamDto) o;
          return super.singleSelect(o);
    }

	/**
	 * Direction: 删除
	 * ename: delete
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if (dto.getIseqno()== null) {
			MessageDialog.openMessageDialog(null,StateConstant.DELETESELECT);
			return "";
		}
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定删除选中的记录吗？")) {
			return "";
		}
    	try {
    		tdTaxorgParamService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TdTaxorgParamDto();
		editor.fireModelChanged();
		return this.searchRs(o);
    }

	/**
	 * Direction: 到修改界面
	 * ename: goModify
	 * 引用方法: 
	 * viewers: 修改界面
	 * messages: 
	 */
    public String goModify(Object o){
    	if (dto.getSbookorgcode() == null||dto.getIseqno()==null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
    	staxorgcode	= dto.getStaxorgcode();
        return super.goModify(o);
    }

	/**
	 * Direction: 修改保存
	 * ename: modifySave
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String modifySave(Object o){
    	try {
    		TdTaxorgParamDto finddto=new TdTaxorgParamDto();
    		finddto.setSbookorgcode(dto.getSbookorgcode());
    		finddto.setStaxorgcode(dto.getStaxorgcode());
			List<TdTaxorgParamDto> list=commonDataAccessService.findRsByDto(finddto);
			if(list!=null&&list.size()>0&&!(list.get(0).getStaxorgcode().equals(staxorgcode))){				
				MessageDialog.openMessageDialog(null, "不能修改征收机关代码为："+dto.getStaxorgcode()+"，\n征收机关代码："+dto.getStaxorgcode()+"已存在！");			
			    return "";
			}		
    		dto.setTssysupdate(new Timestamp(System.currentTimeMillis()));
    		tdTaxorgParamService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TdTaxorgParamDto();
		editor.fireModelChanged();
		return super.backMaintenance(o);
    }

	/**
	 * Direction: 查询
	 * ename: searchRs
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String searchRs(Object o){
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		if(pageResponse.getData().size()<=0){
			MessageDialog.openMessageDialog(null,"没有查询到符合条件的数据");
//			return this.rebackSearch(o);
		}
		pagingcontext.setPage(pageResponse);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.searchRs(o);
    }

	/**
	 * Direction: 返回查询界面
	 * ename: rebackSearch
	 * 引用方法: 
	 * viewers: 查询界面
	 * messages: 
	 */
    public String rebackSearch(Object o){
    	finddto = new TdTaxorgParamDto();
    	finddto.setSbookorgcode(loginfo.getSorgcode());
    	dto = new TdTaxorgParamDto();
    	searchRs(null);
          return super.rebackSearch(o);
    }
    
    
	public String expfile(Object o) {
		// 选择保存路径
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
			return "";
		}
		String fileName = filePath+ File.separator+ loginfo.getSorgcode() + "_征收机关代码表 .csv";
		try {
			List<TdTaxorgParamDto> result = commonDataAccessService.findRsByDto(finddto);
			if(null == result || result.size() == 0 ){
				MessageDialog.openMessageDialog(null, "没有需要导出的数据！");
				return null;
			}
			export(result, fileName);
			MessageDialog.openMessageDialog(null, "操作成功！");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.expfile(o);
	}
	private void export(List<TdTaxorgParamDto> resultlist,String filepath ) throws FileOperateException{
		String separator = ",";
		StringBuffer result = new StringBuffer();
		result.append("核算主体代码_CHARACTER_ NOT NULL,征收机关代码_VARCHAR_NOT NULL,征收机关名称_VARCHAR_NOT NULL,征收机关性质_CHARACTER_NOT NULL,征收机关电话_VARCHAR,征收机关简码_VARCHAR,系统更新时间\n");
		for(TdTaxorgParamDto tmp : resultlist){
			result.append(tmp.getSbookorgcode() + separator);	//核算主体代码
			result.append(tmp.getStaxorgcode() + separator);	//征收机关代码
			result.append(tmp.getStaxorgname() + separator);	//征收机关名称
			result.append(tmp.getCtaxorgprop() + separator);	//征收机关性质
			result.append((tmp.getStaxorgphone()==null?"":tmp.getStaxorgphone())+ separator );	//征收机关电话
			result.append((tmp.getStaxorgsht()==null?"":tmp.getStaxorgsht()) + separator);	//征收机关简码
			result.append(tmp.getTssysupdate() );	//系统更新时间
			result.append("\n");
		}
		FileUtil.getInstance().writeFile(filepath, result.toString());
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	try{
    		return commonDataAccessService.findRsByDtoWithWherePaging(finddto,
					pageRequest, "1=1");
    	} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
    	return super.retrieve(pageRequest);
	}

}
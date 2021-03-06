package com.cfcc.itfe.client.para.tdbookacctmain;

import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.TdBookacctMainDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author ZZD
 * @time   13-03-04 16:53:28
 * 子系统: Para
 * 模块:TdBookacctMain
 * 组件:TdBookacctMain
 */
public class TdBookacctMainBean extends AbstractTdBookacctMainBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TdBookacctMainBean.class);
    private ITFELoginInfo loginfo;
    public TdBookacctMainBean() {
      super();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      dto = new TdBookacctMainDto();
      finddto = new TdBookacctMainDto();
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
    	dto = new TdBookacctMainDto();
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
    		dto.setTssysupdate(new Timestamp(System.currentTimeMillis()));
    		tdBookacctMainService.addInfo(dto);
    		
   		} catch (Throwable e) {
   			log.error(e);
   			MessageDialog.openErrorDialog(null, e);
   			
   			return super.inputSave(o);
   		}
   		MessageDialog.openMessageDialog(null,StateConstant.INPUTSAVE);
   		dto = new TdBookacctMainDto();
   		editor.fireModelChanged();
          return super.inputSave(o);
    }

	/**
	 * Direction: 返回到维护界面
	 * ename: backMaintenance
	 * 引用方法: 
	 * viewers: 维护界面
	 * messages: 
	 */
    public String backMaintenance(Object o){
    	dto = new TdBookacctMainDto();
          return super.backMaintenance(o);
    }

	/**
	 * Direction: 单选
	 * ename: singleSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	dto = (TdBookacctMainDto)o;
          return super.singleSelect(o);
    }

	/**
	 * Direction: 删除
	 * ename: delete
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if (dto.getSbookorgcode()== null || dto.getSbookacct() == null) {
			MessageDialog.openMessageDialog(null,StateConstant.DELETESELECT);
			return "";
		}
    	try {
    		tdBookacctMainService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TdBookacctMainDto();
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
    	if (dto.getSbookorgcode() == null||dto.getSbookacct()==null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
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
    		dto.setTssysupdate(new Timestamp(System.currentTimeMillis()));
    		tdBookacctMainService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TdBookacctMainDto();
		editor.fireModelChanged();
		return super.backMaintenance(o);
    }

	/**
	 * Direction: 查询
	 * ename: searchRs
	 * 引用方法: 
	 * viewers: 维护界面
	 * messages: 
	 */
    public String searchRs(Object o){
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
//		if(pageResponse.getData().size()<=0){
//			MessageDialog.openMessageDialog(null,"没有查询到符合条件的数据");
//			return this.rebackSearch(o);
//		}
		pagingcontext.setPage(pageResponse);
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
    	dto = new TdBookacctMainDto();
    	finddto = new TdBookacctMainDto();
          return super.rebackSearch(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	try{
    		finddto.setSbookorgcode(loginfo.getSorgcode());
    		return commonDataAccessService.findRsByDtoWithWherePaging(finddto,
					pageRequest, "1=1");
    	} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

}
package com.cfcc.itfe.client.para.tsmankeymode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsMankeymodeDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author Administrator
 * @time   12-06-25 13:31:34
 * 子系统: Para
 * 模块:tsmankeymode
 * 组件:TsMankeymode
 */
public class TsMankeymodeBean extends AbstractTsMankeymodeBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TsMankeymodeBean.class);
    private ITFELoginInfo loginfo;
    public TsMankeymodeBean() {
      super();
      dto = new TsMankeymodeDto();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
      pagingContext = new PagingContext(this);
      init(); 
    }
    
	/**
	 * Direction: 转到密钥模式修改
	 * ename: toKeymodemodify
	 * 引用方法: 
	 * viewers: 密钥模式修改
	 * messages: 
	 */
    public String toKeymodemodify(Object o){
    	if(!StateConstant.ORG_CENTER_CODE.equals(loginfo.getSorgcode())) {
    		MessageDialog.openMessageDialog(null, "权限不足，该功能由中心管理员维护!");
    		return null;
    	}
    	if(null==dto||null==dto.getSkeymode())
    	{
    		MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
    		return null;
    	}
          return super.toKeymodemodify(o);
    }

	/**
	 * Direction: 密钥模式修改
	 * ename: keymodeModify
	 * 引用方法: 
	 * viewers: 密钥模式列表
	 * messages: 
	 */
    public String keymodeModify(Object o){
    	if(null==dto.getSkeymode()||dto.getSkeymode().equals(""))
		{
			MessageDialog.openMessageDialog(null,StateConstant.CHECKVALID);
			return null;
		}
    	try{
			tsMankeymodeService.keymodeModify(dto);
			MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		} catch (ITFEBizException e) {
			log.error(e);
			org.eclipse.jface.dialogs.MessageDialog.openError(null, null, StateConstant.MODIFYFAIL);
			return null;
		}
		dto=new TsMankeymodeDto();
		init();
        return super.keymodeModify(o);

    }

	/**
	 * Direction: 转到密钥模式列表
	 * ename: toKeylist
	 * 引用方法: 
	 * viewers: 密钥模式列表
	 * messages: 
	 */
    public String toKeylist(Object o){
    	 dto = new TsMankeymodeDto();
    	 init();
          return super.toKeylist(o);
    }

	/**
	 * Direction: 单击选中对象
	 * ename: clickSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String clickSelect(Object o){
    	dto=(TsMankeymodeDto) o;
          return super.clickSelect(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	try {
			return tsMankeymodeService.keyList(dto, pageRequest);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null,e);
		}
		return super.retrieve(pageRequest);

	}

    
    public void init()
    {
    	PageRequest pageRequest=new PageRequest();
    	PageResponse pageResponse=retrieve(pageRequest);
    	pagingContext.setPage(pageResponse);
    }

}
package com.cfcc.itfe.client.para.voucherauto;

import java.util.List;

import org.apache.commons.logging.*;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;

/**
 * codecomment: 
 * @author db2admin
 * @time   11-08-01 09:16:05
 * 子系统: Para
 * 模块:voucherAuto
 * 组件:VoucherAuto
 */
public class VoucherAutoBean extends AbstractVoucherAutoBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(VoucherAutoBean.class);
	private ITFELoginInfo loginfo = null;
    private TsVouchercommitautoDto searchdto;

	public VoucherAutoBean() {
      super();
      voucherAutodto = new TsVouchercommitautoDto();
      searchdto = new TsVouchercommitautoDto();
      pagingcontext = new PagingContext(null);
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		voucherAutodto.setSorgcode(loginfo.getSorgcode());
		pagingcontext = new PagingContext(this);
		init();
    }
	private void init() {
		voucherAutodto.setSorgcode(this.getLoginfo().getSorgcode());
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		List<TsVouchercommitautoDto> list = pageResponse.getData();
		if(list!=null&&list.size()>0)
		{
			for(TsVouchercommitautoDto temp:list)
			{
				if(temp.getSreturbacknauto()==null)
					temp.setSreturbacknauto("");
			}
		}
		pagingcontext.setPage(pageResponse);
	}
    
	/**
	 * Direction: 跳转录入界面
	 * ename: goInput
	 * 引用方法: 
	 * viewers: 录入界面
	 * messages: 
	 */
    public String goInput(Object o){
    	voucherAutodto = new TsVouchercommitautoDto();
    	voucherAutodto.setSorgcode(loginfo.getSorgcode());
        return super.goInput(o);
    }
    /**
	 * 校验字段属性
	 * 
	 * @param idto
	 * @return
	 */
	public String verifyPorp(TsVouchercommitautoDto idto) {
		if (null == idto) {
			return "要操作的纪录为空，请确认！";
		}

		if (null == idto.getStrecode() || "".equals(idto.getStrecode().trim())
				|| idto.getStrecode().trim().length() != 10) {
			return "国库代码必须填写，且必须为10位！";
		}

		if (null == idto.getSvtcode() || "".equals(idto.getSvtcode())) {
			return "凭证代码不能为空！";
		}
		return null;
	}
	/**
	 * Direction: 录入保存
	 * ename: inputSave
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String inputSave(Object o){
    	String verifyRs = verifyPorp(voucherAutodto);
		if (null != verifyRs) {
			MessageDialog.openMessageDialog(null, verifyRs);
			return null;
		}
		try {
			if(voucherAutodto.getScommitauto()==null)
				voucherAutodto.setScommitauto("");
			if(voucherAutodto.getSstampauto()==null)
				voucherAutodto.setSstampauto("");
			voucherAutoService.addInfo(voucherAutodto);
			voucherAutodto = new TsVouchercommitautoDto();
			init();
			MessageDialog.openMessageDialog(null,StateConstant.INPUTSAVE);
			this.editor.fireModelChanged();
		
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
			return super.inputSave(o);
		}
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
    	voucherAutodto = new TsVouchercommitautoDto();
		init();
		editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
          return super.backMaintenance(o);
    }

	/**
	 * Direction: 单选
	 * ename: singleSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	voucherAutodto = (TsVouchercommitautoDto) o;
          return super.singleSelect(o);
    }

	/**
	 * Direction: 删除
	 * ename: delete
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if (null == voucherAutodto || null == voucherAutodto.getStrecode()) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return null;
		}

		boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
				null, "删除数据确认!", "是否确认要删除这条记录！");
		if (flag) {
			try {
				voucherAutoService.delInfo(voucherAutodto);
			} catch (Throwable e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
				return super.delete(o);
			}
			MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
			voucherAutodto = new TsVouchercommitautoDto();
		}else{
			voucherAutodto = new TsVouchercommitautoDto();
		}

		init();
		editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
          return super.delete(o);
    }

	/**
	 * Direction: 到修改界面
	 * ename: goModify
	 * 引用方法: 
	 * viewers: 修改界面
	 * messages: 
	 */
    public String goModify(Object o){
    	if (null == voucherAutodto || null == voucherAutodto.getSvtcode()) {
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
    	String verifyRs = verifyPorp(voucherAutodto);
		if (null != verifyRs) {
			MessageDialog.openMessageDialog(null, verifyRs);
			return null;
		}
		try {
			if(voucherAutodto.getScommitauto()==null)
				voucherAutodto.setScommitauto("");
			if(voucherAutodto.getSstampauto()==null)
				voucherAutodto.setSstampauto("");
			voucherAutoService.modInfo(voucherAutodto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		voucherAutodto = new TsVouchercommitautoDto();
		editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
          return super.modifySave(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {
    	if (null == searchdto) {
    		searchdto = new TsVouchercommitautoDto();
		}

    	searchdto.setSorgcode(this.getLoginfo().getSorgcode());

		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(searchdto,
					arg0, "1=1");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(arg0);
	}

    public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}
}
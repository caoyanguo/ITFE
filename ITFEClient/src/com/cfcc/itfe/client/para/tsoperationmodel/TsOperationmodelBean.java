package com.cfcc.itfe.client.para.tsoperationmodel;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.FileDialog;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsOperationmodelDto;
import com.cfcc.itfe.persistence.dto.TsOperationtypeDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author caoyg
 * @time 09-10-19 21:24:31 子系统: Para 模块:TsOperationmodel 组件:TsOperationmodel
 */
public class TsOperationmodelBean extends AbstractTsOperationmodelBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(TsOperationmodelBean.class);
	private List list2;
	private String sisuse;
	private String soperationtypecode;

	public List getList2() {
		return list2;
	}

	public void setList2(List list2) {
		this.list2 = list2;
	}

	public String getSisuse() {
		return sisuse;
	}

	public void setSisuse(String sisuse) {
		this.sisuse = sisuse;
	}

	public String getSoperationtypecode() {
		return soperationtypecode;
	}

	public void setSoperationtypecode(String soperationtypecode) {
		this.soperationtypecode = soperationtypecode;
	}

	public TsOperationmodelBean() {
		super();
		dto = new TsOperationmodelDto();
		pagingcontext = new PagingContext(this);
		init();
	}

	/**
	 * Direction: 跳转录入界面 ename: goInput 引用方法: viewers: 录入界面 messages:
	 */
	public String goInput(Object o) {
		dto = new TsOperationmodelDto();
		return super.goInput(o);
	}

	/**
	 * Direction: 录入保存 ename: inputSave 引用方法: viewers: * messages:
	 */
	public String inputSave(Object o) {

		dto.setSisuse(getSisuse());
		dto.setSoperationtypecode(getSoperationtypecode());
		try {
			tsOperationmodelService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsOperationmodelDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 返回到维护界面 ename: backMaintenance 引用方法: viewers: 维护界面 messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsOperationmodelDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsOperationmodelDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: 删除 ename: delete 引用方法: viewers: * messages:
	 */
	public String delete(Object o) {
		if (dto == null || dto.getSmodelid() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(editor.getCurrentComposite().getShell(), "提示", "是否删除模版" + dto.getSoperationtypecode())){
			return "";
		}
		try {
			tsOperationmodelService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsOperationmodelDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 到修改界面 ename: goModify 引用方法: viewers: 修改界面 messages:
	 */
	public String goModify(Object o) {
		if (dto.getSmodelid() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		setSisuse(dto.getSisuse());
		setSoperationtypecode(dto.getSoperationtypecode());
		return super.goModify(o);
	}

	/**
	 * Direction: 修改保存 ename: modifySave 引用方法: viewers: * messages:
	 */
	public String modifySave(Object o) {
		dto.setSisuse(getSisuse());
		dto.setSoperationtypecode(getSoperationtypecode());
		try {
			tsOperationmodelService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsOperationmodelDto();
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

		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(dto,
					pageRequest, "1=1");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	private void init() {
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
				.getDefault().getLoginInfo();
		TsOperationtypeDto operdto = new TsOperationtypeDto();
		try {
			list2 = commonDataAccessService.findRsByDto(operdto);

		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	/**
	 * 模版上载
	 */
	public String uploadModel(Object o) {
		if (dto.getSisuse().charAt(0) != '1'){
			//只有电子凭证才需要上载模版，其他凭证不需要
			MessageDialog.openMessageDialog(null, "只有电子凭证才需要模版，其他凭证不需要模版。");
			return "";
		}
		FileDialog dialog = new FileDialog(editor.getCurrentComposite().getShell(),0);
		dialog.setFilterExtensions(new String[]{"*.*"});
		if (dialog.open() == null){
			MessageDialog.openMessageDialog(null, "请选择要上载的模版文件。");
			return "";
		}
		String path = dialog.getFilterPath();
		String fileName = dialog.getFileName();
		//模版文件上载
    	try {
			String fileUploadPath = ClientFileTransferUtil.uploadFile(path + "\\" + fileName);
			String fileSavePath = tsOperationmodelService.uploadModel(fileUploadPath);
			dto.setSmodelsavepath(fileSavePath);
			editor.fireModelChanged();
			MessageDialog.openMessageDialog(null, "模版文件上载完毕，请注意保存。");
    	}catch(Throwable e){
    		log.error(e);
    		MessageDialog.openErrorDialog(null, e);
    		return "";
    	}
		
		return super.uploadModel(o);
	}
}
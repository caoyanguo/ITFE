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
 * @time 09-10-19 21:24:31 ��ϵͳ: Para ģ��:TsOperationmodel ���:TsOperationmodel
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
	 * Direction: ��ת¼����� ename: goInput ���÷���: viewers: ¼����� messages:
	 */
	public String goInput(Object o) {
		dto = new TsOperationmodelDto();
		return super.goInput(o);
	}

	/**
	 * Direction: ¼�뱣�� ename: inputSave ���÷���: viewers: * messages:
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
	 * Direction: ���ص�ά������ ename: backMaintenance ���÷���: viewers: ά������ messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsOperationmodelDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsOperationmodelDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: ɾ�� ename: delete ���÷���: viewers: * messages:
	 */
	public String delete(Object o) {
		if (dto == null || dto.getSmodelid() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(editor.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ɾ��ģ��" + dto.getSoperationtypecode())){
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
	 * Direction: ���޸Ľ��� ename: goModify ���÷���: viewers: �޸Ľ��� messages:
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
	 * Direction: �޸ı��� ename: modifySave ���÷���: viewers: * messages:
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
	 * ģ������
	 */
	public String uploadModel(Object o) {
		if (dto.getSisuse().charAt(0) != '1'){
			//ֻ�е���ƾ֤����Ҫ����ģ�棬����ƾ֤����Ҫ
			MessageDialog.openMessageDialog(null, "ֻ�е���ƾ֤����Ҫģ�棬����ƾ֤����Ҫģ�档");
			return "";
		}
		FileDialog dialog = new FileDialog(editor.getCurrentComposite().getShell(),0);
		dialog.setFilterExtensions(new String[]{"*.*"});
		if (dialog.open() == null){
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص�ģ���ļ���");
			return "";
		}
		String path = dialog.getFilterPath();
		String fileName = dialog.getFileName();
		//ģ���ļ�����
    	try {
			String fileUploadPath = ClientFileTransferUtil.uploadFile(path + "\\" + fileName);
			String fileSavePath = tsOperationmodelService.uploadModel(fileUploadPath);
			dto.setSmodelsavepath(fileSavePath);
			editor.fireModelChanged();
			MessageDialog.openMessageDialog(null, "ģ���ļ�������ϣ���ע�Ᵽ�档");
    	}catch(Throwable e){
    		log.error(e);
    		MessageDialog.openErrorDialog(null, e);
    		return "";
    	}
		
		return super.uploadModel(o);
	}
}
package com.cfcc.itfe.client.para.tsoperationplace;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsOperationformDto;
import com.cfcc.itfe.persistence.dto.TsOperationmodelDto;
import com.cfcc.itfe.persistence.dto.TsOperationplaceDto;
import com.cfcc.itfe.persistence.dto.TsStamptypeDto;
import com.cfcc.itfe.service.ITFELoginInfo;

/**
 * codecomment:
 * 
 * @author caoyg
 * @time 09-10-20 08:42:02 子系统: Para 模块:TsOperationplace 组件:TsOperationplace
 */
public class TsOperationplaceBean extends AbstractTsOperationplaceBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(TsOperationplaceBean.class);
	private String smodelid;
	private String sformid;
	private String splacetype;
	private String sstamptypecode;
	private String sisuse;
	private String sbeforestatus;
	private String safterstatus;
	private List list1;
	private List list2;
	private List list3;
	private List list4;

	public List getList4() {
		return list4;
	}

	public void setList4(List list4) {
		this.list4 = list4;
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

	public List getList3() {
		return list3;
	}

	public void setList3(List list3) {
		this.list3 = list3;
	}

	public String getSmodelid() {
		return smodelid;
	}

	public void setSmodelid(String smodelid) {
		this.smodelid = smodelid;
	}

	public String getSformid() {
		return sformid;
	}

	public void setSformid(String sformid) {
		this.sformid = sformid;
	}

	public String getSplacetype() {
		return splacetype;
	}

	public void setSplacetype(String splacetype) {
		this.splacetype = splacetype;
	}

	public String getSstamptypecode() {
		return sstamptypecode;
	}

	public void setSstamptypecode(String sstamptypecode) {
		this.sstamptypecode = sstamptypecode;
	}

	public String getSisuse() {
		return sisuse;
	}

	public void setSisuse(String sisuse) {
		this.sisuse = sisuse;
	}

	public String getSbeforestatus() {
		return sbeforestatus;
	}

	public void setSbeforestatus(String sbeforestatus) {
		this.sbeforestatus = sbeforestatus;
	}

	public String getSafterstatus() {
		return safterstatus;
	}

	public void setSafterstatus(String safterstatus) {
		this.safterstatus = safterstatus;
	}

	public TsOperationplaceBean() {
		super();
		dto = new TsOperationplaceDto();
		pagingcontext = new PagingContext(this);
		init();
	}

	/**
	 * Direction: 跳转录入界面 ename: goInput 引用方法: viewers: 录入界面 messages:
	 */
	public String goInput(Object o) {
		dto = new TsOperationplaceDto();
		return super.goInput(o);
	}

	/**
	 * Direction: 录入保存 ename: inputSave 引用方法: viewers: * messages:
	 */
	public String inputSave(Object o) {
		dto.setSmodelid(getSmodelid());
		dto.setSformid(getSformid());
		dto.setSplacetype(getSplacetype());
		dto.setSstamptypecode(getSstamptypecode());
		dto.setSisuse(getSisuse());
		dto.setSbeforestatus(getSbeforestatus());
		dto.setSbeforestatus(getSafterstatus());
		try {
			tsOperationplaceService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsOperationplaceDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 返回到维护界面 ename: backMaintenance 引用方法: viewers: 维护界面 messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsOperationplaceDto();
		init();
		setSmodelid("");
		setSformid("");
		setSplacetype("");
		setSstamptypecode("");
		setSisuse("");
		setSbeforestatus("");
		setSafterstatus("");
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsOperationplaceDto) o;
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
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(editor.getCurrentComposite().getShell(), "提示", "是否删除盖章位置" + dto.getSplaceid())){
			return "";
		}
		try {
			tsOperationplaceService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsOperationplaceDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 到修改界面 ename: goModify 引用方法: viewers: 修改界面 messages:
	 */
	public String goModify(Object o) {
		setSmodelid(dto.getSmodelid());
		setSformid(dto.getSformid());
		setSplacetype(dto.getSplacetype());
		setSstamptypecode(dto.getSstamptypecode());
		setSisuse(dto.getSisuse());
		setSbeforestatus(dto.getSbeforestatus());
		setSafterstatus(dto.getSafterstatus());
		if (dto == null || dto.getSformid() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		return super.goModify(o);
	}

	/**
	 * Direction: 修改保存 ename: modifySave 引用方法: viewers: * messages:
	 */
	public String modifySave(Object o) {
		dto.setSmodelid(getSmodelid());
		dto.setSformid(getSformid());
		dto.setSplacetype(getSplacetype());
		dto.setSstamptypecode(getSstamptypecode());
		dto.setSisuse(getSisuse());
		dto.setSbeforestatus(getSbeforestatus());
		dto.setSbeforestatus(getSafterstatus());
		try {
			tsOperationplaceService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		if (dto.getSmodelid() == null || dto.getSformid() == null) {
			return null;
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsOperationplaceDto();
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
		TsOperationmodelDto operationdto = new TsOperationmodelDto();
		try {
			list1 = commonDataAccessService.findRsByDto(operationdto);

		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		TsOperationformDto opfdto = new TsOperationformDto();
		try {
			list2 = commonDataAccessService.findRsByDto(opfdto);

		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		TsStamptypeDto sttdto = new TsStamptypeDto();
		try {
			list3 = commonDataAccessService.findRsByDto(sttdto);

		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}

		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}
}
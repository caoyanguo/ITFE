package com.cfcc.itfe.client.para.tsconverttobank;

import java.util.List;

import org.apache.commons.logging.*;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsConverttobankDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.service.ITFELoginInfo;

/**
 * codecomment:
 * 
 * @author caoyg
 * @time 09-10-20 08:42:02 ��ϵͳ: Para ģ��:TsConverttobank ���:TsConverttobank
 */
public class TsConverttobankBean extends AbstractTsConverttobankBean implements IPageDataProvider {

	private static Log log = LogFactory.getLog(TsConverttobankBean.class);
	private List list;
	private ITFELoginInfo loginfo = null;

	public TsConverttobankBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
		dto = new TsConverttobankDto();
		dto.setSorgcode(loginfo.getSorgcode());
		pagingcontext = new PagingContext(this);
		init();
	}

	/**
	 * Direction: ��ת¼����� ename: goInput ���÷���: viewers: ¼����� messages:
	 */
	public String goInput(Object o) {
		dto = new TsConverttobankDto();
		dto.setSorgcode(loginfo.getSorgcode());
		return super.goInput(o);
	}

	/**
	 * Direction: ¼�뱣�� ename: inputSave ���÷���: viewers: * messages:
	 * 
	 * @param sorgcode
	 */
	public String inputSave(Object o) {
		if(null == dto.getSorgcode() || "".equals(dto.getSorgcode().trim())
				|| null == dto.getSrecvunit() || "".equals(dto.getSrecvunit().trim())
				|| null == dto.getStrecode() || "".equals(dto.getStrecode().trim())
				|| null == dto.getStobank() || "".equals(dto.getStobank().trim())
				|| null == dto.getSagentbank() || "".equals(dto.getSagentbank().trim())){
			MessageDialog.openMessageDialog(null, StateConstant.CHECKVALID);
			return null;
		}

		try {
			tsConverttobankService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);

			return super.inputSave(o);
		}

		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsConverttobankDto();
		dto.setSorgcode(loginfo.getSorgcode());
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ���ص�ά������ ename: backMaintenance ���÷���: viewers: ά������ messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsConverttobankDto();
		dto.setSorgcode(loginfo.getSorgcode());
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsConverttobankDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: ɾ�� ename: delete ���÷���: viewers: * messages:
	 */
	public String delete(Object o) {
		if(null == dto || null == dto.getSrecvunit()){
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return super.delete(o);
		}
		
		try {
			tsConverttobankService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsConverttobankDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ���޸Ľ��� ename: goModify ���÷���: viewers: �޸Ľ��� messages:
	 */
	public String goModify(Object o) {
		if (dto == null || null == dto.getSrecvunit()) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		return super.goModify(o);
	}

	/**
	 * Direction: �޸ı��� ename: modifySave ���÷���: viewers: * messages:
	 * 
	 * @param sorgcode
	 */
	public String modifySave(Object o) {
//		if(null == dto.getStobank().trim() || "".equals(dto.getStobank().trim())){
//			MessageDialog.openMessageDialog(null, "ת�����в���Ϊ��");
//			return null;
//		}
		if(null == dto.getSorgcode() || "".equals(dto.getSorgcode().trim())
				|| null == dto.getSrecvunit() || "".equals(dto.getSrecvunit().trim())
				|| null == dto.getStrecode() || "".equals(dto.getStrecode().trim())
				|| null == dto.getStobank() || "".equals(dto.getStobank().trim())
				|| null == dto.getSagentbank() || "".equals(dto.getSagentbank().trim())){
			MessageDialog.openMessageDialog(null, StateConstant.CHECKVALID);
			return null;
		}
		
		try {
			tsConverttobankService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}

		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsConverttobankDto();
		dto.setSorgcode(loginfo.getSorgcode());
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
		dto.setSorgcode(loginfo.getSorgcode());
		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(dto, pageRequest, "1=1");
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
			list = commonDataAccessService.findRsByDto(orgdto);

		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}

		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

}

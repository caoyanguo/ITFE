package com.cfcc.itfe.client.para.tsconverttaxorgforzj;

import java.util.*;

import org.apache.commons.logging.*;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;

/**
 * 功能：浙江征收机关对照表
 * @author hejianrong
 * @time   14-07-29 10:44:15
 * 子系统: Para
 * 模块:TsConverttaxorgForZj
 * 组件:TsConverttaxorgForZj
 */
public class TsConverttaxorgForZjBean extends AbstractTsConverttaxorgForZjBean implements IPageDataProvider {

	private static Log log = LogFactory.getLog(TsConverttaxorgForZjBean.class);
	private List list1;
	private List list2;
	private ITFELoginInfo loginfo = null;
	private TsConverttaxorgDto initdto = new TsConverttaxorgDto();

	public TsConverttaxorgForZjBean() {
		super();
		dto = new TsConverttaxorgDto();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		dto.setSorgcode(loginfo.getSorgcode());
		pagingcontext = new PagingContext(this);
		init();
	}

	/**
	 * Direction: 跳转录入界面 ename: goInput 引用方法: viewers: 录入界面 messages:
	 */
	public String goInput(Object o) {
		dto = new TsConverttaxorgDto();
		dto.setSorgcode(loginfo.getSorgcode());
		return super.goInput(o);
	}

	/**
	 * Direction: 录入保存 ename: inputSave 引用方法: viewers: * messages:
	 * 
	 * @param stbstaxorgcode
	 * @param stbstaxorgcode
	 * @param stcbstaxorgcode
	 * @param stcbstaxorgcode
	 */
	public String inputSave(Object o) {

		if (datacheck(dto, "input")) {
			return null;
		}
		try {
			tsConverttaxorgForZjService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsConverttaxorgDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 返回到维护界面 ename: backMaintenance 引用方法: viewers: 维护界面 messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsConverttaxorgDto();
		dto.setSorgcode(loginfo.getSorgcode());
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsConverttaxorgDto) o;
		initdto =  (TsConverttaxorgDto)dto.clone();
		return super.singleSelect(o);
	}

	/**
	 * Direction: 删除 ename: delete 引用方法: viewers: * messages:
	 */
	public String delete(Object o) {
		if (dto.getStbstaxorgcode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		try {
			tsConverttaxorgForZjService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsConverttaxorgDto();
		init();
		return super.backMaintenance(o);

	}

	/**
	 * Direction: 到修改界面 ename: goModify 引用方法: viewers: 修改界面 messages:
	 */
	public String goModify(Object o) {
		if (dto == null || dto.getStbstaxorgcode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		return super.goModify(o);
	}

	/**
	 * Direction: 修改保存 ename: modifySave 引用方法: viewers: * messages:
	 * 
	 * @param stbstaxorgcode
	 * @param stbstaxorgcode
	 * @param stcbstaxorgcode
	 * @param stcbstaxorgcode
	 */
	public String modifySave(Object o) {
		if (datacheck(dto, "modify")) {
			return null;
		}
		try {
			tsConverttaxorgForZjService.modInfo(initdto,dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsConverttaxorgDto();
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
		TsConverttaxorgDto tmpdto = new TsConverttaxorgDto();
		tmpdto.setSorgcode(loginfo.getSorgcode());
		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(tmpdto,
					pageRequest, "1=1");
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
			list1 = commonDataAccessService.findRsByDto(orgdto);

		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}

		TsTreasuryDto tredto = new TsTreasuryDto();
		tredto.setSorgcode(loginfo.getSorgcode());
		try {
			list2 = commonDataAccessService.findRsByDto(tredto);

		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	private boolean datacheck(TsConverttaxorgDto dto, String operType) {
		if (null == dto.getSorgcode() || dto.getSorgcode().trim().length() == 0) {
			MessageDialog.openMessageDialog(null, "核算主体代码不能为空！");
			return true;
		} else if (null == dto.getStrecode() || dto.getStrecode().trim().length() == 0) {
			MessageDialog.openMessageDialog(null, "国库代码不能为空！");
			return true;
		} else if (null == dto.getStcbstaxorgcode() || dto.getStcbstaxorgcode().trim().length() == 0) {
			MessageDialog.openMessageDialog(null, "TCBS征收机关代码不能为空！");
			return true;
		} else if (null == dto.getImodicount()
				|| !(dto.getImodicount() == 1 || dto.getImodicount() == 2 || dto.getImodicount() == 3 || dto.getImodicount() == 4)) {
			MessageDialog.openMessageDialog(null, "征收机关性质错误！");
			return true;
		}
		
		if (null == dto.getSnationtaxorgcode() || dto.getSnationtaxorgcode().trim().length() == 0) {
			dto.setSnationtaxorgcode("N");
//			MessageDialog.openMessageDialog(null, "国税征收机关代码不能为空！");
//			return true;
		} 
		
		if (null == dto.getSareataxorgcode() || dto.getSareataxorgcode().trim().length() == 0) {
			dto.setSareataxorgcode("N");
//			MessageDialog.openMessageDialog(null, "地税征收机关代码不能为空！");
//			return true;
		} 

		if (null == dto.getStbstaxorgcode() || dto.getStbstaxorgcode().trim().length() == 0) {
			dto.setStbstaxorgcode("N");
//			Mess8ageDialog.openMessageDialog(null, "TBS征收机关代码不能为空！");
//			return true;
		} 
		
		TsConverttaxorgDto tempdto = new TsConverttaxorgDto();
		tempdto.setSorgcode(dto.getSorgcode());
		tempdto.setStcbstaxorgcode(dto.getStcbstaxorgcode());
		tempdto.setStrecode(dto.getStrecode());

		List list = null;
		try {
			list = commonDataAccessService.findRsByDto(tempdto);
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return true;
		}
		if (operType.equals("input")) {
			if (null != list && list.size() > 0) {
				MessageDialog.openMessageDialog(null, "核算主体代码+国库代码+TCBS征收机关代码重复！");
				return true;
			} else {
				return false;
			}
		} else {
			if (null != list && list.size() > 1) {
				MessageDialog.openMessageDialog(null, "核算主体代码+国库代码+TCBS征收机关代码重复！");
				return true;
			} else {
				return false;
			}
		}
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

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}
}
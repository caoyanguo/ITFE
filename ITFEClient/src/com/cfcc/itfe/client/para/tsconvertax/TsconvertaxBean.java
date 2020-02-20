package com.cfcc.itfe.client.para.tsconvertax;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.para.tsconvertax.ITsconvertaxService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsConvertaxDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.pk.TsConvertaxPK;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 13-04-16 14:55:14 子系统: Para 模块:tsconvertax 组件:Tsconvertax
 */
public class TsconvertaxBean extends AbstractTsconvertaxBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsconvertaxBean.class);
	private ITFELoginInfo loginfo;

	public TsconvertaxBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		searchDto = new TsConvertaxDto();
		searchDto.setSorgcode(loginfo.getSorgcode());
		detailDto = new TsConvertaxDto();
		oriDto = new TsConvertaxDto();
		searchresult = new ArrayList();
		init();
	}

	private void init() {
		try {
			searchresult.addAll(commonDataAccessService.findRsByDto(searchDto));
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "初始化信息失败！");
		}

	}

	/**
	 * Direction: 表格单击 ename: singleclick 引用方法: viewers: * messages:
	 */
	public String singleclick(Object o) {
		detailDto = (TsConvertaxDto) o;
		oriDto = (TsConvertaxDto) ((TsConvertaxDto) o).clone();
		return super.singleclick(o);
	}

	/**
	 * Direction: 返回默认页面 ename: gomainview 引用方法: viewers: * messages:
	 */
	public String gomainview(Object o) {
		detailDto = new TsConvertaxDto();
		return super.gomainview(o);
	}

	/**
	 * Direction: 删除 ename: del 引用方法: viewers: * messages:
	 */
	public String del(Object o) {
		if (null == detailDto || StringUtils.isBlank(detailDto.getStaxcode())) {
			MessageDialog.openMessageDialog(null, "请选择需要删除的信息！");
			return null;
		}
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否确认删除？")) {
			try {
				tsconvertaxService.delInfo(detailDto);
				MessageDialog.openMessageDialog(null, "操作成功！");
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "删除信息失败！");
			} finally {
				detailDto = new TsConvertaxDto();
				search(o);
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
			}
		}
		return super.del(o);
	}

	/**
	 * Direction: 跳转录入页面 ename: goaddview 引用方法: viewers: 信息录入 messages:
	 */
	public String goaddview(Object o) {
		detailDto = new TsConvertaxDto();
		detailDto.setSorgcode(loginfo.getSorgcode());
		return super.goaddview(o);
	}

	/**
	 * Direction: 跳转更新页面 ename: gomodview 引用方法: viewers: 信息修改 messages:
	 */
	public String gomodview(Object o) {
		if(null == detailDto || StringUtils.isBlank(detailDto.getStaxcode())){
			MessageDialog.openMessageDialog(null, "请选择需要修改的信息！");
			return null;
		}
		return super.gomodview(o);
	}

	/**
	 * Direction: 查询 ename: search 引用方法: viewers: * messages:
	 */
	public String search(Object o) {
		try {
			searchresult = new ArrayList();
			searchresult.addAll(commonDataAccessService.findRsByDto(searchDto));
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "查询信息失败！");
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		return super.search(o);
	}

	/**
	 * Direction: 添加信息 ename: addInfo 引用方法: viewers: 查询 messages:
	 */
	public String addInfo(Object o) {
		try {
			TsConvertaxPK convertaxPK = new TsConvertaxPK();
			convertaxPK.setSorgcode(loginfo.getSorgcode());
			convertaxPK.setStaxcode(detailDto.getStaxcode());
			TsConvertaxDto tmpDto = (TsConvertaxDto) commonDataAccessService
					.find(convertaxPK);
			if(null == tmpDto){
				tsconvertaxService.addInfo(detailDto);
			}else{
				MessageDialog.openMessageDialog(null, "横联征收机关代码["
						+ detailDto.getStaxcode() + "]已经存在！");
				return null;
			}
			detailDto = new TsConvertaxDto();
			MessageDialog.openMessageDialog(null, "操作成功！");
			search(o);
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "录入信息失败！");
		}
		return super.addInfo(o);
	}

	/**
	 * Direction: 修改信息 ename: modInfo 引用方法: viewers: 查询 messages:
	 */
	public String modInfo(Object o) {
		try {
			tsconvertaxService.modInfo(detailDto, oriDto);
			MessageDialog.openMessageDialog(null, "操作成功！");
			detailDto = new TsConvertaxDto();
			search(o);
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "修改信息失败！");
		}
		return super.modInfo(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

}
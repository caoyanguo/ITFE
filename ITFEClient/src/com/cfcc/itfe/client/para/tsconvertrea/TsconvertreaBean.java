package com.cfcc.itfe.client.para.tsconvertrea;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsConvertreaDto;
import com.cfcc.itfe.persistence.pk.TsConvertreaPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 13-04-16 12:22:22 子系统: Para 模块:tsconvertrea 组件:Tsconvertrea
 */
public class TsconvertreaBean extends AbstractTsconvertreaBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsconvertreaBean.class);
	private ITFELoginInfo loginfo;
	private List trelist;
	private TsConvertreaDto oridto;

	public TsconvertreaBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		searchDto = new TsConvertreaDto();
		searchDto.setSorgcode(loginfo.getSorgcode());
		detailDto = new TsConvertreaDto();
		searchResult = new ArrayList();
		oridto = new TsConvertreaDto();
		init();
	}

	private void init() {
		try {
			trelist = TrelistByOrgcode.getTreList(loginfo.getSorgcode(),
					commonDataAccessService);
			searchResult.addAll(commonDataAccessService.findRsByDto(searchDto));
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "初始化信息失败！");
		}

	}

	/**
	 * Direction: 表格单击 ename: singleclick 引用方法: viewers: * messages:
	 */
	public String singleclick(Object o) {
		detailDto = (TsConvertreaDto) o;
		oridto = (TsConvertreaDto) ((TsConvertreaDto) o).clone();
		return super.singleclick(o);
	}

	/**
	 * Direction: 查询 ename: search 引用方法: viewers: * messages:
	 */
	public String search(Object o) {
		try {
			searchResult = new ArrayList();
			searchResult.addAll(commonDataAccessService.findRsByDto(searchDto));
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "查询信息失败！");
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		return super.search(o);
	}

	/**
	 * Direction: 删除 ename: del 引用方法: viewers: * messages:
	 */
	public String del(Object o) {
		if (null == detailDto  || StringUtils.isBlank(detailDto.getStrecode())) {
			MessageDialog.openMessageDialog(null, "请选择需要删除的信息！");
			return null;
		}
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否确认删除？")) {
			try {
				tsconvertreaService.delInfo(detailDto);
				MessageDialog.openMessageDialog(null, "操作成功！");
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "删除信息失败！");
			} finally {
				detailDto = new TsConvertreaDto();
				search(o);
				this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
			}
		}
		return super.del(o);
	}

	/**
	 * Direction: 跳转修改信息页面 ename: gomodview 引用方法: viewers: 修改 messages:
	 */
	public String gomodview(Object o) {
		if (null == detailDto || StringUtils.isBlank(detailDto.getStrecode())) {
			MessageDialog.openMessageDialog(null, "请选择需要修改的信息！");
			return null;
		}
		return super.gomodview(o);
	}

	/**
	 * Direction: 跳转添加信息页面 ename: goaddview 引用方法: viewers: 新增 messages:
	 */
	public String goaddview(Object o) {
		detailDto = new TsConvertreaDto();
		detailDto.setSorgcode(loginfo.getSorgcode());
		return super.goaddview(o);
	}

	/**
	 * Direction: 返回默认界面 ename: gomainview 引用方法: viewers: 查询 messages:
	 */
	public String gomainview(Object o) {
		detailDto = new TsConvertreaDto();
		return super.gomainview(o);
	}

	/**
	 * Direction: 添加信息 ename: addInfo 引用方法: viewers: 查询 messages:
	 */
	public String addInfo(Object o) {
		try {
			TsConvertreaPK tsConvertreaPK = new TsConvertreaPK();
			tsConvertreaPK.setSorgcode(loginfo.getSorgcode());
			tsConvertreaPK.setStrecode(detailDto.getStrecode());
			TsConvertreaDto tmpDto = (TsConvertreaDto) commonDataAccessService
					.find(tsConvertreaPK);
			if (null == tmpDto) {
				tsconvertreaService.addInfo(detailDto);
			} else {
				MessageDialog.openMessageDialog(null, "横联国库代码["
						+ detailDto.getStrecode() + "]已经存在！");
				return null;
			}
			detailDto = new TsConvertreaDto();
			MessageDialog.openMessageDialog(null, "操作成功！");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "录入信息失败！");
		}

		search(o);
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		return super.addInfo(o);
	}

	/**
	 * Direction: 修改信息 ename: modInfo 引用方法: viewers: 查询 messages:
	 */
	public String modInfo(Object o) {
		try {
			tsconvertreaService.modInfo(detailDto,oridto);
			MessageDialog.openMessageDialog(null, "操作成功！");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "修改信息失败！");
		} finally {
			detailDto = new TsConvertreaDto();
			search(o);
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
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

	public List getTrelist() {
		return trelist;
	}

	public void setTrelist(List trelist) {
		this.trelist = trelist;
	}

}
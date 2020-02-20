package com.cfcc.itfe.client.para.tspayacctinfo;

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
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.para.tspayacctinfo.ITspayacctinfoService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsPayacctinfoDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment:
 * 
 * @author King
 * @time 13-06-25 14:56:44 子系统: Para 模块:tspayacctinfo 组件:Tspayacctinfo
 */
public class TspayacctinfoBean extends AbstractTspayacctinfoBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TspayacctinfoBean.class);
	private ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService) getService(ICommonDataAccessService.class);
	private ITFELoginInfo loginfo;
	private List list2;
	private TsPayacctinfoDto modTmpDto;

	public TspayacctinfoBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		pagingcontext = new PagingContext(this);
		searchDto = new TsPayacctinfoDto();
		searchDto.setSorgcode(loginfo.getSorgcode());
		detailDto = new TsPayacctinfoDto();
		TsTreasuryDto tredto = new TsTreasuryDto();
		tredto.setSorgcode(loginfo.getSorgcode());
		try {
			list2 = commonDataAccessService.findRsByDto(tredto);
		} catch (Throwable e) {
			log.error("取机构对应国库代码出错", e);
			return;
		}
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	/**
	 * Direction: 表格点击 ename: singleclick 引用方法: viewers: * messages:
	 */
	public String singleclick(Object o) {
		detailDto = (TsPayacctinfoDto) o;
		return super.singleclick(o);
	}

	/**
	 * Direction: 跳转录入 ename: goinput 引用方法: viewers: 信息录入 messages:
	 */
	public String goinput(Object o) {
		detailDto = new TsPayacctinfoDto();
		detailDto.setSorgcode(loginfo.getSorgcode());
		return super.goinput(o);
	}

	/**
	 * Direction: 跳转修改 ename: gomod 引用方法: viewers: 修改信息 messages:
	 */
	public String gomod(Object o) {
		if (null == detailDto || StringUtils.isBlank(detailDto.getSorgcode())
				|| StringUtils.isBlank(detailDto.getSgenbankcode())) {
			MessageDialog.openMessageDialog(null, "请选择需要修改的信息！");
			return null;
		}
		modTmpDto = (TsPayacctinfoDto) detailDto.clone();
		return super.gomod(o);
	}

	/**
	 * Direction: 删除信息 ename: del 引用方法: viewers: * messages:
	 */
	public String del(Object o) {
		if (null == detailDto || StringUtils.isBlank(detailDto.getSorgcode())
				|| StringUtils.isBlank(detailDto.getSgenbankcode())) {
			MessageDialog.openMessageDialog(null, "请选择需要修改的信息！");
			return null;
		}
		boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
				null, "删除数据确认!", "是否确认要删除这条记录！");
		if (flag) {
			try {
				tspayacctinfoService.del(detailDto);
			} catch (Throwable e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
				return null;
			}
			MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
			detailDto = new TsPayacctinfoDto();
		}
		init();
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		return super.del(o);
	}

	/**
	 * Direction: 保存 ename: save 引用方法: viewers: 参数查询一览表 messages:
	 */
	public String save(Object o) {
		TsPayacctinfoDto tmpDto = new TsPayacctinfoDto();
		tmpDto.setSorgcode(detailDto.getSorgcode());
		tmpDto.setSgenbankcode(detailDto.getSgenbankcode());
		tmpDto.setStrecode(detailDto.getStrecode());
		tmpDto.setSpayeracct(detailDto.getSpayeracct());
		tmpDto.setSpayeeacct(detailDto.getSpayeeacct());
		if(null==detailDto.getSgenbankcode()||"".equals(detailDto.getSgenbankcode())){
			MessageDialog.openMessageDialog(null, "代理银行行号不能为空！");
			return null;
		}
		try {
			if(tspayacctinfoService.verifyPayeeBankNo(detailDto.getSgenbankcode())){
				List list = commonDataAccessService.findRsByDto(tmpDto);
				if(null == list || list .size() == 0){
					tspayacctinfoService.save(detailDto);
					MessageDialog.openMessageDialog(null, "操作成功！");
				}else{
					MessageDialog.openMessageDialog(null, "核算主体代码：" + detailDto.getSorgcode() + ";国库代码：" + detailDto.getStrecode() + ";代理银行行号:" + detailDto.getSgenbankcode() + ";付款人帐户:" + detailDto.getSpayeracct() + "重复！");
					return null;
				}
			}else{
				MessageDialog.openMessageDialog(null, "代理银行行号:" + detailDto.getSgenbankcode() + "支付系统行号中不存在！");
				return null;
			}
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			detailDto = new TsPayacctinfoDto();
			return null;
		}
		init();
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		return super.save(o);
	}

	/**
	 * Direction: 返回 ename: gomain 引用方法: viewers: 参数查询一览表 messages:
	 */
	public String gomain(Object o) {
		init();
		detailDto = new TsPayacctinfoDto();
		this.editor.fireModelChanged();
		return super.gomain(o);
	}

	/**
	 * Direction: 修改信息 ename: mod 引用方法: viewers: 参数查询一览表 messages:
	 */
	public String mod(Object o) {
		
		try {
			tspayacctinfoService.mod(modTmpDto, detailDto);
			MessageDialog.openMessageDialog(null, "操作成功！");
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
//			detailDto = new TsPayacctinfoDto();
			return null;
		}
		init();
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		return super.mod(o);
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
			return commonDataAccessService.findRsByDtoWithWherePaging(
					searchDto, pageRequest, null);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	public List getList2() {
		return list2;
	}

	public void setList2(List list2) {
		this.list2 = list2;
	}

}
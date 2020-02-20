package com.cfcc.itfe.client.dataquery.settleaccounts;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dataquery.bizdatacollect.BizDataCountBean;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.DetailTsUsers;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 13-03-24 20:53:58 子系统: DataQuery 模块:settleAccounts 组件:SettleAccounts
 */
public class SettleAccountsBean extends AbstractSettleAccountsBean implements
		IPageDataProvider {

	private DetailTsUsers searchDto;
	private DetailTsUsers detailDto;
	private static Log log = LogFactory.getLog(SettleAccountsBean.class);
	private ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService) getService(ICommonDataAccessService.class);
	private String sorgname;
	private String susertype;
	private String suserstatus;
	private String sorglevel;
	private List resultList;
	private String detailorgname;
	private String detailorglevel;
	private String sloginstatus;
	private List<Mapper> orglist;

	public SettleAccountsBean() {
		super();
		orglist = new ArrayList<Mapper>();
		detailTsUsersDto = new TsUsersDto();
		searchDto = new DetailTsUsers();
		detailDto = new DetailTsUsers();
		resultList = new ArrayList();
		init();

	}

	private void init() {
		orglist.addAll(BizDataCountBean.getOrgLists(((ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo()).getSorgcode(), commonDataAccessService));
		
	}

	/**
	 * Direction: 查询 ename: search 引用方法: viewers: * messages:
	 */
	public String search(Object o) {
		setSearchDtoContent();
		try {
			resultList.clear();
			resultList = settleAccountsService.search(searchDto);
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "查询用户信息失败！");
		} finally {
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		}
		return super.search(o);
	}
	
	private void setSearchDtoContent(){
		searchDto = new DetailTsUsers();
		searchDto.setSorglevel(sorglevel);
		searchDto.setSuserstatus(suserstatus);
		searchDto.setSorgname(sorgname);
		searchDto.setSusertype(susertype);
		searchDto.setSloginstatus(sloginstatus);
	}

	/**
	 * Direction: 返回 ename: returnback 引用方法: viewers: 查询 messages:
	 */
	public String returnback(Object o) {
		return super.returnback(o);
	}

	/**
	 * Direction: 双击 ename: doubleclick 引用方法: viewers: 用户明细 messages:
	 */
	public String doubleclick(Object o) {
		tableclick(o);
		return super.doubleclick(o);
	}
	
	
	

	@Override
	public String singleClick(Object o) {
		tableclick(o);
		return super.singleClick(o);
	}

	@Override
	public String searchnextleve(Object o) {
		setSearchDtoContent();
		if(null == detailDto || StringUtils.isBlank(detailDto.getSorgcode())){
			MessageDialog.openMessageDialog(null, "请选择一条记录！");
			return null;
		}
		if("5".equals(detailorglevel)){	//县库没有下级
			MessageDialog.openMessageDialog(null, "县级库没有下级！");
			return null;
		}
		try {
			List tmplist = settleAccountsService.searchByLeve(detailTsUsersDto.getSorgcode(), String.valueOf((Integer.valueOf(detailorglevel) + 1)), searchDto);
			if(null == tmplist || tmplist.size() == 0){
				MessageDialog.openMessageDialog(null, "没有找到下级机构信息！");
				return null;
			}
			resultList.clear();
			resultList.addAll(tmplist);
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "查询用户信息失败！");
		} finally {
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		}
		detailDto = null;
		return super.searchnextleve(o);
	}

	@Override
	public String searchupleve(Object o) {
		setSearchDtoContent();
		if(null == detailDto || StringUtils.isBlank(detailDto.getSorgcode())){
			MessageDialog.openMessageDialog(null, "请选择一条记录！");
			return null;
		}
		if("2".equals(detailorglevel)){	//省库没有下级
			MessageDialog.openMessageDialog(null, "省库没有上级！");
			return null;
		}
		try {
			List tmplist = settleAccountsService.searchByLeve(detailTsUsersDto.getSorgcode(), String.valueOf((Integer.valueOf(detailorglevel) - 1)), searchDto);
			if(null == tmplist || tmplist.size() == 0){
				MessageDialog.openMessageDialog(null, "没有找到上级机构信息！");
				return null;
			}
			resultList.clear();
			resultList.addAll(tmplist);
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "查询用户信息失败！");
		} finally {
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		}
		detailDto = null;
		return super.searchupleve(o);
	}

	private void tableclick(Object o){
		detailDto = (DetailTsUsers) o;
		detailTsUsersDto = new TsUsersDto();
		detailTsUsersDto.setSorgcode(detailDto.getSorgcode());
		detailTsUsersDto.setSusercode(detailDto.getSusercode());
		detailTsUsersDto.setSusername(detailDto.getSusername());
		detailTsUsersDto.setSuserstatus(detailDto.getSuserstatus());
		detailTsUsersDto.setSusertype(detailDto.getSusertype());
		detailTsUsersDto.setSloginstatus(detailDto.getSloginstatus());
		detailTsUsersDto.setSpassmoddate(detailDto.getSpassmoddate());
		detailorglevel = detailDto.getSorglevel();
		detailorgname = detailDto.getSorgname();
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

	public DetailTsUsers getSearchDto() {
		return searchDto;
	}

	public void setSearchDto(DetailTsUsers searchDto) {
		this.searchDto = searchDto;
	}

	public DetailTsUsers getDetailDto() {
		return detailDto;
	}

	public void setDetailDto(DetailTsUsers detailDto) {
		this.detailDto = detailDto;
	}

	public String getSorgname() {
		return sorgname;
	}

	public void setSorgname(String sorgname) {
		this.sorgname = sorgname;
	}

	public String getSusertype() {
		return susertype;
	}

	public void setSusertype(String susertype) {
		this.susertype = susertype;
	}

	public String getSuserstatus() {
		return suserstatus;
	}

	public void setSuserstatus(String suserstatus) {
		this.suserstatus = suserstatus;
	}

	public String getSorglevel() {
		return sorglevel;
	}

	public void setSorglevel(String sorglevel) {
		this.sorglevel = sorglevel;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public String getDetailorgname() {
		return detailorgname;
	}

	public void setDetailorgname(String detailorgname) {
		this.detailorgname = detailorgname;
	}

	public String getDetailorglevel() {
		return detailorglevel;
	}

	public void setDetailorglevel(String detailorglevel) {
		this.detailorglevel = detailorglevel;
	}

	public List<Mapper> getOrglist() {
		return orglist;
	}

	public void setOrglist(List<Mapper> orglist) {
		this.orglist = orglist;
	}

	public String getSloginstatus() {
		return sloginstatus;
	}

	public void setSloginstatus(String sloginstatus) {
		this.sloginstatus = sloginstatus;
	}
	
	

}
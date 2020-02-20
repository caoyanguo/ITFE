package com.cfcc.itfe.client.dataquery.remindinfosearch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.dataquery.remindinfosearch.IRemindInfoSearchService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 15-03-26 15:14:56 子系统: DataQuery 模块:remindInfoSearch
 *       组件:RemindInfoSearch
 */
public class RemindInfoSearchBean extends AbstractRemindInfoSearchBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(RemindInfoSearchBean.class);
	private ITFELoginInfo loginInfo;
	private String treCode;

	public RemindInfoSearchBean() {
		super();
		voucherType = "";
		remindList = new ArrayList();
		remindIs = "";
		voucherSourceList = new ArrayList();
		voucherSource = "";
		pagingcontext = new PagingContext(this);
		loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		// commonDataAccessService.getSysDBDate()
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
			acctDate = new Date(simpleDateFormat.parse(
					commonDataAccessService.getSysDBDate()).getTime());
			Mapper mapper01 = new Mapper();
			mapper01.setUnderlyValue("1"); // 是
			mapper01.setDisplayValue("是");
			Mapper mapper00 = new Mapper();
			mapper00.setUnderlyValue("0"); // 否
			mapper00.setDisplayValue("否");
//			remindIs = "1";
			remindList.add(mapper00);
			remindList.add(mapper01);
			voucherTypeList = new itferesourcepackage.SVoucherTypeEnumFactory()
					.getEnums(null);
			if (null != voucherTypeList || voucherTypeList.size() > 0) {
				voucherType = (String) ((Mapper) voucherTypeList.get(0))
						.getUnderlyValue();
			}
			setVoucherType("");
		} catch (ITFEBizException e) {
			log.error("获取系统日期失败！", e);
			return;
		} catch (ParseException e) {
			log.error("获取系统日期失败！", e);
			return;
		}
	}
	
	/**
	 * Direction: 查询提醒信息
	 * ename: searchInfo
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String searchInfo(Object o){
    	List params = new ArrayList();
        params.add(getTreCode());
        params.add(DateUtil.date2String2(getAcctDate()));
        params.add(getRemindIs());
        params.add(getVoucherSource());
        params.add(getVoucherType());
        try {
			serverResult = remindInfoSearchService.searchInfo(params);
			if (null == serverResult || serverResult.size() == 0){
				serverResult = new ArrayList();
				MessageDialog.openMessageDialog(null, "没有查询到对应的数据信息！");
				return null;
			}
		} catch (ITFEBizException e) {
			log.error("查询数据异常！",e);
			MessageDialog.openErrorDialog(null, e);
		}finally{
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		}
        return super.searchInfo(o);
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

	public String getTreCode() {
		return treCode;
	}

	public void setTreCode(String treCode) {
		this.treCode = treCode;
		getBankType(this.treCode);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}

	private void getBankType(String treCode) {
		try {
			//获取国库代码
//			TsTreasuryDto searchTreCode = new TsTreasuryDto();
//			searchTreCode.setSorgcode(loginInfo.getSorgcode());
//			searchTreCode.setStrecode(treCode);
			List resultList ;
//			List resultList = commonDataAccessService.findRsByDto(searchTreCode);
//			if(null == resultList || resultList.size() == 0){
//				return ;
//			}
			voucherSourceList = new ArrayList();
//			Mapper mapper011 = new Mapper();
//			mapper011.setDisplayValue(((TsTreasuryDto)resultList.get(0)).getStrename());
//			mapper011.setUnderlyValue("011");	//人民银行
//			voucherSource = "011";
//			voucherSourceList.add(mapper011);
			resultList = null;
			//获取财政机构代码
			TsConvertfinorgDto searchFinOrgDto = new TsConvertfinorgDto();
			searchFinOrgDto.setSorgcode(loginInfo.getSorgcode());
			searchFinOrgDto.setStrecode(treCode);
			resultList = commonDataAccessService.findRsByDto(searchFinOrgDto);
			if(null == resultList || resultList.size() == 0){
				return ;
			}
			Mapper mapper001 = new Mapper();
			mapper001.setDisplayValue(((TsConvertfinorgDto)resultList.get(0)).getSfinorgname());
			mapper001.setUnderlyValue("001");	//财政机构代码
			voucherSourceList.add(mapper001);
			resultList = null;
			voucherSource = "001";
			TsConvertbanktypeDto searchBankType = new TsConvertbanktypeDto();
			searchBankType.setSorgcode(loginInfo.getSorgcode());
			searchBankType.setStrecode(treCode);
			resultList = commonDataAccessService
					.findRsByDto(searchBankType);
			if(null == resultList || resultList.size() == 0){
				return;
			}
			Mapper tmpMapper = null;
			for(TsConvertbanktypeDto tmpDto : (List<TsConvertbanktypeDto>)resultList){
				tmpMapper = new Mapper();
				tmpMapper.setUnderlyValue(tmpDto.getSbankcode());
				tmpMapper.setDisplayValue(tmpDto.getSbankname());
				voucherSourceList.add(tmpMapper);
			}
		} catch (ITFEBizException e) {
			log.error("初始化信息失败！",e);
			MessageDialog.openErrorDialog(null, e);
		}
	}

}
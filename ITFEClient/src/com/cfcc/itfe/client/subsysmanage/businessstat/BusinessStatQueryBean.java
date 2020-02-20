package com.cfcc.itfe.client.subsysmanage.businessstat;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;

/**
 * codecomment:
 * 
 * @author zhouchuan
 * @time 10-05-31 15:57:21 ��ϵͳ: SubSysManage ģ��:businessStat
 *       ���:BusinessStatQuery
 */
public class BusinessStatQueryBean extends AbstractBusinessStatQueryBean implements IPageDataProvider {

	private static Log log = LogFactory.getLog(BusinessStatQueryBean.class);
	private PagingContext incometablepage  = new PagingContext(this); // ��ҳ�ؼ�
	TvInfileDto finddto = null;

	public BusinessStatQueryBean() {
		super();
		finddto = new TvInfileDto();
	}


	public PagingContext getIncometablepage() {
		return incometablepage;
	}

	public void setIncometablepage(PagingContext incometablepage) {
		this.incometablepage = incometablepage;
	}

	/**
	 * Direction: ��ѯ�б��¼� ename: searchList ���÷���: viewers: ҵ����ͳ�Ʋ�ѯ��� messages:
	 */
	public String searchList(Object o) {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		incometablepage.setPage(pageResponse);

		if (pageResponse.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
			return super.rebackSearch(o);
		}

		editor.fireModelChanged();
		return super.searchList(o);
	}

	/**
	 * Direction: ���ز�ѯ���� ename: rebackSearch ���÷���: viewers: ҵ����ͳ�Ʋ�ѯ��� messages:
	 */
	public String rebackSearch(Object o) {
		return super.rebackSearch(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest arg0) {

		try {
			return businessStatService.findBusinessByPage(finddto, arg0);
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return super.retrieve(arg0);
	}

	public TvInfileDto getFinddto() {
		return finddto;
	}


	public void setFinddto(TvInfileDto finddto) {
		this.finddto = finddto;
	}
	
}
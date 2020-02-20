package com.cfcc.itfe.client.subsysmanage.datachangeandcompare;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;

import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 17-01-16 14:29:31 子系统: SubSysManage 模块:datachangeAndCompare
 *       组件:DatachangeAndCompare
 */
public class DatachangeAndCompareBean extends AbstractDatachangeAndCompareBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(DatachangeAndCompareBean.class);

	public DatachangeAndCompareBean() {
		super();
		strsql = "";
		tcfile = new ArrayList<File>();
		qcfile = new ArrayList<File>();
	}

	/**
	 * Direction: 执行SQL ename: runsql 引用方法: viewers: * messages:
	 */
	public String runsql(Object o) {

		try {
			datachangeAndCompareService.runsql(strsql);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}

		return super.runsql(o);
	}

	/**
	 * Direction: 比对 ename: comparedata 引用方法: viewers: * messages:
	 */
	public String comparedata(Object o) {

		List<String[]> listqc = new ArrayList<String[]>();
		List<String[]> listtc = new ArrayList<String[]>();
		try {
			for (File tcf : (List<File>) tcfile) {
				listtc.addAll(com.cfcc.itfe.util.FileUtil.getInstance()
						.readFileWithLine(tcf.getPath(), ","));
			}

			for (File qcf : (List<File>) qcfile) {
				listqc.addAll(com.cfcc.itfe.util.FileUtil.getInstance()
						.readFileWithLine(qcf.getPath(), ","));
			}

			Map<String, String[]> maptc = trancMap(listtc);
			String str = "";
			for (String[] qc : listqc) {
				if (maptc.get(qc[2].trim()) == null) {
					str = str + qc[2].trim() + "," + qc[3].trim() + "," + "\n";
				}
			}
			if (str.equals("")) {
				MessageDialog.openMessageDialog(null, "单位清册中的预算单位都已维护在TC中！");
			} else {
				MessageDialog.openMessageDialog(null, "TC中没有的预算单位如下：" + str);
			}
			System.out.println("------------------" + str);
		} catch (FileOperateException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}

		return super.comparedata(o);
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

	public static Map<String, String[]> trancMap(List<String[]> list) {
		Map<String, String[]> map = new HashMap<String, String[]>();
		for (String[] ss : list) {
			if (map.get(ss[2].trim()) == null) {
				map.put(ss[2].trim(), ss);
			}
		}
		return map;
	}
}
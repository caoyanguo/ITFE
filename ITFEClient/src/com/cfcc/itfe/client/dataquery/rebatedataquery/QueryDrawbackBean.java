package com.cfcc.itfe.client.dataquery.rebatedataquery;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 09-10-21 09:53:01 子系统: DataQuery 模块:RebateDataQuery 组件:QueryDrawback
 */
public class QueryDrawbackBean extends AbstractQueryDrawbackBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(QueryDrawbackBean.class);
	private String selQueryKind;
	private String selProcFlag;

	public String getSelQueryKind() {
		return selQueryKind;
	}

	public void setSelQueryKind(String selQueryKind) {
		this.selQueryKind = selQueryKind;
	}

	public String getSelProcFlag() {
		return selProcFlag;
	}

	public void setSelProcFlag(String selProcFlag) {
		this.selProcFlag = selProcFlag;
	}

	public QueryDrawbackBean() {
		super();

	}

	/**
	 * Direction: 退税数据查询 ename: queryDrawback 引用方法: viewers: 退税查询-预接收 messages:
	 */
	public String queryDrawback(Object o) {
		if (this.getSelQueryKind().equals("1")) {
             if (this.getSelProcFlag()== "1") {
			//	editor.openComposite(toBeOpen);
			} else {

			}
		}

		return "退税查询-预接收";
	}

	/**
	 * Direction: 显示申报表数据 ename: queryReportDatas 引用方法: viewers: 申报表查询结果
	 * messages:
	 */
	public String queryReportDatas(Object o) {

		return "申报表查询结果";
	}
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	
    
	/**
	 * Direction: 还原退库凭证
	 * ename: backdwbkvou
	 * 引用方法: 
	 * viewers: 报表显示
	 * messages: 
	 */
    public String backdwbkvou(Object o){
        
        return "报表显示";
    }
    
	/**
	 * Direction: 汇总退还书
	 * ename: sumdwbkreport
	 * 引用方法: 
	 * viewers: 报表显示
	 * messages: 
	 */
    public String sumdwbkreport(Object o){
        
        return "报表显示";
    }
    
	/**
	 * Direction: 退税明细打印
	 * ename: detaildwbkprint
	 * 引用方法: 
	 * viewers: 报表显示
	 * messages: 
	 */
    public String detaildwbkprint(Object o){
        
        return "报表显示";
    }
    
	/**
	 * Direction: 发送回执报文
	 * ename: senddwbkreport
	 * 引用方法: 
	 * viewers: 退税查询发送回执
	 * messages: 
	 */
    public String senddwbkreport(Object o){
        
        return "退税查询发送回执";
    }
    
	/**
	 * Direction: 取消校验
	 * ename: cancelcheck
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String cancelcheck(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 删除报文
	 * ename: deletereport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String deletereport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 生成退库退回
	 * ename: makedwbkbackreport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String makedwbkbackreport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 返回报文查询
	 * ename: goback
	 * 引用方法: 
	 * viewers: 通用查询条件
	 * messages: 
	 */
    public String goback(Object o){
        
        return "通用查询条件";
    }
    
	/**
	 * Direction: 全选
	 * ename: selectAll
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectAll(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 返回报文处理界面
	 * ename: gobackproc
	 * 引用方法: 
	 * viewers: 退税查询处理
	 * messages: 
	 */
    public String gobackproc(Object o){
        
        return "退税查询处理";
    }
    
	/**
	 * Direction: 清算失败汇总退还书
	 * ename: sumdwbkfailreport
	 * 引用方法: 
	 * viewers: 报表显示2
	 * messages: 
	 */
    public String sumdwbkfailreport(Object o){
        
        return "报表显示2";
    }
    
	/**
	 * Direction: 到数据页面
	 * ename: goDataview
	 * 引用方法: 
	 * viewers: 退税报文查询结果
	 * messages: 
	 */
    public String goDataview(Object o){
        
        return "退税报文查询结果";
    }
    
	/**
	 * Direction: 到回执界面
	 * ename: godwbkbackview
	 * 引用方法: 
	 * viewers: 退税查询发送回执
	 * messages: 
	 */
    public String godwbkbackview(Object o){
        
        return "退税查询发送回执";
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

	private void init() {
		// ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
		// .getDefault().getLoginInfo();
		// PageRequest pageRequest = new PageRequest();
		// PageResponse pageResponse = retrieve(pageRequest);
		// pagingcontext.setPage(pageResponse);

	}

}
package com.cfcc.itfe.client.dataquery.payoutquery;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.mvc.BasicModel;
import com.cfcc.itfe.service.dataquery.payoutquery.IPayoutService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgmainDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;

/**
 * codecomment: 
 *
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * 子系统: DataQuery
 * 模块:payOutQuery
 * 组件:PayOutQuery
 */
public abstract class AbstractPayOutQueryBean extends BasicModel {
	/** LOG */
	final Log log = LogFactory.getLog(this.getClass());
	
	/** service */
    protected IPayoutService payoutService = (IPayoutService)getService(IPayoutService.class);
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
		
	/** 属性列表 */
    TvPayoutmsgsubDto modifyDto = null;
    private Properties MESSAGE_PROPERTIES;
	private static final String MESSAGE_KEY_PREFIX = "itfe_dataquery_payoutquery_payoutquery_";
	
    /** Message */
    
    
    /** 构造函数 */
    public AbstractPayOutQueryBean() {
		super();
		//
		MESSAGE_PROPERTIES = new Properties();
		try {
			InputStream is = com.cfcc.jaf.common.util.ResourceUitl
					.getClassPathInputStream("/messages/itfe_dataquery_payoutquery_payoutquery.properties");
			MESSAGE_PROPERTIES.load(is);
			Set set = MESSAGE_PROPERTIES.keySet();
			Iterator it = set.iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				String value = MESSAGE_PROPERTIES.getProperty((String) key);
				value = new String(value.getBytes("ISO-8859-1"), "GBK");
				MESSAGE_PROPERTIES.setProperty((String) key, value);
			}
		} catch (Exception e) {
			log.warn("为itfe_dataquery_payoutquery_payoutquery读取messages出错", e);
		}

	}
	
	/**
	 * =========================================================================
	 * direction
	 * =========================================================================
	 */
        
	/**
	 * Direction: 查询列表事件
	 * ename: searchList
	 * 引用方法: 
	 * viewers: 实拨资金信息列表
	 * messages: 
	 */
    public String searchList(Object o){
        
        return "实拨资金信息列表";
    }
    
	/**
	 * Direction: 返回查询界面
	 * ename: rebackSearch
	 * 引用方法: 
	 * viewers: 实拨资金查询界面
	 * messages: 
	 */
    public String rebackSearch(Object o){
        
        return "实拨资金查询界面";
    }
    
	/**
	 * Direction: 主信息单击事件
	 * ename: singleclickMain
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleclickMain(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 主信息双击事件
	 * ename: doubleclickMain
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String doubleclickMain(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 修改保存
	 * ename: modifysave
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String modifysave(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 返回列表事件
	 * ename: rebackSearchList
	 * 引用方法: 
	 * viewers: 实拨资金信息列表
	 * messages: 
	 */
    public String rebackSearchList(Object o){
        
        return "实拨资金信息列表";
    }
    
	/**
	 * Direction: 转到发送页面事件
	 * ename: gosendmsg
	 * 引用方法: 
	 * viewers: 实拨资金包发送界面
	 * messages: 
	 */
    public String gosendmsg(Object o){
        
        return "实拨资金包发送界面";
    }
    
	/**
	 * Direction: 报文发送事件
	 * ename: sendmsg
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String sendmsg(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 发送报文单击事件
	 * ename: singleclicksendmsg
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleclicksendmsg(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 实拨资金复核
	 * ename: checkPayout
	 * 引用方法: 
	 * viewers: 实拨资金信息列表
	 * messages: 
	 */
    public String checkPayout(Object o){
        
        return "实拨资金信息列表";
    }
    
	/**
	 * Direction: 去实拨资金复核
	 * ename: goCheckPayout
	 * 引用方法: 
	 * viewers: 实拨资金复核界面
	 * messages: 
	 */
    public String goCheckPayout(Object o){
        
        return "实拨资金复核界面";
    }
    
	/**
	 * Direction: 退库对账单打印
	 * ename: queryPrint
	 * 引用方法: 
	 * viewers: 实拨资金对账单
	 * messages: 
	 */
    public String queryPrint(Object o){
        
        return "实拨资金对账单";
    }
    
	/**
	 * Direction: 导出
	 * ename: dataExport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String dataExport(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 更新成功
	 * ename: updateSuccess
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String updateSuccess(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 全选/反选
	 * ename: selectAllOrNone
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectAllOrNone(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 更新失败
	 * ename: updateFail
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String updateFail(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 导出选中回单
	 * ename: exportSelectData
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportSelectData(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 明细信息双击事件
	 * ename: doubleclickSub
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String doubleclickSub(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 导出文件csv
	 * ename: exportfile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportfile(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 转到修改界面
	 * ename: goModify
	 * 引用方法: 
	 * viewers: 实拨资金修改界面
	 * messages: 
	 */
    public String goModify(Object o){
        
        return "实拨资金修改界面";
    }
    
	/**
	 * Direction: 修改保存
	 * ename: modifyDetailSave
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String modifyDetailSave(Object o){
        
        return "";
    }
    
	/**
	 * Direction: 返回到明细列表界面
	 * ename: backToList
	 * 引用方法: 
	 * viewers: 实拨资金信息列表
	 * messages: 
	 */
    public String backToList(Object o){
        
        return "实拨资金信息列表";
    }
    
	/**
	 * Direction: 明细修改
	 * ename: toDetail
	 * 引用方法: 
	 * viewers: 实拨资金明细修改界面
	 * messages: 
	 */
    public String toDetail(Object o){
        
        return "实拨资金明细修改界面";
    }
    
	/**
	 * Direction: 单选
	 * ename: detailSingleSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String detailSingleSelect(Object o){
        
        return "";
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {
		return null;
	}
	

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.rcp.mvc.IModel#getMESSAGE_PROPERTIES()
	 */
	public Properties getMESSAGE_PROPERTIES() {
		return MESSAGE_PROPERTIES;
	}

	/* (non-Javadoc)
	 * @see com.cfcc.jaf.rcp.mvc.IModel#getMessage(java.lang.String, java.lang.String)
	 */
	public String getMessage(String _direction, String _msgkey) {
		return MESSAGE_PROPERTIES.getProperty(MESSAGE_KEY_PREFIX + _direction
				+ "_" + _msgkey);
	}

	/**
	 * =========================================================================
	 * getter and setter
	 * =========================================================================
	 */
        
    public com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto getModifyDto() {
        return modifyDto;
    }

    public void setModifyDto(com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto _modifyDto) {
        modifyDto = _modifyDto;
    }
}
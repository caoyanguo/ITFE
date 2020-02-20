package com.cfcc.itfe.client.subsysmanage.sysmoniter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author db2admin
 * @time   13-01-15 11:29:32
 * 子系统: SubSysManage
 * 模块:sysmoniter
 * 组件:SysmoniterUI
 */
public class SysmoniterUIBean extends AbstractSysmoniterUIBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(SysmoniterUIBean.class);
    
    private String moniterarea;
    private String infoArea;
    public SysmoniterUIBean() {
      super();
                  
    }
    
	/**
	 * Direction: 刷新
	 * ename: refresh
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String refresh(Object o){
    	String temparea=getMoniterarea();
    	setMoniterarea(temparea);
          return super.refresh(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

    public String getMonitorInfo(String moniterarea){
    	try {
    		//系统监控范围 0--操作系统，1--数据库，2--MQ，3--应用服务器，4--CA证书，5--应用日志
    		infoArea=sysmonitorService.sysMonitor(moniterarea);
    		this.editor.fireModelChanged();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "获取系统监控信息出错！"+e.getMessage());
		}
    	return "";
    }
    
	public String getMoniterarea() {
		return moniterarea;
	}

	public void setMoniterarea(String moniterarea) {
		this.moniterarea = moniterarea;
		getMonitorInfo(moniterarea);
	}

	public String getInfoArea() {
		return infoArea;
	}

	public void setInfoArea(String infoArea) {
		this.infoArea = infoArea;
	}

}
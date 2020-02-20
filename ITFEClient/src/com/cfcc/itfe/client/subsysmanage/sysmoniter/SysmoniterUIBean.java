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
 * ��ϵͳ: SubSysManage
 * ģ��:sysmoniter
 * ���:SysmoniterUI
 */
public class SysmoniterUIBean extends AbstractSysmoniterUIBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(SysmoniterUIBean.class);
    
    private String moniterarea;
    private String infoArea;
    public SysmoniterUIBean() {
      super();
                  
    }
    
	/**
	 * Direction: ˢ��
	 * ename: refresh
	 * ���÷���: 
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
    		//ϵͳ��ط�Χ 0--����ϵͳ��1--���ݿ⣬2--MQ��3--Ӧ�÷�������4--CA֤�飬5--Ӧ����־
    		infoArea=sysmonitorService.sysMonitor(moniterarea);
    		this.editor.fireModelChanged();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "��ȡϵͳ�����Ϣ����"+e.getMessage());
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
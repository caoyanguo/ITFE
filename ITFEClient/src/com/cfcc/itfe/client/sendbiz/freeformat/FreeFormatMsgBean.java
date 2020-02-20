package com.cfcc.itfe.client.sendbiz.freeformat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author db2admin
 * @time   12-03-15 14:42:19
 * 子系统: SendBiz
 * 模块:FreeFormat
 * 组件:FreeFormatMsg
 */
public class FreeFormatMsgBean extends AbstractFreeFormatMsgBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(FreeFormatMsgBean.class);
    private String sendOrgCode;
    private String rcvOrgCode;
    private String content;
    
    public FreeFormatMsgBean() {
      super();
    }
    
	/**
	 * Direction: 发送自由格式报文
	 * ename: sendFreeFormatMsg
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String sendFreeFormatMsg(Object o){
    	if (content==null||content.equals("")) {
			MessageDialog.openMessageDialog(null, "内容不能为空！");
			return null;
		}
    	try {
			freeFormateMsgService.sendFreeFormateMsg(sendOrgCode, rcvOrgCode, content);
		} catch (ITFEBizException e) {
			// TODO Auto-generated catch block
			MessageDialog.openErrorDialog(null, e);
			e.printStackTrace();
		}
		MessageDialog.openMessageDialog(null, "自由格式报文发送成功！");
          return super.sendFreeFormatMsg(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

	public String getSendOrgCode() {
		return sendOrgCode;
	}

	public void setSendOrgCode(String sendOrgCode) {
		this.sendOrgCode = sendOrgCode;
	}

	public String getRcvOrgCode() {
		return rcvOrgCode;
	}

	public void setRcvOrgCode(String rcvOrgCode) {
		this.rcvOrgCode = rcvOrgCode;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
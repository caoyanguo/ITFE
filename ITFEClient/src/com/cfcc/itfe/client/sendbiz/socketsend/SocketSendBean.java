package com.cfcc.itfe.client.sendbiz.socketsend;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;

import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.service.sendbiz.socketsend.SocketSendService;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;

/**
 * codecomment: 
 * @author Administrator
 * @time   14-07-22 11:21:41
 * 子系统: SendBiz
 * 模块:socketSend
 * 组件:SocketSend
 */
public class SocketSendBean extends AbstractSocketSendBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(SocketSendBean.class);
    public SocketSendBean() {
      super();
      filepath= new ArrayList();;
                  
    }
    
	/**
	 * Direction: 发送文件
	 * ename: sendfile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String sendfile(Object o){
		List<String> serverpathlist = new ArrayList<String>();	
		if (null == filepath || filepath.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要加载的文件！");
			return null;
		}				
		List<File> fileList=new ArrayList<File>();
		HashMap map = new HashMap();
		try {			
			for (File tmpfile:(List<File>) filepath) {
				if (null == tmpfile || null == tmpfile.getName()|| null == tmpfile.getAbsolutePath()) {					
					MessageDialog.openMessageDialog(null, " 请选择要加载的文件！");
					return null;
				}				
				String serverpath =ClientFileTransferUtil.uploadFile(tmpfile.getAbsolutePath());
				fileList.add(new File(serverpath));
				serverpathlist.add(serverpath);
			}
			socketSendService.sendFile(serverpathlist);
			commonDataAccessService.saveTvrecvlog(DeleteServerFileUtil.wipeFileOut
					(serverpathlist, null),MsgConstant.VOUCHER_ATTACH_SEND);				
			MessageDialog.openMessageDialog(null, "文件发送成功,本次共发送成功 "+ fileList.size() + " 个文件！");			
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);			
		}
		filepath = new ArrayList();
		this.editor.fireModelChanged();          
		return super.sendfile(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

}
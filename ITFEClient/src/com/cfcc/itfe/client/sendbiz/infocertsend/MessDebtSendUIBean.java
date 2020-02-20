package com.cfcc.itfe.client.sendbiz.infocertsend;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author Administrator
 * @time   09-10-19 14:25:20
 * 子系统: SendBiz
 * 模块:InfoCertSend
 * 组件:MessDebtSendUI
 */
public class MessDebtSendUIBean extends AbstractMessDebtSendUIBean implements IPageDataProvider {
    private static Log log = LogFactory.getLog(MessDebtSendUIBean.class);
	//登陆人员所属机构名称
	private String userOrgName;
	//当前服务器日期yyyy-mm-dd格式
	private String sendDate;
	//用户登录信息
	private ITFELoginInfo loginInfo;
	//全部的接收机构的集合
	private List<TsOrganDto> allRecvOrgs;
	//用户所选择的接收机构集合
	private List<TsOrganDto> recvOrgs;
	//用户所选择的接收机构的名称字符串，显示使用
	private String recvOrgNames;
	//信息文件标题
	private String title;
	//信息文件内容
	private String content;
	//记录上载文件的集合
	private List<String> upLoadFiles;
   
    public MessDebtSendUIBean() {
      super();
      try{
    	  //获得用户信息
    	  loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
          userOrgName = loginInfo.getSorgName();
          //获得系统日期
          sendDate = commonDataAccessService.getSysDate();
          //所有接收机构的列表
          allRecvOrgs = messDataUploadService.getAllConnOrgs();
          upLoadFiles = new ArrayList<String>();
      }catch(Throwable e){
    	  log.error(e);
    	  MessageDialog.openErrorDialog(null, e);
      }
    }
    /**
	 * Direction: 信息凭证发送
	 * ename: messDebtSend
	 * 引用方法: 
	 * viewers: 信息凭证发送页面
	 * messages: 
	 */
    public int upload(){
    	//判断一些信息用户是否已经输入
    	if ((recvOrgs == null) || (recvOrgs.size() == 0)){
    		MessageDialog.openMessageDialog(null, "请选择接收机构。");
    		return -1;
    	}
    	if ((title == null) || (title.length() == 0)){
    		MessageDialog.openMessageDialog(null, "请录入信息文件的标题。");
    		return -1;
    	}
    	if (title.length() > 200){
    		MessageDialog.openMessageDialog(null, "文件标题的长度不能超过200。");
    		return -1;
    	}
    	if (content.length() > 250){
    		MessageDialog.openMessageDialog(null, "内容的长度不能超过250。");
    		return -1;
    	}
    	try{
        	messDataUploadService.upload(title, content, recvOrgs, upLoadFiles);
        	MessageDialog.openMessageDialog(null, " 信息凭证发送成功！");
            return 0;
    	}catch(Throwable e){
    		try{
    			String errMsg = "";
    			if ((null == e.getCause()) || (null == e.getCause().getMessage())){
    				errMsg = e.getMessage();
    			}else{
    				errMsg = e.getCause().getMessage(); 
    			}
    			errMsg = errMsg.substring(0,(errMsg.length()>198?198:errMsg.length()));
    			messDataUploadService.addErrorSendLog(title, errMsg);
    		}catch(Throwable ex){
    			log.error(ex);
    		}
    		if (null == e.getCause())
    			MessageDialog.openErrorDialog(null, e);
    		else
    			MessageDialog.openErrorDialog(null, e.getCause());
    		return -1;
    	}
    }
    
    /**
     * 上载一个文件
     * @param filePath 要上载的文件的绝对路径
     */
    public void uploadOneFile(String filePath){
		//上载文件
    	try {
			String fileUploadPath = ClientFileTransferUtil.uploadFile(filePath);
			upLoadFiles.add(fileUploadPath);
		} catch (Throwable e) {
    		MessageDialog.openErrorDialog(null, e);
		}
    }
    
    /**
     * 删除一个已经上载的文件
     * @param fileName 文件名
     */
    public void deleteOneFile(String fileName){
    	try {
    		int i = 0;
    		for (; i<upLoadFiles.size(); i++){
    			if (upLoadFiles.get(i).endsWith(fileName))
    				break;
    		}
    		if (i < upLoadFiles.size()){
        		messDataUploadService.deleteOneFile(upLoadFiles.get(i));
        		upLoadFiles.remove(i);
    		}
		} catch (Throwable e) {
    		MessageDialog.openErrorDialog(null, e);
		}
    }
    
    
	/**
	 * Direction: 转接收机构选择页面
	 * ename: toReciveUnit
	 * 引用方法: 
	 * viewers: 接收机构选择页面
	 * messages: 
	 */
    public String toReciveUnit(Object o){
        
        return "接收机构选择页面";
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {
		return super.retrieve(arg0);
	}
    
	public String getUserOrgName() {
		return userOrgName;
	}
	public void setUserOrgName(String userOrgName) {
		this.userOrgName = userOrgName;
	}
	public String getSendDate() {
		return sendDate;
	}
	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}
	public List<TsOrganDto> getAllRecvOrgs() {
		return allRecvOrgs;
	}
	public void setAllRecvOrgs(List<TsOrganDto> allRecvOrgs) {
		this.allRecvOrgs = allRecvOrgs;
	}
	public List<TsOrganDto> getRecvOrgs() {
		return recvOrgs;
	}
	public void setRecvOrgs(List<TsOrganDto> recvOrgs) {
		this.recvOrgs = recvOrgs;
		String name = "";
		for (TsOrganDto one : this.recvOrgs){
			name += (one.getSorgname()==null?"":one.getSorgname()) + ";";
		}
		setRecvOrgNames(name);
	}
	public String getRecvOrgNames() {
		return recvOrgNames;
	}
	public void setRecvOrgNames(String recvOrgNames) {
		this.recvOrgNames = recvOrgNames;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

}
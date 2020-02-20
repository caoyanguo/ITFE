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
 * ��ϵͳ: SendBiz
 * ģ��:InfoCertSend
 * ���:MessDebtSendUI
 */
public class MessDebtSendUIBean extends AbstractMessDebtSendUIBean implements IPageDataProvider {
    private static Log log = LogFactory.getLog(MessDebtSendUIBean.class);
	//��½��Ա������������
	private String userOrgName;
	//��ǰ����������yyyy-mm-dd��ʽ
	private String sendDate;
	//�û���¼��Ϣ
	private ITFELoginInfo loginInfo;
	//ȫ���Ľ��ջ����ļ���
	private List<TsOrganDto> allRecvOrgs;
	//�û���ѡ��Ľ��ջ�������
	private List<TsOrganDto> recvOrgs;
	//�û���ѡ��Ľ��ջ����������ַ�������ʾʹ��
	private String recvOrgNames;
	//��Ϣ�ļ�����
	private String title;
	//��Ϣ�ļ�����
	private String content;
	//��¼�����ļ��ļ���
	private List<String> upLoadFiles;
   
    public MessDebtSendUIBean() {
      super();
      try{
    	  //����û���Ϣ
    	  loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
          userOrgName = loginInfo.getSorgName();
          //���ϵͳ����
          sendDate = commonDataAccessService.getSysDate();
          //���н��ջ������б�
          allRecvOrgs = messDataUploadService.getAllConnOrgs();
          upLoadFiles = new ArrayList<String>();
      }catch(Throwable e){
    	  log.error(e);
    	  MessageDialog.openErrorDialog(null, e);
      }
    }
    /**
	 * Direction: ��Ϣƾ֤����
	 * ename: messDebtSend
	 * ���÷���: 
	 * viewers: ��Ϣƾ֤����ҳ��
	 * messages: 
	 */
    public int upload(){
    	//�ж�һЩ��Ϣ�û��Ƿ��Ѿ�����
    	if ((recvOrgs == null) || (recvOrgs.size() == 0)){
    		MessageDialog.openMessageDialog(null, "��ѡ����ջ�����");
    		return -1;
    	}
    	if ((title == null) || (title.length() == 0)){
    		MessageDialog.openMessageDialog(null, "��¼����Ϣ�ļ��ı��⡣");
    		return -1;
    	}
    	if (title.length() > 200){
    		MessageDialog.openMessageDialog(null, "�ļ�����ĳ��Ȳ��ܳ���200��");
    		return -1;
    	}
    	if (content.length() > 250){
    		MessageDialog.openMessageDialog(null, "���ݵĳ��Ȳ��ܳ���250��");
    		return -1;
    	}
    	try{
        	messDataUploadService.upload(title, content, recvOrgs, upLoadFiles);
        	MessageDialog.openMessageDialog(null, " ��Ϣƾ֤���ͳɹ���");
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
     * ����һ���ļ�
     * @param filePath Ҫ���ص��ļ��ľ���·��
     */
    public void uploadOneFile(String filePath){
		//�����ļ�
    	try {
			String fileUploadPath = ClientFileTransferUtil.uploadFile(filePath);
			upLoadFiles.add(fileUploadPath);
		} catch (Throwable e) {
    		MessageDialog.openErrorDialog(null, e);
		}
    }
    
    /**
     * ɾ��һ���Ѿ����ص��ļ�
     * @param fileName �ļ���
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
	 * Direction: ת���ջ���ѡ��ҳ��
	 * ename: toReciveUnit
	 * ���÷���: 
	 * viewers: ���ջ���ѡ��ҳ��
	 * messages: 
	 */
    public String toReciveUnit(Object o){
        
        return "���ջ���ѡ��ҳ��";
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
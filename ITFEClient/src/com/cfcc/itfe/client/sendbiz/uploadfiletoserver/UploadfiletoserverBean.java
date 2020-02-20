package com.cfcc.itfe.client.sendbiz.uploadfiletoserver;

import java.util.*;
import java.io.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;

/**
 * codecomment: 
 * @author zhangliang
 * @time   17-06-06 13:55:58
 * ��ϵͳ: SendBiz
 * ģ��:uploadfiletoserver
 * ���:Uploadfiletoserver
 */
@SuppressWarnings("unchecked")
public class UploadfiletoserverBean extends AbstractUploadfiletoserverBean implements IPageDataProvider {
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
    private static Log log = LogFactory.getLog(UploadfiletoserverBean.class);
	private List filepath;
    private ITFELoginInfo loginInfo;
    private List checklist;
    private List searchlist;
    private String trecode;
    private String serverlogpath;
    public UploadfiletoserverBean() {
      super();
      loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      filepath=new ArrayList<File>();
      checklist=new ArrayList();
    }
    
	/**
	 * Direction: �ϴ�
	 * ename: uploadfile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String uploadfile(Object o){
    	if (StringUtils.isBlank(trecode)) {
			MessageDialog.openMessageDialog(null, "��ѡ�������룡");
			return null;
		}
		List<String> serverpathlist = new ArrayList<String>();	
		if (null == filepath || filepath.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص��ļ���");
			return null;
		}
		if (filepath.size() > 1) {
			MessageDialog.openMessageDialog(null, "��ѡ�����ļ����ܴ���1����");
			return null;
		}	
		if(searchlist!=null&&searchlist.size()>=10)
		{
			MessageDialog.openMessageDialog(null, "ÿ�������ϴ��ļ��������ܴ���10����");
			return null;
		}
		List<File> fileList=new ArrayList<File>();
		try {			
			for (File tmpfile:(List<File>) filepath) {
				if (null == tmpfile || null == tmpfile.getName()|| null == tmpfile.getAbsolutePath()) {					
					MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ���ص��ļ���");
					return null;
				}
				if (tmpfile.length()>(1048576*20)) {					
					MessageDialog.openMessageDialog(null, " �����ļ���С���ܴ���20MB��");
					return null;
				}
				String serverpath =ClientFileTransferUtil.uploadFile(tmpfile.getAbsolutePath());
				fileList.add(new File(serverpath));
				serverpathlist.add(serverpath);
			}
			Map parammap = new HashMap();
			parammap.put("trecode", trecode);
			parammap.put("filelist", serverpathlist);
			uploadfiletoserverService.uploadfile(parammap);
			MessageDialog.openMessageDialog(null, "�ļ��ϴ��ɹ�,���ι��ϴ� "+ fileList.size() + " ���ļ���");			
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch(Exception e){
			log.error(e);					
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e);
		}
		filepath = new ArrayList();
		searchfile(o);
		this.editor.fireModelChanged();
        return super.uploadfile(o);
    }

	/**
	 * Direction: ��ѯ
	 * ename: searchfile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String searchfile(Object o){
    	if (StringUtils.isBlank(trecode)) {
			MessageDialog.openMessageDialog(null, "��ѡ�������룡");
			return null;
		}
    	checklist = new ArrayList();
    	Map parammap = new HashMap();
		parammap.put("trecode", trecode);
		if(serverlogpath!=null&&!"".equals(serverlogpath))
			parammap.put("serverlogpath", serverlogpath);
		try {
			searchlist = uploadfiletoserverService.searchfile(parammap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.editor.fireModelChanged();
        return super.searchfile(o);
    }

	/**
	 * Direction: ɾ��
	 * ename: deletefile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String deletefile(Object o){
    	Map parammap = new HashMap();
		parammap.put("trecode", trecode);
		parammap.put("filelist", checklist);
		try {
			uploadfiletoserverService.deletefile(parammap);
			searchfile(o);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return super.deletefile(o);
    }

	/**
	 * Direction: ����
	 * ename: downloadfile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String downloadfile(Object o){
    	String path = new DirectoryDialog(Display.getCurrent().getActiveShell()).open();		
		if (StringUtils.isBlank(path)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
			return "";
		}
		if(checklist==null||checklist.size()==0){
			MessageDialog.openMessageDialog(null, "��ѡ����Ҫ���ص��ļ���");
			return "";
		}
		try{
			if(checklist!=null&&checklist.size()>0)
			{
				Map parammap = null;
				List filelist = new ArrayList();
				for(int i=0;i<checklist.size();i++){
					parammap = (Map)checklist.get(i);
					if(!String.valueOf(parammap.get("trecode")).contains("itfelogs"))
						ClientFileTransferUtil.downloadFile("/filetoserver/"+trecode+"/"+parammap.get("filename"),path+"/"+parammap.get("filename"));
				}
				for(int i=0;i<checklist.size();i++)
				{
					parammap = (Map)checklist.get(i);
					if(String.valueOf(parammap.get("trecode")).contains("itfelogs"))
					{
						uploadfiletoserverService.downloadfile(parammap);
						ClientFileTransferUtil.downloadFile(String.valueOf(parammap.get("filename")),path+"/"+parammap.get("filename"));
						filelist.add(parammap.get("filename"));
					}
				}
				if(filelist!=null&&filelist.size()>0)
					DeleteServerFileUtil.delFile(commonDataAccessService,filelist);
			}
											
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e);
			return "";
		}		
		MessageDialog.openMessageDialog(null, "�ļ����سɹ������ι����� "+ checklist.size() + " ���ļ���");
        return super.downloadfile(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		UploadfiletoserverBean.log = log;
	}

	public List getFilepath() {
		return filepath;
	}

	public void setFilepath(List filepath) {
		this.filepath = filepath;
	}

	public ITFELoginInfo getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(ITFELoginInfo loginInfo) {
		this.loginInfo = loginInfo;
	}

	public List getChecklist() {
		return checklist;
	}

	public void setChecklist(List checklist) {
		this.checklist = checklist;
	}

	public List getSearchlist() {
		return searchlist;
	}

	public void setSearchlist(List searchlist) {
		this.searchlist = searchlist;
	}

	public String getTrecode() {
		return trecode;
	}

	public void setTrecode(String trecode) {
		this.trecode = trecode;
		if(trecode!=null||!"".equals(trecode))
			searchfile(null);
	}

	public String getServerlogpath() {
		return serverlogpath;
	}

	public void setServerlogpath(String serverlogpath) {
		this.serverlogpath = serverlogpath;
	}

}
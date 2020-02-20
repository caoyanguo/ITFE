package com.cfcc.itfe.client.sendbiz.bizregister;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author Administrator
 * @time   19-10-08 15:11:52
 * ��ϵͳ: SendBiz
 * ģ��:BizRegister
 * ���:BizRegister
 */
public class BizRegisterBean extends AbstractBizRegisterBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(BizRegisterBean.class);
    private ITFELoginInfo loginfo;
    public BizRegisterBean() {
      super();
      filepath = new ArrayList();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();          
    }
    
	/**
	 * Direction: ע��
	 * ename: sendfileRegister
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String sendfileRegister(Object o){
    	
    	List<String> serverpathlist = new ArrayList<String>();	
		if (null == filepath || filepath.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص�ע���ļ���");
			return null;
		}		
     
		List<File> fileList=new ArrayList<File>();
		HashMap map = new HashMap();
		try {			
			for (File tmpfile:(List<File>) filepath) {
				if (null == tmpfile || null == tmpfile.getName()|| null == tmpfile.getAbsolutePath()) {					
					MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ���ص�ע���ļ���");
					return null;
				}			
				 String key = FileUtil.getInstance().readFile(tmpfile.getAbsolutePath());//
				 String filename = tmpfile.getName();
				 if (!filename.substring(0, 12).equals(loginfo.getSorgcode())) {
						if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
								.getCurrentComposite().getShell(), "����", "�ļ���ǰ12λ�����ڵ�¼��������,���������������ļ���ǰ12λ["+filename.substring(0, 12)+"]��Ϊע��ĺ�������,�Ƿ����")) {
						 return null;
				    	}
				 }
				 //���Һ�������
				 TsOrganDto _dto = new TsOrganDto();
				 _dto.setSorgcode(filename.substring(0, 12));
				  List <TsOrganDto> list = commonDataAccessService.findRsByDto(_dto);
				  if (list.size()>0) {
					  TsOrganDto upddto = list.get(0);
					  key = key.substring(key.length() - 12);
					  upddto.setSofcityorgcode(key);
					  commonDataAccessService.updateData(upddto);
					  MessageDialog.openMessageDialog(null, "ע��ɹ�");		
				  }else{
					  MessageDialog.openMessageDialog(null, "��������"+filename.substring(0, 12)+"�����ڣ�");	
				  }
			}
			
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);			
		}
		filepath = new ArrayList();
		this.editor.fireModelChanged(); 
        return super.sendfileRegister(o);
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
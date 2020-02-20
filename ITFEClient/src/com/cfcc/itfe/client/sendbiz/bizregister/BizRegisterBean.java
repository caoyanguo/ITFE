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
 * 子系统: SendBiz
 * 模块:BizRegister
 * 组件:BizRegister
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
	 * Direction: 注册
	 * ename: sendfileRegister
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String sendfileRegister(Object o){
    	
    	List<String> serverpathlist = new ArrayList<String>();	
		if (null == filepath || filepath.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要加载的注册文件！");
			return null;
		}		
     
		List<File> fileList=new ArrayList<File>();
		HashMap map = new HashMap();
		try {			
			for (File tmpfile:(List<File>) filepath) {
				if (null == tmpfile || null == tmpfile.getName()|| null == tmpfile.getAbsolutePath()) {					
					MessageDialog.openMessageDialog(null, " 请选择要加载的注册文件！");
					return null;
				}			
				 String key = FileUtil.getInstance().readFile(tmpfile.getAbsolutePath());//
				 String filename = tmpfile.getName();
				 if (!filename.substring(0, 12).equals(loginfo.getSorgcode())) {
						if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
								.getCurrentComposite().getShell(), "敬告", "文件名前12位不等于登录核算主体,继续操作将按照文件名前12位["+filename.substring(0, 12)+"]作为注册的核算主体,是否继续")) {
						 return null;
				    	}
				 }
				 //查找核算主体
				 TsOrganDto _dto = new TsOrganDto();
				 _dto.setSorgcode(filename.substring(0, 12));
				  List <TsOrganDto> list = commonDataAccessService.findRsByDto(_dto);
				  if (list.size()>0) {
					  TsOrganDto upddto = list.get(0);
					  key = key.substring(key.length() - 12);
					  upddto.setSofcityorgcode(key);
					  commonDataAccessService.updateData(upddto);
					  MessageDialog.openMessageDialog(null, "注册成功");		
				  }else{
					  MessageDialog.openMessageDialog(null, "核算主体"+filename.substring(0, 12)+"不存在！");	
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
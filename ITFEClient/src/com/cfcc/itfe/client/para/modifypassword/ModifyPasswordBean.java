package com.cfcc.itfe.client.para.modifypassword;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.devplatform.model.List;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.security.Md5App;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author caoyg
 * @time   09-11-20 14:34:55
 * 子系统: Para
 * 模块:modifyPassword
 * 组件:ModifyPassword
 */
public class ModifyPasswordBean extends AbstractModifyPasswordBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(ModifyPasswordBean.class);
    ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
	.getDefault().getLoginInfo();
    public ModifyPasswordBean() {
      super();
      ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
		.getDefault().getLoginInfo();
      password = "";
      validatePassword = "";
      oldpassword ="";
      usercode = loginfo.getSuserCode();
                  
    }
    
	/**
	 * Direction: 修改密码
	 * ename: modifyPassword
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String modifyPassword(Object o){
    	if (null==password ||"".equals(password.trim())|| password.length() < 8) {
			MessageDialog.openMessageDialog(null, "密码长度应不小于8位！");
			return "";
		} else if (!(password.equals(validatePassword))) {
			MessageDialog.openMessageDialog(null, "密码与确认密码不符！");
			return "";
		}else if (null==oldpassword ||"".equals(oldpassword.trim())|| oldpassword.length()< 8) {
			MessageDialog.openMessageDialog(null, "原密码长度应该不小于8位！");
			return "";
		}
    	if (password.equals(oldpassword)) {
    		MessageDialog.openMessageDialog(null, "原密码与新密码不能相同！");
			return "";
		}
    	TsUsersDto dto = new TsUsersDto();
    	dto.setSusercode(usercode);
    	dto.setSpassword(new Md5App().makeMd5(oldpassword));
    	dto.setSorgcode(loginfo.getSorgcode());
    	try {
			java.util.List list = commonDataAccessService.findRsByDto(dto);
			if (null ==list || list.size()==0 ) {
				MessageDialog.openMessageDialog(null,"原密码输入有误，请重新输入！");
				return null;
			}
		} catch (Throwable e1) {
			log.error(e1);
			MessageDialog.openErrorDialog(null, e1);
			return null;
		}
		try {
			String pwd = new Md5App().makeMd5(password);
			modifyPasswordService.modifyPassword(pwd);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return "";
		}
		loginfo.setSpassword(password);
		oldpassword ="";
		password = "";
		validatePassword = "";
		MessageDialog.openMessageDialog(null, "密码修改成功！");
		AbstractMetaDataEditorPart holder = (AbstractMetaDataEditorPart) this
				.getModelHolder();
		holder.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.modifyPassword(o);

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
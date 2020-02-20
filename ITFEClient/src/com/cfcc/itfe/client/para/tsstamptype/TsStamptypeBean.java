package com.cfcc.itfe.client.para.tsstamptype;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsStamptypeDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author caoyg
 * @time   09-10-20 08:42:01
 * ��ϵͳ: Para
 * ģ��:TsStamptype
 * ���:TsStamptype
 */
public class TsStamptypeBean extends AbstractTsStamptypeBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TsStamptypeBean.class);
    public TsStamptypeBean() {
      super();
      dto = new TsStamptypeDto();
      pagingcontext = new PagingContext(this);
      init();            
    }
    
	/**
	 * Direction: ��ת¼�����
	 * ename: goInput
	 * ���÷���: 
	 * viewers: ¼�����
	 * messages: 
	 */
    public String goInput(Object o){
    	dto = new TsStamptypeDto();
        return super.goInput(o);
    }

	/**
	 * Direction: ¼�뱣��
	 * ename: inputSave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String inputSave(Object o){
    	if (!datacheck()) {
			return null;
		}
    	 try {
    		 tsStamptypeService.addInfo(dto);
 		} catch (Throwable e) {
 			log.error(e);
 			MessageDialog.openErrorDialog(null, e);
 			return super.inputSave(o);
 		}
 		MessageDialog.openMessageDialog(null,StateConstant.INPUTSAVE);
 		dto = new TsStamptypeDto();
 		init();
 		return super.backMaintenance(o);
         
    }

	/**
	 * Direction: ���ص�ά������
	 * ename: backMaintenance
	 * ���÷���: 
	 * viewers: ά������
	 * messages: 
	 */
    public String backMaintenance(Object o){
    	dto = new TsStamptypeDto();
		init();
		return super.backMaintenance(o);
  
    }

	/**
	 * Direction: ��ѡ
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	dto = (TsStamptypeDto) o;
		return super.singleSelect(o);
    }

	/**
	 * Direction: ɾ��
	 * ename: delete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if (dto == null || dto.columnSstamptypecode() == null) {
			MessageDialog.openMessageDialog(null,StateConstant.DELETESELECT);
			return "";
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(editor.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ɾ������ǩ������" + dto.getSstamptypecode())){
			return "";
		}
		try {
			tsStamptypeService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsStamptypeDto();
		init();
		return super.backMaintenance(o);
         
    }

	/**
	 * Direction: ���޸Ľ���
	 * ename: goModify
	 * ���÷���: 
	 * viewers: �޸Ľ���
	 * messages: 
	 */
    public String goModify(Object o){
    	if (dto == null || dto.getSstamptypecode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
          return super.goModify(o);
    }

	/**
	 * Direction: �޸ı���
	 * ename: modifySave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String modifySave(Object o){
    	if (!datacheck()) {
			return null;
		}
    	try {
    		tsStamptypeService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsStamptypeDto();
		return super.backMaintenance(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	try {
			return commonDataAccessService.findRsByDtoWithWherePaging(dto,
					pageRequest, "1=1");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}
    private void init() {
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
				.getDefault().getLoginInfo();
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}
    private boolean  datacheck(){
		if (null==dto.getSstamptypecode() ||"".equals(dto.getSstamptypecode().trim())) {
			MessageDialog.openMessageDialog(null, "ǩ�����Ͳ���Ϊ�գ�");
			return false;
		}
		if (null==dto.getSstamptypename() ||"".equals(dto.getSstamptypename().trim())) {
			MessageDialog.openMessageDialog(null, "ǩ���������Ʋ���Ϊ�գ���");
			return false;
		}
		if (null==dto.getSstamptypeid() ||"".equals(dto.getSstamptypeid().trim())) {
			MessageDialog.openMessageDialog(null, "ӡǩ��ʶ����Ϊ�գ�");
			return false;
		}
		return true;
		
	}

}
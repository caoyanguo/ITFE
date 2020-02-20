package com.cfcc.itfe.client.para.tstaxpayacct;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.para.tstaxpayacct.ITsTaxPayacctService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTaxpayacctDto;

/**
 * codecomment: 
 * @author db2admin
 * @time   14-06-18 15:19:53
 * ��ϵͳ: Para
 * ģ��:tsTaxPayacct
 * ���:TsTaxPayacct
 */
public class TsTaxPayacctBean extends AbstractTsTaxPayacctBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TsTaxPayacctBean.class);
    private ITFELoginInfo loginfo=null;
    TsTaxpayacctDto tempDto = null;
    public TsTaxPayacctBean() {
      super();
      dto = new TsTaxpayacctDto();
      queryDto = new TsTaxpayacctDto();
      pagingcontext = new PagingContext(this);
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
      init();
    }
    
    public void init(){
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		dto=new TsTaxpayacctDto();
		queryDto = new TsTaxpayacctDto();
    }
    
	/**
	 * Direction: ��ѯ
	 * ename: query
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String query(Object o){
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		if(pageResponse.getTotalCount()==0){
			MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), "��ѯ�޼�¼��");
		}
		pagingcontext.setPage(pageResponse);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.query(o);
    }

	/**
	 * Direction: ��ת��¼�����
	 * ename: goInput
	 * ���÷���: 
	 * viewers: ¼�����
	 * messages: 
	 */
    public String goInput(Object o){
    	dto=new TsTaxpayacctDto();
          return super.goInput(o);
    }

	/**
	 * Direction: ��ת���޸Ľ���
	 * ename: goModify
	 * ���÷���: 
	 * viewers: �޸Ľ���
	 * messages: 
	 */
    public String goModify(Object o){
    	if(dto.getSorgcode()==null||dto.getSorgcode().equals("")){
    		MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), "��ѡ��һ����¼��");
    		return "";
    	}
    	tempDto = new TsTaxpayacctDto();
    	tempDto.setStrecode(dto.getStrecode());
    	tempDto.setSpayeracct(dto.getSpayeracct());
    	tempDto.setStaxsubcode(dto.getStaxsubcode());
          return super.goModify(o);
    }

	/**
	 * Direction: ɾ��
	 * ename: delete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if(dto.getSorgcode()==null||dto.getSorgcode().equals("")){
    		MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), "��ѡ��һ����¼��");
    		return "";
    	}
    	if(!org.eclipse.jface.dialogs.MessageDialog.openQuestion(null, "��Ϣ��ʾ", "ȷ��ɾ���ü�¼��?")){
    		return "";
    	}
    	try {
			boolean flag=tsTaxPayacctService.delete(dto);
			if(flag){
				MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), "ɾ�����ݳɹ���");
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), "ɾ�����ݳ���:"+e.getMessage());
		}
		dto=new TsTaxpayacctDto();
		init();
    	this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
          return super.delete(o);
    }

	/**
	 * Direction: ¼�뱣��
	 * ename: inputSave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String inputSave(Object o){
    	dto.setSorgcode(loginfo.getSorgcode());
    	dto.setStaxorgcode(MsgConstant.MSG_TAXORG_PLACE_CLASS);
        dto.setStaxorgname("��˰����");
    	StringBuffer sbf = new StringBuffer();
    	if(StringUtils.isBlank(dto.getStrecode())){
    		sbf.append("����������벻��Ϊ�գ�\r\n");
    	}
    	if(StringUtils.isBlank(dto.getSpayeracct())){
    		sbf.append("�������˻�����Ϊ�գ�\r\n");
    	}
    	if(StringUtils.isBlank(dto.getStaxsubcode())){
    		sbf.append("��˰��Ŀ���벻��Ϊ�գ�\r\n");
    	}
    	if(StringUtils.isBlank(dto.getStaxsubname())){
    		sbf.append("��˰��Ŀ���Ʋ���Ϊ�գ�\r\n");
    	}
    	if(!sbf.toString().equals("")){
    		MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), sbf.toString());
    		return "";
    	}
    	try {
    		String info = verifyExists();
    		if(!info.equals("")){
    			MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), info);
    			return "";
    		}
			boolean flag=tsTaxPayacctService.addInfo(dto);
			if(flag){
				MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), "�������ݳɹ���");
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), "�������ݳ���:"+e.getMessage());
			return "";
		}
		dto=new TsTaxpayacctDto();
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
          return super.inputSave(o);
    }

	/**
	 * Direction: ���ز�ѯ����
	 * ename: goBack
	 * ���÷���: 
	 * viewers: ��ѯ������ʾ����
	 * messages: 
	 */
    public String goBack(Object o){
    	init();
    	this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
          return super.goBack(o);
    }

	/**
	 * Direction: �޸ı���
	 * ename: modifySave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String modifySave(Object o){
    	dto.setSorgcode(loginfo.getSorgcode());
    	dto.setStaxorgcode(MsgConstant.MSG_TAXORG_PLACE_CLASS);
        dto.setStaxorgname("��˰����");
    	StringBuffer sbf = new StringBuffer();
    	if(StringUtils.isBlank(dto.getStrecode())){
    		sbf.append("����������벻��Ϊ�գ�\r\n");
    	}
    	if(StringUtils.isBlank(dto.getSpayeracct())){
    		sbf.append("�������˻�����Ϊ�գ�\r\n");
    	}
    	if(StringUtils.isBlank(dto.getStaxsubcode())){
    		sbf.append("��˰��Ŀ���벻��Ϊ�գ�\r\n");
    	}
    	if(StringUtils.isBlank(dto.getStaxsubname())){
    		sbf.append("��˰��Ŀ���Ʋ���Ϊ�գ�\r\n");
    	}
    	if(!sbf.toString().equals("")){
    		MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), sbf.toString());
    		return "";
    	}
    	try {
    		String info = verifyModifySave();
    		if(!info.equals("")){
    			MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), info);
    			return "";
    		}
    		tempDto=null;
			boolean flag=tsTaxPayacctService.modifyInfo(dto);
			if(flag){
				MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), "�������ݳɹ���");
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), "�������ݳ���:"+e.getMessage());
		}
		dto=new TsTaxpayacctDto();
          return super.goBack(o);
    }

	/**
	 * Direction: ��ѡ
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	dto=(TsTaxpayacctDto) o;
          return super.singleSelect(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	try {
    		queryDto.setSorgcode(loginfo.getSorgcode());
			return commonDataAccessService.findRsByDtoWithWherePaging(queryDto,pageRequest, "1=1");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), e.getMessage());
		}
		return super.retrieve(pageRequest);
	}
    
    
    
    
    @SuppressWarnings("unchecked")
	public String verifyExists() throws ITFEBizException{
    	StringBuffer sbf = new StringBuffer("");
    	TsTaxpayacctDto verifyDto =new TsTaxpayacctDto();
    	verifyDto.setSorgcode(loginfo.getSorgcode());
    	verifyDto.setStaxorgcode(MsgConstant.MSG_TAXORG_PLACE_CLASS);
    	verifyDto.setStrecode(dto.getStrecode());
    	verifyDto.setStaxsubcode(dto.getStaxsubcode());
    	List<TsTaxpayacctDto> acctList = commonDataAccessService.findRsByDto(verifyDto);
		if(acctList!=null&&acctList.size()>0){
			sbf.append("�����Ŀ��["+acctList.get(0).getStrecode()+"-"+verifyDto.getStaxsubcode()+"]�µĸ������˻��Ѵ��ڣ������ظ�¼�룡");
		}else {
			TsTaxpayacctDto verifyDto1 =new TsTaxpayacctDto();
	    	verifyDto1.setSorgcode(loginfo.getSorgcode());
	    	verifyDto1.setStaxorgcode(MsgConstant.MSG_TAXORG_PLACE_CLASS);
	    	verifyDto1.setStrecode(dto.getStrecode());
	    	List<TsTaxpayacctDto> trecodeList = commonDataAccessService.findRsByDto(verifyDto1);
	    	for (TsTaxpayacctDto tsTaxpayacctDto : trecodeList) {
				if(tsTaxpayacctDto.getStaxsubcode().equals(dto.getStaxsubcode())){
					sbf.append("�ù����µĵ�˰��Ŀ�����Ѵ��ڣ������ظ�¼�룡");
				}
			}
		}
    	return sbf.toString();
    }
    
    
    @SuppressWarnings("unchecked")
	public String verifyModifySave() throws ITFEBizException{
    	StringBuffer sbf = new StringBuffer("");
		if(tempDto!=null){
		    	TsTaxpayacctDto verifyDto =new TsTaxpayacctDto();
		    	verifyDto.setSorgcode(loginfo.getSorgcode());
		    	verifyDto.setStaxorgcode(MsgConstant.MSG_TAXORG_PLACE_CLASS);
		    	verifyDto.setStrecode(dto.getStrecode());
		    	verifyDto.setStaxsubcode(dto.getStaxsubcode());
		    	List<TsTaxpayacctDto> acctList = commonDataAccessService.findRsByDto(verifyDto);
				if(acctList!=null&&acctList.size()>0){
					sbf.append("�����Ŀ��["+acctList.get(0).getStrecode()+"-"+verifyDto.getStaxsubcode()+"]�µĸ������˻��Ѵ��ڣ������ظ�¼�룡");
					return sbf.toString();
				}
    			TsTaxpayacctDto verifyDto1 =new TsTaxpayacctDto();
    	    	verifyDto1.setSorgcode(loginfo.getSorgcode());
    	    	verifyDto1.setStaxorgcode(MsgConstant.MSG_TAXORG_PLACE_CLASS);
    	    	verifyDto1.setStaxsubcode(dto.getStaxsubcode());
    	    	List<TsTaxpayacctDto> trecodeList = commonDataAccessService.findRsByDto(verifyDto1);
    	    	if(trecodeList!=null&&trecodeList.size()>0){
    	    		for (TsTaxpayacctDto tsTaxpayacctDto : trecodeList) {
						if(tsTaxpayacctDto.getStrecode().equals(dto.getStrecode())){
							sbf.append("�ù����µĵ�˰��Ŀ�����Ѵ��ڣ������ظ�¼�룡");
							return sbf.toString();
						}
					}
				}
		}
		return sbf.toString();
    }

}
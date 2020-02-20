package com.cfcc.itfe.client.para.tscorrreason;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.para.tscorrreason.ITsCorrReasonService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsCorrreasonDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;

/**
 * codecomment: 
 * @author t60
 * @time   12-02-21 09:24:09
 * ��ϵͳ: Para
 * ģ��:TsCorrReason
 * ���:TsCorrReason
 */
public class TsCorrReasonBean extends AbstractTsCorrReasonBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TsCorrReasonBean.class);
    private List list;
	private ITFELoginInfo loginfo = null;
	private List organlist;
    public TsCorrReasonBean() {
      super();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
    dto = new TsCorrreasonDto();
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
    	dto=new TsCorrreasonDto();
    	dto.setSbookorgcode(loginfo.getSorgcode());
          return super.goInput(o);
    }

	/**
	 * Direction: ¼�뱣��
	 * ename: inputSave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String inputSave(Object o){
    	try {
    		if(!check()){
    			return "";
    		}
			tsCorrReasonService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);

			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsCorrreasonDto();
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
    	dto = new TsCorrreasonDto();
    	dto.setSbookorgcode(loginfo.getSorgcode());
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
    	dto=(TsCorrreasonDto)o;
          return super.singleSelect(o);
    }

	/**
	 * Direction: ɾ��
	 * ename: delete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if (dto.getSbookorgcode()== null||dto.getStbscorrcode()==null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		try {
			tsCorrReasonService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsCorrreasonDto();
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
    	if (dto == null || dto.getSbookorgcode() == null||dto.getStbscorrcode()==null) {
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
    	try {
			tsCorrReasonService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsCorrreasonDto();
		return super.backMaintenance(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	TsCorrreasonDto tmpdto = new TsCorrreasonDto();
    	tmpdto.setSbookorgcode(loginfo.getSorgcode());
		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(tmpdto,
					pageRequest, "1=1");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}
    
    private void init() {
    	TsOrganDto orgdto = new TsOrganDto();
		orgdto.setSorgcode(loginfo.getSorgcode());
		try {
			organlist = commonDataAccessService.findRsByDto(orgdto);

		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
    	TsCorrreasonDto crDto=new TsCorrreasonDto();
		try {
			list = commonDataAccessService.findRsByDto(crDto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}
    public boolean check(){
    	//¼������
    	TsCorrreasonDto tmpdto=new TsCorrreasonDto();
    	tmpdto.setSbookorgcode(dto.getSbookorgcode());
    	tmpdto.setStbscorrcode(dto.getStbscorrcode());
    	try {
			List list = commonDataAccessService.findRsByDto(tmpdto);
			if (null != list && list.size() > 0) {
				MessageDialog.openMessageDialog(null,
						"��TBS����ԭ����롿�����ظ�!");
				return false;
			}
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return false;
		}
		return true;
    }

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public List getOrganlist() {
		return organlist;
	}

	public void setOrganlist(List organlist) {
		this.organlist = organlist;
	}
    
	
}
package com.cfcc.itfe.client.para.tsdrawbackreason;

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
import com.cfcc.itfe.service.para.tsdrawbackreason.ITsDrawbackreasonService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsConvertassitsignDto;
import com.cfcc.itfe.persistence.dto.TsDrawbackreasonDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;

/**
 * codecomment: 
 * @author yuanjiong
 * @time   12-02-20 15:43:51
 * ��ϵͳ: Para
 * ģ��:TsDrawbackreason
 * ���:TsDrawbackreason
 */
public class TsDrawbackreasonBean extends AbstractTsDrawbackreasonBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TsDrawbackreasonBean.class);
    private List list;
	private ITFELoginInfo loginfo = null;
	private List organlist;
    public TsDrawbackreasonBean() {
      super();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
      dto = new TsDrawbackreasonDto();
      dto.setSbookorgcode(loginfo.getSorgcode());
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
    	dto = new TsDrawbackreasonDto();
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
    	if(!check()){
    		return null;
    	}
    	try {
			tsDrawbackreasonService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);

			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsDrawbackreasonDto();
		dto.setSbookorgcode(loginfo.getSorgcode());
		init();
		return super.backMaintenance(o);
    }
    private boolean check(){
    	TsDrawbackreasonDto tmpdto=new TsDrawbackreasonDto();
    	tmpdto.setSbookorgcode(dto.getSbookorgcode());
    	tmpdto.setStbsdrawcode(dto.getStbsdrawcode());
    	try {
			List list = commonDataAccessService.findRsByDto(tmpdto);
			if (null != list && list.size() > 0) {
				MessageDialog.openMessageDialog(null,
						"�� TBS�˿�ԭ����롿�����ظ�!");
				return false;
			}
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return false;
		}
		return true;
    }
	/**
	 * Direction: ���ص�ά������
	 * ename: backMaintenance
	 * ���÷���: 
	 * viewers: ά������
	 * messages: 
	 */
    public String backMaintenance(Object o){
    	dto = new TsDrawbackreasonDto();
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
    	dto=(TsDrawbackreasonDto)o;
          return super.singleSelect(o);
    }

	/**
	 * Direction: ɾ��
	 * ename: delete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if (dto.getSbookorgcode()== null||dto.getStbsdrawcode()==null||dto.getStbsdrawcode().equals("")) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		try {
			tsDrawbackreasonService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsDrawbackreasonDto();
		dto.setSbookorgcode(loginfo.getSorgcode());
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
    	if (dto == null ||dto.getStbsdrawcode()==null||dto.getStbsdrawcode().equals("")) {
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
			tsDrawbackreasonService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsDrawbackreasonDto();
		dto.setSbookorgcode(loginfo.getSorgcode());
		return super.backMaintenance(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	TsDrawbackreasonDto tmpdto = new TsDrawbackreasonDto();
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
    	TsDrawbackreasonDto dbrDto=new TsDrawbackreasonDto();
    	dbrDto.setSbookorgcode(loginfo.getSorgcode());
		try {
			list = commonDataAccessService.findRsByDto(dbrDto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public List getOrganlist() {
		return organlist;
	}

	public void setOrganlist(List organlist) {
		this.organlist = organlist;
	}
    
}
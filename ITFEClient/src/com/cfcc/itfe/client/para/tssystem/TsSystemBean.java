package com.cfcc.itfe.client.para.tssystem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TsSystemDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author caoyg
 * @time 09-10-20 08:42:01 ��ϵͳ: Para ģ��:TsSystem ���:TsSystem
 */
public class TsSystemBean extends AbstractTsSystemBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsSystemBean.class);
	private TsSystemDto operdto = new TsSystemDto();
	private String smodicount;
	private ITFELoginInfo loginfo;
	
	private List list ;
	public TsSystemBean() {
		super();
		dto = new TsSystemDto();
		operdto = new TsSystemDto();
		pagingcontext = new PagingContext(this);
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
		init();
	}
	/**
	 * Direction: ��ת¼����� ename: goInput ���÷���: viewers: ¼����� messages:
	 */
	public String goInput(Object o) {
		if(!StateConstant.ORG_CENTER_CODE.equals(loginfo.getSorgcode())) {
			MessageDialog.openMessageDialog(null, "û�иù��ܵ�Ȩ�ޣ�����ϵ���Ĺ���Ա");
			return "";
		}
		dto = new TsSystemDto();
		try {
			list = commonDataAccessService.findRsByDto(dto);
		} catch (Throwable e1) {
			log.error(e1);
			MessageDialog.openErrorDialog(null, e1);
			return null;
		}
		if (list!=null &&list.size()>0) {
			MessageDialog.openMessageDialog(null, StateConstant.ONLYONEREC);
			return null;
		}
		return super.goInput(o);
	}
	/**
	 * Direction: ¼�뱣�� ename: inputSave ���÷���: viewers: * messages:
	 */
	public String inputSave(Object o) {
		/*if(!checkdate(dto.getSendadjustday())){
			return null;
		}*/
        if (dto.getIcleardays()==null||dto.getIcleardays()==null||dto.getImodicount()==null) {
			MessageDialog.openMessageDialog(null, StateConstant.CHECKVALID);
			return null;
		}		
        try {
        	String sdate = String.valueOf(DateUtil.getSystemYear())+"0631";
        	Date dd = DateUtil.stringToDate(sdate);
        	String ss = dto.getSendadjustday();
        	Date ds = DateUtil.stringToDate(ss);
        	if(ds.before(dd)){
        		MessageDialog.openMessageDialog(null, "�����ڽ�������Ӧ��С�ڵ��ڵ����6��30��");
        		return "";
        	}
			String msg = "��������ת������Ϊ1-90��,������������Ϊ90-365��;���������ת��������"+dto.getItransdays()+",��������������"+dto.getIcleardays()+",�Ƿ�ȷ�ϱ���?";
			boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
					null, "��ʾ", msg);
			if (flag) {
				tsSystemService.addInfo(dto);
			}
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null,StateConstant.INPUTSAVE);
		dto = new TsSystemDto();
		init();
		return super.backMaintenance(o);
	}
	

	/**
	 * Direction: ���ص�ά������ ename: backMaintenance ���÷���: viewers: ά������ messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsSystemDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsSystemDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: ɾ�� ename: delete ���÷���: viewers: * messages:
	 */
	public String delete(Object o) {
		if (dto == null || dto.getIcleardays() == null) {
			MessageDialog.openMessageDialog(null,StateConstant.DELETESELECT);
			return "";
		}
		try {
			tsSystemService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsSystemDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ���޸Ľ��� ename: goModify ���÷���: viewers: �޸Ľ��� messages:
	 */
	public String goModify(Object o) {
		if(!StateConstant.ORG_CENTER_CODE.equals(loginfo.getSorgcode())) {
			MessageDialog.openMessageDialog(null, "û�иù��ܵ�Ȩ�ޣ�����ϵ���Ĺ���Ա");
			return "";
		}
		if (dto == null || dto.getIcleardays() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		smodicount = dto.getImodicount().toString();
		return super.goModify(o);
	}
	/*private boolean checkdate(String date){
		try{
			date=date.trim();
			if(date.length()>4){
				throw new Exception();
			}
			date="2001"+date;
			Date date2=new SimpleDateFormat("yyyyMMdd").parse(date);
			return true;
		}catch(Exception ex){
			MessageDialog.openMessageDialog(null, "�����������������");
			return false;
		}
	}*/
	/**
	 * Direction: �޸ı��� ename: modifySave ���÷���: viewers: * messages:
	 */
	public String modifySave(Object o) {
		/*if(!checkdate(dto.getSendadjustday())){
			return null;
		}*/
		if(StringUtils.isBlank(smodicount)){
			smodicount = "0";
		}
		dto.setImodicount(Integer.valueOf(smodicount));
		if(null == dto.getImodicount()){
			dto.setImodicount(0);
		}
		
		if(0!= dto.getImodicount() && 1!= dto.getImodicount()){
			MessageDialog.openMessageDialog(null, "˰Ʊ����ֻ��ѡ��0������     1��������");
			editor.fireModelChanged();
			return backMaintenance(o);
		}
		
		try {
			String sdate = String.valueOf(DateUtil.getSystemYear())+"0631";
        	Date dd = CommonUtil.strToDate(sdate);
        	String ss = dto.getSendadjustday();
        	Date ds = CommonUtil.strToDate(ss);
        	if(ds.after(dd)){
        		MessageDialog.openMessageDialog(null, "�����ڽ�������Ӧ��С�ڵ��ڵ����6��30��");
        		return "";
        	}
			String msg = "��������ת������Ϊ1-90��,������������Ϊ90-365��;���������ת��������"+dto.getItransdays()+",��������������"+dto.getIcleardays()+",�Ƿ�ȷ�ϱ���?";
			boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
					null, "��ʾ", msg);
			if (flag) {
				tsSystemService.modInfo(dto);
				MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
			}
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		dto = new TsSystemDto();
		init();
		return super.backMaintenance(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
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
//		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
//				.getDefault().getLoginInfo();
		dto = new TsSystemDto();
		
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}
	public TsSystemDto getOperdto() {
		return operdto;
	}
	public void setOperdto(TsSystemDto operdto) {
		this.operdto = operdto;
	}
	public String getSmodicount() {
		return smodicount;
	}
	public void setSmodicount(String smodicount) {
		this.smodicount = smodicount;
	}
	
	

}
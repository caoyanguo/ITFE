package com.cfcc.itfe.client.para.tspayacctinfo;

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
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.para.tspayacctinfo.ITspayacctinfoService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsPayacctinfoDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment:
 * 
 * @author King
 * @time 13-06-25 14:56:44 ��ϵͳ: Para ģ��:tspayacctinfo ���:Tspayacctinfo
 */
public class TspayacctinfoBean extends AbstractTspayacctinfoBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TspayacctinfoBean.class);
	private ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService) getService(ICommonDataAccessService.class);
	private ITFELoginInfo loginfo;
	private List list2;
	private TsPayacctinfoDto modTmpDto;

	public TspayacctinfoBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		pagingcontext = new PagingContext(this);
		searchDto = new TsPayacctinfoDto();
		searchDto.setSorgcode(loginfo.getSorgcode());
		detailDto = new TsPayacctinfoDto();
		TsTreasuryDto tredto = new TsTreasuryDto();
		tredto.setSorgcode(loginfo.getSorgcode());
		try {
			list2 = commonDataAccessService.findRsByDto(tredto);
		} catch (Throwable e) {
			log.error("ȡ������Ӧ����������", e);
			return;
		}
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	/**
	 * Direction: ����� ename: singleclick ���÷���: viewers: * messages:
	 */
	public String singleclick(Object o) {
		detailDto = (TsPayacctinfoDto) o;
		return super.singleclick(o);
	}

	/**
	 * Direction: ��ת¼�� ename: goinput ���÷���: viewers: ��Ϣ¼�� messages:
	 */
	public String goinput(Object o) {
		detailDto = new TsPayacctinfoDto();
		detailDto.setSorgcode(loginfo.getSorgcode());
		return super.goinput(o);
	}

	/**
	 * Direction: ��ת�޸� ename: gomod ���÷���: viewers: �޸���Ϣ messages:
	 */
	public String gomod(Object o) {
		if (null == detailDto || StringUtils.isBlank(detailDto.getSorgcode())
				|| StringUtils.isBlank(detailDto.getSgenbankcode())) {
			MessageDialog.openMessageDialog(null, "��ѡ����Ҫ�޸ĵ���Ϣ��");
			return null;
		}
		modTmpDto = (TsPayacctinfoDto) detailDto.clone();
		return super.gomod(o);
	}

	/**
	 * Direction: ɾ����Ϣ ename: del ���÷���: viewers: * messages:
	 */
	public String del(Object o) {
		if (null == detailDto || StringUtils.isBlank(detailDto.getSorgcode())
				|| StringUtils.isBlank(detailDto.getSgenbankcode())) {
			MessageDialog.openMessageDialog(null, "��ѡ����Ҫ�޸ĵ���Ϣ��");
			return null;
		}
		boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
				null, "ɾ������ȷ��!", "�Ƿ�ȷ��Ҫɾ��������¼��");
		if (flag) {
			try {
				tspayacctinfoService.del(detailDto);
			} catch (Throwable e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
				return null;
			}
			MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
			detailDto = new TsPayacctinfoDto();
		}
		init();
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		return super.del(o);
	}

	/**
	 * Direction: ���� ename: save ���÷���: viewers: ������ѯһ���� messages:
	 */
	public String save(Object o) {
		TsPayacctinfoDto tmpDto = new TsPayacctinfoDto();
		tmpDto.setSorgcode(detailDto.getSorgcode());
		tmpDto.setSgenbankcode(detailDto.getSgenbankcode());
		tmpDto.setStrecode(detailDto.getStrecode());
		tmpDto.setSpayeracct(detailDto.getSpayeracct());
		tmpDto.setSpayeeacct(detailDto.getSpayeeacct());
		if(null==detailDto.getSgenbankcode()||"".equals(detailDto.getSgenbankcode())){
			MessageDialog.openMessageDialog(null, "���������кŲ���Ϊ�գ�");
			return null;
		}
		try {
			if(tspayacctinfoService.verifyPayeeBankNo(detailDto.getSgenbankcode())){
				List list = commonDataAccessService.findRsByDto(tmpDto);
				if(null == list || list .size() == 0){
					tspayacctinfoService.save(detailDto);
					MessageDialog.openMessageDialog(null, "�����ɹ���");
				}else{
					MessageDialog.openMessageDialog(null, "����������룺" + detailDto.getSorgcode() + ";������룺" + detailDto.getStrecode() + ";���������к�:" + detailDto.getSgenbankcode() + ";�������ʻ�:" + detailDto.getSpayeracct() + "�ظ���");
					return null;
				}
			}else{
				MessageDialog.openMessageDialog(null, "���������к�:" + detailDto.getSgenbankcode() + "֧��ϵͳ�к��в����ڣ�");
				return null;
			}
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			detailDto = new TsPayacctinfoDto();
			return null;
		}
		init();
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		return super.save(o);
	}

	/**
	 * Direction: ���� ename: gomain ���÷���: viewers: ������ѯһ���� messages:
	 */
	public String gomain(Object o) {
		init();
		detailDto = new TsPayacctinfoDto();
		this.editor.fireModelChanged();
		return super.gomain(o);
	}

	/**
	 * Direction: �޸���Ϣ ename: mod ���÷���: viewers: ������ѯһ���� messages:
	 */
	public String mod(Object o) {
		
		try {
			tspayacctinfoService.mod(modTmpDto, detailDto);
			MessageDialog.openMessageDialog(null, "�����ɹ���");
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
//			detailDto = new TsPayacctinfoDto();
			return null;
		}
		init();
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		return super.mod(o);
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
			return commonDataAccessService.findRsByDtoWithWherePaging(
					searchDto, pageRequest, null);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	public List getList2() {
		return list2;
	}

	public void setList2(List list2) {
		this.list2 = list2;
	}

}
package com.cfcc.itfe.client.para.tsconvertax;

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
import com.cfcc.itfe.service.para.tsconvertax.ITsconvertaxService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsConvertaxDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.pk.TsConvertaxPK;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 13-04-16 14:55:14 ��ϵͳ: Para ģ��:tsconvertax ���:Tsconvertax
 */
public class TsconvertaxBean extends AbstractTsconvertaxBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsconvertaxBean.class);
	private ITFELoginInfo loginfo;

	public TsconvertaxBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		searchDto = new TsConvertaxDto();
		searchDto.setSorgcode(loginfo.getSorgcode());
		detailDto = new TsConvertaxDto();
		oriDto = new TsConvertaxDto();
		searchresult = new ArrayList();
		init();
	}

	private void init() {
		try {
			searchresult.addAll(commonDataAccessService.findRsByDto(searchDto));
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "��ʼ����Ϣʧ�ܣ�");
		}

	}

	/**
	 * Direction: ��񵥻� ename: singleclick ���÷���: viewers: * messages:
	 */
	public String singleclick(Object o) {
		detailDto = (TsConvertaxDto) o;
		oriDto = (TsConvertaxDto) ((TsConvertaxDto) o).clone();
		return super.singleclick(o);
	}

	/**
	 * Direction: ����Ĭ��ҳ�� ename: gomainview ���÷���: viewers: * messages:
	 */
	public String gomainview(Object o) {
		detailDto = new TsConvertaxDto();
		return super.gomainview(o);
	}

	/**
	 * Direction: ɾ�� ename: del ���÷���: viewers: * messages:
	 */
	public String del(Object o) {
		if (null == detailDto || StringUtils.isBlank(detailDto.getStaxcode())) {
			MessageDialog.openMessageDialog(null, "��ѡ����Ҫɾ������Ϣ��");
			return null;
		}
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ��ɾ����")) {
			try {
				tsconvertaxService.delInfo(detailDto);
				MessageDialog.openMessageDialog(null, "�����ɹ���");
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "ɾ����Ϣʧ�ܣ�");
			} finally {
				detailDto = new TsConvertaxDto();
				search(o);
				this.editor
						.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
			}
		}
		return super.del(o);
	}

	/**
	 * Direction: ��ת¼��ҳ�� ename: goaddview ���÷���: viewers: ��Ϣ¼�� messages:
	 */
	public String goaddview(Object o) {
		detailDto = new TsConvertaxDto();
		detailDto.setSorgcode(loginfo.getSorgcode());
		return super.goaddview(o);
	}

	/**
	 * Direction: ��ת����ҳ�� ename: gomodview ���÷���: viewers: ��Ϣ�޸� messages:
	 */
	public String gomodview(Object o) {
		if(null == detailDto || StringUtils.isBlank(detailDto.getStaxcode())){
			MessageDialog.openMessageDialog(null, "��ѡ����Ҫ�޸ĵ���Ϣ��");
			return null;
		}
		return super.gomodview(o);
	}

	/**
	 * Direction: ��ѯ ename: search ���÷���: viewers: * messages:
	 */
	public String search(Object o) {
		try {
			searchresult = new ArrayList();
			searchresult.addAll(commonDataAccessService.findRsByDto(searchDto));
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "��ѯ��Ϣʧ�ܣ�");
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		return super.search(o);
	}

	/**
	 * Direction: �����Ϣ ename: addInfo ���÷���: viewers: ��ѯ messages:
	 */
	public String addInfo(Object o) {
		try {
			TsConvertaxPK convertaxPK = new TsConvertaxPK();
			convertaxPK.setSorgcode(loginfo.getSorgcode());
			convertaxPK.setStaxcode(detailDto.getStaxcode());
			TsConvertaxDto tmpDto = (TsConvertaxDto) commonDataAccessService
					.find(convertaxPK);
			if(null == tmpDto){
				tsconvertaxService.addInfo(detailDto);
			}else{
				MessageDialog.openMessageDialog(null, "�������ջ��ش���["
						+ detailDto.getStaxcode() + "]�Ѿ����ڣ�");
				return null;
			}
			detailDto = new TsConvertaxDto();
			MessageDialog.openMessageDialog(null, "�����ɹ���");
			search(o);
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "¼����Ϣʧ�ܣ�");
		}
		return super.addInfo(o);
	}

	/**
	 * Direction: �޸���Ϣ ename: modInfo ���÷���: viewers: ��ѯ messages:
	 */
	public String modInfo(Object o) {
		try {
			tsconvertaxService.modInfo(detailDto, oriDto);
			MessageDialog.openMessageDialog(null, "�����ɹ���");
			detailDto = new TsConvertaxDto();
			search(o);
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "�޸���Ϣʧ�ܣ�");
		}
		return super.modInfo(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

}
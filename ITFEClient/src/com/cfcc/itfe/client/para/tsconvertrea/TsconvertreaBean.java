package com.cfcc.itfe.client.para.tsconvertrea;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsConvertreaDto;
import com.cfcc.itfe.persistence.pk.TsConvertreaPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 13-04-16 12:22:22 ��ϵͳ: Para ģ��:tsconvertrea ���:Tsconvertrea
 */
public class TsconvertreaBean extends AbstractTsconvertreaBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsconvertreaBean.class);
	private ITFELoginInfo loginfo;
	private List trelist;
	private TsConvertreaDto oridto;

	public TsconvertreaBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		searchDto = new TsConvertreaDto();
		searchDto.setSorgcode(loginfo.getSorgcode());
		detailDto = new TsConvertreaDto();
		searchResult = new ArrayList();
		oridto = new TsConvertreaDto();
		init();
	}

	private void init() {
		try {
			trelist = TrelistByOrgcode.getTreList(loginfo.getSorgcode(),
					commonDataAccessService);
			searchResult.addAll(commonDataAccessService.findRsByDto(searchDto));
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "��ʼ����Ϣʧ�ܣ�");
		}

	}

	/**
	 * Direction: ��񵥻� ename: singleclick ���÷���: viewers: * messages:
	 */
	public String singleclick(Object o) {
		detailDto = (TsConvertreaDto) o;
		oridto = (TsConvertreaDto) ((TsConvertreaDto) o).clone();
		return super.singleclick(o);
	}

	/**
	 * Direction: ��ѯ ename: search ���÷���: viewers: * messages:
	 */
	public String search(Object o) {
		try {
			searchResult = new ArrayList();
			searchResult.addAll(commonDataAccessService.findRsByDto(searchDto));
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "��ѯ��Ϣʧ�ܣ�");
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		return super.search(o);
	}

	/**
	 * Direction: ɾ�� ename: del ���÷���: viewers: * messages:
	 */
	public String del(Object o) {
		if (null == detailDto  || StringUtils.isBlank(detailDto.getStrecode())) {
			MessageDialog.openMessageDialog(null, "��ѡ����Ҫɾ������Ϣ��");
			return null;
		}
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ��ɾ����")) {
			try {
				tsconvertreaService.delInfo(detailDto);
				MessageDialog.openMessageDialog(null, "�����ɹ���");
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "ɾ����Ϣʧ�ܣ�");
			} finally {
				detailDto = new TsConvertreaDto();
				search(o);
				this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
			}
		}
		return super.del(o);
	}

	/**
	 * Direction: ��ת�޸���Ϣҳ�� ename: gomodview ���÷���: viewers: �޸� messages:
	 */
	public String gomodview(Object o) {
		if (null == detailDto || StringUtils.isBlank(detailDto.getStrecode())) {
			MessageDialog.openMessageDialog(null, "��ѡ����Ҫ�޸ĵ���Ϣ��");
			return null;
		}
		return super.gomodview(o);
	}

	/**
	 * Direction: ��ת�����Ϣҳ�� ename: goaddview ���÷���: viewers: ���� messages:
	 */
	public String goaddview(Object o) {
		detailDto = new TsConvertreaDto();
		detailDto.setSorgcode(loginfo.getSorgcode());
		return super.goaddview(o);
	}

	/**
	 * Direction: ����Ĭ�Ͻ��� ename: gomainview ���÷���: viewers: ��ѯ messages:
	 */
	public String gomainview(Object o) {
		detailDto = new TsConvertreaDto();
		return super.gomainview(o);
	}

	/**
	 * Direction: �����Ϣ ename: addInfo ���÷���: viewers: ��ѯ messages:
	 */
	public String addInfo(Object o) {
		try {
			TsConvertreaPK tsConvertreaPK = new TsConvertreaPK();
			tsConvertreaPK.setSorgcode(loginfo.getSorgcode());
			tsConvertreaPK.setStrecode(detailDto.getStrecode());
			TsConvertreaDto tmpDto = (TsConvertreaDto) commonDataAccessService
					.find(tsConvertreaPK);
			if (null == tmpDto) {
				tsconvertreaService.addInfo(detailDto);
			} else {
				MessageDialog.openMessageDialog(null, "�����������["
						+ detailDto.getStrecode() + "]�Ѿ����ڣ�");
				return null;
			}
			detailDto = new TsConvertreaDto();
			MessageDialog.openMessageDialog(null, "�����ɹ���");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "¼����Ϣʧ�ܣ�");
		}

		search(o);
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		return super.addInfo(o);
	}

	/**
	 * Direction: �޸���Ϣ ename: modInfo ���÷���: viewers: ��ѯ messages:
	 */
	public String modInfo(Object o) {
		try {
			tsconvertreaService.modInfo(detailDto,oridto);
			MessageDialog.openMessageDialog(null, "�����ɹ���");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "�޸���Ϣʧ�ܣ�");
		} finally {
			detailDto = new TsConvertreaDto();
			search(o);
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
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

	public List getTrelist() {
		return trelist;
	}

	public void setTrelist(List trelist) {
		this.trelist = trelist;
	}

}
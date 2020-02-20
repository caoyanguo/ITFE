package com.cfcc.itfe.client.dataquery.tvincorrhandbookquery;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;

import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.dataquery.tvincorrhandbookquery.ITvInCorrhandbookQueryService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.file.DacctChooseDialog;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.HtvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;

/**
 * codecomment:
 * 
 * @author t60
 * @time 12-02-22 16:26:14 ��ϵͳ: DataQuery ģ��:TvInCorrhandbookQuery
 *       ���:TvInCorrhandbookQuery
 */
public class TvInCorrhandbookQueryBean extends
		AbstractTvInCorrhandbookQueryBean implements IPageDataProvider {

	private static Log log = LogFactory.getLog(TvInCorrhandbookQueryBean.class);
	private String selectedtable;
	private List statelist;
	private ITFELoginInfo loginfo;
	private List selectRs = null;

	public TvInCorrhandbookQueryBean() {
		super();
		selectedtable = "0";
		selectRs = new ArrayList();
		dto = new TvInCorrhandbookDto();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		dto.setSbookorgcode(loginfo.getSorgcode());
		init();
		pagingcontext = new PagingContext(this);

	}

	/**
	 * Direction: ��ѯ ename: search ���÷���: viewers: ������Ϣ�б� messages:
	 */
	public String search(Object o) {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		if (pageResponse == null || pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, " ��ѯ�޼�¼��");
			return super.reback(o);
		}
		return super.search(o);
	}

	public String updateFail(Object o) {
		if (selectRs == null || selectRs.size() <= 0) {
			MessageDialog.openMessageDialog(null, "��ѡ������һ����¼��");
			return null;
		}
		for (Object obj : (List<Object>) selectRs) {
			if (obj instanceof TvInCorrhandbookDto) {
				if (((TvInCorrhandbookDto) obj).getSstatus().equals(
						DealCodeConstants.DEALCODE_ITFE_SUCCESS)) {
					Boolean b = org.eclipse.jface.dialogs.MessageDialog
							.openQuestion(null, "��ʾ",
									"ѡ��ļ�¼�����Ѿ�����Ϊ�ɹ��ļ�¼,�Ƿ�һ�����Ϊʧ�ܣ�");
					if (b) {
						((TvInCorrhandbookDto) obj)
								.setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
					}
				}
			} else {
				if (((HtvInCorrhandbookDto) obj).getSstatus().equals(
						DealCodeConstants.DEALCODE_ITFE_SUCCESS)) {
					Boolean b = org.eclipse.jface.dialogs.MessageDialog
							.openQuestion(null, "��ʾ",
									"ѡ��ļ�¼�����Ѿ�����Ϊ�ɹ��ļ�¼,�Ƿ�һ�����Ϊʧ�ܣ�");
					if (b) {
						((HtvInCorrhandbookDto) obj)
								.setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
					}
				}
			}
		}
		try {
			tvInCorrhandbookQueryService.updateFail(selectRs);
			selectRs = new ArrayList();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		MessageDialog.openMessageDialog(null, "��������ʧ�ܳɹ���");
		selectRs = new ArrayList();
		return super.updateFail(o);
	}

	public String updateSuccess(Object o) {
		if (selectRs == null || selectRs.size() <= 0) {
			MessageDialog.openMessageDialog(null, "��ѡ������һ����¼��");
			return null;
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ",
				"�ò�������²�ѯ�������и������ĵ�״̬���Ƿ�����˲�����")) {
			return null;
		}
    	DacctChooseDialog dialog = new DacctChooseDialog(null);
    	String dacct="";
		if (IDialogConstants.OK_ID == dialog.open()) {
			dacct = dialog.getDacctstr();
		}

		try {
			
			for (Object obj : (List<Object>) selectRs) {
				if (obj instanceof TvInCorrhandbookDto) {
					if (((TvInCorrhandbookDto) obj).getSstatus().equals(
							DealCodeConstants.DEALCODE_ITFE_FAIL)) {
						Boolean b = org.eclipse.jface.dialogs.MessageDialog
								.openQuestion(null, "��ʾ",
										"ѡ��ļ�¼�����Ѿ�����Ϊʧ�ܵļ�¼,�Ƿ�һ�����Ϊ�ɹ���");
						if (b) {
							((TvInCorrhandbookDto) obj)
									.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
							((TvInCorrhandbookDto) obj).setDacct(new Date(new SimpleDateFormat("yyyyMMdd").parse(dacct).getTime()));
							
						}
					}

				} else {
					if (((HtvInCorrhandbookDto) obj).getSstatus().equals(
							DealCodeConstants.DEALCODE_ITFE_FAIL)) {
						Boolean b = org.eclipse.jface.dialogs.MessageDialog
								.openQuestion(null, "��ʾ",
										"ѡ��ļ�¼�����Ѿ�����Ϊʧ�ܵļ�¼,�Ƿ�һ�����Ϊ�ɹ���");
						if (b) {
							((HtvInCorrhandbookDto) obj)
									.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
							((HtvInCorrhandbookDto) obj).setDacct(new Date(new SimpleDateFormat("yyyyMMdd").parse(dacct).getTime()));
							
						
						}
					}
				}
			}
			
			tvInCorrhandbookQueryService.updateSuccess(selectRs);
			selectRs = new ArrayList();
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		MessageDialog.openMessageDialog(null, "�����������ݳɹ���");

		return this.search(0);
	}

	public String selectAll(Object o) {
		if (selectRs.size() == this.pagingcontext.getPage().getData().size()) {
			selectRs = new ArrayList();
			return "������Ϣ�б�";
		}
		selectRs = new ArrayList();
		selectRs.addAll(this.pagingcontext.getPage().getData());
		return "������Ϣ�б�";
	}

	/**
	 * Direction: ���� ename: reback ���÷���: viewers: ������ѯ���� messages:
	 */
	public String reback(Object o) {
		dto = new TvInCorrhandbookDto();
		dto.setSbookorgcode(loginfo.getSorgcode());
		return super.reback(o);
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
			dto.setSbookorgcode(loginfo.getSorgcode());
			if (selectedtable.equals("0")) {

				return commonDataAccessService.findRsByDtoWithWherePaging(dto,
						pageRequest, "1=1");
			} else if (selectedtable.equals("1")) {
				HtvInCorrhandbookDto htvdto = new HtvInCorrhandbookDto();
				htvdto.setIvousrlno(dto.getIvousrlno());
				htvdto.setSelecvouno(dto.getSelecvouno());
				htvdto.setScorrvouno(dto.getScorrvouno());
				htvdto.setDaccept(dto.getDaccept());
				htvdto.setCoribdgkind(dto.getCoribdgkind());
				htvdto.setCcurbdgkind(dto.getCcurbdgkind());
				htvdto.setSreasoncode(dto.getSreasoncode());
				htvdto.setSpackageno(dto.getSpackageno());
				htvdto.setSbookorgcode(dto.getSbookorgcode());
				return commonDataAccessService.findRsByDtoWithWherePaging(
						htvdto, pageRequest, "1=1");
			}

		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return null;
	}

	private void init() {
		this.statelist = new ArrayList<TdEnumvalueDto>();

		TdEnumvalueDto valuedto3 = new TdEnumvalueDto();
		valuedto3.setStypecode("��ʽ��");
		valuedto3.setSvalue("0");
		this.statelist.add(valuedto3);

		TdEnumvalueDto valuedto2 = new TdEnumvalueDto();
		valuedto2.setStypecode("��ʷ��");
		valuedto2.setSvalue("1");
		this.statelist.add(valuedto2);
	}

	public String getSelectedtable() {
		return selectedtable;
	}

	public void setSelectedtable(String selectedtable) {
		this.selectedtable = selectedtable;
	}

	public List getStatelist() {
		return statelist;
	}

	public void setStatelist(List statelist) {
		this.statelist = statelist;
	}

	public List getSelectRs() {
		return selectRs;
	}

	public void setSelectRs(List selectRs) {
		this.selectRs = selectRs;
	}

}
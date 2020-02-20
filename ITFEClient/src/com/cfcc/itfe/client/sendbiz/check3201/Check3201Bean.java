package com.cfcc.itfe.client.sendbiz.check3201;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvTips3201Dto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author zgz
 * @time 12-03-12 15:45:18 ��ϵͳ: SendBiz ģ��:Check3201 ���:Check3201
 */
public class Check3201Bean extends AbstractCheck3201Bean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(Check3201Bean.class);
	private ITFELoginInfo loginfo;

	public Check3201Bean() {
		super();

		schkdate = TimeFacade.getCurrentDateTime();
		resultList = new ArrayList();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
	}

	/**
	 * Direction: �˶� ename: doCheck ���÷���: viewers: * messages:
	 */
	public String doCheck(Object o) {
		TvTips3201Dto tv3201dto = new TvTips3201Dto();
		// �˶�����
		tv3201dto.setSchkdate(schkdate.toString().replace("-", ""));
		// �����������
		tv3201dto.setSorgcode(loginfo.getSorgcode());
		resultList = new ArrayList();
		try {
			// �����ҵ�������������Ϊ�Ѻ˶Գɹ�
			check3201Service.doCheck(tv3201dto);
			// ��ʾ�˶���Ϣ
			resultList = (ArrayList) commonDataAccessService
					.findRsByDto(tv3201dto);
			if (resultList == null || resultList.size() == 0) {
				MessageDialog.openMessageDialog(null, "û�к˶�����Ϊ"
						+ schkdate.toString() + "�Ĵ������˶԰���Ϣ��");
			} else {
				MessageDialog.openMessageDialog(null, "�˶���ɣ�");
			}
		} catch (ITFEBizException e) {
			log.error("�˶�ʧ�ܣ�", e);
			MessageDialog.openErrorDialog(null, e);
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.doCheck(o);
	}

	/**
	 * Direction: �ط����� ename: applyReport ���÷���: viewers: * messages:
	 */
	public String applyReport(Object o) {
		if (resultList == null || resultList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��˶Ժ����ط����룡");
			return super.applyReport(o);
		}

		try {
			for (int i = 0; i < resultList.size(); i++) {
				TvTips3201Dto tv3201dto = (TvTips3201Dto) resultList.get(i);
				// �˶�ʧ�����ط�����
				if (tv3201dto.getScheck() == null
						|| tv3201dto.getScheck().equals("0")) {
					check3201Service.doApply(tv3201dto);
				}
			}
			MessageDialog.openMessageDialog(null, "�ط�������ɣ�");
		} catch (ITFEBizException e) {
			log.error("�ط�����ʧ�ܣ�", e);
			MessageDialog.openErrorDialog(null, e);
		}

		return super.applyReport(o);
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

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

}
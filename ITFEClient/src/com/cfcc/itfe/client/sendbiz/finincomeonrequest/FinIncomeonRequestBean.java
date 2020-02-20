package com.cfcc.itfe.client.sendbiz.finincomeonrequest;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.recbiz.reportdownload.IRptDownloadService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author db2admin
 * @time 12-02-23 13:47:43 ��ϵͳ: SendBiz ģ��:FinIncomeonRequest
 *       ���:FinIncomeonRequest
 */
@SuppressWarnings("unchecked")
public class FinIncomeonRequestBean extends AbstractFinIncomeonRequestBean
		implements IPageDataProvider {

	private IRptDownloadService rptDownloadService = (IRptDownloadService) getService(IRptDownloadService.class);
	private static Log log = LogFactory.getLog(FinIncomeonRequestBean.class);
	private String sendorgcode;
	private String applydate;
	private ITFELoginInfo loginfo;
	private List<TsTreasuryDto> trelist;
	private String searchArea = null;

	public FinIncomeonRequestBean() {
		super();
		applydate = TimeFacade.getCurrentStringTimebefor();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		TsTreasuryDto tredto = new TsTreasuryDto();
		if (!StateConstant.ORG_CENTER_CODE.equals(loginfo.getSorgcode())) {
			tredto.setSorgcode(loginfo.getSorgcode());
		} else {
			tredto.setStreattrib(StateConstant.COMMON_YES);
		}
		try {
			// trelist =
			// commonDataAccessService.findRsByDto(tredto," order by S_TRELEVEL");
			trelist = commonDataAccessService.getSubTreCode(loginfo
					.getSorgcode());
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
	}

	/**
	 * Direction: ����˰Ʊ���� ename: request ���÷���: viewers: * messages:
	 */
	public String request(Object o) {
		try {
			if ((null == sendorgcode || sendorgcode.trim().length() == 0)
					&& (this.searchArea == null || "".equals(this.searchArea))) {
				if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(loginfo
						.getArea())) {
					MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ͱ�������Ĺ�����룡");
				} else {
					MessageDialog.openMessageDialog(null,
							"��ѡ��Ҫ���ͱ�������Ĺ�������ѡ�����뷶Χ��");
				}
				return null;
			}
			if (null == applydate || applydate.trim().length() == 0) {
				MessageDialog.openMessageDialog(null, "������ڲ���Ϊ�գ�");
				return null;
			}
			if (sendorgcode == null || "".equals(sendorgcode.trim())) {
				List<TsTreasuryDto> findTreCodeList = null;
				if ("0".equals(searchArea)) {
					TsTreasuryDto findto = new TsTreasuryDto();
					findto.setSorgcode(loginfo.getSorgcode());
					findTreCodeList = commonDataAccessService
							.findRsByDto(findto);
				} else if ("1".equals(searchArea)) {
					findTreCodeList = commonDataAccessService
							.getSubTreCode(loginfo.getSorgcode());
				}
				for (int i = 0; i < findTreCodeList.size(); i++)
					taxorgapplyintreService.sendMsg(findTreCodeList.get(i)
							.getStrecode(), applydate, MsgConstant.MSG_NO_5003);
			} else
				taxorgapplyintreService.sendMsg(sendorgcode, applydate,
						MsgConstant.MSG_NO_5003);
			MessageDialog.openMessageDialog(null, "���ͳɹ���");
		} catch (ITFEBizException e) {
			log.error("����ʧ�ܣ�", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.request(o);
	}

	@Override
	public String sendAll(Object o) {
		if (!StateConstant.ORG_CENTER_CODE.equals(loginfo.getSorgcode())) {
			MessageDialog.openMessageDialog(null, "�������û�,��Ȩ�ޣ�");
			return null;
		}
		if (null == applydate || "".equals(applydate.trim())) {
			MessageDialog.openMessageDialog(null, "������ڲ���Ϊ�գ�");
			return super.sendAll(o);
		}
		StringBuffer result = new StringBuffer();
		try {
			List<TsTreasuryDto> listtre = commonDataAccessService
					.findAllDtos(TsTreasuryDto.class);
			for (TsTreasuryDto tmpTre : listtre) {
				TsConvertfinorgDto finorg = new TsConvertfinorgDto();
				finorg.setStrecode(tmpTre.getStrecode());
				finorg.setSorgcode(tmpTre.getSorgcode());
				List<TsConvertfinorgDto> listfin = commonDataAccessService
						.findRsByDto(finorg);
				if (null == listfin || listfin.size() == 0) {
					result.append(tmpTre.getStrename()
							+ "û��ά����Ӧ�Ĳ����������룬�޷���������!\r");
				} else {
					rptDownloadService.sendApplyInfo(applydate,
							MsgConstant.MSG_NO_5003, listfin.get(0)
									.getSfinorgcode(), tmpTre.getSorgcode());
				}
			}
			if (StringUtils.isBlank(result.toString())) {
				MessageDialog.openMessageDialog(null, "���ͱ�������ɹ���");
			} else {
				MessageDialog.openMessageDialog(null, result.toString());
			}
		} catch (Exception e) {
			MessageDialog.openMessageDialog(null, e.toString());
			return null;
		}
		return super.sendAll(o);
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

	public String getSendorgcode() {
		return sendorgcode;
	}

	public void setSendorgcode(String sendorgcode) {
		this.sendorgcode = sendorgcode;
		if (this.sendorgcode != null && !"".equals(this.sendorgcode))
			this.searchArea = "";
		editor.fireModelChanged();
	}

	public String getApplydate() {
		return applydate;
	}

	public void setApplydate(String applydate) {
		this.applydate = applydate;
	}

	public List<TsTreasuryDto> getTrelist() {
		return trelist;
	}

	public void setTrelist(List<TsTreasuryDto> trelist) {
		this.trelist = trelist;
	}

	public String getSearchArea() {
		return searchArea;
	}

	public void setSearchArea(String searchArea) {
		this.searchArea = searchArea;
		if (this.searchArea != null && !"".equals(this.searchArea))
			sendorgcode = "";
		editor.fireModelChanged();
	}

}
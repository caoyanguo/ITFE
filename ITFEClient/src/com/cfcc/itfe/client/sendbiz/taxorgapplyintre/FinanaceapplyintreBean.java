package com.cfcc.itfe.client.sendbiz.taxorgapplyintre;

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
 * @time 12-02-23 13:47:43 子系统: SendBiz 模块:taxorgapplyintre
 *       组件:Finanaceapplyintre
 */
@SuppressWarnings("unchecked")
public class FinanaceapplyintreBean extends AbstractFinanaceapplyintreBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(FinanaceapplyintreBean.class);
	private IRptDownloadService rptDownloadService = (IRptDownloadService) getService(IRptDownloadService.class);
	private String sendOrgCode;
	private String inTreDate;
	private List trelist = null;
	private ITFELoginInfo loginfo = null;
	private String searchArea = null;// 辖属标志

	public FinanaceapplyintreBean() {
		super();
		inTreDate = TimeFacade.getCurrentStringTimebefor();
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
	 * Direction: 申请 ename: applyReport 引用方法: viewers: * messages:
	 */
	public String applyReport(Object o) {
		try {
			if ((null == sendOrgCode || sendOrgCode.trim().length() == 0)
					&& (this.searchArea == null || "".equals(this.searchArea))) {
				if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(loginfo
						.getArea())) {
					MessageDialog.openMessageDialog(null, "请选择要发送报表申请的国库代码！");
				} else {
					MessageDialog.openMessageDialog(null,
							"请选择要发送报表申请的国库代码或选择申请范围！");
				}
				return null;
			}
			if (null == inTreDate || inTreDate.trim().length() == 0) {
				MessageDialog.openMessageDialog(null, "入库日期不能为空！");
				return null;
			}
			if (sendOrgCode == null || "".equals(sendOrgCode.trim())) {
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
							.getStrecode(), inTreDate, MsgConstant.MSG_NO_5002);
			} else
				taxorgapplyintreService.sendMsg(sendOrgCode, inTreDate,
						MsgConstant.MSG_NO_5002);
			MessageDialog.openMessageDialog(null, "发送成功！");
		} catch (ITFEBizException e) {
			log.error("申请失败！", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.applyReport(o);
	}

	@Override
	public String sendAll(Object o) {
		if (!StateConstant.ORG_CENTER_CODE.equals(loginfo.getSorgcode())) {
			MessageDialog.openMessageDialog(null, "非中心用户,无权限！");
			return null;
		}
		if (null == inTreDate || "".equals(inTreDate.trim())) {
			MessageDialog.openMessageDialog(null, "入库日期不能为空！");
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
							+ "没有维护对应的财政机构代码，无法申请数据!\r");
				} else {
					rptDownloadService.sendApplyInfo(inTreDate,
							MsgConstant.MSG_NO_5002, listfin.get(0)
									.getSfinorgcode(), tmpTre.getSorgcode());
				}
			}
			if (StringUtils.isBlank(result.toString())) {
				MessageDialog.openMessageDialog(null, "发送报表申请成功！");
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

	public String getSendOrgCode() {
		return sendOrgCode;
	}

	public void setSendOrgCode(String sendOrgCode) {
		this.sendOrgCode = sendOrgCode;
		if (this.sendOrgCode != null && !"".equals(this.sendOrgCode))
			this.searchArea = "";
		editor.fireModelChanged();
	}

	public String getInTreDate() {
		return inTreDate;
	}

	public void setInTreDate(String inTreDate) {
		this.inTreDate = inTreDate;
	}

	public List getTrelist() {
		return trelist;
	}

	public void setTrelist(List trelist) {
		this.trelist = trelist;
	}

	public String getSearchArea() {
		return searchArea;
	}

	public void setSearchArea(String searchArea) {
		this.searchArea = searchArea;
		if (this.searchArea != null && !"".equals(this.searchArea))
			sendOrgCode = "";
		editor.fireModelChanged();
	}

}
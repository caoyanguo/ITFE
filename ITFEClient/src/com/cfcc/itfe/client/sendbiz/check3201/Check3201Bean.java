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
 * @time 12-03-12 15:45:18 子系统: SendBiz 模块:Check3201 组件:Check3201
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
	 * Direction: 核对 ename: doCheck 引用方法: viewers: * messages:
	 */
	public String doCheck(Object o) {
		TvTips3201Dto tv3201dto = new TvTips3201Dto();
		// 核对日期
		tv3201dto.setSchkdate(schkdate.toString().replace("-", ""));
		// 核算主体代码
		tv3201dto.setSorgcode(loginfo.getSorgcode());
		resultList = new ArrayList();
		try {
			// 将能找到处理结果包的置为已核对成功
			check3201Service.doCheck(tv3201dto);
			// 显示核对信息
			resultList = (ArrayList) commonDataAccessService
					.findRsByDto(tv3201dto);
			if (resultList == null || resultList.size() == 0) {
				MessageDialog.openMessageDialog(null, "没有核对日期为"
						+ schkdate.toString() + "的处理结果核对包信息！");
			} else {
				MessageDialog.openMessageDialog(null, "核对完成！");
			}
		} catch (ITFEBizException e) {
			log.error("核对失败！", e);
			MessageDialog.openErrorDialog(null, e);
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.doCheck(o);
	}

	/**
	 * Direction: 重发申请 ename: applyReport 引用方法: viewers: * messages:
	 */
	public String applyReport(Object o) {
		if (resultList == null || resultList.size() == 0) {
			MessageDialog.openMessageDialog(null, "请核对后再重发申请！");
			return super.applyReport(o);
		}

		try {
			for (int i = 0; i < resultList.size(); i++) {
				TvTips3201Dto tv3201dto = (TvTips3201Dto) resultList.get(i);
				// 核对失败则重发申请
				if (tv3201dto.getScheck() == null
						|| tv3201dto.getScheck().equals("0")) {
					check3201Service.doApply(tv3201dto);
				}
			}
			MessageDialog.openMessageDialog(null, "重发申请完成！");
		} catch (ITFEBizException e) {
			log.error("重发申请失败！", e);
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
package com.cfcc.itfe.client.sendbiz.finincomecheckresendrequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author t60
 * @time   12-02-24 10:44:42
 * 子系统: SendBiz
 * 模块:finincomecheckresendrequest
 * 组件:Finincomecheckresendrequest
 */
public class FinincomecheckresendrequestBean extends AbstractFinincomecheckresendrequestBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(FinincomecheckresendrequestBean.class);
    private TvSendlogDto dto;
	private ITFELoginInfo loginfo = null;

	public FinincomecheckresendrequestBean() {
		super();
		dto = new TvSendlogDto();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		dto.setSsendorgcode(loginfo.getSorgcode());
		dto.setSdate(TimeFacade.getCurrentStringTime());
	}

	/**
	 * Direction: 发送 ename: send 引用方法: viewers: * messages:
	 */
	public String send(Object o) {
		
		if (null == dto.getSsendorgcode() || "".equals(dto.getSsendorgcode().trim())) {
			MessageDialog.openMessageDialog(null, "请填写发起机构代码！");
			return super.send(o);
		}
		
		//中心核算主体代码
		String centerorg=null;
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		if (!loginfo.getSorgcode().equals(centerorg)) {
			
			if((dto.getSsendorgcode()==null || "".equals(dto.getSsendorgcode()))){
				MessageDialog.openMessageDialog(null, " 请填写用户所属的核算主体代码！");
				return null;
			}
			
			if(!dto.getSsendorgcode().equals(loginfo.getSorgcode())){
				MessageDialog.openMessageDialog(null, " 权限不够，请填写当前用户所属的核算主体代码！");
				return super.send(o);
			}
		}
		
		if (null == dto.getSdate() || "".equals(dto.getSdate().trim())) {
			MessageDialog.openMessageDialog(null, "请填写委托日期！");
			return super.send(o);
		}
		
		if (null == dto.getSoperationtypecode()
				|| "".equals(dto.getSoperationtypecode().trim())) {
			MessageDialog.openMessageDialog(null, "原包报文编号不能为空！");
			return null;
		}
		if (null == dto.getSsenddate() || "".equals(dto.getSsenddate().trim())) {
			MessageDialog.openMessageDialog(null, "原对账日期不能为空！");
			return null;
		}
		if (null == dto.getSifsend() || "".equals(dto.getSifsend().trim())) {
			MessageDialog.openMessageDialog(null, "原文件对账类型不能为空！");
			return null;
		}
		try {
			finincomecheckresendrequestService.sendMsg(dto);
			MessageDialog.openMessageDialog(null, "申请报文发送成功！");
		} catch (Throwable e) {
			log.error("调用后台发送报文时发生错误", e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.send(o);
	}

	/**
	 * Direction: 关闭 ename: close 引用方法: viewers: * messages:
	 */
	public String close(Object o) {
		return super.close(o);
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

	public TvSendlogDto getDto() {
		return dto;
	}

	public void setDto(TvSendlogDto dto) {
		this.dto = dto;
	}


}
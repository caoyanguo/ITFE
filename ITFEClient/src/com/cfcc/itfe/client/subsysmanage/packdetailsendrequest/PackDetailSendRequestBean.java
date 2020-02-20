package com.cfcc.itfe.client.subsysmanage.packdetailsendrequest;

import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.persistence.dto.ContentDto;

/**
 * codecomment:
 * 
 * @author caoyg
 * @time 09-11-30 11:02:47 子系统: SubSysManage 模块:PackDetailSendRequest
 *       组件:PackDetailSendRequest
 */
public class PackDetailSendRequestBean extends AbstractPackDetailSendRequestBean implements IPageDataProvider {

	private ContentDto dto = null;
	private  String oriPackMsgNo = "";
    private  String oriSendOrgCode = "";
	private  String oriEntrustDate = "";
	private  String oriPackNo = "";

	public PackDetailSendRequestBean() {
		super();
		dto = new ContentDto();
	}

	/**
	 * Direction: 发送 ename: send 引用方法: viewers: * messages:
	 */
	public String send(Object o) {
		if (null == oriPackMsgNo || "".equals(oriPackMsgNo.trim())) {
			MessageDialog.openMessageDialog(null, "原报文编号不能为空！");
			return null;
		}else {
			if(oriPackMsgNo.length() > 4) {
				MessageDialog.openMessageDialog(null, "原报文编号长度错误!");
				return null;
			}			
		}
		if (null == oriSendOrgCode || "".equals(oriSendOrgCode.trim())) {
			MessageDialog.openMessageDialog(null, "原发送机构代码不能为空！");
			return null;
		}
		if (null == oriEntrustDate || "".equals(oriEntrustDate.trim())) {
			MessageDialog.openMessageDialog(null, "原委托日期不能为空！");
			return null;
		}
		if (null == oriPackNo || "".equals(oriPackNo.trim())) {
			MessageDialog.openMessageDialog(null, "原包流水号不能为空！");
			return null;
		}
         dto.set_OriEntrustDate(oriEntrustDate);
         dto.set_OriPackMsgNo(oriPackMsgNo);
         dto.set_OriPackNo(oriPackNo);
         dto.set_OriSendOrgCode(oriSendOrgCode);
		try {
			packDetailSendRequestService.sendRequestMsg(dto);
			MessageDialog.openMessageDialog(null, "包明细重发请求报文[9111]发送成功！");
		} catch (Throwable e) {
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

	public ContentDto getDto() {
		return dto;
	}

	public void setDto(ContentDto dto) {
		this.dto = dto;
	}

	public String getOriPackMsgNo() {
		return oriPackMsgNo;
	}

	public void setOriPackMsgNo(String oriPackMsgNo) {
		this.oriPackMsgNo = oriPackMsgNo;
	}

	public String getOriSendOrgCode() {
		return oriSendOrgCode;
	}

	public void setOriSendOrgCode(String oriSendOrgCode) {
		this.oriSendOrgCode = oriSendOrgCode;
	}

	public String getOriEntrustDate() {
		return oriEntrustDate;
	}

	public void setOriEntrustDate(String oriEntrustDate) {
		this.oriEntrustDate = oriEntrustDate;
	}

	public String getOriPackNo() {
		return oriPackNo;
	}

	public void setOriPackNo(String oriPackNo) {
		this.oriPackNo = oriPackNo;
	}

	

}
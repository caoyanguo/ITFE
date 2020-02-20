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
 * @time 09-11-30 11:02:47 ��ϵͳ: SubSysManage ģ��:PackDetailSendRequest
 *       ���:PackDetailSendRequest
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
	 * Direction: ���� ename: send ���÷���: viewers: * messages:
	 */
	public String send(Object o) {
		if (null == oriPackMsgNo || "".equals(oriPackMsgNo.trim())) {
			MessageDialog.openMessageDialog(null, "ԭ���ı�Ų���Ϊ�գ�");
			return null;
		}else {
			if(oriPackMsgNo.length() > 4) {
				MessageDialog.openMessageDialog(null, "ԭ���ı�ų��ȴ���!");
				return null;
			}			
		}
		if (null == oriSendOrgCode || "".equals(oriSendOrgCode.trim())) {
			MessageDialog.openMessageDialog(null, "ԭ���ͻ������벻��Ϊ�գ�");
			return null;
		}
		if (null == oriEntrustDate || "".equals(oriEntrustDate.trim())) {
			MessageDialog.openMessageDialog(null, "ԭί�����ڲ���Ϊ�գ�");
			return null;
		}
		if (null == oriPackNo || "".equals(oriPackNo.trim())) {
			MessageDialog.openMessageDialog(null, "ԭ����ˮ�Ų���Ϊ�գ�");
			return null;
		}
         dto.set_OriEntrustDate(oriEntrustDate);
         dto.set_OriPackMsgNo(oriPackMsgNo);
         dto.set_OriPackNo(oriPackNo);
         dto.set_OriSendOrgCode(oriSendOrgCode);
		try {
			packDetailSendRequestService.sendRequestMsg(dto);
			MessageDialog.openMessageDialog(null, "����ϸ�ط�������[9111]���ͳɹ���");
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return super.send(o);
	}

	/**
	 * Direction: �ر� ename: close ���÷���: viewers: * messages:
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
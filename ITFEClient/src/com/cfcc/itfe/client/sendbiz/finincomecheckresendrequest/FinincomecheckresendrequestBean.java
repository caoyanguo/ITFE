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
 * ��ϵͳ: SendBiz
 * ģ��:finincomecheckresendrequest
 * ���:Finincomecheckresendrequest
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
	 * Direction: ���� ename: send ���÷���: viewers: * messages:
	 */
	public String send(Object o) {
		
		if (null == dto.getSsendorgcode() || "".equals(dto.getSsendorgcode().trim())) {
			MessageDialog.openMessageDialog(null, "����д����������룡");
			return super.send(o);
		}
		
		//���ĺ����������
		String centerorg=null;
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		if (!loginfo.getSorgcode().equals(centerorg)) {
			
			if((dto.getSsendorgcode()==null || "".equals(dto.getSsendorgcode()))){
				MessageDialog.openMessageDialog(null, " ����д�û������ĺ���������룡");
				return null;
			}
			
			if(!dto.getSsendorgcode().equals(loginfo.getSorgcode())){
				MessageDialog.openMessageDialog(null, " Ȩ�޲���������д��ǰ�û������ĺ���������룡");
				return super.send(o);
			}
		}
		
		if (null == dto.getSdate() || "".equals(dto.getSdate().trim())) {
			MessageDialog.openMessageDialog(null, "����дί�����ڣ�");
			return super.send(o);
		}
		
		if (null == dto.getSoperationtypecode()
				|| "".equals(dto.getSoperationtypecode().trim())) {
			MessageDialog.openMessageDialog(null, "ԭ�����ı�Ų���Ϊ�գ�");
			return null;
		}
		if (null == dto.getSsenddate() || "".equals(dto.getSsenddate().trim())) {
			MessageDialog.openMessageDialog(null, "ԭ�������ڲ���Ϊ�գ�");
			return null;
		}
		if (null == dto.getSifsend() || "".equals(dto.getSifsend().trim())) {
			MessageDialog.openMessageDialog(null, "ԭ�ļ��������Ͳ���Ϊ�գ�");
			return null;
		}
		try {
			finincomecheckresendrequestService.sendMsg(dto);
			MessageDialog.openMessageDialog(null, "���뱨�ķ��ͳɹ���");
		} catch (Throwable e) {
			log.error("���ú�̨���ͱ���ʱ��������", e);
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

	public TvSendlogDto getDto() {
		return dto;
	}

	public void setDto(TvSendlogDto dto) {
		this.dto = dto;
	}


}
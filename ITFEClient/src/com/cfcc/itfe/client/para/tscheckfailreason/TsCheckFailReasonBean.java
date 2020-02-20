package com.cfcc.itfe.client.para.tscheckfailreason;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.para.tscheckfailreason.ITsCheckFailReasonService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsCheckfailreasonDto;

/**
 * codecomment:
 * 
 * @author db2itfe
 * @time 13-09-04 10:33:40 ��ϵͳ: Para ģ��:TsCheckFailReason ���:TsCheckFailReason
 */
public class TsCheckFailReasonBean extends AbstractTsCheckFailReasonBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(TsCheckFailReasonBean.class);
	private ITFELoginInfo loginfo= (ITFELoginInfo) ApplicationActionBarAdvisor
	.getDefault().getLoginInfo();
	private PageRequest request = new PageRequest();
	public TsCheckFailReasonBean() {
		super();
		dto = new TsCheckfailreasonDto();
		pagingcontext = new PagingContext(null);
		request = new PageRequest();
		retrieve(request);
	}

	/**
	 * Direction: ��ѯ ename: search ���÷���: viewers: ����ʧ��ԭ��ά��ҳ messages:
	 */
	public String search(Object o) {
		retrieve(request);
		return super.search(o);
	}

	/**
	 * Direction: ¼�� ename: newInput ���÷���: viewers: ����ʧ��ԭ��¼��ҳ messages:
	 */
	public String newInput(Object o) {
		dto = new TsCheckfailreasonDto();
		dto.setSorgcode(loginfo.getSorgcode());
		return super.newInput(o);
	}

	/**
	 * Direction: �޸���ת ename: updateDireck ���÷���: viewers: ����ʧ��ԭ���޸�ҳ messages:
	 */
	public String updateDireck(Object o) {
		return super.updateDireck(o);
	}

	/**
	 * Direction: ���� ename: save ���÷���: viewers: ����ʧ��ԭ��ά��ҳ messages:
	 */
	public String save(Object o) {
		try {
			TsCheckfailreasonDto checkdouble = new TsCheckfailreasonDto();
			checkdouble.setSorgcode(loginfo.getSorgcode());// �����������
			checkdouble.setScheckfailcode(dto.getScheckfailcode());
			List<TsCheckfailreasonDto> list = commonDataAccessService.findRsByDto(checkdouble);
			if(list!=null && list.size()>0){
				MessageDialog.openMessageDialog(null,"ʧ��ԭ������ظ������������룡");
				return "";
			}else{
				commonDataAccessService.create(dto);
			}
			MessageDialog.openMessageDialog(null,"����ɹ���");
		} catch (ITFEBizException e) {
			log.error("����ʧ��ԭ����Ϣ����", e);
			MessageDialog.openErrorDialog(null, e);
		}
		retrieve(request);
		return super.save(o);
	}

	/**
	 * Direction: �޸� ename: update ���÷���: viewers: ����ʧ��ԭ��ά��ҳ messages:
	 */
	public String update(Object o) {
		try {
			commonDataAccessService.updateData(dto);
			MessageDialog.openMessageDialog(null,"����ʧ��ԭ���޸ĳɹ���");
		} catch (ITFEBizException e) {
			log.error("�޸�ʧ��ԭ����Ϣ����", e);
			MessageDialog.openErrorDialog(null, e);
		}
		retrieve(request);
		return super.update(o);
	}

	/**
	 * Direction: ȡ�� ename: exit ���÷���: viewers: ����ʧ��ԭ��ά��ҳ messages:
	 */
	public String exit(Object o) {
		retrieve(request);
		return super.exit(o);
	}

	/**
	 * Direction: ɾ�� ename: delete ���÷���: viewers: ����ʧ��ԭ��ά��ҳ messages:
	 */
	public String delete(Object o) {
		try {
			if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(
					this.editor.getCurrentComposite().getShell(), StateConstant.TIPS,
					StateConstant.DELETECONFIRM)) {
				return null;
			} 
			
			commonDataAccessService.delete(dto);
			MessageDialog.openMessageDialog(null,"����ʧ��ԭ��ɾ���ɹ���");
		} catch (ITFEBizException e) {
			log.error("ɾ��ʧ��ԭ����Ϣ����", e);
			MessageDialog.openErrorDialog(null, e);
		}
		retrieve(request);
		return super.delete(o);
	}

	 /**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsCheckfailreasonDto) o;
		return super.singleSelect(o);
	}
	/**
	 * Direction: ˫����ת�޸� ename: doubleclickToUpdate ���÷���: viewers: ����ʧ��ԭ���޸�ҳ
	 * messages:
	 */
	public String doubleclickToUpdate(Object o) {
		dto = (TsCheckfailreasonDto) o;
		return super.doubleclickToUpdate(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest arg0) {
		TsCheckfailreasonDto dto = new TsCheckfailreasonDto();
		dto.setSorgcode(loginfo.getSorgcode());
		try {
			PageResponse response = commonDataAccessService
					.findRsByDtoWithWherePaging(dto, request, " 1=1 ");
			pagingcontext.getPage().getData().clear();
			pagingcontext.getPage().setData(response.getData());
		} catch (ITFEBizException e) {
			log.error("��ѯʧ��ԭ����Ϣ����", e);
			MessageDialog.openErrorDialog(null, e);
		}catch(Exception e){
			e.printStackTrace();
		}
		return super.retrieve(arg0);
	}

}
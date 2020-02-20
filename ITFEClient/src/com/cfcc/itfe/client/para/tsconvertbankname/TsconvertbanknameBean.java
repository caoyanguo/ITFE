package com.cfcc.itfe.client.para.tsconvertbankname;

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
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsConvertbanknameDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.para.tsconvertbankname.ITsconvertbanknameService;

/**
 * codecomment:
 * 
 * @author King
 * @time 13-05-24 10:10:53 ��ϵͳ: Para ģ��:tsconvertbankname ���:Tsconvertbankname
 */
public class TsconvertbanknameBean extends AbstractTsconvertbanknameBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(TsconvertbanknameBean.class);
	private ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService) getService(ICommonDataAccessService.class);
	private ITsconvertbanknameService tsconvertbanknameService = (ITsconvertbanknameService) getService(ITsconvertbanknameService.class);
	private ITFELoginInfo loginfo;
	private TsConvertbanknameDto oldDto;
	public TsconvertbanknameBean() {
		super();
		oldDto = new TsConvertbanknameDto();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		resultlist = new ArrayList();
		searchDto = new TsConvertbanknameDto();
		searchDto.setSorgcode(loginfo.getSorgcode());
		detailDto = new TsConvertbanknameDto();
		init();
	}

	private void init() {
		try {
			String whsql = "";
			
			if(searchDto.getSbankname()!=null && !searchDto.getSbankname().equals("")){
				String sbankname = new String();
				sbankname = searchDto.getSbankname();
				whsql = " and S_BANKNAME like '%"+sbankname+"%'";
				searchDto.setSbankname("");
			}
			resultlist = commonDataAccessService.findRsByDtoWithWhere(searchDto, whsql);//findRsByDto(searchDto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
	}

	/**
	 * Direction: ��񵥻� ename: singleclick ���÷���: viewers: * messages:
	 */
	public String singleclick(Object o) {
		detailDto = (TsConvertbanknameDto) o;
		return super.singleclick(o);
	}

	/**
	 * Direction: ���˫�� ename: doubleclick ���÷���: viewers: ��Ϣ�޸� messages:
	 */
	public String doubleclick(Object o) {
		this.detailDto = (TsConvertbanknameDto) o;
		if (null != detailDto && null != detailDto.getSorgcode()) {
			oldDto = new TsConvertbanknameDto();
			oldDto = (TsConvertbanknameDto) detailDto.clone();
			return super.doubleclick(o);
		} else {
			MessageDialog.openMessageDialog(null, "��ѡ����Ҫ�޸ĵ���Ϣ��");
			return null;
		}
	}

	/**
	 * Direction: ��ת¼����� ename: goinputview ���÷���: viewers: ��Ϣ¼�� messages:
	 */
	public String goinputview(Object o) {
		detailDto = new TsConvertbanknameDto();
		detailDto.setSorgcode(loginfo.getSorgcode());
		return super.goinputview(o);
	}

	/**
	 * Direction: ��ת�޸Ľ��� ename: gomodview ���÷���: viewers: ��Ϣ�޸� messages:
	 */
	public String gomodview(Object o) {
		
		if (null == detailDto || null == detailDto.getSorgcode()) {
			MessageDialog.openMessageDialog(null, "��ѡ����Ҫ�޸ĵ���Ϣ��");
			return null;
		}
		oldDto = new TsConvertbanknameDto();
		oldDto = (TsConvertbanknameDto) detailDto.clone();
		return super.gomodview(o);
	}

	/**
	 * Direction: ������ҳ�� ename: gomainview ���÷���: viewers: �����������ղ���ά�� messages:
	 */
	public String gomainview(Object o) {
		try {
			detailDto = new TsConvertbanknameDto();
			resultlist.clear();
			resultlist = commonDataAccessService.findRsByDto(searchDto);
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.gomainview(o);
	}

	/**
	 * Direction: ɾ������ ename: del ���÷���: viewers: * messages:
	 */
	public String del(Object o) {
		if (null == detailDto || null == detailDto.getSorgcode()) {
			MessageDialog.openMessageDialog(null, "��ѡ����Ҫɾ������Ϣ��");
			return null;
		}
		if (org.eclipse.jface.dialogs.MessageDialog.openConfirm(null,
				"��ʾ!", "�Ƿ�ȷ��Ҫɾ��ѡ�еļ�¼��")) {
			try {
				tsconvertbanknameService.delInfo(detailDto);
				MessageDialog.openMessageDialog(null, "�����ɹ���");
				gomainview(o);
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
				return null;
			}
		}else{
			detailDto = new TsConvertbanknameDto();
		}
		return super.del(o);
	}

	/**
	 * Direction: ���� ename: inputsave ���÷���: viewers: �����������ղ���ά�� messages:
	 */
	public String inputsave(Object o) {
		try {
			TsConvertbanknameDto tmpDto = new TsConvertbanknameDto();
			tmpDto.setSorgcode(detailDto.getSorgcode());
			tmpDto.setSbankname(detailDto.getSbankname());
			List<TsConvertbanknameDto> list = commonDataAccessService.findRsByDto(tmpDto);
			if(null != list && list.size() > 0){
				MessageDialog.openMessageDialog(null, "������������:" + detailDto.getSbankname() + "�Ѿ����ڣ����֤��");
				return null;
			}
			tsconvertbanknameService.addInfo(detailDto);
			MessageDialog.openMessageDialog(null, "�����ɹ���");
			gomainview(o);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.inputsave(o);
	}

	/**
	 * Direction: �޸ı��� ename: modsave ���÷���: viewers: �����������ղ���ά�� messages:
	 */
	public String modsave(Object o) {
		try {
			tsconvertbanknameService.modInfo(oldDto,detailDto);
			MessageDialog.openMessageDialog(null, "�����ɹ���");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.modsave(o);
	}
	/**
	 * Direction: ��ѯ
	 * ename: search
	 * ���÷���: 
	 * viewers: �����������ղ���ά��
	 * messages: 
	 */
    public String search(Object o){
    	try {
			String whsql = "";
			
			if(searchDto.getSbankname()!=null && !searchDto.getSbankname().equals("")){
				String sbankname = new String();
				sbankname = searchDto.getSbankname();
				whsql = " and S_BANKNAME like '%"+sbankname+"%'";
				searchDto.setSbankname("");
			}
			resultlist = commonDataAccessService.findRsByDtoWithWhere(searchDto, whsql);//findRsByDto(searchDto);
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
        return super.search(o);
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

}
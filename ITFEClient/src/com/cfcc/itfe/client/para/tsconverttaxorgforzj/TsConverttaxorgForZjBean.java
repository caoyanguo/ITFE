package com.cfcc.itfe.client.para.tsconverttaxorgforzj;

import java.util.*;

import org.apache.commons.logging.*;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;

/**
 * ���ܣ��㽭���ջ��ض��ձ�
 * @author hejianrong
 * @time   14-07-29 10:44:15
 * ��ϵͳ: Para
 * ģ��:TsConverttaxorgForZj
 * ���:TsConverttaxorgForZj
 */
public class TsConverttaxorgForZjBean extends AbstractTsConverttaxorgForZjBean implements IPageDataProvider {

	private static Log log = LogFactory.getLog(TsConverttaxorgForZjBean.class);
	private List list1;
	private List list2;
	private ITFELoginInfo loginfo = null;
	private TsConverttaxorgDto initdto = new TsConverttaxorgDto();

	public TsConverttaxorgForZjBean() {
		super();
		dto = new TsConverttaxorgDto();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		dto.setSorgcode(loginfo.getSorgcode());
		pagingcontext = new PagingContext(this);
		init();
	}

	/**
	 * Direction: ��ת¼����� ename: goInput ���÷���: viewers: ¼����� messages:
	 */
	public String goInput(Object o) {
		dto = new TsConverttaxorgDto();
		dto.setSorgcode(loginfo.getSorgcode());
		return super.goInput(o);
	}

	/**
	 * Direction: ¼�뱣�� ename: inputSave ���÷���: viewers: * messages:
	 * 
	 * @param stbstaxorgcode
	 * @param stbstaxorgcode
	 * @param stcbstaxorgcode
	 * @param stcbstaxorgcode
	 */
	public String inputSave(Object o) {

		if (datacheck(dto, "input")) {
			return null;
		}
		try {
			tsConverttaxorgForZjService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsConverttaxorgDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ���ص�ά������ ename: backMaintenance ���÷���: viewers: ά������ messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsConverttaxorgDto();
		dto.setSorgcode(loginfo.getSorgcode());
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsConverttaxorgDto) o;
		initdto =  (TsConverttaxorgDto)dto.clone();
		return super.singleSelect(o);
	}

	/**
	 * Direction: ɾ�� ename: delete ���÷���: viewers: * messages:
	 */
	public String delete(Object o) {
		if (dto.getStbstaxorgcode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		try {
			tsConverttaxorgForZjService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsConverttaxorgDto();
		init();
		return super.backMaintenance(o);

	}

	/**
	 * Direction: ���޸Ľ��� ename: goModify ���÷���: viewers: �޸Ľ��� messages:
	 */
	public String goModify(Object o) {
		if (dto == null || dto.getStbstaxorgcode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		return super.goModify(o);
	}

	/**
	 * Direction: �޸ı��� ename: modifySave ���÷���: viewers: * messages:
	 * 
	 * @param stbstaxorgcode
	 * @param stbstaxorgcode
	 * @param stcbstaxorgcode
	 * @param stcbstaxorgcode
	 */
	public String modifySave(Object o) {
		if (datacheck(dto, "modify")) {
			return null;
		}
		try {
			tsConverttaxorgForZjService.modInfo(initdto,dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsConverttaxorgDto();
		return super.backMaintenance(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest pageRequest) {
		TsConverttaxorgDto tmpdto = new TsConverttaxorgDto();
		tmpdto.setSorgcode(loginfo.getSorgcode());
		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(tmpdto,
					pageRequest, "1=1");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	private void init() {
		TsOrganDto orgdto = new TsOrganDto();
		orgdto.setSorgcode(loginfo.getSorgcode());

		try {
			list1 = commonDataAccessService.findRsByDto(orgdto);

		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}

		TsTreasuryDto tredto = new TsTreasuryDto();
		tredto.setSorgcode(loginfo.getSorgcode());
		try {
			list2 = commonDataAccessService.findRsByDto(tredto);

		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	private boolean datacheck(TsConverttaxorgDto dto, String operType) {
		if (null == dto.getSorgcode() || dto.getSorgcode().trim().length() == 0) {
			MessageDialog.openMessageDialog(null, "����������벻��Ϊ�գ�");
			return true;
		} else if (null == dto.getStrecode() || dto.getStrecode().trim().length() == 0) {
			MessageDialog.openMessageDialog(null, "������벻��Ϊ�գ�");
			return true;
		} else if (null == dto.getStcbstaxorgcode() || dto.getStcbstaxorgcode().trim().length() == 0) {
			MessageDialog.openMessageDialog(null, "TCBS���ջ��ش��벻��Ϊ�գ�");
			return true;
		} else if (null == dto.getImodicount()
				|| !(dto.getImodicount() == 1 || dto.getImodicount() == 2 || dto.getImodicount() == 3 || dto.getImodicount() == 4)) {
			MessageDialog.openMessageDialog(null, "���ջ������ʴ���");
			return true;
		}
		
		if (null == dto.getSnationtaxorgcode() || dto.getSnationtaxorgcode().trim().length() == 0) {
			dto.setSnationtaxorgcode("N");
//			MessageDialog.openMessageDialog(null, "��˰���ջ��ش��벻��Ϊ�գ�");
//			return true;
		} 
		
		if (null == dto.getSareataxorgcode() || dto.getSareataxorgcode().trim().length() == 0) {
			dto.setSareataxorgcode("N");
//			MessageDialog.openMessageDialog(null, "��˰���ջ��ش��벻��Ϊ�գ�");
//			return true;
		} 

		if (null == dto.getStbstaxorgcode() || dto.getStbstaxorgcode().trim().length() == 0) {
			dto.setStbstaxorgcode("N");
//			Mess8ageDialog.openMessageDialog(null, "TBS���ջ��ش��벻��Ϊ�գ�");
//			return true;
		} 
		
		TsConverttaxorgDto tempdto = new TsConverttaxorgDto();
		tempdto.setSorgcode(dto.getSorgcode());
		tempdto.setStcbstaxorgcode(dto.getStcbstaxorgcode());
		tempdto.setStrecode(dto.getStrecode());

		List list = null;
		try {
			list = commonDataAccessService.findRsByDto(tempdto);
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return true;
		}
		if (operType.equals("input")) {
			if (null != list && list.size() > 0) {
				MessageDialog.openMessageDialog(null, "�����������+�������+TCBS���ջ��ش����ظ���");
				return true;
			} else {
				return false;
			}
		} else {
			if (null != list && list.size() > 1) {
				MessageDialog.openMessageDialog(null, "�����������+�������+TCBS���ջ��ش����ظ���");
				return true;
			} else {
				return false;
			}
		}
	}

	public List getList1() {
		return list1;
	}

	public void setList1(List list1) {
		this.list1 = list1;
	}

	public List getList2() {
		return list2;
	}

	public void setList2(List list2) {
		this.list2 = list2;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}
}
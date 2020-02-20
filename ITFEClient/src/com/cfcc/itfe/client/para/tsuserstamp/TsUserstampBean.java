package com.cfcc.itfe.client.para.tsuserstamp;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.stamp.BaseStampHandler;
import com.cfcc.itfe.client.common.stamp.koal.KoalStampHandler;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsStamptypeDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TsUserstampDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author caoyg
 * @time 09-10-20 08:42:02 ��ϵͳ: Para ģ��:TsUserstamp ���:TsUserstamp
 */
public class TsUserstampBean extends AbstractTsUserstampBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsUserstampBean.class);
	private String sorgcode;
	private String susercode;
	private String sstamptypecode;
	private String sisvalid;
	private List list1;
	private List list2;
	private List list3;
	public String getCenterorg() {
		return centerorg;
	}

	public void setCenterorg(String centerorg) {
		this.centerorg = centerorg;
	}

	ITFELoginInfo loginfo;
	private String centerorg;

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

	public List getList3() {
		return list3;
	}

	public void setList3(List list3) {
		this.list3 = list3;
	}

	public String getSorgcode() {
		return sorgcode;
	}

	public void setSorgcode(String sorgcode) {
		this.sorgcode = sorgcode;
		TsUsersDto usersdto = new TsUsersDto();
		usersdto.setSorgcode(sorgcode);
		try {
			list3 = commonDataAccessService.findRsByDto(usersdto);
		
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
	}

	public String getSusercode() {
		return susercode;
	}

	public void setSusercode(String susercode) {
		this.susercode = susercode;
		
	}

	public String getSstamptypecode() {
		return sstamptypecode;
	}

	public void setSstamptypecode(String sstamptypecode) {
		this.sstamptypecode = sstamptypecode;
	}

	public String getSisvalid() {
		return sisvalid;
	}

	public void setSisvalid(String sisvalid) {
		this.sisvalid = sisvalid;
	}

	public TsUserstampBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		dto = new TsUserstampDto();
		pagingcontext = new PagingContext(this);
		init();
	}

	/**
	 * Direction: ��ת¼����� ename: goInput ���÷���: viewers: ¼����� messages:
	 */
	public String goInput(Object o) {
		dto = new TsUserstampDto();
		dto.setSisvalid("1");
		setSisvalid(dto.getSisvalid());
		return super.goInput(o);
	}

	/**
	 * Direction: ¼�뱣�� ename: inputSave ���÷���: viewers: * messages:
	 */
	public String inputSave(Object o) {
		if (!datacheck()) {
			return null;
		}
		dto.setSorgcode(getSorgcode());
		dto.setSusercode(getSusercode());
		dto.setSstamptypecode(getSstamptypecode());
		dto.setSisvalid(getSisvalid());
		try {
			tsUserstampService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);

			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsUserstampDto();
		setSorgcode("");
		setSisvalid("");
		setSstamptypecode("");
		setSusercode("");
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ���ص�ά������ ename: backMaintenance ���÷���: viewers: ά������ messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsUserstampDto();
		setSorgcode("");
		setSisvalid("");
		setSstamptypecode("");
		setSusercode("");
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsUserstampDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: ɾ�� ename: delete ���÷���: viewers: * messages:
	 */
	public String delete(Object o) {
		if (dto.getSusercode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		//��ʾ�û�ȷ��ɾ��
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�Ҫɾ��ѡ���û�" + dto.getSusercode() + "��" + dto.getSstamptypecode() + "���͵���ӡ����")){
			return "";
		}
		try {
			tsUserstampService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsUserstampDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ���޸Ľ��� ename: goModify ���÷���: viewers: �޸Ľ��� messages:
	 */
	public String goModify(Object o) {
		if (dto == null || dto.getSisvalid() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		setSisvalid(dto.getSisvalid());
		setSorgcode(dto.getSorgcode());
		setSstamptypecode(dto.getSstamptypecode());
		setSusercode(dto.getSusercode());
		return super.goModify(o);
	}

	/**
	 * Direction: �޸ı��� ename: modifySave ���÷���: viewers: * messages:
	 */
	public String modifySave(Object o) {
		if (!datacheck()) {
			return null;
		}
		dto.setSorgcode(getSorgcode());
		dto.setSusercode(getSusercode());
		dto.setSstamptypecode(getSstamptypecode());
		dto.setSisvalid(getSisvalid());
		try {
			tsUserstampService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsUserstampDto();
		return super.backMaintenance(o);
	}

	/**
	 * ��ȡUsbKey��ָ����ǩ����ӡ��id�ķ���
	 */
	public String getEsealId(Object o) {
		if ((getSstamptypecode() == null) || (getSstamptypecode().length() == 0)){
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ��ȡ�ĵ���ǩ�µ����͡�");
			return "";
		}
		BaseStampHandler stamp = new KoalStampHandler(editor);
		String id = stamp.getEsealIdFromKey(getSstamptypecode());
		if (id == ""){
			MessageDialog.openMessageDialog(null, stamp.getLastError());
			return "";
		}
		dto.setSstampid(id);
		this.editor.fireModelChanged();
		return super.getEsealId(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest pageRequest) {
		if (!centerorg.equals(loginfo.getSorgcode())) {
			dto.setSorgcode(loginfo.getSorgcode());
		}else
		{
			dto = new TsUserstampDto();
		}
		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(dto,
					pageRequest, "1=1");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	private void init() {
	
		TsOrganDto orgdto = new TsOrganDto();
		//���Ĺ���Ա��ѯȫ��������ֻ�������Լ�
		if (!centerorg.equals(loginfo.getSorgcode())) {
			orgdto.setSorgcode(loginfo.getSorgcode());
		}
		try {
			list1 = commonDataAccessService.findRsByDto(orgdto);

		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		TsStamptypeDto sttdto = new TsStamptypeDto();
		try {
			list2 = commonDataAccessService.findRsByDto(sttdto);

		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
//		TsUsersDto usersdto = new TsUsersDto();
//		try {
//			list3 = commonDataAccessService.findRsByDto(usersdto);
//
//		} catch (ITFEBizException e) {
//			log.error(e);
//			MessageDialog.openErrorDialog(null, e);
//			return;
//		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}
	
	private boolean  datacheck(){
		if (null==sorgcode ||"".equals(sorgcode)) {
			MessageDialog.openMessageDialog(null, "������������Ϊ�գ�");
			return false;
		}
		if (null==sstamptypecode ||"".equals(sstamptypecode)) {
			MessageDialog.openMessageDialog(null, "ǩ�����Ͳ���Ϊ�գ�");
			return false;
		}
		if (null==sisvalid ||"".equals(sisvalid.trim())) {
			MessageDialog.openMessageDialog(null, "�Ƿ���Ч���ܿգ�");
			return false;
		}
		if (null==susercode ||"".equals(susercode.trim())) {
			MessageDialog.openMessageDialog(null, "����Ա���Ʋ��ܿգ�");
			return false;
		}
		return true;
		
	}
}
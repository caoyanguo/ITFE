package com.cfcc.itfe.client.para.interestparam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfInterestParamDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsJxAcctinfoDto;
import com.cfcc.itfe.service.ITFELoginInfo;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 14-09-24 14:58:34 ��ϵͳ: Para ģ��:interestParam ���:InterestParam
 */
public class InterestParamBean extends AbstractInterestParamBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(InterestParamBean.class);
	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();
	private TfInterestParamDto searchDto;
	private TsJxAcctinfoDto searchJXDto;
	private TsJxAcctinfoDto tsJxAcctinfoDto;
	private String trecode;

	public InterestParamBean() {
		super();
		dto = new TfInterestParamDto();
		JXDto = new TsJxAcctinfoDto();
		searchDto = new TfInterestParamDto();
		searchDto.setSorgcode(loginfo.getSorgcode());
		tsJxAcctinfoDto = new TsJxAcctinfoDto();
		searchJXDto = new TsJxAcctinfoDto();
		searchJXDto.setSorgcode(loginfo.getSorgcode());
		pagingcontext = new PagingContext(null);
		pagingcontextJX = new PagingContext(null);
		trecode = "";
		msg = "�Թ���Ϊ��λ�������±���ά��һ���б�Ϊ�յ�Ĭ�����ʣ����ĳ��û����ȷά����������Ĭ������Ϊ����";
		initJX();
		init();

	}

	private void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	private void initJX() {
		PageRequest pageRequestJX = new PageRequest();
		PageResponse pageResponseJX = retrieveJX(pageRequestJX);
		pagingcontextJX.setPage(pageResponseJX);
	}

	/**
	 * Direction: ��ת¼����� ename: goInput ���÷���: viewers: ¼����� messages:
	 */
	public String goInput(Object o) {
		dto = new TfInterestParamDto();
		return super.goInput(o);
	}

	/**
	 * Direction: ��ת��Ϣ�˻�¼����� ename: goJXInput ���÷���: viewers: ��Ϣ�˻�¼����� messages:
	 */
	public String goJXInput(Object o) {
		// TODO Auto-generated method stub
		JXDto = new TsJxAcctinfoDto();
		JXDto.setSorgcode(loginfo.getSorgcode());
		setBankList();
		return super.goJXInput(o);
	}

	/*
	 * У��
	 */
	public boolean checkInfo() {
		if (dto.getSquarter() == null || dto.getSquarter().equals("")) {
			MessageDialog.openMessageDialog(null, "����ѡ�񼾶ȣ�");
			return false;
		}
		if (dto.getSstartdate() == null || dto.getSstartdate().equals("")) {
			MessageDialog.openMessageDialog(null, "���ȿ�ʼ���ڲ���Ϊ�գ�");
			return false;
		} else {
			try {
				if (TimeFacade.parseDate(dto.getSstartdate(), "yyyyMMdd") == null) {
					MessageDialog.openMessageDialog(null, "���ȿ�ʼ���ڸ�ʽ����\n");
					return false;
				}
			} catch (Exception e) {
				MessageDialog.openMessageDialog(null, "���ȿ�ʼ���ڸ�ʽ����\n");
				return false;
			}
		}
		if (dto.getNinterestrates() == null) {
			MessageDialog.openMessageDialog(null, "����ֵ����Ϊ�գ�");
			return false;
		} else {
			try {
				if (dto.getNinterestrates().compareTo(new BigDecimal(0)) != 1) {
					MessageDialog.openMessageDialog(null, "����ֵ����Ϊ��ֵ����\n");
					return false;
				}
			} catch (Exception e) {
				MessageDialog.openMessageDialog(null, "����ֵ��������");
				return false;
			}
		}
		if (dto.getSenddate() == null || dto.getSenddate().equals("")) {
			MessageDialog.openMessageDialog(null, "���Ƚ������ڲ���Ϊ�գ�");
			return false;
		} else {
			try {
				if (TimeFacade.parseDate(dto.getSenddate(), "yyyyMMdd") == null) {
					MessageDialog.openMessageDialog(null, "���Ƚ�����ʽ����\n");
					return false;
				}
			} catch (Exception e) {
				MessageDialog.openMessageDialog(null, "���Ƚ�����ʽ����\n");
				return false;
			}
		}
		if (TimeFacade.parseDate(dto.getSenddate()).before(
				TimeFacade.parseDate(dto.getSstartdate()))) {
			MessageDialog.openMessageDialog(null, "���ȿ�ʼ���ڲ��ܳ������Ƚ�������\n");
			return false;
		}
		// ��ѯ���õ����������Ƿ��Ѿ�����
		dto.setSext1(dto.getSenddate().substring(0, 4));// ���ֶδ������
		String ls_SQL = "";
		if (dto.getIvousrlno() != null && !dto.getIvousrlno().equals("")) {
			ls_SQL = "AND I_VOUSRLNO <> " + dto.getIvousrlno() + "";
		}
		TfInterestParamDto paramDto = new TfInterestParamDto();
		paramDto.setSorgcode(dto.getSorgcode());
		paramDto.setSext3(getTrecode());
		paramDto.setSquarter(dto.getSquarter());
		if (StringUtils.isNotBlank(dto.getSext2())) {
			paramDto.setSext2(dto.getSext2());
		} else {
			ls_SQL = ls_SQL + "  AND S_EXT2 IS NULL ";
		}
		try {
			List<TfInterestParamDto> paramDtoList = commonDataAccessService
					.findRsByDtoWithWhere(paramDto, ls_SQL
							+ " AND (( S_STARTDATE >= '" + dto.getSstartdate()
							+ "' AND S_ENDDATE >= '" + dto.getSenddate()
							+ "' AND S_STARTDATE <= '" + dto.getSenddate()
							+ "') OR (S_STARTDATE >= '" + dto.getSstartdate()
							+ "' AND S_ENDDATE <= '" + dto.getSenddate()
							+ "') OR ( S_STARTDATE <= '" + dto.getSstartdate()
							+ "' AND S_ENDDATE >= '" + dto.getSenddate()
							+ "') OR ( S_STARTDATE <= '" + dto.getSstartdate()
							+ "' AND S_ENDDATE <= '" + dto.getSenddate()
							+ "' AND S_ENDDATE >= '" + dto.getSstartdate()
							+ "'))");
			if (paramDtoList != null && paramDtoList.size() > 0) {
				MessageDialog.openMessageDialog(null, "�����õ����������Ѿ�����\n");
				return false;
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return true;
	}

	/**
	 * ��ϢУ��
	 */

	public boolean checkJXInfo() {
		if (JXDto.getStrecode() == null || JXDto.getStrecode().equals("")) {
			MessageDialog.openMessageDialog(null, "��ѡ�������룡");
			return false;
		}
		if (JXDto.getSopnbankcode() == null
				|| JXDto.getSopnbankcode().equals("")) {
			MessageDialog.openMessageDialog(null, "�����в���Ϊ�գ�");
			return false;
		}
		if (JXDto.getSpayeeacct() == null || JXDto.getSpayeeacct().equals("")) {
			MessageDialog.openMessageDialog(null, "��Ϣ�˻�����Ϊ�գ�");
			return false;
		}
		if (JXDto.getSbiztype() == null || JXDto.getSbiztype().equals("")) {
			MessageDialog.openMessageDialog(null, "��ѡ��ҵ������");
			return false;
		}
		TsJxAcctinfoDto searchJxDto = new TsJxAcctinfoDto();
		searchJxDto.setSpayeeacct(JXDto.getSpayeeacct());
		try {
			if (commonDataAccessService.findRsByDto(searchJxDto).size() != 0) {
				MessageDialog.openMessageDialog(null, "�ü�Ϣ�˻��Ѵ��ڡ�");
				return false;
			}
		} catch (ITFEBizException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Direction: ¼�뱣�� ename: inputSave ���÷���: viewers: * messages:
	 */
	public String inputSave(Object o) {
		dto.setSorgcode(loginfo.getSorgcode());
		try {
			dto.setSext3(trecode);
			setQuarterDate(dto);
			if (checkInfo()) {
				if (null == interestParamService.addInfo(dto)) {
					MessageDialog.openMessageDialog(null, "����ά��������룺"
							+ dto.getSext3() + "��,�ü���Ĭ�ϵ����ʲ�����");
					return null;
				}
				dto = new TfInterestParamDto();
			} else {
				return "";
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TfInterestParamDto();
		init();
		return super.goInput(o);
	}

	/**
	 * Direction: ��Ϣ¼�뱣�� ename: jxInputSave ���÷���: viewers: * messages:
	 */
	public String jxInputSave(Object o) {
		if (checkJXInfo()) {
			try {
				interestParamService.addJXInfo(JXDto);
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
				return super.jxInputSave(o);
			}
		} else {
			return "";
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		JXDto = new TsJxAcctinfoDto();
		initJX();
		return super.backJXMaintenance(o);
	}

	/**
	 * Direction: ���ص�ά������ ename: backMaintenance ���÷���: viewers: ά������ messages:
	 */
	public String backMaintenance(Object o) {
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ���ؼ�Ϣ�˻�ά������ ename: backJXMaintenance ���÷���: viewers: ��Ϣ�˻�ά������
	 * messages:
	 */
	public String backJXMaintenance(Object o) {
		JXDto = new TsJxAcctinfoDto();
		initJX();
		return super.backJXMaintenance(o);
	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TfInterestParamDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: ��ѡ ename: singleSelectJX ���÷���: viewers: * messages:
	 */
	public String jxSingleSelect(Object o) {
		JXDto = (TsJxAcctinfoDto) o;
		tsJxAcctinfoDto.setSorgcode(JXDto.getSorgcode());
		tsJxAcctinfoDto.setStrecode(JXDto.getStrecode());
		tsJxAcctinfoDto.setSopnbankcode(JXDto.getSopnbankcode());
		tsJxAcctinfoDto.setSpayeeacct(JXDto.getSpayeeacct());
		tsJxAcctinfoDto.setSpayeename(JXDto.getSpayeename());
		tsJxAcctinfoDto.setSbiztype(JXDto.getSbiztype());
		return super.jxSingleSelect(o);
	}

	/**
	 * Direction: ɾ�� ename: delete ���÷���: viewers: * messages:
	 */
	public String delete(Object o) {
		if (dto.getSorgcode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		if (null == dto.getSext2()) {
			MessageDialog.openMessageDialog(null, "Ĭ�����ʲ���������ɾ����");
			return "";
		}
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ��ɾ��?")) {
			if (dto.getSorgcode() == null) {
				MessageDialog.openMessageDialog(null,
						StateConstant.DELETESELECT);
				return "";
			}
			try {
				interestParamService.delInfo(dto);
			} catch (Throwable e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
				return super.delete(o);
			}
			MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
			dto = new TfInterestParamDto();
			init();
			editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		}
		return super.delete(o);
	}

	@Override
	public String jxDelete(Object o) {
		if (JXDto.getSorgcode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		if (org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ��ɾ��?")) {
			if (JXDto.getSorgcode() == null) {
				MessageDialog.openMessageDialog(null,
						StateConstant.DELETESELECT);
				return "";
			}
			try {
				interestParamService.delJXInfo(JXDto);
			} catch (Throwable e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
				return super.delete(o);
			}
			MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
			JXDto = new TsJxAcctinfoDto();
			initJX();
			editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		}
		return super.jxDelete(o);
	}

	/**
	 * Direction: ���޸Ľ��� ename: goModify ���÷���: viewers: �޸Ľ��� messages:
	 */
	public String goModify(Object o) {
		if (dto.getSorgcode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		setTrecode(dto.getSext3());
		return super.goModify(o);
	}

	/**
	 * Direction: ����Ϣ�˻��޸Ľ��� ename: goJXModify ���÷���: viewers: ��Ϣ�˻��޸Ľ��� messages:
	 */
	public String goJXModify(Object o) {
		if (JXDto.getSorgcode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}

		setBankList();
		return super.goJXModify(o);
	}

	/**
	 * Direction: �޸ı��� ename: modifySave ���÷���: viewers: * messages:
	 */
	public String modifySave(Object o) {
		try {
			dto.setSext3(getTrecode());
			setQuarterDate(dto);
			if (checkInfo()) {
				interestParamService.modInfo(dto);
			} else {
				return "";
			}
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ��Ϣ�˻��޸ı��� ename: jxModifySave ���÷���: viewers: ��Ϣ�˻�ά������ messages:
	 */
	public String jxModifySave(Object o) {
		try {
			interestParamService.modJXInfo(tsJxAcctinfoDto, JXDto);
			MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.jxModifySave(o);
		}
		return super.backJXMaintenance(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest arg0) {
		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(
					searchDto, arg0, " 1=1 ");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(arg0);
	}

	public PageResponse retrieveJX(PageRequest arg0) {
		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(
					searchJXDto, arg0, " 1=1 ");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(arg0);
	}

	public String getTrecode() {
		return trecode;
	}

	public void setTrecode(String trecode) {
		this.trecode = trecode;
		try {
			TsConvertbanktypeDto searchBankType = new TsConvertbanktypeDto();
			searchBankType.setSorgcode(loginfo.getSorgcode());
			searchBankType.setStrecode(this.trecode);
			List resultList;
			resultList = commonDataAccessService.findRsByDto(searchBankType);
			bankTypeList = new ArrayList();
			if (null == resultList || resultList.size() == 0) {
				return;
			}
			Mapper tmpMapper = null;
			for (TsConvertbanktypeDto tmpDto : (List<TsConvertbanktypeDto>) resultList) {
				tmpMapper = new Mapper();
				tmpMapper.setUnderlyValue(tmpDto.getSbanktype());
				tmpMapper.setDisplayValue(tmpDto.getSbankname());
				bankTypeList.add(tmpMapper);
			}
		} catch (ITFEBizException e) {
			log.error("��ʼ����Ϣʧ�ܣ�", e);
			MessageDialog.openErrorDialog(null, e);
		} finally {
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		}
	}

	public void setBankList() {
		try {
			TsConvertbanktypeDto searchBankType = new TsConvertbanktypeDto();
			searchBankType.setSorgcode(loginfo.getSorgcode());
			List resultList;
			resultList = commonDataAccessService.findRsByDto(searchBankType);
			bankList = new ArrayList();
			if (null == resultList || resultList.size() == 0) {
				return;
			}
			Mapper tmpMapper = null;
			for (TsConvertbanktypeDto tmpDto : (List<TsConvertbanktypeDto>) resultList) {
				tmpMapper = new Mapper();
				tmpMapper.setUnderlyValue(tmpDto.getSbankcode());
				tmpMapper.setDisplayValue(tmpDto.getSbankname());
				bankList.add(tmpMapper);
			}
		} catch (ITFEBizException e) {
			log.error("��ʼ����Ϣʧ�ܣ�", e);
			MessageDialog.openErrorDialog(null, e);
		} finally {
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		}
	}

	private void setQuarterDate(TfInterestParamDto dto) {

		// ��ȡ����������
		String sysdbDate;
		try {
			sysdbDate = commonDataAccessService.getSysDBDate();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		sysdbDate = sysdbDate.substring(0, 4);
		if (null == dto || StringUtils.isBlank(dto.getSquarter())) {
			return;
		}
		if ("1".equals(dto.getSquarter().trim())) {
			dto.setSstartdate(String.valueOf(Integer.valueOf(sysdbDate) - 1)
					+ "1221");
			dto.setSenddate(sysdbDate + "0320");
		} else if ("2".equals(dto.getSquarter().trim())) {
			dto.setSstartdate(sysdbDate + "0321");
			dto.setSenddate(sysdbDate + "0620");
		} else if ("3".equals(dto.getSquarter().trim())) {
			dto.setSstartdate(sysdbDate + "0621");
			dto.setSenddate(sysdbDate + "0920");
		} else if ("4".equals(dto.getSquarter().trim())) {
			dto.setSstartdate(sysdbDate + "0921");
			dto.setSenddate(sysdbDate + "1220");
		}
	}

}
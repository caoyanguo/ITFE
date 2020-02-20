package com.cfcc.itfe.client.para.tsbudgetsubject;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author wangtuo
 * @time 10-04-08 12:58:20 ��ϵͳ: Para ģ��:TsBudgetsubject ���:TsBudgetsubject
 */
public class TsBudgetsubjectBean extends AbstractTsBudgetsubjectBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsBudgetsubjectBean.class);
	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();
	private PagingContext pagingcontext = new PagingContext(this);

	/**
	 * TsBudgetsubjectDto
	 */
	private TsBudgetsubjectDto dto = null;
	// ��ѯdto
	private TsBudgetsubjectDto querydto = null;

	public TsBudgetsubjectBean() {
		super();
		querydto = new TsBudgetsubjectDto();
		dto = new TsBudgetsubjectDto();
	}

	/**
	 * Direction: ��ѯ ename: queryBudget ���÷���: viewers: ά����ѯ��� messages:
	 */
	public String queryBudget(Object o) {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		return super.queryBudget(o);
	}

	/**
	 * Direction: ��ת¼����� ename: goInput ���÷���: viewers: ¼����� messages:
	 */
	public String goInput(Object o) {
		dto = new TsBudgetsubjectDto();
		dto.setSorgcode(loginfo.getSorgcode());
		return super.goInput(o);
	}

	/**
	 * Direction: ¼�뱣�� ename: inputSave ���÷���: viewers: * messages:
	 */
	public String inputSave(Object o) {
		try {
//			dto.getSorgcode();
			dto.setSdrawbacktype("0");
			tsBudgetsubjectService.addInfo(dto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null,e.getMessage());
			return super.inputSave(o);
		}
		dto = new TsBudgetsubjectDto();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ���ص�ά������ ename: backMaintenance ���÷���: viewers: ά������ messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsBudgetsubjectDto();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsBudgetsubjectDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: ɾ�� ename: delete ���÷���: viewers: * messages:
	 */
	public String delete(Object o) {
		if (null == dto.getSsubjectcode() || "".equals(dto.getSsubjectcode())) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		// ��ʾ�û�ȷ��ɾ��
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�Ҫɾ��ѡ��Ŀ�Ŀ"
				+ dto.getSsubjectcode())) {
			return "";
		}
		try {
			tsBudgetsubjectService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		queryBudget(null);
		editor.fireModelChanged();
		return super.delete(o);
	}

	/**
	 * Direction: ���޸Ľ��� ename: goModify ���÷���: viewers: �޸Ľ��� messages:
	 */
	public String goModify(Object o) {
		if (null == dto.getSsubjectcode() || "".equals(dto.getSsubjectcode())) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		return super.goModify(o);
	}

	/**
	 * Direction: �޸ı��� ename: modifySave ���÷���: viewers: * messages:
	 */
	public String modifySave(Object o) {
		try {
			tsBudgetsubjectService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsBudgetsubjectDto();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ���� ename: goBack ���÷���: viewers: ��Ϣ��ѯ messages:
	 */
	public String goBack(Object o) {
		return super.goBack(o);
	}
	
	

	@Override
	public String expfile(Object o) {
		// ѡ�񱣴�·��
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
			return "";
		}
		String fileName = filePath+ File.separator+ loginfo.getSorgcode() + "_Ԥ���Ŀ���� .csv";
		try {
			List<TsBudgetsubjectDto> list = commonDataAccessService.findRsByDto(querydto);
			if(null == list || list.size() == 0 ){
				MessageDialog.openMessageDialog(null, "û����Ҫ���������ݣ�");
				return null;
			}
			StringBuffer result = new StringBuffer();
			result.append("�����������_CHARACTER_ NOT NULL,Ԥ���Ŀ����_VARCHAR_NOT NULL," +
					"Ԥ���Ŀ����_VARCHAR_NOT NULL,Ԥ���Ŀ����_CHARACTER_NOT NULL,Ԥ���Ŀ�������_CHARACTER_NOT NULL," +
					"Ԥ������_CHARACTER_NOT NULL,Ԥ���Ŀ���ʹ���_CHARACTER_NOT NULL,Ԥ���Ŀ����_VARCHAR,¼���־_CHARACTER_NOT NULL," +
					"Ԥ���Ŀ����_CHARACTER_NOT NULL,������־_VARCHAR_NOT NULL,��������_VARCHAR,���ջ��ش���_VARCHAR," +
					"ͳһ����_VARCHAR,ͳ�ƴ���_VARCHAR_NOT NULL,��Ӧ�����Ŀ����_VARCHAR,��Ӧʡ��Ŀ����_VARCHAR," +
					"��Ӧ�п�Ŀ����_VARCHAR,��Ӧ�ؿ�Ŀ����_VARCHAR\n");
			for(TsBudgetsubjectDto tmp : list ){
				result.append(tmp.getSorgcode() + ",");		//�����������
				result.append(tmp.getSsubjectcode() + ",");		//��Ŀ����
				result.append(tmp.getSsubjectname() + ",");		//Ԥ���Ŀ����
				result.append(tmp.getSsubjectclass() + ",");		//Ԥ���Ŀ����
				result.append(tmp.getSclassflag() + ",");		//Ԥ���Ŀ�������
				result.append(tmp.getSbudgettype() + ",");		//Ԥ������
				result.append(tmp.getSsubjecttype() + ",");		//Ԥ���Ŀ���ʹ���
				result.append(" ,");		//Ԥ���Ŀ����
				result.append(tmp.getSwriteflag() + ",");		//¼���־
				result.append(tmp.getSsubjectattr() + ",");		//Ԥ���Ŀ����
				result.append(tmp.getSmoveflag() + ",");		//������־
				result.append("" + ",");		//��������
				result.append("" + ",");		//���ջ��ش���
				result.append("" + ",");		//ͳһ����
				result.append("" + ",");		//ͳ�ƴ���
				result.append("" + ",");		//��Ӧ�����Ŀ����
				result.append("" + ",");		//��Ӧʡ��Ŀ����
				result.append("" + ",");		//��Ӧ�п�Ŀ����
				result.append("" + "\n");		//��Ӧ�ؿ�Ŀ����
			}
			FileUtil.getInstance().writeFile(fileName, result.toString());
			MessageDialog.openMessageDialog(null, "�����ɹ���");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.expfile(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest pageRequest) {
		try {
			String where = "";
			if(loginfo.getPublicparam().contains(",Budgetsubject=public,"))
			{
				where = " and (s_orgcode='"+(loginfo.getSorgcode().substring(0,2)+"0000000003'")+" or s_orgcode = '"+(loginfo.getSorgcode().substring(0,2)+"0000000002'")+")";
			}else
			{
				querydto.setSorgcode(loginfo.getSorgcode());
			}
			TsBudgetsubjectDto sdto = (TsBudgetsubjectDto)querydto.clone();
			String subjetcode = sdto.getSsubjectcode();
			if(subjetcode!=null&&!"".equals(subjetcode))
			{
				where = where + " and S_SUBJECTCODE like '"+subjetcode+"%'";
				sdto.setSsubjectcode(null);
			}
			return commonDataAccessService.findRsByDtoWithWherePaging(sdto,
					pageRequest, "1=1" +where);

		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		TsBudgetsubjectBean.log = log;
	}

	public PagingContext getPagingcontext() {
		return pagingcontext;
	}

	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
	}

	public TsBudgetsubjectDto getDto() {
		return dto;
	}

	public void setDto(TsBudgetsubjectDto dto) {
		this.dto = dto;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public TsBudgetsubjectDto getQuerydto() {
		return querydto;
	}

	public void setQuerydto(TsBudgetsubjectDto querydto) {
		this.querydto = querydto;
	}

}
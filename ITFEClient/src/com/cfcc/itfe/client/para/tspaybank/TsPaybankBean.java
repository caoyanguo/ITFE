package com.cfcc.itfe.client.para.tspaybank;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;

import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.service.ITFELoginInfo;

/**
 * codecomment:
 * 
 * @author caoyg
 * @time 09-10-20 08:42:02 ��ϵͳ: Para ģ��:TsPaybank ���:TsPaybank
 */
public class TsPaybankBean extends AbstractTsPaybankBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsPaybankBean.class);
	private List list;
	private String sorgcode;
	ITFELoginInfo loginfo;
	private String banktype;
	

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public String getSorgcode() {
		return sorgcode;
	}

	public void setSorgcode(String sorgcode) {
		this.sorgcode = sorgcode;
	}

	public String getBanktype() {
		return banktype;
	}

	public void setBanktype(String banktype) {
		this.banktype = banktype;
	}

	public TsPaybankBean() {
		super();
		dto = new TsPaybankDto();
		searchdto=new TsPaybankDto();
		pagingcontext = new PagingContext(this);
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		banktype="";
		// init();
	}

	/**
	 * Direction: ��ת¼����� ename: goInput ���÷���: viewers: ¼����� messages:
	 */
	public String goInput(Object o) {
		if (!loginfo.getSorgcode().equals(StateConstant.ORG_CENTER_CODE)) {
			MessageDialog.openMessageDialog(null, "֧���кŲ��������Ĺ���Աͳһά��,��ǰ�û�����ִ�д˲�����");
			return null;
		}
		dto = new TsPaybankDto();
		return super.goInput(o);
	}

	/**
	 * Direction: ¼�뱣�� ename: inputSave ���÷���: viewers: * messages:
	 */
	public String inputSave(Object o) {
		dto.setSorgcode("000000000000");
		//dto.setSstate("1");
		if (datacheck(dto, "add")) {
			return null;
		}
		try {
			tsPaybankService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);

			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsPaybankDto();
		init();
		setSorgcode("");
		return super.rebackResultView(o);
	}

	/**
	 * Direction: ���ص�ά������ ename: backMaintenance ���÷���: viewers: ά������ messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsPaybankDto();
		searchdto = new TsPaybankDto();
		setBanktype("");
		setSorgcode("");
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsPaybankDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: ɾ�� ename: delete ���÷���: viewers: * messages:
	 */
	public String delete(Object o) {
		if (dto.getSorgcode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		if(!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ȷ��ɾ��?"))
    	{
    		return "";
    	}
		try {
			tsPaybankService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsPaybankDto();
		init();
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.delete(o);
	}

	/**
	 * Direction: ���޸Ľ��� ename: goModify ���÷���: viewers: �޸Ľ��� messages:
	 */
	public String goModify(Object o) {
		if (!loginfo.getSorgcode().equals(StateConstant.ORG_CENTER_CODE)) {
			MessageDialog.openMessageDialog(null, "֧���кŲ��������Ĺ���Աͳһά��,��ǰ�û�����ִ�д˲�����");
			return null;
		}
		setSorgcode(dto.getSorgcode());
		if (dto.getSorgcode() == null||dto.getSbankno()==null||dto.getSbankno().equals("")) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		return super.goModify(o);
	}

	/**
	 * Direction: �޸ı��� ename: modifySave ���÷���: viewers: * messages:
	 */
	public String modifySave(Object o) {
		dto.setSorgcode(getSorgcode());
		if (datacheck(dto, "modify")) {
			return null;
		}
		try {
			tsPaybankService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsPaybankDto();
		setSorgcode("");
		return super.rebackResultView(o);
	}

	/**
	 * Direction: ���ܲ�ѯ ename: queryResultList ���÷���: viewers: ��ѯ���ҳ�� messages:
	 */
	public String queryResultList(Object o) {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
//		dto = new TsPaybankDto();
		setSorgcode("");
		return "��ѯ���ҳ��";
	}

	/**
	 * Direction: ���� ename: dataExport ���÷���: viewers: * messages:
	 */
	public String dataExport(Object o) {
		
		// ѡ�񱣴�·��
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		List<String> filelist = new ArrayList<String>();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
			return "";
		}
		String clientFilePath;
		String strdate = DateUtil.date2String2(new java.util.Date()); // ��ǰϵͳ������
		String dirsep = File.separator;
		String serverFilePath;
		try {
			searchdto.setSaddcolumn1("");
			searchdto.setSaddcolumn1(banktype);
			serverFilePath = tsPaybankService.bankInfoexport(searchdto).replace("\\",
					"/");
			int j = serverFilePath.lastIndexOf("/");
			String partfilepath  = serverFilePath.replaceAll(serverFilePath.substring(
					0, j + 1), "");
			clientFilePath = filePath + dirsep
					+ partfilepath;
			File f = new File(clientFilePath);
			if(f.exists()){
				f.delete();
			}
			File dir = new File(f.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			ClientFileTransferUtil.downloadFile(serverFilePath, clientFilePath);
			MessageDialog.openMessageDialog(null, "�����ļ��ɹ����ļ������ڣ�\n"
					+ clientFilePath);

		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest pageRequest) {
		
		searchdto.setSorgcode(StateConstant.ORG_CENTER_CODE);
		String sqlwhere="";
		if (null != searchdto.getSbankno()) {
			sqlwhere += " and  S_BANKNO like '" + searchdto.getSbankno() + "%'";
		}
		if (null != searchdto.getSbankname()) {
			sqlwhere += " and  S_BANKNAME like '%" + searchdto.getSbankname() + "%'";
		}
        if (null!=searchdto.getSpaybankname()) {
			sqlwhere += " and  S_PAYBANKNAME like '%"+searchdto.getSpaybankname()+"%'";
		}
        if(null!= banktype&&!"".equals(banktype) ) {
			sqlwhere += " and  substr(s_bankno,1,3)='"+banktype+"'";
		}
		if(!sqlwhere.equals("")){
			sqlwhere=sqlwhere.substring(sqlwhere.indexOf("and")+3) + " and ";
		}
		try {
			TsPaybankDto idto =new TsPaybankDto();
			idto=(TsPaybankDto) searchdto.clone();
			if (null != searchdto.getSbankno()) {
				idto.setSbankno(null);
			}
			if (null != searchdto.getSbankname()) {
				idto.setSbankname("");
			}
	        if (null!=searchdto.getSpaybankname()) {
	        	idto.setSpaybankname("");
			}
			return commonDataAccessService.findRsByDtoWithWherePaging(idto,
					pageRequest, sqlwhere+" 1=1 ");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}
	

	private void init() {
		TsOrganDto orgdto = new TsOrganDto();
		// ����ֻ�������Լ�

		orgdto.setSorgcode(loginfo.getSorgcode());

		try {
			list = commonDataAccessService.findRsByDto(orgdto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	private boolean datacheck(TsPaybankDto dto, String flag) {
		if (null == dto.getSorgcode() || dto.getSorgcode().trim().length() == 0) {
			MessageDialog.openMessageDialog(null, "����������벻��Ϊ�գ�");
			return true;
		} else if (null == dto.getSpaybankno()
				|| dto.getSpaybankno().trim().length() == 0) {
			MessageDialog.openMessageDialog(null, "�������кŲ���Ϊ�գ�");
			return true;
		} else if (null == dto.getSbankno()
				|| dto.getSbankno().trim().length() == 0) {
			MessageDialog.openMessageDialog(null, "�кŲ���Ϊ�գ�");
			return true;
		} else if (null == dto.getSbankname()
				|| dto.getSbankname().trim().length() == 0) {
			MessageDialog.openMessageDialog(null, "���Ʋ���Ϊ��!");
			return true;
		}
		if(!isNumber(dto.getSbankno())|| dto.getSbankno().trim().length() != 12){
			MessageDialog.openMessageDialog(null, "�кű���Ϊ12λ���֡�");
			return true;
		}
		if(!isNumber(dto.getSpaybankno()) || dto.getSpaybankno().trim().length() != 12){
			MessageDialog.openMessageDialog(null, "�����кű���Ϊ12λ���֡�");
			return true;
		}
		if(!isNumber(dto.getSofcity())){
			MessageDialog.openMessageDialog(null, "���д������Ϊ���֡�");
			return true;
		}

		if (flag.equals("add")) {
			TsPaybankDto tempdto = new TsPaybankDto();
			tempdto.setSorgcode(sorgcode);
			tempdto.setSbankno(dto.getSbankno());
			List list = null;
			try {
				list = commonDataAccessService.findRsByDto(tempdto);
			} catch (ITFEBizException e) {
				MessageDialog.openErrorDialog(null, e);
				return true;
			}
			if (null != list && list.size() > 0) {
				MessageDialog.openMessageDialog(null, "�����������+�к��ظ�!");
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * �ж��ַ����Ƿ����������
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str)
    {
        java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("[0-9]*");
        java.util.regex.Matcher match=pattern.matcher(str);
        if(match.matches()==false)
        {
           return false;
        }
        else
        {
           return true;
        }
    }
	
	
}
package com.cfcc.itfe.client.para.tsconvertassitsign;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.ChinaTest;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.persistence.dto.TsConvertassitsignDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;

/**
 * codecomment:
 * 
 * @author caoyg
 * @time 09-10-20 08:42:02 ��ϵͳ: Para ģ��:TsConvertassitsign ���:TsConvertassitsign
 */
@SuppressWarnings("unchecked")
public class TsConvertassitsignBean extends AbstractTsConvertassitsignBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(TsConvertassitsignBean.class);
	private List list;
	private ITFELoginInfo loginfo = null;
	private TsConvertassitsignDto searchDto;
	List filepath = null;
	private String filedemo="�����ļ�Ϊtxt��ʽ�ļ�,�ļ������ļ�·���в����к��֡��ļ���ÿ��Ϊһ�����ݣ�һ�����ݷ�5���ֶΣ�ÿ���ֶ��Զ��ŷָ����ֶκ���:�����������,�����������,Ԥ���Ŀ����,TBS������־,TIPS������־";
	public TsConvertassitsignBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		searchDto = new TsConvertassitsignDto();
		searchDto.setSorgcode(loginfo.getSorgcode());
		dto = new TsConvertassitsignDto();
		dto.setSorgcode(loginfo.getSorgcode());
		filepath = new ArrayList();
		pagingcontext = new PagingContext(this);
		init();
	}

	/**
	 * Direction: ��ת¼����� ename: goInput ���÷���: viewers: ¼����� messages:
	 */
	public String goInput(Object o) {
		dto = new TsConvertassitsignDto();
		dto.setSorgcode(loginfo.getSorgcode());
		return super.goInput(o);
	}

	/**
	 * Direction: ¼�뱣�� ename: inputSave ���÷���: viewers: * messages:
	 * 
	 * @param stbsassitsign
	 * @param stbsassitsign
	 */
	public String inputSave(Object o) {
		if (datacheck(dto)) {
			return null;
		}
		try {
			tsConvertassitsignService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);

			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsConvertassitsignDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ���ص�ά������ ename: backMaintenance ���÷���: viewers: ά������ messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsConvertassitsignDto();
		dto.setSorgcode(loginfo.getSorgcode());
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsConvertassitsignDto) o;
		return super.singleSelect(o);
	}
	/**
	 * Direction: ��������
	 * ename: dataexport
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String dataexport(Object o){
    	// ѡ�񱣴�·��
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
			return "";
		}
		String fileName = filePath+ File.separator+ "������־��Ӧ����" +loginfo.getCurrentDate().replaceAll("-", "") + ".txt";
		try {
			TsConvertassitsignDto finddto = new TsConvertassitsignDto();
			if(!"000000000000".equals(loginfo.getSorgcode()))
				finddto.setSorgcode(loginfo.getSorgcode());
			List<TsConvertassitsignDto> result = commonDataAccessService.findRsByDto(finddto);
			if(null == result || result.size() == 0 ){
				MessageDialog.openMessageDialog(null, "û����Ҫ���������ݣ�");
				return null;
			}
			expdata(result,fileName);
			MessageDialog.openMessageDialog(null, "�����ɹ���");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
        return super.dataexport(o);
    }
    private void expdata(List<TsConvertassitsignDto> result ,String fileName) throws FileOperateException{
    	StringBuffer resultStr = new StringBuffer();
		for(TsConvertassitsignDto tmp : result){
			resultStr.append(tmp.getSorgcode() + ","); //�����������
			resultStr.append(tmp.getStrecode() + ",");	//�������
			resultStr.append(tmp.getSbudgetsubcode() + ",");	//Ԥ���Ŀ����
			resultStr.append(tmp.getStbsassitsign() + ",");	//TBS������־
			resultStr.append(tmp.getStipsassistsign() + System.getProperty("line.separator"));	//TIPS������־
		}
		FileUtil.getInstance().writeFile(fileName, resultStr.toString());
    }
	/**
	 * Direction: ׷�ӵ�������
	 * ename: adddataimport
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String adddataimport(Object o){
    	try{
    	// �ӿ�����
			List<File> fileList = new ArrayList<File>();
			List<String> serverpathlist = new ArrayList<String>();
			// �ж��Ƿ�ѡ���ļ�
			if (null == filepath || filepath.size() == 0) {
				MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص��ļ���");
				return null;
			}
			if (filepath.size() > 1) {
				MessageDialog.openMessageDialog(null, "��ѡ�����ļ������Ƕ����");
				return null;
			}
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			for (int i = 0; i < filepath.size(); i++) {
				File tmpfile = (File) filepath.get(i);
				// �ļ�������
				String tmpfilename = tmpfile.getName();
				// ��ȡ�ļ���·��
				String tmpfilepath = tmpfile.getAbsolutePath();
				if(ChinaTest.isChinese(tmpfilepath))
				{
					MessageDialog.openMessageDialog(null, " �ļ������ļ�·���в����к��֣�");
					return null;
				}
				if (!tmpfilename.trim().toLowerCase().endsWith(".txt")) {
					continue;
				}
				if (null == tmpfile || null == tmpfilename
						|| null == tmpfilepath) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ���ص��ļ���");
					return null;
				}
				// �ļ��ϴ�����¼��־
				String serverpath = ClientFileTransferUtil.uploadFile(tmpfile
						.getAbsolutePath());
				fileList.add(new File(serverpath.replace("/", File.separator).replace("\\",File.separator)));
				serverpathlist.add(serverpath);
			}
			if(fileList!=null&&fileList.size()>0)
			{
				
				tsConvertassitsignService.dataimport(String.valueOf(fileList.get(0)), "addimport");
			}
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openMessageDialog(null, " ׷�ӵ������ݳɹ���");
			return null;
    	}catch(Exception e)
    	{
    		MessageDialog.openErrorDialog(null, e);
    	}
		
        return super.adddataimport(o);
    }
    
	/**
	 * Direction: ɾ����������
	 * ename: deletedataimport
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String deletedataimport(Object o){
    	try
    	{
	    	// �ӿ�����
			List<File> fileList = new ArrayList<File>();
			List<String> serverpathlist = new ArrayList<String>();
			// �ж��Ƿ�ѡ���ļ�
			if (null == filepath || filepath.size() == 0) {
				MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص��ļ���");
				return null;
			}
			if (filepath.size() > 1) {
				MessageDialog.openMessageDialog(null, "��ѡ�����ļ������Ƕ����");
				return null;
			}
			if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(
					this.editor.getCurrentComposite().getShell(), "��ʾ",
					"�ò�����ɾ��ԭ�и�����־���ݣ��뱸�����ݣ��Ƿ�������룿")) {
				return null;
			}
			DisplayCursor.setCursor(SWT.CURSOR_WAIT);
			for (int i = 0; i < filepath.size(); i++) {
				File tmpfile = (File) filepath.get(i);
				// �ļ�������
				String tmpfilename = tmpfile.getName();
				// ��ȡ�ļ���·��
				String tmpfilepath = tmpfile.getAbsolutePath();
				if(ChinaTest.isChinese(tmpfilepath))
				{
					MessageDialog.openMessageDialog(null, " �ļ������ļ�·���в����к��֣�");
					return null;
				}
				if (!tmpfilename.trim().toLowerCase().endsWith(".txt")) {
					continue;
				}
				if (null == tmpfile || null == tmpfilename
						|| null == tmpfilepath) {
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ���ص��ļ���");
					return null;
				}
				// �ļ��ϴ�����¼��־
				String serverpath = ClientFileTransferUtil.uploadFile(tmpfile
						.getAbsolutePath());
				fileList.add(new File(serverpath));
				serverpathlist.add(serverpath);
			}
			if(fileList!=null&&fileList.size()>0)
			{
				tsConvertassitsignService.dataimport(String.valueOf(fileList.get(0)), "deleteimport");
			}
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openMessageDialog(null, " ɾ���������ݳɹ���");
			return null;
    	}catch(Exception e)
    	{
    		MessageDialog.openErrorDialog(null, e);
    	}
        return super.deletedataimport(o);
    }
	/**
	 * Direction: ɾ�� ename: delete ���÷���: viewers: * messages:
	 */
	public String delete(Object o) {
		if (dto.getStbsassitsign() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		try {
			tsConvertassitsignService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsConvertassitsignDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ���޸Ľ��� ename: goModify ���÷���: viewers: �޸Ľ��� messages:
	 */
	public String goModify(Object o) {
		if (dto == null || dto.getStbsassitsign() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}

		return super.goModify(o);
	}

	/**
	 * Direction: �޸ı��� ename: modifySave ���÷���: viewers: * messages:
	 * 
	 * @param stbsassitsign
	 * @param stbsassitsign
	 */
	public String modifySave(Object o) {
		if (datacheck(dto)) {
			return null;
		}
		try {
			tsConvertassitsignService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsConvertassitsignDto();
		return super.backMaintenance(o);

	}

	private void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	
	/**
	 * Direction: ��ѯ
	 * ename: search
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String search(Object o){
    	init();
    	PageResponse pageResponse=pagingcontext.getPage();
    	if(pageResponse.getTotalCount()==0)
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼��");		
        editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
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
		
		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(searchDto,
					pageRequest, "1=1");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	private boolean datacheck(TsConvertassitsignDto dto) {
		if (null == dto.getSorgcode() || dto.getSorgcode().trim().length() == 0) {
			MessageDialog.openMessageDialog(null, "����������벻��Ϊ�գ�");
			return true;
		} else if (null == dto.getStbsassitsign()
				|| dto.getStbsassitsign().trim().length() == 0) {
			MessageDialog.openMessageDialog(null, "����������־����Ϊ�գ�");
			return true;

		} else if (null == dto.getStipsassistsign()
				|| dto.getStipsassistsign().trim().length() == 0) {
			MessageDialog.openMessageDialog(null, "TIPS������־����Ϊ�գ�");
			return true;
		} else if (dto.getStipsassistsign().trim().length() != 35) {
			MessageDialog.openMessageDialog(null, "TIPS������־����Ϊ35λ��");
			return true;
		}
		return false;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public TsConvertassitsignDto getSearchDto() {
		return searchDto;
	}

	public void setSearchDto(TsConvertassitsignDto searchDto) {
		this.searchDto = searchDto;
	}

	public List getFilepath() {
		return filepath;
	}

	public void setFilepath(List filepath) {
		this.filepath = filepath;
	}

	public String getFiledemo() {
		return filedemo;
	}

	public void setFiledemo(String filedemo) {
		this.filedemo = filedemo;
	}
	
	
}
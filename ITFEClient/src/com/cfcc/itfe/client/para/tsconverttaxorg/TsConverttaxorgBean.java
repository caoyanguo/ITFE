package com.cfcc.itfe.client.para.tsconverttaxorg;

import java.io.File;
import java.util.*;

import org.apache.commons.logging.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.ChinaTest;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;

/**
 * codecomment:
 * 
 * @author caoyg
 * @time 09-10-20 08:42:01 ��ϵͳ: Para ģ��:TsConverttaxorg ���:TsConverttaxorg
 */
@SuppressWarnings("unchecked")
public class TsConverttaxorgBean extends AbstractTsConverttaxorgBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsConverttaxorgBean.class);
	private List list1;
	private List list2;
	private String filedemo="�����ļ�Ϊtxt��ʽ�ļ�,�ļ������ļ�·���в��ܰ������֡��ļ���ÿ��Ϊһ�����ݣ�һ�����ݷ�5���ֶΣ�ÿ���ֶ��Զ��ŷָ����ֶκ���:�����������,�����������,TBS���ջ��ش���,TIPS���ջ��ش���,���ջ�������(1-��˰;2-��˰;3-����;4-����;5-����;)";
	private ITFELoginInfo loginfo = null;
	private TsConverttaxorgDto initdto = new TsConverttaxorgDto();
	private List<TsTaxorgDto> taxorglist=new ArrayList<TsTaxorgDto>();
	private String smodicount;
	List filepath = null;
	public TsConverttaxorgBean() {
		super();
		filepath = new ArrayList();
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
		smodicount = null;
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
		if(null != smodicount && !"".equals(smodicount)) {
			dto.setImodicount(Integer.valueOf(smodicount));
		}		
		if (datacheck(dto, "input")) {
			return null;
		}
		try {
			
			tsConverttaxorgService.addInfo(dto);
			smodicount = null;
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
		String fileName = filePath+ File.separator+ "���ջ��ض�Ӧ����" +loginfo.getCurrentDate().replaceAll("-", "") + ".txt";
		try {
			TsConverttaxorgDto finddto = new TsConverttaxorgDto();
			if(!"000000000000".equals(loginfo.getSorgcode()))
				finddto.setSorgcode(loginfo.getSorgcode());
			List<TsConverttaxorgDto> result = commonDataAccessService.findRsByDto(finddto);
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
    private void expdata(List<TsConverttaxorgDto> result ,String fileName) throws FileOperateException{
    	StringBuffer resultStr = new StringBuffer();
		for(TsConverttaxorgDto tmp : result){
			resultStr.append(tmp.getSorgcode() + ","); //�����������
			resultStr.append(tmp.getStrecode() + ",");	//�������
			resultStr.append(tmp.getStbstaxorgcode() + ",");	//TBS���ջ���
			resultStr.append(tmp.getStcbstaxorgcode() + ",");	//TIPS���ջ���
			resultStr.append(tmp.getImodicount() + System.getProperty("line.separator"));	//���ջ�������
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
				tsConverttaxorgService.dataimport(String.valueOf(fileList.get(0)), "addimport");
			}
			MessageDialog.openMessageDialog(null, " ׷�ӵ������ݳɹ���");
			return null;
    	}catch(Exception e)
    	{
    		MessageDialog.openErrorDialog(null, e);
    	}finally {
    		DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    	}
    	this.editor.fireModelChanged();
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
					"�ò�����ɾ��ԭ�����ջ��ض������ݣ��뱸�����ݣ��Ƿ�������룿")) {
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
				tsConverttaxorgService.dataimport(String.valueOf(fileList.get(0)), "deleteimport");
			}
			MessageDialog.openMessageDialog(null, " ɾ���������ݳɹ���");
			return null;
    	}catch(Exception e)
    	{
    		MessageDialog.openErrorDialog(null, e);
    	}finally {
    		DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    	}
		
        return super.deletedataimport(o);
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
			tsConverttaxorgService.delInfo(dto);
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
		smodicount = dto.getImodicount().toString();
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
		if(null != smodicount && !"".equals(smodicount)) {
			dto.setImodicount(Integer.valueOf(smodicount));
		}
		if (datacheck(dto, "modify")) {
			return null;
		}
		try {
			tsConverttaxorgService.modInfo(initdto,dto);
			smodicount = null;
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
			
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}

		TsTreasuryDto tredto = new TsTreasuryDto();
		tredto.setSorgcode(loginfo.getSorgcode());
		try {
			TsTaxorgDto taxorgdto=new TsTaxorgDto();
			taxorgdto.setSorgcode(loginfo.getSorgcode());
			taxorglist=commonDataAccessService.findRsByDto(taxorgdto, " ORDER BY S_TAXORGCODE");
			list2 = commonDataAccessService.findRsByDto(tredto);
			if(taxorglist!=null&&taxorglist.size()>0)
			{
				List<TsTaxorgDto> temp = taxorglist;
				taxorglist = new ArrayList<TsTaxorgDto>();
				Map<String,TsTaxorgDto> tempMap = new HashMap<String,TsTaxorgDto>();
				for(TsTaxorgDto tempdto:temp)
				{
					if(!tempMap.containsKey(tempdto.getStaxorgcode()))
					{
						if(tempdto.getStaxorgname()==null||"".equals(tempdto.getStaxorgname()))
							tempdto.setStaxorgname(tempdto.getStaxorgcode());
						tempMap.put(tempdto.getStaxorgcode(), tempdto);
						taxorglist.add(tempdto);
					}
				}
			}
		} catch (Throwable e) {
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
			MessageDialog.openMessageDialog(null, "TIPS���ջ��ش��벻��Ϊ�գ�");
			return true;
		} else if (null == dto.getImodicount()
				|| !(dto.getImodicount() == 0 || dto.getImodicount() == 1 || dto.getImodicount() == 2 || dto.getImodicount() == 3 || dto.getImodicount() == 4 || dto.getImodicount() == 5)) {
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
		tempdto.setStbstaxorgcode(dto.getStbstaxorgcode());
		tempdto.setStrecode(dto.getStrecode());
		tempdto.setStcbstaxorgcode(dto.getStcbstaxorgcode());

		List list = null;
		try {
			list = commonDataAccessService.findRsByDto(tempdto);
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return true;
		}
		if (operType.equals("input")) {
			if (null != list && list.size() > 0) {
				MessageDialog.openMessageDialog(null, "�����������+�����������+TBS���ջ��ش���+TIPS���ջ��ش����ظ���");
				return true;
			} else {
				return false;
			}
		} else {
			if (null != list && list.size() > 1) {
				MessageDialog.openMessageDialog(null, "�����������+�������+TBS���ջ��ش���+TIPS���ջ��ش����ظ���");
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

	public List<TsTaxorgDto> getTaxorglist() {
		return taxorglist;
	}

	public void setTaxorglist(List<TsTaxorgDto> taxorglist) {
		this.taxorglist = taxorglist;
	}

	public String getSmodicount() {
		return smodicount;
	}

	public void setSmodicount(String smodicount) {
		this.smodicount = smodicount;
	}
	public String getFiledemo() {
		return filedemo;
	}

	public void setFiledemo(String filedemo) {
		this.filedemo = filedemo;
	}

	public List getFilepath() {
		return filepath;
	}

	public void setFilepath(List filepath) {
		this.filepath = filepath;
	}
	
	
}
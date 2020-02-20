package com.cfcc.itfe.client.recbiz.tfunitrecordmain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.persistence.dto.TfUnitrecordmainDto;
import com.cfcc.itfe.persistence.dto.TfUnitrecordsubDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author Administrator
 * @time   14-09-22 09:54:37
 * ��ϵͳ: RecBiz
 * ģ��:TfUnitrecordmain
 * ���:TfUnitrecordmain
 */
public class TfUnitrecordmainBean extends AbstractTfUnitrecordmainBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TfUnitrecordmainBean.class);
    private ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
    private ITFELoginInfo logInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    private TfUnitrecordsubBean subBean;
    public TfUnitrecordmainBean() {
      super();
      searchDto = new TfUnitrecordmainDto();
      searchDto.setSorgcode(logInfo.getSorgcode());
      detailDto = new TfUnitrecordmainDto();
      pagingContext = new PagingContext(this);
      subSearchDto = new TfUnitrecordsubDto();
      subBean = new TfUnitrecordsubBean();
    }
    
	/**
	 * Direction: ��ѯ
	 * ename: searchDtoList
	 * ���÷���: 
	 * viewers: ��ѯ���
	 * messages: 
	 */
    public String searchDtoList(Object o){
    	PageRequest pageRequest = new PageRequest();
		try {
			PageResponse p = this.retrieve(pageRequest);
			if (p == null || p.getPageRowCount() == 0) {
				MessageDialog.openMessageDialog(null, "��ѯ���ݲ�����!");
				return null;
			}
			pagingContext.setPage(p);
		} catch (Exception e) {
			log.error("��ѯ���ݴ���", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.searchDtoList(o);
    }

	/**
	 * Direction: ���ز�ѯ
	 * ename: backToSearch
	 * ���÷���: 
	 * viewers: ��ѯ����
	 * messages: 
	 */
    public String backToSearch(Object o){
    	searchDto = new TfUnitrecordmainDto();
    	searchDto.setSorgcode(logInfo.getSorgcode());
        return super.backToSearch(o);
    }

	/**
	 * Direction: ���ز�ѯ���
	 * ename: backToResult
	 * ���÷���: 
	 * viewers: ��ѯ���
	 * messages: 
	 */
    public String backToResult(Object o){
          return super.backToResult(o);
    }

	/**
	 * Direction: ����ϸ
	 * ename: toDetail
	 * ���÷���: 
	 * viewers: ��ϸ��Ϣ
	 * messages: 
	 */
    public String toDetail(Object o){
          return super.toDetail(o);
    }

	/**
	 * Direction: ���ز�ѯ���
	 * ename: detailToResult
	 * ���÷���: 
	 * viewers: ��ѯ���
	 * messages: 
	 */
    public String detailToResult(Object o){
          return super.detailToResult(o);
    }

	/**
	 * Direction: ��ѡ
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	if(o instanceof TfUnitrecordmainDto){
    		subSearchDto.setIvousrlno(((TfUnitrecordmainDto)o).getIvousrlno());
    	}
    	return super.singleSelect(o);
    }

	/**
	 * Direction: ˫��
	 * ename: doubleClick
	 * ���÷���: 
	 * viewers: �ӱ��ѯ���
	 * messages: 
	 */
    public String doubleClick(Object o){
    	if(o instanceof TfUnitrecordmainDto){
    		subSearchDto.setIvousrlno(((TfUnitrecordmainDto)o).getIvousrlno());
    	}
    	return subQueryResult(o);
    }

	/**
	 * Direction: �ӱ��б��������б�
	 * ename: subBackToSearch
	 * ���÷���: 
	 * viewers: ��ѯ���
	 * messages: 
	 */
    public String subBackToSearch(Object o){
    	subSearchDto = new TfUnitrecordsubDto();
        return super.subBackToSearch(o);
    }

	/**
	 * Direction: ��ѯ���ӱ���ҳ��
	 * ename: subQueryResult
	 * ���÷���: 
	 * viewers: �ӱ��ѯ���
	 * messages: 
	 */
    public String subQueryResult(Object o){
    	if (subSearchDto == null || subSearchDto.getIvousrlno() == null) {
			MessageDialog.openMessageDialog(null,"��ѡ��һ����¼��ѯ��ϸ��Ϣ��");
			return null;
		}
		try {
			String isJumpNext = subBean.searchDtoList(subSearchDto);
			if (isJumpNext != null) {
				return super.subQueryResult(o);
			} else {
				MessageDialog.openMessageDialog(null,"δ��ѯ����ϸ��Ϣ��");
				return null;
			}
		} catch (Exception e) {
			log.info("��ѯ��ϸ��Ϣ", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
    }
    
    /**
	 * Direction: ���˴��뵼��
	 * ename: dataExport
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String dataExport(Object o){
    	// ѡ�񱣴�·��
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent().getActiveShell());
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
			serverFilePath = tfUnitrecordmainService.legalPersonCodeExport(searchDto).replace("\\","/");
			int j = serverFilePath.lastIndexOf("/");
			String partfilepath  = serverFilePath.replaceAll(serverFilePath.substring(0, j + 1), "");
			clientFilePath = filePath + dirsep + partfilepath;
			File f = new File(clientFilePath);
			if(f.exists()){
				f.delete();
			}
			File dir = new File(f.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			ClientFileTransferUtil.downloadFile(serverFilePath, clientFilePath);
			MessageDialog.openMessageDialog(null, "�����ļ��ɹ����ļ������ڣ�\n" + clientFilePath);

		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}
        return "";
    }
    
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest request) {
    	PageResponse response = new PageResponse(request);
		try {
			String wheresql = " 1=1 ";
			response = commonDataAccessService.findRsByDtoWithWherePaging(searchDto, request, wheresql);
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			this.editor.fireModelChanged();
		}
		return response;
	}

	public TfUnitrecordsubBean getSubBean() {
		return subBean;
	}

	public void setSubBean(TfUnitrecordsubBean subBean) {
		this.subBean = subBean;
	}

}
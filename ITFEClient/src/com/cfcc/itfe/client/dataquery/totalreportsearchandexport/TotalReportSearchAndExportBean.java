package com.cfcc.itfe.client.dataquery.totalreportsearchandexport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.*;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;

/**
 * codecomment: 
 * @author zl
 * @time   13-04-17 11:01:48
 * ��ϵͳ: DataQuery
 * ģ��:totalReportSearchAndExport
 * ���:TotalReportSearchAndExport
 */
@SuppressWarnings("unchecked")
public class TotalReportSearchAndExportBean extends AbstractTotalReportSearchAndExportBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TotalReportSearchAndExportBean.class);
    private String strecode = null;
    private String selectedtable = null;
    private PagingContext pagingcontext = null;
	private List checkList = null;
    private List childList = null;
    private List statelist2 ;
    private List<TsTreasuryDto> treList = null;
    ITFELoginInfo loginfo = null;
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
    public TotalReportSearchAndExportBean() {
      super();
      findto = new TrIncomedayrptDto();
      findto.setSrptdate(TimeFacade.getCurrentStringTime());
      checkList = new ArrayList();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      pagingcontext = new PagingContext(this);
      selectedtable="0";
      init();            
    }
    
    private void init() {
		// ��ѯ���������dto
		TsTreasuryDto tredto = new TsTreasuryDto();
		// ���Ļ�������
		String centerorg = null;

		try {
			this.statelist2 = new ArrayList<TdEnumvalueDto>();

			TdEnumvalueDto valuedtoa = new TdEnumvalueDto();
			valuedtoa.setStypecode("��ǰ��");
			valuedtoa.setSvalue("0");
			this.statelist2.add(valuedtoa);
			
			TdEnumvalueDto valuedtob = new TdEnumvalueDto();
			valuedtob.setStypecode("��ʷ��");
			valuedtob.setSvalue("1");
			this.statelist2.add(valuedtob);
			centerorg = StateConstant.ORG_CENTER_CODE;
			// ���Ļ�����ȡ�����й����б�
			if (loginfo.getSorgcode().equals(centerorg)) {
				treList = commonDataAccessService.findRsByDto(tredto);
			}
			// �����Ļ�����ȡ�õ�¼������Ӧ����
			else {
				tredto = new TsTreasuryDto();
				tredto.setSorgcode(loginfo.getSorgcode());
				treList = commonDataAccessService.findRsByDto(tredto);
			}
			// ��ʼ���������Ĭ��ֵ
			if (treList.size() > 0) {
				strecode = treList.get(0).getStrecode();
				tredto.setSgoverntrecode(strecode);
				tredto.setSorgcode("");
				childList = commonDataAccessService.findRsByDto(tredto);
			}
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
			return;
		}
    }
	/**
	 * Direction: ��ѯ�б�
	 * ename: searchTotalReport
	 * ���÷���: 
	 * viewers: �ܶ�ֳ��б�
	 * messages: 
	 */
    public String searchTotalReport(Object o){
    	findto.setStrecode(strecode);
    	findto.setSbillkind("4");//��������Ϊ�ܶ�ֳɱ���
    	if(findto.getStrecode()==null||"".equals(findto.getStrecode())||findto.getSrptdate()==null||"".equals(findto.getSrptdate()))
    	{
    		MessageDialog.openMessageDialog(null, "������롢�������ڲ���Ϊ��!");
    		return "";
    	}
    	PageRequest mainpageRequest = new PageRequest();
		PageResponse response=null;
		response = retrieve(mainpageRequest);
		if (response.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
			 return "";
		}
		this.pagingcontext.setPage(response);
		editor.fireModelChanged();
        return super.searchTotalReport(o);
    }

	/**
	 * Direction: ���ز�ѯ
	 * ename: returnTotalReport
	 * ���÷���: 
	 * viewers: �ܶ�ֳɲ�ѯ
	 * messages: 
	 */
    public String returnTotalReport(Object o){
          return super.returnTotalReport(o);
    }

	/**
	 * Direction: ����TXT
	 * ename: exportTotalReport
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportTotalReport(Object o){
    	// ѡ�񱣴�·��
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
			return "";
		}
		String clientFilePath;
		String dirsep = File.separator;
		String serverFilePath;
		try {
			findto.setStrecode(strecode);
	    	findto.setSbillkind("4");//��������Ϊ�ܶ�ֳɱ���
			StringBuffer where = new StringBuffer(" and 1=1 ");
    		if(checkList!=null&&checkList.size()>0)
    		{
    			
    			where.append(" and s_trecode in('"+findto.getStrecode()+"',");
    			findto.setStrecode("");
    			for(int i=0;i<checkList.size();i++)
    			{
    				if(i==checkList.size()-1)
    					where.append("'"+((TsTreasuryDto)checkList.get(i)).getStrecode()+"')");
    				else
    					where.append("'"+((TsTreasuryDto)checkList.get(i)).getStrecode()+"',");
    			}
    		}
			if("0".equals(getSelectedtable()))
				serverFilePath = totalReportSearchAndExportService.dataExport(findto,where.toString()).replace("\\","/");
			else
			{
				HtrIncomedayrptDto hfindto = new HtrIncomedayrptDto();
				hfindto.setStrecode(findto.getStrecode());
				hfindto.setSrptdate(findto.getSrptdate());
				hfindto.setSbillkind(findto.getSbillkind());
				serverFilePath = totalReportSearchAndExportService.dataExport(hfindto,where.toString()).replace("\\","/");
			}
			int j = serverFilePath.lastIndexOf("/");
			String partfilepath  = serverFilePath.replaceAll(serverFilePath.substring(
					0, j + 1), "");
			clientFilePath = filePath + dirsep
					+ partfilepath;
			File f = new File(clientFilePath);
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
        return super.exportTotalReport(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {
    	try {
    		StringBuffer where = new StringBuffer(" 1=1 ");
    		TrIncomedayrptDto tmpDto = (TrIncomedayrptDto) findto.clone();
    		if(checkList!=null&&checkList.size()>0)
    		{
    			
    			where.append(" and s_trecode in('"+tmpDto.getStrecode()+"',");
    			tmpDto.setStrecode("");
    			for(int i=0;i<checkList.size();i++)
    			{
    				if(i==checkList.size()-1)
    					where.append("'"+((TsTreasuryDto)checkList.get(i)).getStrecode()+"')");
    				else
    					where.append("'"+((TsTreasuryDto)checkList.get(i)).getStrecode()+"',");
    			}
    		}
			PageResponse response = null;
			if("0".equals(getSelectedtable()))
				response = commonDataAccessService.findRsByDtoWithWherePaging(tmpDto, arg0, where.toString());
			else
			{
				HtrIncomedayrptDto hfindto = new HtrIncomedayrptDto();
				hfindto.setStrecode(tmpDto.getStrecode());
				hfindto.setSrptdate(tmpDto.getSrptdate());
				hfindto.setSbillkind(tmpDto.getSbillkind());
				response = commonDataAccessService.findRsByDtoWithWherePaging(hfindto, arg0, where.toString());
			}
			return response;
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
	}
    /**
	 * Direction: ȫѡ
	 * ename: selectedAll
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectedAll(Object o){
    	if (checkList.size() > 0) {
			checkList = new ArrayList();
		} else {
			checkList = childList;
		}
		this.editor.fireModelChanged();
        return "";
    }
	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		TotalReportSearchAndExportBean.log = log;
	}

	public String getStrecode() {
		return strecode;
	}

	public void setStrecode(String strecode) {
		this.strecode = strecode;
		TsTreasuryDto tredto = new TsTreasuryDto();
		try {
			tredto.setSgoverntrecode(strecode);
//			tredto.setSorgcode(loginfo.getSorgcode());
			childList = commonDataAccessService.findRsByDto(tredto);
			checkList = new ArrayList();
			editor.fireModelChanged();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return;
		}
	}

	public PagingContext getPagingcontext() {
		return pagingcontext;
	}

	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
	}

	public List getCheckList() {
		return checkList;
	}

	public void setCheckList(List checkList) {
		this.checkList = checkList;
	}

	public List getChildList() {
		return childList;
	}

	public void setChildList(List childList) {
		this.childList = childList;
	}

	public List<TsTreasuryDto> getTreList() {
		return treList;
	}

	public void setTreList(List<TsTreasuryDto> treList) {
		this.treList = treList;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public ICommonDataAccessService getCommonDataAccessService() {
		return commonDataAccessService;
	}

	public void setCommonDataAccessService(
			ICommonDataAccessService commonDataAccessService) {
		this.commonDataAccessService = commonDataAccessService;
	}

	public String getSelectedtable() {
		return selectedtable;
	}

	public void setSelectedtable(String selectedtable) {
		this.selectedtable = selectedtable;
	}

	public List getStatelist2() {
		return statelist2;
	}

	public void setStatelist2(List statelist2) {
		this.statelist2 = statelist2;
	}

}
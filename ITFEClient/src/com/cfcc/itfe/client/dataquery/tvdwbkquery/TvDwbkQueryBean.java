package com.cfcc.itfe.client.dataquery.tvdwbkquery;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;
import java.math.*;
import java.sql.Date;

import org.apache.commons.logging.*;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.file.DacctChooseDialog;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.HtvDwbkDto;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvDwbkReportDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * codecomment: 
 * @author t60
 * @time   12-02-22 14:06:01
 * ��ϵͳ: DataQuery
 * ģ��:tvDwbkQuery
 * ���:TvDwbkQuery
 */
public class TvDwbkQueryBean extends AbstractTvDwbkQueryBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TvDwbkQueryBean.class);
    private String selectedtable;
    private List<TdEnumvalueDto> statelist;
    private List<TvDwbkReportDto> reportRs = null;
	private Map<String, Comparable> reportmap = new HashMap<String, Comparable>();
	private String reportPath = "com/cfcc/itfe/client/ireport/dwbkReport.jasper";
	 private ITFELoginInfo loginfo;
	 private List<IDto> selectRs=null;
	 private List allList = null;
	 HtvDwbkDto htvdto = null;
    public TvDwbkQueryBean() {
      super();
      selectedtable="0";
      dto = new TvDwbkDto();
      reportRs = new ArrayList<TvDwbkReportDto>();
      selectRs=new ArrayList<IDto>();
      allList = new ArrayList();
      init();   
      pagingcontext = new PagingContext(this);
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      dto.setSbookorgcode(loginfo.getSorgcode());
    }
    
	/**
	 * Direction: ��ѯ
	 * ename: search
	 * ���÷���: 
	 * viewers: �˿���Ϣ�б�
	 * messages: 
	 */
    public String search(Object o){
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		if (pageResponse == null || pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, " ��ѯ�޼�¼��");
			return super.reback(o);
		}
          return super.search(o);
    }
    
	/**
	 * Direction: �����ļ�csv
	 * ename: exportfile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportfile(Object o){
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
		String dirsep = File.separator;
		String serverFilePath;
		try {
			serverFilePath = tvDwbkQueryService.exportfile(dto, selectedtable).replace("\\", "/");
			int j = serverFilePath.lastIndexOf("/");
			String partfilepath = serverFilePath.replaceAll(serverFilePath.substring(0, j + 1), "");
			clientFilePath = filePath + dirsep + partfilepath;
			File f = new File(clientFilePath);
			File dir = new File(f.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			ClientFileTransferUtil.downloadFile(serverFilePath, clientFilePath);
//			String key = commonDataAccessService.getModeKey(loginfo.getSorgcode(), "1");
//			SM3Process.addSM3Sign(clientFilePath, key);
			MessageDialog.openMessageDialog(null, "�����ļ��ɹ����ļ������ڣ�\n" + clientFilePath);
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}
        return "";
    }
    
    /**
	 * Direction: �˿���˵���ӡ
	 * ename: queryPrint
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String queryPrint(Object o){
    	if(null == selectRs || selectRs.size() == 0) {
    		MessageDialog.openMessageDialog(null, "����ѡ���¼!");
    		return null;
    	}
    	BigDecimal totalmoney=new BigDecimal(0);
    	try {
    		List reportTemp = new ArrayList();
    		reportRs = new ArrayList<TvDwbkReportDto>();
    		reportTemp = tvDwbkQueryService.searchForReport(selectedtable,dto);
    		if(reportTemp.size() == 0) {
    			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
    			return super.search(o);
    		}
	    	for(Object obj:reportTemp){
	    		TvDwbkReportDto tvdwbkdto=(TvDwbkReportDto)obj;
	    		for(Object obj2 : selectRs) {
	    			IDto dwbkdto = (IDto)obj2;
	    			if (dwbkdto instanceof  TvDwbkDto) {
	    				TvDwbkDto _dto = (TvDwbkDto) dwbkdto;
	    				if(_dto.getIvousrlno().equals(tvdwbkdto.getIvousrlno())) {
		    				reportRs.add(tvdwbkdto);
		    				totalmoney=totalmoney.add(tvdwbkdto.getFamt());
		    				break;
		    			}
					}else{
						HtvDwbkDto _dto = (HtvDwbkDto) dwbkdto;
						if(_dto.getIvousrlno().equals(tvdwbkdto.getIvousrlno())) {
		    				reportRs.add(tvdwbkdto);
		    				totalmoney=totalmoney.add(tvdwbkdto.getFamt());
		    				break;
		    			}
					}
	    			
	    		}			
	    	}    	
    	} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return super.search(o);
		}
    	if(null == reportRs || reportRs.size() == 0){
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
			return super.search(o);
		}
    	reportmap.put("title", "�˿���˵�");
    	reportmap.put("printdate", new SimpleDateFormat("yyyy��MM��dd��").format(new java.util.Date()));
    	reportmap.put("totalcount", reportRs.size());
    	reportmap.put("totalmoney", totalmoney);
    	reportmap.put("worker",loginfo.getSuserName());
        return "�˿���˵�";
    }

	/**
	 * Direction: ����
	 * ename: reback
	 * ���÷���: 
	 * viewers: �˿��ѯ����
	 * messages: 
	 */
    public String reback(Object o){
          return super.reback(o);
    }
    public String updateFail(Object o){

    	if(selectRs==null||selectRs.size()<=0){
    		MessageDialog.openMessageDialog(null, "��ѡ������һ����¼��");
    		return null;
    	}
    	int i = 0;
    	for(IDto dwbkDto: (List<IDto>)selectRs){
    		if (dwbkDto instanceof TvDwbkDto) {
    			TvDwbkDto  _dto  =(TvDwbkDto) dwbkDto;
    			if (_dto.getSstatus().equals(DealCodeConstants.DEALCODE_ITFE_SUCCESS) && i==0) {
    				Boolean b = org.eclipse.jface.dialogs.MessageDialog.openQuestion(null, "��ʾ", "ѡ��ļ�¼�����Ѿ�����Ϊ�ɹ��ļ�¼,�Ƿ�һ�����Ϊʧ�ܣ�");
    				if (b) {
    					_dto.setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
    				}
    				i++;
    			} else{
    			    _dto.setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
    			}
			}else if (dwbkDto instanceof HtvDwbkDto){
				HtvDwbkDto  _dto  =(HtvDwbkDto) dwbkDto;
				if (_dto.getSstatus().equals(DealCodeConstants.DEALCODE_ITFE_SUCCESS) && i==0) {
					Boolean b = org.eclipse.jface.dialogs.MessageDialog.openQuestion(null, "��ʾ", "ѡ��ļ�¼�����Ѿ�����Ϊ�ɹ��ļ�¼,�Ƿ�һ�����Ϊʧ�ܣ�");
					if (b) {
						_dto.setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
					}
					i++;
				}else{
					_dto.setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
				} 
			}	
    	}
    	try {
			tvDwbkQueryService.updateFail(selectRs);
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		MessageDialog.openMessageDialog(null, "��������ʧ�ܳɹ���");
		selectRs=new ArrayList<IDto>();
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
//		this.editor.fireModelChanged(ModelChangedEvent.SAFE_REFRESH_TABLE_EVENT);
		return this.search(0);
    }
    public String updateSuccess(Object o){
    	
    	if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�ò�������²�ѯ���������˿ⱨ�ĵ�״̬Ϊ�ɹ����Ƿ�����˲�����")) {
		 return null;
    	}
    	DacctChooseDialog dialog = new DacctChooseDialog(null);
    	String dacct="";
		if (IDialogConstants.OK_ID == dialog.open()) {
			dacct = dialog.getDacctstr();
		}
    	List list=new ArrayList();
    	try {
    		dto.setSbookorgcode(loginfo.getSorgcode());
    		if(selectedtable.equals("0")){
    			list=commonDataAccessService.findRsByDto(dto);
    			for(TvDwbkDto tdd:(List<TvDwbkDto>)list){
    				tdd.setDacct(new Date(new SimpleDateFormat("yyyyMMdd").parse(dacct).getTime()));
    				tdd.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
    			}
    		}else{
    			list=commonDataAccessService.findRsByDto(htvdto);
    			for(HtvDwbkDto tdd:(List<HtvDwbkDto>)list){
    				tdd.setDacct(new Date(new SimpleDateFormat("yyyyMMdd").parse(dacct).getTime()));
    				tdd.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
    			} 
    		}
			
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
    	try {
			tvDwbkQueryService.updateSuccess(list);
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		MessageDialog.openMessageDialog(null, "�����������ݳɹ���");
		selectRs=new ArrayList<IDto>();
		this.editor.fireModelChanged(ModelChangedEvent.SAFE_REFRESH_TABLE_EVENT);
    	return this.search(0);
    }
    /**
     * �����˻�
     */
    public String setback(Object o){
    	if(selectRs==null||selectRs.size()<=0){
    		MessageDialog.openMessageDialog(null, "��ѡ������һ����¼��");
    		return null;
    	}
    	try {
    		if(selectedtable.equals("0")){
    			for(IDto tdd:selectRs){
    				((TvDwbkDto)tdd).setCbckflag("1");
    			}
    		}else{
    			for(IDto tdd:selectRs){
    				((HtvDwbkDto)tdd).setCbckflag("1");
    			}
    		}
			tvDwbkQueryService.setback(selectRs);
			selectRs=new ArrayList<IDto>();
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		MessageDialog.openMessageDialog(null, "�����������ݳɹ���");
    	return this.search(o);
    }
    public String selectAll(Object o){    	
    	if(selectRs.size() > 0){
    		selectRs=new ArrayList<IDto>();
    		return "�˿���Ϣ�б�";
    	}
    	selectRs=new ArrayList<IDto>();
    	selectRs.addAll(queryforSelect().getData());
    	this.editor.fireModelChanged();
    	return "�˿���Ϣ�б�";
    }
    
    public PageResponse queryforSelect() {
    	List list = new ArrayList();
    	PageRequest pageRequest = new PageRequest();
    	if(this.pagingcontext != null) {
    		pageRequest.setPageSize(this.getPagingcontext().getPage().getTotalCount());
    	}else {
    		pageRequest.setPageSize(20);
    	}  	    	
    	PageResponse resp = null ;
    	try {
    		if (selectedtable.equals("0")) {
    			resp = commonDataAccessService.findRsByDtoWithWherePaging(dto, pageRequest, " 1=1 ");	
			} else {
				resp = commonDataAccessService.findRsByDtoWithWherePaging(htvdto, pageRequest, " 1=1 ");	
			}
				
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return resp;
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	try {
    		dto.setSbookorgcode(loginfo.getSorgcode());
    		if(selectedtable.equals("0")){
    			return commonDataAccessService.findRsByDtoWithWherePaging(dto,
    					pageRequest, "1=1");
    		}else if(selectedtable.equals("1")){
    			htvdto=new HtvDwbkDto();
    			htvdto.setIvousrlno(dto.getIvousrlno());
    			htvdto.setSdwbkvoucode(dto.getSdwbkvoucode());
    			htvdto.setStaxorgcode(dto.getStaxorgcode());
    			htvdto.setDaccept(dto.getDaccept());
    			htvdto.setCbdgkind(dto.getCbdgkind());
    			htvdto.setSdwbkreasoncode(dto.getSdwbkreasoncode());
    			htvdto.setSbiztype(dto.getSbiztype());
    			htvdto.setSpackageno(dto.getSpackageno());
    			htvdto.setSbookorgcode(dto.getSbookorgcode());
    			htvdto.setFamt(dto.getFamt());
    			return commonDataAccessService.findRsByDtoWithWherePaging(htvdto,
    					pageRequest, "1=1");
    		}
			
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return null;
	}
    
    private void init(){
    	this.statelist = new ArrayList<TdEnumvalueDto>();

		TdEnumvalueDto valuedto3 = new TdEnumvalueDto();
		valuedto3.setStypecode("��ǰ��");
		valuedto3.setSvalue("0");
		this.statelist.add(valuedto3);
		
		TdEnumvalueDto valuedto2 = new TdEnumvalueDto();
		valuedto2.setStypecode("��ʷ��");
		valuedto2.setSvalue("1");
		this.statelist.add(valuedto2);
    }

    

	public String getSelectedtable() {
		return selectedtable;
	}

	public void setSelectedtable(String selectedtable) {
		this.selectedtable = selectedtable;
	}

	public List<TdEnumvalueDto> getStatelist() {
		return statelist;
	}

	public void setStatelist(List<TdEnumvalueDto> statelist) {
		this.statelist = statelist;
	}

	public List<TvDwbkReportDto> getReportRs() {
		return reportRs;
	}

	public void setReportRs(List<TvDwbkReportDto> reportRs) {
		this.reportRs = reportRs;
	}

	public Map<String, Comparable> getReportmap() {
		return reportmap;
	}

	public void setReportmap(Map<String, Comparable> reportmap) {
		this.reportmap = reportmap;
	}

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public List<IDto> getSelectRs() {
		return selectRs;
	}

	public void setSelectRs(List<IDto> selectRs) {
		this.selectRs = selectRs;
	}

	public List getAllList() {
		return allList;
	}

	public void setAllList(List allList) {
		this.allList = allList;
	}
    

}
package com.cfcc.itfe.client.recbiz.voucherattachservice;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.client.recbiz.bankvouchervalidate.BankVoucherValidateBean;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.PayReckDetailListDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.recbiz.bankvouchervalidate.IBankVoucherValidateService;
import com.cfcc.itfe.service.sendbiz.exporttbsforbj.IExportTBSForBJService;
import com.cfcc.itfe.util.FileRootPathUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.core.service.filetransfer.support.FileSystemConfig;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * ���ܣ�ƾ֤��������(��ɫͨ��)
 * @author hejianrong
 * @time   14-06-06 14:32:45
 * ��ϵͳ: RecBiz
 * ģ��:VoucherAttachService
 * ���:VoucherAttachService
 */
public class VoucherAttachServiceBean extends AbstractVoucherAttachServiceBean implements IPageDataProvider {
	private IExportTBSForBJService exportTBSForBJService = (IExportTBSForBJService) getService(IExportTBSForBJService.class);
	private IBankVoucherValidateService bankVoucherValidateService = (IBankVoucherValidateService)getService(IBankVoucherValidateService.class);
	private static Log log = LogFactory.getLog(VoucherAttachServiceBean.class);
    private List filePath;
    private ITFELoginInfo loginInfo;
    private String reportPath;
    private List reportRs;
    private Map reportMap;
    private List checklist;
    public VoucherAttachServiceBean() {
      super();
      loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      dto = new TvVoucherinfoDto();
      dto.setSorgcode(loginInfo.getSorgcode());
      dto.setSstyear(TimeFacade.getCurrentStringTime().substring(0, 4));
      dto.setScreatdate(TimeFacade.getCurrentStringTime());
      filePath=new ArrayList<File>();
      pagingcontext = new PagingContext(this);
      checklist = new ArrayList();
      reportPath = "/com/cfcc/itfe/client/ireport/payReckDetailList.jasper";
      reportRs = new ArrayList<PayReckDetailListDto>();
      reportMap = new HashMap<String, String>();
      setDefualtStrecode();//����Ĭ�Ϲ������     
    }
    
    /**
	 * Direction: ����ƾ֤����
	 * ename: sendVoucherAttach
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String sendVoucherAttach(Object o){
    	if (StringUtils.isBlank(dto.getStrecode())) {
			MessageDialog.openMessageDialog(null, "��ѡ�������룡");
			return null;
		}
    	if(findAdmDivCodeByStrecode(dto))
    		return null;
		List<String> serverpathlist = new ArrayList<String>();	
		if (null == filePath || filePath.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص��ļ���");
			return null;
		}
		if (filePath.size() > 1000) {
			MessageDialog.openMessageDialog(null, "��ѡ�����ļ����ܴ���1000����");
			return null;
		}						
		List<File> fileList=new ArrayList<File>();
		try {			
			for (File tmpfile:(List<File>) filePath) {
				if (null == tmpfile || null == tmpfile.getName()|| null == tmpfile.getAbsolutePath()) {					
					MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ���ص��ļ���");
					return null;
				}				
				String serverpath =ClientFileTransferUtil.uploadFile(tmpfile.getAbsolutePath());
				fileList.add(new File(serverpath));
				serverpathlist.add(serverpath);
			}
			for(String fileName:serverpathlist){			
				TvVoucherinfoDto voucherDto=(TvVoucherinfoDto) dto.clone();
				voucherDto.setSfilename(fileName);
				voucherLoadService.sendData(voucherDto);				
			}
			commonDataAccessService.saveTvrecvlog(DeleteServerFileUtil.wipeFileOut
					(serverpathlist, null),MsgConstant.VOUCHER_ATTACH_SEND);				
			MessageDialog.openMessageDialog(null, "�ļ����ͳɹ�,���ι����ͳɹ� "+ fileList.size() + " ���ļ���");			
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);			
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch(Exception e){
			log.error(e);					
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e);
		}
		filePath = new ArrayList();
		this.editor.fireModelChanged();
		return super.sendVoucherAttach(o);
    }
    
    /**
	 * Direction: ����ƾ֤����
	 * ename: recvVoucherAttach
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String recvVoucherAttach(Object o){    	      
        try {
        	List list=new ArrayList();
            if(StringUtils.isBlank(dto.getStrecode())){
            	List<TsTreasuryDto> strecodeList=findStrecodeList();
            	if(strecodeList==null||strecodeList.size()==0){
            		MessageDialog.openMessageDialog(null, "��ǰ��֯���������¹����������δά����");
            		return "";
            	}
				for(TsTreasuryDto stredto: strecodeList){
					TvVoucherinfoDto voucherDto=(TvVoucherinfoDto) dto.clone();
					voucherDto.setStrecode(stredto.getStrecode());
					if(findAdmDivCodeByStrecode(voucherDto))
						return "";
					list.add(voucherDto);
				}
            }else{
            	list.add(dto);
            	if(findAdmDivCodeByStrecode(dto))
					return "";
            }
            int count=0;
        	for(TvVoucherinfoDto dto:(List<TvVoucherinfoDto>)list)
             	count += voucherLoadService.getData(dto);
//        	if(count>0){
        	MessageDialog.openMessageDialog(null, "�ļ����ճɹ������ι����ճɹ� "+ count + " ���ļ���");
        		
//        	}else{
//        		MessageDialog.openMessageDialog(null, "�ļ�����ʧ�ܣ�ƾ֤��δ�����ļ���");
//        	}
        	List<TvVoucherinfoDto> listfile = findFileList();
    		if(loginInfo.getSorgcode().startsWith("06") && listfile!=null && listfile.size()>0){
    			String separator="/";
    			List<File> filelist = new ArrayList<File>();
    			for(TvVoucherinfoDto dto:(List<TvVoucherinfoDto>)listfile){
        			String path = "C:/itfe/"+dto.getSorgcode()+separator+TimeFacade.getCurrentStringTime()+separator;
        			File file = new File(path);
    				if(!file.exists()){
    					file.mkdirs();
    				}
        			ClientFileTransferUtil.downloadFile("VoucherAttach"+separator+dto.getSorgcode()+separator+
    						dto.getStrecode()+separator+dto.getScreatdate()+separator+dto.getSfilename(),
    						  path+dto.getSfilename());
        			filelist.add(new File(path+dto.getSfilename()));
    			}
    			/** �������������ļ��ϴ��������� */
    			List<String> serverPathList = new ArrayList<String>();
    			for (int i=0;i<filelist.size();i++) {
    				File file = filelist.get(i);
    				String absFilePath = file.getAbsolutePath();
    				String serverFilePath = commonDataAccessService.getServerRootPath(absFilePath, loginInfo.getSorgcode());
    				exportTBSForBJService.deleteServerFile(serverFilePath);
    				serverPathList.add(ClientFileTransferUtil.uploadFile(absFilePath));
    			}

    			/** ��ʼ���е��� */
    			MulitTableDto resultDto = bankVoucherValidateService.voucherImport(serverPathList, null);
    			MessageDialog.openMessageDialog(null, new BankVoucherValidateBean().generateMessage(resultDto, serverPathList));
    			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    		}
        	
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);			
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch(Exception e){
			log.error(e);
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e);
		}
        return "";
    }
    
    /**
	 * Direction: ��ѯ 
	 * ename: search
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String search(Object o){
    	checklist = new ArrayList();
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse=new PageResponse();
		pageResponse = retrieve(pageRequest);
		if(pageResponse.getTotalCount()==0){
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼��");
			return "";
		}
		pagingcontext.setPage(pageResponse);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
        return super.search(o);
    }
    
    /**
	 * Direction: �ļ���������
	 * ename: downloadAll
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String downloadAll(Object o){
    	String path = new DirectoryDialog(Display.getCurrent().getActiveShell()).open();		
		if (StringUtils.isBlank(path)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
			return "";
		}
		if(checklist==null||checklist.size()==0){
			MessageDialog.openMessageDialog(null, "��ѡ����Ҫ���ص��ļ���");
			return "";
		}
		String separator=File.separator;
		separator="/";
		try{
			for(TvVoucherinfoDto dto:(List<TvVoucherinfoDto>)checklist){
				ClientFileTransferUtil.downloadFile("VoucherAttach"+separator+dto.getSorgcode()+separator+
						dto.getStrecode()+separator+dto.getScreatdate()+separator+dto.getSfilename(),
						  path+separator+dto.getSfilename());
			}
											
		} catch (FileTransferException e) {
			log.error(e);
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e);
			return "";
		}		
		MessageDialog.openMessageDialog(null, "�ļ����سɹ������ι����سɹ� "+ checklist.size() + " ���ļ���");
        return "";
    }
        
    /**
     * ����Ĭ�Ϲ������
     */
    public void setDefualtStrecode(){
    	TsTreasuryDto tmpdto = new TsTreasuryDto();
    	tmpdto.setSorgcode(loginInfo.getSorgcode());		
		try {
			if(StringUtils.isBlank(dto.getStrecode())){
				List list=commonDataAccessService.findRsByDto(tmpdto);
				if(list!=null||list.size()>0)
					dto.setStrecode(((TsTreasuryDto) list.get(0)).getStrecode());
			}			
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}		
    }
    
    /**
     * ���ҵ�ǰ��֯�������������й������
     * @return
     * @throws ITFEBizException
     */
    private List findStrecodeList() throws ITFEBizException{
    	TsTreasuryDto tmpdto=new TsTreasuryDto();
    	tmpdto.setSorgcode(loginInfo.getSorgcode());
    	return commonDataAccessService.findRsByDto(tmpdto);
    }
    
    /**
     * ������֯�������롢������������Ӧ����������
     * @return
     */
    private Boolean findAdmDivCodeByStrecode(TvVoucherinfoDto dto){
    	TsConvertfinorgDto finorgDto=new TsConvertfinorgDto();
    	finorgDto.setSorgcode(dto.getSorgcode());
    	finorgDto.setStrecode(dto.getStrecode());
    	try {
			List list=commonDataAccessService.findRsByDto(finorgDto);
			if(list==null||list.size()==0){
				MessageDialog.openMessageDialog(null, "�������"+dto.getStrecode()+"��Ӧ�Ĳ����������δά����");
				return true;
			}				
			finorgDto=(TsConvertfinorgDto) list.get(0);
			if(StringUtils.isBlank(finorgDto.getSadmdivcode()))
				MessageDialog.openMessageDialog(null, "��������"+finorgDto.getSfinorgcode()+"��Ӧ�������������δά����");
			else{
				dto.setSadmdivcode(finorgDto.getSadmdivcode());
				return false;
			}			
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "��ѯ������������쳣��");			
		}return true;   	
    }

    
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	PageResponse pageResponse=new PageResponse(pageRequest);
    	List fileList=findFileList();
    	pageResponse.setTotalCount(fileList.size());
    	pageResponse.getData().clear();
    	if(fileList.size()>0){
    		int start=(pageRequest.getPageNum()-1)*pageRequest.getPageSize();
    		int end=pageRequest.getPageNum()*pageRequest.getPageSize()>fileList.size()?fileList.size():
    			pageRequest.getPageNum()*pageRequest.getPageSize();
        	pageResponse.setData(fileList.subList(start,end));
    	}else
    		pageResponse.setData(fileList);   	   	
    	return pageResponse;    	
	}
    
    /**
     * ���ҽ���ƾ֤�����б�
     * @return
     */
    private List findFileList(){    	
    	dto.setSfilename(getRoot());
    	if(StringUtils.isBlank(dto.getStrecode())&&!StringUtils.isBlank(dto.getScreatdate())){
    		List list=new ArrayList();
    		List<TsTreasuryDto> strecodeList=null;
			try {
				strecodeList = findStrecodeList();
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),
						new Exception("��ѯ���������������쳣��"));			
				return list;
			}    		
        	if(strecodeList==null||strecodeList.size()==0){
        		MessageDialog.openMessageDialog(null, "��ǰ��֯���������¹����������δά����");
        		return list;
        	}
        	for(TsTreasuryDto tempdto:strecodeList){				
				TvVoucherinfoDto voucherDto=(TvVoucherinfoDto) dto.clone();	
				voucherDto.setSfilename(dto.getSfilename()+File.separator+
						tempdto.getStrecode()+File.separator+dto.getScreatdate());
        		list.addAll(findFileList(voucherDto));
        	}return list;
    	}
    	if(!StringUtils.isBlank(dto.getStrecode())&&StringUtils.isBlank(dto.getScreatdate()))
    		dto.setSfilename(dto.getSfilename()+File.separator+dto.getStrecode());
    	if(!StringUtils.isBlank(dto.getStrecode())&&!StringUtils.isBlank(dto.getScreatdate()))
    		dto.setSfilename(dto.getSfilename()+File.separator+dto.getStrecode()+File.separator+dto.getScreatdate());    	
    	return findFileList(dto);
    }
    
    /**
     * ���ҽ���ƾ֤�����б�
     * @param dto
     * @return
     */
    private List findFileList(TvVoucherinfoDto dto){
    	String root=getRoot();
    	String separator=File.separator;
    	List<Map<String,String>> fileList = null;
		try {
			fileList = voucherAttachServiceService.getFileList(dto.getSfilename());
		} catch (ITFEBizException e1) {
			log.error(e1);
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),new Exception("�����ļ�·���쳣��"));
		}
		List<TvVoucherinfoDto> list=new ArrayList<TvVoucherinfoDto>();
		if(fileList!=null&&fileList.size()>0){		
			for(Map<String,String> map:fileList){
				try{		
					String fileName = map.get("filepath");
					TvVoucherinfoDto voucherDto=(TvVoucherinfoDto) dto.clone();
					voucherDto.setStrecode(fileName.substring(root.length()+separator.length(),root.length()+separator.length()+10));
					voucherDto.setScreatdate(fileName.substring(root.length()+separator.length()+10+separator.length(),
							root.length()+separator.length()+10+separator.length()+8));
					int lastIndex = fileName.replace("\\", "/").lastIndexOf("/");
					voucherDto.setSfilename(fileName.substring(lastIndex + 1));
					voucherDto.setScheckdate(map.get("edittime"));
					list.add(voucherDto);						
				} catch(Exception e){
					log.error(e);
					MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),new Exception("�����ļ�·���쳣��"));
					return list;
				}
			}		
		}return list;
    }
    
    /**
     * ��ȡ����ƾ֤������·��
     * @return
     */
    private String getRoot(){
    	String root="";
    	try {
			root = commonDataAccessService.getFileRootPath()+"VoucherAttach"+File.separator+dto.getSorgcode();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����ļ���·���쳣��");			
		}
		return root;
    }
    
	public List getFilePath() {
		return filePath;
	}
	public void setFilePath(List filePath) {
		this.filePath = filePath;
	}

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	public List getReportRs() {
		return reportRs;
	}

	public void setReportRs(List reportRs) {
		this.reportRs = reportRs;
	}

	public Map getReportMap() {
		return reportMap;
	}

	public void setReportMap(Map reportMap) {
		this.reportMap = reportMap;
	}
	/**
	 * Direction: ����
	 * ename: goQuery
	 * ���÷���: 
	 * viewers: ����ƾ֤����
	 * messages: 
	 */
	public String goQuery(Object o){
		return super.goQuery(o);
		
	}
	/**
	 * Direction: �����ӡ
	 * ename: reportPrint
	 * ���÷���: 
	 * viewers: �����ӡ
	 * messages: 
	 */
    public String reportPrint(Object o){
		return super.reportPrint(o);
    	
    }

	public List getChecklist() {
		return checklist;
	}

	public void setChecklist(List checklist) {
		this.checklist = checklist;
	}
}
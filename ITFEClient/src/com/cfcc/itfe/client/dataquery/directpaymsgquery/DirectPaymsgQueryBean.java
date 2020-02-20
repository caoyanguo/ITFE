package com.cfcc.itfe.client.dataquery.directpaymsgquery;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.HtfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * ���ܣ�ֱ��֧����ѯ
 * @author hejianrong
 * @time   14-11-17 16:45:25
 * ��ϵͳ: DataQuery
 * ģ��:directPaymsgQuery
 * ���:DirectPaymsgQuery
 */
public class DirectPaymsgQueryBean extends AbstractDirectPaymsgQueryBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(DirectPaymsgQueryBean.class);
   
	/** �����б� */
    private ITFELoginInfo loginfo;
    private TfDirectpaymsgmainDto finddto;
	private String selectedtable;
	private List tableMapperList;
	private IDto idto;

    public DirectPaymsgQueryBean() {
      super();         
      initTableMapperList();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
      finddto=new TfDirectpaymsgmainDto();
      finddto.setSorgcode(loginfo.getSorgcode());
      finddto.setScommitdate(TimeFacade.getCurrentStringTime());
      selectedtable = "0";
      pagingcontextMain = new PagingContext(this);
      pagingcontextSub = new PagingContext(this);
      pagingcontextMainHis = new PagingContext(this);
      pagingcontextSubHis = new PagingContext(this);      
    }
    
	/**
	 * Direction: ��ѯ�б��¼�
	 * ename: searchList
	 * ���÷���: 
	 * viewers: ֱ��֧����Ϣ�б�
	 * messages: 
	 */
    public String searchList(Object o){
    	if(StringUtils.isBlank(selectedtable)){
    		MessageDialog.openMessageDialog(null, "��ѡ��Ҫ��ѯ�ı�!");
    		return "";
    	}
		if(selectedtable.equals("0")){
			idto=finddto;
		}else{
			idto=new HtfDirectpaymsgmainDto();
			try {
				CommonUtil.copyProperties(idto, finddto);
			} catch (IllegalAccessException e) {
				log.error(e);
				MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),
						new Exception("��ʷ��͵�ǰ���ṹ��һ��!",e));
				return "";
			} catch (InvocationTargetException e) {
				log.error(e);
				MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),
						new Exception("��ʷ��͵�ǰ���ṹ��һ��!",e));
				return "";
			}
		}
		if(!init())
			return "";
		if(selectedtable.equals("1"))
			return "ֱ��֧����Ϣ�б�(��ʷ��)";
		return super.searchList(o);
	}
    
    public boolean init(){
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse=new PageResponse();
		pageResponse = retrieve(pageRequest);
		if(pageResponse.getTotalCount()==0){
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼��");
			return false;
		}
		if(idto instanceof TfDirectpaymsgmainDto){
			pagingcontextMain.setPage(pageResponse);
		}else if(idto instanceof HtfDirectpaymsgmainDto){
			pagingcontextMainHis.setPage(pageResponse);
		}else if(idto instanceof TfDirectpaymsgsubDto){
			pagingcontextSub.setPage(pageResponse);
		}else if(idto instanceof HtfDirectpaymsgsubDto){
			pagingcontextSubHis.setPage(pageResponse);
		}		
		editor.fireModelChanged();
		return true;
    }
    
	/**
	 * Direction: ���ز�ѯ����
	 * ename: rebackSearch
	 * ���÷���: 
	 * viewers: ֱ��֧����ѯ����
	 * messages: 
	 */
    public String rebackSearch(Object o){
          return super.rebackSearch(o);
    }

	/**
	 * Direction: ����Ϣ˫���¼�
	 * ename: doubleclickMain
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String doubleclickMain(Object o){
    	if(o instanceof TfDirectpaymsgmainDto){
    		idto=new TfDirectpaymsgsubDto();   		
    		((TfDirectpaymsgsubDto)idto).setIvousrlno(((TfDirectpaymsgmainDto) o).getIvousrlno());
    	}else if(o instanceof HtfDirectpaymsgmainDto){
    		idto=new HtfDirectpaymsgsubDto();   		
    		((HtfDirectpaymsgsubDto)idto).setIvousrlno(((HtfDirectpaymsgmainDto) o).getIvousrlno());
    	}
    	init(); 
        return super.doubleclickMain(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);
    	try {
    		return  commonDataAccessService.findRsByDtoPaging(idto,
					pageRequest, "1=1", " I_VOUSRLNO DESC");   	
    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}	catch (Throwable e) {	
			log.error(e);
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),
					new Exception("��ѯ�����쳣��",e));
		}
		return super.retrieve(pageRequest);
	}
    
    public void initTableMapperList(){
    	Mapper m1=new Mapper("0","��ǰ��");
    	Mapper m2=new Mapper("1","��ʷ��");
    	tableMapperList=new ArrayList<Mapper>();
    	tableMapperList.add(m1);
    	tableMapperList.add(m2);    	
    }

	public TfDirectpaymsgmainDto getFinddto() {
		return finddto;
	}

	public void setFinddto(TfDirectpaymsgmainDto finddto) {
		this.finddto = finddto;
	}

	public String getSelectedtable() {
		return selectedtable;
	}

	public void setSelectedtable(String selectedtable) {
		this.selectedtable = selectedtable;
	}

	public List getTableMapperList() {
		return tableMapperList;
	}

	public void setTableMapperList(List tableMapperList) {
		this.tableMapperList = tableMapperList;
	}

}
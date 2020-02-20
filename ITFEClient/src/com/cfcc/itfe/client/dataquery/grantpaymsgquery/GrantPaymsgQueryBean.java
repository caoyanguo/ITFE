package com.cfcc.itfe.client.dataquery.grantpaymsgquery;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.lang.reflect.InvocationTargetException;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtfGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.HtfGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfGrantpaymsgsubDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;

/**
 * codecomment: 
 * @author db2itfe
 * @time   15-05-12 17:14:47
 * ��ϵͳ: DataQuery
 * ģ��:grantPaymsgQuery
 * ���:GrantPaymsgQuery
 */
public class GrantPaymsgQueryBean extends AbstractGrantPaymsgQueryBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(GrantPaymsgQueryBean.class);
    /** �����б� */
    private ITFELoginInfo loginfo;
    private TfGrantpaymsgmainDto finddto;
	private String selectedtable;
	private List tableMapperList;
	private IDto idto;
    public GrantPaymsgQueryBean() {
      super();
      initTableMapperList();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
      finddto=new TfGrantpaymsgmainDto();
      finddto.setSorgcode(loginfo.getSorgcode());
      finddto.setScommitdate(TimeFacade.getCurrentStringTime());
      selectedtable = "0";
      pagingcontextMain = new PagingContext(this);
      pagingcontextSub = new PagingContext(this);
      pagingcontextMainHis = new PagingContext(this);
      pagingcontextSubHis = new PagingContext(this);
                  
    }
    
	/**
	 * Direction: ��ѯ
	 * ename: search
	 * ���÷���: 
	 * viewers: ��Ȩ֧����Ϣ�б�
	 * messages: 
	 */
    public String search(Object o){
    	if(StringUtils.isBlank(selectedtable)){
    		MessageDialog.openMessageDialog(null, "��ѡ��Ҫ��ѯ�ı�!");
    		return "";
    	}
		if(selectedtable.equals("0")){
			idto=finddto;
		}else{
			idto=new HtfGrantpaymsgmainDto();
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
			return "��Ȩ֧����Ϣ�б�(��ʷ��)";
          return super.search(o);
    }

	/**
	 * Direction: ����
	 * ename: reback
	 * ���÷���: 
	 * viewers: ��Ȩ֧����ѯ����
	 * messages: 
	 */
    public String reback(Object o){
          return super.reback(o);
    }

	/**
	 * Direction: ����Ϣ˫���¼�
	 * ename: doubleclickMain
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String doubleclickMain(Object o){
    	if(o instanceof TfGrantpaymsgmainDto){
    		idto=new TfGrantpaymsgsubDto();   		
    		((TfGrantpaymsgsubDto)idto).setIvousrlno(((TfGrantpaymsgmainDto) o).getIvousrlno());
    	}else if(o instanceof HtfGrantpaymsgmainDto){
    		idto=new HtfGrantpaymsgsubDto();   		
    		((HtfGrantpaymsgsubDto)idto).setIvousrlno(((HtfGrantpaymsgmainDto) o).getIvousrlno());
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
    
    public boolean init(){
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse=new PageResponse();
		pageResponse = retrieve(pageRequest);
		if(pageResponse.getTotalCount()==0){
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼��");
			return false;
		}
		if(idto instanceof TfGrantpaymsgmainDto){
			pagingcontextMain.setPage(pageResponse);
		}else if(idto instanceof HtfGrantpaymsgmainDto){
			pagingcontextMainHis.setPage(pageResponse);
		}else if(idto instanceof TfGrantpaymsgsubDto){
			pagingcontextSub.setPage(pageResponse);
		}else if(idto instanceof HtfGrantpaymsgsubDto){
			pagingcontextSubHis.setPage(pageResponse);
		}        
		editor.fireModelChanged();
		return true;
    }

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		GrantPaymsgQueryBean.log = log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public TfGrantpaymsgmainDto getFinddto() {
		return finddto;
	}

	public void setFinddto(TfGrantpaymsgmainDto finddto) {
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

	public IDto getIdto() {
		return idto;
	}

	public void setIdto(IDto idto) {
		this.idto = idto;
	}

    
}
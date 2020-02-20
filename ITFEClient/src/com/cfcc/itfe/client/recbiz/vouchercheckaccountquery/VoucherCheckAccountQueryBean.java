package com.cfcc.itfe.client.recbiz.vouchercheckaccountquery;

import itferesourcepackage.CompareVoucherTypeBankEnumFactory;
import itferesourcepackage.VoucherCheckAccountEnumFactory;
import itferesourcepackage.VoucherCheckFinanceAccountQueryEnumFactory;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositevoucherCheckAccountQuery;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.pk.TvVoucherinfoPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * codecomment: 
 * @author db2admin
 * @time   13-09-03 11:12:28
 * ��ϵͳ: SendBiz
 * ģ��:createVoucherForReport
 * ���:CreateVoucherForReport
 */
public class VoucherCheckAccountQueryBean extends AbstractVoucherCheckAccountQueryBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(VoucherCheckAccountQueryBean.class);
    private ITFELoginInfo loginfo;
    private String voucherType = null;
    private String subVoucherType=null;
    public List voucherList;
    private List subVoucherList;
    List<TvVoucherinfoDto> checkList=null; 
    public VoucherCheckAccountQueryBean() {
    	super();
        pagingcontext = new PagingContext(this);  
        dto = new TvVoucherinfoDto();
        dto.setScreatdate(TimeFacade.getCurrentStringTime());
        dto.setScheckdate(TimeFacade.getCurrentStringTime());
        loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    	checkList=new ArrayList();
    	voucherList = new VoucherCheckAccountEnumFactory().getEnums(null);
    }	
    
    /**
     * ��ȡOCX�ؼ�url
     * @return
     */
    public String getOcxVoucherServerURL(){
    	String ls_URL = "";
    	try {
    		ls_URL = voucherLoadService.getOCXServerURL();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("��ȡOCX�ؼ�URL��ַ���������쳣��",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
		}
		return ls_URL;
    }
    
    /**
     * ��ȡǩ�·����ַ
     * @return
     */
    public String getOCXStampServerURL(){
    	String ls_URL = "";
    	try {
    		ls_URL = voucherLoadService.getOCXStampServerURL();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("��ȡOCX�ؼ�ǩ�·���URL��ַ���������쳣��",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
		}
		return ls_URL;
    }
    
    
	/**
	 * Direction: ��ѯ
	 * ename: search
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String search(Object o){
    	refreshTable();
        return super.search(o);
    }
    
    private void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse=new PageResponse();
		pageResponse = retrieve(pageRequest);
		if(pageResponse.getTotalCount()==0){
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼��");
		}
		pagingcontext.setPage(pageResponse);
	}
    
    /**
	 * Direction: ȫѡ
	 * ename: selectAll
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectAll(Object o){
    	 if(checkList==null||checkList.size()==0){
         	checkList = new ArrayList();
         	checkList.addAll(pagingcontext.getPage().getData());
         }
         else
         	checkList.clear();
         this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
         return super.selectAll(o);
    }
    
  

	/**
	 * Direction: ƾ֤�鿴
	 * ename: voucherView
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherView(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�鿴�ļ�¼��");
    		return "";
    	}
    	try{
        	ActiveXCompositevoucherCheckAccountQuery.init(0);
        	
    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			return "";
		}catch(Exception e){
    		log.error(e);
    		Exception e1=new Exception("ƾ֤�鿴�쳣��",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			return "";
    	}
        return super.voucherView(o);
    }
    
    
    /**
	 * Direction: ��ѯƾ֤��ӡ����
	 * ename: queryVoucherPrintCount
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String queryVoucherPrintCount(TvVoucherinfoDto vDto){
    	String err=null;
    	try {
			err=voucherLoadService.queryVoucherPrintCount(vDto);
			
		} catch (ITFEBizException e) {			
			log.error(e);
			return "��ѯ�쳣";
		}
    	return err;
    }
    
    /**
	 * Direction: ��ѯƾ֤����
	 * ename: queryVoucherPrintCount
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public int queryVoucherJOintCount(TvVoucherinfoDto vDto){
    	int count=0;
    	TsVouchercommitautoDto tDto=new TsVouchercommitautoDto();
    	tDto.setSorgcode(vDto.getSorgcode());
    	tDto.setStrecode(vDto.getStrecode());
    	tDto.setSvtcode(vDto.getSvtcode());
    	try {
			List<TsVouchercommitautoDto> list= (List) commonDataAccessService.findRsByDto(tDto);
			if(list==null||list.size()==0)
				return -1;
			tDto=list.get(0);
			if(tDto.getSjointcount()==null){
				return -1;
			}			
		} catch (ITFEBizException e) {
			log.error(e);
			return -2;
		}catch(Exception e){
			log.error(e);
			return -1;
		}
    	return tDto.getSjointcount();
    }
    
    public String getVoucherXMl(TvVoucherinfoDto vDto) throws ITFEBizException{
   	    return voucherLoadService.voucherStampXml(vDto);
    }

    public void refreshTable(){
    	init();
		checkList.clear();		
		editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
    }
    
        /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);
    	PageResponse page = null;
    	StringBuffer wheresql=new StringBuffer();
    	wheresql.append(" 1=1 ");
    	if(voucherType!=null&&!voucherType.equals("")){
    		dto.setSvtcode(voucherType);
    	}
    	if(dto.getSvtcode()==null||dto.getSvtcode().equals("")){
        	List voucherList = new VoucherCheckAccountEnumFactory().getEnums(null);
        	wheresql.append("AND ( ");
        	for(int i=0;i<voucherList.size();i++){
        		Mapper mapper=(Mapper)voucherList.get(i);
        		if(i==voucherList.size()-1){
            		wheresql.append(" S_VTCODE = '"+ mapper.getUnderlyValue()+"' )");
            		break;
        		}            		
        		wheresql.append(" S_VTCODE = '"+ mapper.getUnderlyValue()+"' OR ");
        	}       	
    	}
		dto.setScheckvouchertype(subVoucherType);
    	try {
    		page =  commonDataAccessService.findRsByDtoPaging(dto, pageRequest, wheresql.toString(), " TS_SYSUPDATE desc");
    	
    		return page;
    				
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			
		}catch (Throwable e) {
			log.error(e);
			Exception e1=new Exception("ƾ֤��ѯ�����쳣��",e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			
		}
		return super.retrieve(pageRequest);
	}
    public TvVoucherinfoDto getDto(TvVoucherinfoDto dto) throws ITFEBizException{
    	TvVoucherinfoPK pk=new TvVoucherinfoPK();
    	pk.setSdealno(dto.getSdealno());    	   	
    	return (TvVoucherinfoDto) commonDataAccessService.find(pk);
    }
    
	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public List getSubVoucherList() {
		return subVoucherList;
	}

	public void setSubVoucherList(List subVoucherList) {
		this.subVoucherList = subVoucherList;
	}

	public List getVoucherList() {
		return voucherList;
	}

	public void setVoucherList(List voucherList) {
		this.voucherList = voucherList;
	}
	public String getVoucherType() {
		return voucherType;
	}
	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
		if(voucherType.equals(MsgConstant.VOUCHER_NO_5502))
			subVoucherList =new VoucherCheckFinanceAccountQueryEnumFactory().getEnums(null);
		else if(voucherType.equals(MsgConstant.VOUCHER_NO_2502))
			subVoucherList =new CompareVoucherTypeBankEnumFactory().getEnums(null);
		subVoucherType=null;
	}

	public List<TvVoucherinfoDto> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<TvVoucherinfoDto> checkList) {
		this.checkList = checkList;
	}

	public String getSubVoucherType() {
		return subVoucherType;
	}

	public void setSubVoucherType(String subVoucherType) {
		this.subVoucherType = subVoucherType;
	}

}
package com.cfcc.itfe.client.dataquery.paybankrefundquery;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.HtfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
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
 * ���ܣ��տ������˿�֪ͨ��ѯ
 * @author hejianrong
 * @time   14-11-16 14:03:08
 * ��ϵͳ: DataQuery
 * ģ��:directPaymentQuery
 * ���:DirectPaymentQuery
 */
public class PayBankRefundQueryBean extends AbstractPayBankRefundQueryBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(PayBankRefundQueryBean.class);
   
	/** �����б� */
    private ITFELoginInfo loginfo;
    private TfPaybankRefundmainDto finddto;
	private String selectedtable;
	private List tableMapperList;
	private IDto idto;

    public PayBankRefundQueryBean() {
      super();         
      initTableMapperList();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
      finddto=new TfPaybankRefundmainDto();
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
			idto=new HtfPaybankRefundmainDto();
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
			return "�տ������˿�֪ͨ��Ϣ�б�(��ʷ��)";
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
		if(idto instanceof TfPaybankRefundmainDto){
			pagingcontextMain.setPage(pageResponse);
		}else if(idto instanceof HtfPaybankRefundmainDto){
			pagingcontextMainHis.setPage(pageResponse);
		}else if(idto instanceof TfPaybankRefundsubDto){
			pagingcontextSub.setPage(pageResponse);
		}else if(idto instanceof HtfPaybankRefundsubDto){
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
    	if(o instanceof TfPaybankRefundmainDto){
    		idto=new TfPaybankRefundsubDto();   		
    		((TfPaybankRefundsubDto)idto).setIvousrlno(((TfPaybankRefundmainDto) o).getIvousrlno());
    	}else if(o instanceof HtfPaybankRefundmainDto){
    		idto=new HtfPaybankRefundsubDto();   		
    		((HtfPaybankRefundsubDto)idto).setIvousrlno(((HtfPaybankRefundmainDto) o).getIvousrlno());
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

	public TfPaybankRefundmainDto getFinddto() {
		return finddto;
	}

	public void setFinddto(TfPaybankRefundmainDto finddto) {
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
package com.cfcc.itfe.client.dataquery.tvincomedetailreportcheck;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsSyslogDto;
import com.cfcc.itfe.persistence.dto.TvIncomeDetailReportCheckDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author hjr
 * @time   14-02-26 15:26:35
 * ��ϵͳ: DataQuery
 * ģ��:tvIncomeDetailReportCheck
 * ���:TvIncomeDetailReportCheck
 */
public class TvIncomeDetailReportCheckBean extends AbstractTvIncomeDetailReportCheckBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TvIncomeDetailReportCheckBean.class);
    private ITFELoginInfo loginInfo;
    private TsSyslogDto syslogdto;
    PagingContext logpagingcontext = null;
    public TvIncomeDetailReportCheckBean() {
      super();
      loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      dto = new TvIncomeDetailReportCheckDto(); 
      syslogdto = new TsSyslogDto();
      dto.setSorgcode(loginInfo.getSorgcode());
      dto.setScreatdate(TimeFacade.getCurrentStringTimebefor());
      logpagingcontext = new PagingContext(this);
      pagingcontext = new PagingContext(this);
      init();
    }
    
	// ��ʼ����ʾ��ϸ��¼
	private void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}
    
	/**
	 * Direction: ��ѯ
	 * ename: search
	 * ���÷���: 
	 * viewers: �����������ˮ�˶Խ���
	 * messages: 
	 */
    public String search(Object o){
		refreshTable();
		return super.search(o);
    }

	/**
	 * Direction: �˶�
	 * ename: incomeDetailReportCheck
	 * ���÷���: 
	 * viewers: �����������ˮ�˶Խ���
	 * messages: 
	 */
    public String incomeDetailReportCheck(Object o){
    		if(dto.getScreatdate()==null){
    			MessageDialog.openMessageDialog(null, "�����뱨�����ڣ�");
    			return null;
    		}
    		try {
				String result=tvIncomeDetailReportCheckService.incomeDetailReportCheck(dto);
				if(result.length()==0){
					List list=commonDataAccessService.findRsByDto(dto);
					if(list==null||list.size()==0){
						MessageDialog.openMessageDialog(null, "����������Ҫ�˶Ե����ݣ�");						
						init();
						editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
						return "";
					}else
						MessageDialog.openMessageDialog(null, "�����������ˮ�˶���ɣ�");				
				}else					
					MessageDialog.openMessageDialog(null, result);					
				refreshTable();			
			} catch (ITFEBizException e) {
				log.error(e);		
				MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e);
			}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
				log.error(e);
				MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			}catch(Throwable e){
				log.error(e);	
				MessageDialog.openErrorDialog(
						Display.getDefault().getActiveShell(),new Exception("���������쳣��",e));
			}		
          return super.incomeDetailReportCheck(o);
    }
    
	/**
	 * Direction: ��ѯ��־
	 * ename: querysyslog
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String querysyslog(Object o){
    	if(StringUtils.isBlank(syslogdto.getSdate())){
    		MessageDialog.openMessageDialog(null, "�����뱨�����ڣ�");
			return null;
    	}
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrievelog(pageRequest);
		if(pageResponse.getTotalCount()==0)
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼��");
		logpagingcontext.setPage(pageResponse);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.querysyslog(o);
    }
    
    public void refreshTable(){
		PageResponse pageResponse=new PageResponse();
		pageResponse = retrieve(new PageRequest());
		if(pageResponse.getTotalCount()==0)
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼�����Ⱥ˶����ݣ�");
		pagingcontext.setPage(pageResponse);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);    
    	try {
    		return commonDataAccessService.findRsByDtoPaging(dto,
					pageRequest, "1=1", "TS_SYSUPDATE DESC");
    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}	catch (Throwable e) {	
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),new Exception("��ѯ�����쳣��",e));
		}
		return super.retrieve(pageRequest);
	}
    
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrievelog(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);    
    	String sqlwhere = "1=1";
    	if(StringUtils.isBlank(syslogdto.getSoperationtypecode())){
    		sqlwhere += " and S_OPERATIONTYPECODE in ('3128','3129','3139')";
    	}
    	try {
    		return commonDataAccessService.findRsByDtoPaging(syslogdto,
					pageRequest, sqlwhere, "S_TIME DESC");
    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}	catch (Throwable e) {	
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),new Exception("��ѯ�����쳣��",e));
		}
		return super.retrieve(pageRequest);
	}

	public TsSyslogDto getSyslogdto() {
		return syslogdto;
	}

	public void setSyslogdto(TsSyslogDto syslogdto) {
		this.syslogdto = syslogdto;
	}

	public PagingContext getLogpagingcontext() {
		return logpagingcontext;
	}

	public void setLogpagingcontext(PagingContext logpagingcontext) {
		this.logpagingcontext = logpagingcontext;
	}

}
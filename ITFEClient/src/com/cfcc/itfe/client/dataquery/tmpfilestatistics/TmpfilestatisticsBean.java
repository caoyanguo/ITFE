package com.cfcc.itfe.client.dataquery.tmpfilestatistics;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;
import org.eclipse.swt.SWT;

import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.dataquery.tmpfilestatistics.ITmpfilestatisticsService;

/**
 * codecomment: 
 * @author Administrator
 * @time   12-05-16 16:26:36
 * ��ϵͳ: DataQuery
 * ģ��:tmpfilestatistics
 * ���:Tmpfilestatistics
 */
public class TmpfilestatisticsBean extends AbstractTmpfilestatisticsBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TmpfilestatisticsBean.class);
    private ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
    private ITFELoginInfo loginfo;
    public TmpfilestatisticsBean() {
      super();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
      starttime = loginfo.getCurrentDate().replaceAll("-", "");
      endtime = loginfo.getCurrentDate().replaceAll("-", "");
      orgcode = loginfo.getSorgcode();
      trecode = null;
      trecodelist = new ArrayList();
      reportpath = "/com/cfcc/itfe/client/ireport/statistics.jasper";
      reportlist = new ArrayList();
      reportmap = new HashMap();
      init();
                  
    }
    
	private void init() {
		TsTreasuryDto tredto = new TsTreasuryDto();
		tredto.setSorgcode(loginfo.getSorgcode());
		try {
			trecodelist = commonDataAccessService.findRsByDto(tredto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
	}

	/**
	 * Direction: ��Ϣ��ѯ
	 * ename: searchDto
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String searchDto(Object o){
    	reportlist = new ArrayList();
    	try {
    		String sql = null;
    		StringBuffer trecodebuf = new StringBuffer();
    		if(null == trecode || trecode.length() == 0){
    			trecodebuf.append(" and (S_TRECODE in (");
    			for(Object obj : trecodelist){
    				TsTreasuryDto dto = (TsTreasuryDto) obj;
    				trecodebuf.append("'").append(dto.getStrecode()).append("',");
    			}
    			sql = trecodebuf.toString().substring(0, trecodebuf.toString().length() - 1) + "))";
    		}else{
    			sql = " and (S_TRECODE = '" + trecode + "') ";
    		}
    		DisplayCursor.setCursor(SWT.CURSOR_WAIT);
    		reportlist = tmpfilestatisticsService.getlist(starttime, endtime,sql);
    		DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			if(null == reportlist || reportlist.size() == 0){
				reportlist = new ArrayList();
				MessageDialog.openMessageDialog(null, "û�з������������ݣ�");
			}
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		} catch (Throwable e) {
			log.error("��ѯ���ݿ�ʧ�ܣ�");
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
          return super.searchDto(o);
    }

	/**
	 * Direction: ��ӡ��Ϣ
	 * ename: toReport
	 * ���÷���: 
	 * viewers: ��ӡ���
	 * messages: 
	 */
    public String toReport(Object o){
    	if(reportlist == null || reportlist.size() == 0 ){
    		MessageDialog.openMessageDialog(null, "û����Ҫ��ӡ�����ݣ�");
    		return null;
    	}
          return super.toReport(o);
    }

	/**
	 * Direction: ���ز�ѯ����
	 * ename: toback
	 * ���÷���: 
	 * viewers: ��ѯ��Ϣ
	 * messages: 
	 */
    public String toback(Object o){
          return super.toback(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}
    
    

}
package com.cfcc.itfe.client.dataquery.selectrecord1;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;
import org.eclipse.core.commands.Command;
import org.eclipse.swt.widgets.Display;

import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.dataquery.selectrecord1.ISelectRecordGDService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;

/**
 * codecomment: 
 * @author db2admin
 * @time   16-01-14 09:09:46
 * ��ϵͳ: DataQuery
 * ģ��:selectRecord1
 * ���:SelectRecordGD
 */
public class SelectRecordGDBean extends AbstractSelectRecordGDBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(SelectRecordGDBean.class);
    // �û���¼��Ϣ
	private ITFELoginInfo loginInfo;
	
	private String reportPath = "com/cfcc/itfe/client/ireport/guangdongOnerReport.jasper";
	private PagingContext pagingcontext = null;
	private List reportlist = null;
	private Map reportmap = null;

	public SelectRecordGDBean() {
      super();
      searchDto = new TvPayreckBankDto();
      loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();         
      resultList = new ArrayList();
      pagingcontext = new PagingContext(this);
      reportlist = new ArrayList();
      reportmap = new HashMap();
    }
    
	/**
	 * Direction: ��ѯ
	 * ename: queryInfo
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String queryInfo(Object o){
    	if (searchDto.getSofyear() == null || searchDto.getSofyear().equals("")) {
			MessageDialog.openMessageDialog(null, "������Ҫ��ѯ����ȣ�");
			return "";
		}
    	if ((searchDto.getSpayername()==null||searchDto.getSpayername().equals(""))&&(searchDto.getSagentacctbankname()==null||searchDto.getSagentacctbankname().equals(""))&&(searchDto.getSdescription()==null||searchDto.getSdescription().equals(""))) {
			MessageDialog.openMessageDialog(null, "���������ơ��տ����С���;����ͬʱΪ�գ�");
			return "";
		}
    	 reportlist.clear();
         reportmap.clear();
    	//������ ¼�� ������ ����
    	String ls_SelectName = "";
    	String ls_And  = "";
    	String ls_GroupBy = "";
    	if(!(searchDto.getSpayername()==null||searchDto.getSpayername().equals(""))){
    		ls_SelectName = ls_SelectName +",S_PAYERNAME ";
    		ls_And = ls_And +" and S_PAYERNAME = '"+searchDto.getSpayername()+"'";
    		ls_GroupBy = ls_GroupBy + ",S_PAYERNAME";
    	}
    	if(!(searchDto.getSagentacctbankname()==null||searchDto.getSagentacctbankname().equals(""))){
    		ls_SelectName = ls_SelectName +",S_AGENTACCTBANKNAME ";
    		ls_And = ls_And +" and S_AGENTACCTBANKNAME = '"+searchDto.getSagentacctbankname()+"'";
    		ls_GroupBy = ls_GroupBy + ",S_AGENTACCTBANKNAME";
    	}
    	if(!(searchDto.getSdescription()==null||searchDto.getSdescription().equals(""))){
    		ls_SelectName = ls_SelectName +",S_DESCRIPTION ";
    		ls_And = ls_And +" and S_DESCRIPTION = '"+searchDto.getSdescription()+"'";
    		ls_GroupBy = ls_GroupBy + ",S_DESCRIPTION";
    	}
    	String ls_SQL  = "select to_char(month(D_ENTRUSTDATE)) AS S_FINORGCODE,sum(F_AMT) AS F_AMT,count AS I_STATINFNUM "+ls_SelectName+" from (SELECT S_BOOKORGCODE,D_ENTRUSTDATE,S_PAYERNAME,S_AGENTACCTBANKNAME,S_DESCRIPTION,F_AMT,S_OFYEAR,S_RESULT FROM TV_PAYRECK_BANK UNION SELECT S_BOOKORGCODE,D_ENTRUSTDATE,S_PAYERNAME,S_AGENTACCTBANKNAME,S_DESCRIPTION,F_AMT,S_OFYEAR,S_RESULT  FROM HTV_PAYRECK_BANK)  where S_BOOKORGCODE = '"+loginInfo.getSorgcode()+"' and S_RESULT = '"+DealCodeConstants.DEALCODE_ITFE_SUCCESS+"' and  S_OFYEAR = '"+searchDto.getSofyear()+"' "+ls_And+" GROUP  BY month(D_ENTRUSTDATE) "+ls_GroupBy+" ORDER BY month(D_ENTRUSTDATE) ";
    	try {
    		resultList = selectRecordGDService.selectGroupResultByCondition(ls_SQL);
    		if(resultList.size()>0){
    			reportlist.addAll(resultList);
    			//��resultList��� һ�����Ϊ���� �����Ļ���
    			Integer allCount  = 0;
    			BigDecimal allMoney = new BigDecimal(0.00);
    			for(int i=0;i<resultList.size();i++){
    				allMoney = allMoney.add(((TvPayreckBankDto)resultList.get(i)).getFamt());
    				allCount+=((TvPayreckBankDto)resultList.get(i)).getIstatinfnum();
    			}
    			TvPayreckBankDto dto = new TvPayreckBankDto();
    			dto.setSfinorgcode("ȫ��ϼ�");
    			dto.setFamt(allMoney);
    			dto.setIstatinfnum(allCount);
    			resultList.add(dto);
    			reportmap.put("SUM_AMT", allMoney);
    			reportmap.put("SUM_COUNT", allCount);
    		}else{
    			MessageDialog.openMessageDialog(null, "û�з������������� ��");
    		}
		} catch (ITFEBizException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
    	return super.queryInfo(o);
    }

	/**
	 * Direction: ��ӡ
	 * ename: queryPrint
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String queryPrint(Object o){
    	if(reportlist == null || reportlist.size() == 0 ){
    		MessageDialog.openMessageDialog(null, "û����Ҫ��ӡ�����ݣ����Ƚ��в�ѯ��");
    		return null;
    	}
          return super.queryPrint(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);
		PageResponse page = null;
		try {
			page = commonDataAccessService.findRsByDtoPaging(searchDto, pageRequest,
					"", "");
			return page;

		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Throwable e) {
			log.error(e);
			Exception e1 = new Exception("��ѯ�����쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return super.retrieve(pageRequest);
	}
   
	public ITFELoginInfo getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(ITFELoginInfo loginInfo) {
		this.loginInfo = loginInfo;
	}
	public PagingContext getPagingcontext() {
		return pagingcontext;
	}

	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
	}

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	public List getReportlist() {
		return reportlist;
	}

	public void setReportlist(List reportlist) {
		this.reportlist = reportlist;
	}

	public Map getReportmap() {
		return reportmap;
	}

	public void setReportmap(Map reportmap) {
		this.reportmap = reportmap;
	}
	
}
package com.cfcc.itfe.client.dataquery.selectrecord3;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.dataquery.selectrecord1.ISelectRecordGDService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;

/**
 * codecomment: 
 * @author db2admin
 * @time   16-01-19 13:46:13
 * ��ϵͳ: DataQuery
 * ģ��:selectRecord3
 * ���:SelectRecordThreeGD
 */
public class SelectRecordThreeGDBean extends AbstractSelectRecordThreeGDBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(SelectRecordThreeGDBean.class);
 // �û���¼��Ϣ
	private ITFELoginInfo loginInfo;
	
	private TvPayreckBankListDto searchListDto; 
	private List resultList;
	private String reportPath = "com/cfcc/itfe/client/ireport/guangdongThreeReport.jasper";
	private List reportlist = null;
	private Map reportmap = null;
    public SelectRecordThreeGDBean() {
      super();
      loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();    
      searchDto = new TvPayreckBankDto();
      searchListDto = new TvPayreckBankListDto();
      pagingcontext = new PagingContext(this);
      resultList = new ArrayList();
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
    	if (searchDto.getSagentacctbankname() == null || searchDto.getSagentacctbankname().equals("")) {
			MessageDialog.openMessageDialog(null, "������Ҫ��ѯ�Ŀ������У�");
			return "";
		}
    	//������ ¼�� ������ ����
    	String ls_And  = "";
    	if(!(searchDto.getSpaymode()==null||searchDto.getSpaymode().equals(""))){
    		ls_And = ls_And +" and S_PAYMODE = '"+searchDto.getSpaymode()+"' ";
    	}
    	if(!(searchListDto.getSsupdepname()==null||searchListDto.getSsupdepname().equals(""))){
    		ls_And = ls_And +" and S_SUPDEPNAME = '"+searchListDto.getSsupdepname()+"' ";
    	}
    	if(!(searchListDto.getSexpfuncname()==null||searchListDto.getSexpfuncname().equals(""))){
    		ls_And = ls_And +" and S_EXPFUNCNAME = '"+searchListDto.getSexpfuncname()+"' ";
    	}
    	/*
    	 * /ls_SQL��ϸ�е�Ԥ�㵥λ���롢Ԥ�㵥λ���ơ�Ԥ���Ŀ���롢Ԥ���Ŀ���Ʒֱ���TvPayreckBankDto�е��ֶΡ��������˺š����������ơ��տ����˺š��տ������ơ�����
    		/S_BDGORGCODE as S_PAYERACCT,S_SUPDEPNAME as S_PAYERNAME,S_FUNCBDGSBTCODE as S_PAYEEACCT,S_EXPFUNCNAME as S_PAYEENAME
    	 */
    	String ls_SQL  = "SELECT month(D_ENTRUSTDATE) AS S_FINORGCODE,S_AGENTACCTBANKNAME,S_PAYMODE,S_BDGORGCODE as S_PAYERACCT,S_SUPDEPNAME as S_PAYERNAME,S_FUNCBDGSBTCODE as S_PAYEEACCT,S_EXPFUNCNAME as S_PAYEENAME,sum(F_AMT) AS F_AMT " +
				"FROM (SELECT a.S_OFYEAR,a.D_ENTRUSTDATE,a.S_BOOKORGCODE,a.S_AGENTACCTBANKNAME,a.S_PAYMODE,b.S_BDGORGCODE,b.S_SUPDEPNAME,b.S_FUNCBDGSBTCODE,b.S_EXPFUNCNAME,b.F_AMT " +
    				   "FROM TV_PAYRECK_BANK a ,TV_PAYRECK_BANK_LIST b " +
    				   "WHERE a.S_RESULT = '"+DealCodeConstants.DEALCODE_ITFE_SUCCESS+"' and a.I_VOUSRLNO =  b.I_VOUSRLNO " +
    				   "UNION " +
    				   "SELECT c.S_OFYEAR,c.D_ENTRUSTDATE,c.S_BOOKORGCODE,c.S_AGENTACCTBANKNAME,c.S_PAYMODE,d.S_BDGORGCODE,d.S_SUPDEPNAME,d.S_FUNCBDGSBTCODE,d.S_EXPFUNCNAME,d.F_AMT " +
    				   "FROM hTV_PAYRECK_BANK c ,hTV_PAYRECK_BANK_LIST d " +
    				   "WHERE c.S_RESULT = '"+DealCodeConstants.DEALCODE_ITFE_SUCCESS+"' and c.I_VOUSRLNO =  d.I_VOUSRLNO)  " +
    			"WHERE S_BOOKORGCODE = '"+loginInfo.getSorgcode()+"' and  S_OFYEAR = '"+searchDto.getSofyear()+"' and S_AGENTACCTBANKNAME = '"+searchDto.getSagentacctbankname()+"' " +
    			ls_And+
    			"GROUP  BY month(D_ENTRUSTDATE) ,S_AGENTACCTBANKNAME,S_PAYMODE,S_BDGORGCODE,S_SUPDEPNAME,S_FUNCBDGSBTCODE,S_EXPFUNCNAME " +
    			"ORDER BY month(D_ENTRUSTDATE) ";
    	try {
    		resultList = selectRecordGDService.selectGroupResultByCondition(ls_SQL);
    		Map<String,String> map = new HashMap();
    		map.put("0", "ֱ��֧��");
    		map.put("1", "��Ȩ֧��");
    		if(resultList.size()>0){
    			reportlist.addAll(resultList);
        		//��resultList��� һ�����Ϊ���� �����Ļ���
        		BigDecimal allMoney = new BigDecimal(0.00);
        		for(int i=0;i<resultList.size();i++){
        			allMoney = allMoney.add(((TvPayreckBankDto)resultList.get(i)).getFamt());
        			TvPayreckBankDto dto = ((TvPayreckBankDto)resultList.get(i));
        			dto.setSpaymode(map.get(dto.getSpaymode()));
        		}
        		TvPayreckBankDto dto = new TvPayreckBankDto();
        		dto.setSfinorgcode("ȫ��ϼ�");
        		dto.setFamt(allMoney);
        		dto.setSpaymode("");
        		resultList.add(dto);
        		reportmap.put("SUM_AMT", allMoney);
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
    		MessageDialog.openMessageDialog(null, "û����Ҫ��ӡ������,���Ƚ��в�ѯ��");
    		return null;
    	}
          return super.queryPrint(o);
    }

	/**
	 * Direction: ����
	 * ename: backQuery
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String backQuery(Object o){
          return super.backQuery(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}
    public TvPayreckBankListDto getSearchListDto() {
		return searchListDto;
	}

	public void setSearchListDto(TvPayreckBankListDto searchListDto) {
		this.searchListDto = searchListDto;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
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
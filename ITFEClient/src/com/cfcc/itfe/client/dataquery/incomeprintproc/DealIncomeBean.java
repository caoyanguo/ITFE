package com.cfcc.itfe.client.dataquery.incomeprintproc;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.IncomeReportDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;

/**
 * codecomment: 
 * @author zhouchuan
 * @time   10-05-24 13:45:48
 * ��ϵͳ: DataQuery
 * ģ��:incomePrintProc
 * ���:DealIncome
 */
public class DealIncomeBean extends AbstractDealIncomeBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(DealIncomeBean.class);
	private TvInfileDto finddto = null; // ��ѯ��������
	private TvInfileDto operdto = null; // ���ݲ�������
	private PagingContext incometablepage; // ��ҳ�ؼ�
	
	private List reportRs = null;
	private Map reportmap = new HashMap();
	private String reportPath = "com/cfcc/itfe/client/ireport/incomeReport.jasper";
//	private String reportPath = "com/cfcc/itfe/client/ireport/test.jasper";
    public DealIncomeBean() {
		super();
		reportRs = new ArrayList();
		finddto = new TvInfileDto();
		operdto = new TvInfileDto();
		incometablepage = new PagingContext(this);
          
    }
    
	/**
	 * Direction: ���������ѯ
	 * ename: queryIncome
	 * ���÷���: 
	 * viewers: ���������ѯ
	 * messages: 
	 */
    public String queryIncome(Object o){
		//У���ʽ�������ˮ���Ƿ���д
		if(null == finddto.getStrasrlno() || "".equals(finddto.getStrasrlno().trim())
				|| finddto.getStrasrlno().trim().length() != 18){
			MessageDialog.openMessageDialog(null, "����ҵ���ʽ�������ˮ�ű�����д���ұ���Ϊ18λ��");
			return super.backSearch(o);
		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		incometablepage.setPage(pageResponse);

		if (pageResponse.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
			return super.backSearch(o);
		}

		editor.fireModelChanged();
		
         return super.queryIncome(o);
    }

	/**
	 * Direction: ���ع��������ѯ���
	 * ename: backSearchResult
	 * ���÷���: 
	 * viewers: ��ѯ����
	 * messages: 
	 */
    public String backSearchResult(Object o){
        
        return super.backSearchResult(o);
    }

    /**
	 * Direction: ���ع��������ѯ
	 * ename: backSearch
	 * ���÷���: 
	 * viewers: ���������ѯ
	 * messages: 
	 */
    public String backSearch(Object o){
          return super.backSearch(o);
    }

	/**
	 * Direction: ��ѡһ����¼
	 * ename: selOneRecode
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selOneRecode(Object o){
		operdto = (TvInfileDto) o;
         return super.selOneRecode(o);
    }

	/**
	 * Direction: �����嵥��ӡ
	 * ename: queryPrint
	 * ���÷���: 
	 * viewers: �������뱨���嵥��ӡ
	 * messages: 
	 */
    public String queryPrint(Object o){
		if (null == operdto || null == operdto.getSfilename() || "".equals(operdto.getSfilename().trim())) {
			MessageDialog.openMessageDialog(null, "��ѡ���ӡ�����嵥�ļ���");
	         return super.backSearchResult(o);
		}
		
		try {
			reportRs = dealIncomeService.findIncomeByPrint(operdto);
			
			if(null == reportRs || reportRs.size() == 0){
				MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
				return super.backSearchResult(o);
			}
			
			BigDecimal sumamt = new BigDecimal(0.0);
		
			for(int i = 0; i < reportRs.size(); i++)
			{
				IncomeReportDto dto = new IncomeReportDto();
				dto = (IncomeReportDto)reportRs.get(i);
				sumamt = sumamt.add(dto.getNmoney());
			}
			
			Date date = CommonUtil.strToDate(((IncomeReportDto)reportRs.get(0)).getSaccdate());
			reportmap.put("TRE_NAME", ((IncomeReportDto)reportRs.get(0)).getStrename());
			reportmap.put("REPORT_DATE", date);
			reportmap.put("RECV_TRE_NAME", ((IncomeReportDto)reportRs.get(0)).getSrecvtrename());
			reportmap.put("TAXORG_NAME", ((IncomeReportDto)reportRs.get(0)).getStaxorgname());
			reportmap.put("SUM_AMT", sumamt);
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
			return super.backSearchResult(o);
		}

        return super.queryPrint(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
		try {
			return dealIncomeService.findIncomeByPage(finddto, pageRequest);
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return super.retrieve(pageRequest);
	}

	public TvInfileDto getFinddto() {
		return finddto;
	}

	public void setFinddto(TvInfileDto finddto) {
		this.finddto = finddto;
	}

	public TvInfileDto getOperdto() {
		return operdto;
	}

	public void setOperdto(TvInfileDto operdto) {
		this.operdto = operdto;
	}

	public List getReportRs() {
		return reportRs;
	}

	public void setReportRs(List reportRs) {
		this.reportRs = reportRs;
	}

	public Map getReportmap() {
		return reportmap;
	}

	public void setReportmap(Map reportmap) {
		this.reportmap = reportmap;
	}

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	public PagingContext getIncometablepage() {
		return incometablepage;
	}

	public void SetIncometablepage(PagingContext incometablepage) {
		this.incometablepage = incometablepage;
	}
	

}
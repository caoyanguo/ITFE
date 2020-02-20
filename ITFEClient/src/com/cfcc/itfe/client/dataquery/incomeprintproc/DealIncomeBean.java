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
 * 子系统: DataQuery
 * 模块:incomePrintProc
 * 组件:DealIncome
 */
public class DealIncomeBean extends AbstractDealIncomeBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(DealIncomeBean.class);
	private TvInfileDto finddto = null; // 查询条件对象
	private TvInfileDto operdto = null; // 数据操作对象
	private PagingContext incometablepage; // 分页控件
	
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
	 * Direction: 国库收入查询
	 * ename: queryIncome
	 * 引用方法: 
	 * viewers: 国库收入查询
	 * messages: 
	 */
    public String queryIncome(Object o){
		//校验资金收纳流水号是否填写
		if(null == finddto.getStrasrlno() || "".equals(finddto.getStrasrlno().trim())
				|| finddto.getStrasrlno().trim().length() != 18){
			MessageDialog.openMessageDialog(null, "收入业务：资金收纳流水号必须填写，且必须为18位！");
			return super.backSearch(o);
		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		incometablepage.setPage(pageResponse);

		if (pageResponse.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录!");
			return super.backSearch(o);
		}

		editor.fireModelChanged();
		
         return super.queryIncome(o);
    }

	/**
	 * Direction: 返回国库收入查询结果
	 * ename: backSearchResult
	 * 引用方法: 
	 * viewers: 查询处理
	 * messages: 
	 */
    public String backSearchResult(Object o){
        
        return super.backSearchResult(o);
    }

    /**
	 * Direction: 返回国库收入查询
	 * ename: backSearch
	 * 引用方法: 
	 * viewers: 国库收入查询
	 * messages: 
	 */
    public String backSearch(Object o){
          return super.backSearch(o);
    }

	/**
	 * Direction: 单选一条记录
	 * ename: selOneRecode
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selOneRecode(Object o){
		operdto = (TvInfileDto) o;
         return super.selOneRecode(o);
    }

	/**
	 * Direction: 报解清单打印
	 * ename: queryPrint
	 * 引用方法: 
	 * viewers: 国库收入报解清单打印
	 * messages: 
	 */
    public String queryPrint(Object o){
		if (null == operdto || null == operdto.getSfilename() || "".equals(operdto.getSfilename().trim())) {
			MessageDialog.openMessageDialog(null, "请选择打印报解清单文件！");
	         return super.backSearchResult(o);
		}
		
		try {
			reportRs = dealIncomeService.findIncomeByPrint(operdto);
			
			if(null == reportRs || reportRs.size() == 0){
				MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录!");
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
package com.cfcc.itfe.client.dataquery.trtaxorgpayoutreport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.sendbiz.maketbsfile.IExportTBSFileService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

public class QueryConPayOutReportBean implements IPageDataProvider {

	private static Log log = LogFactory.getLog(QueryConPayOutReportBean.class);
	private ICommonDataAccessService commonDataAccessService;
	private TrTaxorgPayoutReportDto dto;
	private PagingContext queryConPayOutReport;
	
	public QueryConPayOutReportBean(ICommonDataAccessService commonDataAccessService,TrTaxorgPayoutReportDto dto ){
		this.commonDataAccessService = commonDataAccessService;
		this.dto = dto;
		this.queryConPayOutReport = new PagingContext(this);
	}
	
	public PageResponse retrieve(PageRequest pageRequest) {
		try {
			PageResponse response = commonDataAccessService.findRsByDtoWithWherePaging(dto,
					pageRequest, " 1=1 ", "", dto.tableName());
			queryConPayOutReport.setPage(response);
			return response;
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
	}

	public ICommonDataAccessService getCommonDataAccessService() {
		return commonDataAccessService;
	}

	public void setCommonDataAccessService(
			ICommonDataAccessService commonDataAccessService) {
		this.commonDataAccessService = commonDataAccessService;
	}

	public TrTaxorgPayoutReportDto getDto() {
		return dto;
	}

	public void setDto(TrTaxorgPayoutReportDto dto) {
		this.dto = dto;
	}

	public PagingContext getQueryConPayOutReport() {
		return queryConPayOutReport;
	}

	public void setQueryConPayOutReport(PagingContext queryConPayOutReport) {
		this.queryConPayOutReport = queryConPayOutReport;
	}

	

	
}

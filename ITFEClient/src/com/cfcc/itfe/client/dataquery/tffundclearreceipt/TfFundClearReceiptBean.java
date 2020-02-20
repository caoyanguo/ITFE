package com.cfcc.itfe.client.dataquery.tffundclearreceipt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfFundClearReceiptDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author Administrator
 * @time   17-10-27 17:16:17
 * ��ϵͳ: DataQuery
 * ģ��:TfFundClearReceipt
 * ���:TfFundClearReceipt
 */
public class TfFundClearReceiptBean extends AbstractTfFundClearReceiptBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TfFundClearReceiptBean.class);
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
    private ITFELoginInfo logInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    
    public TfFundClearReceiptBean() {
      super();
      searchDto = new TfFundClearReceiptDto();
      pagingcontext = new PagingContext(this);
      printDto = new TfFundClearReceiptDto();
      searchDto.setStrecode(logInfo.getSorgcode().substring(0, 10));
      searchDto.setSentrustdate(TimeFacade.getCurrentStringTime());
    }
    
	/**
	 * Direction: ��ѯ���������
	 * ename: queryResult
	 * ���÷���: 
	 * viewers: ��ѯ�������
	 * messages: 
	 */
    public String queryResult(Object o){
    	PageRequest pageRequest = new PageRequest();
		try {
			PageResponse p = this.retrieve(pageRequest);
			if (p == null || p.getPageRowCount() == 0) {
				MessageDialog.openMessageDialog(null,"��ѯ���ݲ�����!");
				return null;
			}else{
				pagingcontext.setPage(p);
			}
		} catch (Exception e) {
			log.error("��ѯ���ݴ���", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
        return super.queryResult(o);
    }

	/**
	 * Direction: ���ص���ѯ����
	 * ename: backQuery
	 * ���÷���: 
	 * viewers: ��ѯ����
	 * messages: 
	 */
    public String backQuery(Object o){
          return super.backQuery(o);
    }

	/**
	 * Direction: ��ѡ
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	printDto = (TfFundClearReceiptDto)o;
    	return super.singleSelect(o);
    }

	/**
	 * Direction: ������ϸ
	 * ename: detail
	 * ���÷���: 
	 * viewers: ������ϸ
	 * messages: 
	 */
    public String detail(Object o){
    	if (printDto == null || printDto.getSid() == null) {
    		MessageDialog.openMessageDialog(null, "�뵥��ѡ��һ����¼��ѯ��ϸ��Ϣ��");
			return null;
		}
    	return super.detail(o);
    }

	/**
	 * Direction: ������ϸ
	 * ename: backToDetail
	 * ���÷���: 
	 * viewers: ������ϸ
	 * messages: 
	 */
    public String backToDetail(Object o){
          return super.backToDetail(o);
    }

	/**
	 * Direction: ������ϸ
	 * ename: backToModifyDetail
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String backToModifyDetail(Object o){
          return super.backToModifyDetail(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest request) {
    	PageResponse response = new PageResponse(request);
		try {
			String wheresql = " 1=1 ";
			response = commonDataAccessService.findRsByDtoWithWherePaging(searchDto, request, wheresql);
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			this.editor.fireModelChanged();
		}
		return response;
	}

}
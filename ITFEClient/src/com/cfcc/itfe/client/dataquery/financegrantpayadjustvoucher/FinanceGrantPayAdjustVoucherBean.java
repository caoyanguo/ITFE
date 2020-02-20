package com.cfcc.itfe.client.dataquery.financegrantpayadjustvoucher;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.persistence.dto.HtfGrantpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.HtfGrantpayAdjustsubDto;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TfGrantpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfGrantpayAdjustsubDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author Administrator
 * @time   14-11-15 19:19:42
 * ��ϵͳ: DataQuery
 * ģ��:financeGrantPayAdjustVoucher
 * ���:FinanceGrantPayAdjustVoucher
 */
public class FinanceGrantPayAdjustVoucherBean extends AbstractFinanceGrantPayAdjustVoucherBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(FinanceGrantPayAdjustVoucherBean.class);
    
    private FinanceGrantPayAdjustVoucherSubBean curSubBean;
    private FinanceGrantPayAdjustVoucherHisBean hisMainBean;
    private FinanceGrantPayAdjustVoucherHisSubBean hisSubBean;
    private ITFELoginInfo logInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    
    public FinanceGrantPayAdjustVoucherBean() {
      super();
      curSearchDto = new TfGrantpayAdjustmainDto();
      curSubSearchDto = new TfGrantpayAdjustsubDto();
      hisSearchDto = new HtfGrantpayAdjustmainDto();
      hisSubSearchDto = new HtfGrantpayAdjustsubDto();
      pagingContext = new PagingContext(this);
      curSubBean = new FinanceGrantPayAdjustVoucherSubBean();
  	  hisMainBean = new FinanceGrantPayAdjustVoucherHisBean();
  	  hisSubBean = new FinanceGrantPayAdjustVoucherHisSubBean();

  	  curSearchDto.setSorgcode(logInfo.getSorgcode());
  	  hisSearchDto.setSorgcode(logInfo.getSorgcode());
      
      enumList = new ArrayList<TdEnumvalueDto>();
	  TdEnumvalueDto valuedtoa = new TdEnumvalueDto();
	  valuedtoa.setStypecode("��ǰ��");
	  valuedtoa.setSvalue("0");
	  enumList.add(valuedtoa);
	
	  TdEnumvalueDto valuedtob = new TdEnumvalueDto();
	  valuedtob.setStypecode("��ʷ��");
	  valuedtob.setSvalue("1");
	  enumList.add(valuedtob);
	  realValue = "0";
    }
    
	/**
	 * Direction: ��ѡ
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	if(o instanceof TfGrantpayAdjustmainDto){
    		curSubSearchDto.setIvousrlno(((TfGrantpayAdjustmainDto)o).getIvousrlno());
    		String tmp = curSubBean.searchDtoList(curSubSearchDto);
    		if(tmp==null){
    			PagingContext p = curSubBean.getPagingContext();
    			p.setPage(new PageResponse());
    		}
    	}else if(o instanceof HtfGrantpayAdjustmainDto){
    		hisSubSearchDto.setIvousrlno(((HtfGrantpayAdjustmainDto)o).getIvousrlno());
    		String tmp = hisSubBean.searchDtoList(hisSubSearchDto);
    		if(tmp==null){
    			PagingContext p = hisSubBean.getPagingContext();
    			p.setPage(new PageResponse());
    		}
    	}
    	this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
    	return super.singleSelect(o);
    }

	/**
	 * Direction: ˫��
	 * ename: doubleClick
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String doubleClick(Object o){
    	if(o instanceof TfGrantpayAdjustmainDto){
    		curSubSearchDto.setIvousrlno(((TfGrantpayAdjustmainDto)o).getIvousrlno());
    		String tmp = curSubBean.searchDtoList(curSubSearchDto);
    		if(tmp==null){
    			PagingContext p = curSubBean.getPagingContext();
    			p.setPage(new PageResponse());
    		}
    	}else if(o instanceof HtfGrantpayAdjustmainDto){
    		hisSubSearchDto.setIvousrlno(((HtfGrantpayAdjustmainDto)o).getIvousrlno());
    		String tmp = hisSubBean.searchDtoList(hisSubSearchDto);
    		if(tmp==null){
    			PagingContext p = hisSubBean.getPagingContext();
    			p.setPage(new PageResponse());
    		}
    	}
    	this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
    	return super.doubleClick(o);
    }

	/**
	 * Direction: ��ѯ
	 * ename: searchToList
	 * ���÷���: 
	 * viewers: ������Ȩ֧������ƾ֤��ǰ��ѯ���
	 * messages: 
	 */
    public String searchToList(Object o){
    	PageRequest pageRequest = new PageRequest();
		try {
			if(realValue.equals("0")){
				PageResponse p = this.retrieve(pageRequest);
				if (p == null || p.getPageRowCount() == 0) {
					MessageDialog.openMessageDialog(null, "��ѯ���ݲ�����!");
					return null;
				}
				pagingContext.setPage(p);
				return super.searchToList(o);
			}else{
				copyValue();
				hisMainBean.searchDtoList(hisSearchDto);
				return "������Ȩ֧������ƾ֤��ʷ��ѯ���";
			}
		} catch (Exception e) {
			log.error("��ѯ���ݴ���", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
    }

	/**
	 * Direction: ����
	 * ename: backToSearch
	 * ���÷���: 
	 * viewers: ������Ȩ֧������ƾ֤��ѯ����
	 * messages: 
	 */
    public String backToSearch(Object o){
    	curSubBean = new FinanceGrantPayAdjustVoucherSubBean();
    	hisSubBean = new FinanceGrantPayAdjustVoucherHisSubBean();
    	return super.backToSearch(o);
    }

	/**
	 * Direction: �����ļ�
	 * ename: exportFile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportFile(Object o){
    	return super.exportFile(o);
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
			response = commonDataAccessService.findRsByDtoWithWherePaging(curSearchDto, request, wheresql);
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			this.editor.fireModelChanged();
		}
		return response;
	}
    
    public void copyValue(){
    	hisSearchDto.setStrecode(curSearchDto.getStrecode());
    	hisSearchDto.setSstatus(curSearchDto.getSstatus());
    	hisSearchDto.setSdemo(curSearchDto.getSdemo());
    	hisSearchDto.setSpackageno(curSearchDto.getSpackageno());
    	hisSearchDto.setSid(curSearchDto.getSid());
    	hisSearchDto.setSadmdivcode(curSearchDto.getSadmdivcode());
    	hisSearchDto.setSstyear(curSearchDto.getSstyear());
    	hisSearchDto.setSvtcode(curSearchDto.getSvtcode());
    	hisSearchDto.setSvoudate(curSearchDto.getSvoudate());
    	hisSearchDto.setSvoucherno(curSearchDto.getSvoucherno());
    	hisSearchDto.setSbgttypecode(curSearchDto.getSbgttypecode());
    	hisSearchDto.setSbgttypename(curSearchDto.getSbgttypename());
    	hisSearchDto.setSfundtypecode(curSearchDto.getSfundtypecode());
    	hisSearchDto.setSfundtypename(curSearchDto.getSfundtypename());
    	hisSearchDto.setNpayamt(curSearchDto.getNpayamt());
    	hisSearchDto.setSpaybankcode(curSearchDto.getSpaybankcode());
    	hisSearchDto.setSpaybankname(curSearchDto.getSpaybankname());
    	hisSearchDto.setSremark(curSearchDto.getSremark());
    	hisSearchDto.setSxaccdate(curSearchDto.getSxaccdate());
    }

	public FinanceGrantPayAdjustVoucherSubBean getCurSubBean() {
		return curSubBean;
	}

	public void setCurSubBean(FinanceGrantPayAdjustVoucherSubBean curSubBean) {
		this.curSubBean = curSubBean;
	}

	public FinanceGrantPayAdjustVoucherHisBean getHisMainBean() {
		return hisMainBean;
	}

	public void setHisMainBean(FinanceGrantPayAdjustVoucherHisBean hisMainBean) {
		this.hisMainBean = hisMainBean;
	}

	public FinanceGrantPayAdjustVoucherHisSubBean getHisSubBean() {
		return hisSubBean;
	}

	public void setHisSubBean(FinanceGrantPayAdjustVoucherHisSubBean hisSubBean) {
		this.hisSubBean = hisSubBean;
	}

}
package com.cfcc.itfe.client.dataquery.financedirecpayadjustvoucher;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.persistence.dto.HtfDirectpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.HtfDirectpayAdjustsubDto;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TfDirectpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpayAdjustsubDto;
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
 * 子系统: DataQuery
 * 模块:financeDirecPayAdjustVoucher
 * 组件:FinanceDirecPayAdjustVoucher
 */
public class FinanceDirecPayAdjustVoucherBean extends AbstractFinanceDirecPayAdjustVoucherBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(FinanceDirecPayAdjustVoucherBean.class);
    
    private FinanceDirecPayAdjustVoucherSubBean curSubBean;
    private FinanceDirecPayAdjustVoucherHisBean hisMainBean;
    private FinanceDirecPayAdjustVoucherHisSubBean hisSubBean;
    private ITFELoginInfo logInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    
    public FinanceDirecPayAdjustVoucherBean() {
      super();
      curSearchDto = new TfDirectpayAdjustmainDto();
      curSubSearchDto = new TfDirectpayAdjustsubDto();
      hisSearchDto = new HtfDirectpayAdjustmainDto();
      hisSubSearchDto = new HtfDirectpayAdjustsubDto();
      pagingContext = new PagingContext(this);
      curSubBean = new FinanceDirecPayAdjustVoucherSubBean();
  	  hisMainBean = new FinanceDirecPayAdjustVoucherHisBean();
  	  hisSubBean = new FinanceDirecPayAdjustVoucherHisSubBean();

  	  curSearchDto.setSorgcode(logInfo.getSorgcode());
  	  hisSearchDto.setSorgcode(logInfo.getSorgcode());
      
      enumList = new ArrayList<TdEnumvalueDto>();
	  TdEnumvalueDto valuedtoa = new TdEnumvalueDto();
	  valuedtoa.setStypecode("当前表");
	  valuedtoa.setSvalue("0");
	  enumList.add(valuedtoa);
	
	  TdEnumvalueDto valuedtob = new TdEnumvalueDto();
	  valuedtob.setStypecode("历史表");
	  valuedtob.setSvalue("1");
	  enumList.add(valuedtob);
	  realValue = "0";
                  
    }
    
	/**
	 * Direction: 单选
	 * ename: singleSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	if(o instanceof TfDirectpayAdjustmainDto){
    		curSubSearchDto.setIvousrlno(((TfDirectpayAdjustmainDto)o).getIvousrlno());
    		String tmp = curSubBean.searchDtoList(curSubSearchDto);
    		if(tmp==null){
    			PagingContext p = curSubBean.getPagingContext();
    			p.setPage(new PageResponse());
    		}
    	}else if(o instanceof HtfDirectpayAdjustmainDto){
    		hisSubSearchDto.setIvousrlno(((HtfDirectpayAdjustmainDto)o).getIvousrlno());
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
	 * Direction: 双击
	 * ename: doubleClick
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String doubleClick(Object o){
    	if(o instanceof TfDirectpayAdjustmainDto){
    		curSubSearchDto.setIvousrlno(((TfDirectpayAdjustmainDto)o).getIvousrlno());
    		String tmp = curSubBean.searchDtoList(curSubSearchDto);
    		if(tmp==null){
    			PagingContext p = curSubBean.getPagingContext();
    			p.setPage(new PageResponse());
    		}
    	}else if(o instanceof HtfDirectpayAdjustmainDto){
    		hisSubSearchDto.setIvousrlno(((HtfDirectpayAdjustmainDto)o).getIvousrlno());
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
	 * Direction: 查询
	 * ename: searchToList
	 * 引用方法: 
	 * viewers: 财政直接支付调整凭证当前查询结果
	 * messages: 
	 */
    public String searchToList(Object o){
    	PageRequest pageRequest = new PageRequest();
		try {
			if(realValue.equals("0")){
				PageResponse p = this.retrieve(pageRequest);
				if (p == null || p.getPageRowCount() == 0) {
					MessageDialog.openMessageDialog(null, "查询数据不存在!");
					return null;
				}
				pagingContext.setPage(p);
				return super.searchToList(o);
			}else{
				copyValue();
				hisMainBean.searchDtoList(hisSearchDto);
				return "财政直接支付调整凭证历史查询结果";
			}
		} catch (Exception e) {
			log.error("查询数据错误！", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
    }

	/**
	 * Direction: 返回
	 * ename: backToSearch
	 * 引用方法: 
	 * viewers: 财政直接支付调整凭证查询条件
	 * messages: 
	 */
    public String backToSearch(Object o){
    	curSubBean = new FinanceDirecPayAdjustVoucherSubBean();
    	hisSubBean = new FinanceDirecPayAdjustVoucherHisSubBean();
    	return super.backToSearch(o);
    }

	/**
	 * Direction: 导出文件
	 * ename: exportFile
	 * 引用方法: 
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
    	hisSearchDto.setSfinorgcode(curSearchDto.getSfinorgcode());
    	hisSearchDto.setSstatus(curSearchDto.getSstatus());
    	hisSearchDto.setSdemo(curSearchDto.getSdemo());
    	hisSearchDto.setSpackageno(curSearchDto.getSpackageno());
    	hisSearchDto.setSid(curSearchDto.getSid());
    	hisSearchDto.setSadmdivcode(curSearchDto.getSadmdivcode());
    	hisSearchDto.setSstyear(curSearchDto.getSstyear());
    	hisSearchDto.setSvtcode(curSearchDto.getSvtcode());
    	hisSearchDto.setSvoudate(curSearchDto.getSvoudate());
    	hisSearchDto.setSvoucherno(curSearchDto.getSvoucherno());
    	hisSearchDto.setSfundtypecode(curSearchDto.getSfundtypecode());
    	hisSearchDto.setSfundtypename(curSearchDto.getSfundtypename());
    	hisSearchDto.setSbgttypecode(curSearchDto.getSbgttypecode());
    	hisSearchDto.setSbgttypename(curSearchDto.getSbgttypename());
    	hisSearchDto.setSpaytypecode(curSearchDto.getSpaytypecode());
    	hisSearchDto.setSpaytypename(curSearchDto.getSpaytypename());
    	hisSearchDto.setSprocatcode(curSearchDto.getSprocatcode());
    	hisSearchDto.setSprocatname(curSearchDto.getSprocatname());
    	hisSearchDto.setSmofdepcode(curSearchDto.getSmofdepcode());
    	hisSearchDto.setSmofdepname(curSearchDto.getSmofdepname());
    	hisSearchDto.setSfilenocode(curSearchDto.getSfilenocode());
    	hisSearchDto.setSfilenoname(curSearchDto.getSfilenoname());
    	hisSearchDto.setSsupdepcode(curSearchDto.getSsupdepcode());
    	hisSearchDto.setSsupdepname(curSearchDto.getSsupdepname());
    	hisSearchDto.setSagencycode(curSearchDto.getSagencycode());
    	hisSearchDto.setSagencyname(curSearchDto.getSagencyname());
    	hisSearchDto.setSexpfunccode(curSearchDto.getSexpfunccode());
    	hisSearchDto.setSexpfuncname(curSearchDto.getSexpfuncname());
    	hisSearchDto.setSexpfunccode1(curSearchDto.getSexpfunccode1());
    	hisSearchDto.setSexpfuncname1(curSearchDto.getSexpfuncname1());
    	hisSearchDto.setSexpfunccode2(curSearchDto.getSexpfunccode2());
    	hisSearchDto.setSexpfuncname2(curSearchDto.getSexpfuncname2());
    	hisSearchDto.setSexpfunccode3(curSearchDto.getSexpfunccode3());
    	hisSearchDto.setSexpfuncname3(curSearchDto.getSexpfuncname3());
    	hisSearchDto.setSexpecocode(curSearchDto.getSexpecocode());
    	hisSearchDto.setSexpeconame(curSearchDto.getSexpeconame());
    	hisSearchDto.setSexpecocode1(curSearchDto.getSexpecocode1());
    	hisSearchDto.setSexpeconame1(curSearchDto.getSexpeconame1());
    	hisSearchDto.setSexpecocode2(curSearchDto.getSexpecocode2());
    	hisSearchDto.setSexpeconame2(curSearchDto.getSexpeconame2());
    	hisSearchDto.setSdepprocode(curSearchDto.getSdepprocode());
    	hisSearchDto.setSdepproname(curSearchDto.getSdepproname());
    	hisSearchDto.setSsetmodecode(curSearchDto.getSsetmodecode());
    	hisSearchDto.setSsetmodename(curSearchDto.getSsetmodename());
    	hisSearchDto.setSpaybankcode(curSearchDto.getSpaybankcode());
    	hisSearchDto.setSpaybankname(curSearchDto.getSpaybankname());
    	hisSearchDto.setSclearbankcode(curSearchDto.getSclearbankcode());
    	hisSearchDto.setSclearbankname(curSearchDto.getSclearbankname());
    	hisSearchDto.setSpayeeacctno(curSearchDto.getSpayeeacctno());
    	hisSearchDto.setSpayeeacctname(curSearchDto.getSpayeeacctname());
    	hisSearchDto.setSpayeeacctbankname(curSearchDto.getSpayeeacctbankname());
    	hisSearchDto.setSpayeeacctbankno(curSearchDto.getSpayeeacctbankno());
    	hisSearchDto.setSpayacctno(curSearchDto.getSpayacctno());
    	hisSearchDto.setSpayacctname(curSearchDto.getSpayacctname());
    	hisSearchDto.setSpayacctbankname(curSearchDto.getSpayacctbankname());
    	hisSearchDto.setSpaysummarycode(curSearchDto.getSpaysummarycode());
    	hisSearchDto.setSpaysummaryname(curSearchDto.getSpaysummaryname());
    	hisSearchDto.setNpayamt(curSearchDto.getNpayamt());
    	hisSearchDto.setSbusinesstypecode(curSearchDto.getSbusinesstypecode());
    	hisSearchDto.setSbusinesstypename(curSearchDto.getSbusinesstypename());
    	hisSearchDto.setScheckno(curSearchDto.getScheckno());
    	hisSearchDto.setSxpaydate(curSearchDto.getSxpaydate());
    	hisSearchDto.setSxagentbusinessno(curSearchDto.getSxagentbusinessno());
    	hisSearchDto.setSxcheckno(curSearchDto.getSxcheckno());
    	hisSearchDto.setNxpayamt(curSearchDto.getNxpayamt());
    	hisSearchDto.setSxpayeeacctbankname(curSearchDto.getSxpayeeacctbankname());
    	hisSearchDto.setSxpayeeacctno(curSearchDto.getSxpayeeacctno());
    	hisSearchDto.setSxpayeeacctname(curSearchDto.getSxpayeeacctname());
    }

	public FinanceDirecPayAdjustVoucherSubBean getCurSubBean() {
		return curSubBean;
	}

	public void setCurSubBean(FinanceDirecPayAdjustVoucherSubBean curSubBean) {
		this.curSubBean = curSubBean;
	}

	public FinanceDirecPayAdjustVoucherHisBean getHisMainBean() {
		return hisMainBean;
	}

	public void setHisMainBean(FinanceDirecPayAdjustVoucherHisBean hisMainBean) {
		this.hisMainBean = hisMainBean;
	}

	public FinanceDirecPayAdjustVoucherHisSubBean getHisSubBean() {
		return hisSubBean;
	}

	public void setHisSubBean(FinanceDirecPayAdjustVoucherHisSubBean hisSubBean) {
		this.hisSubBean = hisSubBean;
	}

}
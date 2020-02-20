package com.cfcc.itfe.client.dataquery.tvnontaxincomesearch;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TvNontaxmainDto;
import com.cfcc.itfe.persistence.dto.TvNontaxsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.pk.TvVoucherinfoPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;

/**
 * codecomment: 
 * @author zhangliang
 * @time   17-01-22 09:39:26
 * 子系统: DataQuery
 * 模块:TvNontaxIncomeSearch
 * 组件:TvNontaxIncomeSearch
 */

@SuppressWarnings("unchecked")
public class TvNontaxIncomeSearchBean extends AbstractTvNontaxIncomeSearchBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TvNontaxIncomeSearchBean.class);
    private List<TvNontaxsubDto> sublist;
    private PagingContext tablepage;
    private ITFELoginInfo loginfo;
    private TvNontaxmainDto mainDto;
    private TvNontaxmainDto bldto;
    private List<Mapper> bankCodeList;
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
    public TvNontaxIncomeSearchBean() {
      super();
      searchdto = new TvNontaxmainDto();
      updatedto = new TvNontaxmainDto();
      bldto = new TvNontaxmainDto();
      selectDataList = new ArrayList<TvNontaxmainDto>();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      searchdto.setSorgcode(loginfo.getSorgcode());
      searchdto.setScommitdate(TimeFacade.getCurrentStringTime());
      searchdto.setSstatus(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
      searchdto.setStrecode(loginfo.getSorgcode().substring(0,10));
      tablepage = new PagingContext(this);
		if(bankCodeList==null||bankCodeList.size()<=0)
		{
			TsConvertbanktypeDto finddto = new TsConvertbanktypeDto();
			finddto.setSorgcode(loginfo.getSorgcode());
			List<TsConvertbanktypeDto> resultList;
			try {
				resultList = commonDataAccessService.findRsByDto(finddto);
				if(resultList!=null&&resultList.size()>0)
				{
					Mapper map = null;
					bankCodeList = new ArrayList<Mapper>();
					for(TsConvertbanktypeDto temp:resultList)
					{
						map = new Mapper(temp.getSbankcode(), temp.getSbankname()+(temp.getSbankcode().length()>3?temp.getSbankcode().substring(0,3):""));
						bankCodeList.add(map);
					}
				}
			} catch (ITFEBizException e) {
			}
			
		}
    }
        /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest request) {
    	try {
    		PageResponse response = commonDataAccessService.findRsByDtoPaging(searchdto, request, null, null);
			return response;
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
	}
    /**
	 * Direction: 全选
	 * ename: checkall
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String checkall(Object o){
    	PageResponse response = tablepage.getPage();
    	if(response!=null&&response.getData()!=null&&response.getData().size()>0)
    	{
    		if(selectDataList==null)
    			selectDataList = new ArrayList<TvNontaxmainDto>();
    		for(Object el:response.getData())
    		{
    			if(!selectDataList.contains(el))
    				selectDataList.add(el);
    		}
    	}
    	editor.fireModelChanged();
        return super.checkall(o);
    }
    
	/**
	 * Direction: 反选
	 * ename: initall
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String initall(Object o){
    	selectDataList = new ArrayList<TvNontaxmainDto>();
    	editor.fireModelChanged();
        return super.initall(o);
    }
    /**
	 * Direction: 双击事件
	 * ename: doubleclick
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String doubleclick(Object o){
    	mainDto = (TvNontaxmainDto) o;
    	try {
    		TvNontaxsubDto searchdto = new TvNontaxsubDto();
    		searchdto.setSdealno(mainDto.getSdealno());
			sublist = commonDataAccessService.findRsByDto(searchdto);
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		editor.fireModelChanged();
        return super.doubleclick(o);
    }
    /**
	 * Direction: 数据查询
	 * ename: datasearch
	 * 引用方法: 
	 * viewers: 非税收入查询结果
	 * messages: 
	 */
    public String datasearch(Object o){
    	PageRequest request = new PageRequest();
    	PageResponse response = retrieve(request);
		tablepage.setPage(response);
//		editor.fireModelChanged();
    	if (response ==null||response.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录!");
			return null;
		}
        return super.datasearch(o);
    }
    
	/**
	 * Direction: 返回
	 * ename: returnSearch
	 * 引用方法: 
	 * viewers: 非税收入查询条件
	 * messages: 
	 */
    public String returnSearch(Object o){
        
        return super.returnSearch(o);
    }
    
    /**
	 * Direction: 补录
	 * ename: addSeq
	 * 引用方法: 
	 * viewers: 补录资金收纳流水号
	 * messages: 
	 */
    public String addSeq(Object o){
    	String restring = super.addSeq(o);
    	if(selectDataList!=null&&selectDataList.size()>0)
    	{
    		updatedto = (TvNontaxmainDto) selectDataList.get(0);
    		TvNontaxmainDto el = null;
    		bldto = new TvNontaxmainDto();
    		for(int i=0;i<selectDataList.size();i++)
    		{
    			el = (TvNontaxmainDto)selectDataList.get(i);
    			if(updatedto.getSpaydictateno()!=null&&el.getSpaydictateno()!=null&&updatedto.getSpaydictateno().equals(el.getSpaydictateno()))
    			{
    				if(!DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING.equals(el.getSstatus()))
    				{
    					MessageDialog.openMessageDialog(null, "只有状态为待处理才可补录!");
    		        	return null;
    				}
    				if(i==0)
    				{
    					bldto.setSpaydictateno(updatedto.getSpaydictateno());
    					bldto.setSpaymsgno(updatedto.getSpaymsgno());
    					bldto.setStranom(updatedto.getStranom());
    					bldto.setNmoney(updatedto.getNmoney());
    					restring = "补录资金流水号";
    				}else
    				{
    					if(bldto.getNmoney()!=null&&el.getNmoney()!=null)
    						bldto.setNmoney(bldto.getNmoney().add(el.getNmoney()));
    				}
    			}else if(updatedto.getSpaydictateno()==null||el.getSpaydictateno()==null)
    			{
    				if(selectDataList.size()>1){
    		    		MessageDialog.openMessageDialog(null, "每次只能补录一条记录!");
    		        	return null;
    		    	}
    		    	if(selectDataList.size()==0){
    		    		MessageDialog.openMessageDialog(null, "请选择要补录的记录!");
    		        	return null;
    		    	}
    			}else
    			{
    				MessageDialog.openMessageDialog(null, "选中的记录中支付交易序号必须唯一!");
    				return null;
    			}
    		}
    	}    	
        return restring;
    }
    
	/**
	 * Direction: 保存
	 * ename: saveNonTaxIncome
	 * 引用方法: 
	 * viewers: 非税收入查询结果
	 * messages: 
	 */
    public String saveNonTaxIncome(Object o){
    	try {
    		Pattern pattern = Pattern.compile("\\d{18}");
    		Matcher matcher = null;
    		if(updatedto.getSpaydictateno()==null&&updatedto.getSpaydictateno()==null)
    		{
	    		matcher = pattern.matcher(updatedto.getShold1());
	    		if (!matcher.matches()) {
	    			MessageDialog.openMessageDialog(null, "输入不合法，资金收纳流水号必须是18位数字!");
	            	return null;
	    		}
				commonDataAccessService.updateData(updatedto);
				TvVoucherinfoDto dto = new TvVoucherinfoDto();
				dto.setSdealno(updatedto.getSdealno());
				TvVoucherinfoDto udto = (TvVoucherinfoDto) commonDataAccessService.findRsByDto(dto).get(0);
				udto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
				udto.setSdemo("校验成功");
				commonDataAccessService.updateData(udto);
    		}else if(selectDataList!=null)
    		{
    			matcher = pattern.matcher(bldto.getShold1());
    			if (!matcher.matches()) {
	    			MessageDialog.openMessageDialog(null, "输入不合法，资金收纳流水号必须是18位数字!");
	            	return null;
	    		}
    			TvNontaxmainDto el = null;
    			List<TvVoucherinfoDto> vlist = new ArrayList();
    			for(int i=0;i<selectDataList.size();i++)
    			{
    				el = (TvNontaxmainDto)selectDataList.get(i);
    				el.setShold1(bldto.getShold1());
    				TvVoucherinfoDto dto = new TvVoucherinfoDto();
    				dto.setSdealno(el.getSdealno());
    				TvVoucherinfoDto udto = (TvVoucherinfoDto) commonDataAccessService.findRsByDto(dto).get(0);
    				udto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
    				udto.setSdemo("校验成功");
    				vlist.add(udto);
    			}
    			if(vlist.size()>0)
    				commonDataAccessService.updateDtos(vlist);
				commonDataAccessService.updateDtos(selectDataList);
    		}
    		MessageDialog.openMessageDialog(null, "补录成功!");
			selectDataList = new ArrayList<TvNontaxmainDto>();
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
        return super.saveNonTaxIncome(o);
    }
    
	/**
	 * Direction: 返回查询结果
	 * ename: rebackSearchList
	 * 引用方法: 
	 * viewers: 非税收入查询结果
	 * messages: 
	 */
    public String rebackSearchList(Object o){
    
    	selectDataList = new ArrayList<TvNontaxmainDto>();
    	datasearch(o);
        return super.rebackSearchList(o);
    }
    
	public List getSublist() {
		return sublist;
	}
	public void setSublist(List sublist) {
		this.sublist = sublist;
	}
	public PagingContext getTablepage() {
		return tablepage;
	}
	public void setTablepage(PagingContext tablepage) {
		this.tablepage = tablepage;
	}
	public TvNontaxmainDto getMainDto() {
		return mainDto;
	}
	public void setMainDto(TvNontaxmainDto mainDto) {
		this.mainDto = mainDto;
	}
	public TvNontaxmainDto getBldto() {
		return bldto;
	}
	public void setBldto(TvNontaxmainDto bldto) {
		this.bldto = bldto;
	}
	public List<Mapper> getBankCodeList() {
		return bankCodeList;
	}
	public void setBankCodeList(List<Mapper> bankCodeList) {
		this.bankCodeList = bankCodeList;
	}
	
	
	
}
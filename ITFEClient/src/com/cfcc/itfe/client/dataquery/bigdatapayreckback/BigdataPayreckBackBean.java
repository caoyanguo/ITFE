package com.cfcc.itfe.client.dataquery.bigdatapayreckback;

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
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBigdataBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBigdataBackListDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;

/**
 * codecomment: 
 * @author zhangliang
 * @time   18-11-07 15:14:22
 * ��ϵͳ: DataQuery
 * ģ��:bigdataPayreckBack
 * ���:BigdataPayreckBack
 */
public class BigdataPayreckBackBean extends AbstractBigdataPayreckBackBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(BigdataPayreckBackBean.class);
    private List<TvPayreckBigdataBackListDto> sublist;
    private PagingContext tablepage;
    private ITFELoginInfo loginfo;
    private TvPayreckBigdataBackDto mainDto;
    private TvPayreckBigdataBackDto bldto;
    private List<Mapper> bankCodeList;
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
    public BigdataPayreckBackBean() {
      super();
      searchdto = new TvPayreckBigdataBackDto();
      updatedto = new TvPayreckBigdataBackDto();
      selectDataList = new ArrayList();
      
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      searchdto.setSorgcode(loginfo.getSorgcode());
      searchdto.setSvoudate(TimeFacade.getCurrentStringTime());
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
    
	/**
	 * Direction: ���ݲ�ѯ
	 * ename: datasearch
	 * ���÷���: 
	 * viewers: ��˰�����ѯ���
	 * messages: 
	 */
    public String datasearch(Object o){
    	PageRequest request = new PageRequest();
    	PageResponse response = retrieve(request);
		tablepage.setPage(response);
    	if (response ==null||response.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
			return null;
		}
          return super.datasearch(o);
    }

	/**
	 * Direction: ����
	 * ename: returnSearch
	 * ���÷���: 
	 * viewers: ��˰�����ѯ����
	 * messages: 
	 */
    public String returnSearch(Object o){
          return super.returnSearch(o);
    }

	/**
	 * Direction: ˫���¼�
	 * ename: doubleclick
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String doubleclick(Object o){
    	mainDto = (TvPayreckBigdataBackDto) o;
    	try {
    		TvPayreckBigdataBackListDto searchdto = new TvPayreckBigdataBackListDto();
    		searchdto.setIvousrlno(mainDto.getIvousrlno());
			sublist = commonDataAccessService.findRsByDto(searchdto);
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		editor.fireModelChanged();
          return super.doubleclick(o);
    }

	/**
	 * Direction: ȫѡ
	 * ename: checkall
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String checkall(Object o){
    	PageResponse response = tablepage.getPage();
    	if(response!=null&&response.getData()!=null&&response.getData().size()>0)
    	{
    		if(selectDataList==null)
    			selectDataList = new ArrayList<TvPayreckBigdataBackDto>();
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
	 * Direction: ��ѡ
	 * ename: initall
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String initall(Object o){
    	selectDataList = new ArrayList<TvPayreckBigdataBackDto>();
    	editor.fireModelChanged();
          return super.initall(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {
    	try {
    		PageResponse response = commonDataAccessService.findRsByDtoPaging(searchdto, arg0, null, null);
			return response;
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
	}

	public List<TvPayreckBigdataBackListDto> getSublist() {
		return sublist;
	}

	public void setSublist(List<TvPayreckBigdataBackListDto> sublist) {
		this.sublist = sublist;
	}

	public PagingContext getTablepage() {
		return tablepage;
	}

	public void setTablepage(PagingContext tablepage) {
		this.tablepage = tablepage;
	}

	public TvPayreckBigdataBackDto getMainDto() {
		return mainDto;
	}

	public void setMainDto(TvPayreckBigdataBackDto mainDto) {
		this.mainDto = mainDto;
	}

	public TvPayreckBigdataBackDto getBldto() {
		return bldto;
	}

	public void setBldto(TvPayreckBigdataBackDto bldto) {
		this.bldto = bldto;
	}

	public List<Mapper> getBankCodeList() {
		return bankCodeList;
	}

	public void setBankCodeList(List<Mapper> bankCodeList) {
		this.bankCodeList = bankCodeList;
	}

}
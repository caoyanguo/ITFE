package com.cfcc.itfe.client.para.tdtaxorgmerger;

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
import com.cfcc.jaf.rcp.util.SearchUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.para.tdtaxorgmerger.ITstaxorgcodemergerService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TdTaxorgMergerDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;

/**
 * codecomment: 
 * @author Yuan
 * @time   13-03-25 15:48:52
 * 子系统: Para
 * 模块:tdtaxorgmerger
 * 组件:Tstaxorgcodemerger
 */
public class TstaxorgcodemergerBean extends AbstractTstaxorgcodemergerBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TstaxorgcodemergerBean.class);
    private TdTaxorgMergerDto finddto;
    private TdTaxorgMergerDto adddto;
    private TdTaxorgMergerDto moddto;
    private TdTaxorgMergerDto selddto;
    private List<TdTaxorgMergerDto> searchlist;
    private ITFELoginInfo loginfo=null;
    private String precode;
    private String aftercode;
    private String orgname;
	private List<TsTaxorgDto> taxorglist=new ArrayList<TsTaxorgDto>();
    public TstaxorgcodemergerBean() {
      super();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
      finddto=new TdTaxorgMergerDto();  
      adddto=new TdTaxorgMergerDto(); 
      moddto=new TdTaxorgMergerDto(); 
      selddto=new TdTaxorgMergerDto(); 
      searchlist=new ArrayList<TdTaxorgMergerDto>();
      init();
      init2();
    }
    
	
    public void init(){
    	  finddto.setSbookorgcode(loginfo.getSorgcode());
    	  try {
    		TsTaxorgDto taxorgdto=new TsTaxorgDto();
  			taxorgdto.setSorgcode(loginfo.getSorgcode());
  			taxorglist=commonDataAccessService.findRsByDto(taxorgdto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "查询信息时出现异常："+e.getMessage());
		}
    }
    public void init2(){
  	  finddto.setSbookorgcode(loginfo.getSorgcode());
  	  try {
			searchlist=commonDataAccessService.findRsByDto(finddto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "查询信息时出现异常："+e.getMessage());
		}
  }
    public String singleselect(Object o){
    	selddto=(TdTaxorgMergerDto) o;
    	return super.singleselect(o);
    }

	/**
	 * Direction: 增加
	 * ename: add
	 * 引用方法: 
	 * viewers: 录入界面
	 * messages: 
	 */
    public String add(Object o){
    
    	adddto.setSbookorgcode(loginfo.getSorgcode());
    	adddto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));
    	try {
			tstaxorgcodemergerService.add(adddto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "保存数据时出现异常："+e.getMessage());
		}
		MessageDialog.openMessageDialog(null, "保存成功");
		init2();
          return super.backmain(o);
    }

	/**
	 * Direction: 修改
	 * ename: modify
	 * 引用方法: 
	 * viewers: 修改界面
	 * messages: 
	 */
    public String modify(Object o){
    	try {
			tstaxorgcodemergerService.modify(selddto,precode,aftercode);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "保存数据时出现异常："+e.getMessage());
		}
		this.editor.fireModelChanged();
		MessageDialog.openMessageDialog(null, "修改成功");
		init2();
         return super.backmain(o);
    }

	/**
	 * Direction: 删除
	 * ename: delete
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	try {
    		
			tstaxorgcodemergerService.delete(selddto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "删除数据时出现异常："+e.getMessage());
		}
		init();
		this.editor.fireModelChanged();
		MessageDialog.openMessageDialog(null, "删除成功");
		init2();
          return super.delete(o);
    }

	/**
	 * Direction: 返回维护界面
	 * ename: backmain
	 * 引用方法: 
	 * viewers: 维护界面
	 * messages: 
	 */
    public String backmain(Object o){
    	init();
          return super.backmain(o);
    }


	/**
	 * Direction: 到录入界面
	 * ename: toadd
	 * 引用方法: 
	 * viewers: 录入界面
	 * messages: 
	 */
    public String toadd(Object o){
    	adddto=new TdTaxorgMergerDto(); 
          return super.toadd(o);
    }

	/**
	 * Direction: 到修改界面
	 * ename: tomodify
	 * 引用方法: 
	 * viewers: 修改界面
	 * messages: 
	 */
    public String tomodify(Object o){
    	if(selddto==null||selddto.getSbookorgcode()==null||"".equals(selddto.getSbookorgcode())){
			MessageDialog.openMessageDialog(null, "请选择一条记录");
			return null;
		}
    	this.precode=selddto.getSpretaxorgcode();
    	this.aftercode=selddto.getSaftertaxorgcode();
    	this.orgname =selddto.getStaxorgname();
          return super.tomodify(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

	public TdTaxorgMergerDto getFinddto() {
		return finddto;
	}

	public void setFinddto(TdTaxorgMergerDto finddto) {
		this.finddto = finddto;
	}

	public TdTaxorgMergerDto getAddddto() {
		return adddto;
	}

	public void setAddddto(TdTaxorgMergerDto addddto) {
		this.adddto = addddto;
	}

	public TdTaxorgMergerDto getModddto() {
		return moddto;
	}

	public void setModddto(TdTaxorgMergerDto modddto) {
		this.moddto = modddto;
	}

	public TdTaxorgMergerDto getSelddto() {
		return selddto;
	}

	public void setSelddto(TdTaxorgMergerDto selddto) {
		this.selddto = selddto;
	}

	public List<TdTaxorgMergerDto> getSearchList() {
		return searchlist;
	}

	public void setSearchList(List<TdTaxorgMergerDto> searchList) {
		this.searchlist = searchList;
	}

	public TdTaxorgMergerDto getAdddto() {
		return adddto;
	}

	public void setAdddto(TdTaxorgMergerDto adddto) {
		this.adddto = adddto;
	}

	public List<TdTaxorgMergerDto> getSearchlist() {
		return searchlist;
	}

	public void setSearchlist(List<TdTaxorgMergerDto> searchlist) {
		this.searchlist = searchlist;
	}

	public TdTaxorgMergerDto getModdto() {
		return moddto;
	}

	public void setModdto(TdTaxorgMergerDto moddto) {
		this.moddto = moddto;
	}


	public List<TsTaxorgDto> getTaxorglist() {
		return taxorglist;
	}


	public void setTaxorglist(List<TsTaxorgDto> taxorglist) {
		this.taxorglist = taxorglist;
	}


	public String getOrgname() {
		return orgname;
	}


	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}
    
}
package com.cfcc.itfe.client.dataquery.trecodelargetaxandpaytable;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeonlineDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author db2admin
 * @time   14-06-05 16:56:38
 * 子系统: DataQuery
 * 模块:TrecodeLargeTaxAndPayTable
 * 组件:TrecodeLargeTaxAndPayTable
 */
public class TrecodeLargeTaxAndPayTableBean extends AbstractTrecodeLargeTaxAndPayTableBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TrecodeLargeTaxAndPayTableBean.class);
    
    private ITFELoginInfo loginInfo;
    private String queryStyle;
    private BigDecimal limitMoney;
    private List<IDto> taxList = null;
    private List<IDto> checkTaxList = null;
    private List<IDto> payList = null;
    private List<IDto> checkPayList = null;
    private String applydateStart = null;
    private String applydateEnd = null;
    private String billdateStart = null;
    private String billdateEnd = null;
    private String sgenticketdateStart = null;
    private String sgenticketdateEnd = null;
    private String commdateStart = null;
    private String commdateEnd = null;
    public TrecodeLargeTaxAndPayTableBean() {
      super();
      loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      dto = new TvFinIncomeonlineDto();
      dto.setSorgcode(loginInfo.getSorgcode());
      payoutDto = new TvPayoutmsgmainDto();
      payoutDto.setSorgcode(loginInfo.getSorgcode());
      queryStyle = StateConstant.LARGE_TAX_QUERY;
      applydateStart=TimeFacade.getCurrentStringTime();
      applydateEnd=TimeFacade.getCurrentStringTime();
      billdateStart=TimeFacade.getCurrentStringTime();
      billdateEnd=TimeFacade.getCurrentStringTime();
      sgenticketdateStart=TimeFacade.getCurrentStringTime();
      sgenticketdateEnd=TimeFacade.getCurrentStringTime();
      setDefualtStrecode(queryStyle);
      taxList = new ArrayList<IDto>();
      checkTaxList = new ArrayList<IDto>();
      checkPayList = new ArrayList<IDto>();            
      payList = new ArrayList<IDto>();            
    }
    

	/**
     * 设置默认国库代码
     */
	@SuppressWarnings("unchecked")
	public void setDefualtStrecode(String queryStyle){
    	TsTreasuryDto tmpdto = new TsTreasuryDto();
    	tmpdto.setSorgcode(loginInfo.getSorgcode());		
		try {
			if(queryStyle.equals(StateConstant.LARGE_TAX_QUERY)){
				if(StringUtils.isBlank(dto.getStrecode())){
					List<TsTreasuryDto> list=commonDataAccessService.findRsByDto(tmpdto);
					if(list!=null&&list.size()>0)
						dto.setStrecode(list.get(0).getStrecode());
				}
        	}else if(queryStyle.equals(StateConstant.LARGE_PAY_QUERY)){
        		if(StringUtils.isBlank(payoutDto.getStrecode())){
    				List<TsTreasuryDto> list=commonDataAccessService.findRsByDto(tmpdto);
    				if(list!=null&&list.size()>0)
    					payoutDto.setStrecode(list.get(0).getStrecode());
    			}	
        	}else
        	{
        		if(StringUtils.isBlank(dto.getStrecode())){
					List<TsTreasuryDto> list=commonDataAccessService.findRsByDto(tmpdto);
					if(list!=null&&list.size()>0)
						dto.setStrecode(list.get(0).getStrecode());
				}
        	}
					
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}		
    }
    
    
	/**
	 * Direction: 查询
	 * ename: queryResult
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    @SuppressWarnings("unchecked")
	public String queryResult(Object o){
    	if(StringUtils.isBlank(queryStyle)){
    		MessageDialog.openMessageDialog(null, "请选择查询方式！");
    		return "";
    	}
    	StringBuffer where = null;
    	IDto _dto=null;
    	if(queryStyle.equals(StateConstant.LARGE_TAX_QUERY)){
    		if(limitMoney!=null){
    			dto.setFtraamt(limitMoney);
    		}
    		TvFinIncomeonlineDto querydto = (TvFinIncomeonlineDto) dto.clone();
			where = new StringBuffer("");
			if(applydateStart!=null&&!"".equals(applydateStart))
				where.append(" and S_APPLYDATE >='"+applydateStart+"' ");
			if(applydateEnd!=null&&!"".equals(applydateEnd))
				where.append(" and S_APPLYDATE <='"+applydateEnd+"' ");
			if(billdateStart!=null&&!"".equals(billdateStart))
				where.append(" and S_BILLDATE >='"+billdateStart+"' ");
			if(billdateEnd!=null&&!"".equals(billdateEnd))
				where.append(" and S_BILLDATE <='"+billdateEnd+"' ");
			querydto.setSremark(where.toString());
			_dto=querydto;
			
    	}else if(queryStyle.equals(StateConstant.LARGE_PAY_QUERY)){
    		if(limitMoney!=null){
    			payoutDto.setNmoney(limitMoney);
    		}
    		TvPayoutmsgmainDto querydto = (TvPayoutmsgmainDto) payoutDto.clone();
    		
    		where = new StringBuffer("");
    		if(commdateStart!=null&&!"".equals(commdateStart))
				where.append(" and S_COMMITDATE >='"+commdateStart+"' ");
			if(commdateEnd!=null&&!"".equals(commdateEnd))
				where.append(" and S_COMMITDATE <='"+commdateEnd+"' ");
    		if(sgenticketdateStart!=null&&!"".equals(sgenticketdateStart))
				where.append(" and S_GENTICKETDATE >='"+sgenticketdateStart+"' ");
			if(sgenticketdateEnd!=null&&!"".equals(sgenticketdateEnd))
				where.append(" and S_GENTICKETDATE <='"+sgenticketdateEnd+"' ");
			querydto.setSaddword(where.toString());
			_dto=querydto;
    	}
    	List<IDto> list= new ArrayList<IDto>();
    	try {
    		list = trecodeLargeTaxAndPayTableService.queryByList(queryStyle, _dto);
    		if(list==null||list.size()<=0){
    			MessageDialog.openMessageDialog(null, "查询无记录！");
    			return "";
    		}
    		if(queryStyle.equals(StateConstant.LARGE_TAX_QUERY)){
    			taxList.clear();
    			taxList.addAll(list);
        	}else if(queryStyle.equals(StateConstant.LARGE_PAY_QUERY)){
        		payList.clear();
        		payList.addAll(list);
        	}
		} catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "查询大额税源支出统计表出错！");
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.queryResult(o);
    }

	/**
	 * Direction: 全选
	 * ename: selectAll
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectAll(Object o){
    	if(queryStyle.equals(StateConstant.LARGE_TAX_QUERY)){
    		if(checkTaxList==null||checkTaxList.size()==0){
    			checkTaxList = new ArrayList<IDto>();
    			checkTaxList.addAll(taxList);
             }else{
            	 checkTaxList.clear();
             }
    	}else if(queryStyle.equals(StateConstant.LARGE_PAY_QUERY)){
    		if(checkPayList==null||checkPayList.size()==0){
    			checkPayList = new ArrayList<IDto>();
    			checkPayList.addAll(payList);
             }else{
            	 checkPayList.clear(); 
             }
    	}
         this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
         return super.selectAll(o);
    }

	/**
	 * Direction: 导出
	 * ename: exportFile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportFile(Object o){
    	List<IDto> list = new ArrayList<IDto>();
    	if(queryStyle.equals(StateConstant.LARGE_TAX_QUERY)){
    		if(checkTaxList==null||checkTaxList.size()==0){
    			list.addAll(taxList);
             }else{
            	list.addAll(checkTaxList);
             }
    	}else if(queryStyle.equals(StateConstant.LARGE_PAY_QUERY)){
    		if(checkPayList==null||checkPayList.size()==0){
    			list.addAll(payList);
            }else{
            	list.addAll(checkPayList);
            }
    	}
    	// 选择保存路径
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent().getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择导出文件路径!");
			return "";
		}
		String clientFilePath;
		String dirsep = File.separator;
		String serverFilePath;
    	try {
    		serverFilePath = trecodeLargeTaxAndPayTableService.exportFile(queryStyle, list).replace("\\","/");
			int j = serverFilePath.lastIndexOf("/");
			String partfilepath  = serverFilePath.replaceAll(serverFilePath.substring(0, j + 1), "");
			clientFilePath = filePath + dirsep + partfilepath;
			File f = new File(clientFilePath);
			File dir = new File(f.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			ClientFileTransferUtil.downloadFile(serverFilePath, clientFilePath);
			MessageDialog.openMessageDialog(null, "导出文件成功！文件保存在：\n"+ clientFilePath);
			selectAll(null);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "导出大额税源支出统计表出错！");
		} catch (FileTransferException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "导出大额税源支出统计表出错！");
		}
          return super.exportFile(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	pageRequest.setPageSize(20);
    	if(!StringUtils.isBlank(limitMoney.toString())){
    		dto.setFtraamt(limitMoney);
    	}
    	try {
			return trecodeLargeTaxAndPayTableService.queryByPage(queryStyle, pageRequest, dto);
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "查询大额税源支出统计表出错！");
		}
		return super.retrieve(pageRequest);
	}

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		TrecodeLargeTaxAndPayTableBean.log = log;
	}

	public ITFELoginInfo getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(ITFELoginInfo loginInfo) {
		this.loginInfo = loginInfo;
	}

	public String getQueryStyle() {
		return queryStyle;
	}

	public void setQueryStyle(String queryStyle) {
		this.queryStyle = queryStyle;
		if(queryStyle==null||"".equals(queryStyle))
			queryStyle = StateConstant.LARGE_TAX_QUERY;
		if(StateConstant.LARGE_TAX_QUERY.equals(queryStyle))
		{
			List<String> contreaNames = new ArrayList<String>();
			contreaNames.add("大额税源查询条件");
			contreaNames.add("大额税源查询一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames, true);
			List<String> contreaNames1 = new ArrayList<String>();
			contreaNames1.add("大额支出查询条件");
			contreaNames1.add("大额支出查询一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
			taxList.clear();
		}else if(StateConstant.LARGE_PAY_QUERY.equals(queryStyle))
		{
			List<String> contreaNames = new ArrayList<String>();
			contreaNames.add("大额税源查询条件");
			contreaNames.add("大额税源查询一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames, false);
			List<String> contreaNames1 = new ArrayList<String>();
			contreaNames1.add("大额支出查询条件");
			contreaNames1.add("大额支出查询一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, true);
			payList.clear();
		}
		setDefualtStrecode(queryStyle);
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}

	public BigDecimal getLimitMoney() {
		return limitMoney;
	}

	public void setLimitMoney(BigDecimal limitMoney) {
		this.limitMoney = limitMoney;
	}

	public List<IDto> getCheckTaxList() {
		return checkTaxList;
	}

	public void setCheckTaxList(List<IDto> checkTaxList) {
		this.checkTaxList = checkTaxList;
	}

	public List<IDto> getCheckPayList() {
		return checkPayList;
	}

	public void setCheckPayList(List<IDto> checkPayList) {
		this.checkPayList = checkPayList;
	}

	public List<IDto> getTaxList() {
		return taxList;
	}

	public void setTaxList(List<IDto> taxList) {
		this.taxList = taxList;
	}

	public List<IDto> getPayList() {
		return payList;
	}

	public void setPayList(List<IDto> payList) {
		this.payList = payList;
	}


	public String getApplydateStart() {
		return applydateStart;
	}


	public void setApplydateStart(String applydateStart) {
		this.applydateStart = applydateStart;
	}


	public String getApplydateEnd() {
		return applydateEnd;
	}


	public void setApplydateEnd(String applydateEnd) {
		this.applydateEnd = applydateEnd;
	}


	public String getBilldateStart() {
		return billdateStart;
	}


	public void setBilldateStart(String billdateStart) {
		this.billdateStart = billdateStart;
	}


	public String getBilldateEnd() {
		return billdateEnd;
	}


	public void setBilldateEnd(String billdateEnd) {
		this.billdateEnd = billdateEnd;
	}


	public String getSgenticketdateStart() {
		return sgenticketdateStart;
	}


	public void setSgenticketdateStart(String sgenticketdateStart) {
		this.sgenticketdateStart = sgenticketdateStart;
	}


	public String getSgenticketdateEnd() {
		return sgenticketdateEnd;
	}


	public void setSgenticketdateEnd(String sgenticketdateEnd) {
		this.sgenticketdateEnd = sgenticketdateEnd;
	}


	public String getCommdateStart() {
		return commdateStart;
	}


	public void setCommdateStart(String commdateStart) {
		this.commdateStart = commdateStart;
	}


	public String getCommdateEnd() {
		return commdateEnd;
	}


	public void setCommdateEnd(String commdateEnd) {
		this.commdateEnd = commdateEnd;
	}

	
}
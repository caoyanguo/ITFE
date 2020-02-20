package com.cfcc.itfe.client.sendbiz.createvoucherforreport;

import itferesourcepackage.CompareVoucherTypeBankEnumFactory;
import itferesourcepackage.CompareVoucherTypeEnumFactory;
import itferesourcepackage.CompareVoucherTypeMofEnumFactory;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositeVoucherDayCheckAccountOcx;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.pk.TvVoucherinfoPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * codecomment: 
 * @author db2admin
 * @time   13-09-03 11:12:28
 * 子系统: SendBiz
 * 模块:createVoucherForReport
 * 组件:CreateVoucherForReport
 */
public class CreateVoucherForReportBean extends AbstractCreateVoucherForReportBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(CreateVoucherForReportBean.class);
    private ITFELoginInfo loginfo;
    private String voucherType = null;
    public List voucherList;
    private List subVoucherList;
    List<TvVoucherinfoDto> checkList=null; 
    public CreateVoucherForReportBean() {
    	super();
        pagingcontext = new PagingContext(this);  
        dto = new TvVoucherinfoDto();
        dto.setScreatdate(TimeFacade.getCurrentStringTime());
        dto.setScheckdate(TimeFacade.getCurrentStringTime());
        loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();    	
    	checkList=new ArrayList();
    	voucherList = new CompareVoucherTypeEnumFactory().getEnums(null);
    }
  
	/**
	 * Direction: 生成凭证
	 * ename: createVoucherAndSend
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String createVoucherAndSend(Object o){
    	if (null == dto.getScheckdate() || dto.getScheckdate().equals("")) {
			MessageDialog.openMessageDialog(null,
					"请输入对账日期！");
			return null;
		}    	
    	int count=0;
    	try {
    		//判断是否已经生成凭证
    		String sbuf=voucherIsRepeat();
    		if(StringUtils.isNotBlank(sbuf))
        		if(!org.eclipse.jface.dialogs.MessageDialog.openQuestion(null, "信息提示", sbuf.toString()+"\r\n确定继续生成吗？"))
            		return "";       	
    		count = createVoucherForReportService.createVoucherAndSend(voucherType, subVoucherType, treCode, dto.getScheckdate(), loginfo.getSorgcode());
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception(e.getMessage(),e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
			return "";
		} catch(Exception e){
			log.error(e);
			Exception e1=new Exception("生成凭证操作出现异常！",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
			return "";
		}
		if(count==0){
			MessageDialog.openMessageDialog(null,"没有该日期的凭证生成！");
			return null;
		}
		MessageDialog.openMessageDialog(null, "报表凭证生成操作成功，成功条数为："+count+" ！");
		refreshTable();
        return super.createVoucherAndSend(o);
    }
    
    /**
     * 获取OCX控件url
     * @return
     */
    public String getOcxVoucherServerURL(){
    	String ls_URL = "";
    	try {
    		ls_URL = voucherLoadService.getOCXServerURL();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("获取OCX控件URL地址操作出现异常！",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
		}
		return ls_URL;
    }
    
    /**
     * 获取签章服务地址
     * @return
     */
    public String getOCXStampServerURL(){
    	String ls_URL = "";
    	try {
    		ls_URL = voucherLoadService.getOCXStampServerURL();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("获取OCX控件签章服务URL地址操作出现异常！",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
		}
		return ls_URL;
    }
    
	/**
	 * Direction: 查询
	 * ename: search
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String search(Object o){
    	refreshTable();
        return super.search(o);
    }
    
    private void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse=new PageResponse();
		pageResponse = retrieve(pageRequest);
		if(pageResponse.getTotalCount()==0){
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录，请先生成凭证！");
		}
		pagingcontext.setPage(pageResponse);
	}
    
    /**
	 * Direction: 全选
	 * ename: selectAll
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectAll(Object o){
    	 if(checkList==null||checkList.size()==0){
         	checkList = new ArrayList();
         	checkList.addAll(pagingcontext.getPage().getData());
         }
         else
         	checkList.clear();
         this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
         return super.selectAll(o);
    }
    
    /**
	 * Direction: 发送电子凭证
	 * ename: sendReturnVoucher
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherSend(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "请选择要发送电子凭证的记录！");
    		return "";
    	}
    	int count=0;
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定要对选中的记录执行发送电子凭证操作吗？")) {
			return "";
		}
    	for(TvVoucherinfoDto infoDto:checkList){
			if(!(DealCodeConstants.VOUCHER_STAMP.equals(infoDto.getSstatus().trim()))){
				MessageDialog.openMessageDialog(null, "请选择凭证状态为 \"处理成功\" 的记录！");
        		return "";
				}
	    	}
		
    	try {
    		
    		count=voucherLoadService.voucherReturnSuccess(checkList);
    		MessageDialog.openMessageDialog(null, "发送电子凭证   "+checkList.size()+" 条，成功条数为："+count+" ！");
    		refreshTable();
		} catch (ITFEBizException e) {			
			log.error(e);	
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e);
			return "";
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}catch(Exception e){
			log.error(e);
			Exception e1=new Exception("发送电子凭证库操作出现异常！",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			
			return "";
		}
	
    	return super.voucherSend(o);
         
    }

	/**
	 * Direction: 凭证查看
	 * ename: voucherView
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherView(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "请选择要查看的记录！");
    		return "";
    	}
    	try{
        	ActiveXCompositeVoucherDayCheckAccountOcx.init(0);
        	
    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
			return "";
		}catch(Exception e){
    		log.error(e);
    		Exception e1=new Exception("凭证查看异常！",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			return "";
    	}
        return super.voucherView(o);
    }
    
    
    /**
	 * Direction: 查询凭证打印次数
	 * ename: queryVoucherPrintCount
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String queryVoucherPrintCount(TvVoucherinfoDto vDto){
    	String err=null;
    	try {
			err=voucherLoadService.queryVoucherPrintCount(vDto);
			
		} catch (ITFEBizException e) {			
			log.error(e);
			return "查询异常";
		}
    	return err;
    }
    
    /**
	 * Direction: 查询凭证联数
	 * ename: queryVoucherPrintCount
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public int queryVoucherJOintCount(TvVoucherinfoDto vDto){
    	int count=0;
    	TsVouchercommitautoDto tDto=new TsVouchercommitautoDto();
    	tDto.setSorgcode(vDto.getSorgcode());
    	tDto.setStrecode(vDto.getStrecode());
    	tDto.setSvtcode(vDto.getSvtcode());
    	try {
			List<TsVouchercommitautoDto> list= (List) commonDataAccessService.findRsByDto(tDto);
			if(list==null||list.size()==0)
				return -1;
			tDto=list.get(0);
			if(tDto.getSjointcount()==null){
				return -1;
			}			
		} catch (ITFEBizException e) {
			log.error(e);
			return -2;
		}catch(Exception e){
			log.error(e);
			return -1;
		}
    	return tDto.getSjointcount();
    }
    
    public String getVoucherXMl(TvVoucherinfoDto vDto) throws ITFEBizException{
   	    return voucherLoadService.voucherStampXml(vDto);
    }

    public void refreshTable(){
    	init();
		checkList.clear();		
		editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
    }
    
        /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);
    	PageResponse page = null;
    	StringBuffer wheresql=new StringBuffer();
    	wheresql.append(" 1=1 ");
    	if(voucherType==null||voucherType.equals("")){
    		dto.setSvtcode(null);
        	voucherList = new CompareVoucherTypeEnumFactory().getEnums(null);
        	wheresql.append("AND ( ");
        	for(int i=0;i<voucherList.size();i++){
        		Mapper mapper=(Mapper)voucherList.get(i);
        		if(i==voucherList.size()-1){
            		wheresql.append(" S_VTCODE = '"+ mapper.getUnderlyValue()+"' )");
            		break;
        		}            		
        		wheresql.append(" S_VTCODE = '"+ mapper.getUnderlyValue()+"' OR ");
        	}       	
    	}else{
    		dto.setSvtcode(voucherType);
    	}
    	dto.setScheckvouchertype(subVoucherType);
    	dto.setStrecode(treCode);
    	try {
    		page =  commonDataAccessService.findRsByDtoPaging(dto, pageRequest, wheresql.toString(), " TS_SYSUPDATE desc");
    	
    		return page;
    				
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
			
		}catch (Throwable e) {
			log.error(e);
			Exception e1=new Exception("凭证查询出现异常！",e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			
		}
		return super.retrieve(pageRequest);
	}
    
    public TvVoucherinfoDto getDto(TvVoucherinfoDto dto) throws ITFEBizException{
    	TvVoucherinfoPK pk=new TvVoucherinfoPK();
    	pk.setSdealno(dto.getSdealno());    	   	
    	return (TvVoucherinfoDto) commonDataAccessService.find(pk);
    }
    
	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public List getSubVoucherList() {
		return subVoucherList;
	}

	public void setSubVoucherList(List subVoucherList) {
		this.subVoucherList = subVoucherList;
	}

	public List getVoucherList() {
		return voucherList;
	}

	public void setVoucherList(List voucherList) {
		this.voucherList = voucherList;
	}
	public String getVoucherType() {
		return voucherType;
	}
	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
		if(voucherType.equals(MsgConstant.VOUCHER_NO_3501))
			subVoucherList =new CompareVoucherTypeMofEnumFactory().getEnums(null);
		else if(voucherType.equals(MsgConstant.VOUCHER_NO_3502))
			subVoucherList =new CompareVoucherTypeBankEnumFactory().getEnums(null);
		subVoucherType=null;
	}

	public List<TvVoucherinfoDto> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<TvVoucherinfoDto> checkList) {
		this.checkList = checkList;
	}
	
	/**
	 * 对账凭证判重
	 * @return
	 * @throws ITFEBizException
	 */
	private String voucherIsRepeat() throws ITFEBizException{
		TvVoucherinfoDto vDto=new TvVoucherinfoDto();
		vDto.setSorgcode(loginfo.getSorgcode());
    	vDto.setScheckdate(dto.getScheckdate());
		StringBuffer sbf = new StringBuffer("");
		List<TsTreasuryDto> tList = new ArrayList<TsTreasuryDto>();
		TsTreasuryDto tDto=new TsTreasuryDto();
		tDto.setSorgcode(loginfo.getSorgcode());
		tDto.setStrecode(treCode);
		if(StringUtils.isBlank(treCode))   		
    		tList = commonDataAccessService.findRsByDto(tDto);			
    	else
    		tList.add(tDto);
		//根据具体国库遍历凭证列表
		for(TsTreasuryDto tedto:tList){
			vDto.setStrecode(tedto.getStrecode());
			if(StringUtils.isBlank(voucherType)||(StringUtils.isNotBlank(voucherType)&&StringUtils.isBlank(subVoucherType)))
				sbf.append(voucherIsRepeat(vDto, voucherType));
			else if(StringUtils.isNotBlank(voucherType)&&StringUtils.isNotBlank(subVoucherType))
				sbf.append(voucherIsRepeat(vDto,voucherType,subVoucherType));						
		}return sbf.toString();    	
	}
	
	/**
	 * 查询重复凭证
	 * @param dto
	 * @param voucherType
	 * @param subVoucherType
	 * @return
	 * @throws ITFEBizException
	 */
	private String voucherIsRepeat(TvVoucherinfoDto dto,String voucherType,String subVoucherType) throws ITFEBizException{
		dto.setSvtcode(voucherType);
		dto.setSattach(subVoucherType);
		List list=commonDataAccessService.findRsByDto(dto);
		StringBuffer sbf = new StringBuffer("");
		if(list!=null&&list.size()>0){
			sbf.append("国库为：")
    		.append(dto.getStrecode())
    		.append(", 对账类型为：")
    		.append(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3501)?"人民银行与财政凭证对账3501":"人民银行与代理银行凭证对账3502")
    		.append(", 凭证类型为：")
    		.append(dto.getSattach())
    		.append(", 对账日期为：")
    		.append(dto.getScheckdate())
    		.append(" 对账凭证已生成,")
    		.append("\r\n");
		}return sbf.toString();	
	}
	
	/**
	 * 查询重复凭证
	 * @param dto
	 * @param voucherType
	 * @return
	 * @throws ITFEBizException
	 */
	private String voucherIsRepeat(TvVoucherinfoDto dto,String voucherType) throws ITFEBizException{
		StringBuffer sbf = new StringBuffer("");
		if(StringUtils.isBlank(voucherType)||(StringUtils.isNotBlank(voucherType)&&voucherType.equals(MsgConstant.VOUCHER_NO_3501))){
			sbf.append(voucherIsRepeat(dto,MsgConstant.VOUCHER_NO_3501,MsgConstant.VOUCHER_NO_5106));
			sbf.append(voucherIsRepeat(dto,MsgConstant.VOUCHER_NO_3501,MsgConstant.VOUCHER_NO_5108));
			sbf.append(voucherIsRepeat(dto,MsgConstant.VOUCHER_NO_3501,MsgConstant.VOUCHER_NO_5207));
			sbf.append(voucherIsRepeat(dto,MsgConstant.VOUCHER_NO_3501,MsgConstant.VOUCHER_NO_5209));
			sbf.append(voucherIsRepeat(dto,MsgConstant.VOUCHER_NO_3501,MsgConstant.VOUCHER_NO_3208));
		}else if(StringUtils.isBlank(voucherType)||(StringUtils.isNotBlank(voucherType)&&voucherType.equals(MsgConstant.VOUCHER_NO_3502))){
			if(StringUtils.isNotBlank(voucherType))
				sbf = new StringBuffer("");
			sbf.append(voucherIsRepeat(dto,MsgConstant.VOUCHER_NO_3502,MsgConstant.VOUCHER_NO_2301));
			sbf.append(voucherIsRepeat(dto,MsgConstant.VOUCHER_NO_3502,MsgConstant.VOUCHER_NO_2302));
		}return sbf.toString();
	}
}
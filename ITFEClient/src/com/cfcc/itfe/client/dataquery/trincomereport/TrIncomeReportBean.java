package com.cfcc.itfe.client.dataquery.trincomereport;

import java.util.*;
import java.io.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.metadata.ContainerMetaData;
import com.cfcc.jaf.ui.metadata.TextMetaData;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.DeleteServerFileUtil;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TnLedgerdataDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment: 
 * @author db2admin
 * @time   14-06-17 16:33:50
 * 子系统: DataQuery
 * 模块:trIncomeReport
 * 组件:TrIncomeReport
 */
@SuppressWarnings("unchecked")
public class TrIncomeReportBean extends AbstractTrIncomeReportBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TrIncomeReportBean.class);
    private PagingContext pagingcontext = new PagingContext(this);
    private String reportStyle = null;
    private String sbillkind = null;
    private ITFELoginInfo loginInfo;
	private List ledgerlist = null;
	private TnLedgerdataDto searchdto;
    public TrIncomeReportBean() {
      super();
      String dateString = TimeFacade.getCurrentStringTime();
      stockDto = new TrStockdayrptDto();
      stockDto.setSrptdate(dateString);
      stockDto.setSaccdate(dateString);
      incomeDto = new TrIncomedayrptDto();
      incomeDto.setSrptdate(dateString);
      incomeDto.setSdividegroup("0");
      searchdto = new TnLedgerdataDto();
      stockFilePath = new ArrayList<File>();
      incomeFilePath = new ArrayList<File>();
      reportStyle = StateConstant.REPORT_STYLE_INCOME;   
      loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      searchdto.setSorgcode(loginInfo.getSorgcode());
    }
    
	/**
	 * Direction: TCBS收入类报表导入
	 * ename: importFile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String importFile(Object o){
    	if(reportStyle==null||reportStyle.equals("")){
    		MessageDialog.openMessageDialog(null, "报表种类不能为空！");
			return "";
    	}
		String info = verifyIsBlank(reportStyle);
		if(!info.equals("")){
			MessageDialog.openMessageDialog(null, info);
			return "";
		}
		if(reportStyle!=null&&reportStyle.equals(StateConstant.REPORT_STYLE_INCOME)){
			String res = verifyParam(incomeDto.getSfinorgcode(), incomeDto.getStaxorgcode(), incomeDto.getStrecode());
			if(!res.equals("")){
				MessageDialog.openMessageDialog(null, res);
				return "";
			}
			return importFile(incomeFilePath, incomeDto, o,reportStyle);
    	}else if(reportStyle!=null&&reportStyle.equals(StateConstant.REPORT_STYLE_STOCK)){
    		String res = verifyParam(stockDto.getSorgcode(), null, stockDto.getStrecode());
    		if(!res.equals("")){
				MessageDialog.openMessageDialog(null, res);
				return "";
			}
    		return importFile(stockFilePath, stockDto, o,reportStyle);
    	}else
    	{
    		importFile(stockFilePath, searchdto, o,reportStyle);
    	}
        return super.importFile(o);
    }
    
    /**
	 * Direction: 分户账查询
	 * ename: ledgersearch
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String ledgersearch(Object o){
    	PageRequest pageRequest = new PageRequest();
    	searchdto.setSorgcode(loginInfo.getSorgcode());
		PageResponse pageResponse = retrieve(pageRequest);
		if (null==pageResponse ||pageResponse.getTotalCount()==0 ) {
			MessageDialog.openMessageDialog(null, "查询无记录！");
			return null;
		}
		pagingcontext.setPage(pageResponse);
		this.editor.fireModelChanged();
        return super.ledgersearch(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {
    	try {
			PageResponse response = null;
			TnLedgerdataDto qudto = (TnLedgerdataDto)searchdto.clone();
			qudto.setSorgcode(loginInfo.getSorgcode());
			String startdate = qudto.getSext1();
			String enddate = qudto.getSext2();
			qudto.setSext1(null);
			qudto.setSext2(null);
			String where = null;
			if(startdate!=null&&!"".equals(startdate))
			{
				where = "S_ACCTDATE>='"+startdate+"'";
				if(enddate!=null&&!"".equals(enddate))
				{
					where = where + " and S_ACCTDATE<='"+enddate+"' ";
				}
			}else if(enddate!=null&&!"".equals(enddate))
			{
				where = "S_ACCTDATE<='"+startdate+"'";
			}
			response = commonDataAccessService.findRsByDtoWithWherePaging(qudto,arg0, " 1=1 " +(where==null?"":" and "+where));
			return response;

		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(arg0);
	}
    public String importFile(List<File> filePath,IDto dto ,Object o,String reportStyle){
    	String errorInfo="";
    	if(reportStyle!=null&&reportStyle.equals(StateConstant.REPORT_STYLE_INCOME)){
    		errorInfo="TCBS收入日报文件";
    	}else if(reportStyle!=null&&reportStyle.equals(StateConstant.REPORT_STYLE_STOCK)){
    		errorInfo="TCBS库存日报文件";
    	}
    	List<String> serverpathlist = new ArrayList<String>();	
		if (null == filePath || filePath.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要加载的文件！");
			return "";
		}
		if (filePath.size() > 1000) {
			MessageDialog.openMessageDialog(null, "所选加载文件不能大于1000个！");
			return "";
		}
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "导入提示"," 请确认导入文件与所选择的条件是否一致，确定需要导入吗？")) {
			return "";
		}
		try {
			for (File tmpfile:(List<File>) filePath) {
				if (null == tmpfile || null == tmpfile.getName()|| null == tmpfile.getAbsolutePath()) {					
					MessageDialog.openMessageDialog(null, " 请选择要加载的文件！");
					return "";
				}
				if (!tmpfile.getName().trim().toLowerCase().endsWith(".csv")) {
					MessageDialog.openMessageDialog(null, " 请选择正确的文件格式,只支持csv格式文件！");					
					return "";
				}
				//删除服务器的同名文件，防止覆盖写入
				List<String> pathlist = new ArrayList<String>();
				String path =File.separator + "ITFEDATA"+File.separator+loginInfo.getSorgcode()+File.separator+TimeFacade.getCurrentStringTime()+File.separator+tmpfile.getName();
				pathlist.add(path);
				DeleteServerFileUtil.delFile(commonDataAccessService,pathlist);
				//将导入文件加载到服务器
				String serverpath = ClientFileTransferUtil.uploadFile(tmpfile.getAbsolutePath());
				serverpathlist.add(serverpath);
			}	
			trIncomeReportService.importFile(serverpathlist, dto,reportStyle);
			MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), "导入成功！");
		} catch (FileTransferException e) {
			try {
				DeleteServerFileUtil.delFile(commonDataAccessService,serverpathlist);
			} catch (ITFEBizException e1) {
				log.error("删除服务器文件失败！", e1);
				MessageDialog.openErrorDialog(null, e);
			}
			log.debug(errorInfo+"上传服务器失败！"+e.getMessage());
			MessageDialog.openMessageDialog(null, errorInfo+"上传服务器失败！"+e.getMessage());
			
		} catch (ITFEBizException e) {
			try {
				DeleteServerFileUtil.delFile(commonDataAccessService,serverpathlist);
			} catch (ITFEBizException e1) {
				log.error("删除服务器文件失败！", e1);
				MessageDialog.openErrorDialog(null, e);
			}
			log.debug(errorInfo+"导入失败！"+e.getMessage());
			MessageDialog.openMessageDialog(null, errorInfo+"导入失败！"+e.getMessage());
		}
		return super.importFile(o);
    }
    
    
    public String verifyIsBlank(String reportStyle){
    	StringBuffer sbf = new StringBuffer("");
    	if(reportStyle!=null&&reportStyle.equals(StateConstant.REPORT_STYLE_INCOME)){
    		if(StringUtils.isBlank(incomeDto.getStrecode())){
    			sbf.append("国库代码不能为空！\r\n");
    		}else if(!incomeDto.getStrecode().toString().trim().matches("[0-9]{10}")){
    			sbf.append("国库代码必须为10位数字！\r\n");
    		}
    		if(StringUtils.isBlank(incomeDto.getSfinorgcode())){
    			sbf.append("财政机构代码不能为空！\r\n");
    		}else if(!incomeDto.getSfinorgcode().toString().trim().matches("[0-9]+")){
    			sbf.append("财政机构代码必须为数字！\r\n");
    		}
    		if(StringUtils.isBlank(incomeDto.getStaxorgcode())){
    			sbf.append("征收机关代码不能为空！\r\n");
    		}else if(!incomeDto.getStaxorgcode().toString().trim().matches("[0-9]+")){
    			sbf.append("征收机关代码必须为数字！\r\n");
    		}
    		if(StringUtils.isBlank(incomeDto.getSrptdate())){
    			sbf.append("报表日期不能为空！\r\n");
    		}else if(!incomeDto.getSrptdate().toString().trim().matches("[0-9]{8}")){
    			sbf.append("报表日期格式不正确,必须为8位数字！\r\n");
    		}
    		if(StringUtils.isBlank(incomeDto.getSbudgettype())){
    			sbf.append("预算种类不能为空！\r\n");
    		}
    		if(StringUtils.isBlank(incomeDto.getSbudgetlevelcode())){
    			sbf.append("预算级次不能为空！\r\n");
    		}
    		if(StringUtils.isBlank(incomeDto.getSbelongflag())){
    			sbf.append("辖属标志不能为空！\r\n");
    		}
    		if(StringUtils.isBlank(incomeDto.getStrimflag())){
    			sbf.append("调整期标志不能为空！\r\n");
    		}
    		if(StringUtils.isBlank(incomeDto.getSbillkind())){
    			sbf.append("报表种类不能为空！\r\n");
    		}
    		if(StringUtils.isBlank(incomeDto.getSdividegroup())){
    			sbf.append("分成组标志不能为空！\r\n");
    		}
    	}else if(reportStyle!=null&&reportStyle.equals(StateConstant.REPORT_STYLE_STOCK)){
    		if(StringUtils.isBlank(stockDto.getStrecode())){
    			sbf.append("国库代码不能为空！\r\n");
    		}else if(!stockDto.getStrecode().toString().trim().matches("[0-9]{10}")){
    			sbf.append("国库代码必须为10位数字！\r\n");
    		}
    		if(StringUtils.isBlank(stockDto.getSorgcode())){
    			sbf.append("财政机构代码不能为空！\r\n");
    		}else if(!stockDto.getSorgcode().toString().trim().matches("[0-9]+")){
    			sbf.append("财政机构代码必须为数字！\r\n");
    		}
    		if(StringUtils.isBlank(stockDto.getSrptdate())){
    			sbf.append("报表日期不能为空！\r\n");
    		}else if(!stockDto.getSrptdate().toString().trim().matches("[0-9]{8}")){
    			sbf.append("报表日期格式不正确,必须为8位数字！\r\n");
    		}
    		if(StringUtils.isBlank(stockDto.getSaccdate())){
    			sbf.append("帐户日期不能为空！");
    		}else if(!stockDto.getSaccdate().toString().trim().matches("[0-9]{8}")){
    			sbf.append("帐户日期格式不正确,必须为8位数字！");
    		}
    	}else
    	{
    		if(StringUtils.isBlank(searchdto.getStrecode())){
    			sbf.append("国库代码不能为空！\r\n");
    		}
    	}
		return sbf.toString();
	}
    
    
	public String verifyParam(String sfinorgcode,String staxorgcode,String strecode){
    	StringBuffer sbf = new StringBuffer("");
	    try {
	    	//检验国库主体代码
	    	TsTreasuryDto trecodeDto=new TsTreasuryDto();
	    	trecodeDto.setSorgcode(loginInfo.getSorgcode());
	    	trecodeDto.setStrecode(strecode);
			List<TsTreasuryDto> trecodeDtos = commonDataAccessService.findRsByDto(trecodeDto);
			if(trecodeDtos==null||trecodeDtos.size()<=0){
				sbf.append("国库主体代码在[国库主体信息参数]中未维护!\r\n");
				return sbf.toString();
			}
	    	//校验财政机构代码
	    	TsConvertfinorgDto finorgDto = new TsConvertfinorgDto();
	    	finorgDto.setSorgcode(loginInfo.getSorgcode());
	    	finorgDto.setSfinorgcode(sfinorgcode);
	    	finorgDto.setStrecode(strecode);
	    	List<TsConvertfinorgDto> finorgDtos = commonDataAccessService.findRsByDto(finorgDto);
	    	if(finorgDtos==null||finorgDtos.size()<=0){
				sbf.append("该国库下的[财政机构代码]尚未维护!\r\n");
			}
	    	if(staxorgcode==null){
				return sbf.toString();
			}
			//校验征收机关代码
	    	if(staxorgcode!=null&&!"000000000000".equals(staxorgcode.trim())&&!"111111111111".equals(staxorgcode.trim())&&!"222222222222".equals(staxorgcode.trim())&&!"333333333333".equals(staxorgcode.trim())&&!"444444444444".equals(staxorgcode.trim())&&!"555555555555".equals(staxorgcode.trim()))
	    	{
				TsTaxorgDto taxorgDto = new TsTaxorgDto();
				taxorgDto.setSorgcode(loginInfo.getSorgcode());
				taxorgDto.setStaxorgcode(staxorgcode);
				taxorgDto.setStrecode(strecode);
				List<TsTaxorgDto> taxorgDtos = commonDataAccessService.findRsByDto(taxorgDto);
				if(taxorgDtos==null||taxorgDtos.size()<=0){
					sbf.append("该国库下的[征收机关代码]尚未维护!");
				}
	    	}
		} catch (ITFEBizException e) {
			log.debug("校验参数失败！"+e.getMessage());
			MessageDialog.openMessageDialog(null,"校验参数失败！"+e.getMessage());
		}
		return sbf.toString();
    	
    }
    

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		TrIncomeReportBean.log = log;
	}

	public String getReportStyle() {
		return reportStyle;
	}

	public void setReportStyle(String reportStyle) {
		this.reportStyle = reportStyle;
		if(StateConstant.REPORT_STYLE_INCOME.equals(reportStyle))
		{
			List<String> contreaNames = new ArrayList<String>();
			contreaNames.add("TCBS收入日报导入条件");
			contreaNames.add("TCBS收入日报导入文件");
			contreaNames.add("TCBS分户账选择条件");
			MVCUtils.setContentAreaVisible(editor, contreaNames, true);
			List<String> contreaNames1 = new ArrayList<String>();
			contreaNames1.add("TCBS库存日报导入条件");
			contreaNames1.add("TCBS库存日报导入文件");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
		}else if(StateConstant.REPORT_STYLE_STOCK.equals(reportStyle))
		{
			List<String> contreaNames = new ArrayList<String>();
			contreaNames.add("TCBS收入日报导入条件");
			contreaNames.add("TCBS收入日报导入文件");
			contreaNames.add("TCBS分户账选择条件");
			MVCUtils.setContentAreaVisible(editor, contreaNames, false);
			List<String> contreaNames1 = new ArrayList<String>();
			contreaNames1.add("TCBS库存日报导入条件");
			contreaNames1.add("TCBS库存日报导入文件");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, true);
		}else
		{
			
			List<String> contreaNames = new ArrayList<String>();
			contreaNames.add("TCBS收入日报导入条件");
			contreaNames.add("TCBS库存日报导入条件");
			contreaNames.add("TCBS收入日报导入文件");
			MVCUtils.setContentAreaVisible(editor, contreaNames, false);
			List<String> contreaNames1 = new ArrayList<String>();
			contreaNames1.add("TCBS分户账选择条件");
			contreaNames1.add("TCBS库存日报导入文件");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, true);
		}
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}

	public String getSbillkind() {
		return sbillkind;
	}

	public void setSbillkind(String sbillkind) {
		this.sbillkind = sbillkind;
		this.incomeDto.setSbillkind(sbillkind);
		//根据不同的凭证显示不同的控件
		setContentVisible();
	}
	
	
	public void setContentVisible(){
		List<String> visContentAreaName=new ArrayList<String>();
		visContentAreaName.add("TCBS收入日报导入条件");
	    List<ContainerMetaData> containerMetaData=MVCUtils.setContentAreasToVisible(editor, visContentAreaName);
	    for (ContainerMetaData metadata : containerMetaData) {
	    	List controls = metadata.controlMetadatas;
			for (int i = 0; i < controls.size(); i++) {
				if(sbillkind.equals(StateConstant.REPORTTYPE_FLAG_TRSHAREBILL)
						||sbillkind.equals(StateConstant.REPORTTYPE_FLAG_NRSHAREBILL)){
					if (controls.get(i) instanceof TextMetaData) {
						TextMetaData textmetadata = (TextMetaData) controls.get(i);
						if (textmetadata.caption.equals("分成组标志")) {
							textmetadata.visible=true;
							incomeDto.setSdividegroup(null);
						}
					}
				}else{
					if (controls.get(i) instanceof TextMetaData) {
						TextMetaData textmetadata = (TextMetaData) controls.get(i);
						if (textmetadata.caption.equals("分成组标志")) {
							textmetadata.visible=false;
							incomeDto.setSdividegroup("0");
						}
					}
				}
			}
		}
	    MVCUtils.reopenCurrentComposite(editor);
	}

	public List getLedgerlist() throws ITFEBizException {
		if(ledgerlist==null)
		{
			ledgerlist = new ArrayList();
			TsInfoconnorgaccDto quedto = new TsInfoconnorgaccDto();
			quedto.setSorgcode(loginInfo.getSorgcode());
			quedto.setStrecode(searchdto.getStrecode());
			List acclist = commonDataAccessService.findRsByDto(quedto);
			if(acclist!=null&&acclist.size()>0)
			{
				Mapper map = null;//new Mapper("all","全部");//				ledgerlist.add(map);
				for(int i=0;i<acclist.size();i++)
				{
					quedto = (TsInfoconnorgaccDto)acclist.get(i);
					map = new Mapper(quedto.getSpayeraccount(),quedto.getSpayeraccount());
					ledgerlist.add(map);
				}
			}
		}
		return ledgerlist;
	}

	public void setLedgerlist(List ledgerlist) {
		this.ledgerlist = ledgerlist;
	}

	public PagingContext getPagingcontext() {
		return pagingcontext;
	}

	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
	}

	public ITFELoginInfo getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(ITFELoginInfo loginInfo) {
		this.loginInfo = loginInfo;
	}

	public TnLedgerdataDto getSearchdto() {
		return searchdto;
	}

	public void setSearchdto(TnLedgerdataDto searchdto) {
		this.searchdto = searchdto;
	}
	
}
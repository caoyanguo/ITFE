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
 * ��ϵͳ: DataQuery
 * ģ��:trIncomeReport
 * ���:TrIncomeReport
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
	 * Direction: TCBS�����౨����
	 * ename: importFile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String importFile(Object o){
    	if(reportStyle==null||reportStyle.equals("")){
    		MessageDialog.openMessageDialog(null, "�������಻��Ϊ�գ�");
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
	 * Direction: �ֻ��˲�ѯ
	 * ename: ledgersearch
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String ledgersearch(Object o){
    	PageRequest pageRequest = new PageRequest();
    	searchdto.setSorgcode(loginInfo.getSorgcode());
		PageResponse pageResponse = retrieve(pageRequest);
		if (null==pageResponse ||pageResponse.getTotalCount()==0 ) {
			MessageDialog.openMessageDialog(null, "��ѯ�޼�¼��");
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
    		errorInfo="TCBS�����ձ��ļ�";
    	}else if(reportStyle!=null&&reportStyle.equals(StateConstant.REPORT_STYLE_STOCK)){
    		errorInfo="TCBS����ձ��ļ�";
    	}
    	List<String> serverpathlist = new ArrayList<String>();	
		if (null == filePath || filePath.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ص��ļ���");
			return "";
		}
		if (filePath.size() > 1000) {
			MessageDialog.openMessageDialog(null, "��ѡ�����ļ����ܴ���1000����");
			return "";
		}
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "������ʾ"," ��ȷ�ϵ����ļ�����ѡ��������Ƿ�һ�£�ȷ����Ҫ������")) {
			return "";
		}
		try {
			for (File tmpfile:(List<File>) filePath) {
				if (null == tmpfile || null == tmpfile.getName()|| null == tmpfile.getAbsolutePath()) {					
					MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ���ص��ļ���");
					return "";
				}
				if (!tmpfile.getName().trim().toLowerCase().endsWith(".csv")) {
					MessageDialog.openMessageDialog(null, " ��ѡ����ȷ���ļ���ʽ,ֻ֧��csv��ʽ�ļ���");					
					return "";
				}
				//ɾ����������ͬ���ļ�����ֹ����д��
				List<String> pathlist = new ArrayList<String>();
				String path =File.separator + "ITFEDATA"+File.separator+loginInfo.getSorgcode()+File.separator+TimeFacade.getCurrentStringTime()+File.separator+tmpfile.getName();
				pathlist.add(path);
				DeleteServerFileUtil.delFile(commonDataAccessService,pathlist);
				//�������ļ����ص�������
				String serverpath = ClientFileTransferUtil.uploadFile(tmpfile.getAbsolutePath());
				serverpathlist.add(serverpath);
			}	
			trIncomeReportService.importFile(serverpathlist, dto,reportStyle);
			MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), "����ɹ���");
		} catch (FileTransferException e) {
			try {
				DeleteServerFileUtil.delFile(commonDataAccessService,serverpathlist);
			} catch (ITFEBizException e1) {
				log.error("ɾ���������ļ�ʧ�ܣ�", e1);
				MessageDialog.openErrorDialog(null, e);
			}
			log.debug(errorInfo+"�ϴ�������ʧ�ܣ�"+e.getMessage());
			MessageDialog.openMessageDialog(null, errorInfo+"�ϴ�������ʧ�ܣ�"+e.getMessage());
			
		} catch (ITFEBizException e) {
			try {
				DeleteServerFileUtil.delFile(commonDataAccessService,serverpathlist);
			} catch (ITFEBizException e1) {
				log.error("ɾ���������ļ�ʧ�ܣ�", e1);
				MessageDialog.openErrorDialog(null, e);
			}
			log.debug(errorInfo+"����ʧ�ܣ�"+e.getMessage());
			MessageDialog.openMessageDialog(null, errorInfo+"����ʧ�ܣ�"+e.getMessage());
		}
		return super.importFile(o);
    }
    
    
    public String verifyIsBlank(String reportStyle){
    	StringBuffer sbf = new StringBuffer("");
    	if(reportStyle!=null&&reportStyle.equals(StateConstant.REPORT_STYLE_INCOME)){
    		if(StringUtils.isBlank(incomeDto.getStrecode())){
    			sbf.append("������벻��Ϊ�գ�\r\n");
    		}else if(!incomeDto.getStrecode().toString().trim().matches("[0-9]{10}")){
    			sbf.append("����������Ϊ10λ���֣�\r\n");
    		}
    		if(StringUtils.isBlank(incomeDto.getSfinorgcode())){
    			sbf.append("�����������벻��Ϊ�գ�\r\n");
    		}else if(!incomeDto.getSfinorgcode().toString().trim().matches("[0-9]+")){
    			sbf.append("���������������Ϊ���֣�\r\n");
    		}
    		if(StringUtils.isBlank(incomeDto.getStaxorgcode())){
    			sbf.append("���ջ��ش��벻��Ϊ�գ�\r\n");
    		}else if(!incomeDto.getStaxorgcode().toString().trim().matches("[0-9]+")){
    			sbf.append("���ջ��ش������Ϊ���֣�\r\n");
    		}
    		if(StringUtils.isBlank(incomeDto.getSrptdate())){
    			sbf.append("�������ڲ���Ϊ�գ�\r\n");
    		}else if(!incomeDto.getSrptdate().toString().trim().matches("[0-9]{8}")){
    			sbf.append("�������ڸ�ʽ����ȷ,����Ϊ8λ���֣�\r\n");
    		}
    		if(StringUtils.isBlank(incomeDto.getSbudgettype())){
    			sbf.append("Ԥ�����಻��Ϊ�գ�\r\n");
    		}
    		if(StringUtils.isBlank(incomeDto.getSbudgetlevelcode())){
    			sbf.append("Ԥ�㼶�β���Ϊ�գ�\r\n");
    		}
    		if(StringUtils.isBlank(incomeDto.getSbelongflag())){
    			sbf.append("Ͻ����־����Ϊ�գ�\r\n");
    		}
    		if(StringUtils.isBlank(incomeDto.getStrimflag())){
    			sbf.append("�����ڱ�־����Ϊ�գ�\r\n");
    		}
    		if(StringUtils.isBlank(incomeDto.getSbillkind())){
    			sbf.append("�������಻��Ϊ�գ�\r\n");
    		}
    		if(StringUtils.isBlank(incomeDto.getSdividegroup())){
    			sbf.append("�ֳ����־����Ϊ�գ�\r\n");
    		}
    	}else if(reportStyle!=null&&reportStyle.equals(StateConstant.REPORT_STYLE_STOCK)){
    		if(StringUtils.isBlank(stockDto.getStrecode())){
    			sbf.append("������벻��Ϊ�գ�\r\n");
    		}else if(!stockDto.getStrecode().toString().trim().matches("[0-9]{10}")){
    			sbf.append("����������Ϊ10λ���֣�\r\n");
    		}
    		if(StringUtils.isBlank(stockDto.getSorgcode())){
    			sbf.append("�����������벻��Ϊ�գ�\r\n");
    		}else if(!stockDto.getSorgcode().toString().trim().matches("[0-9]+")){
    			sbf.append("���������������Ϊ���֣�\r\n");
    		}
    		if(StringUtils.isBlank(stockDto.getSrptdate())){
    			sbf.append("�������ڲ���Ϊ�գ�\r\n");
    		}else if(!stockDto.getSrptdate().toString().trim().matches("[0-9]{8}")){
    			sbf.append("�������ڸ�ʽ����ȷ,����Ϊ8λ���֣�\r\n");
    		}
    		if(StringUtils.isBlank(stockDto.getSaccdate())){
    			sbf.append("�ʻ����ڲ���Ϊ�գ�");
    		}else if(!stockDto.getSaccdate().toString().trim().matches("[0-9]{8}")){
    			sbf.append("�ʻ����ڸ�ʽ����ȷ,����Ϊ8λ���֣�");
    		}
    	}else
    	{
    		if(StringUtils.isBlank(searchdto.getStrecode())){
    			sbf.append("������벻��Ϊ�գ�\r\n");
    		}
    	}
		return sbf.toString();
	}
    
    
	public String verifyParam(String sfinorgcode,String staxorgcode,String strecode){
    	StringBuffer sbf = new StringBuffer("");
	    try {
	    	//��������������
	    	TsTreasuryDto trecodeDto=new TsTreasuryDto();
	    	trecodeDto.setSorgcode(loginInfo.getSorgcode());
	    	trecodeDto.setStrecode(strecode);
			List<TsTreasuryDto> trecodeDtos = commonDataAccessService.findRsByDto(trecodeDto);
			if(trecodeDtos==null||trecodeDtos.size()<=0){
				sbf.append("�������������[����������Ϣ����]��δά��!\r\n");
				return sbf.toString();
			}
	    	//У�������������
	    	TsConvertfinorgDto finorgDto = new TsConvertfinorgDto();
	    	finorgDto.setSorgcode(loginInfo.getSorgcode());
	    	finorgDto.setSfinorgcode(sfinorgcode);
	    	finorgDto.setStrecode(strecode);
	    	List<TsConvertfinorgDto> finorgDtos = commonDataAccessService.findRsByDto(finorgDto);
	    	if(finorgDtos==null||finorgDtos.size()<=0){
				sbf.append("�ù����µ�[������������]��δά��!\r\n");
			}
	    	if(staxorgcode==null){
				return sbf.toString();
			}
			//У�����ջ��ش���
	    	if(staxorgcode!=null&&!"000000000000".equals(staxorgcode.trim())&&!"111111111111".equals(staxorgcode.trim())&&!"222222222222".equals(staxorgcode.trim())&&!"333333333333".equals(staxorgcode.trim())&&!"444444444444".equals(staxorgcode.trim())&&!"555555555555".equals(staxorgcode.trim()))
	    	{
				TsTaxorgDto taxorgDto = new TsTaxorgDto();
				taxorgDto.setSorgcode(loginInfo.getSorgcode());
				taxorgDto.setStaxorgcode(staxorgcode);
				taxorgDto.setStrecode(strecode);
				List<TsTaxorgDto> taxorgDtos = commonDataAccessService.findRsByDto(taxorgDto);
				if(taxorgDtos==null||taxorgDtos.size()<=0){
					sbf.append("�ù����µ�[���ջ��ش���]��δά��!");
				}
	    	}
		} catch (ITFEBizException e) {
			log.debug("У�����ʧ�ܣ�"+e.getMessage());
			MessageDialog.openMessageDialog(null,"У�����ʧ�ܣ�"+e.getMessage());
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
			contreaNames.add("TCBS�����ձ���������");
			contreaNames.add("TCBS�����ձ������ļ�");
			contreaNames.add("TCBS�ֻ���ѡ������");
			MVCUtils.setContentAreaVisible(editor, contreaNames, true);
			List<String> contreaNames1 = new ArrayList<String>();
			contreaNames1.add("TCBS����ձ���������");
			contreaNames1.add("TCBS����ձ������ļ�");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
		}else if(StateConstant.REPORT_STYLE_STOCK.equals(reportStyle))
		{
			List<String> contreaNames = new ArrayList<String>();
			contreaNames.add("TCBS�����ձ���������");
			contreaNames.add("TCBS�����ձ������ļ�");
			contreaNames.add("TCBS�ֻ���ѡ������");
			MVCUtils.setContentAreaVisible(editor, contreaNames, false);
			List<String> contreaNames1 = new ArrayList<String>();
			contreaNames1.add("TCBS����ձ���������");
			contreaNames1.add("TCBS����ձ������ļ�");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, true);
		}else
		{
			
			List<String> contreaNames = new ArrayList<String>();
			contreaNames.add("TCBS�����ձ���������");
			contreaNames.add("TCBS����ձ���������");
			contreaNames.add("TCBS�����ձ������ļ�");
			MVCUtils.setContentAreaVisible(editor, contreaNames, false);
			List<String> contreaNames1 = new ArrayList<String>();
			contreaNames1.add("TCBS�ֻ���ѡ������");
			contreaNames1.add("TCBS����ձ������ļ�");
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
		//���ݲ�ͬ��ƾ֤��ʾ��ͬ�Ŀؼ�
		setContentVisible();
	}
	
	
	public void setContentVisible(){
		List<String> visContentAreaName=new ArrayList<String>();
		visContentAreaName.add("TCBS�����ձ���������");
	    List<ContainerMetaData> containerMetaData=MVCUtils.setContentAreasToVisible(editor, visContentAreaName);
	    for (ContainerMetaData metadata : containerMetaData) {
	    	List controls = metadata.controlMetadatas;
			for (int i = 0; i < controls.size(); i++) {
				if(sbillkind.equals(StateConstant.REPORTTYPE_FLAG_TRSHAREBILL)
						||sbillkind.equals(StateConstant.REPORTTYPE_FLAG_NRSHAREBILL)){
					if (controls.get(i) instanceof TextMetaData) {
						TextMetaData textmetadata = (TextMetaData) controls.get(i);
						if (textmetadata.caption.equals("�ֳ����־")) {
							textmetadata.visible=true;
							incomeDto.setSdividegroup(null);
						}
					}
				}else{
					if (controls.get(i) instanceof TextMetaData) {
						TextMetaData textmetadata = (TextMetaData) controls.get(i);
						if (textmetadata.caption.equals("�ֳ����־")) {
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
				Mapper map = null;//new Mapper("all","ȫ��");//				ledgerlist.add(map);
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
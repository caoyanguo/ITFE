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
 * ��ϵͳ: SendBiz
 * ģ��:createVoucherForReport
 * ���:CreateVoucherForReport
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
	 * Direction: ����ƾ֤
	 * ename: createVoucherAndSend
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String createVoucherAndSend(Object o){
    	if (null == dto.getScheckdate() || dto.getScheckdate().equals("")) {
			MessageDialog.openMessageDialog(null,
					"������������ڣ�");
			return null;
		}    	
    	int count=0;
    	try {
    		//�ж��Ƿ��Ѿ�����ƾ֤
    		String sbuf=voucherIsRepeat();
    		if(StringUtils.isNotBlank(sbuf))
        		if(!org.eclipse.jface.dialogs.MessageDialog.openQuestion(null, "��Ϣ��ʾ", sbuf.toString()+"\r\nȷ������������"))
            		return "";       	
    		count = createVoucherForReportService.createVoucherAndSend(voucherType, subVoucherType, treCode, dto.getScheckdate(), loginfo.getSorgcode());
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception(e.getMessage(),e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			return "";
		} catch(Exception e){
			log.error(e);
			Exception e1=new Exception("����ƾ֤���������쳣��",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
			return "";
		}
		if(count==0){
			MessageDialog.openMessageDialog(null,"û�и����ڵ�ƾ֤���ɣ�");
			return null;
		}
		MessageDialog.openMessageDialog(null, "����ƾ֤���ɲ����ɹ����ɹ�����Ϊ��"+count+" ��");
		refreshTable();
        return super.createVoucherAndSend(o);
    }
    
    /**
     * ��ȡOCX�ؼ�url
     * @return
     */
    public String getOcxVoucherServerURL(){
    	String ls_URL = "";
    	try {
    		ls_URL = voucherLoadService.getOCXServerURL();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("��ȡOCX�ؼ�URL��ַ���������쳣��",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
		}
		return ls_URL;
    }
    
    /**
     * ��ȡǩ�·����ַ
     * @return
     */
    public String getOCXStampServerURL(){
    	String ls_URL = "";
    	try {
    		ls_URL = voucherLoadService.getOCXStampServerURL();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("��ȡOCX�ؼ�ǩ�·���URL��ַ���������쳣��",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
		}
		return ls_URL;
    }
    
	/**
	 * Direction: ��ѯ
	 * ename: search
	 * ���÷���: 
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
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼����������ƾ֤��");
		}
		pagingcontext.setPage(pageResponse);
	}
    
    /**
	 * Direction: ȫѡ
	 * ename: selectAll
	 * ���÷���: 
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
	 * Direction: ���͵���ƾ֤
	 * ename: sendReturnVoucher
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherSend(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���͵���ƾ֤�ļ�¼��");
    		return "";
    	}
    	int count=0;
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��Ҫ��ѡ�еļ�¼ִ�з��͵���ƾ֤������")) {
			return "";
		}
    	for(TvVoucherinfoDto infoDto:checkList){
			if(!(DealCodeConstants.VOUCHER_STAMP.equals(infoDto.getSstatus().trim()))){
				MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬Ϊ \"����ɹ�\" �ļ�¼��");
        		return "";
				}
	    	}
		
    	try {
    		
    		count=voucherLoadService.voucherReturnSuccess(checkList);
    		MessageDialog.openMessageDialog(null, "���͵���ƾ֤   "+checkList.size()+" �����ɹ�����Ϊ��"+count+" ��");
    		refreshTable();
		} catch (ITFEBizException e) {			
			log.error(e);	
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e);
			return "";
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch(Exception e){
			log.error(e);
			Exception e1=new Exception("���͵���ƾ֤����������쳣��",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			
			return "";
		}
	
    	return super.voucherSend(o);
         
    }

	/**
	 * Direction: ƾ֤�鿴
	 * ename: voucherView
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherView(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�鿴�ļ�¼��");
    		return "";
    	}
    	try{
        	ActiveXCompositeVoucherDayCheckAccountOcx.init(0);
        	
    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			return "";
		}catch(Exception e){
    		log.error(e);
    		Exception e1=new Exception("ƾ֤�鿴�쳣��",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			return "";
    	}
        return super.voucherView(o);
    }
    
    
    /**
	 * Direction: ��ѯƾ֤��ӡ����
	 * ename: queryVoucherPrintCount
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String queryVoucherPrintCount(TvVoucherinfoDto vDto){
    	String err=null;
    	try {
			err=voucherLoadService.queryVoucherPrintCount(vDto);
			
		} catch (ITFEBizException e) {			
			log.error(e);
			return "��ѯ�쳣";
		}
    	return err;
    }
    
    /**
	 * Direction: ��ѯƾ֤����
	 * ename: queryVoucherPrintCount
	 * ���÷���: 
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
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			
		}catch (Throwable e) {
			log.error(e);
			Exception e1=new Exception("ƾ֤��ѯ�����쳣��",e);
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
	 * ����ƾ֤����
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
		//���ݾ���������ƾ֤�б�
		for(TsTreasuryDto tedto:tList){
			vDto.setStrecode(tedto.getStrecode());
			if(StringUtils.isBlank(voucherType)||(StringUtils.isNotBlank(voucherType)&&StringUtils.isBlank(subVoucherType)))
				sbf.append(voucherIsRepeat(vDto, voucherType));
			else if(StringUtils.isNotBlank(voucherType)&&StringUtils.isNotBlank(subVoucherType))
				sbf.append(voucherIsRepeat(vDto,voucherType,subVoucherType));						
		}return sbf.toString();    	
	}
	
	/**
	 * ��ѯ�ظ�ƾ֤
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
			sbf.append("����Ϊ��")
    		.append(dto.getStrecode())
    		.append(", ��������Ϊ��")
    		.append(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3501)?"�������������ƾ֤����3501":"�����������������ƾ֤����3502")
    		.append(", ƾ֤����Ϊ��")
    		.append(dto.getSattach())
    		.append(", ��������Ϊ��")
    		.append(dto.getScheckdate())
    		.append(" ����ƾ֤������,")
    		.append("\r\n");
		}return sbf.toString();	
	}
	
	/**
	 * ��ѯ�ظ�ƾ֤
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
package com.cfcc.itfe.client.sendbiz.divideandtaxanti;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositeVoucherDivideAndTaxantiReportOcx;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsStamppositionDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvUsersconditionDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.pk.TvVoucherinfoPK;
import com.cfcc.itfe.service.ITFELoginInfo;

/**
 * codecomment: 
 * @author zhangliang
 * @time   14-05-22 06:54:53
 * ��ϵͳ: SendBiz
 * ģ��:divideAndTaxanti
 * ���:DivideAndTaxanti
 */
@SuppressWarnings("unchecked")
public class DivideAndTaxantiBean extends AbstractDivideAndTaxantiBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(DivideAndTaxantiBean.class);
    List<TvVoucherinfoDto> checkList=null;   
	
	//�û���¼��Ϣ
	private ITFELoginInfo loginInfo;
	private String stamp;
	private String svtcode=MsgConstant.VOUCHER_NO_3405;//��������3405-�ܶ�ֳ�,3406-��˰����
	private String shold4="8888888888";//��Ŀ����
	private List stampList=null;
	private List voucherTypeList = null;
	//dto.svtcode��������
	//dto.shold1Ԥ������
	//dto.shold2�����ڱ�־
	//dto.shold3����Χ
	//dto.shold4��Ŀ����
	//dto.strecode�����������
	//dto.scheckdate��������
	//dto.sadmdivcode ��������
	//
	//
    public DivideAndTaxantiBean() {
      super();
      dto = new TvVoucherinfoDto();
      loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      checkList=new ArrayList();
      pagingcontext = new PagingContext(this); 
      TsTreasuryDto tsTreasuryDto = new TsTreasuryDto();
	  tsTreasuryDto.setSorgcode(loginInfo.getSorgcode());
	  dto.setScheckdate(TimeFacade.getCurrentStringTime());
	  dto.setSorgcode(loginInfo.getSorgcode());
	  dto.setShold1(MsgConstant.BUDGET_TYPE_IN_BDG);
	  dto.setSvtcode(svtcode);
	  dto.setShold4(shold4);
	  dto.setShold2(MsgConstant.TIME_FLAG_NORMAL);
	  List<TsTreasuryDto> list = null;
	  try {
		  list = (List<TsTreasuryDto>)commonDataAccessService.findRsByDto(tsTreasuryDto);
	  } catch (ITFEBizException e){
		  log.error(e);
	  }
	  if(list!=null && list.size() > 0){
		  dto.setStrecode(list.get(0).getStrecode());//�������
	  }
	  refreshStampType("");
    }
    
	/**
	 * Direction: ��ѯ
	 * ename: search
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String search(Object o){
    	if(dto.getSvtcode()==null||dto.getSvtcode().equals("")){
    		MessageDialog.openMessageDialog(null, "�������Ͳ���Ϊ�գ�");
    		return "";
    	}
    	if(dto.getShold3()==null||dto.getShold3().equals("")){
    		MessageDialog.openMessageDialog(null, "����Χ����Ϊ�գ�");
    		return "";
    	}
    	if(dto.getStrecode()==null||dto.getStrecode().equals("")){
    		MessageDialog.openMessageDialog(null, "������벻��Ϊ�գ�");
    		return "";
    	}
//    	if(dto.getScheckdate()==null||dto.getScheckdate().equals("")){
//    		MessageDialog.openMessageDialog(null, "�������ڲ���Ϊ�գ�");
//    		return "";
//    	}
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
    public void refreshTable(){
    	init();
		checkList.clear();
		refreshStampType();
    }
    /**
     * ˢ��ǩ������
     * 
     */
    public void refreshStampType(){
    	TsStamppositionDto tsDto=new TsStamppositionDto();
        tsDto.setSorgcode(dto.getSorgcode());
      	tsDto.setSvtcode(dto.getSvtcode());
      	tsDto.setStrecode(dto.getStrecode());
      	Set set=new HashSet();
      	TsStamppositionDto sDto=new TsStamppositionDto();
      	List<TsStamppositionDto> tList=null;
      	stampList=new ArrayList();
      	List<TsStamppositionDto> tsList=new ArrayList<TsStamppositionDto>();
      	try{
          	tList= commonDataAccessService.findRsByDto(tsDto);
          	if(tList.size()>0){
          		for(TsStamppositionDto sdto:tList){
          			set.add(sdto.getSstamptype());
          		}
          		for(String stamptype:(Set<String>)set){
          			sDto=new TsStamppositionDto();
          			sDto.setSorgcode(dto.getSorgcode());
          			sDto.setSvtcode(dto.getSvtcode());
          			sDto.setSstamptype(stamptype);
          			sDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(sDto).get(0);
          			tsList.add(sDto);
          		}
          		stampList.addAll(tsList);
          		if(stampList.size()==1){
          			stamp = ((TsStamppositionDto)stampList.get(0)).getSstamptype();     
          		}
          	}      		
      	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch(Exception e){
      		log.error(e);
      		Exception e1=new Exception("ƾ֤ˢ�²��������쳣��",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);			   			
      	}
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
    }
    /**
     * ˢ��ǩ������
     * 
     */
    public void refreshStampType(String type){
    	TsStamppositionDto tsDto=new TsStamppositionDto();
        tsDto.setSorgcode(dto.getSorgcode());
      	tsDto.setSvtcode(dto.getSvtcode());
      	tsDto.setStrecode(dto.getStrecode());
      	Set set=new HashSet();
      	TsStamppositionDto sDto=new TsStamppositionDto();
      	List<TsStamppositionDto> tList=null;
      	stampList=new ArrayList();
      	List<TsStamppositionDto> tsList=new ArrayList<TsStamppositionDto>();
      	try{
          	tList= commonDataAccessService.findRsByDto(tsDto);
          	if(tList.size()>0){
          		for(TsStamppositionDto sdto:tList){
          			set.add(sdto.getSstamptype());
          		}
          		for(String stamptype:(Set<String>)set){
          			sDto=new TsStamppositionDto();
          			sDto.setSorgcode(dto.getSorgcode());
          			sDto.setSvtcode(dto.getSvtcode());
          			sDto.setSstamptype(stamptype);
          			sDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(sDto).get(0);
          			tsList.add(sDto);
          		}
          		stampList.addAll(tsList);
          		if(stampList.size()==1){
          			stamp = ((TsStamppositionDto)stampList.get(0)).getSstamptype();     
          		}
          	}      		
      	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch(Exception e){
      		log.error(e);
      		Exception e1=new Exception("ƾ֤ˢ�²��������쳣��",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);			   			
      	}
//		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
    }
    /**
	 * Direction: ɾ��ƾ֤
	 * ename: deletegenvoucher
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String delgenvoucher(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "��ѡ��Ҫɾ������ƾ֤���ݼ�¼��");
    		return "";
    	}
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��Ҫ��ѡ�еļ�¼ִ��ɾ��������?")) {
			return "";
		}
    	for(TvVoucherinfoDto infoDto:checkList){
			if(!DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto.getSstatus().trim())){
				MessageDialog.openMessageDialog(null, "ֻ��״̬Ϊ����ɹ������ݿ���ɾ����");
        		return "";
			}
    	}
    	try {
	    	for(TvVoucherinfoDto infoDto:checkList)
	    	{
	    		commonDataAccessService.delete(infoDto);
	    	}
	    	refreshTable();
    	} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("ɾ������ʧ�ܣ�",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			return "";
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}
        return "";
    }
	/**
	 * Direction: ����ƾ֤
	 * ename: voucherGenerator
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherGenerator(Object o){
    	int count=0;
    	List<TvVoucherinfoDto> list=new ArrayList<TvVoucherinfoDto>();
    	if(dto.getSvtcode()==null||dto.getSvtcode().equals("")){
    		MessageDialog.openMessageDialog(null, "�������Ͳ���Ϊ�գ�");
    		return "";
    	}
    	if(dto.getShold3()==null||dto.getShold3().equals("")){
    		MessageDialog.openMessageDialog(null, "����Χ����Ϊ�գ�");
    		return "";
    	}
    	if(dto.getShold1()==null||dto.getShold1().equals("")){
    		MessageDialog.openMessageDialog(null, "Ԥ�����಻��Ϊ�գ�");
    		return "";
    	}
    	if(dto.getStrecode()==null||dto.getStrecode().equals("")){
    		MessageDialog.openMessageDialog(null, "������벻��Ϊ�գ�");
    		return "";
    	}
    	if(dto.getScheckdate()==null||dto.getScheckdate().equals("")){
    		MessageDialog.openMessageDialog(null, "�������ڲ���Ϊ�գ�");
    		return "";
    	}
    	if(MsgConstant.VOUCHER_NO_3406.equals(dto.getSvtcode()))
		{
    		if(dto.getShold1()==null||dto.getShold1().equals("")){
        		MessageDialog.openMessageDialog(null, "Ԥ�����಻��Ϊ�գ�");
        		return "";
        	}
    		if(dto.getShold2()==null||dto.getShold2().equals("")){
        		MessageDialog.openMessageDialog(null, "�����ڱ�־����Ϊ�գ�");
        		return "";
        	}
		}
    	try{
			List dtoList = commonDataAccessService.findRsByDto(dto);
			dto.setScreatdate(TimeFacade.getCurrentStringTime());//ƾ֤����
			if(dtoList!=null&&dtoList.size()>0)
			{
				count=-1;
			}
		}catch(Exception e){
			log.error(e);
			Exception e1=new Exception("����ƾ֤���������쳣��",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
			return "";
		}
		if(count==-1){
    		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
        		.getCurrentComposite().getShell(), "��ʾ"," ��ǰ���ڣ�"+dto.getScheckdate()+"������ƾ֤��ȷ������������")) {
        		return "";
        	}
    	}
		TvVoucherinfoDto gendto = new TvVoucherinfoDto();
		gendto.setSvtcode(dto.getSvtcode());
		gendto.setShold3(dto.getShold3());
		gendto.setStrecode(dto.getStrecode());
		gendto.setSstatus(dto.getSstatus());
		gendto.setShold4(dto.getShold4());
		gendto.setScheckdate(dto.getScheckdate());
		gendto.setSorgcode(dto.getSorgcode());
		gendto.setSext1(dto.getSext1());
		if(MsgConstant.VOUCHER_NO_3406.equals(dto.getSvtcode()))
		{
			gendto.setSpaybankcode(MsgConstant.MSG_TAXORG_SHARE_CLASS);//���ջ��ش���
			gendto.setSattach(MsgConstant.RULE_SIGN_ALL);//Ͻ����־
			gendto.setShold1(dto.getShold1());
			gendto.setShold2(dto.getShold2());
		}
		list.add(gendto);
		List resultList=null;
    	try {
    		if(list.size()>0){
    			resultList=voucherLoadService.voucherGenerate(list);
    		}
    		count=Integer.parseInt(resultList.get(0)+"");
    		if(count==0){
    			MessageDialog.openMessageDialog(null, "��ǰ�������ޱ������ݣ�");
				return "";
    		}    		
    		if(count==-1){
        		MessageDialog.openMessageDialog(null, "����ƾ֤���ɲ����ɹ����ɹ�����Ϊ��"+resultList.get(2)+" ��");
        		refreshTable();
        		return "";
        	}								
		} catch (ITFEBizException e) {
			log.error(e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e);			
			refreshTable();
			return "";
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			return "";
		}catch(Exception e){
			log.error(e);		
			Exception e1=new Exception("����ƾ֤���������쳣��",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);			
			return "";
		}	
		MessageDialog.openMessageDialog(null, "����ƾ֤���ɲ����ɹ����ɹ�����Ϊ��"+count+" ��");
		refreshTable();		
        return super.voucherGenerator(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);
    	PageResponse page = null;
    	try {
    		TvVoucherinfoDto finddto = new TvVoucherinfoDto();
    		finddto.setSvtcode(dto.getSvtcode());
    		finddto.setShold3(dto.getShold3());
    		finddto.setStrecode(dto.getStrecode());
    		finddto.setSorgcode(dto.getSorgcode());
    		finddto.setSstatus(dto.getSstatus());
    		if(loginInfo.getPublicparam().contains(("divideandtaxant=searchbudget")))
    			finddto.setShold4(dto.getShold4());
    		finddto.setScheckdate(dto.getScheckdate());
    		finddto.setSext1(dto.getSext1());//Ԥ�㼶��
    		if(MsgConstant.VOUCHER_NO_3406.equals(dto.getSvtcode()))
    		{
    			finddto.setSpaybankcode(MsgConstant.MSG_TAXORG_SHARE_CLASS);//���ջ��ش���
    			finddto.setSattach(MsgConstant.RULE_SIGN_ALL);//Ͻ����־
    			finddto.setShold1(dto.getShold1());
    			finddto.setShold2(dto.getShold2());
    		}
//    		if(loginInfo.getPublicparam().contains((",vouchersearchhold=5,")))
//			{
//				Calendar calendar = Calendar.getInstance();
//				calendar.setTime(TimeFacade.getCurrentDate());
//				calendar.add(Calendar.DATE, -5);
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// ʱ���ʽ
//				String getdate = dateFormat.format(calendar.getTime());
//				page =  commonDataAccessService.findRsByDtoPaging(finddto,pageRequest, "1=1"+" AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+finddto.getSorgcode()+(finddto.getStrecode()==null?"":"' and S_TRECODE='"+finddto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+finddto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')", " TS_SYSUPDATE desc");
//			}else if(loginInfo.getPublicparam().contains((",vouchersearchhold=15,")))
//			{
//				Calendar calendar = Calendar.getInstance();
//				calendar.setTime(TimeFacade.getCurrentDate());
//				calendar.add(Calendar.DATE, -15);
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// ʱ���ʽ
//				String getdate = dateFormat.format(calendar.getTime());
//				page =  commonDataAccessService.findRsByDtoPaging(finddto,pageRequest, "1=1"+" AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+finddto.getSorgcode()+(finddto.getStrecode()==null?"":"' and S_TRECODE='"+finddto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+finddto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')", " TS_SYSUPDATE desc");
//			}else
//			{
				page =  commonDataAccessService.findRsByDtoPaging(finddto,pageRequest, "1=1", " TS_SYSUPDATE desc");
//			}
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

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		DivideAndTaxantiBean.log = log;
	}

	public List getVoucherTypeList() {
		if(voucherTypeList==null)
		{
			voucherTypeList = new ArrayList();
			Mapper map = new Mapper(MsgConstant.VOUCHER_NO_3405, "�ܶ�ֳɱ���");
			Mapper map1 = new Mapper(MsgConstant.VOUCHER_NO_3406, "��˰��������");
			voucherTypeList.add(map);
			voucherTypeList.add(map1);
			dto.setSvtcode(MsgConstant.VOUCHER_NO_3405);
			dto.setShold3("0");
		}
		return voucherTypeList;
	}

	public void setVoucherTypeList(List voucherTypeList) {
		this.voucherTypeList = voucherTypeList;
	}

	public ITFELoginInfo getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(ITFELoginInfo loginInfo) {
		this.loginInfo = loginInfo;
	}

	public String getStamp() {
		return stamp;
	}

	public void setStamp(String stamp) {
		this.stamp = stamp;
	}

	public List<TvVoucherinfoDto> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<TvVoucherinfoDto> checkList) {
		this.checkList = checkList;
	}

	public List getStampList() {
		return stampList;
	}

	public void setStampList(List stampList) {
		this.stampList = stampList;
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
    		ActiveXCompositeVoucherDivideAndTaxantiReportOcx.init(0);
        	
    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch(Exception e){
    		log.error(e);
    		Exception e1=new Exception("ƾ֤�鿴�쳣��",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
    	}
        return super.voucherView(o);
    }
    /**
	 * Direction: ǩ��
	 * ename: transferCompletedStamp
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherStamp(Object o){    	
    	boolean ocxflag=false;
    	List<TvVoucherinfoDto> checkList=new ArrayList<TvVoucherinfoDto>();
    	String stamp=null;
    	TvVoucherinfoDto dto=new TvVoucherinfoDto();    
    	if(o instanceof List){
			List  ocxStampList=(List) o;
    		String stampname=(String) ocxStampList.get(0);       		
    		dto=(TvVoucherinfoDto) ocxStampList.get(1);    		
    		TsStamppositionDto stampPostionDto=new TsStamppositionDto();
    		stampPostionDto.setSorgcode(dto.getSorgcode());
    		stampPostionDto.setStrecode(dto.getStrecode());
    		stampPostionDto.setSvtcode(dto.getSvtcode());
    		stampPostionDto.setSstampname(stampname);
				try {
					stampPostionDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(stampPostionDto).get(0);
				} catch (ITFEBizException e) {
					log.error(e); 
					Exception e1=new Exception("ǩ�³����쳣��",e);			
					MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
					return "";
				}			
			stamp=stampPostionDto.getSstamptype();
			this.stamp = stampPostionDto.getSstamptype();
			checkList.add(dto);
			ocxflag=true;			
    	}
    	if(!ocxflag){
    		stamp=this.stamp;
    		checkList=this.checkList;
    		dto=this.dto;
    	}
    	int count=0;
    	if ((null == stamp || stamp.trim().length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ��ǩ�����ͣ�");
			return null;
		}
    	
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "��ѡ��Ҫǩ�µļ�¼��");
    		return "";
    	}
    	
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��Ҫ��ѡ�еļ�¼ִ��ǩ�²�����")) {
			return "";
		}
    	for(TvVoucherinfoDto infoDto:checkList){
	    		if(!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto.getSstatus().trim()))){
	    			MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬Ϊ����ɹ��ļ�¼��");	    			
	        		return "";
	    		}
	    	}		
    	try {
    		if(!((TvVoucherinfoDto)checkList.get(0)).getSvtcode().equals(dto.getSvtcode())){
    			MessageDialog.openMessageDialog(null, "ѡ��Ĳ���[ƾ֤����]��У�����ݲ�����");    			
    			return "";       		
    		}    		
    		TsUsersDto uDto=new TsUsersDto();
    		uDto.setSorgcode(loginInfo.getSorgcode());
    		uDto.setSusercode(loginInfo.getSuserCode());
    		uDto=(TsUsersDto) commonDataAccessService.findRsByDto(uDto).get(0);
    		TsStamppositionDto stampDto=new TsStamppositionDto();
    		stampDto.setSvtcode(dto.getSvtcode());
    		stampDto.setSorgcode(loginInfo.getSorgcode());
    		stampDto.setSstamptype(stamp);
    		stampDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(stampDto).get(0);
    		String permission= uDto.getSstamppermission();
    		boolean flag=true;
    		if(permission==null||permission.equals("")){
    			flag=false;
    		}else{
    			if(permission.indexOf(",")<0){
        			if(!permission.equals(stamp)){
        				flag=false;
        			}       			
        		}else{
        			flag=false;
        			String[] permissions=permission.split(",");
        			for(int i=0;i<permissions.length;i++){
        				if(permissions[i].equals(stamp)){
        					flag=true;
        					break;
        				}
        			}        			
        		}
    		}    		
    		if(flag==false){
				MessageDialog.openMessageDialog(null, "��ǰ�û���  \""+stampDto.getSstampname()+"\"  ǩ��Ȩ�ޣ�");    			
    			return "";
			}
    		TsTreasuryDto tDto=new TsTreasuryDto();
    		TsStamppositionDto sDto=new TsStamppositionDto();
    		Map map=new HashMap();
    		String usercode=uDto.getSusercode();
    		String stampuser="";
    		String stampid="";
    		for(TvVoucherinfoDto vDto:checkList){
    			map.put(vDto.getStrecode(), "");
    			stampid=vDto.getSstampid();
    			if(stampid!=null&&!stampid.equals("")){
					String[] stampids=stampid.split(",");
					for(int i=0;i<stampids.length;i++){
						if(stamp.equals(stampids[i])){
							MessageDialog.openMessageDialog(null, "ƾ֤��ţ�"+vDto.getSvoucherno()+" ��ǩ  \""+stampDto.getSstampname()+"\" ��ͬһƾ֤�����ظ�ǩ�£�");			    			
			    			return "";
						}
					}
    			}
    			if(!stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)&&!stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)&&!stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH)){
    				stampuser=vDto.getSstampuser();
    				if(stampuser!=null&&!stampuser.equals("")){
						String[] stampusers=stampuser.split(",");
						for(int i=0;i<stampusers.length;i++){							
							if(usercode.equals(stampusers[i])){
								TsStamppositionDto tstampDto=new TsStamppositionDto();
								tstampDto.setSorgcode(loginInfo.getSorgcode());
								tstampDto.setSvtcode(dto.getSvtcode());
								String[]  stampids=vDto.getSstampid().split(",");
								for(int j=0;j<stampids.length;j++){
									if(!stampids[i].equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){
										tstampDto.setSstamptype(stampids[i]);
										break;
									}
								}
								tstampDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(tstampDto).get(0);
								MessageDialog.openMessageDialog(null, "ƾ֤��ţ�"+vDto.getSvoucherno()+" ��ǰ�û���ǩ  \""+tstampDto.getSstampname()+"\" ��ͬһ�û�ֻ��ǩһ��˽�£���ѡ�������û���");    			    			
    			    			return "";
							}
						}
					
    				}
        		}
    		}    		
    		Iterator it=map.keySet().iterator();    		
    		List lists=new ArrayList();
    		List list=null;
    		List sinList=null;
    		List<TsStamppositionDto> sList=null;
    		List vList=null;
    		String strecode="";
//    		String xml="";
    		while(it.hasNext()){
    			 strecode=it.next()+"";
    			 vList=new ArrayList();
    			 tDto=new TsTreasuryDto();
    			 sDto=new TsStamppositionDto();
    			 sList=new ArrayList<TsStamppositionDto>();
    			 list=new ArrayList();
    			try{
    				tDto.setSorgcode(loginInfo.getSorgcode());
    				tDto.setStrecode(strecode);
        			tDto=(TsTreasuryDto) commonDataAccessService.findRsByDto(tDto).get(0);
    			}catch(Exception e){
    				log.error(e);
    				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ�����в����ڣ�");
    				
    				return "";
    			}    			
    			try{
    				sDto.setSorgcode(loginInfo.getSorgcode());
        			sDto.setStrecode(strecode);
        			sDto.setSvtcode(dto.getSvtcode());
        			sList=(List<TsStamppositionDto>) commonDataAccessService.findRsByDto(sDto);
        			sDto.setSstamptype(stamp);
        			sDto= (TsStamppositionDto) commonDataAccessService.findRsByDto(sDto).get(0);
        			
    			}catch(Exception e){
    			
    				log.error(e);
    				MessageDialog.openMessageDialog(null, "����������룺"+strecode+" "+stampDto.getSstampname()+" ����δά���� ");
    				
    				return "";
    			}    			
    			if(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)){
    				if(tDto.getSrotarycertid()==null||tDto.getSrotarycertid().equals("")){
        				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ������ "+stampDto.getSstampname()+"֤��ID ����δά���� ");
        				
        				return "";
        			}else if(tDto.getSrotaryid()==null||tDto.getSrotaryid().equals("")){
        				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ������ "+stampDto.getSstampname()+"ӡ��ID ����δά���� ");
        				
        				return "";
        			}
    			}else if(stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){

    				if(tDto.getScertid()==null||tDto.getScertid().equals("")){
        				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ������ "+stampDto.getSstampname()+"֤��ID ����δά���� ");
        				
        				return "";
        			}else if(tDto.getSstampid()==null||tDto.getSstampid().equals("")){
        				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ������ "+stampDto.getSstampname()+"ӡ��ID ����δά���� ");
        				
        				return "";
        			}
    			
    			}else if(stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH))
				{
    				if(tDto.getSattachcertid()==null||tDto.getSattachcertid().equals("")){
        				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ������ "+stampDto.getSstampname()+"֤��ID ����δά���� ");
        				
        				return "";
        			}else if(tDto.getSattachid()==null||tDto.getSattachid().equals("")){
        				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ������ "+stampDto.getSstampname()+"ӡ��ID ����δά���� ");
        				
        				return "";
        			}
				}else{
    				if(uDto.getScertid()==null||uDto.getScertid().equals("")){
        				MessageDialog.openMessageDialog(null, "��ǰ�û�  "+stampDto.getSstampname()+"  ֤��ID����δά���� ");        				
        				return "";
        			}else if(uDto.getSstampid()==null||uDto.getSstampid().equals("")){
        				MessageDialog.openMessageDialog(null, "��ǰ�û�   "+stampDto.getSstampname()+"  ӡ��ID����δά���� ");        				
        				return "";
        			}    		    			
    			}
    			for(TvVoucherinfoDto vDto:checkList){
    				if(vDto.getStrecode().equals(strecode)){    					
    					sinList=new ArrayList();
    					sinList.add(vDto);
						stampid=vDto.getSstampid();	
						String seq=sDto.getSstampsequence();   						
						if(seq!=null&&!seq.equals("")){
							List<String> seqList=new ArrayList<String>();
    						for(int i=0;i<sList.size();i++){
    							TsStamppositionDto tsDto=(TsStamppositionDto) sList.get(i);
    							if(tsDto.getSstampsequence()!=null&&!tsDto.getSstampsequence().equals("")){
    								seqList.add(tsDto.getSstampsequence());
    							}
    						}
    						if(seqList!=null&&seqList.size()>0){
    							String[] seqs=seqList.toArray(new String[seqList.size()]);
    							ActiveXCompositeVoucherDivideAndTaxantiReportOcx.sortStringArray(seqs);    							
    							String temp="";
    							for(int i=seqs.length-1;i>-1;i--){
    								if(Integer.parseInt(seqs[i])<Integer.parseInt(seq)){
    									temp=seqs[i];
    									break;
    								}
    							}
    							if(!temp.equals("")){
    								List<TsStamppositionDto> tsList=new ArrayList<TsStamppositionDto>();
    								TsStamppositionDto tsDto=new TsStamppositionDto();
    								tsDto.setSorgcode(loginInfo.getSorgcode());
    								tsDto.setStrecode(strecode);
    								tsDto.setSvtcode(vDto.getSvtcode());
    								tsDto.setSstampsequence(temp);
    			        			tsList=(List<TsStamppositionDto>) commonDataAccessService.findRsByDto(tsDto);
    			        			String err="";
    			        			for(TsStamppositionDto tstampDto:tsList){
    			        				err=tstampDto.getSstampname()+" , "+err;
    			        			}
    			        			err=err.substring(0,err.lastIndexOf(","));
    			        			if(stampid==null||stampid.equals("")){
        								MessageDialog.openMessageDialog(null, "������룺"+vDto.getStrecode()+" ƾ֤����: "+vDto.getSvtcode()+vDto.getSvoucherno()+" \""+stampDto.getSstampname()+"\"ǩ��ǰ���� \""+err+"\"ǩ�£�");
            			    			
            			    			return "";
        							
    			        			}else{
    			        				err="";
    			        				String[] stampids=stampid.split(",");
			        					List<TsStamppositionDto> tsList1=new ArrayList<TsStamppositionDto>();
			        					for(int j=0;j<tsList.size();j++){
			        						for(int i=0;i<stampids.length;i++){
    			        						if(stampids[i].equals(tsList.get(j).getSstamptype())){
    			        							tsList1.add(tsList.get(j));
    			        						}
    			        					}
		        						}
			        					tsList.removeAll(tsList1);
			        					if(tsList.size()>0){
			        						for(TsStamppositionDto tstampDto:tsList){
		    			        				err=tstampDto.getSstampname()+" , "+err;
		    			        			}
			        						err=err.substring(0,err.lastIndexOf(","));
			        						MessageDialog.openMessageDialog(null, "������룺"+vDto.getStrecode()+" ƾ֤����: "+vDto.getSvtcode()+" \""+stampDto.getSstampname()+"\"ǩ��ǰ���� \""+err+"\"ǩ�£�");
	            			    			
	            			    			return "";
			        					}
    			        			}
    			        		
    							}
    						}
						}
						if(stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){
							if(!voucherLoadService.getOfficialStamp().equals(MsgConstant.VOUCHER_OFFICIALSTAMP)){								
								sinList.add(ActiveXCompositeVoucherDivideAndTaxantiReportOcx.getVoucherStamp(vDto, tDto.getScertid(), sDto.getSstampposition(), tDto.getSstampid()));   	    												
							}
						}else if(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)){
							if(!voucherLoadService.getOfficialStamp().equals(MsgConstant.VOUCHER_ROTARYSTAMP)){								
								sinList.add(ActiveXCompositeVoucherDivideAndTaxantiReportOcx.getVoucherStamp(vDto, tDto.getSrotarycertid(), sDto.getSstampposition(), tDto.getSrotaryid()));   	    												
							}
						}else if(stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH))
						{
							if(!voucherLoadService.getOfficialStamp().equals(MsgConstant.VOUCHER_ROTARYSTAMP))
								sinList.add(ActiveXCompositeVoucherDivideAndTaxantiReportOcx.getVoucherStamp(vDto, tDto.getSattachcertid(), sDto.getSstampposition(), tDto.getSattachid()));
						}else{
							if(!loginInfo.getPublicparam().contains(",jbrstamp=server,"))
							{
								sinList.add(ActiveXCompositeVoucherDivideAndTaxantiReportOcx.getVoucherStamp(vDto, uDto.getScertid(), sDto.getSstampposition(),uDto.getSstampid()));
							}
						}
    					vList.add(sinList);
    				}
    			}
    			list.add(uDto);
    			list.add(tDto);
    			list.add(sDto);
    			list.add(sList.size());
    			list.add(vList);    			
    			lists.add(list);
    		}
    		count=voucherLoadService.voucherStamp(lists);
    		if(ocxflag){
    			
    			return count+"";
    		}
    		MessageDialog.openMessageDialog(null, "ƾ֤ǩ��   "+checkList.size()+" �����ɹ�����Ϊ��"+count+" ��");
    		refreshTable();
    	}catch (ITFEBizException e) {
    		log.error(e);    		
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e);
			return "";
			
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch(Exception e){
			log.error(e);
			Exception e1=new Exception("ǩ�²��������쳣��",e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			return "";
			
		}		
        return super.voucherStamp(o);
    }
    
    /**
	 * Direction:ǩ�³���
	 * ename: transferCompletedStampCancle
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherStampCancle(Object o){
    	int count=0;        	
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "��ѡ��Ҫǩ�³����ļ�¼��");
    		return "";
    	}
    	if ((null == stamp || stamp.trim().length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ��ǩ�����ͣ�");
			return null;
		}
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��Ҫ��ѡ�еļ�¼ִ��ǩ�³���������")) {
			return "";
		}
    	for(TvVoucherinfoDto infoDto:checkList){
	    		if(!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto.getSstatus()))&&!(DealCodeConstants.VOUCHER_STAMP.equals(infoDto.getSstatus()))){
	    			MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬Ϊ����ɹ���ǩ�³ɹ��ļ�¼��");
	    			
	        		return "";
	    		}
	    	}
		
    	try {
    		if(!((TvVoucherinfoDto)checkList.get(0)).getSvtcode().equals(dto.getSvtcode())){
    			MessageDialog.openMessageDialog(null, "ѡ��Ĳ���[ƾ֤����]��У�����ݲ�����");
    			
    			return "";
        		
    		}
   
    		TsUsersDto uDto=new TsUsersDto();
    		uDto.setSorgcode(loginInfo.getSorgcode());
    		uDto.setSusercode(loginInfo.getSuserCode());
    		uDto=(TsUsersDto) commonDataAccessService.findRsByDto(uDto).get(0);
    		TsStamppositionDto stampDto=new TsStamppositionDto();
    		stampDto.setSvtcode(dto.getSvtcode());
    		stampDto.setSorgcode(loginInfo.getSorgcode());
    		stampDto.setSstamptype(stamp);
    		stampDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(stampDto).get(0);
    		String permission= uDto.getSstamppermission();
    		boolean flag=true;
    		if(permission==null||permission.equals("")){
    			flag=false;
    		}else{
    			if(permission.indexOf(",")<0){
        			if(!permission.equals(stamp)){
        				flag=false;
        			}
        			
        		}else{
        			flag=false;
        			String[] permissions=permission.split(",");
        			for(int i=0;i<permissions.length;i++){
        				if(permissions[i].equals(stamp)){
        					flag=true;
        					break;
        				}
        			}
        			
        		}
    		}
    		boolean managerPermission=false;
    		if(flag==false){
				MessageDialog.openMessageDialog(null, "��ǰ�û���  \""+stampDto.getSstampname()+"\"  ǩ��Ȩ�ޣ�����ͨ��������Ȩ����ǩ��");
				String msg = "��Ҫ������Ȩ���ܲ��ܳ���ǩ�£�";
				if(!AdminConfirmDialogFacade.open("B_247", "���뱨��ƾ֤", "��Ȩ�û�"+loginInfo.getSuserName()+"����ǩ��", msg)){
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					return null;
				}else{
					managerPermission=true;
				}
				
			}
    		TsTreasuryDto tDto=new TsTreasuryDto();
    		TsStamppositionDto sDto=new TsStamppositionDto();
    		Map map=new HashMap();
    		String usercode=uDto.getSusercode();
//    		String stampuser="";
    		String stampid="";    		
    		for(TvVoucherinfoDto vDto:checkList){
    			usercode=uDto.getSusercode();
    			map.put(vDto.getStrecode(), "");
    			stampid=vDto.getSstampid();
    			int j=-1;
    			if(stampid==null||stampid.equals("")){
    				flag=false;
    			}else{
    				flag=false;
					String[] stampids=stampid.split(",");
					for(int i=0;i<stampids.length;i++){
						if(stamp.equals(stampids[i])){
							flag=true;
							j=i;
							break;
							
						}
					}
    			
    			}if(flag==false){
					MessageDialog.openMessageDialog(null, "ƾ֤��ţ�"+vDto.getSvoucherno()+" δǩ  \""+stampDto.getSstampname()+"\" ��");	    			
	    			return "";
				}
    			if(managerPermission==false){
    				if(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)){
						usercode=usercode+"!";
					}else if(stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){
						usercode=usercode+"#";
					}
    				String stampuserboolean=vDto.getSstampuser().split(",")[j];
    				if(!stampuserboolean.equals(usercode)){
    					MessageDialog.openMessageDialog(null, "ƾ֤��ţ�"+vDto.getSvoucherno()+"   \""+stampDto.getSstampname()+"\" ���ǵ�ǰ�û���ǩ����ͨ��������Ȩ����ǩ��");
    					String msg = "��Ҫ������Ȩ���ܲ��ܳ���ǩ�£�";
    					if(!AdminConfirmDialogFacade.open("B_247", "���뱨��ƾ֤", "��Ȩ�û�"+loginInfo.getSuserName()+"����ǩ��", msg)){
    						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    						return null;
    					}else{
    						managerPermission=true;
    					}    				
    				}   				
    			}
    		}
    		
    		Iterator it=map.keySet().iterator();
    		
    		List lists=new ArrayList();
    		List list=null;
    		List sinList=null;
    		List<TsStamppositionDto> sList=null;
    		List vList=null;
    		
    		String strecode="";
//    		String xml="";
    		while(it.hasNext()){
    			 strecode=it.next()+"";
    			 vList=new ArrayList<TvVoucherinfoDto>();
    			 tDto=new TsTreasuryDto();
    			 sDto=new TsStamppositionDto();
    			 sList=new ArrayList<TsStamppositionDto>();
    			 list=new ArrayList();
    			try{
    				tDto.setSorgcode(loginInfo.getSorgcode());
    				tDto.setStrecode(strecode);
        			tDto=(TsTreasuryDto) commonDataAccessService.findRsByDto(tDto).get(0);
    			}catch(Exception e){
    				log.error(e);
    				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ�����в����ڣ�");
    				
    				return "";
    			}
    			
    			try{
    				sDto.setSorgcode(loginInfo.getSorgcode());
        			sDto.setStrecode(strecode);
        			sDto.setSvtcode(dto.getSvtcode());
        			sList=(List<TsStamppositionDto>) commonDataAccessService.findRsByDto(sDto);
        			sDto.setSstamptype(stamp);
        			sDto= (TsStamppositionDto) commonDataAccessService.findRsByDto(sDto).get(0);
        			
    			}catch(Exception e){
    			
    				log.error(e);
    				MessageDialog.openMessageDialog(null, "����������룺"+strecode+" "+stampDto.getSstampname()+" ����δά���� ");
    				
    				return "";
    			}
    			
    			if(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)){
    				if(tDto.getSrotarycertid()==null||tDto.getSrotarycertid().equals("")){
        				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ������ "+stampDto.getSstampname()+"֤��ID ����δά���� ");
        				
        				return "";
        			}else if(tDto.getSrotaryid()==null||tDto.getSrotaryid().equals("")){
        				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ������ "+stampDto.getSstampname()+"ӡ��ID ����δά���� ");
        				
        				return "";
        			}
    			}else if(stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){

    				if(tDto.getScertid()==null||tDto.getScertid().equals("")){
        				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ������ "+stampDto.getSstampname()+"֤��ID ����δά���� ");
        				
        				return "";
        			}else if(tDto.getSstampid()==null||tDto.getSstampid().equals("")){
        				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ������ "+stampDto.getSstampname()+"ӡ��ID ����δά���� ");
        				
        				return "";
        			}
    			
    			}else{
    				if(uDto.getScertid()==null||uDto.getScertid().equals("")){
        				MessageDialog.openMessageDialog(null, "��ǰ�û�  "+stampDto.getSstampname()+"  ֤��ID����δά���� ");
        				
        				return "";
        			}else if(uDto.getSstampid()==null||uDto.getSstampid().equals("")){
        				MessageDialog.openMessageDialog(null, "��ǰ�û�   "+stampDto.getSstampname()+"  ӡ��ID����δά���� ");
        				
        				return "";
        			}
    		    			
    			}
 
    			for(TvVoucherinfoDto vDto:checkList){
    				if(vDto.getStrecode().equals(strecode)){
    					
    					sinList=new ArrayList();
    					sinList.add(vDto);
						stampid=vDto.getSstampid();	
						String seq=sDto.getSstampsequence();   						
						if(seq!=null&&!seq.equals("")){
							List<String> seqList=new ArrayList<String>();
    						for(int i=0;i<sList.size();i++){
    							TsStamppositionDto tsDto=(TsStamppositionDto) sList.get(i);
    							if(tsDto.getSstampsequence()!=null&&!tsDto.getSstampsequence().equals("")){
    								seqList.add(tsDto.getSstampsequence());
    							}
    						}
    						if(seqList!=null&&seqList.size()>0){
    							String[] seqs=seqList.toArray(new String[seqList.size()]);
    							ActiveXCompositeVoucherDivideAndTaxantiReportOcx.sortStringArray(seqs);
    							
    							String temp="";
    							for(int i=0;i<seqs.length;i++){
    								if(Integer.parseInt(seqs[i])>Integer.parseInt(seq)){
    									temp=seqs[i];
    									break;
    								}
    							}
    							if(!temp.equals("")){
    								List<TsStamppositionDto> tsList=new ArrayList<TsStamppositionDto>();
    								TsStamppositionDto tsDto=new TsStamppositionDto();
    								tsDto.setSorgcode(loginInfo.getSorgcode());
    								tsDto.setStrecode(strecode);
    								tsDto.setSvtcode(vDto.getSvtcode());
    								tsDto.setSstampsequence(temp);
    			        			tsList=(List<TsStamppositionDto>) commonDataAccessService.findRsByDto(tsDto);
    			        			String err="";
    			      
			        				String[] stampids=stampid.split(",");
//		        					List<TsStamppositionDto> tsList1=new ArrayList<TsStamppositionDto>();
		        					for(int j=0;j<tsList.size();j++){
		        						for(int i=0;i<stampids.length;i++){
			        						if(stampids[i].equals(tsList.get(j).getSstamptype())){
			        							err=tsList.get(j).getSstampname()+" "+err;
			        						}
			        					}
	        						}
		        					if(!err.trim().equals("")){
		        						for(TsStamppositionDto tstampDto:tsList){
	    			        				err=tstampDto.getSstampname()+" , "+err;
	    			        			}
		        						err=err.substring(0,err.lastIndexOf(","));
		        						MessageDialog.openMessageDialog(null, "������룺"+vDto.getStrecode()+" ƾ֤����: "+vDto.getSvtcode()+" \""+stampDto.getSstampname()+"\"����ǩ��ǰ���ȳ��� \""+err+"\"ǩ�£�");
            			    			
            			    			return "";
		        					}    			        		
    							}
    						}
						}
						int j=-1;
						String[] stampids=stampid.split(",");
						for(int i=0;i<stampids.length;i++){
							if(stamp.equals(stampids[i])){								
								j=i;
								break;
								
							}
						}
						TsUsersDto userDto=new TsUsersDto();
						userDto.setSorgcode(loginInfo.getSorgcode());
						String user=vDto.getSstampuser().split(",")[j];
						
						userDto.setSusercode(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)?user.substring(0, (user.length()-1)):(stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)?user.substring(0, (user.length()-1)):user));
						userDto=(TsUsersDto) commonDataAccessService.findRsByDto(userDto).get(0);
						sinList.add(userDto);
						vList.add(sinList);
    				}
    			}
    			
    			list.add(tDto);
    			list.add(sDto);
    			
    			list.add(vList);
    			
    			lists.add(list);
    		}
    		count=voucherLoadService.voucherStampCancle(lists);
    		MessageDialog.openMessageDialog(null, "ƾ֤����ǩ��   "+checkList.size()+" �����ɹ�����Ϊ��"+count+" ��");
    		refreshTable();
    	}catch (ITFEBizException e) {
    		log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e);
			return "";
			
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch(Exception e){
			log.error(e);
			Exception e1=new Exception("ǩ�²������������쳣��",e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			return "";
			
		}
        return super.voucherStampCancle(o);
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
//    	int count=0;
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
    

    public List<Mapper> getStampEnums(TvVoucherinfoDto vDto){
		List<Mapper> maplist = new ArrayList<Mapper>();
		
		List<TsStamppositionDto> enumList = new ArrayList<TsStamppositionDto>();
		TsStamppositionDto tDto=new TsStamppositionDto();
		tDto.setSorgcode(vDto.getSorgcode());
		tDto.setStrecode(vDto.getStrecode());
		tDto.setSvtcode(vDto.getSvtcode());
		try {
			enumList =commonDataAccessService.findRsByDto(tDto);
			if(enumList!=null&&enumList.size()>0){
				for (TsStamppositionDto emuDto : enumList) {
					Mapper map = new Mapper(emuDto.getSstamptype(), emuDto.getSstampname());
					maplist.add(map);
				}
			}
			
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("��ȡƾ֤ǩ���б�����쳣��",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
		}		
		return maplist;
	}
    public String getVoucherXMl(TvVoucherinfoDto vDto) throws ITFEBizException{
    	
    	return voucherLoadService.voucherStampXml(vDto);
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
				MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬Ϊ \"ǩ�³ɹ�\" �ļ�¼��");
        		return "";
				}
	    	}		
    	try {   		
    		 count=voucherLoadService.voucherReturnSuccess(checkList);
    		 MessageDialog.openMessageDialog(null, "���͵���ƾ֤   "+checkList.size()+" �����ɹ�����Ϊ��"+count+" ��");
    		 refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("���͵���ƾ֤����������쳣��",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
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

	public String getSvtcode() {
		return svtcode;
	}

	public void setSvtcode(String svtcode) {
		this.svtcode = svtcode;
		dto.setSvtcode(svtcode);
		if(MsgConstant.VOUCHER_NO_3405.equals(svtcode))
		{
			List contreaNames = new ArrayList();
			contreaNames.add("��˰������������");
			contreaNames.add("��˰������ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames, false);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("�ܶ�ֳɱ�������");
			contreaNames1.add("�ܶ�ֳɲ�ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, true);
			dto.setShold4("8888888888");
		}else
		{
			List contreaNames = new ArrayList();
			contreaNames.add("��˰������������");
			contreaNames.add("��˰������ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames, true);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("�ܶ�ֳɱ�������");
			contreaNames1.add("�ܶ�ֳɲ�ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
			setShold4(null);
		}
		checkList.clear();
		PageResponse page = new PageResponse();
		pagingcontext.setPage(page);
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}

	public String getShold4() {
		if(StringUtils.isBlank(shold4)||StringUtils.isEmpty(shold4))
		{
			try {
				TvUsersconditionDto userScondto = new TvUsersconditionDto();
				userScondto.setSorgcode(loginInfo.getSorgcode());
				userScondto.setSusercode(loginInfo.getSuserCode());
				List<TvUsersconditionDto> usersconList = commonDataAccessService.findRsByDto(userScondto);
				if(usersconList!=null&&usersconList.size()>0)
				{
					shold4 = usersconList.get(0).getSconditions();
					dto.setShold4(shold4);
				}
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "�õ������û���Ŀ�������");
			}
		}
		return shold4;
	}

	public void setShold4(String shold4) {
		
		if(shold4!=null&&!"".equals(shold4)&&MsgConstant.VOUCHER_NO_3406.equals(svtcode))
		{
			try {
				TvUsersconditionDto userScondto = new TvUsersconditionDto();
				userScondto.setSorgcode(loginInfo.getSorgcode());
				userScondto.setSusercode(loginInfo.getSuserCode());
				List searList = commonDataAccessService.findRsByDto(userScondto);
				if(searList!=null&&searList.size()>0)
				{
					userScondto = (TvUsersconditionDto)searList.get(0);
					if(!shold4.equals(userScondto.getSconditions()))
					{
						userScondto.setSconditions(shold4);
						commonDataAccessService.updateData(userScondto);
					}
				}else
				{
					userScondto.setSusername(loginInfo.getSuserName());
					userScondto.setSconditions(shold4);
					commonDataAccessService.create(userScondto);
				}
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "�����û���Ŀ�������");
			}
		}else
		{
			if(MsgConstant.VOUCHER_NO_3406.equals(svtcode))
			{
				Map<String,String> getMap = null;
				try {
					getMap = commonDataAccessService.getmapforkey(loginInfo.getSorgcode());
				} catch (ITFEBizException e) {
					log.error(e);
					MessageDialog.openMessageDialog(null, "�õ������û���Ŀ�������");
				}
				if(getMap!=null&&getMap.get(loginInfo.getSuserCode())!=null)
					shold4 = getMap.get(loginInfo.getSuserCode());
			}else
			{
				if(!"7777777777".equals(shold4)&&!"8888888888".equals(shold4)&&!"9999999999".equals(shold4))
				{
					MessageDialog.openMessageDialog(null, "�ܶ�ֳɱ����Ŀ����ֻ����7777777777��8888888888��9999999999!");
					shold4="8888888888";
				}
			}
		}
		dto.setShold4(shold4);
		this.shold4 = shold4;
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
    public TvVoucherinfoDto getDto(TvVoucherinfoDto dto) throws ITFEBizException{
    	TvVoucherinfoPK pk=new TvVoucherinfoPK();
    	pk.setSdealno(dto.getSdealno());    	   	
    	return (TvVoucherinfoDto) commonDataAccessService.find(pk);
    }
    /**
	 * Direction: �ص�״̬��ѯ ename: queryStatusReturnVoucher ���÷���: viewers: *
	 * messages:
	 */
	public String queryStatusReturnVoucher(Object o) {
		int count = 0;
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�鿴�ص�״̬�ļ�¼��");
			return "";
		}
		if (checkdbstatus() == 1) {
			MessageDialog.openMessageDialog(null,
					"ѡ��ļ�¼��ƾ֤״̬�����ݿⲻһ��,�����²�ѯ���ٽ��в�����");
			return "";
		}
		for (TvVoucherinfoDto infoDto : checkList) {
			if (!(DealCodeConstants.VOUCHER_SUCCESS.equals(infoDto.getSstatus().trim())||DealCodeConstants.VOUCHER_SUCCESS_BACK.equals(infoDto.getSstatus()))) {
				MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬Ϊ�ص��ɹ�������Ʊ�ɹ��ļ�¼��");
				return "";
			}
		}
		try {
			count = voucherLoadService.queryStatusReturnVoucher(checkList);
			MessageDialog.openMessageDialog(null, "ƾ֤�鿴�ص�״̬" + checkList.size()
					+ " �����ɹ�����Ϊ��" + count + " , ��鿴��");
			refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("ƾ֤�鿴�ص�״̬���������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return "";
	}
	// �ж��û�ѡ��ļ�¼�Ƿ������ݿ���״̬һ�£������һ�£�����ʾ�û����²�ѯ���ٽ��в���
	// �����û�ѡ����������²�ѯ���ݿ⣬�����ȶ��û�״̬
	public int checkdbstatus() {
		int i = 0;
		try {
			TvVoucherinfoDto finddto = new TvVoucherinfoDto();
    		finddto.setSvtcode(dto.getSvtcode());
    		finddto.setShold3(dto.getShold3());
    		finddto.setStrecode(dto.getStrecode());
    		finddto.setSstatus(dto.getSstatus());
    		if(loginInfo.getPublicparam().contains(("divideandtaxant=searchbudget")))
    			finddto.setShold4(dto.getShold4());
    		finddto.setScheckdate(dto.getScheckdate());
    		finddto.setSext1(dto.getSext1());//Ԥ�㼶��
    		if(MsgConstant.VOUCHER_NO_3406.equals(dto.getSvtcode()))
    		{
    			finddto.setSpaybankcode(MsgConstant.MSG_TAXORG_SHARE_CLASS);//���ջ��ش���
    			finddto.setSattach(MsgConstant.RULE_SIGN_ALL);//Ͻ����־
    			finddto.setShold1(dto.getShold1());
    			finddto.setShold2(dto.getShold2());
    		}
    		StringBuffer orsql = null;
    		if(finddto.getShold3()!=null&&!"0".equals(finddto.getShold3()))
    		{
    			TsTreasuryDto tDto=new TsTreasuryDto();
				tDto.setSgoverntrecode(dto.getStrecode());
				List<TsTreasuryDto> trelist = commonDataAccessService.findRsByDto(tDto);
				if(trelist!=null&&trelist.size()>0)
				{
					orsql = new StringBuffer("(S_TRECODE = '"+finddto.getStrecode()+"'");
					finddto.setStrecode(null);
					for(TsTreasuryDto tsDto:trelist)
					{
						orsql.append(" or S_TRECODE = '"+tsDto.getStrecode()+"'");
					}
					orsql.append(")");
				}
    		}
			List<TvVoucherinfoDto> queryList = commonDataAccessService.findRsByDtoWithWhere(finddto, " and 1=1"+(orsql==null?"":" and "+orsql.toString()));
			if (queryList.size() > 0) {
				HashMap<String, String> map = new HashMap<String, String>();
				for (TvVoucherinfoDto tmp : queryList) {
					if (!map.containsKey(tmp.getSdealno())) {
						map.put(tmp.getSdealno(), tmp.getSstatus());
					}
				}
				for (TvVoucherinfoDto tmp : checkList) {
					if (map.containsKey(tmp.getSdealno())
							&& tmp.getSstatus().equals(
									map.get(tmp.getSdealno()))) {
						continue;
					} else {
						i++;
						break;
					}
				}
			} else {
				return 1;
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return 1;
		}
		return i;
	}
}
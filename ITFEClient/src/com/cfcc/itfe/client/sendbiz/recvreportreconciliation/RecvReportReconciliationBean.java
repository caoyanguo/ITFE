package com.cfcc.itfe.client.sendbiz.recvreportreconciliation;

import itferesourcepackage.ReportBussReadReturnEnumFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositeRecvReconciliationOcx;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositeRecvReconciliationReturnOcx;
import com.cfcc.itfe.client.sendbiz.sendreportreconciliation.ReportDefraySubBean;
import com.cfcc.itfe.client.sendbiz.sendreportreconciliation.ReportDepositSubBean;
import com.cfcc.itfe.client.sendbiz.sendreportreconciliation.ReportIncomeSubBean;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfReportDefrayMainDto;
import com.cfcc.itfe.persistence.dto.TfReportDefraySubDto;
import com.cfcc.itfe.persistence.dto.TfReportDepositMainDto;
import com.cfcc.itfe.persistence.dto.TfReportDepositSubDto;
import com.cfcc.itfe.persistence.dto.TfReportIncomeMainDto;
import com.cfcc.itfe.persistence.dto.TfReportIncomeSubDto;
import com.cfcc.itfe.persistence.dto.TsStamppositionDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.pk.TvVoucherinfoPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * codecomment: 
 * @author zhangliang
 * @time   15-01-16 10:28:33
 * ��ϵͳ: SendBiz
 * ģ��:recvReportReconciliation
 * ���:RecvReportReconciliation
 */
@SuppressWarnings("unchecked")
public class RecvReportReconciliationBean extends AbstractRecvReportReconciliationBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(RecvReportReconciliationBean.class);
    private ReportIncomeSubBean incomeSubBean;
    private ReportDefraySubBean defraySubBean;
    private ReportDepositSubBean depositSubBean;
    
    List<TvVoucherinfoDto> checkList=null;   
    List<TvVoucherinfoDto> returnList=null;
    private List acctList = null;
	//�û���¼��Ϣ
	private ITFELoginInfo loginInfo;
	private String stamp;
	private List stampList=null;
	private String voucherType=null;
	private List voucherTypeList=null;
	private Map paramMap = null;
	private String ysjc;
	private String vouchertype=MsgConstant.VOUCHER_NO_3513;//ƾ֤����3511-���뱨����ˣ�3512-֧��������ˣ�3513-����˻�����
    public RecvReportReconciliationBean() {
    	super();
    	incomeSubBean = new ReportIncomeSubBean();
    	defraySubBean = new ReportDefraySubBean();
    	depositSubBean = new ReportDepositSubBean();
    	incomeMainDto = new TfReportIncomeMainDto();
        defrayMainDto = new TfReportDefrayMainDto();
        depositMainDto = new TfReportDepositMainDto();
        dto = new TvVoucherinfoDto();
        loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
        checkList=new ArrayList();
        paramMap = new HashMap();
        dto.setSorgcode(loginInfo.getSorgcode());
        pagingcontext = new PagingContext(this); 
        dto.setSvtcode(vouchertype);//ƾ֤����3511-���뱨����ˣ�3512-֧��������ˣ�3513-����˻�����
        dto.setScheckdate(TimeFacade.getCurrentStringTime());//������ʼ����
        dto.setSpaybankcode(TimeFacade.getCurrentStringTime());//�����ֹ����
		dto.setSattach("1");//���ջ��ش���1-���֣�2-�����������ջ��أ�0-��������(444444444444)
		dto.setShold3("0");//Ͻ����־0-������1-ȫϽ
//		dto.setShold2();//Ԥ�㼶��1-���룬2-ʡ��3-�У�4-�أ�5-��
		dto.setScheckvouchertype("1");//��������1
        TsTreasuryDto tsTreasuryDto = new TsTreasuryDto();
  	  	tsTreasuryDto.setSorgcode(loginInfo.getSorgcode());
  	  	List<TsTreasuryDto> list = null;
	  	  try {
	  		list = (List<TsTreasuryDto>)commonDataAccessService.findRsByDto(tsTreasuryDto);
//	  		acctList = new ArrayList();
//	  		Map<String,TsInfoconnorgaccDto> getMap=ServiceUtil.getAcctCache();
//	  		Mapper map = null;
//	  		TsInfoconnorgaccDto tempdto = null;
//	  		for(String mapkey:getMap.keySet())
//	  		{
//	  			if(mapkey.indexOf(loginInfo.getSorgcode())>=0)
//	  			{
//	  				tempdto = getMap.get(mapkey);
//	  				map = new Mapper(tempdto.getSpayeraccount(), tempdto.getSpayername());
//	  				acctList.add(map);
//	  			}
//	  		}
	  	  } catch (ITFEBizException e){
	  		  log.error(e);
	  	  }
	  	  if(list!=null && list.size() > 0){
	  		  dto.setStrecode(list.get(0).getStrecode());//�������
	  	  }
        dto.setSvtcode(vouchertype);
  	  	refreshStampType("");           
    }
    /**
	 * Direction: ����ƾ֤
	 * ename: voucherGenerator
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherGenerator(Object o){
    	try {
			if(checkList!=null&&checkList.size()>0)
			{
				TvVoucherinfoDto vDto = null;
				boolean isgen = false;
				for(int i=0;i<checkList.size();i++)
				{
					vDto = (TvVoucherinfoDto)checkList.get(i);
					if(!DealCodeConstants.VOUCHER_RECEIVE_SUCCESS.equals(vDto.getSstatus())&&!DealCodeConstants.VOUCHER_VALIDAT_SUCCESS.equals(vDto.getSstatus()))
					{
						isgen = true;
						break;
					}
					if(MsgConstant.VOUCHER_NO_3507.equals(vDto.getSvtcode()))
					{
						if(vDto.getSext1()!=null&&"2".equals(vDto.getSext1()))
							vDto.setSvtcode(MsgConstant.VOUCHER_NO_5507);
						else
							vDto.setSvtcode(MsgConstant.VOUCHER_NO_2507);
					}else if(MsgConstant.VOUCHER_NO_3508.equals(vDto.getSvtcode()))
					{
						vDto.setSvtcode(MsgConstant.VOUCHER_NO_5508);
					}else if(MsgConstant.VOUCHER_NO_3509.equals(vDto.getSvtcode()))
					{
						vDto.setSvtcode(MsgConstant.VOUCHER_NO_5509);
					}else if(MsgConstant.VOUCHER_NO_3510.equals(vDto.getSvtcode()))
					{
						vDto.setSvtcode(MsgConstant.VOUCHER_NO_5510);
					}else if(MsgConstant.VOUCHER_NO_3511.equals(vDto.getSvtcode()))
					{
						vDto.setSvtcode(MsgConstant.VOUCHER_NO_5511);
					}else if(MsgConstant.VOUCHER_NO_3512.equals(vDto.getSvtcode()))
					{
						vDto.setSvtcode(MsgConstant.VOUCHER_NO_5512);
					}else if(MsgConstant.VOUCHER_NO_3513.equals(vDto.getSvtcode()))
					{
						vDto.setSvtcode(MsgConstant.VOUCHER_NO_5513);
					}
				}
				if(isgen)
				{
					MessageDialog.openMessageDialog(null, "ѡ�е������а����������ɻص������ݣ�");
					return null;
				}
			}else
			{
				MessageDialog.openMessageDialog(null, "��ѡ����Ҫ���ɻص���ƾ֤��");
				return null;
			}
			List resultList = voucherLoadService.voucherGenerate(checkList);
			if (resultList != null && resultList.size() > 0) {
				MessageDialog.openMessageDialog(null, "�ص����ɲ����ɹ����ɹ�����Ϊ��" + resultList.get(0)
						+ "��");
			}
			refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1 = new Exception(e.getMessage(), e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("����ƾ֤���������쳣��", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
        return super.voucherGenerator(o);
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
    public String voucherSearch(Object o){
    	dto.setSorgcode(loginInfo.getSorgcode());
    	dto.setSvtcode(vouchertype);//ƾ֤����
    	if(dto.getStrecode()==null||dto.getStrecode().equals("")){
    		MessageDialog.openMessageDialog(null, "������벻��Ϊ�գ�");
    		return "";
    	}
//    	if(MsgConstant.VOUCHER_NO_3507.equals(vouchertype))
//    	{
//    		if(dto.getScheckdate()==null||dto.getScheckdate().equals("")){
//	    		MessageDialog.openMessageDialog(null, "�����ꡢ�²���Ϊ�գ�");
//	    		return "";
//	    	}else if(dto.getScheckdate().length()!=6)
//	    	{
//	    		MessageDialog.openMessageDialog(null, "�����ꡢ�¸�ʽΪ6λ(���4λ,�·���λ)��");
//	    		return "";
//	    	}
//    	}else
//    	{
//	    	if(dto.getScheckdate()==null||dto.getScheckdate().equals("")){
//	    		MessageDialog.openMessageDialog(null, "�������ڲ���Ϊ�գ�");
//	    		return "";
//	    	}
//    	}
    	if(MsgConstant.VOUCHER_NO_3404.equals(vouchertype))
    	{
	    	if(dto.getSpaybankcode()==null||dto.getSpaybankcode().equals("")){
	    		MessageDialog.openMessageDialog(null, "���ջ��ز���Ϊ�գ�");
	    		return "";
	    	}
	    	TsTreasuryDto tDto=new TsTreasuryDto();
			tDto.setSorgcode(loginInfo.getSorgcode());
			tDto.setStrecode(loginInfo.getSorgcode().substring(0,10));				
			try {
				List treList = commonDataAccessService.findRsByDto(tDto);
				if(treList!=null&&treList.size()>0)
				{
					tDto = (TsTreasuryDto)treList.get(0);
					if(!"2".equals(tDto.getStrelevel()))
					{
						MessageDialog.openMessageDialog(null, "�˹���ֻ��ʡ������������ʹ�ã�");
				    	return "";
					}
				}else
				{
					MessageDialog.openMessageDialog(null, "�˹���ֻ��ʡ������������ʹ�ã�");
			    	return "";
				}
			} catch (ITFEBizException e) {
				MessageDialog.openMessageDialog(null, "�ж��Ƿ�ʡ�������쳣��");
		    	return "";
			}
    	}
    	if(MsgConstant.VOUCHER_NO_3401.equals(vouchertype)||MsgConstant.VOUCHER_NO_3507.equals(vouchertype))
    	{
    		if(dto.getShold2()!=null&&("0".equals(dto.getShold2())))
        	{
        		MessageDialog.openMessageDialog(null, "Ԥ�㼶�β�֧�ֹ���");
        		return "";
        	}
//    		if(dto.getSattach()==null||dto.getSattach().equals("")){
//        		MessageDialog.openMessageDialog(null, "�������Ͳ���Ϊ�գ�");
//        		return "";
//        	}
    		if(dto.getShold2()!=null&&"6".equals(dto.getShold2()))
    		{
    			dto.setShold1(null);//Ԥ������
    			dto.setScheckvouchertype(null);//��������
    			dto.setShold3(MsgConstant.RULE_SIGN_ALL);//Ͻ����־
    			dto.setSpaybankcode("000000000000");//���ջ���
    		}else
    		{
	        	if(dto.getShold1()==null||dto.getShold1().equals("")){
	        		MessageDialog.openMessageDialog(null, "Ԥ�����಻��Ϊ�գ�");
	        		return "";
	        	}
	        	if(dto.getShold3()==null||dto.getShold3().equals("")){
	        		MessageDialog.openMessageDialog(null, "Ͻ����־����Ϊ�գ�");
	        		return "";
	        	}else if("3".equals(dto.getShold3()))
	        	{
	        		MessageDialog.openMessageDialog(null, "Ͻ����־��֧��ȫϽ�Ǳ�����");
	        		return "";
	        	}
	        	if(dto.getScheckvouchertype()!=null&&("5".equals(dto.getScheckvouchertype())||"9".equals(dto.getScheckvouchertype())))
	        	{
	        		MessageDialog.openMessageDialog(null, "�������಻֧�ֹ���ֳɱ���");
	        		return "";
	        	}
    		}
    	}
    	if(dto.getSvtcode()==null||dto.getSvtcode().equals("")){
    		MessageDialog.openMessageDialog(null, "����ѡ��ƾ֤���ͣ�");
    		return "";
    	}
    	
    	refreshTable();
        return super.voucherSearch(o);
    }
    
    private void init() {
    	dto.setSvtcode(vouchertype);//ƾ֤����
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
    	List newcheckList = new ArrayList();
    	for(TvVoucherinfoDto vDto:this.checkList){
    		try {
    			newcheckList.add(getDto(vDto));
			} catch (ITFEBizException e) {
				Exception e1=new Exception("���²�ѯ���ݳ��ִ���",e);			
				MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
			}
			
    	}
    	checkList.clear();
    	checkList.addAll(newcheckList);
    	for(TvVoucherinfoDto infoDto:checkList)
    	{
    		if (loginInfo.getPublicparam().indexOf(",stampmode=sign") >= 0) {
				if (!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto
						.getSstatus().trim()))) {
					MessageDialog.openMessageDialog(null,
							"��ѡ��ƾ֤״̬Ϊ \"����ɹ�\" �ļ�¼��");
					return "";
				}
			} else {
				if (!(DealCodeConstants.VOUCHER_STAMP.equals(infoDto
						.getSstatus().trim()))) {
					MessageDialog.openMessageDialog(null,
							"��ѡ��ƾ֤״̬Ϊ \"ǩ�³ɹ�\" �ļ�¼��");
					return "";
				}
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
    		ActiveXCompositeRecvReconciliationOcx.init(0);
        	
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
	 * Direction: �ص���ԭչʾ
	 * ename: returnVoucherView
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String returnVoucherView(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�鿴�ļ�¼��");
    		return "";
    	}
    	try{
    		if(returnList==null)
    			returnList = new ArrayList<TvVoucherinfoDto>();
	    	TvVoucherinfoDto fdto = new TvVoucherinfoDto();
	    	List tempList = null;
	    	for(TvVoucherinfoDto vdto:checkList)
	    	{
	    		fdto.setSext1(vdto.getSdealno());
	    		if(MsgConstant.VOUCHER_NO_3511.equals(vdto.getSvtcode()))
	    		{
	    			fdto.setSvtcode(MsgConstant.VOUCHER_NO_5511);
	    			tempList = commonDataAccessService.findRsByDto(fdto);
	    			if(tempList!=null&&tempList.size()>0)
	    				returnList.addAll(tempList);
	    		}else if(MsgConstant.VOUCHER_NO_3512.equals(vdto.getSvtcode()))
	    		{
	    			fdto.setSvtcode(MsgConstant.VOUCHER_NO_5512);
	    			tempList = commonDataAccessService.findRsByDto(fdto);
	    			if(tempList!=null&&tempList.size()>0)
	    				returnList.addAll(tempList);
	    		}else if(MsgConstant.VOUCHER_NO_3513.equals(vdto.getSvtcode()))
	    		{
	    			fdto.setSvtcode(MsgConstant.VOUCHER_NO_5513);
	    			tempList = commonDataAccessService.findRsByDto(fdto);
	    			if(tempList!=null&&tempList.size()>0)
	    				returnList.addAll(tempList);
	    		}
	    	}
	    	if(returnList==null||returnList.size()==0){
	    		MessageDialog.openMessageDialog(null, "ѡ��ļ�¼û���յ��Է��Ļص���");
	    		return "";
	    	}
    		ActiveXCompositeRecvReconciliationReturnOcx.init(0);
        	
    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch(Exception e){
    		log.error(e);
    		Exception e1=new Exception("ƾ֤�鿴�쳣��",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
    	}
        return super.returnVoucherView(o);
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
    			if(!stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)&&!stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){
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
    							ActiveXCompositeRecvReconciliationReturnOcx.sortStringArray(seqs);    							
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
								sinList.add(ActiveXCompositeRecvReconciliationReturnOcx.getVoucherStamp(vDto, tDto.getScertid(), sDto.getSstampposition(), tDto.getSstampid()));   	    												
							}
						}else if(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)){
							if(!voucherLoadService.getOfficialStamp().equals(MsgConstant.VOUCHER_ROTARYSTAMP)){								
								sinList.add(ActiveXCompositeRecvReconciliationReturnOcx.getVoucherStamp(vDto, tDto.getSrotarycertid(), sDto.getSstampposition(), tDto.getSrotaryid()));   	    												
							}
						}else{
							if(!loginInfo.getPublicparam().contains(",jbrstamp=server,"))
							{
								sinList.add(ActiveXCompositeRecvReconciliationReturnOcx.getVoucherStamp(vDto, uDto.getScertid(), sDto.getSstampposition(),uDto.getSstampid()));
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
				if(!AdminConfirmDialogFacade.open("B_247", "�������ƾ֤", "��Ȩ�û�"+loginInfo.getSuserName()+"����ǩ��", msg)){
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
    							ActiveXCompositeRecvReconciliationReturnOcx.sortStringArray(seqs);
    							
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
	 * Direction: ˫���鿴ҵ����ϸ
	 * ename: doubleClick
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String doubleClick(Object o){
        TvVoucherinfoDto voucherinfoDto = (TvVoucherinfoDto)o;
        if(MsgConstant.VOUCHER_NO_3511.equals(voucherinfoDto.getSvtcode())){
        	incomeMainDto = new TfReportIncomeMainDto();
        	incomeMainDto.setIvousrlno(Long.valueOf(voucherinfoDto.getSdealno()));
        	List<TfReportIncomeMainDto> list = null;
        	try {
				list = commonDataAccessService.findRsByDto(incomeMainDto);
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "��ѯ���뱨�������Ϣҵ�����ݳ���");
			}
			incomeMainDto = list.get(0);
			TfReportIncomeSubDto incomeSubDto = new TfReportIncomeSubDto();
			incomeSubDto.setIvousrlno(incomeMainDto.getIvousrlno());
			String tmp = incomeSubBean.searchDtoList(incomeSubDto);
			if(tmp==null){
    			PagingContext p = incomeSubBean.getPagingContext();
    			p.setPage(new PageResponse());
    		}
			return "���뱨�����";
        }else if(MsgConstant.VOUCHER_NO_3512.equals(voucherinfoDto.getSvtcode())){
        	defrayMainDto = new TfReportDefrayMainDto();
        	defrayMainDto.setIvousrlno(Long.valueOf(voucherinfoDto.getSdealno()));
        	List<TfReportDefrayMainDto> list = null;
        	try {
				list = commonDataAccessService.findRsByDto(defrayMainDto);
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "��ѯ֧���������ҵ�����ݳ���");
			}
			defrayMainDto = list.get(0);
			TfReportDefraySubDto defraySubDto = new TfReportDefraySubDto();
			defraySubDto.setIvousrlno(defrayMainDto.getIvousrlno());
			String tmp = defraySubBean.searchDtoList(defraySubDto);
			if(tmp==null){
    			PagingContext p = defraySubBean.getPagingContext();
    			p.setPage(new PageResponse());
    		}
			return "֧���������";
        }else if(MsgConstant.VOUCHER_NO_3513.equals(voucherinfoDto.getSvtcode())){
        	depositMainDto = new TfReportDepositMainDto();
        	depositMainDto.setIvousrlno(Long.valueOf(voucherinfoDto.getSdealno()));
        	List<TfReportDepositMainDto> list = null;
        	try {
				list = commonDataAccessService.findRsByDto(depositMainDto);
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "��ѯ����˻�����ҵ�����ݳ���");
			}
			depositMainDto = list.get(0);
			TfReportDepositSubDto depositSubDto = new TfReportDepositSubDto();
			depositSubDto.setIvousrlno(depositMainDto.getIvousrlno());
			String tmp = depositSubBean.searchDtoList(depositSubDto);
			if(tmp==null){
    			PagingContext p = depositSubBean.getPagingContext();
    			p.setPage(new PageResponse());
    		}
			return "����˻�����";
        }
        return "";
    }
    
        /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);
    	dto.setSvtcode(vouchertype);
    	PageResponse page = null;
    	TvVoucherinfoDto vDto=new TvVoucherinfoDto();
    	vDto.setSvtcode(dto.getSvtcode());
    	vDto.setSstatus(dto.getSstatus());
    	vDto.setScreatdate(dto.getScreatdate());//ƾ֤����
    	vDto.setStrecode(dto.getStrecode());//�������
    	if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3511))
    	{
    		vDto.setShold1(dto.getShold1());//Ԥ������
    		vDto.setShold2(dto.getShold2());//Ԥ�㼶��
    		vDto.setShold3(dto.getShold3());//Ͻ����־
    		vDto.setSattach(dto.getSattach());//���ջ���
    		vDto.setScheckvouchertype(dto.getScheckvouchertype());//��������
    		vDto.setSpaybankcode(dto.getSpaybankcode());//���˽�ֹ����
    		vDto.setScheckdate(dto.getScheckdate());//������ʼ����
    	}
    	else if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3512))
    	{
    		vDto.setShold1(dto.getShold1());//Ԥ������
    		vDto.setScheckvouchertype(dto.getScheckvouchertype());//��������
    		vDto.setSpaybankcode(dto.getSpaybankcode());//���˽�ֹ����
    		vDto.setScheckdate(dto.getScheckdate());//������ʼ����
    	}else if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3513))
    	{
    		vDto.setShold1(dto.getShold1());//����˻�
    		vDto.setSpaybankcode(dto.getSpaybankcode());//���˽�ֹ����
    		vDto.setScheckdate(dto.getScheckdate());//������ʼ����
    	}
//    	}3512-dto.shold1Ԥ������dto.scheckvouchertype��������dto.spaybankcode���˽�ֹ����dto.scheckdate������ʼ����dto.screatdateƾ֤����dto.strecode�������
//    	3513-dto.shold1����˻�dto.spaybankcode���˽�ֹ����dto.scheckdate������ʼ����dto.screatdateƾ֤����dto.strecode�������
//    	3511-dto.shold3Ͻ����־dto.shold1Ԥ������dto.scheckvouchertype��������dto.shold2Ԥ�㼶��dto.sattach���ջ���
    	try {
    		page =  commonDataAccessService.findRsByDtoWithWherePaging(vDto,pageRequest, "S_EXT1<>'1'");
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
	public List<TvVoucherinfoDto> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<TvVoucherinfoDto> checkList) {
		this.checkList = checkList;
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

	public List getStampList() {
		return stampList;
	}

	public void setStampList(List stampList) {
		this.stampList = stampList;
	}

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
		this.dto.setSvtcode(voucherType);
		refreshStampType();
	}

	public List getVoucherTypeList() {
		voucherTypeList = new ReportBussReadReturnEnumFactory().getEnums("0450");
		if(voucherTypeList==null||voucherTypeList.size()<=0)
		{
			voucherTypeList = new ArrayList();
			Mapper map = new Mapper(MsgConstant.VOUCHER_NO_3511, "���뱨�����3511");
			Mapper map1 = new Mapper(MsgConstant.VOUCHER_NO_3512, "֧���������3512");
			Mapper map2 = new Mapper(MsgConstant.VOUCHER_NO_3513, "����˻�����3513");
			;//ƾ֤����3511-���뱨����ˣ�3512-֧��������ˣ�3513-����˻�����
			voucherTypeList.add(map);
			voucherTypeList.add(map1);
			voucherTypeList.add(map2);
		}
		return voucherTypeList;
	}

	public void setVoucherTypeList(List voucherTypeList) {
		this.voucherTypeList = voucherTypeList;
	}

	public Map getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map paramMap) {
		this.paramMap = paramMap;
	}

	public String getVouchertype() {
		return vouchertype;
	}

	public void setVouchertype(String vouchertype) {
		this.vouchertype = vouchertype;
		this.dto.setSvtcode(vouchertype);
		PageResponse page = new PageResponse();
		checkList.clear();
		pagingcontext.setPage(page);
		if(MsgConstant.VOUCHER_NO_3511.equals(vouchertype))
		{
			dto.setScheckvouchertype("1");//��������1
			dto.setShold1("1");//Ԥ������1-Ԥ����2-Ԥ����
			List contreaNames = new ArrayList();
			contreaNames.add("���뱨����˲�ѯ����");
			contreaNames.add("���뱨����˲�ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames, true);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("֧��������˲�ѯ����");
			contreaNames1.add("����˻����˲�ѯ����");
			contreaNames1.add("֧��������˲�ѯ���һ����");
			contreaNames1.add("����˻����˲�ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
		} else if(MsgConstant.VOUCHER_NO_3512.equals(vouchertype))
		{
			dto.setScheckvouchertype("1");//��������1
			dto.setShold1("1");//Ԥ������1-Ԥ����2-Ԥ����
			List contreaNames = new ArrayList();
			contreaNames.add("֧��������˲�ѯ����");
			contreaNames.add("֧��������˲�ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames, true);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("����˻����˲�ѯ����");
			contreaNames1.add("���뱨����˲�ѯ����");
			contreaNames1.add("���뱨����˲�ѯ���һ����");
			contreaNames1.add("����˻����˲�ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
		}else if(MsgConstant.VOUCHER_NO_3513.equals(vouchertype))
		{
			dto.setShold1("");//����˻�
			List contreaNames = new ArrayList();
			contreaNames.add("����˻����˲�ѯ����");
			contreaNames.add("����˻����˲�ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames, true);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("���뱨����˲�ѯ����");
			contreaNames1.add("֧��������˲�ѯ����");
			contreaNames1.add("���뱨����˲�ѯ���һ����");
			contreaNames1.add("֧��������˲�ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
		}
		refreshStampType();
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}
	public String getYsjc() {
		return ysjc;
	}
	public void setYsjc(String ysjc) {
		this.ysjc = ysjc;
		dto.setShold2(ysjc);
	}
	public List getAcctList() {
		return acctList;
	}
	public void setAcctList(List acctList) {
		this.acctList = acctList;
	}
	public List<TvVoucherinfoDto> getReturnList() {
		return returnList;
	}
	public void setReturnList(List<TvVoucherinfoDto> returnList) {
		this.returnList = returnList;
	}
	public ReportIncomeSubBean getIncomeSubBean() {
		return incomeSubBean;
	}
	public void setIncomeSubBean(ReportIncomeSubBean incomeSubBean) {
		this.incomeSubBean = incomeSubBean;
	}
	public ReportDefraySubBean getDefraySubBean() {
		return defraySubBean;
	}
	public void setDefraySubBean(ReportDefraySubBean defraySubBean) {
		this.defraySubBean = defraySubBean;
	}
	public ReportDepositSubBean getDepositSubBean() {
		return depositSubBean;
	}
	public void setDepositSubBean(ReportDepositSubBean depositSubBean) {
		this.depositSubBean = depositSubBean;
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
		if (dto.getSvtcode() == null || dto.getSvtcode().equals("")) {
			dto.setSvtcode(checkList.get(0).getSvtcode());
		}
		int i = 0;
		try {
			List<TvVoucherinfoDto> queryList = commonDataAccessService
					.findRsByDto(dto);
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
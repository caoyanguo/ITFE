package com.cfcc.itfe.client.recbiz.voucherreport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositeVoucherOcx;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositeVoucherReportOcx;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsStamppositionDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
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
 * @author hjr
 * @time   13-08-11 17:53:03
 * ��ϵͳ: RecBiz
 * ģ��:voucherReport
 * ���:VoucherReport
 */
@SuppressWarnings("unchecked")
public class VoucherReportBean extends AbstractVoucherReportBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(VoucherReportBean.class);
    List<TvVoucherinfoDto> checkList=null;   
	private String strecode = null;
	//�û���¼��Ϣ
	private ITFELoginInfo loginInfo;
	private boolean isJiLinArea = false ;
	private String stamp;
	private List stampList=null;
	private String voucherType=null;
	private List voucherTypeList=null;
	private Map paramMap = null;
	private List taxtrelist = null;
	private String ysjc;
	private String vouchertype=MsgConstant.VOUCHER_NO_3401;//ƾ֤����3404-ȫʡ����ձ���(���ֲ���3453)��3401-�����ձ���3402-����ձ��� 3567 �����±�
//	private String trecode;//�������
//	private String voudate=TimeFacade.getCurrentStringTime();//ƾ֤����
//	private String taxorgcode="2";//���ջ��ش������ջ��ش���1-���֣�2-�����������ջ��أ�0-��������(444444444444)
//	private String reportkind="1";//��������1-�ձ� 2-�±� 3-�걨
//	private String budgettype="1";//Ԥ������1-Ԥ����2-Ԥ����
//	private String belongflag="0";//Ͻ����־0-������1-ȫϽ��3-ȫϽ�Ǳ���
//	private String budgetlevelcode;//Ԥ�㼶��0-���ּ��� 1-����2ʡ��3���м�4���� 5�缶 0-����1-���룬2-ʡ��3-�У�4-�أ�5-�磬6-�ط�
//	private String billkind;//��������0-���ֱ�������1-������Ԥ�����뱨��2-�������˿ⱨ����3-�����ڵ������뱨��4-�ܶ�ֳɱ�����5-������Ԥ�����뱨����6-�������˿ⱨ����7-�����ڵ������뱨����
//	//1-������Ԥ�����뱨��2-�������˿ⱨ����3-�����ڵ������뱨��4-�ܶ�ֳɱ�����									5-������Ԥ�����뱨����6-�������˿ⱨ����7-�����ڵ������뱨����
//	//1-������Ԥ�����뱨���壬2-�������˿ⱨ���壬3-�����ڵ������뱨���壬4-�ܶ�ֳɱ����壬5-�����ڹ���ֳɱ����壬6-������Ԥ�����뱨���壬7-�������˿ⱨ���壬8-�����ڵ������뱨���壬9-�����ڹ���ֳɱ�����
    public VoucherReportBean() {
      super();
      dto = new TvVoucherinfoDto();
      loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      if (loginInfo.getPublicparam().indexOf(",jilin,")!= -1) {
		setJiLinArea(true);
	}
      checkList=new ArrayList();
      paramMap = new HashMap();
//      dto.setScreatdate(TimeFacade.getCurrentStringTime());
      dto.setSorgcode(loginInfo.getSorgcode());
      pagingcontext = new PagingContext(this); 
//      voucherTypeList= new itferesourcepackage.VoucherReportEnumFactory().getEnums(null);
      dto.setSvtcode(vouchertype);//ƾ֤����3404-ȫʡ����ձ���3401-�����ձ���3402-����ձ���,3567-Ԥ�������±���
//      dto.setSattach("1");//��������1-�ձ� 2-�±� 3-�걨
      dto.setScheckdate(TimeFacade.getCurrentStringTime());//��������
      dto.setShold1(MsgConstant.BDG_KIND_IN);//Ԥ������
      dto.setShold3(MsgConstant.RULE_SIGN_ALL);//Ͻ����־0-������1-ȫϽ��3-ȫϽ�Ǳ���
      dto.setSpaybankcode("1");//���ջ��ش������ջ��ش���1-���֣�2-�����������ջ��أ�0-��������(444444444444)
      dto.setScheckvouchertype("1");//��������1-������Ԥ�����뱨��
      TsTreasuryDto tsTreasuryDto = new TsTreasuryDto();
	  tsTreasuryDto.setSorgcode(loginInfo.getSorgcode());
	  List<TsTreasuryDto> list = null;
	  try {
		  list = (List<TsTreasuryDto>)commonDataAccessService.findRsByDto(tsTreasuryDto);
	  } catch (ITFEBizException e){
		  log.error(e);
	  }
	  if(list!=null && list.size() > 0){
		  dto.setStrecode(list.get(0).getStrecode());//�������
		  strecode = dto.getStrecode();
	  }
      dto.setShold2("");//Ԥ�㼶��0-���ּ��� 1-����2ʡ��3���м�4���� 5�缶 0-����1-���룬2-ʡ��3-�У�4-�أ�5-�磬6-�ط�
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
    	if(vouchertype==null||vouchertype.equals("")){
    		MessageDialog.openMessageDialog(null, "ƾ֤���Ͳ���Ϊ�գ�");
    		return "";
    	}
    	if(dto.getStrecode()==null||dto.getStrecode().equals("")){
    		MessageDialog.openMessageDialog(null, "������벻��Ϊ�գ�");
    		return "";
    	}
    	if(MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
    	{
    		if(dto.getScheckdate()==null||dto.getScheckdate().equals("")){
	    		MessageDialog.openMessageDialog(null, "�����ꡢ�²���Ϊ�գ�");
	    		return "";
	    	}else if(dto.getScheckdate().length()!=6)
	    	{
	    		MessageDialog.openMessageDialog(null, "�����ꡢ�¸�ʽΪ6λ(���4λ,�·���λ)��");
	    		return "";
	    	}
    	}else
    	{
	    	if(dto.getScheckdate()==null||dto.getScheckdate().equals("")){
	    		MessageDialog.openMessageDialog(null, "�������ڲ���Ϊ�գ�");
	    		return "";
	    	}
    	}
    	if(MsgConstant.VOUCHER_NO_3404.equals(vouchertype)|| MsgConstant.VOUCHER_NO_3453.equals(vouchertype))
    	{
//	    	if(dto.getSpaybankcode()==null||dto.getSpaybankcode().equals("")){
//	    		MessageDialog.openMessageDialog(null, "���ջ��ز���Ϊ�գ�");
//	    		return "";
//	    	}
//	    	if(dto.getStrecode()==null||dto.getStrecode().equals("")){
//	    		MessageDialog.openMessageDialog(null, "������벻��Ϊ�գ�");
//	    		return "";
//	    	}
	    	TsTreasuryDto tDto=new TsTreasuryDto();
			tDto.setSorgcode(loginInfo.getSorgcode());
			tDto.setStrecode(loginInfo.getSorgcode().substring(0,10));				
			try {
				List treList = commonDataAccessService.findRsByDto(tDto);
				if(treList!=null&&treList.size()>0)
				{
					tDto = (TsTreasuryDto)treList.get(0);
					if(!"2".equals(tDto.getStrelevel()) && !("000002100003".equals(commonDataAccessService.getSrcNode())))
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
    	if(MsgConstant.VOUCHER_NO_3401.equals(vouchertype)||MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
    	{
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
            	if(dto.getScheckvouchertype()==null||"".equals(dto.getScheckvouchertype()))
            	{
            		MessageDialog.openMessageDialog(null, "�������಻��Ϊ�գ�");
            		return "";
            	}else if(dto.getScheckvouchertype()!=null&&("5".equals(dto.getScheckvouchertype())||"9".equals(dto.getScheckvouchertype())))
            	{
            		MessageDialog.openMessageDialog(null, "�������಻֧�ֹ���ֳɱ���");
            		return "";
            	}
            	if(dto.getShold2()!=null&&("0".equals(dto.getShold2())))//||"6".equals(dto.getShold2())
            	{
            		MessageDialog.openMessageDialog(null, "Ԥ�㼶�β�֧�ֹ���");
            		return "";
            	}
    		}
        	
    	}
    	dto.setSvtcode(vouchertype);
    	List<TvVoucherinfoDto> list=new ArrayList<TvVoucherinfoDto>();
    	int count=0;
		List<TsTreasuryDto> tList=new ArrayList<TsTreasuryDto>();
		TvVoucherinfoDto vDto=new TvVoucherinfoDto();
		try {
			TsTreasuryDto tDto=new TsTreasuryDto();
			tDto.setSorgcode(loginInfo.getSorgcode());
			if(MsgConstant.VOUCHER_NO_3404.equals(dto.getSvtcode()) || MsgConstant.VOUCHER_NO_3453.equals(dto.getSvtcode()))
			{
				tDto.setStrecode(loginInfo.getSorgcode().substring(0,10));				
				tList.add(tDto);
			}else
			{
				if(dto.getStrecode()==null||dto.getStrecode().equals("")){				
					tList=commonDataAccessService.findRsByDto(tDto);
				}else{
					tDto.setStrecode(dto.getStrecode());				
					tList.add(tDto);
				}
			}
			for(TsTreasuryDto tsDto:tList){
				vDto=new TvVoucherinfoDto();
		    	vDto.setSorgcode(loginInfo.getSorgcode());
				vDto.setScheckdate(dto.getScheckdate());//��������
				vDto.setSvtcode(vouchertype);
				vDto.setStrecode(tsDto.getStrecode());
				if(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3401)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3567))
				{
					if(dto.getShold2()!=null&&"6".equals(dto.getShold2()))
		    		{
						vDto.setShold1(null);//Ԥ������
		    			vDto.setShold2(dto.getShold2());//Ԥ�㼶��
		    			vDto.setScheckvouchertype(null);//��������
		    			vDto.setShold3(MsgConstant.RULE_SIGN_ALL);//Ͻ����־
		    			vDto.setSpaybankcode("000000000000");//���ջ���
		    			vDto.setSattach(dto.getSattach());//��������
		    		}else
		    		{
						vDto.setSattach(dto.getSattach());//��������
						vDto.setShold1(dto.getShold1());//Ԥ������
						vDto.setShold3(dto.getShold3());//Ͻ����־
						vDto.setSpaybankcode(dto.getSpaybankcode());//���ջ���
						vDto.setShold2(dto.getShold2());//Ԥ�㼶��
						vDto.setScheckvouchertype(dto.getScheckvouchertype());//��������
		    		}
				}
				list.add(vDto);
				if(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3402)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3567)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3401)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3404) || vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3453)){
					try{
						if(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3567))
						{
							if(vDto.getScheckdate()!=null&&vDto.getScheckdate().length()==6)
								vDto.setScheckdate(vDto.getScheckdate()+"01");
							Map<String,String> dateMap = commonDataAccessService.getmapforkey("getEndDateOfMonth-"+loginInfo.getSorgcode()+"-"+vDto.getStrecode()+"-"+vDto.getScheckdate());
				    		if(dateMap!=null&&dateMap.get("getEndDateOfMonth")!=null)
				    			vDto.setScheckdate(dateMap.get("getEndDateOfMonth"));
				    		else
				    			vDto.setScheckdate(TimeFacade.getEndDateOfMonth(dto.getScheckdate()));
						}
						List dtoList = commonDataAccessService.findRsByDto(vDto);
						vDto.setScreatdate(TimeFacade.getCurrentStringTime());//ƾ֤����
						if(dtoList!=null&&dtoList.size()>0)
						{
							count=-1;
							break;
						}
					}catch(java.lang.IndexOutOfBoundsException e){
						continue;
					}catch(Exception e){
						log.error(e);
						Exception e1=new Exception("����ƾ֤���������쳣��",e);			
						MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
						return "";
					}
				}else
					vDto.setScreatdate(TimeFacade.getCurrentStringTime());//ƾ֤����
			}
		}catch(java.lang.NullPointerException e){
			MessageDialog.openMessageDialog(null, "�����������δά����");
			return "";
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
    	if(count==-1){
//    		if(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3404)|| vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3453))
//    		{
//    			if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
//        				.getCurrentComposite().getShell(), "��ʾ"," ��ǰ���ڣ�"+dto.getScheckdate()+"������ȫʡ����ձ�ƾ֤��ȷ������������")) {
//        			return "";
//        		}
//    		}
//    		else if(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3402))
//    		{
//        		MessageDialog.openMessageDialog(null, "���⣺"+vDto.getStrecode()+" ��ǰ���ڣ�"+dto.getScheckdate()+"����ձ�������ƾ֤�������ظ�����ƾ֤��");
//    			refreshTable();
//        		return "";
//    		}
//    		else 
    		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
    				.getCurrentComposite().getShell(), "��ʾ", "���⣺"+vDto.getStrecode()+" ��ǰ���ڣ�"+dto.getScheckdate()+"������ƾ֤��ȷ������������")) {
    			return "";
    		}
    	}
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
//    			if(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3402))
//        		{
//    				MessageDialog.openMessageDialog(null, "���⣺"+resultList.get(1)+"��ǰ���ڣ�"+dto.getScheckdate()+"�¿���ձ�������ƾ֤�������ظ�����ƾ֤��");
//        		}
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
			MessageDialog.openMessageDialog(null,"�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
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
    
    public String getResult3208(List list){
    	if(list==null||list.size()==0){
    		return "��ǰ��������ƾ֤���ݣ�";
    	}
    	String repeat=list.get(0)+"";
    	String succCount=list.get(1)+"";
    	if(repeat.equals("0")){
    		return  "ƾ֤���ɲ����ɹ����ɹ�����Ϊ��"+succCount+" ��";
    	}else if(succCount.equals("0")){
    		return "ƾ֤���ڣ�"+dto.getScreatdate()+" ��ʵ���ʽ��˿�������ƾ֤�������ظ�����ƾ֤��";
    	}else{
    		return "ƾ֤���ɲ����ɹ����ɹ�������"+succCount+" , �Ѵ���ƾ֤������"+repeat+"��";
    	}		
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
//    	if(MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
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
    	if(MsgConstant.VOUCHER_NO_3404.equals(vouchertype) || MsgConstant.VOUCHER_NO_3453.equals(vouchertype))
    	{
    		String checkdate = new String();
    		checkdate = dto.getScheckdate();
	    	dto = new TvVoucherinfoDto();
	    	dto.setSorgcode(loginInfo.getSorgcode());
	    	dto.setSvtcode(vouchertype);//ƾ֤����
	    	dto.setStrecode(strecode);
	    	dto.setScheckdate(checkdate);
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
    	if(MsgConstant.VOUCHER_NO_3401.equals(vouchertype)||MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
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
        	ActiveXCompositeVoucherReportOcx.init(0);
        	
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
    							ActiveXCompositeVoucherOcx.sortStringArray(seqs);    							
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
								sinList.add(ActiveXCompositeVoucherReportOcx.getVoucherStamp(vDto, tDto.getScertid(), sDto.getSstampposition(), tDto.getSstampid()));   	    												
							}
						}else if(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)){
							if(!voucherLoadService.getOfficialStamp().equals(MsgConstant.VOUCHER_ROTARYSTAMP)){								
								sinList.add(ActiveXCompositeVoucherReportOcx.getVoucherStamp(vDto, tDto.getSrotarycertid(), sDto.getSstampposition(), tDto.getSrotaryid()));   	    												
							}
						}else if(stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH))
						{
							if(!voucherLoadService.getOfficialStamp().equals(MsgConstant.VOUCHER_ROTARYSTAMP))
								sinList.add(ActiveXCompositeVoucherReportOcx.getVoucherStamp(vDto, tDto.getSattachcertid(), sDto.getSstampposition(), tDto.getSattachid()));
						}else{
							if(!loginInfo.getPublicparam().contains(",jbrstamp=server,"))
							{
								sinList.add(ActiveXCompositeVoucherReportOcx.getVoucherStamp(vDto, uDto.getScertid(), sDto.getSstampposition(),uDto.getSstampid()));
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
    							ActiveXCompositeVoucherOcx.sortStringArray(seqs);
    							
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
        /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);
    	dto.setSvtcode(vouchertype);
    	PageResponse page = null;
    	TvVoucherinfoDto vDto=null;
    	TvVoucherinfoDto sDto=null;
    	if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3402))
    	{
	    	vDto = new TvVoucherinfoDto();
	    	vDto.setSorgcode(loginInfo.getSorgcode());
			vDto.setScheckdate(dto.getScheckdate());//��������
			vDto.setSvtcode(vouchertype);
			vDto.setStrecode(dto.getStrecode());
			vDto.setSstatus(dto.getSstatus());
    	}
    	if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3404)|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3453))
    	{
	    	sDto = new TvVoucherinfoDto();
	    	sDto.setSorgcode(loginInfo.getSorgcode());
	    	sDto.setScheckdate(dto.getScheckdate());//��������
	    	sDto.setSvtcode(vouchertype);
	    	sDto.setSstatus(dto.getSstatus());
    	}
//    	try {
//	    	if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3567))
//	    	{
//	    		if(dto.getScheckdate()!=null&&dto.getScheckdate().length()==6)
//	    			dto.setScheckdate(dto.getScheckdate()+"01");
//	    		Map<String,String> dateMap = commonDataAccessService.getmapforkey("getEndDateOfMonth-"+loginInfo.getSorgcode()+"-"+dto.getStrecode()+"-"+dto.getScheckdate());
//	    		if(dateMap!=null&&dateMap.get("getEndDateOfMonth")!=null)
//	    			dto.setScheckdate(dateMap.get("getEndDateOfMonth"));
//	    		else
//	    			dto.setScheckdate(TimeFacade.getEndDateOfMonth(dto.getScheckdate()));
//	    	}
//    	}catch(Exception e)
//    	{
//    		log.error(e);
//			MessageDialog.openMessageDialog(null, "���ڷǷ���");
//			return null;
//    	}
    	try {
    		if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3402))
    		{
    			if(loginInfo.getPublicparam().contains((",vouchersearchold=5,")))
    			{
    				Calendar calendar = Calendar.getInstance();
    				calendar.setTime(TimeFacade.getCurrentDate());
    				calendar.add(Calendar.DATE, -5);
    				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// ʱ���ʽ
    				String getdate = dateFormat.format(calendar.getTime());
    				page =  commonDataAccessService.findRsByDtoPaging(vDto,pageRequest, "1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+vDto.getSorgcode()+(vDto.getStrecode()==null?"":"' and S_TRECODE='"+vDto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+vDto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')", " TS_SYSUPDATE desc");
    			}else if(loginInfo.getPublicparam().contains((",vouchersearchold=15,")))
    			{
    				Calendar calendar = Calendar.getInstance();
    				calendar.setTime(TimeFacade.getCurrentDate());
    				calendar.add(Calendar.DATE, -15);
    				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// ʱ���ʽ
    				String getdate = dateFormat.format(calendar.getTime());
    				page =  commonDataAccessService.findRsByDtoPaging(vDto,pageRequest, "1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+vDto.getSorgcode()+(vDto.getStrecode()==null?"":"' and S_TRECODE='"+vDto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+vDto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')", " TS_SYSUPDATE desc");
    			}else
    			{
    				page =  commonDataAccessService.findRsByDtoPaging(vDto,pageRequest, "1=1"," TS_SYSUPDATE desc");
    			}
    		}
    		else if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3401)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3567))
    		{
    			if(dto.getShold2()!=null&&"6".equals(dto.getShold2()))
	    		{
					dto.setShold1(null);//Ԥ������
//	    			vDto.setShold2(dto.getShold2());//Ԥ�㼶��
					dto.setScheckvouchertype(null);//��������
					dto.setShold3(MsgConstant.RULE_SIGN_ALL);//Ͻ����־
					dto.setSpaybankcode("000000000000");//���ջ���
	    		}
    			if(loginInfo.getPublicparam().contains((",vouchersearchold=5,")))
    			{
    				Calendar calendar = Calendar.getInstance();
    				calendar.setTime(TimeFacade.getCurrentDate());
    				calendar.add(Calendar.DATE, -5);
    				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// ʱ���ʽ
    				String getdate = dateFormat.format(calendar.getTime());
    				page =  commonDataAccessService.findRsByDtoPaging(dto,pageRequest, "1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+dto.getSorgcode()+(dto.getStrecode()==null?"":"' and S_TRECODE='"+dto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+dto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')", " TS_SYSUPDATE desc");
    			}else if(loginInfo.getPublicparam().contains((",vouchersearchold=15,")))
    			{
    				Calendar calendar = Calendar.getInstance();
    				calendar.setTime(TimeFacade.getCurrentDate());
    				calendar.add(Calendar.DATE, -15);
    				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// ʱ���ʽ
    				String getdate = dateFormat.format(calendar.getTime());
    				page =  commonDataAccessService.findRsByDtoPaging(dto,pageRequest, "1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+dto.getSorgcode()+(dto.getStrecode()==null?"":"' and S_TRECODE='"+dto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+dto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')", " TS_SYSUPDATE desc");
    			}else
    			{
    				page =  commonDataAccessService.findRsByDtoPaging(dto,pageRequest, "1=1", " TS_SYSUPDATE desc");
    			}
    			if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3567))
    			{
    				if(dto!=null&&dto.getScheckdate()!=null&&dto.getScheckdate().length()>6)
    					dto.setScheckdate(dto.getScheckdate().substring(0,6));
    			}
    		}
    		else if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3410))
    		{
    			TvVoucherinfoDto zdto = new TvVoucherinfoDto();
    			zdto.setSvtcode(dto.getSvtcode());
    			zdto.setStrecode(dto.getStrecode());
    			zdto.setScreatdate(dto.getScreatdate());
    			zdto.setScheckvouchertype(dto.getScheckvouchertype());
    			zdto.setShold1(dto.getShold1());
    			zdto.setSstatus(dto.getSstatus());
    			if(loginInfo.getPublicparam().contains((",vouchersearchhold=5,")))
    			{
    				Calendar calendar = Calendar.getInstance();
    				calendar.setTime(TimeFacade.getCurrentDate());
    				calendar.add(Calendar.DATE, -5);
    				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// ʱ���ʽ
    				String getdate = dateFormat.format(calendar.getTime());
    				page =  commonDataAccessService.findRsByDtoPaging(zdto,pageRequest, "1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+sDto.getSorgcode()+(sDto.getStrecode()==null?"":"' and S_TRECODE='"+sDto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+sDto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')", " TS_SYSUPDATE desc");
    			}else if(loginInfo.getPublicparam().contains((",vouchersearchhold=15,")))
    			{
    				Calendar calendar = Calendar.getInstance();
    				calendar.setTime(TimeFacade.getCurrentDate());
    				calendar.add(Calendar.DATE, -15);
    				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// ʱ���ʽ
    				String getdate = dateFormat.format(calendar.getTime());
    				page =  commonDataAccessService.findRsByDtoPaging(zdto,pageRequest, "1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+sDto.getSorgcode()+(sDto.getStrecode()==null?"":"' and S_TRECODE='"+sDto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+sDto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')", " TS_SYSUPDATE desc");
    			}else
    			{
    				page =  commonDataAccessService.findRsByDtoPaging(zdto,pageRequest, "1=1", " TS_SYSUPDATE desc");
    			}
    		}else
    		{
    			if(loginInfo.getPublicparam().contains((",vouchersearchhold=5,")))
    			{
    				Calendar calendar = Calendar.getInstance();
    				calendar.setTime(TimeFacade.getCurrentDate());
    				calendar.add(Calendar.DATE, -5);
    				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// ʱ���ʽ
    				String getdate = dateFormat.format(calendar.getTime());
    				page =  commonDataAccessService.findRsByDtoPaging(dto,pageRequest, "1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+sDto.getSorgcode()+(sDto.getStrecode()==null?"":"' and S_TRECODE='"+sDto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+sDto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')", " TS_SYSUPDATE desc");
    			}else if(loginInfo.getPublicparam().contains((",vouchersearchhold=15,")))
    			{
    				Calendar calendar = Calendar.getInstance();
    				calendar.setTime(TimeFacade.getCurrentDate());
    				calendar.add(Calendar.DATE, -15);
    				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// ʱ���ʽ
    				String getdate = dateFormat.format(calendar.getTime());
    				page =  commonDataAccessService.findRsByDtoPaging(dto,pageRequest, "1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+sDto.getSorgcode()+(sDto.getStrecode()==null?"":"' and S_TRECODE='"+sDto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+sDto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')", " TS_SYSUPDATE desc");
    			}else
    			{
    				page =  commonDataAccessService.findRsByDtoPaging(dto,pageRequest, "1=1", " TS_SYSUPDATE desc");
    			}
    		}
    			
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
		if(voucherTypeList==null||voucherTypeList.size()<=0)
		{
			voucherTypeList = new ArrayList();
			Mapper map = new Mapper(MsgConstant.VOUCHER_NO_3401, "�����ձ���");
			Mapper map1 = new Mapper(MsgConstant.VOUCHER_NO_3567, "�����±���");
			Mapper map2 = new Mapper(MsgConstant.VOUCHER_NO_3402, "����ձ���");
			Mapper map3; 
			if (isJiLinArea) {
				 map3 = new Mapper(MsgConstant.VOUCHER_NO_3453, "ȫʡ����ձ���");
			}else {
				 map3 = new Mapper(MsgConstant.VOUCHER_NO_3404, "ȫʡ����ձ���");
			}
			
			voucherTypeList.add(map);
			voucherTypeList.add(map1);
			voucherTypeList.add(map2);
			voucherTypeList.add(map3);
			if(loginInfo.getPublicparam().contains(",zc=true,"))
			{
				Mapper map4 = new Mapper(MsgConstant.VOUCHER_NO_3410, "֧���ձ���");
				voucherTypeList.add(map4);
			}
		}
		return voucherTypeList;
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
				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��Ҫ��ѡ�еļ�¼ִ��ɾ��������")) {
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
		PageResponse page = new PageResponse();
		if(dto.getScheckdate()==null)
			 dto.setScheckdate(TimeFacade.getCurrentStringTime());//��������
		if(!MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
		{
			if(dto.getScheckdate().length()<8)
				dto.setScheckdate(TimeFacade.getCurrentStringTime());//��������
		}
		if(MsgConstant.VOUCHER_NO_3410.equals(vouchertype))
			dto.setScreatdate(TimeFacade.getCurrentStringTime());
		else
			dto.setScreatdate(null);
		if(MsgConstant.VOUCHER_NO_3402.equals(vouchertype))
		{
			List contreaNames = new ArrayList();
			contreaNames.add("�����ձ���������");
			contreaNames.add("ȫʡ����ձ���������");
			contreaNames.add("�����±���������");
			contreaNames.add("�����ձ���ѯ���һ����");
			contreaNames.add("�ط��������±���������");
			contreaNames.add("�ط��������ձ���������");
			contreaNames.add("�ط��±���ѯ���һ����");
			contreaNames.add("֧��������������");
			contreaNames.add("֧�������ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames, false);
			dto.setShold1(null);//Ԥ������
		    dto.setShold3(null);//Ͻ����־0-������1-ȫϽ��3-ȫϽ�Ǳ���
		    dto.setSpaybankcode(null);//���ջ��ش������ջ��ش���1-���֣�2-�����������ջ��أ�0-��������(444444444444)
		    dto.setScheckvouchertype(null);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("����ձ���������");
			contreaNames1.add("����ձ���ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, true);
			checkList.clear();
			pagingcontext.setPage(page);
		}else if(MsgConstant.VOUCHER_NO_3401.equals(vouchertype))
		{
//			dto.setSattach("1");//����Ϊ����Ϊ�ձ�
			List contreaNames = new ArrayList();
			if("6".equals(dto.getShold2()))
			{
				contreaNames.add("�ط��������ձ���������");
				contreaNames.add("�ط��±���ѯ���һ����");
			}
			else
			{
				contreaNames.add("�����ձ���������");
				contreaNames.add("�����ձ���ѯ���һ����");
			}
			MVCUtils.setContentAreaVisible(editor, contreaNames, true);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("����ձ���������");
			contreaNames1.add("�����±���������");
			contreaNames1.add("ȫʡ����ձ���������");
			contreaNames1.add("����ձ���ѯ���һ����");
			contreaNames1.add("�ط��������±���������");
			if("6".equals(dto.getShold2()))
			{
				contreaNames1.add("�����ձ���������");
				contreaNames1.add("�����ձ���ѯ���һ����");
			}else
			{
				contreaNames1.add("�ط��������ձ���������");
				contreaNames1.add("�ط��±���ѯ���һ����");
			}
			contreaNames1.add("֧��������������");
			contreaNames1.add("֧�������ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
			checkList.clear();
			pagingcontext.setPage(page);
		}else if(MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
		{
//			dto.setSattach("2");//����Ϊ����Ϊ�±�
			if(dto!=null&&dto.getScheckdate()!=null&&dto.getScheckdate().length()>6)
				dto.setScheckdate(dto.getScheckdate().substring(0,6));
			List contreaNames = new ArrayList();
			if("6".equals(dto.getShold2()))
			{
				contreaNames.add("�ط��������±���������");
				contreaNames.add("�ط��±���ѯ���һ����");
			}else
			{
				contreaNames.add("�����±���������");
				contreaNames.add("�����ձ���ѯ���һ����");
			}
			MVCUtils.setContentAreaVisible(editor, contreaNames, true);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("����ձ���������");
			contreaNames1.add("�����ձ���������");
			contreaNames1.add("ȫʡ����ձ���������");
			contreaNames1.add("����ձ���ѯ���һ����");
			contreaNames1.add("�ط��������ձ���������");
			if("6".equals(dto.getShold2()))
			{
				contreaNames1.add("�����±���������");
				contreaNames1.add("�����ձ���ѯ���һ����");
			}else
			{
				contreaNames1.add("�ط��������±���������");
				contreaNames1.add("�ط��±���ѯ���һ����");
			}
			contreaNames1.add("֧��������������");
			contreaNames1.add("֧�������ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
			checkList.clear();
			pagingcontext.setPage(page);
		}else if(MsgConstant.VOUCHER_NO_3404.equals(vouchertype)||MsgConstant.VOUCHER_NO_3453.equals(vouchertype))
		{
			TsTreasuryDto tDto=new TsTreasuryDto();
			tDto.setSorgcode(loginInfo.getSorgcode());
			tDto.setStrecode(loginInfo.getSorgcode().substring(0,10));				
			try {
				List treList = commonDataAccessService.findRsByDto(tDto);
				if(treList!=null&&treList.size()>0)
				{
					tDto = (TsTreasuryDto)treList.get(0);
					if(!"2".equals(tDto.getStrelevel()) && !("000002100003".equals(commonDataAccessService.getSrcNode())))
					{
						MessageDialog.openMessageDialog(null, "�˹���ֻ��ʡ������������ʹ�ã�");
			    		return;
					}
				}else
				{
					MessageDialog.openMessageDialog(null, "�˹���ֻ��ʡ������������ʹ�ã�");
		    		return;
				}
			} catch (ITFEBizException e) {
				MessageDialog.openMessageDialog(null, "�ж��Ƿ�ʡ�������쳣��");
	    		return;
			}
			List contreaNames = new ArrayList();
			contreaNames.add("�����ձ���������");
			contreaNames.add("����ձ���������");
			contreaNames.add("�����±���������");
			contreaNames.add("�����ձ���ѯ���һ����");
			contreaNames.add("�ط��������±���������");
			contreaNames.add("�ط��±���ѯ���һ����");
			contreaNames.add("�ط��������ձ���������");
			contreaNames.add("֧��������������");
			contreaNames.add("֧�������ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames, false);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("ȫʡ����ձ���������");
			contreaNames1.add("����ձ���ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, true);
			checkList.clear();
			pagingcontext.setPage(page);
		}else if(MsgConstant.VOUCHER_NO_3410.equals(vouchertype))
		{
			List contreaNames = new ArrayList();
			contreaNames.add("�����ձ���������");
			contreaNames.add("����ձ���������");
			contreaNames.add("�����±���������");
			contreaNames.add("�����ձ���ѯ���һ����");
			contreaNames.add("�ط��������±���������");
			contreaNames.add("�ط��±���ѯ���һ����");
			contreaNames.add("�ط��������ձ���������");
			contreaNames.add("ȫʡ����ձ���������");
			contreaNames.add("����ձ���ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames, false);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("֧��������������");
			contreaNames1.add("֧�������ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, true);
			checkList.clear();
			pagingcontext.setPage(page);
		}
		this.dto.setSvtcode(vouchertype);
		setYsjc(null);
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
		PageResponse page = new PageResponse();
		checkList.clear();
		pagingcontext.setPage(page);
		if(ysjc==null||"".equals(ysjc))
			return;
		if("6".equals(ysjc))//�ж�Ԥ�㼶��Ϊ�ط�
		{
			if(MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
			{
				if(dto!=null&&dto.getScheckdate()!=null&&dto.getScheckdate().length()>6)
					dto.setScheckdate(dto.getScheckdate().substring(0,6));
			}else
			{
				dto.setScheckdate(TimeFacade.getCurrentStringTime());
			}
			List contreaNames = new ArrayList();
			if(MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
				contreaNames.add("�ط��������±���������");
			else
				contreaNames.add("�ط��������ձ���������");
			contreaNames.add("�ط��±���ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames, true);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("�����±���������");
			contreaNames1.add("����ձ���������");
			contreaNames1.add("�����ձ���������");
			contreaNames1.add("ȫʡ����ձ���������");
			contreaNames1.add("����ձ���ѯ���һ����");
			contreaNames1.add("�����ձ���ѯ���һ����");
			if(MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
				contreaNames1.add("�ط��������ձ���������");
			else
				contreaNames1.add("�ط��������±���������");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
			
		}else
		{
			List contreaNames = new ArrayList();
			if(MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
				contreaNames.add("�����±���������");
			else
				contreaNames.add("�����ձ���������");
			contreaNames.add("�����ձ���ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames, true);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("�ط��������±���������");
			contreaNames1.add("����ձ���������");
			if(MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
				contreaNames1.add("�����ձ���������");
			else
				contreaNames1.add("�����±���������");
			contreaNames1.add("�ط��������ձ���������");
			contreaNames1.add("�ط��������±���������");
			contreaNames1.add("ȫʡ����ձ���������");
			contreaNames1.add("����ձ���ѯ���һ����");
			contreaNames1.add("�ط��±���ѯ���һ����");
			if(MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
				contreaNames1.add("�����ձ���������");
			else
				contreaNames1.add("�����±���������");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
		}
		refreshStampType();
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}
	public void setJiLinArea(boolean isJiLinArea) {
		this.isJiLinArea = isJiLinArea;
	}
	public boolean isJiLinArea() {
		return isJiLinArea;
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
	public String getStrecode() {
		return strecode;
	}
	public void setStrecode(String strecode) {
		this.strecode = strecode;
		dto.setStrecode(strecode);
		getTaxtrelist();
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}
	public List getTaxtrelist() {
		Mapper taxmap = null;
		taxtrelist=new ArrayList();
		taxmap = new Mapper("1","����");
		taxtrelist.add(taxmap);
		taxmap = new Mapper("0","��������");
		taxtrelist.add(taxmap);
		taxmap = new Mapper("2","�����������ջ���");
		taxtrelist.add(taxmap);
		taxmap = new Mapper("3","��˰����");//111111111111
		taxtrelist.add(taxmap);
		taxmap = new Mapper("4","��˰����");//222222222222
		taxtrelist.add(taxmap);
		taxmap = new Mapper("5","���ش���");//333333333333
		taxtrelist.add(taxmap);
		taxmap = new Mapper("6","��������");//555555555555
		taxtrelist.add(taxmap);
		if(dto.getStrecode()!=null&&!"".equals(dto.getStrecode()))
		{
			TsTaxorgDto taxdto = new TsTaxorgDto();
			taxdto.setSorgcode(dto.getSorgcode());
			taxdto.setStrecode(dto.getStrecode());
			try {
				List<TsTaxorgDto> queryList = commonDataAccessService.findRsByDto(taxdto," order by s_taxorgcode ");
				if(queryList!=null&&queryList.size()>0)
				{
					for(TsTaxorgDto temp:queryList)
					{
						if(temp.getStaxorgname()!=null&&!"".equals(temp.getStaxorgname()))
							taxmap = new Mapper(temp.getStaxorgcode(),temp.getStaxorgname());
						else
							taxmap = new Mapper(temp.getStaxorgcode(),temp.getStaxorgcode());
						taxtrelist.add(taxmap);
					}
				}
			} catch (ITFEBizException e) {
				e.printStackTrace();
			}
		}
		return taxtrelist;
	}
	public void setTaxtrelist(List taxtrelist) {
		this.taxtrelist = taxtrelist;
	}
}
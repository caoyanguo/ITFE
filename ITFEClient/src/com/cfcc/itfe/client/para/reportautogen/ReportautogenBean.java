package com.cfcc.itfe.client.para.reportautogen;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsAutogenervoucherDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;

/**
 * codecomment: 
 * @author zhangliang
 * @time   15-11-11 10:12:15
 * ��ϵͳ: Para
 * ģ��:reportautogen
 * ���:Reportautogen
 */
public class ReportautogenBean extends AbstractReportautogenBean implements IPageDataProvider {
	private String ysjc;
	private String vouchertype=MsgConstant.VOUCHER_NO_3401;//ƾ֤����3404-ȫʡ����ձ���(���ֲ���3453)��3401-�����ձ���3402-����ձ��� 3567 �����±�
    private static Log log = LogFactory.getLog(ReportautogenBean.class);
    boolean issearch = true;
    private TsAutogenervoucherDto dto = null;
    private ITFELoginInfo loginInfo;
    private List voucherTypeList=null;
    List<TsAutogenervoucherDto> checkList=null;
    private List taxtrelist = null;
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
    public ReportautogenBean() {
      super();
      loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      dto = new TsAutogenervoucherDto();
      dto.setSorgcode(loginInfo.getSorgcode());
      checkList=new ArrayList<TsAutogenervoucherDto>();
      pagingcontext = new PagingContext(this); 
      dto.setSvtcode(vouchertype);//ƾ֤����3404-ȫʡ����ձ���3401-�����ձ���3402-����ձ���,3567-Ԥ�������±���
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
	  }
      dto.setShold2("");//Ԥ�㼶��0-���ּ��� 1-����2ʡ��3���м�4���� 5�缶 0-����1-���룬2-ʡ��3-�У�4-�أ�5-�磬6-�ط�
      dto.setSvtcode(vouchertype);
      PageRequest pageRequest = new PageRequest();
      PageResponse pageResponse=new PageResponse();
      pageResponse = retrieve(pageRequest);
      pagingcontext.setPage(pageResponse);
    }
    /**
	 * Direction: ��ѯ
	 * ename: searchdata
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String searchdata(Object o){
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse=new PageResponse();
		issearch = false;
		pageResponse = retrieve(pageRequest);
		if(pageResponse.getTotalCount()==0){
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼����������ƾ֤��");
		}
		pagingcontext.setPage(pageResponse);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.searchdata(o);
    }
    
	/**
	 * Direction: ����
	 * ename: savedata
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String savedata(Object o){
    	if(vouchertype==null||vouchertype.equals("")){
    		MessageDialog.openMessageDialog(null, "ƾ֤���Ͳ���Ϊ�գ�");
    		return "";
    	}
    	if(dto.getStrecode()==null||dto.getStrecode().equals("")){
    		MessageDialog.openMessageDialog(null, "������벻��Ϊ�գ�");
    		return "";
    	}
    	if(MsgConstant.VOUCHER_NO_3404.equals(vouchertype)|| MsgConstant.VOUCHER_NO_3453.equals(vouchertype))
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
            	if(dto.getShold2()==null||"".equals(dto.getShold2()))
            	{
            		MessageDialog.openMessageDialog(null, "Ԥ�㼶�β���Ϊ�գ�");
            		return "";
            	}
    		}
        	
    	}
    	dto.setSvtcode(vouchertype);
		try {
			
			List dtoList = commonDataAccessService.findRsByDto(dto);
			if(dtoList!=null&&dtoList.size()>0)
			{
				MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), "���⵱ǰ�����Զ����������Ѿ�����,ͬ�������������ظ����棡");
				return "";
			}
			String ieq = "00000000"+commonDataAccessService.getSequenceNo("TRAID_SEQ");
			dto.setSdealno(TimeFacade.getCurrentStringTime()+ieq.substring(ieq.length()-8));
			TsConvertfinorgDto cDto=new TsConvertfinorgDto();
			cDto.setSorgcode(dto.getSorgcode());
			cDto.setStrecode(dto.getStrecode());
			List cList = commonDataAccessService.findRsByDto(cDto);
			if(cList!=null&&cList.size()>0)
				dto.setSadmdivcode(((TsConvertfinorgDto)cList.get(0)).getSadmdivcode());
			else
				dto.setSadmdivcode("0000000000");
			dto.setSvoucherno(ieq);
			dto.setSfilename("autoreport");
			dto.setSvoucherflag("1");
			dto.setNmoney(new BigDecimal(66666));
			dto.setScreatdate(TimeFacade.getCurrentStringTime());
			dto.setScheckdate(TimeFacade.getCurrentStringTime());
			dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
			dto.setSstyear(TimeFacade.getCurrentStringTime().substring(0,4));
			commonDataAccessService.create(dto);
			MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), "����ɹ���");
			dto = new TsAutogenervoucherDto();
		      dto.setSorgcode(loginInfo.getSorgcode());
		      checkList=new ArrayList<TsAutogenervoucherDto>();
		      pagingcontext = new PagingContext(this); 
		      dto.setSvtcode(vouchertype);//ƾ֤����3404-ȫʡ����ձ���3401-�����ձ���3402-����ձ���,3567-Ԥ�������±���
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
			  }
		      dto.setShold2("");//Ԥ�㼶��0-���ּ��� 1-����2ʡ��3���м�4���� 5�缶 0-����1-���룬2-ʡ��3-�У�4-�أ�5-�磬6-�ط�
		      dto.setSvtcode(vouchertype);
			PageRequest pageRequest = new PageRequest();
			PageResponse pageResponse=new PageResponse();
			issearch = true;
			pageResponse = retrieve(pageRequest);
			pagingcontext.setPage(pageResponse);
			editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		}catch(java.lang.IndexOutOfBoundsException e){
			Exception e1=new Exception("����ƾ֤���������쳣��",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
		}catch(Exception e){
			log.error(e);
			Exception e1=new Exception("����ƾ֤���������쳣��",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
			return "";
		}
        return super.savedata(o);
    }
	/**
	 * Direction: ɾ��
	 * ename: deletedata
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String deletedata(Object o){
    	try {
	        if(checkList!=null&&checkList.size()>0)
	        {
	        	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
	    				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��Ҫɾ��ѡ�е�������")) {
	    			return "";
	    		}
	        	for(TsAutogenervoucherDto tempdto : checkList)
						commonDataAccessService.delete(tempdto);
	        	PageRequest pageRequest = new PageRequest();
				PageResponse pageResponse=new PageResponse();
				issearch = true;
				pageResponse = retrieve(pageRequest);
				pagingcontext.setPage(pageResponse);
				editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	        }else
	        {
	        	MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), "��ѡ����Ҫɾ�������ݣ�");
	        }
    	} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
        return super.deletedata(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	checkList=new ArrayList<TsAutogenervoucherDto>();
    	dto.setSvtcode(vouchertype);
    	PageResponse page = null;
    	TsAutogenervoucherDto vDto = new TsAutogenervoucherDto();
    	TsAutogenervoucherDto sDto = new TsAutogenervoucherDto();
    	try {
    		if(issearch)
        	{
    	    	vDto.setSorgcode(loginInfo.getSorgcode());
    	    	vDto.setSvtcode(dto.getSvtcode());
    	    	page =  commonDataAccessService.findRsByDtoWithWherePaging(vDto,pageRequest, "1=1");
    	    	return page;
        	}
        	if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3402))
        	{
    	    	vDto.setSorgcode(loginInfo.getSorgcode());
    			vDto.setSvtcode(vouchertype);
    			vDto.setStrecode(dto.getStrecode());
        	}
        	if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3404)|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3453))
        	{
    	    	sDto = new TsAutogenervoucherDto();
    	    	sDto.setSorgcode(loginInfo.getSorgcode());
    	    	sDto.setSvtcode(vouchertype);
        	}
    		if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3401)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3567))
    		{
    			if(dto.getShold2()!=null&&"6".equals(dto.getShold2()))
	    		{
					dto.setShold1(null);//Ԥ������
//	    			vDto.setShold2(dto.getShold2());//Ԥ�㼶��
					dto.setScheckvouchertype(null);//��������
					dto.setShold3(MsgConstant.RULE_SIGN_ALL);//Ͻ����־
					dto.setSpaybankcode("000000000000");//���ջ���
	    		}else
	    		{
	    			vDto.setSvtcode(dto.getSvtcode());
	    			vDto.setStrecode(dto.getStrecode());
	    			vDto.setShold1(dto.getShold1());
	    			vDto.setShold2(dto.getShold2());
	    			vDto.setScheckvouchertype(dto.getScheckvouchertype());
	    			vDto.setShold3(dto.getShold3());
	    			vDto.setSpaybankcode(dto.getSpaybankcode());
	    			vDto.setSisauto(dto.getSisauto());
	    		}
    		}
    		page =  commonDataAccessService.findRsByDtoWithWherePaging(vDto,pageRequest, "1=1");
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
	public String getYsjc() {
		return ysjc;
	}
	public void setYsjc(String ysjc) {
		this.ysjc = ysjc;
		dto.setShold2(ysjc);
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
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
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
			MVCUtils.setContentAreaVisible(editor, contreaNames, false);
			dto.setShold1(null);//Ԥ������
		    dto.setShold3(null);//Ͻ����־0-������1-ȫϽ��3-ȫϽ�Ǳ���
		    dto.setSpaybankcode(null);//���ջ��ش������ջ��ش���1-���֣�2-�����������ջ��أ�0-��������(444444444444)
		    dto.setScheckvouchertype(null);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("����ձ���������");
			contreaNames1.add("����ձ���ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, true);
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
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
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
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
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
			MVCUtils.setContentAreaVisible(editor, contreaNames, false);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("ȫʡ����ձ���������");
			contreaNames1.add("����ձ���ѯ���һ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, true);
		}
		this.dto.setSvtcode(vouchertype);
		setYsjc(null);
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}
	public List getVoucherTypeList() {
		if(voucherTypeList==null||voucherTypeList.size()<=0)
		{
			voucherTypeList = new ArrayList();
			Mapper map = new Mapper(MsgConstant.VOUCHER_NO_3401, "�����ձ���");
//			Mapper map1 = new Mapper(MsgConstant.VOUCHER_NO_3567, "�����±���");
			Mapper map2 = new Mapper(MsgConstant.VOUCHER_NO_3402, "����ձ���");
//			Mapper map3 = new Mapper(MsgConstant.VOUCHER_NO_3404, "ȫʡ����ձ���");; 
			voucherTypeList.add(map);
//			voucherTypeList.add(map1);
			voucherTypeList.add(map2);
//			voucherTypeList.add(map3);
		}
		return voucherTypeList;
	}

	public void setVoucherTypeList(List voucherTypeList) {
		this.voucherTypeList = voucherTypeList;
	}
	public TsAutogenervoucherDto getDto() {
		return dto;
	}
	public void setDto(TsAutogenervoucherDto dto) {
		this.dto = dto;
	}
	public PagingContext getPagingcontext() {
		return pagingcontext;
	}
	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
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
	public List<TsAutogenervoucherDto> getCheckList() {
		return checkList;
	}
	public void setCheckList(List<TsAutogenervoucherDto> checkList) {
		this.checkList = checkList;
	}
}
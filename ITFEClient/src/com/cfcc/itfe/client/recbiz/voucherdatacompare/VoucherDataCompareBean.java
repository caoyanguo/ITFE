package com.cfcc.itfe.client.recbiz.voucherdatacompare;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainSxDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubSxDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainSxDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubSxDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainSxDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubSxDto;
import com.cfcc.itfe.persistence.dto.TvPayreckbankSxDto;
import com.cfcc.itfe.persistence.dto.TvPayreckbankbackSxDto;
import com.cfcc.itfe.persistence.dto.TvPayreckbankbacklistSxDto;
import com.cfcc.itfe.persistence.dto.TvPayreckbanklistSxDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoSxDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author Administrator
 * @time   13-10-23 15:30:16
 * ��ϵͳ: RecBiz
 * ģ��:voucherDataCompare
 * ���:VoucherDataCompare
 */
public class VoucherDataCompareBean extends AbstractVoucherDataCompareBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(VoucherDataCompareBean.class);
    List<TvVoucherinfoSxDto> checkList=null; 
    private ITFELoginInfo loginInfo;
    public VoucherDataCompareBean() {
      super();
      //��������2301 
      tvPayreckbankSxDto = new TvPayreckbankSxDto();
      tvPayreckbanklistSxDto = new TvPayreckbanklistSxDto();
      //�����˿�ƾ֤2302
      tvPayreckbankbackSxDto = new TvPayreckbankbackSxDto();
      tvPayreckbankbacklistSxDto = new TvPayreckbankbacklistSxDto();
      //ֱ��֧�����5108
      tvDirectpaymsgmainSxDto = new TvDirectpaymsgmainSxDto();
      tvDirectpaymsgsubSxDto = new TvDirectpaymsgsubSxDto();
      //��Ȩ֧�����5106
      tvGrantpaymsgmainSxDto = new TvGrantpaymsgmainSxDto();
      tvGrantpaymsgsubSxDto = new TvGrantpaymsgsubSxDto();
      //ʵ������ƾ֤5207
      tvPayoutmsgmainSxDto = new TvPayoutmsgmainSxDto();
      tvPayoutmsgsubSxDto = new TvPayoutmsgsubSxDto();
  	
      pagingPayreckbanklist = new PagingContext(this);
      pagingPayreckbankbacklist = new PagingContext(this);
      pagingDirectpaymsgsub = new PagingContext(this);
      pagingGrantpaymsgsub = new PagingContext(this);
      pagingGrantpaymsgmain = new PagingContext(this);
      pagingPayoutmsgsub = new PagingContext(this);
  	
      dto = new TvVoucherinfoSxDto();
      checkList=new ArrayList();
      pagingcontext = new PagingContext(this);
      loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      checkList=new ArrayList();
      dto.setScreatdate(TimeFacade.getCurrentStringTime());
      dto.setSorgcode(loginInfo.getSorgcode());
    }
    
    private void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse=new PageResponse();
		pageResponse = retrieve(pageRequest);
		if(pageResponse.getTotalCount()==0){
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼��");
		}
		pagingcontext.setPage(pageResponse);
	}
    
    private void init2() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse=new PageResponse();
		pageResponse = retrieve(pageRequest);
		if(pageResponse.getTotalCount()==0){
//			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼��");
		}
		pagingcontext.setPage(pageResponse);
	}
    
	/**
	 * Direction: ��ѯ 
	 * ename: search
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String search(Object o){
    	if(dto.getSvtcode()==null||dto.getSvtcode().equals("")){
    		MessageDialog.openMessageDialog(null, "����ѡ��ƾ֤���ͣ�");
    		return "";
    	}
    
    	refreshTable();
        return super.search(o);
    }

	/**
	 * Direction: ��ȡƾ֤
	 * ename: voucherRead
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherRead(Object o){
    	int count = 0; 
    	 try {
    		 count = voucherDataCompareService.voucherRead(dto.getSvtcode(),loginInfo.getSorgcode());
    		 MessageDialog.openMessageDialog(null, "�ѳɹ���ȡƾ֤"+count+"����");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
		} catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}
		 refreshTable2();
         return super.voucherRead(o);
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
	 * Direction: ���ݱȶ�
	 * ename: voucherDataCompare
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherDataCompare(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�ȶԵļ�¼��");
    		return "";
    	}
    	try {
    		MulitTableDto msgDto = voucherDataCompareService.voucherDataCompare(checkList);
    		String msg = msgDto.getErrorList().get(0);
			MessageDialog.openMessageDialog(null, msg);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
    		return "";
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}
		refreshTable2();
        return super.voucherDataCompare(o);
    }

	/**
	 * Direction: ���ͻص�
	 * ename: sendReturnVoucher
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String sendReturnVoucher(Object o)
    {
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���ͻص��ļ�¼��");
    		return "";
    	}
    	for(int i = 0; i < checkList.size(); i++)
    	{
    		TvVoucherinfoSxDto curentdto = (TvVoucherinfoSxDto)checkList.get(i);
			String status = curentdto.getSstatus();
			String svtcode = curentdto.getSvtcode();
			if(!DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(status))
			{
				MessageDialog.openMessageDialog(null, "��ѡ��״̬Ϊ����ɹ��ļ�¼��");
	    		return "";
			}
			if(!svtcode.equals(MsgConstant.VOUCHER_NO_2301) && !svtcode.equals(MsgConstant.VOUCHER_NO_2302))
			{
				MessageDialog.openMessageDialog(null, "ֻ�����뻮��ƾ֤�������˿�ƾ֤����Ҫ���ͻص���");
	    		return "";
			}
    	}
    	
      try 
      {
		voucherDataCompareService.sendReturnVoucher(checkList);
		MessageDialog.openMessageDialog(null, "���ͻص��ɹ���");
	  } catch (ITFEBizException e) 
	  {
		  log.error(e);
		  MessageDialog.openMessageDialog(null, e.getMessage());
		  return "";
	  } catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
	  }
	  refreshTable2();
      return super.sendReturnVoucher(o);
    }

	/**
	 * Direction: ����У��
	 * ename: voucherVerify
	 * ���÷���: 
	 * viewers: * messages: 
	 * @author sunyan
	 */
    public String voucherVerify(Object o){
    	int count = 0;

    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "��ѡ��ҪУ��ļ�¼��");
    		return "";
    	}
    	
    	for(int i=0;i<checkList.size();i++){
    		String ls_Status =  checkList.get(i).getSstatus().trim();
    		if(!(ls_Status.equals(DealCodeConstants.VOUCHER_RECEIVE_SUCCESS) )&&!(ls_Status.equals(DealCodeConstants.VOUCHER_VALIDAT_FAIL) )){
    			MessageDialog.openMessageDialog(null, "ֻ��״̬Ϊ\"ǩ�ճɹ�\" , \"У��ʧ��\"��ƾ֤��ִ�д˲���!");
    			
        		return "";
    		}
    	}
    	
    	try {
			count=voucherDataCompareService.voucherVerify(checkList);
			MessageDialog.openMessageDialog(null, "ƾ֤У��"+checkList.size()+"�����ɹ�����Ϊ��"+count);
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("ƾ֤У����������쳣��",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch(Exception e){
			log.error(e);
			Exception e1=new Exception("ƾ֤У����������쳣��",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
		}
		refreshTable2();
    	
        return super.voucherVerify(o);
    }

	/**
	 * Direction: ˫����¼
	 * ename: doubleClick
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String doubleClick(Object o)
    {
    	TvVoucherinfoSxDto vouvherinfo = (TvVoucherinfoSxDto)o;
    	String svtcode = vouvherinfo.getSvtcode();
    	String sdealno = vouvherinfo.getSdealno();
    	PageRequest subRequest = new PageRequest();
    	
    	if(MsgConstant.VOUCHER_NO_2301.equals(svtcode))
    	{
    		TvPayreckbankSxDto qdto = new TvPayreckbankSxDto();
    		qdto.setIvousrlno(Long.valueOf(sdealno));
    		try
    		{
    			List<TvPayreckbankSxDto> list = (List<TvPayreckbankSxDto>)commonDataAccessService.findRsByDto(qdto);
    			if(list == null || list.size() == 0)
    			{
    				MessageDialog.openMessageDialog(null, "û����ص�ҵ����Ϣ��");
            		return "";
    			}else
    			{
    				tvPayreckbankSxDto = list.get(0);
    			}
			} catch (ITFEBizException e)
			{
				log.error(e);
				MessageDialog.openMessageDialog(null, "��ѯ���ݴ���");
			} catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
				log.error(e);
				MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			}
			this.multiRetrieve(subRequest, MsgConstant.VOUCHER_NO_2301, tvPayreckbankSxDto);
			
    		editor.fireModelChanged();
    		return "���뻮��ƾ֤��ϸ��Ϣҳ��";
    	}else if(MsgConstant.VOUCHER_NO_2302.equals(svtcode))
    	{
    		TvPayreckbankbackSxDto qdto = new TvPayreckbankbackSxDto();
    		qdto.setIvousrlno(Long.valueOf(sdealno));
    		try
    		{
    			List<TvPayreckbankbackSxDto> list = (List<TvPayreckbankbackSxDto>)commonDataAccessService.findRsByDto(qdto);
    			if(list == null || list.size() == 0)
    			{
    				MessageDialog.openMessageDialog(null, "û����ص�ҵ����Ϣ��");
            		return "";
    			}else
    			{
    				tvPayreckbankbackSxDto = list.get(0);
    			}
			} catch (ITFEBizException e)
			{
				log.error(e);
				MessageDialog.openMessageDialog(null, "��ѯ���ݴ���");
			} catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
				log.error(e);
				MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			}
			this.multiRetrieve(subRequest, MsgConstant.VOUCHER_NO_2302, tvPayreckbankbackSxDto);
			
    		editor.fireModelChanged();
    		return "�����˿�ƾ֤��ϸ��Ϣҳ��";
    	}else if(MsgConstant.VOUCHER_NO_5106.equals(svtcode))
    	{
    		TvGrantpaymsgmainSxDto qdto = new TvGrantpaymsgmainSxDto();
    		qdto.setIvousrlno(Long.valueOf(sdealno));
    		try
    		{
    			List<TvGrantpaymsgmainSxDto> list = (List<TvGrantpaymsgmainSxDto>)commonDataAccessService.findRsByDto(qdto);
    			if(list == null || list.size() == 0)
    			{
    				MessageDialog.openMessageDialog(null, "û����ص�ҵ����Ϣ��");
            		return "";
    			}else
    			{
    				this.multiRetrieve(null, MsgConstant.VOUCHER_NO_5106, qdto);
    			}
			} catch (ITFEBizException e)
			{
				log.error(e);
				MessageDialog.openMessageDialog(null, "��ѯ���ݴ���");
			} catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
				log.error(e);
				MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			}
			this.multiRetrieve(subRequest, MsgConstant.VOUCHER_NO_5106, qdto);
    		
    		editor.fireModelChanged();
    		return "��Ȩ֧����������ϸ��Ϣҳ��";
    	}else if(MsgConstant.VOUCHER_NO_5108.equals(svtcode))
    	{
    		TvDirectpaymsgmainSxDto qdto = new TvDirectpaymsgmainSxDto();
    		qdto.setIvousrlno(Long.valueOf(sdealno));
    		try
    		{
    			List<TvDirectpaymsgmainSxDto> list = (List<TvDirectpaymsgmainSxDto>)commonDataAccessService.findRsByDto(qdto);
    			if(list == null || list.size() == 0)
    			{
    				MessageDialog.openMessageDialog(null, "û����ص�ҵ����Ϣ��");
            		return "";
    			}else
    			{
    				tvDirectpaymsgmainSxDto = list.get(0);
    			}
			} catch (ITFEBizException e)
			{
				log.error(e);
				MessageDialog.openMessageDialog(null, "��ѯ���ݴ���");
			} catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
				log.error(e);
				MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			}
			this.multiRetrieve(subRequest, MsgConstant.VOUCHER_NO_5108, tvDirectpaymsgmainSxDto);
			
    		editor.fireModelChanged();
    		return "ֱ��֧����������ϸ��Ϣҳ��";
    	}else if(MsgConstant.VOUCHER_NO_5207.equals(svtcode))
    	{
    		TvPayoutmsgmainSxDto qdto = new TvPayoutmsgmainSxDto();
    		qdto.setSbizno(sdealno);
    		try
    		{
    			List<TvPayoutmsgmainSxDto> list = (List<TvPayoutmsgmainSxDto>)commonDataAccessService.findRsByDto(qdto);
    			if(list == null || list.size() == 0)
    			{
    				MessageDialog.openMessageDialog(null, "û����ص�ҵ����Ϣ��");
            		return "";
    			}else
    			{
    				tvPayoutmsgmainSxDto = list.get(0);
    			}
			} catch (ITFEBizException e)
			{
				log.error(e);
				MessageDialog.openMessageDialog(null, "��ѯ���ݴ���");
			} catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
				log.error(e);
				MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			}
			this.multiRetrieve(subRequest, MsgConstant.VOUCHER_NO_5207, tvPayoutmsgmainSxDto);
			
    		editor.fireModelChanged();
    		return "ʵ���ʽ���ϸ��Ϣ";
    	}
    	
    	editor.fireModelChanged();
        return super.doubleClick(o);
    }
    
    public PageResponse multiRetrieve(PageRequest pageRequest, String type, Object bizDto) 
    {
    	if(MsgConstant.VOUCHER_NO_2301.equals(type))
    	{
    		TvPayreckbankSxDto dto = (TvPayreckbankSxDto)bizDto;
    		TvPayreckbanklistSxDto qdto = new TvPayreckbanklistSxDto();
    		qdto.setIvousrlno(dto.getIvousrlno());
			try 
			{
				PageResponse response =  commonDataAccessService.findRsByDtoPaging(qdto, pageRequest, "1=1", "TS_UPDATE DESC");
				pagingPayreckbanklist.setPage(response);
				return response;
			} catch (ITFEBizException e) 
			{
				log.error(e);
				MessageDialog.openMessageDialog(null, "��ѯ���ݴ���");
				return null;
			} catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
				log.error(e);
				MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			}
    	}else if(MsgConstant.VOUCHER_NO_2302.equals(type))
    	{
    		TvPayreckbankbackSxDto dto = (TvPayreckbankbackSxDto)bizDto;
    		TvPayreckbankbacklistSxDto qdto = new TvPayreckbankbacklistSxDto();
    		qdto.setIvousrlno(dto.getIvousrlno());
    	    try 
			{
				PageResponse response =  commonDataAccessService.findRsByDtoPaging(qdto, pageRequest, "1=1", "TS_UPDATE DESC");
				pagingPayreckbankbacklist.setPage(response);
				return response;
			} catch (ITFEBizException e) 
			{
				log.error(e);
				MessageDialog.openMessageDialog(null, "��ѯ���ݴ���");
				return null;
			} catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
				log.error(e);
				MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			}
    	}else if(MsgConstant.VOUCHER_NO_5106.equals(type))
    	{
    		TvGrantpaymsgmainSxDto dto = (TvGrantpaymsgmainSxDto)bizDto;
    		if(pageRequest == null)
    		{
    			PageRequest mainRequest = new PageRequest();
    			try 
    			{
    				PageResponse response =  commonDataAccessService.findRsByDtoPaging(dto, mainRequest, "1=1", "TS_SYSUPDATE DESC");
    				pagingGrantpaymsgmain.setPage(response);
    				return response;
    			} catch (ITFEBizException e) 
    			{
    				log.error(e);
    				MessageDialog.openMessageDialog(null, "��ѯ���ݴ���");
    				return null;
    			} catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
    				log.error(e);
    				MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
    			}
    		}else
    		{
    			TvGrantpaymsgsubSxDto qdto = new TvGrantpaymsgsubSxDto();
    			qdto.setIvousrlno(dto.getIvousrlno());
    			qdto.setSpackageticketno(dto.getSpackageticketno());
    			try 
    			{
    				PageResponse response =  commonDataAccessService.findRsByDtoPaging(qdto, pageRequest, "1=1", "TS_SYSUPDATE DESC");
    				pagingGrantpaymsgsub.setPage(response);
    				return response;
    			} catch (ITFEBizException e) 
    			{
    				log.error(e);
    				MessageDialog.openMessageDialog(null, "��ѯ���ݴ���");
    				return null;
    			} catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
    				log.error(e);
    				MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
    			}
    		}
    	}else if(MsgConstant.VOUCHER_NO_5108.equals(type))
    	{
    		TvDirectpaymsgmainSxDto dto = (TvDirectpaymsgmainSxDto)bizDto;
    		TvDirectpaymsgsubSxDto qdto = new TvDirectpaymsgsubSxDto();
    		qdto.setIvousrlno(dto.getIvousrlno());
			try 
			{
				PageResponse response =  commonDataAccessService.findRsByDtoPaging(qdto, pageRequest, "1=1", "TS_SYSUPDATE DESC");
				pagingDirectpaymsgsub.setPage(response);
				return response;
			} catch (ITFEBizException e) 
			{
				log.error(e);
				MessageDialog.openMessageDialog(null, "��ѯ���ݴ���");
				return null;
			} catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
				log.error(e);
				MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			}
    	}else if(MsgConstant.VOUCHER_NO_5207.equals(type))
    	{
    		TvPayoutmsgmainSxDto dto = (TvPayoutmsgmainSxDto)bizDto;
    		TvPayoutmsgsubSxDto qdto = new TvPayoutmsgsubSxDto();
    		qdto.setSbizno(dto.getSbizno());
			try 
			{
				PageResponse response =  commonDataAccessService.findRsByDtoPaging(qdto, pageRequest, "1=1", "TS_SYSUPDATE DESC");
				pagingPayoutmsgsub.setPage(response);
				return response;
			} catch (ITFEBizException e) 
			{
				log.error(e);
				MessageDialog.openMessageDialog(null, "��ѯ���ݴ���");
				return null;
			} catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
				log.error(e);
				MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			}
    	}
    	return null;
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
    		page =  commonDataAccessService.findRsByDtoPaging(dto, pageRequest, "1=1", "S_RECVTIME DESC");
    		return page;
    		
    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}	catch (Throwable e) {	
			log.error(e);
			Exception e1=new Exception("��ѯ�����쳣��",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
		}
		return super.retrieve(pageRequest);
	}
    
    /**
	 * Direction: ������ʽ����
	 * ename: generateData
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String generateData(Object o)
    {
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "��ѡ��Ҫ������ʽ���ݵļ�¼��");
    		return "";
    	}
    	String msg = "�ù��ܽ��ڵ���ƾ֤����ֹ��������ʹ�ã��Ƿ����";
    	boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(null, "��ʾ", msg);
    	if (flag)
    	{
    		if(AdminConfirmDialogFacade.open("��Ҫ������Ȩ����������ʽ����"))
    		{
				DisplayCursor.setCursor(SWT.CURSOR_ARROW);
				try 
				{
					voucherDataCompareService.generateData(this.checkList);
					MessageDialog.openMessageDialog(null, "������ʽ���ݳɹ���");
				} catch (ITFEBizException e)
				{
					log.error(e);
					MessageDialog.openMessageDialog(null, e.getMessage());
					return null;
				}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
					log.error(e);
					MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
				}
			}
		} else 
		{
			return null;
		}
        return super.generateData(o);
    }
    
	public String goBack(Object o) {
		this.search(o);
		return super.goBack(o);
	}
	
	/**
	 * Direction: ����״̬
	 * ename: updateStatus
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String updateStatus(Object o)
    {
        try
        {
		  this.voucherDataCompareService.updateStatus(loginInfo.getSorgcode());
		  MessageDialog.openMessageDialog(null, "ͬ��״̬�ɹ���");
		} catch (ITFEBizException e) 
		{
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}
    	refreshTable2();
        return super.updateStatus(o);
    }
    
    /**
	 * Direction: ����Ϣ�����¼�
	 * ename: singleclickMain
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleclickMain(Object o){
    	TvGrantpaymsgmainSxDto parDto = (TvGrantpaymsgmainSxDto) o;
    	PageRequest subpageRequest = new PageRequest();
    	this.multiRetrieve(subpageRequest, MsgConstant.VOUCHER_NO_5106, parDto);
    	editor.fireModelChanged();
        return super.singleclickMain(o);
    }
    
	/**
	 * Direction: ����Ϣ˫���¼�
	 * ename: doubleclickMain
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String doubleclickMain(Object o){
    	TvGrantpaymsgmainSxDto parDto = (TvGrantpaymsgmainSxDto) o;
    	PageRequest subpageRequest = new PageRequest();
    	this.multiRetrieve(subpageRequest, MsgConstant.VOUCHER_NO_5106, parDto);
    	editor.fireModelChanged();
        return super.doubleclickMain(o);
    }
    
    public void refreshTable(){
    	init();
		checkList.clear();
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);	
    }
    
    public void refreshTable2(){
    	init2();
		checkList.clear();
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);	
    }

	public List<TvVoucherinfoSxDto> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<TvVoucherinfoSxDto> checkList) {
		this.checkList = checkList;
	}

}
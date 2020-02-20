package com.cfcc.itfe.client.dataquery.voucherhissearch;

import itferesourcepackage.AllVoucherTypeEnumFactory;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;
import org.eclipse.swt.widgets.Display;

import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositeVoucherHisOcx;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.pk.HtvVoucherinfoPK;
import com.cfcc.itfe.service.ITFELoginInfo;

/**
 * codecomment: 
 * @author db2itfe
 * @time   13-11-19 16:25:46
 * ��ϵͳ: DataQuery
 * ģ��:voucherHisSearch
 * ���:VoucherHisSearch
 */
public class VoucherHisSearchBean extends AbstractVoucherHisSearchBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(VoucherHisSearchBean.class);
    List<HtvVoucherinfoDto> checkList=null;   
	
	//�û���¼��Ϣ
	private ITFELoginInfo loginInfo;
	private String voucherType=null;
	private List<Mapper> voucherTypeList=null;
    public VoucherHisSearchBean() {
      super();
      dto = new HtvVoucherinfoDto();
      pagingcontext = new PagingContext(this);
      loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      checkList=new ArrayList();
      String dateStr = TimeFacade.getCurrentStringTime();
      dto.setScreatdate(dateStr);
      dto.setSstyear(dateStr.substring(0, 4));
      dto.setSorgcode(loginInfo.getSorgcode());
      voucherTypeList = new AllVoucherTypeEnumFactory().getEnums(null);            
    }
    

	/**
	 * Direction: ��ѯ
	 * ename: searchVoucherHis
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String searchVoucherHis(Object o){
    	if (dto.getSvtcode() == null || dto.getSvtcode().equals("")) {
			MessageDialog.openMessageDialog(null, "����ѡ��ƾ֤���ͣ�");
			return "";
		}
		refreshTable();
		return super.searchVoucherHis(o);
    }
    public void refreshTable(){
    	init();
		checkList.clear();
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
	/**
	 * Direction: ����ƾ֤��ԭչʾ
	 * ename: voucherHisView
	 * ���÷���: 
	 * viewers: ����ƾ֤�鿴����
	 * messages: 
	 */
    public String voucherHisView(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�鿴�ļ�¼��");
    		return "";
    	}
    	try{
        	ActiveXCompositeVoucherHisOcx.init(0);

    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch(Exception e){
    		log.error(e);
    		Exception e1=new Exception("ƾ֤�鿴���������쳣��",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
    	}catch(Error e){
    		log.error(e);
    		Exception e1=new Exception("ƾ֤�鿴����OCX���ó���ϵͳ��������ϵ������Ա��",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
    	}
          return super.voucherHisView(o);
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
    		page =  commonDataAccessService.findRsByDtoPaging(dto,
					pageRequest, "1=1", "S_RECVTIME DESC");
    	
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

	public List<HtvVoucherinfoDto> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<HtvVoucherinfoDto> checkList) {
		this.checkList = checkList;
	}

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
		this.voucherType = voucherType;
		this.dto.setSvtcode(voucherType);
		//��̬��ʾ
		if (MsgConstant.VOUCHER_NO_5207.equals(voucherType) || MsgConstant.VOUCHER_NO_5209.equals(voucherType)) {
			List contreaNames1 = new ArrayList();
			contreaNames1.add("ƾ֤��ѯһ���������");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
			List contreaNames = new ArrayList();
			contreaNames.add("ƾ֤��ѯһ�����Ʊ��λ");
			MVCUtils.setContentAreaVisible(editor, contreaNames, true);
		}else{
			List contreaNames = new ArrayList();
			contreaNames.add("ƾ֤��ѯһ�����Ʊ��λ");
			MVCUtils.setContentAreaVisible(editor, contreaNames, false);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("ƾ֤��ѯһ���������");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, true);
		}
		pagingcontext.setPage(new PageResponse());
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged();
	}

	public List<Mapper> getVoucherTypeList() {
		return voucherTypeList;
	}

	public void setVoucherTypeList(List<Mapper> voucherTypeList) {
		this.voucherTypeList = voucherTypeList;
	}

	public int queryVoucherJOintCount(HtvVoucherinfoDto vDto) {
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


	public String getOcxVoucherServerURL() {
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
	
	public HtvVoucherinfoDto getDto(HtvVoucherinfoDto dto) throws ITFEBizException{
		HtvVoucherinfoPK pk=new HtvVoucherinfoPK();
    	pk.setSdealno(dto.getSdealno());    	   	
    	return (HtvVoucherinfoDto) commonDataAccessService.find(pk);
    }
}
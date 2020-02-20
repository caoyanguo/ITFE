package com.cfcc.itfe.client.dataquery.tfrefundnotice;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.dataquery.tfrefundnotice.ITfRefundNoticeService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TfRefundNoticeDto;

/**
 * codecomment: 
 * @author Administrator
 * @time   17-03-27 20:07:23
 * ��ϵͳ: DataQuery
 * ģ��:TfRefundNotice
 * ���:TfRefundNotice
 */
public class TfRefundNoticeBean extends AbstractTfRefundNoticeBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TfRefundNoticeBean.class);
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
    private ITFELoginInfo logInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    List enumList = null;
    String realValue = null;
    
    public TfRefundNoticeBean() {
		super();
		searchDto = new TfRefundNoticeDto();
		searchDto.setSentrustdate(TimeFacade.getCurrentStringTime());
		searchDto.setStrecode(logInfo.getSorgcode().substring(0, 10));
		pagingcontext = new PagingContext(this);
		reportPath = "/com/cfcc/itfe/client/ireport/itfe_TF_REFUND_NOTICE.jasper";
		rsList = new ArrayList();
		paramMap = new HashMap();
		printDto = new TfRefundNoticeDto();
		reportPathDetail = "/com/cfcc/itfe/client/ireport/itfe_TF_REFUND_NOTICE_detail.jasper";
		rsListDetail = new ArrayList();
		paramMapDetail = new HashMap();
      
		enumList = new ArrayList<TdEnumvalueDto>();
		TdEnumvalueDto valuedtoa = new TdEnumvalueDto();
		valuedtoa.setStypecode("ʵ��");
		valuedtoa.setSvalue("1");
		enumList.add(valuedtoa);
		
		TdEnumvalueDto valuedtob = new TdEnumvalueDto();
		valuedtob.setStypecode("�˿�");
		valuedtob.setSvalue("2");
		enumList.add(valuedtob);
		
		TdEnumvalueDto valuedtoc = new TdEnumvalueDto();
		valuedtoc.setStypecode("���л���");
		valuedtoc.setSvalue("3");
		enumList.add(valuedtoc);
        
		TdEnumvalueDto valuedtod = new TdEnumvalueDto();
		valuedtod.setStypecode("����");
		valuedtod.setSvalue("4");
		enumList.add(valuedtod);
		
		realValue = "1";
		searchDto.setSpayoutvoutype(realValue);
    }
    
	/**
	 * Direction: ��ѯ���������
	 * ename: queryResult
	 * ���÷���: 
	 * viewers: ��ѯ�������
	 * messages: 
	 */
    public String queryResult(Object o){
    	printDto = new TfRefundNoticeDto();
    	PageRequest pageRequest = new PageRequest();
		try {
			PageResponse p = this.retrieve(pageRequest);
			if (p == null || p.getPageRowCount() == 0) {
				MessageDialog.openMessageDialog(null,"��ѯ���ݲ�����!");
				return null;
			}else{
				pagingcontext.setPage(p);
			}
		} catch (Exception e) {
			log.error("��ѯ���ݴ���", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
        return super.queryResult(o);
    }

	/**
	 * Direction: ���ص���ѯ����
	 * ename: backQuery
	 * ���÷���: 
	 * viewers: ��ѯ����
	 * messages: 
	 */
    public String backQuery(Object o){
          return super.backQuery(o);
    }

	/**
	 * Direction: ��ѡ
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	printDto = (TfRefundNoticeDto)o;
        return super.singleSelect(o);
    }

	/**
	 * Direction: ����
	 * ename: toReport
	 * ���÷���: 
	 * viewers: ����
	 * messages: 
	 */
    public String toReport(Object o){
    	try {
			rsList = commonDataAccessService.findRsByDtoWithWhere(searchDto, " and 1=1 ");
		} catch (ITFEBizException e) {
			log.error("�򿪱��� �쳣",e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
    	return super.toReport(o);
    }

	/**
	 * Direction: ������ϸ
	 * ename: detail
	 * ���÷���: 
	 * viewers: ������ϸ
	 * messages: 
	 */
    public String detail(Object o){
    	if (printDto == null || printDto.getSid() == null) {
    		MessageDialog.openMessageDialog(null, "�뵥��ѡ��һ����¼��ѯ��ϸ��Ϣ��");
			return null;
		}
    	return super.detail(o);
    }

	/**
	 * Direction: ��ӡ��ϸ
	 * ename: printDetail
	 * ���÷���: 
	 * viewers: ��ϸ����
	 * messages: 
	 */
    public String printDetail(Object o){
    	rsListDetail.clear();
    	rsListDetail.add(printDto);
    	return super.printDetail(o);
    }

	/**
	 * Direction: ������ϸ
	 * ename: backToDetail
	 * ���÷���: 
	 * viewers: ������ϸ
	 * messages: 
	 */
    public String backToDetail(Object o){
          return super.backToDetail(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest request) {
    	PageResponse response = new PageResponse(request);
		try {
			String wheresql = " 1=1 ";
			searchDto.setSpayoutvoutype(realValue);
			response = commonDataAccessService.findRsByDtoWithWherePaging(searchDto, request, wheresql);
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			this.editor.fireModelChanged();
		}
		return response;
	}

	public List getEnumList() {
		return enumList;
	}

	public void setEnumList(List enumList) {
		this.enumList = enumList;
	}

	public String getRealValue() {
		return realValue;
	}

	public void setRealValue(String realValue) {
		this.realValue = realValue;
	}

}
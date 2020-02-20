package com.cfcc.itfe.client.dataquery.tfresultreconci;

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
import com.cfcc.itfe.service.dataquery.tfresultreconci.ITfResultReconciService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfResultReconciDto;

/**
 * codecomment: 
 * @author Administrator
 * @time   17-04-04 10:47:03
 * ��ϵͳ: DataQuery
 * ģ��:TfResultReconci
 * ���:TfResultReconci
 */
public class TfResultReconciBean extends AbstractTfResultReconciBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TfResultReconciBean.class);
    
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
    private ITFELoginInfo logInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    
    public TfResultReconciBean() {
      super();
      searchDto = new TfResultReconciDto();
      searchDto.setSchkdate(TimeFacade.getCurrentStringTime());
      searchDto.setStrecode(logInfo.getSorgcode().substring(0, 10));
      pagingcontext = new PagingContext(this);
      reportPath = "/com/cfcc/itfe/client/ireport/itfe_TF_RESULT_RECONCI.jasper";
      rsList = new ArrayList();
      paramMap = new HashMap();
      printDto = new TfResultReconciDto();
      reportPathDetail = "/com/cfcc/itfe/client/ireport/itfe_TF_RESULT_RECONCI_detail.jasper";
      rsListDetail = new ArrayList();
      paramMapDetail = new HashMap();
                  
    }
    
	/**
	 * Direction: ��ѯ���������
	 * ename: queryResult
	 * ���÷���: 
	 * viewers: ��ѯ�������
	 * messages: 
	 */
    public String queryResult(Object o){
    	printDto = new TfResultReconciDto();
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
    	printDto = (TfResultReconciDto)o;
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
    	TfResultReconciDto rsRecDto = null;
    	try {
			rsList = commonDataAccessService.findRsByDtoWithWhere(searchDto, " and 1=1 ");
			for(int i = 0;i<rsList.size();i++){
				rsRecDto = (TfResultReconciDto)rsList.get(i);
				String svoutype = rsRecDto.getSpayoutvoutype();
				if(svoutype != null && !svoutype.trim().equals("")){
					if(svoutype.equals("1")){
						rsRecDto.setSpayoutvoutype("ʵ��");
					}else if(svoutype.equals("2")){
						rsRecDto.setSpayoutvoutype("�˿�");
					}else if(svoutype.equals("3")){
						rsRecDto.setSpayoutvoutype("���л���");
					}else if(svoutype.equals("4")){
						rsRecDto.setSpayoutvoutype("�ʽ������ִ");
					}else if(svoutype.equals("5")){
						rsRecDto.setSpayoutvoutype("�˿�֪ͨ����");
					}else if(svoutype.equals("6")){
						rsRecDto.setSpayoutvoutype("����");
					}
				}
				if(rsRecDto.getScheckresult().equals("0")){
					rsRecDto.setScheckresult("�������");
				}else{
					rsRecDto.setScheckresult("���˲���");
				}
			}
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
    	String svoutype = printDto.getSpayoutvoutype();
    	if(svoutype != null && !svoutype.trim().equals("")){
	    	if(svoutype.equals("1")){
	    		printDto.setSpayoutvoutype("ʵ��");
			}else if(svoutype.equals("2")){
				printDto.setSpayoutvoutype("�˿�");
			}else if(svoutype.equals("3")){
				printDto.setSpayoutvoutype("���л���");
			}else if(svoutype.equals("4")){
				printDto.setSpayoutvoutype("�ʽ������ִ");
			}else if(svoutype.equals("5")){
				printDto.setSpayoutvoutype("�˿�֪ͨ����");
			}else if(svoutype.equals("6")){
				printDto.setSpayoutvoutype("����");
			}
    	}
    	if(printDto.getScheckresult().equals("0")){
    		printDto.setScheckresult("�������");
		}else{
			printDto.setScheckresult("���˲���");
		}
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
			response = commonDataAccessService.findRsByDtoWithWherePaging(searchDto, request, wheresql);
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			this.editor.fireModelChanged();
		}
		return response;
	}

}
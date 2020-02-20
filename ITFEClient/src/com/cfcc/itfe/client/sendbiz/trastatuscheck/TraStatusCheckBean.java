package com.cfcc.itfe.client.sendbiz.trastatuscheck;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author db2admin
 * @time   12-03-12 15:15:32
 * ��ϵͳ: SendBiz
 * ģ��:TraStatusCheck
 * ���:TraStatusCheck
 */
public class TraStatusCheckBean extends AbstractTraStatusCheckBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TraStatusCheckBean.class);
    private String taxorg;//���ջ���
	private String searchType;//��ѯ����
    private String oriMsgNo;//ԭ���ı��
    private String oriEntrustDate;//ԭί������
    private String oriPackNo;//ԭ����ˮ��
    private String oriTraNo;//ԭ������ˮ��
    private List resultList = null;
    public TraStatusCheckBean() {
    	 super();
    	resultList = new ArrayList();
     
    }
    
	/**
	 * Direction: ��ѯ
	 * ename: TraStatusSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String traStatusSelect(Object o){
    	
    	if (searchType==null||searchType.equals("")) {
			MessageDialog.openMessageDialog(null, "��ѡ���ѯ���ͣ�");
			return null;
		}
    	if (oriMsgNo==null||oriMsgNo.equals("")) {
			MessageDialog.openMessageDialog(null, "ԭ���ı�Ų���Ϊ�գ�");
			return null;
		}
    	if (oriEntrustDate==null||oriEntrustDate.equals("")) {
			MessageDialog.openMessageDialog(null, "ԭί�����ڲ���Ϊ�գ�");
			return null;
		}
    	if (oriEntrustDate.length()!=8) {
			MessageDialog.openMessageDialog(null, "ԭί�����ڸ�ʽ����ȷ��");
			return null;
		}
    	if (searchType.equals("0")) {
    		if (oriTraNo==null||oriTraNo.equals("")) {
    			MessageDialog.openMessageDialog(null, "ԭ������ˮ�Ų���Ϊ�գ�");
    			return null;
    		}
		}
    	if (searchType.equals("1")) {
    		if (oriPackNo==null||oriPackNo.equals("")) {
    			MessageDialog.openMessageDialog(null, "ԭ����ˮ�Ų���Ϊ�գ�");
    			return null;
    		}
		}
    	
    	try {
			traStatusCheckService.traStatusCheck(taxorg, searchType, oriMsgNo, oriEntrustDate, oriPackNo, oriTraNo);
		} catch (ITFEBizException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          return super.traStatusSelect(o);
    }

	/**
	 * Direction: �鿴��ѯ���
	 * ename: ViewSelectResult
	 * ���÷���: 
	 * viewers: 9003����״̬��ѯ��Ϣ����
	 * messages: 
	 */
    public String viewSelectResult(Object o){
    	try {
			resultList  = traStatusCheckService.viewSelectRusult(taxorg, searchType, oriMsgNo, oriEntrustDate, oriPackNo, oriTraNo);
			if (resultList==null||resultList.size()==0) {
    			MessageDialog.openMessageDialog(null, "��������û�м��������������ļ�¼��");
    		}
		} catch (ITFEBizException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		editor.fireModelChanged();
          return super.viewSelectResult(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		TraStatusCheckBean.log = log;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public String getOriMsgNo() {
		return oriMsgNo;
	}

	public void setOriMsgNo(String oriMsgNo) {
		this.oriMsgNo = oriMsgNo;
	}

	public String getOriEntrustDate() {
		return oriEntrustDate;
	}

	public void setOriEntrustDate(String oriEntrustDate) {
		this.oriEntrustDate = oriEntrustDate;
	}

	public String getOriPackNo() {
		return oriPackNo;
	}

	public void setOriPackNo(String oriPackNo) {
		this.oriPackNo = oriPackNo;
	}

	public String getOriTraNo() {
		return oriTraNo;
	}

	public void setOriTraNo(String oriTraNo) {
		this.oriTraNo = oriTraNo;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
    public String getTaxorg() {
		return taxorg;
	}

	public void setTaxorg(String taxorg) {
		this.taxorg = taxorg;
	}

}
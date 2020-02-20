package com.cfcc.itfe.client.dataquery.batchbizdetailqueryforfinance;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.persistence.dto.HtfPaymentDetailsmainDto;
import com.cfcc.itfe.persistence.dto.HtfPaymentDetailssubDto;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailsmainDto;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailssubDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author Administrator
 * @time   14-11-04 16:36:35
 * ��ϵͳ: DataQuery
 * ģ��:batchBizDetailQueryForFinance
 * ���:BatchBizDetailQueryForFinance
 */
public class BatchBizDetailQueryForFinanceBean extends AbstractBatchBizDetailQueryForFinanceBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(BatchBizDetailQueryForFinanceBean.class);
    private BatchBizDetailSubQueryForFinanceBean curSubBean;
    private BatchBizDetailHisQueryForFinanceBean hisMainBean;
    private BatchBizDetailHisSubQueryForFinanceBean hisSubBean;
    private ITFELoginInfo logInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    
    public BatchBizDetailQueryForFinanceBean() {
    	super();
    	curSearchDto = new TfPaymentDetailsmainDto();
    	curSubSearchDto = new TfPaymentDetailssubDto();
    	hisSearchDto = new HtfPaymentDetailsmainDto();
    	hisSubSearchDto = new HtfPaymentDetailssubDto();
    	pagingContext = new PagingContext(this);
    	curSubBean = new BatchBizDetailSubQueryForFinanceBean();
    	hisMainBean = new BatchBizDetailHisQueryForFinanceBean();
    	hisSubBean = new BatchBizDetailHisSubQueryForFinanceBean();
  
    	curSearchDto.setSorgcode(logInfo.getSorgcode());
    	hisSearchDto.setSorgcode(logInfo.getSorgcode());
    	
    	enumList = new ArrayList<TdEnumvalueDto>();
    	TdEnumvalueDto valuedtoa = new TdEnumvalueDto();
		valuedtoa.setStypecode("��ǰ��");
		valuedtoa.setSvalue("0");
		enumList.add(valuedtoa);
		
		TdEnumvalueDto valuedtob = new TdEnumvalueDto();
		valuedtob.setStypecode("��ʷ��");
		valuedtob.setSvalue("1");
		enumList.add(valuedtob);
		realValue = "0";
    }
    
	/**
	 * Direction: ��ѡ
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	if(o instanceof TfPaymentDetailsmainDto){
    		curSubSearchDto.setIvousrlno(((TfPaymentDetailsmainDto)o).getIvousrlno());
    		curSubBean.searchDtoList(curSubSearchDto);
    	}else if(o instanceof HtfPaymentDetailsmainDto){
    		hisSubSearchDto.setIvousrlno(((HtfPaymentDetailsmainDto)o).getIvousrlno());
    		hisSubBean.searchDtoList(hisSubSearchDto);
    	}
    	this.editor.fireModelChanged();
    	return super.singleSelect(o);
    }

	/**
	 * Direction: ˫��
	 * ename: doubleClick
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String doubleClick(Object o){
    	if(o instanceof TfPaymentDetailsmainDto){
    		curSubSearchDto.setIvousrlno(((TfPaymentDetailsmainDto)o).getIvousrlno());
    		curSubBean.searchDtoList(curSubSearchDto);
    	}else if(o instanceof HtfPaymentDetailsmainDto){
    		hisSubSearchDto.setIvousrlno(((HtfPaymentDetailsmainDto)o).getIvousrlno());
    		hisSubBean.searchDtoList(hisSubSearchDto);
    	}
    	this.editor.fireModelChanged();
    	return super.doubleClick(o);
    }

	/**
	 * Direction: ��ѯ
	 * ename: searchToList
	 * ���÷���: 
	 * viewers: ��������ҵ��֧����ϸ��ǰ��Ϣ��ѯ���
	 * messages: 
	 */
    public String searchToList(Object o){
    	PageRequest pageRequest = new PageRequest();
		try {
			if(realValue.equals("0")){
				PageResponse p = this.retrieve(pageRequest);
				if (p == null || p.getPageRowCount() == 0) {
					MessageDialog.openMessageDialog(null, "��ѯ���ݲ�����!");
					return null;
				}
				pagingContext.setPage(p);
				return super.searchToList(o);
			}else{
				copyValue();
				hisMainBean.searchDtoList(hisSearchDto);
				return "��������ҵ��֧����ϸ��ǰ��ʷ��Ϣ��ѯ���";
			}
		} catch (Exception e) {
			log.error("��ѯ���ݴ���", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
    }

	/**
	 * Direction: ����
	 * ename: backToSearch
	 * ���÷���: 
	 * viewers: ��������ҵ��֧����ϸ��ѯ����
	 * messages: 
	 */
    public String backToSearch(Object o){
    	curSubBean = new BatchBizDetailSubQueryForFinanceBean();
    	hisSubBean = new BatchBizDetailHisSubQueryForFinanceBean();
    	return super.backToSearch(o);
    }

	/**
	 * Direction: �����ļ�
	 * ename: exportFile
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String exportFile(Object o){
    	// ѡ�񱣴�·��
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent().getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
			return "";
		}
		String clientFilePath;
		String dirsep = File.separator;
		String serverFilePath;
		try {
			if(realValue.equals("0")){
				serverFilePath = batchBizDetailQueryForFinanceService.exportFile(curSearchDto,realValue).replace("\\","/");
			}else{
				serverFilePath = batchBizDetailQueryForFinanceService.exportFile(hisSearchDto,realValue).replace("\\","/");
			}
			
			int j = serverFilePath.lastIndexOf("/");
			String partfilepath  = serverFilePath.replaceAll(serverFilePath.substring(0, j + 1), "");
			clientFilePath = filePath + dirsep + partfilepath;
			File f = new File(clientFilePath);
			File dir = new File(f.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			ClientFileTransferUtil.downloadFile(serverFilePath, clientFilePath);
			MessageDialog.openMessageDialog(null, "�����ļ��ɹ����ļ������ڣ�\n"+ clientFilePath);
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}
		return super.exportFile(o);
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
			response = commonDataAccessService.findRsByDtoWithWherePaging(curSearchDto, request, wheresql);
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			this.editor.fireModelChanged();
		}
		return response;
	}
    
    public void copyValue(){
    	hisSearchDto.setStrecode(curSearchDto.getStrecode());
    	hisSearchDto.setSstatus(curSearchDto.getSstatus());
    	hisSearchDto.setSpackageno(curSearchDto.getSpackageno());
    	hisSearchDto.setSadmdivcode(curSearchDto.getSadmdivcode());
    	hisSearchDto.setSstyear(curSearchDto.getSstyear());
    	hisSearchDto.setSvtcode(curSearchDto.getSvtcode());
    	hisSearchDto.setSvoudate(curSearchDto.getSvoudate());
    	hisSearchDto.setSoriginalvtcode(curSearchDto.getSoriginalvtcode());
    	hisSearchDto.setSoriginalvoucherno(curSearchDto.getSoriginalvoucherno());
    	hisSearchDto.setSfundtypecode(curSearchDto.getSfundtypecode());
    	hisSearchDto.setSpaydictateno(curSearchDto.getSpaydictateno());
    	hisSearchDto.setSpaymsgno(curSearchDto.getSpaymsgno());
    	hisSearchDto.setSpayentrustdate(curSearchDto.getSpayentrustdate());
    	hisSearchDto.setSpaysndbnkno(curSearchDto.getSpaysndbnkno());
    	hisSearchDto.setNsumamt(curSearchDto.getNsumamt());
    	hisSearchDto.setSagencycode(curSearchDto.getSagencycode());
    	hisSearchDto.setSagencyname(curSearchDto.getSagencyname());
    	hisSearchDto.setSpayacctno(curSearchDto.getSpayacctno());
    	hisSearchDto.setSpayacctname(curSearchDto.getSpayacctname());
    	hisSearchDto.setSpayacctbankname(curSearchDto.getSpayacctbankname());
    	hisSearchDto.setSpaybankcode(curSearchDto.getSpaybankcode());
    	hisSearchDto.setSpaybankname(curSearchDto.getSpaybankname());
    	hisSearchDto.setSbusinesstypecode(curSearchDto.getSbusinesstypecode());
    	hisSearchDto.setSbusinesstypename(curSearchDto.getSbusinesstypename());
    	hisSearchDto.setSpaytypecode(curSearchDto.getSpaytypecode());
    	hisSearchDto.setSpaytypename(curSearchDto.getSpaytypename());
    	hisSearchDto.setSxpaydate(curSearchDto.getSxpaydate());
    	hisSearchDto.setNxsumamt(curSearchDto.getNxsumamt());
    }

	public BatchBizDetailSubQueryForFinanceBean getCurSubBean() {
		return curSubBean;
	}

	public void setCurSubBean(BatchBizDetailSubQueryForFinanceBean curSubBean) {
		this.curSubBean = curSubBean;
	}

	public BatchBizDetailHisQueryForFinanceBean getHisMainBean() {
		return hisMainBean;
	}

	public void setHisMainBean(BatchBizDetailHisQueryForFinanceBean hisMainBean) {
		this.hisMainBean = hisMainBean;
	}

	public BatchBizDetailHisSubQueryForFinanceBean getHisSubBean() {
		return hisSubBean;
	}

	public void setHisSubBean(BatchBizDetailHisSubQueryForFinanceBean hisSubBean) {
		this.hisSubBean = hisSubBean;
	}

}
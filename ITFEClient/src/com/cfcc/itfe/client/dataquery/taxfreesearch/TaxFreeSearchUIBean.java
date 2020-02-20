package com.cfcc.itfe.client.dataquery.taxfreesearch;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.persistence.dto.HtvFreeDto;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TvFreeDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author db2admin
 * @time   13-02-21 11:27:13
 * ��ϵͳ: DataQuery
 * ģ��:TaxFreeSearch
 * ���:TaxFreeSearchUI
 */
public class TaxFreeSearchUIBean extends AbstractTaxFreeSearchUIBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TaxFreeSearchUIBean.class);
    
    private String selectedtable;
    private List<TdEnumvalueDto> statelist;
    private ITFELoginInfo loginfo;
    private List<IDto> selectRs=null;
    public TaxFreeSearchUIBean() {
      selectedtable="0";
      dto = new TvFreeDto();
      pagingcontext = new PagingContext(this);
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      dto.setSbookorgcode(loginfo.getSorgcode());
      selectRs=new ArrayList<IDto>();
      init();           
    }
    
	/**
	 * Direction: ��ѯ
	 * ename: search
	 * ���÷���: 
	 * viewers: ��ֵ���Ϣ�б�
	 * messages: 
	 */
    public String search(Object o){
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		if (pageResponse == null || pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, " ��ѯ�޼�¼��");
			return "";
		}
          return super.search(o);
    }

	/**
	 * Direction: ����
	 * ename: reback
	 * ���÷���: 
	 * viewers: ��ֵ���ѯ����
	 * messages: 
	 */
    public String reback(Object o){
          return super.reback(o);
    }

	/**
	 * Direction: ȫѡ
	 * ename: selectAll
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectAll(Object o){
          return super.selectAll(o);
    }

	/**
	 * Direction: ����ʧ��
	 * ename: updateFail
	 * ���÷���: 
	 * viewers: ��ֵ���Ϣ�б�
	 * messages: 
	 */
    public String updateFail(Object o){
    	if(selectRs==null||selectRs.size()<=0){
    		MessageDialog.openMessageDialog(null, "��ѡ������һ����¼��");
    		return null;
    	}
    	for(IDto freedto: (List<IDto>)selectRs){
    		
    		if (freedto instanceof TvFreeDto) {
    			int i =0;
    			TvFreeDto  _dto  =(TvFreeDto) freedto;
    			if (_dto.getSstatus().equals(DealCodeConstants.DEALCODE_ITFE_SUCCESS) && i==0) {
    				Boolean b = org.eclipse.jface.dialogs.MessageDialog.openQuestion(null, "��ʾ", "ѡ��ļ�¼�����Ѿ�����Ϊ�ɹ��ļ�¼,�Ƿ�һ�����Ϊʧ�ܣ�");
    				if (b) {
    					_dto.setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
    				}
    				i++;
    			} else{
    			    _dto.setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
    			}
			}else if (freedto instanceof HtvFreeDto){
				int i = 0;
				HtvFreeDto  _dto  =(HtvFreeDto) freedto;
				if (_dto.getSstatus().equals(DealCodeConstants.DEALCODE_ITFE_SUCCESS) && i==0) {
					Boolean b = org.eclipse.jface.dialogs.MessageDialog.openQuestion(null, "��ʾ", "ѡ��ļ�¼�����Ѿ�����Ϊ�ɹ��ļ�¼,�Ƿ�һ�����Ϊʧ�ܣ�");
					if (b) {
						_dto.setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
					}
					i++;
				}else{
					_dto.setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
				} 
			}	
    	}
    	try {
			this.taxFreeSearchService.updateFail(selectRs);
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		MessageDialog.openMessageDialog(null, "��������ʧ�ܳɹ���");
		selectRs=new ArrayList<IDto>();
    	return super.updateFail(o);
    }
    private void init(){
    	this.statelist = new ArrayList<TdEnumvalueDto>();

		TdEnumvalueDto valuedto3 = new TdEnumvalueDto();
		valuedto3.setStypecode("��ǰ��");
		valuedto3.setSvalue("0");
		this.statelist.add(valuedto3);
		
		TdEnumvalueDto valuedto2 = new TdEnumvalueDto();
		valuedto2.setStypecode("��ʷ��");
		valuedto2.setSvalue("1");
		this.statelist.add(valuedto2);
    }
	/**
	 * Direction: ���³ɹ�
	 * ename: updateSuccess
	 * ���÷���: 
	 * viewers: ��ֵ���Ϣ�б�
	 * messages: 
	 */
    public String updateSuccess(Object o){
    	if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�ò�������²�ѯ���������˿ⱨ�ĵ�״̬Ϊ�ɹ����Ƿ�����˲�����")) {
		 return null;
    	}
    	List list=new ArrayList();
    	try {
    		dto.setSbookorgcode(loginfo.getSorgcode());
    		if(selectedtable.equals("0")){
    			list=commonDataAccessService.findRsByDto(dto);
    			for(TvFreeDto tdd:(List<TvFreeDto>)list){
    				tdd.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
    			}
    		}else{
    			HtvFreeDto htvdto = new HtvFreeDto();
    			BeanUtils.copyProperties(htvdto, dto);
    			list=commonDataAccessService.findRsByDto(htvdto);
    			for(HtvFreeDto tdd:(List<HtvFreeDto>)list){
    				tdd.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
    			}
    		}
			
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
    	try {
			this.taxFreeSearchService.updateSuccess(list);
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		MessageDialog.openMessageDialog(null, "�����������ݳɹ���");
		selectRs=new ArrayList<IDto>();
    	return this.search(0);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	try {
    		if(selectedtable.equals("0")){
				return commonDataAccessService.findRsByDtoWithWherePaging(dto,
						pageRequest, "1=1");
			
		}else if(selectedtable.equals("1")){
			HtvFreeDto hdto = new HtvFreeDto();
			BeanUtils.copyProperties(hdto, dto);
			return commonDataAccessService.findRsByDtoWithWherePaging(hdto,
					pageRequest, "1=1");
		}
    	} catch (Exception e) {
    		log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	public String getSelectedtable() {
		return selectedtable;
	}

	public void setSelectedtable(String selectedtable) {
		this.selectedtable = selectedtable;
	}

	public List<TdEnumvalueDto> getStatelist() {
		return statelist;
	}

	public void setStatelist(List<TdEnumvalueDto> statelist) {
		this.statelist = statelist;
	}

	public List<IDto> getSelectRs() {
		return selectRs;
	}

	public void setSelectRs(List<IDto> selectRs) {
		this.selectRs = selectRs;
	}

}
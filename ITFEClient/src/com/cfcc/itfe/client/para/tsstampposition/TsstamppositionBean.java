package com.cfcc.itfe.client.para.tsstampposition;


import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.EditStampUserMsgDialogFacade;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsStamppositionDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
/**
 * codecomment: 
 * @author db2admin
 * @time   11-08-01 14:49:22
 * ��ϵͳ: Para
 * ģ��:tsstampposition
 * ���:Tsstampposition
 */
@SuppressWarnings("unchecked")
public class TsstamppositionBean extends AbstractTsstamppositionBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TsstamppositionBean.class);
    private ITFELoginInfo loginfo = null;
    TsStamppositionDto selectStamppositionDto = null;
    TsStamppositionDto readStamppositionDto = null;
    private List<TsStamppositionDto> checklist = null;
    private String strecode = null;
    private List<IDto> admlist = null;
    private String sadmdivcode = null;
	public TsstamppositionBean() {
      super();
      tsstamppositionDto = new TsStamppositionDto();
      selectStamppositionDto = new TsStamppositionDto();
      readStamppositionDto = new TsStamppositionDto();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      pagingcontext = new PagingContext(this);
      init();            
    }
	private void init() {
		checklist = new ArrayList<TsStamppositionDto>();
		tsstamppositionDto.setSorgcode(loginfo.getSorgcode());
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}
	/**
	 * Direction: ��ת¼�����
	 * ename: goInput
	 * ���÷���: 
	 * viewers: ¼�����
	 * messages: 
	 */
    public String goInput(Object o){
    	selectStamppositionDto = new TsStamppositionDto();
    	readStamppositionDto.setSorgcode(loginfo.getSorgcode());
    	tsstamppositionDtoList = null;
        return super.goInput(o);
    }
    /**
	 * У���ֶ�����
	 * 
	 * @param idto
	 * @return
	 */
	public String verifyPorp(TsStamppositionDto idto) {
		if (null == idto) {
			return "Ҫ�����ļ�¼Ϊ�գ���ȷ�ϣ�";
		}

		if (null == idto.getSadmdivcode()|| "".equals(idto.getSadmdivcode().trim())) {
			return "�������벻��Ϊ�գ�";
		}

		if (null == idto.getSvtcode() || "".equals(idto.getSvtcode())) {
			return "ƾ֤���벻��Ϊ�գ�";
		}

		return null;
	}
	/**
	 * Direction: ¼�뱣��
	 * ename: inputSave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String inputSave(Object o){
    	String verifyRs = null;
    	if(tsstamppositionDtoList==null || tsstamppositionDtoList.size()==0){
    		MessageDialog.openMessageDialog(null, "δ��ȡ��ǩ��λ����Ϣ��");
    		return null;
    	}
    	for(int i=0;i<tsstamppositionDtoList.size();i++){
    		TsStamppositionDto tsstamppositionDto = (TsStamppositionDto) tsstamppositionDtoList.get(i);
    		verifyRs = verifyPorp(tsstamppositionDto);
    		if (null != verifyRs) {
    			MessageDialog.openMessageDialog(null, verifyRs);
    			return null;
    		}
    		if(tsstamppositionDto.getSstampuser()==null)
    			tsstamppositionDto.setSstampuser("");
    		try {
    			tsstamppositionService.addInfo(tsstamppositionDto);
    		} catch (Throwable e) {
    			log.error(e);
    			MessageDialog.openErrorDialog(null, e);
    			return super.inputSave(o);
    		}
    	}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
        return super.inputSave(o);
    }

	/**
	 * Direction: ���ص�ά������
	 * ename: backMaintenance
	 * ���÷���: 
	 * viewers: ��Ϣ��ѯ
	 * messages: 
	 */
    public String backMaintenance(Object o){
    	tsstamppositionDtoList = new ArrayList();
    	readStamppositionDto = new TsStamppositionDto();
    	selectStamppositionDto = new TsStamppositionDto();
		init();
        return super.backMaintenance(o);
    }
    /**
	 * Direction: �����޸�ǩ���û�
	 * ename: editStampUsers
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String editStampUsers(Object o){
    	try {
			EditStampUserMsgDialogFacade.open(checklist, commonDataAccessService);
			this.queryBudget(o);
			editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
			checklist = new ArrayList<TsStamppositionDto>();
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "�����޸�ǩ���û������쳣��");
			return null;
		}
        return super.editStampUsers(o);
    }
	/**
	 * Direction: 
	 * ename: backMaintenance
	 * ���÷���: 
	 * viewers: ǩ��λ����Ϣ��ѯ
	 * messages: 
	 */
    public String queryStampPosition(Object o){
    	
    	if (null == readStamppositionDto.getStrecode()|| "".equals(readStamppositionDto.getStrecode().trim())) {
			MessageDialog.openMessageDialog(null, "������벻��Ϊ�գ�");
			return null;
		}

		if (null == readStamppositionDto.getSvtcode() || "".equals(readStamppositionDto.getSvtcode())) {
			MessageDialog.openMessageDialog(null, "ƾ֤���Ͳ���Ϊ�գ�");
			return null;
		}
    	
    	try {
    		boolean flag = true;
    		//��ѯǩ����Ϣ�Ƿ��Ѿ�����
    		List stampPositionRs = commonDataAccessService.findRsByDto(readStamppositionDto);
    		if(stampPositionRs!=null && stampPositionRs.size() > 0){
    			flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
        				null, "�Ѿ�����ǩ��λ����Ϣ!", "�Ƿ�ȷ��Ҫ���¶�ȡ��");
    		}
    		if(flag)
			tsstamppositionDtoList= tsstamppositionService.queryStampPosition(loginfo.getScertId(), readStamppositionDto.getStrecode(), Integer.parseInt(loginfo.getCurrentDate().substring(0, 4)), readStamppositionDto.getSvtcode());
    		if(tsstamppositionDtoList!=null&&tsstamppositionDtoList.size()>0){
    			TsStamppositionDto dto = (TsStamppositionDto)tsstamppositionDtoList.get(0);
    			readStamppositionDto.setSadmdivcode(dto.getSadmdivcode());
    		}else{
    			MessageDialog.openMessageDialog(null, "δ��ȡ��ǩ��λ����Ϣ��");
    			tsstamppositionDtoList.clear();
        		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
    			return null;
    		}
    			
    	} catch (NumberFormatException e) {
    		MessageDialog.openMessageDialog(null, "ǩ��λ����Ϣ��ѯ�����쳣��");
			return null;
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "ǩ��λ����Ϣ��ѯ�����쳣��");
			return null;
		}
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.queryStampPosition(o);
    }

	/**
	 * Direction: ��ѡ
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	selectStamppositionDto = (TsStamppositionDto) o;
        return super.singleSelect(o);
    }

	/**
	 * Direction: ɾ��
	 * ename: delete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if (checklist==null||checklist.size()<=0) {
			MessageDialog.openMessageDialog(null, "��ѡ����Ҫɾ���ļ�¼!");
			return null;
		}

		boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
				null, "ɾ������ȷ��!", "�Ƿ�ȷ��Ҫɾ��ѡ�еļ�¼��");
		if (flag) {
			try {
				for(int i=0;i<checklist.size();i++)
				{
					selectStamppositionDto = (TsStamppositionDto)checklist.get(i);
					tsstamppositionService.delInfo(selectStamppositionDto);
				}
			} catch (Throwable e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
				return super.delete(o);
			}
			MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
			selectStamppositionDto = new TsStamppositionDto();
		}

		init();
		editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
          return super.delete(o);
    }

	/**
	 * Direction: ���޸Ľ���
	 * ename: goModify
	 * ���÷���: 
	 * viewers: �޸Ľ���
	 * messages: 
	 */
    public String goModify(Object o){
    		if(checklist==null||checklist.size()!=1)
    		{
    			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
    			return null;
    		}
    		else
    			selectStamppositionDto = checklist.get(0);
          return super.goModify(o);
    }

	/**
	 * Direction: �޸ı���
	 * ename: modifySave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String modifySave(Object o){
    	String verifyRs = verifyPorp(selectStamppositionDto);
		if (null != verifyRs) {
			MessageDialog.openMessageDialog(null, verifyRs);
			return null;
		}
		try {
			if(selectStamppositionDto != null && selectStamppositionDto.getSstampuser() == null) {
				selectStamppositionDto.setSstampuser("");
			}
			tsstamppositionService.modInfo(selectStamppositionDto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		this.queryBudget(o);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.backMaintenance(o);
    }

	/**
	 * Direction: ��ѯ
	 * ename: queryBudget
	 * ���÷���: 
	 * viewers: ��ѯ���
	 * messages: 
	 */
    public String queryBudget(Object o){
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.queryBudget(o);
    }
    /**
	 * Direction: ȫѡ
	 * ename: selectall
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectall(Object o){
    	if (null == checklist || checklist.size() == 0) {
			checklist = new ArrayList();
			checklist.addAll(pagingcontext.getPage().getData());
		} else {
			checklist.clear();
		}
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.selectall(o);
    }
	/**
	 * Direction: ����
	 * ename: goBack
	 * ���÷���: 
	 * viewers: ��Ϣ��ѯ
	 * messages: 
	 */
    public String goBack(Object o){
    	//tsstamppositionDto = new TsStamppositionDto();
    	selectStamppositionDto = new TsStamppositionDto();
        return super.goBack(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {
    	if (null == tsstamppositionDto) {
    		tsstamppositionDto = new TsStamppositionDto();
    		tsstamppositionDto.setSorgcode(loginfo.getSorgcode());
		}
		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(tsstamppositionDto,
					arg0, "1=1 ");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(arg0);
	}
	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}
	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}
	public TsStamppositionDto getSelectStamppositionDto() {
		return selectStamppositionDto;
	}
	public void setSelectStamppositionDto(TsStamppositionDto selectStamppositionDto) {
		this.selectStamppositionDto = selectStamppositionDto;
	}
	public TsStamppositionDto getReadStamppositionDto() {
		return readStamppositionDto;
	}
	public void setReadStamppositionDto(TsStamppositionDto readStamppositionDto) {
		this.readStamppositionDto = readStamppositionDto;
	}
	public List getChecklist() {
		return checklist;
	}
	public void setChecklist(List checklist) {
		this.checklist = checklist;
	}
	public String getStrecode() {
		return strecode;
	}
	public void setStrecode(String strecode) {
		this.strecode = strecode;
		readStamppositionDto.setStrecode(strecode);
		if(strecode!=null&&!"".equals(strecode))
		{
			TsConvertfinorgDto searchDto = new TsConvertfinorgDto();
			searchDto.setSorgcode(loginfo.getSorgcode());
			searchDto.setStrecode(strecode);
			try {
				admlist = commonDataAccessService.findRsByDto(searchDto);
				if(admlist!=null&&admlist.size()>0)
				{
					TsConvertfinorgDto tempdto = null;
					for(int i=0;i<admlist.size();i++)
					{
						tempdto = (TsConvertfinorgDto)admlist.get(i);
						if(tempdto.getSadmdivcode()==null)
							tempdto.setSadmdivcode("");
					}
					sadmdivcode = ((TsConvertfinorgDto)admlist.get(0)).getSadmdivcode();
				}
			} catch (ITFEBizException e) {
				
			}
			editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		}
	}
	public List<IDto> getAdmlist() {
		return admlist;
	}
	public void setAdmlist(List<IDto> admlist) {
		this.admlist = admlist;
	}
	public String getSadmdivcode() {
		return sadmdivcode;
	}
	public void setSadmdivcode(String sadmdivcode) {
		this.sadmdivcode = sadmdivcode;
	}

}
package com.cfcc.itfe.client.para.tdcorp;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.pk.TsUsersPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author zhang
 * @time   10-12-28 12:42:35
 * ��ϵͳ: Para
 * ģ��:TdCorp
 * ���:TdCorp
 */
public class TdCorpBean extends AbstractTdCorpBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TdCorpBean.class);
    
    ITFELoginInfo loginfo;
    private String sbookorgcode;
    private TsUsersPK tsuserspk=new TsUsersPK();
    private TsUsersDto tsuser=new TsUsersDto();
    private TdCorpDto searchDto;
    public TdCorpBean() {
      super();
      dto=new TdCorpDto();
      searchDto = new TdCorpDto();
      pagingcontext = new PagingContext(this);
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
		.getDefault().getLoginInfo();
      tsuserspk.setSorgcode(loginfo.getSorgcode());
      tsuserspk.setSusercode(loginfo.getSuserCode());
      try {
    	  tsuser=(TsUsersDto) commonDataAccessService.find(tsuserspk);
	} catch (Throwable e) {
		log.error(e);
		MessageDialog.openErrorDialog(null, e);
	}
      searchDto.setSbookorgcode(loginfo.getSorgcode());
      //init();
    }
    
    private void init() {
    	searchDto = new TdCorpDto();
    	searchDto.setSbookorgcode(loginfo.getSorgcode());

		/*PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);*/
	}
    public String searchRs(Object o){
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		if(pageResponse.getData().size()<=0){
			MessageDialog.openMessageDialog(null,"û�в�ѯ����������������");
		}
		pagingcontext.setPage(pageResponse);
    	return super.searchRs(o);
    }
    public String rebackSearch(Object o){
    	searchDto.setSbookorgcode(loginfo.getSorgcode());
    	return super.rebackSearch(o);
    }
	/**
	 * Direction: ��ת¼�����
	 * ename: goInput
	 * ���÷���: 
	 * viewers: ¼�����
	 * messages: 
	 */
    public String goInput(Object o){
    	dto = new TdCorpDto();
    	dto.setSbookorgcode(loginfo.getSorgcode());
          return super.goInput(o);
    }

	/**
	 * Direction: ¼�뱣��
	 * ename: inputSave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String inputSave(Object o){
    	if(datacheck(dto,"add")){
    		return null;
    	}
    	try {
    		tdCorpService.addInfo(dto);
    		
   		} catch (Throwable e) {
   			log.error(e);
   			MessageDialog.openErrorDialog(null, e);
   			
   			return super.inputSave(o);
   		}
   		MessageDialog.openMessageDialog(null,StateConstant.INPUTSAVE);
   		
//   		init();
   		editor.fireModelChanged();
//        return super.inputSave(o);
        return searchRs(o);
    }

	/**
	 * Direction: ���ص�ά������
	 * ename: backMaintenance
	 * ���÷���: 
	 * viewers: ά������
	 * messages: 
	 */
    public String backMaintenance(Object o){
    	dto = new  TdCorpDto();
		init();
		setSbookorgcode("");
          return super.backMaintenance(o);
    }

	/**
	 * Direction: ��ѡ
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	dto = (TdCorpDto) o;
          return super.singleSelect(o);
    }

	/**
	 * Direction: ɾ��
	 * ename: delete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if(!tsuser.getSusertype().equals("2")){
    		MessageDialog.openMessageDialog(null,"�ù�����ҵ������ά��,��ǰ�û�����ִ�д˲���");
    		return null;
    	}
    	if (dto.getSbookorgcode()== null) {
			MessageDialog.openMessageDialog(null,StateConstant.DELETESELECT);
			return "";
		}
    	try {
    		tdCorpService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		init();
		editor.fireModelChanged();
		return this.searchRs(o);
    }

	/**
	 * Direction: ���޸Ľ���
	 * ename: goModify
	 * ���÷���: 
	 * viewers: �޸Ľ���
	 * messages: 
	 */
    public String goModify(Object o){
    	if(!tsuser.getSusertype().equals("2")){
    		MessageDialog.openMessageDialog(null,"�ù�����ҵ������ά��,��ǰ�û�����ִ�д˲���");
    		return null;
    	}
    	if (dto.getSbookorgcode() == null||dto.getScorpcode()==null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
    	
          return super.goModify(o);
    }

	/**
	 * Direction: �޸ı���
	 * ename: modifySave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String modifySave(Object o){
    	
    	if(datacheck(dto,"modify")){
    		return null;
    	}
    	try {
    		tdCorpService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TdCorpDto();
		editor.fireModelChanged();
		return super.backMaintenance(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {

    	//dto.setSbookorgcode(loginfo.getSorgcode());
	
		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(searchDto,
					pageRequest, "1=1");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}
    
    private boolean datacheck(TdCorpDto dto,String flag){
//    	if(null==dto.getSbookorgcode()||dto.getSbookorgcode().trim().length()==0){
//    		MessageDialog.openMessageDialog(null, "����������벻��Ϊ�ղ���Ϊ�գ�");
//			return true;
//    	}else if(null==dto.getScorpcode()||dto.getScorpcode().trim().length()==0){
//    		MessageDialog.openMessageDialog(null, "Ԥ�㵥λ���벻��Ϊ�գ�");
//    		return true;
//    	}else if(null==dto.getCtrimflag()||dto.getCtrimflag().trim().length()==0){
//    		MessageDialog.openMessageDialog(null, "�����ڱ�־����Ϊ�գ�");
//    		return true;
//    	}else if(null==dto.getStrecode()||dto.getStrecode().trim().length()==0){
//    		MessageDialog.openMessageDialog(null, "������벻��Ϊ��!");
//    		return true;
//    	}
		
    	if (flag.equals("add")) {
    		TdCorpDto tempdto=new TdCorpDto();
	    	tempdto.setScorpcode(dto.getScorpcode());
	    	tempdto.setCtrimflag(dto.getCtrimflag());
	    	tempdto.setStrecode(dto.getStrecode());
	    	List list=null;
    	try{
    		list = commonDataAccessService.findRsByDto(tempdto);
    	}catch(ITFEBizException e){
    		MessageDialog.openErrorDialog(null, e);
			return true;
    	}
    	if(null !=list&&list.size()>0){
    		MessageDialog.openMessageDialog(null, "�������+���˴����ظ�!");
    		return true;
    	}else{
    		return false;
    	}
    	}
		return false;
    }

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public String getSbookorgcode() {
		return sbookorgcode;
	}

	public void setSbookorgcode(String sbookorgcode) {
		this.sbookorgcode = sbookorgcode;
	}

	public TdCorpDto getSearchDto() {
		return searchDto;
	}

	public void setSearchDto(TdCorpDto searchDto) {
		this.searchDto = searchDto;
	}
	
	

}
package com.cfcc.itfe.client.para.tdtaxorgparam;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TdTaxorgParamDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author ZZD
 * @time   13-03-01 10:54:40
 * ��ϵͳ: Para
 * ģ��:TdTaxorgParam
 * ���:TdTaxorgParam
 */
public class TdTaxorgParamBean extends AbstractTdTaxorgParamBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TdTaxorgParamBean.class);
    private ITFELoginInfo loginfo = null;
    private String staxorgcode=null;
    public TdTaxorgParamBean() {
      super();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      dto = new TdTaxorgParamDto();
      finddto = new TdTaxorgParamDto();
      finddto.setSbookorgcode(loginfo.getSorgcode());
      pagingcontext = new PagingContext(this);
                  
    }
    
	/**
	 * Direction: ��ת¼�����
	 * ename: goInput
	 * ���÷���: 
	 * viewers: ¼�����
	 * messages: 
	 */
    public String goInput(Object o){
    	dto = new TdTaxorgParamDto();
    	dto.setSbookorgcode(loginfo.getSorgcode());
		dto.setTssysupdate(new Timestamp(System.currentTimeMillis()));
          return super.goInput(o);
    }

	/**
	 * Direction: ¼�뱣��
	 * ename: inputSave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String inputSave(Object o){
    	try {
    		
    		tdTaxorgParamService.addInfo(dto);
    		
   		}catch(ITFEBizException e){
   			log.error(e);
   			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e);
   			return "";
   		}catch (Throwable e) {
   			log.error(e);
   			MessageDialog.openErrorDialog(null, e);   			
   			return super.inputSave(o);
   		}
   		MessageDialog.openMessageDialog(null,StateConstant.INPUTSAVE);
   		dto = new TdTaxorgParamDto();
   		dto.setSbookorgcode(loginfo.getSorgcode());
		dto.setTssysupdate(new Timestamp(System.currentTimeMillis()));
   		editor.fireModelChanged();
   		return backMaintenance(o);
    }

	/**
	 * Direction: ���ص�ά������
	 * ename: backMaintenance
	 * ���÷���: 
	 * viewers: ά������
	 * messages: 
	 */
    public String backMaintenance(Object o){
    	dto = new TdTaxorgParamDto();
    	searchRs(null);
        return super.backMaintenance(o);
    }

	/**
	 * Direction: ��ѡ
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	dto = (TdTaxorgParamDto) o;
          return super.singleSelect(o);
    }

	/**
	 * Direction: ɾ��
	 * ename: delete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if (dto.getIseqno()== null) {
			MessageDialog.openMessageDialog(null,StateConstant.DELETESELECT);
			return "";
		}
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��ɾ��ѡ�еļ�¼��")) {
			return "";
		}
    	try {
    		tdTaxorgParamService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TdTaxorgParamDto();
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
    	if (dto.getSbookorgcode() == null||dto.getIseqno()==null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
    	staxorgcode	= dto.getStaxorgcode();
        return super.goModify(o);
    }

	/**
	 * Direction: �޸ı���
	 * ename: modifySave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String modifySave(Object o){
    	try {
    		TdTaxorgParamDto finddto=new TdTaxorgParamDto();
    		finddto.setSbookorgcode(dto.getSbookorgcode());
    		finddto.setStaxorgcode(dto.getStaxorgcode());
			List<TdTaxorgParamDto> list=commonDataAccessService.findRsByDto(finddto);
			if(list!=null&&list.size()>0&&!(list.get(0).getStaxorgcode().equals(staxorgcode))){				
				MessageDialog.openMessageDialog(null, "�����޸����ջ��ش���Ϊ��"+dto.getStaxorgcode()+"��\n���ջ��ش��룺"+dto.getStaxorgcode()+"�Ѵ��ڣ�");			
			    return "";
			}		
    		dto.setTssysupdate(new Timestamp(System.currentTimeMillis()));
    		tdTaxorgParamService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TdTaxorgParamDto();
		editor.fireModelChanged();
		return super.backMaintenance(o);
    }

	/**
	 * Direction: ��ѯ
	 * ename: searchRs
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String searchRs(Object o){
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		if(pageResponse.getData().size()<=0){
			MessageDialog.openMessageDialog(null,"û�в�ѯ����������������");
//			return this.rebackSearch(o);
		}
		pagingcontext.setPage(pageResponse);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.searchRs(o);
    }

	/**
	 * Direction: ���ز�ѯ����
	 * ename: rebackSearch
	 * ���÷���: 
	 * viewers: ��ѯ����
	 * messages: 
	 */
    public String rebackSearch(Object o){
    	finddto = new TdTaxorgParamDto();
    	finddto.setSbookorgcode(loginfo.getSorgcode());
    	dto = new TdTaxorgParamDto();
    	searchRs(null);
          return super.rebackSearch(o);
    }
    
    
	public String expfile(Object o) {
		// ѡ�񱣴�·��
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
			return "";
		}
		String fileName = filePath+ File.separator+ loginfo.getSorgcode() + "_���ջ��ش���� .csv";
		try {
			List<TdTaxorgParamDto> result = commonDataAccessService.findRsByDto(finddto);
			if(null == result || result.size() == 0 ){
				MessageDialog.openMessageDialog(null, "û����Ҫ���������ݣ�");
				return null;
			}
			export(result, fileName);
			MessageDialog.openMessageDialog(null, "�����ɹ���");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.expfile(o);
	}
	private void export(List<TdTaxorgParamDto> resultlist,String filepath ) throws FileOperateException{
		String separator = ",";
		StringBuffer result = new StringBuffer();
		result.append("�����������_CHARACTER_ NOT NULL,���ջ��ش���_VARCHAR_NOT NULL,���ջ�������_VARCHAR_NOT NULL,���ջ�������_CHARACTER_NOT NULL,���ջ��ص绰_VARCHAR,���ջ��ؼ���_VARCHAR,ϵͳ����ʱ��\n");
		for(TdTaxorgParamDto tmp : resultlist){
			result.append(tmp.getSbookorgcode() + separator);	//�����������
			result.append(tmp.getStaxorgcode() + separator);	//���ջ��ش���
			result.append(tmp.getStaxorgname() + separator);	//���ջ�������
			result.append(tmp.getCtaxorgprop() + separator);	//���ջ�������
			result.append((tmp.getStaxorgphone()==null?"":tmp.getStaxorgphone())+ separator );	//���ջ��ص绰
			result.append((tmp.getStaxorgsht()==null?"":tmp.getStaxorgsht()) + separator);	//���ջ��ؼ���
			result.append(tmp.getTssysupdate() );	//ϵͳ����ʱ��
			result.append("\n");
		}
		FileUtil.getInstance().writeFile(filepath, result.toString());
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	try{
    		return commonDataAccessService.findRsByDtoWithWherePaging(finddto,
					pageRequest, "1=1");
    	} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
    	return super.retrieve(pageRequest);
	}

}
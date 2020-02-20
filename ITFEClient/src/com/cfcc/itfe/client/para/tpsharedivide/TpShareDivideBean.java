package com.cfcc.itfe.client.para.tpsharedivide;

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
import com.cfcc.itfe.persistence.dto.TpShareDivideDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author ZZD
 * @time   13-03-04 11:19:36
 * ��ϵͳ: Para
 * ģ��:TpShareDivide
 * ���:TpShareDivide
 */
public class TpShareDivideBean extends AbstractTpShareDivideBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TpShareDivideBean.class);
    private ITFELoginInfo loginfo;
    public TpShareDivideBean() {
      super();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      dto = new TpShareDivideDto();
      finddto = new TpShareDivideDto();
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
    	dto = new TpShareDivideDto();
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
    		dto.setTssysupdate(new Timestamp(System.currentTimeMillis()));
    		tpShareDivideService.addInfo(dto);
    		
   		} catch (Throwable e) {
   			log.error(e);
   			MessageDialog.openErrorDialog(null, e);
   			
   			return super.inputSave(o);
   		}
   		MessageDialog.openMessageDialog(null,StateConstant.INPUTSAVE);
   		dto = new TpShareDivideDto();
   		editor.fireModelChanged();
          return super.inputSave(o);
    }

	/**
	 * Direction: ���ص�ά������
	 * ename: backMaintenance
	 * ���÷���: 
	 * viewers: ά������
	 * messages: 
	 */
    public String backMaintenance(Object o){
    	dto = new TpShareDivideDto();
          return super.backMaintenance(o);
    }

	/**
	 * Direction: ��ѡ
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	dto = (TpShareDivideDto)o;
          return super.singleSelect(o);
    }

	/**
	 * Direction: ɾ��
	 * ename: delete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if (dto.getIparamseqno()== null) {
			MessageDialog.openMessageDialog(null,StateConstant.DELETESELECT);
			return "";
		}
    	try {
    		tpShareDivideService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TpShareDivideDto();
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
    	if (dto.getSbookorgcode() == null||dto.getIparamseqno()==null) {
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
    	try {
    		dto.setTssysupdate(new Timestamp(System.currentTimeMillis()));
    		tpShareDivideService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TpShareDivideDto();
		editor.fireModelChanged();
		return super.backMaintenance(o);
    }

	/**
	 * Direction: ��ѯ
	 * ename: searchRs
	 * ���÷���: 
	 * viewers: ά������
	 * messages: 
	 */
    public String searchRs(Object o){
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
//		if(pageResponse.getData().size()<=0){
//			MessageDialog.openMessageDialog(null,"û�в�ѯ����������������");
//			return this.rebackSearch(o);
//		}
		pagingcontext.setPage(pageResponse);
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
    	dto = new TpShareDivideDto();
    	finddto = new TpShareDivideDto();
    	finddto.setSbookorgcode(loginfo.getSorgcode());
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
		String fileName = filePath+ File.separator+ loginfo.getSorgcode() + "_����ֳɲ���ά�� .csv";
		try {
			List<TpShareDivideDto> result = commonDataAccessService.findRsByDto(finddto);
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
	
	private void export(List<TpShareDivideDto> resultlist,String filepath ) throws FileOperateException{
		String separator = ",";
		StringBuffer result = new StringBuffer();
		result.append("�����������_CHARACTER_ NOT NULL,Դ�����������_CHARACTER_NOT NULL,���׹����������_CHARACTER_NOT NULL," +
				"�տ�������_CHARACTER_NOT NULL,Դ���ջ��ش���_VARCHAR,ԴԤ���Ŀ����_VARCHAR_NOT NULL,ԴԤ������_CHARACTER_NOT NULL," +
				"Դ������־_VARCHAR,�ֳɺ�����������_CHARACTER_NOT NULL,�ֳɺ����ջ���_VARCHAR_NOT NULL," +
				"�ֳɺ󼶴�_CHARACTER_NOT NULL,�ֳɺ�Ԥ���Ŀ_VARCHAR_NOT NULL,�ֳɺ�Ԥ������_CHARACTER_NOT NULL," +
				"�ֳɺ�����־_VARCHAR,�μӷֳɱ���_DECIMAL_NOT NULL,Ͻ����ϵ_CHARACTER_NOT NULL\n");
		for(TpShareDivideDto tmp : resultlist){
			result.append(tmp.getSbookorgcode() + separator);	//�����������
			result.append(tmp.getSroottrecode() + separator);	//Դ�����������
			result.append(tmp.getStratrecode() + separator);	//���׹����������
			result.append(tmp.getSpayeetrecode() + separator);	//�տ�������
			result.append(tmp.getSroottaxorgcode() + separator);	//Դ���ջ��ش���
			result.append(tmp.getSrootbdgsbtcode() + separator);	//ԴԤ���Ŀ����
			result.append(tmp.getCrootbdgkind() + separator);	//ԴԤ������
			result.append(tmp.getSrootastflag() + separator);	//Դ������־
			result.append(tmp.getSafttrecode() + separator);	//�ֳɺ�����������
			result.append(tmp.getSafttaxorgcode() + separator);	//�ֳɺ����ջ���
			result.append(tmp.getCaftbdglevel() + separator);	//�ֳɺ󼶴�
			result.append(tmp.getSaftbdgsbtcode() + separator);	//�ֳɺ�Ԥ���Ŀ
			result.append(tmp.getCaftbdgtype() + separator);	//�ֳɺ�Ԥ������
			result.append(tmp.getSaftastflag() + separator);	//�ֳɺ�����־
			result.append(tmp.getFjoindividerate() + separator);	//�μӷֳɱ���
			result.append(tmp.getCgovernralation() );	//Ͻ����ϵ
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
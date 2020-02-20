package com.cfcc.itfe.client.para.tsbankcode;

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
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.TsBankcodeDto;

/**
 * codecomment: 
 * @author lushaoqing
 * @time   10-09-26 10:35:24
 * ��ϵͳ: Para
 * ģ��:TsBankcode
 * ���:TsBankcode
 */
public class TsBankcodeBean extends AbstractTsBankcodeBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TsBankcodeBean.class);
    TsBankcodeDto findDto = new TsBankcodeDto();
    public TsBankcodeBean() {
      super();
      dto = new TsBankcodeDto();
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
    	dto = new TsBankcodeDto();
    	return super.goInput(o);
    }

	/**
	 * Direction: ¼�뱣��
	 * ename: inputSave
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String inputSave(Object o){
		String verifyRs = verifyPorp(dto);
		if (null != verifyRs) {
			MessageDialog.openMessageDialog(null, verifyRs);
			return null;
		}

		try {
			tsBankcodeService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsBankcodeDto();
        return super.backMaintenance(o);
    }

	/**
	 * Direction: ���ص�ά������
	 * ename: backMaintenance
	 * ���÷���: 
	 * viewers: ά������
	 * messages: 
	 */
    public String backMaintenance(Object o){
    	dto = new TsBankcodeDto();
        return super.backMaintenance(o);
    }

	/**
	 * Direction: ��ѡ
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
		dto = (TsBankcodeDto) o;
        return super.singleSelect(o);
    }

	/**
	 * Direction: ɾ��
	 * ename: delete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
		if (null == dto || null == dto.getSbnkcode() || null == dto.getSacctstatus()) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return null;
		}

		boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
				null, "ɾ������ȷ��!", "�Ƿ�ȷ��Ҫɾ��������¼��");
		if (flag) {
			try {
				tsBankcodeService.delInfo(dto);
			} catch (Throwable e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
				return super.delete(o);
			}
			MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
			dto = (TsBankcodeDto)findDto.clone();
		}
         return this.goSearchResult(o);
    }

	/**
	 * Direction: ���޸Ľ���
	 * ename: goModify
	 * ���÷���: 
	 * viewers: �޸Ľ���
	 * messages: 
	 */
    public String goModify(Object o){
		if (null == dto || null == dto.getSbnkcode() || null == dto.getSacctstatus()) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return null;
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
		String verifyRs = verifyPorp(dto);
		if (null != verifyRs) {
			MessageDialog.openMessageDialog(null, verifyRs);
			return null;
		}
		try {
			tsBankcodeService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = (TsBankcodeDto)findDto.clone();
		return this.goSearchResult(o);
    }

	/**
	 * Direction: ����ѯ�������
	 * ename: goSearchResult
	 * ���÷���: 
	 * viewers: ά������
	 * messages: 
	 */
    public String goSearchResult(Object o){
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);

		if (pageResponse.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼!");
			return super.goSearchResult(o);
		}

		editor.fireModelChanged();
        return super.goSearchResult(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
		if (null == dto) {
			dto = new TsBankcodeDto();
		}
		try {
			if(null != dto && ((dto.getSbnkname() != null && dto.getSbnkname().trim() != "")
					||(dto.getSbnkcode() != null && dto.getSbnkcode().trim() != "")
					||(dto.getSacctstatus() != null && dto.getSacctstatus().trim() != "")))
			{
				findDto = (TsBankcodeDto)dto.clone();
			}
			return tsBankcodeService.findBanknoByPage(dto, pageRequest);
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	/**
	 * У���ֶ�����
	 * 
	 * @param idto
	 * @return
	 */
	public String verifyPorp(TsBankcodeDto idto) {
		if (null == idto) {
			return "Ҫ�����ļ�¼Ϊ�գ���ȷ�ϣ�";
		}

		if (null == idto.getSbnkcode() || "".equals(idto.getSbnkcode().trim())
				|| idto.getSbnkcode().trim().length() != 12) {
			return "�����˺Ŵ��������д���ұ���Ϊ12λ��";
		}

		if (null == idto.getSacctstatus() || "".equals(idto.getSacctstatus())) {
			return "�����˺�״̬Ϊ�գ�";
		}
		
		if (null == idto.getSbnkname() || "".equals(idto.getSbnkname())) {
			return "�����˺�����Ϊ�գ�";
		}

		return null;
	}
}
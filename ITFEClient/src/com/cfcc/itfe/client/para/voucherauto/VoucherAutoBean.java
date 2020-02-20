package com.cfcc.itfe.client.para.voucherauto;

import java.util.List;

import org.apache.commons.logging.*;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;

/**
 * codecomment: 
 * @author db2admin
 * @time   11-08-01 09:16:05
 * ��ϵͳ: Para
 * ģ��:voucherAuto
 * ���:VoucherAuto
 */
public class VoucherAutoBean extends AbstractVoucherAutoBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(VoucherAutoBean.class);
	private ITFELoginInfo loginfo = null;
    private TsVouchercommitautoDto searchdto;

	public VoucherAutoBean() {
      super();
      voucherAutodto = new TsVouchercommitautoDto();
      searchdto = new TsVouchercommitautoDto();
      pagingcontext = new PagingContext(null);
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		voucherAutodto.setSorgcode(loginfo.getSorgcode());
		pagingcontext = new PagingContext(this);
		init();
    }
	private void init() {
		voucherAutodto.setSorgcode(this.getLoginfo().getSorgcode());
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		List<TsVouchercommitautoDto> list = pageResponse.getData();
		if(list!=null&&list.size()>0)
		{
			for(TsVouchercommitautoDto temp:list)
			{
				if(temp.getSreturbacknauto()==null)
					temp.setSreturbacknauto("");
			}
		}
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
    	voucherAutodto = new TsVouchercommitautoDto();
    	voucherAutodto.setSorgcode(loginfo.getSorgcode());
        return super.goInput(o);
    }
    /**
	 * У���ֶ�����
	 * 
	 * @param idto
	 * @return
	 */
	public String verifyPorp(TsVouchercommitautoDto idto) {
		if (null == idto) {
			return "Ҫ�����ļ�¼Ϊ�գ���ȷ�ϣ�";
		}

		if (null == idto.getStrecode() || "".equals(idto.getStrecode().trim())
				|| idto.getStrecode().trim().length() != 10) {
			return "������������д���ұ���Ϊ10λ��";
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
    	String verifyRs = verifyPorp(voucherAutodto);
		if (null != verifyRs) {
			MessageDialog.openMessageDialog(null, verifyRs);
			return null;
		}
		try {
			if(voucherAutodto.getScommitauto()==null)
				voucherAutodto.setScommitauto("");
			if(voucherAutodto.getSstampauto()==null)
				voucherAutodto.setSstampauto("");
			voucherAutoService.addInfo(voucherAutodto);
			voucherAutodto = new TsVouchercommitautoDto();
			init();
			MessageDialog.openMessageDialog(null,StateConstant.INPUTSAVE);
			this.editor.fireModelChanged();
		
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
			return super.inputSave(o);
		}
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
    	voucherAutodto = new TsVouchercommitautoDto();
		init();
		editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
          return super.backMaintenance(o);
    }

	/**
	 * Direction: ��ѡ
	 * ename: singleSelect
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	voucherAutodto = (TsVouchercommitautoDto) o;
          return super.singleSelect(o);
    }

	/**
	 * Direction: ɾ��
	 * ename: delete
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if (null == voucherAutodto || null == voucherAutodto.getStrecode()) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return null;
		}

		boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
				null, "ɾ������ȷ��!", "�Ƿ�ȷ��Ҫɾ��������¼��");
		if (flag) {
			try {
				voucherAutoService.delInfo(voucherAutodto);
			} catch (Throwable e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
				return super.delete(o);
			}
			MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
			voucherAutodto = new TsVouchercommitautoDto();
		}else{
			voucherAutodto = new TsVouchercommitautoDto();
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
    	if (null == voucherAutodto || null == voucherAutodto.getSvtcode()) {
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
    	String verifyRs = verifyPorp(voucherAutodto);
		if (null != verifyRs) {
			MessageDialog.openMessageDialog(null, verifyRs);
			return null;
		}
		try {
			if(voucherAutodto.getScommitauto()==null)
				voucherAutodto.setScommitauto("");
			if(voucherAutodto.getSstampauto()==null)
				voucherAutodto.setSstampauto("");
			voucherAutoService.modInfo(voucherAutodto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		voucherAutodto = new TsVouchercommitautoDto();
		editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
          return super.modifySave(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {
    	if (null == searchdto) {
    		searchdto = new TsVouchercommitautoDto();
		}

    	searchdto.setSorgcode(this.getLoginfo().getSorgcode());

		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(searchdto,
					arg0, "1=1");
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
}
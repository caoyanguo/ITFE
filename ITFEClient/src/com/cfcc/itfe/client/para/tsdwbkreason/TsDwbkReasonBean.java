package com.cfcc.itfe.client.para.tsdwbkreason;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsDwbkReasonDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author db2itfe
 * @time 13-09-07 14:09:45 ��ϵͳ: Para ģ��:TsDwbkReason ���:TsDwbkReason
 */
public class TsDwbkReasonBean extends AbstractTsDwbkReasonBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsDwbkReasonBean.class);
	private ITFELoginInfo loginfo;
	private PageRequest request = new PageRequest();

	public TsDwbkReasonBean() {
		super();
		searchdto = new TsDwbkReasonDto();
		pagingcontext = new PagingContext(this);
		savedto = new TsDwbkReasonDto();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
	}

	/**
	 * Direction: ��ѯ ename: search ���÷���: viewers: �˸�ԭ�������ѯ��� messages:
	 */
	public String search(Object o) {
		retrieve(request);
		return super.search(o);
	}

	/**
	 * Direction: ���� ename: newInput ���÷���: viewers: �˸�ԭ�����¼����� messages:
	 */
	public String newInput(Object o) {
		savedto = new TsDwbkReasonDto();
		savedto.setSorgcode(loginfo.getSorgcode());
		return super.newInput(o);
	}

	/**
	 * Direction: �޸���ת ename: updateDri ���÷���: viewers: �˸�ԭ������޸Ľ��� messages:
	 */
	public String updateDri(Object o) {
		if(savedto.getSdrawbackreacode() == null){
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		return super.updateDri(o);
	}

	/**
	 * Direction: ɾ�� ename: delete ���÷���: viewers: * messages:
	 */
	public String delete(Object o) {
		if(savedto.getSdrawbackreacode() == null){
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��ɾ��ѡ�еļ�¼��")) {
			savedto = new TsDwbkReasonDto();
			return "";
		}
		try {
			commonDataAccessService.delete(savedto);
			MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
			retrieve(request);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		savedto = new TsDwbkReasonDto();
		retrieve(request);
		return super.delete(o);
	}

	/**
	 * Direction: ���� ename: reback ���÷���: viewers: �˸�ԭ�������ѯ messages:
	 */
	public String reback(Object o) {
		savedto = new TsDwbkReasonDto();
		return super.reback(o);
	}

	/**
	 * Direction: ���� ename: save ���÷���: viewers: �˸�ԭ�������ѯ��� messages:
	 */
	public String save(Object o) {
		try {
			if(!isNumber(savedto.getSdrawbackreacode())){
				MessageDialog.openMessageDialog(null, "�˸�ԭ��������Ϊ������4λ�����֡�");
				return null;
			}
			TsDwbkReasonDto dto = new TsDwbkReasonDto();
			dto.setSorgcode(loginfo.getSorgcode());
			dto.setSdrawbackreacode(savedto.getSdrawbackreacode());
			List<TsDwbkReasonDto> list = commonDataAccessService.findRsByDto(dto);
			if(list!=null && list.size()>0){
				MessageDialog.openMessageDialog(null,"�˸�ԭ������ظ������������룡");
				return "";
			}else{
				commonDataAccessService.create(savedto);
				retrieve(request);
			}
			
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		savedto = new TsDwbkReasonDto();
		return super.save(o);
	}

	/**
	 * Direction: �޸� ename: update ���÷���: viewers: �˸�ԭ�������ѯ��� messages:
	 */
	public String update(Object o) {
		try {
			commonDataAccessService.updateData(savedto);
			retrieve(request);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		savedto = new TsDwbkReasonDto();
		return super.update(o);
	}

	/**
	 * Direction: ȡ�� ename: exit ���÷���: viewers: �˸�ԭ�������ѯ��� messages:
	 */
	public String exit(Object o) {
		retrieve(request);
		savedto = new TsDwbkReasonDto();
		return super.exit(o);
	}

	/**
	 * Direction: ˫���޸� ename: doubleclickToUpdate ���÷���: viewers: �˸�ԭ������޸Ľ���
	 * messages:
	 */
	public String doubleclickToUpdate(Object o) {
		savedto = (TsDwbkReasonDto) o;
		return super.doubleclickToUpdate(o);
	}
	
	/**
	 * Direction: ����ѡ������
	 * ename: singleclickdate
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String singleclickdate(Object o){
        savedto = (TsDwbkReasonDto) o;
        return "";
    }
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest arg0) {
		try {
			searchdto.setSorgcode(loginfo.getSorgcode());
			List<TsDwbkReasonDto> list = commonDataAccessService
					.findRsByDto(searchdto);
			pagingcontext.getPage().getData().clear();
			pagingcontext.getPage().setData(list);
			editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(arg0);
	}
	
	/**
	 * �ж��ַ����Ƿ����������
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str)
    {
        java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("[0-9]{1,4}");
        java.util.regex.Matcher match=pattern.matcher(str);
        if(match.matches()==false)
        {
           return false;
        }
        else
        {
           return true;
        }
    }

}
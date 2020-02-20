package com.cfcc.itfe.client.para.tsinfoconnorgacc;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author caoyg
 * @time 09-10-20 08:42:02 ��ϵͳ: Para ģ��:TsInfoconnorgacc ���:TsInfoconnorgacc
 */
public class TsInfoconnorgaccBean extends AbstractTsInfoconnorgaccBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(TsInfoconnorgaccBean.class);
	private ITFELoginInfo loginfo;

	public TsInfoconnorgaccBean() {
		super();
		dto = new TsInfoconnorgaccDto();
		pagingcontext = new PagingContext(this);
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		init();
	}

	/**
	 * Direction: ��ת¼����� ename: goInput ���÷���: viewers: ¼����� messages:
	 */
	public String goInput(Object o) {
		dto = new TsInfoconnorgaccDto();
		dto.setSorgcode(loginfo.getSorgcode());
		dto.setSbiztype("1");//Ԥ����
		return super.goInput(o);
	}

	/**
	 * Direction: ¼�뱣�� ename: inputSave ���÷���: viewers: * messages:
	 */
	public String inputSave(Object o) {
		
		if (!datacheck("add")) {
			return null;
		}
		try {
			tsInfoconnorgaccService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, e.getMessage());
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, "�������˻���"+dto.getSpayeraccount()+" ����ɹ���");
		dto = new TsInfoconnorgaccDto();
		init();		
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ���ص�ά������ ename: backMaintenance ���÷���: viewers: ά������ messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsInfoconnorgaccDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsInfoconnorgaccDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: ɾ�� ename: delete ���÷���: viewers: * messages:
	 */
	public String delete(Object o) {
		if (null == dto || dto.getSpayeraccount()== null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(
				this.editor.getCurrentComposite().getShell(), StateConstant.TIPS,
				StateConstant.DELETECONFIRM)) {
			dto = new TsInfoconnorgaccDto();
			return null;
		} 
		try {
			tsInfoconnorgaccService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsInfoconnorgaccDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ���޸Ľ��� ename: goModify ���÷���: viewers: �޸Ľ��� messages:
	 */
	public String goModify(Object o) {
		if (dto == null || dto.getSpayeraccount() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		dto.setSorgcode(loginfo.getSorgcode());
		return super.goModify(o);
	}

	/**
	 * Direction: �޸ı��� ename: modifySave ���÷���: viewers: * messages:
	 */
	public String modifySave(Object o) {
		if (!datacheck("modify")) {
			return null;
		}
		try {
			tsInfoconnorgaccService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsInfoconnorgaccDto();
		return super.backMaintenance(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest pageRequest) {
		TsInfoconnorgaccDto searchdto = new TsInfoconnorgaccDto();
		searchdto.setSorgcode(loginfo.getSorgcode());
		try {
			PageResponse response = commonDataAccessService.findRsByDtoWithWherePaging(searchdto,pageRequest, "1=1");
			List<TsInfoconnorgaccDto> list = response.getData();
			boolean repeatQuery=false;
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				TsInfoconnorgaccDto tsInfoconnorgaccDto = (TsInfoconnorgaccDto) iterator.next();
				if (StringUtils.isBlank(tsInfoconnorgaccDto.getSbiztype())) {
					repeatQuery=true;
					tsInfoconnorgaccDto.setSbiztype(com.cfcc.itfe.constant.MsgConstant.BDG_KIND_IN);
					tsInfoconnorgaccDto.setImodicount(1);
					commonDataAccessService.updateData(tsInfoconnorgaccDto);
				}
			}
			
			if (repeatQuery) {
				return commonDataAccessService.findRsByDtoWithWherePaging(dto,pageRequest, "1=1");
			}else {
				return response;
			}
			
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);

	}

	private void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	// ����У��
	private boolean datacheck(String flag) {
		if (null == dto.getSpayeraccount()
				|| "".equals(dto.getSpayeraccount().trim())) {
			MessageDialog.openMessageDialog(null, "�������ʻ�����Ϊ�գ�");
			return false;
		}
		if (null == dto.getSpayername()
				|| "".equals(dto.getSpayername().trim())) {
			MessageDialog.openMessageDialog(null, "���������Ʋ���Ϊ�գ�");
			return false;
		}

		if (null ==dto.getSbillorgcode() || "".equals(dto.getSbillorgcode())) {
			MessageDialog.openMessageDialog(null, "������Ʊ��λ����Ϊ�գ�");
			return false;
		}


		// ¼������
		if (flag.equals("add")) {
			TsInfoconnorgaccDto tempdto = new TsInfoconnorgaccDto();
			tempdto.setSorgcode(loginfo.getSorgcode());
			tempdto.setSpayeraccount(dto.getSpayeraccount());
			try {
				List list = commonDataAccessService.findRsByDto(tempdto);
				if (null != list && list.size() > 0) {
					MessageDialog.openMessageDialog(null,
							"�������������+�������ʻ� �������ظ�!");
					return false;
				}
			} catch (Throwable e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
				return false;
			}
		}
		return true;

	}
}
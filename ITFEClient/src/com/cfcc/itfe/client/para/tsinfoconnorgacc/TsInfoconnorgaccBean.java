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
 * @time 09-10-20 08:42:02 子系统: Para 模块:TsInfoconnorgacc 组件:TsInfoconnorgacc
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
	 * Direction: 跳转录入界面 ename: goInput 引用方法: viewers: 录入界面 messages:
	 */
	public String goInput(Object o) {
		dto = new TsInfoconnorgaccDto();
		dto.setSorgcode(loginfo.getSorgcode());
		dto.setSbiztype("1");//预算内
		return super.goInput(o);
	}

	/**
	 * Direction: 录入保存 ename: inputSave 引用方法: viewers: * messages:
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
		MessageDialog.openMessageDialog(null, "付款人账户："+dto.getSpayeraccount()+" 保存成功！");
		dto = new TsInfoconnorgaccDto();
		init();		
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 返回到维护界面 ename: backMaintenance 引用方法: viewers: 维护界面 messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsInfoconnorgaccDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsInfoconnorgaccDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: 删除 ename: delete 引用方法: viewers: * messages:
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
	 * Direction: 到修改界面 ename: goModify 引用方法: viewers: 修改界面 messages:
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
	 * Direction: 修改保存 ename: modifySave 引用方法: viewers: * messages:
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

	// 数据校验
	private boolean datacheck(String flag) {
		if (null == dto.getSpayeraccount()
				|| "".equals(dto.getSpayeraccount().trim())) {
			MessageDialog.openMessageDialog(null, "付款人帐户不能为空！");
			return false;
		}
		if (null == dto.getSpayername()
				|| "".equals(dto.getSpayername().trim())) {
			MessageDialog.openMessageDialog(null, "付款人名称不能为空！");
			return false;
		}

		if (null ==dto.getSbillorgcode() || "".equals(dto.getSbillorgcode())) {
			MessageDialog.openMessageDialog(null, "所属出票单位不能为空！");
			return false;
		}


		// 录入判重
		if (flag.equals("add")) {
			TsInfoconnorgaccDto tempdto = new TsInfoconnorgaccDto();
			tempdto.setSorgcode(loginfo.getSorgcode());
			tempdto.setSpayeraccount(dto.getSpayeraccount());
			try {
				List list = commonDataAccessService.findRsByDto(tempdto);
				if (null != list && list.size() > 0) {
					MessageDialog.openMessageDialog(null,
							"【核算主体代码+付款人帐户 】不能重复!");
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
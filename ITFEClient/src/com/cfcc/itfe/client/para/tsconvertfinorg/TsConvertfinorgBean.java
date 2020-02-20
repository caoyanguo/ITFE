package com.cfcc.itfe.client.para.tsconvertfinorg;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.AreaSpecUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author db2admin
 * @time 09-11-09 15:22:03 子系统: Para 模块:TsConvertfinorg 组件:TsConvertfinorg
 */
public class TsConvertfinorgBean extends AbstractTsConvertfinorgBean implements IPageDataProvider {

	private static Log log = LogFactory.getLog(TsConvertfinorgBean.class);
	private List list2;
	private ITFELoginInfo loginfo;
	String area ;

	public TsConvertfinorgBean() {
		super();
		dto = new TsConvertfinorgDto();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
		dto.setSorgcode(loginfo.getSorgcode());
		pagingcontext = new PagingContext(this);
		area= loginfo.getArea();
		init();
	}

	/**
	 * Direction: 跳转录入界面 ename: goInput 引用方法: viewers: 录入界面 messages:
	 */
	public String goInput(Object o) {
		dto = new TsConvertfinorgDto();
		dto.setSorgcode(loginfo.getSorgcode());
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
			TsConvertfinorgDto tmpDto = new TsConvertfinorgDto();
			tmpDto.setSorgcode(dto.getSorgcode());
			tmpDto.setStrecode(dto.getStrecode());
			List list = commonDataAccessService.findRsByDto(tmpDto);
			if(!"".equals(dto.getSadmdivcode()) && dto.getSadmdivcode() != null){
				TsConvertfinorgDto tmpDto2 = new TsConvertfinorgDto();
				tmpDto2.setSadmdivcode(dto.getSadmdivcode());
				List list2 = commonDataAccessService.findRsByDto(tmpDto2);
				if(list2.size() > 0){
					MessageDialog.openMessageDialog(null, "区划代码：[" + dto.getSadmdivcode() + "]已经维护，请查证！");
					return null;
				}
			}
			if(null == list || list.size() == 0){
				tsConvertfinorgService.addInfo(dto);
			}else{
				MessageDialog.openMessageDialog(null, "财政机关代码：[" + dto.getStrecode() + "]已经维护，请查证！");
				return null;
			}
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsConvertfinorgDto();
		dto.setSorgcode(loginfo.getSorgcode());
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 返回到维护界面 ename: backMaintenance 引用方法: viewers: 维护界面 messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsConvertfinorgDto();
		dto.setSorgcode(loginfo.getSorgcode());
		init();
		return super.backMaintenance(o);

	}

	/**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsConvertfinorgDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: 删除 ename: delete 引用方法: viewers: * messages:
	 */
	public String delete(Object o) {
		if (null == dto || dto.getSfinorgcode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(
				this.editor.getCurrentComposite().getShell(), StateConstant.TIPS,
				StateConstant.DELETECONFIRM)) {
			dto = new TsConvertfinorgDto();
			return null;
		}
		try {
			
			tsConvertfinorgService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsConvertfinorgDto();
		dto.setSorgcode(loginfo.getSorgcode());
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 到修改界面 ename: goModify 引用方法: viewers: 修改界面 messages:
	 */
	public String goModify(Object o) {
		if (dto == null || dto.getSfinorgcode() == null) {
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
			tsConvertfinorgService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsConvertfinorgDto();
		dto.setSorgcode(loginfo.getSorgcode());
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
		dto = new TsConvertfinorgDto();
		dto.setSorgcode(loginfo.getSorgcode());
		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(dto, pageRequest, "1=1");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	// 初始化显示明细记录
	private void init() {
		TsTreasuryDto tredto = new TsTreasuryDto();
		tredto.setSorgcode(loginfo.getSorgcode());
		try {
			list2 = commonDataAccessService.findRsByDto(tredto);
		} catch (Throwable e) {
			log.error("取机构对应国库代码出错", e);
			return;
		}

		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	// 数据校验
	private boolean datacheck(String flag) {
		String regex ="^[0-9]*$";
		Matcher matcher;
		Pattern pattern;
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(dto.getStrecode());
		if (!matcher.matches()|| dto.getStrecode().trim().length() != 10 ) {
			MessageDialog.openMessageDialog(null, "国库代码必须为10位数字字符！");
			return false;
		}
		if (null == dto.getSfinorgcode() || "".equals(dto.getSfinorgcode().trim())) {
			MessageDialog.openMessageDialog(null, "财政机构代码不能为空！");
			return false;
		}
		if (null == dto.getSfinorgname() || "".equals(dto.getSfinorgname().trim())) {
			MessageDialog.openMessageDialog(null, "财政机构名称不能为空！");
			return false;
		}
//		if (null == dto.getStrename() || "".equals(dto.getStrename().trim())) {
//			MessageDialog.openMessageDialog(null, "国库名称不能为空！");
//			return false;
//		}
		/*if (null == dto.getSfinflag() || "".equals(dto.getSfinflag().trim())) {
			MessageDialog.openMessageDialog(null, "财政局标识不能为空！");
			return false;
		}*/
		// 录入判重
		if (flag.equals("add")) {
			TsConvertfinorgDto tempdto = new TsConvertfinorgDto();
			tempdto.setSorgcode(dto.getSorgcode());
			tempdto.setSfinorgcode(dto.getSfinorgcode());
			try {
				List list = commonDataAccessService.findRsByDto(tempdto);
				if (null != list && list.size() > 0) {
					MessageDialog.openMessageDialog(null, "核算主体代码+财政核算主体代码不能重复!");
					return false;
				}
			} catch (Throwable e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
			}
		}
		return true;

	}

	public List getList2() {
		return list2;
	}

	public void setList2(List list2) {
		this.list2 = list2;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

}
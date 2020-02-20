package com.cfcc.itfe.client.para.tsbudgetsubject;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author wangtuo
 * @time 10-04-08 12:58:20 子系统: Para 模块:TsBudgetsubject 组件:TsBudgetsubject
 */
public class TsBudgetsubjectBean extends AbstractTsBudgetsubjectBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsBudgetsubjectBean.class);
	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();
	private PagingContext pagingcontext = new PagingContext(this);

	/**
	 * TsBudgetsubjectDto
	 */
	private TsBudgetsubjectDto dto = null;
	// 查询dto
	private TsBudgetsubjectDto querydto = null;

	public TsBudgetsubjectBean() {
		super();
		querydto = new TsBudgetsubjectDto();
		dto = new TsBudgetsubjectDto();
	}

	/**
	 * Direction: 查询 ename: queryBudget 引用方法: viewers: 维护查询结果 messages:
	 */
	public String queryBudget(Object o) {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		return super.queryBudget(o);
	}

	/**
	 * Direction: 跳转录入界面 ename: goInput 引用方法: viewers: 录入界面 messages:
	 */
	public String goInput(Object o) {
		dto = new TsBudgetsubjectDto();
		dto.setSorgcode(loginfo.getSorgcode());
		return super.goInput(o);
	}

	/**
	 * Direction: 录入保存 ename: inputSave 引用方法: viewers: * messages:
	 */
	public String inputSave(Object o) {
		try {
//			dto.getSorgcode();
			dto.setSdrawbacktype("0");
			tsBudgetsubjectService.addInfo(dto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null,e.getMessage());
			return super.inputSave(o);
		}
		dto = new TsBudgetsubjectDto();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 返回到维护界面 ename: backMaintenance 引用方法: viewers: 维护界面 messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsBudgetsubjectDto();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsBudgetsubjectDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: 删除 ename: delete 引用方法: viewers: * messages:
	 */
	public String delete(Object o) {
		if (null == dto.getSsubjectcode() || "".equals(dto.getSsubjectcode())) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		// 提示用户确定删除
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否要删除选择的科目"
				+ dto.getSsubjectcode())) {
			return "";
		}
		try {
			tsBudgetsubjectService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		queryBudget(null);
		editor.fireModelChanged();
		return super.delete(o);
	}

	/**
	 * Direction: 到修改界面 ename: goModify 引用方法: viewers: 修改界面 messages:
	 */
	public String goModify(Object o) {
		if (null == dto.getSsubjectcode() || "".equals(dto.getSsubjectcode())) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		return super.goModify(o);
	}

	/**
	 * Direction: 修改保存 ename: modifySave 引用方法: viewers: * messages:
	 */
	public String modifySave(Object o) {
		try {
			tsBudgetsubjectService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsBudgetsubjectDto();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 返回 ename: goBack 引用方法: viewers: 信息查询 messages:
	 */
	public String goBack(Object o) {
		return super.goBack(o);
	}
	
	

	@Override
	public String expfile(Object o) {
		// 选择保存路径
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
			return "";
		}
		String fileName = filePath+ File.separator+ loginfo.getSorgcode() + "_预算科目代码 .csv";
		try {
			List<TsBudgetsubjectDto> list = commonDataAccessService.findRsByDto(querydto);
			if(null == list || list.size() == 0 ){
				MessageDialog.openMessageDialog(null, "没有需要导出的数据！");
				return null;
			}
			StringBuffer result = new StringBuffer();
			result.append("核算主体代码_CHARACTER_ NOT NULL,预算科目代码_VARCHAR_NOT NULL," +
					"预算科目名称_VARCHAR_NOT NULL,预算科目种类_CHARACTER_NOT NULL,预算科目分类代码_CHARACTER_NOT NULL," +
					"预算种类_CHARACTER_NOT NULL,预算科目类型代码_CHARACTER_NOT NULL,预算科目简码_VARCHAR,录入标志_CHARACTER_NOT NULL," +
					"预算科目属性_CHARACTER_NOT NULL,调拨标志_VARCHAR_NOT NULL,财政代码_VARCHAR,征收机关代码_VARCHAR," +
					"统一代码_VARCHAR,统计代码_VARCHAR_NOT NULL,对应中央科目代码_VARCHAR,对应省科目代码_VARCHAR," +
					"对应市科目代码_VARCHAR,对应县科目代码_VARCHAR\n");
			for(TsBudgetsubjectDto tmp : list ){
				result.append(tmp.getSorgcode() + ",");		//核算主体代码
				result.append(tmp.getSsubjectcode() + ",");		//科目代码
				result.append(tmp.getSsubjectname() + ",");		//预算科目名称
				result.append(tmp.getSsubjectclass() + ",");		//预算科目种类
				result.append(tmp.getSclassflag() + ",");		//预算科目分类代码
				result.append(tmp.getSbudgettype() + ",");		//预算种类
				result.append(tmp.getSsubjecttype() + ",");		//预算科目类型代码
				result.append(" ,");		//预算科目简码
				result.append(tmp.getSwriteflag() + ",");		//录入标志
				result.append(tmp.getSsubjectattr() + ",");		//预算科目属性
				result.append(tmp.getSmoveflag() + ",");		//调拨标志
				result.append("" + ",");		//财政代码
				result.append("" + ",");		//征收机关代码
				result.append("" + ",");		//统一代码
				result.append("" + ",");		//统计代码
				result.append("" + ",");		//对应中央科目代码
				result.append("" + ",");		//对应省科目代码
				result.append("" + ",");		//对应市科目代码
				result.append("" + "\n");		//对应县科目代码
			}
			FileUtil.getInstance().writeFile(fileName, result.toString());
			MessageDialog.openMessageDialog(null, "操作成功！");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.expfile(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest pageRequest) {
		try {
			String where = "";
			if(loginfo.getPublicparam().contains(",Budgetsubject=public,"))
			{
				where = " and (s_orgcode='"+(loginfo.getSorgcode().substring(0,2)+"0000000003'")+" or s_orgcode = '"+(loginfo.getSorgcode().substring(0,2)+"0000000002'")+")";
			}else
			{
				querydto.setSorgcode(loginfo.getSorgcode());
			}
			TsBudgetsubjectDto sdto = (TsBudgetsubjectDto)querydto.clone();
			String subjetcode = sdto.getSsubjectcode();
			if(subjetcode!=null&&!"".equals(subjetcode))
			{
				where = where + " and S_SUBJECTCODE like '"+subjetcode+"%'";
				sdto.setSsubjectcode(null);
			}
			return commonDataAccessService.findRsByDtoWithWherePaging(sdto,
					pageRequest, "1=1" +where);

		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		TsBudgetsubjectBean.log = log;
	}

	public PagingContext getPagingcontext() {
		return pagingcontext;
	}

	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
	}

	public TsBudgetsubjectDto getDto() {
		return dto;
	}

	public void setDto(TsBudgetsubjectDto dto) {
		this.dto = dto;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public TsBudgetsubjectDto getQuerydto() {
		return querydto;
	}

	public void setQuerydto(TsBudgetsubjectDto querydto) {
		this.querydto = querydto;
	}

}
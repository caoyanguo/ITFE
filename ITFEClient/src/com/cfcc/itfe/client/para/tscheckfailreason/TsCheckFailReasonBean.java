package com.cfcc.itfe.client.para.tscheckfailreason;

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
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.para.tscheckfailreason.ITsCheckFailReasonService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsCheckfailreasonDto;

/**
 * codecomment:
 * 
 * @author db2itfe
 * @time 13-09-04 10:33:40 子系统: Para 模块:TsCheckFailReason 组件:TsCheckFailReason
 */
public class TsCheckFailReasonBean extends AbstractTsCheckFailReasonBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(TsCheckFailReasonBean.class);
	private ITFELoginInfo loginfo= (ITFELoginInfo) ApplicationActionBarAdvisor
	.getDefault().getLoginInfo();
	private PageRequest request = new PageRequest();
	public TsCheckFailReasonBean() {
		super();
		dto = new TsCheckfailreasonDto();
		pagingcontext = new PagingContext(null);
		request = new PageRequest();
		retrieve(request);
	}

	/**
	 * Direction: 查询 ename: search 引用方法: viewers: 常用失败原因维护页 messages:
	 */
	public String search(Object o) {
		retrieve(request);
		return super.search(o);
	}

	/**
	 * Direction: 录入 ename: newInput 引用方法: viewers: 常用失败原因录入页 messages:
	 */
	public String newInput(Object o) {
		dto = new TsCheckfailreasonDto();
		dto.setSorgcode(loginfo.getSorgcode());
		return super.newInput(o);
	}

	/**
	 * Direction: 修改跳转 ename: updateDireck 引用方法: viewers: 常用失败原因修改页 messages:
	 */
	public String updateDireck(Object o) {
		return super.updateDireck(o);
	}

	/**
	 * Direction: 保存 ename: save 引用方法: viewers: 常用失败原因维护页 messages:
	 */
	public String save(Object o) {
		try {
			TsCheckfailreasonDto checkdouble = new TsCheckfailreasonDto();
			checkdouble.setSorgcode(loginfo.getSorgcode());// 核算主体代码
			checkdouble.setScheckfailcode(dto.getScheckfailcode());
			List<TsCheckfailreasonDto> list = commonDataAccessService.findRsByDto(checkdouble);
			if(list!=null && list.size()>0){
				MessageDialog.openMessageDialog(null,"失败原因代码重复，请重新输入！");
				return "";
			}else{
				commonDataAccessService.create(dto);
			}
			MessageDialog.openMessageDialog(null,"保存成功！");
		} catch (ITFEBizException e) {
			log.error("保存失败原因信息错误！", e);
			MessageDialog.openErrorDialog(null, e);
		}
		retrieve(request);
		return super.save(o);
	}

	/**
	 * Direction: 修改 ename: update 引用方法: viewers: 常用失败原因维护页 messages:
	 */
	public String update(Object o) {
		try {
			commonDataAccessService.updateData(dto);
			MessageDialog.openMessageDialog(null,"常用失败原因修改成功！");
		} catch (ITFEBizException e) {
			log.error("修改失败原因信息错误！", e);
			MessageDialog.openErrorDialog(null, e);
		}
		retrieve(request);
		return super.update(o);
	}

	/**
	 * Direction: 取消 ename: exit 引用方法: viewers: 常用失败原因维护页 messages:
	 */
	public String exit(Object o) {
		retrieve(request);
		return super.exit(o);
	}

	/**
	 * Direction: 删除 ename: delete 引用方法: viewers: 常用失败原因维护页 messages:
	 */
	public String delete(Object o) {
		try {
			if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(
					this.editor.getCurrentComposite().getShell(), StateConstant.TIPS,
					StateConstant.DELETECONFIRM)) {
				return null;
			} 
			
			commonDataAccessService.delete(dto);
			MessageDialog.openMessageDialog(null,"常用失败原因删除成功！");
		} catch (ITFEBizException e) {
			log.error("删除失败原因信息错误！", e);
			MessageDialog.openErrorDialog(null, e);
		}
		retrieve(request);
		return super.delete(o);
	}

	 /**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsCheckfailreasonDto) o;
		return super.singleSelect(o);
	}
	/**
	 * Direction: 双击跳转修改 ename: doubleclickToUpdate 引用方法: viewers: 常用失败原因修改页
	 * messages:
	 */
	public String doubleclickToUpdate(Object o) {
		dto = (TsCheckfailreasonDto) o;
		return super.doubleclickToUpdate(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest arg0) {
		TsCheckfailreasonDto dto = new TsCheckfailreasonDto();
		dto.setSorgcode(loginfo.getSorgcode());
		try {
			PageResponse response = commonDataAccessService
					.findRsByDtoWithWherePaging(dto, request, " 1=1 ");
			pagingcontext.getPage().getData().clear();
			pagingcontext.getPage().setData(response.getData());
		} catch (ITFEBizException e) {
			log.error("查询失败原因信息错误！", e);
			MessageDialog.openErrorDialog(null, e);
		}catch(Exception e){
			e.printStackTrace();
		}
		return super.retrieve(arg0);
	}

}
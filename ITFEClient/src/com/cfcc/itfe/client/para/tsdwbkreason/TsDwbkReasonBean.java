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
 * @time 13-09-07 14:09:45 子系统: Para 模块:TsDwbkReason 组件:TsDwbkReason
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
	 * Direction: 查询 ename: search 引用方法: viewers: 退付原因参数查询结果 messages:
	 */
	public String search(Object o) {
		retrieve(request);
		return super.search(o);
	}

	/**
	 * Direction: 新增 ename: newInput 引用方法: viewers: 退付原因参数录入界面 messages:
	 */
	public String newInput(Object o) {
		savedto = new TsDwbkReasonDto();
		savedto.setSorgcode(loginfo.getSorgcode());
		return super.newInput(o);
	}

	/**
	 * Direction: 修改跳转 ename: updateDri 引用方法: viewers: 退付原因参数修改界面 messages:
	 */
	public String updateDri(Object o) {
		if(savedto.getSdrawbackreacode() == null){
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		return super.updateDri(o);
	}

	/**
	 * Direction: 删除 ename: delete 引用方法: viewers: * messages:
	 */
	public String delete(Object o) {
		if(savedto.getSdrawbackreacode() == null){
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定删除选中的记录吗？")) {
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
	 * Direction: 返回 ename: reback 引用方法: viewers: 退付原因参数查询 messages:
	 */
	public String reback(Object o) {
		savedto = new TsDwbkReasonDto();
		return super.reback(o);
	}

	/**
	 * Direction: 保存 ename: save 引用方法: viewers: 退付原因参数查询结果 messages:
	 */
	public String save(Object o) {
		try {
			if(!isNumber(savedto.getSdrawbackreacode())){
				MessageDialog.openMessageDialog(null, "退付原因代码必须为不大于4位的数字。");
				return null;
			}
			TsDwbkReasonDto dto = new TsDwbkReasonDto();
			dto.setSorgcode(loginfo.getSorgcode());
			dto.setSdrawbackreacode(savedto.getSdrawbackreacode());
			List<TsDwbkReasonDto> list = commonDataAccessService.findRsByDto(dto);
			if(list!=null && list.size()>0){
				MessageDialog.openMessageDialog(null,"退付原因代码重复，请重新输入！");
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
	 * Direction: 修改 ename: update 引用方法: viewers: 退付原因参数查询结果 messages:
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
	 * Direction: 取消 ename: exit 引用方法: viewers: 退付原因参数查询结果 messages:
	 */
	public String exit(Object o) {
		retrieve(request);
		savedto = new TsDwbkReasonDto();
		return super.exit(o);
	}

	/**
	 * Direction: 双击修改 ename: doubleclickToUpdate 引用方法: viewers: 退付原因参数修改界面
	 * messages:
	 */
	public String doubleclickToUpdate(Object o) {
		savedto = (TsDwbkReasonDto) o;
		return super.doubleclickToUpdate(o);
	}
	
	/**
	 * Direction: 单击选中数据
	 * ename: singleclickdate
	 * 引用方法: 
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
	 * 判断字符串是否是数字组成
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
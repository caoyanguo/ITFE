package com.cfcc.itfe.client.para.tsisnopaper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsIsnopaperDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author db2admin
 * @time   14-05-14 16:23:02
 * 子系统: Para
 * 模块:TsIsnopaper
 * 组件:TsIsnopaper
 */
public class TsIsnopaperBean extends AbstractTsIsnopaperBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TsIsnopaperBean.class);
    ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
	.getDefault().getLoginInfo();
    
    private TsIsnopaperDto dto = null;
    // 查询dto
	private TsIsnopaperDto querydto = null;
	
	private List<TdEnumvalueDto> biztypelist = new ArrayList<TdEnumvalueDto>();
    
    private PagingContext pagingcontext = new PagingContext(this);
    public TsIsnopaperBean() {
      super();
      dto = new TsIsnopaperDto();
      querydto = new TsIsnopaperDto();
      querydto.setSorgcode(loginfo.getSorgcode());
      querydto.setSisnopaper(StateConstant.MSG_NORELAY_TIPS);
      initbiztypelist();
    }
    
	private void initbiztypelist() {
		TdEnumvalueDto dto1 = new TdEnumvalueDto();
		dto1.setStypecode("直接支付额度");
		dto1.setSvalue("5108");
		biztypelist.add(dto1);
		
		TdEnumvalueDto dto2 = new TdEnumvalueDto();
		dto2.setStypecode("授权支付额度");
		dto2.setSvalue("5106");
		biztypelist.add(dto2);
		
		TdEnumvalueDto dto3 = new TdEnumvalueDto();
		dto3.setStypecode("商行划款申请");
		dto3.setSvalue("2301");
		biztypelist.add(dto3);
		
		TdEnumvalueDto dto4 = new TdEnumvalueDto();
		dto4.setStypecode("商行退款申请");
		dto4.setSvalue("2302");
		biztypelist.add(dto4);
	}

	/**
	 * Direction: 查询
	 * ename: queryResult
	 * 引用方法: 
	 * viewers: 查询结果
	 * messages: 
	 */
    public String queryResult(Object o){
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
        return super.queryResult(o);
    }

	/**
	 * Direction: 单选
	 * ename: singleSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	dto = (TsIsnopaperDto) o;
        return super.singleSelect(o);
    }

	/**
	 * Direction: 跳转录入界面
	 * ename: goInput
	 * 引用方法: 
	 * viewers: 录入界面
	 * messages: 
	 */
    public String goInput(Object o){
    	dto = new TsIsnopaperDto();
		dto.setSorgcode(loginfo.getSorgcode());
		dto.setSisnopaper(StateConstant.MSG_NORELAY_TIPS);
        return super.goInput(o);
    }

	/**
	 * Direction: 录入保存
	 * ename: inputSave
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String inputSave(Object o){
    	if(datacheck(dto)){
			return null;
		}
    	try {
    		  tsIsnopaperService.addInfo(dto);
		    } catch (Exception e) {
			log.error(e);
			MessageDialog.openMessageDialog(null,e.getMessage());
			this.queryResult(o);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsIsnopaperDto();
		this.queryResult(o);
        return super.backMaintenance(o);
    }

	/**
	 * Direction: 返回到维护界面
	 * ename: backMaintenance
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String backMaintenance(Object o){
    	dto = new TsIsnopaperDto();
    	this.queryResult(o);
        return super.backMaintenance(o);
    }

	/**
	 * Direction: 删除
	 * ename: delete
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if (null == dto.getStrecode() || "".equals(dto.getStrecode())) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		// 提示用户确定删除
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否要删除选择的记录？")) {
			dto=new TsIsnopaperDto();
			return "";
		}
		try {
			tsIsnopaperService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		queryResult(null);
		editor.fireModelChanged();
		this.queryResult(o);
        return super.delete(o);
    }

	/**
	 * Direction: 到修改界面
	 * ename: goModify
	 * 引用方法: 
	 * viewers: 修改界面
	 * messages: 
	 */
    public String goModify(Object o){
    	if (null == dto.getStrecode() || "".equals(dto.getStrecode())) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
        return super.goModify(o);
    }

	/**
	 * Direction: 修改保存
	 * ename: modifySave
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String modifySave(Object o){
    	if(datacheck(dto)){
			return null;
		}
		try {
    			tsIsnopaperService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openMessageDialog(null,e.getMessage());
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsIsnopaperDto();
		this.queryResult(o);
        return super.backMaintenance(o);
    }

	/**
	 * Direction: 返回
	 * ename: goBack
	 * 引用方法: 
	 * viewers: 信息查询
	 * messages: 
	 */
    public String goBack(Object o){
    	dto = new TsIsnopaperDto();
		this.queryResult(o);
        return super.goBack(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	StringBuffer whereSql=new StringBuffer("1=1");
    	try {
			querydto.setSorgcode(loginfo.getSorgcode());
			if(querydto.getSbankname()!=null&&!querydto.getSbankname().equals("")){
				whereSql.append(" ")
			    .append("AND S_BANKNAME LIKE '")
			    .append("%")
				.append(querydto.getSbankname())
				.append("%")
				.append("'");
			}
			querydto.setSbankname(null);//为了使用LIKE模糊查询
			return commonDataAccessService.findRsByDtoWithWherePaging(querydto,
					pageRequest, whereSql.toString());

		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

    
    private boolean datacheck(TsIsnopaperDto dto) {
		if(!isNumber(dto.getSbankcode())){
			MessageDialog.openMessageDialog(null, "代理银行行号必须为数字。");
			return true;
		}
		return false;
	}
    
    /**
	 * 判断字符串是否是数字组成
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str)
    {
        java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("[0-9]*");
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

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		TsIsnopaperBean.log = log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public TsIsnopaperDto getDto() {
		return dto;
	}

	public void setDto(TsIsnopaperDto dto) {
		this.dto = dto;
	}

	public TsIsnopaperDto getQuerydto() {
		return querydto;
	}

	public void setQuerydto(TsIsnopaperDto querydto) {
		this.querydto = querydto;
	}

	public PagingContext getPagingcontext() {
		return pagingcontext;
	}

	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
	}

	public List<TdEnumvalueDto> getBiztypelist() {
		return biztypelist;
	}

	public void setBiztypelist(List<TdEnumvalueDto> biztypelist) {
		this.biztypelist = biztypelist;
	}
	
}
package com.cfcc.itfe.client.para.tsbankcodeconverttype;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author sunyan
 * @time   13-10-16 17:35:08
 * 子系统: Para
 * 模块:TsBankcodeConverttype
 * 组件:TsConvertbanktype
 */
public class TsConvertbanktypeBean extends AbstractTsConvertbanktypeBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TsConvertbanktypeBean.class);
    ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
	.getDefault().getLoginInfo();
    private PagingContext pagingcontext = new PagingContext(this);
    
    /**
	 * TsConvertbanktypeDto
	 */
    private TsConvertbanktypeDto dto = null;
    // 查询dto
	private TsConvertbanktypeDto querydto = null;
	
	
    public TsConvertbanktypeBean() {
      super();
      querydto = new TsConvertbanktypeDto();
      querydto.setSorgcode(loginfo.getSorgcode());
      dto = new TsConvertbanktypeDto();
    }
    
	/**
	 * Direction: 查询
	 * ename: queryConvert
	 * 引用方法: 
	 * viewers: 查询结果
	 * messages: 
	 */
    public String queryConvert(Object o){
	      PageRequest pageRequest = new PageRequest();
		  PageResponse pageResponse = retrieve(pageRequest);
		  pagingcontext.setPage(pageResponse);
          return super.queryConvert(o);
    }
    
    /**
	 * Direction: 跳转录入界面
	 * ename: goInput
	 * 引用方法: 
	 * viewers: 录入界面
	 * messages: 
	 */
    public String goInput(Object o){
    	dto = new TsConvertbanktypeDto();
		dto.setSorgcode(loginfo.getSorgcode());
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
    		  tsConvertbanktypeService.addInfo(dto);
		    } catch (Exception e) {
			log.error(e);
			MessageDialog.openMessageDialog(null,e.getMessage());
			this.queryConvert(o);
			return super.inputSave(o);
		}
		dto = new TsConvertbanktypeDto();
		this.queryConvert(o);
		return super.backMaintenance(o);
    }
    
    /**
	 * Direction: 返回到维护界面
	 * ename: backMaintenance
	 * 引用方法: 
	 * viewers: 查询结果
	 * messages: 
	 */
    public String backMaintenance(Object o){
    	dto = new TsConvertbanktypeDto();
    	this.queryConvert(o);
		return super.backMaintenance(o);
    }
    
	/**
	 * Direction: 单选
	 * ename: singleSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	  dto = (TsConvertbanktypeDto) o;
          return super.singleSelect(o);
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
			dto=new TsConvertbanktypeDto();
			return "";
		}
		try {
			tsConvertbanktypeService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		queryConvert(null);
		editor.fireModelChanged();
		this.queryConvert(o);
		return super.delete(o);
    }    
    
    /**
	 * Direction: 到修改界面 ename: goModify 引用方法: viewers: 修改界面 messages:
	 */
	public String goModify(Object o) {
		if (null == dto.getStrecode() || "".equals(dto.getStrecode())) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		return super.goModify(o);
	}
    
	/**
	 * Direction: 修改保存 ename: modifySave 引用方法: viewers: * messages:
	 */
	public String modifySave(Object o) {
		if(datacheck(dto)){
			return null;
		}
		try {
    			tsConvertbanktypeService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openMessageDialog(null,e.getMessage());
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsConvertbanktypeDto();
		this.queryConvert(o);
		return super.backMaintenance(o);
	}
	
	/**
	 * Direction: 返回 ename: goBack 引用方法: viewers: 信息查询 messages:
	 */
	public String goBack(Object o) {
		dto = new TsConvertbanktypeDto();
		this.queryConvert(o);
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
		String fileName = filePath+ File.separator+ loginfo.getSorgcode() + "_行号行别对照关系.csv";
		try {
			List<TsConvertbanktypeDto> list = commonDataAccessService.findRsByDto(querydto);
			if(null == list || list.size() == 0 ){
				MessageDialog.openMessageDialog(null, "没有需要导出的数据！");
				return null;
			}
			StringBuffer result = new StringBuffer();
			result.append("机构代码_VARCHAR_NOT NULL,国库代码_VARCHAR_NOT NULL," +
					"代理银行名称_VARCHAR,代理银行代码_CHARACTER_NOT NULL,银行行别代码_VARCHAR_NOT NULL\n");
			for(TsConvertbanktypeDto tmp : list ){
				result.append(tmp.getSorgcode() + ",");		//机构代码
				result.append(tmp.getStrecode() + ",");		//国库代码
				result.append(tmp.getSbankname() + ",");	//代理银行名称
				result.append(tmp.getSbankcode() + ",");    //代理银行代码
				result.append(tmp.getSbanktype() + ",\n");	//银行行别代码（凭证库定义3位数字）
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
	
	private boolean datacheck(TsConvertbanktypeDto dto) {
		if(!isNumber(dto.getSbankcode())){
			MessageDialog.openMessageDialog(null, "代理银行行号必须为数字。");
			return true;
		}
		if(!isNumber(dto.getSbanktype())){
			MessageDialog.openMessageDialog(null, "银行行别必须为数字。");
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
	
	
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	try {
			querydto.setSorgcode(loginfo.getSorgcode());
			return commonDataAccessService.findRsByDtoWithWherePaging(querydto,
					pageRequest, "1=1");

		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	public PagingContext getPagingcontext() {
		return pagingcontext;
	}

	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
	}

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		TsConvertbanktypeBean.log = log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public TsConvertbanktypeDto getDto() {
		return dto;
	}

	public void setDto(TsConvertbanktypeDto dto) {
		this.dto = dto;
	}

	public TsConvertbanktypeDto getQuerydto() {
		return querydto;
	}

	public void setQuerydto(TsConvertbanktypeDto querydto) {
		this.querydto = querydto;
	}

    
}
package com.cfcc.itfe.client.para.tpsharedivide;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TpShareDivideDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author ZZD
 * @time   13-03-04 11:19:36
 * 子系统: Para
 * 模块:TpShareDivide
 * 组件:TpShareDivide
 */
public class TpShareDivideBean extends AbstractTpShareDivideBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TpShareDivideBean.class);
    private ITFELoginInfo loginfo;
    public TpShareDivideBean() {
      super();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      dto = new TpShareDivideDto();
      finddto = new TpShareDivideDto();
      finddto.setSbookorgcode(loginfo.getSorgcode());
      pagingcontext = new PagingContext(this);
                  
    }
    
	/**
	 * Direction: 跳转录入界面
	 * ename: goInput
	 * 引用方法: 
	 * viewers: 录入界面
	 * messages: 
	 */
    public String goInput(Object o){
    	dto = new TpShareDivideDto();
          return super.goInput(o);
    }

	/**
	 * Direction: 录入保存
	 * ename: inputSave
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String inputSave(Object o){
    	try {
    		dto.setTssysupdate(new Timestamp(System.currentTimeMillis()));
    		tpShareDivideService.addInfo(dto);
    		
   		} catch (Throwable e) {
   			log.error(e);
   			MessageDialog.openErrorDialog(null, e);
   			
   			return super.inputSave(o);
   		}
   		MessageDialog.openMessageDialog(null,StateConstant.INPUTSAVE);
   		dto = new TpShareDivideDto();
   		editor.fireModelChanged();
          return super.inputSave(o);
    }

	/**
	 * Direction: 返回到维护界面
	 * ename: backMaintenance
	 * 引用方法: 
	 * viewers: 维护界面
	 * messages: 
	 */
    public String backMaintenance(Object o){
    	dto = new TpShareDivideDto();
          return super.backMaintenance(o);
    }

	/**
	 * Direction: 单选
	 * ename: singleSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	dto = (TpShareDivideDto)o;
          return super.singleSelect(o);
    }

	/**
	 * Direction: 删除
	 * ename: delete
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if (dto.getIparamseqno()== null) {
			MessageDialog.openMessageDialog(null,StateConstant.DELETESELECT);
			return "";
		}
    	try {
    		tpShareDivideService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TpShareDivideDto();
		editor.fireModelChanged();
		return this.searchRs(o);
    }

	/**
	 * Direction: 到修改界面
	 * ename: goModify
	 * 引用方法: 
	 * viewers: 修改界面
	 * messages: 
	 */
    public String goModify(Object o){
    	if (dto.getSbookorgcode() == null||dto.getIparamseqno()==null) {
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
    	try {
    		dto.setTssysupdate(new Timestamp(System.currentTimeMillis()));
    		tpShareDivideService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TpShareDivideDto();
		editor.fireModelChanged();
		return super.backMaintenance(o);
    }

	/**
	 * Direction: 查询
	 * ename: searchRs
	 * 引用方法: 
	 * viewers: 维护界面
	 * messages: 
	 */
    public String searchRs(Object o){
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
//		if(pageResponse.getData().size()<=0){
//			MessageDialog.openMessageDialog(null,"没有查询到符合条件的数据");
//			return this.rebackSearch(o);
//		}
		pagingcontext.setPage(pageResponse);
        return super.searchRs(o);
    }

	/**
	 * Direction: 返回查询界面
	 * ename: rebackSearch
	 * 引用方法: 
	 * viewers: 查询界面
	 * messages: 
	 */
    public String rebackSearch(Object o){
    	dto = new TpShareDivideDto();
    	finddto = new TpShareDivideDto();
    	finddto.setSbookorgcode(loginfo.getSorgcode());
          return super.rebackSearch(o);
    }
    
    
	public String expfile(Object o) {
		// 选择保存路径
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
			return "";
		}
		String fileName = filePath+ File.separator+ loginfo.getSorgcode() + "_共享分成参数维护 .csv";
		try {
			List<TpShareDivideDto> result = commonDataAccessService.findRsByDto(finddto);
			if(null == result || result.size() == 0 ){
				MessageDialog.openMessageDialog(null, "没有需要导出的数据！");
				return null;
			}
			export(result, fileName);
			MessageDialog.openMessageDialog(null, "操作成功！");
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.expfile(o);
	}
	
	private void export(List<TpShareDivideDto> resultlist,String filepath ) throws FileOperateException{
		String separator = ",";
		StringBuffer result = new StringBuffer();
		result.append("核算主体代码_CHARACTER_ NOT NULL,源国库主体代码_CHARACTER_NOT NULL,交易国库主体代码_CHARACTER_NOT NULL," +
				"收款国库代码_CHARACTER_NOT NULL,源征收机关代码_VARCHAR,源预算科目代码_VARCHAR_NOT NULL,源预算种类_CHARACTER_NOT NULL," +
				"源辅助标志_VARCHAR,分成后国库主体代码_CHARACTER_NOT NULL,分成后征收机关_VARCHAR_NOT NULL," +
				"分成后级次_CHARACTER_NOT NULL,分成后预算科目_VARCHAR_NOT NULL,分成后预算种类_CHARACTER_NOT NULL," +
				"分成后辅助标志_VARCHAR,参加分成比例_DECIMAL_NOT NULL,辖属关系_CHARACTER_NOT NULL\n");
		for(TpShareDivideDto tmp : resultlist){
			result.append(tmp.getSbookorgcode() + separator);	//核算主体代码
			result.append(tmp.getSroottrecode() + separator);	//源国库主体代码
			result.append(tmp.getStratrecode() + separator);	//交易国库主体代码
			result.append(tmp.getSpayeetrecode() + separator);	//收款国库代码
			result.append(tmp.getSroottaxorgcode() + separator);	//源征收机关代码
			result.append(tmp.getSrootbdgsbtcode() + separator);	//源预算科目代码
			result.append(tmp.getCrootbdgkind() + separator);	//源预算种类
			result.append(tmp.getSrootastflag() + separator);	//源辅助标志
			result.append(tmp.getSafttrecode() + separator);	//分成后国库主体代码
			result.append(tmp.getSafttaxorgcode() + separator);	//分成后征收机关
			result.append(tmp.getCaftbdglevel() + separator);	//分成后级次
			result.append(tmp.getSaftbdgsbtcode() + separator);	//分成后预算科目
			result.append(tmp.getCaftbdgtype() + separator);	//分成后预算种类
			result.append(tmp.getSaftastflag() + separator);	//分成后辅助标志
			result.append(tmp.getFjoindividerate() + separator);	//参加分成比例
			result.append(tmp.getCgovernralation() );	//辖属关系
			result.append("\n");
		}
		FileUtil.getInstance().writeFile(filepath, result.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	try{
    		return commonDataAccessService.findRsByDtoWithWherePaging(finddto,
					pageRequest, "1=1");
    	} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

}
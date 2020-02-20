package com.cfcc.itfe.client.para.tsmankey;

import itferesourcepackage.TskeyorgnameEnumFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsGenbankandreckbankDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 12-06-25 16:34:52 子系统: Para 模块:tsmankey 组件:TsMankey
 */
public class TsMankeyBean extends AbstractTsMankeyBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsMankeyBean.class);
	private ITFELoginInfo loginfo;
	private String repeatencryptkey;
	private List<TsMankeyDto> checkList = new ArrayList<TsMankeyDto>();
	List<TdEnumvalueDto> list = new ArrayList<TdEnumvalueDto>();
	java.sql.Date daffdate;
	List<Mapper> maplist;
	String funcinfo;
	String skeymode;
	String skeyorgcode;
	Date current;
	public TsMankeyBean() {
		super();
		dto = new TsMankeyDto();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		pagingContext = new PagingContext(this);
		repeatkey = "";
		repeatencryptkey = "";
		skeymode = loginfo.getMankeyMode();
		TskeyorgnameEnumFactory  list2 = new TskeyorgnameEnumFactory();
		maplist = list2.getEnums(null);
		current = com.cfcc.jaf.common.util.DateUtil.currentDate();
		daffdate =com.cfcc.jaf.common.util.DateUtil.difDate((java.sql.Date) current, 1);
		funcinfo="自动获取密钥代码 功能是从财政机构信息维护、征收机关对照维护、代理银行与支付行号对应关系表中获取TCBS的相关代码用于密钥生成。密钥更新及导出  是更新选择机构的密钥，并导出密钥文件";
		list = getorglist(skeymode);
		init();
	
	}

	/**
	 * Direction: 转到密钥参数录入 ename: toKeysave 引用方法: viewers: 密钥参数录入 messages:
	 */
	public String toKeysave(Object o) {
		if(StateConstant.KEY_ALL.equals(loginfo.getMankeyMode()) && !StateConstant.ORG_CENTER_CODE.equals(loginfo.getSorgcode())) { //如果是全省统一，则不需要其他核算主体维护该参数
			MessageDialog.openMessageDialog(null, "目前密钥维护模式为全省统一，由中心管理员设置即可!");
			return "";
		}
		dto = new TsMankeyDto();
		repeatkey = "";
		repeatencryptkey = "";
		dto.setSorgcode(loginfo.getSorgcode());
		dto.setSkeymode(skeymode);
		dto.setImodicount(StateConstant.KEYORG_STATUS_AFF);
		return super.toKeysave(o);
	}

	/**
	 * Direction: 密钥参数删除 ename: keyDelete 引用方法: viewers: * messages:
	 */
	public String keyDelete(Object o) {
		if (null == checkList || checkList.size() != 1) {
			MessageDialog.openMessageDialog(null, "删除密钥时一次只能选择一条记录！");
			checkList = new ArrayList<TsMankeyDto>();
			return null;
		} else {
			dto = checkList.get(0);
		}
		if (!dto.getSorgcode().equals(loginfo.getSorgcode())) {
			MessageDialog.openMessageDialog(null, "你没有本条记录的操作权限！");
			return null;
		}
		boolean flag = org.eclipse.jface.dialogs.MessageDialog.openQuestion(
				null, "提示信息", "你确定删除此条记录吗？");
		if (!flag) {
			return null;
		}
		try {
			tsMankeyService.keyDelete(dto);
			MessageDialog.openMessageDialog(null, "处理成功！");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, StateConstant.DELETEFAIL);
			return null;
		}
		dto = new TsMankeyDto();
		init();
		checkList = new ArrayList<TsMankeyDto>();
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.keyDelete(o);
	}

	/**
	 * Direction: 转到密钥参数修改 ename: toKeymodify 引用方法: viewers: 密钥参数修改 messages:
	 */
	public String toKeymodify(Object o) {

		if (null == checkList || checkList.size() != 1) {
			MessageDialog.openMessageDialog(null, "修改密钥时一次只能选择一条记录！");
			checkList =new ArrayList<TsMankeyDto>();
			return null;
		} else {
			dto = checkList.get(0);
		}
		if (!dto.getSorgcode().equals(loginfo.getSorgcode())) {
			if (loginfo.getSorgcode().equals("000000000000")) {
				skeymode = dto.getSkeymode();
				return super.toKeymodify(o);
			}
			MessageDialog.openMessageDialog(null, "你没有本条记录的操作权限！");
			return null;
		}
		
		skeymode =dto.getSkeymode();
		
		repeatkey = dto.getSkey();
		repeatencryptkey = dto.getSencryptkey();
		dto.setSorgcode(loginfo.getSorgcode());
		if (!AdminConfirmDialogFacade.open("修改密钥需要会计主管授权！")) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			return null;
		}
		return super.toKeymodify(o);
	}

	/**
	 * Direction: 密钥参数录入 ename: keySave 引用方法: viewers: 密钥参数列表 messages:
	 */
	public String keySave(Object o) {
		if (null == dto || dto.getSkeyorgcode()==null) {
			MessageDialog.openMessageDialog(null, StateConstant.CHECKVALID);
			return null;
		} else if (isExists()) {
			MessageDialog.openMessageDialog(null, StateConstant.PRIMAYKEY);
			return null;
		} else if (!repeatkey.equals(dto.getSkey())) {
			MessageDialog.openMessageDialog(null, "确认签名密钥与机构签名密钥不一致！");
			return null;
		} else if ((repeatencryptkey != null && dto.getSencryptkey() != null)
				&& !repeatencryptkey.equals(dto.getSencryptkey())) {
			MessageDialog.openMessageDialog(null, "确认加密密钥与机构加密密钥不一致！");
			return null;
		}
		dto.setDaffdate((java.sql.Date) current);
		dto.setSnewkey(dto.getSkey());
		dto.setSnewencryptkey(dto.getSkey());
		try {
			tsMankeyService.keySave(dto);
			MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, StateConstant.INPUTFAIL);
			return null;
		}
		dto = new TsMankeyDto();
		init();
		return super.keySave(o);
	}

	@SuppressWarnings("unchecked")
	public boolean isExists() {
		TsMankeyDto tempdto = new TsMankeyDto();
		try {
			tempdto.setSorgcode(dto.getSorgcode());
			tempdto.setSkeyorgcode(dto.getSkeyorgcode());
			tempdto.setSkeymode(dto.getSkeymode());
			List list = commonDataAccessService.findRsByDto(tempdto);
			if (list != null && list.size() > 0) {
				return true;
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return false;
	}

	/**
	 * Direction: 转到密钥参数列表 ename: toKeylist 引用方法: viewers: 密钥参数列表 messages:
	 */
	public String toKeylist(Object o) {
		dto = new TsMankeyDto();
		init();
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		return super.toKeylist(o);
	}

	/**
	 * Direction: 密钥参数修改 ename: keyModify 引用方法: viewers: 密钥参数列表 messages:
	 */
	public String keyModify(Object o) {
//		if (null == dto || null == dto.getSkeymode()
//				|| dto.getSkeymode().equals("") || dto.getSkey().equals("")
//				|| null == dto.getSkey()) {
//			MessageDialog.openMessageDialog(null, StateConstant.CHECKVALID);
//			return null;
//		} else 
		if (dto.getSnewkey()==null||"".equals(dto.getSnewkey())) {
			MessageDialog.openMessageDialog(null, "加密密钥不可为空！");
			return null;
		}else if ((dto.getSnewkey() != null && dto.getSnewencryptkey() != null)
				&& !dto.getSnewencryptkey().equals(dto.getSnewkey())) {
			MessageDialog.openMessageDialog(null, "确认加密密钥与机构加密密钥不一致！");
			return null;
		}
		try {
//			dto.setDaffdate((java.sql.Date) current);
//			dto.setSnewkey(dto.getSkey());
//			dto.setSnewencryptkey(dto.getSkey());
			tsMankeyService.keyModify(dto);
			MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, StateConstant.MODIFYFAIL);
			return null;
		}
		checkList = new ArrayList<TsMankeyDto>();
		dto = new TsMankeyDto();
		init();
		return super.keyModify(o);
	}

	/**
	 * Direction: 单击选中对象 ename: clickSelect 引用方法: viewers: * messages:
	 */
	public String clickSelect(Object o) {
		dto = (TsMankeyDto) o;
		return super.clickSelect(o);
	}

	/**
	 * Direction: 更新密钥并导出 ename: updateAndExport 引用方法: viewers: * messages:
	 */
	public String updateAndExport(Object o) {
		if (checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要更新密钥的机构记录！");
			return null;
		}
//		MessageDialog.openMessageDialog(null, "测试阶段密钥不受生效日期的限制，导出后密钥及时生效");
		org.eclipse.jface.dialogs.MessageDialog msg = new org.eclipse.jface.dialogs.MessageDialog(
				editor.getContainer().getShell(), "更新机构密钥提示", null,
				"是否确定要更新选择机构的密钥,更新密钥后，请及时将密钥文件下发相关单位\n"
						+ " 否则会造成双方维护密钥不一致，影响业务正常进行，请慎重处理！",
				org.eclipse.jface.dialogs.MessageDialog.QUESTION, new String[] {
						"是", "否" }, 0);
		if (msg.open() == org.eclipse.jface.dialogs.MessageDialog.OK) {
			if (null == daffdate) {
				MessageDialog.openMessageDialog(null, "请录入新密钥的生效日期！");
				return null;
			} else if (daffdate.compareTo(com.cfcc.jaf.common.util.DateUtil
					.currentDate()) <= 0) {
				MessageDialog.openMessageDialog(null, "生效日期需要大于当前日期");
				return null;
			}
			String filePath="C:";
			String dirsep = File.separator;
			String clientFilePath = null;
			String strdate = DateUtil.date2String2(new Date()); // 当前系统年月日
			try {
				List<String> filelist = tsMankeyService.updateAndExport(checkList,
						daffdate);
				for (int i = 0; i < filelist.size(); i++) {
					String serverFilePath = filelist.get(i).replace("\\", "/");
					int j = serverFilePath.lastIndexOf("/");
					String partfilepath = serverFilePath.replaceAll(
							serverFilePath.substring(0, j + 1), "");
					clientFilePath = filePath + dirsep + strdate
							+ dirsep + partfilepath;
					File f = new File(clientFilePath);
					File dir = new File(f.getParent());
					if (!dir.exists()) {
						dir.mkdirs();
					}
					if (f.exists()) {
						f.delete();
					}
					ClientFileTransferUtil.downloadFile(serverFilePath,
							clientFilePath);

				}
				if (filelist.size() > 0) {
					init();
					editor.fireModelChanged();
					MessageDialog.openMessageDialog(null, "密钥已更新,共生成"+ filelist.size() + "个文件,"+
							"保存在"+filePath+ dirsep + strdate+ dirsep+"目录下\n新密钥生效日期 [" + daffdate+" ],请及时下发相关单位");
					
				} else {
					MessageDialog.openMessageDialog(null, "密钥没有更新！");
				}

			} catch (Exception e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
			}
		}else{
			MessageDialog.openMessageDialog(null, "密钥未更新！");
		}
		checkList = new ArrayList<TsMankeyDto>();
		editor.fireModelChanged();
		return null;
	}

	/**
	 * Direction: 自动获取出票单位 ename: autoGetBillOrg 引用方法: viewers: * messages:
	 */
	public String autoGetBillOrg(Object o) {
		try {
			List crelist = tsMankeyService.autoGetBillOrg();
			init();
			editor.fireModelChanged();
			checkList =new ArrayList<TsMankeyDto>();
			MessageDialog.openMessageDialog(null, "获取出票单位信息成功!,共获取"
					+ crelist.size() + "记录");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return "";
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
			dto.setSorgcode(loginfo.getSorgcode());
			return tsMankeyService.keyList(dto, pageRequest);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	public void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingContext.setPage(pageResponse);
		
	}
	public java.sql.Date getDaffdate() {
		return daffdate;
	}

	public void setDaffdate(java.sql.Date daffdate) {
		this.daffdate = daffdate;
	}

	public String getSkeyorgcode() {
		return skeyorgcode;
	}

	public void setSkeyorgcode(String skeyorgcode) {
		this.skeyorgcode = skeyorgcode;
	}

	public String getRepeatencryptkey() {
		return repeatencryptkey;
	}

	public void setRepeatencryptkey(String repeatencryptkey) {
		this.repeatencryptkey = repeatencryptkey;
	}



	public String getSkeymode() {
		return skeymode;
	}

	public void setSkeymode(String skeymode) {
		this.skeymode = skeymode;
		list = getorglist(skeymode);
		editor.fireModelChanged();

	}
	/**
	 * Direction: 全选
	 * ename: allSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String allSelect(Object o){
        if (checkList.size()>0) {
			checkList = new ArrayList<TsMankeyDto>();
		}else{
			checkList = pagingContext.getPage().getData();
		}
    	editor.fireModelChanged();
        return "";
    }
    
    /**
	 * Direction: 已生效密钥导出
	 * ename: affKeyExport
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String affKeyExport(Object o){
    	if (checkList.size()>0) {
    		String filePath="C:";
			String dirsep = File.separator;
			String strdate = DateUtil.date2String2(new Date()); // 当前系统年月日
			String msg = "";
			int i=0,j=0;
    		for (TsMankeyDto _dto :checkList) {
    			if (_dto.getImodicount().compareTo(StateConstant.KEYORG_STATUS_NO)==0) {
    				i++;
    				continue;
				}
    			String stitle =_dto.getSorgcode()+"_"+_dto.getSkeyorgcode()+".KEY";
    			String fileName =filePath+dirsep+strdate+dirsep+stitle;
    			File file = new File(fileName);
    			File dir = new File(file.getParent());
    			FileOutputStream output = null;
    				if (!dir.exists()) {
    					dir.mkdirs();
    				}
    				try {
						output = new FileOutputStream(fileName, false);
						output.write(_dto.getSkey().getBytes("GBK"));
						output.close();
					} catch (Exception e) {
						MessageDialog.openErrorDialog(null, e);
						return null;
					} 
				j++;	
			}
    		if (j>0) {
    			msg+="共导出"+j+"个密钥文件，放在"+filePath+dirsep+strdate;
    		}	
    		if (i>0) {
    			msg+="有"+i+"个机构密钥尚未生成，不能导出";
			}
			MessageDialog.openMessageDialog(null, msg);
			checkList = new ArrayList<TsMankeyDto>();
		}else{
			MessageDialog.openMessageDialog(null, "请选择要导出的 密钥机构");
			checkList = new ArrayList<TsMankeyDto>();
		}
    	editor.fireModelChanged();
        return "";
    }

	public List<TdEnumvalueDto> getList() {
		return list;
	}

	public void setList(List<TdEnumvalueDto> list) {
		this.list = list;
	}

	public List<TsMankeyDto> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<TsMankeyDto> checkList) {
		this.checkList = checkList;
	}
    
	public List<Mapper> getMaplist() {
		return maplist;
	}

	public String getFuncinfo() {
		return funcinfo;
	}

	public void setFuncinfo(String funcinfo) {
		this.funcinfo = funcinfo;
	}

	public void setMaplist(List<Mapper> maplist) {
		this.maplist = maplist;
	}
	
	

	public List<TdEnumvalueDto> getorglist(String keymode) {
		List<TdEnumvalueDto> list = new ArrayList<TdEnumvalueDto>();
		TdEnumvalueDto _dto = new TdEnumvalueDto();
		IItfeCacheService cacheServer = (IItfeCacheService) ServiceFactory
				.getService(IItfeCacheService.class);
		if (StateConstant.KEY_BOOK.equals(keymode)) {
			_dto.setSvalue(loginfo.getSorgcode());
			_dto.setSvaluecmt(loginfo.getSorgName());
			list.add(_dto);

		} else if (StateConstant.KEY_TRECODE.equals(keymode)) {
			TsTreasuryDto dto = new TsTreasuryDto();
			dto.setSorgcode(loginfo.getSorgcode());
			try {
				List<TsTreasuryDto> enumList = cacheServer
						.cacheGetValueByDto(dto);
				if (enumList.size() > 0) {
					for (TsTreasuryDto emuDto : enumList) {
						_dto.setSvalue(emuDto.getStrecode());
						_dto.setSvaluecmt(emuDto.getStrename());
						list.add(_dto);
					}
				}
			} catch (Throwable e) {
				MessageDialog.openErrorDialog(null, e);
			}

		} else if (StateConstant.KEY_BILLORG.equals(keymode)) {
			TsConvertfinorgDto dto = new TsConvertfinorgDto();
			dto.setSorgcode(loginfo.getSorgcode());
			try {
				List<TsConvertfinorgDto> enumList = cacheServer
						.cacheGetValueByDto(dto);
				if (enumList.size() > 0) {
					for (TsConvertfinorgDto emuDto : enumList) {
						_dto.setSvalue(emuDto.getSfinorgcode());
						_dto.setSvaluecmt(emuDto.getSfinorgname());
						list.add(_dto);
					}
				}
			} catch (Throwable e) {
				MessageDialog.openErrorDialog(null, e);
			}

		} else if (StateConstant.KEY_GENBANK.equals(toString())) {
			TsGenbankandreckbankDto dto = new TsGenbankandreckbankDto();
			dto.setSbookorgcode(loginfo.getSorgcode());
			try {
				List<TsGenbankandreckbankDto> enumList = cacheServer
						.cacheGetValueByDto(dto);
				if (enumList.size() > 0) {
					for (TsGenbankandreckbankDto emuDto : enumList) {
						_dto.setSvalue(emuDto.getSreckbankcode());
						_dto.setSvaluecmt(emuDto.getSreckbankname());
						list.add(_dto);

					}
				}
			} catch (Throwable e) {
				MessageDialog.openErrorDialog(null, e);
			}

		} else if (StateConstant.KEY_TAXORG.equals(toString())) {
			TsTaxorgDto dto = new TsTaxorgDto();
			dto.setSorgcode(loginfo.getSorgcode());
			try {
				List<TsTaxorgDto> enumList = cacheServer
						.cacheGetValueByDto(dto);
				if (enumList.size() > 0) {

					for (TsTaxorgDto emuDto : enumList) {
						_dto.setSvalue(emuDto.getStaxorgcode());
						_dto.setSvaluecmt(emuDto.getStaxorgname());
						list.add(_dto);
					}
				}
			} catch (Throwable e) {
				MessageDialog.openErrorDialog(null, e);
			}

		} else if(StateConstant.KEY_ALL.equals(keymode)) {
			_dto.setSvalue(StateConstant.ORG_CENTER_CODE);
			_dto.setSvaluecmt("中心管理机构");
			list.add(_dto);
		}
		return list;

	}
}
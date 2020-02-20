package com.cfcc.itfe.client.dataquery.findatastatdownforyn;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdTaxorgMergerDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvUsersconditionDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;
/**
 * codecomment: 
 * @author db2admin
 * @time   13-04-18 14:21:17
 * 子系统: DataQuery
 * 模块:FinDataStatDownForYN
 * 组件:FinDataStatDownForYN
 */
public class FinDataStatDownForYNBean extends AbstractFinDataStatDownForYNBean implements IPageDataProvider {

	private static Log log = LogFactory.getLog(FinDataStatDownForYNBean.class);
	ITFELoginInfo loginfo;
	String sleTrimFlag;
	String sleBudKind;
	String sleTreCode;
	String sleTaxOrgCode;
	String sleOfFlag;
	String sleSumItem;
	Date sdate;
	String sfuncbdgsbtcode;
	String exportarea;
	Date sdateend;
	// 国库代码列表
	private List<TsTreasuryDto> treList;
	// 征收机关代码列表
	private List<TsTaxorgDto> taxorgList;
	public FinDataStatDownForYNBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		init();

	}

	/**
	 * Direction: 导出excel文件 ename: exportFile 引用方法: viewers: * messages:
	 */
	public String exportFile(Object o) {
		//保存用户查询科目代码条件信息
		TvUsersconditionDto tvUsersconditionDto = new TvUsersconditionDto();
		tvUsersconditionDto.setSorgcode(loginfo.getSorgcode());
		tvUsersconditionDto.setSusercode(loginfo.getSuserCode());
		tvUsersconditionDto.setSusername(loginfo.getSuserName());
		tvUsersconditionDto.setSconditions(this.getSfuncbdgsbtcode());
		TrIncomedayrptDto incomedto = new TrIncomedayrptDto();
		// 国库主体代码
		incomedto.setStrecode(sleTreCode);
		// 根据国库代码找同级机关代码
		/**
		 * 第一步 根据国库代码找到对应的财政代码
		 */
		TsConvertfinorgDto finadto = new TsConvertfinorgDto();
		finadto.setStrecode(sleTreCode);
		finadto.setSorgcode(loginfo.getSorgcode());

		List finorgList = null;
		try {
			finorgList = commonDataAccessService.findRsByDto(finadto);
			finDataStatDownForYNService.saveCondition(tvUsersconditionDto);
		} catch (ITFEBizException e1) {
			MessageDialog.openErrorDialog(null, e1);
		}
		if (finorgList == null || finorgList.size() == 0) {
			log.error("请在财政机构信息中维护国库和财政代码的对应关系!");
			MessageDialog.openMessageDialog(null, "请在财政机构信息中维护国库和财政代码的对应关系!");
			return "";
		} else if (finorgList.size() > 1) {
			log.error("一个国库只能对应一个同级财政");
			MessageDialog.openMessageDialog(null, "一个国库只能对应一个同级财政");
			return "";
		} else if(!exportarea.equals(StateConstant.ALL_AREA)){
			finadto = (TsConvertfinorgDto) finorgList.get(0);
			incomedto.setSfinorgcode(finadto.getSfinorgcode());
		}

		// 征收机关代码
		incomedto.setStaxorgcode(sleTaxOrgCode);
		// 预算种类
		incomedto.setSbudgettype(sleBudKind);
		// 辖属标志
		incomedto.setSbelongflag(sleOfFlag);
		// 调整期标志
		incomedto.setStrimflag(sleTrimFlag);
		// 日期
		incomedto.setSrptdate(sdate.toString().replaceAll("-", ""));
		incomedto.setSbudgetsubname(sdateend.toString().replaceAll("-",""));
		// 是否含款合计
		incomedto.setSdividegroup(sleSumItem);
		incomedto.setSbudgetsubcode(sfuncbdgsbtcode);
		//用预算级次代码代传参数exportarea
		incomedto.setSbudgetlevelcode(exportarea);
		// 选择保存路径
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		List<String> filelist = new ArrayList<String>();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
			return "";
		}
		String serverFilePath;
		String clientFilePath;
		String partfilepath = "";
		String strdate = DateUtil.date2String2(new Date()); // 当前系统年月日
		String dirsep = File.separator;
		
		try {
			filelist = finDataStatDownForYNService.makeRptFile(incomedto);
			for (int i = 0; i < filelist.size(); i++) {
				serverFilePath = filelist.get(i).replace("\\", "/");
				int j = serverFilePath.lastIndexOf("/");
				partfilepath = serverFilePath.replaceAll(serverFilePath
						.substring(0, j + 1), "");
				clientFilePath = filePath + dirsep + strdate + dirsep
						+ partfilepath;
				File f = new File(clientFilePath);
				File dir = new File(f.getParent());
				if (!dir.exists()) {
					dir.mkdirs();
				}
				ClientFileTransferUtil.downloadFile(serverFilePath,
						clientFilePath);

			}
			if (filelist.size() > 0) {
				MessageDialog.openMessageDialog(null, "报表下载成功，共生成"
						+ filelist.size() + "个文件");
			} else {
				MessageDialog.openMessageDialog(null, "没有生成文件！");
			}

		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);

		} catch (FileTransferException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.exportFile(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest arg0) {

		return super.retrieve(arg0);
	}

	/**
	 * 根据国库代码查询征收机关代码 国库与征收机关对应关系表
	 */
	private List querytaxoeglist(String sleTreCode) {
		// 查询国库对应征收机关代码用dto
		TsTaxorgDto taxorgdto = new TsTaxorgDto();
		taxorgdto.setStrecode(sleTreCode);// 国库代码
		try {
			taxorgList = commonDataAccessService.findRsByDto(taxorgdto);

			TsTaxorgDto orgdto_1 = new TsTaxorgDto();
			orgdto_1.setSorgcode(loginfo.getSorgcode());// 核算主体
			orgdto_1.setStrecode(sleTreCode);// 国库代码
			orgdto_1.setStaxorgcode(MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS);
			orgdto_1.setStaxorgname("不分征收机关(TBS)");
			orgdto_1.setStaxprop(MsgConstant.MSG_TAXORG_SHARE_PROP);
			taxorgList.add(orgdto_1);

			TsTaxorgDto orgdto0 = new TsTaxorgDto();
			orgdto0.setSorgcode(loginfo.getSorgcode());// 核算主体
			orgdto0.setStrecode(sleTreCode);// 国库代码
			orgdto0.setStaxorgcode(MsgConstant.MSG_TAXORG_SHARE_CLASS);
			orgdto0.setStaxorgname("不分征收机关");
			orgdto0.setStaxprop(MsgConstant.MSG_TAXORG_SHARE_PROP);
			taxorgList.add(orgdto0);

			TsTaxorgDto orgdto1 = new TsTaxorgDto();
			orgdto1.setSorgcode(loginfo.getSorgcode());// 核算主体
			orgdto1.setStrecode(sleTreCode);// 国库代码
			orgdto1.setStaxorgcode(MsgConstant.MSG_TAXORG_NATION_CLASS);
			orgdto1.setStaxorgname("国税");
			orgdto1.setStaxprop(MsgConstant.MSG_TAXORG_NATION_PROP);
			taxorgList.add(orgdto1);

			TsTaxorgDto orgdto2 = new TsTaxorgDto();
			orgdto2.setSorgcode(loginfo.getSorgcode());// 核算主体
			orgdto2.setStrecode(sleTreCode);// 国库代码
			orgdto2.setStaxorgcode(MsgConstant.MSG_TAXORG_PLACE_CLASS);
			orgdto2.setStaxorgname("地税");
			orgdto2.setStaxprop(MsgConstant.MSG_TAXORG_PLACE_PROP);
			taxorgList.add(orgdto2);

			TsTaxorgDto orgdto3 = new TsTaxorgDto();
			orgdto3.setSorgcode(loginfo.getSorgcode());// 核算主体
			orgdto3.setStrecode(sleTreCode);// 国库代码
			orgdto3.setStaxorgcode(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
			orgdto3.setStaxorgname("海关");
			orgdto3.setStaxprop(MsgConstant.MSG_TAXORG_CUSTOM_PROP);
			taxorgList.add(orgdto3);

			TsTaxorgDto orgdto4 = new TsTaxorgDto();
			orgdto4.setSorgcode(loginfo.getSorgcode());// 核算主体
			orgdto4.setStrecode(sleTreCode);// 国库代码
			orgdto4.setStaxorgcode(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
			orgdto4.setStaxorgname("财政");
			orgdto4.setStaxprop(MsgConstant.MSG_TAXORG_FINANCE_PROP);
			taxorgList.add(orgdto4);

			TsTaxorgDto orgdto5 = new TsTaxorgDto();
			orgdto5.setSorgcode(loginfo.getSorgcode());// 核算主体
			orgdto5.setStrecode(sleTreCode);// 国库代码
			orgdto5.setStaxorgcode(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			orgdto5.setStaxorgname("其他");
			orgdto5.setStaxprop(MsgConstant.MSG_TAXORG_OTHER_PROP);
			taxorgList.add(orgdto5);
			//增加合并后的增收机关
			TdTaxorgMergerDto  taxorg = new TdTaxorgMergerDto();
			taxorg.setSbookorgcode(loginfo.getSorgcode());
			List <TdTaxorgMergerDto> taxMergeorgList = commonDataAccessService.findRsByDto(taxorg);
			if (null!=taxMergeorgList && taxMergeorgList.size()>0) {
				HashMap<String,String> map = new HashMap<String, String>();
				for (TdTaxorgMergerDto _dto : taxMergeorgList) {
					 if (!map.containsKey(_dto.getSaftertaxorgcode())) {
						 TsTaxorgDto adddto = new TsTaxorgDto();
						 adddto.setSorgcode(_dto.getSbookorgcode());
						 adddto.setStaxorgcode(_dto.getSaftertaxorgcode());
						 adddto.setStaxorgname(_dto.getStaxorgname());
						 map.put(_dto.getSaftertaxorgcode(), _dto.getStaxorgname());
						 taxorgList.add(adddto);
					}
					
					
				}
			}
			
			
			

		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		// 初始化征收机关代码默认值
		if (taxorgList.size() > 0) {
			taxorgdto.setStrecode(sleTreCode);// 国库代码
			if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(loginfo.getArea())) {
				sleTaxOrgCode = MsgConstant.MSG_TAXORG_SHARE_CLASS_TBS;
			} else {
				sleTaxOrgCode = MsgConstant.MSG_TAXORG_SHARE_CLASS;
			}
		}
		return taxorgList;
	}

	private void init() {
		
		//查询用户查询科目代码条件信息
		TvUsersconditionDto tvUsersconditionDto = new TvUsersconditionDto();
		tvUsersconditionDto.setSorgcode(loginfo.getSorgcode());
		tvUsersconditionDto.setSusercode(loginfo.getSuserCode());
		
		// 查询国库代码用dto
		TsTreasuryDto tredto = new TsTreasuryDto();
		// 中心机构代码
		String centerorg = null;
		try {
			this.sfuncbdgsbtcode = finDataStatDownForYNService.queryCondition(tvUsersconditionDto);
			centerorg = StateConstant.ORG_CENTER_CODE;
			// 中心机构，取得所有国库列表
			if (loginfo.getSorgcode().equals(centerorg)) {
				treList = commonDataAccessService.findRsByDto(tredto);
			}
			// 非中心机构，取得登录机构对应国库
			else {
				tredto = new TsTreasuryDto();
				tredto.setSorgcode(loginfo.getSorgcode());
				treList = commonDataAccessService.findRsByDto(tredto);
			}
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		// 初始化国库代码默认值
		if (treList.size() > 0) {
			sleTreCode = treList.get(0).getStrecode();
		}
		// 初始化征收机关代码
		querytaxoeglist(this.getSleTreCode());
		// 调整期标志
		sleTrimFlag = StateConstant.TRIMSIGN_FLAG_NORMAL;
		// 辖属标志
		sleOfFlag = MsgConstant.RULE_SIGN_SELF;
		// 预算种类
		sleBudKind = MsgConstant.BDG_KIND_IN;
		// 项合计
		sleSumItem = MsgConstant.INPUT_SIGN_NO;
		// 查询日期
		sdate = TimeFacade.getCurrentDateTime();
		sdateend = TimeFacade.getCurrentDateTime();
		//导出范围
		exportarea = StateConstant.SELF_AREA;
	}

	public String getSleTrimFlag() {
		return sleTrimFlag;
	}

	public void setSleTrimFlag(String sleTrimFlag) {
		this.sleTrimFlag = sleTrimFlag;
	}

	public String getSleBudKind() {
		return sleBudKind;
	}

	public void setSleBudKind(String sleBudKind) {
		this.sleBudKind = sleBudKind;
	}

	public String getSleTreCode() {
		return sleTreCode;
	}

	public void setSleTreCode(String sleTreCode) {
		this.sleTreCode = sleTreCode;
		List list = querytaxoeglist(sleTreCode);
		if (list != null && list.size() > 0) {
			taxorgList = list;
		} else {
			taxorgList.clear();
		}
		editor.fireModelChanged();
	}

	public String getSleTaxOrgCode() {
		return sleTaxOrgCode;
	}

	public void setSleTaxOrgCode(String sleTaxOrgCode) {
		this.sleTaxOrgCode = sleTaxOrgCode;
	}

	public String getSleOfFlag() {
		return sleOfFlag;
	}

	public void setSleOfFlag(String sleOfFlag) {
		this.sleOfFlag = sleOfFlag;
	}

	public String getSleSumItem() {
		return sleSumItem;
	}

	public void setSleSumItem(String sleSumItem) {
		this.sleSumItem = sleSumItem;
	}

	public List<TsTreasuryDto> getTreList() {
		return treList;
	}

	public void setTreList(List<TsTreasuryDto> treList) {
		this.treList = treList;
	}

	public List<TsTaxorgDto> getTaxorgList() {
		return taxorgList;
	}

	public void setTaxorgList(List<TsTaxorgDto> taxorgList) {
		this.taxorgList = taxorgList;
	}

	public Date getSdate() {
		return sdate;
	}

	public void setSdate(Date sdate) {
		this.sdate = sdate;
	}
	public String getSfuncbdgsbtcode() {
		return sfuncbdgsbtcode;
	}
	public void setSfuncbdgsbtcode(String sfuncbdgsbtcode) {
		this.sfuncbdgsbtcode = sfuncbdgsbtcode;
	}

	public String getExportarea() {
		return exportarea;
	}

	public void setExportarea(String exportarea) {
		this.exportarea = exportarea;
	}

	public Date getSdateend() {
		return sdateend;
	}

	public void setSdateend(Date sdateend) {
		this.sdateend = sdateend;
	}
	
	
}
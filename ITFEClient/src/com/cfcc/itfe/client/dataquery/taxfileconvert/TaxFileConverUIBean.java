package com.cfcc.itfe.client.dataquery.taxfileconvert;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TpShareDivideDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertassitsignDto;
import com.cfcc.itfe.persistence.dto.TsConvertaxDto;
import com.cfcc.itfe.persistence.dto.TsConvertreaDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.util.TaxFileConverter;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author db2admin
 * @time 13-01-28 16:06:40 子系统: DataQuery 模块:taxFileConvert 组件:TaxFileConverUI
 */
public class TaxFileConverUIBean extends AbstractTaxFileConverUIBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TaxFileConverUIBean.class);
	ITFELoginInfo  loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
	.getDefault().getLoginInfo();
	public TaxFileConverUIBean() {
		super();
		files = new ArrayList();

	}

	/**
	 * Direction: 接口转换 ename: fileConvert 引用方法: viewers: * messages:
	 */
	public String fileConvert(Object o) {
		TaxFileConverter converter = new TaxFileConverter();
		StringBuffer sb = new StringBuffer();
		if (files.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择DS开头的文件\n");
			return null;
		}
		String tmpName = null;
		TsConvertreaDto idto = new TsConvertreaDto();
		TsConvertassitsignDto tmpdto = new TsConvertassitsignDto();
		TsConvertaxDto taxdto = new TsConvertaxDto();
		TsBudgetsubjectDto budgetdto = new TsBudgetsubjectDto();
		idto.setSorgcode(loginfo.getSorgcode());
		tmpdto.setSorgcode(loginfo.getSorgcode());
		taxdto.setSorgcode(loginfo.getSorgcode());
		budgetdto.setSorgcode(loginfo.getSorgcode());
		HashMap<String, String> map = new HashMap<String, String>();
		HashMap<String, String> assinmap = new HashMap<String, String>();
		HashMap<String, String> taxmap = new HashMap<String, String>();
		HashMap<String, TsBudgetsubjectDto> budgetmap = new HashMap<String, TsBudgetsubjectDto>();
		
		try {
			List<TsConvertreaDto> list = commonDataAccessService
					.findRsByDto(idto);
			if (null!=list && list.size()> 0) {
				for (TsConvertreaDto _dto:list) {
					map.put(_dto.getStrecode(), _dto.getStcbstrea());
				}
			}else{
				MessageDialog.openMessageDialog(null,"没有维护国库代码对照关系！");
				return null;
			}
			List<TsConvertassitsignDto> list2 = commonDataAccessService.findRsByDto(tmpdto);
			if (null!=list2 && list2.size()> 0) {
				for (TsConvertassitsignDto _tmpdto:list2) {
					if (!assinmap.containsKey(_tmpdto.getSbudgetsubcode())) {
						assinmap.put(_tmpdto.getSbudgetsubcode()+_tmpdto.getStrecode(), _tmpdto.getStbsassitsign());
					}				
				}
			}else{
				MessageDialog.openMessageDialog(null,  "没有导入辅助标志对照表！");
				return null;
			}
			List<TsConvertaxDto> list3 = commonDataAccessService.findRsByDto(taxdto);
			if (null!=list3 && list3.size()> 0) {
				for (TsConvertaxDto _tmpdto:list3) {
					if (!taxmap.containsKey(_tmpdto.getStaxcode())) {
						taxmap.put(_tmpdto.getStaxcode(), _tmpdto.getStcbstax());
					}				
				}
			}else{
				MessageDialog.openMessageDialog(null,  "没有维护地方横联与TBS征收机关对照表！");
				return null;
			}
			
			List<TsBudgetsubjectDto> list4 = commonDataAccessService.findRsByDto(budgetdto);
			if (null!=list4 && list4.size()> 0) {
				for (TsBudgetsubjectDto _tmpdto:list4) {
					if (!budgetmap.containsKey(_tmpdto.getSsubjectcode())) {
						budgetmap.put(_tmpdto.getSsubjectcode(),_tmpdto);
					}				
				}
			}else{
				MessageDialog.openMessageDialog(null,  "预算科目代码参数数据没有导入！");
				return null;
			}
			
			for (int i = 0; i < files.size(); i++) {
				File tmp = (File) files.get(i);
				tmpName = tmp.getName();
				if (!tmpName.startsWith("DS")) {
					sb.append("不能转换" + tmpName + "\n");
					continue;
				}

				String newFile = converter.convert(tmp,map,assinmap,taxmap,budgetmap);
				sb.append(tmpName + "转换成功,已保存为：" + newFile + "\n");
			}
	
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		MessageDialog.openMessageDialog(null,sb.toString());
		files = new ArrayList();
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);

		return super.fileConvert(o);
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

}
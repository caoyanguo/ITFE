package com.cfcc.itfe.client.dataquery.tvfincustomline;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.dataquery.tvfinincomeonline.ITvFinIncomeonlineService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFinCustomonlineDto;

/**
 * codecomment: 
 * @author zhangliang
 * @time   19-05-06 11:11:10
 * 子系统: DataQuery
 * 模块:TvFinCustomline
 * 组件:TvFinIncomecustom
 */
public class TvFinIncomecustomBean extends AbstractTvFinIncomecustomBean implements IPageDataProvider {

    public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		TvFinIncomecustomBean.log = log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public String getStaxpayname() {
		return staxpayname;
	}

	public void setStaxpayname(String staxpayname) {
		this.staxpayname = staxpayname;
	}

	public List<TvFinCustomonlineDto> getExcheckList() {
		return excheckList;
	}

	public void setExcheckList(List<TvFinCustomonlineDto> excheckList) {
		this.excheckList = excheckList;
	}

	public List<TsConvertfinorgDto> getFinddtolist() {
		return finddtolist;
	}

	public void setFinddtolist(List<TsConvertfinorgDto> finddtolist) {
		this.finddtolist = finddtolist;
	}

	public List<TsTaxorgDto> getQuerydtolist() {
		return querydtolist;
	}

	public void setQuerydtolist(List<TsTaxorgDto> querydtolist) {
		this.querydtolist = querydtolist;
	}

	public List<TsTaxorgDto> getTaxorgList() {
		return taxorgList;
	}

	public void setTaxorgList(List<TsTaxorgDto> taxorgList) {
		this.taxorgList = taxorgList;
	}
	private static Log log = LogFactory.getLog(TvFinIncomecustomBean.class);

	private ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();
	private String staxpayname = null;
	private List<TvFinCustomonlineDto> excheckList;// 选中记录
	private List<TsConvertfinorgDto> finddtolist = null;
	private List<TsTaxorgDto> querydtolist = null;
	// 征收机关代码列表
	private List<TsTaxorgDto> taxorgList;

    public TvFinIncomecustomBean() {
      super();
      dto = new TvFinCustomonlineDto();
      dto.setSapplydate(TimeFacade.getCurrentStringTime());// 申请日期默认当天
	  dto.setSbilldate(TimeFacade.getCurrentStringTime());// 统计日期默认当天
	  excheckList = new ArrayList<TvFinCustomonlineDto>();
	  staxpayname = "";
	  pagingcontext = new PagingContext(this);
	  init();
                  
    }
    private void init() {
		// 查询国库对应征收机关代码用dto
		taxorgList = new ArrayList<TsTaxorgDto>();
		TsTaxorgDto taxorgdto = new TsTaxorgDto();
		// taxorgdto.setStrecode(sleTreCode);// 国库代码
		taxorgdto.setSorgcode(loginfo.getSorgcode());
		try {
			TsTaxorgDto orgdto0 = new TsTaxorgDto();
			orgdto0.setSorgcode(loginfo.getSorgcode());// 核算主体
			orgdto0.setStaxorgcode(MsgConstant.MSG_TAXORG_SHARE_CLASS);
			orgdto0.setStaxorgname("不分征收机关");
			orgdto0.setStaxprop(MsgConstant.MSG_TAXORG_SHARE_PROP);
			taxorgList.add(orgdto0);

			TsTaxorgDto orgdto1 = new TsTaxorgDto();
			orgdto1.setSorgcode(loginfo.getSorgcode());// 核算主体
			orgdto1.setStaxorgcode(MsgConstant.MSG_TAXORG_NATION_CLASS);
			orgdto1.setStaxorgname("国税");
			orgdto1.setStaxprop(MsgConstant.MSG_TAXORG_NATION_PROP);
			taxorgList.add(orgdto1);

			TsTaxorgDto orgdto2 = new TsTaxorgDto();
			orgdto2.setSorgcode(loginfo.getSorgcode());// 核算主体
			orgdto2.setStaxorgcode(MsgConstant.MSG_TAXORG_PLACE_CLASS);
			orgdto2.setStaxorgname("地税");
			orgdto2.setStaxprop(MsgConstant.MSG_TAXORG_PLACE_PROP);
			taxorgList.add(orgdto2);

			TsTaxorgDto orgdto3 = new TsTaxorgDto();
			orgdto3.setSorgcode(loginfo.getSorgcode());// 核算主体
			orgdto3.setStaxorgcode(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
			orgdto3.setStaxorgname("海关");
			orgdto3.setStaxprop(MsgConstant.MSG_TAXORG_CUSTOM_PROP);
			taxorgList.add(orgdto3);

			TsTaxorgDto orgdto4 = new TsTaxorgDto();
			orgdto4.setSorgcode(loginfo.getSorgcode());// 核算主体
			orgdto4.setStaxorgcode(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
			orgdto4.setStaxorgname("财政");
			orgdto4.setStaxprop(MsgConstant.MSG_TAXORG_FINANCE_PROP);
			taxorgList.add(orgdto4);

			TsTaxorgDto orgdto5 = new TsTaxorgDto();
			orgdto5.setSorgcode(loginfo.getSorgcode());// 核算主体
			orgdto5.setStaxorgcode(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			orgdto5.setStaxorgname("其他");
			orgdto5.setStaxprop(MsgConstant.MSG_TAXORG_OTHER_PROP);
			taxorgList.add(orgdto5);
			taxorgList.addAll(commonDataAccessService.findRsByDto(taxorgdto));
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		// 初始化征收机关代码默认值
		if (taxorgList.size() > 0) {
			dto.setStaxorgcode(MsgConstant.MSG_TAXORG_SHARE_CLASS);
		}
	}
    /**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		return super.singleSelect(o);
	}

	/**
	 * Direction: 查询 ename: query 引用方法: viewers: 电子税票信息查询结果 messages:
	 */
	public String query(Object o) {

		// 清空选中权限
		excheckList = new ArrayList<TvFinCustomonlineDto>();
		// 返回页面
		String returnpage = null;

		// 中心机构代码
		String centerorg = null;
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}

		// 判断当前登录查询国库代码、财政代码是否在核算主体中
		TsConvertfinorgDto finddto = new TsConvertfinorgDto();

		if (!loginfo.getSorgcode().equals(centerorg)) {

			finddto.setSorgcode(loginfo.getSorgcode()); // 核算主体代码
			if (dto.getSfinorgcode() != null && !"".equals(dto.getSfinorgcode())) {
				finddto.setSfinorgcode(dto.getSfinorgcode()); // 财政代码
			}
			if (dto.getStrecode() != null && !"".equals(dto.getStrecode())) {
				finddto.setStrecode(dto.getStrecode()); // 国库主体代码
			}
		}
		
			try {
			finddtolist = commonDataAccessService.findRsByDtocheckUR(finddto,
					"1");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		if (null == finddtolist || finddtolist.size() == 0) {
			MessageDialog.openMessageDialog(null, " 权限不足！请填写正确的财政代码或者国库代码！");
			return null;
		}

		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		if (pageResponse == null || pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, " 查询无记录！");
			return null;
		}
		return super.query(o);

	}

	/**
	 * Direction: 返回 ename: goBack 引用方法: viewers: 电子税票信息查询条件 messages:
	 */
	public String goBack(Object o) {
		return super.goBack(o);
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
			TvFinCustomonlineDto tmpsearchDto = (TvFinCustomonlineDto) dto
					.clone();
			String strwhere = "1=1 ";

			// 财政代码为空，国库代码为空，查全局
			if ((dto.getStrecode() == null || "".equals(dto.getStrecode()))
					&& (dto.getSfinorgcode() == null || "".equals(dto
							.getSfinorgcode())) && finddtolist != null
					&& finddtolist.size() != 0) {

				strwhere += " and ( S_TRECODE='"
						+ finddtolist.get(0).getStrecode() + "' ";
				for (int i = 1; i < finddtolist.size(); i++) {
					strwhere += " or S_TRECODE= '"
							+ finddtolist.get(i).getStrecode() + "' ";
				}
				strwhere += " ) ";
			}

			if (staxpayname != null && !"".equals(staxpayname)) {
				strwhere += " and S_TAXPAYNAME like '%" + staxpayname.trim()
						+ "%' ";
			}
			if(tmpsearchDto.getSapplydate()!=null&&!"".equals(tmpsearchDto.getSapplydate()))
				strwhere += " And S_APPLYDATE >='" + tmpsearchDto.getSapplydate()+"' ";
			if(tmpsearchDto.getSbilldate()!=null&&!"".equals(tmpsearchDto.getSbilldate()))
				strwhere+= " and S_APPLYDATE <='" + tmpsearchDto.getSbilldate() + "' ";
			tmpsearchDto.setSbilldate(null);
			tmpsearchDto.setSapplydate(null);
			
			// /////////////////////////////////////////////////////////////////////////
			// 如果是大类则根据大类的征收机关性质查找
			boolean bool = false;
			TsTaxorgDto taxorgdto = new TsTaxorgDto();
			taxorgdto.setSorgcode(loginfo.getSorgcode());
			if (MsgConstant.MSG_TAXORG_SHARE_CLASS.equals(dto.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				bool = true;
			} else if (MsgConstant.MSG_TAXORG_NATION_CLASS.equals(dto
					.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				taxorgdto.setStaxprop(MsgConstant.MSG_TAXORG_NATION_PROP);
				bool = true;
			} else if (MsgConstant.MSG_TAXORG_PLACE_CLASS.equals(dto
					.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				taxorgdto.setStaxprop(MsgConstant.MSG_TAXORG_PLACE_PROP);
				bool = true;
			} else if (MsgConstant.MSG_TAXORG_CUSTOM_CLASS.equals(dto
					.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				taxorgdto.setStaxprop(MsgConstant.MSG_TAXORG_CUSTOM_PROP);
				bool = true;
			} else if (MsgConstant.MSG_TAXORG_FINANCE_CLASS.equals(dto
					.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				taxorgdto.setStaxprop(MsgConstant.MSG_TAXORG_FINANCE_PROP);
				bool = true;
			} else if (MsgConstant.MSG_TAXORG_OTHER_CLASS.equals(dto
					.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				taxorgdto.setStaxprop(MsgConstant.MSG_TAXORG_OTHER_PROP);
				bool = true;
			}
			
				StringBuffer sql = new StringBuffer();
				if (bool) {
					List<TsTaxorgDto> list = commonDataAccessService.findRsByDto(taxorgdto);
					if(null != list && list.size() > 0){
						sql.append(" and S_TAXORGCODE in (");
						for(TsTaxorgDto tmpdto : list ){
							sql.append("'" + tmpdto.getStaxorgcode() + "',");
						}
						sql.append("'') ");
						strwhere = strwhere + sql.toString();
					}
				}
				// /////////////////////////////////////////////////////////////////////////
			
			
			
			
			
			return commonDataAccessService.findRsByDtoWithWherePaging(
					tmpsearchDto, pageRequest, strwhere, "", tmpsearchDto
							.tableName());

		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	/**
	 * Direction: 校验纳税人与国库对应关系 ename: checkTaxPayCodeOrTrecode 引用方法: viewers: *
	 * messages:
	 */
	public String checkTaxPayCodeOrTrecode(Object o) {

		if (null == this.excheckList || excheckList.size() == 0) {
			MessageDialog.openMessageDialog(null, " 请选择要操作的记录！");
			return null;
		}
		String msg = null;
		try {
			msg = tvFinIncomeonlineService
					.checkTaxPayCodeOrTrecode(excheckList);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		if ("".equals(msg)) {
			MessageDialog.openMessageDialog(null, " 校验成功！");
		} else {
			String fPath = "C:/client/checkmsg/" + loginfo.getSorgcode() + "_"
					+ dto.getStrecode() + "_" + loginfo.getSuserCode() + "_"
					+ DateUtil.date2String3(new java.util.Date()) + ".txt";
			try {
				// 文件回写
				FileUtil.getInstance().writeFile(fPath, msg);
			} catch (Throwable e) {
				MessageDialog.openMessageDialog(null, " 校验信息写入文件操作失败！");
				return null;
			}
			MessageDialog.openMessageDialog(null, " 校验失败！失败信息已保存到：" + fPath);
		}
		return super.checkTaxPayCodeOrTrecode(o);
	}

	/**
	 * Direction: 手动处理共享分成 ename: makeDivide 引用方法: viewers: * messages:
	 */
	public String makeDivide(Object o) {

		if (null == this.excheckList || excheckList.size() == 0) {
			MessageDialog.openMessageDialog(null, " 请选择要操作的记录！");
			return null;
		}

		String msg = null;
		try {
			msg = tvFinIncomeonlineService.makeDivide(excheckList, dto
					.getSapplydate());
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		if ("".equals(msg)) {
			MessageDialog.openMessageDialog(null, " 手工处理成功！");
		} else {
			MessageDialog.openMessageDialog(null, " 手工处理有未执行信息！原因：" + msg);
		}

		return super.makeDivide(o);
	}

	/**
	 * Direction: 导出 ename: exportTable 引用方法: viewers: * messages:
	 */
	public String exportTable(Object o) {

		PageResponse page = this.pagingcontext.getPage();
		if (page == null || page.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, "列表无数据,无法执行导出，请先做查询！");
			return null;
		}
		// 选择保存路径
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		List<TvFinCustomonlineDto> filelist = new ArrayList<TvFinCustomonlineDto>();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
			return "";
		}
		String fileName = filePath + File.separator + "3178海关电子税票("
				+ dto.getSapplydate() + "-" + dto.getSbilldate() + ").CSV";

		try {
			TvFinCustomonlineDto tmpsearchDto = (TvFinCustomonlineDto) dto
					.clone();
			String strwhere = "";

			// 财政代码为空，国库代码为空，查全局
			if ((dto.getStrecode() == null || "".equals(dto.getStrecode()))
					&& (dto.getSfinorgcode() == null || "".equals(dto
							.getSfinorgcode())) && finddtolist != null
					&& finddtolist.size() != 0) {

				strwhere += " and ( S_TRECODE='"
						+ finddtolist.get(0).getStrecode() + "' ";
				for (int i = 1; i < finddtolist.size(); i++) {
					strwhere += " or S_TRECODE= '"
							+ finddtolist.get(i).getStrecode() + "' ";
				}
				strwhere += " ) ";
			}

			if (staxpayname != null && !"".equals(staxpayname)) {
				strwhere += " and S_TAXPAYNAME like '%" + staxpayname.trim()
						+ "%' ";
			}

			strwhere += " And S_ext2 >='" + tmpsearchDto.getSapplydate()
					+ "' and S_ext2 <='" + tmpsearchDto.getSbilldate() + "'";
			tmpsearchDto.setSbilldate(null);
			tmpsearchDto.setSapplydate(null);
			// /////////////////////////////////////////////////////////////////////////
			// 如果是大类则根据大类的征收机关性质查找
			boolean bool = false;
			TsTaxorgDto taxorgdto = new TsTaxorgDto();
			taxorgdto.setSorgcode(loginfo.getSorgcode());
			if (MsgConstant.MSG_TAXORG_SHARE_CLASS.equals(dto.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				bool = true;
			} else if (MsgConstant.MSG_TAXORG_NATION_CLASS.equals(dto
					.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				taxorgdto.setStaxprop(MsgConstant.MSG_TAXORG_NATION_PROP);
				bool = true;
			} else if (MsgConstant.MSG_TAXORG_PLACE_CLASS.equals(dto
					.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				taxorgdto.setStaxprop(MsgConstant.MSG_TAXORG_PLACE_PROP);
				bool = true;
			} else if (MsgConstant.MSG_TAXORG_CUSTOM_CLASS.equals(dto
					.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				taxorgdto.setStaxprop(MsgConstant.MSG_TAXORG_CUSTOM_PROP);
				bool = true;
			} else if (MsgConstant.MSG_TAXORG_FINANCE_CLASS.equals(dto
					.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				taxorgdto.setStaxprop(MsgConstant.MSG_TAXORG_FINANCE_PROP);
				bool = true;
			} else if (MsgConstant.MSG_TAXORG_OTHER_CLASS.equals(dto
					.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				taxorgdto.setStaxprop(MsgConstant.MSG_TAXORG_OTHER_PROP);
				bool = true;
			}
			
				StringBuffer sql = new StringBuffer();
				if (bool) {
					List<TsTaxorgDto> list = commonDataAccessService.findRsByDto(taxorgdto);
					if(null != list && list.size() > 0){
						sql.append(" and S_TAXORGCODE in (");
						for(TsTaxorgDto tmpdto : list ){
							sql.append("'" + tmpdto.getStaxorgcode() + "',");
						}
						sql.append("'') ");
						strwhere = strwhere + sql.toString();
					}
				}
				// /////////////////////////////////////////////////////////////////////////
			
			filelist = tvFinIncomeonlineService.exportTable(tmpsearchDto, strwhere);
			exportTableForWhere(filelist, fileName, ",");
			MessageDialog.openMessageDialog(null, "数据导出成功\n" + fileName);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return "";
	}

	/**
	 * 按条件导出数据
	 * 
	 * @param list
	 * @param filename
	 * @return
	 * @throws FileOperateException
	 */
	public String exportTableForWhere(List<TvFinCustomonlineDto> templist,
			String fileName, String splitSign) throws FileOperateException {
		String sql = "财政机关代码,申请日期,包流水号,国库代码,国库名称,征收机关代码,付款行行号,交易流水号,原报文编号,交易金额,付款人开户行行号,付款开户行名称,缴款单位名称,付款人账号,税票号码,开票日期,纳税人编号,纳税人名称,预算种类,调整期标志,企业代码,企业名称,企业类型,预算科目代码,预算科目名称,限缴日期,税种代码,税种名称,预算级次,预算级次名称,税款所属日期起,税款所属日期止,辅助标志,税款类型,账务日期,处理状态,备注,备注1,备注2,系统更新时间,业务流水号	";
		StringBuffer filebuf = new StringBuffer(sql + "\r\n");
		File file=new File(fileName);
		if(file.exists()){
			file.delete();
		}
		int count = 0;
		for (TvFinCustomonlineDto _dto : templist) {
			filebuf.append(_dto.getSfinorgcode());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSapplydate());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSpackno());
			filebuf.append(splitSign);
			filebuf.append(_dto.getStrecode());
			filebuf.append(splitSign);
			filebuf.append(_dto.getStrename());
			filebuf.append(splitSign);

			filebuf.append(_dto.getStaxorgcode());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSpaybnkno());
			filebuf.append(splitSign);
			filebuf.append(_dto.getStrano());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSorimsgno());
			filebuf.append(splitSign);
			filebuf.append(_dto.getNtraamt());
			filebuf.append(splitSign);

			filebuf.append(_dto.getSpayeropbkno());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSpayeropbkname());
			filebuf.append(splitSign);
			filebuf.append(_dto.getShandorgname());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSpayacct());
			filebuf.append(splitSign);
			filebuf.append(_dto.getStaxvouno());
			filebuf.append(splitSign);

			filebuf.append(_dto.getSbilldate());
			filebuf.append(splitSign);
			filebuf.append(_dto.getStaxpaycode());
			filebuf.append(splitSign);
			filebuf.append(_dto.getStaxpayname());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSbudgettype());
			filebuf.append(splitSign);
			filebuf.append(_dto.getStrimsign());
			filebuf.append(splitSign);

			filebuf.append(_dto.getStaxpaycode());
			filebuf.append(splitSign);
			filebuf.append(_dto.getStaxpayname());
			filebuf.append(splitSign);
			filebuf.append(_dto.getScorptype());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSbudgetsubjectcode());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSbudgetsubjectname());
			filebuf.append(splitSign);

			filebuf.append(_dto.getSlimitdate());
			filebuf.append(splitSign);
			filebuf.append(_dto.getStaxtypecode());
			filebuf.append(splitSign);
			filebuf.append(_dto.getStaxtypename());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSbudgetlevelcode());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSbudgetlevelname());
			filebuf.append(splitSign);

			filebuf.append(_dto.getStaxstartdate());
			filebuf.append(splitSign);
			filebuf.append(_dto.getStaxenddate());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSvicesign());
			filebuf.append(splitSign);
			filebuf.append(_dto.getStaxtype());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSbilldate());
			filebuf.append(splitSign);

			filebuf.append(_dto.getSopstat());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSremark());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSremark1());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSremark2());
			filebuf.append(splitSign);

			filebuf.append(_dto.getSext1());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSseq());
			filebuf.append(splitSign);
			filebuf.append("\r\n");
			count ++ ;
			if(count > 5000){
				FileUtil.getInstance().writeFile(fileName, filebuf.toString(),true);
				filebuf = new StringBuffer();
				count = 0;
			}

		}
		FileUtil.getInstance().writeFile(fileName, filebuf.toString(),true);
		return staxpayname;

	}

	/**
	 * Direction: 全选/反选 ename: selectAllOrNone 引用方法: viewers: * messages:
	 */
	public String selectAllOrNone(Object o) {
		if (this.pagingcontext == null) {
			return super.selectAllOrNone(o);
		}
		PageResponse page = this.pagingcontext.getPage();
		if (page == null || page.getTotalCount() == 0) {
			return super.selectAllOrNone(o);
		}
		List<TvFinCustomonlineDto> templist = page.getData();
		if (templist != null && this.excheckList != null) {
			if (excheckList.size() != 0 && excheckList.containsAll(templist)) {
				excheckList.removeAll(templist);
			} else {
				for (int i = 0; i < templist.size(); i++) {
					if (excheckList.contains(templist.get(i))) {
						excheckList.set(excheckList.indexOf(templist.get(i)),
								templist.get(i));
					} else {
						excheckList.add(i, templist.get(i));
					}
				}
			}
		}
		this.editor.fireModelChanged();
		return super.selectAllOrNone(o);
	}

}
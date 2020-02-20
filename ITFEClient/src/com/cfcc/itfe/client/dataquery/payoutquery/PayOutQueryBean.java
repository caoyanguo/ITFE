package com.cfcc.itfe.client.dataquery.payoutquery;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.page.HisMainPayOutBean;
import com.cfcc.itfe.client.common.page.HisSubPayOutBean;
import com.cfcc.itfe.client.common.page.MainPayOutBean;
import com.cfcc.itfe.client.common.page.SubPayOutBean;
import com.cfcc.itfe.client.dialog.BursarAffirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.client.dialog.ModifySubcodeDialogFacade;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsFinmovepaysubDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayOutReportDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.pk.TsFinmovepaysubPK;
import com.cfcc.itfe.security.SM3Process;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.service.filetransfer.support.ClientFileTransferUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author zhouchuan
 * @time 10-02-04 11:39:18 子系统: DataQuery 模块:payOutQuery 组件:PayOutQuery
 */
public class PayOutQueryBean extends AbstractPayOutQueryBean implements
		IPageDataProvider {
	String startdate = TimeFacade.getCurrentStringTime();
	String enddate = TimeFacade.getCurrentStringTime();
	int pici = 0;
	private TvPayoutmsgmainDto finddto;
	private TvPayoutmsgmainDto operdto;
	private HtvPayoutmsgmainDto hisoperdto;
	private TvPayoutmsgmainDto checkdto;
	private TvFilepackagerefDto msgdto;
	private List<TvFilepackagerefDto> sendmsglist;
	private ITFELoginInfo loginfo;
	private String selectedtable;
	private List statelist;
	private List statelist2;
	private String scommitdatestart;
	private String scommitdateend;
	private String expfunccode;
	private String payamt;
	// 在主信息列表选择用list
	private List selectedlist = new ArrayList();
	// 在主信息列表选择用list
	private List<HtvPayoutmsgmainDto> hselectedlist = new ArrayList<HtvPayoutmsgmainDto>();

	/** 属性列表 */
	MainPayOutBean mainPayOutBean = null;
	SubPayOutBean subPayOutBean = null;
	HisMainPayOutBean hisMainPayOutBean = null;
	HisSubPayOutBean hisSubPayOutBean = null;
	private List reportRs = null;
	private Map reportmap = new HashMap();
	private String reportPath = "com/cfcc/itfe/client/ireport/payOutReport.jasper";
	
	private List statusList;
	private String realValue;

	public PayOutQueryBean() {
		super();
		finddto = new TvPayoutmsgmainDto();
		operdto = new TvPayoutmsgmainDto();
		checkdto = new TvPayoutmsgmainDto();
		modifyDto = new TvPayoutmsgsubDto();
		selectedtable = "0";
		initStatelist();
		reportRs = new ArrayList<TvPayOutReportDto>();
		setMainPayOutBean(new MainPayOutBean(payoutService, finddto));
		setSubPayOutBean(new SubPayOutBean(payoutService, finddto));
		setHisMainPayOutBean(new HisMainPayOutBean(payoutService,
				new HtvPayoutmsgmainDto()));
		setHisSubPayOutBean(new HisSubPayOutBean(payoutService,
				new HtvPayoutmsgmainDto()));
		sendmsglist = new ArrayList<TvFilepackagerefDto>();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		finddto.setSorgcode(loginfo.getSorgcode());
		scommitdatestart = TimeFacade.getCurrentStringTime();
		scommitdateend = TimeFacade.getCurrentStringTime();
	}

	/**
	 * Direction: 退库对账单打印 ename: queryPrint 引用方法: viewers: 实拨资金对账单 messages:
	 */
	public String queryPrint(Object o) {

		try {
			finddto.setSorgcode(loginfo.getSorgcode());
			StringBuffer where = null;
			if(scommitdatestart!=null&&!"".equals(scommitdatestart))
			{
				where = new StringBuffer("");
				where.append(" s_commitdate >='"+scommitdatestart+"'");
			}
			if(scommitdateend!=null&&!"".equals(scommitdateend))
			{
				if(where==null)
				{
					where = new StringBuffer();
					where.append(" s_commitdate <='"+scommitdateend+"'");
				}else
				{
					where.append(" and s_commitdate <='"+scommitdateend+"'");
				}
			}
			if(where!=null)
				finddto.setSaddword(where.toString());
			else
				finddto.setSaddword(null);
			reportRs = payoutService.findPayOutByPrint(finddto, selectedtable);
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return super.rebackSearchList(o);
		}
		if (null == reportRs || reportRs.size() == 0) {
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录!");
			return super.rebackSearchList(o);
		}
		reportmap.put("title", "实拨资金对账单");
		reportmap.put("printdate", new SimpleDateFormat("yyyy年MM月dd日")
				.format(new java.util.Date()));
		reportmap.put("worker", loginfo.getSuserName());
		return super.queryPrint(o);
	}

	/**
	 * Direction: 查询列表事件 name: searchList 引用方法: viewers: * messages:
	 */
	public String searchList(Object o) {
		PageRequest mainpageRequest = new PageRequest();
		PageResponse response = null;
		StringBuffer where = null;
		if(scommitdatestart!=null&&!"".equals(scommitdatestart))
		{
			where = new StringBuffer("");
			where.append(" s_commitdate >='"+scommitdatestart+"'");
		}
		if(scommitdateend!=null&&!"".equals(scommitdateend))
		{
			if(where==null)
			{
				where = new StringBuffer();
				where.append(" s_commitdate <='"+scommitdateend+"'");
			}else
			{
				where.append(" and s_commitdate <='"+scommitdateend+"'");
			}
		}
		if(where!=null)
			finddto.setSaddword(where.toString());
		else
			finddto.setSaddword(null);
		if (selectedtable.equals("0")) {
			this.getMainPayOutBean().setMaindto(finddto);
			this.getMainPayOutBean().setExpfunccode(expfunccode);
			this.getMainPayOutBean().setPayamt(payamt);
			response = this.getMainPayOutBean().retrieve(mainpageRequest);
		} else if (selectedtable.equals("1")) {
			HtvPayoutmsgmainDto htvdto = new HtvPayoutmsgmainDto();
			htvdto.setStrecode(finddto.getStrecode());
			htvdto.setScommitdate(finddto.getScommitdate());
			htvdto.setSpackageno(finddto.getSpackageno());
			htvdto.setSpayunit(finddto.getSpayunit());
			htvdto.setSpayeebankno(finddto.getSpayeebankno());
			htvdto.setSdealno(finddto.getSdealno());
			htvdto.setStaxticketno(finddto.getStaxticketno());
			htvdto.setSgenticketdate(finddto.getSgenticketdate());
			htvdto.setNmoney(finddto.getNmoney());
			htvdto.setSpayeracct(finddto.getSpayeracct());
			htvdto.setSrecacct(finddto.getSrecacct());
			htvdto.setSrecbankno(finddto.getSrecbankno());
			htvdto.setStrimflag(finddto.getStrimflag());
			htvdto.setSbudgetunitcode(finddto.getSbudgetunitcode());
			htvdto.setSofyear(finddto.getSofyear());
			htvdto.setSbudgettype(finddto.getSbudgettype());
			htvdto.setSstatus(finddto.getSstatus());
			htvdto.setSfilename(finddto.getSfilename());
			htvdto.setSaddword(finddto.getSaddword());
			this.getHisMainPayOutBean().setMaindto(htvdto);
			this.getHisMainPayOutBean().setExpfunccode(expfunccode);
			this.getHisMainPayOutBean().setPayamt(payamt);
			response = this.getHisMainPayOutBean().retrieve(mainpageRequest);
			if (response.getTotalCount() <= 0) {
				MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录!");
				return this.rebackSearch(o);
			}

			editor.fireModelChanged();
			return "实拨资金信息列表(历史表)";
		}

		if (response.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录!");
			return this.rebackSearch(o);
		}

		editor.fireModelChanged();
		operdto = new TvPayoutmsgmainDto();
		return super.searchList(o);
	}

	/**
	 * Direction: 返回查询界面 ename: rebackSearch 引用方法: viewers: * messages:
	 */
	public String rebackSearch(Object o) {
		// finddto=new TvPayoutmsgmainDto();
		setSubPayOutBean(new SubPayOutBean(payoutService, new TvPayoutmsgmainDto()));
		setHisSubPayOutBean(new HisSubPayOutBean(payoutService,new HtvPayoutmsgmainDto()));
		return super.rebackSearch(o);
	}

	/**
	 * Direction: 主信息单击事件 ename: singleclickMain 引用方法: viewers: * messages:
	 */
	public String singleclickMain(Object o) {
		
		return super.singleclickMain(o);
	}

	/**
	 * Direction: 主信息双击事件 ename: doubleclickMain 引用方法: viewers: 实拨资金修改界面
	 * messages:
	 */
	public String doubleclickMain(Object o) {
		PageRequest subpageRequest = new PageRequest();

		if (selectedtable.equals("0")) {
			operdto = (TvPayoutmsgmainDto) o;
			this.getSubPayOutBean().setMaindto(operdto);
			this.getSubPayOutBean().setExpfunccode(expfunccode);
			this.getSubPayOutBean().setPayamt(payamt);
			this.getSubPayOutBean().retrieve(subpageRequest);
		} else if (selectedtable.equals("1")) {
			hisoperdto = (HtvPayoutmsgmainDto) o;
			this.getHisSubPayOutBean().setMaindto(hisoperdto);
			this.getHisSubPayOutBean().setExpfunccode(expfunccode);
			this.getHisSubPayOutBean().setPayamt(payamt);
			this.getHisSubPayOutBean().retrieve(subpageRequest);
		}

		editor.fireModelChanged();
		return super.doubleclickMain(o);
	}
	
	
	/**
	 * Direction: 明细信息双击事件 ename: doubleclickSub 引用方法: 
	 * messages:
	 */
	public String doubleclickSub(Object o) {
		boolean flag=false;
		if (selectedtable.equals("0")) {
			TvPayoutmsgsubDto operdto = (TvPayoutmsgsubDto) o;
			if(checkParam(operdto)==null || ((TsFinmovepaysubDto)checkParam(operdto)).getSorgcode()==null){
				MessageDialog.openMessageDialog(null, "当前机构未维护要修改的预算科目代码参数，请维护后再修改！");
				return null;
			}
			flag=ModifySubcodeDialogFacade.open(operdto, "0");
		} else if (selectedtable.equals("1")) {
			HtvPayoutmsgsubDto hisoperdto = (HtvPayoutmsgsubDto) o;
			if(checkParam(hisoperdto)==null || ((TsFinmovepaysubDto)checkParam(hisoperdto)).getSorgcode()==null){
				MessageDialog.openMessageDialog(null, "当前机构未维护要修改的预算科目代码参数，请维护后再修改！");
				return null;
			}
			flag=ModifySubcodeDialogFacade.open(hisoperdto, "1");
		}
		if(flag){
			MessageDialog.openMessageDialog(null, "科目代码修改成功！");
		}
		return super.doubleclickSub(o);
	}
	
	@Override
	public String detailSingleSelect(Object o) {
		if (selectedtable.equals("0")) {
			modifyDto = (TvPayoutmsgsubDto) o;
			realValue = modifyDto.getSstatus();
			if(modifyDto.getSstatus() != null && modifyDto.getSstatus().equals("80000")){
				modifyDto.setSxpayamt(modifyDto.getNmoney());
			}else if(modifyDto.getSstatus() != null && modifyDto.getSstatus().equals("80001")){
				modifyDto.setSxpayamt(new BigDecimal("0.00"));
			}
		}else if (selectedtable.equals("1")) {
			
		}
		return super.detailSingleSelect(o);
	}
	
	@Override
	public String modifyDetailSave(Object o) {
		try {
			commonDataAccessService.updateData(modifyDto);
			MessageDialog.openMessageDialog(null, "修改成功！");
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.modifyDetailSave(o);
	}
	
	@Override
	public String toDetail(Object o) {
		if(o instanceof HtvPayoutmsgsubDto){
			MessageDialog.openMessageDialog(null, "历史明细数据不允许修改！");
			return null;
		}
		if(modifyDto == null || modifyDto.getSbizno() == null){
			MessageDialog.openMessageDialog(null, "请选择要修改的明细记录！");
			return null;
		}
		return super.toDetail(o);
	}
	
	@Override
	public String backToList(Object o) {
		modifyDto = new TvPayoutmsgsubDto();
		return super.backToList(o);
	}
	
	public IDto checkParam(IDto dto){
		TsFinmovepaysubPK paramPk = new TsFinmovepaysubPK();
		if(dto instanceof TvPayoutmsgsubDto){
			TvPayoutmsgsubDto tmpdto = (TvPayoutmsgsubDto)dto;
			paramPk.setSorgcode(loginfo.getSorgcode());
			paramPk.setSsubjectcode(tmpdto.getSfunsubjectcode());
		}else{
			HtvPayoutmsgsubDto tmpdto = (HtvPayoutmsgsubDto)dto;
			paramPk.setSorgcode(loginfo.getSorgcode());
			paramPk.setSsubjectcode(tmpdto.getSfunsubjectcode());
		}
		try {
			return commonDataAccessService.find(paramPk);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return null;
	}
	

	/**
	 * Direction: 修改保存 ename: modifysave 引用方法: viewers: * messages:
	 */
	public String modifysave(Object o) {

		if (datacheck(operdto)) {
			return null;
		}
		try {
			payoutService.saveInfo(operdto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifysave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		operdto = new TvPayoutmsgmainDto();
		return super.rebackSearchList(o);
	}

	/**
	 * Direction: 返回列表事件 ename: rebackSearchList 引用方法: viewers: 实拨资金信息列表
	 * messages:
	 */
	public String rebackSearchList(Object o) {

		operdto = new TvPayoutmsgmainDto();
		hisoperdto = new HtvPayoutmsgmainDto();
		this.reportmap = new HashMap();
		this.reportRs = new ArrayList();
		if (selectedtable.equals("0")) {
			return "实拨资金信息列表";
		} else if (selectedtable.equals("1")) {
			return "实拨资金信息列表(历史表)";
		}
		return super.rebackSearchList(o);
	}

	/**
	 * Direction: 更新失败 ename: updateFail 引用方法: viewers: * messages:
	 */
	public String updateFail(Object o) {
		if (selectedlist.size() == 0 || selectedlist == null) {
			MessageDialog.openMessageDialog(null, "请选中更新状态的记录！");
			return null;
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定要设置选中的记录为失败吗？")) {
			return "";
		}
		// 打开授权窗口
		String msg = "需要主管授权才能更新状态为失败！";
		if (!BursarAffirmDialogFacade.open(msg)) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			return null;
		}
		;
		for (int i = 0; i < selectedlist.size(); i++) {
			checkdto = (TvPayoutmsgmainDto) selectedlist.get(i);
			checkdto.setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL);
			try {
				commonDataAccessService.updateData(checkdto);
			} catch (ITFEBizException e) {
				MessageDialog.openErrorDialog(null, e);
			}
		}
		editor.fireModelChanged();
		selectedlist.clear();
		MessageDialog.openMessageDialog(null, "更新状态成功！");
		return super.updateFail(o);
	}

	/**
	 * Direction: 更新成功 ename: updateSuccess 引用方法: viewers: * messages:
	 */
	public String updateSuccess(Object o) {
		if (selectedlist.size() == 0 || selectedlist == null) {
			MessageDialog.openMessageDialog(null, "请选中更新状态的记录！");
			return null;
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定要设置选中的记录为成功吗？")) {
			return "";
		}
		// 打开授权窗口
		String msg = "需要主管授权才能更新状态为成功！";
		if (!BursarAffirmDialogFacade.open(msg)) {
			DisplayCursor.setCursor(SWT.CURSOR_ARROW);
			return null;
		}
		;
		for (int i = 0; i < selectedlist.size(); i++) {
			checkdto = (TvPayoutmsgmainDto) selectedlist.get(i);
			checkdto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
			try {
				commonDataAccessService.updateData(checkdto);
			} catch (ITFEBizException e) {
				MessageDialog.openErrorDialog(null, e);
			}
		}
		editor.fireModelChanged();
		selectedlist.clear();
		MessageDialog.openMessageDialog(null, "更新状态成功！");
		return super.updateSuccess(o);
	}

	/**
	 * Direction: 全选/反选 ename: selectAllOrNone 引用方法: viewers: * messages:
	 */
	public String selectAllOrNone(Object o) {
		if (this.mainPayOutBean.getMaintablepage() == null) {
			return super.selectAllOrNone(o);
		}
		PageResponse page = this.mainPayOutBean.getMaintablepage().getPage();
		if (page == null || page.getTotalCount() == 0) {
			return super.selectAllOrNone(o);
		}
		List<TvPayoutmsgmainDto> templist = page.getData();
		if (templist != null && this.selectedlist != null) {
			if (selectedlist.size() != 0 && selectedlist.containsAll(templist)) {
				selectedlist.removeAll(templist);
			} else {
				for (int i = 0; i < templist.size(); i++) {
					if (selectedlist.contains(templist.get(i))) {
						selectedlist.set(selectedlist.indexOf(templist.get(i)),
								templist.get(i));
					} else {
						selectedlist.add(i, templist.get(i));
					}
				}
			}
		}
		this.editor.fireModelChanged();
		return super.selectAllOrNone(o);
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

	private void initStatelist() {
		this.statelist = new ArrayList<TdEnumvalueDto>();

		TdEnumvalueDto valuedto3 = new TdEnumvalueDto();
		valuedto3.setStypecode("处理中");
		valuedto3.setSvalue(DealCodeConstants.DEALCODE_ITFE_DEALING);
		this.statelist.add(valuedto3);

		TdEnumvalueDto valuedto4 = new TdEnumvalueDto();
		valuedto4.setStypecode("已收妥");
		valuedto4.setSvalue(DealCodeConstants.DEALCODE_ITFE_RECEIVER);
		this.statelist.add(valuedto4);

		TdEnumvalueDto valuedto1 = new TdEnumvalueDto();
		valuedto1.setStypecode("成功");
		valuedto1.setSvalue(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
		this.statelist.add(valuedto1);

		TdEnumvalueDto valuedto2 = new TdEnumvalueDto();
		valuedto2.setStypecode("失败");
		valuedto2.setSvalue(DealCodeConstants.DEALCODE_ITFE_FAIL);
		this.statelist.add(valuedto2);

		TdEnumvalueDto valuedto5 = new TdEnumvalueDto();
		valuedto5.setStypecode("已重发");
		valuedto5.setSvalue(DealCodeConstants.DEALCODE_ITFE_REPEAT_SEND);
		this.statelist.add(valuedto5);

		this.statelist2 = new ArrayList<TdEnumvalueDto>();

		TdEnumvalueDto valuedtoa = new TdEnumvalueDto();
		valuedtoa.setStypecode("当前表");
		valuedtoa.setSvalue("0");
		this.statelist2.add(valuedtoa);

		TdEnumvalueDto valuedtob = new TdEnumvalueDto();
		valuedtob.setStypecode("历史表");
		valuedtob.setSvalue("1");
		this.statelist2.add(valuedtob);
		
		
		statusList = new ArrayList<TdEnumvalueDto>();
		TdEnumvalueDto statEnuma = new TdEnumvalueDto();
		statEnuma.setStypecode("成功");
		statEnuma.setSvalue("80000");
		statusList.add(statEnuma);

		TdEnumvalueDto statEnumb = new TdEnumvalueDto();
		statEnumb.setStypecode("失败");
		statEnumb.setSvalue("80001");
		statusList.add(statEnumb);
		
	}

	public String getSelectedtable() {
		return selectedtable;
	}

	public void setSelectedtable(String selectedtable) {
		this.selectedtable = selectedtable;
	}

	public List getStatelist2() {
		return statelist2;
	}

	public void setStatelist2(List statelist2) {
		this.statelist2 = statelist2;
	}

	public TvPayoutmsgmainDto getFinddto() {
		return finddto;
	}

	public void setFinddto(TvPayoutmsgmainDto finddto) {
		this.finddto = finddto;
	}

	public TvPayoutmsgmainDto getOperdto() {
		return operdto;
	}

	public void setOperdto(TvPayoutmsgmainDto operdto) {
		this.operdto = operdto;
	}

	public MainPayOutBean getMainPayOutBean() {
		return mainPayOutBean;
	}

	public void setMainPayOutBean(MainPayOutBean mainPayOutBean) {
		this.mainPayOutBean = mainPayOutBean;
	}

	public SubPayOutBean getSubPayOutBean() {
		return subPayOutBean;
	}

	public void setSubPayOutBean(SubPayOutBean subPayOutBean) {
		this.subPayOutBean = subPayOutBean;
	}

	public List getStatelist() {
		return statelist;
	}

	public void setStatelist(List statelist) {
		this.statelist = statelist;
	}

	private boolean datacheck(TvPayoutmsgmainDto dto) {
		if (null == dto.getSpayeebankno()
				|| dto.getSpayeebankno().trim().length() == 0) {
			MessageDialog.openMessageDialog(null, "转发银行不能为空！");
			return true;

		} else if (null == dto.getSrecbankno()
				|| dto.getSrecbankno().trim().length() == 0) {
			MessageDialog.openMessageDialog(null, "收款人开户行不能为空！");
			return true;

		}
		return false;

	}

	/**
	 * Direction: 转到发送页面事件 ename: gosendmsg 引用方法: viewers: 实拨资金包发送界面 messages:
	 */
	public String gosendmsg(Object o) {
		String sorgcode = loginfo.getSorgcode();
		try {
			sendmsglist = payoutService.sendfilelist(sorgcode);

			if (null == sendmsglist || sendmsglist.size() == 0) {
				MessageDialog.openMessageDialog(null, "没有符合条件的业务数据！");
				return super.rebackSearchList(o);
			}
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "查询实拨资金发送包出错："+e.getMessage());
			return "";
		}
		return super.gosendmsg(o);
	}

	/**
	 * Direction: 报文发送事件 ename: sendmsg 引用方法: viewers: * messages:
	 */
	public String sendmsg(Object o) {
		if (null == msgdto || null == msgdto.getSfilename()
				|| "".equals(msgdto.getSfilename().trim())) {
			MessageDialog.openMessageDialog(null, "请选择要发送报文的文件名称！");
			return super.sendmsg(o);
		}

		String sorgcode = loginfo.getSorgcode();
		String strecode = msgdto.getStrecode();
		String sfilename = msgdto.getSfilename();
		String scommitdate;
		String spackageno;
		try {
			List list = payoutService.getsendmsg(sorgcode, strecode, sfilename);
			if (null == list || list.size() == 0) {
				MessageDialog.openMessageDialog(null, "没有符合条件的业务数据！");
				return super.sendmsg(o);
			}

			TvFilepackagerefDto sendmsgdto;
			String spacknolist = "";
			for (int i = 0; i < list.size(); i++) {
				sendmsgdto = (TvFilepackagerefDto) list.get(i);
				scommitdate = sendmsgdto.getScommitdate();
				spackageno = sendmsgdto.getSpackageno();
				payoutService.reSendMsg(scommitdate, spackageno, sorgcode,
						sfilename);
				spacknolist = spacknolist + spackageno;
				if (i != list.size() - 1) {
					spacknolist = spacknolist + "\t\n";
				}
			}

			MessageDialog.openMessageDialog(null, "发送报文成功！包流水号：[" + spacknolist
					+ "]");
			msgdto = new TvFilepackagerefDto();

		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, "发送实拨资金报文失败！"+e.getMessage());
			return "";
		}

		return super.sendmsg(o);
	}

	/**
	 * Direction: 发送报文单击事件 ename: singleclicksendmsg 引用方法: viewers: * messages:
	 */
	public String singleclicksendmsg(Object o) {

		msgdto = (TvFilepackagerefDto) o;
		return "";
	}

	public TvFilepackagerefDto getMsgdto() {
		return msgdto;
	}

	public void setMsgdto(TvFilepackagerefDto msgdto) {
		this.msgdto = msgdto;
	}

	public List<TvFilepackagerefDto> getSendmsglist() {
		return sendmsglist;
	}

	public void setSendmsglist(List<TvFilepackagerefDto> sendmsglist) {
		this.sendmsglist = sendmsglist;
	}

	/**
	 * Direction: 实拨资金复核 ename: checkPayout 引用方法: viewers: 实拨资金信息列表 messages:
	 */
	public String checkPayout(Object o) {

		StringBuffer sbuf = new StringBuffer("");
		// 凭证编号复核
		if (operdto.getStaxticketno().compareTo(checkdto.getStaxticketno()) != 0) {
			sbuf.append("录入凭证编号【" + checkdto.getStaxticketno() + "】与文件凭证编号【"
					+ operdto.getStaxticketno() + "】不一致!");
			sbuf.append("\n");
		}

		// 收款人账号复核
		if (operdto.getSrecacct().compareTo(checkdto.getSrecacct()) != 0) {
			sbuf.append("录入收款人账号【" + checkdto.getSrecacct() + "】与文件收款人账号【"
					+ operdto.getSrecacct() + "】不一致!");
			sbuf.append("\n");
		}

		// 交易金额复核
		BigDecimal inputamt = checkdto.getNmoney();
		BigDecimal fileamt = operdto.getNmoney();
		if (inputamt.compareTo(fileamt) != 0) {
			sbuf.append("录入交易金额【" + inputamt + "】与文件交易金额【" + fileamt + "】不一致!");
			sbuf.append("\n");
		}
		if (sbuf.length() != 0) {
			MessageDialog.openMessageDialog(null, sbuf.toString());
			return null;
		} else {
			MessageDialog.openMessageDialog(null, "复核成功!");
		}
		operdto = new TvPayoutmsgmainDto();
		return super.rebackSearchList(o);
	}

	/**
	 * Direction: 去实拨资金复核 ename: goCheckPayout 引用方法: viewers: 实拨资金复核界面 messages:
	 */
	public String goCheckPayout(Object o) {

		if (null == selectedlist ||selectedlist.size()<=0) {
			MessageDialog.openMessageDialog(null, "请勾选要复核的记录！");
			return null;
		}else if(selectedlist.size()!=1){
			MessageDialog.openMessageDialog(null, "只能勾选一条要复核的记录！");
			return null;
		}
		checkdto = new TvPayoutmsgmainDto();
		return super.goCheckPayout(o);
	}
	
	/**
	 * Direction: 导出文件csv
	 * ename: exportfile
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String exportfile(Object o){
    	// 选择保存路径
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
			return "";
		}
		String clientFilePath;
		String dirsep = File.separator;
		String serverFilePath;
		try {
			StringBuffer where = null;
			if(scommitdatestart!=null&&!"".equals(scommitdatestart))
			{
				where = new StringBuffer("");
				where.append(" s_commitdate >='"+scommitdatestart+"'");
			}
			if(scommitdateend!=null&&!"".equals(scommitdateend))
			{
				if(where==null)
				{
					where = new StringBuffer();
					where.append(" s_commitdate <='"+scommitdateend+"'");
				}else
				{
					where.append(" and s_commitdate <='"+scommitdateend+"'");
				}
			}
			if(where!=null)
				finddto.setSaddword(where.toString());
			else
				finddto.setSaddword(null);
			serverFilePath = payoutService.exportfile(finddto, selectedtable)
					.replace("\\", "/");
			int j = serverFilePath.lastIndexOf("/");
			String partfilepath = serverFilePath.replaceAll(serverFilePath
					.substring(0, j + 1), "");
			clientFilePath = filePath + dirsep + partfilepath;
			File f = new File(clientFilePath);
			File dir = new File(f.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			ClientFileTransferUtil.downloadFile(serverFilePath, clientFilePath);
//			String key = commonDataAccessService.getModeKey(loginfo.getSorgcode(), "1");
//			SM3Process.addSM3Sign(clientFilePath, key);
			MessageDialog.openMessageDialog(null, "导出文件成功！文件保存在：\n"
					+ clientFilePath);

		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}
        return "";
    }

	/**
	 * Direction: 导出 ename: dataExport 引用方法: viewers: * messages:
	 */
	public String dataExport(Object o) {

		// 选择保存路径
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
			return "";
		}
		String clientFilePath;
		String dirsep = File.separator;
		String serverFilePath;
		try {
			StringBuffer where = null;
			if(scommitdatestart!=null&&!"".equals(scommitdatestart))
			{
				where = new StringBuffer("");
				where.append(" s_commitdate >='"+scommitdatestart+"'");
			}
			if(scommitdateend!=null&&!"".equals(scommitdateend))
			{
				if(where==null)
				{
					where = new StringBuffer();
					where.append(" s_commitdate <='"+scommitdateend+"'");
				}else
				{
					where.append(" and s_commitdate <='"+scommitdateend+"'");
				}
			}
			if(where!=null)
				finddto.setSaddword(where.toString());
			else
				finddto.setSaddword(null);
			serverFilePath = payoutService.dataexport(finddto, selectedtable).replace("\\", "/");
			int j = serverFilePath.lastIndexOf("/");
			String partfilepath = serverFilePath.replaceAll(serverFilePath
					.substring(0, j + 1), "");
			clientFilePath = filePath + dirsep + partfilepath;
			File f = new File(clientFilePath);
			File dir = new File(f.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			ClientFileTransferUtil.downloadFile(serverFilePath, clientFilePath);
			String key = commonDataAccessService.getModeKey(loginfo
					.getSorgcode(), "1");
			SM3Process.addSM3Sign(clientFilePath, key);
			MessageDialog.openMessageDialog(null, "导出文件成功！文件保存在：\n"
					+ clientFilePath);

		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return "";
	}

	/**
	 * Direction: 导出选中回单 ename: exportSelectData 引用方法: viewers: * messages:
	 */
	public String exportSelectData(Object o) {
		if (selectedlist.size() == 0) {
			MessageDialog.openMessageDialog(null, "未选择回单，请选择要导出的回单！");
			return "";
		}
		// 选择保存路径
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择导出文件路径。");
			return "";
		}
		String clientFilePath;
		String serverFilePath;
		String dirsep = File.separator;
		try {
			serverFilePath = payoutService.exportSelectData(selectedtable, selectedlist,filePath)
					.replace("\\", "/");
			int j = serverFilePath.lastIndexOf("/");
			String partfilepath = serverFilePath.replaceAll(serverFilePath
					.substring(0, j + 1), "");
			clientFilePath = filePath + dirsep + partfilepath;
			File f = new File(clientFilePath);
			File dir = new File(f.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			ClientFileTransferUtil.downloadFile(serverFilePath, clientFilePath);
			String key = commonDataAccessService.getModeKey(loginfo
					.getSorgcode(), "1");
			SM3Process.addSM3Sign(clientFilePath, key);
			MessageDialog.openMessageDialog(null, "导出文件成功！文件保存在：\n"
					+ clientFilePath);

		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}
		return "";
	}
	
	
	
	

	/**
	 * Direction: 转到修改页面 ename: goModify 引用方法: viewers: * messages:
	 */
	public String goModify(Object o) {
		if (null == selectedlist ||selectedlist.size()<=0) {
			MessageDialog.openMessageDialog(null, "请勾选要复核的记录！");
			return null;
		}else if(selectedlist.size()!=1){
			MessageDialog.openMessageDialog(null, "只能勾选一条要修改的记录！");
			return null;
		}
		operdto = (TvPayoutmsgmainDto) selectedlist.get(0);
		return super.goModify(o);
	}

	public TvPayoutmsgmainDto getCheckdto() {
		return checkdto;
	}

	public void setCheckdto(TvPayoutmsgmainDto checkdto) {
		this.checkdto = checkdto;
	}

	public HisMainPayOutBean getHisMainPayOutBean() {
		return hisMainPayOutBean;
	}

	public void setHisMainPayOutBean(HisMainPayOutBean hisMainPayOutBean) {
		this.hisMainPayOutBean = hisMainPayOutBean;
	}

	public HisSubPayOutBean getHisSubPayOutBean() {
		return hisSubPayOutBean;
	}

	public void setHisSubPayOutBean(HisSubPayOutBean hisSubPayOutBean) {
		this.hisSubPayOutBean = hisSubPayOutBean;
	}

	public HtvPayoutmsgmainDto getHisoperdto() {
		return hisoperdto;
	}

	public void setHisoperdto(HtvPayoutmsgmainDto hisoperdto) {
		this.hisoperdto = hisoperdto;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public List getReportRs() {
		return reportRs;
	}

	public void setReportRs(List reportRs) {
		this.reportRs = reportRs;
	}

	public Map getReportmap() {
		return reportmap;
	}

	public void setReportmap(Map reportmap) {
		this.reportmap = reportmap;
	}

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	public List<TvPayoutmsgmainDto> getSelectedlist() {
		return selectedlist;
	}

	public void setSelectedlist(List<TvPayoutmsgmainDto> selectedlist) {
		this.selectedlist = selectedlist;
	}

	public List<HtvPayoutmsgmainDto> getHselectedlist() {
		return hselectedlist;
	}

	public void setHselectedlist(List<HtvPayoutmsgmainDto> hselectedlist) {
		this.hselectedlist = hselectedlist;
	}

	public String getScommitdatestart() {
		return scommitdatestart;
	}

	public void setScommitdatestart(String scommitdatestart) {
		this.scommitdatestart = scommitdatestart;
	}

	public String getScommitdateend() {
		return scommitdateend;
	}

	public void setScommitdateend(String scommitdateend) {
		this.scommitdateend = scommitdateend;
	}

	public String getExpfunccode() {
		return expfunccode;
	}

	public void setExpfunccode(String expfunccode) {
		this.expfunccode = expfunccode;
	}

	public String getPayamt() {
		return payamt;
	}

	public void setPayamt(String payamt) {
		this.payamt = payamt;
	}

	public List getStatusList() {
		return statusList;
	}

	public void setStatusList(List statusList) {
		this.statusList = statusList;
	}

	public String getRealValue() {
		return realValue;
	}

	public void setRealValue(String realValue) {
		this.realValue = realValue;
		if(realValue != null && realValue.equals("80000")){
			modifyDto.setSxpayamt(modifyDto.getNmoney());
		}else if(realValue != null && realValue.equals("80001")){
			modifyDto.setSxpayamt(new BigDecimal("0.00"));
		}
		this.editor.fireModelChanged();
	}
	

}
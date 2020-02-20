package com.cfcc.itfe.client.dataquery.interestratemsg;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.logging.*;
import org.eclipse.swt.SWT;

import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfInterestDetailDto;
import com.cfcc.itfe.persistence.dto.TfInterestParamDto;
import com.cfcc.itfe.persistence.dto.TfInterestrateMsgDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsJxAcctinfoDto;
import com.cfcc.itfe.persistence.dto.TsPayacctinfoDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 14-09-23 17:40:57 子系统: DataQuery 模块:InterestrateMsg
 *       组件:InterestrateMsgQuery
 */
public class InterestrateMsgQueryBean extends AbstractInterestrateMsgQueryBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(InterestrateMsgQueryBean.class);
	private String ls_CurrentDate = TimeFacade.getCurrentStringTime();
	private String ls_CurrentYear = ls_CurrentDate.substring(0, 4);
	private ITFELoginInfo loginfo = null;

	private TfInterestrateMsgDto interestrateMsgDto;

	public InterestrateMsgQueryBean() {
		super();
		interestrateMsgDto = new TfInterestrateMsgDto();
		interestRateList = new ArrayList();
		interestVoucherList = new PagingContext(this);
		finddto = new TfInterestrateMsgDto();
		finddto.setSyear(ls_CurrentYear);
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		finddto.setSorgcode(loginfo.getSorgcode());
		interestDetailDto = new TfInterestDetailDto();
		reportPath = "/com/cfcc/itfe/client/ireport/interestParam.jasper";
		reportresult = new ArrayList();
		reportmap = new HashMap<String, String>();
		TfInterestParamDto paramDto = new TfInterestParamDto();
		try {
			List<TfInterestParamDto> paramDtoList = commonDataAccessService
					.findRsByDtoWithWhere(paramDto, " and S_STARTDATE<='"
							+ ls_CurrentDate + "' and S_ENDDATE >= '"
							+ ls_CurrentDate + "'");
			if (paramDtoList != null && paramDtoList.size() > 0)
				finddto.setSquarter(paramDtoList.get(0).getSquarter());
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
	}

	/**
	 * Direction: 查询列表事件 ename: searchList 引用方法: viewers: 计息统计信息列表 messages:
	 */
	public String searchList(Object o) {
		// interestVoucherList.setPage(new PageResponse());
		if (finddto == null || finddto.getSyear().equals("")) {
			MessageDialog.openMessageDialog(null, "请选择要查询的年度！");
			return "";
		}
		// PageRequest pageRequest = new PageRequest();
		// PageResponse pageResponse = retrieve(pageRequest);
		try {
			interestRateList.clear();
			interestRateList.addAll(commonDataAccessService
					.findRsByDto(finddto));
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return "";
		} finally {
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		}
		if (interestRateList == null || interestRateList.size() == 0) {
			MessageDialog.openMessageDialog(null, " 查询无记录,请先进行计息操作！");
			return null;
		}
		return super.searchList(o);
	}

	/**
	 * Direction: 返回查询界面 ename: 引用方法: viewers: messages:
	 */
	public String rebackSearch(Object o) {
		return super.rebackSearch(o);
	}

	/**
	 * Direction: 计息 ename: interestRate 引用方法: viewers: * messages:
	 */
	public String interestRate(Object o) {
		try {
			if (finddto == null || finddto.getSyear() == null
					|| finddto.getSyear().equals("")) {
				MessageDialog.openMessageDialog(null, "请选择要计息的年度！");
				return "";
			}
			if (finddto.getSquarter() == null || finddto.getSquarter() == null
					|| finddto.getSquarter().equals("")) {
				MessageDialog.openMessageDialog(null, "请选择要计息的季度！");
				return "";
			} else {
				// 计息 前先 查询季度利息参数表中是否已经维护参数，参数维护不正确的话，提示要先维护好参数表
				TfInterestParamDto paramDto = new TfInterestParamDto();
				paramDto.setSquarter(finddto.getSquarter());
				paramDto.setSext1(finddto.getSyear());
				List<TfInterestParamDto> paramDtoList = commonDataAccessService
						.findRsByDto(paramDto);// 
				if (paramDtoList == null || paramDtoList.size() == 0) {
					MessageDialog.openMessageDialog(null,
							"该季度的利息参数未设置或者设置有误，请先维护利息参数！");
					return "";
				}
				if (finddto != null) {
					if (!ls_CurrentYear.equals(finddto.getSyear())) {
						MessageDialog.openMessageDialog(null,
								"不能同时对跨年度的数据进行计息，请选择要重新计息的季度！");
						return "";
					}
					// 不在当前季度的记录计息要主管授权
					if ((Integer.parseInt(paramDtoList.get(0).getSstartdate()) > Integer
							.parseInt(ls_CurrentDate) || Integer
							.parseInt(paramDtoList.get(0).getSenddate()) < Integer
							.parseInt(ls_CurrentDate))) {
						if (!AdminConfirmDialogFacade
								.open("不在当前季度的记录计息要主管授权才能操作！")) {
							DisplayCursor.setCursor(SWT.CURSOR_ARROW);
							return null;
						}
					}
				}
			}
			interestrateMsgService.interestRate(finddto);
			MessageDialog.openMessageDialog(null, "计息完成！");
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}

		// return super.searchList(o);
		return super.interestRate(o);
	}

	/**
	 * Direction: 跳转计息报表展示 ename: goshowreport 引用方法: viewers: 计息报表展示 messages:
	 */
	public String goshowreport(Object o) {
		try {
			if (null == interestRateList || interestRateList.size() == 0) {
				MessageDialog.openMessageDialog(null, "查询数据库数据失败！");
				return null;
			}
			TfInterestrateMsgDto tmpDto = (TfInterestrateMsgDto) interestRateList
					.get(0);
			reportmap.put("startAndEndDate", tmpDto.getSstartdate() + "--"
					+ tmpDto.getSenddate());
			// reportmap.put("endDate", );
			reportmap.put("printDate", commonDataAccessService.getSysDBDate());
			reportresult = new ArrayList();
			reportresult.addAll(commonDataAccessService.findRsByDto(finddto));
			assembleReport();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.goshowreport(o);
	}

	private void assembleReport() throws ITFEBizException {
		if (null == reportresult || reportresult.size() == 0) {
			throw new ITFEBizException("查询数据库数据失败！");
		}
		// 代理行信息
		Map<String, TsConvertbanktypeDto> bankMap = getBankTypeMap();
		// 清算账户信息
		Map<String, TsPayacctinfoDto> payAcctInfoMap = getPayAcctInfo();
		// 计息账户信息
		Map<String, TsJxAcctinfoDto> jxAcctionInfos = getJxAcctInfos();
		// 财政机构代码
		Map<String, String> finOrgMap = getFinOrgMap();

		TfInterestDetailDto subDto;
		InterestrateReportDto tmpReportDto;
		TfInterestrateMsgDto search2302;
		List list2302;
		Map<String, List> reportListMap = new HashMap<String, List>();
		List tmpReportList;
		for (TfInterestrateMsgDto tmp : (List<TfInterestrateMsgDto>) reportresult) {
			if ("2302".equals(tmp.getSstatus().trim())) {
				continue;
			}
			search2302 = new TfInterestrateMsgDto();
			search2302.setSbanktype(tmp.getSbanktype());
			search2302.setSquarter(tmp.getSquarter());
			search2302.setSext3(tmp.getSext3());
			search2302.setSstartdate(tmp.getSstartdate());
			search2302.setSenddate(tmp.getSenddate());
			search2302.setSstatus("2302");
			list2302 = commonDataAccessService.findRsByDto(search2302);
			subDto = getInterestDetail(tmp);

			tmpReportDto = new InterestrateReportDto();
			if (null != bankMap.get(tmp.getSorgcode() + tmp.getSbanktype()
					+ subDto.getSbankno())) {
				tmpReportDto.setBankCode(bankMap.get(
						tmp.getSorgcode() + tmp.getSbanktype()
								+ subDto.getSbankno()).getSbankcode());
				tmpReportDto.setBankName(bankMap.get(
						tmp.getSorgcode() + tmp.getSbanktype()
								+ subDto.getSbankno()).getSbankname());
			} else {
				tmpReportDto.setBankCode(tmp.getSbanktype());
				tmpReportDto.setBankName(tmp.getSbanktype());
			}
			tmpReportDto
					.setFinOrgName(null == finOrgMap.get(tmp.getSext3()) ? tmp
							.getSext3() : finOrgMap.get(tmp.getSext3()));
			if (null != jxAcctionInfos.get(tmp.getSext3()
					+ tmpReportDto.getBankCode())) {
				tmpReportDto.setJxAcctCode(jxAcctionInfos.get(
						tmp.getSext3() + tmpReportDto.getBankCode())
						.getSpayeeacct());
				tmpReportDto.setJxAcctName(jxAcctionInfos.get(
						tmp.getSext3() + tmpReportDto.getBankCode())
						.getSpayeename());
			} else {
				tmpReportDto.setJxAcctCode(tmpReportDto.getBankCode());
				tmpReportDto.setJxAcctName(tmpReportDto.getBankCode());
			}
			tmpReportDto.setJxInterestRates(tmp.getNinterestrates());
			if (null != list2302 && list2302.size() > 0) {
				tmpReportDto
						.setJxInterestRateCount(((TfInterestrateMsgDto) list2302
								.get(0)).getNinterestratecount());
				tmpReportDto
						.setJxInterestvalue(((TfInterestrateMsgDto) list2302
								.get(0)).getNinterestvalue());
			} else {
				tmpReportDto.setJxInterestRateCount(BigDecimal.ZERO);
				tmpReportDto.setJxInterestvalue(BigDecimal.ZERO);
			}
			if (null != payAcctInfoMap.get(subDto.getSbankno())) {
				tmpReportDto.setPayAcctCode(payAcctInfoMap.get(
						subDto.getSbankno()).getSpayeracct());
				tmpReportDto.setPayAcctName(payAcctInfoMap.get(
						subDto.getSbankno()).getSpayername());
			} else {
				tmpReportDto.setPayAcctCode(subDto.getSbankno());
				tmpReportDto.setPayAcctName(subDto.getSbankno());
			}
			tmpReportDto.setPayInterestRateCount(tmp.getNinterestratecount());
			tmpReportDto.setPayInterestRates(tmp.getNinterestrates());
			tmpReportDto.setPayInterestvalue(tmp.getNinterestvalue());

			// if(null != bankMap.get(tmp.getSorgcode() + tmp.getSbanktype() +
			// subDto.getSbankno())){
			// tmp.setSbanktype(bankMap.get(tmp.getSorgcode() +
			// tmp.getSbanktype() + subDto.getSbankno()).getSbankname());
			// }else{
			// tmp.setSbanktype(tmp.getSbanktype());
			// }
			// if(null != payAcctInfoMap.get(subDto.getSbankno())){
			// tmp.setSext1(payAcctInfoMap.get(subDto.getSbankno()).getSpayername());
			// tmp.setSext2(payAcctInfoMap.get(subDto.getSbankno()).getSpayeracct());
			// }else{
			// tmp.setSext1(subDto.getSbankno());
			// tmp.setSext2(subDto.getSbankno());
			// }
			// tmp.setSext3(null == finOrgMap.get(tmp.getSext3()) ?
			// tmp.getSext3() : finOrgMap.get(tmp.getSext3()));

			if (null == reportListMap.get(tmpReportDto.getFinOrgName())) {
				List reportsubList = new ArrayList();
				reportsubList.add(tmpReportDto);
				reportListMap.put(tmpReportDto.getFinOrgName(), reportsubList);
			} else {
				reportListMap.get(tmpReportDto.getFinOrgName()).add(
						tmpReportDto);
			}
		}
		// 分组
		reportresult.clear();
		for (String finOrg : reportListMap.keySet()) {
			reportresult.addAll(reportListMap.get(finOrg));
		}

	}

	/**
	 * 获取计息账户
	 * 
	 * @return
	 */
	private Map<String, TsJxAcctinfoDto> getJxAcctInfos() {
		// TODO Auto-generated method stub
		try {
			TsJxAcctinfoDto searchDto = new TsJxAcctinfoDto();
			searchDto.setSorgcode(loginfo.getSorgcode());
			List<TsJxAcctinfoDto> list = commonDataAccessService
					.findRsByDto(searchDto);
			Map<String, TsJxAcctinfoDto> jxAcctMap = new HashMap<String, TsJxAcctinfoDto>();
			if (null == list || list.size() == 0) {
				return jxAcctMap;
			}
			for (TsJxAcctinfoDto tmpdto : list) {
				jxAcctMap.put(tmpdto.getStrecode() + tmpdto.getSopnbankcode(),
						tmpdto);
			}
			return jxAcctMap;
		} catch (ITFEBizException e) {
			log.error("查询行名行别对照关系失败！", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}

	}

	/**
	 * Direction: 主信息双击事件 ename: doubleclickMain 引用方法: viewers: * messages:
	 */
	public String doubleclickMain(Object o) {
		interestrateMsgDto = (TfInterestrateMsgDto) o;
		// interestDetailDto.setSorgcode(interestrateMsgDto.getSorgcode());
		// interestDetailDto.setSbanktype(interestrateMsgDto.getSbanktype());
		// String ls_sql = " S_LIQUIDATIONDATE >= '"
		// + interestrateMsgDto.getSstartdate()
		// + "' and S_LIQUIDATIONDATE <= '"
		// + interestrateMsgDto.getSenddate() + "'";
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = null;
		// try {
		pageResponse = retrieve(pageRequest);
		// commonDataAccessService.findRsByDtoWithWherePaging(
		// interestDetailDto, pageRequest, ls_sql);
		// } catch (ITFEBizException e) {
		// log.error(e);
		// MessageDialog.openErrorDialog(null, e);
		// }
		interestVoucherList.setPage(pageResponse);
		/*
		 * if (pageResponse == null || pageResponse.getTotalCount() == 0) {
		 * MessageDialog.openMessageDialog(null, " 查询无记录！"); return null; }
		 */
		return super.searchList(o);
	}

	/**
	 * 获取行名行别对照map
	 */
	private Map<String, TsConvertbanktypeDto> getBankTypeMap() {
		TsConvertbanktypeDto searchBankTypeDto = new TsConvertbanktypeDto();
		try {
			List<TsConvertbanktypeDto> result = commonDataAccessService
					.findAllDtos(TsConvertbanktypeDto.class);
			if (null != result && result.size() == 0) {
				MessageDialog.openMessageDialog(null, "查询行名行别对照关系失败！");
				return null;
			}
			Map<String, TsConvertbanktypeDto> resultMap = new HashMap<String, TsConvertbanktypeDto>();
			for (TsConvertbanktypeDto tmp : result) {
				resultMap.put(tmp.getSorgcode() + tmp.getSbanktype().trim()
						+ tmp.getSbankcode(), tmp);
			}
			return resultMap;
		} catch (ITFEBizException e) {
			log.error("查询行名行别对照关系失败！", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
	}

	/**
	 * 获取账户信息
	 * 
	 * @throws ITFEBizException
	 */
	private Map<String, TsPayacctinfoDto> getPayAcctInfo()
			throws ITFEBizException {
		try {
			List<TsPayacctinfoDto> result = commonDataAccessService
					.findRsByDtoWithWhere(new TsPayacctinfoDto(),
							" AND S_ORGCODE = '" + loginfo.getSorgcode()
									+ "'  AND S_PAYERACCT NOT LIKE '"
									+ loginfo.getSorgcode()
									+ "%' AND S_PAYERNAME LIKE '%授%权%'");
			if (null == result || result.size() == 0) {
				throw new ITFEBizException("查询代理行账户信息失败！");
			}
			Map<String, TsPayacctinfoDto> acctInfoMap = new HashMap<String, TsPayacctinfoDto>();
			for (TsPayacctinfoDto tmp : result) {
				acctInfoMap.put(tmp.getSgenbankcode(), tmp);
			}
			return acctInfoMap;
		} catch (ITFEBizException e) {
			log.error("查询代理行账户信息失败！", e);
			throw new ITFEBizException("查询代理行账户信息失败！", e);
		}
	}

	/**
	 * 查询计息子表信息
	 * 
	 * @throws ITFEBizException
	 */
	private TfInterestDetailDto getInterestDetail(
			TfInterestrateMsgDto interestrateMsgDto) throws ITFEBizException {
		try {
			TfInterestDetailDto searchsub = new TfInterestDetailDto();
			searchsub.setSorgcode(interestrateMsgDto.getSorgcode());
			searchsub.setSbanktype(interestrateMsgDto.getSbanktype());
			String ls_sql = " AND S_LIQUIDATIONDATE >= '"
					+ interestrateMsgDto.getSstartdate()
					+ "' and S_LIQUIDATIONDATE <= '"
					+ interestrateMsgDto.getSenddate() + "'";
			List<TfInterestDetailDto> result = commonDataAccessService
					.findRsByDtoWithWhere(searchsub, ls_sql);
			if (null == result || result.size() == 0) {
				MessageDialog.openMessageDialog(null, "查询计息明细信息失败！");
				return null;
			}
			return result.get(0);
		} catch (ITFEBizException e) {
			log.error("查询计息明细信息失败！", e);
			throw new ITFEBizException("查询计息明细信息失败！", e);
		}
	}

	// TS_CONVERTFINORG
	private Map<String, String> getFinOrgMap() throws ITFEBizException {
		List<TsConvertfinorgDto> result = commonDataAccessService
				.findAllDtos(TsConvertfinorgDto.class);
		if (null == result || result.size() == 0) {
			throw new ITFEBizException("查询财政机构代码信息失败！");
		}
		Map<String, String> finMap = new HashMap<String, String>();
		for (TsConvertfinorgDto tmp : result) {
			finMap.put(tmp.getStrecode(), tmp.getSfinorgname());
		}
		return finMap;
	}

	/**
	 * Direction: 数据导出 ename: dataExport 引用方法: viewers: * messages:
	 */
	public String dataExport(Object o) {
		try {
			if (null == interestRateList || interestRateList.size() == 0) {
				MessageDialog.openMessageDialog(null, "没有需要导出的数据！");
				return null;
			}
			TfInterestDetailDto tmpSubDto = null;
			List<TfInterestDetailDto> resultList = new ArrayList<TfInterestDetailDto>();
//			StringBuffer resultStr = new StringBuffer(
//					"年度,季度开始日期,季度截止日期,行别,利息积数,利率（%）,利息,业务类型\n
			//行别,行号,凭证编号,支付凭证单号,商行处理日期,清算日期,计息金额\n");
			StringBuffer resultStr = new StringBuffer("行别,凭证编号,支付凭证单号,金额,商行受理日期,人行清算日期,计息天数,积数,利息类型(支付或退票)\n");
			for (TfInterestrateMsgDto tmpMainDto : (List<TfInterestrateMsgDto>) interestRateList) {
				tmpSubDto = new TfInterestDetailDto();
				tmpSubDto.setSorgcode(tmpMainDto.getSorgcode());
				tmpSubDto.setSbanktype(tmpMainDto.getSbanktype());
				tmpSubDto.setSvouchertype(tmpMainDto.getSstatus());
				resultList = commonDataAccessService.findRsByDtoWithWhere(tmpSubDto,
						" and S_LIQUIDATIONDATE >= '" + tmpMainDto.getSstartdate()
								+ "' and S_LIQUIDATIONDATE <= '"
								+ tmpMainDto.getSenddate() + "'");
				if(null == resultList || resultList.size() == 0){
					continue;
				}
//				resultStr.append(tmpMainDto.getSyear()+ "," + tmpMainDto.getSstartdate() + "," + tmpMainDto.getSenddate() + "," + tmpMainDto.getSbanktype() + "," + tmpMainDto.getNinterestratecount() + "," + tmpMainDto.getNinterestrates() + "," + tmpMainDto.getNinterestvalue() + "," + (tmpMainDto.getSstatus().trim().equals("2301")?"授权划款":"授权退款") + "\n");
//				resultStr.append(tmpMainDto.getSyear()+ "," + tmpMainDto.getSstartdate() + "," + tmpMainDto.getSenddate() + "," + tmpMainDto.getSbanktype() + "," + tmpMainDto.getNinterestratecount() + "," + tmpMainDto.getNinterestrates() + "," + tmpMainDto.getNinterestvalue() + "," + (tmpMainDto.getSstatus().trim().equals("2301")?"授权划款":"授权退款") + "\n");
				exportStr(resultStr, resultList,tmpMainDto);
			}
			FileUtil.getInstance().writeFile("C:\\jx\\" + loginfo.getCurrentDate() + "\\计息详细信息" + loginfo.getCurrentDate().replaceAll("-", "") +".csv", resultStr.toString());
			MessageDialog.openMessageDialog(null, "文件导出成功！" + "C:\\jx\\" + loginfo.getCurrentDate() + "\\计息详细信息" + loginfo.getCurrentDate().replaceAll("-", "") +".csv");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}  catch (FileOperateException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.dataExport(o);
	}
	
	private void exportStr(StringBuffer resultStr , List<TfInterestDetailDto> resultList,TfInterestrateMsgDto tmpMainDto) throws ITFEBizException{
		for(TfInterestDetailDto tmpSubDto : resultList){
			resultStr.append(tmpSubDto.getSbanktype() + "," + tmpSubDto.getSvoucherno() + "," + tmpSubDto.getSext3() + "," + tmpSubDto.getNmoney() + "," + tmpSubDto.getSinterestdate() + "," + tmpSubDto.getSliquidationdate() + "," + jsDate(tmpSubDto.getSinterestdate(),tmpSubDto.getSliquidationdate()) + "," + tmpMainDto.getNinterestrates() + ","  + (tmpMainDto.getSstatus().trim().equals("2301")?"授权划款":"授权退款") + "\n");
			
//			resultStr.append(tmpSubDto.getSbanktype() + "," + tmpSubDto.getSbankno() + "," + tmpSubDto.getSvoucherno() + "," + tmpSubDto.getSext3() + "," + tmpSubDto.getSinterestdate() + "," + tmpSubDto.getSliquidationdate() + "," + tmpSubDto.getNmoney() + "\n");
		}
	}
	
	private int jsDate(String startDate,String endDate) throws ITFEBizException{
		try {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date enddate = format.parse(endDate);
		Date startdate = format.parse(startDate);
		
		return (int) ((enddate.getTime() - startdate.getTime())/(24*3600*1000));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(),e);
			throw new ITFEBizException("日期格式化失败！",e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest arg0) {
		interestDetailDto.setSorgcode(interestrateMsgDto.getSorgcode());
		interestDetailDto.setSbanktype(interestrateMsgDto.getSbanktype());
		interestDetailDto.setSvouchertype(interestrateMsgDto.getSstatus());
		String ls_sql = " S_LIQUIDATIONDATE >= '"
				+ interestrateMsgDto.getSstartdate()
				+ "' and S_LIQUIDATIONDATE <= '"
				+ interestrateMsgDto.getSenddate() + "'";

		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(
					interestDetailDto, arg0, ls_sql);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(arg0);
	}

}
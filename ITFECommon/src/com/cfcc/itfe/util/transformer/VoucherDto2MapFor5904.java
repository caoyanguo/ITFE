package com.cfcc.itfe.util.transformer;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.MainAndSubDto;
import com.cfcc.itfe.persistence.dto.PayreckCountDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoSubDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

public class VoucherDto2MapFor5904 implements IVoucherDto2Map {

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor5904.class);

	/**
	 * 凭证生成
	 * 
	 * @param vDto
	 * @return
	 * @throws ITFEBizException
	 */
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException {
		if (vDto == null)
			return null;
		vDto.setScreatdate(TimeFacade.getCurrentStringTime());
		return getVoucher(vDto);
	}

	private List getVoucher(TvVoucherinfoDto vDto) throws ITFEBizException {
		List returnList = new ArrayList();
		TsConvertfinorgDto cDto = new TsConvertfinorgDto();
		SQLExecutor execDetail = null;
		try {
			cDto.setSorgcode(vDto.getSorgcode());
			cDto.setStrecode(vDto.getStrecode());
			List<TsConvertfinorgDto> tsConvertfinorgList = (List<TsConvertfinorgDto>) CommonFacade
					.getODB().findRsByDto(cDto);
			if (tsConvertfinorgList == null || tsConvertfinorgList.size() == 0) {
				throw new ITFEBizException("国库：" + vDto.getStrecode()
						+ "对应的财政机关代码参数未维护！");
			}
			cDto = (TsConvertfinorgDto) tsConvertfinorgList.get(0);
			vDto.setSadmdivcode(cDto.getSadmdivcode());
			if (cDto.getSadmdivcode() == null
					|| cDto.getSadmdivcode().equals("")) {
				throw new ITFEBizException("国库：" + cDto.getStrecode()
						+ "对应的区划代码未维护！");
			}
			execDetail = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			List listall = getDataList(vDto, execDetail);
			if (listall != null && listall.size() > 0) {
				createVoucher(vDto, returnList, cDto, listall);
			}

		} catch (JAFDatabaseException e2) {
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！", e2);
		} catch (ValidateException e2) {
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！", e2);
		} finally {
			if (execDetail != null)
				execDetail.closeConnection();
		}
		return returnList;
	}

	private void createVoucher(TvVoucherinfoDto vDto, List returnList,
			TsConvertfinorgDto cDto, List listall) throws ITFEBizException {
		List<List> sendList = this.getSubLists(listall, 1000);
		for (int i = 0; i < sendList.size(); i++) {
			List<IDto> tempList = sendList.get(i);
			
			IDto idto = (IDto) tempList.get(i);
			String danhao = null;
			String FileName = null;
			String dirsep = File.separator;
			String mainvou = "";
			TvVoucherinfoDto dto = new TvVoucherinfoDto();
			dto.setSorgcode(vDto.getSorgcode());
			dto.setSadmdivcode(vDto.getSadmdivcode());
			dto.setSvtcode(vDto.getSvtcode());
			dto.setScreatdate(TimeFacade.getCurrentStringTime());
			dto.setStrecode(vDto.getStrecode());
			dto.setSstyear(dto.getScreatdate().substring(0, 4));
			dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
			dto.setSdemo("处理成功");
			dto.setSvoucherflag("1");
			dto.setNmoney(new BigDecimal("0.00"));
			dto.setSext1("1");// 发起方式1:人行发起,2:财政发起,3:商行发起
			if (idto instanceof TsBudgetsubjectDto) {
				dto.setSext4("1");
				dto.setSext5("预算科目");
			} else if (idto instanceof TsPaybankDto) {
				dto.setSext4("2");
				dto.setSext5("支付行号");
			}
			mainvou = VoucherUtil.getGrantSequence();
			vDto.setSdealno(mainvou);
			vDto.setSvoucherno(mainvou);
			dto.setSvoucherno(mainvou);
			if (danhao == null)
				danhao = mainvou;
			FileName = ITFECommonConstant.FILE_ROOT_PATH + dirsep + "Voucher"
					+ dirsep + vDto.getScreatdate() + dirsep + "send"
					+ vDto.getSvtcode() + "_" + mainvou + ".msg";
			dto.setSfilename(FileName);
			List<IDto> idtoList = new ArrayList<IDto>();
			Map map = tranfor(dto, tempList);
			dto.setSpaybankcode("011");
			dto.setSdealno(mainvou);
			dto.setSvoucherno(mainvou);
			List vouList = new ArrayList();
			vouList.add(map);
			vouList.add(dto);
			vouList.add(idtoList);
			returnList.add(vouList);
		}

	}

	private Map tranfor(TvVoucherinfoDto vDto, List<IDto> idtolist)
			throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体
			vouchermap.put("Id", vDto.getSvoucherno());// 流水号
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());// 行政区划代码
			vouchermap.put("StYear", vDto.getScreatdate().substring(0, 4));// 业务年度
			vouchermap.put("VtCode", vDto.getSvtcode());// 凭证类型编号
			vouchermap.put("VouDate", vDto.getScreatdate());// 凭证日期
			vouchermap.put("VoucherNo", vDto.getSvoucherno());// 凭证号
			vouchermap.put("DataEle", vDto.getSext4());// 基础数据代码
			vouchermap.put("DataEleName", vDto.getSext5());//基础数据中文名称
			vouchermap.put("Hold1", "");// 预留字段1
			vouchermap.put("Hold2", "");// 预留字段2
			vouchermap.put("Hold3", "");// 预留字段3
			vouchermap.put("Hold4", "");// 预留字段4
			List<Object> Detail= new ArrayList<Object>();
			if (vDto.getSext4().equals("1")) {//预算科目
				for(int i = 0; i<idtolist.size();i++){
					HashMap<String, Object> Detailmap = new HashMap<String, Object>();
					TsBudgetsubjectDto idto = (TsBudgetsubjectDto) idtolist.get(i);
					Detailmap.put("Id", i+1);// 流水号
					Detailmap.put("Subjectcode", idto.getSsubjectcode());// 科目代码
					Detailmap.put("Subjectname", idto.getSsubjectname());// 科目名称
					Detailmap.put("Subjecttype", idto.getSsubjecttype());// 科目类型
					Detailmap.put("Subjectclassification",idto.getSsubjectclass());// 科目分类
					Detailmap.put("IEsign", idto.getSinoutflag());// 收支标志
					Detailmap.put("Entrymark", idto.getSwriteflag());// 录入标志
					Detailmap.put("Subjectattribute", idto.getSsubjectattr());// 科目属性
					Detailmap.put("Allocationmark", idto.getSmoveflag());// 调拨标志
					Detailmap.put("SubjectcodeSimplify", "");// 科目简码
					Detailmap.put("FiscalCode", "");// 财政代码
					Detailmap.put("TaxCode", "");// 税务代码
					Detailmap.put("SuperiorsCode", idto.getSsubjectcode());// 上级代码
					Detailmap.put("UniformCode", "");// 统一代码
					Detailmap.put("Statisticalcode", "");// 统计代码
					
					Detailmap.put("Paylinenumber", "");// 支付行号
					Detailmap.put("Status", "");// 状态
					Detailmap.put("Liquidationlinenumber", "");// 清算行行号
					Detailmap.put("City", "");// 所在城市
					Detailmap.put("Participantsfullname", "");// 参与者全称
					Detailmap.put("effectivedate", "");// 生效日期
					Detailmap.put("Expirationdate", "");// 失效日期
					Detailmap.put("Remark", "");// 备注
					Detailmap.put("ZeroAccount", "");// 零余额账号
					Detailmap.put("ZBAsstate", "");// 零余额账户状态
					Detailmap.put("Zerotime", "");// 零余额操作时间
					Detailmap.put("ZeroBanknumber", "");// 零余额开户行号
					Detailmap.put("ZeroBankname", "");// 零余额开户行名称
					Detailmap.put("Zerocodingunit", "");// 零余额开户单位编码
					Detailmap.put("ZeroAccountName", "");// 零余额开户单位名称
					Detailmap.put("SupDepName", "");// 单位名称
					Detailmap.put("SupDepCode", "");// 单位编码
					Detailmap.put("UnitChangeStatus", "");// 单位变更状态
					Detailmap.put("IssuedDate", "");// 发文日期
					Detailmap.put("EffectiveDate", "");// 生效日期 
					Detail.add(Detailmap);
				}
			}
			
			if (vDto.getSext4().equals("2")) {
				
				for(int i = 0; i<idtolist.size();i++){
					HashMap<String, Object> Detailmap = new HashMap<String, Object>();
					TsPaybankDto idto = (TsPaybankDto) idtolist.get(i);
					Detailmap.put("Id", i);// 流水号
					Detailmap.put("Subjectcode", "");// 科目代码
					Detailmap.put("Subjectname", "");// 科目名称
					Detailmap.put("Subjecttype", "");// 科目类型
					Detailmap.put("Subjectclassification","");// 科目分类
					Detailmap.put("IEsign", "");// 收支标志
					Detailmap.put("Entrymark", "");// 录入标志
					Detailmap.put("Subjectattribute", "");// 科目属性
					Detailmap.put("Allocationmark", "");// 调拨标志
					Detailmap.put("SubjectcodeSimplify", "");// 科目简码
					Detailmap.put("FiscalCode", "");// 财政代码
					Detailmap.put("TaxCode", "");// 税务代码
					Detailmap.put("SuperiorsCode", "");// 上级代码
					Detailmap.put("UniformCode", "");// 统一代码
					Detailmap.put("Statisticalcode", "");// 统计代码
					
					Detailmap.put("Paylinenumber",idto.getSbankno());// 支付行号
					String state = idto.getSstate();// 0生效前，1有效，2注销
					if (state.endsWith("0")) {
						Detailmap.put("Status", "1");// 状态
					} else if (state.endsWith("2")) {
						Detailmap.put("Status", "2");// 状态
					}
					Detailmap.put("Liquidationlinenumber", idto.getSpaybankno());// 清算行行号
					Detailmap.put("City", idto.getSofcity());// 所在城市
					Detailmap.put("Participantsfullname", idto.getSbankname());// 参与者全称
					Detailmap.put("effectivedate", idto.getDaffdate());// 生效日期
					Detailmap.put("Expirationdate", "");// 失效日期
					Detailmap.put("Remark", "");// 备注
					
					Detailmap.put("ZeroAccount", "");// 零余额账号
					Detailmap.put("ZBAsstate", "");// 零余额账户状态
					Detailmap.put("Zerotime", "");// 零余额操作时间
					Detailmap.put("ZeroBanknumber", "");// 零余额开户行号
					Detailmap.put("ZeroBankname", "");// 零余额开户行名称
					Detailmap.put("Zerocodingunit", "");// 零余额开户单位编码
					Detailmap.put("ZeroAccountName", "");// 零余额开户单位名称
					Detailmap.put("SupDepName", "");// 单位名称
					Detailmap.put("SupDepCode", "");// 单位编码
					Detailmap.put("UnitChangeStatus", "");// 单位变更状态
					Detailmap.put("IssuedDate", "");// 发文日期
					Detailmap.put("EffectiveDate", "");// 生效日期 
					Detail.add(Detailmap);
				}
			}
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail",Detail);
			vouchermap.put("DetailList", DetailListmap);
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}

	private String getString(Map datamap, String key) {
		if (datamap == null || key == null)
			return "";
		else
			return String.valueOf(datamap.get(key));
	}

	public Map tranfor(List list) throws ITFEBizException {
		Map map = new HashMap<String, PayreckCountDto>();
		for (PayreckCountDto dto : (List<PayreckCountDto>) list) {
			if (map.get(dto.getFundtypecode() + "-" + dto.getPaytypecode()) == null) {
				map.put(dto.getFundtypecode() + "-"+ dto.getPaytypecode(), dto);
			}
		}
		return map;
	}

	private List getDataList(TvVoucherinfoDto vDto, SQLExecutor execDetail)
			throws ITFEBizException {
		List listall = new ArrayList();
		StringBuffer sql = null;
		try {
			// 预算科目
			if (vDto.getSext4() == null || vDto.getSext4().equals("")
					|| vDto.getSext4().equals("1")) {
				TsBudgetsubjectDto dto = new TsBudgetsubjectDto();
				dto.setSorgcode(vDto.getSorgcode());
				dto.setScreatstatus("1");// 没有生成过5904
				List<TsBudgetsubjectDto> list = (List<TsBudgetsubjectDto>) CommonFacade
						.getQDB().findRsByDto(dto);
				if (list != null && list.size() > 0)
					listall.addAll(list);
			}
			// 支付系统行号
			if (vDto.getSext4() == null || vDto.getSext4().equals("")
					|| vDto.getSext4().equals("2")) {
				TsPaybankDto dto = new TsPaybankDto();
				dto.setScreatstatus("1");// 没有生成过5904
				List<TsPaybankDto> list = (List<TsPaybankDto>) CommonFacade
						.getQDB().findRsByDto(dto);
				if (list != null && list.size() > 0)
					listall.addAll(list);
			}

		} catch (Exception e) {
			if (execDetail != null)
				execDetail.closeConnection();
			logger.error(e.getMessage(), e);
			throw new ITFEBizException("查询" + sql == null ? "" : sql.toString()
					+ "基础数据元数据信息异常！", e);
		} finally {
			if (execDetail != null)
				execDetail.closeConnection();
		}
		return listall;
	}
	
	private List getSubLists(List list,int subsize)
	{
		List getList = null;
		if(list!=null&&list.size()>0)
		{
			if(subsize<1)
				subsize=500;
			int count = list.size()/subsize;
			int yu = list.size()%subsize;
			getList = new ArrayList();
			for(int i=0;i<count;i++)
				getList.add(list.subList(i*subsize, subsize*(i+1)));
			if(yu>0)
				getList.add(list.subList(count*subsize, (count*subsize)+yu));
		}
		return getList;
	}
	
	private String getString(String key) {
		if (key == null)
			key = "";
		return key;
	}

	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		return null;
	}

}

package com.cfcc.itfe.service.recbiz.backvoucher;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.IVoucherDto2Map;
import com.cfcc.itfe.util.transformer.VoucherDto2MapFor3208;
import com.cfcc.itfe.util.transformer.VoucherDto2MapFor3210;
import com.cfcc.itfe.util.transformer.VoucherDto2MapFor3268;
import com.cfcc.itfe.util.transformer.VoucherDto2MapFor3251;
import com.cfcc.itfe.voucher.service.VoucherFactory;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author Tyler
 * @time 14-04-01 16:47:31 codecomment:
 */

public class BackVoucherService extends AbstractBackVoucherService {
	private static Log log = LogFactory.getLog(BackVoucherService.class);

	/**
	 * 生成凭证
	 */
	@SuppressWarnings("unchecked")
	public void voucherGenerate(List params) throws ITFEBizException {
		String type = (String) params.get(0);
		TvVoucherinfoDto vdto = (TvVoucherinfoDto) params.get(2);
		if (type.equals(MsgConstant.VOUCHER_NO_3208)) {
			TvPayoutmsgmainDto tvPayoutmsgmainDto = (TvPayoutmsgmainDto) params
					.get(1);
			tvPayoutmsgmainDto.setSbackflag("1");
			// 组装报文
			Map map = new VoucherDto2MapFor3208().tranfor(params);
			// 保存文件
			VoucherUtil.sendTips(vdto, map);
			try {
				// 更新业务表标志字段
				String sqlStr = "UPDATE TV_PAYOUTMSGMAIN SET S_BACKFLAG = '1' where S_BIZNO = '"
						+ tvPayoutmsgmainDto.getSbizno() + "'";
				DatabaseFacade.getDb().execSql(sqlStr);
				DatabaseFacade.getDb().execSql(sqlStr.replace("TV_PAYOUTMSGMAIN", "HTV_PAYOUTMSGMAIN"));
				// 保存索引表
				vdto.setSext4(tvPayoutmsgmainDto.getSbizno());
				DatabaseFacade.getDb().create(vdto);
			} catch (JAFDatabaseException e) {
				log.error(e);
				throw new ITFEBizException("更新业务主表出错或保存索引表出错！");
			}
		}else if(type.equals(MsgConstant.VOUCHER_NO_3268)) {
			TvPayoutmsgmainDto tvPayoutmsgmainDto = (TvPayoutmsgmainDto) params
			.get(1);
			tvPayoutmsgmainDto.setSbackflag("1");
			// 组装报文
			Map map = new VoucherDto2MapFor3268().tranfor(params);
			// 保存文件
			VoucherUtil.sendTips(vdto, map);
			try {
				// 更新业务表标志字段
				String sqlStr = "UPDATE TV_PAYOUTMSGMAIN SET S_BACKFLAG = '1' where S_BIZNO = '"
						+ tvPayoutmsgmainDto.getSbizno() + "'";
				DatabaseFacade.getDb().execSql(sqlStr);
				DatabaseFacade.getDb().execSql(sqlStr.replace("TV_PAYOUTMSGMAIN", "HTV_PAYOUTMSGMAIN"));
				// 保存索引表
				vdto.setSext4(tvPayoutmsgmainDto.getSbizno());
				DatabaseFacade.getDb().create(vdto);
			} catch (JAFDatabaseException e) {
				log.error(e);
				throw new ITFEBizException("更新业务主表出错或保存索引表出错！");
			}
		} else if (type.equals(MsgConstant.VOUCHER_NO_3210)|| type.equals(MsgConstant.VOUCHER_NO_3251)) {
			TvDwbkDto tvDwbkDto = (TvDwbkDto) params.get(1);
			tvDwbkDto.setCbckflag("1");
			// 组装报文
			Map map ;
				if(ITFECommonConstant.PUBLICPARAM.contains(",jilin,")){
					map = new VoucherDto2MapFor3251().tranfor(params);
				}else{
					map = new VoucherDto2MapFor3210().tranfor(params);
				}
			// 保存文件
			VoucherUtil.sendTips(vdto, map);
			try {
				// 更新业务表标志字段
				String sqlStr = "UPDATE TV_DWBK SET C_BCKFLAG = '1' where I_VOUSRLNO = "
						+ tvDwbkDto.getIvousrlno()
						+ " and S_BOOKORGCODE = '"
						+ tvDwbkDto.getSbookorgcode() + "'";
				DatabaseFacade.getDb().execSql(sqlStr);
				// 保存索引表
				vdto.setSext4(tvDwbkDto.getSbizno());
				DatabaseFacade.getDb().create(vdto);
			} catch (JAFDatabaseException e) {
				log.error(e);
				throw new ITFEBizException("更新业务主表出错或保存索引表出错！");
			}
		}else if (type.equals(MsgConstant.VOUCHER_NO_2203)) {
			IVoucherDto2Map voucher = (IVoucherDto2Map) ContextFactory
					.getApplicationContext().getBean(
							MsgConstant.VOUCHER_DTO2MAP + type);
			Map map = voucher.tranfor(params);
			// 前置发起的，需要将5201数据复制到 商行直接支付退款表中，用与后续生成2302
			IDto idto = (IDto) params.get(1);
			TvVoucherinfoDto _dto = (TvVoucherinfoDto) map.get("voucherDto");
			if (idto instanceof TfDirectpaymsgmainDto) {
				try {
					copyMain5201to2202(_dto, (TfDirectpaymsgmainDto) idto);
					copySub5201to2202((TfDirectpaymsgmainDto) idto);
				} catch (JAFDatabaseException e) {
					log.error(e);
					throw new ITFEBizException("前置生成直接支付退款时 复制5201表出错！", e);
				}

			}
		}
	}

	public TvVoucherinfoDto getTvVoucherinfoDto(List params)
			throws ITFEBizException {
		String type = (String) params.get(0);
		TsConvertfinorgDto cforgDto = (TsConvertfinorgDto) params.get(1);
		BigDecimal nMoney = (BigDecimal) params.get(2);

		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		String dirsep = File.separator;
		String currentDate = TimeFacade.getCurrentStringTime();
		Timestamp now = new Timestamp(TimeFacade.getCurrentDateTime().getTime());
		String number1 = VoucherUtil.getGrantSequence();
		String FileName = ITFECommonConstant.FILE_ROOT_PATH + File.separator
				+ "Voucher" + File.separator + currentDate + File.separator
				+ "send" + type + "_" + number1 + ".msg";

		vDto.setSdealno(number1);
		vDto.setSorgcode(cforgDto.getSorgcode());
		vDto.setStrecode(cforgDto.getStrecode());
		vDto.setSfilename(FileName);
		vDto.setSadmdivcode(cforgDto.getSadmdivcode());
		vDto.setSstyear(currentDate.substring(0, 4));
		vDto.setSvtcode(type);
		vDto.setSvoucherno(number1);
		vDto.setSvoucherflag("0");
		if (type.equals(MsgConstant.VOUCHER_NO_3208)) {
			vDto.setSattach(MsgConstant.VOUCHER_NO_5207);
		} else if (type.equals(MsgConstant.VOUCHER_NO_3268)) {
			vDto.setSattach(MsgConstant.VOUCHER_NO_5267);
		}else if (type.equals(MsgConstant.VOUCHER_NO_3210)) {
			vDto.setSattach(MsgConstant.VOUCHER_NO_5209);
		}
		vDto.setScreatdate(currentDate);
		vDto.setNmoney(nMoney);
		vDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		vDto.setSdemo("处理成功");
		vDto.setTssysupdate(now);
		vDto.setSrecvtime(now);
		//如果是由TCBS回执发起的实拨资金退款,需要标注单笔还是批量业务(除了上海，其他地方默认都是单笔业务--1)
		if(params.size()>=5&&params.get(4).toString().trim().equals("TCBS")){
			TvPayoutbackmsgMainDto dto = (TvPayoutbackmsgMainDto) params.get(3);
			//设置批量标志，单笔 - 1 、批量退款 - 4
			if(StringUtils.isNotBlank(dto.getShold4())&&dto.getShold4().trim().equals(StateConstant.BIZTYPE_CODE_BATCH)){
				vDto.setShold4(StateConstant.BIZTYPE_CODE_BATCH);// 标明为批量业务
			}else{
				vDto.setShold4(StateConstant.BIZTYPE_CODE_SINGLE);// 标明为单笔业务
			}
		}
		
		return vDto;
	}

	/**
	 * voucherGenerate
	 * 
	 * @generated
	 * @param list
	 * @return com.cfcc.itfe.facade.data.MulitTableDto
	 * @throws ITFEBizException
	 */
	public MulitTableDto voucherGenerateForTCBS(List list)
			throws ITFEBizException {
		int successcount = 0;
		int failcount = 0;
		MulitTableDto mulitTableDto = new MulitTableDto();
		List<String> errorList = new ArrayList<String>();
		String type = (String) list.get(0);// 业务类型
		TvPayoutbackmsgMainDto querydto = (TvPayoutbackmsgMainDto) list.get(1);// 查询dto
		// 校验国库对应的财政机构信息、区划代码是否维护
		TsConvertfinorgDto cDto = new TsConvertfinorgDto();
		cDto.setSorgcode(querydto.getSorgcode());
		cDto.setStrecode(querydto.getStrecode());
		List finorgDtoList = null;
		try {
			finorgDtoList = CommonFacade.getODB().findRsByDto(cDto);
		} catch (JAFDatabaseException e2) {
			log.error(e2);
			throw new ITFEBizException("查询信息异常！", e2);
		} catch (ValidateException e2) {
			log.error(e2);
			throw new ITFEBizException("查询信息异常！", e2);
		}
		if (finorgDtoList == null || finorgDtoList.size() == 0) {
			throw new ITFEBizException("国库：" + cDto.getStrecode()
					+ " 对应的财政代码未维护！");
		}
		cDto = (TsConvertfinorgDto) finorgDtoList.get(0);
		if (cDto.getSadmdivcode() == null || cDto.getSadmdivcode().equals("")) {
			throw new ITFEBizException("国库：" + cDto.getStrecode()
					+ " 对应的区划代码未维护，凭证未生成！");
		}
		// 生成凭证
		if (type.equals(MsgConstant.VOUCHER_NO_3208)||type.equals(MsgConstant.VOUCHER_NO_3268)) {
			List<TvPayoutbackmsgMainDto> voucherlist = (List<TvPayoutbackmsgMainDto>) list
					.get(2);// 生成凭证list
			for (int i = 0; i < voucherlist.size(); i++) {
				TvPayoutbackmsgMainDto dto = voucherlist.get(i);
				try {
					// 组装凭证索引表dto
					List param = new ArrayList();
					param.add(type);
					param.add(cDto);
					param.add(dto.getNmoney());
					param.add(dto);
					param.add("TCBS");
					TvVoucherinfoDto voucherdto = getTvVoucherinfoDto(param);
					voucherdto.setSvoucherno(dto.getSvouno());
					// 组装报文
					Map map = null;
					if(type.equals(MsgConstant.VOUCHER_NO_3208))
						map = new VoucherDto2MapFor3208().tranforForTCBS(dto,voucherdto);
					else
						map = new VoucherDto2MapFor3268().tranforForTCBS(dto,voucherdto);
					// 保存文件
					VoucherUtil.sendTips(voucherdto, map);
					dto.setSisreturn(StateConstant.COMMON_YES);
					DatabaseFacade.getODB().update(dto);
					DatabaseFacade.getODB().create(voucherdto);
					successcount++;
				} catch (JAFDatabaseException e2) {
					log.error(e2);
					throw new ITFEBizException("查询信息异常！", e2);
				} catch (Exception e2) {
					log.error(e2);
					throw new ITFEBizException(e2.getMessage(), e2);
				}
			}
		} else if (type.equals(MsgConstant.MSG_NO_2202)) {
			type = MsgConstant.VOUCHER_NO_2203;
			IVoucherDto2Map voucher = (IVoucherDto2Map) ContextFactory
					.getApplicationContext().getBean(
							MsgConstant.VOUCHER_DTO2MAP + type);
			List<TvPayreckBankBackDto> list2202 = (List) list.get(2);
			for (TvPayreckBankBackDto maindto : list2202) {
				List newList2202 = new ArrayList();
				newList2202.add(type);
				newList2202.add(maindto);
				voucher.tranfor(newList2202);
			}
			successcount = list2202.size();
		}
		mulitTableDto.setTotalCount(successcount);
		mulitTableDto.setErrorCount(failcount);
		mulitTableDto.setErrorList(errorList);
		return mulitTableDto;
	}

	/**
	 * 实拨资金退款(TCBS资金文件导入)voucherGenerateForPayoutBack
	 * 
	 * @generated
	 * @param list
	 * @return list
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public List voucherGenerateForPayoutBack(List list) throws ITFEBizException {

		return VoucherFactory.voucherGenerateForPayoutBack(list);
	}

	/**
	 * 查找退款信息
	 */
	public List findMainDto(TvVoucherinfoDto dto) throws ITFEBizException {
		try {
			SQLExecutor sqlExec = DatabaseFacade.getODB()
					.getSqlExecutorFactory().getSQLExecutor();
			SQLResults rs = sqlExec.runQueryCloseCon(getSQL(dto),
					TfDirectpaymsgmainDto.class);
			return (List) rs.getDtoCollection();
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(e);
		}
	}

	private String getSQL(TvVoucherinfoDto dto) {
		return " SELECT * FROM TF_DIRECTPAYMSGMAIN WHERE S_TRECODE='"
				+ dto.getStrecode()
				+ "' AND "
				+ " S_VOUCHERNO='"
				+ dto.getSvoucherno()
				+ "' "
				+ (StringUtils.isBlank(dto.getScreatdate()) ? ""
						: "AND S_VOUDATE='" + dto.getScreatdate() + "' ")
				+ " AND S_STATUS='"
				+ DealCodeConstants.DEALCODE_ITFE_SUCCESS
				+ "' "
				+ (dto.getNmoney() == null ? "" : " AND N_PAYAMT="
						+ dto.getNmoney())
				+ " AND EXISTS ( SELECT 1 FROM "
				+ " TV_VOUCHERINFO WHERE TV_VOUCHERINFO.S_DEALNO=TF_DIRECTPAYMSGMAIN.I_VOUSRLNO AND TV_VOUCHERINFO.S_VTCODE"
				+ " ='"
				+ MsgConstant.VOUCHER_NO_5201
				+ "' AND TV_VOUCHERINFO.S_STATUS='"
				+ DealCodeConstants.VOUCHER_SUCCESS
				+ "')"
				+ " UNION ALL "
				+ " SELECT * FROM TF_DIRECTPAYMSGMAIN WHERE S_TRECODE='"
				+ dto.getStrecode()
				+ "' AND "
				+ " S_VOUCHERNO='"
				+ dto.getSvoucherno()
				+ "' "
				+ (StringUtils.isBlank(dto.getScreatdate()) ? ""
						: "AND S_VOUDATE='" + dto.getScreatdate() + "' ")
				+ " AND S_STATUS='"
				+ DealCodeConstants.DEALCODE_ITFE_SUCCESS
				+ "' "
				+ (dto.getNmoney() == null ? "" : " AND N_PAYAMT="
						+ dto.getNmoney())
				+ " AND NOT EXISTS ( SELECT 1 FROM "
				+ " TV_VOUCHERINFO WHERE TV_VOUCHERINFO.S_DEALNO=TF_DIRECTPAYMSGMAIN.I_VOUSRLNO )";
	}

	/**
	 * 复制5201主表数据,用于前置生成退款
	 * 
	 * @param dto
	 * @param maindto5201
	 * @return
	 * @throws JAFDatabaseException
	 */
	private static TvPayreckBankBackDto copyMain5201to2202(
			TvVoucherinfoDto dto, TfDirectpaymsgmainDto maindto5201)
			throws JAFDatabaseException {
		TvPayreckBankBackDto maindto = new TvPayreckBankBackDto();
		maindto.setStrano(maindto5201.getSdealno());// 申请划款凭证Id
		maindto.setIvousrlno(maindto5201.getIvousrlno());// 凭证流水号
		maindto.setSid(maindto5201.getSid());// 申请划款凭证Id
		maindto.setSadmdivcode(maindto5201.getSadmdivcode());// 行政区划代码
		maindto.setSofyear(maindto5201.getSstyear());// 业务年度
		maindto.setSvtcode(dto.getSvtcode());// 凭证类型编号
		maindto.setDvoudate(CommonUtil.strToDate(dto.getScreatdate())); // 凭证日期
		maindto.setSvouno(dto.getSvoucherno());// 凭证号
		maindto.setSbookorgcode(maindto5201.getSorgcode());// 核算主体代码
		maindto.setStrecode(maindto5201.getStrecode()); // 国库主体代码
		maindto.setSfinorgcode(maindto5201.getSfinorgcode());// 财政机关代码
		maindto.setSbgttypecode(maindto5201.getSbgttypecode());// 预算类型编码
		maindto.setSbgttypename(maindto5201.getSbusinesstypename());// 预算类型名称
		maindto.setSfundtypecode(maindto5201.getSfundtypecode());// 资金性质编码
		maindto.setSfundtypename(maindto5201.getSfundtypename());// 资金性质名称
		maindto.setSpaymode(maindto5201.getSpaytypecode());
		maindto.setSpaytypecode(maindto5201.getSpaytypecode());// 支付方式编码
		maindto.setSpaytypename(maindto5201.getSpaytypename());// 支付方式名称
		maindto.setSpayeeacct(maindto5201.getSpayeeacctno());// 收款人账号
		maindto.setSpayeename(maindto5201.getSpayeeacctname());// 收款人账户名称
		maindto.setSagentacctbankname(maindto5201.getSpayeeacctbankname());// 收款银行
		maindto.setSpayeracct(maindto5201.getSpayacctno());// 付款账号
		maindto.setSpayername(maindto5201.getSpayacctname());// 付款账户名称
		maindto.setSclearacctbankname(maindto5201.getSpayacctbankname());// 付款银行
		maindto.setFamt(maindto5201.getNpayamt());// 汇总清算金额
		maindto.setSpaybankname(maindto5201.getSpayacctbankname());// 代理银行名称
		maindto.setSagentbnkcode(maindto5201.getSpayeeacctbankno());// 代理银行行号
		maindto.setSremark(maindto5201.getSdemo());// 摘要
		maindto.setSmoneycorpcode("");// 金融机构编码
		maindto.setShold1(maindto5201.getShold1());// 预留字段1
		maindto.setShold2(maindto5201.getShold2());// 预留字段2
		maindto.setDentrustdate(DateUtil.currentDate());// 委托日期
		maindto.setDorientrustdate(CommonUtil.strToDate(maindto5201
				.getScommitdate()));// 原委托日期
		maindto.setSpackno(maindto5201.getSdealno());// 包流水号
		maindto.setSoritrano(maindto5201.getSdealno());// 原交易流水号
		maindto.setDacceptdate(CommonUtil.strToDate(dto.getScreatdate()));// 接收日期
		maindto.setStrimsign(MsgConstant.TIME_FLAG_NORMAL);// 调整期标志
		maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);// 预算种类(默认预算内)
		maindto.setDorivoudate(CommonUtil.strToDate(maindto5201.getSvoudate()));// 原凭证日期
		maindto.setSorivouno(maindto5201.getSvoucherno());// 原凭证编号
		maindto.setSpaydictateno("no");// 大额支付退款交易序号
		maindto.setSpaymsgno("111");// 支付报文编号
		maindto.setDpayentrustdate(CommonUtil
				.strToDate(maindto5201.getShold1()));// 支付委托日期
		maindto.setSpaysndbnkno(maindto5201.getSpayeeacctbankno());// 支付发起行行号
		maindto.setSfilename(maindto5201.getSfilename());
		maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);// 状态 处理中
		maindto.setIstatinfnum(0);
		maindto.setSxpaysndbnkno("");// 支付发起行行号
		maindto.setSaddword(maindto5201.getSdemo());// 附言
		maindto.setSbackflag(maindto5201.getSbackflag());
		maindto.setSrefundtype(maindto5201.getSrefundtype());
		maindto.setNbackmoney(maindto5201.getNpayamt());
		maindto.setSbckreason(maindto5201.getSdemo());
//		maindto.setSxcleardate(CommonUtil.strToDate(maindto5201.getShold1()));// 清算日期
		maindto.setSxcleardate(CommonUtil.strToDate(maindto5201.getSxpaydate()));// 清算日期
		maindto.setSxpayamt(maindto5201.getNbackmoney());
		maindto.setTsupdate(new Timestamp(new java.util.Date().getTime()));// 系统时间
		DatabaseFacade.getODB().create(maindto);
		return maindto;
	}

	/**
	 * * 复制5201明细数据
	 * 
	 * @param maindto5201
	 * @return
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException
	 */
	private static TvPayreckBankBackListDto copySub5201to2202(
			TfDirectpaymsgmainDto maindto5201) throws ITFEBizException,
			JAFDatabaseException {
		List<IDto> subdtoLis = PublicSearchFacade.findSubDtoByMain(maindto5201);
		List<IDto> list = new ArrayList<IDto>();
		for (IDto tmpdto : subdtoLis) {
			TfDirectpaymsgsubDto subdto5201 = (TfDirectpaymsgsubDto) tmpdto;
			TvPayreckBankBackListDto subdto = new TvPayreckBankBackListDto();
			subdto.setIseqno(Integer.parseInt(subdto5201.getIseqno() + ""));
			subdto.setSid(subdto5201.getSid());
			subdto.setSacctprop(MsgConstant.ACCT_PROP_ZERO);// 账户性质
			subdto.setIvousrlno(subdto5201.getIvousrlno());// 凭证流水号
			subdto.setSvoucherno(subdto5201.getSvoucherbillno());// 子表明细序号
			subdto.setSbdgorgcode(subdto5201.getSagencycode());// 一级预算单位编码
			subdto.setSsupdepname(subdto5201.getSagencyname());// 一级预算单位名称
			subdto.setSfuncbdgsbtcode(subdto5201.getSexpfunccode());// 支出功能分类科目编码
			subdto.setSexpfuncname(subdto5201.getSexpfuncname());// 支出功能分类科目名称
			subdto.setSecnomicsubjectcode(subdto5201.getSexpecocode());// 经济科目代码
			subdto.setFamt(subdto5201.getNpayamt());// 支付金额
			subdto.setSpaysummaryname(subdto5201.getSremark());// 摘要名称
			subdto.setShold1(subdto5201.getShold1());// 预留字段1
			subdto.setShold2(subdto5201.getShold2());// 预留字段2
			subdto.setShold3(subdto5201.getShold3());// 预留字段3
			subdto.setShold4(subdto5201.getShold4());// 预留字段4
			subdto.setTsupdate(new Timestamp(new java.util.Date().getTime()));// 更新时间
			subdto.setSorivouno(maindto5201.getSvoucherno());// 原支付凭证单号
			subdto.setSorivoudetailno(subdto5201.getSvoucherbillno());// 原支付凭证明细单号
			subdto.setDorivoudate(CommonUtil.strToDate(maindto5201
					.getSvoudate()));
			subdto.setShold1(subdto5201.getSvoucherno());
			list.add(subdto);
		}
		if (list.size() > 0) {
			DatabaseFacade.getODB().create(CommonUtil.listTArray(list));
		}
		return null;

	}

}
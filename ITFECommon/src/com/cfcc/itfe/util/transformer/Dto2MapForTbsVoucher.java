package com.cfcc.itfe.util.transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfFundAppropriationDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

public class Dto2MapForTbsVoucher {

	private static Log logger = LogFactory.getLog(Dto2MapForTbsVoucher.class);

	/**
	 * 报文信息转化
	 * 
	 * @param dto
	 * @param orgcode
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(TvVoucherinfoDto vdto) throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> cfxMap = new HashMap<String, Object>();
			HashMap<String, Object> headMap = new HashMap<String, Object>();
			HashMap<String, Object> msgMap = new HashMap<String, Object>();

			// 设置报文节点 CFX
			map.put("cfx", cfxMap);
			cfxMap.put("HEAD", headMap);
			cfxMap.put("MSG", msgMap);

			// 报文头 HEAD
			headMap.put("VER", MsgConstant.MSG_HEAD_VER);
			headMap.put("SRC", "111111111111");
			headMap.put("DES", ITFECommonConstant.TBS_TREANDBANK.get(vdto.getStrecode()) + "000000000");
			headMap.put("APP", "TCQS");
			headMap.put("MsgNo", MsgConstant.MSG_TBS_NO_1000);
			headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
			if(vdto.getSpackno() == null){ //用于区分是对账补发的1000报文，还是第一次发送的资金报文。第一次发送时包流水号为空
				vdto.setSpackno(SequenceGenerator
						.changePackNoForLocal(SequenceGenerator.getNextByDb2(
								SequenceName.FILENAME_PACKNO_REF_SEQ,
								SequenceName.TRAID_SEQ_CACHE,
								SequenceName.TRAID_SEQ_STARTWITH,
								MsgConstant.SEQUENCE_MAX_DEF_VALUE)));
			}else{ //补发的资金报文，要将原资金表(TF_FUND_APPROPRIATION)的msgid和msgref更新成现在补发的msgid和msgref，因为收到的清算回执会根据原msgid更新资金表状态
				TfFundAppropriationDto fundDto = new TfFundAppropriationDto();
				fundDto.setStrecode(vdto.getStrecode());
				fundDto.setSpackno(vdto.getSpackno());
//				fundDto.setSentrustdate(vdto.getScreatdate());
				List<TfFundAppropriationDto> tmpList = CommonFacade.getODB().findRsByDto(fundDto);
				if (null == tmpList || tmpList.size()==0) {
					logger.error("未查询到原发送的资金报文");
					throw new ITFEBizException("未查询到原发送的资金报文");
				}
				fundDto = tmpList.get(0);
				fundDto.setSmsgid(msgid);
				fundDto.setSmsgref(msgid);
				DatabaseFacade.getODB().update(fundDto);
			}

			if (MsgConstant.VOUCHER_NO_2301.equals(vdto.getSvtcode())) {
				tranfor2301(map, vdto);
			} else if (MsgConstant.VOUCHER_NO_2302.equals(vdto.getSvtcode())) {
				tranfor2302(map, vdto);
			} else if (MsgConstant.VOUCHER_NO_5207.equals(vdto.getSvtcode())) {
				tranfor5207(map, vdto);
			} else if (MsgConstant.VOUCHER_NO_5209.equals(vdto.getSvtcode())) {
				tranfor5209(map, vdto);
			}else if ("5230".equals(vdto.getSvtcode())){//tbs导入的退库
				tranfor5230(map, vdto);
			} else {
				logger.error("报文类型不匹配！");
				throw new ITFEBizException("报文类型不匹配！");
			}
			//资金清算中更新索引表状态
			vdto.setSext1("40");
			vdto.setSdemo("资金清算中");
			DatabaseFacade.getODB().update(vdto);
			return map;
		} catch (SequenceException e) {
			logger.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		} catch (JAFDatabaseException e) {
			logger.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		} catch (ValidateException e) {
			logger.error("未查询到原发送的资金报文");
			throw new ITFEBizException("未查询到原发送的资金报文");
		}
	}
	
	/**
	 * 报文信息转化
	 * 
	 * @param dto
	 * @param orgcode
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(TfFundAppropriationDto vdto) throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> cfxMap = new HashMap<String, Object>();
			HashMap<String, Object> headMap = new HashMap<String, Object>();
			HashMap<String, Object> msgMap = new HashMap<String, Object>();

			// 设置报文节点 CFX
			map.put("cfx", cfxMap);
			cfxMap.put("HEAD", headMap);
			cfxMap.put("MSG", msgMap);

			// 报文头 HEAD
			headMap.put("VER", MsgConstant.MSG_HEAD_VER);
			headMap.put("SRC", "111111111111");
			headMap.put("DES", vdto.getSpayeebankno().substring(0, 3) + "000000000");
			headMap.put("APP", "TCQS");
			headMap.put("MsgNo", MsgConstant.MSG_TBS_NO_1000);
			headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
			vdto.setSmsgid(msgid);
			vdto.setSmsgref(msgid);
			vdto.setSpackno(SequenceGenerator
					.changePackNoForLocal(SequenceGenerator.getNextByDb2(
							SequenceName.FILENAME_PACKNO_REF_SEQ,
							SequenceName.TRAID_SEQ_CACHE,
							SequenceName.TRAID_SEQ_STARTWITH,
							MsgConstant.SEQUENCE_MAX_DEF_VALUE)));
			vdto.setStrano(vdto.getSpackno());
			tranfor5209(map, vdto);
			//资金清算中更新索引表状态
			DatabaseFacade.getODB().create(vdto);
			return map;
		} catch (SequenceException e) {
			logger.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		} catch (JAFDatabaseException e) {
			logger.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		}
	}

	private static void tranfor5209(Map map, TfFundAppropriationDto vDto) throws ITFEBizException {
			Map<String, String> BatchHead1000 = new HashMap<String, String>();
			BatchHead1000.put("BillOrg", vDto.getSbillorg());
			BatchHead1000.put("EntrustDate", vDto.getSentrustdate());
			BatchHead1000.put("PackNo", vDto.getSpackno());
			BatchHead1000.put("TreCode", vDto.getStrecode());
			BatchHead1000.put("AllNum", "1");
			BatchHead1000.put("AllAmt", MtoCodeTrans.transformString(vDto.getNallamt()));
			BatchHead1000.put("PayoutVouType", "2");

			Map<String, Object> msgMap = (Map<String, Object>) ((Map) map
					.get("cfx")).get("MSG");
			msgMap.put("BatchHead1000", BatchHead1000);
			List list = new ArrayList();
			Map<String, String> BillSend1000 = new HashMap<String, String>();
			BillSend1000.put("TraNo", vDto.getSpackno()); // 交易流水号
			BillSend1000.put("VouNo", vDto.getSvouno()); // 凭证编号
			BillSend1000.put("VouDate", vDto.getSvoudate()); // 凭证日期
			BillSend1000.put("PayerAcct", vDto.getSpayeracct()); // 付款人账号
			BillSend1000.put("PayerName", vDto.getSpayername()); // 付款人名称
			BillSend1000.put("PayerAddr", ""); // 付款人地址
			BillSend1000.put("Amt", MtoCodeTrans.transformString(vDto.getNamt())); // 金额
			BillSend1000
					.put("PayeeBankNo", vDto.getSpayeebankno()); // 收款行行号
			BillSend1000.put("PayeeOpBkNo", vDto.getSpayeeopbkno()); // 收款人开户行行号
			BillSend1000.put("PayeeAcct", vDto.getSpayeeacct()); // 收款人账号
			BillSend1000.put("PayeeName", vDto.getSpayeename()); // 收款人名称
			BillSend1000.put("PayReason", ""); // 拨款或退库原因
			BillSend1000.put("BudgetSubjectCode", ""); // 预算科目代码
			BillSend1000.put("AddWord", ""); // 附言
			BillSend1000.put("OfYear", vDto.getSofyear()); // 所属年度
			BillSend1000.put("Flag", getBankTypeFlag(vDto.getSpayeebankno(), ITFECommonConstant.TBS_TREANDBANK.get(vDto.getStrecode()))); // 收款方标识
			list.add(BillSend1000);
			msgMap.put("BillSend1000", list);
	}
	
	
	private static void tranfor5209(Map map, TvVoucherinfoDto vDto) throws ITFEBizException {
		try {
			TvDwbkDto dwbkDto = new TvDwbkDto();
			dwbkDto.setSbizno(vDto.getSdealno());
			dwbkDto = (TvDwbkDto) CommonFacade.getODB().findRsByDto(dwbkDto).get(0);
			Map<String, String> BatchHead1000 = new HashMap<String, String>();
			BatchHead1000.put("BillOrg", vDto.getSpaybankcode());
			BatchHead1000.put("EntrustDate", TimeFacade.getCurrentStringTime());
			BatchHead1000.put("PackNo", vDto.getSpackno());
			BatchHead1000.put("TreCode", vDto.getStrecode());
			BatchHead1000.put("ChangeNo", "");
			BatchHead1000.put("AllNum", "1");
			BatchHead1000.put("AllAmt", MtoCodeTrans.transformString(dwbkDto.getFamt()));
			BatchHead1000.put("PayoutVouType", "2");

			Map<String, Object> msgMap = (Map<String, Object>) ((Map) map
					.get("cfx")).get("MSG");
			msgMap.put("BatchHead1000", BatchHead1000);
			List list = new ArrayList();
			Map<String, String> BillSend1000 = new HashMap<String, String>();
			BillSend1000.put("TraNo", vDto.getSpackno()); // 交易流水号
			BillSend1000.put("VouNo", vDto.getSvoucherno()); // 凭证编号
			BillSend1000.put("VouDate", vDto.getSext4()); // 凭证日期
			BillSend1000.put("PayerAcct", dwbkDto.getSpayacctno()); // 付款人账号
			BillSend1000.put("PayerName", dwbkDto.getSpayacctname()); // 付款人名称
			BillSend1000.put("PayerAddr", ""); // 付款人地址
			BillSend1000.put("Amt", MtoCodeTrans.transformString(dwbkDto.getFamt())); // 金额
			BillSend1000.put("PayeeBankNo", dwbkDto.getSpayeeopnbnkno()); // 收款行行号
			BillSend1000.put("PayeeOpBkNo", dwbkDto.getSpayeeopnbnkno()); // 收款人开户行行号
			BillSend1000.put("PayeeAcct", dwbkDto.getSpayeeacct()); // 收款人账号
			BillSend1000.put("PayeeName", dwbkDto.getSpayeename()); // 收款人名称
			BillSend1000.put("PayReason", ""); // 拨款或退库原因
			BillSend1000.put("BudgetSubjectCode", dwbkDto.getSbdgsbtcode()); // 预算科目代码
			BillSend1000.put("AddWord", ""); // 附言
			BillSend1000.put("OfYear", vDto.getSstyear()); // 所属年度
			BillSend1000.put("Flag", getBankTypeFlag(vDto.getSpaybankcode(), ITFECommonConstant.TBS_TREANDBANK.get(vDto.getStrecode()))); // 收款方标识
			list.add(BillSend1000);
			msgMap.put("BillSend1000", list);
			dwbkDto.setSpackageno(vDto.getSpackno());
			DatabaseFacade.getODB().update(dwbkDto);
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			logger.equals(e);
			throw new ITFEBizException("查询业务数据失败！", e);
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.equals(e);
			throw new ITFEBizException("查询业务数据失败！", e);
		}
	}
	
	/**
	 * TBS导入的退库
	 * @param map
	 * @param vDto
	 * @throws ITFEBizException
	 */
	private static void tranfor5230(Map map, TvVoucherinfoDto vDto) throws ITFEBizException {
		try {
			TvDwbkDto dwbkDto = new TvDwbkDto();
			dwbkDto.setSbizno(vDto.getSdealno());
			dwbkDto = (TvDwbkDto) CommonFacade.getODB().findRsByDto(dwbkDto).get(0);
			Map<String, String> BatchHead1000 = new HashMap<String, String>();
			BatchHead1000.put("BillOrg", dwbkDto.getStaxorgcode());
			BatchHead1000.put("EntrustDate", TimeFacade.getCurrentStringTime());
			BatchHead1000.put("PackNo", vDto.getSpackno());
			BatchHead1000.put("TreCode", vDto.getStrecode());
			BatchHead1000.put("ChangeNo", "");
			BatchHead1000.put("AllNum", "1");
			BatchHead1000.put("AllAmt", MtoCodeTrans.transformString(dwbkDto.getFamt()));
			BatchHead1000.put("PayoutVouType", "2");

			Map<String, Object> msgMap = (Map<String, Object>) ((Map) map
					.get("cfx")).get("MSG");
			msgMap.put("BatchHead1000", BatchHead1000);
			List list = new ArrayList();
			Map<String, String> BillSend1000 = new HashMap<String, String>();
			BillSend1000.put("TraNo", vDto.getSpackno()); // 交易流水号
			BillSend1000.put("VouNo", vDto.getSvoucherno()); // 凭证编号
			BillSend1000.put("VouDate", vDto.getSext4()); // 凭证日期
			BillSend1000.put("PayerAcct", dwbkDto.getSpayacctno()); // 付款人账号
			BillSend1000.put("PayerName", dwbkDto.getSpayacctname()); // 付款人名称
			BillSend1000.put("PayerAddr", ""); // 付款人地址
			BillSend1000.put("Amt", MtoCodeTrans.transformString(dwbkDto.getFamt())); // 金额
			BillSend1000.put("PayeeBankNo", dwbkDto.getSrecbankname()); // 收款行行号
			BillSend1000.put("PayeeOpBkNo", dwbkDto.getSpayeeopnbnkno()); // 收款人开户行行号
			BillSend1000.put("PayeeAcct", dwbkDto.getSpayeeacct()); // 收款人账号
			BillSend1000.put("PayeeName", dwbkDto.getSpayeename()); // 收款人名称
			BillSend1000.put("PayReason", dwbkDto.getSreturnreasonname()); // 拨款或退库原因
			BillSend1000.put("BudgetSubjectCode", dwbkDto.getSbdgsbtcode()); // 预算科目代码
			BillSend1000.put("AddWord", dwbkDto.getSreturnreasonname()); // 附言
			BillSend1000.put("OfYear", vDto.getSstyear()); // 所属年度
			BillSend1000.put("Flag", getBankTypeFlag(vDto.getSpaybankcode(), ITFECommonConstant.TBS_TREANDBANK.get(vDto.getStrecode()))); // 收款方标识
			list.add(BillSend1000);
			msgMap.put("BillSend1000", list);
			dwbkDto.setSpackageno(vDto.getSpackno());
			DatabaseFacade.getODB().update(dwbkDto);
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			logger.equals(e);
			throw new ITFEBizException("查询业务数据失败！", e);
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.equals(e);
			throw new ITFEBizException("查询业务数据失败！", e);
		}
	}
	

	private static void tranfor2301(Map map, TvVoucherinfoDto vDto)
			throws ITFEBizException {
		SQLExecutor sqlExecutor = null;
		try {
			TvPayreckBankDto tvPayreckBankDto = new TvPayreckBankDto();
			tvPayreckBankDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
			tvPayreckBankDto = (TvPayreckBankDto) CommonFacade.getODB()
					.findRsByDto(tvPayreckBankDto).get(0);
			Map<String, String> BatchHead1000 = new HashMap<String, String>();
			BatchHead1000.put("BillOrg", tvPayreckBankDto.getSagentbnkcode());
			BatchHead1000.put("EntrustDate", TimeFacade.getCurrentStringTime());
			BatchHead1000.put("PackNo", vDto.getSpackno());
			BatchHead1000.put("TreCode", vDto.getStrecode());
			BatchHead1000.put("ChangeNo", "");
			BatchHead1000.put("AllNum", "1");
			BatchHead1000.put("AllAmt", MtoCodeTrans.transformString(vDto
					.getNmoney()));
			BatchHead1000.put("PayoutVouType", "3");

			Map<String, Object> msgMap = (Map<String, Object>) ((Map) map
					.get("cfx")).get("MSG");
			msgMap.put("BatchHead1000", BatchHead1000);
			List list = new ArrayList();
			Map<String, String> BillSend1000 = new HashMap<String, String>();
			BillSend1000.put("TraNo", vDto.getSpackno()); // 交易流水号
			BillSend1000.put("VouNo", vDto.getSvoucherno()); // 凭证编号
			BillSend1000.put("VouDate", DateUtil.date2String2(tvPayreckBankDto
					.getDvoudate())); // 凭证日期
			BillSend1000.put("PayerAcct", tvPayreckBankDto.getSpayeracct()); // 付款人账号
			BillSend1000.put("PayerName", tvPayreckBankDto.getSpayername()); // 付款人名称
			BillSend1000.put("PayerAddr", ""); // 付款人地址
			BillSend1000.put("Amt", MtoCodeTrans
					.transformString(tvPayreckBankDto.getFamt())); // 金额
			BillSend1000
					.put("PayeeBankNo", tvPayreckBankDto.getSagentbnkcode()); // 收款行行号
			BillSend1000.put("PayeeOpBkNo", tvPayreckBankDto
					.getSagentbnkcode()); // 收款人开户行行号
			BillSend1000.put("PayeeAcct", tvPayreckBankDto.getSpayeeacct()); // 收款人账号
			BillSend1000.put("PayeeName", tvPayreckBankDto.getSpayeename()); // 收款人名称
			BillSend1000.put("PayReason", ""); // 拨款或退库原因
			BillSend1000.put("BudgetSubjectCode", ""); // 预算科目代码
			BillSend1000.put("AddWord", ""); // 附言
			BillSend1000.put("OfYear", vDto.getSstyear()); // 所属年度
			BillSend1000.put("Flag", getBankTypeFlag(vDto.getSpaybankcode(), ITFECommonConstant.TBS_TREANDBANK.get(vDto.getStrecode()))); // 收款方标识
			list.add(BillSend1000);
			msgMap.put("BillSend1000", list);
			String sql = "UPDATE TV_PAYRECK_BANK SET S_PACKNO = ?,S_TRANO = ?  WHERE D_ENTRUSTDATE = ? AND S_BOOKORGCODE = ?  AND S_AGENTBNKCODE = ? AND S_VOUNO = ? AND F_AMT = ?  ";
			sqlExecutor = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExecutor.addParam(vDto.getSpackno());
			sqlExecutor.addParam(vDto.getSpackno());
			sqlExecutor.addParam(tvPayreckBankDto.getDentrustdate());
			sqlExecutor.addParam(tvPayreckBankDto.getSbookorgcode());
			sqlExecutor.addParam(tvPayreckBankDto.getSagentbnkcode());
			sqlExecutor.addParam(tvPayreckBankDto.getSvouno());
			sqlExecutor.addParam(tvPayreckBankDto.getFamt());
			sqlExecutor.runQuery(sql);
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			logger.equals(e);
			throw new ITFEBizException("查询业务数据失败！", e);
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.equals(e);
			throw new ITFEBizException("查询业务数据失败！", e);
		}finally{
			if(null != sqlExecutor){
				sqlExecutor.closeConnection();
			}
		}
	}

	private static void tranfor2302(Map map, TvVoucherinfoDto vDto)
			throws ITFEBizException {
		TvPayreckBankBackDto tvPayreckBankBackDto = new TvPayreckBankBackDto();
		tvPayreckBankBackDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
		try {
			tvPayreckBankBackDto = (TvPayreckBankBackDto) CommonFacade.getODB()
					.findRsByDto(tvPayreckBankBackDto).get(0);
			Map<String, String> BatchHead1000 = new HashMap<String, String>();
			BatchHead1000.put("BillOrg", tvPayreckBankBackDto
					.getSagentbnkcode());
			BatchHead1000.put("EntrustDate", TimeFacade.getCurrentStringTime());
			BatchHead1000.put("PackNo", vDto.getSpackno());
			BatchHead1000.put("TreCode", vDto.getStrecode());
			BatchHead1000.put("AllNum", "1");
			BatchHead1000.put("AllAmt", MtoCodeTrans
					.transformString(tvPayreckBankBackDto.getFamt()));
			BatchHead1000.put("PayoutVouType", "3");

			Map<String, Object> msgMap = (Map<String, Object>) ((Map) map
					.get("cfx")).get("MSG");
			msgMap.put("BatchHead1000", BatchHead1000);
			List list = new ArrayList();
			Map<String, String> BillSend1000 = new HashMap<String, String>();
			BillSend1000.put("TraNo", vDto.getSpackno()); // 交易流水号
			BillSend1000.put("VouNo", vDto.getSvoucherno()); // 凭证编号
			BillSend1000.put("VouDate", DateUtil
					.date2String2(tvPayreckBankBackDto.getDvoudate())); // 凭证日期
			BillSend1000.put("PayerAcct", tvPayreckBankBackDto.getSpayeracct()); // 付款人账号
			BillSend1000.put("PayerName", tvPayreckBankBackDto.getSpayername()); // 付款人名称
			BillSend1000.put("PayerAddr", ""); // 付款人地址
			BillSend1000.put("Amt", MtoCodeTrans
					.transformString(tvPayreckBankBackDto.getFamt())); // 金额
			BillSend1000.put("PayeeBankNo", tvPayreckBankBackDto.getSagentbnkcode()); // 收款行行号
			BillSend1000.put("PayeeOpBkNo", tvPayreckBankBackDto
					.getSagentacctbankname()); // 收款人开户行行号
			BillSend1000.put("PayeeAcct", tvPayreckBankBackDto.getSpayeeacct()); // 收款人账号
			BillSend1000.put("PayeeName", tvPayreckBankBackDto.getSpayeename()); // 收款人名称
			BillSend1000.put("PayReason", ""); // 拨款或退库原因
			BillSend1000.put("BudgetSubjectCode", ""); // 预算科目代码
			BillSend1000.put("AddWord", ""); // 附言
			BillSend1000.put("OfYear", vDto.getSstyear()); // 所属年度
			BillSend1000.put("Flag", getBankTypeFlag(vDto.getSpaybankcode(), ITFECommonConstant.TBS_TREANDBANK.get(vDto.getStrecode()))); // 收款方标识
			list.add(BillSend1000);
			msgMap.put("BillSend1000", list);
			tvPayreckBankBackDto.setSpackno(vDto.getSpackno());
			DatabaseFacade.getODB().update(tvPayreckBankBackDto);
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			logger.equals(e);
			throw new ITFEBizException("查询业务数据失败！", e);
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.equals(e);
			throw new ITFEBizException("查询业务数据失败！", e);
		}
	}

	private static void tranfor5207(Map map, TvVoucherinfoDto vDto)
			throws ITFEBizException {
		try {
			TvPayoutmsgmainDto tvPayoutmsgmainDto = new TvPayoutmsgmainDto();
			tvPayoutmsgmainDto.setSbizno(vDto.getSdealno());
			tvPayoutmsgmainDto = (TvPayoutmsgmainDto) CommonFacade.getODB().findRsByDto(tvPayoutmsgmainDto).get(0);
			TvPayoutmsgsubDto payoutsubDto = new TvPayoutmsgsubDto();
			payoutsubDto.setSbizno(vDto.getSdealno());
			payoutsubDto = (TvPayoutmsgsubDto) CommonFacade.getODB().findRsByDto(payoutsubDto).get(0);
			Map<String, String> BatchHead1000 = new HashMap<String, String>();
			BatchHead1000.put("BillOrg", vDto.getSpaybankcode());
			BatchHead1000.put("EntrustDate", TimeFacade.getCurrentStringTime());
			BatchHead1000.put("PackNo", vDto.getSpackno());
			BatchHead1000.put("TreCode", vDto.getStrecode());
			BatchHead1000.put("ChangeNo", "");
			BatchHead1000.put("AllNum", "1");
			BatchHead1000.put("AllAmt", MtoCodeTrans
					.transformString(tvPayoutmsgmainDto.getNmoney()));
			BatchHead1000.put("PayoutVouType", "1");

			Map<String, Object> msgMap = (Map<String, Object>) ((Map) map
					.get("cfx")).get("MSG");
			msgMap.put("BatchHead1000", BatchHead1000);
			List list = new ArrayList();
			Map<String, String> BillSend1000 = new HashMap<String, String>();
			BillSend1000.put("TraNo", vDto.getSpackno()); // 交易流水号
			BillSend1000.put("VouNo", vDto.getSvoucherno()); // 凭证编号
			BillSend1000.put("VouDate", tvPayoutmsgmainDto.getSgenticketdate()); // 凭证日期
			BillSend1000.put("PayerAcct", tvPayoutmsgmainDto.getSpayeracct()); // 付款人账号
			BillSend1000.put("PayerName", tvPayoutmsgmainDto.getSpayername()); // 付款人名称
			BillSend1000.put("PayerAddr", ""); // 付款人地址
			BillSend1000.put("Amt", MtoCodeTrans
					.transformString(tvPayoutmsgmainDto.getNmoney())); // 金额
			if(tvPayoutmsgmainDto.getSpayeebankno()==null || tvPayoutmsgmainDto.getSpayeebankno().trim().equals("")){
				BillSend1000.put("PayeeBankNo", tvPayoutmsgmainDto.getSrecbankno()); // 收款行行号
			}else{
				BillSend1000.put("PayeeBankNo", tvPayoutmsgmainDto.getSpayeebankno()); // 收款行行号
			}
			BillSend1000.put("PayeeOpBkNo", tvPayoutmsgmainDto.getSrecbankno()); // 收款人开户行行号
			BillSend1000.put("PayeeAcct", tvPayoutmsgmainDto.getSrecacct()); // 收款人账号
			BillSend1000.put("PayeeName", tvPayoutmsgmainDto.getSrecname()); // 收款人名称
			BillSend1000.put("PayReason", tvPayoutmsgmainDto.getSpaysummaryname()==null?"":tvPayoutmsgmainDto.getSpaysummaryname()); // 拨款或退库原因
			BillSend1000.put("BudgetSubjectCode", payoutsubDto.getSfunsubjectcode()); // 预算科目代码
			BillSend1000.put("AddWord", tvPayoutmsgmainDto.getSpaysummaryname()==null?"":tvPayoutmsgmainDto.getSpaysummaryname()); // 附言
			BillSend1000.put("OfYear", vDto.getSstyear()); // 所属年度
			BillSend1000.put("Flag", getRecvbankFlag(vDto)); // 收款方标识
			list.add(BillSend1000);
			msgMap.put("BillSend1000", list);
			tvPayoutmsgmainDto.setSpackageno(vDto.getSpackno());
			DatabaseFacade.getODB().update(tvPayoutmsgmainDto);
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			logger.equals(e);
			throw new ITFEBizException("查询业务数据失败！", e);
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.equals(e);
			throw new ITFEBizException("查询业务数据失败！", e);
		}
	}
	
	/**
	 * 实拨判断收款行是本行还是还是其它行
	 * @param vDto
	 * @return
	 * @throws ITFEBizException 
	 */
	private static String getRecvbankFlag(TvVoucherinfoDto vDto) throws ITFEBizException{
		TvPayoutmsgmainDto payDto = new TvPayoutmsgmainDto();
		payDto.setSbizno(vDto.getSdealno());
		try {
			List list = CommonFacade.getODB().findRsByDtoWithUR(payDto);
			payDto = (TvPayoutmsgmainDto)list.get(0);
			return getBankTypeFlag(payDto.getSrecbankno(),ITFECommonConstant.TBS_TREANDBANK.get(vDto.getStrecode()));
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查询业务数据失败！", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("查询业务数据失败！", e);
		}
	}
	
	private static String getBankTypeFlag(String desOrg,String payBankCode){
		if(desOrg.substring(0, 3).equals(payBankCode.substring(0, 3))){
			return "1";	//1-本行业务
		}else{
			return "2";	//2-同城跨行
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

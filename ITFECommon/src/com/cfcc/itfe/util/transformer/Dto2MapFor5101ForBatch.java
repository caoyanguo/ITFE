package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * 实拨资金额度转化
 * 
 * @author zhouchuan
 * 
 */
public class Dto2MapFor5101ForBatch {

	private static Log logger = LogFactory.getLog(Dto2MapFor5101ForBatch.class);
	
	/**
	 * DTO转化XML报文(实拨资金业务)
	 * 
	 * @param List
	 *            <TvPayoutmsgmainDto> list 发送报文对象集合
	 * @param String
	 *            orgcode 机构代码
	 * @param String
	 *            filename 文件名称
	 * @param String
	 *            packno 包流水号
	 * @param boolean isRepeat 是否重发报文
	 * @return
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException 
	 */
	public static Map tranfor(TvPayoutmsgmainDto maindto, List<TvPayoutmsgsubDto> subList,String orgcode, String filename, String packno,boolean isRepeat) throws ITFEBizException, JAFDatabaseException {
		if (null == maindto || null==subList||subList.size()==0) {
			throw new ITFEBizException("要转化的对象为空,请确认!");
		}

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
		if(orgcode!=null&&orgcode.startsWith("1702"))
			headMap.put("SRC", ITFECommonConstant.SRCCITY_NODE);
		else
			headMap.put("SRC", ITFECommonConstant.SRC_NODE);
		headMap.put("DES", ITFECommonConstant.DES_NODE);
		headMap.put("APP", MsgConstant.MSG_HEAD_APP);
		headMap.put("MsgNo", MsgConstant.MSG_NO_5101);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		String tmpPackNo;
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
			tmpPackNo = SequenceGenerator
			.changePackNoForLocal(SequenceGenerator.getNextByDb2(
					SequenceName.FILENAME_PACKNO_REF_SEQ,
					SequenceName.TRAID_SEQ_CACHE,
					SequenceName.TRAID_SEQ_STARTWITH,
					MsgConstant.SEQUENCE_MAX_DEF_VALUE));
		} catch (SequenceException e) {
			logger.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		}
		
		// 设置报文消息体 MSG
		HashMap<String, Object> head5101Map = new HashMap<String, Object>();
		head5101Map.put("TreCode", maindto.getStrecode()); // 国库代码
		head5101Map.put("BillOrg", maindto.getSpayunit()); // 出票单位
		head5101Map.put("PayeeBankNo", subList.get(0).getSpayeebankno()); // 收款行行号
		head5101Map.put("EntrustDate", maindto.getScommitdate().replaceAll("-", "")); // 委托日期
		head5101Map.put("PackNo", tmpPackNo); // 包序号
		head5101Map.put("AllNum", String.valueOf(subList.size())); // 总笔数
		BigDecimal allamt = new BigDecimal("0.00"); // 总金额-赋值见下面
		head5101Map.put("PayoutVouType", MsgConstant.PAYOUT_VOU_TYPE_PAPER_YES); // 支出凭证类型  0、无纸凭证1、有纸凭证
		List<Object> bill5101List = new ArrayList<Object>();
		List updateList = new ArrayList();
		for (int i = 0; i < subList.size(); i++) {
			HashMap<String, Object> bill5101Map = new HashMap<String, Object>();
			List<Object> detail5101List = new ArrayList<Object>();
			
			TvPayoutmsgsubDto subdto = (TvPayoutmsgsubDto) subList.get(i);
			
			allamt = allamt.add(subdto.getNmoney());
			String  strasno = VoucherUtil.getGrantSequence();
			subdto.setSpackageno(tmpPackNo);
			subdto.setStrasno(strasno.subSequence(8, 16).toString());
			updateList.add(subdto);
			bill5101Map.put("TraNo", strasno.subSequence(8, 16)); // 交易流水号
			bill5101Map.put("VouNo", subdto.getSvoucherno()); // 明细单凭证编号
			bill5101Map.put("VouDate", maindto.getSgenticketdate().replaceAll("-", "")); // 凭证日期[开票日期]
			bill5101Map.put("PayerAcct", maindto.getSpayeracct()); // 付款人账号
			bill5101Map.put("PayerName", maindto.getSpayername()); // 付款人名称
			bill5101Map.put("PayerAddr", maindto.getSpayeraddr()); // 付款人地址
			bill5101Map.put("Amt", MtoCodeTrans.transformString(subdto.getNmoney())); // 交易金额
			bill5101Map.put("PayeeOpBkNo", subdto.getSpayeebankno()); // 收款人开户行行号
			bill5101Map.put("PayeeAcct", subdto.getSpayeeacctno()); // 收款人账号
			bill5101Map.put("PayeeName", subdto.getSpayeeacctname()); // 收款人名称
			//福建实拨资金附言问题修改
			String addword = subdto.getSpaysummaryname();// 附言demo中存放的是调拨原因或支出原因
			if ("000059100005".equals(ITFECommonConstant.SRC_NODE)&&null != addword&&!addword.equals("")) { 
				addword = addword.getBytes().length >= 60 ? addword.substring(0, 30) : addword;
				bill5101Map.put("AddWord", addword); 
			}else{
				bill5101Map.put("AddWord",subdto.getSpaysummaryname()); // 附言
			}
			bill5101Map.put("BdgOrgCode", subdto.getSagencycode()); // 预算单位代码
			bill5101Map.put("BudgetOrgName", subdto.getSagencyname()); // 预算单位名称
			bill5101Map.put("OfYear", maindto.getSofyear()); // 所属年度
			bill5101Map.put("BudgetType", maindto.getSbudgettype()); // 预算种类
			bill5101Map.put("TrimSign", maindto.getStrimflag()); // 整理期标志
			bill5101Map.put("StatInfNum", 1); // 统计信息条数
			String s = "0";
			try {
				HashMap<String,TsTreasuryDto> mapTreInfo =SrvCacheFacade.cacheTreasuryInfo(null);
			    s = mapTreInfo.get(maindto.getStrecode()).getSisuniontre();
			} catch (JAFDatabaseException e) {
				logger.error("查找国库代码异常!", e);
				throw new ITFEBizException("查找国库代码异常！", e);
			}
				HashMap<String, Object> detail5101Map = new HashMap<String, Object>();
				
				detail5101Map.put("SeqNo", subdto.getSseqno());
				detail5101Map.put("FuncBdgSbtCode", subdto.getSfunsubjectcode());
				if (StateConstant.COMMON_YES.equals(s)) {
					detail5101Map.put("EcnomicSubjectCode", subdto.getSecnomicsubjectcode());
				}else{
					detail5101Map.put("EcnomicSubjectCode", "");
				}
				detail5101Map.put("BudgetPrjCode", subdto.getSbudgetprjcode());
				detail5101Map.put("Amt", MtoCodeTrans.transformString(subdto.getNmoney()));
				detail5101List.add(detail5101Map);
			
			bill5101Map.put("Detail5101", detail5101List);
			bill5101List.add(bill5101Map);

		}
		DatabaseFacade.getODB().update(CommonUtil.listTArray(updateList));
		head5101Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead5101", head5101Map);
		msgMap.put("Bill5101", bill5101List);
		
		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

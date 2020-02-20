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
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * 实拨资金额度转化
 * 
 * @author zhouchuan
 * 
 */
public class Dto2MapFor5101 {

	private static Log logger = LogFactory.getLog(Dto2MapFor5101.class);
	
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
	 */
	public static Map tranfor(List<TvPayoutmsgmainDto> list, String orgcode, String filename, String packno,boolean isRepeat) throws ITFEBizException {
		if (null == list || list.size() == 0) {
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
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			logger.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		}

		// 设置报文消息体 MSG
		HashMap<String, Object> head5101Map = new HashMap<String, Object>();
		head5101Map.put("TreCode", list.get(0).getStrecode()); // 国库代码
		head5101Map.put("BillOrg", list.get(0).getSpayunit()); // 出票单位
		head5101Map.put("PayeeBankNo", list.get(0).getSpayeebankno()); // 收款行行号
		head5101Map.put("EntrustDate", list.get(0).getScommitdate().replaceAll("-", "")); // 委托日期
		head5101Map.put("PackNo", list.get(0).getSpackageno()); // 包序号
		head5101Map.put("AllNum", String.valueOf(list.size())); // 总笔数
		BigDecimal allamt = new BigDecimal("0.00"); // 总金额-赋值见下面
		head5101Map.put("PayoutVouType", MsgConstant.PAYOUT_VOU_TYPE_PAPER_YES); // 支出凭证类型  0、无纸凭证1、有纸凭证
		
		List<Object> bill5101List = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> bill5101Map = new HashMap<String, Object>();
			List<Object> detail5101List = new ArrayList<Object>();

			TvPayoutmsgmainDto maindto = list.get(i);
			List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(maindto);
			if (null == sublist || sublist.size() == 0) {
				throw new ITFEBizException("要转化的对象对应的子记录为空,请确认![" + list.get(i).toString() + "]");
			}
			allamt = allamt.add(maindto.getNmoney());

			bill5101Map.put("TraNo", maindto.getSdealno()); // 交易流水号
			bill5101Map.put("VouNo", maindto.getStaxticketno()); // 凭证编号
			bill5101Map.put("VouDate", maindto.getSgenticketdate().replaceAll("-", "")); // 凭证日期[开票日期]
			bill5101Map.put("PayerAcct", maindto.getSpayeracct()); // 付款人账号
			bill5101Map.put("PayerName", maindto.getSpayername()); // 付款人名称
			bill5101Map.put("PayerAddr", maindto.getSpayeraddr()); // 付款人地址
			bill5101Map.put("Amt", MtoCodeTrans.transformString(maindto.getNmoney())); // 交易金额
			bill5101Map.put("PayeeOpBkNo", maindto.getSrecbankno()); // 收款人开户行行号
			bill5101Map.put("PayeeAcct", maindto.getSrecacct()); // 收款人账号
			bill5101Map.put("PayeeName", maindto.getSrecname()); // 收款人名称
			//福建实拨资金附言问题修改
			String addword = maindto.getSdemo();// 附言demo中存放的是调拨原因或支出原因
			if ("000059100005".equals(ITFECommonConstant.SRC_NODE)&&null != addword&&!addword.equals("")) { 
				addword = addword.getBytes().length >= 60 ? addword.substring(0, 30) : addword;
				bill5101Map.put("AddWord", addword); 
			}else{
				bill5101Map.put("AddWord", maindto.getSaddword()); // 附言
			}
			bill5101Map.put("BdgOrgCode", maindto.getSbudgetunitcode()); // 预算单位代码
			bill5101Map.put("BudgetOrgName", maindto.getSunitcodename()); // 预算单位名称
			bill5101Map.put("OfYear", maindto.getSofyear()); // 所属年度
			bill5101Map.put("BudgetType", maindto.getSbudgettype()); // 预算种类
			bill5101Map.put("TrimSign", maindto.getStrimflag()); // 整理期标志
			bill5101Map.put("StatInfNum", sublist.size()); // 统计信息条数
			String s = "0";
			try {
				HashMap<String,TsTreasuryDto> mapTreInfo =SrvCacheFacade.cacheTreasuryInfo(null);
			    s = mapTreInfo.get(list.get(0).getStrecode()).getSisuniontre();
			} catch (JAFDatabaseException e) {
				logger.error("查找国库代码异常!", e);
				throw new ITFEBizException("查找国库代码异常！", e);
			}

			// 循环子信息
			for (int j = 0; j < sublist.size(); j++) {
				HashMap<String, Object> detail5101Map = new HashMap<String, Object>();
				TvPayoutmsgsubDto subdto = (TvPayoutmsgsubDto) sublist.get(j);
				
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
			}

			bill5101Map.put("Detail5101", detail5101List);
			bill5101List.add(bill5101Map);

		}

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

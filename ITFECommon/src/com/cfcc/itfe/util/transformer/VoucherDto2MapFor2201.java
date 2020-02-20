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
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * TIPS商行划款申请报文（2201）(上海扩展)
 * 直接支付（5201）批量业务生成 
 * @author hejianrong
 * @time 2014-09-19
 * 
 */
public class VoucherDto2MapFor2201 {

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor2201.class);
	
	/**
	 * DTO转化XML报文(划款申请业务)
	 * 
	 * @param List
	 *            <TvDirectpaymsgmainDto> list 发送报文对象集合
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
	public static Map tranfor(List<TfDirectpaymsgmainDto> list, String orgcode, String filename, String packno,boolean isRepeat) throws ITFEBizException {
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
		headMap.put("MsgNo", MsgConstant.APPLYPAY_DAORU);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		headMap.put("Reserve", MsgConstant.CFCCHL);
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			logger.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		}

		// 设置报文消息体 MSG
		HashMap<String, Object> head2201Map = new HashMap<String, Object>();
		head2201Map.put("AgentBnkCode", list.get(0).getSpayeeacctbankno());//代理银行行号
		head2201Map.put("FinOrgCode", list.get(0).getSfinorgcode());//财政机关代码
		head2201Map.put("TreCode", list.get(0).getStrecode()); // 国库代码
		head2201Map.put("EntrustDate", list.get(0).getScommitdate());//委托日期
		head2201Map.put("PackNo", list.get(0).getSpackageno()); // 包序号
		head2201Map.put("AllNum", String.valueOf(list.size())); // 总笔数
		BigDecimal allamt = new BigDecimal("0.00"); // 总金额-赋值见下面
		head2201Map.put("PayoutVouType", MsgConstant.PAYOUT_VOU_TYPE_PAPER_YES); // 支出凭证类型: 0、无纸凭证1、有纸凭证	
		head2201Map.put("PayMode",list.get(0).getSpaytypecode());//支付方式
		List<Object> bill2201List = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> bill2201Map = new HashMap<String, Object>();
			List<Object> detail2201List = new ArrayList<Object>();
			TfDirectpaymsgmainDto maindto = list.get(i);
			List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(maindto);
			if (null == sublist || sublist.size() == 0) {
				throw new ITFEBizException("要转化的对象对应的子记录为空,请确认![" + list.get(i).toString() + "]");
			}
			
			allamt = allamt.add(maindto.getNpayamt());
			bill2201Map.put("TraNo", maindto.getSdealno()); // 交易流水号
			bill2201Map.put("VouNo", maindto.getSvoucherno()); // 凭证编号
			bill2201Map.put("VouDate", maindto.getSvoudate()); // 凭证日期[开票日期]
			bill2201Map.put("PayerAcct", maindto.getSpayacctno());//付款人账号
			bill2201Map.put("PayerName", maindto.getSpayacctname());//付款人名称
			bill2201Map.put("PayerAddr", "");//付款人地址 
			bill2201Map.put("PayeeAcct", maindto.getSpayeeacctno());//收款人账号
			bill2201Map.put("PayeeName", maindto.getSpayeeacctname());//收款人名称
			bill2201Map.put("PayeeAddr","");//收款人地址
			bill2201Map.put("PayeeOpnBnkNo", maindto.getSpayeeacctbankno());//收款人开户行行号 
			bill2201Map.put("AddWord", maindto.getSdemo());//附言 
			bill2201Map.put("BudgetType", MsgConstant.BDG_KIND_IN);//预算种类       默认 1 预算内
			bill2201Map.put("TrimSign", MsgConstant.TIME_FLAG_NORMAL);//整理期标志
			bill2201Map.put("OfYear", maindto.getSstyear());//所属年度
			bill2201Map.put("Amt", MtoCodeTrans.transformString(maindto.getNpayamt()));//金额
			bill2201Map.put("StatInfNum", sublist.size());//统计信息条数
			String s = "0";
			 try {
				HashMap<String,TsTreasuryDto> mapTreInfo =SrvCacheFacade.cacheTreasuryInfo(null);
			    s = mapTreInfo.get(list.get(0).getStrecode()).getSisuniontre();
			} catch (JAFDatabaseException e) {
				logger.error("查找国库代码异常!", e);
				throw new ITFEBizException("查找国库代码异常！", e);
			}
			
			// 明细信息
			for (int j = 0; j < sublist.size(); j++) {
				HashMap<String, Object> detail2201Map = new HashMap<String, Object>();
				TfDirectpaymsgsubDto subdto = (TfDirectpaymsgsubDto) sublist.get(j);
				detail2201Map.put("SeqNo", subdto.getIseqno().toString());//序号
				detail2201Map.put("BdgOrgCode", subdto.getSagencycode());//预算单位代码
				detail2201Map.put("FuncSbtCode", subdto.getSexpfunccode());//功能类科目代码
				if (StateConstant.COMMON_YES.equals(s)) {//经济类科目代码
					detail2201Map.put("EcnomicSubjectCode", MtoCodeTrans.transformString(subdto.getSexpecocode()));
				}else{
					detail2201Map.put("EcnomicSubjectCode", "");
				}
				detail2201Map.put("Amt", MtoCodeTrans.transformString(subdto.getNpayamt()));//金额
				detail2201Map.put("AcctProp", MsgConstant.ACCT_PROP_ZERO);//账户性质	     默认 1 零余额 
				detail2201List.add(detail2201Map);
			}
			bill2201Map.put("Detail2201", detail2201List);
			bill2201List.add(bill2201Map);
		}
		
		head2201Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead2201", head2201Map);
		msgMap.put("Bill2201", bill2201List);
		return map;
	}	

}

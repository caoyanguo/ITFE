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
import com.cfcc.itfe.persistence.dto.TvPbcpayMainDto;
import com.cfcc.itfe.persistence.dto.TvPbcpaySubDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * 人行办理直接支付转化
 * 
 * @author zhouchuan
 * 
 */
public class Dto2MapFor5104 {

	private static Log logger = LogFactory.getLog(Dto2MapFor5104.class);

	/**
	 * DTO转化XML报文(人行办理直接支付业务)
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
	public static Map tranfor(List<TvPbcpayMainDto> list, String orgcode, String filename, String packno,boolean isRepeat) throws ITFEBizException {
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
		headMap.put("MsgNo", MsgConstant.MSG_NO_5104);
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
		HashMap<String, Object> head5104Map = new HashMap<String, Object>();
		head5104Map.put("TreCode", list.get(0).getStrecode()); // 国库代码
		head5104Map.put("BillOrg", list.get(0).getSbillorg()); // 出票单位
		head5104Map.put("PayeeBankNo", list.get(0).getSrcvbnkno()); // 收款行行号
		head5104Map.put("EntrustDate", list.get(0).getSentrustdate().replaceAll("-", "")); // 委托日期
		head5104Map.put("PackNo", list.get(0).getSpackno()); // 包序号
		head5104Map.put("AllNum", String.valueOf(list.size())); // 总笔数
		BigDecimal allamt = new BigDecimal("0.00"); // 总金额-赋值见下面
		head5104Map.put("PayoutVouType", MsgConstant.PAYOUT_VOU_TYPE_PAPER_YES); // 支出凭证类型  0、无纸凭证1、有纸凭证
		head5104Map.put("PayMode", MsgConstant.directPay); 
		
		List<Object> bill5104List = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> bill5104Map = new HashMap<String, Object>();
			List<Object> detail5104List = new ArrayList<Object>();

			TvPbcpayMainDto maindto = list.get(i);
			List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(maindto);
			if (null == sublist || sublist.size() == 0) {
				throw new ITFEBizException("要转化的对象对应的子记录为空,请确认![" + list.get(i).toString() + "]");
			}

			allamt = allamt.add(maindto.getFamt());

			bill5104Map.put("TraNo", maindto.getStrano()); // 交易流水号
			bill5104Map.put("VouNo", maindto.getSvouno()); // 凭证编号
			bill5104Map.put("VouDate", maindto.getDvoucher().replaceAll("-", "")); // 凭证日期[开票日期]
			bill5104Map.put("PayerAcct", maindto.getSpayeracct()); // 付款人账号
			
			bill5104Map.put("PayerName", maindto.getSpayername()); // 付款人名称
            if (null==maindto.getSpayeraddr()) {
        	    maindto.setSpayeraddr("");
			}
			bill5104Map.put("PayerAddr", maindto.getSpayeraddr()); // 付款人地址
			bill5104Map.put("Amt", MtoCodeTrans.transformString(maindto.getFamt())); // 交易金额
			bill5104Map.put("PayeeOpnBnkNo", maindto.getSpayeeopnbnkno()); // 收款人开户行行号
			bill5104Map.put("PayeeAcct", maindto.getSpayeeacct()); // 收款人账号
			bill5104Map.put("PayeeName", maindto.getSpayeename()); // 收款人名称
			 if (null== maindto.getSpayeeaddr()) {
	        	    maindto.setSpayeeaddr("");
				}
			bill5104Map.put("PayeeAddr", maindto.getSpayeeaddr()); // 收款人地址
			bill5104Map.put("AddWord", maindto.getSaddword()); // 附言
			bill5104Map.put("OfYear", ""+maindto.getIofyear()); // 所属年度
			bill5104Map.put("BudgetType", maindto.getCbdgkind()); // 预算种类
			 if (null== maindto.getSbdgadmtype()) {
	        	    maindto.setSbdgadmtype("");
				}
			bill5104Map.put("BdgAdmType", maindto.getSbdgadmtype()); //预算管理类型
			
			bill5104Map.put("TrimSign", maindto.getCtrimflag()); // 整理期标志
			bill5104Map.put("StatInfNum", sublist.size()); // 统计信息条数
			
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
				HashMap<String, Object> detail5104Map = new HashMap<String, Object>();
				TvPbcpaySubDto subdto = (TvPbcpaySubDto) sublist.get(j);
				
				detail5104Map.put("SeqNo", subdto.getIseqno());
				detail5104Map.put("FuncSbtCode", subdto.getSfuncsbtcode());
				 if (null== subdto.getSecosbtcode()) {
					 subdto.setSecosbtcode("");
					}
				if (StateConstant.COMMON_YES.equals(s)) {
					detail5104Map.put("EcnomicSubjectCode", subdto.getSecosbtcode());
				}else{
					detail5104Map.put("EcnomicSubjectCode", "");
				}
				detail5104Map.put("BdgOrgCode", subdto.getSbdgorgcode());
				detail5104Map.put("Amt", MtoCodeTrans.transformString(subdto.getFamt()));
				detail5104Map.put("AcctProp", StateConstant.COMMON_YES);
				detail5104List.add(detail5104Map);
			}

			bill5104Map.put("Detail5104", detail5104List);
			bill5104List.add(bill5104Map);

		}

		head5104Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead5104", head5104Map);
		msgMap.put("Bill5104", bill5104List);
		
		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

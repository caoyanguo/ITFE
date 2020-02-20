package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceSubDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * 代发财政性款项请求
 * 
 * @author wangyunbin
 * 
 */
public class Dto2MapFor5112 {

	private static Log logger = LogFactory.getLog(Dto2MapFor5112.class);
	
	/**
	 * DTO转化XML报文(代发财政性款项请求)
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
	public static Map tranfor(List<TvPayoutfinanceMainDto> list, String orgcode, String filename, String packno,boolean isRepeat) throws ITFEBizException {
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
		headMap.put("MsgNo", MsgConstant.MSG_NO_5112);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		String msgid = "";
		try {
			msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			logger.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		}

		// 设置报文消息体 MSG
		HashMap<String, Object> head5112Map = new HashMap<String, Object>();
		head5112Map.put("TreCode", list.get(0).getStrecode()); // 国库代码
		head5112Map.put("BillOrg", list.get(0).getSbillorg()); // 出票单位
		head5112Map.put("PayeeBankNo", list.get(0).getSpayeebankno()); // 收款行行号
		head5112Map.put("PayerAcct", list.get(0).getSpayeracct()); // 付款人账号
		head5112Map.put("PayerName", list.get(0).getSpayername()); // 付款人名称
		head5112Map.put("PayerAddr", list.get(0).getSpayeraddr()); // 付款人地址
		head5112Map.put("EntrustDate", list.get(0).getSentrustdate().replaceAll("-", "")); // 委托日期
		head5112Map.put("PackNo", list.get(0).getSpackageno()); // 包流水号
		BigDecimal allamt = new BigDecimal("0.00"); // 总金额-赋值见下面
		head5112Map.put("PayoutVouType", MsgConstant.PAYOUT_VOU_TYPE_PAPER_YES); // 支出凭证类型
																					// 0、无纸凭证1、有纸凭证
		HashMap<String, Object> bill5112Map = new HashMap<String, Object>();
		List<Object> detail5112List = new ArrayList<Object>();

		TvPayoutfinanceMainDto maindto = list.get(0);
		List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(maindto);
		if (null == sublist || sublist.size() == 0) {
			throw new ITFEBizException("要转化的对象对应的子记录为空,请确认![" + list.get(0).toString() + "]");
		}
		head5112Map.put("AllNum", String.valueOf(sublist.size())); // 总笔数
		allamt = maindto.getNamt();

		bill5112Map.put("VouNo", maindto.getSvouno()); // 凭证编号
		bill5112Map.put("VouDate", maindto.getSvoudate().replaceAll("-", "")); // 凭证日期[开票日期]
		bill5112Map.put("AddWord", maindto.getSaddword()); // 附言
		bill5112Map.put("BudgetType", maindto.getSbudgettype()); // 预算种类
		bill5112Map.put("BdgOrgCode", maindto.getSbdgorgcode()); // 预算单位代码
		bill5112Map.put("FuncSbtCode", maindto.getSfuncsbtcode()); // 功能类科目代码
		bill5112Map.put("EcnomicSubjectCode", maindto.getSecnomicsubjectcode()); // 经济类科目代码
		bill5112Map.put("Amt", MtoCodeTrans.transformString(maindto.getNamt())); //金额(等于明细交易金额之和)

		// 循环子信息
		for (int j = 0; j < sublist.size(); j++) {
			HashMap<String, Object> detail5112Map = new HashMap<String, Object>();
			TvPayoutfinanceSubDto subdto = (TvPayoutfinanceSubDto) sublist.get(j);
			detail5112Map.put("TraNo", subdto.getStrano());//交易流水号
			detail5112Map.put("PayeeAcct", subdto.getSpayeeacct());//收款人账号
			detail5112Map.put("PayeeName", subdto.getSpayeename());//收款人名称
			detail5112Map.put("PayeeAddr", subdto.getSpayeeaddr());//收款人地址
			detail5112Map.put("PayeeOpnBnkNo", MtoCodeTrans.transformString(subdto.getSpayeeopnbnkno()));//收款人开户行行号 
			detail5112Map.put("Amt", MtoCodeTrans.transformString(subdto.getNsubamt()));
			detail5112List.add(detail5112Map);
		}

		bill5112Map.put("Detail5112", detail5112List);


		head5112Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead5112", head5112Map);
		msgMap.put("Bill5112", bill5112Map);

		// 更新报文头的状态为处理中
		String updateFileSQL = "update TV_FILEPACKAGEREF set S_RETCODE = ? where S_ORGCODE = ? and S_TRECODE = ? and S_FILENAME = ? and S_PACKAGENO = ? ";
		SQLExecutor sqlUpdateExec;
		try {
			sqlUpdateExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlUpdateExec.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING); // 处理中
			sqlUpdateExec.addParam(orgcode);
			sqlUpdateExec.addParam(list.get(0).getStrecode());
			sqlUpdateExec.addParam(filename);
			sqlUpdateExec.addParam(packno);
			sqlUpdateExec.runQueryCloseCon(updateFileSQL);
		} catch (JAFDatabaseException e) {
			logger.error("更新报文头的时候出现异常！", e);
			throw new ITFEBizException("更新报文头的时候出现异常！", e);
		}
		
		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * 划款申请转化
 * 
 * @author zhouchuan
 * 
 */
public class Dto2MapFor2201 {

	private static Log logger = LogFactory.getLog(Dto2MapFor2201.class);

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
	public static Map tranfor(List<TvPayreckBankDto> list, String orgcode,
			String filename, String packno, boolean isRepeat)
			throws ITFEBizException {
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
		if (orgcode != null && orgcode.startsWith("1702"))
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
		head2201Map.put("AgentBnkCode", list.get(0).getSagentbnkcode());
		head2201Map.put("FinOrgCode", list.get(0).getSfinorgcode());
		head2201Map.put("TreCode", list.get(0).getStrecode()); // 国库代码
		head2201Map.put("EntrustDate", list.get(0).getDentrustdate().toString()
				.replaceAll("-", ""));
		head2201Map.put("PackNo", list.get(0).getSpackno()); // 包序号
		head2201Map.put("AllNum", String.valueOf(list.size())); // 总笔数
		BigDecimal allamt = new BigDecimal("0.00"); // 总金额-赋值见下面
		head2201Map.put("PayoutVouType", MsgConstant.PAYOUT_VOU_TYPE_PAPER_YES); // 支出凭证类型:
																					// 0、无纸凭证1、有纸凭证
		// 支付方式赋值方式修改（解决无纸化与地方横联冲突问题）
		head2201Map.put("PayMode", list.get(0).getSpaymode());// 支付方式

		/**
		 * String biztype="";
		 * 
		 * if(!filename.equals("")&&filename.length()>13){ String
		 * name=filename.toLowerCase().replace(".txt", "");
		 * if(name.length()==13){ biztype=name.substring(10, 12); }else if
		 * (name.length()==15){ biztype=name.substring(12, 14); } }else{
		 * biztype=filename; }
		 * 
		 * //不同的业务类型具有不同的支付方式（0-直接支付 1-授权支付）
		 * if(biztype.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)){
		 * head2201Map.put("PayMode", MsgConstant.directPay); }else
		 * if(biztype.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)){
		 * head2201Map.put("PayMode",MsgConstant.grantPay); }else{
		 * head2201Map.put
		 * ("PayMode",MsgConstant.directPay+MsgConstant.grantPay); }
		 **/

		List<Object> bill2201List = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> bill2201Map = new HashMap<String, Object>();
			List<Object> detail2201List = new ArrayList<Object>();

			TvPayreckBankDto maindto = list.get(i);
			//补充学江漏提交的代码
			List sublist = new ArrayList();
			if (maindto.getStrecode().startsWith("19") && !maindto.getStrecode().startsWith("1906") ) {
				SQLExecutor execDetail = null;
				String sql = "SELECT ROW_NUMBER() OVER() AS I_SEQNO,S_BDGORGCODE,S_FUNCBDGSBTCODE,S_ACCTPROP,SUM(F_AMT) AS F_AMT FROM TV_PAYRECK_BANK_LIST WHERE I_VOUSRLNO = '"
						+ maindto.getIvousrlno()
						+ "' GROUP BY S_BDGORGCODE,S_FUNCBDGSBTCODE,S_ACCTPROP";
				try {
					execDetail = DatabaseFacade.getODB()
							.getSqlExecutorFactory().getSQLExecutor();
					sublist = (List) execDetail.runQuery(sql.toString(),
							TvPayreckBankListDto.class).getDtoCollection();
				} catch (Exception e) {
					if (execDetail != null)
						execDetail.closeConnection();
					logger.error(e.getMessage(), e);
					throw new ITFEBizException(sql.toString() + "查询集中支付明细信息异常！", e);
				} finally {
					if (execDetail != null)
						execDetail.closeConnection();
				}
			} else {
				sublist = PublicSearchFacade.findSubDtoByMain(maindto);
			}
			if(ITFECommonConstant.PUBLICPARAM.contains(",bankpaysub=sum,")&&(filename!=null&&filename.endsWith(".msg")))
					sublist = tranList(sublist);
			if (null == sublist || sublist.size() == 0) {
				throw new ITFEBizException("要转化的对象对应的子记录为空,请确认![凭证编号" + list.get(i).getSvouno()+ "]");
			}else if(sublist.size()>=500)
			{
				if(ITFECommonConstant.PUBLICPARAM.contains(",bankpaysub=sum,"))
					throw new ITFEBizException("明细按预算单位和科目汇总大于500笔,请确认![凭证编号" + list.get(i).getSvouno()+ "]");
			}

			allamt = allamt.add(maindto.getFamt());

			bill2201Map.put("TraNo", maindto.getStrano()); // 交易流水号
			bill2201Map.put("VouNo", maindto.getSvouno()); // 凭证编号
			bill2201Map.put("VouDate", maindto.getDvoudate().toString()
					.replaceAll("-", "")); // 凭证日期[开票日期]
			bill2201Map.put("PayerAcct", maindto.getSpayeracct());
			bill2201Map.put("PayerName", maindto.getSpayername());
			bill2201Map.put("PayerAddr", maindto.getSpayeraddr());
			bill2201Map.put("PayeeAcct", maindto.getSpayeeacct());
			bill2201Map.put("PayeeName", maindto.getSpayeename());
			bill2201Map.put("PayeeAddr", maindto.getSpayeeaddr());
			bill2201Map.put("PayeeOpnBnkNo", maindto.getSpayeeopbkno());
			bill2201Map.put("AddWord", maindto.getSaddword());
			bill2201Map.put("BudgetType", maindto.getSbudgettype());
			bill2201Map.put("TrimSign", maindto.getStrimsign());
			bill2201Map.put("OfYear", maindto.getSofyear());
			bill2201Map.put("Amt", MtoCodeTrans.transformString(maindto
					.getFamt()));
			bill2201Map.put("StatInfNum", sublist.size());
			String s = "0";
			try {
				HashMap<String, TsTreasuryDto> mapTreInfo = SrvCacheFacade
						.cacheTreasuryInfo(null);
				s = mapTreInfo.get(list.get(0).getStrecode()).getSisuniontre();
			} catch (JAFDatabaseException e) {
				logger.error("查找国库代码异常!", e);
				throw new ITFEBizException("查找国库代码异常！", e);
			}

			// 循环子信息
			for (int j = 0; j < sublist.size(); j++) {
				HashMap<String, Object> detail2201Map = new HashMap<String, Object>();
				TvPayreckBankListDto subdto = (TvPayreckBankListDto) sublist
						.get(j);
				detail2201Map.put("SeqNo", subdto.getIseqno().toString());
				detail2201Map.put("BdgOrgCode", subdto.getSbdgorgcode());
				detail2201Map.put("FuncSbtCode", subdto.getSfuncbdgsbtcode());
				if (StateConstant.COMMON_YES.equals(s)) {
					detail2201Map.put("EcnomicSubjectCode", subdto
							.getSecnomicsubjectcode());
				} else {
					detail2201Map.put("EcnomicSubjectCode", "");
				}
				detail2201Map.put("Amt", MtoCodeTrans.transformString(subdto
						.getFamt()));
				detail2201Map.put("AcctProp", subdto.getSacctprop());
				detail2201List.add(detail2201Map);
			}

			bill2201Map.put("Detail2201", detail2201List);
			bill2201List.add(bill2201Map);

		}

		head2201Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead2201", head2201Map);
		msgMap.put("Bill2201", bill2201List);

		/*
		 * if(!isRepeat){ // 不是重发 TvFilepackagerefDto packdto = new
		 * TvFilepackagerefDto(); packdto.setSorgcode(orgcode); // 机构代码
		 * packdto.setStrecode(list.get(0).getStrecode()); // 国库代码
		 * packdto.setSfilename(filename); // 导入文件名
		 * packdto.setStaxorgcode(list.get(0).getSpayunit()); // 机关代码
		 * packdto.setScommitdate(list.get(0).getScommitdate()); // 委托日期
		 * packdto.setSaccdate(TimeFacade.getCurrentStringTime()); // 账务日期
		 * packdto.setSpackageno(packno); // 包流水号
		 * packdto.setSoperationtypecode(BizTypeConstant
		 * .BIZ_TYPE_DIRECT_PAY_PLAN); // 业务类型 packdto.setIcount(list.size());
		 * // 总笔数 packdto.setNmoney(allamt); // 总金额
		 * packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_DEALING); // 处理中
		 * packdto.setSusercode(list.get(0).getSusercode()); // 操作员代码
		 * packdto.setImodicount(1);
		 * 
		 * try { DatabaseFacade.getDb().create(packdto); } catch
		 * (JAFDatabaseException e) { logger.error("保存报文头的时候出现异常！", e); throw
		 * new ITFEBizException("保存报文头的时候出现异常！", e); } }
		 */
		return map;
	}
	public static List<IDto> tranList(List<IDto> sublist)
	{
		List<IDto> returnlist = new ArrayList<IDto>();
		if(sublist!=null&&sublist.size()>0)
		{
			TvPayreckBankListDto subdto = null;
			Map<String,TvPayreckBankListDto> packMap = new HashMap<String,TvPayreckBankListDto>();
			for(int i=0;i<sublist.size();i++)
			{
				subdto = (TvPayreckBankListDto)sublist.get(i);
				subdto.setSbdgorgcode(subdto.getSbdgorgcode().substring(0, 7));
				if(packMap.get(subdto.getSbdgorgcode()+subdto.getSfuncbdgsbtcode())==null)
				{
					packMap.put(subdto.getSbdgorgcode()+subdto.getSfuncbdgsbtcode(), subdto);
				}else
				{
					packMap.get(subdto.getSbdgorgcode()+subdto.getSfuncbdgsbtcode()).setFamt(packMap.get(subdto.getSbdgorgcode()+subdto.getSfuncbdgsbtcode()).getFamt().add(subdto.getFamt()));
				}
			}
			Set<String> set = packMap.keySet();
			for(String key:set)
				returnlist.add(packMap.get(key));
		}
		return returnlist;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}

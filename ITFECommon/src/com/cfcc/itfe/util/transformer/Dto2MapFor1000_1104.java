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
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;


/**
 * 退库请求
 * 
 * @author wangwenbo
 * 
 */
public class Dto2MapFor1000_1104 {

	private static Log logger = LogFactory.getLog(Dto2MapFor1000_1104.class);
	
	/**
	 * DTO转化XML报文(退库请求)旧版本
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
	public static Map tranfor(List<TvDwbkDto> list, String orgcode, String filename, String packno,boolean isRepeat) throws ITFEBizException {
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
		headMap.put("MsgNo", MsgConstant.MSG_NO_1104);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		String msgid  = "";
		try {
			msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			logger.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		}

		// 设置报文消息体 MSG
		HashMap<String, Object> BatchHead1000 = new HashMap<String, Object>();
		
		BatchHead1000.put("BillOrg", list.get(0).getStaxorgcode()); ///出票单位,  TvDwbkDto 中没有此项?
		BatchHead1000.put("EntrustDate", list.get(0).getDaccept().toString().replaceAll("-", "")); // 委托日期
		BatchHead1000.put("PackNo", list.get(0).getSpackageno()); // 包流水号
		BatchHead1000.put("TreCode", list.get(0).getSpayertrecode()); //国库主体代码,  TvDwbkDto 中没有此项?
		BatchHead1000.put("AllNum", String.valueOf(list.size())); // 总笔数
		BatchHead1000.put("PayoutVouType", MsgConstant.SAME_BANK_PAYOUT_VOU_TYPE2); //凭证类型: 1实拨, 2退库, 3商行划款, 4其他
		
		///<AllAmt>总金额</AllAmt>
		BigDecimal allamt = new BigDecimal("0.00"); // 总金额-赋值见下面

		List<Object> bill1000List = new ArrayList<Object>();
		
		//取库款账户信息
		TsInfoconnorgaccDto dto =  getDoconnorgAcc(list.get(0).getSbookorgcode(),list.get(0).getSpayertrecode());
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> bill1000Map = new HashMap<String, Object>();
			TvDwbkDto maindto = list.get(i);
			allamt = allamt.add(maindto.getFamt());
			bill1000Map.put("TraNo", maindto.getSdealno()); // 交易流水号
			
			String vouno = maindto.getSdwbkvoucode();
			if (vouno.length() > 8) {
				vouno = vouno.substring(vouno.length()-8,vouno.length());
			}else if(vouno.length() < 8){
				vouno = changeVouNo(vouno);
			}
			bill1000Map.put("VouNo", vouno); // 退库凭证编号
			bill1000Map.put("VouDate", maindto.getDvoucher().toString().replaceAll("-", "")); // 开票日期 -> 凭证日期 ?
			
			bill1000Map.put("PayerAcct",dto.getSpayeraccount()); // 付款人账号, TvDwbkDto 中没有此项?
			
			bill1000Map.put("PayerName",dto.getSpayername()); // 付款人名称, TvDwbkDto 中没有此项?
			
			bill1000Map.put("PayerAddr",dto.getSpayeraddr()); // 付款人地址, TvDwbkDto 中没有此项?
			
			bill1000Map.put("Amt", MtoCodeTrans.transformString(maindto.getFamt())); //交易金额 -> 金额?
			
			bill1000Map.put("PayeeBankNo", maindto.getSpayeeopnbnkno()); // 收款行行号, TvDwbkDto 中没有此项?
			bill1000Map.put("PayeeOpBkNo", maindto.getSpayeeopnbnkno()); // 收款人开户行行号
			bill1000Map.put("PayeeAcct", maindto.getSpayeeacct()); // 收款人账号
			bill1000Map.put("PayeeName", maindto.getSpayeename()); // 收款人名称
			
			bill1000Map.put("PayReason", maindto.getSdwbkreasoncode()); // 退库原因代码->拨款或退库原因?
			
			bill1000Map.put("OfYear",BatchHead1000.get("EntrustDate").toString().substring(0, 4) ); // 所属年度, TvDwbkDto 中没有此项?
			bill1000Map.put("Flag", MsgConstant.MSG_1000_FLAG2); //收款方标识: 1-本行业务 2-同城跨行 3-异地跨行
			
			
			
			/***********************************<1104>********************************************************/
			
			bill1000Map.put("DrawBackTreCode", maindto.getSpayertrecode()); // 退库国库代码
			bill1000Map.put("PayeeBankNo", ""); // 收款行行号
			bill1000Map.put("PayeeOpBkCode", maindto.getSpayeeopnbnkno()); // 收款开户行行号
			bill1000Map.put("PayeeAcct", maindto.getSpayeeacct()); // 收款人账号
			bill1000Map.put("PayeeName", maindto.getSpayeename()); // 收款人名称
			bill1000Map.put("TaxPayCode", ""); // 纳税人编码
			bill1000Map.put("TaxPayName", ""); // 纳税人名称
			
			bill1000Map.put("ElectroTaxVouNo", maindto.getSelecvouno()); // 电子凭证号码
			bill1000Map.put("OriTaxVouNo", ""); // 原税票号码
			bill1000Map.put("TraAmt", MtoCodeTrans.transformString(maindto.getFamt())); //交易金额
			
			bill1000Map.put("CorpCode", maindto.getSpayeecode()); // 企业代码
			bill1000Map.put("BudgetType", maindto.getCbdgkind()); // 预算种类
			bill1000Map.put("BudgetSubjectCode", maindto.getSbdgsbtcode()); // 预算科目代码
			bill1000Map.put("BudgetLevelCode", maindto.getCbdglevel()); // 预算级次代码
			bill1000Map.put("TrimSign", maindto.getCtrimflag()); // 整理期标志
			bill1000Map.put("ViceSign", maindto.getSastflag()); // 辅助标志
			bill1000Map.put("DrawBackReasonCode", maindto.getSdwbkreasoncode()); // 退库原因代码
			bill1000Map.put("DrawBackVou", maindto.getSdwbkby()); // 退库依据
			bill1000Map.put("ExamOrg", maindto.getSexamorg()); // 审批机关
			bill1000Map.put("AuthNo", ""); // 批准文号
			bill1000Map.put("TransType",""); // 转账类型
			
			/***********************************</1104>********************************************************/
			
			bill1000List.add(bill1000Map);
		}

		BatchHead1000.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead1000", BatchHead1000);
		msgMap.put("BillSend1000", bill1000List);
		
		return map;
	}
	
	/**
	 * 厦门退库凭证编号转化（不够8位补零）
	 * @param String seqno 原始凭证编号
	 * @return
	 */
	public static String changeVouNo(String seqno){
		String tmpVouNo = "00000000" + seqno;
		String trasrlNo = tmpVouNo.substring(tmpVouNo.length()- 8 ,tmpVouNo.length());
		return trasrlNo;
	}

	private static TsInfoconnorgaccDto getDoconnorgAcc(String orgCode,String treCode){
		TsInfoconnorgaccDto accdto = new TsInfoconnorgaccDto();
		accdto.setStrecode(orgCode);
		accdto.setSorgcode(treCode);
		List<TsInfoconnorgaccDto> accList = null;
		try {
			accList = (List<TsInfoconnorgaccDto>) CommonFacade.getODB().findRsByDto(accdto);
		} catch (JAFDatabaseException e) {
			logger.debug(e);
		} catch (ValidateException e) {
			logger.debug(e);
		}
		if(accList!=null&&accList.size()>0)
		{
			for(TsInfoconnorgaccDto tempdto:accList)
			{
				if(orgCode.equals("")&&treCode.equals(""))
					if(tempdto.getSpayeraccount().indexOf("371")>0)
						accdto =  tempdto;
			}
		}
		return accdto;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
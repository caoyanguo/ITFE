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
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 退库请求
 * 
 * @author wangyunbin
 * 
 */
public class Dto2MapFor11042 {

	private static Log logger = LogFactory.getLog(Dto2MapFor11042.class);
	
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
	 * @throws ValidateException 
	 * @throws JAFDatabaseException 
	 */
	@SuppressWarnings("unchecked")
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
				// 设置报文消息体 MSG
		HashMap<String, Object> head1104Map = new HashMap<String, Object>();
		head1104Map.put("TaxOrgCode", list.get(0).getStaxorgcode()); // 征收机关代码
		if(orgcode!=null&&orgcode.startsWith("1702"))
			headMap.put("SRC", ITFECommonConstant.SRCCITY_NODE);
		else
			headMap.put("SRC", ITFECommonConstant.SRC_NODE);
		if("000057400006".equals(ITFECommonConstant.SRC_NODE)&&head1104Map.get("TaxOrgCode")!=null&&!"".equals(String.valueOf(head1104Map.get("TaxOrgCode"))))//宁波专用国税地税已经介入tips，但退库还在前置走
		{
			if(String.valueOf(head1104Map.get("TaxOrgCode")).startsWith("1"))//国税
				headMap.put("SRC","201057400009");
			else if(String.valueOf(head1104Map.get("TaxOrgCode")).startsWith("2"))//地税
				headMap.put("SRC","202057400000");
		}
		//山东，国税和地税根据参数维护结果填写节点号
		if(ITFECommonConstant.SRC_NODE.equals("201053100013")){
			TsTaxorgDto dto = new TsTaxorgDto();
			dto.setSorgcode(StateConstant.ORG_CENTER_CODE);
			dto.setStaxorgcode(list.get(0).getStaxorgcode());
			dto.setStrecode(list.get(0).getSpayertrecode());
			
			List<TsTaxorgDto> listdto = new ArrayList<TsTaxorgDto>();
			try {
				listdto = CommonFacade.getODB().findRsByDto(dto);
			} catch (JAFDatabaseException e) {
				e.printStackTrace();
			} catch (ValidateException e) {
				e.printStackTrace();
			}
			if(listdto!=null && listdto.size()>0){
				headMap.put("SRC",listdto.get(0).getSnodecode());
			}
			
		}
		headMap.put("DES", ITFECommonConstant.DES_NODE);
		headMap.put("APP", MsgConstant.MSG_HEAD_APP);
		headMap.put("MsgNo", MsgConstant.MSG_NO_1104);
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
		head1104Map.put("EntrustDate", list.get(0).getDaccept().toString().replaceAll("-", "")); // 委托日期
		head1104Map.put("PackNo", list.get(0).getSpackageno()); // 包流水号
		head1104Map.put("AllNum", String.valueOf(list.size())); // 总笔数
		BigDecimal allamt = new BigDecimal("0.00"); // 总金额-赋值见下面

		List<Object> bill1104List = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> bill1104Map = new HashMap<String, Object>();
			TvDwbkDto maindto = list.get(i);

			allamt = allamt.add(maindto.getFamt());

			bill1104Map.put("TraNo", maindto.getSdealno()); // 交易流水号
			bill1104Map.put("DrawBackTreCode", maindto.getSpayertrecode()); // 退库国库代码
			bill1104Map.put("PayeeBankNo", ""); // 收款行行号
			bill1104Map.put("PayeeOpBkCode", maindto.getSpayeeopnbnkno()); // 收款开户行行号
			bill1104Map.put("PayeeAcct", maindto.getSpayeeacct()); // 收款人账号
			bill1104Map.put("PayeeName", maindto.getSpayeename()); // 收款人名称
			bill1104Map.put("TaxPayCode", ""); // 纳税人编码
			bill1104Map.put("TaxPayName", ""); // 纳税人名称
			bill1104Map.put("DrawBackVouNo", maindto.getSdwbkvoucode()); // 退库凭证编号
			bill1104Map.put("ElectroTaxVouNo", maindto.getSelecvouno()); // 电子凭证号码
			//bill1104Map.put("OriTaxVouNo", ""); // 原税票号码(tips新版本删除原税票号码)
			bill1104Map.put("TraAmt", MtoCodeTrans.transformString(maindto.getFamt())); //交易金额
			bill1104Map.put("BillDate", maindto.getDvoucher().toString().replaceAll("-", "")); // 开票日期
			bill1104Map.put("CorpCode", maindto.getSpayeecode()); // 企业代码
			bill1104Map.put("BudgetType", maindto.getCbdgkind()); // 预算种类
			bill1104Map.put("BudgetSubjectCode", maindto.getSbdgsbtcode()); // 预算科目代码
			bill1104Map.put("BudgetLevelCode", maindto.getCbdglevel()); // 预算级次代码
			bill1104Map.put("TrimSign", maindto.getCtrimflag()); // 整理期标志
			bill1104Map.put("ViceSign", maindto.getSastflag()); // 辅助标志
			bill1104Map.put("DrawBackReasonCode", maindto.getSdwbkreasoncode()); // 退库原因代码
			bill1104Map.put("DrawBackVou", maindto.getSdwbkby()); // 退库依据
			bill1104Map.put("ExamOrg", maindto.getSexamorg()); // 审批机关
			bill1104Map.put("AuthNo", ""); // 批准文号
			bill1104Map.put("TransType",""); // 转账类型
			bill1104Map.put("ChannelCode",""); // 渠道代码
			List OriTaxList1104MapList = new ArrayList();
			OriTaxList1104MapList.add(new HashMap());
			bill1104Map.put("OriTaxList1104", OriTaxList1104MapList);
			bill1104List.add(bill1104Map);
		}

		head1104Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead1104", head1104Map);
		msgMap.put("RetTreasury1104", bill1104List);
		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

	}

}

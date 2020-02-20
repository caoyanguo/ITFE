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
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvFreeDto;

/**
 * 免抵调请求
 * 
 * @author zhuguozhen
 * 
 */
public class Dto2MapFor1106 {

	private static Log logger = LogFactory.getLog(Dto2MapFor1104.class);

	/**
	 * DTO转化XML报文(免抵调请求)旧接口
	 * 
	 * @param List
	 *            <TvFreeDto> list 发送报文对象集合
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
	public static Map tranfor(List<TvFreeDto> list, String orgcode,
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
		if(orgcode!=null&&orgcode.startsWith("1702"))
			headMap.put("SRC", ITFECommonConstant.SRCCITY_NODE);
		else
			headMap.put("SRC", ITFECommonConstant.SRC_NODE);
		headMap.put("DES", ITFECommonConstant.DES_NODE);
		headMap.put("APP", MsgConstant.MSG_HEAD_APP);
		headMap.put("MsgNo", MsgConstant.MSG_NO_1106);
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
		HashMap<String, Object> head1106Map = new HashMap<String, Object>();
		// 征收机关代码
		head1106Map.put("TaxOrgCode", list.get(0).getStaxorgcode());
		// 委托日期
		head1106Map.put("EntrustDate", list.get(0).getDacceptdate().toString()
				.replaceAll("-", ""));
		// 包流水号
		head1106Map.put("PackNo", list.get(0).getSpackno());
		// 总笔数
		head1106Map.put("AllNum", String.valueOf(list.size()));
		// 总金额-赋值见下面
		BigDecimal allamt = new BigDecimal("0.00");

		List<Object> bill1106List = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> bill1106Map = new HashMap<String, Object>();
			TvFreeDto maindto = list.get(i);
			// 免抵调减交易金额+免抵调增交易金额
			allamt = allamt.add(maindto.getFfreepluamt());
			// 交易流水号
			bill1106Map.put("TraNo", maindto.getStrano());
			// 开票日期
			bill1106Map.put("BillDate", maindto.getDbilldate().toString()
					.replaceAll("-", ""));
			// 免抵调凭证编号
			bill1106Map.put("FreeVouNo", maindto.getSfreevouno());
			// 电子凭证号码
			bill1106Map.put("ElectroTaxVouNo", maindto.getSelectrotaxvouno());
			// 免抵调增预算种类
			bill1106Map.put("FreePluType", maindto.getCfreeplutype());
			// 免抵调增预算科目代码
			bill1106Map.put("FreePluSubjectCode", maindto
					.getSfreeplusubjectcode());
			// 免抵调增预算级次代码
			bill1106Map.put("FreePluLevCode", maindto.getCfreeplulevel());
			// 免抵调增辅助标志
			bill1106Map.put("FreePluSign", maindto.getSfreeplusign());
			// 免抵调增收款国库代码
			bill1106Map.put("FreePluPTreCode", maindto.getSfreepluptrecode());
			// 免抵调增交易金额
			bill1106Map.put("FreePluAmt", MtoCodeTrans.transformString(maindto
					.getFfreepluamt()));
			// 免抵调减预算种类
			bill1106Map.put("FreeMiKind", maindto.getCfreemikind());
			// 免抵调减预算科目代码
			bill1106Map.put("FreeMiSubject", maindto.getSfreemisubject());
			// 免抵调减预算级次代码
			bill1106Map.put("FreeMiLevel", maindto.getCfreemilevel());
			// 免抵调减辅助标志
			bill1106Map.put("FreeMiSign", maindto.getSfreemisign());
			// 免抵调减收款国库代码
			bill1106Map.put("FreeMiPTre", maindto.getSfreemiptre());
			// 免抵调减交易金额
			bill1106Map.put("FreeMiAmt", maindto.getFfreemiamt());
			// 整理期标志
			bill1106Map.put("TrimSign", maindto.getCtrimsign());
			// 企业代码
			bill1106Map.put("CorpCode", maindto.getScorpcode());

			bill1106List.add(bill1106Map);
		}
		head1106Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead1106", head1106Map);
		msgMap.put("FreeInfo1106", bill1106List);

		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

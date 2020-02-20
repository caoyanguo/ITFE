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
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;


/**
 * 对账报文接口(8000)（代理库发起）转换
 * 
 * @author wangyunbin
 * 
 */
public class Dto2MapFor8000 {

	private static Log logger = LogFactory.getLog(Dto2MapFor8000.class);
	
	/**
	 * DTO转化XML报文(退库请求)旧版本
	 * 
	 * @param List
	 *            <TvFilepackagerefDto> list 发送报文对象集合
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(List<TvFilepackagerefDto> list,String orgcode) throws ITFEBizException {
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
		headMap.put("MsgNo", MsgConstant.MSG_NO_8000);
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
		HashMap<String, Object> BatchHead8000 = new HashMap<String, Object>();
		BatchHead8000.put("ChkDate", TimeFacade.getCurrentStringTime()); ///对账日期
		BatchHead8000.put("TreCode", list.get(0).getStrecode()); // 国库主体代码
		BatchHead8000.put("SendPackNum", list.size()); // 本日发送包总个数
		
		///<AllAmt>总金额</AllAmt>
		BigDecimal allamt = new BigDecimal("0.00"); // 总金额-赋值见下面

		List<Object> BillCheck8000List = new ArrayList<Object>();
		
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> BillCheck8000Map = new HashMap<String, Object>();
			TvFilepackagerefDto filedPackegeDto = list.get(i);
			allamt = allamt.add(filedPackegeDto.getNmoney());
			BillCheck8000Map.put("PayoutVouType", VoucherUtil.getPayOutVouType(filedPackegeDto.getSoperationtypecode())); // 凭证类型
			BillCheck8000Map.put("EntrustDate", filedPackegeDto.getScommitdate());
			BillCheck8000Map.put("PackNo", filedPackegeDto.getSpackageno());
			BillCheck8000Map.put("CurPackVouNum",filedPackegeDto.getIcount());
			BillCheck8000List.add(BillCheck8000Map);
		}
		//BatchHead1000.put("AllAmt", MtoCodeTrans.transformString(allamt));
		BatchHead8000.put("SendPackAmt",MtoCodeTrans.transformString(allamt)); //本日发送包总金额
		msgMap.put("BatchHead8000", BatchHead8000);
		msgMap.put("BillCheck8000", BillCheck8000List);
		return map;
	}
}
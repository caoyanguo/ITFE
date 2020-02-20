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
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

public class Dto2MapFor1105 {
	
	private static Log logger = LogFactory.getLog(Dto2MapFor1105.class);
	
	/**
	 * DTO转化XML报文(更正请求)Tips旧版本
	 * 
	 * @param List
	 *            <TvInfileDto> list 发送报文对象集合
	 * @param String
	 *            orgcode 机构代码
	 * @param String
	 *            filename 文件名称
	 * @param String
	 *            packno 包流水号
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(List<TvInCorrhandbookDto> list,String orgcode,String filename,String packno ,boolean isRepeat) throws ITFEBizException{
		if(null == list || list.size() == 0){
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
		headMap.put("MsgNo",MsgConstant.MSG_NO_1105);
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
		HashMap<String, String> batchHeadMap = new HashMap<String, String>();
		batchHeadMap.put("TaxOrgCode", list.get(0).getScurtaxorgcode());
//		batchHeadMap.put("EntrustDate", list.get(0).getDaccept().toString().replaceAll("-", ""));
		batchHeadMap.put("EntrustDate", list.get(0).getDentrustdate()); //委托日期
		batchHeadMap.put("PackNo", list.get(0).getSpackageno());//????
		batchHeadMap.put("AllNum", String.valueOf(list.size())); // TODO需要重新设置
		BigDecimal allamt = new BigDecimal("0.00");//包中所有交易的 （现交易金额－原列金额）之和。一般情况下，该值为0
//		batchHeadMap.put("AllAmt", "0.00");
		
		
		List<Object> correctBodyList = new ArrayList<Object>();
		
		for (int i = 0; i < list.size(); i++) {
			allamt =allamt.add(list.get(i).getFcurcorramt().subtract(list.get(i).getForicorramt()));
			//allamt = allamt.add(list.get(i).getNmoney());
			HashMap<String, Object> correctBody1105Map = new HashMap<String, Object>();
			HashMap<String, Object> correctInfo1105Map = new HashMap<String, Object>();
			HashMap<String, Object> correctOld1105Map = new HashMap<String, Object>();
			HashMap<String, Object> correctNow1105Map = new HashMap<String, Object>();
			correctBody1105Map.put("CorrectInfo1105", correctInfo1105Map);
			correctBody1105Map.put("CorrectOld1105", correctOld1105Map);
			correctBody1105Map.put("CorrectNow1105", correctNow1105Map);
			
			correctInfo1105Map.put("TraNo", MtoCodeTrans.transformString(list.get(i).getSdealno()));//交易流水号
			correctInfo1105Map.put("OriTraNo", "");//原交易流水号
			correctInfo1105Map.put("BillDate", MtoCodeTrans.transformString(list.get(i).getDaccept()).replace("-", ""));//开票日期
			correctInfo1105Map.put("CorrVouNo", MtoCodeTrans.transformString(list.get(i).getScorrvouno()));//更正凭证编号
//			correctInfo1105Map.put("ElectroTaxVouNo", MtoCodeTrans.transformString(list.get(i).getSelecvouno()));//电子凭证号码
			correctInfo1105Map.put("ElectroTaxVouNo", MtoCodeTrans.transformString(list.get(i).getScorrvouno()));//电子凭证号码 根据更正调库处理类型区分电子与手工
			correctInfo1105Map.put("TrimSign", MtoCodeTrans.transformString(list.get(i).getCtrimflag()));//整理期标志
			correctInfo1105Map.put("CorrReaCode", MtoCodeTrans.transformString(list.get(i).getSreasoncode()));//更正原因代码
			correctInfo1105Map.put("Remark", "");//备注
			
			correctOld1105Map.put("OriTaxVouNo", "");//原税票号码
			correctOld1105Map.put("OriBudgetType", list.get(i).getCoribdgkind());//原预算种类
			correctOld1105Map.put("OriBudgetSubjectCode", list.get(i).getSoribdgsbtcode());//原列预算科目代码
			correctOld1105Map.put("OriBudgetLevCode", list.get(i).getCoribdglevel());//原列预算级次代码
			correctOld1105Map.put("OriViceSign", list.get(i).getSoriastflag());//原辅助标志
			correctOld1105Map.put("OriTreCode", list.get(i).getSoriaimtrecode());//原收款国库代码
			correctOld1105Map.put("OriRevAmt", MtoCodeTrans.transformString(list.get(i).getForicorramt()));//原列金额
			
			correctNow1105Map.put("CurBudgetType", list.get(i).getCcurbdgkind());//现预算种类
			correctNow1105Map.put("CurBudgetSubjectCode", list.get(i).getScurbdgsbtcode());//现预算科目代码
			correctNow1105Map.put("CurBudgetLevCode", list.get(i).getCcurbdglevel());//现预算级次代码
			correctNow1105Map.put("CurViceSign", list.get(i).getScurastflag());//现辅助标志
			correctNow1105Map.put("CurTreCode", list.get(i).getScuraimtrecode());//现收款国库代码
			correctNow1105Map.put("CurTraAmt", MtoCodeTrans.transformString(list.get(i).getFcurcorramt()));//现交易金额
			correctBodyList.add(correctBody1105Map);
		}
		
		batchHeadMap.put("AllAmt", String.valueOf(allamt));
		
		msgMap.put("BatchHead1105", batchHeadMap);
		msgMap.put("CorrectBody1105", correctBodyList);
		
		
		SQLExecutor updateExce = null;
		try {
			TvVoucherinfoDto vDto = new TvVoucherinfoDto();
			vDto.setSdealno(String.valueOf(list.get(0).getIvousrlno()));
			vDto = (TvVoucherinfoDto) DatabaseFacade.getDb().getODB().find(vDto);
			updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String updateSql ="update "+TvFilepackagerefDto.tableName()+" set  S_MSGID = ? , T_SENDTIME = CURRENT TIMESTAMP where  S_PACKAGENO = ? and S_TRECODE = ? and S_COMMITDATE = ?";
				updateExce.clearParams();
				updateExce.addParam(msgid);
				//updateExce.addParam(new Timestamp(TimeFacade.getCurrentDateTime().getTime()));
				updateExce.addParam(list.get(0).getSpackageno());
				updateExce.addParam(vDto==null?list.get(0).getSoripayeetrecode():vDto.getStrecode());//国库主体代码 
				updateExce.addParam((list.get(0).getDaccept()).toString().replaceAll("-", ""));////委托日期
				updateExce.runQuery(updateSql);
			updateExce.closeConnection();
			
		} catch (JAFDatabaseException e) {
			String error = "更新更正处理回执状态时出现数据库异常！";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (null != updateExce) {
				updateExce.closeConnection();
			}
		}
		return map;
	}

}

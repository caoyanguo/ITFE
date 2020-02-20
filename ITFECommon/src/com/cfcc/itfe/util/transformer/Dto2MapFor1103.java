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
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

public class Dto2MapFor1103 {
	
	private static Log logger = LogFactory.getLog(Dto2MapFor1103.class);
	
	/**
	 * DTO转化XML报文(收入税票业务)
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
	public static Map tranfor(List<TvInfileDto> list,String orgcode,String filename,String packno ) throws ITFEBizException{
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
		headMap.put("MsgNo",MsgConstant.MSG_NO_1103);
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
		batchHeadMap.put("TaxOrgCode", list.get(0).getStaxorgcode());
		batchHeadMap.put("EntrustDate", list.get(0).getScommitdate().replaceAll("-", ""));
		batchHeadMap.put("PackNo", list.get(0).getSpackageno());
		batchHeadMap.put("AllNum", String.valueOf(list.size())); // TODO需要重新设置
		BigDecimal allamt = new BigDecimal("0.00");
//		batchHeadMap.put("AllAmt", "0.00");
		
		HashMap<String, String> turnAccountMap = new HashMap<String, String>();
		turnAccountMap.put("PayeeOrgCode", list.get(0).getSrecvtrecode());
		turnAccountMap.put("PayeeAcct", "2560");// TODO 这个为默认值，不需要修改
		turnAccountMap.put("PayeeName", "");//可空不用填写 ---------------------------------
		
		List<Object> paymentList = new ArrayList<Object>();
		
		for (int i = 0; i < list.size(); i++) {
			allamt = allamt.add(list.get(i).getNmoney());
			HashMap<String, Object> paymentMap = new HashMap<String, Object>();
			paymentMap.put("TraNo", list.get(i).getSdealno());
			paymentMap.put("HandOrgName", "地方横联业务"); // TODO 这个为默认值，不需要修改
			paymentMap.put("TraAmt", list.get(i).getNmoney());
			paymentMap.put("TaxVouNo", list.get(i).getStaxticketno());
			paymentMap.put("BillDate", list.get(i).getSaccdate());
			paymentMap.put("TaxPayCode", "10101");// TODO 这个为默认值，不需要修改
			paymentMap.put("TaxPayName", "地方横联");// TODO 这个为默认值，不需要修改
			paymentMap.put("CorpCode", ""); // 可空不用填写 ---------------------------------
			paymentMap.put("BudgetType", list.get(i).getSbudgettype());
			paymentMap.put("TrimSign", list.get(i).getStrimflag());
			paymentMap.put("CorpType", ""); // 可空不用填写 ---------------------------------
			paymentMap.put("Remark", ""); // 可空不用填写 ---------------------------------
			paymentMap.put("Remark1", ""); // 可空不用填写 ---------------------------------
			paymentMap.put("Remark2", ""); // 可空不用填写 ---------------------------------
			paymentMap.put("TaxTypeNum", 1);
			
			
			List<Object> taxTypeList = new ArrayList<Object>();
			HashMap<String, Object> taxTypeListMap = new HashMap<String, Object>();
			taxTypeListMap.put("ProjectId", 1);
			taxTypeListMap.put("BudgetSubjectCode", list.get(i).getSbudgetsubcode());
			taxTypeListMap.put("LimitDate", list.get(i).getSaccdate().replaceAll("-", ""));
			taxTypeListMap.put("TaxTypeName", "");// 可空不用填写,需要委托银行打印付款凭证时必填 ---------------------------------
			taxTypeListMap.put("BudgetLevelCode", list.get(i).getSbudgetlevelcode());
			taxTypeListMap.put("BudgetLevelName", "预算内");// 根据级次去取名称 
			taxTypeListMap.put("TaxStartDate", list.get(i).getSaccdate().replaceAll("-", ""));
			taxTypeListMap.put("TaxEndDate", list.get(i).getSaccdate().replaceAll("-", ""));
			
			if(null == list.get(i).getSassitsign() || "".equals(list.get(i).getSassitsign().trim())){
				taxTypeListMap.put("ViceSign", "");
			}else{
				taxTypeListMap.put("ViceSign", list.get(i).getSassitsign());
			}
			
			taxTypeListMap.put("TaxType", "1");
			taxTypeListMap.put("TaxTypeAmt", list.get(i).getNmoney());
			taxTypeListMap.put("DetailNum", 1);
			
			List<Object> taxSubjectList = new ArrayList<Object>();
			HashMap<String, Object> taxSubjectListMap = new HashMap<String, Object>();
			taxSubjectListMap.put("DetailNo", "1");
			taxSubjectListMap.put("TaxSubjectCode", "1");
			taxSubjectListMap.put("TaxSubjectName", "1");
			taxSubjectListMap.put("TaxNumber", "1");
			taxSubjectListMap.put("TaxAmt", "1");
			taxSubjectListMap.put("TaxRate", "1");
			taxSubjectListMap.put("ExpTaxAmt", "1");
			taxSubjectListMap.put("DiscountTaxAmt", "1");
			taxSubjectListMap.put("FactTaxAmt", list.get(i).getNmoney());
			taxSubjectList.add(taxSubjectListMap);
				
			taxTypeListMap.put("TaxSubjectList1103", taxSubjectList);
			taxTypeList.add(taxTypeListMap);
			paymentMap.put("TaxTypeList1103", taxTypeList);
			paymentList.add(paymentMap);
		}
		
		batchHeadMap.put("AllAmt", String.valueOf(allamt));
		
		msgMap.put("BatchHead1103", batchHeadMap);
		msgMap.put("TurnAccount1103", turnAccountMap);
		msgMap.put("Payment1103", paymentList);
		
		TvSendlogDto sendlogDto = new TvSendlogDto();
		try {
			sendlogDto.setSsendno(StampFacade.getStampSendSeq("FS"));
		} catch (SequenceException e) {
			throw new ITFEBizException("取发送流水号失败",e);
		}
		sendlogDto.setSrecvno("");//对应接收日志流水号
		sendlogDto.setSsendorgcode(ITFECommonConstant.DES_NODE);//发送机构代码
		sendlogDto.setSdate(list.get(0).getScommitdate());//所属日期
		sendlogDto.setSoperationtypecode(MsgConstant.MSG_NO_1103);//业务凭证类型
		sendlogDto.setSrecvorgcode("");//接收机构
		sendlogDto.setStitle(filename);//凭证标题(?? 路径)
		sendlogDto.setSsendtime(Timestamp.valueOf(TimeFacade.getCurrent2StringTime()));//发送时间
		sendlogDto.setSretcode(DealCodeConstants.DEALCODE_ITFE_SEND)   ;//处理码
		sendlogDto.setSretcodedesc("");//处理说明
		sendlogDto.setIcount(list.size());//笔数
		sendlogDto.setNmoney(allamt) ;//金额
		sendlogDto.setSusercode("");//操作员代码
		sendlogDto.setSdemo("");//备注
		sendlogDto.setSseq(msgid);//报文ID 
		sendlogDto.setSstate(null);//处理状态
		sendlogDto.setSpackno(list.get(0).getSpackageno());//包流水号
		sendlogDto.setStrecode(list.get(0).getSrecvtrecode());//国库代码
		sendlogDto.setSpayeebankno("");//收款行行号
		sendlogDto.setSbillorg(list.get(0).getSunitcode());//出票单位
		sendlogDto.setSpayoutvoutype("");//支出凭证类型
		sendlogDto.setSproctime(Timestamp.valueOf(TimeFacade.getCurrent2StringTime()));//S_PROCTIME
		sendlogDto.setSsenddate(TimeFacade.getCurrentDate().toString());//发送日期 
		sendlogDto.setSifsend("");//是否转发
		sendlogDto.setSturnsendflag("");//转发标志
		try {
			DatabaseFacade.getDb().create(sendlogDto);
		} catch (JAFDatabaseException e) {
			logger.error("保存发送日志出现异常！", e);
			throw new ITFEBizException("保存发送日志的时候出现异常！", e);
		}
		SQLExecutor updateExce = null;
		try {
			updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String updateSql ="update "+TvFilepackagerefDto.tableName()+" set  S_MSGID = ? , T_SENDTIME = CURRENT TIMESTAMP where  S_PACKAGENO = ? S_TRECODE = ? ,S_COMMITDATE = ?";
				updateExce.clearParams();
				updateExce.addParam(msgid);
				//updateExce.addParam(new Timestamp(TimeFacade.getCurrentDateTime().getTime()));
				updateExce.addParam(list.get(0).getSpackageno());
				updateExce.addParam(list.get(0).getSrecvtrecode());//国库主体代码 
				updateExce.addParam(list.get(0).getScommitdate().replaceAll("-", ""));////委托日期
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

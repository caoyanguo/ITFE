package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TfReconciliationDto;
import com.cfcc.itfe.persistence.dto.TfResultReconciDto;
import com.cfcc.itfe.util.transformer.Dto2MapFor1002;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * 对账结果接口 (1002)处理(北京)
 * @author Administrator
 *
 */
public class Send1002MsgServer extends AbstractMsgManagerServer {
	private static Log logger = LogFactory.getLog(Send1002MsgServer.class);

	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		MuleMessage muleMessage = eventContext.getMessage();
		TfReconciliationDto dto = (TfReconciliationDto)muleMessage.getProperty(MessagePropertyKeys.MSG_HEAD_DTO);
		List<TfReconciliationDto> list = (List<TfReconciliationDto>)muleMessage.getProperty(MessagePropertyKeys.MSG_DTO_LIST);
		String revNode = (String)muleMessage.getProperty(MessagePropertyKeys.MSG_DESC);
		Map xmlMap = Dto2MapFor1002.tranfor(list, dto,revNode);
		
		/**
		 * 对账结果信息保存数据库
		 */
		HashMap<String, Object> cfxMap = new HashMap<String, Object>();
		HashMap<String, Object> headMap = new HashMap<String, Object>();
		HashMap<String, Object> msgMap = new HashMap<String, Object>();
		HashMap<String, Object> head1002map = new HashMap<String, Object>();
		List<Object> billCheck1002list = new ArrayList<Object>();
		
		cfxMap = (HashMap<String, Object>) xmlMap.get("cfx");
		headMap = (HashMap<String, Object>) cfxMap.get("HEAD");
		msgMap = (HashMap<String, Object>) cfxMap.get("MSG");
		head1002map = (HashMap<String, Object>) msgMap.get("BatchHead1002");
		billCheck1002list = (List<Object>) msgMap.get("BillCheck1002");
		Timestamp ts = null;
		try {
			ts = TSystemFacade.getDBSystemTime();
		} catch (JAFDatabaseException e) {
			logger.error("获取系统时间错误",e);
			throw new ITFEBizException("获取系统时间错误");
		}
		if(billCheck1002list.size()>0){
			List<IDto> dtoList = new ArrayList<IDto>();
			for(int i=0;i<billCheck1002list.size();i++){
				TfResultReconciDto rsDto = new TfResultReconciDto();
				rsDto.setSmsgid(headMap.get("MsgID").toString());
				rsDto.setSmsgno(MsgConstant.MSG_TBS_NO_1002);
				rsDto.setSmsgref(headMap.get("MsgRef").toString());
				rsDto.setSchkdate(head1002map.get("ChkDate").toString());
				rsDto.setStrecode(head1002map.get("TreCode").toString());
				rsDto.setIpackno(Integer.parseInt(head1002map.get("PackNo").toString()));
				rsDto.setScheckresult(head1002map.get("CheckResult").toString());
				rsDto.setIsendpacknum(Integer.parseInt(head1002map.get("SendPackNum").toString()));
				rsDto.setIrecvpacknum(Integer.parseInt(head1002map.get("RecvPackNum").toString()));
				rsDto.setSpayoutvoutype(((HashMap<String, Object>)billCheck1002list.get(i)).get("PayoutVouType").toString());
				rsDto.setSpackno(((HashMap<String, Object>)billCheck1002list.get(i)).get("PackNo").toString());
				rsDto.setIcurpackvounum(Integer.parseInt(((HashMap<String, Object>)billCheck1002list.get(i)).get("CurPackVouNum").toString()));
				rsDto.setNcurpackvouamt(new BigDecimal(((HashMap<String, Object>)billCheck1002list.get(i)).get("CurPackVouAmt").toString()));
				rsDto.setTssysupdate(ts);
				dtoList.add(rsDto);
			}
			if(dtoList.size()>0){
				try {
					DatabaseFacade.getDb().create(dtoList.toArray(new IDto[dtoList.size()]));
				} catch (JAFDatabaseException e) {
					logger.error("保存对账结果信息出错",e);
					throw new ITFEBizException("保存对账结果信息出错");
				}
			}
		}else{
			TfResultReconciDto rsDto = new TfResultReconciDto();
			rsDto.setSmsgid(headMap.get("MsgID").toString());
			rsDto.setSmsgno(MsgConstant.MSG_TBS_NO_1002);
			rsDto.setSmsgref(headMap.get("MsgRef").toString());
			rsDto.setSchkdate(head1002map.get("ChkDate").toString());
			rsDto.setStrecode(head1002map.get("TreCode").toString());
			rsDto.setIpackno(Integer.parseInt(head1002map.get("PackNo").toString()));
			rsDto.setScheckresult(head1002map.get("CheckResult").toString());
			rsDto.setIsendpacknum(Integer.parseInt(head1002map.get("SendPackNum").toString()));
			rsDto.setIrecvpacknum(Integer.parseInt(head1002map.get("RecvPackNum").toString()));
			rsDto.setSpayoutvoutype("");
			try {
				DatabaseFacade.getDb().create(rsDto);
			} catch (JAFDatabaseException e) {
				logger.error("保存对账结果信息出错",e);
				throw new ITFEBizException("保存对账结果信息出错");
			}
		}
		
		// 写发送日志
		String _ssendno = null;
		try {
			_ssendno = StampFacade.getStampSendSeq("FS");
		} catch (SequenceException e1) {
			logger.error("取发送流水号错误!", e1);
			throw new ITFEBizException("取发送流水号错误!", e1);
		}
		// 写发送日志
		muleMessage.setProperty(MessagePropertyKeys.MSG_SEND_LOG_DTO,
				MsgLogFacade.writeSendLogWithResult(_ssendno, null,
						(String)headMap.get("SRC"),
						(String)headMap.get("DES"), TimeFacade.getCurrentStringTime(), 
						MsgConstant.MSG_TBS_NO_1002, null, 0, null, null, null, null,
						(String)headMap.get("SRC"), null, headMap.get("MsgID").toString(),
						DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
						(String) eventContext.getMessage().getProperty(
								MessagePropertyKeys.MSG_SENDER), null, null));
		
		// 设置消息体
		eventContext.getMessage().setPayload(xmlMap);
	}
	
}

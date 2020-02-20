package com.cfcc.itfe.component.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceMainDto;
import com.cfcc.itfe.util.transformer.Dto2MapFor5112;
import com.cfcc.itfe.util.transformer.Dto2MapFor7211;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

public class CommitIncmBillComponent implements Callable {
	
	private static Log logger = LogFactory.getLog(CommitIncmBillComponent.class);

	// 定时提交税票 
	public Object onCall(MuleEventContext eventContext) throws Exception {

//		logger.debug("======================== sleep start ========================");
//		Thread.sleep(1000 * 30 * 1);
//		logger.debug("======================== sleep end ========================");
		
		logger.debug("======================== start commit ========================");
		
		String trasrlsql = "select * from TV_FILEPACKAGEREF where S_RETCODE = ?  and S_OPERATIONTYPECODE IN(?,?) order by S_ORGCODE,S_PACKAGENO fetch first " + ITFECommonConstant.COMMIT_NUM + " rows only ";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			
			sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
			sqlExec.addParam(BizTypeConstant.BIZ_TYPE_BATCH_OUT);
			sqlExec.addParam(BizTypeConstant.BIZ_TYPE_INCOME);
			SQLResults fileSrlnoRs = sqlExec.runQueryCloseCon(trasrlsql,TvFilepackagerefDto.class);
			List<TvFilepackagerefDto> dtoList = (List<TvFilepackagerefDto>) fileSrlnoRs.getDtoCollection();
			
			// S_ORGCODE,S_FILENAME,S_PACKAGENO,S_COMMITDATE
			if(dtoList != null && dtoList.size() > 0){
//				TvFilepackagerefDto[] packdtos = new TvFilepackagerefDto[dtoList.size()];
				
				for(int i = 0 ;i < dtoList.size(); i++){
					TvFilepackagerefDto tmppackdto = dtoList.get(i);
					Map map = new HashMap();
					MuleMessage message = new DefaultMuleMessage(map);
					String orgcode = tmppackdto.getSorgcode();
					String filename = tmppackdto.getSfilename();
					String packno = tmppackdto.getSpackageno();
					String commitdate = tmppackdto.getScommitdate();
					Map xmlMap = new HashMap();
					if(BizTypeConstant.BIZ_TYPE_INCOME.equals(tmppackdto.getSoperationtypecode())){
						message.setProperty(MessagePropertyKeys.MSG_NO_KEY, MsgConstant.MSG_NO_7211);
						// 查找数据库
						String orderbysql = "where S_ORGCODE = ? and S_FILENAME = ? and S_PACKAGENO = ? and S_COMMITDATE = ? ";
						List<String> params = new ArrayList<String>();
						params.add(orgcode);
						params.add(filename);
						params.add(packno);
						params.add(commitdate);
						List<TvInfileDto> list = DatabaseFacade.getDb().find(TvInfileDto.class, orderbysql, params);
						if(null == list || list.size() == 0){
							continue;
						}
						// DTO -> MAP
						xmlMap = Dto2MapFor7211.tranfor(list, orgcode, filename, packno, false);

						String ls_MsgId  = (String)((Map)((Map)xmlMap.get("cfx")).get("HEAD")).get("MsgID");
						Map msg  = (Map) xmlMap.get("cfx");
						Map batchHead7211 = (Map)((Map)msg.get("MSG")).get("BatchHead7211");
						BigDecimal amt = new BigDecimal(batchHead7211.get("AllAmt").toString());
						int num = Integer.valueOf( batchHead7211.get("AllNum").toString());
						
						// 写发送日志
						message.setProperty(MessagePropertyKeys.MSG_SEND_LOG_DTO,
								MsgLogFacade.writeSendLogWithResult(StampFacade.getStampSendSeq("FS"), null,
										orgcode,
										ITFECommonConstant.DES_NODE, TimeFacade
												.getCurrentStringTime(),
										MsgConstant.MSG_NO_7211, (String) eventContext
										.getMessage().getProperty("XML_MSG_FILE_PATH"), num, amt, list.get(0).getSpackageno(), list.get(0).getSrecvtrecode(),
										null, list.get(0).getStaxorgcode(), null, ls_MsgId,
										DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
										StateConstant.MSG_SENDER_FLAG_0, null, MsgConstant.ITFE_AUTO_SEND+MsgConstant.MSG_NO_7211));
						SQLExecutor updateExce = null;
						try {
							updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
							String updateSql ="update "+TvFilepackagerefDto.tableName()+" set  S_MSGID = ?,T_SENDTIME =CURRENT TIMESTAMP where  S_PACKAGENO = ? and  S_ORGCODE = ?  and S_COMMITDATE = ?";
								updateExce.clearParams();
								updateExce.addParam(ls_MsgId);
								updateExce.addParam(list.get(0).getSpackageno());
								updateExce.addParam(list.get(0).getSorgcode());//核算主体代码
								updateExce.addParam(list.get(0).getScommitdate().replace("-", ""));////委托日期
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
					}else if(BizTypeConstant.BIZ_TYPE_BATCH_OUT.equals(tmppackdto.getSoperationtypecode())){
						message.setProperty(MessagePropertyKeys.MSG_NO_KEY, MsgConstant.MSG_NO_5112);
						// 查找数据库
						String orderbysql = "where S_BOOKORGCODE = ? and S_PACKAGENO = ? and S_ENTRUSTDATE = ? ";
						List<String> params = new ArrayList<String>();
						params.add(orgcode);
						//params.add(filename);
						params.add(packno);
						params.add(commitdate);
						List<TvPayoutfinanceMainDto> list = DatabaseFacade.getDb().find(TvPayoutfinanceMainDto.class, orderbysql, params);
						if(null == list || list.size() == 0){
							continue;
						}
						// DTO -> MAP
						xmlMap = Dto2MapFor5112.tranfor(list, orgcode, filename, packno, false);

						String ls_MsgId  = (String)((Map)((Map)xmlMap.get("cfx")).get("HEAD")).get("MsgID");
						Map msg  = (Map) xmlMap.get("cfx");
						Map batchHead5112 = (Map)((Map)msg.get("MSG")).get("BatchHead5112");
						BigDecimal amt = new BigDecimal(batchHead5112.get("AllAmt").toString());
						int num = Integer.valueOf( batchHead5112.get("AllNum").toString());

						// 写发送日志
						message.setProperty(MessagePropertyKeys.MSG_SEND_LOG_DTO,
								MsgLogFacade.writeSendLogWithResult(StampFacade.getStampSendSeq("FS"), null,
										orgcode,
										ITFECommonConstant.DES_NODE, TimeFacade
												.getCurrentStringTime(),
										MsgConstant.MSG_NO_5112, (String) eventContext
										.getMessage().getProperty("XML_MSG_FILE_PATH"), num, amt, list.get(0).getSpackageno(), list.get(0).getStrecode(),
										null, list.get(0).getSbillorg(), null, ls_MsgId,
										DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
										StateConstant.MSG_SENDER_FLAG_0,null, MsgConstant.ITFE_AUTO_SEND+MsgConstant.MSG_NO_5112));
}
										
					// 设置消息体
					message.setPayload(xmlMap);
					
					eventContext.dispatchEvent(message, "Dispatchjms");
				}
			}else{
				logger.debug("======================== end commit ========================");
				return null;
			}
		} catch (JAFDatabaseException e) {
			logger.error("查询数据的时候出现异常!", e);
			throw new ITFEBizException("查询数据的时候出现异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
			
			
		
//		String filename = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_FILE_NAME);// 文件名称
//		String orgcode = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_ORGCODE);// 机构代码
//		String packno = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_PACK_NO);// 包流水号
//		String commitdate = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_DATE); // 委托日期
//
//		// 查找数据库
//		String orderbysql = "where S_ORGCODE = ? and S_FILENAME = ? and S_PACKAGENO = ? and S_COMMITDATE = ? and S_STATUS = ? order by S_PACKAGENO";
//		List<String> params = new ArrayList<String>();
//		params.add(orgcode);
//		params.add(filename);
//		params.add(packno);
//		params.add(commitdate);
//		params.add(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
//
//		for(){
//			eventContext.dispatchEvent(message, "Dispatchjms");
//		}
//		
//		
//		
//		try {
//			List<TvInfileDto> list = DatabaseFacade.getDb().find(TvInfileDto.class, orderbysql, params);
//			if(null == list || list.size() == 0){
//				return null;
//			}
//
//			// DTO -> MAP
//			Map xmlMap = Dto2MapFor7211.tranfor(list, orgcode, filename, packno, false);
//			// 设置消息体
//			eventContext.getMessage().setPayload(xmlMap);
//			
//			
//		} catch (JAFDatabaseException e) {
//			logger.error("查询收入税票业务出现数据库异常!", e);
//			throw new ITFEBizException("查询收入税票业务出现数据库异常!", e);
//		}
		
		logger.debug("======================== end commit ========================");

		return eventContext.getMessage().getPayload();
	}

}

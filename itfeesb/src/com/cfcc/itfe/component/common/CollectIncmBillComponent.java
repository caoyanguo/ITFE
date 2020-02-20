package com.cfcc.itfe.component.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;


public class CollectIncmBillComponent implements Callable {
	
	private static Log logger = LogFactory.getLog(CollectIncmBillComponent.class);

	public Object onCall(MuleEventContext eventContext) throws Exception {
		
		logger.debug("======================== start collect ========================");

//		// 查询包流水号信息
//		String trasrlsql = "select S_ORGCODE,S_FILENAME,S_PACKAGENO,S_COMMITDATE from TV_FILEPACKAGEREF where S_RETCODE = ? order by S_ORGCODE,S_PACKAGENO";
////		String trasrlsql = "select S_ORGCODE,S_FILENAME,S_PACKAGENO,S_COMMITDATE from TV_INFILE where S_STATUS = ? group by S_ORGCODE,S_FILENAME,S_PACKAGENO,S_COMMITDATE order by S_ORGCODE,S_PACKAGENO";
////		String updateSQL = "update TV_INFILE set S_STATUS = ? where S_ORGCODE = ? and S_FILENAME = ? and S_PACKAGENO = ? and S_COMMITDATE = ? ";
//		
//		SQLExecutor sqlExec = null;
////		SQLExecutor updatesqlExec = null;
//		try {
//			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
////			updatesqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
//			
//			sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
//			SQLResults fileSrlnoRs = sqlExec.runQueryCloseCon(trasrlsql);
//			int row = fileSrlnoRs.getRowCount();
//		
//			for(int i = 0 ;i < row && i < 30 ; i++){
//				String sorgcode = fileSrlnoRs.getString(i, 0);
//				String filename = fileSrlnoRs.getString(i, 1);
//				String packageno = fileSrlnoRs.getString(i, 2);
//				String commitdate = fileSrlnoRs.getString(i, 3);
//				
////				// 更新凭证状态
////				String updatesql = "update TV_INFILE set S_STATUS = ? where S_ORGCODE = ? and S_FILENAME = ? and S_PACKAGENO = ? and S_COMMITDATE = ?";
////				updatesqlExec.clearParams();
////				updatesqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING); // 处理中
////				updatesqlExec.addParam(sorgcode);
////				updatesqlExec.addParam(filename);
////				updatesqlExec.addParam(packageno);
////				updatesqlExec.addParam(commitdate);
////				updatesqlExec.runQuery(updatesql);
//				
//				MuleClient client = new MuleClient();
//				Map map = new HashMap();
//				MuleMessage message = new DefaultMuleMessage(map);
//				
//				message.setProperty(MessagePropertyKeys.MSG_FILE_NAME, filename);
//				message.setProperty(MessagePropertyKeys.MSG_ORGCODE, sorgcode);
//				message.setProperty(MessagePropertyKeys.MSG_PACK_NO, packageno);
//				message.setProperty(MessagePropertyKeys.MSG_DATE, commitdate);
//				message.setProperty(MessagePropertyKeys.MSG_NO_KEY, MsgConstant.MSG_NO_7211);
//				message.setPayload(map);
//				
//				message = client.send("vm://commitIncomeBill", message);
//					
//				ServiceUtil.checkResult(message);
//			} 
//			
////			updatesqlExec.closeConnection();
			
//		logger.debug("======================== end collect ========================");
//		} catch (JAFDatabaseException e) {
//			logger.error("查询数据的时候出现异常!", e);
//			throw new ITFEBizException("查询数据的时候出现异常!", e);
//		} finally {
////			if(null != updatesqlExec){
////				updatesqlExec.closeConnection();
////			}
//			if (null != sqlExec) {
//				sqlExec.closeConnection();
//			}
//		}
			
		eventContext.getMessage().setPayload("1");
		
		return eventContext.getMessage().getPayload();
		
	}

}

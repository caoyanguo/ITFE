package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.util.transformer.Dto2MapFor5101;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

public class PayOutMsgServer extends AbstractMsgManagerServer {
	
	private static Log logger = LogFactory.getLog(PayOutMsgServer.class);

	/**
	 * (ʵ���ʽ�)������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		

		String filename = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_FILE_NAME);// �ļ�����
		String orgcode = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_ORGCODE);// ��������
		String packno = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_PACK_NO);// ����ˮ��
		String commitdate = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_DATE); // ί������
		boolean isRepeat =  (Boolean) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_REPEAT); // �Ƿ��ط�����
		String ls_Path = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_DATE); // ί������
		//String msg = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_CONTENT);

		/*if(!isRepeat && null != msg && !"".equals(msg)){
			// ��һ�η���
			eventContext.getMessage().setProperty(MessagePropertyKeys.MSG_CONTENT, "");
			eventContext.getMessage().setPayload(msg);
		}else{*/
			// �����ط� ����Ҫ�������ݿ�
			String orderbysql = "where S_ORGCODE = ?  and S_PACKAGENO = ? and S_COMMITDATE = ? ";
			List<String> params = new ArrayList<String>();
			params.add(orgcode);
			//params.add(filename);
			params.add(packno);
			params.add(commitdate);

			try {
				List<TvPayoutmsgmainDto> list = DatabaseFacade.getDb().find(TvPayoutmsgmainDto.class, orderbysql, params);

				// DTO -> MAP
				Map xmlMap = Dto2MapFor5101.tranfor(list, orgcode, filename, packno,isRepeat);
				
				String ls_MsgId  = (String)((Map)((Map)xmlMap.get("cfx")).get("HEAD")).get("MsgID");
				Map msg  = (Map) xmlMap.get("cfx");
				Map batchHead5101 = (Map)((Map)msg.get("MSG")).get("BatchHead5101");
				BigDecimal amt = new BigDecimal(batchHead5101.get("AllAmt").toString());
				int num = Integer.valueOf( batchHead5101.get("AllNum").toString());
				
				
				// д������־
				eventContext.getMessage().setProperty(MessagePropertyKeys.MSG_SEND_LOG_DTO,
						MsgLogFacade.writeSendLogWithResult(StampFacade.getStampSendSeq("FS"), null,
								orgcode,
								ITFECommonConstant.DES_NODE, commitdate.replaceAll("-", ""),
								MsgConstant.MSG_NO_5101, (String) eventContext
								.getMessage().getProperty("XML_MSG_FILE_PATH"), num, amt, list.get(0).getSpackageno(), list.get(0).getStrecode(),
								null, list.get(0).getSpayunit(), null, ls_MsgId,
								DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
								(String) eventContext.getMessage().getProperty(
										MessagePropertyKeys.MSG_SENDER), null, ""));
				SQLExecutor updateExce = null;
				try {
					updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
					String updateSql ="update "+TvFilepackagerefDto.tableName()+" set  S_MSGID = ?,T_SENDTIME =CURRENT TIMESTAMP where  S_PACKAGENO = ? and  S_orgcode = ?  and S_COMMITDATE = ?";
						updateExce.clearParams();
						updateExce.addParam(ls_MsgId);
						updateExce.addParam(list.get(0).getSpackageno());
						updateExce.addParam(orgcode);//�����������
						updateExce.addParam(list.get(0).getScommitdate().replace("-", ""));////ί������
						updateExce.runQuery(updateSql);
					updateExce.closeConnection();
					
				} catch (JAFDatabaseException e) {
					String error = "����ʵ���ʽ����ִ״̬ʱ�������ݿ��쳣��";
					logger.error(error, e);
					throw new ITFEBizException(error, e);
				} finally {
					if (null != updateExce) {
						updateExce.closeConnection();
					}
				}
				// ������Ϣ��
				eventContext.getMessage().setPayload(xmlMap);
			} catch (JAFDatabaseException e) {
				logger.error("��ѯʵ���ʽ�ҵ��������ݿ��쳣!", e);
				throw new ITFEBizException("��ѯʵ���ʽ�ҵ��������ݿ��쳣!", e);
			} catch (SequenceException e) {
				throw new ITFEBizException("ȡ������ˮ��ʧ��",e);
			}
		//}
	}
}

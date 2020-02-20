package com.cfcc.itfe.msgmanager.msg;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.mule.api.MuleEventContext;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.ContentDto;
import com.cfcc.itfe.persistence.dto.HeadDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.transformer.Dto2MapFor9005;
import com.cfcc.itfe.util.transformer.Dto2MapFor9006;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutorFactory;

public class LoginTipsReturnServer extends AbstractMsgManagerServer {

	/**
	 * ������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap msgMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) msgMap.get("HEAD");
		HashMap bodyMap = (HashMap) msgMap.get("MSG");
		String msgRef = (String) headMap.get("MsgRef");
		HashMap resultMap = (HashMap) bodyMap.get("LoginReturn9007");
		//д������־��
		TvRecvlogDto dto = new TvRecvlogDto();
		String msgno = headMap.get("MsgNo").toString();
		dto.setSsendorgcode(headMap.get("SRC").toString());
		dto.setStitle(msgno);
		dto.setSoperationtypecode(msgno);
		String des = headMap.get("DES").toString();
		dto.setSrecvorgcode(des);
		String sdate = TimeFacade.getCurrentStringTime();
		dto.setSdate(sdate);
		dto.setSsenddate(sdate);
		try {
			TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(msgRef,MsgConstant.MSG_NO_9006);
			dto.setSsendno(senddto.getSsendno());
			dto.setSrecvno(StampFacade.getStampSendSeq("JS"));
		} catch (SequenceException e) {
			throw new ITFEBizException("ȡ������ˮ��ʧ��",e);
		}
//		dto.setSsendno("0");
		dto.setSrecvtime(new Timestamp(new java.util.Date().getTime()));
		String retcode = resultMap.get("LoginResult").toString();
		String addword = resultMap.get("AddWord").toString();
		dto.setSdemo(addword);
		dto.setSusercode("�Զ�����");
		dto.setSretcode(retcode);
		dto.setSretcodedesc(addword);
        try {
			DatabaseFacade.getDb().create(dto);
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException("������־����ʧ��",e);
		}
		//���·�����־
		SQLExecutor exec = null;
		try {
			exec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String sql ="Update TV_SENDLOG set s_retcode =? ,s_demo = ? ,s_recvno =? where  S_OPERATIONTYPECODE = ? and s_seq =?" ;
			exec.addParam(retcode);
			exec.addParam(addword);
			exec.addParam(dto.getSrecvno());
			exec.addParam(MsgConstant.MSG_NO_9006);
			exec.addParam(headMap.get("MsgRef"));
			exec.runQuery(sql);
			
			
			if (retcode.equals(DealCodeConstants.DEALCODE_TIPS_SUCCESS)) {
				String tipslogstate = StateConstant.LOGINSTATE_FLAG_LOGIN;
				sql =" update ts_system set S_TIPSLOGINSTATE = '"+tipslogstate+"'" ;
				exec.runQuery(sql);
			}
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException("���·�����־������ʧ��",e);
		}finally{
			exec.closeConnection();
		}	
		eventContext.setStopFurtherProcessing(true);
		return;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

package com.cfcc.itfe.msgmanager.msg;

import org.mule.api.MuleEventContext;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.util.datamove.DataMoveUtil;

public class DataMoveMsgServer {

	/**
	 * ������Ϣ����
	 */
	public void dealMsg() throws ITFEBizException {
		try {
			DataMoveUtil.timerTaskForDataMove();
		} catch (Exception e1) {
			throw new ITFEBizException("��ʱת�����ݳ���!", e1);
		}
	}
}

package com.cfcc.itfe.facade;

import com.cfcc.deptone.common.core.exception.SequenceException;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.facade.time.TimeFacade;

/**
 * 取发送报文的sequenen,即报文头中的MSgId
 * 
 */
public class MsgSeqFacade {
	private static String MsgSendSeq = "MSG_SEND_SEQUENCE";
	private static String OperLogSeq = "OPER_LOG_SEQUENCE";

	/**
	 * 返回20位长的发送或接收流水号
	 * 
	 * @param flag
	 *            收发标志，接收-JS，发送-FS
	 * @return
	 * @throws com.cfcc.itfe.exception.SequenceException
	 * @throws SequenceException
	 * @throws com.cfcc.itfe.exception.SequenceException
	 */
	public static String getMsgSendSeq()
			throws com.cfcc.itfe.exception.SequenceException {

		String seq = SequenceGenerator.getNextByDb2(MsgSendSeq,
				SequenceName.TRAID_SEQ_CACHE, SequenceName.TRAID_SEQ_STARTWITH);
		// 流水号为18位长
		seq = "00000000000000000000" + seq;
		if(ITFECommonConstant.PUBLICPARAM.indexOf(",systemMode=8,")>=0)//地市模式需要省转发-针对吉林(吉林地区)
			seq = "8" + TimeFacade.getCurrentStringTime()+ seq.substring(seq.length() - 11, seq.length());
		else if(ITFECommonConstant.PUBLICPARAM.indexOf(",systemMode=7,")>=0)//地市模式需要省转发-针对吉林（其他地区）
			seq = "7" + TimeFacade.getCurrentStringTime()+ seq.substring(seq.length() - 11, seq.length());
		else
			seq = "9" + TimeFacade.getCurrentStringTime()+ seq.substring(seq.length() - 11, seq.length());
		return seq;
	}

	public static String getlogSeq() throws com.cfcc.itfe.exception.SequenceException {

		 return SequenceGenerator.getNextByDb2(OperLogSeq,
				SequenceName.TRAID_SEQ_CACHE, SequenceName.TRAID_SEQ_STARTWITH);
	
	}

}
package com.cfcc.itfe.util.transformer;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.ContentDto;

public class Dto2MapFor9111 {
	
	private static Log logger = LogFactory.getLog(Dto2MapFor9111.class);
	
	/**
	 * ������Ϣת��
	 * @param dto
	 * @param orgcode
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(ContentDto dto,String orgcode) throws ITFEBizException{
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> cfxMap = new HashMap<String, Object>();
		HashMap<String, Object> headMap = new HashMap<String, Object>();
		HashMap<String, Object> msgMap = new HashMap<String, Object>();
		
		// ���ñ��Ľڵ� CFX
		map.put("cfx", cfxMap);
		cfxMap.put("HEAD", headMap);
		cfxMap.put("MSG", msgMap);
		
		// ����ͷ HEAD
		headMap.put("VER", MsgConstant.MSG_HEAD_VER);
		if(orgcode!=null&&orgcode.startsWith("1702"))
			headMap.put("SRC", ITFECommonConstant.SRCCITY_NODE);
		else
			headMap.put("SRC", ITFECommonConstant.SRC_NODE);
		headMap.put("DES", ITFECommonConstant.DES_NODE);
		headMap.put("APP", MsgConstant.MSG_HEAD_APP);
		headMap.put("MsgNo",MsgConstant.MSG_NO_9111);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʱ�����쳣��", e);
			throw new ITFEBizException("ȡ������ˮ��ʱ�����쳣��", e);
		}
	
		HashMap<String, Object> getMsgMap = new HashMap<String, Object>();
		// ������GETMSG
		getMsgMap.put("SendOrgCode", orgcode); // �����������
		getMsgMap.put("EntrustDate", dto.get_OriEntrustDate()); // ί������
		getMsgMap.put("OriPackMsgNo", dto.get_OriPackMsgNo()); // ԭ�����ı��
		getMsgMap.put("OriSendOrgCode", dto.get_OriSendOrgCode()); // ԭ�����������
		getMsgMap.put("OriEntrustDate", dto.get_OriEntrustDate().replaceAll("-", "")); // ԭί������
		getMsgMap.put("OriPackNo", dto.get_OriPackNo()); // ԭ����ˮ��
		getMsgMap.put("OrgType", MsgConstant.MSG_ORG_TYPE_FINA); // ��������[����]
		
		msgMap.put("GetMsg9111", getMsgMap);

		return map;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

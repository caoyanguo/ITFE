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
import com.cfcc.itfe.persistence.dto.HeadDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;

public class Dto2MapFor9116 {

	private static Log logger = LogFactory.getLog(Dto2MapFor9116.class);

	/**
	 * ������Ϣת��
	 * 
	 * @param dto
	 * @param orgcode
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(HeadDto headto, TvSendlogDto dto)
			throws ITFEBizException {
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> cfxMap = new HashMap<String, Object>();
		HashMap<String, Object> headMap = new HashMap<String, Object>();
		HashMap<String, Object> msgMap = new HashMap<String, Object>();

		// ���ñ��Ľڵ� CFX
		map.put("cfx", cfxMap);
		cfxMap.put("HEAD", headMap);
		cfxMap.put("MSG", msgMap);

		// ����ͷ HEAD
		headMap.put("VER", headto.get_VER());
		headMap.put("SRC", headto.get_SRC());
		headMap.put("DES", headto.get_DES());
		headMap.put("APP", headto.get_APP());
		headMap.put("MsgNo", headto.get_msgNo());
		headMap.put("WorkDate", headto.get_workDate());
		headMap.put("MsgID", headto.get_msgID());
		headMap.put("MsgRef", headto.get_msgRef());

		HashMap<String, Object> getMsgMap = new HashMap<String, Object>();
		// ������GETMSG
		getMsgMap.put("SendOrgCode", dto.getSsendorgcode()); // �����������
		getMsgMap.put("EntrustDate", dto.getSdate().replaceAll("-", "")); // ί������
		getMsgMap.put("OriPackMsgNo", dto.getSoperationtypecode()); // ԭ�����ı��
		getMsgMap.put("OriChkDate", dto.getSsenddate().replaceAll("-", "")); // ԭ��������
		getMsgMap.put("OriChkAcctType", dto.getSifsend()); // ԭ�ļ���������
		getMsgMap.put("OriReportMonth", dto.getSdemo()); // ԭ������������
		getMsgMap.put("OirDocNameMonth", dto.getSretcodedesc()); // ԭ�ļ�����������

		msgMap.put("GetMsg9116", getMsgMap);

		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

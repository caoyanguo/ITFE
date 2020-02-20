package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvFreeDto;

/**
 * ��ֵ�����
 * 
 * @author zhuguozhen
 * 
 */
public class Dto2MapFor1106 {

	private static Log logger = LogFactory.getLog(Dto2MapFor1104.class);

	/**
	 * DTOת��XML����(��ֵ�����)�ɽӿ�
	 * 
	 * @param List
	 *            <TvFreeDto> list ���ͱ��Ķ��󼯺�
	 * @param String
	 *            orgcode ��������
	 * @param String
	 *            filename �ļ�����
	 * @param String
	 *            packno ����ˮ��
	 * @param boolean isRepeat �Ƿ��ط�����
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(List<TvFreeDto> list, String orgcode,
			String filename, String packno, boolean isRepeat)
			throws ITFEBizException {
		if (null == list || list.size() == 0) {
			throw new ITFEBizException("Ҫת���Ķ���Ϊ��,��ȷ��!");
		}

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
		headMap.put("MsgNo", MsgConstant.MSG_NO_1106);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		String msgid = "";
		try {
			msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʱ�����쳣��", e);
			throw new ITFEBizException("ȡ������ˮ��ʱ�����쳣��", e);
		}

		// ���ñ�����Ϣ�� MSG
		HashMap<String, Object> head1106Map = new HashMap<String, Object>();
		// ���ջ��ش���
		head1106Map.put("TaxOrgCode", list.get(0).getStaxorgcode());
		// ί������
		head1106Map.put("EntrustDate", list.get(0).getDacceptdate().toString()
				.replaceAll("-", ""));
		// ����ˮ��
		head1106Map.put("PackNo", list.get(0).getSpackno());
		// �ܱ���
		head1106Map.put("AllNum", String.valueOf(list.size()));
		// �ܽ��-��ֵ������
		BigDecimal allamt = new BigDecimal("0.00");

		List<Object> bill1106List = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> bill1106Map = new HashMap<String, Object>();
			TvFreeDto maindto = list.get(i);
			// ��ֵ������׽��+��ֵ������׽��
			allamt = allamt.add(maindto.getFfreepluamt());
			// ������ˮ��
			bill1106Map.put("TraNo", maindto.getStrano());
			// ��Ʊ����
			bill1106Map.put("BillDate", maindto.getDbilldate().toString()
					.replaceAll("-", ""));
			// ��ֵ�ƾ֤���
			bill1106Map.put("FreeVouNo", maindto.getSfreevouno());
			// ����ƾ֤����
			bill1106Map.put("ElectroTaxVouNo", maindto.getSelectrotaxvouno());
			// ��ֵ���Ԥ������
			bill1106Map.put("FreePluType", maindto.getCfreeplutype());
			// ��ֵ���Ԥ���Ŀ����
			bill1106Map.put("FreePluSubjectCode", maindto
					.getSfreeplusubjectcode());
			// ��ֵ���Ԥ�㼶�δ���
			bill1106Map.put("FreePluLevCode", maindto.getCfreeplulevel());
			// ��ֵ���������־
			bill1106Map.put("FreePluSign", maindto.getSfreeplusign());
			// ��ֵ����տ�������
			bill1106Map.put("FreePluPTreCode", maindto.getSfreepluptrecode());
			// ��ֵ������׽��
			bill1106Map.put("FreePluAmt", MtoCodeTrans.transformString(maindto
					.getFfreepluamt()));
			// ��ֵ���Ԥ������
			bill1106Map.put("FreeMiKind", maindto.getCfreemikind());
			// ��ֵ���Ԥ���Ŀ����
			bill1106Map.put("FreeMiSubject", maindto.getSfreemisubject());
			// ��ֵ���Ԥ�㼶�δ���
			bill1106Map.put("FreeMiLevel", maindto.getCfreemilevel());
			// ��ֵ���������־
			bill1106Map.put("FreeMiSign", maindto.getSfreemisign());
			// ��ֵ����տ�������
			bill1106Map.put("FreeMiPTre", maindto.getSfreemiptre());
			// ��ֵ������׽��
			bill1106Map.put("FreeMiAmt", maindto.getFfreemiamt());
			// �����ڱ�־
			bill1106Map.put("TrimSign", maindto.getCtrimsign());
			// ��ҵ����
			bill1106Map.put("CorpCode", maindto.getScorpcode());

			bill1106List.add(bill1106Map);
		}
		head1106Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead1106", head1106Map);
		msgMap.put("FreeInfo1106", bill1106List);

		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

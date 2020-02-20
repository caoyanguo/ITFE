package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceSubDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * ���������Կ�������
 * 
 * @author wangyunbin
 * 
 */
public class Dto2MapFor5112 {

	private static Log logger = LogFactory.getLog(Dto2MapFor5112.class);
	
	/**
	 * DTOת��XML����(���������Կ�������)
	 * 
	 * @param List
	 *            <TvDirectpaymsgmainDto> list ���ͱ��Ķ��󼯺�
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
	public static Map tranfor(List<TvPayoutfinanceMainDto> list, String orgcode, String filename, String packno,boolean isRepeat) throws ITFEBizException {
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
		headMap.put("MsgNo", MsgConstant.MSG_NO_5112);
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
		HashMap<String, Object> head5112Map = new HashMap<String, Object>();
		head5112Map.put("TreCode", list.get(0).getStrecode()); // �������
		head5112Map.put("BillOrg", list.get(0).getSbillorg()); // ��Ʊ��λ
		head5112Map.put("PayeeBankNo", list.get(0).getSpayeebankno()); // �տ����к�
		head5112Map.put("PayerAcct", list.get(0).getSpayeracct()); // �������˺�
		head5112Map.put("PayerName", list.get(0).getSpayername()); // ����������
		head5112Map.put("PayerAddr", list.get(0).getSpayeraddr()); // �����˵�ַ
		head5112Map.put("EntrustDate", list.get(0).getSentrustdate().replaceAll("-", "")); // ί������
		head5112Map.put("PackNo", list.get(0).getSpackageno()); // ����ˮ��
		BigDecimal allamt = new BigDecimal("0.00"); // �ܽ��-��ֵ������
		head5112Map.put("PayoutVouType", MsgConstant.PAYOUT_VOU_TYPE_PAPER_YES); // ֧��ƾ֤����
																					// 0����ֽƾ֤1����ֽƾ֤
		HashMap<String, Object> bill5112Map = new HashMap<String, Object>();
		List<Object> detail5112List = new ArrayList<Object>();

		TvPayoutfinanceMainDto maindto = list.get(0);
		List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(maindto);
		if (null == sublist || sublist.size() == 0) {
			throw new ITFEBizException("Ҫת���Ķ����Ӧ���Ӽ�¼Ϊ��,��ȷ��![" + list.get(0).toString() + "]");
		}
		head5112Map.put("AllNum", String.valueOf(sublist.size())); // �ܱ���
		allamt = maindto.getNamt();

		bill5112Map.put("VouNo", maindto.getSvouno()); // ƾ֤���
		bill5112Map.put("VouDate", maindto.getSvoudate().replaceAll("-", "")); // ƾ֤����[��Ʊ����]
		bill5112Map.put("AddWord", maindto.getSaddword()); // ����
		bill5112Map.put("BudgetType", maindto.getSbudgettype()); // Ԥ������
		bill5112Map.put("BdgOrgCode", maindto.getSbdgorgcode()); // Ԥ�㵥λ����
		bill5112Map.put("FuncSbtCode", maindto.getSfuncsbtcode()); // �������Ŀ����
		bill5112Map.put("EcnomicSubjectCode", maindto.getSecnomicsubjectcode()); // �������Ŀ����
		bill5112Map.put("Amt", MtoCodeTrans.transformString(maindto.getNamt())); //���(������ϸ���׽��֮��)

		// ѭ������Ϣ
		for (int j = 0; j < sublist.size(); j++) {
			HashMap<String, Object> detail5112Map = new HashMap<String, Object>();
			TvPayoutfinanceSubDto subdto = (TvPayoutfinanceSubDto) sublist.get(j);
			detail5112Map.put("TraNo", subdto.getStrano());//������ˮ��
			detail5112Map.put("PayeeAcct", subdto.getSpayeeacct());//�տ����˺�
			detail5112Map.put("PayeeName", subdto.getSpayeename());//�տ�������
			detail5112Map.put("PayeeAddr", subdto.getSpayeeaddr());//�տ��˵�ַ
			detail5112Map.put("PayeeOpnBnkNo", MtoCodeTrans.transformString(subdto.getSpayeeopnbnkno()));//�տ��˿������к� 
			detail5112Map.put("Amt", MtoCodeTrans.transformString(subdto.getNsubamt()));
			detail5112List.add(detail5112Map);
		}

		bill5112Map.put("Detail5112", detail5112List);


		head5112Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead5112", head5112Map);
		msgMap.put("Bill5112", bill5112Map);

		// ���±���ͷ��״̬Ϊ������
		String updateFileSQL = "update TV_FILEPACKAGEREF set S_RETCODE = ? where S_ORGCODE = ? and S_TRECODE = ? and S_FILENAME = ? and S_PACKAGENO = ? ";
		SQLExecutor sqlUpdateExec;
		try {
			sqlUpdateExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlUpdateExec.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING); // ������
			sqlUpdateExec.addParam(orgcode);
			sqlUpdateExec.addParam(list.get(0).getStrecode());
			sqlUpdateExec.addParam(filename);
			sqlUpdateExec.addParam(packno);
			sqlUpdateExec.runQueryCloseCon(updateFileSQL);
		} catch (JAFDatabaseException e) {
			logger.error("���±���ͷ��ʱ������쳣��", e);
			throw new ITFEBizException("���±���ͷ��ʱ������쳣��", e);
		}
		
		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

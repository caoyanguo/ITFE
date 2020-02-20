package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.sql.Date;
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
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

public class Dto2MapFor11052 {
	
	private static Log logger = LogFactory.getLog(Dto2MapFor11052.class);
	
	/**
	 * DTOת��XML����(��������)Tips�°汾�ӿ�
	 * 
	 * @param List
	 *            <TvInfileDto> list ���ͱ��Ķ��󼯺�
	 * @param String
	 *            orgcode ��������
	 * @param String
	 *            filename �ļ�����
	 * @param String
	 *            packno ����ˮ��
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(List<TvInCorrhandbookDto> list,String orgcode,String filename,String packno ,boolean isRepeat) throws ITFEBizException{
		if(null == list || list.size() == 0){
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
		headMap.put("MsgNo",MsgConstant.MSG_NO_1105);
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
		HashMap<String, String> batchHeadMap = new HashMap<String, String>();
		batchHeadMap.put("TaxOrgCode", list.get(0).getScurtaxorgcode());
		batchHeadMap.put("EntrustDate", list.get(0).getDaccept().toString().replaceAll("-", ""));
		batchHeadMap.put("PackNo", list.get(0).getSpackageno());
		batchHeadMap.put("AllNum", String.valueOf(list.size())); // TODO��Ҫ��������
		BigDecimal allamt = new BigDecimal("0.00");//�������н��׵� ���ֽ��׽�ԭ�н�֮�͡�һ������£���ֵΪ0
//		batchHeadMap.put("AllAmt", "0.00");
		
		
		List<Object> correctBodyList = new ArrayList<Object>();
		SQLExecutor updateExce = null;
		
		try {
			for (int i = 0; i < list.size(); i++) {
				allamt =allamt.add(list.get(i).getFcurcorramt().subtract(list.get(i).getForicorramt()));
				//allamt = allamt.add(list.get(i).getNmoney());
				HashMap<String, Object> correctBody1105Map = new HashMap<String, Object>();
				HashMap<String, Object> correctInfo1105Map = new HashMap<String, Object>();
				HashMap<String, Object> correctOld1105Map = new HashMap<String, Object>();
				HashMap<String, Object> correctNow1105Map = new HashMap<String, Object>();
				correctBody1105Map.put("CorrectInfo1105", correctInfo1105Map);
				correctBody1105Map.put("CorrectOld1105", correctOld1105Map);
				correctBody1105Map.put("CorrectNow1105", correctNow1105Map);
				
				correctInfo1105Map.put("TraNo", list.get(i).getSdealno());//������ˮ��
				correctInfo1105Map.put("OriTraNo", "");//ԭ������ˮ��
				String dvoucher = list.get(i).getDvoucher();
				logger.info("============info==============="+" dvoucher ::"+dvoucher+", toString()=>"+dvoucher.toString());
				correctInfo1105Map.put("BillDate", MtoCodeTrans.transformString(dvoucher.toString().replaceAll("-", "")));//��Ʊ����
				correctInfo1105Map.put("CorrVouNo", list.get(i).getScorrvouno());//����ƾ֤���
				correctInfo1105Map.put("ElectroTaxVouNo", list.get(i).getSelecvouno());//����ƾ֤����
				correctInfo1105Map.put("TrimSign", list.get(i).getCtrimflag());//�����ڱ�־(�°汾ɾ�������ڱ�־)
				correctInfo1105Map.put("CorrReaCode", list.get(i).getSreasoncode());//����ԭ�����
				correctInfo1105Map.put("Remark", "");//��ע
				
				correctOld1105Map.put("OriTaxVouNo", "");//ԭ˰Ʊ����
				correctOld1105Map.put("OriTaxOrgCode", list.get(i).getSoritaxorgcode());//ԭ���ջ��ش���
				correctOld1105Map.put("OriBudgetType", list.get(i).getCoribdgkind());//ԭԤ������
				correctOld1105Map.put("OriBudgetSubjectCode", list.get(i).getSoribdgsbtcode());//ԭ��Ԥ���Ŀ����
				correctOld1105Map.put("OriBudgetLevCode", list.get(i).getCoribdglevel());//ԭ��Ԥ�㼶�δ���
				correctOld1105Map.put("OriTrimSign", list.get(i).getCtrimflag());//ԭ�����ڱ�־
				if(list.get(i).getCoribdglevel().equals(MsgConstant.BUDGET_LEVEL_SHARE)){
					correctOld1105Map.put("OriViceSign", list.get(i).getSoriastflag());//ԭ������־
				}
				correctOld1105Map.put("OriTreCode", list.get(i).getSoripayeetrecode());//ԭ�տ�������
				correctOld1105Map.put("OriRevAmt", MtoCodeTrans.transformString(list.get(i).getForicorramt()));//ԭ�н��
				
				correctNow1105Map.put("CorTaxOrgCode", list.get(i).getScurtaxorgcode());//�������ջ��ش���
				correctNow1105Map.put("CurBudgetType", list.get(i).getCcurbdgkind());//��Ԥ������
				correctNow1105Map.put("CurBudgetSubjectCode", list.get(i).getScurbdgsbtcode());//��Ԥ���Ŀ����
				correctNow1105Map.put("CurBudgetLevCode", list.get(i).getCcurbdglevel());//��Ԥ�㼶�δ���
				correctNow1105Map.put("CurTrimSign", list.get(i).getCtrimflag());//�������ڱ�־
				correctNow1105Map.put("CurViceSign", list.get(i).getScurastflag());//�ָ�����־
				correctNow1105Map.put("CurTreCode", list.get(i).getScurpayeetrecode());//���տ�������
				correctNow1105Map.put("CurTraAmt", MtoCodeTrans.transformString(list.get(i).getFcurcorramt()));//�ֽ��׽��
				correctBodyList.add(correctBody1105Map);
			}
			
			batchHeadMap.put("AllAmt", String.valueOf(allamt));
			
			msgMap.put("BatchHead1105", batchHeadMap);
			msgMap.put("CorrectBody1105", correctBodyList);
		
			TvVoucherinfoDto vDto = new TvVoucherinfoDto();
			vDto.setSdealno(String.valueOf(list.get(0).getIvousrlno()));
			vDto = (TvVoucherinfoDto) DatabaseFacade.getDb().getODB().find(vDto);
			updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String updateSql ="update "+TvFilepackagerefDto.tableName()+" set  S_MSGID = ? , T_SENDTIME = CURRENT TIMESTAMP where  S_PACKAGENO = ? and S_TRECODE = ? and S_COMMITDATE = ?";
			updateExce.clearParams();
			updateExce.addParam(msgid);
			//updateExce.addParam(new Timestamp(TimeFacade.getCurrentDateTime().getTime()));
			updateExce.addParam(list.get(0).getSpackageno());
			updateExce.addParam(vDto==null?list.get(0).getSoripayeetrecode():vDto.getStrecode());//����������� 
			updateExce.addParam(list.get(0).getDaccept().toString().replaceAll("-", ""));////ί������
			updateExce.runQuery(updateSql);
			updateExce.closeConnection();
			
		} catch (Exception e) {
			String error = "���¸��������ִ״̬ʱ�������ݿ��쳣��";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (null != updateExce) {
				updateExce.closeConnection();
			}
		}
		return map;
	}

}

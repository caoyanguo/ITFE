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
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

public class Dto2MapFor1103 {
	
	private static Log logger = LogFactory.getLog(Dto2MapFor1103.class);
	
	/**
	 * DTOת��XML����(����˰Ʊҵ��)
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
	public static Map tranfor(List<TvInfileDto> list,String orgcode,String filename,String packno ) throws ITFEBizException{
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
		headMap.put("MsgNo",MsgConstant.MSG_NO_1103);
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
		batchHeadMap.put("TaxOrgCode", list.get(0).getStaxorgcode());
		batchHeadMap.put("EntrustDate", list.get(0).getScommitdate().replaceAll("-", ""));
		batchHeadMap.put("PackNo", list.get(0).getSpackageno());
		batchHeadMap.put("AllNum", String.valueOf(list.size())); // TODO��Ҫ��������
		BigDecimal allamt = new BigDecimal("0.00");
//		batchHeadMap.put("AllAmt", "0.00");
		
		HashMap<String, String> turnAccountMap = new HashMap<String, String>();
		turnAccountMap.put("PayeeOrgCode", list.get(0).getSrecvtrecode());
		turnAccountMap.put("PayeeAcct", "2560");// TODO ���ΪĬ��ֵ������Ҫ�޸�
		turnAccountMap.put("PayeeName", "");//�ɿղ�����д ---------------------------------
		
		List<Object> paymentList = new ArrayList<Object>();
		
		for (int i = 0; i < list.size(); i++) {
			allamt = allamt.add(list.get(i).getNmoney());
			HashMap<String, Object> paymentMap = new HashMap<String, Object>();
			paymentMap.put("TraNo", list.get(i).getSdealno());
			paymentMap.put("HandOrgName", "�ط�����ҵ��"); // TODO ���ΪĬ��ֵ������Ҫ�޸�
			paymentMap.put("TraAmt", list.get(i).getNmoney());
			paymentMap.put("TaxVouNo", list.get(i).getStaxticketno());
			paymentMap.put("BillDate", list.get(i).getSaccdate());
			paymentMap.put("TaxPayCode", "10101");// TODO ���ΪĬ��ֵ������Ҫ�޸�
			paymentMap.put("TaxPayName", "�ط�����");// TODO ���ΪĬ��ֵ������Ҫ�޸�
			paymentMap.put("CorpCode", ""); // �ɿղ�����д ---------------------------------
			paymentMap.put("BudgetType", list.get(i).getSbudgettype());
			paymentMap.put("TrimSign", list.get(i).getStrimflag());
			paymentMap.put("CorpType", ""); // �ɿղ�����д ---------------------------------
			paymentMap.put("Remark", ""); // �ɿղ�����д ---------------------------------
			paymentMap.put("Remark1", ""); // �ɿղ�����д ---------------------------------
			paymentMap.put("Remark2", ""); // �ɿղ�����д ---------------------------------
			paymentMap.put("TaxTypeNum", 1);
			
			
			List<Object> taxTypeList = new ArrayList<Object>();
			HashMap<String, Object> taxTypeListMap = new HashMap<String, Object>();
			taxTypeListMap.put("ProjectId", 1);
			taxTypeListMap.put("BudgetSubjectCode", list.get(i).getSbudgetsubcode());
			taxTypeListMap.put("LimitDate", list.get(i).getSaccdate().replaceAll("-", ""));
			taxTypeListMap.put("TaxTypeName", "");// �ɿղ�����д,��Ҫί�����д�ӡ����ƾ֤ʱ���� ---------------------------------
			taxTypeListMap.put("BudgetLevelCode", list.get(i).getSbudgetlevelcode());
			taxTypeListMap.put("BudgetLevelName", "Ԥ����");// ���ݼ���ȥȡ���� 
			taxTypeListMap.put("TaxStartDate", list.get(i).getSaccdate().replaceAll("-", ""));
			taxTypeListMap.put("TaxEndDate", list.get(i).getSaccdate().replaceAll("-", ""));
			
			if(null == list.get(i).getSassitsign() || "".equals(list.get(i).getSassitsign().trim())){
				taxTypeListMap.put("ViceSign", "");
			}else{
				taxTypeListMap.put("ViceSign", list.get(i).getSassitsign());
			}
			
			taxTypeListMap.put("TaxType", "1");
			taxTypeListMap.put("TaxTypeAmt", list.get(i).getNmoney());
			taxTypeListMap.put("DetailNum", 1);
			
			List<Object> taxSubjectList = new ArrayList<Object>();
			HashMap<String, Object> taxSubjectListMap = new HashMap<String, Object>();
			taxSubjectListMap.put("DetailNo", "1");
			taxSubjectListMap.put("TaxSubjectCode", "1");
			taxSubjectListMap.put("TaxSubjectName", "1");
			taxSubjectListMap.put("TaxNumber", "1");
			taxSubjectListMap.put("TaxAmt", "1");
			taxSubjectListMap.put("TaxRate", "1");
			taxSubjectListMap.put("ExpTaxAmt", "1");
			taxSubjectListMap.put("DiscountTaxAmt", "1");
			taxSubjectListMap.put("FactTaxAmt", list.get(i).getNmoney());
			taxSubjectList.add(taxSubjectListMap);
				
			taxTypeListMap.put("TaxSubjectList1103", taxSubjectList);
			taxTypeList.add(taxTypeListMap);
			paymentMap.put("TaxTypeList1103", taxTypeList);
			paymentList.add(paymentMap);
		}
		
		batchHeadMap.put("AllAmt", String.valueOf(allamt));
		
		msgMap.put("BatchHead1103", batchHeadMap);
		msgMap.put("TurnAccount1103", turnAccountMap);
		msgMap.put("Payment1103", paymentList);
		
		TvSendlogDto sendlogDto = new TvSendlogDto();
		try {
			sendlogDto.setSsendno(StampFacade.getStampSendSeq("FS"));
		} catch (SequenceException e) {
			throw new ITFEBizException("ȡ������ˮ��ʧ��",e);
		}
		sendlogDto.setSrecvno("");//��Ӧ������־��ˮ��
		sendlogDto.setSsendorgcode(ITFECommonConstant.DES_NODE);//���ͻ�������
		sendlogDto.setSdate(list.get(0).getScommitdate());//��������
		sendlogDto.setSoperationtypecode(MsgConstant.MSG_NO_1103);//ҵ��ƾ֤����
		sendlogDto.setSrecvorgcode("");//���ջ���
		sendlogDto.setStitle(filename);//ƾ֤����(?? ·��)
		sendlogDto.setSsendtime(Timestamp.valueOf(TimeFacade.getCurrent2StringTime()));//����ʱ��
		sendlogDto.setSretcode(DealCodeConstants.DEALCODE_ITFE_SEND)   ;//������
		sendlogDto.setSretcodedesc("");//����˵��
		sendlogDto.setIcount(list.size());//����
		sendlogDto.setNmoney(allamt) ;//���
		sendlogDto.setSusercode("");//����Ա����
		sendlogDto.setSdemo("");//��ע
		sendlogDto.setSseq(msgid);//����ID 
		sendlogDto.setSstate(null);//����״̬
		sendlogDto.setSpackno(list.get(0).getSpackageno());//����ˮ��
		sendlogDto.setStrecode(list.get(0).getSrecvtrecode());//�������
		sendlogDto.setSpayeebankno("");//�տ����к�
		sendlogDto.setSbillorg(list.get(0).getSunitcode());//��Ʊ��λ
		sendlogDto.setSpayoutvoutype("");//֧��ƾ֤����
		sendlogDto.setSproctime(Timestamp.valueOf(TimeFacade.getCurrent2StringTime()));//S_PROCTIME
		sendlogDto.setSsenddate(TimeFacade.getCurrentDate().toString());//�������� 
		sendlogDto.setSifsend("");//�Ƿ�ת��
		sendlogDto.setSturnsendflag("");//ת����־
		try {
			DatabaseFacade.getDb().create(sendlogDto);
		} catch (JAFDatabaseException e) {
			logger.error("���淢����־�����쳣��", e);
			throw new ITFEBizException("���淢����־��ʱ������쳣��", e);
		}
		SQLExecutor updateExce = null;
		try {
			updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String updateSql ="update "+TvFilepackagerefDto.tableName()+" set  S_MSGID = ? , T_SENDTIME = CURRENT TIMESTAMP where  S_PACKAGENO = ? S_TRECODE = ? ,S_COMMITDATE = ?";
				updateExce.clearParams();
				updateExce.addParam(msgid);
				//updateExce.addParam(new Timestamp(TimeFacade.getCurrentDateTime().getTime()));
				updateExce.addParam(list.get(0).getSpackageno());
				updateExce.addParam(list.get(0).getSrecvtrecode());//����������� 
				updateExce.addParam(list.get(0).getScommitdate().replaceAll("-", ""));////ί������
				updateExce.runQuery(updateSql);
			updateExce.closeConnection();
			
		} catch (JAFDatabaseException e) {
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

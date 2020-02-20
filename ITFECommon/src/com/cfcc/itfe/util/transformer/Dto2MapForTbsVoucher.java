package com.cfcc.itfe.util.transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfFundAppropriationDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

public class Dto2MapForTbsVoucher {

	private static Log logger = LogFactory.getLog(Dto2MapForTbsVoucher.class);

	/**
	 * ������Ϣת��
	 * 
	 * @param dto
	 * @param orgcode
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(TvVoucherinfoDto vdto) throws ITFEBizException {
		try {
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
			headMap.put("SRC", "111111111111");
			headMap.put("DES", ITFECommonConstant.TBS_TREANDBANK.get(vdto.getStrecode()) + "000000000");
			headMap.put("APP", "TCQS");
			headMap.put("MsgNo", MsgConstant.MSG_TBS_NO_1000);
			headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
			if(vdto.getSpackno() == null){ //���������Ƕ��˲�����1000���ģ����ǵ�һ�η��͵��ʽ��ġ���һ�η���ʱ����ˮ��Ϊ��
				vdto.setSpackno(SequenceGenerator
						.changePackNoForLocal(SequenceGenerator.getNextByDb2(
								SequenceName.FILENAME_PACKNO_REF_SEQ,
								SequenceName.TRAID_SEQ_CACHE,
								SequenceName.TRAID_SEQ_STARTWITH,
								MsgConstant.SEQUENCE_MAX_DEF_VALUE)));
			}else{ //�������ʽ��ģ�Ҫ��ԭ�ʽ��(TF_FUND_APPROPRIATION)��msgid��msgref���³����ڲ�����msgid��msgref����Ϊ�յ��������ִ�����ԭmsgid�����ʽ��״̬
				TfFundAppropriationDto fundDto = new TfFundAppropriationDto();
				fundDto.setStrecode(vdto.getStrecode());
				fundDto.setSpackno(vdto.getSpackno());
//				fundDto.setSentrustdate(vdto.getScreatdate());
				List<TfFundAppropriationDto> tmpList = CommonFacade.getODB().findRsByDto(fundDto);
				if (null == tmpList || tmpList.size()==0) {
					logger.error("δ��ѯ��ԭ���͵��ʽ���");
					throw new ITFEBizException("δ��ѯ��ԭ���͵��ʽ���");
				}
				fundDto = tmpList.get(0);
				fundDto.setSmsgid(msgid);
				fundDto.setSmsgref(msgid);
				DatabaseFacade.getODB().update(fundDto);
			}

			if (MsgConstant.VOUCHER_NO_2301.equals(vdto.getSvtcode())) {
				tranfor2301(map, vdto);
			} else if (MsgConstant.VOUCHER_NO_2302.equals(vdto.getSvtcode())) {
				tranfor2302(map, vdto);
			} else if (MsgConstant.VOUCHER_NO_5207.equals(vdto.getSvtcode())) {
				tranfor5207(map, vdto);
			} else if (MsgConstant.VOUCHER_NO_5209.equals(vdto.getSvtcode())) {
				tranfor5209(map, vdto);
			}else if ("5230".equals(vdto.getSvtcode())){//tbs������˿�
				tranfor5230(map, vdto);
			} else {
				logger.error("�������Ͳ�ƥ�䣡");
				throw new ITFEBizException("�������Ͳ�ƥ�䣡");
			}
			//�ʽ������и���������״̬
			vdto.setSext1("40");
			vdto.setSdemo("�ʽ�������");
			DatabaseFacade.getODB().update(vdto);
			return map;
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʱ�����쳣��", e);
			throw new ITFEBizException("ȡ������ˮ��ʱ�����쳣��", e);
		} catch (JAFDatabaseException e) {
			logger.error("ȡ������ˮ��ʱ�����쳣��", e);
			throw new ITFEBizException("ȡ������ˮ��ʱ�����쳣��", e);
		} catch (ValidateException e) {
			logger.error("δ��ѯ��ԭ���͵��ʽ���");
			throw new ITFEBizException("δ��ѯ��ԭ���͵��ʽ���");
		}
	}
	
	/**
	 * ������Ϣת��
	 * 
	 * @param dto
	 * @param orgcode
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(TfFundAppropriationDto vdto) throws ITFEBizException {
		try {
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
			headMap.put("SRC", "111111111111");
			headMap.put("DES", vdto.getSpayeebankno().substring(0, 3) + "000000000");
			headMap.put("APP", "TCQS");
			headMap.put("MsgNo", MsgConstant.MSG_TBS_NO_1000);
			headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
			vdto.setSmsgid(msgid);
			vdto.setSmsgref(msgid);
			vdto.setSpackno(SequenceGenerator
					.changePackNoForLocal(SequenceGenerator.getNextByDb2(
							SequenceName.FILENAME_PACKNO_REF_SEQ,
							SequenceName.TRAID_SEQ_CACHE,
							SequenceName.TRAID_SEQ_STARTWITH,
							MsgConstant.SEQUENCE_MAX_DEF_VALUE)));
			vdto.setStrano(vdto.getSpackno());
			tranfor5209(map, vdto);
			//�ʽ������и���������״̬
			DatabaseFacade.getODB().create(vdto);
			return map;
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʱ�����쳣��", e);
			throw new ITFEBizException("ȡ������ˮ��ʱ�����쳣��", e);
		} catch (JAFDatabaseException e) {
			logger.error("ȡ������ˮ��ʱ�����쳣��", e);
			throw new ITFEBizException("ȡ������ˮ��ʱ�����쳣��", e);
		}
	}

	private static void tranfor5209(Map map, TfFundAppropriationDto vDto) throws ITFEBizException {
			Map<String, String> BatchHead1000 = new HashMap<String, String>();
			BatchHead1000.put("BillOrg", vDto.getSbillorg());
			BatchHead1000.put("EntrustDate", vDto.getSentrustdate());
			BatchHead1000.put("PackNo", vDto.getSpackno());
			BatchHead1000.put("TreCode", vDto.getStrecode());
			BatchHead1000.put("AllNum", "1");
			BatchHead1000.put("AllAmt", MtoCodeTrans.transformString(vDto.getNallamt()));
			BatchHead1000.put("PayoutVouType", "2");

			Map<String, Object> msgMap = (Map<String, Object>) ((Map) map
					.get("cfx")).get("MSG");
			msgMap.put("BatchHead1000", BatchHead1000);
			List list = new ArrayList();
			Map<String, String> BillSend1000 = new HashMap<String, String>();
			BillSend1000.put("TraNo", vDto.getSpackno()); // ������ˮ��
			BillSend1000.put("VouNo", vDto.getSvouno()); // ƾ֤���
			BillSend1000.put("VouDate", vDto.getSvoudate()); // ƾ֤����
			BillSend1000.put("PayerAcct", vDto.getSpayeracct()); // �������˺�
			BillSend1000.put("PayerName", vDto.getSpayername()); // ����������
			BillSend1000.put("PayerAddr", ""); // �����˵�ַ
			BillSend1000.put("Amt", MtoCodeTrans.transformString(vDto.getNamt())); // ���
			BillSend1000
					.put("PayeeBankNo", vDto.getSpayeebankno()); // �տ����к�
			BillSend1000.put("PayeeOpBkNo", vDto.getSpayeeopbkno()); // �տ��˿������к�
			BillSend1000.put("PayeeAcct", vDto.getSpayeeacct()); // �տ����˺�
			BillSend1000.put("PayeeName", vDto.getSpayeename()); // �տ�������
			BillSend1000.put("PayReason", ""); // ������˿�ԭ��
			BillSend1000.put("BudgetSubjectCode", ""); // Ԥ���Ŀ����
			BillSend1000.put("AddWord", ""); // ����
			BillSend1000.put("OfYear", vDto.getSofyear()); // �������
			BillSend1000.put("Flag", getBankTypeFlag(vDto.getSpayeebankno(), ITFECommonConstant.TBS_TREANDBANK.get(vDto.getStrecode()))); // �տ��ʶ
			list.add(BillSend1000);
			msgMap.put("BillSend1000", list);
	}
	
	
	private static void tranfor5209(Map map, TvVoucherinfoDto vDto) throws ITFEBizException {
		try {
			TvDwbkDto dwbkDto = new TvDwbkDto();
			dwbkDto.setSbizno(vDto.getSdealno());
			dwbkDto = (TvDwbkDto) CommonFacade.getODB().findRsByDto(dwbkDto).get(0);
			Map<String, String> BatchHead1000 = new HashMap<String, String>();
			BatchHead1000.put("BillOrg", vDto.getSpaybankcode());
			BatchHead1000.put("EntrustDate", TimeFacade.getCurrentStringTime());
			BatchHead1000.put("PackNo", vDto.getSpackno());
			BatchHead1000.put("TreCode", vDto.getStrecode());
			BatchHead1000.put("ChangeNo", "");
			BatchHead1000.put("AllNum", "1");
			BatchHead1000.put("AllAmt", MtoCodeTrans.transformString(dwbkDto.getFamt()));
			BatchHead1000.put("PayoutVouType", "2");

			Map<String, Object> msgMap = (Map<String, Object>) ((Map) map
					.get("cfx")).get("MSG");
			msgMap.put("BatchHead1000", BatchHead1000);
			List list = new ArrayList();
			Map<String, String> BillSend1000 = new HashMap<String, String>();
			BillSend1000.put("TraNo", vDto.getSpackno()); // ������ˮ��
			BillSend1000.put("VouNo", vDto.getSvoucherno()); // ƾ֤���
			BillSend1000.put("VouDate", vDto.getSext4()); // ƾ֤����
			BillSend1000.put("PayerAcct", dwbkDto.getSpayacctno()); // �������˺�
			BillSend1000.put("PayerName", dwbkDto.getSpayacctname()); // ����������
			BillSend1000.put("PayerAddr", ""); // �����˵�ַ
			BillSend1000.put("Amt", MtoCodeTrans.transformString(dwbkDto.getFamt())); // ���
			BillSend1000.put("PayeeBankNo", dwbkDto.getSpayeeopnbnkno()); // �տ����к�
			BillSend1000.put("PayeeOpBkNo", dwbkDto.getSpayeeopnbnkno()); // �տ��˿������к�
			BillSend1000.put("PayeeAcct", dwbkDto.getSpayeeacct()); // �տ����˺�
			BillSend1000.put("PayeeName", dwbkDto.getSpayeename()); // �տ�������
			BillSend1000.put("PayReason", ""); // ������˿�ԭ��
			BillSend1000.put("BudgetSubjectCode", dwbkDto.getSbdgsbtcode()); // Ԥ���Ŀ����
			BillSend1000.put("AddWord", ""); // ����
			BillSend1000.put("OfYear", vDto.getSstyear()); // �������
			BillSend1000.put("Flag", getBankTypeFlag(vDto.getSpaybankcode(), ITFECommonConstant.TBS_TREANDBANK.get(vDto.getStrecode()))); // �տ��ʶ
			list.add(BillSend1000);
			msgMap.put("BillSend1000", list);
			dwbkDto.setSpackageno(vDto.getSpackno());
			DatabaseFacade.getODB().update(dwbkDto);
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			logger.equals(e);
			throw new ITFEBizException("��ѯҵ������ʧ�ܣ�", e);
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.equals(e);
			throw new ITFEBizException("��ѯҵ������ʧ�ܣ�", e);
		}
	}
	
	/**
	 * TBS������˿�
	 * @param map
	 * @param vDto
	 * @throws ITFEBizException
	 */
	private static void tranfor5230(Map map, TvVoucherinfoDto vDto) throws ITFEBizException {
		try {
			TvDwbkDto dwbkDto = new TvDwbkDto();
			dwbkDto.setSbizno(vDto.getSdealno());
			dwbkDto = (TvDwbkDto) CommonFacade.getODB().findRsByDto(dwbkDto).get(0);
			Map<String, String> BatchHead1000 = new HashMap<String, String>();
			BatchHead1000.put("BillOrg", dwbkDto.getStaxorgcode());
			BatchHead1000.put("EntrustDate", TimeFacade.getCurrentStringTime());
			BatchHead1000.put("PackNo", vDto.getSpackno());
			BatchHead1000.put("TreCode", vDto.getStrecode());
			BatchHead1000.put("ChangeNo", "");
			BatchHead1000.put("AllNum", "1");
			BatchHead1000.put("AllAmt", MtoCodeTrans.transformString(dwbkDto.getFamt()));
			BatchHead1000.put("PayoutVouType", "2");

			Map<String, Object> msgMap = (Map<String, Object>) ((Map) map
					.get("cfx")).get("MSG");
			msgMap.put("BatchHead1000", BatchHead1000);
			List list = new ArrayList();
			Map<String, String> BillSend1000 = new HashMap<String, String>();
			BillSend1000.put("TraNo", vDto.getSpackno()); // ������ˮ��
			BillSend1000.put("VouNo", vDto.getSvoucherno()); // ƾ֤���
			BillSend1000.put("VouDate", vDto.getSext4()); // ƾ֤����
			BillSend1000.put("PayerAcct", dwbkDto.getSpayacctno()); // �������˺�
			BillSend1000.put("PayerName", dwbkDto.getSpayacctname()); // ����������
			BillSend1000.put("PayerAddr", ""); // �����˵�ַ
			BillSend1000.put("Amt", MtoCodeTrans.transformString(dwbkDto.getFamt())); // ���
			BillSend1000.put("PayeeBankNo", dwbkDto.getSrecbankname()); // �տ����к�
			BillSend1000.put("PayeeOpBkNo", dwbkDto.getSpayeeopnbnkno()); // �տ��˿������к�
			BillSend1000.put("PayeeAcct", dwbkDto.getSpayeeacct()); // �տ����˺�
			BillSend1000.put("PayeeName", dwbkDto.getSpayeename()); // �տ�������
			BillSend1000.put("PayReason", dwbkDto.getSreturnreasonname()); // ������˿�ԭ��
			BillSend1000.put("BudgetSubjectCode", dwbkDto.getSbdgsbtcode()); // Ԥ���Ŀ����
			BillSend1000.put("AddWord", dwbkDto.getSreturnreasonname()); // ����
			BillSend1000.put("OfYear", vDto.getSstyear()); // �������
			BillSend1000.put("Flag", getBankTypeFlag(vDto.getSpaybankcode(), ITFECommonConstant.TBS_TREANDBANK.get(vDto.getStrecode()))); // �տ��ʶ
			list.add(BillSend1000);
			msgMap.put("BillSend1000", list);
			dwbkDto.setSpackageno(vDto.getSpackno());
			DatabaseFacade.getODB().update(dwbkDto);
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			logger.equals(e);
			throw new ITFEBizException("��ѯҵ������ʧ�ܣ�", e);
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.equals(e);
			throw new ITFEBizException("��ѯҵ������ʧ�ܣ�", e);
		}
	}
	

	private static void tranfor2301(Map map, TvVoucherinfoDto vDto)
			throws ITFEBizException {
		SQLExecutor sqlExecutor = null;
		try {
			TvPayreckBankDto tvPayreckBankDto = new TvPayreckBankDto();
			tvPayreckBankDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
			tvPayreckBankDto = (TvPayreckBankDto) CommonFacade.getODB()
					.findRsByDto(tvPayreckBankDto).get(0);
			Map<String, String> BatchHead1000 = new HashMap<String, String>();
			BatchHead1000.put("BillOrg", tvPayreckBankDto.getSagentbnkcode());
			BatchHead1000.put("EntrustDate", TimeFacade.getCurrentStringTime());
			BatchHead1000.put("PackNo", vDto.getSpackno());
			BatchHead1000.put("TreCode", vDto.getStrecode());
			BatchHead1000.put("ChangeNo", "");
			BatchHead1000.put("AllNum", "1");
			BatchHead1000.put("AllAmt", MtoCodeTrans.transformString(vDto
					.getNmoney()));
			BatchHead1000.put("PayoutVouType", "3");

			Map<String, Object> msgMap = (Map<String, Object>) ((Map) map
					.get("cfx")).get("MSG");
			msgMap.put("BatchHead1000", BatchHead1000);
			List list = new ArrayList();
			Map<String, String> BillSend1000 = new HashMap<String, String>();
			BillSend1000.put("TraNo", vDto.getSpackno()); // ������ˮ��
			BillSend1000.put("VouNo", vDto.getSvoucherno()); // ƾ֤���
			BillSend1000.put("VouDate", DateUtil.date2String2(tvPayreckBankDto
					.getDvoudate())); // ƾ֤����
			BillSend1000.put("PayerAcct", tvPayreckBankDto.getSpayeracct()); // �������˺�
			BillSend1000.put("PayerName", tvPayreckBankDto.getSpayername()); // ����������
			BillSend1000.put("PayerAddr", ""); // �����˵�ַ
			BillSend1000.put("Amt", MtoCodeTrans
					.transformString(tvPayreckBankDto.getFamt())); // ���
			BillSend1000
					.put("PayeeBankNo", tvPayreckBankDto.getSagentbnkcode()); // �տ����к�
			BillSend1000.put("PayeeOpBkNo", tvPayreckBankDto
					.getSagentbnkcode()); // �տ��˿������к�
			BillSend1000.put("PayeeAcct", tvPayreckBankDto.getSpayeeacct()); // �տ����˺�
			BillSend1000.put("PayeeName", tvPayreckBankDto.getSpayeename()); // �տ�������
			BillSend1000.put("PayReason", ""); // ������˿�ԭ��
			BillSend1000.put("BudgetSubjectCode", ""); // Ԥ���Ŀ����
			BillSend1000.put("AddWord", ""); // ����
			BillSend1000.put("OfYear", vDto.getSstyear()); // �������
			BillSend1000.put("Flag", getBankTypeFlag(vDto.getSpaybankcode(), ITFECommonConstant.TBS_TREANDBANK.get(vDto.getStrecode()))); // �տ��ʶ
			list.add(BillSend1000);
			msgMap.put("BillSend1000", list);
			String sql = "UPDATE TV_PAYRECK_BANK SET S_PACKNO = ?,S_TRANO = ?  WHERE D_ENTRUSTDATE = ? AND S_BOOKORGCODE = ?  AND S_AGENTBNKCODE = ? AND S_VOUNO = ? AND F_AMT = ?  ";
			sqlExecutor = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExecutor.addParam(vDto.getSpackno());
			sqlExecutor.addParam(vDto.getSpackno());
			sqlExecutor.addParam(tvPayreckBankDto.getDentrustdate());
			sqlExecutor.addParam(tvPayreckBankDto.getSbookorgcode());
			sqlExecutor.addParam(tvPayreckBankDto.getSagentbnkcode());
			sqlExecutor.addParam(tvPayreckBankDto.getSvouno());
			sqlExecutor.addParam(tvPayreckBankDto.getFamt());
			sqlExecutor.runQuery(sql);
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			logger.equals(e);
			throw new ITFEBizException("��ѯҵ������ʧ�ܣ�", e);
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.equals(e);
			throw new ITFEBizException("��ѯҵ������ʧ�ܣ�", e);
		}finally{
			if(null != sqlExecutor){
				sqlExecutor.closeConnection();
			}
		}
	}

	private static void tranfor2302(Map map, TvVoucherinfoDto vDto)
			throws ITFEBizException {
		TvPayreckBankBackDto tvPayreckBankBackDto = new TvPayreckBankBackDto();
		tvPayreckBankBackDto.setIvousrlno(Long.valueOf(vDto.getSdealno()));
		try {
			tvPayreckBankBackDto = (TvPayreckBankBackDto) CommonFacade.getODB()
					.findRsByDto(tvPayreckBankBackDto).get(0);
			Map<String, String> BatchHead1000 = new HashMap<String, String>();
			BatchHead1000.put("BillOrg", tvPayreckBankBackDto
					.getSagentbnkcode());
			BatchHead1000.put("EntrustDate", TimeFacade.getCurrentStringTime());
			BatchHead1000.put("PackNo", vDto.getSpackno());
			BatchHead1000.put("TreCode", vDto.getStrecode());
			BatchHead1000.put("AllNum", "1");
			BatchHead1000.put("AllAmt", MtoCodeTrans
					.transformString(tvPayreckBankBackDto.getFamt()));
			BatchHead1000.put("PayoutVouType", "3");

			Map<String, Object> msgMap = (Map<String, Object>) ((Map) map
					.get("cfx")).get("MSG");
			msgMap.put("BatchHead1000", BatchHead1000);
			List list = new ArrayList();
			Map<String, String> BillSend1000 = new HashMap<String, String>();
			BillSend1000.put("TraNo", vDto.getSpackno()); // ������ˮ��
			BillSend1000.put("VouNo", vDto.getSvoucherno()); // ƾ֤���
			BillSend1000.put("VouDate", DateUtil
					.date2String2(tvPayreckBankBackDto.getDvoudate())); // ƾ֤����
			BillSend1000.put("PayerAcct", tvPayreckBankBackDto.getSpayeracct()); // �������˺�
			BillSend1000.put("PayerName", tvPayreckBankBackDto.getSpayername()); // ����������
			BillSend1000.put("PayerAddr", ""); // �����˵�ַ
			BillSend1000.put("Amt", MtoCodeTrans
					.transformString(tvPayreckBankBackDto.getFamt())); // ���
			BillSend1000.put("PayeeBankNo", tvPayreckBankBackDto.getSagentbnkcode()); // �տ����к�
			BillSend1000.put("PayeeOpBkNo", tvPayreckBankBackDto
					.getSagentacctbankname()); // �տ��˿������к�
			BillSend1000.put("PayeeAcct", tvPayreckBankBackDto.getSpayeeacct()); // �տ����˺�
			BillSend1000.put("PayeeName", tvPayreckBankBackDto.getSpayeename()); // �տ�������
			BillSend1000.put("PayReason", ""); // ������˿�ԭ��
			BillSend1000.put("BudgetSubjectCode", ""); // Ԥ���Ŀ����
			BillSend1000.put("AddWord", ""); // ����
			BillSend1000.put("OfYear", vDto.getSstyear()); // �������
			BillSend1000.put("Flag", getBankTypeFlag(vDto.getSpaybankcode(), ITFECommonConstant.TBS_TREANDBANK.get(vDto.getStrecode()))); // �տ��ʶ
			list.add(BillSend1000);
			msgMap.put("BillSend1000", list);
			tvPayreckBankBackDto.setSpackno(vDto.getSpackno());
			DatabaseFacade.getODB().update(tvPayreckBankBackDto);
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			logger.equals(e);
			throw new ITFEBizException("��ѯҵ������ʧ�ܣ�", e);
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.equals(e);
			throw new ITFEBizException("��ѯҵ������ʧ�ܣ�", e);
		}
	}

	private static void tranfor5207(Map map, TvVoucherinfoDto vDto)
			throws ITFEBizException {
		try {
			TvPayoutmsgmainDto tvPayoutmsgmainDto = new TvPayoutmsgmainDto();
			tvPayoutmsgmainDto.setSbizno(vDto.getSdealno());
			tvPayoutmsgmainDto = (TvPayoutmsgmainDto) CommonFacade.getODB().findRsByDto(tvPayoutmsgmainDto).get(0);
			TvPayoutmsgsubDto payoutsubDto = new TvPayoutmsgsubDto();
			payoutsubDto.setSbizno(vDto.getSdealno());
			payoutsubDto = (TvPayoutmsgsubDto) CommonFacade.getODB().findRsByDto(payoutsubDto).get(0);
			Map<String, String> BatchHead1000 = new HashMap<String, String>();
			BatchHead1000.put("BillOrg", vDto.getSpaybankcode());
			BatchHead1000.put("EntrustDate", TimeFacade.getCurrentStringTime());
			BatchHead1000.put("PackNo", vDto.getSpackno());
			BatchHead1000.put("TreCode", vDto.getStrecode());
			BatchHead1000.put("ChangeNo", "");
			BatchHead1000.put("AllNum", "1");
			BatchHead1000.put("AllAmt", MtoCodeTrans
					.transformString(tvPayoutmsgmainDto.getNmoney()));
			BatchHead1000.put("PayoutVouType", "1");

			Map<String, Object> msgMap = (Map<String, Object>) ((Map) map
					.get("cfx")).get("MSG");
			msgMap.put("BatchHead1000", BatchHead1000);
			List list = new ArrayList();
			Map<String, String> BillSend1000 = new HashMap<String, String>();
			BillSend1000.put("TraNo", vDto.getSpackno()); // ������ˮ��
			BillSend1000.put("VouNo", vDto.getSvoucherno()); // ƾ֤���
			BillSend1000.put("VouDate", tvPayoutmsgmainDto.getSgenticketdate()); // ƾ֤����
			BillSend1000.put("PayerAcct", tvPayoutmsgmainDto.getSpayeracct()); // �������˺�
			BillSend1000.put("PayerName", tvPayoutmsgmainDto.getSpayername()); // ����������
			BillSend1000.put("PayerAddr", ""); // �����˵�ַ
			BillSend1000.put("Amt", MtoCodeTrans
					.transformString(tvPayoutmsgmainDto.getNmoney())); // ���
			if(tvPayoutmsgmainDto.getSpayeebankno()==null || tvPayoutmsgmainDto.getSpayeebankno().trim().equals("")){
				BillSend1000.put("PayeeBankNo", tvPayoutmsgmainDto.getSrecbankno()); // �տ����к�
			}else{
				BillSend1000.put("PayeeBankNo", tvPayoutmsgmainDto.getSpayeebankno()); // �տ����к�
			}
			BillSend1000.put("PayeeOpBkNo", tvPayoutmsgmainDto.getSrecbankno()); // �տ��˿������к�
			BillSend1000.put("PayeeAcct", tvPayoutmsgmainDto.getSrecacct()); // �տ����˺�
			BillSend1000.put("PayeeName", tvPayoutmsgmainDto.getSrecname()); // �տ�������
			BillSend1000.put("PayReason", tvPayoutmsgmainDto.getSpaysummaryname()==null?"":tvPayoutmsgmainDto.getSpaysummaryname()); // ������˿�ԭ��
			BillSend1000.put("BudgetSubjectCode", payoutsubDto.getSfunsubjectcode()); // Ԥ���Ŀ����
			BillSend1000.put("AddWord", tvPayoutmsgmainDto.getSpaysummaryname()==null?"":tvPayoutmsgmainDto.getSpaysummaryname()); // ����
			BillSend1000.put("OfYear", vDto.getSstyear()); // �������
			BillSend1000.put("Flag", getRecvbankFlag(vDto)); // �տ��ʶ
			list.add(BillSend1000);
			msgMap.put("BillSend1000", list);
			tvPayoutmsgmainDto.setSpackageno(vDto.getSpackno());
			DatabaseFacade.getODB().update(tvPayoutmsgmainDto);
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			logger.equals(e);
			throw new ITFEBizException("��ѯҵ������ʧ�ܣ�", e);
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.equals(e);
			throw new ITFEBizException("��ѯҵ������ʧ�ܣ�", e);
		}
	}
	
	/**
	 * ʵ���ж��տ����Ǳ��л��ǻ���������
	 * @param vDto
	 * @return
	 * @throws ITFEBizException 
	 */
	private static String getRecvbankFlag(TvVoucherinfoDto vDto) throws ITFEBizException{
		TvPayoutmsgmainDto payDto = new TvPayoutmsgmainDto();
		payDto.setSbizno(vDto.getSdealno());
		try {
			List list = CommonFacade.getODB().findRsByDtoWithUR(payDto);
			payDto = (TvPayoutmsgmainDto)list.get(0);
			return getBankTypeFlag(payDto.getSrecbankno(),ITFECommonConstant.TBS_TREANDBANK.get(vDto.getStrecode()));
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯҵ������ʧ�ܣ�", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯҵ������ʧ�ܣ�", e);
		}
	}
	
	private static String getBankTypeFlag(String desOrg,String payBankCode){
		if(desOrg.substring(0, 3).equals(payBankCode.substring(0, 3))){
			return "1";	//1-����ҵ��
		}else{
			return "2";	//2-ͬ�ǿ���
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

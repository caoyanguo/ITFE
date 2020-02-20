package com.cfcc.itfe.facade;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsOperationmodelDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvFreeDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPbcpayMainDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * �յ���ִ���Ĵ���
 * 
 * @author zhouchuan
 * 
 */
public class MsgRecvFacade {

	private static Log logger = LogFactory.getLog(MsgRecvFacade.class);

	/**
	 * ���ݱ��ı����Ϊ�����������±���ͷ��Ϣ
	 * 
	 * @param String
	 *            smsgno ���ı��
	 * @param String
	 *            staxorgcode ���ش���(���ջ��ػ��Ʊ��λ)
	 * @param String
	 *            spackno ����ˮ��
	 * @param String
	 *            scommitdate ί������
	 * @param String
	 *            state ����״̬
	 * @throws ITFEBizException
	 */
	public static void updateMsgHeadByMsgNo(String smsgno, String staxorgcode,
			String spackno, String scommitdate, String state)
			throws ITFEBizException {
		/**
		 * ��һ�� ���ݱ��ı���ҵ���Ӧ��ҵ������
		 */
		String biztype = PublicSearchFacade.getBizTypeByMsgNo(smsgno);

		/**
		 * �ڶ��� ���±���ͷ��Ϣ
		 */
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();

			String updateSql = " update "
					+ TvFilepackagerefDto.tableName()
					+ " set S_RETCODE = ? "
					+ " where S_TAXORGCODE = ? and S_COMMITDATE =? and S_PACKAGENO =? and S_OPERATIONTYPECODE = ? ";
			updateExec.addParam(state);
			updateExec.addParam(staxorgcode);
			updateExec.addParam(scommitdate);
			updateExec.addParam(spackno);
			updateExec.addParam(biztype);
			updateExec.runQueryCloseCon(updateSql);
		} catch (JAFDatabaseException e) {
			logger.error("���±���ͷ��Ϣʱ�������ݿ��쳣!", e);
			throw new ITFEBizException("���±���ͷ��Ϣʱ�������ݿ��쳣!", e);
		}
	}

	/**
	 * ���ݱ��ı����Ϊ�����������±���ͷ��Ϣ
	 * 
	 * @param String
	 *            smsgno ���ı��
	 * @param String
	 *            staxorgcode ���ش���(���ջ��ػ��Ʊ��λ)
	 * @param String
	 *            spackno ����ˮ��
	 * @param String
	 *            scommitdate ί������
	 * @param String
	 *            state ����״̬
	 * @throws ITFEBizException
	 */
	public static void updateMsgHeadByMsgNofor3201(String staxorgcode,
			String spackno, String scommitdate, String state)
			throws ITFEBizException {

		/**
		 * �ڶ��� ���±���ͷ��Ϣ
		 */
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();

			String updateSql = " update "
					+ TvFilepackagerefDto.tableName()
					+ " set S_RETCODE = ? "
					+ " where S_TAXORGCODE = ? and S_COMMITDATE =? and S_PACKAGENO =?  ";
			updateExec.addParam(state);
			updateExec.addParam(staxorgcode);
			updateExec.addParam(scommitdate);
			updateExec.addParam(spackno);
			updateExec.runQueryCloseCon(updateSql);
		} catch (JAFDatabaseException e) {
			logger.error("���±���ͷ��Ϣʱ�������ݿ��쳣!", e);
			throw new ITFEBizException("���±���ͷ��Ϣʱ�������ݿ��쳣!", e);
		}
	}

	/*
	 * ����ԭ���ı�Ÿ���ҵ��״̬
	 */
	public static void updateMsgBizDetailByOriMsgNo(String msgref,
			String orimsgno, String state, String spackno,
			HashMap<String, String> map) throws ITFEBizException {

		/**
		 * ��һ�� ���ݱ��ı���ҵ���Ӧ��ҵ������
		 */
		String ls_UpdateSql = "";
		String tablename = map.get(orimsgno);
		// ��Ҫ���9121,9120���Ĵ���
		if(orimsgno.equals(MsgConstant.MSG_NO_5104) ||orimsgno.equals(MsgConstant.MSG_NO_2202)|| orimsgno.equals(MsgConstant.MSG_NO_1106)) {
			ls_UpdateSql = "update " + tablename
				+ " set S_STATUS = ? where S_PACKNO = ? AND S_STATUS = ? ";
		} else if(orimsgno.equals(MsgConstant.MSG_NO_2201)){
			ls_UpdateSql = "update " + tablename
			+ " set S_Result = ? where S_PACKNO = ? AND S_Result = ? ";
		}else{
			ls_UpdateSql = "update " + tablename
				+ " set S_STATUS = ? where S_PACKAGENO = ? AND S_STATUS = ? ";
		}

		/**
		 * �ڶ��� ����ҵ�����ݱ�
		 */
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();

			updateExec.addParam(state);
			updateExec.addParam(spackno);
			updateExec.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);

			updateExec.runQueryCloseCon(ls_UpdateSql);
			
		} catch (JAFDatabaseException e) {
			logger.error("����ҵ����ϸ������쳣!" + tablename, e);
			throw new ITFEBizException("����ҵ����ϸ������쳣!" + tablename, e);
		}
		//�Ϻ���ֽ��5201��5106��5108����ԭ���ı�Ÿ���ҵ���������ƾ֤������״̬
		updateMsgBizDetailByOriMsgNo(orimsgno, state, spackno);
	}

	
	/**
	 * �Ϻ���ֽ��5201��5106��5108����ԭ���ı�Ÿ���ҵ���������ƾ֤������״̬
	 * @param orimsgno
	 * @param state
	 * @param spackno
	 * @throws ITFEBizException
	 */
	public static void updateMsgBizDetailByOriMsgNo(String orimsgno, String state, String spackno) 
		throws ITFEBizException {		
		//���Ϻ���ֽ������Ҫ������
		if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")<0)
			return;
		//ֱ��֧������Ȩ֧����Ƚ���TIPS��ִ���²��ƾ֤������
		if(orimsgno.equals(MsgConstant.MSG_NO_5103)||orimsgno.equals(MsgConstant.MSG_NO_5102)){
			VoucherUtil.updateVoucherSplitReceiveTIPS(orimsgno, state, spackno);
			return;
		}		
		if(!orimsgno.equals(MsgConstant.MSG_NO_2201)&&!orimsgno.equals(MsgConstant.MSG_NO_5104))
			return;
		String tablename="TF_DIRECTPAYMSGMAIN";	
		String ls_UpdateSql =  "update " + tablename+ " set S_STATUS = ? where S_PACKAGENO = ? AND S_STATUS = ? ";
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updateExec.addParam(state);
			updateExec.addParam(spackno);
			updateExec.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);
			updateExec.runQueryCloseCon(ls_UpdateSql);			
		} catch (JAFDatabaseException e) {
			logger.error("����ҵ����ϸ������쳣!" + tablename, e);
			throw new ITFEBizException("����ҵ����ϸ������쳣!" + tablename, e);
		}
	}

	
	/*
	 * ���ݱ��ĵ�MSG_ID���¹�ϵ�����ˮ��״̬
	 */
	public static void updateMsgHeadByMsgId(String smsgid, String state,
			String sdemo) throws ITFEBizException {
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			String updateSql = " update " + TvFilepackagerefDto.tableName()
					+ " set S_RETCODE = ? ,s_demo = ?" + " where S_MSGID = ? ";
			updateExec.addParam(state);
			updateExec.addParam(sdemo);
			updateExec.addParam(smsgid);
			updateExec.runQueryCloseCon(updateSql);
		} catch (JAFDatabaseException e) {
			logger.error("���±���ͷ��Ϣʱ�������ݿ��쳣!", e);
			throw new ITFEBizException("���±���ͷ��Ϣʱ�������ݿ��쳣!", e);
		}
	}

	/**
	 * ���·�����־��״̬���������־��ˮ��
	 * 
	 * @param _dto
	 * @param state
	 * @param recvno
	 * @param sdemo
	 * @throws ITFEBizException
	 */
	public static void updateMsgSendLogByMsgId(TvSendlogDto _dto, String state,
			String recvno, String sdemo) throws ITFEBizException {
		try {
			_dto.setSretcode(state);
			if (null != recvno) {
				_dto.setSrecvno(recvno);
			}
			_dto.setSdemo(sdemo);
			DatabaseFacade.getODB().update(_dto);
		} catch (JAFDatabaseException e) {
			logger.error("���±���ͷ��Ϣʱ�������ݿ��쳣!", e);
			throw new ITFEBizException("���±���ͷ��Ϣʱ�������ݿ��쳣!", e);
		}
	}

	/*
	 * ����TC��ִ���ĸ���ҵ���״̬
	 */
	public static void updateMsgByMsgPackageNo(String orimsgno,
			String oripackno, String orientrustDate, String sstate)
			throws ITFEBizException {
		String ls_UpdateSql = "";
		// ��Ҫ���9121���Ĵ���
		if (orimsgno.equals("1104")) {
			// �˿�ҵ��
			ls_UpdateSql = "update "
					+ TvDwbkDto.tableName()
					+ " set S_STATUS = ? where S_PACKAGENO = ? and S_STATUS != ? and D_ACCEPT = ? ";
			orientrustDate = CommonUtil.strToDate(orientrustDate).toString();
		} else if (orimsgno.equals("1105")) {
			// ����
			ls_UpdateSql = "update "
					+ TvInCorrhandbookDto.tableName()
					+ " set S_STATUS = ? where S_PACKAGENO = ?  and S_STATUS != ? and D_ACCEPT = ? ";
			orientrustDate = CommonUtil.strToDate(orientrustDate).toString();
		} else if (orimsgno.equals("1106")) {
			// ��ֵ�
			ls_UpdateSql = "update "
					+ TvFreeDto.tableName()
					+ " set S_STATUS = ? where S_PACKNO = ?  and S_STATUS != ? and D_ACCEPTDATE = ? ";
			orientrustDate = CommonUtil.strToDate(orientrustDate).toString();
		} else if (orimsgno.equals("5101")) {
			// ʵ���ʽ�
			ls_UpdateSql = "update "
					+ TvPayoutmsgmainDto.tableName()
					+ " set S_STATUS = ? where S_PACKAGENO = ?  and S_STATUS != ? and S_COMMITDATE = ? ";
		} else if (orimsgno.equals("5102")) {
			// ֱ��֧�����
			ls_UpdateSql = "update "
					+ TvDirectpaymsgmainDto.tableName()
					+ " set S_STATUS = ? where S_PACKAGENO = ?  and S_STATUS != ? and S_COMMITDATE = ? ";
		} else if (orimsgno.equals("5103")) {
			// ��Ȩ֧�����
			ls_UpdateSql = "update "
					+ TvGrantpaymsgmainDto.tableName()
					+ " set S_STATUS = ? where S_PACKAGENO = ?  and S_STATUS != ? and S_COMMITDATE = ? ";
		} else if (orimsgno.equals("5112")) {
			// ���������Կ�������
			ls_UpdateSql = "update "
					+ TvPayoutfinanceMainDto.tableName()
					+ " set S_STATUS = ? where S_PACKAGENO = ?  and S_STATUS != ? and S_ENTRUSTDATE = ? ";
		} else if (orimsgno.equals("7211")) {
			// ����˰Ʊ
			ls_UpdateSql = "update "
					+ TvInfileDto.tableName()
					+ " set S_STATUS = ? where S_PACKAGENO = ?  and S_STATUS != ? and S_COMMITDATE = ? ";
		} else if (orimsgno.equals("5104")) {
			//���а���ֱ��֧��
			ls_UpdateSql = "update "
					+ TvPbcpayMainDto.tableName()
					+ " set S_STATUS = ? where S_PACKNO = ?  and S_STATUS != ? and S_ENTRUSTDATE = ? ";
		}else if (orimsgno.equals("2201")) {
			//2201��������
			ls_UpdateSql = "update "
					+ TvPayreckBankDto.tableName()
					+ " set S_Result = ? where s_PackNo = ?  and S_Result != ? and d_EntrustDate = ? "; 
			orientrustDate = CommonUtil.strToDate(orientrustDate).toString();
		}else if (orimsgno.equals("2202")) {
			//2202���������˻�
				ls_UpdateSql = "update "
						+ TvPayreckBankBackDto.tableName()
						+ " set s_STATUS = ? where s_PackNo = ?  and s_STATUS != ? and d_EntrustDate = ? "; 
			orientrustDate = CommonUtil.strToDate(orientrustDate).toString();
		}else {
			logger.info("�˱��ı��" + orimsgno + "û�ж�Ӧ��ҵ���!");
		}
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			updateExec.clearParams();
			updateExec.addParam(sstate);
			updateExec.addParam(oripackno);
			updateExec.addParam(sstate);
			updateExec.addParam(orientrustDate);
			updateExec.runQueryCloseCon(ls_UpdateSql);
		} catch (JAFDatabaseException e) {
			logger.error("���±���ͷ��Ϣʱ�������ݿ��쳣!", e);
			throw new ITFEBizException("���±���ͷ��Ϣʱ�������ݿ��쳣!", e);
		}
	}

	/*
	 * ���ݻ�ִ���ĸ�����־��״̬
	 */
	public static void updateSendLogByPackageNo(String orimsgno,
			String orisendorgcode, String oripackno, String orientrustDate,
			String sstate) throws ITFEBizException {
		String ls_UpdateSql = " update "
				+ TvSendlogDto.tableName()
				+ " set S_RETCODE = ? "
				+ " where S_PACKNO = ? and S_DATE = ? and S_SENDORGCODE = ? and S_OPERATIONTYPECODE = ? ";
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			updateExec.clearParams();
			updateExec.addParam(sstate);
			updateExec.addParam(oripackno);
			updateExec.addParam(orientrustDate);
			updateExec.addParam(orisendorgcode);
			updateExec.addParam(orimsgno);
			updateExec.runQueryCloseCon(ls_UpdateSql);
		} catch (JAFDatabaseException e) {
			logger.error("���±���ͷ��Ϣʱ�������ݿ��쳣!", e);
			throw new ITFEBizException("���±���ͷ��Ϣʱ�������ݿ��쳣!", e);
		}
	}

	/*
	 * ���ݻ�ִ���ĸ�����־��״̬
	 */
	public static void updateRecvLogByPackageNo(String orimsgno,
			String orisendorgcode, String oripackno, String orientrustDate,
			String sstate) throws ITFEBizException {
		String ls_UpdateSql = " update "
				+ TvRecvlogDto.tableName()
				+ " set S_RETCODE = ? "
				+ " where S_PACKNO = ? and S_DATE = ? and S_SENDORGCODE = ?  and S_OPERATIONTYPECODE = ? ";
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			updateExec.clearParams();
			updateExec.addParam(sstate);
			updateExec.addParam(oripackno);
			updateExec.addParam(orientrustDate);
			updateExec.addParam(orisendorgcode);
			updateExec.addParam(orimsgno);
			updateExec.runQueryCloseCon(ls_UpdateSql);
		} catch (JAFDatabaseException e) {
			logger.error("���±���ͷ��Ϣʱ�������ݿ��쳣!", e);
			throw new ITFEBizException("���±���ͷ��Ϣʱ�������ݿ��쳣!", e);
		}
	}

	/**
	 * 
	 * ����MSGID���ҷ�����־��
	 * 
	 * @param smsgid
	 * @return
	 * @throws ITFEBizException
	 */
	public static TvSendlogDto findSendLogByMsgId(String smsgid, String msgno)
			throws ITFEBizException {
		try {
			TvSendlogDto _dto = new TvSendlogDto();
			_dto.setSseq(smsgid);
			_dto.setSoperationtypecode(msgno);
			List<TvSendlogDto> list = CommonFacade.getODB()
					.findRsByDtoForWhere(_dto, "And 1=1");
			if (list.size() > 0) {
				TvSendlogDto orisenddto = list.get(0);
				return orisenddto;
			}
		} catch (JAFDatabaseException e) {
			logger.error("����MSGID���ҷ�����־������쳣!", e);
			throw new ITFEBizException("����MSGID���ҷ�����־������쳣!", e);
		} catch (ValidateException e) {
			logger.error("����MSGID���ҷ�����־������쳣!", e);
			throw new ITFEBizException("����MSGID���ҷ�����־������쳣!", e);
		}
		return null;
	}

	/**
	 * 
	 * ����MSGID���ҷ�����־��
	 * 
	 * @param smsgid
	 * @return
	 * @throws ITFEBizException
	 */
	public static TvSendlogDto findSendLogByMsgId(String msgno, String billorg,
			String sdate, String spackno, String result)
			throws ITFEBizException {
		try {
			TvSendlogDto _dto = new TvSendlogDto();
			_dto.setSbillorg(billorg);
			_dto.setSoperationtypecode(msgno);
			_dto.setSdate(sdate);
			_dto.setSpackno(spackno);
			List<TvSendlogDto> list = CommonFacade.getODB()
					.findRsByDtoForWhere(_dto, "And 1=1");
			if (list.size() > 0) {
				TvSendlogDto orisenddto = list.get(0);
				return orisenddto;
			}
		} catch (JAFDatabaseException e) {
			logger.error("����MSGID���ҷ�����־������쳣!", e);
			throw new ITFEBizException("����MSGID���ҷ�����־������쳣!", e);
		} catch (ValidateException e) {
			logger.error("����MSGID���ҷ�����־������쳣!", e);
			throw new ITFEBizException("����MSGID���ҷ�����־������쳣!", e);
		}
		return null;
	}

	/**
	 * ������Ҫ��������Ϊ�����������±���ͷ��Ϣ
	 * 
	 * @param String
	 *            smsgno ���ı��
	 * @param String
	 *            staxorgcode ���ش���(���ջ��ػ��Ʊ��λ)
	 * @param String
	 *            spackno ����ˮ��
	 * @param String
	 *            scommitdate ί������
	 * @param String
	 *            state ����״̬
	 * @throws ITFEBizException
	 */
	public static void updateMsgHeadByCon(String staxorgcode, String spackno,
			String scommitdate, String state) throws ITFEBizException {
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();

			String updateSql = " update "
					+ TvFilepackagerefDto.tableName()
					+ " set S_RETCODE = ? "
					+ " where S_TAXORGCODE = ? and S_COMMITDATE =? and S_PACKAGENO =? ";
			updateExec.addParam(state);
			updateExec.addParam(staxorgcode);
			updateExec.addParam(scommitdate);
			updateExec.addParam(spackno);
			updateExec.runQueryCloseCon(updateSql);
		} catch (JAFDatabaseException e) {
			logger.error("�����ļ������Ӧ��ϵ�����쳣!", e);
			throw new ITFEBizException("�����ļ������Ӧ��ϵ�����쳣!", e);
		}
	}

	/**
	 * �������ȡ�ú����������
	 * 
	 * @param strecode
	 * @return
	 * @throws ITFEBizException
	 */
	public static TsTreasuryDto getBookOrgByTre(String strecode)
			throws ITFEBizException {
		try {
			TsTreasuryDto _dto = new TsTreasuryDto();
			_dto.setStrecode(strecode);
			List<TsTreasuryDto> list = CommonFacade.getODB().findRsByDtoWithUR(
					_dto);
			if (null != list && list.size() == 1) {
				return list.get(0);
			}
		} catch (JAFDatabaseException e) {
			logger.error("���ݹ�������ѯ��Ӧ�ĺ����������!", e);
			throw new ITFEBizException("���ݹ�������ѯ��Ӧ�ĺ����������!", e);
		} catch (ValidateException e) {
			logger.error("���ݹ�������ѯ��Ӧ�ĺ����������!", e);
			throw new ITFEBizException("���ݹ�������ѯ��Ӧ�ĺ����������!", e);
		}
		return null;

	}

	/**
	 * ���ݲ�������õ������������
	 * 
	 * @param strecode
	 * @return
	 * @throws ITFEBizException
	 */
	public static TsConvertfinorgDto getBookOrgByFin(String sFinCode)
			throws ITFEBizException {
		try {
			TsConvertfinorgDto _dto = new TsConvertfinorgDto();
			_dto.setSfinorgcode(sFinCode);
			List<TsConvertfinorgDto> list = CommonFacade.getODB()
					.findRsByDtoWithUR(_dto);
			if (null != list && list.size() == 1) {
				return list.get(0);
			}
		} catch (JAFDatabaseException e) {
			logger.error("���ݲ��������ѯ��Ӧ�ĺ����������!", e);
			throw new ITFEBizException("���ݲ��������ѯ��Ӧ�ĺ����������!", e);
		} catch (ValidateException e) {
			logger.error("���ݲ��������ѯ��Ӧ�ĺ����������!", e);
			throw new ITFEBizException("���ݲ��������ѯ��Ӧ�ĺ����������!", e);
		}
		return null;

	}

	/**
	 * ����ԭ���Ÿ��³��°���
	 * 
	 * @param msgno
	 * @param oriPackNo
	 * @param newPackNo
	 * @param map
	 * @throws ITFEBizException
	 */
	public static void updateNewPackNoByOldPackNo(String msgno,
			String oriPackNo, String newPackNo, HashMap<String, String> map)
			throws ITFEBizException {
		String tablename = map.get(msgno);
		String sql = "";
		if(msgno.equals(MsgConstant.PBC_DIRECT_IMPORT) ||msgno.equals(MsgConstant.MSG_NO_2201)||msgno.equals(MsgConstant.MSG_NO_2202)||msgno.equals(MsgConstant.MSG_NO_1106)) {
			sql ="update " + tablename
				+ " set S_PACKNO = ? where S_PACKNO = ? ";
		}else {
			sql = "update " + tablename
			+ " set S_PACKAGENO = ? where S_PACKAGENO = ? ";
		}		
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			updateExec.addParam(newPackNo);
			updateExec.addParam(oriPackNo);
			updateExec.runQueryCloseCon(sql);
		} catch (JAFDatabaseException e) {
			logger.error("����ҵ����ϸ������쳣!" + tablename, e);
			throw new ITFEBizException("����ҵ����ϸ������쳣!" + tablename, e);
		}
	}

	/**
	 * ���ļ������Ӧ��ϵ�оɰ��Ÿ�Ϊ�°���
	 * 
	 * @param msgno
	 * @param oriPackNo
	 * @param newPackNo
	 * @param map
	 * @throws ITFEBizException
	 */
	public static void updateFileRefPackNo(String oriPackNo, String newPackNo)
			throws ITFEBizException {
		String sql = "update " + TvFilepackagerefDto.tableName()
				+ " set S_PACKAGENO = ? where S_PACKAGENO = ? ";
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			updateExec.addParam(newPackNo);
			updateExec.addParam(oriPackNo);
			updateExec.runQueryCloseCon(sql);
		} catch (JAFDatabaseException e) {
			logger
					.error("�����ļ������Ӧ��ϵ�����쳣!" + TvFilepackagerefDto.tableName(),
							e);
			throw new ITFEBizException("�����ļ������Ӧ��ϵ�����쳣!"
					+ TvFilepackagerefDto.tableName(), e);
		}
	}
	
	/**
	 * ��ƾ֤�������еľɰ��Ÿ�Ϊ�°���
	 * 
	 * @param msgno
	 * @param oriPackNo
	 * @param newPackNo
	 * @param map
	 * @throws ITFEBizException
	 */
	public static void updateVoucherInfoByOldPackNo(String oriPackno, 
			String tmpPackNo)
			throws ITFEBizException {
		String sql = "update " + TvVoucherinfoDto.tableName()
				+ " set S_PACKNO = ? where S_PACKNO = ? ";
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			updateExec.addParam(tmpPackNo);
			updateExec.addParam(oriPackno);
			updateExec.runQueryCloseCon(sql);
		} catch (JAFDatabaseException e) {
			logger.error("����ƾ֤����������쳣!" + TvVoucherinfoDto.tableName(),e);
			throw new ITFEBizException("����ƾ֤����������쳣!" + TvVoucherinfoDto.tableName(), e);
		}
	}

	/**
	 * ����TIPS�·��������ˮ���·�����
	 * @param eventContext
	 * @param msgno
	 * @throws ITFEBizException
	 */
	public static void recvMsglog(MuleEventContext eventContext, String msgno) throws ITFEBizException {
		try {
			String recvseqno = StampFacade.getStampSendSeq("JS"); // ȡ������־��ˮ
			TsOperationmodelDto _dto = new TsOperationmodelDto();
			String _smodelid = recvseqno.substring(0, 10);
			int _ino = Integer.valueOf(recvseqno.substring(10, 20));
			String path = (String) eventContext.getMessage().getProperty(
					"XML_MSG_FILE_PATH");
			_dto.setSmodelid(_smodelid);
			_dto.setIno(_ino);
			_dto.setSoperationtypecode(msgno);
			_dto.setSmodelsavepath(path);
			DatabaseFacade.getDb().create(_dto);
			if (StateConstant.SendFinYes
					.equals(ITFECommonConstant.IFSENDMSGTOFIN)) {
				//����TIPS�����·���MQCORRELID
				eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");
				Object msg = eventContext.getMessage().getProperty("MSG_INFO");
				eventContext.getMessage().setPayload(msg);
			} else {
				eventContext.setStopFurtherProcessing(true);
				return;
			}
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʧ��!", e);
			throw new ITFEBizException("ȡ������ˮ��ʧ��!", e);
		} catch (JAFDatabaseException e) {
			logger.error("�ǽ��ձ�����־��ʧ��!", e);
			throw new ITFEBizException("��¼���ձ�����־��ʧ��!", e);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

package com.cfcc.itfe.service.recbiz.reportdownload;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HeadDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.ServiceUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLBatchRetriever;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author zhouchuan ˵��������ı���������Ϊҵ��������ʱ���ṩ�����ڱ�������
 * @time 09-11-09 10:51:29 codecomment:
 */

public class RptDownloadService extends AbstractRptDownloadService {

	public static final int MAX_NUM = 5000; // ÿ��ȡ������¼��

	private static Log logger = LogFactory.getLog(RptDownloadService.class);

	/**
	 * ������������
	 * 
	 * @generated
	 * @param String
	 *            strecode �������
	 * @param String
	 *            rptDate �������
	 * @param String
	 *            taxprop ���ջ�������
	 * @param String
	 *            rpttype ����Χ
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List downloadRpt(String strecode, String rptDate, String taxprop,
			String rpttype) throws ITFEBizException {
		String sorgcode = this.getLoginInfo().getSorgcode(); // ȡ�û�������
		String dirsep = File.separator; // ȡ��ϵͳ�ָ��
		String srcpath = rptDate + dirsep + sorgcode + dirsep + "report"
				+ dirsep; // ԭ·��
		List<String> filepathList = new ArrayList<String>();

		if (MsgConstant.RULE_SIGN_ALL.equals(rpttype)
				&& MsgConstant.MSG_TAXORG_FINANCE_PROP.equals(taxprop)) {
			// �����౨��(��ҪȫϽ���뱨��Ϳ�汨��)
			String stockfilepath = getStockRpt(strecode, rptDate, srcpath);
			if (null != stockfilepath) {
				filepathList.add(stockfilepath);
			}
		}

		String incomefilepath = getIncomeRpt(strecode, rptDate, srcpath,
				taxprop, rpttype);
		if (null != incomefilepath) {
			filepathList.add(incomefilepath);
		}

		return filepathList;
	}

	/**
	 * ��������
	 * 
	 * @generated
	 * @param String
	 *            strecode �������
	 * @param String
	 *            srptdate ��������
	 * @throws ITFEBizException
	 */
	public void requestRpt(String strecode, String srptdate)
			throws ITFEBizException {
		try {
			/**
			 * ��һ�� ���ݹ�������ҵ���Ӧ�Ĳ�������
			 */
			TsConvertfinorgDto finadto = new TsConvertfinorgDto();
			finadto.setStrecode(strecode);
//			if (!StateConstant.ORG_CENTER_CODE.equals(getLoginInfo().getSorgcode())) {
//				finadto.setSorgcode(this.getLoginInfo().getSorgcode());
//			}
			
			List<TsConvertfinorgDto> list = CommonFacade.getODB().findRsByDto(
					finadto);
			if (null == list || list.size() == 0) {
				logger.error("���ڲ���������Ϣ��ά������Ͳ�������Ķ�Ӧ��ϵ!�������:"+strecode);
				throw new ITFEBizException("���ڲ���������Ϣ��ά������Ͳ�������Ķ�Ӧ��ϵ!�������:"+strecode);
			}

			String finorgcode = list.get(0).getSfinorgcode();

			/**
			 * �ڶ��� ���ݲ�����������뱨�����ڵ��ú�̨����
			 */
			MuleClient client = new MuleClient();
			Map map = new HashMap();
			MuleMessage message = new DefaultMuleMessage(map);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY,
					MsgConstant.MSG_NO_5001 + "_OUT");
			message.setProperty(MessagePropertyKeys.MSG_BILL_CODE, finorgcode);
			message.setProperty(MessagePropertyKeys.MSG_DATE, srptdate);
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, getLoginInfo()
					.getSorgcode());
			message.setPayload(map);
			if(strecode!=null&&strecode.startsWith("1702"))
				message = client.send("vm://ManagerMsgToPbcCity", message);
			else
				message = client.send("vm://ManagerMsgToPbc", message);
			ServiceUtil.checkResult(message);
		} catch (MuleException e) {
			logger.error("����YAK��̨���Ĵ�������쳣!", e);
			throw new ITFEBizException("����YAK��̨���Ĵ�������쳣!", e);
		} catch (JAFDatabaseException e) {
			logger.error("���ú�̨����������ݿ��쳣!", e);
			throw new ITFEBizException("���ú�̨����������ݿ��쳣!", e);
		} catch (ValidateException e) {
			logger.error("���ú�̨����������ݿ��쳣!", e);
			throw new ITFEBizException("���ú�̨����������ݿ��쳣!", e);
		}
	}

	/**
	 * ���뱨��
	 * 
	 * @param String
	 *            strecode �������
	 * @param String
	 *            rptDate ��������
	 * @param String
	 *            srcpath ��·��
	 * @param String
	 *            taxprop ���ջ�������
	 * @param String
	 *            rpttype ��������
	 * @return
	 * @throws ITFEBizException
	 */
	private String getIncomeRpt(String strecode, String rptDate,
			String srcpath, String taxprop, String rpttype)
			throws ITFEBizException {
		SQLBatchRetriever batchRetriever = null;
		boolean isAllIncomeRecordflag = false;
		String selectSQL = null;

		try {
			batchRetriever = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLBatchRetriever();
			String taxsql = ""; // �ļ������е����ջ��ش���

			if (MsgConstant.MSG_TAXORG_FINANCE_PROP.equals(taxprop)) {
				taxsql = MsgConstant.MSG_TAXORG_SHARE_CLASS;

				if (MsgConstant.RULE_SIGN_ALL.equals(rpttype)) {
					// ����ǲ���ȫϽ-���ջ��ش���Ϊ����
					selectSQL = "select S_TRECODE,S_BUDGETLEVELCODE,S_TAXORGCODE,S_BUDGETSUBCODE,S_RPTDATE,S_BUDGETTYPE,N_MONEYDAY,N_MONEYMONTH,N_MONEYYEAR"
							+ " from "
							+ TrIncomedayrptDto.tableName()
							+ " where S_TRECODE = ? and S_RPTDATE = ? and S_BELONGFLAG = ? and S_TRIMFLAG = ? and S_TAXORGCODE = ? with ur";

					batchRetriever.clearParams();
					batchRetriever.addParam(strecode); // �������
					batchRetriever.addParam(rptDate); // ��������
					batchRetriever.addParam(rpttype); // Ͻ����־
					batchRetriever.addParam(MsgConstant.TIME_FLAG_NORMAL); // �����ڱ�־Ϊ������
					batchRetriever.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS); // �������ջ���
					batchRetriever.setMaxRows(MAX_NUM); // ����ÿ��ȡ��¼�������
				} else {
					// ����ǲ�������-����Ҫ���ջ�����Ϊ����
					selectSQL = "select S_TRECODE,S_BUDGETLEVELCODE,S_TAXORGCODE,S_BUDGETSUBCODE,S_RPTDATE,S_BUDGETTYPE,N_MONEYDAY,N_MONEYMONTH,N_MONEYYEAR"
							+ " from "
							+ TrIncomedayrptDto.tableName()
							+ " where S_TRECODE = ? and S_RPTDATE = ? and S_BELONGFLAG = ? and S_TRIMFLAG = ? with ur";

					batchRetriever.clearParams();
					batchRetriever.addParam(strecode); // �������
					batchRetriever.addParam(rptDate); // ��������
					batchRetriever.addParam(rpttype); // Ͻ����־
					batchRetriever.addParam(MsgConstant.TIME_FLAG_NORMAL); // �����ڱ�־Ϊ������
					batchRetriever.setMaxRows(MAX_NUM); // ����ÿ��ȡ��¼�������
				}
			} else {
				taxsql = getTaxBigClassByProp(taxprop);

				if (MsgConstant.RULE_SIGN_ALL.equals(rpttype)) {
					// ����ǹ���˰ȫϽ-���ջ��ش���Ϊ���ԵĲ��Ŵ������
					selectSQL = "select S_TRECODE,S_BUDGETLEVELCODE,S_TAXORGCODE,S_BUDGETSUBCODE,S_RPTDATE,S_BUDGETTYPE,N_MONEYDAY,N_MONEYMONTH,N_MONEYYEAR"
							+ " from "
							+ TrIncomedayrptDto.tableName()
							+ " where S_TRECODE = ? and S_RPTDATE = ? and S_BELONGFLAG = ? and S_TRIMFLAG = ? and S_TAXORGCODE = ? with ur";

					batchRetriever.clearParams();
					batchRetriever.addParam(strecode); // �������
					batchRetriever.addParam(rptDate); // ��������
					batchRetriever.addParam(rpttype); // Ͻ����־
					batchRetriever.addParam(MsgConstant.TIME_FLAG_NORMAL); // �����ڱ�־Ϊ������
					batchRetriever.addParam(taxsql); // �������ֵ����ջ��ش���
					batchRetriever.setMaxRows(MAX_NUM); // ����ÿ��ȡ��¼�������
				} else {
					// ����ǹ���˰����-�������ջ���������Ϊ����
					selectSQL = "select tn.S_TRECODE,tn.S_BUDGETLEVELCODE,ts.S_TBSTAXORGCODE,tn.S_BUDGETSUBCODE,tn.S_RPTDATE,tn.S_BUDGETTYPE,tn.N_MONEYDAY,tn.N_MONEYMONTH,tn.N_MONEYYEAR"
							+ " from "
							+ TrIncomedayrptDto.tableName()
							+ " tn , "
							+ TsConverttaxorgDto.tableName()
							+ " ts "
							+ " where tn.S_TAXORGCODE = ts.S_TCBSTAXORGCODE and tn.S_TRECODE = ts.S_TRECODE and ts.I_MODICOUNT = ? and tn.S_TRECODE = ? and tn.S_RPTDATE = ?"
							+ " and tn.S_BELONGFLAG = ? and tn.S_TRIMFLAG = ? and ts.S_TBSTAXORGCODE <> 'N' with ur";

					batchRetriever.clearParams();
					batchRetriever.addParam(taxprop); // ���ش�������
					batchRetriever.addParam(strecode); // �������
					batchRetriever.addParam(rptDate); // ��������
					batchRetriever.addParam(rpttype); // Ͻ����־
					batchRetriever.addParam(MsgConstant.TIME_FLAG_NORMAL); // �����ڱ�־Ϊ������
					batchRetriever.setMaxRows(MAX_NUM); // ����ÿ��ȡ��¼�������
				}

			}

			String incomeAllPath = srcpath + "s" + strecode + "_1" + rpttype
					+ rptDate + "_" + taxsql + ".txt";

			batchRetriever.runQuery(selectSQL);
			// Ԥ�����뱨���ʽͷ
			StringBuffer allbuf = new StringBuffer();
			allbuf
					.append("skgkdm,mdgkdm,ssgkdm,ysjc,jgdm,kmdm,zwrq,yszl,rlj,ylj,nlj");
			allbuf.append(StateConstant.SPACE_SPLIT);

			// �ڶ��� ɾ���������ļ�(�����Ѿ����ɹ�)
			FileUtil.getInstance().deleteFiles(
					ITFECommonConstant.FILE_ROOT_PATH + incomeAllPath);

			while (batchRetriever.hasMore()) {
				SQLResults result = batchRetriever.RetrieveNextBatch();

				int count = result.getRowCount();
				for (int i = 0; i < count; i++) {
					isAllIncomeRecordflag = true;
					int j = 0;
					String trecode = result.getString(i, j++);
					allbuf.append(trecode); // �������
					allbuf.append(StateConstant.INCOME_SPLIT);
					allbuf.append(trecode); // �������
					allbuf.append(StateConstant.INCOME_SPLIT);
					allbuf.append(trecode); // �������
					allbuf.append(StateConstant.INCOME_SPLIT);
					allbuf.append(result.getString(i, j++)); // Ԥ�㼶��
					allbuf.append(StateConstant.INCOME_SPLIT);
					allbuf.append(result.getString(i, j++)); // ���ջ��ش���
					allbuf.append(StateConstant.INCOME_SPLIT);
					allbuf.append(result.getString(i, j++)); // Ԥ���Ŀ����
					allbuf.append(StateConstant.INCOME_SPLIT);
					allbuf.append(result.getString(i, j++)); // ��������
					allbuf.append(StateConstant.INCOME_SPLIT);
					allbuf.append(result.getString(i, j++)); // Ԥ������
					allbuf.append(StateConstant.INCOME_SPLIT);
					// allbuf.append(result.getString(i, j++)); // Ԥ���Ŀ��������
					// allbuf.append(StateConstant.INCOME_SPLIT);
					allbuf.append(result.getBigDecimal(i, j++)); // ���ۼ�
					allbuf.append(StateConstant.INCOME_SPLIT);
					allbuf.append(result.getBigDecimal(i, j++)); // ���ۼ�
					allbuf.append(StateConstant.INCOME_SPLIT);
					allbuf.append(result.getBigDecimal(i, j++)); // ���ۼ�
					allbuf.append(StateConstant.SPACE_SPLIT);
				}
				// ׷�ӷ�ʽд�ļ�
				FileUtil.getInstance().writeFile(
						ITFECommonConstant.FILE_ROOT_PATH + incomeAllPath,
						allbuf.toString(), true);

				allbuf = new StringBuffer();
			}

			batchRetriever.closeConnection();

			if (isAllIncomeRecordflag) {
				return incomeAllPath;
			} else {
				return null;
			}
		} catch (JAFDatabaseException e) {
			String error = "��ѯԤ�����뱨��ʱ�������ݿ��쳣��";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		} catch (FileOperateException e) {
			String error = "����Ԥ�����뱨���ļ�ʱ�����쳣��";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (null != batchRetriever) {
				batchRetriever.closeConnection();
			}
		}
	}

	/**
	 * ����ձ�
	 * 
	 * @param String
	 *            strecode �������
	 * @param String
	 *            srptdate ��������
	 * @param String
	 *            srcpath �����·��
	 * 
	 * @throws ITFEBizException
	 */
	private String getStockRpt(String strecode, String rptDate, String srcpath)
			throws ITFEBizException {

		SQLBatchRetriever batchRetriever = null;
		try {
			boolean isStockRecordflag = false;
			String stockSQL = "select S_ACCNO,S_ACCNAME,S_ACCDATE,N_MONEYYESTERDAY,N_MONEYIN,N_MONEYOUT,N_MONEYTODAY "
					+ " from "
					+ TrStockdayrptDto.tableName()
					+ " where S_TRECODE = ? and S_RPTDATE = ? order by S_ACCNO with ur";

			// ��һ�� ��ѯ���������ļ�¼
			batchRetriever = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLBatchRetriever();
			batchRetriever.addParam(strecode); // �������
			batchRetriever.addParam(rptDate); // ��������
			batchRetriever.setMaxRows(MAX_NUM); // ����ÿ��ȡ��¼�������

			batchRetriever.runQuery(stockSQL);

			// ��汨���ʽͷ
			StringBuffer sbuf = new StringBuffer();
			sbuf.append("kjzh,kjzhm,zwrq,srye,brdfsr,brjfzc,brye");
			sbuf.append(StateConstant.SPACE_SPLIT);

			// String stockSelfPath = srcpath + strecode + "_" +
			// MsgConstant.RULE_SIGN_SELF + "_" + MsgConstant.REPORT_TYPE_STOCK
			// + ".txt";
			String stockSelfPath = srcpath + "s" + strecode + "_c"
					+ MsgConstant.RULE_SIGN_SELF + rptDate + ".txt";

			// �ڶ��� ɾ���������ļ�(�����Ѿ����ɹ�)
			FileUtil.getInstance().deleteFiles(
					ITFECommonConstant.FILE_ROOT_PATH + stockSelfPath);

			while (batchRetriever.hasMore()) {
				SQLResults result = batchRetriever.RetrieveNextBatch();

				int count = result.getRowCount();
				for (int i = 0; i < count; i++) {
					isStockRecordflag = true;
					int j = 0;
					// sbuf.append(result.getString(i, j++)); // �������
					// sbuf.append(StateConstant.INCOME_SPLIT);
					// sbuf.append(result.getString(i, j++)); // ��������
					// sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(result.getString(i, j++)); // �ʻ�����
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(result.getString(i, j++)); // �ʻ�����
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(result.getString(i, j++)); // �ʻ�����
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(result.getBigDecimal(i, j++)); // �������
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(result.getBigDecimal(i, j++)); // ��������
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(result.getBigDecimal(i, j++)); // ����֧��
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(result.getBigDecimal(i, j++)); // �������
					sbuf.append(StateConstant.SPACE_SPLIT);
				}
				// ׷�ӷ�ʽд�ļ�
				FileUtil.getInstance().writeFile(
						ITFECommonConstant.FILE_ROOT_PATH + stockSelfPath,
						sbuf.toString(), true);

				sbuf = new StringBuffer();
			}

			batchRetriever.closeConnection();

			if (isStockRecordflag) {
				return stockSelfPath;
			} else {
				return null;
			}
		} catch (JAFDatabaseException e) {
			String error = "��ѯ����ձ���ʱ�������ݿ��쳣��";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		} catch (FileOperateException e) {
			String error = "���ɿ���ձ����ļ�ʱ�����쳣��";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (null != batchRetriever) {
				batchRetriever.closeConnection();
			}
		}
	}

	/**
	 * �������ջ�������ȡ�����ջ��ش���
	 * 
	 * @param String
	 *            taxprop ���ջ�������
	 * @return
	 */
	private String getTaxBigClassByProp(String taxprop) {
		if (MsgConstant.MSG_TAXORG_NATION_PROP.equals(taxprop)) {
			return MsgConstant.MSG_TAXORG_NATION_CLASS;
		}
		if (MsgConstant.MSG_TAXORG_PLACE_PROP.equals(taxprop)) {
			return MsgConstant.MSG_TAXORG_PLACE_CLASS;
		}
		if (MsgConstant.MSG_TAXORG_CUSTOM_PROP.equals(taxprop)) {
			return MsgConstant.MSG_TAXORG_CUSTOM_CLASS;
		}
		if (MsgConstant.MSG_TAXORG_OTHER_PROP.equals(taxprop)) {
			return MsgConstant.MSG_TAXORG_OTHER_CLASS;
		}

		return MsgConstant.MSG_TAXORG_SHARE_CLASS;
	}

	public void sendApplyInfo(String acctdate, String msgid, String finorg,
			String sorgcode) throws ITFEBizException {
		try {
			if (MsgConstant.MSG_NO_5001.equals(msgid)) {
				MuleClient client = new MuleClient();
				Map map = new HashMap();
				MuleMessage message = new DefaultMuleMessage(map);
				message.setProperty(MessagePropertyKeys.MSG_NO_KEY, msgid
						+ "_OUT");
				message.setProperty(MessagePropertyKeys.MSG_BILL_CODE, finorg);
				message.setProperty(MessagePropertyKeys.MSG_DATE, acctdate);
				message.setProperty(MessagePropertyKeys.MSG_ORGCODE, sorgcode);
				message.setPayload(map);
				if(sorgcode!=null&&sorgcode.startsWith("1702"))
					message = client.send("vm://ManagerMsgToPbcCity", message);
				else
					message = client.send("vm://ManagerMsgToPbc", message);
			} else {
				HeadDto headdto = new HeadDto();
				headdto.set_VER(MsgConstant.MSG_HEAD_VER);
				headdto.set_SRC(finorg);
				headdto.set_APP(MsgConstant.MSG_HEAD_APP);
				headdto.set_DES(acctdate);
				headdto.set_msgNo(msgid);
				try {
					String msgid0 = MsgSeqFacade.getMsgSendSeq();
					headdto.set_msgID(msgid0);
					headdto.set_msgRef(msgid0);
				} catch (SequenceException e) {
					log.error("ȡ������ˮ��ʱ�����쳣��", e);
					throw new ITFEBizException("ȡ������ˮ��ʱ�����쳣��", e);
				}
				headdto.set_workDate(TimeFacade.getCurrentStringTime());

				MuleClient client = new MuleClient();
				Map map = new HashMap();
				MuleMessage message = new DefaultMuleMessage(map);
				message.setProperty(MessagePropertyKeys.MSG_NO_KEY, msgid
						+ "_OUT");
				message.setProperty(MessagePropertyKeys.MSG_HEAD_DTO, headdto);
				message.setProperty(MessagePropertyKeys.MSG_ORGCODE,
						getLoginInfo().getSorgcode());// �����������
				message.setProperty(MessagePropertyKeys.MSG_DATE, acctdate);// �������
				message.setProperty(MessagePropertyKeys.MSG_BILL_CODE, finorg);// ���ͻ��ش���

				message.setPayload(map);
				if(sorgcode!=null&&sorgcode.startsWith("1702"))
					message = client.send("vm://ManagerMsgToPbcCity", message);
				else
					message = client.send("vm://ManagerMsgToPbc", message);
			}

		} catch (MuleException e) {
			log.error(e);
			throw new ITFEBizException("�������뱨��ʧ��!", e);
		}

	}

	/**
	 * * ���뱨��(�����ں͵����ڶ���)
	 * 
	 * { SQLBatchRetriever batchRetriever = null; try { String selectSQL ="select S_TRECODE,S_BUDGETLEVELCODE,S_TAXORGCODE,S_BUDGETSUBCODE,S_RPTDATE,S_BUDGETTYPE,N_MONEYDAY,N_MONEYMONTH,N_MONEYYEAR"
	 * + " from " + TrIncomedayrptDto.tableName() +" where S_TRECODE = ? and S_RPTDATE = ? and S_BELONGFLAG = ? and S_TRIMFLAG = ? with ur"
	 * ;
	 * 
	 * 
	 * ��������(������)
	 * 
	 * { boolean isSelfIncomeRecordflag = false; // ��һ�� ��ѯ���������ļ�¼ batchRetriever
	 * = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLBatchRetriever();
	 * batchRetriever.addParam(strecode); // �������
	 * batchRetriever.addParam(rptDate); // ��������
	 * batchRetriever.addParam(MsgConstant.RULE_SIGN_SELF); // Ͻ����־Ϊ����
	 * batchRetriever.addParam(MsgConstant.TIME_FLAG_NORMAL); // �����ڱ�־Ϊ������
	 * batchRetriever.setMaxRows(MAX_NUM); // ����ÿ��ȡ��¼�������
	 * 
	 * batchRetriever.runQuery(selectSQL); StringBuffer selfbuf = new
	 * StringBuffer(); // Ԥ�����뱨���ʽͷ selfbuf.append(
	 * "skgkdm,mdgkdm,ssgkdm,ysjc,jgdm,kmdm,zwrq,yszl,rlj,ylj,nlj");
	 * selfbuf.append(StateConstant.SPACE_SPLIT);
	 * 
	 * // ˵���� ����TBS�ͱ�����ȫϽö��ֵ�෴������������⴦�� String incomeSelfPath = srcpath + "s" +
	 * strecode + "_1" + MsgConstant.RULE_SIGN_ALL + rptDate + "_" +
	 * MsgConstant.TIME_FLAG_NORMAL + ".txt";
	 * 
	 * // �ڶ��� ɾ���������ļ�(�����Ѿ����ɹ�)
	 * FileUtil.getInstance().deleteFiles(ITFECommonConstant.FILE_ROOT_PATH +
	 * incomeSelfPath);
	 * 
	 * while (batchRetriever.hasMore()) { SQLResults result =
	 * batchRetriever.RetrieveNextBatch();
	 * 
	 * int count = result.getRowCount(); for (int i = 0; i < count; i++) {
	 * isSelfIncomeRecordflag = true; int j = 0; String trecode =
	 * result.getString(i, j++); selfbuf.append(trecode); // �������
	 * selfbuf.append(StateConstant.INCOME_SPLIT); selfbuf.append(trecode); //
	 * ������� selfbuf.append(StateConstant.INCOME_SPLIT); selfbuf.append(trecode);
	 * // ������� selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getString(i, j++)); // Ԥ�㼶��
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getString(i, j++)); // ���ջ��ش���
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getString(i, j++)); // Ԥ���Ŀ����
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getString(i, j++)); // ��������
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getString(i, j++)); // Ԥ������
	 * selfbuf.append(StateConstant.INCOME_SPLIT); //
	 * selfbuf.append(result.getString(i, j++)); // Ԥ���Ŀ�������� //
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getBigDecimal(i, j++)); // ���ۼ�
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getBigDecimal(i, j++)); // ���ۼ�
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getBigDecimal(i, j++)); // ���ۼ�
	 * selfbuf.append(StateConstant.SPACE_SPLIT); } // ׷�ӷ�ʽд�ļ�
	 * FileUtil.getInstance().writeFile(ITFECommonConstant.FILE_ROOT_PATH +
	 * incomeSelfPath, selfbuf.toString(), true);
	 * 
	 * selfbuf = new StringBuffer(); }
	 * 
	 * if(isSelfIncomeRecordflag){ filepathList.add(incomeSelfPath); } }
	 * 
	 * 
	 * ��������(������)
	 * 
	 * { boolean isSelfIncomeRecordflag = false; // ��һ�� ��ѯ���������ļ�¼ batchRetriever
	 * = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLBatchRetriever();
	 * batchRetriever.addParam(strecode); // �������
	 * batchRetriever.addParam(rptDate); // ��������
	 * batchRetriever.addParam(MsgConstant.RULE_SIGN_SELF); // Ͻ����־Ϊ����
	 * batchRetriever.addParam(MsgConstant.TIME_FLAG_TRIM); // �����ڱ�־Ϊ������
	 * batchRetriever.setMaxRows(MAX_NUM); // ����ÿ��ȡ��¼�������
	 * 
	 * batchRetriever.runQuery(selectSQL); StringBuffer selfbuf = new
	 * StringBuffer(); // Ԥ�����뱨���ʽͷ selfbuf.append(
	 * "skgkdm,mdgkdm,ssgkdm,ysjc,jgdm,kmdm,zwrq,yszl,rlj,ylj,nlj");
	 * selfbuf.append(StateConstant.SPACE_SPLIT);
	 * 
	 * // ˵���� ����TBS�ͱ�����ȫϽö��ֵ�෴������������⴦�� String incomeSelfPath = srcpath + "s" +
	 * strecode + "_1" + MsgConstant.RULE_SIGN_ALL + rptDate + "_" +
	 * MsgConstant.TIME_FLAG_TRIM + ".txt";
	 * 
	 * // �ڶ��� ɾ���������ļ�(�����Ѿ����ɹ�)
	 * FileUtil.getInstance().deleteFiles(ITFECommonConstant.FILE_ROOT_PATH +
	 * incomeSelfPath);
	 * 
	 * while (batchRetriever.hasMore()) { SQLResults result =
	 * batchRetriever.RetrieveNextBatch();
	 * 
	 * int count = result.getRowCount(); for (int i = 0; i < count; i++) {
	 * isSelfIncomeRecordflag = true; int j = 0; String trecode =
	 * result.getString(i, j++); selfbuf.append(trecode); // �������
	 * selfbuf.append(StateConstant.INCOME_SPLIT); selfbuf.append(trecode); //
	 * ������� selfbuf.append(StateConstant.INCOME_SPLIT); selfbuf.append(trecode);
	 * // ������� selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getString(i, j++)); // Ԥ�㼶��
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getString(i, j++)); // ���ջ��ش���
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getString(i, j++)); // Ԥ���Ŀ����
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getString(i, j++)); // ��������
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getString(i, j++)); // Ԥ������
	 * selfbuf.append(StateConstant.INCOME_SPLIT); //
	 * selfbuf.append(result.getString(i, j++)); // Ԥ���Ŀ�������� //
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getBigDecimal(i, j++)); // ���ۼ�
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getBigDecimal(i, j++)); // ���ۼ�
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getBigDecimal(i, j++)); // ���ۼ�
	 * selfbuf.append(StateConstant.SPACE_SPLIT); } // ׷�ӷ�ʽд�ļ�
	 * FileUtil.getInstance().writeFile(ITFECommonConstant.FILE_ROOT_PATH +
	 * incomeSelfPath, selfbuf.toString(), true);
	 * 
	 * selfbuf = new StringBuffer(); }
	 * 
	 * if(isSelfIncomeRecordflag){ filepathList.add(incomeSelfPath); }
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * ȫϽ���루�����ڣ�
	 * 
	 * { boolean isAllIncomeRecordflag = false; // ��һ�� ��ѯ���������ļ�¼
	 * batchRetriever.clearParams(); batchRetriever.addParam(strecode); // �������
	 * batchRetriever.addParam(rptDate); // ��������
	 * batchRetriever.addParam(MsgConstant.RULE_SIGN_ALL); // Ͻ����־ΪȫϽ
	 * batchRetriever.addParam(MsgConstant.TIME_FLAG_TRIM); // �����ڱ�־Ϊ������
	 * batchRetriever.setMaxRows(MAX_NUM); // ����ÿ��ȡ��¼�������
	 * 
	 * batchRetriever.runQuery(selectSQL); // Ԥ�����뱨���ʽͷ StringBuffer allbuf =
	 * new StringBuffer(); allbuf.append(
	 * "skgkdm,mdgkdm,ssgkdm,ysjc,jgdm,kmdm,zwrq,yszl,rlj,ylj,nlj");
	 * allbuf.append(StateConstant.SPACE_SPLIT);
	 * 
	 * // ˵���� ����TBS�ͱ�����ȫϽö��ֵ�෴������������⴦�� // String incomeAllPath = srcpath +
	 * strecode + "_" + MsgConstant.RULE_SIGN_ALL + "_" +
	 * MsgConstant.REPORT_TYPE_INCOME + ".txt"; String incomeAllPath = srcpath +
	 * "s" + strecode + "_1" + MsgConstant.RULE_SIGN_SELF + rptDate + "_" +
	 * MsgConstant.TIME_FLAG_TRIM + ".txt";
	 * 
	 * // �ڶ��� ɾ���������ļ�(�����Ѿ����ɹ�)
	 * FileUtil.getInstance().deleteFiles(ITFECommonConstant.FILE_ROOT_PATH +
	 * incomeAllPath);
	 * 
	 * while (batchRetriever.hasMore()) { SQLResults result =
	 * batchRetriever.RetrieveNextBatch();
	 * 
	 * int count = result.getRowCount(); for (int i = 0; i < count; i++) {
	 * isAllIncomeRecordflag = true; int j = 0; String trecode =
	 * result.getString(i, j++); allbuf.append(trecode); // �������
	 * allbuf.append(StateConstant.INCOME_SPLIT); allbuf.append(trecode); //
	 * ������� allbuf.append(StateConstant.INCOME_SPLIT); allbuf.append(trecode);
	 * // ������� allbuf.append(StateConstant.INCOME_SPLIT);
	 * allbuf.append(result.getString(i, j++)); // Ԥ�㼶��
	 * allbuf.append(StateConstant.INCOME_SPLIT);
	 * allbuf.append(result.getString(i, j++)); // ���ջ��ش���
	 * allbuf.append(StateConstant.INCOME_SPLIT);
	 * allbuf.append(result.getString(i, j++)); // Ԥ���Ŀ����
	 * allbuf.append(StateConstant.INCOME_SPLIT);
	 * allbuf.append(result.getString(i, j++)); // ��������
	 * allbuf.append(StateConstant.INCOME_SPLIT);
	 * allbuf.append(result.getString(i, j++)); // Ԥ������
	 * allbuf.append(StateConstant.INCOME_SPLIT); //
	 * allbuf.append(result.getString(i, j++)); // Ԥ���Ŀ�������� //
	 * allbuf.append(StateConstant.INCOME_SPLIT);
	 * allbuf.append(result.getBigDecimal(i, j++)); // ���ۼ�
	 * allbuf.append(StateConstant.INCOME_SPLIT);
	 * allbuf.append(result.getBigDecimal(i, j++)); // ���ۼ�
	 * allbuf.append(StateConstant.INCOME_SPLIT);
	 * allbuf.append(result.getBigDecimal(i, j++)); // ���ۼ�
	 * allbuf.append(StateConstant.SPACE_SPLIT); } // ׷�ӷ�ʽд�ļ�
	 * FileUtil.getInstance().writeFile(ITFECommonConstant.FILE_ROOT_PATH +
	 * incomeAllPath, allbuf.toString(), true);
	 * 
	 * allbuf = new StringBuffer(); }
	 * 
	 * if(isAllIncomeRecordflag){ filepathList.add(incomeAllPath); } }
	 * 
	 * batchRetriever.closeConnection(); } catch (JAFDatabaseException e) {
	 * String error = "��ѯԤ�����뱨��ʱ�������ݿ��쳣��"; logger.error(error, e); throw new
	 * ITFEBizException(error, e); } catch (FileOperateException e) { String
	 * error = "����Ԥ�����뱨���ļ�ʱ�����쳣��"; logger.error(error, e); throw new
	 * ITFEBizException(error, e); } finally { if (null != batchRetriever) {
	 * batchRetriever.closeConnection(); } } }
	 * 
	 * 
	 * ����ձ�
	 * 
	 * { SQLBatchRetriever batchRetriever = null; try { boolean
	 * isStockRecordflag = false; String stockSQL ="select S_ACCNO,S_ACCNAME,S_ACCDATE,N_MONEYYESTERDAY,N_MONEYIN,N_MONEYOUT,N_MONEYTODAY "
	 * + " from " + TrStockdayrptDto.tableName() +
	 * " where S_TRECODE = ? and S_RPTDATE = ? order by S_ACCNO with ur";
	 * 
	 * // ��һ�� ��ѯ���������ļ�¼ batchRetriever =
	 * DatabaseFacade.getDb().getSqlExecutorFactory().getSQLBatchRetriever();
	 * batchRetriever.addParam(strecode); // �������
	 * batchRetriever.addParam(rptDate); // ��������
	 * batchRetriever.setMaxRows(MAX_NUM); // ����ÿ��ȡ��¼�������
	 * 
	 * batchRetriever.runQuery(stockSQL);
	 * 
	 * // ��汨���ʽͷ StringBuffer sbuf = new StringBuffer();
	 * sbuf.append("kjzh,kjzhm,zwrq,srye,brdfsr,brjfzc,brye");
	 * sbuf.append(StateConstant.SPACE_SPLIT);
	 * 
	 * // String stockSelfPath = srcpath + strecode + "_" +
	 * MsgConstant.RULE_SIGN_SELF + "_" + MsgConstant.REPORT_TYPE_STOCK +
	 * ".txt"; String stockSelfPath = srcpath + "s" + strecode + "_c" +
	 * MsgConstant.RULE_SIGN_ALL + rptDate + ".txt";
	 * 
	 * // �ڶ��� ɾ���������ļ�(�����Ѿ����ɹ�)
	 * FileUtil.getInstance().deleteFiles(ITFECommonConstant.FILE_ROOT_PATH +
	 * stockSelfPath);
	 * 
	 * while (batchRetriever.hasMore()) { SQLResults result =
	 * batchRetriever.RetrieveNextBatch();
	 * 
	 * int count = result.getRowCount(); for (int i = 0; i < count; i++) {
	 * isStockRecordflag = true; int j = 0; // sbuf.append(result.getString(i,
	 * j++)); // ������� // sbuf.append(StateConstant.INCOME_SPLIT); //
	 * sbuf.append(result.getString(i, j++)); // �������� //
	 * sbuf.append(StateConstant.INCOME_SPLIT); sbuf.append(result.getString(i,
	 * j++)); // �ʻ����� sbuf.append(StateConstant.INCOME_SPLIT);
	 * sbuf.append(result.getString(i, j++)); // �ʻ�����
	 * sbuf.append(StateConstant.INCOME_SPLIT); sbuf.append(result.getString(i,
	 * j++)); // �ʻ����� sbuf.append(StateConstant.INCOME_SPLIT);
	 * sbuf.append(result.getBigDecimal(i, j++)); // �������
	 * sbuf.append(StateConstant.INCOME_SPLIT);
	 * sbuf.append(result.getBigDecimal(i, j++)); // ��������
	 * sbuf.append(StateConstant.INCOME_SPLIT);
	 * sbuf.append(result.getBigDecimal(i, j++)); // ����֧��
	 * sbuf.append(StateConstant.INCOME_SPLIT);
	 * sbuf.append(result.getBigDecimal(i, j++)); // �������
	 * sbuf.append(StateConstant.SPACE_SPLIT); } // ׷�ӷ�ʽд�ļ�
	 * FileUtil.getInstance().writeFile(ITFECommonConstant.FILE_ROOT_PATH +
	 * stockSelfPath, sbuf.toString(), true);
	 * 
	 * sbuf = new StringBuffer(); }
	 * 
	 * if(isStockRecordflag){ filepathList.add(stockSelfPath); }
	 * 
	 * batchRetriever.closeConnection(); } catch (JAFDatabaseException e) {
	 * String error = "��ѯ����ձ���ʱ�������ݿ��쳣��"; logger.error(error, e); throw new
	 * ITFEBizException(error, e); } catch (FileOperateException e) { String
	 * error = "���ɿ���ձ����ļ�ʱ�����쳣��"; logger.error(error, e); throw new
	 * ITFEBizException(error, e); } finally { if (null != batchRetriever) {
	 * batchRetriever.closeConnection(); } }
	 * 
	 * }
	 */
}
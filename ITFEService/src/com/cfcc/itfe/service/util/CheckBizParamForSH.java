package com.cfcc.itfe.service.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpayMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvDwbkDto;
import com.cfcc.itfe.persistence.dto.TbsTvGrantpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TbsTvPbcpayDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

public class CheckBizParamForSH {
	/**
	 * Ϊ�Ϻ��Ĳ�¼�к�У�鵥������һ����
	 */
	private static Log logger = LogFactory.getLog(CheckBizParamForSH.class);

	/**
	 * �ж��Ƿ�ά�����д�����֧���кŶ�Ӧ��ϵ
	 * 
	 * @param bizType
	 * @param fileName
	 * @return
	 * @throws ITFEBizException
	 */
	static boolean checkGenbankandreckbank(String bizType, TvFilepackagerefDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			boolean bresult = true;
			String selectSQL = "";
			String bankName = "";
			String fileName = null;
			String treCode = null;
			if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// ֱ��֧��
				/**
				 * �����ж��Ƿ񻹴���Ϊ��¼�кŵļ�¼������ֱ���׳��쳣
				 */
				selectSQL = "SELECT S_FILENAME,S_PAYEEOPNBNKNO FROM TBS_TV_DIRECTPAYPLAN_MAIN WHERE (S_PAYEEOPNBNKNO IS NULL OR S_PAYEEOPNBNKNO ='')"
				+ " and S_TRECODE<>S_PAYEEOPNBNKNO AND S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? ) and S_IFMATCH=? ";
				sqlExec.addParam(loginfo.getSorgcode());
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",""));// δ����
				sqlExec.addParam(StateConstant.IF_MATCHBNKNAME_YES);
				if(null != idto) {
					selectSQL += " AND S_FILENAME= ? ";
					sqlExec.addParam(idto.getSfilename());
				}
				SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
				if (trasrlnoRs.getRowCount() > 0) {
					bresult = false;
					logger.error("�ļ�[" + trasrlnoRs.getString(0, 0) + "]�д���δ��¼�տ��˿������к���Ϣ������ȷ���ύ!");
					throw new ITFEBizException("�ļ�[" + trasrlnoRs.getString(0, 0) + "]�д���δ��¼�տ��˿������к���Ϣ������ȷ���ύ!");
				}
				sqlExec.clearParams();
				
				if (null==idto) {
					selectSQL = "SELECT DISTINCT S_PAYEEOPNBNKNO,S_FILENAME FROM TBS_TV_DIRECTPAYPLAN_MAIN A "
							+ " WHERE S_TRECODE<>S_PAYEEOPNBNKNO AND S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? ) AND (S_IFMATCH IS NULL OR S_IFMATCH=?) "
							+ " AND NOT EXISTS (SELECT 1 FROM Ts_Genbankandreckbank WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_PAYEEOPNBNKNO) ";
				} else {
					fileName=idto.getSfilename();
					treCode=idto.getStrecode();
					selectSQL = "SELECT DISTINCT S_PAYEEOPNBNKNO,S_FILENAME FROM TBS_TV_DIRECTPAYPLAN_MAIN A "
							+ " WHERE  S_TRECODE<>S_PAYEEOPNBNKNO AND S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ? AND (S_IFMATCH IS NULL OR S_IFMATCH=?) "
							+ " AND NOT EXISTS (SELECT 1 FROM Ts_Genbankandreckbank WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_PAYEEOPNBNKNO) ";
				}
				bankName = "�տ��˿�����";
			} else {
				return false;
			}
			
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			// ��������
			sqlExec.addParam(loginfo.getSorgcode());

			if (null==idto) {
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",
						""));// δ����
			} else {
				sqlExec.addParam(fileName);// �����ļ���
				sqlExec.addParam(treCode);//��������
			}
			sqlExec.addParam(StateConstant.IF_MATCHBNKNAME_NO);
			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("�ļ�[" + trasrlnoRs.getString(0, 1) + "]�е�"
						+ bankName + "[" + trasrlnoRs.getString(0, 0)
						+ "]δά�����д�����֧���кŶ�Ӧ��ϵ������ȷ���ύ!");
				throw new ITFEBizException("�ļ�[" + trasrlnoRs.getString(0, 1)
						+ "]�е�" + bankName + "[" + trasrlnoRs.getString(0, 0)
						+ "]δά�����д�����֧���кŶ�Ӧ��ϵ������ȷ���ύ!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("��ѯ���ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ���ݵ�ʱ������쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * �ж��Ƿ�ά�����д�����֧���кŶ�Ӧ��ϵ
	 * 
	 * @param bizType
	 * @param fileName
	 * @return
	 * @throws ITFEBizException
	 */
	static boolean checkGenbankandreckbank(String bizType, IDto idto)
			throws ITFEBizException {
		boolean bresult = true;
		SQLExecutor sqlExec = null;
		String selectSQL = "";
		String strVousrlno = "";
		String bankName = "";
		try {
			if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {// ��Ȩ֧��
				selectSQL = "SELECT DISTINCT S_BNKNO,S_FILENAME FROM TBS_TV_GRANTPAYPLAN_MAIN A "
						+ " WHERE I_VOUSRLNO= ? AND (S_IFMATCH IS NULL OR S_IFMATCH=?) "
						+ " AND NOT EXISTS (SELECT 1 FROM Ts_Genbankandreckbank WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_BNKNO)";
				strVousrlno = String.valueOf(((TbsTvGrantpayplanMainDto) idto)
						.getIvousrlno());
				bankName = "�������д���";
			} else if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// ֱ��֧��
				strVousrlno = String.valueOf(((TbsTvDirectpayplanMainDto) idto)
						.getIvousrlno());
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				/**
				 * �����ж��Ƿ񻹴���Ϊ��¼�кŵļ�¼������ֱ���׳��쳣
				 */
				selectSQL = "SELECT S_FILENAME,S_PAYEEOPNBNKNO FROM TBS_TV_DIRECTPAYPLAN_MAIN WHERE (S_PAYEEOPNBNKNO IS NULL OR S_PAYEEOPNBNKNO ='')"
				+ " and S_TRECODE<>S_PAYEEOPNBNKNO AND I_VOUSRLNO= ? AND (S_STATUS IS NULL OR S_STATUS= ? ) and S_IFMATCH=? ";
				sqlExec.addParam(strVousrlno);
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",""));// δ����
				sqlExec.addParam(StateConstant.IF_MATCHBNKNAME_YES);
				SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
				if (trasrlnoRs.getRowCount() > 0) {
					bresult = false;
					logger.error("�ļ�[" + trasrlnoRs.getString(0, 0) + "]�д���δ��¼�տ��˿������к���Ϣ������ȷ���ύ!");
					throw new ITFEBizException("�ļ�[" + trasrlnoRs.getString(0, 0) + "]�д���δ��¼�տ��˿������к���Ϣ������ȷ���ύ!");
				}
				sqlExec.clearParams();
				
				selectSQL = "SELECT DISTINCT S_PAYEEOPNBNKNO,S_FILENAME FROM TBS_TV_DIRECTPAYPLAN_MAIN A "
						+ " WHERE  S_TRECODE<>S_PAYEEOPNBNKNO AND I_VOUSRLNO= ? AND (S_IFMATCH IS NULL OR S_IFMATCH=?) "
						+ " AND NOT EXISTS (SELECT 1 FROM Ts_Genbankandreckbank WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_PAYEEOPNBNKNO)";
				
				bankName = "�տ��˿�����";
			} else
				return false;
		
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			// ƾ֤��ˮ��
			sqlExec.addParam(strVousrlno);
			sqlExec.addParam(StateConstant.IF_MATCHBNKNAME_NO);
			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("ƾ֤��ˮ��Ϊ[" + strVousrlno + "]�ļ�¼�е�" + bankName
						+ "[" + trasrlnoRs.getString(0, 0)
						+ "]δά�����д�����֧���кŶ�Ӧ��ϵ������ȷ���ύ!");
				throw new ITFEBizException("ƾ֤��ˮ��Ϊ[" + strVousrlno + "]�ļ�¼�е�"
						+ bankName + "[" + trasrlnoRs.getString(0, 0)
						+ "]δά�����д�����֧���кŶ�Ӧ��ϵ������ȷ���ύ!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("��ѯ���ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ���ݵ�ʱ������쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * ���ӵ����ķ�����У���Ƿ���δ��¼�кż�¼����
	 * @return
	 */
	static boolean checkIsMatchNameOver(String bizType, TvFilepackagerefDto idto,ITFELoginInfo loginfo) throws ITFEBizException{
		SQLExecutor sqlExec = null;
		String selectSQL = "";
		boolean bresult = true;
		try {
			if (bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)
					||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)) {
				// ���а���֧����������
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				/**
				 * �����ж��Ƿ񻹴���Ϊ��¼�кŵļ�¼������ֱ���׳��쳣
				 */
				if(null != idto) {
					selectSQL = "SELECT S_FILENAME,S_PAYEEOPNBNKNO FROM TBS_TV_BNKPAY_MAIN WHERE (S_PAYEEOPNBNKNO IS NULL OR S_PAYEEOPNBNKNO ='') 	"
						+ "  AND (S_STATE IS NULL OR S_STATE= ? ) and S_BOOKORGCODE=? and S_IFMATCH=? and s_filename=? ";
				}else {
					selectSQL = "SELECT S_FILENAME,S_PAYEEOPNBNKNO FROM TBS_TV_BNKPAY_MAIN WHERE (S_PAYEEOPNBNKNO IS NULL OR S_PAYEEOPNBNKNO ='') 	"
						+ "  AND (S_STATE IS NULL OR S_STATE= ? ) and S_BOOKORGCODE=? and S_IFMATCH=? ";
				}
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",""));// δ����
				sqlExec.addParam(loginfo.getSorgcode());
				sqlExec.addParam(StateConstant.IF_MATCHBNKNAME_YES);
				if(null != idto) {
					sqlExec.addParam(idto.getSfilename());
				}
	 			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
				if (trasrlnoRs.getRowCount() > 0) {
					bresult = false;
					logger.error("�ļ�[" + trasrlnoRs.getString(0, 0) + "]�д���δ��¼�տ��˿������к���Ϣ������ȷ���ύ!");
					throw new ITFEBizException("�ļ�[" + trasrlnoRs.getString(0, 0) + "]�д���δ��¼�տ��˿������к���Ϣ������ȷ���ύ!");
				}
			}else if(bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)
					||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) {
				//���л��������˻�
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				/**
				 * �����ж��Ƿ񻹴���Ϊ��¼�кŵļ�¼������ֱ���׳��쳣
				 */
				if(null != idto) {
					selectSQL = "SELECT S_FILENAME,S_PAYEEOPNBNKNO FROM TBS_TV_BNKPAY_MAIN WHERE (S_PAYEROPNBNKNO IS NULL OR S_PAYEROPNBNKNO ='') 	"
						+ "  AND (S_STATE IS NULL OR S_STATE= ? ) and S_BOOKORGCODE=? and S_IFMATCH=? and s_filename=? ";
				}else {
					selectSQL = "SELECT S_FILENAME,S_PAYEEOPNBNKNO FROM TBS_TV_BNKPAY_MAIN WHERE (S_PAYEROPNBNKNO IS NULL OR S_PAYEROPNBNKNO ='') 	"
						+ "  AND (S_STATE IS NULL OR S_STATE= ? ) and S_BOOKORGCODE=? and S_IFMATCH=? ";
				}
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",""));// δ����
				sqlExec.addParam(loginfo.getSorgcode());
				sqlExec.addParam(StateConstant.IF_MATCHBNKNAME_YES);
				if(null != idto) {
					sqlExec.addParam(idto.getSfilename());
				}
	 			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
				if (trasrlnoRs.getRowCount() > 0) {
					bresult = false;
					logger.error("�ļ�[" + trasrlnoRs.getString(0, 0) + "]�д���δ��¼�����˿������к���Ϣ������ȷ���ύ!");
					throw new ITFEBizException("�ļ�[" + trasrlnoRs.getString(0, 0) + "]�д���δ��¼�����˿������к���Ϣ������ȷ���ύ!");
				}
			}
		} 
		catch (JAFDatabaseException e) {
			logger.error("�����ύ�����쳣!", e);
			throw new ITFEBizException("�����ύ�����쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
		
		return bresult;
	}

	/**
	 * Ϊ��������ṩ�ж��Ƿ���δ��¼�к���Ϣ
	 * @param bizType
	 * @param idto
	 * @return
	 * @throws ITFEBizException
	 */
	public static boolean checkIsMatchNameOverEach(String bizType, IDto idto,ITFELoginInfo loginfo) throws ITFEBizException {
		SQLExecutor sqlExec = null;
		String selectSQL = "";
		boolean bresult = true;
		try {
			if (bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)
					||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)
					) {
				// ���а���֧����������
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				/**
				 * �����ж��Ƿ񻹴���Ϊ��¼�кŵļ�¼������ֱ���׳��쳣
				 */
				if(null != idto) {
					selectSQL = "SELECT S_FILENAME,S_PAYEEOPNBNKNO FROM TBS_TV_BNKPAY_MAIN WHERE (S_PAYEEOPNBNKNO IS NULL OR S_PAYEEOPNBNKNO ='') 	"
						+ "  AND (S_STATE IS NULL OR S_STATE= ? ) and S_BOOKORGCODE=? and S_IFMATCH=? and I_VOUSRLNO=? ";
				}else {
					selectSQL = "SELECT S_FILENAME,S_PAYEEOPNBNKNO FROM TBS_TV_BNKPAY_MAIN WHERE (S_PAYEEOPNBNKNO IS NULL OR S_PAYEEOPNBNKNO ='') 	"
						+ "  AND (S_STATE IS NULL OR S_STATE= ? ) and S_BOOKORGCODE=? and S_IFMATCH=? ";
				}
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",""));// δ����
				sqlExec.addParam(loginfo.getSorgcode());
				sqlExec.addParam(StateConstant.IF_MATCHBNKNAME_YES);
				if(null != idto) {
					sqlExec.addParam(((TbsTvBnkpayMainDto)idto).getIvousrlno());
				}
	 			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
				if (trasrlnoRs.getRowCount() > 0) {
					bresult = false;
					logger.error("�ļ�[" + trasrlnoRs.getString(0, 0) + "]�д���δ��¼�տ��˿������к���Ϣ������ȷ���ύ!");
					throw new ITFEBizException("�ļ�[" + trasrlnoRs.getString(0, 0) + "]�д���δ��¼�տ��˿������к���Ϣ������ȷ���ύ!");
				}
			}else if(bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)
					||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) {
				// ���а���֧�����������˻�
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				/**
				 * �����ж��Ƿ񻹴���Ϊ��¼�кŵļ�¼������ֱ���׳��쳣
				 */
				if(null != idto) {
					selectSQL = "SELECT S_FILENAME,S_PAYEEOPNBNKNO FROM TBS_TV_BNKPAY_MAIN WHERE (S_PAYEROPNBNKNO IS NULL OR S_PAYEROPNBNKNO ='') 	"
						+ "  AND (S_STATE IS NULL OR S_STATE= ? ) and S_BOOKORGCODE=? and S_IFMATCH=? and I_VOUSRLNO=? ";
				}else {
					selectSQL = "SELECT S_FILENAME,S_PAYEEOPNBNKNO FROM TBS_TV_BNKPAY_MAIN WHERE (S_PAYEROPNBNKNO IS NULL OR S_PAYEROPNBNKNO ='') 	"
						+ "  AND (S_STATE IS NULL OR S_STATE= ? ) and S_BOOKORGCODE=? and S_IFMATCH=? ";
				}
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",""));// δ����
				sqlExec.addParam(loginfo.getSorgcode());
				sqlExec.addParam(StateConstant.IF_MATCHBNKNAME_YES);
				if(null != idto) {
					sqlExec.addParam(((TbsTvBnkpayMainDto)idto).getIvousrlno());
				}
	 			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
				if (trasrlnoRs.getRowCount() > 0) {
					bresult = false;
					logger.error("�ļ�[" + trasrlnoRs.getString(0, 0) + "]�д���δ��¼�����˿������к���Ϣ������ȷ���ύ!");
					throw new ITFEBizException("�ļ�[" + trasrlnoRs.getString(0, 0) + "]�д���δ��¼�����˿������к���Ϣ������ȷ���ύ!");
				}
			}
		} 
		catch (JAFDatabaseException e) {
			logger.error("�����ύ�����쳣!", e);
			throw new ITFEBizException("�����ύ�����쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
		return bresult;
	}
}

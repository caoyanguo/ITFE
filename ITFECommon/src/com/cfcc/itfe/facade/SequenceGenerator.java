/*
 * �������� 2005-7-12
 *
 * �����������ļ�ģ��Ϊ
 * ���� > ��ѡ�� > Java > �������� > �����ע��
 */
package com.cfcc.itfe.facade;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * ����Sequence
 * 
 * @author ������
 */
public class SequenceGenerator {
	
	private static Log _logger = LogFactory.getLog(SequenceGenerator.class);

	/**
	 * ͨ��DB2��sequence�����ȡ��һ��ֵ
	 * 
	 * @param seqname
	 *            sequence����
	 * @param cache
	 *            ������
	 * @param startWith
	 *            �Ӽ���ʼ
	 * @return sequence
	 * @throws SequenceException
	 */
	public static String getNextByDb2(String seqname, int cache, int startWith)
			throws SequenceException {
		String seq = null;

		if (seqname == null) {
			_logger.error("��ȡ���к�����Ϊ�ա�");

			throw new SequenceException("��ȡ���к�����Ϊ�ա�");
		}

		seqname = "ITFE_" + seqname;
		
		String sqlGet;
		if (ITFECommonConstant.DBTYPE.contains("db2")) {
			 sqlGet = "values (nextval for " + seqname + " )";
		} else {
			 sqlGet = "SELECT " + seqname + ".NEXTVAL FROM DUAL";
		}
		
		try {
			seq = getNewNextNumber(sqlGet);

		} catch (JAFDatabaseException se) {
			_logger.info("��ȡ���к�:" + seqname + "����.�����ǲ�����!" + se);
			try {
				createNewSeq(seqname, cache, startWith);
				seq = getNewNextNumber(sqlGet);
			} catch (JAFDatabaseException sqle) {
				String errorinfo = "��ȡ���к�:" + seqname + "����.";
				_logger.error(errorinfo, sqle);

				throw new SequenceException(errorinfo, sqle);
			}
		}

		_logger.debug("��ȡ���к�:" + seqname + "�ɹ�:" + seq);
		return seq;
	}
	/**
	 * ͨ��DB2��sequence�����ȡ��һ��ֵ
	 * 
	 * @param seqname
	 *            sequence����
	 * @param cache
	 *            ������
	 * @param startWith
	 *            �Ӽ���ʼ
	 * @return sequence
	 * @throws SequenceException
	 */
	public static String getNextByDb2(String seqname, int cache, int startWith,int maxvalue)
	throws SequenceException {
		String seq = null;
		
		if (seqname == null) {
			_logger.error("��ȡ���к�����Ϊ�ա�");
			
			throw new SequenceException("��ȡ���к�����Ϊ�ա�");
		}
		
		seqname = "ITFE_" + seqname;
		
		String sqlGet;
		if (ITFECommonConstant.DBTYPE.contains("db2")) {
			 sqlGet = "values (nextval for " + seqname + " )";
		} else {
			 sqlGet = "SELECT " + seqname + ".NEXTVAL FROM DUAL";
		}
		
		
		try {
			seq = getNewNextNumber(sqlGet);
			
		} catch (JAFDatabaseException se) {
			_logger.info("��ȡ���к�:" + seqname + "����.�����ǲ�����!" + se);
			try {
				createNewSeqWithCry(seqname, cache, startWith, maxvalue);
				seq = getNewNextNumber(sqlGet);
			} catch (JAFDatabaseException sqle) {
				String errorinfo = "��ȡ���к�:" + seqname + "����.";
				_logger.error(errorinfo, sqle);
				
				throw new SequenceException(errorinfo, sqle);
			}
		}
		
		_logger.debug("��ȡ���к�:" + seqname + "�ɹ�:" + seq);
		return seq;
	}
	
	/**
	 * ͨ��DB2��sequence�����ȡ��һ��ֵ
	 * 
	 * @param seqname
	 *            sequence����
	 * @return sequence
	 * @throws SequenceException
	 */
	public static String getNextByDb2(String seqname) throws SequenceException {
		return getNextByDb2(seqname, SequenceName.TRAID_SEQ_CACHE, SequenceName.TRAID_SEQ_STARTWITH);
	}

	/**
	 * �����µ�sequence����
	 * 
	 * @param seqname
	 *            sequence����
	 * @param cache
	 *            ������
	 * @param startWith
	 *            �Ӽ���ʼ
	 * @throws JAFDatabaseException
	 */
	private static void createNewSeq(String seqname, int cache, int startWith)
			throws JAFDatabaseException {
		String sqlNew;
		if (cache > 0)
			sqlNew = " create sequence  " + seqname + " start with "
					+ startWith + "  increment by 1   cache " + cache
					+ "  order ";

		else

			sqlNew = " create sequence  " + seqname + " start with "
					+ startWith + "  increment by 1   order ";

		SQLExecutor sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
				.getSQLExecutor();
		sqlExec.runQueryCloseCon(sqlNew);
	}

	/**
	 * ѭ������SEQ
	 * 
	 * @param seqname
	 * @param cache
	 * @param startWith
	 * @throws JAFDatabaseException
	 */
	private static void createNewSeqWithCry(String seqname, int cache,
			int startWith, int maxVal) throws JAFDatabaseException {
		String sqlNew = " create sequence  " + seqname
				+ " minvalue 1 maxvalue " + maxVal
				+ "  start with 1 increment by 1 cache 1000 cycle order";

		SQLExecutor sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
				.getSQLExecutor();
		sqlExec.runQueryCloseCon(sqlNew);
	}

	/**
	 * ���һ����ֵ
	 * 
	 * @param sqlGet
	 *            sql���
	 * @return
	 * @throws JAFDatabaseException
	 */
	private static String getNewNextNumber(String sqlGet)
			throws JAFDatabaseException {
		if (_logger.isDebugEnabled())
			_logger.debug("��ȡSequence����ֵ��" + sqlGet);
		SQLExecutor sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
				.getSQLExecutor();

		SQLResults rs2 = sqlExec.runQueryCloseCon(sqlGet);
		long seqlong = rs2.getLong(0, 0);
		return String.valueOf(seqlong);

	}

	/**
	 * ��������ת������ˮ��
	 * ��ˮ�ű������: 5λ����ˮ�� + 3λ����ϸ������ˮ��
	 * ����ˮ�����ɷ�ʽ:ȡ�������еĺ���λ + "000"
	 * ��ϸ������ˮ��:����ˮ�� + 3λ����ϸ��(��ΪTIPSĬ�ϵİ���ϸ�����Ϊ1000��)
	 * @param seqnopk
	 * @return
	 */
	public static String changePackNo(String seqno){
		String tmpPackNo = "00000" + seqno;
		String packNo = tmpPackNo.substring(tmpPackNo.length() -5 ,tmpPackNo.length());
		
		return packNo;
	}
	/**
	 * ��������ת������ˮ��
	 * ��ˮ�ű������: 5λ����ˮ�� + 3λ����ϸ������ˮ��
	 * ����ˮ�����ɷ�ʽ:ȡ�������еĺ���λ + "000"
	 * ��ϸ������ˮ��:����ˮ�� + 3λ����ϸ��(��ΪTIPSĬ�ϵİ���ϸ�����Ϊ1000��)
	 *   �ط�������
	 * @param seqnopk
	 * @return
	 */
	public static String changePackNoForLocal(String seqno){
		String tmpPackNo = "0000000" + seqno;
		String packNo = tmpPackNo.substring(tmpPackNo.length() -8 ,tmpPackNo.length());
		
		return packNo;
	}
	
	/**
	 * ��������ת������ˮ��
	 * ��ˮ�ű������: 5λ����ˮ�� + 3λ����ϸ������ˮ��
	 * ����ˮ�����ɷ�ʽ:ȡ�������еĺ���λ + "000"
	 * ��ϸ������ˮ��:����ˮ�� + 3λ����ϸ��(��ΪTIPSĬ�ϵİ���ϸ�����Ϊ1000��)
	 * @param trasrlno
	 * @return
	 */
	public static String changeTraSrlNo(String packNo,int count){
		String tmpTrasrlNo = "000" + String.valueOf(count);
		String trasrlNo = tmpTrasrlNo.substring(tmpTrasrlNo.length() -3 ,tmpTrasrlNo.length());
		return packNo + trasrlNo;
	}
	
	/**
	 * ת����ˮ��(ǰ8λ������)
	 * @param int count ��ˮ�ŵ�λ��(�������8)
	 * @param String seqno ԭʼ��ˮ��
	 * @return
	 */
	public static String changeTraSrlNo(int count,String seqno){
		String tmpTrasrlNo = "000000000000000000000000000" + seqno;
		String trasrlNo = tmpTrasrlNo.substring(tmpTrasrlNo.length() - (count - 8) ,tmpTrasrlNo.length());
		
		String currentDate = TimeFacade.getCurrentStringTime(); // ��ǰϵͳ��ʱ��
		
		return currentDate+trasrlNo;
	}
	
	public static void main(String[] args) {
	}

}

/*
 * 创建日期 2005-7-12
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
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
 * 生成Sequence
 * 
 * @author 赵新鹏
 */
public class SequenceGenerator {
	
	private static Log _logger = LogFactory.getLog(SequenceGenerator.class);

	/**
	 * 通过DB2的sequence对象获取下一个值
	 * 
	 * @param seqname
	 *            sequence名称
	 * @param cache
	 *            缓存数
	 * @param startWith
	 *            从几开始
	 * @return sequence
	 * @throws SequenceException
	 */
	public static String getNextByDb2(String seqname, int cache, int startWith)
			throws SequenceException {
		String seq = null;

		if (seqname == null) {
			_logger.error("获取序列号名称为空。");

			throw new SequenceException("获取序列号名称为空。");
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
			_logger.info("获取序列号:" + seqname + "错误.可能是不存在!" + se);
			try {
				createNewSeq(seqname, cache, startWith);
				seq = getNewNextNumber(sqlGet);
			} catch (JAFDatabaseException sqle) {
				String errorinfo = "获取序列号:" + seqname + "错误.";
				_logger.error(errorinfo, sqle);

				throw new SequenceException(errorinfo, sqle);
			}
		}

		_logger.debug("获取序列号:" + seqname + "成功:" + seq);
		return seq;
	}
	/**
	 * 通过DB2的sequence对象获取下一个值
	 * 
	 * @param seqname
	 *            sequence名称
	 * @param cache
	 *            缓存数
	 * @param startWith
	 *            从几开始
	 * @return sequence
	 * @throws SequenceException
	 */
	public static String getNextByDb2(String seqname, int cache, int startWith,int maxvalue)
	throws SequenceException {
		String seq = null;
		
		if (seqname == null) {
			_logger.error("获取序列号名称为空。");
			
			throw new SequenceException("获取序列号名称为空。");
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
			_logger.info("获取序列号:" + seqname + "错误.可能是不存在!" + se);
			try {
				createNewSeqWithCry(seqname, cache, startWith, maxvalue);
				seq = getNewNextNumber(sqlGet);
			} catch (JAFDatabaseException sqle) {
				String errorinfo = "获取序列号:" + seqname + "错误.";
				_logger.error(errorinfo, sqle);
				
				throw new SequenceException(errorinfo, sqle);
			}
		}
		
		_logger.debug("获取序列号:" + seqname + "成功:" + seq);
		return seq;
	}
	
	/**
	 * 通过DB2的sequence对象获取下一个值
	 * 
	 * @param seqname
	 *            sequence名称
	 * @return sequence
	 * @throws SequenceException
	 */
	public static String getNextByDb2(String seqname) throws SequenceException {
		return getNextByDb2(seqname, SequenceName.TRAID_SEQ_CACHE, SequenceName.TRAID_SEQ_STARTWITH);
	}

	/**
	 * 创建新的sequence对象
	 * 
	 * @param seqname
	 *            sequence名称
	 * @param cache
	 *            缓存数
	 * @param startWith
	 *            从几开始
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
	 * 循环创建SEQ
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
	 * 获得一个新值
	 * 
	 * @param sqlGet
	 *            sql语句
	 * @return
	 * @throws JAFDatabaseException
	 */
	private static String getNewNextNumber(String sqlGet)
			throws JAFDatabaseException {
		if (_logger.isDebugEnabled())
			_logger.debug("获取Sequence对象值：" + sqlGet);
		SQLExecutor sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
				.getSQLExecutor();

		SQLResults rs2 = sqlExec.runQueryCloseCon(sqlGet);
		long seqlong = rs2.getLong(0, 0);
		return String.valueOf(seqlong);

	}

	/**
	 * 根据序列转化包流水号
	 * 流水号编码规则: 5位包流水号 + 3位的明细交易流水号
	 * 包流水号生成方式:取得新序列的后五位 + "000"
	 * 明细交易流水号:包流水号 + 3位的明细号(因为TIPS默认的包明细最大数为1000笔)
	 * @param seqnopk
	 * @return
	 */
	public static String changePackNo(String seqno){
		String tmpPackNo = "00000" + seqno;
		String packNo = tmpPackNo.substring(tmpPackNo.length() -5 ,tmpPackNo.length());
		
		return packNo;
	}
	/**
	 * 根据序列转化包流水号
	 * 流水号编码规则: 5位包流水号 + 3位的明细交易流水号
	 * 包流水号生成方式:取得新序列的后五位 + "000"
	 * 明细交易流水号:包流水号 + 3位的明细号(因为TIPS默认的包明细最大数为1000笔)
	 *   地方横连版
	 * @param seqnopk
	 * @return
	 */
	public static String changePackNoForLocal(String seqno){
		String tmpPackNo = "0000000" + seqno;
		String packNo = tmpPackNo.substring(tmpPackNo.length() -8 ,tmpPackNo.length());
		
		return packNo;
	}
	
	/**
	 * 根据序列转化包流水号
	 * 流水号编码规则: 5位包流水号 + 3位的明细交易流水号
	 * 包流水号生成方式:取得新序列的后五位 + "000"
	 * 明细交易流水号:包流水号 + 3位的明细号(因为TIPS默认的包明细最大数为1000笔)
	 * @param trasrlno
	 * @return
	 */
	public static String changeTraSrlNo(String packNo,int count){
		String tmpTrasrlNo = "000" + String.valueOf(count);
		String trasrlNo = tmpTrasrlNo.substring(tmpTrasrlNo.length() -3 ,tmpTrasrlNo.length());
		return packNo + trasrlNo;
	}
	
	/**
	 * 转化流水号(前8位是日期)
	 * @param int count 流水号的位数(必须大于8)
	 * @param String seqno 原始流水号
	 * @return
	 */
	public static String changeTraSrlNo(int count,String seqno){
		String tmpTrasrlNo = "000000000000000000000000000" + seqno;
		String trasrlNo = tmpTrasrlNo.substring(tmpTrasrlNo.length() - (count - 8) ,tmpTrasrlNo.length());
		
		String currentDate = TimeFacade.getCurrentStringTime(); // 当前系统的时间
		
		return currentDate+trasrlNo;
	}
	
	public static void main(String[] args) {
	}

}

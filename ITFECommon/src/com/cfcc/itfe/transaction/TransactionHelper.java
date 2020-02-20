/**
 * 
 */
package com.cfcc.itfe.transaction;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import com.cfcc.itfe.config.ITFEGlobalBeanId;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * 用来单独提交事务所使用的对象
 * @author sjz
 *
 */
public class TransactionHelper {
	
	/**
	 * 在新建事务中执行传入的方法<br>
	 * 在方法执行结束时会自动将传入方法中所作的操作提交<br>
	 * 由于方法的执行会新建一个事务，所以并不会对已有事务产生影响<br>
	 * 待执行的方法需要实现org.springframework.transaction.support.TransactionCallback接口
	 * <pre>
	 * Class TransactionCallbackClass imeplements TransactionCallback{
	 * 		public Object doInTransaction(TransactionStatus status) {
	 *			try{
	 *				//准备在新建事务中进行的操作
	 * 			}catch(Exception e){
	 *				status.setRollbackOnly();//设置回滚
	 *			}
	 *		}
	 * }
	 * @param callback 准备要在新事务中执行的方法
	 * @return
	 */
	public static Object execFuncInNewTransaction(TransactionCallback callback){
		TransactionTemplate transactionTemplate = (TransactionTemplate)ContextFactory.getApplicationContext().getBean(ITFEGlobalBeanId.NEW_TRANSACTION_TEMPLATE);
		return transactionTemplate.execute(callback);
	}

	/**
	 * 在已有事务（如果事务不存在会新建）中执行传入的方法<br>
	 * 在方法执行结束时会自动将传入方法中所作的操作提交<br>
	 * 由于方法的执行时会优先使用已有事务，所以并会对已有事务产生影响，会将所有未提交操作提交<br>
	 * 待执行的方法需要实现org.springframework.transaction.support.TransactionCallback接口
	 * <pre>
	 * Class TransactionCallbackClass imeplements TransactionCallback{
	 * 		public Object doInTransaction(TransactionStatus status) {
	 *			try{
	 *				//准备在新建事务中进行的操作
	 * 			}catch(Exception e){
	 *				status.setRollbackOnly();//设置回滚
	 *			}
	 *		}
	 * }
	 * @param callback 准备要在新事务中执行的方法
	 * @return
	 */
	public static Object execFuncInRequiredTransaction(TransactionCallback callback){
		TransactionTemplate transactionTemplate = (TransactionTemplate)ContextFactory.getApplicationContext().getBean(ITFEGlobalBeanId.REQUEIRED_TRANSACTION_TEMPLATE);
		return transactionTemplate.execute(callback);
	}
	
	/**
	 * 在新建事务中执行传入的SQL语句，SQL语句可以为增加、删除、修改等部返回值的SQL语句<br>
	 * 在本类执行结束时会自动将的操作提交<br>
	 * 因为本系统中会使用到多个数据库，所以在执行的时候需要指定SQL语句所对应的数据库<br>
	 * 由于方法的执行会新建一个事务，所以并不会对已有事务产生影响<br>
	 * 返回值为1时表示执行成功，返回值为-1时表示执行失败<br>
	 * 注意：传入的SQL语句必须为增加、删除、修改等操作的语句<br>
	 * @param dbName SQL语句所对应的数据库名称
	 * @param sql 要执行的增加、删除、修改SQL语句
	 * @return 执行结果，1-成功，-1-失败
	 */
	public static int execSQLInNewTransaction(String dbName, String sql){
		TransactionTemplate transactionTemplate = (TransactionTemplate)ContextFactory.getApplicationContext().getBean(ITFEGlobalBeanId.NEW_TRANSACTION_TEMPLATE);
		Object ret = transactionTemplate.execute(new SQLExecTransactionCallback(dbName, sql));
		return Integer.parseInt(ret.toString());
	}
	
	/**
	 * 在新建事务中执行传入的SQL语句，SQL语句可以为增加、删除、修改等部返回值的SQL语句<br>
	 * 在本类执行结束时会自动将的操作提交<br>
	 * 因为本系统中会使用到多个数据库，所以在执行的时候需要指定SQL语句所对应的数据库<br>
	 * 由于方法的执行会新建一个事务，所以并不会对已有事务产生影响<br>
	 * 返回值为长度为0的字符串时表示执行成功，返回值为长度大于0的字符串时表示执行失败，该字符串记录失败的原因<br>
	 * 注意：传入的SQL语句必须为增加、删除、修改等操作的语句<br>
	 * @param dbName SQL语句所对应的数据库名称
	 * @param sql 要执行的增加、删除、修改SQL语句
	 * @param params SQL语句的参数
	 * @return 执行结果，长度为0的字符串为成功；长度大于0的字符串为失败，字符串记录失败原因
	 */
	public static String execSQLInNewTransaction(String dbName, String sql, List<Object> params){
		TransactionTemplate transactionTemplate = (TransactionTemplate)ContextFactory.getApplicationContext().getBean(ITFEGlobalBeanId.NEW_TRANSACTION_TEMPLATE);
		Object ret = transactionTemplate.execute(new SQLExecWithParamsTransactionCallback(dbName, sql,params));
		return ret.toString();
	}
	
	/**
	 * 在已有事务（如果事务不存在会新建）中执行传入的SQL语句，SQL语句可以为增加、删除、修改等部返回值的SQL语句<br>
	 * 在本类执行结束时会自动将的操作提交<br>
	 * 因为本系统中会使用到多个数据库，所以在执行的时候需要指定SQL语句所对应的数据库<br>
	 * 由于方法的执行时会优先使用已有事务，所以并会对已有事务产生影响，会将所有未提交操作提交<br>
	 * 返回值为1时表示执行成功，返回值为-1时表示执行失败<br>
	 * 注意：传入的SQL语句必须为增加、删除、修改等操作的语句<br>
	 * @param dbName SQL语句所对应的数据库名称
	 * @param sql 要执行的增加、删除、修改SQL语句
	 * @return 执行结果，1-成功，-1-失败
	 */
	public static int execSQLInRequiredTransaction(String dbName, String sql){
		TransactionTemplate transactionTemplate = (TransactionTemplate)ContextFactory.getApplicationContext().getBean(ITFEGlobalBeanId.REQUEIRED_TRANSACTION_TEMPLATE);
		Object ret = transactionTemplate.execute(new SQLExecTransactionCallback(dbName,sql));
		return Integer.parseInt(ret.toString());
	}
	
	/**
	 * 在新建事务中将Dto中的内容新增到数据库中<br>
	 * 在本类执行结束时会自动将的操作提交<br>
	 * 本方法对应的数据库为AcsTADB<br>
	 * 由于方法的执行会新建一个事务，所以并不会对已有事务产生影响<br>
	 * 返回值为长度为0的字符串时表示执行成功，返回值为长度大于0的字符串时表示执行失败，该字符串记录失败的原因<br>
	 * 注意：传入的对象必须继承IDto接口<br>
	 * @param dto 要新增的Dto对象
	 * @return 执行结果，长度为0的字符串为成功；长度大于0的字符串为失败，字符串记录失败原因
	 */
	public static String dtoInsertInNewTransaction(String dbName, IDto dto){
		TransactionTemplate transactionTemplate = (TransactionTemplate)ContextFactory.getApplicationContext().getBean(ITFEGlobalBeanId.NEW_TRANSACTION_TEMPLATE);
		Object ret = transactionTemplate.execute(new DtoInsertTransactionCallback(dbName,dto));
		return ret.toString();
	}
	
	/**
	 * 在新建事务中将Dto中的内容更新到数据库中<br>
	 * 在本类执行结束时会自动将的操作提交<br>
	 * 本方法对应的数据库为AcsTADB<br>
	 * 由于方法的执行会新建一个事务，所以并不会对已有事务产生影响<br>
	 * 返回值为长度为0的字符串时表示执行成功，返回值为长度大于0的字符串时表示执行失败，该字符串记录失败的原因<br>
	 * 注意：传入的对象必须继承IDto接口<br>
	 * @param dto 要新增的Dto对象
	 * @return 执行结果，长度为0的字符串为成功；长度大于0的字符串为失败，字符串记录失败原因
	 */
	public static String dtoUpdateInNewTransaction(String dbName, IDto dto){
		TransactionTemplate transactionTemplate = (TransactionTemplate)ContextFactory.getApplicationContext().getBean(ITFEGlobalBeanId.NEW_TRANSACTION_TEMPLATE);
		Object ret = transactionTemplate.execute(new DtoUpdateTransactionCallback(dbName,dto));
		return ret.toString();
	}
	
}

/**
 * 在新建事务中执行传入的SQL语句，SQL语句可以为增加、删除、修改等部返回值的SQL语句<br>
 * 在本类执行结束时会自动将的操作提交<br>
 * 因为本系统中会使用到多个数据库，所以在执行的时候需要指定SQL语句所对应的数据库<br>
 * 由于方法的执行会新建一个事务，所以并不会对已有事务产生影响<br>
 * 返回值为1时表示执行成功，返回值为-1时表示执行失败<br>
 * 注意：传入的SQL语句必须为增加、删除、修改等操作的语句<br>
 */
class SQLExecTransactionCallback implements TransactionCallback{
	private static Log log = LogFactory.getLog(SQLExecTransactionCallback.class);
	private String dbName;
	private String sql;
	
	SQLExecTransactionCallback(String dbName, String sql){
		this.dbName = dbName;
		this.sql = sql;
	}

	public Object doInTransaction(TransactionStatus status) {
		try {
			if (log.isDebugEnabled()){
				log.debug("Execute sql: " + sql);
			}
			DatabaseFacade.getDb(dbName).execSql(sql);
			return 1;
		} catch (Exception e) {
			status.setRollbackOnly();
			log.error("Execute sql in new transaction error",e);
			return -1;
		}
	}
	
}

/**
 * 在新建事务中执行传入的SQL语句，SQL语句可以为增加、删除、修改等部返回值的SQL语句<br>
 * 在本类执行结束时会自动将的操作提交<br>
 * 因为本系统中会使用到多个数据库，所以在执行的时候需要指定SQL语句所对应的数据库<br>
 * 由于方法的执行会新建一个事务，所以并不会对已有事务产生影响<br>
 * 返回值为长度为0的字符串时表示执行成功，返回值为长度大于0的字符串时表示执行失败，该字符串记录失败的原因<br>
 * 注意：传入的SQL语句必须为增加、删除、修改等操作的语句<br>
 */
class SQLExecWithParamsTransactionCallback implements TransactionCallback{
	private static Log log = LogFactory.getLog(SQLExecTransactionCallback.class);
	private String dbName;
	private String sql;
	private List<Object> params;
	
	SQLExecWithParamsTransactionCallback(String dbName, String sql, List<Object> params){
		this.dbName = dbName;
		this.sql = sql;
		this.params = params;
	}

	public Object doInTransaction(TransactionStatus status) {
		try {
			if (log.isDebugEnabled()){
				log.debug("Execute sql: " + sql);
			}
			SQLExecutor exec = DatabaseFacade.getDb(dbName).getSqlExecutorFactory().getSQLExecutor();
			if ((null != params) && (params.size() > 0)){
				for (int i=0; i<params.size(); i++){
					exec.addParam(params.get(i));
				}
			}
			exec.runQueryCloseCon(sql);
			return "";
		} catch (Exception e) {
			status.setRollbackOnly();
			log.error("Execute sql with params in new transaction error",e);
			String error = e.getMessage();
			if ((null == error) || (error.trim().length() == 0)){
				error = "Execute sql with params in new transaction error";
			}
			return error;
		}
	}
}

/**
 * 在新建事务中将Dto中的内容新增到数据库中<br>
 * 在本类执行结束时会自动将的操作提交<br>
 * 本方法对应的数据库为AcsTADB<br>
 * 由于方法的执行会新建一个事务，所以并不会对已有事务产生影响<br>
 * 返回值为长度为0的字符串时表示执行成功，返回值为长度大于0的字符串时表示执行失败，该字符串记录失败的原因<br>
 * 注意：传入的对象必须继承IDto接口<br>
 */
class DtoInsertTransactionCallback implements TransactionCallback{
	private static Log log = LogFactory.getLog(SQLExecTransactionCallback.class);
	private String dbName;
	private IDto dto;
	
	DtoInsertTransactionCallback(String dbName, IDto dto){
		this.dbName = dbName;
		this.dto = dto;
	}

	public Object doInTransaction(TransactionStatus status) {
		try {
			//新增一条记录
			DatabaseFacade.getDb(dbName).create(dto);
			return "";
		} catch (Exception e) {
			status.setRollbackOnly();
			log.error("Insert row in new transaction error",e);
			String error = e.getMessage();
			if ((null == error) || (error.trim().length() == 0)){
				error = "Insert row in new transaction error";
			}
			return error;
		}
	}
}

/**
 * 在新建事务中将Dto中的内容更新到数据库中<br>
 * 在本类执行结束时会自动将的操作提交<br>
 * 本方法对应的数据库为AcsTADB<br>
 * 由于方法的执行会新建一个事务，所以并不会对已有事务产生影响<br>
 * 返回值为长度为0的字符串时表示执行成功，返回值为长度大于0的字符串时表示执行失败，该字符串记录失败的原因<br>
 * 注意：传入的对象必须继承IDto接口<br>
 */
class DtoUpdateTransactionCallback implements TransactionCallback{
	private static Log log = LogFactory.getLog(SQLExecTransactionCallback.class);
	private String dbName;
	private IDto dto;
	
	DtoUpdateTransactionCallback(String dbName, IDto dto){
		this.dbName = dbName;
		this.dto = dto;
	}

	public Object doInTransaction(TransactionStatus status) {
		try {
			//新增一条记录
			DatabaseFacade.getDb(dbName).update(dto);
			return "";
		} catch (Exception e) {
			status.setRollbackOnly();
			log.error("Update row in new transaction error",e);
			String error = e.getMessage();
			if ((null == error) || (error.trim().length() == 0)){
				error = "Update row in new transaction error";
			}
			return error;
		}
	}
}

/***************************************************************************************************
 * 用于控制子事务的内部类的例子
 **************************************************************************************************/
//class ChangeStateTransactionCallback implements TransactionCallback {
//	/**
//	 * Logger for this class
//	 */
//	private static final Log logger = LogFactory.getLog(ChangeStateTransactionCallback.class);
//
//	private Class dtoClass;
//
//	private String traState;
//
//	private Date acctDate;
//
//	private String dealCode;
//
//	private String S_TRASRLNO;
//
//	public Object doInTransaction(TransactionStatus status) {
//		try {
//
//			String tabName = OrmapFactory.getTableName(dtoClass.getName());
//			String sql = "update " + tabName
//					+ " set s_TraState=?, d_acct=?,S_STATEDEALCODE=? WHERE S_TRASRLNO=? ";
//
//			SQLExecutor sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
//			sqlExec.addParam(traState);
//			sqlExec.addParam(acctDate);
//			sqlExec.addParam(dealCode);
//			sqlExec.addParam(S_TRASRLNO);
//			
//			sqlExec.runQueryCloseCon(sql);
//
//		} catch (Throwable e) {
//			status.setRollbackOnly();
//			logger.error(e);
//		}
//		return status;
//	}
//
//	public Class getDtoClass() {
//		return dtoClass;
//	}
//
//	public void setDtoClass(Class dtoClass) {
//		this.dtoClass = dtoClass;
//	}
//
//	public Date getAcctDate() {
//		return acctDate;
//	}
//
//	public void setAcctDate(Date acctDate) {
//		this.acctDate = acctDate;
//	}
//
//	public String getDealCode() {
//		return dealCode;
//	}
//
//	public void setDealCode(String dealCode) {
//		this.dealCode = dealCode;
//	}
//
//	public String getS_TRASRLNO() {
//		return S_TRASRLNO;
//	}
//
//	public void setS_TRASRLNO(String s_trasrlno) {
//		S_TRASRLNO = s_trasrlno;
//	}
//
//	public String getTraState() {
//		return traState;
//	}
//
//	public void setTraState(String traState) {
//		this.traState = traState;
//	}
//
//}

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
 * ���������ύ������ʹ�õĶ���
 * @author sjz
 *
 */
public class TransactionHelper {
	
	/**
	 * ���½�������ִ�д���ķ���<br>
	 * �ڷ���ִ�н���ʱ���Զ������뷽���������Ĳ����ύ<br>
	 * ���ڷ�����ִ�л��½�һ���������Բ�����������������Ӱ��<br>
	 * ��ִ�еķ�����Ҫʵ��org.springframework.transaction.support.TransactionCallback�ӿ�
	 * <pre>
	 * Class TransactionCallbackClass imeplements TransactionCallback{
	 * 		public Object doInTransaction(TransactionStatus status) {
	 *			try{
	 *				//׼�����½������н��еĲ���
	 * 			}catch(Exception e){
	 *				status.setRollbackOnly();//���ûع�
	 *			}
	 *		}
	 * }
	 * @param callback ׼��Ҫ����������ִ�еķ���
	 * @return
	 */
	public static Object execFuncInNewTransaction(TransactionCallback callback){
		TransactionTemplate transactionTemplate = (TransactionTemplate)ContextFactory.getApplicationContext().getBean(ITFEGlobalBeanId.NEW_TRANSACTION_TEMPLATE);
		return transactionTemplate.execute(callback);
	}

	/**
	 * ����������������񲻴��ڻ��½�����ִ�д���ķ���<br>
	 * �ڷ���ִ�н���ʱ���Զ������뷽���������Ĳ����ύ<br>
	 * ���ڷ�����ִ��ʱ������ʹ�������������Բ���������������Ӱ�죬�Ὣ����δ�ύ�����ύ<br>
	 * ��ִ�еķ�����Ҫʵ��org.springframework.transaction.support.TransactionCallback�ӿ�
	 * <pre>
	 * Class TransactionCallbackClass imeplements TransactionCallback{
	 * 		public Object doInTransaction(TransactionStatus status) {
	 *			try{
	 *				//׼�����½������н��еĲ���
	 * 			}catch(Exception e){
	 *				status.setRollbackOnly();//���ûع�
	 *			}
	 *		}
	 * }
	 * @param callback ׼��Ҫ����������ִ�еķ���
	 * @return
	 */
	public static Object execFuncInRequiredTransaction(TransactionCallback callback){
		TransactionTemplate transactionTemplate = (TransactionTemplate)ContextFactory.getApplicationContext().getBean(ITFEGlobalBeanId.REQUEIRED_TRANSACTION_TEMPLATE);
		return transactionTemplate.execute(callback);
	}
	
	/**
	 * ���½�������ִ�д����SQL��䣬SQL������Ϊ���ӡ�ɾ�����޸ĵȲ�����ֵ��SQL���<br>
	 * �ڱ���ִ�н���ʱ���Զ����Ĳ����ύ<br>
	 * ��Ϊ��ϵͳ�л�ʹ�õ�������ݿ⣬������ִ�е�ʱ����Ҫָ��SQL�������Ӧ�����ݿ�<br>
	 * ���ڷ�����ִ�л��½�һ���������Բ�����������������Ӱ��<br>
	 * ����ֵΪ1ʱ��ʾִ�гɹ�������ֵΪ-1ʱ��ʾִ��ʧ��<br>
	 * ע�⣺�����SQL������Ϊ���ӡ�ɾ�����޸ĵȲ��������<br>
	 * @param dbName SQL�������Ӧ�����ݿ�����
	 * @param sql Ҫִ�е����ӡ�ɾ�����޸�SQL���
	 * @return ִ�н����1-�ɹ���-1-ʧ��
	 */
	public static int execSQLInNewTransaction(String dbName, String sql){
		TransactionTemplate transactionTemplate = (TransactionTemplate)ContextFactory.getApplicationContext().getBean(ITFEGlobalBeanId.NEW_TRANSACTION_TEMPLATE);
		Object ret = transactionTemplate.execute(new SQLExecTransactionCallback(dbName, sql));
		return Integer.parseInt(ret.toString());
	}
	
	/**
	 * ���½�������ִ�д����SQL��䣬SQL������Ϊ���ӡ�ɾ�����޸ĵȲ�����ֵ��SQL���<br>
	 * �ڱ���ִ�н���ʱ���Զ����Ĳ����ύ<br>
	 * ��Ϊ��ϵͳ�л�ʹ�õ�������ݿ⣬������ִ�е�ʱ����Ҫָ��SQL�������Ӧ�����ݿ�<br>
	 * ���ڷ�����ִ�л��½�һ���������Բ�����������������Ӱ��<br>
	 * ����ֵΪ����Ϊ0���ַ���ʱ��ʾִ�гɹ�������ֵΪ���ȴ���0���ַ���ʱ��ʾִ��ʧ�ܣ����ַ�����¼ʧ�ܵ�ԭ��<br>
	 * ע�⣺�����SQL������Ϊ���ӡ�ɾ�����޸ĵȲ��������<br>
	 * @param dbName SQL�������Ӧ�����ݿ�����
	 * @param sql Ҫִ�е����ӡ�ɾ�����޸�SQL���
	 * @param params SQL���Ĳ���
	 * @return ִ�н��������Ϊ0���ַ���Ϊ�ɹ������ȴ���0���ַ���Ϊʧ�ܣ��ַ�����¼ʧ��ԭ��
	 */
	public static String execSQLInNewTransaction(String dbName, String sql, List<Object> params){
		TransactionTemplate transactionTemplate = (TransactionTemplate)ContextFactory.getApplicationContext().getBean(ITFEGlobalBeanId.NEW_TRANSACTION_TEMPLATE);
		Object ret = transactionTemplate.execute(new SQLExecWithParamsTransactionCallback(dbName, sql,params));
		return ret.toString();
	}
	
	/**
	 * ����������������񲻴��ڻ��½�����ִ�д����SQL��䣬SQL������Ϊ���ӡ�ɾ�����޸ĵȲ�����ֵ��SQL���<br>
	 * �ڱ���ִ�н���ʱ���Զ����Ĳ����ύ<br>
	 * ��Ϊ��ϵͳ�л�ʹ�õ�������ݿ⣬������ִ�е�ʱ����Ҫָ��SQL�������Ӧ�����ݿ�<br>
	 * ���ڷ�����ִ��ʱ������ʹ�������������Բ���������������Ӱ�죬�Ὣ����δ�ύ�����ύ<br>
	 * ����ֵΪ1ʱ��ʾִ�гɹ�������ֵΪ-1ʱ��ʾִ��ʧ��<br>
	 * ע�⣺�����SQL������Ϊ���ӡ�ɾ�����޸ĵȲ��������<br>
	 * @param dbName SQL�������Ӧ�����ݿ�����
	 * @param sql Ҫִ�е����ӡ�ɾ�����޸�SQL���
	 * @return ִ�н����1-�ɹ���-1-ʧ��
	 */
	public static int execSQLInRequiredTransaction(String dbName, String sql){
		TransactionTemplate transactionTemplate = (TransactionTemplate)ContextFactory.getApplicationContext().getBean(ITFEGlobalBeanId.REQUEIRED_TRANSACTION_TEMPLATE);
		Object ret = transactionTemplate.execute(new SQLExecTransactionCallback(dbName,sql));
		return Integer.parseInt(ret.toString());
	}
	
	/**
	 * ���½������н�Dto�е��������������ݿ���<br>
	 * �ڱ���ִ�н���ʱ���Զ����Ĳ����ύ<br>
	 * ��������Ӧ�����ݿ�ΪAcsTADB<br>
	 * ���ڷ�����ִ�л��½�һ���������Բ�����������������Ӱ��<br>
	 * ����ֵΪ����Ϊ0���ַ���ʱ��ʾִ�гɹ�������ֵΪ���ȴ���0���ַ���ʱ��ʾִ��ʧ�ܣ����ַ�����¼ʧ�ܵ�ԭ��<br>
	 * ע�⣺����Ķ������̳�IDto�ӿ�<br>
	 * @param dto Ҫ������Dto����
	 * @return ִ�н��������Ϊ0���ַ���Ϊ�ɹ������ȴ���0���ַ���Ϊʧ�ܣ��ַ�����¼ʧ��ԭ��
	 */
	public static String dtoInsertInNewTransaction(String dbName, IDto dto){
		TransactionTemplate transactionTemplate = (TransactionTemplate)ContextFactory.getApplicationContext().getBean(ITFEGlobalBeanId.NEW_TRANSACTION_TEMPLATE);
		Object ret = transactionTemplate.execute(new DtoInsertTransactionCallback(dbName,dto));
		return ret.toString();
	}
	
	/**
	 * ���½������н�Dto�е����ݸ��µ����ݿ���<br>
	 * �ڱ���ִ�н���ʱ���Զ����Ĳ����ύ<br>
	 * ��������Ӧ�����ݿ�ΪAcsTADB<br>
	 * ���ڷ�����ִ�л��½�һ���������Բ�����������������Ӱ��<br>
	 * ����ֵΪ����Ϊ0���ַ���ʱ��ʾִ�гɹ�������ֵΪ���ȴ���0���ַ���ʱ��ʾִ��ʧ�ܣ����ַ�����¼ʧ�ܵ�ԭ��<br>
	 * ע�⣺����Ķ������̳�IDto�ӿ�<br>
	 * @param dto Ҫ������Dto����
	 * @return ִ�н��������Ϊ0���ַ���Ϊ�ɹ������ȴ���0���ַ���Ϊʧ�ܣ��ַ�����¼ʧ��ԭ��
	 */
	public static String dtoUpdateInNewTransaction(String dbName, IDto dto){
		TransactionTemplate transactionTemplate = (TransactionTemplate)ContextFactory.getApplicationContext().getBean(ITFEGlobalBeanId.NEW_TRANSACTION_TEMPLATE);
		Object ret = transactionTemplate.execute(new DtoUpdateTransactionCallback(dbName,dto));
		return ret.toString();
	}
	
}

/**
 * ���½�������ִ�д����SQL��䣬SQL������Ϊ���ӡ�ɾ�����޸ĵȲ�����ֵ��SQL���<br>
 * �ڱ���ִ�н���ʱ���Զ����Ĳ����ύ<br>
 * ��Ϊ��ϵͳ�л�ʹ�õ�������ݿ⣬������ִ�е�ʱ����Ҫָ��SQL�������Ӧ�����ݿ�<br>
 * ���ڷ�����ִ�л��½�һ���������Բ�����������������Ӱ��<br>
 * ����ֵΪ1ʱ��ʾִ�гɹ�������ֵΪ-1ʱ��ʾִ��ʧ��<br>
 * ע�⣺�����SQL������Ϊ���ӡ�ɾ�����޸ĵȲ��������<br>
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
 * ���½�������ִ�д����SQL��䣬SQL������Ϊ���ӡ�ɾ�����޸ĵȲ�����ֵ��SQL���<br>
 * �ڱ���ִ�н���ʱ���Զ����Ĳ����ύ<br>
 * ��Ϊ��ϵͳ�л�ʹ�õ�������ݿ⣬������ִ�е�ʱ����Ҫָ��SQL�������Ӧ�����ݿ�<br>
 * ���ڷ�����ִ�л��½�һ���������Բ�����������������Ӱ��<br>
 * ����ֵΪ����Ϊ0���ַ���ʱ��ʾִ�гɹ�������ֵΪ���ȴ���0���ַ���ʱ��ʾִ��ʧ�ܣ����ַ�����¼ʧ�ܵ�ԭ��<br>
 * ע�⣺�����SQL������Ϊ���ӡ�ɾ�����޸ĵȲ��������<br>
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
 * ���½������н�Dto�е��������������ݿ���<br>
 * �ڱ���ִ�н���ʱ���Զ����Ĳ����ύ<br>
 * ��������Ӧ�����ݿ�ΪAcsTADB<br>
 * ���ڷ�����ִ�л��½�һ���������Բ�����������������Ӱ��<br>
 * ����ֵΪ����Ϊ0���ַ���ʱ��ʾִ�гɹ�������ֵΪ���ȴ���0���ַ���ʱ��ʾִ��ʧ�ܣ����ַ�����¼ʧ�ܵ�ԭ��<br>
 * ע�⣺����Ķ������̳�IDto�ӿ�<br>
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
			//����һ����¼
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
 * ���½������н�Dto�е����ݸ��µ����ݿ���<br>
 * �ڱ���ִ�н���ʱ���Զ����Ĳ����ύ<br>
 * ��������Ӧ�����ݿ�ΪAcsTADB<br>
 * ���ڷ�����ִ�л��½�һ���������Բ�����������������Ӱ��<br>
 * ����ֵΪ����Ϊ0���ַ���ʱ��ʾִ�гɹ�������ֵΪ���ȴ���0���ַ���ʱ��ʾִ��ʧ�ܣ����ַ�����¼ʧ�ܵ�ԭ��<br>
 * ע�⣺����Ķ������̳�IDto�ӿ�<br>
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
			//����һ����¼
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
 * ���ڿ�����������ڲ��������
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

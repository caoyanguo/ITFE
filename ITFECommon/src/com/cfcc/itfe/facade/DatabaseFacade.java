package com.cfcc.itfe.facade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFEGlobalBeanId;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.BatchRetriever;
import com.cfcc.jaf.persistence.jaform.IOrmTemplate;
import com.cfcc.jaf.persistence.jaform.config.OrmapFactory;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutorFactory;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.CommonQto;
import com.cfcc.jaf.persistence.util.SqlUtil;

/**
 * 
 * ͨ�õ�facade
 * 
 * @author ������
 * 
 */
public class DatabaseFacade {
	private static Log log = LogFactory.getLog(DatabaseFacade.class);

	/**
	 * SQL����ת����Java����
	 */

	public static DatabaseFacade getDb(String dbName) {
		return (DatabaseFacade) ContextFactory.getApplicationContext().getBean(
				ITFEGlobalBeanId.DATABASE_FACADE_DB_PREFIX + dbName);
	}
	
	public static DatabaseFacade getDb() {
		return (DatabaseFacade) ContextFactory.getApplicationContext().getBean(
				ITFEGlobalBeanId.DATABASE_FACADE_DB_PREFIX + "ITFEDB");
	}

	public static DatabaseFacade getODB() {

		return (DatabaseFacade) ContextFactory.getApplicationContext().getBean(
				ITFEGlobalBeanId.DATABASE_FACADE_DB_PREFIX + "ITFEDB");

	}

	public static DatabaseFacade getQDB() {
		return (DatabaseFacade) ContextFactory.getApplicationContext().getBean(
				ITFEGlobalBeanId.DATABASE_FACADE_DB_PREFIX + "QDB");

	}

	public static DatabaseFacade getBatchQDB() {
		return (DatabaseFacade) ContextFactory.getApplicationContext().getBean(
				ITFEGlobalBeanId.DATABASE_FACADE_DB_PREFIX + "QDB");

	}

	public static DatabaseFacade getBatchODB() {
		return (DatabaseFacade) ContextFactory.getApplicationContext().getBean(
				ITFEGlobalBeanId.DATABASE_FACADE_DB_PREFIX + "ODB");

	}

	IOrmTemplate ormTemplate;

	SQLExecutorFactory sqlExecutorFactory;

	/**
	 * 
	 * ���ݿ�����һ����¼
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void create(IDto _dto) throws JAFDatabaseException {
		ormTemplate.create(_dto);
	}

	/**
	 * ���ݿ�����һ����¼,�������ӵĽ��
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto createWithResult(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.createWithResult(_dto);

	}

	/**
	 * ���ݿ�����һ����¼,�����ش��������е�һ�������ļ�¼
	 * 
	 * @param _dto
	 * @return IDto
	 * @throws JAFDatabaseException
	 */
	public IDto createForGenerateKeyOnly(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.createWithResultForGeneratedKeyOnly(_dto);
	}

	/**
	 * ͨ������ɾ�����ݿ��е�һ����¼
	 * 
	 * @param _pk
	 * @throws JAFDatabaseException
	 */
	public void delete(IPK _pk) throws JAFDatabaseException {
		ormTemplate.delete(_pk);

	}

	/**
	 * ͨ����Ҫɾ���Ķ���ɾ�����ݿ��е�һ����¼
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void delete(IDto _dto) throws JAFDatabaseException {
		ormTemplate.delete(_dto);

	}

	/**
	 * ������״̬ȷ��ɾ�����ݿ��е�һ����¼
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void deleteWithCheck(IDto _dto) throws JAFDatabaseException {
		ormTemplate.deleteWithCheck(_dto);
	}

	/**
	 * ������״̬ȷ��ɾ�����ݿ��е�һ���¼
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void deleteWithCheck(IDto[] _dtos) throws JAFDatabaseException {
		ormTemplate.deleteWithCheck(_dtos);
	}

	/**
	 * ������״̬���м���ɾ�����ݿ��еļ�¼�������ڸ��ӱ�Ľṹ��
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void deleteCascadeWithCheck(IDto _dto) throws JAFDatabaseException {
		ormTemplate.deleteCascadeWithCheck(_dto);

	}

	/**
	 * ͨ�������е��������м���ɾ�����ݿ��еļ�¼�������ڸ��ӱ�Ľṹ����������״̬
	 * 
	 * @param _pk
	 * @throws JAFDatabaseException
	 */
	public void deleteCascade(IPK _pk) throws JAFDatabaseException {
		ormTemplate.deleteCascade(_pk);

	}

	/**
	 * ͨ����Ҫɾ�������ݽ��м���ɾ�����ݿ��еļ�¼�������ڸ��ӱ�Ľṹ��,��������״̬
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void deleteCascade(IDto _dto) throws JAFDatabaseException {
		ormTemplate.deleteCascade(_dto);

	}

	/**
	 * ͨ����������һ����¼
	 * 
	 * @param _pk
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto find(IPK _pk) throws JAFDatabaseException {
		return ormTemplate.find(_pk);

	}

	/**
	 * ͨ�����ݲ���һ����¼
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto find(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.find(_dto);

	}

	/**
	 * ͨ�������е��������м����������ݿ��еļ�¼�������ڸ��ӱ�Ľṹ��
	 * 
	 * @param _pk
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto findCascade(IPK _pk) throws JAFDatabaseException {
		return ormTemplate.findCascade(_pk);

	}

	/**
	 * ͨ�������е����ݽ��м����������ݿ��еļ�¼�������ڸ��ӱ�Ľṹ��
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto findCascade(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.findCascade(_dto);

	}

	/**
	 * �������ݿ��е�һ����¼
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void update(IDto _dto) throws JAFDatabaseException {
		ormTemplate.update(_dto);

	}

	/**
	 * �������ݿ��е�һ����¼
	 * 
	 * @param _dtos
	 * @throws JAFDatabaseException
	 */
	public void update(IDto[] _dtos) throws JAFDatabaseException {
		if (_dtos != null && _dtos.length > 0) {
			ormTemplate.update(_dtos);
		}

	}

	/**
	 * �������ݿ��е�һ����¼,���ظ��½��
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto updateWithResult(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.updateWithResult(_dto);

	}

	/**
	 * �������ݿ��е�һ����¼,���ظ��½��
	 * 
	 * @param _dtos
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto[] updateWithResult(IDto[] _dtos) throws JAFDatabaseException {
		if (_dtos != null && _dtos.length > 0) {
			return ormTemplate.updateWithResult(_dtos);
		}
		return null;

	}

	/**
	 * ͨ��������״̬���и������ݿ��е�һ����¼
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void updateWithCheck(IDto _dto) throws JAFDatabaseException {
		ormTemplate.updateWithCheck(_dto);

	}

	/**
	 * ͨ��������״̬���и������ݿ��е�һ����¼
	 * 
	 * @param _dtos
	 * @throws JAFDatabaseException
	 */
	public void updateWithCheck(IDto[] _dtos) throws JAFDatabaseException {
		if (_dtos != null && _dtos.length > 0) {
			ormTemplate.updateWithCheck(_dtos);
		}

	}

	/**
	 * ͨ��������״̬���и������ݿ��е�һ����¼�����ظ��½��
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto updateWithResultCheck(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.updateWithResultCheck(_dto);

	}

	/**
	 * ͨ��������״̬���и������ݿ��е�һ����¼�����ظ��½��
	 * 
	 * @param _dtos
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto[] updateWithResultCheck(IDto[] _dtos)
			throws JAFDatabaseException {
		if (_dtos != null && _dtos.length > 0) {
			return ormTemplate.updateWithResultCheck(_dtos);
		}
		return null;

	}

	/**
	 * �����������ݿ��еļ�¼�������ڸ��ӱ�Ľṹ��
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void updateCascade(IDto _dto) throws JAFDatabaseException {
		ormTemplate.updateCascade(_dto);

	}

	/**
	 * �����������ݿ��е�һ����¼�������ڸ��ӱ�Ľṹ��
	 * 
	 * @param _dtos
	 * @throws JAFDatabaseException
	 */
	public void updateCascade(IDto[] _dtos) throws JAFDatabaseException {
		if (_dtos != null && _dtos.length > 0) {
			ormTemplate.updateCascade(_dtos);
		}

	}

	/**
	 * �����������ݿ��еļ�¼�������ڸ��ӱ�Ľṹ�������ظ��½��
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto updateCascadeWithResult(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.updateCascadeWithResult(_dto);

	}

	/**
	 * �����������ݿ��е�һ����¼�������ڸ��ӱ�Ľṹ�������ظ��½��
	 * 
	 * @param _dtos
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto[] updateCascadeWithResult(IDto[] _dtos)
			throws JAFDatabaseException {

		if (_dtos != null && _dtos.length > 0) {
			return ormTemplate.updateCascadeWithResult(_dtos);
		}
		return null;

	}

	/**
	 * ͨ��������״̬���м����������ݿ��еļ�¼�������ڸ��ӱ�Ľṹ��
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void updateCascadeWithCheck(IDto _dto) throws JAFDatabaseException {
		ormTemplate.updateCascadeWithCheck(_dto);

	}

	/**
	 * ͨ��������״̬���м����������ݿ��е�һ����¼�������ڸ��ӱ�Ľṹ��
	 * 
	 * @param _dtos
	 * @throws JAFDatabaseException
	 */
	public void updateCascadeWithCheck(IDto[] _dtos)
			throws JAFDatabaseException {
		if (_dtos != null && _dtos.length > 0) {
			ormTemplate.updateCascadeWithCheck(_dtos);
		}

	}

	/**
	 * ͨ��������״̬���м����������ݿ��еļ�¼�������ڸ��ӱ�Ľṹ�������ظ��½��
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto updateCascadeWithResultCheck(IDto _dto)
			throws JAFDatabaseException {
		return ormTemplate.updateCascadeWithResultCheck(_dto);

	}

	/**
	 * ͨ��������״̬���м����������ݿ��е�һ����¼�������ڸ��ӱ�Ľṹ�������ظ��½��
	 * 
	 * @param _dtos
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto[] updateCascadeWithResultCheck(IDto[] _dtos)
			throws JAFDatabaseException {

		if (_dtos != null && _dtos.length > 0) {
			return ormTemplate.updateCascadeWithResultCheck(_dtos);

		}

		return null;
	}

	/**
	 * ����SQL��ѯ���ض���ֵ��ֵ��IDto�ķ�ʽ�����Collection��
	 * 
	 * @param sql
	 *            ��ѯ����
	 * @param dtoClass
	 *            ��Ҫ��ѯIDto����
	 * @return
	 * @throws JAFDatabaseException
	 */
	protected Collection findValuesBySQL(String sql, Class dtoClass)
			throws JAFDatabaseException {

		SQLResults res = null;
		SQLExecutor sqlExec = sqlExecutorFactory.getSQLExecutor();

		res = sqlExec.runQueryCloseCon(sql, dtoClass);

		if (res.getDtoCollection() == null
				&& res.getDtoCollection().size() == 0) {
			return null;
		}
		return res.getDtoCollection();

	}

	/**
	 * Retrive batch record from Database. it only find the first layer dto ��ѯ
	 * �������ݣ� ���where �Ӿ�Ϊ�����ѯ���е�����
	 * 
	 * @param _dtoClass
	 * @param where
	 * @param params
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List find(Class _dtoClass, String where, List params)
			throws JAFDatabaseException {
		return ormTemplate.find(_dtoClass, where, params);
	}

	/**
	 * ��ѯ��������,���where �Ӿ�Ϊ�����ѯ���е����� ֧��for update
	 * 
	 * @param _dtoClass
	 * @param where
	 * @param params
	 * @return
	 * @throws JAFDatabaseException
	 */
	// public List findForUpdate(Class _dtoClass, String where, List params)
	// throws JAFDatabaseException {
	// BatchRetriever retriever =
	// DatabaseFacade.getODB().getBatchRetriever(_dtoClass);
	// retriever.setResultSetConcurrency(ResultSet.CONCUR_UPDATABLE);// ֧��for
	// update
	// retriever.runQuery(where, params);
	// return retriever.getResults();
	//
	// }
	/**
	 * Retrive batch record from Database. it only find the first layer dto ��ѯ
	 * �������ݣ� ���where �Ӿ�Ϊ�����ѯ���е����ݣ�ʹ�������ʽ��ѯ
	 * 
	 * @param _dtoClass
	 * @param where
	 * @param params
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List findWithUR(Class _dtoClass, String where, List params)
			throws JAFDatabaseException {
		if (where == null) {
			where = "";
		}
		return ormTemplate.find(_dtoClass, where, params);
	}

	/**
	 * ���ݴ����CommonQto�����ѯ��Ӧ�ļ�¼
	 * 
	 * @param _dtoClass
	 * @param _qto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List find(Class _dtoClass, CommonQto _qto)
			throws JAFDatabaseException {
		if (_qto == null) {
			return ormTemplate.find(_dtoClass, null, null);
		} else {
			return ormTemplate.find(_dtoClass, _qto.getSWhereClause(), _qto
					.getLParams());
		}
	}

	/**
	 * ���ݴ����CommonQto�����ѯ��Ӧ�ļ�¼,ʹ�������ʽ
	 * 
	 * @param _dtoClass
	 * @param _qto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List findWithUR(Class _dtoClass, CommonQto _qto)
			throws JAFDatabaseException {
		if (_qto == null) {
			return ormTemplate.find(_dtoClass, "");
		} else {
			return ormTemplate.find(_dtoClass, _qto.getSWhereClause() + "",
					_qto.getLParams());
		}
	}

	/**
	 * Retrive batch record from Database. it only find the first layer dto ��ѯ
	 * �������ݣ� ���where �Ӿ�Ϊ�����ѯ���е�����
	 * 
	 * @param _dtoClass
	 * @param where
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List find(Class _dtoClass, String where) throws JAFDatabaseException {
		return ormTemplate.find(_dtoClass, where);
	}

	/**
	 * Retrive batch record from Database. it only find the first layer dto ��ѯ
	 * �������ݣ� ���where �Ӿ�Ϊ�����ѯ���е�����,ʹ�������ʽ��ѯ
	 * 
	 * @param _dtoClass
	 * @param where
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List findWithUR(Class _dtoClass, String where)
			throws JAFDatabaseException {
		if (where == null) {
			where = " ";
		}
		return ormTemplate.find(_dtoClass, where);
	}

	/**
	 * Retrive count from Database. ���where�Ӿ�Ϊ���ѯ�����������
	 * 
	 * @param _dtoClass
	 *            dto��
	 * @param where
	 *            ��ѯ����
	 * @param params
	 *            ��ѯ�����Ĳ�������
	 * @return �����ѯ��������������
	 * @throws JAFDatabaseException
	 */
	public int countFind(Class _dtoClass, String where, List params)
			throws JAFDatabaseException {
		if (where == null) {
			where = "";
		}
		return ormTemplate.countFind(_dtoClass, where, params);
	}

	/**
	 * �õ�ĳ����ѯ���ִ�н���ķ��ؼ�¼����
	 * 
	 * @param _dtoClass
	 *            ��ѯ�����Ӧ��DTO����
	 * @param _qto
	 *            ��ѯ��������
	 * @return �����ѯ��������������
	 * @throws JAFDatabaseException
	 */
	public int countFind(Class _dtoClass, CommonQto _qto)
			throws JAFDatabaseException {
		return ormTemplate.countFind(_qto, _dtoClass);
	}

	/**
	 * ����DTO�����ѯ������ҳ���ͻ��˲�ѯʹ�� �˷�ҳ���ܽ������ڵ����ѯ����ѯ�����ڴ˱��Ӧ��dto�� added by Ren Yong,
	 * 2008-03-15
	 * 
	 * @param dto
	 *            ��DTO�Ĳ�ѯ����
	 * @param request
	 *            ��ҳ������Ϣ
	 * @return PageResponse
	 * @throws JAFDatabaseException
	 */
	public PageResponse findByDtoPaging(IDto dto, PageRequest request)
			throws JAFDatabaseException {
		CommonQto qto = SqlUtil.IDto2CommonQto(dto);
		PageResponse response = new PageResponse(request);
		if (response.getTotalCount() == 0) {
			// ��ʼ���ܼ�¼��
			int totalCount = ormTemplate.countFind(qto, dto.getClass());
			response.setTotalCount(totalCount);
		}
		String tableName = OrmapFactory.getTableName(dto.getClass().getName());
		String sql = "select * from " + tableName;
		SQLExecutor sqlExe = getSqlExecutorFactory().getSQLExecutor();
		if (qto != null) {
			sql += qto.getSWhereClause();
			sqlExe.addParam(qto.getLParams());
		}
		SQLResults res = sqlExe.runQueryCloseCon(sql, dto.getClass(), request
				.getStartPosition(), request.getPageSize(), true);
		List list = new ArrayList();
		list.addAll(res.getDtoCollection());
		response.getData().clear();
		response.setData(list);
		return response;
	}

	/**
	 * ��ȡ������ѯ BatchRetriever
	 * 
	 * @param _dtoClass
	 * @return
	 */
	public BatchRetriever getBatchRetriever(Class _dtoClass) {
		return ormTemplate.getBatchRetriever(_dtoClass);
	}

	/**
	 * �����������ݿ�
	 * 
	 * @param dtos
	 * @throws JAFDatabaseException
	 */
	public void create(IDto[] dtos) throws JAFDatabaseException {
		if (dtos != null && dtos.length > 0) {
			ormTemplate.create(dtos);

		}

	}

	/**
	 * ����һ����������һ������
	 * 
	 * @param iPKs
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto[] find(IPK[] iPKs) throws JAFDatabaseException {
		return ormTemplate.find(iPKs);
	}

	/**
	 * ����һ����������һ����������
	 * 
	 * @param iPKs
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto[] findCascade(IPK[] iPKs) throws JAFDatabaseException {
		return ormTemplate.findCascade(iPKs);
	}

	/**
	 * ����һ���������ݲ���һ����������
	 * 
	 * @param iDtos
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto[] findCascade(IDto[] iDtos) throws JAFDatabaseException {
		return ormTemplate.findCascade(iDtos);
	}

	/**
	 * ����һ����������ɾ��һ������
	 * 
	 * @param iPKs
	 * @throws JAFDatabaseException
	 */
	public void deleteCascade(IPK[] iPKs) throws JAFDatabaseException {
		if (iPKs != null && iPKs.length > 0) {
			ormTemplate.deleteCascade(iPKs);

		}

	}

	/**
	 * ����һ��������Ϣ����ɾ��һ������
	 * 
	 * @param iDtos
	 * @throws JAFDatabaseException
	 */
	public void deleteCascade(IDto[] iDtos) throws JAFDatabaseException {
		ormTemplate.deleteCascade(iDtos);
	}

	/**
	 * ����һ������Ϣɾ������
	 * 
	 * @param iDtos
	 * @throws JAFDatabaseException
	 */
	public void delete(IDto[] iDtos) throws JAFDatabaseException {
		ormTemplate.delete(iDtos);
	}

	/**
	 * ����һ��������Ϣɾ������
	 * 
	 * @param iDtos
	 * @throws JAFDatabaseException
	 */
	public void delete(IPK[] iPKs) throws JAFDatabaseException {
		ormTemplate.delete(iPKs);
	}

	/**
	 * ���ݴ����dto�����ɾ��������估�����б�ɾ�����������������ļ�¼��
	 * 
	 * @param _dtoClass
	 * @param _whereClause
	 *            ����Where���������
	 * @param _paramList
	 * @throws JAFDatabaseException
	 */
	public void delete(Class _dtoClass, String _whereClause, List _paramList)
			throws JAFDatabaseException {
		StringBuffer sb_sql = new StringBuffer();
		SQLExecutor sqlExec = sqlExecutorFactory.getSQLExecutor();

		String sTableName = OrmapFactory.getTableName(_dtoClass.getName());
		if ((_whereClause == null) || (_whereClause.trim().length() == 0)) {
			sb_sql.append("delete from ").append(sTableName);
		} else {
			sb_sql.append("delete from ").append(sTableName).append(" where ")
					.append(_whereClause);
		}
		if ((_paramList == null) || (_paramList.size() == 0)) {
			sqlExec.runQueryCloseCon(sb_sql.toString());
		} else {
			Iterator iter = _paramList.iterator();
			while (iter.hasNext()) {
				sqlExec.addParam(iter.next());
			}
			sqlExec.runQueryCloseCon(sb_sql.toString());
		}
	}

	/**
	 * ���ݴ����dto�����Ͳ�ѯ����qto����ɾ����Ӧ�ļ�¼
	 * 
	 * @param _dtoClass
	 * @param _qto
	 * @throws JAFDatabaseException
	 */
	public void delete(Class _dtoClass, CommonQto _qto)
			throws JAFDatabaseException {
		StringBuffer sb_sql = new StringBuffer();
		SQLExecutor sqlExec = sqlExecutorFactory.getSQLExecutor();

		String sTableName = OrmapFactory.getTableName(_dtoClass.getName());
		if (_qto == null) {
			sb_sql.append("delete from ").append(sTableName);
		} else {
			sb_sql.append("delete from ").append(sTableName).append(" where ")
					.append(_qto.getSClause());
		}

		List paramList = _qto.getLParams();
		if (paramList.size() > 0) {
			Iterator iter = paramList.iterator();
			while (iter.hasNext()) {
				sqlExec.addParam(iter.next());
			}
			sqlExec.runQueryCloseCon(sb_sql.toString());
		} else {
			sqlExec.runQueryCloseCon(sb_sql.toString());
		}

	}

	/**
	 * �������ݿ�������Ҹñ����������
	 * 
	 * @param dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List find(Class dto) throws JAFDatabaseException {
		return ormTemplate.find(dto, "");

	}

	/**
	 * ʹ������ķ�ʽ���������ݿ�������Ҹñ����������
	 * 
	 * @param dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List findWithUR(Class dto) throws JAFDatabaseException {
		return ormTemplate.find(dto, "");

	}

	/**
	 * @return ���� ormTemplate
	 */
	public IOrmTemplate getOrmTemplate() {
		return ormTemplate;
	}

	/**
	 * @param ormTemplate
	 *            Ҫ���õ� ormTemplate
	 */
	public void setOrmTemplate(IOrmTemplate ormTemplate) {
		this.ormTemplate = ormTemplate;
	}

	/**
	 * @return ���� sqlExecutorFactory
	 */
	public SQLExecutorFactory getSqlExecutorFactory() {
		// ���Ӷ�oracle���ݿ�֧��
		ConvertUtils.register(new Converter() {

			public Object convert(Class type, Object value) {
				if (value == null) {
					return null;
				}
				if (value instanceof java.sql.Timestamp) {
					return new java.sql.Date(((java.sql.Timestamp) value)
							.getTime());
				} else if (value instanceof String) {
					return java.sql.Date.valueOf(((String) value).substring(0,
							10));
				}
				return value;
			}
		}, java.sql.Date.class);
		return sqlExecutorFactory;
	}

	/**
	 * @param sqlExecutorFactory
	 *            Ҫ���õ� sqlExecutorFactory
	 */
	public void setSqlExecutorFactory(SQLExecutorFactory sqlExecutorFactory) {
		this.sqlExecutorFactory = sqlExecutorFactory;
	}

	/**
	 * ���ҵ�һ������
	 * @param exec
	 * @param sql
	 * @param c
	 * @return
	 * @throws JAFDatabaseException
	 */
	public static IDto findFirstOneDataWithUr(SQLExecutor exec, String sql, Class c)
			throws JAFDatabaseException {
		sql = sql + " fetch first 1 rows only with ur";
		SQLResults res = exec.runQueryCloseCon(sql, c);
		List<IDto> l = (List<IDto>) res.getDtoCollection();
		if (l != null && l.size() > 0) {
			return l.get(0);
		} else {
			return null;
		}

	}
	
	/**
	 * ִ��SQL����Ҫ����ִ�����ӡ�ɾ�����޸����
	 * @param sql �������ӡ�ɾ�����޸ĵ�SQL���
	 * @throws JAFDatabaseException
	 */
	public void execSql(String sql) throws JAFDatabaseException{
		SQLExecutor exec = getSqlExecutorFactory().getSQLExecutor();
		exec.runQueryCloseCon(sql);
	}

}

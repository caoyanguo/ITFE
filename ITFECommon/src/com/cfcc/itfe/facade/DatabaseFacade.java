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
 * 通用的facade
 * 
 * @author 赵新鹏
 * 
 */
public class DatabaseFacade {
	private static Log log = LogFactory.getLog(DatabaseFacade.class);

	/**
	 * SQL类型转换成Java类型
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
	 * 数据库增加一条记录
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void create(IDto _dto) throws JAFDatabaseException {
		ormTemplate.create(_dto);
	}

	/**
	 * 数据库增加一条记录,返回增加的结果
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto createWithResult(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.createWithResult(_dto);

	}

	/**
	 * 数据库增加一条记录,并返回带自增序列的一条完整的记录
	 * 
	 * @param _dto
	 * @return IDto
	 * @throws JAFDatabaseException
	 */
	public IDto createForGenerateKeyOnly(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.createWithResultForGeneratedKeyOnly(_dto);
	}

	/**
	 * 通过主键删除数据库中的一条记录
	 * 
	 * @param _pk
	 * @throws JAFDatabaseException
	 */
	public void delete(IPK _pk) throws JAFDatabaseException {
		ormTemplate.delete(_pk);

	}

	/**
	 * 通过需要删除的对象删除数据库中的一条记录
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void delete(IDto _dto) throws JAFDatabaseException {
		ormTemplate.delete(_dto);

	}

	/**
	 * 检查更新状态确定删除数据库中的一条记录
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void deleteWithCheck(IDto _dto) throws JAFDatabaseException {
		ormTemplate.deleteWithCheck(_dto);
	}

	/**
	 * 检查更新状态确定删除数据库中的一组记录
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void deleteWithCheck(IDto[] _dtos) throws JAFDatabaseException {
		ormTemplate.deleteWithCheck(_dtos);
	}

	/**
	 * 检查更新状态进行及联删除数据库中的记录（适用于父子表的结构）
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void deleteCascadeWithCheck(IDto _dto) throws JAFDatabaseException {
		ormTemplate.deleteCascadeWithCheck(_dto);

	}

	/**
	 * 通过父表中的主键进行及联删除数据库中的记录（适用于父子表的结构）不检查更新状态
	 * 
	 * @param _pk
	 * @throws JAFDatabaseException
	 */
	public void deleteCascade(IPK _pk) throws JAFDatabaseException {
		ormTemplate.deleteCascade(_pk);

	}

	/**
	 * 通过需要删除的数据进行及联删除数据库中的记录（适用于父子表的结构）,不检查更新状态
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void deleteCascade(IDto _dto) throws JAFDatabaseException {
		ormTemplate.deleteCascade(_dto);

	}

	/**
	 * 通过主键查找一条记录
	 * 
	 * @param _pk
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto find(IPK _pk) throws JAFDatabaseException {
		return ormTemplate.find(_pk);

	}

	/**
	 * 通过数据查找一条记录
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto find(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.find(_dto);

	}

	/**
	 * 通过父表中的主键进行及联查找数据库中的记录（适用于父子表的结构）
	 * 
	 * @param _pk
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto findCascade(IPK _pk) throws JAFDatabaseException {
		return ormTemplate.findCascade(_pk);

	}

	/**
	 * 通过父表中的数据进行及联查找数据库中的记录（适用于父子表的结构）
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto findCascade(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.findCascade(_dto);

	}

	/**
	 * 更新数据库中的一条记录
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void update(IDto _dto) throws JAFDatabaseException {
		ormTemplate.update(_dto);

	}

	/**
	 * 更新数据库中的一批记录
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
	 * 更新数据库中的一条记录,返回更新结果
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto updateWithResult(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.updateWithResult(_dto);

	}

	/**
	 * 更新数据库中的一批记录,返回更新结果
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
	 * 通过检查更新状态进行更新数据库中的一条记录
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void updateWithCheck(IDto _dto) throws JAFDatabaseException {
		ormTemplate.updateWithCheck(_dto);

	}

	/**
	 * 通过检查更新状态进行更新数据库中的一批记录
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
	 * 通过检查更新状态进行更新数据库中的一条记录，返回更新结果
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto updateWithResultCheck(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.updateWithResultCheck(_dto);

	}

	/**
	 * 通过检查更新状态进行更新数据库中的一批记录，返回更新结果
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
	 * 及联更新数据库中的记录（适用于父子表的结构）
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void updateCascade(IDto _dto) throws JAFDatabaseException {
		ormTemplate.updateCascade(_dto);

	}

	/**
	 * 及联更新数据库中的一批记录（适用于父子表的结构）
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
	 * 及联更新数据库中的记录（适用于父子表的结构），返回更新结果
	 * 
	 * @param _dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto updateCascadeWithResult(IDto _dto) throws JAFDatabaseException {
		return ormTemplate.updateCascadeWithResult(_dto);

	}

	/**
	 * 及联更新数据库中的一批记录（适用于父子表的结构），返回更新结果
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
	 * 通过检查更新状态进行及联更新数据库中的记录（适用于父子表的结构）
	 * 
	 * @param _dto
	 * @throws JAFDatabaseException
	 */
	public void updateCascadeWithCheck(IDto _dto) throws JAFDatabaseException {
		ormTemplate.updateCascadeWithCheck(_dto);

	}

	/**
	 * 通过检查更新状态进行及联更新数据库中的一批记录（适用于父子表的结构）
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
	 * 通过检查更新状态进行及联更新数据库中的记录（适用于父子表的结构），返回更新结果
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
	 * 通过检查更新状态进行及联更新数据库中的一批记录（适用于父子表的结构），返回更新结果
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
	 * 根据SQL查询返回多条值，值以IDto的方式存放在Collection中
	 * 
	 * @param sql
	 *            查询条件
	 * @param dtoClass
	 *            需要查询IDto对象
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
	 * Retrive batch record from Database. it only find the first layer dto 查询
	 * 批量数据， 如果where 子句为空则查询所有的数据
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
	 * 查询批量数据,如果where 子句为空则查询所有的数据 支持for update
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
	// retriever.setResultSetConcurrency(ResultSet.CONCUR_UPDATABLE);// 支持for
	// update
	// retriever.runQuery(where, params);
	// return retriever.getResults();
	//
	// }
	/**
	 * Retrive batch record from Database. it only find the first layer dto 查询
	 * 批量数据， 如果where 子句为空则查询所有的数据，使用脏读方式查询
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
	 * 根据传入的CommonQto对象查询相应的记录
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
	 * 根据传入的CommonQto对象查询相应的记录,使用脏读方式
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
	 * Retrive batch record from Database. it only find the first layer dto 查询
	 * 批量数据， 如果where 子句为空则查询所有的数据
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
	 * Retrive batch record from Database. it only find the first layer dto 查询
	 * 批量数据， 如果where 子句为空则查询所有的数据,使用脏读方式查询
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
	 * Retrive count from Database. 如果where子句为则查询表的数据总数
	 * 
	 * @param _dtoClass
	 *            dto类
	 * @param where
	 *            查询条件
	 * @param params
	 *            查询条件的参数集合
	 * @return 满足查询条件的数据总数
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
	 * 得到某个查询语句执行结果的返回记录条数
	 * 
	 * @param _dtoClass
	 *            查询结果对应的DTO对象
	 * @param _qto
	 *            查询条件对象
	 * @return 满足查询条件的数据总数
	 * @throws JAFDatabaseException
	 */
	public int countFind(Class _dtoClass, CommonQto _qto)
			throws JAFDatabaseException {
		return ormTemplate.countFind(_qto, _dtoClass);
	}

	/**
	 * 根据DTO对象查询，并分页。客户端查询使用 此分页功能仅适用于单表查询，查询条件在此表对应的dto中 added by Ren Yong,
	 * 2008-03-15
	 * 
	 * @param dto
	 *            表DTO的查询条件
	 * @param request
	 *            分页请求信息
	 * @return PageResponse
	 * @throws JAFDatabaseException
	 */
	public PageResponse findByDtoPaging(IDto dto, PageRequest request)
			throws JAFDatabaseException {
		CommonQto qto = SqlUtil.IDto2CommonQto(dto);
		PageResponse response = new PageResponse(request);
		if (response.getTotalCount() == 0) {
			// 初始化总记录数
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
	 * 获取批量查询 BatchRetriever
	 * 
	 * @param _dtoClass
	 * @return
	 */
	public BatchRetriever getBatchRetriever(Class _dtoClass) {
		return ormTemplate.getBatchRetriever(_dtoClass);
	}

	/**
	 * 批量更新数据库
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
	 * 根据一批主键查找一批数据
	 * 
	 * @param iPKs
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto[] find(IPK[] iPKs) throws JAFDatabaseException {
		return ormTemplate.find(iPKs);
	}

	/**
	 * 根据一批主键查找一批级联数据
	 * 
	 * @param iPKs
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto[] findCascade(IPK[] iPKs) throws JAFDatabaseException {
		return ormTemplate.findCascade(iPKs);
	}

	/**
	 * 根据一批主表数据查找一批级联数据
	 * 
	 * @param iDtos
	 * @return
	 * @throws JAFDatabaseException
	 */
	public IDto[] findCascade(IDto[] iDtos) throws JAFDatabaseException {
		return ormTemplate.findCascade(iDtos);
	}

	/**
	 * 根据一批主键级联删除一批数据
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
	 * 根据一批主表信息级联删除一批数据
	 * 
	 * @param iDtos
	 * @throws JAFDatabaseException
	 */
	public void deleteCascade(IDto[] iDtos) throws JAFDatabaseException {
		ormTemplate.deleteCascade(iDtos);
	}

	/**
	 * 根据一批表信息删除数据
	 * 
	 * @param iDtos
	 * @throws JAFDatabaseException
	 */
	public void delete(IDto[] iDtos) throws JAFDatabaseException {
		ormTemplate.delete(iDtos);
	}

	/**
	 * 根据一批主键信息删除数据
	 * 
	 * @param iDtos
	 * @throws JAFDatabaseException
	 */
	public void delete(IPK[] iPKs) throws JAFDatabaseException {
		ormTemplate.delete(iPKs);
	}

	/**
	 * 根据传入的dto类对象，删除条件语句及参数列表删除单表中满足条件的记录集
	 * 
	 * @param _dtoClass
	 * @param _whereClause
	 *            不含Where的条件语句
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
	 * 根据传入的dto类对象和查询条件qto对象删除相应的记录
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
	 * 根据数据库表名查找该标的所有数据
	 * 
	 * @param dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List find(Class dto) throws JAFDatabaseException {
		return ormTemplate.find(dto, "");

	}

	/**
	 * 使用脏读的方式，根据数据库表名查找该标的所有数据
	 * 
	 * @param dto
	 * @return
	 * @throws JAFDatabaseException
	 */
	public List findWithUR(Class dto) throws JAFDatabaseException {
		return ormTemplate.find(dto, "");

	}

	/**
	 * @return 返回 ormTemplate
	 */
	public IOrmTemplate getOrmTemplate() {
		return ormTemplate;
	}

	/**
	 * @param ormTemplate
	 *            要设置的 ormTemplate
	 */
	public void setOrmTemplate(IOrmTemplate ormTemplate) {
		this.ormTemplate = ormTemplate;
	}

	/**
	 * @return 返回 sqlExecutorFactory
	 */
	public SQLExecutorFactory getSqlExecutorFactory() {
		// 增加对oracle数据库支持
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
	 *            要设置的 sqlExecutorFactory
	 */
	public void setSqlExecutorFactory(SQLExecutorFactory sqlExecutorFactory) {
		this.sqlExecutorFactory = sqlExecutorFactory;
	}

	/**
	 * 查找第一条数据
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
	 * 执行SQL，主要用来执行增加、删除、修改语句
	 * @param sql 用来增加、删除、修改的SQL语句
	 * @throws JAFDatabaseException
	 */
	public void execSql(String sql) throws JAFDatabaseException{
		SQLExecutor exec = getSqlExecutorFactory().getSQLExecutor();
		exec.runQueryCloseCon(sql);
	}

}

package com.cfcc.itfe.facade;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.cfcc.itfe.config.ITFEGlobalBeanId;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.BusinessStatDto;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.IOrmTemplate;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutorFactory;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.CommonQto;
import com.cfcc.jaf.persistence.util.SqlUtil;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 数据库操作的公共工具
 * 
 */
public class CommonFacade {

	// 每页显示的页数
	private int numPage;

	private String dbName;
	IOrmTemplate ormTemplate;

	SQLExecutorFactory sqlExecutorFactory;

	private CommonFacade() {
	}

	public static CommonFacade getODB() {
		return (CommonFacade) ContextFactory.getApplicationContext().getBean(
				ITFEGlobalBeanId.COMMON_FACADE_DB_PREFIX + "ITFEDB");

	}

	public static CommonFacade getQDB() {
		return (CommonFacade) ContextFactory.getApplicationContext().getBean(
				ITFEGlobalBeanId.COMMON_FACADE_DB_PREFIX + "ITFEDB");

	}

	/**
	 * 根据Dto中设置的条件查找相应的记录并返回获取的记录集, (整表查询) 约束：只能针对单值相等的条件进行查询
	 * 
	 * @param _dto
	 *            （dto对象）
	 * @return List（记录集）
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public List findRsByDto(IDto _dto) throws JAFDatabaseException,
			ValidateException {
		CommonQto qto = SqlUtil.IDto2CommonQto(_dto);
		return DatabaseFacade.getDb(dbName).find(_dto.getClass(), qto);
	}

	/**
	 * 根据Dto中设置的条件查找相应的记录并返回获取的记录集, (整表查询) 约束：只能针对单值相等的条件进行查询
	 * 
	 * @param _dto
	 *            （dto对象）
	 * @return List（记录集）
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public List findRsByDtoWithUR(IDto _dto) throws JAFDatabaseException,
			ValidateException {
		CommonQto qto = SqlUtil.IDto2CommonQto(_dto);
		return DatabaseFacade.getDb(dbName).findWithUR(_dto.getClass(), qto);
	}

	/**
	 * 根据Dto中设置的条件查找相应的记录并返回获取的记录集, (带Where 条件) 约束：只能针对单值相等的条件进行查询
	 * 
	 * @param _dto
	 *            （dto对象）
	 * @return List（记录集）
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public List findRsByDtoForWhere(IDto _dto, String sqlWhere)
			throws JAFDatabaseException, ValidateException {
		CommonQto qto = SqlUtil.IDto2CommonQto(_dto);
		return DatabaseFacade.getDb(dbName).find(_dto.getClass(),qto==null?" where 1=1 "+sqlWhere:qto.getSWhereClause()+" "+sqlWhere, qto==null?null:qto.getLParams());
	}
	/**
	 * 根据Dto中设置的条件查找相应的记录并返回获取的记录集, (带Where 条件) 约束：只能针对单值相等的条件进行查询
	 * 
	 * @param _dto
	 *            （dto对象）
	 * @return List（记录集）
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public List findRsByWhereForDto(IDto _dto, String sqlWhere)
			throws JAFDatabaseException, ValidateException {
		CommonQto qto = SqlUtil.IDto2CommonQto(_dto);
		return DatabaseFacade.getDb(dbName).find(_dto.getClass(),qto==null?" where "+sqlWhere:" where "+sqlWhere+StringUtils.replace(qto.getSWhereClause(), "Where ", " and "), qto==null?null:qto.getLParams());
	}
	/**
	 * 通过设置的Dto返回符合要求的记录数
	 * 
	 * @param dto
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public int getRecordCountByDto(IDto dto) throws JAFDatabaseException,
			ValidateException {

		CommonQto qto = SqlUtil.IDto2CommonQto(dto);
		int recordCount = DatabaseFacade.getDb(dbName)
				.find(dto.getClass(), qto).size();
		return recordCount;
	}

	/**
	 * 根据Dto中设置的条件级联查找相应的记录并返回获取的记录集
	 * 
	 * @param _dto
	 *            dto对象
	 * @return List（记录集）
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public List findRsByDtoCascade(IDto _dto) throws JAFDatabaseException,
			ValidateException {
		CommonQto qto = SqlUtil.IDto2CommonQto(_dto);
		List list = null;
		ArrayList result = new ArrayList();
		list = DatabaseFacade.getDb(dbName).find(_dto.getClass(), qto);

		Iterator it = list.iterator();
		while (it.hasNext()) {
			IDto dto = (IDto) it.next();
			IDto idto = DatabaseFacade.getDb(dbName).findCascade(dto);
			result.add(idto);
		}

		return result;
	}

	/**
	 * 根据复合条件查找指定数据库表的记录集
	 * 
	 * @param _dtoClass
	 *            dto类对象
	 * @param _fieldList
	 *            字段列表）格式：其中对象为要查询的字段名称
	 * @param _paramList
	 *            参数取值列表，其中一项可以包含多值，此时这些多值项将解释为逻辑或的关系格式：其中对象为Map
	 * @param _isAnd
	 *            查询条件之间是否为逻辑“与”关系，true:与false：或
	 * @return List 记录集
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 */
	public List findRsByCondition(Class _dtoClass, List _fieldList,
			List _paramList, boolean _isAnd) throws ValidateException,
			JAFDatabaseException {

		// 字段列表的长度必须和参数列表长度一致
		if (_fieldList.size() != _paramList.size())
			throw new ValidateException("传入参数中字段列表和参数列表大小不一致");

		int i_loop = 0;
		CommonQto totalQto = new CommonQto();
		Iterator iter = _fieldList.iterator();
		// 解析传入参数，生成可以直接调用的SQL查询条件语法及对应的参数列表
		while (iter.hasNext()) {
			Object o = _paramList.get(i_loop);
			String s_FieldName = (String) iter.next();

			// 转化为标准查询对象
			CommonQto qto = SqlUtil.toStdQto(s_FieldName, (Map) o);
			// 组合原有查询对象和新的查询对象
			totalQto = SqlUtil.toWhereCursorWithout(qto, totalQto, _isAnd);
			i_loop++;
		}
		// log.debug(totalQto.getSClause());
		// 根据解析得到的查询条件语法和对应的参数列表返回要查询的记录集
		return DatabaseFacade.getDb(dbName).find(_dtoClass,
				totalQto.getSWhereClause(), totalQto.getLParams());
	}

	/**
	 * 根据复合条件查找指定数据库表的记录集
	 * 
	 * @param _dtoClass
	 *            (dto类对象)
	 * @param _map
	 *            字段－取值对
	 * @param _isAnd
	 *            查询条件逻辑关系（与或或）
	 * @return List(记录集)
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 */
	public List findRsByCondition(Class _dtoClass, Map _map, boolean _isAnd)
			throws ValidateException, JAFDatabaseException {
		CommonQto totalQto = new CommonQto();
		Iterator iter = _map.keySet().iterator();
		// 解析传入参数，生成可以直接调用的SQL查询条件语法及对应的参数列表
		while (iter.hasNext()) {
			String s_FieldName = (String) iter.next();
			Object o = _map.get(s_FieldName);

			// 转化为标准查询对象
			CommonQto qto = SqlUtil.toStdQto(s_FieldName, (Map) o);
			// log.debug(qto.getSClause());
			if (qto.getSClause().trim().length() == 0)
				continue;
			// 组合原有查询对象和新的查询对象
			totalQto = SqlUtil.toWhereCursorWithout(qto, totalQto, _isAnd);
		}
		// log.debug(totalQto.getSClause());
		// 根据解析得到的查询条件语法和对应的参数列表返回要查询的记录集

		return DatabaseFacade.getDb(dbName).find(_dtoClass,
				totalQto.getSWhereClause(), totalQto.getLParams());
	}

	/**
	 * 根据dto中指定的条件删除相应的记录
	 * 
	 * @param _dto
	 *            对象
	 * @return
	 * @throws JAFDatabaseException
	 */
	public void deleteRsByDto(IDto _dto) throws ValidateException,
			JAFDatabaseException {
		CommonQto qto = SqlUtil.IDto2CommonQto(_dto);
		DatabaseFacade.getDb(dbName).delete(_dto.getClass(), qto);
	}

	/**
	 * 根据CommonQto中指定的查询条件删除相应的记录
	 * 
	 * @param _dtoClass
	 *            要删除的dto类
	 * @param _qto
	 *            (依据的查询条件对象)
	 * @return void
	 * @throws JAFDatabaseException
	 */
	public void deleteRsByCondition(Class _dtoClass, CommonQto _qto)
			throws JAFDatabaseException {

		DatabaseFacade.getDb(dbName).delete(_dtoClass, _qto);
	}

	/**
	 * 根据Dto中设置的条件及匹配关系查找相应的记录并返回获取的记录集
	 * 
	 * @param _dto
	 *            要转换的Dto对象
	 * @param fullmatch
	 *            匹配方式，true 全字段匹配，false 根据conditions中的字段值列表进行匹配，该字段值链表需全部大写
	 * @param conditions
	 *            需要匹配的字段值的列表
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	// public static List findRsByDtoWithLike(IDto _dto,boolean fullmatch,List
	// conditions) throws JAFDatabaseException,
	// ValidateException {
	// CommonQto qto =
	// SqlUtil.IDto2CommonQtoWithLike(_dto,fullmatch,conditions);
	// return DatabaseFacade.getDb(dbName).find(_dto.getClass(), qto);
	// }
	/**
	 * // * 根据分页的配置信息查找指定页的数据,输入where条件 // * // * @param _dto // * 要查询的Dto类型对象
	 * // * @param where // * where条件子句 // * @param params // * 条件参数 // * @param
	 * page // * 查询页配置信息 // * @throws JAFDatabaseException // * @throws
	 * ValidateException //
	 */
	// public Page findRsByDtoForPageWhere(IDto _dto, String where, List params,
	// Page page) throws JAFDatabaseException, ValidateException {
	// int allCount = 0;
	// String sNoWhere = null;
	// List pagelist = null;
	// CommonQto qto = SqlUtil.IDto2CommonQto(_dto);
	// page.setNumperpage(numPage);
	// int len = params.size();
	//
	// // 若已存在where子句
	// if ((qto != null)
	// && (qto.getSClause() != null && qto.getSClause().length() > 0)) {
	// where = where.toLowerCase();
	// int index = where.indexOf("where");
	// if (index > -1) {
	// // Modify by 王俊杰 2007/05/15
	// // 说明:添加下面代码是为了能执行带子查询的sql语句;
	// String subWhere = where.substring(0, index + 5);
	// if (subWhere.trim().equals("where")) {
	// where = where.substring(index + 5);
	// }
	// }
	// sNoWhere = qto.getSClause() + " AND " + where;
	// qto.setSClause(sNoWhere.toLowerCase());
	// for (int i = 0; i < len; i++) {
	// qto.getLParams().add(params.get(i));
	// }
	// } else {
	// qto = new CommonQto();
	// qto.setSClause(where);
	// for (int i = 0; i < len; i++) {
	// if (qto.getLParams() == null)
	// qto.setLParams(new ArrayList());
	// qto.getLParams().add(params.get(i));
	// }
	// }
	// allCount = DatabaseFacade.getDb(dbName).find(_dto.getClass(), qto)
	// .size();
	//
	// if (allCount == 0)
	// return null;
	//
	// if (page.getTotalpage() == null) {
	// page.setPagenum(new Integer(1));
	// }
	// // if(page.getTotalrecord()==null){
	// page.setTotalrecord(new Integer(allCount));
	// // }
	// // if(page.getTotalpage()==null){
	// int pn = page.getTotalrecord().intValue()
	// / page.getNumperpage().intValue();
	// if (page.getTotalrecord().intValue() % page.getNumperpage().intValue() !=
	// 0) {
	// pn++;
	// }
	// page.setTotalpage(new Integer(pn));
	// // }
	//
	// JAFOrmTemplate template = (JAFOrmTemplate) DatabaseFacade.getDb(dbName)
	// .getOrmTemplate();
	//
	// pagelist = template.find(_dto.getClass(), " where " + qto.getSClause(),
	// qto.getLParams(), (page.getPagenum().intValue() - 1)
	// * page.getNumperpage().intValue() + 1, page
	// .getNumperpage().intValue());
	//
	// page.setRecords(pagelist);
	// return page;
	// }
	//
	// /**
	// * 专为凭证查询使用
	// *
	// * @param _dto
	// * @param where
	// * @param params
	// * @param dtoclass
	// * @param page
	// * @return
	// * @throws JAFDatabaseException
	// * @throws ValidateException
	// * @throws TcbsException
	// */
	// public Page findRsByDtoForPageWhere(IDto _dto, String where, List params,
	// Class dtoclass, Page page) throws JAFDatabaseException,
	// ValidateException {
	// int allCount = 0;
	// String sNoWhere = null;
	// List pagelist = null;
	// CommonQto qto = SqlUtil.IDto2CommonQto(_dto);
	// page.setNumperpage(numPage);
	// int len = params.size();
	//
	// // 若已存在where子句
	// if ((qto != null)
	// && (qto.getSClause() != null && qto.getSClause().length() > 0)) {
	// where = where.toLowerCase();
	// int index = where.indexOf("where");
	// if (index > -1) {
	// // Modify by 王俊杰 2007/05/15
	// // 说明:添加下面代码是为了能执行带子查询的sql语句;
	// String subWhere = where.substring(0, index + 5);
	// if (subWhere.trim().equals("where")) {
	// where = where.substring(index + 5);
	// }
	// }
	// sNoWhere = qto.getSClause() + " AND " + where;
	// qto.setSClause(sNoWhere.toLowerCase());
	// for (int i = 0; i < len; i++) {
	// qto.getLParams().add(params.get(i));
	// }
	// } else {
	// qto = new CommonQto();
	// qto.setSClause(where);
	// for (int i = 0; i < len; i++) {
	// if (qto.getLParams() == null)
	// qto.setLParams(new ArrayList());
	// qto.getLParams().add(params.get(i));
	// }
	// }
	// allCount = DatabaseFacade.getDb(dbName).find(dtoclass, qto).size();
	//
	// if (allCount == 0)
	// return null;
	//
	// if (page.getTotalpage() == null) {
	// page.setPagenum(new Integer(1));
	// }
	// // if(page.getTotalrecord()==null){
	// page.setTotalrecord(new Integer(allCount));
	// // }
	// // if(page.getTotalpage()==null){
	// int pn = page.getTotalrecord().intValue()
	// / page.getNumperpage().intValue();
	// if (page.getTotalrecord().intValue() % page.getNumperpage().intValue() !=
	// 0) {
	// pn++;
	// }
	// page.setTotalpage(new Integer(pn));
	// // }
	//
	// JAFOrmTemplate template = (JAFOrmTemplate) DatabaseFacade.getDb(dbName)
	// .getOrmTemplate();
	//
	// pagelist = template.find(dtoclass, " where " + qto.getSClause(), qto
	// .getLParams(), (page.getPagenum().intValue() - 1)
	// * page.getNumperpage().intValue() + 1, page.getNumperpage()
	// .intValue());
	//
	// page.setRecords(pagelist);
	// return page;
	// }
	//
	// /**
	// * 根据分页的配置信息查找指定页的数据,输入where条件,适用多表查询(2张表)
	// *
	// * @param _dto
	// * 要查询的Dto类型对象
	// * @param where
	// * where条件子句
	// * @param params
	// * 条件参数
	// * @param page
	// * 查询页配置信息
	// * @throws JAFDatabaseException
	// * @throws ValidateException
	// */
	// public Page findRsByDtoForPageWhereDoubleTable(IDto _dto, String where,
	// List params, Page page) throws JAFDatabaseException,
	// ValidateException {
	// int allCount = 0;
	// String sNoWhere = null;
	// List pagelist = null;
	// CommonQto qto = SqlUtil.IDto2CommonQto(_dto);
	// page.setNumperpage(numPage);
	// int len = params.size();
	//
	// // 若已存在where子句
	// if ((qto != null)
	// && (qto.getSClause() != null && qto.getSClause().length() > 0)) {
	// where = where.toLowerCase();
	// sNoWhere = qto.getSClause() + " AND " + where;
	// qto.setSClause(sNoWhere.toLowerCase());
	// for (int i = 0; i < len; i++) {
	// qto.getLParams().add(params.get(i));
	// }
	// } else {
	// qto = new CommonQto();
	// qto.setSClause(where);
	// for (int i = 0; i < len; i++) {
	// if (qto.getLParams() == null)
	// qto.setLParams(new ArrayList());
	// qto.getLParams().add(params.get(i));
	// }
	// }
	// allCount = DatabaseFacade.getDb(dbName).find(_dto.getClass(), qto)
	// .size();
	//
	// if (allCount == 0)
	// return null;
	//
	// if (page.getTotalpage() == null) {
	// page.setPagenum(new Integer(1));
	// }
	// // if(page.getTotalrecord()==null){
	// page.setTotalrecord(new Integer(allCount));
	// // }
	// // if(page.getTotalpage()==null){
	// int pn = page.getTotalrecord().intValue()
	// / page.getNumperpage().intValue();
	// if (page.getTotalrecord().intValue() % page.getNumperpage().intValue() !=
	// 0) {
	// pn++;
	// }
	// page.setTotalpage(new Integer(pn));
	// // }
	//
	// JAFOrmTemplate template = (JAFOrmTemplate) DatabaseFacade.getDb(dbName)
	// .getOrmTemplate();
	//
	// pagelist = template.find(_dto.getClass(), " where " + qto.getSClause(),
	// qto.getLParams(), (page.getPagenum().intValue() - 1)
	// * page.getNumperpage().intValue() + 1, page
	// .getNumperpage().intValue());
	//
	// page.setRecords(pagelist);
	// return page;
	// }
	//
	// public Page findRsByDtoForPageWhere(Class dtoclass, String where,
	// List params, Page page) throws JAFDatabaseException,
	// ValidateException {
	// int allCount = 0;
	// String sNoWhere = null;
	// List pagelist = null;
	// page.setNumperpage(numPage);
	// int len = params.size();
	//
	// allCount = DatabaseFacade.getDb(dbName).findWithUR(dtoclass, where,
	// params).size();
	//
	// if (allCount == 0)
	// return null;
	//
	// if (page.getTotalpage() == null) {
	// page.setPagenum(new Integer(1));
	// }
	// // if(page.getTotalrecord()==null){
	// page.setTotalrecord(new Integer(allCount));
	// // }
	// // if(page.getTotalpage()==null){
	// int pn = page.getTotalrecord().intValue()
	// / page.getNumperpage().intValue();
	// if (page.getTotalrecord().intValue() % page.getNumperpage().intValue() !=
	// 0) {
	// pn++;
	// }
	// page.setTotalpage(new Integer(pn));
	// // }
	//
	// JAFOrmTemplate template = (JAFOrmTemplate) DatabaseFacade.getDb(dbName)
	// .getOrmTemplate();
	//
	// pagelist = template.find(dtoclass, where, params, (page.getPagenum()
	// .intValue() - 1)
	// * page.getNumperpage().intValue() + 1, page.getNumperpage()
	// .intValue());
	//
	// page.setRecords(pagelist);
	// return page;
	// }
	//
	// /**
	// * 根据分页的配置信息查找指定页的数据
	// *
	// * @param _dto
	// * 要查询的Dto类型对象
	// * @param page
	// * 查询页配置信息
	// * @throws JAFDatabaseException
	// * @throws ValidateException
	// */
	// public Page findRsByDtoForPage(IDto _dto, Page page)
	// throws JAFDatabaseException, ValidateException {
	// int allCount = 0;
	// List pagelist = null;
	// page.setNumperpage(numPage);
	// allCount = getRecordCountByDto(_dto);
	// // 查询无记录直接返回
	// if (allCount == 0)
	// return null;
	// if (page.getTotalpage() == null) {
	// page.setPagenum(new Integer(1));
	// }
	// // if(page.getTotalrecord()==null){
	// page.setTotalrecord(new Integer(allCount));
	// // }
	// // if(page.getTotalpage()==null){
	// int pn = page.getTotalrecord().intValue()
	// / page.getNumperpage().intValue();
	// if (page.getTotalrecord().intValue() % page.getNumperpage().intValue() !=
	// 0) {
	// pn++;
	// }
	// page.setTotalpage(new Integer(pn));
	// // }
	//
	// JAFOrmTemplate template = (JAFOrmTemplate) DatabaseFacade.getDb(dbName)
	// .getOrmTemplate();
	//
	// // com.cfcc.jaf.persistence.util.CommonQto qto =
	// // SqlUtil.IIDto2CommonQto(_dto);
	// CommonQto qto = SqlUtil.IDto2CommonQto(_dto);
	// int start = (page.getPagenum().intValue() - 1)
	// * page.getNumperpage().intValue() + 1;
	// int size = page.getNumperpage().intValue();
	// if (qto == null) {
	//
	// pagelist = template.find(_dto.getClass(), null, null, start, size);
	// } else
	// pagelist = template.find(_dto.getClass(), qto.getSWhereClause(),
	// qto.getLParams(), start, size);
	//
	// page.setRecords(pagelist);
	// return page;
	// }
	//
	// /**
	// * 专为凭证查询使用
	// *
	// * @param _dto
	// * @param dtoclass
	// * @param page
	// * @return
	// * @throws JAFDatabaseException
	// * @throws ValidateException
	// */
	// public Page findRsByDtoForPage(IDto _dto, Class dtoclass, Page page)
	// throws JAFDatabaseException, ValidateException {
	// int allCount = 0;
	// List pagelist = null;
	// page.setNumperpage(numPage);
	// allCount = getRecordCountByDto(_dto);
	// // 查询无记录直接返回
	// if (allCount == 0)
	// return null;
	// if (page.getTotalpage() == null) {
	// page.setPagenum(new Integer(1));
	// }
	// // if(page.getTotalrecord()==null){
	// page.setTotalrecord(new Integer(allCount));
	// // }
	// // if(page.getTotalpage()==null){
	// int pn = page.getTotalrecord().intValue()
	// / page.getNumperpage().intValue();
	// if (page.getTotalrecord().intValue() % page.getNumperpage().intValue() !=
	// 0) {
	// pn++;
	// }
	// page.setTotalpage(new Integer(pn));
	// // }
	//
	// JAFOrmTemplate template = (JAFOrmTemplate) DatabaseFacade.getDb(dbName)
	// .getOrmTemplate();
	//
	// // com.cfcc.jaf.persistence.util.CommonQto qto =
	// // SqlUtil.IIDto2CommonQto(_dto);
	// CommonQto qto = SqlUtil.IDto2CommonQto(_dto);
	// int start = (page.getPagenum().intValue() - 1)
	// * page.getNumperpage().intValue() + 1;
	// int size = page.getNumperpage().intValue();
	// if (qto == null) {
	//
	// pagelist = template.find(dtoclass, null, null, start, size);
	// } else
	// pagelist = template.find(dtoclass, qto.getSWhereClause(), qto
	// .getLParams(), start, size);
	//
	// page.setRecords(pagelist);
	// return page;
	// }
	//
	// /**
	// * 拼写sql语句分页查询
	// *
	// * @param dtoclass
	// * 查询记录对象，自定义Dto
	// * @param sql
	// * 查询sql
	// * @param sqlCount
	// * 查询记录数sql
	// * @param params
	// * 参数
	// * @param sqlCount
	// * 参数
	// * @param page
	// * 页
	// * @return page
	// * @throws JAFDatabaseException
	// * 数据库错误
	// * @throws ValidateException
	// * 校验错误
	// */
	// public Page findRsByDtoForPageSql(Class dtoclass, String sql,
	// String sqlCount, List params, List parmsCount, Page page)
	// throws JAFDatabaseException, ValidateException {
	//
	// SQLExecutor sqlExec = DatabaseFacade.getDb(dbName)
	// .getSqlExecutorFactory().getSQLExecutor();
	//
	// int allCount = 0;
	// page.setNumperpage(numPage);
	//
	// sqlExec.addParam(parmsCount);
	// SQLResults resCount = sqlExec.runQuery(sqlCount, null,
	// true);
	// allCount = resCount.getInt(0, 0);
	//
	// sqlExec.clearParams();
	// if (allCount == 0)
	// return page;
	//
	// if (page.getTotalpage() == null) {
	// page.setPagenum(new Integer(1));
	// }
	//
	// page.setTotalrecord(new Integer(allCount));
	//
	// int pn = page.getTotalrecord().intValue()
	// / page.getNumperpage().intValue();
	// if (page.getTotalrecord().intValue() % page.getNumperpage().intValue() !=
	// 0) {
	// pn++;
	// }
	// page.setTotalpage(new Integer(pn));
	//
	// sqlExec.addParam(params);
	// // SQLResults resa = sqlExec.runQuery(sql, dtoclass);
	// // List list = (List) resa.getDtoCollection();
	// SQLResults res = sqlExec.runQuery(sql , dtoclass, (page
	// .getPagenum().intValue() - 1)
	// * page.getNumperpage().intValue() + 1, page.getNumperpage()
	// .intValue(), true);
	//
	// List<Class> ls = (List) res.getDtoCollection();
	//
	// page.setRecords(ls);
	// return page;
	// }
	// /**
	// * 根据分页的配置信息查找指定页的数据,支持模糊查询
	// *
	// * @param _dtoclass
	// * 要查询的Dto类型对象
	// * @param page
	// * 查询页配置信息
	// * @author panx
	// * @throws JAFDatabaseException
	// * @throws ValidateException
	// */
	// public Page findRsByDtoForPageWithLike(IDto _dto,Page page,boolean
	// fullmatch,List conditions,String where) throws JAFDatabaseException,
	// ValidateException {
	// int allCount = 0;
	// List pagelist = null;
	// page.setNumperpage(numPage);
	// //设置每页显示的记录数
	// //CommonQto qto =
	// SqlUtil.IDto2CommonQtoWithLike(_dto,fullmatch,conditions);
	// CommonQto qto = null;
	// if(where!=null)
	// qto.setSClause(" where "+ qto.getSClause()+ " and " + where);
	// else
	// qto.setSClause(" where "+ qto.getSClause());
	// List retLiet = DatabaseFacade.getDb(dbName).find(_dto.getClass(), qto);
	// if(retLiet!=null&&retLiet.size()>0)
	// allCount = retLiet.size();
	// else
	// return page;
	// if(page.getTotalpage()==null)
	// {
	// page.setPagenum(new Integer(1));
	// }
	// // if(page.getTotalrecord()==null){
	// page.setTotalrecord(new Integer(allCount));
	// // }
	// // if(page.getTotalpage()==null){
	// int pn =
	// page.getTotalrecord().intValue()/page.getNumperpage().intValue();
	// if(page.getTotalrecord().intValue()%page.getNumperpage().intValue()!=0){
	// pn++;
	// }
	// page.setTotalpage(new Integer(pn));
	// // }
	//		
	// JAFOrmTemplate template =
	// (JAFOrmTemplate)DatabaseFacade.getDb(dbName).getOrmTemplate();
	//
	// //com.cfcc.jaf.persistence.util.CommonQto qto =
	// SqlUtil.IIDto2CommonQto(_dto);
	// // CommonQto qto = SQLUtil.IDto2CommonQto(_dto);
	// int start =
	// (page.getPagenum().intValue()-1)*page.getNumperpage().intValue()+1;
	// int size = page.getNumperpage().intValue();
	// if(qto==null)
	// {
	//    		
	// pagelist = template.find(_dto.getClass(),null, null,start,size);
	// }
	// else
	// pagelist = template.find(_dto.getClass(), qto.getSWhereClause(),
	// qto.getLParams(),start,size);
	//    		
	// page.setRecords(pagelist);
	// return page;
	// }
	/**
	 * @return numPage
	 */
	public int getNumPage() {
		return numPage;
	}

	/**
	 * @param numPage
	 *            要设置的 numPage
	 */
	public void setNumPage(int numPage) {
		this.numPage = numPage;
	}

	/**
	 * @return dbName
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * @param dbName
	 *            要设置的 dbName
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
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
		return sqlExecutorFactory;
	}

	/**
	 * 根据DTO对象查询，并分页。客户端查询使用
	 * 
	 * @param dto
	 * @param request
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 */
	public PageResponse findRsByDtoPaging(IDto dto, PageRequest request)
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		PageResponse response = new PageResponse(request);
		if (response.getTotalCount() == 0) {
			// 初始化总记录数
			int totalCount = getRecordCountByDto(dto);
			response.setTotalCount(totalCount);
		}

		// 分页查询
		SQLExecutor sqlExe = DatabaseFacade.getDb(dbName)
				.getSqlExecutorFactory().getSQLExecutor();
		String tableName = tableName(dto.getClass());
		String where = "select * from " + tableName;
		CommonQto qto = SqlUtil.IDto2CommonQto(dto);
		if (qto != null) {
			where += qto.getSWhereClause();
			sqlExe.addParam(qto.getLParams());
		}

		SQLResults res = sqlExe.runQueryCloseCon(where, dto.getClass(), request
				.getStartPosition(), request.getPageSize(), true);
		List list = new ArrayList();
		list.addAll(res.getDtoCollection());
		response.getData().clear();
		response.setData(list);
		return response;
	}

	/**
	 * 带Where条件根据DTO对象查询，并分页。客户端查询使用
	 * 
	 * @param dto
	 * @param request
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 */
	public PageResponse findRsByDtoWithWherePaging(IDto dto,
			PageRequest request, String where) throws JAFDatabaseException,
			ValidateException, ITFEBizException {
		PageResponse response = new PageResponse(request);

		// 分页查询
		String tableName = tableName(dto.getClass());
		String sqlwhere = "select * from " + tableName;
		CommonQto qto = SqlUtil.IDto2CommonQto(dto);
		if (qto != null) {
			if (where != null) {
				sqlwhere += qto.getSWhereClause() + " and " + where
						+ " with ur";
			} else {
				sqlwhere += qto.getSWhereClause() + " with ur";
			}
		} else {
			if (where != null) {
				sqlwhere += " where " + where;
			}
		}

		if (response.getTotalCount() == 0) {
			SQLExecutor sqlExe = DatabaseFacade.getDb(dbName)
					.getSqlExecutorFactory().getSQLExecutor();
			if (qto != null) {
				sqlExe.addParam(qto.getLParams());
			}
			// 初始化总记录数
			String countsqlwhere = sqlwhere.replace("*", "count(*)");
			int count = sqlExe.runQueryCloseCon(countsqlwhere, dto.getClass())
					.getInt(0, 0);
			response.setTotalCount(count);
		}

		SQLExecutor sqlExe2 = DatabaseFacade.getDb(dbName)
				.getSqlExecutorFactory().getSQLExecutor();
		if (qto != null) {
			sqlExe2.addParam(qto.getLParams());
		}

		SQLResults res = sqlExe2.runQueryCloseCon(sqlwhere, dto.getClass(),
				request.getStartPosition(), request.getPageSize(), false);
		List list = new ArrayList();
		list.addAll(res.getDtoCollection());
		response.getData().clear();
		response.setData(list);
		return response;
	}

	/**
	 * 根据DTO对象查询，并分页。客户端查询使用
	 * 
	 * @param dto
	 * @param request
	 * @param String
	 *            wherestr where条件,不能带参数
	 * @param String
	 *            orderbyProp 排序条件
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws TPPFSBizException
	 * @author zhouchuan
	 */
	public PageResponse findRsByDtoPaging(IDto dto, PageRequest request,
			String wherestr, String orderbyProp) throws JAFDatabaseException,
			ValidateException, ITFEBizException {
		PageResponse response = new PageResponse(request);

		// 分页查询
		SQLExecutor sqlExe = DatabaseFacade.getDb(dbName)
				.getSqlExecutorFactory().getSQLExecutor();
		String tableName = tableName(dto.getClass());
		String where = "select * from " + tableName;
		CommonQto qto = SqlUtil.IDto2CommonQto(dto);
		if (qto != null) {
			where += qto.getSWhereClause();
			sqlExe.addParam(qto.getLParams());
			if (null != wherestr && !"".equals(wherestr.trim())) {
				where += " and " + wherestr;
			}
		} else {
			if (null != wherestr && !"".equals(wherestr.trim())) {
				where += " where " + wherestr;
			}
		}

		if (null != orderbyProp && !"".equals(orderbyProp.trim())) {
			where += " order by " + orderbyProp;
		}

		if (response.getTotalCount() == 0) {
			// 初始化总记录数
			int totalCount = 0;
			if (null != wherestr && !"".equals(wherestr.trim())) {
				totalCount = getRecordCountByDto(dto, wherestr);
			} else {
				totalCount = getRecordCountByDto(dto);
			}
			response.setTotalCount(totalCount);
		}

		SQLResults res = sqlExe.runQueryCloseCon(where, dto.getClass(), request
				.getStartPosition(), request.getPageSize(), true);
		List list = new ArrayList();
		list.addAll(res.getDtoCollection());
		response.getData().clear();
		response.setData(list);
		return response;
	}

	/**
	 * 根据DTO对象查询，并分页。客户端查询使用
	 * 
	 * @param dto
	 * @param request
	 * @param String
	 *            wherestr where条件,不能带参数
	 * @param String
	 *            groupbyProp 分组条件
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws TPPFSBizException
	 * @author zhouchuan
	 */
	public PageResponse findRsByDtoPagingGroupby(IDto dto, PageRequest request,
			String wherestr, String groupbyProp) throws JAFDatabaseException,
			ValidateException, ITFEBizException {
		PageResponse response = new PageResponse(request);

		// 分页查询
		SQLExecutor sqlExe = DatabaseFacade.getDb(dbName)
				.getSqlExecutorFactory().getSQLExecutor();
		String tableName = tableName(dto.getClass());
		String where = "select  S_ACCDATE, S_RECVTRECODE, S_FILENAME  from " + tableName;
		CommonQto qto = SqlUtil.IDto2CommonQto(dto);
		if (qto != null) {
			where += qto.getSWhereClause();
			sqlExe.addParam(qto.getLParams());
			if (null != wherestr && !"".equals(wherestr.trim())) {
				where += " and " + wherestr;
			}
		} else {
			if (null != wherestr && !"".equals(wherestr.trim())) {
				where += " where " + wherestr;
			}
		}

		if (null != groupbyProp && !"".equals(groupbyProp.trim())) {
			where += " group by " + groupbyProp;
		}

		if (response.getTotalCount() == 0) {
			// 初始化总记录数
			int totalCount = 0;
			if (null != groupbyProp && !"".equals(groupbyProp.trim())) {
				totalCount = getRecordCountByGroupby(dto, groupbyProp);
				response.setTotalCount(totalCount);
			}
		}

		SQLResults res = sqlExe.runQueryCloseCon(where, dto.getClass(), request
				.getStartPosition(), request.getPageSize(), true);
		List list = new ArrayList();
		list.addAll(res.getDtoCollection());
		response.getData().clear();
		response.setData(list);
		return response;
	}

		/**
		 * 通过设置的Dto返回符合要求的记录数
		 * 
		 * @param dto
		 * @return
		 * @throws JAFDatabaseException
		 * @throws ValidateException
		 */
		public int getRecordCountByGroupby(IDto dto, String groupbyProp)
				throws JAFDatabaseException, ValidateException, ITFEBizException {

			// 分页查询
			SQLExecutor sqlExe = DatabaseFacade.getDb(dbName)
					.getSqlExecutorFactory().getSQLExecutor();
			String tableName = tableName(dto.getClass());
			String where = "select S_ACCDATE, S_RECVTRECODE, S_FILENAME from " + tableName;
			CommonQto qto = SqlUtil.IDto2CommonQto(dto);
			if (qto != null) {
				where += qto.getSWhereClause();
				where += " group by " + groupbyProp;
				sqlExe.addParam(qto.getLParams());
			} else {
				where += " group by " + groupbyProp;
			}

			SQLResults res = sqlExe.runQueryCloseCon(where, dto.getClass(), true);
			return res.getDtoCollection().size();
		}
		
		/**
		 * 根据DTO对象查询，并分页。客户端查询使用
		 * 
		 * @param dto
		 * @param request
		 * @param String
		 *            groupbyProp 分组条件
		 * @return
		 * @throws JAFDatabaseException
		 * @throws ValidateException
		 * @throws TPPFSBizException
		 * @author zhouchuan
		 */
		public PageResponse findRsByDtoPagingBusiness(IDto dto, PageRequest request,
				String groupbyProp) throws JAFDatabaseException,
				ValidateException, ITFEBizException {
			PageResponse response = new PageResponse(request);

			// 分页查询
			SQLExecutor sqlExe = DatabaseFacade.getDb(dbName)
					.getSqlExecutorFactory().getSQLExecutor();
			String tableName = tableName(dto.getClass());
			String where = "select  S_ACCDATE, count(S_ACCDATE) as N_BUSINESSNUM from tv_infile " ;
			CommonQto qto = SqlUtil.IDto2CommonQto(dto);
			if (qto != null) {
				where += qto.getSWhereClause();
				sqlExe.addParam(qto.getLParams());
			}

			if (null != groupbyProp && !"".equals(groupbyProp.trim())) {
				where += " group by " + groupbyProp;
			}
			where += " union select  S_ACCDATE, count(S_ACCDATE) as N_BUSINESSNUM from htv_infile ";
			if (qto != null) {
				where += qto.getSWhereClause();
				sqlExe.addParam(qto.getLParams());
			}
			if (null != groupbyProp && !"".equals(groupbyProp.trim())) {
				where += " group by " + groupbyProp;
				where += " order by " + groupbyProp;
			}

			if (response.getTotalCount() == 0) {
				// 初始化总记录数
				int totalCount = 0;
				if (null != groupbyProp && !"".equals(groupbyProp.trim())) {
					totalCount = getRecordCountBusiness(dto, groupbyProp);
					response.setTotalCount(totalCount);
				}
			}

			SQLResults res = sqlExe.runQueryCloseCon(where, BusinessStatDto.class, request
					.getStartPosition(), request.getPageSize(), true);
			List list = new ArrayList();
			list.addAll(res.getDtoCollection());
			response.getData().clear();
			response.setData(list);
			return response;
		}
		/**
		 * 带Where条件根据DTO对象当前表历史表合并查询，并分页。客户端查询使用
		 * @param dto
		 * @param request
		 * @param where
		 * @param sort
		 * @param tablename
		 * @return
		 * @throws JAFDatabaseException
		 * @throws ValidateException
		 * @throws ITFEBizException
		 */
		public PageResponse findRsByDtoWithWherePaging(IDto dto,
				PageRequest request, String where,String sort,String tablename) throws JAFDatabaseException,
				ValidateException, ITFEBizException {
			
			PageResponse response = new PageResponse(request);
			// 分页查询
			SQLExecutor sqlExe = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			
			String sql = "select t.* from (select * from "+tablename+" "+
							" union all "+
							" select * from h"+tablename+") as t ";
			
			CommonQto qto = SqlUtil.IDto2CommonQto(dto);
			if (qto != null) {
				if (where != null) {
					sql += qto.getSWhereClause() + " and " + where;
				} else {
					sql += qto.getSWhereClause();
				}
				sqlExe.addParam(qto.getLParams());
			} else {
				if (where != null) {
					sql += " where " + where;
				}
			}
			
			if (response.getTotalCount() == 0) {
				SQLExecutor sqlExe1 =DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				if (qto != null) {
					sqlExe1.addParam(qto.getLParams());
				}
				// 初始化总记录数
				String countsqlwhere = sql.replace("t.*", "count(*)")+ " with ur";
				int count = sqlExe1.runQueryCloseCon(countsqlwhere, dto.getClass())
						.getInt(0, 0);
				response.setTotalCount(count);
			}
			
			sql += sort + " with ur";

			SQLResults res = sqlExe.runQueryCloseCon(sql, dto.getClass(), request
					.getStartPosition(), request.getPageSize(), false);
			List list = new ArrayList();
			list.addAll(res.getDtoCollection());
		
			response.getData().clear();
			response.setData(list);
			return response;
		}

			/**
			 * 通过设置的Dto返回符合要求的记录数
			 * 
			 * @param dto
			 * @return
			 * @throws JAFDatabaseException
			 * @throws ValidateException
			 */
			public int getRecordCountBusiness(IDto dto, String groupbyProp)
					throws JAFDatabaseException, ValidateException, ITFEBizException {

				// 分页查询
				SQLExecutor sqlExe = DatabaseFacade.getDb(dbName)
						.getSqlExecutorFactory().getSQLExecutor();
				
				String where = "select  S_ACCDATE, count(S_ACCDATE) as N_BUSINESSNUM from tv_infile " ;
				CommonQto qto = SqlUtil.IDto2CommonQto(dto);
				if (qto != null) {
					where += qto.getSWhereClause();
					sqlExe.addParam(qto.getLParams());
				}

				if (null != groupbyProp && !"".equals(groupbyProp.trim())) {
					where += " group by " + groupbyProp;
				}
				where += " union select  S_ACCDATE, count(S_ACCDATE) as N_BUSINESSNUM from htv_infile ";
				if (qto != null) {
					where += qto.getSWhereClause();
					sqlExe.addParam(qto.getLParams());
				}
				if (null != groupbyProp && !"".equals(groupbyProp.trim())) {
					where += " group by " + groupbyProp;
				}


				SQLResults res = sqlExe.runQueryCloseCon(where, BusinessStatDto.class, true);
				return res.getDtoCollection().size();
			}
			
	/**
	 * 通过设置的Dto返回符合要求的记录数
	 * 
	 * @param dto
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public int getRecordCountByDto(IDto dto, String whereStr)
			throws JAFDatabaseException, ValidateException, ITFEBizException {

		// 分页查询
		SQLExecutor sqlExe = DatabaseFacade.getDb(dbName)
				.getSqlExecutorFactory().getSQLExecutor();
		String tableName = tableName(dto.getClass());
		String where = "select * from " + tableName;
		CommonQto qto = SqlUtil.IDto2CommonQto(dto);
		if (qto != null) {
			where += qto.getSWhereClause();
			where += " and " + whereStr;
			sqlExe.addParam(qto.getLParams());
		} else {
			where += " where " + whereStr;
		}

		SQLResults res = sqlExe.runQueryCloseCon(where, dto.getClass(), true);
		return res.getDtoCollection().size();
	}

	private static String tableName(Class dtoClass) throws ITFEBizException {
		try {
			Method m = dtoClass.getMethod("tableName", new Class[] {});
			return (String) m.invoke(null, new Object[] {});
		} catch (Exception e) {
			throw new ITFEBizException(e);
		}

	}
	/**
	 * 根据Dto中设置的条件查找相应的记录并返回获取的记录集, 当前表历史表合并查询 约束：只能针对单值相等的条件进行查询
	 * 
	 * @param _dto
	 *            （dto对象）
	 * @return List（记录集）
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public List findRsByDto(IDto dto,String sort, String tablename) throws JAFDatabaseException,
			ValidateException {
		
		SQLExecutor sqlExe = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
		
		String sql = "select * from (select * from "+tablename+" "+
						" union all "+
						" select * from h"+tablename+") as t ";
		
		CommonQto qto = SqlUtil.IDto2CommonQto(dto);
		if (qto != null) {
			sql += qto.getSWhereClause()+ " ";
			sqlExe.addParam(qto.getLParams());
		} 
		sql += sort;

		SQLResults res = sqlExe.runQueryCloseCon(sql, dto.getClass(), true);
		List list = new ArrayList();
		list.addAll(res.getDtoCollection());
		
		return list;
	}

	/**
	 * @param sqlExecutorFactory
	 *            要设置的 sqlExecutorFactory
	 */
	public void setSqlExecutorFactory(SQLExecutorFactory sqlExecutorFactory) {
		this.sqlExecutorFactory = sqlExecutorFactory;
	}
	/**
	 * 综合查询
	 * 
	 * @param dto
	 *            查询条件dto
	 * @param page
	 *            查询页配置信息
	 * @author panx
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	// public static void findCompositiveQry(CompositivieQureyDto dto,Page page)
	// throws JAFDatabaseException,
	// ValidateException, FamsServiceException {
	// int allCount = 0;
	// page.setNumperpage(Integer.valueOf(SysCfgFactory.getInstance().getValueByKey("PAGEPNUM")));
	// //统计查询记录数
	// String countSql = SqlUtil.getCompositiveCountSql(dto);
	// log.debug("***" + countSql + "***");
	// SQLExecutor sqlexec =
	// DatabaseFacade.getDb(dbName).getSqlExecutorFactory().getSQLExecutor();
	// SQLResults results = sqlexec.runQueryCloseCon(countSql);
	// allCount = results.getInt(0, 0);
	// //查询无记录直接返回
	// if(allCount==0)
	// return;
	// if(page.getTotalpage()==null)
	// {
	// page.setPagenum(new Integer(1));
	// }
	// // if(page.getTotalrecord()==null){
	// page.setTotalrecord(new Integer(allCount));
	// // }
	// // if(page.getTotalpage()==null){
	// int pn =
	// page.getTotalrecord().intValue()/page.getNumperpage().intValue();
	// if(page.getTotalrecord().intValue()%page.getNumperpage().intValue()!=0){
	// pn++;
	// }
	// page.setTotalpage(new Integer(pn));
	// // }
	//
	// int start =
	// (page.getPagenum().intValue())*page.getNumperpage().intValue()+1;
	// int size = page.getNumperpage().intValue();
	//
	// String sql = SqlUtil.getCompositiveSql(dto);
	// sqlexec =
	// DatabaseFacade.getDb(dbName).getSqlExecutorFactory().getSQLExecutor();
	// results = sqlexec.runQueryCloseCon(sql, CustBuildingQryDto.class, start,
	// size, true);
	//
	// page.setRecords(new ArrayList(results.getDtoCollection()));
	//
	//
	// }
	//
	
	public List selHtableBydto(IDto dto) throws ITFEBizException, JAFDatabaseException {
		SQLExecutor sqlExe=DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
		String tableName = tableName(dto.getClass());
		String where = "select distinct S_RPTDATE from " + tableName;
		CommonQto qto = SqlUtil.IDto2CommonQto(dto);
		if (qto != null) {
			where += qto.getSWhereClause();
			where += " FETCH FIRST 1 ROWS ONLY";
			sqlExe.addParam(qto.getLParams());
		} 
		SQLResults res = sqlExe.runQueryCloseCon(where, dto.getClass(), true);
		return (List) res.getDtoCollection();
	}
	
	
	/**
	 * 带Where条件根据DTO对象当前表历史表合并查询，并分页。客户端查询使用
	 * @param dto
	 * @param request
	 * @param where
	 * @param sort
	 * @param tablename
	 * @param rptdate
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 */
	public PageResponse findRsByDtoWithConditionPaging(IDto dto,
			PageRequest request, String where,String sort,String tablename,String rptdate) throws JAFDatabaseException,
			ValidateException, ITFEBizException {
		
		PageResponse response = new PageResponse(request);
		// 分页查询
		SQLExecutor sqlExe = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
		
		String sql = "SELECT T.* FROM (SELECT * FROM "+tablename+" "+rptdate+" "+
						" UNION ALL "+
						" SELECT * FROM H"+tablename+" "+rptdate+") AS T ";
		
		CommonQto qto = SqlUtil.IDto2CommonQto(dto);
		if (qto != null) {
			if (where != null) {
				sql += qto.getSWhereClause() + " AND " + where;
			} else {
				sql += qto.getSWhereClause();
			}
			sqlExe.addParam(qto.getLParams());
		} else {
			if (where != null) {
				sql += " WHERE " + where;
			}
		}
		
		if (response.getTotalCount() == 0) {
			SQLExecutor sqlExe1 =DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			if (qto != null) {
				sqlExe1.addParam(qto.getLParams());
			}
			// 初始化总记录数
			String countsqlwhere = sql.replace("T.*", "COUNT(*)")+ " WITH UR";
			int count = sqlExe1.runQueryCloseCon(countsqlwhere, dto.getClass())
					.getInt(0, 0);
			response.setTotalCount(count);
		}
		
		sql += sort + " WITH UR";

		SQLResults res = sqlExe.runQueryCloseCon(sql, dto.getClass(), request
				.getStartPosition(), request.getPageSize(), false);
		List list = new ArrayList();
		list.addAll(res.getDtoCollection());
	
		response.getData().clear();
		response.setData(list);
		return response;
	}
}
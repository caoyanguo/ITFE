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
 * ���ݿ�����Ĺ�������
 * 
 */
public class CommonFacade {

	// ÿҳ��ʾ��ҳ��
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
	 * ����Dto�����õ�����������Ӧ�ļ�¼�����ػ�ȡ�ļ�¼��, (�����ѯ) Լ����ֻ����Ե�ֵ��ȵ��������в�ѯ
	 * 
	 * @param _dto
	 *            ��dto����
	 * @return List����¼����
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public List findRsByDto(IDto _dto) throws JAFDatabaseException,
			ValidateException {
		CommonQto qto = SqlUtil.IDto2CommonQto(_dto);
		return DatabaseFacade.getDb(dbName).find(_dto.getClass(), qto);
	}

	/**
	 * ����Dto�����õ�����������Ӧ�ļ�¼�����ػ�ȡ�ļ�¼��, (�����ѯ) Լ����ֻ����Ե�ֵ��ȵ��������в�ѯ
	 * 
	 * @param _dto
	 *            ��dto����
	 * @return List����¼����
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public List findRsByDtoWithUR(IDto _dto) throws JAFDatabaseException,
			ValidateException {
		CommonQto qto = SqlUtil.IDto2CommonQto(_dto);
		return DatabaseFacade.getDb(dbName).findWithUR(_dto.getClass(), qto);
	}

	/**
	 * ����Dto�����õ�����������Ӧ�ļ�¼�����ػ�ȡ�ļ�¼��, (��Where ����) Լ����ֻ����Ե�ֵ��ȵ��������в�ѯ
	 * 
	 * @param _dto
	 *            ��dto����
	 * @return List����¼����
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public List findRsByDtoForWhere(IDto _dto, String sqlWhere)
			throws JAFDatabaseException, ValidateException {
		CommonQto qto = SqlUtil.IDto2CommonQto(_dto);
		return DatabaseFacade.getDb(dbName).find(_dto.getClass(),qto==null?" where 1=1 "+sqlWhere:qto.getSWhereClause()+" "+sqlWhere, qto==null?null:qto.getLParams());
	}
	/**
	 * ����Dto�����õ�����������Ӧ�ļ�¼�����ػ�ȡ�ļ�¼��, (��Where ����) Լ����ֻ����Ե�ֵ��ȵ��������в�ѯ
	 * 
	 * @param _dto
	 *            ��dto����
	 * @return List����¼����
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public List findRsByWhereForDto(IDto _dto, String sqlWhere)
			throws JAFDatabaseException, ValidateException {
		CommonQto qto = SqlUtil.IDto2CommonQto(_dto);
		return DatabaseFacade.getDb(dbName).find(_dto.getClass(),qto==null?" where "+sqlWhere:" where "+sqlWhere+StringUtils.replace(qto.getSWhereClause(), "Where ", " and "), qto==null?null:qto.getLParams());
	}
	/**
	 * ͨ�����õ�Dto���ط���Ҫ��ļ�¼��
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
	 * ����Dto�����õ���������������Ӧ�ļ�¼�����ػ�ȡ�ļ�¼��
	 * 
	 * @param _dto
	 *            dto����
	 * @return List����¼����
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
	 * ���ݸ�����������ָ�����ݿ��ļ�¼��
	 * 
	 * @param _dtoClass
	 *            dto�����
	 * @param _fieldList
	 *            �ֶ��б���ʽ�����ж���ΪҪ��ѯ���ֶ�����
	 * @param _paramList
	 *            ����ȡֵ�б�����һ����԰�����ֵ����ʱ��Щ��ֵ�����Ϊ�߼���Ĺ�ϵ��ʽ�����ж���ΪMap
	 * @param _isAnd
	 *            ��ѯ����֮���Ƿ�Ϊ�߼����롱��ϵ��true:��false����
	 * @return List ��¼��
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 */
	public List findRsByCondition(Class _dtoClass, List _fieldList,
			List _paramList, boolean _isAnd) throws ValidateException,
			JAFDatabaseException {

		// �ֶ��б�ĳ��ȱ���Ͳ����б���һ��
		if (_fieldList.size() != _paramList.size())
			throw new ValidateException("����������ֶ��б�Ͳ����б��С��һ��");

		int i_loop = 0;
		CommonQto totalQto = new CommonQto();
		Iterator iter = _fieldList.iterator();
		// ����������������ɿ���ֱ�ӵ��õ�SQL��ѯ�����﷨����Ӧ�Ĳ����б�
		while (iter.hasNext()) {
			Object o = _paramList.get(i_loop);
			String s_FieldName = (String) iter.next();

			// ת��Ϊ��׼��ѯ����
			CommonQto qto = SqlUtil.toStdQto(s_FieldName, (Map) o);
			// ���ԭ�в�ѯ������µĲ�ѯ����
			totalQto = SqlUtil.toWhereCursorWithout(qto, totalQto, _isAnd);
			i_loop++;
		}
		// log.debug(totalQto.getSClause());
		// ���ݽ����õ��Ĳ�ѯ�����﷨�Ͷ�Ӧ�Ĳ����б���Ҫ��ѯ�ļ�¼��
		return DatabaseFacade.getDb(dbName).find(_dtoClass,
				totalQto.getSWhereClause(), totalQto.getLParams());
	}

	/**
	 * ���ݸ�����������ָ�����ݿ��ļ�¼��
	 * 
	 * @param _dtoClass
	 *            (dto�����)
	 * @param _map
	 *            �ֶΣ�ȡֵ��
	 * @param _isAnd
	 *            ��ѯ�����߼���ϵ������
	 * @return List(��¼��)
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 */
	public List findRsByCondition(Class _dtoClass, Map _map, boolean _isAnd)
			throws ValidateException, JAFDatabaseException {
		CommonQto totalQto = new CommonQto();
		Iterator iter = _map.keySet().iterator();
		// ����������������ɿ���ֱ�ӵ��õ�SQL��ѯ�����﷨����Ӧ�Ĳ����б�
		while (iter.hasNext()) {
			String s_FieldName = (String) iter.next();
			Object o = _map.get(s_FieldName);

			// ת��Ϊ��׼��ѯ����
			CommonQto qto = SqlUtil.toStdQto(s_FieldName, (Map) o);
			// log.debug(qto.getSClause());
			if (qto.getSClause().trim().length() == 0)
				continue;
			// ���ԭ�в�ѯ������µĲ�ѯ����
			totalQto = SqlUtil.toWhereCursorWithout(qto, totalQto, _isAnd);
		}
		// log.debug(totalQto.getSClause());
		// ���ݽ����õ��Ĳ�ѯ�����﷨�Ͷ�Ӧ�Ĳ����б���Ҫ��ѯ�ļ�¼��

		return DatabaseFacade.getDb(dbName).find(_dtoClass,
				totalQto.getSWhereClause(), totalQto.getLParams());
	}

	/**
	 * ����dto��ָ��������ɾ����Ӧ�ļ�¼
	 * 
	 * @param _dto
	 *            ����
	 * @return
	 * @throws JAFDatabaseException
	 */
	public void deleteRsByDto(IDto _dto) throws ValidateException,
			JAFDatabaseException {
		CommonQto qto = SqlUtil.IDto2CommonQto(_dto);
		DatabaseFacade.getDb(dbName).delete(_dto.getClass(), qto);
	}

	/**
	 * ����CommonQto��ָ���Ĳ�ѯ����ɾ����Ӧ�ļ�¼
	 * 
	 * @param _dtoClass
	 *            Ҫɾ����dto��
	 * @param _qto
	 *            (���ݵĲ�ѯ��������)
	 * @return void
	 * @throws JAFDatabaseException
	 */
	public void deleteRsByCondition(Class _dtoClass, CommonQto _qto)
			throws JAFDatabaseException {

		DatabaseFacade.getDb(dbName).delete(_dtoClass, _qto);
	}

	/**
	 * ����Dto�����õ�������ƥ���ϵ������Ӧ�ļ�¼�����ػ�ȡ�ļ�¼��
	 * 
	 * @param _dto
	 *            Ҫת����Dto����
	 * @param fullmatch
	 *            ƥ�䷽ʽ��true ȫ�ֶ�ƥ�䣬false ����conditions�е��ֶ�ֵ�б����ƥ�䣬���ֶ�ֵ������ȫ����д
	 * @param conditions
	 *            ��Ҫƥ����ֶ�ֵ���б�
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
	 * // * ���ݷ�ҳ��������Ϣ����ָ��ҳ������,����where���� // * // * @param _dto // * Ҫ��ѯ��Dto���Ͷ���
	 * // * @param where // * where�����Ӿ� // * @param params // * �������� // * @param
	 * page // * ��ѯҳ������Ϣ // * @throws JAFDatabaseException // * @throws
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
	// // ���Ѵ���where�Ӿ�
	// if ((qto != null)
	// && (qto.getSClause() != null && qto.getSClause().length() > 0)) {
	// where = where.toLowerCase();
	// int index = where.indexOf("where");
	// if (index > -1) {
	// // Modify by ������ 2007/05/15
	// // ˵��:������������Ϊ����ִ�д��Ӳ�ѯ��sql���;
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
	// * רΪƾ֤��ѯʹ��
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
	// // ���Ѵ���where�Ӿ�
	// if ((qto != null)
	// && (qto.getSClause() != null && qto.getSClause().length() > 0)) {
	// where = where.toLowerCase();
	// int index = where.indexOf("where");
	// if (index > -1) {
	// // Modify by ������ 2007/05/15
	// // ˵��:������������Ϊ����ִ�д��Ӳ�ѯ��sql���;
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
	// * ���ݷ�ҳ��������Ϣ����ָ��ҳ������,����where����,���ö���ѯ(2�ű�)
	// *
	// * @param _dto
	// * Ҫ��ѯ��Dto���Ͷ���
	// * @param where
	// * where�����Ӿ�
	// * @param params
	// * ��������
	// * @param page
	// * ��ѯҳ������Ϣ
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
	// // ���Ѵ���where�Ӿ�
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
	// * ���ݷ�ҳ��������Ϣ����ָ��ҳ������
	// *
	// * @param _dto
	// * Ҫ��ѯ��Dto���Ͷ���
	// * @param page
	// * ��ѯҳ������Ϣ
	// * @throws JAFDatabaseException
	// * @throws ValidateException
	// */
	// public Page findRsByDtoForPage(IDto _dto, Page page)
	// throws JAFDatabaseException, ValidateException {
	// int allCount = 0;
	// List pagelist = null;
	// page.setNumperpage(numPage);
	// allCount = getRecordCountByDto(_dto);
	// // ��ѯ�޼�¼ֱ�ӷ���
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
	// * רΪƾ֤��ѯʹ��
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
	// // ��ѯ�޼�¼ֱ�ӷ���
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
	// * ƴдsql����ҳ��ѯ
	// *
	// * @param dtoclass
	// * ��ѯ��¼�����Զ���Dto
	// * @param sql
	// * ��ѯsql
	// * @param sqlCount
	// * ��ѯ��¼��sql
	// * @param params
	// * ����
	// * @param sqlCount
	// * ����
	// * @param page
	// * ҳ
	// * @return page
	// * @throws JAFDatabaseException
	// * ���ݿ����
	// * @throws ValidateException
	// * У�����
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
	// * ���ݷ�ҳ��������Ϣ����ָ��ҳ������,֧��ģ����ѯ
	// *
	// * @param _dtoclass
	// * Ҫ��ѯ��Dto���Ͷ���
	// * @param page
	// * ��ѯҳ������Ϣ
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
	// //����ÿҳ��ʾ�ļ�¼��
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
	 *            Ҫ���õ� numPage
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
	 *            Ҫ���õ� dbName
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
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
		return sqlExecutorFactory;
	}

	/**
	 * ����DTO�����ѯ������ҳ���ͻ��˲�ѯʹ��
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
			// ��ʼ���ܼ�¼��
			int totalCount = getRecordCountByDto(dto);
			response.setTotalCount(totalCount);
		}

		// ��ҳ��ѯ
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
	 * ��Where��������DTO�����ѯ������ҳ���ͻ��˲�ѯʹ��
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

		// ��ҳ��ѯ
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
			// ��ʼ���ܼ�¼��
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
	 * ����DTO�����ѯ������ҳ���ͻ��˲�ѯʹ��
	 * 
	 * @param dto
	 * @param request
	 * @param String
	 *            wherestr where����,���ܴ�����
	 * @param String
	 *            orderbyProp ��������
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

		// ��ҳ��ѯ
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
			// ��ʼ���ܼ�¼��
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
	 * ����DTO�����ѯ������ҳ���ͻ��˲�ѯʹ��
	 * 
	 * @param dto
	 * @param request
	 * @param String
	 *            wherestr where����,���ܴ�����
	 * @param String
	 *            groupbyProp ��������
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

		// ��ҳ��ѯ
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
			// ��ʼ���ܼ�¼��
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
		 * ͨ�����õ�Dto���ط���Ҫ��ļ�¼��
		 * 
		 * @param dto
		 * @return
		 * @throws JAFDatabaseException
		 * @throws ValidateException
		 */
		public int getRecordCountByGroupby(IDto dto, String groupbyProp)
				throws JAFDatabaseException, ValidateException, ITFEBizException {

			// ��ҳ��ѯ
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
		 * ����DTO�����ѯ������ҳ���ͻ��˲�ѯʹ��
		 * 
		 * @param dto
		 * @param request
		 * @param String
		 *            groupbyProp ��������
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

			// ��ҳ��ѯ
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
				// ��ʼ���ܼ�¼��
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
		 * ��Where��������DTO����ǰ����ʷ��ϲ���ѯ������ҳ���ͻ��˲�ѯʹ��
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
			// ��ҳ��ѯ
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
				// ��ʼ���ܼ�¼��
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
			 * ͨ�����õ�Dto���ط���Ҫ��ļ�¼��
			 * 
			 * @param dto
			 * @return
			 * @throws JAFDatabaseException
			 * @throws ValidateException
			 */
			public int getRecordCountBusiness(IDto dto, String groupbyProp)
					throws JAFDatabaseException, ValidateException, ITFEBizException {

				// ��ҳ��ѯ
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
	 * ͨ�����õ�Dto���ط���Ҫ��ļ�¼��
	 * 
	 * @param dto
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public int getRecordCountByDto(IDto dto, String whereStr)
			throws JAFDatabaseException, ValidateException, ITFEBizException {

		// ��ҳ��ѯ
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
	 * ����Dto�����õ�����������Ӧ�ļ�¼�����ػ�ȡ�ļ�¼��, ��ǰ����ʷ��ϲ���ѯ Լ����ֻ����Ե�ֵ��ȵ��������в�ѯ
	 * 
	 * @param _dto
	 *            ��dto����
	 * @return List����¼����
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
	 *            Ҫ���õ� sqlExecutorFactory
	 */
	public void setSqlExecutorFactory(SQLExecutorFactory sqlExecutorFactory) {
		this.sqlExecutorFactory = sqlExecutorFactory;
	}
	/**
	 * �ۺϲ�ѯ
	 * 
	 * @param dto
	 *            ��ѯ����dto
	 * @param page
	 *            ��ѯҳ������Ϣ
	 * @author panx
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	// public static void findCompositiveQry(CompositivieQureyDto dto,Page page)
	// throws JAFDatabaseException,
	// ValidateException, FamsServiceException {
	// int allCount = 0;
	// page.setNumperpage(Integer.valueOf(SysCfgFactory.getInstance().getValueByKey("PAGEPNUM")));
	// //ͳ�Ʋ�ѯ��¼��
	// String countSql = SqlUtil.getCompositiveCountSql(dto);
	// log.debug("***" + countSql + "***");
	// SQLExecutor sqlexec =
	// DatabaseFacade.getDb(dbName).getSqlExecutorFactory().getSQLExecutor();
	// SQLResults results = sqlexec.runQueryCloseCon(countSql);
	// allCount = results.getInt(0, 0);
	// //��ѯ�޼�¼ֱ�ӷ���
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
	 * ��Where��������DTO����ǰ����ʷ��ϲ���ѯ������ҳ���ͻ��˲�ѯʹ��
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
		// ��ҳ��ѯ
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
			// ��ʼ���ܼ�¼��
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
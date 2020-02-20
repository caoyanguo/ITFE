package com.cfcc.itfe.service.dataquery.tvfinincomeonline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.HtvFinIncomeonlineDto;
import com.cfcc.itfe.persistence.dto.TvFinCustomonlineDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeonlineDto;
import com.cfcc.itfe.util.datamove.DataMoveUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.BatchRetriever;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.CommonQto;
import com.cfcc.jaf.persistence.util.SqlUtil;

/**
 * @author t60
 * @time 12-02-24 10:44:47 codecomment:
 */

public class TvFinIncomeonlineService extends AbstractTvFinIncomeonlineService {
	private static Log log = LogFactory.getLog(TvFinIncomeonlineService.class);

	/**
	 * ����
	 * 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException
	 */
	public IDto addInfo(IDto dtoInfo) throws ITFEBizException {
		return null;
	}

	/**
	 * ${JMethod.getCodecomment()}
	 * 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException
	 */
	public void delInfo(IDto dtoInfo) throws ITFEBizException {

	}

	/**
	 * ${JMethod.getCodecomment()}
	 * 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException
	 */
	public void modInfo(IDto dtoInfo) throws ITFEBizException {

	}

	/**
	 * У����˰��������Ӧ��ϵ
	 * 
	 * @generated
	 * @param excheckList
	 * @return java.lang.String
	 * @throws ITFEBizException
	 */
	public String checkTaxPayCodeOrTrecode(List excheckList)
			throws ITFEBizException {
		// String msg="";
		// List<TsTaxpaycodeDto> finddtolist = new ArrayList<TsTaxpaycodeDto>();
		// try {
		// for(int i=0;i<excheckList.size();i++){
		// TvFinIncomeonlineDto dto = (TvFinIncomeonlineDto)excheckList.get(i);
		//				
		// //ȡ����˰��������Ӧ��ϵ��Ϣ
		// finddtolist=DataMoveUtil.getTsTaxpaycodeDtolist(dto);
		//				
		// if(null == finddtolist || 0 ==finddtolist.size() ){
		// // msg
		// +="��ǰ��˰�˴���,���ƣ�["+dto.getStaxpaycode()+"/"+dto.getStaxpayname()+"] �����["+dto.getStrecode()+","+dto.getStrename()+"]У��ʧ�ܣ�"+
		// // "ԭ��ϵͳδ��¼��ǰ��˰��������Ӧ��ϵ��Ϣ���ߵ�¼��˰��������ϵ��Ϣ��Ψһ ! \r\n";
		// continue;
		// }
		// else if(1 < finddtolist.size()){
		// msg
		// +="��ǰ��˰�˴���,���ƣ�["+dto.getStaxpaycode()+"/"+dto.getStaxpayname()+"] �����["+dto.getStrecode()+","+dto.getStrename()+"]У��ʧ�ܣ�"+
		// "ԭ�򣺵�¼��˰��������ϵ��Ϣ��Ψһ ! \r\n";
		// finddtolist.clear();
		// continue;
		// }
		// else {
		// TsTaxpaycodeDto finddto = finddtolist.get(0);
		// if(!dto.getStrecode().equals(finddto.getStrecode())){
		// msg
		// +="��ǰ��˰�˴���,���ƣ�["+dto.getStaxpaycode()+"/"+dto.getStaxpayname()+"] �����["+dto.getStrecode()+","+dto.getStrename()+"]У��ʧ�ܣ�"+
		// "ԭ�򣺵�ǰ��˰�˴���,���������ϵͳ��¼���ݲ�һ�£�ϵͳ��¼����Ϊ��["+finddto.getStaxpaycode()+","+finddto.getStrecode()+"] \r\n";
		// }
		// }
		// finddtolist.clear();
		// }
		// } catch (ITFEBizException e) {
		// log.error("У����˰��������Ӧ��ϵʱ����!", e);
		// throw new ITFEBizException("У����˰��������Ӧ��ϵʱ����!", e);
		// }
		// log.error(msg);

		return null;
	}

	/**
	 * �ֶ�������ֳ�
	 * 
	 * @generated
	 * @param excheckList
	 * @return java.lang.String
	 * @throws ITFEBizException
	 */
	public String makeDivide(List excheckList, String reportdate)
			throws ITFEBizException {

		return null;
		// return DataMoveUtil.makeDivide_(excheckList,1,reportdate);
	}

	public List exportTable(IDto dto, String sqlwhere)
			throws ITFEBizException {
		List<TvFinIncomeonlineDto> result = new ArrayList<TvFinIncomeonlineDto>();
		try {
			CommonQto qto = SqlUtil.IDto2CommonQto(dto);
			SQLExecutor sqlExecutor = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExecutor.setMaxRows(150000);
			if(null != qto){
				sqlExecutor.addParam(qto.getLParams());
			}
			String sql = "select t.* from (select * from TV_FIN_INCOMEONLINE  union all  select * from hTV_FIN_INCOMEONLINE) as t " + ((null == qto) ? " where 1=1 " + sqlwhere : qto.getSWhereClause() + " " + sqlwhere);
			if(dto instanceof TvFinCustomonlineDto)
			{
				sql = sql.replace("TV_FIN_INCOMEONLINE", "TV_FIN_CUSTOMONLINE");
				return (List) sqlExecutor.runQueryCloseCon(sql, TvFinCustomonlineDto.class).getDtoCollection();
			}else
			{
				return (List) sqlExecutor.runQueryCloseCon(sql, TvFinIncomeonlineDto.class).getDtoCollection();
			}
			
		} catch (JAFDatabaseException e) {
			log.error("�����ļ�ʧ�ܣ�",e);
			throw new ITFEBizException("�����ļ�ʧ�ܣ�",e);
		}
	}

	public String exportTable(IDto dto) throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}

}
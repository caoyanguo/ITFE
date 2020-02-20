package com.cfcc.itfe.util;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
/**
 * ������Ϣ����������
 * @author Yuan
 *
 */
public class BizTableUtil {
	private static Log log=LogFactory.getLog(BizTableUtil.class);
	
	/**
	 * ���ݹ�������ȡ��������
	 * @param strecode
	 * @return
	 * @throws ITFEBizException 
	 */
	public static String getOrgcodeByTrecode(String strecode) throws ITFEBizException{
		SQLExecutor sqlExecutor=null;
		String sorgcode=null;
		try {
			sqlExecutor=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			String sql="SELECT * FROM TS_TREASURY WHERE S_TRECODE=?";
			sqlExecutor.addParam(strecode);
			List<TsTreasuryDto> list=(List<TsTreasuryDto>) sqlExecutor.runQueryCloseCon(sql, TsTreasuryDto.class).getDtoCollection(); 
			if(list!=null&&list.size()>0){
				sorgcode=list.get(0).getSorgcode();
			}
		} catch (JAFDatabaseException e) {
			log.error("��ѯ������Ϣʱ�����쳣",e);
			throw new ITFEBizException("��ѯ������Ϣʱ�����쳣",e);
		}
		return sorgcode;
	}
	
	
	/**
	 * ���ݲ������������ȡ��������
	 * @param strecode
	 * @return
	 * @throws ITFEBizException 
	 */
	public static String getOrgcodeByFinorgcode(String sfinorgcode) throws ITFEBizException{
		SQLExecutor sqlExecutor=null;
		String sorgcode=null;
		try {
			sqlExecutor=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			String sql="SELECT * FROM TS_CONVERTFINORG WHERE S_FINORGCODE=?";
			sqlExecutor.addParam(sfinorgcode);
			List<TsConvertfinorgDto> list=(List<TsConvertfinorgDto>) sqlExecutor.runQueryCloseCon(sql, TsConvertfinorgDto.class).getDtoCollection(); 
			if(list!=null&&list.size()>0){
				sorgcode=list.get(0).getSorgcode();
			}
		} catch (JAFDatabaseException e) {
			log.error("��ѯ������־�������ձ�ʱ�����쳣",e);
			throw new ITFEBizException("��ѯ������־�������ձ�ʱ�����쳣",e);
		}
		return sorgcode;
	}
	
	/**
	 * ���ݲ������������ȡ�������
	 * @param strecode
	 * @return
	 * @throws ITFEBizException 
	 */
	public static String getTrecodeByFinorgcode(String sfinorgcode) throws ITFEBizException{
		SQLExecutor sqlExecutor=null;
		String strecode=null;
		try {
			sqlExecutor=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			String sql="SELECT * FROM TS_CONVERTFINORG WHERE S_FINORGCODE=?";
			sqlExecutor.addParam(sfinorgcode);
			List<TsConvertfinorgDto> list=(List<TsConvertfinorgDto>) sqlExecutor.runQueryCloseCon(sql, TsConvertfinorgDto.class).getDtoCollection(); 
			if(list!=null&&list.size()>0){
				strecode=list.get(0).getStrecode();
			}
		} catch (JAFDatabaseException e) {
			log.error("��ѯ������־�������ձ�ʱ�����쳣",e);
			throw new ITFEBizException("��ѯ������־�������ձ�ʱ�����쳣",e);
		}
		return strecode;
	}
	
	
	
	
}

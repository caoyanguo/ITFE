package com.cfcc.itfe.tipsfileparser;

import java.util.Map;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * �������������ļ������ӿ�
 * 
 * @author wangwz
 * 
 */
public interface ITipsFileOper {

	/**
	 * ���������ļ�����
	 * @param file
	 * @param sBookorgCode
	 * @param acctDate
	 * @param grpsrl
	 * @param biztype
	 * @param filekind �Ƿ���Ҫ���ܻ�У��
	 * @return
	 * @throws TASBizException
	 */
	public abstract MulitTableDto fileParser(String file,String sbookorgcode,String userid ,String biztype,String filekind,IDto paramdto,Map<String,TsBudgetsubjectDto> bmap) throws ITFEBizException;
	
	/**
	 * ���������ļ�����,����2202���Ľ���
	 * @param file
	 * @param sBookorgCode
	 * @param acctDate
	 * @param grpsrl
	 * @param biztype
	 * @param filekind �Ƿ���Ҫ���ܻ�У��
	 * @return
	 * @throws TASBizException
	 */
	public abstract MulitTableDto fileParser(String file,String sbookorgcode,String userid ,String biztype,String filekind,IDto paramdto,Map<String,TsBudgetsubjectDto> bmap,IDto idto) throws ITFEBizException;
}

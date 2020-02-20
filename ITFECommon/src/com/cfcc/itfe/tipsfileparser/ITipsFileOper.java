package com.cfcc.itfe.tipsfileparser;

import java.util.Map;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * 横向联网导入文件解析接口
 * 
 * @author wangwz
 * 
 */
public interface ITipsFileOper {

	/**
	 * 横联解析文件服务
	 * @param file
	 * @param sBookorgCode
	 * @param acctDate
	 * @param grpsrl
	 * @param biztype
	 * @param filekind 是否需要解密或校验
	 * @return
	 * @throws TASBizException
	 */
	public abstract MulitTableDto fileParser(String file,String sbookorgcode,String userid ,String biztype,String filekind,IDto paramdto,Map<String,TsBudgetsubjectDto> bmap) throws ITFEBizException;
	
	/**
	 * 横联解析文件服务,用于2202报文解析
	 * @param file
	 * @param sBookorgCode
	 * @param acctDate
	 * @param grpsrl
	 * @param biztype
	 * @param filekind 是否需要解密或校验
	 * @return
	 * @throws TASBizException
	 */
	public abstract MulitTableDto fileParser(String file,String sbookorgcode,String userid ,String biztype,String filekind,IDto paramdto,Map<String,TsBudgetsubjectDto> bmap,IDto idto) throws ITFEBizException;
}

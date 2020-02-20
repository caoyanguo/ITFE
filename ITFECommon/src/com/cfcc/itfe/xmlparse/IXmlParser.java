package com.cfcc.itfe.xmlparse;

import java.util.HashMap;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * 陕西财政统一银行接口XML解析接口
 * 
 * @author renqb
 * 
 */
public interface IXmlParser {

	/**
	 * 陕西财政统一银行接口XML解析服务
	 * @param file
	 * @return
	 * @throws ITFEBizException
	 */
	public abstract void dealMsg(HashMap map, String xmlString) throws ITFEBizException;
	
}

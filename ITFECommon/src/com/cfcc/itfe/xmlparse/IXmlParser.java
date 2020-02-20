package com.cfcc.itfe.xmlparse;

import java.util.HashMap;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * ��������ͳһ���нӿ�XML�����ӿ�
 * 
 * @author renqb
 * 
 */
public interface IXmlParser {

	/**
	 * ��������ͳһ���нӿ�XML��������
	 * @param file
	 * @return
	 * @throws ITFEBizException
	 */
	public abstract void dealMsg(HashMap map, String xmlString) throws ITFEBizException;
	
}

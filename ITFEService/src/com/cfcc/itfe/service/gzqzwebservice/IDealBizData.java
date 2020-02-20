package com.cfcc.itfe.service.gzqzwebservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import com.cfcc.itfe.exception.ITFEBizException;

/**
 * ���ݽӿ�
 * @author Administrator
 *
 */
@WebService
public interface IDealBizData {
	
	/**
	 * ������ȡ�ط�����ϵͳ˰Ʊ����˰��֧����ʵ��������֧����ȡ���������ݵ������ۺ�ǰ�á�
	 * @param fileStr   ҵ������(ǰ���ϴ����ļ�����)
	 * @param biztype   ҵ������
	 * @param paramStr ԭǰ�ý�����Ҫ¼��Ҫ�أ��ַ������룬�Զ��ŷָ�
	 * @return          �Ƿ�ɹ� 0-ʧ��  1-�ɹ�
	 * @throws ITFEBizException
	 */
	String readCommBizData(@WebParam(name = "fileStr") String fileStr,@WebParam(name = "biztype") String biztype,@WebParam(name = "paramStr") String paramStr,String fileName) throws ITFEBizException;
	
	/**
	 * ����ȫʡ����������֧ҵ����ˮ��ϸ����(˰Ʊ���籣�������������˰���롢�˿⡢֧��ҵ���)
	 * @param strecodeList ��������б�
	 * @param paramsList   ԭǰ�ý�����Ҫ¼��Ҫ�أ�ͨ��List����
	 * @param exptList     ��Ҫ����ҵ����ˮ��ϸ�б�
	 * @return             ҵ����ˮ��ϸ���ݣ�ͨ��list����
	 * @throws ITFEBizException
	 */
	String sendBizSeriData(@WebParam String params) throws ITFEBizException;
}

package com.cfcc.itfe.msgmanager.core;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.FileResultDto;

public interface IServiceManagerServer {

	/**
	 * ����ӿͻ��˴��͹������ļ�����
	 * 
	 * @param FileResultDto
	 *            fileResultDto �ļ����������DTO
	 * @param String
	 *            sorgcode ��������
	 * @param String
	 *            susercode �û�����
	 * @throws ITFEBizException
	 */
	public abstract void dealFileDto(FileResultDto fileResultDto ,String sorgcode, String susercode) throws ITFEBizException;

	/**
	 * ���ͱ��Ĵ���
	 * 
	 * @param String
	 *            sfilename �ļ�����
	 * @param String
	 *            sorgcode ��������
	 * @param String
	 *            spackno ����ˮ��
	 * @param String
	 *            msgno ���ı��
	 * @param String
	 *            commitdate ί������
	 * @param String
	 *            msgcontent �ļ�����(ֻ��ʵ���ʽ�[5101]��Ҫ)
	 * @param boolean isRepeat �Ƿ��ط�
	 * 
	 * @throws ITFEBizException
	 */
	public void sendMsg(String sfilename, String sorgcode, String spackno, String msgno, String commitdate, String msgcontent,boolean isRepeat) throws ITFEBizException;

}

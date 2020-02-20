package com.cfcc.itfe.component;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface WebServiceComponent {
	
	String sayHi(@WebParam(name = "text") String text);

	/**
	 * ���ݶ�ȡ
	 * ��ȡָ�����ı�ʶ�ŵ����ݡ�
	 * 
	 * @param finorgcode ������������
	 * @param reportdate �������� ��ʽYYYYMMDD
	 * @param biztype    ҵ������ �������ˮ��3139������˰Ʊ��3129�������ձ���3128��
	 * @param msgid      ���ı�ʶ��
	 * 
	 * @return �������ݼ���
	 * @throws Exception 
	 */
	byte[] readReport(
			@WebParam(name = "finorgcode") String finorgcode,
			@WebParam(name = "reportdate") String reportdate,
			@WebParam(name = "biztype") String biztype,
			@WebParam(name = "msgid") String msgid) throws Exception;
	
	/**
	 * ���ݶ�ȡ
	 * ��ȡָ������ĳ�������ڵı��ı�ʶ�ŵļ��ϣ�����û���յ�����ʱ��������������
	 * 
	 * @param finorgcode  ������������
	 * @param reportdate  �������� ��ʽYYYYMMDD
	 * @param biztype     ҵ������ �������ˮ��3139������˰Ʊ��3129�������ձ���3128��
	 * @throws Exception 
	 */
	List readReportMsgID(
			@WebParam(name = "finorgcode") String finorgcode,
			@WebParam(name = "reportdate") String reportdate,
			@WebParam(name = "biztype") String biztype) throws Exception;
	
	/**
	 * ���ݶ�ȡ(ָ���û�������)
	 * ��ȡָ������ĳ�������ڵı��ı�ʶ�ŵļ��ϣ�����û���յ�����ʱ��������������
	 * 
	 * @param finorgcode  ������������
	 * @param reportdate  �������� ��ʽYYYYMMDD
	 * @param biztype     ҵ������ �������ˮ��3139������˰Ʊ��3129�������ձ���3128��
	 * @param usercode    �û���
	 * @param password    ����
	 * @throws Exception 
	 */
	List readReportMsgIDByUser(
			@WebParam(name = "finorgcode") String finorgcode,
			@WebParam(name = "reportdate") String reportdate,
			@WebParam(name = "biztype") String biztype,
			@WebParam(name = "usercode") String usercode,
			@WebParam(name = "password") String password) throws Exception;
	
	/**
	 * ���ݶ�ȡ��ִ
	 * 
	 * @param finorgcode ������������
	 * @param reportdate �������� ��ʽYYYYMMDD
	 * @param biztype    ҵ������ �������ˮ��3139������˰Ʊ��3129�������ձ���3128��
	 * @param msgid      ���ı�ʶ��
	 * @param status     ����״̬ ���ɹ���0 ��ʧ�ܣ�1��
	 * 
	 */
	void readReportReceipt(
			@WebParam(name = "finorgcode") String finorgcode,
			@WebParam(name = "reportdate") String reportdate,
			@WebParam(name = "biztype") String biztype,
			@WebParam(name = "msgid") String msgid,
			@WebParam(name = "status") String status);
	
}

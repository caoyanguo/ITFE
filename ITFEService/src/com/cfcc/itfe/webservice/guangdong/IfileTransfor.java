package com.cfcc.itfe.webservice.guangdong;


import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * �ṩ���㶫���õ�webservice�ӿ�
 * @author 
 *
 */
@WebService
public interface IfileTransfor {
	
	/**
	 * ҵ�����ݷ���
	 * @param fileHandler �ļ�����
	 * @param biztype ҵ������
	 * @param paramStr ԭǰ�ý�����Ҫ¼���Ҫ�أ��Զ��ŷָ�
	 * @param fileName �ļ�����
	 * @param treCode �������
	 * @return
	 * @throws Exception 
	 */
	public String sendCommBizData(@WebParam(name="fileContent") String fileContent,
								  @WebParam(name="biztype") String biztype,
								  @WebParam(name="paramStr") String paramStr,
								  @WebParam(name="fileName") String fileName,
								  @WebParam(name="treCode") String treCode);
	/**
	 * ��������״̬��ѯ(��ʱ���ṩ)
	 * @param fileName �ļ�����
	 * @param treCode �������
	 * @return
	 */
	/*
	public String queryCommBizData(@WebParam(name="fileName")String fileName,
								   @WebParam(name="treCode")String treCode);
    */
	
	/**
	 * ҵ����ˮ��ϸ���ݵ��� 
	 * @param billType ҵ������
	 * @param params ԭ�������ݵ��������ѯ���������ַ������룬�Զ��ŷָ���<br>
	 * 		���ηֱ�Ϊ������� ���������ڣ�Ԥ�����࣬Ͻ����־������ϼƱ�־�������ڱ�־�����ջ��ش���
	 * @param separator �ָ��� 
	 * @return
	 */
	public String readBizSeriData(@WebParam(name="billType") String billType,
								  @WebParam(name="params") String params,
								  @WebParam(name="separator") String separator);
}

/**
 * 
 */
package com.cfcc.itfe.tbs;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author Administrator
 *
 */

@WebService
public interface ITbs {

	/**
	 * ����
	 * @param text
	 * @return
	 * @throws ITFEBizException 
	 */
	String sayHi(@WebParam(name = "text") String text);
	
	/**��ȡƾ֤
	 * @param admDivCode��������
	 * @param vtCode	ƾ֤���
	 * @param treCode	�������
	 * @param vouDate	ƾ֤����
	 * @return	����ƾ֤���ݣ���Base64����
	 * @throws ITFEBizException 
	 */
	String readVoucher(@WebParam(name = "admDivCode") String admDivCode,
			           @WebParam(name = "vtCode") String vtCode,
			           @WebParam(name = "treCode") String treCode, 
			           @WebParam(name = "vouDate") String vouDate) ;
	/**���¶�ȡƾ֤
	 * @param admDivCode��������
	 * @param vtCode	ƾ֤���
	 * @param treCode	�������
	 * @param vouDate	ƾ֤����
	 * @return	����ƾ֤���ݣ���Base64����
	 * @throws ITFEBizException 
	 */
	String readVoucherAgain(@WebParam(name = "admDivCode") String admDivCode,
			           @WebParam(name = "vtCode") String vtCode,
			           @WebParam(name = "treCode") String treCode, 
			           @WebParam(name = "vouDate") String vouDate) ;
	/**ƾ֤ǩ��
	  @param admDivCode ��������
	 * @param vtCode	ƾ֤���
	 * @param treCode	�������
	 * @param vouDate	ƾ֤����
	 * @param vouNo		ƾ֤���
	 * @return ���ز������ ����(0:�ɹ���-1:ʧ��)
	 * @throws ITFEBizException 
	 */
	String confirmVoucher(@WebParam(name = "admDivCode") String admDivCode,
				           @WebParam(name = "vtCode") String vtCode,
				           @WebParam(name = "treCode") String treCode, 
				           @WebParam(name = "vouDate") String vouDate, 
				           @WebParam(name = "vouNo") String vouNo) ;

	/**ƾ֤�˻�
	  @param admDivCode ��������
	 * @param vtCode	ƾ֤���
	 * @param treCode	�������
	 * @param vouDate	ƾ֤����
	 * @param vouNo		ƾ֤���
	 * @param errMsg	ʧ��ԭ��
	 * @return	���ز������ ����(0:�ɹ���-1:ʧ��)
	 * @throws ITFEBizException 
	 */
	String returnVoucherBack(@WebParam(name = "admDivCode") String admDivCode,
					           @WebParam(name = "vtCode") String vtCode,
					           @WebParam(name = "treCode") String treCode, 
					           @WebParam(name = "vouDate") String vouDate, 
					           @WebParam(name = "vouNo") String vouNo,
					           @WebParam(name = "errMsg") String errMsg) ;
	
	/**����ƾ֤
	  @param admDivCode ��������
	 * @param vtCode	ƾ֤���
	 * @param treCode	�������
	 * @param vouDate	ƾ֤����
	 * @param vouNo		ƾ֤���
	 * @return  ���ز������ ����(0:�ɹ���-1:ʧ��)
	 * @throws ITFEBizException 
	 */
	String sendVoucher(@WebParam(name = "admDivCode") String admDivCode,
					   @WebParam(name = "vtCode") String vtCode,
					   @WebParam(name = "treCode") String treCode, 
					   @WebParam(name = "vouDate") String vouDate, 
					   @WebParam(name = "vouNo") String vouNo) ;
	
	/**ƾ֤״̬ͬ��
	  @param admDivCode ��������
	 * @param vtCode	ƾ֤���
	 * @param treCode	�������
	 * @param vouDate	ƾ֤����
	 * @param vouNo		ƾ֤���
	 * @return	ƾ֤״̬
	 * @throws ITFEBizException 
	 */
	String synchronousVoucherStatus(@WebParam(name = "admDivCode") String admDivCode,
							   @WebParam(name = "vtCode") String vtCode,
							   @WebParam(name = "treCode") String treCode, 
							   @WebParam(name = "vouDate") String vouDate, 
							   @WebParam(name = "vouNo") String vouNo) ;
	
	
	/**
	 * ����������
	 * @throws ITFEBizException
	 */
	void testConnect(@WebParam(name = "desOrgCode") String desOrgCode,@WebParam(name = "vouDate") String vouDate,@WebParam(name = "orgCode") String orgCode) ;
	/**
	 * �ʽ�����ӿ�
	 * @param vtCode	ƾ֤���
	 * @param treCode	�������
	 * @param vouDate	ƾ֤����
	 * @param vouNo		ƾ֤���
	 * @throws ITFEBizException
	 */
	String fundLiquidation(@WebParam(name = "admDivCode") String admDivCode,
			   @WebParam(name = "vtCode") String vtCode,
			   @WebParam(name = "treCode") String treCode, 
			   @WebParam(name = "vouDate") String vouDate, 
			   @WebParam(name = "vouNo") String vouNo) ;
	/**
	 * �˿�����ӿ�
	 * @param fileContent
	 * @return
	 * @throws ITFEBizException
	 */
//	String fundLiquidationDwbk(@WebParam(name = "fileContent") String fileContent) throws ITFEBizException;
	
	/**
	 * 
	 * @param orgCode	��������
	 * @param userCode	�û�����
	 * @param password	�û�����
	 * @param operate	0:��½ 1:�˳�
	 * @return
	 * @throws ITFEBizException
	 */
	String userLoginOrOut(@WebParam(name = "orgCode")String orgCode,@WebParam(name = "password")String password,@WebParam(name = "operate")String operate) ;
	
	
	/**
	 * 
	 * @param voucherType �������ͱ��
	 * @param fileContent	�ļ�����
	 * @return
	 * @throws ITFEBizException
	 */
	String genVoucherTKXml(@WebParam(name = "voucherType") String voucherType,@WebParam(name = "fileContent") String fileContent) ;
	
	/**
	 * tbs�Ĳ���ͬ����ITFE(��Ŀ���룬���˴���)
	 * @param orgCode �����������
	 * @param paraType �����ǿ�Ŀ���뻹�Ƿ��˴��� 0--��Ŀ����   1--���˴���
	 * @param paramContent ͬ��������
	 * @return �Ƿ�ɹ� 0:�����ɹ� -1:����ʧ��
	 */
	String synchronousParam(@WebParam(name = "orgCode")String orgCode,@WebParam(name = "paraType") String paraType,@WebParam(name = "paramContent") String paramContent);
	
	/**
	 * tbs�ı�������ͬ����ITFE(���룬֧��)
	 * @param orgCode �����������
	 * @param paraType ����һ��ռλ��Ϊ0
	 * @param paramContent ͬ��������
	 * @return �Ƿ�ɹ� 0:�����ɹ� -1:����ʧ��
	 */
	String synchronousIncomeDayRpt(@WebParam(name = "orgCode")String orgCode,@WebParam(name = "paraType") String paraType,@WebParam(name = "paramContent") String paramContent);
	
	/**
	 * tbs�Ŀ��ͬ����ITFE(���)
	 * @param orgCode �����������
	 * @param paraType ����һ��ռλ��Ϊ0
	 * @param paramContent ͬ��������
	 * @return �Ƿ�ɹ� 0:�����ɹ� -1:����ʧ��
	 */
	String synchronousStockRpt(@WebParam(name = "orgCode")String orgCode,@WebParam(name = "checkDate") String paraType,@WebParam(name = "paramContent") String paramContent);
	
	/**
	 * ��ֽ�����˽��ͬ����tbs
	 * @param strecode �������
	 * @param paraType ��������
	 * @param checkNum ���˴���
	 * @return
	 */
	String synchronousAcctBalance(@WebParam(name = "strecode")String strecode,@WebParam(name = "checkDate") String checkDate,@WebParam(name = "checkNum")String checkNum);
	
	/**
	 * tbs������Ϣ��¼
	 * @param msgNo ���ܱ�Ż��ı�Ż�ƾ֤���
	 * @param errorMsg ������Ϣ
	 */
	void tbsErrorLog(@WebParam(name = "msgNo")String msgNo,@WebParam(name = "errorMsg")String errorMsg);
	
	/**
	 * ����ƾ֤״̬
	 * @param admDivCode ��������
	 * @param vtCode     ƾ֤����
	 * @param treCode    �������
	 * @param vouDate    ��������
	 * @param vouNo      ƾ֤���
	 * @return
	 */
	String updateVoucherStatus(@WebParam(name = "admDivCode") String admDivCode,
	           @WebParam(name = "vtCode") String vtCode,
	           @WebParam(name = "treCode") String treCode, 
	           @WebParam(name = "vouDate") String vouDate, 
	           @WebParam(name = "vouNo") String vouNo);
}

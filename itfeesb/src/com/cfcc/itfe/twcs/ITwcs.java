/**
 * 
 */
package com.cfcc.itfe.twcs;

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
public interface ITwcs {

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
	 * @param vtCode	ƾ֤���
	 * @param treCode	�������
	 * @param vouDate	ƾ֤����
	 * @return ���ز������ ����(0:�ɹ���-1:ʧ��)
	 * @throws ITFEBizException 
	 */
	String updateConPayState(
				           @WebParam(name = "vtCode") String vtCode,
				           @WebParam(name = "treCode") String treCode, 
				           @WebParam(name = "vouDate") String vouDate) ;

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
	

	/**������֧���ϵͳ������״̬������ֽ����״̬��״̬���º󣬲��ܽ�����һ������
	 * @param vtCode	ƾ֤���
	 * @param msgInfo  ��������
	 * @return	�������
	 * @throws ITFEBizException 
	 */
	String updateItfeVoucherInfo(@WebParam(name = "vtCode") String vtCode,
							   @WebParam(name = "msgInfo") String msgInfo) ;
	
	String synchronousVoucherStatus(
			   @WebParam(name = "vtCode") String vtCode, 
			   @WebParam(name = "dealsInfo") String dealsInfo) ;
	
	
	/**
	 * ����������
	 * @throws ITFEBizException
	 */
	void testConnect(@WebParam(name = "desOrgCode") String desOrgCode,@WebParam(name = "vouDate") String vouDate,@WebParam(name = "orgCode") String orgCode) ;
	
	
}

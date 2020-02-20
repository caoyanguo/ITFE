package com.cfcc.itfe.webservice;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.util.CommonUtil;
@SuppressWarnings("unchecked")
public class VoucherService {
	
	private static Log logger = LogFactory.getLog(VoucherService.class);
	private static String url = ITFECommonConstant.WEBSERVICE_URL;
	private static String pdfurl = ITFECommonConstant.PDFDERVICE_URL;
	// �����������
	private static Service service;
	private static Call call;
	
	public VoucherService () throws ITFEBizException{
		
		try {
			service=new Service();
			call = (Call) service.createCall();
		} catch (ServiceException e) {
			throw new ITFEBizException("��ʼ��WebServiceʱ�����쳣��",e);
		}
		
		// ����Զ�̵�ַ
		call.setTargetEndpointAddress(url+"/realware/services/AsspCBankService");
		
		
	}
	
	
	/*
	 *	5.2.2.9 δǩ��ƾ֤��ȡ
	 *	1	admDivCode	String	����������6λ������
		2	stYear	int	ҵ����ȣ�4λ����
		3	vtCode	String	ƾ֤���ԣ�4λ�ַ�
		4	voucherCount	int	һ�ζ�ȡ��ƾ֤���������100
	 */
	public byte[] readVoucher( String admDivCode, int stYear, String vtCode) throws ITFEBizException{
		call.setOperationName("readVoucherAgain");
		// ���ýӿ�
		byte[] result = null;
		try {
			
			result = (byte[]) call.invoke(new Object[] {"001", admDivCode,  stYear,vtCode,  100});
			logger.debug("ƾ֤��ȡ�����ɹ�:  ��������  "+admDivCode+" ƾ֤����  "+vtCode);

		} catch (RemoteException e) {
			throw new ITFEBizException("δǩ��ƾ֤��ȡ�����쳣"+e.getMessage(),e);
		}catch(Exception e){			
			throw new ITFEBizException("δǩ��ƾ֤��ȡ�����쳣"+e.getMessage(),e);
		}
		if(result!=null)
			logger.debug("δǩ��ƾ֤��ȡ����");
		else
			logger.debug("====================δǩ��ƾ֤��ȡ����,δ��ȡ�����ݣ�====================");
		return result;
	}
	
	/**
	 * ƾ֤ǩ��
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param voucherNo
	 * @return
	 */
	public String confirmVoucher(String certID,String admDivCode, int stYear, String vtCode, String[] voucherNo) {
		logger.debug("ƾ֤ǩ�ճɹ�����=="+voucherNo.length);
		String err=null;
		call.setOperationName("confirmVoucher");
		try {
			logger.debug("ƾ֤ǩ�ճɹ�����ƾ֤�ⷽ��==========confirmVoucher(001,"+admDivCode+","+stYear+","+vtCode+","+Arrays.toString(voucherNo)+")");
			call.invoke(new Object[] {"001", admDivCode,  stYear,vtCode,  voucherNo});
			logger.debug("ƾ֤ǩ�ղ����ɹ�:  ƾ֤�б�   voucherNo==="+Arrays.toString(voucherNo));
			
		} catch (RemoteException e) {
			logger.debug("ƾ֤ǩ�ճɹ�ʱ�����쳣ƾ֤�б� voucherNo==="+Arrays.toString(voucherNo),e);
			err="ǩ�ճɹ��쳣: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());	
			
		}return err;
	}
	
	/**
	 * ƾ֤ǩ��ʧ��
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param voucherlist
	 * @param errMsg
	 * @return
	 */
	public String  confirmVoucherfail(String certID,String admDivCode, int stYear, String vtCode, String[] voucherlist, String[] errMsg){
		String err=null;
		String[] suberrMsg = new String[errMsg.length];
		for(int i = 0; i < errMsg.length; i++){
			if(errMsg[i].getBytes().length > 200){
				suberrMsg[i] = CommonUtil.subString(errMsg[i], 190);
			}else{
				suberrMsg[i] = errMsg[i];
			}
		}
		try {
			logger.debug("ƾ֤ǩ��ʧ�ܵ���ƾ֤�ⷽ��debug==========confirmVoucherFail(001,"+admDivCode+","+stYear+","+vtCode+",'"+Arrays.toString(voucherlist)+"',"+Arrays.toString(suberrMsg)+")");
			call.setOperationName("confirmVoucherFail");
			call.invoke(new Object[] {"001",admDivCode, stYear, vtCode, voucherlist, suberrMsg});
			logger.debug("ƾ֤ǩ��ʧ�ܲ����ɹ�: ƾ֤�б�:voucherNo==="+Arrays.toString(voucherlist)+"ƾ֤ǩ��ʧ��ԭ���б�:"+Arrays.toString(suberrMsg));
		} catch (RemoteException e) {
			logger.debug("ƾ֤ǩ��ʧ�ܳ����쳣�� voucherNo==="+Arrays.toString(voucherlist),e);
			err="ǩ��ʧ���쳣:"+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());	
		}return err;
	}
	//ƾ֤д��
	public Map<String,String> WirteVoucher (String admDivCode, int stYear, String vtCode, byte[] voucher,String desorgcode) throws ITFEBizException{
		logger.debug("ƾ֤д�� ����������="+admDivCode +" ƾ֤����="+vtCode);
		admDivCode= admDivCode.trim();
		vtCode = vtCode.trim();
		call.setOperationName("writeVoucher");	
		Map map = null;
		try {
			map = (Map)call.invoke(new Object[] { "001",admDivCode,  stYear,vtCode,voucher,"001", desorgcode});
		} catch (RemoteException e) {
			throw new ITFEBizException("ƾ֤д��ʱ�����쳣��",e);
		}
		return map;
	}
	/**
	 * ƾ֤�˻�
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param voucherNo
	 * @param errMsg
	 * @return
	 */
	public String returnVoucherBack(String certID,String admDivCode, int stYear, String vtCode, String[] voucherNo, String[] errMsg) {
		logger.debug("ƾ֤ǩ���˻�����=="+voucherNo.length);
		String err=null;
		call.setOperationName("returnVoucher");	
		try {
			logger.debug("ƾ֤ǩ���˻ص���ƾ֤�ⷽ��debug==========returnVoucher(001,"+admDivCode+","+stYear+","+vtCode+","+Arrays.toString(voucherNo)+","+Arrays.toString(errMsg)+")");
			call.invoke(new Object[] {"001",admDivCode,  stYear,vtCode,  voucherNo,errMsg});
			logger.debug("ƾ֤�˻ز����ɹ��� ƾ֤�б� voucherNo==="+Arrays.toString(voucherNo)+"ƾ֤�˻ر��ʧ��ԭ���б� voucherNo==="+Arrays.toString(errMsg));
		} catch (RemoteException e) {
			logger.debug("ƾ֤�˻س����쳣voucherNo==="+Arrays.toString(voucherNo),e);
			err="�˻��쳣: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());	
		}return err;
	}
	
	/**
	 * ƾ֤����
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param voucherNo
	 * @return
	 */
	public String discardVoucher(String certID, String admDivCode, int stYear, String vtCode, String[] voucherNo) {

		logger.debug("voucherNo==="+Arrays.toString(voucherNo));
		String err=null;
		call.setOperationName("discardVoucher");	
		try {
			logger.debug("ƾ֤���ϵ���ƾ֤�ⷽ��debug==========discardVoucher(001,"+admDivCode+","+stYear+","+vtCode+","+Arrays.toString(voucherNo)+")");
			call.invoke(new Object[] { "001",admDivCode,  stYear,vtCode,  voucherNo});
		} catch (RemoteException e) {
			logger.error("ƾ֤����ʱ�����쳣��",e);
			err="ƾ֤�����쳣��"+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());	
		}return err;
	} 

	
	/**
	 * ƾ֤ǩ��
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param vouchers
	 * @param Stamp
	 * @param voucherNo
	 * @param stampName
	 * @return
	 */
	public String signStampByNos (String certID ,String admDivCode, int stYear, String vtCode, byte[] vouchers, String Stamp,String voucherNo,String stampName){
			String err=null;
			logger.debug("ƾ֤д�� ����������="+admDivCode +" �û�֤��="+certID +" ƾ֤����="+vtCode+ " ������="+stampName);
			call.setOperationName("signStampByNos");	
			try {
				logger.debug("ƾ֤ǩ�µ���ƾ֤�ⷽ��debug==========signStampByNos("+certID+","+admDivCode+","+stYear+","+vtCode+","+vouchers+","+Stamp+")");
				call.invoke(new Object[] { certID,admDivCode,  stYear,vtCode,vouchers,Stamp});
				logger.debug("ƾ֤ǩ�²����ɹ�:����������="+admDivCode +" ƾ֤����="+vtCode+" ƾ֤���="+voucherNo);

			} catch (RemoteException e) {
				logger.error("ƾ֤д�� ����������="+admDivCode +" ƾ֤����="+vtCode+" ƾ֤���="+voucherNo+"�����쳣��",e);
				err="ǩ���쳣: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());	
			}
			return err;
		
	}
	

	/**
	 * ����ƾ֤
	 * @param certID
	 * @param admDivCode
	 * @param decOrgType
	 * @param stYear
	 * @param vtCode
	 * @param voucherNo
	 * @return
	 */
	public String sendVoucher(String certID, String admDivCode, String decOrgType,int stYear, String vtCode, String[] voucherNo){
		logger.debug("ƾ֤д�� ����������="+admDivCode +" �û�֤��="+certID +" ƾ֤����="+vtCode+" ���ջ���="+decOrgType);
		call.setOperationName("sendVoucher");	
		String err=null;
		try {
			logger.debug("ƾ֤���͵���ƾ֤�ⷽ��debug==========sendVoucher(001"+","+admDivCode+",001,"+decOrgType+","+stYear+","+vtCode+","+Arrays.toString(voucherNo)+")");
			call.invoke(new Object[] { "001",admDivCode,"001",decOrgType,stYear,vtCode,voucherNo});
			logger.debug("ƾ֤�ص������ɹ���  ƾ֤�б� voucherNo==="+Arrays.toString(voucherNo));
		} catch (RemoteException e) {
			logger.error("���ͻص�ƾ֤�б�����쳣: voucherNo==="+Arrays.toString(voucherNo),e);
			err="����ƾ֤�쳣: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());			
		}
		return err;
	}
	
	/**
	 * ����ȫ�汾ƾ֤
	 * @param certID
	 * @param admDivCode
	 * @param decOrgType
	 * @param stYear
	 * @param vtCode
	 * @param voucherNo
	 * @return
	 */
	public String sendVoucherFullSigns(String certID, String admDivCode, String decOrgType,int stYear, String vtCode, String[] voucherNo) {
		logger.debug("ƾ֤д�� ����������="+admDivCode +" �û�֤��="+certID +" ƾ֤����="+vtCode+" ���ջ���="+decOrgType);
		call.setOperationName("sendVoucherFullSigns");	
		String err=null;
		try {
			logger.debug("ƾ֤����ȫ�汾����ƾ֤�ⷽ��debug==========sendVoucherFullSigns(001"+","+admDivCode+",001,"+decOrgType+","+stYear+","+vtCode+","+Arrays.toString(voucherNo)+")");
			call.invoke(new Object[] { "001",admDivCode,"001",decOrgType,stYear,vtCode,voucherNo});
			logger.debug("ƾ֤�ص������ɹ���  ƾ֤�б� voucherNo==="+Arrays.toString(voucherNo));
		} catch (RemoteException e) {
			logger.error("���ͻص�ƾ֤�б�ʱ�����쳣: voucherNo==="+Arrays.toString(voucherNo),e);
			err="����ƾ֤�쳣: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());
			
		}
		return err;
	}
	
	/**
	 * �ѻص�ƾ֤״̬������ѯ
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param voucherNos
	 * @return
	 */
	public Map batchQuerySentVoucherStatus (String certID,String admDivCode, int stYear, String vtCode, String[] voucherNos){
		logger.debug("ƾ֤д�� ����������="+admDivCode +" ƾ֤����="+vtCode);		
		call.setOperationName("batchQuerySentVoucherStatus");	
		Map map = null;
		try {
			map = (Map)call.invoke(new Object[] { "001",admDivCode,stYear,vtCode,voucherNos});
			if(map!=null){
				logger.debug("ƾ֤��ѯ�ѻص�ƾ֤״̬�����ɹ�:voucherNo==="+Arrays.toString(voucherNos));
			}
		} catch (RemoteException e) {
			logger.error("ƾ֤״̬��ѯ�����쳣:voucherNo==="+Arrays.toString(voucherNos),e);
			String err="�鿴ƾ֤״̬�쳣: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());
			map=new HashMap();
			map.put("-2", err);			
		}
		return map;
	}
	
	/**
	 * ָ��������ѯƾ֤״̬�ӿ�
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param voucherNos
	 * @return
	 */
	public Map queryVoucherStatusByOrgType(String certID,String admDivCode, int stYear, String vtCode, String[] voucherNos,String decOrgType){
		logger.debug("ƾ֤д�� ����������="+admDivCode +" ƾ֤����="+vtCode+" ���ջ���="+decOrgType);		
		call.setOperationName("queryVoucherStatusByOrgType");	
		Map map = null;
		try {
			map = (Map)call.invoke(new Object[] { "001",admDivCode,stYear,vtCode,voucherNos,decOrgType});
			if(map!=null){
				logger.debug("ƾ֤��ѯ�ѻص�ƾ֤״̬�����ɹ���ƾ֤�б� voucherNo==="+Arrays.toString(voucherNos));
			}
		} catch (RemoteException e) {
			logger.error("�ѷ���ƾ֤������ѯ�б� voucherNo==="+Arrays.toString(voucherNos)+"ʱ�����쳣:",e);
			String err="�鿴ƾ֤״̬�쳣: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());
			map=new HashMap();
			map.put("-100", err);			
		}
		return map;
	}
	
	/**
	 * ����ָ��λ��ƾ֤ǩ��
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param voucherNo
	 * @param stampPosition
	 * @return
	 */
	public String cancelStampWithPosition(String certID, String admDivCode,
			int stYear, String vtCode, String voucherNo, String stampPosition) {
		logger.debug("ƾ֤д�� ����������="+admDivCode +" �û�֤��="+certID +" ƾ֤����="+vtCode);
		call.setOperationName("cancelStampWithPosition");	
		String err=null;
		try {
			logger.debug("ƾ֤ǩ�³�������ƾ֤�ⷽ��debug==========cancelStampWithPosition("+certID+","+admDivCode+","+stYear+","+vtCode+","+voucherNo+","+stampPosition+")");
			call.invoke(new Object[] { certID,admDivCode,stYear,vtCode,voucherNo,stampPosition});
			logger.debug("ƾ֤����ǩ�²����ɹ���	ƾ֤�б� voucherNo==="+voucherNo);
			
		} catch (RemoteException e) {
			logger.error("����ǩ��ƾ֤�б� voucherNo==="+voucherNo+"ʱ�����쳣:",e);
			err="ǩ�³����쳣: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());			
		}return err;
	}
	
	/**
	 * �ͻ���ǩ��
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param signeVoucher
	 * @param stampname
	 * @return
	 */
	public String saveStampVoucher(String certID,String admDivCode,int stYear,String vtCode,byte[] signeVoucher,String stampname) {
		logger.debug("ƾ֤д�� ����������="+admDivCode  +" ƾ֤����="+vtCode+" �û�֤��="+certID+"������"+stampname);
		call.setOperationName("saveStampVoucher");
		String err=null;
		try {
		  logger.debug("�ͻ���ǩ�µ���ƾ֤�ⷽ��debug==========saveStampVoucher("+certID+","+admDivCode+","+stYear+","+vtCode+","+signeVoucher+")");
		  call.invoke(new Object[] { certID,admDivCode,stYear,vtCode,signeVoucher});
		  logger.debug("======ƾ֤"+stampname+"�ͻ���ǩ�²����ɹ�=====");
		} catch (RemoteException e) {
			logger.error(stampname+"�ͻ���ǩ��ʱ�����쳣:",e);
			err="ǩ���쳣: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());	
		}return err;
	}
	
	/**
	 * ��ѯƾ֤��ӡ����
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param VoucherNo
	 * @return
	 */
	public String queryVoucherPrintCount(String certID, String admDivCode, int stYear, String vtCode, String VoucherNo){
		logger.debug("ƾ֤д�� ����������="+admDivCode +" �û�֤��="+certID +" ƾ֤����="+vtCode+" ƾ֤���"+VoucherNo);
		call.setOperationName("queryVoucherPrintCount");
		String err=null;
		try {
			err=call.invoke(new Object[] {"001", admDivCode,  stYear,vtCode,  VoucherNo})+"";
			
			logger.debug("��ѯƾ֤��ӡ���������ɹ�:voucherNo==="+VoucherNo);
			
		} catch (RemoteException e) {
			logger.debug("��ѯƾ֤��ӡ����ʱ�����쳣:"+e);
			err="��ѯ�쳣: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());	
		}
		return err;
	}
	
	/**
	 * ��ѯ����ƾ֤��ĸ���λ�ü�����
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @return
	 * @throws ITFEBizException
	 */
	public Map queryStampPositionWithName(String certID, String admDivCode, int stYear, String vtCode)throws ITFEBizException{
		logger.debug("��ѯƾ֤����λ���б�="+admDivCode +" �û�֤��="+certID +" ƾ֤����="+vtCode);
		call.setOperationName("queryStampPositionWithName");
		Map positionMap = null;

		try {
			positionMap=(Map)call.invoke(new Object[] {"001", admDivCode,  stYear,vtCode});
		} catch (RemoteException e) {
			logger.debug("��ѯ����ƾ֤��ĸ���λ�ü�����: ",e);
		}
		return positionMap;
	}
	
	/**
	 * д�벻��λ�÷�����ǩ����ƾ֤
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param voucher
	 * @return
	 * @throws ITFEBizException
	 */
	public String signWithoutPosition (String certID ,String admDivCode, int stYear, String vtCode, byte[] voucher) throws ITFEBizException{
		call.setOperationName("signWithoutPosition");	
		String err=null;
		byte[] result = null;
		
		try {
			logger.debug("д�벻��λ�÷�����ǩ��ƾ֤����ƾ֤�ⷽ��debug==========signWithoutPosition(001"+","+admDivCode+","+stYear+","+vtCode+","+voucher+")");
			result=(byte[]) call.invoke(new Object[] { "001",admDivCode,stYear,vtCode,voucher});
			if(result!=null){
				try {
					err=new String(result,"gbk");
				} catch (UnsupportedEncodingException e) {
					logger.error(e);
					err="ƾ֤ǩ���쳣: ��ȡǩ�����Ĵ���";
				}
			}
			logger.debug("ƾ֤д�벻��λ��ǩ��ƾ֤�����ɹ�");			
		} catch (RemoteException e) {
			logger.error("ƾ֤д�벻��λ��ǩ��ƾ֤�����ɹ�ʱ�����쳣��",e);
			err="ƾ֤ǩ���쳣: "+(e.getMessage().indexOf("Exception:")==-1?e.getMessage():(e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length())));
			return err;
		}
		return err;
	}
	
	
	/**
	 * д��ͻ���ǩ����ƾ֤
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param voucher
	 * @return
	 * @throws ITFEBizException
	 */
	public String saveSignVoucher (String certID ,String admDivCode, int stYear, String vtCode, byte[] voucher) throws ITFEBizException{
		call.setOperationName("saveSignVoucher");	
		String err=null;		
		try {
			logger.debug("д��ͻ���ǩ��ƾ֤����ƾ֤�ⷽ��debug==========saveSignVoucher("+certID+","+admDivCode+","+stYear+","+vtCode+","+voucher+")");
			call.invoke(new Object[] { certID,admDivCode,stYear,vtCode,voucher});
			logger.debug("д��ͻ���ǩ����ƾ֤�����ɹ�");			
		} catch (RemoteException e) {
			logger.error("д��ͻ���ǩ����ƾ֤���������쳣��",e);
			err="ƾ֤ǩ���쳣: "+(e.getMessage().indexOf("Exception:")==-1?e.getMessage():(e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length())));
			return err;
		}
		return err;
	}
	
	/**
	 * д��ָ��λ�÷�����ǩ����ƾ֤
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param vouchers
	 * @param Stamp
	 * @return
	 */
	public String signByNos (String certID ,String admDivCode, int stYear, String vtCode, byte[] vouchers, String Stamp){
		call.setOperationName("signByNos");	
		String err=null;	
		try {
			logger.debug("д��ָ��λ�÷�����ǩ��ƾ֤����ƾ֤�ⷽ��debug==========signByNos(001"+","+admDivCode+","+stYear+","+vtCode+","+vouchers+","+Stamp+")");
			call.invoke(new Object[] {"001",admDivCode,stYear,vtCode,vouchers,Stamp});			
			logger.debug("ƾ֤д���λ��ǩ��ƾ֤�����ɹ�");			
		} catch (RemoteException e) {
			logger.error("ƾ֤д�벻��λ��ǩ��ƾ֤�����ɹ�ʱ�����쳣��",e);
			err="ƾ֤ǩ���쳣: "+(e.getMessage().indexOf("Exception:")==-1?e.getMessage():(e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length())));
			return err;
		}
		return err;
	}
	
	/**
	 * д��ָ��λ�÷�����ǩ����ƾ֤
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param vouchers
	 * @param Stamp
	 * @return
	 */
	public String signByNosAndSend(String certID ,String admDivCode, int stYear, String vtCode, byte[] vouchers, String Stamp,String srcOrgType,String desOrgType){
		call.setOperationName("signByNosAndSend");	
		String err=null;	
		try {
			logger.debug("д��ָ��λ�÷�����ǩ����ƾ֤����ƾ֤�ⷽ��debug==========signByNosAndSend("+certID+","+admDivCode+","+stYear+","+vtCode+","+vouchers+","+srcOrgType+","+desOrgType+")");
			call.invoke(new Object[] {certID,admDivCode,stYear,vtCode,vouchers,Stamp,srcOrgType,desOrgType});			
			logger.debug("ƾ֤д���λ��ǩ��ƾ֤�����ɹ�");			
		} catch (RemoteException e) {
			logger.error("ƾ֤д�벻��λ��ǩ��ƾ֤�����ɹ�ʱ�����쳣��",e);
			err="ƾ֤ǩ���쳣: "+(e.getMessage().indexOf("Exception:")==-1?e.getMessage():(e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length())));
			return err;
		}
		return err;
	}
	

	/**
	 * ����ƾ֤����(��ɫͨ��)
	 * @param certID
	 * @param admDivCode
	 * @param srcOrgType
	 * @param decOrgType
	 * @param stYear
	 * @param bytes
	 * @throws ITFEBizException
	 */
	public void sendData(String certID, String admDivCode, String srcOrgType, String decOrgType, int stYear, byte[] bytes) throws ITFEBizException{		
		call.setOperationName("sendData");	
		try {
			logger.debug("������ɫͨ������ƾ֤�ⷽ��debug==========sendData("+certID+","+admDivCode+","+srcOrgType+","+decOrgType+","+stYear+","+bytes+")");
			call.invoke(new Object[] {certID, admDivCode, srcOrgType,decOrgType,stYear,bytes});			
			logger.debug("����ƾ֤�����ɹ�");			
		} catch (RemoteException e) {
			logger.error("���͸��������쳣��",e);	
			throw new ITFEBizException("����ƾ֤���������쳣\t\n"+e.getMessage());
		}
	}
	
	/**
	 * ����ƾ֤����(��ɫͨ��)
	 * @param certID
	 * @param stYear
	 * @param admDivCode
	 * @return
	 * @throws ITFEBizException
	 */
	public byte[] getData(String certID, int stYear, String admDivCode)throws ITFEBizException{		
		call.setOperationName("getData");
		byte[] result = null;
		try {
			logger.debug("������ɫͨ������ƾ֤�ⷽ��debug==========getData("+certID+","+stYear+","+admDivCode+")");
			result = (byte[]) call.invoke(new Object[] {certID,stYear,admDivCode});			
			logger.debug("����ƾ֤�����ɹ�");			
		} catch (RemoteException e) {
			logger.error("���ո��������쳣��",e);	
			throw new ITFEBizException("����ƾ֤���������쳣\t\n"+e.getMessage());
		}
		return result;
	}
	
	public byte[] exportPDF(String certID, String admDivCode, int stYear, String vt_code, int pageNo, String voucher_no)throws ITFEBizException{		
		byte[] result = null;
		try {
			Call pdfcall = (Call) new Service().createCall();
			pdfcall.setTargetEndpointAddress(pdfurl);
			pdfcall.setOperationName("exportPDF");
			logger.debug("����PDF�ļ�����ƾ֤�ⷽ��debug==========exportPDF("+certID+","+admDivCode+","+stYear+","+vt_code+","+pageNo+","+voucher_no+")");
			result = (byte[]) pdfcall.invoke(new Object[] {certID,admDivCode,stYear,vt_code,pageNo,voucher_no});			
			logger.debug("����PDF�ļ��ɹ�");			
		} catch (Exception e) {
			logger.error("����PDF�ļ������쳣:",e);	
			throw new ITFEBizException("����PDF�ļ������쳣\t\n"+e.getMessage());
		}
		return result;
	}
	/**
	 * ������ѯƾ֤�ص�״̬
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param voucherNos
	 * @return
	 * @throws ITFEBizException
	 */
	public Map<String,Object[]> batchQueryAllVoucherStatus(String certID,String admDivCode,int stYear,String vtCode,String[] voucherNos) throws ITFEBizException{
		logger.debug("ƾ֤д�� ����������="+admDivCode +" ƾ֤����="+vtCode);		
		call.setOperationName("batchQueryAllVoucherStatus");	
		Map<String,Object[]> map = null;
		try {
			map = (Map)call.invoke(new Object[] { certID,admDivCode,stYear,vtCode,voucherNos});
			if(map!=null){
				logger.debug("ƾ֤��ѯ�ѻص�ƾ֤״̬�����ɹ���ƾ֤�б� voucherNo==="+Arrays.toString(voucherNos));
			}
		} catch (RemoteException e) {
			logger.error("�ѷ���ƾ֤������ѯ�б� voucherNo==="+Arrays.toString(voucherNos)+"ʱ�����쳣:",e);
			String err="�鿴ƾ֤״̬�쳣: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());		
		}
		return map;
	}
}

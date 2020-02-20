package com.cfcc.itfe.voucher.service;

import java.util.List;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;

public interface IVoucherFactory {
	//��ȡƾ֤
	public int voucherRead(List list);
	//ƾ֤����
	public List voucherGenerate(List list) throws ITFEBizException;
	//ƾ֤У��
	public int voucherVerify(List lists,String vtcode) throws ITFEBizException;
	//ƾ֤�ύ
	public int voucherCommit(List list) throws ITFEBizException;
	//ƾ֤ǩ��
	public int voucherStamp(List lists) throws ITFEBizException;
	//ȡ��ָ��λ��ǩ��
	public int voucherStampCancle(List lists) throws ITFEBizException;
	//ƾ֤ǩ�³ɹ����ͻص�
	public int voucherSendReturnSuccess(List list) throws ITFEBizException;
	//ƾ֤�˻�
	public int voucherReturnBack(List list) throws ITFEBizException;
	//���ҷ��ͻص���ƾ֤״̬
	public int voucherReturnQueryStatus(List list) throws ITFEBizException;	
	//����Tips���ر��ĸ���ƾ֤״̬
	public void VoucherReceiveTIPS(String spackno,String state, String addword) throws Exception;
	//����ƾ֤����(��ɫͨ��)
	public void sendData(TvVoucherinfoDto dto) throws ITFEBizException;
}

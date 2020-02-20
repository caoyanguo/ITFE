package com.cfcc.itfe.voucher.service;

import java.util.List;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;

public interface IVoucherFactory {
	//读取凭证
	public int voucherRead(List list);
	//凭证生成
	public List voucherGenerate(List list) throws ITFEBizException;
	//凭证校验
	public int voucherVerify(List lists,String vtcode) throws ITFEBizException;
	//凭证提交
	public int voucherCommit(List list) throws ITFEBizException;
	//凭证签章
	public int voucherStamp(List lists) throws ITFEBizException;
	//取消指定位置签章
	public int voucherStampCancle(List lists) throws ITFEBizException;
	//凭证签章成功后发送回单
	public int voucherSendReturnSuccess(List list) throws ITFEBizException;
	//凭证退回
	public int voucherReturnBack(List list) throws ITFEBizException;
	//查找发送回单后凭证状态
	public int voucherReturnQueryStatus(List list) throws ITFEBizException;	
	//根据Tips返回报文更新凭证状态
	public void VoucherReceiveTIPS(String spackno,String state, String addword) throws Exception;
	//发送凭证附件(绿色通道)
	public void sendData(TvVoucherinfoDto dto) throws ITFEBizException;
}

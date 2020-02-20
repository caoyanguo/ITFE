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
	// 创建服务对象
	private static Service service;
	private static Call call;
	
	public VoucherService () throws ITFEBizException{
		
		try {
			service=new Service();
			call = (Call) service.createCall();
		} catch (ServiceException e) {
			throw new ITFEBizException("初始化WebService时出现异常：",e);
		}
		
		// 设置远程地址
		call.setTargetEndpointAddress(url+"/realware/services/AsspCBankService");
		
		
	}
	
	
	/*
	 *	5.2.2.9 未签收凭证读取
	 *	1	admDivCode	String	行政区划，6位国标码
		2	stYear	int	业务年度，4位数字
		3	vtCode	String	凭证类性，4位字符
		4	voucherCount	int	一次读取的凭证数量，最大100
	 */
	public byte[] readVoucher( String admDivCode, int stYear, String vtCode) throws ITFEBizException{
		call.setOperationName("readVoucherAgain");
		// 调用接口
		byte[] result = null;
		try {
			
			result = (byte[]) call.invoke(new Object[] {"001", admDivCode,  stYear,vtCode,  100});
			logger.debug("凭证读取操作成功:  区划代码  "+admDivCode+" 凭证类型  "+vtCode);

		} catch (RemoteException e) {
			throw new ITFEBizException("未签收凭证读取出现异常"+e.getMessage(),e);
		}catch(Exception e){			
			throw new ITFEBizException("未签收凭证读取出现异常"+e.getMessage(),e);
		}
		if(result!=null)
			logger.debug("未签收凭证读取结束");
		else
			logger.debug("====================未签收凭证读取结束,未读取到数据！====================");
		return result;
	}
	
	/**
	 * 凭证签收
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param voucherNo
	 * @return
	 */
	public String confirmVoucher(String certID,String admDivCode, int stYear, String vtCode, String[] voucherNo) {
		logger.debug("凭证签收成功条数=="+voucherNo.length);
		String err=null;
		call.setOperationName("confirmVoucher");
		try {
			logger.debug("凭证签收成功调用凭证库方法==========confirmVoucher(001,"+admDivCode+","+stYear+","+vtCode+","+Arrays.toString(voucherNo)+")");
			call.invoke(new Object[] {"001", admDivCode,  stYear,vtCode,  voucherNo});
			logger.debug("凭证签收操作成功:  凭证列表   voucherNo==="+Arrays.toString(voucherNo));
			
		} catch (RemoteException e) {
			logger.debug("凭证签收成功时出现异常凭证列表 voucherNo==="+Arrays.toString(voucherNo),e);
			err="签收成功异常: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());	
			
		}return err;
	}
	
	/**
	 * 凭证签收失败
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
			logger.debug("凭证签收失败调用凭证库方法debug==========confirmVoucherFail(001,"+admDivCode+","+stYear+","+vtCode+",'"+Arrays.toString(voucherlist)+"',"+Arrays.toString(suberrMsg)+")");
			call.setOperationName("confirmVoucherFail");
			call.invoke(new Object[] {"001",admDivCode, stYear, vtCode, voucherlist, suberrMsg});
			logger.debug("凭证签收失败操作成功: 凭证列表:voucherNo==="+Arrays.toString(voucherlist)+"凭证签收失败原因列表:"+Arrays.toString(suberrMsg));
		} catch (RemoteException e) {
			logger.debug("凭证签收失败出现异常： voucherNo==="+Arrays.toString(voucherlist),e);
			err="签收失败异常:"+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());	
		}return err;
	}
	//凭证写入
	public Map<String,String> WirteVoucher (String admDivCode, int stYear, String vtCode, byte[] voucher,String desorgcode) throws ITFEBizException{
		logger.debug("凭证写入 行政区划码="+admDivCode +" 凭证类型="+vtCode);
		admDivCode= admDivCode.trim();
		vtCode = vtCode.trim();
		call.setOperationName("writeVoucher");	
		Map map = null;
		try {
			map = (Map)call.invoke(new Object[] { "001",admDivCode,  stYear,vtCode,voucher,"001", desorgcode});
		} catch (RemoteException e) {
			throw new ITFEBizException("凭证写入时出现异常：",e);
		}
		return map;
	}
	/**
	 * 凭证退回
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param voucherNo
	 * @param errMsg
	 * @return
	 */
	public String returnVoucherBack(String certID,String admDivCode, int stYear, String vtCode, String[] voucherNo, String[] errMsg) {
		logger.debug("凭证签收退回条数=="+voucherNo.length);
		String err=null;
		call.setOperationName("returnVoucher");	
		try {
			logger.debug("凭证签收退回调用凭证库方法debug==========returnVoucher(001,"+admDivCode+","+stYear+","+vtCode+","+Arrays.toString(voucherNo)+","+Arrays.toString(errMsg)+")");
			call.invoke(new Object[] {"001",admDivCode,  stYear,vtCode,  voucherNo,errMsg});
			logger.debug("凭证退回操作成功： 凭证列表 voucherNo==="+Arrays.toString(voucherNo)+"凭证退回编号失败原因列表： voucherNo==="+Arrays.toString(errMsg));
		} catch (RemoteException e) {
			logger.debug("凭证退回出现异常voucherNo==="+Arrays.toString(voucherNo),e);
			err="退回异常: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());	
		}return err;
	}
	
	/**
	 * 凭证作废
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
			logger.debug("凭证作废调用凭证库方法debug==========discardVoucher(001,"+admDivCode+","+stYear+","+vtCode+","+Arrays.toString(voucherNo)+")");
			call.invoke(new Object[] { "001",admDivCode,  stYear,vtCode,  voucherNo});
		} catch (RemoteException e) {
			logger.error("凭证作废时出现异常：",e);
			err="凭证作废异常："+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());	
		}return err;
	} 

	
	/**
	 * 凭证签章
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
			logger.debug("凭证写入 行政区划码="+admDivCode +" 用户证书="+certID +" 凭证类型="+vtCode+ " 章名称="+stampName);
			call.setOperationName("signStampByNos");	
			try {
				logger.debug("凭证签章调用凭证库方法debug==========signStampByNos("+certID+","+admDivCode+","+stYear+","+vtCode+","+vouchers+","+Stamp+")");
				call.invoke(new Object[] { certID,admDivCode,  stYear,vtCode,vouchers,Stamp});
				logger.debug("凭证签章操作成功:行政区划码="+admDivCode +" 凭证类型="+vtCode+" 凭证编号="+voucherNo);

			} catch (RemoteException e) {
				logger.error("凭证写入 行政区划码="+admDivCode +" 凭证类型="+vtCode+" 凭证编号="+voucherNo+"出现异常：",e);
				err="签章异常: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());	
			}
			return err;
		
	}
	

	/**
	 * 发送凭证
	 * @param certID
	 * @param admDivCode
	 * @param decOrgType
	 * @param stYear
	 * @param vtCode
	 * @param voucherNo
	 * @return
	 */
	public String sendVoucher(String certID, String admDivCode, String decOrgType,int stYear, String vtCode, String[] voucherNo){
		logger.debug("凭证写入 行政区划码="+admDivCode +" 用户证书="+certID +" 凭证类型="+vtCode+" 接收机构="+decOrgType);
		call.setOperationName("sendVoucher");	
		String err=null;
		try {
			logger.debug("凭证发送调用凭证库方法debug==========sendVoucher(001"+","+admDivCode+",001,"+decOrgType+","+stYear+","+vtCode+","+Arrays.toString(voucherNo)+")");
			call.invoke(new Object[] { "001",admDivCode,"001",decOrgType,stYear,vtCode,voucherNo});
			logger.debug("凭证回单操作成功：  凭证列表 voucherNo==="+Arrays.toString(voucherNo));
		} catch (RemoteException e) {
			logger.error("发送回单凭证列表出现异常: voucherNo==="+Arrays.toString(voucherNo),e);
			err="发送凭证异常: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());			
		}
		return err;
	}
	
	/**
	 * 发送全版本凭证
	 * @param certID
	 * @param admDivCode
	 * @param decOrgType
	 * @param stYear
	 * @param vtCode
	 * @param voucherNo
	 * @return
	 */
	public String sendVoucherFullSigns(String certID, String admDivCode, String decOrgType,int stYear, String vtCode, String[] voucherNo) {
		logger.debug("凭证写入 行政区划码="+admDivCode +" 用户证书="+certID +" 凭证类型="+vtCode+" 接收机构="+decOrgType);
		call.setOperationName("sendVoucherFullSigns");	
		String err=null;
		try {
			logger.debug("凭证发送全版本调用凭证库方法debug==========sendVoucherFullSigns(001"+","+admDivCode+",001,"+decOrgType+","+stYear+","+vtCode+","+Arrays.toString(voucherNo)+")");
			call.invoke(new Object[] { "001",admDivCode,"001",decOrgType,stYear,vtCode,voucherNo});
			logger.debug("凭证回单操作成功：  凭证列表 voucherNo==="+Arrays.toString(voucherNo));
		} catch (RemoteException e) {
			logger.error("发送回单凭证列表时出现异常: voucherNo==="+Arrays.toString(voucherNo),e);
			err="发送凭证异常: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());
			
		}
		return err;
	}
	
	/**
	 * 已回单凭证状态批量查询
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param voucherNos
	 * @return
	 */
	public Map batchQuerySentVoucherStatus (String certID,String admDivCode, int stYear, String vtCode, String[] voucherNos){
		logger.debug("凭证写入 行政区划码="+admDivCode +" 凭证类型="+vtCode);		
		call.setOperationName("batchQuerySentVoucherStatus");	
		Map map = null;
		try {
			map = (Map)call.invoke(new Object[] { "001",admDivCode,stYear,vtCode,voucherNos});
			if(map!=null){
				logger.debug("凭证查询已回单凭证状态操作成功:voucherNo==="+Arrays.toString(voucherNos));
			}
		} catch (RemoteException e) {
			logger.error("凭证状态查询出现异常:voucherNo==="+Arrays.toString(voucherNos),e);
			String err="查看凭证状态异常: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());
			map=new HashMap();
			map.put("-2", err);			
		}
		return map;
	}
	
	/**
	 * 指定机构查询凭证状态接口
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param voucherNos
	 * @return
	 */
	public Map queryVoucherStatusByOrgType(String certID,String admDivCode, int stYear, String vtCode, String[] voucherNos,String decOrgType){
		logger.debug("凭证写入 行政区划码="+admDivCode +" 凭证类型="+vtCode+" 接收机构="+decOrgType);		
		call.setOperationName("queryVoucherStatusByOrgType");	
		Map map = null;
		try {
			map = (Map)call.invoke(new Object[] { "001",admDivCode,stYear,vtCode,voucherNos,decOrgType});
			if(map!=null){
				logger.debug("凭证查询已回单凭证状态操作成功：凭证列表 voucherNo==="+Arrays.toString(voucherNos));
			}
		} catch (RemoteException e) {
			logger.error("已发送凭证批量查询列表 voucherNo==="+Arrays.toString(voucherNos)+"时出现异常:",e);
			String err="查看凭证状态异常: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());
			map=new HashMap();
			map.put("-100", err);			
		}
		return map;
	}
	
	/**
	 * 撤销指定位置凭证签章
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
		logger.debug("凭证写入 行政区划码="+admDivCode +" 用户证书="+certID +" 凭证类型="+vtCode);
		call.setOperationName("cancelStampWithPosition");	
		String err=null;
		try {
			logger.debug("凭证签章撤销调用凭证库方法debug==========cancelStampWithPosition("+certID+","+admDivCode+","+stYear+","+vtCode+","+voucherNo+","+stampPosition+")");
			call.invoke(new Object[] { certID,admDivCode,stYear,vtCode,voucherNo,stampPosition});
			logger.debug("凭证撤销签章操作成功：	凭证列表 voucherNo==="+voucherNo);
			
		} catch (RemoteException e) {
			logger.error("撤销签章凭证列表 voucherNo==="+voucherNo+"时出现异常:",e);
			err="签章撤销异常: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());			
		}return err;
	}
	
	/**
	 * 客户端签章
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param signeVoucher
	 * @param stampname
	 * @return
	 */
	public String saveStampVoucher(String certID,String admDivCode,int stYear,String vtCode,byte[] signeVoucher,String stampname) {
		logger.debug("凭证写入 行政区划码="+admDivCode  +" 凭证类型="+vtCode+" 用户证书="+certID+"章类型"+stampname);
		call.setOperationName("saveStampVoucher");
		String err=null;
		try {
		  logger.debug("客户端签章调用凭证库方法debug==========saveStampVoucher("+certID+","+admDivCode+","+stYear+","+vtCode+","+signeVoucher+")");
		  call.invoke(new Object[] { certID,admDivCode,stYear,vtCode,signeVoucher});
		  logger.debug("======凭证"+stampname+"客户端签章操作成功=====");
		} catch (RemoteException e) {
			logger.error(stampname+"客户端签章时出现异常:",e);
			err="签章异常: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());	
		}return err;
	}
	
	/**
	 * 查询凭证打印次数
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param VoucherNo
	 * @return
	 */
	public String queryVoucherPrintCount(String certID, String admDivCode, int stYear, String vtCode, String VoucherNo){
		logger.debug("凭证写入 行政区划码="+admDivCode +" 用户证书="+certID +" 凭证类型="+vtCode+" 凭证编号"+VoucherNo);
		call.setOperationName("queryVoucherPrintCount");
		String err=null;
		try {
			err=call.invoke(new Object[] {"001", admDivCode,  stYear,vtCode,  VoucherNo})+"";
			
			logger.debug("查询凭证打印次数操作成功:voucherNo==="+VoucherNo);
			
		} catch (RemoteException e) {
			logger.debug("查询凭证打印次数时出现异常:"+e);
			err="查询异常: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());	
		}
		return err;
	}
	
	/**
	 * 查询电子凭证库的盖章位置及名称
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @return
	 * @throws ITFEBizException
	 */
	public Map queryStampPositionWithName(String certID, String admDivCode, int stYear, String vtCode)throws ITFEBizException{
		logger.debug("查询凭证盖章位置列表="+admDivCode +" 用户证书="+certID +" 凭证类型="+vtCode);
		call.setOperationName("queryStampPositionWithName");
		Map positionMap = null;

		try {
			positionMap=(Map)call.invoke(new Object[] {"001", admDivCode,  stYear,vtCode});
		} catch (RemoteException e) {
			logger.debug("查询电子凭证库的盖章位置及名称: ",e);
		}
		return positionMap;
	}
	
	/**
	 * 写入不带位置服务器签名的凭证
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
			logger.debug("写入不带位置服务器签名凭证调用凭证库方法debug==========signWithoutPosition(001"+","+admDivCode+","+stYear+","+vtCode+","+voucher+")");
			result=(byte[]) call.invoke(new Object[] { "001",admDivCode,stYear,vtCode,voucher});
			if(result!=null){
				try {
					err=new String(result,"gbk");
				} catch (UnsupportedEncodingException e) {
					logger.error(e);
					err="凭证签名异常: 获取签名报文错误";
				}
			}
			logger.debug("凭证写入不带位置签名凭证操作成功");			
		} catch (RemoteException e) {
			logger.error("凭证写入不带位置签名凭证操作成功时出现异常：",e);
			err="凭证签名异常: "+(e.getMessage().indexOf("Exception:")==-1?e.getMessage():(e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length())));
			return err;
		}
		return err;
	}
	
	
	/**
	 * 写入客户端签名的凭证
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
			logger.debug("写入客户端签名凭证调用凭证库方法debug==========saveSignVoucher("+certID+","+admDivCode+","+stYear+","+vtCode+","+voucher+")");
			call.invoke(new Object[] { certID,admDivCode,stYear,vtCode,voucher});
			logger.debug("写入客户端签名的凭证操作成功");			
		} catch (RemoteException e) {
			logger.error("写入客户端签名的凭证操作出现异常：",e);
			err="凭证签名异常: "+(e.getMessage().indexOf("Exception:")==-1?e.getMessage():(e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length())));
			return err;
		}
		return err;
	}
	
	/**
	 * 写入指定位置服务器签名的凭证
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
			logger.debug("写入指定位置服务器签名凭证调用凭证库方法debug==========signByNos(001"+","+admDivCode+","+stYear+","+vtCode+","+vouchers+","+Stamp+")");
			call.invoke(new Object[] {"001",admDivCode,stYear,vtCode,vouchers,Stamp});			
			logger.debug("凭证写入带位置签名凭证操作成功");			
		} catch (RemoteException e) {
			logger.error("凭证写入不带位置签名凭证操作成功时出现异常：",e);
			err="凭证签名异常: "+(e.getMessage().indexOf("Exception:")==-1?e.getMessage():(e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length())));
			return err;
		}
		return err;
	}
	
	/**
	 * 写入指定位置服务器签名的凭证
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
			logger.debug("写入指定位置服务器签名的凭证调用凭证库方法debug==========signByNosAndSend("+certID+","+admDivCode+","+stYear+","+vtCode+","+vouchers+","+srcOrgType+","+desOrgType+")");
			call.invoke(new Object[] {certID,admDivCode,stYear,vtCode,vouchers,Stamp,srcOrgType,desOrgType});			
			logger.debug("凭证写入带位置签名凭证操作成功");			
		} catch (RemoteException e) {
			logger.error("凭证写入不带位置签名凭证操作成功时出现异常：",e);
			err="凭证签名异常: "+(e.getMessage().indexOf("Exception:")==-1?e.getMessage():(e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length())));
			return err;
		}
		return err;
	}
	

	/**
	 * 发送凭证附件(绿色通道)
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
			logger.debug("发送绿色通道调用凭证库方法debug==========sendData("+certID+","+admDivCode+","+srcOrgType+","+decOrgType+","+stYear+","+bytes+")");
			call.invoke(new Object[] {certID, admDivCode, srcOrgType,decOrgType,stYear,bytes});			
			logger.debug("发送凭证附件成功");			
		} catch (RemoteException e) {
			logger.error("发送附件出现异常：",e);	
			throw new ITFEBizException("发送凭证附件出现异常\t\n"+e.getMessage());
		}
	}
	
	/**
	 * 接收凭证附件(绿色通道)
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
			logger.debug("接收绿色通道调用凭证库方法debug==========getData("+certID+","+stYear+","+admDivCode+")");
			result = (byte[]) call.invoke(new Object[] {certID,stYear,admDivCode});			
			logger.debug("接收凭证附件成功");			
		} catch (RemoteException e) {
			logger.error("接收附件出现异常：",e);	
			throw new ITFEBizException("接收凭证附件出现异常\t\n"+e.getMessage());
		}
		return result;
	}
	
	public byte[] exportPDF(String certID, String admDivCode, int stYear, String vt_code, int pageNo, String voucher_no)throws ITFEBizException{		
		byte[] result = null;
		try {
			Call pdfcall = (Call) new Service().createCall();
			pdfcall.setTargetEndpointAddress(pdfurl);
			pdfcall.setOperationName("exportPDF");
			logger.debug("导出PDF文件调用凭证库方法debug==========exportPDF("+certID+","+admDivCode+","+stYear+","+vt_code+","+pageNo+","+voucher_no+")");
			result = (byte[]) pdfcall.invoke(new Object[] {certID,admDivCode,stYear,vt_code,pageNo,voucher_no});			
			logger.debug("导出PDF文件成功");			
		} catch (Exception e) {
			logger.error("导出PDF文件出现异常:",e);	
			throw new ITFEBizException("导出PDF文件出现异常\t\n"+e.getMessage());
		}
		return result;
	}
	/**
	 * 批量查询凭证回单状态
	 * @param certID
	 * @param admDivCode
	 * @param stYear
	 * @param vtCode
	 * @param voucherNos
	 * @return
	 * @throws ITFEBizException
	 */
	public Map<String,Object[]> batchQueryAllVoucherStatus(String certID,String admDivCode,int stYear,String vtCode,String[] voucherNos) throws ITFEBizException{
		logger.debug("凭证写入 行政区划码="+admDivCode +" 凭证类型="+vtCode);		
		call.setOperationName("batchQueryAllVoucherStatus");	
		Map<String,Object[]> map = null;
		try {
			map = (Map)call.invoke(new Object[] { certID,admDivCode,stYear,vtCode,voucherNos});
			if(map!=null){
				logger.debug("凭证查询已回单凭证状态操作成功：凭证列表 voucherNo==="+Arrays.toString(voucherNos));
			}
		} catch (RemoteException e) {
			logger.error("已发送凭证批量查询列表 voucherNo==="+Arrays.toString(voucherNos)+"时出现异常:",e);
			String err="查看凭证状态异常: "+e.getMessage().substring(e.getMessage().indexOf("Exception:")+"Exception:".length(), e.getMessage().length());		
		}
		return map;
	}
}

package com.cfcc.itfe.client.sendbiz.bizcertsend;

public class OcxConstant {

	/**
	 * 初始化ocx控件
	 */
	public final static String INITOCX = "Initialize";

	/**
	 * 添加凭证的常量类
	 */
	public final static String ADDVOUCHER = "AddVoucher";
	/**
	 * 添加凭证的常量类(服务器加载)
	 */
	public final static String ADDVOUCHERFROMSERVER = "AddVoucherfromServer";

	/**
	 * 清空凭证信息的常量类
	 */
	public final static String CLEARVOUCHER = "EstampOcxClearVouchers";
	public final static String Refresh = "Refresh";
	/**
	 * 展示当前选定的凭证信息
	 */
	public final static String SETCURRENTVOUCHER = "SetCurrentVoucher";

	/**
	 * 获得签名档数据的常量
	 */
	public final static String GETSIGNEDVOUCHERS = "EstampOcxGetSingedVouchers";

	/**
	 *获得操作报文的常量类
	 */
	public final static String GETOPERATEVOUCHRS = "EstampOcxGetVoucherOperate";

	/**
	 * 设置url
	 */
	public final static String SETURL = "SetEvoucherServiceUrl";
	
	/**
	 * 设置电子印章url
	 */
	public final static String SETEstampURL = "SetEstampServiceUrl";

	/**
	 * 获得签章档数据的常量
	 */
	public final static String GETVOUCHERSTAMP = "GetVoucherStamp";
	
	/**
	 * 获得签名档数据的常量
	 */
	public final static String GETVOUCHERSIGN = "GetVoucherSign";

	/**
	 * 获取ocx调用过程异常信息
	 */
	public final static String GetLastErr = "GetLastErr";
	
	/**
	 * 自动适应显示凭证
	 */
	public final static String ZoomToFit = "ZoomToFit";

	/**
	 * 电子凭证打印（后台打印，不显示OCX）
	 */
	public final static String PRINTALLVOUCHER = "PrintAllVoucher";

	/**
	 * 电子凭证打印
	 */
	public final static String PRINTONEVOUCHER = "PrintVoucherByNo";

	/**
	 * 上一联
	 */
	public final static String PAGEUP = "PageUp";

	/**
	 * 下一联
	 */
	public final static String PAGEDOWN = "PageDown";
	
	/**
	 * OCX访问凭证库地址（后半部分）
	 */
	public final static String EVOUCHERSERVICEURL = "/realware/signer/result!getMethod.action";
	
	/**
	 * OCX访问签章服务地址（后半部分）
	 */
	public final static String ESTAMPSERVICEURL = "/realware/services/AsspEStampService";
}

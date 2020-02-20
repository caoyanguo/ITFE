package com.cfcc.itfe.client.sendbiz.bizcertsend;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.recbiz.voucherload.IVoucherLoadService;
import com.cfcc.itfe.service.recbiz.voucherload.VoucherLoadService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.core.invoker.http.HttpConfig;
import com.cfcc.jaf.core.loader.ContextFactory;

/**
 * 为客户端调用凭证库OCX服务提供代理工具 
 * @author hua
 */
public class ClientOcxServiceProxyUtils {
	private static Log log = LogFactory.getLog(ClientOcxServiceProxyUtils.class);
	/**凭证加载服务**/
	private static IVoucherLoadService voucherLoadService = (IVoucherLoadService)ServiceFactory.getService(IVoucherLoadService.class);
	/**数据访问公共服务**/
	private static ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService) ServiceFactory.getService(ICommonDataAccessService.class);
	
	private static final String OCXSERVICETYPE_VOUCHER = "ocxService";
	private static final String OCXSERVICETYPE_STAMP = "stampService";
	private static final String PROXY_NO = "0"; //不代理
	private static final String PROXY_YES = "1"; //代理
	
	/**产生代理服务地址**/
	public static String generatePoxyUrl(String serviceType){
		HttpConfig config = (HttpConfig) ContextFactory.getApplicationContext().getBean("httpConfig");
		String proxyUrl = "http://" + config.getHost() + ":" + config.getPort() + config.getServerFile().substring(0, config.getServerFile().indexOf("/", 1));
		try {
			String isClientProxy = commonDataAccessService.getIsClientProxy();
			if(StringUtils.isBlank(isClientProxy) || (!PROXY_NO.equals(isClientProxy)&&!PROXY_YES.equals(isClientProxy))) {
				isClientProxy = PROXY_NO; //如果没有取到该参数,或者取到的不是0或1,则默认为不使用代理
			}
 			if(OCXSERVICETYPE_VOUCHER.equals(serviceType)) {
 				return PROXY_NO.equals(isClientProxy)?voucherLoadService.getOCXServerURL():proxyUrl;
			}
 			if(OCXSERVICETYPE_STAMP.equals(serviceType)) {
 				return PROXY_NO.equals(isClientProxy)?voucherLoadService.getOCXStampServerURL():proxyUrl;
			}
		} catch (Exception e) {
			log.error("获取客户端访问ocx服务是否使用代理参数 出现异常",e);
			try {
				/**在异常情况下,默认为不使用代理,最大程度保证程序在异常下继续运行**/
				if(OCXSERVICETYPE_VOUCHER.equals(serviceType)){
					return voucherLoadService.getOCXServerURL();
				}else if(OCXSERVICETYPE_STAMP.equals(serviceType)){
					return voucherLoadService.getOCXStampServerURL();
				}
			} catch (ITFEBizException e1) {
				log.error(e);
				throw new RuntimeException("客户端访问OCX服务出现异常(proxy)",e);
			}
		}
		return proxyUrl;
	}
	public static String getOcxVoucherServerURL(){
		return generatePoxyUrl(OCXSERVICETYPE_VOUCHER);
	}
	public static String getOCXStampServerURL(){
		return generatePoxyUrl(OCXSERVICETYPE_STAMP);
	}
}

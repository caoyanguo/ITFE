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
 * Ϊ�ͻ��˵���ƾ֤��OCX�����ṩ������ 
 * @author hua
 */
public class ClientOcxServiceProxyUtils {
	private static Log log = LogFactory.getLog(ClientOcxServiceProxyUtils.class);
	/**ƾ֤���ط���**/
	private static IVoucherLoadService voucherLoadService = (IVoucherLoadService)ServiceFactory.getService(IVoucherLoadService.class);
	/**���ݷ��ʹ�������**/
	private static ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService) ServiceFactory.getService(ICommonDataAccessService.class);
	
	private static final String OCXSERVICETYPE_VOUCHER = "ocxService";
	private static final String OCXSERVICETYPE_STAMP = "stampService";
	private static final String PROXY_NO = "0"; //������
	private static final String PROXY_YES = "1"; //����
	
	/**������������ַ**/
	public static String generatePoxyUrl(String serviceType){
		HttpConfig config = (HttpConfig) ContextFactory.getApplicationContext().getBean("httpConfig");
		String proxyUrl = "http://" + config.getHost() + ":" + config.getPort() + config.getServerFile().substring(0, config.getServerFile().indexOf("/", 1));
		try {
			String isClientProxy = commonDataAccessService.getIsClientProxy();
			if(StringUtils.isBlank(isClientProxy) || (!PROXY_NO.equals(isClientProxy)&&!PROXY_YES.equals(isClientProxy))) {
				isClientProxy = PROXY_NO; //���û��ȡ���ò���,����ȡ���Ĳ���0��1,��Ĭ��Ϊ��ʹ�ô���
			}
 			if(OCXSERVICETYPE_VOUCHER.equals(serviceType)) {
 				return PROXY_NO.equals(isClientProxy)?voucherLoadService.getOCXServerURL():proxyUrl;
			}
 			if(OCXSERVICETYPE_STAMP.equals(serviceType)) {
 				return PROXY_NO.equals(isClientProxy)?voucherLoadService.getOCXStampServerURL():proxyUrl;
			}
		} catch (Exception e) {
			log.error("��ȡ�ͻ��˷���ocx�����Ƿ�ʹ�ô������ �����쳣",e);
			try {
				/**���쳣�����,Ĭ��Ϊ��ʹ�ô���,���̶ȱ�֤�������쳣�¼�������**/
				if(OCXSERVICETYPE_VOUCHER.equals(serviceType)){
					return voucherLoadService.getOCXServerURL();
				}else if(OCXSERVICETYPE_STAMP.equals(serviceType)){
					return voucherLoadService.getOCXStampServerURL();
				}
			} catch (ITFEBizException e1) {
				log.error(e);
				throw new RuntimeException("�ͻ��˷���OCX��������쳣(proxy)",e);
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

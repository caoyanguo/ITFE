package com.cfcc.itfe.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * 为客户端调用ocx服务提供HTTP代理
 * @author hua
 */
public class OcxHttpProxyServlet extends HttpServlet {
	private static Log log = LogFactory.getLog(OcxHttpProxyServlet.class);
	private static final long serialVersionUID = 1L;
	
	/**ocx访问WEBSERVICE的服务地址*/
	private static String OCXSERVICE_URL = ITFECommonConstant.OCXSERVICE_URL;
	
	/**ocx访问签章服务的地址*/
	private static String OCXSERVICE_STAMPURL = ITFECommonConstant.OCXSERVICE_STAMPURL;
	
	/**客户端访问ocx_voucher服务 url后缀*/
	private static String OCXVOUCHEREXT = "/realware/services/OCXService";
	
	/**客户端访问ocx_stamp服务 url后缀*/
	private static String OCXSTAMPEXT = (ITFECommonConstant.PUBLICPARAM.contains(",ocxnewinterface=true,")?"/estamp":"/realware")+"/services/AsspEStampService";
	
	/** 1. 当凭证库未启动或者出现其他异常时,向客户端OCX控件返回相应错误信息,以防止客户端异常退出等情况发生;
	  * 2. 在客户端弹框显示的错误信息,注意最好为英文,否则在首次使用电子凭证还原功能时会出现乱码提示;*/
	private static final String OCX_BACK_EXCEPTION_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soapenv:Body><soapenv:Fault><faultcode>Server.userException</faultcode><faultstring>";
	private static final String OCX_BACK_EXCEPTION_INFO = "Connect exception [Voucher_Servcie_Connection_ERROR] ";
	private static final String OCX_BACK_EXCEPTION_FOOT = "</faultstring><detail><ns1:hostname xmlns:ns1=\"http://xml.apache.org/axis/\">Connect exception</ns1:hostname></detail></soapenv:Fault></soapenv:Body></soapenv:Envelope>";

	/**是否开启日志 0--不开启 1--开启 (开启日志主要是方便调试)*/
	private static final String isLogOpen = "0";
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		proxyHttp(request, response);
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		proxyHttp(request, response);
	}
	public void proxyHttp(HttpServletRequest request,
			HttpServletResponse response){
		try {
			int count = request.getContentLength();
			if (count != -1) { //只有当请求体不为空时才代理
				/** 1. 获取客户端访问的内容*/
				StringBuffer builder = extractContentFromRequest(request);
				
				/** 2. 根据访问uri来组装凭证库真实地址*/
				String realServiceUrl = generateProxyUrl(request);
				
				/** 3. 组装代理服务内容,并封装到HTTP_POST消息体中*/
				PostMethod postMethod = packPostMethod(builder, realServiceUrl);
				
				printLog("======================== Start Client Proxy Service ========================");
				printLog("Client_Proxy_realServiceUrl:"+realServiceUrl);
				printLog("Client_Proxy_accessContent:\n"+builder);
				
				/** 4. 开始代理OCX服务*/
				int code = 0;
				try {
					code = new HttpClient().executeMethod(postMethod);
				} catch (Exception e) {
					log.error("客户端OCX代理服务出现异常(可能是凭证库服务未启动)",e);
					
					/**catch可能发生的异常,并做相应的处理*/
					returnInfoWhenException(response);
					
					printLog("========= end Client Proxy Service with an Exception["+e.getMessage()+"] =========");
					return;
				}
				
				/** 5. 获取凭证库返回的数据,并原样回传给客户端*/
				writeResult2Client(response, postMethod);
				
				printLog("============== end Client Proxy Service[Http_Result_Code:"+code+"] ==============");
			}
		} catch (Exception e) {
			log.error("在提供客户端访问ocx服务代理时出现异常(OCX_HTTP_PROXY):"+e.getMessage(),e);
			/**这里只记录不抛出异常,防止客户端异常退出的情况出现*/
//			throw new RuntimeException("在提供客户端访问ocx服务代理时出现异常(OCX_HTTP_PROXY)",e);
		}
	}
	
	/**当出现异常时,向客户端发送异常信息*/
	private void returnInfoWhenException(HttpServletResponse response)
			throws IOException {
		ServletOutputStream out = response.getOutputStream();
		StringBuffer errorInfo = new StringBuffer(OCX_BACK_EXCEPTION_HEAD)
				.append(OCX_BACK_EXCEPTION_INFO)
				.append(OCX_BACK_EXCEPTION_FOOT);
		out.write(errorInfo.toString().getBytes());
	}
	
	/**根据日志标志来判断是否记录日志,主要用于调试*/
	private void printLog(String info) {
		if(isLogOpen.equals("1")) {
			log.debug(info);
		}
	}
	
	/**将OCX服务返回的内容传回到客户端*/
	private void writeResult2Client(HttpServletResponse response,
			PostMethod postMethod) throws IOException {
		InputStream input = postMethod.getResponseBodyAsStream();
		ServletOutputStream out = response.getOutputStream();
		if(input!=null) {
			int len = 0;
			byte[] dataTemp = new byte[1024];
			while ((len = input.read(dataTemp)) != -1) {
				out.write(dataTemp,0,len);
			}
		}
	}
	
	/**组装Http_Post 方法,并填充消息体内容*/
	private PostMethod packPostMethod(StringBuffer builder,
			String realServiceUrl) throws IOException {
		PostMethod postMethod = new PostMethod(realServiceUrl);
		postMethod.addRequestHeader("SOAPAction", "");
		postMethod.addRequestHeader("Content-Type","application/soap+xml; charset=\"UTF-8\"");
		InputStream inputStream = new ByteArrayInputStream(builder.toString().getBytes());
		postMethod.setRequestBody(inputStream);
		return postMethod;
	}
	
	/**判断客户端访问的代理地址来产生相应真实地址*/
	private String generateProxyUrl(HttpServletRequest request) {
		String serverName = request.getRequestURI();
		serverName = serverName.substring(serverName.lastIndexOf("/") + 1);
		String realServiceUrl = "";
		if(serverName.contains("OCXService")){
			realServiceUrl = OCXSERVICE_URL + OCXVOUCHEREXT;
		}else if(serverName.contains("AsspEStampService")){
			realServiceUrl = OCXSERVICE_STAMPURL + OCXSTAMPEXT;
		}else {
			log.error("在提供客户端访问ocx服务代理时出现异常(OCX_HTTP_PROXY):暂不支持该请求类型["+serverName+"],请联系管理员!");
		}
		return realServiceUrl;
	}
	
	/**从request里面抽取出客户端访问的内容*/
	private StringBuffer extractContentFromRequest(HttpServletRequest request) throws IOException {
		ServletInputStream in = request.getInputStream();
		int len = 0;
		byte[] buffer = new byte[1024];
		StringBuffer builder = new StringBuffer("");
		while ((len = in.read(buffer)) != -1) {
			builder.append(new String(buffer, 0, len));
		}
		return builder;
	}
}

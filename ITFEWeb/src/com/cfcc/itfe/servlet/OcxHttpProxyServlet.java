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
 * Ϊ�ͻ��˵���ocx�����ṩHTTP����
 * @author hua
 */
public class OcxHttpProxyServlet extends HttpServlet {
	private static Log log = LogFactory.getLog(OcxHttpProxyServlet.class);
	private static final long serialVersionUID = 1L;
	
	/**ocx����WEBSERVICE�ķ����ַ*/
	private static String OCXSERVICE_URL = ITFECommonConstant.OCXSERVICE_URL;
	
	/**ocx����ǩ�·���ĵ�ַ*/
	private static String OCXSERVICE_STAMPURL = ITFECommonConstant.OCXSERVICE_STAMPURL;
	
	/**�ͻ��˷���ocx_voucher���� url��׺*/
	private static String OCXVOUCHEREXT = "/realware/services/OCXService";
	
	/**�ͻ��˷���ocx_stamp���� url��׺*/
	private static String OCXSTAMPEXT = (ITFECommonConstant.PUBLICPARAM.contains(",ocxnewinterface=true,")?"/estamp":"/realware")+"/services/AsspEStampService";
	
	/** 1. ��ƾ֤��δ�������߳��������쳣ʱ,��ͻ���OCX�ؼ�������Ӧ������Ϣ,�Է�ֹ�ͻ����쳣�˳����������;
	  * 2. �ڿͻ��˵�����ʾ�Ĵ�����Ϣ,ע�����ΪӢ��,�������״�ʹ�õ���ƾ֤��ԭ����ʱ�����������ʾ;*/
	private static final String OCX_BACK_EXCEPTION_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soapenv:Body><soapenv:Fault><faultcode>Server.userException</faultcode><faultstring>";
	private static final String OCX_BACK_EXCEPTION_INFO = "Connect exception [Voucher_Servcie_Connection_ERROR] ";
	private static final String OCX_BACK_EXCEPTION_FOOT = "</faultstring><detail><ns1:hostname xmlns:ns1=\"http://xml.apache.org/axis/\">Connect exception</ns1:hostname></detail></soapenv:Fault></soapenv:Body></soapenv:Envelope>";

	/**�Ƿ�����־ 0--������ 1--���� (������־��Ҫ�Ƿ������)*/
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
			if (count != -1) { //ֻ�е������岻Ϊ��ʱ�Ŵ���
				/** 1. ��ȡ�ͻ��˷��ʵ�����*/
				StringBuffer builder = extractContentFromRequest(request);
				
				/** 2. ���ݷ���uri����װƾ֤����ʵ��ַ*/
				String realServiceUrl = generateProxyUrl(request);
				
				/** 3. ��װ�����������,����װ��HTTP_POST��Ϣ����*/
				PostMethod postMethod = packPostMethod(builder, realServiceUrl);
				
				printLog("======================== Start Client Proxy Service ========================");
				printLog("Client_Proxy_realServiceUrl:"+realServiceUrl);
				printLog("Client_Proxy_accessContent:\n"+builder);
				
				/** 4. ��ʼ����OCX����*/
				int code = 0;
				try {
					code = new HttpClient().executeMethod(postMethod);
				} catch (Exception e) {
					log.error("�ͻ���OCX�����������쳣(������ƾ֤�����δ����)",e);
					
					/**catch���ܷ������쳣,������Ӧ�Ĵ���*/
					returnInfoWhenException(response);
					
					printLog("========= end Client Proxy Service with an Exception["+e.getMessage()+"] =========");
					return;
				}
				
				/** 5. ��ȡƾ֤�ⷵ�ص�����,��ԭ���ش����ͻ���*/
				writeResult2Client(response, postMethod);
				
				printLog("============== end Client Proxy Service[Http_Result_Code:"+code+"] ==============");
			}
		} catch (Exception e) {
			log.error("���ṩ�ͻ��˷���ocx�������ʱ�����쳣(OCX_HTTP_PROXY):"+e.getMessage(),e);
			/**����ֻ��¼���׳��쳣,��ֹ�ͻ����쳣�˳����������*/
//			throw new RuntimeException("���ṩ�ͻ��˷���ocx�������ʱ�����쳣(OCX_HTTP_PROXY)",e);
		}
	}
	
	/**�������쳣ʱ,��ͻ��˷����쳣��Ϣ*/
	private void returnInfoWhenException(HttpServletResponse response)
			throws IOException {
		ServletOutputStream out = response.getOutputStream();
		StringBuffer errorInfo = new StringBuffer(OCX_BACK_EXCEPTION_HEAD)
				.append(OCX_BACK_EXCEPTION_INFO)
				.append(OCX_BACK_EXCEPTION_FOOT);
		out.write(errorInfo.toString().getBytes());
	}
	
	/**������־��־���ж��Ƿ��¼��־,��Ҫ���ڵ���*/
	private void printLog(String info) {
		if(isLogOpen.equals("1")) {
			log.debug(info);
		}
	}
	
	/**��OCX���񷵻ص����ݴ��ص��ͻ���*/
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
	
	/**��װHttp_Post ����,�������Ϣ������*/
	private PostMethod packPostMethod(StringBuffer builder,
			String realServiceUrl) throws IOException {
		PostMethod postMethod = new PostMethod(realServiceUrl);
		postMethod.addRequestHeader("SOAPAction", "");
		postMethod.addRequestHeader("Content-Type","application/soap+xml; charset=\"UTF-8\"");
		InputStream inputStream = new ByteArrayInputStream(builder.toString().getBytes());
		postMethod.setRequestBody(inputStream);
		return postMethod;
	}
	
	/**�жϿͻ��˷��ʵĴ����ַ��������Ӧ��ʵ��ַ*/
	private String generateProxyUrl(HttpServletRequest request) {
		String serverName = request.getRequestURI();
		serverName = serverName.substring(serverName.lastIndexOf("/") + 1);
		String realServiceUrl = "";
		if(serverName.contains("OCXService")){
			realServiceUrl = OCXSERVICE_URL + OCXVOUCHEREXT;
		}else if(serverName.contains("AsspEStampService")){
			realServiceUrl = OCXSERVICE_STAMPURL + OCXSTAMPEXT;
		}else {
			log.error("���ṩ�ͻ��˷���ocx�������ʱ�����쳣(OCX_HTTP_PROXY):�ݲ�֧�ָ���������["+serverName+"],����ϵ����Ա!");
		}
		return realServiceUrl;
	}
	
	/**��request�����ȡ���ͻ��˷��ʵ�����*/
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

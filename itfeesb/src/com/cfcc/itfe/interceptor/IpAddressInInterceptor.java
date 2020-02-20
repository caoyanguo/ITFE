package com.cfcc.itfe.interceptor;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.mule.api.MuleMessage;

public class IpAddressInInterceptor extends AbstractSoapInterceptor {

	public static ThreadLocal<String> ip = new ThreadLocal<String>();
	public IpAddressInInterceptor() {
		super(Phase.POST_PROTOCOL);
	}

	public void handleMessage(SoapMessage msg) throws Fault {
		MuleMessage mm = (MuleMessage)msg.get("mule.message");
		String str = mm.getProperty("MULE_REMOTE_CLIENT_ADDRESS").toString();
		String remoteIPAddress = str.substring(1, str.lastIndexOf(":"));
		ip.set(remoteIPAddress);
	}
}

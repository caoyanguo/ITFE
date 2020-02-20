package com.cfcc.test;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.encoding.ser.ArrayDeserializerFactory;
import org.apache.axis.encoding.ser.ArraySerializerFactory;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;

public class WebserviceTest {
	
	
	
	public static void main(String[] args) {
		try {
//			String endpoint = "http://localhost:9991/ITFE/dealBizDataImpl";
//			String endpoint = "http://127.0.0.1:9991/ITFE/tbs";
			String endpoint = "http://localhost:9120/ITFE/tbs";
			// 直接引用远程的wsdl文件
			// 以下都是套路
			String namespace="http://gzqzwebservice.service.itfe.cfcc.com/";
//			String actionurl="WebServiceComponent";
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(endpoint);
			
			/**
			 * readReport
			 */
//			call.setOperationName("sayHi");// WSDL里面描述的接口名称
//			call.addParameter("str",
//					org.apache.axis.encoding.XMLType.XSD_STRING,
//					javax.xml.rpc.ParameterMode.IN);// 接口的参数
//			String result = readVoucher(call, "430000", "5108", "0900000000", "20150724");
//			String result = confirmVoucher(call, "430000", "5108", "0900000000", "20150724","TCNO1000065007");
//			String result = synchronousVoucherStatus(call, "310000", "5108", "0900000000", "20150710","5108011151255713");
//			String result = sendVoucher(call, "430000", "5108", "0900000000", "20150724","TCNO1000065007");
			
//			System.out.println("结果 is " + result);
//			sendBizSeriData(call,namespace);
			testConnect(call,"102000000000","20160324","102000000000");
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}
	
	public static void testConnect(Call call, String desOrgCode, String vouDate, String orgCode) throws RemoteException {
		call.setOperationName("testConnect");// WSDL里面描述的接口名称
		call.addParameter("desOrgCode",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("vouDate",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("orgCode",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.invoke(new Object[]{desOrgCode,vouDate,orgCode});
	}
	
	public static String readVoucher(Call call,String admDivCode, String vtCode, String treCode,
			String vouDate ) throws RemoteException{
		call.setOperationName("readVoucher");// WSDL里面描述的接口名称
		call.addParameter("admDivCode",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("vtCode",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("treCode",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("vouDate",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		String result = (String) call.invoke(new Object[]{admDivCode,vtCode,treCode,vouDate});
		return result;
	}
	public static String readVoucherAgain(Call call,String admDivCode, String vtCode, String treCode,
			String vouDate ) throws RemoteException{
		call.setOperationName("readVoucherAgain");// WSDL里面描述的接口名称
		call.addParameter("admDivCode",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("vtCode",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("treCode",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("vouDate",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		String result = (String) call.invoke(new Object[]{admDivCode,vtCode,treCode,vouDate});
		return result;
	}
	public static String confirmVoucher(Call call,String admDivCode, String vtCode, String treCode,
			String vouDate ,String vouNo) throws RemoteException{
		call.setOperationName("confirmVoucher");// WSDL里面描述的接口名称
		call.addParameter("admDivCode",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("vtCode",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("treCode",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("vouDate",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("vouNo",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		String result = (String) call.invoke(new Object[]{admDivCode,vtCode,treCode,vouDate,vouNo});
		return result;
	}
	
	public static String sendVoucher(Call call,String admDivCode, String vtCode, String treCode,
			String vouDate ,String vouNo) throws RemoteException{
		call.setOperationName("sendVoucher");// WSDL里面描述的接口名称
		call.addParameter("admDivCode",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("vtCode",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("treCode",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("vouDate",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("vouNo",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		String result = (String) call.invoke(new Object[]{admDivCode,vtCode,treCode,vouDate,vouNo});
		return result;
	}
	public static String synchronousVoucherStatus(Call call,String admDivCode, String vtCode, String treCode,
			String vouDate ,String vouNo) throws RemoteException{
		call.setOperationName("synchronousVoucherStatus");// WSDL里面描述的接口名称
		call.addParameter("admDivCode",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("vtCode",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("treCode",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("vouDate",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("vouNo",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		String result = (String) call.invoke(new Object[]{admDivCode,vtCode,treCode,vouDate,vouNo});
		return result;
	}
	public static HashMap<String, String> sendBizSeriData(Call call,String namespace) throws RemoteException{
		HashMap<String, String> resultMap = new HashMap<String, String>();
		StringBuffer params = new StringBuffer();
		params.append("TRECODE:0900080000,ACCTDATE:20141104,TRIMFLAG:1,BUDGETTYPE:1,TAXORGCODE:13101040000,BELONGFLAG:1,DIVIDEGROUP:1,BILLTYPE:2");
//		String[] treList = new String[2];
//		treList[0] = "0900080000";
//		treList[1] = "0900080000";
//		ArrayList paramsList = new ArrayList();
//		paramsList.add("1");
//		paramsList.add("1");
//		paramsList.add("13101040000");
//		paramsList.add("1");
//		paramsList.add("1");2
//		paramsList.add("20141222");
//		ArrayList exptList = new ArrayList();
//		exptList.add("2");
//		exptList.add("3");
//		exptList.add("9");
		call.setOperationName("sendBizSeriData");// WSDL里面描述的接口名称
		call.addParameter("params",
				XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
//		call.addParameter("paramsList",
//				XMLType.XSD_ANYTYPE,
//				javax.xml.rpc.ParameterMode.IN);// 接口的参数
//		call.addParameter("exptList",
//				XMLType.XSD_ANYTYPE,
//				javax.xml.rpc.ParameterMode.IN);// 接口的参数
//		call.addParameter("msg",
//				org.apache.axis.encoding.XMLType.XSD_STRING,
//				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		Object o =  call.invoke(new Object[]{params.toString()});
			System.out.println(o);
		return resultMap;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

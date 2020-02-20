package com.cfcc.tbs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.UUID;

import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TbsWebservice {
	private static Log logger = LogFactory.getLog(TbsWebservice.class);

	private static String ITFEServerIp, ITFEServerPort, WriteVoucherFilePath;

	public static void main(String[] args) {
		try {
			if (null == args || args.length == 0) {
				logger.info("main参数args不能为空");
				return;
			}
			// 读取properties文件
			Properties properties = new Properties();
			InputStream in = TbsWebservice.class
					.getResourceAsStream("TbsVoucher.properties");
			if(null == in){
				logger.error("读取配置参数信息失败！");
				return;
			}
			properties.load(in);
			in.close();
			if (properties.containsKey("ITFEServerIp")) {
				ITFEServerIp = properties.getProperty("ITFEServerIp");
			}
			if (properties.containsKey("ITFEServerPort")) {
				ITFEServerPort = properties.getProperty("ITFEServerPort");
			}
			if (properties.containsKey("WriteVoucherFilePath")) {
				WriteVoucherFilePath = properties
						.getProperty("WriteVoucherFilePath");
			}
			logger.info(args[0] + "操作开始......");
			// 直接引用远程的wsdl文件
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress("http://" + ITFEServerIp + ":"
					+ ITFEServerPort + "/ITFE/tbs");

			if ("readVoucher".equals(args[0]) && args.length == 4) { // 读取凭证
				// 1:vtCode 2:treCode 3:vouDate
				readVoucher(call, "", args[1], args[2], args[3]);
			} else if ("readVoucherAgain".equals(args[0]) && args.length == 4) { // 重新读取凭证
				// 1:vtCode 2:treCode 3:vouDate
				readVoucherAgain(call, "", args[1], args[2], args[3]);
			} else if ("confirmVoucher".equals(args[0]) && args.length == 5) { // 确认签收凭证
				// 1:vtCode 2:treCode 3:vouDate 4:vouNo
				confirmVoucher(call, "", args[1], args[2], args[3], args[4]);
			} else if ("returnVoucherBack".equals(args[0]) && args.length == 6) { // 退回凭证
				// 1:vtCode 2:treCode 3:vouDate 4:vouNo 5:失败原因
				returnVoucherBack(call, "", args[1], args[2], args[3], args[4],
						args[5]);
			} else if ("sendVoucher".equals(args[0]) && args.length == 5) { // 发送凭证
				// 1:vtCode 2:treCode 3:vouDate 4:vouNo
				sendVoucher(call, "", args[1], args[2], args[3], args[4]);
			} else if ("synchronousVoucherStatus".equals(args[0]) // 同步状态
					&& args.length == 5) {
				// 1:vtCode 2:treCode 3:vouDate 4:vouNo
				synchronousVoucherStatus(call, "", args[1], args[2], args[3], args[4]);
			} else {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < args.length; i++) {
					sb.append("接收参数" + (i + 1) + ":" + args[i] + "\t");
				}
				logger.error(sb.toString() + "传入参数有误");
			}
		} catch (IOException e) {
			logger.error("配置文件未找到！");
			logger.error(e.getMessage(), e);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			logger.error("调用WebService失败！");
			logger.error(e.getMessage(), e);
		} finally {
			logger.info(args[0] + "操作结束。");
		}
	}

	public static void readVoucher(Call call, String admDivCode, String vtCode,
			String treCode, String vouDate) {
		try {
			logger.info("开始读取数据参数readVoucher[业务类型:" + vtCode + "国库代码:" + treCode
					+ "凭证日期:" + vouDate + "]");
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
			String result = (String) call.invoke(new Object[] { admDivCode,
					vtCode, treCode, vouDate });
			if (null == result || result.length() == 0) {
				logger.info("数据参数readVoucher[业务类型:" + vtCode + "国库代码:" + treCode
						+ "凭证日期:" + vouDate + "]读取内容为空");
				return;
			}
			logger.info("读取内容readVoucher：" + base64Decode(result));
			File voucherFile = new File(WriteVoucherFilePath + File.separator
					+ "Vouhcer" + File.separator + vouDate + File.separator
					+ vtCode + "_" + treCode + "_"
					+ UUID.randomUUID().toString() + ".msg");
			wirteFile(voucherFile, result);
			logger.info("结束读取数据参数readVoucher[业务类型:" + vtCode + "国库代码:" + treCode
					+ "凭证日期:" + vouDate + "]");

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			logger.error("读取数据参数readVoucher[业务类型:" + vtCode + "国库代码:" + treCode
					+ "凭证日期:" + vouDate + "]失败");
			logger.error(e.getMessage(), e);
		}

	}

	public static void readVoucherAgain(Call call, String admDivCode,
			String vtCode, String treCode, String vouDate) {
		try {
			logger.info("重新读取数据参数readVoucherAgain[业务类型:" + vtCode + "国库代码:"
					+ treCode + "凭证日期:" + vouDate + "]");
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
			String result = (String) call.invoke(new Object[] { admDivCode,
					vtCode, treCode, vouDate });
			if (null == result || result.length() == 0) {
				logger.info("数据参数readVoucher[业务类型:" + vtCode + "国库代码:" + treCode
						+ "凭证日期:" + vouDate + "]读取内容为空");
				return;
			}
			logger.info("读取内容readVoucherAgain：" + base64Decode(result));
			File voucherFile = new File(WriteVoucherFilePath + File.separator
					+ "Vouhcer" + File.separator + vouDate + File.separator
					+ vtCode + "_" + treCode + "_"
					+ UUID.randomUUID().toString() + ".msg");
			wirteFile(voucherFile, result);
			logger.info("结束重新读取数据参数readVoucherAgain[业务类型:" + vtCode + "国库代码:"
					+ treCode + "凭证日期:" + vouDate + "]");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			logger.error("重新读取数据参数readVoucherAgain[业务类型:" + vtCode + "国库代码:"
					+ treCode + "凭证日期:" + vouDate + "]失败");
			logger.error(e.getMessage(), e);
		}
	}

	public static String userLoginAndOut(Call call, String orgCode,
			String password, String operate) throws RemoteException {
		call.setOperationName("userLoginOrOut");// WSDL里面描述的接口名称
		call.addParameter("orgCode",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("password",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("operate",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		String result = (String) call.invoke(new Object[] { orgCode, password,
				operate });
		return result;
	}

	public static String confirmVoucher(Call call, String admDivCode,
			String vtCode, String treCode, String vouDate, String vouNo) {
		String result = null;
		try {
			logger.info("确认签收数据参数confirmVoucher[业务类型:" + vtCode + "国库代码:"
					+ treCode + "凭证日期:" + vouDate + "凭证编号:" + vouNo + "]");
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
			result = (String) call.invoke(new Object[] { admDivCode, vtCode,
					treCode, vouDate, vouNo });
			logger.info("确认签收数据参数confirmVoucher[业务类型:" + vtCode + "国库代码:"
					+ treCode + "凭证日期:" + vouDate + "凭证编号:" + vouNo + "]返回结果:"
					+ base64Decode(result));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			logger.error("确认签收数据参数confirmVoucher[业务类型:" + vtCode + "国库代码:"
					+ treCode + "凭证日期:" + vouDate + "凭证编号:" + vouNo + "]失败");
			logger.error(e.getMessage(), e);
		}
		return result;
	}

	public static String fundLiquidation(Call call, String admDivCode,
			String vtCode, String treCode, String vouDate, String vouNo) {
		String result = null;
		try {
			logger.info("清算资金fundLiquidation参数[业务类型：" + vtCode + "国库代码:" + treCode
					+ "凭证日期：" + vouDate + "凭证编号:" + vouNo + "]");
			call.setOperationName("fundLiquidation");// WSDL里面描述的接口名称
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
			result = (String) call.invoke(new Object[] { admDivCode, vtCode,
					treCode, vouDate, vouNo });
			logger.info("清算资金fundLiquidation参数[业务类型：" + vtCode + "国库代码:" + treCode
					+ "凭证日期：" + vouDate + "凭证编号:" + vouNo + "]结果:" + result);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			logger.error("清算资金fundLiquidation参数[业务类型：" + vtCode + "国库代码:"
					+ treCode + "凭证日期：" + vouDate + "凭证编号:" + vouNo + "]失败！");
			logger.error(e.getMessage(), e);
		}
		return result;
	}

	public static String returnVoucherBack(Call call, String admDivCode,
			String vtCode, String treCode, String vouDate, String vouNo,
			String errMsg) {
		String result = null;
		try {
			logger.info("凭证退回returnVoucherBack参数[业务类型：" + vtCode + "国库代码:"
					+ treCode + "凭证日期：" + vouDate + "凭证编号:" + vouNo + "失败原因:"
					+ errMsg + "]");
			call.setOperationName("returnVoucherBack");// WSDL里面描述的接口名称
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
			call.addParameter("errMsg",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// 接口的参数
			result = (String) call.invoke(new Object[] { admDivCode, vtCode,
					treCode, vouDate, vouNo, errMsg});
			logger.info("凭证退回returnVoucherBack参数[业务类型：" + vtCode + "国库代码:"
					+ treCode + "凭证日期：" + vouDate + "凭证编号:" + vouNo + "失败原因:"
					+ errMsg + "]结果:" + base64Decode(result));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			logger.error("凭证退回returnVoucherBack参数[业务类型：" + vtCode + "国库代码:"
					+ treCode + "凭证日期：" + vouDate + "凭证编号:" + vouNo + "失败原因:"
					+ errMsg + "]失败");
			logger.error(e.getMessage(), e);
		}
		return result;
	}

	public static String sendVoucher(Call call, String admDivCode,
			String vtCode, String treCode, String vouDate, String vouNo) {
		String result = null;
		try {
			logger.info("发送凭证sendVoucher参数[业务类型：" + vtCode + "国库代码:" + treCode
					+ "凭证日期：" + vouDate + "凭证编号:" + vouNo + "]");
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
			result = (String) call.invoke(new Object[] { admDivCode, vtCode,
					treCode, vouDate, vouNo });
			logger.info("发送凭证sendVoucher参数[业务类型：" + vtCode + "国库代码:" + treCode
					+ "凭证日期：" + vouDate + "凭证编号:" + vouNo + "]结果:" + result);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			logger.error("发送凭证sendVoucher参数[业务类型：" + vtCode + "国库代码:" + treCode
					+ "凭证日期：" + vouDate + "凭证编号:" + vouNo + "]失败！");
			logger.error(e.getMessage(), e);
		}
		return result;
	}

	public static String synchronousVoucherStatus(Call call, String admDivCode,
			String vtCode, String treCode, String vouDate, String vouNo) {
		String result = null;
		try {
			logger.info("状态同步synchronousVoucherStatus参数[业务类型：" + vtCode + "国库代码:"
					+ treCode + "凭证日期：" + vouDate + "凭证编号:" + vouNo + "]");
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
			result = (String) call.invoke(new Object[] { admDivCode, vtCode,
					treCode, vouDate, vouNo });
			logger.info("状态同步synchronousVoucherStatus参数[业务类型：" + vtCode + "国库代码:"
					+ treCode + "凭证日期：" + vouDate + "凭证编号:" + vouNo + "]结果:"
					+ base64Decode(result));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			logger.error("状态同步synchronousVoucherStatus参数[业务类型：" + vtCode + "国库代码:"
					+ treCode + "凭证日期：" + vouDate + "凭证编号:" + vouNo + "]失败");
			logger.error(e.getMessage(), e);
		}
		return result;
	}

	public static String genVoucherXml(Call call, String vtcode,
			String fileContent) throws RemoteException {
		call.setOperationName("genVoucherXml");// WSDL里面描述的接口名称
		call.addParameter("voucherType",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		call.addParameter("fileContent",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// 接口的参数
		String result = (String) call
				.invoke(new Object[] { vtcode, fileContent });
		return result;
	}

	public static void testConnect(Call call) throws RemoteException {
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
		call.invoke(new Object[] { "102", "20150806", "090000000002" });
	}

	private static void wirteFile(File voucherFile, String fileContent) {
		FileOutputStream output = null;
		try {
			File voucherFileDir = new File(voucherFile.getParent());
			if (!voucherFileDir.exists()) {
				voucherFileDir.mkdirs();
			}
			output = new FileOutputStream(voucherFile.getPath(), false);
			output.write(fileContent.getBytes("GBK"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {
					logger.error("关闭文件出错！", ex);

				}
			}
		}
	}

	/**
	 * 由base64编码的字符串，生成源字符串 gbk
	 * 
	 * @param src
	 *            base64编码的字符串
	 * @return 源字符串
	 * @throws UnsupportedEncodingException
	 */
	private static String base64Decode(String src) {
		String ret = null;
		try {
			byte[] out = Base64.decode(src);
			ret = new String(out, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error("解密base64失败！");
			logger.info(e.getMessage(), e);
		}
		return ret;
	}

	/**
	 * 对字符串进行base64编码
	 * 
	 * @param src字符串
	 * @return base64字符串
	 * @throws UnsupportedEncodingException
	 */
	private static String base64Encode(String src) {
		String ret = null;
		try {
			byte[] outByte = Base64.encode(src.getBytes());
			ret = new String(outByte, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error("进行base64加密失败！");
			logger.info(e.getMessage(), e);
		}
		return ret;
	}

}

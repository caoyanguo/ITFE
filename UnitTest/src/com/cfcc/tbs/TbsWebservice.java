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
				logger.info("main����args����Ϊ��");
				return;
			}
			// ��ȡproperties�ļ�
			Properties properties = new Properties();
			InputStream in = TbsWebservice.class
					.getResourceAsStream("TbsVoucher.properties");
			if(null == in){
				logger.error("��ȡ���ò�����Ϣʧ�ܣ�");
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
			logger.info(args[0] + "������ʼ......");
			// ֱ������Զ�̵�wsdl�ļ�
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress("http://" + ITFEServerIp + ":"
					+ ITFEServerPort + "/ITFE/tbs");

			if ("readVoucher".equals(args[0]) && args.length == 4) { // ��ȡƾ֤
				// 1:vtCode 2:treCode 3:vouDate
				readVoucher(call, "", args[1], args[2], args[3]);
			} else if ("readVoucherAgain".equals(args[0]) && args.length == 4) { // ���¶�ȡƾ֤
				// 1:vtCode 2:treCode 3:vouDate
				readVoucherAgain(call, "", args[1], args[2], args[3]);
			} else if ("confirmVoucher".equals(args[0]) && args.length == 5) { // ȷ��ǩ��ƾ֤
				// 1:vtCode 2:treCode 3:vouDate 4:vouNo
				confirmVoucher(call, "", args[1], args[2], args[3], args[4]);
			} else if ("returnVoucherBack".equals(args[0]) && args.length == 6) { // �˻�ƾ֤
				// 1:vtCode 2:treCode 3:vouDate 4:vouNo 5:ʧ��ԭ��
				returnVoucherBack(call, "", args[1], args[2], args[3], args[4],
						args[5]);
			} else if ("sendVoucher".equals(args[0]) && args.length == 5) { // ����ƾ֤
				// 1:vtCode 2:treCode 3:vouDate 4:vouNo
				sendVoucher(call, "", args[1], args[2], args[3], args[4]);
			} else if ("synchronousVoucherStatus".equals(args[0]) // ͬ��״̬
					&& args.length == 5) {
				// 1:vtCode 2:treCode 3:vouDate 4:vouNo
				synchronousVoucherStatus(call, "", args[1], args[2], args[3], args[4]);
			} else {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < args.length; i++) {
					sb.append("���ղ���" + (i + 1) + ":" + args[i] + "\t");
				}
				logger.error(sb.toString() + "�����������");
			}
		} catch (IOException e) {
			logger.error("�����ļ�δ�ҵ���");
			logger.error(e.getMessage(), e);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			logger.error("����WebServiceʧ�ܣ�");
			logger.error(e.getMessage(), e);
		} finally {
			logger.info(args[0] + "����������");
		}
	}

	public static void readVoucher(Call call, String admDivCode, String vtCode,
			String treCode, String vouDate) {
		try {
			logger.info("��ʼ��ȡ���ݲ���readVoucher[ҵ������:" + vtCode + "�������:" + treCode
					+ "ƾ֤����:" + vouDate + "]");
			call.setOperationName("readVoucher");// WSDL���������Ľӿ�����
			call.addParameter("admDivCode",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("vtCode",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("treCode",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("vouDate",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			String result = (String) call.invoke(new Object[] { admDivCode,
					vtCode, treCode, vouDate });
			if (null == result || result.length() == 0) {
				logger.info("���ݲ���readVoucher[ҵ������:" + vtCode + "�������:" + treCode
						+ "ƾ֤����:" + vouDate + "]��ȡ����Ϊ��");
				return;
			}
			logger.info("��ȡ����readVoucher��" + base64Decode(result));
			File voucherFile = new File(WriteVoucherFilePath + File.separator
					+ "Vouhcer" + File.separator + vouDate + File.separator
					+ vtCode + "_" + treCode + "_"
					+ UUID.randomUUID().toString() + ".msg");
			wirteFile(voucherFile, result);
			logger.info("������ȡ���ݲ���readVoucher[ҵ������:" + vtCode + "�������:" + treCode
					+ "ƾ֤����:" + vouDate + "]");

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			logger.error("��ȡ���ݲ���readVoucher[ҵ������:" + vtCode + "�������:" + treCode
					+ "ƾ֤����:" + vouDate + "]ʧ��");
			logger.error(e.getMessage(), e);
		}

	}

	public static void readVoucherAgain(Call call, String admDivCode,
			String vtCode, String treCode, String vouDate) {
		try {
			logger.info("���¶�ȡ���ݲ���readVoucherAgain[ҵ������:" + vtCode + "�������:"
					+ treCode + "ƾ֤����:" + vouDate + "]");
			call.setOperationName("readVoucherAgain");// WSDL���������Ľӿ�����
			call.addParameter("admDivCode",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("vtCode",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("treCode",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("vouDate",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			String result = (String) call.invoke(new Object[] { admDivCode,
					vtCode, treCode, vouDate });
			if (null == result || result.length() == 0) {
				logger.info("���ݲ���readVoucher[ҵ������:" + vtCode + "�������:" + treCode
						+ "ƾ֤����:" + vouDate + "]��ȡ����Ϊ��");
				return;
			}
			logger.info("��ȡ����readVoucherAgain��" + base64Decode(result));
			File voucherFile = new File(WriteVoucherFilePath + File.separator
					+ "Vouhcer" + File.separator + vouDate + File.separator
					+ vtCode + "_" + treCode + "_"
					+ UUID.randomUUID().toString() + ".msg");
			wirteFile(voucherFile, result);
			logger.info("�������¶�ȡ���ݲ���readVoucherAgain[ҵ������:" + vtCode + "�������:"
					+ treCode + "ƾ֤����:" + vouDate + "]");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			logger.error("���¶�ȡ���ݲ���readVoucherAgain[ҵ������:" + vtCode + "�������:"
					+ treCode + "ƾ֤����:" + vouDate + "]ʧ��");
			logger.error(e.getMessage(), e);
		}
	}

	public static String userLoginAndOut(Call call, String orgCode,
			String password, String operate) throws RemoteException {
		call.setOperationName("userLoginOrOut");// WSDL���������Ľӿ�����
		call.addParameter("orgCode",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
		call.addParameter("password",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
		call.addParameter("operate",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
		String result = (String) call.invoke(new Object[] { orgCode, password,
				operate });
		return result;
	}

	public static String confirmVoucher(Call call, String admDivCode,
			String vtCode, String treCode, String vouDate, String vouNo) {
		String result = null;
		try {
			logger.info("ȷ��ǩ�����ݲ���confirmVoucher[ҵ������:" + vtCode + "�������:"
					+ treCode + "ƾ֤����:" + vouDate + "ƾ֤���:" + vouNo + "]");
			call.setOperationName("confirmVoucher");// WSDL���������Ľӿ�����
			call.addParameter("admDivCode",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("vtCode",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("treCode",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("vouDate",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("vouNo",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			result = (String) call.invoke(new Object[] { admDivCode, vtCode,
					treCode, vouDate, vouNo });
			logger.info("ȷ��ǩ�����ݲ���confirmVoucher[ҵ������:" + vtCode + "�������:"
					+ treCode + "ƾ֤����:" + vouDate + "ƾ֤���:" + vouNo + "]���ؽ��:"
					+ base64Decode(result));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			logger.error("ȷ��ǩ�����ݲ���confirmVoucher[ҵ������:" + vtCode + "�������:"
					+ treCode + "ƾ֤����:" + vouDate + "ƾ֤���:" + vouNo + "]ʧ��");
			logger.error(e.getMessage(), e);
		}
		return result;
	}

	public static String fundLiquidation(Call call, String admDivCode,
			String vtCode, String treCode, String vouDate, String vouNo) {
		String result = null;
		try {
			logger.info("�����ʽ�fundLiquidation����[ҵ�����ͣ�" + vtCode + "�������:" + treCode
					+ "ƾ֤���ڣ�" + vouDate + "ƾ֤���:" + vouNo + "]");
			call.setOperationName("fundLiquidation");// WSDL���������Ľӿ�����
			call.addParameter("admDivCode",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("vtCode",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("treCode",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("vouDate",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("vouNo",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			result = (String) call.invoke(new Object[] { admDivCode, vtCode,
					treCode, vouDate, vouNo });
			logger.info("�����ʽ�fundLiquidation����[ҵ�����ͣ�" + vtCode + "�������:" + treCode
					+ "ƾ֤���ڣ�" + vouDate + "ƾ֤���:" + vouNo + "]���:" + result);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			logger.error("�����ʽ�fundLiquidation����[ҵ�����ͣ�" + vtCode + "�������:"
					+ treCode + "ƾ֤���ڣ�" + vouDate + "ƾ֤���:" + vouNo + "]ʧ�ܣ�");
			logger.error(e.getMessage(), e);
		}
		return result;
	}

	public static String returnVoucherBack(Call call, String admDivCode,
			String vtCode, String treCode, String vouDate, String vouNo,
			String errMsg) {
		String result = null;
		try {
			logger.info("ƾ֤�˻�returnVoucherBack����[ҵ�����ͣ�" + vtCode + "�������:"
					+ treCode + "ƾ֤���ڣ�" + vouDate + "ƾ֤���:" + vouNo + "ʧ��ԭ��:"
					+ errMsg + "]");
			call.setOperationName("returnVoucherBack");// WSDL���������Ľӿ�����
			call.addParameter("admDivCode",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("vtCode",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("treCode",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("vouDate",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("vouNo",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("errMsg",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			result = (String) call.invoke(new Object[] { admDivCode, vtCode,
					treCode, vouDate, vouNo, errMsg});
			logger.info("ƾ֤�˻�returnVoucherBack����[ҵ�����ͣ�" + vtCode + "�������:"
					+ treCode + "ƾ֤���ڣ�" + vouDate + "ƾ֤���:" + vouNo + "ʧ��ԭ��:"
					+ errMsg + "]���:" + base64Decode(result));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			logger.error("ƾ֤�˻�returnVoucherBack����[ҵ�����ͣ�" + vtCode + "�������:"
					+ treCode + "ƾ֤���ڣ�" + vouDate + "ƾ֤���:" + vouNo + "ʧ��ԭ��:"
					+ errMsg + "]ʧ��");
			logger.error(e.getMessage(), e);
		}
		return result;
	}

	public static String sendVoucher(Call call, String admDivCode,
			String vtCode, String treCode, String vouDate, String vouNo) {
		String result = null;
		try {
			logger.info("����ƾ֤sendVoucher����[ҵ�����ͣ�" + vtCode + "�������:" + treCode
					+ "ƾ֤���ڣ�" + vouDate + "ƾ֤���:" + vouNo + "]");
			call.setOperationName("sendVoucher");// WSDL���������Ľӿ�����
			call.addParameter("admDivCode",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("vtCode",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("treCode",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("vouDate",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("vouNo",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			result = (String) call.invoke(new Object[] { admDivCode, vtCode,
					treCode, vouDate, vouNo });
			logger.info("����ƾ֤sendVoucher����[ҵ�����ͣ�" + vtCode + "�������:" + treCode
					+ "ƾ֤���ڣ�" + vouDate + "ƾ֤���:" + vouNo + "]���:" + result);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			logger.error("����ƾ֤sendVoucher����[ҵ�����ͣ�" + vtCode + "�������:" + treCode
					+ "ƾ֤���ڣ�" + vouDate + "ƾ֤���:" + vouNo + "]ʧ�ܣ�");
			logger.error(e.getMessage(), e);
		}
		return result;
	}

	public static String synchronousVoucherStatus(Call call, String admDivCode,
			String vtCode, String treCode, String vouDate, String vouNo) {
		String result = null;
		try {
			logger.info("״̬ͬ��synchronousVoucherStatus����[ҵ�����ͣ�" + vtCode + "�������:"
					+ treCode + "ƾ֤���ڣ�" + vouDate + "ƾ֤���:" + vouNo + "]");
			call.setOperationName("synchronousVoucherStatus");// WSDL���������Ľӿ�����
			call.addParameter("admDivCode",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("vtCode",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("treCode",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("vouDate",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			call.addParameter("vouNo",
					org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
			result = (String) call.invoke(new Object[] { admDivCode, vtCode,
					treCode, vouDate, vouNo });
			logger.info("״̬ͬ��synchronousVoucherStatus����[ҵ�����ͣ�" + vtCode + "�������:"
					+ treCode + "ƾ֤���ڣ�" + vouDate + "ƾ֤���:" + vouNo + "]���:"
					+ base64Decode(result));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			logger.error("״̬ͬ��synchronousVoucherStatus����[ҵ�����ͣ�" + vtCode + "�������:"
					+ treCode + "ƾ֤���ڣ�" + vouDate + "ƾ֤���:" + vouNo + "]ʧ��");
			logger.error(e.getMessage(), e);
		}
		return result;
	}

	public static String genVoucherXml(Call call, String vtcode,
			String fileContent) throws RemoteException {
		call.setOperationName("genVoucherXml");// WSDL���������Ľӿ�����
		call.addParameter("voucherType",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
		call.addParameter("fileContent",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
		String result = (String) call
				.invoke(new Object[] { vtcode, fileContent });
		return result;
	}

	public static void testConnect(Call call) throws RemoteException {
		call.setOperationName("testConnect");// WSDL���������Ľӿ�����
		call.addParameter("desOrgCode",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
		call.addParameter("vouDate",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
		call.addParameter("orgCode",
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
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
					logger.error("�ر��ļ�����", ex);

				}
			}
		}
	}

	/**
	 * ��base64������ַ���������Դ�ַ��� gbk
	 * 
	 * @param src
	 *            base64������ַ���
	 * @return Դ�ַ���
	 * @throws UnsupportedEncodingException
	 */
	private static String base64Decode(String src) {
		String ret = null;
		try {
			byte[] out = Base64.decode(src);
			ret = new String(out, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error("����base64ʧ�ܣ�");
			logger.info(e.getMessage(), e);
		}
		return ret;
	}

	/**
	 * ���ַ�������base64����
	 * 
	 * @param src�ַ���
	 * @return base64�ַ���
	 * @throws UnsupportedEncodingException
	 */
	private static String base64Encode(String src) {
		String ret = null;
		try {
			byte[] outByte = Base64.encode(src.getBytes());
			ret = new String(outByte, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error("����base64����ʧ�ܣ�");
			logger.info(e.getMessage(), e);
		}
		return ret;
	}

}

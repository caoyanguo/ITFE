package com.cfcc.itfe.service.recbiz.bankvouchervalidate;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.sendbiz.exporttbsforbj.IProcessHandler;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;

/**
 * ����ƾ֤У�鸨����
 * 
 * @author hua
 * 
 */
public abstract class AbstractBankVoucherValidateProcesser implements IProcessHandler {
	// ��¼��Ϣ
	protected ITFELoginInfo loginfo;

	/**
	 * ���������ˮ��
	 * 
	 * @return
	 * @throws ITFEBizException
	 * @throws NumberFormatExceptio
	 */
	protected String generateVousrlno() throws NumberFormatException, ITFEBizException {
		return VoucherUtil.getGrantSequence();
	}

	/**
	 * ���ļ�ת����document
	 * 
	 * @param fileName
	 * @return
	 */
	public static Document parseFile2Doc(String fileName) {
		try {
			return DocumentHelper.parseText(readFile(fileName));
		} catch (DocumentException e) {
			throw new RuntimeException("ת��XML��DOC�ĵ��쳣!", e);
		}
	}

	/**
	 * ��ȡ�ļ�������
	 * 
	 * @param fileName
	 * @return
	 */
	public static String resloveFileType(String fileName) {
		try {
			Document document = DocumentHelper.parseText(readFile(fileName));
			Element rootElement = document.getRootElement();
			Element vtCodeElement = null;
			if (rootElement.getName().equalsIgnoreCase("voucher")) { // ���ڵ����Voucher�����
				vtCodeElement = rootElement.element("VtCode");
			} else { // ���ڵ㲻��Voucher�����, ����XPathȥ����Ψһ��һ��Voucher(�����Ƕ��Voucher�����)
				Element elementVoucher = (Element) rootElement.selectSingleNode("Voucher");
				vtCodeElement = elementVoucher.element("VtCode");
			}
			return vtCodeElement.getText();
		} catch (DocumentException e) {
			throw new RuntimeException("ת��XML��DOC�ĵ��쳣!", e);
		}
	}

	public static void main(String[] args) {
		String fileName = "E:\\8202_wzh\\8202����\\8202.xml";
		String fileTypeName = resloveFileType(fileName);
		System.out.println(fileTypeName);
	}

	/**
	 * ���ո����ķָ��������ļ�
	 * 
	 * @param file
	 * @param split
	 * @return
	 */
	public static List<String[]> readFile(String file, String split) {
		try {
			return FileUtil.getInstance().readFileWithLine(file, split);
		} catch (FileOperateException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ��ȡ�ļ�����
	 * 
	 * @param file
	 * @return
	 */
	public static String readFile(String file) {
		try {
			return FileUtil.getInstance().readFile(file);
		} catch (FileOperateException e) {
			throw new RuntimeException(e);
		}
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}
}

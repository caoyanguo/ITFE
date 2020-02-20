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
 * 商行凭证校验辅助类
 * 
 * @author hua
 * 
 */
public abstract class AbstractBankVoucherValidateProcesser implements IProcessHandler {
	// 登录信息
	protected ITFELoginInfo loginfo;

	/**
	 * 产生随机流水号
	 * 
	 * @return
	 * @throws ITFEBizException
	 * @throws NumberFormatExceptio
	 */
	protected String generateVousrlno() throws NumberFormatException, ITFEBizException {
		return VoucherUtil.getGrantSequence();
	}

	/**
	 * 将文件转换成document
	 * 
	 * @param fileName
	 * @return
	 */
	public static Document parseFile2Doc(String fileName) {
		try {
			return DocumentHelper.parseText(readFile(fileName));
		} catch (DocumentException e) {
			throw new RuntimeException("转换XML到DOC文档异常!", e);
		}
	}

	/**
	 * 获取文件的类型
	 * 
	 * @param fileName
	 * @return
	 */
	public static String resloveFileType(String fileName) {
		try {
			Document document = DocumentHelper.parseText(readFile(fileName));
			Element rootElement = document.getRootElement();
			Element vtCodeElement = null;
			if (rootElement.getName().equalsIgnoreCase("voucher")) { // 根节点就是Voucher的情况
				vtCodeElement = rootElement.element("VtCode");
			} else { // 根节点不是Voucher的情况, 根据XPath去查找唯一的一个Voucher(不考虑多个Voucher的情况)
				Element elementVoucher = (Element) rootElement.selectSingleNode("Voucher");
				vtCodeElement = elementVoucher.element("VtCode");
			}
			return vtCodeElement.getText();
		} catch (DocumentException e) {
			throw new RuntimeException("转换XML到DOC文档异常!", e);
		}
	}

	public static void main(String[] args) {
		String fileName = "E:\\8202_wzh\\8202报文\\8202.xml";
		String fileTypeName = resloveFileType(fileName);
		System.out.println(fileTypeName);
	}

	/**
	 * 按照给定的分隔符解析文件
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
	 * 读取文件内容
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

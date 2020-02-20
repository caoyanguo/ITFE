package com.cfcc.tbs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.configuration.ConfigurationUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.util.FileUtil;
import com.csvreader.CsvReader;

public class CsvToXmlTest2 {
	public static void main(String[] args) {
		//�ļ�·��
		long beginTime = System.currentTimeMillis();
		String path = "C:/Users/Administrator/Desktop/Results.csv";
		CsvToXmlTest2.csvToXml(path);
		long endTime = System.currentTimeMillis();
		System.out.println("��ʱ��"+(endTime-beginTime)/1000+"s");
	}
	public static void csvToXml(String path) {
		Integer i = 0;
		try {
			//ͷ
			String mapping1 = "C:/config/Msg6103.xml";
			URL url1 = ConfigurationUtils.locate(mapping1);
			SAXReader reader1 = new SAXReader();
			Document doc1 = reader1.read(url1);
			
			String mapping2 = "C:/config/Msg6103.xml";
			URL url2 = ConfigurationUtils.locate(mapping2);
			SAXReader reader2 = new SAXReader();
			Document doc2 = reader2.read(url2);
			
			File file = new File(path);
			FileInputStream fileIn = new FileInputStream(file);
			InputStreamReader in = new InputStreamReader(fileIn,"UTF-8");
			CsvReader csvReader = new CsvReader(in);
			csvReader.readHeaders();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			//��֯ͷ���ڵ�
			Element exportDate = (Element)doc2.selectSingleNode("/CFX/MSG/VouchHead6103/ExportDate");
			exportDate.setText(sdf.format(new Date()));
			//�������
			Element vouchBody = (Element)doc2.selectSingleNode("/CFX/MSG/VouchBody6103");
			while (csvReader.readRecord()) {
				Element treCode = (Element)doc2.selectSingleNode("/CFX/MSG/VouchHead6103/TreCode");
				treCode.setText(csvReader.get(3).replace("'", ""));
				Element vouchBill6103 = DocumentHelper.createElement("VouchBill6103");
				//���ջ���
				Element taxOrgCode = DocumentHelper.createElement("taxOrgCode");
				taxOrgCode.setText(csvReader.get(5).replace("'", ""));
				vouchBill6103.add(taxOrgCode);
				//ƾ֤����
				Element expTaxVouNo = DocumentHelper.createElement("ExpTaxVouNo");;
				expTaxVouNo.setText(csvReader.get(6).replace("'", ""));
				vouchBill6103.add(expTaxVouNo);
				//����ƾ֤����
				Element exportVouType = DocumentHelper.createElement("ExportVouType");
				exportVouType.setText(csvReader.get(7).replace("'", ""));
				vouchBill6103.add(exportVouType);
				//Ԥ������
				Element budgetType = DocumentHelper.createElement("BudgetType");
				budgetType.setText(csvReader.get(10).replace("'", ""));
				vouchBill6103.add(budgetType);
				//Ԥ�㼶��
				Element budgetLevelCode = DocumentHelper.createElement("BudgetLevelCode");
				budgetLevelCode.setText(csvReader.get(9).replace("'", ""));
				vouchBill6103.add(budgetLevelCode);
				//Ԥ���Ŀ
				Element budgetSubjectCode = DocumentHelper.createElement("BudgetSubjectCode");
				budgetSubjectCode.setText(csvReader.get(11).replace("'", ""));
				vouchBill6103.add(budgetSubjectCode);
				//���׽��TraAmt
				Element traAmt = DocumentHelper.createElement("TraAmt");
				traAmt.setText(csvReader.get(13));
				vouchBill6103.add(traAmt);
				//ƾ֤��ԴOrigin
				Element origin = DocumentHelper.createElement("Origin");
//				origin.setText(csvReader.get(13));
				vouchBill6103.add(origin);
				vouchBody.add(vouchBill6103);
				i++;
			}
			//����
			Element allNum = (Element)doc2.selectSingleNode("/CFX/MSG/VouchHead6103/AllNum");
			allNum.setText(i.toString());
			//String temp = formatXml(doc2.asXML());
			//�����ɵı���д���ļ���
			//writeToFile(temp);
			writeFile("e:/2.xml",doc2.asXML());
		} catch (Exception e) {
			System.err.println("�����쳣��"+e.getMessage());
		}
	}
	
	private static void writeToFile(String temp) {
		try {
			FileWriter write = new FileWriter("E:/����.txt");
			write.write("");//���ԭ�ļ�����
			write.write(temp);
			write.flush();
			write.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String formatXml(String str) throws Exception {
		  Document document = null;
		  document = DocumentHelper.parseText(str);
		  // ��ʽ�������ʽ
		  OutputFormat format = OutputFormat.createPrettyPrint();
		  format.setEncoding("UTF-8");
		  StringWriter writer = new StringWriter();
		  // ��ʽ�������
		  XMLWriter xmlWriter = new XMLWriter(writer, format);
		  // ��documentд�뵽�����
		  xmlWriter.write(document);
		  xmlWriter.close();
		  return writer.toString();
	}
	
	
	/**
	 * ��ָ��������д�뵽ָ�����ļ���
	 * 
	 * @param fileName
	 *            �ļ���
	 * @param fileContent
	 *            �ļ�����
	 * @throws FileOperateException
	 *             �����ļ�ʧ��
	 */
	public static void writeFile(String fileName, String fileContent)
			throws FileOperateException {
		long lBegin = 0;
	
		File file = new File(fileName);
		File dir = new File(file.getParent());

		FileOutputStream output = null;
		try {
			if (!dir.exists()) {
				dir.mkdirs();
			}
			output = new FileOutputStream(fileName, false);
			output.write(fileContent.getBytes("GBK"));

		} catch (IOException e) {
			String msg = new String("д�ļ�ʧ��,IO����." + fileName);
		
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("д�ļ�ʧ�ܣ�����ʱ�쳣." + fileName);
		
			throw new FileOperateException(msg, e);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {
				

				}
			}
		}

		
	}

}

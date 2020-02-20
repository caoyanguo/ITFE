package com.cfcc.tbs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.ConfigurationUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.csvreader.CsvReader;

public class CsvToXmlTest {
	public static void main(String[] args) {
		//�ļ�·��
		long beginTime = System.currentTimeMillis();
		String path = "C:/Users/Administrator/Desktop/Results.csv";
		CsvToXmlTest.csvToXml(path);
		long endTime = System.currentTimeMillis();
		System.out.println("��ʱ��"+(endTime-beginTime)/1000+"s");
	}
	public static void csvToXml(String path) {
		List<String> messages = new ArrayList<String>();
		Integer i = 0;
		try {
			String mapping = "c:/config/Msg6103.xml";
			URL url = ConfigurationUtils.locate(mapping);
			SAXReader reader = new SAXReader();
			Document doc = reader.read(url);
			File file = new File(path);
			FileInputStream fileIn = new FileInputStream(file);
			InputStreamReader in = new InputStreamReader(fileIn,"GBK");
			CsvReader csvReader = new CsvReader(in);
			csvReader.readHeaders();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			while (csvReader.readRecord()) {
				//��������
				Element exportDate = (Element)doc.selectSingleNode("/CFX/MSG/VouchHead6103/ExportDate");
				exportDate.setText(sdf.format(new Date()));
				//�������
				Element treCode = (Element)doc.selectSingleNode("/CFX/MSG/VouchHead6103/TreCode");
				treCode.setText(csvReader.get(3).replace("'", ""));
				//����
				Element allNum = (Element)doc.selectSingleNode("/CFX/MSG/VouchHead6103/AllNum");
				allNum.setText("1");
				//���ջ���
				Element taxOrgCode = (Element)doc.selectSingleNode("/CFX/MSG/VouchBody6103/VouchBill6103/TaxOrgCode");
				taxOrgCode.setText(csvReader.get(5).replace("'", ""));
				//ƾ֤����
				Element expTaxVouNo = (Element)doc.selectSingleNode("/CFX/MSG/VouchBody6103/VouchBill6103/ExpTaxVouNo");
				expTaxVouNo.setText(csvReader.get(6).replace("'", ""));
				//����ƾ֤����
				Element exportVouType = (Element)doc.selectSingleNode("/CFX/MSG/VouchBody6103/VouchBill6103/ExportVouType");
				exportVouType.setText(csvReader.get(7).replace("'", ""));
				//Ԥ������
				Element budgetType = (Element)doc.selectSingleNode("/CFX/MSG/VouchBody6103/VouchBill6103/BudgetType");
				budgetType.setText(csvReader.get(10).replace("'", ""));
				//Ԥ�㼶��
				Element budgetLevelCode = (Element)doc.selectSingleNode("/CFX/MSG/VouchBody6103/VouchBill6103/BudgetLevelCode");
				budgetLevelCode.setText(csvReader.get(9).replace("'", ""));
				//Ԥ���Ŀ
				Element budgetSubjectCode = (Element)doc.selectSingleNode("/CFX/MSG/VouchBody6103/VouchBill6103/BudgetSubjectCode");
				budgetSubjectCode.setText(csvReader.get(11).replace("'", ""));
				//���׽��TraAmt
				Element traAmt = (Element)doc.selectSingleNode("/CFX/MSG/VouchBody6103/VouchBill6103/TraAmt");
				traAmt.setText(csvReader.get(13));
				//ƾ֤��ԴOrigin
				Element origin = (Element)doc.selectSingleNode("/CFX/MSG/VouchBody6103/VouchBill6103/Origin");
//				origin.setText(csvReader.get(13));
				messages.add(doc.asXML());
				i++;
			}
		} catch (Exception e) {
			System.err.println("�����쳣��"+e.getMessage());
		}
		StringBuffer sb = new StringBuffer();
		for (int k = 0; k < messages.size(); k++) {
			sb.append(messages.get(k)+"\n");
		}
		writeToFile(sb.toString());
	}
	private static void writeToFile(String temp) {
		try {
			FileWriter write = new FileWriter("E:/����.txt");
			write.write(temp);
			write.flush();
			write.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

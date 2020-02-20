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
		//文件路径
		long beginTime = System.currentTimeMillis();
		String path = "C:/Users/Administrator/Desktop/Results.csv";
		CsvToXmlTest2.csvToXml(path);
		long endTime = System.currentTimeMillis();
		System.out.println("耗时："+(endTime-beginTime)/1000+"s");
	}
	public static void csvToXml(String path) {
		Integer i = 0;
		try {
			//头
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
			//组织头部节点
			Element exportDate = (Element)doc2.selectSingleNode("/CFX/MSG/VouchHead6103/ExportDate");
			exportDate.setText(sdf.format(new Date()));
			//国库代码
			Element vouchBody = (Element)doc2.selectSingleNode("/CFX/MSG/VouchBody6103");
			while (csvReader.readRecord()) {
				Element treCode = (Element)doc2.selectSingleNode("/CFX/MSG/VouchHead6103/TreCode");
				treCode.setText(csvReader.get(3).replace("'", ""));
				Element vouchBill6103 = DocumentHelper.createElement("VouchBill6103");
				//征收机关
				Element taxOrgCode = DocumentHelper.createElement("taxOrgCode");
				taxOrgCode.setText(csvReader.get(5).replace("'", ""));
				vouchBill6103.add(taxOrgCode);
				//凭证号码
				Element expTaxVouNo = DocumentHelper.createElement("ExpTaxVouNo");;
				expTaxVouNo.setText(csvReader.get(6).replace("'", ""));
				vouchBill6103.add(expTaxVouNo);
				//导出凭证类型
				Element exportVouType = DocumentHelper.createElement("ExportVouType");
				exportVouType.setText(csvReader.get(7).replace("'", ""));
				vouchBill6103.add(exportVouType);
				//预算种类
				Element budgetType = DocumentHelper.createElement("BudgetType");
				budgetType.setText(csvReader.get(10).replace("'", ""));
				vouchBill6103.add(budgetType);
				//预算级次
				Element budgetLevelCode = DocumentHelper.createElement("BudgetLevelCode");
				budgetLevelCode.setText(csvReader.get(9).replace("'", ""));
				vouchBill6103.add(budgetLevelCode);
				//预算科目
				Element budgetSubjectCode = DocumentHelper.createElement("BudgetSubjectCode");
				budgetSubjectCode.setText(csvReader.get(11).replace("'", ""));
				vouchBill6103.add(budgetSubjectCode);
				//交易金额TraAmt
				Element traAmt = DocumentHelper.createElement("TraAmt");
				traAmt.setText(csvReader.get(13));
				vouchBill6103.add(traAmt);
				//凭证来源Origin
				Element origin = DocumentHelper.createElement("Origin");
//				origin.setText(csvReader.get(13));
				vouchBill6103.add(origin);
				vouchBody.add(vouchBill6103);
				i++;
			}
			//笔数
			Element allNum = (Element)doc2.selectSingleNode("/CFX/MSG/VouchHead6103/AllNum");
			allNum.setText(i.toString());
			//String temp = formatXml(doc2.asXML());
			//将生成的报文写入文件中
			//writeToFile(temp);
			writeFile("e:/2.xml",doc2.asXML());
		} catch (Exception e) {
			System.err.println("出现异常："+e.getMessage());
		}
	}
	
	private static void writeToFile(String temp) {
		try {
			FileWriter write = new FileWriter("E:/报文.txt");
			write.write("");//清空原文件内容
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
		  // 格式化输出格式
		  OutputFormat format = OutputFormat.createPrettyPrint();
		  format.setEncoding("UTF-8");
		  StringWriter writer = new StringWriter();
		  // 格式化输出流
		  XMLWriter xmlWriter = new XMLWriter(writer, format);
		  // 将document写入到输出流
		  xmlWriter.write(document);
		  xmlWriter.close();
		  return writer.toString();
	}
	
	
	/**
	 * 把指定的内容写入到指定的文件中
	 * 
	 * @param fileName
	 *            文件名
	 * @param fileContent
	 *            文件内容
	 * @throws FileOperateException
	 *             操作文件失败
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
			String msg = new String("写文件失败,IO错误." + fileName);
		
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("写文件失败，运行时异常." + fileName);
		
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

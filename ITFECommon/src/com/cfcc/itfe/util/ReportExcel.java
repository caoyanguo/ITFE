package com.cfcc.itfe.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.BudgetsubcodemonthDto;
import com.cfcc.itfe.persistence.dto.IncomeCountReportBySubjectDto;
import com.cfcc.itfe.persistence.dto.IncomeMoneyReportDto;
import com.cfcc.itfe.persistence.dto.ShanghaiReport;
import com.cfcc.itfe.persistence.dto.ShanghaiReport2Data;
import com.cfcc.itfe.persistence.dto.StockCountAnalysicsReportDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;

public class ReportExcel {

	private static Log logger = LogFactory.getLog(ReportExcel.class);

	/**
	 * 编制单位
	 */
	public static String bzunit;
	/**
	 * 编制单位
	 */
	public static String bzuser;
	/**
	 * 报表路径
	 */
	public static String filepath = "C:\\";
	/**
	 * 新建报表路径
	 */
	public static String newfilepath = "C:\\";
	/**
	 * 报表名称
	 */
	public static String filename = "model.xls";
	/**
	 * 临时报表名称
	 */
	public static String copyFilename = "";
	/**
	 * sheet名称
	 */
	public static String sheetname = "";
	/**
	 * 报表标题
	 */
	public static String reporttitle = "";
	/**
	 * 编制单位
	 */
	public static String unit = "";

	/**
	 * 编制年月
	 */
	public static String date = "";

	public static Map<String, String> shanghaienum;

	/**
	 * 取得excel字符流
	 */
	public static InputStream inputstream = null;

	/**
	 * 数据
	 */
	public static HashMap<String, Object> datamap = null;

	private static WritableCellFormat wcfN1;
	private static WritableCellFormat wcfN2;
	private static WritableCellFormat wcfN3;
	private static WritableCellFormat wcfN5;
	private static WritableCellFormat wcfN6;
	private static WritableCellFormat wcfN7;
	private static WritableCellFormat wcfN8;
	private static WritableCellFormat wcfN9;
	private static WritableCellFormat wcfN10;
	private static WritableCellFormat wcfN11;
	private static WritableCellFormat wcfN12;
	private static WritableCellFormat wcfN13;
	private static WritableCellFormat wcfN14;
	private static WritableCellFormat wcfN15;
	private static WritableCellFormat wcfN16;
	private static WritableCellFormat wcfN17;
	private static WritableCellFormat wcfN18;
	private static WritableCellFormat wcfN19;
	private static WritableCellFormat defaultwcfn;
	private static NumberFormat nf1;
	private static NumberFormat nf2;
	private static NumberFormat nf5;
	private static NumberFormat nf7;
	private static WritableFont wf3;
	private static WritableFont wf6;
	private static WritableFont wf7;
	private static WritableFont wf8;
	private static WritableFont wf9;
	private static WritableFont wf10;
	private static WritableFont wf11;
	private static WritableFont wf12;
	private static WritableFont wf13;
	private static WritableFont wf14;
	private static WritableFont wf15;
	private static WritableFont wf16;
	private static WritableFont wf17;
	private static WritableFont wf18;
	private static WritableFont wf19;

	public static void main(String[] args) throws Exception {
		init();
		// getreportbylevel("20110101", "20110320");
		List<String> list = new ArrayList<String>();

		for (int i = 0; i < 10; i++) {
			list.add(String.valueOf(i));
		}
		// Excel获得文件
		Workbook wb = Workbook.getWorkbook(new File(
				"C:\\Report\\20130905\\test.xls"));
		// 打开一个文件的副本，并且指定数据写回到原文件
		WritableWorkbook book = Workbook.createWorkbook(new File(
				"C:\\Report\\20130905\\test.xls"), wb);
		// 添加一个工作表
		WritableSheet sheet = book.getSheet(0);
		int lie = 1;
		int hang = 3;
		String olderyear = "";
		String str = "";
		int newhang = 0;

		for (int i = 1; i < list.size(); i++) {
			sheet.mergeCells((i * 3) - 2, 3, i * 3, 3);
			Label label = new Label((i * 3) - 2, 3, "科目" + list.get(i), wcfN6);
			sheet.addCell(label);

			Label label2 = new Label((i * 3) - 2, 4, "本期发生额", wcfN6);
			sheet.addCell(label2);
			Label label3 = new Label((i * 3) - 1, 4, "环比增量", wcfN6);
			sheet.addCell(label3);
			Label label4 = new Label((i * 3), 4, "环比增幅（%）", wcfN6);
			sheet.addCell(label4);
		}
		// sheet.mergeCells(lie, ++ oldhang , lie, hang);
		// Label label = new Label(lie, oldhang, str, wcfN6);
		// sheet.addCell(label);
		book.write();
		book.close();
		System.out.println("结束");
	}

	public static void init() {

		newfilepath = "C:\\";
		copyFilename = "default.xls";
		sheetname = "3";
		reporttitle = "统计报表";
		unit = "XX国库";
		date = "YYYY年MM月DD日";

		shanghaienum = new HashMap<String, String>();
		shanghaienum.put("jc1", "中央级");
		shanghaienum.put("jc3", "市级");
		shanghaienum.put("jc4", "区级");
		shanghaienum.put("jc6", "地方级");
		shanghaienum.put("jc7", "不分级次");
		shanghaienum.put("jedw1", "元");
		shanghaienum.put("jedw2", "万元");
		shanghaienum.put("jedw3", "亿元");

		datamap = new HashMap<String, Object>();

		try {
			nf1 = new NumberFormat("#.00");
			wcfN1 = new WritableCellFormat(nf1);
			wcfN1.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);

			nf2 = new NumberFormat("#,##0.00");
			wcfN2 = new WritableCellFormat(nf2);
			wcfN2.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);

			wf3 = new WritableFont(WritableFont.ARIAL, 20, WritableFont.BOLD,
					false);
			wcfN3 = new WritableCellFormat(wf3);
			wcfN3.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);
			wcfN3.setAlignment(Alignment.CENTRE);
			wcfN3.setVerticalAlignment(VerticalAlignment.CENTRE);

			nf5 = new NumberFormat("0.00");
			wcfN5 = new WritableCellFormat(nf5);
			wcfN5.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);

			wf6 = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD,
					false);
			wcfN6 = new WritableCellFormat(wf6);
			wcfN6.setAlignment(Alignment.CENTRE);
			wcfN6.setVerticalAlignment(VerticalAlignment.CENTRE);

			nf7 = new NumberFormat("#,##0.00");
			wcfN7 = new WritableCellFormat(nf7);
			wcfN7.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);
			wf7 = new WritableFont(WritableFont.ARIAL, 8, WritableFont.NO_BOLD,
					false);
			wcfN7.setFont(wf7);

			wf8 = new WritableFont(WritableFont.ARIAL, 10,
					WritableFont.NO_BOLD, false);
			wcfN8 = new WritableCellFormat(wf8);
			wcfN8.setBorder(jxl.format.Border.BOTTOM, BorderLineStyle.THIN);
			wcfN8.setAlignment(Alignment.LEFT);
			wcfN8.setVerticalAlignment(VerticalAlignment.CENTRE);

			wf9 = new WritableFont(WritableFont.ARIAL, 12,
					WritableFont.NO_BOLD, false);
			wcfN9 = new WritableCellFormat(wf9);
			wcfN9.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);
			wcfN9.setAlignment(Alignment.CENTRE);
			wcfN9.setVerticalAlignment(VerticalAlignment.CENTRE);

			wf10 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD,
					false);
			wcfN10 = new WritableCellFormat(wf10);
			wcfN10.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);
			wcfN10.setAlignment(Alignment.LEFT);
			wcfN10.setVerticalAlignment(VerticalAlignment.CENTRE);
			wcfN10.setWrap(true);

			wf11 = new WritableFont(WritableFont.ARIAL, 9,
					WritableFont.NO_BOLD, false);
			wcfN11 = new WritableCellFormat(wf11);
			wcfN11.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);
			wcfN11.setAlignment(Alignment.LEFT);
			wcfN11.setVerticalAlignment(VerticalAlignment.CENTRE);
			wcfN11.setWrap(true);

			wf12 = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD,
					false);
			wcfN12 = new WritableCellFormat(wf12);
			wcfN12.setBorder(jxl.format.Border.NONE, BorderLineStyle.NONE);
			wcfN12.setAlignment(Alignment.CENTRE);
			wcfN12.setVerticalAlignment(VerticalAlignment.CENTRE);

			wf13 = new WritableFont(WritableFont.ARIAL, 10,
					WritableFont.NO_BOLD, false);
			wcfN13 = new WritableCellFormat(wf13);
			wcfN13.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);
			wcfN13.setAlignment(Alignment.LEFT);
			wcfN13.setVerticalAlignment(VerticalAlignment.CENTRE);

			wf14 = new WritableFont(WritableFont.ARIAL, 10,
					WritableFont.NO_BOLD, false);
			wcfN14 = new WritableCellFormat(wf14);
			wcfN14.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);
			wcfN14.setAlignment(Alignment.RIGHT);
			wcfN14.setVerticalAlignment(VerticalAlignment.CENTRE);

			wf15 = new WritableFont(WritableFont.ARIAL, 10,
					WritableFont.NO_BOLD, false);
			wcfN15 = new WritableCellFormat(wf15);
			wcfN15.setBorder(jxl.format.Border.BOTTOM, BorderLineStyle.THIN);
			wcfN15.setAlignment(Alignment.RIGHT);
			wcfN15.setVerticalAlignment(VerticalAlignment.CENTRE);

			wf16 = new WritableFont(WritableFont.ARIAL, 10,
					WritableFont.NO_BOLD, false);
			wcfN16 = new WritableCellFormat(wf16);
			wcfN16.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);
			wcfN16.setAlignment(Alignment.CENTRE);
			wcfN16.setVerticalAlignment(VerticalAlignment.CENTRE);

			wf17 = new WritableFont(WritableFont.ARIAL, 10,
					WritableFont.NO_BOLD, false);
			wcfN17 = new WritableCellFormat(wf17);
			wcfN17.setBorder(jxl.format.Border.NONE, BorderLineStyle.THIN);
			wcfN17.setAlignment(Alignment.LEFT);
			wcfN17.setVerticalAlignment(VerticalAlignment.CENTRE);

			wf18 = new WritableFont(WritableFont.ARIAL, 10,
					WritableFont.NO_BOLD, false);
			wcfN18 = new WritableCellFormat(wf18);
			wcfN18.setBorder(jxl.format.Border.NONE, BorderLineStyle.THIN);
			wcfN18.setAlignment(Alignment.RIGHT);
			wcfN18.setVerticalAlignment(VerticalAlignment.CENTRE);

			wf19 = new WritableFont(WritableFont.ARIAL, 8,
					WritableFont.NO_BOLD, false);
			wcfN19 = new WritableCellFormat(wf19);
			wcfN19.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);
			wcfN19.setAlignment(Alignment.CENTRE);
			wcfN19.setVerticalAlignment(VerticalAlignment.CENTRE);

			defaultwcfn = new WritableCellFormat();
			defaultwcfn.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);
		} catch (WriteException e) {
			logger.error("初始化失败！", e);
			return;
		}

	}

	// 写入 Excel文件
	public static void writeExcel(int column, int row,
			WritableWorkbook bookCop, String sheetName, String cellContent,
			int flag) throws Exception {

		WritableSheet writeSheet = bookCop.getSheet(sheetName);

		switch (flag) {
		case 1: {
			jxl.write.Number labelNF = new jxl.write.Number(column, row, Double
					.parseDouble(cellContent.trim()), wcfN1);
			// 加入内容
			writeSheet.addCell(labelNF);
			break;
		}
		case 2: {
			jxl.write.Number labelNF = new jxl.write.Number(column, row,
					ArithUtil.div(new BigDecimal(cellContent),
							new BigDecimal("10000.00")).doubleValue(), wcfN2);
			// 加入内容
			writeSheet.addCell(labelNF);
			break;
		}
		case 3: {
			Label label = new Label(column, row, cellContent, wcfN3);
			// 加入内容
			writeSheet.addCell(label);
			break;
		}
		case 5: {
			Label label = new Label(column, row, "0.00", wcfN5);
			// 加入内容
			writeSheet.addCell(label);
			break;
		}
		case 6: {
			Label label = new Label(column, row, cellContent, wcfN6);
			// 加入内容
			writeSheet.addCell(label);
			break;
		}
		case 7: {
			jxl.write.Number labelNF = new jxl.write.Number(column, row, Double
					.parseDouble(cellContent.trim()), wcfN7);
			// 加入内容
			writeSheet.addCell(labelNF);
			break;
		}
		case 8: {
			Label label = new Label(column, row, cellContent, wcfN8);
			// 加入内容
			writeSheet.addCell(label);
			break;
		}
		case 9: {
			Label label = new Label(column, row, cellContent, wcfN9);
			// 加入内容
			writeSheet.addCell(label);
			break;
		}
		case 10: {
			Label label = new Label(column, row, cellContent, wcfN10);
			// 加入内容
			writeSheet.addCell(label);
			break;
		}
		case 11: {
			Label label = new Label(column, row, cellContent, wcfN11);
			// 加入内容
			writeSheet.addCell(label);
			break;
		}
		case 12: {
			Label label = new Label(column, row, cellContent, wcfN12);
			// 加入内容
			writeSheet.addCell(label);
			break;
		}
		case 13: {
			Label label = new Label(column, row, cellContent, wcfN13);
			// 加入内容
			writeSheet.addCell(label);
			break;
		}
		case 14: {
			Label label = new Label(column, row, cellContent, wcfN14);
			// 加入内容
			writeSheet.addCell(label);
			break;
		}
		case 15: {
			Label label = new Label(column, row, cellContent, wcfN15);
			// 加入内容
			writeSheet.addCell(label);
			break;
		}
		case 16: {
			Label label = new Label(column, row, cellContent, wcfN16);
			// 加入内容
			writeSheet.addCell(label);
			break;
		}
		case 17: {
			Label label = new Label(column, row, cellContent, wcfN17);
			// 加入内容
			writeSheet.addCell(label);
			break;
		}
		case 18: {
			Label label = new Label(column, row, cellContent, wcfN18);
			// 加入内容
			writeSheet.addCell(label);
			break;
		}
		case 19: {
			Label label = new Label(column, row, cellContent, wcfN19);
			// 加入内容
			writeSheet.addCell(label);
			break;
		}
		default: {
			Label label = new Label(column, row, cellContent, defaultwcfn);
			// 加入内容
			writeSheet.addCell(label);
		}
		}
	}

	// 新建excel表，填写全辖国库库存
	public static String getreportbyStockdayrpt(String sdate, String edate)
			throws ITFEBizException {

		String writefilepath = "";
		try {

			// 打开要写的Excel文件
			Workbook bookOri = Workbook.getWorkbook(inputstream);
			writefilepath = newfilepath + copyFilename;

			File file = new File(writefilepath);
			File dir = new File(file.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 打开临时表
			WritableWorkbook bookCop = Workbook.createWorkbook(file, bookOri);
			bookCop.getSheet(0).setName(sheetname);
			WritableSheet writeSheet = bookCop.getSheet(sheetname);

			/**
			 * 设置表头信息
			 */
			// 设置表标题
			writeExcel(0, 0, bookCop, sheetname, reporttitle, 6);

			// 设置编制时间
			writeExcel(
					0,
					1,
					bookCop,
					sheetname,
					"                                                                                                              报表日期："
							+ sheetname, 8);

			List<TrStockdayrptDto> infoIds = null; // 财政名称
			List<Map.Entry<String, String>> infoIdsaccountname = null; // 账户
			HashMap<String, BigDecimal> fmt = null; // 金额

			infoIds = (List<TrStockdayrptDto>) datamap.get("infoIds");

			int financeCloumn = 0; // 列
			int financeRow = 4; // 行

			/**
			 * 第一步 以财政名称map循环写入财政名称 本循环financeRow+2 accountRow+2 fmtRow+2
			 */
			DecimalFormat df = new DecimalFormat("0.00");
			for (int i = 0; i < infoIds.size(); i++) {
				TrStockdayrptDto tmpDto = infoIds.get(i);
				writeExcel(financeCloumn, financeRow, bookCop, sheetname,
						tmpDto.getSaccno(), 11);
				writeExcel(financeCloumn += 1, financeRow, bookCop, sheetname,
						df.format(tmpDto.getNmoneyyesterday()), 2);
				writeExcel(financeCloumn += 1, financeRow, bookCop, sheetname,
						df.format(tmpDto.getNmoneyout()), 2);
				writeExcel(financeCloumn += 1, financeRow, bookCop, sheetname,
						df.format(tmpDto.getNmoneyin()), 2);
				writeExcel(financeCloumn += 1, financeRow, bookCop, sheetname,
						df.format(tmpDto.getNmoneytoday()), 2);
				financeCloumn = 0;
				financeRow += 1;
			}
			writeExcel(financeCloumn, financeRow += 1, bookCop, sheetname,
					"编制单位：" + bzunit, 11);
			writeExcel(financeCloumn += 2, financeRow, bookCop, sheetname,
					"编制人：" + bzuser, 11);
			writeExcel(
					financeCloumn += 2,
					financeRow,
					bookCop,
					sheetname,
					"编制时间："
							+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
									.format(
											new Date(System.currentTimeMillis()))
									.toString(), 11);

			// 清除其他sheet
			while (bookCop.getSheetNames().length != 1) {
				// int i=0;
				if (writeSheet.getName().equals(
						bookCop.getSheet(bookCop.getSheetNames().length - 1)
								.getName()))
					bookCop.removeSheet(0);
				else
					bookCop.removeSheet(bookCop.getSheetNames().length - 1);

			}

			bookCop.write();
			bookCop.close();
			bookOri.close();

		} catch (BiffException e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		} catch (IOException e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		} catch (Exception e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		}

		return writefilepath;
	}

	// 新建收入excel表
	public static String getreportbyIncomerpt(List list,String offlag,String sbudgettypename,String sleBillType,String sleBillKind,String username) throws ITFEBizException {
		
		String writefilepath="";
		try {
			
			// 打开要写的Excel文件
			Workbook bookOri= Workbook.getWorkbook(inputstream);
			writefilepath=newfilepath+copyFilename;
			
			File file = new File(writefilepath);
			File dir = new File(file.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 打开临时表
			WritableWorkbook bookCop = Workbook.createWorkbook(file,bookOri);
			bookCop.getSheet(0).setName(sheetname);
			WritableSheet writeSheet =bookCop.getSheet(sheetname);

			/**
			 * 设置表头信息
			 */
			//设置表标题
			writeExcel(0,0,bookCop,sheetname,reporttitle,12);
			
			//设置编制时间
			writeExcel(0,1,bookCop,sheetname,"编制日期："+date,8);
			
			writeExcel(2,1,bookCop,sheetname,"制表人："+username,8);
			//设置辖属标志和预算种类标志
			writeExcel(4,1,bookCop,sheetname,"【" + offlag + "】【" + sbudgettypename + "】",8);
			
			int Cloumn=0; //列
			int Row=3; //行
			
			//财政预算收入日报表查询
			if (StateConstant.REPORT_DAY.equals(sleBillType)) {
				// 财政预算收入日报表二栏式
				if (StateConstant.REPORT_TWO.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"日累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrIncomedayrptDto)list.get(i)).getNmoneyday()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getNmoneyday().toString(),7);
						}
						Row++;
					}
				}
				// 财政预算收入日报表三栏式
				else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"日累计金额",16);
					writeExcel(3,2,bookCop,sheetname,"年累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrIncomedayrptDto)list.get(i)).getNmoneyday()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getNmoneyday().toString(),7);
						}
						if(null==((TrIncomedayrptDto)list.get(i)).getNmoneyyear()){
							writeExcel(Cloumn+3,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+3,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getNmoneyyear().toString(),7);
						}
						Row++;
					}
				}
				// 财政预算收入日报表四栏式
				else if (StateConstant.REPORT_FOUR.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"日累计金额",16);
					writeExcel(3,2,bookCop,sheetname,"月累计金额",16);
					writeExcel(4,2,bookCop,sheetname,"年累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrIncomedayrptDto)list.get(i)).getNmoneyday()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getNmoneyday().toString(),7);
						}
						if(null==((TrIncomedayrptDto)list.get(i)).getNmoneymonth()){
							writeExcel(Cloumn+3,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+3,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getNmoneymonth().toString(),7);
						}
						if(null==((TrIncomedayrptDto)list.get(i)).getNmoneyyear()){
							writeExcel(Cloumn+4,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+4,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getNmoneyyear().toString(),7);
						}
						Row++;
					}
				}
			}
			// 财政预算收入旬报表查询
			else if (StateConstant.REPORT_TEN.equals(sleBillType)) {
				// 财政预算收入旬报表二栏式
				if (StateConstant.REPORT_TWO.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"旬累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrIncomedayrptDto)list.get(i)).getNmoneytenday()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getNmoneytenday().toString(),7);
						}
						Row++;
					}
				}
				// 财政预算收入旬报表三栏式
				else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"旬累计金额",16);
					writeExcel(3,2,bookCop,sheetname,"年累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrIncomedayrptDto)list.get(i)).getNmoneytenday()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getNmoneytenday().toString(),7);
						}
						if(null==((TrIncomedayrptDto)list.get(i)).getNmoneyyear()){
							writeExcel(Cloumn+3,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+3,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getNmoneyyear().toString(),7);
						}
						Row++;
					}
				}
				// 财政预算收入旬报表四栏式
				else if (StateConstant.REPORT_FOUR.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"旬累计金额",16);
					writeExcel(3,2,bookCop,sheetname,"月累计金额",16);
					writeExcel(4,2,bookCop,sheetname,"年累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrIncomedayrptDto)list.get(i)).getNmoneytenday()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getNmoneytenday().toString(),7);
						}
						if(null==((TrIncomedayrptDto)list.get(i)).getNmoneymonth()){
							writeExcel(Cloumn+3,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+3,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getNmoneymonth().toString(),7);
						}
						if(null==((TrIncomedayrptDto)list.get(i)).getNmoneyyear()){
							writeExcel(Cloumn+4,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+4,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getNmoneyyear().toString(),7);
						}
						Row++;
					}
				}
			}
			// 财政预算收入月报表查询
			else if (StateConstant.REPORT_MONTH.equals(sleBillType)) {
				// 财政预算收入月报表二栏式
				if (StateConstant.REPORT_TWO.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"月累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrIncomedayrptDto)list.get(i)).getNmoneymonth()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getNmoneymonth().toString(),7);
						}
						Row++;
					}
				}
				// 财政预算收入月报表三栏式
				else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"月累计金额",16);
					writeExcel(3,2,bookCop,sheetname,"年累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrIncomedayrptDto)list.get(i)).getNmoneymonth()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getNmoneymonth().toString(),7);
						}
						if(null==((TrIncomedayrptDto)list.get(i)).getNmoneyyear()){
							writeExcel(Cloumn+3,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+3,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getNmoneyyear().toString(),7);
						}
						Row++;
					}
				} 
			}
			// 财政预算收入季报表查询
			else if (StateConstant.REPORT_QUAR.equals(sleBillType)) {
				// 财政预算收入季报表二栏式
				if (StateConstant.REPORT_TWO.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"季累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrIncomedayrptDto)list.get(i)).getNmoneyquarter()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getNmoneyquarter().toString(),7);
						}
						Row++;
					}
				}
				// 财政预算收入季报表三栏式
				else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"季累计金额",16);
					writeExcel(3,2,bookCop,sheetname,"年累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrIncomedayrptDto)list.get(i)).getNmoneyquarter()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getNmoneyquarter().toString(),7);
						}
						if(null==((TrIncomedayrptDto)list.get(i)).getNmoneyyear()){
							writeExcel(Cloumn+3,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+3,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getNmoneyyear().toString(),7);
						}
						Row++;
					}
				}
			}
			// 财政预算收入年报表查询
			else if (StateConstant.REPORT_YEAR.equals(sleBillType)) {
				// 财政预算收入年报表二栏式
				if (StateConstant.REPORT_TWO.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"年累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrIncomedayrptDto)list.get(i)).getNmoneyyear()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrIncomedayrptDto)list.get(i)).getNmoneyyear().toString(),7);
						}
						Row++;
					}
				} 
			}
			
			//清除其他sheet
			while( bookCop.getSheetNames().length !=1){
				if(writeSheet.getName().equals(bookCop.getSheet(bookCop.getSheetNames().length-1).getName()))
					bookCop.removeSheet(0);
				else
					bookCop.removeSheet(bookCop.getSheetNames().length-1);
				
			}
			
			bookCop.write();
	        bookCop.close();
	        bookOri.close(); 
        
		} catch (BiffException e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		} catch (IOException e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		} catch (Exception e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		}
		
		return writefilepath;
	}

	// 新建收入excel表sleTrename--国库,
	// sbasebudgetsubcodename--基准科目代码,sleBudLevel--预算级次,sleMoneyUnit--金额单位
	public static String getIncomeReportBySubject(List list, String sleTrename,
			String sbasebudgetsubcodename, String sleBudLevel,
			String sleMoneyUnit, String username) throws ITFEBizException {

		String writefilepath = "";
		try {

			// 打开要写的Excel文件
			Workbook bookOri = Workbook.getWorkbook(inputstream);
			writefilepath = newfilepath + copyFilename;

			File file = new File(writefilepath);
			File dir = new File(file.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 打开临时表
			WritableWorkbook bookCop = Workbook.createWorkbook(file, bookOri);
			bookCop.getSheet(0).setName(sheetname);
			WritableSheet writeSheet = bookCop.getSheet(sheetname);

			/**
			 * 设置表头信息
			 */
			// 设置表标题
			writeExcel(0, 1, bookCop, sheetname, reporttitle, 12);

			// 设置调拨标志
			writeExcel(0, 3, bookCop, sheetname, "基准科目："
					+ sbasebudgetsubcodename, 17);

			// 设置预算级次
			writeExcel(4, 3, bookCop, sheetname, "预算级次：" + sleBudLevel, 17);

			// 设置金额单位
			writeExcel(8, 3, bookCop, sheetname, sleMoneyUnit, 17);

			int Cloumn = 0; // 列
			int Row = 7; // 行

			for (int i = 0; i < list.size(); i++) {
				writeExcel(Cloumn, Row, bookCop, sheetname,
						((IncomeCountReportBySubjectDto) list.get(i))
								.getSbudgetsubname()
								+ "("
								+ ((IncomeCountReportBySubjectDto) list.get(i))
										.getSbudgetsubcode() + ")", 19);
				if (null == ((IncomeCountReportBySubjectDto) list.get(i))
						.getNmoneycurrent()) {
					writeExcel(Cloumn + 1, Row, bookCop, sheetname, "0.00", 7);
				} else {
					writeExcel(Cloumn + 1, Row, bookCop, sheetname,
							((IncomeCountReportBySubjectDto) list.get(i))
									.getNmoneycurrent().toString(), 7);
				}
				if (null == ((IncomeCountReportBySubjectDto) list.get(i))
						.getDmoneygrowth()) {
					writeExcel(Cloumn + 2, Row, bookCop, sheetname, "0.00%", 19);
				} else {
					writeExcel(Cloumn + 2, Row, bookCop, sheetname,
							((IncomeCountReportBySubjectDto) list.get(i))
									.getDmoneygrowth().toString()
									+ "%", 19);
				}
				if (null == ((IncomeCountReportBySubjectDto) list.get(i))
						.getDmoneycontribution()) {
					writeExcel(Cloumn + 3, Row, bookCop, sheetname, "0.00%", 19);
				} else {
					writeExcel(Cloumn + 3, Row, bookCop, sheetname,
							((IncomeCountReportBySubjectDto) list.get(i))
									.getDmoneycontribution().toString()
									+ "%", 19);
				}
				if (null == ((IncomeCountReportBySubjectDto) list.get(i))
						.getDmoneyratio()) {
					writeExcel(Cloumn + 4, Row, bookCop, sheetname, "0.00%", 19);
				} else {
					writeExcel(Cloumn + 4, Row, bookCop, sheetname,
							((IncomeCountReportBySubjectDto) list.get(i))
									.getDmoneyratio().toString()
									+ "%", 19);
				}
				if (null == ((IncomeCountReportBySubjectDto) list.get(i))
						.getNmoneyyear()) {
					writeExcel(Cloumn + 5, Row, bookCop, sheetname, "0.00", 7);
				} else {
					writeExcel(Cloumn + 5, Row, bookCop, sheetname,
							((IncomeCountReportBySubjectDto) list.get(i))
									.getNmoneyyear().toString(), 7);
				}
				if (null == ((IncomeCountReportBySubjectDto) list.get(i))
						.getDmoneyyeargrowth()) {
					writeExcel(Cloumn + 6, Row, bookCop, sheetname, "0.00%", 19);
				} else {
					writeExcel(Cloumn + 6, Row, bookCop, sheetname,
							((IncomeCountReportBySubjectDto) list.get(i))
									.getDmoneyyeargrowth().toString()
									+ "%", 19);
				}
				if (null == ((IncomeCountReportBySubjectDto) list.get(i))
						.getDmoneyyearcontribution()) {
					writeExcel(Cloumn + 7, Row, bookCop, sheetname, "0.00%", 19);
				} else {
					writeExcel(Cloumn + 7, Row, bookCop, sheetname,
							((IncomeCountReportBySubjectDto) list.get(i))
									.getDmoneyyearcontribution().toString()
									+ "%", 19);
				}
				if (null == ((IncomeCountReportBySubjectDto) list.get(i))
						.getDmoneyyearratio()) {
					writeExcel(Cloumn + 8, Row, bookCop, sheetname, "0.00%", 19);
				} else {
					writeExcel(Cloumn + 8, Row, bookCop, sheetname,
							((IncomeCountReportBySubjectDto) list.get(i))
									.getDmoneyyearratio().toString()
									+ "%", 19);
				}
				Row++;
			}

			// 设置编制单位
			writeExcel(0, Row + 2, bookCop, sheetname, "编制单位：" + sleTrename, 17);
			// 设置编制人
			writeExcel(4, Row + 2, bookCop, sheetname, "编制人：" + username, 17);
			// 设置编制时间
			writeExcel(7, Row + 2, bookCop, sheetname, "编制日期：", 18);
			writeExcel(8, Row + 2, bookCop, sheetname, date, 17);

			// 清除其他sheet
			while (bookCop.getSheetNames().length != 1) {
				if (writeSheet.getName().equals(
						bookCop.getSheet(bookCop.getSheetNames().length - 1)
								.getName()))
					bookCop.removeSheet(0);
				else
					bookCop.removeSheet(bookCop.getSheetNames().length - 1);

			}

			bookCop.write();
			bookCop.close();
			bookOri.close();

		} catch (BiffException e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		} catch (IOException e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		} catch (Exception e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		}

		return writefilepath;
	}

	public static String genBudgetsubcodemonthReport() throws ITFEBizException {
		// 新建excel表，填写全辖国库库存
		String writefilepath = "";
		try {

			// 打开要写的Excel文件
			Workbook bookOri = Workbook.getWorkbook(inputstream);
			writefilepath = newfilepath + copyFilename;

			File file = new File(writefilepath);
			File dir = new File(file.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 打开临时表
			WritableWorkbook bookCop = Workbook.createWorkbook(file, bookOri);
			bookCop.getSheet(0).setName(sheetname);
			WritableSheet writeSheet = bookCop.getSheet(sheetname);
			ShanghaiReport report = (ShanghaiReport) datamap.get("dto");
			/**
			 * 设置表头信息
			 */
			// 设置表标题
			writeExcel(0, 0, bookCop, sheetname, reporttitle, 6);
			String starttime = DateUtil.date2String2(report.getStartdate());
			String endtime = DateUtil.date2String2(report.getEnddate());
			// 设置编制时间
			writeExcel(0,1,bookCop,sheetname,"起止月度："+ starttime.substring(4, 6)	+ "月-"+ endtime.substring(4, 6)+ "月",	17);
			writeExcel(6,1,bookCop,sheetname,"预算级次："+ shanghaienum.get("jc" + report.getSlevelcode()),	17);
			writeExcel(14,1,bookCop,sheetname,"单位:"+ shanghaienum.get("jedw" + report.getMoneyunit()),	17);
			
//			writeSheet.insertRow(2);
			
			List<BudgetsubcodemonthDto> infoIds = null; // 财政名称

			infoIds = (List<BudgetsubcodemonthDto>) datamap.get("infoIds");

			int financeCloumn = 0; // 列
			int financeRow = 3; // 行
			/**
			 * 第一步 以财政名称map循环写入财政名称 本循环financeRow+2 accountRow+2 fmtRow+2
			 */
			String str = "";
			int newfinanceRow = financeRow;
			int oldfinanceRow = financeRow;
			String olderyear = "";

			BigDecimal sum = new BigDecimal("0");
			for (int i = 0; i < infoIds.size(); i++) {
				BudgetsubcodemonthDto tmpDto = infoIds.get(i);
				str = tmpDto.getYearcode();

				if (StringUtils.isBlank(olderyear)) {
					olderyear = str;
					oldfinanceRow = financeRow;
				} else {
					if (!olderyear.equals(tmpDto.getYearcode())) {
						writeSheet.mergeCells(0, oldfinanceRow, 0, financeRow);
						writeExcel(0, newfinanceRow, bookCop, sheetname,
								olderyear, 11);
						newfinanceRow++;
						oldfinanceRow = financeRow + 1;
						olderyear = str;
					}
				}

				writeExcel(financeCloumn, financeRow, bookCop, sheetname,
						tmpDto.getYearcode(), 11);
				writeExcel(financeCloumn += 1, financeRow, bookCop, sheetname,
						tmpDto.getSbudgetsubname() + "("
								+ tmpDto.getSbudgetsubcode() + ")", 11);

				writeExcel(financeCloumn += 1, financeRow, bookCop, sheetname,
						tmpDto.getYiamt().setScale(2,
								BigDecimal.ROUND_HALF_EVEN).toString(), 7);
				sum = sum.add(tmpDto.getYiamt().setScale(2,
						BigDecimal.ROUND_HALF_EVEN));
				writeExcel(financeCloumn += 1, financeRow, bookCop, sheetname,
						tmpDto.getEramt().setScale(2,
								BigDecimal.ROUND_HALF_EVEN).toString(), 7);
				sum = sum.add(tmpDto.getEramt().setScale(2,
						BigDecimal.ROUND_HALF_EVEN));
				writeExcel(financeCloumn += 1, financeRow, bookCop, sheetname,
						tmpDto.getSanamt().setScale(2,
								BigDecimal.ROUND_HALF_EVEN).toString(), 7);
				sum = sum.add(tmpDto.getSanamt().setScale(2,
						BigDecimal.ROUND_HALF_EVEN));
				writeExcel(financeCloumn += 1, financeRow, bookCop, sheetname,
						tmpDto.getSiamt().setScale(2,
								BigDecimal.ROUND_HALF_EVEN).toString(), 7);
				sum = sum.add(tmpDto.getSiamt().setScale(2,
						BigDecimal.ROUND_HALF_EVEN));
				writeExcel(financeCloumn += 1, financeRow, bookCop, sheetname,
						tmpDto.getWuamt().setScale(2,
								BigDecimal.ROUND_HALF_EVEN).toString(), 7);
				sum = sum.add(tmpDto.getWuamt().setScale(2,
						BigDecimal.ROUND_HALF_EVEN));
				writeExcel(financeCloumn += 1, financeRow, bookCop, sheetname,
						tmpDto.getLiuamt().setScale(2,
								BigDecimal.ROUND_HALF_EVEN).toString(), 7);
				sum = sum.add(tmpDto.getLiuamt().setScale(2,
						BigDecimal.ROUND_HALF_EVEN));
				writeExcel(financeCloumn += 1, financeRow, bookCop, sheetname,
						tmpDto.getQiamt().setScale(2,
								BigDecimal.ROUND_HALF_EVEN).toString(), 7);
				sum = sum.add(tmpDto.getQiamt().setScale(2,
						BigDecimal.ROUND_HALF_EVEN));
				writeExcel(financeCloumn += 1, financeRow, bookCop, sheetname,
						tmpDto.getBaamt().setScale(2,
								BigDecimal.ROUND_HALF_EVEN).toString(), 7);
				sum = sum.add(tmpDto.getBaamt().setScale(2,
						BigDecimal.ROUND_HALF_EVEN));
				writeExcel(financeCloumn += 1, financeRow, bookCop, sheetname,
						tmpDto.getJiuamt().setScale(2,
								BigDecimal.ROUND_HALF_EVEN).toString(), 7);
				sum = sum.add(tmpDto.getJiuamt().setScale(2,
						BigDecimal.ROUND_HALF_EVEN));
				writeExcel(financeCloumn += 1, financeRow, bookCop, sheetname,
						tmpDto.getShiamt().setScale(2,
								BigDecimal.ROUND_HALF_EVEN).toString(), 7);
				sum = sum.add(tmpDto.getShiamt().setScale(2,
						BigDecimal.ROUND_HALF_EVEN));
				writeExcel(financeCloumn += 1, financeRow, bookCop, sheetname,
						tmpDto.getShiyiamt().setScale(2,
								BigDecimal.ROUND_HALF_EVEN).toString(), 7);
				sum = sum.add(tmpDto.getShiyiamt().setScale(2,
						BigDecimal.ROUND_HALF_EVEN));
				writeExcel(financeCloumn += 1, financeRow, bookCop, sheetname,
						tmpDto.getShieramt().setScale(2,
								BigDecimal.ROUND_HALF_EVEN).toString(), 7);
				sum = sum.add(tmpDto.getShieramt().setScale(2,
						BigDecimal.ROUND_HALF_EVEN));
				// 合计
				writeExcel(financeCloumn += 1, financeRow, bookCop, sheetname,
						sum.setScale(2, BigDecimal.ROUND_HALF_EVEN).toString(),
						7);
				sum = new BigDecimal(0);
				financeCloumn = 0;
				financeRow += 1;
			}

			writeSheet.mergeCells(0, oldfinanceRow, 0, financeRow - 1);
			writeExcel(0, oldfinanceRow, bookCop, sheetname, olderyear, 11);

			writeExcel(0, financeRow += 1, bookCop, sheetname,
					"编制单位：" + bzunit, 17);
			writeExcel(6, financeRow, bookCop, sheetname, "编制人：" + bzuser, 17);
			writeExcel(14, financeRow, bookCop, sheetname, "编制时间："
					+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(
							new Date(System.currentTimeMillis())).toString(),
					17);

			// 清除其他sheet
			while (bookCop.getSheetNames().length != 1) {
				// int i=0;
				if (writeSheet.getName().equals(
						bookCop.getSheet(bookCop.getSheetNames().length - 1)
								.getName()))
					bookCop.removeSheet(0);
				else
					bookCop.removeSheet(bookCop.getSheetNames().length - 1);

			}

			bookCop.write();
			bookCop.close();
			bookOri.close();

		} catch (BiffException e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		} catch (IOException e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		} catch (Exception e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		}

		return writefilepath;
	}

	public static String genShbudgetsubcodesearch(ShanghaiReport searchdto)
			throws ITFEBizException {
		// 新建excel表，填写全辖国库库存
		String writefilepath = "";
		try {

			// 打开要写的Excel文件
			Workbook bookOri = Workbook.getWorkbook(inputstream);
			writefilepath = newfilepath + copyFilename;

			File file = new File(writefilepath);
			File dir = new File(file.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 打开临时表
			WritableWorkbook bookCop = Workbook.createWorkbook(file, bookOri);
			bookCop.getSheet(0).setName(sheetname);
			WritableSheet writeSheet = bookCop.getSheet(sheetname);
			/**
			 * 设置表头信息
			 */
			// 日期和科目值列表
			HashMap<String, List<ShanghaiReport2Data>> groupdatedata;
			// 所有科目列表
			List<String> subcodes;
			// 日期列表
			List<String> dates;

			subcodes = (List<String>) datamap.get("subcodes");
			groupdatedata = (HashMap<String, List<ShanghaiReport2Data>>) datamap
					.get("infoIds");
			dates = (List<String>) datamap.get("dates");
			// 设置表标题
			writeExcel(0, 0, bookCop, sheetname, reporttitle, 6);
			// 设置编制时间
			writeExcel(0, 1, bookCop, sheetname, datamap.get("reporttype")
					.toString(), 6);
			writeExcel(0, 2, bookCop, sheetname, "预算级次："
					+ shanghaienum.get("jc" + searchdto.getSlevelcode()), 17);
			writeExcel(9, 2, bookCop, sheetname, "单位："
					+ shanghaienum.get("jedw" + searchdto.getMoneyunit())
							.toString(), 17);
//			writeSheet.insertRow(2);
			
			/**
			 * 生成所有科目的列
			 */
			int lie = 1;
			int hang = 3;
			String olderyear = "";
			String str = "";
			int newhang = 0;
			Label labeltime = new Label(0, 5, "时间", wcfN19);
			writeSheet.addCell(labeltime);
			for (int i = 1; i < subcodes.size() + 1; i++) {
				writeSheet.mergeCells((i * 3) - 2, 5, i * 3, 5);
				Label label = new Label((i * 3) - 2, 5, subcodes.get(i - 1),
						wcfN19);
				writeSheet.addCell(label);

				Label label2 = new Label((i * 3) - 2, 6, "本期发生额", wcfN19);
				writeSheet.addCell(label2);
				Label label3 = new Label((i * 3) - 1, 6, "环比增量", wcfN19);
				writeSheet.addCell(label3);
				Label label4 = new Label((i * 3), 6, "环比增幅（%）", wcfN19);
				writeSheet.addCell(label4);
			}
			/**
			 * 第二步：向每个列中填充值
			 */
			int financeCloumn = 0; // 列
			int financeRow = 7; // 行
			int count = 1;
			for (String date : dates) {
				List<ShanghaiReport2Data> datas = groupdatedata.get(date);
				Label labeltimenow = new Label(0, financeRow, datas.get(0)
						.getSrptdate(), wcfN19);
				writeSheet.addCell(labeltimenow);
				Label morenlabel;

				for (int k = 1; k < subcodes.size() + 1; k++) {
					morenlabel = new Label((k * 3) - 2, financeRow, "", wcfN19);
					writeSheet.addCell(morenlabel);
					morenlabel = new Label((k * 3) - 1, financeRow, "", wcfN19);
					writeSheet.addCell(morenlabel);
					morenlabel = new Label((k * 3), financeRow, "", wcfN19);
					writeSheet.addCell(morenlabel);
				}
				for (int i = 0; i < datas.size(); i++) {
					ShanghaiReport2Data tmpdata = datas.get(i);
					financeCloumn = (subcodes.lastIndexOf(tmpdata
							.getSbudgetsubcode()) * 3) + 1;
					// 如果值为0 就不显示
					writeExcel(financeCloumn, financeRow, bookCop, sheetname,
							tmpdata.getNum1().setScale(2,
									BigDecimal.ROUND_HALF_EVEN).toString(), 7);
					if (null == tmpdata.getNum2()) {
						writeExcel(financeCloumn += 1, financeRow, bookCop,
								sheetname, "", 19);
					} else {
						writeExcel(financeCloumn += 1, financeRow, bookCop,
								sheetname, tmpdata.getNum2().setScale(2,
										BigDecimal.ROUND_HALF_EVEN).toString(),
								7);

					}
					if (null == tmpdata.getNum3()) {
						writeExcel(financeCloumn += 1, financeRow, bookCop,
								sheetname, "", 19);
					} else {
						writeExcel(financeCloumn += 1, financeRow, bookCop,
								sheetname, tmpdata.getNum3().setScale(2,
										BigDecimal.ROUND_HALF_EVEN).toString(),
								7);

					}
				}

				financeRow += 1;
			}
			financeRow += 1;
			writeSheet.mergeCells(0, financeRow, 2, financeRow);
			writeExcel(0, financeRow, bookCop, sheetname, "编制单位：" + bzunit, 17);
			writeSheet.mergeCells(4, financeRow, 5, financeRow);
			writeExcel(4, financeRow, bookCop, sheetname, "编制人：" + bzuser, 17);
			writeSheet.mergeCells(8, financeRow, 10, financeRow);
			writeExcel(8, financeRow, bookCop, sheetname, "编制时间："
					+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(
							new Date(System.currentTimeMillis())).toString(),
					17);

			// 清除其他sheet
			while (bookCop.getSheetNames().length != 1) {
				// int i=0;
				if (writeSheet.getName().equals(
						bookCop.getSheet(bookCop.getSheetNames().length - 1)
								.getName()))
					bookCop.removeSheet(0);
				else
					bookCop.removeSheet(bookCop.getSheetNames().length - 1);

			}

			bookCop.write();
			bookCop.close();
			bookOri.close();

		} catch (BiffException e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		} catch (IOException e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		} catch (Exception e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		}
		return writefilepath;
	}

	// 新建收入excel表
	// startenddate-起止日期 ,moneyUnitName--金额单位,sleTrename--国库,username-用户名
	public static String getStockCountAnalysisReport(List list,
			String startenddate, String moneyUnitName, String sleTrename,
			String username) throws ITFEBizException {

		String writefilepath = "";
		try {

			// 打开要写的Excel文件
			Workbook bookOri = Workbook.getWorkbook(inputstream);
			writefilepath = newfilepath + copyFilename;

			File file = new File(writefilepath);
			File dir = new File(file.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 打开临时表
			WritableWorkbook bookCop = Workbook.createWorkbook(file, bookOri);
			bookCop.getSheet(0).setName(sheetname);
			WritableSheet writeSheet = bookCop.getSheet(sheetname);

			/**
			 * 设置表头信息
			 */
			// 设置表标题
			writeExcel(0, 1, bookCop, sheetname, reporttitle, 12);

			// 设置调拨标志
			writeExcel(0, 3, bookCop, sheetname, "起止日期：" + startenddate, 17);

			// 设置金额单位
			writeExcel(6, 3, bookCop, sheetname, moneyUnitName, 17);

			int Cloumn = 0; // 列
			int Row = 6; // 行

			for (int i = 0; i < list.size(); i++) {
				writeExcel(
						Cloumn,
						Row,
						bookCop,
						sheetname,
						((StockCountAnalysicsReportDto) list.get(i)).getSyear(),
						19);
				if (null == ((StockCountAnalysicsReportDto) list.get(i))
						.getNaveragevalue()) {
					writeExcel(Cloumn + 1, Row, bookCop, sheetname, "0.00", 7);
				} else {
					writeExcel(Cloumn + 1, Row, bookCop, sheetname,
							((StockCountAnalysicsReportDto) list.get(i))
									.getNaveragevalue().toString(), 7);
				}
				if (null == ((StockCountAnalysicsReportDto) list.get(i))
						.getNmaximumvalue()) {
					writeExcel(Cloumn + 2, Row, bookCop, sheetname, "0.00", 7);
				} else {
					writeExcel(Cloumn + 2, Row, bookCop, sheetname,
							((StockCountAnalysicsReportDto) list.get(i))
									.getNminimumvalue().toString(), 7);
				}
				if (null == ((StockCountAnalysicsReportDto) list.get(i))
						.getNminimumvalue()) {
					writeExcel(Cloumn + 3, Row, bookCop, sheetname, "0.00", 7);
				} else {
					writeExcel(Cloumn + 3, Row, bookCop, sheetname,
							((StockCountAnalysicsReportDto) list.get(i))
									.getNminimumvalue().toString(), 7);
				}
				if (null == ((StockCountAnalysicsReportDto) list.get(i))
						.getNstandarddeviation()) {
					writeExcel(Cloumn + 4, Row, bookCop, sheetname, "0.00", 7);
				} else {
					writeExcel(Cloumn + 4, Row, bookCop, sheetname,
							((StockCountAnalysicsReportDto) list.get(i))
									.getNstandarddeviation().toString(), 7);
				}
				if (null == ((StockCountAnalysicsReportDto) list.get(i))
						.getNskewness()) {
					writeExcel(Cloumn + 5, Row, bookCop, sheetname, "0.00", 7);
				} else {
					writeExcel(Cloumn + 5, Row, bookCop, sheetname,
							((StockCountAnalysicsReportDto) list.get(i))
									.getNskewness().toString(), 7);
				}
				if (null == ((StockCountAnalysicsReportDto) list.get(i))
						.getNkurtosis()) {
					writeExcel(Cloumn + 6, Row, bookCop, sheetname, "0.00", 7);
				} else {
					writeExcel(Cloumn + 6, Row, bookCop, sheetname,
							((StockCountAnalysicsReportDto) list.get(i))
									.getNkurtosis().toString(), 7);
				}
				Row++;
			}

			// 设置编制单位
			writeExcel(0, Row + 2, bookCop, sheetname, "编制单位：", 18);
			writeExcel(1, Row + 2, bookCop, sheetname, sleTrename, 17);
			// 设置编制人
			writeExcel(3, Row + 2, bookCop, sheetname, "编制人：" + username, 17);
			// 设置编制时间
			writeExcel(5, Row + 2, bookCop, sheetname, "编制日期：", 18);
			writeExcel(6, Row + 2, bookCop, sheetname, date, 17);

			// 清除其他sheet
			while (bookCop.getSheetNames().length != 1) {
				if (writeSheet.getName().equals(
						bookCop.getSheet(bookCop.getSheetNames().length - 1)
								.getName()))
					bookCop.removeSheet(0);
				else
					bookCop.removeSheet(bookCop.getSheetNames().length - 1);

			}

			bookCop.write();
			bookCop.close();
			bookOri.close();

		} catch (BiffException e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		} catch (IOException e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		} catch (Exception e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		}

		return writefilepath;
	}

	public static String getFilepath() {
		return filepath;
	}

	public static void setFilepath(String filepath) {
		ReportExcel.filepath = filepath;
	}

	public static String getNewfilepath() {
		return newfilepath;
	}

	public static void setNewfilepath(String newfilepath) {
		ReportExcel.newfilepath = newfilepath;
	}

	public static String getFilename() {
		return filename;
	}

	public static void setFilename(String filename) {
		ReportExcel.filename = filename;
	}

	public static String getCopyFilename() {
		return copyFilename;
	}

	public static void setCopyFilename(String copyFilename) {
		ReportExcel.copyFilename = copyFilename;
	}

	public static String getSheetname() {
		return sheetname;
	}

	public static void setSheetname(String sheetname) {
		ReportExcel.sheetname = sheetname;
	}

	public static String getReporttitle() {
		return reporttitle;
	}

	public static void setReporttitle(String reporttitle) {
		ReportExcel.reporttitle = reporttitle;
	}

	public static String getUnit() {
		return unit;
	}

	public static void setUnit(String unit) {
		ReportExcel.unit = unit;
	}

	/**
	 * @return the datamap
	 */
	public static HashMap<String, Object> getDatamap() {
		return datamap;
	}

	/**
	 * @param datamap
	 *            the datamap to set
	 */
	public static void setDatamap(HashMap<String, Object> datamap) {
		ReportExcel.datamap = datamap;
	}

	public static InputStream getInputstream() {
		return inputstream;
	}

	public static void setInputstream(InputStream inputstream) {
		ReportExcel.inputstream = inputstream;
	}

	/**
	 * @return the date
	 */
	public static String getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public static void setDate(String date) {
		ReportExcel.date = date;
	}

	public String getBzunit() {
		return bzunit;
	}

	public void setBzunit(String bzunit) {
		this.bzunit = bzunit;
	}

	public String getBzpeople() {
		return bzuser;
	}

	public void setBzpeople(String bzpeople) {
		this.bzuser = bzpeople;
	}
	
	// 新建支出excel表
	public static String getreportbyOutrpt(List list,String offlag,String sbudgettypename,String sleBillType,String sleBillKind,String username) throws ITFEBizException {
		
		String writefilepath="";
		try {
			
			// 打开要写的Excel文件
			Workbook bookOri= Workbook.getWorkbook(inputstream);
			writefilepath=newfilepath+copyFilename;
			
			File file = new File(writefilepath);
			File dir = new File(file.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 打开临时表
			WritableWorkbook bookCop = Workbook.createWorkbook(file,bookOri);
			bookCop.getSheet(0).setName(sheetname);
			WritableSheet writeSheet =bookCop.getSheet(sheetname);

			/**
			 * 设置表头信息
			 */
			//设置表标题
			writeExcel(0,0,bookCop,sheetname,reporttitle,12);
			
			//设置编制时间
			writeExcel(0,1,bookCop,sheetname,"编制日期："+date,8);
			
			writeExcel(2,1,bookCop,sheetname,"制表人："+username,8);
			//设置辖属标志和预算种类标志
			writeExcel(4,1,bookCop,sheetname,"【" + offlag + "】【" + sbudgettypename + "】",8);
			
			int Cloumn=0; //列
			int Row=3; //行
			
			//财政预算收入日报表查询
			if (StateConstant.REPORT_DAY.equals(sleBillType)) {
				// 财政预算收入日报表二栏式
				if (StateConstant.REPORT_TWO.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"日累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyday()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyday().toString(),7);
						}
						Row++;
					}
				}
				// 财政预算收入日报表三栏式
				else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"日累计金额",16);
					writeExcel(3,2,bookCop,sheetname,"年累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyday()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyday().toString(),7);
						}
						if(null==((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyyear()){
							writeExcel(Cloumn+3,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+3,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyyear().toString(),7);
						}
						Row++;
					}
				}
				// 财政预算收入日报表四栏式
				else if (StateConstant.REPORT_FOUR.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"日累计金额",16);
					writeExcel(3,2,bookCop,sheetname,"月累计金额",16);
					writeExcel(4,2,bookCop,sheetname,"年累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyday()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyday().toString(),7);
						}
						if(null==((TrTaxorgPayoutReportDto)list.get(i)).getNmoneymonth()){
							writeExcel(Cloumn+3,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+3,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getNmoneymonth().toString(),7);
						}
						if(null==((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyyear()){
							writeExcel(Cloumn+4,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+4,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyyear().toString(),7);
						}
						Row++;
					}
				}
			}
			// 财政预算收入旬报表查询
			else if (StateConstant.REPORT_TEN.equals(sleBillType)) {
				// 财政预算收入旬报表二栏式
				if (StateConstant.REPORT_TWO.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"旬累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrTaxorgPayoutReportDto)list.get(i)).getNmoneytenday()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getNmoneytenday().toString(),7);
						}
						Row++;
					}
				}
				// 财政预算收入旬报表三栏式
				else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"旬累计金额",16);
					writeExcel(3,2,bookCop,sheetname,"年累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrTaxorgPayoutReportDto)list.get(i)).getNmoneytenday()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getNmoneytenday().toString(),7);
						}
						if(null==((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyyear()){
							writeExcel(Cloumn+3,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+3,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyyear().toString(),7);
						}
						Row++;
					}
				}
				// 财政预算收入旬报表四栏式
				else if (StateConstant.REPORT_FOUR.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"旬累计金额",16);
					writeExcel(3,2,bookCop,sheetname,"月累计金额",16);
					writeExcel(4,2,bookCop,sheetname,"年累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrTaxorgPayoutReportDto)list.get(i)).getNmoneytenday()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getNmoneytenday().toString(),7);
						}
						if(null==((TrTaxorgPayoutReportDto)list.get(i)).getNmoneymonth()){
							writeExcel(Cloumn+3,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+3,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getNmoneymonth().toString(),7);
						}
						if(null==((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyyear()){
							writeExcel(Cloumn+4,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+4,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyyear().toString(),7);
						}
						Row++;
					}
				}
			}
			// 财政预算收入月报表查询
			else if (StateConstant.REPORT_MONTH.equals(sleBillType)) {
				// 财政预算收入月报表二栏式
				if (StateConstant.REPORT_TWO.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"月累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrTaxorgPayoutReportDto)list.get(i)).getNmoneymonth()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getNmoneymonth().toString(),7);
						}
						Row++;
					}
				}
				// 财政预算收入月报表三栏式
				else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"月累计金额",16);
					writeExcel(3,2,bookCop,sheetname,"年累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrTaxorgPayoutReportDto)list.get(i)).getNmoneymonth()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getNmoneymonth().toString(),7);
						}
						if(null==((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyyear()){
							writeExcel(Cloumn+3,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+3,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyyear().toString(),7);
						}
						Row++;
					}
				} 
			}
			// 财政预算收入季报表查询
			else if (StateConstant.REPORT_QUAR.equals(sleBillType)) {
				// 财政预算收入季报表二栏式
				if (StateConstant.REPORT_TWO.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"季累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyquarter()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyquarter().toString(),7);
						}
						Row++;
					}
				}
				// 财政预算收入季报表三栏式
				else if (StateConstant.REPORT_THREE.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"季累计金额",16);
					writeExcel(3,2,bookCop,sheetname,"年累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyquarter()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyquarter().toString(),7);
						}
						if(null==((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyyear()){
							writeExcel(Cloumn+3,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+3,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyyear().toString(),7);
						}
						Row++;
					}
				}
			}
			// 财政预算收入年报表查询
			else if (StateConstant.REPORT_YEAR.equals(sleBillType)) {
				// 财政预算收入年报表二栏式
				if (StateConstant.REPORT_TWO.equals(sleBillKind)) {
					writeExcel(2,2,bookCop,sheetname,"年累计金额",16);
					
					for(int i = 0; i < list.size(); i++){
						writeExcel(Cloumn,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubcode(),13);
						writeExcel(Cloumn+1,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getSbudgetsubname(),13);
						if(null==((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyyear()){
							writeExcel(Cloumn+2,Row,bookCop,sheetname,"0.00",7);
						}else{
							writeExcel(Cloumn+2,Row,bookCop,sheetname,((TrTaxorgPayoutReportDto)list.get(i)).getNmoneyyear().toString(),7);
						}
						Row++;
					}
				} 
			}
			
			
			//清除其他sheet
			while( bookCop.getSheetNames().length !=1){
				if(writeSheet.getName().equals(bookCop.getSheet(bookCop.getSheetNames().length-1).getName()))
					bookCop.removeSheet(0);
				else
					bookCop.removeSheet(bookCop.getSheetNames().length-1);
				
			}
			
			bookCop.write();
	        bookCop.close();
	        bookOri.close(); 
        
		} catch (BiffException e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		} catch (IOException e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		} catch (Exception e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		}
		
		return writefilepath;
	}
	
	// 新建excel表，填写数据内容
	public static String getreport() throws ITFEBizException {
		
		String writefilepath="";
		try {
			
			// 打开要写的Excel文件
//			Workbook bookOri= Workbook.getWorkbook(new File(filepath+filename));
			Workbook bookOri= Workbook.getWorkbook(inputstream);
			writefilepath=newfilepath+copyFilename;
			
			File file = new File(writefilepath);
			File dir = new File(file.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			// 打开临时表
			WritableWorkbook bookCop = Workbook.createWorkbook(file,bookOri);
			// 打开模板sheet
			Sheet readSheet = bookOri.getSheet(sheetname);
			
			/**
			 * 设置表头信息
			 */
			//设置表标题
			writeExcel(0,0,bookCop,sheetname,reporttitle,3);
			
			//设置编制单位
			writeExcel(1,1,bookCop,sheetname,unit,0);
			
			//编制数据
			int rows = readSheet.getRows();
			for(int i=2;i<rows;i++){
				
				//取得数据列标示内容
				String cellContent="";
				if(sheetname.equals(StateConstant.RPT_TYPE_NAME_4)){
					cellContent=readSheet.getCell(7,i).getContents().trim();
				}
				else{
					cellContent=readSheet.getCell(0,i).getContents().trim();
				}
				//如果取得的科目代码为空，继续搜索
				if(null==cellContent || "".equals(cellContent.trim())){
					continue;
				}
//				cellContent=cellContent.substring(cellContent.indexOf(".")+1).trim();
				String[] strarr=null;
				strarr=(String[])datamap.get(cellContent.trim());
				//如果取得数组为空，继续搜寻
				if(null ==strarr || strarr.length==0){
					
//					if(sheetname.equals(StateConstant.RPT_TYPE_NAME_4)){
//						for(int j=0;j<4;j++){
//							writeExcel(j+2,i,bookCop,sheetname,"0.00",5);
//						}
//					}
//					else{
//						writeExcel(2,i,bookCop,sheetname,"0.00",5);
//					}
					continue;
				}
				
				for(int j=0;j<strarr.length;j++){
					if("".equals(strarr[j]) || "0.00".equals(strarr[j])){
//						writeExcel(j+2,i,bookCop,sheetname,"0.00",5);
					}
					else{
						writeExcel(j+2,i,bookCop,sheetname,strarr[j],2);
					}
				}
				
				datamap.remove(cellContent.trim());
			}
			
			//全口径其他收入处理
			if(sheetname.equals(StateConstant.RPT_TYPE_NAME_4)){
				
				BigDecimal sum = new BigDecimal(0.00);
				BigDecimal central = new BigDecimal(0.00);
				BigDecimal provice = new BigDecimal(0.00);
				BigDecimal city = new BigDecimal(0.00);
				
				for(Object o: datamap.keySet()){   
					String[] strarr = (String[])datamap.get(o);
					
					sum=ArithUtil.add(sum,strarr[0]);
					central=ArithUtil.add(central,strarr[1]);
					provice=ArithUtil.add(provice,strarr[2]);
					city=ArithUtil.add(city,strarr[3]);
				} 
				writeExcel(2,25,bookCop,sheetname,sum.toString(),2);
				writeExcel(3,25,bookCop,sheetname,central.toString(),2);
				writeExcel(4,25,bookCop,sheetname,provice.toString(),2);
				writeExcel(5,25,bookCop,sheetname,city.toString(),2);
			}
			
			bookCop.write();
	        bookCop.close();
	        bookOri.close(); 
        
		} catch (BiffException e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		} catch (IOException e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		} catch (Exception e) {
			logger.error("文件写入失败!", e);
			throw new ITFEBizException("文件写入失败!", e);
		}
		
		return writefilepath;
	}

}

package com.cfcc.itfe.client.common.report;

import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.jaf.rcp.reportx.ContextFinder;

/**
 * 基本上从旧的rcp中照搬过来
 * sjz根据ITFE系统的需要进行了修改
 * @author fengqing
 */
public class ReportGen {
	static Map<String, JasperReport> jasperReportMap = new HashMap<String, JasperReport>();
	private static Log log = LogFactory.getLog(ReportGen.class);

	/**
	 * 产生JasperPrint, 先产生JasperReport,然后作缓存,因为JasperReport对于与.jasper文件，都是一样的，
	 * 但是JasperPrint由于传入的参数及数据不同，而不一样。
	 * @param params 报表所需参数的HashMap
	 * @param data   报表明细区域所需数据的Collection
	 * @param report 报表显示的控件
	 * @param reportIndex 报表配置文件
	 *            ,如："/com/cfcc/jaf/rcp/report/example2.jasper"
	 * @return 报表显示对象
	 * @throws JRException
	 */
	public static void genReport(Map<String,Object> params, Collection<Object> data, ReportComposite report, String reportIndex)
			throws JRException {
		JasperPrint print = genReport(params, data, reportIndex);
		if (print == null){
			log.error("报表：" + reportIndex + "生成时出错，返回报表对象为空");
			throw new JRException("报表：" + reportIndex + "生成时出错，返回报表对象为空");
		}else{
			report.getReportViewer().setDocument(print);
		}
	}

	/**
	 * 产生JasperPrint, 先产生JasperReport,然后作缓存,因为JasperReport对于与.jasper文件，都是一样的，
	 * 但是JasperPrint由于传入的参数及数据不同，而不一样。
	 * @param params 报表所需参数的HashMap
	 * @param data   报表明细区域所需数据的Collection
	 * @param reportIndex 报表配置文件
	 *            ,如："/com/cfcc/jaf/rcp/report/example2.jasper"
	 * @return 报表显示对象
	 * @throws JRException
	 */
	public static JasperPrint genReport(Map<String,Object> params, Collection<Object> data, String reportIndex)
			throws JRException {
		JasperPrint myJPrint = null;
		// Loading my custom classloader
		Thread.currentThread().setContextClassLoader(new ContextFinder(Thread.currentThread().getContextClassLoader()));
		JasperReport jasperReport = getJasperReport(reportIndex);

		// Passing parameters to the report
		JRDataSource ds;
		if ((data == null) || (data.size() == 0))
			ds = new JREmptyDataSource();
		else
			ds = new JRBeanCollectionDataSource(data);
		// Filling the report with data from the database based on the
		// parameters passed.
		try {
			myJPrint = JasperFillManager.fillReport(jasperReport, params,	ds);
		} catch (Throwable e) {
			log.error("报表：" + reportIndex + "生成时出错，原因是：" + e.getMessage());
			return null;
		}
		return myJPrint;
	}

	private static JasperReport getJasperReport(String reportIndex)
			throws JRException {
		// Loading my jasper file检查缓存中是否存在，如果存在，那么直接从缓存中读取，不再重新生成
		JasperReport jasperReport = (JasperReport) jasperReportMap.get(reportIndex);
		if (jasperReport == null) {
			jasperReport = (JasperReport) net.sf.jasperreports.engine.util.JRLoader
					.loadObject(ReportGen.class.getClassLoader().getResourceAsStream(reportIndex));
			// 放入缓存
			jasperReportMap.put(reportIndex, jasperReport);
		}
		return jasperReport;
	}
	
	/**
	 * 导出单个报表为XLS
	 * 
	 * @param params
	 * @param data
	 * @param reportIndex
	 * @param out
	 * @throws JRException
	 */
	public static void exportReportToXLS(Map<String, Object> params,
			Collection<Object> data, String reportIndex, OutputStream out)
			throws JRException {

		JasperPrint jasperPrint = genReport(params, data, reportIndex);
		JRXlsExporter xlsExporter = new JRXlsExporter();
		xlsExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		xlsExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
//		xlsExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND,
//				Boolean.TRUE);
//		xlsExporter.setParameter(JRXlsExporterParameter.IS_AUTO_DETECT_CELL_TYPE,
//				Boolean.TRUE);
		
		try {
			xlsExporter.exportReport();
			log.debug("Genertate the XLS report ok! :" + out);
		} catch (JRException e) {
			log.error("Generate the XLS report file ERROR!");
			throw e;
		}
	}
	
	
	/**
	 * 导出单个报表为rtf或PDF
	 * 
	 * @param params
	 * @param data
	 * @param reportIndex
	 * @param out
	 * @throws JRException
	 */
	public static void exportReportToPDF(Map<String, Object> params,
			Collection<Object> data, String reportIndex, OutputStream out)
			throws JRException {
		try {
			
			JasperPrint jasperPrint = genReport(params, data, reportIndex);
			JRPdfExporter pdfExporter = new JRPdfExporter();
			pdfExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			pdfExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
//			JasperExportManager.exportReportToPdfStream(jasperPrint, out);
			pdfExporter.exportReport();
			log.debug("Genertate the PDF report ok! :" + out);
		} catch (JRException e) {
			log.error("Generate the PDF report file ERROR!");
			throw e;
		}
	}

}
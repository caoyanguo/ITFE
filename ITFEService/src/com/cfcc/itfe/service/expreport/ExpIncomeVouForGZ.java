package com.cfcc.itfe.service.expreport;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeDetailDto;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.itfe.security.ImplGKEncryptKeyJNI;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.TreasuryEncrypt;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.BatchRetriever;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLBatchRetriever;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

public class ExpIncomeVouForGZ extends AbstractExpReport {
	final Log log = LogFactory.getLog(this.getClass());
	private static final int MAX_NUM = 10000; // 每次取出最大记录数

	public String makeReportByBiz(TrIncomedayrptDto idto, String bizType,
			String sbookorgcode) throws ITFEBizException {

		// 国库主体代码
		String strecode = idto.getStrecode();
		// 预算种类
		String bugtype = idto.getSbudgettype();
		// 辖属标志
		String sbelong = idto.getSbelongflag();
		// 调整期标志
		String strimflag = idto.getStrimflag();
		// 日期
		String srptdate = idto.getSrptdate();
		// 是否含款合计
		String slesumitem = idto.getSdividegroup();
		// 报表查询条件
		String sqlWhere = CommonMakeReport.makesqlwhere(idto);
		String root = ITFECommonConstant.FILE_ROOT_PATH; // 取得根路径
		String dirsep = File.separator; // 取得系统分割符
		String strdate = DateUtil.date2String2(new Date()); // 当前系统年月日
		String filename ="";
		SQLBatchRetriever batchRetriever = null;
		try {
			HashMap<String, String> taxMap = super.converTaxCode(sbookorgcode);
			filename = CommonMakeReport.getExpFileNameByBillTypeForGZ(idto,
					bizType,taxMap);// 
			String fullpath = root + "exportFile" + dirsep + strdate + dirsep+ filename;
			File f = new File(fullpath);
			if (f.exists()) {
				f.delete();
			}
			String sql = " Select * from TV_FIN_INCOME_DETAIL where S_TRECODE = ? and s_intredate= ? with ur";
			batchRetriever = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLBatchRetriever();
			batchRetriever.addParam(strecode); // 国库代码
			batchRetriever.addParam(srptdate); // 报表日期
			batchRetriever.setMaxRows(MAX_NUM); // 设置每次取记录的最大数

			batchRetriever.runQuery(sql);
			StringBuffer filebuf = new StringBuffer();
			filebuf.append("skgkdm,mdgkdm,ysjc,jgdm,kmdm,zwrq,yszl,pzbh,ysdw,jkszldm,yhdm,fse\r\n");
			while (batchRetriever.hasMore()) {
				SQLResults result = batchRetriever.RetrieveNextBatch(); 
				int count = result.getRowCount();
				if (count > 0) {
					// 得到相对文件名称
					String splitSign = ","; // 文件记录分隔符号
					for (int i=0;i< count ;i++) {
						filebuf.append(result.getString(i, 1));
						filebuf.append(splitSign);
						filebuf.append(result.getString(i, 1));
						filebuf.append(splitSign);
						filebuf.append(result.getString(i, 6));
						filebuf.append(splitSign);
						String staxorgcode = "";
						if (taxMap.containsKey(result.getString(i, 11))) {
							staxorgcode = taxMap.get(result.getString(i, 11));
						} else {
							throw new ITFEBizException("核算主体代码" + sbookorgcode
									+ "下TCBS征收机关代码（" + result.getString(i, 11)
									+ "），没有找到对应的地方横联征收机关代码！");
						}
						filebuf.append(staxorgcode);
						filebuf.append(splitSign);
						filebuf.append(result.getString(i, 7));
						filebuf.append(splitSign);
						filebuf.append(result.getString(i, 2));
						filebuf.append(splitSign);
						filebuf.append(result.getString(i, 8));
						filebuf.append(splitSign);
						filebuf.append(result.getString(i, 4));
						filebuf.append(splitSign);
						filebuf.append(result.getString(i, 1));
						filebuf.append(splitSign);
						filebuf.append("1");
						filebuf.append(splitSign);
						filebuf.append("");
						filebuf.append(splitSign);
						filebuf.append(result.getBigDecimal(i, 9));
						filebuf.append("\r\n");
					}

					FileUtil.getInstance().writeFile(fullpath,
							filebuf.toString(),true);
					filebuf = new StringBuffer();
				} else {
					return null;
				}
			}

			return fullpath;
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("生成预算收入凭证出错" + filename, e);
		} finally {
			if (batchRetriever != null) {
				try {
					batchRetriever.closeConnection();
				} catch (Exception e) {
					log.error("关闭连接出错", e);
				}
			}
		}
	}
}

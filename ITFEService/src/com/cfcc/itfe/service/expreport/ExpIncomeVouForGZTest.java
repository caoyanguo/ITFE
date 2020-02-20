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
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

public class ExpIncomeVouForGZTest extends AbstractExpReport {
	final Log log = LogFactory.getLog(this.getClass());
	private static final int MAX_NUM = 300000; // 每次取出最大记录数

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

		String filename = CommonMakeReport.getExpFileNameByBillType(idto,
				bizType);// 
		String root = ITFECommonConstant.FILE_ROOT_PATH; // 取得根路径
		String dirsep = File.separator; // 取得系统分割符
		String strdate = DateUtil.date2String2(new Date()); // 当前系统年月日
		String fullpath = root + "exportFile" + dirsep + strdate + dirsep
				+ filename;
		SQLExecutor sqlExec = null;
		try {
			int count = 0;
			String sql = " where S_TRECODE = ? and s_intredate= ? with ur";
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			sqlExec.setMaxRows(MAX_NUM);
			List<TvFinIncomeDetailDto> resVouList = (List<TvFinIncomeDetailDto>) sqlExec
					.runQueryCloseCon(sql, TvFinIncomeDetailDto.class)
					.getDtoCollection();
			StringBuffer filebuf = new StringBuffer(
					"skgkdm,mdgkdm,ysjc,jgdm,kmdm,zwrq,yszl,pzbh,ysdw,jkszldm,yhdm,fse\r\n");
			HashMap<String, String> taxMap = super.converTaxCode(sbookorgcode);
			if (resVouList.size() > 0) {
				// 得到相对文件名称
				String splitSign = ","; // 文件记录分隔符号
				for (TvFinIncomeDetailDto _dto : resVouList) {
					filebuf.append(_dto.getStrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getStrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getCbdglevel());
					filebuf.append(splitSign);
					String staxorgcode = "";
					if (taxMap.containsKey(_dto.getStaxorgcode())) {
						staxorgcode = taxMap.get(_dto.getStaxorgcode());
					} else {
						throw new ITFEBizException("核算主体代码" + sbookorgcode
								+ "下TCBS征收机关代码（" + _dto.getStaxorgcode()
								+ "），没有找到对应的地方横联征收机关代码！");
					}
					filebuf.append(_dto.getStaxorgcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbdgsbtcode());
					filebuf.append(splitSign);
					filebuf.append(srptdate);
					filebuf.append(splitSign);
					filebuf.append(_dto.getCbdgkind());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSexpvouno());
					filebuf.append(splitSign);
					filebuf.append(_dto.getStrecode());
					filebuf.append(splitSign);
					filebuf.append("1");
					filebuf.append(splitSign);
					filebuf.append("");
					filebuf.append(splitSign);
					filebuf.append(_dto.getFamt());
					filebuf.append("\r\n");
				}

			} else {
				return null;
			}
			FileUtil.getInstance().writeFile(fullpath, filebuf.toString());

			return fullpath;
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("生成预算收入凭证出错" + filename, e);
		} finally {
			if (sqlExec != null) {
				try {
					sqlExec.closeConnection();
				} catch (Exception e) {
					log.error("关闭连接出错", e);
				}
			}
		}
	}
}

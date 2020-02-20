package com.cfcc.itfe.service.expreport;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TvPbcpayReportDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

public class ExpPbcpayVou extends AbstractExpReport {
	final Log log = LogFactory.getLog(this.getClass());

	@SuppressWarnings("unchecked")
	public String makeReportByBiz(TrIncomedayrptDto idto, String bizType,String sbookorgcode)
			throws ITFEBizException {
		// 国库主体代码
		String strecode = idto.getStrecode();
		// 预算种类
		String bugtype = idto.getSbudgettype();
		// 调整期标志
		String strimflag = idto.getStrimflag();
		// 日期
		String srptdate = idto.getSrptdate();
		// 数据表中的报表类型
		String rptType = CommonMakeReport
				.getReportTypeByBillType(idto, bizType);
		TrIncomedayrptDto queryDto = new TrIncomedayrptDto();
		queryDto.setSrptdate(srptdate);
		queryDto.setSbillkind(rptType);
		queryDto.setStrimflag(strimflag);
		queryDto.setSbudgettype(bugtype);
		
		String filename = CommonMakeReport.getExpFileNameByBillType(idto, bizType);// 
		List<TvPbcpayReportDto> pbcpayList = null; // 人行支出
		try {
			String sql = " select A.S_TRECODE as S_TRECODE,A.S_PAYERACCT as S_PAYERACCT,A.C_BDGKIND as C_BDGKIND,B.S_BDGORGCODE as S_BDGORGCODE,B.S_FUNCSBTCODE as S_FUNCSBTCODE ,B.S_ECOSBTCODE as S_ECOSBTCODE,B.C_ACCTPROP as C_ACCTPROP ,A.S_VOUNO as S_VOUNO,A.D_VOUCHER as D_VOUCHER,A.S_BIZTYPE as S_BIZTYPE,A.S_BACKFLAG as S_BACKFLAG,A.F_AMT  as F_AMT from TV_PBCPAY_MAIN A,TV_PBCPAY_SUB B where A.I_VOUSRLNO = B.I_VOUSRLNO AND A.S_ORGCODE = ?  AND  A.S_TRECODE = ? AND A.D_ACCT = ? and A.S_TRASTATE =? ";
			SQLExecutor exec;
				exec = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();
				exec.addParam(sbookorgcode);
				exec.addParam(strecode);
				exec.addParam(srptdate);
				exec.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
				pbcpayList = (List<TvPbcpayReportDto>) exec.runQueryCloseCon(
						sql, TvPbcpayReportDto.class).getDtoCollection();
		
			if (pbcpayList.size() > 0) {
				// 得到相对文件名称
				String root = ITFECommonConstant.FILE_ROOT_PATH; // 取得根路径
				String dirsep = File.separator; // 取得系统分割符
				String strdate = DateUtil.date2String2(new Date()); // 当前系统年月日
				String fullpath = root + "exportFile" + dirsep + strdate
						+ dirsep + filename;
				String splitSign = ","; // 文件记录分隔符号
				StringBuffer filebuf = new StringBuffer(
						"skgkdm,mdgkdm,ysjc,jgdm,kmdm,zwrq,yszl,pzbh,ysdw,jkszldm,yhdm,fse\r\n");
				for (TvPbcpayReportDto _dto : pbcpayList) {
					filebuf.append(_dto.getStrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSpayeracct());
					filebuf.append(splitSign);
					filebuf.append(_dto.getCbdgkind());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbdgorgcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSfuncsbtcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSecosbtcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getCacctprop());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSvouno());
					filebuf.append(splitSign);
					filebuf.append(_dto.getDvoucher());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbiztype());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbackflag());
					filebuf.append(splitSign);
					filebuf.append(_dto.getFamt());
					filebuf.append("\r\n");
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
				return fullpath;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("生成人行支出凭证出错" + filename, e);
		}

	}
}

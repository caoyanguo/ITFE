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
import com.cfcc.itfe.constant.MsgConstant;
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
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

public class ExpIncomeVouForGZ0 extends AbstractExpReport {
	final Log log = LogFactory.getLog(this.getClass());
	private static final int MAX_NUM = 150000; // 每次取出最大记录数

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
		//征收机关
		String staxorg = idto.getStaxorgcode();
		// 报表查询条件
		String sqlWhere = CommonMakeReport.makesqlwhere(idto);
		// 数据表中的报表类型
		String rptType = CommonMakeReport
				.getReportTypeByBillType(idto, bizType);
		TrIncomedayrptDto queryDto = new TrIncomedayrptDto();
		queryDto.setSrptdate(srptdate);
		queryDto.setSbillkind(rptType);
		queryDto.setStrimflag(strimflag);
		queryDto.setSbudgettype(bugtype);

		String filename = null;// 

		try {
			HashMap<String, String> taxMap = super.converTaxCode(sbookorgcode);
			filename = CommonMakeReport.getExpFileNameByBillTypeForGZ(idto,
					bizType, taxMap);// 			
			String sql = "select * from TV_INFILE_DETAIL where S_RECVTRECODE = ? and s_orgcode = ? and s_commitdate= ? and s_status = ? ";
			
			/**
			 * 导出凭证时增加征收机关条件 by hua 2013-06-18
			 */
			sql = new CommonMakeReport().alertSqlStringWithTaxorg(strecode,staxorg,sql);
			
			SQLExecutor sqlExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			sqlExec.setMaxRows(MAX_NUM);
			sqlExec.addParam(strecode);
			sqlExec.addParam(sbookorgcode);
			sqlExec.addParam(srptdate);
			sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
			List<TvInfileDetailDto> resVouList = (List<TvInfileDetailDto>) sqlExec
					.runQueryCloseCon(sql, TvInfileDetailDto.class)
					.getDtoCollection();
			
			// 获取手工税票，一并导出
			List <TvInfileDetailDto> handbookList = getHandBookDetail(strecode, srptdate,staxorg);
			if (null!=handbookList && handbookList.size()>0) {
				resVouList.addAll(handbookList)	;
			}
			if (resVouList.size() > 0) {
				// 得到相对文件名称
				String root = ITFECommonConstant.FILE_ROOT_PATH; // 取得根路径
				String dirsep = File.separator; // 取得系统分割符
				String strdate = DateUtil.date2String2(new Date()); // 当前系统年月日
				String fullpath = root + "exportFile" + dirsep + strdate
						+ dirsep + filename;
				String splitSign = ","; // 文件记录分隔符号
				StringBuffer filebuf = new StringBuffer(
						"skgkdm,mdgkdm,ysjc,jgdm,kmdm,zwrq,yszl,pzbh,ysdw,jkszldm,yhdm,fse\r\n");
				for (TvInfileDetailDto _dto : resVouList) {
					filebuf.append(_dto.getSrecvtrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSrecvtrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgetlevelcode());
					filebuf.append(splitSign);
					String staxorgcode = "";
					if (taxMap.containsKey(_dto.getStaxorgcode())) {
						staxorgcode = taxMap.get(_dto.getStaxorgcode());
					} else {
						throw new ITFEBizException("核算主体代码" + sbookorgcode
								+ "下TCBS征收机关代码（" + _dto.getStaxorgcode()
								+ "），没有找到对应的地方横联征收机关代码！");
					}
					filebuf.append(staxorgcode);
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgetsubcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getScommitdate());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgettype());
					filebuf.append(splitSign);
					if (null == _dto.getStaxticketno()) {
						filebuf.append("");
					} else {
						filebuf.append(_dto.getStaxticketno());
					}
					filebuf.append(splitSign);
					if (null == _dto.getSunitcode()) {
						filebuf.append("");
					} else {
						filebuf.append(_dto.getSunitcode());
					}
					filebuf.append(splitSign);
					if (null == _dto.getSpaybookkind()) {
						filebuf.append("");
					} else {
						filebuf.append(_dto.getSpaybookkind());
					}
					filebuf.append(splitSign);
					if (null == _dto.getSopenaccbankcode()) {
						filebuf.append("");
					} else {
						filebuf.append(_dto.getSopenaccbankcode());
					}

					filebuf.append(splitSign);
					filebuf.append(_dto.getNmoney());
					filebuf.append("\r\n");
				}
				File f = new File(fullpath);
				if (f.exists()) {
					FileUtil.getInstance().deleteFiles(fullpath);
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
				return fullpath;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("生成预算收入凭证出错" + filename, e);
		}

	}

	
	/**
	 * 获取手工税票，并放在税票明细Dto中，以便很容易的一并导出
	 * @param strecode
	 * @param sintredate
	 * @return
	 * @throws JAFDatabaseException
	 */
	private List<TvInfileDetailDto> getHandBookDetail(String strecode,String sintredate,String staxorg) throws JAFDatabaseException {
		String sql = " Select * from TV_FIN_INCOME_DETAIL where S_TRECODE = ? and s_intredate= ? AND ( S_EXPVOUNO NOT IN " +
				" (SELECT S_TAXTICKETNO FROM TV_INFILE WHERE S_RECVTRECODE = ? AND S_COMMITDATE <=? ) OR S_EXPVOUNO is null) ";
		sql = new CommonMakeReport().alertSqlStringWithTaxorg(strecode, staxorg, sql);
		SQLExecutor sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor(); 
		sqlExec.addParam(strecode);
		sqlExec.addParam(sintredate);
		sqlExec.addParam(strecode);
		sqlExec.addParam(sintredate);
		List <TvFinIncomeDetailDto> handBookList= (List<TvFinIncomeDetailDto>) sqlExec.runQueryCloseCon(sql,TvFinIncomeDetailDto.class).getDtoCollection();
		//转换Dto
		List <TvInfileDetailDto> incomeList = null ;
		if (handBookList.size()>0) {
			incomeList= new ArrayList<TvInfileDetailDto>();
			for (TvFinIncomeDetailDto _dto : handBookList) {
				TvInfileDetailDto tmpdto = new TvInfileDetailDto();
				tmpdto.setSrecvtrecode(_dto.getStrecode());
				tmpdto.setSbudgetlevelcode(_dto.getCbdglevel());
				tmpdto.setStaxorgcode(_dto.getStaxorgcode());
				tmpdto.setSbudgetsubcode(_dto.getSbdgsbtcode());
				tmpdto.setScommitdate(_dto.getSintredate());
				tmpdto.setSbudgettype(_dto.getCbdgkind());
				tmpdto.setStaxticketno(_dto.getSexpvouno());
				tmpdto.setSpaybookkind(_dto.getCvouchannel());
				tmpdto.setNmoney(_dto.getFamt());
				incomeList.add(tmpdto);
			}

		}
		return incomeList;

	}
}

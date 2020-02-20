package com.cfcc.itfe.service.expreport;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.itfe.security.ImplGKEncryptKeyJNI;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.TreasuryEncrypt;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

public class ExpCorrVou extends AbstractExpReport {
	final Log log = LogFactory.getLog(this.getClass());

	public String makeReportByBiz(TrIncomedayrptDto idto, String bizType,String sbookorgcode)
			throws ITFEBizException {
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
		// 数据表中的报表类型
		String rptType = CommonMakeReport
				.getReportTypeByBillType(idto, bizType);
		TrIncomedayrptDto queryDto = new TrIncomedayrptDto();
		queryDto.setSrptdate(srptdate);
		queryDto.setSbillkind(rptType);
		queryDto.setStrimflag(strimflag);
		queryDto.setSbudgettype(bugtype);
		String filename = CommonMakeReport.getExpFileNameByBillType(idto, bizType);// 
		List<TvInCorrhandbookDto> resVouList = null; // 收入更正税票
		String sqlwhere= "";
		if (ITFECommonConstant.IFNEWINTERFACE.equals(StateConstant.IFNEWINTERFACE_TRUE)) {
			sqlwhere = " AND s_Status = '"+DealCodeConstants.DEALCODE_ITFE_SUCCESS+"'";
		} else {
			sqlwhere  = " AND (s_Status = '"+DealCodeConstants.DEALCODE_ITFE_SUCCESS+"' OR s_Status <> '"+DealCodeConstants.DEALCODE_ITFE_FAIL+"')";
		}
		try {
			TvInCorrhandbookDto corrdto = new TvInCorrhandbookDto();
			corrdto.setSbookorgcode(sbookorgcode);
			corrdto.setSoripayeetrecode(strecode);
			corrdto.setDacct(CommonUtil.strToDate(srptdate));
			resVouList = CommonFacade.getODB().findRsByDtoForWhere(corrdto, sqlwhere);
		
			if (resVouList.size() > 0) {
				// 得到相对文件名称
				String root = ITFECommonConstant.FILE_ROOT_PATH; // 取得根路径
				String dirsep = File.separator; // 取得系统分割符
				String strdate = DateUtil.date2String2(new Date()); // 当前系统年月日
				String fullpath = root + "exportFile" + dirsep + strdate
						+ dirsep + filename;
				String splitSign = ","; // 文件记录分隔符号
				StringBuffer filebuf = new StringBuffer(
						"pzbh,yskgkdm,ymdgkdm,yjgdm,yysjc,ykmdm,yfzbz,yfse,gzyydm,xskgkdm,xmdgkdm,xjgdm,xysjc,xkmdm,xfzbz,xfse,ssyf,yszl,tzqbz,ztbz,yssgkdm,xssgkdm,zwrq\r\n");
				for (TvInCorrhandbookDto _dto : resVouList) {
					filebuf.append(_dto.getScorrvouno());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSoripayeetrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSoriaimtrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSoritaxorgcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getCoribdglevel());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSoribdgsbtcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSoriastflag());
					filebuf.append(splitSign);
					filebuf.append(_dto.getForicorramt());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSreasoncode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getScurpayeetrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getScuraimtrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getScurtaxorgcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getCcurbdglevel());
					filebuf.append(splitSign);
					filebuf.append(_dto.getScurbdgsbtcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getScurastflag());
					filebuf.append(splitSign);
					filebuf.append(_dto.getFcurcorramt());
					filebuf.append(splitSign);
					if (_dto.getCtrimflag().equals(StateConstant.TRIMSIGN_FLAG_NORMAL)) {
						filebuf.append(_dto.getDacct().toString().replace("-", "")).substring(4,2);//所属月份
					} else {
						filebuf.append("12");
					}
					filebuf.append(_dto.getDacct());//所属月份
					filebuf.append(splitSign);
					filebuf.append(_dto.getCcurbdgkind());
					filebuf.append(splitSign);
					filebuf.append(_dto.getCtrimflag());
					filebuf.append(splitSign);
					filebuf.append("0");
					filebuf.append(splitSign);
					filebuf.append(strecode);
					filebuf.append(splitSign);
					filebuf.append(strecode);
					filebuf.append(splitSign);
					filebuf.append(_dto.getDacct());
					filebuf.append("\r\n");
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
				return fullpath;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("生成预算收入退库凭证出错" + filename, e);
		}

	}
}

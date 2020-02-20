package com.cfcc.itfe.service.expreport;

import java.io.File;
import java.io.FileNotFoundException;
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
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.itfe.security.ImplGKEncryptKeyJNI;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.TreasuryEncrypt;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

public class ExpIncomeVouForTJ extends AbstractExpReportForTJ {
	final Log log = LogFactory.getLog(this.getClass());

	public HashMap makeReportByBiz(TrIncomedayrptDto idto, String bizType,String sbookorgcode,HashMap map)
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
		List<TvInfileDetailDto> resVouList = null; // 收入税票
		//返回Map
		HashMap<String, HashMap> returnMap = new HashMap<String, HashMap>();
		try {
			TvInfileDetailDto voudto = new TvInfileDetailDto();
			voudto.setSorgcode(sbookorgcode);
			voudto.setScommitdate(srptdate);
			voudto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
			
			resVouList = CommonFacade.getODB().findRsByDtoWithUR(voudto);
		
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
					filebuf.append(_dto.getStaxorgcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgetsubcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgettype());
					filebuf.append(splitSign);
					filebuf.append(_dto.getStaxticketno());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSunitcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSpaybookkind());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSopenaccbankcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getNmoney());
					filebuf.append("\r\n");
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
				returnMap.put(fullpath.replaceAll(root, ""), null);
				return returnMap;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("生成预算收入凭证出错" + filename, e);
		}

	}
}

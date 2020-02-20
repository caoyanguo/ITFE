package com.cfcc.itfe.service.expreport;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectforqueryDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.pk.TsTreasuryPK;
import com.cfcc.itfe.security.ImplGKEncryptKeyJNI;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.TreasuryEncrypt;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

public class ExpAmtReportForGZ extends AbstractExpReport {
	final Log log = LogFactory.getLog(this.getClass());

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
		// 数据表中的报表类型
		String rptType = CommonMakeReport
				.getReportTypeByBillType(idto, bizType);
		TrIncomedayrptDto queryDto = new TrIncomedayrptDto();
		queryDto.setSrptdate(srptdate);
		queryDto.setSbillkind(rptType);
		queryDto.setStrimflag(strimflag);
		queryDto.setSbudgettype(bugtype);
//		queryDto.setSfinorgcode(idto.getSfinorgcode());
		List<TrIncomedayrptDto> resList = new ArrayList<TrIncomedayrptDto>();// 收入报表
		String filename = CommonMakeReport.getExpFileNameByBillType(idto,
				bizType);
		try {
			
            if (MsgConstant.RULE_SIGN_SELF.equals(sbelong)) {
	            List <TrIncomedayrptDto>  specList = procSpecBudsubject(idto, rptType, sbookorgcode);
	            resList.addAll(specList);
	            //过滤掉特殊预算科目
	            sqlWhere += " AND S_BUDGETSUBCODE NOT IN (select S_SUBJECTCODE from TS_BUDGETSUBJECTFORQUERY where S_ORGCODE = '"
					+ sbookorgcode + "')";
			}
            
            resList.addAll(CommonFacade.getODB().findRsByDtoForWhere(queryDto,
					sqlWhere));
			if (resList.size() > 0) {
				// 得到相对文件名称
				Map<String, String> taxorg = super.converTaxCode(sbookorgcode);
				String root = ITFECommonConstant.FILE_ROOT_PATH; // 取得根路径
				String dirsep = File.separator; // 取得系统分割符
				String strdate = DateUtil.date2String2(new Date()); // 当前系统年月日
				String fullpath = root + "exportFile" + dirsep + strdate
						+ dirsep + filename;
				String splitSign = ","; // 文件记录分隔符号
				StringBuffer filebuf = new StringBuffer(
						"skgkdm,mdgkdm,ssgkdm,ysjc,kmdm,zwrq,xsbz,yszl,rlj,ylj,nlj\r\n");
				
				//得到国库代码的级次
				TsTreasuryPK trepk = new TsTreasuryPK();
				trepk.setSorgcode(sbookorgcode);
				trepk.setStrecode(strecode);
				TsTreasuryDto tredto = (TsTreasuryDto)DatabaseFacade.getODB().find(trepk);
				String tredtolevel = tredto.getStrelevel();
				
				
				for (int i = 0; i < resList.size(); i++) {
					TrIncomedayrptDto _dto = resList.get(i);
					
					if (taxorg.get(_dto.getStaxorgcode()) == null) {
						throw new ITFEBizException("核算主体代码" + sbookorgcode
								+ "下TCBS征收机关代码（" + _dto.getStaxorgcode()
								+ "），没有找到对应的地方横联征收机关代码！");
					}
					//如果选本级报表并且报表的级次大于当前国库的级次则不导出
					if(MsgConstant.RULE_SIGN_SELF.equals(sbelong)&&_dto.getSbudgetlevelcode().compareTo(tredtolevel)>0){
						continue;
					}
					
					filebuf.append(_dto.getStrecode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getStrecode());
					filebuf.append(splitSign);
					filebuf.append(strecode);
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgetlevelcode());
					filebuf.append(splitSign);
//					filebuf.append(taxorg.get(_dto.getStaxorgcode()));
//					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgetsubcode());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSrptdate());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbelongflag());
					filebuf.append(splitSign);
					filebuf.append(_dto.getSbudgettype());
					filebuf.append(splitSign);
					filebuf.append(_dto.getNmoneyday());
					filebuf.append(splitSign);
					filebuf.append(_dto.getNmoneymonth());
					filebuf.append(splitSign);
					filebuf.append(_dto.getNmoneyyear());
					if ((i + 1) != resList.size())
						filebuf.append("\r\n");
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString());
				return fullpath;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}

	}



	/**
	 * 增加对特殊科目，主要是用于类似成品油税收的没有具体征收机关，在统计本级次报表时无法统计的 的科目
	 * 
	 * @param idto
	 * @return
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 */
	private List<TrIncomedayrptDto> procSpecBudsubject(TrIncomedayrptDto idto,
			String rptType, String sbookorgcode) throws JAFDatabaseException,
			ValidateException {

		TrIncomedayrptDto queryDto = new TrIncomedayrptDto();
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
		queryDto.setSrptdate(srptdate);
		queryDto.setSbillkind(rptType);
		queryDto.setStrimflag(strimflag);
		queryDto.setSbudgettype(bugtype);
		//queryDto.setSfinorgcode(idto.getSfinorgcode());
		queryDto.setStaxorgcode(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
		queryDto.setSbelongflag(MsgConstant.RULE_SIGN_ALL);
		// 取统计国库的特殊科目的全辖数：
		String sqlwhere = " AND s_trecode = '"
				+ strecode
				+ "' AND S_BUDGETSUBCODE IN (select S_SUBJECTCODE from TS_BUDGETSUBJECTFORQUERY where S_ORGCODE = '"
				+ sbookorgcode + "' )  order by S_BUDGETSUBCODE";
		List<TrIncomedayrptDto> listup = CommonFacade.getODB()
				.findRsByDtoForWhere(queryDto, sqlwhere);

		// 取管辖国库等于统计国库的特殊科目的全辖数
		sqlwhere = " AND  s_trecode  IN ( select S_TRECODE from TS_TREASURY where S_GOVERNTRECODE = '"
				+ strecode
				+ "' )  AND S_BUDGETSUBCODE IN (select S_SUBJECTCODE from TS_BUDGETSUBJECTFORQUERY where s_orgcode = '"
				+ sbookorgcode + "' ) order by S_BUDGETSUBCODE";
		List<TrIncomedayrptDto> listdown = CommonFacade.getODB()
				.findRsByDtoForWhere(queryDto, sqlwhere);

		// 科目实际发生数 = 本机全辖数 --管辖国库的全辖数据，按科目循环
		if (null != listup && listup.size() > 0) {
			for (TrIncomedayrptDto _dto : listup) {
				String sbtcode = _dto.getSbudgetsubcode();
				String budlevel = _dto.getSbudgetlevelcode();
				_dto.setStaxorgcode(idto.getSfinorgcode());
				for (TrIncomedayrptDto _dto2 : listdown) {
					String sbtcode2 = _dto2.getSbudgetsubcode();
					String budlevel2 = _dto2.getSbudgetlevelcode();
					if (sbtcode.equals(sbtcode2) && budlevel.equals(budlevel2)) {
						_dto.setNmoneyday(_dto.getNmoneyday().add(_dto2.getNmoneyday().negate()));
						_dto.setNmoneymonth(_dto.getNmoneymonth().add(_dto2.getNmoneymonth().negate()));
						_dto.setNmoneyquarter(_dto.getNmoneyquarter().add(_dto2.getNmoneyquarter().negate()));
						_dto.setNmoneytenday(_dto.getNmoneytenday().add(_dto2.getNmoneytenday().negate()));
						_dto.setNmoneyyear(_dto.getNmoneyyear().add(_dto2.getNmoneyyear().negate()));
					}
				}
			}
		}

		return listup;

	}

	/**
	 * 获取特殊预算科目的map
	 * @throws ValidateException 
	 * @throws JAFDatabaseException 
	 */
	private HashMap findSpecialBudjectSubjectMap(String sbookorgcode) throws JAFDatabaseException, ValidateException{
		//获得特殊科目的list
		TsBudgetsubjectforqueryDto qto = new TsBudgetsubjectforqueryDto();
		qto.setSorgcode(sbookorgcode);
		List<TsBudgetsubjectforqueryDto> bslist = CommonFacade.getODB().findRsByDto(qto);
		
		HashMap<String,String> map = new HashMap<String,String>();
		for(TsBudgetsubjectforqueryDto dto :bslist){
			map.put(dto.getSsubjectcode(),dto.getSsubjectname());
		}
		return map;
	}
	
}

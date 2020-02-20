package com.cfcc.itfe.service.recbiz.agentbankcountanalysedataimport;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.service.recbiz.agentbankcountanalysedataimport.AbstractAgentBankCountAnalyseDataImportService;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.ServiceUtil;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.core.service.filetransfer.support.FileSystemConfig;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.CommonQto;
import com.cfcc.jaf.persistence.util.SqlUtil;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author Administrator
 * @time 15-03-16 11:03:46 codecomment:
 */

public class AgentBankCountAnalyseDataImportService extends
		AbstractAgentBankCountAnalyseDataImportService {
	private static Log log = LogFactory
			.getLog(AgentBankCountAnalyseDataImportService.class);

	/**
	 * 数据导入
	 * 
	 * @generated
	 * @param dataList
	 * @return java.util.ArrayList
	 * @throws ITFEBizException
	 */
	public List dataImport(List dataList) throws ITFEBizException {
		// 得到文件上传配置
		FileSystemConfig sysconfig = (FileSystemConfig) ContextFactory
				.getApplicationContext().getBean("fileSystemConfig.ITFE.ID");
		// 文件上传根路径
		String root = sysconfig.getRoot();
		List<String> failList = new ArrayList<String>();
		List<File> succeeList = new ArrayList<File>();
		List resultList = new ArrayList();
		StringBuffer failReasonSb = new StringBuffer();
		resultList.add(succeeList);
		resultList.add(failList);
		resultList.add(failReasonSb);
		File f = new File("");
		for (String str : (List<String>) dataList) {
			try {
				f = new File(root
						+ str.toString().replace("/", File.separator).replace(
								"\\", File.separator));
				if (f.getName().toLowerCase().endsWith(".kcr")) {
					//解析库存
					kcrTxtToDto(f, failList);
					succeeList.add(f);
					if(!f.renameTo(new File(f.getPath() + ".TMP"))){	//重命名失败，删除文件
						f.delete();
					}
				} else if (f.getName().toLowerCase().endsWith("sr.txt")) {
					//解析收入日报
					srTxtToDto(f, failList);
					succeeList.add(f);
					if(!f.renameTo(new File(f.getPath() + ".TMP"))){	//重命名失败，删除文件
						f.delete();
					}
				} else {
					f.delete();
					failList.add(f.getPath());
				}
			} catch (ITFEBizException e) {
				f.delete();
				failList.add(str);
				failReasonSb.append(f.getName() + "加载失败原因：");
				failReasonSb.append("\n" + e.getMessage() + "\n");
				continue;
			}
		}
		return resultList;
	}

	/**
	 * 数据发送
	 * 
	 * @generated
	 * @param senddataInfo
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public String sendData(List senddataInfo) throws ITFEBizException {
		try {
			MuleClient client = new MuleClient();
			Map map = new HashMap();
			MuleMessage message = new DefaultMuleMessage(map);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY,
					MsgConstant.MSG_NO_3128 + "_OUT");
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, getLoginInfo()
					.getSorgcode());
			message.setPayload(map);

			TsConvertfinorgDto searchFinOrgCode = new TsConvertfinorgDto();
			searchFinOrgCode.setStrecode((String) senddataInfo.get(0));
			searchFinOrgCode.setSorgcode(getLoginInfo().getSorgcode());
			List<TsConvertfinorgDto> finList = CommonFacade.getODB()
					.findRsByDto(searchFinOrgCode);
			if (null == finList || finList.size() == 0) {
				return "发送报表失败：国库代码：" + (String) senddataInfo.get(0)
						+ "没有找到对应的财政机关代码，请在[财政机构信息维护]进行维护！";
			}
			message.setProperty(MessagePropertyKeys.MSG_BILL_CODE, finList.get(
					0).getSfinorgcode());
			message.setProperty(MessagePropertyKeys.MSG_DATE,
					(String) senddataInfo.get(1));
			List<IDto> resultList = getRptDate((String) senddataInfo.get(0),
					(String) senddataInfo.get(1));
			if (null == resultList || resultList.size() == 0) {
				return "发送报表失败：没有查询到" + (String) senddataInfo.get(1)
						+ "当日的报表信息！";
			}
			message.setProperty(MessagePropertyKeys.MSG_CONTENT, resultList);
			if(getLoginInfo().getSorgcode()!=null&&getLoginInfo().getSorgcode().startsWith("1702"))
				message = client.send("vm://ManagerMsgToMofCity", message);
			else
				message = client.send("vm://ManagerMsgToMof", message);
			ServiceUtil.checkResult(message);
			return "报表发送成功！";
		} catch (MuleException e) {
			log.error("发送报表数据失败！", e);
			throw new ITFEBizException("发送报表数据失败！", e);
		} catch (JAFDatabaseException e) {
			log.error("查询数据库异常！", e);
			throw new ITFEBizException("查询数据库异常！", e);
		} catch (ValidateException e) {
			log.error("查询数据库异常！", e);
			throw new ITFEBizException("查询数据库异常！", e);
		}
	}

	private List<IDto> getRptDate(String treCode, String rptDate)
			throws JAFDatabaseException, ValidateException {
		List<IDto> searchResultList = new ArrayList<IDto>();
		TrStockdayrptDto searchDto = new TrStockdayrptDto();
		searchDto.setStrecode(treCode);
		searchDto.setSaccdate(rptDate);
		searchResultList.addAll((List<IDto>) CommonFacade.getODB().findRsByDto(
				searchDto));
		TrIncomedayrptDto searchInComeDto = new TrIncomedayrptDto();
		searchInComeDto.setStrecode(treCode);
		searchInComeDto.setSrptdate(rptDate);
		searchInComeDto
				.setSbillkind(StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL);
		searchResultList.addAll((List<IDto>) CommonFacade.getODB().findRsByDto(
				searchInComeDto));
		if (null == searchResultList || searchResultList.size() == 0) {
			return null;
		}
		return searchResultList;

	}

	/**
	 * 解析库存日报
	 * 
	 * @param f
	 * @param failList
	 * @throws ITFEBizException
	 */
	private void kcrTxtToDto(File f, List<String> failList)
			throws ITFEBizException {
		try {
			List<String> fileContent = FileUtil.getInstance().readFileWithLine(
					f.getPath());
			String orgCode = getLoginInfo().getSorgcode(); // 核算主体
			String treCode = ""; // 国库代码
			String rptdate = ""; // 报表日期
			String line = ""; // 每行信息
			List<String> rptContent = new ArrayList<String>(); // 报表内容
			boolean dcdataFlag = false;
			for (int i = 0; i < fileContent.size(); i++) {
				line = fileContent.get(i);
				if (line.indexOf("<fsgkdm>") >= 0) {
					treCode = fileContent.get(i + 1).trim();
				} else if (line.indexOf("<fsdate>") >= 0) {
					rptdate = fileContent.get(i + 1).trim();
				} else if (line.indexOf("<dc_data>") >= 0) {
					dcdataFlag = true;
				} else if (dcdataFlag && line.split(",").length == 9) {
					rptContent.add(line);
				} else if (line.indexOf("</dc_data>") >= 0) {
					// 读取文件结束
					break;
				}
			}
			String finOrgCode = getFinOrgCode(treCode).getSfinorgcode();
			// 国库代码 报表日期 财政机构代码 报表内容 任意为空时，该文件解析失败
			if (StringUtils.isBlank(treCode) || StringUtils.isBlank(rptdate)
					|| StringUtils.isBlank(finOrgCode)
					|| rptContent.size() == 0) {
				failList.add(f.getPath());
			}
			String[] strs;
			TrStockdayrptDto trStockdayrptDto;
			List<IDto> kcrListDto = new ArrayList<IDto>();
			for (String str : rptContent) {
				strs = str.split(",");
				trStockdayrptDto = new TrStockdayrptDto();
				trStockdayrptDto.setStrecode(treCode);
				trStockdayrptDto.setSrptdate(rptdate);
				trStockdayrptDto.setSorgcode(orgCode);
				trStockdayrptDto.setSaccno(strs[1]);
				trStockdayrptDto.setSaccname(strs[2]);
				trStockdayrptDto.setSaccdate(rptdate);
				trStockdayrptDto.setNmoneyin(new BigDecimal(strs[6]));
				trStockdayrptDto.setNmoneyout(new BigDecimal(strs[5]));
				trStockdayrptDto.setNmoneytoday(new BigDecimal(strs[7]));
				trStockdayrptDto.setNmoneyyesterday(trStockdayrptDto
						.getNmoneytoday().add(trStockdayrptDto.getNmoneyout())
						.subtract(trStockdayrptDto.getNmoneyin()));
				kcrListDto.add(trStockdayrptDto);
			}
			DatabaseFacade.getODB().create(CommonUtil.listTArray(kcrListDto));
		} catch (FileOperateException e) {
			log.error("解析库存日报失败", e);
			throw new ITFEBizException("解析库存日报失败", e);
		} catch (ArrayIndexOutOfBoundsException e) {
			log.error("解析库存日报失败", e);
			throw new ITFEBizException("解析库存日报失败", e);
		} catch (JAFDatabaseException e) {
			if (e.getSqlState().equals("23505")) {
				log.error("导入报表文件数据主键重复，检查该文件是否已经导入！", e);
				throw new ITFEBizException("导入报表文件数据主键重复，检查该文件是否已经导入！", e);
			}
			log.error("解析库存日报入库时失败！", e);
			throw new ITFEBizException("解析库存日报入库时失败", e);
		}
	}

	/**
	 * 查询国库对应的财政机构信息
	 * 
	 * @param treCode
	 *            国库代码
	 * @return
	 * @throws ITFEBizException
	 */
	private TsConvertfinorgDto getFinOrgCode(String treCode)
			throws ITFEBizException {
		try {
			TsConvertfinorgDto searchFinOrgDto = new TsConvertfinorgDto();
			searchFinOrgDto.setSorgcode(getLoginInfo().getSorgcode());
			searchFinOrgDto.setStrecode(treCode);
			List<TsConvertfinorgDto> resultList = CommonFacade.getODB()
					.findRsByDto(searchFinOrgDto);
			if (null == resultList || resultList.size() == 0) {
				throw new ITFEBizException("请在[财政机构信息维护]中维护国库代码:" + treCode
						+ "对应的财政机构信息！");
			}
			return resultList.get(0);
		} catch (JAFDatabaseException e) {
			log.error("查询数据库异常！", e);
			throw new ITFEBizException("查询数据库异常！", e);
		} catch (ValidateException e) {
			log.error("查询数据库异常！", e);
			throw new ITFEBizException("查询数据库异常！", e);
		}

	}

	/**
	 * 解析收入日报
	 * 
	 * @param f
	 * @param failList
	 * @throws ITFEBizException
	 */
	private void srTxtToDto(File f, List<String> failList)
			throws ITFEBizException {
		try {
			List<String> fileContent = FileUtil.getInstance().readFileWithLine(
					f.getPath());
			String[] strs;
			TrIncomedayrptDto trIncomedayrptDto;
			Map<String, String> finOrgMap = new HashMap<String, String>();
			List<IDto> listDtos = new ArrayList<IDto>();
			Map<String, TsBudgetsubjectDto> tsBdsbtInfosMap = SrvCacheFacade
					.cacheTsBdgsbtInfo(getLoginInfo().getSorgcode());
			for (String str : fileContent) {
				strs = str.trim().split("	");
				if (strs.length != 8) {
					continue;
				}
				trIncomedayrptDto = new TrIncomedayrptDto();
				trIncomedayrptDto.setStrecode(strs[1]);
				if (null == finOrgMap.get(strs[1])) {
					finOrgMap.put(strs[1], getFinOrgCode(strs[1])
							.getSfinorgcode());
				}
				trIncomedayrptDto.setSfinorgcode(finOrgMap.get(strs[1]));
				trIncomedayrptDto.setStaxorgcode(strs[3]);
				trIncomedayrptDto.setSrptdate(strs[0]);
				trIncomedayrptDto.setSbudgettype(strs[4]);
				trIncomedayrptDto.setSbudgetlevelcode(strs[5]);
				trIncomedayrptDto.setSbudgetsubcode(strs[2]);
				if (null != tsBdsbtInfosMap.get(strs[2])) {
					trIncomedayrptDto.setSbudgetsubname(tsBdsbtInfosMap.get(
							strs[2]).getSsubjectname());
				} else {
					continue;
//					throw new ITFEBizException("文件:" + f.getName() + "中，预算科目："
//							+ strs[2] + "在[预算科目查询]没有找到对应的信息，请确认！");
				}
				trIncomedayrptDto.setSbelongflag("0");
				trIncomedayrptDto.setStrimflag(MsgConstant.TIME_FLAG_NORMAL);
				trIncomedayrptDto.setSdividegroup("0");
				trIncomedayrptDto
						.setSbillkind(StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL);
				trIncomedayrptDto.setNmoneyday(new BigDecimal(strs[6]));
				//如果是01说明是第一天，第一天则等于当日发生额
				if(trIncomedayrptDto.getSrptdate().endsWith("01")){
					trIncomedayrptDto.setNmoneymonth(new BigDecimal(strs[6]));
				}else{
					trIncomedayrptDto.setNmoneymonth(getNmoneymonth((TrIncomedayrptDto)trIncomedayrptDto.clone()));
				}
				trIncomedayrptDto.setNmoneyyear(new BigDecimal(strs[7]));
				trIncomedayrptDto.setNmoneytenday(BigDecimal.ZERO);
//				trIncomedayrptDto.setNmoneymonth(BigDecimal.ZERO);
				trIncomedayrptDto.setNmoneyquarter(BigDecimal.ZERO);
				listDtos.add(trIncomedayrptDto);
			}
			DatabaseFacade.getODB().create(CommonUtil.listTArray(listDtos));
		} catch (FileOperateException e) {
			log.error("解析库存日报失败", e);
			throw new ITFEBizException("解析库存日报失败", e);
		} catch (JAFDatabaseException e) {
			if (e.getSqlState().equals("23505")) {
				log.error("导入报表文件数据主键重复，检查该文件是否已经导入！", e);
				throw new ITFEBizException("导入报表文件数据主键重复，检查该文件是否已经导入！", e);
			}
			log.error("解析库存日报入库时失败！", e);
			throw new ITFEBizException("解析库存日报入库时失败", e);
		}
	}

	/**
	 * 获取月累计
	 * @param trIncomedayrptDto
	 * @return
	 * @throws ITFEBizException
	 */
	private BigDecimal getNmoneymonth(TrIncomedayrptDto trIncomedayrptDto) throws ITFEBizException {
		try {
			String rptDate = trIncomedayrptDto.getSrptdate();
			trIncomedayrptDto.setSrptdate(null);
			BigDecimal moneyday = trIncomedayrptDto.getNmoneyday();
			trIncomedayrptDto.setNmoneyday(null);
			trIncomedayrptDto.setSbudgetsubname(null);
			CommonQto commonQto = SqlUtil.IDto2CommonQto(trIncomedayrptDto);
			SQLExecutor sqlExecutor = DatabaseFacade.getODB()
					.getSqlExecutorFactory().getSQLExecutor();
			sqlExecutor.addParam(commonQto.getLParams());
			SQLResults sqlResults = sqlExecutor.runQueryCloseCon("SELECT sum(n_moneyday) FROM tr_Incomedayrpt " + commonQto.getSWhereClause() + " AND S_RPTDATE LIKE '" + rptDate.substring(0, 6) + "%'");
			if(null != sqlResults.getBigDecimal(0, 0)){
				return sqlResults.getBigDecimal(0, 0).add(moneyday);
			}else{
				return moneyday;
			}
		} catch (JAFDatabaseException e) {
			log.error("查询收入日报月累计失败！",e);
			throw new ITFEBizException("查询收入日报月累计失败！", e);
		}
	}

}
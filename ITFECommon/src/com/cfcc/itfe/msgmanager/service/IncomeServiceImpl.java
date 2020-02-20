package com.cfcc.itfe.msgmanager.service;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.CallShellUtil;
import com.cfcc.deptone.common.util.DB2CallShell;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractServiceManagerServer;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TsSystemDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvInfileDetailDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.persistence.dto.TvInfileTmpCountryDto;
import com.cfcc.itfe.persistence.dto.TvInfileTmpDto;
import com.cfcc.itfe.persistence.dto.TvInfileTmpPlaceDto;
import com.cfcc.itfe.persistence.dto.TvInfileTmpTipsDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.verify.VerifyCallShellRs;
import com.cfcc.itfe.verify.VerifyFileName;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLBatchRetriever;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

public class IncomeServiceImpl extends AbstractServiceManagerServer {

	private static Log logger = LogFactory.getLog(IncomeServiceImpl.class);

	/**
	 * 处理从客户端传送过来的文件对象(收入税票业务处理)
	 * 
	 * @param FileResultDto
	 *            fileResultDto 文件结果集对象DTO
	 * @param String
	 *            sorgcode 机构代码
	 * @param String
	 *            susercode 用户代码
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public void dealFileDto(FileResultDto fileResultDto, String sorgcode,
			String susercode) throws ITFEBizException {

		try {
			// 第一步校验导入文件的重复性
			boolean repeatflag = VerifyFileName.verifyImportRepeat(sorgcode,fileResultDto.getSfilename());
			if (repeatflag) {
				// 是重复导入,提示用户错误信息.
				logger.error("此文件[" + fileResultDto.getSfilename() + "]已经导入成功,不能重复导入!");
				throw new ITFEBizException("此文件["	 + fileResultDto.getSfilename() + "]已经导入成功,不能重复导入!");
			}
			Integer iscollect = 0;
			if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(fileResultDto.getArea())) {
				iscollect = Integer.valueOf(fileResultDto.getIscollect());
			} else {
				TsSystemDto sysdto = new TsSystemDto();
				List<TsSystemDto> syslist = CommonFacade.getODB().findRsByDto(sysdto);
				if (null == syslist || syslist.size() == 0) {
					logger.error("查询系统参数表错误，没有找到对应的基础数据!");
					throw new ITFEBizException("查询系统参数表错误，没有找到对应的基础数据!");
				}
				iscollect = syslist.get(0).getImodicount();
			}

			if (MsgConstant.MSG_SOURCE_PLACE.equals(fileResultDto.getCsourceflag())) {
				dealPlaceData(fileResultDto, sorgcode, susercode, iscollect);
			} else if (MsgConstant.MSG_SOURCE_NATION.equals(fileResultDto.getCsourceflag())) {
				dealNationData(fileResultDto, sorgcode, susercode, iscollect);
			} else if (MsgConstant.MSG_SOURCE_TBS.equals(fileResultDto.getCsourceflag())) {
				if(sorgcode.startsWith("11")){
					dealTBSDataForZJ(fileResultDto, sorgcode, susercode, iscollect);
				}else{
					dealTBSData(fileResultDto, sorgcode, susercode, iscollect);
				}
			} else if (MsgConstant.MSG_SOURCE_TIPS.equals(fileResultDto.getCsourceflag())) {
				dealTIPSData(fileResultDto, sorgcode, susercode, iscollect);
			} else {
				// 是重复导入,提示用户错误信息.
				logger.error("文件来源标志[" + fileResultDto.getCsourceflag() + "]不对!");
				throw new ITFEBizException("文件来源标志[" + fileResultDto.getCsourceflag() + "]不对!");
			}
		} catch (JAFDatabaseException e) {
			logger.error("数据搬运的时候出现数据库异常!", e);
			throw new ITFEBizException("数据搬运的时候出现数据库异常!", e);
		} catch (ValidateException e) {
			logger.error("删除临时表记录的时候出现数据库异常!", e);
			throw new ITFEBizException("删除临时表记录的时候出现数据库异常!", e);
		}
	}

	/**
	 * 处理TBS接口数据
	 * 
	 * @param FileResultDto
	 *            fileResultDto 解析文件接口对象
	 * @param String
	 *            sorgcode 机构代码
	 * @param String
	 *            susercode 用户代码
	 * @throws ITFEBizException
	 */
	public void dealTBSData(FileResultDto fileResultDto, String sorgcode,
			String susercode, Integer iscollect) throws ITFEBizException {
		String currentDate = TimeFacade.getCurrentStringTime(); // 当前系统的时间
		FileObjDto fileobj = PublicSearchFacade
				.getFileObjByFileName(fileResultDto.getSfilename());
		String filetime = fileobj.getSdate(); // 文件日期
		String ctrimflag = fileobj.getCtrimflag(); // 调整期标志

		String dirsep = File.separator; // 取得系统分割符
		String absolutePath = ITFECommonConstant.FILE_ROOT_PATH + currentDate
				+ dirsep + sorgcode + dirsep + fileResultDto.getSfilename();
		String absoluteSql = absolutePath + ".sql";

		SQLBatchRetriever batchRetriever = null; // 批量查询要用到

		// 第一步校验凭证编号是否重复
		List<String> voulist = new ArrayList<String>(); // 初始化凭证编号LIST
		String mainInfo = fileResultDto.getSmaininfo();
		String[] lineInfo = mainInfo.split("\r\n");
		for (int i = 0; i < lineInfo.length; i++) {
			String[] arr = lineInfo[i].split(StateConstant.INCOME_SPLIT);
			if (arr[4] != null && !"".equals(arr[4])) {
				voulist.add(arr[3] + "," + arr[4]);
			}
		}
		if (!StateConstant.SPECIAL_AREA_GUANGDONG.equals(fileResultDto
				.getArea())) {
			// 非汇总方式校验 add by zhuguozhen 20120509
			if (null != iscollect && iscollect != 0) {
				checkIncomeVouRepeat(sorgcode, voulist, fileResultDto
						.getSfilename());
			}
		}

		try {
			// 第二步 存储文件
			FileUtil.getInstance().writeFile(absolutePath,
					fileResultDto.getSmaininfo());

			// 第三步 调用SHELL将文件IMPORT进数据库 临时表TV_INFILE_TMP (先删除然后插入)
			String importcontent = PublicSearchFacade.getSqlConnectByProp()
					+ "delete from TV_INFILE_TMP where S_ORGCODE = '"
					+ sorgcode
					+ "' and S_FILENAME = '"
					+ fileResultDto.getSfilename()
					+ "';\n"
					+ "import from "
					+ absolutePath
					+ " of del modified by compound=100 insert into TV_INFILE_TMP;";
			FileUtil.getInstance().writeFile(absoluteSql, importcontent);
			String results = null;
			try {
				byte[] bytes = null;
				// if(!isWin()){
				// String command =
				// "su - db2inst1 -c \"/home/db2inst1/sqllib/bin/db2 -tvf ";
				// command = command + absoluteSql + "\"";
				// bytes = CallShellUtil.callShellWithRes(command);
				// }else{
				// bytes = DB2CallShell.dbCallShellWithRes(absoluteSql);
				// }
				bytes = DB2CallShell.dbCallShellWithRes(absoluteSql);
				if (bytes.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
					results = new String(bytes, 0,
							MsgConstant.MAX_CALLSHELL_RS * 1024);
				} else {
					results = new String(bytes);
				}
				logger.debug("调用SHELL结果:" + results);

				bytes = null;
			} catch (Exception e) {
				logger.error("调用SHELL:数据导入的时候出现数据库异常!", e);
				throw new ITFEBizException("调用SHELL:数据导入的时候出现数据库异常!", e);
			}

			// 第四步 判断调用SHELL的处理结果,保证全部都成功.可能出现的错误:类型转化错误,数据被截断
			VerifyCallShellRs.verifyShellForIncomeRs(results);

			// 第五步 参数的校验 1 征收机关代码 2 辅助标志
			if (!ITFECommonConstant.ISCONVERT.equals("0")) {
				VerifyParamTrans.verifyTBSIncomeTrans(sorgcode, fileResultDto
						.getSfilename());
			} else {
				VerifyParamTrans.SetTCBSIncomeTrans(sorgcode, fileResultDto
						.getSfilename());
			}
			VerifyParamTrans.verifyBdgSbt(MsgConstant.MSG_SOURCE_TBS, sorgcode,
					fileResultDto.getSfilename(), null);
			// 国库级次与预算级次的校验
			VerifyParamTrans.verifyTreasuryLevel(sorgcode, fileResultDto
					.getSfilename(), ITFECommonConstant.IFVERIFYTRELEVEL);

			// 第六步 按照文件名称,机构代码,国库代码和征收机关代码分组(注意要去掉代征业务)
			List<FileObjDto> taxlist = new ArrayList<FileObjDto>();

			// String selectSQL =
			// "select S_RECVTRECODE,S_TAXORGCODE from TV_INFILE_TMP where S_RECVTRECODE = S_DESCTRECODE "
			// +
			// "and S_FILENAME = ? and S_ORGCODE = ? group by S_RECVTRECODE,S_TAXORGCODE";
			/**
			 * 增加对收款国库和目的国库不一致的情况校验，存在则抛出异常 20130609
			 */
			String tmpSql = "select count(*) from TV_INFILE_TMP "
					+ "	where "
					+ " (S_DESCTRECODE IS NOT NULL) AND (S_RECVTRECODE <> S_DESCTRECODE) "
					+ " AND S_FILENAME = ? and S_ORGCODE = ? ";
			SQLExecutor sqlExec_ = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();

			sqlExec_.addParam(fileResultDto.getSfilename());
			sqlExec_.addParam(sorgcode);
			SQLResults taxorgRs_ = sqlExec_.runQueryCloseCon(tmpSql);
			if (taxorgRs_.getInt(0, 0) > 0) {
				logger.error("收入文件[" + fileResultDto.getSfilename()
						+ "]中存在收款国库与目的国库不一致记录!");
				throw new ITFEBizException("收入文件["
						+ fileResultDto.getSfilename() + "]中存在收款国库与目的国库不一致记录!");
			}

			/**
			 * 修改为如果目的国库为空则不校验和收款国库相等 修改日期：20120906
			 */
			String selectSQL = "select S_RECVTRECODE,S_TAXORGCODE from TV_INFILE_TMP "
					+ "	where "
					+ " ((S_DESCTRECODE IS NOT NULL AND S_RECVTRECODE = S_DESCTRECODE) OR (S_DESCTRECODE IS NULL)) and S_FILENAME = ? and S_ORGCODE = ? "
					+ " group by S_RECVTRECODE,S_TAXORGCODE";
			SQLExecutor sqlExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();

			sqlExec.addParam(fileResultDto.getSfilename());
			sqlExec.addParam(sorgcode);
			SQLResults taxorgRs = sqlExec.runQueryCloseCon(selectSQL);
			int count = taxorgRs.getRowCount();
			for (int i = 0; i < count; i++) {
				FileObjDto tmpdto = new FileObjDto();
				tmpdto.setStrecode(taxorgRs.getString(i, 0));
				tmpdto.setStaxorgcode(taxorgRs.getString(i, 1));

				taxlist.add(tmpdto);
			}

			if (count == 0) {
				logger.error("没有找到符合条件的记录(可能是收款国库与目的国库不一致)!");
				throw new ITFEBizException("没有找到符合条件的记录(可能是收款国库与目的国库不一致)!");
			}

			// 增加一步 如果是异常处理，需要校验文件金额匹配
			if (fileResultDto.getIsError()) {
				String amtsql = "select sum(N_MONEY) as N_MONEY from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? ";
				SQLExecutor amtExec = null;
				BigDecimal zero = new BigDecimal("0.00");
				try {
					amtExec = DatabaseFacade.getDb().getSqlExecutorFactory()
							.getSQLExecutor();
					amtExec.addParam(sorgcode);
					amtExec.addParam(fileResultDto.getSfilename());
					SQLResults amtRs = amtExec.runQueryCloseCon(amtsql);
					BigDecimal famt = amtRs.getBigDecimal(0, 0);
					if (null == famt || zero.compareTo(famt) == 0
							|| famt.compareTo(fileResultDto.getFsumamt()) != 0) {
						logger.error("异常处理中文件总金额[" + famt + "]与录入金额不相等！");
						throw new ITFEBizException("异常处理中文件总金额[" + famt
								+ "]与录入金额不相等！");
					}
				} catch (JAFDatabaseException e) {
					logger.error("查询文件汇总金额时出现异常!", e);
					throw new ITFEBizException("查询文件汇总金额时出现异常!", e);
				} finally {
					if (null != amtExec) {
						amtExec.closeConnection();
					}
				}
			}

			// 第七步 根据分组条件去组包.

			List<String> packList = new ArrayList<String>();
			String tmpfilesql = null;
			String tmpDetailsql = null;

			// 汇总时去掉按单位代码汇总，单位代码在7211报文中没有使用该字段，可以提高汇总力度
			if (null == iscollect || iscollect == 0) {
				// 是汇总处理
				// tmpfilesql =
				// "select S_TBS_TAXORGCODE,sum(N_MONEY) as N_MONEY,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_TBS_ASSITSIGN,S_UNITCODE,S_OPENACCBANKCODE,S_PAYBOOKKIND "
				// +
				// " from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? and S_RECVTRECODE =? and S_TAXORGCODE = ? "
				// +
				// " group by S_TBS_TAXORGCODE,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_TBS_ASSITSIGN,S_UNITCODE,S_OPENACCBANKCODE,S_PAYBOOKKIND";
				tmpfilesql = "select S_TBS_TAXORGCODE,sum(N_MONEY) as N_MONEY,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_TBS_ASSITSIGN,S_OPENACCBANKCODE,S_PAYBOOKKIND "
						+ " from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? and S_RECVTRECODE =? and S_TAXORGCODE = ? "
						+ " group by S_TBS_TAXORGCODE,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_TBS_ASSITSIGN,S_OPENACCBANKCODE,S_PAYBOOKKIND";

				// 明细信息
				tmpDetailsql = "select N_MONEY,S_ASSITSIGN,S_TBS_ASSITSIGN,S_DEALNO,S_TAXTICKETNO,S_DESCTRECODE "
						+ " from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? and S_RECVTRECODE =? and S_TAXORGCODE = ? "
						+ " and S_TBS_TAXORGCODE = ? and S_BUDGETTYPE = ? and S_BUDGETSUBCODE = ? and S_BUDGETLEVELCODE = ? ";
			} else {
				// 按照明细处理
				tmpfilesql = "select S_TBS_TAXORGCODE,N_MONEY,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_TBS_ASSITSIGN,S_OPENACCBANKCODE,S_PAYBOOKKIND,S_DEALNO,S_TAXTICKETNO,S_DESCTRECODE,S_UNITCODE "
						+ " from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? and S_RECVTRECODE =? and S_TAXORGCODE = ? ";
			}
			// //
			List<TvFilepackagerefDto> packdtos = new ArrayList<TvFilepackagerefDto>();
			batchRetriever = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLBatchRetriever();
			SQLExecutor sqlDetail = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			for (int i = 0; i < count; i++) {
				FileObjDto tmpdto = taxlist.get(i);
				batchRetriever.clearParams();
				batchRetriever.addParam(sorgcode);
				batchRetriever.addParam(fileResultDto.getSfilename());
				batchRetriever.addParam(tmpdto.getStrecode()); // 国库代码
				batchRetriever.addParam(tmpdto.getStaxorgcode()); // 征收机关代码
				batchRetriever.setMaxRows(MsgConstant.TIPS_MAX_OF_PACK); // TIPS每包最最大笔数

				batchRetriever.runQuery(tmpfilesql);
				while (batchRetriever.hasMore()) {
					String seqno = SequenceGenerator
							.getNextByDb2(SequenceName.TRA_ID_SEQUENCE_KEY);
					String packno = SequenceGenerator.changePackNo(seqno);
					String tmpPackNo = packno + "000";

					SQLResults result = batchRetriever.RetrieveNextBatch();

					int icount = result.getRowCount();
					if (icount != 0) {
						TvInfileDto[] dtos = new TvInfileDto[icount];
						List<TvInfileDetailDto> listDetail = new ArrayList<TvInfileDetailDto>();
						BigDecimal sumamt = new BigDecimal("0.00");
						for (int j = 0; j < icount; j++) {
							int k = 0;
							TvInfileDetailDto mainDto = new TvInfileDetailDto();
							dtos[j] = new TvInfileDto();

							String stbstaxorgcode = result.getString(j, k++);
							dtos[j].setStbstaxorgcode(stbstaxorgcode); // TBS征收机关代码
							BigDecimal nmoney = result.getBigDecimal(j, k++);
							dtos[j].setNmoney(nmoney); // 交易金额
							String sbudgettype = result.getString(j, k++);
							dtos[j].setSbudgettype(sbudgettype); // 预算种类
							String sbudgetsubcode = result.getString(j, k++);
							dtos[j].setSbudgetsubcode(sbudgetsubcode); // 预算科目代码
							String sbudgetlevelcode = result.getString(j, k++);
							dtos[j].setSbudgetlevelcode(sbudgetlevelcode); // 预算级次
							String sassitsign = result.getString(j, k++);
							String stbsassitsig = result.getString(j, k++);
							if (ITFECommonConstant.ISCONVERT.equals("0")) {
								dtos[j].setSassitsign(stbsassitsig); // 辅助标志
							} else {
								dtos[j].setSassitsign(sassitsign); // 辅助标志
							}
							dtos[j].setStbsassitsign(stbsassitsig); // TBS辅助标志
							// String sunitcode = result.getString(j, k++);
							// dtos[j].setSunitcode(sunitcode); // 单位代码
							dtos[j].setSunitcode(null); // 单位代码
							String sopenaccbankcode = result.getString(j, k++);
							dtos[j].setSopenaccbankcode(sopenaccbankcode); // 开户银行
							String spaybookkind = result.getString(j, k++);
							dtos[j].setSpaybookkind(spaybookkind); // 缴款书种类

							dtos[j].setSorgcode(sorgcode); // 机构代码
							// dtos[j].setScommitdate(filetime); // 委托日期
							dtos[j].setScommitdate(currentDate); // 委托日期
							dtos[j].setSaccdate(currentDate); // 账务日期
							dtos[j].setSpackageno(tmpPackNo); // 包流水号
							if (ITFECommonConstant.ISCONVERT.equals("0")) {
								dtos[j].setStaxorgcode(stbstaxorgcode); // 征收机关代码
							} else {
								dtos[j].setStaxorgcode(tmpdto.getStaxorgcode()); // 征收机关代码
							}
							dtos[j]
									.setSfilename((fileResultDto.getSfilename())); // 文件名称
							dtos[j].setSrecvtrecode(tmpdto.getStrecode()); // 收款国库代码
							String sdealno = SequenceGenerator.changeTraSrlNo(
									packno, j);
							dtos[j].setSdealno(sdealno); // 交易流水号
							dtos[j].setStrasrlno(fileResultDto.getStrasrlno()); // 资金收纳流水号
							String staxticketno = dtos[j].getSdealno();
							dtos[j].setStaxticketno(staxticketno); // 税票号码
							dtos[j].setSgenticketdate(filetime); // 开票日期
							dtos[j].setStrimflag(ctrimflag); // 调整 期标志
							dtos[j]
									.setSstatus(DealCodeConstants.DEALCODE_ITFE_NO_SEND); // 交易状态未发送
							dtos[j].setSusercode(susercode);

							// 明细信息
							// mainDto.setSunitcode(sunitcode); // 单位代码
							mainDto.setSunitcode(null); // 单位代码
							mainDto.setSrecvtrecode(tmpdto.getStrecode()); // 收款国库代码
							mainDto.setStbstaxorgcode(stbstaxorgcode); // TBS征收机关代码
							mainDto.setSpaybookkind(spaybookkind); // 缴款书种类
							mainDto.setSopenaccbankcode(sopenaccbankcode); // 开户银行
							mainDto.setSbudgetlevelcode(sbudgetlevelcode); // 预算级次
							mainDto.setSbudgettype(sbudgettype); // 预算种类
							mainDto.setSbudgetsubcode(sbudgetsubcode); // 预算科目代码
							mainDto.setStbsassitsign(stbsassitsig); // TBS辅助标志
							mainDto.setStrasrlno(fileResultDto.getStrasrlno()); // 资金收纳流水号
							mainDto
									.setSfilename((fileResultDto.getSfilename())); // 文件名称
							mainDto.setSorgcode(sorgcode); // 机构代码
							mainDto.setSdealno(sdealno); // 交易流水号
							mainDto.setSpackageno(tmpPackNo); // 包流水号
							mainDto.setStaxorgcode(tmpdto.getStaxorgcode()); // 征收机关代码
							mainDto.setSsumtaxticketno(staxticketno); // 汇总凭证编号
							mainDto.setScommitdate(currentDate); // 账务日期
							mainDto
									.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING); // 明细状态默认为
							// 处理中

							// 汇总方式
							if (null == iscollect || iscollect == 0) {
								// SQLExecutor sqlDetail =
								// DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();

								sqlDetail.addParam(sorgcode);
								sqlDetail
										.addParam(fileResultDto.getSfilename());
								sqlDetail.addParam(tmpdto.getStrecode());
								sqlDetail.addParam(tmpdto.getStaxorgcode());
								sqlDetail.addParam(stbstaxorgcode);
								sqlDetail.addParam(sbudgettype);
								sqlDetail.addParam(sbudgetsubcode);
								sqlDetail.addParam(sbudgetlevelcode);
								String detailSql = tmpDetailsql;
								if (spaybookkind != null
										&& spaybookkind.length() > 0) {
									detailSql = detailSql
											+ " and S_PAYBOOKKIND = ?  ";
									sqlDetail.addParam(spaybookkind);
								} else {
									detailSql = detailSql
											+ " and (S_PAYBOOKKIND is null or S_PAYBOOKKIND ='') ";
								}

								if (sopenaccbankcode != null
										&& sopenaccbankcode.length() > 0) {
									detailSql = detailSql
											+ " and S_OPENACCBANKCODE = ? ";
									sqlDetail.addParam(sopenaccbankcode);
								} else {
									detailSql = detailSql
											+ " and (S_OPENACCBANKCODE is null or S_OPENACCBANKCODE ='') ";
								}
								// if(sunitcode != null && sunitcode.length() >
								// 0){
								// detailSql = detailSql +
								// " and S_UNITCODE = ? ";
								// sqlDetail.addParam(sunitcode);
								// }else{
								// detailSql = detailSql +
								// " and (S_UNITCODE is null or S_UNITCODE ='') ";
								// }
								if (sassitsign != null
										&& sassitsign.length() > 0) {
									detailSql = detailSql
											+ " and S_ASSITSIGN = ? ";
									sqlDetail.addParam(sassitsign);
								} else {
									detailSql = detailSql
											+ " and (S_ASSITSIGN is null  or S_ASSITSIGN ='') ";
								}
								if (stbsassitsig != null
										&& stbsassitsig.length() > 0) {
									detailSql = detailSql
											+ " and S_TBS_ASSITSIGN = ? ";
									sqlDetail.addParam(stbsassitsig);
								} else {
									detailSql = detailSql
											+ " and (S_TBS_ASSITSIGN is null or S_TBS_ASSITSIGN ='') ";
								}
								SQLResults detailRs = sqlDetail
										.runQuery(detailSql);

								int detailcount = detailRs.getRowCount();
								for (int n = 0; n < detailcount; n++) {
									TvInfileDetailDto detailDto = new TvInfileDetailDto();
									detailDto = (TvInfileDetailDto) mainDto
											.clone();
									detailDto.setInum(Integer.parseInt(detailRs
											.getString(n, 3))); // 顺序号
									detailDto.setNmoney(detailRs.getBigDecimal(
											n, 0)); // 交易金额
									detailDto.setStaxticketno(detailRs
											.getString(n, 4)); // 凭证编号
									detailDto.setSdesctrecode(detailRs
											.getString(n, 5)); // 目的国库

									listDetail.add(detailDto);
								}
							} else {
								mainDto.setNmoney(nmoney); // 交易金额
								int iNum = Integer.parseInt(result.getString(j,
										k++));
								mainDto.setInum(iNum); // 顺序号
								String taxticketno = result.getString(j, k++);
								if (null == taxticketno
										|| taxticketno.trim().length() == 0) {
									taxticketno = mainDto.getSsumtaxticketno(); // 凭证编号
								}
								mainDto.setStaxticketno(taxticketno); // 凭证编号
								String desctrecode = result.getString(j, k++);
								mainDto.setSdesctrecode(desctrecode);
								String sunitcode = result.getString(j, k++); // 单位代码
								listDetail.add(mainDto);

								dtos[j].setStaxticketno(taxticketno); // 凭证编号
								dtos[j].setStaxpaycode(sunitcode); // 纳税人编码
							}

							sumamt = sumamt.add(dtos[j].getNmoney());
							// sqlDetail.clearParams();
						}

						// 批量保存数据
						DatabaseFacade.getDb().create(dtos);
						packList.add(tmpPackNo);

						// 明细数据保存
						TvInfileDetailDto[] detail = new TvInfileDetailDto[listDetail
								.size()];
						detail = listDetail.toArray(detail);
						DatabaseFacade.getDb().create(detail);
						listDetail = new ArrayList<TvInfileDetailDto>();

						// 保存组头信息
						TvFilepackagerefDto packdto = new TvFilepackagerefDto();
						packdto.setSorgcode(sorgcode); // 机构代码
						packdto.setStrecode(tmpdto.getStrecode()); // 国库代码
						packdto.setSfilename(fileResultDto.getSfilename()); // 导入文件名
						packdto.setStaxorgcode(tmpdto.getStaxorgcode()); // 机关代码
						// packdto.setScommitdate(filetime); // 委托日期
						packdto.setScommitdate(currentDate); // 委托日期
						packdto.setSaccdate(currentDate); // 账务日期
						packdto.setSpackageno(tmpPackNo); // 包流水号
						packdto
								.setSoperationtypecode(BizTypeConstant.BIZ_TYPE_INCOME); // 业务类型
						packdto.setIcount(icount); // 总笔数
						packdto.setNmoney(sumamt); // 总金额

						if (fileResultDto.getIsError()) {
							// 如果是异常处理，直接发送报文，否则需要确认提交
							packdto
									.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING); // 交易状态未待处理
						} else {
							packdto
									.setSretcode(DealCodeConstants.DEALCODE_ITFE_NO_SEND); // 交易状态未未发送
						}

						packdto.setSusercode(susercode); // 操作员代码
						packdto.setSdemo(fileResultDto.getStrasrlno()); // 资金收纳流水号
						packdto.setImodicount(1);

						packdtos.add(packdto);
					}
				}
			}
			try {
				TvFilepackagerefDto[] heads = new TvFilepackagerefDto[packdtos
						.size()];
				heads = packdtos.toArray(heads);
				DatabaseFacade.getDb().create(heads);
			} catch (JAFDatabaseException e) {
				logger.error("保存报文头的时候出现异常！", e);
				throw new ITFEBizException("保存报文头的时候出现异常！", e);
			}

			// 关闭连接
			sqlDetail.closeConnection();
			batchRetriever.closeConnection();

			// if (fileResultDto.getIsError()) {
			// // 如果是异常处理，直接发送报文，否则需要确认提交
			// // 按照条件发送报文
			// fileResultDto.setPacknos(packList);
			// for (int i = 0; i < packList.size(); i++) {
			// this.sendMsg(fileResultDto.getSfilename(), sorgcode,
			// packList.get(i), MsgConstant.MSG_NO_7211, filetime, null, false);
			// }
			// }

			// 删除临时表记录
			TvInfileTmpDto tmpdto = new TvInfileTmpDto();
			tmpdto.setSorgcode(sorgcode);
			tmpdto.setSfilename(fileResultDto.getSfilename());
			CommonFacade.getODB().deleteRsByDto(tmpdto);
		} catch (FileOperateException e) {
			logger.error("文件上载的时候出现异常!", e);
			throw new ITFEBizException("文件上载的时候出现异常!", e);
		} catch (JAFDatabaseException e) {
			logger.error("数据搬运的时候出现数据库异常!", e);
			throw new ITFEBizException("数据搬运的时候出现数据库异常!", e);
		} catch (SequenceException e) {
			logger.error("生成流水号时出现异常!", e);
			throw new ITFEBizException("生成流水号时出现异常!", e);
		} catch (ValidateException e) {
			logger.error("删除临时表记录的时候出现数据库异常!", e);
			throw new ITFEBizException("删除临时表记录的时候出现数据库异常!", e);
		} finally {
			if (null != batchRetriever) {
				batchRetriever.closeConnection();
			}

			TvInfileTmpDto tmpdto = new TvInfileTmpDto();
			tmpdto.setSorgcode(sorgcode);
			tmpdto.setSfilename(fileResultDto.getSfilename());
			try {
				CommonFacade.getODB().deleteRsByDto(tmpdto);
			} catch (ValidateException e) {
				logger.error("删除临时表记录的时候出现数据库异常!", e);
			} catch (JAFDatabaseException e) {
				logger.error("删除临时表记录的时候出现数据库异常!", e);
			}
		}
	}

	
	/**
	 * 处理TBS接口数据
	 * 
	 * @param FileResultDto
	 *            fileResultDto 解析文件接口对象
	 * @param String
	 *            sorgcode 机构代码
	 * @param String
	 *            susercode 用户代码
	 * @throws ITFEBizException
	 */
	public void dealTBSDataForZJ(FileResultDto fileResultDto, String sorgcode, String susercode , Integer iscollect) throws ITFEBizException {
		String currentDate = TimeFacade.getCurrentStringTime(); // 当前系统的时间
		FileObjDto fileobj = PublicSearchFacade.getFileObjByFileName(fileResultDto.getSfilename());
		String filetime = fileobj.getSdate(); // 文件日期
		String ctrimflag = fileobj.getCtrimflag(); // 调整期标志

		String dirsep = File.separator; // 取得系统分割符
		String absolutePath = ITFECommonConstant.FILE_ROOT_PATH + currentDate + dirsep + sorgcode + dirsep + fileResultDto.getSfilename();
		String absoluteSql = absolutePath + ".sql";

		SQLBatchRetriever batchRetriever = null; // 批量查询要用到

		try {
			// 第二步 存储文件
			String os = System.getProperties().getProperty("os.name");
			if (os.indexOf("Win") >= 0) {
				FileUtil.getInstance().writeFile(absolutePath, fileResultDto.getSmaininfo());
			} else {
				FileUtil.getInstance().writeFile(absolutePath, fileResultDto.getSmaininfo().replaceAll("\\r", ""));
			}
			
			// 第三步 调用SHELL将文件IMPORT进数据库 临时表TV_INFILE_TMP (先删除然后插入)
			String importcontent = PublicSearchFacade.getSqlConnectByProp() + "delete from TV_INFILE_TMP where S_ORGCODE = '" + sorgcode
					+ "' and S_FILENAME = '" + fileResultDto.getSfilename() + "';\n" + "import from " + absolutePath
					+ " of del modified by compound=100 insert into TV_INFILE_TMP;";
			FileUtil.getInstance().writeFile(absoluteSql, importcontent);
			String results = null;
			try {
				byte[] bytes = DB2CallShell.dbCallShellWithRes(absoluteSql);
				if (bytes.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
					results = new String(bytes, 0, MsgConstant.MAX_CALLSHELL_RS * 1024);
				} else {
					results = new String(bytes);
				}

				bytes = null;
			} catch (Exception e) {
				logger.error("调用SHELL:数据导入的时候出现数据库异常!", e);
				throw new ITFEBizException("调用SHELL:数据导入的时候出现数据库异常!", e);
			}

			// 第四步 判断调用SHELL的处理结果,保证全部都成功.可能出现的错误:类型转化错误,数据被截断
			VerifyCallShellRs.verifyShellForIncomeRs(results);

			// 第五步 参数的校验 1 征收机关代码 2 辅助标志
			VerifyParamTrans.verifyTBSIncomeTransForZJ(sorgcode, fileResultDto.getSfilename());
			VerifyParamTrans.verifyBdgSbt(MsgConstant.MSG_SOURCE_TBS, sorgcode, fileResultDto.getSfilename(), null);

			// 第六步 按照文件名称,机构代码,国库代码和征收机关代码分组(注意要去掉代征业务)
			List<FileObjDto> taxlist = new ArrayList<FileObjDto>();
			String selectSQL = "select S_RECVTRECODE,S_TAXORGCODE from TV_INFILE_TMP where S_RECVTRECODE = S_DESCTRECODE "
					+ "and S_FILENAME = ? and S_ORGCODE = ? group by S_RECVTRECODE,S_TAXORGCODE";
			SQLExecutor sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.addParam(fileResultDto.getSfilename());
			sqlExec.addParam(sorgcode);
			SQLResults taxorgRs = sqlExec.runQueryCloseCon(selectSQL);
			int count = taxorgRs.getRowCount();
			for (int i = 0; i < count; i++) {
				FileObjDto tmpdto = new FileObjDto();
				tmpdto.setStrecode(taxorgRs.getString(i, 0));
				tmpdto.setStaxorgcode(taxorgRs.getString(i, 1));

				taxlist.add(tmpdto);
			}

			if (count == 0) {
				logger.error("没有找到符合条件的记录(可能是收款国库与目的国库不一致)!");
				throw new ITFEBizException("没有找到符合条件的记录(可能是收款国库与目的国库不一致)!");
			}

			// 增加一步 如果是异常处理，需要校验文件金额匹配
			if (fileResultDto.getIsError()) {
				String amtsql = "select sum(N_MONEY) as N_MONEY from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? ";
				SQLExecutor amtExec = null;
				BigDecimal zero = new BigDecimal("0.00");
				try {
					amtExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
					amtExec.addParam(sorgcode);
					amtExec.addParam(fileResultDto.getSfilename());
					SQLResults amtRs = amtExec.runQueryCloseCon(amtsql);
					BigDecimal famt = amtRs.getBigDecimal(0, 0);
					if (null == famt || zero.compareTo(famt) == 0 || famt.compareTo(fileResultDto.getFsumamt()) != 0) {
						logger.error("异常处理中文件总金额[" + famt + "]与录入金额不相等！");
						throw new ITFEBizException("异常处理中文件总金额[" + famt + "]与录入金额不相等！");
					}
				} catch (JAFDatabaseException e) {
					logger.error("查询文件汇总金额时出现异常!", e);
					throw new ITFEBizException("查询文件汇总金额时出现异常!", e);
				} finally {
					if (null != amtExec) {
						amtExec.closeConnection();
					}
				}
			}

			// 第七步 根据分组条件去组包.

			List<String> packList = new ArrayList<String>();
			String tmpfilesql = null;

			if (null == iscollect || iscollect == 0) {
				// 是汇总处理
				tmpfilesql = "select S_TBS_TAXORGCODE,sum(N_MONEY) as N_MONEY,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_TBS_ASSITSIGN,S_UNITCODE,S_OPENACCBANKCODE,S_PAYBOOKKIND "
						+ " from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? and S_RECVTRECODE =? and S_TAXORGCODE = ? "
						+ " group by S_TBS_TAXORGCODE,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_TBS_ASSITSIGN,S_UNITCODE,S_OPENACCBANKCODE,S_PAYBOOKKIND";
			} else {
				// 按照明细处理
				tmpfilesql = "select S_TBS_TAXORGCODE,N_MONEY,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_TBS_ASSITSIGN,S_UNITCODE,S_OPENACCBANKCODE,S_PAYBOOKKIND"
						+ " from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? and S_RECVTRECODE =? and S_TAXORGCODE = ? ";
			}

			List<TvFilepackagerefDto> packdtos = new ArrayList<TvFilepackagerefDto>();
			batchRetriever = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLBatchRetriever();
			for (int i = 0; i < count; i++) {
				FileObjDto tmpdto = taxlist.get(i);
				batchRetriever.clearParams();
				batchRetriever.addParam(sorgcode);
				batchRetriever.addParam(fileResultDto.getSfilename());
				batchRetriever.addParam(tmpdto.getStrecode()); // 国库代码
				batchRetriever.addParam(tmpdto.getStaxorgcode()); // 征收机关代码
				batchRetriever.setMaxRows(MsgConstant.TIPS_MAX_OF_PACK); // TIPS每包最最大笔数

				batchRetriever.runQuery(tmpfilesql);
				while (batchRetriever.hasMore()) {
					String seqno = SequenceGenerator.getNextByDb2(SequenceName.TRA_ID_SEQUENCE_KEY);
					String packno = SequenceGenerator.changePackNo(seqno);
					String tmpPackNo = packno + "000";

					SQLResults result = batchRetriever.RetrieveNextBatch();

					int icount = result.getRowCount();
					if (icount != 0) {
						TvInfileDto[] dtos = new TvInfileDto[icount];
						BigDecimal sumamt = new BigDecimal("0.00");
						for (int j = 0; j < icount; j++) {
							int k = 0;
							dtos[j] = new TvInfileDto();

							dtos[j].setStbstaxorgcode((result.getString(j, k++))); // TBS征收机关代码
							dtos[j].setNmoney((result.getBigDecimal(j, k++))); // 交易金额
							dtos[j].setSbudgettype((result.getString(j, k++))); // 预算种类
							dtos[j].setSbudgetsubcode(result.getString(j, k++)); // 预算科目代码
							dtos[j].setSbudgetlevelcode(result.getString(j, k++)); // 预算级次
							dtos[j].setSassitsign(result.getString(j, k++)); // 辅助标志
							dtos[j].setStbsassitsign(result.getString(j, k++)); // TBS辅助标志
							dtos[j].setSunitcode(result.getString(j, k++)); // 单位代码
							dtos[j].setSopenaccbankcode(result.getString(j, k++)); // 开户银行
							dtos[j].setSpaybookkind(result.getString(j, k++)); // 缴款书种类

							dtos[j].setSorgcode(sorgcode); // 机构代码
							dtos[j].setScommitdate(filetime); // 委托日期
							dtos[j].setSaccdate(currentDate); // 账务日期
							dtos[j].setSpackageno(tmpPackNo); // 包流水号
							dtos[j].setStaxorgcode(tmpdto.getStaxorgcode()); // 征收机关代码
							dtos[j].setSfilename((fileResultDto.getSfilename())); // 文件名称
							dtos[j].setSrecvtrecode(tmpdto.getStrecode()); // 收款国库代码
							dtos[j].setSdealno(SequenceGenerator.changeTraSrlNo(packno, j)); // 交易流水号
							dtos[j].setStrasrlno(fileResultDto.getStrasrlno()); // 资金收纳流水号
							dtos[j].setStaxticketno(dtos[j].getSdealno()); // 税票号码
							dtos[j].setSgenticketdate(filetime); // 开票日期
							dtos[j].setStrimflag(ctrimflag); // 调整 期标志
							dtos[j].setSstatus(DealCodeConstants.DEALCODE_ITFE_NO_SEND); // 交易状态未发送
							dtos[j].setSusercode(susercode);

							sumamt = sumamt.add(dtos[j].getNmoney());
						}

						// 批量保存数据
						DatabaseFacade.getDb().create(dtos);
						packList.add(tmpPackNo);

						// 保存组头信息
						TvFilepackagerefDto packdto = new TvFilepackagerefDto();
						packdto.setSorgcode(sorgcode); // 机构代码
						packdto.setStrecode(tmpdto.getStrecode()); // 国库代码
						packdto.setSfilename(fileResultDto.getSfilename()); // 导入文件名
						packdto.setStaxorgcode(tmpdto.getStaxorgcode()); // 机关代码
						packdto.setScommitdate(filetime); // 委托日期
						packdto.setSaccdate(currentDate); // 账务日期
						packdto.setSpackageno(tmpPackNo); // 包流水号
						packdto.setSoperationtypecode(BizTypeConstant.BIZ_TYPE_INCOME); // 业务类型
						packdto.setIcount(icount); // 总笔数
						packdto.setNmoney(sumamt); // 总金额	
						
						if (fileResultDto.getIsError()) {
							// 如果是异常处理，直接发送报文，否则需要确认提交
							packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING); // 交易状态未待处理
						}else{
							packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_NO_SEND); // 交易状态未未发送
						}

						packdto.setSusercode(susercode); // 操作员代码
						packdto.setSdemo(fileResultDto.getStrasrlno()); // 资金收纳流水号
						packdto.setImodicount(1);

						packdtos.add(packdto);
					}
				}
			}
			try {
				TvFilepackagerefDto[] heads = new TvFilepackagerefDto[packdtos.size()];
				heads = packdtos.toArray(heads);
				DatabaseFacade.getDb().create(heads);
			} catch (JAFDatabaseException e) {
				logger.error("保存报文头的时候出现异常！", e);
				throw new ITFEBizException("保存报文头的时候出现异常！", e);
			}

			// 关闭连接
			batchRetriever.closeConnection();

//			if (fileResultDto.getIsError()) {
//				// 如果是异常处理，直接发送报文，否则需要确认提交
//				// 按照条件发送报文
//				fileResultDto.setPacknos(packList);
//				for (int i = 0; i < packList.size(); i++) {
//					this.sendMsg(fileResultDto.getSfilename(), sorgcode, packList.get(i), MsgConstant.MSG_NO_7211, filetime, null, false);
//				}
//			}

			// 删除临时表记录
			TvInfileTmpDto tmpdto = new TvInfileTmpDto();
			tmpdto.setSorgcode(sorgcode);
			tmpdto.setSfilename(fileResultDto.getSfilename());
			CommonFacade.getODB().deleteRsByDto(tmpdto);
		} catch (FileOperateException e) {
			logger.error("文件上载的时候出现异常!", e);
			throw new ITFEBizException("文件上载的时候出现异常!", e);
		} catch (JAFDatabaseException e) {
			logger.error("数据搬运的时候出现数据库异常!", e);
			throw new ITFEBizException("数据搬运的时候出现数据库异常!", e);
		} catch (SequenceException e) {
			logger.error("生成流水号时出现异常!", e);
			throw new ITFEBizException("生成流水号时出现异常!", e);
		} catch (ValidateException e) {
			logger.error("删除临时表记录的时候出现数据库异常!", e);
			throw new ITFEBizException("删除临时表记录的时候出现数据库异常!", e);
		} finally {
			if (null != batchRetriever) {
				batchRetriever.closeConnection();
			}

			TvInfileTmpDto tmpdto = new TvInfileTmpDto();
			tmpdto.setSorgcode(sorgcode);
			tmpdto.setSfilename(fileResultDto.getSfilename());
			try {
				CommonFacade.getODB().deleteRsByDto(tmpdto);
			} catch (ValidateException e) {
				logger.error("删除临时表记录的时候出现数据库异常!", e);
			} catch (JAFDatabaseException e) {
				logger.error("删除临时表记录的时候出现数据库异常!", e);
			}
		}
	}
	
	
	
	
	/**
	 * 处理国税接口数据
	 * 
	 * @param FileResultDto
	 *            fileResultDto 解析文件接口对象
	 * @param String
	 *            sorgcode 机构代码
	 * @param String
	 *            susercode 用户代码
	 * @throws ITFEBizException
	 */
	public void dealNationData(FileResultDto fileResultDto, String sorgcode,
			String susercode, Integer iscollect) throws ITFEBizException {

		String currentDate = TimeFacade.getCurrentStringTime(); // 当前系统的时间
		SQLBatchRetriever batchRetriever = null; // 批量查询要用到

		try {
			List<TvInfileTmpCountryDto> dtolist = fileResultDto.getMainlist();
			if (null == dtolist || dtolist.size() == 0) {
				logger.error("解析国税文件出现错误，没有找到对应的数据！");
				throw new ITFEBizException("解析国税文件出现错误，没有找到对应的数据！！");
			}

			TvInfileTmpCountryDto[] countrydtos = new TvInfileTmpCountryDto[dtolist
					.size()];
			countrydtos = dtolist.toArray(countrydtos);
			DatabaseFacade.getDb().create(countrydtos);

			// 增加一步 如果是异常处理，需要校验文件金额匹配
			if (fileResultDto.getIsError()) {
				String amtsql = "select sum(N_FACTTAXAMT) as N_FACTTAXAMT from TV_INFILE_TMP_COUNTRY where S_ORGCODE = ? and S_FILENAME = ? ";
				SQLExecutor amtExec = null;
				BigDecimal zero = new BigDecimal("0.00");
				try {
					amtExec = DatabaseFacade.getDb().getSqlExecutorFactory()
							.getSQLExecutor();
					amtExec.addParam(sorgcode);
					amtExec.addParam(fileResultDto.getSfilename());
					SQLResults amtRs = amtExec.runQueryCloseCon(amtsql);
					BigDecimal famt = amtRs.getBigDecimal(0, 0);
					if (null == famt || zero.compareTo(famt) == 0
							|| famt.compareTo(fileResultDto.getFsumamt()) != 0) {
						logger.error("异常处理中文件总金额[" + famt + "]与录入金额不相等！");
						throw new ITFEBizException("异常处理中文件总金额[" + famt
								+ "]与录入金额不相等！");
					}
				} catch (JAFDatabaseException e) {
					logger.error("查询文件汇总金额时出现异常!", e);
					throw new ITFEBizException("查询文件汇总金额时出现异常!", e);
				} finally {
					if (null != amtExec) {
						amtExec.closeConnection();
					}
				}
			}

			// 1.2参数的校验.
			List<FileObjDto> taxtrereflist = VerifyParamTrans
					.verifyNationIncomeTrans(sorgcode, fileResultDto
							.getSfilename());
			VerifyParamTrans.verifyBdgSbt(MsgConstant.MSG_SOURCE_NATION,
					sorgcode, fileResultDto.getSfilename(), null);
			if("000077100005".equals(ITFECommonConstant.SRC_NODE))
				VerifyParamTrans.verifyTreasury(sorgcode, fileResultDto.getSfilename());//验证收款国库和目标国库是否一致
			// 1.3按照文件名称,机构代码,国库代码和征收机关代码分组.
			String tmpplacefilesql = "";

			if (null != iscollect && iscollect != 0) {
				// 按照明细处理
				tmpplacefilesql = "select S_TCBSTAXORGCODE,N_FACTTAXAMT,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_BILLDATE,"
						+ "S_PAYBNKNO,S_OPENACCBANKCODE,S_CORPNAME,S_PAYACCT,S_CORPCODE,S_LIMITDATE,"
						+ "S_TAXSTARTDATE,S_TAXENDDATE,S_TAXTYPENAME"
						+ " from TV_INFILE_TMP_COUNTRY where S_ORGCODE = ? and S_FILENAME = ? and S_TCBSTRECODE =? and S_TCBSTAXORGCODE = ? ";
			} else {
				// 是汇总处理
				tmpplacefilesql = "select S_TCBSTAXORGCODE,sum(N_FACTTAXAMT) as N_FACTTAXAMT,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN"
						+ " from TV_INFILE_TMP_COUNTRY where S_ORGCODE = ? and S_FILENAME = ? and S_TCBSTRECODE =? and S_TCBSTAXORGCODE = ? "
						+ " group by S_TCBSTAXORGCODE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN";
			}

			List<String> packList = new ArrayList<String>();
			List<TvFilepackagerefDto> packdtos = new ArrayList<TvFilepackagerefDto>();
			batchRetriever = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLBatchRetriever();
			for (int i = 0; i < taxtrereflist.size(); i++) {
				FileObjDto tmpdto = taxtrereflist.get(i);
				batchRetriever.clearParams();
				batchRetriever.addParam(sorgcode);
				batchRetriever.addParam(fileResultDto.getSfilename());
				batchRetriever.addParam(tmpdto.getStrecode()); // 国库代码
				batchRetriever.addParam(tmpdto.getStaxorgcode()); // 征收机关代码
				batchRetriever.setMaxRows(MsgConstant.TIPS_MAX_OF_PACK); // TIPS每包最最大笔数

				batchRetriever.runQuery(tmpplacefilesql);
				while (batchRetriever.hasMore()) {
					String seqno = SequenceGenerator
							.getNextByDb2(SequenceName.TRA_ID_SEQUENCE_KEY);
					String packno = SequenceGenerator.changePackNo(seqno);
					String tmpPackNo = packno + "000";

					SQLResults result = batchRetriever.RetrieveNextBatch();

					int icount = result.getRowCount();
					if (icount != 0) {
						TvInfileDto[] dtos = new TvInfileDto[icount];
						BigDecimal sumamt = new BigDecimal("0.00");
						for (int j = 0; j < icount; j++) {
							int k = 0;
							dtos[j] = new TvInfileDto();

							dtos[j].setStaxorgcode((result.getString(j, k++))); // TCBS征收机关代码
							dtos[j].setNmoney((result.getBigDecimal(j, k++))); // 交易金额
							dtos[j].setSbudgetsubcode(result.getString(j, k++)); // 预算科目代码
							dtos[j].setSbudgetlevelcode(result
									.getString(j, k++)); // 预算级次
							dtos[j].setSassitsign(result.getString(j, k++)); // 辅助标志

							dtos[j].setSgenticketdate(currentDate); // 开票日期
							dtos[j].setSbudgettype(MsgConstant.BDG_KIND_IN); // 预算种类
							dtos[j].setSorgcode(sorgcode); // 机构代码
							dtos[j].setSaccdate(currentDate); // 账务日期
							dtos[j].setSpackageno(tmpPackNo); // 包流水号
							dtos[j].setStaxorgcode(tmpdto.getStaxorgcode()); // 征收机关代码
							dtos[j]
									.setSfilename((fileResultDto.getSfilename())); // 文件名称
							dtos[j].setSrecvtrecode(tmpdto.getStrecode()); // 收款国库代码
							dtos[j].setSdealno(SequenceGenerator
									.changeTraSrlNo(packno, j)); // 交易流水号
							dtos[j].setStrasrlno(fileResultDto.getStrasrlno()); // 资金收纳流水号
							dtos[j].setStaxticketno(dtos[j].getSdealno()); // 税票号码
							dtos[j].setScommitdate(currentDate); // 委托日期
							dtos[j].setStrimflag(MsgConstant.TIME_FLAG_NORMAL); // 调整期为正常期
							dtos[j]
									.setSstatus(DealCodeConstants.DEALCODE_ITFE_NO_SEND); // 交易状态未发送
							dtos[j].setSusercode(susercode);
							dtos[j]
									.setSsourceflag(MsgConstant.MSG_SOURCE_NATION);// 数据来源

							// 1-国税
							if (null != iscollect && iscollect != 0) {
								// 按照明细处理
								dtos[j].setSgenticketdate(result.getString(j,
										k++)); // 开票日期
								dtos[j].setSpaybnkno(result.getString(j, k++));// 付款行号
								dtos[j].setSopenaccbankcode(result.getString(j,
										k++));// 付款开户行行号
								dtos[j]
										.setStaxpayname(result
												.getString(j, k++));// 纳税人名称
								dtos[j].setSpayacct(result.getString(j, k++));// 付款帐户
								dtos[j]
										.setStaxpaycode(result
												.getString(j, k++));// 纳税人编码
								dtos[j].setSlimitdate(result.getString(j, k++));// 限缴日期

								dtos[j].setStaxstartdate(result.getString(j,
										k++));// 所属起始日期
								dtos[j]
										.setStaxenddate(result
												.getString(j, k++));// 所属终止日期
								dtos[j].setStaxtypename(result
										.getString(j, k++));// 税种名称
								dtos[j].setStaxtypecode(dtos[j]
										.getSbudgetsubcode());// 税种代码
							}

							sumamt = sumamt.add(dtos[j].getNmoney());
						}

						// 批量保存数据
						DatabaseFacade.getDb().create(dtos);
						packList.add(tmpPackNo);

						// 保存组头信息
						TvFilepackagerefDto packdto = new TvFilepackagerefDto();
						packdto.setSorgcode(sorgcode); // 机构代码
						packdto.setStrecode(tmpdto.getStrecode()); // 国库代码
						packdto.setSfilename(fileResultDto.getSfilename()); // 导入文件名
						packdto.setStaxorgcode(tmpdto.getStaxorgcode()); // 机关代码
						packdto.setScommitdate(currentDate); // 委托日期
						packdto.setSaccdate(currentDate); // 账务日期
						packdto.setSpackageno(tmpPackNo); // 包流水号
						packdto
								.setSoperationtypecode(BizTypeConstant.BIZ_TYPE_INCOME); // 业务类型
						packdto.setIcount(icount); // 总笔数
						packdto.setNmoney(sumamt); // 总金额

						if (fileResultDto.getIsError()) {
							// 如果是异常处理，直接发送报文，否则需要确认提交
							packdto
									.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING); // 交易状态未待处理
						} else {
							packdto
									.setSretcode(DealCodeConstants.DEALCODE_ITFE_NO_SEND); // 交易状态未未发送
						}

						packdto.setSusercode(susercode); // 操作员代码
						packdto.setSdemo(fileResultDto.getStrasrlno()); // 资金收纳流水号
						packdto.setImodicount(1);

						packdtos.add(packdto);
					}
				}
			}
			try {
				TvFilepackagerefDto[] heads = new TvFilepackagerefDto[packdtos
						.size()];
				heads = packdtos.toArray(heads);
				DatabaseFacade.getDb().create(heads);
			} catch (JAFDatabaseException e) {
				logger.error("保存报文头的时候出现异常！", e);
				throw new ITFEBizException("保存报文头的时候出现异常！", e);
			}

			// 关闭连接
			batchRetriever.closeConnection();

			TvInfileTmpCountryDto tmpdto = new TvInfileTmpCountryDto();
			tmpdto.setSorgcode(sorgcode);
			tmpdto.setSfilename(fileResultDto.getSfilename());
			try {
				CommonFacade.getODB().deleteRsByDto(tmpdto);
			} catch (ValidateException e) {
				logger.error("删除临时表记录的时候出现数据库异常!", e);
			} catch (JAFDatabaseException e) {
				logger.error("删除临时表记录的时候出现数据库异常!", e);
			}
		} catch (JAFDatabaseException e) {
			logger.error("数据搬运的时候出现数据库异常!", e);
			throw new ITFEBizException("数据搬运的时候出现数据库异常!", e);
		} catch (SequenceException e) {
			logger.error("生成流水号时出现异常!", e);
			throw new ITFEBizException("生成流水号时出现异常!", e);
		} finally {
			if (null != batchRetriever) {
				batchRetriever.closeConnection();
			}

			TvInfileTmpCountryDto tmpdto = new TvInfileTmpCountryDto();
			tmpdto.setSorgcode(sorgcode);
			tmpdto.setSfilename(fileResultDto.getSfilename());
			try {
				CommonFacade.getODB().deleteRsByDto(tmpdto);
			} catch (ValidateException e) {
				logger.error("删除临时表记录的时候出现数据库异常!", e);
			} catch (JAFDatabaseException e) {
				logger.error("删除临时表记录的时候出现数据库异常!", e);
			}
		}
	}

	/**
	 * 处理地税接口数据
	 * 
	 * @param FileResultDto
	 *            fileResultDto 解析文件接口对象
	 * @param String
	 *            sorgcode 机构代码
	 * @param String
	 *            susercode 用户代码
	 * @throws ITFEBizException
	 */
	public void dealPlaceData(FileResultDto fileResultDto, String sorgcode,
			String susercode, Integer iscollect) throws ITFEBizException {

		String currentDate = TimeFacade.getCurrentStringTime(); // 当前系统的时间
		String dirsep = File.separator; // 取得系统分割符

		SQLBatchRetriever batchRetriever = null; // 批量查询要用到

		String fileflag = ""; // 操作文件批次

		try {
			fileflag = SequenceGenerator.changeTraSrlNo(18, SequenceGenerator
					.getNextByDb2(SequenceName.TRA_ID_SEQUENCE_KEY));

			// 地税接口处理方式
			// 1.1 数据导入
			String serverpath = ITFECommonConstant.FILE_ROOT_PATH + dirsep
					+ fileResultDto.getSmaininfo(); // 服务器路径
			String serverdelpath = changeFileName(serverpath, ".txt", ".del");
			String absoluteSql = serverdelpath + ".sql";
			FileUtil.getInstance().writeFile(serverdelpath, "aaaaaaaaaaaaaaaaaaaaaaa");
			try {
				// 第二步 存储文件
				String os = System.getProperties().getProperty("os.name");
				byte[] bytes;
				if (os.indexOf("Win") >= 0) {
				     bytes = CallShellUtil
					.callShellWithRes("cmd.exe /c C:\\WINDOWS\\sed\\bin\\sed.exe -e \"s | , g\" "
							+ serverpath + ">" + serverdelpath);
				} else {

					 bytes = CallShellUtil
							.callShellWithRes("sh /itfe/script/init/sed " +serverpath.replaceAll("//", "/") +" "+serverdelpath.replaceAll("//", "/"));
				}
				
				if (bytes.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
					logger.error(new String(bytes, 0,
							MsgConstant.MAX_CALLSHELL_RS * 1024));
				} else {
					logger.error(new String(bytes));
				}

				// SQLExecutor proexcutor =
				// DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				// proexcutor.addParam(fileflag);

				String tmptablename = "tv_itfe_model_" + fileflag;

				// 调用SHELL创建批次单位的临时表a
				// 调用SHELL将文件IMPORT进 临时表a
				// 调用SHELL将 临时表a内容导入地税临时表
				// 删除临时表a
				String importcontent = PublicSearchFacade.getSqlConnectByProp()
						+ "create table "
						+ tmptablename
						+ " like tv_infile_tmp_place_model in TS_BD_16K index in TS_BX_8K compress no;\n"
//						+ " like tv_infile_tmp_place_model compress no;\n"
						+ "import from " + serverdelpath
						+ " of del replace into " + tmptablename
						+ ";\n" + "insert into tv_infile_tmp_place select "
						+ "'" + fileflag + "',a.*,'','','','','' from "
						+ tmptablename + " a;\n" + "drop table " + tmptablename
						+ ";";

				// String importcontent =
				// PublicSearchFacade.getSqlConnectByProp() +
				// "call PRC_INFILE_PLACE('" + serverdelpath
				// + "' , '" + fileflag + "');";
				FileUtil.getInstance().writeFile(absoluteSql, importcontent);
				String results = null;
				try {
					byte[] bytesimport = DB2CallShell
							.dbCallShellWithRes(absoluteSql);
					if (bytesimport.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
						results = new String(bytesimport, 0,
								MsgConstant.MAX_CALLSHELL_RS * 1024);
					} else {
						results = new String(bytesimport);
					}

					bytes = null;
				} catch (Exception e) {
					logger.error("调用SHELL:数据导入的时候出现数据库异常!", e);
					throw new ITFEBizException("调用SHELL:数据导入的时候出现数据库异常!", e);
				}

				// 判断调用SHELL的处理结果,保证全部都成功.可能出现的错误:类型转化错误,数据被截断
				VerifyCallShellRs.verifyShellForIncomeRs(results);

				// 导入模板表，删除临时表
				// proexcutor.runStoredProcExecCloseCon("PRC_INFILE_PLACE_2");
			} catch (Throwable e) {
				logger.error("调用SHELL:数据导入的时候出现数据库异常!", e);
				throw new ITFEBizException("调用SHELL:数据导入的时候出现数据库异常!", e);
			}

			// 增加一步 如果是异常处理，需要校验文件金额匹配
			if (fileResultDto.getIsError()) {
				String amtsql = "select sum(N_FACTTAXAMT) as N_FACTTAXAMT from TV_INFILE_TMP_PLACE where S_OPERBATCH = ? ";
				SQLExecutor amtExec = null;
				BigDecimal zero = new BigDecimal("0.00");
				try {
					amtExec = DatabaseFacade.getDb().getSqlExecutorFactory()
							.getSQLExecutor();
					amtExec.addParam(fileflag);
					SQLResults amtRs = amtExec.runQueryCloseCon(amtsql);
					BigDecimal famt = amtRs.getBigDecimal(0, 0);
					if (null == famt || zero.compareTo(famt) == 0
							|| famt.compareTo(fileResultDto.getFsumamt()) != 0) {
						logger.error("异常处理中文件总金额[" + famt + "]与录入金额不相等！");
						throw new ITFEBizException("异常处理中文件总金额[" + famt
								+ "]与录入金额不相等！");
					}
				} catch (JAFDatabaseException e) {
					logger.error("查询文件汇总金额时出现异常!", e);
					throw new ITFEBizException("查询文件汇总金额时出现异常!", e);
				} finally {
					if (null != amtExec) {
						amtExec.closeConnection();
					}
				}
			}

			// 1.2参数的校验（征收机关代码，国库代码，辅助标志，付款开户行行号）.
			List<FileObjDto> taxtrereflist = VerifyParamTrans
					.verifyPlaceIncomeTrans(sorgcode, fileflag);
			VerifyParamTrans.verifyBdgSbt(MsgConstant.MSG_SOURCE_PLACE,
					sorgcode, fileResultDto.getSfilename(), fileflag);
			if("000077100005".equals(ITFECommonConstant.SRC_NODE))
				VerifyParamTrans.verifyTreasury(sorgcode, fileResultDto.getSfilename());//验证收款国库和目标国库是否一致
			// 1.3按照文件名称,机构代码,国库代码和征收机关代码分组.
			String tmpplacefilesql = "";

			if (null != iscollect && iscollect != 0) {
				// 按照明细处理
				tmpplacefilesql = "select S_TCBSTAXORGCODE,N_FACTTAXAMT,S_BUDGETSUBJECTCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_BILLDATE,"
						+ "S_PAYBNKNO,S_PAYOPBNKNO,S_CORPNAME,S_PAYACCT,S_CORPCODE,S_LIMITDATE,S_TAXTYPECODE,S_TAXTYPENAME,"
						+ "S_TAXSTARTDATE,S_TAXENDDATE,S_TAXSUBJECTNAME,I_TAXNUMBER,N_TAXAMT,N_TAXRATE,N_FACTTAXAMT,N_DISCOUNTTAXAMT"
						+ " from TV_INFILE_TMP_PLACE where S_OPERBATCH = ? and S_TCBSTRECODE =? and S_TCBSTAXORGCODE = ? ";
			} else {
				// 是汇总处理
				tmpplacefilesql = "select S_TCBSTAXORGCODE,sum(N_FACTTAXAMT) as N_FACTTAXAMT,S_BUDGETSUBJECTCODE,S_BUDGETLEVELCODE,S_ASSITSIGN"
						+ " from TV_INFILE_TMP_PLACE where S_OPERBATCH = ? and S_TCBSTRECODE =? and S_TCBSTAXORGCODE = ? "
						+ " group by S_TCBSTAXORGCODE,S_BUDGETSUBJECTCODE,S_BUDGETLEVELCODE,S_ASSITSIGN";
			}

			List<String> packList = new ArrayList<String>();
			List<TvFilepackagerefDto> packdtos = new ArrayList<TvFilepackagerefDto>();
			batchRetriever = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLBatchRetriever();
			for (int i = 0; i < taxtrereflist.size(); i++) {
				FileObjDto tmpdto = taxtrereflist.get(i);
				batchRetriever.clearParams();
				batchRetriever.addParam(fileflag);
				batchRetriever.addParam(tmpdto.getStrecode()); // 国库代码
				batchRetriever.addParam(tmpdto.getStaxorgcode()); // 征收机关代码
				batchRetriever.setMaxRows(MsgConstant.TIPS_MAX_OF_PACK); // TIPS每包最最大笔数

				batchRetriever.runQuery(tmpplacefilesql);
				while (batchRetriever.hasMore()) {
					String seqno = SequenceGenerator
							.getNextByDb2(SequenceName.TRA_ID_SEQUENCE_KEY);
					String packno = SequenceGenerator.changePackNo(seqno);
					String tmpPackNo = packno + "000";

					SQLResults result = batchRetriever.RetrieveNextBatch();

					int icount = result.getRowCount();
					if (icount != 0) {
						TvInfileDto[] dtos = new TvInfileDto[icount];
						BigDecimal sumamt = new BigDecimal("0.00");
						for (int j = 0; j < icount; j++) {
							int k = 0;
							dtos[j] = new TvInfileDto();

							dtos[j].setStaxorgcode((result.getString(j, k++))); // TCBS征收机关代码
							dtos[j].setNmoney((result.getBigDecimal(j, k++))); // 交易金额
							dtos[j].setSbudgetsubcode(result.getString(j, k++)); // 预算科目代码
							dtos[j].setSbudgetlevelcode(result
									.getString(j, k++)); // 预算级次
							dtos[j].setSassitsign(result.getString(j, k++)); // 辅助标志
							dtos[j].setSgenticketdate(currentDate); // 开票日期

							dtos[j].setSbudgettype(MsgConstant.BDG_KIND_IN); // 预算种类
							dtos[j].setSorgcode(sorgcode); // 机构代码
							dtos[j].setSaccdate(currentDate); // 账务日期
							dtos[j].setSpackageno(tmpPackNo); // 包流水号
							dtos[j].setStaxorgcode(tmpdto.getStaxorgcode()); // 征收机关代码
							dtos[j]
									.setSfilename((fileResultDto.getSfilename())); // 文件名称
							dtos[j].setSrecvtrecode(tmpdto.getStrecode()); // 收款国库代码
							dtos[j].setSdealno(SequenceGenerator
									.changeTraSrlNo(packno, j)); // 交易流水号
							dtos[j].setStrasrlno(fileResultDto.getStrasrlno()); // 资金收纳流水号
							dtos[j].setStaxticketno(dtos[j].getSdealno()); // 税票号码
							dtos[j].setScommitdate(currentDate); // 委托日期
							dtos[j].setStrimflag(MsgConstant.TIME_FLAG_NORMAL); // 调整期为正常期
							dtos[j]
									.setSstatus(DealCodeConstants.DEALCODE_ITFE_NO_SEND); // 交易状态未发送
							dtos[j].setSusercode(susercode);
							dtos[j]
									.setSsourceflag(MsgConstant.MSG_SOURCE_PLACE);// 数据来源

							// 2-地税
							if (null != iscollect && iscollect != 0) {
								// 按照明细处理
								dtos[j].setSgenticketdate(result.getString(j,
										k++)); // 开票日期
								dtos[j].setSpaybnkno(result.getString(j, k++));// 付款行号
								dtos[j].setSopenaccbankcode(result.getString(j,
										k++));// 付款开户行行号
								dtos[j]
										.setStaxpayname(result
												.getString(j, k++));// 纳税人名称
								dtos[j].setSpayacct(result.getString(j, k++));// 付款帐户
								dtos[j]
										.setStaxpaycode(result
												.getString(j, k++));// 纳税人编码
								dtos[j].setSlimitdate(result.getString(j, k++));// 限缴日期
								dtos[j].setStaxtypecode(result
										.getString(j, k++));// 税种代码
								dtos[j].setStaxtypename(result
										.getString(j, k++));// 税种名称
								dtos[j].setStaxstartdate(result.getString(j,
										k++));// 所属起始日期
								dtos[j]
										.setStaxenddate(result
												.getString(j, k++));// 所属终止日期
								dtos[j].setStaxsubjectname(result.getString(j,
										k++));// 税目名称
								dtos[j].setStaxnumber(result.getString(j, k++));// 课税数量
								dtos[j]
										.setNtaxamt(result
												.getBigDecimal(j, k++));// 计税金额
								dtos[j].setNtaxrate(result
										.getBigDecimal(j, k++));// 税率
								dtos[j].setNfacttaxamt(result.getBigDecimal(j,
										k++));// 实缴税额
								dtos[j].setNdiscounttaxamt(result
										.getBigDecimal(j, k++));// 扣税额
							}

							sumamt = sumamt.add(dtos[j].getNmoney());
						}

						// 批量保存数据
						DatabaseFacade.getDb().create(dtos);
						packList.add(tmpPackNo);

						// 保存组头信息
						TvFilepackagerefDto packdto = new TvFilepackagerefDto();
						packdto.setSorgcode(sorgcode); // 机构代码
						packdto.setStrecode(tmpdto.getStrecode()); // 国库代码
						packdto.setSfilename(fileResultDto.getSfilename()); // 导入文件名
						packdto.setStaxorgcode(tmpdto.getStaxorgcode()); // 机关代码
						packdto.setScommitdate(currentDate); // 委托日期
						packdto.setSaccdate(currentDate); // 账务日期
						packdto.setSpackageno(tmpPackNo); // 包流水号
						packdto
								.setSoperationtypecode(BizTypeConstant.BIZ_TYPE_INCOME); // 业务类型
						packdto.setIcount(icount); // 总笔数
						packdto.setNmoney(sumamt); // 总金额

						if (fileResultDto.getIsError()) {
							// 如果是异常处理，直接发送报文，否则需要确认提交
							packdto
									.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING); // 交易状态未待处理
						} else {
							packdto
									.setSretcode(DealCodeConstants.DEALCODE_ITFE_NO_SEND); // 交易状态未未发送
						}

						packdto.setSusercode(susercode); // 操作员代码
						packdto.setSdemo(fileResultDto.getStrasrlno()); // 资金收纳流水号
						packdto.setImodicount(1);

						packdtos.add(packdto);
					}
				}
			}
			try {
				TvFilepackagerefDto[] heads = new TvFilepackagerefDto[packdtos
						.size()];
				heads = packdtos.toArray(heads);
				DatabaseFacade.getDb().create(heads);
			} catch (JAFDatabaseException e) {
				logger.error("保存报文头的时候出现异常！", e);
				throw new ITFEBizException("保存报文头的时候出现异常！", e);
			}

			// 关闭连接
			batchRetriever.closeConnection();

			TvInfileTmpPlaceDto tmpdto = new TvInfileTmpPlaceDto();
			tmpdto.setSoperbatch(fileflag);

			try {
				CommonFacade.getODB().deleteRsByDto(tmpdto);
			} catch (ValidateException e) {
				logger.error("删除临时表记录的时候出现数据库异常!", e);
			} catch (JAFDatabaseException e) {
				logger.error("删除临时表记录的时候出现数据库异常!", e);
			}

		} catch (FileOperateException e) {
			logger.error("文件上载的时候出现异常!", e);
			throw new ITFEBizException("文件上载的时候出现异常!", e);
		} catch (JAFDatabaseException e) {
			logger.error("数据搬运的时候出现数据库异常!", e);
			throw new ITFEBizException("数据搬运的时候出现数据库异常!", e);
		} catch (SequenceException e) {
			logger.error("生成流水号时出现异常!", e);
			throw new ITFEBizException("生成流水号时出现异常!", e);
		} finally {
			if (null != batchRetriever) {
				batchRetriever.closeConnection();
			}

			TvInfileTmpPlaceDto tmpdto = new TvInfileTmpPlaceDto();
			tmpdto.setSoperbatch(fileflag);

			try {
				CommonFacade.getODB().deleteRsByDto(tmpdto);
			} catch (ValidateException e) {
				logger.error("删除临时表记录的时候出现数据库异常!", e);
			} catch (JAFDatabaseException e) {
				logger.error("删除临时表记录的时候出现数据库异常!", e);
			}
		}
	}

	/**
	 * 处理TIPS接口数据
	 * 
	 * @param FileResultDto
	 *            fileResultDto 解析文件接口对象
	 * @param String
	 *            sorgcode 机构代码
	 * @param String
	 *            susercode 用户代码
	 * @throws ITFEBizException
	 */
	public void dealTIPSData(FileResultDto fileResultDto, String sorgcode,
			String susercode, Integer iscollect) throws ITFEBizException {

		String currentDate = TimeFacade.getCurrentStringTime(); // 当前系统的时间

		String dirsep = File.separator; // 取得系统分割符
		String absolutePath = ITFECommonConstant.FILE_ROOT_PATH + currentDate
				+ dirsep + sorgcode + dirsep + fileResultDto.getSfilename();
		String absoluteSql = absolutePath + ".sql";

		SQLBatchRetriever batchRetriever = null; // 批量查询要用到

		try {
			// 第二步 存储文件
			String os = System.getProperties().getProperty("os.name");
			if (os.indexOf("Win") >= 0) {
				FileUtil.getInstance().writeFile(absolutePath,
						fileResultDto.getSmaininfo());
			} else {
				FileUtil.getInstance().writeFile(absolutePath,
						fileResultDto.getSmaininfo().replaceAll("\\r", ""));
			}
			

			// 第三步 调用SHELL将文件IMPORT进数据库 临时表tv_infile_tmp (先删除然后插入)
			String importcontent = PublicSearchFacade.getSqlConnectByProp()
					+ "delete from TV_INFILE_TMP_TIPS where S_ORGCODE = '"
					+ sorgcode
					+ "' and S_FILENAME = '"
					+ fileResultDto.getSfilename()
					+ "';\n"
					+ "import from "
					+ absolutePath
					+ " of del modified by compound=100 insert into TV_INFILE_TMP_TIPS;";
			FileUtil.getInstance().writeFile(absoluteSql, importcontent);
			String results = null;
			try {
				byte[] bytes = null;
				// if(!isWin()){
				// String command =
				// "su - db2inst1 -c \"/home/db2inst1/sqllib/bin/db2 -tvf ";
				// command = command + absoluteSql + "\"";
				// bytes = CallShellUtil.callShellWithRes(command);
				// }else{
				// bytes = DB2CallShell.dbCallShellWithRes(absoluteSql);
				// }

				bytes = DB2CallShell.dbCallShellWithRes(absoluteSql);
				if (bytes.length > MsgConstant.MAX_CALLSHELL_RS * 1024) {
					results = new String(bytes, 0,
							MsgConstant.MAX_CALLSHELL_RS * 1024);
				} else {
					results = new String(bytes);
				}
				logger.debug("调用SHELL结果:" + results);
				bytes = null;
			} catch (Exception e) {
				logger.error("调用SHELL:数据导入的时候出现数据库异常!", e);
				throw new ITFEBizException("调用SHELL:数据导入的时候出现数据库异常!", e);
			}

			// 第四步 判断调用SHELL的处理结果,保证全部都成功.可能出现的错误:类型转化错误,数据被截断
			VerifyCallShellRs.verifyShellForIncomeRs(results);

			// // 第五步 参数的校验 1 征收机关代码 2 辅助标志(取消掉参数的转化,直接需要校验)
			// VerifyParamTrans.verifyIncomeTrans(sorgcode,
			// fileResultDto.getSfilename());
			VerifyParamTrans.verifyBdgSbt(MsgConstant.MSG_SOURCE_TIPS,
					sorgcode, fileResultDto.getSfilename(), null);
			if("000077100005".equals(ITFECommonConstant.SRC_NODE))
				VerifyParamTrans.verifyTreasury(sorgcode, fileResultDto.getSfilename());//验证收款国库和目标国库是否一致
			/**
			 * 由于字符格式问题，经过import到数据库中的核算主体代码这一项带有除12位代码外的其他字符，所以以下的判断都失败，
			 * 所以增加一个SQL，用于将核算主体这一列还原 2013-04-25
			 */
			String reviewSQL = "update TV_INFILE_TMP_TIPS set s_orgcode=substr(s_orgcode,1,12) where S_FILENAME = ?";
			SQLExecutor reviewExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			reviewExec.addParam(fileResultDto.getSfilename());
			reviewExec.runQueryCloseCon(reviewSQL);

			// 第六步 按照文件名称,机构代码,国库代码和征收机关代码分组(注意要去掉代征业务)
			List<FileObjDto> taxlist = new ArrayList<FileObjDto>();
			String selectSQL = "select S_TRECODE,S_TAXORGCODE from TV_INFILE_TMP_TIPS where S_FILENAME = ? and S_ORGCODE = ? group by S_TRECODE,S_TAXORGCODE";
			SQLExecutor sqlExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			sqlExec.addParam(fileResultDto.getSfilename());
			sqlExec.addParam(sorgcode);
			SQLResults taxorgRs = sqlExec.runQueryCloseCon(selectSQL);
			int count = taxorgRs.getRowCount();
			for (int i = 0; i < count; i++) {
				FileObjDto tmpdto = new FileObjDto();
				tmpdto.setStrecode(taxorgRs.getString(i, 0));
				tmpdto.setStaxorgcode(taxorgRs.getString(i, 1));

				taxlist.add(tmpdto);
			}

			if (count == 0) {
				logger.error("没有找到符合条件的记录!");
				throw new ITFEBizException("没有找到符合条件的记录");
			}

			// 增加一步 如果是异常处理，需要校验文件金额匹配
			if (fileResultDto.getIsError()) {
				String amtsql = "select sum(N_MONEY) as N_MONEY from TV_INFILE_TMP_TIPS where S_ORGCODE = ? and S_FILENAME = ? ";
				SQLExecutor amtExec = null;
				BigDecimal zero = new BigDecimal("0.00");
				try {
					amtExec = DatabaseFacade.getDb().getSqlExecutorFactory()
							.getSQLExecutor();
					amtExec.addParam(sorgcode);
					amtExec.addParam(fileResultDto.getSfilename());
					SQLResults amtRs = amtExec.runQueryCloseCon(amtsql);
					BigDecimal famt = amtRs.getBigDecimal(0, 0);
					if (null == famt || zero.compareTo(famt) == 0
							|| famt.compareTo(fileResultDto.getFsumamt()) != 0) {
						logger.error("异常处理中文件总金额[" + famt + "]与录入金额不相等！");
						throw new ITFEBizException("异常处理中文件总金额[" + famt
								+ "]与录入金额不相等！");
					}
				} catch (JAFDatabaseException e) {
					logger.error("查询文件汇总金额时出现异常!", e);
					throw new ITFEBizException("查询文件汇总金额时出现异常!", e);
				} finally {
					if (null != amtExec) {
						amtExec.closeConnection();
					}
				}
			}

			// 第七步 根据分组条件去组包.
			List<String> packList = new ArrayList<String>();
			String tmpfilesql = null;

			if (null == iscollect || iscollect == 0) {
				// 是汇总处理
				tmpfilesql = "select S_TAXORGCODE,sum(N_MONEY) as N_MONEY,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_BILLDATE "
						+ " from TV_INFILE_TMP_TIPS where S_ORGCODE = ? and S_FILENAME = ? and S_TRECODE =? and S_TAXORGCODE = ? "
						+ " group by S_TAXORGCODE,S_TRECODE,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_BILLDATE";
			} else {
				// 按照明细处理 S_BILLDATE
				tmpfilesql = " select S_TAXORGCODE,N_MONEY,S_BUDGETTYPE,S_BUDGETSUBCODE,S_BUDGETLEVELCODE,S_ASSITSIGN,S_BILLDATE,"
						+ " S_TAXPAYCODE,S_TAXPAYNAME,S_TAXSTARTDATE,S_TAXENDDATE,S_PAYBNKNO,S_PAYOPBNKNO,S_PAYACCT"
						+ " from TV_INFILE_TMP_TIPS where S_ORGCODE = ? and S_FILENAME = ? and S_TRECODE =? and S_TAXORGCODE = ? ";
			}

			List<TvFilepackagerefDto> packdtos = new ArrayList<TvFilepackagerefDto>();
			batchRetriever = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLBatchRetriever();
			for (int i = 0; i < count; i++) {
				FileObjDto tmpdto = taxlist.get(i);
				batchRetriever.clearParams();
				batchRetriever.addParam(sorgcode);
				batchRetriever.addParam(fileResultDto.getSfilename());
				batchRetriever.addParam(tmpdto.getStrecode()); // 国库代码
				batchRetriever.addParam(tmpdto.getStaxorgcode()); // 征收机关代码
				batchRetriever.setMaxRows(MsgConstant.TIPS_MAX_OF_PACK); // TIPS每包最最大笔数

				batchRetriever.runQuery(tmpfilesql);
				while (batchRetriever.hasMore()) {
					String seqno = SequenceGenerator
							.getNextByDb2(SequenceName.TRA_ID_SEQUENCE_KEY);
					String packno = SequenceGenerator.changePackNo(seqno);
					String tmpPackNo = packno + "000";

					SQLResults result = batchRetriever.RetrieveNextBatch();

					int icount = result.getRowCount();
					if (icount != 0) {
						TvInfileDto[] dtos = new TvInfileDto[icount];
						BigDecimal sumamt = new BigDecimal("0.00");
						for (int j = 0; j < icount; j++) {
							int k = 0;
							dtos[j] = new TvInfileDto();

							dtos[j].setStaxorgcode(result.getString(j, k++)); // 征收机关代码
							dtos[j].setNmoney((result.getBigDecimal(j, k++))); // 交易金额
							dtos[j].setSbudgettype((result.getString(j, k++))); // 预算种类
							dtos[j].setSbudgetsubcode(result.getString(j, k++)); // 预算科目代码
							dtos[j].setSbudgetlevelcode(result
									.getString(j, k++)); // 预算级次
							dtos[j].setSassitsign(result.getString(j, k++)); // 辅助标志
							dtos[j].setSgenticketdate(result.getString(j, k++)); // 开票日期

							dtos[j].setSorgcode(sorgcode); // 机构代码
							dtos[j].setScommitdate(currentDate); // 委托日期
							dtos[j].setSaccdate(currentDate); // 账务日期
							dtos[j].setSpackageno(tmpPackNo); // 包流水号
							dtos[j].setStaxorgcode(tmpdto.getStaxorgcode()); // 征收机关代码
							dtos[j]
									.setSfilename((fileResultDto.getSfilename())); // 文件名称
							dtos[j].setSrecvtrecode(tmpdto.getStrecode()); // 收款国库代码
							dtos[j].setSdealno(SequenceGenerator
									.changeTraSrlNo(packno, j)); // 交易流水号
							dtos[j].setStrasrlno(fileResultDto.getStrasrlno()); // 资金收纳流水号
							dtos[j].setStaxticketno(dtos[j].getSdealno()); // 税票号码
							dtos[j].setStrimflag(MsgConstant.TIME_FLAG_NORMAL); // 调整期为正常期
							dtos[j]
									.setSstatus(DealCodeConstants.DEALCODE_ITFE_NO_SEND); // 交易状态未发送
							dtos[j].setSusercode(susercode);
							dtos[j].setSsourceflag(MsgConstant.MSG_SOURCE_TIPS);// 数据来源

							// 2-TIPS
							if (null != iscollect && iscollect != 0) {
								// 按照明细处理
								dtos[j].setSgenticketdate(result.getString(j,
										k++)); // 开票日期
								dtos[j]
										.setStaxpaycode(result
												.getString(j, k++));// 纳税人编码
								dtos[j]
										.setStaxpayname(result
												.getString(j, k++));// 纳税人名称
								dtos[j].setStaxstartdate(result.getString(j,
										k++));// 所属起始日期
								dtos[j]
										.setStaxenddate(result
												.getString(j, k++));// 所属终止日期
								dtos[j].setSpaybnkno(result.getString(j, k++));// 付款行号
								dtos[j].setSopenaccbankcode(result.getString(j,
										k++));// 付款开户行行号
								dtos[j].setSpayacct(result.getString(j, k++));// 付款帐户
							}

							sumamt = sumamt.add(dtos[j].getNmoney());
						}

						// 批量保存数据
						DatabaseFacade.getDb().create(dtos);
						packList.add(tmpPackNo);

						// 保存组头信息
						TvFilepackagerefDto packdto = new TvFilepackagerefDto();
						packdto.setSorgcode(sorgcode); // 机构代码
						packdto.setStrecode(tmpdto.getStrecode()); // 国库代码
						packdto.setSfilename(fileResultDto.getSfilename()); // 导入文件名
						packdto.setStaxorgcode(tmpdto.getStaxorgcode()); // 机关代码
						packdto.setScommitdate(currentDate); // 委托日期
						packdto.setSaccdate(currentDate); // 账务日期
						packdto.setSpackageno(tmpPackNo); // 包流水号
						packdto
								.setSoperationtypecode(BizTypeConstant.BIZ_TYPE_INCOME); // 业务类型
						packdto.setIcount(icount); // 总笔数
						packdto.setNmoney(sumamt); // 总金额

						if (fileResultDto.getIsError()) {
							// 如果是异常处理，直接发送报文，否则需要确认提交
							packdto
									.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING); // 交易状态未待处理
						} else {
							packdto
									.setSretcode(DealCodeConstants.DEALCODE_ITFE_NO_SEND); // 交易状态未未发送
						}

						packdto.setSusercode(susercode); // 操作员代码
						packdto.setSdemo(fileResultDto.getStrasrlno()); // 资金收纳流水号
						packdto.setImodicount(1);

						packdtos.add(packdto);
					}
				}
			}
			try {
				TvFilepackagerefDto[] heads = new TvFilepackagerefDto[packdtos
						.size()];
				heads = packdtos.toArray(heads);
				DatabaseFacade.getDb().create(heads);
			} catch (JAFDatabaseException e) {
				logger.error("保存报文头的时候出现异常！", e);
				throw new ITFEBizException("保存报文头的时候出现异常！", e);
			}

			// 关闭连接
			batchRetriever.closeConnection();

			// if (fileResultDto.getIsError()) {
			// // 如果是异常处理，直接发送报文，否则需要确认提交
			// // 按照条件发送报文
			// fileResultDto.setPacknos(packList);
			// for (int i = 0; i < packList.size(); i++) {
			// this.sendMsg(fileResultDto.getSfilename(), sorgcode,
			// packList.get(i), MsgConstant.MSG_NO_7211, filetime, null, false);
			// }
			// }

			// 删除临时表记录
			TvInfileTmpTipsDto tmpdto = new TvInfileTmpTipsDto();
			tmpdto.setSorgcode(sorgcode);
			tmpdto.setSfilename(fileResultDto.getSfilename());
			CommonFacade.getODB().deleteRsByDto(tmpdto);
		} catch (FileOperateException e) {
			logger.error("文件上载的时候出现异常!", e);
			throw new ITFEBizException("文件上载的时候出现异常!", e);
		} catch (JAFDatabaseException e) {
			logger.error("数据搬运的时候出现数据库异常!", e);
			throw new ITFEBizException("数据搬运的时候出现数据库异常!", e);
		} catch (SequenceException e) {
			logger.error("生成流水号时出现异常!", e);
			throw new ITFEBizException("生成流水号时出现异常!", e);
		} catch (ValidateException e) {
			logger.error("删除临时表记录的时候出现数据库异常!", e);
			throw new ITFEBizException("删除临时表记录的时候出现数据库异常!", e);
		} finally {
			if (null != batchRetriever) {
				batchRetriever.closeConnection();
			}

			TvInfileTmpTipsDto tmpdto = new TvInfileTmpTipsDto();
			tmpdto.setSorgcode(sorgcode);
			tmpdto.setSfilename(fileResultDto.getSfilename());
			try {
				CommonFacade.getODB().deleteRsByDto(tmpdto);
			} catch (ValidateException e) {
				logger.error("删除临时表记录的时候出现数据库异常!", e);
			} catch (JAFDatabaseException e) {
				logger.error("删除临时表记录的时候出现数据库异常!", e);
			}
		}

	}

	/**
	 * 校验电子税票凭证编号是否重复
	 * 
	 * @param voulist
	 * @param filename
	 * @throws ITFEBizException
	 */
	public static void checkIncomeVouRepeat(String orgcode,
			List<String> voulist, String filename) throws ITFEBizException {

		String today = TimeFacade.getCurrentStringTime(); // 当前日期

		int oldSize = 0;

		int newSize = 0;

		Set<String> sets = new HashSet<String>();

		if (voulist == null || voulist.size() == 0) {
			return;
		}

		// 电子税票DTO
		TvInfileDto dto = new TvInfileDto();
		dto.setScommitdate(today);
		dto.setSorgcode(orgcode);
		List<TvInfileDto> incomelist = null;
		try {
			incomelist = (List<TvInfileDto>) CommonFacade.getODB()
					.findRsByDtoWithUR(dto);
		} catch (JAFDatabaseException e) {
			logger.error("查找收入税票异常！", e);
			throw new ITFEBizException("查找收入税票异常！", e);
		} catch (ValidateException e) {
			logger.error("查找收入税票异常！", e);
			throw new ITFEBizException("查找收入税票异常！", e);
		}
		if (incomelist != null && incomelist.size() > 0) {
			for (TvInfileDto dt : incomelist) {
				voulist.add(dt.getStbstaxorgcode().trim() + ","
						+ dt.getStaxticketno().trim());
			}
			for (String item : voulist) {
				oldSize = sets.size();
				sets.add(item.trim());
				newSize = sets.size();
				if (newSize == oldSize) {
					throw new ITFEBizException("电子税票文件[" + filename + "]中凭证编号["
							+ item.split(",")[1] + "]和当日已导入凭证编号重复,请查证");
				}
			}
		}
	}

	/**
	 * 文件名称转化
	 * 
	 * @param String
	 *            srcFile 原文件
	 * @param String
	 *            srcFlag 原标志
	 * @param String
	 *            desFlag 目标标志
	 * @return
	 */
	private String changeFileName(String srcFile, String srcFlag, String desFlag) {
		String filename = srcFile.replace(srcFlag, desFlag);
		if (filename.indexOf(desFlag) <= 0) {
			filename = filename + desFlag;
		}

		return filename;
	}

	/**
	 * 文件路径转化
	 * 
	 * @param String
	 *            srcFile 源文件路径
	 * @param String
	 *            dirsep 系统文件分隔符号
	 * @return
	 */
	private String changeFilePath(String srcFile, String dirsep) {

		srcFile = srcFile.replace("///", dirsep).replace("//", dirsep).replace(
				"/", dirsep);
		srcFile = srcFile.replace("\\\\\\", dirsep).replace("\\\\", dirsep)
				.replace("\\", dirsep);
		srcFile = srcFile.replace(dirsep + dirsep + dirsep, dirsep).replace(
				dirsep + dirsep, dirsep);

		return srcFile;
	}

	/**
	 * @return
	 */
	private static boolean isWin() {
		String osName = System.getProperty("os.name");
		if (osName.indexOf("Windows") >= 0) {
			return true;
		} else {
			return false;
		}
	}

}

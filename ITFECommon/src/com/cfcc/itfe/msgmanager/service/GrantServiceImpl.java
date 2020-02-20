package com.cfcc.itfe.msgmanager.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DB2CallShell;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractServiceManagerServer;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsConverttobankDto;
import com.cfcc.itfe.persistence.dto.TvGrantpayfilemainTmpDto;
import com.cfcc.itfe.persistence.dto.TvGrantpayfilesubTmpDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.verify.VerifyCallShellRs;
import com.cfcc.itfe.verify.VerifyFileName;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 授权支付额度报文发送预处理
 * 
 * @author zhouchuan modify by zhouchuan 20091201 将出票单位修改为银行代码
 */
public class GrantServiceImpl extends AbstractServiceManagerServer {

	private static Log logger = LogFactory.getLog(GrantServiceImpl.class);

	/**
	 * 处理从客户端传送过来的文件对象(直接支付额度业务业务处理)
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
	public void dealFileDto(FileResultDto fileResultDto, String sorgcode, String susercode) throws ITFEBizException {
		String filetime = PublicSearchFacade.getFileObjByFileName(fileResultDto.getSfilename()).getSdate(); // 文件日期
		String currentDate = TimeFacade.getCurrentStringTime();// 当前系统日期

		try {
			// 第一步 取出关键字段
			HashMap<String, Object> mapkey = fileResultDto.getMap();
			String czjname = (String) mapkey.get(MsgConstant.MSG_FLAG_CZJNAME); // 财政局标志
			String reveiver = (String) mapkey.get(MsgConstant.MSG_FLAG_RECEIVER); // 接收单位

			// 第二步 进行关键字段的转化
			if (null == czjname || "".equals(czjname.trim()) || null == reveiver || "".equals(reveiver.trim())) {
				logger.error("财政局标志[" + czjname + "]或接收单位标志[" + reveiver + "]为空,请确认!");
				throw new ITFEBizException("财政局标志[" + czjname + "]或接收单位标志[" + reveiver + "]为空,请确认!");
			}
			TsConvertfinorgDto finflagdto = PublicSearchFacade.findFinOrgDto(sorgcode, czjname);
			if (null == finflagdto) {
				logger.error("没有找到对应财政局标志转化参数！[" + czjname + "]");
				throw new ITFEBizException("没有找到对应财政局标志转化参数！[" + czjname + "]");
			}
			TsConverttobankDto bankdto = PublicSearchFacade.findBankDto(sorgcode, reveiver, finflagdto.getStrecode());
			if (null == bankdto) {
				logger.error("没有找到对应接收单位和国库代码的转发银行的转化参数！[" + reveiver + "-" + finflagdto.getStrecode() +  "]");
				throw new ITFEBizException("没有找到对应接收单位和国库代码的转发银行的转化参数！[" + reveiver + "-" + finflagdto.getStrecode() +  "]");
			}
			String strecode = finflagdto.getStrecode(); // 国库代码
			String sfincode = finflagdto.getSfinorgcode(); // 财政机构代码
			String sbankno = bankdto.getStobank(); // 银行代码
			String sagentbank = bankdto.getSagentbank(); // 代理银行

			// 第三步 存储文件
			String dirsep = File.separator; // 取得系统分割符
			// 主信息记录存放的路径
			String mainPath = ITFECommonConstant.FILE_ROOT_PATH + currentDate + dirsep + sorgcode + dirsep + "main" + fileResultDto.getSfilename();
			// 明细信息记录存放的路径
			String detailPath = ITFECommonConstant.FILE_ROOT_PATH + currentDate + dirsep + sorgcode + dirsep + "detail"
					+ fileResultDto.getSfilename();
			// 调用SQL语句存放的路径
			String callshellSql = ITFECommonConstant.FILE_ROOT_PATH + currentDate + dirsep + sorgcode + dirsep + fileResultDto.getSfilename()
					+ ".sql";
			String os = System.getProperties().getProperty("os.name");
			if (os.indexOf("Win") >= 0) {
				FileUtil.getInstance().writeFile(mainPath, fileResultDto.getSmaininfo());
				FileUtil.getInstance().writeFile(detailPath, fileResultDto.getSdetailinfo());
			} else {
				FileUtil.getInstance().writeFile(mainPath, fileResultDto.getSmaininfo().replaceAll("\\r", ""));
				FileUtil.getInstance().writeFile(detailPath, fileResultDto.getSdetailinfo().replaceAll("\\r", ""));
			}
			
			// 第四步 调用SHELL将文件IMPORT进数据库 临时表TV_GRANTPAYFILEMAIN_TMP
			// 和TV_GRANTPAYFILESUB_TMP
			String importcontent = PublicSearchFacade.getSqlConnectByProp() + "delete from " + TvGrantpayfilemainTmpDto.tableName()
					+ " where S_ORGCODE = '" + sorgcode + "' and S_FILENAME = '" + fileResultDto.getSfilename() + "';\n" + "delete from "
					+ TvGrantpayfilesubTmpDto.tableName() + " where S_ORGCODE = '" + sorgcode + "' and S_FILENAME = '" + fileResultDto.getSfilename()
					+ "';\n" + "import from " + mainPath + " of del modified by delprioritychar insert into " + TvGrantpayfilemainTmpDto.tableName()
					+ ";\n" + "import from " + detailPath + " of del modified by delprioritychar insert into " + TvGrantpayfilesubTmpDto.tableName()
					+ ";\n";

			FileUtil.getInstance().writeFile(callshellSql, importcontent);
			String results = null;
			try {
				byte[] bytes = DB2CallShell.dbCallShellWithRes(callshellSql);
				if(bytes.length > MsgConstant.MAX_CALLSHELL_RS*1024){
					results = new String(bytes,0,MsgConstant.MAX_CALLSHELL_RS*1024);
				}else{
					results = new String(bytes);
				}
				
				bytes = null;
			} catch (Exception e) {
				logger.error("调用SHELL:数据导入的时候出现数据库异常!", e);
				throw new ITFEBizException("调用SHELL:数据导入的时候出现数据库异常!", e);
			}

			// 判断调用SHELL的处理结果,保证全部都成功.可能出现的错误:类型转化错误,数据被截断
			VerifyCallShellRs.verifyShellForPayoutRs(results);
			
			// 校验总金额
			VerifyParamTrans.verifySumAmt(sorgcode, fileResultDto.getSfilename(), TvGrantpayfilemainTmpDto.tableName(), fileResultDto.getFsumamt());
			
			// 第五步 取得预算种类
			String sbdgkind = VerifyParamTrans.verifyPayOutBdgKind(sorgcode, fileResultDto.getSfilename(), TvGrantpayfilesubTmpDto.tableName());

			// 第六步校验导入文件的重复性
			boolean repeatflag = VerifyFileName.verifyImportRepeat(sorgcode, fileResultDto.getSfilename());
			TvGrantpaymsgmainDto grantdto = new TvGrantpaymsgmainDto();
			grantdto.setSorgcode(sorgcode);
			grantdto.setSfilename(fileResultDto.getSfilename());
			
			List grantlist = CommonFacade.getODB().findRsByDto(grantdto);
			if(grantlist != null && grantlist.size() > 0){
				repeatflag = true;
			}else{
				repeatflag = false;
			}
			
			//获取凭证序列号
			String mainvou = SequenceGenerator.getNextByDb2(SequenceName.GRANTPAY_SEQ,SequenceName.TRAID_SEQ_CACHE,SequenceName.TRAID_SEQ_STARTWITH);
			
			if (repeatflag) {
				// 是重复导入,查找是否有失败的记录.如果有,发送失败的业务,如果没有,提示用户错误信息.
				boolean failflag = PublicSearchFacade.findFailedGrantRecord(sorgcode, fileResultDto.getSfilename());
				if (!failflag) {
					// 没有失败记录,提示用户错误信息.
					logger.error("此文件[" + fileResultDto.getSfilename() + "]已经导入成功,不能重复导入!");
					throw new ITFEBizException("此文件[" + fileResultDto.getSfilename() + "]已经导入成功,不能重复导入!");
				}

				// 第 六.一 步 将临时表的记录取出然后存储到正式表
				SQLExecutor sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				String movedataSql = "insert into "
						+ TvGrantpaymsgmainDto.tableName()
						+ " (I_VOUSRLNO,S_ORGCODE,S_LIMITID,S_COMMITDATE,S_ACCDATE,S_FILENAME,S_TRECODE,S_PACKAGENO,S_PAYUNIT,S_TRANSBANKCODE,N_MONEY,S_PACKAGETICKETNO,"
						+ "	S_GENTICKETDATE,S_OFYEAR,S_OFMONTH,S_TRANSACTUNIT,S_AMTTYPE,S_USERCODE,S_STATUS,S_DEALNO,S_BUDGETUNITCODE,S_BUDGETTYPE,TS_SYSUPDATE) "
						+ " select next value for ITFE_GRANTPAYSEQ,main.S_ORGCODE,main.S_LIMITID,'"
						+ filetime
						+ "','"
						+ currentDate
						+ "',main.S_FILENAME,'"
						+ strecode
						+ "',sub.S_PACKAGENO,'"
						+ sfincode
						+ "','"
						+ sbankno
						+ "',sub.N_MONEY,main.S_LIMITID,main.S_GENTICKETDATE,main.S_OFYEAR,main.S_OFMONTH,' "
						+ sagentbank
						+ "','"
						+ MsgConstant.AMT_KIND_BANK
						+ "','"
						+ susercode
						+ "','"
						+ DealCodeConstants.DEALCODE_ITFE_DEALING
//						+ "',RIGHT(TRIM(CHAR('0000000'||CHAR(next value for ITFE_DEALNO_SEQUENCE))),8),"
						+ "',sub.S_DEALNO,sub.S_BUDGETUNITCODE,'"
						+ sbdgkind
						+ "',CURRENT TIMESTAMP"
						+ "  from "
						+ TvGrantpayfilemainTmpDto.tableName()
						+ " main,(select sub1.S_PACKAGENO as S_PACKAGENO, sum(sub1.N_MONEY) as N_MONEY , sub1.S_LIMITID as S_LIMITID,sub1.S_OFYEAR as S_OFYEAR ,sub1.S_ORGCODE as S_ORGCODE ,sub1.S_FILENAME as S_FILENAME ,sub1.S_BUDGETUNITCODE as S_BUDGETUNITCODE,sub1.S_DEALNO as S_DEALNO from "
						+ TvGrantpayfilesubTmpDto.tableName()
						+ " sub1 , "
						+ TvGrantpaymsgsubDto.tableName()
						+ " sub2 "
						+ " where sub1.S_ORGCODE = ? and sub1.S_FILENAME = ? and sub2.S_STATUS = ? "
						+ " and sub1.S_ORGCODE = sub2.S_ORGCODE and sub1.S_FILENAME = sub2.S_FILENAME and sub1.S_LIMITID = sub2.S_LIMITID and sub1.S_OFYEAR = sub2.S_OFYEAR and sub1.S_LINE = sub2.S_LINE and sub1.S_BUDGETUNITCODE = sub2.S_BUDGETUNITCODE and sub1.S_DEALNO as sub2.S_DEALNO "
						+ " group by sub1.S_ORGCODE,sub1.S_FILENAME,sub1.S_PACKAGENO,sub1.S_LIMITID,sub1.S_OFYEAR,sub1.S_BUDGETUNITCODE,sub1.S_DEALNO) "
						+ " sub "
						+ " where main.S_ORGCODE = ? and main.S_FILENAME = ? and "
						+ " main.S_ORGCODE = sub.S_ORGCODE and main.S_FILENAME = sub.S_FILENAME and main.S_LIMITID = sub.S_LIMITID and main.S_OFYEAR  = sub.S_OFYEAR ";
				sqlExec.addParam(sorgcode);
				sqlExec.addParam(fileResultDto.getSfilename());
				sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL); // 处理失败
				sqlExec.addParam(sorgcode);
				sqlExec.addParam(fileResultDto.getSfilename());
				sqlExec.runQueryCloseCon(movedataSql);

				
				TvGrantpaymsgmainDto mainDto = new TvGrantpaymsgmainDto();
				mainDto.setSorgcode(sorgcode);
				mainDto.setScommitdate(filetime);
				mainDto.setSaccdate(currentDate);
				mainDto.setStrecode(strecode);
				mainDto.setSfilename(fileResultDto.getSfilename());
				List<TvGrantpaymsgmainDto> mainDtoList = CommonFacade.getODB().findRsByDto(mainDto);
				for (TvGrantpaymsgmainDto main : mainDtoList) {
					SQLExecutor subsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
					String movesubdataSql = "insert into "
							+ TvGrantpaymsgsubDto.tableName()
							+ " (I_VOUSRLNO,I_DETAILSEQNO,S_ORGCODE,S_FILENAME,S_LIMITID,S_OFYEAR,S_LINE,S_PACKAGENO,S_DEALNO,S_PACKAGETICKETNO,S_BUDGETUNITCODE,S_BUDGETTYPE,S_FUNSUBJECTCODE,N_MONEY,S_ACCATTRIB,S_STATUS,S_USERCODE,S_ACCDATE,TS_SYSUPDATE) "
							+ "  select main.I_VOUSRLNO,row_number() over(),sub.S_ORGCODE,sub.S_FILENAME,sub.S_LIMITID,sub.S_OFYEAR,sub.S_LINE,sub.S_PACKAGENO,sub.S_DEALNO,sub.S_LIMITID,sub.S_BUDGETUNITCODE,'"
							+ sbdgkind
							+ "',sub.S_FUNSUBJECTCODE,sub.N_MONEY,'"
							+ MsgConstant.AMT_KIND_BANK
							+ "','"
							+ DealCodeConstants.DEALCODE_ITFE_DEALING
							+ "','"
							+ susercode
							+ "','"
							+ currentDate
							+ "',CURRENT TIMESTAMP"
							+ "  from "
							+ TvGrantpayfilesubTmpDto.tableName()
							+ " sub ,"
							+ TvGrantpaymsgmainDto.tableName()
							+ " main "
							+ " where sub.S_ORGCODE = ? and sub.S_FILENAME = ? and main.I_VOUSRLNO = ? and main.S_BUDGETUNITCODE = sub.S_BUDGETUNITCODE and main.S_DEALNO = sub.S_DEALNO and "
							+ " sub.S_ORGCODE = main.S_ORGCODE and sub.S_FILENAME = main.S_FILENAME and sub.S_LIMITID = main.S_LIMITID and sub.S_OFYEAR = main.S_OFYEAR ";

					subsqlExec.addParam(sorgcode);
					subsqlExec.addParam(fileResultDto.getSfilename());
					subsqlExec.addParam(main.getIvousrlno()); 
					subsqlExec.runQueryCloseCon(movesubdataSql);
				}

				// 遍历包流水号,生成包流水号条件
				StringBuffer packbuf = new StringBuffer();
				for (int i = 0; i < fileResultDto.getPacknos().size(); i++) {
					if (i == fileResultDto.getPacknos().size() - 1) {
						packbuf.append("'" + fileResultDto.getPacknos().get(i) + "'");
					} else {
						packbuf.append("'" + fileResultDto.getPacknos().get(i) + "',");
					}
				}

				// 第 六.二 步 将已经重发的记录修改状态为已重发
				SQLExecutor updatesqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				String updatesql = " update "
						+ TvGrantpaymsgsubDto.tableName()
						+ " msg set msg.S_STATUS = ? "
						+ " where exists(select 1 from "
						+ TvGrantpaymsgsubDto.tableName()
						+ " tmp where msg.S_ORGCODE = ? and msg.S_FILENAME = ? and "
						+ " msg.S_ORGCODE = tmp.S_ORGCODE and msg.S_FILENAME = tmp.S_FILENAME and msg.S_LIMITID = tmp.S_LIMITID and msg.S_OFYEAR = tmp.S_OFYEAR and "
						+ " msg.S_LINE = tmp.S_LINE and msg.S_STATUS = ? and tmp.S_PACKAGENO in (" + packbuf + ") )";
				updatesqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_REPEAT_SEND);
				updatesqlExec.addParam(sorgcode);
				updatesqlExec.addParam(fileResultDto.getSfilename());
				updatesqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);
				updatesqlExec.runQueryCloseCon(updatesql);

				// 查找要重发数据的包
				SQLExecutor packsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				String packsql = "select distinct S_PACKAGENO from " + TvGrantpaymsgmainDto.tableName()
						+ " where S_ORGCODE = ? and S_FILENAME = ? and S_PACKAGENO in (";
				packsqlExec.addParam(sorgcode);
				packsqlExec.addParam(fileResultDto.getSfilename());
				SQLResults rs = packsqlExec.runQueryCloseCon(packsql + packbuf + ")");
				int packcount = rs.getRowCount();
				if (packcount <= 0) {
					logger.error("没有找到要发送的报文记录,请确认!");
					throw new ITFEBizException("没有找到要发送的报文记录,请确认!");
				}
				List<String> packlist = new ArrayList<String>();
				for (int i = 0; i < packcount; i++) {
					packlist.add(rs.getString(i, 0));
				}

				fileResultDto.setPacknos(packlist);
			} else {
				// 文件是第一次导入.直接将数据插入数据库处理.
				// 第七步 将临时表的记录取出然后存储到正式表
				SQLExecutor sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				String movedataSql = "insert into "
						+ TvGrantpaymsgmainDto.tableName()
						+ " (I_VOUSRLNO,S_ORGCODE,S_LIMITID,S_COMMITDATE,S_ACCDATE,S_FILENAME,S_TRECODE,S_PACKAGENO,S_PAYUNIT,S_TRANSBANKCODE,N_MONEY,S_PACKAGETICKETNO,"
						+ "	S_GENTICKETDATE,S_OFYEAR,S_OFMONTH,S_TRANSACTUNIT,S_AMTTYPE,S_USERCODE,S_STATUS,S_DEALNO,S_BUDGETUNITCODE,S_BUDGETTYPE,TS_SYSUPDATE) "
						+ " select next value for ITFE_GRANTPAYSEQ,main.S_ORGCODE,main.S_LIMITID,'"
						+ filetime
						+ "','"
						+ currentDate
						+ "',main.S_FILENAME,'"
						+ strecode
						+ "',sub.S_PACKAGENO,'"
						+ sfincode
						+ "','"
						+ sbankno
						+ "',sub.N_MONEY,main.S_LIMITID,main.S_GENTICKETDATE,main.S_OFYEAR,main.S_OFMONTH,' "
						+ sagentbank
						+ "','"
						+ MsgConstant.AMT_KIND_BANK
						+ "','"
						+ susercode
						+ "','"
						+ DealCodeConstants.DEALCODE_ITFE_DEALING
//						+ "',RIGHT(TRIM(CHAR('0000000'||CHAR(next value for ITFE_DEALNO_SEQUENCE))),8),"
						+ "',sub.S_DEALNO,sub.S_BUDGETUNITCODE,'"
						+ sbdgkind
						+ "',CURRENT TIMESTAMP"
						+ "  from "
						+ TvGrantpayfilemainTmpDto.tableName()
						+ " main,(select S_PACKAGENO as S_PACKAGENO, sum(N_MONEY) as N_MONEY , S_LIMITID as S_LIMITID,S_OFYEAR as S_OFYEAR ,S_ORGCODE as S_ORGCODE ,S_FILENAME as S_FILENAME,S_BUDGETUNITCODE as S_BUDGETUNITCODE,S_DEALNO as S_DEALNO from "
						+ TvGrantpayfilesubTmpDto.tableName()
						+ " where S_ORGCODE = ? and S_FILENAME = ? group by S_ORGCODE,S_FILENAME,S_PACKAGENO,S_LIMITID,S_OFYEAR,S_BUDGETUNITCODE,S_DEALNO) "
						+ " sub "
						+ " where main.S_ORGCODE = ? and main.S_FILENAME = ? and "
						+ " main.S_ORGCODE = sub.S_ORGCODE and main.S_FILENAME = sub.S_FILENAME and main.S_LIMITID = sub.S_LIMITID and main.S_OFYEAR  = sub.S_OFYEAR ";
				sqlExec.addParam(sorgcode);
				sqlExec.addParam(fileResultDto.getSfilename());
				sqlExec.addParam(sorgcode);
				sqlExec.addParam(fileResultDto.getSfilename());
				sqlExec.runQueryCloseCon(movedataSql);

				
				TvGrantpaymsgmainDto mainDto = new TvGrantpaymsgmainDto();
				mainDto.setSorgcode(sorgcode);
				mainDto.setScommitdate(filetime);
				mainDto.setSaccdate(currentDate);
				mainDto.setStrecode(strecode);
				mainDto.setSfilename(fileResultDto.getSfilename());
				List<TvGrantpaymsgmainDto> mainDtoList = CommonFacade.getODB().findRsByDto(mainDto);
				for (TvGrantpaymsgmainDto main : mainDtoList) {
					SQLExecutor subsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
					String movesubdataSql = "insert into "
							+ TvGrantpaymsgsubDto.tableName()
							+ " (I_VOUSRLNO,I_DETAILSEQNO,S_ORGCODE,S_FILENAME,S_LIMITID,S_OFYEAR,S_LINE,S_PACKAGENO,S_DEALNO,S_PACKAGETICKETNO,S_BUDGETUNITCODE,S_BUDGETTYPE,S_FUNSUBJECTCODE,N_MONEY,S_ACCATTRIB,S_STATUS,S_USERCODE,S_ACCDATE,TS_SYSUPDATE) "
							+ "  select main.I_VOUSRLNO,row_number() over(),sub.S_ORGCODE,sub.S_FILENAME,sub.S_LIMITID,sub.S_OFYEAR,sub.S_LINE,sub.S_PACKAGENO,sub.S_DEALNO,sub.S_LIMITID,sub.S_BUDGETUNITCODE,'"
							+ sbdgkind 
							+ "',sub.S_FUNSUBJECTCODE,sub.N_MONEY,'" 
							+ MsgConstant.ACCT_PROP_ZERO + "','"
							+ DealCodeConstants.DEALCODE_ITFE_DEALING 
							+ "','" 
							+ susercode 
							+ "','" 
							+ currentDate 
							+ "',CURRENT TIMESTAMP from "
							+ TvGrantpayfilesubTmpDto.tableName() 
							+ " sub ," 
							+ TvGrantpaymsgmainDto.tableName()	
							+ " main " 
							+ " where sub.S_ORGCODE = ? and sub.S_FILENAME = ? and main.I_VOUSRLNO = ? and main.S_BUDGETUNITCODE = sub.S_BUDGETUNITCODE and main.S_DEALNO = sub.S_DEALNO and "
							+ " main.S_ORGCODE = sub.S_ORGCODE and main.S_FILENAME = sub.S_FILENAME and main.S_LIMITID = sub.S_LIMITID and main.S_OFYEAR  = sub.S_OFYEAR ";
					subsqlExec.addParam(sorgcode);
					subsqlExec.addParam(fileResultDto.getSfilename());
					subsqlExec.addParam(main.getIvousrlno());
					subsqlExec.runQueryCloseCon(movesubdataSql);
				}
			}
			
			// 按照条件发送报文
			for (int i = 0; i < fileResultDto.getPacknos().size(); i++) {
				this.sendMsg(fileResultDto.getSfilename(), sorgcode, (String) (fileResultDto.getPacknos().get(i)), MsgConstant.MSG_NO_5103+"_OUT",
						filetime, null , false);
			}

			// 删除临时表记录
			TvGrantpayfilesubTmpDto submpdto = new TvGrantpayfilesubTmpDto();
			submpdto.setSorgcode(sorgcode);
			submpdto.setSfilename(fileResultDto.getSfilename());
			
			TvGrantpayfilemainTmpDto maintmpdto = new TvGrantpayfilemainTmpDto();
			maintmpdto.setSorgcode(sorgcode);
			maintmpdto.setSfilename(fileResultDto.getSfilename());
			
			CommonFacade.getODB().deleteRsByDto(submpdto);
			CommonFacade.getODB().deleteRsByDto(maintmpdto);
		} catch (FileOperateException e) {
			logger.error("文件上载的时候出现异常!", e);
			throw new ITFEBizException("文件上载的时候出现异常!", e);
		} catch (JAFDatabaseException e) {
			logger.error("数据搬运的时候出现数据库异常!", e);
			throw new ITFEBizException("数据搬运的时候出现数据库异常!", e);
		} catch (ValidateException e) {
			logger.error("删除临时表记录的时候出现数据库异常!", e);
			throw new ITFEBizException("删除临时表记录的时候出现数据库异常!", e);
		} catch (SequenceException e) {
			logger.error("获取授权支付额度凭证序列号失败!", e);
			throw new ITFEBizException("获取授权支付额度凭证序列号失败!", e);
		}
	}
}

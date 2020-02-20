package com.cfcc.itfe.msgmanager.service;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractServiceManagerServer;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.security.DESPlus;
import com.cfcc.itfe.util.ParserXml;
import com.cfcc.itfe.verify.VerifyFileName;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

public class PayOutServiceImpl extends AbstractServiceManagerServer {
	
	private static Log logger = LogFactory.getLog(PayOutServiceImpl.class);
	
	/**
	 * 处理从客户端传送过来的文件对象(实拨资金业务处理)
	 * 
	 * @param FileResultDto
	 *            fileResultDto 文件结果集对象DTO
	 * @param String
	 *            sorgcode 机构代码
	 * @param String
	 *            susercode 用户代码
	 * @throws ITFEBizException
	 */
	public void dealFileDto(FileResultDto fileResultDto, String sorgcode,
			String susercode) throws ITFEBizException {

		try {
			// 第一步校验导入文件的重复性
			boolean repeatflag = VerifyFileName.verifyImportRepeat(sorgcode, fileResultDto.getSfilename());
			if (repeatflag) {
				// 是重复导入,提示用户错误信息.
				logger.error("此文件[" + fileResultDto.getSfilename() + "]已经导入成功,不能重复导入!");
				throw new ITFEBizException("此文件[" + fileResultDto.getSfilename() + "]已经导入成功,不能重复导入!");
			}
			
			FileObjDto fileobj = PublicSearchFacade.getFileObjByFileName(fileResultDto.getSfilename());
			
			// 第一步 得到报文内容
			String msgcontent = fileResultDto.getSmaininfo();
			
			// 第二步 取得机构密钥(密文-明文)
			TsInfoconnorgDto keydto = PublicSearchFacade.findOrgKeyDto(sorgcode, fileobj.getStaxorgcode());
			String encryptKey = keydto.getSkey();
			DESPlus deskey = new DESPlus();
			String key = deskey.decrypt(encryptKey); 
			
			// 第三步 对报文进行接密码
			DESPlus desmsg = new DESPlus(key);
			String msg = null;
			try{
				msg = desmsg.decrypt(msgcontent);
			}catch(Exception e)
			{
				logger.error("实拨资金解密失败！", e);
				throw new ITFEBizException("实拨资金解密失败！", e);
			}
			
			// 第四步 将解密后的文件加载到服务器
			String currentDate = TimeFacade.getCurrentStringTime(); // 当前系统的时间
			String dirsep = File.separator; // 取得系统分割符
			String tmpfilepath = ITFECommonConstant.FILE_ROOT_PATH + currentDate + dirsep + sorgcode + dirsep + fileResultDto.getSfilename();
			FileUtil.getInstance().writeFile(tmpfilepath, msg);
			
			ParserXml parse = new ParserXml();
			List list = parse.parser5101Xml(tmpfilepath, sorgcode, fileResultDto.getSfilename(), susercode);
			
			BigDecimal inputamt = fileResultDto.getFsumamt(); // 界面录入总金额
			BigDecimal fileamt = (BigDecimal) list.get(1); // 文件内容中总金额
			
			if(inputamt.compareTo(fileamt) != 0){
				logger.error("录入文件金额[" + inputamt +"]与文件实际金额[" + fileamt +"]不相等,请确认!");
				throw new ITFEBizException("录入文件金额[" + inputamt +"]与文件实际金额[" + fileamt +"]不相等,请确认!");
			}
			
			// 第四步  保存记录
			TvFilepackagerefDto packdto = new TvFilepackagerefDto();
			packdto.setSorgcode(sorgcode); // 机构代码
			packdto.setStrecode(fileobj.getStrecode()); // 国库代码
			packdto.setSfilename(fileResultDto.getSfilename()); // 导入文件名
			packdto.setStaxorgcode(fileobj.getStaxorgcode()); // 机关代码
			packdto.setScommitdate(fileobj.getSdate()); // 委托日期
			packdto.setSaccdate(TimeFacade.getCurrentStringTime()); // 账务日期
			packdto.setSpackageno(fileobj.getSpackno()); // 包流水号
			packdto.setSoperationtypecode(BizTypeConstant.BIZ_TYPE_PAY_OUT); // 业务类型
			packdto.setIcount((Integer) list.get(0)); // 总笔数
			packdto.setNmoney(fileamt); // 总金额
			packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_DEALING); // 处理中
			packdto.setSusercode(susercode); // 操作员代码
			packdto.setImodicount(1);
			
			List<TvPayoutmsgmainDto> mainlist = (List<TvPayoutmsgmainDto>) list.get(2);
			List<TvPayoutmsgsubDto> sublist = (List<TvPayoutmsgsubDto>) list.get(3);
			
			TvPayoutmsgmainDto[] mains = new TvPayoutmsgmainDto[mainlist.size()];
			mains = mainlist.toArray(mains);
			
			TvPayoutmsgsubDto[] subs = new TvPayoutmsgsubDto[sublist.size()];
			subs = sublist.toArray(subs);

			try {
				DatabaseFacade.getDb().create(mains);
				//收款行行号进行校验：银行账号表中不存在是系统报错。
				VerifyParamTrans.verifyBankno(sorgcode, fileResultDto.getSfilename());
				DatabaseFacade.getDb().create(subs);
				
				//DatabaseFacade.getDb().create(packdto);
			} catch (JAFDatabaseException e) {
				logger.error("保存实拨资金业务信息表的时候出现异常！", e);
				throw new ITFEBizException("保存保存实拨资金业务信息表的时候出现异常！", e);
			}
			
			// 第五步 按照接收行行号分组重新组包,记录发送报文日志,更新业务主表的包流水号
			List<TvFilepackagerefDto> packlist = new ArrayList<TvFilepackagerefDto>();
			String selectSQL = "select COUNT(N_MONEY),SUM(N_MONEY),S_PAYEEBANKNO from TV_PAYOUTMSGMAIN where "
					+ "S_FILENAME = ? and S_ORGCODE = ? group by S_PAYEEBANKNO";

			String updateSQL = "update TV_PAYOUTMSGMAIN  set S_PACKAGENO = ? where "
				+ "S_FILENAME = ? and S_ORGCODE = ? and S_PAYEEBANKNO = ?";
			SQLExecutor sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.addParam(fileResultDto.getSfilename());
			sqlExec.addParam(sorgcode);
			SQLResults sqlRs = sqlExec.runQueryCloseCon(selectSQL);
			int count = sqlRs.getRowCount();
			String spackno = "";
			SQLExecutor updatesqlExec = null;
			for (int i = 0; i < count; i++) {
				TvFilepackagerefDto packetdto = new TvFilepackagerefDto();
				packetdto = (TvFilepackagerefDto)packdto.clone();
				packetdto.setIcount(sqlRs.getInt(i, 0));
				packetdto.setNmoney(sqlRs.getBigDecimal(i, 1));
				spackno =  String.format("%03d", i+1);
				spackno = spackno + packdto.getSpackageno().substring(3);
				packetdto.setSpackageno(spackno);
				packlist.add(packetdto);
				
				//第六步 更新实拨资金主表的包流水号，与包头信息相对应
				updatesqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				updatesqlExec.addParam(spackno);
				updatesqlExec.addParam(fileResultDto.getSfilename());
				updatesqlExec.addParam(sorgcode);
				updatesqlExec.addParam(sqlRs.getString(i, 2));
				updatesqlExec.runQuery(updateSQL);
			}
			try {
				TvFilepackagerefDto packetdtos[] = new TvFilepackagerefDto[packlist.size()];
				packetdtos = packlist.toArray(packetdtos);
				DatabaseFacade.getDb().create(packetdtos);
			} catch (JAFDatabaseException e) {
				logger.error("保存报文头的时候出现异常！", e);
				throw new ITFEBizException("保存报文头的时候出现异常！", e);
			}finally {
				if (null != updatesqlExec) {
					updatesqlExec.closeConnection();
				}
			}
			
			// 第四步 发送报文
//			this.sendMsg(fileResultDto.getSfilename(), sorgcode, null, MsgConstant.MSG_NO_5101, null, msg ,false);
		} catch (Exception e) {
			logger.error("处理实拨资金业务时出现异常!" , e );
			throw new ITFEBizException("处理实拨资金业务时出现异常!" , e );
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

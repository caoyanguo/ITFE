package com.cfcc.itfe.service.sendbiz.exporttbsforbj;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TimerVoucherInfoDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
/**
 * @author hua
 * @time   14-09-10 10:11:07
 * 部分方法是从系统原功能 '导出TBS文件' 拷贝代码过来稍作修改
 */

@SuppressWarnings("unchecked")
public class ExportTBSForBJService extends AbstractExportTBSForBJService {
	private static Log log = LogFactory.getLog(ExportTBSForBJService.class);

	/**为客户端提供查询数据服务**/
	public List exportTBS(String bizType, String treCode, Date acctDate,
			String trimFlag, Object backParam) throws ITFEBizException {
		List<IDto> list = new ArrayList<IDto>();
		try {
			if (bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_PAY_OUT)) {// 实拨资金
				ArrayList params = new ArrayList();
				String where = " where  S_TRECODE = ?";
				params.add(treCode);
				if (null != acctDate) {
					where += " AND  S_ACCDATE = ? ";
					params.add(TimeFacade.formatDate(acctDate, "yyyyMMdd"));
				}
				if (null != trimFlag) {
					where += " AND  S_TRIMFLAG = ? ";
					params.add(trimFlag);
				}
				where += " AND S_BIZNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE= ? AND S_STATUS= ? )";
				params.add(MsgConstant.VOUCHER_NO_5207);
				params.add(DealCodeConstants.VOUCHER_CHECK_SUCCESS);
				
				list = DatabaseFacade.getODB().findWithUR(
						TvPayoutmsgmainDto.class, where, params);

			} else if (bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN)) {// 生成直接支付额度
				ArrayList params = new ArrayList();
				String where = " where  S_TRECODE = ?";
				params.add(treCode);

				if (null != acctDate) {
					where += " AND  S_ACCDATE = ? ";
					params.add(TimeFacade.formatDate(acctDate, "yyyyMMdd"));
				}
				
				where += " AND I_VOUSRLNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE= ? AND S_STATUS= ? )";
				params.add(MsgConstant.VOUCHER_NO_5108);
				params.add(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
				
				list = DatabaseFacade.getODB().findWithUR(
						TvDirectpaymsgmainDto.class, where, params);

			} else if (bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN)) {//授权支付额度
				ArrayList params = new ArrayList();
				String where = " where  S_TRECODE = ?";
				params.add(treCode);

				if (null != acctDate) {
					where += " AND  S_ACCDATE = ? ";
					params.add(TimeFacade.formatDate(acctDate, "yyyyMMdd"));
				}
				
				where += " AND I_VOUSRLNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE= ? AND S_STATUS= ? )";
				params.add(MsgConstant.VOUCHER_NO_5106);
				params.add(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
				
				list = DatabaseFacade.getODB().findWithUR(
						TvGrantpaymsgmainDto.class, where, params);

			} else if (bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)
					|| bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)) {// 集中支付划款申请
				ArrayList params = new ArrayList();
				String where = " where  S_TRECODE = ?";
				params.add(treCode);

				if (null != acctDate) {
					where += " AND  D_VOUDATE = ? ";
					params.add(acctDate);
				}
				if (null != trimFlag) {
					where += " AND  S_TRIMSIGN = ? ";
					params.add(trimFlag);
				}
				where += " AND  S_PAYTYPECODE = ? ";
				if (bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)){
					params.add("11");
				}
				if (bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)){
					params.add("12");
				}
				where += " AND I_VOUSRLNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE= ? AND S_STATUS= ? )";
				params.add(MsgConstant.VOUCHER_NO_2301);
				params.add(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
				
				list = DatabaseFacade.getODB().findWithUR(
						TvPayreckBankDto.class, where, params);
			} else if (bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)
					|| bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) {// 集中支付划款申请退回
				ArrayList params = new ArrayList();
				String where = " where  S_TRECODE = ?";
				params.add(treCode);

				if (null != acctDate) {
					where += " AND  D_VOUDATE = ? ";
					params.add(acctDate);
				}
				if (null != trimFlag) {
					where += " AND  S_TRIMSIGN = ? ";
					params.add(trimFlag);
				}
				where += " AND  S_PAYTYPECODE = ? ";
				if (bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)){
					params.add("11");
				}
				if (bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
					params.add("12");
				}
				where += " AND I_VOUSRLNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE= ? AND S_STATUS= ? )";
				params.add(MsgConstant.VOUCHER_NO_2302);
				params.add(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
				
				list = DatabaseFacade.getODB().findWithUR(
						TvPayreckBankBackDto.class, where, params);
			} else if (bizType.equalsIgnoreCase(BizTypeConstant.BIZ_TYPE_RET_TREASURY)){ //退库
				ArrayList params = new ArrayList();
				String where = " where  S_PAYERTRECODE = ?";
				params.add(treCode);

				if (null != acctDate) {
					where += " AND  D_ACCEPT = ? ";
					params.add(acctDate);
				}
				if (null != trimFlag) {
					where += " AND  C_TRIMFLAG = ? ";
					params.add(trimFlag);
				}
				where += " AND I_VOUSRLNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE= ? AND S_STATUS= ? )";
				params.add(MsgConstant.VOUCHER_NO_5209);
				params.add(DealCodeConstants.VOUCHER_CHECK_SUCCESS);
				
				list = DatabaseFacade.getODB().findWithUR(
						TvDwbkDto.class, where, params);
			} else {
				throw new RuntimeException("导入功能暂不支持该业务类型数据导入["+bizType+"]!");
			}
		} catch (JAFDatabaseException e) {
			log.error("查询数据异常(导出TBS文件-北京区县)\n" + e.getMessage());
			throw new ITFEBizException("业务处理异常:" + e.getCause().getMessage(), e);
		}
		return list;
	}
	
	/**导入TBS回执**/
	public MulitTableDto importTBS(List fileList, Object param) throws ITFEBizException {
		if (fileList == null || fileList.size() == 0) {
			throw new RuntimeException("上传至服务端的文件列表为空!");
		}
		List<String> filePathList = fileList;
		MulitTableDto resultDto = new MulitTableDto();
		resultDto.setTotalCount(fileList.size());
		try {
			for (String fileName : filePathList) {
				
				/** 1.解析文件路径，得到文件业务类型 **/
				String fileType = BasicTBSFileProcesser.resolveFileType(fileName);
				
				/** 2.根据文件业务类型实例化相应的处理类进行处理 **/
				IProcessHandler processer = BasicTBSFileProcesser.generateProcesser(fileType);
				
				/** 3.初始化处理器结果对象 **/
				MulitTableDto processResult = null;
				
				try {
					/** 4.让处理器开始处理文件 **/
					processResult = processer.process(BasicTBSFileProcesser.getServerFilePath(fileName));
					
					/** 5.没有出现exception异常的情况下,处理业务相关的错误 **/
					BasicTBSFileProcesser.copyProcessRes2Result(fileName,processResult,resultDto);
					
				} catch (Exception e) {
					/** 6.出现异常,则记录问题信息,继续处理下一个文件 **/
					if(processResult!=null && processResult.getErrorList().size() > 0){
						resultDto.getErrorList().addAll(processResult.getErrorList());
					}
					resultDto.getErrNameList().add(new File(fileName).getName());
					resultDto.setErrorCount(resultDto.getErrorCount() + 1);
					log.error(e);
					continue;
				}
			}
		} catch (Exception e) {
			log.error(e);
			throw new RuntimeException("导入TBS回执出现异常!",e);
		} 
		return resultDto;
	}

	/**根据主表信息查找字表**/
	public List findSubInfoByMain(IDto mainDto) throws ITFEBizException {
		List list = new ArrayList();
		try {
			if (mainDto instanceof TvPayreckBankDto) {// 划款申请
				TvPayreckBankDto dto = (TvPayreckBankDto) mainDto;
				list = DatabaseFacade.getDb().find(TvPayreckBankListDto.class,
						" where  I_VOUSRLNO = " + dto.getIvousrlno());
			} else if (mainDto instanceof TvPayreckBankBackDto) {// 划款申请退回
				TvPayreckBankBackDto dto = (TvPayreckBankBackDto) mainDto;
				list = DatabaseFacade.getDb().find(TvPayreckBankBackListDto.class,
						" where  I_VOUSRLNO = " + dto.getIvousrlno());
			} else if (mainDto instanceof TvGrantpaymsgmainDto) {// 授权支付额度
				TvGrantpaymsgmainDto dto = (TvGrantpaymsgmainDto) mainDto;
				list = DatabaseFacade.getDb().find(TvGrantpaymsgsubDto.class,
						" where  I_VOUSRLNO = " + dto.getIvousrlno() + " and S_PACKAGETICKETNO = '"+dto.getSpackageticketno()+"' ");//拆包后主键
			} else if (mainDto instanceof TvDirectpaymsgmainDto) {// 直接支付额度
				TvDirectpaymsgmainDto dto = (TvDirectpaymsgmainDto) mainDto;
				list = DatabaseFacade.getDb().find(TvDirectpaymsgsubDto.class,
						" where  I_VOUSRLNO = " + dto.getIvousrlno());
			} else if (mainDto instanceof TvPayoutmsgmainDto) {// 实拨资金
				TvPayoutmsgmainDto dto = (TvPayoutmsgmainDto) mainDto;
				list = DatabaseFacade.getDb().find(TvPayoutmsgsubDto.class,
						" where  S_BIZNO = '" + dto.getSbizno() + "'");
			}
		} catch (JAFDatabaseException e) {
			log.error("查询数据异常(导出TBS文件-北京区县)\n" + e.getMessage());
			throw new ITFEBizException("查询数据异常:" + e.getCause().getMessage(), e);
		}
		return list;
	}
	
	/**导出TBS格式文件之后更新凭证状态**/
	public void updateVouStatus(List list) throws ITFEBizException {
		String updateStatus = DealCodeConstants.VOUCHER_SENDED;
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			List<IDto> updateList = list;
			String sql = "UPDATE TV_VOUCHERINFO SET S_STATUS=?,S_DEMO=? WHERE S_DEALNO = ? ";
			conn = BasicTBSFileProcesser.getConnection();
			conn.setAutoCommit(false); //自己控制事务,要么全部更新成功,要么全部不更新
			st = conn.prepareStatement(sql);
			for(int i=0; updateList!=null && updateList.size()>0 && i<updateList.size(); i++) {
				IDto dto = updateList.get(i);
				st.setString(1, updateStatus);
				st.setString(2, "已发送");
				if(dto instanceof TvPayoutmsgmainDto) {
					st.setString(3, BeanUtils.getProperty(dto, "sbizno")+"");
				} else {
					st.setString(3, BeanUtils.getProperty(dto, "ivousrlno")+"");
				}
				st.addBatch();
			}
			st.executeBatch();
			conn.commit();
		} catch (Exception e) {
			if(conn!=null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					log.error("数据回滚失败(TBS文件导出)!",e);
				}
			}
			log.error(e);
			throw new RuntimeException("更新凭证状态异常(TBS文件导出)!",e);
		} finally {
			BasicTBSFileProcesser.releaseConnection(conn, st, rs);
		}
	}

	public void deleteServerFile(String fileName) throws ITFEBizException {
		//删除错误文件
		try {
			if(new File(fileName).exists()) {
				FileUtil.getInstance().deleteFile(fileName);
			}
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("删除服务器错误文件操作异常", e);
		} catch (FileNotFoundException e) {
			log.error(e);
		} 
	}

	/**保存文件导入导出日志信息*/
	public void writeTbsFileLog(List logList) throws ITFEBizException {
		if(isNotBlankList(logList)){
			try {
				for(Object obj : logList) {
					TvSendlogDto dto = (TvSendlogDto) obj;
					// 取发送日志流水
					dto.setSsendno(StampFacade.getStampSendSeq("FS"));
					// 发送机构代码
					dto.setSsendorgcode(getLoginInfo().getSorgcode());
					// 接收结构代码
					dto.setSrecvorgcode(getLoginInfo().getSorgcode());
					// 上传日期
					dto.setSdate(DateUtil.date2String2(TSystemFacade.findDBSystemDate()));
					// 发送时间
					dto.setSsendtime(TSystemFacade.getDBSystemTime());
					// 处理时间
					dto.setSproctime(TSystemFacade.getDBSystemTime());
					// 接收日期
					dto.setSsenddate(TimeFacade.getCurrentStringTime());
					// 处理码说明
					dto.setSretcodedesc("导出TBS格式文件成功");
					dto.setSifsend(StateConstant.MSG_SENDER_FLAG_3);// 文件方式
					dto.setSturnsendflag(StateConstant.SendFinNo);// 转发标志
					dto.setSdemo("导出TBS格式文件成功");
				}
				DatabaseFacade.getODB().create(CommonUtil.listTArray(logList));
			} catch (Exception e) {
				log.error("记录文件导出日志异常(北京区县)!",e);
			}
		}
	}
	
	private static final String TIMER_INFO_TYPE1 = "'20'"; //校验成功
	private static final String TIMER_INFO_TYPE2 = "'17','40','45','50','60','61','71','73'"; //处理中
	private static final String TIMER_INFO_TYPE3 = "'10','15','16','30','62','72'"; //处理失败
	private static final String TIMER_INFO_TYPE4 = "'80','90','100'"; //已回单
	
	/**
	 * 为客户端 定时提醒凭证处理情况 提供数据访问服务(方法写在这只是为了找个地方搁而已)
	 */
	public Map fetchVoucherInfoForClientTimer(String startDate, String endDate, IDto paramDto, String backParamString) throws ITFEBizException {
		
		if(startDate == null || "".equals(startDate)) {
			startDate = TimeFacade.getCurrentStringTime();
		}
		if(endDate == null || "".equals(endDate)) {
			endDate = TimeFacade.getCurrentStringTime();
		}
		
		//sql中用了很多case，主要目的是为了行转列
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT  S_VTCODE AS SVTCODE,s_trecode as strecode,SBIZNAME,COALESCE(COUNT1,0) AS COUNT1,COALESCE(COUNT2,0) AS COUNT2,COALESCE(COUNT3,0) AS COUNT3,COALESCE(COUNT4,0) AS COUNT4 FROM (");
		sql.append("SELECT  S_VTCODE,s_trecode,SBIZNAME,");
		sql.append("		MAX(CASE SSTATUS WHEN '1' THEN SCOUNT END) AS COUNT1,");
		sql.append("		MAX(CASE SSTATUS WHEN '2' THEN SCOUNT END) AS COUNT2,");
		sql.append("		MAX(CASE SSTATUS WHEN '3' THEN SCOUNT END) AS COUNT3,");
		sql.append("		MAX(CASE SSTATUS WHEN '4' THEN SCOUNT END) AS COUNT4 ");
		sql.append("FROM ( ");
		sql.append("		SELECT  S_VTCODE ,s_trecode,");
		sql.append("                (SELECT S_VALUECMT FROM TD_ENUMVALUE WHERE S_TYPECODE=? AND S_VALUE=A.S_VTCODE) SBIZNAME,");
		sql.append("			    SSTATUS, ");
		sql.append("			    COUNT(*) SCOUNT  ");
		sql.append("		FROM ( ");
		sql.append("			  SELECT S_VTCODE,s_trecode,  ");
		sql.append("					CASE WHEN S_STATUS IN ("+TIMER_INFO_TYPE1+") THEN '1' ");
		sql.append("						 WHEN S_STATUS IN ("+TIMER_INFO_TYPE2+") THEN '2' ");
		sql.append("						 WHEN S_STATUS IN ("+TIMER_INFO_TYPE3+") THEN '3' ");
		sql.append("						 WHEN S_STATUS IN ("+TIMER_INFO_TYPE4+") THEN '4' ");
		sql.append("						 ELSE '2' ");
		sql.append("					END AS SSTATUS  ");
		sql.append("			  FROM TV_VOUCHERINFO  ");
		sql.append("			  WHERE S_CREATDATE BETWEEN ? AND ? AND S_ORGCODE=? AND S_STATUS IS NOT NULL AND S_VTCODE IS NOT NULL");
		sql.append("		) A WHERE EXISTS (SELECT S_VALUECMT FROM TD_ENUMVALUE WHERE S_TYPECODE='0419' AND S_VALUE=A.S_VTCODE) ");
		sql.append("	        GROUP BY A.S_VTCODE,A.SSTATUS,s_trecode ");
		sql.append("            ORDER BY A.S_VTCODE,A.SSTATUS ");
		sql.append(")");
		sql.append("GROUP BY SBIZNAME,S_VTCODE,s_trecode )");
		
		SQLExecutor sqlExec = null;
		SQLResults rs = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			if("sendvoucherinfocount".equals(startDate)&&paramDto instanceof TvVoucherinfoDto)
			{
				TvVoucherinfoDto sdto = (TvVoucherinfoDto)paramDto;
				String csql = "select s_vtcode AS svtcode,s_trecode as strecode,s_status as sbizname,sum(N_MONEY) as totalamt, count(*) as COUNT1,'' as COUNT2,'' as COUNT3,'' as COUNT4 from"
				+"(select * from tv_voucherinfo union all select * from htv_voucherinfo)"
				+" where s_trecode=? and s_creatdate >=? and s_creatdate<=? and s_vtcode "
				+((sdto.getSvtcode()==null||"".equals(sdto.getSvtcode()))?"in(SELECT S_VTCODE FROM TS_VOUCHERCOMMITAUTO WHERE S_ORGCODE=? AND S_TRECODE=? AND S_RETURBACKNAUTO=?)":"=?")
				+" group by s_vtcode,s_status,s_trecode";
				sqlExec.addParam(sdto.getStrecode());
				sqlExec.addParam(sdto.getShold1());
				sqlExec.addParam(sdto.getShold2());
				if(sdto.getSvtcode()==null||"".equals(sdto.getSvtcode()))
				{
					sqlExec.addParam(sdto.getSorgcode());
					sqlExec.addParam(sdto.getStrecode());
					sqlExec.addParam("0");
				}else
				{
					sqlExec.addParam(sdto.getSvtcode());
				}
				rs = sqlExec.runQueryCloseCon(csql, TimerVoucherInfoDto.class);
				if(rs != null && rs.getRowCount() > 0) {
					List<TimerVoucherInfoDto> list = (List<TimerVoucherInfoDto>) rs.getDtoCollection();
					Map<String, List<TimerVoucherInfoDto>> map = new HashMap<String, List<TimerVoucherInfoDto>>();
					map.put("timerResult", list);
					return map;
				} else {
					return Collections.emptyMap();
				}
			}else
			{
				sqlExec.addParam("0419");
				sqlExec.addParam(startDate);
				sqlExec.addParam(endDate);
				sqlExec.addParam(getLoginInfo().getSorgcode());
				rs = sqlExec.runQueryCloseCon(sql.toString(), TimerVoucherInfoDto.class);
				if(rs != null && rs.getRowCount() > 0) {
					List<TimerVoucherInfoDto> list = (List<TimerVoucherInfoDto>) rs.getDtoCollection();
					Map<String, List<TimerVoucherInfoDto>> map = new HashMap<String, List<TimerVoucherInfoDto>>();
					map.put("timerResult", list);
					return map;
				} else {
					return Collections.emptyMap();
				}
			}
		} catch (JAFDatabaseException e) {
			log.error("在获取客户端定时提醒功能所需数据时出现异常",e);
			throw new RuntimeException("数据获取异常!",e);
		}
	}
	
	private boolean isNotBlankList(List list) {
		return (list!=null)&&(list.size()>0);
	}
}
package com.cfcc.itfe.service.dataquery.querylogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvRecvLogShowDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.pk.TvSendlogPK;
import com.cfcc.itfe.util.ServiceUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author db2admin
 * @time 09-11-19 08:27:36 codecomment:
 */

public class QueryLogsService extends AbstractQueryLogsService {
	private static Log log = LogFactory.getLog(QueryLogsService.class);

	/**
	 * 查询
	 * 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List queryLog(IDto dto) throws ITFEBizException {
		return null;

	}

	public void result() throws ITFEBizException {
		// TODO Auto-generated method stub

	}

	public PageResponse queryLog(IDto dto, PageRequest pagerequest)
			throws ITFEBizException {
		TvSendlogDto tempdto = (TvSendlogDto) dto;
		PageResponse response = new PageResponse(pagerequest);
		try {
			SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String sql;

			if (tempdto.getSdemo().trim().equals(StateConstant.LOG_SEND)) {// 发送
				sql = "select a.s_sendno as sendno,a.S_DATE as sdate, a.S_recvorgcode as sorgcode,a.S_OPERATIONTYPECODE as soperationtypecode, a.S_SENDTIME as ssendtime ,"
						+ " a.S_TITLE as stitle ,a.S_DEMO as sdemo, '"
						+ tempdto.getSdemo()
						+ "'as sopertype,a.s_retcode "
						+ " from TV_SENDLOG a where a.S_SENDORGCODE = ?  ";
			} else {// 接收
				sql = "select a.s_sendno as sendno,a.S_DATE as sdate,b.S_ORGNAME as sorgcode,a.S_OPERATIONTYPECODE as soperationtypecode, a.S_RECVTIME as ssendtime ,"
						+ "a.S_TITLE as stitle, a.S_DEMO as sdemo ,'"
						+ tempdto.getSdemo()
						+ "'as sopertype,a.s_retcode "
						+ "from TV_RECVLOG a,TS_ORGan b where  a.S_RECVORGCODE = ?  and a.S_SENDORGCODE =  b.s_orgcode ";
			}
			exec.addParam(getLoginInfo().getSorgcode());
			if (null != tempdto.getSdate()
					&& tempdto.getSdate().trim().length() > 0) {
				sql = sql + " AND a.S_DATE =?";
				exec.addParam(tempdto.getSdate());
			}
			if (null != tempdto.getSoperationtypecode()
					&& tempdto.getSoperationtypecode().trim().length() > 0) {
				sql = sql + " AND a.S_OPERATIONTYPECODE=? ";
				exec.addParam(tempdto.getSoperationtypecode());
			}
			if(tempdto.getNmoney()!=null&&!"".equals(tempdto.getNmoney()))
				sql +=" and a.n_money="+tempdto.getNmoney();
			sql = sql + " order by ssendtime";
			SQLResults res = exec.runQueryCloseCon(sql, TvRecvLogShowDto.class,
					pagerequest.getStartPosition(), pagerequest.getPageSize(),
					false);
			List list = new ArrayList();
			list.addAll(res.getDtoCollection());
			response.getData().clear();
			response.setData(list);
			return response;
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询核算主体代码出错", e);
		}

	}

	/**
	 * 查询一条发送记录的所有附件信息
	 */
	public List<String> queryAttach(String sendNo) throws ITFEBizException {
		try {
			List<String> files = new ArrayList<String>();
			String sql = "select s_savepath from tv_files where s_no='"
					+ sendNo + "'";
			SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			SQLResults result = exec.runQueryCloseCon(sql);
			if (result != null) {
				int rowCount = result.getRowCount();
				for (int i = 0; i < rowCount; i++) {
					if ((result.getString(i, 0) != null)
							&& (result.getString(i, 0).length() > 0)) {
						files.add(result.getString(i, 0));
					}
				}
			}
			return files;
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询附件信息出错", e);
		}
	}

	/**
	 * 查询一条发送记录的详细内容
	 */
	public String queryContent(String sendNo) throws ITFEBizException {
		try {
			String content = "";
			String sql = "select distinct s_content from tv_files where s_no='"
					+ sendNo + "'";
			SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			SQLResults result = exec.runQueryCloseCon(sql);
			if (result != null) {
				if (result.getRowCount() == 0) {
					content = "";
				} else {
					content = result.getString(0, 0);
				}
			}
			return content;
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询详细内容出错", e);
		}
	}

	/**
	 * 查询报文收发日志
	 */
	public PageResponse queryMsgLog2(TvRecvlogDto finddto,
			PageRequest pageRequest) throws ITFEBizException {
		String opertype = finddto.getSdemo(); // 取得操作类型
		SQLExecutor bizexec = null;
		SQLExecutor countexec = null;
		try {
			bizexec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			countexec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String searchsql = "";
			String wheresql = "";
			String fromsql = "";
			String countsql = " select count(*) ";
			if (StateConstant.LOG_SEND.equals(opertype)) {
				searchsql = "select S_SENDORGCODE as S_RECVORGCODE,S_RECVNO as S_SENDNO,S_DATE,S_OPERATIONTYPECODE,S_TITLE,I_COUNT,N_MONEY,S_RETCODE,S_SENDTIME as S_RECVTIME,S_DEMO ";
				fromsql = " from " + TvSendlogDto.tableName() + " where 1 = 1 ";

			} else {
				searchsql = "select S_RECVORGCODE as S_RECVORGCODE,S_SENDNO as S_SENDNO,S_DATE,S_OPERATIONTYPECODE,S_TITLE,I_COUNT,N_MONEY,S_RETCODE,S_RECVTIME as S_RECVTIME,S_DEMO ";
				fromsql = " from " + TvRecvlogDto.tableName() + " where 1 = 1 ";
			}
			if(finddto.getNmoney()!=null&&!"".equals(finddto.getNmoney()))
				fromsql +=" and n_money="+finddto.getNmoney();
			if (null != finddto.getSoperationtypecode()
					&& !"".equals(finddto.getSoperationtypecode())) {
				wheresql = wheresql + " and S_OPERATIONTYPECODE = ? ";
				bizexec.addParam(finddto.getSoperationtypecode());
				countexec.addParam(finddto.getSoperationtypecode());
			}

			if (null != finddto.getSdate() && !"".equals(finddto.getSdate())) {
				wheresql = wheresql + " and S_DATE = ? ";
				bizexec.addParam(finddto.getSdate());
				countexec.addParam(finddto.getSdate());
			}

			PageResponse response = new PageResponse(pageRequest);
			List<?> list = new ArrayList();

			// 计算记录数
			SQLResults countRs = countexec.runQueryCloseCon(countsql + fromsql
					+ wheresql + " with ur");
			int count = countRs.getInt(0, 0);

			if (count == 0) {
				bizexec.closeConnection();
			} else {
				SQLResults dataRs = bizexec.runQueryCloseCon(searchsql
						+ fromsql + wheresql, TvRecvlogDto.class, pageRequest
						.getStartPosition(), pageRequest.getPageSize(), true);
				list.addAll(dataRs.getDtoCollection());
			}

			response.setTotalCount(count);
			response.getData().clear();
			response.setData(list);

			return response;
		} catch (JAFDatabaseException e) {
			log.error("查询报文收发日志出现异常!", e);
			throw new ITFEBizException("查询报文收发日志出现异常!", e);
		} finally {
			if (null != bizexec) {
				bizexec.closeConnection();
			}
			if (null != countexec) {
				countexec.closeConnection();
			}
		}
	}

	/**
	 * 作废已经发送的业务凭证
	 */
	public void cancelSend(TvRecvLogShowDto sendLog) throws ITFEBizException {
		try {
			TvSendlogPK pk = new TvSendlogPK();
			pk.setSsendno(sendLog.getSendno());
			TvSendlogDto sendDto = (TvSendlogDto) DatabaseFacade.getDb().find(
					pk);
			if (sendDto == null) {
				throw new ITFEBizException("找不到流水号为" + pk.getSsendno()
						+ "的发送日志。");
			}
			if (!sendDto.getSretcode().trim().equals(
					sendLog.getSretcode().trim())) {
				throw new ITFEBizException("您所作废的业务凭证已经被修改，请重新查询后再作废。");
			}
			// 修改发送日志和接收日志的处理标志
			String sql = "update tv_sendlog set s_retcode='"
					+ ITFECommonConstant.STATUS_CANCELED + "' where s_sendno='"
					+ pk.getSsendno() + "'";
			DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor()
					.runQueryCloseCon(sql);
			sql = "update tv_recvlog set s_retcode='"
					+ ITFECommonConstant.STATUS_CANCELED + "' where s_sendno='"
					+ pk.getSsendno() + "'";
			DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor()
					.runQueryCloseCon(sql);
		} catch (Exception e) {
			setRollbackOnly();
			log.error(e);
			throw new ITFEBizException("查询详细内容出错", e);
		}
	}

	/**
	 * 查询报文收发日志
	 */
	public PageResponse queryMsgLog(TvRecvlogDto finddto,
			PageRequest pageRequest,String startdate,String enddate) throws ITFEBizException {
		String opertype = finddto.getSdemo(); // 取得操作类型
		SQLExecutor bizexec = null;
		SQLExecutor countexec = null;
		try {
			bizexec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			countexec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String searchsql = "";
			String wheresql = "";
			String fromsql = "";
			String sortsql = "";
			String countsql = " select count(*) ";
			
			
			
			
			if (StateConstant.LOG_SEND.equals(opertype)) {
				searchsql = "select S_SENDNO , S_RECVNO,S_BillORG,S_PACKNO,S_DATE,S_SENDORGCODE,S_RECVORGCODE,S_SENDDATE,S_OPERATIONTYPECODE,S_TITLE,I_COUNT,N_MONEY,S_SEQ,coalesce(S_RETCODE,'') as S_RETCODE,S_SENDTIME as S_RECVTIME,S_DEMO,S_RETCODEDESC,S_PROCTIME,coalesce(S_IFSEND,'') as S_IFSEND,S_DEMO,S_TRECODE,S_RETCODEDESC";
				fromsql = " from " + TvSendlogDto.tableName() + " where 1 = 1 ";
				if (!getLoginInfo().getSorgcode().equals(StateConstant.ORG_CENTER_CODE) && !getLoginInfo().getSorgcode().equals(StateConstant.STAT_CENTER_CODE)) {
					wheresql+=" and  (S_SENDORGCODE ='"+this.getLoginInfo().getSorgcode()+"' or  S_SENDORGCODE='"+ITFECommonConstant.SRC_NODE+"')";
				} 
				
			} else {
				searchsql = "select S_SENDNO as S_RECVNO,S_RECVNO as S_SENDNO, S_BillORG,S_PACKNO,S_DATE,S_SENDORGCODE,S_RECVORGCODE ,S_SENDDATE,S_OPERATIONTYPECODE,S_TITLE,I_COUNT,N_MONEY,S_SEQ,coalesce(S_RETCODE,'') as S_RETCODE,S_RECVTIME,S_DEMO,S_RETCODEDESC,S_PROCTIME,coalesce(S_IFSEND,'') as S_IFSEND,S_DEMO,S_TRECODE,S_RETCODEDESC";
				fromsql = " from " + TvRecvlogDto.tableName() + " where 1 = 1 ";
				if (!getLoginInfo().getSorgcode().equals(StateConstant.ORG_CENTER_CODE) && !getLoginInfo().getSorgcode().equals(StateConstant.STAT_CENTER_CODE)) {
					wheresql+=" and (S_RECVORGCODE ='"+this.getLoginInfo().getSorgcode()+"' or S_RECVORGCODE='"+ITFECommonConstant.SRC_NODE+"' ) ";
				} 
				
			}
			if(finddto.getNmoney()!=null&&!"".equals(finddto.getNmoney()))
				fromsql +=" and n_money="+finddto.getNmoney();
			if (null != finddto.getSoperationtypecode()
					&& !"".equals(finddto.getSoperationtypecode())) {
				wheresql = wheresql + " and S_OPERATIONTYPECODE = ? ";
				bizexec.addParam(finddto.getSoperationtypecode());
				countexec.addParam(finddto.getSoperationtypecode());
			}

			/*if (null != finddto.getSsenddate()
					&& !"".equals(finddto.getSsenddate())) {
				wheresql = wheresql + " and S_SENDDATE = ? ";
				bizexec.addParam(finddto.getSsenddate());
				countexec.addParam(finddto.getSsenddate());
			}*/

			if (null != finddto.getStrecode()
					&& !"".equals(finddto.getStrecode())) {
				wheresql = wheresql + " and S_TRECODE = ? ";
				bizexec.addParam(finddto.getStrecode());
				countexec.addParam(finddto.getStrecode());
			}
			
			if (null != finddto.getSpackno()
					&& !"".equals(finddto.getSpackno())) {
				wheresql = wheresql + " and S_PACKNO = ? ";
				bizexec.addParam(finddto.getSpackno());
				countexec.addParam(finddto.getSpackno());
			}
			
			if (null != finddto.getSbillorg()
					&& !"".equals(finddto.getSbillorg())) {
				wheresql = wheresql + " and S_BILLORG = ? ";
				bizexec.addParam(finddto.getSbillorg());
				countexec.addParam(finddto.getSbillorg());
			}
			if (null != finddto.getSretcode()
					&& !"".equals(finddto.getSretcode())) {
				if (!DealCodeConstants.DEALCODE_ITFE_OTHER.equals(finddto.getSretcode())) {
					wheresql = wheresql + " and S_RETCODE = ? ";
					bizexec.addParam(finddto.getSretcode());
					countexec.addParam(finddto.getSretcode());
				}else{
					wheresql = wheresql + " and Substr(S_RETCODE,1,3) <> '800' AND  S_RETCODE <> '90000' ";
				}	
			}

			if (null != finddto.getSifsend()
					&& !"".equals(finddto.getSifsend())) {
				wheresql = wheresql + " and S_IFSEND = ? ";
				bizexec.addParam(finddto.getSifsend());
				countexec.addParam(finddto.getSifsend());
			}
			
			if (null != startdate && !"".equals(startdate)&&null != enddate && !"".equals(enddate)) {
				if(Integer.parseInt(startdate)>Integer.parseInt(enddate)){
					return null;
				}
				wheresql = wheresql + " and S_SENDDATE between '"+startdate+"' and '"+enddate+"'";
			}else if(null != startdate && !"".equals(startdate)){
				wheresql = wheresql + " and S_SENDDATE = '"+startdate+"'";
			}else if(null != enddate && !"".equals(enddate)){
				wheresql = wheresql + " and S_SENDDATE = '"+enddate+"'";
			}
			if (StateConstant.LOG_SEND.equals(opertype)) {
				sortsql=" order by S_SENDTIME desc ";
			} else {
				sortsql=" order by S_RECVTIME desc ";
			}
			

			PageResponse response = new PageResponse(pageRequest);
			List list = new ArrayList();

			// 计算记录数
			SQLResults countRs = countexec.runQueryCloseCon(countsql + fromsql
					+ wheresql + " with ur");
			int count = countRs.getInt(0, 0);

			if (count == 0) {
				bizexec.closeConnection();
			} else {
				SQLResults dataRs = bizexec.runQueryCloseCon(searchsql
						+ fromsql + wheresql+sortsql+" with ur ", TvRecvlogDto.class, pageRequest
						.getStartPosition(), pageRequest.getPageSize(), true);
				list.addAll(dataRs.getDtoCollection());
			}

			response.setTotalCount(count);
			response.getData().clear();
			response.setData(list);

			return response;
		} catch (JAFDatabaseException e) {
			log.error("查询报文收发日志出现异常!", e);
			throw new ITFEBizException("查询报文收发日志出现异常!", e);
		} finally {
			if (null != bizexec) {
				bizexec.closeConnection();
			}
			if (null != countexec) {
				countexec.closeConnection();
			}
		}
	}

	public String getFileRootPath() {
		return ITFECommonConstant.FILE_ROOT_PATH;
	}


	public void resendMsg(String _ssendno) throws ITFEBizException {
		try {
			TvSendlogPK _pk = new TvSendlogPK();
			_pk.setSsendno(_ssendno);
			TvSendlogDto sendlogdto = (TvSendlogDto) DatabaseFacade.getODB().find(_pk);
			String url = "";
			TsTreasuryDto streDto = new TsTreasuryDto();
			streDto.setStrecode(sendlogdto.getStrecode());
			List<TsTreasuryDto> streDtoList = CommonFacade.getODB().findRsByDto(streDto);
			if(streDtoList.get(0).getStreattrib().equals("2")){ //tbs重发
				url = "vm://ManagerMsgWithCommBank";
			}else{
				// 重发给财政,，不需要修改id
				if (MsgLogFacade.resendtofin(sendlogdto.getSifsend(),sendlogdto.getSturnsendflag())) {
					if(sendlogdto.getStrecode()!=null&&sendlogdto.getStrecode().startsWith("1702"))
						url = "vm://FromTipsResendMsgCity";
					else
						url = "vm://FromTipsResendMsg";
				} else {
					if(sendlogdto.getStrecode()!=null&&sendlogdto.getStrecode().startsWith("1702"))
						url = "vm://ToTipsResendMsgCity";
					else
						url = "vm://ToTipsResendMsg";
				}
			}
			MuleMessage message = new DefaultMuleMessage(sendlogdto);
			message.setPayload(sendlogdto);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY,MsgConstant.MSG_NO_RESEND);
			if(streDtoList.get(0).getStreattrib().equals("2")){ //tbs重发
				TvVoucherinfoDto vDto = new TvVoucherinfoDto();
				vDto.setStrecode(sendlogdto.getStrecode());
				vDto.setSext5("1");//表示重发的报文
				message.setProperty(MessagePropertyKeys.MSG_DTO, vDto);
			}
			MuleClient client = new MuleClient();
			message = client.send(url, message);
			ServiceUtil.checkResult(message);
		} catch (Exception e) {
			log.error("调用后台报文处理的时候出现异常!", e);
			throw new ITFEBizException("调用后台报文处理的时候出现异常!", e);
		}

	}
	/*
	 * 设置失败
	 * @see com.cfcc.itfe.service.dataquery.querylogs.IQueryLogsService#updateFail(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void updateFail(String sendNo, String pacackageNo, String msgno,String sdate)
			throws ITFEBizException {
		try {
			TvSendlogDto sendlogDto = new TvSendlogDto();
			sendlogDto.setSsendno(sendNo);
			List<TvSendlogDto> sendlogDtoList = CommonFacade.getODB().findRsByDto(sendlogDto);
			sendlogDto = sendlogDtoList.get(0);
			
			TsTreasuryDto streDto = new TsTreasuryDto();
			streDto.setStrecode(sendlogDto.getStrecode());
			List<TsTreasuryDto> streDtoList = CommonFacade.getODB().findRsByDto(streDto);
			
			if(streDtoList.get(0).getStreattrib().equals("2")){ //此分支用于代理库设置失败
				tbsUpdateFail(sendNo, pacackageNo, msgno, sdate);
				return;
			}
		} catch (JAFDatabaseException e1) {
			log.error("状态更新失败!", e1);
			throw new ITFEBizException("状态更新失败", e1);
		} catch (ValidateException e1) {
			log.error("状态更新失败!", e1);
			throw new ITFEBizException("状态更新失败", e1);
		}
		
		String ls_UpdateSendLog  = "update "+TvSendlogDto.tableName()+" set S_RETCODE = ? where S_SENDNO = ? and S_PACKNO = ? AND S_OPERATIONTYPECODE =? and  S_DATE = ? ";
		String updatePackageSql = " update "
			+ TvFilepackagerefDto.tableName()
			+ " set S_RETCODE = ? "
			+ " where S_COMMITDATE =? and S_PACKAGENO =?  ";

		if (msgno.equals(MsgConstant.MSG_NO_2201)){
			updatePackageSql+=" AND  S_OPERATIONTYPECODE  IN(?,'27')";
		} else if(msgno.equals(MsgConstant.MSG_NO_2202)){
			updatePackageSql+=" AND  S_OPERATIONTYPECODE  IN(?,'28')";
		} else {
			updatePackageSql+=" and S_OPERATIONTYPECODE = ? ";
		}
		/**
		 * 第一步 根据报文编号找到对应的业务类型
		 */
		String biztype = PublicSearchFacade.getBizTypeByMsgNo(msgno);
		/**
		 * 第二步 更新报表头信息
		 */
		try {
			//1.设置包状态
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			updateExec.clearParams();
			updateExec.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);
			updateExec.addParam(sdate);
			updateExec.addParam(pacackageNo);
			updateExec.addParam(biztype);
			updateExec.runQuery(updatePackageSql);
			//2.设置日志状态
			updateExec.clearParams();
			updateExec.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);
			updateExec.addParam(sendNo);
			updateExec.addParam(pacackageNo);
			updateExec.addParam(msgno);
			updateExec.addParam(sdate);
			updateExec.runQueryCloseCon(ls_UpdateSendLog);
			
		} catch (JAFDatabaseException e) {
			log.error("更新报表头信息时出现数据库异常!", e);
			throw new ITFEBizException("更新报表头信息时出现数据库异常!", e);
		}
		
		//3.设置业务表状态
		MsgRecvFacade.updateMsgByMsgPackageNo(msgno,pacackageNo,sdate,DealCodeConstants.DEALCODE_ITFE_FAIL);
		
		
		HashMap<String, String> bizMsgNoMap = ITFECommonConstant.bizMsgNoList;
		// 如果是业务报文需要更新文件与包的对应关系和业务表明细的状态
		if (bizMsgNoMap.containsKey(msgno)) {
			// 更新业务表明细状态
			MsgRecvFacade.updateMsgBizDetailByOriMsgNo("",
					msgno, DealCodeConstants.DEALCODE_ITFE_FAIL, pacackageNo, bizMsgNoMap);
		}
	}
	
	public void tbsUpdateFail(String sendNo, String pacackageNo, String msgno,String sdate)throws ITFEBizException {
		String ls_UpdateSendLog  = "update "+TvSendlogDto.tableName()+" set S_RETCODE = ? where S_SENDNO = ? and S_PACKNO = ? AND S_OPERATIONTYPECODE =? and  S_DATE = ? ";
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updateExec.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);
			updateExec.addParam(sendNo);
			updateExec.addParam(pacackageNo);
			updateExec.addParam(msgno);
			updateExec.addParam(sdate);
			updateExec.runQueryCloseCon(ls_UpdateSendLog);
			
		} catch (JAFDatabaseException e) {
			log.error("设置失败时异常!", e);
			throw new ITFEBizException("设置失败时异常!", e);
		}
	}

	public Boolean isBizMsgNo(String msgno) throws ITFEBizException {
		HashMap<String, String> bizMsgNoMap = ITFECommonConstant.bizMsgNoList;
		if(bizMsgNoMap.containsKey(msgno)){
			return true;
		}else{
			return false;
		}
	}

}
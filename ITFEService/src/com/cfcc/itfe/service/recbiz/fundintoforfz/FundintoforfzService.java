package com.cfcc.itfe.service.recbiz.fundintoforfz;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.service.util.CheckBizParam;
import com.cfcc.itfe.service.util.CheckIfConfirm;
import com.cfcc.itfe.service.util.EachPayOutSalaryCommitUtil;
import com.cfcc.itfe.service.util.WriteOperLog;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author Administrator
 * @time 13-09-13 16:21:33 codecomment:
 */

public class FundintoforfzService extends AbstractFundintoforfzService {
	private static Log log = LogFactory.getLog(FundintoforfzService.class);
 
	/**
	 * 获取查询列表
	 * 
	 * @generated
	 * @param dto
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List getdestorydata(TbsTvPayoutDto dto) throws ITFEBizException {
		String sql = " SELECT t2.*  FROM  TV_FILEPACKAGEREF t1 ,TBS_TV_PAYOUT t2 WHERE t1.S_ORGCODE = t2.S_BOOKORGCODE AND t1.S_PACKAGENO = t2.S_PACKAGENO AND t1.S_ORGCODE = ? AND t1.N_MONEY = ? AND t2.S_VOUNO = ? AND t2.S_STATUS = ? ";
		try {
			SQLExecutor sqlExecutor = DatabaseFacade.getODB()
					.getSqlExecutorFactory().getSQLExecutor();
			sqlExecutor.addParam(getLoginInfo().getSorgcode());
			sqlExecutor.addParam(dto.getFamt()); 
			sqlExecutor.addParam(dto.getSvouno());
			sqlExecutor.addParam(StateConstant.CONFIRMSTATE_NO);
			return (List) sqlExecutor.runQueryCloseCon(sql,
					TbsTvPayoutDto.class).getDtoCollection();
		} catch (JAFDatabaseException e) {
			log.error("调用后台处理出现数据库异常!", e);
			throw new ITFEBizException("调用后台处理出现数据库异常!", e);
		}
	}

	public Integer destorydata(TbsTvPayoutDto tmpdto) throws ITFEBizException {
		/*
		 * 判断文件名与包对应关系中是否已销号
		 */
		boolean b = false;
		String bizname = CheckBizParam
				.getBizname(BizTypeConstant.BIZ_TYPE_PAY_OUT);
		tmpdto.setSbookorgcode(getLoginInfo().getSorgcode());
		b = EachPayOutSalaryCommitUtil.confirmPayout(BizTypeConstant.BIZ_TYPE_PAY_OUT, tmpdto, getLoginInfo());
		WriteOperLog.operLog(getLoginInfo().getSuserCode(), BizTypeConstant.BIZ_TYPE_PAY_OUT, bizname,
				"eachConfirm", !b, getLoginInfo());
		if (b) {
			return StateConstant.SUBMITSTATE_SUCCESS;
		} else {
			return StateConstant.SUBMITSTATE_FAILURE;
		}

	}

}
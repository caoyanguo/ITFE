package com.cfcc.itfe.service.para.tspayacctinfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TsPayacctinfoDto;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author King
 * @time 13-06-25 14:56:44 codecomment:
 */

public class TspayacctinfoService extends AbstractTspayacctinfoService {
	private static Log log = LogFactory.getLog(TspayacctinfoService.class);

	/**
	 * 保存
	 * 
	 * @generated
	 * @param idto
	 * @throws ITFEBizException
	 */
	public void save(IDto idto) throws ITFEBizException {
		try {
			DatabaseFacade.getODB().create(idto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("字段国库主体代码，代理银行行号，付款人帐户已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}
	}

	/**
	 * 修改
	 * 
	 * @generated
	 * @param oriDto
	 * @param saveDto
	 * @throws ITFEBizException
	 */
	public void mod(IDto oriDto, IDto saveDto) throws ITFEBizException {
		TsPayacctinfoDto soriDto = (TsPayacctinfoDto) oriDto;
		TsPayacctinfoDto ssaveDto = (TsPayacctinfoDto) saveDto;
		try {
			SQLExecutor sqlExecutor = DatabaseFacade.getODB()
					.getSqlExecutorFactory().getSQLExecutor();
			sqlExecutor.addParam(ssaveDto.getSorgcode());
			sqlExecutor.addParam(ssaveDto.getStrecode());
			sqlExecutor.addParam(ssaveDto.getSgenbankcode());
			sqlExecutor.addParam(ssaveDto.getSpayeracct());
			sqlExecutor.addParam(ssaveDto.getSpayername());
			sqlExecutor.addParam(ssaveDto.getSpayeeacct());
			sqlExecutor.addParam(ssaveDto.getSpayeename());
			sqlExecutor.addParam(soriDto.getSorgcode());
			sqlExecutor.addParam(soriDto.getStrecode());
			sqlExecutor.addParam(soriDto.getSgenbankcode());
			sqlExecutor.addParam(soriDto.getSpayeracct());
			sqlExecutor.addParam(soriDto.getSpayeeacct());
			sqlExecutor
					.runQueryCloseCon("UPDATE TS_PAYACCTINFO SET S_ORGCODE = ?,S_TRECODE = ?,S_GENBANKCODE = ?,S_PAYERACCT = ?,S_PAYERNAME = ?,S_PAYEEACCT = ?,S_PAYEENAME = ?,TS_SYSUPDATE = CURRENT TIMESTAMP WHERE S_ORGCODE = ? AND S_TRECODE = ?  AND S_GENBANKCODE = ? AND S_PAYERACCT = ? AND S_PAYEEACCT = ?");
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("字段国库主体代码，代理银行行号，付款人帐户已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		}
	}

	/**
	 * 删除信息
	 * 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException
	 */
	public void del(IDto dto) throws ITFEBizException {
		try {
			DatabaseFacade.getODB().delete(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		}
	}

	public Boolean verifyPayeeBankNo(String agentBankNo)
			throws ITFEBizException {
		VoucherVerify voucherVerify = new VoucherVerify();
		try {
			return voucherVerify.verifyPayeeBankNo(agentBankNo,"");
		} catch (JAFDatabaseException e) {
			log.error(e);
			return false;
		} catch (ValidateException e) {
			log.error(e);
			return false;
		}
	}

}
package com.cfcc.itfe.service.para.voucherauto;

import java.util.List;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.pk.TsVouchercommitautoPK;
import com.cfcc.itfe.service.para.voucherauto.AbstractVoucherAutoService;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author db2admin
 * @time   11-08-01 09:16:05
 * codecomment: 
 */

public class VoucherAutoService extends AbstractVoucherAutoService {
	private static Log log = LogFactory.getLog(VoucherAutoService.class);	


	/**
	 * 增加	 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
    public IDto addInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
    		TsVouchercommitautoDto dto= (TsVouchercommitautoDto) dtoInfo;
    		TsVouchercommitautoPK pk =new TsVouchercommitautoPK();
    		pk.setSorgcode(dto.getSorgcode());
    		pk.setStrecode(dto.getStrecode());
    		pk.setSvtcode(dto.getSvtcode());
    		
    		TsVouchercommitautoDto tmp=null;
    		tmp= (TsVouchercommitautoDto) DatabaseFacade.getODB().find(pk);
    		if (null!=tmp) {
    			throw new ITFEBizException("该凭证信息已维护！");
			}
    		if(dto.getSreturbacknauto()==null)
    			dto.setSreturbacknauto("");
    		if(dto.getSreturnvoucherauto()==null)
    			dto.setSreturnvoucherauto("");
			DatabaseFacade.getDb().create(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("字段核算主体代码，国库代码，凭证类型已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(e.getMessage(), e);
		}
      return null;
    }

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void delInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
    		DatabaseFacade.getDb().delete(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		} 
    }

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void modInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
    		TsVouchercommitautoDto dto= (TsVouchercommitautoDto) dtoInfo;
    		if(dto.getSreturbacknauto()==null)
    			dto.setSreturbacknauto("");
    		if(dto.getSreturnvoucherauto()==null)
    			dto.setSreturnvoucherauto("");
			DatabaseFacade.getDb().update(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("字段核算主体代码，国库代码，凭证类型已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		} 
    }

}
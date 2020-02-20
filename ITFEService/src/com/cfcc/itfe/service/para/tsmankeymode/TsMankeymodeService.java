package com.cfcc.itfe.service.para.tsmankeymode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TsMankeymodeDto;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author Administrator
 * @time   12-06-25 13:31:34
 * codecomment: 
 */

public class TsMankeymodeService extends AbstractTsMankeymodeService {
	private static Log log = LogFactory.getLog(TsMankeymodeService.class);	


	/**
	 * 密钥模式修改
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
    public void keymodeModify(IDto dto) throws ITFEBizException {
    	try {
    		SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
    		String sql = "update ts_mankeymode set s_keymode=?";
    		exec.addParam(((TsMankeymodeDto)dto).getSkeymode());
    		exec.runQueryCloseCon(sql);
//			DatabaseFacade.getODB().update(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(e);
		}
    }

	/**
	 * 密钥模式列表
	 	 
	 * @generated
	 * @param dto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
    public PageResponse keyList(IDto dto, PageRequest pageRequest) throws ITFEBizException {
    	try {
    		return CommonFacade.getODB().findRsByDtoPaging(dto, pageRequest);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException(e);
		}
    }

}
package com.cfcc.itfe.service.para.tsconvertfinorg;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author db2admin
 * @time   09-11-09 15:22:03
 * codecomment: 
 */

public class TsConvertfinorgService extends AbstractTsConvertfinorgService {
	private static Log log = LogFactory.getLog(TsConvertfinorgService.class);	


	/**
	 * 增加	 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
    public IDto addInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
    		isExist(dtoInfo);
			DatabaseFacade.getDb().create(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("字段核算主体代码，财政机关代码已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}
		return null;
    }

    /**
     * 判断是否存在相同的行政区域代码
     * @param dtoInfo
     * @throws ITFEBizException 
     */
	private void isExist(IDto dtoInfo) throws ITFEBizException {
		TsConvertfinorgDto paramDto=(TsConvertfinorgDto) dtoInfo;
		try {
		List<TsConvertfinorgDto>	list= DatabaseFacade.getODB().find(TsConvertfinorgDto.class, " WHERE S_ADMDIVCODE = '"+paramDto.getSadmdivcode()+"'");
		if (null!=list && list.size()>0) {
			throw new ITFEBizException("该行政区域代码信息已经存在");
		}
		} catch (JAFDatabaseException e) {
			e.printStackTrace();
			throw new ITFEBizException("查询信息异常: "+e.getMessage(),e);
		}
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
			DatabaseFacade.getDb().update(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("字段核算主体代码，财政机关代码已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		}
    }
}
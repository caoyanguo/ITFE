package com.cfcc.itfe.service.para.tdtaxorgparam;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TdTaxorgParamDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author ZZD
 * @time   13-03-01 10:54:40
 * codecomment: 
 */

public class TdTaxorgParamService extends AbstractTdTaxorgParamService {
	private static Log log = LogFactory.getLog(TdTaxorgParamService.class);	


	/**
	 * 增加	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void addInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
    		TdTaxorgParamDto dto = (TdTaxorgParamDto)dtoInfo;
    		TdTaxorgParamDto finddto=new TdTaxorgParamDto();
    		finddto.setSbookorgcode(dto.getSbookorgcode());
    		finddto.setStaxorgcode(dto.getStaxorgcode());
    		try {
				List list=CommonFacade.getODB().findRsByDto(finddto);
				if(list!=null&&list.size()>0){
					throw new ITFEBizException("征收机关代码："+dto.getStaxorgcode()+" 已存在，不能重复录入！");
				}
			} catch (ValidateException e) {
				log.error(e);
				throw new ITFEBizException(e.getMessage(),e);
			}   		
			DatabaseFacade.getDb().create(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException(StateConstant.PRIMAYKEY, e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}
    }

	/**
	 * 删除	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void delInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
			CommonFacade.getODB().deleteRsByDto(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		}
    }

	/**
	 * 修改	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void modInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
    		TdTaxorgParamDto dto = (TdTaxorgParamDto)dtoInfo;    		
			DatabaseFacade.getDb().update(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException(StateConstant.PRIMAYKEY, e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		} 
    }

}
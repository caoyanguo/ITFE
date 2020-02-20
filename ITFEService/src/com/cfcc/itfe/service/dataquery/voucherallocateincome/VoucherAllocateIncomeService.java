package com.cfcc.itfe.service.dataquery.voucherallocateincome;

import org.apache.commons.logging.*;
import com.cfcc.itfe.service.dataquery.voucherallocateincome.AbstractVoucherAllocateIncomeService;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.exception.ITFEBizException;import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

/**
 * @author db2admin
 * @time   14-11-15 17:49:48
 * codecomment: 
 */

public class VoucherAllocateIncomeService extends AbstractVoucherAllocateIncomeService {
	private static Log log = LogFactory.getLog(VoucherAllocateIncomeService.class);	


	/**
	 * 保存
	 	 
	 * @generated
	 * @param saveDto
	 * @throws ITFEBizException	 
	 */
    public void saveDto(TvVoucherinfoAllocateIncomeDto saveDto) throws ITFEBizException {
    	try {
    		String mainvou = mainvou = VoucherUtil.getGrantSequence();
    		saveDto.setSadmdivcode(SrvCacheFacade.cacheFincInfo("").get(saveDto.getStrecode()).getSadmdivcode());
    		saveDto.setSdealno(mainvou);
			DatabaseFacade.getODB().create(saveDto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("该记录已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(e.getMessage());
		}
    }

	/**
	 * 删除
	 	 
	 * @generated
	 * @param deleteDto
	 * @throws ITFEBizException	 
	 */
    public void deleteDto(TvVoucherinfoAllocateIncomeDto deleteDto) throws ITFEBizException {
    	try {
			DatabaseFacade.getODB().delete(deleteDto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage());
		}
    }

	/**
	 * 修改保存
	 	 
	 * @generated
	 * @param modifyDto
	 * @throws ITFEBizException	 
	 */
    public void modifySaveDto(TvVoucherinfoAllocateIncomeDto modifyDto) throws ITFEBizException {
    	try {
    		modifyDto.setSadmdivcode(SrvCacheFacade.cacheFincInfo("").get(modifyDto.getStrecode()).getSadmdivcode());
			DatabaseFacade.getODB().update(modifyDto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage());
		}
    }

}
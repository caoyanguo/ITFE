/**
 * 
 */
package com.cfcc.itfe.client.para.tsconvertrea;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;

/**
 * @author Administrator
 * 
 */
public class TrelistByOrgcode {

	public static List getTreList(String orgcode,
			ICommonDataAccessService commonServer) throws ITFEBizException {
		TsTreasuryDto tmpDto = new TsTreasuryDto();
		tmpDto.setSorgcode(orgcode);
		return commonServer.findRsByDto(tmpDto);

	}
}

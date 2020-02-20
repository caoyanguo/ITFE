package com.cfcc.itfe.service.dataquery.voucherinfo;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.CommonDataAccessService;
import com.cfcc.itfe.service.dataquery.voucherinfo.AbstractVoucherInfoService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;
/**
 * @author whj
 * @time   13-09-04 11:20:28
 * codecomment: 
 */

public class VoucherInfoService extends AbstractVoucherInfoService {
	private static Log log = LogFactory.getLog(VoucherInfoService.class);

	public PageResponse searchResult(TvVoucherinfoDto searchDto, String dzType,
			PageRequest request) throws ITFEBizException {
    	try {
    		String where ="";
    		if (dzType.equals("0")) {
				where = " S_VTCODE in ('5501','5502','3501') ";
			}else{
				where ="  S_VTCODE in ('2501','2502','2503','2504','3502') ";
			}
			return CommonFacade.getODB().findRsByDtoWithWherePaging(searchDto, request, where);
		} catch (ITFEBizException e) {
			log.error(e);
		} catch (JAFDatabaseException e) {
			e.printStackTrace();
		} catch (ValidateException e) {
			e.printStackTrace();
		}
		return null;
	}

}
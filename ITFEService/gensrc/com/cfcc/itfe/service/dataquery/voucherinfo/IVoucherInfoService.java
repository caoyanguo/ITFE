package com.cfcc.itfe.service.dataquery.voucherinfo;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.jaf.common.page.PageRequest;

/**
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * codecomment: 
 */
public interface IVoucherInfoService extends IService {



	/**
	 * ²éÑ¯½á¹û
	 	 
	 * @generated
	 * @param searchDto
	 * @param dzType
	 * @param request
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse searchResult(TvVoucherinfoDto searchDto, String dzType, PageRequest request) throws ITFEBizException;

}
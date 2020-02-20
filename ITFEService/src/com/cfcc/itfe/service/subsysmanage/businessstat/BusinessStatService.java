package com.cfcc.itfe.service.subsysmanage.businessstat;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;
import com.cfcc.itfe.service.subsysmanage.businessstat.AbstractBusinessStatService;
import com.cfcc.itfe.exception.ITFEBizException;import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author zhouchuan
 * @time   10-05-31 15:57:21
 * codecomment: 
 */

public class BusinessStatService extends AbstractBusinessStatService {
	private static Log log = LogFactory.getLog(BusinessStatService.class);	


	/**
	 * ��ҳ��ѯҵ����	 
	 * @generated
	 * @param findDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
    public PageResponse findBusinessByPage(TvInfileDto findDto, PageRequest pageRequest) throws ITFEBizException {
		try {
			
			return CommonFacade.getODB().findRsByDtoPagingBusiness(findDto, pageRequest, " S_ACCDATE ");

		} catch (JAFDatabaseException e) {
			log.error("��ҳ��ѯҵ����ͳ����Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯҵ����ͳ����Ϣʱ����!", e);
		} catch (ValidateException e) {
			log.error("��ҳ��ѯ����������Ϣʱ����!", e);
			throw new ITFEBizException("��ҳ��ѯҵ����ͳ����Ϣʱ����!", e);
		}
    }
}
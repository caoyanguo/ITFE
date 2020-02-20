package com.cfcc.itfe.service.dataquery.payoutfinancequery;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceMainDto;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.itfe.persistence.dto.HtvPayoutfinanceMainDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;


import com.cfcc.itfe.service.dataquery.payoutfinancequery.IPayOutFinanceQueryService;
import com.cfcc.itfe.service.AbstractITFEService;
import org.apache.commons.logging.*;
import com.cfcc.itfe.exception.ITFEBizException;
/**
 * @author Administrator
 * @time 19-12-08 13:00:40
 * @generated
 * codecomment: 
 */

public abstract class AbstractPayOutFinanceQueryService extends AbstractITFEService implements IPayOutFinanceQueryService {

    /** 属性列表 */
    


    public String getServiceDescription() {

		return "批量拨付查询处理服务组件";
	}
	
    /**
     * =========================================================================
     * getter and setter
     * =========================================================================
     */

}
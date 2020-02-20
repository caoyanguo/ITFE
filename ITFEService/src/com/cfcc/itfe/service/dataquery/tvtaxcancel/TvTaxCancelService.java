package com.cfcc.itfe.service.dataquery.tvtaxcancel;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvTaxCancelDto;

/**
 * @author VAIO
 * @time 10-06-02 09:48:01 codecomment:
 */

public class TvTaxCancelService extends AbstractTvTaxCancelService {
	private static Log log = LogFactory.getLog(TvTaxCancelService.class);

	public List query(TvTaxCancelDto dtoInfo) throws ITFEBizException {
		StringBuffer sqlbuf = new StringBuffer();
		// Ìõ¼þ
		String where = "";
		if (null != dtoInfo) {
			where = "where";

			//
		}
		String sql = "select * from HTV_TAX_CANCEL";

		if (where.length() > 0) {
			sql += where;
		}
		sql += "union all select * from TV_TAX_CANCEL";

		if (where.length() > 0) {
			sql += where;
		}
		sql += "order by S_TAXORGCODE,S_ENTRUSTDATE,S_CANCELAPPNO;";

		return null;
	}
}
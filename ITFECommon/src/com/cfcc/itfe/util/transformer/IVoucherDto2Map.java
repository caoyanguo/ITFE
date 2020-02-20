package com.cfcc.itfe.util.transformer;

import java.util.List;
import java.util.Map;

import com.cfcc.deptone.common.core.exception.SequenceException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

public interface IVoucherDto2Map {
	public Map tranfor(List list) throws ITFEBizException ;
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException;
	public Map voucherTranfor(TvVoucherinfoDto vDto) throws ITFEBizException;
}

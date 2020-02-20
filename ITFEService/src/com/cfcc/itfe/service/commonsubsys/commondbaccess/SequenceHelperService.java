package com.cfcc.itfe.service.commonsubsys.commondbaccess;

import org.apache.commons.logging.*;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.AbstractSequenceHelperService;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.SequenceGenerator;
/**
 * @author zhouchuan
 * @time   09-11-03 14:45:11
 * codecomment: 
 */

public class SequenceHelperService extends AbstractSequenceHelperService {
	
	private static Log log = LogFactory.getLog(SequenceHelperService.class);	


	/**
	 * 取得指定序列的序列号	 
	 * @generated
	 * @param seqname
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    public String getSeqNo(String seqname) throws ITFEBizException {
    	
    	try {
			String seq = SequenceGenerator.getNextByDb2(seqname,
					SequenceName.TRAID_SEQ_CACHE,
					SequenceName.TRAID_SEQ_STARTWITH);
			return seq;
		} catch (SequenceException e) {
			String errorinfo = "获取序列号:" + seqname + "错误.";
			log.error(errorinfo, e);
			throw new ITFEBizException(errorinfo, e);
		}
   }

}
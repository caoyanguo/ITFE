package com.cfcc.itfe.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

public class ServiceUtil {
	private static Log log = LogFactory.getLog(ServiceUtil.class);
	public static void checkResult(MuleMessage message) throws ITFEBizException {
		if (message.getExceptionPayload() != null) {

			log.error("调用服务错误", message.getExceptionPayload().getException());
			throw new ITFEBizException("调用后台服务错误"
					+ message.getExceptionPayload().getRootException());

		}
	}
	public static Map<String,TsInfoconnorgaccDto> getAcctCache(String orgcode)
	{
		Map<String,TsInfoconnorgaccDto> getMap = null;
		String sql = "SELECT S_ORGCODE,S_TRECODE,S_ACCNO,S_ACCNAME FROM TR_STOCKDAYRPT GROUP BY S_ORGCODE,S_TRECODE,S_ACCNO,S_ACCNAME union SELECT S_ORGCODE,S_TRECODE,S_ACCNO,S_ACCNAME FROM HTR_STOCKDAYRPT GROUP BY S_ORGCODE,S_TRECODE,S_ACCNO,S_ACCNAME";
		SQLExecutor sqlExe = null;
		SQLResults res = null;
		try {
			sqlExe = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			res = sqlExe.runQueryCloseCon(sql);
			if(res!=null&&res.getRowCount()>0)
			{
				TsInfoconnorgaccDto dto = null;
				getMap = new HashMap<String,TsInfoconnorgaccDto>();
				for(int i=0;i<res.getRowCount();i++)
				{
					if(res.getString(i, 2).indexOf("371")<0)
					{
						dto = new TsInfoconnorgaccDto();
						dto.setSorgcode(orgcode);
						dto.setStrecode(res.getString(i, 1));
						dto.setSpayeraccount(res.getString(i, 2));
						dto.setSpayername(res.getString(i, 3));
						getMap.put(orgcode+dto.getStrecode()+dto.getSpayeraccount(), dto);
					}
				}
			}
		} catch (JAFDatabaseException e) {
			log.error("生成库款账户对帐单缓存库存日报表中库款账户失败！", e);
		}finally{
			if(res!=null)
				res = null;
			if(sqlExe!=null)
				sqlExe.closeConnection();
		}
		
		return getMap;
	}
}

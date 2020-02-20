package com.cfcc.itfe.component;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvBatchpayDto;
import com.cfcc.itfe.util.FtpUtil;

/**
 * 
 * 山东一本通定时回执任务
 * 
 */
@SuppressWarnings("unchecked")
public class TimerFtpReturnComponent implements Callable {

	private static Log logger = LogFactory.getLog(TimerFtpReturnComponent.class);
	public Object onCall(MuleEventContext eventContext) throws Exception {
		String date = TimeFacade.getCurrentStringTime();
		logger.info("山东一本通定时回执Ftp文件start=================================="+date);
		List<TsTreasuryDto> treList = DatabaseFacade.getDb().find(TsTreasuryDto.class);
		if(treList!=null&&treList.size()>0)
		{
			TvBatchpayDto queryDto = null;
			List<TvBatchpayDto> returnList = null;
			for(int i=0;i<treList.size();i++)
			{
				queryDto = new TvBatchpayDto();
				queryDto.setSdate(date);
				queryDto.setStrecode(treList.get(i).getStrecode());
				returnList = FtpUtil.queryFtpReturnFileList(queryDto);
				FtpUtil.uploadZipFile(returnList);
				
			}
		}
		FtpUtil.downLoadZipFile(date, "");
		logger.info("山东一本通定时回执Ftp文件end=================================="+date);
		return eventContext;
	}
}

package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author db2admin
 * @time   13-05-04 23:48:29
 */
public class LogRetLogEnumFactory implements IEnumFactory {
	private static Log log = LogFactory.getLog(ITFEEnumFactory.class);
	public List<Mapper> getEnums(Object o) {
		List<Mapper> maplist = new ArrayList<Mapper>();
		IItfeCacheService cacheService = (IItfeCacheService)ServiceFactory.getService(IItfeCacheService.class);
		List<TdEnumvalueDto> enumList = new ArrayList<TdEnumvalueDto>();
		try {
			enumList = cacheService.cacheEnumValueByCode("0300");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		
		for (TdEnumvalueDto emuDto : enumList) {
			
			if (emuDto.getSvalue().startsWith("800")) {
				if (emuDto.getSvalue().startsWith("80000")) {
					Mapper map = new Mapper(DealCodeConstants.DEALCODE_TIPS_SUCCESS, "成功 ");
					maplist.add(map);
					continue;
				}
				Mapper map = new Mapper(emuDto.getSvalue(), emuDto.getSvaluecmt());
				maplist.add(map);
			}
		}
		Mapper map = new Mapper(DealCodeConstants.DEALCODE_ITFE_OTHER, "其他错误");
		maplist.add(map);
		return maplist;
	}

}

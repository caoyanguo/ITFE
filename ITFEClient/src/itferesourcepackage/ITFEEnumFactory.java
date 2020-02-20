package itferesourcepackage;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;



public class ITFEEnumFactory implements IEnumFactory {
	private static Log log = LogFactory.getLog(ITFEEnumFactory.class);
	/**
	 * 通过数据库中取得枚举值
	 * @param o
	 * @return
	 */
	public List<Mapper> getEnums(Object o) {
		List<Mapper> maplist = new ArrayList<Mapper>();
		IItfeCacheService cacheService = (IItfeCacheService)ServiceFactory.getService(IItfeCacheService.class);
		List<TdEnumvalueDto> enumList = new ArrayList<TdEnumvalueDto>();
		try {
			enumList = cacheService.cacheEnumValueByCode(o.toString());
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		for (TdEnumvalueDto emuDto : enumList) {
			Mapper map = new Mapper(emuDto.getSvalue(), emuDto.getSvaluecmt());
			maplist.add(map);
		}
		return maplist;
	}
}

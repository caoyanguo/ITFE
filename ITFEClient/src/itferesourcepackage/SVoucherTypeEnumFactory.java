package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author lenovo
 * @time 13-07-06 22:35:36
 */
public class SVoucherTypeEnumFactory extends ITFEEnumFactory {

	public List<Mapper> getEnums(Object o) {
		// TODO Auto-generated method stub
		List<Mapper> maplist = new ArrayList<Mapper>();
		try {
			IItfeCacheService cacheService = (IItfeCacheService) ServiceFactory
					.getService(IItfeCacheService.class);
			List<TdEnumvalueDto> enumList = cacheService
					.cacheEnumValueBySql("SELECT * FROM TD_ENUMVALUE WHERE S_TYPECODE = '0419' ORDER BY S_IFAVAILABLE ASC");
			if(null == enumList || enumList.size() == 0){
				return null;
			}
			for (TdEnumvalueDto emuDto : enumList) {
				Mapper map = new Mapper(emuDto.getSvalue(), emuDto.getSvaluecmt());
				maplist.add(map);
			}
			return maplist;
		} catch (ITFEBizException e) {
		}
		return null;
	}

}
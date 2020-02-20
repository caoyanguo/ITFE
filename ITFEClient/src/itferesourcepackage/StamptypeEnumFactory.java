package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsOperationtypeDto;
import com.cfcc.itfe.persistence.dto.TsStamptypeDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author caoyg
 * @time   09-12-04 15:42:30
 */
public class StamptypeEnumFactory extends ITFEEnumFactory {

private static Log log = LogFactory.getLog(ITFEEnumFactory.class);
	
	public List<Mapper> getEnums(Object arg0) {
		return super.getEnums("0425");
		/**List<Mapper> maplist = new ArrayList<Mapper>();
		ICommonDataAccessService commondataaccess = (ICommonDataAccessService)ServiceFactory.getService(ICommonDataAccessService.class);
		List<TsStamptypeDto> enumList = new ArrayList<TsStamptypeDto>();
		TsStamptypeDto dto = new TsStamptypeDto();
		try {
			enumList =  commondataaccess.findRsByDto(dto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		for (TsStamptypeDto emuDto : enumList) {
			Mapper map = new Mapper(emuDto.getSstamptypecode(), emuDto.getSstamptypename());
			maplist.add(map);
		}
		return maplist;**/
	}

}

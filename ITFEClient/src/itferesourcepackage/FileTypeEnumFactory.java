package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsOperationtypeDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.CommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author db2admin
 * @time   09-11-19 14:02:03
 */
public class FileTypeEnumFactory implements IEnumFactory {
	private static Log log = LogFactory.getLog(ITFEEnumFactory.class);
	
	public List<Mapper> getEnums(Object arg0) {
		List<Mapper> maplist = new ArrayList<Mapper>();
		ICommonDataAccessService commondataaccess = (ICommonDataAccessService)ServiceFactory.getService(ICommonDataAccessService.class);
		List<TsOperationtypeDto> enumList = new ArrayList<TsOperationtypeDto>();
		TsOperationtypeDto dto = new TsOperationtypeDto();
		try {
			enumList =  commondataaccess.findRsByDto(dto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		for (TsOperationtypeDto emuDto : enumList) {
			Mapper map = new Mapper(emuDto.getSoperationtypecode(), emuDto.getSoperationtypename());
			maplist.add(map);
		}
		return maplist;
	}
	

}

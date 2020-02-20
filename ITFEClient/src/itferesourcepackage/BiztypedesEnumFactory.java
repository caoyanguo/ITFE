package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.client.dataquery.querymsglog.QueryMsgLogBean;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.mvc.BasicModel;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author Administrator
 * @time   13-03-27 10:04:18
 */
public class BiztypedesEnumFactory implements IEnumFactory  {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> maplist = new ArrayList<Mapper>();
		ICommonDataAccessService commondataService = (ICommonDataAccessService)ServiceFactory.getService(ICommonDataAccessService.class);
		TdEnumvalueDto idto = new TdEnumvalueDto();
		idto.setStypecode("0418");
		List<TdEnumvalueDto> enumList = new ArrayList<TdEnumvalueDto>();
		try {
			enumList = commondataService.findRsByDto(idto,"order by S_IFAVAILABLE");
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		for (TdEnumvalueDto emuDto : enumList) {
				Mapper map = new Mapper(emuDto.getSvalue(), "["+emuDto.getSvalue()+"] "+emuDto.getSvaluecmt());
				maplist.add(map);
		}
	
		return maplist;
		
		
		
	}

}

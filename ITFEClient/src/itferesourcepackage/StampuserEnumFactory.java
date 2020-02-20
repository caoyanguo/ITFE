package itferesourcepackage;



import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author hjr
 * @time   14-03-18 16:40:49
 */
public class StampuserEnumFactory extends ITFEEnumFactory {
	private static Log log = LogFactory.getLog(StampuserEnumFactory.class);
	public List<Mapper> getEnums(Object o) {
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
		List<Mapper> maplist = new ArrayList<Mapper>();
		ICommonDataAccessService commondataaccess = (ICommonDataAccessService)ServiceFactory.getService(ICommonDataAccessService.class);
		TsUsersDto dto=new TsUsersDto();
		dto.setSorgcode(loginfo.getSorgcode());		
		try {
			List<TsUsersDto> list =commondataaccess.findRsByDto(dto);
			if(list==null||list.size()==0)
				return maplist;
			for (TsUsersDto userDto : list) {
				Mapper map = new Mapper(userDto.getSusercode(), userDto.getSusername());
				maplist.add(map);
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}		
		return maplist;	
	}
}

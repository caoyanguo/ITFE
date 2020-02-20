package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author db2admin
 * @time   13-03-13 01:52:42
 */
public class BillOrgCodeEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> maplist = new ArrayList<Mapper>();
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
		.getDefault().getLoginInfo();
		IItfeCacheService cacheServer = (IItfeCacheService) ServiceFactory
		.getService(IItfeCacheService.class);
		// 出票单位维护
		TsConvertfinorgDto dto = new TsConvertfinorgDto();
		dto.setSorgcode(loginfo.getSorgcode());
		Mapper map;

		List<TsConvertfinorgDto> enumList;
		try {
			enumList = cacheServer.cacheGetValueByDto(dto);
			if (enumList.size() > 0) {
				for (TsConvertfinorgDto emuDto : enumList) {
					map = new Mapper(emuDto.getSfinorgcode(), emuDto
							.getSfinorgname());
					maplist.add(map);
				}
			}
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		return maplist;
	}

}

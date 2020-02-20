package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * 国库主体代码信息
 * @author zhangxuehong
 * @time   10-08-26 16:41:47
 */
public class StrecodemsgEnumFactory implements IEnumFactory {

	private static Log log = LogFactory.getLog(ITFEEnumFactory.class);
	
	public List<Mapper> getEnums(Object o) {
		List<Mapper> maplist = new ArrayList<Mapper>();
		ICommonDataAccessService commondataaccess = (ICommonDataAccessService)ServiceFactory.getService(ICommonDataAccessService.class);
		List<TsTreasuryDto> enumList = new ArrayList<TsTreasuryDto>();
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
		TsTreasuryDto dto = new TsTreasuryDto();
//		dto.setSorgcode(loginfo.getSorgcode());
//		中心机构代码
		if (!loginfo.getSorgcode().equals(StateConstant.ORG_CENTER_CODE)) {
			dto.setSorgcode(loginfo.getSorgcode());
		}
		try {
			enumList =  commondataaccess.findRsByDto(dto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		for (TsTreasuryDto emuDto : enumList) {
			Mapper map = new Mapper(emuDto.getStrecode(), emuDto.getStrename());
			maplist.add(map);
		}
		return maplist;
	}

}

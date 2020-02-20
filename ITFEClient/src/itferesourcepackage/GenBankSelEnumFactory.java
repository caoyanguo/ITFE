package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.deptone.common.core.msgexchange.mto.MessageData;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsGenbankandreckbankDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author db2admin
 * @time 13-03-08 10:52:35
 */
public class GenBankSelEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> maplist = new ArrayList<Mapper>();

		IItfeCacheService cacheServer = (IItfeCacheService) ServiceFactory
				.getService(IItfeCacheService.class);
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
				.getDefault().getLoginInfo();

		// 代理银行支付行号对应关系
		TsGenbankandreckbankDto dto = new TsGenbankandreckbankDto();
		dto.setSbookorgcode(loginfo.getSorgcode());
		Mapper map;

		List<TsGenbankandreckbankDto> enumList;
		try {
			enumList = cacheServer.cacheGetValueByDto(dto);
			if (enumList.size() > 0) {
				for (TsGenbankandreckbankDto emuDto : enumList) {
					map = new Mapper(emuDto.getSreckbankcode(), emuDto
							.getSreckbankname()
							+ "(" + emuDto.getSreckbankcode() + ")");
					maplist.add(map);
				}
			}
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
		return maplist;

	}

}

package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsGenbankandreckbankDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author db2admin
 * @time 13-03-01 17:11:24
 */
public class TskeyorgnameEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		
		List<Mapper> maplist = new ArrayList<Mapper>();
		IItfeCacheService cacheServer = (IItfeCacheService) ServiceFactory
				.getService(IItfeCacheService.class);
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
				.getDefault().getLoginInfo();
		// 核算主体代码
		Mapper map = new Mapper(loginfo.getSorgcode(), loginfo.getSorgName());
		maplist.add(map);
		// 国库主体代码
		TsTreasuryDto dto = new TsTreasuryDto();
		dto.setSorgcode(loginfo.getSorgcode());
		try {
			List<TsTreasuryDto> enumList = cacheServer.cacheGetValueByDto(dto);
			if (enumList.size() > 0) {
				for (TsTreasuryDto emuDto : enumList) {
					map = new Mapper(emuDto.getStrecode(), emuDto.getStrename());
					maplist.add(map);
				}
			}
			// 财政代码
			TsConvertfinorgDto findto = new TsConvertfinorgDto();
			findto.setSorgcode(loginfo.getSorgcode());
			List<TsConvertfinorgDto> finenumList = cacheServer
					.cacheGetValueByDto(findto);
			if (enumList.size() > 0) {
				for (TsConvertfinorgDto emuDto : finenumList) {
					map = new Mapper(emuDto.getSfinorgcode(), emuDto
							.getSfinorgname());
					maplist.add(map);
				}
			}

			// 代理银行
			TsGenbankandreckbankDto bnkdto = new TsGenbankandreckbankDto();
			bnkdto.setSbookorgcode(loginfo.getSorgcode());

			List<TsGenbankandreckbankDto> bnkenumList = cacheServer
					.cacheGetValueByDto(bnkdto);
			if (enumList.size() > 0) {
				for (TsGenbankandreckbankDto emuDto : bnkenumList) {
					map = new Mapper(emuDto.getSreckbankcode(), emuDto
							.getSreckbankname());
					maplist.add(map);
				}
			}
			// 征收机关
			TsTaxorgDto taxdto = new TsTaxorgDto();
			taxdto.setSorgcode(loginfo.getSorgcode());
			List<TsTaxorgDto> taxenumList = cacheServer.cacheGetValueByDto(taxdto);
			if (enumList.size() > 0) {
				for (TsTaxorgDto emuDto : taxenumList) {
					map = new Mapper(emuDto.getStaxorgcode(), emuDto
							.getStaxorgname());
					maplist.add(map);
				}
			}
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
		}

		return maplist;
	}

}

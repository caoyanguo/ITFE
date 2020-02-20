package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.para.tsmankey.TsMankeyBean;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsGenbankandreckbankDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author db2admin
 * @time   13-02-28 00:14:07
 */
public class TskeyorgEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		
		List<Mapper> maplist = new ArrayList<Mapper>();
		IItfeCacheService cacheServer = (IItfeCacheService)ServiceFactory.getService(IItfeCacheService.class);
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();	
	   if (StateConstant.KEY_BOOK.equals(toString())) {
		   Mapper map = new Mapper(loginfo.getSorgcode(), loginfo.getSorgName());
		   maplist.add(map);
		 
	   }else if(StateConstant.KEY_TRECODE.equals(toString())){
		   TsTreasuryDto dto = new TsTreasuryDto();
			dto.setSorgcode(loginfo.getSorgcode());
			try {
				List<TsTreasuryDto> enumList = cacheServer
						.cacheGetValueByDto(dto);
				if (enumList.size() > 0) {
					for (TsTreasuryDto emuDto : enumList) {
						Mapper map = new Mapper(emuDto.getStrecode(), emuDto
								.getStrename());
						maplist.add(map);
					}
				}
			} catch (ITFEBizException e) {
				MessageDialog.openErrorDialog(null, e);
			}
		   
	   }else if(StateConstant.KEY_BILLORG.equals(toString())){
		   TsConvertfinorgDto dto = new TsConvertfinorgDto();
			dto.setSorgcode(loginfo.getSorgcode());
			try {
				List<TsConvertfinorgDto> enumList = cacheServer
						.cacheGetValueByDto(dto);
				if (enumList.size() > 0) {
					for (TsConvertfinorgDto emuDto : enumList) {
						Mapper map = new Mapper(emuDto.getSfinorgcode(), emuDto
								.getSfinorgname());
						maplist.add(map);
					}
				}
			} catch (ITFEBizException e) {
				MessageDialog.openErrorDialog(null, e);
			}
		   
	   }else if(StateConstant.KEY_GENBANK.equals(toString())){
		   TsGenbankandreckbankDto dto = new TsGenbankandreckbankDto();
			dto.setSbookorgcode(loginfo.getSorgcode());
			try {
				List<TsGenbankandreckbankDto> enumList = cacheServer
						.cacheGetValueByDto(dto);
				if (enumList.size() > 0) {
					for (TsGenbankandreckbankDto emuDto : enumList) {
						Mapper map = new Mapper(emuDto.getSreckbankcode(), emuDto
								.getSreckbankname());
						maplist.add(map);
					}
				}
			} catch (ITFEBizException e) {
				MessageDialog.openErrorDialog(null, e);
			}
		   
	   }else if(StateConstant.KEY_TAXORG.equals(toString())){
		   TsTaxorgDto dto = new TsTaxorgDto();
		   dto.setSorgcode(loginfo.getSorgcode());
			try {
				List<TsTaxorgDto> enumList = cacheServer
						.cacheGetValueByDto(dto);
				if (enumList.size() > 0) {
					for (TsTaxorgDto emuDto : enumList) {
						Mapper map = new Mapper(emuDto.getStaxorgcode(), emuDto.getStaxorgname());
						maplist.add(map);
					}
				}
			} catch (ITFEBizException e) {
				MessageDialog.openErrorDialog(null, e);
			}
	
	}
	return maplist;}

}

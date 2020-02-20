package itferesourcepackage;

import java.util.List;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author lenovo
 * @time   13-07-06 22:35:36
 */
public class SVoucherStatusEnumFactory extends ITFEEnumFactory {

	public List<Mapper> getEnums(Object o) {
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
		List<Mapper> maplist = super.getEnums("0420");
//		if(loginfo.getPublicparam().indexOf(",payoutstampmode=true,")<0){
//			maplist.remove(16);
//		}
		return maplist;
	}

}

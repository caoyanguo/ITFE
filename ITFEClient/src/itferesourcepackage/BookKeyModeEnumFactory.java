package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * 核算主体内密钥设置模式
 * @author db2admin
 * @time   12-06-21 16:49:21
 */
public class BookKeyModeEnumFactory extends ITFEEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> maplist = new ArrayList<Mapper>();
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
		.getDefault().getLoginInfo();
		if (null!=loginfo.getMankeyMode() && loginfo.getMankeyMode().equals(StateConstant.KEY_ALL)) {
			Mapper map = new Mapper("0", "全省统一");
			maplist.add(map);
			return maplist;
		}else{
			return super.getEnums("0414");
		}
	}

}

package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author db2admin
 * @time   09-11-13 14:59:56
 */
public class SusertypeEnumFactory extends ITFEEnumFactory {
	public List<Mapper> getEnums(Object o) {
		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
		if (StateConstant.STAT_CENTER_CODE.equals(loginfo.getSorgcode())) {
			List<Mapper> list=new ArrayList<Mapper>();
			list.add(new Mapper(StateConstant.User_Type_Admin,"管理员"));
			list.add(new Mapper(StateConstant.User_Type_Stat,"监控员"));
			return list;
		}else{
		   return super.getEnums("0112");
		}
	}

}

package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author Administrator
 * @time   13-03-26 17:16:20
 */
public class UserloginstatusEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> list = new ArrayList<Mapper>();
		Mapper mapper0 = new Mapper();
		mapper0.setDisplayValue("Î´µÇÂ¼");
		mapper0.setUnderlyValue(StateConstant.LOGIN_STATUE_0);
		list.add(mapper0);
		Mapper mapper1 = new Mapper();
		mapper1.setDisplayValue("ÒÑµÇÂ¼");
		mapper1.setUnderlyValue(StateConstant.LOGIN_STATUE_1);
		list.add(mapper1);
		return list;
	}

}

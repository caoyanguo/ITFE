package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author Administrator
 * @time   17-04-04 15:38:50
 */
public class TbsCheckResultEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> list = new ArrayList<Mapper>();
		Mapper mp0 = new Mapper("0","对账相符");
		Mapper mp1 = new Mapper("1","对账不符");
		list.add(mp0);
		list.add(mp1);
		return list;
	}

}

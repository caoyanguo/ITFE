package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author Administrator
 * @time   17-10-28 15:47:20
 */
public class RecvFlagEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> list = new ArrayList<Mapper>();
		Mapper mp0 = new Mapper("1","本行业务");
		Mapper mp1 = new Mapper("2","同城跨行");
		Mapper mp2 = new Mapper("3","异地跨行");
		list.add(mp0);
		list.add(mp1);
		list.add(mp2);
		return list;
	}

}

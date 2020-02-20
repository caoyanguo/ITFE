package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author Administrator
 * @time   17-10-28 15:47:20
 */
public class ClearStatusEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> list = new ArrayList<Mapper>();
		Mapper mp0 = new Mapper("0","清算成功");
		Mapper mp1 = new Mapper("1","待清算");
		list.add(mp0);
		list.add(mp1);
		return list;
	}

}

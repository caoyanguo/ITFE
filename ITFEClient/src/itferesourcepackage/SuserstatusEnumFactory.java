package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author db2admin
 * @time   09-11-13 15:01:39
 */
public class SuserstatusEnumFactory  extends ITFEEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> maplist = new ArrayList<Mapper>();
		Mapper map = new Mapper("2", "นคื๗");
		maplist.addAll(super.getEnums("0113"));
		maplist.add(map);
		return maplist;
	}

}

package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author ZZD
 * @time   13-02-19 15:53:24
 */
public class IsCollectEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List list = new ArrayList();
		list.add(new Mapper("1","·ñ"));
		list.add(new Mapper("0","ÊÇ"));
		return list;
	}

}

package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author Yuan
 * @time   13-03-08 10:52:53
 */
public class CommonyesnoEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List list = new ArrayList();
		list.add(new Mapper("1","ÊÇ"));
		list.add(new Mapper("0","·ñ"));
		return list;
	}

}

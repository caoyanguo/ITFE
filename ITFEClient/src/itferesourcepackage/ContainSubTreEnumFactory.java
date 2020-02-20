package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author hua
 * @time   12-05-16 17:39:26
 */
public class ContainSubTreEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> list = new ArrayList<Mapper>();
		list.add(new Mapper("0","不包含下级国库"));
		list.add(new Mapper("1","包含下级国库"));
		return list;
	}

}

package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author db2admin
 * @time   14-06-05 16:56:43
 */
public class QueryStypeEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> ms = new ArrayList<Mapper>();
		Mapper m0 = new Mapper("0","大额税源");
		Mapper m1 = new Mapper("1","大额支出");
		ms.add(m0);
		ms.add(m1);
		return ms;
	}

}

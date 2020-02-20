package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author renqingbin
 * @time   14-03-27 13:54:48
 */
public class ReportTypeEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> ms = new ArrayList<Mapper>();
		Mapper m0 = new Mapper("1","日报");
		Mapper m1 = new Mapper("3","月报");
		Mapper m2 = new Mapper("6","年报");
		ms.add(m0);
		ms.add(m1);
		ms.add(m2);
		return ms;
	}
}

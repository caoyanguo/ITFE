package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author db2admin
 * @time   13-01-10 16:18:24
 */
public class ReportAreaEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> ms = new ArrayList<Mapper>();
		Mapper m0 = new Mapper("0","����");
		Mapper m1 = new Mapper("1","ȫϽ");
		ms.add(m0);
		ms.add(m1);
		return ms;
	}

}

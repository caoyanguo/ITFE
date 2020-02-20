package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;

import com.cfcc.jaf.ui.factory.IEnumFactory;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * @author db2admin
 * @time   13-08-27 16:01:34
 */
public class GoflagEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> ms = new ArrayList<Mapper>();
		Mapper m0 = new Mapper("0","非调拨");
		Mapper m1 = new Mapper("1","调拨");
		Mapper m2 = new Mapper("2","不区分");
		ms.add(m0);
		ms.add(m1);
		ms.add(m2);
		return ms;
	}

}

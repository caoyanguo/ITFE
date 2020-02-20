package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author db2admin
 * @time   14-05-14 10:26:37
 */
public class NoPaperFlagEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> ms = new ArrayList<Mapper>();
		Mapper m0 = new Mapper("0","未使用");
		Mapper m1 = new Mapper("1","使用");
		ms.add(m0);
		ms.add(m1);
		return ms;
	}

}

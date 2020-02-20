package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author t60
 * @time   12-02-28 14:22:18
 */
public class StaxpropEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List list = new ArrayList();
		list.add(new Mapper("0","不分征收机关"));
		list.add(new Mapper("1","国税"));
		list.add(new Mapper("2","地税"));
		list.add(new Mapper("3","海关"));
		list.add(new Mapper("4","财政"));
		list.add(new Mapper("5","其它"));
		//list.add(new Mapper(null,""));
		return list;
	}

}

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
		list.add(new Mapper("0","�������ջ���"));
		list.add(new Mapper("1","��˰"));
		list.add(new Mapper("2","��˰"));
		list.add(new Mapper("3","����"));
		list.add(new Mapper("4","����"));
		list.add(new Mapper("5","����"));
		//list.add(new Mapper(null,""));
		return list;
	}

}

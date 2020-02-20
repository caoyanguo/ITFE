package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author caoyg
 * @time   09-11-12 20:07:55
 */
public class ProcFlagEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List list = new ArrayList();
		list.add(new Mapper("1","预接收"));
		list.add(new Mapper("2","正式接收"));
		list.add(new Mapper("3","已复核"));
		list.add(new Mapper("4","校验失败"));
		list.add(new Mapper("5","退库退回"));
		list.add(new Mapper("6","清算失败"));
		return list;
	}

}

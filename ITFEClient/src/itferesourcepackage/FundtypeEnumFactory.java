package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author renqingbin
 * @time   14-06-10 08:52:23
 */
public class FundtypeEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List list = new ArrayList();
		list.add(new Mapper("111","�������111"));
		list.add(new Mapper("112","�������112"));
		list.add(new Mapper("121","����С��121"));
		list.add(new Mapper("122","����С��122"));
		list.add(new Mapper("999","����������������"));
		list.add(new Mapper("100","CMT100"));
		list.add(new Mapper("103","CMT103"));
		list.add(new Mapper("108","CMT108"));
		list.add(new Mapper("001","PKG001"));
		list.add(new Mapper("007","PKG007"));
		return list;
	}

}

package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author t60
 * @time   12-03-14 10:06:04
 */
public class SearchTypeEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> list=new ArrayList<Mapper>();
		list.add(new Mapper("0","查询单笔"));
		list.add(new Mapper("1","查询批量包"));
		return list;
	}

}

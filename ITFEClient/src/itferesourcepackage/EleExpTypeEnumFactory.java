package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author hua
 * @time   12-05-24 12:34:45
 */
public class EleExpTypeEnumFactory implements IEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List<Mapper> list = new ArrayList<Mapper>();
		list.add(new Mapper("0","库表格式"));
		list.add(new Mapper("1","重点税源接口格式"));
		return list;
	}

}

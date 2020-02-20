package itferesourcepackage;

import java.util.ArrayList;
import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author zgz
 * @time   12-03-14 16:30:00
 */
public class OperTypeEnumFactory extends ITFEEnumFactory {

	public List<Mapper> getEnums(Object o) {
		List <Mapper> listmap = new ArrayList<Mapper>();
		listmap.add(new Mapper("0000","全部业务类型"));
		listmap.addAll(super.getEnums("0407"));
		return listmap;
	}

}

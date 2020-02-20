package itferesourcepackage;

import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author db2admin
 * @time   09-11-16 13:43:03
 */
public class SisvalidEnumFactory extends ITFEEnumFactory{

	public List<Mapper> getEnums(Object o) {
		return super.getEnums("0114");
	}

}

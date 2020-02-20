package itferesourcepackage;

import java.util.List;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.ui.factory.IEnumFactory;

/**
 * @author db2admin
 * @time   13-03-01 17:01:18
 */
public class KeyOrgStatusEnumFactory extends ITFEEnumFactory  {

	public List<Mapper> getEnums(Object o) {
		return super.getEnums("0416");
	}

}
